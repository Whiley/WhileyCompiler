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

package wyjc.ast.types;

import java.util.*;

import wyjc.ast.attrs.SyntacticElement;
import wyjc.ast.exprs.*;
import wyjc.ast.exprs.logic.*;
import static wyjc.util.SyntaxError.*;

public class Types {
	public static final AnyType T_ANY = new AnyType();
	public static final VoidType T_VOID = new VoidType();
	public static final ExistentialType T_EXISTENTIAL = new ExistentialType();
	public static final BoolType T_BOOL = new BoolType(null);
	public static final IntType T_INT = new IntType(null);
	public static final RealType T_REAL = new RealType(null);
	
	public static IntType T_INT(Condition constraint) {
		if(constraint == null) {
			return T_INT;
		} else {
			return new IntType(constraint);
		}
	}
	
	public static RealType T_REAL(Condition constraint) {
		if(constraint == null) {
			return T_REAL;
		} else {
			return new RealType(constraint);
		}
	}
	
	public static BoolType T_BOOL(Condition constraint) {
		if(constraint == null) {
			return T_BOOL;
		} else {
			return new BoolType(constraint);
		}
	}
	
	public static Type leastUpperBound(Type t1, Type t2) {		
		// NOTE. There are still bugs in this algorithm.
		
		if(isStrictSubtype(t1,t2)) {
			return t1;
		} else if(isStrictSubtype(t2,t1)) {
			return t2;
		} else if(baseEquivalent(t1,t2)) {
			return recondition(t1, new Or(t1.constraint(), t2.constraint()));
		} else if(isBaseSubtype(t1,t2)) {
			Condition c = t1.constraint();
			String v;
			c = new And(c,new TypeGate(new Variable("$"),v,t2.constraint()));
		} else if(isBaseSubtype(t2,t1)) {
			
		}
		
		if(t1 instanceof UnionType && t2 instanceof UnionType) {
			UnionType ut1 = (UnionType) t1;
			UnionType ut2 = (UnionType) t2;
			ArrayList<NonUnionType> types = new ArrayList<NonUnionType>(ut1.types());
			
			// OK, this is totally broken because it doesn't eliminate subsumed
			// types. I really need to work harder here, but for now i'm
			// slacking off...
			
			types.addAll(ut2.types());
			return new UnionType(types);
			
		} else if(t1 instanceof UnionType) {			
			UnionType ut1 = (UnionType) t1;
			ArrayList<NonUnionType> types = new ArrayList<NonUnionType>(ut1.types());
			
			for(int i=0;i!=types.size();++i) {
				NonUnionType t = types.get(i);
				if(t2.isBaseSubtype(t, Collections.EMPTY_MAP)) {
					types.set(i, (NonUnionType) t2);
					return new UnionType(types);
				}
			}
						
			types.add((NonUnionType) t2);
			return new UnionType(types);
		} else if(t2 instanceof UnionType) {
			UnionType ut2 = (UnionType) t2;
			ArrayList<NonUnionType> types = new ArrayList<NonUnionType>(ut2.types());
			
			for(int i=0;i!=types.size();++i) {
				NonUnionType t = types.get(i);
				if(t1.isBaseSubtype(t, Collections.EMPTY_MAP)) {
					types.set(i, (NonUnionType) t1);			
					return new UnionType(types);
				}
			}					
			types.add((NonUnionType) t1);			
			return new UnionType(types);			
		} else if(t1 instanceof ListType && t2 instanceof ListType) {
			ListType l1 = (ListType) t1;
			ListType l2 = (ListType) t2;
			// FIXME: bug here
			return new ListType(leastUpperBound(l1.element(),l2.element()));
		} else if(t1 instanceof SetType && t2 instanceof SetType) {
			SetType s1 = (SetType) t1;
			SetType s2 = (SetType) t2;
			// FIXME: bug here
			return new SetType(leastUpperBound(s1.element(),s2.element()));
		} else {
			return new UnionType((NonUnionType) t1, (NonUnionType) t2);
		}
	}
	
	public static Type greatestLowerBound(Type t1, Type t2) {
		
		// NOTE. There are still bugs in this algorithm.
		
		if(t1.isBaseSubtype(t2, Collections.EMPTY_MAP)) {									
			return t1;
		} else if(t2.isBaseSubtype(t1, Collections.EMPTY_MAP)) {			
			return t2;
		}
		
		if(t1 instanceof ListType && t2 instanceof ListType) {
			ListType l1 = (ListType) t1;
			ListType l2 = (ListType) t2;			
			return new ListType(greatestLowerBound(l1.element(),l2.element()));
		} else if(t1 instanceof SetType && t2 instanceof SetType) {
			SetType s1 = (SetType) t1;
			SetType s2 = (SetType) t2;
			return new SetType(greatestLowerBound(s1.element(),s2.element()));
		} else {
			// THIS IS COMPLETELY BROKEN. NEED TO ADD A SPECIAL INTERSECTION TYPE.
			return new UnionType((NonUnionType) t1, (NonUnionType) t2);
		}
	}

