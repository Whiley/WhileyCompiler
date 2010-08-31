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

package wyone.theory.set;

import java.util.*;
import wyone.core.*;
import wyone.theory.numeric.*;
import wyone.theory.type.WTypes;
import wyone.theory.list.*;
import wyone.theory.logic.*;
import wyone.theory.congruence.*;

public class SubsetClosure implements InferenceRule {

	public void infer(WFormula nlit, SolverState state, Solver solver) {
		if(nlit instanceof WSubsetEq) {
			WSubsetEq seq = (WSubsetEq) nlit;			
			closeSubset(seq,state,solver);
			if(seq.sign()) {
				// uncertain whether any of the following rewrites apply to
				// subset not eqs.
				lubSubset(seq,state,solver);
				inferSubsetLen(seq,state,solver);			
				inferVariableEqs(seq.lhs(),seq.rhs(),state,solver);
			} else {
				
			}
		} else if(nlit instanceof WEquality) {
			// FIXME: there's definitely more we can do here. For example, if
			// the number of elements in one is less than the other, then we
			// know some of the elements are the same.
			WEquality eq = (WEquality) nlit;
			if(eq.sign()) {
				inferVariableEqs(eq.lhs(),eq.rhs(),state,solver);
			} else {
				inferVariableNeqs(eq,state,solver);
			}
		}
	}	
	
	private void inferVariableNeqs(WEquality eq, SolverState state,
			Solver solver) {
		WType lhs_t = eq.lhs().type(state);
		if (!(lhs_t instanceof WSetType)) {
			return; // nothing doing here
		}
		WSetType type = (WSetType) lhs_t;
		state.eliminate(eq); // not needed any longer

		WVariable skolem = WTypes.newSkolem(type.element(), state, solver);
		WExpr setc = new WSetConstructor(skolem);
		WFormula left = WFormulas.and(WSets.subsetEq(setc, eq.lhs()), WSets
				.subsetEq(setc, eq.rhs()).not());
		WFormula right = WFormulas.and(WSets.subsetEq(setc, eq.rhs()), WSets
				.subsetEq(setc, eq.lhs()).not());
		WFormula nf = WFormulas.or(left, right);
		if (!state.contains(nf)) {
			state.infer(nf, solver);
		}
	}
	
	private void inferVariableEqs(WExpr lhs, WExpr rhs, SolverState state, Solver solver) {
		List<? extends WExpr> lhsElems = null;		
		List<? extends WExpr> rhsElems = null;
		
		if(lhs instanceof WSetConstructor || lhs instanceof WSetVal) {			
			lhsElems = lhs.subterms();	
		} 
		
		if(rhs instanceof WSetConstructor || rhs instanceof WSetVal) {			
			rhsElems = rhs.subterms();	
		} 				
		
		if(lhsElems == null || rhsElems == null) {
			return; // can't do anything
		}
				
		for(WExpr l : lhsElems) {
			WFormula af = null;
			for(WExpr r : rhsElems) {
				WFormula nf = WExprs.equals(l,r);
				if(af == null) {
					af = nf;
				} else {
					af = WFormulas.or(af,nf);
				}
			}													
			
			if(af != null && !state.contains(af)) {				
				state.infer(af, solver);
			}
		}
	}
	
	private void inferSubsetLen(WSubsetEq seq, SolverState state, Solver solver) {		
		WExpr lhs = seq.lhs();
		WExpr rhs = seq.rhs();
		
		// FIXME: the following is not needed in all cases
		WFormula f = WNumerics.lessThanEq(new WLengthOf(seq.lhs()),new WLengthOf(seq.rhs()));
		if(!state.contains(f)) {			
			state.infer(f,solver);
		}
		
		// First, do stuff based on length of lhs
		if(lhs instanceof WSetVal) {
			// Easy case.
			
			WSetVal v = (WSetVal) lhs;
			WFormula nf = WNumerics.lessThanEq(new WNumber(v.subterms().size()),
					new WLengthOf(rhs));
			if(!state.contains(nf)) {				
				state.infer(nf, solver);
			}
		} else if(lhs instanceof WSetConstructor) {
			// Harder case			
			WSetConstructor v = (WSetConstructor) lhs;
			int min = 0;
			
			for(WExpr e : v.subterms()) {
				if(e instanceof WValue) {
					min++;
				}
			}
			
			if(min > 0) {
				WFormula nf = WNumerics.lessThanEq(new WNumber(min),
						new WLengthOf(rhs));

				if(!state.contains(nf)) {					
					state.infer(nf, solver);
				}
			}
		}
		
		// Now, do stuff based on size of rhs.
		if (rhs instanceof WSetVal) {
			// Easy case.

			WSetVal v = (WSetVal) rhs;
			WFormula nf = WNumerics.lessThanEq(new WLengthOf(lhs),
					new WNumber(v.subterms().size()));
			if (!state.contains(nf)) {					
				state.infer(nf, solver);
			}
		} else if (rhs instanceof WSetConstructor) {
			// Harder case
			WSetConstructor v = (WSetConstructor) rhs;
			int min = 0;

			for (WExpr e : v.subterms()) {
				if (e instanceof WValue) {
					min++;
				}
			}

			if (min > 0) {
				WFormula nf = WNumerics.lessThanEq(new WLengthOf(lhs),
						new WNumber(min));

				if (!state.contains(nf)) {					
					state.infer(nf, solver);
				}
			}
		}
	}
	
