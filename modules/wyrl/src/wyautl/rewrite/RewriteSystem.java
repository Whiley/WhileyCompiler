package wyautl.rewrite;

import wyautl.core.Automaton;

/**
 * Represents the (abstract) mechanism for controlling the rewriting of a given
 * automaton under a given set of rules. Different implementation of this
 * interface are possible, and will have different performance characteristics.
 * 
 * @author David J. Pearce
 * 
 */
public interface RewriteSystem {
	
	/**
	 * Apply this rewriter to the given automaton, rewriting it as much as
	 * possible. Some implementations of this method may chose to stop rewriting
	 * before all rewrites are performed (e.g. to limit the number of steps
	 * taken). The return value indicates whether or not rewriting was
	 * completed.
	 * 
	 * @param automaton
	 *            --- The automaton to be rewritten.
	 * 
	 * @return --- Indicates whether or not rewriting is complete (true
	 *         indicates it was completed). This is necessary for systems which
	 *         only rewrite upto a given number of steps (e.g. to prevent
	 *         rewriting from continuing too long).
	 */
	public boolean apply(Automaton automaton);
}
