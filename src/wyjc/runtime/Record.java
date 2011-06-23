package wyjc.runtime;

import java.util.*;

public final class Record extends HashMap<String,Object> implements Comparable<Record> {	
	/**
	 * The reference count is use to indicate how many variables are currently
	 * referencing this compound structure. This is useful for making imperative
	 * updates more efficient. In particular, when the <code>refCount</code> is
	 * <code>1</code> we can safely perform an in-place update of the structure.
	 */
	int refCount = 100; // TODO: implement proper reference counting
	
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
			r = r + s + ":" + Util.str(get(s));
		}
		return r + "}";
	}
	
	public int compareTo(Record t) {
		ArrayList<String> mKeys = new ArrayList<String>(keySet());
		ArrayList<String> tKeys = new ArrayList<String>(t.keySet());
		Collections.sort(mKeys);
		Collections.sort(tKeys);

		for(int i=0;i!=Math.min(mKeys.size(),tKeys.size());++i) {
			String mk = mKeys.get(i);
			String tk = tKeys.get(i);
			int c = mk.compareTo(tk);
			if(c != 0) {
				return c;
			}
			String mv = get(mk).toString();
			String tv = get(tk).toString();
			c = mv.compareTo(tv);
			if(c != 0) {
				return c;
			}
		}

		if(mKeys.size() < tKeys.size()) {
			return -1;
		} else if(mKeys.size() > tKeys.size()) {
			return 1;
		} else {
			return 0;
		}
	}
	
	// ================================================================================
	// Record Operations
	// ================================================================================	 	

	public static Object get(final Record record, final String field) {
		return record.get(field);
	}
	
	public static Record put(final Record record, final String field, final Object value) {		
		//if(record.refCount == 1) {
			record.put(field, value);
		//} else {
			// TODO: this is broken --- must recursively update reference
			// counts.
			//record = (HashMap) record.clone();
		//}
		return record;
	}
	
	public static int size(Record record) {
		return record.size();
	}	
}
