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

import java.math.*;
import java.util.*;

import wyjc.ModuleLoader;
import wyjc.ast.exprs.*;
import wyjc.ast.types.*;
import wyjc.lang.Attribute;
import wyjc.lang.SyntacticElementImpl;
import wyjc.util.*;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;

/**
 * This class represents an integer or real number used in an expression.
 */
public class IntVal extends SyntacticElementImpl implements Expr,Value {
	private final BigInteger value;

	public IntVal(int number, Attribute... attributes) {
		super(attributes);
		this.value = BigInteger.valueOf(number);
	}
	
	public IntVal(BigInteger number, Attribute... attributes) {
		super(attributes);
		this.value = number;
	}	
	
	public IntVal(BigInteger number, Collection<Attribute> attributes) {
		super(attributes);
		this.value = number;
	}	
	
	
	public IntVal substitute(Map<String,Expr> binding) {
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
		List<T> matches = new ArrayList<T>();
		if(match.isInstance(this)) {
			matches.add((T)this);
		}
		return matches;
	}
		
	public Set<Variable> uses() {
		return new HashSet<Variable>();
	}	
	
	public int hashCode() {
		return value.hashCode();
	}
	
	public boolean equals(Object o) {
		return (o instanceof IntVal) && ((IntVal) o).value.equals(value);
	}
	
	/**
	 * Get the integer this object refers to
	 */
	public BigInteger value() {
		return value;
	}

	public Expr reduce(Map<String, Type> environment) {
		return this;
	}
	
	public Pair<WExpr,WFormula> convert(Map<String, Type> environment, ModuleLoader loader) throws ResolveError {
		return new Pair<WExpr,WFormula>(new WNumber(value), WBool.TRUE);
	}
	
	public Type type(Map<String,Type> environment) {
		return Types.T_INT(null);
	}
	
	public Type type() {
		return Types.T_INT(null);
	}
	
	public String toString() {		
		return value.toString();		
	}
}
