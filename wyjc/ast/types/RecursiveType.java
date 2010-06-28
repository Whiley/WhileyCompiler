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

import wyjc.util.ModuleID;
import wyone.core.WType;

public class RecursiveType implements NonUnionType {
	// name the recursive variable used to terminate the recursion
	private ModuleID module;
	private String name;
	
	// this contains one or more instances of a matching type variable.
	private Type type;
	
	public RecursiveType(ModuleID module, String name, Type type) {
		this.name = name;
		this.module = module;
		this.type = type;
	}
	
	public String name() {
		return name;
	}
	
	public Type type() {
		return type;
	}
	
	public boolean equals(Object o) {
		if(o instanceof RecursiveType) {
			RecursiveType rt = (RecursiveType) o;
			return name.equals(rt.name) && type.equals(rt.type);
		}
		return false;
	}
	
	public int hashCode() {
		return name.hashCode() + type.hashCode();
	}
	
	public boolean isSubtype(Type t) {
		return this.equals(t) || type.isSubtype(t);
	}
	
	public boolean isExistential() {
		return type.isExistential();
	}
	
	public Type flattern() {
		return new RecursiveType(module, name, type.flattern());
	}
	
	public String toString() {
		return "(" + module + ":" + name + " => " + type + ")";
	}
	
	public WType convert() {
		return null; // dunno about this
	}
}
