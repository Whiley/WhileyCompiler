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

import wyil.util.Pair;
import wyil.util.TypeParser;
import wyts.lang.*;
import wyts.lang.Automata.State;
import wyts.util.PrintBuilder;
import wyts.util.TypeBuilder;

/**
 * A structural type. See
 * http://whiley.org/2011/03/07/implementing-structural-types/ for more on how
 * this class works.
 * 
 * @author djp
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
	public static final Char T_CHAR = new Char();
	public static final Int T_INT = new Int();		
	public static final Real T_REAL = new Real();
	public static final Strung T_STRING = new Strung();	
	public static final Meta T_META = new Meta();
	
	/**
	 * Construct a tuple type using the given element types.
	 * 
	 * @param element
	 */
	public static final Tuple T_TUPLE(Type... elements) {		
		return new Tuple(construct(Automata.K_TUPLE,null,elements));
	}
	
	/**
	 * Construct a tuple type using the given element types.
	 * 
	 * @param element
	 */
	public static final Tuple T_TUPLE(java.util.List<Type> elements) {
		return new Tuple(construct(Automata.K_TUPLE,null,elements));
	}
	
	/**
	 * Construct a process type using the given element type.
	 * 
	 * @param element
	 */
	public static final Process T_PROCESS(Type element) {
		return new Process(construct(Automata.K_PROCESS,null,element));		
	}
	
	public static final Existential T_EXISTENTIAL(NameID name) {
		if (name == null) {
			throw new IllegalArgumentException(
					"existential name cannot be null");
		}
		return new Existential(name);
	}
	
	/**
	 * Construct a set type using the given element type.
	 * 
	 * @param element
	 */
	public static final Set T_SET(Type element) {
		return new Set(construct(Automata.K_SET,null,element));		
	}
	
	/**
	 * Construct a list type using the given element type.
	 * 
	 * @param element
	 */
	public static final List T_LIST(Type element) {
		return new List(construct(Automata.K_LIST,null,element));		
	}
	
	/**
	 * Construct a dictionary type using the given key and value types.
	 * 
	 * @param element
	 */
	public static final Dictionary T_DICTIONARY(Type key, Type value) {
		return new Dictionary(construct(Automata.K_DICTIONARY,null,key,value));			
	}
	
	/**
	 * Construct a union type using the given type bounds
	 * 
	 * @param element
	 */
	public static final Union T_UNION(Collection<Type> bounds) {
		return new Union(construct(Automata.K_UNION,null,bounds));		
	}
	
	/**
	 * Construct a union type using the given type bounds
	 * 
	 * @param element
	 */
	public static final Union T_UNION(Type... bounds) {
		return new Union(construct(Automata.K_UNION,null,bounds));			
	}	
	
	/**
	 * Construct an intersection type using the given type bounds
	 * 
	 * @param element
	 */
	public static final Type T_INTERSECTION(Collection<Type> bounds) {
		return new Intersection(construct(Automata.K_INTERSECTION,null,bounds));
	}
	
	/**
	 * Construct an intersection type using the given type bounds
	 * 
	 * @param element
	 */
	public static final Type T_INTERSECTION(Type... bounds) {
		return new Intersection(construct(Automata.K_INTERSECTION,null,bounds));			
	}	
	
	/**
	 * Construct a difference of two types.
	 * 
	 * @param element
	 */
	public static final Type T_DIFFERENCE(Type left, Type right) {
		return new Difference(construct(Automata.K_DIFFERENCE,null,left,right));				
	}
	
	/**
	 * Construct a function type using the given return and parameter types.
	 * 
	 * @param element
	 */
	public static final Fun T_FUN(Type ret,
			Collection<Type> params) {
		Type[] rparams = new Type[params.size()+1];		
		int i = 0;
		for (Type t : params) { rparams[++i] = t; }		
		rparams[0] = ret;
		return new Fun(construct(Automata.K_FUNCTION,null,rparams));		
	}
	
	/**
	 * Construct a function type using the given return and parameter types.
	 * 
	 * @param element
	 */
	public static final Fun T_FUN(Type ret, Type... params) {
		Type[] rparams = new Type[params.length+1];		
		System.arraycopy(params, 0, rparams, 1, params.length);
		rparams[0] = ret;
		return new Fun(construct(Automata.K_FUNCTION,null,rparams));				
	}
	
	/**
	 * Construct a method type using the given receiver, return and parameter types.
	 * 
	 * @param element
	 */
	public static final Meth T_METH(Process receiver, Type ret,
			Collection<Type> params) {
		Type[] rparams = new Type[params.size()+1];		
		int i = 1;
		for (Type t : params) { rparams[++i] = t; }		
		rparams[0] = receiver;
		rparams[1] = ret;
		return new Meth(construct(Automata.K_METHOD,null,rparams));		
	}
	
	/**
	 * Construct a function type using the given return and parameter types.
	 * 
	 * @param element
	 */
	public static final Meth T_METH(Process receiver, Type ret, Type... params) {				
		Type[] rparams = new Type[params.length+2];		
		System.arraycopy(params, 0, rparams, 2, params.length);
		rparams[0] = receiver;
		rparams[1] = ret;
		return new Meth(construct(Automata.K_METHOD,null,rparams));		
	}
	
	/**
	 * Construct a record type using the given fields.
	 * 
	 * @param element
	 */
	public static final Record T_RECORD(Map<String,Type> fields) {				
		java.util.Set<String> keySet = fields.keySet();
		String[] keys = keySet.toArray(new String[keySet.size()]);
		Arrays.sort(keys);
		Type[] types = new Type[keys.length];
		for(int i=0;i!=types.length;++i) {
			types[i] = fields.get(keys[i]);
		}
		return new Record(construct(Automata.K_RECORD,keys,types));				
	}

	/**
	 * Construct a label type. These are used in the construction of recursive
	 * types. Essentially, a label corresponds to the leaf of a recursive type,
	 * which we can then "close" later on as we build up the type. For example,
	 * we construct the recursive type <code>X<null|{X next}></code> as follows:
	 * 
	 * <pre>
	 * HashMap<String,Type> fields = new HashMap<String,Type>();
	 * fields.put("next",T_LABEL("X"));	 * 
	 * Type tmp = T_UNION(T_NULL,T_RECORD(fields));
	 * Type type = T_RECURSIVE("X",tmp);
	 * </pre>
	 * 
	 * <b>NOTE:</b> a type containing a label is not considered valid until it
	 * is closed using a recursive type.
	 * 
	 * @param label
	 * @return
	 */
	public static final Type T_LABEL(String label) {
		return new Automata(construct(Automata.K_LABEL,label));
	}

	/**
	 * Close a recursive type using a given label. Essentially, this traverses
	 * the given type and routes each occurrence of the label to recursively
	 * point to the type's root. For example, we construct the recursive type
	 * <code>X<null|{X next}></code> as follows:
	 * 
	 * <pre>
	 * HashMap<String,Type> fields = new HashMap<String,Type>();
	 * fields.put("next",T_LABEL("X"));	 * 
	 * Type tmp = T_UNION(T_NULL,T_RECORD(fields));
	 * Type type = T_RECURSIVE("X",tmp);
	 * </pre>
	 * 
	 * <b>NOTE:</b> it is invalid to close a type which does not contain at
	 * least one instance of the given label. Doing this will cause an exception
	 * to be raised.
	 * 
	 * @param label
	 *            --- label to be used for closing.
	 * @param type
	 *            --- type to be closed.
	 * @return
	 */
	public static final Type T_RECURSIVE(String label, Type type) {
		// first stage, identify all matching labels
		if(type instanceof Leaf) { throw new IllegalArgumentException("cannot close a leaf type"); }
		Automata compound = (Automata) type;
		State[] nodes = compound.states;
		int[] rmap = new int[nodes.length];		
		int nmatches = 0;
		for(int i=0;i!=nodes.length;++i) {
			State c = nodes[i];
			if(c.kind == Automata.K_LABEL && c.data.equals(label)) {
				rmap[i] = 0;
				nmatches++;
			} else {
				rmap[i] = i - nmatches;
			}
		}
		if (nmatches == 0) {
			throw new IllegalArgumentException(
					"type cannot be closed, as it contains no matching labels");
		}
		State[] newnodes = new State[nodes.length-nmatches];
		nmatches = 0;
		for(int i=0;i!=nodes.length;++i) {
			State c = nodes[i];
			if(c.kind == Automata.K_LABEL && c.data.equals(label)) {				
				nmatches++;
			} else {
				newnodes[i-nmatches] = Automata.remap(nodes[i],rmap);
			}
		}
		return construct(newnodes);
	}

	/**
	 * The following code converts a "type string" into an actual type. This is
	 * useful, amongst other things, for debugging.
	 * 
	 * @param str
	 * @return
	 */
	public static Type fromString(String str) {
		return new TypeParser(str).parse();
	}
		
	/**
	 * This is a utility helper for constructing types. In particular, it's
	 * useful for determine whether or not a type needs to be closed. An open
	 * type is one which contains a "dangling" reference to some node which
	 * needs to be connected to back to form a cycle.
	 * 
	 * @param label
	 * @param t
	 * @return
	 */
	public static boolean isOpen(String label, Type t) {
		if (t instanceof Leaf) {
			return false;
		}
		Automata graph = (Automata) t;
		for (State n : graph.states) {
			if (n.kind == Automata.K_LABEL && n.data.equals(label)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This is a utility helper for constructing types. In particular, it's
	 * useful to check that a type has been built sanely.
	 * 
	 * @param label
	 * @param t
	 * @return
	 */
	public static boolean isOpen(Type t) {
		if (t instanceof Leaf) {
			return false;
		}
		Automata graph = (Automata) t;
		for (State n : graph.states) {			
			if (n.kind == Automata.K_LABEL) {
				return true;
			}
		}
		return false;
	}	
		
	// =============================================================
	// Type operations
	// =============================================================

		/**
	 * Determine whether type <code>t2</code> is a <i>coercive subtype</i> of
	 * type <code>t1</code> (written t1 :> t2). In other words, whether the set
	 * of all possible values described by the type <code>t2</code> is a subset
	 * of that described by <code>t1</code>.
	 */
	public static boolean isCoerciveSubtype(Type t1, Type t2) {				
		State[] g1 = nodes(t1);
		State[] g2 = nodes(t2);
		Relation inference = new CoerciveSubtypeOperator(g1,g2);		
		Relation rel = inference.doInference();		
		return rel.isSubSet(0, 0); 
	}
	
	/**
	 * Determine whether type <code>t2</code> is a <i>subtype</i> of type
	 * <code>t1</code> (written t1 :> t2). In other words, whether the set of
	 * all possible values described by the type <code>t2</code> is a subset of
	 * that described by <code>t1</code>.
	 */
	public static boolean isSubtype(Type t1, Type t2) {				
		State[] g1 = nodes(t1);
		State[] g2 = nodes(t2);
		Relation inference = new DefaultSubtypeOperator(g1,g2);		
		Relation rel = inference.doInference();		
		return rel.isSubSet(0, 0); 
	}

	/**
	 * Check whether two types are <i>isomorphic</i>. This is true if they are
	 * identical, or encode the same structure.
	 * 
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static boolean isomorphic(Type t1, Type t2) {
		return isSubtype(t1,t2) && isSubtype(t2,t1);
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
	public static Type leastUpperBound(Type t1, Type t2) {
		return minimise(T_UNION(t1,t2)); // so easy
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
	public static Type greatestLowerBound(Type t1, Type t2) {
		return minimise(T_INTERSECTION(t1,t2)); // so easy
	}

	/**
	 * Let <code>S</code> be determined by subtracting the set of values
	 * described by type <code>t2</code> from that described by <code>t1</code>.
	 * Then, this method returns the <i>least</i> type <code>t3</code> which
	 * covers <code>S</code> (that is, every value in <code>S</code> is in the
	 * set of values described by <code>t3</code>). Unfortunately, in some
	 * cases, <code>t3</code> may contain other (spurious) values not found in
	 * <code>S</code>.
	 * 
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static Type leastDifference(Type t1, Type t2) {
		return minimise(T_DIFFERENCE(t1,t2)); // so easy
	}

	/**
	 * The effective record type gives a subset of the visible fields which are
	 * guaranteed to be in the type. For example, consider this type:
	 * 
	 * <pre>
	 * {int op, int x} | {int op, [int] y}
	 * </pre>
	 * 
	 * Here, the field op is guaranteed to be present. Therefore, the effective
	 * record type is just <code>{int op}</code>.
	 * 
	 * @param t
	 * @return
	 */
	public static Record effectiveRecordType(Type t) {
		if (t instanceof Type.Record) {
			return (Type.Record) t;
		} else if (t instanceof Type.Union) {
			Union ut = (Type.Union) t;
			Record r = null;
			for (Type b : ut.bounds()) {
				if (!(b instanceof Record)) {
					return null;
				}
				Record br = (Record) b;
				if (r == null) {
					r = br;
				} else {
					HashMap<String, Type> rfields = r.fields();
					HashMap<String, Type> bfields = br.fields();
					HashMap<String, Type> nfields = new HashMap();
					for (Map.Entry<String, Type> e : rfields.entrySet()) {
						Type bt = bfields.get(e.getKey());
						if (bt != null) {
							nfields.put(e.getKey(),
									leastUpperBound(e.getValue(), bt));
						}
					}					
					r = T_RECORD(nfields);
				}
			}
			return r;
		}
		return null;
	}

	public static Set effectiveSetType(Type t) {
		if (t instanceof Type.Set) {
			return (Type.Set) t;
		} else if (t instanceof Type.Union) {			
			Union ut = (Type.Union) t;
			Set r = null;
			for (Type b : ut.bounds()) {
				if (!(b instanceof Set)) {
					return null;
				}
				Set br = (Set) b;
				if (r == null) {
					r = br;
				} else {
					r = T_SET(leastUpperBound(r.element(),br.element()));
				}
			}			
			return r;
		}
		return null;
	}
	
	public static List effectiveListType(Type t) {
		if (t instanceof Type.List) {
			return (Type.List) t;
		} else if (t instanceof Type.Union) {			
			Union ut = (Type.Union) t;
			List r = null;
			for (Type b : ut.bounds()) {
				if (!(b instanceof List)) {
					return null;
				}
				List br = (List) b;
				if (r == null) {
					r = br;
				} else {
					r = T_LIST(leastUpperBound(r.element(),br.element()));
				}
			}			
			return r;
		}
		return null;
	}
	
	public static Dictionary effectiveDictionaryType(Type t) {
		if (t instanceof Type.Dictionary) {
			return (Type.Dictionary) t;
		} else if (t instanceof Type.Union) {
			Union ut = (Type.Union) t;
			Dictionary r = null;
			for (Type b : ut.bounds()) {
				if (!(b instanceof Dictionary)) {
					return null;
				}
				Dictionary br = (Dictionary) b;
				if (r == null) {
					r = br;
				} else {
					r = T_DICTIONARY(leastUpperBound(r.key(), br.key()),
							leastUpperBound(r.value(), br.value()));
				}
			}
			return r;
		}
		return null;
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
		return Automatas.minimise(type);
	}

	// =============================================================
	// Primitive Types
	// =============================================================

	/**
	 * A leaf type represents a type which has no component types. For example,
	 * primitive types like <code>int</code> and <code>real</code> are leaf
	 * types.
	 * 
	 * @author djp
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
	 * The type mets represents the type of types. That is, values of this type
	 * are themselves types. (think reflection, where we have
	 * <code>class Class {}</code>).
	 * 
	 * @author djp
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
	 * The existential type represents the an unknown type, defined at a given
	 * position.
	 * 
	 * @author djp
	 * 
	 */
	public static final class Existential extends Automata{
		private Existential(NameID name) {
			super(new State[] { new State(K_EXISTENTIAL,name) });
		}
		public boolean equals(Object o) {
			if(o instanceof Existential) {
				Existential e = (Existential) o;
				return nodes[0].children.equals(nodes[0].children);
			}
			return false;
		}
		public NameID name() {
			return (NameID) nodes[0].children;
		}
		public int hashCode() {
			return nodes[0].children.hashCode();
		}
		public String toString() {
			return "?" + name();
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
	 * Represents a unicode character.
	 * 
	 * @author djp
	 * 
	 */
	public static final class Char extends Leaf {
		private Char() {}
		public boolean equals(Object o) {
			return o == T_CHAR;
		}
		public int hashCode() {
			return 4;
		}
		public String toString() {
			return "char";
		}	
	}
	
	/**
	 * Represents a unicode character.
	 * 
	 * @author djp
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
	
	/**
	 * Represents a string of characters 
	 * 
	 * @author djp
	 * 
	 */
	public static final class Strung extends Leaf {
		private Strung() {}
		public boolean equals(Object o) {
			return o == T_STRING;
		}
		public int hashCode() {
			return 6;
		}
		public String toString() {
			return "string";
		}
	}
			
	// =============================================================
	// Compound Faces
	// =============================================================

	/*
	 * The compound faces are not technically necessary, as they simply provide
	 * interfaces to the underlying nodes of a compound type. However, they
	 * certainly make it more pleasant to use this library.
	 */

	/**
	 * A tuple type describes a compound type made up of two or more
	 * subcomponents. It is similar to a record, except that fields are
	 * effectively anonymous.
	 * 
	 * @author djp
	 * 
	 */
	public static final class Tuple extends Automata  {
		private Tuple(State[] nodes) {
			super(nodes);
		}		
		public java.util.List<Type> elements() {
			int[] values = (int[]) nodes[0].children;
			ArrayList<Type> elems = new ArrayList<Type>();
			for(Integer i : values) {
				elems.add(extract(i));
			}
			return elems;
		}		
	}	
	
	/**
	 * A set type describes set values whose elements are subtypes of the
	 * element type. For example, <code>{1,2,3}</code> is an instance of set
	 * type <code>{int}</code>; however, <code>{1.345}</code> is not.
	 * 
	 * @author djp
	 * 
	 */
	public static final class Set extends Automata  {
		private Set(State[] nodes) {
			super(nodes);
		}
		public Type element() {
			int elemIdx = (Integer) nodes[0].children;
			return extract(elemIdx);			
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
	public static final class List extends Automata  {
		private List(State[] nodes) {
			super(nodes);
		}
		public Type element() {			
			int elemIdx = (Integer) nodes[0].children;
			return extract(elemIdx);
		}		
	}

	/**
	 * A process represents a reference to an actor in Whiley.
	 * 
	 * @author djp
	 * 
	 */
	public static final class Process extends Automata  {
		private Process(State[] nodes) {
			super(nodes);
		}
		public Type element() {
			int elemIdx = (Integer) nodes[0].children;
			return extract(elemIdx);	
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
	public static final class Dictionary extends Automata  {
		private Dictionary(State[] nodes) {
			super(nodes);
		}
		public Type key() {
			Pair<Integer,Integer> p = (Pair) nodes[0].children;
			return extract(p.first());
		}
		public Type value() {
			Pair<Integer,Integer> p = (Pair) nodes[0].children;
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
	public static final class Record extends Automata  {
		private Record(State[] nodes) {
			super(nodes);
		}

		/**
		 * Extract just the key set for this field. This is a cheaper operation
		 * than extracting the keys and their types (since types must be
		 * extracted).
		 * 
		 * @return
		 */
		public HashSet<String> keys() {
			Pair<String,Integer>[] fields = (Pair[]) nodes[0].children;
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
		public HashMap<String,Type> fields() {
			Pair<String,Integer>[] fields = (Pair[]) nodes[0].children;
			HashMap<String,Type> r = new HashMap<String,Type>();
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
	public static final class Union extends Automata {
		private Union(State[] nodes) {
			super(nodes);
		}

		/**
		 * Return the bounds of this union type.
		 * 
		 * @return
		 */
		public HashSet<Type> bounds() {			
			HashSet<Type> r = new HashSet<Type>();
			int[] fields = (int[]) nodes[0].children;
			for(int i : fields) {
				Type b = extract(i);					
				r.add(b);					
			}			
			return r;
		}
	}

	/**
	 * An intersection type represents a type which accepts values in the
	 * intersection of its bounds. <b>NOTE:</b>There must be at least two bounds
	 * for an intersection type to make sense.
	 * 
	 * @author djp
	 * 
	 */
	public static final class Intersection extends Automata {
		private Intersection(State[] nodes) {
			super(nodes);
		}

		/**
		 * Return the bounds of this union type.
		 * 
		 * @return
		 */
		public HashSet<Type> bounds() {			
			HashSet<Type> r = new HashSet<Type>();
			int[] fields = (int[]) nodes[0].children;
			for(int i : fields) {
				Type b = extract(i);					
				r.add(b);					
			}			
			return r;
		}
	}
	
	/**
	 * A difference type represents a type which accepts values in the
	 * difference between its bounds. 
	 * 
	 * @author djp
	 * 
	 */
	public static final class Difference extends Automata {
		private Difference(State[] nodes) {
			super(nodes);
		}
		
		public Type left() {						
			int[] fields = (int[]) nodes[0].children;
			return extract(fields[0]);			
		}
		
		public Type right() {						
			int[] fields = (int[]) nodes[0].children;
			return extract(fields[1]);			
		}
	}
	
	/**
	 * A function type, consisting of a list of zero or more parameters and a
	 * return type.
	 * 
	 * @author djp
	 * 
	 */
	public static class Fun extends Automata  {
		Fun(State[] nodes) {
			super(nodes);
		}

		/**
		 * Get the return type of this function type.
		 * 
		 * @return
		 */
		public Type ret() {						
			int[] fields = (int[]) nodes[0].children;			
			return extract(fields[1]);
		}		
		
		/**
		 * Get the parameter types of this function type.
		 * 
		 * @return
		 */
		public ArrayList<Type> params() {
			int[] fields = (int[]) nodes[0].children;
			ArrayList<Type> r = new ArrayList<Type>();
			for(int i=2;i<fields.length;++i) {
				r.add(extract(fields[i]));
			}
			return r;
		}
	}
	
	public static final class Meth extends Fun {
		Meth(State[] nodes) {
			super(nodes);
		}
		/**
		 * Get the receiver type of this function type.
		 * 
		 * @return
		 */
		public Type.Process receiver() {
			int[] fields = (int[]) nodes[0].children;
			int r = fields[0];
			if(r == -1) { return null; }
			return (Type.Process) extract(r);
		}
	}
	
	/**
	 * The following method traverses the graph using a depth-first
	 * search to identify nodes which are "loop headers". That is, they are the
	 * target of one or more recursive edges in the graph.
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
			BitSet onStack, BitSet headers, State[] graph) {
		if(visited.get(index)) {
			// node already visited
			if(onStack.get(index)) {
				headers.set(index);
			}
			return; 
		} 		
		onStack.set(index);
		visited.set(index);
		State node = graph[index];
		for(int child : node.children) {
			findHeaders(child,visited,onStack,headers,graph);
		}	
		onStack.set(index,false);
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
	 * Determine the node kind of a Type.Leafs
	 * @param leaf
	 * @return
	 */
	public static final byte leafKind(Type.Leaf leaf) {
		if(leaf instanceof Type.Void) {
			return Automata.K_VOID;
		} else if(leaf instanceof Type.Any) {
			return Automata.K_ANY;
		} else if(leaf instanceof Type.Null) {
			return Automata.K_NULL;
		} else if(leaf instanceof Type.Bool) {
			return Automata.K_BOOL;
		} else if(leaf instanceof Type.Byte) {
			return Automata.K_BYTE;
		} else if(leaf instanceof Type.Char) {
			return Automata.K_CHAR;
		} else if(leaf instanceof Type.Int) {
			return Automata.K_INT;
		} else if(leaf instanceof Type.Real) {
			return Automata.K_RATIONAL;
		} else if(leaf instanceof Type.Strung) {
			return Automata.K_STRING;
		} else if(leaf instanceof Type.Meta) {
			return Automata.K_META;
		} else {
			// should be dead code
			throw new IllegalArgumentException("Invalid leaf node: " + leaf);
		}
	}

	
	/**
	 * The construct methods constructs a Type from an array of Components.
	 * It carefully ensures the kind of the root node matches the class
	 * created (e.g. a kind K_SET results in a class Set).
	 * 
	 * @param nodes
	 * @return
	 */
	public final static Type construct(State[] nodes) {
		State root = nodes[0];
		switch(root.kind) {
		case Automata.K_VOID:
			return T_VOID;
		case Automata.K_ANY:
			return T_ANY;
		case Automata.K_META:
			return T_META;
		case Automata.K_NULL:
			return T_NULL;			
		case Automata.K_BOOL:
			return T_BOOL;
		case Automata.K_BYTE:
			return T_BYTE;
		case Automata.K_CHAR:
			return T_CHAR;
		case Automata.K_INT:
			return T_INT;
		case Automata.K_RATIONAL:
			return T_REAL;
		case Automata.K_STRING:
			return T_STRING;
		case Automata.K_TUPLE:
			return new Tuple(nodes);
		case Automata.K_SET:
			return new Set(nodes);
		case Automata.K_LIST:
			return new List(nodes);
		case Automata.K_EXISTENTIAL:
			if(root.children == null) {
				throw new RuntimeException("Problem");
			}
			return new Existential((NameID) root.children);
		case Automata.K_PROCESS:
			return new Process(nodes);
		case Automata.K_DICTIONARY:
			return new Dictionary(nodes);
		case Automata.K_RECORD:
			return new Record(nodes);
		case Automata.K_UNION:
			return new Union(nodes);
		case Automata.K_METHOD:
			return new Meth(nodes);
		case Automata.K_FUNCTION:
			return new Fun(nodes);		
		case Automata.K_LABEL:
			return T_LABEL((String)root.children);
		default:
			throw new IllegalArgumentException("invalid node kind: " + root.kind);
		}
	}

	/**
	 * This method constructs a Node array from an array of types which will
	 * form children.
	 * 
	 * @param kind
	 * @param elements
	 * @return
	 */
	private static State[] construct(byte kind, Object data, Type... elements) {
		int len = 1;
		for(Type b : elements) {
			// could be optimised slightly
			len += nodes(b).length;
		}		
		State[] nodes = new State[len];
		int[] children = new int[elements.length];
		int start = 1;
		for(int i=0;i!=elements.length;++i) {
			children[i] = start;
			State[] comps = nodes(elements[i]);
			insertNodes(start,comps,nodes);
			start += comps.length;
		}
		nodes[0] = new State(kind, children);		
		return nodes;
	}
	
	/**
	 * This method constructs a Node array from a collection of types which will
	 * form children.
	 * 
	 * @param kind
	 * @param elements
	 * @return
	 */
	private static State[] construct(byte kind, Object data, Collection<Type> elements) {		
		int len = 1;
		for(Type b : elements) {
			// could be optimised slightly
			len += nodes(b).length;
		}		
		State[] nodes = new State[len];
		int[] children = new int[elements.size()];
		int start = 1;
		int i=0;
		for(Type element : elements) {
			children[i] = start;
			State[] comps = nodes(element);
			insertNodes(start,comps,nodes);
			start += comps.length;
			i = i + 1;
		}
		
		nodes[0] = new State(kind, children, data);		
		return nodes;	
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
	public static State[] insertNodes(int start, State[] from, State[] into) {
		int[] rmap = new int[from.length];
		for(int i=0;i!=from.length;++i) {
			rmap[i] = i+start;			
		}
		for(int i=0;i!=from.length;++i) {
			into[i+start] = remap(from[i],rmap);			
		}
		return into;
	}
	
	private static final State[] nodes(Type t) {
		if (t instanceof Leaf) {
			return new State[]{new State(leafKind((Leaf) t), null)};
		} else {			
			// compound type
			return ((Compound)t).states;
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
	
	public static void main(String[] args) {				
		PrintBuilder printer = new PrintBuilder(System.out);	
		//Type t1 = contractive(); //linkedList(2);
		Type t1 = T_UNION(T_NULL,T_NULL);
		System.out.println("GOT: " + t1);
		System.out.println("MIN: " + minimise(t1));
		/*
		Type t2 = fromString("{int x,any y}");
		//Type t1 = T_REAL;
		//Type t2 = T_INT;
		System.out.println("Type: " + t1 + "\n------------------");
		build(printer,t1);		
		System.out.println("\nType: " + t2 + "\n------------------");
		build(printer,t2);		
		System.out.println("====================");
		System.out.println(isSubtype(t1,t2));
		System.out.println(isSubtype(t2,t1));
		Type glb = greatestLowerBound(t1,t2);
		System.out.println(glb);
		Type lub = leastUpperBound(t1,t2);
		System.out.println(lub);
		*/	
	}
	
	public static Type contractive() {
		Type lab = T_LABEL("Contractive");
		Type union = T_UNION(lab,lab);
		return T_RECURSIVE("Contractive", union);
	}
	
	public static Type linkedList(int n) {
		return T_RECURSIVE("X",innerLinkedList(n));
	}
	
	public static Type innerLinkedList(int n) {
		if(n == 0) {
			return T_LABEL("X");
		} else {
			Type leaf = T_PROCESS(innerLinkedList(n-1)); 
			HashMap<String,Type> fields = new HashMap<String,Type>();
			fields.put("next", T_UNION(T_NULL,leaf));
			fields.put("data", T_BOOL);
			Type.Record rec = T_RECORD(fields);
			return rec;
		}
	}
}
