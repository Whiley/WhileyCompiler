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

package wyil.util.dfa;

import static wyil.util.SyntaxError.syntaxError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.stages.ModuleTransform;
import wyil.util.*;

public abstract class BackwardFlowAnalysis<T> implements ModuleTransform {
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
		Block body = propagate(mcase.body(), last).first();		
		return new Module.Case(mcase.parameterNames(),
				mcase.precondition(), mcase.postcondition(), body);
	}		
	
	protected Pair<Block, T> propagate(Block block, T store) {
		
		Block nblock = new Block();
		for(int i=(block.size()-1);i>=0;--i) {						
			Stmt stmt = block.get(i);						
			try {				
				Code code = stmt.code;

				// First, check for a label which may have incoming information.
				if (code instanceof Code.Label) {
					Code.Label l = (Code.Label) code;
					stores.put(l.label,store);
				} else if (code instanceof Code.End) {					
					Code.Start start = null;
					Code.End end = (Code.End) code;
					// Note, I could make this more efficient!
					Block body = new Block();
					while (--i >= 0) {						
						stmt = block.get(i);
						if (stmt.code instanceof Code.Start) {
							start = (Code.Start) stmt.code;
							if (end.target.equals(start.label)) {
								// start of loop body found
								break;
							}
						}
						body.add(0,stmt.code, stmt.attributes());
					}			
					
					Pair<Block, T> r = propagate(start, end, body, stmt, store);										
					
					nblock.addAll(0,r.first());
					store = r.second();
					continue;
				} else if (code instanceof Code.IfGoto) {
					Code.IfGoto ifgoto = (Code.IfGoto) code;
					T trueStore = stores.get(ifgoto.target);
					if(trueStore == null) {
						System.out.println("PROBLEM");
					}
					Pair<Stmt, T> r = propagate(ifgoto, stmt, trueStore,store);
					stmt = r.first();
					store = r.second();
				} else if (code instanceof Code.Goto) {
					Code.Goto gto = (Code.Goto) stmt.code;
					store = stores.get(gto.target);					
				} else {
					// This indicates a sequential statement was encountered.					
					Pair<Stmt, T> r = propagate(stmt, store);
					stmt = r.first();
					store = r.second();					
				}
				// Must always add to front in backward analysis
				nblock.add(0, stmt.code, stmt.attributes());
			} catch (SyntaxError se) {
				throw se;
			} catch (Throwable ex) {
				syntaxError("internal failure", filename, stmt, ex);
			}
		}
		
		return new Pair<Block,T>(nblock,store);
	}

	/**
	 * <p>
	 * Propagate back from a conditional branch. This produces a potentially
	 * updated statement, and one store representing the state before the
	 * branch. The method accepts two stores --- one originating from the true
	 * branch, and the other from the false branch.
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
	 * @param trueStore
	 *            --- abstract store which holds true immediately after this
	 *            statement on the true branch.
	 * @param falseStore
	 *            --- abstract store which holds true immediately after this
	 *            statement on the false branch.
	 * @return
	 */
	protected abstract Pair<Stmt, T> propagate(Code.IfGoto ifgoto, Stmt stmt,
			T trueStore, T falseStore);

	/**
	 * <p>
	 * Propagate back from a block statement (e.g. loop, or check), producing a
	 * potentially updated block and the store which holds true immediately
	 * before the statement
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
	 * Propagate back from a sequential statement, producing a potentially updated
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
	 * Generate the store which holds true immediately after the last statement
	 * of the method-case body.  By default, this is null and the first return
	 * statement encountered during the backwards propagation initialises things.
	 * 
	 * @return
	 */
	protected T lastStore() {
		return null;
	}
	
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
