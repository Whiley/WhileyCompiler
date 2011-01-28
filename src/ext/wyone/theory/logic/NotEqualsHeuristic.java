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

import wyone.core.*;
import wyone.theory.congruence.*;
import static wyone.theory.numeric.Numerics.*;

public class NotEqualsHeuristic implements Solver.Heuristic {
	
	public List<Solver.State> split(Solver.State state,Solver solver) {
		
		for(Constraint f : state) {
			if(f instanceof WEquality) {
				WEquality weq = (WEquality) f;
				if(!weq.sign()) {
					return split((WEquality)f, state, solver);
				}
			}
		}
						
		return null;
	}
	
		
	private List<Solver.State> split(WEquality d, Solver.State lhs, Solver solver) {		
		lhs.eliminate(d);		
		Solver.State rhs = lhs.clone();		
		lhs.add(lessThan(d.lhs(),d.rhs()), solver);
		rhs.add(greaterThan(d.lhs(),d.rhs()), solver);						
		ArrayList<Solver.State> splits = new ArrayList<Solver.State>();
		splits.add(lhs);		
		splits.add(rhs);
		return splits;
		
	}
}
