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
package wyil.util;

import static wyil.lang.WyilFile.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import wycc.util.AbstractCompilationUnit.Tuple;
import wyil.lang.WyilFile.Type;

public interface Subtyping {

	/**
	 * Provides an environment in which it makes sense to talk about one type being
	 * a subtype of another. In particular, such an environment must account for the
	 * dynamic lifetime graph which is determined by the specific position within a
	 * given source file.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Environment {

		/**
		 * <p>
		 * Empty (i.e non-contractive) types are types which cannot accept a value
		 * because they have an <i>unterminated cycle</i>. An unterminated cycle has no
		 * leaf nodes terminating it. For example, <code>X<{X field}></code> is
		 * contractive, where as <code>X<{null|X field}></code> is not.
		 * </p>
		 *
		 * <p>
		 * This method returns true if the type is contractive, or contains a
		 * contractive subcomponent. For example, <code>null|X<{X field}></code> is
		 * considered contractive.
		 * </p>
		 *
		 * @param type --- type to test for contractivity.
		 * @return
		 * @throws ResolveError
		 */
		public boolean isEmpty(QualifiedName nid, Type type);

		/**
		 * Subtract one type from another to produce the result. For example, subtracting
		 * <code>null</code> from <code>int|null</code> produces <code>int</code>.
		 *
		 * @param t1
		 * @param t2
		 * @return
		 */
		public Type subtract(Type t1, Type t2);

		/**
		 * <p>
		 * Determine whether the <code>rhs</code> type is a <i>subtype</i> of the
		 * <code>lhs</code> (denoted <code>lhs :> rhs</code>). In the presence of type
		 * invariants, this operation is undecidable. Therefore, a <i>three-valued</i>
		 * logic is employed. Either it was concluded that the subtype relation
		 * <i>definitely holds</i>, or that it <i>definitely does not hold</i> that it
		 * is <i>unknown</i> whether it holds or not.
		 * </p>
		 *
		 * <p>
		 * For example, <code>int|null :> int</code> definitely holds. Likewise,
		 * <code>int :> int|null</code> definitely does not hold. However, whether or
		 * not <code>nat :> pos</code> holds depends on the type invariants given for
		 * <code>nat</code> and <code>pos</code> which this operator cannot reason
		 * about. Observe that, in some cases, we do get effective reasoning about types
		 * with invariants. For example, <code>null|nat :> nat</code> will be determined
		 * to definitely hold, despite the fact that <code>nat</code> has a type
		 * invariant.
		 * </p>
		 *
		 * <p>
		 * Depending on the exact language of types involved, this can be a surprisingly
		 * complex operation. For example, in the presence of <i>union</i>,
		 * <i>intersection</i> and <i>negation</i> types, the subtype algorithm is
		 * surprisingly intricate.
		 * </p>
		 *
		 * @param lhs The candidate "supertype". That is, lhs's raw type may be a
		 *            supertype of <code>rhs</code>'s raw type.
		 * @param rhs The candidate "subtype". That is, rhs's raw type may be a subtype
		 *            of <code>lhs</code>'s raw type.
		 * @return A given constraints set which may or may not be satisfiable. If the
		 *         constraints are not satisfiable then the relation does not hold.
		 */
		public Constraints isSubtype(Type lhs, Type rhs);

		/**
		 * <p>
		 * Determine whether the <code>rhs</code> type is a <i>subtype</i> of the
		 * <code>lhs</code> (denoted <code>lhs :> rhs</code>). In the presence of type
		 * invariants, this operation is undecidable. Therefore, a <i>three-valued</i>
		 * logic is employed. Either it was concluded that the subtype relation
		 * <i>definitely holds</i>, or that it <i>definitely does not hold</i> that it
		 * is <i>unknown</i> whether it holds or not.
		 * </p>
		 *
		 * <p>
		 * For example, <code>int|null :> int</code> definitely holds. Likewise,
		 * <code>int :> int|null</code> definitely does not hold. However, whether or
		 * not <code>nat :> pos</code> holds depends on the type invariants given for
		 * <code>nat</code> and <code>pos</code> which this operator cannot reason
		 * about. Observe that, in some cases, we do get effective reasoning about types
		 * with invariants. For example, <code>null|nat :> nat</code> will be determined
		 * to definitely hold, despite the fact that <code>nat</code> has a type
		 * invariant.
		 * </p>
		 *
		 * <p>
		 * Depending on the exact language of types involved, this can be a surprisingly
		 * complex operation. For example, in the presence of <i>union</i>,
		 * <i>intersection</i> and <i>negation</i> types, the subtype algorithm is
		 * surprisingly intricate.
		 * </p>
		 *
		 * @param lhs The candidate "supertype". That is, lhs's raw type may be a
		 *            supertype of <code>rhs</code>'s raw type.
		 * @param rhs The candidate "subtype". That is, rhs's raw type may be a subtype
		 *            of <code>lhs</code>'s raw type.
		 * @return A boolean indicating whether or not one is a subtype of the other.
		 */
		public boolean isSatisfiableSubtype(Type lhs, Type rhs);

