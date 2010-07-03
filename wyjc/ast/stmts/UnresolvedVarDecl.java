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
import wyjc.ast.exprs.Expr;
import wyjc.ast.types.*;
import wyjc.ast.types.unresolved.UnresolvedType;

/**
 * This class represents a variable declaration. All variables in whiley must be
 * declared before they are used.
 * 
 * @author djp
 */
public final class UnresolvedVarDecl extends SyntacticElementImpl implements Stmt {
	protected UnresolvedType type;
	protected String name;
	protected Expr initialiser;
	
	public UnresolvedVarDecl(UnresolvedType type, String name, Expr initialiser,
			Attribute... attributes) {
		super(attributes);
		this.type = type;
		this.name = name;
		this.initialiser = initialiser;
	}

	public UnresolvedVarDecl(UnresolvedType type, String name, Expr initialiser,
			Collection<Attribute> attributes) {
		super(attributes);
		this.type = type;
		this.name = name;
		this.initialiser = initialiser;
	}

	
	public UnresolvedType type() { return type; }
	public String name() { return name; }		
	
	public Expr initialiser() { return initialiser; }
		
	public String toString() {
		if(initialiser != null) {
			return type + " " + name + " = " + initialiser;
		} else {
			return type + " " + name;
		}
	}
}
