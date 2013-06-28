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

package wyrl.util;

import wyautl.core.Automaton;
import wyrl.core.Type;
import wyrl.core.Types;

public class Runtime {
	
	
	/**
	 * Construct an <code>Automaton.List</code> representing the consecutive
	 * list of numbers between <code>start</code> and <code>end</code>
	 * (exclusive).
	 * 
	 * @param automaton
	 *            --- automaton into which to create this List.
	 * @param start
	 *            --- starting index
	 * @param end
	 *            --- final index
	 * @return
	 */
	public static Automaton.List rangeOf(Automaton automaton,
			Automaton.Int _start, Automaton.Int _end) {
		// FIXME: there is a bug here for big integer values.
		int start = _start.intValue();
		int end = _end.intValue();
		int[] children = new int[end - start];
		for (int i = 0; i < children.length; ++i, ++start) {
			children[i] = automaton.add(new Automaton.Int(start));
		}
		return new Automaton.List(children);
	}

	/**
	 * Determine whether a given automaton is <i>accepted</i> by (i.e. contained
	 * in) an given type. For example, consider this very simple type:
	 * 
	 * <pre>
	 * term True
	 * term False
	 * define Bool as True | False
	 * </pre>
	 * 
	 * We can then ask the question as to whether or not the type
	 * <code>Bool</code> accepts the automaton which describes <code>True</code>
	 * . This function is used during rewriting to determine whether or not a
	 * given pattern leaf matches, and also for implementing the <code>is</code>
	 * operator
	 * 
	 * @param type
	 *            --- The type being to check for containment.
	 * @param automaton
	 *            --- The automaton being checked for inclusion.
	 * @return
	 */
	public static boolean accepts(Type type, Automaton automaton) {
		
		// FIXME: this doesn't yet handle cyclic automata
		
		Automaton type_automaton = type.automaton();
		
		return accepts(type_automaton,type_automaton.getRoot(0),automaton,automaton.getRoot(0));
	}
	
	/**
	 * Check whether type accepts automaton from the given states.
	 * 
	 * @param type
	 * @param tIndex
	 * @param automaton
	 * @param aIndex
	 * @return
	 */
	private static boolean accepts(Automaton type, int tIndex, Automaton automaton, int aIndex) {
		Automaton.Term tState = (Automaton.Term) type.get(tIndex);
		Automaton.State aState = type.get(aIndex);
		
		switch(tState.kind){
		case Types.K_Void:
			return false;
		case Types.K_Any:
			return true;
		case Types.K_Bool:
			return aState instanceof Automaton.Bool;
		case Types.K_Int:
			return aState instanceof Automaton.Int;
		case Types.K_Real:
			return aState instanceof Automaton.Real;
		case Types.K_String:
			return aState instanceof Automaton.Strung;
		case Types.K_Set:
			if(aState instanceof Automaton.Set) {
				Automaton.Set aSet = (Automaton.Set) aState;
				return accepts(type,tState,automaton,aSet);				
			}
			return false;
		case Types.K_Bag:
		case Types.K_List:
		case Types.K_Or:
		case Types.K_And:
		
		}
	}
	
	private static boolean accepts(Automaton type, Automaton.Term tState, Automaton automaton, Automaton.Set aSet) {
		Automaton.List list = (Automaton.List) automaton.get(tState.contents);
		Automaton.Collection collection = (Automaton.Collection) automaton
				.get(list.get(1));
		Automaton.Term unbounded = (Automaton.Term) automaton.get(list.get(0));
		boolean isUnbounded = unbounded.kind != Types.K_Void;
		
		// need to now check acceptance.
	}
}

