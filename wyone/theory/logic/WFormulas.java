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

import java.math.BigInteger;
import java.util.*;

import wyone.core.SolverState;

public class WFormulas {
	
	/**
	 * Return the logical AND of one or more formulas.
	 * @param formulas
	 * @return
	 */
	public static WFormula and(WFormula... formulas) {
		HashSet<WFormula> fs = new HashSet<WFormula>();
		Collections.addAll(fs, formulas);
		return new WConjunct(fs).substitute(Collections.EMPTY_MAP);		
	}
	
	/**
	 * Return the logical OR of one or more formulas.
	 * @param formulas
	 * @return
	 */
	public static WFormula or(WFormula... formulas) {
		HashSet<WFormula> fs = new HashSet<WFormula>();
		Collections.addAll(fs, formulas);
		return new WDisjunct(fs).substitute(Collections.EMPTY_MAP);		
	}
		
	public static WFormula implies(WFormula f1, WFormula f2) {
		return or(f1.not(),f2).substitute(Collections.EMPTY_MAP);
	}
	
	public static WFormula iff(WFormula f1, WFormula f2) {
		return and(implies(f1,f2),implies(f2,f1)).substitute(Collections.EMPTY_MAP);
	}		
	
	public static WFormula not(WFormula f) {
		return f.not().substitute(Collections.EMPTY_MAP);
	}

}
