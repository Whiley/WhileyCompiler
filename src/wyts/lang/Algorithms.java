package wyts.lang;

import static wyts.lang.Node.K_ANY;
import static wyts.lang.Node.K_BOOL;
import static wyts.lang.Node.K_BYTE;
import static wyts.lang.Node.K_CHAR;
import static wyts.lang.Node.K_DICTIONARY;
import static wyts.lang.Node.K_EXISTENTIAL;
import static wyts.lang.Node.K_FUNCTION;
import static wyts.lang.Node.K_INT;
import static wyts.lang.Node.K_LIST;
import static wyts.lang.Node.K_META;
import static wyts.lang.Node.K_METHOD;
import static wyts.lang.Node.K_NULL;
import static wyts.lang.Node.K_PROCESS;
import static wyts.lang.Node.K_RATIONAL;
import static wyts.lang.Node.K_RECORD;
import static wyts.lang.Node.K_SET;
import static wyts.lang.Node.K_STRING;
import static wyts.lang.Node.K_TUPLE;
import static wyts.lang.Node.K_UNION;
import static wyts.lang.Node.K_VOID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import wyil.lang.NameID;
import wyil.lang.Type;
import wyil.util.Pair;
import wyts.lang.Automata.State;

/**
 * This class houses various algorithms for manipulating types.
 * 
 * @author djp
 * 
 */
public class Algorithms {

	/**
	 * The following algorithm simplifies a type. This includes, for example,
	 * removing unions involving any, unions of unions and unions with a single
	 * element.
	 * 
	 * @param t
	 * @return
	 */
	public static Type simplify(Type t) {
		return t;
	}
	
	/**
	 * <p>
	 * The following algorithm minimises a type. For example:
	 * </p>
	 * 
	 * <pre>
	 * define InnerList as null|{int data, OuterList next}
	 * define OuterList as null|{int data, InnerList next}
	 * </pre>
	 * <p>
	 * This type is minimised into the following (equivalent) form:
	 * </p>
	 * 
	 * <pre>
	 * define LinkedList as null|{int data, LinkedList next}
	 * </pre>
	 * <p>
	 * The minisation algorithm is based on the well-known algorithm for
	 * minimising a DFA (see e.g. <a
	 * href="http://en.wikipedia.org/wiki/DFA_minimization">[1]</a>).
	 * </p>
	 * <p>
	 * The algorithm operates by performing a subtype test of each node against
	 * all others. From this, we can identify nodes which are equivalent under
	 * the subtype operator. Using this information, the type is reconstructed
	 * such that for each equivalence class only a single node is created.
	 * </p>
	 * <p>
	 * <b>NOTE:</b> this algorithm does not put the type into a canonical form.
	 * Additional work is necessary to do this.
	 * </p>
	 * 
	 * @param type
	 * @return
	 */
	public static Type minimise(Type type) {
		// Leaf types never need minmising!
		if (type instanceof Type.Leaf) {
			return type;
		}				
		
		// Only compound types need minimising.
		State[] nodes = ((Type.Compound) type).states;		
		
		// First, determine the equivalence classes using the default subtype
		// operator.
		
		SubtypeRelation relation;
		relation = new DefaultSubtypeOperator(nodes,nodes).doInference();
		
		// Second, reconstruct the type taking these equivalence classes into
		// account.
		ArrayList<State> newnodes = new ArrayList<State>();
		int[] allocated = new int[nodes.length];
		//System.out.println("REBUILDING: " + type);
		//Type.build(new PrintBuilder(System.out),type);
		//System.out.println(relation.toString());
		rebuild(0, nodes, allocated, newnodes, relation);				
		
		return Type.construct(newnodes.toArray(new State[newnodes.size()]));
	}
	
