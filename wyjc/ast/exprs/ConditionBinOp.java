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

import wyjc.ModuleLoader;
import wyjc.ast.attrs.*;
import wyjc.ast.types.*;
import wyjc.util.*;
import wyjc.util.Triple;
import wyone.core.*;
import wyone.theory.logic.*;

public abstract class ConditionBinOp<T extends Expr> extends BinOp<T> implements Condition {
	
	public ConditionBinOp(T lhs, T rhs, Attribute... attributes) {
		super(lhs,rhs,Types.T_BOOL,attributes);
	}
	
	public ConditionBinOp(T lhs, T rhs, Collection<Attribute> attributes) {
		super(lhs,rhs,Types.T_BOOL,attributes);		
	}	
	
	public BoolType type(Map<String,Type> environment) {
		return Types.T_BOOL;
	}
	
	public Triple<WExpr, WFormula, WEnvironment> convert(Map<String, Type> environment,
			ModuleLoader loader) throws ResolveError {
		WVariable fv = WVariable.freshVar();
		Triple<WFormula,WFormula,WEnvironment> cn = convertCondition(environment, loader);		
		WFormula f = cn.second();
		WFormula tmp = cn.first();
		WFormula left = WFormulas.implies(new WPredicate(true,fv.name()),tmp); 
		WFormula right = WFormulas.implies(tmp,new WPredicate(true,fv.name()));
		f = WFormulas.and(f,left,right);		
		return new Triple<WExpr,WFormula,WEnvironment>(fv,f,cn.third());
	}	
}
