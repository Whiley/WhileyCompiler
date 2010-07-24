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

package wyjc.ast.exprs.list;

import java.util.*;

import wyjc.ModuleLoader;
import wyjc.ast.exprs.*;
import wyjc.ast.exprs.integer.*;
import wyjc.ast.types.ListType;
import wyjc.ast.types.Type;
import wyjc.ast.types.Types;
import wyjc.lang.Attribute;
import wyjc.lang.SyntacticElementImpl;
import wyjc.util.Pair;
import wyjc.util.ResolveError;
import wyjc.util.Triple;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;
import static wyone.theory.logic.WFormulas.*;
import static wyone.theory.numeric.WNumerics.*;

public class RangeVal extends SyntacticElementImpl implements Value,Expr {
	private IntVal start;
	private IntVal end;
	
	public RangeVal(IntVal start, IntVal end, Attribute... attributes) {
		super(attributes);		
		this.start=start;
		this.end=end;
	}

	public RangeVal(IntVal start, IntVal end, Collection<Attribute> attributes) {
		super(attributes);
		this.start=start;
		this.end=end;
	}
	
	public IntVal start() {
		return start;
	}
	
	public IntVal end() {
		return end;
	}
	
	public RangeVal substitute(Map<String,Expr> binding) {
		return this;
	}

	public Expr replace(Map<Expr,Expr> binding) {
		Expr t = binding.get(this);
		if(t != null) {
			return t;
		} else {
			return this;
		}
	}
    
    public <T> List<T> match(Class<T> match) {
		ArrayList<T> matches = new ArrayList<T>();
		matches.addAll(start.match(match));
		matches.addAll(end.match(match));		
		if(match.isInstance(this)) {
			matches.add((T)this);
		}
		return matches;
	}
	
	public Set<Variable> uses() {
		return new HashSet<Variable>();
	}
	
	public Type type(Map<String,Type> environment) {
		return type();
	}
	
	public Type type() {		
		return new ListType(Types.T_INT(null));
	}
	
	public Expr reduce(Map<String, Type> environment) {
		return this;
	}
	
	public int hashCode() {
		return start.hashCode() + end.hashCode();
	}
	
	public boolean equals(Object o) {
		if(o instanceof RangeVal) {
			RangeVal rg = (RangeVal) o;
			return start.equals(rg.start) && end.equals(rg.end);
		}
		return false;
	}

	public Pair<WExpr,WFormula> convert(Map<String, Type> environment,
			ModuleLoader loader) throws ResolveError {
		WVariable retVar = WVariable.freshVar();
		WFormula constraints;
		if(start.value().compareTo(end.value()) != 1) {
			constraints = and(lessThanEq(new WNumber(start.value()), retVar),
					lessThan(retVar, new WNumber(end.value())));
		} else {
			constraints = and(lessThan(new WNumber(end.value()), retVar),
					lessThanEq(retVar, new WNumber(start.value())));
		}		
		return new Pair<WExpr,WFormula>(retVar, constraints);		
	}
	
	public String toString() { 
		return start + ".." + end; 
	}
}
