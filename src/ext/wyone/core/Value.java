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

package wyone.core;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wyil.jvm.rt.BigRational;
import wyil.lang.Type;
import wyone.core.Constructor.Variable;

/**
 * <p>
 * A Wyone value is something fixed, which cannot be further subdivided. For
 * example, an integer or real number. Values are important as, for each
 * variable in a formula, we must attempt to find an appropriate value for it.
 * This must satisfy the variables type and, in addition, enable the formula to
 * be reduced to true. If it is impossible to do this, then the formula is
 * <i>unsatisfiable</i>; or, if we run out of time trying then it's
 * satisfiability is <i>unknown</i>.
 * <p>
 * 
 * @author djp
 * 
 */
public abstract class Value implements Constructor, Comparable<Constructor> {
	
	public List<Constructor> subterms() {
		return Collections.EMPTY_LIST;
	}
	
	public abstract boolean equals(Object o);
	public abstract int hashCode();
	public abstract int compareTo(Constructor c);
	
	public Constraint equate(Constructor other) {

		// Observe it should be impossible to get here with other being an
		// instanceof Value. The method Equality.equals() will take care of
		// that.
		
		if(other instanceof Variable) {			
			return new Equality(true,other,this);			 				
		} else {
			// Using double dispatch here is sneaky, but it does ensure that
			// more complex forms of expression get the opportunity to
			// rearrange, In particular, if the lhs was a rational of some kind
			// that reference this variable, then we want to take that into
			// account.  e.g. x-1 == x => -1 = 0 => false
			return other.equate(this);
		}
	}
		
	/**
	 * Substituting into a value has no effect. However, we need this method
	 * because it overrides Expr.substitute.
	 */
	public Value substitute(Map<Constructor,Constructor> binding) {
		return this;
	}

	private final static int CID = Helpers.registerCID();
	public int cid() { return CID; }

	// ====================================================================
	// CONSTRUCTORS
	// ====================================================================
	
	public static Bool V_BOOL(boolean v) {
		return new Bool(v);
	}
	
	public static Number V_NUM(BigInteger v) {
		return new Number(v);
	}
	
	public static Number V_NUM(BigInteger num, BigInteger den) {
		return new Number(new BigRational(num,den));
	}
	
	public static Number V_NUM(BigRational v) {
		return new Number(v);
	}
	
	// ====================================================================
	// CONSTANTS
	// ====================================================================
	
	public final static Bool FALSE = new Bool(false);
	public final static Bool TRUE = new Bool(true);
	
	public final static Number ZERO = new Number(BigRational.ZERO);
	public final static Number ONE = new Number(BigRational.ONE);
	public final static Number TWO = new Number(BigRational.valueOf(2));
	public final static Number MONE = new Number(BigRational.MONE);	
	
	// ====================================================================
	// IMPLEMENTATIONS
	// ====================================================================
		
	public static class Bool extends Value implements Constraint {
		public final boolean value;
		
		Bool(boolean b) {
			value = b;
		}
		
		public Bool substitute(Map<Constructor,Constructor> binding) {
			return this;
		}
		
		public boolean sign() {
			return value;
		}
		
		public Bool not() {
			return new Bool(!value);
		}
		
		public boolean equals(Object o) {
			if(o instanceof Bool) {
				Bool b = (Bool) o;
				return value == b.value;
			}
			return false;
		}
		
		public int hashCode() {
			return value ? 1 : 0;
		}
		
		public int compareTo(Constructor c) {
			if(c instanceof Value && c instanceof Bool) {
				Bool b = (Bool) c;
				if(value == b.value) {
					return 0;
				} else if(value) {
					return 1;
				} else {
					return -1;
				}
			} else {
				return -1;
			}
		}
	}
	
	public static class Number extends Value {
		public final BigRational value;
		
		Number(BigRational value) {
			this.value = value;
		}
		Number(BigInteger r) {
			this.value = BigRational.valueOf(r);
		}
		
		public BigInteger numerator() {
			return value.numerator();
		}
		
		public BigInteger denominator() {
			return value.denominator();
		}
		
		public Number add(Number n) {
			return new Number(value.add(n.value));
		}
		
		public Number subtract(Number n) {
			return new Number(value.subtract(n.value));
		}
		
		public Number multiply(Number n) {
			return new Number(value.multiply(n.value));
		}
		
		public Number divide(Number n) {
			return new Number(value.divide(n.value));
		}		
		
		public Number negate() {
			return new Number(value.negate());
		}
		
		public boolean isInteger() {
			return value.isInteger();
		}
		
		public Number ceil() {
			return V_NUM(value.ceil());
		}
		
		public Number floor() {
			return V_NUM(value.floor());
		}
		
		public boolean equals(Object o) {
			return value.equals(o);
		}
		
		public int hashCode() {
			return value.hashCode();
		}
		
		public int compareTo(Constructor c) {
			if(c instanceof Value && c instanceof Bool) {
				return 1;
			} else if(c instanceof Number) {
				Number n = (Number) c;
				return value.compareTo(n.value);
			} else {
				return -1;
			}
		}
	}			
}
