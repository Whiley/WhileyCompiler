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
import java.math.*;

import wyjc.ModuleLoader;
import wyjc.ast.attrs.Attribute;
import wyjc.ast.attrs.SyntacticElementImpl;
import wyjc.ast.exprs.*;
import wyjc.ast.exprs.integer.IntVal;
import wyjc.ast.types.ListType;
import wyjc.ast.types.Type;
import wyjc.util.Pair;
import wyjc.util.ResolveError;
import wyjc.util.Triple;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.list.*;
import static wyjc.util.SyntaxError.*;

public final class ListAccess extends SyntacticElementImpl implements Expr, LVal {
	private Expr source;
	private Expr index;	
	
	public ListAccess(Expr source, Expr index, Attribute... attributes)  {
		super(attributes);
		this.source = source;
		this.index = index;
	}
	
	public ListAccess(Expr source, Expr index, Collection<Attribute> attributes)  {
		super(attributes);
		this.source = source;
		this.index = index;
	}
	
	public Type type(Map<String,Type> environment) {
		Type src = source.type(environment).flattern();		
		if(src instanceof ListType) {
			return ((ListType)src).element();
		}
		syntaxError("expecting list type",source);
		return null;
	}
	
	public Expr source() { return source; }
	public Expr index() { return index; }
	
	public List<Expr> flattern() {
		// Annoying that we need this cast. Perhaps source could only be an LVal
		// ?
    	List<Expr> r = ((LVal)source).flattern();
		r.add(this);
		return r;
	}
	    
    public Expr substitute(Map<String,Expr> binding) {
		Expr s = source.substitute(binding);
		Expr i = index.substitute(binding);
		return new ListAccess(s,i,attributes());
	}
    
    public Expr replace(Map<Expr,Expr> binding) {
		Expr t = binding.get(this);
		if(t != null) {
			return t;
		} else {
			Expr l = source.replace(binding);
			Expr r = index.replace(binding);
			return new ListAccess(l,r,attributes());
		}
	}
    
    public <T> List<T> match(Class<T> match) {
		List<T> matches = source.match(match);
		matches.addAll(index.match(match));
		if(match.isInstance(this)) {
			matches.add((T)this);
		}
		return matches;
	}
	    
    public Expr reduce(Map<String, Type> environment) {
		Expr s = source.reduce(environment);
		Expr e = index.reduce(environment);

		if (s instanceof ListVal && e instanceof IntVal) {
			ListVal l = (ListVal) s;
			IntVal i = (IntVal) e;

			if (i.value().compareTo(BigInteger.ZERO) < 0
					|| i.value().compareTo(
							BigInteger.valueOf(l.getValues().size())) >= 0) {
				// no further reduction possible --- out of bounds
				return new ListAccess(s, e, attributes());
			}

			return l.getValues().get(i.value().intValue());
		} else {
			// no further reduction possible.
			return new ListAccess(s, e, attributes());
		}
	}
    
    public Set<Variable> uses() {
		Set<Variable> r = source.uses();
		r.addAll(index.uses());
		return r;
	}

	public Triple<WExpr, WFormula, WEnvironment> convert(Map<String, Type> environment,
			ModuleLoader loader) throws ResolveError {
		Triple<WExpr, WFormula, WEnvironment> idx = index.convert(environment, loader);
		Triple<WExpr, WFormula, WEnvironment> src = source.convert(environment, loader);
		WEnvironment wenv = idx.third();
		wenv.addAll(src.third());
		WFormula constraints = WFormulas.and(idx.second(), src.second());
		return new Triple<WExpr, WFormula, WEnvironment>(new WListAccess(src.first(), idx
				.first()), constraints, wenv);
	}

	public boolean equals(Object o) {
		if(o instanceof ListAccess) {
			ListAccess la = (ListAccess) o;
			return source.equals(la.source) && index.equals(la.index);
		}
		return false;
	}
	
	public int hashCode() {
		return index.hashCode() + source.hashCode();
	}
	
    public String toString() {
    	return source + "[" + index + "]";
    }
}
