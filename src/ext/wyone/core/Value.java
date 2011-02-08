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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import wyil.jvm.rt.BigRational;

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
	public Value substitute(java.util.Map<Constructor,Constructor> binding) {
		return this;
	}

	public abstract Type type();
	
	private final static int CID = Helpers.registerCID();
	public int cid() { return CID; }

	// ====================================================================
	// CONSTRUCTORS
	// ====================================================================
	
	public static Bool V_BOOL(boolean v) {
		return new Bool(v);		
	}
	
	public static Number V_NUM(int v) {
		return new Number(BigInteger.valueOf(v));
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

	public static Strung V_STR(String str) {
		return new Strung(str);
	}
	
	public static Map V_MAP(Value from, Value to) {
		return new Map(from,to);
	}
	
	public static Set V_SET(java.util.Set<Value> items) {
		return new Set(items);
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
		
	public static final class Bool extends Value implements Constraint {
		public final boolean value;
		
		Bool(boolean b) {
			value = b;
		}
		
		public Bool substitute(java.util.Map<Constructor,Constructor> binding) {
			return this;
		}
		
		public Bool not() {
			return new Bool(!value);
		}
		
		public Type type() {
			return Type.T_BOOL;
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
	

	public static final class Number extends Value {
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
		
		public Type type() {
			if(value.isInteger()) {
				return Type.T_INT;
			} else {
				return Type.T_REAL;
			}
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
		
	public static final class Strung extends Value {
		public final String value;
		private Strung(String value) {
			this.value = value;
		}
		
		public Type type() {
			return Type.T_STRING;
		}
		
		public boolean equals(Object o) {
			if(o instanceof Strung) {
				Strung b = (Strung) o;
				return value.equals(b.value);
			}
			return false;
		}
		
		public int hashCode() {
			return 10;
		}
		
		public int compareTo(Constructor c) {
			if (c instanceof Value && c instanceof Strung) {
				Strung b = (Strung) c;
				return value.compareTo(b.value);
			} else if (c instanceof Bool || c instanceof Number) {
				return 1;
			} else {
				return -1;
			}
		}	
		
		public String toString() {
			return "\"" + value + "\"";
		}
	}
	
	public static final class Map extends Value {
		public final Value from;
		public final Value to;
		
		private Map(Value from, Value to ) {
			this.from = from;
			this.to = to;
		}
		
		public Type type() {
			return Type.T_MAP(from.type(),to.type());
		}
		
		public boolean equals(Object o) {
			if(o instanceof Map) {
				Map m = (Map) o;
				return from.equals(m.from) && to.equals(m.to);
			}
			return false;
		}
		
		public int hashCode() {
			return from.hashCode() + to.hashCode();
		}
		
		public int compareTo(Constructor c) {
			if (c instanceof Value && c instanceof Map) {
				Map m = (Map) c;
				int r = from.compareTo(m.from);
				if(r == 0) { 
					r = to.compareTo(m.to);
				}
				return r;
			} else if (c instanceof Bool || c instanceof Number
					|| c instanceof Strung) {
				return 1;
			} else {
				return -1;
			}
		}	
		
		public String toString() {
			return from + "-> " + to;
		}
	}
	
	public static final class Set extends Value {
		public final HashSet<Value> values;
		private Set(Collection<Value> value) {
			this.values = new HashSet<Value>(value);
		}
		public Type type() {
			if(values.isEmpty()) {
				return Type.T_SET(Type.T_VOID);
			} else {
				// FIXME: need to use lub here
				return Type.T_SET(values.iterator().next().type());
			}
		}
		public List<Constructor> subterms() {
			ArrayList vals = new ArrayList<Value>(values);
			Collections.sort(vals);
			return vals;
		}
		public int hashCode() {
			return values.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Set) {
				Set i = (Set) o;
				return values.equals(i.values);
			}
			return false;
		}
		public int compareTo(Constructor v) {			
			if(v instanceof Set) {
				Set l = (Set) v;
				if(values.size() < l.values.size()) {
					return -1;
				} else if(values.size() > l.values.size()) {
					return 1;
				} else {
					// this case is slightly awkward, since we can't rely on the
					// iteration order for HashSet.
					ArrayList<Value> vs1 = new ArrayList<Value>(values);
					ArrayList<Value> vs2 = new ArrayList<Value>(l.values);
					Collections.sort(vs1);
					Collections.sort(vs2);
					for(int i=0;i!=values.size();++i) {
						Value v1 = vs1.get(i);
						Value v2 = vs2.get(i);
						int c = v1.compareTo(v2);
						if(c != 0) { return c; }
					}
					return 0;
				}
			} else if (v instanceof Bool
					|| v instanceof Number					
					|| v instanceof Strung
					|| v instanceof Map) {
				return 1;
			}
			return -1;			
		}
		public String toString() {
			String r = "{";
			boolean firstTime=true;
			for(Value v : values) {
				if(!firstTime) {
					r += ",";
				}
				firstTime=false;
				r += v;
			}
			return r + "}";
		}
	}	
}
