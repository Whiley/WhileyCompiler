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

import wyjc.ast.exprs.Condition;
import wyone.core.WType;
import wyone.theory.type.WUnionType;

public class UnionType extends ConstrainedType implements Type {
	private HashSet<NonUnionType> types;
	
	public UnionType(Collection<NonUnionType> types, Condition constraint) {
		super(constraint);
		this.types = new HashSet<NonUnionType>(types);
	}

	public UnionType(Condition constraint, NonUnionType... ts) {
		super(constraint);
		types = new HashSet<NonUnionType>();
		for(NonUnionType t : ts) {
			types.add(t);
		}
	}
	
	public Set<NonUnionType> types() {
		return types;
	}
	
	public boolean equals(Object o) {
		if(o instanceof UnionType) {
			UnionType ut = (UnionType) o;
			return ut.types.equals(types)
					&& (constraint == ut.constraint || (constraint != null && constraint
							.equals(ut.constraint)));
		}
		return false;
	}
	
	public int hashCode() {
		int hc = constraint == null ? 0 : constraint.hashCode();
		return types.hashCode() + hc;
	}
	
	public boolean isSubtype(Type t, Map<String, Type> environment) {
		if(t instanceof NamedType) {
			t = ((NamedType)t).type();
		}
		if(t instanceof UnionType) {
			UnionType ut = (UnionType) t;
			for(Type b1 : types) {
				boolean matched = false;
				for(Type b2 : ut.types) {
					if(b1.isSubtype(b2, environment)) {
						matched = true;
					}
				}
				if(!matched) {
					return false;
				}
			}
			return true;
		} else {
			for(Type b : types) {
				if(b.isSubtype(t, environment)) {
					return true;
				}
			}
			if(t instanceof ProcessType) {
				ProcessType pt = (ProcessType) t;
				return isSubtype(pt.element(), environment);
			} 
			return false;
		}
	}	
	
	public Type flattern() {
		Type t = Types.T_VOID;
		for (Type b : types) {
			t = Types.leastUpperBound(t, b);
		}
		return t;
	}
	
	public boolean isExistential() {		
		for (Type b : types) {
			if(b.isExistential()) {
				return true;
			}
		}
		return false;
	}
	
	public Type substitute(Map<String, Type> binding) {
		HashSet<NonUnionType> ts = new HashSet<NonUnionType>();
		for (NonUnionType b : types) {
			ts.add((NonUnionType) b.substitute(binding));
		}
		return new UnionType(ts, constraint);
	}
	
	public <T> Set<T> match(Class<T> type) {
		HashSet<T> r = new HashSet<T>();
		
		for(Type t : types) {
			r.addAll(t.match(type));
		}
		
		if(UnionType.class == type) {			
			r.add((T)this);			
		} 
		
		return r;		
	}
	
	public String toString() {
		String r = "";
		boolean firstTime=true;
		for(Type t : types) {
			if(!firstTime) {
				r += "|";
			}
			firstTime=false;
			r += t.toString();
		}
		return r + super.toString();
	}
	
	public WType convert() {
		HashSet<WType> ntypes = new HashSet<WType>();
		for(Type t : types) {
			ntypes.add(t.convert());
		}
		return new WUnionType(ntypes);
	}
}
