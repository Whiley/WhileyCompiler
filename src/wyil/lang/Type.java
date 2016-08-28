// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute
// it and/or modify it under the terms of the GNU General Public
// License as published by the Free Software Foundation; either
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it
// will be useful, but WITHOUT ANY WARRANTY; without even the
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public
// License along with the Whiley-to-Java Compiler. If not, see
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce.

package wyil.lang;

import java.util.*;
import wybs.lang.NameID;
import wyil.util.type.TypeParser;

/**
 * A structural type. See
 * http://whiley.org/2011/03/07/implementing-structural-types/ for more on how
 * this class works.
 *
 * @author David J. Pearce
 *
 */
public abstract class Type {

	// =============================================================
	// Type Constructors
	// =============================================================

	public static final Any T_ANY = new Any();
	public static final Void T_VOID = new Void();
	public static final Null T_NULL = new Null();
	public static final Bool T_BOOL = new Bool();
	public static final Byte T_BYTE = new Byte();
	public static final Int T_INT = new Int();
	public static final Meta T_META = new Meta();
	
	public static final Type T_NOTNULL = new Negation(T_NULL);

	public static Type Union(Type left, Type right) {
		// Apply a number of simple optimisations
		if(left == T_ANY || right == T_ANY) {
			return T_ANY;
		} else if(left == T_VOID) {
			return right;
		} else if(right == T_VOID) {
			return left;
		} else if(left instanceof Type.Union) {
			// Flatten left-hand side
			Type.Union u = (Type.Union) left;
			return Union(right,Union(u.bounds()));
		} else if(right instanceof Type.Union) {
			// Flatten right-hand side
			Type.Union u = (Type.Union) right;
			return Union(left,Union(u.bounds()));
		} else if(left.equals(right)) {		
			return left;
		} else {
			return new Union(left,right);
		}
	}
	
	public static Type Union(List<Type> types) {
		HashSet<Type> nTypes = new HashSet<Type>(types);
		Type result = types.get(0);
		for(Type type : nTypes) {
			result = Union(result,type);
		}
		return result;
	}
	
	public static Type Intersection(Type left, Type right) {
		// Apply a number of simple optimisations
		if(left == T_VOID || right == T_VOID) {
			return T_VOID;
		} else if(left == T_ANY) {
			return right;
		} else if(right == T_ANY) {
			return left;
		} else if(left instanceof Type.Intersection) {
			// Flatten left-hand side
			Type.Intersection i = (Type.Intersection) left;
			return Intersection(right,Intersection(i.bounds()));
		} else if(right instanceof Type.Union) {
			// Flatten right-hand side
			Type.Intersection i = (Type.Intersection) right;
			return Intersection(left,Intersection(i.bounds()));
		} else if(left.equals(right)) {
			return left;
		} else {
			return new Intersection(left,right);
		}
	}
	
	public static Type Intersection(List<Type> types) {
		HashSet<Type> nTypes = new HashSet<Type>(types);
		Type result = types.get(0);
		for(Type type : nTypes) {
			result = Intersection(result,type);
		}
		return result;
	}
	
	public static Type fromString(String str) {
		return new TypeParser(str).parse();
	}
	
	// =============================================================
	// Primitive Types
	// =============================================================

