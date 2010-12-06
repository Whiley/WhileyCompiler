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

public abstract class Type {
	
	public final NameID name;
		
	public Type() {
		this.name = null;		
	}
	
	public Type(NameID name) {		
		this.name = name;
	}
	
	public boolean equals(Object o) {
		if(o instanceof Type) {
			Type t = (Type) o;
			
			if(name == null) {
				return t.name == null;
			} else {
				return name.equals(t.name);
			}
		}
		return false;
	}
	
	public int hashCode() {
		if(name == null) {
			return 0;
		} else {
			return name.hashCode();
		}
	}
	
	// =============================================================
	// Type Constructors
	// =============================================================
		
	public static final Any T_ANY = new Any();
	public static final Void T_VOID = new Void();
	public static final Null T_NULL = new Null();
	public static final Existential T_EXISTENTIAL = new Existential();
	public static final Bool T_BOOL = new Bool();
	public static final Int T_INT = new Int();
	public static final Real T_REAL = new Real();
	public static final Meta T_META = new Meta();
	
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
	
	// both T_UNION methods must be private
	private static Union T_UNION(Collection<NonUnion> bounds) {
		return get(new Union(bounds));
	}
	private static Union T_UNION(NonUnion... bounds) {
		return get(new Union(bounds));
	}
		
	public static Process T_PROCESS(Type element) {
		return get(new Process(element));
	}
	
	public static Record T_RECORD(Map<String,Type> types) {
		return get(new Record(types));
	}
	
	public static Recurse T_RECURSE(NameID name) {
		return get(new Recurse(name));
	}
		
	// =============================================================
	// Type Methods
	// =============================================================	
	
	/**
	 * Add a name to a given type
	 */
	public static <T extends Type> T nameType(NameID name, T element) {
		return element; // temporary for now
	}
			
	/**
	 * Return true iff t2 is a subtype of t1
	 */
	public static boolean isSubtype(Type t1, Type t2) {		
		return isSubtype(t1,t2,Collections.EMPTY_MAP);				
	}
	
	private static boolean isSubtype(Type t1, Type t2, Map<String,Type> environment) {		
		
		// At this point here, I need to look for tags
		
		if(t1 == t2 || 
				(t2 instanceof Void) ||
				(t1 instanceof Any) ||
				(t1 instanceof Real && t2 instanceof Int)) {
			return true;
		} else if(t1 instanceof List && t2 instanceof List) {
			List l1 = (List) t1;
			List l2 = (List) t2;
			return isSubtype(l1.element,l2.element,environment);
		} else if(t1 instanceof Set && t2 instanceof Set) {
			Set l1 = (Set) t1;
			Set l2 = (Set) t2;
			return isSubtype(l1.element,l2.element,environment);
		} else if(t1 instanceof Set && t2 instanceof List) {
			Set l1 = (Set) t1;
			List l2 = (List) t2;
			return isSubtype(l1.element,l2.element,environment);
		} else if(t1 instanceof Process && t2 instanceof Process) {
			Process l1 = (Process) t1;
			Process l2 = (Process) t2;
			return isSubtype(l1.element,l2.element,environment);
		} else if(t1 instanceof Union && t2 instanceof Union) {			
			Union u2 = (Union) t2;
			for(Type t : u2.bounds) {
				if(!isSubtype(t1,t,environment)) {
					return false;
				}				
			}
			return true;
		} else if(t1 instanceof Union) {
			Union u1 = (Union) t1;
			for(Type t : u1.bounds) {
				if(isSubtype(t,t2,environment)) {
					return true;
				}
			}
			return false;
		} else if(t1 instanceof Record && t2 instanceof Record) {
			Record tt1 = (Record) t1;
			Record tt2 = (Record) t2;
			for(Map.Entry<String,Type> e : tt1.types.entrySet()) {
				Type t = tt2.types.get(e.getKey());
				if(!isSubtype(e.getValue(),t,environment)) {
					return false;
				}
			}
			return true;
		} else if(t1 instanceof Recurse) {
			Recurse rt1 = (Recurse) t1;
			
			Type rt1type = environment.get(rt1.name);
			if (rt1type == null) {
				return false;
			}

			if (t2 instanceof Recurse) {
				// Here, we attempt to show an isomorphism between the two
				// recursive types.
				Recurse rt2 = (Recurse) t2;
				Type rt2type = environment.get(rt2.name);
				if (rt2type == null) {
					return false;
				}
				
				HashMap<NameID,Type> binding = new HashMap<NameID,Type>();
				binding.put(rt2.name, T_RECURSE(rt1.name));
				rt2type = substituteRecursiveTypes(rt2type,binding);
				
				if(isSubtype(rt1type,rt2type,environment)) {
					return true;
				}
			}
			
			return isSubtype(rt1type,t2,environment);
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
		} else if(t1 instanceof Record && t2 instanceof Record) {
			Record r1 = (Record) t1;
			Record r2 = (Record) t2;
						
			if(r1.types.keySet().equals(r2.types.keySet())) {
				HashMap<String, Type> types = new HashMap<String, Type>();
				for (Map.Entry<String,Type> e : r2.types.entrySet()) {
					String key = e.getKey();
					Type rt2 = e.getValue();
					Type rt1 = r1.types.get(key);					
					types.put(key, leastUpperBound(rt1,rt2));					
				}
				return T_RECORD(types);
			}
								
		} 
		
		return T_UNION((NonUnion)t1,(NonUnion)t2);					
	}

