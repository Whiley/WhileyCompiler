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

package wyone.theory.tuple;

import java.util.*;

import wyone.core.*;
import wyone.theory.tuple.*;
import wyone.theory.type.WVoidType;
import wyone.util.*;

public class WTupleType implements WType {
	private List<Pair<String,WType>> types;
	
	public WTupleType(List<Pair<String,WType>> types) {
		this.types = new ArrayList<Pair<String,WType>>(types);			
	}
	
	public Iterator<Pair<String,WType>> iterator() {
		return types.iterator();
	}
	
	public List<Pair<String,WType>> types() {
		return types;
	}
	
	public WType get(String key) {
		for(Pair<String,WType> p : types) {
			if(p.first().equals(key)) {
				return p.second();
			}
		}
		return null;
	}
	
	public boolean isSubtype(WType val, Map<String, WType> environment) {
		if(val instanceof WTupleType) {
			WTupleType wt = (WTupleType) val;
			if(wt.types.size() != types.size()) {
				return false;
			}
			List<Pair<String,WType>> w_types = wt.types;			
			for(int i=0;i!=types.size();++i) {
				String field = w_types.get(i).first();
				WType t = w_types.get(i).second();
				Pair<String,WType> p = types.get(i);
				if (!p.first().equals(field)
						|| !p.second().isSubtype(t, environment)) {
					return false;
				}				
			}
			
			return true;
		}
		return val instanceof WVoidType;
	}
	
	public boolean equals(Object o) {
		if(o instanceof WTupleType) {
			WTupleType t = (WTupleType) o;
			return types.equals(t.types);
		}
		return false;
	}
	
	public String toString() {
		String r = "(";
		boolean firstTime = true;

		for(Pair<String,WType> p : types) {
			if (!firstTime) {
				r = r + ",";
			}
			firstTime = false;
			r = r + p.second() + " " + p.first();
		}
		return r + ")";
	}
	
	public int hashCode() {		
		return types.hashCode();
	}	
}
