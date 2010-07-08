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

package wyone.theory.type;

import wyone.core.*;
import wyone.theory.logic.WBool;
import wyone.theory.logic.WFormula;

public class TypeClosure implements InferenceRule {
	
	public void infer(WFormula nlit, SolverState state, Solver solver) {
		if(nlit instanceof WSubtype) {
			WSubtype wt = (WSubtype) nlit;
			WType lhs_t = wt.lhs().type(state);
			if(lhs_t.isSubtype(wt.rhs())) {
				if(!wt.sign()) {
					// definite problem
					state.infer(WBool.FALSE, solver);
				} else {
					// can possibly refine a variable's type here.
					refineType(wt.lhs(),wt.rhs(),state,solver);
				}
			} else {
				if(wt.sign()) {
					// definite problem
					state.infer(WBool.FALSE, solver);
				}
			}
		}
	}
	
	protected void refineType(WExpr lhs, WType rhs, SolverState state,
			Solver solver) {
		System.out.println("ATTEMPTING TO REFINE TYPE: " + lhs + " => " + rhs);
	}
}
