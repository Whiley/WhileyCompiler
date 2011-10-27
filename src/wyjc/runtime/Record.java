package wyjc.runtime;

import java.util.*;

public final class Record extends HashMap<String,Object> {	
	/**
	 * The reference count is use to indicate how many variables are currently
	 * referencing this compound structure. This is useful for making imperative
	 * updates more efficient. In particular, when the <code>refCount</code> is
	 * <code>1</code> we can safely perform an in-place update of the structure.
	 */
	int refCount = 100; // TODO: implement proper reference counting
	
	public Record() {}
	
	Record(HashMap<String,Object> r) {
		super(r);
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
			r = r + s + ":" + whiley.lang.String$native.toString(get(s));
		}
		return r + "}";
	}
		
	// ================================================================================
	// Record Operations
	// ================================================================================	 	

	public static Object get(final Record record, final String field) {
		return record.get(field);
	}
	
	public static Record put(Record record, final String field, final Object value) {		
		if(record.refCount > 1) {
			Record nrecord = new Record(record);
			for(Object e : nrecord.values()) {
				Util.incRefs(e);
			}
			record = nrecord;
		}
		Object val = record.put(field, value);
		Util.decRefs(val);
		Util.incRefs(value);		
		return record;
	}
	
	public static int size(Record record) {
		return record.size();
	}	
}
