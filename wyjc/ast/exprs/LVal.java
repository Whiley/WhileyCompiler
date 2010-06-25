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

public interface LVal extends Expr {
	
	/**
	 * <p>
	 * This method flatterns an lval into a sequence of access expressions,
	 * where the first expression is always a variable. Essentially, the list
	 * returned identifies the "location" being updated by an assignment.
	 * </p>
	 * <p>
	 * For example
	 * </p>
	 * 
	 * <pre>
	 * a[0].r = 1
	 * </pre>
	 * 
	 * <p>This flatterns into the list: a;[0];.r. Here, the first expression is
	 * variable a, which represents the local variable whose value is being
	 * changed by the assignment.</p>
	 * 
	 * @return
	 */
	public List<Expr> flattern();
}
