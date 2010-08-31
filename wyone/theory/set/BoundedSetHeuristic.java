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

	/**
	 * The following flag indicates that only explicit upper bounds (i.e. those
	 * which are either set constructors, or set values) will be split. It's
	 * better to do these before those which have only lower bounds, or worse
	 * those which are unbounded.  This is because the potential state space for
	 * exploration is much smaller.
	 * 
	 * In this case, we pick those first with the smallest difference between
	 * the number of values in the lower and upper bound.
	 */
	private boolean splitFiniteBounded;

	public BoundedSetHeuristic(boolean splitFiniteBounded) {
		this.splitFiniteBounded = splitFiniteBounded;	
	}
	
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
