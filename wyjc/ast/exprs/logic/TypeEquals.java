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
import wyjc.ast.exprs.list.RangeVal;
import wyjc.ast.exprs.tuple.*;
import wyjc.ast.types.*;
import wyjc.util.Pair;
import wyjc.util.ResolveError;
import wyjc.util.Triple;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.type.WTypes;
import static wyone.theory.logic.WFormulas.*;

public class TypeEquals extends SyntacticElementImpl implements Condition {
	private Type type;
	private String var;
	private Expr lhs;
	private Condition rhs;
	
	public TypeEquals(Type type, String var, Expr lhs, Condition rhs, Attribute... attributes) {
		super(attributes);		
		this.type = type;
		this.var = var;
		this.lhs = lhs;
		this.rhs = rhs;		
	}

	public TypeEquals(Type type, String var, Expr lhs, Condition rhs, Collection<Attribute> attributes) {
		super(attributes);
		this.type = type;
		this.var = var;
		this.lhs = 	lhs;
		this.rhs = rhs;		
	}
	
	public BoolType type(Map<String,Type> environment) {		
		return Types.T_BOOL;
	}
	
	public Type lhsTest() {
		return type;
	}
	
	public String variable() {
		return var;
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
		return new TypeEquals(type,var,l,r,attributes());
	}
	
	public Expr replace(Map<Expr, Expr> binding) {
		Expr t = binding.get(this);
		if (t != null) {
			return t;
		} else {
			Condition r = (Condition)  rhs.replace(binding);
			Expr l = lhs.replace(binding);
			return new TypeEquals(type,var,l,r, attributes());
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
		r.remove(new Variable(var));
		return r;
	}
	
	public Condition reduce(Map<String, Type> environment) {			
		Expr l = lhs.reduce(environment);
		HashMap<String,Type> nenv = new HashMap<String,Type>(environment);
		nenv.put(var,type);		
		Condition r = rhs.reduce(nenv);		
		Type t = l.type(environment);
		
		if(type.isBaseSubtype(t, Collections.EMPTY_MAP)) {			
			HashMap<String,Expr> binding = new HashMap<String,Expr>();
			binding.put(var, l);			
			return r.substitute(binding);
		} else if (!t.isBaseSubtype(type, Collections.EMPTY_MAP) || l instanceof Value) {
			return new BoolVal(false);
		}
		return new TypeEquals(type, var, l, r, attributes());
	}
	
	public String toString() {
		return "(" + lhs + "[" + var + "] ~= " + type + " && " + rhs + ")";
	}	

	public Pair<WExpr,WFormula> convert(Map<String, Type> environment, ModuleLoader loader) throws ResolveError {
		return rhs.convert(environment, loader);		
	}

	public int hashCode() {
		return lhs.hashCode() + rhs.hashCode();
	}
	
	public boolean equals(Object o) {
		if(o instanceof TypeEquals) {
			TypeEquals e = (TypeEquals) o;
			HashMap<String,Expr> binding = new HashMap<String,Expr>();
			binding.put(e.var,new Variable(var));
			// the following substitution is necessary to ensure we're
			// equivalent upto variable renaming.
			Expr e_rhs = e.rhs.substitute(binding);
			return lhs.equals(e.lhs) && rhs.equals(e_rhs);
		}
		return false;
	}
	
	public Pair<WFormula, WFormula> convertCondition(
			Map<String, Type> environment, ModuleLoader loader)
			throws ResolveError {
		// FIXME: this is incomplete obviously
		
		Pair<WExpr,WFormula> l = lhs.convert(environment,loader);		
		WFormula constraints = l.second();
		
		environment = new HashMap<String, Type>(environment);
		environment.put(var, type);
		Pair<WFormula,WFormula> r = rhs.convertCondition(environment, loader);		
		constraints = and(constraints,r.second());
		WFormula condition = r.first();
		condition = and(WTypes.subtypeOf(l.first(), type.convert()), WExprs
				.equals(new WVariable(var), l.first()), condition);
		return new Pair(condition, constraints);		
	}  		
}
