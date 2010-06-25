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

import wyjc.ast.attrs.Attribute;
import wyjc.ast.attrs.SyntacticElementImpl;
import wyjc.ast.exprs.Condition;

/**
 * This represents a "if c then l_1 else l_2" statement.
 */
public final class IfElse extends SyntacticElementImpl implements Stmt {
	private final Condition cond;
	private final List<Stmt> trueBranch;
	private final List<Stmt> falseBranch;

	public IfElse(Condition cond, List<Stmt> trueBranch,
			List<Stmt> falseBranch, Attribute... attributes) { 
		super(attributes);
		this.cond = cond;
		this.trueBranch = trueBranch;
		this.falseBranch = falseBranch;
	}

	public IfElse(Condition cond, List<Stmt> trueBranch,
			List<Stmt> falseBranch, Collection<Attribute> attributes) { 
		super(attributes);
		this.cond = cond;
		this.trueBranch = trueBranch;
		this.falseBranch = falseBranch;
	}
	
	/**
	 * Get the condition for this If statement
	 */
	public Condition condition() { 
		return cond; 
	}

	/**
	 * Get true label to be taken when the condition evaluates to
	 * true.
	 */
	public List<Stmt> trueBranch() { return trueBranch; }

	/**
	 * Get true label to be taken when the condition evaluates to
	 * false.
	 */
	public List<Stmt> falseBranch() { return falseBranch; }	
}
