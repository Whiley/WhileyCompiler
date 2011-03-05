package wyil.lang;

import java.util.*;


import wyil.lang.Type.NonUnion;
import wyil.util.Pair;

public abstract class NewType {

	// =============================================================
	// Type Constructors
	// =============================================================

	public static final Any T_ANY = new Any();
	public static final Void T_VOID = new Void();
	public static final Null T_NULL = new Null();	
	public static final Bool T_BOOL = new Bool();
	public static final Int T_INT = new Int();
	public static final Real T_REAL = new Real();	
	
	// =============================================================
	// Primitive Types
	// =============================================================
	public static abstract class NonUnion extends NewType {}		
	public static class Leaf extends NonUnion {}
	
	public static final class Void extends Leaf {
		private Void() {}
		public boolean equals(Object o) {
			return this == o;
		}
		public int hashCode() {
			return 1;
		}
		public String toString() {
			return "void";
		}
	}	
	public static final class Any extends Leaf {
		private Any() {}
		public boolean equals(Object o) {
			return o == T_ANY;
		}
		public int hashCode() {
			return 1;
		}
		public String toString() {
			return "*";
		}
	}
	public static final class Null extends Leaf {
		private Null() {}
		public boolean equals(Object o) {
			return this == o;
		}
		public int hashCode() {
			return 2;
		}
		public String toString() {
			return "null";
		}
	}
	public static final class Bool extends Leaf {
		private Bool() {}
		public boolean equals(Object o) {
			return o == T_BOOL;
		}
		public int hashCode() {
			return 3;
		}
		public String toString() {
			return "bool";
		}
	}
	public static final class Int extends Leaf {
		private Int() {}
		public boolean equals(Object o) {
			return o == T_INT;
		}
		public int hashCode() {
			return 4;
		}
		public String toString() {
			return "int";
		}	
	}
	public static final class Real extends Leaf {
		private Real() {}
		public boolean equals(Object o) {
			return o == T_REAL;
		}
		public int hashCode() {
			return 5;
		}
		public String toString() {
			return "real";
		}
	}
	// =============================================================
	// Compound Types
	// =============================================================

	/**
	 * A Compound data structure is essentially a graph encoding of a type. Each
	 * node in the graph corresponds to a component of the type. Recursive
	 * cycles in the graph are permitted. <b>NOTE:</b> care must be take to
	 * ensure that the root of the graph (namely node at index 0) matches the
	 * actual Compound class (i.e. if its kind is K_SET, then this is an
	 * instance of Set).
	 */
	private static abstract class Compound extends NewType {
		protected final Component[] components;
		
		public Compound(Component[] components) {
			this.components = components;
		}

		/**
		 * The extract method basically performs a DFS from the root, extracts
		 * what it finds and minimises it.
		 * 
		 * @param root
		 *            --- the starting node to extract from.
		 * @return
		 */
		protected final NewType extract(int root) {
			// First, we perform the DFS.
			BitSet visited = new BitSet(components.length);
			// extracted maps new indices to old indices
			ArrayList<Integer> extracted = new ArrayList<Integer>();
			dfs(root,visited,extracted);
			// rextracted is the reverse of extracted
			int[] rextracted = new int[extracted.size()];
			int i=0;
			for(int j : extracted) {
				rextracted[i++]=j;
			}
			Component[] ncomponents = new Component[extracted.size()];
			i=0;
			for(int j : extracted) {
				ncomponents[i++] = remap(components[j],rextracted);  
			}
				
			return construct(ncomponents);
		}

