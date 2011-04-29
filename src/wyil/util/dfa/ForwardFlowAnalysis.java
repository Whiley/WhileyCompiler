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

package wyil.util.dfa;

import static wyil.util.SyntaxError.syntaxError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.transforms.Transform;
import wyil.util.*;

public abstract class ForwardFlowAnalysis<T> implements Transform {
	protected ModuleLoader loader;
	protected String filename;
	protected Module.Method method;
	protected Module.Case methodCase;
	protected HashMap<String,T> stores;
	
	public ForwardFlowAnalysis(ModuleLoader loader) {
		this.loader = loader;
	}
	
	public Module apply(Module module) {	
		ArrayList<Module.TypeDef> types = new ArrayList<Module.TypeDef>();		
		ArrayList<Module.ConstDef> constants = new ArrayList<Module.ConstDef>();
		ArrayList<Module.Method> methods = new ArrayList<Module.Method>();
		
		filename = module.filename();
		
		for(Module.ConstDef type : module.constants()) {
			constants.add(propagate(type));
		}
		for(Module.TypeDef type : module.types()) {
			types.add(propagate(type));
		}		
		for(Module.Method method : module.methods()) {
			methods.add(propagate(method));
		}
		return new Module(module.id(), module.filename(), methods, types, constants);
	}
	
	public Module.ConstDef propagate(Module.ConstDef constant) {
		return constant;
	}
	public Module.TypeDef propagate(Module.TypeDef type) {
		return type;
	}
	
	public Module.Method propagate(Module.Method method) {
		this.method = method;
		ArrayList<Module.Case> cases = new ArrayList<Module.Case>();
		for (Module.Case c : method.cases()) {
			cases.add(propagate(c));
		}
		return new Module.Method(method.name(), method.type(), cases);
	}
	
	public Module.Case propagate(Module.Case mcase) {
		this.methodCase = mcase;
		this.stores = new HashMap<String,T>();
		T init = initialStore();
		Block body = propagate(mcase.body(), init).first();		
		return new Module.Case(mcase.parameterNames(), body, mcase.attributes());
	}		
	
	protected Pair<Block, T> propagate(Block block, T store) {
		
		Block nblock = new Block();
		for(int i=0;i<block.size();++i) {						
			Stmt stmt = block.get(i);								
			try {				
				Code code = stmt.code;

				// First, check for a label which may have incoming information.
				if (code instanceof Code.Label) {
					Code.Label l = (Code.Label) code;
					T tmp = stores.get(l.label);					
					if (tmp != null && store != null) {						
						store = join(store, tmp);
					} else if (tmp != null) {
						store = tmp;
					}					
				}
				
				
				if (store == null) {
					// this indicates dead-code has been reached.
					continue;
				} else if (code instanceof Code.Start) {					
					Code.Start start = (Code.Start) code;
					Code.End end = null;
					// Note, I could make this more efficient!
					Block body = new Block();
					while (++i < block.size()) {
						stmt = block.get(i);
						if (stmt.code instanceof Code.End) {
							end = (Code.End) stmt.code;
							if (end.target.equals(start.label)) {
								// end of loop body found
								break;
							}
						}
						body.add(stmt.code, stmt.attributes());
					}										
					Pair<Block, T> r = propagate(start, end, body, stmt, store);
					nblock.addAll(r.first());
					store = r.second();
					continue;
				} else if (code instanceof Code.IfGoto) {
					Code.IfGoto ifgoto = (Code.IfGoto) code;
					Triple<Stmt, T, T> r = propagate(ifgoto, stmt, store);
					stmt = r.first();
					store = r.third();

					// Now, check to see if the statement has been updated, and
					// process outgoing information accordingly.
					if (stmt.code instanceof Code.IfGoto) {
						Code.IfGoto gto = (Code.IfGoto) stmt.code;
						merge(gto.target, r.second(), stores);
					} else if (stmt.code instanceof Code.Goto) {
						Code.Goto gto = (Code.Goto) stmt.code;
						merge(gto.target, r.second(), stores);
						store = null;
					}
				} else if (code instanceof Code.Switch) {
					Code.Switch sw = (Code.Switch) code;
					Pair<Stmt, List<T>> r = propagate(sw, stmt, store);
					stmt = r.first();					

					// Now, check to see if the statement has been updated, and
					// process outgoing information accordingly.
					if (stmt.code instanceof Code.Switch) {
						// assert r.second().size() == nsw.branches.size()
						Code.Switch nsw = (Code.Switch) stmt.code;
						for(int j=0;j!=nsw.branches.size();++j){
							String target = nsw.branches.get(j).second();
							T nstore = r.second().get(j);
							merge(target, nstore, stores);
						}
						merge(sw.defaultTarget, store, stores);
						store = null;
					} else if (stmt.code instanceof Code.Goto) {
						// assert r.second().size() == 1
						Code.Goto gto = (Code.Goto) stmt.code;
						merge(gto.target, r.second().get(0), stores);
						store = null;
					}
				} else if (code instanceof Code.Throw) {
					// FIXME:  should I do something here?
					store = null;
				} else if (code instanceof Code.Goto) {
					Code.Goto gto = (Code.Goto) stmt.code;
					merge(gto.target, store, stores);
					store = null;
				} else {
					// This indicates a sequential statement was encountered.
					Pair<Stmt, T> r = propagate(stmt, store);
					stmt = r.first();
					store = r.second();
					if (stmt.code instanceof Code.Fail
							|| stmt.code instanceof Code.Return) {
						store = null;
					}
				}
				nblock.add(stmt.code, stmt.attributes());
			} catch (SyntaxError se) {
				throw se;
			} catch (Throwable ex) {
				syntaxError("internal failure", filename, stmt, ex);
			}
		}
		
		return new Pair<Block,T>(nblock,store);
	}
	
