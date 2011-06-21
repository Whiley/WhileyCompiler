package wyjc.runtime;


public final class List implements Any {		
	/**
	 * The reference count is use to indicate how many variables are currently
	 * referencing this compound structure. This is useful for making imperative
	 * updates more efficient. In particular, when the <code>refCount</code> is
	 * <code>1</code> we can safely perform an in-place update of the structure.
	 */
	private int refCount;
	
	/**
	 * The size indicates the current size of the list.
	 */
	private int size;
	
	/**
	 * The data field contains the actual data making up this compound structure.
	 */
	private Any[] data;

	// ================================================================================
	// Generic Operations
	// ================================================================================	 	
	
	public List(int size) {		
		this.refCount = 1;
		this.data = new Any[size];
	}
	
	private List(int size, Any[] data) {		
		this.refCount = 1;
		this.data = data;
	}
	
	public List inc() {
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
		return null;
	}
	
	// ================================================================================
	// List Operations
	// ================================================================================	 
	
	public static Any get(List list, int index) {		
		return list.data[index];
	}
	
	public static List set(final List list, final int index, final Any value) {
		Any[] data = list.data;		
		int size = list.size;
		if(list.refCount == 1) {
			data[index] = value;
			return list;
		} else {			
			Any[] ndata = new Any[size * 2];
			for(int i=0;i!=size;++i) {
				Any v = (i == index) ? value : data[i];				
				ndata[i] = v.inc();
			}			
			return new List(size,ndata);
		}
	}
	
	public static List sublist(final List list, final int start, final int end) {
		return null;
	}
	
	public static List append(final List lhs, final List rhs) {
		return null;
	}
	
	public static List append_l(final List list, final Any item) {
		return null;
	}
	
	public static List append_r(final Any item, final List list) {
		return null;
	}
	
	public static int size(final List list) {
		return list.size;
	}
	
	public static java.util.Iterator<Any> iterator(List c) {
		return null;
	}		
}
