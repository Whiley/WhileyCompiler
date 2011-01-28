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
import wyone.util.Pair;

public final class DisjunctHeuristic implements Solver.Heuristic {

	public List<Solver.State> split(Solver.State state, Solver solver) {
		for (Constraint f : state) {
			if (f instanceof WDisjunct) {				
				return split((WDisjunct) f, state, solver);
			}
		}

		return null;
	}

	private static List<Solver.State> split(WDisjunct d, Solver.State state,
			Solver solver) {				
		ArrayList<Solver.State> splits = new ArrayList<Solver.State>();
		state.eliminate(d);
		Constraint split = d.iterator().next();				
		
		Solver.State left = state.clone();		
		left.add(split, solver);
		splits.add(left);
		
		Solver.State right = state;					
		if (d.subterms().size() > 1) {
			HashSet<Constraint> subterms = new HashSet<Constraint>(d.subterms());
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
