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

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import wyjc.ast.exprs.Condition;
import wyone.core.WType;
import wyone.theory.numeric.WIntType;


public final class IntType extends ConstrainedType implements NonUnionType {
	public String toString() { return "int" + super.toString(); }
	
	IntType(Condition constraint) {
		super(constraint);
	}
	
	public boolean equals(Object o) {
		if(o instanceof IntType) {
			IntType bt = (IntType) o;
			return constraint == bt.constraint
					|| (constraint != null && constraint.equals(bt.constraint));			
		}
		return false;
	}
	
	public int hashCode() {
		int hc = constraint == null ? 0 : constraint.hashCode();
		return 2 + hc;
	}	

	public Type flattern() {
		return this;
	}	
	
	public boolean isExistential() {
		return false;
	}
	
	public Type substitute(Map<String, Type> binding) {
		return this;
	}
	
	public <T> Set<T> match(Class<T> type) {
		if(IntType.class == type) {
			HashSet r = new HashSet();
			r.add(this);
			return r;
		} else {
			return Collections.EMPTY_SET;
		}
	}
	
	public WType convert() {
		return WIntType.T_INT;
	}
}
