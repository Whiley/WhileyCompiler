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

package wyone.theory.tuple;

import java.util.*;
import wyone.core.*;
import wyone.util.*;
import wyone.theory.congruence.*;
import wyone.theory.logic.*;

public class TupleClosure implements InferenceRule {

	public void infer(WFormula nlit, SolverState state, Solver solver) {
		if (nlit instanceof WEquality) {
			WEquality weq = (WEquality) nlit;
			if (weq.sign() && weq.lhs() instanceof WTupleAccess) {
				inferTuple(weq, state, solver);
			}
		}
	}
	
	public void inferTuple(WEquality weq, SolverState state, Solver solver) {
		HashMap<String,WExpr> fields = new HashMap();
		WTupleAccess lhs = (WTupleAccess) weq.lhs();
		WExpr target = lhs.target();
		for(WFormula f : state) {
			if(!(f instanceof WEquality)) {
				continue;
			}
			WEquality eq = (WEquality) f;
			if (!(eq.sign() && eq.lhs() instanceof WTupleAccess)) {
				continue;
			}
			lhs = (WTupleAccess) eq.lhs();						
			if(lhs.target().equals(target)) {
				fields.put(lhs.field(), eq.rhs());
			}
		}		
		// It can arise in some situations that we don't have the full type for
		// a variable. I could do better here, but for now this will do I think.
		WType _type = target.type(state);
		if(!(_type instanceof WTupleType)) {
			return;
		}
		WTupleType type = (WTupleType) target.type(state);				
		if(type.types().size() == fields.size()) {			
			ArrayList<String> names = new ArrayList<String>();
			ArrayList<WExpr> exprs = new ArrayList<WExpr>();
			for(Pair<String,WType> t : type.types()) {
				String name = t.first();
				names.add(name);
				WExpr expr = fields.get(name);				
				if(expr == null) {
					// sanity check
					exprs.add(expr);
					return; // nothing doing here
				}
				exprs.add(expr);
			}			
			WExpr tval = new WTupleConstructor(names,exprs).substitute(Collections.EMPTY_MAP);			
			WFormula nf = WExprs.equals(target, tval);								
			if(!state.contains(nf)) {				
				state.infer(nf, solver);
			}
		}
	}
}
