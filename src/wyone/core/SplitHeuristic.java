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

package wyone.core;

import java.util.List;

/**
 * A split heuristic is used to split a given solver state into one or more
 * sub-states. For example, the split might be done on a disjunction, whereby
 * the different operands of the disjunction are asserted in corresponding
 * sub-states.
 * 
 * @author djp
 * 
 */
public interface SplitHeuristic {
	
	/**
	 * Given a solver state, generate as many sub-states as possible. If the
	 * returned list is empty, this indicates no further splits are possible.
	 * 
	 * @param state
	 * @return
	 */
	public List<SolverState> split(SolverState state, Solver solver); 
}
