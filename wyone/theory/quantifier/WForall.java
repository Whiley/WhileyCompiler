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

package wyone.theory.quantifier;

import java.util.*;

import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.WRealType;

public class WForall implements WLiteral {
	private final HashMap<WVariable,WType> variables;
	private final WFormula formula;
	private final boolean sign;
	
	public WForall(boolean sign, WVariable variable, WType type, WFormula formula) {
		this.variables = new HashMap<WVariable,WType>();
		this.variables.put(variable,type);
		this.formula = formula;	
		this.sign = sign;
	}	
	
	public WForall(boolean sign, Map<WVariable,WType> variables, WFormula formula) {
		this.variables = new HashMap<WVariable,WType>(variables);
		this.formula = formula;	
		this.sign = sign;
	}	
	
	// =================================================================
	// ACCESSORS
	// =================================================================
	
	public boolean sign() {
		return sign;
	}
		
	public Set<WVariable> variables() {
		return variables.keySet();
	}		
	
	public WFormula formula() {
		return formula;
	}	
	
	public WType type(SolverState state) {		
		return WBoolType.T_BOOL;		
	}
	
	
	// =================================================================
	// REQUIRED METHODS
	// =================================================================
	
	public WForall not() {
		return new WForall(!sign,variables,formula.not());
	}
	
	public List<WFormula> subterms() {
		ArrayList<WFormula> ls = new ArrayList<WFormula>();
		ls.add(formula);
		return ls;
	}
	
	public WLiteral substitute(Map<WExpr, WExpr> binding) {
		/**
		 * The no capture set contains the list of all variables which should
		 * not become captured. Hence, if one of these variables has the same
		 * name as a captured variable, then we need to rename the captured
		 * variable.
		 */
		Set<WVariable> nocapture = new HashSet<WVariable>();
		
		for(Map.Entry<WExpr,WExpr> e : binding.entrySet()) {
			// nocapture.add(e.getKey());
			nocapture.addAll(WExprs.match(WVariable.class,e.getValue()));
		}
				
		HashMap<WExpr, WExpr> nbinding = null;	
		HashMap<WVariable,WType> nvariables = null;
		for (WVariable cv : nocapture) {			
			if(variables.containsKey(cv)) {	
				if(nbinding == null) {
					nbinding = new HashMap<WExpr,WExpr>();
					nvariables = new HashMap<WVariable,WType>(variables);
				}
				WVariable nv = WVariable.freshVar();
				nbinding.put(cv, nv);				
				nvariables.remove(cv);
				nvariables.put(nv,variables.get(cv));
			} 
		}

		if(nbinding == null) {
			WFormula f = formula.substitute(binding);
			if(f != formula) {
				return new WForall(sign,variables,f);
			} else {
				return this; // no change
			}
		} else {			
			WFormula f = formula.substitute(nbinding).substitute(binding);			
			return new WForall(sign,nvariables,f);			
		}
	}
	
	public WLiteral rearrange(WExpr lhs) {		
		throw new RuntimeException("Need to implement WForall.rearrange()!");		
	}
	
	public boolean match(WExpr e) {
		// FIXME: this is a temporary measure, since matching on quantifiers is
		// rare.
		return false;
	}
	
	// =================================================================
	// OBJECT METHODS
	// =================================================================
	
	public boolean equals(Object o) {
		if (o instanceof WForall) {
			WForall f = (WForall) o;
			return f.sign == sign && f.variables.equals(variables)
					&& f.formula.equals(formula);
		}
		return false;
	}

	public int hashCode() {
		return variables.hashCode() + formula.hashCode();
	}

	public int compareTo(WExpr e) {
		if(e instanceof WForall) {
			WForall f = (WForall) e;
			if(f.subterms().size() < variables.size()) {
				return 1;
			} else if(f.subterms().size() > variables.size()) {
				return -1;
			} else if(sign && !f.sign) {
				return -1;
			} else if(!sign && f.sign) {
				return 1;
			}
			// This could probably be improved. For example, by storing
			// variables in a sorted array list, rather than a hashset
			ArrayList<WVariable> mvariables = new ArrayList<WVariable>(variables.keySet());
			ArrayList<WVariable> fvariables = new ArrayList<WVariable>(f.variables.keySet());
			Collections.sort(mvariables);
			Collections.sort(fvariables);
			for(int i=0;i!=variables.size();++i) {
				WVariable v1 = mvariables.get(i);
				WVariable v2 = fvariables.get(i);
				int c = v1.compareTo(v2);
				if(c != 0) {
					return c;
				}
			}
			return formula.compareTo(f.formula());
		} else if(cid() < e.cid()){
			return -1;
		} else {
			return 1;
		}
	}
	
	public String toString() {		
		String r = sign ? "forall [" : "exists [";				
		for (Map.Entry<WVariable,WType> e : variables.entrySet()) {			
			r += e.getValue() + " " + e.getKey() + ";";
		}
		return r + " " + formula.toString() + "]";
	}
	

	private final static int CID = WExprs.registerCID();
	public int cid() { return CID; }
}
