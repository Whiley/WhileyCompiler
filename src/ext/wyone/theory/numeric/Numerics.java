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

import java.math.BigInteger;
import java.util.Collections;

import wyone.core.*;
import wyone.util.Pair;
import wyone.theory.logic.*;

public class Numerics {

	private static Constructor normalise(Rational r) {
		if(r.isConstant()) {
			return r.constant();
		} else if(r.isAtom()) {
			return r.atom();
		} else {
			return r;
		}
	}
	
	public static Constructor normalise(Polynomial p) {
		if(p.isConstant()) {
			return Value.V_NUM(p.constant());
		} else if(p.isAtom()) {
			return p.atom();
		} else {
			return new Rational(p);
		}
	}	
	
	public static Rational rational(Constructor r) {
		if(r instanceof Value.Number) {
			Value.Number n = (Value.Number) r;
			return new Rational(new Polynomial(n.numerator()),new Polynomial(n.denominator()));
		} else if(r instanceof Rational) {
			return (Rational) r;
		} else  {			
			return new Rational(new Polynomial(r));
		} 
	}
	
	public static Constructor negate(Constructor expr) {
		if(expr instanceof Value.Number) {
			return ((Value.Number)expr).negate();
		} else if(expr instanceof Rational) {
			// must be a rational
			Rational r = (Rational) expr;
			return normalise(r.multiply(-1));
		} else {
			// something else
			return new Rational(new Polynomial(expr)).multiply(-1);
		} 
	}
		
	public static Constructor add(Constructor lhs, Constructor rhs) {
		if (lhs instanceof Value.Number && rhs instanceof Value.Number) {
			Value.Number l = (Value.Number) lhs;
			Value.Number r = (Value.Number) rhs;
			return l.add(r);
		} else {
			return normalise(rational(lhs).add(rational(rhs)));
		}		
	}
	
	public static Constructor subtract(Constructor lhs, Constructor rhs) {
		if (lhs instanceof Value.Number && rhs instanceof Value.Number) {
			Value.Number l = (Value.Number) lhs;
			Value.Number r = (Value.Number) rhs;
			return l.subtract(r);
		} else {
			return normalise(rational(lhs).subtract(rational(rhs)));
		}
	}
	
	public static Constructor multiply(Constructor lhs, Constructor rhs) {
		if (lhs instanceof Value.Number && rhs instanceof Value.Number) {
			Value.Number l = (Value.Number) lhs;
			Value.Number r = (Value.Number) rhs;
			return l.multiply(r);
		} else {
			return normalise(rational(lhs).multiply(rational(rhs)));
		}
	}
	
	public static Constructor divide(Constructor lhs, Constructor rhs) {
		if (lhs instanceof Value.Number && rhs instanceof Value.Number) {
			Value.Number l = (Value.Number) lhs;
			Value.Number r = (Value.Number) rhs;
			return l.divide(r);
		} else {
			return normalise(rational(lhs).divide(rational(rhs)));
		}
	}
	
	/**
	 * The following method rearranges an equation such that all occurrences of a
	 * given variable occur on the lhs, and everything else is on the rhs.
	 * 
	 * @param <T>
	 * @param var
	 *            --- variable to rearrange for
	 * @param eq
	 *            --- Equation to rearrange
	 * @return A pair (r,f) consisting of a factor f, and the right-hand side.
	 */
	public static Pair<Constructor,Constructor> rearrangeFor(Constructor atom, Inequality eq) {	
		Constructor rhs = eq.rhs();	
				
		if(rhs.equals(atom)) {
			return new Pair<Constructor,Constructor>(Value.Number.ZERO,Value.MONE);
		} else if(rhs instanceof Rational){
			Rational r = (Rational) rhs;
			Pair<Polynomial,Polynomial> p = r.rearrangeFor(atom);			
			Constructor l = normalise(new Rational(p.first()));
			Constructor f = normalise(new Rational(p.second()));
			return new Pair<Constructor,Constructor>(l,f);
		} else  {			
			return new Pair<Constructor,Constructor>(rhs,Value.Number.ZERO);
		} 	
	}
				
	public static Constraint lessThan(Constructor lhs, Constructor rhs) {
		return inequals(false,rhs,lhs).substitute(Collections.EMPTY_MAP);
	}
	
	public static Constraint lessThanEq(Constructor lhs, Constructor rhs) {		
		return inequals(true,lhs,rhs).substitute(Collections.EMPTY_MAP);
	}
	
	public static Constraint greaterThan(Constructor lhs, Constructor rhs) {
		return inequals(false,lhs,rhs).substitute(Collections.EMPTY_MAP);
	}
	
