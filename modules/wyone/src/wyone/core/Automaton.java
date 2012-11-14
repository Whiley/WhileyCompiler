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

package wyone.core;

import java.math.BigInteger;
import java.util.*;

import wyone.util.BigRational;

/**
 * <p>
 * A finite-state automaton for representing Wyone objects. An
 * <code>Automaton</code> is a directed graph whose nodes and edges are referred
 * to as <i>states</i> and <i>transitions</i>. There are three distinct kinds of
 * state support:
 * </p>
 * <ul>
 * <li><p><b>Constants.</b> These states have no children and represent constant
 * values such as integers, booleans and strings.</p>
 * </li>
 * <li><p><b>Collections.</b> These states have 0 or more children and represent
 * collections of objects. There are three kinds of collection: <i>sets</i>,
 * <i>bags</i> and <i>lists</i>. A set maintains its children in sorted order
 * and eliminated duplicates; a bag simple maintains sorted order; finally, a
 * list maintains the given order.</p>
 * </li>
 * <li><p><b>Terms.</b> These represents the user-defined terms of the given
 * rewrite system. Every term has a unique name and an optional child.</p></li>
 * </ul>
 * 
 * <p>
 * <b>NOTE:</b> In the internal representation of automata, leaf states may be
 * not be represented as actual nodes. This will occur if the leaf node does not
 * include any supplementary data, and is primarily for space and performance
 * optimisation. In such case, the node is represented as a child node using a
 * negative index.
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public final class Automaton {	
	
	public static final int DEFAULT_NUM_STATES = 4;
	
	public static final int DEFAULT_NUM_ROOTS = 1;
	
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
	 * The number of used slots in the markers array.  It follows that
	 * <code>nMarkers <= markers.length</code> always holds.
	 */
	public int nMarkers;

	/**
	 * The array of automaton markers.
	 */
	public int[] markers;
	
	/**
	 * Describes the possible layouts of the used-defined states.
	 */
	private final Type.Term[] schema;
	
	public Automaton(Type.Term[] schema) {
		this.states = new Automaton.State[DEFAULT_NUM_STATES];
		this.markers = new int[DEFAULT_NUM_ROOTS];
		this.schema = schema;
	}
			
	public Automaton(Automaton automaton) {
		this.nStates = automaton.nStates;
		this.states = new State[automaton.states.length];
		for(int i=0;i!=states.length;++i) {
			Automaton.State state = automaton.states[i];			
			states[i] = state.clone();			
		}
		this.nMarkers = automaton.nMarkers;
		this.markers = Arrays.copyOf(automaton.markers, nMarkers);		
		this.schema = automaton.schema;
	}
	
	public Automaton(Type.Term[] schema, java.util.List<State> list) {
		this.nStates = list.size();
		this.states = new State[nStates];
		for (int i = 0; i != nStates; ++i) {
			Automaton.State state = list.get(i);
			this.states[i] = state.clone();
		}
		this.markers = new int[DEFAULT_NUM_ROOTS];
		this.schema = schema;
	}
	
	public int nStates() {
		return nStates;
	}
	
	public int nRoots() {
		return nMarkers;
	}
	
	public Type[] schema() {
		return schema;
	}
	
	/**
	 * Return the state at a given index into the automaton.
	 * 
	 * @param index
	 *            --- Index of state to return where
	 *            <code>0 <= index <= nStates()</code>.
	 * @return
	 */
	public State get(int index) {
		if(index < 0) {
			switch(index) {				
				case K_LIST:
					return EMPTY_LIST;
				case K_SET:
					return EMPTY_SET;
				case K_BAG:
					return EMPTY_BAG;
				default:
					return new Term(-index + K_FREE,K_VOID);
			}
		} 
		
		return states[index];		
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
	 * the automaton retains the strong equivalence property.
	 * </p>
	 * 
	 * @param state
	 * @return
	 */
	public int add(Automaton.State state) {

		// First, check to see whether this state is uniquely identified by its
		// kind.
		if (state instanceof Term) {
			Term term = (Term) state;
			if (term.contents == Automaton.K_VOID) {
				return K_FREE - term.kind;
			}
		} else if (state instanceof Compound) {
			Compound compound = (Compound) state;
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
	 * the automaton remains compacted and with the strong equivalence property.
	 * </p>
	 * 
	 * @param root
	 *            --- root index to begin copying from.
	 * @param automaton
	 *            --- other automaton to copye states from.
	 * @return
	 */
	public int addAll(int root, Automaton automaton) {
		int[] binding = new int[automaton.nStates()];
		int max = nStates + automaton.nStates;
		for (int i = 0; i != binding.length; ++i) {
			binding[i] = -1;
		}
		int nroot = nStates;
		copy(root, binding, automaton);
		remap(nroot, nStates, binding);
		minimise();
		// FIXME: nroot might have been reduced
		return nroot;
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
	 * necessary to use a marker. The resulting automaton is guaranteed to have
	 * the strong equivalence property and to be compacted.
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
			remap(0, nStates, map);
			minimise();
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
	 * the automaton retains the strong equivalence property. However, the
	 * automaton may now contain garbage states and should be be compacted at a
	 * later stage.
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
		Arrays.fill(binding, -1);
		if (reachable(source, search, binding)) {
			int start = nStates;
			Arrays.fill(binding, -1);
			binding[search] = search; // don't visit subtrees of search term
			copy(source, binding, this);
			binding[search] = replacement;
			remap(start, nStates, binding);
			// FIXME: problem here because start may be reduced
			minimise();
			return start;
		} else {
			return source; // no change
		}
	}	
	
	/**
	 * Mark a given state. This means it is treated specially, and will never be
	 * deleted from the automaton as a result of garbage collection.
	 * 
	 * @param root
	 * @return
	 */
	public int mark(int root) {
		// First, check whether this root was already marked
		for(int i=0;i!=nMarkers;++i) {
			if(markers[i] == root) {
				return i; // match
			}
		}
		// Second, create a new root
		if(nMarkers == markers.length) {
			int[] nroots = nMarkers == 0
					? new int[DEFAULT_NUM_ROOTS]
					: new int[nMarkers * 2];
			System.arraycopy(markers,0,nroots,0,nMarkers);
			markers = nroots;
		}		
		markers[nMarkers] = root;
		return nMarkers++;
	}
	
	/**
	 * Get the given marked node.
	 * 
	 * @param index
	 * @return
	 */
	public int marker(int index) {
		return markers[index];
	}
	
	/**
	 * <p>
	 * Remove all garbage from the automaton, and generally compact the
	 * automaton's representation. Garbage is defined as any state which is
	 * unreachable from any marked nodes.
	 * </p>
	 * <p>
	 * <b>NOTE:</b> all references which were valid beforehand may now be
	 * invalidated. In order to preserve a reference through compaction, it is
	 * necessary to use a marker.
	 * </p>
	 */
	public void compact() {
		int[] tmp = new int[nStates]; // temporary storage

		// first, visit all nodes
		for (int i = 0; i != nMarkers; ++i) {
			int root = markers[i];
			if (root >= 0) {
				findHeaders(root, tmp);
			}
		}

		// second, shift slots down to compact them
		int count = 0;
		for (int i = 0; i != nStates; ++i) {
			if (tmp[i] != 0) {
				tmp[i] = count;
				states[count++] = states[i];
			}
		}
		nStates = count;

		// finally, update mappings and roots
		for (int i = 0; i != count; ++i) {
			states[i].remap(tmp);
		}
		for (int i = 0; i != nMarkers; ++i) {
			int root = markers[i];
			if (root >= 0) {
				markers[i] = tmp[root];
			}
		}
	}
	
	
	/**
	 * Determine the hashCode of a type.
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
	 * <i>identical</i>. Observe that it does not perform an
	 * <i>isomorphism</i> test. Thus, two distinct types which are
	 * structurally isomorphic will <b>not</b> be considered equal under
	 * this method. <b>NOTE:</b> to test whether two types are structurally
	 * isomorphic, using the <code>isomorphic(t1,t2)</code> method.
	 */
	public boolean equals(Object o) {
		if(o instanceof Automaton) {
			Automaton a = (Automaton) o;
			State[] cs = a.states;
			if(a.nStates != nStates) {
				return false;
			}
			for(int i=0;i!=nStates;++i) {
				State si = states[i];
				State ci = cs[i];
				if(!states[i].equals(ci)) {
					return false;
				} 
			}
			return true;
		}
		return false;
	}
	
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
				if(t.contents == K_VOID) {
					r = r + schema[t.kind].name();
				} else {
					r = r + schema[t.kind].name() + "(" + t.contents + ")";
				}
			} else {
				r = r + state.toString();
			} 
		}		
		r = r + " <";
		for(int i=0;i!=markers.length;++i) {
			if(i != 0) {
				r += ",";
			}
			r += markers[i];
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
			if(kind < 0) { throw new IllegalArgumentException("invalid term kind"); }
			this.contents = K_VOID;
		}
		
		public Term(int kind, int data) {
			super(kind);
			if(kind < 0) { throw new IllegalArgumentException("invalid term kind"); }
			this.contents = data;
		}
		
		public Term clone() {
			return new Term(kind,contents);
		}
		
		public boolean remap(int[] map) {
			int old = contents;
			if(old >= 0) {
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
	
	public static final class Int extends Constant<BigInteger> implements Comparable<Int> {
		public Int(BigInteger value) {
			super(K_INT, value);
		}

		public Int(long value) {
			super(K_INT,BigInteger.valueOf(value));
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
	
	public static final class Real extends Constant<BigRational> implements Comparable<Real> {
		public Real(BigInteger value) {
			super(K_REAL, BigRational.valueOf(value));
		}

		public Real(BigRational value) {
			super(K_REAL, value);
		}
		
		public Real(long value) {
			super(K_REAL,BigRational.valueOf(value));
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
	}
	
	/**
	 * Represents a sequence of zero or more object in the automaton.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static abstract class Compound extends State {

		protected int[] children;
		protected int length;

		private Compound(int kind, int...children) {
			super(kind);
			if (kind != K_LIST && kind != K_BAG && kind != K_SET) {
				throw new IllegalArgumentException("invalid compound kind");
			} 			
			this.children = children;
			this.length = children.length;
		}
		
		private Compound(int kind, java.util.List<Integer> children) {
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
				if(ochild >= 0) {
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
			for(int i=0;i<length;++i) {
				if(children[i] == index) {
					return true;
				}
			}
			return false;
		}
		
		public boolean equals(final Object o) {
			if (o instanceof Compound) {				
				Compound t = (Compound) o;
				if(kind == t.kind && length == t.length) {
					int[] t_children = t.children;
					for(int i=0;i!=length;++i) {
						if(children[i] != t_children[i]) {
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
			for(int i=0;i!=length;++i) {
				hashCode ^= children[i];
			}
			return hashCode;
		}			
		
		public Int lengthOf() {
			return new Int(length);
		}
		
		protected void internal_add(int ref) {
			if(length == children.length) {
				int nlength = (1+length) * 2;
				int[] nchildren = new int[nlength];
				System.arraycopy(children,0,nchildren,0,length);
				children = nchildren;
			}
			children[length++] = ref;			
		}
		
		public int[] toArray() {
			int[] result = new int[length];
			System.arraycopy(children,0,result,0,length);
			return result;
		}
	}
	
	public static final class Bag extends Compound {
		public Bag(int... children) {
			super(K_BAG, children);
			Arrays.sort(this.children);
		}
		
		public Bag(java.util.List<Integer> children) {
			super(K_BAG, children);
			Arrays.sort(this.children);
		}
		
		public boolean remap(int[] map) {
			if(super.remap(map)) {
				Arrays.sort(children,0,length);
				return true;
			} else {
				return false;
			}
		}
		
		public Bag clone() {
			return new Bag(Arrays.copyOf(children, length));
		}
		
		public Bag append(Bag rhs) {
			return new Bag(Automaton.append(children,length,rhs.children,rhs.length));
		}
		
		public Bag append(int rhs) {
			return new Bag(Automaton.append(children,length,rhs));			
		}
		
		public Bag appendFront(int lhs) {
			return new Bag(Automaton.append(lhs,children,length));
		}
		
		public Bag removeAll(Bag rhs) {
			return new Bag(sortedRemoveAll(this.children, length, rhs.children, rhs.length));
		}
		
		public String toString() {
			String r = "{|";
			for(int i=0;i!=length;++i) {
				if(i != 0) { r += ","; }
				r += children[i];
			}
			return r + "|}";
		}
	}
	
	public static final class Set extends Compound {
		public Set(int... children) {
			super(K_SET, children);
			sortAndRemoveDuplicates();
		}

		public Set(java.util.List<Integer> children) {
			super(K_SET, children);
			sortAndRemoveDuplicates();
		}

		public boolean remap(int[] map) {
			if(super.remap(map)) {
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
			return new Set(Automaton.append(children,length,rhs.children,rhs.length));
		}
		
		public Set append(int rhs) {
			return new Set(Automaton.append(children,length,rhs));			
		}
		
		public Set appendFront(int lhs) {
			return new Set(Automaton.append(lhs,children,length));
		}
		
		public Set removeAll(Set rhs) {
			
			// TODO: avoid the unnecessary sortAndRemoveDuplicates
			
			return new Set(sortedRemoveAll(this.children, length, rhs.children, rhs.length));
		}
		
		public String toString() {
			String r = "{";
			for(int i=0;i!=length;++i) {
				if(i != 0) { r += ","; }
				r += children[i];
			}
			return r + "}";
		}
		
		private void sortAndRemoveDuplicates() {
			if(length == 0) {
				return;
			}
			
			Arrays.sort(children,0,length);
			
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
			if(i != length) {				
				// duplicates is created lazily to avoid allocations in the common
				// case.
				boolean[] duplicates = new boolean[length];
				int count = 0;
				for(;i < length;++i) {
					int current = children[i];
					if(current == last) {
						duplicates[i] = true;
						count++;
					} else {
						last = current;
					}
				}
				int[] nchildren = new int[length-count];
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
	
	public static final class List extends Compound {
		public List(int...children) {
			super(K_LIST,children);
		}
		
		public List(java.util.List<Integer> children) {
			super(K_LIST,children);
		}
		
		public int indexOf(Int idx) {
			return children[idx.intValue()];
		}
		
		public List update(Int idx, int value) {
			int[] nchildren = Arrays.copyOf(children,length);
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
			return new List(Arrays.copyOfRange(children, start,
					end));
		}

		public List sublist(int start) {
			return new List(
					Arrays.copyOfRange(children, start, length));
		}
		
		public List append(List rhs) {
			return new List(Automaton.append(children,length,rhs.children,rhs.length));
		}
		
		public List append(int rhs) {
			return new List(Automaton.append(children,length,rhs));			
		}
		
		public List appendFront(int lhs) {
			return new List(Automaton.append(lhs,children,length));
		}
		
		public void add(int ref) {
			internal_add(ref);
		}
		
		public Compound clone() {
			return new List(Arrays.copyOf(children,length));
		}		
		
		public String toString() {
			String r = "[";
			for(int i=0;i!=length;++i) {
				if(i != 0) { r += ","; }
				r += children[i];
			}
			return r + "]";
		}
	}
	
	/**
	 * Applying a mapping from "old" vertices to "new" vertices across the whole
	 * object space. Thus, any references to an "old" vertex is replaced with
	 * its corresponding "new" vertex (according to the given map). This can
	 * invalidate the strong equivalence property and the minimise function
	 * should be called subsequently.
	 * 
	 * @param map
	 */
	private void remap(int start, int end, int[] map) {
		for (int i = start; i < end; ++i) {
			State state = states[i];			
			state.remap(map);			
		}
	}
	
	/**
	 * Copy into this automaton all states in the given automaton reachable from
	 * a given root state.
	 * 
	 * @param root
	 *            --- root index to begin copying from.
	 * @param binding
	 *            --- mapping from states in given automaton to states in this
	 *            automaton.  
	 * @param automaton
	 *            --- other automaton to copye states from.
	 * @return
	 */
	private void copy(int root, int[] binding, Automaton automaton) {
		if (root >= 0 && binding[root] == -1) {
			// this root not yet visited.
			Automaton.State state = automaton.get(root);		
			binding[root] = internalAdd(state.clone());

			if(state instanceof Automaton.Term) {
				Automaton.Term term = (Automaton.Term) state;
				if(term.contents != Automaton.K_VOID) {
					copy(term.contents,binding,automaton);
				} 
			} else if(state instanceof Automaton.Compound) {
				Automaton.Compound compound = (Automaton.Compound) state;
				int[] children = compound.children;			
				for(int i=0;i!=children.length;++i) {
					copy(compound.children[i],binding,automaton);							
				}			
			}
		}
	}
	
	/**
	 * Check whether a given node is reachable from a given root.
	 * 
	 * @param root
	 *            --- root index to begin copying from.
	 * @param binding
	 *            --- mapping from states in given automaton to states in this
	 *            automaton.  
	 * @return
	 */
	private boolean reachable(int root, int search, int[] binding) {
		if (root == search) {
			return true;
		} else if (binding[root] == -1) {
			// this root not yet visited.
			Automaton.State state = states[root];
			binding[root] = 0; // visited

			if (state instanceof Automaton.Term) {
				Automaton.Term term = (Automaton.Term) state;
				if (term.contents != Automaton.K_VOID) {
					if (reachable(term.contents, search, binding)) {
						return true;
					}
				}
			} else if (state instanceof Automaton.Compound) {
				Automaton.Compound compound = (Automaton.Compound) state;
				int[] children = compound.children;
				for (int i = 0; i != children.length; ++i) {
					if (reachable(children[i], search, binding)) {
						return true;
					}
				}
			}
		}
		return false;
	}
		
	/**
	 * Visit all nodes reachable from the given node.
	 * 
	 * @param node
	 *            --- root index to begin copying from.
	 * @param visited
	 *            --- iniitially, unvisited states are marked with '0' which
	 *            subsequently becomes non-zero to indicate they were visited.
	 *            For nodes assigned a header value of 1, this indicates they
	 *            are not the header for cycle, whilst those assigned header
	 *            value > 1 are the head of a cycle.
	 * @return
	 */
	public void findHeaders(int node, int[] headers) {
		int header = headers[node];
		if(header > 1) {
			return; // nothing to do, already marked as a header
		} else if(header == 1) {
			headers[node] = node + 2;
			return; // done
		} else {
			headers[node] = 1;
			Automaton.State state = states[node];
			if (state instanceof Automaton.Term) {
				Automaton.Term term = (Automaton.Term) state;
				if (term.contents != Automaton.K_VOID) {
					findHeaders(term.contents, headers);
					
				}
			} else if (state instanceof Automaton.Compound) {
				Automaton.Compound compound = (Automaton.Compound) state;
				int[] children = compound.children;
				for (int i = 0; i != children.length; ++i) {
					findHeaders(children[i], headers);
				}
			}
		}		
	}
	
	private void minimise() {
		// FIXME: to do!
	}
	
	/**
	 * Add a state onto the end of the states array, expanding that as
	 * necessary. However, the state is not collapsed with respect to any
	 * equivalent states.
	 */
	private int internalAdd(Automaton.State state) {
		if (nStates == states.length) {
			// oh dear, need to increase space
			State[] nstates = nStates == 0
					? new State[DEFAULT_NUM_STATES]
					: new State[nStates * 2];
			System.arraycopy(states, 0, nstates, 0, nStates);
			states = nstates;
		}

		states[nStates] = state;
		return nStates++;
	}
	
	private static int[] sortedRemoveAll(int[] lhs, int lhs_len, int[] rhs, int rhs_len) {		
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
		System.arraycopy(lhs,0,nchildren,0,lhs_len);
		nchildren[lhs_len] = rhs;			
		return nchildren;
	}
	
	private static int[] append(int lhs, int[] rhs, int rhs_len) {
		int[] nchildren = new int[rhs_len + 1];
		System.arraycopy(rhs,0,nchildren,1,rhs_len);
		nchildren[0] = lhs;			
		return nchildren;
	}
	
	public static final int K_VOID = -1;
	public static final int K_INT = -2;
	public static final int K_REAL = -3;
	public static final int K_STRING = -4;
	public static final int K_LIST = -5;
	public static final int K_BAG = -6;
	public static final int K_SET = -7;
	public static final int K_FREE = -8;
	
	
	/**
	 * The following constant is used simply to prevent unnecessary memory
	 * allocations.
	 */
	public static final int[] NOCHILDREN = new int[0];
	public static final List EMPTY_LIST = new List(NOCHILDREN);
	public static final Set EMPTY_SET = new Set(NOCHILDREN);
	public static final Bag EMPTY_BAG = new Bag(NOCHILDREN);
}
