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

public class CongruenceClosure implements Solver.Rule {

	public void infer(Constraint nlit, Solver.State state, Solver solver) {				
		if(nlit instanceof Equality) {		
			Equality eq = (Equality) nlit;			
			if(eq.sign()) {				
				inferEquality(eq, state, solver);				
				return;
			} 			
		} 
		inferFormula(nlit,state,solver);				
	}
	
	private void inferFormula(Constraint formula, Solver.State state, Solver solver) {
		HashMap<Constructor,Constructor> binding = new HashMap<Constructor,Constructor>();
	
		for(Constraint f : state) {
			if(f instanceof Equality) {
				Equality weq = (Equality) f;
				if(weq.sign()) {
					// FIXME: there is a subtle problem here, when we have multiple
					// possible assignments for a variable. This can arise when we
					// don't eliminate assignments in inferEquality.
					binding.put(weq.lhs(),weq.rhs());					
				}
			}
		}
		
		Constraint nf = formula.substitute(binding);
		if(nf != formula) {			
			state.eliminate(formula);	
			if(!state.contains(nf)) {				
				state.infer(nf,solver);
			}
		}
	}
	
	private void inferEquality(Equality eq, Solver.State state, Solver solver) {
		
		if(typeCheck(eq,state) != eq) {				
			state.infer(Value.FALSE,solver);
			return; // no point going on.
		}
		
		// So, at this point we have an equality of the form lhs == rhs.
		// This equality should have been normalised into a form where lhs
		// represents a single "effective-variable", and rhs represents other
		// stuff. Observe that lhs is no guaranteed to be an instanceof
		// wVariable (unforatunately); this is because of tupple and list
		// accessors --- which are not WVariables as they are "interpreted".
		// That is, given a value for the target variable their value can be
		// immediately deduced.
		//
		// Now, our objective at this point is to substitute all occurrences of
		// the lhs with the rhs, such that this equality is the only place where
		// the lhs now exists. When the lhs is actually an instance of
		// WVariable, then this will be an "assignment".
		//
		// Some issues can arise from other equalities which now on a new form.
		// For example, consider this:
		//
		// t1.x < 0 && t2.x == t2.y && t2.y > 0 && t1 == t2*
		// 
		// Here, last equality is the one being added. So, after substituting t1
		// for t2, we get this state:
		//
		// t2.x < 0 && t2.x == t2.y && t2.y > 0 && t1 == t2
		//
		// But, we now need to substitute for t2.x as well, in order to get
		// here:
		//
		// t2.y < 0 && t2.x == t2.y && t2.y > 0 && t1 == t2
		//
		// This then gives the contradiction.
		//
		// What we're really trying to do is choose a representative from the
		// class of equivalences, and ensure that all constraints are in terms
		// of the current representatives.
			
		HashMap<Constructor, Constructor> binding = new HashMap<Constructor,Constructor>();
		binding.put(eq.lhs(),eq.rhs());
		
		// Second, we iterate all existing literals and attempt to simplify
		// them. Those which are simplified are subsumed, and their simplified
		// forms are added into the literal set.
		for(Constraint f : state) {
			if(f == eq) { continue; }			
			Constraint nf = typeCheck(f.substitute(binding),state);									
			if(nf != f) {
				// f has been replaced!					
				if(!isAssignment(f)) {					
					state.eliminate(f);
				}							
				state.infer(nf,solver);							
			}
		}
	}	
	
	private static boolean isAssignment(Constraint f) {
		if (f instanceof Equality) {
			Equality weq = (Equality) f;
			return weq.lhs() instanceof Variable
					&& weq.rhs() instanceof Value;
		}
		return false;
	}	
	
	public Constraint typeCheck(Constraint f, Solver.State state) {
		// sanity check assignments. Not strictly necessary, but useful to
		// ensure early termination when the type of an assignment is clearly
		// wrong.
		if(f instanceof Equality) {
			Equality weq = (Equality) f;
			Constructor lhs = weq.lhs();
			Constructor rhs = weq.rhs();
			if (lhs instanceof Variable && rhs instanceof Value) {
				Variable var = (Variable) lhs;
				Value val = (Value) rhs;
				Type t = Subtype.type(var,state);
				// Type can currently be null if represents a quantified
				// variable.
				if(t != null && !Type.isSubtype(t,val.type(null))) {					
					return Value.FALSE;
				}
			}
		}
		
		return f;
	}
}
