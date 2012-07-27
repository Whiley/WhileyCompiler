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

import java.util.HashSet;

import wyone.core.*;
import wyone.theory.congruence.WEquality;
import wyone.theory.logic.*;

public class WSets {
	public static WFormula subsetEq(WExpr lhs, WExpr rhs) {
		return new WSubsetEq(true,lhs,rhs);
	}

	public static WFormula subset(WExpr lhs, WExpr rhs) {
		
		if(rhs instanceof WSetConstructor || rhs instanceof WSetVal) {
			// This is a useful optimisation case.
			WFormula r = WBool.FALSE;
			for(WExpr e : rhs.subterms()) {
				HashSet elems = new HashSet(rhs.subterms());
				elems.remove(e);					
				if(rhs instanceof WSetConstructor) {
					e = new WSetConstructor(elems);
				} else {
					e = new WSetVal(elems);
				}
				r = WFormulas.or(r,subsetEq(lhs,e));
			}
			return r;			
		} 
		
		return WFormulas.and(new WSubsetEq(true, lhs, rhs), new WEquality(
				false, lhs, rhs));
	}
	
	public static WFormula supsetEq(WExpr lhs, WExpr rhs) {
		return new WSubsetEq(true,rhs,lhs);
	}
	
	public static WFormula elementOf(WExpr lhs, WExpr rhs) {
		return new WSubsetEq(true,new WSetConstructor(lhs),rhs);
	}	
}
