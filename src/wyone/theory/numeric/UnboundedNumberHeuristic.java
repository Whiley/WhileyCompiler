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

package wyone.theory.numeric;

import static wyone.theory.numeric.FourierMotzkinSolver.rearrange;

import java.util.*;

import wyone.core.*;
import wyone.theory.congruence.WEquality;
import wyone.theory.logic.WFormula;
import wyone.theory.numeric.FourierMotzkinSolver.BoundUpdate;
import wyone.theory.set.WSubsetEq;
import wyone.util.Pair;

public class UnboundedNumberHeuristic implements SplitHeuristic {
	private final boolean isInteger;
	
	public UnboundedNumberHeuristic(boolean isInteger) {
		this.isInteger = isInteger;
	}
	
	public List<SolverState> split(SolverState state, Solver solver) {
		return splitOnFirstUnbounded(state,solver);		
	}

	private List<SolverState> splitOnFirstUnbounded(SolverState state,
			Solver solver) {		
		HashSet<WVariable> unbounded = new HashSet<WVariable>();
		for(WFormula f : state) {
			if(f instanceof WEquality) {
				WEquality weq = (WEquality) f;
				if (weq.sign() && weq.lhs() instanceof WVariable
						&& weq.rhs() instanceof WNumber) {
					continue; // skip assignment
				}
			}
			Set<WVariable> vars = WExprs.match(WVariable.class, f);
			for(WVariable v : vars) {
				if(isInteger && v.type(state) == WIntType.T_INT) {
					unbounded.add(v);
				} else if(!isInteger && v.type(state) == WRealType.T_REAL) {
					unbounded.add(v);
				}
			}
		}
		if(unbounded.isEmpty()) {
			return null;
		}
		WVariable v = unbounded.iterator().next();
		SolverState rhs = state.clone();
		
		state.infer(WNumerics.greaterThanEq(WNumber.ZERO, v), solver);
		rhs.infer(WNumerics.lessThan(v,WNumber.ZERO), solver);
		
		ArrayList<SolverState> states = new ArrayList<SolverState>();
		states.add(state);
		states.add(rhs);
		return states;
	}
}
