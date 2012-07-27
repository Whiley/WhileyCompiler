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

import static wyone.theory.numeric.WNumerics.*;
import wyone.core.*;
import wyone.util.*;

public final class WTerm implements Comparable<WTerm> {
	public static final WTerm ZERO = new WTerm(0);
	public static final WTerm ONE = new WTerm(1);
	
	private final BigInteger coefficient;
	private final List<WExpr> subterms;	

	public WTerm(int coeff, List<WExpr> atoms) {
		coefficient = BigInteger.valueOf(coeff);
		if(coeff != 0) {
			this.subterms = new ArrayList<WExpr>(atoms);
			Collections.sort(this.subterms);
		} else {
			this.subterms = new ArrayList<WExpr>();
		}		
	}

	public WTerm(int coeff, WExpr... atoms) {
		coefficient = BigInteger.valueOf(coeff);
		this.subterms = new ArrayList<WExpr>();
		if(coeff != 0) {
			for(WExpr v : atoms) {
				this.subterms.add(v);
			}
			Collections.sort(this.subterms);
		}
	}

	public WTerm(BigInteger coeff, List<WExpr> atoms) {
		coefficient = coeff;
		if(!coeff.equals(BigInteger.ZERO)) {
			this.subterms = new ArrayList<WExpr>(atoms);
			Collections.sort(this.subterms);
		} else {
			this.subterms = new ArrayList<WExpr>();
		}				
	}

	public WTerm(BigInteger coeff, WExpr... atoms) {
		coefficient = coeff;
		this.subterms = new ArrayList<WExpr>();
		if (!coeff.equals(BigInteger.ZERO)) {
			for (WExpr v : atoms) {
				this.subterms.add(v);
			}
			Collections.sort(this.subterms);
		}		
	}	
	
	public BigInteger coefficient() {
		return coefficient;
	}
	
	public List<WExpr> atoms() {
		return Collections.unmodifiableList(subterms);
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof WTerm)) {
			return false;
		}
		WTerm e = (WTerm) o;
		return e.coefficient.equals(coefficient)
		&& e.subterms.equals(subterms);
	}		
	
	public String toString() {
		String r = "";
		
		if(subterms.size() == 0) {
			return coefficient.toString();
		} else if(coefficient.equals(BigInteger.ONE.negate())) {
			r += "-";
		} else if(!coefficient.equals(BigInteger.ONE)) {
			r += coefficient.toString();
		} 
		boolean firstTime=true;
		for(WExpr v : subterms) {
			if(!firstTime) {
				r += "*";
			}
			firstTime=false;
			r += v;
		}
		return r;
	}

	public int hashCode() {
		return subterms.hashCode();
	}

	public int compareTo(WTerm e) {
		int maxp = maxPower();
		int emaxp = e.maxPower();
		if(maxp > emaxp) {
			return -1;
		} else if(maxp < emaxp) {
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
			WExpr v = subterms.get(i);
			WExpr ev = e.subterms.get(i);
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

	public WExpr expand(Map<WExpr,WExpr> binding) {
		WExpr r = new WNumber(coefficient);
		for(WExpr a : subterms) {				
			WExpr e = a.substitute(binding);			
			r = WNumerics.multiply(r,e);			
		}
		return r;
	}
	
	public WPolynomial add(int i) {		
		return new WPolynomial(this).add(i);
	}
	
	public WPolynomial add(BigInteger i) {		
		return new WPolynomial(this).add(i);
	}
	
	public WPolynomial add(WTerm e) {		
		return new WPolynomial(this).add(e);
	}
	
	public WPolynomial add(WPolynomial p) {		
		return new WPolynomial(this).add(p);
	}
	
	public WPolynomial subtract(int i) {		
		return new WPolynomial(this).subtract(i);
	}
	
	public WPolynomial subtract(BigInteger i) {		
		return new WPolynomial(this).subtract(i);
	}
	
	public WPolynomial subtract(WTerm e) {		
		return new WPolynomial(this).subtract(e);
	}
	
	public WPolynomial subtract(WPolynomial p) {		
		return new WPolynomial(this).subtract(p);
	}
	
	public WTerm multiply(int i) {
		return new WTerm(coefficient.multiply(BigInteger.valueOf(i)),subterms);
	}
	
	public WTerm multiply(long i) {
		return new WTerm(coefficient.multiply(BigInteger.valueOf(i)),subterms);
	}
	
	public WTerm multiply(BigInteger i) {
		return new WTerm(coefficient.multiply(i),subterms);
	}
	
	public WTerm multiply(WTerm e) {
		ArrayList<WExpr> nvars = new ArrayList<WExpr>(subterms);
		nvars.addAll(e.subterms);	
		return new WTerm(coefficient.multiply(e.coefficient),nvars);
	}	
	
	public WPolynomial multiply(WPolynomial e) {
		return e.multiply(this);		
	}	
	
	public Pair<WTerm,WTerm> divide(WTerm t) {
		if (subterms.containsAll(t.subterms)
				&& coefficient.compareTo(t.coefficient) >= 0) {				
			BigInteger[] ncoeff = coefficient.divideAndRemainder(t.coefficient);			
			ArrayList<WExpr> nvars = new ArrayList<WExpr>(subterms);
			for(WExpr v : t.subterms) {
				nvars.remove(v);
			}			
			WTerm quotient = new WTerm(ncoeff[0],nvars);
			WTerm remainder = new WTerm(ncoeff[1],nvars);			
			return new Pair(quotient,remainder);		
		} else {
			// no division is possible.
			return new Pair(ZERO,this);
		}
	}
	
	public WTerm negate() {
		return new WTerm(coefficient.negate(),subterms);
	}		
	
	private int maxPower() {
		int max = 0;
		WExpr last = null;
		int cur = 0;
		for(WExpr v : subterms){
			if(last == null) {
				cur = 1;
				last = v;
			} else if(v.equals(last)){
				cur = cur + 1;
			} else {
				max = Math.max(max,cur);
				cur = 1;
				last = v;
			}
		}
		return Math.max(max,cur);		
	}
}
