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
import wyone.core.*;
import wyone.theory.numeric.FourierMotzkinSolver.BoundUpdate;
import wyone.util.Pair;

public class BoundedNumberHeuristic implements Solver.Heuristic {
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
	
	public List<Solver.State> split(Solver.State state, Solver solver) {
		HashMap<Constructor, Pair<Bound, Bound>> bounds = determineVariableBounds(
				state, solver);
		if (strict) {
			return splitOnSmallestBounded(bounds, state, solver);
		} else {			
			return splitOnFirstSemiBounded(bounds, state, solver);
		}
	}

	private List<Solver.State> splitOnFirstSemiBounded(
			HashMap<Constructor, Pair<Bound, Bound>> bounds, Solver.State lhs,
			Solver solver) {						
		
		for(Map.Entry<Constructor, Pair<Bound,Bound>> e : bounds.entrySet()) {
			Bound low = e.getValue().first();
			Bound high = e.getValue().second();
						
			if(low != null || high != null) {							
				Constructor var = e.getKey();
				Solver.State rhs = lhs.clone();								
				
				if(low != null) {				
					lhs.add(Equality.equals(var,low.num), solver);
					rhs.add(Numerics.greaterThan(var,low.num), solver);					
				} else {					
					lhs.add(Equality.equals(var,high.num), solver);
					rhs.add(Numerics.lessThan(var,high.num), solver);					
				}

				ArrayList<Solver.State> splits = new ArrayList<Solver.State>();				
				splits.add(lhs);
				splits.add(rhs);
				return splits;						
			}
		}
		
		return null;
	}
	
	private List<Solver.State> splitOnSmallestBounded(
			HashMap<Constructor, Pair<Bound, Bound>> bounds, Solver.State lhs,
			Solver solver) {
		Value.Number minDiff = null;
		Constructor var = null;
		Value.Number varlow = null;
		Value.Number varhigh = null;
		
		for(Map.Entry<Constructor, Pair<Bound,Bound>> e : bounds.entrySet()) {
			Bound low = e.getValue().first();
			Bound high = e.getValue().second();
			if(low != null && high != null) {
				Value.Number diff = high.num.subtract(low.num);
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
		
		Value.Number mid = varlow.add(varhigh).divide(Value.TWO);		
		Solver.State rhs = lhs.clone();
				
		lhs.add(Numerics.lessThanEq(var,mid), solver);
		rhs.add(Numerics.greaterThan(var,mid), solver);
		
		ArrayList<Solver.State> splits = new ArrayList<Solver.State>();
		splits.add(lhs);
		splits.add(rhs);
		return splits;
	}
	
	private HashMap<Constructor, Pair<Bound,Bound>> determineVariableBounds(
			Solver.State state, Solver solver) {
		HashMap<Constructor,Pair<Bound,Bound>> bounds = new HashMap();
		
		for(Constraint f : state) {
			if(f instanceof Inequality) {				
				Inequality wieq = (Inequality) f;
				Constructor var = variable(wieq);									
				Type t = var.type(state);
				if(isInteger && t instanceof Type.Int) {
					updateBounds(wieq,var,true,bounds);
				} else if(!isInteger && t instanceof Type.Real) {
					updateBounds(wieq,var,false,bounds);
				}								
			} 
		}
		
		return bounds;
	}
	
	private Constructor variable(Inequality ieq) {
		Constructor ieq_rhs = ieq.rhs();
		if (ieq_rhs instanceof Rational) {
			Rational r = (Rational) ieq_rhs;
			List<Constructor> subterms = r.subterms();
			if (subterms.size() == 1) {
				return subterms.get(0);
			}
		} 
		return ieq_rhs;		
	}
	
	private static void updateBounds(Inequality wieq, Constructor var,
			boolean isInteger, HashMap<Constructor, Pair<Bound, Bound>> bounds) {
		// FIXME: could possibly do better here?
		Pair<BoundUpdate,BoundUpdate> r = rearrange(wieq,null,var);		
		BoundUpdate below = r.first();
		BoundUpdate above = r.second();				
		
		if(below != null) {
			updateLowerBound(below, var, isInteger, bounds);
		} else {
			updateUpperBound(above, var, isInteger, bounds);
		}					
	}
	
	private static void updateUpperBound(BoundUpdate above, Constructor var,
			boolean isInteger, HashMap<Constructor, Pair<Bound, Bound>> bounds) {

		Value.Number up = (Value.Number) above.poly;
		boolean isStrict = above.sign;
		
		up = up.divide(above.factor);
		
		if (isInteger && isStrict && up.isInteger()) {
			up = up.subtract(Value.Number.ONE).floor();
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
	
	private static void updateLowerBound(BoundUpdate below, Constructor var,
			boolean isInteger, HashMap<Constructor, Pair<Bound, Bound>> bounds) {
		
		Value.Number bp = (Value.Number) below.poly;
		boolean isStrict = below.sign;
		
		bp = bp.divide(below.factor);
		
		if(isInteger && isStrict && bp.isInteger()) {
			bp = bp.add(Value.Number.ONE).ceil();
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
		public final Value.Number num;
		public Bound(boolean sign, Value.Number num) {
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
		
		public String toString() {
			return sign ? num + " < " : " <= " + num;
		}
	}
}
