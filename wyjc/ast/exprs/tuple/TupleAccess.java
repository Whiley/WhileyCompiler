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

import static wyjc.util.SyntaxError.syntaxError;
import wyjc.ModuleLoader;
import wyjc.ast.attrs.Attribute;
import wyjc.ast.attrs.SyntacticElementImpl;
import wyjc.ast.exprs.*;
import wyjc.ast.exprs.process.ProcessAccess;
import wyjc.ast.types.*;
import wyjc.util.Pair;
import wyjc.util.ResolveError;
import wyjc.util.Triple;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.tuple.*;


public class TupleAccess extends SyntacticElementImpl implements Expr, LVal {
	private Expr source;
	private String name;
	
	public TupleAccess(Expr source, String name, Attribute... attributes)  {
		super(attributes);
		this.source = source;
		this.name = name;
	}
	
	public TupleAccess(Expr source, String name, Collection<Attribute> attributes)  {
		super(attributes);
		this.source = source;
		this.name = name;
	}
	
	public Type type(Map<String,Type> environment) {
		Type src = source.type(environment).flattern();				
		TupleType tt = Types.effectiveTupleType(src,this);		
		src = tt.types().get(name);
		if(src != null) {
			return src;
		}
		syntaxError("expecting tuple type with field " + name + ", got: " + tt,source);
		return null;
	}
	
	public Expr replace(Map<Expr, Expr> binding) {
		Expr t = binding.get(this);
		if (t != null) {
			return t;
		} else {			
			Expr v = source.replace(binding);
			return new TupleAccess(v,name, attributes());
		}
	}

	public <T> List<T> match(Class<T> match) {
		List<T> matches = new ArrayList();
		matches.addAll(source.match(match));
		if (match.isInstance(this)) {
			matches.add((T) this);
		}
		return matches;
	}

	public Expr source() { return source; }
	public String name() { return name; }
	
    public Expr substitute(Map<String,Expr> binding) {
		Expr s = source.substitute(binding);		
		return new TupleAccess(s,name,attributes());
	}
    
    public Set<Variable> uses() {
		return source.uses();
	}    
   
    public Expr reduce(Map<String, Type> environment) {
		Expr s = source.reduce(environment);
		if(s instanceof TupleVal) {
			TupleVal tv = (TupleVal) s;
			Value v = tv.values().get(name);
			if(v != null) {
				return v; 
			}
		}
		return new TupleAccess(s,name,attributes());
	}
    
    public List<Expr> flattern() {
    	Expr src = source;    	
    	List<Expr> r = ((LVal)src).flattern();
		r.add(this);
		return r;
	}
    
	public Triple<WExpr, WFormula, WEnvironment> convert(Map<String, Type> environment, ModuleLoader loader) throws ResolveError {
		Triple<WExpr, WFormula, WEnvironment> src = source.convert(environment, loader);			
		WExpr access = new WTupleAccess(src.first(),name);
		return new Triple<WExpr, WFormula, WEnvironment>(access,src.second(),src.third());
	}
    
    public String toString() {
    	if(source instanceof ProcessAccess) {
    		ProcessAccess pa = (ProcessAccess) source;
    		return pa.mhs() + "->" + name;
    	} else {
    		return source + "." + name;
    	}
    }
    
    public boolean equals(Object o) {
    	if(o instanceof TupleAccess) {
    		TupleAccess t = (TupleAccess) o;
    		return name.equals(t.name) && source.equals(t.source);
    	}
    	return false;
    }
    
    public int hashCode() {
    	return source.hashCode();
    }

}