	/**
	 * A leaf type represents a type which has no component types. For example,
	 * primitive types like <code>int</code> and <code>real</code> are leaf
	 * types.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Leaf extends Type {}

	/**
	 * A void type represents the type whose variables cannot exist! That is,
	 * they cannot hold any possible value. Void is used to represent the return
	 * type of a function which does not return anything. However, it is also
	 * used to represent the element type of an empty list of set. <b>NOTE:</b>
	 * the void type is a subtype of everything; that is, it is bottom in the
	 * type lattice.
	 *
	 * @author David J. Pearce
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
	 * @author David J. Pearce
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
			return "any";
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
	 * @author David J. Pearce
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
	 * The type mets represents the type of types. That is, values of this type
	 * are themselves types. (think reflection, where we have
	 * <code>class Class {}</code>).
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Meta extends Leaf {
		private Meta() {}
		public boolean equals(Object o) {
			return o == T_META;
		}
		public int hashCode() {
			return 1;
		}
		public String toString() {
			return "type";
		}
	}

	/**
	 * Represents the set of boolean values (i.e. true and false)
	 * @author David J. Pearce
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
	 * Represents a sequence of 8 bits. Note that, unlike many languages, there
	 * is no representation associated with a byte. For example, to extract an
	 * integer value from a byte, it must be explicitly decoded according to
	 * some representation (e.g. two's compliment) using an auxillary function
	 * (e.g. <code>Byte.toInt()</code>).
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Byte extends Leaf {
		private Byte() {}
		public boolean equals(Object o) {
			return o == T_BYTE;
		}
		public int hashCode() {
			return 4;
		}
		public String toString() {
			return "byte";
		}
	}

	/**
	 * Represents the set of (unbound) integer values. Since integer types in
	 * Whiley are unbounded, there is no equivalent to Java's
	 * <code>MIN_VALUE</code> and <code>MAX_VALUE</code> for <code>int</code>
	 * types.
	 *
	 * @author David J. Pearce
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
	 * The existential type represents the an unknown type, defined at a given
	 * position.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Nominal extends Leaf {
		private NameID nid;

		public Nominal(NameID name) {
			nid = name;
		}

		public boolean equals(Object o) {
			if (o instanceof Nominal) {
				Nominal e = (Nominal) o;
				return nid.equals(e.nid);
			}
			return false;
		}

		public NameID name() {
			return nid;
		}

		public int hashCode() {
			return nid.hashCode();
		}

		public String toString() {
			return nid.toString();
		}
	}

	// =============================================================
	// Compound Faces
	// =============================================================

	/**
	 * A type which is either a list, or a union of lists. An effective list
	 * gives access to an effective element type, which is the union of possible
	 * element types.
	 *
	 * <pre>
	 * int[] | bool[]
	 * </pre>
	 *
	 * Here, the effective element type is int|bool.
	 *
	 * @return
	 */
	public interface EffectiveArray {
		public Type element();
	}

	/**
	 * A list type describes list values whose elements are subtypes of the
	 * element type. For example, <code>[1,2,3]</code> is an instance of list
	 * type <code>[int]</code>; however, <code>[1.345]</code> is not.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Array extends Type implements EffectiveArray {
		private final Type element;

		public Array(Type element) {
			this.element = element;
		}

		public Type element() {
			return element;
		}
		
		public Type.Array update(Type newElement) {
			// FIXME: could employ simple simplications here
			return new Type.Array(new Type.Union(element,newElement));
		}
		
		@Override
		public boolean equals(Object o) {
			if(o instanceof Array) {
				Array a = (Array) o;
				return element.equals(a.element);
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return element.hashCode() * 2;
		}
		
		@Override
		public String toString() {
			return element.toString() + "[]";
		}
	}

	/**
	 * Represents a reference to an object in Whiley.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Reference extends Type  {
		private final Type element;
		private final String lifetime;
		
		public Reference(Type element, String lifetime) {
			this.element = element;
			this.lifetime = lifetime;
		}
		public Type element() {
			return element;
		}
		public String lifetime() {
			return lifetime;
		}
		@Override
		public boolean equals(Object o) {
			if(o instanceof Reference) {
				Reference r = (Reference) o;
				return element.equals(r.element) && lifetime.equals(r.lifetime);
			}
			return false;
		}
		@Override
		public int hashCode() {
			return element.hashCode() + lifetime.hashCode();
		}

		@Override
		public String toString() {
			return "&" + lifetime + ":" + element.toString();
		}
	}

	/**
	 * A type which is either a record, or a union of records. An effective
	 * record gives access to a subset of the visible fields which are
	 * guaranteed to be in the type. For example, consider this type:
	 *
	 * <pre>
	 * {int op, int x} | {int op, [int] y}
	 * </pre>
	 *
	 * Here, the field op is guaranteed to be present. Therefore, the effective
	 * record type is just <code>{int op}</code>.
	 *
	 * @return
	 */
	public interface EffectiveRecord {
		public Type field(String field);

