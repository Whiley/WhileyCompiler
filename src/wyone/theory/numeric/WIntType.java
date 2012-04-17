// This file is part of the Wyone automated theorem prover.
//
// Wyone is free software; you can redistribute it and/or modify 
// it under the terms of the GNU General Public License as published 
// by the Free Software Foundation; either version 3 of the License, 
// or (at your option) any later version.
//
// Wyone is distributed in the hope that it will be useful, but 
// WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See 
// the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with Wyone. If not, see <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyone.theory.numeric;

import java.util.Map;

import wyone.core.WType;
import wyone.core.WValue;
import wyone.theory.logic.WBoolType;
import wyone.theory.type.WVoidType;

public class WIntType implements WType {
	WIntType() {}
	
	public static final WIntType T_INT = new WIntType(); 
	
	public String toString() {
		return "int";
	}
	
	public boolean isSubtype(WType o, Map<String, WType> environment) {				
		return o instanceof WIntType || o instanceof WVoidType;
	}
	
	public boolean equals(Object o) {
		return o instanceof WIntType;
	}
	
	public int hashCode() {
		return 1;
	}
}
