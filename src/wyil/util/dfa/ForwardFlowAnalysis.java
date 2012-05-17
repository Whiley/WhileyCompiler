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

import static wybs.lang.SyntaxError.internalFailure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wybs.lang.SyntaxError;
import wyil.Transform;
import wyil.lang.*;
import wyil.util.*;
import static wyil.lang.Block.*;

public abstract class ForwardFlowAnalysis<T> implements Transform {
	protected String filename;
	protected WyilFile.Method method;
	protected WyilFile.Case methodCase;
	protected Block block;
	protected HashMap<String,T> stores;
	
	public void apply(WyilFile module) {			
		filename = module.filename();
		
		for(WyilFile.ConstDef type : module.constants()) {
			module.add(propagate(type));
		}
		for(WyilFile.TypeDef type : module.types()) {
			module.add(propagate(type));
		}	
		
		for(WyilFile.Method method : module.methods()) {					
			if(!method.isNative()) {
				// native functions/methods don't have bodies
				module.add(propagate(method));
			}
		}		
	}
	
	protected WyilFile.ConstDef propagate(WyilFile.ConstDef constant) {
		return constant;
	}
	
	protected WyilFile.TypeDef propagate(WyilFile.TypeDef type) {
		return type;
	}
	
	protected WyilFile.Method propagate(WyilFile.Method method) {
		this.method = method;
		ArrayList<WyilFile.Case> cases = new ArrayList<WyilFile.Case>();
		for (WyilFile.Case c : method.cases()) {
			cases.add(propagate(c));
		}
		return new WyilFile.Method(method.modifiers(), method.name(), method.type(), cases);
	}
	
	protected WyilFile.Case propagate(WyilFile.Case mcase) {
		this.methodCase = mcase;		
		this.stores = new HashMap<String,T>();
		this.block = mcase.body();
		T init = initialStore();		
		propagate(0, mcase.body().size(), init, Collections.EMPTY_LIST);		
		return mcase;
	}		
	
	protected T propagate(int start, int end, T store, List<Pair<Type,String>> handlers) {
		for(int i=start;i<end;++i) {						
			Entry entry = block.get(i);			
			try {				
				Code code = entry.code;
				
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
				
				T oldStore = store;
				
				if (store == null) {
					// this indicates dead-code has been reached.
					continue;
				} else if (code instanceof Code.Loop) {
					Code.Loop loop = (Code.Loop) code;
					Block.Entry nEntry = entry;
					int s = i;
					// Note, I could make this more efficient!					
					while (++i < block.size()) {
						nEntry = block.get(i);
						if (nEntry.code instanceof Code.Label) {
							Code.Label l = (Code.Label) nEntry.code;
							if (l.label.equals(loop.target)) {
								// end of loop body found
								break;
							}
						}						
					}
					store = propagate(s, i, loop, entry, store, handlers);					
					continue;
				} else if (code instanceof Code.IfGoto) {
					Code.IfGoto ifgoto = (Code.IfGoto) code;
					Pair<T, T> r = propagate(i, ifgoto, entry, store);					
					store = r.second();
					merge(ifgoto.target, r.first(), stores);
				}  else if (code instanceof Code.IfType) {
					Code.IfType ifgoto = (Code.IfType) code;
					Pair<T, T> r = propagate(i, ifgoto, entry, store);					
					store = r.second();
					merge(ifgoto.target, r.first(), stores);
				} else if (code instanceof Code.Switch) {
					Code.Switch sw = (Code.Switch) code;
					
					List<T> r = propagate(i, sw, entry, store);										

					// assert r.second().size() == nsw.branches.size()
					Code.Switch nsw = (Code.Switch) entry.code;
					for(int j=0;j!=nsw.branches.size();++j){
						String target = nsw.branches.get(j).second();
						T nstore = r.get(j);
						merge(target, nstore, stores);
					}
					merge(sw.defaultTarget, store, stores);
					store = null;
				} else if (code instanceof Code.TryCatch) {
					Code.TryCatch sw = (Code.TryCatch) code;					
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
					
					ArrayList<Pair<Type,String>> nhandlers = new ArrayList<Pair<Type,String>>(handlers);														
					nhandlers.addAll(0,sw.catches);
					store = propagate(s+1,i,store,nhandlers);
					i = i - 1; // this is necessary since last label of
								// try-catch is first label of catch handler
				} else if (code instanceof Code.Goto) {
					Code.Goto gto = (Code.Goto) entry.code;
					merge(gto.target, store, stores);
					store = null;
				} else {
					// This indicates a sequential statement was encountered.
					store = propagate(i, entry, store);					
					if (entry.code instanceof Code.Return
							|| entry.code instanceof Code.Throw) {
						store = null;
					}
				}				
					
				mergeHandlers(i,code,oldStore,handlers,stores);
				
			} catch (SyntaxError se) {
				throw se;
			} catch (Throwable ex) {
				internalFailure("internal failure", filename, entry, ex);
			}
		}
		
		return store;
	}
	
	private void merge(String target, T store, Map<String, T> stores) {		
		T old = stores.get(target);
		if (old == null) {
			stores.put(target, store);
		} else {
			stores.put(target, join(old, store));
		}
	}

