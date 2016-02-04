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

package wyil.checks;

import java.util.*;

import wybs.lang.Builder;
import wycc.lang.SyntaxError;
import wycc.lang.Transform;
import wycc.util.Pair;
import wyil.attributes.SourceLocation;
import wyil.lang.*;
import wyil.lang.CodeForest.Index;
import wyil.lang.Codes.*;
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
public class ModuleCheck implements Transform<WyilFile> {
	private String filename;

	public ModuleCheck(Builder builder) {

	}

	public void apply(WyilFile module) {
		filename = module.filename();

		for (WyilFile.FunctionOrMethod method : module.functionOrMethods()) {
			check(method);
		}
	}

	public void check(WyilFile.FunctionOrMethod method) {		
		if(method.isFunction()) {
			checkFunctionPure(method);
		}		
	}

	private static class Handler {
		public final ArrayList<Type> handlers;
		public final HashSet<Type> active;
		public final Handler parent;

		public Handler(java.util.List<Pair<Type, String>> handlers, Handler parent) {
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

	/**
	 * Check a given function is pure. That is all invocations within the
	 * function are to themselves to pure functions, and no heap memory is used.
	 *
	 * @param c
	 */
	protected void checkFunctionPure(WyilFile.FunctionOrMethod c) {
		checkFunctionPure(c.body(),c.code());
	}

	protected void checkFunctionPure(int blockID, CodeForest forest) {
		CodeForest.Block block = forest.get(blockID);
		for (int i = 0; i != block.size(); ++i) {
			CodeForest.Entry e = block.get(i);
			Code code = e.first();			
			if(code instanceof Codes.Invoke && ((Codes.Invoke)code).type(0) instanceof Type.Method) {
				// internal message send
				syntaxError(errorMessage(METHODCALL_NOT_PERMITTED_IN_FUNCTION), filename, e.attribute(SourceLocation.class));
			} else if (code instanceof Codes.IndirectInvoke && ((Codes.IndirectInvoke)code).type(0) instanceof Type.Method) {
				syntaxError(errorMessage(METHODCALL_NOT_PERMITTED_IN_FUNCTION), filename, e.attribute(SourceLocation.class));
			} else if(code instanceof Codes.NewObject) {
				syntaxError(errorMessage(ALLOCATION_NOT_PERMITTED_IN_FUNCTION), filename, e.attribute(SourceLocation.class));
			} else if(code instanceof Codes.Dereference){
				syntaxError(errorMessage(REFERENCE_ACCESS_NOT_PERMITTED_IN_FUNCTION), filename, e.attribute(SourceLocation.class));
			} else if(code instanceof Code.AbstractCompoundBytecode) {
				Code.AbstractCompoundBytecode a = (Code.AbstractCompoundBytecode) code; 
				checkFunctionPure(a.block(), forest);
			}
		}
	}
}
