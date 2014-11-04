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

package wyautl_old.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;

import wyautl_old.lang.Automaton.State;
import wyautl_old.util.BinaryMatrix;

/**
 * <p>
 * This class provides various algorithms for manipulating automata. In
 * particular, the following main algorithms are provided:
 * </p>
 * <ul>
 * <li><b>Extraction.</b> This is used to extract one automaton out of another.</li>
 * <li><b>Minimisation.</b> This is used to eliminate equivalent states within
 * an automaton.</li>
 * <li><b>Canonicalisation.</b> This is used to convert an automaton into a
 * canonical form.</li>
 * </ul>
 *
 * @author David J. Pearce
 */
public final class Automata {

	/**
	 * <p>
	 * Check whether or not an automaton is "concrete". A concrete automaton
	 * cannot have recursive links or non-deterministic states.'
	 * </p>
	 */
	public static boolean isConcrete(Automaton automaton) {
		// First, check all states are deterministic
		for(int i=0;i!=automaton.size();++i) {
			State s = automaton.states[i];
			if(!s.deterministic) {
				return false;
			}
		}

		// Second, check for cycles (i.e. recursive links)
		BitSet visited = new BitSet(automaton.size());
		BitSet onStack = new BitSet(automaton.size());
		return isConcrete(0,onStack,visited,automaton);
	}

	/**
	 * Helper algorithm. This is similar to the well-known algorithm for finding
	 * strongly connected components. The main difference is that it doesn't
	 * actually return the components.
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
	private static boolean isConcrete(int index, BitSet onStack,
			BitSet visited, Automaton automaton) {

		if(onStack.get(index)) {
			return false; // found a cycle!
		}

		if (visited.get(index)) {
			// Ok, we've traversed this node before and it checked out OK.
			return true;
		}

		visited.set(index);
		onStack.set(index);

		State state = automaton.states[index];
		for(int child : state.children) {
			if(!isConcrete(child,onStack,visited,automaton)) {
				return false;
			}
		}

		onStack.set(index,false);

		return true;
	}

	/**
	 * <p>
	 * Traverse the automaton rooted at the given state and recursively extract
	 * all reachable states to produce a (potentially smaller) automaton.
	 * </p>
	 *
	 * <p>
	 * <b>NOTE:</b> one additional use-case for this method is to effectively
	 * "garbage collect" states in the automaton. That is, if you extract from
	 * the root of any automaton, you'll get an automaton consisting only of those
	 * nodes reachable from the root --- but, no others. Therefore, unreachable
	 * nodes (which can arise as a result of other automaton operations) are
	 * lost.
	 * </p>
	 *
	 *
	 * @param automaton
	 *            --- automaton to extract from
	 * @param root
	 *            --- state in automaton to begin extraction from
	 * @return --- extracted automaton.
	 */
	public static Automaton extract(Automaton automaton, int root) {
		// First, perform a depth-first search from the root.
		State[] nodes = automaton.states;
		ArrayList<Integer> extracted = new ArrayList<Integer>();
		extract(root,new BitSet(nodes.length),extracted,nodes);

		// Second, build up remapping
		int[] remap = new int[nodes.length];
		int i=0;
		for(int j : extracted) {
			remap[j]=i++;
		}
		// Third, apply remapping
		State[] newNodes = new State[extracted.size()];
		i=0;
		for(int j : extracted) {
			newNodes[i++] = remap(nodes[j],remap);
		}
		return new Automaton(newNodes);
	}


	public static void extractOnto(int index, Automaton automaton,
			ArrayList<Automaton.State> newNodes) {
		// First, perform a depth-first search from the root.
		State[] nodes = automaton.states;
		ArrayList<Integer> extracted = new ArrayList<Integer>();
		extract(index,new BitSet(nodes.length),extracted,nodes);

		// Second, build up remapping
		int[] remap = new int[nodes.length];
		int i=newNodes.size();
		for(int j : extracted) {
			remap[j]=i++;
		}
		// Third, apply remapping
		i=0;
		for(int j : extracted) {
			newNodes.add(remap(nodes[j],remap));
		}
	}


