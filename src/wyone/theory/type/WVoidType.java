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

package wyone.theory.type;

import java.util.Map;

import wyone.core.WType;

/**
 * The VoidType is used to for situations when there is no possible type. For
 * example, the element type of an empty set is void. The VoidType is a subtype
 * of everything.
 */
public class WVoidType implements WType {
	WVoidType() {}
	
	public static final WVoidType T_VOID = new WVoidType(); 
	
	public String toString() {
		return "void";
	}
	
	public boolean isSubtype(WType o, Map<String, WType> environment) {				
		return false;
	}
	
	public boolean equals(Object o) {
		return o instanceof WVoidType;
	}
	
	public int hashCode() {
		return 123;
	}
}
