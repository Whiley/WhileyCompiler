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

import java.math.*;
import java.util.*;

import wyone.core.*;
import wyone.theory.congruence.*;
import wyone.theory.logic.*;
import wyone.util.*;

/**
 * <p>
 * A rational object represents a rational number. Examples include:
 * </p>
 * 
 * <pre>
 *  
 * 1/x
 * x/y
 * 1/3
 * (10x+6)/(y+1)
 * </pre>
 * 
 * <b>Please note that this class exploits <code>Polynomial.divide()</code>
 * and <code>Polynomial.gcd()</code>, which is experimental and probably
 * contains bugs</b>.
 * 
 * @author djp
 * 
 */

public final class WRational implements WExpr {		
	private WPolynomial numerator;
	private WPolynomial denominator;
	
	public WRational(WPolynomial i) {
		numerator = i;
		denominator = new WPolynomial(1);
	}
	
	public WRational(WPolynomial i, WPolynomial j) {
		// Now, we attempt to do some simplification here. The main reason is
		// that, if this rational is actually a constant, then we want to know
		// this explicitly. For example, 2x / x -> 2. Thus, we can always
		// compare a rational against a constant value (such as one or zero) and
		// be confident of the result.
		Pair<WPolynomial, WPolynomial> tmp = i.divide(j);

		if (tmp.second().equals(WPolynomial.ZERO)) {
			numerator = tmp.first();
			denominator = WPolynomial.ONE;
		} else {
			// FIXME: we may require better normalisation here ?
			if (j.isConstant() && j.constant().compareTo(BigInteger.ZERO) < 0) {
				numerator = i.negate();
				denominator = j.negate();
			} else {
				numerator = i;
				denominator = j;
			}
		}
	}
		
	public WPolynomial numerator() {
		return numerator;
	}
	
	public WPolynomial denominator() {
		return denominator;
	}
	
	public WType type(SolverState state) {		
		return WRealType.T_REAL;		
	}
		
	public boolean isConstant() {
		return numerator.isConstant() && denominator.isConstant();
	}
	
	public WNumber constant() {
		return new WNumber(numerator.constant(),denominator.constant());
	}
	
	public Set<WExpr> atoms() {
		HashSet<WExpr> atoms = new HashSet<WExpr>(numerator.atoms());
		atoms.addAll(denominator.atoms());
		return atoms;
	}
	
	public List<WExpr> subterms() {
		ArrayList<WExpr> subterms = new ArrayList<WExpr>();
		subterms.addAll(numerator.atoms());
		subterms.addAll(denominator.atoms());
		return subterms;
	}
	
	public boolean isAtom() {
		return numerator.isAtom() && denominator.equals(WPolynomial.ONE);
	}
	
	public WExpr atom() {
		return numerator.atom();
	}
	
	public WExpr substitute(Map<WExpr, WExpr> binding) {
		Set<WExpr> vars = numerator.atoms();
		vars.addAll(denominator.atoms());		
		boolean changed = false;
		for(WExpr v : vars) {					
			WExpr val = v.substitute(binding);
			if(val != v) {				
				changed = true;
				break; 
			}
		}
		if(!changed) {			
			return this;
		} else {						
			WExpr num = numerator.expand(binding);
			WExpr den = denominator.expand(binding);			
			return WNumerics.divide(num,den);			
		}	
	}		
	
	public WLiteral rearrange(WExpr lhs) {
		// THIS METHOD IS SUCH A CLUDGE RIGHT NOW --- ARRRGGHHH
		WExpr r;
		if(lhs instanceof WNumber) {
			r = WNumerics.subtract(this,lhs);
		} else if(lhs instanceof WVariable) {
			WVariable v = (WVariable) lhs;
			r = subtract(new WPolynomial(v));			
		} else if(lhs instanceof WRational) {
			r = subtract((WRational)lhs);
		} else {
			r = subtract(new WPolynomial(lhs));
		}
		
		if(r instanceof WRational) {
			WRational rat = (WRational) r;
			
			if(rat.isConstant()) {
				return WBool.FALSE; // ? 
			}
			
			WExpr v = rat.atoms().iterator().next();
			Pair<WPolynomial,WPolynomial> p = rat.rearrangeFor(v);
			
			rat = new WRational(p.first(),p.second());
			if(rat.isAtom()) {
				return new WEquality(true,v,rat.atom()); 
			} else if(rat.isConstant()) {
				return new WEquality(true,v,rat.constant());
			} else {		
				return new WEquality(true,v,rat);
			}
		} else if(r instanceof WVariable) {
			WVariable v = (WVariable) r;
			return new WEquality(true,v,WNumber.ZERO);
		} else {
			return WBool.FALSE;
		}
	}
	
	/**
	 * <p>The purpose of this method is to rearrange the rational, such that it now
	 * equals the given atom.  For example:</p>
	 * <pre>
	 * 2x+y / 1 ==> -y/2
	 * </pre>
	 * 
	 * @param var
	 * @return
	 */
	public Pair<WPolynomial,WPolynomial> rearrangeFor(WExpr atom) {		
		WPolynomial lhs = WPolynomial.ZERO;
		WPolynomial rhs = WPolynomial.ZERO;
		
		for(WTerm t : numerator) {
			if(t.atoms().contains(atom)) {
				lhs = lhs.subtract(t); 				
			} else {
				rhs = rhs.add(t);
			}
		}
		
		// FIXME: originally, I had lhs = lhs.multiply(denominator)
		// this doesn't seem to work because I'm basically multiplying both
		// sides by the denominator [I think]. There will probably be a bug if
		// the denominator contains the variable, but not the numerator.
		
		Pair<WPolynomial,WPolynomial> p = lhs.factoriseFor(atom);
		rhs = rhs.add(p.second());
		
		return new Pair<WPolynomial,WPolynomial>(rhs,p.first());
	}
	
