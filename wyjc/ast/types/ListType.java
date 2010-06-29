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

import java.util.Map;

import wyone.core.WType;
import wyone.theory.list.WListType;


public final class ListType extends SetType {
	public ListType(Type element) {
		super(element);		
	}
	
	public boolean equals(Object o) {
		if(!(o instanceof ListType)) {
			return false;
		}
		ListType at = (ListType) o;
		return at.element().equals(element());
	}
		
	public boolean isSubtype(Type t) {
		if(t instanceof NamedType) {
			t = ((NamedType)t).type();
		}
		if(t == Types.T_VOID) {
			return true;
		} else if(t instanceof ListType) {
			ListType lt = (ListType) t;
			return element().isSubtype(lt.element());
		} else if(t instanceof ProcessType) {
			ProcessType pt = (ProcessType) t;
			return isSubtype(pt.element());
		} 		
		return false;
	}
	
	public Type flattern() {
		return new ListType(element().flattern());
	}
	
	public WType convert() {
		return new WListType(element().convert());
	}
	
	public Type substitute(Map<String,String> binding) {
		return new ListType(element().substitute(binding));
	}
	
	public String toString() {
		return "[" + element().toString() + "]";
	}
}
