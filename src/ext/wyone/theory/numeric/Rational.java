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

import wyil.jvm.rt.BigRational;
import wyil.lang.Type;
import wyone.core.*;
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

public final class Rational implements Constructor {		
	private Polynomial numerator;
	private Polynomial denominator;
	
	public Rational(Polynomial i) {
		numerator = i;
		denominator = new Polynomial(1);
	}
	
	public Rational(Polynomial i, Polynomial j) {
		// Now, we attempt to do some simplification here. The main reason is
		// that, if this rational is actually a constant, then we want to know
		// this explicitly. For example, 2x / x -> 2. Thus, we can always
		// compare a rational against a constant value (such as one or zero) and
		// be confident of the result.
		Pair<Polynomial, Polynomial> tmp = i.divide(j);

		if (tmp.second().equals(Polynomial.ZERO)) {
			numerator = tmp.first();
			denominator = Polynomial.ONE;
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
		
	public Polynomial numerator() {
		return numerator;
	}
	
	public Polynomial denominator() {
		return denominator;
	}
	
	public Type type(Solver.State state) {		
		return Type.T_REAL;		
	}
		
	public boolean isConstant() {
		return numerator.isConstant() && denominator.isConstant();
	}
	
	public Value constant() {
		return Value.V_NUM(new BigRational(numerator.constant(),denominator.constant()));
	}
	
	public Set<Constructor> atoms() {
		HashSet<Constructor> atoms = new HashSet<Constructor>(numerator.atoms());
		atoms.addAll(denominator.atoms());
		return atoms;
	}
	
	public List<Constructor> subterms() {
		ArrayList<Constructor> subterms = new ArrayList<Constructor>();
		subterms.addAll(numerator.atoms());
		subterms.addAll(denominator.atoms());
		return subterms;
	}
	
	public boolean isAtom() {
		return numerator.isAtom() && denominator.equals(Polynomial.ONE);
	}
	
	public Constructor atom() {
		return numerator.atom();
	}
	
	public Constructor substitute(Map<Constructor, Constructor> binding) {
		Set<Constructor> vars = numerator.atoms();
		vars.addAll(denominator.atoms());		
		boolean changed = false;
		for(Constructor v : vars) {					
			Constructor val = v.substitute(binding);
			if(val != v) {				
				changed = true;
				break; 
			}
		}
		if(!changed) {			
			return this;
		} else {						
			Constructor num = numerator.expand(binding);
			Constructor den = denominator.expand(binding);			
			return Numerics.divide(num,den);			
		}	
	}		
	
	public Constraint equate(Constructor lhs) {
		// THIS METHOD IS SUCH A CLUDGE RIGHT NOW --- ARRRGGHHH		
		Constructor r;
		if(lhs instanceof Value.Number) {			
			r = Numerics.subtract(this,lhs);			
		} else if(lhs instanceof Variable) {
			Variable v = (Variable) lhs;
			r = subtract(new Polynomial(v));			
		} else if(lhs instanceof Rational) {
			r = subtract((Rational)lhs);
		} else {
			r = subtract(new Polynomial(lhs));
		}
		
		if(r instanceof Rational) {
			Rational rat = (Rational) r;
			
			if(rat.isConstant()) {
				return Value.FALSE; // ? 
			}
			
			Constructor v = rat.atoms().iterator().next();
			Pair<Polynomial,Polynomial> p = rat.rearrangeFor(v);
			
			rat = new Rational(p.first(),p.second());
			if(rat.isAtom()) {
				return new Equality(true,v,rat.atom()); 
			} else if(rat.isConstant()) {
				return new Equality(true,v,rat.constant());
			} else {		
				return new Equality(true,v,rat);
			}
		} else if(r instanceof Variable) {
			Variable v = (Variable) r;
			return new Equality(true,v,Value.ZERO);
		} else {
			return Value.FALSE;
		}
	}
	
	/**
	 * <p>The purpose of this method is to rearrange the rational, such that it now
	 * equals the given atom.  For example, for atom x we have:</p>
	 * <pre>
	 * 2x+y / 1 ==> -y/2
	 * </pre>
	 * 
	 * @param var
	 * @return
	 */
	public Pair<Polynomial,Polynomial> rearrangeFor(Constructor atom) {		
		Polynomial lhs = Polynomial.ZERO;
		Polynomial rhs = Polynomial.ZERO;
		
		for(Term t : numerator) {
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
		
		Pair<Polynomial,Polynomial> p = lhs.factoriseFor(atom);
		rhs = rhs.add(p.second());
		
		return new Pair<Polynomial,Polynomial>(rhs,p.first());
	}
	
	/* =========================================================== */
	/* ========================== ADDITION ======================= */
	/* =========================================================== */
	
	public Rational add(int i) {
		Polynomial top = denominator.multiply(i);
		top = top.add(numerator);
		return new Rational(top, denominator);
	}
	
	public Rational add(BigInteger i) {
		Polynomial top = denominator.multiply(i);
		top = top.add(numerator);
		return new Rational(top, denominator);
	}
	
	public Rational add(Value.Number r) {
		Polynomial top = denominator.multiply(r.numerator());
		top = top.add(numerator.multiply(r.denominator()));
		return new Rational(top, denominator.multiply(r.denominator()));
	}
	
	public Rational add(Term i) {
		Polynomial top = denominator.multiply(i);
		top = top.add(numerator);
		return new Rational(top, denominator);
	}
	
	public Rational add(Polynomial p) {
		Polynomial top = denominator.multiply(p);
		top = top.add(numerator);
		return new Rational(top, denominator);
	}
	
	public Rational add(Rational r) {
		Polynomial top = r.numerator().multiply(denominator);
		top = top.add(numerator.multiply(r.denominator));
		return new Rational(top, denominator.multiply(r.denominator));
	}
	
	/* =========================================================== */
	/* ======================= SUBTRACTION ======================= */
	/* =========================================================== */

	public Rational subtract(int i) {
		Polynomial top = numerator;
		top = top.subtract(denominator.multiply(i));
		return new Rational(top, denominator);
	}
	
	public Rational subtract(Term t) {
		Polynomial top = numerator;
		top = top.subtract(denominator.multiply(t));
		return new Rational(top, denominator);
	}
	
	public Rational subtract(Polynomial p) {
		Polynomial top = numerator;
		top = top.subtract(denominator.multiply(p));
		return new Rational(top, denominator);
	}
	
	public Rational subtract(Rational r) {
		Polynomial top = numerator.multiply(r.denominator);
		top = top.subtract(denominator.multiply(r.numerator));
		return new Rational(top, denominator.multiply(r.denominator));
	}
	
	/* =========================================================== */
	/* ====================== MULTIPLICATION ===================== */
	/* =========================================================== */

	public Rational multiply(int i) {
		Polynomial top = numerator.multiply(i);		
		return new Rational(top, denominator);
	}
	
	public Rational multiply(BigInteger i) {
		Polynomial top = numerator.multiply(i);		
		return new Rational(top, denominator);
	}
	
	public Rational multiply(Term t) {
		Polynomial top = numerator.multiply(t);		
		return new Rational(top, denominator);
	}
	
	public Rational multiply(Polynomial p) {
		Polynomial top = numerator.multiply(p);		
		return new Rational(top, denominator);
	}
	
	public Rational multiply(Value.Number r) {
		Polynomial top = numerator.multiply(r.numerator());		
		return new Rational(top, denominator.multiply(r.denominator()));
	}
	
	public Rational multiply(Rational r) {
		Polynomial top = numerator.multiply(r.numerator());		
		return new Rational(top, denominator.multiply(r.denominator));
	}
	
	/* =========================================================== */
	/* ========================= DIVISION ======================== */
	/* =========================================================== */

	public Rational divide(int r) {
		Polynomial top = numerator;		
		return new Rational(top, denominator.multiply(r));
	}
	
	public Rational divide(BigInteger r) {
		Polynomial top = numerator;		
		return new Rational(top, denominator.multiply(r));
	}
	
	public Rational divide(Term r) {
		Polynomial top = numerator;		
		return new Rational(top, denominator.multiply(r));
	}
	
	public Rational divide(Polynomial r) {
		Polynomial top = numerator;		
		return new Rational(top, denominator.multiply(r));
	}
	
	public Rational divide(Rational r) {
		Polynomial top = numerator.multiply(r.denominator());		
		return new Rational(top, denominator.multiply(r.numerator));
	}
	
	/* =========================================================== */
	/* =========================== OTHER ========================= */
	/* =========================================================== */

	public Rational negate() {		
		return new Rational(numerator.negate(),denominator());
	}
	
	public int compareTo(Constructor e) {
		if (e instanceof Rational) {
			// this is fairly difficult to do for sure.
			Rational r = (Rational) e;
			Polynomial lhs = numerator.multiply(r.denominator);
			Polynomial rhs = r.numerator.multiply(denominator);
			return lhs.compareTo(rhs);
		} else if(CID < e.cid()) {
			return -1;
		} else {
			return 1;
		}
	}

	private final static int CID = Helpers.registerCID();
	public int cid() { return CID; }
	
	public boolean equals(Object o) {
		if(o instanceof Rational) {			
			Rational r = (Rational) o;
			Polynomial lhs = numerator.multiply(r.denominator);
			Polynomial rhs = r.numerator.multiply(denominator);
			return lhs.equals(rhs);
		}
		return false;
	}
	
	public int hashCode() {		
		// we have to be careful here to ensure the Java contract.
		Set<Constructor> fvs = numerator.atoms();
		fvs.addAll(denominator.atoms());
		return fvs.hashCode();
	}
	
	public String toString() {
		if (denominator.equals(Polynomial.ONE)) {
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
}