	/**
	 * This method reconstructs a graph given a set of equivalent nodes. The
	 * equivalence classes for a node are determined by the given subtype
	 * matrix, whilst the allocated array identifies when a node has already
	 * been allocated for a given equivalence class.
	 * 
	 * @param idx
	 *            --- index of node to be rebuilt in the old graph.
	 * @param graph
	 *            --- graph being rebuilt (i.e. the <i>old</i> graph).
	 * @param allocated
	 *            --- map from nodes in old graph to their index in new graph.
	 *            In fact, it stores their index+1 since zero is tsken indicate
	 *            the node has not yet been allocated.
	 * @param newNodes
	 *            --- graph being constructed (i.e. the <i>new</i> graph).
	 * @param assumptions
	 *            --- the subtype relation.
	 * @return
	 */
	private static int rebuild(int idx, State[] graph, int[] allocated,
			ArrayList<State> newNodes, SubtypeRelation assumptions) {			 	
		
		// First, check if this node has already been allocated. If so, then
		// simply return the new index for this node. A node may be allocated
		// before its ever visited because another node in its equivalence class
		// may allocated.
		
		int cidx = allocated[idx];		
		if(cidx > 0) {
			return cidx - 1;
		} 
		
		cidx = newNodes.size(); // new index for this node		
		int graph_size = graph.length;
		
		// Second, allocate all nodes in equivalence class. This ensures that we
		// only every have one node in the new graph for every equivalence
		// class. 
		for(int i=0;i!=graph_size;++i) {
			if(assumptions.isSubtype(i,idx) && assumptions.isSubtype(idx, i)) {
				allocated[i] = cidx + 1; 
			}
		}
		 
		// Third, allocate space for the new node. We have to use null here,
		// since we haven't got the necessary information for this node yet. 
		newNodes.add(null); // reserve space for my node		
		
		// Fourth, recursively reconstruct the children (if any) of this node.
		State node = graph[idx];
		Object data = null;
		switch(node.kind) {
		case K_EXISTENTIAL:
			data = node.data;
			break;
		case K_SET:
		case K_LIST:
		case K_PROCESS: {
			int element = (Integer) node.data;
			data = (Integer) rebuild(element,graph,allocated,newNodes,assumptions);
			break;
		}
		case K_DICTIONARY: {
			Pair<Integer,Integer> p = (Pair) node.data;
			int from = (Integer) rebuild(p.first(),graph,allocated,newNodes,assumptions);
			int to = (Integer) rebuild(p.second(),graph,allocated,newNodes,assumptions);
			data = new Pair(from,to);
			break;
		}		
		case K_TUPLE:
		case K_METHOD:
		case K_FUNCTION: {
			int[] elems = (int[]) node.data;
			int[] nelems = new int[elems.length];
			for(int i = 0; i!=elems.length;++i) {
				if(elems[i] == -1) {
					// possible for K_FUNCTION
					nelems[i] = -1;
				} else {
					nelems[i]  = (Integer) rebuild(elems[i],graph,allocated,newNodes,assumptions);
				}
			}			
			data = nelems;
			break;			
		}
		case K_RECORD: {			
				Pair<String, Integer>[] elems = (Pair[]) node.data;
				Pair<String, Integer>[] nelems = new Pair[elems.length];
				for (int i = 0; i != elems.length; ++i) {
					Pair<String, Integer> p = elems[i];					
					int j = (Integer) rebuild(p.second(), graph, allocated,
							newNodes, assumptions);							
					nelems[i] = new Pair<String, Integer>(p.first(), j);
				}
				data = nelems;			
			break;
		}	
		case K_UNION: {
			int[] elems = (int[]) node.data;
			
			// The aim here is to try and remove equivalent nodes, and nodes
			// which are subsumed by other nodes. The issue of contractivity is
			// an awkward problem. A union type is contractive if it has itself
			// as a child. E.g. the type X<X|null> is contractive. In such case,
			// we want to remove the direct recursive link.
			
			HashSet<Integer> nelems = new HashSet<Integer>();			
			for(int i : elems) { nelems.add(i); }
			nelems.remove(idx); // necessary to spot contractive case						
			for(int i=0;i!=elems.length;i++) {				
				int n1 = elems[i];
				if(n1 == idx) { continue; } // necessary to spot contractive case
				for(int j=0;j<elems.length;j++) {
					if(i==j) { continue; }
					int n2 = elems[j];	
					
					if(assumptions.isSubtype(n1,n2) && (!assumptions.isSubtype(n2,n1) || i < j)) {				
						nelems.remove(n2);												
					}
				}	
			}											
			
			// ok, let's see what we've got left			
			if (nelems.size() == 1) {				
				// ok, union node should be removed as it's entirely subsumed. I
				// need to undo what I've already done in allocating a new node.
				newNodes.remove(cidx);	
				for(int i=0;i!=graph_size;++i) {
					if(assumptions.isSubtype(i,idx) && assumptions.isSubtype(idx, i)) {
						allocated[i] = 0; 
					}
				}
				return rebuild(nelems.iterator().next(), graph, allocated, newNodes,
						assumptions);
			} else {
				ArrayList<Integer> nnelems = new ArrayList(nelems);
				// TODO: this is a terrible hack
				Collections.sort(nnelems,new MinimiseComparator(graph,assumptions));			
				// ok, now rebuild
				int[] melems = new int[nelems.size()];
				int i=0;
				for (Integer j : nnelems) {
					melems[i++] = (Integer) rebuild(j, graph,
							allocated, newNodes, assumptions);
				}
				data = melems;
			}
			break;			
		}
		}
		
		// finally, create the new node!!!
		newNodes.set(cidx, new State(node.kind,data));
		return cidx;
	}
		
