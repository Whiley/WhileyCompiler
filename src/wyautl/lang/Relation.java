package wyautl.lang;

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
	 * Get the automata in the "from" position.
	 * 
	 * @return
	 */
	public Automaton from();
	
	/**
	 * Get the automata in the "to" position.
	 * 
	 * @return
	 */
	public Automaton to();

	/**
	 * Check whether a node in the <code>from</code> automata, and a node in the
	 * <code>to</code> automata are related or not.
	 * 
	 * @param from
	 *            --- An index into automata <code>from</code>.
	 * @param to
	 *            --- An index into automata <code>to</code>.
	 * @return
	 */
	public boolean isRelated(int from, int to);

	/**
	 * <p>
	 * Recalculate the relationship status between a node in the
	 * <code>from</code> automata, and a node in the <code>to</code> automata.
	 * </p>
	 * 
	 * @param from
	 *            --- An index into automata <code>from</code>.
	 * @param to
	 *            --- An index into automata <code>to</code>.
	 * @return --- true if their status changed, false otherwise.
	 */
	public boolean update(int from, int to);
}
