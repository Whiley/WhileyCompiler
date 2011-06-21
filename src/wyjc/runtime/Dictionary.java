package wyjc.runtime;


public final class Dictionary extends java.util.HashMap<Any,Any> implements Any {	
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
	
	public Dictionary inc() {
		if (++refCount < 0) {
			throw new RuntimeException("Reference Count Overflow");
		}
		return this;
	}
	
	public void dec() { refCount--; }
	
	public boolean instanceOf(Type t) {
		return false;
	}
	
	public Any coerce(Type t) {
		return this;
	}
	
	// ================================================================================
	// Dictionary Operations
	// ================================================================================	 	

	public static Any get(Dictionary dict, Any key) {
		return dict.get(key);
	}
	
	public static Dictionary put(Dictionary dict, Any key, Any value) {
		dict.put(key,value);
		return dict;
	}
	
	public static int size(Dictionary dict) {
		return dict.size();
	}
}
