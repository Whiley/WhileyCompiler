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

import static wyone.core.Constructor.*;
import wyil.lang.Type;
import wyone.core.*;
import wyone.util.*;

/**
 * This represents an inequality of the form lhs <= rhs
 * 
 * @author djp
 * 
 */
public final class WInequality extends Base<Constructor> implements Constraint {		
	private boolean sign;
	/**
	 * Construct an inequality from left and right rationals. So, this generates
	 * lhs<=rhs.
	 * 
	 * @param sign 
	 * @param rhs --- right-hand side (lhs is zero)
	 */
	WInequality(boolean sign, Constructor rhs) {	
		super(sign ? "<=" : ">",rhs);
		this.sign = sign;
	}
	
	public boolean sign() {
		return sign;
	}
	
	public Constructor rhs() {
		return subterms.get(0);
	}
	
	public Type type(Solver.State state) {
		return Type.T_BOOL;
	}
	
	public WInequality not() {
		return new WInequality(!sign,rhs());
	}
	
	public Constraint substitute(Map<Constructor, Constructor> binding) {
		Constructor orhs = subterms.get(0);
		Constructor rhs = orhs.substitute(binding);		
		
		if (rhs instanceof Value.Number) {			
			Value.Number nrhs = (Value.Number) rhs;
			int nc = Value.ZERO.compareTo(nrhs);
			if (sign) {
				return nc <= 0 ? Value.TRUE : Value.FALSE;
			} else {
				return nc > 0 ? Value.TRUE : Value.FALSE;
			}
		} else if(rhs != orhs){
			return new WInequality(sign, rhs);
		} else {
			return this;
		}
	}
	
	public String toString() {
		if(sign) {
			return "0 <= " + rhs();
		} else {
			return "0 > " + rhs();
		}
	}
}
