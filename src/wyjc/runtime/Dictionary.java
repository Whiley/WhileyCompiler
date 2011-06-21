package wyjc.runtime;


public final class Dictionary extends java.util.HashMap {	
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
	
	Dictionary() {		
		super();
		this.refCount = 1;		
	}
	
	// ================================================================================
	// Dictionary Operations
	// ================================================================================	 	

	public static Object get(Dictionary dict, Object key) {
		return dict.get(key);
	}
	
	public static Dictionary put(Dictionary dict, Object key, Object value) {
		dict.put(key,value);
		return dict;
	}
	
	public static int size(Dictionary dict) {
		return dict.size();
	}
}
