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

package wyjc.ast;

import java.util.*;

import wyjc.ast.exprs.Condition;
import wyjc.ast.exprs.Value;
import wyjc.ast.types.FunType;
import wyjc.ast.types.Type;
import wyjc.ast.types.unresolved.UnresolvedType;
import wyjc.util.ModuleID;

public class ModuleInfo extends SkeletonInfo {		
	private HashMap<String,List<Method>> methods;
	private HashMap<String,TypeDef> types;
	private HashMap<String,Const> constants;
	
	public ModuleInfo(ModuleID mid,
			Map<String, List<Method>> methods,
			Map<String, TypeDef> types,
			Map<String, Const> constants) {
		super(mid);		
		this.methods = new HashMap<String, List<Method>>(methods);
		this.types = new HashMap<String, TypeDef>(types);
		this.constants = new HashMap<String, Const>(constants);
	}
	
	public ModuleID id() {
		return mid;
	}
	
	public UnresolvedType unresolvedType(String name) {
		TypeDef td = types.get(name);
		if(td == null) { return null; }
		else {
			return td.type();
		}
	}
	
	public TypeDef type(String name) {
		return types.get(name);
	}
	
	public Const constant(String name) {
		return constants.get(name);
	}
	
	public List<Method> method(String name) {
		List<Method> ls= methods.get(name);
		if(ls == null) {
			return new ArrayList<Method>();
		} else {
			return ls;
		}
	}
	
	public List<Method> method(String name, FunType ft) {
		List<Method> ls = methods.get(name);
		ArrayList<Method> rs = new ArrayList<Method>();
		if(ls != null) {
			for(Method m : ls) {
				if(m.type().equals(ft)) {
					rs.add(m);
				}
			}
		}
		return rs;
	}
	
	public boolean hasName(String name) {		
		return types.get(name) != null || constants.get(name) != null
				|| method(name).size() > 0;
	}
	
	public static class TypeDef {
		private String name;
		private Type type;

		public TypeDef(String name, Type type) {			
			this.name = name;
			this.type = type;			
		}

		public String name() {
			return name;
		}

		public Type type() {
			return type;
		}
	}
	
	public static class Const {
		private String name;		
		private Value constant;
		
		public Const(String name, Value constant) {
			this.name = name;
			this.constant = constant;
		}
		
		public String name() {
			return name;
		}
		
		public Value constant() {
			return constant;
		}
	}
	
	public static class Method {
		private String name;
		private Type receiver;
		private FunType type;
		private ArrayList<String> paramNames;
		private Condition preCondition;
		private Condition postCondition;
		
		public Method(Type receiver, String name, FunType type, Collection<String> paramNames, Condition preCondition, Condition postCondition) {
			this.receiver = receiver;
			this.name = name;
			this.type = type;
			this.paramNames = new ArrayList<String>(paramNames);
			this.preCondition = preCondition;
			this.postCondition = postCondition;
		}
		
		public Type receiver() {
			return receiver;
		}
		
		public String name() {
			return name;
		}
		
		public FunType type() {
			return type;
		}
		
		public boolean isFunction() {
			return receiver == null;
		}
		
		public Condition preCondition() {
			return preCondition;
		}
		
		public Condition postCondition() {
			return postCondition;
		}
		
		public List<String> parameterNames() {
			return paramNames;
		}
	}
}
