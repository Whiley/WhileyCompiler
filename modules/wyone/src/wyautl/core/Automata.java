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

import java.util.Arrays;

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
	 *            are (cyclic) headers States which have a final value of '0'
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
	 * Eliminate any states which are unreachable from a root state.
	 * 
	 * @param automaton
	 * @param tmp
	 */
	final static void eliminateUnreachableStates(Automaton automaton, int[] tmp) {
		Arrays.fill(tmp,0);
		// first, visit all nodes
		for (int i = 0; i != automaton.nRoots(); ++i) {
			int root = automaton.getRoot(i);
			if (root >= 0) {
				Automata.traverse(automaton, root, tmp);
			}
		}
		for (int i = 0; i != automaton.nStates(); ++i) {
			if (tmp[i] == 0) {
				automaton.set(i,null);
			}
		}
	}
	
	/**
	 * Given a relation identifying equivalence classes determine, for each, the
	 * mapping from states to their representatives. 
	 */
	final static int determineRepresentativeStates(Automaton automaton,
			BinaryMatrix equivs, int[] mapping) {
		int oldSize = automaton.nStates();
		int newSize = 0;
		for (int i = 0; i != oldSize; ++i) {
			Automaton.State i_state = automaton.get(i);
			if(i_state != null) {
				int classRep = i;			
				for (int j = 0; j < i; ++j) {
					if (equivs.get(i, j)) {
						classRep = j;
						break;
					}
				}
				if (i == classRep) {
					int cid = newSize++;
					mapping[i] = cid;
					automaton.set(cid, i_state);
				} else {
					mapping[i] = mapping[classRep];
					automaton.set(i, null);
				}
			}
		}
		return newSize;
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
}
