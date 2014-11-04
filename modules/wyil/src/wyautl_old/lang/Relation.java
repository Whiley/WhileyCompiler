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

package wyautl_old.lang;

/**
 * <p>
 * A relation is used to determining whether a relationship exists between two
 * automata. It provides an interpretation of states in the automata being
 * compared.
 * </p>
 *
 * <p>
 * As an example, consider the common case of <i>subsumption</i>. One automata
 * <code>a1</code> subsumes another automata <code>a2</code> if <code>a1</code>
 * accepts all the values accepted by <code>a2</code> (and possibly more). Then,
 * a state of kind "OR" can subsume a state with the same kind as one of its
 * children.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public interface Relation {

	/**
	 * Get the automaton in the "from" position.
	 *
	 * @return
	 */
	public Automaton from();

	/**
	 * Get the automaton in the "to" position.
	 *
	 * @return
	 */
	public Automaton to();

	/**
	 * Check whether a node in the <code>from</code> automaton, and a node in the
	 * <code>to</code> automaton are related or not.
	 *
	 * @param from
	 *            --- An index into automaton <code>from</code>.
	 * @param to
	 *            --- An index into automaton <code>to</code>.
	 * @return
	 */
	public boolean isRelated(int from, int to);

	/**
	 * <p>
	 * Recalculate the relationship status between a node in the
	 * <code>from</code> automaton, and a node in the <code>to</code> automaton.
	 * </p>
	 *
	 * @param from
	 *            --- An index into automaton <code>from</code>.
	 * @param to
	 *            --- An index into automaton <code>to</code>.
	 * @return --- true if their status changed, false otherwise.
	 */
	public boolean update(int from, int to);
}
