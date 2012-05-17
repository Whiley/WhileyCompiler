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
import wyone.theory.logic.WFormula;
import wyone.theory.numeric.FourierMotzkinSolver.BoundUpdate;
import wyone.util.Pair;

public class BoundedNumberHeuristic implements SplitHeuristic {
	private final boolean strict;
	private final boolean isInteger;
	
	/**
	 * If strict true, then only fully bounded variables are selected.
	 * 
	 * @param strict
	 */
	public BoundedNumberHeuristic(boolean isInteger, boolean strict) {
		this.strict = strict;
		this.isInteger = isInteger;
	}
	
	public List<SolverState> split(SolverState state, Solver solver) {
		HashMap<WExpr, Pair<Bound, Bound>> bounds = determineVariableBounds(
				state, solver);
		if (strict) {
			return splitOnSmallestBounded(bounds, state, solver);
		} else {
			return splitOnFirstSemiBounded(bounds, state, solver);
		}
	}

	private List<SolverState> splitOnFirstSemiBounded(
			HashMap<WExpr, Pair<Bound, Bound>> bounds, SolverState lhs,
			Solver solver) {		
		
		for(Map.Entry<WExpr, Pair<Bound,Bound>> e : bounds.entrySet()) {
			Bound low = e.getValue().first();
			Bound high = e.getValue().second();
			if(low != null || high != null) {							
				WExpr var = e.getKey();
				SolverState rhs = lhs.clone();								
				
				if(low != null) {				
					lhs.add(WExprs.equals(var,low.num), solver);
					rhs.add(WNumerics.greaterThan(var,low.num), solver);
				} else {					
					lhs.add(WExprs.equals(var,high.num), solver);
					rhs.add(WNumerics.lessThan(var,high.num), solver);					
				}

				ArrayList<SolverState> splits = new ArrayList<SolverState>();				
				splits.add(lhs);
				splits.add(rhs);
				return splits;						
			}
		}
		
		return null;
	}
	
	private List<SolverState> splitOnSmallestBounded(
			HashMap<WExpr, Pair<Bound, Bound>> bounds, SolverState lhs,
			Solver solver) {
		WNumber minDiff = null;
		WExpr var = null;
		WNumber varlow = null;
		WNumber varhigh = null;
		
		for(Map.Entry<WExpr, Pair<Bound,Bound>> e : bounds.entrySet()) {
			Bound low = e.getValue().first();
			Bound high = e.getValue().second();
			if(low != null && high != null) {
				WNumber diff = high.num.subtract(low.num);
				if(minDiff == null || diff.compareTo(minDiff) < 0) {
					var = e.getKey();
					varlow = low.num;
					varhigh = high.num;
					minDiff = diff;
				}
			}
		}
		
		if(var == null) {
			return null;
		}								
		
		WNumber mid = varlow.add(varhigh).divide(WNumber.TWO);		
		SolverState rhs = lhs.clone();
				
		lhs.add(WNumerics.lessThanEq(var,mid), solver);
		rhs.add(WNumerics.greaterThan(var,mid), solver);
		
		ArrayList<SolverState> splits = new ArrayList<SolverState>();
		splits.add(lhs);
		splits.add(rhs);
		return splits;
	}
	
	private HashMap<WExpr, Pair<Bound,Bound>> determineVariableBounds(
			SolverState state, Solver solver) {
		HashMap<WExpr,Pair<Bound,Bound>> bounds = new HashMap();
		
		for(WFormula f : state) {
			if(f instanceof WInequality) {				
				WInequality wieq = (WInequality) f;
				WExpr var = variable(wieq);									
				WType t = var.type(state);
				if(isInteger && t instanceof WIntType) {
					updateBounds(wieq,var,true,bounds);
				} else if(!isInteger && t instanceof WRealType) {
					updateBounds(wieq,var,false,bounds);
				}								
			}
		}
		
		return bounds;
	}
	
	private WExpr variable(WInequality ieq) {
		WExpr ieq_rhs = ieq.rhs();
		if (ieq_rhs instanceof WRational) {
			WRational r = (WRational) ieq_rhs;
			List<WExpr> subterms = r.subterms();
			if (subterms.size() == 1) {
				return subterms.get(0);
			}
		} 
		return ieq_rhs;		
	}
	
	private static void updateBounds(WInequality wieq, WExpr var,
			boolean isInteger, HashMap<WExpr, Pair<Bound, Bound>> bounds) {
		Pair<BoundUpdate,BoundUpdate> r = rearrange(wieq,var);		
		BoundUpdate below = r.first();
		BoundUpdate above = r.second();				
		
		if(below != null) {
			updateLowerBound(below, var, isInteger, bounds);
		} else {
			updateUpperBound(above, var, isInteger, bounds);
		}					
	}
	
	private static void updateUpperBound(BoundUpdate above, WExpr var,
			boolean isInteger, HashMap<WExpr, Pair<Bound, Bound>> bounds) {

		WNumber up = (WNumber) above.poly;
		boolean isStrict = above.sign;
		
		up = up.divide(above.factor);
		
		if (isInteger && isStrict && up.isInteger()) {
			up = up.subtract(WNumber.ONE).floor();
			isStrict = false;
		} else if (isInteger) {
			up = up.floor();
		}
		
		Bound bound = new Bound(isStrict, up);
		Pair<Bound, Bound> bs = bounds.get(var);
		if (bs == null) {
			bounds.put(var, new Pair<Bound, Bound>(null, bound));
		} else if (bs.second() == null || bs.second().compareTo(bound) > 0) {
			bounds.put(var, new Pair<Bound, Bound>(bs.first(), bound));
		} else {
			// no update
		}
	}
	
	private static void updateLowerBound(BoundUpdate below, WExpr var,
			boolean isInteger, HashMap<WExpr, Pair<Bound, Bound>> bounds) {
		
		WNumber bp = (WNumber) below.poly;
		boolean isStrict = below.sign;
		
		bp = bp.divide(below.factor);
		
		if(isInteger && isStrict && bp.isInteger()) {
			bp = bp.add(WNumber.ONE).ceil();
			isStrict = false; 
		} else if(isInteger) {
			bp = bp.ceil();
		}
		
		Bound bound = new Bound(!isStrict,bp);
		Pair<Bound,Bound> bs = bounds.get(var);
		if(bs == null) {
			bounds.put(var,new Pair<Bound,Bound>(bound,null));
		} else if(bs.first() == null || bs.first().compareTo(bound) < 0){
			bounds.put(var,new Pair<Bound,Bound>(bound,bs.second()));
		} else {
			// no update
		}
	}
	
	
	private final static class Bound implements Comparable<Bound> {
		public final boolean sign;
		public final WNumber num;
		public Bound(boolean sign, WNumber num) {
			this.sign = sign;
			this.num = num;
		}
		
		public int compareTo(Bound b) {
			int nc = num.compareTo(b.num);
			if(nc != 0) {
				return nc;
			}
			if(!sign && b.sign) {
				return -1;
			} else if(sign && !sign) {
				return 1;
			} else {
				return 0;
			}
		}
	}
}
