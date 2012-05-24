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

package wyone.theory.congruence;

import java.util.*;

import wyone.core.*;
import wyone.theory.logic.*;
import wyone.util.*;

// equalities and inequalities should always be normalised.
public final class WEquality extends WConstructor<WExpr> implements WLiteral {
	private boolean sign;
	/**
	 * Construct an inequality from left and right rationals. So, this generates
	 * 0==rhs-lhs if sign, otherwise 0!=rhs-lhs
	 * 
	 * @param sign
	 * @param lhs --- left-hand side
	 * @param rhs --- right-hand side
	 */
	public WEquality(boolean sign, WExpr lhs, WExpr rhs) {
		super(sign ? "==" : "!=",lhs,rhs);
		this.sign = sign;
	}
	
	public boolean sign() {
		return sign;
	}
	
	public WExpr lhs() {
		return subterms.get(0);
	}
	
	public WExpr rhs() {
		return subterms.get(1);
	}
	
	public WType type(SolverState state) {
		return WBoolType.T_BOOL;
	}
	
	public WEquality not() {
		return new WEquality(!sign,lhs(),rhs());
	}
	
	public WLiteral substitute(Map<WExpr, WExpr> binding) {				
		WExpr olhs = subterms.get(0);
		WExpr orhs = subterms.get(1);
		WExpr lhs = olhs.substitute(binding);
		WExpr rhs = orhs.substitute(binding);		
		if(lhs instanceof WValue && rhs instanceof WValue) {						
			WValue lhsv = (WValue) lhs;
			WBool b = lhsv.equals(rhs) ? WBool.TRUE : WBool.FALSE;
			if(sign) {
				return b;
			} else {
				return b.not();
			}
		} else if(olhs == lhs && orhs == rhs) {			
			// to ensure substitute contract is met
			return this;
		} else {			
			if(sign) {			
				return lhs.rearrange(rhs);
			} else {
				return new WEquality(false,lhs,rhs);
			}
		}
	}		
	
	public WLiteral rearrange(WExpr rhs) {
		// no idea what to do here ...
		throw new RuntimeException("Not sure how to rearrange equality!");
	}
	
	public String toString() {
		if(sign) {
			return lhs() + "==" + rhs();
		} else {
			return lhs() + "!=" + rhs();
		}
	}
}
