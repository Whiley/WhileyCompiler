// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wyil.check;

import wybs.lang.*;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Name;
import wybs.util.AbstractCompilationUnit.Pair;

import static wybs.util.AbstractCompilationUnit.ITEM_bool;
import static wybs.util.AbstractCompilationUnit.ITEM_byte;
import static wybs.util.AbstractCompilationUnit.ITEM_int;
import static wybs.util.AbstractCompilationUnit.ITEM_null;
import static wybs.util.AbstractCompilationUnit.ITEM_utf8;
import static wyc.util.ErrorMessages.syntaxError;
import static wyil.lang.WyilFile.*;

import java.sql.Types;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.tools.ant.types.Environment;

import wybs.util.AbstractCompilationUnit.Tuple;
import wybs.util.AbstractCompilationUnit.Value;
import wycc.util.ArrayUtils;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Expr;
import wyil.lang.WyilFile.LVal;
import wyil.lang.WyilFile.Stmt;
import wyil.lang.WyilFile.Template;
import wyil.lang.WyilFile.Type;
import wyil.util.*;

/**
 * This is an overflow class for <code>FlowTypeCheck</code>. It provides various
 * clearly defined helper functions which can be off-loaded to make
 * <code>FlowTypeCheck</code> more focused.
 *
 * @author David J. Pearce
 *
 */
public class FlowTypeUtils {

	public static Environment union(Environment... environments) {
		Environment result = environments[0];
		for (int i = 1; i != environments.length; ++i) {
			result = union(result, environments[i]);
		}
		//
		return result;
	}

	public static Environment union(Environment left, Environment right) {
		if (left == right || right == BOTTOM) {
			return left;
		} else if (left == BOTTOM) {
			return right;
		} else {
			Environment result = new Environment();
			Set<Decl.Variable> leftRefinements = left.getRefinedVariables();
			Set<Decl.Variable> rightRefinements = right.getRefinedVariables();
			for (Decl.Variable var : leftRefinements) {
				if (rightRefinements.contains(var)) {
					// We have a refinement on both branches
					Type leftT = left.getType(var);
					Type rightT = right.getType(var);
					Type mergeT = new Type.Union(leftT, rightT);
					result = result.refineType(var, mergeT);
				}
			}
			return result;
		}
	}

	/**
	 * <p>
	 * Extract the "true" test from a given type test in order that we might try to
	 * retype it. This does not always succeed if, for example, the expression being
	 * tested cannot be retyped. An example would be a test like
	 * <code>arr[i] is int</code> as, in this case, we cannot retype
	 * <code>arr[i]</code>.
	 * </p>
	 *
	 * <p>
	 * In the simple case of e.g. <code>x is int</code> we just extract
	 * <code>x</code> and type <code>int</code>. The more interesting case arises
	 * when there is at least one field access involved. For example,
	 * <code>x.f is int</code> extracts variable <code>x</code> with type
	 * <code>{int f, ...}</code> (which is a safe approximation).
	 * </p>
	 *
	 * @param expr
	 * @param type
	 * @return A pair on successful extraction, or null if possible extraction.
	 */
	public static Pair<Decl.Variable, Type> extractTypeTest(Expr expr, Type type) {
		if (expr instanceof Expr.VariableAccess) {
			Expr.VariableAccess var = (Expr.VariableAccess) expr;
			return new Pair<>(var.getVariableDeclaration(), type);
		} else if (expr instanceof Expr.RecordAccess) {
			Expr.RecordAccess ra = (Expr.RecordAccess) expr;
			Expr ra_operand = ra.getOperand();
			Type.Record ra_operandT = ra_operand.getType().as(Type.Record.class);
			if (ra_operandT != null) {
				Tuple<Type.Field> ra_fields = ra_operandT.getFields();
				Type.Field[] fields = new Type.Field[ra_fields.size()];
				//
				for (int i = 0; i != ra_fields.size(); ++i) {
					Type.Field f = ra_fields.get(i);
					if (f.getName().equals(ra.getField())) {
						fields[i] = new Type.Field(f.getName(), type);
					} else {
						fields[i] = f;
					}
				}
				Type.Record recT = new Type.Record(ra_operandT.isOpen(), new Tuple<>(fields));
				return extractTypeTest(ra.getOperand(), recT);
			}
		}
		// no extraction is possible
		return null;
	}

	/**
	 * Determine the set of modifier variables for a given statement block. A
	 * modified variable is one which is assigned.
	 *
	 * @param block
	 */
	public static Tuple<Decl.Variable> determineModifiedVariables(Stmt.Block block) {
		HashSet<Decl.Variable> modified = new HashSet<>();
		determineModifiedVariables(block, modified);
		return new Tuple<>(modified);
	}