	protected void mergeHandlers(int index, Code code, T store, List<Pair<Type, String>> handlers,
			Map<String, T> stores) {
		if(code instanceof Code.Throw) {
			Code.Throw t = (Code.Throw) code;	
			mergeHandler(t.type,store,handlers,stores);
		} else if(code instanceof Code.IndirectInvoke) {
			Code.IndirectInvoke i = (Code.IndirectInvoke) code;			
			mergeHandler(i.type.throwsClause(),store,handlers,stores);
		} else if(code instanceof Code.Invoke) {
			Code.Invoke i = (Code.Invoke) code;	
			mergeHandler(i.type.throwsClause(),store,handlers,stores);
		} else if(code instanceof Code.IndirectSend) {
			Code.IndirectSend i = (Code.IndirectSend) code;
			mergeHandler(i.type.throwsClause(),store,handlers,stores);
		} else if(code instanceof Code.Send) {
			Code.Send i = (Code.Send) code;			
			mergeHandler(i.type.throwsClause(),store,handlers,stores);
		}
	}
	
	protected void mergeHandler(Type type, T store, List<Pair<Type, String>> handlers,
			Map<String, T> stores) {
		for(Pair<Type,String> p : handlers) {
			Type handler = p.first();			

			if(Type.isSubtype(handler,type)) {
				T nstore = propagate(handler,store);
				merge(p.second(),nstore,stores);
				return; // completely subsumed
			} else if(Type.isSubtype(type, handler)) {
				T nstore = propagate(handler,store);
				merge(p.second(),nstore,stores);
				// not completely subsumed
				type = Type.intersect(type,Type.Negation(handler));
			} 
		}
	}
	
	/**
	 * <p>
	 * Propagate through a conditional branch. This produces two stores for the
	 * true and false branches respectively. The code of the statement returned
	 * is either that of the original statement, a Skip, or a Goto. The latter
	 * two indicate that the code was proven definitely false, or definitely
	 * true (respectively).
	 * </p>
	 * 
	 * @param index
	 *            --- the index of this bytecode in the method's block
	 * @param ifgoto
	 *            --- the code of this statement
	 * @param entry
	 *            --- Block entry for this bytecode.
	 * @param store
	 *            --- abstract store which holds true immediately before this
	 *            statement.
	 * @return
	 */
	protected abstract Pair<T,T> propagate(int index, Code.IfGoto ifgoto, Entry entry, T store);

	/**
	 * <p>
	 * Propagate through a type test. This produces two stores for the true and
	 * false branches respectively. The code of the statement returned is either
	 * that of the original statement, a Skip, or a Goto. The latter two
	 * indicate that the code was proven definitely false, or definitely true
	 * (respectively).
	 * </p>
	 * 
	 * @param index
	 *            --- the index of this bytecode in the method's block
	 * @param iftype
	 *            --- the code of this statement
	 * @param entry
	 *            --- Block entry for this bytecode.
	 * @param store
	 *            --- abstract store which holds true immediately before this
	 *            statement.
	 * @return
	 */
	protected abstract Pair<T, T> propagate(int index, Code.IfType iftype,
			Entry entry, T store);

	/**
	 * <p>
	 * Propagate through a multi-way branch. This produces multiple stores ---
	 * one for each of the various branches. 
	 * </p>
	 * 
	 * @param index
	 *            --- the index of this bytecode in the method's block
	 * @param sw
	 *            --- the code of this statement
	 * @param entry
	 *            --- block entry for this bytecode
	 * @param store
	 *            --- abstract store which holds true immediately before this
	 *            statement.
	 * @return
	 */
	protected abstract List<T> propagate(int index, Code.Switch sw, Entry entry, T store);

	/**
	 * Propagate an exception into a catch handler.
	 * 
	 * @param handler
	 *            --- type of handler catching exception
	 * @param store
	 *            --- store immediately before cause
	 * @return
	 */
	protected abstract T propagate(Type handler, T store);
	
	/**
	 * <p>
	 * Propagate through a loop statement, producing a store which holds true
	 * immediately after the statement
	 * </p>
	 * <p>
	 * <b>NOTE: the <code>start</code> index holds the loop code, whilst the
	 * <code>end</code> index holds the end code.
	 * </p>
	 * 
	 * @param start
	 *            --- the start index of loop block
	 * @param end
	 *            --- last index of loop block
	 * @param end
	 *            --- instruction block
	 * @param code
	 *            --- the start code of the block
	 * @param entry
	 *            --- the block entry for the loop statement
	 * @param store
	 *            --- abstract store which holds true immediately before this
	 *            statement.
	 * @return
	 */
	protected abstract T propagate(int start, int end, 
			Code.Loop code, Entry entry, T store, List<Pair<Type,String>> handlers);

	/**
	 * <p>
	 * Propagate through a sequential statement, producing a store which holds
	 * true immediately after the statement
	 * </p>
	 * 
	 * @param index
	 *            --- the index of this bytecode in the method's block
	 * @param entry
	 *            --- block entry for this bytecode
	 * @param store
	 *            --- abstract store which holds true immediately before this
	 *            statement.
	 * @return
	 */
	protected abstract T propagate(int index, Entry entry, T store);
	
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
