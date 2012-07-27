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
import wyone.theory.congruence.WEquality;
import wyone.theory.list.WListType;
import wyone.theory.logic.WLiteral;
import wyone.theory.tuple.WTupleConstructor;
import wyone.theory.tuple.WTupleType;
import wyone.theory.tuple.WTupleVal;
import wyone.theory.type.WAnyType;
import wyone.theory.type.WVoidType;
import wyone.util.*;

public class WSetVal extends WConstructor<WValue> implements WValue {
	public WSetVal(WValue param) {
		super("{.}", param);
	}
	
	public WSetVal(Set<WValue> params) {
		super("{.}", params);
	}

	public WLiteral rearrange(WExpr rhs) {		
		// always best to do rhs on left side, since this might a variable and
		// hence we might be creating an assignment
		return new WEquality(true,rhs,this);
	}
	
	public WSetType type(SolverState state) {
		if(subterms.isEmpty()) {
			return new WSetType(WVoidType.T_VOID);
		} else {
			// FIXME: following is inherently broken
			return new WSetType(subterms.get(0).type(state));
		}
	}
	
	public WValue substitute(Map<WExpr,WExpr> binding) {
		return this;		
	}
	
	public String toString() {
		String r = "{";
		boolean firstTime = true;
		for (WExpr p : subterms()) {
			if (!firstTime) {
				r = r + ",";
			}
			firstTime = false;
			r = r + p;
		}
		return r + "}";
	}
}