	public static void determineModifiedVariables(Stmt.Block block, Set<Decl.Variable> modified) {
		for (int i = 0; i != block.size(); ++i) {
			Stmt stmt = block.get(i);
			switch (stmt.getOpcode()) {
			case STMT_assign: {
				Stmt.Assign s = (Stmt.Assign) stmt;
				for (LVal lval : s.getLeftHandSide()) {
					addAssignedVariables(lval, modified);
					// FIXME: this is not an ideal solution long term. In
					// particular, we really need this method to detect not
					// just modified variables, but also modified locations
					// in general (e.g. assignments through references, etc)
					continue;
				}
				break;
			}
			case STMT_dowhile: {
				Stmt.DoWhile s = (Stmt.DoWhile) stmt;
				determineModifiedVariables(s.getBody(), modified);
				break;
			}
			case STMT_if:
			case STMT_ifelse: {
				Stmt.IfElse s = (Stmt.IfElse) stmt;
				determineModifiedVariables(s.getTrueBranch(), modified);
				if (s.hasFalseBranch()) {
					determineModifiedVariables(s.getFalseBranch(), modified);
				}
				break;
			}
			case STMT_namedblock: {
				Stmt.NamedBlock s = (Stmt.NamedBlock) stmt;
				determineModifiedVariables(s.getBlock(), modified);
				break;
			}
			case STMT_switch: {
				Stmt.Switch s = (Stmt.Switch) stmt;
				for (Stmt.Case c : s.getCases()) {
					determineModifiedVariables(c.getBlock(), modified);
				}
				break;
			}
			case STMT_while: {
				Stmt.While s = (Stmt.While) stmt;
				determineModifiedVariables(s.getBody(), modified);
				break;
			}
			}
		}
	}

	/**
	 * Determine the modified variable for a given LVal. Almost all lvals modify
	 * exactly one variable, though dereferences don't.
	 *
	 * @param lval
	 * @param modified
	 * @return
	 */
	public static void addAssignedVariables(LVal lval, Set<Decl.Variable> modified) {
		if (lval instanceof Expr.VariableAccess) {
			Expr.VariableAccess lv = (Expr.VariableAccess) lval;
			modified.add(lv.getVariableDeclaration());
		} else if (lval instanceof Expr.RecordAccess) {
			Expr.RecordAccess e = (Expr.RecordAccess) lval;
			addAssignedVariables((LVal) e.getOperand(), modified);
		} else if (lval instanceof Expr.ArrayAccess) {
			Expr.ArrayAccess e = (Expr.ArrayAccess) lval;
			addAssignedVariables((LVal) e.getFirstOperand(), modified);
		} else if (lval instanceof Expr.TupleInitialiser) {
			Expr.TupleInitialiser e = (Expr.TupleInitialiser) lval;
			Tuple<Expr> operands = e.getOperands();
			for (int i = 0; i != operands.size(); ++i) {
				addAssignedVariables((LVal) operands.get(i), modified);
			}
		} else if (lval instanceof Expr.Dereference || lval instanceof Expr.FieldDereference) {

		} else {
			syntaxError(lval, WyilFile.INVALID_LVAL_EXPRESSION);
		}
	}

	// ===============================================================================================================
	// isPure
	// ===============================================================================================================

	public static class PurityVisitor extends AbstractVisitor {
		public boolean pure = true;

		public PurityVisitor(Build.Meter meter) {
			super(meter);
		}

		@Override
		public void visitExternalUnit(Decl.Unit unit) {
			// Terminate
		}

		@Override
		public void visitDeclaration(Decl type) {
			// Terminate
		}

		@Override
		public void visitStatement(Stmt stmt) {
			// Terminate
		}

		@Override
		public void visitStaticVariableAccess(Expr.StaticVariableAccess expr) {
			pure = false;
		}

		@Override
		public void visitNew(Expr.New expr) {
			pure = false;
		}

		@Override
		public void visitDereference(Expr.Dereference expr) {
			pure = false;
		}

		@Override
		public void visitFieldDereference(Expr.FieldDereference expr) {
			pure = false;
		}

		@Override
		public void visitInvoke(Expr.Invoke expr) {
			Decl.Link<Decl.Callable> l = expr.getLink();
			if (l.getTarget() instanceof Decl.Method) {
				// This expression is definitely not pure
				pure = false;
			}
		}

		@Override
		public void visitIndirectInvoke(Expr.IndirectInvoke expr) {
			Type.Callable sourceType = expr.getSource().getType().as(Type.Callable.class);
			if (sourceType instanceof Type.Method) {
				pure = false;
			}
		}

		@Override
		public void visitType(Type type) {
			// Terminate
		}
	};

	// ===============================================================================================================
	// getNaturalType
	// ===============================================================================================================

