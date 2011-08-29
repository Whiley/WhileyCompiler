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

import java.util.Arrays;

import wyil.util.Pair;
import wyil.lang.NameID;
import wyil.lang.Type;
import wyil.lang.Type.Dictionary;
import wyil.lang.Type.Existential;
import wyil.lang.Type.Fun;
import wyil.lang.Type.List;
import wyil.lang.Type.Meth;
import wyil.lang.Type.Process;
import wyil.lang.Type.Record;
import wyil.lang.Type.Set;
import wyil.lang.Type.Tuple;
import wyil.lang.Type.Union;

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
public final class Node {
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
	
	public final static String[] kinds = { "void", "any", "meta", "null", "bool",
			"char","int", "real", "string", "tuple", "dict", "set", "list", "ref", "record", "union",
			"fun", "label" };
	
	public String toString() {
		if(data instanceof Pair[]) {
			return kinds[kind] + " : " + Arrays.toString((Pair[])data);
		} else if(data instanceof int[]) {
			return kinds[kind] + " : " + Arrays.toString((int[])data);				
		} else {
			return kinds[kind] + " : " + data;
		}
	}
	
		/**
	 * Determine the node kind of a Type.Leaf
	 * @param leaf
	 * @return
	 */
	public static final byte leafKind(Type.Leaf leaf) {
		if(leaf instanceof Type.Void) {
			return K_VOID;
		} else if(leaf instanceof Type.Any) {
			return K_ANY;
		} else if(leaf instanceof Type.Null) {
			return K_NULL;
		} else if(leaf instanceof Type.Bool) {
			return K_BOOL;
		} else if(leaf instanceof Type.Byte) {
			return K_BYTE;
		} else if(leaf instanceof Type.Char) {
			return K_CHAR;
		} else if(leaf instanceof Type.Int) {
			return K_INT;
		} else if(leaf instanceof Type.Real) {
			return K_RATIONAL;
		} else if(leaf instanceof Type.Strung) {
			return K_STRING;
		} else if(leaf instanceof Type.Meta) {
			return K_META;
		} else {
			// should be dead code
			throw new IllegalArgumentException("Invalid leaf node: " + leaf);
		}
	}

	/**
	 * This method inserts a blank node at the head of the nodes
	 * array, whilst remapping all existing nodes appropriately.
	 * 
	 * @param nodes
	 * @return
	 */
	public static Node[] insertComponent(Node[] nodes) {
		Node[] newnodes = new Node[nodes.length+1];		
		int[] rmap = new int[nodes.length];
		for(int i=0;i!=nodes.length;++i) {
			rmap[i] = i+1;			
		}
		for(int i=0;i!=nodes.length;++i) {
			newnodes[i+1] = remap(nodes[i],rmap);			
		}
		return newnodes;
	}

	/**
	 * The method inserts the nodes in
	 * <code>from</from> into those in <code>into</code> at the given index.
	 * This method remaps nodes in <code>from</code>, but does not remap
	 * any in <code>into</code>
	 * 
	 * @param start
	 * @param from
	 * @param into
	 * @return
	 */
	public static Node[] insertNodes(int start, Node[] from, Node[] into) {
		int[] rmap = new int[from.length];
		for(int i=0;i!=from.length;++i) {
			rmap[i] = i+start;			
		}
		for(int i=0;i!=from.length;++i) {
			into[i+start] = remap(from[i],rmap);			
		}
		return into;
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
	public static Node remap(Node node, int[] rmap) {
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
