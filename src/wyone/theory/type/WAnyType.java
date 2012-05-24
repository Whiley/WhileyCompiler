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
 * The AnyType is used to declare variables whose type is unknown. As the
 * theorem prover searches for a model, it may happen that on certain paths a
 * distinct type is indeed known. This is used to model the union types that are
 * found in Whiley.
 */
public class WAnyType implements WType {
	WAnyType() {}
	
	public static final WAnyType T_ANY = new WAnyType(); 
	
	public String toString() {
		return "?";
	}
	
	public boolean isSubtype(WType o, Map<String, WType> environment) {				
		return true;
	}
	
	public boolean equals(Object o) {
		return o instanceof WAnyType;
	}
	
	public int hashCode() {
		return 123;
	}
}
