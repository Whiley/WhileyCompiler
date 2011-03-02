package wyil.jvm.rt;

import java.util.Map;

public class Util {

	public static Object clone(Object o) {
		if(o instanceof BigRational || o instanceof Boolean || o == null) {
			return o;
		} else if(o instanceof WhileyList) {
			return list_clone((WhileyList)o);
		} else if(o instanceof WhileySet) {
			return set_clone((WhileySet)o);
		} else {
			return record_clone((WhileyRecord)o);
		} 
	}
	
	public static WhileyList list_clone(WhileyList in) {
		WhileyList l = new WhileyList();
		for(Object o : in) {
			l.add(clone(o));
		}
		return l;
	}
	
	public static WhileySet set_clone(WhileySet in) {
		WhileySet l = new WhileySet();
		for(Object o : in) {
			l.add(clone(o));
		}
		return l;
	}
	
	public static WhileyRecord record_clone(WhileyRecord in) {
		WhileyRecord l = new WhileyRecord();
		for(Map.Entry<String,Object> o : in.entrySet()) {
			l.put(o.getKey(),clone(o.getValue()));
		}
		return l;
	}
}
