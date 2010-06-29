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

import wyjc.util.NameID;
import wyone.core.WFunType;
import wyone.core.WType;


public class FunType implements Type {
	private Type ret;
	private ArrayList<Type> parameters;
	
	public FunType(Type ret, Collection<Type> parameters) {
		this.ret = ret;
		this.parameters = new ArrayList<Type>(parameters);
	}

	public Type returnType() {
		return ret;
	}

	public List<Type> parameters() {
		return Collections.unmodifiableList(parameters);
	}

	public boolean isSubtype(Type t, Map<NameID,Type> environment) {
		if(t instanceof NamedType) {
			t = ((NamedType)t).type();
		}
		if(t instanceof FunType) {
			FunType ft = (FunType) t;
			List<Type> ftparams = ft.parameters();
			if(ftparams.size() == parameters.size()) {
				for(int i=0;i!=ftparams.size();++i) {
					Type t1 = parameters.get(i);
					Type t2 = ftparams.get(i);
					if(!t1.isSubtype(t2,environment)) {
						return false;
					}
				}
				return ft.returnType().isSubtype(ret,environment);
			}
		} 
		return false;
	}	
	
	public Type flattern() {
		ArrayList<Type> fps = new ArrayList<Type>();
		for(Type p : parameters) {
			fps.add(p.flattern());
		}
		return new FunType(ret.flattern(),fps);
	}

	public boolean isExistential() {
		for(Type p : parameters) {
			if(p.isExistential()) {
				return true;
			}
		}
		return ret.isExistential();
	}
	
	public boolean equals(Object o) {
		if (o instanceof FunType) {
			FunType ft = (FunType) o;
			return ret.equals(ft.ret) && parameters.equals(ft.parameters);
		}
		return false;
	}

	public Type substitute(Map<NameID,NameID> binding) {
		Type retType = ret.substitute(binding);
		ArrayList<Type> params = new ArrayList<Type>();
		for(Type t : parameters) {
			params.add(t.substitute(binding));
		}
		return new FunType(retType,parameters);
	}
	
	public int hashCode() {
		return ret.hashCode() + parameters.hashCode();
	}
	
	public String toString() {
		String r = ret.toString() + "(";
		boolean firstTime=true;
		for(Type t : parameters) {
			if(!firstTime) {
				r += ",";
			}
			firstTime=false;
			r += t.toString();
		}
		return r + ")";		
	}
	
	public WType convert() {
		ArrayList<WType> params = new ArrayList<WType>();
		for(Type t : parameters) {
			params.add(t.convert());
		}
		return new WFunType(ret.convert(),params);
	}
}
