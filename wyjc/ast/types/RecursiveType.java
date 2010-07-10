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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import wyjc.ast.exprs.Condition;
import wyjc.util.*;
import wyone.core.WType;
import wyone.theory.type.WRecursiveType;

/**
 * A NameType is simply a type which has been defined and, hence, has a name
 * associated with it.
 * 
 * @author djp
 * 
 */
public class RecursiveType extends ConstrainedType implements NonUnionType {	
	private final String name;
	private final Type type; // underlying type
	
	// care needed here to avoid capture.	
	public RecursiveType(String name, Type type, Condition constraint) {
		super(constraint);
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
		
	public boolean isSubtype(Type t, Map<String, Type> environment) {
		// this is a delicate method; don't mess with it unless you know what
		// you're doing.	
		if(equals(t)) {
			return true;
		} else if(type == null) {			
			// leaf case, so unroll one level
			Type tmp = environment.get(name);			
			return tmp != null && tmp.isSubtype(t,environment);
		} else if(t instanceof RecursiveType) {
			// non-leaf case
			RecursiveType nt = (RecursiveType) t;			
			Type nt_type = nt.type();
			HashMap<String,Type> binding = new HashMap();
			binding.put(nt.name(), new RecursiveType(name,null,null));
			t = nt_type.substitute(binding);
		}
		
		environment = new HashMap<String,Type>(environment);
		// FIXME: potential for variable capture here?
		environment.put(name, type);
		return type.isSubtype(t,environment);				
	}
	
	public boolean isExistential() {
		return false;
	}
	
	public Type substitute(Map<String, Type> binding) {		
		// FIXME: Potential for name capture bug here. Uncertain if it can
		// definitely happen though.			
		if(type != null) {
			Type tt = type.substitute(binding);
			Type t = binding.get(name);
			String n = name;
			if(t instanceof RecursiveType) { 
				// FIXME: somehow this feels like a hack. I'm sure there's a
				// more elegant way of doing this.
				RecursiveType rt = (RecursiveType) t;
				n = rt.name();
			}
			return new RecursiveType(n,tt,constraint);			
		} else {
			Type t = binding.get(name);
			if(t == null) { return this; }
			else {
				return t;
			}
		}
	}
	
	public <T> Set<T> match(Class<T> t) {
		Set<T> r;
		if(type == null) {
			r = Collections.EMPTY_SET;
		} else {
			r = type.match(t);
		}
		if(RecursiveType.class == t) {
			r = new HashSet(r);
			r.add((T)this);			
		} 
		return r;		
	}
	
	public boolean equals(Object o) {
		if(o instanceof RecursiveType) {
			RecursiveType nt = (RecursiveType) o;			
			return name.equals(nt.name)
					&& (type == nt.type || (type != null && type
							.equals(nt.type)))
					&& (constraint == nt.constraint || (constraint != null && constraint
							.equals(nt.constraint)));
		} 
		return false;
	}
			
	public int hashCode() {
		int hc = constraint == null ? 0 : constraint.hashCode();
		return name.hashCode() + hc;
	}
	
	public String toString() {
		if(type != null) {
			return name + "[" + type + "]";
		} else {
			return name.toString();
		}
	}
	
	public WType convert() {
		if(type == null) {
			return new WRecursiveType(name,null);
		} else {
			return new WRecursiveType(name,type.convert());			
		}
	}	
}
