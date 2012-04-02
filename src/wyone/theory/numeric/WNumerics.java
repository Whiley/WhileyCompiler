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
import wyone.theory.logic.WBool;
import wyone.theory.logic.WFormula;
import wyone.theory.logic.WFormulas;
import wyone.theory.logic.WLiteral;
import wyone.util.Pair;

public class WNumerics {

	private static WExpr normalise(WRational r) {
		if(r.isConstant()) {
			return r.constant();
		} else if(r.isAtom()) {
			return r.atom();
		} else {
			return r;
		}
	}
	
	public static WExpr normalise(WPolynomial p) {
		if(p.isConstant()) {
			return new WNumber(p.constant());
		} else if(p.isAtom()) {
			return p.atom();
		} else {
			return new WRational(p);
		}
	}	
	
	public static WRational rational(WExpr r) {
		if(r instanceof WNumber) {
			WNumber n = (WNumber) r;
			return new WRational(new WPolynomial(n.numerator()),new WPolynomial(n.denominator()));
		} else if(r instanceof WRational) {
			return (WRational) r;
		} else  {			
			return new WRational(new WPolynomial(r));
		} 
	}
	
	public static WExpr negate(WExpr expr) {
		if(expr instanceof WNumber) {
			return ((WNumber)expr).negate();
		} else if(expr instanceof WRational) {
			// must be a rational
			WRational r = (WRational) expr;
			return normalise(r.multiply(-1));
		} else {
			// something else
			return new WRational(new WPolynomial(expr)).multiply(-1);
		} 
	}
		
	public static WExpr add(WExpr lhs, WExpr rhs) {
		if (lhs instanceof WNumber && rhs instanceof WNumber) {
			WNumber l = (WNumber) lhs;
			WNumber r = (WNumber) rhs;
			return l.add(r);
		} else {
			return normalise(rational(lhs).add(rational(rhs)));
		}		
	}
	
	public static WExpr subtract(WExpr lhs, WExpr rhs) {
		if (lhs instanceof WNumber && rhs instanceof WNumber) {
			WNumber l = (WNumber) lhs;
			WNumber r = (WNumber) rhs;
			return l.subtract(r);
		} else {
			return normalise(rational(lhs).subtract(rational(rhs)));
		}
	}
	
	public static WExpr multiply(WExpr lhs, WExpr rhs) {
		if (lhs instanceof WNumber && rhs instanceof WNumber) {
			WNumber l = (WNumber) lhs;
			WNumber r = (WNumber) rhs;
			return l.multiply(r);
		} else {
			return normalise(rational(lhs).multiply(rational(rhs)));
		}
	}
	
