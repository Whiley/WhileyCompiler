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
			} else {
				r = r + state.toString();
			}
		}		
		r = r + "<";
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
	
	public static final class Int extends Constant<BigInteger> {
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
		public final int[] children;

		private Compound(int kind, int...children) {
			super(kind);
			if(kind != K_LIST && kind != K_SET) {
				throw new IllegalArgumentException("invalid compound kind");
			} else if(kind == K_SET) {
				Arrays.sort(children);
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
			if(kind == K_SET) {
				Arrays.sort(nchildren);
			}
			this.children = nchildren;			
		}
		
		public void remap(int from, int to) {
			for(int i=0;i!=children.length;++i) {
				if(children[i] == from) {
					children[i] = to;
				}
			}
			if(kind == K_SET) {
				Arrays.sort(children);
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
				return kind == t.kind && Arrays.equals(children,t_children);							
			}
			return false;
		}

		public int hashCode() {
			return kind * Arrays.hashCode(children);
		}
		
		public String toString() {
			String r = "";
			switch(kind) {
				case K_LIST:
					r = "[";
					break;
				case K_SET:
					r = "{";
					break;
			}
			
			for(int i=0;i!=children.length;++i) {
				if(i != 0) { r += ","; }
				r += children[i];
			}
			
			switch(kind) {
				case K_LIST:
					r += "]";
					break;
				case K_SET:
					r += "}";
					break;
			}
			
			return r;
		}
	}
	
	public static final class Set extends Compound {
		public Set(int... children) {
			super(K_SET, children);
		}

		public Set(java.util.List<Integer> children) {
			super(K_SET, children);
		}

		public Compound clone() {
			return new Set(Arrays.copyOf(children, children.length));
		}
		
		public Set append(Compound rhs) {
			int[] nchildren = new int[children.length + rhs.children.length];
			System.arraycopy(children,0,nchildren,0,children.length);
			System.arraycopy(rhs.children,0,nchildren,children.length,rhs.children.length);
			return new Set(nchildren);
		}
		
		public Set append(int rhs) {
			int[] nchildren = new int[children.length + 1];
			System.arraycopy(children,0,nchildren,0,children.length);
			nchildren[children.length] = rhs;			
			return new Set(nchildren);
		}
		
		public Set appendFront(int lhs) {
			int[] nchildren = new int[children.length + 1];
			System.arraycopy(children,0,nchildren,1,children.length);
			nchildren[0] = lhs;			
			return new Set(nchildren);
		}
		
		public Set removeAll(Set rhs) {		
			int[] rhs_children = rhs.children;
			boolean[] marks = new boolean[children.length];
			int count = 0;
			int i = 0;
			int j = 0;
			while (i < children.length && j < rhs_children.length) {
				int ith = children[i];
				int jth = rhs_children[j];
				if (jth < ith) {
					j++;
				} else if (ith < jth) {
					i++;
				} else {
					marks[i] = true;
					i++;
					j++;
				}
			}

			int[] nchildren = new int[children.length - count];
			j = 0;
			for (i = 0; i != children.length; ++i) {
				if (!marks[i]) {
					nchildren[j++] = children[i];
				}
			}
			return new Set(nchildren);
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
			int[] nchildren = new int[children.length + rhs.children.length];
			System.arraycopy(children,0,nchildren,0,children.length);
			System.arraycopy(rhs.children,0,nchildren,children.length,rhs.children.length);
			return new List(nchildren);
		}
		
		public List append(int rhs) {
			int[] nchildren = new int[children.length + 1];
			System.arraycopy(children,0,nchildren,0,children.length);
			nchildren[children.length] = rhs;			
			return new List(nchildren);
		}
		
		public List appendFront(int lhs) {
			int[] nchildren = new int[children.length + 1];
			System.arraycopy(children,0,nchildren,1,children.length);
			nchildren[0] = lhs;			
			return new List(nchildren);
		}
		
		public Compound clone() {
			return new List(Arrays.copyOf(children,children.length));
		}		
	}
	
	public static final int K_VOID = -1;
	public static final int K_INT = -2;
	public static final int K_STRING = -3;
	public static final int K_LIST = -4;
	public static final int K_SET = -5;
	public static final int K_FREE = -6;
	
	
	/**
	 * The following constant is used simply to prevent unnecessary memory
	 * allocations.
	 */
	public static final int[] NOCHILDREN = new int[0];
	public static final List EMPTY_LIST = new List(NOCHILDREN);
	public static final Set EMPTY_SET = new Set(NOCHILDREN);
}
