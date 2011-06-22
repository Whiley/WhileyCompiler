package wyjc.runtime;

import java.util.*;

public final class Record extends HashMap<String,Object> {	
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
	
	public Record() {		
		this.refCount = 1;		
	}
	
	public String toString() {
		String r = "{";
		boolean firstTime = true;

		ArrayList<String> ss = new ArrayList<String>(keySet());
		Collections.sort(ss);
		for (String s : ss) {
			if (!firstTime) {
				r = r + ",";
			}
			firstTime = false;
			r = r + s + ":" + get(s).toString();
		}
		return r + "}";
	}
	
	// ================================================================================
	// Record Operations
	// ================================================================================	 	

	public static Object get(final Record record, final String field) {
		return record.get(field);
	}
	
	public static Record put(final Record record, final String field, final Object value) {		
		if(record.refCount == 1) {
			record.put(field, value);
		} else {
			// TODO: this is broken --- must recursively update reference
			// counts.
			//record = (HashMap) record.clone();
		}
		return record;
	}
	
	public static int size(Record record) {
		return record.size();
	}	
}
