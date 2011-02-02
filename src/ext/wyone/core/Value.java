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
public class Value<T extends wyil.lang.Value> implements Constructor, Comparable<Constructor> {
	public final T value; 

	Value(T value) {
		this.value = value;
	}
	
	public Type type() {
		return value.type();
	}
	
	public List<Constructor> subterms() {
		return Collections.EMPTY_LIST;
	}
	
	public boolean equals(Object o) {
		if(o instanceof Value) {
			Value v = (Value) o;
			return value.equals(v.value);
		}
		return false;
	}
	
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
	
	public int hashCode() {
		return value.hashCode();
	}
	
	public int compareTo(Constructor v) {	
		if(v instanceof Value) {			
			return value.compareTo(((Value)v).value);
		} else if(CID < v.cid()) {
			return -1; 
		} else {
			return 1;
		}
	}
	
	public String toString() {
		return value.toString();
	}
	
	/**
	 * Substituting into a value has no effect. However, we need this method
	 * because it overrides Expr.substitute.
	 */
	public Value<T> substitute(Map<Constructor,Constructor> binding) {
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
		
	public static class Bool extends Value<wyil.lang.Value.Bool> implements Constraint {
		Bool(boolean b) {
			super(wyil.lang.Value.V_BOOL(b));
		}
		
		public Bool substitute(Map<Constructor,Constructor> binding) {
			return this;
		}
		
		public boolean sign() {
			return value.value;
		}
		
		public Bool not() {
			return new Bool(!value.value);
		}
	}
	
	public static class Number extends Value<wyil.lang.Value.Real> {
		Number(BigRational r) {
			super(wyil.lang.Value.V_REAL(r));
		}
		Number(BigInteger r) {
			super(wyil.lang.Value.V_REAL(BigRational.valueOf(r)));
		}
		
		public BigInteger numerator() {
			return value.value.numerator();
		}
		
		public BigInteger denominator() {
			return value.value.denominator();
		}
		
		public Number add(Number n) {
			return new Number(value.value.add(n.value.value));
		}
		
		public Number subtract(Number n) {
			return new Number(value.value.subtract(n.value.value));
		}
		
		public Number multiply(Number n) {
			return new Number(value.value.multiply(n.value.value));
		}
		
		public Number divide(Number n) {
			return new Number(value.value.divide(n.value.value));
		}		
		
		public Number negate() {
			return new Number(value.value.negate());
		}
		
		public boolean isInteger() {
			return value.value.isInteger();
		}
		
		public Number ceil() {
			return V_NUM(value.value.ceil());
		}
		
		public Number floor() {
			return V_NUM(value.value.floor());
		}
	}
}
