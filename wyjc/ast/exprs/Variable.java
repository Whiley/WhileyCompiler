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

package wyjc.ast.exprs;

import java.util.*;

import static wyjc.util.SyntaxError.*;
import wyjc.ModuleLoader;
import wyjc.ast.*;
import wyjc.ast.attrs.Attribute;
import wyjc.ast.attrs.SyntacticElementImpl;
import wyjc.ast.exprs.Expr;
import wyjc.ast.types.Type;
import wyjc.util.*;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;

/**
 * This class represents a variable used in an expression.
 */
public class Variable extends SyntacticElementImpl implements Expr, LVal {
	protected final String var;
	
	public Variable(String var, Attribute... attributes) {
		super(attributes);		
		this.var = var;
	}

	public Variable(String var, Collection<Attribute> attributes) {
		super(attributes);		
		this.var = var;
	}
	
	/**
	 * Get the variable name this object refers to
	 */
	public String name() {
		return var;
	}
	
	public Type type(Map<String,Type> environment) {
		Type t = environment.get(var);
		if(t == null) {
			syntaxError("unknown variable encountered: " + var,this);
		}
		return t;
	}
	
	public Expr substitute(Map<String,Expr> binding) {
		Expr e = binding.get(var);
		if(e != null) {
			return (Expr) e; 
		} else {
			return this;
		}
	}
	
	public Expr replace(Map<Expr,Expr> binding) {
		Expr e = binding.get(this);
		if(e != null) {
			return (Expr) e; 
		} else {
			return this;
		}
	}
	
	public <T> List<T> match(Class<T> match) {
		List<T> matches = new ArrayList();
		if(match.isInstance(this)) {
			matches.add((T)this);
		}
		return matches;
	}
	
	public Triple<wyone.core.WExpr, WFormula, WEnvironment> convert(
			Map<String, Type> environment, ModuleLoader loader)
			throws ResolveError {

		WEnvironment wenv = new WEnvironment();
		WVariable wvar = new wyone.core.WVariable(var);
		wenv.add(var, environment.get(var).convert());

		return new Triple<wyone.core.WExpr, WFormula, WEnvironment>(wvar,
				WBool.TRUE, wenv);
	}
	
	public Set<Variable> uses() {
		HashSet<Variable> r = new HashSet<Variable>();
		r.add(this);
		return r;
	}
	
	public List<Expr> flattern() {
		ArrayList<Expr> r = new ArrayList<Expr>();
		r.add(this);
		return r;
	}
	
	public Expr reduce(Map<String, Type> environment) {
		return this;
	}
	
	public String toString() { return var; }
}