	public static Type leastUpperBound(Collection<? extends Type> types) {
		Type t = T_VOID;
		for(Type b : types) {
			t = leastUpperBound(t,b);
		}
		return t;
	}
	
	public static Type leastUpperBound(Type... types) {
		Type t = T_VOID;
		for(Type b : types) {
			t = leastUpperBound(t,b);
		}
		return t;
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
		
		if(isSubtype(t1, t2, Collections.EMPTY_MAP)) {
			return t2;
		} else if(isSubtype(t2, t1, Collections.EMPTY_MAP)) {
			return t1;
		}
		
		if(t1 instanceof Union) {			
			Union ut1 = (Union) t1;
			ArrayList<NonUnion> types = new ArrayList<NonUnion>();
			
			for(NonUnion t : ut1.bounds) {					
				Type glb = greatestLowerBound(t,t2);				
				if(glb instanceof Union) {
					Union ut = (Union) glb;
					types.addAll(ut.bounds);
				} else if(glb != T_VOID) {
					types.add((NonUnion) glb);
				}
			}						
			
			if(types.size() == 1) {
				return types.get(0);
			} else {			
				return T_UNION(types);
			}
		} else if(t2 instanceof Union) {
			Union ut2 = (Union) t2;
			ArrayList<NonUnion> types = new ArrayList<NonUnion>();
			
			for(NonUnion t : ut2.bounds) {				
				Type glb = greatestLowerBound(t1,t);				
				if(glb != T_VOID) {
					types.add((NonUnion) glb);
				}				
			}		
			if(types.size() == 1) {
				return types.get(0);
			} else {			
				return T_UNION(types);
			}					
		} else if(t1 instanceof List && t2 instanceof List) {
			List l1 = (List) t1;
			List l2 = (List) t2;
			return T_LIST(greatestLowerBound(l1.element,l2.element));
		} else if(t1 instanceof List && t2 instanceof Set) {
			List l1 = (List) t1;
			Set l2 = (Set) t2;
			return T_LIST(greatestLowerBound(l1.element,l2.element));
		} else if(t1 instanceof Set && t2 instanceof List) {
			Set l1 = (Set) t1;
			List l2 = (List) t2;
			return T_LIST(greatestLowerBound(l1.element,l2.element));
		} else if(t1 instanceof Set && t2 instanceof Set) {
			Set s1 = (Set) t1;
			Set s2 = (Set) t2;
			return T_SET(greatestLowerBound(s1.element,s2.element));
		} else if(t1 instanceof Record && t2 instanceof Record) {
			Record r1 = (Record) t1;
			Record r2 = (Record) t2;
			if(r1.types.keySet().equals(r2.types.keySet())) {
				HashMap<String,Type> types = new HashMap<String,Type>();
				for(Map.Entry<String,Type> e : r1.types.entrySet()) {
					String key = e.getKey();
					Type rt1 = e.getValue();
					Type rt2 = r2.types.get(key);					
					types.put(key, greatestLowerBound(rt1,rt2));
				}			
				return T_RECORD(types);
			}
		} 
		
		return T_VOID;					
	}
	
