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

import java.util.*;

import wyil.lang.Type;
import wyone.theory.numeric.Rational;

/**
 * A constructor represents some kind of expression which makes up some, or all
 * of a constraint.  A constructor may have one or more subterms, unless it's a
 * variable (in which case it may have none).  
 * 
 * @author djp
 * 
 */
public interface Constructor extends Comparable<Constructor> {

	/**
	 * <p>
	 * The cid is a specifier used to carve up the domain of constructors into
	 * distinct regions of different values. The purpose of the cid is to
	 * simplify the task of implementing the compareTo method for any given
	 * implementation of Constructor.
	 * </p>
	 * <p>
	 * The reason for requiring that all constructors have a cid() method is to
	 * ensure we can always construct a sorted sequence of constructors. This
	 * simplifies various algorithms (e.g. for factoring polynomials).
	 * </p>
	 * 
	 * @return
	 */
	public int cid();
	
	/**
	 * <p>
	 * This method replaces all occurrences of a given expression with another
	 * expression. Typically, this is used to substitute a variable for an
	 * expression, though not in all cases. Formulas automatically simplify
	 * themselves to be as small and compact as possible. For example, a formula
	 * where all variables have been eliminated necessarily becomes a constant
	 * of some sort.
	 * </p>
	 * 
	 * <p>
	 * <b>NOTE:</b> An important contract required by this method is that, if
	 * the substitution did not alter this expression in any way, then the
	 * return must be <b>this</b> expression (not a clone of this expression).
	 * The reason for this primarily to improve performance of this operation
	 * which is executed fairly frequently.
	 * </p>
	 * 
	 * 
	 * @param binding
	 * @return
	 */
	public Constructor substitute(Map<Constructor,Constructor> binding);

	/**
	 * <p>
	 * Equality --- or <i>congruence</i> --- is a fundamental concept which
	 * underlies almost all other theories. As such, it is given special status
	 * through this method, which enables normalisation of equalities across
	 * multiple theories.
	 * <p>
	 * <p>
	 * The basic issue of normalising equalities is that it requires specialised
	 * knowledge, that only an individual theory will have. For example:
	 * </p>
	 * 
	 * <pre>
	 * x+1 != x+1
	 * x-1 == 1
	 * {1,2,3} == x+y
	 * </pre>
	 * <p>
	 * The first two examples above require knowledge of <i>arithmetic
	 * expressions</i> to be normalised. However, the third one requires
	 * knowledge of <i>set expressions</i>, and it does not make sense to ask
	 * the arithmetic theory to normalise it.
	 * </p>
	 * <p>
	 * The solution adopted to this problem exploits <i>double dispatch</i> to
	 * ensure the appropriate theory gets to control normalisation. What happens
	 * is that, for a given equality <code>lhs == rhs</code>, we invoke
	 * <code>lhs.equate(rhs)</code>. If the <code>lhs</code> is a constructor
	 * from some theory, then it will simply control the normalisation process.
	 * However, if the lhs is either a variable, or a value, then it will invoke
	 * <code>rhs.equate(lhs)</code>, giving the right-hand side a chance to
	 * control normalisation. (<b>NOTE:</b: in the case of just values and/or
	 * variables on the right-hand side, then the second invoke is not made, as
	 * this would result in an infinite loop).
	 * </p>
	 * 
	 * @param c
	 * @return
	 */
	public Constraint equate(Constructor c);
	
	/**
	 * The following method returns the list of parameters to this expression
	 * constructor, in the order they are given. A constructor with no subterms
	 * is referred to as being <b>indivisible</b>.
	 * 
	 * @return
	 */
	public List<? extends Constructor> subterms();

	/**
	 * <p>
	 * This is a basic constructor implementation which is provided to simplify
	 * developing other constructors. This is because, in many cases, one can
	 * develop a speciflc constructor by subclassing this.
	 * </p>
	 * 
	 * @author djp
	 * 
	 */
	public static abstract class Base<T extends Constructor> implements Iterable<T>, Constructor {
		protected final String name; // constructor name
		protected final ArrayList<T> subterms; 	
		
		public Base(String name, T... subterms) {
			assert name != null;
			this.name = name;		
			this.subterms = new ArrayList<T>();
			for(T p : subterms) {
				this.subterms.add(p);
			}		
		}		
		
		public Base(String name, Collection<T> subterms) {
			assert name != null;
			this.name = name;		
			this.subterms = new ArrayList<T>(subterms);
		}
		
		// =================================================================
		// ACCESSORS
		// =================================================================

		public String name() { 
			return name;
		}
			
		public Iterator<T> iterator() {
			return subterms.iterator();
		}
		
		// =================================================================
		// REQUIRED METHODS
		// =================================================================
		
		public abstract Constructor substitute(
				Map<Constructor, Constructor> binding);
					
		public Constraint equate(Constructor other) {
			if(other instanceof Variable) {			
				return new Equality(true,other,this);				 				
			} else {
				// In this case, there's not much we can do. This will happen if
				// a specialised constructor for some theory does not offer any
				// useful mechanism for normalising equalities.
				return new Equality(true,this,other);
			}
		}
		
		
		public List<T> subterms() {
			return subterms;
		}
		
