package wyil.jvm.rt;

import java.util.*;

public final class WhileySet extends HashSet {
	public WhileySet() {
		super();
	}
	
	public WhileySet(Collection c) {
		super(c);
	}
	
	public WhileySet clone() {
		return new WhileySet(this);
	}
	
	public String toString() {
		String r = "{";
		boolean firstTime=true;
		ArrayList<String> ss = new ArrayList<String>(this);		
		Collections.sort(ss);

		for(Object o : ss) {
			if(!firstTime) {
				r = r + ", ";
			}
			firstTime=false;
			r = r + o.toString();
		}
		return r + "}";
	}		
	
	public boolean equals(WhileySet ws) {
		// FIXME: optimisation opportunity here
		return super.equals(ws);
	}
	
	public boolean notEquals(WhileySet ws) {
		return !super.equals(ws);
	}
	
	public boolean subset(WhileySet ws) {
		return ws.containsAll(this) && ws.size() > size();
	}
	
	public boolean subsetEq(WhileySet ws) {
		return ws.containsAll(this);
	}
	
	public WhileySet union(WhileySet rset) {
		WhileySet set = new WhileySet(this);
		set.addAll(rset);
		return set;
	}
	
	public WhileySet difference(WhileySet rset) {
		WhileySet set = new WhileySet(this);
		set.removeAll(rset);
		return set;
	}
	
	public WhileySet intersect(WhileySet rset) {
		WhileySet set = new WhileySet(); 		
		for(Object o : this) {
			if(rset.contains(o)) {
				set.add(o);
			}
		}
		return set;
	}
}
