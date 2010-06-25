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

import java.util.*;

import wyjc.ast.types.*;

public class UnresolvedUnionType implements UnresolvedType {
	private HashSet<UnresolvedType> types;
	
	public UnresolvedUnionType(Collection<UnresolvedType> types) {
		// need to flattern union types here.
		this.types = new HashSet<UnresolvedType>(types);
	}

	public UnresolvedUnionType(UnresolvedType... ts) {
		types = new HashSet<UnresolvedType>();
		for(UnresolvedType t : ts) {
			types.add(t);
		}
	}
	
	public Set<UnresolvedType> types() {
		return types;
	}
	
	public boolean equals(Object o) {
		if(o instanceof UnresolvedUnionType) {
			return ((UnresolvedUnionType)o).types.equals(types);
		}
		return false;
	}
	
	public int hashCode() {
		return types.hashCode();
	}	
	
	public String toString() {
		String r = "";
		boolean firstTime=true;
		for(UnresolvedType t : types) {
			if(!firstTime) {
				r += "|";
			}
			firstTime=false;
			r += t.toString();
		}
		return r;
	}
}
