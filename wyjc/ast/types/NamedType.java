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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import wyjc.util.*;
import wyone.core.WType;

/**
 * A NameType is simply a type which has been defined and, hence, has a name
 * associated with it.
 * 
 * @author djp
 * 
 */
public class NamedType extends ConstrainedType implements NonUnionType {
	private ModuleID module;
	private String name;
	private Type type; // underlying type
	
	public NamedType(ModuleID module, String name, Type type) {		
		this.module = module;
		this.name = name;
		this.type = type;		
		if(!type.isExistential()) {
			throw new IllegalArgumentException("Named types must be existential");
		}
	}
	
	public ModuleID module() {
		return module;
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
	
	public boolean isExistential() {
		return false;
	}
	
	public boolean equals(Object o) {
		if(o instanceof NamedType) {
			NamedType nt = (NamedType) o;			
			if(module == null && nt.module == null) {
				return name.equals(nt.name);
			} else if(module != null && nt.module != null) {
				return name.equals(nt.name) && module.equals(nt.module);
			} // otherwise, not equal
		} 
		return false;
	}
	
	public Type substitute(Map<String, Type> binding) {
		return new NamedType(module,name,type.substitute(binding));
	}
	
	public int hashCode() {
		return name.hashCode();
	}
	
	public String toString() {
		return module + ":" + name + "[" + type + "]";
	}
	
	public <T> Set<T> match(Class<T> t) {
		Set<T> r = type.match(t);
		if(NamedType.class == t) {
			r = new HashSet(r);
			r.add((T)this);			
		} 
		return r;		
	}
	
	public WType convert() {
		return type.convert();
	}
}
