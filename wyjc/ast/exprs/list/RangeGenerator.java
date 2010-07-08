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
import wyjc.ast.attrs.Attribute;
import wyjc.ast.attrs.SyntacticElementImpl;
import wyjc.ast.exprs.*;
import wyjc.ast.exprs.integer.*;
import wyjc.ast.types.ListType;
import wyjc.ast.types.Type;
import wyjc.ast.types.Types;
import wyjc.util.Pair;
import wyjc.util.ResolveError;
import wyjc.util.Triple;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;
import wyone.theory.list.*;
import static wyone.theory.logic.WFormulas.*;
import static wyone.theory.numeric.WNumerics.*;

public class RangeGenerator extends SyntacticElementImpl implements Expr {
	private Expr start;
	private Expr end;
	
	public RangeGenerator(Expr start, Expr end, Attribute... attributes) {
		super(attributes);
		this.start=start;
		this.end=end;
	}

	public RangeGenerator(Expr start, Expr end, Collection<Attribute> attributes) {
		super(attributes);
		this.start=start;
		this.end=end;
	}
	
	public Expr start() { 
		return start;
	}
	
	public Expr end() {
		return end;
	}
	
	public RangeGenerator substitute(Map<String,Expr> binding) {
		return this;
	}

	public Expr replace(Map<Expr,Expr> binding) {
		Expr t = binding.get(this);
		if(t != null) {
			return t;
		} else {
			Expr st = start.replace(binding);
			Expr ed = end.replace(binding);
			if(st instanceof IntVal && ed instanceof IntVal) {
				return new RangeVal((IntVal)st,(IntVal)ed,attributes());
			} else {
				return new RangeGenerator(st,ed,attributes());
			}
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
		Set<Variable> uses = start.uses();
		uses.addAll(end.uses());
		return uses;
	}
	
	public Type type(Map<String,Type> environment) {				
		return new ListType(Types.T_INT);
	}
	
	public Expr reduce(Map<String, Type> environment) {
		return this;
	}
	
	public int hashCode() {
		return start.hashCode() + end.hashCode();
	}
	
	public boolean equals(Object o) {
		if(o instanceof RangeGenerator) {
			RangeGenerator rg = (RangeGenerator) o;
			return start.equals(rg.start) && end.equals(rg.end);
		}
		return false;
	}

	public Pair<WExpr,WFormula> convert(
			Map<String, Type> environment, ModuleLoader loader)
			throws ResolveError {
		WVariable retVar = WVariable.freshVar();
		
		Pair<WExpr,WFormula> st = start.convert(environment,
				loader);
		Pair<WExpr,WFormula> ed = end.convert(environment,
				loader);
		WFormula lhs = and(lessThanEq(st.first(), ed.first()), lessThanEq(st
				.first(), retVar), lessThan(retVar, ed.first()));
		WFormula rhs = and(lessThan(st.first(), ed.first()), lessThanEq(ed
				.first(), retVar), lessThanEq(retVar, st.first()));
		WFormula constraints = and(or(lhs, rhs), st.second(), ed.second());

		WEnvironment wenv = st.third();
		wenv.putAll(ed.third());
		wenv.put(retVar.name(),new WListType(WIntType.T_INT));
		return new Pair<WExpr,WFormula>(retVar, constraints,
				wenv);
	}
	
	public String toString() { 
		return start + ".." + end; 
	}
}
