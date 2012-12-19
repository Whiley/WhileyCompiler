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

package wyone.util;

import wyautl.core.Automaton;

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

}
