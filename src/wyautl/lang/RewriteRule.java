package wyautl.lang;

/**
 * <p>
 * A rewrite rule is used to rewrite the states of an automata in some way.
 * Typically, this is used for simplification of the automata. For example, one
 * might have an OR kind and a TOP kind. Then, a common simplification would be
 * <code>OR(TOP,OTHER)</code> => <code>TOP</code>.
 * </p>
 * 
 * <p>
 * <b>NOTE:</b> it is assumed that rewrite rules are <i>convergent</i> and
 * <i>confluent</i>. In other words, repeated application of the rule will
 * eventually terminate; and, the order in which the rule is applied to states
 * does not matter.
 * </p>
 * 
 * @author djp
 * 
 */
public interface RewriteRule {
	/**
	 * Apply this rewrite rule to the state identified by index in the given
	 * automata. The rewrites are performed in-place on the given automata.
	 * 
	 * @param index
	 *            --- index of state in automata.
	 * @param automata
	 *            --- automata to be rewritten.
	 * @return --- true if the rewrite was applied; false, otherwise.
	 */
	boolean apply(int index, Automata automata);
}
