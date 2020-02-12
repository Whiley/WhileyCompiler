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

import java.util.*;
import java.util.function.Function;

import wybs.lang.Build;
import wybs.lang.SyntacticItem;
import wybs.util.AbstractCompilationUnit.Tuple;
import wycc.util.ArrayUtils;
import wycc.util.Pair;
import wyil.check.FlowTypeUtils.Binding;
import wyil.lang.WyilFile.*;
import wyil.lang.WyilFile.Decl.Link;

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

	public List<Binding> bindings(Expr.Invoke expression);

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
	public interface Environment {
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
	public static Typing Relaxed(Expr expression, Subtyping.AbstractEnvironment subtyping, Build.Meter meter) {
		Expr[] schema = AbstractTyping.flattern(expression, meter);
		return new AbstractTyping(schema, new AbstractTyping.Environment(subtyping, subtyping.TOP, 0));
	}

	/**
	 * Provides a straightforward implementation of <code>Typing</code>.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class AbstractTyping implements Typing {
		/**
		 * The sequence expressions being being typed.
		 */
		private final Expr[] schema;
		/**
		 * Provides mapping from expressions to type variables.
		 */
		private final IdentityHashMap<Expr, Integer> mapping;
		/**
		 * Square matrix of typing environments
		 */
		private final Typing.Environment[] environments;

		public AbstractTyping(Expr[] schema, Typing.Environment... environments) {
			this.schema = schema;
			this.mapping = new IdentityHashMap<>();
			for (int i = 0, j = 0; i != schema.length; ++i) {
				mapping.put(schema[i], j);
				j = j + typingWidth(schema[i]) + 1;
			}
			this.environments = environments;
		}

		protected AbstractTyping(Expr[] schema, IdentityHashMap<Expr, Integer> mapping,
				Typing.Environment[] environments) {
			this.schema = schema;
			this.mapping = mapping;
			this.environments = environments;
		}

		/**
		 * Return flag indicating whether there are no valid typings remaining, or not.
		 * This is equivalent to <code>height() == 0</code>.
		 *
		 * @return
		 */
		@Override
		public boolean empty() {
			return environments.length == 0;
		}

		/**
		 * Get the number of type variables managed by this typing. This is largely
		 * determined by the size of the expression being typed.
		 *
		 * @return
		 */
		@Override
		public int width() {
			return schema.length;
		}

		/**
		 * Return the number of typings that remain under consideration. If this is
		 * zero, then there are no valid typings and the original expression cannot be
		 * typed. On the other hand, if there is more than one valid typing (at the end)
		 * then the original expression is ambiguous.
		 *
		 * @return
		 */
		@Override
		public int height() {
			return environments.length;
		}

		/**
		 * Get the ith environment associated with this constraint set.
		 *
		 * @param ith
		 * @return
		 */
		@Override
		public Typing.Environment getEnvironment(int ith) {
			return environments[ith];
		}

		@Override
		public Expr getExpression(int ith) {
			return schema[ith];
		}

		@Override
		public Type[] types(Expr expression) {
			return types(mapping.get(expression));
		}

		@Override
		public Type[] types(int var) {
			// Extract all types for given variable
			Type[] types = new Type[environments.length];
			for (int i = 0; i != types.length; ++i) {
				types[i] = environments[i].get(var);
			}
			// Done
			return types;
		}

		/**
		 * For a given pivot (e.g. invocation) and typing, extract the set of possible
		 * bindings to select from. This is done by taking the given binding from each
		 * row in the typing.
		 *
		 * @param expr
		 * @param typing
		 * @return
		 */
		@Override
		public List<Binding> bindings(Expr.Invoke expr) {
			Link<Decl.Callable> link = expr.getLink();
			int v_expr = indexOf(expr);
			int v_signature = v_expr + 1;
			int v_concrete = v_expr + 2;
			int v_template = v_expr + 3;
			//
			ArrayList<Binding> candidates = new ArrayList<>();
			for (int i = 0; i != height(); ++i) {
				// Identify signature this row corresponds with.
				Typing.Environment ith = getEnvironment(i);
				Type.Callable type = (Type.Callable) ith.get(v_signature);
				Type.Callable concrete = (Type.Callable) ith.get(v_concrete);
				Type template = ith.get(v_template);
				// Identify corresponding declaration.
				Decl.Callable decl = link.lookup(type);
				// Determine whether arguments need to be inferred?
				Tuple<SyntacticItem> arguments = expr.getBinding().getArguments();
				// Extract inferred arguments (if applicable)
				if (arguments.size() == 0 && template.shape() > 0) {
					if (template.shape() == 1) {
						arguments = new Tuple<>(template);
					} else {
						arguments = new Tuple<>(template.getAll());
					}
				}
				// Done
				candidates.add(new Binding(decl, arguments, concrete));
			}
			//
			return candidates;
		}

		@Override
		public int indexOf(Expr expression) {
			return mapping.get(expression);
		}

		@Override
		public int[] indexOf(Tuple<Expr> expressions) {
			int[] indices = new int[expressions.size()];
			for (int i = 0; i != indices.length; ++i) {
				indices[i] = mapping.get(expressions.get(i));
			}
			return indices;
		}

		/**
		 * Invalidate all rows in this constraints set.
		 *
		 * @return
		 */
		@Override
		public Typing invalidate() {
			return copy();
		}

		@Override
		public Typing concretise() {
			return project(row -> row.concretise());
		}

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
		@Override
		public Typing map(Function<Typing.Environment, Typing.Environment> fn) {
			Typing.Environment[] ntypes = environments;
			//
			for (int i = 0; i < environments.length; i = i + 1) {
				final Typing.Environment before = ntypes[i];
				Typing.Environment after = fn.apply(before);
				if (before == after) {
					// No change, so do nothing
					continue;
				} else if (ntypes == environments) {
					// something changed, so clone
					ntypes = Arrays.copyOf(environments, environments.length);
				}
				ntypes[i] = after;
			}
			// Remove all invalid rows
			return copy(ArrayUtils.removeAll(ntypes, null));
		}

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
		@Override
		public Typing project(Function<Typing.Environment, Typing.Environment[]> fn) {
			ArrayList<Typing.Environment> ntypes = new ArrayList<>();
			//
			for (int i = 0; i < environments.length; i = i + 1) {
				final Typing.Environment before = environments[i];
				Typing.Environment[] after = fn.apply(before);
				for (int j = 0; j != after.length; ++j) {
					ntypes.add(after[j]);
				}
			}
			// Remove all invalid rows
			return copy(ntypes.toArray(new Typing.Environment[ntypes.size()]));
		}

		@Override
		public String toString() {
			if (environments.length == 0) {
				return "_|_";
			} else {
				String r = "";
				for (Typing.Environment row : environments) {
					r = r + row;
				}
				return r;
			}
		}

		/**
		 * A simple mechanism to clone typings whilst preserving their top-level
		 * identity.
		 *
		 * @param environtments
		 * @return
		 */
		protected Typing copy(Typing.Environment... environments) {
			return new AbstractTyping(schema, mapping, environments);
		}

		// ============================================================================================================
		// Environment
		// ============================================================================================================

		public static class Environment implements Typing.Environment {
			/**
			 * Provides algorithm for subtyping (to be deprecated).
			 */
			private final Subtyping.AbstractEnvironment subtyping;
			/**
			 * Active Typing Constraints
			 */
			private final Subtyping.AbstractEnvironment.AbstractConstraints constraints;
			/**
			 * Mapping of type variable indices to their current types.
			 */
			private final Type[] types;
			/**
			 * Counts the number of existentials currently allocated for use with this
			 * environment.
			 */
			private final int existentials;

			public Environment(Subtyping.AbstractEnvironment subtyping,
					Subtyping.AbstractEnvironment.AbstractConstraints constraints, int n, Type... types) {
				if (constraints == null) {
					throw new IllegalArgumentException();
				}
				this.subtyping = subtyping;
				this.constraints = constraints;
				this.existentials = n;
				this.types = types;
			}

			/**
			 * Return the "width" of this environment (i.e. the number of allocated
			 * variables).
			 *
			 * @return
			 */
			@Override
			public int size() {
				return types.length;
			}

			/**
			 * Get the type associated with a given variable.
			 *
			 * @param variable
			 * @return
			 */
			@Override
			public Type get(int variable) {
				return types[variable];
			}

			/**
			 * Get the types associated with zero or more variables in this environment.
			 *
			 * @param variables
			 * @return
			 */
			@Override
			public Type[] getAll(int... variables) {
				Type[] ts = new Type[variables.length];
				for (int i = 0; i != ts.length; ++i) {
					ts[i] = types[variables[i]];
				}
				return ts;
			}

			@Override
			public Environment bind(Type lhs, Type rhs) {
				Subtyping.AbstractEnvironment.AbstractConstraints cs = subtyping.isSubtype(lhs, rhs);
				// Intersect with our constraints
				cs = constraints.intersect(cs);
				// Sanity check whether subtyping possible
				if (cs.isEmpty() || rhs instanceof Type.Void) {
					// NOTE: check against void above is required to protect against "void flows".
					// That is where a function with no return type is e.g. assigned to a variable.
					// Such void flows passed subtyping as above, but are non-sensical.
					return null;
				} else {
					return new Environment(subtyping, cs, existentials, types);
				}
			}

			@Override
			public Environment[] concretise() {
				if (constraints.size() == 0) {
					// No constraints over this environment, hence nothing to do.
					return new Environment[] { this };
				} else {
					Type[][] solutions = constraints.solve(existentials);
					Environment[] nenvs = new Environment[solutions.length];
					// Apply substitution to every winner
					for (int i = 0; i != nenvs.length; ++i) {
						final Type[] ith = solutions[i];
						// Creating the necessary binding function for substitution
						Function<Object, SyntacticItem> binder = o -> o instanceof Integer ? ith[(Integer) o] : null;
						// Apply the substitution
						Type[] nTypes = substitute(types, binder);
						//
						nenvs[i] = new Environment(subtyping, subtyping.TOP, 0, nTypes);
					}
					// Done
					return nenvs;
				}
			}

			@Override
			public Environment set(int variable, Type type) {
				if (type == null) {
					// Any attempt to assign a variable a null type always invalidates the
					// environment.
					return null;
				} else if (variable < types.length && types[variable].equals(type)) {
					// No change
					return this;
				} else {
					// Change
					int length = Math.max(variable + 1, types.length);
					Type[] ntypes = Arrays.copyOf(types, length);
					for (int i = types.length; i < length; ++i) {
						ntypes[i] = Type.Void;
					}
					ntypes[variable] = type;
					return new Environment(subtyping, constraints, existentials, ntypes);
				}
			}

			@Override
			public Pair<Typing.Environment, Type.ExistentialVariable[]> allocate(int n) {
				Environment nenv = new Environment(subtyping, constraints, existentials + n, types);
				Type.ExistentialVariable[] vars = new Type.ExistentialVariable[n];
				for (int i = 0; i != n; ++i) {
					vars[i] = new Type.ExistentialVariable(existentials + i);
				}
				return new Pair<>(nenv, vars);
			}

			@Override
			public boolean equals(Object o) {
				if (o instanceof Environment) {
					Environment e = (Environment) o;
					return Arrays.equals(types, e.types);
				} else {
					return false;
				}
			}

			@Override
			public int hashCode() {
				return Arrays.hashCode(types);
			}

			@Override
			public String toString() {
				String body = "";
				for (int i = 0; i != types.length; ++i) {
					if (i != 0) {
						body = body + ", ";
					}
					Type t = types[i];
					if (t == null) {
						body += "#" + i;
					} else {
						body += "#" + i + ":=" + t;
					}
				}
				return "[" + body + "]" + constraints;
			}
		}

		// ==========================================================================================
		// Helpers
		// ==========================================================================================

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

		/**
		 * Flattern an expression tree into a left-to-right linear sequence. For
		 * example, <code>x+1</code> becomes <code>[x,1,+]</code>, etc.
		 *
		 * @param e
		 * @return
		 */
		public static Expr[] flattern(Expr e, Build.Meter meter) {
			ArrayList<Expr> rs = new ArrayList<>();
			new AbstractVisitor(meter) {
				@Override
				public void visitDeclaration(Decl d) {
				}

				@Override
				public void visitStatement(Stmt s) {
				}

				@Override
				public void visitType(Type t) {
				}

				@Override
				public void visitLambda(Decl.Lambda l) {
				}

				@Override
				public void visitLogicalNot(Expr.LogicalNot e) {
				}

				@Override
				public void visitLogicalOr(Expr.LogicalOr e) {
				}

				@Override
				public void visitLogicalAnd(Expr.LogicalAnd e) {
				}

				@Override
				public void visitLogicalIff(Expr.LogicalIff e) {
				}

				@Override
				public void visitLogicalImplication(Expr.LogicalImplication e) {
				}

				@Override
				public void visitUniversalQuantifier(Expr.UniversalQuantifier e) {
				}

				@Override
				public void visitExistentialQuantifier(Expr.ExistentialQuantifier e) {
				}

				@Override
				public void visitExpression(Expr e) {
					super.visitExpression(e);
					rs.add(e);
				};
			}.visitExpression(e);
			//
			return rs.toArray(new Expr[rs.size()]);
		}

		/**
		 * Determine the "typing width" of an expression. This is the number of type
		 * variables required for representing this expression in a given typing. Only
		 * invocations can be represented by more than one typing variable, as these
		 * must account for the selected candidate and any template arguments being
		 * inferred.
		 *
		 * @param e
		 * @return
		 */
		public static int typingWidth(Expr e) {
			// NOTE: performing this calculation over and over is costly, and some kind of
			// caching system could help.
			if (e instanceof Expr.Invoke) {
				// Every invoke expression is represented requires two meta variables.
				// The first holds the candidate type which is used to identify the
				// corresponding declaration candidate. The second is the concrete type which is
				// used to handle parameter binding. After this we have zero or more variables
				// to handle existential types when inferring template arguments.
				return 3;
			} else {
				return 0;
			}
		}
	}
}