		public Map<String,Type> fields();
	}

	/**
	 * A record is made up of a number of fields, each of which has a unique
	 * name. Each field has a corresponding type. One can think of a record as a
	 * special kind of "fixed" map (i.e. where we know exactly which
	 * entries we have).
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Record extends Type implements EffectiveRecord {
		private HashMap<String,Type> fields;
		private boolean isOpen;

		public Record(Map<String,Type> fields, boolean isOpen) {
			this.fields = new HashMap<String,Type>(fields);
			this.isOpen = isOpen;
		}

		/**
		 * Check whether this is an open record or not.
		 * @return
		 */
		public boolean isOpen() {
			return isOpen;
		}		
		
		/**
		 * Extract just the key set for this field. This is a cheaper operation
		 * than extracting the keys and their types (since types must be
		 * extracted).
		 *
		 * @return
		 */
		public Set<String> keys() {
			return fields.keySet();
		}

		public Type field(String field) {
			return fields.get(field);
		}

		/**
		 * Return a mapping from field names to their types.
		 *
		 * @return
		 */
		public Map<String, Type> fields() {
			return fields;
		}

		public Type.Record update(String field, Type type) {
			Type.Record r = new Type.Record(fields, isOpen);
			r.fields.put(field,type);
			return r;
		}
		
		@Override
		public boolean equals(Object o) {
			if(o instanceof Record) {
				Record r = (Record) o;
				return fields.equals(r.fields) && isOpen == r.isOpen;
			}
			return false;
		}

		@Override
		public int hashCode() {
			int f = isOpen ? 1 : 0;
			return fields.hashCode() + f;
		}

