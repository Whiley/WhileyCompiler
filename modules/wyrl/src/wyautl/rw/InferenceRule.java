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

package wyautl.rw;

import wyautl.core.Automaton;

/**
 * <p>
 * A rewrite rule which may increase the overall size of the automaton. Such
 * rules are more complex to handle than reduction rules, simply because they
 * can easily lead to infinite rewriting cycles. Such a cycle occurs when an
 * inference rule generates a new fact which is then immediately eliminated via
 * a reduction rule.
 * </p>
 *
 * <p>
 * Inference rules are generally less frequently occuring, and should be
 * considered as more expensive from a performance perspective.  The
 * following illustrates a very simple rule system:
 * </p>
 *
 * <pre>
 * reduce LessThan[Num(int x), Num(int y)]:
 *   => True, if x < y
 *   => False
 *
 * reduce And{Bool b, BExpr... xs}:
 *    => False, if b == False
 *    => True, if |xs| == 0
 *    => And (xs)
 *
 * infer And{LessThan[Expr e1, Expr e2] l1,
 *           LessThan[Expr e3, Expr e4] l2,
 *           BExpr... bs}:
 *    => And (bs ++ LessThan[e1,e4]), if e2 == e3
 * </pre>
 *
 * <p>
 * The key challenge with this system is that, unless inference rules are
 * handled with care, an infinite loop can easily occur. For example, the
 * expression <code>1 < x && x < 2</code> can lead to an infinite loop where
 * <code>1 < 2</code> is continually being infered, and then immediately reduced
 * to <code>true</code> and eliminated from the conjunction.
 * </p>
 * <p>
 * To deal with the issue of infinite loops, the system requires that inferences
 * rules are only considered to have "fired" if, after full reduction, the
 * automaton remains in a different state from before the rule was activated.
 * This requires some considerable care to implement correctly.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public interface InferenceRule extends RewriteRule {

}
