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
import wyone.theory.type.WSubtype;
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
	private static int count = 0;
	public void infer(WFormula delta, SolverState state, Solver solver) {								
		if (delta instanceof WInequality) {			
			WInequality eq = (WInequality) delta;
			internal_infer(eq, state, solver);			
		} 		
	}

	private static void internal_infer(WInequality ieq, SolverState state,
			Solver solver) {
		WExpr ieq_rhs = ieq.rhs();
		if(ieq_rhs instanceof WRational) {			
			for(WExpr subterm : ieq_rhs.subterms()) {
				internal_infer(subterm,ieq,state,solver);
			}			
		} else {
			internal_infer(ieq_rhs,ieq,state,solver);
		}		
		
	}
	
	private static void internal_infer(WExpr v, WInequality ieq, SolverState state,
			Solver solver) {				
				
		// The purpose of the integer constraints are to identify those terms
		// involving v which are constrained to be an integer. This is then fed
		// into the constraint closure component which can perform additional
		// simplifications.
		List<WRational> intConstraints = intConstraints(v,state);		
		boolean isInteger = true;
		if(intConstraints.isEmpty()) {
			// in this case, there are no integer constraints on v.
			intConstraints.add(new WRational(new WPolynomial(v)));
			isInteger = false;
		}
		for(WRational factor : intConstraints) {
			Pair<BoundUpdate,BoundUpdate> update = rearrange(ieq,factor,v);
			BoundUpdate lower = update.first();
			BoundUpdate upper = update.second();						

			if(lower != null) {				
				// what happens to the divisor ??
				for (WFormula f : state) {
					if (f instanceof WInequality
							&& usesVariable(((WInequality) f).rhs(), v)) {
						WInequality i = (WInequality) f;						
						Pair<BoundUpdate, BoundUpdate> bound = rearrange(i, factor, v);
						upper = bound.second();
						if (upper != null) {
							internal_infer(lower, factor, upper, isInteger,
									state, solver);
						}
					}
				}
			} else if(upper != null) {		
				for (WFormula f : state) {					
					if (f instanceof WInequality
							&& usesVariable(((WInequality) f).rhs(), v)) {
						WInequality i = (WInequality) f;				
						Pair<BoundUpdate, BoundUpdate> bound = rearrange(i, factor, v);
						lower = bound.first();
						if (lower != null) {
							internal_infer(lower, factor, upper, isInteger,
									state, solver);
						}
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
			WRational term, WExpr v) {
		// Now, we factorise the lower bound for the variable in question.
		// Notice that we know the remainder will be zero by construction.		
		Pair<WExpr,WExpr> r = rearrangeFor(v,ieq);				
		WExpr factor = r.second();		
		WExpr remainder = r.first();
				
		if(term != null) {
			Pair<WPolynomial,WPolynomial> p = term.rearrangeFor(v);		
			WExpr divisor = WNumerics.normalise(term.denominator());
			factor = WNumerics.divide(factor,new WRational(p.second()).negate());					
			remainder = WNumerics.subtract(remainder,new WRational(p.first()));			
			remainder = WNumerics.divide(remainder,divisor);
		}		
		
		if (factor instanceof WNumber) {
			BoundUpdate lower = null;
			BoundUpdate upper = null;

			WNumber constant = (WNumber) factor;
			// look at the sign of the coefficient to
			// determine whether or not we have an upper or lower bound.			
			if (constant.compareTo(WNumber.ZERO) < 0) {
				if (ieq.sign()) {					
					lower = new BoundUpdate(v, negate(remainder), (WNumber) negate(constant), false);
				} else {					
					upper = new BoundUpdate(v, negate(remainder), (WNumber) negate(constant), true);
				}
			} else if (constant.compareTo(WNumber.ZERO) > 0) {
				if (ieq.sign()) {					
					upper = new BoundUpdate(v, remainder, constant, false);
				} else {										
					lower = new BoundUpdate(v, remainder, constant, true);
				}
			}
			/*
			if(lower != null) {
				if(lower.sign) {
					System.out.println(lower.factor + "*" + v + " > " + lower.poly);
				} else {
					System.out.println(lower.factor + "*" + v + " >= " + lower.poly);
				}			
			}
			if(upper != null) {
				if(upper.sign) {
					System.out.println(upper.factor + "*" + v + " < " + upper.poly);
				} else {
					System.out.println(upper.factor + "*" + v + " <= " + upper.poly);
				}			
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
	
	private static void internal_infer(BoundUpdate below, WExpr term, BoundUpdate above,
			boolean isInteger, SolverState state, Solver solver) {		
		
		boolean belowSign = below.sign;
		boolean aboveSign = above.sign;				
		WExpr lb = below.poly;
		WExpr ub = above.poly;
		
		// First, check for the "real shadow"
		if (isInteger && lb instanceof WNumber
				&& ub instanceof WNumber) {				
			WNumber bp = (WNumber) lb;
			WNumber up = (WNumber) ub;						
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
			if(belowSign && isInteger) {
				belowSign=false;
				lb = add(lb,WNumber.ONE);
			}
			if(aboveSign && isInteger) {
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
				state.infer(WExprs.equals(lb,term),solver);
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
	
	public static List<WRational> intConstraints(WExpr atom, SolverState state) {
		ArrayList<WRational> factors = new ArrayList<WRational>();
		for(WFormula c : state) {
			// FIXME: need to check that WSubtype is an INT??
			if(c instanceof WSubtype) {
				WSubtype st = (WSubtype) c;
				if(st.rhs() instanceof WIntType) {
					if(st.lhs().equals(atom)) {
						// This is the easiest case. 
						factors.add(new WRational(new WPolynomial(atom)));					
						break; // no need to identify any more factors
					} else if (st.lhs() instanceof WRational
							&& st.lhs().subterms().contains(atom)) {
						// The harder case, where we have a constraint of the form
						// e.g. "int 2*x", where we're looking for atom "x".
						// In such case, we'll return two as the factor.					
						factors.add((WRational) st.lhs());
					}
				}
			}
		}			
		return factors;
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
