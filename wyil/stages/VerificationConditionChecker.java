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

package wyil.stages;

import wyjc.ast.ResolvedWhileyFile;
import wyjc.ast.ResolvedWhileyFile.*;
import wyjc.ast.attrs.*;
import wyjc.ast.stmts.*;
import wyjc.lang.SourceAttr;
import wyjc.lang.SyntacticElement;
import wyjc.util.*;
import wyone.core.*;

public class VerificationConditionChecker {
	private int timeout = 1;
		
	public VerificationConditionChecker() {	}
	
	public VerificationConditionChecker(int timeout) {
		this.timeout = timeout;
	}
	
	public void verify(ResolvedWhileyFile wf) {		
		for(Decl d : wf.declarations()) {
			if(d instanceof FunDecl) {
				verify((FunDecl)d);
			}
		}
	}
	
	/**
	 * The purpose of this method is to check that the method's postcondition,
	 * and that of any statements it contains, is implied by the precondition.
	 * 
	 * @param f
	 *             Method in question.
	 * @return
	 */
	public void verify(FunDecl f) {
		for(Stmt s : f.statements()) {			
			verify(s);									
		}				
	}
	
	public void verify(Stmt s) {
		if (s instanceof Print || s instanceof Assign || s instanceof Return) {
			return; // do nothing
		} else if(s instanceof Check) {			
			Check c = (Check) s;			
			VerificationConditionAttr attr = s.attribute(VerificationConditionAttr.class);
			if(attr == null) {				
				syntaxError("internal failure --- missing verification condition",s);
			}
								
			Proof p = Solver.checkUnsatisfiable(timeout, attr.condition(),
					wyone.Main.heuristic, wyone.Main.theories); 						
			// FIXME: need to update this			
			if (p instanceof Proof.Sat || p instanceof Proof.Unknown) {
				syntaxError(c.message(), s);
			} else if(p instanceof Proof.Unknown) {
				warning(c.message(),s);
			} 
		} else if (s instanceof IfElse) {
			IfElse ife = (IfElse) s;
			for (Stmt ts : ife.trueBranch()) {
				verify(ts);
			}
			if (ife.falseBranch() != null) {
				for (Stmt fs : ife.falseBranch()) {
					verify(fs);
				}
			}
		}
	}
	
	private void warning(String msg, SyntacticElement elem) {
		SourceAttr attr = (SourceAttr) elem.attribute(SourceAttr.class);
		if(attr != null) {
			System.err.println("*** WARNING: runtime check required (" + attr.filename() + ":?)");		
		} else {
			System.err.println("*** WARNING: runtime check required");
		}
	}
	
	private static void syntaxError(String msg, SyntacticElement elem) {
		int start = -1;
		int end = -1;
		String filename = "unknown";
		
		SourceAttr attr = (SourceAttr) elem.attribute(SourceAttr.class);
		if(attr != null) {
			start=attr.start();
			end=attr.end();
			filename = attr.filename();
		}
		
		throw new VerificationError(msg, filename, start, end);
	}
}
