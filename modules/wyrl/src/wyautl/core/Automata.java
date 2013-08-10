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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

import wyautl.core.Automaton.State;
import wyautl.util.BinaryMatrix;

/**
 * Contains various helper functions for working with automata.
 * 
 * @author David J. Pearce
 * 
 */
public class Automata {
	
	/**
	 * Extract all states reachable from a given state in an automaton and load
	 * them onto the states array, whilst retaining their original ordering.
	 * 
	 * @param automaton
	 * @param root
	 * @param states
	 */
	public static int extract(Automaton automaton, int root,
			java.util.List<Automaton.State> states) {
		if(root < 0) {
			// nothing to extract
			return root;
		} else {
			int start = states.size();
			int[] marking = new int[automaton.nStates()];
			traverse(automaton, root, marking);
			for (int i = 0; i != marking.length; ++i) {
				if (marking[i] > 0) {
					marking[i] = states.size();
					states.add(automaton.get(i).clone());
				}
			}
			for (int i = start; i != states.size(); ++i) {
				states.get(i).remap(marking);
			}
			return marking[root];
		}
	}
	
	/**
	 * Visit all states reachable from a given starting state in the given
	 * automaton. In doing this, states which are visited are marked and,
	 * furthermore, those which are "headers" are additionally identified. A
	 * header state is one which is the target of a back-edge in the directed
	 * graph reachable from the start state.
	 * 
	 * @param automaton
	 *            --- automaton to traverse.
	 * @param start
	 *            --- state to begin traversal from.
	 * @param marking
	 *            --- initially, states are marked with '0' which subsequently
	 *            turns positive to indicate they were visited. States assigned
	 *            a final value of 1 were visited, but are not headers. States
	 *            assigned a final value of 2 were visited and are (acyclic)
	 *            headers. States assigned a final value of 3 were visited and
	 *            are (cyclic) headers. States which have a final value of '0'
	 *            were not visited. Finally, states which were initially marked
	 *            with '-1' are not traversed (including their subtrees) and
	 *            retain this value in the final marking.
	 * @return
	 */
	public static void traverse(Automaton automaton, int start, int[] marking) {
		if (start < 0) {
			return;
		}
		int header = marking[start];
		if (header == 4) {
			// We have reached a node which was already visited, and is
			// currently on the stack. Therefore, this
			// node is a cyclic header and should be marked as such.
			marking[start] = 6; // (which reduces to 3 when removed from stack)			
		} else if (header == 1) {
			// We have reached a node which was already visited, but is not
			// currently on the stack. Therefore, this
			// node is an acyclic header and should be marked as such.
			marking[start] = 2;
			return; // done
		} else if (header > 1 || header == Automaton.K_VOID) {
			return; // nothing to do, as either already marked as a header or
					// initially indicated as not to traverse.
		} else {
			marking[start] = 4;
			Automaton.State state = automaton.get(start);
			if (state instanceof Automaton.Term) {
				Automaton.Term term = (Automaton.Term) state;
				if (term.contents != Automaton.K_VOID) {
					traverse(automaton, term.contents, marking);
				}
			} else if (state instanceof Automaton.Collection) {
				Automaton.Collection compound = (Automaton.Collection) state;
				int[] children = compound.children;
				for (int i = 0; i != compound.length; ++i) {
					traverse(automaton, children[i], marking);
				}
			} 
			marking[start] -= 3;
		}
	}
	
	/**
	 * Visit all states reachable from a given starting state in the given
	 * automaton. This yields an ordering of the visited nodes (which is in
	 * <i>reverse post-order</i>).
	 * 
	 * @param automaton
	 *            --- automaton to traverse.
	 * @param start
	 *            --- state to begin traversal from.
	 * @return
	 */
	public static int[] topologicalSort(Automaton automaton, int start) {
		BitSet visited = new BitSet(automaton.nStates());		
		IntStack stack = new IntStack(automaton.nStates());
		topologicalSort(automaton,start,visited,stack);
		
		int[] result = stack.items;
		if(stack.size != result.length) {
			result = Arrays.copyOf(result, stack.size);
		}
		return result;
	}
	
