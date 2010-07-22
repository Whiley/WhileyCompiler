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

import java.util.*;

import wyjc.ast.exprs.Condition;
import wyone.core.WType;


public class FunType extends ConstrainedType implements Type {
	private Type preState;
	private Type postState;

	/**
	 * Construct an unconstrained function type. The preState represents the
	 * state going into the function, whilst the postState represents that
	 * coming out of the function.
	 * 
	 * @param preState
	 * @param postState
	 */
	public FunType(Type preState, Type postState) {
		this.preState = preState;
		this.postState = postState;		
	}

	/**
	 * Construct a constrained function type. The preState represents the state
	 * going into the function, whilst the postState represents that coming out
	 * of the function. In the constraint, "$" refers to the postState, whilst
	 * "#" refers to the preState.
	 * 
	 * @param preState
	 * @param postState
	 */
	public FunType(Type preState, Type postState, Condition constraint) {
		this.preState = preState;
		this.postState = postState;		
	}

	public Type preState() {
		return preState;
	}

	public Type postState() {
		return postState;
	}
	
	public Type flattern() {		
		return new FunType(preState.flattern(), postState.flattern(),
				constraint);
	}

	public boolean isExistential() {		
		return preState.isExistential() || postState.isExistential();
	}
	
	public boolean equals(Object o) {
		if (o instanceof FunType) {
			FunType ft = (FunType) o;
			return preState.equals(ft.preState)
					&& postState.equals(ft.postState); 					
		}
		return false;
	}

	public int hashCode() {
		int hc = constraint == null ? 0 : constraint.hashCode();
		return preState.hashCode() + postState.hashCode() + hc;
	}
	
	public Type substitute(Map<String, Type> binding) {
		Type pre = preState.substitute(binding);
		Type post = postState.substitute(binding);
				
		return new FunType(pre,post,constraint);
	}
	
	public String toString() {
		return preState.toString() + " => " + postState + super.toString();			
	}
	
	public <T> Set<T> match(Class<T> type) {
		HashSet<T> r = new HashSet<T>(preState.match(type));
		r.addAll(postState.match(type));
		
		if(FunType.class == type) {			
			r.add((T)this);			
		} 
		
		return r;		
	}
	
	public WType convert() {
		throw new RuntimeException("cannot convert FunType to WType");
	}
}
