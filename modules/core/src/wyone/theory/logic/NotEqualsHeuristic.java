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

import wyone.core.Solver;
import wyone.core.SolverState;
import wyone.core.SplitHeuristic;
import wyone.theory.congruence.*;
import static wyone.theory.numeric.WNumerics.*;

public class NotEqualsHeuristic implements SplitHeuristic {
	
	public List<SolverState> split(SolverState state,Solver solver) {
		
		for(WFormula f : state) {
			if(f instanceof WEquality) {
				WEquality weq = (WEquality) f;
				if(!weq.sign()) {
					return split((WEquality)f, state, solver);
				}
			}
		}
						
		return null;
	}
	
		
	private List<SolverState> split(WEquality d, SolverState lhs, Solver solver) {		
		lhs.eliminate(d);		
		SolverState rhs = lhs.clone();		
		lhs.add(lessThan(d.lhs(),d.rhs()), solver);
		rhs.add(greaterThan(d.lhs(),d.rhs()), solver);						
		ArrayList<SolverState> splits = new ArrayList<SolverState>();
		splits.add(lhs);		
		splits.add(rhs);
		return splits;
		
	}
}
