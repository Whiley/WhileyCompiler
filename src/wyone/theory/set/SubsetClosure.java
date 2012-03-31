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
import wyone.theory.logic.*;

public class SubsetClosure implements Solver.Rule {

	public String name() {
		return "Subset Closure";
	}
	
	public void infer(Formula nlit, Solver.State state, Solver solver) {
		if(nlit instanceof SubsetEq) {
			SubsetEq seq = (SubsetEq) nlit;			
			closeSubset(seq,state,solver);
			if(seq.sign()) {
				// uncertain whether any of the following rewrites apply to
				// subset not eqs.
				lubSubset(seq,state,solver);
				inferSubsetLen(seq,state,solver);			
				inferVariableEqs(seq.lhs(),seq.rhs(),state,solver);
			} else {
				
			}
		} else if(nlit instanceof Equality) {
			// FIXME: there's definitely more we can do here. For example, if
			// the number of elements in one is less than the other, then we
			// know some of the elements are the same.
			Equality eq = (Equality) nlit;
			if(eq.sign()) {				
				inferVariableEqs(eq.lhs(),eq.rhs(),state,solver);
			} else {
				inferVariableNeqs(eq,state,solver);
			}
		}
	}	
	
	private void inferVariableNeqs(Equality eq, Solver.State state,
			Solver solver) {
		/* FIXME: put back!
		Type lhs_t = eq.lhs().type(state);
		if (!(lhs_t instanceof Type.Set)) {
			return; // nothing doing here
		}
		Type.Set type = (Type.Set) lhs_t;
		state.eliminate(eq); // not needed any longer

		Variable skolem = WTypes.newSkolem(type.element(), state, solver);
		Constructor setc = new Set(skolem);
		Constraint left = Logic.and(SubsetEq.subsetEq(setc, eq.lhs()), SubsetEq
				.subsetEq(setc, eq.rhs()).not());
		Constraint right = Logic.and(SubsetEq.subsetEq(setc, eq.rhs()), SubsetEq
				.subsetEq(setc, eq.lhs()).not());
		Constraint nf = Logic.or(left, right);
		if (!state.contains(nf)) {
			state.infer(nf, solver);
		}
		*/
	}
	
	private void inferVariableEqs(Constructor lhs, Constructor rhs, Solver.State state, Solver solver) {
		List<? extends Constructor> lhsElems = null;		
		List<? extends Constructor> rhsElems = null;
		
		if(lhs instanceof SetConstructor || lhs instanceof Value.Set) {			
			lhsElems = lhs.subterms();	
		} 
		
		if(rhs instanceof SetConstructor || rhs instanceof Value.Set) {			
			rhsElems = rhs.subterms();	
		} 				
		
		if(lhsElems != null && rhsElems != null) {
			for(Constructor l : lhsElems) {
				Formula af = null;
				for(Constructor r : rhsElems) {
					Formula nf = Equality.equals(l,r);
					if(af == null) {
						af = nf;
					} else {
						af = Logic.or(af,nf);
					}
				}													

				if(af != null && !state.contains(af)) {				
					state.infer(af, solver);
				}
			}
		} else if(lhsElems != null) {
			int min=0;		
			for(Constructor e : lhsElems) {
				if(e instanceof Value) {
					min++;
				}
			}
			
			state.infer(Numerics.lessThanEq(new LengthOf(rhs), Value.V_NUM(lhsElems.size())),solver);
			
			if(min > 0) {
				Formula nf = Numerics.lessThanEq(Value.V_NUM(min),
						new LengthOf(rhs));							
				state.infer(nf, solver);				
			}
		} else if(rhsElems != null) {
			int min=0;		
			for(Constructor e : rhsElems) {
				if(e instanceof Value) {
					min++;
				}
			}
			
			state.infer(Numerics.lessThanEq(new LengthOf(lhs), Value.V_NUM(rhsElems.size())),solver);
			
			if(min > 0) {
				Formula nf = Numerics.lessThanEq(Value.V_NUM(min),
						new LengthOf(lhs));							
				state.infer(nf, solver);				
			}
		}
	}
	
