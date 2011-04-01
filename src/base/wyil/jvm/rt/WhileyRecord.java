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

package wyil.jvm.rt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class WhileyRecord extends HashMap<String, Object> implements
		Comparable<WhileyRecord> {
	public WhileyRecord() {
		super();
	}

	public WhileyRecord(Map<String,Object> c) {
		super(c);
	}

	public WhileyRecord clone() {
		return Util.record_clone(this);		
	}
	
	public boolean equals(WhileyRecord t) {
		return super.equals(t);
	}	
	
	public boolean notEquals(WhileyRecord t) {
		return !super.equals(t);
	}

	public int compareTo(WhileyRecord t) {
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
}