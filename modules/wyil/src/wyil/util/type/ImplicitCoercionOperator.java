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
import wyautl_old.lang.*;

/**
 * <p>
 * The implicit coercion operator extends the basic subtype operator to consider
 * <i>implicit coercions</i> in the subtype computation. Thus, <code>T1</code>
 * is a <i>coercive subtype</i> of <code>T2</code> iff <code>T1 ~> T3</code> and
 * <code>T3 <: T2</code> (where <code>T1 ~> T3</code> is taken to mean
 * <code>T1</code> can be implicitly coerced into <code>T3</code>).
 * </p>
 * <p>
 * There are several places in the Whiley language where implicit coercions are
 * applied. For example, in the following:
 *
 * <pre>
 * real f(int x):
 *     return x
 * </pre>
 *
 * The above compiles correctly because, while <code>real :> int</code> does not
 * hold, an <code>int</code> can be implicitly coerced into a <code>real</code>.
 * </p>
 * <p>
 * <b>NOTE:</b> as for the subtype operator, both types must have been
 * normalised beforehand to guarantee correct results from this operator.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class ImplicitCoercionOperator extends SubtypeOperator {

	public ImplicitCoercionOperator(Automaton fromAutomata, Automaton toAutomata) {
		super(fromAutomata,toAutomata);
	}

	@Override
	public boolean isIntersectionInner(int fromIndex, boolean fromSign, int toIndex,
			boolean toSign) {
		Automaton.State fromState = from.states[fromIndex];
		Automaton.State toState = to.states[toIndex];
		int fromKind = fromState.kind;
		int toKind = toState.kind;

		if (primitiveSubtype(fromKind,toKind)) {
			return fromSign == toSign || (fromSign && !toSign);
		} else if(fromKind == K_SET && toKind == K_LIST) {
			if (fromSign != toSign) {
				// nary nodes
				int fromChild = fromState.children[0];
				int toChild = toState.children[0];
				if (!isIntersection(fromChild, fromSign, toChild, toSign)) {
					return false;
				}
			}
			return true;
		} else if (fromKind == K_MAP && toKind == K_LIST) {
			if (!fromSign || !toSign) {
				// nary nodes
				int fromKey = fromState.children[0];
				int fromValue = fromState.children[1];
				int toChild = toState.children[0];
				Automaton.State fromKeyState = from.states[fromKey];
				if (fromKeyState.kind != K_INT) {
					return fromSign != toSign;
				} else if (!isIntersection(fromValue, fromSign, toChild, toSign)) {
					return false;
				}
			}
			return true;
		} else if(fromKind == K_MAP && toKind == K_SET) {
			if (!fromSign || !toSign) {
				// TO DO: this is a bug here for cases when the element type is e.g. int|real
				int toChild = toState.children[0];
				int toChildKind = to.states[toChild].kind;
				if (toChildKind != K_VOID) {
					return fromSign != toSign;
				}
			}
			return fromSign == toSign;
		} else if ((fromKind == K_SET || fromKind == K_LIST) && toKind == K_STRING) {
			if (!fromSign || !toSign) {
				// TO DO: this is a bug here for cases when the element type is e.g. int|real
				int fromChild = fromState.children[0];
				int fromChildKind = from.states[fromChild].kind;
				if (fromChildKind != K_INT
						&& fromChildKind != K_RATIONAL
						&& fromChildKind != K_ANY) {
					return fromSign != toSign;
				}
			}
			return fromSign == toSign;
		} else {
			// TODO: deal with lists and sets
			return super.isIntersectionInner(fromIndex, fromSign, toIndex, toSign);
		}
	}

	private static boolean primitiveSubtype(int fromKind, int toKind) {
		if (fromKind == K_INT && toKind == K_CHAR) {
			// chars can flow into ints
			return true;
		} else if (fromKind == K_RATIONAL
				&& (toKind == K_INT || toKind == K_CHAR)) {
			// ints or chars can flow into rationals
			return true;
		}
		return false;
	}
}