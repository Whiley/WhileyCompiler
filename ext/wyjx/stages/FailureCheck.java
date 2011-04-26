// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyjx.stages;

import java.util.*;

import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.lang.Code.*;
import wyil.stages.ModuleTransform;
import wyjx.attributes.*;
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
				BranchInfo bi = stmt.attribute(BranchInfo.class);
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
