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

package wyautl.lang;

import java.util.Arrays;

/**
 * <p>
 * Provides a standard (ie simplistic) interpretation of automata which
 * provides a suitable base for more complex systems. It is also useful for
 * testing simple kinds of automata.
 * </p>
 * <p>
 * In the default interpretation, a value is a tree where each node has a kind
 * and zero or more children. A value is accepted by a (deterministic) state if
 * it has the same kind, and every child value is accepted by the corresponding
 * child state. For non-deterministic states, we require that every child value
 * is accepted by some child state.
 * </p>
 * <p>
 * <b>NOTE:</b> in the default interpretation, supplementary data is ignored.
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public class DefaultInterpretation implements Interpretation<DefaultInterpretation.Term> {

	/**
	 * Represents a term which may be accepted by an automaton under the default
	 * interpretation.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Term {
		public final int kind;
		public final Term[] children;
		public final Object data;

		public Term(int kind, Object data, Term... children) {
			this.kind = kind;
			this.children = children;		
			this.data = data;
		}

		public boolean equals(Object o) {
			if (o instanceof Term) {
				Term dv = (Term) o;
				if(kind == dv.kind && Arrays.equals(children, dv.children)) {
					if(data == null) {
						return dv.data == null;
					} else {
						return data.equals(dv.data);
					}
				} 
			}
			return false;
		}

		public int hashCode() {
			return kind + Arrays.hashCode(children);
		}

		public String toString() {
			String middle = "";
			boolean firstTime=true;
			for(Term child : children) {
				if(!firstTime) {
					middle = middle + ",";
				}
				firstTime=false;
				middle = middle + child;
			}
			return kind + "(" + middle + ")";
		}
	}
	
	/**
	 * Construct a value from an automata. Will throw an
	 * IllegalArgumentException if the value is not concrete.
	 * 
	 * @param automata
	 * @return
	 */
	public static Term construct(Automaton automata) {
		if(!Automata.isConcrete(automata)) {
			throw new IllegalArgumentException("Cannot construct value from non-concrete automata");
		}
		return construct(0,automata);
	}
	
	private static Term construct(int index, Automaton automata) {
		Automaton.State state = automata.states[index];
		Term[] children = new Term[state.children.length];
		int i = 0;
		for(int c : state.children) {			
			children[i++] = construct(c,automata);
		}
		return new Term(state.kind,state.data,children);
	}
		
	public boolean accepts(Automaton automata, Term value) {
		return accepts(0,automata,value);
	}
	
	public boolean accepts(int index, Automaton automata, Term value) {		
		Automaton.State state = automata.states[index];
		if(state.kind == value.kind) {
			if (state.data == null && value.data != null) {
				return false;
			} else if (state.data != null && !state.data.equals(value.data)) {
				return false;
			}
			if(state.deterministic) {
				int[] schildren = state.children;
				Term[] vchildren = value.children;
				if(schildren.length != vchildren.length) {
					return false;
				}				
				int length = schildren.length;
				for(int i=0;i!=length;++i) {
					int schild = schildren[i];					
					Term vchild = vchildren[i];
					if(!accepts(schild,automata,vchild)) {
						return false;
					}
				}
				return true;
			} else {
				// non-deterministic case
				int[] schildren = state.children;
				Term[] vchildren = value.children;
				
				if (vchildren.length == 0 && schildren.length > 0) {
					return false;
				}
				
				for(int i=0;i!=vchildren.length;++i) {
					Term vchild = vchildren[i];
					boolean matched = false;
					for(int j=0;j!=schildren.length;++j) {
						int schild = schildren[j];							
						if(accepts(schild,automata,vchild)) {
							matched = true;
							break;
						}
					}
					if(!matched) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
}
