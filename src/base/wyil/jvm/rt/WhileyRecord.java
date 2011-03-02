// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

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