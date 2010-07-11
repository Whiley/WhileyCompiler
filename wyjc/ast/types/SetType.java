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

import wyjc.ast.exprs.Condition;
import wyone.core.WType;
import wyone.theory.set.WSetType;

public class SetType extends ConstrainedType implements NonUnionType {
	private Type element;
	
	public SetType(Type element) {	
		this.element = element;
	}
	
	public SetType(Type element, Condition constraint) {
		super(constraint);
		this.element = element;
	}
	
	public Type element() {
		return element;
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof SetType) || o instanceof ListType) {
			return false;
		}
		SetType at = (SetType) o;
		return at.element.equals(element)
				&& (constraint == at.constraint || (constraint != null && constraint
						.equals(at.constraint)));
	}
	
	public int hashCode() {
		int hc = constraint == null ? 0 : constraint.hashCode();
		return (element.hashCode() * 123) + hc;
	}		
		
	public Type flattern() {
		return new SetType(element.flattern(),constraint);
	}
	
	public boolean isExistential() {
		return element.isExistential();
	}
		
	public String toString() {
		return "{" + element.toString() + "}" + super.toString();
	}
	
	public <T> Set<T> match(Class<T> type) {
		Set<T> r = element().match(type);
		if(ListType.class == type) {
			r = new HashSet(r);
			r.add((T)this);			
		} 
		return r;		
	}
	
	public Type substitute(Map<String, Type> binding) {
		return new SetType(element.substitute(binding),constraint);
	}
	
	public WType convert() {
		return new WSetType(element.convert());
	}
}
