// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyautl.core;

import java.math.BigInteger;
import java.util.*;

import wyautl.util.BigRational;
import wyautl.util.BinaryMatrix;

/**
 * <p>
 * A finite-state automaton designed specifically for representing recursive
 * structural types and verification conditions. An <code>Automaton</code> is a
 * directed graph whose nodes and edges are referred to as <i>states</i> and
 * <i>transitions</i>. There are three difference kinds of state supported:
 * </p>
 * <ul>
 * <li>
 * <p>
 * <b>Constants.</b> These states have no children and represent constant values
 * such as integers, booleans and strings.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>Collections.</b> These states have 0 or more children and represent
 * collections of objects. There are three kinds of collection: <i>sets</i>,
 * <i>bags</i> and <i>lists</i>. A set maintains its children in sorted order
 * and eliminated duplicates; a bag simple maintains sorted order; finally, a
 * list maintains the given order.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>Terms.</b> These represents the user-defined terms of the given rewrite
 * system. Every term has a unique name and an optional child.
 * </p>
 * </li>
 * </ul>
 * <h3>Example</h3>
 * <p>
 * As an example, consider the following textual description of an language for
 * representing a simple structural type system:
 * </p>
 * 
 * <pre>
 * term Void  // void type
 * term Bool  // bool type
 * term Int   // int type
 * 
 * term Not(Type) // negation type
 * term Or{Type...} // union of zero or more types
 * term And{Type...} // intersection of zero or more types
 * 
 * define Type as Void | Bool | Int | Not | Or | And
 * </pre>
 * <p>
 * In this simple language, we can express types such as the following:
 * <ul>
 * <li><code>Not(Or{Int,Bool})</code> --- the set of values excluding the
 * <i>integers</i> and <i>booleans</i></li>
 * <li><code>And{Int,Bool}</code> --- the set of values in both the
 * <i>integers</i> and <i>booleans</i> (i.e. the empty set).</li>
 * </ul>
 * We can also see how the various components correspond to states in an
 * automaton. Consider the following examples:
 * <ul>
 * <li><code>Not(Void)</code> --- this corresponds to an automaton with two
 * states: 1) a <i>term</i> with no child representing <code>Void</code>; 2) a
 * <i>term</i> representing <code>Not</code> which has a single child referring
 * to state 1.</li>
 * <li><code>Or{Int,Bool}</code> --- corresponds to an automaton with four
 * states: 1) a <i>term</i> with no child representing <code>Int</code>; 2) a
 * <i>term</i> with no child representing <code>Bool</code>; 3) a <i>set</i>
 * with two children referring to states 1 and 2; 4) a term representing
 * <code>Or</code> with a single child referring to state 3.</li>
 * </ul>
 * </p>
 * <h3>Notes</h3>
 * <ul>
 * <li>
 * <p>
 * <b>Roots.</b> States can be explicitly marked as <i>roots</i> to provide a
 * way to track them through the various operations that might be performed on
 * an automaton. In particular, as states are rewritten, the roots will be
 * updated accordingly.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>Minimisation.</b> An automaton which does not contain garbage and has the
 * <i>strong equivalence property</i> is said to be <i>minimised</i>. Automata
 * are generally kept in the minimised form, and only use of the
 * <code>set()</code> method can break this. Garbage states are those not
 * reachable from any marked root state. The strong equivalence property
 * guarantees that there are no two distinct, but equivalent states. In order to
 * restore this property, the <code>minimise()</code> function must be called
 * explicitly.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>Canonical Form.</b> An automaton which is minimised is not guaranteed to
 * be <i>canonical</i>. This means we can have automata which are effectively
 * equivalent, but which not considered identical (i.e., where
 * <code>equals()</code> returns false). In some circumstance, it is desirable
 * to move an automaton into canonical form, and this can be achieved with the
 * <code>canonicalise()</code> function.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>Virtual States.</b> In the internal representation of automata, leaf
 * states may be not be represented as actual states. This will occur if the
 * leaf node does not include any supplementary data, and is primarily for space
 * and performance optimisation. In such case, the node is represented as a
 * child node using a negative index.
 * </p>
 * </li>
 * </ul>
 * 
 * @author David J. Pearce
 * 
 */
public final class Automaton {

	/**
	 * An internal configuration parameter
	 */
	private static final int DEFAULT_NUM_STATES = 4;

