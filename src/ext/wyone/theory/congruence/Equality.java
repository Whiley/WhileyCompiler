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
import wyil.lang.Type;
import static wyone.core.Constructor.*;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.util.*;

// equalities and inequalities should always be normalised.
public final class Equality extends Base<Constructor> implements Constraint {
	private boolean sign;
	/**
	 * Construct an inequality from left and right rationals. So, this generates
	 * 0==rhs-lhs if sign, otherwise 0!=rhs-lhs
	 * 
	 * @param sign
	 * @param lhs --- left-hand side
	 * @param rhs --- right-hand side
	 */
	public Equality(boolean sign, Constructor lhs, Constructor rhs) {
		super(sign ? "==" : "!=",lhs,rhs);
		this.sign = sign;
	}
	
	public boolean sign() {
		return sign;
	}
	
	public Constructor lhs() {
		return subterms.get(0);
	}
	
	public Constructor rhs() {
		return subterms.get(1);
	}
	
	public Type type(Solver.State state) {
		return Type.T_BOOL;
	}
	
	public Equality not() {
		return new Equality(!sign,lhs(),rhs());
	}
	
	public Constraint substitute(Map<Constructor, Constructor> binding) {				
		Constructor olhs = subterms.get(0);
		Constructor orhs = subterms.get(1);
		Constructor lhs = olhs.substitute(binding);
		Constructor rhs = orhs.substitute(binding);		
		if(lhs instanceof Value && rhs instanceof Value) {						
			Value lhsv = (Value) lhs;
			Value.Bool b = lhsv.equals(rhs) ? Value.TRUE : Value.FALSE;
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
				return new Equality(false,lhs,rhs);
			}
		}
	}		
	
	public Constraint rearrange(Constructor rhs) {
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
	
	public static Constraint equals(Constructor lhs, Constructor rhs) {
		return new Equality(true,lhs,rhs).substitute(Collections.EMPTY_MAP);
	}
	
	public static Constraint notEquals(Constructor lhs, Constructor rhs) {
		return new Equality(false,lhs,rhs).substitute(Collections.EMPTY_MAP);
	}
}
