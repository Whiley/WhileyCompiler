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

package wyjc.ast.exprs.real;

import java.util.*;

import wyjc.ModuleLoader;
import wyjc.ast.*;
import wyjc.ast.attrs.Attribute;
import wyjc.ast.attrs.SyntacticElementImpl;
import wyjc.ast.exprs.*;
import wyjc.ast.exprs.logic.BoolVal;
import wyjc.ast.types.*;
import wyjc.util.Pair;
import wyjc.util.ResolveError;
import wyjc.util.Triple;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;
import static wyone.theory.logic.WFormulas.*;
import static wyone.theory.numeric.WNumerics.*;



/**
 * This class represents an "!=" comparison of two expressions.
 */
public class RealNotEquals extends ConditionBinOp<Expr> implements Condition {
	public RealNotEquals(Expr lhs, Expr rhs, Attribute... attributes) {
		super(lhs,rhs,attributes);
	}

	public RealNotEquals(Expr lhs, Expr rhs, Collection<Attribute> attributes) {
		super(lhs,rhs,attributes);
	}
	
	public Condition substitute(Map<String,Expr> binding) {
		Expr l = lhs.substitute(binding);
		Expr r = rhs.substitute(binding);
		return new RealNotEquals(l,r,attributes());
	}
	
	public Expr replace(Map<Expr,Expr> binding) {
		Expr t = binding.get(this);
		if(t != null) {
			return t;
		} else {
			Expr l = lhs.replace(binding);
			Expr r = rhs.replace(binding);
			return new RealNotEquals(l,r,attributes());
		}
	}
	
	public Condition reduce(Map<String, Type> environment) {
		Expr l = lhs.reduce(environment);
		Expr r = rhs.reduce(environment);

		if (l instanceof RealVal && r instanceof RealVal) {
			RealVal i1 = (RealVal) l;
			RealVal i2 = (RealVal) r;
			return new BoolVal(!i1.value().equals(i2.value()), attributes());
		} else {
			// no further reduction possible.
			return new RealNotEquals(l, r, attributes());
		}
	}
	
	public Triple<WFormula, WFormula, WEnvironment> convertCondition(
			Map<String, Type> environment, ModuleLoader loader)
			throws ResolveError {
		Triple<WExpr, WFormula, WEnvironment> l = lhs.convert(environment, loader);
		Triple<WExpr, WFormula, WEnvironment> r = rhs.convert(environment, loader);
		WEnvironment wenv = l.third();
		wenv.addAll(r.third());
		return new Triple<WFormula, WFormula, WEnvironment>(WExprs.notEquals(l
				.first(), r.first()), WFormulas.and(l.second(), r.second()),
				wenv);		
	}
	
	public String toString() {
		String l = lhs.toString();
		String r = rhs.toString();
		return l + "!=" + r;
	}
}
