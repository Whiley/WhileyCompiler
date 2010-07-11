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

package wyjc.ast.types.unresolved;

import wyjc.ast.exprs.Condition;
import wyjc.ast.types.*;

public class UnresolvedSetType extends ConstrainedType implements UnresolvedType {
	private UnresolvedType element;
	
	public UnresolvedSetType(UnresolvedType element) {
		this.element = element;
	}

	public UnresolvedSetType(UnresolvedType element, Condition constraint) {
		super(constraint);
		this.element = element;
	}
	
	public UnresolvedType element() {
		return element;
	}
	
	public boolean equals(Object o) {
		if(!(o instanceof UnresolvedSetType)) {
			return false;
		}
		UnresolvedSetType at = (UnresolvedSetType) o;
		return at.element.equals(element);
	}
	
	public int hashCode() {
		return element.hashCode() * 123;
	}
		
	public String toString() {
		return "{" + element.toString() + "}";
	}
}
