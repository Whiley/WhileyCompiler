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

import java.util.Collections;

import wyone.core.*;
import wyone.theory.logic.WBool;
import wyone.theory.logic.WFormula;

public class TypeClosure implements InferenceRule {
	
	public void infer(WFormula nlit, SolverState state, Solver solver) {				
		if(nlit instanceof WSubtype) {			
			inferSubtype((WSubtype)nlit,state,solver);			
		}		
	}
	
	protected void inferSubtype(WSubtype ws, SolverState state,
			Solver solver) {
		
		boolean wsign = ws.sign();
		WExpr lhs = ws.lhs();
		WType rhs = ws.rhs();
		
		for(WFormula f : state) {			
			if(f instanceof WSubtype && f!=ws) {				
				WSubtype st = (WSubtype) f;
				WType st_rhs = st.rhs();				
				if(st.lhs().equals(lhs)) {					
					boolean subst = rhs.isSubtype(st_rhs, Collections.EMPTY_MAP);
					boolean stsub = st_rhs.isSubtype(rhs, Collections.EMPTY_MAP);
					boolean signs = wsign == st.sign();
					// ok, this is icky
					if(subst && wsign && signs) {
						// ws is subsumed by st
						state.eliminate(ws);
						return;
					} else if(stsub && wsign && signs) {
						// st is subsumed by ws
						state.eliminate(st);
					} else if(stsub && wsign && !signs) {
						// error
						state.infer(WBool.FALSE, solver);
						return;
					} else if(subst && !wsign && !signs) {
						// error
						state.infer(WBool.FALSE, solver);
						return;
					} else if(!subst && !stsub && wsign && signs) {
						state.infer(WBool.FALSE, solver);
						return;
					}											
				}
			}
		}
	}
}
