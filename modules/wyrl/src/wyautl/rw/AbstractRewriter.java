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

import java.util.Arrays;
import java.util.Comparator;

import wyautl.core.Automata;
import wyautl.core.Automaton;
import wyautl.core.Schema;
import wyautl.core.Automaton.State;

public abstract class AbstractRewriter implements Rewriter {

	/**
	 * The schema used by automata being reduced. This is primarily useful for
	 * debugging purposes.
	 */
	protected final Schema schema;

	/**
	 * Used to count the number of unsuccessful inferences (i.e. those
	 * successful inference rule activations which did not result in a changed
	 * automaton after reduction).
	 */
	private int numInferenceFailures;

	/**
	 * Used to count the number of successful inferences (i.e. those successful
	 * inference rule activations which did result in a changed automaton after
	 * reduction).
	 */
	private int numInferenceSuccesses;

	/**
	 * Used to count the total number of activations made for inference rules.
	 */
	private int numInferenceActivations;

	/**
	 * Used to count the number of unsuccessful reductions (i.e. those
	 * successful reduction rule activations which did not result in a changed
	 * automaton after reduction).
	 */
	private int numReductionFailures;

	/**
	 * Used to count the number of successful reductions (i.e. those successful
	 * reduction rule activations which did result in a changed automaton after
	 * reduction).
	 */
	private int numReductionSuccesses;

	/**
	 * Used to count the total number of activations made for reduction rules.
	 */
	private int numReductionActivations;

	/**
	 * Counts the total number of activation probes, including those which
	 * didn't generate activations.
	 */
	protected int numProbes;

	protected final Strategy inferenceStrategy;
	
	protected final Strategy reductionStrategy;
	
	protected final Automaton automaton;
	
	/**
	 * This is used to maintain information about which states in the current
	 * automaton are reachable. This is necessary to ensure that rewrites are
	 * not applied to multiple states more than once (as this can cause infinite
	 * loops).
	 */
	private int[] reachability = new int[0];

	public AbstractRewriter(Automaton automaton, Schema schema) {
		this.automaton = automaton;
		this.schema = schema;
	}
	
