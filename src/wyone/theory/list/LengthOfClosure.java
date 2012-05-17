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

package wyone.theory.list;

import java.util.ArrayList;
import java.util.HashSet;

import wyone.core.*;
import wyone.theory.congruence.WEquality;
import wyone.theory.logic.WBool;
import wyone.theory.logic.WFormula;
import wyone.theory.logic.WFormulas;
import wyone.theory.numeric.*;
import wyone.theory.set.*;
import wyone.theory.type.*;

public class LengthOfClosure implements InferenceRule {

	public void infer(WFormula nlit, SolverState state, Solver solver) {
		for(WLengthOf la : WExprs.match(WLengthOf.class, nlit)) {
			WExpr source = la.source();			
			WFormula nf = WNumerics.greaterThanEq(new WLengthOf(source),WNumber.ZERO);
			if(!state.contains(nf)) {
				state.infer(nf, solver);
			}
		}
		
		if(nlit instanceof WEquality) {
			WEquality weq = (WEquality) nlit;
			if(weq.sign()) {
				infer((WEquality)nlit,state,solver);
			}
		}
	}
	
	public void infer(WEquality weq, SolverState state, Solver solver) {
		WExpr lhs = weq.lhs();
		WExpr rhs = weq.rhs();		
		WLengthOf l;
		int size;
		
		if(lhs instanceof WLengthOf && rhs instanceof WNumber) {
			l = (WLengthOf) lhs;
			if(!(l.source() instanceof WVariable)) {
				return; // to prevent cyclic inferences
			}
			WNumber n = (WNumber) rhs;
			if(n.isInteger()) {
				size = n.intValue();
			} else {
				// Size of list is not an integer
				state.infer(WBool.FALSE, solver);
				return;
			}
		} else if(rhs instanceof WLengthOf && lhs instanceof WNumber) {
			l = (WLengthOf) rhs;
			if(!(l.source() instanceof WVariable)) {
				return; // to prevent cyclic inferences
			}
			WNumber n = (WNumber) lhs;
			if(n.isInteger()) {
				size = n.intValue();
			} else {
				// Size of list is not an integer
				state.infer(WBool.FALSE, solver);
				return;
			}
		} else {
			return;
		}
		
		WFormula nf = WBool.TRUE;
		WType src_t = l.source().type(state); 
		if(src_t instanceof WListType) {
			WListType st = (WListType) src_t;
			ArrayList vars = new ArrayList<WExpr>();			
			for(int i=0;i!=size;++i) {
				WVariable var = WVariable.freshVar(); 
				nf = WFormulas.and(nf,WTypes.subtypeOf(var,st.element()));
				vars.add(var);
			}			
			if(vars.size() == 0) {
				nf = WFormulas.and(nf,new WEquality(true,l.source(),new WListVal(vars)));
			} else {
				nf = WFormulas.and(nf,new WEquality(true,l.source(),new WListConstructor(vars)));
			}
		} else if(src_t instanceof WSetType) {
			WSetType st = (WSetType) src_t;
			HashSet vars = new HashSet<WExpr>();
			for(int i=0;i!=size;++i) {
				WVariable var = WVariable.freshVar(); 
				nf = WFormulas.and(nf,WTypes.subtypeOf(var,st.element()));
				vars.add(var);
			}
			if(vars.size() == 0) {
				nf = WFormulas.and(nf,new WEquality(true,l.source(),new WSetVal(vars)));
			} else {
				nf = WFormulas.and(nf,new WEquality(true,l.source(),new WSetConstructor(vars)));
			}
		}
		
		state.infer(nf, solver);
	}		
}