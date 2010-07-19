// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyjc.ast.types;

import wyjc.ast.exprs.Condition;

/**
 * Every type may have an optional constraint. Within the constraint, the
 * special variable $ is used to refer to the variable of this type.
 * 
 * @author djp
 * 
 */
public abstract class ConstrainedType {
	protected final Condition constraint;

	/**
	 * Create a constrained type with a given condition. This condition may be
	 * null.
	 * 
	 * @param constraint
	 */
	public ConstrainedType(Condition constraint) {
		this.constraint = constraint;
	}
	
	/**
	 * Create a constrained type with no condition.
	 */
	public ConstrainedType() {
		constraint = null;
	}
	
	public Condition constraint() {
		return constraint;
	}
	
	public String toString() {
		if(constraint == null) {
			return "";
		} else {
			return " where " + constraint;
		}
	}
}
