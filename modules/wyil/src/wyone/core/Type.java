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

package wyone.core;

import java.util.*;

public abstract class Type {

	public static final Any T_ANY = new Any();
	public static final Void T_VOID = new Void();
	public static final Bool T_BOOL = new Bool();
	public static final Int T_INT = new Int();
	public static final Real T_REAL = new Real();
	public static final Strung T_STRING = new Strung();
	public static final List T_LISTANY = new List(T_ANY);
	public static final AnyTerm T_ANYTERM = new AnyTerm();
	
	public static List T_LIST(Type element) {
		return get(new List(element));
	}
	
	public static Term T_TERM(String name, boolean unbounded, Reference... params) {
		return get(new Term(name,unbounded,params));
	}
	
	public static Term T_TERM(String name, boolean unbounded, Collection<Reference> params) {
		return get(new Term(name,unbounded,params));
	}
	
	/**
	 * Return true if t2 is a subtype of t1 in the context of the given type
	 * hierarchy. The type hierarchy maps a given type to its parents (which are
	 * explicitly declared in the wyone specification language).  
	 * 
	 * @param t1
	 * @param t2
	 * @param hierarchy
	 * @return
	 */
	public static boolean isSubtype(Type t1, Type t2, Map<String,java.util.Set<String>> hierarchy) {
		if (t1 == t2 || (t2 instanceof Void) || t1 instanceof Any
				|| (t1 instanceof AnyTerm && t2 instanceof Reference)
				|| (t1 instanceof Real && t2 instanceof Int)) {
			return true;
		} else if (t1 instanceof List && t2 instanceof List) {
			// RULE: S-LIST
			List l1 = (List) t1;
			List l2 = (List) t2;
			return isSubtype(l1.element, l2.element,hierarchy);
		} else if (t1 instanceof Term && t2 instanceof Term) {			
			Term n1 = (Term) t1;
			Term n2 = (Term) t2;
			if(n1.name.equals(n2.name)) {
				return true;
			} else {
				java.util.Set<String> children = hierarchy.get(n1.name);
				return true; // TODO: need to do this properly
//				if(children != null) {
//					for (String n1child : children) {
//						if (isSubtype(Type.T_TERM(n1child), n2, hierarchy)) {
//							return true;
//						}
//					}
//				}
			}
		} 

		return false;
	}
	
	/**
	 * Compute the <i>least upper bound</i> of two types t1 and t2. The least
	 * upper bound is a type t3 where <code>t3 :> t1</code>,
	 * <code>t3 :> t2</code> and there does not exist a type t4, where
	 * <code>t3 :> t4</code>, <code>t4 :> t1</code>, <code>t4 :> t2</code>.
	 * 
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static Type leastUpperBound(Type t1, Type t2, Map<String,java.util.Set<String>> hierarchy) {
		if (isSubtype(t1, t2, hierarchy)) {
			return t1;
		} else if (isSubtype(t2, t1, hierarchy)) {
			return t2;
		} else if (t1 instanceof List && t2 instanceof List) {
			List l1 = (List) t1;
			List l2 = (List) t2;
			return T_LIST(leastUpperBound(l1.element, l2.element, hierarchy));
		} 

		// FIXME: we can do better for named types by searching the hierarchy!
		
		return T_ANYTERM;
	}
	
	public static final class Any  extends Type {
		private Any() {}
		public String toString() {
			return "any";
		}
	}	
	public static final class Void  extends Type {
		private Void() {}
		public String toString() {
			return "void";
		}
	}	
	public static final class Bool  extends Type {
		private Bool() {}
		public String toString() {
			return "bool";
		}
	}
	public static final class Int  extends Type {
		private Int() {}
		public String toString() {
			return "int";
		}
	}
	public static final class Real  extends Type {
		private Real() {}
		public String toString() {
			return "real";
		}
	}
	public static final class Strung  extends Type {				
		private Strung() {}		
		public String toString() {
			return "string";
		}
	}

	public static abstract class Reference extends Type {}
	
	public static final class AnyTerm  extends Reference {
		private AnyTerm() {			
		}		
		public String toString() {
			return "*";
		}
	}
	
	public static final class Term  extends Reference {		
		public final String name;
		public final ArrayList<Reference> params;
		public final boolean unbound;
		
		private Term(String name, boolean unbounded,
				Collection<Reference> params) {			
			this.name = name;
			this.params = new ArrayList<Reference>(params);
			this.unbound = unbounded;			
		}
		private Term(String name, boolean unbounded, Reference... params) {			
			this.name = name;
			this.params = new ArrayList<Reference>();
			for(Reference t : params) {
				this.params.add(t);
			}
			this.unbound = unbounded;
		}		
		public int hashCode() {
			return name.hashCode() + params.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Term) {
				Term t = (Term) o;
				return t.name.equals(name) && params.equals(t.params) && unbound == t.unbound;
			}
			return false;
		}
		public String toString() {			
			if(params.isEmpty()) {
				return name;
			} else {
				String r = name + "(";
				boolean firstTime=true;
				for(Type t : params) {
					if(!firstTime) {
						r += ",";
					}
					firstTime=false;
					r += t;
				}
				if(unbound) {
					r += "...";
				}
				return r + ")";
			}			
		}
	}
	
	public interface SetList { 
		public Type element();
	}
	
	public static final class List extends Type implements SetList {
		public final Type element;
		private List(Type element) {			
			this.element = element;			
		}
		public Type element() {
			return element;
		}
		public boolean equals(Object o) {
			if(o instanceof List) {
				List l = (List) o;
				return element.equals(l.element);				
			}
			return false;
		}
		public int hashCode() {
			return element.hashCode() * element.hashCode();
		}
		public String toString() {
			return "[" + element + "]";			
		}
	}
		
	public static String type2str(Type t) {
		if(t instanceof AnyTerm) {
			return "*";
		} else if(t instanceof Void) {
			return "V";
		} else if(t instanceof Type.Bool) {
			return "B";
		} else if(t instanceof Type.Int) {
			return "I";
		} else if(t instanceof Type.Real) {
			return "R";
		} else if(t instanceof Type.List) {
			Type.List st = (Type.List) t;
			return "[" + type2str(st.element) + "]";
		} else if(t instanceof Type.Term) {
			Type.Term st = (Type.Term) t;
			String r = "T" + st.name;
			for(Type p : st.params){
				r += type2str(p);
			}
			return r;
		} else {
			throw new RuntimeException("unknown type encountered: " + t);
		}
	}

	private static final ArrayList<Type> types = new ArrayList<Type>();
	private static final HashMap<Type,Integer> cache = new HashMap<Type,Integer>();

	private static <T extends Type> T get(T type) {
		Integer idx = cache.get(type);
		if(idx != null) {
			return (T) types.get(idx);
		} else {				
			cache.put(type, types.size());
			types.add(type);	
			return type;
		}
	}
}

