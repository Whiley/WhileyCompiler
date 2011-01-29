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

import wyil.lang.Type;
import static wyone.core.Constructor.*;
import wyone.core.*;
//import wyone.theory.congruence.WEquality;
import wyone.theory.numeric.FourierMotzkinSolver.BoundUpdate;
import wyone.util.Pair;

public class UnboundedNumberHeuristic implements Solver.Heuristic {
	private final boolean isInteger;
	
	public UnboundedNumberHeuristic(boolean isInteger) {
		this.isInteger = isInteger;
	}
	
	public List<Solver.State> split(Solver.State state, Solver solver) {
		return splitOnFirstUnbounded(state,solver);		
	}

	private List<Solver.State> splitOnFirstUnbounded(Solver.State state,
			Solver solver) {		
		HashSet<Variable> unbounded = new HashSet<Variable>();
		for(Constraint f : state) {
			if(f instanceof WEquality) {
				WEquality weq = (WEquality) f;
				if (weq.sign() && weq.lhs() instanceof Variable
						&& weq.rhs() instanceof Value.Number) {
					continue; // skip assignment
				}
			}
			Set<Variable> vars = Helpers.match(Variable.class, f);
			for(Variable v : vars) {
				if(isInteger && v.type(state) == Type.T_INT) {
					unbounded.add(v);
				} else if(!isInteger && v.type(state) == Type.T_REAL) {
					unbounded.add(v);
				}
			}
		}
		if(unbounded.isEmpty()) {
			return null;
		}
		Variable v = unbounded.iterator().next();
		Solver.State rhs = state.clone();
		
		state.infer(Numerics.greaterThanEq(Value.ZERO, v), solver);
		rhs.infer(Numerics.lessThan(v,Value.ZERO), solver);
		
		ArrayList<Solver.State> states = new ArrayList<Solver.State>();
		states.add(state);
		states.add(rhs);
		return states;
	}
}
