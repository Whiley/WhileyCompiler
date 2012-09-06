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

/**
 * <p>
 * A finite-state automaton for representing wyone objects. This is a machine
 * for accepting matching inputs of a given language. An automaton is a directed
 * graph whose nodes and edges are referred to as <i>states</i> and
 * <i>transitions</i>. Each state has a "kind" which determines how the state
 * behaves on given inputs. For example, a state with "OR" kind might accept an
 * input if either of its children does; in contrast, and state of "AND" kind
 * might accept an input only if all its children does.
 * </p>
 * 
 * <p>
 * The organisation of children is done according to two approaches:
 * <i>deterministic</i> and <i>non-deterministic</i>. In the deterministic
 * approach, the ordering of children is important; in the non-deterministic
 * approach, the ordering of children is not important. A flag is used to
 * indicate whether a state is deterministic or not.
 * </p>
 * 
 * <p>
 * Aside from having a particular kind, each state may also have supplementary
 * material. This can be used, for example, to effectively provide labelled
 * transitions. Another use of this might be to store a given string which must
 * be matched.
 * </p>
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
	 * The number of used slots in the states array.
	 */
	private int nStates;
		
	/**
	 * The array of automaton states. <b>NOTES:</b> this may contain
	 * <code>null</code> values for states which have been garbage collected.
	 */
	private State[] states;	// should not be public!
	
	/**
	 * The array of automaton roots.
	 */
	public int[] roots;

	/**
	 * The number of used slots in the roots array
	 */
	public int nRoots;
	
	/**
	 * Describes the possible layouts of the used-defined states.
	 */
	private final Type.Term[] schema;
	
	public Automaton(Type.Term[] schema) {
		this.states = new Automaton.State[DEFAULT_NUM_STATES];
		this.roots = new int[DEFAULT_NUM_ROOTS];
		this.schema = schema;
	}
			
	public Automaton(Automaton automaton) {
		this.nStates = automaton.nStates;
		this.states = new State[automaton.states.length];
		for(int i=0;i!=states.length;++i) {
			states[i] = automaton.states[i].clone();			
		}
		this.nRoots = automaton.nRoots;
		this.roots = Arrays.copyOf(automaton.roots, nRoots);		
		this.schema = automaton.schema;
	}
	
	public int nStates() {
		return nStates;
	}
	
	public int nRoots() {
		return nRoots;
	}
	
	public State get(int index) {
		if(index < 0) {
			switch(index) {				
				case K_LIST:
					return EMPTY_LIST;
				case K_SET:
					return EMPTY_SET;
				default:
					return new Term(-index + K_FREE,K_VOID);
			}
		} 
		
		return states[index];		
	}
	
	/**
	 * Add a new state into the automaton. If there is already an equivalent
	 * state, then its index is returned. Or, if the state can be represented
	 * "virtually", then a negative (but still valid) index will be returned.
	 * Otherwise, the state is added onto the end of the states array and its
	 * index is returned.
	 * 
	 * @param state
	 * @return
	 */
	public int add(Automaton.State state) {
		
		// First, check to see whether this state is uniquely identified by its
		// kind.
		if(state instanceof Term) {
			Term term = (Term) state;
			if(term.contents == Automaton.K_VOID) {
				return K_FREE - term.kind;
			}
		} else if(state instanceof Compound) {
			Compound compound = (Compound) state;
			if(compound.children.length == 0) {
				return compound.kind;
			}
		}
		
		// Second, check to see whether there already exists an equivalent
		// state. 
		for(int i=0;i!=nStates;++i) {
			State ith = states[i];
			if(ith != null && ith.equals(state)) {
				return i; // match
			}
		}
		
		// Third, allocate a new state!
		if(nStates == states.length) {
			// oh dear, need to increase space
			State[] nstates = nStates == 0
					? new State[DEFAULT_NUM_STATES]
					: new State[nStates * 2];
			System.arraycopy(states,0,nstates,0,nStates);
			states = nstates;
		}
		
		states[nStates] = state;
		return nStates++;
	}
	
	/**
	 * Rewrite one state into another.
	 * 
	 * @param src
	 *            --- state being rewritten to look like target.
	 * @param target
	 *            --- src is rewritten to look like target, which is then
	 *            destroyed.
	 * @return
	 */
	public boolean rewrite(int src, int target) {
		if(src == target) {
			return false;
		} else if(target < 0) {
			for(int i=0;i!=nStates;++i) {
				State state = states[i];
				if(state != null) {
					state.remap(src,target);
				}
			}
			for(int i=0;i!=nRoots;++i) {
				if(roots[i] == src) {
					roots[i] = target;
				}
			}
			if(src >= 0) {
				states[src] = null;
			}
			return true;
		} else {
			State os = states[src];
			State ns = states[target];
			states[src] = ns;
			for(int i=0;i!=nStates;++i) {
				State state = states[i];
				if(state != null) {
					state.remap(target,src);
				}
			}
			for(int i=0;i!=nRoots;++i) {
				if(roots[i] == target) {
					roots[i] = src;
				}
			}
			states[target] = null;
			return !os.equals(ns);
		}
	}
	
	/**
	 * Mark a state as a "root". This means it is treated specially, and will
	 * never be deleted from the automaton as a result of garbage collection.
	 * 
	 * @param root
	 * @return 
	 */
	public int mark(int root) {
		// First, check whether this root was already marked
		for(int i=0;i!=nRoots;++i) {
			if(roots[i] == root) {
				return i; // match
			}
		}
		// Second, create a new root
		if(nRoots == roots.length) {
			int[] nroots = nRoots == 0
					? new int[DEFAULT_NUM_ROOTS]
					: new int[nRoots * 2];
			System.arraycopy(roots,0,nroots,0,nRoots);
			roots = nroots;
		}		
		roots[nRoots] = root;
		return nRoots++;
	}
	
	public int root(int index) {
		return roots[index];
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
				if(!states[i].equals(cs[i])) {
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
					r = r + schema[t.kind].name;
				} else {
					r = r + schema[t.kind].name + "(" + t.contents + ")";
				}
			} else if(state != null) {
				r = r + state.toString();
			} else {
				r = r + "null";
			}
		}		
		r = r + " <";
		for(int i=0;i!=roots.length;++i) {
			if(i != 0) {
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
		
		public abstract void remap(int from, int to);
		
		public boolean equals(final Object o) {
			if (o instanceof State) {
				State c = (State) o;
				// in the following, we only need to check data != null for this
				// node as both nodes have the same kind and, hence, this.data
				// != null implies c.data != null.
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
			this.contents = K_VOID;
		}
		
		public Term(int kind, int data) {
			super(kind);
			this.contents = data;
		}
		
		public Term clone() {
			return new Term(kind,contents);
		}
		
		public void remap(int from, int to) {
			if(contents == from) {
				contents = to;
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
			return kind + " " + contents;
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

		public void remap(int from, int to) {
			// nothing to do
		}
		
		public boolean equals(final Object o) {
			if (o instanceof Constant) {
				Constant t = (Constant) o;
				return kind == t.kind && value.equals(t.value);
			}
			return false;
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
		
		public Constant clone() {
			return new Int(value);
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
	}
	
	public static final class Strung extends Constant<String> {
		public Strung(String value) {
			super(K_STRING, value);
		}

		public Strung clone() {
			return new Strung(value);
		}
	}
	
	/**
	 * Represents a sequence of zero or more object in the automaton.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static abstract class Compound extends State {
		public int[] children;

		private Compound(int kind, int...children) {
			super(kind);
			if (kind != K_LIST && kind != K_SET) {
				throw new IllegalArgumentException("invalid compound kind");
			} 			
			this.children = children;
		}
		
		private Compound(int kind, java.util.List<Integer> children) {
			super(kind);
			if(kind != K_LIST && kind != K_SET) {
				throw new IllegalArgumentException("invalid compound kind");
			}
			int[] nchildren = new int[children.size()];
			for (int i = 0; i != children.size(); ++i) {
				nchildren[i] = children.get(i);
			}			
			this.children = nchildren;			
		}
		
		public void remap(int from, int to) {
			for(int i=0;i!=children.length;++i) {
				if(children[i] == from) {
					children[i] = to;
				}
			}			
		}

		public int get(int index) {
			return children[index];
		}
		
		public int size() {
			return children.length;
		}
		
		public boolean equals(final Object o) {
			if (o instanceof Compound) {
				Compound t = (Compound) o;
				int[] t_children = t.children;
				return kind == t.kind && Arrays.equals(children, t_children);
			}
			return false;
		}

		public int hashCode() {
			return kind * Arrays.hashCode(children);
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
		
		public void remap(int from, int to) {
			super.remap(from,to);
			Arrays.sort(children);
		}
		
		public Bag clone() {
			return new Bag(Arrays.copyOf(children, children.length));
		}
		
		public Bag append(Bag rhs) {
			return new Bag(Automaton.append(children,rhs.children));
		}
		
		public Bag append(int rhs) {
			return new Bag(Automaton.append(children,rhs));			
		}
		
		public Bag appendFront(int lhs) {
			return new Bag(Automaton.append(lhs,children));
		}
		
		public Bag removeAll(Bag rhs) {
			return new Bag(sortedRemoveAll(this.children, rhs.children));
		}
		
		public String toString() {
			String r = "{|";
			for(int i=0;i!=children.length;++i) {
				if(i != 0) { r += ","; }
				r += children[i];
			}
			return r + "|}";
		}
	}
	
	public static final class Set extends Compound {
		public Set(int... children) {
			super(K_SET, sortAndRemoveDuplicates(children));
		}

		public Set(java.util.List<Integer> children) {
			super(K_SET, sortAndRemoveDuplicates(children));
		}

		public void remap(int from, int to) {
			super.remap(from,to);
			children = sortAndRemoveDuplicates(children);
		}
		
		public Set clone() {
			return new Set(Arrays.copyOf(children, children.length));
		}
		
		public Set append(Set rhs) {
			return new Set(Automaton.append(children,rhs.children));
		}
		
		public Set append(int rhs) {
			return new Set(Automaton.append(children,rhs));			
		}
		
		public Set appendFront(int lhs) {
			return new Set(Automaton.append(lhs,children));
		}
		
		public Set removeAll(Set rhs) {
			return new Set(sortedRemoveAll(this.children, rhs.children));
		}
		
		public String toString() {
			String r = "{";
			for(int i=0;i!=children.length;++i) {
				if(i != 0) { r += ","; }
				r += children[i];
			}
			return r + "}";
		}
	}
	
	public static final class List extends Compound {
		public List(int...children) {
			super(K_LIST,children);
		}
		
		public List(java.util.List<Integer> children) {
			super(K_LIST,children);
		}
		
		public List sublist(Int start, Int end) {
			return new List(Arrays.copyOfRange(children, start.intValue(),
					end.intValue()));
		}

		public List sublist(Int start) {
			return new List(Arrays.copyOfRange(children, start.intValue(),
					children.length));
		}
		
		public List append(List rhs) {
			return new List(Automaton.append(children,rhs.children));
		}
		
		public List append(int rhs) {
			return new List(Automaton.append(children,rhs));			
		}
		
		public List appendFront(int lhs) {
			return new List(Automaton.append(lhs,children));
		}
		
		public Compound clone() {
			return new List(Arrays.copyOf(children,children.length));
		}		
		
		public String toString() {
			String r = "[";
			for(int i=0;i!=children.length;++i) {
				if(i != 0) { r += ","; }
				r += children[i];
			}
			return r + "]";
		}
	}
	
	private static int[] sortAndRemoveDuplicates(
			java.util.List<Integer> children) {
		int[] nchildren = new int[children.size()];
		for (int i = 0; i != nchildren.length; ++i) {
			nchildren[i] = children.get(i);
		}
		return sortAndRemoveDuplicates(nchildren);
	}
	
	private static int[] sortAndRemoveDuplicates(int[] children) {
		if(children.length == 0) {
			return children;
		}
		
		Arrays.sort(children);
		
		// first, decide if we have duplicates
		final int length = children.length;
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
		if(i == length) {
			return children;
		} else {
			// duplicates is created lazily to avoid allocations in the common
			// case.
			boolean[] duplicates = new boolean[children.length];
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
			int[] nchildren = new int[children.length-count];
			int j;
			for (i = 0, j = 0; i < length; ++i) {
				if (!duplicates[i]) {
					nchildren[j++] = children[i];
				}
			}
			return nchildren;
		}
	}
	
	private static int[] sortedRemoveAll(int[] lhs, int[] rhs) {		
		boolean[] marks = new boolean[lhs.length];
		int count = 0;
		int i = 0;
		int j = 0;
		while (i < lhs.length && j < rhs.length) {
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

		int[] nchildren = new int[lhs.length - count];
		j = 0;
		for (i = 0; i != lhs.length; ++i) {
			if (!marks[i]) {
				nchildren[j++] = lhs[i];
			}
		}
		return nchildren;
	}
	
	private static int[] append(int[] lhs, int[] rhs) {
		int[] nchildren = new int[lhs.length + rhs.length];
		System.arraycopy(lhs, 0, nchildren, 0, rhs.length);
		System.arraycopy(rhs, 0, nchildren, lhs.length, rhs.length);
		return nchildren;
	}
	
	private static int[] append(int[] lhs, int rhs) {
		int[] nchildren = new int[lhs.length + 1];
		System.arraycopy(lhs,0,nchildren,0,lhs.length);
		nchildren[lhs.length] = rhs;			
		return nchildren;
	}
	
	private static int[] append(int lhs, int[] rhs) {
		int[] nchildren = new int[rhs.length + 1];
		System.arraycopy(rhs,0,nchildren,1,rhs.length);
		nchildren[0] = lhs;			
		return nchildren;
	}
	
	public static final int K_VOID = -1;
	public static final int K_INT = -2;
	public static final int K_STRING = -3;
	public static final int K_LIST = -4;
	public static final int K_BAG = -5;
	public static final int K_SET = -6;
	public static final int K_FREE = -7;
	
	
	/**
	 * The following constant is used simply to prevent unnecessary memory
	 * allocations.
	 */
	public static final int[] NOCHILDREN = new int[0];
	public static final List EMPTY_LIST = new List(NOCHILDREN);
	public static final Set EMPTY_SET = new Set(NOCHILDREN);
}
