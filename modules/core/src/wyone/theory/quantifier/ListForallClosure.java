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

package wyone.theory.quantifier;

import java.util.*;
import wyone.core.*;
import wyone.theory.congruence.*;
import wyone.theory.list.WListAccess;
import wyone.theory.logic.*;

public class ListForallClosure implements InferenceRule {

	public void infer(WFormula nlit, SolverState state, Solver solver) {		
		if(nlit instanceof WEquality) {
			WEquality weq = (WEquality) nlit;			
			attemptEqInference(weq, state, solver);														
		} 
	}	
	
	public void attemptEqInference(WEquality weq, SolverState state, Solver solver) {
		if(!weq.sign() || !(weq.lhs() instanceof WListAccess)) {
			return; // not sure what we can do here.
		}
		
		WListAccess lhs = (WListAccess) weq.lhs();
		
		WExpr rhs = weq.rhs();
		
		for(WFormula f : state) {
			if(f instanceof WBoundedForall) {
				WBoundedForall lf = (WBoundedForall) f;
				attemptInference(lhs,rhs,lf,state,solver);
			}
		}
	}
	
	public void attemptInference(WListAccess lhs, WExpr rhs, WBoundedForall lf, SolverState state,
			Solver solver) {
		WExpr src = lhs.source();
		WExpr index = lhs.index();
		HashMap<WVariable,WExpr> nvariables = new HashMap<WVariable,WExpr>();
		HashMap<WExpr,WExpr> binding = new HashMap<WExpr,WExpr>();
		
		for(Map.Entry<WVariable, WExpr> e : lf.variables().entrySet()) {
			WExpr esrc = e.getValue();
			if(src.equals(esrc)) {
				// match
				binding.put(e.getKey(), index);
				binding.put(new WListAccess(src,index),rhs);
			} else {
				nvariables.put(e.getKey(), esrc);
			}
		}
		
		if(!binding.isEmpty()) {
			if(nvariables.isEmpty()) {
				// no forall needed now
				WFormula nf = lf.formula().substitute(binding);
				if(!state.contains(nf)) {
					state.infer(nf, solver);
				}
			} else {
				// still need forall
				WFormula nformula = lf.formula().substitute(binding);
				WBoundedForall nf = new WBoundedForall(true,nvariables,nformula);
				if(!state.contains(nf)) {
					state.infer(nf, solver);
				}
			}
		}
	}
}
