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
import wyjc.ast.attrs.SyntacticElementImpl;
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


public class BoolVal extends SyntacticElementImpl implements Condition, Value {
	private final boolean value;

	public BoolVal(boolean value, Attribute... attributes) {
		super(attributes);
		this.value = value;
	}
	
	public BoolVal(boolean value, Collection<Attribute> attributes) {
		super(attributes);
		this.value = value;
	}
	
	public BoolType type(Map<String,Type> environment) {
		return Types.T_BOOL(null);
	}
	
	public BoolType type() {
		return Types.T_BOOL(null);
	}
	
	public BoolVal substitute(Map<String, Expr> binding) {
		return this;
	}
	
	public Expr replace(Map<Expr,Expr> binding) {
		Expr t = binding.get(this);
		if(t != null) {
			return t;
		} else {
			return this;
		}
	}
	
	public <T> List<T> match(Class<T> match) {
		List<T> matches = new ArrayList();
		if(match.isInstance(this)) {
			matches.add((T)this);
		}
		return matches;
	}
	
	public Condition typeBinding(Map<String, Type> environment) {
		return this;
	}
	
	/**
	 * Get the integer this object refers to
	 */
	public boolean value() {
		return value;
	}

	public Set<Variable> uses() {
		return new HashSet<Variable>();
	}
	
	public int hashCode() {
		return value ? 0 : 1;
	}
	
	public boolean equals(Object o) {
		return (o instanceof BoolVal) && ((BoolVal) o).value == value;
	}
	
	public Condition reduce(Map<String, Type> environment) {
		return this;
	}
	
	public String toString() {		
		if(value) {
			return "true";
		} else {
			return "false";
		}
	}
	
	public Pair<WExpr,WFormula> convert(Map<String, Type> environment, ModuleLoader loader) throws ResolveError {
		if(value) {
			return new Pair(WBool.TRUE,WBool.TRUE);
		} else {
			return new Pair(WBool.FALSE,WBool.TRUE);
		}		
	}
	
	public Pair<WFormula, WFormula> convertCondition(Map<String, Type> environment, ModuleLoader loader) throws ResolveError {
		if(value) {
			return new Pair(WBool.TRUE,WBool.TRUE);
		} else {
			return new Pair(WBool.FALSE,WBool.TRUE);
		}
	}    	
}