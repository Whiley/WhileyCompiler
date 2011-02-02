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
import static wyone.core.Constructor.*;
import wyone.core.*;
import wyone.core.Constructor.Variable;
import wyone.theory.logic.*;
import wyone.util.*;

// equalities and inequalities should always be normalised.
public final class Equality extends Base<Constructor> implements Constraint {
	private boolean sign;
	/**
	 * Construct an inequality from left and right rationals. So, this generates
	 * 0==rhs-lhs if sign, otherwise 0!=rhs-lhs
	 * 
	 * @param sign
	 * @param lhs --- left-hand side
	 * @param rhs --- right-hand side
	 */
	public Equality(boolean sign, Constructor lhs, Constructor rhs) {
		super(sign ? "==" : "!=",lhs,rhs);
		this.sign = sign;
	}
	
	public boolean sign() {
		return sign;
	}
	
	public Constructor lhs() {
		return subterms.get(0);
	}
	
	public Constructor rhs() {
		return subterms.get(1);
	}
	
	public Type type(Solver.State state) {
		return Type.T_BOOL;
	}
	
	public Equality not() {
		return new Equality(!sign,lhs(),rhs());
	}
	
	public boolean isAssignment() {			
		Constructor lhs = lhs();
		if(lhs instanceof Variable && rhs() instanceof Value) {
			Variable v = (Variable) lhs;
			return v.isConcrete();			
		}
		return false;			
	}	
	
	public Constraint substitute(Map<Constructor, Constructor> binding) {				
		Constructor olhs = subterms.get(0);
		Constructor orhs = subterms.get(1);
		Constructor lhs = olhs.substitute(binding);
		Constructor rhs = orhs.substitute(binding);		
		if(lhs instanceof Value && rhs instanceof Value) {						
			Value lhsv = (Value) lhs;
			Value.Bool b = lhsv.equals(rhs) ? Value.TRUE : Value.FALSE;
			if(sign) {
				return b;
			} else {
				return b.not();
			}
		} else if(lhs.equals(rhs)) {
			if(sign) {
				return Value.TRUE;
			} else {
				return Value.FALSE;
			}
		} else {			
			Constraint nf = lhs.equate(rhs);			
			nf = sign ? nf : nf.not();
			// following is needed to meet requirements of Contract.substitute()
			if(nf.equals(this)) {
				return this;
			} else {				
				return nf;
			}
		}
		
	}		
	
	public String toString() {
		if(sign) {
			return lhs() + "==" + rhs();
		} else {
			return lhs() + "!=" + rhs();
		}
	}
	
	public static Constraint equals(Constructor lhs, Constructor rhs) {
		return new Equality(true,lhs,rhs).substitute(Collections.EMPTY_MAP);
	}
	
	public static Constraint notEquals(Constructor lhs, Constructor rhs) {
		return new Equality(false,lhs,rhs).substitute(Collections.EMPTY_MAP);
	}

	/**
	 * <p>
	 * The equality closure is a rule for inferring new facts based on equality,
	 * whilst eliminating those which are subsumed. For example, if we have:
	 * </p>
	 * 
	 * <pre>
	 * x < 0
	 * y > 0
	 * x == y
	 * </pre>
	 * 
	 * <p>
	 * Here, equality closure infers <code>y < 0</code>, and eliminate
	 * <code>x < 0</code>. This allows the linear arithmetic theory to identify
	 * the contradiction.
	 * </p>
	 * 
	 * @author djp
	 * 
	 */
	public static class Closure implements Solver.Rule {

		public String name() {
			return "Congruence Closure";
		}
		
		public void infer(Constraint nlit, Solver.State state, Solver solver) {				
			if(nlit instanceof Equality) {		
				Equality eq = (Equality) nlit;			
				if(eq.sign()) {				
					inferEquality(eq, state, solver);				
					return;
				} 			
			} 
			inferFormula(nlit,state,solver);				
		}
		
		private void inferFormula(Constraint formula, Solver.State state, Solver solver) {
			HashMap<Constructor,Constructor> binding = new HashMap<Constructor,Constructor>();
		
			for(Constraint f : state) {
				if(f instanceof Equality) {
					Equality eq = (Equality) f;
					if(eq.sign()) {
						// FIXME: there is a subtle problem here, when we have multiple
						// possible assignments for a variable. This can arise when we
						// don't eliminate assignments in inferEquality.
						binding.put(eq.lhs(),eq.rhs());					
					}
				}
			}
						
			Constraint nf = formula.substitute(binding);
			if(nf != formula) {							
				state.eliminate(formula);									
				state.infer(nf,solver);				
			}
		}
		
		private void inferEquality(Equality eq, Solver.State state, Solver solver) {
			
			// So, at this point we have an equality of the form lhs == rhs.
			// This equality should have been normalised into a form where lhs
			// represents a single "effective variable", and rhs represents other
			// stuff. Observe that lhs is no guaranteed to be an instanceof
			// Variable (unfortunately); this is because of tuple and list
			// accessors --- which are not Variables as they are "interpreted".
			// That is, given a value for the target variable their value can be
			// immediately deduced.
			//
			// Our objective at this point is to substitute all occurrences of
			// the lhs with the rhs, such that this equality is the only place where
			// the lhs now exists. When the lhs is actually an instance of
			// Variable, then this will be an "assignment".
			//
			// Some issues can arise from other equalities which now take on a new
			// form.  For example, consider this:
			//
			// t1.x < 0 && t2.x == t2.y && t2.y > 0 && t1 == t2
			// 
			// Here, the last equality is the one being added. So, after
			// substituting t1 for t2, we get this state:
			//
			// t2.x < 0 && t2.x == t2.y && t2.y > 0 && t1 == t2
			//
			// But, we now need to substitute for t2.x as well, in order to get
			// here:
			//
			// t2.y < 0 && t2.x == t2.y && t2.y > 0 && t1 == t2
			//
			// This then gives the contradiction.
			//
			// What we're really trying to do is choose a representative from the
			// class of equivalences, and ensure that all constraints are in terms
			// of the current representatives.
				
			HashMap<Constructor, Constructor> binding = new HashMap<Constructor,Constructor>();
			binding.put(eq.lhs(),eq.rhs());
			
			// Second, we iterate all existing literals and attempt to simplify
			// them. Those which are simplified are subsumed, and their simplified
			// forms are added into the literal set.
			for(Constraint f : state) {
				if(f == eq) { continue; }							
				Constraint nf = f.substitute(binding);									
				if(nf != f) {				
					// f has been replaced!					
					if (!(f instanceof Equality)
							|| !((Equality) f).isAssignment()) {					
						state.eliminate(f);
					}							
					state.infer(nf,solver);							
				}
			}
		}	
	}
}
