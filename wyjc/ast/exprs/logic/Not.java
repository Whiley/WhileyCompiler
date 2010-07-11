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

package wyjc.ast.exprs.logic;

import java.util.*;

import wyjc.ModuleLoader;
import wyjc.ast.attrs.Attribute;
import wyjc.ast.exprs.*;
import wyjc.ast.types.*;
import wyjc.util.Pair;
import wyjc.util.ResolveError;
import wyjc.util.Triple;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;
import static wyone.theory.logic.WFormulas.*;
import static wyone.theory.numeric.WNumerics.*;


public class Not extends UnOp<Condition> implements Condition {
	public Not(Condition c, Attribute... attributes) {
		super(c,Types.T_BOOL(null),attributes);		
	}

	public Not(Condition c, Collection<Attribute> attributes) {
		super(c,Types.T_BOOL(null),attributes);
	}
	
	public BoolType type(Map<String,Type> env) {
		return Types.T_BOOL(null);
	}
	
	public Condition substitute(Map<String,Expr> binding) {
		Condition c = expr.substitute(binding);
		return new Not(c,attributes());
	}
	
	public Expr replace(Map<Expr, Expr> binding) {
		Expr t = binding.get(this);
		if (t != null) {
			return t;
		} else {
			Condition l = (Condition) expr.replace(binding);
			return new Not(l, attributes());
		}
	}
	
	public Condition reduce(Map<String, Type> environment) {
		Condition r = expr.reduce(environment);
		
		if (r instanceof BoolVal) {
			BoolVal b = (BoolVal) r;
			return new BoolVal(!b.value(),attributes());
		} else {
			// no further reduction possible.
			return new Not(r, attributes());
		}
	}
	
	public String toString() {
		if(expr instanceof BinOp) {
			return "!(" + expr.toString() + ")";
		} else {
			return "!" + expr.toString();
		}
	}
	
	public Pair<WExpr,WFormula> convert(Map<String, Type> environment, ModuleLoader loader) throws ResolveError {
		return null; // FIXME
	}

	public Pair<WFormula, WFormula> convertCondition(Map<String, Type> environment, ModuleLoader loader) throws ResolveError {
		Pair<WFormula,WFormula> r = expr.convertCondition(environment, loader);
		return new Pair<WFormula,WFormula>(r.first().not(),r.second());
	}  
}
