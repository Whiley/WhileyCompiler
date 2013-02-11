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

package wycs.lang;

import java.math.BigInteger;
import java.util.*;

import wybs.util.Pair;
import wyautl.util.BigRational;

public abstract class Value implements Comparable<Value> {	

	public static final Null Null = new Null();
	
	public static Bool Bool(boolean value) {
		return get(new Bool(value));
	}
	
	public static Rational Rational(BigRational value) {
		return get(new Rational(value));
	}	

	public static Integer Integer(BigInteger value) {
		return get(new Integer(value));
	}
	
	public static Set Set(Collection<Value> values) {
		return get(new Set(values));
	}

	public static List List(Collection<Value> values) {
		return get(new List(values));
	}	
	
	public static Record Record(java.util.Map<String,Value> values) {
		return get(new Record(values));
	}

	public static Map Map(
			java.util.Set<Pair<Value, Value>> values) {
		return get(new Map(values));
	}

	public static Map Map(java.util.Map<Value, Value> values) {
		return get(new Map(values));
	}
	
	public static Tuple Tuple(Collection<Value> values) {
		return get(new Tuple(values));
	}
	
	public static final class Null extends Value {						
		public int hashCode() {
			return 0;
		}
		public boolean equals(Object o) {			
			return o instanceof Null;
		}
		public String toString() {
			return "null";
		}
		public int compareTo(Value v) {
			if(v instanceof Null) {
				return 0;
			} else {
				return 1; // everything is above null
			}
		}
	}
	
	public static final class Bool extends Value {
		public final boolean value;
		private Bool(boolean value) {
			this.value = value;
		}		
		public int hashCode() {
			return value ? 1 : 0;
		}
		public boolean equals(Object o) {
			if(o instanceof Bool) {
				Bool i = (Bool) o;
				return value == i.value;
			}
			return false;
		}
		public int compareTo(Value v) {
			if(v instanceof Bool) {
				Bool b = (Bool) v;
				if(value == b.value) {
					return 0;
				} else if(value) {
					return 1;
				} 
			} else if(v instanceof Null) {
				return 1; 
			} 
			return -1;			
		}
		public String toString() {
			if(value) { return "true"; }
			else {
				return "false";
			}
		}		
	}
		
	public static final class Rational extends Value {
		public final BigRational value;
		private Rational(BigRational value) {
			this.value = value;
		}		
		public int hashCode() {
			return value.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Rational) {
				Rational i = (Rational) o;
				return value.equals(i.value);
			}
			return false;
		}
		public int compareTo(Value v) {
			if(v instanceof Rational) {
				Rational i = (Rational) v;
				return value.compareTo(i.value); 
			} else if(v instanceof Null || v instanceof Bool || v instanceof Integer) {
				return 1; 
			} 
			return -1;			
		}
		public String toString() {
			return value.toString();
		}
		