	private static final class MinimiseComparator implements Comparator<Integer> {
		private State[] graph;
		private SubtypeRelation subtypeMatrix;
		
		public MinimiseComparator(State[] graph, SubtypeRelation matrix) {
			this.graph = graph;
			this.subtypeMatrix = matrix;
		}
		
		public int compare(Integer a, Integer b) {
			State n1 = graph[a];
			State n2 = graph[b];
			if(n1.kind < n2.kind) {
				return -1;
			} else if(n1.kind > n2.kind) {
				return 1;
			} else {
				// First try subtype relation
				if (subtypeMatrix.isSubtype(b,a)) {
					return -1;
				} else if (subtypeMatrix.isSubtype(a,b)) {
					return 1;
				}
				// Second try harder stuff
				Object data1 = n1.data;
				Object data2 = n2.data;
				
				switch(n1.kind){
				case K_VOID:
				case K_ANY:
				case K_META:
				case K_NULL:
				case K_BOOL:
				case K_BYTE:
				case K_CHAR:
				case K_INT:
				case K_RATIONAL:
				case K_STRING:
					return 0;
				case K_EXISTENTIAL: {
					String s1 = (String) data1;
					String s2 = (String) data2;
					return s1.compareTo(s2);
				}
				case K_RECORD: {
					Pair[] fields1 = (Pair[]) data1;
					Pair[] fields2 = (Pair[]) data2;
					if(fields1.length < fields2.length) {
						return -1; 
					} else if(fields1.length > fields2.length) {
						return 1;
					}
					// FIXME: could presumably do more here.
				}
				// FIXME: could do more here!!
				}
				
				if(a < b) {
					return -1;
				} else if(a > b) {
					return 1;
				} else {
					return 0;
				}
			}
		}
	}
	

