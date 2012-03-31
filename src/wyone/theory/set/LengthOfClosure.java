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

import java.util.HashSet;

import static wyone.core.Constructor.*;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;

public class LengthOfClosure implements Solver.Rule {

	public String name() {
		return "LengthOfClosure";
	}
	
	public void infer(Constraint nlit, Solver.State state, Solver solver) {
		for(LengthOf la : Helpers.match(LengthOf.class, nlit)) {
			Constructor source = la.source();			
			Constraint nf = Numerics.greaterThanEq(new LengthOf(source),Value.ZERO);
			if(!state.contains(nf)) {
				state.infer(nf, solver);
			}
		}
		
		if(nlit instanceof Equality) {
			Equality weq = (Equality) nlit;
			if(weq.sign()) {				
				infer((Equality)nlit,state,solver);
			}
		}
	}
	
	public void infer(Equality weq, Solver.State state, Solver solver) {
		Constructor lhs = weq.lhs();
		Constructor rhs = weq.rhs();		
		LengthOf l;
		int size;
		
		if(lhs instanceof LengthOf && rhs instanceof Value.Number) {			
			l = (LengthOf) lhs;			
			if(!(l.source() instanceof Variable || l.source() instanceof SetConstructor)) {
				return; // to prevent cyclic inferences
			}			
			Value.Number n = (Value.Number) rhs;
			if(n.isInteger()) {
				size = n.intValue();
			} else {
				// Size of list is not an integer
				state.infer(Value.FALSE, solver);
				return;
			}
		} else if(rhs instanceof LengthOf && lhs instanceof Value.Number) {
			l = (LengthOf) rhs;
			if(!(l.source() instanceof Variable)) {
				return; // to prevent cyclic inferences
			}
			Value.Number n = (Value.Number) lhs;
			if(n.isInteger()) {
				size = n.intValue();
			} else {
				// Size of list is not an integer
				state.infer(Value.FALSE, solver);
				return;
			}
		} else {
			return;
		}
		
		Constraint nf = Value.TRUE;		
		HashSet vars = new HashSet<Constructor>();
		for(int i=0;i!=size;++i) {
			Variable var = Variable.freshVar(); 
			//nf = Logic.and(nf,new Subtype(true,st.element,var));
			vars.add(var);
		}
		if(vars.size() == 0) {
			nf = Logic.and(nf,Equality.equals(l.source(),Value.V_SET(vars)));
		} else {
			nf = Logic.and(nf,Equality.equals(l.source(),new SetConstructor(vars)));
		}		
		
		state.infer(nf, solver);
	}		
}