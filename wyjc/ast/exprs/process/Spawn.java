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

import java.util.*;

import wyjc.ModuleLoader;
import wyjc.ast.attrs.*;
import wyjc.ast.exprs.*;
import wyjc.ast.stmts.Stmt;
import wyjc.ast.types.*;
import wyjc.util.Pair;
import wyjc.util.ResolveError;
import wyjc.util.Triple;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;
import static wyone.theory.logic.WFormulas.*;
import static wyone.theory.numeric.WNumerics.*;

public class Spawn extends UnOp<Expr> implements Stmt, Expr {	
	public Spawn(Expr state, Attribute... attributes) {
		super(state, Types.T_ANY,attributes);		
	}
	
	public Spawn(Expr state, Collection<Attribute> attributes) {
		super(state, Types.T_ANY,attributes);		
	}
	
	public Type type(Map<String,Type> environment) {
		return new ProcessType(expr.type(environment));
	}
	
	public Expr substitute(Map<String,Expr> binding) {
		Expr e = expr.substitute(binding);		
		return new Spawn(e,attributes());
	}
	
	public Expr replace(Map<Expr,Expr> binding) {
		Expr t = binding.get(this);
		if(t != null) {
			return t;
		} else {
			Expr e = expr.replace(binding);				
			return new Spawn(e,attributes());
		}
	}

	public Expr reduce(Map<String, Type> environment) {
		Expr e = expr.reduce(environment);
		return new Spawn(e,attributes());		
	}
	
	public Pair<WExpr,WFormula> convert(Map<String, Type> environment, ModuleLoader loader) throws ResolveError {
		// FIXME: unsure about this!
		return expr.convert(environment, loader);
	}
	
	public String toString() {		
		return "spawn " + expr;		
	}	
}
