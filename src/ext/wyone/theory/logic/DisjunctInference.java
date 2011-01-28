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

import wyone.core.*;

public final class DisjunctInference implements InferenceRule {

	public void infer(WFormula delta, SolverState state, Solver solver) {
		// Now, substitute it, potentially reducing the disjunct.
		for (WFormula f : state) {
			if (f instanceof WDisjunct) {				
				WFormula nf = state.reduce(f);				
				if (nf != f && nf != WBool.TRUE) {
					state.eliminate(f);
					state.infer(nf,solver);
				}
			}
		}
	}
}
