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

package wyone.theory.logic;

import wyone.core.*;

import java.util.*;

public class WPredicate extends WVariable implements WLiteral {
	private boolean sign;
	
	public WPredicate(boolean sign, String name, WExpr ... parameters) {
		super(sign ? name : "!" + name, parameters);
		this.sign = sign;
	}
	
	public WPredicate(boolean sign, String name, Collection<WExpr> parameters) {
		super(sign ? name : "!" + name, parameters);
		this.sign = sign;
	}
	
	public String realName() {
		return sign ? name : name.substring(1);
	}
	
	public boolean sign() {
		return sign;
	}
	
	public WPredicate not() {			
		return new WPredicate(!sign,realName(),subterms);
	}
	
	public WLiteral substitute(Map<WExpr,WExpr> binding) {
		// First, recursively check for substitutions
		ArrayList<WExpr> nparams = new ArrayList<WExpr>();
		boolean pchanged = false;		
		for(WExpr p : subterms) {
			WExpr np = p.substitute(binding);				
			if(np != p) {				
				nparams.add(np);
				pchanged=true;				
			} else {
				nparams.add(p);
			}
		}
		
		// Second, check whether I am being substituted 
		
		WLiteral r = pchanged ? new WPredicate(sign,realName(),nparams) : this;
		WExpr d = binding.get(r);
		if (d != null) {			
			return (WLiteral) d;
		} else
			return r;
	}
}
