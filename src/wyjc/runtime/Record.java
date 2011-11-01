package wyjc.runtime;

import java.util.*;

public final class Record extends HashMap<String,Object> {	
	/**
	 * The reference count is use to indicate how many variables are currently
	 * referencing this compound structure. This is useful for making imperative
	 * updates more efficient. In particular, when the <code>refCount</code> is
	 * <code>1</code> we can safely perform an in-place update of the structure.
	 */
	int refCount = 1; 
	
	public Record() {}
	
	Record(HashMap<String,Object> r) {
		super(r);
		for(Object item : r.values()) {
			Util.incRefs(item);
		}
	}
	
	// ================================================================================
	// Generic Operations
	// ================================================================================	 	
		
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
			r = r + s + ":" + whiley.lang.Any$native.toString(get(s));
		}
		return r + "}";
	}
		
	// ================================================================================
	// Record Operations
	// ================================================================================	 	

	public static Object get(final Record record, final String field) {
		Util.decRefs(record);
		Object item = record.get(field);
		Util.incRefs(item);
		return item;
	}
	
	public static Record put(Record record, final String field, final Object value) {		
		if(record.refCount > 1) {
			Util.nrecord_clones++;
			Util.nrecord_clones_nfields += record.size();
			Util.decRefs(record);
			record = new Record(record);			
		} else {
			Util.nrecord_strong_updates++;
		}
		Object val = record.put(field, value);
		Util.decRefs(val);
		Util.incRefs(value);		
		return record;
	}
	
	public static int size(Record record) {
		Util.decRefs(record);
		return record.size();
	}	
	
	public static Object internal_get(final Record record, final String field) {
		return record.get(field);		
	}
}
