// This file is part of the Whiley Intermediate Language (wyil).
//
// The Whiley Intermediate Language is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley Intermediate Language is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley Intermediate Language. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyil.lang;

import java.util.*;

public abstract class Type {
	
	// =============================================================
	// Type Constructors
	// =============================================================
		
	public static final Any T_ANY = new Any();
	public static final Void T_VOID = new Void();
	public static final Existential T_EXISTENTIAL = new Existential();
	public static final Bool T_BOOL = new Bool();
	public static final Int T_INT = new Int();
	public static final Real T_REAL = new Real();
	
	public static Named T_NAMED(ModuleID module, String name, Type element) {
		return get(new Named(module, name, element));
	}
	
	public static List T_LIST(Type element) {
		return get(new List(element));
	}
	
	public static Set T_SET(Type element) {
		return get(new Set(element));
	}
	
	public static Fun T_FUN(Process receiver, Type ret, Type... parameters) {
		return get(new Fun(receiver, ret,parameters));
	}
	
	public static Fun T_FUN(Process receiver, Type ret, Collection<Type> parameters) {
		return get(new Fun(receiver, ret, parameters));
	}
	
	public static Union T_UNION(Collection<NonUnion> bounds) {
		return get(new Union(bounds));
	}
	public static Union T_UNION(NonUnion... bounds) {
		return get(new Union(bounds));
	}
	
	public static Process T_PROCESS(Type element) {
		return get(new Process(element));
	}
	
	public static Tuple T_TUPLE(Map<String,Type> types) {
		return get(new Tuple(types));
	}
	
	public static Recursive T_RECURSIVE(String name, Type element) {
		return get(new Recursive(name,element));
	}
	
	// =============================================================
	// Type Methods
	// =============================================================
	
	/**
	 * Return true iff t2 is a subtype of t1
	 */
	public static boolean isSubtype(Type t1, Type t2) {
		return isSubtype(t1,t2,Collections.EMPTY_MAP);
	}
	
	private static boolean isSubtype(Type t1, Type t2, Map<String,Type> environment) {
		if(t1 == t2 || 
				(t2 instanceof Void) ||
				(t1 instanceof Any) ||
				(t1 instanceof Real && t2 instanceof Int)) {
			return true;
		} else if(t1 instanceof Named) {
			Named t = (Named) t1;
			return isSubtype(t.type,t2, environment);
		} else if(t2 instanceof Named) {
			Named t = (Named) t2;
			return isSubtype(t1,t.type, environment);
		} else if(t1 instanceof List && t2 instanceof List) {
			List l1 = (List) t1;
			List l2 = (List) t2;
			return isSubtype(l1.element,l2.element,environment);
		} else if(t1 instanceof Set && t2 instanceof Set) {
			Set l1 = (Set) t1;
			Set l2 = (Set) t2;
			return isSubtype(l1.element,l2.element,environment);
		} else if(t1 instanceof Process && t2 instanceof Process) {
			Process l1 = (Process) t1;
			Process l2 = (Process) t2;
			return isSubtype(l1.element,l2.element,environment);
		} else if(t1 instanceof Union && t2 instanceof Union) {			
			Union u2 = (Union) t2;
			for(Type t : u2.bounds) {
				if(isSubtype(t1,t,environment)) {
					return true;
				}				
			}
			return false;
		} else if(t1 instanceof Union) {
			Union u1 = (Union) t1;
			for(Type t : u1.bounds) {
				if(isSubtype(t,t2,environment)) {
					return true;
				}
			}
			return false;
		} else if(t1 instanceof Tuple && t2 instanceof Tuple) {
			Tuple tt1 = (Tuple) t1;
			Tuple tt2 = (Tuple) t2;
			for(Map.Entry<String,Type> e : tt1.types.entrySet()) {
				Type t = tt2.types.get(e.getKey());
				if(!isSubtype(e.getValue(),t,environment)) {
					return false;
				}
			}
			return true;
		} else if(t1 instanceof Fun && t2 instanceof Fun) {
			Fun f1 = (Fun) t1;
			Fun f2 = (Fun) t2;
			ArrayList<Type> f1_params = f1.params;
			ArrayList<Type> f2_params = f2.params;
			if(f1.params.size() != f2.params.size()) {
				return false;
			}
			for(int i=0;i!=f1_params.size();++i) {
				Type tt1 = f1_params.get(i);
				Type tt2 = f2_params.get(i);
				if(!isSubtype(tt1,tt2,environment)) {
					return false;
				}
			}
			return isSubtype(f2.ret,f1.ret,environment);
		}
		
		return false;
	}
	