	/**
	 * Subtract t2 from t1.
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static Type greatestDifference(Type t1, Type t2) {
		if(isSubtype(t2,t1)) {
			return T_VOID;
		} else if(t2 == T_VOID) {
			return t1;
		} else if(t1 instanceof List && t2 instanceof List) {
			List l1 = (List) t1;
			List l2 = (List) t2;
			return T_LIST(greatestDifference(l1.element,l2.element));
		} else if(t1 instanceof Set && t2 instanceof Set) {
			Set s1 = (Set) t1;
			Set s2 = (Set) t2;
			return T_SET(greatestDifference(s1.element,s2.element));
		} else if(t1 instanceof Record && t2 instanceof Record) {
			Record r1 = (Record) t1;
			Record r2 = (Record) t2;
						
			if (r1.types.keySet().equals(r2.types.keySet())) {
				HashMap<String, Type> types = new HashMap<String, Type>(
						r1.types);
				for (Map.Entry<String, Type> e : r2.types.entrySet()) {
					String key = e.getKey();
					Type rt2 = e.getValue();
					Type rt1 = r1.types.get(key);
					types.put(key, greatestDifference(rt1, rt2));
				}
				return T_RECORD(types);
			}
						
		} else if(t2 instanceof Union) {
			Union u = (Union) t2;
			for(Type t : u.bounds) {
				t1 = greatestDifference(t1,t);
			}
		} else if (t1 instanceof Union) {
			Union u = (Union) t1;
			Type lub = T_VOID;
			// Could probably optimise this more
			for (Type t : u.bounds) {
				lub = leastUpperBound(lub, greatestDifference(t, t2));
			}
			return lub;
		} 
		
		return t1;
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
		} else if (t instanceof Void || t instanceof Null || t instanceof Bool
				|| t instanceof Int || t instanceof Real || t instanceof Any || t instanceof Recurse) {
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
		} else if(t instanceof Record) {			
			Record tt = (Record) t;
			for (Map.Entry<String, Type> b : tt.types.entrySet()) {
				if (isExistential(b.getValue())) {
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
	
	/**
	 * This method renames all of the recursive types used within the given type
	 * using a given binding.
	 * 
	 * @param t
	 * @return
	 */
	public static Type rename(Type t, Map<NameID,NameID> binding) {
		if (t instanceof Existential || t instanceof Void || t instanceof Null
				|| t instanceof Bool || t instanceof Int || t instanceof Real
				|| t instanceof Any) {
			return t;
		} else if(t instanceof List) {
			List lt = (List) t;
			return T_LIST(rename(lt.element, binding));
		} else if(t instanceof Set) {
			Set lt = (Set) t;
			return T_SET(rename(lt.element, binding));			
		} else if(t instanceof Process) {
			Process lt = (Process) t;
			return T_PROCESS(rename(lt.element, binding));			
		} else if(t instanceof Union) {
			Union ut = (Union) t;
			HashSet<NonUnion> bounds = new HashSet<NonUnion>();
			for(NonUnion b : ut.bounds) {
				bounds.add((NonUnion)rename(b, binding));				
			}
			return T_UNION(bounds);			
		} else if(t instanceof Record) {			
			Record tt = (Record) t;			
			HashMap<String,Type> fields = new HashMap<String,Type>();
			for (Map.Entry<String, Type> b : tt.types.entrySet()) {
				fields.put(b.getKey(), rename(b.getValue(),
						binding));				
			}
			return T_RECORD(fields);
		} else if (t instanceof Recurse) {
			Recurse lt = (Recurse) t;
			NameID name = binding.get(lt.name);
			if(name == null) { name = lt.name; }
			return T_RECURSE(name);			
		} else {
			Fun ft = (Fun) t;
			ArrayList<Type> params = new ArrayList<Type>();
			for(Type p : ft.params) {
				params.add(rename(p, binding));
			}
			Type ret = rename(ft.ret, binding);
			Process receiver = null;
			if(ft.receiver != null) {
				receiver = (Process) rename(ft.ret, binding);							
			} 
			return T_FUN(receiver,ret,params);
		}
	}