	private static void topologicalSort(Automaton automaton, int node,
			BitSet visited, IntStack stack) {		
		if (node < 0) {
			return;
		}		
		if (!visited.get(node)) {
			// we've not visited this node before.
			visited.set(node,true);
			Automaton.State state = automaton.get(node);
			if (state instanceof Automaton.Term) {
				Automaton.Term term = (Automaton.Term) state;
				if (term.contents != Automaton.K_VOID) {
					topologicalSort(automaton, term.contents, visited, stack);
				}
			} else if (state instanceof Automaton.Collection) {
				Automaton.Collection compound = (Automaton.Collection) state;
				int[] children = compound.children;
				for (int i = 0; i != compound.length; ++i) {
					topologicalSort(automaton, children[i], visited, stack);
				}
			}
			stack.push(node);
		}
	}
	
	private final static class IntStack {
		public final int[] items; 
		public int size;
		
		public IntStack(int size) {
			this.items = new int[size];
		}
		
		public void push(int item) {
			items[size++] = item;
		}
	}
	
	/**
	 * Determine whether the subgraph reachable from a given node in this
	 * automaton is acyclic or not. It can be useful to know this, since some
	 * algorithms are more efficient on acyclic automata.
	 * 
	 * @param automaton
	 *            --- automaton to traverse.
	 * @param start
	 *            --- state to begin traversal from.
	 * @return
	 */
	public static boolean isAcyclic(Automaton automaton, int start) {
		BitSet visited = new BitSet(automaton.nStates());
		BitSet onStack = new BitSet(automaton.nStates());		
		return isAcyclic(start, onStack, visited, automaton);
	}
	
	/**
	 * Helper algorithm. This is similar to the well-known algorithm for finding
	 * strongly connected components. The main difference is that it doesn't
	 * actually return the components. For reference, see this paper:
	 * 
	 * <ul>
	 * <li><b>An Improved Algorithm for Finding the Strongly Connected
	 * Components of a Directed Graph</b>. David J. Pearce, Technical Report,
	 * 2005.</li>
	 * </ul>
	 * 
	 * @param index
	 *            --- current node being visited.
	 * @param onStack
	 *            --- indicates which nodes are on the current path from the
	 *            root.
	 * @param visited
	 *            --- indicates which nodes have been visited (but may not be on
	 *            the current path).
	 * @param automaton
	 *            --- the automaton being traversed.
	 * @return --- true if the automaton is concrete.
	 */
	private static boolean isAcyclic(int index, BitSet onStack, BitSet visited,
			Automaton automaton) {

		if (onStack.get(index)) {
			return false; // found a cycle!
		}

		if (visited.get(index)) {
			// Ok, we've traversed this node before and it checked out OK.
			return true;
		}

		visited.set(index);
		onStack.set(index);

		Automaton.State state = automaton.get(index);
		if (state instanceof Automaton.Term) {
			Automaton.Term term = (Automaton.Term) state;
			if (term.contents != Automaton.K_VOID) {
				if (!isAcyclic(term.contents, onStack, visited, automaton)) {
					return false;
				}
			}
		} else if (state instanceof Automaton.Collection) {
			Automaton.Collection compound = (Automaton.Collection) state;
			int[] children = compound.children;
			for (int i = 0; i != compound.length; ++i) {
				if (!isAcyclic(children[i], onStack, visited, automaton)) {
					return false;
				}
			}
		}

		onStack.set(index, false);

		return true;
	}	
	