	/**
	 * The following method recursively extracts the subgraph rooted at
	 * <code>index</code> in the given graph using a depth-first search.
	 * Vertices in the subgraph are added to <code>extracted</code> in the order
	 * they are visited.
	 *
	 * @param index
	 *            --- the node to extract the subgraph from.
	 * @param visited
	 *            --- the set of vertices already visited
	 * @param extracted
	 *            --- the list of vertices that make up the subgraph which is
	 *            built by this method.
	 * @param graph
	 *            --- the graph.
	 */
	private final static void extract(int index, BitSet visited,
			ArrayList<Integer> extracted, State[] graph) {
		if(visited.get(index)) { return; } // node already visited}
		extracted.add(index);
		visited.set(index);
		State node = graph[index];
		for(int child : node.children) {
			extract(child,visited,extracted,graph);
		}
	}

	/**
	 * <p>
	 * This method minimises an automaton by removing equivalent states. Two
	 * states <code>s1</code> and <code>s2</code> are considered equivalent
	 * under the following conditions:
	 * </p>
	 * <ul>
	 * <li>They are both leaf nodes of the same kind with identical
	 * supplementary data.</li>
	 * <li>They are nodes of the same (sequential) kind whose children at each
	 * position are equivalent</li>
	 * <li>They are nodes of the same (non-sequential) kind where for each child
	 * in one, there is an equivalent child in the other and vice-versa.</li>
	 * </ul>
	 *
	 * @param automaton
	 *            --- automaton to minimise
	 * @return --- minimised automaton
	 */
	public final static Automaton minimise(Automaton automaton) {
		// First, determine equivalence classes
		BinaryMatrix equivs = new BinaryMatrix(automaton.size(),automaton.size(),true);
		determineEquivalenceClasses(equivs,automaton);

		// TODO: optimise the case when all equivalence classes have unit size.

		// Second, determine representative nodes for each equivalence class.
		int oldSize = automaton.size();
		int[] mapping = new int[oldSize];
		int newSize = 0;
		for(int i=0;i!=oldSize;++i) {
			int classRep = i;
			for(int j=0;j<i;++j) {
				if(equivs.get(i,j)) {
					classRep = j;
					break;
				}
			}
			if(i == classRep) {
				mapping[i] = newSize++;
			} else {
				mapping[i] = mapping[classRep];
			}
		}

		// Finally, reconstruct minimised automaton
		State[] oldStates = automaton.states;
		State[] newStates = new State[newSize];
		for (int i = 0; i != oldSize; ++i) {
			int classRep = mapping[i];
			if (newStates[classRep] == null) {
				// this node is unallocated
				newStates[classRep] = remap(oldStates[i], mapping);
			}
		}

		return new Automaton(newStates);
	}

	private final static void determineEquivalenceClasses(BinaryMatrix equivs,
			Automaton automaton) {
		boolean changed = true;
		int size = automaton.size();

		while (changed) {
			changed = false;
			for (int i = 0; i < size; ++i) {
				for (int j = i + 1; j < size; ++j) {
					if(equivs.get(i,j)) {
						// no need to explore nodes which are already known to
						// be not equivalent.
						boolean b = equivalent(i, j, equivs, automaton);
						equivs.set(i, j, b);
						equivs.set(j, i, b);
						changed |= !b;
					}
				}
			}
		}
	}

