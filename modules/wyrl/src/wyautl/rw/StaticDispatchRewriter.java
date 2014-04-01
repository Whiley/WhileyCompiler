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

import wyautl.core.Automata;
import wyautl.core.Automaton;
import wyautl.core.Schema;
import wyrl.core.Pattern;

/**
 * <p>
 * A simple implementation of <code>RewriteSystem</code> which aims to be more
 * efficient that <code>SimpleRewriter</code>. Specifically, it attempts to cut
 * down the number of probes by using a <i>static dispatch table</i>. This table
 * is precomputed when the rewriter is constructed, and maps every automaton
 * state kind to the list of rules which could potentially match that kind.
 * </p>
 * 
 * <p>
 * <b>NOTE:</b> this is not designed to be used in a concurrent setting.
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public class StaticDispatchRewriter extends AbstractRewriter implements Rewriter {

	/**
	 * The dispatch table for inference rules.
	 */
	private final RewriteRule[][] inferenceDispatchTable;

	/**
	 * The list of available reduction rules.
	 */
	private final RewriteRule[][] reductionDispatchTable;

	/**
	 * Temporary list of reduction activations used.
	 */
	private final ArrayList<Activation> reductionWorklist = new ArrayList<Activation>();
	
	/**
	 * Temporary list of inference activations used.
	 */	
	private final ArrayList<Activation> inferenceWorklist = new ArrayList<Activation>();
		
	/**
	 * Provies a the limit on the number of probes which are permitted during a
	 * single call to <code>apply()</code>. After this point is reached, the
	 * method will return immediately (i.e. even if there are more reductions
	 * that could be applied). The default value is currently 10000.
	 */
	private int maxProbes;
	
	public StaticDispatchRewriter(InferenceRule[] inferences,
			ReductionRule[] reductions, Schema schema) {
		this(inferences, reductions, schema,
				new MinRuleComparator<RewriteRule>(), 100000);
	}

	public StaticDispatchRewriter(InferenceRule[] inferences,
			ReductionRule[] reductions, Schema schema,
			Comparator<RewriteRule> comparator) {
		this(inferences, reductions, schema, comparator, 100000);
	}

	public StaticDispatchRewriter(InferenceRule[] inferences,
			ReductionRule[] reductions, Schema schema, int maxProbes) {
		this(inferences, reductions, schema,
				new MinRuleComparator<RewriteRule>(), maxProbes);
	}

	public StaticDispatchRewriter(InferenceRule[] inferences,
			ReductionRule[] reductions, Schema schema,
			Comparator<RewriteRule> comparator, int maxProbes) {
		super(schema);
		this.inferenceDispatchTable = constructDispatchTable(inferences,
				schema, comparator);
		this.reductionDispatchTable = constructDispatchTable(reductions,
				schema, comparator);
		this.maxProbes = maxProbes;
	}
	
	/**
	 * Set the limit on the number of probes which are permitted during a single
	 * call to <code>apply()</code>. After this point is reached, the method
	 * will return immediately (i.e. even if there are more reductions that
	 * could be applied).
	 */	
	public void setMaxProbes(int maxProbes) {
		this.maxProbes = maxProbes;
	}
	
	@Override
	public boolean apply(Automaton automaton) {

		// First, make sure the automaton is minimised and compacted.
		
		automaton.minimise();
		automaton.compact();

		// Second, continue to apply inference rules until a fixed point is
		// reached.
		try {
			boolean changed = true;
			while (changed) {

				doPartialReduction(automaton, 0);
				changed = false;

				outer: for (int i = 0; i < automaton.nStates(); ++i) {
					Automaton.State state = automaton.get(i);

					// Check whether this state is a term or not (since only
					// term's can be the root of a match).
					if (state instanceof Automaton.Term) {
						RewriteRule[] inferences = inferenceDispatchTable[state.kind];
						for (int j = 0; j != inferences.length; ++j) {
							RewriteRule ir = inferences[j];
							inferenceWorklist.clear();
							if (numProbes++ == maxProbes) {
								throw new MaxProbesReached();
							}
							ir.probe(automaton, i, inferenceWorklist);

							for (int k = 0; k != inferenceWorklist.size(); ++k) {
								Activation activation = inferenceWorklist
										.get(k);
								
								if (applyInference(automaton, activation)) {
									
									// In this case, the automaton has changed state
									// and, therefore, all existing activations must
									// be invalidated. To do this, we break out of
									// the outer for-loop and restart the inference
									// process from scratch.							
									changed = true;
									break outer;
								}								
							}
						}
					}
				}
			}
		} catch (MaxProbesReached e) {
			
			// If we get here, then the maximum number of probes was reached
			// before rewriting could complete. Effectively, this is a simple
			// form of timeout.
			
			return false;
		}

		return true;
	}
	
	@Override
	protected final boolean doPartialReduction(Automaton automaton, int pivot) {			
		boolean changed = true;		

		while (changed) {
			changed = false;
			int nStates = automaton.nStates();
			outer: for (int i = pivot; i < nStates; ++i) {
				Automaton.State state = automaton.get(i);

				// Check whether this state is a term or not (since only term's
				// can be the root of a match).				
				if (state instanceof Automaton.Term) {	

					RewriteRule[] reductions = reductionDispatchTable[state.kind];
					for (int j = 0; j != reductions.length; ++j) {
						RewriteRule rr = reductions[j];
						reductionWorklist.clear();

						if(numProbes++ == maxProbes) { throw new MaxProbesReached(); }
						rr.probe(automaton, i, reductionWorklist);

						for (int k = 0; k != reductionWorklist.size(); ++k) {						
							Activation activation = reductionWorklist.get(k);

							// First, attempt to apply the reduction rule
							// activation.							
							if (applyPartialReduction(automaton,pivot,activation)) {

								// In this case, the automaton has changed state
								// and, therefore, all existing activations must
								// be invalidated. To do this, we break out of
								// the outer for-loop and restart the reduction
								// process from scratch.							
								changed = true;

								break outer;
							} 							
						}
					}
				}
			}
		}		

		// Finally, compact the automaton down by eliminating any unreachable
		// states and compacting the automaton down.
		
		return completePartialReduction(automaton,pivot); 
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
