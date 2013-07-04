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

import wyautl.core.Automata;
import wyautl.core.Automaton;

/**
 * <p>
 * A naive implementation of <code>RewriteSystem</code> which works correctly,
 * but is not efficient.
 * </p>
 * 
 * <p><b>NOTE:</b> this is not designed to be used in a concurrent setting.</p>
 * 
 * @author David J. Pearce
 * 
 */
public class SimpleRewriter implements RewriteSystem {
	
	/**
	 * The list of available inference rules.
	 */
	private final InferenceRule[] inferences;

	/**
	 * The list of available reduction rules.
	 */
	private final ReductionRule[] reductions;
		
	/**
	 * Temporary space used for the various automata operations.
	 */
	private int[] tmp = null;
		
	public SimpleRewriter(InferenceRule[] inferences, ReductionRule[] reductions) {
		this.inferences = inferences;
		this.reductions = reductions;
	}
	
	public boolean apply(Automaton automaton) {
		// FIXME: obviously, need to do more here!
		return reduce(automaton, 0);
	}
	
	private boolean reduce(Automaton automaton, int start) {
		boolean result = false;
		boolean changed = true;
		if (tmp == null || tmp.length < automaton.nStates() * 2) {
			tmp = new int[automaton.nStates() * 2];
		}
		while (changed) {
			changed = false;
			outer: for (int i = start; i < automaton.nStates(); ++i) {
				if (automaton.get(i) == null) {
					continue;
				}

				for (int j = 0; j != reductions.length; ++j) {
					ReductionRule rr = reductions[j];
					Activation a = rr.probe(automaton, i);
					if (a != null) {
						changed |= a.apply(automaton);
						if (changed) {
							//System.out.println("APPLIED: " + a.rule.getClass().getName());
							break outer;
						}
					}
				}
			}
			if (changed) {
				if (automaton.nStates() > tmp.length) {
					tmp = new int[automaton.nStates() * 2];
				}
				Automata.eliminateUnreachableStates(automaton, start,
						automaton.nStates(), tmp);
				result = true;
			}
		}

		automaton.minimise();
		return result;
	}
}
