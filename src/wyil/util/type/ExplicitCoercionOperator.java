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

package wyil.util.type;

import static wyil.lang.Type.*;
import wyautl.lang.*;

/**
 * <p>
 * The explicit coercion operator extends the implicit coercion operator to
 * include coercions which must be specified with an explicit cast operation.
 * This is necessary because such coercions correspond to a loss of precision
 * and, hence, may fail at runtime. An example is the following
 * </p>
 * 
 * <pre>
 * char f(int x):
 *     return (char) x
 * </pre>
 * 
 * <p>
 * The above will only compile if the explicit <code>(char)</code> cast is
 * provided. This is required because a <code>char</code> corresponds only to a
 * subset of the possible integers (i.e. those codepoints defined by the Unicode
 * standard). The semantics of the Whiley language dictate that, should the
 * integer lie outside the range of permissible code points, then a runtime
 * fault is raised.
 * </p>
 * 
 * <b>NOTE:</b> as for the subtype operator, both types must have been
 * normalised beforehand to guarantee correct results from this operator. </p>
 * 
 * @author David J. Pearce
 * 
 */
public class ExplicitCoercionOperator extends ImplicitCoercionOperator {
	
	public ExplicitCoercionOperator(Automaton fromAutomata, Automaton toAutomata) {
		super(fromAutomata,toAutomata);
	}
	
	@Override
	public boolean isIntersectionInner(int fromIndex, boolean fromSign,
			int toIndex, boolean toSign) {
		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];
		int fromKind = fromState.kind;
		int toKind = toState.kind;

		if (primitiveSubtype(fromKind, toKind)) {
			return fromSign == toSign || (fromSign && !toSign);
		} else {
			return super.isIntersectionInner(fromIndex, fromSign, toIndex,
					toSign);
		}
	}
	
	private static boolean primitiveSubtype(int fromKind, int toKind) {
		if (fromKind == K_CHAR && toKind == K_INT) {
			// ints can flow (explicitly) into chars
			return true;
		} 
		return false;
	}
}