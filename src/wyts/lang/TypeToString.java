package wyts.lang;

import static wyts.lang.Node.*;

import java.util.BitSet;

import wyil.lang.Type;
import wyil.util.Pair;

/**
 * This class houses the algorithm for converting a type graph into a string.
 * This is a non-trivial algorithm since type graphs may contain cycles as a
 * result of recursive types. Therefore, we can't simply recurse through the
 * type until we reach a leaf, since this recursion might go on forever.
 * Instead, we employ a DFS-style algorithm which maintains a list of visited
 * nodes.
 * 
 * @author djp
 * 
 */
public class TypeToString {
	
	public static String toString(Node[] nodes) {
		// First, we need to find the headers of the computation. This is
		// necessary in order to mark the start of a recursive type.
		BitSet headers = new BitSet(nodes.length);
		BitSet visited = new BitSet(nodes.length);
		BitSet onStack = new BitSet(nodes.length);
		findHeaders(0, visited, onStack, headers, nodes);
		visited.clear();
		String[] titles = new String[nodes.length];
		int count = 0;
		for (int i = 0; i != nodes.length; ++i) {
			if (headers.get(i)) {
				titles[i] = headerTitle(count++);
			}
		}
		return toString(0, visited, titles, nodes);
	}

	private static final char[] headers = { 'X','Y','Z','U','V','W','L','M','N','O','P','Q','R','S','T'};
	private static String headerTitle(int count) {
		String r = Character.toString(headers[count%headers.length]);
		int n = count / headers.length;
		if(n > 0) {
			return r + n;
		} else {
			return r;
		}
	}
			
	
	/**
	 * The following method traverses the graph using a depth-first
	 * search to identify nodes which are "loop headers". That is, they are the
	 * target of one or more recursive edgesin the graph.
	 * 
	 * @param index
	 *            --- the index to search from.
	 * @param visited
	 *            --- the set of vertices already visited.
	 * @param onStack
	 *            --- the set of nodes currently on the DFS path from the root.
	 * @param headers
	 *            --- header nodes discovered during this search are set to true
	 *            in this bitset.
	 * @param graph
	 *            --- the graph.
	 */
	private final static void findHeaders(int index, BitSet visited,
			BitSet onStack, BitSet headers, Node[] graph) {
		if(visited.get(index)) {
			// node already visited
			if(onStack.get(index)) {
				headers.set(index);
			}
			return; 
		} 		
		onStack.set(index);
		visited.set(index);
		Node node = graph[index];
		switch(node.kind) {
		case K_SET:
		case K_LIST:
		case K_PROCESS:
			// unary nodes
			findHeaders((Integer) node.data,visited,onStack,headers,graph);
			break;
		case K_DICTIONARY:		
			// binary node
			Pair<Integer,Integer> p = (Pair<Integer,Integer>) node.data;
			findHeaders(p.first(),visited,onStack,headers,graph);
			findHeaders(p.second(),visited,onStack,headers,graph);
			break;
		case K_TUPLE:
		case K_UNION:
		case K_INTERSECTION:
		case K_DIFFERENCE:
		case K_METHOD:
		case K_FUNCTION:
			// nary node
			int[] bounds = (int[]) node.data;
			for(int b : bounds) {
				if(b == -1) { continue; } // possible with K_FUNCTION
				findHeaders(b,visited,onStack,headers,graph);
			}
			break;
		case K_RECORD:
			// labeled nary node
			Pair<String,Integer>[] fields = (Pair<String,Integer>[]) node.data;
			for(Pair<String,Integer> f : fields) {
				findHeaders(f.second(),visited,onStack,headers,graph);
			}
			break;			
		}
		onStack.set(index,false);
	}

