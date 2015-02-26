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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wycc.lang.SyntaxError;
import wycc.util.Pair;
import wyil.attributes.SourceLocation;
import wyil.lang.*;
import wyil.util.AttributedCodeBlock;
import static wyil.util.ErrorMessages.*;

public abstract class BackwardFlowAnalysis<T> {

	/**
	 * The filename of the module currently being propagated through
	 */
	protected String filename;

	/**
	 * The function or method currently being propagated through.
	 */
	protected WyilFile.FunctionOrMethod method;

	/**
	 * The root block currently being propagated through.
	 */
	protected AttributedCodeBlock rootBlock;

	/**
	 * The temporary abstract stores being generated during propagation.
	 */
	protected HashMap<String, T> stores;

	public void apply(WyilFile module) {
		filename = module.filename();

		for(WyilFile.Block d : module.blocks()) {
			if(d instanceof WyilFile.Constant) {
				WyilFile.Constant cd = (WyilFile.Constant) d;
				module.replace(cd,propagate((cd)));
			} else if(d instanceof WyilFile.Type) {
				WyilFile.Type td = (WyilFile.Type) d;
				module.replace(td,propagate(td));
			} else if(d instanceof WyilFile.FunctionOrMethod) {
				WyilFile.FunctionOrMethod md = (WyilFile.FunctionOrMethod) d;
				if(!md.hasModifier(Modifier.NATIVE)) {
					// native functions/methods don't have bodies
					module.replace(md,propagate(md));
				}
			}
		}
	}

	protected WyilFile.Constant propagate(WyilFile.Constant constant) {
		return constant;
	}
	protected WyilFile.Type propagate(WyilFile.Type type) {
		return type;
	}

	protected WyilFile.FunctionOrMethod propagate(
			WyilFile.FunctionOrMethod method) {
		this.method = method;
		this.stores = new HashMap<String,T>();
		this.rootBlock = method.body();
		T last = lastStore();
		propagate(null, rootBlock, last, Collections.EMPTY_LIST);
		
		// FIXME: should we propagate through the precondition and postconditions !?
		
		return new WyilFile.FunctionOrMethod(method.modifiers(), method.name(),
				method.type(), method.body(), method.precondition(),
				method.postcondition(), method.attributes());
	}