	/**
	 * A helper method which identifies the "natural" type of an expression.
	 *
	 * @param expression
	 * @param environment
	 * @return
	 */
	public static Type getNaturalType(Expr expression, Environment environment) {
		switch (expression.getOpcode()) {
		case EXPR_constant:
			return typeOf(((Expr.Constant) expression).getValue());
		case EXPR_variablecopy:
			return environment.getType(((Expr.VariableAccess) expression).getVariableDeclaration());
		case EXPR_staticvariable: {
			Decl.Link<Decl.StaticVariable> l = ((Expr.StaticVariableAccess) expression).getLink();
			// Extract type if applicable
			return l.isResolved() ? l.getTarget().getType() : null;
		}
		case EXPR_cast: {
			Expr.Cast c = (Expr.Cast) expression;
			return c.getType();
		}
		case EXPR_invoke: {
			Expr.Invoke l = (Expr.Invoke) expression;
			List<Decl.Callable> types = l.getLink().getCandidates();
			Type[] ts = new Type[types.size()];
			for (int i = 0; i != ts.length; ++i) {
				ts[i] = types.get(i).getType().getReturn();
			}
			return Type.Union.create(ts);
		}
		case EXPR_indirectinvoke: {
			Expr.IndirectInvoke r = (Expr.IndirectInvoke) expression;
			Type.Callable src = getNaturalType(r.getSource(), environment).as(Type.Callable.class);
			return (src == null) ? Type.Any : src.getReturn();
		}
		case EXPR_logicalnot:
		case EXPR_logicalor:
		case EXPR_logicaland:
		case EXPR_logicaliff:
		case EXPR_logicalimplication:
		case EXPR_is:
		case EXPR_logicaluniversal:
		case EXPR_logicalexistential:
		case EXPR_equal:
		case EXPR_notequal:
		case EXPR_integerlessthan:
		case EXPR_integerlessequal:
		case EXPR_integergreaterthan:
		case EXPR_integergreaterequal:
			return Type.Bool;
		case EXPR_integernegation:
		case EXPR_integeraddition:
		case EXPR_integersubtraction:
		case EXPR_integermultiplication:
		case EXPR_integerdivision:
		case EXPR_integerremainder:
			return Type.Int;
		case EXPR_bitwisenot:
		case EXPR_bitwiseand:
		case EXPR_bitwiseor:
		case EXPR_bitwisexor:
		case EXPR_bitwiseshl:
		case EXPR_bitwiseshr:
			return Type.Byte;
		case EXPR_tupleinitialiser: {
			Type[] types = getNaturalTypes(((Expr.TupleInitialiser) expression).getOperands(), environment);
			return Type.Tuple.create(types);
		}
		case EXPR_recordinitialiser: {
			Expr.RecordInitialiser r = (Expr.RecordInitialiser) expression;
			Tuple<Identifier> fields = r.getFields();
			Type[] types = getNaturalTypes(r.getOperands(), environment);
			Type.Field[] fs = new Type.Field[types.length];
			for (int i = 0; i != fields.size(); ++i) {
				fs[i] = new Type.Field(fields.get(i), types[i]);
			}
			return new Type.Record(false, new Tuple<>(fs));
		}
		case EXPR_recordaccess:
		case EXPR_recordborrow: {
			Expr.RecordAccess r = (Expr.RecordAccess) expression;
			Type.Record src = getNaturalType(r.getOperand(), environment).as(Type.Record.class);
			if (src != null && src.getField(r.getField()) != null) {
				return src.getField(r.getField());
			} else {
				return Type.Any;
			}
		}
		case EXPR_arraylength:
			return Type.Int;
		case EXPR_arrayinitialiser: {
			Expr.ArrayInitialiser r = (Expr.ArrayInitialiser) expression;
			Type[] types = getNaturalTypes(r.getOperands(), environment);
			return new Type.Array(Type.Union.create(types));
		}
		case EXPR_arraygenerator: {
			Expr.ArrayGenerator r = (Expr.ArrayGenerator) expression;
			return new Type.Array(getNaturalType(r.getFirstOperand(), environment));
		}
		case EXPR_arrayaccess:
		case EXPR_arrayborrow: {
			Expr.ArrayAccess r = (Expr.ArrayAccess) expression;
			Type.Array src = getNaturalType(r.getFirstOperand(), environment).as(Type.Array.class);
			return (src == null) ? Type.Any : src.getElement();
		}
		case EXPR_arrayrange:
			return Type.IntArray;
		case EXPR_dereference: {
			Expr.Dereference r = (Expr.Dereference) expression;
			Type.Reference src = getNaturalType(r.getOperand(), environment).as(Type.Reference.class);
			return (src == null) ? Type.Any : src.getElement();
		}
		case EXPR_fielddereference: {
			Expr.FieldDereference r = (Expr.FieldDereference) expression;
			Type.Reference src = getNaturalType(r.getOperand(), environment).as(Type.Reference.class);
			if (src != null) {
				Type.Record rec = src.getElement().as(Type.Record.class);
				if (rec != null && rec.getField(r.getField()) != null) {
					return rec.getField(r.getField());
				}
			}
			return Type.Any;
		}
		case EXPR_new: {
			Expr.New r = (Expr.New) expression;
			return new Type.Reference(getNaturalType(r.getOperand(), environment));
		}
		case EXPR_lambdaaccess: {
			Expr.LambdaAccess l = (Expr.LambdaAccess) expression;
			List<Decl.Callable> types = l.getLink().getCandidates();
			Type[] ts = new Type[types.size()];
			for (int i = 0; i != ts.length; ++i) {
				ts[i] = types.get(i).getType();
			}
			return Type.Union.create(ts);
		}
		case DECL_lambda: {
			Decl.Lambda l = (Decl.Lambda) expression;
			Type ret = getNaturalType(l.getBody(), environment);
			Tuple<Type> params = l.getParameters().map(v -> v.getType());
			// Not much more we can do here
			return new Type.Function(Type.Tuple.create(params), ret);
		}
		default:
			return internalFailure("unknown expression encountered (" + expression.getClass().getSimpleName() + ")",
					expression);
		}
	}

