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

import java.util.Map;

import wyone.core.*;
import wyone.theory.congruence.*;
import wyone.theory.logic.*;
import wyone.util.*;

public final class RecordAccess extends Constructor.Base<Constructor> {
	private final String field;
	
	public RecordAccess(Constructor target, String field) {
		super("." + field,target);		
		this.field = field;
	}
	
	public Constructor substitute(Map<Constructor,Constructor> binding) {
		Constructor otarget = target();			
		Constructor target = otarget.substitute(binding);		
		Constructor ret;
		
		if(target instanceof RecordConstructor) {
			RecordConstructor c = (RecordConstructor) target;
			ret = c.field(field);
		} else if(target instanceof Value.Record) {
			Value.Record c = (Value.Record) target;
			ret = c.field(field);
		} else if(target != otarget) {
			ret = new RecordAccess(target,field);
		} else {
			ret = this;
		}
		
		Constructor r = binding.get(ret);
		if(r != null) { 
			return r;
		} else {		
			return ret;
		}
	}
			
	public Constructor target() {
		return subterms.get(0);
	}
	
	public String field() {
		return field;
	}
	
	public String toString() {
		return target() + "." + field;
	}
}
