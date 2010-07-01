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

package wyjc.ast.exprs.logic;

import java.util.*;

import wyjc.ModuleLoader;
import wyjc.ast.attrs.*;
import wyjc.ast.exprs.*;
import wyjc.ast.exprs.list.*;
import wyjc.ast.exprs.tuple.*;
import wyjc.ast.types.*;
import wyjc.util.Pair;
import wyjc.util.ResolveError;
import wyjc.util.Triple;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;
import static wyone.theory.logic.WFormulas.*;
import static wyone.theory.numeric.WNumerics.*;


public class TypeEquals extends SyntacticElementImpl implements Condition {
	private Type type;
	private Expr lhs;
	private Condition rhs;
	
	public TypeEquals(Type type, Expr lhs, Condition rhs, Attribute... attributes) {
		super(attributes);		
		this.type = type;
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public TypeEquals(Type type, Expr lhs, Condition rhs, Collection<Attribute> attributes) {
		super(attributes);
		this.type = type;
		this.lhs = lhs;
		this.rhs = rhs;		
	}
	
	public BoolType type(Map<String,Type> environment) {		
		return Types.T_BOOL;
	}
	
	public Type lhsTest() {
		return type;
	}
	
	public Expr lhs() {
		return lhs;
	}
	
	public Condition rhs() {
		return rhs;
	}
	
	public Condition substitute(Map<String,Expr> binding) {
		Condition r = rhs.substitute(binding);		
		Expr l = lhs.substitute(binding);
		return new TypeEquals(type,l,r,attributes());
	}
	
	public Expr replace(Map<Expr, Expr> binding) {
		Expr t = binding.get(this);
		if (t != null) {
			return t;
		} else {
			Condition r = (Condition)  rhs.replace(binding);
			Expr l = lhs.replace(binding);
			return new TypeEquals(type, l,r, attributes());
		}
	}
	
	public <T> List<T> match(Class<T> match) {
		List<T> matches = lhs.match(match);
		matches.addAll(rhs.match(match));
		if(match.isInstance(this)) {
			matches.add((T)this);
		}
		return matches;
	}
	
	public Set<Variable> uses() {
		Set<Variable> r = lhs.uses();
		r.addAll(rhs.uses());
		return r;
	}
	
	public Condition reduce(Map<String, Type> environment) {
		Expr l = lhs.reduce(environment);
		Condition r = rhs.reduce(environment);				
		
		Type t = l.type(environment);
		
		if (type.isSubtype(t, Collections.EMPTY_MAP)) {			
			return rhs;
		} else if (!t.isSubtype(type, Collections.EMPTY_MAP) || l instanceof Value) {
			return new BoolVal(false);
		}
		return new TypeEquals(type, l,r, attributes());
	}
	
	public String toString() {
		return "(" + lhs + " ~= " + type + " && " + rhs + ")";
	}

	public Triple<WExpr, WFormula, WEnvironment> convert(Map<String, Type> environment, ModuleLoader loader) throws ResolveError {
		return rhs.convert(environment, loader);		
	}

	public Triple<WFormula, WFormula, WEnvironment> convertCondition(
			Map<String, Type> environment, ModuleLoader loader)
			throws ResolveError {
		LVal lv = (LVal) lhs; // should be valid after reduction
		List<Expr> fs = lv.flattern();
		String var = ((Variable) fs.get(0)).name();
		Type t = update(lv, type, environment);
		environment = new HashMap<String, Type>(environment);
		environment.put(var, t);
		return rhs.convertCondition(environment, loader);
	}  	
	
	private Type update(LVal lv, Type t, Map<String, Type> environment) {
		if(lv instanceof Variable) {			
			return t;
		} else if(lv instanceof TupleAccess) {
			TupleAccess ta = (TupleAccess) lv;
			TupleType old = (TupleType) ta.source().type(environment).flattern();
			HashMap<String,Type> types = new HashMap(old.types());
			types.put(ta.name(), t);
			return new TupleType(types);			
		} else {
			return lv.type(environment); // temporary fow now
		}
	}
}
