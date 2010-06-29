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
import wyone.core.WType;
import wyone.theory.numeric.WIntType;

public class AnyType implements NonUnionType {
	public String toString() { return "*"; }
	
	AnyType() {}
	
	public boolean equals(Object o) {
		return o == Types.T_ANY;
	}
	
	public int hashCode() {
		return 3;
	}
	
	public boolean isSubtype(Type t) {		
		return true;
	}
	
	public Type flattern() {
		return this;
	}
	
	public Type lub(Type t) {
		return this;
	}	
	
	public boolean isExistential() {
		return false;
	}
	
	public Type substitute(Map<String,String> binding) {
		return this;
	}
	
	public WType convert() {
		return WIntType.T_INT;
	}
}
