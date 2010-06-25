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

import wyone.core.*;

public class WFunType implements WType {
	private final WType returnType;
	private final ArrayList<WType> parameterTypes;
	
	public WFunType(WType returnType, WType... parameterTypes) {
		this.returnType = returnType;
		this.parameterTypes = new ArrayList<WType>();
		for(WType t : parameterTypes) {
			this.parameterTypes.add(t);
		}
	}
	
	public WFunType(WType returnType, Collection<WType> parameterTypes) {
		this.returnType = returnType;
		this.parameterTypes = new ArrayList<WType>(parameterTypes);		
	}
	
	public boolean isSubtype(WValue v) {
		return false; // impossible
	}
	
	public WType returnType() {
		return returnType;
	}
	
	public List<WType> parameterTypes() {
		return parameterTypes;
	}
	
	public int hashCode() {
		return returnType.hashCode() + parameterTypes.hashCode();
	}
	
	public boolean equals(Object o) {
		if (o instanceof WFunType) {
			WFunType wt = (WFunType) o;
			return returnType.equals(wt.returnType)
					&& parameterTypes.equals(wt.parameterTypes);
		}
		return false;
	}
	
	public String toString() {
		String r = "(";
		boolean firstTime=true;
		for(WType p : parameterTypes) {
			if(!firstTime) {
				r += ",";
			}
			firstTime=false;
			r += p;
		}
		return returnType + r + ")";
	}
}
