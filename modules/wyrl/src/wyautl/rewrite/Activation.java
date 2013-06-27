package wyautl.rewrite;

import java.util.BitSet;

import wyautl.core.Automaton;

/**
 * Represents the potential activation of a given rewrite rule. An activation
 * maps states in the automaton to the inputs of a rewrite rule. An activation
 * has a dependence set which includes exactly those states upon which this
 * activation depends. Thus, any changes to those states will potentially
 * invalidate this activation.
 * 
 * @author David J. Pearce
 * 
 */
public final class Activation {
	
	/**
	 * The rewrite rule that this activation will apply.
	 */
	private final RewriteRule rule;
	
	/**
	 * A mapping from the input variables required by the rewrite rule to
	 * matching states in the automaton.
	 */
	private final int[] mapping;
	
	/**
	 * The complete set of states upon which this activation depends. This must
	 * include all those identified in the mapping.
	 */
	private final BitSet dependencies;
	
	public Activation(RewriteRule rule, int[] mapping, BitSet dependencies) {
		this.rule = rule;
		this.mapping = mapping;
		this.dependencies = dependencies;
	}
	
	/**
	 * Returns the complete set of states upon which this activation depends.
	 * Any changes to those states necessarily invalidates this activation, and
	 * requires the dirty states be rechecked for potential activations.
	 * 
	 * @return
	 */
	public BitSet dependencies() {
		return dependencies;
	}
	
	/**
	 * <p>
	 * Apply this activation to a given automaton. This application may or may
	 * not actually modify the automaton and this is indicates by the return
	 * value.
	 * </p>
	 * 
	 * @param automaton
	 *            --- the automaton to be rewritten.
	 * @return
	 */
	public boolean apply(Automaton automaton) {
		return rule.apply(automaton,mapping);
	}
}