	/**
	 * The following method constructs a string representation of the underlying
	 * graph. This representation may be an expanded version of the underling
	 * graph, since one cannot easily represent aliasing in the type graph in a
	 * textual manner.
	 * 
	 * @param index
	 *            --- the index to start from
	 * @param visited
	 *            --- the set of vertices already visited.
	 * @param headers
	 *            --- an array of strings which identify the name to be given to
	 *            each header.
	 * @param graph
	 *            --- the graph.
	 */
	private final static String toString(int index, BitSet visited,
			String[] headers, Node[] graph) {
		if (visited.get(index)) {
			// node already visited
			return headers[index];
		} else if(headers[index] != null) {
			visited.set(index);
		}
		Node node = graph[index];
		String middle;
		switch (node.kind) {
		case K_VOID:
			return "void";
		case K_ANY:
			return "any";
		case K_NULL:
			return "null";
		case K_BOOL:
			return "bool";
		case K_BYTE:
			return "byte";
		case K_CHAR:
			return "char";
		case K_INT:
			return "int";
		case K_RATIONAL:
			return "real";
		case K_STRING:
			return "string";
		case K_SET:
			middle = "{" + toString((Integer) node.data, visited, headers, graph)
					+ "}";
			break;
		case K_LIST:
			middle = "[" + toString((Integer) node.data, visited, headers, graph)
					+ "]";
			break;
		case K_EXISTENTIAL:
			middle = "?" + node.data.toString();
			break;
		case K_PROCESS:
			middle = "process " + toString((Integer) node.data, visited, headers, graph);
			break;
		case K_DICTIONARY: {
			// binary node
			Pair<Integer, Integer> p = (Pair<Integer, Integer>) node.data;
			String k = toString(p.first(), visited, headers, graph);
			String v = toString(p.second(), visited, headers, graph);
			middle = "{" + k + "->" + v + "}";
			break;
		}
		case K_DIFFERENCE: {
			// binary node
			int[] p = (int[]) node.data;
			String k = toString(p[0], visited, headers, graph);
			String v = toString(p[1], visited, headers, graph);
			middle = "(" + k + "-" + v + ")";
			break;
		}
		case K_UNION: {
			int[] bounds = (int[]) node.data;
			middle = "";
			for (int i = 0; i != bounds.length; ++i) {
				if (i != 0) {
					middle += "|";
				}
				middle += toString(bounds[i], visited, headers, graph);
			}
			break;
		}
		case K_INTERSECTION: {
			int[] bounds = (int[]) node.data;
			middle = "";
			for (int i = 0; i != bounds.length; ++i) {
				if (i != 0) {
					middle += "&";
				}
				middle += toString(bounds[i], visited, headers, graph);
			}
			break;
		}
		case K_TUPLE: {
			middle = "";
			int[] bounds = (int[]) node.data;			
			for (int i = 0; i != bounds.length; ++i) {
				if (i != 0) {
					middle += ",";
				}
				middle += toString(bounds[i], visited, headers, graph);
			}
			middle = "(" + middle + ")";
			break;
		}
		case K_METHOD:
		case K_FUNCTION: {
			middle = "";
			int[] bounds = (int[]) node.data;
			String rec = bounds[0] == -1 ? null : toString(bounds[0],visited,headers,graph);
			String ret = toString(bounds[1], visited, headers, graph);
			for (int i = 2; i != bounds.length; ++i) {
				if (i != 2) {
					middle += ",";
				}
				middle += toString(bounds[i], visited, headers, graph);
			}
			if(node.kind == K_FUNCTION) {
				middle = ret + "(" + middle + ")";
			} else if(rec != null) {
				middle = rec + "::" + ret + "(" + middle + ")";
			} else {
				middle = "::" + ret + "(" + middle + ")";
			}
			break;
		}
		case K_RECORD: {
			// labeled nary node
			middle = "{";
			Pair<String, Integer>[] fields = (Pair<String, Integer>[]) node.data;
			for (int i = 0; i != fields.length; ++i) {
				if (i != 0) {
					middle += ",";
				}
				Pair<String, Integer> f = fields[i];
				middle += toString(f.second(), visited, headers, graph) + " " + f.first();
			}
			middle = middle + "}";
			break;
		}
		case K_LABEL: {
			middle = (String) node.data;
			break;
		}
		default: 
			throw new IllegalArgumentException("Invalid type encountered");
		}
		
		// Finally, check whether this is a header node, or not. If it is a
		// header then we need to insert the recursive type.
		String header = headers[index];
		if(header != null) {
			// The following case is interesting. Basically, we'll never revisit
			// a header. Therefore, if we have multiple edges landing on a
			// header we must update the header string to represent the full
			// type reachable from the header.
			String r = header + "<" + middle + ">"; 
			headers[index] = r;
			return r;
		} else {
			return middle;
		}
	}


}