		public Value.Rational add(Value.Rational val) {
			return Value.Rational(value.add(val.value));
		}
		public Value.Rational subtract(Value.Rational val) {
			return Value.Rational(value.subtract(val.value));
		}
		public Value.Rational multiply(Value.Rational val) {
			return Value.Rational(value.multiply(val.value));
		}
		public Value.Rational divide(Value.Rational val) {
			return Value.Rational(value.divide(val.value));
		}		
	}		
		
	public static final class Integer extends Value {
		public final BigInteger value;
		private Integer(BigInteger value) {
			this.value = value;
		}
		public int hashCode() {
			return value.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Integer) {
				Integer i = (Integer) o;
				return value.equals(i.value);
			}
			return false;
		}
		public int compareTo(Value v) {
			if(v instanceof Integer) {
				Integer i = (Integer) v;
				return value.compareTo(i.value); 
			} else if(v instanceof Null || v instanceof Bool) {
				return 1; 
			} 
			return -1;			
		}
		public String toString() {
			return value.toString();
		}
		
		public Value.Integer add(Value.Integer val) {
			return Value.Integer(value.add(val.value));
		}
		public Value.Integer subtract(Value.Integer val) {
			return Value.Integer(value.subtract(val.value));
		}
		public Value.Integer multiply(Value.Integer val) {
			return Value.Integer(value.multiply(val.value));
		}
		public Value.Integer divide(Value.Integer val) {
			return Value.Integer(value.divide(val.value));
		}		
		public Value.Integer remainder(Value.Integer val) {
			return Value.Integer(value.remainder(val.value));
		}
	}
	
	public static final class List extends Value {
		public final ArrayList<Value> values;
		private List(Collection<Value> value) {
			this.values = new ArrayList<Value>(value);
		}		
		public int hashCode() {
			return values.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof List) {
				List i = (List) o;
				return values.equals(i.values);
			}
			return false;
		}
		public int compareTo(Value v) {
			if(v instanceof List) {
				List l = (List) v;
				if(values.size() < l.values.size()) {
					return -1;
				} else if(values.size() > l.values.size()) {
					return 1;
				} else {
					for(int i=0;i!=values.size();++i) {
						Value v1 = values.get(i);
						Value v2 = l.values.get(i);
						int c = v1.compareTo(v2);
						if(c != 0) { return c; }
					}
					return 0;
				}
			} else if (v instanceof Null || v instanceof Bool
					|| v instanceof Rational || v instanceof Integer) {
				return 1; 
			} 
			return -1;			
		}
		public String toString() {
			String r = "[";
			boolean firstTime=true;
			for(Value v : values) {
				if(!firstTime) {
					r += ",";
				}
				firstTime=false;
				r += v;
			}
			return r + "]";
		}
	}
	
	public static final class Set extends Value {
		public final HashSet<Value> values;
		private Set() {
			this.values = new HashSet<Value>();
		}
		private Set(Collection<Value> value) {
			this.values = new HashSet<Value>(value);
		}		
		public int hashCode() {
			return values.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Set) {
				Set i = (Set) o;
				return values.equals(i.values);
			}
			return false;
		}
		public int compareTo(Value v) {
			if(v instanceof Set) {
				Set l = (Set) v;
				if(values.size() < l.values.size()) {
					return -1;
				} else if(values.size() > l.values.size()) {
					return 1;
				} else {
					// this case is slightly awkward, since we can't rely on the
					// iteration order for HashSet.
					ArrayList<Value> vs1 = new ArrayList<Value>(values);
					ArrayList<Value> vs2 = new ArrayList<Value>(l.values);
					Collections.sort(vs1);
					Collections.sort(vs2);
					for(int i=0;i!=values.size();++i) {
						Value v1 = vs1.get(i);
						Value v2 = vs2.get(i);
						int c = v1.compareTo(v2);
						if(c != 0) { return c; }
					}
					return 0;
				}
			} else if (v instanceof Null || v instanceof Bool
					|| v instanceof Rational || v instanceof Integer
					|| v instanceof List || v instanceof Tuple) {
				return 1;
			}
			return -1;			
		}
		public String toString() {
			String r = "{";
			boolean firstTime=true;
			for(Value v : values) {
				if(!firstTime) {
					r += ",";
				}
				firstTime=false;
				r += v;
			}
			return r + "}";
		}
		
		public Set union(Set rhs) {
			Value.Set nset = new Value.Set(values);
			nset.values.addAll(rhs.values);
			return nset;
			
		}
		
		public Set add(Value val) {
			Value.Set nset = new Value.Set(values);
			nset.values.add(val);
			return nset;
			
		}
		
		public Set difference(Set rhs) {
			Value.Set nset = new Value.Set(values);
			nset.values.removeAll(rhs.values);
			return nset;
		}
		
		public Set remove(Value val) {
			Value.Set nset = new Value.Set(values);
			nset.values.remove(val);
			return nset;
			
		}
		
		public Set intersect(Set rhs) {
			Value.Set nset = new Value.Set();
			for(Value v : values) {
				if(rhs.values.contains(v)) {
					nset.values.add(v);
				}
			}			
			return nset;
		}		
	}
	
	public static final class Record extends Value {
		public final HashMap<String,Value> values;
		private Record(java.util.Map<String,Value> value) {
			this.values = new HashMap<String,Value>(value);
		}
		
		public int hashCode() {
			return values.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Record) {
				Record i = (Record) o;
				return values.equals(i.values);
			}
			return false;
		}
		public int compareTo(Value v) {
			if(v instanceof Record) {
				Record l = (Record) v;
				if(values.size() < l.values.size()) {
					return -1;
				} else if(values.size() > l.values.size()) {
					return 1;
				} else {
					ArrayList<String> vs1 = new ArrayList<String>(values.keySet());
					ArrayList<String> vs2 = new ArrayList<String>(l.values.keySet());
					Collections.sort(vs1);
					Collections.sort(vs2);
					for(int i=0;i!=values.size();++i) {
						String s1 = vs1.get(i);
						String s2 = vs2.get(i);
						int c = s1.compareTo(s2);
						if(c != 0) { return c; }
						Value v1 = values.get(s1);
						Value v2 = l.values.get(s1);
						c = v1.compareTo(v2);
						if(c != 0) { return c; }
					}
					return 0;
				}
			} else if (v instanceof Null || v instanceof Bool
					|| v instanceof Rational || v instanceof Integer
					|| v instanceof Set || v instanceof List || v instanceof Tuple) {
				return 1; 
			} 
			return -1;			
		}
		public String toString() {
			String r = "{";
			boolean firstTime=true;
			ArrayList<String> keys = new ArrayList<String>(values.keySet());
			Collections.sort(keys);
			for(String key : keys) {
				if(!firstTime) {
					r += ",";
				}
				firstTime=false;
				r += key + ":=" + values.get(key);
			}
			return r + "}";
		}
	}
	
	public static final class Map extends Value {
		public final HashMap<Value,Value> values;
		private Map(java.util.Map<Value,Value> value) {
			this.values = new HashMap<Value,Value>(value);
		}
		private Map(java.util.Set<Pair<Value,Value>> values) {
			this.values = new HashMap<Value,Value>();
			for(Pair<Value,Value> p : values) {
				this.values.put(p.first(), p.second());
			}
		}
		public int hashCode() {
			return values.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Map) {
				Map i = (Map) o;
				return values.equals(i.values);
			}
			return false;
		}
		public int compareTo(Value v) {
			if(v instanceof Map) {
				Map l = (Map) v;
				if(values.size() < l.values.size()) {
					return -1;
				} else if(values.size() > l.values.size()) {
					return 1;
				} else {
					ArrayList<Value> vs1 = new ArrayList<Value>(values.keySet());
					ArrayList<Value> vs2 = new ArrayList<Value>(l.values.keySet());
					Collections.sort(vs1);
					Collections.sort(vs2);
					for(int i=0;i!=values.size();++i) {
						Value k1 = vs1.get(i);
						Value k2 = vs2.get(i);
						int c = k1.compareTo(k2);
						if(c != 0) { return c; }
						Value v1 = values.get(k1);
						Value v2 = l.values.get(k1);
						c = v1.compareTo(v2);
						if(c != 0) { return c; }
					}
					return 0;
				}
			} else if (v instanceof Null || v instanceof Bool
					|| v instanceof Rational || v instanceof Integer
					|| v instanceof Set || v instanceof List || v instanceof Tuple
					|| v instanceof Record) {
				return 1;
			}
			return -1;			
		}
		public String toString() {
			String r = "{";
			boolean firstTime=true;
			ArrayList<String> keystr = new ArrayList<String>();
			HashMap<String,Value> keymap = new HashMap<String,Value>();
			for(Value key : values.keySet()) {
				keystr.add(key.toString());
				keymap.put(key.toString(), key);
			}
			Collections.sort(keystr);
			for(String key : keystr) {
				if(!firstTime) {
					r += ",";
				}
				firstTime=false;
				Value k = keymap.get(key); 
				r += k + "->" + values.get(k);
			}
			return r + "}";
		}
	}
		
	public static final class Tuple extends Value {
		public final ArrayList<Value> values;
		private Tuple(Collection<Value> values) {
			this.values = new ArrayList<Value>(values);
		}

		public int hashCode() {
			return values.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Tuple) {
				Tuple i = (Tuple) o;
				return values.equals(i.values);
			}
			return false;
		}
		public int compareTo(Value v) {
			if(v instanceof Tuple) {
				Tuple l = (Tuple) v;
				if(values.size() < l.values.size()) {
					return -1;
				} else if(values.size() > l.values.size()) {
					return 1;
				} else {
					ArrayList<Value> vs1 = values;
					ArrayList<Value> vs2 = l.values;
					for(int i=0;i!=values.size();++i) {
						Value s1 = vs1.get(i);
						Value s2 = vs2.get(i);
						int c = s1.compareTo(s2);
						if(c != 0) { return c; }						
					}
					return 0;
				}
			} else if (v instanceof Null || v instanceof Bool
					|| v instanceof Rational || v instanceof Integer
					|| v instanceof Set || v instanceof List) {
				return 1; 
			} 
			return -1;			
		}
		public String toString() {
			String r = "(";
			boolean firstTime=true;			
			for(Value v : values) {
				if(!firstTime) {
					r += ",";
				}
				firstTime=false;
				r += v;
			}
			return r + ")";
		}
	}
		
	private static final ArrayList<Value> values = new ArrayList<Value>();
	private static final HashMap<Value,java.lang.Integer> cache = new HashMap<Value,java.lang.Integer>();
	
	private static <T extends Value> T get(T type) {
		java.lang.Integer idx = cache.get(type);
		if(idx != null) {
			return (T) values.get(idx);
		} else {					
			cache.put(type, values.size());
			values.add(type);
			return type;
		}
	}
}
