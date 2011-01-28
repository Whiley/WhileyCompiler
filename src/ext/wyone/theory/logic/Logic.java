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

import wyone.core.Constraint;
import wyone.core.Value;

public class Logic {
	
	/**
	 * Return the logical AND of one or more formulas.
	 * @param formulas
	 * @return
	 */
	public static Constraint and(Constraint... formulas) {
		HashSet<Constraint> fs = new HashSet<Constraint>();
		Collections.addAll(fs, formulas);
		return new WConjunct(fs).substitute(Collections.EMPTY_MAP);		
	}
	
	/**
	 * Return the logical OR of one or more formulas.
	 * @param formulas
	 * @return
	 */
	public static Constraint or(Constraint... formulas) {		
		HashSet<Constraint> fs = new HashSet<Constraint>();
		Collections.addAll(fs, formulas);
		return new WDisjunct(fs).substitute(Collections.EMPTY_MAP);		
	}	
		
	public static Constraint implies(Constraint f1, Constraint f2) {
		return or(f1.not(),f2).substitute(Collections.EMPTY_MAP);
	}
	
	public static Constraint iff(Constraint f1, Constraint f2) {
		return and(implies(f1,f2),implies(f2,f1)).substitute(Collections.EMPTY_MAP);
	}		
	
	public static Constraint not(Constraint f) {
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
	public static Constraint intersect(Constraint f1, Constraint f2) {
		if(f1.equals(f2)) {
			return f1;
		}
		
		if (f1 instanceof WConjunct && f2 instanceof WConjunct) {
			WConjunct c1 = (WConjunct) f1;
			WConjunct c2 = (WConjunct) f2;
			HashSet<Constraint> common = new HashSet<Constraint>();
			for (Constraint c : c1.subterms()) {
				if (c2.subterms().contains(c)) {
					common.add(c);
				}
			}
			for (Constraint c : c2.subterms()) {
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
		
		return Value.TRUE;
	}
	
	/**
	 * This method factors f2 out of f1
	 * 
	 * @param f1
	 * @param f2
	 * @return
	 */
	public static Constraint factorOut(Constraint f1, Constraint f2) {
		if(f1.equals(f2)) {
			return Value.TRUE;
		}
		
		if (f1 instanceof WConjunct && f2 instanceof WConjunct) {
			WConjunct c1 = (WConjunct) f1;
			WConjunct c2 = (WConjunct) f2;
			HashSet<Constraint> difference = new HashSet<Constraint>(c1.subterms());
			difference.removeAll(c2.subterms());
			return new WConjunct(difference).substitute(Collections.EMPTY_MAP);
		} else if (f1 instanceof WConjunct) {
			WConjunct c1 = (WConjunct) f1;
			HashSet<Constraint> difference = new HashSet<Constraint>(c1.subterms());
			difference.remove(f2);
			return new WConjunct(difference).substitute(Collections.EMPTY_MAP);
		} else if (f2 instanceof WConjunct) {
			// I don't think this case makes sense
		}
		
		return f1;
	}
}
