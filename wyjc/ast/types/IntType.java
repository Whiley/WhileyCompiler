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
	
	public boolean isSubtype(Type t) {
		if(t instanceof NamedType) {
			t = ((NamedType)t).type();
		} else if(t instanceof ProcessType) {
			ProcessType pt = (ProcessType) t;
			return isSubtype(pt.element());
		} 
		return t instanceof IntType || t == Types.T_VOID;
	}	
		
	public Type flattern() {
		return this;
	}	
	
	public boolean isExistential() {
		return false;
	}
	
	public WType convert() {
		return WIntType.T_INT;
	}
}
