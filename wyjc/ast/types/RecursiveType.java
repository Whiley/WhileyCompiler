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

import java.util.HashMap;
import java.util.Map;

import wyjc.util.*;
import wyone.core.WType;

/**
 * A NameType is simply a type which has been defined and, hence, has a name
 * associated with it.
 * 
 * @author djp
 * 
 */
public class RecursiveType implements NonUnionType {	
	private String name;
	private Type type; // underlying type
	
	public RecursiveType(Type type) {				
		this.name = calculateName(type);
		this.type = type;		
	}
	
	RecursiveType(String name, Type type) {				
		this.name = name;
		this.type = type;		
	}
	
	public String name() {
		return name;
	}
	
	public Type type() {
		return type;
	}
	
	public Type flattern() {
		return type.flattern();
	}
		
	public boolean isSubtype(Type t) {
		if(t instanceof RecursiveType) {
			RecursiveType nt = (RecursiveType) t;			
			Type nt_type = nt.type();
			HashMap<String,String> binding = new HashMap();
			binding.put(nt.name(), name);
			nt_type = nt_type.substitute(binding);
			return type.isSubtype(nt_type);
		} 
		return type.isSubtype(t);					
	}
	
	public boolean isExistential() {
		return false;
	}
	
	public Type substitute(Map<String,String> binding) {
		Type t = type.substitute(binding);
		String newname = binding.get(name);
		if(newname == null) {
			newname = name;
		}
		return new RecursiveType(newname,t);
	}
	
	
	public boolean equals(Object o) {
		if(o instanceof RecursiveType) {
			RecursiveType nt = (RecursiveType) o;			
			// FIXME: not completely sure about this
			return name.equals(nt.name) && type.equals(nt.type);
		} 
		return false;
	}
			
	public int hashCode() {
		return name.hashCode();
	}
	
	public String toString() {
		return name + "[" + type + "]";
	}
	
	public WType convert() {
		return type.convert();
	}
	
	private static String calculateName(Type sub) {
		System.err.println("RECURSIVE TYPE NAME HACK IN PLACE");
		return "X"; // hack for now
	}
}
