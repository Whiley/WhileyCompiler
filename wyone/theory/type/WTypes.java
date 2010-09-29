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

package wyone.theory.type;

import java.util.*;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.congruence.*;

public class WTypes {

	public static WSubtype subtypeOf(WExpr lhs, WType rhs) {
		return new WSubtype(true,lhs,rhs);
	}
	
	/**
	 * Determine the type of a given expression; that is, the type of the value
	 * that this will evaluate to.
	 */
	public static WType type(WExpr e, SolverState state) {
		HashSet<WExpr> aliases = new HashSet<WExpr>();
		aliases.add(e);
		
		// NOTE: would be nice to make this more efficient
		//
		// (yeah, like that's gonna happen)
		
		boolean changed = true;
		while (changed) {
			changed = false;
			for (WFormula f : state) {
				if (f instanceof WEquality) {
					WEquality weq = (WEquality) f;
					if (weq.sign()
							&& (aliases.contains(weq.lhs()) || aliases
									.contains(weq.rhs()))) {
						changed |= aliases.add(weq.lhs());
						changed |= aliases.add(weq.rhs());
					}
				}
			}
		}
		
		WType t = WAnyType.T_ANY; 				
		
		for(WFormula f : state) {
			if(f instanceof WSubtype) {
				// FIXME: probably would make more sense to build up a GLB from
				// all possible types.
				WSubtype st = (WSubtype) f;
				if(aliases.contains(st.lhs())) {
					WType tmp = st.rhs();					
					if(t.isSubtype(tmp, Collections.EMPTY_MAP)) {
						t = tmp;
					}
				}
			}
		}		
		
		return t;
	}	
	
	public static WVariable newSkolem(WType t, SolverState state,
			Solver solver) {
		WVariable v = WVariable.freshVar();
		state.infer(WTypes.subtypeOf(v, t), solver);
		return v;
	}
}
