// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyil.lang;

import java.util.*;

public abstract class OldType {
	
	// =============================================================
	// OldType Constructors
	// =============================================================
		
	public static final Any T_ANY = new Any();
	public static final Void T_VOID = new Void();
	public static final Null T_NULL = new Null();
	public static final Existential T_EXISTENTIAL = new Existential();
	public static final Bool T_BOOL = new Bool();
	public static final Int T_INT = new Int();
	public static final Real T_REAL = new Real();
	public static final Meta T_META = new Meta();
	
	public static Named T_NAMED(NameID name, OldType element) {
		return get(new Named(name, element));
	}
	
	public static List T_LIST(OldType element) {
		return get(new List(element));
	}
	
	public static Set T_SET(OldType element) {
		return get(new Set(element));
	}
	
	public static Dictionary T_DICTIONARY(OldType key,OldType value) {
		return get(new Dictionary(key,value));
	}
	
	public static Fun T_FUN(ProcessName receiver, OldType ret, OldType... parameters) {
		return get(new Fun(receiver, ret,parameters));
	}
	
	public static Fun T_FUN(ProcessName receiver, OldType ret, Collection<OldType> parameters) {
		return get(new Fun(receiver, ret, parameters));
	}
	
	// both T_UNION methods must be private
	private static Union T_UNION(Collection<NonUnion> bounds) {
		return get(new Union(bounds));
	}
	private static Union T_UNION(NonUnion... bounds) {
		return get(new Union(bounds));
	}
	
	
	public static Process T_PROCESS(OldType element) {
		return get(new Process(element));
	}
	
	public static Record T_RECORD(Map<String,OldType> types) {
		return get(new Record(types));
	}
	
	public static Recursive T_RECURSIVE(String name, OldType element) {
		return get(new Recursive(name,element));
	}
	
	// =============================================================
	// OldType Methods
	// =============================================================
	
