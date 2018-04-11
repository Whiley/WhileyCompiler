package wyil.type.subtyping;

import wybs.lang.NameResolver.ResolutionError;

public interface EmptinessTest<T> {
	/**
	 * <p>
	 * Check whether the intersection of two types is equivalent to
	 * <code>void</code> or not. The complexities of Whiley's type system mean that
	 * this is not always obvious. For example, the type <code>int&(!int)</code> is
	 * equivalent to <code>void</code>.
	 * </p>
	 *
	 * @param lhs
	 *            The first type being tested to see whether or not it is equivalent
	 *            to void.
	 * @param lhsState
	 *            The state information for the lhs type
	 * @param rhs
	 *            The second type being tested to see whether or not it is
	 *            equivalent to void.
	 * @param rhsState
	 *            The state information for the rhs type
	 * @param lifetimes
	 *            The within relation between lifetimes that should be used when
	 *            determine whether the <code>rhs</code> is a subtype of the
	 *            <code>lhs</code>.
	 *
	 * @return
	 * @throws ResolutionError
	 */
	public boolean isVoid(T lhs, State lhsState, T rhs, State rhsState, LifetimeRelation lifetimes) throws ResolutionError;

	public static State PositiveMax = new State(true,true);
	public static State PositiveMin = new State(true,true);
	public static State NegativeMax = new State(false,true);
	public static State NegativeMin = new State(false,true);

	/**
	 * Emptiness State encapsulates important information about the types in
	 * question.
	 *
	 * @author David J. Pearce
	 *
	 */
	public final static class State {
		public final boolean sign;
		public final boolean maximise;

		public State(boolean sign, boolean maximise) {
			this.sign = sign;
			this.maximise = maximise;
		}

		public State invert() {
			return toState(!sign,maximise);
		}

		@Override
		public String toString() {
			String r = sign ? "+" : "-";
			return r + (maximise ? "^" : "_");
		}
	}

	public static State toState(boolean sign, boolean maximise) {
		if(sign) {
			return maximise ? NegativeMax : NegativeMin;
		} else {
			return maximise ? PositiveMax : PositiveMin;
		}
	}

	/**
	 * <p>
	 * A lifetime relation determines, for any two lifetimes <code>l</code> and
	 * <code>m</code>, whether <code>l</code> is contained within <code>m</code> or
	 * not. This information is critical for subtype checking of reference types.
	 * Consider this minimal example:
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
	 * @author David J. Pearce
	 *
	 */
	public interface LifetimeRelation {
		/**
		 * Determine whether one lifetime is contained entirely within another. This is
		 * the critical test for ensuring sound subtyping between references.
		 * Specifically, an assignment <code>&l:T p = q</code> is only considered safe
		 * if it can be shown that the lifetime of the cell referred to by
		 * <code>p</code> is <i>within</i> that of <code>q</code>.
		 *
		 * @param outer
		 * @param inner
		 * @return
		 */
		public boolean isWithin(String inner, String outer);
	}
}
