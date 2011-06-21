package wyjc.runtime;


public final class Compound extends Any {
	private static final int K_UNKNOWN = 0; 
	private static final int K_TUPLE = 1;
	private static final int K_SET = 2;
	private static final int K_LIST = 3;
	private static final int K_DICT = 4;
	private static final int K_RECORD = 5;
	
	/**
	 * The reference count is use to indicate how many variables are currently
	 * referencing this compound structure. This is useful for making imperative
	 * updates more efficient. In particular, when the <code>refCount</code> is
	 * <code>1</code> we can safely perform an in-place update of the structure.
	 */
	private int refCount;

	/**
	 * The kind is used to signal the current layout of this compound structure.
	 */
	private byte kind;
	
	/**
	 * The data field contains the actual data making up this compound structure.
	 */
	private Object[] data;

	/**
	 * The size field indicates how may slots from <code>data</code> are used.
	 */
	private int size;

	// ================================================================================
	// Generic Operations
	// ================================================================================	 	
	
	public Compound() {		
		this.refCount = 1;
		this.data = new Object[16];
	}
	
	public void incCount() {
		if (++refCount < 0) {
			throw new RuntimeException("Reference Count Overflow");
		}
	}
	
	public void decCount() { refCount--; }
	
	public boolean instanceofOf(Type t) {
		return false;
	}
	
	// ================================================================================
	// List Operations
	// ================================================================================	 
	
	public static Any get(Compound list, int index) {
		return null;
	}
	
	public static Compound set(Compound list, int index, Any value) {
		return null;
	}
	
	public static Compound append(Compound lhs, Compound rhs) {
		return null;
	}
	
	public static Compound append_l(Compound list, Any item) {
		return null;
	}
	
	public static Compound append_r(Any item, Compound list) {
		return null;
	}
	
	public static int size(Compound list) {
		return list.size;
	}
	

	
	// ================================================================================
	// Set Operations
	// ================================================================================	 	
	
	public int setSize() {
		return 0;
	}
	
	public Compound union(Compound set) {
		return null;
	}
	
	public Compound union_l(Any item) {
		return null;
	}
	
	public Compound union_r(Any item) {
		return null;
	}
	
	public Compound intersect(Compound set) {
		return null;
	}
	
	public Compound difference(Compound set) {
		return null;
	}
	
	// ================================================================================
	// Dictionary Operations
	// ================================================================================	 	

	public Any dictGet(Any key) {
		return null;
	}
	
	public Compound dictPut(Any key, Any value) {
		return this;
	}
	
	public int dictSize() {
		return 0;
	}
	
	// ================================================================================
	// Record Operations
	// ================================================================================	 	

	public Any recGet(String field) {
		return null;
	}
	
	public Compound recPut(String field, Any value) {
		return null;
	}
	
	public int recSize() {
		return 0;
	}
}
