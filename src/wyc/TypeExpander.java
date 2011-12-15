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

import java.util.ArrayList;
import java.util.HashMap;

import wyautl.lang.Automata;
import wyautl.lang.Automaton;
import wyautl.lang.Automaton.State;
import wyc.NameResolver.Skeleton;
import wyc.lang.Expr;
import wyc.lang.UnresolvedType;
import wyil.ModuleLoader;
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
 * 
 * @author David J. Pearce
 * 
 */
public final class TypeExpander {
	
	private final ModuleLoader loader;
	
	/**
	 * A map from module identifiers to skeleton objects. This is required to
	 * permit registration of source files during compilation.
	 */
	private final HashMap<ModuleID, Skeleton> skeletontable = new HashMap<ModuleID, Skeleton>();
			
	/**
	 * Cache of expanded constraints.
	 */
	private final HashMap<NameID,Block> constraints = new HashMap<NameID,Block>();
	
	public TypeExpander(ModuleLoader loader) {
		this.loader = loader;
	}
	
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
	public Type expand(Type type) throws ResolveError {		
		if(type instanceof Type.Leaf) {
			return type; // no expansion possible
		}
		Automaton automaton = Type.destruct(type);

		// first, check whether it's actually worth doing anything
		boolean hasNominal = false;
		for(Automaton.State state : automaton.states) {
			if(state.kind == Type.K_NOMINAL) {
				hasNominal = true;
				break;
			}
		}
		
		if(hasNominal) {
			// ok, possibility of expansion exists
			ArrayList<State> states = new ArrayList<State>();
			HashMap<NameID,Integer> roots = new HashMap<NameID,Integer>();
			expand(0,automaton,roots,states);
			automaton = new Automaton(states);
			return Type.construct(automaton);
		} else {
			return type;
		}
	}

	private int expand(int index, Automaton automaton,
			HashMap<NameID, Integer> roots, ArrayList<State> states)
			throws ResolveError {
		
		Automaton.State state = automaton.states[index];
		int kind = state.kind;
		
		if(kind == Type.K_NOMINAL) {
			NameID key = (NameID) state.data;
			return expand(key,roots,states);
		} else {
			int myIndex = states.size();			
			states.add(null);
			int[] ochildren = state.children;
			int[] nchildren = new int[ochildren.length];
			for(int i=0;i!=ochildren.length;++i) {
				nchildren[i] = expand(i,automaton,roots,states);
			}
			boolean deterministic = kind != Type.K_UNION;		
			Automaton.State myState = new Automaton.State(kind,state.data,deterministic,nchildren);
			states.set(myIndex,myState);
			return myIndex;
		}
	}

	private int expand(NameID key, HashMap<NameID, Integer> roots,
			ArrayList<State> states) throws ResolveError {
		
		// First, check the various caches we have
		Integer root = roots.get(key);			
		if (root != null) { return root; } 		
		
		// check whether this type is external or not
		Skeleton skeleton = skeletontable.get(key.module());
		if (skeleton == null) {						
			// indicates a non-local key which we can resolve immediately
			
			// FIXME: there is an implicit assumption here that types are
			// already fully expanded. In fact, this may not be the case.
			
			Module mi = loader.loadModule(key.module());
			Module.TypeDef td = mi.type(key.name());	
			return append(td.type(),states);			
		} 
		
		Type type = skeleton.type(key.name());
		if(type == null) {
			// FIXME: need a better error message!
			throw new ResolveError("unknown type");
		}
		
		// following is needed to terminate any recursion
		roots.put(key, states.size());

		// now, expand the given type fully			
		if(type instanceof Type.Leaf) {
			// to avoid unnecessarily creating an array of size one
			int myIndex = states.size();
			int kind = Type.leafKind((Type.Leaf)type);			
			states.add(new Automaton.State(kind,null,true,Automaton.NOCHILDREN));
			return myIndex;
		} else {
			return expand(0, Type.destruct(type), roots, states);
		}
		
		// TODO: performance can be improved here, but actually assigning the
		// constructed type into a cache of previously expanded types cache.
		// This is challenging, in the case that the type may not be complete at
		// this point. In particular, if it contains any back-links above this
		// index there could be an issue.
	}	
	
	private static int append(Type type, ArrayList<State> states) {		
		int myIndex = states.size();
		Automaton automaton = Type.destruct(type);
		Automaton.State[] tStates = automaton.states;
		int[] rmap = new int[tStates.length];
		for(int i=0,j=myIndex;i!=rmap.length;++i,++j) {
			rmap[i] = j;
		}
		for(Automaton.State state : tStates) {
			states.add(Automata.remap(state, rmap));
		}
		return myIndex;		
	}
}