	private static Type[] getNaturalTypes(Tuple<Expr> expressions, Environment environment) {
		Type[] types = new Type[expressions.size()];
		for (int i = 0; i != types.length; ++i) {
			types[i] = getNaturalType(expressions.get(i), environment);
		}
		return types;
	}


	/**
	 * Determine the underlying type of a given constant value. For example,
	 * <code>1</code> has type <code>int</code>, etc.
	 *
	 * @param v The value to type
	 * @return
	 */
	public static Type typeOf(Value v) {
		switch (v.getOpcode()) {
		case ITEM_null:
			return Type.Null;
		case ITEM_bool:
			return Type.Bool;
		case ITEM_byte:
			return Type.Byte;
		case ITEM_int:
			return Type.Int;
		case ITEM_utf8:
			return Type.IntArray;
		// break;
		default:
			return internalFailure("unknown constant encountered: " + v, v);
		}
	}

	// ===============================================================================================================
	// disjoint
	// ===============================================================================================================

	/**
	 * Check whether two types are completely disjoint. For example,
	 * <code>bool</code> and <code>int</code> are disjoint, whilst
	 * <code>int|null</code> and <code>int</code> are not. This is used to determine
	 * whether an equality comparison makes any possible sense. More specifically,
	 * whether there is any interpretation under which these two types could be
	 * equal or not.
	 *
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static boolean disjoint(Type t1, Type t2, Set<Name> visited) {
		int t1_opcode = IncrementalSubtypingEnvironment.normalise(t1.getOpcode());
		int t2_opcode = IncrementalSubtypingEnvironment.normalise(t2.getOpcode());
		//
		if (t1_opcode == t2_opcode) {
			switch (t1_opcode) {
			case TYPE_any:
			case TYPE_bool:
			case TYPE_byte:
			case TYPE_int:
			case TYPE_null:
			case TYPE_void:
			case TYPE_existential:
				return false;
			case TYPE_universal: {
				Type.Universal v1 = (Type.Universal) t1;
				Type.Universal v2 = (Type.Universal) t2;
				return !v1.getOperand().toString().equals(v2.getOperand().toString());
			}
			case TYPE_array: {
				Type.Array a1 = (Type.Array) t1;
				Type.Array a2 = (Type.Array) t2;
				return disjoint(a1.getElement(), a2.getElement(), visited);
			}
			case TYPE_reference: {
				Type.Reference a1 = (Type.Reference) t1;
				Type.Reference a2 = (Type.Reference) t2;
				// NOTE: could potentially do better here by examining lifetimes.
				return disjoint(a1.getElement(), a2.getElement(), visited);
			}
			case TYPE_function:
			case TYPE_method:
			case TYPE_property: {
				Type.Callable c1 = (Type.Callable) t1;
				Type.Callable c2 = (Type.Callable) t2;
				return disjoint(c1.getParameter(), c2.getParameter(), visited)
						|| disjoint(c1.getReturn(), c2.getReturn(), visited);
			}
			case TYPE_nominal: {
				Type.Nominal n1 = (Type.Nominal) t1;
				Type.Nominal n2 = (Type.Nominal) t2;
				Name n = n1.getLink().getName();
				if (visited != null && visited.contains(n)) {
					return false;
				} else {
					visited = (visited == null) ? new HashSet<>() : new HashSet<>(visited);
					visited.add(n);
					return disjoint(n1.getConcreteType(), n2.getConcreteType(), visited);
				}
			}
			case TYPE_tuple: {
				Type.Tuple u1 = (Type.Tuple) t1;
				Type.Tuple u2 = (Type.Tuple) t2;
				if (u1.size() != u2.size()) {
					return true;
				} else {
					for (int i = 0; i != u1.size(); ++i) {
						if (disjoint(u1.get(i), u2.get(i), visited)) {
							return true;
						}
					}
					return false;
				}
			}
			case TYPE_union: {
				Type.Union u1 = (Type.Union) t1;
				Type.Union u2 = (Type.Union) t2;
				for (int i = 0; i != u1.size(); ++i) {
					for (int j = 0; j != u2.size(); ++j) {
						if (!disjoint(u1.get(i), u2.get(i), visited)) {
							return false;
						}
					}
				}
				return true;
			}
			case TYPE_record: {
				Type.Record r1 = (Type.Record) t1;
				Type.Record r2 = (Type.Record) t2;
				Tuple<Type.Field> r1fs = r1.getFields();
				Tuple<Type.Field> r2fs = r2.getFields();
				//
				if (r1fs.size() < r2fs.size() && !r1.isOpen()) {
					return true;
				} else if (r1fs.size() > r2fs.size() && !r2.isOpen()) {
					return true;
				}
				for (int i = 0; i != r1fs.size(); ++i) {
					Type.Field f1 = r1fs.get(i);
					Type ft2 = r2.getField(f1.getName());
					if (ft2 != null && disjoint(f1.getType(), ft2, visited)) {
						return true;
					}
				}
				return false;
			}
			}
		} else if (t1 instanceof Type.Any || t2 instanceof Type.Any) {
			return false;
		} else if (t1 instanceof Type.Void || t2 instanceof Type.Void) {
			// NOTE: should only be possible for empty arrays
			return false;
		} else if (t1 instanceof Type.Existential || t2 instanceof Type.Existential) {
			return false;
		} else if (t1 instanceof Type.Union) {
			Type.Union u1 = (Type.Union) t1;
			for (int i = 0; i != u1.size(); ++i) {
				if (!disjoint(u1.get(i), t2, visited)) {
					return false;
				}
			}
			return true;
		} else if (t2 instanceof Type.Union) {
			Type.Union u2 = (Type.Union) t2;
			for (int i = 0; i != u2.size(); ++i) {
				if (!disjoint(t1, u2.get(i), visited)) {
					return false;
				}
			}
			return true;
		} else if (t1 instanceof Type.Nominal) {
			Type.Nominal n1 = (Type.Nominal) t1;
			Name n = n1.getLink().getName();
			if (visited != null && visited.contains(n)) {
				return false;
			} else {
				visited = (visited == null) ? new HashSet<>() : new HashSet<>(visited);
				visited.add(n);
				return disjoint(n1.getConcreteType(), t2, visited);
			}
		} else if (t2 instanceof Type.Nominal) {
			Type.Nominal n2 = (Type.Nominal) t2;
			return disjoint(t1, n2.getConcreteType(), visited);
		}

		return true;
	}

	// ===============================================================================================================
	// getUnderylingType()
	// ===============================================================================================================

	/**
	 * Get the underlying type for a given type. For example, consider the following
	 * declarations:
	 *
	 * <pre>
	 * type nat is (int x) where x >= 0
	 * type rec_t is {nat f, int g}
	 * </pre>
	 *
	 * Here, the underlying type of <code>nat</code> is <code>int</code>, whilst the
	 * underlying type of <code>rec_t</code> is <code>{int f, int g}</code>.
	 *
	 * @param type
	 * @param visited
	 * @return
	 */
	public static Type getUnderlyingType(Type type, Set<Name> visited) {
		switch (type.getOpcode()) {
		case TYPE_null:
		case TYPE_bool:
		case TYPE_byte:
		case TYPE_int:
		case TYPE_void:
		case TYPE_universal:
		case TYPE_existential:
			return type;
		case TYPE_nominal: {
			Type.Nominal t = (Type.Nominal) type;
			Name n = t.getLink().getName();
			if (visited != null && visited.contains(n)) {
				return type;
			} else {
				visited = (visited == null) ? new HashSet<>() : new HashSet<>(visited);
				visited.add(n);
				return getUnderlyingType(t.getConcreteType(), visited);
			}
		}
		case TYPE_array: {
			Type.Array t = (Type.Array) type;
			Type element = t.getElement();
			Type nElement = getUnderlyingType(element, visited);
			if (element == nElement) {
				return type;
			} else {
				return new Type.Array(nElement);
			}
		}
		case TYPE_reference: {
			Type.Reference t = (Type.Reference) type;
			Type element = t.getElement();
			Type nElement = getUnderlyingType(element, visited);
			if (element == nElement) {
				return type;
			} else {
				return new Type.Reference(nElement);
			}
		}
		case TYPE_record: {
			Type.Record t = (Type.Record) type;
			Tuple<Type.Field> fields = t.getFields();
			Type.Field[] nFields = new Type.Field[fields.size()];
			boolean changed = false;
			for (int i = 0; i != nFields.length; ++i) {
				Type.Field field = fields.get(i);
				Type element = field.getType();
				Type nElement = getUnderlyingType(element, visited);
				if (element == nElement) {
					nFields[i] = field;
				} else {
					nFields[i] = new Type.Field(field.getName(), nElement);
					changed = true;
				}
			}
			if (changed) {
				return new Type.Record(t.isOpen(), new Tuple<>(nFields));
			} else {
				return type;
			}
		}
		case TYPE_property:
		case TYPE_function:
		case TYPE_method: {
			Type.Callable t = (Type.Callable) type;
			Type tParam = t.getParameter();
			Type tReturn = t.getReturn();
			Type nParam = getUnderlyingType(tParam, visited);
			Type nReturn = getUnderlyingType(tReturn, visited);
			if (tParam == nParam && tReturn == nReturn) {
				return type;
			} else if (t instanceof Type.Property) {
				return new Type.Property(nParam, nReturn);
			} else if (t instanceof Type.Function) {
				return new Type.Function(nParam, nReturn);
			} else {
				return new Type.Method(nParam, nReturn);
			}
		}
		case TYPE_tuple: {
			Type.Tuple t = (Type.Tuple) type;
			Type[] types = getUnderlyingTypes(t.getAll(), visited);
			return Type.Tuple.create(types);
		}
		case TYPE_union: {
			Type.Union t = (Type.Union) type;
			Type[] types = getUnderlyingTypes(t.getAll(), visited);
			return new Type.Union(types);
		}
		}
		throw new IllegalArgumentException("invalid type (" + type + "," + type.getClass().getName() + ")");
	}

