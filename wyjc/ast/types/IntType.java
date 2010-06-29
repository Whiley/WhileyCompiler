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

import wyjc.util.NameID;
import wyone.core.WType;
import wyone.theory.numeric.WIntType;


public final class IntType implements NonUnionType {
	public String toString() { return "int"; }
	
	IntType() {}
	
	public boolean equals(Object o) {
		return o instanceof IntType;
	}
	
	public int hashCode() {
		return 1;
	}
	
	public boolean isSubtype(Type t, Map<NameID,Type> environment) {
		if(t instanceof NamedType) {
			t = ((NamedType)t).type();
		} else if(t instanceof ProcessType) {
			ProcessType pt = (ProcessType) t;
			return isSubtype(pt.element(), environment);
		} 
		return t instanceof IntType || t == Types.T_VOID;
	}	
		
	public Type flattern() {
		return this;
	}	
	
	public boolean isExistential() {
		return false;
	}
	
	public Type substitute(Map<NameID,NameID> binding) {
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