	public static Constraint greaterThanEq(Constructor lhs, Constructor rhs) {
		return inequals(true,rhs,lhs).substitute(Collections.EMPTY_MAP);
	}
	
	private static Constraint inequals(boolean sign, Constructor lhs, Constructor rhs) {
		return inequals(sign, rational(lhs),rational(rhs));
	}
		
	private static Constraint inequals(boolean sign, Rational lhs, Rational rhs) {		
		if(lhs.denominator().equals(Polynomial.ONE)) {		
			return inequals(sign,lhs.numerator(),rhs);
		} else if(rhs.denominator().equals(Polynomial.ONE)) {
			return inequals(sign,lhs,rhs.numerator());
		} else if(lhs.denominator().isConstant()) {			
			BigInteger constant = lhs.denominator().constant();			
			int r = constant.compareTo(BigInteger.ZERO);
			if(r < 0) {
				return inequals(sign,rhs.multiply(constant),lhs.numerator());
			} else if (r > 0) {
				return inequals(sign,lhs.numerator(),rhs.multiply(constant));
			} else {
				return Value.FALSE;
			}
		} else if(rhs.denominator().isConstant()) {			
			BigInteger constant = rhs.denominator().constant();					
			int r = constant.compareTo(BigInteger.ZERO);
			if(r < 0) {
				return inequals(sign,rhs.denominator(),lhs.multiply(constant));
			} else if (r > 0) {
				return inequals(sign,lhs.multiply(constant),rhs.denominator());
			} else {
				return Value.FALSE;
			}			
		} else {
			// Ok, can't break down any more ... so disjunction required			
			Constraint gtz = inequals(sign,lhs.numerator(),rhs.multiply(lhs.denominator()));
			Constraint ltz = inequals(sign,rhs.multiply(lhs.denominator()),lhs.numerator());
			gtz = Logic.and(gtz,new Inequality(false,subtract(Value.Number.ZERO,normalise(lhs.denominator()))));
			ltz = Logic.and(ltz,new Inequality(false,subtract(normalise(lhs.denominator()),Value.Number.ZERO)));
			return Logic.or(ltz,gtz);			
		}
	}	
	
	private static Constraint inequals(boolean sign, Polynomial lhs, Rational rhs) {				
		if(rhs.denominator().isConstant()) {
			BigInteger constant = rhs.denominator().constant();
			int r = constant.compareTo(BigInteger.ZERO);
			if(r < 0) {
				return new Inequality(sign,subtract(normalise(rhs.numerator()),normalise(lhs.multiply(constant))));
			} else if (r > 0) {				
				return new Inequality(sign,subtract(normalise(rhs.numerator()),normalise(lhs.multiply(constant))));
			} else {
				return Value.FALSE;
			}			
		} else {
			Constraint gtz = new Inequality(sign,subtract(normalise(lhs.multiply(rhs.denominator())),normalise(rhs.numerator())));
			Constraint ltz = new Inequality(sign,subtract(normalise(rhs.numerator()),normalise(lhs.multiply(rhs.denominator()))));
			gtz = Logic.and(gtz,new Inequality(false,subtract(Value.Number.ZERO,normalise(rhs.denominator()))));
			ltz = Logic.and(ltz,new Inequality(false,subtract(normalise(rhs.denominator()),Value.Number.ZERO)));
			return Logic.or(ltz,gtz);	
		}
	}
	
	private static Constraint inequals(boolean sign, Rational lhs, Polynomial rhs) {		
		if(lhs.denominator().isConstant()) {			
			BigInteger constant = lhs.denominator().constant();
			int r = constant.compareTo(BigInteger.ZERO);
			if(r < 0) {				
				return new Inequality(sign,subtract(normalise(lhs.numerator()),normalise(rhs.multiply(constant))));
			} else if (r > 0) {				
				return new Inequality(sign,subtract(normalise(rhs.multiply(constant)),normalise(lhs.numerator())));
			} else {
				return Value.FALSE;
			}
		} else {
			Constraint gtz = new Inequality(sign,subtract(normalise(rhs.multiply(lhs.denominator())),normalise(lhs.numerator())));
			Constraint ltz = new Inequality(sign,subtract(normalise(lhs.numerator()),normalise(rhs.multiply(lhs.denominator()))));
			gtz = Logic.and(gtz,new Inequality(false,subtract(Value.Number.ZERO,normalise(lhs.denominator()))));
			ltz = Logic.and(ltz,new Inequality(false,subtract(normalise(lhs.denominator()),Value.Number.ZERO)));
			return Logic.or(ltz,gtz);		
		}
	}		
}
