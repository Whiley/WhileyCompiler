package wyautl.lang;

import java.util.ArrayList;
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
 * </ul>
 * 
 * @author djp
 */
public final class Automatas {

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
