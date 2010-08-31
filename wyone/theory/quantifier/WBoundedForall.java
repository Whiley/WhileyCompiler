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
import wyone.theory.set.*;
import wyone.theory.list.*;
import wyone.theory.numeric.WNumber;

public class WBoundedForall  implements WFormula {
	private final HashMap<WVariable,WExpr> variables;
	private final WFormula formula;
	private final boolean sign;
	
	public WBoundedForall(boolean sign, WVariable variable, WExpr src, WFormula formula) {
		this.variables = new HashMap<WVariable,WExpr>();
		this.variables.put(variable,src);
		this.formula = formula;	
		this.sign = sign;
	}	
	
	public WBoundedForall(boolean sign, Map<WVariable,WExpr> variables, WFormula formula) {
		this.variables = new HashMap<WVariable,WExpr>(variables);
		this.formula = formula;	
		this.sign = sign;
	}	
	
	// =================================================================
	// ACCESSORS
	// =================================================================
	
	public boolean sign() {
		return sign;
	}
		
	public Map<WVariable,WExpr> variables() {
		return variables;
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
	
	public WBoundedForall not() {
		return new WBoundedForall(!sign,variables,formula.not());
	}
	
	public List<WFormula> subterms() {
		ArrayList<WFormula> ls = new ArrayList<WFormula>();
		ls.add(formula);
		return ls;
	}
	
	public WFormula substitute(Map<WExpr, WExpr> binding) {
		WBoundedForall f = captureConversion(binding);
			
		boolean changed = false;
		boolean composite = true;
		HashMap nvariables = new HashMap();
		for(Map.Entry<WVariable,WExpr> e : f.variables.entrySet()) {
			WVariable var = e.getKey();
			WExpr src = e.getValue();
			WExpr nsrc = src.substitute(binding);
			composite &= nsrc instanceof WSetVal
					|| nsrc instanceof WSetConstructor
					|| nsrc instanceof WListVal
					|| nsrc instanceof WListConstructor;
			changed |= src != nsrc;
			nvariables.put(var, nsrc);
		}
		
		WFormula nformula = f.formula.substitute(binding);
		changed |= nformula != f.formula;
		
		if(composite) {
			// indicates this quantifier can be compiled down into a true or
			// false value.			
			return evaluate(sign, nvariables,nformula);
		} else if(changed) {
			return new WBoundedForall(sign,nvariables,nformula);
		} else {
			return f;
		}
	}

	private static WFormula evaluate(boolean sign, HashMap<WVariable,WExpr> nsrcs, WFormula formula) {
		
		int[] counters = new int[nsrcs.size()];
		WVariable[] vars = new WVariable[counters.length];
		WExpr[] srcs = new WExpr[counters.length];
		List<? extends WExpr>[] data = new List[counters.length];
		int i = 0;
		
		for(Map.Entry<WVariable,WExpr> m : nsrcs.entrySet()) {			
			counters[i] = 0;
			vars[i] = m.getKey();
			// OBSERVE: WE HAVE A GUARANTEE THAT sv is EITHER SET/LIST VAL/CONSTRUCTOR.
			WExpr sv = m.getValue();							
			data[i] = sv.subterms();
			srcs[i] = sv;
			
			if (data[i].isEmpty()) {
				// if any of the input sources are empty, then the result is
                // determined by the sign				
				return sign ? WBool.TRUE : WBool.FALSE;
			}
			i=i+1;
		}
		
		WFormula r = sign ? WBool.TRUE : WBool.FALSE; 
		
		do {
			HashMap<WExpr,WExpr> binding = new HashMap();
			for(i=0;i!=counters.length;++i) {				
				int count = counters[i];
				WExpr src = srcs[i];
				if(src instanceof WSetVal || src instanceof WSetConstructor) {
					binding.put(vars[i], data[i].get(counters[i]));									
				} else {
					WNumber idx = new WNumber(count);
					binding.put(vars[i], idx);
					WListAccess la = new WListAccess(src,idx);
					binding.put(la, data[i].get(count));
				}
			}	
			WFormula tmp = formula.substitute(binding);						
			
			if(sign) {
				r = WFormulas.and(r,tmp);
			} else {
				r = WFormulas.or(r,tmp);
			}
		} while(!countersIncrement(counters,data));
						
		return r;
	}
	
	private static boolean countersIncrement(int[] counters, Collection[] data) {
		for (int i = 0; i != counters.length; ++i) {
			counters[i] = counters[i] + 1;
			if (counters[i] < data[i].size()) {
				return false;
			} else {
				counters[i] = 0;
			}
		}
		return true;
	}	
	
	private WBoundedForall captureConversion(Map<WExpr,WExpr> binding) {
		/**
		 * The no capture set contains the list of all variables which should
		 * not become captured. Hence, if one of these variables has the same
		 * name as a captured variable, then we need to rename the captured
		 * variable.
		 */
		Set<WVariable> nocapture = new HashSet<WVariable>();

		for(Map.Entry<WExpr,WExpr> e : binding.entrySet()) {
			nocapture.addAll(WExprs.match(WVariable.class,e.getKey()));
			nocapture.addAll(WExprs.match(WVariable.class,e.getValue()));
		}

		HashMap<WExpr, WExpr> nbinding = null;	
		for (WVariable cv : nocapture) {			
			if(variables.containsKey(cv)) {	
				if(nbinding == null) {
					nbinding = new HashMap<WExpr,WExpr>();					
				}
				WVariable nv = WVariable.freshVar();
				nbinding.put(cv, nv);								
			} 
		}
		
		if(nbinding != null) {
			// Capture conversion is required, so apply appropriate renaming.
			// Hopefully, this doesn't happen very often.
			HashMap<WVariable,WExpr> nvariables = new HashMap();
			for(Map.Entry<WVariable,WExpr> e : variables.entrySet()) {
				WVariable v = e.getKey();
				WVariable nv = (WVariable) nbinding.get(v);
				nv = nv == null ? v : nv;
				nvariables.put(nv, e.getValue().substitute(nbinding));
			}
			WFormula nformula = formula.substitute(nbinding);
			return new WBoundedForall(sign,nvariables,nformula);			
		} else {
			// no capture conversion required
			return this;
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
		if (o instanceof WBoundedForall) {
			WBoundedForall f = (WBoundedForall) o;
			return f.sign == sign && f.variables.equals(variables)
					&& f.formula.equals(formula);
		}
		return false;
	}

	public int hashCode() {
		return variables.hashCode() + formula.hashCode();
	}

	public int compareTo(WExpr e) {
		if(e instanceof WBoundedForall) {
			WBoundedForall f = (WBoundedForall) e;
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
		String r = sign ? "all [" : "some [";
		boolean firstTime=true;
		for (Map.Entry<WVariable,WExpr> e : variables.entrySet()) {			
			if(!firstTime) {
				r += ", ";
			}
			firstTime=false;
			r += e.getKey() + " : " + e.getValue();
		}
		return r + " | " + formula.toString() + "]";
	}
	

	private final static int CID = WExprs.registerCID();
	public int cid() { return CID; }
}