	/**
	 * The following method substitutes any occurrence of a given recursive type
	 * by the type given in the binding. If no binding for a recursive type is
	 * given, it remains as is.
	 * 
	 * @param t
	 * @param binding
	 * @return
	 */
	public static Type substituteRecursiveTypes(Type t, Map<NameID,Type> binding) {
		if (t instanceof Existential || t instanceof Void || t instanceof Null
				|| t instanceof Bool || t instanceof Int || t instanceof Real
				|| t instanceof Any) {
			return t;
		} else if(t instanceof List) {
			List lt = (List) t;
			return T_LIST(substituteRecursiveTypes(lt.element, binding));
		} else if(t instanceof Set) {
			Set lt = (Set) t;
			return T_SET(substituteRecursiveTypes(lt.element, binding));			
		} else if(t instanceof Process) {
			Process lt = (Process) t;
			return T_PROCESS(substituteRecursiveTypes(lt.element, binding));			
		} else if(t instanceof Union) {
			Union ut = (Union) t;
			HashSet<NonUnion> bounds = new HashSet<NonUnion>();
			for(NonUnion b : ut.bounds) {
				bounds.add((NonUnion)substituteRecursiveTypes(b, binding));				
			}
			return T_UNION(bounds);			
		} else if(t instanceof Record) {			
			Record tt = (Record) t;			
			HashMap<String,Type> fields = new HashMap<String,Type>();
			for (Map.Entry<String, Type> b : tt.types.entrySet()) {
				fields.put(b.getKey(), substituteRecursiveTypes(b.getValue(),
						binding));				
			}
			return T_RECORD(fields);
		} else if (t instanceof Recurse) {
			Recurse lt = (Recurse) t;
			Type type = binding.get(lt.name);
			if(type != null) {
				return type;
			} else {
				return lt;
			}			
		} else {
			Fun ft = (Fun) t;
			ArrayList<Type> params = new ArrayList<Type>();
			for(Type p : ft.params) {
				params.add(substituteRecursiveTypes(p, binding));
			}
			Type ret = substituteRecursiveTypes(ft.ret, binding);
			Process receiver = null;
			if(ft.receiver != null) {
				receiver = (Process) substituteRecursiveTypes(ft.ret, binding);							
			} 
			return T_FUN(receiver,ret,params);
		}
	}
	
	/**
	 * An open recursive type is one for which there is a recursive leaf node
	 * for the given key, but there is no enclosing recursive node for it.
	 * 
	 * @param t
	 * @return
	 */
	public static boolean isOpenRecursive(NameID key, Type t) {
		if (t instanceof Type.Void || t instanceof Type.Null
				|| t instanceof Type.Bool || t instanceof Type.Int
				|| t instanceof Type.Real || t instanceof Type.Any
				|| t instanceof Type.Existential) {
			return false;
		} else if(t instanceof Type.List) {
			Type.List lt = (Type.List) t;
			return isOpenRecursive(key,lt.element);
		} else if(t instanceof Type.Set) {
			Type.Set lt = (Type.Set) t;
			return isOpenRecursive(key,lt.element);
		} else if(t instanceof Type.Process) {
			Type.Process lt = (Type.Process) t;
			return isOpenRecursive(key,lt.element);
		} else if(t instanceof Type.Union) {
			Type.Union ut = (Type.Union) t;
			for(Type b : ut.bounds) {
				if(isOpenRecursive(key,b)) {
					return true;
				}
			}
			return false;
		} else if(t instanceof Type.Record) {			
			Type.Record tt = (Record) t;
			for (Map.Entry<String, Type> b : tt.types.entrySet()) {
				if (isOpenRecursive(key,b.getValue())) {
					return true;
				}
			}
			return false;
		} else if(t instanceof Type.Recurse) {
			Type.Recurse rt = (Type.Recurse) t;
			return rt.name.equals(key);
		} else {		
			Type.Fun ft = (Type.Fun) t;
			for(Type p : ft.params) {
				if(isOpenRecursive(key,p)) {
					return true;
				}
			}
			if (ft.receiver != null) {
				return isOpenRecursive(key, ft.receiver)
						|| isOpenRecursive(key, ft.ret);
			} else {
				return isOpenRecursive(key, ft.ret);
			}
		}
	}