	/**
	 * Determine whether a given type is a subtype of this type. Observe that
	 * this only considers the types themselves, not any constraints that may be
	 * imposed upon them (since we cannot reason about constraints here).
	 * Therefore, it's entirely possible that this will report two types are
	 * subtypes of each other when, in fact, they're not (if constraints are
	 * considered). For example, consider:
	 * 
	 * <pre>
	 * define nat as int where $ >= 0
	 * define pos as int where $ > 0
	 * </pre>
	 * 
	 * Now, this method will report that nat <: pos (since int <: int).
	 * 
	 * @param t
	 *            --- the type being tested to see whether it's a subtype of
	 *            this type.
	 * @param environment
	 *            --- this is used to propagate the types of internally
	 *            generated variables (i.e. arising from RecursiveType).
	 *            Initially, you should just supply Collections.EMPTY_MAP.
	 * @return
	 */
	public static boolean isBaseSubtype(Type t1, Type t2) {
		return isBaseSubtype(t1,t2,Collections.EMPTY_MAP);
	}
	private static boolean isBaseSubtype(Type t1, Type t2, Map<String,Type> environment) {
		if(t1.constraint() != null) {
			return false; // cannot be sure we have a strict subtype
		} else if(t1 instanceof IntType) {
			return t2 instanceof IntType;
		} else if(t1 instanceof RealType) {
			return t2 instanceof IntType || t2 instanceof RealType;
		} else if(t1 instanceof BoolType) {
			return t2 instanceof BoolType;
		} else if(t1 instanceof AnyType) {
			return t2 instanceof AnyType;
		} else if(t1 instanceof ExistentialType) {
			return false;
		} else if(t1 instanceof VoidType) {
			return true;
		} else if(t1 instanceof NamedType && t2 instanceof NamedType) {
			NamedType nt1 = (NamedType) t1;			
			NamedType nt2 = (NamedType) t2;
			return nt1.name().equals(nt2.name())
					&& nt1.module().equals(nt2.module());										
		} else if(t1 instanceof ProcessType && t2 instanceof ProcessType) {
			ProcessType pt1 = (ProcessType) t1;			
			ProcessType pt2 = (ProcessType) t2;
			return isBaseSubtype(pt1.element(),pt2.element(),environment);							
		} else if(t1 instanceof ListType && t2 instanceof ListType) {
			ListType pt1 = (ListType) t1;			
			ListType pt2 = (ListType) t2;
			return isBaseSubtype(pt1.element(),pt2.element(),environment);							
		} else if(t1 instanceof SetType && t2 instanceof SetType) {
			SetType pt1 = (SetType) t1;			
			SetType pt2 = (SetType) t2;
			return isBaseSubtype(pt1.element(),pt2.element(),environment);							
		} else if(t1 instanceof TupleType && t2 instanceof TupleType) {
			TupleType tt1 = (TupleType) t1;			
			TupleType tt2 = (TupleType) t2;
				
			for (Map.Entry<String, Type> p : tt1.types().entrySet()) {
				String n = p.getKey();
				Type mt = tt2.types().get(n);				
				Type ttt = p.getValue();
				if (mt == null || !isBaseSubtype(ttt,mt,environment)) {					
					return false;
				}
			}				
			
			return true;			
		} else if(t1 instanceof RecursiveType) {
			RecursiveType rt1 = (RecursiveType) t1;
			if(t1.equals(t2)) {
				return true;
			} else if(rt1.type() == null) {			
				// leaf case, so unroll one level
				Type tmp = environment.get(rt1.name());			
				return tmp != null && isBaseSubtype(tmp,t2,environment);
			} else if(t2 instanceof RecursiveType) {
				// non-leaf case
				RecursiveType rt2 = (RecursiveType) t2;			
				Type nt_type = rt2.type();
				HashMap<String,Type> binding = new HashMap();
				binding.put(rt2.name(), new RecursiveType(rt1.name(),null,null));
				t2 = nt_type.substitute(binding);
			}
			
			environment = new HashMap<String,Type>(environment);
			// FIXME: potential for variable capture here?
			environment.put(rt1.name(), rt1.type());
			return isBaseSubtype(rt1.type(),t2,environment);	
		} else if(t1 instanceof UnionType) {
			UnionType ut1 = (UnionType) t1;
			if(t2 instanceof UnionType) {
				UnionType ut2 = (UnionType) t2;
				for(Type b1 : ut1.types()) {
					boolean matched = false;
					for(Type b2 : ut2.types()) {
						if(isBaseSubtype(b1,b2, environment)) {
							matched = true;
						}
					}
					if(!matched) {
						return false;
					}
				}
				return true;
			} else {
				for(Type b : ut1.types()) {
					if(isBaseSubtype(b,t2, environment)) {
						return true;
					}
				}
				if (t2 instanceof ProcessType) {
					ProcessType pt = (ProcessType) t2;
					return isBaseSubtype(ut1, pt.element(), environment);
				}
				return false;
			}
		} else if(t1 instanceof FunType && t2 instanceof FunType) {						
			FunType ft1 = (FunType) t1;
			FunType ft2 = (FunType) t2;
			List<Type> ft1params = ft1.parameters();
			List<Type> ft2params = ft2.parameters();
			if(ft2params.size() == ft1params.size()) {
				for(int i=0;i!=ft2params.size();++i) {
					Type tt1 = ft1params.get(i);
					Type tt2 = ft2params.get(i);
					if(!isBaseSubtype(tt1,tt2,environment)) {
						return false;
					}
				}
				return isBaseSubtype(ft2.returnType(), ft1.returnType(), environment);
			}
		}
		
		return false;
	}
	
