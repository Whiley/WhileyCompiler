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
import wyautl.rw.AbstractRewriter.MaxProbesReached;
import wyautl.rw.AbstractRewriter.MinRuleComparator;
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
public class GlobalDispatchRewriter extends AbstractRewriter implements Rewriter {


	/**
	 * The list of available inference rules.
	 */
	private final InferenceRule[] inferences;

	/**
	 * The list of available reduction rules.
	 */
	private final ReductionRule[] reductions;
	
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
	 * that could be applied). The default value is currently 5000000.
	 */
	private int maxProbes;

	public GlobalDispatchRewriter(InferenceRule[] inferences,
			ReductionRule[] reductions, Schema schema) {
		this(inferences, reductions, schema,
				new MinRuleComparator<RewriteRule>(), 5000000);
	}

	public GlobalDispatchRewriter(InferenceRule[] inferences,
			ReductionRule[] reductions, Schema schema, int maxSteps) {
		this(inferences, reductions, schema,
				new MinRuleComparator<RewriteRule>(), maxSteps);
	}
	
	public GlobalDispatchRewriter(InferenceRule[] inferences,
			ReductionRule[] reductions, Schema schema,
			Comparator<RewriteRule> comparator, int maxProbes) {
		super(schema);
		Arrays.sort(inferences, comparator);
		Arrays.sort(reductions, comparator);			
		this.inferences = inferences;
		this.reductions = reductions;
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

				outer: for (int j = 0; j != inferences.length; ++j) {
					InferenceRule ir = inferences[j];
					inferenceWorklist.clear();
					for (int i = 0; i < automaton.nStates(); ++i) {
						Automaton.State state = automaton.get(i);

						// Check whether this state is a term or not (since only
						// term's can be the root of a match).
						if (state instanceof Automaton.Term) {

							if (numProbes++ == maxProbes) {
								throw new MaxProbesReached();
							}
							ir.probe(automaton, i, inferenceWorklist);

							for (int k = 0; k != inferenceWorklist.size(); ++k) {
								Activation activation = inferenceWorklist
										.get(k);

								if (applyInference(automaton, activation)) {

									// In this case, the automaton has changed
									// state
									// and, therefore, all existing activations
									// must
									// be invalidated. To do this, we break out
									// of
									// the outer for-loop and restart the
									// inference
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
			outer: for (int j = 0; j != reductions.length; ++j) {
				ReductionRule rr = reductions[j];
				reductionWorklist.clear();

				for (int i = pivot; i < nStates; ++i) {
					Automaton.State state = automaton.get(i);

					// Check whether this state is a term or not (since only
					// term's
					// can be the root of a match).
					if (state instanceof Automaton.Term) {
						if (numProbes++ == maxProbes) {
							throw new MaxProbesReached();
						}
						rr.probe(automaton, i, reductionWorklist);

						for (int k = 0; k != reductionWorklist.size(); ++k) {
							Activation activation = reductionWorklist.get(k);

							// First, attempt to apply the reduction rule
							// activation.

							if (applyPartialReduction(automaton, pivot,
									activation)) {

								// System.out.println("APPLIED: " +
								// activation.rule.getClass().getName());

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
}
