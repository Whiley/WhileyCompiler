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

import java.math.*;
import java.util.*;

import wyjc.ModuleLoader;
import wyjc.ast.*;
import wyjc.ast.attrs.Attribute;
import wyjc.ast.attrs.SyntacticElementImpl;
import wyjc.ast.exprs.*;
import wyjc.ast.types.*;
import wyjc.jvm.rt.BigRational;
import wyjc.util.Pair;
import wyjc.util.ResolveError;
import wyjc.util.Triple;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;
import static wyone.theory.logic.WFormulas.*;
import static wyone.theory.numeric.WNumerics.*;


/**
 * This class represents an integer or real number used in an expression.
 */
public class RealVal extends SyntacticElementImpl implements Expr,Value {
	private final BigRational value;

	public RealVal(BigRational number, Attribute... attributes) {
		super(attributes);
		this.value = number;
	}
	
	public RealVal(BigRational number, Collection<Attribute> attributes) {
		super(attributes);
		this.value = number;
	}
	
	public RealVal substitute(Map<String,Expr> binding) {
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
	
	public Set<Variable> uses() {
		return new HashSet<Variable>();
	}
			
	/**
	 * Get the integer this object refers to
	 */
	public BigRational value() {
		return value;
	}

	public Expr reduce(Map<String, Type> environment) {
		return this;
	}
	
	public int hashCode() {
		return value.hashCode();
	}
	
	public Type type(Map<String,Type> environment) {
		return Types.T_REAL;
	}
	
	public Type type() {
		return Types.T_REAL;
	}
	
	public boolean equals(Object o) {
		return (o instanceof RealVal) && ((RealVal) o).value.equals(value);
	}
	
	public Pair<WExpr,WFormula> convert(Map<String, Type> environment, ModuleLoader loader) throws ResolveError {		
		return new Pair<WExpr,WFormula>(new WNumber(value.numerator(),
				value.denominator()), WBool.TRUE);		
	}  
	
	public String toString() {		
		return value.toString();		
	}
}
