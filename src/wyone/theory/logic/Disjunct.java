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
import static wyone.core.Constructor.*;
import wyone.core.*;
import wyone.util.*;

public final class Disjunct extends Base<Formula> implements Formula {		
	/**
	 * <p>
	 * Construct a formula from a collection of formulas.
	 * </p>
	 * 
	 * @param clauses
	 */
	public Disjunct(Collection<Formula> fs) {
		super("||",fs);		
	}

	// =================================================================
	// REQUIRED METHODS
	// =================================================================

	/**
	 * This method substitutes all variable names for names given in the
	 * binding. If no binding is given for a variable, then it retains its
	 * original name.
	 * 
	 * @param binding
	 * @return
	 */
	public Formula substitute(Map<Constructor,Constructor> binding) {	
		ArrayList<Formula> nparams = new ArrayList<Formula>();
		boolean pchanged = false;
		boolean composite = true;
		for(Formula p : subterms) {
			Formula np = p.substitute(binding);			
			composite &= np instanceof Value;			
			if(np instanceof Disjunct) {	
				Disjunct c = (Disjunct) np;
				nparams.addAll(c.subterms);
				pchanged = true;
			} else if(np == Value.FALSE){				
				pchanged=true;
			} else if(np != p) {							
				nparams.add(np);				
				pchanged=true;				
			} else {
				nparams.add(np);
			}
		}		
		if(composite) {			
			for(Formula e : nparams) {				
				Value.Bool b = (Value.Bool) e;
				if(b.value) {
					return Value.TRUE;
				}
			}
			return Value.FALSE;			
		} else if(nparams.size() == 1) {
			return nparams.iterator().next();
		} else if(pchanged) {			
			if(nparams.size() == 0) {
				return Value.FALSE;			
			} else {							
				return new Disjunct(nparams);
			} 
		} else {	
			return this;
		}
	}	
		
	public Conjunct not() {
		ArrayList<Formula> nparams = new ArrayList<Formula>();
		for(Formula p : subterms) {
			Formula np = p.not();																
			nparams.add(np);											
		}				
		return new Conjunct(nparams);		 
	}
	
	public String toString() {
		boolean firstTime = true;
		String r = "";
		for(Formula f : subterms) {
			if(!firstTime) {
				r += " || ";
			}
			firstTime=false;			
			r += "(" + f + ")";			
		}
		return r;
	}
}
