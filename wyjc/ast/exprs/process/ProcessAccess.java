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

package wyjc.ast.exprs.process;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static wyjc.util.SyntaxError.*;
import wyjc.ModuleLoader;
import wyjc.ast.exprs.*;
import wyjc.ast.types.*;
import wyjc.lang.Attribute;
import wyjc.util.*;
import wyone.core.*;
import wyone.theory.logic.*;


public class ProcessAccess extends UnOp<Expr> implements LVal {
	public ProcessAccess(Expr c, Attribute... attributes) {
		super(c,Types.T_ANY,attributes);
	}
	
	public ProcessAccess(Expr c, Collection<Attribute> attributes) {
		super(c,Types.T_ANY,attributes);
	}
	
	public Type type(Map<String,Type> environment) {
		Type t = expr.type(environment);
		if(t instanceof ProcessType) {
			return ((ProcessType)t).element();
		} 
		syntaxError("expecting process type, got: " + t,this);
		return null;
	}
	
	public Expr substitute(Map<String,Expr> binding) {
		Expr e = expr.substitute(binding);
		return new ProcessAccess(e,attributes());
	}
	
	public Expr replace(Map<Expr, Expr> binding) {
		Expr t = binding.get(this);
		if (t != null) {
			return t;
		} else {
			Expr e = expr.replace(binding);
			return new ProcessAccess(e, attributes());
		}
	}
	
	public Expr reduce(Map<String, Type> environment) {
		Expr e = expr.reduce(environment);
		return new ProcessAccess(e, attributes());		
	}
	
	public List<Expr> flattern() {
    	Expr src = expr;    	
    	List<Expr> r = ((LVal)src).flattern();
		r.add(this);
		return r;
	}
    	
	public String toString() {		
		return "*" + expr.toString() + "";					
	}

	public Pair<WExpr,WFormula> convert(Map<String, Type> environment, ModuleLoader loader) throws ResolveError {
		return expr.convert(environment, loader);
	}
}
