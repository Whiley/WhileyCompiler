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

package wyone.theory.list;

import java.util.Map;

import wyone.core.WType;
import wyone.core.WValue;
import wyone.theory.set.WSetType;
import wyone.theory.set.WSetVal;
import wyone.theory.type.WVoidType;

public final class WListType implements WType {
	private final WType element;
	
	public WListType(WType element) {
		this.element = element;
	}
	
	public WType element() {
		return element;
	}
	
	public boolean isSubtype(WType o, Map<String, WType> environment) {
		if(o instanceof WListType) {			
			WListType sv = (WListType) o;			
			return element.isSubtype(sv.element, environment);
		} else {
			return  o instanceof WVoidType;
		}
	}
	
	public boolean equals(Object o) {
		if(o instanceof WListType) {
			WListType t = (WListType) o;
			return element.equals(t.element);
		}
		return false;
	}
	
	public int hashCode() {
		return element.hashCode();
	}
	
	public String toString() {
		return "[" + element + "]";
	}
}
