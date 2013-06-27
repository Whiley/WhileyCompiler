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

package wyautl.rewrite;

import wyautl.core.Automaton;

/**
 * Represents a rewrite rule which can be applied to an automaton.
 * 
 * @author David J. Pearce 
 *
 */
public interface RewriteRule {
	
	/**
	 * Indicates whether this rewrite rule is an inference or a reduction.
	 * 
	 * @return
	 */
	public boolean isInference();
	
	/**
	 * Indicates the number of parameters for this rule.
	 * 
	 * @return
	 */
	public int numParameters();
	
	/**
	 * Probe a given root to see whether or not this rule could be applied to
	 * it. If it can, a corresponding activation record is returned; otherwise,
	 * <code>null</code> is returned indicating no application was possible.
	 * 
	 * @param automaton
	 *            --- automaton to probe.
	 * @param root
	 *            --- state to use as the root for the probe.
	 * @return
	 */
	public Activation probe(Automaton automaton, int root);
	
	/**
	 * <p>
	 * Apply this rule to a given automaton using the give parameters. Note that
	 * <code>parameters.length == numParameters()</code> must hold. The
	 * application may or may not actually modify the automaton and this is
	 * indicates by the return value.
	 * </p>
	 * 
	 * <p>
	 * <b>NOTE:</b> reductions always modify the automaton; however, inferences
	 * may or may not.
	 * </p>
	 * 
	 * @param automaton
	 *            --- the automaton to be rewritten.
	 * @param parameters
	 *            --- states in the automaton which map to variables in the
	 *            rewrite rule.
	 * @return
	 */
	public boolean apply(Automaton automaton, int... parameters);
}
