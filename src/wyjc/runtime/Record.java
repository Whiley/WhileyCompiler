// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

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
		Object item = record.get(field);
		Util.incRefs(item);
		return item;
	}
	
	public static Record put(Record record, final String field, final Object value) {
		Util.countRefs(record);
		if(record.refCount > 0) {
			Util.countClone(record);			
			record = new Record(record);			
		} else {
			Util.nrecord_strong_updates++;
		}
		Object val = record.put(field, value);
		Util.decRefs(val); // decrement overwritten value
		Util.incRefs(value);
		return record;
	}
	
	public static Object internal_get(final Record record, final String field) {
		Object item = record.get(field);
		if(record.refCount > 0) {
			Util.incRefs(item);
		}
		return item;		
	}
}
