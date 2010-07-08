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

package wyjc.ast.attrs;

import wyone.theory.logic.WFormula;

/**
 * A vericiation condition attribute is used to store the verification condition
 * used to discharge a given check.
 * 
 * @author djp
 * 
 */
public final class VerificationConditionAttr implements Attribute {	
	private final WFormula condition;

	public VerificationConditionAttr(WFormula condition) {
		this.condition = condition;		
	}

	public WFormula condition() {
		return condition;
	}
	
	public String toString() {
		return condition.toString();
	}
}