	private static Type[] getUnderlyingTypes(Type[] types, Set<Name> visited) {
		Type[] uTypes = new Type[types.length];
		for (int i = 0; i != types.length; ++i) {
			uTypes[i] = getUnderlyingType(types[i], visited);
		}
		return uTypes;
	}

	// ===============================================================================================================
	// Environment
	// ===============================================================================================================

	/**
	 * The bottom environment represents the root environment (in some sense) which
	 * maps no variables.
	 */
	public static final Environment BOTTOM = new Environment();

	/**
	 * Provides a very simple typing environment which defaults to using the
	 * declared type for a variable (this is the "null" case). However, the
	 * environment can also be updated to override the declared type with a new type
	 * as appropriate.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Environment extends IncrementalSubtypingEnvironment {
		private final Map<Decl.Variable, Type> refinements;

		public Environment() {
			this.refinements = new HashMap<>();
		}

		public Environment(Map<Decl.Variable, Type> refinements, Map<String, String[]> withins) {
			super(withins);
			this.refinements = new HashMap<>(refinements);
		}

		public Type getType(Decl.Variable var) {
			Type refined = refinements.get(var);
			if (refined == null) {
				return var.getType();
			} else {
				return refined;
			}
		}

		public Environment refineType(Decl.Variable var, Type refinement) {
			if (getType(var).equals(refinement)) {
				// No refinement necessary
				return this;
			} else {
				Environment r = new Environment(this.refinements, this.withins);
				r.refinements.put(var, refinement);
				return r;
			}
		}

		public Set<Decl.Variable> getRefinedVariables() {
			return refinements.keySet();
		}

		@Override
		public Environment declareWithin(String inner, String... outers) {
			Environment nenv = new Environment(this.refinements, this.withins);
			nenv.withins.put(inner, outers);
			return nenv;
		}

		@Override
		public String toString() {
			String r = "{";
			boolean firstTime = true;
			for (Decl.Variable var : refinements.keySet()) {
				if (!firstTime) {
					r += ", ";
				}
				firstTime = false;
				r += var.getName() + "->" + getType(var);
			}
			return r + "}" + super.toString();
		}
	}

	// ===============================================================================================================
	// Typing
	// ===============================================================================================================

	/**
	 * Represents the high-level data structure used in typing expressions.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Typing {
		private final IncrementalSubtypingEnvironment subtyping;
		/**
		 * The sequence of frames identifies (sub)expressions being typed and the type
		 * variable(s) allocated for them. First such type variable in each frame always
		 * represents the return type of the expression.
		 */
		private final ArrayList<Finaliser> frames;
		/**
		 * Tracks the number of allocated typing variables so we can allocate fresh
		 * variables as necessary.
		 */
		private int nVariables;

