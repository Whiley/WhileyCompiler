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
import wyjc.ast.attrs.Attribute;
import wyjc.ast.types.*;
import wyjc.util.ModuleID;
import wyjc.util.Pair;
import wyjc.util.ResolveError;
import wyjc.util.Triple;
import wyone.core.*;
import wyone.theory.logic.*;

/**
 * This class represents a variable used in an expression.
 */
public class Constant extends Variable {	
	protected final ModuleID mid;

	public Constant(String var, ModuleID mid, Attribute... attributes) {
		super(var,attributes);		
		this.mid = mid;
	}

	public Constant(String var, ModuleID mid, Collection<Attribute> attributes) {
		super(var,attributes);		
		this.mid = mid;
	}
	
	public ModuleID module() {
		return mid;
	}
	
	public Type type(Map<String,Type> environment) {
		// FIXME: well, could do better but this should be OK!
		return Types.T_ANY;
	}
	
	public Expr substitute(Map<String,Expr> binding) {
		Expr e = binding.get(var);
		if(e != null) {
			return (Expr) e; 
		} else {
			return this;
		}
	}
	
	public Triple<WExpr, WFormula, WEnvironment> convert(
			Map<String, Type> environment, ModuleLoader loader)
			throws ResolveError {
		return new Triple<WExpr, WFormula, WEnvironment>(
				new wyone.core.WVariable(var), WBool.TRUE, new WEnvironment());
	}
	
	public List<Expr> flattern() {
		ArrayList<Expr> r = new ArrayList<Expr>();
		r.add(this);
		return r;
	}
	
	public Expr reduce(Map<String, Type> environment) {
		return this;
	}
	
	public String toString() { return var; }
}
