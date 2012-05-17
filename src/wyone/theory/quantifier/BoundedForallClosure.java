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
import wyone.theory.list.*;
import wyone.theory.logic.*;
import wyone.theory.set.*;
import wyone.theory.numeric.*;
import wyone.theory.type.*;

public class BoundedForallClosure implements InferenceRule {

	// somehow I think the general approach to handling lists in quantified
	// expressions is broken. I should only permit sets as sources in quantified
	// expression, and provide a magic mechanism for pulling the set of keys
	// from a list.
	public void infer(WFormula nlit, SolverState state, Solver solver) {		
		if(nlit instanceof WSubsetEq) {
			WSubsetEq seq = (WSubsetEq)nlit;
			for(WExpr s : state) {
				if(s instanceof WBoundedForall) {
					attemptInference(seq, (WBoundedForall)s, state, solver);
				}
			}									
		} else if(nlit instanceof WBoundedForall) {			
			WBoundedForall bfall = (WBoundedForall)nlit;
			if(!bfall.sign()) {
				skolemise(bfall,state,solver);
			} else if(simpleElimination(bfall, state, solver)) {
				// managed to eliminate this
			} else {
				for(WExpr s : state) {
					if(s instanceof WSubsetEq) {
						attemptInference((WSubsetEq)s, bfall, state, solver);
					} 
					for(WListAccess la : WExprs.match(WListAccess.class, s)) {
						attemptInference(la, bfall, state, solver);
					}
				}
			}
		} else {
			Set<WListAccess> las = WExprs.match(WListAccess.class, nlit);
			for(WExpr s : state) {
				if(s instanceof WBoundedForall) {
					for(WListAccess la : las)
					attemptInference(la, (WBoundedForall)s, state, solver);
				}				
			}
		}
	}	
	
	public boolean simpleElimination(WBoundedForall bf, SolverState state, Solver solver) {
		if(bf.variables().size() > 1) { return false; }
		
		Map.Entry<WVariable,WExpr> src = bf.variables().entrySet().iterator().next();
		WVariable v = src.getKey();
		// the following binding is needed for several test cases
		HashMap<WExpr,WExpr> binding = new HashMap<WExpr,WExpr>();
		
		WFormula veq = WSets.subsetEq(new WSetConstructor(v), src.getValue()); 
		binding.put(veq,WBool.TRUE);
		binding.put(veq.not(),WBool.FALSE);
		WFormula f = bf.formula().substitute(binding);
						
		ArrayList<WFormula> inferences = new ArrayList();
		
		if(f instanceof WSubsetEq) {			
			WSubsetEq seq = (WSubsetEq) f;
			WExpr seq_lhs = seq.lhs();			
			if (seq_lhs instanceof WSetConstructor
					&& seq_lhs.subterms().size() == 1
					&& seq_lhs.subterms().get(0).equals(v)) {				
				inferences.add(WSets.subsetEq(src.getValue(), seq.rhs()));
			} 
		} else if(f instanceof WConjunct) {
			WConjunct c = (WConjunct) f;
			for(WFormula d : c) {
				if(d instanceof WSubsetEq) {
					WSubsetEq seq = (WSubsetEq) d;
					WExpr seq_lhs = seq.lhs();
					if (seq_lhs instanceof WSetConstructor
							&& seq_lhs.subterms().size() == 1
							&& seq_lhs.subterms().get(0).equals(v)) {				
						inferences.add(WSets.subsetEq(src.getValue(), seq.rhs()));
						continue;
					}					
				} 
				return false;				
			}
		} 
		
		if(inferences.size() == 0) {
			return false; // didn't infer anything
		} else {
			state.eliminate(bf);
			for(WFormula nf : inferences) {
				if(!state.contains(nf)) {					
					state.infer(nf,solver);
				}
			}
			return true;
		}
	}
	
	public void skolemise(WBoundedForall bf, SolverState state, Solver solver) {				
		state.eliminate(bf);
		HashMap<WExpr,WExpr> binding = new HashMap();
		WFormula constraints = WBool.TRUE;
		for(Map.Entry<WVariable,WExpr> e : bf.variables().entrySet()) {
			WVariable var = e.getKey();
			WExpr src = e.getValue();				
			WType type = src.type(state);					
			WVariable skolem;
			if(type instanceof WListType) {				
				// must be list eyp
				skolem = WTypes.newSkolem(WIntType.T_INT,state,solver);
				WFormula lb = WNumerics.greaterThanEq(skolem,WNumber.ZERO);
				WFormula ub = WNumerics.lessThan(skolem, new WLengthOf(src));
				constraints = WFormulas.and(constraints,lb,ub);
			} else {
				skolem = WTypes.newSkolem(((WSetType)type).element(),state,solver);
				WFormula seq = WSets.elementOf(skolem,src);
				constraints = WFormulas.and(constraints,seq);
			} 
			binding.put(var,skolem);			
		}
		WFormula nf = bf.formula().substitute(binding);
		nf = WFormulas.and(nf,constraints);
		if(!state.contains(nf)) {			
			state.infer(nf, solver);
		}
	}
	
	public void attemptInference(WListAccess seq, WBoundedForall bf, SolverState state, Solver solver) {		
		HashMap<WVariable, WExpr> nvars = new HashMap();
		boolean changed=false;
		for (Map.Entry<WVariable, WExpr> e : bf.variables().entrySet()) {
			WExpr src = e.getValue();
			WVariable key = e.getKey();
			if(src.equals(seq.source())) {
				changed = true;
				nvars.put(key, new WSetConstructor(seq.index()));						
			} else {
				nvars.put(key,src);
			}
		}
		if(changed) {
			// observe that we don't eliminate the original expression
			// here. This is because we can't be sure that we've
			// determined everything that might in one the variable
			// sources.
			WFormula nf = new WBoundedForall(bf.sign(),nvars,bf.formula());			
			nf = nf.substitute(Collections.EMPTY_MAP);				
			state.infer(nf, solver);
		}
	}
	
	public void attemptInference(WSubsetEq seq, WBoundedForall bf, SolverState state, Solver solver) {
		if(!seq.sign() || !bf.sign()) {
			return; // not sure what we can do here.
		}
		
		WExpr lhs = seq.lhs();
		WExpr rhs = seq.rhs();
		List<? extends WExpr> lhsTerms;
		
		if (lhs instanceof WSetVal || lhs instanceof WSetConstructor) {
			lhsTerms = lhs.subterms();
		} else {
			return; // nothing to do
		}

		if (rhs instanceof WSetVal || rhs instanceof WSetConstructor) {
			return; // also nothing to do here
		}
		
		HashMap<WVariable, WExpr> nvars = new HashMap();
		boolean changed=false;
		for (Map.Entry<WVariable, WExpr> e : bf.variables().entrySet()) {
			WExpr src = e.getValue();
			WVariable key = e.getKey();
			if(src.equals(rhs)) {
				changed = true;
				// following line is inefficient!
				nvars.put(key, new WSetConstructor(new HashSet<WExpr>(
						lhsTerms)));						
			} else {
				nvars.put(key,src);
			}
		}
		if(changed) {
			// observe that we don't eliminate the original expression
			// here. This is because we can't be sure that we've
			// determined everything that might in one the variable
			// sources.
			WFormula nf = new WBoundedForall(bf.sign(),nvars,bf.formula());			
			nf = nf.substitute(Collections.EMPTY_MAP);				
			state.infer(nf, solver);
		}
	}		
}
