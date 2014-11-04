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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import wyautl.core.Automaton;
import wyautl.core.Schema;
import wyrl.core.Pattern;

/**
 * <p>
 * A simple implementation of <code>StrategyRewriter.Strategy</code> which aims
 * to be more efficient that <code>SimpleRewriter</code>. Specifically, it
 * attempts to cut down the number of probes by using a <i>static dispatch
 * table</i>. This table is precomputed when the rewriter is constructed, and
 * maps every automaton state kind to the list of rules which could potentially
 * match that kind.
 * </p>
 *
 * <p>
 * <b>NOTE:</b> this is not designed to be used in a concurrent setting.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public final class FairStateRuleRewriteStrategy<T extends RewriteRule> extends IterativeRewriter.Strategy<T> {

	/**
	 * The static dispatch table
	 */
	private final RewriteRule[] rules;

	/**
	 * Temporary list of inference activations used.
	 */
	private final ArrayList<Activation> worklist = new ArrayList<Activation>();

	/**
	 * The automaton being rewritten
	 */
	private final Automaton automaton;

	/**
	 * Starting state on the current round.
	 */
	private int startStep;

	/**
	 * The current state being explored by this strategy
	 */
	private int currentStep;

	/**
	 * Record the number of probes for statistical reporting purposes
	 */
	private int numProbes;

	public FairStateRuleRewriteStrategy(Automaton automaton, T[] rules, Schema schema) {
		this(automaton, rules, schema,new RewriteRule.RankComparator());
	}

	public FairStateRuleRewriteStrategy(Automaton automaton, T[] rules,
			Schema schema, Comparator<RewriteRule> comparator) {
		this.automaton = automaton;
		this.rules = Arrays.copyOf(rules,rules.length);
		Arrays.sort(this.rules,comparator);
		this.startStep = Math.max(0,(automaton.nStates() * rules.length) - 1);
	}

	@Override
	protected Activation next(boolean[] reachable) {
		int nStates = automaton.nStates();
		int maxStep = nStates * rules.length;

		while (currentStep != startStep && worklist.size() == 0) {
			int stateRef = currentStep / rules.length;
			int rule = currentStep % rules.length;

			if (reachable[stateRef]) {
				Automaton.State state = automaton.get(stateRef);
				if (state instanceof Automaton.Term) {
					rules[rule].probe(automaton, stateRef, worklist);
					numProbes++;
				}
			}
			currentStep = (currentStep + 1) % maxStep;
		}

		if (worklist.size() > 0) {
			int lastIndex = worklist.size() - 1;
			Activation last = worklist.get(lastIndex);
			worklist.remove(lastIndex);
			return last;
		} else {
			return null;
		}
	}

	@Override
	protected void reset() {
		int nStates = automaton.nStates();
		int maxStep = nStates * rules.length;
		worklist.clear();
		if(maxStep == 0) {
			startStep = 0;
			currentStep = 0;
		} else if(currentStep == 0) {
			startStep = maxStep - 1;
		} else {
			currentStep = Math.min(maxStep-1,currentStep);
			startStep = currentStep - 1;
		}
	}

	@Override
	public int numProbes() {
		return numProbes;
	}
}
