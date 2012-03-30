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

import static wyone.theory.numeric.Numerics.*;
import wyone.core.*;
import wyone.util.*;

public final class Term implements Comparable<Term> {
	public static final Term ZERO = new Term(0);
	public static final Term ONE = new Term(1);
	
	private final BigInteger coefficient;
	private final List<Constructor> subterms;	

	public Term(int coeff, List<Constructor> atoms) {
		coefficient = BigInteger.valueOf(coeff);
		if(coeff != 0) {
			this.subterms = new ArrayList<Constructor>(atoms);
			Collections.sort(this.subterms);
		} else {
			this.subterms = new ArrayList<Constructor>();
		}		
	}

	public Term(int coeff, Constructor... atoms) {
		coefficient = BigInteger.valueOf(coeff);
		this.subterms = new ArrayList<Constructor>();
		if(coeff != 0) {
			for(Constructor v : atoms) {
				this.subterms.add(v);
			}
			Collections.sort(this.subterms);
		}
	}

	public Term(BigInteger coeff, List<Constructor> atoms) {
		coefficient = coeff;
		if(!coeff.equals(BigInteger.ZERO)) {
			this.subterms = new ArrayList<Constructor>(atoms);
			Collections.sort(this.subterms);
		} else {
			this.subterms = new ArrayList<Constructor>();
		}				
	}

	public Term(BigInteger coeff, Constructor... atoms) {
		coefficient = coeff;
		this.subterms = new ArrayList<Constructor>();
		if (!coeff.equals(BigInteger.ZERO)) {
			for (Constructor v : atoms) {
				this.subterms.add(v);
			}
			Collections.sort(this.subterms);
		}		
	}	
	
	public BigInteger coefficient() {
		return coefficient;
	}
	
	public List<Constructor> atoms() {
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
		
		if(subterms.size() == 0) {
			return coefficient.toString();
		} else if(coefficient.equals(BigInteger.ONE.negate())) {
			r += "-";
		} else if(!coefficient.equals(BigInteger.ONE)) {
			r += coefficient.toString();
		} 
		boolean firstTime=true;
		for(Constructor v : subterms) {
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

	public int compareTo(Term e) {
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
			Constructor v = subterms.get(i);
			Constructor ev = e.subterms.get(i);
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

	public Constructor expand(Map<Constructor,Constructor> binding) {
		Constructor r = Value.V_NUM(coefficient);
		for(Constructor a : subterms) {				
			Constructor e = a.substitute(binding);			
			r = Numerics.multiply(r,e);			
		}
		return r;
	}
	
	public Polynomial add(int i) {		
		return new Polynomial(this).add(i);
	}
	
	public Polynomial add(BigInteger i) {		
		return new Polynomial(this).add(i);
	}
	
	public Polynomial add(Term e) {		
		return new Polynomial(this).add(e);
	}
	
	public Polynomial add(Polynomial p) {		
		return new Polynomial(this).add(p);
	}
	
	public Polynomial subtract(int i) {		
		return new Polynomial(this).subtract(i);
	}
	
	public Polynomial subtract(BigInteger i) {		
		return new Polynomial(this).subtract(i);
	}
	
	public Polynomial subtract(Term e) {		
		return new Polynomial(this).subtract(e);
	}
	
	public Polynomial subtract(Polynomial p) {		
		return new Polynomial(this).subtract(p);
	}
	
	public Term multiply(int i) {
		return new Term(coefficient.multiply(BigInteger.valueOf(i)),subterms);
	}
	
	public Term multiply(long i) {
		return new Term(coefficient.multiply(BigInteger.valueOf(i)),subterms);
	}
	
	public Term multiply(BigInteger i) {
		return new Term(coefficient.multiply(i),subterms);
	}
	
	public Term multiply(Term e) {
		ArrayList<Constructor> nvars = new ArrayList<Constructor>(subterms);
		nvars.addAll(e.subterms);	
		return new Term(coefficient.multiply(e.coefficient),nvars);
	}	
	
	public Polynomial multiply(Polynomial e) {
		return e.multiply(this);		
	}	
	
	public Pair<Term,Term> divide(Term t) {
		if (subterms.containsAll(t.subterms)
				&& coefficient.compareTo(t.coefficient) >= 0) {				
			BigInteger[] ncoeff = coefficient.divideAndRemainder(t.coefficient);			
			ArrayList<Constructor> nvars = new ArrayList<Constructor>(subterms);
			for(Constructor v : t.subterms) {
				nvars.remove(v);
			}			
			Term quotient = new Term(ncoeff[0],nvars);
			Term remainder = new Term(ncoeff[1],nvars);			
			return new Pair(quotient,remainder);		
		} else {
			// no division is possible.
			return new Pair(ZERO,this);
		}
	}
	
	public Term negate() {
		return new Term(coefficient.negate(),subterms);
	}		
	
	private int maxPower() {
		int max = 0;
		Constructor last = null;
		int cur = 0;
		for(Constructor v : subterms){
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
