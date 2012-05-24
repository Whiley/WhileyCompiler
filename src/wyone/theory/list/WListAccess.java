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

import java.math.BigInteger;
import java.util.Map;

import wyone.core.*;
import wyone.theory.congruence.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;
import wyone.util.*;

public final class WListAccess extends WConstructor<WExpr> implements WExpr {
	public WListAccess(WExpr target, WExpr index) {
		super("[]",target,index);				
	}
	
	public WExpr substitute(Map<WExpr,WExpr> binding) {
		WExpr otarget = source();
		WExpr oindex = index();			
		WExpr target = otarget.substitute(binding);		
		WExpr index = oindex.substitute(binding);
		WExpr ret;
		
		
		if(index instanceof WNumber && target instanceof WListConstructor) {
			WNumber idx = (WNumber) index;
			WListConstructor c = (WListConstructor) target;
			BigInteger num = idx.numerator();			
			if (idx.isInteger() && num.compareTo(BigInteger.ZERO) >= 0
					&& num.compareTo(BigInteger.valueOf(c.subterms().size())) < 0) {
				return c.subterms().get(idx.intValue());
			} 
		} else if(index instanceof WNumber && target instanceof WListVal) {
			WNumber idx = (WNumber) index;
			WListVal c = (WListVal) target;
			BigInteger num = idx.numerator();
			if (idx.isInteger() && num.compareTo(BigInteger.ZERO) >= 0
					&& num.compareTo(BigInteger.valueOf(c.subterms().size())) < 0) {				
				return c.subterms().get(idx.intValue());
			} 
		} 
		
		if(target != otarget || index != oindex) {
			ret = new WListAccess(target,index);
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
		WType type = source().type(state);
		if(type instanceof WListType) {			
			WListType tt = (WListType) type;
			return tt.element();			
		} 
		throw new RuntimeException("Illegal list access: " + this + " (target " + type + ")");		
	}
	
	
	public WExpr source() {
		return subterms.get(0);
	}
	
	public WExpr index() {
		return subterms.get(1);
	}
	
	public String toString() {
		return source() + "[" + index() + "]";
	}
}
