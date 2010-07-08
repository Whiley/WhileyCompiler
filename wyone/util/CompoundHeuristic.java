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

package wyone.util;

import java.util.*;

import wyone.core.*;

/**
 * The combinator heuristic is used to combine multiple heuristics for different
 * theories, such that they are considered in a fixed order.
 * 
 * @author djp
 * 
 */
public final class CompoundHeuristic implements SplitHeuristic {
	private final ArrayList<SplitHeuristic> heuristics;
	
	public CompoundHeuristic(SplitHeuristic...heuristics) {
		this.heuristics = new ArrayList<SplitHeuristic>();
		for(SplitHeuristic h : heuristics) {
			this.heuristics.add(h);
		}
	}
	
	public CompoundHeuristic(Collection<SplitHeuristic> heuristics) {
		this.heuristics = new ArrayList<SplitHeuristic>(heuristics);		
	}
	
	public List<SolverState> split(SolverState state,Solver solver) {
		for(SplitHeuristic h : heuristics) {
			List<SolverState> r = h.split(state,solver);
			if(r != null) {				
				return r;
			}
		}
		return null;
	}
}
