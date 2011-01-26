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

package wyone.core;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import wyil.lang.Type;
import wyil.lang.Value;

/**
 * <p>
 * A Wyone value is something fixed, which cannot be further subdivided. For
 * example, an integer or real number. Values are important as, for each
 * variable in a formula, we must attempt to find an appropriate value for it.
 * This must satisfy the variables type and, in addition, enable the formula to
 * be reduced to true. If it is impossible to do this, then the formula is
 * <i>unsatisfiable</i>; or, if we run out of time trying then it's
 * satisfiability is <i>unknown</i>.
 * <p>
 * 
 * @author djp
 * 
 */
public class WValue implements WExpr {
	private Value value; 

	public WValue(Value value) {
		this.value = value;
	}
	
	public Type type(SolverState state) {
		return value.type();
	}
	
	public List<WExpr> subterms() {
		return Collections.EMPTY_LIST;
	}
	
	public boolean equals(Object o) {
		if(o instanceof WValue) {
			WValue v = (WValue) o;
			return value.equals(v.value);
		}
		return false;
	}
	
	public int hashCode() {
		return value.hashCode();
	}
	
	public int compareTo(WExpr e) {
		if(e instanceof WValue) {
			WValue v = (WValue) e;
			throw new RuntimeException("Need to implement WValue.compareTo()");
		} else if(e.cid() < CID) {
			return -1;
		} else {
			return 1;
		}
	}
	
	/**
	 * Substituting into a value has no effect. However, we need this method
	 * because it overrides Expr.substitute.
	 */
	public WValue substitute(Map<WExpr,WExpr> binding) {
		return this;
	}

	private static class Constraint extends WValue implements WConstraint {
		public Constraint(Value.Bool b) {
			super(b);
		}
	}
	
	// ====================================================================
	// CONSTANTS
	// ====================================================================
	
	public final static Constraint FALSE = new Constraint(Value.V_BOOL(false));
	public final static Constraint TRUE = new Constraint(Value.V_BOOL(true));
	
	// ====================================================================
	// CID
	// ====================================================================
	
	public int cid() {
		return CID;
	}
	
	private static final int CID = WExprs.registerCID();
}
