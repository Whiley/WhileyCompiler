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
package wyc.check;

import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Pair;

import static wyc.lang.WhileyFile.STMT_assign;
import static wyc.lang.WhileyFile.STMT_dowhile;
import static wyc.lang.WhileyFile.STMT_if;
import static wyc.lang.WhileyFile.STMT_ifelse;
import static wyc.lang.WhileyFile.STMT_namedblock;
import static wyc.lang.WhileyFile.STMT_switch;
import static wyc.lang.WhileyFile.STMT_while;
import static wyc.util.ErrorMessages.INVALID_LVAL_EXPRESSION;
import static wyc.util.ErrorMessages.errorMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import wybs.lang.CompilationUnit;
import wybs.lang.NameResolver;
import wybs.lang.SyntacticItem;
import wybs.lang.SyntaxError.InternalFailure;
import wybs.util.AbstractCompilationUnit.Tuple;
import wycc.util.ArrayUtils;
import wyc.lang.WhileyFile.Decl;
import wyc.lang.WhileyFile.Expr;
import wyc.lang.WhileyFile.LVal;
import wyc.lang.WhileyFile.SemanticType;
import wyc.lang.WhileyFile.Stmt;
import wyc.lang.WhileyFile.Type;
import wyil.type.subtyping.EmptinessTest.LifetimeRelation;
import wyil.type.subtyping.SubtypeOperator;
import wyil.type.util.*;

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
					SemanticType leftT = left.getType(var);
					SemanticType rightT = right.getType(var);
					SemanticType mergeT = new SemanticType.Union(leftT, rightT);
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
			Type.Field field = new Type.Field(((Expr.RecordAccess) expr).getField(), type);
			Type.Record recT = new Type.Record(true, new Tuple<>(field));
			return extractTypeTest(ra.getOperand(), recT);
		} else {
			// no extraction is possible
			return null;
		}
	}

	/**
	 * Determine the set of modifier variables for a given statement block. A
	 * modified variable is one which is assigned.
	 *
	 * @param block
	 * @param scope
	 * @param modified
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
					Expr.VariableAccess lv = extractAssignedVariable(lval);
					if (lv == null) {
						// FIXME: this is not an ideal solution long term. In
						// particular, we really need this method to detect not
						// just modified variables, but also modified locations
						// in general (e.g. assignments through references, etc)
						continue;
					} else {
						modified.add(lv.getVariableDeclaration());
					}
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
	 * @param scope
	 * @return
	 */
	public static Expr.VariableAccess extractAssignedVariable(LVal lval) {
		if (lval instanceof Expr.VariableAccess) {
			return (Expr.VariableAccess) lval;
		} else if (lval instanceof Expr.RecordAccess) {
			Expr.RecordAccess e = (Expr.RecordAccess) lval;
			return extractAssignedVariable((LVal) e.getOperand());
		} else if (lval instanceof Expr.ArrayAccess) {
			Expr.ArrayAccess e = (Expr.ArrayAccess) lval;
			return extractAssignedVariable((LVal) e.getFirstOperand());
		} else if (lval instanceof Expr.Dereference) {
			return null;
		} else {
			internalFailure(errorMessage(INVALID_LVAL_EXPRESSION), lval);
			return null; // dead code
		}
	}


	// ===============================================================================================================
	// isPure
	// ===============================================================================================================

	/**
	 * Determine whether a given expression calls an impure method, dereferences a
	 * reference or accesses a static variable. This is done by exploiting the
	 * uniform nature of syntactic items. Essentially, we just traverse the entire
	 * tree representing the syntactic item looking for expressions of any kind.
	 *
	 * @param item
	 * @return
	 */
	public static boolean isPure(SyntacticItem item) {
		// Examine expression to determine whether this expression is impure.
		if (item instanceof Expr.StaticVariableAccess || item instanceof Expr.Dereference || item instanceof Expr.New) {
			return false;
		} else if (item instanceof Expr.Invoke) {
			Expr.Invoke e = (Expr.Invoke) item;
			if (e.getSignature() instanceof Decl.Method) {
				// This expression is definitely not pure
				return false;
			}
		} else if (item instanceof Expr.IndirectInvoke) {
			Expr.IndirectInvoke e = (Expr.IndirectInvoke) item;
			// FIXME: need to do something here.
			internalFailure("purity checking currently does not support indirect invocation",item);
		}
		// Recursively examine any subexpressions. The uniform nature of
		// syntactic items makes this relatively easy.
		boolean result = true;
		//
		for (int i = 0; i != item.size(); ++i) {
			result &= isPure(item.get(i));
		}
		return result;
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
	public static class Environment implements LifetimeRelation {
		private final Map<Decl.Variable, SemanticType> refinements;
		private final Map<String, String[]> withins;

		public Environment() {
			this.refinements = new HashMap<>();
			this.withins = new HashMap<>();
		}

		public Environment(Map<Decl.Variable, SemanticType> refinements, Map<String, String[]> withins) {
			this.refinements = new HashMap<>(refinements);
			this.withins = new HashMap<>(withins);
		}

		public SemanticType getType(Decl.Variable var) {
			SemanticType refined = refinements.get(var);
			if (refined == null) {
				return var.getType();
			} else {
				return refined;
			}
		}

		public Environment refineType(Decl.Variable var, SemanticType refinement) {
			Environment r = new Environment(this.refinements, this.withins);
			r.refinements.put(var, refinement);
			return r;
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

		public Environment declareWithin(String inner, String... outers) {
			Environment nenv = new Environment(refinements, withins);
			nenv.withins.put(inner, outers);
			return nenv;
		}
	}
	// ===============================================================================================================
	// Type Constructors
	// ===============================================================================================================

	/**
	 * Given an array of expected element types, construct corresponding expected
	 * array types. For example, consider the following simple Whiley snippet:
	 *
	 * <pre>
	 * function f(int[] arr) -> int:
	 *    return arr[0]
	 * </pre>
	 *
	 * The expected type for the expression <code>arr[0]</code> is <code>int</code>.
	 * From this, we calculate the expected type for the expression <code>arr</code>
	 * as <code>int[]</code>.
	 *
	 * @param field
	 * @param expected
	 * @return
	 */
	public static Type.Array[] typeArrayConstructor(Type[] types) {
		Type.Array[] arrayTypes = new Type.Array[types.length];
		for(int i=0;i!=types.length;++i) {
			arrayTypes[i] = new Type.Array(types[i]);
		}
		return arrayTypes;
	}

	/**
	 * Given an array of expected array types, construct corresponding expected
	 * element types. For example, consider the following simple Whiley snippet:
	 *
	 * <pre>
	 * function f(int x) -> int[]:
	 *    return [x]
	 * </pre>
	 *
	 * The expected type for the expression <code>[x]</code> is <code>int[]</code>.
	 * From this, we calculate the expected type for the expression <code>x</code>
	 * as <code>int</code>.
	 *
	 * @return
	 */
	public static Type[] typeArrayElementConstructor(Type.Array[] types) {
		Type[] elements = new Type[types.length];
		for(int i=0;i!=types.length;++i) {
			elements[i] = types[i].getElement();
		}
		return elements;
	}


	/**
	 * Given an array of expected element types, construct corresponding expected
	 * record types. For example, consider the following simple Whiley snippet:
	 *
	 * <pre>
	 * function f({int x} rec) -> int:
	 *    return rec.x
	 * </pre>
	 *
	 * The expected type for the expression <code>rec.x</code> is <code>int</code>.
	 * From this, we calculate the expected type for the expression <code>rec</code>
	 * as <code>{int x,...}</code>.
	 *
	 * @param field
	 * @param expected
	 * @return
	 */
	public static Type.Record[] typeRecordConstructor(Identifier field, Type... expected) {
		Type.Record[] result = new Type.Record[expected.length];
		for (int i = 0; i != expected.length; ++i) {
			Tuple<Type.Field> fields = new Tuple<>(new Type.Field(field, expected[i]));
			result[i] = new Type.Record(true, fields);
		}
		return result;
	}

	/**
	 * Given an array of expected record types, determine corresponding expected
	 * field types. For example, consider the following simple Whiley snippet:
	 *
	 * <pre>
	 * function f(int x) -> {int f}:
	 *    return {f:x}
	 * </pre>
	 *
	 * The expected type for the expression <code>{f:x}</code> is
	 * <code>{int f}</code>. From this, we calculate the expected type for the
	 * expression <code>x</code> as <code>int</code>.
	 *
	 * @param field
	 * @param expected
	 * @return
	 */
	public static Type[] typeRecordFieldConstructor(Type.Record[] types, Identifier fieldName) {
		Type[] fields = new Type[types.length];
		for(int i=0;i!=fields.length;++i) {
			Type.Record type = types[i];
			Type field = type.getField(fieldName);
			if(field == null) {
				if(type.isOpen()) {
					field = Type.Any;
				} else {
					return null;
				}
			}
			fields[i] = field;
		}
		fields = ArrayUtils.removeAll(fields, null);
		if(fields.length == 0) {
			return null;
		} else {
			return fields;
		}
	}

	/**
	 * Given an array of expected reference types, construct corresponding expected
	 * element types. For example, consider the following simple Whiley snippet:
	 *
	 * <pre>
	 * method f(int x):
	 *    &int ptr = new(x)
	 * </pre>
	 *
	 * The expected type for the expression <code>new(x)</code> is
	 * <code>&int</code>. From this, we calculate the expected type for the
	 * expression <code>x</code> as <code>int</code>.
	 *
	 * @param field
	 * @param expected
	 * @return
	 */
	public static Type[] typeReferenceElementConstructor(Type.Reference[] types) {
		Type[] elements = new Type[types.length];
		for(int i=0;i!=types.length;++i) {
			elements[i] = types[i].getElement();
		}
		return elements;
	}


	/**
	 * Given an array of expected lambda types, construct corresponding expected
	 * return types. For example, consider the following simple Whiley snippet:
	 *
	 * <pre>
	 * type fun_t is function(int)->(int)
	 *
	 * method f(int x):
	 *    fun_t|null xs = &(int y -> y+1)
	 *    ...
	 * </pre>
	 *
	 * The expected type for the expression <code>&(int y -> y+1)</code> is
	 * <code>fun_t</code>. From this, we calculate the expected type for the
	 * expression <code>y+1</code> as <code>int</code>.
	 *
	 * @param field
	 * @param expected
	 * @return
	 */
	public static Type[] typeLambdaReturnConstructor(Type.Callable[] types) {
		Type[] returnTypes = new Type[types.length];
		for(int i=0;i!=types.length;++i) {
			// NOTE: this is an implicit assumption that typeLambdaFilter() only ever returns
			// lambda types with exactly one return type.
			returnTypes[i] = types[i].getReturns().get(0);
		}
		return returnTypes;
	}


	// ===============================================================================================================
	// Type Filters
	// ===============================================================================================================

	/**
	 * <p>
	 * Given an array of expected types, filter out the target array types. For
	 * example, consider the following method:
	 * </p>
	 *
	 *
	 * <pre>
	 * method f(int x):
	 *    int[]|null xs = [x]
	 *    ...
	 * </pre>
	 * <p>
	 * When type checking the expression <code>[x]</code> the flow type checker will
	 * attempt to determine an <i>expected</i> array type. In order to then
	 * determine the appropriate expected element type for expression <code>x</code>
	 * it filters <code>int[]|null</code> down to just <code>int[]</code>.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	public static Type.Array[] typeArrayFilter(Type[] types, NameResolver resolver) {
		return TYPE_ARRAY_FILTER.apply(types, resolver);
	}

	/**
	 * <p>
	 * Given an array of expected types, filter out the target record types. For
	 * example, consider the following method:
	 * </p>
	 *
	 *
	 * <pre>
	 * method f(int x):
	 *    {int f}|null xs = {f: x}
	 *    ...
	 * </pre>
	 * <p>
	 * When type checking the expression <code>{f: x}</code> the flow type checker
	 * will attempt to determine an <i>expected</i> record type. In order to then
	 * determine the appropriate expected type for field initialiser expression
	 * <code>x</code> it filters <code>{int f}|null</code> down to just
	 * <code>{int f}</code>.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	public static Type.Record[] typeRecordFilter(Type[] types, NameResolver resolver) {
		return TYPE_RECORD_FILTER.apply(types, resolver);
	}

	/**
	 * Given an array of record types, filter out those which do not contain exactly
	 * the given set of fields. For example, consider this snippet:
	 *
	 * <pre>
	 * method f(int x):
	 *    {int f}|{int g} xs = {f: x}
	 *    ...
	 * </pre>
	 *
	 * When type checking the expression <code>{f: x}</code> the flow type checker
	 * will attempt to determine an <i>expected</i> record type. In order to then
	 * determine the appropriate expected type for field initialiser expression
	 * <code>x</code> it filters <code>{int f}|{int g}</code> down to just
	 * <code>{int f}</code>.
	 *
	 * @param types
	 * @param fields
	 * @param resolver
	 * @return
	 */
	public static Type.Record[] typeRecordFieldFilter(Type.Record[] types, Tuple<Identifier> fields) {
		Type.Record[] result = new Type.Record[types.length];
		for(int i=0;i!=result.length;++i) {
			Type.Record ith = types[i];
			if(compareFields(ith,fields)) {
				result[i] = ith;
			}
		}
		return ArrayUtils.removeAll(result, null);
	}

	private static boolean compareFields(Type.Record ith, Tuple<Identifier> fields) {
		Tuple<Type.Field> ith_fields = ith.getFields();
		//
		if (ith_fields.size() > fields.size()) {
			return false;
		} else if (ith_fields.size() < fields.size() && !ith.isOpen()) {
			return false;
		} else {
			for (int i = 0; i != ith_fields.size(); ++i) {
				Identifier ith_field = ith_fields.get(i).getName();
				boolean matched = false;
				for (int j = 0; j != fields.size(); ++j) {
					Identifier field = fields.get(j);
					if (ith_field.equals(field)) {
						matched = true;
						break;
					}
				}
				if (!matched) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * <p>
	 * Given an array of expected types, filter out the target reference types. For
	 * example, consider the following method:
	 * </p>
	 *
	 *
	 * <pre>
	 * method f(int x):
	 *    &int|null xs = new(x)
	 *    ...
	 * </pre>
	 * <p>
	 * When type checking the expression <code>new(x)</code> the flow type checker
	 * will attempt to determine an <i>expected</i> reference type. In order to then
	 * determine the appropriate expected type for element expression <code>x</code>
	 * it filters <code>&int|null</code> down to just <code>&int</code>.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	public static Type.Reference[] typeReferenceFilter(Type[] types, NameResolver resolver) {
		return TYPE_REFERENCE_FILTER.apply(types, resolver);
	}

	/**
	 * <p>
	 * Given an array of expected types, filter out the target lambda types. For
	 * example, consider the following method:
	 * </p>
	 *
	 *
	 * <pre>
	 * type fun_t is function(int)->(int)
	 *
	 * method f(int x):
	 *    fun_t|null xs = &(int y -> y+1)
	 *    ...
	 * </pre>
	 * <p>
	 * When type checking the expression <code>&(int y -> y+1)</code> the flow type
	 * checker will attempt to determine an <i>expected</i> lambda type. In order to
	 * then determine the appropriate expected type for the lambda body
	 * <code>y+1</code> it filters <code>fun_t|null</code> down to just
	 * <code>fun_t</code>.
	 * </p>
	 *
	 * @param types
	 * @param parameters
	 *            The known parameter types for this lambda
	 * @param isPure
	 *            Indicates whether lambda is pure or not.
	 * @param resolver
	 * @author David J. Pearce
	 *
	 */
	public static Type.Callable[] typeLambdaFilter(Type[] types, Tuple<Type> parameters, NameResolver resolver) {
		// Construct the default case for matching against any
		Type.Callable anyType = new Type.Function(parameters, TUPLE_ANY);
		// Create the filter itself
		AbstractTypeFilter<Type.Callable> filter = new AbstractTypeFilter<>(Type.Callable.class, anyType);
		//
		return filter.apply(types, resolver);
	}

	private static Tuple<Type> TUPLE_ANY = new Tuple<>(Type.Any);

	private static final AbstractTypeFilter<Type.Array> TYPE_ARRAY_FILTER = new AbstractTypeFilter<>(Type.Array.class,
			new Type.Array(Type.Any));

	private static final AbstractTypeFilter<Type.Record> TYPE_RECORD_FILTER = new AbstractTypeFilter<>(Type.Record.class,
			new Type.Record(true, new Tuple<>()));

	private static final AbstractTypeFilter<Type.Reference> TYPE_REFERENCE_FILTER = new AbstractTypeFilter<>(Type.Reference.class,
			new Type.Reference(Type.Any));

	// ===============================================================================================================
	// Type Extractors
	// ===============================================================================================================

	/**
	 * <p>
	 * Responsible for extracting a readable array type. This is a conservative
	 * approximation of that described in a given type which is safe to use when
	 * reading elements from that type. For example, the type
	 * <code>(int[])|(bool[])</code> has a readable array type of
	 * <code>(int|bool)[]</code>. This is the readable type as, if we were to read
	 * an element from either bound, the return type would be in
	 * <code>int|bool</code>. However, we cannot use the readable array type for
	 * writing as this could be unsafe. For example, if we actually had an array of
	 * type <code>int[]</code>, then writing a boolean value is not permitted. Not
	 * all types have readable array type and, furthermore, care must be exercised
	 * for those that do. For example, <code>(int[])|int</code> does not have a
	 * readable array type. Finally, negations play an important role in determining
	 * the readable array type. For example, <code>(int|null)[] & !(int[])</code>
	 * generates the readable array type <code>null[]</code>.
	 * </p>
	 *
	 */
	public static SemanticType.Array typeArrayExtractor(SemanticType type, LifetimeRelation lifetimes,
			SubtypeOperator subtypeOperator, NameResolver resolver) {
		return new TypeArrayExtractor(resolver, subtypeOperator).apply(type, lifetimes);
	}

	/**
	 * <p>
	 * Responsible for extracting a readable record type. This is a conservative
	 * approximation of that described in a given type which is safe to use when
	 * reading elements from that type. For example, the type
	 * <code>{int f}|{bool f}</code> has a readable record type of
	 * <code>{int|bool f}</code>. This is the readable type as, if we were to read
	 * field <code>f</code> from either bound, the return type would be in
	 * <code>int|bool</code>. However, we cannot use the readable record type for
	 * writing as this could be unsafe. For example, if we actually had a record of
	 * type <code>{int f}</code>, then writing a boolean value is not permitted. Not
	 * all types have readable record type and, furthermore, care must be exercised
	 * for those that do. For example, <code>{int f}|int</code> does not have a
	 * readable record type. Likewise, the readable record type for
	 * <code>{int f, int g}|{bool f}</code> is <code>{int|bool f, ...}</code>.
	 * Finally, negations play an important role in determining the readable record
	 * type. For example, <code>{int|null f} & !{int f}</code> generates the
	 * readable record type <code>{null f}</code>.
	 * </p>
	 */
	public static SemanticType.Record typeRecordExtractor(SemanticType type, LifetimeRelation lifetimes,
			SubtypeOperator subtypeOperator, NameResolver resolver) {
		return new TypeRecordExtractor(resolver, subtypeOperator).apply(type, lifetimes);
	}

	/**
	 * <p>
	 * Responsible for extracting a readable reference type. This is a conservative
	 * approximation of that described in a given type which is safe to use when
	 * reading elements from that type. For example, the type
	 * <code>(&int)|(&bool)</code> has a readable reference type of
	 * <code>&(int|bool)</code>. This is the readable type as, if we were to read an
	 * element from either bound, the return type would be in <code>int|bool</code>.
	 * However, we cannot use the readable reference type for writing as this could
	 * be unsafe. For example, if we actually had an reference of type
	 * <code>&int</code>, then writing a boolean value is not permitted. Not all
	 * types have a readable reference type and, furthermore, care must be exercised
	 * for those that do. For example, <code>(&int)|int</code> does not have a
	 * readable reference type.
	 * </p>
	 */
	public static SemanticType.Reference typeReferenceExtractor(SemanticType type, LifetimeRelation lifetimes,
			SubtypeOperator subtypeOperator, NameResolver resolver) {
		return new TypeReferenceExtractor(resolver, subtypeOperator).apply(type, lifetimes);
	}

	// ===============================================================================================================
	// Misc helpers
	// ===============================================================================================================

	private static <T> T internalFailure(String msg, SyntacticItem e) {
		// FIXME: this is a kludge
		CompilationUnit cu = (CompilationUnit) e.getHeap();
		throw new InternalFailure(msg, cu.getEntry(), e);
	}
}
