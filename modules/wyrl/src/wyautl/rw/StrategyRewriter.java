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

public final class StrategyRewriter implements Rewriter {

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

	/**
	 * Implements the particular rewriting strategy that this rewriter will use.
	 */
	protected StrategyRewriter.Strategy strategy;
	
	/**
	 * This is used to maintain information about which states in the current
	 * automaton are reachable. This is necessary to ensure that rewrites are
	 * not applied to multiple states more than once (as this can cause infinite
	 * loops).
	 */
	private int[] reachability = new int[0];

	public StrategyRewriter(Schema schema) {
		this.schema = schema;
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

	@Override
	public boolean apply(Automaton automaton) {
		// First, make sure the automaton is minimised and compacted.
		automaton.minimise();
		automaton.compact();

		// Second, continue to apply inference rules until a fixed point is
		// reached.
		try {
			applyReductions(automaton, 0);

			boolean changed = true;
			while (haveMoreInferences(automaton)) {
				// First, select an inference activation
				Activation activation = selectInference(automaton);
				// Second, apply the activation and see if anything changed.
				changed = applyInference(automaton, activation);
			}

			return true;

		} catch (MaxProbesReached e) {

			// If we get here, then the maximum number of probes was reached
			// before rewriting could complete. Effectively, this is a simple
			// form of timeout.

			return false;
		}
	}

	/**
	 * Apply a given activation of an inference rule onto an automaton during
	 * rewriting. After the activation is applied, the automaton may have
	 * generate a number of new states. These must then be reduced as much as
	 * possible to determine whether or not any new information was introduced
	 * by this activation.
	 * 
	 * @param automaton
	 *            The automaton being reduced.
	 * @param activation
	 *            The inference rule activation to be applied.
	 * @returns True if the activation was successful (i.e. the automaton has
	 *          changed in some way).
	 */
	protected final boolean applyInference(Automaton automaton,
			Activation activation) {
		int nStates = automaton.nStates();

		// First, attempt to apply the inference rule
		// activation.
		numInferenceActivations++;

		if (activation.apply(automaton)) {

			// Yes, the inference rule was applied; now we must
			// try and reduce the automaton as much as possible to check whether
			// any new information was actually generated or not. If we end up
			// with the original automaton, then no new information was
			// inferred.

			if (applyReductions(automaton, nStates)) {

				// In this case, the automaton has changed state
				// and, therefore, all existing activations must
				// be invalidated. 
				
				invalidateActivations();
				
				numInferenceSuccesses++;
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
		}

		return false;
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
	 *            The pivot point for the partial reduction. All states above
	 *            this (including the pivot index itself) are eligible for
	 *            reduction; all those below are not.
	 * @return True if the original automaton was not retained (i.e. if some new
	 *         information has been generated).
	 */
	protected boolean applyReductions(Automaton automaton, int pivot) {
		boolean changed = true;

		while (changed) {
			// First, select the next activation to attempt
			Activation activation = selectReduction(automaton);

			// Second, attempt to apply the activation
			if (applyPartialReduction(automaton, pivot, activation)) {
				// Yes, this activation applied and the automaton has changed
				// somehow.

				// TODO: need to signal down the hierarchy that something has
				// changed.
				invalidateActivations();
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

	public static abstract class Strategy {
		/**
		 * Get the next inference activation, or null if none available.
		 * 
		 * @return
		 */
		public abstract Activation nextInference();
		
		/**
		 * Get the next reduction activation, or null if none available.
		 * 
		 * @return
		 */
		public abstract Activation nextReduction();
				
		/**
		 * 
		 */
		public abstract void invalidateAll();
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

	/**
	 * Signals that a limit on number of permitted probes has been reached. This
	 * is used simply to prevent rewriting from continuing for ever. In other
	 * words, it's a simple form of timeout.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	protected static final class MaxProbesReached extends RuntimeException {

	}
}
