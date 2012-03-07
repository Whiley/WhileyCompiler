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

import wybs.lang.Builder;
import wybs.lang.Path;
import wybs.lang.SyntaxError;
import wyil.Transform;
import wyil.lang.*;
import wyil.lang.Code.*;
import wyil.util.Pair;
import static wybs.lang.SyntaxError.*;
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
 * declared</b>. Thus, if a method or function throws an exception an
 * appropriate throws clause is required.
 * <li><b>Every catch handler must catch something</b>. It is a syntax error if
 * a catch handler exists which can never catch anything (i.e. it is dead-code).
 * </li>
 * </ul>
 * 
 * @author David J. Pearce
 * 
 */
public class ModuleCheck implements Transform {
	private String filename;

	public ModuleCheck(Builder builder) {

	}
	
	public void apply(WyilFile module) {
		filename = module.filename();
		
		for(WyilFile.Method method : module.methods()) {
			check(method);
		}
	}
		
	public void check(WyilFile.Method method) {		
		for (WyilFile.Case c : method.cases()) {
			checkTryCatchBlocks(c, method);
			if(method.isFunction()) {
				checkFunctionPure(c);
			}
		}		
	}
	
	protected void checkTryCatchBlocks(WyilFile.Case c, WyilFile.Method m) {
		HashMap<String,Block.Entry> labelMap = new HashMap<String,Block.Entry>();
		for (Block.Entry b : c.body()) {
			if(b.code instanceof Code.Label) {
				Label l = (Code.Label) b.code;
				labelMap.put(l.label, b);
			}
		}
		Handler rootHandler = new Handler(m.type().throwsClause());
		checkTryCatchBlocks(0,c.body().size(),c,rootHandler,labelMap);
	}
	
	protected void checkTryCatchBlocks(int start, int end, WyilFile.Case c,
			Handler handler, HashMap<String,Block.Entry> labelMap) {		
		Block block = c.body();
		for (int i = start; i < end; ++i) {
			Block.Entry entry = block.get(i);
			
			try {
				Code code = entry.code;

				if (code instanceof TryCatch) {
					TryCatch sw = (TryCatch) code;
					int s = i;
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
					
					Handler nhandler = new Handler(sw.catches,handler);
					checkTryCatchBlocks(s + 1, i, c, nhandler, labelMap);
					
					// now we need to check that every handler is, in fact,
					// reachable.															
					for(Pair<Type,String> p : sw.catches) {
						if(!nhandler.active.contains(p.first())) {							
							// FIXME: better error message which focuses on the
							// actual handler is required.
							syntaxError(
									errorMessage(UNREACHABLE_CODE),
									filename, labelMap.get(p.second()));
						}
					}
				} else {
					Type ex = thrownException(code);				
					if (ex != Type.T_VOID && !handler.catchException(ex)) {						
						syntaxError(
								errorMessage(MUST_DECLARE_THROWN_EXCEPTION),
								filename, entry);
					}				
				}
			} catch(SyntaxError ex) {
				throw ex;
			} catch(Throwable ex) {
				internalFailure(ex.getMessage(),filename,entry,ex);
			}
		}
	}
	
	private Type thrownException(Code code) {
		if(code instanceof Code.Throw) {
			Code.Throw t = (Code.Throw) code;			
			return t.type;
		} else if(code instanceof Code.IndirectInvoke) {
			Code.IndirectInvoke i = (Code.IndirectInvoke) code;
			return i.type.throwsClause();
		} else if(code instanceof Code.Invoke) {
			Code.Invoke i = (Code.Invoke) code;
			return i.type.throwsClause();
		} else if(code instanceof Code.IndirectSend) {
			Code.IndirectSend i = (Code.IndirectSend) code;
			return i.type.throwsClause();
		} else if(code instanceof Code.Send) {
			Code.Send i = (Code.Send) code;
			return i.type.throwsClause();
		}
		
		return Type.T_VOID;
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

		public Handler(Type throwsClause) {
			this.handlers = new ArrayList<Type>();
			this.handlers.add(throwsClause);
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
					type = Type.intersect(type, Type.Negation(t));					
				}
			}

			if (parent != null) {
				return parent.catchException(type);
			} else {
				return false;
			}
		}
	}
	
	protected void checkFunctionPure(WyilFile.Case c) {
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
			} else if(code instanceof Code.New) {
				syntaxError(errorMessage(SPAWN_NOT_PERMITTED_IN_FUNCTION), filename, stmt);				
			} else if(code instanceof Code.Dereference){ 
				syntaxError(errorMessage(REFERENCE_ACCESS_NOT_PERMITTED_IN_FUNCTION), filename, stmt);							
			}
		}	
	}
}
