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

import wyjc.ast.attrs.*;
import wyjc.ast.exprs.*;
import wyjc.ast.exprs.logic.*;
import wyjc.ast.exprs.set.SetComprehension;
import wyjc.ast.exprs.tuple.TupleAccess;
import wyjc.ast.types.unresolved.*;
import wyjc.util.*;
import static wyjc.util.SyntaxError.*;

public class Types {
	public static final AnyType T_ANY = new AnyType();
	public static final VoidType T_VOID = new VoidType();
	public static final ExistentialType T_EXISTENTIAL = new ExistentialType();
	private static final BoolType T_BOOL = new BoolType(null);
	private static final IntType T_INT = new IntType(null);
	private static final RealType T_REAL = new RealType(null);
	
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
		} else if(t1 instanceof IntType && t2 instanceof IntType) {
			return Types.T_INT(or(t1.constraint(),t2.constraint()));
		} else if(t1 instanceof RealType && t2 instanceof RealType) {
			return Types.T_REAL(or(t1.constraint(),t2.constraint()));
		} else if(t1 instanceof IntType && t2 instanceof RealType) {
			String v = Variable.freshVar();
			Condition tg = new TypeGate(t1,v,new Variable("$"),new BoolVal(true));
			return Types.T_REAL(or(tg,t2.constraint()));
		} else if(t1 instanceof RealType && t2 instanceof IntType) {
			String v = Variable.freshVar();
			Condition tg = new TypeGate(t2,v,new Variable("$"),new BoolVal(true));
			return Types.T_REAL(or(tg,t1.constraint()));
		} else if(t1 instanceof ListType && t2 instanceof ListType) {
			ListType l1 = (ListType) t1;
			ListType l2 = (ListType) t2;
			return new ListType(leastUpperBound(l1.element(),l2.element()),or(t1.constraint(),t2.constraint()));
		} else if(t1 instanceof SetType && t2 instanceof SetType) {
			SetType s1 = (SetType) t1;
			SetType s2 = (SetType) t2;
			return new SetType(leastUpperBound(s1.element(),s2.element()),or(t1.constraint(),t2.constraint()));
		} else if(t1 instanceof TupleType && t2 instanceof TupleType) {
			TupleType tt1 = (TupleType) t1;
			TupleType tt2 = (TupleType) t2;
			if(tt1.types().keySet().equals(tt2.types().keySet())) {
				HashMap<String,Type> types = new HashMap<String,Type>();
				for(Map.Entry<String,Type> e : tt1.types().entrySet()) {
					Type type = leastUpperBound(tt2.types().get(e.getKey()),e.getValue());
					types.put(e.getKey(), type);
				}
				return new TupleType(types,or(t1.constraint(),t2.constraint()));
			}			
		} else if (t1 instanceof UnionType && t2 instanceof UnionType) {
			UnionType ut1 = (UnionType) t1;
			UnionType ut2 = (UnionType) t2;
			ArrayList<NonUnionType> types = new ArrayList<NonUnionType>(ut1
					.types());

			// OK, this could be improved because it doesn't eliminate subsumed
			// types. I really need to work harder here, but for now i'm
			// slacking off...

			types.addAll(ut2.types());
			return new UnionType(types, or(t1.constraint(), t2
					.constraint()));
		} else if(t1 instanceof UnionType) {			
			UnionType ut1 = (UnionType) t1;
			ArrayList<NonUnionType> types = new ArrayList<NonUnionType>(ut1.types());
			
			for(int i=0;i!=types.size();++i) {
				NonUnionType t = types.get(i);
				if(isStrictSubtype(t2, t)) {
					types.set(i, (NonUnionType) t2);
					return new UnionType(types, ut1.constraint());
				} else if(isBaseEquivalent(t,t2)) {
					types.set(i, (NonUnionType) recondition(t, or(
							t.constraint(), t2.constraint())));
					return new UnionType(types,ut1.constraint());
				}
			}
						
			types.add((NonUnionType) t2);
			return new UnionType(types, or(t1.constraint(), t2
					.constraint()));
		} else if(t2 instanceof UnionType) {
			UnionType ut2 = (UnionType) t2;
			ArrayList<NonUnionType> types = new ArrayList<NonUnionType>(ut2.types());
			
			for(int i=0;i!=types.size();++i) {
				NonUnionType t = types.get(i);
				if(isStrictSubtype(t1, t)) {
					types.set(i, (NonUnionType) t1);
					return new UnionType(types, ut2.constraint());
				} else if(isBaseEquivalent(t,t1)) {
					types.set(i, (NonUnionType) recondition(t, or(
							t.constraint(), t1.constraint())));
					return new UnionType(types,ut2.constraint());
				}
			}
			
			types.add((NonUnionType) t1);
			return new UnionType(types, or(t2.constraint(), t1
					.constraint()));
		} 
				
		// FIXME: could do better with recursive types definitely
		
		return new UnionType((NonUnionType) t1, (NonUnionType) t2);		
	}
	
	public static Type greatestLowerBound(Type t1, Type t2) {
		return greatestLowerBound(t1,t2,Collections.EMPTY_MAP);				
	}
	
	private static Type greatestLowerBound(Type t1, Type t2, Map<String,Type> environment) {					
		// NOTE. There are still bugs in this algorithm.
		if(isStrictSubtype(t1,t2)) {			
			return t2;
		} else if(isStrictSubtype(t2,t1)) {			
			return t1;
		} else if(t1 instanceof IntType && t2 instanceof IntType) {
			return Types.T_INT(and(t1.constraint(),t2.constraint()));
		} else if(t1 instanceof RealType && t2 instanceof RealType) {
			return Types.T_REAL(and(t1.constraint(),t2.constraint()));
		} else if(t1 instanceof IntType && t2 instanceof RealType) {
			String v = Variable.freshVar();
			Condition tg = new TypeGate(t1,v,new Variable("$"),new BoolVal(true));
			return Types.T_REAL(and(tg,t2.constraint()));
		} else if(t1 instanceof RealType && t2 instanceof IntType) {
			String v = Variable.freshVar();
			Condition tg = new TypeGate(t2,v,new Variable("$"),new BoolVal(true));
			return Types.T_REAL(and(tg,t1.constraint()));
		} else if(t1 instanceof ListType && t2 instanceof ListType) {
			ListType l1 = (ListType) t1;
			ListType l2 = (ListType) t2;
			return new ListType(greatestLowerBound(l1.element(),l2.element(),environment),and(l1.constraint(),l2.constraint()));
		} else if(t1 instanceof SetType && t2 instanceof SetType) {
			SetType s1 = (SetType) t1;
			SetType s2 = (SetType) t2;
			return new SetType(greatestLowerBound(s1.element(),s2.element(),environment),and(t1.constraint(),t2.constraint()));
		} else if(t1 instanceof TupleType && t2 instanceof TupleType) {
			TupleType tt1 = (TupleType) t1;
			TupleType tt2 = (TupleType) t2;
			if(tt1.types().keySet().equals(tt2.types().keySet())) {
				HashMap<String,Type> types = new HashMap<String,Type>();
				for(Map.Entry<String,Type> e : tt1.types().entrySet()) {
					Type type = greatestLowerBound(tt2.types().get(e.getKey()),e.getValue(),environment);
					types.put(e.getKey(), type);
				}
				return new TupleType(types,and(t1.constraint(),t2.constraint()));
			}			
		} else if(t1 instanceof UnionType && t2 instanceof UnionType) {
			UnionType ut1 = (UnionType) t1;
			UnionType ut2 = (UnionType) t2;
			HashSet<NonUnionType> types = new HashSet<NonUnionType>();
			
			for(NonUnionType b1 : ut1.types()) {
				for(NonUnionType b2 : ut2.types()) {
					if(isBaseSubtype(b1,b2,environment) || isBaseSubtype(b2,b1,environment)) {
						Type glb = greatestLowerBound(b1,b2,environment);
						if(glb instanceof UnionType) {
							types.addAll(((UnionType)glb).types());
						} else {
							types.add((NonUnionType)glb);
						}
					}
				}
			}
			
			if(types.isEmpty()) {
				return T_VOID;
			} else if(types.size() == 1) {
				return types.iterator().next();
			} else {
				return new UnionType(types);
			}
		} else if(t1 instanceof UnionType) {
			UnionType ut1 = (UnionType) t1;			
			HashSet<NonUnionType> types = new HashSet<NonUnionType>();
			
			for(NonUnionType b : ut1.types()) {
				if(isBaseSubtype(b,t2,environment) || isBaseSubtype(t2,b, environment)) {
					Type glb = greatestLowerBound(b,t2,environment);
					if(glb instanceof UnionType) {
						types.addAll(((UnionType)glb).types());
					} else {
						types.add((NonUnionType)glb);
					}
				}
			}
			
			if(types.isEmpty()) {
				return T_VOID;
			} else if(types.size() == 1) {
				return types.iterator().next();
			} else {
				return new UnionType(types);
			}
		} else if(t2 instanceof UnionType) {
			UnionType ut2 = (UnionType) t2;
			HashSet<NonUnionType> types = new HashSet<NonUnionType>();
			
			for(NonUnionType b : ut2.types()) {
				if (isBaseSubtype(b, t1, environment) || isBaseSubtype(t1, b, environment)) {
					Type glb = greatestLowerBound(b,t1,environment);
					if(glb instanceof UnionType) {
						types.addAll(((UnionType)glb).types());
					} else {
						types.add((NonUnionType)glb);
					}
				}
			}
			
			if(types.isEmpty()) {
				return T_VOID;
			} else if(types.size() == 1) {
				return types.iterator().next();
			} else {
				return new UnionType(types);
			}
		} else if(t1 instanceof RecursiveType && t2 instanceof RecursiveType) {
			RecursiveType rt1 = (RecursiveType) t1;
			RecursiveType rt2 = (RecursiveType) t2;
			Type rt1_type = rt1.type();
			Type rt2_type = rt2.type();
			
			if(rt1_type == null && rt2_type == null) {
				return rt1_type;
			} else if(rt1_type != null && rt2_type != null) {
				environment = new HashMap<String,Type>(environment);
				environment.put(rt1.name(), rt1_type);
				environment.put(rt2.name(), rt2_type);
				return new RecursiveType(rt1.name(), greatestLowerBound(
						rt1_type, rt2_type, environment), and(rt1.constraint(),
						rt2.constraint()));
			}
			
		} else if(t1 instanceof RecursiveType) {
			RecursiveType rt1 = (RecursiveType) t1;
			t1 = rt1.type();
			if(t1 == null) {
				t1 = environment.get(rt1.name());
			} else {
				environment = new HashMap<String,Type>(environment);
				environment.put(rt1.name(), rt1.type());
			}
			return greatestLowerBound(t1,t2,environment);
		} else if(t2 instanceof RecursiveType) {
			RecursiveType rt2 = (RecursiveType) t2;
			t2 = rt2.type();
			if(t2 == null) {
				t2 = environment.get(rt2.name());
			} else {
				environment = new HashMap<String,Type>(environment);
				environment.put(rt2.name(), rt2.type());
			}
			return greatestLowerBound(t1,t2,environment);
		}
		
		return Types.T_VOID;	
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
		if(t2 instanceof VoidType) {
			return true;
		} else if(t1 instanceof IntType) {
			return t2 instanceof IntType;
		} else if(t1 instanceof RealType) {
			return t2 instanceof IntType || t2 instanceof RealType;
		} else if(t1 instanceof BoolType) {
			return t2 instanceof BoolType;
		} else if(t1 instanceof AnyType) {
			return true;
		} else if(t1 instanceof ExistentialType) {
			return false;
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
		if(t2 instanceof VoidType) {
			return true;
		} else if(t1.constraint() != null) {
			return false; // cannot be sure we have a strict subtype
		} else if(t1 instanceof IntType) {
			return t2 instanceof IntType;
		} else if(t1 instanceof RealType) {
			return t2 instanceof IntType || t2 instanceof RealType;
		} else if(t1 instanceof BoolType) {
			return t2 instanceof BoolType;
		} else if(t1 instanceof AnyType) {
			return true;
		} else if(t1 instanceof ExistentialType) {
			return false;
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

	/**
	 * Two types are ease equivalent if their underlying types are identical.
	 * That is, when all constraints are ignored.
	 * 
	 * @param t1
	 * @param t2
	 * @param environment
	 * @return
	 */
	public static boolean isBaseEquivalent(Type t1, Type t2) {		
		if(t1 instanceof IntType) {
			return t2 instanceof IntType;
		} else if(t1 instanceof RealType) {
			return t2 instanceof RealType;
		} else if(t1 instanceof BoolType) {
			return t2 instanceof BoolType;
		} else if(t1 instanceof AnyType) {
			return t2 instanceof AnyType;
		} else if(t1 instanceof ExistentialType) {
			return t2 instanceof ExistentialType;
		} else if(t1 instanceof VoidType) {
			return t2 instanceof VoidType;
		} else if(t1 instanceof NamedType && t2 instanceof NamedType) {
			NamedType nt1 = (NamedType) t1;			
			NamedType nt2 = (NamedType) t2;
			return nt1.name().equals(nt2.name())
					&& nt1.module().equals(nt2.module());										
		} else if(t1 instanceof ProcessType && t2 instanceof ProcessType) {
			ProcessType pt1 = (ProcessType) t1;			
			ProcessType pt2 = (ProcessType) t2;
			return isBaseEquivalent(pt1.element(),pt2.element());							
		} else if(t1 instanceof ListType && t2 instanceof ListType) {
			ListType pt1 = (ListType) t1;			
			ListType pt2 = (ListType) t2;
			return isBaseEquivalent(pt1.element(),pt2.element());							
		} else if(t1 instanceof SetType && t2 instanceof SetType) {
			SetType pt1 = (SetType) t1;			
			SetType pt2 = (SetType) t2;
			return isBaseEquivalent(pt1.element(),pt2.element());							
		} else if(t1 instanceof TupleType && t2 instanceof TupleType) {
			TupleType tt1 = (TupleType) t1;			
			TupleType tt2 = (TupleType) t2;
				
			for (Map.Entry<String, Type> p : tt1.types().entrySet()) {
				String n = p.getKey();
				Type mt = tt2.types().get(n);				
				Type ttt = p.getValue();
				if (mt == null || !isBaseEquivalent(ttt,mt)) {					
					return false;
				}
			}				
			
			return true;			
		} else if(t1 instanceof RecursiveType) {
			RecursiveType rt1 = (RecursiveType) t1;
			if(t1.equals(t2)) {
				return true;
			} else if(t2 instanceof RecursiveType) {				
				RecursiveType rt2 = (RecursiveType) t2;
				
				if(rt1.type() == null) {
					return rt2.type() == null;
				}
				
				HashMap<String,Type> binding = new HashMap();
				binding.put(rt2.name(), new RecursiveType(rt1.name(),null,null));				
				return isBaseEquivalent(rt1.type(),rt2.type().substitute(binding));
			}
		} else if(t1 instanceof UnionType && t2 instanceof UnionType) {
			UnionType ut1 = (UnionType) t1;			
			UnionType ut2 = (UnionType) t2;
			Set<NonUnionType> ut1types = ut1.types();
			Set<NonUnionType> ut2types = ut2.types();
			if(ut1types.size() != ut2types.size()) {
				return false;
			}
			Iterator<NonUnionType> iter = ut2.types().iterator();
			for(Type tt1 : ut1types) {
				Type tt2 = iter.next();
				if(!isBaseEquivalent(tt1,tt2)) {
					return false;
				}				
			}
			return true;			
		} else if(t1 instanceof FunType && t2 instanceof FunType) {						
			FunType ft1 = (FunType) t1;
			FunType ft2 = (FunType) t2;
			List<Type> ft1params = ft1.parameters();
			List<Type> ft2params = ft2.parameters();
			if(ft2params.size() == ft1params.size()) {
				for(int i=0;i!=ft2params.size();++i) {
					Type tt1 = ft1params.get(i);
					Type tt2 = ft2params.get(i);
					if(!isBaseEquivalent(tt1,tt2)) {
						return false;
					}
				}
				return isBaseEquivalent(ft2.returnType(), ft1.returnType());
			}
		}
		
		return false;
	}

	public static <T extends Type> T stripConstraints(T type) {		
		if(type instanceof IntType) {
			return (T) Types.T_INT(null);
		} else if(type instanceof RealType) {
			return (T) Types.T_REAL(null);			
		} else if(type instanceof BoolType) {
			return (T) Types.T_BOOL(null);			
		} else if(type instanceof AnyType) {
			return (T) Types.T_ANY;			
		} else if(type instanceof ExistentialType) {
			return (T) Types.T_EXISTENTIAL;		
		} else if(type instanceof VoidType) {
			return (T) Types.T_VOID;
		} else if(type instanceof NamedType) {
			NamedType ntype = (NamedType) type;						
			return (T) new NamedType(ntype.module(),ntype.name(),stripConstraints(ntype.type()));										
		} else if(type instanceof ProcessType) {
			ProcessType ptype = (ProcessType) type;						
			return (T) new ProcessType(stripConstraints(ptype.element()));							
		} else if(type instanceof ListType) {
			ListType ptype = (ListType) type;						
			return (T) new ListType(stripConstraints(ptype.element()));							
		} else if(type instanceof SetType) {
			SetType ptype = (SetType) type;						
			return (T) new SetType(stripConstraints(ptype.element()));								
		} else if(type instanceof TupleType) {
			TupleType ttype = (TupleType) type;						
			HashMap<String,Type> types = new HashMap<String,Type>();
			for (Map.Entry<String, Type> p : ttype.types().entrySet()) {				
				types.put(p.getKey(), stripConstraints(p.getValue()));
			}							
			return (T) new TupleType(types);			
		} else if(type instanceof RecursiveType) {
			RecursiveType rt = (RecursiveType) type;
			Type rt_type = rt.type();
			if(rt_type != null) {
				rt_type = stripConstraints(rt_type);
			}
			return (T) new RecursiveType(rt.name(),rt_type,null);
		} else if(type instanceof UnionType) {
			UnionType utype = (UnionType) type;						
			HashSet<NonUnionType> types = new HashSet<NonUnionType>();
			for(NonUnionType t : utype.types()) {
				types.add(stripConstraints(t));
			}
			return (T) new UnionType(types);			
		} else if(type instanceof FunType) {						
			FunType ftype = (FunType) type;
			ArrayList<Type> params = new ArrayList<Type>();
			for(Type t : ftype.parameters()) {
				params.add(stripConstraints(t));
			}
			return (T) new FunType(stripConstraints(ftype.returnType()),params);
		}
		
		return null;
	}

	/**
	 * The following simply updates the condition associated with a type.
	 * 
	 * @param t
	 * @param c
	 * @return
	 */
	 public static <T extends UnresolvedType> T recondition(T t, Condition c) {		
		if (t instanceof VoidType || t instanceof ExistentialType
				|| t instanceof NamedType || t instanceof AnyType) {
			return t;
		} else if(t instanceof BoolType) {
			return (T) Types.T_BOOL(c);
		} else if(t instanceof IntType) {
			return (T) Types.T_INT(c);
		} else if(t instanceof RealType) {
			return (T) Types.T_REAL(c);
		} else if(t instanceof ListType) {
			ListType lt = (ListType) t;
			return (T) new ListType(lt.element(),c);
		} else if(t instanceof UnresolvedListType) {
			UnresolvedListType lt = (UnresolvedListType) t;
			return (T) new UnresolvedListType(lt.element(),c);
		} else if(t instanceof SetType) {
			SetType st = (SetType) t;
			return (T) new SetType(st.element(),c);
		} else if(t instanceof UnresolvedSetType) {
			UnresolvedSetType st = (UnresolvedSetType) t;
			return (T) new UnresolvedSetType(st.element(),c);
		} else if(t instanceof ProcessType) {
			ProcessType st = (ProcessType) t;
			return (T) new ProcessType(st.element(),c);
		} else if(t instanceof UnresolvedProcessType) {
			UnresolvedProcessType st = (UnresolvedProcessType) t;
			return (T) new UnresolvedProcessType(st.element(),c);
		} else if(t instanceof RecursiveType) {
			RecursiveType st = (RecursiveType) t;
			return (T) new RecursiveType(st.name(),st.type(),c);
		} else if(t instanceof TupleType) {
			TupleType st = (TupleType) t;
			return (T) new TupleType(st.types(),c);
		} else if(t instanceof UnresolvedTupleType) {
			UnresolvedTupleType st = (UnresolvedTupleType) t;
			return (T) new UnresolvedTupleType(st.types(),c);
		} else if(t instanceof UnionType) {
			UnionType st = (UnionType) t;
			return (T) new UnionType(st.types(),c);
		} else if(t instanceof UnresolvedUnionType) {
			UnresolvedUnionType st = (UnresolvedUnionType) t;
			return (T) new UnresolvedUnionType(st.types(),c);
		} else if(t instanceof FunType) {
			FunType ft = (FunType) t;
			return (T) new FunType(ft.returnType(),ft.parameters(),c);
		} else if(t instanceof UserDefType) {				
			UserDefType udt = (UserDefType) t;
			return (T) new UserDefType(udt.name(),udt.module(),c);
		} else {
			throw new IllegalArgumentException("unknown type encountered: " + t);
		}
	}

	 public static Condition expandConstraints(Type t) {		
		 if (t instanceof VoidType || t instanceof ExistentialType
				 || t instanceof NamedType || t instanceof AnyType
				 || t instanceof BoolType || t instanceof IntType
				 || t instanceof RealType) {
			 return t.constraint();
		 } else if(t instanceof ListType) {
			 ListType lt = (ListType) t;
			 Condition c = expandConstraints(lt.element()); 				
			 if(c != null) {				
				 String vn = Variable.freshVar();								
				 Variable v = new Variable(vn,c.attribute(SourceAttr.class));
				 HashMap<String,Expr> binding = new HashMap();
				 binding.put("$",v);				
				 c = c.substitute(binding);			
				 List<Pair<String,Expr>> ss = new ArrayList();
				 ss.add(new Pair(vn, new Variable("$", c
						 .attribute(SourceAttr.class))));
				 c = new None(new SetComprehension(v,ss,new Not(c)));				
			 }
			 return and(c,lt.constraint());
		 } else if(t instanceof SetType) {
			 SetType st = (SetType) t;
			 Condition c = expandConstraints(st.element()); 				
			 if(c != null) {				
				 String vn = Variable.freshVar();								
				 Variable v = new Variable(vn,c.attribute(SourceAttr.class));
				 HashMap<String,Expr> binding = new HashMap();
				 binding.put("$",v);				
				 c = c.substitute(binding);			
				 List<Pair<String,Expr>> ss = new ArrayList();
				 ss.add(new Pair(vn, new Variable("$", c
						 .attribute(SourceAttr.class))));
				 c = new None(new SetComprehension(v,ss,new Not(c)));				
			 }
			 return and(c,st.constraint());				
		 } else if(t instanceof ProcessType) {
			 ProcessType st = (ProcessType) t;
			 return expandConstraints(st.element());
		 } else if(t instanceof RecursiveType) {
			 // FIXME: not sure what to do here
			 return null;
		 } else if(t instanceof TupleType) {
			 TupleType st = (TupleType) t;
			 Condition c = null;

			 for(Map.Entry<String,Type> e : st.types().entrySet()) {
				 String key = e.getKey();
				 Condition ec = expandConstraints(e.getValue());														
				 if(ec != null) {					
					 HashMap<String,Expr> binding = new HashMap<String,Expr>();
					 Variable v = new Variable("$", ec.attribute(SourceAttr.class));
					 binding.put("$",
							 new TupleAccess(v, key, ec.attribute(SourceAttr.class)));										
					 c = and(c, ec.substitute(binding));						
				 }					
			 }	

			 return and(st.constraint(),c);				
		 } else if(t instanceof UnionType) {
			 UnionType utt = (UnionType) t;
			 // Now, build up the condition map. The condition map is rather
			 // subtle in it's formation to ensure we get a good typing at the end.
			 HashMap<Type,Condition> conditions = new HashMap<Type,Condition>();

			 for(Type bound : utt.types()) {				
				 Condition c = expandConstraints(bound);

				 if(c != null) {
					 Condition et = conditions.get(bound);
					 if(et == null) {
						 conditions.put(bound,c);
					 } else if(!(et instanceof BoolVal)) {
						 et = new Or(et,c,c.attribute(SourceAttr.class));
						 conditions.put(bound,et);
					 }
				 } else {
					 conditions.put(bound, new BoolVal(true));
				 }
			 }			
			 
			 return mergeTypeCases(t,conditions);			 
		 } else if(t instanceof FunType) {
			 FunType ft = (FunType) t;
			 Condition post = expandConstraints(ft.returnType());
			 HashMap<String,Expr> binding = new HashMap<String,Expr>();
			 int index = 0;
			 for(Type pt : ft.parameters()) {				 
				 Condition ptc = expandConstraints(pt);
				 if(ptc != null) {
					 binding.put("$", new Variable("$" + index));
					 ptc.substitute(binding);
					 post = and(post,ptc);
				 }
			 }
			 return and(post,ft.constraint());
		 } else {
			 throw new IllegalArgumentException("unknown type encountered: " + t);
		 }
	 }

	 // The following method is pretty icky; i'm in no way 100% sure it works
	 // correctly.
	 protected static Condition mergeTypeCases(Type t,
			 HashMap<Type, Condition> conditions) {

		 if(t instanceof UnionType) {
			 // Recursive Case
			 UnionType ut = (UnionType) t;
			 Condition c = null;
			 for(Type b : ut.types()) {
				 Condition bc = mergeTypeCases(b,conditions);

				 if (bc != null) {
					 Variable v = new Variable("$", bc
							 .attribute(SourceAttr.class));
					 String var = Variable.freshVar();
					 HashMap<String, Expr> binding = new HashMap<String, Expr>();
					 binding.put("$", new Variable(var));
					 // indicates a choice of some kind required

					 bc = new TypeGate(b, var, v, bc.substitute(binding), bc
							 .attribute(SourceAttr.class));
				 }

				 if(c == null) {
					 c = bc;										
				 } else if(bc != null) {
					 c = new And(c,bc,bc.attribute(SourceAttr.class));
				 }				
			 }
			 return c;
		 } else {
			 // Base case
			 Condition c = conditions.get(t);
			 if(c instanceof BoolVal && ((BoolVal)c).value()) {
				 // useful simplification to reduce unnecssary checks
				 c = null;
			 }

			 Type lub = null;
			for (Type b : conditions.keySet()) {
				if (isBaseSubtype(t, b, Collections.EMPTY_MAP)
						&& !isBaseEquivalent(t, b)) {
					// b is a strict subtype of t
					lub = lub == null ? b : Types.leastUpperBound(b, lub);
				}
			}
			 if(lub == null) {
				 // no specific subtypes of this, so ignore
				 return c;
			 } else {
				 Condition r = mergeTypeCases(lub,conditions);
				 if(c == null) {
					 return r;
				 } else if(r == null) {
					 return c;
				 } else {
					 return new Or(r,c,c.attribute(SourceAttr.class));
				 }
			 }
		 }
	 }

	 public static Condition and(Condition c1, Condition c2) {
		 if(c1 == null) {
			 return c2;
		 } else if(c2 == null) {
			 return c1;
		 } else {
			 return new And(c1,c2);
		 }
	 }
	 
	 public static Condition or(Condition c1, Condition c2) {
		 if(c1 == null) {
			 return c2;
		 } else if(c2 == null) {
			 return c1;
		 } else {
			 return new Or(c1,c2);
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
