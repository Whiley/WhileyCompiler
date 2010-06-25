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

/**
 * This represents the "skip" statement, which is a "no-operation" or "nop" for
 * short. This statement doesn't actually do anything!
 */
public class Skip extends SyntacticElementImpl implements Stmt {
	public Skip(Attribute... attributes) {
		super(attributes);
	}	
	public Skip(Collection<Attribute> attributes) {
		super(attributes);
	}	
}
