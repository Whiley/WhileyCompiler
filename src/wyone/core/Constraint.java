package wyone.core;

import java.util.Map;

/**
 * <p>This represents a constraint over one or more variables. For example, "x < 5"
 * is a constraint over variable "x". Constraints are really the key driving
 * force in Wyone, as they represent pieces of "knowledge" with which we can
 * move towards a satisfying solution, or a proof of unsatisfiability.</p>
 * 
 * @author djp
 * 
 */
public interface Constraint extends Constructor {
	public Constraint substitute(Map<Constructor,Constructor> binding);

	/**
	 * Compute the inverse of a constraint. This is a fundamental operation that
	 * all constraints must support. For example, if we have inferred A, then we
	 * need to know whether !A is already inferred (in which case, the
	 * constraint program is unsatisfiable).
	 * 
	 * @return
	 */
	public Constraint not();
}
