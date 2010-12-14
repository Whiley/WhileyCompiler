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

public class WUnionType implements WType {
	private final HashSet<WType> types;
	
	public WUnionType(Collection<WType> types) {
		this.types = new HashSet<WType>(types);
	}
	
	public String toString() {
		String r="";
		boolean firstTime=true;
		for(WType t : types) {
			if(!firstTime) {
				r+="|";
			}
			firstTime=false;
			r+=t.toString();
		}
		return r;
	}
	
	public boolean isSubtype(WType ot, Map<String, WType> environment) {				
		if(ot instanceof WUnionType) {
			WUnionType wut = (WUnionType) ot;
			for(WType t : wut.types) {
				if(!isSubtype(t, environment)) {
					return false;
				}
			}
			return true;
		} else {
			for(WType t : types) {
				if(t.isSubtype(ot, environment)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean equals(Object o) {
		return o instanceof WUnionType && ((WUnionType)o).types.equals(types);
	}
	
	public int hashCode() {
		return types.hashCode();
	}
}
