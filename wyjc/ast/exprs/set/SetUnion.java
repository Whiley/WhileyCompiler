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

package wyjc.ast.exprs.set;

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
import wyone.theory.quantifier.*;
import wyone.theory.set.*;
import wyone.theory.type.WTypes;
import static wyjc.util.SyntaxError.*;
import static wyone.theory.logic.WFormulas.*;
import static wyone.theory.numeric.WNumerics.*;


/**
 * This class represents an "add" of two expressions.
 */
public class SetUnion extends BinOp<Expr> implements Expr {
		public SetUnion(Expr lhs, Expr rhs, Attribute... attributes) {
		super(lhs,rhs,Types.T_ANY,attributes);
	}

	public SetUnion(Expr lhs, Expr rhs, Collection<Attribute> attributes) {
		super(lhs,rhs,Types.T_ANY,attributes);
	}

	public Type type(Map<String,Type> environment) {
		Type t = Types.leastUpperBound(lhs.type(environment),rhs.type(environment));
		if(!(t instanceof SetType)) {
			syntaxError("expecting set type, got: " + t,this);
		}
		return t;
	}
	
	public Expr substitute(Map<String,Expr> binding) {
		Expr l = lhs.substitute(binding);
		Expr r = rhs.substitute(binding);
		return new SetUnion(l,r,attributes());
	}
	
	public Expr replace(Map<Expr,Expr> binding) {
		Expr t = binding.get(this);
		if(t != null) {
			return t;
		} else {
			Expr l = lhs.replace(binding);
			Expr r = rhs.replace(binding);
			return new SetUnion(l,r,attributes());
		}
	}
	
	public Expr reduce(Map<String, Type> environment) {
		Expr l = lhs.reduce(environment);
		Expr r = rhs.reduce(environment);
		
		if (l instanceof SetVal && r instanceof SetVal) {
			SetVal sv1 = (SetVal) l;
			SetVal sv2 = (SetVal) r;			
			Set<Value> vals = sv1.getValues();
			vals.addAll(sv2.getValues());
			return new SetVal(vals, attributes());
		} else {
			// no further reduction possible.
			return new SetUnion(l,r,attributes());
		}
	}
	
	public Pair<WExpr,WFormula> convert(Map<String, Type> environment,
			ModuleLoader loader) throws ResolveError {
		Pair<WExpr,WFormula> l = lhs.convert(environment, loader);
		Pair<WExpr,WFormula> r = rhs.convert(environment, loader);
		
		WVariable v = WVariable.freshVar();
		WVariable vs = WVariable.freshVar();
		WFormula constraints = WTypes.subtypeOf(vs, type(environment).convert());
		HashMap<WVariable, WExpr> vars = new HashMap();
		vars.put(v, vs);
		WSetConstructor sc = new WSetConstructor(v);
		WFormula allc = WFormulas.or(WSets.subsetEq(sc, l.first()), WSets
				.subsetEq(sc, r.first()));
		constraints = WFormulas.and(constraints, l.second(), r.second(), WSets
				.subsetEq(l.first(), vs), WSets.subsetEq(r.first(), vs),
				new WBoundedForall(true, vars, allc));

		return new Pair<WExpr,WFormula>(vs,constraints);
	}
	
	public String toString() {
		String l = lhs.toString();
		String r = rhs.toString();
		return l + "\u222A" + r;
	}
}
