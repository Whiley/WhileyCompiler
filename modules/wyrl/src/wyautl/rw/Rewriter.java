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


import wyautl.core.Automaton;

/**
 * Represents the (abstract) mechanism for controlling the rewriting of a given
 * automaton under a given set of rules. Different implementation of this
 * interface are possible, and will have different performance characteristics.
 * 
 * @author David J. Pearce
 * 
 */
public interface Rewriter {
	
	/**
	 * Apply this rewriter to the given automaton, rewriting it as much as
	 * possible. Some implementations of this method may chose to stop rewriting
	 * before all rewrites are performed (e.g. to limit the number of steps
	 * taken). The return value indicates whether or not rewriting was
	 * completed.
	 * 
	 * @param automaton
	 *            --- The automaton to be rewritten.
	 * 
	 * @return --- Indicates whether or not rewriting is complete (true
	 *         indicates it was completed). This is necessary for systems which
	 *         only rewrite upto a given number of steps (e.g. to prevent
	 *         rewriting from continuing too long).
	 */
	public boolean apply(Automaton automaton);
	
	
	/**
	 * Return the statistics accumulated by this rewrite system so far.
	 * 
	 * @return
	 */
	public Stats getStats();
	
	
	/**
	 * Reset the accumulated statistical information to zero.
	 */
	public void resetStats();
	
	/**
	 * Provides useful statistical information on the number of various
	 * operations performed. This is helpful for profiling different
	 * implementations of <code>RewriteSystem</code>.
	 * 
	 * @author David J. Pearce
	 * 
	 */	
	public static class Stats {
		
		/**
		 * Used to count the number of unsuccessful inferences (i.e. those
		 * successful inference rule activations which did not result in a
		 * changed automaton after reduction). This number is not included in
		 * <code>numFailedActivations</code>. 
		 */
		private final int numInferenceFailures;

		/**
		 * Used to count the total number of activations made for inference
		 * rules. This number if included in <code>numActivations</code>.
		 */
		private final int numInferenceActivations;

		/**
		 * Used to count the number of unsuccessful activations (i.e. those which
		 * did not cause a change in the automaton).
		 */
		private final int numActivationFailures;
		
		/**
		 * Used to count the total number of activations made.
		 */
		private final int numActivations;
		
		/**
		 * Counts the total number of activation probes, including those which
		 * didn't generate activations.
		 */
		private final int numProbes;
		
		/**
		 * Construct an object providing statistical information about how a
		 * given rewrite system has performed.
		 * 
		 * @param numProbes
		 * @param numActivations
		 * @param numActivationFailures
		 * @param numInferenceActivations
		 * @param numInferenceFailures
		 */
		public Stats(int numProbes, int numActivations,
				int numActivationFailures, int numInferenceActivations,
				int numInferenceFailures) {
			this.numProbes = numProbes;
			this.numActivations = numActivations;
			this.numActivationFailures = numActivationFailures;
			this.numInferenceActivations = numInferenceActivations;
			this.numInferenceFailures = numInferenceFailures;
		}
		
		/**
		 * Get the total number of activation probes (including those which
		 * didn't generate activations).
		 */
		public int numProbes() {
			return numProbes;
		}
		
		/**
		 * Get the total number of activations (successful or unsuccessful) made.
		 */
		public int numActivations() {
			return numActivations;
		}
		
		/**
		 * Get the total number of activations (successful or unsuccessful) made
		 * for an <i>inference rule</i>.
		 */
		public int numInferenceActivations() {
			return numInferenceActivations;
		}
		
		/**
		 * Get the total number of activations (successful or unsuccessful) made
		 * for a <i>reduction rule</i>.
		 */
		public int numReductionActivations() {
			return numActivations - numInferenceActivations;
		}
		
		/**
		 * Used to count the number of unsuccessful activations (i.e. those which
		 * did not cause a change in the automaton).
		 */
		public int numActivationFailures() {
			return numActivationFailures;
		}
		
		/**
		 * Used to count the number of successful activations (i.e. those which
		 * did not cause a change in the automaton).
		 */
		public int numSuccessfulActivations() {
			return numActivations - numActivationFailures;
		}
		
		/**
		 * Used to count the number of unsuccessful inferences (i.e. those
		 * successful inference rule activations which did not result in a
		 * changed automaton after reduction). <b>NOTE:</b> This number is not
		 * included in <code>numFailedActivations</code>.
		 */
		public int numInferenceFailures() {
			return numActivationFailures;
		}
		
		/**
		 * Return a standard overview of the statistics embodied here.
		 */
		public String toString() {			
			String r = "#activations = " + numActivations + " / " + numProbes;
			r += ", #successful = " + numSuccessfulActivations();
			return r;
		}
	}
}