	/**
	 * An internal configuration parameter
	 */
	private static final int DEFAULT_NUM_ROOTS = 1;

	/**
	 * The number of used slots in the states array. It follows that
	 * <code>nStates <= states.length</code> always holds.
	 */
	private int nStates;

	/**
	 * The array of automaton states. <b>NOTES:</b> this may not contain
	 * <code>null</code> values.
	 */
	private State[] states;

	/**
	 * The number of used slots in the markers array. It follows that
	 * <code>nRoots <= roots.length</code> always holds.
	 */
	private int nRoots;

	/**
	 * The array of automaton markers.
	 */
	private int[] roots;

	public Automaton() {
		this.states = new Automaton.State[DEFAULT_NUM_STATES];
		this.roots = new int[DEFAULT_NUM_ROOTS];
	}

	public Automaton(Automaton automaton) {
		this.nStates = automaton.nStates;
		this.states = new State[automaton.states.length];
		for (int i = 0; i != states.length; ++i) {
			Automaton.State state = automaton.states[i];
			// FIXME: this check should be unnecessary
			if (state != null) {
				states[i] = state.clone();
			}
		}
		this.nRoots = automaton.nRoots;
		this.roots = Arrays.copyOf(automaton.roots, nRoots);
	}

	public Automaton(State[] states) {
		this.nStates = states.length;
		this.states = states;
		this.roots = new int[DEFAULT_NUM_ROOTS];
	}

	/**
	 * Return the number of states in this automaton.
	 * 
	 * @return
	 */
	public int nStates() {
		return nStates;
	}

	/**
	 * Return the number of states marked as being a root. Such markers provide a
	 * form of reference which can be preserved through the various operations
	 * that can be called on an automaton.
	 * 
	 * @return
	 */
	public int nRoots() {
		return nRoots;
	}

	/**
	 * Return the state at a given index into the automaton.
	 * 
	 * @param index
	 *            --- Index of state to return where
	 *            <code>0 <= index < nStates()</code>.
	 * @return
	 */
	public State get(int index) {
		if (index < 0) {
			switch (index) {
			case K_LIST:
				return EMPTY_LIST;
			case K_SET:
				return EMPTY_SET;
			case K_BAG:
				return EMPTY_BAG;
			default:
				return new Term(-index + K_FREE, K_VOID);
			}
		}

		return states[index];
	}

	/**
	 * <p>
	 * Replace the state at the given index with a new state. This can be useful
	 * for creating cyclic automata, where you first add a "dummy" state and the
	 * replace that with the real state later on.
	 * </p>
	 * <p>
	 * <b>NOTE:</b> all references valid prior to this call remain valid
	 * afterwards. However, the automaton is not guaranteed to be
	 * <i>minimised</i> afterwards (i.e. it does not retain the strong
	 * equivalence property and may contain garbage states). The
	 * <code>minimise()</code> function must be called to restore this property.
	 * </p>
	 * 
	 * @param index
	 *            --- Index of state to replace where
	 *            <code>0 <= index < nStates()</code>.
	 * @param state
	 *            --- state to replace existing state with.
	 */
	public void set(int index, State state) {
		states[index] = state;
	}

	/**
	 * <p>
	 * Add a new state into the automaton. If there is already an equivalent
	 * state, then its index is returned. Or, if the state can be represented
	 * "virtually", then a negative (but still valid) index will be returned.
	 * Otherwise, the state is added onto the end of the states array and its
	 * index is returned.
	 * </p>
	 * <p>
	 * <b>NOTE:</b> all references valid prior to this call remain valid, and
	 * the automaton remains minimised.
	 * </p>
	 * 
	 * @param state
	 *            --- automaton state to be added.
	 * @return
	 */
	public int add(Automaton.State state) {

		// First, check to see whether this state is uniquely identified by its
		// kind. In such case, we return a "virtual" node rather than actually
		// allocating a node. This is a simple optimisation designed to reduce
		// the number of allocated nodes.
		if (state instanceof Term) {
			Term term = (Term) state;
			if (term.contents == Automaton.K_VOID) {
				return K_FREE - term.kind;
			}
		} else if (state instanceof Collection) {
			Collection compound = (Collection) state;
			if (compound.length == 0) {
				return compound.kind;
			}
		}

		// Second, check to see whether there already exists an equivalent
		// state.
		for (int i = 0; i != nStates; ++i) {
			State ith = states[i];
			if (ith.equals(state)) {
				return i; // match
			}
		}

		// Finally, allocate a new state!
		return internalAdd(state);
	}