	private static int intersect(int n1, State[] graph1, int n2, State[] graph2,
			ArrayList<State> newNodes,
			HashMap<Pair<Integer, Integer>, Integer> allocations) {	
		Integer idx = allocations.get(new Pair(n1,n2));
		if(idx != null) {
			// this indicates an allocation has already been performed for this
			// pair.  
			return idx;
		}
		
		State c1 = graph1[n1];
		State c2 = graph2[n2];				
		int nid = newNodes.size(); // my node id
		newNodes.add(null); // reserve space for my node	
		allocations.put(new Pair(n1,n2), nid);
		State node; // new node being created
		
		if(c1.kind == c2.kind) { 
			switch(c1.kind) {
			case K_VOID:
			case K_ANY:
			case K_META:
			case K_NULL:
			case K_BOOL:
			case K_BYTE:
			case K_CHAR:
			case K_INT:
			case K_RATIONAL:
			case K_STRING:
				node = c1;
				break;
			case K_EXISTENTIAL:
				NameID nid1 = (NameID) c1.children;
				NameID nid2 = (NameID) c2.children;				
				if(nid1.name().equals(nid2.name())) {
					node = c1;
				} else {
					node = new State(K_VOID,null);
				}
				break;
			case K_SET:
			case K_LIST:
			case K_PROCESS: {
				// unary node
				int e1 = (Integer) c1.children;
				int e2 = (Integer) c2.children;
				int element = intersect(e1,graph1,e2,graph2,newNodes,allocations);
				node = new State(c1.kind,element);
				break;
			}
			case K_DICTIONARY: {
				// binary node
				Pair<Integer, Integer> p1 = (Pair<Integer, Integer>) c1.children;
				Pair<Integer, Integer> p2 = (Pair<Integer, Integer>) c2.children;
				int key = intersect(p1.first(),graph2,p2.first(),graph2,newNodes,allocations);
				int value = intersect(p1.second(),graph2,p2.second(),graph2,newNodes,allocations);
				node = new State(K_DICTIONARY,new Pair(key,value));
				break;
			}		
			case K_TUPLE:  {
				// nary nodes
				int[] elems1 = (int[]) c1.children;
				int[] elems2 = (int[]) c2.children;
				if(elems1.length != elems2.length) {
					// TODO: can we do better here?
					node = new State(K_VOID,null);
				} else {
					int[] nelems = new int[elems1.length];
					for(int i=0;i!=nelems.length;++i) {
						nelems[i] = intersect(elems1[i],graph1,elems2[i],graph2,newNodes,allocations);
					}
					node = new State(K_TUPLE,nelems);
				}
				break;
			}
			case K_METHOD:
			case K_FUNCTION:  {
				// nary nodes
				int[] elems1 = (int[]) c1.children;
				int[] elems2 = (int[]) c2.children;
				int e1 = elems1[0];
				int e2 = elems2[0];
				if(elems1.length != elems2.length){
					node = new State(K_VOID,null);
				} else if ((e1 == -1 || e2 == -1) && e1 != e2) {
					node = new State(K_VOID, null);
				} else {
					int[] nelems = new int[elems1.length];
					// TODO: need to check here whether or not this is the right
					// thing to do. My gut is telling me that covariant and
					// contravariance should be treated differently ...
					for (int i = 0; i != nelems.length; ++i) {
						nelems[i] = intersect(elems1[i], graph1, elems2[i],
								graph2, newNodes,allocations);
					}
					node = new State(c1.kind, nelems);
				}
				break;
			}
			case K_RECORD: 
					// labeled nary nodes
					outer : {
						Pair<String, Integer>[] fields1 = (Pair<String, Integer>[]) c1.children;
						Pair<String, Integer>[] fields2 = (Pair<String, Integer>[]) c2.children;
						int old = newNodes.size();
						if (fields1.length != fields2.length) {
							node = new State(K_VOID, null);
						} else {
							Pair<String, Integer>[] nfields = new Pair[fields1.length];
							for (int i = 0; i != nfields.length; ++i) {
								Pair<String, Integer> e1 = fields1[i];
								Pair<String, Integer> e2 = fields2[i];
								if (!e1.first().equals(e2.first())) {
									node = new State(K_VOID, null);
									break outer;
								} else {
									int nidx = intersect(e1.second(), graph1,
											e2.second(), graph2, newNodes,
											allocations);

									if (newNodes.get(nidx).kind == K_VOID) {
										// A record with a field of void type
										// cannot exist --- it's just equivalent
										// to void.
										while (newNodes.size() != old) {
											newNodes.remove(newNodes.size() - 1);
										}
										node = new State(K_VOID, null);
										break outer;
									}

									nfields[i] = new Pair<String, Integer>(
											e1.first(), nidx);
								}
							}
							node = new State(K_RECORD, nfields);
						}						
					}	
				break;
			case K_UNION: {
				// This is the hardest (i.e. most expensive) case. Essentially, I
				// just check that for each bound in one node, there is an
				// equivalent bound in the other.
				int[] bounds1 = (int[]) c1.children;
				int[] nbounds = new int[bounds1.length];
								
				// check every bound in c1 is a subtype of some bound in c2.
				for (int i = 0; i != bounds1.length; ++i) {
					nbounds[i] = intersect(bounds1[i], graph1, n2, graph2,
							newNodes,allocations);
				}
				node = new State(K_UNION,nbounds);
				break;
			}					
			default:
				throw new IllegalArgumentException("attempting to minimise open recurisve type");
			}		
		} else if(c1.kind == K_ANY) {			
			newNodes.remove(newNodes.size()-1);
			extractOnto(n2,graph2,newNodes);
			return nid;
		} else if(c2.kind == K_ANY) {			
			newNodes.remove(newNodes.size()-1);
			extractOnto(n1,graph1,newNodes);
			return nid;
		} else if (c1.kind == K_UNION){					
			int[] obounds = (int[]) c1.children;			
			int[] nbounds = new int[obounds.length];
							
			// check every bound in c1 is a subtype of some bound in c2.
			for (int i = 0; i != obounds.length; ++i) {
				nbounds[i] = intersect(obounds[i], graph1, n2, graph2,
						newNodes,allocations);
			}
			node = new State(K_UNION,nbounds);
		} else if (c2.kind == K_UNION) {			
			int[] obounds = (int[]) c2.children;			
			int[] nbounds = new int[obounds.length];
							
			// check every bound in c1 is a subtype of some bound in c2.
			for (int i = 0; i != obounds.length; ++i) {
				nbounds[i] = intersect(n1,graph1,obounds[i], graph2,
						newNodes,allocations);
			}
			node = new State(K_UNION,nbounds);
		} else {
			// default case --> go to void
			node = new State(K_VOID,null);
		}
		// finally, create the new node!!!
		newNodes.set(nid, node);
		return nid;
	}
	
