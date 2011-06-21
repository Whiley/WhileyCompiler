package wyjc.runtime;

/**
 * A Compound is a general data-structure for representing sets, lists,
 * dictionaries and records in Whiley. Using one data-structure avoids many
 * issues related to coercing between different types in Whiley; however, at the
 * same time, it's difficult to make it efficient!
 * 
 * @author djp
 * 
 */
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
	 * The kind tells us something about the current shape of this compound
	 * structure.
	 */
	private byte kind;
	
	/**
	 * The data field contains the actual data making up this compound structure.
	 */
	private Object[] data;
	
	public void incCount() { if(++refCount < 0) { throw new RuntimeException("Reference Count Overflow"); } }
	
	public void decCount() { refCount--; }

	// ================================================================================
	// Generic Operations
	// ================================================================================	 	
	
	public Compound() {
		this.kind = K_UNKNOWN;
		this.refCount = 1;
	}
	
	public int size() {
		switch(kind) {
			case K_UNKNOWN:
				return 0;
			case K_SET:
				return setSize();
			case K_LIST:
				return listSize();
			case K_DICT:
				return dictSize();
			case K_RECORD:
				return recSize();
		}
		// this is dead-code
		throw new RuntimeException("Illegal Compound Kind");
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
	// List Operations
	// ================================================================================	 
	
	public Any listGet(int index) {
		return null;
	}
	
	public Compound listSet(int index, Any value) {
		return null;
	}
	
	public Compound append(Compound list) {
		return null;
	}
	
	public Compound append_l(Any item) {
		return null;
	}
	
	public Compound append_r(Any item) {
		return null;
	}
	
	public int listSize() {
		return 0;
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
