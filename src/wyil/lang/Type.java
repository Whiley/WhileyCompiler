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
	
	public static Named T_NAMED(NameID name, Type element) {
		return get(new Named(name, element));
	}
	
	public static List T_LIST(Type element) {
		return get(new List(element));
	}
	
	public static Set T_SET(Type element) {
		return get(new Set(element));
	}
	
	public static Fun T_FUN(ProcessName receiver, Type ret, Type... parameters) {
		return get(new Fun(receiver, ret,parameters));
	}
	
	public static Fun T_FUN(ProcessName receiver, Type ret, Collection<Type> parameters) {
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
	
	public static Recursive T_RECURSIVE(NameID name, Type element) {
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
	
	private static boolean isSubtype(Type t1, Type t2, Map<NameID,Type> environment) {		
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
			
			if(!tt1.types.keySet().equals(tt2.types.keySet())) {
				// this won't be sufficient in the case of open records.
				return false;
			}						
			
			for(Map.Entry<String,Type> e : tt1.types.entrySet()) {
				Type t = tt2.types.get(e.getKey());
				if(!isSubtype(e.getValue(),t,environment)) {
					return false;
				}
			}
			
			return true;
		} else if(t1 instanceof Recursive) {
			Recursive rt1 = (Recursive) t1;
			Type rt1type = rt1.type;						
			
			if (rt1type == null) {
				// recursive case, need to unroll
				rt1type = environment.get(rt1.name);
				if (rt1type == null) {
					return false;
				}
			} else {
				environment = new HashMap<NameID, Type>(environment);
				environment.put(rt1.name, rt1.type);
			}
			
			if (t2 instanceof Recursive) {
				// Here, we attempt to show an isomorphism between the two
				// recursive types.
				Recursive rt2 = (Recursive) t2;
				Type rt2type = rt2.type;
				if (rt2type instanceof Type.Union) {
					// recursive type not normalised; so normalise then try
					// again.
					return isSubtype(rt2, rt2);
				} else if (rt2type == null) {
					// recursive case, need to unroll
					rt2type = environment.get(rt2.name);
					if (rt2type == null) {
						return false;
					}
				}
				HashMap<NameID,Type> binding = new HashMap<NameID,Type>();
				binding.put(rt2.name, T_RECURSIVE(rt1.name,null));
				rt2type = substituteRecursiveTypes(rt2type,binding);
				if(isSubtype(rt1type,rt2type,environment)) {
					return true;
				}
			}
			
			return isSubtype(rt1type,t2,environment);
		} else if(t1 instanceof Named) {
			Named t = (Named) t1;
			return isSubtype(t.type,t2, environment);
		} else if(t2 instanceof Named) {
			Named t = (Named) t2;
			return isSubtype(t1,t.type, environment);
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
		} else if(t1 instanceof Recursive) {
			Recursive r1 = (Recursive) t1;
			Type r1_type = r1.type;
			
			if(t2 instanceof Recursive) {
				Recursive r2 = (Recursive) t2;
				Type r2_type = r2.type;
				
				if(r1.name.equals(r2.name)) {
					return T_VOID;
				} else if(r1.type != null && r2.type != null) {
					HashMap<NameID,NameID> binding = new HashMap();
					binding.put(r2.name, r1.name);
					r1_type = greatestDifference(r1_type,renameRecursiveTypes(r2_type,binding));
					if(isOpenRecursive(r1.name,r1_type)) {
						return T_RECURSIVE(r1.name,r1_type);
					} else {
						return r1_type;
					}
				}
			}
			
			r1_type = unroll(r1);
			Type tmp = greatestDifference(unroll(r1),t2);
			if(tmp.equals(r1_type)) {
				return r1; // no change
			} else {
				return tmp;
			}
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
				|| t instanceof Int || t instanceof Real || t instanceof Any) {
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
		} else if (t instanceof Recursive) {
			Recursive lt = (Recursive) t;
			if(lt.type != null) {
				return isExistential(lt.type);	
			}
			return false;
		} else if (t instanceof Named) {
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
	 * This method lists the names for all recursive types used in the given
	 * type.
	 * 
	 * @param t
	 * @return
	 */
	public static java.util.Set<NameID> recursiveTypeNames(Type t) {
		if (t instanceof Existential || t instanceof Void || t instanceof Null
				|| t instanceof Bool || t instanceof Int || t instanceof Real
				|| t instanceof Any) {			
			return Collections.EMPTY_SET;
		} else if(t instanceof List) {
			List lt = (List) t;
			return recursiveTypeNames(lt.element);
		} else if(t instanceof Set) {
			Set lt = (Set) t;
			return recursiveTypeNames(lt.element);
		} else if(t instanceof Process) {
			Process lt = (Process) t;
			return recursiveTypeNames(lt.element);
		} else if(t instanceof Union) {
			Union ut = (Union) t;
			HashSet<NameID> names = new HashSet<NameID>();
			for(Type b : ut.bounds) {
				names.addAll(recursiveTypeNames(b));				
			}
			return names;
		} else if(t instanceof Record) {			
			Record tt = (Record) t;
			HashSet<NameID> names = new HashSet<NameID>();
			for (Map.Entry<String, Type> b : tt.types.entrySet()) {
				names.addAll(recursiveTypeNames(b.getValue()));				
			}
			return names;
		} else if (t instanceof Recursive) {			
			Recursive lt = (Recursive) t;
			HashSet<NameID> names = new HashSet<NameID>();
			names.add(lt.name);
			if(lt.type != null) {
				names.addAll(recursiveTypeNames(lt.type));
			}
			return names;
		} else if(t instanceof Named) {
			Named lt = (Named) t;
			return recursiveTypeNames(lt.type);
		} else {
			Fun ft = (Fun) t;
			HashSet<NameID> names = new HashSet<NameID>();
			for(Type p : ft.params) {
				names.addAll(recursiveTypeNames(p));				
			}
			names.addAll(recursiveTypeNames(ft.ret));
			if(ft.receiver != null) {
				names.addAll(recursiveTypeNames(ft.receiver));				
			} 
			return names;
		}
	}

	/**
	 * This method renames all of the recursive types used within the given type
	 * using a given binding.
	 * 
	 * @param t
	 * @return
	 */
	public static Type renameRecursiveTypes(Type t, Map<NameID,NameID> binding) {
		if (t instanceof Existential || t instanceof Void || t instanceof Null
				|| t instanceof Bool || t instanceof Int || t instanceof Real
				|| t instanceof Any) {
			return t;
		} else if(t instanceof List) {
			List lt = (List) t;
			return T_LIST(renameRecursiveTypes(lt.element, binding));
		} else if(t instanceof Set) {
			Set lt = (Set) t;
			return T_SET(renameRecursiveTypes(lt.element, binding));			
		} else if(t instanceof Process) {
			Process lt = (Process) t;
			return T_PROCESS(renameRecursiveTypes(lt.element, binding));			
		} else if(t instanceof Union) {
			Union ut = (Union) t;
			HashSet<NonUnion> bounds = new HashSet<NonUnion>();
			for(NonUnion b : ut.bounds) {
				bounds.add((NonUnion)renameRecursiveTypes(b, binding));				
			}
			return T_UNION(bounds);			
		} else if(t instanceof Record) {			
			Record tt = (Record) t;			
			HashMap<String,Type> fields = new HashMap<String,Type>();
			for (Map.Entry<String, Type> b : tt.types.entrySet()) {
				fields.put(b.getKey(), renameRecursiveTypes(b.getValue(),
						binding));				
			}
			return T_RECORD(fields);
		} else if (t instanceof Recursive) {
			Recursive lt = (Recursive) t;
			NameID name = binding.get(lt.name);			
			if (lt.type != null) {
				return T_RECURSIVE(name, renameRecursiveTypes(lt.type,
						binding));
			} else {
				return T_RECURSIVE(name, null);
			}
		} else if (t instanceof Named) {
			return t;
		} else {
			Fun ft = (Fun) t;
			ArrayList<Type> params = new ArrayList<Type>();
			for(Type p : ft.params) {
				params.add(renameRecursiveTypes(p, binding));
			}
			Type ret = renameRecursiveTypes(ft.ret, binding);
			Process receiver = null;
			if(ft.receiver != null) {
				receiver = (Process) renameRecursiveTypes(ft.ret, binding);							
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
		if (t == null) { throw new IllegalArgumentException("substituteRecursiveTypes cannot be called on null"); }
		
		if (t instanceof Existential || t instanceof Void || t instanceof Null
				|| t instanceof Bool || t instanceof Int || t instanceof Real
				|| t instanceof Any || t instanceof Named) {
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
		} else if (t instanceof Recursive) {
			Recursive lt = (Recursive) t;
			Type type = binding.get(lt.name);
			if(type != null) {
				return type;
			}
			if (lt.type != null) {
				return T_RECURSIVE(lt.name, substituteRecursiveTypes(lt.type,
						binding));
			} else {
				return T_RECURSIVE(lt.name, null);
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
		} else if(t instanceof Type.Recursive) {
			Type.Recursive rt = (Type.Recursive) t;
			if(rt.name.equals(key)) {
				return rt.type == null;
			} else if(rt.type != null) {
				return isOpenRecursive(key,rt.type); 
			} else {
				return false;
			}
		} else if(t instanceof Type.Named) {
			return false;
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
	public static Type normaliseRecursiveType(Type t) {
		if (t instanceof Type.Void || t instanceof Type.Null
				|| t instanceof Type.Bool || t instanceof Type.Int
				|| t instanceof Type.Real || t instanceof Type.Any
				|| t instanceof Type.Existential || t instanceof Type.Named) {
			return t;
		} else if(t instanceof Type.List) {
			Type.List lt = (Type.List) t;
			return Type.T_LIST(normaliseRecursiveType(lt.element));
		} else if(t instanceof Type.Set) {
			Type.Set lt = (Type.Set) t;
			return Type.T_SET(normaliseRecursiveType(lt.element));			
		} else if(t instanceof Type.Process) {
			Type.Process lt = (Type.Process) t;
			return Type.T_PROCESS(normaliseRecursiveType(lt.element));			
		} else if(t instanceof Type.Record) {			
			Type.Record tt = (Record) t;
			HashMap<String,Type> types = new HashMap<String,Type>();
			for (Map.Entry<String, Type> b : tt.types.entrySet()) {
				types.put(b.getKey(), normaliseRecursiveType(b.getValue()));				
			}
			return T_RECORD(types);
		} else if (t instanceof Type.Recursive) {
			Type.Recursive rt = (Type.Recursive) t;
			if (rt.type == null) {
				return rt;
			} else {
				Type element = normaliseRecursiveType(rt.type);
				return unfactor(T_RECURSIVE(rt.name, element));				
			}
		} else if(t instanceof Type.Fun) {		
			Type.Fun ft = (Type.Fun) t;
			ArrayList<Type> params = new ArrayList<Type>();
			for(Type p : ft.params) {
				params.add(normaliseRecursiveType(p));
			}
			Type ret = normaliseRecursiveType(ft.ret);
			Type.ProcessName receiver = ft.receiver;
			if(receiver != null) {
				receiver = (Type.ProcessName) normaliseRecursiveType(receiver);
			}
			return T_FUN(receiver,ret,params);
		} else if(t instanceof Type.Union) {
			Type.Union ut = (Type.Union) t;
			Type lub = Type.T_VOID;
			
			for (Type b : ut.bounds) {
				lub = leastUpperBound(lub, normaliseRecursiveType(b));
			}

			return lub;
		} 
		
		return t;
	}
	
	public static Type unroll(Type.Recursive rt) {
		HashMap<NameID,Type> binding = new HashMap<NameID,Type>();
		binding.put(rt.name, rt);
		return substituteRecursiveTypes(rt.type,binding);
	}
	
	public static Type unfactor(Type.Recursive type) {		
		if(type.type instanceof Union) {			
			Type.Union ut = (Type.Union) type.type;			
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
			
			HashMap<NameID,Type> binding = new HashMap<NameID,Type>();
			binding.put(type.name, leastUpperBound(factors, T_RECURSIVE(
					type.name, null)));
			// FIXME: there is a bug here for sure as substitute recursive types
			// doesn't do quite what you'd expect.
			Type elem = substituteRecursiveTypes(opens,binding);			
			return leastUpperBound(factors,T_RECURSIVE(type.name,elem));
		}		
		
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
		} else if(t instanceof Type.Recursive) {
			// this is more tricky. We need to unroll the type once to ensure we
			// don't lose the recursive information.
			Type.Recursive rt = (Type.Recursive) t;
			if(rt.type != null) {
				return effectiveRecordType(unroll(rt));
			}
		} else if(t instanceof Type.Named) {
			Type.Named nt = (Type.Named) t;
			return effectiveRecordType(nt.type);
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
	
	public static abstract class NonUnion extends Type {}
	public static abstract class ProcessName extends NonUnion {}

	public static abstract class SetList extends NonUnion {
		public abstract Type element();
	}
	
	public static final class Any extends NonUnion {
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
	public static final class Meta extends NonUnion {
		private Meta() {}
		public boolean equals(Object o) {
			return o == T_META;
		}
		public int hashCode() {
			return 1;
		}
		public String toString() {
			return "#";
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
		public String toString() {
			return "void";
		}
	}
	public static final class Null extends NonUnion {
		private Null() {}
		public boolean equals(Object o) {
			return o == T_NULL;
		}
		public int hashCode() {
			return 1;
		}
		public String toString() {
			return "null";
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
		public String toString() {
			return "?";
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
		public String toString() {
			return "bool";
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
		public String toString() {
			return "int";
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
		public String toString() {
			return "real";
		}
	}
	public static class Named extends ProcessName {
		public final NameID name;		
		public final Type type;
		private Named(NameID name, Type element) {			
			this.name = name;
			this.type = element;
		}
		public boolean equals(Object o) {
			if(o instanceof Named) {
				Named l = (Named) o;				
				return type.equals(l.type)
						&& name.equals(l.name); 
			}
			return false;
		}
		public int hashCode() {
			return type.hashCode() + name.hashCode();
		}
		public String toString() {
			return name + ":" + type;
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
				return element.equals(l.element);
			}
			return false;
		}
		public int hashCode() {
			return element.hashCode();	
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
				return element.equals(l.element);
			}
			return false;
		}
		public int hashCode() {
			return element.hashCode();
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
				return bounds.equals(l.bounds);
			}
			return false;
		}
		public int hashCode() {
			return bounds.hashCode();
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
		public final ProcessName receiver;
		public final Type ret;
		public final ArrayList<Type> params;
		
		private Fun(ProcessName receiver, Type ret, Type... parameters) {
			this.ret = ret;
			this.receiver = receiver;
			this.params = new ArrayList<Type>();
			for(Type t : parameters) {
				this.params.add(t);
			}
		}
		private Fun(ProcessName receiver, Type ret, Collection<Type> parameters) {
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

	public static final class Process extends ProcessName {
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
				return types.equals(l.types);
			}
			return false;
		}
		public int hashCode() {
			return types.hashCode();
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
	public static final class Recursive extends NonUnion {				
		public final NameID name;
		public final Type type;
		
		private Recursive(NameID name, Type type) {
			this.name = name;
			this.type = type;
		}
		public boolean equals(Object o) {
			if(o instanceof Recursive) {
				Recursive r = (Recursive) o;
				if(type != null) {
					return name.equals(r.name) && type.equals(r.type);
				} else {
					return name.equals(r.name) && r.type == null;
				}
			}
			return false;
		}

		public int hashCode() {
			if (type != null) {
				return name.hashCode() + type.hashCode();
			} else {
				return name.hashCode();
			}
		}
		
		public String toString() {
			if(type == null) {
				return name.toString();
			} else {
				return name + "<" + type + ">";
			}
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
