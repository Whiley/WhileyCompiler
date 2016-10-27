// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

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
