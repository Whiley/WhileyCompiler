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

package wyone.theory.set;

import java.util.*;

import wyone.core.*;

import wyone.theory.congruence.WEquality;
import wyone.theory.logic.WBool;
import wyone.theory.logic.WFormula;
import wyone.theory.logic.WFormulas;
import wyone.theory.numeric.WInequality;
import wyone.theory.numeric.WRational;
import wyone.util.Pair;

public class BoundedSetHeuristic implements SplitHeuristic {

	public List<SolverState> split(SolverState state, Solver solver) {		
		WSubsetEq seq = pickLeastBounded(state,solver);
		if(seq != null) { return split(seq,state,solver); }
		 
		return null;			
	}
	
	public List<SolverState> split(WSubsetEq seq, SolverState lhs, Solver solver) {		
		SolverState rhs = lhs.clone();
		
		// left-split is easy
		lhs.eliminate(seq);	
		if(seq.lhs() instanceof WVariable) {
			lhs.add(new WEquality(true,seq.lhs(),seq.rhs()), solver);
		} else {
			lhs.add(new WEquality(true,seq.rhs(),seq.lhs()), solver);
		}
		
		// right-split is slightly harder.
		rhs.eliminate(seq);	
		rhs.add(WSets.subset(seq.lhs(),seq.rhs()), solver);
		
		ArrayList<SolverState> splits = new ArrayList<SolverState>();
		splits.add(lhs);		
		splits.add(rhs);		
		return splits;
	}
	
	private WSubsetEq pickLeastBounded(SolverState state, Solver solver) {
		int min = Integer.MAX_VALUE; 
		WSubsetEq seq = null;
		
		for(WFormula f : state) {
			if(f instanceof WSubsetEq) {				
				WSubsetEq wseq = (WSubsetEq) f;
				if(wseq.sign()) {
					int diff = computeBoundDiff(wseq);
					if(diff < min) {
						seq = wseq;
						min = diff;
					}
				}							
			}
		}
		
		return seq;
	}
	
	private int computeBoundDiff(WSubsetEq seq) {
		WExpr lhs = seq.lhs();
		WExpr rhs = seq.rhs();
		
		int lhsSize = 0;
		int rhsSize = Integer.MAX_VALUE;
		
		if(lhs instanceof WSetConstructor || lhs instanceof WSetVal) {			
			lhsSize = lhs.subterms().size();	
		} 
		
		if(rhs instanceof WSetConstructor || rhs instanceof WSetVal) {			
			rhsSize = rhs.subterms().size();			
		} 				
		
		return rhsSize - lhsSize;
	}
}
