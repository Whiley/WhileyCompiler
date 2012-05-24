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

import wyone.util.*;
import wyone.core.*;
import wyone.theory.congruence.WEquality;
import wyone.theory.logic.*;
import wyone.theory.numeric.WIntType;
import wyone.theory.numeric.WNumber;
import wyone.theory.set.*;

public class WLengthOf extends WConstructor<WExpr> implements WExpr {
	public WLengthOf(WExpr source) {
		super("|..|", source);
	}

	public WExpr source() {
		return subterms().get(0);
	}
	
	public String toString() {		
		return "|" + source() + "|";		
	}
	
	public WExpr substitute(Map<WExpr,WExpr> binding) {
		WExpr osource = source();				
		WExpr source = osource.substitute(binding);				
		WExpr ret;
		
		if(source instanceof WListConstructor) {			
			WListConstructor c = (WListConstructor) source;				
			return new WNumber(c.subterms().size());
		} else if(source instanceof WListVal) {
			WListVal c = (WListVal) source;			
			return new WNumber(c.subterms().size());
		} else if(source instanceof WSetVal) {
			WSetVal c = (WSetVal) source;			
			return new WNumber(c.subterms().size());
		} else if(source instanceof WSetConstructor) {
			WSetConstructor c = (WSetConstructor) source;
			int size = c.subterms().size();
			if(size == 0 || size == 1) {
				// in this case, we can be more definite
				return new WNumber(size);
			} else {
				ret = new WLengthOf(source);
			}
		} else if(source != osource) {
			ret = new WLengthOf(source);
		} else {
			ret = this;
		}
		
		WExpr r = binding.get(ret);
		if(r != null) { 
			return r;
		} else {		
			return ret;
		}
	}
	
	public WLiteral rearrange(WExpr e) {
		// No idea what i'm supposed to do here. 
		return new WEquality(true,this,e);
	}
		
	public WType type(SolverState state) {
		return WIntType.T_INT;
	}
}
