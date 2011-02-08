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
import wyone.core.Constructor.Base;
import wyone.theory.logic.*;

public class SubsetEq extends Base<Constructor> implements Constraint {
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
	public SubsetEq(boolean sign, Constructor lhs, Constructor rhs) {
		super(sign ? "{=" : "!{=",lhs,rhs);
		this.sign = sign;
	}
	
	public boolean sign() {
		return sign;
	}
	
	public SubsetEq not() {
		return new SubsetEq(!sign,lhs(),rhs());
	}
	
	public Constructor lhs() {
		return subterms.get(0);
	}

	public Constructor rhs() {
		return subterms.get(1);
	}
	
	public Constraint substitute(Map<Constructor,Constructor> binding) {
		Constructor lhs = lhs();
		Constructor rhs = rhs();
		Constructor nlhs = lhs.substitute(binding);
		Constructor nrhs = rhs.substitute(binding);
		
		List<? extends Constructor> nlhsElems = null;		
		List<? extends Constructor> nrhsElems = null;
		
		if(nlhs instanceof SetConstructor || nlhs instanceof Value.Set) {			
			nlhsElems = lhs.subterms();	
		} 
		
		if(nrhs instanceof SetConstructor || nrhs instanceof Value.Set) {			
			nrhsElems = rhs.subterms();	
		} 	
		
		
		if(nlhs instanceof Value.Set && nrhs instanceof Value.Set) {
			// here, we can compute a final value.
			Value vlhs = (Value) nlhs;
			Value vrhs = (Value) nrhs;
			boolean r = vrhs.subterms().containsAll(vlhs.subterms());			
			if(sign) {
				return r ? Value.TRUE : Value.FALSE;
			} else {
				return r ? Value.FALSE : Value.TRUE;
			}			
		} else if(nrhsElems != null && nlhsElems != null && nrhsElems.containsAll(nlhsElems)) {
			if(sign) {
				return Value.TRUE;
			} else {
				return Value.FALSE;
			}
		} 
		
		Constraint r;
		
		if(lhs != nlhs || rhs != nrhs) {
			r = new SubsetEq(sign,nlhs,nrhs); 			
		} else {
			r = this;
		}
		
		Constraint tmp = (Constraint) binding.get(r);
		return tmp != null ? tmp : r;
	}
	
	public String toString() {		
		if(sign) {
			return lhs().toString() + "{=" + rhs().toString();			
		} else {
			return lhs().toString() + "{!=" + rhs().toString();			
		}
	}
	
	public static Constraint subsetEq(Constructor lhs, Constructor rhs) {
		return new SubsetEq(true,lhs,rhs);
	}

	public static Constraint subset(Constructor lhs, Constructor rhs) {
		
		if(rhs instanceof SetConstructor || rhs instanceof Value.Set) {
			// This is a useful optimisation case.
			Constraint r = Value.FALSE;
			for(Constructor e : rhs.subterms()) {
				HashSet elems = new HashSet(rhs.subterms());
				elems.remove(e);					
				if(rhs instanceof SetConstructor) {
					e = new SetConstructor(elems);
				} else {
					e = Value.V_SET(elems);
				}
				r = Logic.or(r,subsetEq(lhs,e));
			}
			return r;			
		} 
		
		return Logic.and(new SubsetEq(true, lhs, rhs), Equality.notEquals(lhs, rhs));
	}
	
	public static Constraint supsetEq(Constructor lhs, Constructor rhs) {
		return new SubsetEq(true,rhs,lhs);
	}
	
	public static Constraint elementOf(Constructor lhs, Constructor rhs) {		
		return new SubsetEq(true,new SetConstructor(lhs),rhs);
	}	

}