	/**
	 * Check whether one state is reachable from another in a given automaton.
	 * This employs a standard depth-first traversal of the automaton from the
	 * given node. An array of temporary storage is used to record which nodes
	 * have been previously visited in order to protected against infinite
	 * recursion in the presence of cyclic automata.
	 * 
	 * @param start
	 *            --- index to begin the traversal from.
	 * @param storage
	 *            --- temporary storage which must have at least
	 *            <code>automaton.nStates()</code> elements. All states to be
	 *            considered during the traversal must be marked with zero in
	 *            this array. All states visited during the traversal will be
	 *            marked with one afterwards.
	 * @return
	 */
	public static boolean reachable(Automaton automaton, int start, int search,
			int[] storage) {
		if (start == search) {
			return true;
		} else if (start >= 0 && storage[start] == 0) {
			// this root not yet visited.
			Automaton.State state = automaton.get(start);
			storage[start] = 1; // visited

			if (state instanceof Automaton.Term) {
				Automaton.Term term = (Automaton.Term) state;
				if (term.contents != Automaton.K_VOID) {
					if (reachable(automaton, term.contents, search, storage)) {
						return true;
					}
				}
			} else if (state instanceof Automaton.Collection) {
				Automaton.Collection compound = (Automaton.Collection) state;
				int[] children = compound.children;
				for (int i = 0; i != compound.length; ++i) {
					if (reachable(automaton, children[i], search, storage)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Eliminate any states which are unreachable from a root state between the
	 * given start and end indices.
	 * 
	 * @param automaton
	 * @param tmp
	 */
	public final static int[] eliminateUnreachableStates(Automaton automaton,
			int start, int end, int[] tmp) {
		if(tmp.length < automaton.nStates()) {
			tmp = new int[automaton.nStates()*2];
		} else {
			Arrays.fill(tmp,0);
		}
		// first, visit all nodes
		for (int i = 0; i != automaton.nRoots(); ++i) {
			int root = automaton.getRoot(i);
			if (root >= 0) {
				Automata.traverse(automaton, root, tmp);
			}
		}
		for (int i = 0; i != automaton.nStates(); ++i) {
			if (tmp[i] == 0 && i >= start && i < end) {
				automaton.set(i,null);
			}
		}
		return tmp;
	}
	
	/**
	 * Given a relation identifying equivalence classes determine, for each, the
	 * mapping from states to their representatives. This function does not
	 * modify the automaton.
	 */
	final static void determineRepresentativeStates(Automaton automaton,
			BinaryMatrix equivs, int[] mapping) {
		final int size = automaton.nStates();
		
		for (int i = 0; i != size; ++i) {
			Automaton.State i_state = automaton.get(i);
			if(i_state != null) {
				int classRep = i;
				// determine the unique representative for this equivalence
				// class.
				for (int j = 0; j < i; ++j) {
					if (equivs.get(i, j)) {
						classRep = j;
						break;
					}
				}
				// map this state to the unique representative (which may be
				// itself if it is the unique rep).
				mapping[i] = classRep;				
			}
		}
	}
	
	/**
	 * Determine which states are equivalent using a binary matrix of size N*N,
	 * where N is the number of states in the given automaton. This method is
	 * part of the minimisation process.
	 * 
	 * @param automaton
	 *            --- The automaton being minimised.
	 * @param equivs
	 *            --- Binary matrix comparing every state in automaton with
	 *            every other state for equivalence.
	 */
	final static void determineEquivalenceClasses(Automaton automaton,BinaryMatrix equivs) {
		boolean changed = true;
		int size = automaton.nStates();
		
		while (changed) {
			changed = false;
			for (int i = 0; i < size; ++i) {
				for (int j = i + 1; j < size; ++j) {					
					if(equivs.get(i,j)) {
						// no need to explore nodes which are already known to
						// be not equivalent.
						boolean b = equivalent(automaton, equivs, i, j);						
						equivs.set(i, j, b);
						equivs.set(j, i, b);
						changed |= !b;
					}
				}
			}
		}
	}
	
	/**
	 * Check whether two states are equivalent in a given automaton and current set of equivalences.
	 */
	private final static boolean equivalent(Automaton automaton, BinaryMatrix equivs, int i, int j) {
		Automaton.State is = automaton.get(i);
		Automaton.State js = automaton.get(j);
		if(is == null || js == null) {
			return false;
		} else if(is.kind != js.kind) {
			return false;
		} else if(is instanceof Automaton.Constant) {
			Automaton.Constant<?> ic = (Automaton.Constant<?>) is;
			Automaton.Constant<?> jc = (Automaton.Constant<?>) js;
			return ic.value.equals(jc.value);
		} else if(is instanceof Automaton.Term) {
			Automaton.Term it = (Automaton.Term) is;
			Automaton.Term jt = (Automaton.Term) js;
			int it_contents = it.contents;
			int jt_contents = jt.contents;
			if(it_contents < 0 || jt_contents < 0) {
				return it_contents == jt_contents;
			} else {
				return equivs.get(it_contents, jt_contents);
			}
		} else if(is instanceof Automaton.List) {
			Automaton.List il = (Automaton.List) is;
			Automaton.List jl = (Automaton.List) js;
			int il_size = il.size();
			int jl_size = jl.size();
			if(il_size != jl_size) {
				return false;
			}
			int[] il_children = il.children;
			int[] jl_children = jl.children;
			for (int k = 0; k != il_size; ++k) {
				int il_child = il_children[k];
				int jl_child = jl_children[k];
				if (il_child < 0 || jl_child < 0) {
					// virtual node case 
					if(il_child != jl_child) {
						return false;
					}
				} else if (!equivs.get(il_child, jl_child)) {
					return false;
				}
			}
			return true;
		} else {
			// this is the most expensive case (sadly)
			Automaton.Collection ic = (Automaton.Collection) is;
			Automaton.Collection jc = (Automaton.Collection) js;
			int ic_size = ic.size();
			int jc_size = jc.size();
			if (ic instanceof Automaton.Bag && ic_size != jc_size) {
				return false;
			} 
			int[] ic_children = ic.children;
			int[] jc_children = jc.children;
			// First, check every node in s1 has equivalent in s2
			for(int k=0;k!=ic_size;++k) {
				int ic_child = ic_children[k];
				boolean matched = false;
				for(int l=0;l!=jc_size;++l) {
					int jc_child = jc_children[l];					
					if (ic_child == jc_child
							|| (ic_child >= 0 && jc_child >= 0 && equivs.get(
									ic_child, jc_child))) {
						matched = true;
						break;
					}
				}
				if(!matched) {
					return false;
				}
			}

			// Second, check every node in s2 has equivalent in s1
			for(int k=0;k!=jc_size;++k) {
				int jc_child = jc_children[k];
				boolean matched = false;
				for(int l=0;l!=ic_size;++l) {
					int ic_child = ic_children[l];
					if (ic_child == jc_child
							|| (ic_child >= 0 && jc_child >= 0 && equivs.get(
									ic_child, jc_child))) {
						matched = true;
						break;
					}
				}
				if(!matched) {
					return false;
				}
			}
			return true;
		}				
	}
	
	/**
	 * <p>
	 * This algorithm extends all of the current morphisms by a single place.
	 * What this means, is that all of children of the state under consideration
	 * will be placed after this. In the case of non-deterministic states, this
	 * may give rise to a number of equivalent extensions to consider.
	 * <p>
	 * 
	 * @param index
	 *            --- index in morphism to extend. A state must already have
	 *            been placed at its index, but some or all of its children will
	 *            not be placed yet.
	 * @param free
	 *            --- the first free available index in the morphism (note free
	 *            > index).
	 * @param candidates
	 *            --- this is the list of candidate morphisms currently being
	 *            explored. An invariant of this algorithm is that all
	 *            candidates are equivalent under the <code>lessThan()</code>
	 *            relation.
	 * @param automaton
	 *            --- the automaton being canonicalised
	 */
	public static void extend(int index, ArrayList<Morphism> candidates,
			Automaton automaton) {
		
		// Please note, this algorithm is really not very efficient. There is
		// quite a lot more pruning that could be done!
		
		int size = candidates.size();
		for(int i=0;i!=size;++i) {
			Morphism candidate = candidates.get(i);			
			extend(index,candidate,candidates,automaton);
		}
		
		prune(candidates, automaton);
	}

	private static void extend(int index, Morphism candidate,
			ArrayList<Morphism> candidates, Automaton automaton) {
		
		State s = automaton.get(candidate.i2n[index]);
		
		if(s instanceof Automaton.Term) {
			Automaton.Term t = (Automaton.Term) s; 
			if(!candidate.isAllocated(t.contents)) {
				candidate.allocate(t.contents);
			}
		} else if(s instanceof Automaton.List) { 						
			// easy, deterministic collection case
			Automaton.List l = (Automaton.List) s;
			
			for(int child : l.children) {
				if(!candidate.isAllocated(child)) {
					candidate.allocate(child);
				}
			}
		} else if(s instanceof Automaton.Collection) {
			// harder, non-deterministic collection case
			Automaton.Collection l = (Automaton.Collection) s; 
			
			// This loop is why the algorithm has exponential running time.			
			ArrayList<int[]> permutations = permutations(l.children,l.children.length);
			for(int i=0;i!=permutations.size();++i) {
				Morphism ncandidate;
				if((i+1) == permutations.size()) {
					// last one, so overwrite original
					ncandidate = candidate;
				} else {
					ncandidate = new Morphism(candidate);
					candidates.add(ncandidate);
				}				
				int[] permutation = permutations.get(i);				
				for(int child : permutation) {
					// GAH --- the following line is horrendously stupid. It's
					// guaranteed to generate idendical candidates in the case
					// of a child which is already allocated.
					if(!ncandidate.isAllocated(child)) {
						ncandidate.allocate(child);
					}
				}				
			}
		}
	}
	

	/**
	 * The purpose of this method is to prune the candidate list. In otherwords,
	 * to remove any candidates which are above some other candidate. 
	 * 
	 * @param size
	 * @param candidates
	 * @param automaton
	 */
	private static void prune(ArrayList<Morphism> candidates, Automaton automaton) {
		// this is really inefficient!
		// at a minimum, we could avoid recomputing lessThan twice for each candidate.		
		Morphism least = candidates.get(0); 
		for(Morphism candidate : candidates) {			
			if(lessThan(candidate,least,automaton)) {
				least = candidate;
			}
		}
		
		int diff = 0;
		for(int i=0;i!=candidates.size();++i) {
			Morphism candidate = candidates.get(i);
			if(lessThan(least,candidate,automaton)) {
				diff = diff + 1;
			} else {
				candidates.set(i-diff,candidate);
			}
		}
		
		// now actually remove those bypassed.
		int last = candidates.size();
		while(diff > 0) {
			candidates.remove(--last);
			diff = diff - 1;
		}
	}
	
	/**
	 * This function determines whether one morphism of a given automaton is
	 * <i>lexiographically</i> less than another. Starting from the root, we
	 * compare the states at each index in the morphisms. One state is below
	 * another if it has a lower kind, fewer children or its transitions are
	 * "below" those of the other.
	 * 
	 * @param morph1
	 *            --- Morphism to test if below or not.  
	 * @param morph2
	 *            --- Morphism to test if above or not.
	 * @param size
	 *            --- don't consider states above this.
	 * @param automaton
	 *            --- automaton being canonicalised.
	 * @return --- true if morph1 is less than morph2
	 */
	private static boolean lessThan(Morphism morph1, Morphism morph2,
			Automaton automaton) {
		
		int size = Math.min(morph1.free,morph2.free);
		
		for(int i=0;i!=size;++i) {					
			State s1 = automaton.get(morph1.i2n[i]);
			State s2 = automaton.get(morph2.i2n[i]);
			
			if(s1.kind < s2.kind) {
				return true;
			} else if(s1.kind > s2.kind) {
				return false;
			} 
			
			if(s1 instanceof Automaton.Constant) {
				Automaton.Constant c1 = (Automaton.Constant) s1;
				Automaton.Constant c2 = (Automaton.Constant) s2;
				Comparable o1 = (Comparable) c1.value;
				Comparable o2 = (Comparable) c2.value;				
				int c = o1.compareTo(o2);
				if(c != 0) {
					return c < 0;
				}
			} else if(s1 instanceof Automaton.Term) {				
				Automaton.Term t1 = (Automaton.Term) s1;
				Automaton.Term t2 = (Automaton.Term) s2;
				int t1child = t1.contents;
				int t2child = t2.contents;
				if(t1child >= 0 && t2child >= 0) {
					// non-virtual nodes
					t1child = morph1.n2i[t1child];
					t2child = morph2.n2i[t2child];
				} 						
				if(t1child < t2child) {
					return true;
				} else if(t1child > t2child) {
					return false;
				}				
			} else if(s1 instanceof Automaton.Collection) {
				Automaton.Collection c1 = (Automaton.Collection) s1;
				Automaton.Collection c2 = (Automaton.Collection) s2;
				int[] s1children = c1.children;
				int[] s2children = c2.children;				
				if(s1children.length < s2children.length) {
					return true;
				} else if(s1children.length > s2children.length) {
					return false;
				}
				
				int length = s1children.length;		
				
				if(s1.kind == Automaton.K_LIST) {			
					for(int j=0;j!=length;++j) {
						int s1child = s1children[j];
						int s2child = s2children[j];
						if(s1child >= 0 && s2child >= 0) {
							// non-virtual nodes
							s1child = morph1.n2i[s1child];
							s2child = morph2.n2i[s2child];
						} 						
						if(s1child < s2child) {
							return true;
						} else if(s1child > s2child) {
							return false;
						}						
					}									
				} else {
					// as usual, non-deterministic states are awkward
					
					// First, we must precalculate the shift value to account
					// for virtual nodes which have negative indices.
					// Essentially, we're looking for the lowest valued index to
					// use as the "shift".  
					int shift = 0;
					for(int j=0;j!=length;++j) {
						shift = Math.min(shift, s1children[j]);
						shift = Math.min(shift, s2children[j]);
					}
					
					// Second, we can now determine the "spectra" for these two
					// non-deterministic nodes. Using this spectra we'll
					// determine which is "less than" the other and, hence,
					// which should be allocated next.
					BitSet s1Visited = new BitSet(automaton.nStates() - shift);
					BitSet s2Visited = new BitSet(automaton.nStates() - shift);
					for(int j=0;j!=length;++j) {
						int s1child = s1children[j];
						int s2child = s2children[j];
						if(s1child >= 0) { s1child = morph1.n2i[s1child]; }						
						if(s2child >= 0) { s2child = morph2.n2i[s2child]; }
						if(s1child != Integer.MAX_VALUE) {				
							s1Visited.set(s1child - shift);
						}
						if(s2child != Integer.MAX_VALUE) {
							s2Visited.set(s2child - shift);
						}
					}
					
					// Finally, check spectra to see which is least.
					int s1cardinality = s1Visited.cardinality();
					int s2cardinality = s2Visited.cardinality();
					if(s1cardinality != s2cardinality) {					
						// greater cardinality means more allocated children.
						return s1cardinality > s2cardinality;
					}
					// Same number of allocated children, so perform
					// lexiographic check.
					int s1i = s1Visited.nextSetBit(0);
					int s2i = s2Visited.nextSetBit(0);
					while(s1i == s2i && s1i >= 0) {
						s1i = s1Visited.nextSetBit(s1i+1);
						s2i = s2Visited.nextSetBit(s2i+1);
					}
					if(s1i != s2i) {										
						return s1i < s2i;
					}
				}						
			} 
		}
		
		// Ok, they're identical thus far!
		return false;
	}
		
	/**
	 * A morphism maintains a mapping form nodes in the canonical form (indices)
	 * to nodes in the original automaton.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	static final class Morphism {
		final int[] i2n; // indices to nodes
		final int[] n2i; // nodes to indices
		int free;        // first available index
		
		public Morphism(int size, int root) {
			i2n = new int[size];
			n2i = new int[size];
			for(int i=0;i!=size;++i) {
				i2n[i] = Integer.MAX_VALUE;
				n2i[i] = Integer.MAX_VALUE;
			}
			free = 0;			
			allocate(root);
		}
		
		public Morphism(Morphism morph) {
			int size = morph.size();
			i2n = Arrays.copyOf(morph.i2n,size);
			n2i = Arrays.copyOf(morph.n2i,size);
			free = morph.free;
		}
		
		public boolean isAllocated(int node) {
			// We're assuming virtual nodes are always allocated.
			return node < 0 || n2i[node] != Integer.MAX_VALUE;
		}
		
		public void allocate(int node) {
			i2n[free] = node;
			n2i[node] = free++;
		}		
		
		public int size() {
			return i2n.length;
		}
		
		public String toString() {
			String r = "[";
			for(int i=0;i!=i2n.length;++i) {
				if(i != 0) {
					r = r + ", ";
				}
				if(i >= free) {
					r = r + "?";
				} else {
					r = r + i2n[i];
				}
			}
			return r + "]";
		}
	}
	
	/*
	 * The following provides a brute-force way of determining the canonical
	 * form. It's really really slow, but useful for testing.	
	 */
	public static Automaton bruteForce(Automaton automaton) {
		// remember, toplogical sort returns the reverse post order, so this means everything is "backwards".
		int root = automaton.getRoot(0);
		int[] rpo = topologicalSort(automaton,root);
		
		Morphism winner = null;
		ArrayList<int[]> permutations = permutations(rpo,rpo.length-1);
		for (int i=0;i!=permutations.size();++i) {
			int[] permutation = permutations.get(i);
			Morphism m = new Morphism(automaton.nStates(),root);			
			for(int j=permutation.length-1;j>=0;--j) {
				m.allocate(permutation[j]);				
			}
			if (winner == null || lessThan(m, winner, automaton)) {
				winner = m;
			}			
		}

		return map(automaton, winner.n2i);
	}
	
	/**
	 * <p>
	 * The following method produces every possible permutation of the give
	 * array. For example, if <code>children=[1,2,3]</code>, the returned list
	 * includes the following permutations:
	 * </p>
	 * 
	 * <pre>
	 * [1,2,3]
	 * [2,1,3]
	 * [1,3,2]
	 * [3,2,1]
	 * [2,3,1]
	 * [3,1,2]
	 * </pre>
	 * <p>
	 * <b>NOTE:</b> the number of possible permutations is factorial in the size
	 * of the input array. Therefore, <i>this can a very expensive
	 * operation</i>. Use with care!
	 * </p>
	 * 
	 * @param children
	 * @return
	 */
	public static ArrayList<int[]> permutations(int[] children, int end) {
		ArrayList<int[]> permutations = new ArrayList();		
		permutations(0,children,end,permutations);
		return permutations;
	}
	
	private static void permutations(int index, int[] permutation, int size, ArrayList<int[]> permutations) {				
		if(index == size) {			
			permutations.add(Arrays.copyOf(permutation, size));
		} else {
			int t1 = permutation[index];
			for(int i=index;i<size;++i) {
				int t2 = permutation[i];
				permutation[index] = t2;
				permutation[i] = t1;
				permutations(index+1,permutation,size,permutations);
				permutation[index] = t1;
				permutation[i] = t2;								
			}
		}
	}
	
	/**
	 * Reorder an automaton according to a given mapping, where nodes are
	 * relocated to positions given by the mapping. This is effectively an
	 * inplace map operation.
	 * 
	 * @param automaton
	 *            --- automaton, which is modified.
	 * @param mapping
	 *            --- maps node indices in original ordering to those in the new
	 *            ordering.
	 */
	public static void reorder(Automaton automaton, int[] mapping) {
		// now remap all the vertices according to giving binding
		State[] states = new State[automaton.nStates()];		
		for (int i = 0; i != states.length; ++i) {			
			Automaton.State state = automaton.get(i);
			state.remap(mapping);
			states[mapping[i]] = state;			
		}		
		for (int i = 0; i != states.length; ++i) {
			automaton.set(i,states[i]);
		}

		for (int i = 0; i != automaton.nRoots(); ++i) {
			int root = automaton.getRoot(i);
			if (root >= 0) {
				automaton.setRoot(i, mapping[root]);
			}
		}		
	}
	
	/**
	 * Generate a new automaton from a given automaton which is isomorphic to
	 * the original, but where nodes in the first have been relocated to
	 * positions given by a mapping.
	 * 
	 * @param automaton
	 *            --- original automaton, which is left unchanged.
	 * @param mapping
	 *            --- maps node indices in original automaton to those in
	 *            resulting automaton.
	 */
	public static Automaton map(Automaton automaton, int[] mapping) {
		// now remap all the vertices according to giving binding
		State[] states = new State[automaton.nStates()];		
		for (int i = 0; i != states.length; ++i) {			
			Automaton.State state = automaton.get(i).clone();
			state.remap(mapping);
			states[mapping[i]] = state;			
		}		
		
		Automaton r = new Automaton(states); 

		for (int i = 0; i != automaton.nRoots(); ++i) {
			int root = automaton.getRoot(i);
			if (root >= 0) {
				r.setRoot(i, mapping[root]);
			}
		}
		
		return r;
	}
}
