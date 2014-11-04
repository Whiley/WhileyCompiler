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
	 * Apply this rewriter to rewrite its automaton as much as possible within a
	 * given number of steps. The return value indicates whether or not
	 * rewriting was completed.
	 *
	 * @return --- Indicates whether or not rewriting is complete (true
	 *         indicates it was completed). This is necessary to distinguish
	 *         when the maximum number of steps was reached.
	 */
	public boolean apply();


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
		 * changed automaton after reduction).
		 */
		private final int numInferenceFailures;

		/**
		 * Used to count the number of successful inferences (i.e. those
		 * successful inference rule activations which did result in a
		 * changed automaton after reduction).
		 */
		private final int numInferenceSuccesses;

		/**
		 * Used to count the total number of activations made for inference
		 * rules.
		 */
		private final int numInferenceActivations;

		/**
		 * Used to count the number of unsuccessful reductions (i.e. those
		 * successful reduction rule activations which did not result in a
		 * changed automaton after reduction).
		 */
		private final int numReductionFailures;

		/**
		 * Used to count the number of successful reductions (i.e. those
		 * successful reduction rule activations which did result in a
		 * changed automaton after reduction).
		 */
		private final int numReductionSuccesses;

		/**
		 * Used to count the total number of activations made for reduction
		 * rules.
		 */
		private final int numReductionActivations;


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
		public Stats(int numProbes, int numReductionActivations,
				int numReductionFailures, int numReductionSuccesses,
				int numInferenceActivations, int numInferenceFailures,
				int numInferenceSuccesses) {
			this.numProbes = numProbes;

			this.numReductionActivations = numReductionActivations;
			this.numReductionFailures = numReductionFailures;
			this.numReductionSuccesses = numReductionSuccesses;
			this.numInferenceActivations = numInferenceActivations;
			this.numInferenceFailures = numInferenceFailures;
			this.numInferenceSuccesses = numInferenceSuccesses;
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
			return numReductionActivations + numInferenceActivations;
		}

		/**
		 * Get the total number of activations (successful or unsuccessful) made
		 * for a <i>reduction rule</i>.
		 */
		public int numReductionActivations() {
			return numReductionActivations;
		}

		/**
		 * Used to count the number of unsuccessful reductions (i.e. those
		 * successful reduction rule activations which did not result in a
		 * changed automaton after reduction).
		 */
		public int numReductionFailures() {
			return numInferenceFailures;
		}

		/**
		 * Used to count the number of successful reductions (i.e. those
		 * successful reduction rule activations which did result in a
		 * changed automaton after reduction).
		 */
		public int numReductionSuccesses() {
			return numReductionSuccesses;
		}

		/**
		 * Get the total number of activations (successful or unsuccessful) made
		 * for an <i>inference rule</i>.
		 */
		public int numInferenceActivations() {
			return numInferenceActivations;
		}

		/**
		 * Used to count the number of unsuccessful inferences (i.e. those
		 * successful inference rule activations which did not result in a
		 * changed automaton after reduction).
		 */
		public int numInferenceFailures() {
			return numInferenceFailures;
		}

		/**
		 * Used to count the number of successful inferences (i.e. those
		 * successful inference rule activations which did result in a
		 * changed automaton after reduction).
		 */
		public int numInferenceSuccesses() {
			return numInferenceSuccesses;
		}

		/**
		 * Return a standard overview of the statistics embodied here.
		 */
		public String toString() {
			String r = "#activations = " + numActivations() + " / " + numProbes;
			r += ", #reductions = " + numReductionSuccesses + " / "
					+ numReductionActivations() + ", #inferences "
					+ numInferenceSuccesses() + " / "
					+ numInferenceActivations();
			return r;
		}
	}
}
