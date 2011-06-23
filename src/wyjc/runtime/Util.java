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

import java.math.*;

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

	public static String append(final String lhs, final String rhs) {
		return lhs + rhs;
	}
	
	public static BigInteger stringlength(final String lhs) {
		return BigInteger.valueOf(lhs.length());
	}
	
	public static String substring(final String lhs, final BigInteger _start, final BigInteger _end) {
		int start = _start.intValue();
		int end = _end.intValue();
		return lhs.substring(start,end);
	}
	
	/**
	 * This method is used to convert the arguments supplied to main (which have
	 * type <code>String[]</code>) into an appropriate Whiley List.
	 * 
	 * @param args
	 * @return
	 */
	public static List fromStringList(String[] args) {
		List r = new List(args.length);
		for(int i=0;i!=args.length;++i) {
			r.set(i,args[i]);
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
	 * Convert a given Whiley object into a string
	 * @param o
	 * @return
	 */
	public static String str(Object o) {
		if(o == null) {
			return "null";
		} else if(o instanceof String) {
			String s = (String) o;
			return "\"" + s + "\"";
		} else {
			return o.toString();
		}
	}
	
	/**
	 * The following method is used for printing debug output arising from debug
	 * statements.
	 * 
	 * @param list
	 */
	public static void debug(String str) {
		System.out.print(str);			
	}
		
	/**
	 * Increment the reference count for this object. In some cases, this may
	 * have no effect. In other cases, the current reference count will be
	 * maintained and in-place updates can only occur when the reference count is
	 * one.
	 */
	public static Object incRefs(Object obj) {
		if(obj instanceof List) {
			List list = (List) obj;
			list.refCount++;
		} else if(obj instanceof Record) {
			Record rec = (Record) obj;			
			rec.refCount++;
		} else if(obj instanceof Set) {
			Set set = (Set) obj;
			set.refCount++;			
		} else if(obj instanceof Dictionary) {
			Dictionary dict = (Dictionary) obj;
			dict.refCount++;			
		} 
		return obj;
	}

	public static List incRefs(List obj) {		
		obj.refCount++;
		return obj;
	}
	
	public static Set incRefs(Set obj) {
		obj.refCount++;
		return obj;
	}
	
	public static Record incRefs(Record obj) {
		obj.refCount++;
		return obj;
	}
	
	public static Dictionary incRefs(Dictionary obj) {
		obj.refCount++;
		return obj;
	}
	
	/**
	 * Decrement the reference count for this object. In some cases, this may
	 * have no effect. In other cases, the current reference count will be
	 * maintained and in-place updates can only occur when the reference count is
	 * one.
	 */
	public static Object decRefs(Object obj) {
		if(obj instanceof List) {
			List list = (List) obj;
			list.refCount--;
		} else if(obj instanceof Record) {
			Record rec = (Record) obj;			
			rec.refCount--;
		} else if(obj instanceof Set) {
			Set set = (Set) obj;
			set.refCount--;			
		} else if(obj instanceof Dictionary) {
			Dictionary dict = (Dictionary) obj;
			dict.refCount--;			
		} 
		return obj;
	}

	public static List decRefs(List obj) {		
		obj.refCount--;
		return obj;
	}
	
	public static Set decRefs(Set obj) {
		obj.refCount--;
		return obj;
	}
	
	public static Record decRefs(Record obj) {
		obj.refCount--;
		return obj;
	}
	
	public static Dictionary decRefs(Dictionary obj) {
		obj.refCount--;
		return obj;
	}
	
	/**
	 * The <code>instanceOf</code> method implements a runtime type test. 
	 */
	public static boolean instanceOf(Object obj, Type t) {
		switch(t.kind) {
			case Type.K_ANY:
				return true;
			case Type.K_VOID:
				return false;
			case Type.K_NULL:
				return obj == null;
			case Type.K_INT:
				return obj instanceof BigInteger;
			case Type.K_RATIONAL:
				return obj instanceof BigRational;
			case Type.K_STRING:
				return obj instanceof String;
			case Type.K_LIST:
			{
				if(obj instanceof List) {
					List ol = (List) obj;
					Type.List tl = (Type.List) t;
					Type el = tl.element;
					if(el.kind == Type.K_ANY) {
						return true;
					} else if(el.kind == Type.K_VOID) {
						return ol.isEmpty();
					} else {
						for(Object elem : ol) { 
							if(!instanceOf(elem,el)) {
								return false;
							}
						}
						return true;
					}
				}
				break;
			}
			case Type.K_SET:
			{
				if(obj instanceof Set) {
					Set ol = (Set) obj;
					Type.Set tl = (Type.Set) t;
					Type el = tl.element;
					if(el.kind == Type.K_ANY) {
						return true;
					} else if(el.kind == Type.K_VOID) {
						return ol.isEmpty();
					} else {
						for(Object elem : ol) { 
							if(!instanceOf(elem,el)) {
								return false;
							}
						}
						return true;
					}
				}
			}
			case Type.K_DICTIONARY:
			{
				if(obj instanceof Dictionary) {
					Dictionary ol = (Dictionary) obj;
					Type.Dictionary tl = (Type.Dictionary) t;
					Type key = tl.key;
					Type value = tl.value;
					
					if (key.kind == Type.K_ANY && value.kind == Type.K_ANY) {
						return true;						
					} else if(key.kind == Type.K_VOID || value.kind == Type.K_VOID) {
						return ol.isEmpty();
					} else {
						for (java.util.Map.Entry<Object, Object> elem : ol
								.entrySet()) {
							if (!instanceOf(elem.getKey(), key)
									|| !instanceOf(elem.getValue(), value)) {
								return false;
							}
						}
						return true;
					}
				}
				break;
			}
			case Type.K_RECORD:
			{
				if(obj instanceof Record) {
					Record ol = (Record) obj;
					Type.Record tl = (Type.Record) t;
					String[] names = tl.names;
					Type[] types = tl.types;
					for(int i=0;i!=names.length;++i) {
						String name = names[i];
						if(ol.containsKey(name)) {
							Type type = types[i];
							Object val = ol.get(name);						
							if(!instanceOf(val,type)) {
								return false;
							}
						}
					}
					return true;
				}
				break;
			}
			case Type.K_UNION:
			{
				Type.Union un = (Type.Union) t;
				for(Type bound : un.bounds) {
					if(instanceOf(obj,bound)) {
						return true;
					}
				}		
				break;
			}
		}		
		return false;
	}

	public static boolean instanceOf(List ol, Type t) {
		Type.List tl = (Type.List) t;
		Type el = tl.element;
		if(el.kind == Type.K_ANY) {
			return true;
		} else if(el.kind == Type.K_VOID) {
			return ol.isEmpty();
		} else {
			for(Object elem : ol) { 
				if(!instanceOf(elem,el)) {
					return false;
				}
			}
			return true;
		}		
	}
	
	public static boolean instanceOf(Set ol, Type t) {
		Type.Set tl = (Type.Set) t;
		Type el = tl.element;
		if(el.kind == Type.K_ANY) {
			return true;
		} else if(el.kind == Type.K_VOID) {
			return ol.isEmpty();
		} else {
			for(Object elem : ol) { 
				if(!instanceOf(elem,el)) {
					return false;
				}
			}
			return true;
		}
	}
	
	public static boolean instanceOf(Dictionary ol, Type t) {		
		Type.Dictionary tl = (Type.Dictionary) t;
		Type key = tl.key;
		Type value = tl.value;
		
		if (key.kind == Type.K_ANY && value.kind == Type.K_ANY) {
			return true;						
		} else if(key.kind == Type.K_VOID || value.kind == Type.K_VOID) {
			return ol.isEmpty();
		} else {
			for (java.util.Map.Entry<Object, Object> elem : ol
					.entrySet()) {
				if (!instanceOf(elem.getKey(), key)
						|| !instanceOf(elem.getValue(), value)) {
					return false;
				}
			}
			return true;
		}
	}
	
	public static boolean instanceOf(Record obj, Type t) {
		return false;
	}
	
	public static boolean instanceOf(String obj, Type t) {
		return false;
	}
	
	/**
	 * The <code>coerce</code> method forces this object to conform to a given
	 * type.
	 */
	public Object coerce(Object obj, Type t) {
		return obj;
	}
	
}
