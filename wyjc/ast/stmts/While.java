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

package wyjc.ast.stmts;

import java.util.*;

import wyjc.ast.attrs.*;
import wyjc.ast.exprs.*;

public final class While extends SyntacticElementImpl implements Stmt {
	private Condition condition;
	private Condition invariant;
	private ArrayList<Stmt> statements;
	
	public While(Condition condition, Condition invariant, List<Stmt> body,
			Attribute... attributes) {
		super(attributes);
		this.condition = condition;
		this.invariant = invariant;
		this.statements = new ArrayList<Stmt>(body);
	}
	
	public While(Condition condition, Condition invariant, List<Stmt> body,
			Collection<Attribute> attributes) {
		super(attributes);
		this.condition = condition;
		this.invariant = invariant;
		this.statements = new ArrayList<Stmt>(body);
	}
	
	public List<Stmt> body() {
		return statements;
	}
	
	public Condition condition() {
		return condition;
	}
	
	public Condition invariant() {
		return invariant;
	}
}
