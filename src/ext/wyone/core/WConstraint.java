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
public interface WConstraint extends WExpr {
	public WConstraint substitute(Map<WExpr,WExpr> binding);
}
