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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import wycc.lang.NameID;
import wycc.util.Pair;
import wyautl.util.BigRational;

public abstract class Constant implements Comparable<Constant> {

	public static final Null V_NULL = new Null();

	public abstract wyil.lang.Type type();

	public static Bool V_BOOL(boolean value) {
		return get(new Bool(value));
	}

	public static Byte V_BYTE(byte value) {
		return get(new Byte(value));
	}

	public static Decimal V_DECIMAL(BigDecimal value) {
		return get(new Decimal(value));
	}

	public static Rational V_RATIONAL(BigRational value) {
		return get(new Rational(value));
	}
	
	public static Integer V_INTEGER(BigInteger value) {
		return get(new Integer(value));
	}

	public static Array V_ARRAY(Collection<Constant> values) {
		return get(new Array(values));
	}

	public static Record V_RECORD(java.util.Map<String,Constant> values) {
		return get(new Record(values));
	}

	public static Type V_TYPE(wyil.lang.Type type) {
		return get(new Type(type));
	}

	public static Lambda V_LAMBDA(NameID name,
			wyil.lang.Type.FunctionOrMethod type, Constant... arguments) {
		return get(new Lambda(name, type, arguments));
	}

	
	public static final class Null extends Constant {
		public wyil.lang.Type type() {
			return wyil.lang.Type.T_NULL;
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
		public int compareTo(Constant v) {
			if(v instanceof Null) {
				return 0;
			} else {
				return 1; // everything is above null
			}
		}
	}

