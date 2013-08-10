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

import java.util.List;

import wyautl.core.Automaton;
import wyrl.core.Pattern;

public interface RewriteRule {
	
	/**
	 * Get the pattern object that describes what this rule will match against.
	 * More specifically, any state which matches this pattern is guaranteed to
	 * produce at least one activation from probing. This is useful for creating
	 * dispatch tables for more efficient probing of automaton states.
	 * 
	 * @return
	 */
	public Pattern.Term pattern();
	
	/**
	 * Probe a given root to see whether or not this rule could be applied to
	 * it. If it can, the corresponding activation record(s) are added to the
	 * list. Note that, under no circumstances is this function permitted to
	 * modify the automaton.
	 * 
	 * @param automaton
	 *            Automaton to probe.
	 * @param root
	 *            State to use as the root for the probe.
	 * @param activations
	 *            List of activations onto which to add any which are discovered
	 *            during the probe.
	 * 
	 * @return
	 */
	public void probe(Automaton automaton, int root, List<Activation> activations);
	
	/**
	 * <p>
	 * Apply this rule to a given automaton using the given continuation state.
	 * The application may or may not actually modify the automaton and this is
	 * indicated by the return value.
	 * </p>
	 * <p>
	 * After a <i>succesful</i> rule application, the automaton may in a
	 * different state as before. However, some constraints apply. Whilst new
	 * states may be added to the automaton, states which existed prior to
	 * <code>apply()</code> being called cannot be removed (even if they become
	 * unreachable). This is necessary to ensure that, after the sucessful
	 * application of an inference rules, a partial reduction is guaranteed to
	 * produce an identical automaton (unless new information has been inferred).
	 * </p>
	 * <p>
	 * After an <i>unsuccesful</i> rule application, the automaton should be
	 * left in an identical state as before <code>apply()</code> was called.
	 * This means any temporary states added during <code>apply()</code> must be
	 * removed from the automaton.
	 * </p>
	 * 
	 * @param automaton
	 *            --- The automaton to be rewritten.
	 * @param state
	 *            --- Data required by the rewrite to perform the rewrite. This
	 *            may be null if no such data is required.
	 * @return
	 */
	public boolean apply(Automaton automaton, Object state);
	
	
	/**
	 * Give a lower bound on the number of automaton states that are guaranteed
	 * to be eliminated by this rewrite. This number must be zero if the rule is
	 * conditional since it cannot be determined before activation whether this
	 * rule will successfully apply. This number can be <i>negative</i> in the
	 * case that the rule may actually increase the number of states.
	 * 
	 * @return
	 */
	public int minimum();
	
	/**
	 * Give an upper bound on the number of automaton states that are guaranteed
	 * to be eliminated by this rewrite. This number can be
	 * <code>Integer.MAX_VALUE</code> in the case that an unbounded number of
	 * states may be eliminated.
	 * 
	 * @return
	 */
	public int maximum();
}
