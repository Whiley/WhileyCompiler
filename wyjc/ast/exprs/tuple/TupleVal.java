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

package wyjc.ast.exprs.tuple;

import java.util.*;

import wyjc.ModuleLoader;
import wyjc.ast.exprs.*;
import wyjc.ast.types.TupleType;
import wyjc.ast.types.Type;
import wyjc.lang.Attribute;
import wyjc.lang.SyntacticElementImpl;
import wyjc.util.Pair;
import wyjc.util.ResolveError;
import wyjc.util.Triple;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;
import wyone.theory.tuple.*;
import static wyone.theory.logic.WFormulas.*;
import static wyone.theory.numeric.WNumerics.*;


public final class TupleVal extends SyntacticElementImpl implements Value {
	private final HashMap<String,Value> values;
	
	public TupleVal(Map<String,Value> exprs, Attribute... attributes) {
		super(attributes);
		this.values = new HashMap(exprs);
	}
	
	public TupleVal(Map<String,Value> exprs, Collection<Attribute> attributes) {
		super(attributes);
		this.values = new HashMap(exprs);
	}
	
	public Expr replace(Map<Expr,Expr> binding) {
		Expr t = binding.get(this);
		if(t != null) {
			return t;
		} else {
			HashMap map = new HashMap();
			boolean allVals = true;
			for(Map.Entry<String,Value> v : values.entrySet()) {
				Expr e = v.getValue().replace(binding);
				map.put(v.getKey(), e);
				allVals = allVals & e instanceof Value;
			}
			if(allVals) {
				return new TupleVal(map,attributes());
			} else {
				return new TupleGenerator(map,attributes());
			}
		}
	}
	
	public <T> List<T> match(Class<T> match) {
		List<T> matches = new ArrayList();
		for(Map.Entry<String,Value> v : values.entrySet()) {
			matches.addAll(v.getValue().match(match));
		}
		if(match.isInstance(this)) {
			matches.add((T)this);
		}
		return matches;
	}
	
	public TupleType type(Map<String,Type> env) {
		return type();
	}
	
	public TupleType type() {
		HashMap<String,Type> ts = new HashMap<String,Type>();
		for(Map.Entry<String,Value> e : values.entrySet()) {
			ts.put(e.getKey(),e.getValue().type());
		}
		return new TupleType(ts);
	}
	
	public Map<String,Value> values() {
		return values;
	}
				
	public TupleVal substitute(Map<String,Expr> binding) {
		HashMap<String,Value> es = new HashMap();
		for(Map.Entry<String,Value> e : values.entrySet()) {
			es.put(e.getKey(), e.getValue().substitute(binding));
		}
		return new TupleVal(es,attributes());
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
	
	public boolean equals(Object o) {
		return (o instanceof TupleVal) && ((TupleVal)o).values.equals(values);
	}
	
	public Pair<WExpr,WFormula> convert(Map<String, Type> environment,
			ModuleLoader loader) throws ResolveError {
		WFormula constraints = WBool.TRUE;
		ArrayList<String> fields = new ArrayList(values.keySet());
		Collections.sort(fields);		
		ArrayList<WValue> params = new ArrayList<WValue>();
		for (String field : fields) {
			Value e = values.get(field);			
			Pair<WExpr,WFormula> src = e.convert(environment, loader);
			constraints = WFormulas.and(constraints, src.second());
			params.add((WValue) src.first());
		}

		return new Pair<WExpr,WFormula>(new WTupleVal(fields,params), constraints);
	}
	
	public String toString() {
		String r = "(";
		boolean firstTime = true;

		ArrayList<String> ss = new ArrayList<String>(values.keySet());
		Collections.sort(ss);

		for (String s : ss) {
			if (!firstTime) {
				r = r + ",";
			}
			firstTime = false;
			r = r + s + ":" + values.get(s).toString();
		}
		return r + ")";
	}
}
