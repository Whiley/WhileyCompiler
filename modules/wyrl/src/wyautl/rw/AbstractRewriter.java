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
import wyautl.core.Schema;
import wyautl.rw.RewriteSystem.Stats;

public abstract class AbstractRewriter implements RewriteSystem {

	/**
	 * The schema used by automata being reduced. This is primarily useful for
	 * debugging purposes.
	 */
	protected final Schema schema;

	/**
	 * Used to count the number of unsuccessful inferences (i.e. those
	 * successful inference rule activations which did not result in a
	 * changed automaton after reduction). This number is not included in
	 * <code>numFailedActivations</code>. 
	 */
	protected int numInferenceFailures;

	/**
	 * Used to count the total number of activations made for inference
	 * rules. This number if included in <code>numActivations</code>.
	 */
	protected int numInferenceActivations;

	/**
	 * Used to count the number of unsuccessful activations (i.e. those which
	 * did not cause a change in the automaton).
	 */
	protected int numActivationFailures;
	
	/**
	 * Used to count the total number of activations made.
	 */
	protected int numActivations;
	
	/**
	 * Counts the total number of activation probes, including those which
	 * didn't generate activations.
	 */
	protected int numProbes;

	/**
	 * Temporary space used for the various automata operations.
	 */
	private int[] tmp = new int[0];
	
	public AbstractRewriter(Schema schema) {
		this.schema = schema;		
	}	
		
	@Override
	public RewriteSystem.Stats getStats() {
		return new Stats(numProbes, numActivations, numActivationFailures,
				numInferenceActivations, numInferenceFailures);
	}

	@Override
	public void resetStats() {
		this.numProbes = 0;
		this.numActivations = 0;
		this.numActivationFailures = 0;
		this.numInferenceActivations = 0;		
	}
	
	protected final boolean applyInference(Automaton automaton, Activation activation) {
		int nStates = automaton.nStates();
		
		// First, attempt to apply the inference rule
		// activation.						
		numActivations++;
		numInferenceActivations++;
		if (activation.apply(automaton)) {

			// Yes, the inference rule was applied; now we must
			// try and reduce the automaton to its canonical
			// state to check whether any new information was
			// actually generated or not. If we end up with the
			// original automaton, then no new information was
			// inferred.

			doPartialReduction(automaton, nStates);

			// TODO: get rid of the need for the original automaton
			
			if (automaton.nStates() != nStates || !automaton.equals(original)) {

				// In this case, the automaton has changed state
				// and, therefore, all existing activations must
				// be invalidated. To do this, we break out of
				// the outer for-loop and restart the inference
				// process from scratch. 							
				return true;

			} else {
				// In this case, the automaton has not changed
				// state after reduction and, therefore, we
				// consider this activation to have failed.								
				numInferenceFailures++;
			}
		} else {	

			// In this case, the activation failed so we simply
			// continue on to try another activation. 							
			numActivationFailures++;
		}
		
		return false;
	}

	protected abstract boolean doPartialReduction(Automaton automaton, int start);
	
	/**
	 * This method should be used to apply a given activation onto an automaton
	 * during a partial reduction.
	 * 
	 * @param automaton
	 * @param start
	 *            The starting index for the partial reduction.
	 * @param activation
	 *            The activation to be applied.
	 * @returns True if the activation was successful (i.e. the automaton has
	 *          changed in some way).
	 */
	protected final boolean applyReduction(Automaton automaton, int start, Activation activation) {
		numActivations++;
		
		if(activation.apply(automaton)) {
			
			// We need to eliminate any states added during the activation which
			// have become unreachable. This is because if such states remain in
			// the automaton, then they will cause an infinite loop of
			// re-activations. More specifically, where we activate on a state
			// and rewrite it, but then it remains and so we repeat.								

			if (automaton.nStates() > tmp.length) {
				tmp = new int[automaton.nStates() * 2];
			}

			Automata.eliminateUnreachableStates(automaton, start,
					automaton.nStates(), tmp);	
			
			return true;
		} else {
			
			// In this case, the activation failed so we simply
			// continue on to try another activation. 							
			numActivationFailures++;
			
			return false;
		}
	}
	

	/**
	 * Eliminate all unreachable states, and then compact the automaton down.  
	 * 
	 * @param automaton
	 * @param start
	 */
	protected final boolean completeReduction(Automaton automaton, int start) {
		automaton.compact();
		return false;
	}	
	
	protected static final class MaxProbesReached extends RuntimeException {

	}	
}
