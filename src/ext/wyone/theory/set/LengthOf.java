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

import java.math.BigInteger;
import java.util.Map;

import wyone.core.*;

public class LengthOf extends Constructor.Base<Constructor> implements Constructor {
	public LengthOf(Constructor source) {
		super("|..|", source);
	}

	public Constructor source() {
		return subterms().get(0);
	}
	
	public String toString() {		
		return "|" + source() + "|";		
	}
	
	public Constructor substitute(Map<Constructor,Constructor> binding) {
		Constructor osource = source();				
		Constructor source = osource.substitute(binding);				
		Constructor ret;
				
		if(source instanceof Value.Set) {
			Value.Set c = (Value.Set) source;			
			return Value.V_NUM(BigInteger.valueOf(c.subterms().size()));
		} else if(source instanceof SetConstructor) {			
			SetConstructor c = (SetConstructor) source;
			int size = c.subterms().size();
			if(size == 0 || size == 1) {
				// in this case, we can be more definite
				return Value.V_NUM(BigInteger.valueOf(size));
			} 
		} 
		
		if(source != osource) {			
			ret = new LengthOf(source);
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
}