	/**
	 * <p>
	 * The purpose of this method is to move recursive types into a normal form.
	 * This is necessary to ensure that subtyping works as expected. For
	 * example, consider these types:
	 * </p>
	 * 
	 * <pre>
	 * X[int|{X next}]
	 * int|Y[{int|Y next}]
	 * </pre>
	 * <p>
	 * These types can be considered equivalent. However, under the subtype
	 * relation implemented above, they are not considered subtypes. This is
	 * because unrollowing either of the types does not produce the other. For
	 * example, unrolling the first gives this:
	 * </p>
	 * 
	 * <pre>
	 * X[int|{int|{X next} next}]
	 * </pre>
	 * <p>
	 * Unfortunately, this is still not a subtype of the second.
	 * </p>
	 * <p>
	 * To resolve this issue, we normalise recursive types by unfactoring them
	 * where possible. Factoring is where we push types into the recursive
	 * block, as follows
	 * </p>
	 * 
	 * <pre>
	 * X[int|{X next}] ===> int|X[{{int|Y next}]
	 * </pre>
	 */
	public static Type normaliseRecursiveTypes(Type t) {
		if (t instanceof Type.Void || t instanceof Type.Null
				|| t instanceof Type.Bool || t instanceof Type.Int
				|| t instanceof Type.Real || t instanceof Type.Any
				|| t instanceof Type.Existential) {
			return t;
		} else if(t instanceof Type.List) {
			Type.List lt = (Type.List) t;
			return Type.T_LIST(normaliseRecursiveTypes(lt.element));
		} else if(t instanceof Type.Set) {
			Type.Set lt = (Type.Set) t;
			return Type.T_SET(normaliseRecursiveTypes(lt.element));			
		} else if(t instanceof Type.Process) {
			Type.Process lt = (Type.Process) t;
			return Type.T_PROCESS(normaliseRecursiveTypes(lt.element));			
		} else if(t instanceof Type.Record) {			
			Type.Record tt = (Record) t;
			HashMap<String,Type> types = new HashMap<String,Type>();
			for (Map.Entry<String, Type> b : tt.types.entrySet()) {
				types.put(b.getKey(), normaliseRecursiveTypes(b.getValue()));				
			}
			return T_RECORD(types);
		} else if (t instanceof Type.Recurse) {
			return t;
		} else if(t instanceof Type.Fun) {		
			Type.Fun ft = (Type.Fun) t;
			ArrayList<Type> params = new ArrayList<Type>();
			for(Type p : ft.params) {
				params.add(normaliseRecursiveTypes(p));
			}
			Type ret = normaliseRecursiveTypes(ft.ret);
			Type.Process receiver = ft.receiver;
			if(receiver != null) {
				receiver = (Type.Process) normaliseRecursiveTypes(receiver);
			}
			return T_FUN(receiver,ret,params);
		} else if(t instanceof Type.Union) {
			Type.Union ut = (Type.Union) t;
			Type lub = Type.T_VOID;
			
			for (Type b : ut.bounds) {
				lub = leastUpperBound(lub, normaliseRecursiveTypes(b));
			}

			return lub;
		} 
		
		return t;
	}
	
	public static Type unroll(Type rt) {
		HashMap<NameID,Type> binding = new HashMap<NameID,Type>();
		binding.put(rt.name, rt);
		return substituteRecursiveTypes(rt,binding);
	}
	
