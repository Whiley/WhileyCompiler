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

package wyjc.ast.exprs.set;

import java.util.*;

import wyjc.ModuleLoader;
import wyjc.ast.attrs.Attribute;
import wyjc.ast.attrs.SyntacticElementImpl;
import wyjc.ast.exprs.*;
import wyjc.ast.types.SetType;
import wyjc.ast.types.Type;
import wyjc.ast.types.Types;
import wyjc.util.Pair;
import wyjc.util.ResolveError;
import wyjc.util.Triple;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.set.*;
import static wyone.theory.logic.WFormulas.and;

public class SetGenerator extends SyntacticElementImpl implements Expr {
	private final ArrayList<Expr> values;

	public SetGenerator(List<Expr> value, Attribute... attributes) {
		super(attributes);
		this.values = new ArrayList<Expr>(value);
	}

	public SetGenerator(List<Expr> value, Collection<Attribute> attributes) {
		super(attributes);
		this.values = new ArrayList<Expr>(value);
	}
	
	public List<Expr> getValues() {
		return values;
	}
	
	public Type type(Map<String,Type> environment) {
		Type et = Types.T_VOID;
		for (Expr e : values) {
			et = Types.leastUpperBound(et, e.type(environment));
		}
		return new SetType(et);
	}

	public Expr substitute(Map<String,Expr> binding) {
		ArrayList<Expr> vals = new ArrayList<Expr>();
		
		for(Expr v : values) {
			vals.add(v.substitute(binding));
		}
		
		return new SetGenerator(vals,attributes());
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
			
			return new SetGenerator(vals,attributes());			
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
			Expr r = (Expr) e.reduce(environment);
			vs.add(r);
			allValues &= r instanceof Value;
		}

		if (allValues) {
			// this is a bit sneaky
			return new SetVal((Collection)vs, attributes());
		} else {
			return new SetGenerator(vs, attributes());
		}		
	}
	
	public Pair<WExpr,WFormula> convert(Map<String, Type> environment,
			ModuleLoader loader) throws ResolveError {						
		
		WFormula constraints = WBool.TRUE;
		HashSet<WExpr> wvalues = new HashSet<WExpr>();
		WEnvironment wenv = new wyone.util.WHashEnv();
		
		for(Expr e : values) {
			Pair<WExpr,WFormula> p = e.convert(environment,loader);
			wenv.putAll(p.third());
			constraints = and(constraints,p.second());
			wvalues.add(p.first());
		}
		
		return new Pair<WExpr,WFormula>(new WSetConstructor(wvalues), constraints, wenv);
	}
	
	public String toString() { 
		String r = "{";
		boolean firstTime=true;
		for(Expr e : values) {
			if(!firstTime) {
				r += ", ";
			}
			firstTime=false;
			r += e.toString();
		}
		return r + "}";
	}
	
	public boolean equals(Object o) {
		if(o instanceof SetGenerator) {
			SetGenerator s = (SetGenerator) o;
			return values.equals(s.values);
		}
		return false;
	}
	
	public int hashCode() {
		return values.hashCode();
	}
}
