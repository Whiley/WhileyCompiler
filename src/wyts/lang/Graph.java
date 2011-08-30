package wyts.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

import wyil.util.Pair;

public final class Graph {	
	public final Node[] nodes;

	public Graph(Node[] nodes) {
		this.nodes = nodes;
	}

	/**
	 * Determine the hashCode of a type.
	 */
	public int hashCode() {
		int r = 0;
		for(Node c : nodes) {
			r = r + c.hashCode();
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
		if(o instanceof Graph) {
			Node[] cs = ((Graph) o).nodes;
			if(cs.length != nodes.length) {
				return false;
			}
			for(int i=0;i!=cs.length;++i) {
				if(!nodes[i].equals(cs[i])) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * The extract method basically performs a DFS from the root, extracts
	 * what it finds and minimises it.
	 * 
	 * @param root
	 *            --- the starting node to extract from.
	 * @return
	 */
	protected final Type extract(int root) {
		return Type.construct(extract(root,nodes));
	}

	/**
	 * This method converts a type graph into a string. This is a non-trivial
	 * algorithm since type graphs may contain cycles as a result of recursive
	 * types. Therefore, we can't simply recurse through the type until we reach
	 * a leaf, since this recursion might go on forever. Instead, we employ a
	 * DFS-style algorithm which maintains a list of visited nodes.
	 * 
	 * @author djp
	 * 
	 */	
	public String toString() {
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
	
	/**
	 * Represents a node in the type graph. Each node has a kind, along with a
	 * data value identifying any children. For set, list and reference kinds
	 * the data value is an Integer; for records, it's a Pair<String,Integer>[]
	 * (sorted by key). For dictionaries, it's a Pair<Integer,Integer> and, for
	 * unions and functions it's int[] (for functions first element is return).
	 * 
	 * @author djp
	 * 
	 */
	public static final class Node {
		public final byte kind;
		public final Object data;
		
		public Node(byte kind, Object data) {
			this.kind = kind;
			this.data = data;
		}
		public boolean equals(final Object o) {
			if(o instanceof Node) {
				Node c = (Node) o;
				if(kind == c.kind) {
					switch(kind) {
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
						return true;
					case K_SET:
					case K_LIST:
					case K_PROCESS:
					case K_EXISTENTIAL:
					case K_DICTIONARY:
						return data.equals(c.data);
					case K_TUPLE:	
					case K_METHOD:
					case K_FUNCTION:
					case K_UNION:
						return Arrays.equals((int[])data, (int[])c.data);
					case K_RECORD:
						return Arrays.equals((Pair[])data, (Pair[])c.data);
					}
				}				
			}
			return false;
		}
		public int hashCode() {
			if(data == null) {
				return kind;
			} else {					
				switch(kind) {
				case K_SET:
				case K_LIST:
				case K_PROCESS:
				case K_DICTIONARY:
				case K_EXISTENTIAL:
					return data.hashCode();			
				case K_TUPLE:	
				case K_METHOD:
				case K_FUNCTION:
				case K_UNION:
					return Arrays.hashCode((int[])data);
				case K_RECORD:
					return Arrays.hashCode((Pair[])data);
				case K_LABEL:
					return data.hashCode();
				}						
				throw new RuntimeException("Unreachable code reached (" + kind + ")");
			}
		}		
	}

	/**
	 * Traverse the subgraph rooted at the given node an extract all nodes.
	 * 
	 * @param root
	 * @param nodes
	 * @return
	 */
	private static final Node[] extract(int root, Node[] nodes) {
		// First, we perform the DFS.
		BitSet visited = new BitSet(nodes.length);
		// extracted maps new indices to old indices
		ArrayList<Integer> extracted = new ArrayList<Integer>();
		subgraph(root,visited,extracted,nodes);		
		// rextracted is the reverse of extracted
		int[] rextracted = new int[nodes.length];
		int i=0;
		for(int j : extracted) {
			rextracted[j]=i++;
		}
		Node[] newNodes = new Node[extracted.size()];
		i=0;
		for(int j : extracted) {
			newNodes[i++] = remap(nodes[j],rextracted);  
		}
			
		return newNodes;
	}
	
	private static final void extractOnto(int root, Node[] nodes,
			ArrayList<Node> newNodes) {
		// First, we perform the DFS.
		BitSet visited = new BitSet(nodes.length);
		// extracted maps new indices to old indices
		ArrayList<Integer> extracted = new ArrayList<Integer>();
		subgraph(root, visited, extracted, nodes);
		// rextracted is the reverse of extracted
		int[] rextracted = new int[nodes.length];
		int i = newNodes.size();
		for (int j : extracted) {
			rextracted[j] = i++;
		}				
		for (int j : extracted) {
			newNodes.add(remap(nodes[j], rextracted));
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
	private final static void subgraph(int index, BitSet visited,
			ArrayList<Integer> extracted, Node[] graph) {
		if(visited.get(index)) { return; } // node already visited}
		extracted.add(index);
		visited.set(index);
		Node node = graph[index];
		switch(node.kind) {
		case K_SET:
		case K_LIST:
		case K_PROCESS:
			// unary nodes
			subgraph((Integer) node.data,visited,extracted,graph);
			break;
		case K_DICTIONARY:
			// binary node
			Pair<Integer,Integer> p = (Pair<Integer,Integer>) node.data;
			subgraph(p.first(),visited,extracted,graph);
			subgraph(p.second(),visited,extracted,graph);
			break;
		case K_TUPLE:
		case K_UNION:
		case K_METHOD:
		case K_FUNCTION:
			// nary node
			int[] bounds = (int[]) node.data;
			for(int b : bounds) {
				if(b == -1) { continue; } // possible with K_FUNCTION				
				subgraph(b,visited,extracted,graph);				
			}
			break;
		case K_RECORD:
			// labeled nary node
			Pair<String,Integer>[] fields = (Pair<String,Integer>[]) node.data;
			for(Pair<String,Integer> f : fields) {
				subgraph(f.second(),visited,extracted,graph);
			}
			break;			
		}
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
	private static Node remap(Node node, int[] rmap) {
		Object data;

		switch (node.kind) {
		case K_SET:
		case K_LIST:
		case K_PROCESS:
			// unary nodes
			int element = (Integer) node.data;
			data = rmap[element];
			break;
		case K_DICTIONARY:
			// binary node
			Pair<Integer, Integer> p = (Pair<Integer, Integer>) node.data;
			data = new Pair(rmap[p.first()], rmap[p.second()]);
			break;
		case K_TUPLE:
		case K_UNION:
		case K_METHOD:
		case K_FUNCTION:
			// nary node
			int[] bounds = (int[]) node.data;
			int[] nbounds = new int[bounds.length];
			for (int i = 0; i != bounds.length; ++i) {
				if(bounds[i] == -1) { 
					nbounds[i] = -1; // possible with K_FUNCTION
				} else {
					nbounds[i] = rmap[bounds[i]];	
				}
			}
			data = nbounds;
			break;
		case K_RECORD:
			// labeled nary node
			Pair<String, Integer>[] fields = (Pair<String, Integer>[]) node.data;
			Pair<String, Integer>[] nfields = new Pair[fields.length];
			for (int i = 0; i != fields.length; ++i) {
				Pair<String, Integer> field = fields[i];
				nfields[i] = new Pair(field.first(), rmap[field.second()]);
			}
			data = nfields;
			break;
		default:
			return node;
		}
		return new Node(node.kind, data);
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

	public static final byte K_VOID = 0;
	public static final byte K_ANY = 1;
	public static final byte K_META = 2;
	public static final byte K_NULL = 3;
	public static final byte K_BOOL = 4;
	public static final byte K_BYTE = 5;
	public static final byte K_CHAR = 6;
	public static final byte K_INT = 7;
	public static final byte K_RATIONAL = 8;
	public static final byte K_STRING = 9;
	public static final byte K_TUPLE = 10;
	public static final byte K_SET = 11;
	public static final byte K_LIST = 12;
	public static final byte K_DICTIONARY = 13;	
	public static final byte K_PROCESS = 14;
	public static final byte K_RECORD = 15;
	public static final byte K_UNION = 16;
	public static final byte K_INTERSECTION = 17;
	public static final byte K_DIFFERENCE = 18;
	public static final byte K_FUNCTION = 19;
	public static final byte K_METHOD = 20;
	public static final byte K_EXISTENTIAL = 21;
	public static final byte K_LABEL = 22;	
	public static final byte K_HEADLESS = 23; // used for readers/writers
}
