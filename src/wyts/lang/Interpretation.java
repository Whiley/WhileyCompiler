package wyts.lang;

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
 * @author djp
 * 
 */
public interface Interpretation<T> {

	/**
	 * Returns true iff the given automata accepts the given value.
	 * 
	 * @param automata
	 * @param value
	 * @return
	 */
	public boolean accepts(Automata automata, T value);	
}
