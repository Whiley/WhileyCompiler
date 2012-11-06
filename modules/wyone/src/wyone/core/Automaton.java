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
			Automaton.State state = automaton.states[i];
			if(state != null) {
				states[i] = state.clone();
			}
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
	 * <b>NOTE:</b> all references valid prior to this call remain valid.
	 * </p>
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
			if(compound.length == 0) {
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
	 * necessary to use a marker.
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
		if(from != to) {
			int[] map = new int[nStates];
			for(int i=0;i!=map.length;++i) { map[i] = i; }
			map[from] = to;
			rewrite(map);			
		} 
	}
	
	public void rewrite(int[] map) {
		for (int from = 0; from != map.length; ++from) {
			int to = map[from];
			if (from != to) {
				states[from] = null;
			}
		}
		remap(map);
	}
	
	/**
	 * <p>
	 * Clone the source object whilst replacing all reachable instances of the
	 * search term with the replacement term. In the case of no matches, the
	 * original source term is returned.
	 * </p>
	 * 
	 * <p>
	 * <b>NOTE:</b> all references valid prior to this call remain valid.
	 * However, this call may result in garbage being left in the automaton
	 * can be removed by calling <code>compact()</code>.
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
		Arrays.fill(binding, -1); // all states unvisited
		return substitute(source,search,replacement,binding);
	}
	
	private int substitute(int source, int search, int replacement, int[] binding) {
		// first, check with this is the term being replaced
		if(source == search) {
			return replacement;
		} else if(source < 0) {
			return source;
		}

		// second, check whether we've already visited this term (in which case,
		// we can just reused the previously determine result).
		int index = binding[source];
		if(index >= 0) {
			// already processed this one, no need to go further.
			return index;
		}
		
		// third, exam the state in question and continue recursing (if applicable)
		State state = states[source];
		int nSource = source;
		if (state instanceof Automaton.Constant) {
			// fall through as no change possible
		} else if (state instanceof Automaton.Term) {
			Automaton.Term term = (Automaton.Term) state;
			int contents = term.contents;
			int nContents = substitute(contents, search, replacement, binding);
			if (contents != nContents) {
				// contents has changed, so we need to clone ourself.
				nSource = add(new Automaton.Term(term.kind, nContents));
			}
		} else if (state instanceof Automaton.Compound) {
			Automaton.Compound term = (Automaton.Compound) state;
			int[] children = term.children;
			int[] nChildren = null;
			for (int i = 0; i != term.length; ++i) {
				int child = children[i];
				int nChild = substitute(child, search, replacement, binding);
				if (nChildren != null) {
					nChildren[i] = nChild;
				} else if (child != nChild) {
					nChildren = new int[term.length];
					System.arraycopy(children, 0, nChildren, 0, i);
					nChildren[i] = nChild;
				}
			}
			if(nChildren != null) {
				// something changed, so clone ourself.
				switch(term.kind) {
					case K_LIST:
						term = new Automaton.List(nChildren);
						break;
					case K_BAG:
						term = new Automaton.Bag(nChildren);
						break;					
					case K_SET:
						term = new Automaton.Set(nChildren);
						break;
				}				
				nSource = add(term);
			}
		}
		
		// default case indicates no change
		binding[source] = nSource;
		return nSource;
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
	 * <p>
	 * Remove all garbage from the automaton, and generally compact the
	 * automaton's representation.
	 * </p>
	 * <p>
	 * <b>NOTE:</b> all references which were valid beforehand may now be
	 * invalidated. In order to preserve a reference through a rewrite, it is
	 * necessary to use a marker.
	 * </p>
	 */
	public void compact() {
		
		// temporary storage
		BitSet visited = new BitSet(nStates);
		int[] map = new int[nStates];
		
		// first, identify garbage
		int i,j=0;

		for(i=0;i!=nRoots;++i) {
			int root = roots[i];
			if(root >= 0) {
				visited.set(root);
				map[j++] = root;
			}
		}
		
		while(j > 0) {
			i = map[--j];
			State state = states[i];
			if(state instanceof Term) {
				Term t = (Term) state;
				int t_contents = t.contents;
				if(t_contents >= 0 && !visited.get(t_contents)) {
					visited.set(t_contents);
					map[j++] = t_contents;
				}
			} else if(state instanceof Compound) {
				Compound c = (Compound) state;
				int[] children = c.children;
				for(int k=0;k!=c.length;++k) {
					int kth = children[k];
					if(kth >= 0 && !visited.get(kth)) {
						visited.set(kth);
						map[j++] = kth;
					}
				}
			}
		}
		
		// second, shift slots down to compact them		
		for(i=0,j=0;i!=nStates;++i) {
			if(visited.get(i)) {
				map[i] = j;
				states[j++] = states[i];				
			}
		}
		nStates = j;
		
		// finally, update mappings and roots
		for(i=0;i!=nStates;++i) {
			states[i].remap(map);
		}	
		for(i=0;i!=nRoots;++i) {
			int root = roots[i];
			if(root >= 0) {
				roots[i] = map[root];
			}
		}
	}
	
	/**
	 * Copy into this automaton all states in the given automaton reachable from
	 * a given root state.
	 * 
	 * @param root
	 *            --- root index to begin copying from.
	 * @param automaton
	 *            --- other automaton to copye states from.
	 * @return
	 */
	public int copyFrom(int root, Automaton automaton) {
		if (root < 0) {
			// nothing to do in this case, since it's a predefined "virtual"
			// state.
			return root;
		}
		
		Automaton.State state = automaton.get(root);
		if(state instanceof Automaton.Constant) {
			Automaton.Constant<?> constant = (Automaton.Constant<?>) state;
			root = add(constant); // no need to clone since immutable.
		} else if(state instanceof Automaton.Term) {
			Automaton.Term term = (Automaton.Term) state;
			if(term.contents != Automaton.K_VOID) {
				root = copyFrom(term.contents, automaton);
				root = add(new Automaton.Term(term.kind,root));
			} else {
				root = add(term); // no need to clone since immutable.
			}
		} else {
			Automaton.Compound compound = (Automaton.Compound) state;
			int[] children = compound.children;
			int[] nchildren = new int[compound.children.length];
			for(int i=0;i!=children.length;++i) {
				nchildren[i] = copyFrom(children[i],automaton);				
			}
			switch(compound.kind) {
			case K_LIST:
				root = add(new Automaton.List(nchildren));
				break;
			case K_BAG:
				root = add(new Automaton.Bag(nchildren));
				break;
			case K_SET:
				root = add(new Automaton.Set(nchildren));
				break;
			default:
				throw new RuntimeException("unreachable code reached");
				
			}
		}
		
		return root;
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
				if(si != null && !states[i].equals(ci)) {
					return false;
				} else if(si == null && ci != null) {
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
	 * Applying a mapping from "old" vertices to "new" vertices across the whole
	 * object space. Thus, any references to an "old" vertex is replaced with
	 * its corresponding "new" vertex (according to the given map). In some
	 * cases, this will cause follow-on remappings as some existing structures
	 * may collapse down.
	 * 
	 * @param map
	 */
	private void remap(int[] map) {
		int[] delta = new int[nStates];
		int nChanged = 0;
		boolean changed = true;
		
		while(changed) {
			changed = false;

			for(int i=0;i!=nStates;++i) {
				State state = states[i];
				if(state != null) {
					if(state.remap(map)) {
						changed = true;
						delta[nChanged++] = i;
					}
				}
			}
			
			// update roots
			for(int i=0;i!=nRoots;++i) {
				int root = roots[i];
				if(root >= 0) {
					roots[i] = map[root];
				}			
			}	
			
			if(changed) {								
				// now process changes by looking for equivalent states
				for(int k=0;k!=nChanged;++k) {
					int min = Integer.MAX_VALUE; // least equivalent state
					State ith = states[delta[k]];
					if(ith == null) { continue; }
					// first, determine "least equivalent state"
					for(int j=0;j!=nStates;++j) {
						State jth = states[j];
						if(ith.equals(jth)) {					
							if(j < min) {
								min = j;
							} else {
								states[j] = null;
								map[j] = min;		
							}
						}
					}
				}
				
				nChanged = 0;
			}
		}		
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
