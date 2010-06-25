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
import wyjc.ast.types.AnyType;
import wyjc.ast.types.SetType;
import wyjc.ast.types.Type;
import wyjc.ast.types.Types;
import wyjc.util.Pair;
import wyjc.util.ResolveError;
import wyjc.util.Triple;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;
import wyone.theory.quantifier.*;
import wyone.theory.set.WSetConstructor;
import wyone.theory.set.WSets;
import static wyjc.util.SyntaxError.syntaxError;
import static wyone.theory.logic.WFormulas.*;
import static wyone.theory.numeric.WNumerics.*;


/**
 * This class represents an "mul(tiply)" of two expressions.
 */
public class SetIntersection extends BinOp<Expr> implements Expr {
	public SetIntersection(Expr lhs, Expr rhs, Attribute... attributes) {
		super(lhs,rhs,Types.T_ANY,attributes);
	}

	public SetIntersection(Expr lhs, Expr rhs, Collection<Attribute> attributes) {
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
		return new SetIntersection(l,r,attributes());
	}
	
	public Expr replace(Map<Expr,Expr> binding) {
		Expr t = binding.get(this);
		if(t != null) {
			return t;
		} else {
			Expr l = lhs.replace(binding);
			Expr r = rhs.replace(binding);
			return new SetIntersection(l,r,attributes());
		}
	}
	
	public Expr reduce(Map<String, Type> environment) {
		Expr l = lhs.reduce(environment);
		Expr r = rhs.reduce(environment);
		
		if (l instanceof SetVal && r instanceof SetVal) {
			SetVal sv1 = (SetVal) l;
			SetVal sv2 = (SetVal) r;
			HashSet<Value> nvals = new HashSet<Value>();
			Set<Value> vals = sv1.getValues();
			for (Value v : sv2.getValues()) {
				if (vals.contains(v)) {
					nvals.add(v);
				}
			}
			return new SetVal(nvals, attributes());
		} else {
			// no further reduction possible.
			return new SetIntersection(l,r,attributes());
		}
	}
	
	public Triple<WExpr, WFormula, WEnvironment> convert(Map<String, Type> environment,
			ModuleLoader loader) throws ResolveError {
		Triple<WExpr, WFormula, WEnvironment> l = lhs.convert(environment, loader);
		Triple<WExpr, WFormula, WEnvironment> r = rhs.convert(environment, loader);
		WEnvironment wenv = l.third();
		wenv.addAll(r.third());
		
		WVariable v = WVariable.freshVar();
		WVariable vs = WVariable.freshVar();
		HashMap<WVariable, WExpr> vars = new HashMap();
		vars.put(v, vs);
		wenv.add(vs.name(),type(environment).convert());
		WSetConstructor sc = new WSetConstructor(v);
		WFormula left = new WBoundedForall(true, vars, WFormulas.and(WSets
				.subsetEq(sc, l.first()), WSets.subsetEq(sc, r.first())));
		
		vars = new HashMap();
		vars.put(v, l.first());
		WFormula right = new WBoundedForall(true, vars, WFormulas.implies(WSets
				.subsetEq(sc, r.first()), WSets.subsetEq(sc, vs)));
		
		WFormula constraints = WFormulas.and(l.second(), r.second(), left,
				right, WSets.subsetEq(vs, l.first()), WSets.subsetEq(vs, r
						.first()));		
		return new Triple<WExpr, WFormula, WEnvironment>(vs,constraints,wenv);
	}
	
	public String toString() {
		String l = lhs.toString();
		String r = rhs.toString();
		return l + "\u2229" + r;
	}
}
