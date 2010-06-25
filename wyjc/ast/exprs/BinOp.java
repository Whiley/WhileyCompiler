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
import wyjc.ast.types.*;

public abstract class BinOp<T extends Expr> extends SyntacticElementImpl implements Expr {
	protected final T lhs;
	protected final T rhs;
	protected final Type type;
	
	public BinOp(T lhs, T rhs, Type type, Attribute... attributes) {
		super(attributes);
		this.lhs = lhs;
		this.rhs = rhs;
		this.type = type;
	}
	
	public BinOp(T lhs, T rhs, Type type, Collection<Attribute> attributes) {
		super(attributes);
		this.lhs = lhs;
		this.rhs = rhs;
		this.type = type;
	}
	
	public T lhs() {
		return lhs;
	}
	public T rhs() {
		return rhs;
	}

	public Type type(Map<String,Type> environment) {
		return type;
	}
	
	public <T> List<T> match(Class<T> match) {
		List<T> matches = lhs.match(match);
		matches.addAll(rhs.match(match));
		if(match.isInstance(this)) {
			matches.add((T)this);
		}
		return matches;
	}
	
	public Set<Variable> uses() {
		Set<Variable> r = lhs.uses();
		r.addAll(rhs.uses());
		return r;
	}
}
