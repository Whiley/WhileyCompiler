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

package wyc;

import static wyil.util.ErrorMessages.errorMessage;
import static wyil.util.SyntaxError.syntaxError;

import java.util.HashMap;

import wyc.NameResolver.Skeleton;
import wyc.lang.Expr;
import wyc.lang.UnresolvedType;
import wyil.lang.*;
import wyil.util.ErrorMessages;
import wyil.util.Pair;
import wyil.util.ResolveError;
import wyil.util.SyntacticElement;
import wyil.util.Triple;

/**
 * <p>
 * Responsible for expanding all types and constraints for a given module(s).
 * For example, consider these two declarations:
 * </p>
 * 
 * <pre>
 * define Point2D as {int x, int y}
 * define Point3D as {int x, int y, int z}
 * define Point as Point2D | Point3D
 * </pre>
 * <p>
 * This stage will expand the type <code>Point</code> to give its full
 * structural definition. That is,
 * <code>{int x,int y}|{int x,int y,int z}</code>.
 * </p>
 * <p>
 * Type expansion must also account for any constraints on the types in
 * question. For example:
 * </p>
 * 
 * <pre>
 * define nat as int where $ >= 0
 * define natlist as [nat]
 * </pre>
 * <p>
 * The type <code>natlist</code> expands to <code>[int]</code>, whilst its
 * constraint is expanded to <code>all {x in $ | x >= 0}</code>.
 * </p>
 * <p>
 * 
 * @author David J. Pearce
 * 
 */
public final class NameExpander {
	/**
	 * A map from module identifiers to skeleton objects. This is required to
	 * permit registration of source files during compilation.
	 */
	private HashMap<ModuleID, Skeleton> skeletontable = new HashMap<ModuleID, Skeleton>();
	
	/**
	 * Cache of expanded types.
	 */
	private HashMap<NameID,Type> types = new HashMap<NameID,Type>();
	
	/**
	 * Cache of expanded constraints.
	 */
	private HashMap<NameID,Block> constraints = new HashMap<NameID,Block>();
	
	
	/**
	 * Provides information about the declared types and constants of a module.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public abstract static class Skeleton {
		private ModuleID mid;
		
		public Skeleton(ModuleID mid) {
			this.mid = mid;
		}
		
		public ModuleID id() {
			return mid;
		}
		
		public abstract Value constant(String name);
		
		public abstract Type type(String name);
			
		public abstract Type.Function function(String name);
	}

	/**
	 * Register a given skeleton with this expander. This ensures that when
	 * skeleton requests are made, this skeleton will be used instead of
	 * searching for it on the whileypath.
	 * 
	 * @param skeleton
	 *            --- skeleton to register.
	 */
	public void register(Skeleton skeleton) {		
		skeletontable.put(skeleton.id(), skeleton);			
	}
	
	/**
	 * This method fully expands a given type.
	 * 
	 * @param type
	 * @return
	 */
	public Type expandType(Type type) {
		
	}

	private Type expandType(Type type, HashMap<NameID, Type> cache) throws ResolveError {

	}

	private Type expandType(NameID key, HashMap<NameID, Type> cache) throws ResolveError {

	}
	
	/**
	 * This method fully expands the constraints of a given type.
	 * 
	 * @param type
	 * @return
	 */
	public Block expandConstraint(Type type) {
		return null;
	}
}
