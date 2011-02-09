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

import java.util.*;

import wyil.lang.Type;

/**
 * A constructor represents some kind of expression which makes up some, or all
 * of a constraint. A constructor may have one or more subterms.
 * 
 * @author djp
 * 
 */
public final class Constructor implements Comparable<Constructor> {

	public final String name; // constructor name
	public final ArrayList<Constructor> subterms;

	public Constructor(String name, Constructor... subterms) {
		assert name != null;
		this.name = name;
		this.subterms = new ArrayList<Constructor>();
		for (Constructor p : subterms) {
			this.subterms.add(p);
		}
	}

	public Constructor(String name, Collection<Constructor> subterms) {
		assert name != null;
		this.name = name;
		this.subterms = new ArrayList<Constructor>(subterms);
	}

	/**
	 * The following method returns the list of parameters to this expression
	 * constructor, in the order they are given. A constructor with no subterms
	 * is referred to as being <b>indivisible</b>.
	 * 
	 * @return
	 */
	public List<Constructor> subterms() {
		return subterms;
	}

	// =================================================================
	// REQUIRED METHODS
	// =================================================================

	/**
	 * <p>
	 * This method replaces all occurrences of a given expression with another
	 * expression. Typically, this is used to substitute a variable for an
	 * expression, though not in all cases. Formulas automatically simplify
	 * themselves to be as small and compact as possible. For example, a formula
	 * where all variables have been eliminated necessarily becomes a constant
	 * of some sort.
	 * </p>
	 * 
	 * <p>
	 * <b>NOTE:</b> An important contract required by this method is that, if
	 * the substitution did not alter this expression in any way, then the
	 * return must be <b>this</b> expression (not a clone of this expression).
	 * The reason for this primarily to improve performance of this operation
	 * which is executed fairly frequently.
	 * </p>
	 * 
	 * 
	 * @param binding
	 * @return
	 */
	public Constructor substitute(Map<Constructor, Constructor> binding) {
		return this;
	}

	public int compareTo(Constructor c) {
		int nc = name.compareTo(c.name);
		if (nc != 0) {
			return nc;
		}

		if (subterms.size() < c.subterms().size()) {
			return -1;
		} else if (subterms.size() > c.subterms().size()) {
			return 1;
		}

		for (int i = 0; i != subterms.size(); ++i) {
			Constructor p1 = subterms.get(i);
			Constructor p2 = c.subterms().get(i);
			nc = p1.compareTo(p2);
			if (nc != 0) {
				return nc;
			}
		}

		return 0;
	}

	// =================================================================
	// OBJECT METHODS
	// =================================================================

	public String toString() {
		String r = name + "(";
		boolean firstTime = true;

		for (Constructor p : subterms) {
			if (!firstTime) {
				r = r + ",";
			}
			firstTime = false;
			r = r + p;
		}
		return r + ")";
	}

	public boolean equals(Object o) {
		if (o instanceof Constructor) {
			Constructor f = (Constructor) o;
			return name.equals(f.name) && subterms.equals(f.subterms);
		}
		return false;
	}

	public int hashCode() {
		return name.hashCode() + subterms.hashCode();
	}
}