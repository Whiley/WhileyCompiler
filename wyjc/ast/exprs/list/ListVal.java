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
import wyjc.ast.types.*;
import wyjc.lang.Attribute;
import wyjc.lang.SyntacticElementImpl;
import wyjc.util.Pair;
import wyjc.util.ResolveError;
import wyjc.util.Triple;
import wyone.core.*;
import wyone.theory.list.*;
import wyone.theory.logic.*;
import static wyone.theory.logic.WFormulas.*;

public class ListVal extends SyntacticElementImpl implements Expr,Value {
	private final ArrayList<Value> values;

	public ListVal(List<Value> value, Attribute... attributes) {
		super(attributes);
		this.values = new ArrayList<Value>(value);
	}

	public ListVal(List<Value> value, Collection<Attribute> attributes) {
		super(attributes);
		this.values = new ArrayList<Value>(value);
	}
	
	public List<Value> getValues() {
		return values;
	}

	public ListVal substitute(Map<String,Expr> binding) {
		return this;
	}

	public Expr replace(Map<Expr,Expr> binding) {
		Expr t = binding.get(this);
		if(t != null) {
			return t;
		} else {
			ArrayList vals = new ArrayList();
			boolean isVal = true;
			for(Expr e : values) {
				e = e.replace(binding);
				isVal = isVal & e instanceof Value;
				vals.add(e);
			}
			
			if(isVal) {
				return new ListVal(vals,attributes());
			} else {
				return new ListGenerator(vals,attributes());
			}
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
		return new HashSet<Variable>();
	}
	
	public Type type(Map<String,Type> environment) {
		return type();
	}
	
	public Type type() {
		Type et = Types.T_VOID;
		for(Value v : values) {
			et = Types.leastUpperBound(et,v.type());
		}
		return new ListType(et);
	}
	
	public Expr reduce(Map<String, Type> environment) {
		return this;
	}
	
	public int hashCode() {
		return values.hashCode();
	}
	
	public boolean equals(Object o) {
		return (o instanceof ListVal) && ((ListVal) o).values.equals(values);
	}

	public Pair<WExpr,WFormula> convert(Map<String, Type> environment, ModuleLoader loader) throws ResolveError {    	
		WFormula constraints = WBool.TRUE;
		ArrayList<WValue> ps = new ArrayList<WValue>();
		
		for(Expr e : values) {			
			Pair<WExpr,WFormula> p = e.convert(environment,loader);			
			constraints = and(constraints,p.second());			
			ps.add((WValue) p.first());
		}

		return new Pair<WExpr,WFormula>(new WListVal(ps),
				constraints);
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
