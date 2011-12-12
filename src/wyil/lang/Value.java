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

package wyil.lang;

import java.math.BigInteger;
import java.util.*;

import wyil.util.Pair;
import wyjc.runtime.BigRational;

public abstract class Value implements Comparable<Value> {	

	public static final Null V_NULL = new Null();

	public abstract Type type();
	
	public static Bool V_BOOL(boolean value) {
		return get(new Bool(value));
	}
	
	public static Byte V_BYTE(byte value) {
		return get(new Byte(value));
	}
	
	public static Char V_CHAR(char value) {
		return get(new Char(value));
	}
	
	public static Rational V_RATIONAL(BigRational value) {
		return get(new Rational(value));
	}	

	public static Integer V_INTEGER(BigInteger value) {
		return get(new Integer(value));
	}
	
	public static Strung V_STRING(String value) {
		return get(new Strung(value));
	}
	
	public static Set V_SET(Collection<Value> values) {
		return get(new Set(values));
	}

	public static List V_LIST(Collection<Value> values) {
		return get(new List(values));
	}	
	
	public static Record V_RECORD(Map<String,Value> values) {
		return get(new Record(values));
	}

	public static Dictionary V_DICTIONARY(
			java.util.Set<Pair<Value, Value>> values) {
		return get(new Dictionary(values));
	}

	public static Dictionary V_DICTIONARY(Map<Value, Value> values) {
		return get(new Dictionary(values));
	}
	
	public static TypeConst V_TYPE(Type type) {
		return get(new TypeConst(type));
	}
	
	public static Tuple V_TUPLE(Collection<Value> values) {
		return get(new Tuple(values));
	}
	
	public static Function V_FUN(NameID name, Type.Function type) {
		return get(new Function(name,type));
	}		
	
	public static final class Null extends Value {				
		public Type type() {
			return Type.T_NULL;
		}
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
		public Type type() {
			return Type.T_BOOL;
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
		public Type type() {				
			return Type.T_REAL;			
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
			} else if(v instanceof Null || v instanceof Bool || v instanceof Byte || v instanceof Char || v instanceof Integer) {
				return 1; 
			} 
			return -1;			
		}
		public String toString() {
			return value.toString();
		}
		
		public Value.Rational add(Value.Rational val) {
			return Value.V_RATIONAL(value.add(val.value));
		}
		public Value.Rational subtract(Value.Rational val) {
			return Value.V_RATIONAL(value.subtract(val.value));
		}
		public Value.Rational multiply(Value.Rational val) {
			return Value.V_RATIONAL(value.multiply(val.value));
		}
		public Value.Rational divide(Value.Rational val) {
			return Value.V_RATIONAL(value.divide(val.value));
		}		
	}		
	
	public static final class Byte extends Value {
		public final byte value;
		private Byte(byte value) {
			this.value = value;
		}
		public Type type() {				
			return Type.T_BYTE;			
		}
		public int hashCode() {
			return value;
		}
		public boolean equals(Object o) {
			if(o instanceof Byte) {
				Byte i = (Byte) o;
				return value == i.value;
			}
			return false;
		}
		public int compareTo(Value v) {
			if(v instanceof Byte) {
				Byte i = (Byte) v;
				if(value < i.value) {
					return -1;
				} else if(value > i.value) {
					return 1;
				} else {
					return 0;
				}
			} else if(v instanceof Null || v instanceof Bool) {
				return 1; 
			} 
			return -1;			
		}
		public String toString() {
			String r = "b";
			byte v = value;
			for(int i=0;i!=8;++i) {
				if((v&0x1) == 1) {
					r = "1" + r;
				} else {
					r = "0" + r;
				}
				v = (byte) (v >>> 1);
			}
			return r;			
		}
	}
	
	public static final class Char extends Value {
		public final char value;
		private Char(char value) {
			this.value = value;
		}
		public Type type() {				
			return Type.T_CHAR;			
		}
		public int hashCode() {
			return value;
		}
		public boolean equals(Object o) {
			if(o instanceof Char) {
				Char i = (Char) o;
				return value == i.value;
			}
			return false;
		}
		public int compareTo(Value v) {
			if(v instanceof Char) {
				Char i = (Char) v;
				if(value < i.value) {
					return -1;
				} else if(value > i.value) {
					return 1;
				} else {
					return 0;
				}
			} else if (v instanceof Null || v instanceof Bool
					|| v instanceof Byte) {
				return 1; 
			} 
			return -1;			
		}
		public String toString() {
			return "'" + value + "'";			
		}
	}
	
	public static final class Integer extends Value {
		public final BigInteger value;
		private Integer(BigInteger value) {
			this.value = value;
		}
		public Type type() {				
			return Type.T_INT;			
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
			} else if(v instanceof Null || v instanceof Byte || v instanceof Char || v instanceof Bool) {
				return 1; 
			} 
			return -1;			
		}
		public String toString() {
			return value.toString();
		}
		
