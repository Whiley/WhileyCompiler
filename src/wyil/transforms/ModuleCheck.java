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

package wyil.transforms;

import java.util.*;

import wyil.ModuleLoader;
import wyil.Transform;
import wyil.lang.*;
import wyil.lang.Code.*;
import static wyil.util.SyntaxError.*;

/**
 * <p>
 * Performs a number of simplistic checks that a module is syntactically
 * correct. This includes the following
 * </p>
 * <ul>
 * <li><b>Functions cannot have side-effects</b>. This includes sending messages
 * to actors, calling headless methods and spawning processes.</li>
 * <li><b>Functions/Methods cannot throw exceptions unless they are
 * declared</b>. Thus, if a method or function throws an exception an appropriate
 * throws clause is required.
 * </ul>
 * 
 * @author djp
 * 
 */
public class ModuleCheck implements Transform {
	private final ModuleLoader loader;
	private String filename;

	public ModuleCheck(ModuleLoader loader) {
		this.loader = loader;
	}
	
	public void apply(Module module) {
		filename = module.filename();
		
		for(Module.Method method : module.methods()) {
			check(method);
		}
	}
		
	public void check(Module.Method method) {
		boolean isFunction = !(method.type() instanceof Type.Method);
		for (Module.Case c : method.cases()) {
			checkThrowsClause(c);
			if(isFunction) {
				checkFunctionPure(c);
			}
		}		
	}
	
	protected void checkThrowsClause(Module.Case c) {
		// this is where we should check throws clauses
		// however, it requires me to figure out where to put the throw type.
		// Should it be part of Type.Function, for example? (yes, probably)
	}
	
	protected void checkFunctionPure(Module.Case c) {
		Block block = c.body();
		for (int i = 0; i != block.size(); ++i) {
			Block.Entry stmt = block.get(i);
			Code code = stmt.code;
			if (code instanceof Code.Send || code instanceof Code.IndirectSend) {
				// external message send
				syntaxError("cannot send message from function", filename, stmt);
			} else if(code instanceof Code.Invoke && ((Code.Invoke)code).type instanceof Type.Method) {
				// internal message send
				syntaxError("cannot call method message from function", filename, stmt);
			} else if(code instanceof Code.Spawn) {
				syntaxError("cannot spawn process from function",filename,stmt);
			} else if(code instanceof Code.ProcLoad){ 
				syntaxError("cannot access process from function",filename,stmt);				
			}
		}	
	}
}