		private Row[] matrix;

		public Typing(IncrementalSubtypingEnvironment subtyping) {
			this.subtyping = subtyping;
			this.frames = new ArrayList<>();
			this.nVariables = 0;
			this.matrix = new Row[] { new Row(subtyping.TOP) };
		}

		private Typing(IncrementalSubtypingEnvironment subtyping, List<Finaliser> frames, int nVariables, Row[] matrix) {
			this.subtyping = subtyping;
			this.frames = new ArrayList<>(frames);
			this.nVariables = nVariables;
			this.matrix = matrix;
		}

		public boolean isEmpty() {
			return matrix.length == 0;
		}

		public int height() {
			return matrix.length;
		}

		public Tuple<Type> types(int index) {
			Type[] types = new Type[matrix.length];
			for(int i=0;i!=matrix.length;++i) {
				types[i] = matrix[i].get(index);
			}
			return new Tuple<>(types);
		}

		public Tuple<Tuple<Type>> types(int[] indices) {
			Tuple<Type>[] types = new Tuple[matrix.length];
			for(int i=0;i!=matrix.length;++i) {
				Type[] ith = new Type[indices.length];
				for(int j=0;j!=ith.length;++j) {
					ith[j] = matrix[i].get(indices[j]);
				}
				types[i] = new Tuple<>(ith);
			}
			return new Tuple<>(types);
		}

		public int top() {
			return nVariables - 1;
		}

		public int[] top(int n) {
			return ArrayUtils.range(nVariables - n, nVariables);
		}

		public Typing invalidate() {
			return new Typing(subtyping, frames, nVariables, new Row[0]);
		}

		public void register(Predicate<Row[]> finaliser) {
			frames.add(new Finaliser(finaliser));
		}

		public Typing push(Type type) {
			return map(row -> row.add(type));
		}

		public Typing pushAll(int n, BiFunction<Row,Integer,Type[]> projection) {
			return project(row -> row.addAll(n,projection));
		}

		public Typing project(Function<Row,Row[]> projection) {
			ArrayList<Row> nRows = new ArrayList<>();
			// Sanity check existing rows
			for (int i = 0; i != matrix.length; ++i) {
				Row ith = matrix[i];
				Row[] nith = projection.apply(ith);
				//
				for(int j=0;j!=nith.length;++j) {
					Row jth = nith[j];
					if(jth != null) {
						nRows.add(jth);
					}
				}
			}
			// Remove any invalid rows
			Row[] arr = nRows.toArray(new Row[nRows.size()]);
			// Recalculate number of variables
			int nVariables = arr.length > 0 ? arr[0].size() : 0;
			// Create new typing
			return new Typing(subtyping, frames, nVariables, arr);
		}

