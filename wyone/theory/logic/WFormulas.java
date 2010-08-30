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

	/**
	 * Intersecting two formulas means identifying what we can show as true in
	 * both.
	 * 
	 * @param f1
	 * @param f2
	 * @return
	 */
	public static WFormula intersect(WFormula f1, WFormula f2) {
		if(f1.equals(f2)) {
			return f1;
		}
		
		if (f1 instanceof WConjunct && f2 instanceof WConjunct) {
			WConjunct c1 = (WConjunct) f1;
			WConjunct c2 = (WConjunct) f2;
			HashSet<WFormula> common = new HashSet<WFormula>();
			for (WFormula c : c1.subterms()) {
				if (c2.subterms().contains(c)) {
					common.add(c);
				}
			}
			for (WFormula c : c2.subterms()) {
				if (c1.subterms().contains(c)) {
					common.add(c);
				}
			}
			return new WConjunct(common).substitute(Collections.EMPTY_MAP);
		} else if (f1 instanceof WConjunct) {
			WConjunct c1 = (WConjunct) f1;
			if (c1.subterms().contains(f2)) {
				return f2;
			}
		} else if (f2 instanceof WConjunct) {
			WConjunct c2 = (WConjunct) f2;
			if (c2.subterms().contains(f1)) {
				return f1;
			}
		}
		
		return WBool.TRUE;
	}
	
	/**
	 * This method factors f2 out of f1
	 * 
	 * @param f1
	 * @param f2
	 * @return
	 */
	public static WFormula factorOut(WFormula f1, WFormula f2) {
		if(f1.equals(f2)) {
			return WBool.TRUE;
		}
		
		if (f1 instanceof WConjunct && f2 instanceof WConjunct) {
			WConjunct c1 = (WConjunct) f1;
			WConjunct c2 = (WConjunct) f2;
			HashSet<WFormula> difference = new HashSet<WFormula>(c1.subterms());
			difference.removeAll(c2.subterms());
			return new WConjunct(difference).substitute(Collections.EMPTY_MAP);
		} else if (f1 instanceof WConjunct) {
			WConjunct c1 = (WConjunct) f1;
			HashSet<WFormula> difference = new HashSet<WFormula>(c1.subterms());
			difference.remove(f2);
			return new WConjunct(difference).substitute(Collections.EMPTY_MAP);
		} else if (f2 instanceof WConjunct) {
			// I don't think this case makes sense
		}
		
		return f1;
	}
}
