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

import java.math.BigInteger;
import java.util.*;

import wyjc.ModuleLoader;
import wyjc.ast.attrs.Attribute;
import wyjc.ast.attrs.SyntacticElementImpl;
import wyjc.ast.exprs.*;
import wyjc.ast.types.ListType;
import wyjc.ast.types.Type;
import wyjc.ast.types.Types;
import wyjc.util.Pair;
import wyjc.util.ResolveError;
import wyjc.util.Triple;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.list.*;
import static wyone.theory.logic.WFormulas.*;

public final class ListGenerator extends SyntacticElementImpl implements Expr {
	private final ArrayList<Expr> values;

	public ListGenerator(List<Expr> value, Attribute... attributes) {
		super(attributes);
		this.values = new ArrayList<Expr>(value);
	}

	public ListGenerator(List<Expr> value, Collection<Attribute> attributes) {
		super(attributes);
		this.values = new ArrayList<Expr>(value);
	}
	
	public ListType type(Map<String,Type> environment) {		
		Type et = Types.T_VOID;
		for(Expr e : values) {
			Type t = e.type(environment);
			et = Types.leastUpperBound(et,t);
		}
		return new ListType(et);
	}
	
	public List<Expr> getValues() {
		return values;
	}
	
	public Expr substitute(Map<String,Expr> binding) {
		ArrayList<Expr> vals = new ArrayList<Expr>();
		for(Expr e : values) {
			vals.add(e.substitute(binding));
		}		
		return new ListGenerator(vals,attributes());
	}
	
	public Expr replace(Map<Expr,Expr> binding) {
		Expr t = binding.get(this);
		if(t != null) {
			return t;
		} else {
			ArrayList<Expr> vals = new ArrayList<Expr>();
			for(Expr e : values) {
				vals.add(e.replace(binding));
			}
			return new ListGenerator(vals,attributes());
		}
	}
    
    public <T> List<T> match(Class<T> match) {
		ArrayList<T> matches = new ArrayList<T>();
		for(Expr e : values) {
			matches.addAll(e.match(match));
		}
		if(match.isInstance(this)) {
			matches.add((T)this);
		}
		return matches;
	}
	
    public Set<Variable> uses() {
		HashSet<Variable> r = new HashSet<Variable>();
		for(Expr e : values) {
			r.addAll(e.uses());
		}		
		return r;
	}
	
	public Expr reduce(Map<String, Type> environment) {
		ArrayList<Expr> vs = new ArrayList<Expr>();
		boolean allValues = true;

		for (Expr e : values) {
			Expr r = e.reduce(environment);
			vs.add(r);
			allValues &= r instanceof Value;
		}

		if (allValues) {
			// this is a bit sneaky
			return new ListVal((ArrayList) vs, attributes());
		} else {
			return new ListGenerator(vs, attributes());
		}
	}
	

    public Triple<WExpr, WFormula, WEnvironment> convert(Map<String, Type> environment, ModuleLoader loader) throws ResolveError {    	
    	WFormula constraints = WBool.TRUE;
    	ArrayList<WExpr> ps = new ArrayList<WExpr>();
    	WEnvironment wenv = new WEnvironment();
    	for(Expr e : values) {
    		Triple<WExpr, WFormula, WEnvironment> p = e.convert(environment,loader);
    		wenv.addAll(p.third());
    		constraints = and(constraints,p.second());
    		ps.add(p.first());
    	}
		
		return new Triple<WExpr, WFormula, WEnvironment>(new WListConstructor(
				ps), constraints, wenv);
	}    
	
    public int hashCode() {
		return values.hashCode();
	}
	
	public boolean equals(Object o) {
		return (o instanceof ListGenerator) && ((ListGenerator) o).values.equals(values);
	}
    
	public String toString() { 
		String r = "[";
		boolean firstTime=true;
		for(Expr e : values) {
			if(!firstTime) {
				r += ", ";
			}
			firstTime=false;
			r += e.toString();
		}
		return r + "]";
	}
}
