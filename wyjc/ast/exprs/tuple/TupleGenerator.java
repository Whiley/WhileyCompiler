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
import wyjc.ast.*;
import wyjc.ast.exprs.*;
import wyjc.ast.types.TupleType;
import wyjc.ast.types.Type;
import wyjc.lang.Attribute;
import wyjc.lang.SyntacticElementImpl;
import wyjc.util.*;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;
import wyone.theory.tuple.*;
import static wyone.theory.logic.WFormulas.*;
import static wyone.theory.numeric.WNumerics.*;


public class TupleGenerator extends SyntacticElementImpl implements Expr,Iterable<Map.Entry<String,Expr>> {
	HashMap<String,Expr> exprs;
	
	public TupleGenerator(Map<String,Expr> exprs, Attribute... attributes) {
		super(attributes);
		this.exprs = new HashMap(exprs);
	}
	
	public TupleGenerator(Map<String,Expr> exprs, Collection<Attribute> attributes) {
		super(attributes);
		this.exprs = new HashMap(exprs);
	}
	
	public Map<String,Expr> values() {
		return exprs;
	}
	
	public TupleType type(Map<String,Type> environment) {
		HashMap<String,Type> ts = new HashMap<String,Type>();
		for(Map.Entry<String,Expr> e : exprs.entrySet()) {
			ts.put(e.getKey(),e.getValue().type(environment));
		}
		return new TupleType(ts);
	}
	
	public Iterator<Map.Entry<String,Expr>> iterator() {
		return exprs.entrySet().iterator();
	}
	
	public Expr get(String i) {
		return exprs.get(i);
	}
	
	public int size() {
		return exprs.size();
	}
		
	public Expr substitute(Map<String,Expr> binding) {
		HashMap<String,Expr> es = new HashMap();
		for(Map.Entry<String,Expr> e : exprs.entrySet()) {
			es.put(e.getKey(), e.getValue().substitute(binding));
		}
		return new TupleGenerator(es,attributes());
	}
	
	public Expr replace(Map<Expr,Expr> binding) {
		Expr t = binding.get(this);
		if(t != null) {
			return t;
		} else {
			HashMap<String,Expr> map = new HashMap<String,Expr>();			
			for(Map.Entry<String,Expr> v : exprs.entrySet()) {
				Expr e = v.getValue().replace(binding);
				map.put(v.getKey(), e);				
			}
			return new TupleGenerator(map,attributes());			
		}
	}
	
	public <T> List<T> match(Class<T> match) {
		List<T> matches = new ArrayList();
		for (Map.Entry<String, Expr> v : exprs.entrySet()) {
			matches.addAll(v.getValue().match(match));
		}
		if (match.isInstance(this)) {
			matches.add((T) this);
		}
		return matches;
	}
	
	public Set<Variable> uses() {
		HashSet<Variable> r = new HashSet<Variable>();
		
		for(Map.Entry<String,Expr> e : exprs.entrySet()) {
			r.addAll(e.getValue().uses());
		}
		
		return r;
	}
	
	public Expr reduce(Map<String, Type> environment) {
		HashMap vals = new HashMap();
		boolean allValues = true;
		for(Map.Entry<String,Expr> e : exprs.entrySet()) {
			Expr v = e.getValue().reduce(environment);
			allValues &= v instanceof Value;
			vals.put(e.getKey(), v);
		}
		
		if(allValues) {
			return new TupleVal(vals,attributes());
		} else {
			return new TupleGenerator(vals,attributes());
		}
	}
	
	public Pair<WExpr,WFormula> convert(Map<String, Type> environment, ModuleLoader loader) throws ResolveError {		
		WFormula constraints = WBool.TRUE;		
		ArrayList<String> fields = new ArrayList(exprs.keySet());
		Collections.sort(fields);
		
		ArrayList<WExpr> params = new ArrayList<WExpr>();
		for (String field : fields) {
			Expr e = exprs.get(field);									
			Pair<WExpr,WFormula> src = e.convert(environment, loader);
			constraints = WFormulas.and(constraints, src.second());
			params.add(src.first());
		}

		return new Pair<WExpr,WFormula>(new WTupleConstructor(
				fields, params), constraints);
	}
	
	public int hashCode() {
		return exprs.hashCode();
	}
	
	public boolean equals(Object o) {
		return (o instanceof TupleGenerator) && ((TupleGenerator)o).exprs.equals(exprs);
	}
	
	
	public String toString() {
		String r = "(";
		boolean firstTime = true;

		ArrayList<String> ss = new ArrayList<String>(exprs.keySet());
		Collections.sort(ss);

		for (String s : ss) {
			if (!firstTime) {
				r = r + ",";
			}
			firstTime = false;
			r = r + s + ":" + exprs.get(s).toString();
		}
		return r + ")";
	}
}