		@Override
		public String toString() {
			String body = "";
			boolean firstTime=true;
			for(Map.Entry<String, Type> e : fields.entrySet()) {
				if(!firstTime) {
					body = body + ", ";
				}
				firstTime=false;
				body = body + e.getValue() + " " + e.getKey();
			}
			return "{" + body + "}";
		}
	}

	/**
	 * A union type represents a type whose variables may hold values from any
	 * of its "bounds". For example, the union type null|int indicates a
	 * variable can either hold an integer value, or null. <b>NOTE:</b>There
	 * must be at least two bounds for a union type to make sense.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Union extends Type {
		private final ArrayList<Type> elements;
		
		private Union(Type... elements) {
			this.elements = new ArrayList<Type>();
			for(int i=0;i!=elements.length;++i) {
				this.elements.add(elements[i]);
			}
		}
		
		private Union(List<Type> elements) {
			this.elements = new ArrayList<Type>(elements);
		}

		/**
		 * Return the bounds of this union type.
		 *
		 * @return
		 */
		public List<Type> bounds() {
			return elements;
		}

		@Override
		public boolean equals(Object o) {
			if(o instanceof Union) {
				Union r = (Union) o;
				return elements.equals(r.elements);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return elements.hashCode();
		}

		@Override
		public String toString() {
			String body = "";
			for(int i=0;i!=elements.size();++i) {
				if(i != 0) {
					body = body + "|";
				}				
				body = body + elements.get(i);
			}
			return body;
		}
	}
	
	/**
	 * An intersection type represents a type whose variables hold values from
	 * all of its "bounds". For example, the intersection type
	 * <code>T1&T2</code> indicates a variable whose values are in both
	 * <code>T1</code> and <code>T2</code>.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Intersection extends Type {
		private final ArrayList<Type> elements;
		
		private Intersection(List<Type> elements) {
			this.elements = new ArrayList<Type>(elements);
		}
		
		private Intersection(Type... elements) {
			this.elements = new ArrayList<Type>();
			for(int i=0;i!=elements.length;++i) {
				this.elements.add(elements[i]);
			}
		}
		/**
		 * Return the bounds of this union type.
		 *
		 * @return
		 */
		public List<Type> bounds() {
			return elements;
		}

		@Override
		public boolean equals(Object o) {
			if(o instanceof Intersection) {
				Intersection r = (Intersection) o;
				return elements.equals(r.elements);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return elements.hashCode();
		}

		@Override
		public String toString() {
			String body = "";
			for(int i=0;i!=elements.size();++i) {
				if(i != 0) {
					body = body + "&";
				}				
				body = body + elements.get(i);
			}
			return body;
		}
	}

	/**
	 * A difference type represents a type which accepts values in the
	 * difference between its bounds.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Negation extends Type {
		private final Type element;

		public Negation(Type element) {
			this.element = element;
		}

		public Type element() {
			return element;
		}
		
		@Override
		public boolean equals(Object o) {
			if(o instanceof Negation) {
				Negation n = (Negation) o;
				return element.equals(n.element);
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return -element.hashCode();
		}
		@Override
		public String toString() {
			return "!" + element.toString();
		}
	}

	public abstract static class FunctionOrMethod extends Type {
		protected final ArrayList<String> lifetimeParameters;
		protected final HashSet<String> contextLifetimes;		
		protected final ArrayList<Type> parameters;
		protected final ArrayList<Type> returns;
		
		public FunctionOrMethod(List<String> lifetimeParameters, Set<String> contextLifetimes, List<Type> parameters, List<Type> returns) {
			this.lifetimeParameters = new ArrayList<String>(lifetimeParameters);
			this.contextLifetimes = new HashSet<String>(contextLifetimes);
			this.parameters = new ArrayList<Type>(parameters);
			this.returns = new ArrayList<Type>(returns);
		}

		/**
		 * Get the return types of this function or method type.
		 *
		 * @return
		 */
		public List<Type> returns() {
			return returns;
		}
		
		/**
		 * Get the parameter types of this function or method type.
		 *
		 * @return
		 */
		public List<Type> params() {
			return parameters;
		}

		public Type parameter(int i) {
			return parameters.get(i);
		}
		
		/**
		 * Get the context lifetimes of this function or method type.
		 *
		 * @return
		 */
		public Set<String> contextLifetimes() {
			return contextLifetimes;
		}

		/**
		 * Get the lifetime parameters of this function or method type.
		 *
		 * @return
		 */
		public ArrayList<String> lifetimeParams() {
			return lifetimeParameters;
		}
	}

	/**
	 * A function type, consisting of a list of zero or more parameters and a
	 * return type.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Function extends FunctionOrMethod  {
		public Function(List<Type> parameters, List<Type> returns) {
			super(Collections.EMPTY_LIST,Collections.EMPTY_SET,parameters,returns);
		}
		public String toString() {
			String ps = toString(parameters);
			String rs = toString(returns);
			return "(" + ps + ")->(" + rs + ")";
		}
	}

	public static final class Method extends FunctionOrMethod {
		public Method(List<String> lifetimeParameters, Set<String> contextLifetimes, List<Type> parameters,
				List<Type> returns) {
			super(lifetimeParameters, contextLifetimes, parameters, returns);
		}
		public String toString() {
			String cl = toString(contextLifetimes);
			String ls = toString(lifetimeParameters);
			String ps = toString(parameters);
			String rs = toString(returns);
			return "[" + cl +"]<" + ls + ">(" + ps + ")->(" + rs + ")";
		}
	}
	
	private static <T> String toString(Collection<T> things) {
		String body = "";
		boolean firstTime=true;
		for(T thing : things) {
			if(firstTime) {
				body = body + ",";
			}
			firstTime=false;
			body = body + thing;
		}
		return body;
	}
}
