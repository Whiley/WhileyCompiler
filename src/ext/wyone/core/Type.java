package wyone.core;

import java.util.*;

public class Type {
	
	public final static Type T_VOID = new Void();
	public final static Type T_ANY = new Any();
	public final static Type T_BOOL = new Bool();
	public final static Type T_REAL = new Real();
	public final static Type T_INT = new Int();
	public final static Type T_STRING = new Strung();
	
	// both T_UNION methods must be private
	private static Union T_UNION(Collection<NonUnion> bounds) {
		return get(new Union(bounds));
	}
	private static Union T_UNION(NonUnion... bounds) {
		return get(new Union(bounds));
	}
	
	
	public static Type T_MAP(Type from, Type to) {
		return get(new Map(from,to));
	}
	
	public static Type T_SET(Type element) {
		return get(new Set(element));
	}
	
	/**
	 * Return true iff t2 is a subtype of t1
	 */
	public static boolean isSubtype(Type t1, Type t2) {
		if (t1 == t2 || t2 instanceof Void || t1 instanceof Any
				|| (t1 instanceof Real && t2 instanceof Int)) {
			return true;
		} else if(t1 instanceof Map && t2 instanceof Map) {
			Map m1 = (Map) t1;
			Map m2 = (Map) t2;
			return isSubtype(m1.from,m2.from) && isSubtype(m1.to,m2.to); 
		} else if(t1 instanceof Set && t2 instanceof Set) {
			Set s1 = (Set) t1;
			Set s2 = (Set) t2;
			return isSubtype(s1.element, s2.element); 			
		} else if(t2 instanceof Union) {
			// RULE: S-UNION2
			// NOTE: must check S-UNION2 before S-UNION1
			Union u2 = (Union) t2;
			for(Type t : u2.bounds) {
				if(!isSubtype(t1,t)) {
					return false;
				}
			}
			return true;
		} else if(t1 instanceof Union) {			
			// RULE: S-UNION1			
			Union u1 = (Union) t1;
			for(Type t : u1.bounds) {
				if(isSubtype(t,t2)) {
					return true;
				}
			}
			return false;
		} else {
			throw new IllegalArgumentException("unknown types encountered: "
					+ t1 + " " + t2);
		}
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
		if(isSubtype(t1, t2)) {
			return t1;
		} else if(isSubtype(t2, t1)) {
			return t2;
		} else if(t1 instanceof Union && t2 instanceof Union) {
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
				if(isSubtype(t2, t)) {
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
				if(isSubtype(t1, t)) {
					types.set(i, (NonUnion) t1);			
					return T_UNION(types);
				}
			}					
			types.add((NonUnion) t1);			
			return T_UNION(types);			
		} 
		
		return T_UNION((NonUnion)t1,(NonUnion)t2);					
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
		
		if(isSubtype(t1, t2)) {			
			return t2;
		} else if(isSubtype(t2, t1)) {
			return t1;
		} else if(t1 instanceof Set && t2 instanceof Set) {
			Set s1 = (Set) t1;
			Set s2 = (Set) t2;
			return T_SET(greatestLowerBound(s1.element,s2.element));
		} else if(t1 instanceof Union) {			
			Union ut1 = (Union) t1;			
			Type glb = T_VOID;
			for(NonUnion t : ut1.bounds) {				
				glb = leastUpperBound(glb,greatestLowerBound(t,t2));											
			}		
			return glb;
		} else if(t2 instanceof Union) {			
			Union ut2 = (Union) t2;			
			Type glb = T_VOID;
			for(NonUnion t : ut2.bounds) {				
				glb = leastUpperBound(glb,greatestLowerBound(t1,t));											
			}		
			return glb;					
		} 
		
		return T_VOID;					
	}
		
	public static class NonUnion extends Type {}
	
	public static final class Void extends NonUnion {
		private Void() {}
		public String toString() {
			return "void";
		}
	}
	
	public static final class Any extends NonUnion {
		private Any() {}
		public String toString() {
			return "void";
		}
	}
	
	public static final class Bool extends NonUnion {
		private Bool() {}
		public String toString() {
			return "bool";
		}
	}
	
	public static final class Int extends NonUnion {
		private Int() {}
		public String toString() {
			return "int";
		}
	}
	
	public static final class Real extends NonUnion {
		private Real() {}
		public String toString() {
			return "real";
		}
	}
	
	public static final class Strung extends NonUnion {
		private Strung() {}
		public String toString() {
			return "string";
		}
	}
	
	public static final class Map extends NonUnion {
		public final Type from;
		public final Type to;
		private Map(Type from, Type to) {
			this.from = from;
			this.to = to;
		}
		public int hashCode() {
			return from.hashCode() + to.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Map) {
				Map m = (Map) o;
				return from.equals(m.from) && to.equals(m.to);
			}
			return false;
		}
		public String toString() {
			return from + "->" + to;
		}
	}
	
	public static final class Union extends Type {
		public final HashSet<NonUnion> bounds;
		private Union(Collection<NonUnion> bounds) {
			this.bounds = new HashSet<NonUnion>(bounds);
		}
		private Union(NonUnion... bounds) {
			this.bounds = new HashSet<NonUnion>();
			for(NonUnion b : bounds) {
				this.bounds.add(b);
			}
		}
		public int hashCode() {
			return bounds.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Map) {
				Union m = (Union) o;
				return m.bounds.equals(bounds);
			}
			return false;
		}
		public String toString() {
			String r="";
			boolean firstTime = true;
			for(Type t : bounds) {
				if(!firstTime) {
					r += "|";
				}
				r += t; 
			}
			return r;
		}
	}
	
	public static final class Set extends Type {
		public final Type element;
		private Set(Type element) {
			this.element = element;
		}
		public int hashCode() {
			return element.hashCode();
		}
		public boolean equals(Object o) {
			return o instanceof Set && element.equals(((Set) o).element);
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
