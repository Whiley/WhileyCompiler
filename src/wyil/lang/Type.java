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
import wycc.util.ArrayUtils;
import wycc.util.Pair;
import wyil.util.TypeSystem;
import wyil.util.type.TypeParser;

/**
 * A structural type. See
 * http://whiley.org/2011/03/07/implementing-structural-types/ for more on how
 * this class works.
 *
 * @author David J. Pearce
 *
 */
public interface Type {

	// =============================================================
	// Interface
	// =============================================================

	/**
	 * Represents a type which constitutes a "leaf" node in the type tree. That
	 * is, it contains no types as subcomponents.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Leaf extends Type {

	}

	/**
	 * Represents a primitive type (e.g. int, bool, null, etc).
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Primitive extends Leaf {

	}

	/**
	 * Represents a named type within the system. That is, this type refers by
	 * name to a given type declaration somewhere in the overall program.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Nominal extends Leaf {
		public NameID name();
	}

	/**
	 * An effective array is a type which looks like an array, but is not
	 * exactly an array. For example, a union of arrays is an effective array.
	 * This is because in some situations we can read elements from the array,
	 * and also write elements to the array.
	 *
	 * @author David J. Peace
	 *
	 */
	public interface EffectiveArray extends Type {
		/**
		 * Get the element type which could be read from this array.
		 *
		 * @return
		 */
		public Type getReadableElementType();

		/**
		 * Get the element type which could be written to this array.
		 *
		 * @return
		 */
		public Type getWriteableElementType();

		/**
		 * Determine a new type for this array after an assignment to a given
		 * element.
		 *
		 * @param element
		 *            The type of the element being assigned
		 */
		public EffectiveArray update(Type element);
	}