	/**
	 * <p>
	 * Copy into this automaton all states in the given automaton reachable from
	 * a given root state.
	 * </p>
	 * <p>
	 * <b>NOTE:</b> all references valid prior to this call remain valid, and
	 * the automaton remains minimised (provided it was minimised initially).
	 * </p>
	 * 
	 * @param root
	 *            --- root index to begin copying from.
	 * @param automaton
	 *            --- other automaton to copy states from (can be
	 *            <code>this</code>).
	 * @return
	 */
	public int addAll(int root, Automaton automaton) {
		if(root < 0) {
			// no need to do anything in this case.
			return root;
		} else {
			int automaton_nStates = automaton.nStates();
			int[] binding = new int[nStates + automaton_nStates];
			copy(automaton, root, binding);
			for (int i = 0; i != automaton_nStates; ++i) {
				int index = binding[i];
				if (index != K_VOID) {
					states[index].remap(binding);
				}
			}
			// map root from automaton space to this space.
			root = binding[root]; 
			// minimise the automaton to eliminate any states copied
			// over from automaton which are equivalent to existing states.
			minimise(binding);
			// map root from original location to (potentially) new location
			// after minimisation.
			return binding[root];			
		}
	}
	
	/**
	 * <p>
	 * Rewrite a state <code>s1</code> to another state <code>s2</code>. This
	 * means that all occurrences of <code>s1</code> are replaced with
	 * <code>s2</code>. In the resulting automaton, there is guaranteed to be no
	 * state equivalent to <code>s1</code>.
	 * </p>
	 * 
	 * <p>
	 * <b>NOTE:</b> all references which were valid beforehand may now be
	 * invalidated. In order to preserve a reference through a rewrite, it is
	 * necessary to use a root marker. The resulting automaton is guaranteed to
	 * remain minimised.
	 * </p>
	 * <p>
	 * <b>NOTE:</b> for various reasons, this operation does not support
	 * rewriting from a virtual node (i.e. where an index < 0). This is a minor
	 * limitation which shouldn't cause problems in the vast majority of cases.
	 * </p>
	 * 
	 * 
	 * @param from
	 *            --- (non-virtual) state being rewritten from.
	 * @param to
	 *            --- state being rewritten to.
	 * @return
	 */
	public void rewrite(int from, int to) {
		if (from < to) {
			// FIXME: The following is necessary to ensure that the node being
			// rewritten takes the lower position in the final automaton.
			// Without this, we encounter bugs with automaton equivalence after
			// reducing. It's not clear whether or not this is a general
			// solution to the problem.
			State tmp = states[from];
			states[from] = states[to];
			states[to] = tmp;
			int t = from;
			from = to;
			to = t;
		}

		if (from != to) {
			int[] map = new int[nStates];
			for (int i = 0; i != map.length; ++i) {
				map[i] = i;
			}
			map[from] = to;
			for (int i = 0; i < nStates; ++i) {
				states[i].remap(map);
			}
			// map root markers
			for (int i = 0; i != nRoots; ++i) {
				roots[i] = map[roots[i]];
			}
			compactAndMinimise(map);
		}
	}

	/**
	 * <p>
	 * Clone the source object whilst replacing all reachable instances of the
	 * search term with the replacement term. In the case of no matches, the
	 * original source term is returned.
	 * </p>
	 * 
	 * <p>
	 * <b>NOTE:</b> all references valid prior to this call remain valid, and
	 * the automaton remains minimised (provided it was initially minimised). 
	 * </p>
	 * 
	 * @param source
	 *            --- term to be cloned and within which to matching search
	 *            terms are replaced.
	 * @param search
	 *            --- term to search for within terms reachable from source.
	 * @param replacement
	 *            --- term to replace matched terms with.
	 */
	public int substitute(int source, int search, int replacement) {
		int[] binding = new int[nStates];
		if (Automata.reachable(this, source, search, binding)) {
			Arrays.fill(binding, 0);
			binding[search] = -1; // don't visit subtrees of search term
			copy(this, source, binding);
			binding[search] = replacement;
			for (int i = 0; i != binding.length; ++i) {
				int index = binding[i];
				if (index != K_VOID) {
					states[index].remap(binding);
				}
			}
			compactAndMinimise(binding);
			return binding[source];
		} else {
			return source; // no change
		}
	}

