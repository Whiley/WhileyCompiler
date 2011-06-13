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

public class Util {

	private static final boolean debug = false;
	
	static { 
		if(debug) {
			Runtime.getRuntime().addShutdownHook(new Thread(){
				public void run() {
					System.err.println("===========================================");
					System.err.println("CLONING STATS");
					System.err.println("===========================================");
					System.err.println("list clones: " + nlist_clones);
					System.err.println("set clones: " + nset_clones);
					System.err.println("record clones: " + nrecord_clones);
				}
			});
		}
	}	
	
	private static int nlist_clones = 0;
	private static int nset_clones = 0;
	private static int nrecord_clones = 0;
	
	public static Object clone(Object o) {
		if(o instanceof BigRational || o instanceof Boolean || o == null) {
			return o;
		} else if(o instanceof ArrayList) {
			return list_clone((ArrayList)o);
		} else if(o instanceof WhileySet) {
			return set_clone((WhileySet)o);
		} else {
			return record_clone((WhileyRecord)o);
		} 
	}
	
	public static ArrayList list_clone(ArrayList in) {
		nlist_clones++;
		ArrayList l = new ArrayList();
		for(Object o : in) {
			l.add(clone(o));
		}		
		return l;
	}
	
	public static WhileySet set_clone(WhileySet in) {
		nset_clones++;
		WhileySet l = new WhileySet();
		for(Object o : in) {
			l.add(clone(o));
		}
		return l;
	}
	
	public static WhileyRecord record_clone(WhileyRecord in) {
		nrecord_clones++;
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
	public static ArrayList sublist(ArrayList list, BigRational start, BigRational end) {
		int st = start.intValue();
		int en = end.intValue();
		ArrayList r = new ArrayList();
		for(int i=st;i!=en;++i) {
			r.add(list.get(i));
		}
		return r;
	}
	
	/**
	 * Append two lists together
	 * @param rhs
	 * @return
	 */
	public static ArrayList append(ArrayList lhs, ArrayList rhs) {
		ArrayList r = new ArrayList(lhs);
		r.addAll(rhs);
		return r;
	}
	
	public static ArrayList append(ArrayList lhs, Object rhs) {
		ArrayList r = new ArrayList(lhs);
		r.add(rhs);
		return r;
	}
	
	public static ArrayList append(Object lhs, ArrayList rhs) {
		ArrayList r = new ArrayList(rhs);
		r.add(0,lhs);
		return r;
	}
	
	public static WhileySet union(WhileySet lhs, WhileySet rhs) {
		WhileySet set = new WhileySet(lhs);
		set.addAll(rhs);
		return set;
	}
	
	public static WhileySet union(WhileySet lhs, Object rhs) {
		WhileySet set = new WhileySet(lhs);
		set.add(rhs);
		return set;
	}
	
	public static WhileySet union(Object lhs, WhileySet rhs) {
		WhileySet set = new WhileySet(rhs);
		set.add(lhs);
		return set;
	}
	
	public static WhileySet difference(WhileySet lhs, WhileySet rhs) {
		WhileySet set = new WhileySet(lhs);
		set.removeAll(rhs);
		return set;
	}
	
	public static WhileySet difference(WhileySet lhs, Object rhs) {
		WhileySet set = new WhileySet(lhs);
		set.remove(rhs);
		return set;
	}	
	
	public static WhileySet intersect(WhileySet lhs, WhileySet rhs) {
		WhileySet set = new WhileySet(); 		
		for(Object o : lhs) {
			if(rhs.contains(o)) {
				set.add(o);
			}
		}
		return set;
	}
	
	public static WhileySet intersect(WhileySet lhs, Object rhs) {
		WhileySet set = new WhileySet(); 		
		
		if(lhs.contains(rhs)) {
			set.add(rhs);
		} 
				
		return set;
	}
	
	public static WhileySet intersect(Object lhs, WhileySet rhs) {
		WhileySet set = new WhileySet(); 		
		
		if(rhs.contains(lhs)) {
			set.add(lhs);
		} 		
		
		return set;
	}
	
	/**
	 * Generate an integer range from start and end values.
	 * @param start
	 * @param end
	 * @return
	 */
	public static ArrayList range(BigRational start, BigRational end) {
		ArrayList ret = new ArrayList();
		
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
	public static ArrayList fromStringList(String[] args) {
		ArrayList r = new ArrayList();
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
	public static ArrayList fromString(String s) {
		ArrayList r = new ArrayList();
		for(int i=0;i!=s.length();++i) {
			int c = s.charAt(i);
			r.add(BigRational.valueOf(c));
		}
		return r;
	}

	/**
	 * Convert a Whiley list into a Java String
	 * @param list
	 * @return
	 */
	public static String toString(ArrayList list) {
		String r = "";
		for(Object o : list) {
			if(o instanceof BigRational) {
				int v = ((BigRational)o).intValue();
				r += (char) v;
			} else {
				throw new RuntimeException("Invalid Whiley List");
			}
		}
		return r;
	}

	/**
	 * This method is used for the special case when the left-hand side of an
	 * equality operation may be null.
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static boolean equals(Object o1, Object o2) {
		return (o1 != null && o1.equals(o2)) || (o1 == o2);
	}
	
	/**
	 * The following method is used for printing debug output arising from debug
	 * statements.
	 * 
	 * @param list
	 */
	public static void debug(ArrayList list) {
		for(Object o : list) {
			if(o instanceof BigRational) {
				BigRational bi = (BigRational) o;
				System.out.print((char)bi.intValue());
			}
		}		
	}
}