		/**
		 * Filter rows based on some particular predicate.
		 *
		 * @param fn
		 * @return
		 */
		public Typing filter(Predicate<Row> fn) {
			for (int i = 0; i != matrix.length; ++i) {
				Row ith = matrix[i];
				if (!fn.test(ith)) {
					// Something is being filtered. This is the point of no return.
					Row[] nRows = new Row[matrix.length];
					System.arraycopy(matrix, 0, nRows, 0, i);
					for (int j = i + 1; j < nRows.length; ++j) {
						Row jth = matrix[j];
						if (fn.test(jth)) {
							nRows[j] = jth;
						}
					}
					// Remove any invalid rows
					nRows = ArrayUtils.removeAll(nRows, null);
					// Create new typing
					return new Typing(subtyping, frames, nVariables, nRows);
				}
			}
			return this;
		}

		public Typing map(Function<Row,Row> fn) {
			Row[] nRows = matrix;
			// Sanity check existing rows
			for (int i = 0; i != nRows.length; ++i) {
				Row ith = nRows[i];
				Row nith = fn.apply(ith);
				// Sanity check current type
				if(ith != nith && matrix == nRows) {
					nRows = Arrays.copyOf(nRows,nRows.length);
				}
				nRows[i] = nith;
			}
			if(matrix == nRows) {
				return Typing.this;
			} else {
				// Remove any invalid rows
				nRows = ArrayUtils.removeAll(nRows, null);
				// Recalculate number of variables
				int nVariables = nRows.length > 0 ? nRows[0].size() : 0;
				// Create new typing
				return new Typing(subtyping, frames, nVariables, nRows);
			}
		}

		public Typing fold(Comparator<Row> comparator)  {
			if (matrix.length <= 1) {
				return this;
			} else {
				Row[] nrows = Arrays.copyOf(matrix, matrix.length);
				for (int i = 0; i != nrows.length; ++i) {
					Row ith = nrows[i];
					if (ith == null) {
						continue;
					}
					for (int j = i + 1; j < nrows.length; ++j) {
						Row jth = nrows[j];
						if (jth == null) {
							continue;
						}
						int c = comparator.compare(ith, jth);
						if (c < 0) {
							nrows[j] = null;
						} else if (c > 0) {
							nrows[i] = null;
						}
					}
				}
				nrows = ArrayUtils.removeAll(nrows, null);
				return new Typing(subtyping, frames, nVariables, nrows);
			}
		}

		/**
		 * Concretise every type within this typing by substitution each type variable
		 * for its current best solution.
		 *
		 * @return
		 */
		public Typing concretise() {
			return map(r -> r.concretise(subtyping));
		}

		/**
		 * Attempt to finalise the typing by giving a type to all expressions. This
		 * requires that we can boil all valid typings down to a single winning typing.
		 *
		 * @param typing
		 * @param environment
		 * @return
		 */
		public boolean finalise() {
			// Finalising an empty typing should be considered a success.
			boolean r = !isEmpty();
			// Finally, run every registered finaliser.
			for (Finaliser f : frames) {
				r &= f.finalise(matrix);
			}
			return r;
		}

		@Override
		public String toString() {
			return nVariables + ":" + Arrays.toString(matrix);
		}

		public final static class Row {
			/**
			 * Default comparator for typing rows.
			 */
			public static Comparator<Row> COMPARATOR(Subtyping.Environment env) {
				return new Comparator<Row>() {

					@Override
					public int compare(Row o1, Row o2) {
						Type[] o1_types = o1.types;
						Type[] o2_types = o2.types;
						if(o1_types.length < o2_types.length) {
							return -1;
						} else if(o1_types.length > o2_types.length) {
							return 1;
						}
						//
						for(int i=0;i!=o1_types.length;++i) {
							Type t1 = o1_types[i];
							Type t2 = o2_types[i];
							boolean left = env.isSatisfiableSubtype(t1, t2);
							boolean right = env.isSatisfiableSubtype(t2, t1);
							//
							if(left && !right) {
								return -1;
							} else if(!left && right) {
								return 1;
							}
						}
						return 0;
					}
				};
			}

			private Subtyping.Constraints constraints;
			private Type[] types;

			public Row(Subtyping.Constraints constraints, Type... types) {
				this.constraints = constraints;
				this.types = types;
			}

			public Type get(int index) {
				return types[index];
			}

			public Type[] getAll(int... indices) {
				Type[] ts = new Type[indices.length];
				for (int i = 0; i != indices.length; ++i) {
					ts[i] = types[indices[i]];
				}
				return ts;
			}

			public int size() {
				return types.length;
			}

			/**
			 * Allocate a given number of fresh type variables within this typing
			 * environment.
			 *
			 * @param n
			 * @return
			 */
			public wycc.util.Pair<Row,Type.Existential[]> fresh(int n) {
				int m = constraints.maxVariable() + 1;
				Type.Existential[] vars = new Type.Existential[n];
				for (int i = 0; i != vars.length; ++i) {
					vars[i] = new Type.Existential(m + i);
				}
				Typing.Row nrow = new Typing.Row(constraints.fresh(n), types);
				return new wycc.util.Pair<>(nrow,vars);
			}

