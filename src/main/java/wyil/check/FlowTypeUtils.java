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

import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Pair;

import static wyc.util.ErrorMessages.syntaxError;
import static wyil.lang.WyilFile.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import wybs.lang.Build;
import wybs.lang.CompilationUnit;
import wybs.lang.SyntacticException;
import wybs.lang.SyntacticItem;
import wybs.util.AbstractCompilationUnit.Tuple;
import wycc.util.ArrayUtils;
import wyil.check.DefiniteAssignmentCheck.ControlFlow;
import wyil.check.DefiniteAssignmentCheck.DefinitelyAssignedSet;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Expr;
import wyil.lang.WyilFile.LVal;
import wyil.lang.WyilFile.Stmt;
import wyil.lang.WyilFile.Type;
import wyil.lang.WyilFile.Type.Field;
import wyil.util.AbstractVisitor;
import wyil.util.SubtypeOperator.LifetimeRelation;

/**
 * This is an overflow class for <code>FlowTypeCheck</code>. It provides various
 * clearly defined helper functions which can be off-loaded to make
 * <code>FlowTypeCheck</code> more focused.
 *
 * @author David J. Pearce
 *
 */
public class FlowTypeUtils {

	/**
	 * Update the environment to reflect the fact that the special "this" lifetime
	 * is contained within all declared lifetime parameters. Observe that this only
	 * makes sense if the enclosing declaration is for a method.
	 *
	 * @param decl
	 * @param environment
	 * @return
	 */
	public static Environment declareThisWithin(Decl.FunctionOrMethod decl, Environment environment) {
		if (decl instanceof Decl.Method) {
			Decl.Method method = (Decl.Method) decl;
			environment = environment.declareWithin("this", method.getLifetimes());
		}
		return environment;
	}

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
					addAssignedVariables(lval,modified);
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
			for(int i=0;i!=operands.size();++i) {
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
			if(sourceType instanceof Type.Method) {
				pure = false;
			}
		}

		@Override
		public void visitType(Type type) {
			// Terminate
		}
	};

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
	public static class Environment implements LifetimeRelation {
		private final Map<Decl.Variable, Type> refinements;
		private final Map<String, String[]> withins;

		public Environment() {
			this.refinements = new HashMap<>();
			this.withins = new HashMap<>();
		}

		public Environment(Map<Decl.Variable, Type> refinements, Map<String, String[]> withins) {
			this.refinements = new HashMap<>(refinements);
			this.withins = new HashMap<>(withins);
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
			if(getType(var).equals(refinement)) {
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
			r = r + "}{";
			firstTime = true;
			for(Map.Entry<String, String[]> w : withins.entrySet()) {
				if(!firstTime) {
					r += ", ";
				}
				firstTime=false;
				r = r + w.getKey() + " < " + Arrays.toString(w.getValue());
			}
			return r + "}";
		}

		@Override
		public boolean isWithin(String inner, String outer) {
			//
			if (outer.equals("*") || inner.equals(outer)) {
				// Cover easy cases first
				return true;
			} else {
				String[] outers = withins.get(inner);
				return outers != null && (ArrayUtils.firstIndexOf(outers, outer) >= 0);
			}
		}

		public Environment declareWithin(String inner, Tuple<Identifier> outers) {
			String[] outs = new String[outers.size()];
			for (int i = 0; i != outs.length; ++i) {
				outs[i] = outers.get(i).get();
			}
			return declareWithin(inner, outs);
		}

		public Environment declareWithin(String inner, Identifier... outers) {
			String[] outs = new String[outers.length];
			for (int i = 0; i != outs.length; ++i) {
				outs[i] = outers[i].get();
			}
			return declareWithin(inner, outs);
		}

		public Environment declareWithin(String inner, String... outers) {
			Environment nenv = new Environment(refinements, withins);
			nenv.withins.put(inner, outers);
			return nenv;
		}
	}
}
