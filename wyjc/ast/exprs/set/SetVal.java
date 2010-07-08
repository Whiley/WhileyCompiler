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
import wyjc.ast.types.*;
import wyjc.util.Pair;
import wyjc.util.ResolveError;
import wyjc.util.Triple;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.set.*;
import static wyone.theory.logic.WFormulas.*;

public class SetVal extends SyntacticElementImpl implements Expr, Value {
	private final HashSet<Value> values;

	public SetVal(Collection<Value> value, Attribute... attributes) {
		super(attributes);
		this.values = new HashSet<Value>(value);
	}

	public SetVal(Collection<Value> value, Collection<Attribute> attributes) {
		super(attributes);
		this.values = new HashSet<Value>(value);
	}
	
	public Set<Value> getValues() {
		return values;
	}

	public SetVal substitute(Map<String,Expr> binding) {
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
				return new SetVal(vals,attributes());
			} else {
				return new SetGenerator(vals,attributes());
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
	
    public Type type(Map<String,Type> env) {
    	return type();
    }
    
	public Type type() {
		Type et = Types.T_VOID;
		for(Value v : values) {
			et = Types.leastUpperBound(et,v.type());
		}
		return new SetType(et);
	}
	
	public Set<Variable> uses() {
		return new HashSet<Variable>();
	}

	public Expr reduce(Map<String, Type> environment) {
		return this;
	}

	public int hashCode() {
		return values.hashCode();
	}
	
	public Triple<WExpr, WFormula, WEnvironment> convert(Map<String, Type> environment,
			ModuleLoader loader) throws ResolveError {						
		
		WFormula constraints = WBool.TRUE;
		HashSet<WValue> wvalues = new HashSet<WValue>();
		WEnvironment wenv = new wyone.util.WHashEnv();
		
		for(Expr e : values) {
			Triple<WExpr, WFormula, WEnvironment> p = e.convert(environment,loader);
			wenv.putAll(p.third());
			constraints = and(constraints,p.second());
			wvalues.add((WValue) p.first());
		}
		
		return new Triple<WExpr, WFormula, WEnvironment>(new WSetVal(wvalues), constraints, wenv);
	}
	
	public boolean equals(Object o) {
		return (o instanceof SetVal) && ((SetVal) o).values.equals(values);
	}
	
	public String toString() { 
		ArrayList<String> ss = new ArrayList<String>();
		for(Expr e : values) {
			ss.add(e.toString());
		}
		Collections.sort(ss);
		
		String r = "{";		
		boolean firstTime=true;
		for(String s : ss) {
			if(!firstTime) {
				r += ", ";
			}
			firstTime=false;
			r += s;
		}
		return r + "}";
	}
}