	/**
	 * An array type describes array values whose elements are subtypes of the
	 * element type. For example, <code>[1,2,3]</code> is an instance of array
	 * type <code>int[]</code>; however, <code>[false]</code> is not.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Array extends EffectiveArray {
		/**
		 * Get the element type of this array. All values in any instances of
		 * this type must be instances of the element types.
		 */
		public Type element();
	}

	/**
	 * An effective record is a type which looks like a record, but is not
	 * exactly a record. For example, a union of records is an effective record.
	 * This is because in some situations we can read fields from the record,
	 * and also write fields to the record.
	 *
	 * @author David J. Peace
	 *
	 */
	public interface EffectiveRecord extends Type {

		/**
		 * Return the number of fields in this type
		 *
		 * @return
		 */
		public int size();

		/**
		 * Check whether a given field is present in this effective record or
		 * not.
		 *
		 * @param field
		 * @return
		 */
		public boolean hasField(String field);

		/**
		 * Determine the index of a given field in this effective record.
		 *
		 * @param field
		 * @return
		 */
		public int getFieldIndex(String field);

		/**
		 * Get the array of fields used in this type.
		 */
		public String[] getFieldNames();

		/**
		 * Get the element type which could be read from this array.
		 *
		 * @return
		 */
		public Type getReadableFieldType(String field);

		/**
		 * Get the element type which could be written to this array.
		 *
		 * @return
		 */
		public Type getWriteableFieldType(String field);

		/**
		 * Get an updated version of this record type after a given field has
		 * been assigned a given type.
		 *
		 * @param field
		 *            The field name being assigned. This must be an explicit
		 *            field declared in this type.
		 * @param type
		 *            The type of the value being assigned to the given field.
		 * @return
		 */
		public Type.EffectiveRecord update(String field, Type type);
	}

	/**
	 * A record is made up of a number of fields, each of which has a unique
	 * name. Each field has a corresponding type. One can think of a record as a
	 * special kind of "fixed" map (i.e. where we know exactly which entries we
	 * have).
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Record extends EffectiveRecord {
		/**
		 * Get the number of fields in this record
		 */
		@Override
		public int size();

		/**
		 * Check whether this is an open record or not. That is, whether or not
		 * there are additional "unknown" fields in this record.
		 *
		 * @return
		 */
		public boolean isOpen();

		/**
		 * Get the type of a given field in this record
		 */
		public Type getField(String name);
	}

	/**
	 * An effective reference is a type which looks like an reference, but is
	 * not exactly an reference. For example, a union of references is an
	 * effective reference. This is because in some situations we can read from
	 * the reference, and also write through the reference.
	 *
	 * @author David J. Peace
	 *
	 */
	public interface EffectiveReference extends Type {
		/**
		 * Get the element type which could be read from this array.
		 *
		 * @return
		 */
		public Type getReadableElementType();

		/**
		 * Get the element type which could be written to this array.
		 *
		 * @return
		 */
		public Type getWriteableElementType();
	}

	/**
	 * Represents a reference to an object in Whiley. For example,
	 * <code>&this:int</code> is the type of a reference to a location allocated
	 * in the enclosing scope which holds an integer value.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Reference extends EffectiveReference {

		/**
		 * Return the type of the location that this reference refers to. It is
		 * an invariant that the type does not change for the life of the
		 * location.
		 *
		 * @return
		 */
		public Type element();

		/**
		 * Return the lifetime of this reference. That is a symbolic name which
		 * declared in the enclosing scope, or "*" in the case of the global
		 * lifetime.
		 *
		 * @return
		 */
		public String lifetime();
	}

	/**
	 * Represents the set of types which are not in a given type. For example,
	 * <code>!int</code> is the set of all values which are not integers. Thus,
	 * for example, the type <code>bool</code> is a subtype of <code>!int</code>
	 * .
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Negation extends Type {
		/**
		 * Get the element type of this array. All values in any instances of
		 * this type must be instances of the element types.
		 */
		public Type element();
	}

	/**
	 * Represents the set of all functions or methods. These are values which
	 * can be called using an indirect invoke expression. Each function or
	 * method accepts zero or more parameters and will produce zero or more
	 * returns.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface FunctionOrMethod extends Type {
		/**
		 * Get the list of parameter types which are accepted by this function
		 * or method.
		 */
		Type[] params();

		/**
		 * Get the list of types which are returned by this function or method.
		 *
		 * @return
		 */
		Type[] returns();

		/**
		 * Get the ith parameter type
		 *
		 * @param i
		 * @return
		 */
		Type parameter(int i);
	}

	/**
	 * Represents the set of all function values. These are pure functions,
	 * sometimes also called "mathematical" functions. A function cannot have
	 * any side-effects and must always return the same values given the same
	 * inputs. A function cannot have zero returns, since this would make it a
	 * no-operation.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Function extends FunctionOrMethod {

	}

	/**
	 * Represents the set of all method values. These are impure and may have
	 * side-effects (e.g. performing I/O, updating non-local state, etc). A
	 * method may have zero returns and, in such case, the effect of a method
	 * comes through other side-effects. Methods may also have contextual
	 * lifetime arguments, and may themselves declare lifetime arguments.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Method extends FunctionOrMethod {
		/**
		 * Get the context lifetimes for this method. A contextual lifetime
		 * identifies a lifetime to which this method is bound, but which is not
		 * explicit in the parameters or returns of the method. This can arise
		 * through the construction of lambda methods, but also through the use
		 * oc currying.
		 *
		 * @return
		 */
		public String[] contextLifetimes();

		/**
		 * Get the lifetime parameters declared by this method. These are
		 * essentially additional parameters to the method, and reference
		 * parameters or returns may only be expressed in terms of these
		 * lifetimes (and the global lifetime).
		 *
		 * @return
		 */
		public String[] lifetimeParams();
	}

	/**
	 * Represents the intersection of one or more types together. For example,
	 * the intersection of <code>T1</code> and <code>T2</code> is
	 * <code>T1&T2</code>. Furthermore, any variable of this type must be both
	 * an instanceof <code>T1</code> and an instanceof <code>T2</code>.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Intersection extends Type {
		public Type[] bounds();
	}

	/**
	 * Represents the union of one or more types together. For example, the
	 * union of <code>int</code> and <code>null</code> is <code>int|null</code>.
	 * Any variable of this type may hold any integer or the null value.
	 * Furthermore, the types <code>int</code> and <code>null</code> are
	 * collectively referred to as the "bounds" of this type.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Union extends Type {
		public Type[] bounds();
	}

	// =============================================================
	// Constructors
	// =============================================================

	/**
	 * The type any represents the type whose variables may hold any possible
	 * value. <b>NOTE:</b> the any type is top in the type lattice.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final Type T_ANY = new Impl.Primitive(TypeSystem.K_ANY) {
		@Override
		public String toString() {
			return "any";
		}
	};

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
	public static final Type T_VOID = new Impl.Primitive(TypeSystem.K_VOID) {
		@Override
		public String toString() {
			return "void";
		}
	};

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
	public static final Type T_NULL = new Impl.Primitive(TypeSystem.K_NULL) {
		@Override
		public String toString() {
			return "null";
		}
	};
	/**
	 * Represents the set of boolean values (i.e. true and false)
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final Type T_BOOL = new Impl.Primitive(TypeSystem.K_BOOL) {
		@Override
		public String toString() {
			return "bool";
		}
	};
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
	public static final Type T_BYTE = new Impl.Primitive(TypeSystem.K_BYTE) {
		@Override
		public String toString() {
			return "byte";
		}
	};
	/**
	 * Represents the set of (unbound) integer values. Since integer types in
	 * Whiley are unbounded, there is no equivalent to Java's
	 * <code>MIN_VALUE</code> and <code>MAX_VALUE</code> for <code>int</code>
	 * types.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final Type T_INT = new Impl.Primitive(TypeSystem.K_INT) {
		@Override
		public String toString() {
			return "int";
		}
	};
	/**
	 * The type meta represents the type of types. That is, values of this type
	 * are themselves types. (think reflection, where we have
	 * <code>class Class {}</code>).
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final Type T_META = new Impl.Primitive(TypeSystem.K_META) {
		@Override
		public String toString() {
			return "meta";
		}
	};

	public static Type Nominal(NameID name) {
		return new Impl.Nominal(name);
	}

	public static Type Record(boolean isOpen, List<Pair<Type, String>> fields) {
		Pair<Type, String>[] arr = new Pair[fields.size()];
		fields.toArray(arr);
		return Record(isOpen, arr);
	}

	public static Type Record(boolean isOpen, Pair<Type, String>... fields) {
		// Sort arrays by their field
		Arrays.sort(fields, Impl.FIELD_COMPARATOR);
		// Sanity check no two fields with same name, and no void element types
		for (int i = 0; i != fields.length; ++i) {
			Pair<Type, String> field = fields[i];
			Type type = field.first();
			String name = field.second();
			if (type == T_VOID) {
				// A void element type is not a logical error, but it does mean
				// this type reduces immediately to void
				return type;
			} else if (i > 0 && fields[i - 1].second().equals(name)) {
				// This indicates we have two fields with the same name. This is
				// a logical error.
				throw new IllegalArgumentException("duplicate field encountered:" + name);
			}
		}
		// Create the record
		return new Impl.Record(isOpen, (Pair[]) fields);
	}

	public static Type Array(Type element) {
		if (element == T_VOID) {
			return T_VOID;
		} else {
			return new Impl.Array((Impl) element);
		}
	}

	public static Type Reference(String lifetime, Type element) {
		return new Impl.Reference((Impl) element, lifetime);
	}

	public static Type Function(Type[] parameters, Type[] returns) {
		Impl[] iParameters = toImplOrVoid(parameters);
		Impl[] iReturns = toImplOrVoid(returns);
		if (iParameters == null || iReturns == null) {
			return T_VOID;
		} else {
			return new Impl.Function(iParameters, iReturns);
		}
	}

	public static Type Method(Type[] parameters, Type[] returns) {
		return Method(new String[0], new String[0], parameters, returns);
	}

	public static Type Method(Collection<String> lifetimeParameters, Collection<String> contextLifetimes,
			Type[] parameters, Type[] returns) {
		String[] lifetimes = ArrayUtils.toStringArray(lifetimeParameters);
		String[] contexts = ArrayUtils.toStringArray(contextLifetimes);
		// Need to sort contexts and remove duplicates.
		Arrays.sort(contexts);
		contexts = ArrayUtils.sortedRemoveDuplicates(contexts);
		//
		return Method(lifetimes, contexts, parameters, returns);
	}

	public static Type Method(String[] lifetimeParameters, String[] contextLifetimes, Type[] parameters,
			Type[] returns) {
		Impl[] iParameters = toImplOrVoid(parameters);
		Impl[] iReturns = toImplOrVoid(returns);
		if (iParameters == null || iReturns == null) {
			return T_VOID;
		} else {
			// FIXME: should we be sorting the context lifetimes?
			return new Impl.Method(lifetimeParameters, contextLifetimes, iParameters, iReturns);
		}
	}

	static Impl[] toImplOrVoid(Type[] types) {
		Impl[] impls = new Impl[types.length];
		for (int i = 0; i != types.length; ++i) {
			Type type = types[i];
			if (type == T_VOID) {
				return null;
			} else {
				impls[i] = (Impl) type;
			}
		}
		return impls;
	}

	static Impl.Array[] toImplArrays(Impl[] types) {
		Impl.Array[] impls = new Impl.Array[types.length];
		for (int i = 0; i != types.length; ++i) {
			impls[i] = (Impl.Array) types[i];
		}
		return impls;
	}

	static Impl.Record[] toImplRecords(Impl[] types) {
		Impl.Record[] impls = new Impl.Record[types.length];
		for (int i = 0; i != types.length; ++i) {
			impls[i] = (Impl.Record) types[i];
		}
		return impls;
	}

	/**
	 * Construct the negation of a given type. This operation performs certain
	 * simplifications to ensure types are in a manageable form. For example,
	 * the negation of a negation is just the element type. Likewise, the
	 * negation of a union is an intersection of negations, etc.
	 *
	 * @param types
	 * @return
	 */
	public static Type Negation(Type type) {
		if (type instanceof Type.Union) {
			// Apply De-Mogan's law. So, !(A | B) => !A & !B
			Type.Union union = (Type.Union) type;
			Type[] bounds = union.bounds();
			Type[] nBounds = new Type[bounds.length];
			for (int i = 0; i != bounds.length; ++i) {
				nBounds[i] = Negation(bounds[i]);
			}
			return Intersection(nBounds);
		} else if (type instanceof Type.Intersection) {
			// Apply De-Mogan's law. So, !(A & B) => !A | !B
			Type.Intersection intersection = (Type.Intersection) type;
			Type[] bounds = intersection.bounds();
			Type[] nBounds = new Type[bounds.length];
			for (int i = 0; i != bounds.length; ++i) {
				nBounds[i] = Negation(bounds[i]);
			}
			return Intersection(nBounds);
		} else if (type instanceof Type.Negation) {
			// Here, !!T => T
			Type.Negation negation = (Type.Negation) type;
			return negation.element();
		} else {
			return new Impl.Negation((Impl.Atom) type);
		}
	}

	/**
	 * Construct the union of one or more types together. This operation
	 * performs certain simplifications to ensure types are in a manageable
	 * form. For example, the union of one type is that type. Likewise, the
	 * union of two identical types is just one type, etc.
	 *
	 * <b>NOTE</b> this operation assumes free access to modify the given array
	 *
	 * @param types
	 * @return
	 */
	public static Type Union(Type... types) {
		// At this point, we can assume that the given types are themselves
		// simplified. This helps as, for example, it means we cannot
		// encounter a unions of in the parameters.
		//
		Impl.Conjunctable[] cs = Impl.toConjunctables(types);
		// Apply obvious simplifications
		if (ArrayUtils.firstIndexOf(cs, T_ANY) >= 0) {
			// Any union containing any equals any
			return T_ANY;
		}
		cs = ArrayUtils.<Impl.Conjunctable> removeAll(cs, (Impl.Conjunctable) T_VOID);
		cs = ArrayUtils.removeDuplicates(cs);
		//
		if (cs.length == 0) {
			return Type.T_VOID;
		} else if (cs.length == 1) {
			return cs[0];
		} else {
			// Ensure elements in sorted order
			Arrays.sort(cs);
			// Determine whether have union of a specific type or not.
			int kind = Impl.determineCommonKind(cs);
			switch (kind) {
			case TypeSystem.K_RECORD:
				return new Impl.UnionOfRecords(toImplRecords(cs));
			case TypeSystem.K_LIST:
				return new Impl.UnionOfArrays(toImplArrays(cs));
			case TypeSystem.K_REFERENCE:
				// FIXME: return UnionOfReferencess
			default:
				return new Impl.UnionOfConjunctables(cs);
			}

		}
	}

	/**
	 * Construct the intersection of one or more types together. This operation
	 * performs certain simplifications to ensure types are in a manageable
	 * form. For example, the intersection of one type is that type. Likewise,
	 * the intersection of two identical types is just one type, etc.
	 *
	 * <b>NOTE</b> this operation assumes free access to modify the given array
	 *
	 * @param types
	 * @return
	 */
	public static Type Intersection(Type... types) {
		// At this point, we can assume that the given types are themselves
		// simplified. This helps as, for example, it means we cannot
		// encounter a unions of in the parameters.
		//
		// Extract and distribute over any nested unions
		int unionIndex = Impl.findUnion(types);
		if (unionIndex >= 0) {
			// Yes, there is at least one union we can distribute over
			return Impl.distributeUnion(unionIndex, types);
		} else {
			// Flatten any nested intersections so they are all at the same
			// level.
			Impl.Atom[] atoms = Impl.toAtoms(types);
			atoms = ArrayUtils.removeDuplicates(atoms);
			// Intersect all remaining atoms. This may be
			// result in void being discovered.
			for (int i = 0; i < atoms.length; ++i) {
				for (int j = i + 1; j < atoms.length; ++j) {
					Impl.intersectAtoms(i, j, atoms);
				}
			}
			// Check whether found void or not.
			if (ArrayUtils.firstIndexOf(atoms, T_VOID) >= 0) {
				// Any intersection containing void equals void
				return T_VOID;
			}
			// Finally, tidy up the remainder by eliminating any null values
			// that have been created. Null values can be created when
			// intersection certain types together. For example, intersection
			// int with !bool generate int and null.
			atoms = ArrayUtils.<Impl.Atom> removeAll(atoms, (Impl.Atom) T_ANY);
			//
			if (atoms.length == 0) {
				return Type.T_ANY;
			} else if (atoms.length == 1) {
				return atoms[0];
			} else {
				// Ensure elements in sorted order
				Arrays.sort(atoms);
				// Construct the conjunct
				return new Impl.Conjunct(atoms);
			}
		}
	}

	public static Type fromString(String str) {
		return new TypeParser(str).parse();
	}

	// =============================================================
	// Implementation
	// =============================================================

	abstract static class Impl implements Type, Comparable<Impl> {

		public abstract int getKind();

		/**
		 * Represents either an atom or a conjunct
		 *
		 * @author David J. Pearce
		 *
		 */
		public abstract static class Conjunctable extends Impl {

		}

		/**
		 * An atom represents an indivisible type. More specifically, a type
		 * which does not contain a union or intersection anywhere, including in
		 * subcomponents.
		 *
		 * @author David J. Pearce
		 *
		 */
		public abstract static class Atom extends Conjunctable {

		}

		/**
		 * A positive atom is any atom except a negation
		 *
		 * @author David J. Pearce
		 *
		 */
		public abstract static class PositiveAtom extends Atom {

		}

		public abstract static class Primitive extends PositiveAtom implements Type.Primitive {
			private final int kind;

			private Primitive(int kind) {
				this.kind = kind;
			}

			@Override
			public abstract String toString();

			@Override
			public int getKind() {
				return kind;
			}

			@Override
			public boolean equals(Object o) {
				if (o instanceof Impl.Primitive) {
					Impl.Primitive p = (Impl.Primitive) o;
					return kind == p.kind;
				}
				return false;
			}

			@Override
			public int hashCode() {
				return kind;
			}

			@Override
			public int compareTo(Impl p) {
				return kind - p.getKind();
			}
		}

		private static final class Nominal extends Atom implements Type.Nominal {
			private NameID nid;

			public Nominal(NameID name) {
				nid = name;
			}

			@Override
			public boolean equals(Object o) {
				if (o instanceof Impl.Nominal) {
					Impl.Nominal e = (Impl.Nominal) o;
					return nid.equals(e.nid);
				}
				return false;
			}

			@Override
			public NameID name() {
				return nid;
			}

			@Override
			public int hashCode() {
				return nid.hashCode();
			}

			@Override
			public String toString() {
				return nid.toString();
			}

			@Override
			public int getKind() {
				return TypeSystem.K_NOMINAL;
			}

			@Override
			public int compareTo(Impl p) {
				int r = TypeSystem.K_NOMINAL - p.getKind();
				if (r == 0) {
					Impl.Nominal n = (Impl.Nominal) p;
					return nid.compareTo(n.nid);
				} else {
					return r;
				}
			}
		}

		private static final class Reference extends PositiveAtom implements Type.Reference {
			private final Impl element;
			private final String lifetime;

			public Reference(Impl element, String lifetime) {
				this.element = element;
				this.lifetime = lifetime;
			}

			@Override
			public Type element() {
				return element;
			}

			@Override
			public Type getReadableElementType() {
				return element;
			}

			@Override
			public Type getWriteableElementType() {
				return element;
			}

			@Override
			public String lifetime() {
				return lifetime;
			}

			@Override
			public int getKind() {
				return TypeSystem.K_REFERENCE;
			}

			@Override
			public boolean equals(Object o) {
				if (o instanceof Impl.Reference) {
					Impl.Reference r = (Impl.Reference) o;
					return element.equals(r.element) && lifetime.equals(r.lifetime);
				}
				return false;
			}

			@Override
			public int compareTo(Impl p) {
				int r = TypeSystem.K_REFERENCE - p.getKind();
				if (r == 0) {
					Impl.Reference n = (Impl.Reference) p;
					r = element.compareTo(n.element);
					if (r == 0) {
						return lifetime.compareTo(n.lifetime);
					}
				}
				return r;
			}

			@Override
			public int hashCode() {
				return element.hashCode() + lifetime.hashCode();
			}

			@Override
			public String toString() {
				String body = element.toString();
				if (element instanceof Union || element instanceof Intersection || element instanceof Negation
						|| element instanceof Array) {
					body = "(" + body + ")";
				}
				if(lifetime.equals("*")) {
					return "&" + body;
				} else {
					return "&" + lifetime + ":" + body;
				}
			}
		}

		private static final class Array extends PositiveAtom implements Type.Array {
			private final Impl element;

			public Array(Impl element) {
				this.element = element;
			}

			@Override
			public Type element() {
				return element;
			}

			@Override
			public Type getReadableElementType() {
				return element;
			}

			@Override
			public Type getWriteableElementType() {
				return element;
			}

			@Override
			public int getKind() {
				return TypeSystem.K_LIST;
			}

			@Override
			public Impl.Array update(Type newElement) {
				return new Impl.Array((Impl) Type.Union(element, newElement));
			}

			@Override
			public boolean equals(Object o) {
				if (o instanceof Impl.Array) {
					Impl.Array a = (Impl.Array) o;
					return element.equals(a.element);
				}
				return false;
			}

			@Override
			public int compareTo(Impl p) {
				int r = TypeSystem.K_LIST - p.getKind();
				if (r == 0) {
					Impl.Array n = (Impl.Array) p;
					return element.compareTo(n.element);
				}
				return r;
			}

			@Override
			public int hashCode() {
				return element.hashCode() * 2;
			}

			@Override
			public String toString() {
				String body = element.toString();
				if (element instanceof Union || element instanceof Intersection || element instanceof Negation
						|| element instanceof Reference) {
					return "(" + element.toString() + ")[]";
				} else {
					return body + "[]";
				}
			}
		}

		private static final Comparator<Pair<Type, String>> FIELD_COMPARATOR = new Comparator<Pair<Type, String>>() {
			@Override
			public int compare(Pair<Type, String> o1, Pair<Type, String> o2) {
				return o1.second().compareTo(o2.second());
			}
		};

		private static final class Record extends PositiveAtom implements Type.Record {
			private final Pair<Impl, String>[] fields;
			private final boolean isOpen;

			public Record(boolean isOpen, Pair<Impl, String>... fields) {
				this.fields = fields;
				this.isOpen = isOpen;
			}

			@Override
			public boolean isOpen() {
				return isOpen;
			}

			@Override
			public int size() {
				return fields.length;
			}

			@Override
			public int getFieldIndex(String name) {
				for (int i = 0; i != fields.length; ++i) {
					String field = fields[i].second();
					if (field.equals(name)) {
						return i;
					}
				}
				throw new IllegalArgumentException("field not found: " + name);
			}

			@Override
			public boolean hasField(String field) {
				for (int i = 0; i != fields.length; ++i) {
					if (fields[i].second().equals(field)) {
						return true;
					}
				}
				return false;
			}

			@Override
			public String[] getFieldNames() {
				String[] rs = new String[fields.length];
				for (int i = 0; i != fields.length; ++i) {
					rs[i] = fields[i].second();
				}
				return rs;
			}

			@Override
			public Type getReadableFieldType(String field) {
				return getField(field);
			}

			@Override
			public Type getWriteableFieldType(String field) {
				return getField(field);
			}

			@Override
			public Type getField(String field) {
				for (int i = 0; i != fields.length; ++i) {
					Pair<Impl, String> p = fields[i];
					if (p.second().equals(field)) {
						return p.first();
					}
				}
				throw new IllegalArgumentException("invalid field " + field);
			}

			@Override
			public Type.Record update(String field, Type type) {
				// Find field and update (if it exists)
				Pair<Impl, String>[] nfields = Arrays.copyOf(fields, fields.length);
				for (int i = 0; i != nfields.length; ++i) {
					if (nfields[i].second().equals(field)) {
						// FIXME: this line is clearly broken
						nfields[i] = new Pair<Impl, String>((Atom) type, field);
						return new Impl.Record(isOpen, nfields);
					}
				}
				// If we get here, no match was found. This is an error.
				throw new IllegalArgumentException("invalid field " + field);
			}

			@Override
			public boolean equals(Object o) {
				if (o instanceof Impl.Record) {
					Impl.Record r = (Impl.Record) o;
					return Arrays.equals(fields, r.fields) && isOpen == r.isOpen;
				}
				return false;
			}

			@Override
			public int compareTo(Impl p) {
				int r = TypeSystem.K_RECORD - p.getKind();
				if (r == 0) {
					Impl.Record n = (Impl.Record) p;
					if (isOpen != n.isOpen) {
						return isOpen ? 1 : -1;
					} else if (fields.length != n.fields.length) {
						return fields.length - n.fields.length;
					}
					for (int i = 0; i != fields.length; ++i) {
						Pair<Impl, String> p1 = fields[i];
						Pair<Impl, String> p2 = n.fields[i];
						r = p1.first().compareTo(p2.first());
						if (r != 0) {
							return r;
						}
						r = p1.second().compareTo(p2.second());
						if (r != 0) {
							return r;
						}
					}
					return 0;
				}
				return r;
			}

			@Override
			public int hashCode() {
				int f = isOpen ? 1 : 0;
				return Arrays.hashCode(fields) + f;
			}

			@Override
			public String toString() {
				String body = "";
				boolean firstTime = true;
				for (int i = 0; i != fields.length; ++i) {
					Pair<Impl, String> f = fields[i];
					if (!firstTime) {
						body = body + ",";
					}
					firstTime = false;
					body = body + f.first() + " " + f.second();
				}
				if (isOpen) {
					body += ", ...";
				}
				return "{" + body + "}";
			}

			@Override
			public int getKind() {
				return TypeSystem.K_RECORD;
			}
		}

		private static final class Negation extends Atom implements Type.Negation {
			private final Atom element;

			private Negation(Atom element) {
				this.element = element;
			}

			@Override
			public Type element() {
				return element;
			}

			@Override
			public boolean equals(Object o) {
				if (o instanceof Impl.Negation) {
					Impl.Negation n = (Impl.Negation) o;
					return element.equals(n.element);
				}
				return false;
			}

			@Override
			public int compareTo(Impl o) {
				int r = TypeSystem.K_NEGATION - o.getKind();
				if (r == 0) {
					Impl.Negation n = (Impl.Negation) o;
					return element.compareTo(n.element);
				}
				return r;
			}

			@Override
			public int hashCode() {
				return -element.hashCode();
			}

			@Override
			public String toString() {
				String body = element.toString();
				if (element instanceof Array || element instanceof Reference) {
					return "!(" + element.toString() + ")";
				} else {
					return "!" + body;
				}
			}

			@Override
			public int getKind() {
				return TypeSystem.K_NEGATION;
			}
		}

		public abstract static class FunctionOrMethod extends Atom implements Type.FunctionOrMethod {
			protected final Impl[] parameters;
			protected final Impl[] returns;

			public FunctionOrMethod(Impl[] parameters, Impl[] returns) {
				this.parameters = parameters;
				this.returns = returns;
			}

			/**
			 * Get the return types of this function or method type.
			 *
			 * @return
			 */
			@Override
			public Impl[] returns() {
				return returns;
			}

			/**
			 * Get the parameter types of this function or method type.
			 *
			 * @return
			 */
			@Override
			public Impl[] params() {
				return parameters;
			}

			@Override
			public Impl parameter(int i) {
				return parameters[i];
			}

			@Override
			public boolean equals(Object o) {
				if (o instanceof Impl.FunctionOrMethod) {
					Impl.FunctionOrMethod f = (Impl.FunctionOrMethod) o;
					return Arrays.equals(parameters, f.parameters) && Arrays.equals(returns, f.returns);
				}
				return false;
			}

			@Override
			public int compareTo(Impl o) {
				int r = getKind() - o.getKind();
				if (r == 0) {
					Impl.FunctionOrMethod fm = (Impl.FunctionOrMethod) o;
					r = ArrayUtils.compareTo(parameters, fm.parameters);
					if (r == 0) {
						r = ArrayUtils.compareTo(returns, fm.returns);
					}
				}
				return r;
			}

			@Override
			public int hashCode() {
				int hc = Arrays.hashCode(parameters);
				hc += Arrays.hashCode(returns);
				return hc;
			}
		}

		/**
		 * A function type, consisting of a list of zero or more parameters and
		 * a return type.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Function extends FunctionOrMethod implements Type.Function {
			public Function(Impl[] parameters, Impl[] returns) {
				super(parameters, returns);
			}

			@Override
			public String toString() {
				String ps = Impl.toString(parameters);
				String rs = Impl.toString(returns);
				return "function(" + ps + ")->(" + rs + ")";
			}

			@Override
			public int getKind() {
				return TypeSystem.K_FUNCTION;
			}
		}

		public static final class Method extends FunctionOrMethod implements Type.Method {
			protected final String[] lifetimeParameters;
			protected final String[] contextLifetimes;

			public Method(Impl[] parameters, Impl[] returns) {
				super(parameters, returns);
				this.lifetimeParameters = new String[0];
				this.contextLifetimes = new String[0];
			}

			public Method(String[] lifetimeParameters, String[] contextLifetimes, Impl[] parameters, Impl[] returns) {
				super(parameters, returns);
				this.lifetimeParameters = lifetimeParameters;
				this.contextLifetimes = contextLifetimes;
			}

			public Method(Collection<String> lifetimeParameters, Collection<String> contextLifetimes, Impl[] parameters,
					Impl[] returns) {
				super(parameters, returns);
				this.lifetimeParameters = ArrayUtils.toStringArray(lifetimeParameters);
				this.contextLifetimes = ArrayUtils.toStringArray(contextLifetimes);
			}

			@Override
			public boolean equals(Object o) {
				if (o instanceof Impl.Method) {
					Impl.Method m = (Impl.Method) o;
					return Arrays.equals(lifetimeParameters, m.lifetimeParameters)
							&& Arrays.equals(contextLifetimes, m.contextLifetimes) && super.equals(o);
				}
				return false;
			}

			@Override
			public int hashCode() {
				int hc = super.hashCode();
				hc += Arrays.hashCode(lifetimeParameters);
				hc += Arrays.hashCode(contextLifetimes);
				return hc;
			}

			/**
			 * Get the context lifetimes of this function or method type.
			 *
			 * @return
			 */
			@Override
			public String[] contextLifetimes() {
				return contextLifetimes;
			}

			/**
			 * Get the lifetime parameters of this function or method type.
			 *
			 * @return
			 */
			@Override
			public String[] lifetimeParams() {
				return lifetimeParameters;
			}

			@Override
			public String toString() {
				String prefix = "method";
				if (contextLifetimes.length > 0) {
					prefix += "[" + Impl.<String>toString(contextLifetimes) + "]";
				}
				if (lifetimeParameters.length > 0) {
					prefix += "<" + Impl.<String>toString(lifetimeParameters) + ">";
				}
				String ps = Impl.<Impl>toString(parameters);
				String rs = Impl.<Impl>toString(returns);
				return prefix + "(" + ps + ")->(" + rs + ")";
			}

			@Override
			public int getKind() {
				return TypeSystem.K_METHOD;
			}
		}

		private static class Conjunct extends Conjunctable implements Intersection {
			private final Impl.Atom[] atoms;

			private Conjunct(Impl.Atom... atoms) {
				this.atoms = atoms;
			}

			/**
			 * Return the bounds of this union type.
			 *
			 * @return
			 */
			@Override
			public Type[] bounds() {
				return atoms;
			}

			@Override
			public int getKind() {
				return TypeSystem.K_INTERSECTION;
			}

			@Override
			public boolean equals(Object o) {
				if (o instanceof Impl.Conjunct) {
					Impl.Conjunct r = (Impl.Conjunct) o;
					return Arrays.equals(atoms, r.atoms);
				}
				return false;
			}

			@Override
			public int compareTo(Impl p) {
				int r = TypeSystem.K_INTERSECTION - p.getKind();
				if (r == 0) {
					Impl.Conjunct n = (Impl.Conjunct) p;
					r = ArrayUtils.compareTo(atoms, n.atoms);
				}
				return r;
			}

			@Override
			public int hashCode() {
				return Arrays.hashCode(atoms);
			}

			@Override
			public String toString() {
				String body = "";
				for (int i = 0; i != atoms.length; ++i) {
					Type element = atoms[i];
					if (i != 0) {
						body = body + "&";
					}
					if (element instanceof Union || element instanceof Reference) {
						body = body + "(" + element + ")";
					} else {
						body = body + element;
					}
				}
				return body;
			}
		}

		private static abstract class Union<T extends Impl> extends Impl implements Type.Union {
			// INVARIANT: elements in sorted order
			// INVARIANT: elements > 1
			protected final T[] elements;

			private Union(T... elements) {
				this.elements = elements;
			}

			/**
			 * Return the bounds of this union type.
			 *
			 * @return
			 */
			@Override
			public T[] bounds() {
				return elements;
			}

			@Override
			public boolean equals(Object o) {
				if (o instanceof Impl.Union) {
					Impl.Union r = (Impl.Union) o;
					return Arrays.equals(elements, r.elements);
				}
				return false;
			}

			@Override
			public int compareTo(Impl p) {
				int r = TypeSystem.K_UNION - p.getKind();
				if (r == 0) {
					Impl.Union<?> n = (Impl.Union<?>) p;
					if (elements.length != n.elements.length) {
						return elements.length - n.elements.length;
					}
					for (int i = 0; i != elements.length; ++i) {
						Impl p1 = elements[i];
						Impl p2 = n.elements[i];
						r = p1.compareTo(p2);
						if (r != 0) {
							return r;
						}
					}
					return 0;
				}
				return r;
			}

			@Override
			public int hashCode() {
				return Arrays.hashCode(elements);
			}

			@Override
			public String toString() {
				String body = "";
				for (int i = 0; i != elements.length; ++i) {
					Type element = elements[i];
					if (i != 0) {
						body = body + "|";
					}
					if (element instanceof Intersection || element instanceof Reference) {
						body = body + "(" + element + ")";
					} else {
						body = body + element;
					}
				}
				return body;
			}

			@Override
			public int getKind() {
				return TypeSystem.K_UNION;
			}
		}

		private static class UnionOfConjunctables extends Union<Conjunctable> {
			public UnionOfConjunctables(Conjunctable... conjunctables) {
				super(conjunctables);
			}
		}

		/**
		 * A union of records corresponds to an <i>effective record</i> in
		 * Whiley. This means it can be treated in a similar fashion to a record
		 * in many situations.
		 *
		 * @author David J. Pearce
		 *
		 */
		private static class UnionOfRecords extends Union<Impl.Record> implements EffectiveRecord {
			public UnionOfRecords(Impl.Record... records) {
				super(records);
				if (records.length < 2) {
					throw new IllegalArgumentException("insufficient records provided");
				}
			}

			@Override
			public int size() {
				return getFieldNames().length;
			}

			@Override
			public int getFieldIndex(String field) {
				// FIXME: it's not really clear what this method should do in
				// this circumstance.
				int i = ArrayUtils.firstIndexOf(getFieldNames(), field);
				if (i < 0) {
					throw new IllegalArgumentException("field not found: " + field);
				} else {
					return i;
				}
			}

			@Override
			public boolean hasField(String field) {
				for (int i = 0; i != elements.length; ++i) {
					if (!elements[i].hasField(field)) {
						return false;
					}
				}
				return true;
			}

			@Override
			public String[] getFieldNames() {
				// To compute the set of fields accessible from a union of
				// records, we must intersect the the set of available fields in
				// each nested record.
				String[] first = elements[0].getFieldNames();
				String[] result = new String[first.length];

				outer: for (int i = 0; i != result.length; ++i) {
					// FIXME: this is a slight cheat, since it's only computing
					// the common initial sequence for fields. At this stage,
					// it's unclear whether or not this is sufficient. In
					// particular, whether or not the language design should
					// permit more flexibility here.
					String field = first[i];
					for (int j = 0; j != elements.length; ++j) {
						if (!elements[j].hasField(field)) {
							break outer;
						}
					}
					result[i] = field;
				}

				return result;
			}

			@Override
			public Type getReadableFieldType(String field) {
				Type readable = elements[0].getField(field);
				for (int i = 1; i != elements.length; ++i) {
					readable = Union(readable, elements[i].getField(field));
				}
				return readable;
			}

			@Override
			public Type getWriteableFieldType(String field) {
				Type readable = elements[0].getField(field);
				for (int i = 1; i != elements.length; ++i) {
					readable = Intersection(readable, elements[i].getField(field));
				}
				return readable;
			}

			@Override
			public EffectiveRecord update(String field, Type type) {
				Impl.Record[] records = new Impl.Record[elements.length];
				for (int i = 0; i != elements.length; ++i) {
					records[i] = (Impl.Record) elements[i].update(field, type);
				}
				return (EffectiveRecord) Union(records);
			}
		}

		/**
		 * A union of arrays corresponds to an <i>effective array</i> in Whiley.
		 * This means it can be treated in a similar fashion to a array in many
		 * situations.
		 *
		 * @author David J. Pearce
		 *
		 */
		private static class UnionOfArrays extends Union<Impl.Array> implements EffectiveArray {
			public UnionOfArrays(Impl.Array... arrays) {
				super(arrays);
				if (arrays.length < 2) {
					throw new IllegalArgumentException("insufficient arrays provided");
				}
			}

			@Override
			public Type getReadableElementType() {
				Type readable = elements[0].element();
				for (int i = 1; i != elements.length; ++i) {
					readable = Union(readable, elements[i].element());
				}
				return readable;
			}

			@Override
			public Type getWriteableElementType() {
				Type readable = elements[0].element();
				for (int i = 1; i != elements.length; ++i) {
					readable = Intersection(readable, elements[i].element());
				}
				return readable;
			}

			@Override
			public EffectiveArray update(Type type) {
				Impl.Array[] arrays = new Impl.Array[elements.length];
				for (int i = 0; i != elements.length; ++i) {
					arrays[i] = elements[i].update(type);
				}
				return (EffectiveArray) Union(arrays);
			}
		}

		private static <T> String toString(T[] things) {
			String body = "";
			boolean firstTime = true;
			for (T thing : things) {
				if (!firstTime) {
					body = body + ",";
				}
				firstTime = false;
				body = body + thing;
			}
			return body;
		}

		// =============================================================
		// Simplifications
		// =============================================================

		/**
		 * Identify the common kind (if any) amongst a sequence of two or more
		 * types.
		 *
		 * @param cs
		 * @return
		 */
		static int determineCommonKind(Impl.Conjunctable... cs) {
			int kind = cs[0].getKind();
			for (int i = 1; i != cs.length; ++i) {
				if (cs[i].getKind() != kind) {
					return -1;
				}
			}
			return kind;
		}

		/**
		 * Flatten a sequence of zero or more types into a sequence of zero or
		 * more conjunctables.union types to form one big array of types. The
		 * resulting sequence may be larger than the original sequence. For
		 * example, if the original sequence contains a union of some sort.
		 *
		 * @param types
		 * @return
		 */
		private static Conjunctable[] toConjunctables(Type... types) {
			int length = 0;
			for (int i = 0; i != types.length; ++i) {
				Type ith = types[i];
				if (ith instanceof Union) {
					Union ut = (Union) ith;
					length = length + ut.bounds().length;
				} else {
					length = length + 1;
				}
			}
			Conjunctable[] nTypes = new Conjunctable[length];
			for (int i = 0, j = 0; i != types.length; ++i) {
				Type ith = types[i];
				if (ith instanceof Union) {
					Union ut = (Union) ith;
					Type[] ut_bounds = ut.bounds();
					System.arraycopy(ut_bounds, 0, nTypes, j, ut_bounds.length);
					j += ut_bounds.length;
				} else {
					nTypes[j++] = (Conjunctable) ith;
				}
			}
			return nTypes;
		}

		/**
		 * Flattern union types to form one big array of types.
		 *
		 * @param types
		 * @return
		 */
		private static Atom[] toAtoms(Type... types) {
			int length = 0;
			for (int i = 0; i != types.length; ++i) {
				Type ith = types[i];
				if (ith instanceof Intersection) {
					Intersection it = (Intersection) ith;
					length = length + it.bounds().length;
				} else {
					length = length + 1;
				}
			}
			//
			Atom[] nTypes = new Atom[length];
			for (int i = 0, j = 0; i != types.length; ++i) {
				Type ith = types[i];
				if (ith instanceof Intersection) {
					// This indicates a nested intersection. This needs to
					// be flatterned out, since an intersection is not an
					// atom.
					Intersection it = (Intersection) ith;
					Type[] ut_bounds = it.bounds();
					System.arraycopy(ut_bounds, 0, nTypes, j, ut_bounds.length);
					j += ut_bounds.length;
				} else {
					// At this stage, we can assume ith is not a union.
					// This is because unions are previously removed early
					// in Intersection by distributing over them.
					nTypes[j++] = (Atom) ith;
				}
			}
			return nTypes;
		}

		/**
		 * Determine the first index of a union within the sequence of types, or
		 * -1 if none. This is used to find the distribution point within a
		 * given set of atoms being intersected.
		 *
		 * @param types
		 * @return
		 */
		private static int findUnion(Type... types) {
			for (int i = 0; i != types.length; ++i) {
				Type ith = types[i];
				if (ith instanceof Union) {
					return i;
				}
			}
			// We didn't find any unions
			return -1;
		}

		/**
		 * Apply the laws of distributivity for a sequence of one or more types
		 * and a given union within that sequence. Specifically, for any nested
		 * unions, we must extract an distribute over them. This is a relatively
		 * expensive operation, and the performance of the algorithm implemented
		 * here could be vastly improved with some care.
		 *
		 * @param types
		 * @return
		 */
		private static Type distributeUnion(int unionIndex, Type... types) {
			Union<?> ut = (Union<?>) types[unionIndex];
			Type[] ut_bounds = ut.bounds();
			// Distribute over the union
			Type[] clauses = new Type[ut_bounds.length];
			for (int j = 0; j != ut_bounds.length; ++j) {
				Type jth = ut_bounds[j];
				Type[] tmp = Arrays.copyOf(types, types.length);
				tmp[unionIndex] = jth;
				clauses[j] = Intersection(tmp);
			}
			return Union(clauses);
		}

		/**
		 * <p>
		 * Intersect two types which are atoms (and not null). That is, they are
		 * neither unions nor intersections. In many cases, either the two types
		 * are the same or no intersection is possible. In some cases (e.g. for
		 * records and arrays), we must recursively intersect the element types
		 * to decide whether an intersection is possible.
		 * </p>
		 * <p>
		 * As part of the intersection process, the algorithm will combine types
		 * where appropriate. For example, duplicates are combined into tone. In
		 * such case, the result is stored in the ith position and the jth
		 * position is set to null.
		 * </p>
		 * <p>
		 * Finally, if the algorithm determines the overall type is void then it
		 * will stop early and return false to signal a failure.
		 * </p>
		 *
		 * @param i
		 *            ith index to consider
		 * @param j
		 *            jth index to consider
		 * @param types
		 *            Array of types being intersected
		 */
		private static void intersectAtoms(int i, int j, Atom[] types) {
			// System.out.println("INTERSECTING: " + i + ", " + j + " in " +
			// Arrays.toString(types));
			Atom ith = types[i];
			Atom jth = types[j];

			int ith_kind = ith.getKind();
			int jth_kind = jth.getKind();

			if (ith_kind == TypeSystem.K_NEGATION && jth_kind == TypeSystem.K_NEGATION) {
				// Intersection of negative atoms
				intersectNegativeNegative(i, j, types);
			} else if (ith_kind == TypeSystem.K_NEGATION) {
				// Intersection of negative with positive
				intersectNegativePositive(i, j, types);
			} else if (jth_kind == TypeSystem.K_NEGATION) {
				// Intersection of positive with negative
				intersectNegativePositive(j, i, types);
			} else {
				// Intersection of positive with positive
				intersectPositivePositive(i, j, types);
			}
		}

		private static void intersectNegativeNegative(int i, int j, Atom[] types) {
			// FIXME: Unsure what could do here.
		}

		private static void intersectNegativePositive(int i, int j, Atom[] types) {
			Negation ith = (Negation) types[i];
			Impl.Atom ith_element = ith.element;
			Atom jth = types[j];
			if(jth == T_ANY) {
				// Do nothing as any will be dropped eventually.
			} else if(ith_element.equals(jth)) {
				// FIXME: should do more here as there are other cases where we
				// should reduce to void. For example, if jth element is
				// supertype of ith.
				types[i] = (Atom) T_VOID;
				types[j] = (Atom) T_VOID;
			} else if(ith_element instanceof Nominal || jth instanceof Nominal) {
				// There's not much we can do here, since we can't be sure
				// whether or not the Nominal types having anything in common.
			} else {
				// ith is subsumed
				types[i] = (Atom) T_ANY;
			}
		}

		private static void intersectPositivePositive(int i, int j, Atom[] types) {
			Atom ith = types[i];
			Atom jth = types[j];
			//
			int ith_kind = ith.getKind();
			int jth_kind = jth.getKind();
			//
			if (ith_kind == TypeSystem.K_ANY || jth_kind == TypeSystem.K_ANY) {
				// In this case, there is nothing really to do. Basically
				// intersection something with any gives something.
			} else if (ith_kind == TypeSystem.K_NOMINAL || jth_kind == TypeSystem.K_NOMINAL) {
				// In this case, there is also nothing to do. That's because
				// we don't know what a nominal is, and hence we are
				// essentially treating it as being the same as any.
			} else if (ith_kind != jth_kind) {
				// There are no situations where this results in a positive
				// outcome. Therefore, set both parties to be void. This
				// guarantees the void will be caught at the earliest possible
				// moment,
				types[i] = (Atom) T_VOID;
				types[j] = (Atom) T_VOID;
			} else {
				// Now check individual cases
				switch (ith_kind) {
				case TypeSystem.K_VOID:
				case TypeSystem.K_BOOL:
				case TypeSystem.K_BYTE:
				case TypeSystem.K_META:
				case TypeSystem.K_INT:
					// For primitives, it's enough to know that they have
					// the same kind.
					break;
				case TypeSystem.K_LIST: {
					// For arrays, we need to know whether or not their element
					// types intersect.
					types[i] = (Atom) intersectArray((Impl.Array) types[i], (Impl.Array) types[j]);
					types[j] = (Atom) T_ANY;
					break;
				}
				case TypeSystem.K_RECORD: {
					// For records, we need to know whether their fields match
					// appropriately and whether or not they intersect.
					types[i] = (Atom) intersectRecord((Impl.Record) types[i], (Impl.Record) types[j]);
					types[j] = (Atom) T_ANY;
					break;
				}
				case TypeSystem.K_REFERENCE: {
					// For arrays, we need to know whether or not their element
					// types match exactly.
					types[i] = (Atom) intersectReference((Impl.Reference) types[i], (Impl.Reference) types[j]);
					types[j] = (Atom) T_ANY;
					break;
				}
				case TypeSystem.K_FUNCTION:
				case TypeSystem.K_METHOD:
				default:
					throw new RuntimeException("DEADCODE REACHED");
				}
			}
		}

		/**
		 * <p>
		 * Intersect one record type with another. The computation is fairly
		 * straight-forward. For every field in common, we intersect their
		 * types. For example, <code>{int|null f}&{int|bool f}</code> gives
		 * <code>{int f}</code> because <code>(int|null)&(int|bool)</code> gives
		 * <code>int</code>.
		 * </p>
		 *
		 * <p>
		 * In the case that there are fields in one but not the other, the
		 * result is void. The only exception is when one or both of the records
		 * is open. When both records are open, there is always an intersection.
		 * When only one is open, then it's fields must be a subset of the
		 * other.
		 * </p>
		 *
		 * @param ith
		 *            First record type to intersect
		 * @param jth
		 *            Second record type to intersect
		 */
		private static Type intersectRecord(Impl.Record ith, Impl.Record jth) {
			if (ith.isOpen && jth.isOpen) {
				return intersectOpenOpenRecord(ith, jth);
			} else if (ith.isOpen) {
				return intersectOpenClosedRecord(ith, jth);
			} else if (jth.isOpen) {
				return intersectOpenClosedRecord(jth, ith);
			} else {
				return intersectClosedClosedRecord(jth, ith);
			}
		}

		/**
		 * When intersecting two closed records, we require they both have the
		 * same set of fields. Then we simply intersect each respective field.
		 *
		 * @param ith
		 * @param jth
		 * @return
		 */
		public static Type intersectClosedClosedRecord(Impl.Record ith, Impl.Record jth) {
			// ith and jth fields should be in sorted order by field name
			Pair<Impl, String>[] ith_fields = ith.fields;
			Pair<Impl, String>[] jth_fields = jth.fields;
			// Sanity check the lengths
			if (ith_fields.length != jth_fields.length) {
				// Since the lengths are different, we know these records cannot
				// be intersected.
				return T_VOID;
			} else {
				// Lengths are equal, so check fields match and intersect them.
				Pair<Type, String>[] new_fields = new Pair[ith_fields.length];
				for (int i = 0; i != ith_fields.length; ++i) {
					Pair<Impl, String> ith_field = ith_fields[i];
					Pair<Impl, String> jth_field = jth_fields[i];
					String ith_name = ith_field.second();
					String jth_name = jth_field.second();
					if (ith_name.equals(jth_name)) {
						Type type = Intersection(ith_field.first(), jth_field.first());
						new_fields[i] = new Pair<Type, String>(type, ith_name);
					} else {
						// We've found a field that is not comon to both.
						return T_VOID;
					}
				}
				return Type.Record(false, new_fields);
			}
		}

		/**
		 * When intersecting an open record with a closed record. In this case,
		 * the result is void if there is a field in the open record not present
		 * in the closed record. Otherwise, the result is the intersection of
		 * those fields common to both with the remainder taken from the closed
		 * record.
		 *
		 * @param open
		 * @param jth
		 * @return
		 */
		public static Type intersectOpenClosedRecord(Impl.Record open, Impl.Record closed) {
			// ith and jth fields should be in sorted order by field name
			Pair<Impl, String>[] open_fields = open.fields;
			Pair<Impl, String>[] closed_fields = closed.fields;
			// Sanity check the lengths
			if (open_fields.length > closed_fields.length) {
				// In this case, it is impossible for those fields in the open
				// record to be a subset of those in the closed record.
				return T_VOID;
			} else {
				// The aim now is to
				// Lengths are equal, so check fields match and intersect them.
				Pair<Type, String>[] new_fields = new Pair[closed_fields.length];
				for (int i = 0, j = 0; i != new_fields.length; ++i) {
					Pair<Impl, String> closed_field = closed_fields[i];
					String closed_name = closed_field.second();
					if (j < open_fields.length) {
						Pair<Impl, String> open_field = open_fields[j];
						String open_name = open_field.second();
						int c = open_name.compareTo(closed_name);
						if (c > 0) {
							// In this case, there is a field in the closed
							// record not present in the open record.
							new_fields[i] = (Pair) closed_field;
						} else if (c == 0) {
							// In this case, there is a field common to both the
							// closed and open records.
							Type type = Intersection(open_field.first(), closed_field.first());
							new_fields[i] = new Pair<Type, String>(type, open_name);
							j = j + 1;
						} else {
							// In this case, there is a field in the open record
							// which is not in the closed record. Hence, no
							// intersection is possible.
							return T_VOID;
						}
					} else {
						// In this case, there is a field in the closed record
						// not present in the open record.
						new_fields[i] = (Pair) closed_fields[i];
					}
				}
				return Type.Record(false, new_fields);
			}
		}

		/**
		 * When intersecting an open record with another open record, there is
		 * always a resulting intersection. In this case, fields common to both
		 * are intersected and the remainder are pulled as is.
		 *
		 * @param ith
		 * @param jth
		 * @return
		 */
		public static Type intersectOpenOpenRecord(Impl.Record ith, Impl.Record jth) {
			// ith and jth fields should be in sorted order by field name
			Pair<Impl, String>[] ith_fields = ith.fields;
			Pair<Impl, String>[] jth_fields = jth.fields;
			ArrayList<Pair<Type, String>> new_fields = new ArrayList<>();
			int i = 0;
			int j = 0;
			while (i < ith_fields.length && j < jth_fields.length) {
				Pair<Impl, String> ith_field = ith_fields[i];
				Pair<Impl, String> jth_field = jth_fields[j];
				String ith_name = ith_field.second();
				String jth_name = jth_field.second();
				int c = ith_name.compareTo(jth_name);
				if (c < 0) {
					// In this case, there is a field in the ith
					// record not present in the jth record.
					new_fields.add((Pair) ith_fields[i]);
					i = i + 1;
				} else if (c == 0) {
					// In this case, there is a field common to both the
					// closed and open records.
					Type type = Intersection(ith_field.first(), jth_field.first());
					new_fields.add(new Pair<Type, String>(type, ith_name));
					i = i + 1;
					j = j + 1;
				} else {
					// In this case, there is a field in the jth
					// record not present in the ith record.
					new_fields.add((Pair) jth_fields[i]);
					j = j + 1;
				}
			}
			// Finally, tidy up any lose ends
			for (; i < ith_fields.length; i = i + 1) {
				new_fields.add((Pair) ith_fields[i]);
			}
			for (; j < jth_fields.length; j = j + 1) {
				new_fields.add((Pair) jth_fields[j]);
			}
			// Done
			return Type.Record(false, new_fields);
		}

		/**
		 * The intersection of two arrays is simply the intersection of their
		 * element types. Observe that if this is void, then the resulting type
		 * will be void as well.
		 *
		 * @param ith
		 * @param jth
		 * @return
		 */
		private static Type intersectArray(Type.Array ith, Type.Array jth) {
			Type element = Intersection(ith.element(), jth.element());
			return Type.Array(element);
		}

		/**
		 * The intersection of two references is only possible if their type
		 * match exactly.
		 *
		 * @param ith
		 * @param jth
		 * @return
		 */
		private static Type intersectReference(Type.Reference ith, Type.Reference jth) {
			if(ith.element().equals(jth.element())) {
				return ith;
			} else {
				return Type.T_VOID;
			}
		}
	}
}
