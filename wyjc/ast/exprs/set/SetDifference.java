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
import wyjc.ast.exprs.*;
import wyjc.ast.types.AnyType;
import wyjc.ast.types.SetType;
import wyjc.ast.types.Type;
import wyjc.ast.types.Types;
import wyjc.lang.Attribute;
import wyjc.util.*;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;
import static wyjc.util.SyntaxError.syntaxError;
import static wyone.theory.logic.WFormulas.*;
import static wyone.theory.numeric.WNumerics.*;

/**
 * This class represents an "sub" of two expressions.
 */
public class SetDifference extends BinOp<Expr> implements Expr {
	public SetDifference(Expr lhs, Expr rhs, Attribute... attributes) {
		super(lhs,rhs,Types.T_ANY,attributes);
	}

	public SetDifference(Expr lhs, Expr rhs, Collection<Attribute> attributes) {
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
		return new SetDifference(l,r,attributes());
	}	
	
	public Expr replace(Map<Expr,Expr> binding) {
		Expr t = binding.get(this);
		if(t != null) {
			return t;
		} else {
			Expr l = lhs.replace(binding);
			Expr r = rhs.replace(binding);
			return new SetDifference(l,r,attributes());
		}
	}
		
	public Expr reduce(Map<String, Type> environment) {
		Expr l = lhs.reduce(environment);
		Expr r = rhs.reduce(environment);
		
		if(l instanceof SetVal && r instanceof SetVal) {
			SetVal sv1 = (SetVal) l;
			SetVal sv2 = (SetVal) r;
			Set<Value> vals = sv1.getValues();
			vals.removeAll(sv2.getValues());
			return new SetVal(vals,attributes());
		} else {
			// no further reduction possible.
			return new SetDifference(l,r,attributes());
		}
	}
	
	public Pair<WExpr,WFormula> convert(Map<String, Type> environment, ModuleLoader loader) throws ResolveError {
		return null;
	}  
			
	public String toString() {
		String l = lhs.toString();
		String r = rhs.toString();
		return l + "-" + r;
	}
}
