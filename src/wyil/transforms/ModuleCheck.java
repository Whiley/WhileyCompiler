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
import wyil.util.Pair;
import static wyil.util.SyntaxError.*;
import static wyil.util.ErrorMessages.*;

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
			checkTryCatchBlocks(c, method);
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
	
	protected void checkTryCatchBlocks(Module.Case c, Module.Method m) {
		// TODO: add list of declared thrown exceptions
		Handler rootHandler = new Handler(Collections.EMPTY_LIST);
		checkTryCatchBlocks(0,c.body().size(),c,rootHandler);
	}
	
	protected void checkTryCatchBlocks(int start, int end, Module.Case c,
			Handler handler) {
		Block block = c.body();
		for (int i = start; i != end; ++i) {
			Block.Entry entry = block.get(i);
			Code code = entry.code;

			if (code instanceof TryCatch) {
				TryCatch sw = (TryCatch) code;
				int s = start;
				// Note, I could make this more efficient!					
				while (++i < block.size()) {
					entry = block.get(i);
					if (entry.code instanceof Code.Label) {
						Code.Label l = (Code.Label) entry.code;
						if (l.label.equals(sw.target)) {
							// end of loop body found
							break;
						}
					}						
				}
//				checkTryCatchBlocks(s + 1, i, c, new Handler(sw.catches,
//						handler));
			} else {
				for (Type t : thrownExceptions(code)) {
					if (!handler.catchException(t)) {
						syntaxError(
								errorMessage(MUST_DECLARE_THROWN_EXCEPTION),
								filename, entry);
					}
				}
			}
		}
	}
	
	private List<Type> thrownExceptions(Code code) {
		if(code instanceof Code.Throw) {
			Code.Throw t = (Code.Throw) code;
			ArrayList<Type> exceptions = new ArrayList<Type>();
			exceptions.add(t.type);
			return exceptions;
		} else if(code instanceof Code.IndirectInvoke) {
			Code.IndirectInvoke i = (Code.IndirectInvoke) code;
			// TODO
		} else if(code instanceof Code.Invoke) {
			Code.Invoke i = (Code.Invoke) code;
			// TODO
		} else if(code instanceof Code.IndirectSend) {
			Code.IndirectSend i = (Code.IndirectSend) code;
			// TODO
		} else if(code instanceof Code.Send) {
			Code.Send i = (Code.Send) code;
			// TODO
		}
		
		return Collections.EMPTY_LIST;
	}
	
	private static class Handler {
		public final ArrayList<Type> handlers;
		public final HashSet<Type> active;
		public final Handler parent;

		public Handler(List<Pair<Type, String>> handlers, Handler parent) {
			this.handlers = new ArrayList<Type>();
			for(Pair<Type,String> handler : handlers) {
				this.handlers.add(handler.first());
			}
			this.parent = parent;
			this.active = new HashSet<Type>();
		}

		public Handler(Collection<Type> handlers) {
			this.handlers = new ArrayList<Type>(handlers);			
			this.parent = null;
			this.active = new HashSet<Type>();
		}
		
		public boolean catchException(Type type) {
			for (Type t : handlers) {
				if (Type.isSubtype(t, type)) {
					active.add(t);
					return true;
				} else if (Type.isSubtype(type, t)) {
					active.add(t);
					// this exception may escape
				}
			}

			if (parent != null) {
				return parent.catchException(type);
			} else {
				return false;
			}
		}
	}
	
	protected void checkFunctionPure(Module.Case c) {
		Block block = c.body();
		for (int i = 0; i != block.size(); ++i) {
			Block.Entry stmt = block.get(i);
			Code code = stmt.code;
			if (code instanceof Code.Send || code instanceof Code.IndirectSend) {
				// external message send
				syntaxError(errorMessage(SEND_NOT_PERMITTED_IN_FUNCTION), filename, stmt);
			} else if(code instanceof Code.Invoke && ((Code.Invoke)code).type instanceof Type.Method) {
				// internal message send
				syntaxError(errorMessage(METHODCALL_NOT_PERMITTED_IN_FUNCTION), filename, stmt);				
			} else if(code instanceof Code.Spawn) {
				syntaxError(errorMessage(SPAWN_NOT_PERMITTED_IN_FUNCTION), filename, stmt);				
			} else if(code instanceof Code.ProcLoad){ 
				syntaxError(errorMessage(PROCESS_ACCESS_NOT_PERMITTED_IN_FUNCTION), filename, stmt);							
			}
		}	
	}
}