	public static Type unfactor(Type type) {	
		/*
		if(type instanceof Union) {			
			Type.Union ut = (Type.Union) type;			
			Type factors = T_VOID;
			Type opens = T_VOID;
			for(Type b : ut.bounds) {
				if(!isOpenRecursive(type.name,b)) {
					factors = leastUpperBound(factors,b);
				} else {
					opens = leastUpperBound(opens,b);
				}
			}
			
			if(factors == T_VOID) {
				// nothing doing here
				return type;
			} 
			
			HashMap<String,Type> binding = new HashMap<String,Type>();
			binding.put(type.name, leastUpperBound(factors, T_RECURSIVE(
					type.name, null)));
			// FIXME: there is a bug here for sure as substitute recursive types
			// doesn't do quite what you'd expect.
			Type elem = substituteRecursiveTypes(opens,binding);			
			return leastUpperBound(factors,T_RECURSIVE(type.name,elem));
		}		
		*/
		// no unfactoring possible
		return type;
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
	public static Type.Record effectiveRecordType(Type t) {

		if(t instanceof Type.Record) {
			return (Type.Record) t;
		} else if(t instanceof Type.Union) {
			Type.Union ut = (Type.Union) t;
			return effectiveRecordType(commonType(ut.bounds));
		} 		
		return null;	
	}
	
	
	private static Type commonType(Collection<? extends Type> types) {		
		Type type = types.iterator().next();
		
		if(type instanceof Type.Record) {
			return commonTupleType(types);
		} else if(type instanceof Type.List) {
			// FIXME: to do			
		} else if(type instanceof Type.Set) {
			// FIXME: to do
		} 
		
		return null;		
	}

	private static Type.Record commonTupleType(Collection<? extends Type> types) {
		Type.Record rt = null;
		for (Type pt : types) {
			if(!(pt instanceof Type.Record)) {
				return null;
			}
			Type.Record tt = (Type.Record) pt;
			if (rt == null) {
				rt = tt;
			} else {
				HashMap<String, Type> it = new HashMap<String, Type>();
				for (Map.Entry<String, Type> ent : rt.types.entrySet()) {
					Type ttt = tt.types.get(ent.getKey());
					if (ttt != null) {
						it.put(ent.getKey(), Type.leastUpperBound(ttt, ent
								.getValue()));
					}
				}				
				
				rt = new Type.Record(it);
			}
		}
		if (rt != null) {				
			return rt;
		} else {
			return null;
		}		
	}

	
	// =============================================================
	// Type Classes
	// =============================================================
	
	public static abstract class NonUnion extends Type {
		public NonUnion() {}
		public NonUnion(NameID name) {
			super(name);
		}
	}
	
	public static abstract class SetList extends NonUnion {
		public abstract Type element();
	}
	
	public static final class Any extends NonUnion {
		private Any() {}
		public boolean equals(Object o) {
			return o == T_ANY && super.equals(o);
		}
		public int hashCode() {
			return 1 + super.hashCode();
		}
		public String toString() {
			return "*";
		}
	}
	public static final class Meta extends NonUnion {
		private Meta() {}
		public boolean equals(Object o) {
			return o == T_META && super.equals(o);
		}
		public int hashCode() {
			return 1 + super.hashCode();
		}
		public String toString() {
			return "#";
		}
	}
	public static final class Void extends NonUnion {
		private Void() {}
		public boolean equals(Object o) {
			return o == T_VOID && super.equals(o);
		}
		public int hashCode() {
			return 1 + super.hashCode();
		}
		public String toString() {
			return "void";
		}
	}
	public static final class Null extends NonUnion {
		private Null() {}
		public boolean equals(Object o) {
			return o == T_NULL && super.equals(o);
		}
		public int hashCode() {
			return 1 + super.hashCode();
		}
		public String toString() {
			return "null";
		}
	}
	public static final class Existential extends NonUnion {
		private Existential() {}
		public boolean equals(Object o) {
			return o == T_EXISTENTIAL && super.equals(o);
		}
		public int hashCode() {
			return 2 + super.hashCode();
		}
		public String toString() {
			return "?";
		}
	}
	public static final class Bool extends NonUnion {
		private Bool() {}
		public boolean equals(Object o) {
			return o == T_BOOL && super.equals(o);
		}
		public int hashCode() {
			return 3 + super.hashCode();
		}
		public String toString() {
			return "bool";
		}
	}
	public static final class Int extends NonUnion {
		private Int() {}
		public boolean equals(Object o) {
			return o == T_INT && super.equals(o);
		}
		public int hashCode() {
			return 4 + super.hashCode();
		}
		public String toString() {
			return "int";
		}	
	}
	public static final class Real extends NonUnion {
		private Real() {}
		public boolean equals(Object o) {
			return o == T_REAL && super.equals(o);
		}
		public int hashCode() {
			return 5 + super.hashCode();
		}
		public String toString() {
			return "real";
		}
	}
	
	public static final class List extends SetList {
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
				return element.equals(l.element) && super.equals(o);
			}
			return false;
		}
		public int hashCode() {
			return element.hashCode() + super.hashCode();
		}
		public String toString() {
			return "[" + element + "]";
		}
	}
	
	public static final class Set extends SetList {
		public final Type element;
		private Set(Type element) {
			this.element = element;
		}
		public Type element() {
			return element;
		}
		public boolean equals(Object o) {
			if(o instanceof Set) {
				Set l = (Set) o;
				return element.equals(l.element) && super.equals(o);
			}
			return false;
		}
		public int hashCode() {
			return element.hashCode() + super.hashCode();
		}
		public String toString() {
			return "{" + element + "}";
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
		public boolean equals(Object o) {
			if(o instanceof Union) {
				Union l = (Union) o;
				return bounds.equals(l.bounds) && super.equals(o);
			}
			return false;
		}
		public int hashCode() {
			return bounds.hashCode() + super.hashCode();
		}
		public String toString() {
			String r = "";
			boolean firstTime=true;
			for(Type t : bounds) {
				if(!firstTime) {
					r +="|";
				}
				firstTime=false;
				r += t;
			}
			return r;
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
					return fun.receiver == null && ret.equals(fun.ret) && params.equals(fun.params) && super.equals(o);	
				} else {
					return receiver.equals(fun.receiver) && ret.equals(fun.ret) && params.equals(fun.params) && super.equals(o);
				}				
			}
			return false;
		}
		public int hashCode() {
			return ret.hashCode() + params.hashCode() + super.hashCode();
		}
		public String toString() {
			String r = "";
			if(receiver != null) {
				r += receiver + "::";
			}
			r += "(";
			boolean firstTime=true;
			for(Type p : params) {
				if(!firstTime) {
					r +=",";
				}
				firstTime=false;
				r += p;
			}
			return r + ")" + ret;
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
				return element.equals(l.element) && super.equals(o);
			}
			return false;
		}
		public int hashCode() {
			return element.hashCode() + super.hashCode();
		}
		public String toString() {
			return "process " + element;
		}
	}
	public static final class Record extends NonUnion {
		public final HashMap<String,Type> types;
		
		private Record(Map<String,Type> types) {			
			this.types = new HashMap<String,Type>(types);			
		}
		public boolean equals(Object o) {
			if(o instanceof Record) {
				Record l = (Record) o;
				return types.equals(l.types) && super.equals(o);
			}
			return false;
		}
		public int hashCode() {
			return types.hashCode() + super.hashCode();
		}
		public String toString() {
			ArrayList<String> fields = new ArrayList<String>(types.keySet());
			Collections.sort(fields);
			String r = "{";
			boolean firstTime=true;
			for(String f : fields) {
				if(!firstTime) {
					r += ",";
				}
				firstTime=false;
				r += types.get(f) + " " + f;
			}
			return r + "}";
		}
	}
	public static final class Recurse extends NonUnion {
		
		private Recurse(NameID name) {
			super(name);		
		}
		public boolean equals(Object o) {
			if(o instanceof Recurse) {
				return super.equals(o);
			}
			return false;
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
