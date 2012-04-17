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

package wyone.theory.numeric;

import java.util.*;

import wyone.core.*;
import wyone.theory.logic.*;
import wyone.util.*;

/**
 * This represents an inequality of the form lhs <= rhs
 * 
 * @author djp
 * 
 */
public final class WInequality extends WConstructor<WExpr> implements WLiteral {		
	private boolean sign;
	/**
	 * Construct an inequality from left and right rationals. So, this generates
	 * lhs<=rhs.
	 * 
	 * @param sign 
	 * @param rhs --- right-hand side (lhs is zero)
	 */
	WInequality(boolean sign, WExpr rhs) {	
		super(sign ? "<=" : ">",rhs);
		this.sign = sign;
	}
	
	public boolean sign() {
		return sign;
	}
	
	public WExpr rhs() {
		return subterms.get(0);
	}
	
	public WType type(SolverState state) {
		return WBoolType.T_BOOL;
	}
	
	public WInequality not() {
		return new WInequality(!sign,rhs());
	}
	
	public WLiteral substitute(Map<WExpr, WExpr> binding) {
		WExpr orhs = subterms.get(0);
		WExpr rhs = orhs.substitute(binding);		
		
		if (rhs instanceof WNumber) {			
			WNumber nrhs = (WNumber) rhs;
			int nc = WNumber.ZERO.compareTo(nrhs);
			if (sign) {
				return nc <= 0 ? WBool.TRUE : WBool.FALSE;
			} else {
				return nc > 0 ? WBool.TRUE : WBool.FALSE;
			}
		} else if(rhs != orhs){
			return new WInequality(sign, rhs);
		} else {
			return this;
		}
	}
	
	public WLiteral rearrange(WExpr rhs) {
		// no idea what to do here ...
		throw new RuntimeException("Not sure how to rearrange equality!");
	}
	
	public String toString() {
		if(sign) {
			return "0 <= " + rhs();
		} else {
			return "0 > " + rhs();
		}
	}
}
