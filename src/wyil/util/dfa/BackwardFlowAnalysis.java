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
import wyil.Transform;
import wyil.lang.*;
import wyil.lang.Block.Entry;
import wyil.util.*;
import static wyil.lang.Block.*;

public abstract class BackwardFlowAnalysis<T> implements Transform {
	protected ModuleLoader loader;
	protected String filename;
	protected Module.Method method;
	protected Module.Case methodCase;
	protected HashMap<String,T> stores;
	
	public BackwardFlowAnalysis(ModuleLoader loader) {
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
		T last = lastStore();						
		propagate(0, mcase.body().size(), last);		
		return mcase;
	}		
	
	protected T propagate(int start, int end, T store) {
		Block block = methodCase.body();
		
		for(int i=end-1;i>=start;--i) {						
			Entry stmt = block.get(i);						
			try {				
				Code code = stmt.code;

				// First, check for a label which may have incoming information.
				if (code instanceof Code.Label) {
					Code.Label l = (Code.Label) code;
					stores.put(l.label,store);
				} else if (code instanceof Code.End) {					
					Code.Loop loop = null;
					String label = ((Code.End) code).label;
					int loopEnd = i;
					while (--i >= 0) {						
						stmt = block.get(i);
						if (stmt.code instanceof Code.Loop) {
							loop = (Code.Loop) stmt.code;
							if (label.equals(loop.target)) {
								// start of loop body found
								break;
							}
						}						
					}			
					
					store = propagate(i, loopEnd, loop, stmt, store);															
					continue;
				} else if (code instanceof Code.IfGoto) {
					Code.IfGoto ifgoto = (Code.IfGoto) code;
					T trueStore = stores.get(ifgoto.target);					
					store = propagate(i, ifgoto, stmt, trueStore,store);										
				} else if (code instanceof Code.Goto) {
					Code.Goto gto = (Code.Goto) stmt.code;
					store = stores.get(gto.target);					
				} else {
					// This indicates a sequential statement was encountered.					
					store = propagate(i, stmt, store);									
				}
			} catch (SyntaxError se) {
				throw se;
			} catch (Throwable ex) {
				syntaxError("internal failure", filename, stmt, ex);
			}
		}
		
		return store;
	}

	/**
	 * <p>
	 * Propagate back from a conditional branch. This produces a potentially
	 * updated store representing the state before the branch. The method
	 * accepts two stores --- one originating from the true branch, and the
	 * other from the false branch.
	 * </p>
	 * 
	 * @param index
	 *            --- the index of this bytecode in the method's block
	 * @param ifgoto
	 *            --- the code of this statement
	 * @param stmt
	 *            --- this statement
	 * @param trueStore
	 *            --- abstract store which holds true immediately after this
	 *            statement on the true branch.
	 * @param falseStore
	 *            --- abstract store which holds true immediately after this
	 *            statement on the false branch.
	 * @return
	 */
	protected abstract T propagate(int index, Code.IfGoto ifgoto, Entry stmt,
			T trueStore, T falseStore);

	/**
	 * <p>
	 * Propagate back from a type test. This produces a store representing the
	 * state before the branch. The method accepts two stores --- one
	 * originating from the true branch, and the other from the false branch.
	 * </p>
	 * 
	 * @param index
	 *            --- the index of this bytecode in the method's block
	 * @param iftype
	 *            --- the code of this statement
	 * @param stmt
	 *            --- this statement
	 * @param trueStore
	 *            --- abstract store which holds true immediately after this
	 *            statement on the true branch.
	 * @param falseStore
	 *            --- abstract store which holds true immediately after this
	 *            statement on the false branch.
	 * @return
	 */
	protected abstract T propagate(int index, Code.IfType iftype, Entry stmt,
			T trueStore, T falseStore);

	/**
	 * <p>
	 * Propagate back from a multi-way branch. This accepts multiple stores ---
	 * one for each of the various branches. 
	 * </p>
	 * 
	 * @param index
	 *            --- the index of this bytecode in the method's block
	 * @param sw
	 *            --- the code of this statement
	 * @param entry
	 *            --- block entry for this bytecode
	 * @param stores
	 *            --- abstract stores coming from the various branches.
	 *            statement.
	 * @return
	 */
	protected abstract T propagate(int index, Code.Switch sw, Entry entry, List<T> stores);


	/**
	 * <p>
	 * Propagate back from a loop statement, producing a store which holds true
	 * immediately before the statement
	 * </p>
	 * 
	 * @param index
	 *            --- the index of this bytecode in the method's block
	 * @param loop
	 *            --- the code of the block
	 * @param body
	 *            --- the body of the block
	 * @param stmt
	 *            --- the statement being propagated through
	 * @param store
	 *            --- abstract store which holds true immediately before this
	 *            statement.
	 * @return
	 */
	protected abstract T propagate(int start, int end, Code.Loop code, Entry stmt,
			T store);

	/**
	 * <p>
	 * Propagate back from a sequential statement, producing a store which holds
	 * true immediately after the statement
	 * </p>
	 * 
	 * @param index
	 *            --- the index of this bytecode in the method's block
	 * @param stmt
	 *            --- the statement being propagated through
	 * @param store
	 *            --- abstract store which holds true immediately before this
	 *            statement.
	 * @return
	 */
	protected abstract T propagate(int index, Entry stmt, T store);

	/**
	 * Generate the store which holds true immediately after the last statement
	 * of the method-case body.  By default, this is null and the first return
	 * statement encountered during the backwards propagation initialises things.
	 * 
	 * @return
	 */
	protected T lastStore() {
		return null;
	}	
}
