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
// WITHOUT WARRANTIES OR CONDITIONS OF Type.Selector.TOP KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wyil.util;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import wybs.lang.SyntacticHeap;
import wycc.util.ArrayUtils;
import wyil.check.FlowTypeUtils.*;
import wyil.lang.WyilFile.*;
import wyil.util.SubtypeOperator.LifetimeRelation;

public class FlowTyping {

	/**
	 * Represents a set of constraints over the return types of a set of
	 * expressions.
	 *
	 * @author David J Pearce
	 *
	 */
	public static interface Constraints {
		/**
		 * Return the number of valid typings. If this is zero, then there are no valid
		 * typings and the original expression cannot be typed. On the other hand, if
		 * there is more than one valid typing (at the end) then the original expression
		 * is ambiguous.
		 *
		 * @return
		 */
		public int size();

		/**
		 * Access the last variable on the stack.
		 *
		 * @return
		 */
		public Variable top();

		/**
		 * Get the number of type variables managed by this typing. This is largely
		 * determined by the size of the expression being typed.
		 *
		 * @return
		 */
		public int width();

		/**
		 * Get the types associated with a given type variable
		 *
		 * @param var
		 * @return
		 */
		public Type[] types(Variable var);

		/**
		 * Create a new type variable with a given initial type. The index of this
		 * variable is given by <code>last()</code>. If the initial type is
		 * <code>null</code>, then all rows are invalidated.
		 *
		 * @return
		 */
		public Constraints add(Expr expression, Type type);

		/**
		 * Create a new type variable as a projection of another variable. The index of
		 * this variable is given by <code>last()</code>. The projection function
		 * <code>fn</code> may return <code>null</code> to indicate no projection is
		 * possible for the source on this row (which, in turn, invalidates that row).
		 *
		 * @param kind
		 * @param fn
		 * @param child
		 * @return
		 */
		public Constraints project(Function<Type, Type> fn, Expr expr, Variable src);

		/**
		 * Create a new variable as a projection of zero or more other variables. The
		 * index of this variable is given by <code>last()</code>. The projection
		 * function <code>fn</code> may return <code>null</code> to indicate no
		 * projection is possible from source(s) on this the row (which, in turn,
		 * invalidates that row).
		 *
		 * @param fn
		 * @param children
		 * @return
		 */
		public Constraints project(Function<Type[], Type> fn, Expr expr, Variable... children);

		/**
		 * Constrain the type of the given expression to <i>at most</i> a given type.
		 * Thus, the type of the expression will not be above this type. For example, if
		 * we constrain an expression to the type <code>int|null</code>, then it cannot
		 * take the type <code>int|null|bool</code> (because this is not at most type
		 * <code>int|null</code>).
		 *
		 * @param upperBound the constraint being applied
		 * @param variable   the type variable being constrained.
		 * @return
		 */
		public Constraints constrain(Type upperBound, Variable variable);

		/**
		 * Apply this typing to the underlying expressions as best as possible. If we
		 * have a valid typing, then everything will work out. Otherwise, we'll do our
		 * best to type things consistently but anything which cannot be typed remains
		 * void.
		 */
		public void apply();
	}


	/**
	 * Represents a variable whose type we are attempting to determine. In most
	 * cases, this corresponds to the return type of some expression.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Variable {
		/**
		 * Index of this variable in typing.
		 */
		private final int index;

		public Variable(int index) {
			this.index = index;
		}

