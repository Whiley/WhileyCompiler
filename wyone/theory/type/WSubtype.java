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

import wyone.core.*;
import wyone.theory.logic.*;
import wyone.util.WConstructor;

public class WSubtype extends WConstructor<WExpr> implements WLiteral {
	private boolean sign;
	private WType type;

	/**
	 * Construct a (non-strict) subset relationship between lhs and rhs. If
	 * sign, then we have |lhs| <= |rhs| (i.e. all of lhs is in rhs, and they
	 * may be equal), otherwise there is an element of lhs which is not in rhs.
	 * 
	 * @param sign
	 * @param lhs
	 * @param rhs
	 */
	public WSubtype(boolean sign, WExpr lhs, WType rhs) {
		super(sign ? ("<:"+rhs) : ("<!:"+rhs),lhs);
		this.sign = sign;
		this.type = rhs;
	}
	
	public boolean sign() {
		return sign;
	}
	
	public WLiteral not() {
		return new WSubtype(!sign,lhs(),type);
	}
	
	public WBoolType type(SolverState state) {
		return WBoolType.T_BOOL;
	}
	
	public WExpr lhs() {
		return subterms.get(0);
	}

	public WType rhs() {
		return type;
	}
	
	public WLiteral rearrange(WExpr rhs) {
		// no idea what to do here ...
		throw new RuntimeException("Not sure how to rearrange type tests!");
	}
	
	public WLiteral substitute(Map<WExpr,WExpr> binding) {
		WExpr lhs = lhs();		
		WExpr nlhs = lhs.substitute(binding);
		
		// need to check the type here!
		if(nlhs instanceof WValue) {
			WValue v = (WValue) nlhs;
			if(type.isSubtype(v.type(null), Collections.EMPTY_MAP)) {
				return sign ? WBool.TRUE : WBool.FALSE;
			} else {
				return sign ? WBool.FALSE : WBool.TRUE;				
			}
		}
		
		WLiteral r;
		
		if(lhs != nlhs) {
			r = new WSubtype(sign,nlhs,type); 			
		} else {
			r = this;
		}
		
		WLiteral tmp = (WLiteral) binding.get(r);
		return tmp != null ? tmp : r;
	}
	
	public String toString() {		
		if(sign) {
			return lhs().toString() + "<:" + type;			
		} else {
			return lhs().toString() + "<!:" + type;			
		}		
	}
}
