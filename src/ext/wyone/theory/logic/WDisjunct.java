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

import wyil.lang.Type;
import wyone.core.*;
import wyone.util.*;

public final class WDisjunct extends WConstructor<WConstraint> implements WConstraint {		
	/**
	 * <p>
	 * Construct a formula from a collection of formulas.
	 * </p>
	 * 
	 * @param clauses
	 */
	public WDisjunct(Set<WConstraint> fs) {
		super("||",fs);		
	}

	// =================================================================
	// REQUIRED METHODS
	// =================================================================

	public Type type(SolverState state) {		
		return Type.T_BOOL;		
	}	
	
	/**
	 * This method substitutes all variable names for names given in the
	 * binding. If no binding is given for a variable, then it retains its
	 * original name.
	 * 
	 * @param binding
	 * @return
	 */
	public WConstraint substitute(Map<WExpr,WExpr> binding) {	
		HashSet<WConstraint> nparams = new HashSet<WConstraint>();
		boolean pchanged = false;
		boolean composite = true;
		for(WConstraint p : subterms) {
			WConstraint np = p.substitute(binding);			
			composite &= np instanceof WValue;			
			if(np instanceof WDisjunct) {	
				WDisjunct c = (WDisjunct) np;
				nparams.addAll(c.subterms);
				pchanged = true;
			} else if(np == WValue.FALSE){				
				pchanged=true;
			} else if(np != p) {								
				nparams.add(np);				
				pchanged=true;				
			} else {
				nparams.add(np);
			}
		}		
		if(composite) {			
			for(WConstraint e : nparams) {				
				WValue.Bool b = (WValue.Bool) e;
				if(b.sign()) {
					return WValue.TRUE;
				}
			}
			return WValue.FALSE;			
		} else if(nparams.size() == 1) {
			return nparams.iterator().next();
		} else if(pchanged) {			
			if(nparams.size() == 0) {
				return WValue.FALSE;			
			} else {							
				return new WDisjunct(nparams);
			} 
		} else {	
			return this;
		}
	}	
		
	public String toString() {
		boolean firstTime = true;
		String r = "";
		for(WConstraint f : subterms) {
			if(!firstTime) {
				r += " || ";
			}
			firstTime=false;			
			r += "(" + f + ")";			
		}
		return r;
	}
	
	public static WConstraint or(WConstraint... formulas) {		
		HashSet<WConstraint> fs = new HashSet<WConstraint>();
		Collections.addAll(fs, formulas);
		return new WDisjunct(fs).substitute(Collections.EMPTY_MAP);		
	}	
}
