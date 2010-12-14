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

package wyone.theory.set;

import java.util.Map;

import wyone.core.WType;
import wyone.core.WValue;
import wyone.theory.list.WListType;
import wyone.theory.list.WListVal;
import wyone.theory.type.WVoidType;

public final class WSetType implements WType {
	private final WType element;
	
	public WSetType(WType element) {
		this.element = element;
	}
	
	public WType element() {
		return element;
	}
	
	public boolean isSubtype(WType o, Map<String, WType> environment) {		
		if(o instanceof WSetType) {			
			WSetType sv = (WSetType) o;			
			return element.isSubtype(sv.element, environment);
		} else {
			return o instanceof WVoidType;
		}
	}
	
	public boolean equals(Object o) {
		if(o instanceof WSetType) {
			WSetType t = (WSetType) o;
			return element.equals(t.element);
		}
		return false;
	}
	
	public int hashCode() {
		return element.hashCode();
	}
	
	public String toString() {
		return "{" + element + "}";
	}
}
