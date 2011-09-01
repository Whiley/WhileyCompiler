package wyautl.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

import wyautl.lang.Automata.State;

/**
 * <p>
 * This class provides various algorithms for manipulating automatas. In
 * particular, the following main algorithms are provided:
 * </p>
 * <ul>
 * <li><b>Extraction.</b> This is used to extract one automata out of another.</li>
 * <li><b>Minimisation.</b> This is used to eliminate equivalent states within
 * an automata.</li>
 * <li><b>Canonicalisation.</b> This is used to convert an automata into a
 * canonical form.</li>
 * </ul>
 * 
 * @author djp
 */
public final class Automatas {

	/**
	 * <p>
	 * Check whether or not an automata is "concrete". A concrete automata
	 * cannot have recursive links or non-deterministic states.'
	 * </p>
	 */
	public static boolean isConcrete(Automata automata) {
		// First, check all states are deterministic
		for(int i=0;i!=automata.size();++i) {
			State s = automata.states[i];
			if(!s.deterministic) {
				return false;
			}
		}
		
		// Second, check for cycles (i.e. recursive links)
		BitSet visited = new BitSet(automata.size());
		BitSet onStack = new BitSet(automata.size());
		return isConcrete(0,onStack,visited,automata);
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
	 * @param automata
	 *            --- the automata being traversed.
	 * @return
	 */
	private static boolean isConcrete(int index, BitSet onStack,
			BitSet visited, Automata automata) {
		
		if(onStack.get(index)) {
			return false; // found a cycle!
		} 		
		
		if (visited.get(index)) {
			// Ok, we've traversed this node before and it checked out OK.
			return true;
		}
		
		visited.set(index);
		onStack.set(index);
		
		State state = automata.states[index];
		for(int child : state.children) {
			if(!isConcrete(child,onStack,visited,automata)) {
				return false;
			}
		}
		
		onStack.set(index,false);
		
		return true;
	}
	
	/**
	 * <p>
	 * Traverse the automata rooted at the given state and recursively extract
	 * all reachable states to produce a (potentially smaller) automata.
	 * </p>
	 * 
	 * <p>
	 * <b>NOTE:</b> one additional use-case for this method is to effectively
	 * "garbage collect" states in the automata. That is, if you extract from
	 * the root of any automata, you'll get an automata consisting only of those
	 * nodes reachable from the root --- but, no others. Therefore, unreachable
	 * nodes (which can arise as a result of other automata operations) are
	 * lost.
	 * </p>
	 * 
	 * @param root
	 * @param kinds
	 * @return
	 */
	public static Automata extract(Automata automata, int root) {
		// First, perform a depth-first search from the root.
		State[] nodes = automata.states;		
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
		return new Automata(newNodes);
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
	 * The following algorithm simplifies a type. This includes, for example,
	 * removing unions involving any, unions of unions and unions with a single
	 * element.
	 * 
	 * @param t
	 * @return
	 */
	public static Automata simplify(Automata t) {
		return t;
	}
		
	/**
	 * <p>
	 * This method minimises an automata by removing equivalent states. Two
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
	 * 
	 * @param automata
	 * @return
	 */
	public final static Automata minimise(Automata automata) {		
		// First, determine equivalence classes
		BinaryMatrix equivs = new BinaryMatrix(automata.size(),automata.size(),true);
		determineEquivalenceClasses(equivs,automata);
		
		// Second, determine representative nodes for each equivalence class.
		int oldSize = automata.size();
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
			
		// Finally, reconstruct minimised automata
		State[] oldStates = automata.states;
		State[] newStates = new State[newSize];
		for (int i = 0; i != oldSize; ++i) {
			int classRep = mapping[i];
			if (newStates[classRep] == null) {
				// this node is unallocated
				newStates[classRep] = remap(oldStates[i], mapping);
			}
		}
				
		return new Automata(newStates);
	}
		
	private final static void determineEquivalenceClasses(BinaryMatrix equivs,
			Automata automata) {
		boolean changed = true;
		int size = automata.size();
		
		while (changed) {
			changed = false;
			for (int i = 0; i < size; ++i) {
				for (int j = i + 1; j < size; ++j) {					
					if(equivs.get(i,j)) {
						// no need to explore nodes which are already known to
						// be not equivalent.
						boolean b = equivalent(i, j, equivs, automata);						
						equivs.set(i, j, b);
						equivs.set(j, i, b);
						changed |= !b;
					}
				}
			}
		}
	}
	
	/**
	 * Check whether two states are equivalent under the rules set out for
	 * minimisation above.
	 * 
	 * @param i
	 * @param j
	 * @return
	 */
	private final static boolean equivalent(int i, int j, BinaryMatrix equivs, Automata automata) {
		State s1 = automata.states[i];
		State s2 = automata.states[j];
		
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
	 * Turn an automata into its canonical form. For two automatas in canonical
	 * form they are <code>equal()</code> iff they accept exactly the same set
	 * of values. Two automatas which are not <code>equal()</code> are said to
	 * be <i>isomorphic</i> if the accept the same set of values. We can tell if
	 * two automatas are isomorphic by checking whether they have the same
	 * canonical form. More generally, this known as the graph isomorphism
	 * problem. From a computational perspective, graph isomorphism is
	 * interesting in that (at the time of writing) no known polynomial time
	 * algorithms are known; however, it is also not known to be NP-complete.
	 * </p>
	 * 
	 * <p>
	 * The canonical form is computed using a straightforward (brute-force)
	 * back-tracking search. This means it is potentially quite expensive,
	 * although in most cases it probably runs in polynomial time. The number of
	 * non-deterministic states in the automata directly affects how hard the
	 * computation is. In particular, if there are no non-deterministic states,
	 * the algorithm runs in guaranteed polynomial time.
	 * </p>
	 * 
	 * <p>
	 * <b>NOTE:</b>Generally speaking, you want to run extract, simplify and
	 * minimise before calling this algorithm. Otherwise, you don't get a true
	 * canonical form.
	 * </p>
	 * 
	 * @param automata ---  to be canonicalised
	 * @return
	 */
	public static Automata canonicalise(Automata automata) {
		int[] morph = new int[automata.size()];
		Arrays.fill(morph, automata.size());		
		morph[0] = 0; // root *must* be mapped to itself		
		ArrayList<int[]> candidates = new ArrayList<int[]>();
		candidates.add(morph);
		extend(0,candidates,automata);
		return remap(automata,candidates.get(0));
	}
	
	private static void extend(int index, ArrayList<int[]> candidates,
			Automata automata) {
		
	}
	
	/**
	 * This function determines whether one morphism of a given automata is
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
	 * @param automata
	 *            --- automata being canonicalised.
	 * @return
	 */
	private static boolean lessThan(int[] morph1, int[] morph2, int size,
			Automata automata) {
		State[] states = automata.states;
		for(int i=0;i!=size;++i) {
			State s1 = states[morph1[i]];
			State s2 = states[morph2[i]];
			if(s1.kind < s2.kind) {
				return true;
			} else if(s1.kind > s2.kind) {
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
			for(int j=0;j!=length;++j) {
				int s1child = morph1[s1children[j]];
				int s2child = morph2[s2children[j]];
				if(s1child < s2child) {
					return true;
				} else if(s1child > s2child) {
					return false;
				}				
			}
			
			if(s1.data != null || s2.data != null) {
				throw new RuntimeException("Need to deal with supplementary data in canonicalise");
			}
		}
		
		// Ok, they're identical thus far!
		return false;
	}
	
	/**
	 * <p>
	 * Determine whether a relationship between two automata exists. The most
	 * common relationship of interest is that of <i>subsumption</i>. One
	 * automata <code>a1</code> subsumes another automata <code>a2</code> if
	 * <code>a1</code> accepts all the values accepted by <code>a2</code> (and
	 * possibly more).
	 * </p>
	 * 
	 * @param relation
	 *            --- the relation to be computed. automata.
	 * @return
	 */
	public static final void computeRelation(Relation relation) {
		Automata from = relation.from();
		Automata to = relation.to();
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
	 * Append a new state onto the end of an automata. It is assumed that any
	 * children the new state has already refere to nodes within the old
	 * automata.
	 * 
	 * @param automata
	 * @param state
	 * @return
	 */
	public static Automata append(Automata automata, State state) {
		State[] ostates = automata.states;
		State[] nstates = new State[ostates.length+1];
		System.arraycopy(ostates,0,nstates,0,ostates.length);
		nstates[ostates.length] = state;
		return new Automata(nstates);
	}
	
	/**
	 * The remap method takes an automata, and a mapping from vertices in the
	 * old space to the those in the new space. It then applies this mapping, so
	 * that all states and transitions are remapped accordingly.
	 * 
	 * @param automata
	 *            --- automata to be transposed.
	 * @param rmap
	 *            --- mapping from integers in old space to those in new space.
	 * @return
	 */
	private static Automata remap(Automata automata, int[] rmap) {
		State[] ostates = automata.states;
		int length = ostates.length;
		State[] nstates = new State[length];		
		for(int i=0;i!=length;++i) {
			State os = ostates[rmap[i]];
			nstates[i] = remap(os,rmap);
		}
		return new Automata(nstates);
	}	
	
	/**
	 * The remap method takes a node, and mapping from vertices in the old
	 * space to the those in the new space. It then applies this mapping, so
	 * that the node produced refers to vertices in the new space. Or, in
	 * other words, it transposes the node into the new space.
	 * 
	 * @param node
	 *            --- node to be transposed.
	 * @param rmap
	 *            --- mapping from integers in old space to those in new
	 *            space.
	 * @return
	 */
	private static State remap(State node, int[] rmap) {
		int[] ochildren = node.children;
		int[] nchildren = new int[ochildren.length];
		for (int i = 0; i != nchildren.length; ++i) {
			nchildren[i] = rmap[ochildren[i]];
		}
		return new State(node.kind, nchildren, node.deterministic, node.data);
	}	
}
