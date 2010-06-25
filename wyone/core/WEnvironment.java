// This file is part of the Wyone automated theorem prover.
//
// Wyone is free software; you can redistribute it and/or modify 
// it under the terms of the GNU General Public License as published 
// by the Free Software Foundation; either version 3 of the License, 
// or (at your option) any later version.
//
// Wyone is distributed in the hope that it will be useful, but 
// WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See 
// the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with Wyone. If not, see <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyone.core;

import java.util.*;

public class WEnvironment implements Iterable<Map.Entry<String,WType>> {
	private final HashMap<String,WType> types = new HashMap<String,WType>();
	
	/**
	 * Set the type of a variable in the typing environment.
	 * 
	 * @param var
	 * @param type
	 */
	public void add(String var,WType type) {
		WType old = types.get(var);
		if (old != null && !old.equals(type)) {			
			throw new IllegalArgumentException("Variable " + var
					+ " cannot have types: " + type + " & " + old);
		}
		types.put(var, type);
	}

	public void addAll(WEnvironment wenv) {
		for (Map.Entry<String, WType> e : wenv.types.entrySet()) {
			add(e.getKey(), e.getValue());
		}
	}
	
	public void remove(String var) {
		types.remove(var);
	}
	
	/**
	 * Clear all types for the given variables.
	 * 
	 * @param vars
	 */
	public void clearAll(Collection<String> vars) {
		for(String v : vars) {
			types.remove(v);
		}
	}
	
	
	public WEnvironment clone() {
		WEnvironment r = new WEnvironment();
		r.types.putAll(types);
		return r;
	}
	
	/**
	 * The full type returns the true type of the variable in question. This may
	 * be a function type, if the variable accepts arguments.
	 * 
	 * @param var
	 * @return
	 */
	public WType fullType(String var) {
		return types.get(var);
	}

	/**
	 * This returns type that this expression will evaluate to. So, in the case
	 * of a function, then this will be its return type.
	 * 
	 * @param var
	 * @return
	 */
	public WType evalType(String var) {
		WType t = types.get(var);
		if(t instanceof WFunType) {
			WFunType ft = (WFunType) t;
			return ft.returnType();
		} else {
			return t;
		}
	}
	
	public Iterator<Map.Entry<String,WType>> iterator() {
		return types.entrySet().iterator();
	}
	
	public String toString() {
		String r = "[";
		boolean firstTime=true;
		for(Map.Entry<String,WType> e : types.entrySet()) {
			if(!firstTime) {
				r += ", ";
			}
			firstTime=false;
			r += e.getKey() + "->" + e.getValue();
		}
		return r + "]";
	}
}
