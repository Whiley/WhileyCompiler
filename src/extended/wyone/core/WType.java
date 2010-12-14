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

import java.util.Map;

/**
 * A Wyone type is declared for each variable in the formula being tested for
 * satisfiability. The type is often necessary to ensure a given model does
 * indeed satisfy the formula. For example, we may have determined an assignment
 * of "x==6.23" for the variable "x". However, if "x" has type "int", then this
 * assignment is invalid and, hence, the original formula (on this path) is
 * <i>unsatisfiable</i>.
 * 
 * @author djp
 * 
 */
public interface WType {
	/**
	 * Check whether the given value is an instance of this type.
	 * @param environment TODO
	 * @param o
	 * 
	 * @return
	 */
	boolean isSubtype(WType v, Map<String, WType> environment); 
}
