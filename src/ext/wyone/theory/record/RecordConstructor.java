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

package wyone.theory.record;

import java.util.*;

import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.congruence.*;
import wyone.util.*;

public final class RecordConstructor extends Constructor.Base<Constructor> {
	private final ArrayList<String> fields;

	public RecordConstructor(Collection<String> fields, Collection<Constructor> params) {
		super("()", params);
		this.fields = new ArrayList<String>(fields);
	}
	
	public Constructor field(String name) {
		for(int i=0;i!=fields.size();++i) {
			if(fields.get(i).equals(name)) {
				return subterms.get(i);
			}
		}
		throw new IllegalArgumentException("field does not exist: " + name);
	}
	
	public Constructor substitute(Map<Constructor,Constructor> binding) {
		ArrayList nparams = new ArrayList();
		boolean pchanged = false;
		boolean composite = true;		
		for(Constructor p : subterms) {
			Constructor np = p.substitute(binding);
			composite &= np instanceof Value;			
			if(np != p) {				
				nparams.add(np);
				pchanged=true;				
			} else {
				nparams.add(p);
			}			
		}
		if(composite) {
			HashMap<String,wyil.lang.Value> values = new HashMap<String,wyil.lang.Value>();
			for(int i=0;i!=subterms.size();++i) {
				values.put(fields.get(i), ((Value)nparams.get(i)).value);
			}
			return Value.V_RECORD(values);
		} else if(pchanged) {
			return new RecordConstructor(fields,nparams);
		} else {
			return this;
		}
	}
	
	public String toString() {
		String r = "(";
		boolean firstTime=true;
		for(int i=0;i!=subterms.size();++i) {		
			if(!firstTime) {
				r += ",";
			}
			firstTime=false;
			r += fields.get(i) + "=";
			r += subterms.get(i).toString();
		}
		return r + ")";
	}
}
