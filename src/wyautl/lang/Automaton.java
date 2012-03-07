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

package wyautl.lang;

import java.util.*;

/**
 * <p>
 * A finite-state automaton for representing Whiley types. This is a machine for
 * accepting matching inputs of a given language. An automaton is a directed
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
	public State[] states;	// should not be public!
	
	public Automaton(State... states) {
		this.states = states;
	}

	public Automaton(List<State> states) {
		int statesSize = states.size();
		this.states = new State[statesSize];
		for(int i=0;i!=statesSize;++i) {
			this.states[i] = states.get(i);
		}		
	}
	
	public Automaton(Automaton automaton) {
		this.states = new State[automaton.states.length];
		for(int i=0;i!=states.length;++i) {
			states[i] = new State(automaton.states[i]);
		}
	}
	
	public int size() {
		return states.length;
	}
	
	/**
	 * Determine the hashCode of a type.
	 */
	public int hashCode() {
		int r = 0;
		for(State c : states) {
			r = r + c.hashCode();
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
			State[] cs = ((Automaton) o).states;
			if(cs.length != states.length) {
				return false;
			}
			for(int i=0;i!=cs.length;++i) {
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
		for(int i=0;i!=states.length;++i) {
			if(i != 0) {
				r = r + ", ";
			}
			Automaton.State state = states[i];
			int kind = state.kind;
			r = r + "#";
			r = r + i;
			r = r + "(";						
			r = r + kind;
			
			if(state.data != null) {
				r = r + "," + state.data;
			}
			r = r + ")";
			if(state.deterministic) {
				r = r + "[";
			} else {
				r = r + "{";
			}
			boolean firstTime=true;
			for(int c : state.children) {
				if(!firstTime) {
					r = r + ",";
				}
				firstTime=false;
				r = r + c;
			}
			if(state.deterministic) {
				r = r + "]";
			} else {
				r = r + "}";
			}
		}		
		return r;
	}

	/**
	 * Represents a state in an automaton. Each state has a kind, along with zero
	 * or more children and an (optional) supplementary data item.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class State {
		public int kind;
		public int[] children;
		public boolean deterministic;
		public Object data;

		/**
		 * Construct a deterministic state with no children and no supplementary data.
		 * 
		 * @param kind
		 *            --- State kind (must be positive integer).
		 */
		public State(int kind) {
			this(kind,null,true,NOCHILDREN);
		}
		
		/**
		 * Construct a deterministic state with no supplementary data.
		 * 
		 * @param kind
		 *            --- State kind (must be positive integer).
		 * @param children
		 *            --- Array of child indices.
		 */
		public State(int kind, int... children) {
			this(kind,null,true,children);
		}

		/**
		 * Construct a state with no supplementary data.
		 * 
		 * @param kind
		 *            --- State kind (must be positive integer).
		 * @param deterministic
		 *            --- Indicates whether node should be treated as
		 *            deterministic or not.
		 * @param children
		 *            --- Array of child indices.
		 */
		public State(int kind, boolean deterministic, int... children) {
			this(kind,null,deterministic,children);
		}
		
		/**
		 * Construct a state with children and supplementary data.
		 * 
		 * @param kind
		 *            --- State kind (must be positive integer).
		 * @param data
		 *            --- Supplementary data store with state.
		 * @param deterministic
		 *            --- Indicates whether node should be treated as
		 *            deterministic or not.
		 * @param children
		 *            --- Array of child indices.
		 */
		public State(int kind, Object data, boolean deterministic, int... children) {
			this.kind = kind;
			this.children = children;
			this.data = data;
			this.deterministic = deterministic;
		}
		
		public State(State state) {
			kind = state.kind;
			children = Arrays.copyOf(state.children, state.children.length);
			data = state.data;
			deterministic = state.deterministic;
		}
		
		public boolean equals(final Object o) {
			if (o instanceof State) {
				State c = (State) o;
				// in the following, we only need to check data != null for this
				// node as both nodes have the same kind and, hence, this.data
				// != null implies c.data != null.
				return kind == c.kind && deterministic == c.deterministic
						&& Arrays.equals(children, c.children)
						&& (data == null || data.equals(c.data));
			}
			return false;
		}

		public int hashCode() {
			int r = Arrays.hashCode(children) + kind;
			if (data != null) {
				return r + data.hashCode();
			} else {
				return r;
			}
		}
	}

	/**
	 * The following constant is used simply to prevent unnecessary memory
	 * allocations.
	 */
	public static final int[] NOCHILDREN = new int[0];

}