		/**
		 * <p>
		 * Return the greatest lower bound of two types. That is, for any given types
		 * <code>T1</code> and <code>T2</code> return <code>T3</code> where
		 * <code>T1 :> T3</code> and <code>T2 :> T3</code> such that no
		 * <code>T4 :> T3</code> exists which is also a lower bound of <code>T1</code>
		 * and <code>T2</code>. Observe that such a lower bound always exists, as
		 * <code>void</code> is a lower bound of any two types. The following
		 * illustrates some examples:
		 * </p>
		 *
		 * <pre>
		 * int /\ int ============> int
		 * nat /\ pos ============> void
		 * bool /\ int ===========> void
		 * int|null /\ int =======> int
		 * int|null /\ nat =======> nat
		 * int|null /\ bool|nat ==> nat
		 * </pre>
		 *
		 * <p>
		 * Here, <code>nat</code> is <code>(int n) where n >= 0</code> and
		 * <code>pos</code> is <code>(int n) where n > 0</code>. Observe that, if
		 * <code>pos</code> was declared as <code>(nat n) where n >= 0</code> then
		 * <code>nat /\ pos ==> pos</code>.
		 * </p>
		 * <p>
		 * <b>NOTE:</b> If the result equals either of the parameters, then that
		 * parameter must be returned to allow reference equality to be used to
		 * determine whether the result matches either parameter.
		 * </p>
		 *
		 * @param lhs
		 * @param rhs
		 * @return
		 */
		public Type greatestLowerBound(Type lhs, Type rhs);

		/**
		 * <p>
		 * Return the least upper bound of two types. That is, for any given types
		 * <code>T2</code> and <code>T3</code> return <code>T1</code> where
		 * <code>T1 :> T3</code> and <code>T1 :> T2</code> such that no
		 * <code>T1 :> T0</code> exists which is also an upper bound of <code>T2</code>
		 * and <code>T3</code>. Observe that such an upper bound always exists, as
		 * <code>any</code> is an upper bound of any two types. The following
		 * illustrates some examples:
		 * </p>
		 *
		 * <pre>
		 * int \/ int ============> int
		 * int \/ nat ============> int
		 * nat \/ pos ============> nat|pos
		 * int \/ null ===========> int|null
		 * int|null \/ nat =======> null|nat
		 * int|null \/ bool|nat ==> null|bool|nat
		 * </pre>
		 *
		 * <p>
		 * Here, <code>nat</code> is <code>(int n) where n >= 0</code> and
		 * <code>pos</code> is <code>(int n) where n > 0</code>. Observe that, if
		 * <code>pos</code> was declared as <code>(nat n) where n >= 0</code> then
		 * <code>nat \/ pos ==> nat</code>.
		 * </p>
		 * <p>
		 * <b>NOTE:</b> If the result equals either of the parameters, then that
		 * parameter must be returned to allow reference equality to be used to
		 * determine whether the result matches either parameter.
		 * </p>
		 *
		 * @param lhs
		 * @param rhs
		 * @return
		 */
		public Type leastUpperBound(Type lhs, Type rhs);

