// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

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
}