		public Value.Integer add(Value.Integer val) {
			return Value.V_INTEGER(value.add(val.value));
		}
		public Value.Integer subtract(Value.Integer val) {
			return Value.V_INTEGER(value.subtract(val.value));
		}
		public Value.Integer multiply(Value.Integer val) {
			return Value.V_INTEGER(value.multiply(val.value));
		}
		public Value.Integer divide(Value.Integer val) {
			return Value.V_INTEGER(value.divide(val.value));
		}		
		public Value.Integer remainder(Value.Integer val) {
			return Value.V_INTEGER(value.remainder(val.value));
		}
	}
	
	public static final class Strung extends Value {
		public final String value;
		private Strung(String value) {
			this.value = value;
		}
		public Type type() {
			return Type.T_STRING;
		}
		public int hashCode() {
			return value.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Strung) {
				Strung i = (Strung) o;
				return value.equals(i.value);
			}
			return false;
		}
		public int compareTo(Value v) {
			if(v instanceof Strung) {
				Strung i = (Strung) v;
				return value.compareTo(i.value); 
			} else if(v instanceof Null || v instanceof Bool || v instanceof Rational || v instanceof Byte || v instanceof Char || v instanceof Integer) {
				return 1; 
			} 
			return -1;			
		}
		public String toString() {
			return "\"" + value.toString() + "\"";
		}
	}
	
	public static final class List extends Value {
		public final ArrayList<Value> values;
		private List(Collection<Value> value) {
			this.values = new ArrayList<Value>(value);
		}
		public Type type() {
			Type t = Type.T_VOID;
			for(Value arg : values) {
				t = Type.Union(t,arg.type());
			}
			return Type.List(t, !values.isEmpty());			
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
					|| v instanceof Rational || v instanceof Byte || v instanceof Char || v instanceof Integer || v instanceof Strung) {
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
		public Type type() {
			Type t = Type.T_VOID;
			for(Value arg : values) {
				t = Type.Union(t,arg.type());
			}
			return Type.Set(t, !values.isEmpty());	
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
					|| v instanceof Rational || v instanceof Byte || v instanceof Char || v instanceof Integer || v instanceof Strung
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
		private Record(Map<String,Value> value) {
			this.values = new HashMap<String,Value>(value);
		}

		public Type type() {
			HashMap<String, Type> types = new HashMap<String, Type>();
			for (Map.Entry<String, Value> e : values.entrySet()) {
				types.put(e.getKey(), e.getValue().type());
			}
			return Type.Record(false,types);
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
					|| v instanceof Rational || v instanceof Byte || v instanceof Char || v instanceof Integer || v instanceof Strung
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
	
	public static final class Dictionary extends Value {
		public final HashMap<Value,Value> values;
		private Dictionary(Map<Value,Value> value) {
			this.values = new HashMap<Value,Value>(value);
		}
		private Dictionary(java.util.Set<Pair<Value,Value>> values) {
			this.values = new HashMap<Value,Value>();
			for(Pair<Value,Value> p : values) {
				this.values.put(p.first(), p.second());
			}
		}
		public Type type() {
			Type key = Type.T_VOID;
			Type value = Type.T_VOID;
			for (Map.Entry<Value, Value> e : values.entrySet()) {
				key = Type.Union(key,e.getKey().type());
				value = Type.Union(value,e.getKey().type());
			}
			return Type.Dictionary(key,value);
		}
		public int hashCode() {
			return values.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Dictionary) {
				Dictionary i = (Dictionary) o;
				return values.equals(i.values);
			}
			return false;
		}
		public int compareTo(Value v) {
			if(v instanceof Dictionary) {
				Dictionary l = (Dictionary) v;
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
					|| v instanceof Rational || v instanceof Byte || v instanceof Char || v instanceof Integer || v instanceof Strung
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
	
	public static final class TypeConst extends Value {
		public final Type type;
		private TypeConst(Type type) {
			this.type = type;
		}
		public Type type() {
			return Type.T_META;
		}
		public int hashCode() {
			return type.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof TypeConst) {
				TypeConst i = (TypeConst) o;
				return type == i.type;
			}
			return false;
		}
		public int compareTo(Value v) {
			if(v instanceof TypeConst) {
				TypeConst t = (TypeConst) v;
				// FIXME: following is an ugly hack!
				return type.toString().compareTo(t.toString());
			} else {
				return 1; // everything is above a type constant
			}					
		}
		public String toString() {
			return type.toString();
		}
	}
	
	public static final class Function extends Value {
		public final NameID name;
		public final Type.Function type;
		
		private Function(NameID name, Type.Function type) {
			this.name = name;
			this.type = type;
		}
		public Type type() {
			if (type == null) {				
				return Type.Function(Type.T_ANY, Type.T_ANY);
			} else {
				return type;
			}
		}
		public int hashCode() {
			if(type != null) {
				return type.hashCode() + name.hashCode();
			} else {
				return name.hashCode();
			}
		}
		public boolean equals(Object o) {
			if(o instanceof Function) {
				Function i = (Function) o;
				return name.equals(i.name)
						&& (type == i.type || (type != null && type
								.equals(i.type)));
			}
			return false;
		}
		public int compareTo(Value v) {
			if(v instanceof Function) {
				Function t = (Function) v;
				// FIXME: following is an ugly hack!
				return type.toString().compareTo(t.toString());
			} else {
				return 1; // everything is above a type constant
			}					
		}
		public String toString() {
			return "&" + name.toString() + ":" + type.toString();
		}
	}
	
	public static final class Tuple extends Value {
		public final ArrayList<Value> values;
		private Tuple(Collection<Value> values) {
			this.values = new ArrayList<Value>(values);
		}

		public Type type() {
			ArrayList<Type> types = new ArrayList<Type>();			
			for (Value e : values) {
				types.add(e.type());				
			}
			return Type.Tuple(types);
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
					|| v instanceof Rational || v instanceof Byte || v instanceof Char || v instanceof Integer || v instanceof Strung
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