	public static Type leastUpperBound(Type t1, Type t2) {
		
		if(isSubtype(t1, t2, Collections.EMPTY_MAP)) {
			return t1;
		} else if(isSubtype(t2, t1, Collections.EMPTY_MAP)) {
			return t2;
		}
		
		if(t1 instanceof Union && t2 instanceof Union) {
			Union ut1 = (Union) t1;
			Union ut2 = (Union) t2;
			ArrayList<NonUnion> types = new ArrayList<NonUnion>(ut1.bounds);
			
			// FIXME: this is totally broken because it doesn't eliminate
			// subsumed types. I really need to work harder here, but for now
			// i'm slacking off...
			types.addAll(ut2.bounds);
			
			return T_UNION(types);			
		} else if(t1 instanceof Union) {			
			Union ut1 = (Union) t1;
			ArrayList<NonUnion> types = new ArrayList<NonUnion>(ut1.bounds);
			
			for(int i=0;i!=types.size();++i) {
				NonUnion t = types.get(i);
				if(isSubtype(t2, t, Collections.EMPTY_MAP)) {
					types.set(i, (NonUnion) t2);
					return T_UNION(types);
				}
			}						
			types.add((NonUnion) t2);
			return T_UNION(types);
		} else if(t2 instanceof Union) {
			Union ut2 = (Union) t2;
			ArrayList<NonUnion> types = new ArrayList<NonUnion>(ut2.bounds);
			
			for(int i=0;i!=types.size();++i) {
				NonUnion t = types.get(i);
				if(isSubtype(t1, t, Collections.EMPTY_MAP)) {
					types.set(i, (NonUnion) t1);			
					return T_UNION(types);
				}
			}					
			types.add((NonUnion) t1);			
			return T_UNION(types);			
		} else if(t1 instanceof List && t2 instanceof List) {
			List l1 = (List) t1;
			List l2 = (List) t2;
			return T_LIST(leastUpperBound(l1.element,l2.element));
		} else if(t1 instanceof Set && t2 instanceof Set) {
			Set s1 = (Set) t1;
			Set s2 = (Set) t2;
			return T_SET(leastUpperBound(s1.element,s2.element));
		} else {
			return T_UNION((NonUnion) t1, (NonUnion) t2);
		}
	}

	/**
	 * Determine whether a given type contains an existential or not.
	 * 
	 * @param t
	 * @return
	 */
	public static boolean isExistential(Type t) {
		if (t instanceof Existential) {
			return true;
		} else if (t instanceof Void || t instanceof Bool || t instanceof Int
				|| t instanceof Real || t instanceof Any || t instanceof Named) {
			return false;
		} else if(t instanceof List) {
			List lt = (List) t;
			return isExistential(lt.element);
		} else if(t instanceof Set) {
			Set lt = (Set) t;
			return isExistential(lt.element);
		} else if(t instanceof Process) {
			Process lt = (Process) t;
			return isExistential(lt.element);
		} else if(t instanceof Union) {
			Union ut = (Union) t;
			for(Type b : ut.bounds) {
				if(isExistential(b)) {
					return true;
				}
			}
			return false;
		} else {
			Fun ft = (Fun) t;
			for(Type p : ft.params) {
				if(isExistential(p)) {
					return true;
				}
			}
			if(ft.receiver != null) {
				return isExistential(ft.receiver) || isExistential(ft.ret);
			} else {
				return isExistential(ft.ret);
			}
		}
	}
	
	// =============================================================
	// Type Classes
	// =============================================================
	
	public static abstract class NonUnion extends Type {}
	
