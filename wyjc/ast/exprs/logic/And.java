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
import wyjc.ast.exprs.integer.IntEquals;
import wyjc.ast.types.*;
import wyjc.util.Pair;
import wyjc.util.ResolveError;
import wyjc.util.Triple;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;
import static wyone.theory.logic.WFormulas.*;
import static wyone.theory.numeric.WNumerics.*;


public class And extends ConditionBinOp<Condition> implements Condition {
	public And(Condition lhs, Condition rhs, Attribute... attributes) {
		super(lhs, rhs, attributes);
	}

	public And(Condition lhs, Condition rhs, Collection<Attribute> attributes) {
		super(lhs, rhs, attributes);
	}

	public Condition substitute(Map<String, Expr> binding) {
		Condition l = lhs.substitute(binding);
		Condition r = rhs.substitute(binding);
		return new And(l, r, attributes());
	}
	
	public Expr replace(Map<Expr,Expr> binding) {
		Expr t = binding.get(this);
		if(t != null) {
			return t;
		} else {
			Condition l = (Condition) lhs.replace(binding);
			Condition r = (Condition) rhs.replace(binding);
			return new And(l,r,attributes());
		}
	}
	
	public Condition reduce(Map<String, Type> environment) {
		Condition l = lhs.reduce(environment);
		Condition r = rhs.reduce(environment);

		if (l instanceof BoolVal && r instanceof BoolVal) {
			BoolVal i1 = (BoolVal) l;
			BoolVal i2 = (BoolVal) r;
			return new BoolVal(i1.value() && i2.value(), attributes());
		} else if (l instanceof BoolVal) {
			BoolVal i = (BoolVal) l;
			if(i.value()) {
				return r;
			} else {
				return i;
			}
		} else if (r instanceof BoolVal) {
			BoolVal i = (BoolVal) r;
			if(i.value()) {
				return l;
			} else {
				return i;
			}
		} else {
			// no further reduction possible.
			return new And(l, r, attributes());
		}
	}
	
	public Triple<WFormula, WFormula, WEnvironment> convertCondition(Map<String, Type> environment, ModuleLoader loader) throws ResolveError {
		Triple<WFormula, WFormula, WEnvironment> l = lhs.convertCondition(environment, loader);
		Triple<WFormula, WFormula, WEnvironment> r = rhs.convertCondition(environment, loader);
		WEnvironment wenv = l.third();
		wenv.putAll(r.third());
		return new Triple<WFormula, WFormula, WEnvironment>(WFormulas.and(l
				.first(), r.first()), WFormulas.and(l.second(), r.second()),
				wenv);			
	}    
	 
	public String toString() {
		return "(" + lhs + ") && (" + rhs + ")";
	}
}
