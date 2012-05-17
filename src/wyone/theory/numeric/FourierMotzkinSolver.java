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

import java.util.*;

import wyone.core.*;
import wyone.theory.logic.*;
import wyone.util.*;
import static wyone.theory.numeric.WNumerics.*;

/**
 * <p>
 * This algorithm is based on the well-known fourier-motzkin elimination
 * procedure. See <a
 * href="http://en.wikipedia.org/wiki/Fourier-Motzkin_elimination">Wikipedia</a>
 * for more information on this.
 * </p>
 * <p>
 * <b>NOTE:</b> This code is unsound in the presence of non-linear equations.
 * </p>
 * 
 * @author djp
 * 
 */
public final class FourierMotzkinSolver implements InferenceRule {	
	
	public void infer(WFormula delta, SolverState state, Solver solver) {								
		if (delta instanceof WInequality) {			
			WInequality eq = (WInequality) delta;
			internal_infer(eq, state, solver);			
		} 		
	}

	private static void internal_infer(WInequality ieq, SolverState state,
			Solver solver) {				
		WExpr ieq_rhs = ieq.rhs();
		WExpr v;
		
		if(ieq_rhs instanceof WRational) {			
			v = ieq_rhs.subterms().iterator().next();
		} else {
			v = ieq_rhs;
		}		
		Pair<BoundUpdate,BoundUpdate> update = rearrange(ieq,v);
		BoundUpdate lower = update.first();
		BoundUpdate upper = update.second();						
		
		if(lower != null) {				
			for(WFormula f : state) {
				if(f instanceof WInequality && usesVariable(((WInequality)f).rhs(),v)) {
					WInequality i = (WInequality) f;
					Pair<BoundUpdate,BoundUpdate> bound = rearrange(i,v);						
					upper = bound.second();						
					if(upper != null) {													
						internal_infer(lower,upper,state,solver);
					}
				}
			}
		} else if(upper != null) {				
			for(WFormula f : state) {
				if(f instanceof WInequality && usesVariable(((WInequality)f).rhs(),v)) {						
					WInequality i = (WInequality) f;
					Pair<BoundUpdate,BoundUpdate> bound = rearrange(i,v);
					lower = bound.first();
					if(lower != null) {							
						internal_infer(lower,upper,state,solver);
					}
				}
			}
		}	
	}

	public static boolean usesVariable(WExpr f, WExpr v) {
		if(f instanceof WRational) {
			WRational r = (WRational) f;
			return r.subterms().contains(v);
		} else {
			return f.equals(v);
		}
	}
	
	public static Pair<BoundUpdate, BoundUpdate> rearrange(WInequality ieq,
			WExpr v) {
		// Now, we factorise the lower bound for the variable in question.
		// Notice that we know the remainder will be zero by construction.		
		Pair<WExpr,WExpr> r = rearrangeFor(v,ieq);		
		WExpr factor = r.second();		
		
		if (factor instanceof WNumber) {
			BoundUpdate lower = null;
			BoundUpdate upper = null;

			WNumber constant = (WNumber) factor;
			// look at the sign of the coefficient to
			// determine whether or not we have an upper or lower bound.			
			if (constant.compareTo(WNumber.ZERO) < 0) {
				if (ieq.sign()) {					
					lower = new BoundUpdate(v, negate(r.first()), (WNumber) negate(constant), false);
				} else {					
					upper = new BoundUpdate(v, negate(r.first()), (WNumber) negate(constant), true);
				}
			} else if (constant.compareTo(WNumber.ZERO) > 0) {
				if (ieq.sign()) {					
					upper = new BoundUpdate(v, r.first(), constant, false);
				} else {										
					lower = new BoundUpdate(v, r.first(), constant, true);
				}
			}
			/*
			if(lower != null) {
				System.out.println("LOWER: " + lower.poly + ", " + lower.factor + " : " + lower.sign);
			}
			if(upper != null) {
				System.out.println("UPPER: " + upper.poly + ", " + upper.factor + " : " + upper.sign);
			}
			*/
			return new Pair<BoundUpdate, BoundUpdate>(lower, upper);
		} else {
			// In this case, the factor is not a constant which indicates a
			// non-linear constraint. At the moment, there's nothing we can
			// do about this so we silently drop it ... making the system
			// unsound.
			return new Pair(null, null);
		}
	}
	
	private static void internal_infer(BoundUpdate below, BoundUpdate above,
			SolverState state, Solver solver) {		
		WExpr lb;
		WExpr ub;
		WType atom_t = above.atom.type(state);
		boolean belowSign = below.sign;
		boolean aboveSign = above.sign;				
		
		// First, check for the "real shadow"
		if (atom_t instanceof WIntType
				&& below.poly instanceof WNumber
				&& above.poly instanceof WNumber) {			
			WNumber bp = (WNumber) below.poly;
			WNumber up = (WNumber) above.poly;						
			// Note, the following is guaranteed to work because the above and
			// below factors are normalised to be always positive; that way, we
			// can ignore the divide by negative number case.
			if(belowSign) {
				lb = bp.divide(below.factor).add(WNumber.ONE).ceil();
				belowSign=false;
			} else {
				lb = bp.divide(below.factor).ceil();
			}
			if(aboveSign) {
				ub = up.divide(above.factor).subtract(WNumber.ONE).floor();
				aboveSign=false;
			} else {
				ub = up.divide(above.factor).floor();
			}								
		} else {
			lb = below.poly;
			ub = above.poly;
			
			if(atom_t instanceof WIntType && belowSign) {
				belowSign=false;
				lb = add(lb,WNumber.ONE);
			}
			if(atom_t instanceof WIntType && aboveSign) {
				aboveSign=false;
				ub = subtract(ub,WNumber.ONE);
			}
			
			lb = multiply(lb,above.factor);		
			ub = multiply(ub,below.factor);
		}
		
		if(lb.equals(ub) && (belowSign || aboveSign)) {			
			state.infer(WBool.FALSE,solver);
		} else {			
			// Second, generate new inequalities
			if(lb.equals(ub)) {				
				state.infer(WExprs.equals(lb,above.atom),solver);
			} else {
				WFormula f;

				if (belowSign || aboveSign) {					
					f = lessThan(lb, ub);
				} else {
					f = lessThanEq(lb, ub);													
				}				
				state.infer(f,solver);										
			} 
		}
	}
		
	/**
     * A bound update represents an incremental update to the lower or upper
     * bound of some variable. These are identified specifically, in order that
     * we can process them incrementally, rather than trawling all of the bounds
     * for a given variable.
     * 
     * @author djp
     * 
     */	
	public final static class BoundUpdate {
		public final WExpr atom;
		public final WExpr poly;
		public final WNumber factor;
		public final Boolean sign; // true indicates strict inequality
		
		public BoundUpdate(WExpr v, WExpr p, WNumber i, Boolean s) {
			atom = v;
			poly = p;
			factor = i;
			sign = s;
		}
		
		public  String toString() {
			return factor + "*" + atom + " >< " + poly;
		}
	}	
}
