// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyrl.util;

import java.util.*;
import java.math.*;

/**
 * Provides a general class for representing polynomials. <b>Please note that
 * polynomial division and gcd is experimental and probably contains bugs</b>.
 *
 * @author David J. Pearce
 *
 */
public final class Polynomial implements Iterable<Polynomial.Term>,
		Comparable<Polynomial> {
	public static final Polynomial MTWO = new Polynomial(-2);
	public static final Polynomial MONE = new Polynomial(-1);
	public static final Polynomial ZERO = new Polynomial(0);
	public static final Polynomial ONE = new Polynomial(1);
	public static final Polynomial TWO = new Polynomial(2);
	public static final Polynomial THREE = new Polynomial(3);
	public static final Polynomial FOUR = new Polynomial(4);
	public static final Polynomial FIVE = new Polynomial(5);
	public static final Polynomial TEN = new Polynomial(10);

	// NOTE: ZERO is represented only by the empty set of terms and not, for
	// example, as a single term with no variables and zero coefficient.
	private final HashSet<Term> terms;

	public Polynomial() {
		terms = new HashSet<Term>();
	}

	public Polynomial(int constant) {
		terms = new HashSet<Term>();
		if (constant != 0) {
			this.terms.add(new Term(constant));
		}
	}

	public Polynomial(BigInteger constant) {
		terms = new HashSet<Term>();
		if (!constant.equals(BigInteger.ZERO)) {
			this.terms.add(new Term(constant));
		}
	}

	public Polynomial(String atom) {
		terms = new HashSet<Term>();
		terms.add(new Term(1, atom));
	}

	public Polynomial(Term term) {
		terms = new HashSet<Term>();
		if (!term.coefficient().equals(BigInteger.ZERO)) {
			this.terms.add(term);
		}
	}

	public Polynomial(Term... terms) {
		this.terms = new HashSet<Term>();
		for (Term t : terms) {
			if (!t.coefficient().equals(BigInteger.ZERO)) {
				this.terms.add(t);
			}
		}
	}

	public Polynomial(Set<Term> terms) {
		this.terms = new HashSet<Term>();
		for (Term t : terms) {
			if (!t.coefficient().equals(BigInteger.ZERO)) {
				this.terms.add(t);
			}
		}
	}

	public Polynomial(Polynomial poly) {
		this.terms = (HashSet<Term>) poly.terms.clone();
	}

	/* =========================================================== */
	/* ========================== ACCESSORS ====================== */
	/* =========================================================== */

	public Iterator<Term> iterator() {
		return terms.iterator();
	}

	public Set<Term> terms() {
		return Collections.unmodifiableSet(terms);
	}

	public boolean isConstant() {
		for (Term e : terms) {
			if (!e.isConstant()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Assumes that isConstant() holds.
	 *
	 * @return
	 */
	public BigInteger constant() {
		BigInteger c = BigInteger.ZERO;
		for (Term e : terms) {
			c = c.add(e.coefficient());
		}
		return c;
	}

	public boolean isLinear() {
		for (Term e : terms) {
			if (e.atoms().size() > 1) {
				return false;
			}
		}
		return true;
	}

	public boolean isAtom() {
		if (terms.size() != 1) {
			return false;
		}
		Term t = terms.iterator().next();
		if (t.atoms().size() != 1 || !t.coefficient().equals(BigInteger.ONE)) {
			return false;
		}

		return true;
	}

	public String atom() {
		return terms.iterator().next().atoms().get(0);
	}

	public Set<String> atoms() {
		HashSet<String> fvs = new HashSet();
		for (Term e : terms) {
			fvs.addAll(e.atoms());
		}
		return fvs;
	}

	// ===========================================================
	// ADDITION
	// ===========================================================

	public Polynomial add(int i) {
		return add(new Term(i));
	}

	public Polynomial add(BigInteger i) {
		return add(new Term(i));
	}

	public Polynomial add(Term e) {
		final BigInteger zero = BigInteger.ZERO;

		for (Term me : terms) {
			if (me.atoms().equals(e.atoms())) {
				BigInteger ncoeff = me.coefficient().add(e.coefficient());
				Polynomial r = new Polynomial(this);
				r.terms.remove(me);
				if (!(ncoeff.equals(zero))) {
					r = r.add(new Term(ncoeff, me.atoms()));
				}
				return r;
			}
		}
		if (!e.coefficient().equals(zero)) {
			Polynomial r = new Polynomial(this);
			r.terms.add(e);
			return r;
		} else {
			return this;
		}
	}

	public Polynomial add(Polynomial poly) {
		Polynomial r = this;
		for (Term e : poly.terms) {
			r = r.add(e);
		}
		return r;
	}

	// ===========================================================
	// SUBTRACTION
	// ===========================================================

	public Polynomial subtract(int i) {
		return subtract(new Term(i));
	}

	public Polynomial subtract(BigInteger i) {
		return subtract(new Term(i));
	}

	public Polynomial subtract(Term e) {
		Term ne = new Term(e.coefficient().negate(), e.atoms());
		return add(ne);
	}

	public Polynomial subtract(Polynomial poly) {
		Polynomial r = this;
		for (Term e : poly.terms) {
			r = r.subtract(e);
		}
		return r;
	}

	// ===========================================================
	// MULTIPLICATION
	// ===========================================================

	public Polynomial multiply(int i) {
		return multiply(new Term(i));
	}

	public Polynomial multiply(BigInteger i) {
		return multiply(new Term(i));
	}

	public Polynomial multiply(Term e1) {
		Polynomial r = new Polynomial();
		for (Term e2 : terms) {
			r = r.add(e1.multiply(e2));
		}
		return r;
	}

	public Polynomial multiply(Polynomial poly) {
		Polynomial r = new Polynomial();
		for (Term e : poly.terms) {
			r = r.add(this.multiply(e));
		}
		return r;
	}

	// ===========================================================
	// GCD
	// ===========================================================

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
	 * For more information, see for example this <a href=
	 * "http://en.wikipedia.org/wiki/Greatest_common_divisor_of_two_polynomials"
	 * >Wikipedia page</a>.
	 * </p>
	 * <b>NOTE: THE IMPLEMENTATION OF THIS METHOD IS CURRENTLY BUGGY. IN
	 * PARTICULAR, IT CAN RETURN NEGATIVE INTEGERS UNLIKE NORMAL GCD</b>
	 */
	public Polynomial gcd(Polynomial a) {
		final Polynomial zero = Polynomial.ZERO;
		Polynomial b = this;
		Polynomial c;

		// First, decide the right way around for a + b

		// BUG HERE: currently there is a bug here, since it doesn't always make
		// the right choice. In particular, if both polynomials are in fact
		// integers, then it doesn't always pick the largest.
		Pair<Polynomial, Polynomial> r = a.divide(b);

		if (r.first().equals(zero)) {
			r = a.divide(b);
			if (r.first().equals(zero)) {
				// a + b are mutually indivisible
				return Polynomial.ONE;
			} else {
				// b is divisible by a, but not the other way around.
				c = a;
				a = b;
				b = c;
			}
		}

		while (!b.equals(Polynomial.ZERO)) {
			r = a.divide(b);
			c = r.second();
			if (c.equals(Polynomial.ZERO)) {
				return b;
			} else if (r.first().equals(Polynomial.ZERO)) {
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
	public Pair<Polynomial, Polynomial> divide(Term t1) {
		Polynomial quotient = new Polynomial(0);
		Polynomial remainder = new Polynomial(0);

		for (Term t2 : terms) {
			Pair<Term, Term> r = t2.divide(t1);
			quotient = quotient.add(r.first());
			remainder = remainder.add(r.second());
		}

		return new Pair(quotient, remainder);
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
	public Pair<Polynomial, Polynomial> divide(Polynomial x) {

		// Ok, yes, this piece of code is horribly inefficient. But, it's tough
		// even to make it work properly, let alone make it work fast.

		Term max = null;

		for (Term t : x) {
			if (max == null || max.compareTo(t) > 0) {
				max = t;
			}
		}

		if (max == null) {
			// this indicates an attempt at division by zero!
			throw new ArithmeticException("polynomial division by zero");
		}

		ArrayList<Term> myterms = new ArrayList<Term>(terms);
		Collections.sort(myterms);

		for (Term t1 : myterms) {
			Pair<Term, Term> d = t1.divide(max);
			if (!d.first().equals(Term.ZERO)) {
				Term quotient = d.first();

				Polynomial remainder = this.subtract(x.multiply(quotient));

				Pair<Polynomial, Polynomial> r = remainder.divide(x);
				return new Pair(r.first().add(quotient), r.second());
			}
		}

		// base case for recursion.
		return new Pair(Polynomial.ZERO, this);
	}

	// ===========================================================
	// NEGATION
	// ===========================================================

	public Polynomial negate() {
		Polynomial r = new Polynomial(0);

		for (Term t : terms) {
			r.terms.add(t.negate());
		}

		return r;
	}

	// ===========================================================
	/* ======================= FACTORISATION ===================== */
	// ===========================================================

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
	public Pair<Polynomial, Polynomial> factoriseFor(String atom) {
		Polynomial factor = new Polynomial(0);
		Polynomial remainder = new Polynomial(0);

		for (Term t : terms) {
			if (t.atoms().contains(atom)) {
				ArrayList<String> atoms = new ArrayList(t.atoms());
				atoms.remove(atom); // remove one instance of var only
				factor = factor.add(new Term(t.coefficient(), atoms));
			} else {
				remainder = remainder.add(t);
			}
		}

		return new Pair(factor, remainder);
	}

	// ===========================================================
	// OTHER
	// ===========================================================

	public int compareTo(Polynomial p) {
		Collection<Term> p_terms = p.terms;

		if (terms.size() < p_terms.size()) {
			return -1;
		} else if (terms.size() > p_terms.size()) {
			return 1;
		}

		Iterator<Term> mi = terms.iterator();
		Iterator<Term> pi = p_terms.iterator();
		while (mi.hasNext()) {
			Term mt = mi.next();
			Term pt = pi.next();
			int c = mt.compareTo(pt);
			if (c != 0) {
				return c;
			}
		}
		return 0;
	}

	public boolean equals(Object o) {
		if (o instanceof Polynomial) {
			Polynomial p = (Polynomial) o;
			return p.terms.equals(terms);
		}
		return false;
	}

	public int hashCode() {
		return terms.hashCode();
	}

	public String toString() {
		if (terms.isEmpty()) {
			return "0";
		}

		String r = "";
		boolean firstTime = true;
		if (terms.size() > 1) {
			r += "(";
		}
		for (Term e : terms) {
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
			for (String v : e.atoms()) {
				if (!ffirstTime) {
					r += "*";
				}
				ffirstTime = false;
				r += v;
			}
		}
		if (terms.size() > 1) {
			r += ")";
		}
		return r;
	}

	// ===========================================================
	// TERM
	// ===========================================================

	public final static class Term implements Comparable<Term> {
		public static final Term ZERO = new Term(0);
		public static final Term ONE = new Term(1);

		private final BigInteger coefficient;
		private final List<String> subterms;

		public Term(int coeff, List<String> atoms) {
			coefficient = BigInteger.valueOf(coeff);
			if (coeff != 0) {
				this.subterms = new ArrayList<String>(atoms);
				Collections.sort(this.subterms);
			} else {
				this.subterms = new ArrayList<String>();
			}
		}

		public Term(int coeff, String... atoms) {
			coefficient = BigInteger.valueOf(coeff);
			this.subterms = new ArrayList<String>();
			if (coeff != 0) {
				for (String v : atoms) {
					this.subterms.add(v);
				}
				Collections.sort(this.subterms);
			}
		}

		public Term(BigInteger coeff, List<String> atoms) {
			coefficient = coeff;
			if (!coeff.equals(BigInteger.ZERO)) {
				this.subterms = new ArrayList<String>(atoms);
				Collections.sort(this.subterms);
			} else {
				this.subterms = new ArrayList<String>();
			}
		}

		public Term(BigInteger coeff, String... atoms) {
			coefficient = coeff;
			this.subterms = new ArrayList<String>();
			if (!coeff.equals(BigInteger.ZERO)) {
				for (String v : atoms) {
					this.subterms.add(v);
				}
				Collections.sort(this.subterms);
			}
		}

		public BigInteger coefficient() {
			return coefficient;
		}

		public List<String> atoms() {
			return Collections.unmodifiableList(subterms);
		}

		public boolean equals(Object o) {
			if (!(o instanceof Term)) {
				return false;
			}
			Term e = (Term) o;
			return e.coefficient.equals(coefficient)
					&& e.subterms.equals(subterms);
		}

		public String toString() {
			String r = "";

			if (subterms.size() == 0) {
				return coefficient.toString();
			} else if (coefficient.equals(BigInteger.ONE.negate())) {
				r += "-";
			} else if (!coefficient.equals(BigInteger.ONE)) {
				r += coefficient.toString();
			}
			boolean firstTime = true;
			for (String v : subterms) {
				if (!firstTime) {
					r += "*";
				}
				firstTime = false;
				r += v;
			}
			return r;
		}

		public int hashCode() {
			return subterms.hashCode();
		}

		public int compareTo(Term e) {
			int maxp = maxPower();
			int emaxp = e.maxPower();
			if (maxp > emaxp) {
				return -1;
			} else if (maxp < emaxp) {
				return 1;
			}
			if (subterms.size() < e.subterms.size()) {
				return 1;
			} else if (subterms.size() > e.subterms.size()) {
				return -1;
			}
			if (coefficient.compareTo(e.coefficient) < 0) {
				return 1;
			} else if (coefficient.compareTo(e.coefficient) > 0) {
				return -1;
			}
			for (int i = 0; i < Math.min(subterms.size(), e.subterms.size()); ++i) {
				String v = subterms.get(i);
				String ev = e.subterms.get(i);
				int r = v.compareTo(ev);
				if (r != 0) {
					return r;
				}
			}
			return 0;

		}

		public boolean isConstant() {
			return subterms.isEmpty() || coefficient.equals(BigInteger.ZERO);
		}

		public Term multiply(Term e) {
			ArrayList<String> nvars = new ArrayList<String>(subterms);
			nvars.addAll(e.subterms);
			return new Term(coefficient.multiply(e.coefficient), nvars);
		}

		public Pair<Term, Term> divide(Term t) {
			if (subterms.containsAll(t.subterms)
					&& coefficient.compareTo(t.coefficient) >= 0) {
				BigInteger[] ncoeff = coefficient
						.divideAndRemainder(t.coefficient);
				ArrayList<String> nvars = new ArrayList<String>(subterms);
				for (String v : t.subterms) {
					nvars.remove(v);
				}
				Term quotient = new Term(ncoeff[0], nvars);
				Term remainder = new Term(ncoeff[1], nvars);
				return new Pair(quotient, remainder);
			} else {
				// no division is possible.
				return new Pair(ZERO, this);
			}
		}

		public Term negate() {
			return new Term(coefficient.negate(), subterms);
		}

		private int maxPower() {
			int max = 0;
			String last = null;
			int cur = 0;
			for (String v : subterms) {
				if (last == null) {
					cur = 1;
					last = v;
				} else if (v.equals(last)) {
					cur = cur + 1;
				} else {
					max = Math.max(max, cur);
					cur = 1;
					last = v;
				}
			}
			return Math.max(max, cur);
		}
	}
}
