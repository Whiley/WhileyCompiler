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

public class UnresolvedTupleType implements UnresolvedType {
	private HashMap<String,UnresolvedType> types;
	
	public UnresolvedTupleType(Map<String,UnresolvedType> types) {
		this.types = new HashMap<String,UnresolvedType>(types);			
	}
	
	public Iterator<Map.Entry<String,UnresolvedType>> iterator() {
		return types.entrySet().iterator();
	}
	
	public Map<String,UnresolvedType> types() {
		return types;
	}
	
	public UnresolvedType get(String key) {
		return types.get(key);
	}
	
	public boolean equals(Object o) {
		if(o instanceof UnresolvedTupleType) {
			UnresolvedTupleType t = (UnresolvedTupleType) o;
			return types.equals(t.types);
		}
		return false;
	}
	
	public int hashCode() {		
		return types.hashCode();
	}		
		
	public String toString() {
		String r = "(";
		boolean firstTime = true;

		ArrayList<String> ss = new ArrayList<String>(types.keySet());
		Collections.sort(ss);

		for (String s : ss) {
			if (!firstTime) {
				r = r + ",";
			}
			firstTime = false;
			r = r + types.get(s).toString() + " " + s;
		}
		return r + ")";
	}
}
