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

/**
 * An implementation of <code>wyrl.rw.Rewriter</code> which utilises the
 * strategy pattern to support different rule selection heuristics.
 * Specifically, it is parameterised by two strategies which (respectively)
 * control the selection of inference rules and reduction rules.
 * 
 * @author David J. Pearce
 *
 */
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
	 * The inference strategy controls the order in which inference rules are
	 * applied. This has a useful effect on performance, though it is
	 * currently unclear which strategies are best.
	 */
	protected final Strategy<InferenceRule> inferenceStrategy;
	
	/**
	 * The reduction strategy controls the order in which reduction rules are
	 * applied. This has a significant effect on performance, though it is
	 * currently unclear which strategies are best.
	 */
	protected final Strategy<ReductionRule> reductionStrategy;
	
	/**
	 * The automaton being rewritten by this rewriter.
	 */
	protected final Automaton automaton;
	
	/**
	 * This is used to maintain information about which states in the current
	 * automaton are reachable. This is necessary to ensure that rewrites are
	 * not applied to multiple states more than once (as this can cause infinite
	 * loops).
	 */
	protected boolean[] reachable;

	/**
	 * The oneStepUndo provides a mapping from new automaton states to their
	 * original states during a reduction. Using this map, every unreachable
	 * state can be returned to its original form in "one step".
	 */
	protected int[] oneStepUndo;
	
	/**
	 * Construct a simple rewriter for a given automaton which uses the given
	 * strategies for selecting inference and reduction tool.
	 * 
	 * @param automaton
	 *            Automaton to be rewritten
	 * @param inferenceStrategy
	 *            Strategy to use for selecting inference rules
	 * @param reductionStrategy
	 *            Strategy to use for selecting reduction rules
	 * @param schema
	 *            Schema used by automaton, which only used for debugging
	 *            purposes.
	 */
	public StrategyRewriter(Automaton automaton,
			Strategy<InferenceRule> inferenceStrategy,
			Strategy<ReductionRule> reductionStrategy, Schema schema) {
		this.automaton = automaton;
		this.schema = schema;
		this.reachable = new boolean[automaton.nStates() * 2];
		this.oneStepUndo = new int[automaton.nStates() * 2];
		this.inferenceStrategy = inferenceStrategy;
		this.reductionStrategy = reductionStrategy;
	}
	
	@Override
	public final boolean apply(int maxSteps) {
		// First, make sure the automaton is minimised and compacted.
		automaton.minimise();
		automaton.compact();

		// Second, continue to apply inference rules until a fixed point is
		// reached.
		doReduction(Automaton.K_VOID,Automaton.K_VOID,0);

		int step = 0;
		Activation activation;
		
		while (step < maxSteps
				&& (activation = inferenceStrategy.next(reachable)) != null) {
			
			// Apply the activation and see if anything changed.
			int nStates = automaton.nStates();

			// First, apply inference rule activation.
			numInferenceActivations++;
			int target = activation.apply(automaton);
			
			if (target != Automaton.K_VOID) {
				// Yes, inference rule was applied so reduce automaton and check
				// whether any new information generated or not.
				
				if (doReduction(activation.root(),target,nStates)) {
					// Automaton remains different after reduction, hence new
					// information was generated and a fixed point is not yet
					// reached. 
					
					inferenceStrategy.reset();
					
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
	 * reductions are applied only to reachable states (to prevent against the
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
	 * @param from
	 *            Automaton state which was been rewritten from.
	 * @param to
	 *            Automaton state which was been rewritten to.
	 * @return True if the original automaton was not retained (i.e. if some new
	 *         information has been generated).
	 */
	private final boolean doReduction(int from, int to, int pivot) {
		Activation activation;
		
		// Initialise undo information so that each node maps only to itself.
		initialiseUndo();
		
		// Need to update the reachability and undo information here: (1) after
		// a successful inference application; (2) the first time this is called
		// prior to any inference activations.
		updateReachable();
		
		//Apply undo information after the inference application (if applicable);
		if(from != Automaton.K_VOID) {
			applyUndo(from,to,pivot);
		}
		
		// Now, continue applying reductions until no more left.
		while ((activation = reductionStrategy.next(reachable)) != null) {
			// Apply the activation
			numReductionActivations++;
			
			int target = activation.apply(automaton);
			
			if (target != Automaton.K_VOID) {
				// Update reachability status for nodes affected by this
				// activation. This is because such states could cause
				// an infinite loop of re-activations. More specifically, where
				// we activate on a state and rewrite it, but then it remains
				// and so we repeat.
				updateReachable();
				
				// Revert all states below the pivot which are now unreachable.
				// This is essential to ensuring that the automaton will return
				// to its original state iff it is the unchanged.  				
				applyUndo(activation.root(),target,pivot);
				
				// Reset the strategy for the next time we use it.
				reductionStrategy.reset();				
				numReductionSuccesses++;				
			} else {
				// In this case, the activation failed so we simply
				// continue on to try another activation.
				numReductionFailures++;
			}			
		}
		
		reductionStrategy.reset();

		return completeReduction(pivot);
	}
	
	/**
	 * Complete the reduction process. This is the most difficult aspect of the
	 * process because this must efficiently and precisely determine whether or
	 * not the automaton has changed.
	 * 
	 * @param pivot
	 * @return
	 */
	private final boolean completeReduction(int pivot) {
		// Exploit reachability information to determine how many states remain
		// above the pivot, and how many free states there are below the pivot.
		// An invariant is that if countAbove == 0 then countBelow == 0. In the
		// case that no new states remain (i.e. countAbove == 0) then we know
		// the automaton has not changed.

		int countBelow = 0;
		for (int i = 0; i != pivot; ++i) {
			if (reachable[i]) {
				countBelow++;
			}
		}
		int countAbove = 0;
		for (int i = pivot; i != automaton.nStates(); ++i) {
			if (reachable[i]) {
				countAbove++;
			}
		}

		// Finally, determine whether the automaton has actually changed or not.
		if (countAbove == 0 && countBelow == pivot) {
			// Indicates no reachable states remain above the pivot and, hence, the
			// automaton has not changed. We must now eliminate these states to
			// ensure the automaton remains identical as before.			
			automaton.resize(pivot); 			
			return false;
		} else {
			// Otherwise, the automaton has definitely changed. Therefore, we
			// compact the automaton down by eliminating all unreachable states.	
			compact(automaton,reachable);			
			return true;
		}
	}

	/**
	 * Update the reachability information associated with the automaton after
	 * some change has occurred. This information is currently recomputed from
	 * scratch, though in principle it could be updated incrementally.
	 */
	private void updateReachable() {
		
		// TODO: update reachability information incrementally
		
		if (reachable.length < automaton.nStates()) {
			reachable = new boolean[automaton.nStates() * 2];
		} else {
			Arrays.fill(reachable,false);
		}
		// first, visit all nodes
		for (int i = 0; i != automaton.nRoots(); ++i) {
			int root = automaton.getRoot(i);
			if (root >= 0) {
				findReachable(automaton, reachable, root);
			}
		}
	}
	
	/**
	 * The purpose of this method is to ensure that states below the pivot which
	 * are now unreachable (if any) are reverted to their original state. This
	 * is import to ensure that the automaton will always return to its original
	 * state iff it is equivalent.
	 * 
	 * @param activation
	 * @param pivot
	 */
	private void applyUndo(int from, int to, int pivot) {
		int nStates = automaton.nStates();
		int oStates = oneStepUndo.length;
		
		// First, ensure enough memory allocated for undo function.
		if (oStates < nStates) {
			int[] tmpUndo = new int[nStates * 2];
			System.arraycopy(oneStepUndo, 0, tmpUndo, 0, oStates);
			for (int i = oStates; i != tmpUndo.length; ++i) {
				tmpUndo[i] = i;
			}
			oneStepUndo = tmpUndo;
		}
		
		// Second, update the oneStepUndo map
		if(to >= 0) {
			oneStepUndo[to] = oneStepUndo[from]; // hmmm, yum.
		}
		
		// Third, apply the oneStepUndo map to all unreachable vertices	
		for(int i=0;i!=pivot;++i) {
			if(!reachable[i]) {
				State state = automaton.get(i);
				if(state != null) {
					state.remap(oneStepUndo);
				}
			}
		}
	}

	/**
	 * Initialise the oneStepUndo map by assigning every state to itself, and
	 * ensuring that enough space was allocated.
	 */
	private void initialiseUndo() {
		int nStates = automaton.nStates();
		if (oneStepUndo.length < nStates) {
			oneStepUndo = new int[nStates * 2];
		}
		for(int i=0;i!=oneStepUndo.length;++i) {
			oneStepUndo[i] = i;
		}
	}
	/**
	 * Visit all states reachable from a given starting state in the given
	 * automaton. In doing this, states which are visited are marked and,
	 * furthermore, those which are "headers" are additionally identified. A
	 * header state is one which is the target of a back-edge in the directed
	 * graph reachable from the start state.
	 * 
	 * @param automaton
	 *            --- automaton to traverse.
	 * @param reachable
	 *            --- states marked with false are those which have not been
	 *            visited.
	 * @param index
	 *            --- state to begin traversal from.
	 * @return
	 */
	public static void findReachable(Automaton automaton, boolean[] reachable, int index) {
		if (index < 0) {
			return;
		} else if(reachable[index]) {
			// Already visited, so terminate here
			return;
		} else {
			// Not previously visited, so mark now and traverse any children
			reachable[index] = true;
			Automaton.State state = automaton.get(index);
			if (state instanceof Automaton.Term) {
				Automaton.Term term = (Automaton.Term) state;
				if (term.contents != Automaton.K_VOID) {
					findReachable(automaton, reachable, term.contents);
				}
			} else if (state instanceof Automaton.Collection) {
				Automaton.Collection compound = (Automaton.Collection) state;
				for (int i = 0; i != compound.size(); ++i) {
					findReachable(automaton, reachable, compound.get(i));
				}
			}
		}
	}
		
	private static void compact(Automaton automaton, boolean[] reachable) {
		int nStates = automaton.nStates();
		int nRoots = automaton.nRoots();
		int[] binding = new int[nStates];
				
		int j=0;
		for(int i=0;i!=nStates;++i) {
			if(reachable[i]) {
				State ith = automaton.get(i);
				binding[i] = j;
				reachable[i] = false;
				reachable[j] = true;
				automaton.set(j++, ith);				
			} 
		}
		
		nStates = j;
		automaton.resize(nStates); // will nullify all deleted states
		
		for(int i=0;i!=nStates;++i) {
			automaton.get(i).remap(binding);
		}
		for (int i = 0; i != nRoots; ++i) {
			int root = automaton.getRoot(i);
			if (root >= 0) {
				automaton.setRoot(i,binding[root]);
			}
		}	
	}
	
	@Override
	public Rewriter.Stats getStats() {
		int numProbes = inferenceStrategy.numProbes()
				+ reductionStrategy.numProbes(); 
		return new Stats(numProbes, numReductionActivations,
				numReductionFailures, numReductionSuccesses,
				numInferenceActivations, numInferenceFailures,
				numInferenceSuccesses);
	}

	@Override
	public void resetStats() {
		this.numReductionActivations = 0;
		this.numReductionFailures = 0;
		this.numReductionSuccesses = 0;
		this.numInferenceActivations = 0;
		this.numInferenceFailures = 0;
		this.numInferenceSuccesses = 0;
	}

	public static abstract class Strategy<T extends RewriteRule> {
		
		/**
		 * Get the next activation according to this strategy, or null if none
		 * available.
		 * 
		 * @return
		 */
		protected abstract Activation next(boolean[] reachable);

		/**
		 * Reset strategy so that all reachable states and rewrite rules will be
		 * considered again.
		 */
		protected abstract void reset();
		
		/**
		 * Return the number of probes performed by this strategy.
		 * 
		 * @return
		 */
		protected abstract int numProbes();
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