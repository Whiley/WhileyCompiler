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
import wyjc.ast.exprs.*;
import wyjc.ast.types.*;

/**
 * This class represents a variable declaration. All variables in whiley must be
 * declared before they are used.
 * 
 * @author djp
 */
public final class VarDecl extends SyntacticElementImpl implements Stmt {
	protected Type type;
	protected String name;	
	protected Expr initialiser;
	
	public VarDecl(Type type, String name, Expr initialiser,
			Attribute... attributes) {
		super(attributes);
		this.type = type;		
		this.name = name;
		this.initialiser = initialiser;
	}

	public VarDecl(Type type, String name,
			Expr initialiser, Collection<Attribute> attributes) {
		super(attributes);
		this.type = type;		
		this.name = name;
		this.initialiser = initialiser;
	}

	
	public Type type() { return type; }	
	public String name() { return name; }		
	
	public Expr initialiser() { return initialiser; }
		
	public String toString() {
		String cstr = "";		
		if(initialiser != null) {
			return type + " " + name + " = " + initialiser + cstr;
		} else {
			return type + " " + name + cstr;
		}
	}
}