	/**
	 * Propagate a given store backwards through this bytecode block. A list of
	 * exception handlers that are active is provided.
	 *
	 * @param parentIndex
	 *            The bytecode index of the bytecode containing this block, or
	 *            the empty index otherwise.
	 * @param block
	 *            The bytecode block to be propagated through.
	 * @param store
	 *            The store which holds at the end of this block.
	 * @param handlers
	 *            The list of active exception handlers
	 * @return
	 */
	protected T propagate(CodeBlock.Index parentIndex, CodeBlock block, T store, List<Pair<Type,String>> handlers) {

		for (int i = block.size()-1; i >= 0; --i) {
			Code code = block.get(i);

			// Construct the bytecode index
			CodeBlock.Index id = new CodeBlock.Index(parentIndex,i);

			try {
				// First, check for a label which may have incoming information.
				if (code instanceof Codes.Loop) {
					Codes.Loop loop = (Codes.Loop) code;
					store = propagate(id, loop, store, handlers);
					continue;
				} else if (code instanceof Codes.Label) {
					Codes.Label l = (Codes.Label) code;
					stores.put(l.label,store);
				} else if (code instanceof Codes.If) {
					Codes.If ifgoto = (Codes.If) code;
					T trueStore = stores.get(ifgoto.target);
					store = propagate(id, ifgoto, trueStore, store);
				} else if (code instanceof Codes.IfIs) {
					Codes.IfIs iftype = (Codes.IfIs) code;
					T trueStore = stores.get(iftype.target);
					store = propagate(id, iftype, trueStore, store);
				} else if (code instanceof Codes.Switch) {
					Codes.Switch sw = (Codes.Switch) code;

					ArrayList<T> swStores = new ArrayList<T>();
					for(int j=0;j!=sw.branches.size();++j){
						String target = sw.branches.get(j).second();
						swStores.add(stores.get(target));
					}
					T defStore = stores.get(sw.defaultTarget);

					store = propagate(id, sw, swStores, defStore);
				} else if (code instanceof Codes.Goto) {
					Codes.Goto gto = (Codes.Goto) code;
					store = stores.get(gto.target);
				} else {
					// This indicates a sequential statement was encountered.
					if (code instanceof Codes.Return
						|| code instanceof Codes.Fail) {
						store = lastStore();
					}
					store = propagate(id, code, store);
				}
			} catch (SyntaxError se) {
				throw se;
			} catch (Throwable ex) {
				internalFailure("internal failure", filename, ex, rootBlock.attribute(id,SourceLocation.class));			}
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
	 *            --- Index of bytecode in root CodeBlock
	 * @param ifgoto
	 *            --- the code of this statement
	 * @param trueStore
	 *            --- abstract store which holds true immediately after this
	 *            statement on the true branch.
	 * @param falseStore
	 *            --- abstract store which holds true immediately after this
	 *            statement on the false branch.
	 * @return
	 */
	protected abstract T propagate(CodeBlock.Index index, Codes.If ifgoto,
			T trueStore, T falseStore);

	/**
	 * <p>
	 * Propagate back from a type test. This produces a store representing the
	 * state before the branch. The method accepts two stores --- one
	 * originating from the true branch, and the other from the false branch.
	 * </p>
	 *
	 * @param index
	 *            --- Index of bytecode in root CodeBlock
	 * @param iftype
	 *            --- the code of this statement
	 * @param trueStore
	 *            --- abstract store which holds true immediately after this
	 *            statement on the true branch.
	 * @param falseStore
	 *            --- abstract store which holds true immediately after this
	 *            statement on the false branch.
	 * @return
	 */
	protected abstract T propagate(CodeBlock.Index index, Codes.IfIs iftype, T trueStore,
			T falseStore);

	/**
	 * <p>
	 * Propagate back from a multi-way branch. This accepts multiple stores ---
	 * one for each of the various branches.
	 * </p>
	 *
	 * @param index
	 *            --- Index of bytecode in root CodeBlock
	 * @param sw
	 *            --- the code of this statement
	 * @param stores
	 *            --- abstract stores coming from the various branches.
	 *            statement.
	 * @param defStore
	 *            --- abstract store coming from default branch
	 * @return
	 */
	protected abstract T propagate(CodeBlock.Index index, Codes.Switch sw,
			List<T> stores, T defStore);

	/**
	 * <p>
	 * Propagate back from a loop statement, producing a store which holds true
	 * immediately before the statement
	 * </p>
	 *
	 * @param loop
	 *            --- the code of the block
	 * @param body
	 *            --- the body of the block
	 * @param store
	 *            --- abstract store which holds true immediately before this
	 *            statement.
	 * @return
	 */
	protected abstract T propagate(CodeBlock.Index index, Codes.Loop code, T store,
			List<Pair<Type, String>> handlers);

	/**
	 * <p>
	 * Propagate back from a sequential statement, producing a store which holds
	 * true immediately after the statement
	 * </p>
	 *
	 * @param index
	 *            --- Index of bytecode in root CodeBlock
	 * @param code
	 *            --- Bytecode being propagated through
	 * @param store
	 *            --- abstract store which holds true immediately before this
	 *            statement.
	 * @return
	 */
	protected abstract T propagate(CodeBlock.Index index, Code code, T store);

	/**
	 * Propagate from an exception handler.
	 *
	 * @param handler
	 *            --- type of handler catching exception
	 * @param normalStore
	 *            --- store from non-exception flow
	 * @param exceptionStore
	 *            --- store from exception flow
	 * @return
	 */
	protected abstract T propagate(Type handler, T normalStore, T exceptionStore);

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
