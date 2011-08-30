package wyts.lang;

import java.util.Arrays;

import wyil.util.Pair;
import wyts.lang.Type.Compound;

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
		if(o instanceof Compound) {
			Node[] cs = ((Compound) o).nodes;
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
		return construct(Type.extract(root,nodes));
	}

	public String toString() {
		return TypeToString.toString(nodes);
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