		public int compareTo(Constructor e) {				
			if(e instanceof Base) {
				Base<Constructor> c = (Base<Constructor>) e;
				if(subterms.size() < c.subterms().size()) {
					return -1;
				} else if(subterms.size() > c.subterms().size()) {
					return 1;
				} 

				int nc = name.compareTo(c.name());
				if(nc != 0) {
					return nc;
				}

				for(int i=0;i!=subterms.size();++i) {
					Constructor p1 = subterms.get(i);
					Constructor p2 = c.subterms().get(i);
					nc = p1.compareTo(p2);
					if(nc != 0) { return nc; }
				}

				return 0;
			} else if(cid() < e.cid()){
				return -1;
			} else {
				return 1;
			}
		}	
		
		// =================================================================
		// OBJECT METHODS
		// =================================================================
		
		public String toString() {
			String r = name + "(";
			boolean firstTime=true;
			
			for(Constructor p : subterms) {
				if(!firstTime) {
					r = r + ",";
				}
				firstTime=false;
				r = r + p;
			}
			return r + ")";		
		}
			
		public boolean equals(Object o) {
			if (o instanceof Base) {
				Base f = (Base) o;				
				return name.equals(f.name) && subterms.equals(f.subterms);
			}
			return false;
		}				

		public int hashCode() {
			return name.hashCode() + subterms.hashCode();
		}	
		
		private final static int CID = Helpers.registerCID();
		public int cid() { return CID; }
	}
	
	/**
	 * <p>
	 * A variable is a component of a Wyone expression for which we need to infer a
	 * value. Effectively, a variable is an <i>uninterpreted function</i> which acts
	 * as a barrier between its parameters and return value. Thus, even if we have
	 * concrete values for its parameters, we cannot immediately infer what its
	 * return value should be.
	 * </p>
	 * 
	 * <p>
	 * Variables differ from other Wyone expressions, which can be thought of as
	 * <i>interpreted functions</i>. In such case, we can determine their return
	 * value immediately once we have concrete values for their parameters.
	 * </p>
	 * 
	 * <p>
	 * Finally, a <i>model</i> is an assignment of (concrete) Wyone variables to
	 * values. A <i>concrete Wyone</i> variable is one whose parameters are either
	 * themselves concrete variables, or are values.  Thus, a model is all that is
	 * required to satisfy a given Wyone program.
	 * </p>
	 * 
	 * @author djp
	 * 
	 */
	public static class Variable extends Base<Constructor> implements Constructor {	
		private final static int CID = Helpers.registerCID(); 
		
		public Variable(String var, Constructor... args) {
			super(var,args);
		}
		
		public Variable(String var, Collection<Constructor> args) {
			super(var,args);
		}
		
		public boolean isConcrete() {
			for(Constructor p : subterms) {
				if(!(p instanceof Value)) {
					return false;
				}
			}
			return true;
		}
		
		public int cid() { return CID; }
		
		public Constructor substitute(Map<Constructor,Constructor> binding) {		
			// First, recursively check for substitutions
			ArrayList<Constructor> nparams = new ArrayList<Constructor>();
			boolean pchanged = false;		
			for(Constructor p : subterms) {
				Constructor np = p.substitute(binding);				
				if(np != p) {				
					nparams.add(np);
					pchanged=true;				
				} else {
					nparams.add(p);
				}
			}
			
			// Second, check whether I am being substituted 
			
			Constructor r = pchanged ? new Variable(name,nparams) : this;
			Constructor d = binding.get(r);
			if(d != null) { return d; }
			else return r;
		}
		
		public Constraint equate(Constructor other) {
			if(other instanceof Variable) {			
				if(this.equals(other)) {
					return Value.TRUE;
				} else {
					// NOTE. We have to be very careful here not to disturb the
					// ordering that may have been set by previous assignments.
					return new Equality(true,this,other);
				} 					
			} else if(other instanceof Value) {
				// Here, we have an assignment
				return new Equality(true,this,other);
			} else if(other instanceof Rational) {
				// this is a bit of a hack, and doesn't fit into the real idea
				// behind equate(). Basically, I don't want to normalise this
				// equality unless doing so will actually lead to something
				// better. Doing this gives greater control over what is an
				// assignment, and what is not. This is necessary for correctly
				// support subtype closure.
				Rational rhs = (Rational) other;
				if(rhs.subterms().contains(this)) {
					return other.equate(this);
				} else {
					// no optimisation possible
					return new Equality(true,this,other);
				}
			} else {
				// Using double dispatch here is sneaky, but it does ensure that
				// more complex forms of expression get the opportunity to
				// rearrange, In particular, if the lhs was a rational of some kind
				// that reference this variable, then we want to take that into
				// account.  e.g. x-1 == x => -1 = 0 => false
				return other.equate(this);
			}
		}
		
		public String toString() {
			if(subterms.size() == 0) {
				return name;
			} else{
				return super.toString();
			}
		}
			
		// =================================================================
		// HELPER METHODS
		// =================================================================
		
		private static int fvidx = 0;
		public static Variable freshVar() {
			return new Variable("#" + fvidx++);
		}
	}

}
