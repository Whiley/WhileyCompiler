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
	
	public boolean isSubtype(WValue val) {
		if(val instanceof WTupleVal) {
			WTupleVal wt = (WTupleVal) val;
			if(wt.subterms().size() != types.size()) {
				return false;
			}
			List<String> wt_fields = wt.fields();
			List<WValue> wt_params = wt.subterms();
			for(int i=0;i!=types.size();++i) {
				String field = wt_fields.get(i);
				WValue v = wt_params.get(i);
				Pair<String,WType> p = types.get(i);
				if (!p.first().equals(field) || !p.second().isSubtype(v)) {
					return false;
				}				
			}
			
			return true;
		}
		return false;
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
