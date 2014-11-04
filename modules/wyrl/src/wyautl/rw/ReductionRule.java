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
 * A rewrite rule guaranteed to reduce the Automaton (in some sense). Typically,
 * this means it is guaranteed to reduce the number of states in the automaton
 * by at least one (although it can often be many more). The following
 * illustrates such a rule:
 *
 * <pre>
 * reduce Not(Not(* x)):
 *    => x
 * </pre>
 *
 * This rewrite rule is guaranteed to reduce the automaton by exactly two
 * states. As another example, consider the following:
 *
 * <pre>
 * reduce And{Bool b, BExpr... xs}:
 *    => False, if b == False
 *    => True, if |xs| == 0
 *    => And (xs)
 * </pre>
 *
 * This rewrite rule is guaranteed to reduce the automaton by at least one or
 * more states. However, some rewrite rules do not necessarily reduce the
 * automaton's size. For example, consider the following rule which distributes
 * logical <code>And</code> over logical <code>Or</code>:
 *
 * <pre>
 * reduce And{Or{BExpr... xs}, BExpr... ys}:
 *    => let ys = { And(x ++ ys) | x in xs }
 *       in Or(ys)
 * </pre>
 *
 * Observe that this rule may <i>increase</i> the overall number of states in
 * the automaton. For example, the logical expresion <code>X && (Y || Z)</code>
 * becomes <code>(X && Y) || (X && Z)</code>, which contains one additional
 * state. However, observe also that this rule cannot be applied indefinitely
 * and, for this reason, is considered to "reduce" the automaton (provided there
 * is no other rule which can "undo" what this rule does, leading to an infinite
 * rewrite cycle).
 *
 * @author David J. Pearce
 *
 */
public interface ReductionRule extends RewriteRule {

}
