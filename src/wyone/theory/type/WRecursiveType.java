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

import java.util.*;

import wyone.core.WType;

public class WRecursiveType implements WType {
	private final String name;
	private final WType subtype;
	
	public WRecursiveType(String name, WType subtype) {
		this.subtype = subtype;
		this.name = name;
	}
	
	public String toString() {
		return name +"[" + subtype + "]";		
	}
	
	public boolean isSubtype(WType ot, Map<String, WType> environment) {
		if (this.equals(ot)) {
			return true;
		} else if (subtype == null) {
			// leaf of recursive type
			return environment.get(name).isSubtype(ot, environment);
		} else {
			environment = new HashMap<String, WType>();
			environment.put(name, this);
			return subtype.isSubtype(ot, environment);
		}
	}
	
	public boolean equals(Object o) {
		if(o instanceof WRecursiveType) {
			WRecursiveType rt = (WRecursiveType) o;
			// FIXME: this is broken, since it should be oblivious to the actual
			// name used. That is, recursive types which are isomorphic should
			// be considered equal.
			return name.equals(rt.name) && subtype.equals(rt.subtype);
		}
		return false;
	}
	
	public int hashCode() {
		if(subtype != null) {
			return name.hashCode() + subtype.hashCode();
		} else {
			return name.hashCode();
		}
	}
}
