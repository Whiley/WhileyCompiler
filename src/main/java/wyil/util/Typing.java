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

import java.util.function.BiFunction;
import java.util.function.Function;

import wybs.lang.Build;
import wybs.util.AbstractCompilationUnit.Tuple;
import wycc.util.Pair;
import wyil.lang.WyilFile.Expr;
import wyil.lang.WyilFile.Type;
import wyil.util.SubtypeOperator.LifetimeRelation;


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
public interface Typing {
	/**
	 * Return flag indicating whether there are no valid typings remaining, or not.
	 * This is equivalent to <code>height() == 0</code>.
	 *
	 * @return
	 */
	public boolean empty();

	/**
	 * Get the number of type variables managed directly by this typing, as
	 * determined by the number of expressions. Observe that individual environments
	 * may have more variables to handle special cases.
	 *
	 * @return
	 */
	public int width();

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
	 * Get the ith environment associated with this constraint set.
	 *
	 * @param ith
	 * @return
	 */
	public Typing.Environment getEnvironment(int ith);

	/**
	 * Get the ith expression associated with this typing.
	 *
	 * @param ith
	 * @return
	 */
	public Expr getExpression(int ith);

	/**
	 * Determine the type variable index associated with a given expression. This is
	 * based on the underlying schema originally provided.
	 *
	 * @param expression
	 * @return
	 */
	public int indexOf(Expr expression);

	/**
	 * Determine the type variables associated with zero or more expressions. This is
	 * based on the underlying schema originally provided.
	 *
	 * @param expression
	 * @return
	 */
	public int[] indexOf(Tuple<Expr> expressions);

	/**
	 * Get the types associated with a given expression
	 *
	 * @param expression
	 * @return
	 */
	public Type[] types(Expr expression);

	/**
	 * Get the types associated with a given type variable.
	 *
	 * @param variable
	 * @return
	 */
	public Type[] types(int variable);

	/**
	 * Invalidate this typing entirely.
	 *
	 * @return
	 */
	public Typing invalidate();

	/**
	 * Attempt to resolve all outstanding existential types into concrete types.
	 *
	 * @return
	 */
	public Typing concretise();

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
	public Typing map(Function<Typing.Environment,Typing.Environment> fn);

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
	public Typing project(Function<Typing.Environment, Typing.Environment[]> fn);

	/**
	 * Provides an environment for working with types where various operations are
	 * guaranteed to make sense (e.g. subtyping). The environment is responsible for
	 * managing all existential variables, etc.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Environment extends LifetimeRelation {
		/**
		 * Return the "width" of this environment (i.e. the number of allocated
		 * variables).
		 *
		 * @return
		 */
		public int size();

		/**
		 * Get the type associated with a given variable.
		 *
		 * @param variable
		 * @return
		 */
		public Type get(int variable);

		/**
		 * Get the types associated with zero or more variables in this environment.
		 *
		 * @param variables
		 * @return
		 */
		public Type[] getAll(int... variables);

		/**
		 * Bind two types together in this environment assuming the <code>lhs</code> is
		 * the supertype of the <code>rhs</code>. For ease of use in the type checker,
		 * we permit either lhs or rhs to be null and, in such case, an empty array of
		 * environments is returned.
		 *
		 * @param lhs The candidate "supertype". That is, lhs's raw type may be a
		 *            supertype of <code>rhs</code>'s raw type.
		 * @param rhs The candidate "subtype". That is, rhs's raw type may be a subtype
		 *            of <code>lhs</code>'s raw type.
		 * @return
		 */
		public Environment bind(Type lhs, Type rhs);

		/**
		 * Attempt to resolve all outstanding existential types into concrete types.
		 *
		 * @return
		 */
		public Environment[] concretise();

		/**
		 * Update the type of a given variable in the environment. Observe that if the
		 * type is <code>null</code> then resulting <code>Environment</code> is invalid
		 * and<code>null</code> is returned.
		 *
		 * @param variable The type variable's index.
		 * @param type     The type being assigned which maybe <code>null</code>.
		 * @return
		 */
		public Environment set(int variable, Type type);

		/**
		 * Allocate a given number of existential type variables for use within this
		 * environment.
		 *
		 * @param n
		 * @return
		 */
		public Pair<Environment,Type.ExistentialVariable[]> allocate(int n);
	}

	/**
	 * Create a relaxed typing system which initially consists of a single, empty,
	 * environment.
	 *
	 * @param expression
	 * @return
	 */
	public static Typing Relaxed(Expr expression, AbstractSubtypeOperator subtyping, LifetimeRelation lifetimes,
			Build.Meter meter) {
		Expr[] schema = AbstractTyping.flattern(expression, meter);
		return new AbstractTyping(schema,
				new AbstractTyping.Environment(subtyping, lifetimes, subtyping.TOP, 0));
	}
}
