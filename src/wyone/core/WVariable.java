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

package wyone.core;

import java.util.*;

import wyone.theory.congruence.*;
import wyone.theory.logic.*;
import wyone.theory.type.WTypes;
import wyone.util.WConstructor;

/**
 * <p>
 * A variable is a component of a Wyone expression for which we need to infer a
 * value. Effectively, a variable is an <i>uninterpreted function</i> which acts
 * as a barrier between its parameters and return value. Thus, even if we have
 * concrete values for its parameters, we cannot immediately infer what its
 * return value should be.
 * </p>
 * 
 * <p>
 * Variables differ from other Wyone expressions, which can be thought of as
 * <i>interpreted functions</i>. In such case, we can determine their return
 * value immediately once we have concrete values for their parameters.
 * </p>
 * 
 * <p>
 * Finally, a <i>model</i> is an assignment of (concrete) wone variables to
 * values. A <i>concrete wone</i> variable is one whose parameters are either
 * themselves concrete variables, or are values. Thus, a model is all that is
 * required to reduce a given wone formula to a true or false value.
 * </p>
 * 
 * @author djp
 * 
 */
public class WVariable extends WConstructor<WExpr> implements WExpr {
	
	private final static int CID = WExprs.registerCID(); 
	
	public WVariable(String var, WExpr... args) {
		super(var,args);
	}
	
	public WVariable(String var, Collection<WExpr> args) {
		super(var,args);
	}
			
	public int cid() { return CID; }
	
	public boolean isConcrete() {
		for(WExpr p : subterms) {
			if(!(p instanceof WValue)) {
				return false;
			}
		}
		return true;
	}
	
	public WExpr substitute(Map<WExpr,WExpr> binding) {		
		// First, recursively check for substitutions
		ArrayList<WExpr> nparams = new ArrayList<WExpr>();
		boolean pchanged = false;		
		for(WExpr p : subterms) {
			WExpr np = p.substitute(binding);				
			if(np != p) {				
				nparams.add(np);
				pchanged=true;				
			} else {
				nparams.add(p);
			}
		}
		
		// Second, check whether I am being substituted 
		
		WExpr r = pchanged ? new WVariable(name,nparams) : this;
		WExpr d = binding.get(r);
		if(d != null) { return d; }
		else return r;
	}
	
	public WLiteral rearrange(WExpr lhs) {
		if(lhs instanceof WVariable) {			
			if(this.equals(lhs)) {
				return WBool.TRUE;
			} else {
				// NOTE. We have to be very careful here not to disturb the
				// ordering that may have been set by previous assignments.
				return new WEquality(true,this,lhs);
			} 					
		} else if(lhs instanceof WValue) {
			// Here, we have an assignment
			return new WEquality(true,this,lhs);
		} else {
			// Using double dispatch here is sneaky, but it does ensure that
			// more complex forms of expression get the opportunity to
			// rearrange, In particular, if the lhs was a rational of some kind
			// that reference this variable, then we want to take that into
			// account.  e.g. x-1 == x => -1 = 0 => false
			return lhs.rearrange(this);
		}
	}
	
	public WType type(SolverState state) {
		return WTypes.type(this,state);		
	}
	
	public String toString() {
		if(subterms.size() == 0) {
			return name;
		} else{
			return super.toString();
		}
	}
		
	// =================================================================
	// HELPER METHODS
	// =================================================================
	
	private static int fvidx = 0;
	public static WVariable freshVar() {
		return new WVariable("$" + fvidx++);
	}
}