	private void inferSubsetLen(SubsetEq seq, Solver.State state, Solver solver) {		
		Constructor lhs = seq.lhs();
		Constructor rhs = seq.rhs();
		
		// FIXME: the following is not needed in all cases
		Formula f = Numerics.lessThanEq(new LengthOf(seq.lhs()),new LengthOf(seq.rhs()));
		if(!state.contains(f)) {			
			state.infer(f,solver);
		}
		
		// First, do stuff based on length of lhs
		if(lhs instanceof Value.Set) {
			// Easy case.
			
			Value.Set v = (Value.Set) lhs;
			Formula nf = Numerics.lessThanEq(Value.V_NUM(v.subterms().size()),
					new LengthOf(rhs));
			if(!state.contains(nf)) {				
				state.infer(nf, solver);
			}
		} else if(lhs instanceof SetConstructor) {
			// Harder case			
			SetConstructor v = (SetConstructor) lhs;
			int min = 0;
			
			for(Constructor e : v.subterms()) {
				if(e instanceof Value) {
					min++;
				}
			}
			
			if(min > 0) {
				Formula nf = Numerics.lessThanEq(Value.V_NUM(min),
						new LengthOf(rhs));

				if(!state.contains(nf)) {					
					state.infer(nf, solver);
				}
			}
		}
		
		// Now, do stuff based on size of rhs.
		if (rhs instanceof Value.Set) {
			// Easy case.

			Value.Set v = (Value.Set) rhs;
			Formula nf = Numerics.lessThanEq(new LengthOf(lhs),
					Value.V_NUM(v.subterms().size()));
			if (!state.contains(nf)) {					
				state.infer(nf, solver);
			}
		} else if (rhs instanceof SetConstructor) {
			// Harder case
			SetConstructor v = (SetConstructor) rhs;
			int min = 0;

			for (Constructor e : v.subterms()) {
				if (e instanceof Value) {
					min++;
				}
			}

			if (min > 0) {
				Formula nf = Numerics.lessThanEq(new LengthOf(lhs),
						Value.V_NUM(min));

				if (!state.contains(nf)) {					
					state.infer(nf, solver);
				}
			}
		}
	}
	
	private void closeSubset(SubsetEq seq, Solver.State state, Solver solver) {		
		Constructor seq_lhs = seq.lhs();
		Constructor seq_rhs = seq.rhs();
		
		for(Formula f : state) {
			if(f instanceof SubsetEq) {								
				SubsetEq fseq = (SubsetEq) f;
				Constructor fseq_lhs = fseq.lhs();
				Constructor fseq_rhs = fseq.rhs();
				
				if(seq.sign() && fseq_rhs.equals(seq_lhs)) {
					if(fseq.sign() && seq_rhs.equals(fseq_lhs)) {						
						Formula nf = Equality.equals(seq_lhs,seq_rhs);						
						if(!state.contains(nf)) {							
							state.infer(nf, solver);
						}					
					} else {
						Formula nf = Sets.subsetEq(fseq_lhs,seq_rhs);
						if(!fseq.sign()) { nf = nf.not(); }
						if(!state.contains(nf)) {							
							state.infer(nf, solver);
						}
					}
				} else if(fseq.sign()&& seq_rhs.equals(fseq_lhs)) {
					Formula nf = Sets.subsetEq(seq_lhs,fseq_rhs);					
					if(!seq.sign()) { nf = nf.not(); }
					if(!state.contains(nf)) {						
						state.infer(nf, solver);
					}
				} 				
			} 
		}
	}
	
	private void lubSubset(SubsetEq seq, Solver.State state, Solver solver) {		
		Constructor seq_lhs = seq.lhs();
		Constructor seq_rhs = seq.rhs();
		List seq_lhs_terms = null;
		boolean seq_lhs_isval = false;
		
		if(seq_lhs instanceof SetConstructor) {
			SetConstructor c = (SetConstructor) seq_lhs;
			seq_lhs_terms = c.subterms();
		} else if(seq_lhs instanceof Value.Set) {
			Value.Set c = (Value.Set) seq_lhs;
			seq_lhs_terms = c.subterms();
			seq_lhs_isval = true;
		}
		
		for (Formula f : state) {
			if (f instanceof SubsetEq) {
				SubsetEq fseq = (SubsetEq) f;
				Constructor fseq_lhs = fseq.lhs();
				Constructor fseq_rhs = fseq.rhs();
				if (fseq.sign() && seq_lhs_terms != null && fseq_rhs.equals(seq_rhs)) {
					if (fseq_lhs instanceof SetConstructor) {
						SetConstructor c = (SetConstructor) fseq_lhs;
						HashSet<Constructor> nterms = new HashSet<Constructor>();
						nterms.addAll(seq_lhs_terms);
						nterms.addAll(c.subterms());
						SetConstructor nc = new SetConstructor(nterms);
						Formula nf = Sets.subsetEq(nc, seq_rhs);
						if (!state.contains(nf)) {
							state.eliminate(seq);
							state.eliminate(f);										
							state.infer(nf, solver);
						}
					} else if (fseq_lhs instanceof Value.Set) {
						Value.Set c = (Value.Set) fseq_lhs;
						HashSet nterms = new HashSet<Constructor>();
						nterms.addAll(seq_lhs_terms);
						nterms.addAll(c.subterms());
						Constructor nc;
						if (seq_lhs_isval) {
							nc = Value.V_SET(nterms);
						} else {
							nc = new SetConstructor(nterms);
						}

						Formula nf = Sets.subsetEq(nc, seq_rhs);
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
