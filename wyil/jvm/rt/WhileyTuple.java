package wyil.jvm.rt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class WhileyTuple extends HashMap<String, Object> implements
		Comparable<WhileyTuple> {
	public WhileyTuple() {
		super();
	}

	public WhileyTuple(Map<String,Object> c) {
		super(c);
	}

	public WhileyTuple clone() {
		return new WhileyTuple(this);
	}
	
	public boolean equals(WhileyTuple t) {
		return super.equals(t);
	}	
	
	public boolean notEquals(WhileyTuple t) {
		return !super.equals(t);
	}

	public int compareTo(WhileyTuple t) {
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
		String r = "(";
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
		return r + ")";
	}
}