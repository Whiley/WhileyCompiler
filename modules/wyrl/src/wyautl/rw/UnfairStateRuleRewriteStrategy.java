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
public final class UnfairStateRuleRewriteStrategy<T extends RewriteRule> extends IterativeRewriter.Strategy<T> {

	/**
	 * The static dispatch table
	 */
	private final RewriteRule[][] dispatchTable;

	/**
	 * Temporary list of inference activations used.
	 */
	private final ArrayList<Activation> worklist = new ArrayList<Activation>();

	/**
	 * The automaton being rewritten
	 */
	private final Automaton automaton;

	/**
	 * The current state being explored by this strategy
	 */
	private int current;

	/**
	 * Record the number of probes for statistical reporting purposes
	 */
	private int numProbes;

	public UnfairStateRuleRewriteStrategy(Automaton automaton, T[] rules, Schema schema) {
		this(automaton, rules, schema,new RewriteRule.RankComparator());
	}

	public UnfairStateRuleRewriteStrategy(Automaton automaton, T[] rules,
			Schema schema, Comparator<RewriteRule> comparator) {
		this.automaton = automaton;
		this.dispatchTable = constructDispatchTable(rules,schema,comparator);
	}

	@Override
	protected Activation next(boolean[] reachable) {
		int nStates = automaton.nStates();

		while (current < nStates && worklist.size() == 0) {
			// Check whether state is reachable and that it's a term. This is
			// because only reachable states should be rewritten; and, only
			// terms can be roots of rewrite rules.
			if (reachable[current]) {
				Automaton.State state = automaton.get(current);
				if (state instanceof Automaton.Term) {
					RewriteRule[] rules = dispatchTable[state.kind];
					for (int j = 0; j != rules.length; ++j) {
						RewriteRule rw = rules[j];
						rw.probe(automaton, current, worklist);
						numProbes++;
					}
				}
			}
			current = current + 1;
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
		worklist.clear();
		current = 0;
	}

	@Override
	public int numProbes() {
		return numProbes;
	}

	private static RewriteRule[][] constructDispatchTable(RewriteRule[] rules,
			Schema schema, Comparator<RewriteRule> comparator) {
		RewriteRule[][] table = new RewriteRule[schema.size()][];
		for (int i = 0; i != table.length; ++i) {
			Schema.Term term = schema.get(i);
			ArrayList<RewriteRule> tmp = new ArrayList<RewriteRule>();
			for (int j = 0; j != rules.length; ++j) {
				RewriteRule ir = rules[j];
				Pattern.Term pt = ir.pattern();
				if (pt.name.equals(term.name)) {
					tmp.add(ir);
				}
			}
			RewriteRule[] rs = tmp.toArray(new RewriteRule[tmp.size()]);
			Arrays.sort(rs, comparator);
			table[i] = rs;
		}
		return table;
	}
}
