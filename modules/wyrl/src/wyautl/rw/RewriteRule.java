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

import java.util.Comparator;
import java.util.List;

import wyautl.core.Automaton;
import wyrl.core.Pattern;

public interface RewriteRule {

	/**
	 * Get the name associated with this rule. This has no semantic meaning, but
	 * is useful for debugging and constructing a proof tree.
	 *
	 * @return
	 */
	public String name();

	/**
	 * Return the rank associated with this rewrite rule. This is a measure of
	 * how this rule should be prioritised during rewriting, with zero being
	 * the highest priority.
	 *
	 * @return
	 */
	public int rank();

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
	 * After a <i>successful</i> rule application, the automaton may in a
	 * different state as before. However, some constraints apply. Whilst new
	 * states may be added to the automaton, states which existed prior to
	 * <code>apply()</code> being called cannot be removed (even if they become
	 * unreachable). This is necessary to ensure that, after the sucessful
	 * application of an inference rules, a partial reduction is guaranteed to
	 * produce an identical automaton (unless new information has been
	 * inferred).
	 * </p>
	 * <p>
	 * After an <i>unsuccessful</i> rule application, the automaton should be
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
	 * @param binding
	 *            --- Returns a mapping from states before the rewrite to states
	 *            after the rewrite. This must at least as big as the automaton.
	 *
	 * @return The state that was rewritten to. Using this, and state[0], you
	 *         can determine which state was rewritten from, and which was
	 *         rewritten to. In the case of an unsuccessful rewrite, then K_Void
	 *         is returned (-1).
	 */
	public int apply(Automaton automaton, int[] state);

	/**
	 * A standard comparator for comparing rewrite rules based on their rank.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class RankComparator
			implements Comparator<RewriteRule> {

		@Override
		public int compare(RewriteRule o1, RewriteRule o2) {
			int r1_rank = o1.rank();
			int r2_rank = o2.rank();
			if (r1_rank < r2_rank) {
				return -1;
			} else if (r1_rank > r2_rank) {
				return 1;
			}

			return 0;
		}
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
	public static final class MinComparator<T extends RewriteRule>
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
	public static final class MaxComparator<T extends RewriteRule>
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
