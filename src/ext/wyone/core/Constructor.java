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
import wyone.theory.type.*;

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
	 * The following method returns the set of parameters to this expression
	 * constructor, in the order they are given.
	 * 
	 * @return
	 */
	public List<? extends Constructor> subterms();

	/**
	 * The following method gives the type of this expression. That is, the type
	 * of the value that this expression will evaluate to. A reference to the
	 * solver state is required in order to handle variables, whose type is
	 * determined by the current state. This is particularly useful for compound
	 * values, such as tuples and lists. In such cases, we need to know whether
	 * the element is an integer or not.
	 * 
	 * @param state
	 * @return
	 */
	public Type type(Solver.State state);

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
	public static class Base<T extends Constructor> implements Iterable<T> {
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
		
		public Type type(Solver.State state) {
			return Subtype.type(this,state);		
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
