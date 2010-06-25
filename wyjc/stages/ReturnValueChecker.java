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

package wyjc.stages;

import static wyjc.util.SyntaxError.*;
import wyjc.ast.ResolvedWhileyFile;
import wyjc.ast.ResolvedWhileyFile.*;
import wyjc.ast.stmts.*;
import wyjc.ast.types.Types;

public class ReturnValueChecker {
	public void check(ResolvedWhileyFile wf) {
		for(Decl d : wf.declarations()) {
			if(d instanceof FunDecl) {
				check((FunDecl)d);
			}
		}
	}
	
	public void check(FunDecl fun) {
		if (fun.returnType().type() == Types.T_VOID) {
			return;
		}
		
		for(Stmt s : fun.statements()) {
			if(!check(s)) {
				return;
			}
		}
		
		// if we get here, then it means there is no return statement.
		syntaxError("missing return statement",fun);
	}
	
	/*
	 * This method returns true if it's possible for execution to follow through
	 * to the following statement.
	 */
	public boolean check(Stmt statement) {
		if (statement instanceof IfElse) {
			IfElse is = (IfElse) statement;
			boolean exit = true;
			
			for(Stmt s : is.trueBranch()) {
				if(!check(s)) {
					exit = false;
				}
			}
			
			if(!exit && is.falseBranch() != null) {				
				exit = true;
				for(Stmt s : is.falseBranch()) {
					if(!check(s)) {
						exit = false;
					}
				}	
			}
			
			return exit || is.falseBranch() == null;
		} else if(statement instanceof Return) {
			return false;
		}

		return true;
	}
}