	private static int difference(int n1, State[] graph1, int n2, State[] graph2,
			ArrayList<State> newNodes,
			HashMap<Pair<Integer, Integer>, Integer> allocations, SubtypeRelation matrix) {
		
		int nid = newNodes.size(); // my node id		
		if(matrix.isSupertype(n1,n2)) {
			newNodes.add(new State(K_VOID,null));
			return nid; 
		}
		
		Integer idx = allocations.get(new Pair(n1,n2));
		if(idx != null) {
			// this indicates an allocation has already been performed for this
			// pair.  
			return idx;
		}
		
		State c1 = graph1[n1];
		State c2 = graph2[n2];				
		
		allocations.put(new Pair(n1,n2), nid);
		newNodes.add(null); // reserve space for my node	
		State node; // new node being created
		
		if(c1.kind == c2.kind) { 
			switch(c1.kind) {
			case K_VOID:
			case K_ANY:
			case K_META:
			case K_NULL:
			case K_BOOL:
			case K_BYTE:
			case K_CHAR:
			case K_INT:
			case K_RATIONAL:
			case K_STRING:
				node = new State(K_VOID,null);
				break;
			case K_EXISTENTIAL:
				NameID nid1 = (NameID) c1.children;
				NameID nid2 = (NameID) c2.children;				
				if(nid1.name().equals(nid2.name())) {
					node = new State(K_VOID,null);					
				} else {
					node = c1;
				}
				break;
			case K_SET:
			case K_LIST:
			case K_PROCESS: {
				// unary node
				int e1 = (Integer) c1.children;
				int e2 = (Integer) c2.children;
				int element = difference(e1,graph1,e2,graph2,newNodes,allocations,matrix);
				node = new State(c1.kind,element);
				break;
			}
			case K_DICTIONARY: {
				// binary node
				Pair<Integer, Integer> p1 = (Pair<Integer, Integer>) c1.children;
				Pair<Integer, Integer> p2 = (Pair<Integer, Integer>) c2.children;
				int key = difference(p1.first(),graph2,p2.first(),graph2,newNodes,allocations,matrix);
				int value = difference(p1.second(),graph2,p2.second(),graph2,newNodes,allocations,matrix);
				node = new State(K_DICTIONARY,new Pair(key,value));
				break;
			}		
			case K_TUPLE:  {
				// nary nodes
				int[] elems1 = (int[]) c1.children;
				int[] elems2 = (int[]) c2.children;
				if(elems1.length != elems2.length) {
					node = c1;
				} else {
					int[] nelems = new int[elems1.length];
					for(int i=0;i!=nelems.length;++i) {
						nelems[i] = difference(elems1[i],graph1,elems2[i],graph2,newNodes,allocations,matrix);
					}
					node = new State(K_TUPLE,nelems);
				}
				break;
			}
			case K_METHOD:
			case K_FUNCTION:  {
				// nary nodes
				int[] elems1 = (int[]) c1.children;
				int[] elems2 = (int[]) c2.children;
				int e1 = elems1[0];
				int e2 = elems2[0];
				if(elems1.length != elems2.length){
					node = c1;
				} else if ((e1 == -1 || e2 == -1) && e1 != e2) {
					node = c1;
				} else {
					int[] nelems = new int[elems1.length];
					// TODO: need to check here whether or not this is the right
					// thing to do. My gut is telling me that covariant and
					// contravariance should be treated differently ...
					for (int i = 0; i != nelems.length; ++i) {
						nelems[i] = difference(elems1[i], graph1, elems2[i],
								graph2, newNodes,allocations,matrix);
					}
					node = new State(c1.kind, nelems);
				}
				break;
			}
			case K_RECORD:
				// labeled nary nodes
					outer : {
						Pair<String, Integer>[] fields1 = (Pair<String, Integer>[]) c1.children;
						Pair<String, Integer>[] fields2 = (Pair<String, Integer>[]) c2.children;
						int old = newNodes.size();
						if (fields1.length != fields2.length) {
							node = c1;
						} else {
							Pair<String, Integer>[] nfields = new Pair[fields1.length];
							boolean voidField = false;
							for (int i = 0; i != fields1.length; ++i) {
								Pair<String, Integer> e1 = fields1[i];
								Pair<String, Integer> e2 = fields2[i];
								if (!e1.first().equals(e2.first())) {
									node = c1;
									break outer;
								} else {
									int nidx = difference(e1.second(), graph1,
											e2.second(), graph2, newNodes,
											allocations, matrix);
									State nnode = newNodes.get(nidx);
									if (nnode != null && nnode.kind == K_VOID) {
										voidField = true;
									}

									nfields[i] = new Pair<String, Integer>(
											e1.first(), nidx);
								}
							}
							if(voidField) {
								// A record with a field of void type
								// cannot exist --- it's just equivalent
								// to void.
								while (newNodes.size() != old) {
									newNodes.remove(newNodes.size() - 1);
								}
								node = new State(K_VOID, null);
								break outer;
							}
							node = new State(K_RECORD, nfields);
						}
					}
				break;
			case K_UNION: {
				// This is the hardest (i.e. most expensive) case. Essentially, I
				// just check that for each bound in one node, there is an
				// equivalent bound in the other.
				int[] bounds1 = (int[]) c1.children;
				int[] nbounds = new int[bounds1.length];
								
				// check every bound in c1 is a subtype of some bound in c2.
				for (int i = 0; i != bounds1.length; ++i) {
					nbounds[i] = difference(bounds1[i], graph1, n2, graph2,
							newNodes,allocations,matrix);
				}
				node = new State(K_UNION,nbounds);
				break;
			}					
			default:
				throw new IllegalArgumentException("attempting to minimise open recurisve type");
			}		
		} else if(c1.kind == K_ANY) {			
			// TODO: try to do better
			node = new State(K_ANY,null);
		} else if(c2.kind == K_ANY) {			
			node = new State(K_VOID,null);
		} else if (c1.kind == K_UNION){					
			int[] obounds = (int[]) c1.children;			
			int[] nbounds = new int[obounds.length];
							
			for (int i = 0; i != obounds.length; ++i) {
				nbounds[i] = difference(obounds[i], graph1, n2, graph2,
						newNodes,allocations,matrix);
			}
			node = new State(K_UNION,nbounds);
		} else if (c2.kind == K_UNION) {			
			int[] obounds = (int[]) c2.children;			
			int[] nbounds = new int[obounds.length];
							
			for (int i = 0; i != obounds.length; ++i) {
				nbounds[i] = difference(n1,graph1,obounds[i], graph2,
						newNodes,allocations,matrix);
			}
			// FIXME: this is broken. need intersection types.
			node = new State(K_UNION,nbounds);
		} else {
			// default case --> go to no change
			node = c1;			
		}								
		
		if(node == c1) {
			while(newNodes.size() > nid) {
				newNodes.remove(newNodes.size()-1);
			}
						
			extractOnto(n1,graph1,newNodes);			
			return nid;
		} else {
			// finally, create the new node!!!
			newNodes.set(nid, node);
			return nid;
		}
	}
}
