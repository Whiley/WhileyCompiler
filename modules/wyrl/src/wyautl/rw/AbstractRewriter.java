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
import wyautl.rw.Rewriter.Stats;

public abstract class AbstractRewriter implements Rewriter {

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
	public Rewriter.Stats getStats() {
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
	
	/**
	 * This method should be used to apply a given activation of an inference
	 * rule onto an automaton during rewriting.
	 * 
	 * @param automaton
	 *            The automaton being reduced.
	 * @param activation
	 *            The inference rule activation to be applied.
	 * @returns True if the activation was successful (i.e. the automaton has
	 *          changed in some way).
	 */
	protected final boolean applyInference(Automaton automaton, Activation activation) {
		int nStates = automaton.nStates();
		
		// First, attempt to apply the inference rule
		// activation.						
		numActivations++;
		numInferenceActivations++;
		if (activation.apply(automaton)) {

			// Yes, the inference rule was applied; now we must
			// try and reduce the automaton as much as possible to check whether
			// any new information was actually generated or not. If we end up
			// with the original automaton, then no new information was
			// inferred.

			if(doPartialReduction(automaton, nStates)) {

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

	/**
	 * <p>
	 * Reduce the upper states of a given automaton as much as possible. The
	 * pivot point indicates the portion of the automaton which is "new" (i.e.
	 * above the pivot) versus that which is "old" (i.e. below the pivot).
	 * States above the pivot are those which need to be reduced, whilst those
	 * below the pivot are considered to be already fully reduced (and therefore
	 * do not need further reducing).
	 * </p>
	 * 
	 * <p>
	 * This function is used during the application of an inference rule. An
	 * important aspect of this is that the function must indicate whether or
	 * not <i>the original automaton was left after reduction</i>. That is when,
	 * after reduction, all states above the <code>pivot</code> have been
	 * eliminated, but no state below the pivot has. This indicates that the new
	 * states introduced by the inference rule were reduced away leaving an
	 * automaton identical to before the rule was applied. When this happens,
	 * the inference rule has not been successfully applied and we should
	 * continue to search for other rules which can be applied.
	 * </p>
	 * 
	 * <p>
	 * The generally accepted strategy for checking whether the original
	 * automaton remains is as follows: firstly, reductions are only applied to
	 * states above the pivot point (which includes the pivot index itself);
	 * secondly, after a reduction is successfully applied all unreachable
	 * states above the pivot are eliminated (to prevent against the continued
	 * reapplication of a reduction rule); thirdly, when the fixed-point is
	 * reached, the automaton is fully compacted. If during the final
	 * compaction, any state below the pivot becomes unreachable, then the
	 * original automaton was not retained; likewise, if after compaction the
	 * number of states exceeds the pivot, then it was not retained either.
	 * </p>
	 * 
	 * @param automaton
	 *            The automaton to be reduced.
	 * @param pivot
	 *            The pivot point for the partial reduction. All states above
	 *            this (including the pivot index itself) are eligible for
	 *            reduction; all those below are not.
	 * @return True if the original automaton was not retained (i.e. if some new
	 *         information has been generated).
	 */
	protected abstract boolean doPartialReduction(Automaton automaton, int pivot);
	
	/**
	 * This method should be used to apply a given reduce activation onto an
	 * automaton during a partial reduction.
	 * 
	 * @param automaton
	 *            The automaton being reduced.
	 * @param pivot
	 *            The pivot point for the partial reduction. All states above
	 *            this (including the pivot index itself) are eligible for
	 *            reduction; all those below are not.
	 * @param activation
	 *            The reduction rule activation to be applied.
	 * @returns True if the activation was successful (i.e. the automaton has
	 *          changed in some way).
	 */
	protected final boolean applyPartialReduction(Automaton automaton, int pivot, Activation activation) {
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

			Automata.eliminateUnreachableStates(automaton, pivot,
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
	 * Complete the final step of a partial reduction, where the automaton is
	 * compacted down. This operation checks to see whether any state below the
	 * pivot has been eliminated and/or whether there remain states above the
	 * pivot. If either of these hold, then the automaton is considered to have
	 * changed and this function returns <code>true</code>.
	 * 
	 * @param automaton
	 *            The automaton being reduced.
	 * @param pivot
	 *            The pivot point for the partial reduction. All states above
	 *            this (including the pivot index itself) were eligible for
	 *            reduction; all those below were not.
	 * @returns True if the reduction was successful (i.e. the automaton has
	 *          changed in some way).
	 */
	protected final boolean completePartialReduction(Automaton automaton, int pivot) {
		automaton.compact();
		return false;
	}	
	
	/**
	 * Signals that a limit on number of permitted probes has been reached. This
	 * is used simply to prevent rewriting from continuing for ever. In
	 * otherwords, it's a simple form of timeout.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	protected static final class MaxProbesReached extends RuntimeException {

	}	
}
