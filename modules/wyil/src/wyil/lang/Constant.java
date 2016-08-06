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

import wybs.lang.NameID;

public abstract class Constant implements Comparable<Constant> {

	/**
	 * The Null constant value
	 */
	public static final Null Null = new Null();

	/**
	 * The Bool true constant
	 */
	public static final Bool True = new Constant.Bool(true);
	
	/**
	 * The Bool false constant
	 */
	public static final Bool False = new Constant.Bool(false);
	
	/**
	 * Get the appropriate Bool constant corresponding to a Java boolean.
	 * 
	 * @param f
	 * @return
	 */
	public static final Bool Bool(boolean f) {
		return f ? True : False;
	}
	
	public abstract wyil.lang.Type type();

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
		private final boolean value;
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
		public boolean value() {
			return value;
		}
	}

	public static final class Byte extends Constant {
		private final byte value;
		public Byte(byte value) {
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
		public byte value() {
			return value;
		}
	}

	public static final class Integer extends Constant {
		private final BigInteger value;
		public Integer(BigInteger value) {
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
		public BigInteger value() {
			return value;
		}
	}

	public static final class Array extends Constant {
		private final ArrayList<Constant> values;
		public Array(Collection<Constant> value) {
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
					|| v instanceof Byte || v instanceof Integer) {
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
		public ArrayList<Constant> values() {
			return values;
		}
	}

	public static final class Record extends Constant {
		private final HashMap<String,Constant> values;
		public Record(java.util.Map<String,Constant> value) {
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

		public HashMap<String,Constant> values() {
			return values;
		}
	}

	public static final class Type extends Constant {
		private final wyil.lang.Type value;
		public Type(wyil.lang.Type type) {
			this.value = type;
		}
		public wyil.lang.Type.Meta type() {
			return wyil.lang.Type.T_META;
		}
		public int hashCode() {
			return value.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Type) {
				Type i = (Type) o;
				return value == i.value;
			}
			return false;
		}
		public int compareTo(Constant v) {
			if(v instanceof Type) {
				Type t = (Type) v;
				// FIXME: following is an ugly hack!
				return value.toString().compareTo(t.toString());
			} else {
				return 1; // everything is above a type constant
			}
		}
		public String toString() {
			return value.toString();
		}
		public wyil.lang.Type value() {
			return value;
		}
	}

	/**
	 * Represents a named function or method. This is used when taking the
	 * address of a function or method.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static final class FunctionOrMethod extends Constant {
		private final NameID name;
		private final wyil.lang.Type.FunctionOrMethod type;
		private final ArrayList<Constant> arguments;
		
		public FunctionOrMethod(NameID name, wyil.lang.Type.FunctionOrMethod type, Constant... arguments) {
			this.name = name;
			this.type = type;
			this.arguments = new ArrayList<Constant>();
			for(int i=0;i!=arguments.length;++i) {
				this.arguments.add(arguments[i]);
			}
		}

		public FunctionOrMethod(NameID name, wyil.lang.Type.FunctionOrMethod type, Collection<Constant> arguments) {
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
			if(o instanceof FunctionOrMethod) {
				FunctionOrMethod i = (FunctionOrMethod) o;
				return name.equals(i.name)
						&& (type == i.type || (type != null && type
								.equals(i.type))) && arguments.equals(i.arguments);
			}
			return false;
		}
		public int compareTo(Constant v) {
			if(v instanceof FunctionOrMethod) {
				FunctionOrMethod t = (FunctionOrMethod) v;
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

		public NameID name() {
			return name;
		}

		public ArrayList<Constant> arguments() {
			return arguments;
		}
	}
}
