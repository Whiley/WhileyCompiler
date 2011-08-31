package wyts.lang;

/**
 * <p>
 * An interpretation is necessary for determining whether one automata subsumes
 * another. It provides an interpretation of states in the automata being
 * compared. This is useful in situations where states of one kind subsume those
 * of another. For example, a state of kind "OR" can subsume a state with the
 * same kind as one of its children. Sometimes, there is a special state "TOP"
 * which subsumes all states.
 * </p>
 * 
 * @author djp
 * 
 */
public interface Interpretation {
	/**
	 * <p>
	 * Determine whether state <code>to</code> is a <i>sub-set</i> of state
	 * <code>from</code> (written from :> to). In other words, whether the set
	 * of all possible values accepted by state <code>to</code> is a subset of
	 * that described by <code>from</code>.
	 * </p>
	 * 
	 * @param fromIndex
	 *            --- An index into automata <code>from</code>.
	 * @param toIndex
	 *            --- An index into automata <code>to</code>.
	 * @return
	 */
	public boolean isSubSet(int fromIndex, Automata from, int toIndex,
			Automata to);

	/**
	 * <p>
	 * Determine whether state <code>to</code> is a <i>super-set</i> of state
	 * <code>from</code> (written from <: to). In other words, whether the set
	 * of all possible values accepted by the state <code>to</code> is a super
	 * set of that described by <code>from</code>.
	 * </p>
	 * 
	 * @param fromIndex
	 *            --- An index into automata <code>from</code>.
	 * @param toIndex
	 *            --- An index into automata <code>to</code>.
	 * @return
	 */
	public boolean isSuperSet(int fromIndex, Automata from,
			int toIndex, Automata to);
}
