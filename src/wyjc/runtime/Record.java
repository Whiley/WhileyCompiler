package wyjc.runtime;

import java.util.*;

public final class Record {	
	/**
	 * The reference count is use to indicate how many variables are currently
	 * referencing this compound structure. This is useful for making imperative
	 * updates more efficient. In particular, when the <code>refCount</code> is
	 * <code>1</code> we can safely perform an in-place update of the structure.
	 */
	private int refCount;
	
	private HashMap<String,Object> data;

	// ================================================================================
	// Generic Operations
	// ================================================================================	 	
	
	public Record() {		
		this.refCount = 1;
		this.data = new HashMap<String,Object>();
	}
	
	// ================================================================================
	// Record Operations
	// ================================================================================	 	

	public static Object get(final Record record, final String field) {
		return record.data.get(field);
	}
	
	public static Record put(final Record record, final String field, final Object value) {
		HashMap<String,Object> data = record.data;
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

	public static java.util.Iterator<Object> iterator(Record c) {
		return null;
	}	
}
