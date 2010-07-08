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

package wyone.theory.logic;

import java.util.*;

import wyone.core.*;
import wyone.util.*;

public final class WConjunct extends WConstructor<WFormula> implements WFormula {	
	
	/**
	 * <p>
	 * Construct a formula from a collection of formulas.
	 * </p>
	 * 
	 * @param clauses
	 */
	public WConjunct(Set<WFormula> fs) {
		super("&&",fs);
	}			
	
	public WType type(SolverState state) {		
		return WBoolType.T_BOOL;		
	}	
	
	/**
	 * This method substitutes all variable names for names given in the
	 * binding. If no binding is given for a variable, then it retains its
	 * original name.
	 * 
	 * @param binding
	 * @return
	 */
	public WFormula substitute(Map<WExpr,WExpr> binding) {	
		HashSet<WFormula> nparams = new HashSet<WFormula>();
		boolean pchanged = false;
		boolean composite = true;
		for(WFormula p : subterms) {						
			WFormula np = p.substitute(binding);			
			composite &= np instanceof WValue;			
			if(np instanceof WConjunct) {
				WConjunct c = (WConjunct) np;
				nparams.addAll(c.subterms);
				pchanged=true;
			} else if(p==WBool.TRUE) {
				pchanged=true;
			} else if(np != p) {						
				nparams.add(np);				
				pchanged=true;	
			} else { 
				nparams.add(p);
			}
		}		
		if(composite) {			
			for(WExpr e : nparams) {				
				WBool b = (WBool) e;
				if(!b.sign()) {
					return WBool.FALSE;
				}
			}
			return WBool.TRUE;
		} else if(nparams.size() == 1) {
			return nparams.iterator().next();
		} else if(pchanged) {
			if(nparams.size() == 0) {
				return WBool.TRUE;			
			} else {
				return new WConjunct(nparams);
			}
		} else {
			return this;
		}
	}	

	public WLiteral rearrange(WExpr lhs) {
		throw new RuntimeException("Need to implement WConstructor.rearrange()!");
	}
	
	public WDisjunct not() {
		HashSet<WFormula> nparams = new HashSet<WFormula>();
		for(WFormula p : subterms) {
			WFormula np = p.not();																
			nparams.add(np);											
		}				
		return new WDisjunct(nparams);		 
	}
	
	public String toString() {
		boolean firstTime = true;
		String r = "";
		for(WFormula f : subterms) {
			if(!firstTime) {
				r += " && ";
			}
			firstTime=false;
			if(f instanceof WLiteral) {
				r += f;
			} else {
				r += "(" + f + ")";
			}
		}
		return r;
	}
}
