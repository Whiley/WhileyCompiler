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

import java.util.Map;

import wyone.core.*;
import wyone.theory.congruence.*;
import wyone.theory.logic.*;
import wyone.util.*;

public final class WTupleAccess extends WConstructor<WExpr> implements WExpr {
	private final String field;
	
	public WTupleAccess(WExpr target, String field) {
		super("." + field,target);		
		this.field = field;
	}
	
	public WExpr substitute(Map<WExpr,WExpr> binding) {
		WExpr otarget = target();			
		WExpr target = otarget.substitute(binding);		
		WExpr ret;
		
		if(target instanceof WTupleConstructor) {
			WTupleConstructor c = (WTupleConstructor) target;
			ret = c.field(field);
		} else if(target instanceof WTupleVal) {
			WTupleVal c = (WTupleVal) target;
			ret = c.field(field);
		} else if(target != otarget) {
			ret = new WTupleAccess(target,field);
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
		WType type = target().type(state);
		if(type instanceof WTupleType) {			
			WTupleType tt = (WTupleType) type;
			WType ft = tt.get(field);
			if(ft != null) {				
				return ft;
			}			
		} 
		throw new RuntimeException("Illegal tuple access: " + this + " (target " + type + ")");		
	}
	
	
	public WExpr target() {
		return subterms.get(0);
	}
	
	public String field() {
		return field;
	}
	
	public String toString() {
		return target() + "." + field;
	}
}
