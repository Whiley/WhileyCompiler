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

import wyjc.ast.attrs.*;
import wyjc.ast.types.Type;

public abstract class UnOp<T extends Expr> extends SyntacticElementImpl implements Expr {
	protected final T expr;
	protected final Type type;
	
	public UnOp(T expr, Type type, Attribute... attributes) {
		super(attributes);
		this.expr = expr;
		this.type = type;
	}
	
	public UnOp(T expr, Type type, Collection<Attribute> attributes) {
		super(attributes);
		this.expr = expr;
		this.type = type;
	}
	
	public T mhs() {
		return expr;
	}
	
	public Type type(Map<String,Type> environment) {
		return type;
	}
	
	public <S> List<S> match(Class<S> match) {
		List<S> matches = expr.match(match);
		if(match.isInstance(this)) {	
			matches.add((S)this);
		}
		return matches;
	}
	
	public Set<Variable> uses() {
		return expr.uses();		
	}
	
	public boolean equals(Object o) {
		if(o != null && o instanceof UnOp && o.getClass().equals(getClass())) {
			UnOp bop = (UnOp) o;
			return expr.equals(bop.expr); 
		} 
		return false;		
	}
	
	public int hashCode() {
		return expr.hashCode();
	}
}
