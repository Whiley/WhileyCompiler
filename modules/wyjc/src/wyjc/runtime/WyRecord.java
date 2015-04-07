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

public final class WyRecord extends HashMap<String,Object> {
	
	public WyRecord() {}

	WyRecord(HashMap<String,Object> r) {
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
			r = r + s + ":" + whiley.lang.Any$native.toRealString(get(s));
		}
		return r + "}";
	}

	// ================================================================================
	// Record Operations
	// ================================================================================

	public Object get(final String field) {
		return super.get(field);
	}
	
	public static Object get(final WyRecord record, final String field) {
		return record.get(field);
	}

	public static WyRecord put(WyRecord record, final String field, final Object value) {
		record = new WyRecord(record);
		record.put(field, value);		
		return record;
	}

	public static Object internal_get(final WyRecord record, final String field) {
		return record.get(field);		
	}
}
