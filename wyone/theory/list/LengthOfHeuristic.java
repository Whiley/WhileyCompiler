package wyone.theory.list;

import java.util.*;

import wyone.core.*;
import wyone.theory.congruence.WEquality;
import wyone.theory.logic.WFormula;
import wyone.theory.numeric.WNumber;
import wyone.theory.set.WSetConstructor;

public class LengthOfHeuristic implements SplitHeuristic {
	
	public List<SolverState> split(SolverState state, Solver solver) {		
		WEquality weq = pickSmallestFixed(state,solver); 
		if(weq != null) {
			return split(weq,state,solver);
		}
		return null;			
	}
	
	private List<SolverState> split(WEquality weq, SolverState state, Solver solver) {
		WLengthOf l;
		
		if(weq.lhs() instanceof WLengthOf) {
			l = (WLengthOf) weq.lhs();
		} else {
			l = (WLengthOf) weq.rhs();
		} 
		int size = computeFixedBound(weq);
		
		WFormula nf;
		
		if(l.source().type(state) instanceof WListType) {
			ArrayList<WExpr> vars = new ArrayList<WExpr>();			
			for(int i=0;i!=size;++i) {
				vars.add(WVariable.freshVar());
			}
			nf = new WEquality(true,l.source(),new WListConstructor(vars));
		} else {
			HashSet<WExpr> vars = new HashSet<WExpr>();
			for(int i=0;i!=size;++i) {
				vars.add(WVariable.freshVar());
			}
			nf = new WEquality(true,l.source(),new WSetConstructor(vars));
		}
		
		System.out.println("GOT: " + nf);
		
		ArrayList<SolverState> states = new ArrayList<SolverState>();		
		state.add(nf,solver);
		states.add(state);
		return states;
	}
	
	private WEquality pickSmallestFixed(SolverState state, Solver solver) {
		int min = Integer.MAX_VALUE; 
		WEquality seq = null;
		
		for(WFormula f : state) {
			if(f instanceof WEquality) {				
				WEquality wseq = (WEquality) f;
				if(wseq.sign()) {
					int diff = computeFixedBound(wseq);
					if(diff < min) {
						seq = wseq;
						min = diff;
					}
				}							
			}
		}
		
		return seq;
	}
	
	private int computeFixedBound(WEquality seq) {
		WExpr lhs = seq.lhs();
		WExpr rhs = seq.rhs();
		
		int min = Integer.MAX_VALUE;
		
		if(lhs instanceof WLengthOf && rhs instanceof WNumber) {			
			WNumber n = (WNumber) rhs;
			if(n.isInteger()) {
				min = n.intValue();
			}
		} else if(rhs instanceof WLengthOf && lhs instanceof WNumber) {			
			WNumber n = (WNumber) lhs;
			if(n.isInteger()) {
				min = n.intValue();
			}
		}
		
		return min;
	}
}
