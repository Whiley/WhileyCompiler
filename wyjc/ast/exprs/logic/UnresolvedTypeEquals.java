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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wyjc.ModuleLoader;
import wyjc.ast.attrs.Attribute;
import wyjc.ast.attrs.SyntacticElementImpl;
import wyjc.ast.exprs.*;
import wyjc.ast.exprs.tuple.TupleAccess;
import wyjc.ast.types.*;
import wyjc.ast.types.unresolved.UnresolvedType;
import wyjc.util.Pair;
import wyjc.util.ResolveError;
import wyjc.util.Triple;
import wyone.core.WEnvironment;
import wyone.theory.logic.WBool;
import wyone.theory.logic.WFormula;

public class UnresolvedTypeEquals extends SyntacticElementImpl implements Condition {
	private UnresolvedType rhs;
	private Expr lhs;
	
	public UnresolvedTypeEquals(Expr lhs, UnresolvedType rhs, Attribute... attributes) {
		super(attributes);				
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public UnresolvedTypeEquals(Expr lhs, UnresolvedType rhs, Collection<Attribute> attributes) {
		super(attributes);	
		this.lhs = lhs;
		this.rhs = rhs;		
	}
	
	public BoolType type(Map<String,Type> environment) {		
		return Types.T_BOOL;
	}
	
	public Expr lhs() {
		return lhs;
	}
	
	public UnresolvedType rhs() {
		return rhs;
	}
	
	public Condition substitute(Map<String,Expr> binding) {		
		Expr l = lhs.substitute(binding);
		return new UnresolvedTypeEquals(l,rhs,attributes());
	}
	
	public Expr replace(Map<Expr, Expr> binding) {
		Expr t = binding.get(this);
		if (t != null) {
			return t;
		} else {			
			Expr l = lhs.replace(binding);
			return new UnresolvedTypeEquals(l,rhs, attributes());
		}
	}
	
	public <T> List<T> match(Class<T> match) {
		List<T> matches = lhs.match(match);		
		if(match.isInstance(this)) {
			matches.add((T)this);
		}
		return matches;
	}
	
	public Set<Variable> uses() {
		return lhs.uses();				
	}
	
	public Condition reduce(Map<String, Type> environment) {
		return this;
	}
	
	public String toString() {
		return lhs + " ~= " + rhs;
	}

	public Pair<WExpr,WFormula> convert(Map<String, Type> environment, ModuleLoader loader) throws ResolveError {
		// need to do better here.
		throw new IllegalArgumentException("Cannot convert UnresolvedTypeEquals");		
	}

	public Pair<WFormula, WFormula> convertCondition(
			Map<String, Type> environment, ModuleLoader loader)
			throws ResolveError {
		// need to do better here.
		throw new IllegalArgumentException("Cannot convert UnresolvedTypeEquals");		
	}  		
	
	public int hashCode() {
		return lhs.hashCode() + rhs.hashCode();
	}
	
	public boolean equals(Object o) {
		if(o instanceof UnresolvedTypeEquals) {
			UnresolvedTypeEquals e = (UnresolvedTypeEquals) o;
			return lhs.equals(e.lhs) && rhs.equals(e.rhs);
		}
		return false;
	}
}
