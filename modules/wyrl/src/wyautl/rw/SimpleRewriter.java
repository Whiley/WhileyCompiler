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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import wyautl.core.Automata;
import wyautl.core.Automaton;
import wyautl.core.Schema;
import wyautl.io.PrettyAutomataWriter;

/**
 * <p>
 * A naive implementation of <code>RewriteSystem</code> which works correctly,
 * but is not efficient.
 * </p>
 * 
 * <p>
 * <b>NOTE:</b> this is not designed to be used in a concurrent setting.
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public class SimpleRewriter implements RewriteSystem {

	/**
	 * The list of available inference rules.
	 */
	private final InferenceRule[] inferences;

	/**
	 * The list of available reduction rules.
	 */
	private final ReductionRule[] reductions;

	/**
	 * The schema used by automata being reduced. This is primarily useful for
	 * debugging purposes.
	 */
	private final Schema schema;
	
	/**
	 * Temporary space used for the various automata operations.
	 */
	private int[] tmp = null;

	/**
	 * Used to count the number of unsuccessful inferences (i.e. those
	 * successful inference rule activations which did not result in a
	 * changed automaton after reduction). This number is not included in
	 * <code>numFailedActivations</code>. 
	 */
	private int numInferenceFailures;

	/**
	 * Used to count the total number of activations made for inference
	 * rules. This number if included in <code>numActivations</code>.
	 */
	private int numInferenceActivations;

	/**
	 * Used to count the number of unsuccessful activations (i.e. those which
	 * did not cause a change in the automaton).
	 */
	private int numActivationFailures;
	
	/**
	 * Used to count the total number of activations made.
	 */
	private int numActivations;
	
	/**
	 * Counts the total number of activation probes, including those which
	 * didn't generate activations.
	 */
	private int numProbes;

	/**
	 * Provies a the limit on the number of probes which are permitted during a
	 * single call to <code>apply()</code>. After this point is reached, the
	 * method will return immediately (i.e. even if there are more reductions
	 * that could be applied). The default value is 100000.
	 */
	private int maxProbes = 100000;
	
	public SimpleRewriter(InferenceRule[] inferences, ReductionRule[] reductions, Schema schema) {
		this.inferences = inferences;
		this.reductions = reductions;
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
		ArrayList<Activation> activations = new ArrayList<Activation>();

		// First, reduce the automaton as much as possible before applying any
		// inference rules.
		automaton.minimise();
		automaton.compact();		

		// Second, continue to apply inference rules until a fixed point is
		// reached.
		try {
			boolean changed = true;
			while (changed) {
				changed = reduce(automaton,0);
				outer: for (int i = 0; i < automaton.nStates(); ++i) {
					Automaton.State state = automaton.get(i);

					// Check whether this state is a term or not (since only term's
					// can be the root of a match).
					if (state instanceof Automaton.Term) {

						int nStates = automaton.nStates();
						for (int j = 0; j != inferences.length; ++j) {
							InferenceRule ir = inferences[j];
							activations.clear();	
							if(numProbes++ == maxProbes) { throw new MaxProbesReached(); }							
							ir.probe(automaton, i, activations);

							for (int k = 0; k != activations.size(); ++k) {
								Activation activation = activations.get(k);

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

									reduce(automaton, nStates);

									if (automaton.nStates() != nStates) {

										System.out.println("APPLIED: " + activation.rule.getClass().getName());

										// In this case, the automaton has changed state
										// and, therefore, all existing activations must
										// be invalidated. To do this, we break out of
										// the outer for-loop and restart the inference
										// process from scratch. 							
										changed = true;									
										break outer;

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
							}
						}
					}
				}
			}
		} catch(MaxProbesReached e) {
			return false;
		}
		
		return true;
	}

	/**
	 * Reduce all states above or equal to a given state as much as possible.
	 * All states below the given <code>start</code> state are left untouched.
	 * Crucially, this means that if reduction eliminates all states being
	 * considered then we have left an identical automaton to before.
	 * 
	 * @param automaton
	 * @param start
	 * @return
	 */
	private boolean reduce(Automaton automaton, int start) {
		ArrayList<Activation> activations = new ArrayList<Activation>();

		boolean result = false;
		boolean changed = true;
		if (tmp == null || tmp.length < automaton.nStates() * 2) {
			tmp = new int[automaton.nStates() * 2];
		}

		while (changed) {
			changed = false;
			int nStates = automaton.nStates();
			outer: for (int i = start; i < nStates; ++i) {
				Automaton.State state = automaton.get(i);

				// Check whether this state is a term or not (since only term's
				// can be the root of a match).				
				if (state instanceof Automaton.Term) {	
					
					for (int j = 0; j != reductions.length; ++j) {
						ReductionRule rr = reductions[j];
						activations.clear();

						if(numProbes++ == maxProbes) { throw new MaxProbesReached(); }
						rr.probe(automaton, i, activations);

						for (int k = 0; k != activations.size(); ++k) {						
							Activation activation = activations.get(k);

							// First, attempt to apply the reduction rule
							// activation.

							numActivations++;
							if (activation.apply(automaton)) {

								System.out.println("APPLIED: " + activation.rule.getClass().getName());
								
								// In this case, the automaton has changed state
								// and, therefore, all existing activations must
								// be invalidated. To do this, we break out of
								// the outer for-loop and restart the reduction
								// process from scratch.							
								result = changed = true;
								
								// We also need to eliminate any states which
								// have become unreachable. This is because if
								// such states remain in the automaton, then
								// they will cause an infinite loop of
								// re-activations. More specifically, where we
								// activate on a state and rewrite it, but then
								// it remains and so we repeat.								
								if (automaton.nStates() > tmp.length) {
									tmp = new int[automaton.nStates() * 2];
								}
								Automata.eliminateUnreachableStates(automaton, start,
										automaton.nStates(), tmp);
								
								break outer;

							} else {

								// In this case, the activation failed so we simply
								// continue on to try another activation. 							
								numActivationFailures++;
							}
						}
					}
				}
			}		
		}

		// Finally, eliminate any null states that have accumulated at the end
		// of the automaton. This is necessary to ensure that, in the case of an
		// identical automaton being (re)produced after an inference, we have
		// *exactly* the same number of states. 
		
		automaton.trim(); 
		
		return result;
	}
	
	private static final class MaxProbesReached extends RuntimeException {
		
	}
}
