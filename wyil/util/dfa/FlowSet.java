package wyil.util.dfa;

public interface FlowSet {

	/**
	 * FlowSets must be cloneable to facilitate multiple flows of execution
	 * from conditionals
	 * 
	 * @return A Clone of the current FlowSet
	 */
	public Object clone();
	
	/**
     * Computes the least upper bound of this flowset and that provided. <b>NOTE</b>
     * the join operation has a subtle, yet important, requirement. If the
     * result of the join must be equivalent to *this* flowset, then it must be
     * the *same* flowset.
     * 
     * @param s
     *            Another FlowSet to join with this
     * @return true if this FlowSet has changed due to the computation, false
     *         otherwise
     */
	public FlowSet join(FlowSet s);
}
