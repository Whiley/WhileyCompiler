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
	

	/**
	 * Compute a sublist of a list.
	 * @param start
	 * @param end
	 * @return
	 */
	public static final WhileyList sublist(WhileyList list, BigRational start, BigRational end) {
		int st = start.intValue();
		int en = end.intValue();
		WhileyList r = new WhileyList();
		for(int i=st;i!=en;++i) {
			r.add(list.get(i));
		}
		return r;
	}
	
	/**
	 * Generate an integer range from start and end values.
	 * @param start
	 * @param end
	 * @return
	 */
	public static WhileyList range(BigRational start, BigRational end) {
		WhileyList ret = new WhileyList();
		
		// FIXME: seems ludicrously inefficient!
		BigRational dir = BigRational.valueOf(end.compareTo(start));
		
		while(!start.equals(end)) {
			ret.add(start);
			start = start.add(dir);
		}
		
		return ret;
	}	
	
	/**
	 * The following method is used by the main launcher to convert from Java's
	 * main(String[] args) into whiley's main([string] args) format.
	 * 
	 * @param args
	 * @return
	 */
	public static WhileyList fromStringList(String[] args) {
		WhileyList r = new WhileyList();
		for(String s : args) {
			r.add(fromString(s));
		}
		return r;
	}
	
	/**
	 * Generate a Whiley list from a Java String. 
	 * @param s
	 * @return
	 */
	public static WhileyList fromString(String s) {
		WhileyList r = new WhileyList();
		for(int i=0;i!=s.length();++i) {
			int c = s.charAt(i);
			r.add(BigRational.valueOf(c));
		}
		return r;
	}

	/**
	 * The following method is used for printing debug output arising from debug
	 * statements.
	 * 
	 * @param list
	 */
	public static void debug(WhileyList list) {
		for(Object o : list) {
			if(o instanceof BigRational) {
				BigRational bi = (BigRational) o;
				System.out.print((char)bi.intValue());
			}
		}
		System.out.println("");
	}
}
