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

import java.util.Collections;
import java.util.HashMap;

import wyautl.lang.Automata;
import wyautl.lang.Automaton;
import wyil.ModuleLoader;
import wyil.lang.*;
import wyil.util.ResolveError;

/**
 * <p>
 * Constraint expansion account for any constraints on the types in question.
 * For example:
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
 * 
 * @author David J. Pearce
 * 
 */
public final class ConstraintExpander {
	private final ModuleLoader loader;
	
	/**
	 * The constraints map is a cache of previously expanded constraints.
	 */
	private final HashMap<NameID,Block> constraints = new HashMap<NameID,Block>();
	
	public ConstraintExpander(ModuleLoader loader) {
		this.loader = loader;		
	}
	
	public Block expand(Type type) throws ResolveError {
		if(type instanceof Type.Leaf) {
			return null; // no expansion possible
		}
		Automaton automaton = Type.destruct(type);
		return expand(0,automaton);					
	}
	
	private Block expand(int index, Automaton automaton) throws ResolveError {
		Automaton.State state = automaton.states[index];
		switch(state.kind) {
			case Type.K_VOID:
			case Type.K_ANY:
			case Type.K_META:
			case Type.K_NULL:
			case Type.K_BOOL:
			case Type.K_BYTE:
			case Type.K_CHAR:
			case Type.K_INT:				
			case Type.K_RATIONAL:
			case Type.K_STRING:
				return null;
			case Type.K_TUPLE:
				return expandTuple(index,state,automaton);
			case Type.K_SET:
				return expandSet(index,state,automaton);
			case Type.K_LIST:	
				return expandList(index,state,automaton);
			case Type.K_DICTIONARY:	
				return expandDictionary(index,state,automaton);
			case Type.K_PROCESS:	
				return expandProcess(index,state,automaton);
			case Type.K_RECORD:	
				return expandRecord(index,state,automaton);
			case Type.K_UNION:
				return expandUnion(index,state,automaton);
			case Type.K_NEGATION:
				return expandNegation(index,state,automaton);			
			case Type.K_NOMINAL:					
				return expand((NameID) state.data);
			default:
				// unreachable
				return null;
		}
	}
	
	public Block expand(NameID nid) throws ResolveError {
		
		// first, check cache
		Block constraint = constraints.get(nid);
		if(constraint != null) {
			return constraint;
		}
		
		Module m = loader.loadModule(nid.module());
		Module.TypeDef td = m.type(nid.name());
		if(td == null) {
			throw new ResolveError("unable to resolve type (" + nid + ")");
		}
		
		constraint = expand(td.type());
		if(td.constraint() != null) {
			if(constraint == null) {
				constraint = td.constraint();
			} else {
				constraint.append(td.constraint());
			}
		}

		/**
		 * FIXME: this is actually broken in the case of recursive constraints.
		 * The problem is similar to that of Type Expander. Essentially, we
		 * can't guarantee that the give constraint is complete at this point.
		 * Therefore, we really need to check whether it is before storing it
		 * into the cache.
		 */
		constraints.put(nid, constraint);
		
		return constraint;
	}
		
	private Block expandSet(int index, Automaton.State state, Automaton automaton) throws ResolveError {
		Block constraint = expand(state.children[0], automaton);
		Block blk = null;
		if (constraint != null) {
			blk = new Block(1);
			String label = Block.freshLabel();
			Type type = Type.construct(Automata.extract(automaton, index));
			blk.append(Code.Load(type, Code.THIS_SLOT));
			/**
			 * FIXME: add in modified variables
			 */
			blk.append(Code.ForAll(type, Code.THIS_SLOT + 1, label,
					Collections.EMPTY_LIST));			
			shiftAppend(1,constraint,blk);					
			blk.append(Code.End(label));
		}			
		return blk;
	}
	
	private Block expandList(int index, Automaton.State state, Automaton automaton) throws ResolveError {
		Block constraint = expand(state.children[0], automaton);
		Block blk = null;
		if (constraint != null) {
			blk = new Block(1);
			String label = Block.freshLabel();
			Type type = Type.construct(Automata.extract(automaton, index));
			blk.append(Code.Load(type, Code.THIS_SLOT));	
			/**
			 * FIXME: add in modified variables
			 */
			blk.append(Code.ForAll(type, Code.THIS_SLOT + 1, label,
					Collections.EMPTY_LIST));			
			shiftAppend(1,constraint,blk);					
			blk.append(Code.End(label));
		}			
		return blk;
	}
	
	private Block expandDictionary(int index, Automaton.State state, Automaton automaton) throws ResolveError {
		// TODO: add dictionary constraints
		return null;
	}
	
	private Block expandProcess(int index, Automaton.State state, Automaton automaton) throws ResolveError {
		// TODO: add process constraints
		return null;
	}
	
	private Block expandTuple(int index, Automaton.State state,
			Automaton automaton) throws ResolveError {
		Block blk = null;		
		int[] children = state.children;
		for (int i=0;i!=children.length;++i) {
			Block constraint = expand(children[i], automaton);
			if (constraint != null) {
				if (blk == null) {
					blk = new Block(1);
				}
				Type.Tuple type = (Type.Tuple) Type.construct(Automata.extract(
						automaton, index));
				blk.append(Code.Load(type, Code.THIS_SLOT));
				blk.append(Code.TupleLoad(type, i));
				blk.append(Code.Store(type, Code.THIS_SLOT + 1));
				shiftAppend(1, constraint, blk);
			}			
		}
		return blk;
	}
	
	private Block expandRecord(int index, Automaton.State state,
			Automaton automaton) throws ResolveError {
		Block blk = null;
		String[] fields = (String[]) state.data;
		int[] children = state.children;
		for (int i = 0; i != children.length; ++i) {
			Block constraint = expand(children[i], automaton);
			if (constraint != null) {
				if (blk == null) {
					blk = new Block(1);
				}
				Type.Record type = (Type.Record) Type.construct(Automata
						.extract(automaton, index));
				blk.append(Code.Load(type, Code.THIS_SLOT));
				blk.append(Code.FieldLoad(type, fields[i]));
				blk.append(Code.Store(type, Code.THIS_SLOT + 1));
				shiftAppend(1, constraint, blk);
			}
		}
		return blk;
	}
	
	private Block expandNegation(int index, Automaton.State state, Automaton automaton) throws ResolveError {
		// TODO: add negation constraints
		return null;
	}
	
	private Block expandUnion(int index, Automaton.State state, Automaton automaton) throws ResolveError {
		// TODO: add union constraints
		return null;
	}
	
	/**
	 * The shiftBlock method takes a block and shifts every slot a given amount
	 * to the right. The number of inputs remains the same. This method is used 
	 * 
	 * @param amount
	 * @param src
	 * @return
	 */
	private static void shiftAppend(int amount, Block src, Block dest) {
		HashMap<Integer,Integer> binding = new HashMap<Integer,Integer>();
		for(int i=0;i!=src.numSlots();++i) {
			binding.put(i,i+amount);
		}		
		for(Block.Entry e : src) {
			Code code = e.code.remap(binding);
			dest.append(code,e.attributes());
		}
	}
}
