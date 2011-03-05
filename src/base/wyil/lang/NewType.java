package wyil.lang;

import java.util.*;

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
	public static final Rational T_RATIONAL = new Rational();	
	
	
	// =============================================================
	// Type operations
	// =============================================================

	/**
	 * Determine whether type <code>t2</code> is a <i>subtype</i> of type
	 * <code>t1</code> (written t1 :> t2). In other words, whether the set of
	 * all possible values described by the type <code>t2</code> is a subset of
	 * that described by <code>t1</code>.
	 */
	public boolean isSubtype(Type t1, Type t2) {
		return false;
	}
	
	/**
	 * Compute the <i>least upper bound</i> of two types t1 and t2. The least upper
	 * bound is a type t3 where <code>t3 :> t1</code>, <code>t3 :> t2</code> and
	 * there does not exist a type t4, where <code>t3 :> t4</code>,
	 * <code>t4 :> t1</code>, <code>t4 :> t2</code>.
	 * 
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static NewType leastUpperBound(NewType t1, NewType t2) {
		return null;
	}
	
	/**
	 * Compute the <i>greatest lower bound</i> of two types t1 and t2. The
	 * greatest lower bound is a type t3 where <code>t1 :> t3</code>,
	 * <code>t2 :> t3</code> and there does not exist a type t4, where
	 * <code>t1 :> t4</code>, <code>t2 :> t4</code> and <code>t4 :> t3</code>.
	 * 
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static NewType greatestLowerBound(NewType t1, NewType t2) {
		return null;
	}
	
	
	// =============================================================
	// Primitive Types
	// =============================================================

	/**
	 * A non-union type represents a type which is not a Union. This is useful
	 * for describing the bounds of a union type, because it's never the case
	 * that a union type is a bound of another union type.
	 */
	public static abstract class NonUnion extends NewType {}

	/**
	 * A leaf type represents a type which has no component types. For example,
	 * primitive types like <code>int</code> and <code>real</code> are leaf
	 * types.
	 * 
	 * @author djp
	 * 
	 */
	public static class Leaf extends NonUnion {}

	/**
	 * A void type represents the type whose variables cannot exist! That is,
	 * they cannot hold any possible value. Void is used to represent the return
	 * type of a function which does not return anything. However, it is also
	 * used to represent the element type of an empty list of set. <b>NOTE:</b>
	 * the void type is a subtype of everything; that is, it is bottom in the
	 * type lattice.
	 * 
	 * @author djp
	 * 
	 */
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

	/**
	 * The type any represents the type whose variables may hold any possible
	 * value. <b>NOTE:</b> the any type is top in the type lattice.
	 * 
	 * @author djp
	 * 
	 */
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

	/**
	 * The null type is a special type which should be used to show the absence
	 * of something. It is distinct from void, since variables can hold the
	 * special <code>null</code> value (where as there is no special "void"
	 * value). With all of the problems surrounding <code>null</code> and
	 * <code>NullPointerException</code>s in languages like Java and C, it may
	 * seem that this type should be avoided. However, it remains a very useful
	 * abstraction to have around and, in Whiley, it is treated in a completely
	 * safe manner (unlike e.g. Java).
	 * 
	 * @author djp
	 * 
	 */
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
	/**
	 * Represents the set of boolean values (i.e. true and false)
	 * @author djp
	 *
	 */
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

	/**
	 * Represents the set of (unbound) integer values. Since integer types in
	 * Whiley are unbounded, there is no equivalent to Java's
	 * <code>MIN_VALUE</code> and <code>MAX_VALUE</code> for <code>int</code>
	 * types.
	 * 
	 * @author djp
	 * 
	 */
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

	/**
	 * Represents the set of (unbound) rational numbers. 
	 * 
	 * @author djp
	 * 
	 */
	public static final class Rational extends Leaf {
		private Rational() {}
		public boolean equals(Object o) {
			return o == T_RATIONAL;
		}
		public int hashCode() {
			return 5;
		}
		public String toString() {
			return "rat";
		}
	}
	// =============================================================
	// Compound Type
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
			case K_RATIONAL:
				return T_RATIONAL;
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
	
	// =============================================================
	// Compound Faces
	// =============================================================

	/*
	 * The compound faces are not technically necessary, as they simply provide
	 * interfaces to the underlying components of a compound type. However, they
	 * certainly make it more pleasant to use this library.
	 */

	/**
	 * A set type describes set values whose elements are subtypes of the
	 * element type. For example, <code>{1,2,3}</code> is an instance of set
	 * type <code>{int}</code>; however, <code>{1.345}</code> is not.
	 * 
	 * @author djp
	 * 
	 */
	public static final class Set extends Compound {
		private Set(Component[] components) {
			super(components);
		}
		public NewType element() {
			return extract(1);
		}		
	}

	/**
	 * A list type describes list values whose elements are subtypes of the
	 * element type. For example, <code>[1,2,3]</code> is an instance of list
	 * type <code>[int]</code>; however, <code>[1.345]</code> is not.
	 * 
	 * @author djp
	 * 
	 */
	public static final class List extends Compound {
		private List(Component[] components) {
			super(components);
		}
		public NewType element() {
			return extract(1);
		}		
	}

	/**
	 * A reference (aka pointer) represents a reference to an object on a heap.
	 * 
	 * @author djp
	 * 
	 */
	public static final class Reference extends Compound {
		private Reference(Component[] components) {
			super(components);
		}
		public NewType element() {
			int i = (Integer) components[0].data;
			return extract(i);			
		}		
	}

	/**
	 * A dictionary represents a one-many mapping from variables of one type to
	 * variables of another type. For example, the dictionary type
	 * <code>int->real</code> represents a map from integers to real values. A
	 * valid instance of this type might be <code>{1->1.2,2->3}</code>.
	 * 
	 * @author djp
	 * 
	 */
	public static final class Dictionary extends Compound {
		private Dictionary(Component[] components) {
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

	/**
	 * A record is made up of a number of fields, each of which has a unique
	 * name. Each field has a corresponding type. One can think of a record as a
	 * special kind of "fixed" dictionary (i.e. where we know exactly which
	 * entries we have).
	 * 
	 * @author djp
	 * 
	 */
	public static final class Record extends Compound {
		private Record(Component[] components) {
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

	/**
	 * A union type represents a type whose variables may hold values from any
	 * of its "bounds". For example, the union type null|int indicates a
	 * variable can either hold an integer value, or null. <b>NOTE:</b>There
	 * must be at least two bounds for a union type to make sense.
	 * 
	 * @author djp
	 * 
	 */
	public static final class Union extends Compound {
		private Union(Component[] components) {
			super(components);
		}

		/**
		 * Return the bounds of this union type.
		 * 
		 * @return
		 */
		public HashSet<NonUnion> bounds() {
			Integer[] fields = (Integer[]) components[0].data;
			HashSet r = new HashSet<NonUnion>();
			for(int i : fields) {
				r.add(extract(i));
			}
			return r;
		}
	}

	/**
	 * A function type, consisting of a list of zero or more parameters and a
	 * return type.
	 * 
	 * @author djp
	 * 
	 */
	public static final class Fun extends Compound {
		Fun(Component[] components) {
			super(components);
		}

		/**
		 * Get the return type of this function type.
		 * 
		 * @return
		 */
		public NewType ret() {
			Integer[] fields = (Integer[]) components[0].data;
			return extract(fields[0]);
		}

		/**
		 * Get the parameter types of this function type.
		 * 
		 * @return
		 */
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
	private static final byte K_RATIONAL = 5;
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
					// FIXME: this is broken!!!
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
