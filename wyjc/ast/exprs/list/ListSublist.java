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
import wyone.theory.numeric.*;
import wyone.theory.quantifier.*;
import wyone.theory.list.*;
import static wyjc.util.SyntaxError.*;
import static wyone.theory.logic.WFormulas.*;
import static wyone.theory.numeric.WNumerics.*;


public final class ListSublist extends SyntacticElementImpl implements Expr {
	private Expr source;
	private Expr start;	
	private Expr end;
	
	public ListSublist(Expr source, Expr start, Expr end, Attribute... attributes)  {
		super(attributes);
		this.source = source;
		this.start = start;
		this.end = end;
	}
	
	public ListSublist(Expr source, Expr start, Expr end, Collection<Attribute> attributes)  {
		super(attributes);
		this.source = source;		
		this.start = start;
		this.end = end;
	}
	
	public Type type(Map<String,Type> environment) {
		Type src = source.type(environment).flattern();		
		if(src instanceof ListType) {
			return src;
		}
		syntaxError("expecting list type",source);
		return null;
	}
	
	public Expr source() { return source; }
	public Expr start() { return start; }
	public Expr end() { return end; }
		
    public Expr substitute(Map<String,Expr> binding) {
		Expr s = source.substitute(binding);
		Expr i = start.substitute(binding);
		Expr j = end.substitute(binding);
		return new ListSublist(s,i,j,attributes());
	}
    
    public Expr replace(Map<Expr,Expr> binding) {
		Expr t = binding.get(this);
		if(t != null) {
			return t;
		} else {
			Expr l = source.replace(binding);
			Expr s = start.replace(binding);
			Expr e = end.replace(binding);
			return new ListSublist(l,s,e,attributes());
		}
	}
    
    public <T> List<T> match(Class<T> match) {
		List<T> matches = source.match(match);
		matches.addAll(start.match(match));
		if(match.isInstance(this)) {
			matches.add((T)this);
		}
		return matches;
	}
	    
    public Expr reduce(Map<String, Type> environment) {
		Expr s = source.reduce(environment);
		Expr e = start.reduce(environment);
		Expr n = end.reduce(environment);
		
		if (s instanceof ListVal && e instanceof IntVal && n instanceof IntVal) {
			ListVal l = (ListVal) s;
			IntVal i = (IntVal) e;
			IntVal j = (IntVal) n;

			if (i.value().compareTo(BigInteger.ZERO) < 0
					|| i.value().compareTo(
							BigInteger.valueOf(l.getValues().size())) >= 0) {
				// no further reduction possible --- out of bounds
				return new ListSublist(s, e, n, attributes());
			} else if (j.value().compareTo(BigInteger.ZERO) < 0
					|| j.value().compareTo(
							BigInteger.valueOf(l.getValues().size())) >= 0) {
				// no further reduction possible --- out of bounds
				return new ListSublist(s, e, n, attributes());
			} else if(i.value().compareTo(j.value()) > 0) {
				// no further reduction possible --- i > j
				return new ListSublist(s, e, n, attributes());
			}
			return new ListVal(l.getValues().subList(i.value().intValue(), j.value().intValue()));			
		} else {
			// no further reduction possible.
			return new ListSublist(s, e, n, attributes());
		}
	}
    
    public Set<Variable> uses() {
		Set<Variable> r = source.uses();
		r.addAll(start.uses());
		r.addAll(end.uses());
		return r;
	}
    
    public Triple<WExpr, WFormula, WEnvironment> convert(Map<String, Type> environment, ModuleLoader loader) throws ResolveError {		
		Triple<WExpr, WFormula, WEnvironment> src = source.convert(environment, loader);
		Triple<WExpr, WFormula, WEnvironment> start = this.start.convert(environment, loader);
		Triple<WExpr, WFormula, WEnvironment> end = this.end.convert(environment, loader);
		
		WEnvironment wenv = src.third();
		wenv.addAll(start.third());
		wenv.addAll(end.third());
		WVariable retVar = WVariable.freshVar();
		wenv.add(retVar.name(), type(environment).convert());
		
		// first, identify new length					
		WFormula lenConstraints = WExprs.equals(new WLengthOf(retVar), subtract(end
				.first(), start.first()));
				
		// second, pump from src into retVar
		WVariable i = WVariable.freshVar();
		HashMap<WVariable,WExpr> variables = new HashMap();
		variables.put(i,src.first());
		WFormula lhs = WFormulas.and(WNumerics.lessThanEq(start.first(), i),
				WNumerics.lessThan(i, end.first()));
		WFormula rhs = WExprs.equals(new WListAccess(src.first(), i),
				new WListAccess(retVar, WNumerics.subtract(i, start.first())));
		WFormula forall1 = new WBoundedForall(true,variables,WFormulas.implies(lhs,rhs));
		
		// third, pump from retVar into src
		
		variables = new HashMap();
		variables.put(i,retVar);
		rhs = WExprs.equals(new WListAccess(src.first(), WNumerics.add(i,start.first())),
				new WListAccess(retVar, i));
		WFormula forall2 = new WBoundedForall(true,variables,rhs);
		
		WFormula constraints = and(start.second(), end.second(), src
				.second(), lenConstraints, forall1, forall2);
		return new Triple<WExpr, WFormula, WEnvironment>(retVar, constraints,
				wenv);
	}
    
    public int hashCode() {
		return source.hashCode() + start.hashCode() + end.hashCode();
	}
	
	public boolean equals(Object o) {
		if (o instanceof ListVal) {
			ListSublist l = (ListSublist) o;
			return start.equals(l.start) && source.equals(l.source)
					&& end.equals(l.end);
		}
		return false;
	}
    
    public String toString() {
    	return source + "[" + start + ":" + end + "]";
    }
}