	public static final class Any extends NonUnion {
		private Any() {}
		public boolean equals(Object o) {
			return o == T_ANY;
		}
		public int hashCode() {
			return 1;
		}
	}
	public static final class Void extends NonUnion {
		private Void() {}
		public boolean equals(Object o) {
			return o == T_VOID;
		}
		public int hashCode() {
			return 1;
		}
	}
	public static final class Existential extends NonUnion {
		private Existential() {}
		public boolean equals(Object o) {
			return o == T_EXISTENTIAL;
		}
		public int hashCode() {
			return 2;
		}
	}
	public static final class Bool extends NonUnion {
		private Bool() {}
		public boolean equals(Object o) {
			return o == T_BOOL;
		}
		public int hashCode() {
			return 3;
		}
	}
	public static final class Int extends NonUnion {
		private Int() {}
		public boolean equals(Object o) {
			return o == T_INT;
		}
		public int hashCode() {
			return 4;
		}	
	}
	public static final class Real extends NonUnion {
		private Real() {}
		public boolean equals(Object o) {
			return o == T_REAL;
		}
		public int hashCode() {
			return 5;
		}
	}
	public static final class Named extends NonUnion {
		public final ModuleID module;
		public final String name;
		public final Type type;
		private Named(ModuleID mid, String name, Type element) {
			this.module = mid;
			this.name = name;
			this.type = element;
		}
		public boolean equals(Object o) {
			if(o instanceof Named) {
				Named l = (Named) o;
				return type.equals(l.type) && module.equals(l.module)
						&& name.equals(l.name); 
			}
			return false;
		}
		public int hashCode() {
			return type.hashCode() + module.hashCode() + name.hashCode();
		}
	}
	public static final class List extends NonUnion {
		public final Type element;
		private List(Type element) {
			this.element = element;
		}
		public boolean equals(Object o) {
			if(o instanceof List) {
				List l = (List) o;
				return element.equals(l.element);
			}
			return false;
		}
		public int hashCode() {
			return element.hashCode();
		}
	}
	public static final class Set extends NonUnion {
		public final Type element;
		private Set(Type element) {
			this.element = element;
		}
		public boolean equals(Object o) {
			if(o instanceof Set) {
				Set l = (Set) o;
				return element.equals(l.element);
			}
			return false;
		}
		public int hashCode() {
			return element.hashCode();
		}
	}
	public static final class Union extends Type {
		public final HashSet<NonUnion> bounds;
		public Union(Collection<NonUnion> bounds) {
			if (bounds.size() < 2) {
				new IllegalArgumentException(
						"Cannot construct a type union with fewer than two bounds");
			}
			this.bounds = new HashSet<NonUnion>(bounds);
		}
		public Union(NonUnion... bounds) {			
			this.bounds = new HashSet<NonUnion>();
			for(NonUnion b : bounds) {
				this.bounds.add(b);
			}
			if (this.bounds.size() < 2) {
				new IllegalArgumentException(
						"Cannot construct a type union with fewer than two bounds");
			}
		}
	}
	public static final class Fun extends NonUnion {
		public final Process receiver;
		public final Type ret;
		public final ArrayList<Type> params;
		
		private Fun(Process receiver, Type ret, Type... parameters) {
			this.ret = ret;
			this.receiver = receiver;
			this.params = new ArrayList<Type>();
			for(Type t : parameters) {
				this.params.add(t);
			}
		}
		private Fun(Process receiver, Type ret, Collection<Type> parameters) {
			this.ret = ret;
			this.receiver = receiver;
			this.params = new ArrayList<Type>(parameters);			
		}
		public boolean equals(Object o) {
			if(o instanceof Fun) {
				Fun fun = (Fun) o;
				if(receiver == null) {
					return fun.receiver == null && ret.equals(fun.ret) && params.equals(fun.params);	
				} else {
					return receiver.equals(fun.receiver) && ret.equals(fun.ret) && params.equals(fun.params);
				}				
			}
			return false;
		}
		public int hashCode() {
			return ret.hashCode() + params.hashCode();
		}
	}
	public static final class Process extends NonUnion {
		public final Type element;
		private Process(Type element) {
			this.element = element;
		}
		public boolean equals(Object o) {
			if(o instanceof Process) {
				Process l = (Process) o;
				return element.equals(l.element);
			}
			return false;
		}
		public int hashCode() {
			return element.hashCode();
		}
	}
	public static final class Tuple extends NonUnion {
		public final HashMap<String,Type> types;
		private Tuple(Map<String,Type> types) {
			if(types.size() == 0) {
				throw new IllegalArgumentException(
						"Cannot create type tuple with no fields");
			}
			this.types = new HashMap<String,Type>(types);
		}
		public boolean equals(Object o) {
			if(o instanceof Tuple) {
				Tuple l = (Tuple) o;
				return types.equals(l.types);
			}
			return false;
		}
		public int hashCode() {
			return types.hashCode();
		}
	}
	public static final class Recursive extends NonUnion {
		public final String name;
		public final Type type;
		private Recursive(String name, Type element) {
			this.name = name;
			this.type = element;
		}
		public boolean equals(Object o) {
			if(o instanceof Recursive) {
				Recursive l = (Recursive) o;				
				if(type == null && l.type == null) {
					return name.equals(l.name);
				} else if(l.type != null){
					// FIXME: should do isomorphic test here?
					return name.equals(l.name) && type.equals(l.type); 
				}
				return false;
			}
			return false;
		}
		public int hashCode() {
			return type.hashCode();
		}
	}
	private static final ArrayList<Type> types = new ArrayList<Type>();
	private static final HashMap<Type,Integer> cache = new HashMap<Type,Integer>();
	
	private static <T extends Type> T get(T type) {
		Integer idx = cache.get(type);
		if(idx != null) {
			return (T) types.get(idx);
		} else {
			types.add(type);			
			cache.put(type, types.size());
			return type;
		}
	}
}
