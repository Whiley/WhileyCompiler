// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyautl_old.lang;

/**
 * <p>
 * An interpretation is used to define the meaning of states in an Automata. In
 * particular, it defines an acceptance relation which characterises precisely
 * which values are accepted by the automata.
 * </p>
 *
 * <p>
 * The primary use of this interface is for testing purposes. As an example, a
 * common kind of test is to check that a minimised automata accepts the same
 * set of values. Likewise, if one automata is thought to subsume another, then
 * is should be the case that all it accepts all of the values accepted by the
 * other.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public interface Interpretation<T> {

	/**
	 * Returns true iff the given automaton accepts the given value.
	 *
	 * @param automaton
	 * @param value
	 * @return
	 */
	public boolean accepts(Automaton automaton, T value);
}
