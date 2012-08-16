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
	
	/**
	 * The number of used slots in the states array.
	 */
	private int length;
	
	/**
	 * The array of automaton states
	 */
	private State[] states;	// should not be public!
	
	/**
	 * Describes the possible layouts of the used-defined states.
	 */
	private final Type.Term[] schema;
	
	public Automaton(Type.Term[] schema, State... states) {
		this.states = states;
		this.length = states.length;
		this.schema = schema;
	}

	public Automaton(Type.Term[] schema, List<State> states) {
		int statesSize = states.size();
		this.states = new State[statesSize];
		for(int i=0;i!=statesSize;++i) {
			this.states[i] = states.get(i);
		}
		this.length = states.size();
		this.schema = schema;
	}
	
	public Automaton(Automaton automaton) {
		this.length = automaton.length;
		this.states = new State[automaton.states.length];
		for(int i=0;i!=states.length;++i) {
			states[i] = automaton.states[i].clone();			
		}
		this.schema = automaton.schema;
	}
	
	public int size() {
		return length;
	}
	
	/**
	 * Determine the hashCode of a type.
	 */
	public int hashCode() {
		int r = 0;
		for (int i = 0; i != length; ++i) {
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
			if(a.length != length) {
				return false;
			}
			for(int i=0;i!=length;++i) {
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
		for (int i = 0; i != length; ++i) {
			if (i != 0) {
				r = r + ", ";
			}
			Automaton.State state = states[i];
			r = r + "#";
			r = r + i;
			if (state instanceof Term) {
				Term t = (Term) state;
				r = r + schema[t.kind - K_FREE].name + "(" + t.contents + ")";
			} else {
				r = r + state.toString();
			}
		}		
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
		public final int contents;
		
		public Term(int kind, int data) {
			super(kind);
			this.contents = data;
		}
		
		public Term clone() {
			return new Term(kind,contents);
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
	public static final class Item extends State {
		public final Object payload;

		public Item(int kind, Object data) {
			super(kind);
			this.payload = data;
		}

		public Item clone() {
			return new Item(kind, payload);
		}

		public boolean equals(final Object o) {
			if (o instanceof Item) {
				Item t = (Item) o;
				return kind == t.kind && payload.equals(t.payload);
			}
			return false;
		}

		public int hashCode() {
			return payload.hashCode() * kind;
		}
		
		public String toString() {
			return payload.toString();
		}
	}
	
	/**
	 * Represents a sequence of zero or more object in the automaton.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Compound extends State {
		public final int[] children;

		public Compound(int kind, int...children) {
			super(kind);
			if(kind != K_LIST || kind != K_SET || kind != K_BAG) {
				throw new IllegalArgumentException("invalid compound kind");
			}
			this.children = children;
		}

		public Compound clone() {
			return new Compound(kind, Arrays.copyOf(children,children.length));
		}

		public boolean equals(final Object o) {
			if (o instanceof Compound) {
				Compound t = (Compound) o;
				return kind == t.kind && Arrays.equals(children,t.children);
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
					r = "(";
					break;
				case K_SET:
					r = "{";
					break;
				case K_BAG:
					r = "[";
					break;
			}
			
			for(int i=0;i!=children.length;++i) {
				if(i != 0) { r += ","; }
				r += children[i];
			}
			
			switch(kind) {
				case K_LIST:
					r = ")";
					break;
				case K_SET:
					r = "}";
					break;
				case K_BAG:
					r = "]";
					break;
			}
			
			return r;
		}
	}
	
	/**
	 * The following constant is used simply to prevent unnecessary memory
	 * allocations.
	 */
	public static final int[] NOCHILDREN = new int[0];

	public static final int K_INT = 0;
	public static final int K_STRING = 1;
	public static final int K_LIST = 2;
	public static final int K_SET = 3;
	public static final int K_BAG = 4;
	
	public static final int K_FREE = 10;
}