	/**
	 * <p>
	 * Return the automaton to a minimised form (i.e. where there are no
	 * distinct but equivalent states, and no garbage states).
	 * </p>
	 * <p>
	 * <b>NOTE:</b> all references valid prior to this call may be invalidated
	 * (unless the automaton was already minimised).
	 * </p>
	 */
	public void minimise() {
		compactAndMinimise(new int[nStates]);
	}

	/**
	 * <p>
	 * Convert the automaton into a canonical form. This ensures the property
	 * that any two equivalent automaton are identical when converted to
	 * canonical form. This can be useful if we want to generate a unique string
	 * identifying a given class of automata.
	 * </p>
	 * 
	 */
	public void canonicalise() {
		// FIXME: to do!
	}
	
	/**
	 * Mark a given state. This means it is treated specially, and will never be
	 * deleted from the automaton as a result of garbage collection.
	 * 
	 * @param root
	 * @return
	 */
	public void setRoot(int index, int root) {
		// First, create space if necessary
		if (index >= roots.length) {
			int[] nroots = nRoots == 0 ? new int[DEFAULT_NUM_ROOTS]
					: new int[(index + 1) * 2];
			System.arraycopy(roots, 0, nroots, 0, nRoots);
			roots = nroots;
		}
		// Second set the marker!
		roots[index] = root;
		nRoots = Math.max(index + 1, nRoots);
	}

	/**
	 * Get the given marked node.
	 * 
	 * @param index
	 * @return
	 */
	public int getRoot(int index) {
		return roots[index];
	}

	/**
	 * Determine the hashCode of an automaton.
	 */
	public int hashCode() {
		int r = 0;
		for (int i = 0; i != nStates; ++i) {
			r = r + states[i].hashCode();
		}
		return r;
	}

