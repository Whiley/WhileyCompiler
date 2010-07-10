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
import wyjc.ast.exprs.Condition;
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
		
		if(t1.isSubtype(t2, Collections.EMPTY_MAP)) {
			return t1;
		} else if(t2.isSubtype(t1, Collections.EMPTY_MAP)) {
			return t2;
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
				if(t2.isSubtype(t, Collections.EMPTY_MAP)) {
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
				if(t1.isSubtype(t, Collections.EMPTY_MAP)) {
					types.set(i, (NonUnionType) t1);			
					return new UnionType(types);
				}
			}					
			types.add((NonUnionType) t1);			
			return new UnionType(types);			
		} else if(t1 instanceof ListType && t2 instanceof ListType) {
			ListType l1 = (ListType) t1;
			ListType l2 = (ListType) t2;
			return new ListType(leastUpperBound(l1.element(),l2.element()));
		} else if(t1 instanceof SetType && t2 instanceof SetType) {
			SetType s1 = (SetType) t1;
			SetType s2 = (SetType) t2;
			return new SetType(leastUpperBound(s1.element(),s2.element()));
		} else {
			return new UnionType((NonUnionType) t1, (NonUnionType) t2);
		}
	}
	
	public static Type greatestLowerBound(Type t1, Type t2) {
		
		// NOTE. There are still bugs in this algorithm.
		
		if(t1.isSubtype(t2, Collections.EMPTY_MAP)) {									
			return t1;
		} else if(t2.isSubtype(t1, Collections.EMPTY_MAP)) {			
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