		@Override
		public String toString() {
			return "#" + index;
		}
	}


	private static abstract class AbstractTyping implements FlowTyping.Constraints {
		protected final int size;

		public AbstractTyping(int size) {
			this.size = size;
		}

		@Override
		public FlowTyping.Variable top() {
			return new Variable(size - 1);
		}

		@Override
		public int width() {
			return size;
		}
	}

	public static class TypeCombinator extends AbstractTyping implements FlowTyping.Constraints {
		private final Expr target;
		private final Decl.Callable[] candidates;
		private final Constraints[] forks;

		public TypeCombinator(Expr.Invoke src, Constraints child, Variable... args) {
			this(src,child,args,src.getLink().getCandidates());
		}

		public TypeCombinator(Expr.LambdaAccess src, Constraints child, Variable... args) {
			this(src,child,args,src.getLink().getCandidates());
		}

		public TypeCombinator(Expr src, Constraints child, Variable[] args, List<Decl.Callable> candidates) {
			super(child.width()+1);
			this.target = src;
			// Initialise empty fork array
			this.candidates = trimCandidates(args.length, candidates);
			this.forks = new Constraints[this.candidates.length];
			// Configure forks
			for (int i = 0; i != forks.length; ++i) {
				Type.Callable c = this.candidates[i].getType();
				Constraints t = child;
				for (int j = 0; j != args.length; ++j) {
					t = t.constrain(c.getParameter().dimension(j), args[j]);
				}
				forks[i] = t.add(src, c.getReturn());
			}
		}

		private TypeCombinator(TypeCombinator other, Constraints[] forks) {
			// FIXME: use of width() here seems less than ideal
			super(forks.length == 0 ? 0 : forks[0].width());
			this.target = other.target;
			this.candidates = other.candidates;
			this.forks = forks;
		}

		public Decl.Link<Decl.Callable> getLink() {
			if(target instanceof Expr.Invoke) {
				return ((Expr.Invoke)target).getLink();
			} else {
				return ((Expr.LambdaAccess)target).getLink();
			}
		}

		public Constraints get(int ith) {
			return forks[ith];
		}

		public int height() {
			return forks.length;
		}

		@Override
		public int size() {
			int r = 0;
			for(int i=0;i!=forks.length;++i) {
				r = r + forks[i].size();
			}
			return r;
		}

		@Override
		public Type[] types(FlowTyping.Variable var) {
			Type[] types = new Type[0];
			for(int i=0;i!=forks.length;++i) {
				types = ArrayUtils.append(types,  forks[i].types(var));
			}
			return ArrayUtils.removeAll(types,null);
		}

		@Override
		public Constraints add(Expr expression, Type type) {
			Constraints[] nForks = new Constraints[forks.length];
			for (int i = 0; i != forks.length; ++i) {
				nForks[i] = forks[i].add(expression, type);
			}
			return new TypeCombinator(this, nForks);
		}

		@Override
		public Constraints project(Function<Type, Type> fn, Expr expr, Variable src) {
			Constraints[] nForks = new Constraints[forks.length];
			for (int i = 0; i != forks.length; ++i) {
				nForks[i] = forks[i].project(fn, expr, src);
			}
			return new TypeCombinator(this, nForks);
		}

		@Override
		public Constraints constrain(Type upperBound, Variable variable) {
			Constraints[] nForks = new Constraints[forks.length];
			for (int i = 0; i != forks.length; ++i) {
				nForks[i] = forks[i].constrain(upperBound, variable);
			}
			return new TypeCombinator(this, nForks);
		}

		@Override
		public Constraints project(Function<Type[], Type> fn, Expr expr, Variable... children) {
			Constraints[] nForks = new Constraints[forks.length];
			for (int i = 0; i != forks.length; ++i) {
				nForks[i] = forks[i].project(fn, expr, children);
			}
			return new TypeCombinator(this, nForks);
		}

		@Override
		public void apply() {
			for (int i = 0; i != forks.length; ++i) {
				Constraints fork = forks[i];
				if (fork.size() == 1) {
					fork.apply();
					Decl.Link<Decl.Callable> link;
					// TODO: this is a little ugly
					if (target instanceof Expr.Invoke) {
						link = ((Expr.Invoke) target).getLink();
					} else {
						link = ((Expr.LambdaAccess) target).getLink();
					}
					link.resolve(candidates[i]);
					break;
				}
			}
		}

		@Override
		public String toString() {
			String r = "";
			for (int i = 0; i != forks.length; ++i) {
				if (i != 0) {
					r += "+";
				}
				r = r + forks[i].toString();
			}
			return r;
		}

		private static Decl.Callable[] trimCandidates(int n, List<Decl.Callable> candidates) {
			Decl.Callable[] cs = candidates.toArray(new Decl.Callable[candidates.size()]);
			for(int i=0;i!=cs.length;++i) {
				if(cs[i].getParameters().size() != n) {
					cs[i] = null;
				}
			}
			return ArrayUtils.removeAll(cs, null);
		}
	}

	/**
	 * Represents a set of constraints over the return types of a set of
	 * expressions.
	 *
	 * @author David J Pearce
	 *
	 */
	public static class TypeSequence extends AbstractTyping implements FlowTyping.Constraints {
		/**
		 * Subtyping
		 */
		private final SubtypeOperator subtyping;
		private final LifetimeRelation lifetimes;
		/**
		 * The master list of expressions being typed
		 */
		private final Expr[] expressions;
		/**
		 * Matrix of current typing under consideration.
		 */
		private final Type[] typing;
		/**
		 * Indicates whether typing invalid or not
		 */
		private final boolean empty;

		public TypeSequence(SubtypeOperator subtyping, LifetimeRelation lifetimes) {
			super(0);
			this.subtyping = subtyping;
			this.lifetimes = lifetimes;
			// Assume initial size which is sufficient on average
			this.expressions = new Expr[10];
			// Initially, assume only one typing.
			this.typing = new Type[10];
			this.empty = false;
		}

		private TypeSequence(TypeSequence other, int size, boolean empty) {
			super(size);
			this.subtyping = other.subtyping;
			this.lifetimes = other.lifetimes;
			// Copy expressions over (lazily)
			if (other.expressions.length > size) {
				this.expressions = other.expressions;
			} else {
				this.expressions = Arrays.copyOf(other.expressions, size * 2);
			}
			// Copy typing over
			this.typing = Arrays.copyOf(other.typing, size);
			this.empty = empty;
		}

		@Override
		public Type[] types(FlowTyping.Variable var) {
			return new Type[] { typing[var.index] };
		}

		@Override
		public int size() {
			return empty ? 0 : 1;
		}

		@Override
		public TypeSequence add(Expr expression, Type type) {
			int index = size;
			// Create new typing to ensure functional semantics
			TypeSequence n = new TypeSequence(this, size+1, empty);
			// Allocate new variable
			n.expressions[index] = expression;
			// Assign initial type
			n.typing[index] = type;
			//
			return n;
		}

		@Override
		public Constraints project(Function<Type, Type> fn, Expr expr, FlowTyping.Variable src) {
			// Determine source index
			int from = src.index;
			// Apply the projection
			Type t = typing[from];
			Type k = (t == null) ? null : fn.apply(t);
			//
			if(k != null) {
				return add(expr,k);
			} else {
				// Done!
				TypeSequence n = new TypeSequence(this, size + 1, true);
				// Allocate new variable
				n.expressions[size] = expr;
				//
				return n;
			}
		}

		@Override
		public Constraints project(Function<Type[], Type> fn, Expr expr, FlowTyping.Variable... children) {
			// Create fresh variable
			Type[] ts = new Type[children.length];
			for (int j = 0; j != children.length; ++j) {
				Type jth = typing[children[j].index];
				ts[j] = jth;
			}
			// Apply the project only if have successfully typed all children
			if(ArrayUtils.firstIndexOf(ts, null) < 0) {
				return add(expr,fn.apply(ts));
			} else {
				// Done!
				TypeSequence n = new TypeSequence(this, size+1, empty);
				// Allocate new variable
				n.expressions[size] = expr;
				//
				return n;
			}
		}

		@Override
		public Constraints constrain(Type upperBound, FlowTyping.Variable variable) {
			int index = variable.index;
			// Check whether type will be invalidated
			Type t = typing[index];
			if (t != null && !subtyping.isSubtype(upperBound, t, lifetimes)) {
				// Yes, type is invalid.  Therefore, committed to failure
				TypeSequence n = new TypeSequence(this, size, true);
				// Done
				return n;
			} else {
				// No change
				return this;
			}
		}

		@Override
		public void apply() {
			// NOTE: At the moment, we only type expressions for the case that we have a
			// single valid typing. However, we could better in other cases by typing all
			// expressions which have consistent types across typings.
			if (!empty) {
				// Extract enclosing heap
				SyntacticHeap heap = expressions[0].getHeap();
				// Type all expressions
				for (int i = 0; i != size; ++i) {
					Expr e = expressions[i];
					Type t = typing[i];
					if (e != null && t != null) {
						if (!(e instanceof Expr.Invoke) && !(e instanceof Expr.LambdaAccess)) {
							e.setType(heap.allocate(t));
						}
					}
				}
			}
		}

		@Override
		public String toString() {
			String r = empty ? "!{" : "{";
			for (int i = 0; i != size; ++i) {
				if (i != 0) {
					r = r + ", ";
				}
				Type type = typing[i];
				if (type == null) {
					r += "#" + i;
				} else {
					r += "#" + i + ":" + type;
				}
			}
			return r + "}";
		}
	}
}