	protected void merge(String target, T store, Map<String, T> stores) {		
		T old = stores.get(target);
		if (old == null) {
			stores.put(target, store);
		} else {
			stores.put(target, join(old, store));
		}
	}

	/**
	 * <p>
	 * Propagate through a conditional branch. This produces a potentially
	 * updated statement, and two stores for the true and false branches
	 * respectively. The code of the statement returned is either that of the
	 * original statement, a Skip, or a Goto. The latter two indicate that the
	 * code was proven definitely false, or definitely true (respectively).
	 * </p>
	 * <p>
	 * <b>NOTE:</b> if the returned statement is a goto, then the third element
	 * of the return value must be null; likewise, if the new code is a skip
	 * then the second element must be null.
	 * </p>
	 * 
	 * @param ifgoto
	 *            --- the code of this statement
	 * @param stmt
	 *            --- this statement
	 * @param store
	 *            --- abstract store which holds true immediately before this
	 *            statement.
	 * @return
	 */
	protected abstract Triple<Stmt,T,T> propagate(Code.IfGoto ifgoto, Stmt stmt, T store);

	/**
	 * <p>
	 * Propagate through a multi-way branch. This produces a potentially updated
	 * statement, and multiple stores for the various branches. The code of the
	 * statement returned is either that of the original statement, a Skip, a
	 * Goto, or a switch statement with a reduced number of branches. 
	 * </p>
	 * <p>	
	 * 
	 * @param sw
	 *            --- the code of this statement
	 * @param stmt
	 *            --- this statement
	 * @param store
	 *            --- abstract store which holds true immediately before this
	 *            statement.
	 * @return
	 */
	protected abstract Pair<Stmt,List<T>> propagate(Code.Switch sw, Stmt stmt, T store);
	
	/**
	 * <p>
	 * Propagate through a block statement (e.g. loop, or check), producing a
	 * potentially updated block and the store which holds true immediately
	 * after the statement
	 * </p>
	 * <p>
	 * <b>NOTE:</b> the block returned must include the start and end code of
	 * the block. This allows blocks to be completely bypassed where appropriate
	 * (for example, if a loop is shown to be over an empty collection).
	 * </p>
	 * 
	 * @param start
	 *            --- the start code of the block
	 * @param end
	 *            --- the end code of the block
	 * @param body
	 *            --- the body of the block
	 * @param stmt
	 *            --- the statement being propagated through
	 * @param store
	 *            --- abstract store which holds true immediately before this
	 *            statement.
	 * @return
	 */
	protected abstract Pair<Block, T> propagate(Code.Start code, Code.End end,
			Block body, Stmt stmt, T store);
	
	/**
	 * <p>
	 * Propagate through a sequential statement, producing a potentially updated
	 * statement and the store which holds true immediately after the statement
	 * </p>
	 * 
	 * @param stmt
	 *            --- the statement being propagated through
	 * @param store
	 *            --- abstract store which holds true immediately before this
	 *            statement.
	 * @return
	 */
	protected abstract Pair<Stmt,T> propagate(Stmt stmt, T store);
	
	/**
	 * Determine the initial store for the current method case.
	 * 
	 * @return
	 */
	protected abstract T initialStore();

	/**
	 * Join two abstract stores together producing a new abstract store. Observe
	 * that this operation must not side-effect the two input stores. This is
	 * because they may currently be stored in the stores map.
	 * 
	 * @param store1
	 * @param store2
	 * @return
	 */
	protected abstract T join(T store1, T store2);
}
