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
	
	public static Set T_SET(Type element) {
		return get(new Set(element));
	}
	
	public static List T_LIST(Type element) {
		return get(new List(element));
	}
	
	public static Named T_NAMED(String name) {
		return get(new Named(name));
	}
	
	public static Record T_RECORD(Map<String,Type> types) {
		return get(new Record(types));
	}
	
	public static Tuple T_TUPLE(Collection<Type> types) {
		return get(new Tuple(types));
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
		if (t1 == t2 || (t2 instanceof Void) || (t1 instanceof Any)
				|| (t1 instanceof Real && t2 instanceof Int)) {
			return true;
		} else if (t1 instanceof List && t2 instanceof List) {
			// RULE: S-LIST
			List l1 = (List) t1;
			List l2 = (List) t2;
			return isSubtype(l1.element, l2.element,hierarchy);
		} else if (t1 instanceof Set && t2 instanceof Set) {
			// RULE: S-SET
			Set l1 = (Set) t1;
			Set l2 = (Set) t2;
			return isSubtype(l1.element, l2.element,hierarchy);
		} else if (t1 instanceof Set && t2 instanceof List) {
			// This rule surely should not be here
			Set l1 = (Set) t1;
			List l2 = (List) t2;
			HashMap<String, Type> types = new HashMap<String, Type>();
			types.put("key", Type.T_INT);
			types.put("value", l2.element);
			return isSubtype(l1.element, Type.T_RECORD(types),hierarchy);
		} else if (t1 instanceof Record && t2 instanceof Record) {
			// RULE: S-DEPTH
			Record tt1 = (Record) t1;
			Record tt2 = (Record) t2;

			if (!tt1.types.keySet().equals(tt2.types.keySet())) {
				// this won't be sufficient in the case of open records.
				return false;
			}

			for (Map.Entry<String, Type> e : tt1.types.entrySet()) {
				Type t = tt2.types.get(e.getKey());
				if (!isSubtype(e.getValue(), t, hierarchy)) {
					return false;
				}
			}

			return true;
		} else if (t1 instanceof Named && t2 instanceof Named) {
			Named n1 = (Named) t1;
			Named n2 = (Named) t2;
			if(n1.name.equals(n2.name)) {
				return true;
			} else {
				java.util.Set<String> children = hierarchy.get(n1.name);
				if(children != null) {
					for (String n1child : children) {
						if (isSubtype(Type.T_NAMED(n1child), n1, hierarchy)) {
							return true;
						}
					}
				}
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
		} else if (t1 instanceof Record && t2 instanceof Record) {
			Record r1 = (Record) t1;
			Record r2 = (Record) t2;

			if (r1.types.keySet().equals(r2.types.keySet())) {
				HashMap<String, Type> types = new HashMap<String, Type>();
				for (Map.Entry<String, Type> e : r2.types.entrySet()) {
					String key = e.getKey();
					Type rt2 = e.getValue();
					Type rt1 = r1.types.get(key);
					types.put(key, leastUpperBound(rt1, rt2, hierarchy));
				}
				return T_RECORD(types);
			}
		} 

		// FIXME: we can do better for named types by searching the hierarchy!
		
		return T_ANY;
	}
	
	public static final class Any  extends Type {
		private Any() {			
		}		
		public String toString() {
			return "*";
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
	public static final class Named  extends Type {		
		public final String name;		
		private Named(String name) {			
			this.name = name;
		}		
		public String toString() {
			return name;
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
		public String toString() {
			return "[" + element + "]";			
		}
	}
	public static final class Set extends Type implements SetList {
		public final Type element;
		private Set(Type element) {			
			this.element = element;
		}
		public Type element() {
			return element;
		}
		public String toString() {
			return "{" + element + "}";			
		}
	}	
	public static final class Record  extends Type {
		public final HashMap<String,Type> types;
		private Record(Map<String,Type> types) {			
			if(types.size() == 0) {
				throw new IllegalArgumentException(
						"Cannot create type tuple with no fields");
			}
			this.types = new HashMap<String,Type>(types);
		}
	}
	public static final class Tuple  extends Type {
		public final ArrayList<Type> types;
		private Tuple(Collection<Type> types) {			
			if(types.size() == 0) {
				throw new IllegalArgumentException(
						"Cannot create type tuple with no fields");
			}
			this.types = new ArrayList<Type>(types);
		}
	}	
	
	public static String type2str(Type t) {
		if(t instanceof Any) {
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
		} else if(t instanceof Type.Set) {
			Type.Set st = (Type.Set) t;
			return "{" + type2str(st.element) + "}";
		} else if(t instanceof Type.Record) {
			Type.Record st = (Type.Record) t;
			ArrayList<String> keys = new ArrayList<String>(st.types.keySet());
			Collections.sort(keys);
			String r="(";
			for(String k : keys) {
				Type kt = st.types.get(k);
				r += k + ":" + type2str(kt);
			}			
			return r + ")";
		} else if(t instanceof Type.Named) {
			Type.Named st = (Type.Named) t;
			return "N" + st.name + ";";
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

