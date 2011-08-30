package wyts.lang;

/**
 * A subtype inference is responsible for computing a complete subtype
 * relation between two given graphs. The class is abstract because there
 * are different possible implementations of this. In particular, the case
 * when coercions are being considered, versus the case when they are not.
 * 
 * @author djp
 * 
 */
public abstract class SubtypeInference {
	protected final Node[] fromGraph;
	protected final Node[] toGraph;
	protected final SubtypeRelation assumptions;
	
	public SubtypeInference(Node[] fromGraph, Node[] toGraph) {
		this.fromGraph = fromGraph;
		this.toGraph = toGraph;
		this.assumptions = new SubtypeRelation(fromGraph.length,toGraph.length);
	}
	
	public SubtypeRelation doInference() {
		int fromDomain = fromGraph.length;
		int toDomain = toGraph.length;
		
		boolean changed = true;
		while(changed) {
			changed=false;
			for(int i=0;i!=fromDomain;i++) {
				for(int j=0;j!=toDomain;j++) {					
					boolean isubj = isSubType(i,j);					
					boolean isupj = isSuperType(i,j);		
					
					if(assumptions.isSubtype(i,j) && !isubj) {
						assumptions.setSubtype(i,j,false);
						changed = true;
					}
					if(assumptions.isSupertype(i,j) && !isupj) {
						assumptions.setSupertype(i,j,false);
						changed = true;
					}						
				}	
			}
		}
		
		return assumptions;
	}
	
	/**
	 * <p>
	 * Determine whether type <code>to</code> is a <i>subtype</i> of type
	 * <code>from</code> (written from :> to). In other words, whether the set
	 * of all possible values described by the type <code>to</code> is a
	 * subset of that described by <code>from</code>.
	 * </p>
	 * 
	 * @param from --- An index into <code>fromGraph</code>.
	 * @param to --- An index into <code>toGraph</code>.
	 * @return
	 */
	public abstract boolean isSubType(int from, int to);
	
	/**
	 * <p>
	 * Determine whether type <code>to</code> is a <i>super type</i> of type
	 * <code>from</code> (written from <: to). In other words, whether the set
	 * of all possible values described by the type <code>to</code> is a
	 * super set of that described by <code>from</code>.
	 * </p>
	 * 
	 * @param from --- An index into <code>fromGraph</code>.
	 * @param to --- An index into <code>toGraph</code>.
	 * @return
	 */
	public abstract boolean isSuperType(int from, int to);
}
