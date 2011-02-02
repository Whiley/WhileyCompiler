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

import wyil.lang.Type;
import wyone.core.*;
import wyone.util.*;
import static wyone.theory.numeric.Numerics.*;

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
public final class FourierMotzkinSolver implements Solver.Rule {	
	
	public String name() {
		return "Linear Arithmetic";
	}
	
	public void infer(Constraint delta, Solver.State state, Solver solver) {								
		if (delta instanceof Inequality) {			
			Inequality eq = (Inequality) delta;
			internal_infer(eq, state, solver);			
		} 		
	}

	private static void internal_infer(Inequality ieq, Solver.State state,
			Solver solver) {				
		Constructor ieq_rhs = ieq.rhs();
		Constructor v;
		
		if(ieq_rhs instanceof Rational) {			
			v = ieq_rhs.subterms().iterator().next();
		} else {
			v = ieq_rhs;
		}		
		Pair<BoundUpdate,BoundUpdate> update = rearrange(ieq,v);
		BoundUpdate lower = update.first();
		BoundUpdate upper = update.second();						
		
		if(lower != null) {				
			for(Constraint f : state) {
				if(f instanceof Inequality && usesVariable(((Inequality)f).rhs(),v)) {
					Inequality i = (Inequality) f;
					Pair<BoundUpdate,BoundUpdate> bound = rearrange(i,v);						
					upper = bound.second();						
					if(upper != null) {													
						internal_infer(lower,upper,state,solver);
					}
				}
			}
		} else if(upper != null) {				
			for(Constraint f : state) {
				if(f instanceof Inequality && usesVariable(((Inequality)f).rhs(),v)) {						
					Inequality i = (Inequality) f;
					Pair<BoundUpdate,BoundUpdate> bound = rearrange(i,v);
					lower = bound.first();
					if(lower != null) {							
						internal_infer(lower,upper,state,solver);
					}
				}
			}
		}	
	}

	public static boolean usesVariable(Constructor f, Constructor v) {
		if(f instanceof Rational) {
			Rational r = (Rational) f;
			return r.subterms().contains(v);
		} else {
			return f.equals(v);
		}
	}
	
	public static Pair<BoundUpdate, BoundUpdate> rearrange(Inequality ieq,
			Constructor v) {
		// Now, we factorise the lower bound for the variable in question.
		// Notice that we know the remainder will be zero by construction.		
		Pair<Constructor,Constructor> r = rearrangeFor(v,ieq);		
		Constructor factor = r.second();		
		
		if (factor instanceof Value.Number) {
			BoundUpdate lower = null;
			BoundUpdate upper = null;

			Value.Number constant = (Value.Number) factor;
			// look at the sign of the coefficient to
			// determine whether or not we have an upper or lower bound.			
			if (constant.compareTo(Value.Number.ZERO) < 0) {
				if (ieq.sign()) {					
					lower = new BoundUpdate(v, negate(r.first()), (Value.Number) negate(constant), false);
				} else {					
					upper = new BoundUpdate(v, negate(r.first()), (Value.Number) negate(constant), true);
				}
			} else if (constant.compareTo(Value.Number.ZERO) > 0) {
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
			Solver.State state, Solver solver) {		
		Constructor lb;
		Constructor ub;	
		Type atom_t = above.atom.type(state);						
		
		boolean belowSign = below.sign;
		boolean aboveSign = above.sign;				
		
		// First, check for the "real shadow"
		if (atom_t instanceof Type.Int
				&& below.poly instanceof Value.Number
				&& above.poly instanceof Value.Number) {			
			Value.Number bp = (Value.Number) below.poly;
			Value.Number up = (Value.Number) above.poly;						
			// Note, the following is guaranteed to work because the above and
			// below factors are normalised to be always positive; that way, we
			// can ignore the divide by negative number case.
			if(belowSign) {
				lb = bp.divide(below.factor).add(Value.ONE).ceil();
				belowSign=false;
			} else {
				lb = bp.divide(below.factor).ceil();
			}
			if(aboveSign) {
				ub = up.divide(above.factor).subtract(Value.ONE).floor();
				aboveSign=false;
			} else {
				ub = up.divide(above.factor).floor();
			}								
		} else {
			lb = below.poly;
			ub = above.poly;
			
			if(atom_t instanceof Type.Int && belowSign) {
				belowSign=false;
				lb = add(lb,Value.Number.ONE);
			}
			if(atom_t instanceof Type.Int && aboveSign) {
				aboveSign=false;
				ub = subtract(ub,Value.Number.ONE);
			}
			
			lb = multiply(lb,above.factor);		
			ub = multiply(ub,below.factor);
		}
		
		if(lb.equals(ub) && (belowSign || aboveSign)) {			
			state.infer(Value.FALSE,solver);
		} else {			
			// Second, generate new inequalities
			if(lb.equals(ub)) {				
				state.infer(Equality.equals(lb,above.atom),solver);
			} else {
				Constraint f;

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
		public final Constructor atom;
		public final Constructor poly;
		public final Value.Number factor;
		public final Boolean sign; // true indicates strict inequality
		
		public BoundUpdate(Constructor v, Constructor p, Value.Number i, Boolean s) {
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