		/**
		 * The remap method takes a node, and mapping from vertices in the old
		 * space to the those in the new space. It then applies this mapping, so
		 * that the node produced refers to vertices in the new space. Or, in
		 * other words, it transposes the component into the new space.
		 * 
		 * @param node
		 *            --- component to be transposed.
		 * @param rmap
		 *            --- mapping from integers in old space to those in new
		 *            space.
		 * @return
		 */
		public Component remap(Component node, int[] rmap) {
			Object data;

			switch (node.kind) {
			case K_SET:
			case K_LIST:
			case K_REFERENCE:
				// unary nodes
				Integer element = (Integer) node.data;
				data = rmap[element];
				break;
			case K_DICTIONARY:
				// binary node
				Pair<Integer, Integer> p = (Pair<Integer, Integer>) node.data;
				data = new Pair(rmap[p.first()], rmap[p.second()]);
				break;
			case K_UNION:
			case K_FUNCTION:
				// nary node
				int[] bounds = (int[]) node.data;
				int[] nbounds = new int[bounds.length];
				for (int i = 0; i != bounds.length; ++i) {
					nbounds[i] = rmap[bounds[i]];
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
			return new Component(node.kind, data);
		}

		/**
		 * The construct methods constructs a Type from an array of Components.
		 * It carefully ensures the kind of the root component matches the class
		 * created (e.g. a kind K_SET results in a class Set).
		 * 
		 * @param ncomponents
		 * @return
		 */
		private final static NewType construct(Component[] components) {
			Component root = components[0];
			switch(root.kind) {
			case K_VOID:
				return T_VOID;
			case K_ANY:
				return T_ANY;
			case K_NULL:
				return T_NULL;			
			case K_BOOL:
				return T_BOOL;
			case K_INT:
				return T_INT;
			case K_REAL:
				return T_REAL;
			case K_SET:
				return new Set(components);
			case K_LIST:
				return new List(components);
			case K_DICTIONARY:
				return new Dictionary(components);
			case K_RECORD:
				return new Record(components);
			case K_UNION:
				return new Union(components);
			case K_FUNCTION:
				return new Fun(components);
			default:
				throw new IllegalArgumentException("invalid component kind: " + root.kind);
			}
		}
		
		private final void dfs(int index, BitSet visited,
				ArrayList<Integer> extracted) {
			extracted.add(index);
			visited.set(index);
			Component node = components[index];
			switch(node.kind) {
			case K_SET:
			case K_LIST:
			case K_REFERENCE:
				// unary nodes
				dfs((Integer) node.data,visited,extracted);
				break;
			case K_DICTIONARY:
				// binary node
				Pair<Integer,Integer> p = (Pair<Integer,Integer>) node.data;
				dfs(p.first(),visited,extracted);
				dfs(p.second(),visited,extracted);
				break;
			case K_UNION:
			case K_FUNCTION:
				// nary node
				int[] bounds = (int[]) node.data;
				for(Integer b : bounds) {
					dfs(b,visited,extracted);
				}
				break;
			case K_RECORD:
				// labeled nary node
				Pair<String,Integer>[] fields = (Pair<String,Integer>[]) node.data;
				for(Pair<String,Integer> f : fields) {
					dfs(f.second(),visited,extracted);
				}
				break;			
			}
		}
	}
	
	public static final class Set extends Compound {
		Set(Component[] components) {
			super(components);
		}
		public NewType element() {
			return extract(1);
		}		
	}
	public static final class List extends Compound {
		List(Component[] components) {
			super(components);
		}
		public NewType element() {
			return extract(1);
		}		
	}
	public static final class Reference extends Compound {
		Reference(Component[] components) {
			super(components);
		}
		public NewType element() {
			int i = (Integer) components[0].data;
			return extract(i);			
		}		
	}
	public static final class Dictionary extends Compound {
		Dictionary(Component[] components) {
			super(components);
		}
		public NewType key() {
			Pair<Integer,Integer> p = (Pair) components[0].data;
			return extract(p.first());
		}
		public NewType value() {
			Pair<Integer,Integer> p = (Pair) components[0].data;
			return extract(p.second());			
		}
	}
	public static final class Record extends Compound {
		Record(Component[] components) {
			super(components);
		}

		/**
		 * Extract just the key set for this field. This is a cheaper operation
		 * than extracting the keys and their types (since types must be
		 * extracted).
		 * 
		 * @return
		 */
		public HashSet<String> keys() {
			Pair<String,Integer>[] fields = (Pair[]) components[0].data;
			HashSet<String> r = new HashSet<String>();
			for(Pair<String,Integer> f : fields) {
				r.add(f.first());
			}
			return r;
		}

		/**
		 * Return a mapping from field names to their types.
		 * 
		 * @return
		 */
		public HashMap<String,NewType> fields() {
			Pair<String,Integer>[] fields = (Pair[]) components[0].data;
			HashMap<String,NewType> r = new HashMap<String,NewType>();
			for(Pair<String,Integer> f : fields) {
				r.put(f.first(),extract(f.second()));
			}
			return r;
		}
	}
	public static final class Union extends Compound {
		Union(Component[] components) {
			super(components);
		}
		HashSet<NonUnion> bounds() {
			Integer[] fields = (Integer[]) components[0].data;
			HashSet r = new HashSet<NonUnion>();
			for(int i : fields) {
				r.add(extract(i));
			}
			return r;
		}
	}
	public static final class Fun extends Compound {
		Fun(Component[] components) {
			super(components);
		}
		
		public NewType ret() {
			Integer[] fields = (Integer[]) components[0].data;
			return extract(fields[0]);
		}
		public ArrayList<NewType> params() {
			Integer[] fields = (Integer[]) components[0].data;
			ArrayList<NewType> r = new ArrayList<NewType>();
			for(int i=1;i<fields.length;++i) {
				r.add(extract(i));
			}
			return r;
		}
	}
	// =============================================================
	// Components
	// =============================================================
	private static final byte K_VOID = 0;
	private static final byte K_ANY = 1;
	private static final byte K_NULL = 2;
	private static final byte K_BOOL = 3;
	private static final byte K_INT = 4;
	private static final byte K_REAL = 5;
	private static final byte K_SET = 6;
	private static final byte K_LIST = 7;
	private static final byte K_DICTIONARY = 8;
	private static final byte K_REFERENCE = 9;
	private static final byte K_RECORD = 10;
	private static final byte K_UNION = 11;
	private static final byte K_FUNCTION = 12;

	/**
	 * A component represents a node in the type graph. Each node has a kind,
	 * along with a data value identifying any children. For set, list and
	 * reference kinds the data value is an Integer; for records, it's a
	 * Pair<String,Integer>[] (sorted by key). For dictionaries, it's a
	 * Pair<Integer,Integer> and, for unions and functions it's int[] (for
	 * functions first element is return).
	 * 
	 * @author djp
	 * 
	 */
	private static final class Component {
		final byte kind;
		final Object data;
		
		public Component(byte kind, Object data) {
			this.kind = kind;
			this.data = data;
		}
		public boolean equals(final Object o) {
			if(o instanceof Component) {
				Component c= (Component) o;
				if(data == null) {
					return kind == c.kind && c.data == null;
				} else {
					return kind == c.kind && data.equals(c.data);
				}
			}
			return false;
		}
		public int hashCode() {
			if(data == null) {
				return kind;
			} else {
				return kind + data.hashCode();
			}
		}
	}
}
