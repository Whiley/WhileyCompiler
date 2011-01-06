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

package wyjx.stages;

import java.util.*;

import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.lang.Code.*;
import wyil.stages.ModuleTransform;
import static wyil.util.SyntaxError.*;

public class FailureCheck implements ModuleTransform {
	private final ModuleLoader loader;
	private String filename;

	public FailureCheck(ModuleLoader loader) {
		this.loader = loader;
	}
	
	public Module apply(Module module) {
		filename = module.filename();
		
		for(Module.Method method : module.methods()) {
			check(method);
		}
		return module;
	}
		
	public void check(Module.Method method) {		
		for (Module.Case c : method.cases()) {
			check(c.body(),method);
		}		
	}
	
	protected void check(Block block,  Module.Method method) {		
		// The reachables set contains those labels which are known to be
		// definitely reachable.
		HashSet<String> reachables = new HashSet<String>();
		int checkNesting = 0;
		boolean reachable = true;
		boolean runtimeChecks = false;
		
		for (int i = 0; i != block.size(); ++i) {
			Stmt stmt = block.get(i);
			Code code = stmt.code;

			if(code instanceof Check) {
				reachables.clear();
				checkNesting++;
				reachable = true;
			} else if(code instanceof CheckEnd) {
				checkNesting--;
			} else if (code instanceof Label) {
				Label label = (Label) code;
				reachable = reachable || reachables.contains(label.label);
			} 
				
			if (code instanceof Fail) {
				Fail f = (Fail) code;
				if(reachable) {
					syntaxError(f.msg,filename,stmt);
				} else {
					runtimeChecks = true;
				}
			} else if (checkNesting == 0 || !reachable) {
				continue; 
			} else if (code instanceof Goto) {
				Goto got = (Goto) code;
				reachables.add(got.target);
				reachable = false;
			} else if (code instanceof IfGoto) {
				IfGoto igot = (IfGoto) code;
				Attribute.BranchInfo bi = stmt.attribute(Attribute.BranchInfo.class);
				if(bi != null) {
					if(bi.trueBranch) {
						reachables.add(igot.target);
					}
					if(!bi.falseBranch) {
						reachable = false;
					}
				} else {
					reachable = false;
				}
			} else if (code instanceof Assign) {
				
			} 		
		}	
		
		if (runtimeChecks) {
			// would be nice to do better than this by actually reporting line
			// numbers, etc
			System.err.println("*** WARNING: runtime check(s) in "
					+ filename + ", " + method.name());
		}
	}
}