			public Row set(int index, Type type) {
				if(type == null || type instanceof Type.Void) {
					return null;
				} else {
					Type[] nTypes = Arrays.copyOf(types, types.length);
					nTypes[index] = type;
					return new Row(constraints, nTypes);
				}
			}

			public Row add(Type type) {
				if(type != null) {
					Type[] nTypes = Arrays.copyOf(types, types.length + 1);
					nTypes[types.length] = type;
					return new Row(constraints, nTypes);
				} else {
					return null;
				}
			}

			public Row[] addAll(int n, BiFunction<Row, Integer, Type[]> fn) {
				if(n == 0) {
					return new Row[] {this};
				} else {
					int m = 0;
					Type[][] nTypes = new Type[n][];
					for (int i = 0; i < n; ++i) {
						nTypes[i] = fn.apply(this, i);
						m = Math.max(m, nTypes[i].length);
					}
					Row[] nRows = new Row[m];
					for (int i = 0; i != m; ++i) {
						Type[] ith = Arrays.copyOf(types, types.length + n);
						for (int j = 0; j < n; ++j) {
							ith[types.length + j] = nTypes[j][i];
						}
						nRows[i] = new Row(constraints, ith);
					}
					return nRows;
				}
			}

			public Row intersect(Subtyping.Constraints constraints) {
				Subtyping.Constraints cs = this.constraints.intersect(constraints);
				if (!cs.isSatisfiable()) {
					return null;
				} else {
					return new Row(cs, types);
				}
			}

			public Row concretise(IncrementalSubtypingEnvironment subtyping) {
				int n = constraints.maxVariable();
				Subtyping.Constraints.Solution solution = constraints.solve(n);
				// Creating the necessary binding function for substitution
				Function<Object, SyntacticItem> binder = o -> o instanceof Integer ? solution.get((Integer) o) : null;
				Type[] nTypes = substitute(types,binder);
				// Create new row only if something changes
				if(nTypes == types) {
					return this;
				} else {
					return new Row(subtyping.TOP,nTypes);
				}
			}

			@Override
			public String toString() {
				return constraints + ":" + Arrays.toString(types);
			}
		}

		private class Finaliser {
			private final Predicate<Row[]> fn;

			public Finaliser(Predicate<Row[]> fn) {
				this.fn = fn;
			}

			public boolean finalise(Row[] solution) {
				return fn.test(solution);
			}

			@Override
			public String toString() {
				return fn.toString();
			}
		}

	}

	private static Type[] substitute(Type[] types, Function<Object, SyntacticItem> binder) {
		// NOTE: optimisation to prevent allocation
		Type[] nTypes = types;
		for (int j = 0; j != nTypes.length; ++j) {
			Type before = types[j];
			Type after = before.substitute(binder);
			if (before != after && nTypes == types) {
				nTypes = Arrays.copyOf(types, types.length);
			}
			nTypes[j] = after;
		}
		return nTypes;
	}

	// ===============================================================================================================
	// Binding
	// ===============================================================================================================

	/**
	 * Represents a candidate binding between a lambda type and declaration. For
	 * example, consider this:
	 *
	 * <pre>
	 * function id<T>(T x) -> (T x):
	 *    return 1
	 * </pre>
	 *
	 * Typing an invocation <code>id(1)</code> which will generate at least one
	 * binding. The <i>candidate</i> type will be
	 * <code>function(T)->(T)<code>, whilst the concrete type is <code>function(int)->(int)</code>.
	 * This binding additionally clarifies how the concrete type is derived from the
	 * candidate type using a mapping from template variables to types (e.g. in this
	 * case <code>{T=>int}</code>).
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Binding {
		private final Tuple<SyntacticItem> arguments;
		private final Decl.Callable candidate;
		private final Type.Callable concreteType;

		public Binding(Decl.Callable candidate, Tuple<SyntacticItem> arguments, Type.Callable concreteType) {
			this.candidate = candidate;
			this.arguments = arguments;
			this.concreteType = concreteType;
		}

		public Decl.Callable getCandidateDeclaration() {
			return candidate;
		}

		public Type.Callable getConcreteType() {
			return concreteType;
		}

		public Tuple<SyntacticItem> getArguments() {
			return arguments;
		}

		@Override
		public String toString() {
			Tuple<Template.Variable> variables = candidate.getTemplate();
			String r = "";
			for (int i = 0; i != variables.size(); ++i) {
				if (i != 0) {
					r = r + ",";
				}
				r += variables.get(i);
				r += "=";
				r += arguments.get(i);
			}
			return "{" + r + "}:" + candidate.getType();
		}
	}

	private static String[] toStrings(Identifier...ids) {
		String[] ss = new String[ids.length];
		for(int i=0;i!=ids.length;++i) {
			ss[i] = ids[i].get();
		}
		return ss;
	}

	private static <T> T internalFailure(String msg, SyntacticItem e) {
		CompilationUnit cu = (CompilationUnit) e.getHeap();
		throw new SyntacticException(msg, cu.getEntry(), e);
	}
}