	@Override
	public boolean apply(int maxSteps) {
		// First, make sure the automaton is minimised and compacted.
		automaton.minimise();
		automaton.compact();

		// Second, continue to apply inference rules until a fixed point is
		// reached.
		applyReductions(0);

		int step = 0;
		Activation activation;

		while (step < maxSteps
				&& (activation = inferenceStrategy.next()) != null) {
			// Apply the activation and see if anything changed.
			int nStates = automaton.nStates();

			// First, apply inference rule activation.
			numInferenceActivations++;

			if (activation.apply(automaton)) {
				// Yes, inference rule was applied so reduce automaton and check
				// whether any new information generated or not.

				if (applyReductions(nStates)) {
					// Automaton remains different after reduction, hence new
					// information was generated and a fixed point is not yet
					// reached. 
					
					inferenceStrategy.invalidate();
					
					numInferenceSuccesses++;
				} else {
					// Automaton has not changed after reduction, so we
					// consider this activation to have failed.
					numInferenceFailures++;
				}
			} 
			
			step = step + 1;
		}

		if(step == maxSteps) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * <p>
	 * Reduce the states of a given automaton as much as possible. The pivot
	 * point indicates the portion of the automaton which is "new" (i.e. above
	 * the pivot) versus that which is "old" (i.e. below the pivot). States
	 * above the pivot are those which must be reduced, whilst those below the
	 * pivot are (mostly) already fully reduced (and therefore do not need
	 * further reducing). However, there may be states in the old region which
	 * have changed (e.g. after an <code>Automaton.rewrite()</code> has been
	 * applied) and should be reduce. See #382 for more on this process.
	 * </p>
	 * 
	 * <p>
	 * This function is used primarily during the application of an inference
	 * rule. An important aspect of this is that the function must indicate
	 * whether or not <i>the original automaton was left after reduction</i>.
	 * That is when, after reduction, all states above the <code>pivot</code>
	 * have been eliminated, but no state below the pivot has. This indicates
	 * that the new states introduced by the inference rule were reduced away
	 * leaving an automaton identical to before the rule was applied. When this
	 * happens, the inference rule has not been successfully applied and we
	 * should continue to search for other rules which can be applied.
	 * </p>
	 * 
	 * <p>
	 * The generally accepted strategy for checking whether the original
	 * automaton remains is as follows: firstly, reductions are applied to all
	 * states, but particularly those above the pivot point; secondly,
	 * reductions are applied only to reachable state (to prevent against the
	 * continued reapplication of a reduction rule); thirdly, when the
	 * fixed-point is reached, the automaton is fully compacted. If during the
	 * final compaction, any state below the pivot becomes unreachable, then the
	 * original automaton was not retained; likewise, if after compaction the
	 * number of states exceeds the pivot, then it was not retained either.
	 * </p>
	 * 
	 * @param automaton
	 *            The automaton to be reduced.
	 * @param pivot
	 *            The pivot point for the reduction. All states above this
	 *            (including the pivot index itself) are eligible for reduction;
	 *            all those below are not.
	 * @return True if the original automaton was not retained (i.e. if some new
	 *         information has been generated).
	 */
	protected boolean applyReductions(int pivot) {
		Activation activation;
		
		while ((activation = reductionStrategy.next()) != null) {
			// Apply the activation
			if (applyPartialReduction(automaton, pivot, activation)) {
				// Yes, this activation applied and the automaton has changed
				// somehow.

				// TODO: need to signal down the hierarchy that something has
				// changed.
				reductionStrategy.invalidate();
			}
		}

		// Exploit reachability information to determine how many states remain
		// above the pivot, and how many free states there are below the pivot.
		// An invariant is that if countAbove == 0 then countBelow == 0. In the
		// case that no new states remain (i.e. countAbove == 0) then we know
		// the automaton has not changed.

		int countBelow = 0;
		for (int i = 0; i != pivot; ++i) {
			if (reachability[i] == 0) {
				countBelow++;
			}
		}
		int countAbove = 0;
		for (int i = pivot; i != automaton.nStates(); ++i) {
			if (reachability[i] != 0) {
				countAbove++;
			}
		}

		// Finally, determine whether the automaton has actually changed or not.
		
		if (countAbove == 0) {
			// Indicates no states remain above the pivot and, hence, the
			// automaton has not changed.
			return false;
		} else if (countAbove == countBelow) {
			// Here, there is a chance that the automaton is still equivalent
			// and we must now aggressively determine whether or not this is the
			// case.
		} 
		
		// Otherwise, the automaton has definitely changed. Therefore, we
		// compact the automaton down by eliminating all unreachable states.

		int nStates = automaton.nStates();

		if (nStates > reachability.length) {
			// multiply by 2 to amortize cost of reallocating this array.
			reachability = new int[nStates * 2];
		}

		// TODO: can optimise this by inlining and eliminating reachability
		// search.

		automaton.compact(reachability);
	}

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
	protected final boolean applyPartialReduction(Automaton automaton,
			int pivot, Activation activation) {
		numReductionActivations++;

		if (activation.apply(automaton)) {

			// We need to identify any states added during the activation which
			// have become unreachable. This is because such states could cause
			// an infinite loop of re-activations. More specifically, where we
			// activate on a state and rewrite it, but then it remains and so
			// we repeat.

			if (automaton.nStates() > reachability.length) {
				// Insufficient space in the current reachability array
				reachability = new int[automaton.nStates() * 2];
			} else {
				// Clear the reachability array

				// TODO: incrementally maintain the reachability array.

				Arrays.fill(reachability, 0);
			}

			// Visit all states reachable from a root to update the
			// reachability information.
			for (int i = 0; i != automaton.nRoots(); ++i) {
				int root = automaton.getRoot(i);
				if (root >= 0) {
					Automata.traverse(automaton, root, reachability);
				}
			}

			numReductionSuccesses++;
			return true;
		} else {

			// In this case, the activation failed so we simply
			// continue on to try another activation.
			numReductionFailures++;
			return false;
		}
	}


	@Override
	public Rewriter.Stats getStats() {
		return new Stats(numProbes, numReductionActivations,
				numReductionFailures, numReductionSuccesses,
				numInferenceActivations, numInferenceFailures,
				numInferenceSuccesses);
	}

	@Override
	public void resetStats() {
		this.numProbes = 0;
		this.numReductionActivations = 0;
		this.numReductionFailures = 0;
		this.numReductionSuccesses = 0;
		this.numInferenceActivations = 0;
		this.numInferenceFailures = 0;
		this.numInferenceSuccesses = 0;
	}

	public abstract class Strategy {
		/**
		 * Get the next activation according to this strategy, or null if none
		 * available.
		 * 
		 * @return
		 */
		protected abstract Activation next(boolean[] reachable);

		/**
		 * Invalidates all states ?
		 */
		protected abstract void invalidate();
	}
	
	/**
	 * A standard comparator for comparing rewrite rules. This favours minimum
	 * guarantees over maximum pay off. That is, a rule with a minimum / maximum
	 * guarantee of <code>1 / 1</code> will be favoured over a rule with a
	 * guarantee of <code>0 / 10</code>. The latter has a greater potential
	 * payoff, but a lower minimum payoff.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class MinRuleComparator<T extends RewriteRule>
			implements Comparator<T> {

		@Override
		public int compare(T o1, T o2) {
			int r1_minimum = o1.minimum();
			int r2_minimum = o2.minimum();
			if (r1_minimum > r2_minimum) {
				return -1;
			} else if (r1_minimum < r2_minimum) {
				return 1;
			}

			int r1_maximum = o1.maximum();
			int r2_maximum = o2.maximum();
			if (r1_maximum > r2_maximum) {
				return -1;
			} else if (r1_maximum < r2_maximum) {
				return 1;
			}

			return 0;
		}

	}

	/**
	 * A standard comparator for comparing rewrite rules. This favours maximum
	 * opportunity over guaranteed minimum pay off. That is, a rule with a
	 * minimum / maximum guarantee of <code>0 / 10</code> will be favoured over
	 * a rule with a guarantee of <code>0 / 1</code>. The former has a greater
	 * potential payoff, but a lower minimum payoff.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class MaxRuleComparator<T extends RewriteRule>
			implements Comparator<T> {

		@Override
		public int compare(T o1, T o2) {
			int r1_minimum = o1.minimum();
			int r2_minimum = o2.minimum();
			if (r1_minimum < r2_minimum) {
				return -1;
			} else if (r1_minimum > r2_minimum) {
				return 1;
			}

			int r1_maximum = o1.maximum();
			int r2_maximum = o2.maximum();
			if (r1_maximum < r2_maximum) {
				return -1;
			} else if (r1_maximum > r2_maximum) {
				return 1;
			}

			return 0;
		}

	}
}