	public static WExpr divide(WExpr lhs, WExpr rhs) {
		if (lhs instanceof WNumber && rhs instanceof WNumber) {
			WNumber l = (WNumber) lhs;
			WNumber r = (WNumber) rhs;
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
	public static Pair<WExpr,WExpr> rearrangeFor(WExpr atom, WInequality eq) {	
		WExpr rhs = eq.rhs();	
				
		if(rhs.equals(atom)) {
			return new Pair<WExpr,WExpr>(WNumber.ZERO,WNumber.MONE);
		} else if(rhs instanceof WRational){
			WRational r = (WRational) rhs;
			Pair<WPolynomial,WPolynomial> p = r.rearrangeFor(atom);			
			WExpr l = normalise(new WRational(p.first()));
			WExpr f = normalise(new WRational(p.second()));
			return new Pair<WExpr,WExpr>(l,f);
		} else  {			
			return new Pair<WExpr,WExpr>(rhs,WNumber.ZERO);
		} 	
	}
				
	public static WFormula lessThan(WExpr lhs, WExpr rhs) {
		return inequals(false,rhs,lhs).substitute(Collections.EMPTY_MAP);
	}
	
	public static WFormula lessThanEq(WExpr lhs, WExpr rhs) {		
		return inequals(true,lhs,rhs).substitute(Collections.EMPTY_MAP);
	}
	
	public static WFormula greaterThan(WExpr lhs, WExpr rhs) {
		return inequals(false,lhs,rhs).substitute(Collections.EMPTY_MAP);
	}
	
	public static WFormula greaterThanEq(WExpr lhs, WExpr rhs) {
		return inequals(true,rhs,lhs).substitute(Collections.EMPTY_MAP);
	}
	
	private static WFormula inequals(boolean sign, WExpr lhs, WExpr rhs) {
		return inequals(sign, rational(lhs),rational(rhs));
	}
	
	private static WFormula inequals(boolean sign, WRational lhs, WRational rhs) {		
		if(lhs.denominator().equals(WPolynomial.ONE)) {		
			return inequals(sign,lhs.numerator(),rhs);
		} else if(rhs.denominator().equals(WPolynomial.ONE)) {
			return inequals(sign,lhs,rhs.numerator());
		} else if(lhs.denominator().isConstant()) {			
			BigInteger constant = lhs.denominator().constant();			
			int r = constant.compareTo(BigInteger.ZERO);
			if(r < 0) {
				return inequals(sign,rhs.multiply(constant),lhs.numerator());
			} else if (r > 0) {
				return inequals(sign,lhs.numerator(),rhs.multiply(constant));
			} else {
				return WBool.FALSE;
			}
		} else if(rhs.denominator().isConstant()) {			
			BigInteger constant = rhs.denominator().constant();					
			int r = constant.compareTo(BigInteger.ZERO);
			if(r < 0) {
				return inequals(sign,rhs.denominator(),lhs.multiply(constant));
			} else if (r > 0) {
				return inequals(sign,lhs.multiply(constant),rhs.denominator());
			} else {
				return WBool.FALSE;
			}			
		} else {
			// Ok, can't break down any more ... so disjunction required			
			WFormula gtz = inequals(sign,lhs.numerator(),rhs.multiply(lhs.denominator()));
			WFormula ltz = inequals(sign,rhs.multiply(lhs.denominator()),lhs.numerator());
			gtz = WFormulas.and(gtz,new WInequality(false,subtract(WNumber.ZERO,normalise(lhs.denominator()))));
			ltz = WFormulas.and(ltz,new WInequality(false,subtract(normalise(lhs.denominator()),WNumber.ZERO)));
			return WFormulas.or(ltz,gtz);			
		}
	}	
	
	private static WFormula inequals(boolean sign, WPolynomial lhs, WRational rhs) {				
		if(rhs.denominator().isConstant()) {
			BigInteger constant = rhs.denominator().constant();
			int r = constant.compareTo(BigInteger.ZERO);
			if(r < 0) {
				return new WInequality(sign,subtract(normalise(rhs.numerator()),normalise(lhs.multiply(constant))));
			} else if (r > 0) {				
				return new WInequality(sign,subtract(normalise(rhs.numerator()),normalise(lhs.multiply(constant))));
			} else {
				return WBool.FALSE;
			}			
		} else {
			WFormula gtz = new WInequality(sign,subtract(normalise(lhs.multiply(rhs.denominator())),normalise(rhs.numerator())));
			WFormula ltz = new WInequality(sign,subtract(normalise(rhs.numerator()),normalise(lhs.multiply(rhs.denominator()))));
			gtz = WFormulas.and(gtz,new WInequality(false,subtract(WNumber.ZERO,normalise(rhs.denominator()))));
			ltz = WFormulas.and(ltz,new WInequality(false,subtract(normalise(rhs.denominator()),WNumber.ZERO)));
			return WFormulas.or(ltz,gtz);	
		}
	}
	
	private static WFormula inequals(boolean sign, WRational lhs, WPolynomial rhs) {		
		if(lhs.denominator().isConstant()) {			
			BigInteger constant = lhs.denominator().constant();
			int r = constant.compareTo(BigInteger.ZERO);
			if(r < 0) {				
				return new WInequality(sign,subtract(normalise(lhs.numerator()),normalise(rhs.multiply(constant))));
			} else if (r > 0) {				
				return new WInequality(sign,subtract(normalise(rhs.multiply(constant)),normalise(lhs.numerator())));
			} else {
				return WBool.FALSE;
			}
		} else {
			WFormula gtz = new WInequality(sign,subtract(normalise(rhs.multiply(lhs.denominator())),normalise(lhs.numerator())));
			WFormula ltz = new WInequality(sign,subtract(normalise(lhs.numerator()),normalise(rhs.multiply(lhs.denominator()))));
			gtz = WFormulas.and(gtz,new WInequality(false,subtract(WNumber.ZERO,normalise(lhs.denominator()))));
			ltz = WFormulas.and(ltz,new WInequality(false,subtract(normalise(lhs.denominator()),WNumber.ZERO)));
			return WFormulas.or(ltz,gtz);		
		}
	}		
}
