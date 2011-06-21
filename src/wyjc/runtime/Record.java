package wyjc.runtime;

import java.util.*;

public final class Record implements Any {	
	/**
	 * The reference count is use to indicate how many variables are currently
	 * referencing this compound structure. This is useful for making imperative
	 * updates more efficient. In particular, when the <code>refCount</code> is
	 * <code>1</code> we can safely perform an in-place update of the structure.
	 */
	private int refCount;
	
	private HashMap<String,Any> data;

	// ================================================================================
	// Generic Operations
	// ================================================================================	 	
	
	public Record() {		
		this.refCount = 1;		
	}
	
	public Record inc() {
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
	// Record Operations
	// ================================================================================	 	

	public static Any get(final Record record, final String field) {
		return record.data.get(field);
	}
	
	public static Record put(final Record record, final String field, final Any value) {
		HashMap<String,Any> data = record.data;
		if(record.refCount == 1) {
			data.put(field, value);
		} else {
			// TODO: this is broken --- must recursively update reference
			// counts.
			data = (HashMap) data.clone();
		}
		return record;
	}
	
	public static int size(Record c) {
		return 0;
	}	

	public static java.util.Iterator<Any> iterator(Record c) {
		return null;
	}	
}
