package wyautl.core;

/**
 * Contains various helper functions for working with automata.
 * 
 * @author David J. Pearce
 * 
 */
public class Automata {
	
	/**
	 * Visit all nodes reachable from the given node in a given automaton.
	 * 
	 * @param automaton
	 *            --- automaton to traverse.
	 * @param node
	 *            --- root index to begin copying from.
	 * @param visited
	 *            --- initially, unvisited states are marked with '0' which
	 *            subsequently turns positive to indicate they were visited. For
	 *            nodes assigned a value of 1, this indicates they are not the
	 *            header for cycle, whilst those assigned header value > 1 are
	 *            the head of a cycle. Finally, nodes (and their subtress) which
	 *            were initially marked with '-1' are not traversed.
	 * @return
	 */
	public static void findHeaders(Automaton automaton, int node, int[] headers) {
		if (node < 0) {
			return;
		}
		int header = headers[node];
		if (header > 1 || header == Automaton.K_VOID) {
			return; // nothing to do, as either already marked as a header or
					// initially indicated as not to traverse.
		} else if (header == 1) {
			// We have reached a node which was already visited. Therefore, this
			// node is a header and should be marked as such.
			headers[node] = node + 2;
			return; // done
		} else {
			headers[node] = 1;
			Automaton.State state = automaton.get(node);
			if (state instanceof Automaton.Term) {
				Automaton.Term term = (Automaton.Term) state;
				if (term.contents != Automaton.K_VOID) {
					findHeaders(automaton, term.contents, headers);
				}
			} else if (state instanceof Automaton.Collection) {
				Automaton.Collection compound = (Automaton.Collection) state;
				int[] children = compound.children;
				for (int i = 0; i != compound.length; ++i) {
					findHeaders(automaton, children[i], headers);
				}
			}
		}
	}
	
	/**
	 * Check whether one state is reachable from another in a given automaton.
	 * This employs a standard depth-first traversal of the automaton from the
	 * given node. An array of temporary storage is used to record which nodes
	 * have been previously visited in order to protected against infinite
	 * recursion in the presence of cyclic automata.
	 * 
	 * @param start
	 *            --- index to begin the traversal from.
	 * @param storage
	 *            --- temporary storage which must have at least
	 *            <code>automaton.nStates()</code> elements. All states to be
	 *            considered during the traversal must be marked with zero in
	 *            this array. All states visited during the traversal will be
	 *            marked with one afterwards.
	 * @return
	 */
	public static boolean reachable(Automaton automaton, int start, int search,
			int[] storage) {
		if (start == search) {
			return true;
		} else if (storage[start] == 0) {
			// this root not yet visited.
			Automaton.State state = automaton.get(start);
			storage[start] = 1; // visited

			if (state instanceof Automaton.Term) {
				Automaton.Term term = (Automaton.Term) state;
				if (term.contents != Automaton.K_VOID) {
					if (reachable(automaton, term.contents, search, storage)) {
						return true;
					}
				}
			} else if (state instanceof Automaton.Collection) {
				Automaton.Collection compound = (Automaton.Collection) state;
				int[] children = compound.children;
				for (int i = 0; i != children.length; ++i) {
					if (reachable(automaton, children[i], search, storage)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
