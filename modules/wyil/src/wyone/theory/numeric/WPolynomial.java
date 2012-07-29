//This file is part of the Wyone automated theorem prover.
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
import java.math.*;

import wyone.core.*;
import wyone.util.Pair;

/**
 * Provides a general class for representing polynomials. <b>Please note that
 * polynomial division and gcd is experimental and probably contains bugs</b>.
 * 
 * @author djp
 * 
 */
public final class WPolynomial implements Iterable<WTerm>, Comparable<WPolynomial> {
	public static final WPolynomial MTWO = new WPolynomial(-2);
	public static final WPolynomial MONE = new WPolynomial(-1);
	public static final WPolynomial ZERO = new WPolynomial(0);	
	public static final WPolynomial ONE = new WPolynomial(1);
	public static final WPolynomial TWO = new WPolynomial(2);
	public static final WPolynomial THREE = new WPolynomial(3);
	public static final WPolynomial FOUR = new WPolynomial(4);
	public static final WPolynomial FIVE = new WPolynomial(5);	
	public static final WPolynomial TEN = new WPolynomial(10);
	
	// NOTE: ZERO is represented only by the empty set of terms and not, for
	// example, as a single term with no variables and zero coefficient.
	//
	private final HashSet<WTerm> terms;	
	
	public WPolynomial() {
		terms = new HashSet<WTerm>();		
	}
	
	public WPolynomial(int constant) {
		terms = new HashSet<WTerm>();
		if(constant != 0) {
			this.terms.add(new WTerm(constant));
		}
	}
	
	public WPolynomial(BigInteger constant) {
		terms = new HashSet<WTerm>();
		if(!constant.equals(BigInteger.ZERO)) {
			this.terms.add(new WTerm(constant));
		}		
	}
	
	public WPolynomial(WExpr atom) {
		terms = new HashSet<WTerm>();
		terms.add(new WTerm(1,atom));
	}
	
	public WPolynomial(WTerm term) {
		terms = new HashSet<WTerm>();
		if(!term.coefficient().equals(BigInteger.ZERO)) {
			this.terms.add(term);
		}
	}
	
	public WPolynomial(WTerm... terms) {
		this.terms = new HashSet<WTerm>();
		for (WTerm t : terms) {
			if (!t.coefficient().equals(BigInteger.ZERO)) {
				this.terms.add(t);
			}
		}		
	}	
	
	public WPolynomial(Set<WTerm> terms) {
		this.terms = new HashSet<WTerm>();
		for (WTerm t : terms) {
			if (!t.coefficient().equals(BigInteger.ZERO)) {
				this.terms.add(t);
			}
		}			
	}
	
	public WPolynomial(WPolynomial poly) {		
		this.terms = (HashSet<WTerm>) poly.terms.clone();
	}

	/* =========================================================== */
	/* ========================== ACCESSORS ====================== */
	/* =========================================================== */


	public Iterator<WTerm> iterator() {
		return terms.iterator();
	}
	
	public Set<WTerm> terms() {
		return Collections.unmodifiableSet(terms);
	}
	