	private void closeSubset(WSubsetEq seq, SolverState state, Solver solver) {		
		WExpr seq_lhs = seq.lhs();
		WExpr seq_rhs = seq.rhs();
		
		for(WFormula f : state) {
			if(f instanceof WSubsetEq) {								
				WSubsetEq fseq = (WSubsetEq) f;
				WExpr fseq_lhs = fseq.lhs();
				WExpr fseq_rhs = fseq.rhs();
				
				if(seq.sign() && fseq_rhs.equals(seq_lhs)) {
					if(fseq.sign() && seq_rhs.equals(fseq_lhs)) {						
						WFormula nf = WExprs.equals(seq_lhs,seq_rhs);						
						if(!state.contains(nf)) {							
							state.infer(nf, solver);
						}					
					} else {
						WFormula nf = WSets.subsetEq(fseq_lhs,seq_rhs);
						if(!fseq.sign()) { nf = nf.not(); }
						if(!state.contains(nf)) {							
							state.infer(nf, solver);
						}
					}
				} else if(fseq.sign()&& seq_rhs.equals(fseq_lhs)) {
					WFormula nf = WSets.subsetEq(seq_lhs,fseq_rhs);					
					if(!seq.sign()) { nf = nf.not(); }
					if(!state.contains(nf)) {						
						state.infer(nf, solver);
					}
				} 				
			} 
		}
	}
	
	private void lubSubset(WSubsetEq seq, SolverState state, Solver solver) {		
		WExpr seq_lhs = seq.lhs();
		WExpr seq_rhs = seq.rhs();
		List seq_lhs_terms = null;
		boolean seq_lhs_isval = false;
		
		if(seq_lhs instanceof WSetConstructor) {
			WSetConstructor c = (WSetConstructor) seq_lhs;
			seq_lhs_terms = c.subterms();
		} else if(seq_lhs instanceof WSetVal) {
			WSetVal c = (WSetVal) seq_lhs;
			seq_lhs_terms = c.subterms();
			seq_lhs_isval = true;
		}
		
		for (WFormula f : state) {
			if (f instanceof WSubsetEq) {
				WSubsetEq fseq = (WSubsetEq) f;
				WExpr fseq_lhs = fseq.lhs();
				WExpr fseq_rhs = fseq.rhs();
				if (fseq.sign() && seq_lhs_terms != null && fseq_rhs.equals(seq_rhs)) {
					if (fseq_lhs instanceof WSetConstructor) {
						WSetConstructor c = (WSetConstructor) fseq_lhs;
						HashSet<WExpr> nterms = new HashSet<WExpr>();
						nterms.addAll(seq_lhs_terms);
						nterms.addAll(c.subterms());
						WSetConstructor nc = new WSetConstructor(nterms);
						WFormula nf = WSets.subsetEq(nc, seq_rhs);
						if (!state.contains(nf)) {
							state.eliminate(seq);
							state.eliminate(f);										
							state.infer(nf, solver);
						}
					} else if (fseq_lhs instanceof WSetVal) {
						WSetVal c = (WSetVal) fseq_lhs;
						HashSet nterms = new HashSet<WExpr>();
						nterms.addAll(seq_lhs_terms);
						nterms.addAll(c.subterms());
						WExpr nc;
						if (seq_lhs_isval) {
							nc = new WSetVal(nterms);
						} else {
							nc = new WSetConstructor(nterms);
						}

						WFormula nf = WSets.subsetEq(nc, seq_rhs);
						if (!state.contains(nf)) {
							state.eliminate(seq);
							state.eliminate(f);									
							state.infer(nf, solver);
						}
					} // need to do something similar to other direction.
				}
			}
		}
	}
}
