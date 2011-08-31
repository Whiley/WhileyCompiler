package wyts.lang;

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
 * @author djp
 * 
 */
public interface Interpretation {
	
	/**
	 * <p>
	 * Determine whether state <code>to</code> is <i>subsumed</i> by state
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
	public boolean isSubsumed(int fromIndex, Automata from, int toIndex,
			Automata to);
}