	public boolean isConstant() {
		for(WTerm e : terms) {
			if(!e.isConstant()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Assumes that isConstant() holds.
	 * @return
	 */
	public BigInteger constant() {
		BigInteger c = BigInteger.ZERO;
		for(WTerm e : terms) {
			c = c.add(e.coefficient());
		}
		return c;
	}
	
	public boolean isLinear() {
		for(WTerm e : terms) {
			if(e.atoms().size() > 1) {
				return false;
			}
		}
		return true;
	}

	public boolean isAtom() {
		if(terms.size() != 1) {
			return false;
		}
		WTerm t = terms.iterator().next();
		if (t.atoms().size() != 1
				|| !t.coefficient().equals(BigInteger.ONE)) {
			return false;
		}
		
		return true;
	}
		
	public WExpr atom() {		
		return terms.iterator().next().atoms().get(0);
	}
	
	public Set<WExpr> atoms() {
		HashSet<WExpr> fvs = new HashSet();
		for(WTerm e : terms) {
			fvs.addAll(e.atoms());			
		}
		return fvs;
	}
	
	/* =========================================================== */
	/* ========================== ADDITION ======================= */
	/* =========================================================== */
	
	public WPolynomial add(int i) {
		return add(new WTerm(i));
	}
	
	public WPolynomial add(BigInteger i) {
		return add(new WTerm(i));
	}
	
	public WPolynomial add(WTerm e) {
		final BigInteger zero = BigInteger.ZERO;
		
		for(WTerm me : terms) {
			if (me.atoms().equals(e.atoms())) {
				BigInteger ncoeff = me.coefficient().add(e.coefficient());
				WPolynomial r = new WPolynomial(this);
				r.terms.remove(me);
				if (!(ncoeff.equals(zero))) {
					r = r.add(new WTerm(ncoeff, me.atoms()));
				}
				return r;
			}
		}		
		if(!e.coefficient().equals(zero)) {
			WPolynomial r = new WPolynomial(this);		
			r.terms.add(e);
			return r;		
		} else {
			return this;
		}
	}
	
	public WPolynomial add(WPolynomial poly) {
		WPolynomial r = this;
		for(WTerm e : poly.terms) {
			r = r.add(e);
		}
		return r;
	}		
	
	/* =========================================================== */
	/* ========================= SUBTRACTION ===================== */
	/* =========================================================== */
	
	public WPolynomial subtract(int i) {
		return subtract(new WTerm(i));
	}
	
	public WPolynomial subtract(BigInteger i) {
		return subtract(new WTerm(i));
	}
	
	public WPolynomial subtract(WTerm e) {
		WTerm ne = new WTerm(e.coefficient().negate(), e.atoms());
		return add(ne);
	}
	

	public WPolynomial subtract(WPolynomial poly) {
		WPolynomial r = this;
		for (WTerm e : poly.terms) {			
			r = r.subtract(e);
		}
		return r;
	}	
				
	/* =========================================================== */
	/* ======================= MULTIPLICATION ==================== */
	/* =========================================================== */
	
	public WPolynomial multiply(int i) {
		return multiply(new WTerm(i));		
	}
	
	public WPolynomial multiply(BigInteger i) {
		return multiply(new WTerm(i));		
	}
	
	public WPolynomial multiply(WTerm e1) {
		WPolynomial r = new WPolynomial();
		for (WTerm e2 : terms) {
			r = r.add(e1.multiply(e2));
		}	
		return r;
	}
	
	public WPolynomial multiply(WPolynomial poly) {
		WPolynomial r = new WPolynomial();
		for (WTerm e : poly.terms) {			
			r = r.add(this.multiply(e));
		}
		return r;
	}		
	
	/* =========================================================== */
	/* ============================ GCD ========================== */
	/* =========================================================== */

	
	/**
	 * <p>
	 * This method computes the Greatest Common Divisor of this Polynomial and
	 * the supplied polynomial. That is, the "biggest" polynomial that divides
	 * evenly into both polynomials. For example:
	 * </p>
	 * 
	 * <pre>
	 * gcd(2x,x) = x
	 * gcd(2x+1,x) = 1
	 * </pre>
	 * 
	 * <p>
	 * In the special case that both polynomials are constants, then it simply
	 * resolves to the normal gcd operation.
	 * </p>
	 * <p>
	 * For more information, see for example this <a
	 * href="http://en.wikipedia.org/wiki/Greatest_common_divisor_of_two_polynomials">Wikipedia
	 * page</a>.
	 * </p>
	 * <b>NOTE: THE IMPLEMENTATION OF THIS METHOD IS CURRENTLY BUGGY. IN
	 * PARTICULAR, IT CAN RETURN NEGATIVE INTEGERS UNLIKE NORMAL GCD</b>
	 */
	public WPolynomial gcd(WPolynomial a) {		
		final WPolynomial zero = WPolynomial.ZERO;
		WPolynomial b = this;
		WPolynomial c;				
						
		// First, decide the right way around for a + b
		
		// BUG HERE: currently there is a bug here, since it doesn't always make
		// the right choice. In particular, if both polynomials are in fact
		// integers, then it doesn't always pick the largest.
		Pair<WPolynomial,WPolynomial> r = a.divide(b);				
		
		if(r.first().equals(zero)) {			
			r = a.divide(b);
			if(r.first().equals(zero)) {
				// a + b are mutually indivisible
				return WPolynomial.ONE;
			} else {
				// b is divisible by a, but not the other way around.
				c = a;
				a = b;
				b = c;
			}
		}		
		
		while (!b.equals(WPolynomial.ZERO)) {			
			r = a.divide(b); 
			c = r.second();			
			if (c.equals(WPolynomial.ZERO)) {
				return b;
			} else if(r.first().equals(WPolynomial.ZERO)) {
				// no further division is possible.
				return b;
			}
			a = b;
			b = c;
		}
		return a;
	}
		
	/**
	 * This method divides this polynomial by the term argument using simple
	 * division. The method produces the pair (quotient,remainder). For example:
	 * 
	 * <pre>
	 * (x + 2xy) / x = (1+2y,0)
	 * (x + 2xy) / y = (2x,x)
	 * 
	 * &#064;param x
	 * &#064;return
	 * 
	 * For more information on polynomial division see: &lt;a href=&quot;http://en.wikipedia.org/wiki/Polynomial_long_division&quot;&gt;wikipedia&lt;/a&gt;
	 * 
	 */
	public Pair<WPolynomial,WPolynomial> divide(WTerm t1) {
		WPolynomial quotient = new WPolynomial(0);
		WPolynomial remainder = new WPolynomial(0);
		
		for(WTerm t2 : terms) {
			Pair<WTerm,WTerm> r = t2.divide(t1);
			quotient = quotient.add(r.first());
			remainder = remainder.add(r.second());
		}
		
		return new Pair(quotient,remainder);
	}
	
	/**
	 * This method divides this polynomial by the polynomial argument using long
	 * division. The method produces the pair (quotient,remainder). For example:
	 * 
	 * <pre>
	 * (x + 2xy) / x = (1+2y,0)
	 * (x + 2xy) / y = (2x,x)
	 * 
	 * &#064;param x
	 * &#064;return
	 * 
	 * For more information on polynomial long division see: &lt;a href=&quot;http://en.wikipedia.org/wiki/Polynomial_long_division&quot;&gt;wikipedia&lt;/a&gt;
	 * 
	 */
	public Pair<WPolynomial,WPolynomial> divide(WPolynomial x) {
		
		// Ok, yes, this piece of code is horribly inefficient. But, it's tough
		// even to make it work properly, let alone make it work fast.

		WTerm max = null;
		
		for(WTerm t : x) {
			if (max == null || max.compareTo(t) > 0) {
				max = t;
			}
		}	

		if (max == null) {
			// this indicates an attempt at division by zero!
			throw new ArithmeticException("polynomial division by zero");
		}
		
		ArrayList<WTerm> myterms = new ArrayList<WTerm>(terms);		
		Collections.sort(myterms);
		
		for(WTerm t1 : myterms) {
			Pair<WTerm,WTerm> d = t1.divide(max);	
			if(!d.first().equals(WTerm.ZERO)) {			
				WTerm quotient = d.first();	

				WPolynomial remainder = this.subtract(x.multiply(quotient));				

				Pair<WPolynomial,WPolynomial> r = remainder.divide(x);										
				return new Pair(r.first().add(quotient),r.second());
			}	
		}
		
		// base case for recursion.
		return new Pair(WPolynomial.ZERO,this);
	}
	
	/* =========================================================== */
	/* ========================== NEGATION ======================= */
	/* =========================================================== */

	public WPolynomial negate() {
		WPolynomial r = new WPolynomial(0);
		
		for(WTerm t : terms) {
			r.terms.add(t.negate());
		}
		
		return r;
	}		

	/* =========================================================== */
	/* ======================= FACTORISATION ===================== */
	/* =========================================================== */

	/**
	 * The purpose of this method is to factorise the polynomial for a given
	 * variable, producing a factor and a remainder. For example:
	 * 
	 * <pre>
	 * 2x + xy + 2 ======&gt; (2+y, 2) 
	 * </pre>
	 * 
	 * Here, <code>2+y</code> is the factor, whilst <code>2</code> is the
	 * remainder. Thus, <code>x * (2+y) + 2</code> yields the original
	 * polynomial.
	 * 
	 * Notice, that in the case where the variable in question is raised to a
	 * power, then the factor will contain the original variable. For example:
	 * 
	 * <pre>
	 * 2x&circ;2 + xy + 2 ======&gt; (2x+y, 2) 
	 * </pre>
	 */
	public Pair<WPolynomial,WPolynomial> factoriseFor(WExpr atom) {
		WPolynomial factor = new WPolynomial(0);
		WPolynomial remainder = new WPolynomial(0);
		
		for(WTerm t : terms) {
			if(t.atoms().contains(atom)) {
				ArrayList<WExpr> atoms = new ArrayList(t.atoms());
				atoms.remove(atom); // remove one instance of var only
				factor = factor.add(new WTerm(t.coefficient(),atoms));
			} else {
				remainder = remainder.add(t);
			}
		}
		
		return new Pair(factor,remainder);
	}
	
	/* =========================================================== */
	/* ============================ OTHER ======================== */
	/* =========================================================== */

	public int compareTo(WPolynomial p) {
		Collection<WTerm> p_terms = p.terms;
		
		if(terms.size() < p_terms.size()) {
			return -1;
		} else if(terms.size() > p_terms.size()) {
			return 1;
		}
		
		Iterator<WTerm> mi = terms.iterator();
		Iterator<WTerm> pi = p_terms.iterator();
		while(mi.hasNext()) {		
			WTerm mt = mi.next();
			WTerm pt = pi.next();
			int c = mt.compareTo(pt);
			if(c != 0) {
				return c;
			}
		}
		return 0;
	}
	
	public boolean equals(Object o) {
		if(o instanceof WPolynomial) {
			WPolynomial p = (WPolynomial) o;
			return p.terms.equals(terms);
		}
		return false;
	}
	
	public int hashCode() {
		return terms.hashCode();
	}
	
	public String toString() {
		if(terms.isEmpty()) {
			return "0";
		}
		
		String r = "";
		boolean firstTime=true;	
		if(terms.size() > 1) {
			r += "(";
		}
		for (WTerm e : terms) {			
			if (!firstTime) {
				if (e.coefficient().compareTo(BigInteger.ZERO) > 0) {
					r += "+";					
				}
			} 			
			boolean ffirstTime = true;
			
			// yugly.
			if (!e.coefficient().equals(BigInteger.ONE.negate())
					&& (!e.coefficient().equals(BigInteger.ONE))
					|| e.atoms().isEmpty()) {
				firstTime = false;
				r += e.coefficient();
			} else if (e.coefficient().equals(BigInteger.ONE.negate())) {
				firstTime = false;
				r += "-";
			} else if (e.atoms().size() > 0) {
				firstTime = false;
			}
			for (WExpr v : e.atoms()) {
				if (!ffirstTime) {
					r += "*";
				}
				ffirstTime = false;
				r += v;
			}
		}
		if(terms.size() > 1) {
			r += ")";
		}
		return r;
	}
	
	/**
     * This method substitutes all variable names for names given in the
     * binding. If no binding is given for a variable, then it retains its
     * original name. Guarantees to return same object if not substitution
     * performed.
     * 
     * @param environment
     * @return
     */
	public WPolynomial substitute(Map<WVariable,WVariable> environment) {
		WPolynomial r = new WPolynomial();
		boolean changed=false;
		for (WTerm e : terms) {
			ArrayList<WExpr> nvars = new ArrayList<WExpr>();
			for (WExpr v : e.atoms()) {
				if(environment.containsKey(v)) {
					nvars.add(environment.get(v));
					changed=true;
				} else {
					nvars.add(v);
				}
			}
			r = r.add(new WTerm(e.coefficient(), nvars));
		}
		if(changed) {
			return r;
		} else {
			return this;
		}
	}	
	
	public WExpr expand(Map<WExpr,WExpr> binding) {
		WExpr r = WNumber.ZERO;
		for(WTerm t : terms) {			
			r = WNumerics.add(r,t.expand(binding));			
		}
		return r;
	}	
}
