package wyjc.runtime;


public final class Set extends java.util.HashSet {	
	/**
	 * The reference count is use to indicate how many variables are currently
	 * referencing this compound structure. This is useful for making imperative
	 * updates more efficient. In particular, when the <code>refCount</code> is
	 * <code>1</code> we can safely perform an in-place update of the structure.
	 */
	private int refCount;

	// ================================================================================
	// Generic Operations
	// ================================================================================	 	
		
	public Set() {		
		this.refCount = 1;		
	}
	
	private Set(java.util.Collection c) {
		super(c);
	}	
	
	// ================================================================================
	// Set Operations
	// ================================================================================	 	
	
	public static Set union(Set lhs, Set rhs) {
		Set set = new Set(lhs);
		set.addAll(rhs);
		return set;
	}
	
	public static Set union(Set lhs, Object rhs) {
		Set set = new Set(lhs);
		set.add(rhs);
		return set;
	}
	
	public static Set union(Object lhs, Set rhs) {
		Set set = new Set(rhs);
		set.add(lhs);
		return set;
	}
	
	public static Set difference(Set lhs, Set rhs) {
		Set set = new Set(lhs);
		set.removeAll(rhs);
		return set;
	}
	
	public static Set difference(Set lhs, Object rhs) {
		Set set = new Set(lhs);
		set.remove(rhs);
		return set;
	}	
	
	public static Set intersect(Set lhs, Set rhs) {
		Set set = new Set(); 		
		for(Object o : lhs) {
			if(rhs.contains(o)) {
				set.add(o);
			}
		}
		return set;
	}
	
	public static Set intersect(Set lhs, Object rhs) {
		Set set = new Set(); 		
		
		if(lhs.contains(rhs)) {
			set.add(rhs);
		} 
				
		return set;
	}
	
	public static Set intersect(Object lhs, Set rhs) {
		Set set = new Set(); 		
		
		if(rhs.contains(lhs)) {
			set.add(lhs);
		} 		
		
		return set;
	}	
}