	/**
	 * This method compares two compound types to test whether they are
	 * <i>identical</i>. Observe that it does not perform an <i>isomorphism</i>
	 * test. Thus, two distinct types which are structurally isomorphic will
	 * <b>not</b> be considered equal under this method. <b>NOTE:</b> to test
	 * whether two types are structurally isomorphic, using the
	 * <code>isomorphic(t1,t2)</code> method.
	 */
	public boolean equals(Object o) {
		if (o instanceof Automaton) {
			Automaton a = (Automaton) o;
			State[] cs = a.states;
			if (a.nStates != nStates || a.nRoots != nRoots) {
				return false;
			}
			for (int i = 0; i != nStates; ++i) {
				State si = states[i];
				State ci = cs[i];
				if (!si.equals(ci)) {
					return false;
				}
			}
			for (int i = 0; i != nRoots; ++i) {
				if (roots[i] != a.roots[i]) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Return a simple string representation of an automaton. Generally
	 * speaking, this is only useful for debugging purposes. In order to get a
	 * nice string representation of an automaton, the
	 * <code>PrettyAutomataWriter</code> should be used.
	 */
	public String toString() {
		String r = "";
		for (int i = 0; i != nStates; ++i) {
			if (i != 0) {
				r = r + ", ";
			}
			Automaton.State state = states[i];
			r = r + "#" + i + " ";

			if (state instanceof Term) {
				Term t = (Term) state;
				if (t.contents == K_VOID) {
					r = r + t.kind;
				} else {
					r = r + t.kind + "(" + t.contents + ")";
				}
			} else if(state != null){
				r = r + state.toString();
			} else {
				r = r + "null";
			}
		}
		r = r + " <";
		for (int i = 0; i != nRoots; ++i) {
			if (i != 0) {
				r += ",";
			}
			r += roots[i];
		}
		r = r + ">";
		return r;
	}

	/**
	 * Represents an abstract state in an automaton. Each state has a kind.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static abstract class State {
		public final int kind;

		/**
		 * Construct a state of a given kind
		 * 
		 * @param kind
		 *            --- State kind (must be positive integer).
		 */
		public State(int kind) {
			this.kind = kind;
		}

		public State(State state) {
			kind = state.kind;
		}

		public abstract State clone();

		public abstract boolean remap(int[] map);

		public boolean equals(final Object o) {
			if (o instanceof State) {
				State c = (State) o;
				return kind == c.kind;
			}
			return false;
		}

		public int hashCode() {
			return kind;
		}
	}

	/**
	 * Represents a nominal object within an automaton. Such an object can only
	 * be equivalent to a Term of the same kind.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Term extends State {
		public int contents;

		public Term(int kind) {
			super(kind);
			if (kind < 0) {
				throw new IllegalArgumentException("invalid term kind (" + kind
						+ ")");
			}
			this.contents = K_VOID;
		}

		public Term(int kind, int data) {
			super(kind);
			if (kind < 0) {
				throw new IllegalArgumentException("invalid term kind (" + kind
						+ ")");
			}
			this.contents = data;
		}

		public Term clone() {
			return new Term(kind, contents);
		}

		public boolean remap(int[] map) {
			int old = contents;
			if (old >= 0) {
				contents = map[contents];
				return contents != old;
			} else {
				return false;
			}
		}

		public boolean equals(final Object o) {
			if (o instanceof Term) {
				Term t = (Term) o;
				return kind == t.kind && contents == t.contents;
			}
			return false;
		}

		public int hashCode() {
			return contents * kind;
		}

		public String toString() {
			return kind + "(" + contents + ")";
		}
	}

	/**
	 * Represents a data item within an automaton. Each item has a payload which
	 * is an object of some description. Payload objects must have appropriate
	 * <code>equals()</code> and <code>hashCode()</code> methods defined.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static abstract class Constant<T> extends State {
		public final T value;

		public Constant(int kind, T data) {
			super(kind);
			this.value = data;
		}

		public boolean remap(int[] map) {
			return false;
		}

		public boolean equals(final Object o) {
			if (o instanceof Constant) {
				Constant t = (Constant) o;
				return kind == t.kind && value.equals(t.value);
			}
			return false;
		}

		public final Constant<T> clone() {
			return this;
		}

		public int hashCode() {
			return value.hashCode() * kind;
		}

		public String toString() {
			return value.toString();
		}
	}

	public static final class Bool extends Constant<Boolean> {
		public Bool(boolean value) {
			super(K_BOOL, value);
		}
		
		public Bool invert() {
			return value ? FALSE : TRUE;
		}
	}

	public static final class Int extends Constant<BigInteger> implements
			Comparable<Int> {
		public Int(BigInteger value) {
			super(K_INT, value);
		}

		public Int(long value) {
			super(K_INT, BigInteger.valueOf(value));
		}

		public Int(String str) {
			super(K_INT, new BigInteger(str));
		}

		public int intValue() {
			return value.intValue();
		}

		public int compareTo(Int rhs) {
			return value.compareTo(rhs.value);
		}

		public Int add(Int x) {
			return new Int(value.add(x.value));
		}

		public Int subtract(Int x) {
			return new Int(value.subtract(x.value));
		}

		public Int multiply(Int x) {
			return new Int(value.multiply(x.value));
		}

		public Int divide(Int x) {
			return new Int(value.divide(x.value));
		}

		public Int negate() {
			return new Int(value.negate());
		}
	}

	public static final class Real extends Constant<BigRational> implements
			Comparable<Real> {
		public Real(BigInteger value) {
			super(K_REAL, BigRational.valueOf(value));
		}

		public Real(BigRational value) {
			super(K_REAL, value);
		}

		public Real(long value) {
			super(K_REAL, BigRational.valueOf(value));
		}

		public Real(String str) {
			super(K_REAL, new BigRational(str));
		}

		public Int numerator() {
			return new Int(value.numerator());
		}

		public Int denominator() {
			return new Int(value.denominator());
		}

		public int intValue() {
			return value.intValue();
		}

		public int compareTo(Real rhs) {
			return value.compareTo(rhs.value);
		}

		public Real add(Real x) {
			return new Real(value.add(x.value));
		}

		public Real subtract(Real x) {
			return new Real(value.subtract(x.value));
		}

		public Real multiply(Real x) {
			return new Real(value.multiply(x.value));
		}

		public Real divide(Real x) {
			return new Real(value.divide(x.value));
		}

		public Real negate() {
			return new Real(value.negate());
		}
	}

	public static final class Strung extends Constant<String> {
		public Strung(String value) {
			super(K_STRING, value);
		}

		public int compareTo(Strung rhs) {
			return value.compareTo(rhs.value);
		}

		public String toString() {
			return "\"" + value + "\"";
		}
	}

	/**
	 * Represents a sequence of zero or more object in the automaton.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static abstract class Collection extends State {

		protected int[] children;
		protected int length;

		private Collection(int kind, int... children) {
			super(kind);
			if (kind != K_LIST && kind != K_BAG && kind != K_SET) {
				throw new IllegalArgumentException("invalid compound kind");
			}
			this.children = children;
			this.length = children.length;
		}

		private Collection(int kind, java.util.List<Integer> children) {
			super(kind);
			int[] nchildren = new int[children.size()];
			for (int i = 0; i != nchildren.length; ++i) {
				nchildren[i] = children.get(i);
			}
			this.children = nchildren;
			length = nchildren.length;
		}

		public boolean remap(int[] map) {
			boolean changed = false;
			for (int i = 0; i != length; ++i) {
				int ochild = children[i];
				if (ochild >= 0) {
					int nchild = map[ochild];
					children[i] = nchild;
					changed |= nchild != ochild;
				}
			}
			return changed;
		}

		public int get(int index) {
			return children[index];
		}

		public int size() {
			return length;
		}

		public boolean contains(int index) {
			for (int i = 0; i < length; ++i) {
				if (children[i] == index) {
					return true;
				}
			}
			return false;
		}

		public boolean equals(final Object o) {
			if (o instanceof Collection) {
				Collection t = (Collection) o;
				if (kind == t.kind && length == t.length) {
					int[] t_children = t.children;
					for (int i = 0; i != length; ++i) {
						if (children[i] != t_children[i]) {
							return false;
						}
					}
					return true;
				}
			}
			return false;
		}

		public int hashCode() {
			int hashCode = kind;
			for (int i = 0; i != length; ++i) {
				hashCode ^= children[i];
			}
			return hashCode;
		}

		public Int lengthOf() {
			return new Int(length);
		}

		protected void internal_add(int ref) {
			if (length == children.length) {
				int nlength = (1 + length) * 2;
				int[] nchildren = new int[nlength];
				System.arraycopy(children, 0, nchildren, 0, length);
				children = nchildren;
			}
			children[length++] = ref;
		}

		public int[] toArray() {
			int[] result = new int[length];
			System.arraycopy(children, 0, result, 0, length);
			return result;
		}
	}

	public static final class Bag extends Collection {
		public Bag(int... children) {
			super(K_BAG, children);
			Arrays.sort(this.children);
		}

		public Bag(java.util.List<Integer> children) {
			super(K_BAG, children);
			Arrays.sort(this.children);
		}

		public boolean remap(int[] map) {
			if (super.remap(map)) {
				Arrays.sort(children, 0, length);
				return true;
			} else {
				return false;
			}
		}

		public Bag clone() {
			return new Bag(Arrays.copyOf(children, length));
		}

		public Bag append(Bag rhs) {
			return new Bag(Automaton.append(children, length, rhs.children,
					rhs.length));
		}

		public Bag append(int rhs) {
			return new Bag(Automaton.append(children, length, rhs));
		}

		public Bag appendFront(int lhs) {
			return new Bag(Automaton.append(lhs, children, length));
		}

		public Bag removeAll(Bag rhs) {
			return new Bag(sortedRemoveAll(this.children, length, rhs.children,
					rhs.length));
		}

		public String toString() {
			String r = "{|";
			for (int i = 0; i != length; ++i) {
				if (i != 0) {
					r += ",";
				}
				r += children[i];
			}
			return r + "|}";
		}
	}

	public static final class Set extends Collection {
		public Set(int... children) {
			super(K_SET, children);
			sortAndRemoveDuplicates();
		}

		public Set(java.util.List<Integer> children) {
			super(K_SET, children);
			sortAndRemoveDuplicates();
		}

		public boolean remap(int[] map) {
			if (super.remap(map)) {
				sortAndRemoveDuplicates();
				return true;
			} else {
				return false;
			}
		}

		public Set clone() {
			return new Set(Arrays.copyOf(children, length));
		}

		public Set append(Set rhs) {
			return new Set(Automaton.append(children, length, rhs.children,
					rhs.length));
		}

		public Set append(int rhs) {
			return new Set(Automaton.append(children, length, rhs));
		}

		public Set appendFront(int lhs) {
			return new Set(Automaton.append(lhs, children, length));
		}

		public Set removeAll(Set rhs) {

			// TODO: avoid the unnecessary sortAndRemoveDuplicates

			return new Set(sortedRemoveAll(this.children, length, rhs.children,
					rhs.length));
		}

		public String toString() {
			String r = "{";
			for (int i = 0; i != length; ++i) {
				if (i != 0) {
					r += ",";
				}
				r += children[i];
			}
			return r + "}";
		}

		private void sortAndRemoveDuplicates() {
			if (length == 0) {
				return;
			}

			Arrays.sort(children, 0, length);

			// first, decide if we have duplicates
			int last = children[0];
			int i;
			for (i = 1; i < length; ++i) {
				int current = children[i];
				if (current == last) {
					break; // duplicate detected
				} else {
					last = current;
				}
			}

			// second, if duplicates then mark and remove them
			if (i != length) {
				// duplicates is created lazily to avoid allocations in the
				// common
				// case.
				boolean[] duplicates = new boolean[length];
				int count = 0;
				for (; i < length; ++i) {
					int current = children[i];
					if (current == last) {
						duplicates[i] = true;
						count++;
					} else {
						last = current;
					}
				}
				int[] nchildren = new int[length - count];
				int j;
				for (i = 0, j = 0; i < length; ++i) {
					if (!duplicates[i]) {
						nchildren[j++] = children[i];
					}
				}
				children = nchildren;
				length = length - count;
			}
		}
	}

	public static final class List extends Collection {
		public List(int... children) {
			super(K_LIST, children);
		}

		public List(java.util.List<Integer> children) {
			super(K_LIST, children);
		}
		
		public int indexOf(Int idx) {
			return children[idx.intValue()];
		}

		public List update(Int idx, int value) {
			int[] nchildren = Arrays.copyOf(children, length);
			nchildren[idx.intValue()] = value;
			return new List(nchildren);
		}

		public List sublist(Int start, Int end) {
			return new List(Arrays.copyOfRange(children, start.intValue(),
					end.intValue()));
		}

		public List sublist(Int start) {
			return new List(Arrays.copyOfRange(children, start.intValue(),
					length));
		}

		public List sublist(int start, int end) {
			return new List(Arrays.copyOfRange(children, start, end));
		}

		public List sublist(int start) {
			return new List(Arrays.copyOfRange(children, start, length));
		}

		public List append(List rhs) {
			return new List(Automaton.append(children, length, rhs.children,
					rhs.length));
		}

		public List append(int rhs) {
			return new List(Automaton.append(children, length, rhs));
		}

		public List appendFront(int lhs) {
			return new List(Automaton.append(lhs, children, length));
		}

		public void add(int ref) {
			internal_add(ref);
		}

		public Collection clone() {
			return new List(Arrays.copyOf(children, length));
		}

		public String toString() {
			String r = "[";
			for (int i = 0; i != length; ++i) {
				if (i != 0) {
					r += ",";
				}
				r += children[i];
			}
			return r + "]";
		}
	}

	/**
	 * Copy into this automaton all states in the given automaton reachable from
	 * a given root state. This preserves the ordering of nodes in the original
	 * automaton as much as possible.
	 * 
	 * @param root
	 *            --- root index to begin copying from.
	 * @param binding
	 *            --- Initially, this identifies which states should be visited
	 *            during the copy. States which may be visited are marked with
	 *            zero, whilst those which should be ignored are marked with
	 *            K_VOID (-1). On completion of this method, this maps copied
	 *            states in the given automaton to their allocated states in
	 *            this automaton. States which weren't copied can be identified
	 *            as they are marked with K_VOID (-1).
	 * @param automaton
	 *            --- other automaton to copy states from.
	 * @return
	 */
	private void copy(Automaton automaton, int root, int[] binding) {
		Automata.traverse(automaton, root, binding);
		for (int i = 0; i != automaton.nStates(); ++i) {
			if (binding[i] > 0) {
				Automaton.State state = automaton.get(i);
				binding[i] = internalAdd(state.clone());
			} else {
				binding[i] = K_VOID;
			}
		}		
	}

	/**
	 * Return the automaton to a minimised state (i.e. where there are no
	 * distinct but equivalent states and no garbage states).
	 * @param binding
	 */
	private void compactAndMinimise(int[] binding) {
		Automata.eliminateUnreachableStates(this,binding);
		minimise(binding);
	}
	
	/**
	 * Eliminate all unnecessary equivalent states.
	 * 
	 * @param binding
	 */
	private void minimise(int[] binding) {
		// TODO: try to figure out whether this can be made more efficient!
		BinaryMatrix equivs = new BinaryMatrix(nStates,nStates,true);		
		Automata.determineEquivalenceClasses(this,equivs);
		this.nStates = Automata.determineRepresentativeStates(this,equivs,binding);
			
		for (int i = 0; i != nStates; ++i) {
			states[i].remap(binding);
		}
		for (int i = 0; i != nRoots; ++i) {
			int root = roots[i];
			if (root >= 0) {
				roots[i] = binding[root];
			}
		}	
	}
	
	/**
	 * Add a state onto the end of the states array, expanding that as
	 * necessary. However, the state is not collapsed with respect to any
	 * equivalent states.
	 */
	private int internalAdd(Automaton.State state) {
		if (nStates == states.length) {
			// oh dear, need to increase space
			State[] nstates = nStates == 0 ? new State[DEFAULT_NUM_STATES]
					: new State[nStates * 2];
			System.arraycopy(states, 0, nstates, 0, nStates);
			states = nstates;
		}

		states[nStates] = state;
		return nStates++;
	}

	private static int[] sortedRemoveAll(int[] lhs, int lhs_len, int[] rhs,
			int rhs_len) {
		boolean[] marks = new boolean[lhs_len];
		int count = 0;
		int i = 0;
		int j = 0;
		while (i < lhs_len && j < rhs_len) {
			int ith = lhs[i];
			int jth = rhs[j];
			if (jth < ith) {
				j++;
			} else if (ith < jth) {
				i++;
			} else {
				marks[i] = true;
				count++;
				i++;
			}
		}

		int[] nchildren = new int[lhs_len - count];
		j = 0;
		for (i = 0; i != lhs_len; ++i) {
			if (!marks[i]) {
				nchildren[j++] = lhs[i];
			}
		}
		return nchildren;
	}

	private static int[] append(int[] lhs, int lhs_len, int[] rhs, int rhs_len) {
		int[] nchildren = new int[lhs_len + rhs_len];
		System.arraycopy(lhs, 0, nchildren, 0, lhs_len);
		System.arraycopy(rhs, 0, nchildren, lhs_len, rhs_len);
		return nchildren;
	}

	private static int[] append(int[] lhs, int lhs_len, int rhs) {
		int[] nchildren = new int[lhs_len + 1];
		System.arraycopy(lhs, 0, nchildren, 0, lhs_len);
		nchildren[lhs_len] = rhs;
		return nchildren;
	}

	private static int[] append(int lhs, int[] rhs, int rhs_len) {
		int[] nchildren = new int[rhs_len + 1];
		System.arraycopy(rhs, 0, nchildren, 1, rhs_len);
		nchildren[0] = lhs;
		return nchildren;
	}

	/**
	 * The integer kind for a void state.
	 */
	public static final int K_VOID = -1;
	/**
	 * The integer kind for a Bool state.
	 */
	public static final int K_BOOL = -2;
	/**
	 * The integer kind for an Int state.
	 */
	public static final int K_INT = -3;
	/**
	 * The integer kind for a Real state.
	 */
	public static final int K_REAL = -4;
	/**
	 * The integer kind for a String state.
	 */
	public static final int K_STRING = -5;
	/**
	 * The integer kind for a List state.
	 */
	public static final int K_LIST = -6;
	/**
	 * The integer kind for a Bag state.
	 */
	public static final int K_BAG = -7;
	/**
	 * The integer kind for a Set state.
	 */
	public static final int K_SET = -8;
	
	public static final int K_FREE = -9;

	/**
	 * Constant which can be used simply to prevent unnecessary memory
	 * allocations.
	 */
	public static final int[] NOCHILDREN = new int[0];
	
	/**
	 * Internal constant used to prevent unnecessary memory
	 * allocations.
	 */
	private static final List EMPTY_LIST = new List(NOCHILDREN);
	
	/**
	 * Internal constant used to prevent unnecessary memory
	 * allocations.
	 */
	private static final Set EMPTY_SET = new Set(NOCHILDREN);
	
	/**
	 * Internal constant used to prevent unnecessary memory
	 * allocations.
	 */
	private static final Bag EMPTY_BAG = new Bag(NOCHILDREN);
	
	/**
	 * Internal constant used to prevent unnecessary memory
	 * allocations.
	 */
	public static final Bool TRUE = new Bool(true);
	
	/**
	 * Internal constant used to prevent unnecessary memory
	 * allocations.
	 */
	public static final Bool FALSE = new Bool(false);
}