	/*
	 * Check whether two states are equivalent under the rules set out for
	 * minimisation above.
	 */
	private final static boolean equivalent(int i, int j, BinaryMatrix equivs, Automaton automaton) {
		State s1 = automaton.states[i];
		State s2 = automaton.states[j];

		// first, check supplementary data
		Object s1data = s1.data;
		Object s2data = s2.data;
		if(s1data == null) {
			 if(s2data != null) {
				 return false;
			 }
		} else {
			// following catches case where s2data == null as well
			if(!s1data.equals(s2data)) {
				return false;
			}
		}

		// second, check node kind and children, etc.
		if(s1.kind == s2.kind && s1.deterministic == s2.deterministic) {
			boolean deterministic = s1.deterministic;
			if(deterministic) {
				int[] s1children = s1.children;
				int[] s2children = s2.children;

				if(s1children.length != s2children.length) {
					return false;
				}

				int length = s1children.length;

				for(int k=0;k!=length;++k) {
					int s1child = s1children[k];
					int s2child = s2children[k];
					if(!equivs.get(s1child,s2child)) {
						return false;
					}
				}

				return true;
			} else {
				// non-deterministic (i.e. more expensive) case
				int[] s1children = s1.children;
				int[] s2children = s2.children;
				int s1length = s1children.length;
				int s2length = s2children.length;

				// First, check every node in s1 has equivalent in s2
				for(int k=0;k!=s1length;++k) {
					int s1child = s1children[k];
					boolean matched = false;
					for(int l=0;l!=s2length;++l) {
						int s2child = s2children[l];
						if(equivs.get(s1child,s2child)) {
							matched = true;
							break;
						}
					}
					if(!matched) {
						return false;
					}
				}

				// Second, check every node in s2 has equivalent in s1
				for(int k=0;k!=s2length;++k) {
					int s2child = s2children[k];
					boolean matched = false;
					for(int l=0;l!=s1length;++l) {
						int s1child = s1children[l];
						if(equivs.get(s1child,s2child)) {
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

		return false;
	}

	/**
	 * <p>
	 * Turn an automaton into its canonical form. Two automata are said to be
	 * <i>isomorphic</i> if there is a permutation of states which, when applied
	 * to the first, yields the second. Then, for any isomorphic automata have
	 * an identical canonical form. More generally, this known as the graph
	 * isomorphism problem. From a computational perspective, graph isomorphism
	 * is interesting in that (at the time of writing) no known polynomial time
	 * algorithms are known; however, it is also not known to be NP-complete.
	 * </p>
	 *
	 * <p>
	 * The canonical form is computed using a straightforward (brute-force)
	 * back-tracking search. This means it is potentially quite expensive,
	 * although in most cases it probably runs in polynomial time. The number of
	 * non-deterministic states in the automaton directly affects how hard the
	 * computation is. In particular, if there are no non-deterministic states,
	 * the algorithm runs in guaranteed polynomial time.
	 * </p>
	 *
	 * <p>
	 * <b>NOTE:</b> Generally speaking, you want to run extract, simplify and
	 * minimise before calling this algorithm. Otherwise, you don't get a true
	 * canonical form.
	 * </p>
	 *
	 * @param automaton
	 *            --- to be canonicalised
	 * @param dataComparator
	 *            --- comparator for supplementary data. May be null if no state
	 *            has supplementary data.  The comparator is guaranteed to be
	 *            called on states of matching kind and determinism.
	 */
	public static void canonicalise(Automaton automaton,
			Comparator<State> dataComparator) {
		int size = automaton.size();
		ArrayList<Morphism> candidates = new ArrayList<Morphism>();
		candidates.add(new Morphism(size));
		for(int i=0;i!=size;++i) {
			extend(i,candidates,automaton,dataComparator);
		}
		inplaceReorder(automaton,candidates.get(0).n2i);
	}

	/*
	 * The following provides a brute-force way of determining the canonical
	 * form. It's really really slow, but useful for testing.
	 */
	private static Automaton bruteForce(Automaton automaton, Comparator dataComparator) {
		int[] init = new int[automaton.size()-1];
		for(int i=0;i<init.length;++i) {
			init[i] = i+1;
		}
		Morphism winner = null;
		for(int[] permutation : permutations(init)) {
			Morphism m = new Morphism(automaton.size());
			for(int c : permutation) {
				m.allocate(c);
			}
			if(winner == null || lessThan(m,winner,automaton, dataComparator)) {
				winner = m;
			}
		}

		return reorder(automaton,winner.n2i);
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
	 * @param dataComparator
	 *            --- comparator for supplementary data. May be null if no state
	 *            has supplementary data. The comparator is guaranteed to be
	 *            called on states of matching kind and determinism.
	 *
	 */
	private static void extend(int index, ArrayList<Morphism> candidates,
			Automaton automaton, Comparator<State> dataComparator) {

		// Please note, this algorithm is really not very efficient. There is
		// quite a lot more pruning that could be done!

		int size = candidates.size();
		for(int i=0;i!=size;++i) {
			Morphism candidate = candidates.get(i);
			extend(index,candidate,candidates,automaton);
		}

		prune(candidates, automaton, dataComparator);
	}

	private static void extend(int index, Morphism candidate,
			ArrayList<Morphism> candidates, Automaton automaton) {
		State[] states = automaton.states;
		State s = states[candidate.i2n[index]];
		int[] children = s.children;
		if(s.deterministic) {
			// easy case
			for(int child : children) {
				if(!candidate.isAllocated(child)) {
					candidate.allocate(child);
				}
			}
		} else {
			// harder case

			// This loop is why the algorithm has exponential running time.
			ArrayList<int[]> permutations = permutations(children);
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
	public static ArrayList<int[]> permutations(int[] children) {
		ArrayList<int[]> permutations = new ArrayList();
		permutations(0,children,permutations);
		return permutations;
	}

	private static void permutations(int index, int[] permutation, ArrayList<int[]> permutations) {
		int size = permutation.length;
		if(index == size) {
			permutations.add(Arrays.copyOf(permutation, size));
		} else {
			int t1 = permutation[index];
			for(int i=index;i<size;++i) {
				int t2 = permutation[i];
				permutation[index] = t2;
				permutation[i] = t1;
				permutations(index+1,permutation,permutations);
				permutation[index] = t1;
				permutation[i] = t2;
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
	private static void prune(ArrayList<Morphism> candidates, Automaton automaton, Comparator<State> dataComparator) {
		// this is really inefficient!
		// at a minimum, we could avoid recomputing lessThan twice for each candidate.
		Morphism least = candidates.get(0);
		for(Morphism candidate : candidates) {
			if(lessThan(candidate,least,automaton, dataComparator)) {
				least = candidate;
			}
		}

		int diff = 0;
		for(int i=0;i!=candidates.size();++i) {
			Morphism candidate = candidates.get(i);
			if(lessThan(least,candidate,automaton, dataComparator)) {
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
			Automaton automaton, Comparator<State> dataComparator) {
		State[] states = automaton.states;
		int size = Math.min(morph1.free,morph2.free);

		for(int i=0;i!=size;++i) {
			State s1 = states[morph1.i2n[i]];
			State s2 = states[morph2.i2n[i]];
			if(s1.kind < s2.kind) {
				return true;
			} else if(s1.kind > s2.kind) {
				return false;
			} else if(s1.deterministic && !s2.deterministic) {
				return true;
			} else if(!s1.deterministic && s2.deterministic) {
				return false;
			}

			int[] s1children = s1.children;
			int[] s2children = s2.children;
			if(s1children.length < s2children.length) {
				return true;
			} else if(s1children.length > s2children.length) {
				return false;
			}
			int length = s1children.length;
			boolean deterministic = s1.deterministic;
			if(deterministic) {
				for(int j=0;j!=length;++j) {
					int s1child = morph1.n2i[s1children[j]];
					int s2child = morph2.n2i[s2children[j]];
					if(s1child < s2child) {
						return true;
					} else if(s1child > s2child) {
						return false;
					}
				}
			} else {
				// as usual, non-deterministic states are awkward
				BitSet s1Visited = new BitSet(automaton.size());
				BitSet s2Visited = new BitSet(automaton.size());
				for(int j=0;j!=length;++j) {
					int s1child = morph1.n2i[s1children[j]];
					int s2child = morph2.n2i[s2children[j]];
					if(s1child != Integer.MAX_VALUE) {
						s1Visited.set(s1child);
					}
					if(s2child != Integer.MAX_VALUE) {
						s2Visited.set(s2child);
					}
				}
				int s1cardinality = s1Visited.cardinality();
				int s2cardinality = s2Visited.cardinality();
				if(s1cardinality != s2cardinality) {
					// greater cardinality means more allocated children.
					return s1cardinality > s2cardinality;
				}
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
			if(s1.data != null) {
				if(s2.data == null) {
					return false;
				} else {
					int c = dataComparator.compare(s1,s2);
					if(c != 0) {
						return c < 0;
					}
				}
			} else if(s2.data != null) {
				return true;
			}
		}

		// Ok, they're identical thus far!
		return false;
	}

	private static final class Morphism {
		final int[] i2n; // indices to nodes
		final int[] n2i; // nodes to indices
		int free;        // first available index

		public Morphism(int size) {
			i2n = new int[size];
			n2i = new int[size];
			for(int i=0;i!=size;++i) {
				i2n[i] = Integer.MAX_VALUE;
				n2i[i] = Integer.MAX_VALUE;
			}
			free = 0;
			allocate(0);
		}

		public Morphism(Morphism morph) {
			int size = morph.size();
			i2n = Arrays.copyOf(morph.i2n,size);
			n2i = Arrays.copyOf(morph.n2i,size);
			free = morph.free;
		}

		public boolean isAllocated(int node) {
			return n2i[node] != Integer.MAX_VALUE;
		}

		public void allocate(int node) {
			i2n[free] = node;
			n2i[node] = free++;
		}

		public int size() {
			return i2n.length;
		}
	}

	/**
	 * <p>
	 * Determine whether a relationship between two automaton exists. The most
	 * common relationship of interest is that of <i>subsumption</i>. One
	 * automaton <code>a1</code> subsumes another automaton <code>a2</code> if
	 * <code>a1</code> accepts all the values accepted by <code>a2</code> (and
	 * possibly more).
	 * </p>
	 *
	 * @param relation
	 *            --- the relation to be computed. automaton.
	 */
	public static final void computeFixpoint(Relation relation) {
		Automaton from = relation.from();
		Automaton to = relation.to();
		int fromDomain = from.size();
		int toDomain = to.size();

		boolean changed = true;
		while (changed) {
			changed = false;
			for (int i = 0; i != fromDomain; i++) {
				for (int j = 0; j != toDomain; j++) {
					changed |= relation.update(i, j);
				}
			}
		}
	}

	/**
	 * Append a new state onto the end of an automaton. It is assumed that any
	 * children the new state has already refer to states within the old
	 * automaton.
	 *
	 * @param automaton
	 *            --- the automaton to append on to
	 * @param state
	 *            --- the state to be appended
	 * @return --- the new automaton
	 */
	public static Automaton append(Automaton automaton, State state) {
		State[] ostates = automaton.states;
		State[] nstates = new State[ostates.length+1];
		System.arraycopy(ostates,0,nstates,0,ostates.length);
		nstates[ostates.length] = state;
		return new Automaton(nstates);
	}

	/**
	 * Append all given states in place onto the given automaton.
	 *
	 * @param automaton
	 * @param states
	 */
	public static int inplaceAppend(Automaton automaton, State state) {
		State[] ostates = automaton.states;
		State[] nstates = new State[ostates.length+1];
		System.arraycopy(ostates,0,nstates,0,ostates.length);
		nstates[ostates.length] = state;
		automaton.states = nstates;
		return ostates.length;
	}

	/**
	 * Append all given states in place onto the given automaton.
	 *
	 * @param automaton
	 * @param states
	 */
	public static void inplaceAppendAll(Automaton automaton, State... states) {
		State[] ostates = automaton.states;
		State[] nstates = new State[ostates.length+states.length];
		System.arraycopy(ostates,0,nstates,0,ostates.length);
		System.arraycopy(states,0,nstates,ostates.length,states.length);
		automaton.states = nstates;
	}

	/**
	 * Append an automaton (the <code>tail</code>) onto the back of another (the
	 * <code>head</code>). In this case, all states in the automaton being
	 * appended are remapped automaton for their new location.
	 *
	 * @param head
	 *            --- head automaton
	 * @param tail
	 *            --- tail automaton to be appended onto head.
	 * @return --- the new automaton.
	 */
	public static Automaton append(Automaton head, Automaton tail) {
		State[] hstates = head.states;
		State[] tstates = tail.states;
		int hlength = hstates.length;
		int tlength = tstates.length;
		State[] nstates = new State[hlength+tlength];
		System.arraycopy(hstates,0,nstates,0,hlength);
		// now build remap
		int[] rmap = new int[tlength];
		for(int i=0;i!=tlength;++i) {
			rmap[i] = i + hlength;
		}
		// then copy over states
		int j = hlength;
		for(int i=0;i!=tlength;++i,++j) {
			nstates[j] = remap(tstates[i],rmap);
		}
		return new Automaton(nstates);
	}

	/**
	 * The reorder method takes an automaton, and a mapping from vertices in the
	 * old space to the those in the new space. It then reorders every state
	 * according to this mapping. Thus, states may change position and
	 * transitions are remapped accordingly.
	 *
	 * @param automaton
	 *            --- automaton to be transposed.
	 * @param rmap
	 *            --- mapping from integers in old space to those in new space.
	 */
	public static Automaton reorder(Automaton automaton, int[] rmap) {
		State[] ostates = automaton.states;
		State[] nstates = new State[ostates.length];
		int length = ostates.length;
		for(int i=0;i!=length;++i) {
			State os = ostates[i];
			inplaceRemap(os,rmap);
			nstates[rmap[i]] = new Automaton.State(os);
		}
		return new Automaton(nstates);
	}

	/**
	 * The remap method takes an automaton, and a mapping from vertices in the
	 * old space to the those in the new space. It then applies this mapping, so
	 * that all states and transitions are remapped accordingly.
	 *
	 * @param automaton
	 *            --- automaton to be transposed.
	 * @param rmap
	 *            --- mapping from integers in old space to those in new space.
	 */
	public static Automaton remap(Automaton automaton, int[] rmap) {
		State[] ostates = automaton.states;
		State[] nstates = new State[ostates.length];
		int length = ostates.length;
		for(int i=0;i!=length;++i) {
			State os = ostates[rmap[i]];
			nstates[i] = remap(os,rmap);
		}
		return new Automaton(nstates);
	}

	/**
	 * The reorder method takes an automaton, and a mapping from vertices in the
	 * old space to the those in the new space. It then reorders every state
	 * according to this mapping. Thus, states may change position and
	 * transitions are remapped accordingly.
	 *
	 * @param automaton
	 *            --- automaton to be transposed.
	 * @param rmap
	 *            --- mapping from integers in old space to those in new space.
	 */
	public static void inplaceReorder(Automaton automaton, int[] rmap) {
		State[] ostates = automaton.states;
		State[] nstates = new State[ostates.length];
		int length = ostates.length;
		for(int i=0;i!=length;++i) {
			State os = ostates[i];
			inplaceRemap(os,rmap);
			nstates[rmap[i]] = os;
		}
		automaton.states = nstates;
	}

	/**
	 * The remap method takes an automaton, and a mapping from vertices in the
	 * old space to the those in the new space. It then applies this mapping, so
	 * that all states and transitions are remapped accordingly.
	 *
	 * @param automaton
	 *            --- automaton to be transposed.
	 * @param rmap
	 *            --- mapping from integers in old space to those in new space.
	 */
	public static void inplaceRemap(Automaton automaton, int[] rmap) {
		State[] ostates = automaton.states;
		int length = ostates.length;
		for(int i=0;i!=length;++i) {
			State os = ostates[i];
			inplaceRemap(os,rmap);
		}
	}

	/**
	 * The remap method takes a node, and mapping from vertices in the old
	 * space to the those in the new space. It then applies this mapping, so
	 * that the node now refers to vertices in the new space. Or, in
	 * other words, it transposes the node into the new space.
	 *
	 * @param node
	 *            --- node to be transposed.
	 * @param rmap
	 *            --- mapping from integers in old space to those in new
	 *            space.
	 */
	private static void inplaceRemap(State node, int[] rmap) {
		int[] children = node.children;
		if(node.deterministic) {
			for (int i = 0; i != children.length; ++i) {
				children[i] = rmap[children[i]];
			}
		} else {
			// slightly harder for non-deterministic case
			BitSet visited = new BitSet(rmap.length);
			for (int i = 0; i != children.length; ++i) {
				int nchild = rmap[children[i]];
				visited.set(nchild);
			}
			int nlength = visited.cardinality();
			if(nlength != children.length) {
				children = Arrays.copyOf(children, nlength);
				node.children = children;
			}
			int j=0;
			for (int i = visited.nextSetBit(0); i >= 0; i = visited
					.nextSetBit(i + 1)) {
				children[j++] = i;
			}
		}
	}

	/**
	 * The remap method takes a node, and mapping from vertices in the old
	 * space to the those in the new space. It then applies this mapping, so
	 * that the node now refers to vertices in the new space. Or, in
	 * other words, it transposes the node into the new space.
	 *
	 * @param node
	 *            --- node to be transposed.
	 * @param rmap
	 *            --- mapping from integers in old space to those in new
	 *            space.
	 */
	public static State remap(State node, int[] rmap) {
		int[] ochildren = node.children;
		int[] nchildren;
		if(node.deterministic) {
			nchildren = new int[ochildren.length];
			for (int i = 0; i != ochildren.length; ++i) {
				 nchildren[i] = rmap[ochildren[i]];
			}
		} else {
			// slightly harder for non-deterministic case
			BitSet visited = new BitSet(rmap.length);
			for (int i = 0; i != ochildren.length; ++i) {
				int nchild = rmap[ochildren[i]];
				visited.set(nchild);
			}
			int nlength = visited.cardinality();
			nchildren = new int[nlength];
			int j=0;
			for (int i = visited.nextSetBit(0); i >= 0; i = visited
					.nextSetBit(i + 1)) {
				nchildren[j++] = i;
			}
		}
		return new State(node.kind,node.data,node.deterministic,nchildren);
	}

	public static void main(String[] args) {
		State[] states = new State[3];
		states[0] = new State(0,false,new int[]{1});
		states[1] = new State(0,false,new int[]{1,2});
		states[2] = new State(0,false,new int[]{});
		Automaton a = new Automaton(states);
		System.out.println("GOT: " + a);
		a = minimise(a);
		System.out.println("NOW: " + a);
	}
}