		/**
		 * <p>
		 * Determine, for any two lifetimes <code>l</code> and <code>m</code>, whether
		 * <code>l</code> is contained within <code>m</code> or not. This information is
		 * critical for subtype checking of reference types. Consider this minimal
		 * example:
		 * </p>
		 *
		 * <pre>
		 * method create() -> (&*:int r):
		 *    return this:new 42
		 * </pre>
		 * <p>
		 * This example should not compile. The reason is that the lifetime
		 * <code>this</code> is contained <i>within</i> the static lifetime
		 * <code>*</code>. Thus, the cell allocated within <code>create()</code> will be
		 * deallocated when the method ends and, hence, the method will return a
		 * <i>dangling reference</i>.
		 * </p>
		 *
		 * <p>
		 * More generally, an assignment <code>&l:T p = q</code> is only considered safe
		 * if it can be shown that the lifetime of the cell referred to by
		 * <code>p</code> is <i>within</i> that of <code>q</code>.
		 * </p>
		 *
		 * @param inner Lifetime which should be enclosed.
		 * @param outer Lifetime which should be enclosing.
		 * @author David J. Pearce
		 * @return
		 */
		public boolean isWithin(String inner, String outer);

		/**
		 * Declare a given lifetime
		 * @param inner
		 * @param outers
		 * @return
		 */
		public Environment declareWithin(String inner, String... outers);
	}

	/**
	 * represents a set of subtyping constraints which must be satisfiable for a
	 * given subtyping relationship to hold.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Constraints {
		/**
		 * Determine whether constraints satisfiable or not
		 *
		 * @return
		 */
		public boolean isSatisfiable();

		/**
		 * Get the number of constraints in this set.
		 *
		 * @return
		 */
		public int size();

		/**
		 * Get the largest constraint variable referenced in this constraint set, or
		 * <code>-1</code> if none.
		 *
		 * @return
		 */
		public int maxVariable();

		/**
		 * Get the ith constraint in this set.
		 *
		 * @param ith
		 * @return
		 */
		public Constraint get(int ith);

		/**
		 * Allocate zero or more variables to this constraint set.
		 *
		 * @param n
		 * @return
		 */
		public Constraints fresh(int n);

		/**
		 * Intersect against one or more subtype constraints.
		 *
		 * @param other
		 * @return
		 */
		public Constraints intersect(Subtyping.Constraint... other);

		/**
		 * Intersect two sets of subtyping constraints.
		 *
		 * @param other
		 * @return
		 */
		public Constraints intersect(Constraints other);

		/**
		 * Extract best possible solutions.
		 *
		 * @param n
		 * @return
		 */
		public Solution solve(int n);

		/**
		 * Represents a solution to a set of subtyping constraints. Each type variable
		 * has a given lower and upper bound. As the solution evolves, these bounds are
		 * narrowed done. If we end up where the lower bound is not a subtype of the
		 * upper bound for some variable, then the solution is invalid.
		 *
		 * @author David J. Pearce
		 *
		 */
		public interface Solution {
			/**
			 * Return the number of variables which are currently considered in this
			 * solution.
			 *
			 * @return
			 */
			public int size();

			/**
			 * Check whether a given solution is fully satisfied or not. In short, whether
			 * or not any variables remain which have neither an upper or lower bound. Such
			 * variables are problematic because we cannot determine a valid type for them
			 * (i.e. as neither <code>any</code> nor <code>void</code> are valid types). If
			 * the solution is satisfiable but not yet satisfied, then it means we need to
			 * continue atttempt to find a solution. Note, however, than an unsatisfiable
			 * solution is considered to be satisfied for simplicity.
			 *
			 * @param n
			 * @return
			 */
			public boolean isComplete(int n);

			/**
			 * Check whether the given solution is satisfiable or not. That is, whether or
			 * not there are any bounds which definitely cannot be satisfied. For example,
			 * <code>{?0 :> int}</code> is satisfiable with <code>?0=int</code>. However,
			 * <code>{bool :> ?0 :> int}</code> is not satisfiable.
			 *
			 * @param n
			 * @param env
			 * @return
			 */
			public boolean isUnsatisfiable();

			/**
			 * Get current solution for ith variable.
			 *
			 * @param i
			 * @return
			 */
			public Type get(int i);

			/**
			 * Get lower bound for given variable.
			 *
			 * @param i
			 * @return
			 */
			public Type floor(int i);

			/**
			 * Get upper bound for given variable.
			 *
			 * @param i
			 * @return
			 */
			public Type ceil(int i);

			/**
			 * Constrain the solution of a given variable with a given (concrete) lower
			 * bound. If the solution becomes invalid, return BOTTOM.
			 *
			 * @param i           Variable to be constrained
			 * @param nLowerBound New lowerbound to constrain with
			 * @return
			 */
			public Solution constrain(int i, Type nLowerBound);

			/**
			 * Constraint the solution of a given variable with a given (concrete) upper
			 * bound. If the solution becomes invalid, return BOTTOM.
			 *
			 * @param nUpperBound New upperbound to constrain with
			 * @param i           Variable to be constrained
			 * @return
			 */
			public Solution constrain(Type nUpperBound, int i);
		}


		/**
		 * <p>
		 * Represents a matrix of possible typings under consideration. Each environment
		 * (row) in this matrix represents one possible typing, whilst each column
		 * represents the possible types for a given (sub)expression. If no rows remain,
		 * then there are no possible typings and, hence, the original expression is
		 * untypeable. On the other hand if, at the end, we have more than one possible
		 * typing then the original expression is ambiguous. For example, consider
		 * typing this statement:
		 * </p>
		 *
		 * <pre>
		 * int x = ...
		 * bool y = f(x) == 1
		 * </pre>
		 *
		 * <p>
		 * The expression <code>f(x) + 1</code> gives rise to typing matrices with four
		 * columns. One possible matrix might be:
		 * </p>
		 *
		 * <pre>
		 *  |0   |1   |2   |3   |
		 * -+----+----+----+----+
		 * 0|int |bool|int |bool|
		 * -+----+----+----+----+
		 * 1|int |int |int |bool|
		 * -+----+----+----+----+
		 * </pre>
		 *
		 * <p>
		 * The first column corresponds to the type for subexpression <code>x</code>.
		 * Since <code>x</code> is declared as having <code>int</code>, every entry is
		 * <code>int</code>. The second column corresponds to the type of expression
		 * <code>f(x)</code>. In this case, function <code>f()</code> must be overloaded
		 * to return <code>bool</code> in one case and <code>int</code> in another. The
		 * third column represents subexpression <code>1</code> and the fourth column
		 * represents the complete expression.
		 * </p>
		 * <p>
		 * In the above, it should be clear that the first row is invalid since we
		 * cannot compare a boolean with an integer. As such, at some point during
		 * typing, this row will be "invalidated" (i.e. removed from the set of typings
		 * under consideration).
		 * </p>
		 *
		 * @author David J Pearce
		 *
		 */
		public interface Set {

			/**
			 * Return flag indicating whether there are no valid typings remaining, or not.
			 * This is equivalent to <code>height() == 0</code>.
			 *
			 * @return
			 */
			public boolean empty();

			/**
			 * Return the number of typings that remain under consideration. If this is
			 * zero, then there are no valid typings and the original expression cannot be
			 * typed. On the other hand, if there is more than one valid typing (at the end)
			 * then the original expression is ambiguous.
			 *
			 * @return
			 */
			public int height();

			/**
			 * Get the ith row of this typing.
			 *
			 * @param ith
			 * @return
			 */
			public Subtyping.Constraints get(int ith);

			/**
			 * Attempt to collapse all rows down together using a given comparator. This may
			 * or may not result in a typing which can be finalised.
			 *
			 * @param fn
			 * @return
			 */
			public Set fold(Comparator<Subtyping.Constraints> fn);

			/**
			 * Apply a given function to all rows of the typing matrix producing a
			 * potentially updated set of typing constraints. As part of this process, rows
			 * may be invalidated if they fail to meet some criteria.
			 *
			 * @param fn The mapping function which is applied to each row. This returns
			 *           either an updated row, or <code>null</code>, if the row is
			 *           invalidated.
			 * @return
			 */
			public Set map(Function<Subtyping.Constraints, Subtyping.Constraints> fn);

			/**
			 * Project each row of the typing matrix into zero or more rows, thus producing
			 * a potentially updated constraint set. As part of this process, rows may be
			 * added or removed based on various criteria.
			 *
			 * @param fn The mapping function which is applied to each row. This returns
			 *           zero or more updated rows. Note that it should not return
			 *           <code>null</code>.
			 * @return
			 */
			public Set project(Function<Subtyping.Constraints, Subtyping.Constraints[]> fn);

			/**
			 * Apply a function to each row of the typing matrix, producing an array of
			 * results
			 *
			 * @param fn The mapping function which is applied to each row. This returns
			 *           some element for each row.
			 * @return
			 */
			public void foreach(Consumer<Subtyping.Constraints> fn);
		}
	}

	/**
	 * Represents a single subtyping constraint of the form <code>T1 :> T2</code>.
	 * Such constraints can be concrete whether neither bound contains an
	 * existential variable; or, either bound can contain an existential. For
	 * example, <code>int|null :> int</code> is a concrete constraint which holds.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Constraint {
		/**
		 * Return the largest constraint variable referenced in this constraint, or
		 * <code>-1</code> if none present.
		 *
		 * @return
		 */
		public int maxVariable();

		/**
		 * Apply this constraint to a given solution producing a potentially updated
		 * solution.
		 *
		 * @param solution
		 * @return
		 */
		public Constraints.Solution apply(Constraints.Solution solution);
	}

	// ===========================================================================
	// Constraints
	// ===========================================================================

	/**
	 * A simple implementation of a single symboling subtyping constraint.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class LowerBoundConstraint implements Subtyping.Constraint {
		private final IncrementalSubtypingEnvironment environment;
		private final int variable;
		private final Type lowerBound;

		public LowerBoundConstraint(IncrementalSubtypingEnvironment environment, Type.Existential variable, Type lower) {
			if(lower == null || lower instanceof Type.Void) {
				throw new IllegalArgumentException("invalid lower bound (" + lower + ")");
			}
			this.environment = environment;
			this.variable = variable.get();
			this.lowerBound = lower;
		}

		@Override
		public int maxVariable() {
			return Math.max(variable, Subtyping.maxVariable(lowerBound));
		}

		@Override
		public Constraints.Solution apply(Constraints.Solution solution) {
			Type cUpper = solution.ceil(variable);
			if(!(cUpper instanceof Type.Any || cUpper instanceof Type.Void)) {
				Subtyping.Constraints cs = environment.isSubtype(cUpper, lowerBound);
				// Propagate information downwards
				for(int i=0;i!=cs.size();++i) {
					solution = cs.get(i).apply(solution);
				}
			}
			if (lowerBound instanceof Type.Void) {
				return solution;
			} else {
				// Propagate information upwards
				Type cLower = substitute(lowerBound, solution, false);
				// Here, cLower.isConcrete() guaranteed true
				return solution.constrain(variable, cLower);
			}
		}

		@Override
		public boolean equals(Object o) {
			if(o instanceof LowerBoundConstraint) {
				LowerBoundConstraint c = (LowerBoundConstraint) o;
				return variable == c.variable && lowerBound.equals(c.lowerBound);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return variable ^ lowerBound.hashCode();
		}

		@Override
		public String toString() {
			return "?" + variable + ":>" + lowerBound;
		}
	}

	public static class UpperBoundConstraint implements Subtyping.Constraint {
		private final IncrementalSubtypingEnvironment environment;
		private final Type upperBound;
		private final int variable;

		public UpperBoundConstraint(IncrementalSubtypingEnvironment environment, Type upper, Type.Existential variable) {
			if(upper == null || upper instanceof Type.Any) {
				throw new IllegalArgumentException("invalid upper bound");
			}
			this.environment = environment;
			this.upperBound = upper;
			this.variable = variable.get();
		}

		@Override
		public int maxVariable() {
			return Math.max(variable, Subtyping.maxVariable(upperBound));
		}

		@Override
		public Constraints.Solution apply(Constraints.Solution solution) {
			Type cLower = solution.floor(variable);
			if(!(cLower instanceof Type.Void || cLower instanceof Type.Any)) {
				Subtyping.Constraints cs = environment.isSubtype(upperBound,cLower);
				// Propagate information upwards
				for(int i=0;i!=cs.size();++i) {
					solution = cs.get(i).apply(solution);
				}
			}
			if (upperBound instanceof Type.Any) {
				return solution;
			} else {
				// Propagate information downwards
				Type cUpper = substitute(upperBound, solution, true);
				//
				return solution.constrain(cUpper, variable);
			}
		}

		@Override
		public boolean equals(Object o) {
			if(o instanceof UpperBoundConstraint) {
				UpperBoundConstraint c = (UpperBoundConstraint) o;
				return variable == c.variable && upperBound.equals(c.upperBound);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return variable ^ upperBound.hashCode();
		}

		@Override
		public String toString() {
			return upperBound + ":>?" + variable;
		}
	}





	// ===============================================================================
	// maxVariable
	// ===============================================================================

	public static int numberOfVariables(Subtyping.Constraint... constraints) {
		int n = 0;
		for (int i = 0; i != constraints.length; ++i) {
			n = Math.max(constraints[i].maxVariable() + 1, n);
		}
		return n;
	}
	/**
	 * Determine the maximum (existential) type variable used in this type, or <code>-1</code> if none present.
	 *
	 * @param type The type being tested for the presence of existential variables.
	 * @return
	 */
	public static int maxVariable(Type type) {
		switch (type.getOpcode()) {
		case TYPE_any:
		case TYPE_bool:
		case TYPE_byte:
		case TYPE_int:
		case TYPE_null:
		case TYPE_void:
		case TYPE_universal:
			return -1;
		case TYPE_existential:
			return ((Type.Existential)type).get();
		case TYPE_array:
			return maxVariable(((Type.Array)type).getElement());
		case TYPE_reference:
			return maxVariable(((Type.Reference)type).getElement());
		case TYPE_function:
		case TYPE_method:
		case TYPE_property: {
			Type.Callable t = (Type.Callable) type;
			return Math.max(maxVariable(t.getParameter()), maxVariable(t.getReturn()));
		}
		case TYPE_nominal:
			return maxVariable(((Type.Nominal)type).getParameters());
		case TYPE_tuple:
			return maxVariable(((Type.Tuple)type).getAll());
		case TYPE_union:
			return maxVariable(((Type.Union)type).getAll());
		case TYPE_record: {
			Type.Record t = (Type.Record) type;
			Tuple<Type.Field> fields = t.getFields();
			int m = -1;
			for (int i = 0; i != fields.size(); ++i) {
				m = Math.max(m, maxVariable(fields.get(i).getType()));
			}
			return m;
		}
		default:
			throw new IllegalArgumentException("unknown type encountered (" + type.getClass().getName() + ")");
		}
	}

	public static int maxVariable(Tuple<Type> types) {
		int m = -1;
		for (int i = 0; i != types.size(); ++i) {
			m = Math.max(m, maxVariable(types.get(i)));
		}
		return m;
	}

	public static int maxVariable(Type[] types) {
		int m = -1;
		for (int i = 0; i != types.length; ++i) {
			m = Math.max(m, maxVariable(types[i]));
		}
		return m;
	}

	// ===============================================================================
	// isConcrete
	// ===============================================================================

	/**
	 * Check whether a given type is "concrete" or not. That is, whether or not it
	 * contains a nested existential type variable. For example, the type
	 * <code>int</code> does not contain an existential variable! In contrast, the
	 * type <code>{?1 f}</code> does. This method performs a fairly straightforward
	 * recursive descent through the type tree search for existentiuals.
	 *
	 * @param type The type being tested for the presence of existential variables.
	 * @return
	 */
	public static boolean isConcrete(Type type) {
		switch (type.getOpcode()) {
		case TYPE_any:
		case TYPE_bool:
		case TYPE_byte:
		case TYPE_int:
		case TYPE_null:
		case TYPE_void:
		case TYPE_universal:
			return true;
		case TYPE_existential:
			return false;
		case TYPE_array: {
			Type.Array t = (Type.Array) type;
			return isConcrete(t.getElement());
		}
		case TYPE_reference: {
			Type.Reference t = (Type.Reference) type;
			return isConcrete(t.getElement());
		}
		case TYPE_function:
		case TYPE_method:
		case TYPE_property: {
			Type.Callable t = (Type.Callable) type;
			return isConcrete(t.getParameter()) && isConcrete(t.getReturn());
		}
		case TYPE_nominal: {
			Type.Nominal t = (Type.Nominal) type;
			return isConcrete(t.getParameters());
		}
		case TYPE_tuple: {
			Type.Tuple t = (Type.Tuple) type;
			return isConcrete(t.getAll());
		}
		case TYPE_union: {
			Type.Union t = (Type.Union) type;
			return isConcrete(t.getAll());
		}
		case TYPE_record: {
			Type.Record t = (Type.Record) type;
			Tuple<Type.Field> fields = t.getFields();
			for (int i = 0; i != fields.size(); ++i) {
				if (!isConcrete(fields.get(i).getType())) {
					return false;
				}
			}
			return true;
		}
		default:
			throw new IllegalArgumentException("unknown type encountered (" + type.getClass().getName() + ")");
		}
	}

	public static boolean isConcrete(Tuple<Type> types) {
		for (int i = 0; i != types.size(); ++i) {
			if (!isConcrete(types.get(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isConcrete(Type[] types) {
		for (int i = 0; i != types.length; ++i) {
			if (!isConcrete(types[i])) {
				return false;
			}
		}
		return true;
	}

	// ================================================================================
	// Substitute
	// ================================================================================

	/**
	 * Substitute all existential type variables in a given a type in either an
	 * upper or lower bound position. For example, consider substituting into
	 * <code>{?0 f}</code> with a solution <code>int|bool :> ?0 :> int</code>. In
	 * the upper position, we end up with <code>{int|bool f}</code> and in the lower
	 * position we have <code>{int f}</code>. A key issue is that positional
	 * variance must be observed. This applies, for example, to lambda types where
	 * parameters are <i>contravariant</i>. Thus, consider substituting into
	 * <code>function(?0)->(?0)</code> with a solution
	 * <code>int|bool :> ?0 :> int</code>. In the upper bound position we get
	 * <code>function(int)->(int|bool)</code>, whilst in the lower bound position we
	 * have <code>function(int|bool)->(int)</code>.
	 *
	 * @param type     The type being substituted into.
	 * @param solution The solution being used for substitution.
	 * @param sign     Indicates the upper (<code>true</code>) or lower bound
	 *                 (<code>false</code>) position.
	 * @return
	 */
	public static Type substitute(Type type, Subtyping.Constraints.Solution solution, boolean sign) {
		switch (type.getOpcode()) {
		case TYPE_any:
		case TYPE_bool:
		case TYPE_byte:
		case TYPE_int:
		case TYPE_null:
		case TYPE_void:
		case TYPE_universal:
			return type;
		case TYPE_existential: {
			Type.Existential t = (Type.Existential) type;
			int var = t.get();
			return sign ? solution.ceil(var) : solution.floor(var);
		}
		case TYPE_array: {
			Type.Array t = (Type.Array) type;
			Type element = t.getElement();
			Type nElement = substitute(element, solution, sign);
			if (element == nElement) {
				return type;
			} else if (nElement instanceof Type.Void) {
				return Type.Void;
			} else if (nElement instanceof Type.Any) {
				return Type.Any;
			} else {
				return new Type.Array(nElement);
			}
		}
		case TYPE_reference: {
			Type.Reference t = (Type.Reference) type;
			Type element = t.getElement();
			// NOTE: this substitution is effectively a co-variant substitution. Whilst this
			// may seem problematic, it isn't because we'll always eliminate variables whose
			// bounds are not subtypes of each other. For example, <code>&(int|bool) :> ?1
			// :> &(int)</code> is not satisfiable.
			Type nElement = substitute(element, solution, sign);
			if (element == nElement) {
				return type;
			} else if (nElement instanceof Type.Void) {
				return Type.Void;
			} else if (nElement instanceof Type.Any) {
				return Type.Any;
			} else {
				return new Type.Reference(nElement);
			}
		}
		case TYPE_function:
		case TYPE_method:
		case TYPE_property: {
			Type.Callable t = (Type.Callable) type;
			Type parameters = t.getParameter();
			Type returns = t.getReturn();
			// NOTE: invert sign to account for contra-variance
			Type nParameters = substitute(parameters, solution, !sign);
			Type nReturns = substitute(returns, solution, sign);
			if (nParameters == parameters && nReturns == returns) {
				return type;
			} else if (nReturns instanceof Type.Void || nParameters instanceof Type.Any) {
				return Type.Void;
			} else if (nReturns instanceof Type.Any || nParameters instanceof Type.Void) {
				return Type.Any;
			} else if (type instanceof Type.Function) {
				return new Type.Function(nParameters, nReturns);
			} else if (type instanceof Type.Property) {
				return new Type.Property(nParameters, nReturns);
			} else {
				Type.Method m = (Type.Method) type;
				return new Type.Method(nParameters, nReturns);
			}
		}
		case TYPE_nominal: {
			Type.Nominal t = (Type.Nominal) type;
			Tuple<Type> parameters = t.getParameters();
			// NOTE: the following is problematic in the presence of contra-variant
			// parameter positions. However, this is not unsound per se. Rather it will just
			// mean some variables are eliminated because their bounds are considered
			// unsatisfiable.
			Tuple<Type> nParameters = substitute(parameters, solution, sign);
			if (parameters == nParameters) {
				return type;
			} else {
				// Sanity check substitution makes sense
				for (int i = 0; i != nParameters.size(); ++i) {
					Type ith = nParameters.get(i);
					if (ith instanceof Type.Void) {
						return Type.Void;
					} else if (ith instanceof Type.Any) {
						return Type.Any;
					}
				}
				return new Type.Nominal(t.getLink(), nParameters);
			}
		}
		case TYPE_tuple: {
			Type.Tuple t = (Type.Tuple) type;
			Type[] elements = t.getAll();
			Type[] nElements = substitute(elements, solution, sign);
			if (elements == nElements) {
				return type;
			} else {
				// Sanity check substitution makes sense
				for (int i = 0; i != nElements.length; ++i) {
					Type ith = nElements[i];
					if (ith instanceof Type.Void) {
						return Type.Void;
					} else if (ith instanceof Type.Any) {
						return Type.Any;
					}
				}
				// Done
				return Type.Tuple.create(nElements);
			}
		}
		case TYPE_union: {
			Type.Union t = (Type.Union) type;
			Type[] elements = t.getAll();
			Type[] nElements = substitute(elements, solution, sign);
			if (elements == nElements) {
				return type;
			} else {
				// Sanity check substitution makes sense
				for (int i = 0; i != nElements.length; ++i) {
					Type ith = nElements[i];
					if (ith instanceof Type.Void) {
						return Type.Void;
					} else if (ith instanceof Type.Any) {
						return Type.Any;
					}
				}
				// Done
				return Type.Union.create(nElements);
			}
		}
		case TYPE_record: {
			Type.Record t = (Type.Record) type;
			Tuple<Type.Field> fields = t.getFields();
			Tuple<Type.Field> nFields = substituteFields(fields, solution, sign);
			if (fields == nFields) {
				return type;
			} else {
				// Sanity check substitution makes sense
				for (int i = 0; i != nFields.size(); ++i) {
					Type ith = nFields.get(i).getType();
					if (ith instanceof Type.Void) {
						return Type.Void;
					} else if (ith instanceof Type.Any) {
						return Type.Any;
					}
				}
				return new Type.Record(t.isOpen(), nFields);
			}
		}
		default:
			throw new IllegalArgumentException("unknown type encountered (" + type.getClass().getName() + ")");
		}
	}

	public static Tuple<Type> substitute(Tuple<Type> types, Subtyping.Constraints.Solution solution, boolean sign) {
		for (int i = 0; i != types.size(); ++i) {
			Type t = types.get(i);
			Type n = substitute(t, solution, sign);
			if (t != n) {
				// Committed to change
				Type[] nTypes = new Type[types.size()];
				// Copy all visited so far over
				System.arraycopy(types.getAll(), 0, nTypes, 0, i + 1);
				// Continue substitution
				for (; i < nTypes.length; ++i) {
					nTypes[i] = substitute(types.get(i), solution, sign);
				}
				// Done
				return new Tuple<>(nTypes);
			}
		}
		return types;
	}

	public static Tuple<Type.Field> substituteFields(Tuple<Type.Field> fields, Subtyping.Constraints.Solution solution,
			boolean sign) {
		for (int i = 0; i != fields.size(); ++i) {
			Type.Field t = fields.get(i);
			Type.Field n = substituteField(t, solution, sign);
			if (t != n) {
				// Committed to change
				Type.Field[] nFields = new Type.Field[fields.size()];
				// Copy all visited so far over
				System.arraycopy(fields.getAll(), 0, nFields, 0, i + 1);
				// Continue substitution
				for (; i < nFields.length; ++i) {
					nFields[i] = substituteField(fields.get(i), solution, sign);
				}
				// Done
				return new Tuple<>(nFields);
			}
		}
		return fields;
	}

	public static Type.Field substituteField(Type.Field field, Subtyping.Constraints.Solution solution, boolean sign) {
		Type type = field.getType();
		Type nType = substitute(type, solution, sign);
		if (type == nType) {
			return field;
		} else {
			return new Type.Field(field.getName(), nType);
		}
	}

	public static Type[] substitute(Type[] types, Subtyping.Constraints.Solution solution, boolean sign) {
		Type[] nTypes = types;
		for (int i = 0; i != nTypes.length; ++i) {
			Type t = types[i];
			Type n = substitute(t, solution, sign);
			if (t != n && nTypes == types) {
				nTypes = Arrays.copyOf(types, types.length);
			}
			nTypes[i] = n;
		}
		return nTypes;
	}
}
