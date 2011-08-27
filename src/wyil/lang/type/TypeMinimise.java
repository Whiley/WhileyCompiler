package wyil.lang.type;

import static wyil.lang.type.Node.K_ANY;
import static wyil.lang.type.Node.K_BOOL;
import static wyil.lang.type.Node.K_BYTE;
import static wyil.lang.type.Node.K_CHAR;
import static wyil.lang.type.Node.K_DICTIONARY;
import static wyil.lang.type.Node.K_EXISTENTIAL;
import static wyil.lang.type.Node.K_FUNCTION;
import static wyil.lang.type.Node.K_INT;
import static wyil.lang.type.Node.K_LIST;
import static wyil.lang.type.Node.K_META;
import static wyil.lang.type.Node.K_METHOD;
import static wyil.lang.type.Node.K_NULL;
import static wyil.lang.type.Node.K_PROCESS;
import static wyil.lang.type.Node.K_RATIONAL;
import static wyil.lang.type.Node.K_RECORD;
import static wyil.lang.type.Node.K_SET;
import static wyil.lang.type.Node.K_STRING;
import static wyil.lang.type.Node.K_TUPLE;
import static wyil.lang.type.Node.K_UNION;
import static wyil.lang.type.Node.K_VOID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import wyil.lang.Type;
import wyil.util.Pair;

/**
 * This class houses the algorithm for minimising a type. Essentially, it takes
 * in a type and produces a minimised version of that type.  
 * 
 * @author djp
 * 
 */
public class TypeMinimise {

	public static Type minimise(Type type) {
		// leaf types never need minmising!
		if (type instanceof Type.Leaf) {
			return type;
		}				
		
		// compound types need minimising.
		Node[] nodes = ((Type.Compound) type).nodes;		
		SubtypeRelation relation;;
		relation = new DefaultSubtypeOperator(nodes,nodes).doInference();		
		ArrayList<Node> newnodes = new ArrayList<Node>();
		int[] allocated = new int[nodes.length];
		//System.out.println("REBUILDING: " + type);
		//build(new PrintBuilder(System.out),type);
		//System.out.println(relation.toString());
		rebuild(0, nodes, allocated, newnodes, relation);
		return Type.construct(newnodes.toArray(new Node[newnodes.size()]));
	}
	
	/**
	 * This method reconstructs a graph given a set of equivalent nodes. The
	 * equivalence classes for a node are determined by the given subtype
	 * matrix, whilst the allocate array identifies when a node has already been
	 * allocated for a given equivalence class.
	 * 
	 * @param idx
	 * @param graph
	 * @param allocated
	 * @param newNodes
	 * @param matrix
	 * @return
	 */
	private static int rebuild(int idx, Node[] graph, int[] allocated,
			ArrayList<Node> newNodes, SubtypeRelation assumptions) {	
		int graph_size = graph.length;
		Node node = graph[idx]; 		
		int cidx = allocated[idx];		
		if(cidx > 0) {
			// node already constructed for this equivalence class
			return cidx - 1;
		} 
		
		cidx = newNodes.size(); // my new index
		// now, allocate all nodes in equivalence class
		for(int i=0;i!=graph_size;++i) {
			if(assumptions.isSubtype(i,idx) && assumptions.isSubtype(idx, i)) {
				allocated[i] = cidx + 1; 
			}
		}
		 
		newNodes.add(null); // reserve space for my node
		
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
			// which are subsumed by other nodes.
			
			HashSet<Integer> nelems = new HashSet<Integer>();			
			for(int i : elems) { nelems.add(i); }
									
			for(int i=0;i!=elems.length;i++) {
				int n1 = elems[i];
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
				for (int i = 0; i != graph_size; ++i) {
					if(assumptions.isSubtype(i,idx) && assumptions.isSubtype(idx,i)) {					
						allocated[i] = 0;
					}
				}
				return rebuild(nelems.iterator().next(), graph, allocated, newNodes,
						assumptions);
			} else {
				// first off, we have to normalise this sucker
				ArrayList<Integer> nnelems = new ArrayList(nelems);
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
		newNodes.set(cidx, new Node(node.kind,data));
		return cidx;
	}

	private static final class MinimiseComparator implements Comparator<Integer> {
		private Node[] graph;
		private SubtypeRelation subtypeMatrix;
		
		public MinimiseComparator(Node[] graph, SubtypeRelation matrix) {
			this.graph = graph;
			this.subtypeMatrix = matrix;
		}
		
		public int compare(Integer a, Integer b) {
			Node n1 = graph[a];
			Node n2 = graph[b];
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
}