	public static final class Bool extends Constant {
		public final boolean value;
		private Bool(boolean value) {
			this.value = value;
		}
		public wyil.lang.Type type() {
			return wyil.lang.Type.T_BOOL;
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
		public int compareTo(Constant v) {
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

	public static final class Byte extends Constant {
		public final byte value;
		private Byte(byte value) {
			this.value = value;
		}
		public wyil.lang.Type type() {
			return wyil.lang.Type.T_BYTE;
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
		public int compareTo(Constant v) {
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

	public static final class Integer extends Constant {
		public final BigInteger value;
		private Integer(BigInteger value) {
			this.value = value;
		}
		public wyil.lang.Type type() {
			return wyil.lang.Type.T_INT;
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
		public int compareTo(Constant v) {
			if(v instanceof Integer) {
				Integer i = (Integer) v;
				return value.compareTo(i.value);
			} else if(v instanceof Null || v instanceof Byte || v instanceof Bool) {
				return 1;
			}
			return -1;
		}
		public String toString() {
			return value.toString();
		}

		public Constant.Integer add(Constant.Integer val) {
			return Constant.V_INTEGER(value.add(val.value));
		}
		public Constant.Integer subtract(Constant.Integer val) {
			return Constant.V_INTEGER(value.subtract(val.value));
		}
		public Constant.Integer multiply(Constant.Integer val) {
			return Constant.V_INTEGER(value.multiply(val.value));
		}
		public Constant.Integer divide(Constant.Integer val) {
			return Constant.V_INTEGER(value.divide(val.value));
		}
		public Constant.Integer remainder(Constant.Integer val) {
			return Constant.V_INTEGER(value.remainder(val.value));
		}
		public Constant.Integer negate() {
			return Constant.V_INTEGER(value.negate());
		}
	}

	public static final class Decimal extends Constant {
		public final BigDecimal value;

		private Decimal(BigDecimal value) {
			this.value = value;
		}

		public wyil.lang.Type type() {
			return wyil.lang.Type.T_REAL;
		}

		public int hashCode() {
			return value.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof Decimal) {
				Decimal i = (Decimal) o;
				return value.equals(i.value);
			}
			return false;
		}

		public int compareTo(Constant v) {
			if (v instanceof Decimal) {
				Decimal i = (Decimal) v;
				return value.compareTo(i.value);
			} else if (v instanceof Null || v instanceof Bool
					|| v instanceof Byte || v instanceof Integer) {
				return 1;
			}
			return -1;
		}

		public String toString() {
			String r = value.toString();
			// We need to force the string to include the decimal point.
			if(!r.contains(".")) {
				return r + ".0";
			} else {
				return r;
			}
		}

		public Constant.Decimal add(Constant.Decimal val) {
			return Constant.V_DECIMAL(value.add(val.value));
		}

		public Constant.Decimal subtract(Constant.Decimal val) {
			return Constant.V_DECIMAL(value.subtract(val.value));
		}

		public Constant.Decimal multiply(Constant.Decimal val) {
			return Constant.V_DECIMAL(value.multiply(val.value));
		}

		public Constant.Decimal divide(Constant.Decimal val) {
			return Constant.V_DECIMAL(value.divide(val.value));
		}

		public Constant.Decimal negate() {
			return Constant.V_DECIMAL(value.negate());
		}
	}

	public static final class Rational extends Constant {
		public final BigRational value;

		private Rational(BigRational value) {
			this.value = value;
		}

		public wyil.lang.Type type() {
			return wyil.lang.Type.T_REAL;
		}

		public int hashCode() {
			return value.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof Rational) {
				Rational i = (Rational) o;
				return value.equals(i.value);
			}
			return false;
		}

		public int compareTo(Constant v) {
			if (v instanceof Rational) {
				Rational i = (Rational) v;
				return value.compareTo(i.value);
			} else if (v instanceof Null || v instanceof Bool
					|| v instanceof Byte || v instanceof Integer
					|| v instanceof Decimal) {
				return 1;
			}
			return -1;
		}

		public String toString() {
			return value.toString();
		}

		public Constant.Rational add(Constant.Rational val) {
			return Constant.V_RATIONAL(value.add(val.value));
		}

		public Constant.Rational subtract(Constant.Rational val) {
			return Constant.V_RATIONAL(value.subtract(val.value));
		}

		public Constant.Rational multiply(Constant.Rational val) {
			return Constant.V_RATIONAL(value.multiply(val.value));
		}

		public Constant.Rational divide(Constant.Rational val) {
			return Constant.V_RATIONAL(value.divide(val.value));
		}

		public Constant.Rational negate() {
			return Constant.V_RATIONAL(value.negate());
		}
	}
	
	public static final class Array extends Constant {
		public final ArrayList<Constant> values;
		private Array(Collection<Constant> value) {
			this.values = new ArrayList<Constant>(value);
		}
		public wyil.lang.Type.Array type() {
			wyil.lang.Type t = wyil.lang.Type.T_VOID;
			for(Constant arg : values) {
				t = wyil.lang.Type.Union(t,arg.type());
			}
			return wyil.lang.Type.Array(t, !values.isEmpty());
		}
		public int hashCode() {
			return values.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Array) {
				Array i = (Array) o;
				return values.equals(i.values);
			}
			return false;
		}
		public int compareTo(Constant v) {
			if(v instanceof Array) {
				Array l = (Array) v;
				if(values.size() < l.values.size()) {
					return -1;
				} else if(values.size() > l.values.size()) {
					return 1;
				} else {
					for(int i=0;i!=values.size();++i) {
						Constant v1 = values.get(i);
						Constant v2 = l.values.get(i);
						int c = v1.compareTo(v2);
						if(c != 0) { return c; }
					}
					return 0;
				}
			} else if (v instanceof Null || v instanceof Bool
					|| v instanceof Byte || v instanceof Integer
					|| v instanceof Decimal || v instanceof Rational) {
				return 1;
			}
			return -1;
		}
		public String toString() {
			String r = "[";
			boolean firstTime=true;
			for(Constant v : values) {
				if(!firstTime) {
					r += ",";
				}
				firstTime=false;
				r += v;
			}
			return r + "]";
		}
	}

	public static final class Record extends Constant {
		public final HashMap<String,Constant> values;
		private Record(java.util.Map<String,Constant> value) {
			this.values = new HashMap<String,Constant>(value);
		}

		public wyil.lang.Type.Record type() {
			HashMap<String, wyil.lang.Type> types = new HashMap<String, wyil.lang.Type>();
			for (java.util.Map.Entry<String, Constant> e : values.entrySet()) {
				types.put(e.getKey(), e.getValue().type());
			}
			return wyil.lang.Type.Record(false,types);
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
		public int compareTo(Constant v) {
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
						Constant v1 = values.get(s1);
						Constant v2 = l.values.get(s1);
						c = v1.compareTo(v2);
						if(c != 0) { return c; }
					}
					return 0;
				}
			} else if (v instanceof Null || v instanceof Bool
					|| v instanceof Decimal || v instanceof Rational
					|| v instanceof Byte || v instanceof Integer
					|| v instanceof Set || v instanceof Array) {
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

	public static final class Type extends Constant {
		public final wyil.lang.Type type;
		private Type(wyil.lang.Type type) {
			this.type = type;
		}
		public wyil.lang.Type.Meta type() {
			return wyil.lang.Type.T_META;
		}
		public int hashCode() {
			return type.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Type) {
				Type i = (Type) o;
				return type == i.type;
			}
			return false;
		}
		public int compareTo(Constant v) {
			if(v instanceof Type) {
				Type t = (Type) v;
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

	public static final class Lambda extends Constant {
		public final NameID name;
		public final wyil.lang.Type.FunctionOrMethod type;
		public final ArrayList<Constant> arguments;
		
		private Lambda(NameID name, wyil.lang.Type.FunctionOrMethod type, Constant... arguments) {
			this.name = name;
			this.type = type;
			this.arguments = new ArrayList<Constant>();
			for(int i=0;i!=arguments.length;++i) {
				this.arguments.add(arguments[i]);
			}
		}

		private Lambda(NameID name, wyil.lang.Type.FunctionOrMethod type, Collection<Constant> arguments) {
			this.name = name;
			this.type = type;
			this.arguments = new ArrayList<Constant>(arguments);
		}
		
		public wyil.lang.Type.FunctionOrMethod type() {
			if (type == null) {
				return wyil.lang.Type.Function(new wyil.lang.Type[] { wyil.lang.Type.T_ANY }, wyil.lang.Type.T_ANY);
			} else {
				return type;
			}
		}
		public int hashCode() {
			if(type != null) {
				return type.hashCode() + name.hashCode() + arguments.hashCode();
			} else {
				return name.hashCode();
			}
		}
		public boolean equals(Object o) {
			if(o instanceof Lambda) {
				Lambda i = (Lambda) o;
				return name.equals(i.name)
						&& (type == i.type || (type != null && type
								.equals(i.type))) && arguments.equals(i.arguments);
			}
			return false;
		}
		public int compareTo(Constant v) {
			if(v instanceof Lambda) {
				Lambda t = (Lambda) v;
				// FIXME: following is an ugly hack!
				return type.toString().compareTo(t.toString());
			} else {
				return 1; // everything is above a type constant
			}
		}
		public String toString() {			
			String args = "";
			boolean firstTime=true;
			for(Constant arg : arguments) {
				if(!firstTime) {
					args += ",";
				}
				firstTime=false;
				if(arg == null) {
					args += "_";
				} else {
					args += arg.toString();
				}
				
			}
			return "&" + name.toString() + "(" + args + "):" + type.toString();
		}
	}

	private static final ArrayList<Constant> values = new ArrayList<Constant>();
	private static final HashMap<Constant,java.lang.Integer> cache = new HashMap<Constant,java.lang.Integer>();

	private static <T extends Constant> T get(T type) {
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
