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
import wyone.core.WVariable;
import wyone.util.Pair;

public final class DisjunctHeuristic implements SplitHeuristic {

	public List<SolverState> split(SolverState state, Solver solver) {
		for (WFormula f : state) {
			if (f instanceof WDisjunct) {				
				return split((WDisjunct) f, state, solver);
			}
		}

		return null;
	}

	private static List<SolverState> split(WDisjunct d, SolverState state,
			Solver solver) {				
		ArrayList<SolverState> splits = new ArrayList<SolverState>();
		state.eliminate(d);
		WFormula split = d.iterator().next();				
		
		SolverState left = state.clone();		
		left.add(split, solver);
		splits.add(left);
		
		SolverState right = state;					
		if (d.subterms().size() > 1) {
			HashSet<WFormula> subterms = new HashSet<WFormula>(d.subterms());
			subterms.remove(split);
			right.add(
					new WDisjunct(subterms).substitute(Collections.EMPTY_MAP),
					solver);
		}		
		right.add(split.not(), solver);
		splits.add(right);
		
		return splits;
	}
}
