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

import java.util.*;

import wyone.core.*;
import wyone.theory.logic.*;
import wyone.util.WConstructor;

public class WSubsetEq extends WConstructor<WExpr> implements WLiteral {
	private boolean sign;

	/**
	 * Construct a (non-strict) subset relationship between lhs and rhs. If
	 * sign, then we have |lhs| <= |rhs| (i.e. all of lhs is in rhs, and they
	 * may be equal), otherwise there is an element of lhs which is not in rhs.
	 * 
	 * @param sign
	 * @param lhs
	 * @param rhs
	 */
	public WSubsetEq(boolean sign, WExpr lhs, WExpr rhs) {
		super(sign ? "{=" : "!{=",lhs,rhs);
		this.sign = sign;
	}
	
	public boolean sign() {
		return sign;
	}
	
	public WLiteral not() {
		return new WSubsetEq(!sign,lhs(),rhs());
	}
	
	public WBoolType type(SolverState state) {
		return WBoolType.T_BOOL;
	}
	
	public WExpr lhs() {
		return subterms.get(0);
	}

	public WExpr rhs() {
		return subterms.get(1);
	}
	
	public WLiteral rearrange(WExpr rhs) {
		// no idea what to do here ...
		throw new RuntimeException("Not sure how to rearrange subset!");
	}
	
	public WLiteral substitute(Map<WExpr,WExpr> binding) {
		WExpr lhs = lhs();
		WExpr rhs = rhs();
		WExpr nlhs = lhs.substitute(binding);
		WExpr nrhs = rhs.substitute(binding);
		
		List<? extends WExpr> nlhsElems = null;		
		List<? extends WExpr> nrhsElems = null;
		
		if(nlhs instanceof WSetConstructor || nlhs instanceof WSetVal) {			
			nlhsElems = lhs.subterms();	
		} 
		
		if(nrhs instanceof WSetConstructor || nrhs instanceof WSetVal) {			
			nrhsElems = rhs.subterms();	
		} 	
		
		
		if(nlhs instanceof WSetVal && nrhs instanceof WSetVal) {
			// here, we can compute a final value.
			WValue vlhs = (WValue) nlhs;
			WValue vrhs = (WValue) nrhs;
			boolean r = vrhs.subterms().containsAll(vlhs.subterms());			
			if(sign) {
				return r ? WBool.TRUE : WBool.FALSE;
			} else {
				return r ? WBool.FALSE : WBool.TRUE;
			}			
		} else if(nrhsElems != null && nlhsElems != null && nrhsElems.containsAll(nlhsElems)) {
			if(sign) {
				return WBool.TRUE;
			} else {
				return WBool.FALSE;
			}
		} 
		
		WLiteral r;
		
		if(lhs != nlhs || rhs != nrhs) {
			r = new WSubsetEq(sign,nlhs,nrhs); 			
		} else {
			r = this;
		}
		
		WLiteral tmp = (WLiteral) binding.get(r);
		return tmp != null ? tmp : r;
	}
	
	public String toString() {		
		if(sign) {
			return lhs().toString() + "{=" + rhs().toString();			
		} else {
			return lhs().toString() + "{!=" + rhs().toString();			
		}
	}
}
