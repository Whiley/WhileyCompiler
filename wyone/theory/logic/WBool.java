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

import java.util.*;

import wyone.core.*;
import wyone.theory.numeric.WRealType;

/**
 * <p>
 * A constant represents either true or false. This is helpful sometimes to
 * indicate a formula which is unsatisfiable, for example.
 * </p>
 * 
 * @author djp
 * 
 */
public final class WBool implements WLiteral, WValue {
	private boolean constant;
	
	public static final WBool TRUE = new WBool(true);
	public static final WBool FALSE = new WBool(false);
	
	WBool(boolean constant) {		
		this.constant = constant;
	}
	
	public boolean sign() {
		return constant;
	}		
	
	public WBool substitute(Map<WExpr,WExpr> binding) {
		return this;	
	}		
	
	public List<WFormula> subterms() {
		return Collections.EMPTY_LIST;
	}
	
	public WType type(SolverState state) {		
		return WBoolType.T_BOOL;		
	}
	
	public WLiteral rearrange(WExpr lhs) {
		if(lhs instanceof WBool && this.equals(lhs)) {
			return WBool.TRUE;			
		} else if(lhs instanceof WValue) {
			return WBool.FALSE;
		} else {
			return lhs.rearrange(this);
		}
	}
	
	public <T> Set<T> match(Class<T> match) {
		if(WBool.class == match) {
			HashSet<T> r = new HashSet();
			r.add((T)this);
			return r;
		} else {
			return Collections.EMPTY_SET;
		}
	}
	
	public boolean equals(Object o) {
		if(o instanceof WBool) {
			WBool b = (WBool) o;
			return constant == b.constant;
		}
		return false;
	}

	public int compareTo(WExpr e) {
		if(e instanceof WBool) {
			WBool b = (WBool) e;
			if(constant == b.constant) {
				return 0;
			} else if(constant) {
				return -1;
			} else {
				return 1;
			}
		} else if(CID < e.cid()) {
			return -1;
		} else {
			return 1;
		}
	}
	
	public int hashCode() {
		return constant ? 1 : 0;
	}
	
	public WBool not() {
		if(constant) {
			return FALSE;
		} else {
			return TRUE;
		}
	}
	
	public String toString() {
		if(constant) {
			return "true";
		} else {
			return "false";
		}
	}
	

	private final static int CID = WExprs.registerCID();
	public int cid() { return CID; }
}