	/* =========================================================== */
	/* ========================== ADDITION ======================= */
	/* =========================================================== */
	
	public WRational add(int i) {
		WPolynomial top = denominator.multiply(i);
		top = top.add(numerator);
		return new WRational(top, denominator);
	}
	
	public WRational add(BigInteger i) {
		WPolynomial top = denominator.multiply(i);
		top = top.add(numerator);
		return new WRational(top, denominator);
	}
	
	public WRational add(WNumber r) {
		WPolynomial top = denominator.multiply(r.numerator());
		top = top.add(numerator.multiply(r.denominator()));
		return new WRational(top, denominator.multiply(r.denominator()));
	}
	
	public WRational add(WTerm i) {
		WPolynomial top = denominator.multiply(i);
		top = top.add(numerator);
		return new WRational(top, denominator);
	}
	
	public WRational add(WPolynomial p) {
		WPolynomial top = denominator.multiply(p);
		top = top.add(numerator);
		return new WRational(top, denominator);
	}
	
	public WRational add(WRational r) {
		WPolynomial top = r.numerator().multiply(denominator);
		top = top.add(numerator.multiply(r.denominator));
		return new WRational(top, denominator.multiply(r.denominator));
	}
	
	/* =========================================================== */
	/* ======================= SUBTRACTION ======================= */
	/* =========================================================== */

	public WRational subtract(int i) {
		WPolynomial top = numerator;
		top = top.subtract(denominator.multiply(i));
		return new WRational(top, denominator);
	}
	
	public WRational subtract(WTerm t) {
		WPolynomial top = numerator;
		top = top.subtract(denominator.multiply(t));
		return new WRational(top, denominator);
	}
	
	public WRational subtract(WPolynomial p) {
		WPolynomial top = numerator;
		top = top.subtract(denominator.multiply(p));
		return new WRational(top, denominator);
	}
	
	public WRational subtract(WRational r) {
		WPolynomial top = numerator.multiply(r.denominator);
		top = top.subtract(denominator.multiply(r.numerator));
		return new WRational(top, denominator.multiply(r.denominator));
	}
	
	/* =========================================================== */
	/* ====================== MULTIPLICATION ===================== */
	/* =========================================================== */

	public WRational multiply(int i) {
		WPolynomial top = numerator.multiply(i);		
		return new WRational(top, denominator);
	}
	
	public WRational multiply(BigInteger i) {
		WPolynomial top = numerator.multiply(i);		
		return new WRational(top, denominator);
	}
	
	public WRational multiply(WTerm t) {
		WPolynomial top = numerator.multiply(t);		
		return new WRational(top, denominator);
	}
	
	public WRational multiply(WPolynomial p) {
		WPolynomial top = numerator.multiply(p);		
		return new WRational(top, denominator);
	}
	
	public WRational multiply(WNumber r) {
		WPolynomial top = numerator.multiply(r.numerator());		
		return new WRational(top, denominator.multiply(r.denominator()));
	}
	
	public WRational multiply(WRational r) {
		WPolynomial top = numerator.multiply(r.numerator());		
		return new WRational(top, denominator.multiply(r.denominator));
	}
	
	/* =========================================================== */
	/* ========================= DIVISION ======================== */
	/* =========================================================== */

	public WRational divide(int r) {
		WPolynomial top = numerator;		
		return new WRational(top, denominator.multiply(r));
	}
	
	public WRational divide(BigInteger r) {
		WPolynomial top = numerator;		
		return new WRational(top, denominator.multiply(r));
	}
	
	public WRational divide(WTerm r) {
		WPolynomial top = numerator;		
		return new WRational(top, denominator.multiply(r));
	}
	
	public WRational divide(WPolynomial r) {
		WPolynomial top = numerator;		
		return new WRational(top, denominator.multiply(r));
	}
	
	public WRational divide(WRational r) {
		WPolynomial top = numerator.multiply(r.denominator());		
		return new WRational(top, denominator.multiply(r.numerator));
	}
	
	/* =========================================================== */
	/* =========================== OTHER ========================= */
	/* =========================================================== */

	public WRational negate() {		
		return new WRational(numerator.negate(),denominator());
	}
	
	public int compareTo(WExpr e) {
		if (e instanceof WRational) {
			// this is fairly difficult to do for sure.
			WRational r = (WRational) e;
			WPolynomial lhs = numerator.multiply(r.denominator);
			WPolynomial rhs = r.numerator.multiply(denominator);
			return lhs.compareTo(rhs);
		} else if(CID < e.cid()) {
			return -1;
		} else {
			return 1;
		}
	}
	
	public boolean equals(Object o) {
		if(o instanceof WRational) {			
			WRational r = (WRational) o;
			WPolynomial lhs = numerator.multiply(r.denominator);
			WPolynomial rhs = r.numerator.multiply(denominator);
			return lhs.equals(rhs);
		}
		return false;
	}
	
	public int hashCode() {		
		// we have to be careful here to ensure the Java contract.
		Set<WExpr> fvs = numerator.atoms();
		fvs.addAll(denominator.atoms());
		return fvs.hashCode();
	}
	
	public String toString() {
		if (denominator.equals(WPolynomial.ONE)) {
			return numerator.toString();
		} else if (numerator.isConstant() && denominator.isConstant()) {
			return numerator + " / " + denominator;
		} else if (numerator.isConstant()) {
			return numerator + " / (" + denominator + ")";
		} else if (denominator.isConstant()) {
			return "(" + numerator + ") / " + denominator;
		} else {
			return "(" + numerator + ") / (" + denominator + ")";
		}
	}
	
	private final static int CID = WExprs.registerCID();
	public int cid() { return CID; }
}