	/**
	 * Return true iff t2 is a subtype of t1
	 */
	public static boolean isSubtype(OldType t1, OldType t2) {					
		
		if (t1 == t2 || (t2 instanceof Void) || (t1 instanceof Any)
				|| (t1 instanceof Real && t2 instanceof Int)) {
			return true;
		} else if(t1 instanceof List && t2 instanceof List) {
			// RULE: S-LIST
			List l1 = (List) t1;
			List l2 = (List) t2;
			return isSubtype(l1.element,l2.element);
		} else if(t1 instanceof Set && t2 instanceof Set) {
			// RULE: S-SET
			Set l1 = (Set) t1;
			Set l2 = (Set) t2;
			return isSubtype(l1.element,l2.element);
		} else if(t1 instanceof Set && t2 instanceof List) {
			// This rule surely should not be here
			Set l1 = (Set) t1;
			List l2 = (List) t2;
			HashMap<String,OldType> types = new HashMap<String,OldType>();
			types.put("key", OldType.T_INT);
			types.put("value", l2.element);
			return isSubtype(l1.element,OldType.T_RECORD(types));
		} else if(t1 instanceof Dictionary && t2 instanceof Dictionary) {
			// RULE: S-DICTIONARY
			Dictionary l1 = (Dictionary) t1;
			Dictionary l2 = (Dictionary) t2;			
			return isSubtype(l1.key,l2.key) && isSubtype(l1.key,l2.key);
		} else if(t1 instanceof Process && t2 instanceof Process) {
			Process l1 = (Process) t1;
			Process l2 = (Process) t2;
			return isSubtype(l1.element,l2.element);
		} else if(t2 instanceof Union) {
			// RULE: S-UNION2
			// NOTE: must check S-UNION2 before S-UNION1
			Union u2 = (Union) t2;
			for(OldType t : u2.bounds) {
				if(!isSubtype(t1,t)) {
					return false;
				}
			}
			return true;
		} else if(t1 instanceof Union) {			
			// RULE: S-UNION1			
			Union u1 = (Union) t1;
			for(OldType t : u1.bounds) {
				if(isSubtype(t,t2)) {
					return true;
				}
			}
			return false;
		} else if(t1 instanceof Record && t2 instanceof Record) {
			// RULE: S-DEPTH
			Record tt1 = (Record) t1;
			Record tt2 = (Record) t2;
			
			if(!tt1.types.keySet().equals(tt2.types.keySet())) {
				// this won't be sufficient in the case of open records.
				return false;
			}						
			
			for(Map.Entry<String,OldType> e : tt1.types.entrySet()) {
				OldType t = tt2.types.get(e.getKey());
				if(!isSubtype(e.getValue(),t)) {
					return false;
				}
			}
			
			return true;
		} else if(t1 instanceof Recursive && t2 instanceof Recursive) {
			// S-RECURSE
			Recursive r1 = (Recursive) t1;
			Recursive r2 = (Recursive) t2;
			HashMap<String,String> binding = new HashMap<String,String>();
			binding.put(r2.name, r1.name);
			r2 = (Recursive) renameRecursiveOldTypes(r2,binding);
			return isSubtype(r1.type,r2.type);
		} else if(t1 instanceof Recursive) {
			// Q-UNFOLD
			Recursive r1 = (Recursive) t1;
			if(r1.type != null) { return isSubtype(unfold(r1),t2); }			
		} else if(t2 instanceof Recursive) {			
			// Q-UNFOLD
			Recursive r2 = (Recursive) t2;
			if(r2.type != null) { return isSubtype(t1,unfold(r2));	 }
		} else if(t1 instanceof Named) {
			Named t = (Named) t1;
			return isSubtype(t.type,t2);
		} else if(t2 instanceof Named) {
			Named t = (Named) t2;
			return isSubtype(t1,t.type);
		} else if(t1 instanceof Fun && t2 instanceof Fun) {
			Fun f1 = (Fun) t1;
			Fun f2 = (Fun) t2;
			ArrayList<OldType> f1_params = f1.params;
			ArrayList<OldType> f2_params = f2.params;
			if(f1.params.size() != f2.params.size()) {
				return false;
			}
			for(int i=0;i!=f1_params.size();++i) {
				OldType tt1 = f1_params.get(i);
				OldType tt2 = f2_params.get(i);
				// NOTE: parameter types must be *contravariant*.
				if(!isSubtype(tt2,tt1)) {
					return false;
				}
			}
			return isSubtype(f1.ret,f2.ret);
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
	public static OldType leastUpperBound(OldType t1, OldType t2) {
		if(isSubtype(t1, t2)) {
			return t1;
		} else if(isSubtype(t2, t1)) {
			return t2;
		} else if(t1 instanceof List && t2 instanceof List) {
			List l1 = (List) t1;
			List l2 = (List) t2;
			return T_LIST(leastUpperBound(l1.element,l2.element));
		} else if(t1 instanceof Record && t2 instanceof Record) {
			Record r1 = (Record) t1;
			Record r2 = (Record) t2;
						
			if(r1.types.keySet().equals(r2.types.keySet())) {
				HashMap<String, OldType> types = new HashMap<String, OldType>();
				for (Map.Entry<String,OldType> e : r2.types.entrySet()) {
					String key = e.getKey();
					OldType rt2 = e.getValue();
					OldType rt1 = r1.types.get(key);					
					types.put(key, leastUpperBound(rt1,rt2));					
				}
				return T_RECORD(types);
			}								
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

	public static OldType leastUpperBound(Collection<? extends OldType> types) {
		OldType t = T_VOID;
		for(OldType b : types) {
			t = leastUpperBound(t,b);
		}
		return t;
	}
	
	public static OldType leastUpperBound(OldType... types) {
		OldType t = T_VOID;
		for(OldType b : types) {
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
	public static OldType greatestLowerBound(OldType t1, OldType t2) {
		
		if(isSubtype(t1, t2)) {			
			return t2;
		} else if(isSubtype(t2, t1)) {
			return t1;
		} else if(t1 instanceof List && t2 instanceof List) {
			List l1 = (List) t1;
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
				HashMap<String,OldType> types = new HashMap<String,OldType>();
				for(Map.Entry<String,OldType> e : r1.types.entrySet()) {
					String key = e.getKey();
					OldType rt1 = e.getValue();
					OldType rt2 = r2.types.get(key);			
					OldType glb = greatestLowerBound(rt1,rt2);
					if(glb == T_VOID) { return glb; }
					types.put(key, glb);
				}			
				return T_RECORD(types);
			}
		} else if(t1 instanceof Union) {			
			Union ut1 = (Union) t1;			
			OldType glb = T_VOID;
			for(NonUnion t : ut1.bounds) {				
				glb = leastUpperBound(glb,greatestLowerBound(t,t2));											
			}		
			return glb;
		} else if(t2 instanceof Union) {			
			Union ut2 = (Union) t2;			
			OldType glb = T_VOID;
			for(NonUnion t : ut2.bounds) {				
				glb = leastUpperBound(glb,greatestLowerBound(t1,t));											
			}		
			return glb;					
		} else if(t1 instanceof Recursive && t2 instanceof Recursive) {
			Recursive r1 = (Recursive) t1;
			Recursive r2 = (Recursive) t2;
			
			if(r1.name.equals(r2.name)) {
				return r1;
			} else if(r1.type == null || r2.type == null) {
				return T_VOID;
			} 
			
			HashMap<String,String> binding = new HashMap();
			binding.put(r2.name, r1.name);
			
			OldType glb = greatestLowerBound(r1.type,renameRecursiveOldTypes(r2.type,binding));
			
			if(isOpenRecursive(r1.name,glb)) {
				return T_RECURSIVE(r1.name,glb);
			} else {
				return glb;
			}
			
		} else if(t1 instanceof Recursive) {
			Recursive r1 = (Recursive) t1;
			return greatestLowerBound(unfold(r1),t2);
		} else if(t2 instanceof Recursive) {			
			Recursive r2 = (Recursive) t2;
			return greatestLowerBound(t1, unfold(r2));			
		}
		
		return T_VOID;					
	}
	
	/**
	 * Subtract t2 from t1.
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static OldType greatestDifference(OldType t1, OldType t2) {
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
				HashMap<String, OldType> types = new HashMap<String, OldType>(
						r1.types);
				for (Map.Entry<String, OldType> e : r2.types.entrySet()) {
					String key = e.getKey();
					OldType rt2 = e.getValue();
					OldType rt1 = r1.types.get(key);
					types.put(key, greatestDifference(rt1, rt2));
				}
				return T_RECORD(types);
			}
						
		} else if(t2 instanceof Union) {
			Union u = (Union) t2;
			for(OldType t : u.bounds) {
				t1 = greatestDifference(t1,t);
			}
		} else if (t1 instanceof Union) {
			Union u = (Union) t1;
			OldType lub = T_VOID;
			// Could probably optimise this more
			for (OldType t : u.bounds) {
				lub = leastUpperBound(lub, greatestDifference(t, t2));
			}
			return lub;
		} else if(t1 instanceof Recursive) {
			Recursive r1 = (Recursive) t1;
			OldType r1_type = r1.type;
			
			if(t2 instanceof Recursive) {
				Recursive r2 = (Recursive) t2;
				OldType r2_type = r2.type;
				
				if(r1.name.equals(r2.name)) {
					return T_VOID;
				} else if(r1.type != null && r2.type != null) {
					HashMap<String,String> binding = new HashMap();
					binding.put(r2.name, r1.name);
					r1_type = greatestDifference(r1_type,renameRecursiveOldTypes(r2_type,binding));
					if(isOpenRecursive(r1.name,r1_type)) {
						return T_RECURSIVE(r1.name,r1_type);
					} else {
						return r1_type;
					}
				}
			}
			
			r1_type = unfold(r1);
			OldType tmp = greatestDifference(unfold(r1),t2);
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
	public static boolean isExistential(OldType t) {
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
			for(OldType b : ut.bounds) {
				if(isExistential(b)) {
					return true;
				}
			}
			return false;
		} else if(t instanceof Record) {			
			Record tt = (Record) t;
			for (Map.Entry<String, OldType> b : tt.types.entrySet()) {
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
			for(OldType p : ft.params) {
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
	public static java.util.Set<String> recursiveOldTypeNames(OldType t) {
		if (t instanceof Existential || t instanceof Void || t instanceof Null
				|| t instanceof Bool || t instanceof Int || t instanceof Real
				|| t instanceof Any) {			
			return Collections.EMPTY_SET;
		} else if(t instanceof List) {
			List lt = (List) t;
			return recursiveOldTypeNames(lt.element);
		} else if(t instanceof Set) {
			Set lt = (Set) t;
			return recursiveOldTypeNames(lt.element);
		} else if(t instanceof Process) {
			Process lt = (Process) t;
			return recursiveOldTypeNames(lt.element);
		} else if(t instanceof Union) {
			Union ut = (Union) t;
			HashSet<String> names = new HashSet<String>();
			for(OldType b : ut.bounds) {
				names.addAll(recursiveOldTypeNames(b));				
			}
			return names;
		} else if(t instanceof Record) {			
			Record tt = (Record) t;
			HashSet<String> names = new HashSet<String>();
			for (Map.Entry<String, OldType> b : tt.types.entrySet()) {
				names.addAll(recursiveOldTypeNames(b.getValue()));				
			}
			return names;
		} else if (t instanceof Recursive) {			
			Recursive lt = (Recursive) t;
			HashSet<String> names = new HashSet<String>();
			names.add(lt.name);
			if(lt.type != null) {
				names.addAll(recursiveOldTypeNames(lt.type));
			}
			return names;
		} else if(t instanceof Named) {
			Named lt = (Named) t;
			return recursiveOldTypeNames(lt.type);
		} else {
			Fun ft = (Fun) t;
			HashSet<String> names = new HashSet<String>();
			for(OldType p : ft.params) {
				names.addAll(recursiveOldTypeNames(p));				
			}
			names.addAll(recursiveOldTypeNames(ft.ret));
			if(ft.receiver != null) {
				names.addAll(recursiveOldTypeNames(ft.receiver));				
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
	public static OldType renameRecursiveOldTypes(OldType t, Map<String,String> binding) {
		if (t instanceof Existential || t instanceof Void || t instanceof Null
				|| t instanceof Bool || t instanceof Int || t instanceof Real
				|| t instanceof Any) {
			return t;
		} else if(t instanceof List) {
			List lt = (List) t;
			return T_LIST(renameRecursiveOldTypes(lt.element, binding));
		} else if(t instanceof Set) {
			Set lt = (Set) t;
			return T_SET(renameRecursiveOldTypes(lt.element, binding));			
		} else if(t instanceof Process) {
			Process lt = (Process) t;
			return T_PROCESS(renameRecursiveOldTypes(lt.element, binding));			
		} else if(t instanceof Union) {
			Union ut = (Union) t;
			HashSet<NonUnion> bounds = new HashSet<NonUnion>();
			for(NonUnion b : ut.bounds) {
				bounds.add((NonUnion)renameRecursiveOldTypes(b, binding));				
			}
			return T_UNION(bounds);			
		} else if(t instanceof Record) {			
			Record tt = (Record) t;			
			HashMap<String,OldType> fields = new HashMap<String,OldType>();
			for (Map.Entry<String, OldType> b : tt.types.entrySet()) {
				fields.put(b.getKey(), renameRecursiveOldTypes(b.getValue(),
						binding));				
			}
			return T_RECORD(fields);
		} else if (t instanceof Recursive) {
			Recursive lt = (Recursive) t;
			String name = binding.get(lt.name);			
			if(name == null) { name = lt.name; }
			if (lt.type != null) {
				return T_RECURSIVE(name, renameRecursiveOldTypes(lt.type,
						binding));
			} else {
				return T_RECURSIVE(name, null);
			}
		} else if (t instanceof Named) {
			return t;
		} else {
			Fun ft = (Fun) t;
			ArrayList<OldType> params = new ArrayList<OldType>();
			for(OldType p : ft.params) {
				params.add(renameRecursiveOldTypes(p, binding));
			}
			OldType ret = renameRecursiveOldTypes(ft.ret, binding);
			Process receiver = null;
			if(ft.receiver != null) {
				receiver = (Process) renameRecursiveOldTypes(ft.ret, binding);							
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
	public static OldType substituteRecursiveOldTypes(OldType t, Map<String,OldType> binding) {
		if (t == null) { throw new IllegalArgumentException("substituteRecursiveOldTypes cannot be called on null"); }
		
		if (t instanceof Existential || t instanceof Void || t instanceof Null
				|| t instanceof Bool || t instanceof Int || t instanceof Real
				|| t instanceof Any || t instanceof Named) {
			return t;
		} else if(t instanceof List) {
			List lt = (List) t;
			return T_LIST(substituteRecursiveOldTypes(lt.element, binding));
		} else if(t instanceof Set) {
			Set lt = (Set) t;
			return T_SET(substituteRecursiveOldTypes(lt.element, binding));			
		} else if(t instanceof Process) {
			Process lt = (Process) t;
			return T_PROCESS(substituteRecursiveOldTypes(lt.element, binding));			
		} else if(t instanceof Union) {
			Union ut = (Union) t;
			HashSet<NonUnion> bounds = new HashSet<NonUnion>();
			for(NonUnion b : ut.bounds) {
				bounds.add((NonUnion)substituteRecursiveOldTypes(b, binding));				
			}
			return T_UNION(bounds);			
		} else if(t instanceof Record) {			
			Record tt = (Record) t;			
			HashMap<String,OldType> fields = new HashMap<String,OldType>();
			for (Map.Entry<String, OldType> b : tt.types.entrySet()) {
				fields.put(b.getKey(), substituteRecursiveOldTypes(b.getValue(),
						binding));				
			}
			return T_RECORD(fields);
		} else if (t instanceof Recursive) {
			Recursive lt = (Recursive) t;
			OldType type = binding.get(lt.name);
			if(type != null) {
				return type;
			}
			if (lt.type != null) {
				return T_RECURSIVE(lt.name, substituteRecursiveOldTypes(lt.type,
						binding));
			} else {
				return T_RECURSIVE(lt.name, null);
			}
		} else {		
			Fun ft = (Fun) t;
			ArrayList<OldType> params = new ArrayList<OldType>();
			for(OldType p : ft.params) {
				params.add(substituteRecursiveOldTypes(p, binding));
			}
			OldType ret = substituteRecursiveOldTypes(ft.ret, binding);
			Process receiver = null;
			if(ft.receiver != null) {
				receiver = (Process) substituteRecursiveOldTypes(ft.ret, binding);							
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
	public static boolean isOpenRecursive(String key, OldType t) {
		if (t instanceof OldType.Void || t instanceof OldType.Null
				|| t instanceof OldType.Bool || t instanceof OldType.Int
				|| t instanceof OldType.Real || t instanceof OldType.Any
				|| t instanceof OldType.Existential) {
			return false;
		} else if(t instanceof OldType.List) {
			OldType.List lt = (OldType.List) t;
			return isOpenRecursive(key,lt.element);
		} else if(t instanceof OldType.Set) {
			OldType.Set lt = (OldType.Set) t;
			return isOpenRecursive(key,lt.element);
		} else if(t instanceof OldType.Process) {
			OldType.Process lt = (OldType.Process) t;
			return isOpenRecursive(key,lt.element);
		} else if(t instanceof OldType.Union) {
			OldType.Union ut = (OldType.Union) t;
			for(OldType b : ut.bounds) {
				if(isOpenRecursive(key,b)) {
					return true;
				}
			}
			return false;
		} else if(t instanceof OldType.Record) {			
			OldType.Record tt = (Record) t;
			for (Map.Entry<String, OldType> b : tt.types.entrySet()) {
				if (isOpenRecursive(key,b.getValue())) {
					return true;
				}
			}
			return false;
		} else if(t instanceof OldType.Recursive) {
			OldType.Recursive rt = (OldType.Recursive) t;
			if(rt.name.equals(key)) {
				return rt.type == null;
			} else if(rt.type != null) {
				return isOpenRecursive(key,rt.type); 
			} else {
				return false;
			}
		} else if(t instanceof OldType.Named) {
			return false;
		} else {		
			OldType.Fun ft = (OldType.Fun) t;
			for(OldType p : ft.params) {
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
	public static OldType normaliseRecursiveOldType(OldType t) {
		if (t instanceof OldType.Void || t instanceof OldType.Null
				|| t instanceof OldType.Bool || t instanceof OldType.Int
				|| t instanceof OldType.Real || t instanceof OldType.Any
				|| t instanceof OldType.Existential || t instanceof OldType.Named) {
			return t;
		} else if(t instanceof OldType.List) {
			OldType.List lt = (OldType.List) t;
			return OldType.T_LIST(normaliseRecursiveOldType(lt.element));
		} else if(t instanceof OldType.Set) {
			OldType.Set lt = (OldType.Set) t;
			return OldType.T_SET(normaliseRecursiveOldType(lt.element));			
		} else if(t instanceof OldType.Process) {
			OldType.Process lt = (OldType.Process) t;
			return OldType.T_PROCESS(normaliseRecursiveOldType(lt.element));			
		} else if(t instanceof OldType.Record) {			
			OldType.Record tt = (Record) t;
			HashMap<String,OldType> types = new HashMap<String,OldType>();
			for (Map.Entry<String, OldType> b : tt.types.entrySet()) {
				types.put(b.getKey(), normaliseRecursiveOldType(b.getValue()));				
			}
			return T_RECORD(types);
		} else if (t instanceof OldType.Recursive) {
			OldType.Recursive rt = (OldType.Recursive) t;
			if (rt.type == null) {
				return rt;
			} else {
				OldType element = normaliseRecursiveOldType(rt.type);
				return unfactor(T_RECURSIVE(rt.name, element));				
			}
		} else if(t instanceof OldType.Fun) {		
			OldType.Fun ft = (OldType.Fun) t;
			ArrayList<OldType> params = new ArrayList<OldType>();
			for(OldType p : ft.params) {
				params.add(normaliseRecursiveOldType(p));
			}
			OldType ret = normaliseRecursiveOldType(ft.ret);
			OldType.ProcessName receiver = ft.receiver;
			if(receiver != null) {
				receiver = (OldType.ProcessName) normaliseRecursiveOldType(receiver);
			}
			return T_FUN(receiver,ret,params);
		} else if(t instanceof OldType.Union) {
			OldType.Union ut = (OldType.Union) t;
			OldType lub = OldType.T_VOID;
			
			for (OldType b : ut.bounds) {
				lub = leastUpperBound(lub, normaliseRecursiveOldType(b));
			}

			return lub;
		} 
		
		return t;
	}
	
	public static OldType unfold(OldType.Recursive rt) {
		HashMap<String,OldType> binding = new HashMap<String,OldType>();
		binding.put(rt.name, rt);
		return substituteRecursiveOldTypes(rt.type,binding);
	}
	
	public static OldType unfactor(OldType.Recursive type) {		
		if(type.type instanceof Union) {			
			OldType.Union ut = (OldType.Union) type.type;			
			OldType factors = T_VOID;
			OldType opens = T_VOID;
			for(OldType b : ut.bounds) {
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
			
			HashMap<String,OldType> binding = new HashMap<String,OldType>();
			binding.put(type.name, leastUpperBound(factors, T_RECURSIVE(
					type.name, null)));
			// FIXME: there is a bug here for sure as substitute recursive types
			// doesn't do quite what you'd expect.
			OldType elem = substituteRecursiveOldTypes(opens,binding);			
			return leastUpperBound(factors,T_RECURSIVE(type.name,elem));
		}		
		
		// no unfactoring possible
		return type;
	}

	/**
	 * The following implements an algorithm for minimising recursive types,
	 * based on the algorithm for minimising DFAs.
	 */
	public static OldType minimise(OldType t) {
		// first, extract components
		HashSet<OldType> components = new HashSet<OldType>();		
		// The var map is necessary to constructing a mapping between type
		// variables and their recursive unfoldings.  
		HashMap<String,Recursive> varmap = new HashMap<String,Recursive>();
		initialiseEquivs(t,components,varmap);
		
		// second, initialise partitions, and reverse partition map		
		ArrayList<HashSet<OldType>> partitions = new ArrayList();						
		partitions.add(components);
		HashMap<OldType,Integer> rpartitions = new HashMap<OldType,Integer>();
		for(OldType component : components) {
			rpartitions.put(component, 0);
		}
		boolean changed = true;
		while(changed) {
			changed = false;
			for(int i = 0; i != partitions.size(); ++i) {
				changed |= splitPartition(i,partitions,rpartitions,varmap);
			}
		}
				
		return rebuild(t,partitions,rpartitions,new HashMap());		
	}		
	
	/**
	 * This method is responsible for iterating all elements of a partition and
	 * separating them out as much as possible.
	 * 
	 * @param partition
	 * @param partitions
	 * @return
	 */
	private static boolean splitPartition(int idx,
			ArrayList<HashSet<OldType>> partitions,
			HashMap<OldType, Integer> rpartitions,
			HashMap<String,Recursive> varmap) {
		// The pivot is what we'll use to split the partition. Essentially, I'll
		// find all those equivalent to the pivot, and all those which are not
		// equivalent => thus, either we make two new sets or there is no change
		// to the old set.
		HashSet<OldType> partition = partitions.get(idx);
		OldType pivot = partition.iterator().next();
		HashSet<OldType> equivs = new HashSet<OldType>();
		HashSet<OldType> nequivs = new HashSet<OldType>();
		boolean change = false;
		
		for(OldType t : partition) {
			if(partitionEquiv(t,pivot,rpartitions,varmap)) {
				// t is equivalent to pivot --> so no change
				equivs.add(t);
			} else {
				// t is not equivalent to pivot
				nequivs.add(t);
				change = true;
			}
		}
		
		if(change) {
			partitions.set(idx,equivs);
			int neqidx = partitions.size();
			partitions.add(nequivs);
			for(OldType t : nequivs) {
				rpartitions.put(t,neqidx);
			}
		}		
		
		return change;
	}

	/**
	 * The purpose of this method is to determine whether two types are
	 * "partition" equivalent. That is, given the current partitioning of types
	 * are they equivalent. Two types are not equivalent if they have a
	 * different "shape" (e.g. NULL is never equivalent to INT; or a record is
	 * never equivalent to a list). Furthermore, two types are not equivalent if
	 * one or more of their (immediate) children is in a different partition.
	 * 
	 * @param t1
	 *            --- first type to check equivalence of
	 * @param t2
	 *            --- second type to check equivalence of	
	 * @param rpartitions
	 *            --- maps types to their partition numbers (i.e. same number ==
	 *            same partition).
	 * @return
	 */
	private static boolean partitionEquiv(OldType t1, OldType t2,
			HashMap<OldType, Integer> rpartitions,
			HashMap<String,Recursive> varmap) {
				
		
		// First, deal with simple non-compound types.
		if (t1 instanceof Void || t1 instanceof Null || t1 instanceof Any
				|| t1 instanceof Int || t1 instanceof Real) {
			// FIXME: need to deal with existential types here.
			return t1.getClass() == t2.getClass();
		}
					
		// now, deal with other compound types
		if(t1 instanceof Set && t2 instanceof Set) {
			Set s1 = (Set) t1;
			Set s2 = (Set) t2;
			return rpartitions.get(s1.element).equals(rpartitions.get(s2.element));
		} else if(t1 instanceof List && t2 instanceof List) {
			List l1 = (List) t1;
			List l2 = (List) t2;
			return rpartitions.get(l1.element).equals(rpartitions.get(l2.element));
		} else if(t1 instanceof Dictionary && t2 instanceof Dictionary) {
			Dictionary d1 = (Dictionary) t1;
			Dictionary d2 = (Dictionary) t2;
			return rpartitions.get(d1.key).equals(rpartitions.get(d2.key))
					&& rpartitions.get(d1.value).equals(
							rpartitions.get(d2.value)); 
		} else if(t1 instanceof Record && t2 instanceof Record) {
			Record r1 = (Record) t1;
			Record r2 = (Record) t2;
			java.util.Set<String> r1keys = r1.types.keySet();
			if(!r1keys.equals(r2.types.keySet())) {
				return false;
			}
			for(String key : r1keys) {
				OldType r1t = r1.types.get(key);
				OldType r2t = r2.types.get(key);
				if(!rpartitions.get(r1t).equals(rpartitions.get(r2t))) {
					return false;
				}
			}
			return true;
		} else if(t1 instanceof Union && t2 instanceof Union) {
			// FIXME: I think there is a bug when we are comparing a union with
			// a non-union type. The bug arises in the case that all bounds of
			// the union are, in fact, equivalent.  No idea how to fix this.
			Union u1 = (Union) t1;	
			Union u2 = (Union) t2;
			for(OldType b1 : u1.bounds) {
				boolean b1matched = false;
				for(OldType b2 : u2.bounds) {
					if(partitionEquiv(b1,b2,rpartitions,varmap)) {
						b1matched = true;
						break;
					}
				}
				if(!b1matched) { return false; }
			}				
			for(OldType b2 : u2.bounds) {
				boolean b2matched = false;
				for(OldType b1 : u1.bounds) {
					if(partitionEquiv(b1,b2,rpartitions,varmap)) {
						b2matched = true;
						break;
					}
				}
				if(!b2matched) { return false; }
			}
			return true;
		} else if(t1 instanceof Recursive) {			
			OldType.Recursive tr = (OldType.Recursive) t1;						
			if(tr.type == null) {
				return partitionEquiv(varmap.get(tr.name).type,t2,rpartitions,varmap);			
			} else {							
				return partitionEquiv(tr.type,t2,rpartitions,varmap);
			}
		} else if(t2 instanceof Recursive) {
			OldType.Recursive tr = (OldType.Recursive) t2;
			if(tr.type == null) {
				return partitionEquiv(t1,varmap.get(tr.name).type,rpartitions,varmap);			
			} else {							
				return partitionEquiv(t1,tr.type,rpartitions,varmap);
			}			
		}
						
		return false;
	}
	
	/**
	 * This method simply walks down the type splitting out every subcomponent.
	 * @param t
	 */
	private static void initialiseEquivs(OldType t, HashSet<OldType> equivs, HashMap<String,Recursive> varmap) {		
		equivs.add(t);
		// now, recurse compound types
		if(t instanceof OldType.List) {			
			OldType.List lt = (OldType.List) t;
			initialiseEquivs(lt.element,equivs,varmap);
		} else if(t instanceof OldType.Set) {
			OldType.Set lt = (OldType.Set) t;
			initialiseEquivs(lt.element,equivs,varmap);
		} else if(t instanceof OldType.Process) {
			OldType.Process lt = (OldType.Process) t;
			initialiseEquivs(lt.element,equivs,varmap);
		} else if(t instanceof OldType.Record) {			
			OldType.Record tt = (Record) t;			
			for (Map.Entry<String, OldType> b : tt.types.entrySet()) {
				initialiseEquivs(b.getValue(),equivs,varmap);	
			}			
		} else if (t instanceof OldType.Recursive) {
			OldType.Recursive rt = (OldType.Recursive) t;
			if(rt.type != null) {
				varmap.put(rt.name, rt);
				initialiseEquivs(rt.type,equivs,varmap);
			} 
		} else if(t instanceof OldType.Union) {
			OldType.Union ut = (OldType.Union) t;
			for(OldType b : ut.bounds) {
				initialiseEquivs(b,equivs,varmap);
			}
		} 
	}

	/**
	 * This method rebuilds a type by merging all components in the same
	 * partition.
	 * 
	 * @param t --- the type to be rebuilt
	 * @param partition --- a list of equivalent components
	 * @param rpartitions --- a map from component to partition number
	 * @return
	 */
	private static OldType rebuild(OldType t, ArrayList<HashSet<OldType>> partitions,
			HashMap<OldType, Integer> rpartitions, HashMap<Integer,String> visited) {
		int pnum = rpartitions.get(t);
		String var = visited.get(pnum);
		
		if(var != null) {
			// indicates we've already visited this node and, hence, we can put
			// in the recursive variable.
			return T_RECURSIVE(var,null);
		}
		
		HashSet<OldType> partition = partitions.get(pnum);
		boolean recursive = false;
		for(OldType component : partition) {
			if(component instanceof OldType.Recursive) {
				// Q) is it possible for component to have null type here?
				recursive = true;
				break;
			}
		}
		
		if(recursive) {
			// TODO: better variable naming
			var = "X" + visited.size();
			visited.put(pnum, var);
		} 

		// remove old recursive wrapper (if there is one)
		while(t instanceof Recursive) {
			Recursive r = (Recursive) t;
			t = r.type; // can t be null now?
		}
		
		if(t instanceof Set) {
			Set s = (Set) t;
			t = T_SET(rebuild(s.element,partitions,rpartitions,visited));
		} else if(t instanceof List) {
			List l = (List) t;
			t = T_LIST(rebuild(l.element,partitions,rpartitions,visited));
		} else if(t instanceof Dictionary) {
			Dictionary d = (Dictionary) t;
			t = T_DICTIONARY(rebuild(d.key,partitions,rpartitions,visited),
					rebuild(d.value,partitions,rpartitions,visited));
		} else if(t instanceof Record) {
			Record r = (Record) t;
			HashMap<String,OldType> types = new HashMap<String,OldType>();
			for(Map.Entry<String,OldType> e : r.types.entrySet()) {
				types.put(e.getKey(),rebuild(e.getValue(),partitions,rpartitions,visited));
			}
			t = T_RECORD(types);
		} else if(t instanceof Union) {
			Union u = (Union) t;
			t = T_VOID;
			for(OldType bound : u.bounds) {
				OldType tmp = rebuild(bound,partitions,rpartitions,visited);
				t = leastUpperBound(tmp,t);
			}
		} 
		
		if(recursive) {
			return T_RECURSIVE(var,t);
		} else {
			return t;
		}
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
	public static OldType.Record effectiveRecordOldType(OldType t) {

		if(t instanceof OldType.Record) {
			return (OldType.Record) t;
		} else if(t instanceof OldType.Union) {
			OldType.Union ut = (OldType.Union) t;
			return effectiveRecordOldType(commonOldType(ut.bounds));
		} else if(t instanceof OldType.Recursive) {
			// this is more tricky. We need to unroll the type once to ensure we
			// don't lose the recursive information.
			OldType.Recursive rt = (OldType.Recursive) t;
			if(rt.type != null) {
				return effectiveRecordOldType(unfold(rt));
			}
		} else if(t instanceof OldType.Named) {
			OldType.Named nt = (OldType.Named) t;
			return effectiveRecordOldType(nt.type);
		} 		
		return null;	
	}
	
	
	private static OldType commonOldType(Collection<? extends OldType> types) {		
		OldType type = types.iterator().next();
		
		if(type instanceof OldType.Record) {
			return commonTupleOldType(types);
		} else if(type instanceof OldType.List) {
			// FIXME: to do			
		} else if(type instanceof OldType.Set) {
			// FIXME: to do
		} 
		
		return null;		
	}

	private static OldType.Record commonTupleOldType(Collection<? extends OldType> types) {
		OldType.Record rt = null;
		for (OldType pt : types) {
			if(!(pt instanceof OldType.Record)) {
				return null;
			}
			OldType.Record tt = (OldType.Record) pt;
			if (rt == null) {
				rt = tt;
			} else {
				HashMap<String, OldType> it = new HashMap<String, OldType>();
				for (Map.Entry<String, OldType> ent : rt.types.entrySet()) {
					OldType ttt = tt.types.get(ent.getKey());
					if (ttt != null) {
						it.put(ent.getKey(), OldType.leastUpperBound(ttt, ent
								.getValue()));
					}
				}				
				
				rt = new OldType.Record(it);
			}
		}
		if (rt != null) {				
			return rt;
		} else {
			return null;
		}		
	}

	/**
	 * The following method converts a type into a "short string". In this case,
	 * named types are return only their name, rather than their actually body.
	 * This method is useful for printing out type information, which can
	 * otherwise be quite bukly and unreadable.
	 * 
	 * @param t
	 * @return
	 */
	public static String toShortString(OldType t) {		
		if (t instanceof Any || t instanceof Void || t instanceof Null
				|| t instanceof Real || t instanceof Int || t instanceof Bool
				|| t instanceof Meta || t instanceof Existential) {			
			return t.toString();
		} else if(t instanceof Set) {
			Set st = (Set) t;
			return "{" + toShortString(st.element) + "}";
		} else if(t instanceof Dictionary) {
			Dictionary st = (Dictionary) t;
			return "{" + toShortString(st.key) + "->" + toShortString(st.value) + "}";
		} else if(t instanceof List) {
			List lt = (List) t;
			return "[" + toShortString(lt.element) + "]";
		} else if(t instanceof Record) {
			Record rt = (Record) t;
			String r = "{";
			boolean firstTime = true;
			for(Map.Entry<String,OldType> f : rt.types.entrySet()) {
				if(!firstTime) {
					r += ", ";
				}
				firstTime=false;
				r += toShortString(f.getValue()) + " " + f.getKey();				
			}
			return r + "}";
		} else if(t instanceof Union) {			
			Union ut = (Union) t;
			String r = "";
			boolean firstTime = true;
			for(OldType b : ut.bounds) {
				if(!firstTime) {
					r += "|";
				}
				firstTime=false;
				r += toShortString(b);				
			}
			return r;
		} else if (t instanceof Process) {
			Process pt = (Process) t;
			return "process " + toShortString(pt.element);
		} else if(t instanceof Named) {			
			Named nt = (Named) t;
			return nt.name.toString();
		} else if(t instanceof Recursive) {			
			Recursive nt = (Recursive) t;
			return nt.name.toString();
		} else if(t instanceof Fun) {
			Fun ft = (Fun) t;
			String args = "";
			boolean firstTime=true;
			for(OldType arg : ft.params) {
				if(!firstTime) {
					args += ",";
				}
				firstTime=false;
				args += arg;
			}
			if(ft.receiver != null) {
				return ft.receiver + ":: " + ft.ret + "(" + args + ")";
			} else {
				return ft.ret + "(" + args + ")";
			}
		} else {
			throw new IllegalArgumentException("Unknown type encountered: " + t);
		}
	}
	
	// =============================================================
	// OldType Classes
	// =============================================================
	
	public static abstract class NonUnion extends OldType {}
	public static abstract class ProcessName extends NonUnion {}

	public static abstract class SetList extends NonUnion {
		public abstract OldType element();
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
			return this == o;
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
			return this == o;
		}
		public int hashCode() {
			return 2;
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
		public final OldType type;
		private Named(NameID name, OldType element) {			
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
		public final OldType element;
		private List(OldType element) {
			this.element = element;
		}
		public OldType element() {
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
		public final OldType element;
		private Set(OldType element) {
			this.element = element;
		}
		public OldType element() {
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
	public static final class Dictionary extends NonUnion {
		public final OldType key;
		public final OldType value;
		
		private Dictionary(OldType key,OldType value) {
			this.key = key;
			this.value = value;
		}
		
		public boolean equals(Object o) {
			if(o instanceof Dictionary) {
				Dictionary l = (Dictionary) o;
				return key.equals(l.key) && value.equals(l.value);
			}
			return false;
		}
		public int hashCode() {
			return key.hashCode() + value.hashCode();
		}
		public String toString() {
			return "{" + key + "->" + value + "}";
		}
	}
	public static final class Union extends OldType {
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
			for(OldType t : bounds) {
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
		public final OldType ret;
		public final ArrayList<OldType> params;
		
		private Fun(ProcessName receiver, OldType ret, OldType... parameters) {
			this.ret = ret;
			this.receiver = receiver;
			this.params = new ArrayList<OldType>();
			for(OldType t : parameters) {
				this.params.add(t);
			}
		}
		private Fun(ProcessName receiver, OldType ret, Collection<OldType> parameters) {
			this.ret = ret;
			this.receiver = receiver;
			this.params = new ArrayList<OldType>(parameters);			
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
			for(OldType p : params) {
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
		public final OldType element;
		private Process(OldType element) {
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
		public final HashMap<String,OldType> types;
		
		private Record(Map<String,OldType> types) {			
			this.types = new HashMap<String,OldType>(types);			
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
		public final String name;
		public final OldType type;
		
		private Recursive(String name, OldType type) {
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
	private static final ArrayList<OldType> types = new ArrayList<OldType>();
	private static final HashMap<OldType,Integer> cache = new HashMap<OldType,Integer>();
	
	private static <T extends OldType> T get(T type) {
		Integer idx = cache.get(type);
		if(idx != null) {
			return (T) types.get(idx);
		} else {				
			cache.put(type, types.size());
			types.add(type);	
			return type;
		}
	}
	
	public static void main(String[] args) {
		
		OldType t1 = unfold(binaryTree("X"));
		System.out.println("BEFORE: " + t1);
		t1 = minimise(t1);
		System.out.println("AFTER: " + t1);
	}
	
	public static OldType.Recursive outerUnfold(OldType.Recursive rt) {
		HashMap<String, OldType> binding = new HashMap<String, OldType>();
		binding.put(rt.name, rt.type);
		return OldType.T_RECURSIVE(rt.name,
				substituteRecursiveOldTypes(rt.type, binding));
	}
	
	public static OldType.Recursive linkedList(String var) {
		HashMap<String,OldType> types = new HashMap<String,OldType>();
		types.put("data",T_INT);
		types.put("next",T_RECURSIVE(var,null));
		OldType t6 = T_RECORD(types);
		return T_RECURSIVE(var,leastUpperBound(T_NULL,t6));		
	}
	
	public static OldType.Recursive binaryTree(String var) {
		HashMap<String,OldType> types = new HashMap<String,OldType>();
		types.put("data",T_INT);
		types.put("left",T_RECURSIVE(var,null));
		types.put("right",T_RECURSIVE(var,null));
		OldType t6 = T_RECORD(types);
		return T_RECURSIVE(var,leastUpperBound(T_NULL,t6));		
	}
}