	/**
	 * Check whether t2 is a strict subtype of t1. In this case, strict means
	 * that it is definitely a subtype, even when constraints are taken into
	 * account.
	 * 
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static boolean isStrictSubtype(Type t1, Type t2) {
		return isStrictSubtype(t1,t2,Collections.EMPTY_MAP);
	}
	private static boolean isStrictSubtype(Type t1, Type t2, Map<String,Type> environment) {
		if(t1.constraint() != null) {
			return false; // cannot be sure we have a strict subtype
		} else if(t1 instanceof IntType) {
			return t2 instanceof IntType;
		} else if(t1 instanceof RealType) {
			return t2 instanceof IntType || t2 instanceof RealType;
		} else if(t1 instanceof BoolType) {
			return t2 instanceof BoolType;
		} else if(t1 instanceof AnyType) {
			return t2 instanceof AnyType;
		} else if(t1 instanceof ExistentialType) {
			return false;
		} else if(t1 instanceof VoidType) {
			return true;
		} else if(t1 instanceof NamedType && t2 instanceof NamedType) {
			NamedType nt1 = (NamedType) t1;			
			NamedType nt2 = (NamedType) t2;
			return nt1.name().equals(nt2.name())
					&& nt1.module().equals(nt2.module());										
		} else if(t1 instanceof ProcessType && t2 instanceof ProcessType) {
			ProcessType pt1 = (ProcessType) t1;			
			ProcessType pt2 = (ProcessType) t2;
			return isStrictSubtype(pt1.element(),pt2.element(),environment);							
		} else if(t1 instanceof ListType && t2 instanceof ListType) {
			ListType pt1 = (ListType) t1;			
			ListType pt2 = (ListType) t2;
			return isStrictSubtype(pt1.element(),pt2.element(),environment);							
		} else if(t1 instanceof SetType && t2 instanceof SetType) {
			SetType pt1 = (SetType) t1;			
			SetType pt2 = (SetType) t2;
			return isStrictSubtype(pt1.element(),pt2.element(),environment);							
		} else if(t1 instanceof TupleType && t2 instanceof TupleType) {
			TupleType tt1 = (TupleType) t1;			
			TupleType tt2 = (TupleType) t2;
				
			for (Map.Entry<String, Type> p : tt1.types().entrySet()) {
				String n = p.getKey();
				Type mt = tt2.types().get(n);				
				Type ttt = p.getValue();
				if (mt == null || !isStrictSubtype(ttt,mt,environment)) {					
					return false;
				}
			}				
			
			return true;			
		} else if(t1 instanceof RecursiveType) {
			RecursiveType rt1 = (RecursiveType) t1;
			if(t1.equals(t2)) {
				return true;
			} else if(rt1.type() == null) {			
				// leaf case, so unroll one level
				Type tmp = environment.get(rt1.name());			
				return tmp != null && isStrictSubtype(tmp,t2,environment);
			} else if(t2 instanceof RecursiveType) {
				// non-leaf case
				RecursiveType rt2 = (RecursiveType) t2;			
				Type nt_type = rt2.type();
				HashMap<String,Type> binding = new HashMap();
				binding.put(rt2.name(), new RecursiveType(rt1.name(),null,null));
				t2 = nt_type.substitute(binding);
			}
			
			environment = new HashMap<String,Type>(environment);
			// FIXME: potential for variable capture here?
			environment.put(rt1.name(), rt1.type());
			return isStrictSubtype(rt1.type(),t2,environment);	
		} else if(t1 instanceof UnionType) {
			UnionType ut1 = (UnionType) t1;
			if(t2 instanceof UnionType) {
				UnionType ut2 = (UnionType) t2;
				for(Type b1 : ut1.types()) {
					boolean matched = false;
					for(Type b2 : ut2.types()) {
						if(isStrictSubtype(b1,b2, environment)) {
							matched = true;
						}
					}
					if(!matched) {
						return false;
					}
				}
				return true;
			} else {
				for(Type b : ut1.types()) {
					if(isStrictSubtype(b,t2, environment)) {
						return true;
					}
				}
				if (t2 instanceof ProcessType) {
					ProcessType pt = (ProcessType) t2;
					return isStrictSubtype(ut1, pt.element(), environment);
				}
				return false;
			}
		} else if(t1 instanceof FunType && t2 instanceof FunType) {						
			FunType ft1 = (FunType) t1;
			FunType ft2 = (FunType) t2;
			List<Type> ft1params = ft1.parameters();
			List<Type> ft2params = ft2.parameters();
			if(ft2params.size() == ft1params.size()) {
				for(int i=0;i!=ft2params.size();++i) {
					Type tt1 = ft1params.get(i);
					Type tt2 = ft2params.get(i);
					if(!isStrictSubtype(tt1,tt2,environment)) {
						return false;
					}
				}
				return isStrictSubtype(ft2.returnType(), ft1.returnType(), environment);
			}
		}
		
		return false;
	}
	

	public static Type recondition(Type t, Condition c) {
		if (t instanceof VoidType || t instanceof ExistentialType
				|| t instanceof NamedType) {
			return t;
		} else if(t instanceof IntType) {
			return Types.T_INT(c);
		} else if(t instanceof RealType) {
			return Types.T_INT(c);
		} else if(t instanceof ListType) {
			ListType lt = (ListType) t;
			return new ListType(lt.element(),c);
		} else if(t instanceof SetType) {
			SetType st = (SetType) t;
			return new SetType(st.element(),c);
		} else if(t instanceof ProcessType) {
			ProcessType st = (ProcessType) t;
			return new ProcessType(st.element(),c);
		} else if(t instanceof RecursiveType) {
			RecursiveType st = (RecursiveType) t;
			return new RecursiveType(st.name(),st.type(),c);
		} else if(t instanceof TupleType) {
			TupleType st = (TupleType) t;
			return new TupleType(st.types(),c);
		} else if(t instanceof UnionType) {
			UnionType st = (UnionType) t;
			return new UnionType(c,st.types());
		} else if(t instanceof FunType) {
			FunType ft = (FunType) t;
			return new FunType(ft.returnType(),ft.parameters(),c);
		} else {
			throw new IllegalArgumentException("unknown type encountered: " + t);
		}
	}

	public static TupleType effectiveTupleType(Type t, SyntacticElement elem) {		
		if(t instanceof TupleType) {
			return (TupleType) t;
		} else if(t instanceof UnionType) {
			UnionType ut = (UnionType) t;
			return effectiveTupleType(commonType(ut.types()),elem);
		} else if(t instanceof NamedType) {
			NamedType nt = (NamedType) t;
			return effectiveTupleType(nt.type(),elem);
		} else if(t instanceof RecursiveType) {
			// this is more tricky. We need to unroll the type once to ensure we
			// don't lose the recursive information.
			RecursiveType rt = (RecursiveType) t;
			HashMap<String,Type> binding = new HashMap<String,Type>();
			binding.put(rt.name(), rt);
			t = rt.type().substitute(binding);
			return effectiveTupleType(t,elem);
		}
		syntaxError("expecting tuple type, got: " + t,elem);
		return null;
	}
	
	public static Type commonType(Collection<? extends Type> types) {		
		Type type = types.iterator().next();
		
		if(type instanceof TupleType) {
			return commonTupleType(types);
		} else if(type instanceof ListType) {
			// FIXME: to do			
		} else if(type instanceof SetType) {
			// FIXME: to do
		} 
		
		return null;		
	}
	
	public static TupleType commonTupleType(Collection<? extends Type> types) {
		TupleType rt = null;
		for (Type pt : types) {
			if(!(pt instanceof TupleType)) {
				return null;
			}
			TupleType tt = (TupleType) pt;
			if (rt == null) {
				rt = tt;
			} else {
				HashMap<String, Type> it = new HashMap<String, Type>();
				for (Map.Entry<String, Type> ent : rt.types().entrySet()) {
					Type ttt = tt.get(ent.getKey());
					if (ttt != null) {
						it.put(ent.getKey(), Types.leastUpperBound(ttt, ent
								.getValue()));
					}
				}
				
				rt = new TupleType(it);
			}
		}
		if (rt != null && rt.types().size() > 0) {				
			return rt;
		} else {
			return null;
		}		
	}
}
