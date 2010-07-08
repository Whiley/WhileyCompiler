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

package wyjc.ast.exprs.integer;

import java.util.*;

import wyjc.ModuleLoader;
import wyjc.ast.attrs.Attribute;
import wyjc.ast.exprs.*;
import wyjc.ast.types.*;
import wyjc.util.*;
import wyone.core.*;
import wyone.theory.logic.*;
import static wyone.theory.numeric.WNumerics.*;

/**
 * This class represents an "add" of two expressions.
 */
public class IntAdd extends BinOp<Expr> implements Expr {

	public IntAdd(Expr lhs, Expr rhs, Attribute... attributes) {
		super(lhs,rhs,Types.T_INT,attributes);		
	}

	public IntAdd(Expr lhs, Expr rhs, Collection<Attribute> attributes) {
		super(lhs,rhs,Types.T_INT,attributes);		
	}
	
	public Expr substitute(Map<String,Expr> binding) {
		Expr l = lhs.substitute(binding);
		Expr r = rhs.substitute(binding);
		return new IntAdd(l,r,attributes());
	}
	
	public Expr replace(Map<Expr,Expr> binding) {
		Expr t = binding.get(this);
		if(t != null) {
			return t;
		} else {
			Expr l = lhs.replace(binding);
			Expr r = rhs.replace(binding);
			return new IntAdd(l,r,attributes());
		}
	}
	
	public String toString() {
		String l = lhs.toString();
		String r = rhs.toString();
		return l + "+" + r;
	}
	
	public Expr reduce(Map<String, Type> environment) {
		Expr l = lhs.reduce(environment);
		Expr r = rhs.reduce(environment);

		if (l instanceof IntVal && r instanceof IntVal) {
			IntVal i1 = (IntVal) l;
			IntVal i2 = (IntVal) r;
			return new IntVal(i1.value().add(i2.value()), attributes());
		} else {
			// no further reduction possible.
			return new IntAdd(l, r, attributes());
		}
	}
	
	public Pair<WExpr,WFormula> convert(Map<String, Type> environment, ModuleLoader loader) throws ResolveError {
		Pair<WExpr,WFormula> l = lhs.convert(environment, loader);
		Pair<WExpr,WFormula> r = rhs.convert(environment, loader);
		WFormula constraints = WFormulas.and(l.second(),r.second());
		return new Pair<WExpr,WFormula>(add(l.first(),r.first()), constraints);
	}
}
