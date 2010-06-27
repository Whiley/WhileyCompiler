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

import wyone.core.*;
import wyone.theory.numeric.*;
import wyone.theory.logic.*;

public class ListLengthClosure implements InferenceRule {

	public void infer(WFormula nlit, SolverState state, Solver solver) {
		for(WListAccess la : WExprs.match(WListAccess.class, nlit)) {
			WExpr source = la.source();
			WExpr index = la.index();			
			
			WFormula nf = WNumerics.greaterThan(new WLengthOf(source),index);
			if(!state.contains(nf)) {				
				state.infer(nf, solver);
			}
			nf = WNumerics.greaterThanEq(index,WNumber.ZERO);
			if(!state.contains(nf)) {								
				state.infer(nf, solver);
			}
			
		}				
	}		
}
