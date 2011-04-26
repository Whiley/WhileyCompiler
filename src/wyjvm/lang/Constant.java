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

package wyjvm.lang;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

public class Constant {
	
	/**
	 * This method creates a CONSTANT_info object from a String
	 * 
	 * @param c
	 * @return
	 */
	public static Constant.String fromString(java.lang.String c) {		
		return new Constant.String(new Utf8(c));
	}
	
	/**
	 * This method creates a CONSTANT_info object from a Number object.
	 * 
	 * @param c
	 * @return
	 */
	public static Constant.Info fromNumber(Number c) {
		if(c instanceof java.lang.Integer) {
			return new Constant.Integer((java.lang.Integer)c);
		} else if(c instanceof java.lang.Long) {
			return new Constant.Long((java.lang.Long)c);
		} else if(c instanceof java.lang.Float) {
			return new Constant.Float((java.lang.Float)c);
		} else if(c instanceof java.lang.Double) {
			return new Constant.Double((java.lang.Double)c);
		} 
		throw new RuntimeException("Shouldn't get here!");
	}
	
	/**
	 * This method creates a CONSTANT_Class object from a Type Reference
	 * 
	 * @param c
	 * @return
	 */
	public static Constant.Class buildClass(JvmType.Reference r) {
		if (r instanceof JvmType.Array) {
			return buildClass((JvmType.Array) r);
		} else if (r instanceof JvmType.Clazz) {
			return buildClass((JvmType.Clazz) r);
		} else {
			throw new IllegalArgumentException(
					"buildClass() cannot accept reference type " + r);
		}
	}
	
	/**
	 * This method creates a CONSTANT_Class object from a Type Clazz
	 * 
	 * @param c
	 * @return
	 */
	public static Constant.Class buildClass(JvmType.Clazz r) {		
		java.lang.String d = ClassFile.descriptor(r,false);		
		d = d.substring(1,d.length()-1); // remove "L" and ";"
		return new Constant.Class(new Constant.Utf8(d));
	}
	
	/**
	 * This method creates a CONSTANT_Class object from an Array
	 * 
	 * @param c
	 * @return
	 */
	public static Constant.Class buildClass(JvmType.Array r) {		
		java.lang.String d = ClassFile.descriptor(r,false);				
		return new Constant.Class(new Constant.Utf8(d));
	}
	
	/**
	 * Creates a CONSTANT_FieldRef object.
	 * 
	 * @param owner
	 * @param name
	 * @param type
	 * @return
	 */
	public static Constant.FieldRef buildFieldRef(JvmType.Clazz owner, 
			java.lang.String name, JvmType type) {
		return new Constant.FieldRef(buildClass(owner), 
				new Constant.NameType(new Constant.Utf8(name),
				 new Constant.Utf8(ClassFile.descriptor(type,false))));
	}
	
	/**
	 * Creates a CONSTANT_MethodRef object.
	 * 
	 * @param owner
	 * @param name
	 * @param type
	 * @return
	 */
	public static Constant.MethodRef buildMethodRef(JvmType.Clazz owner, java.lang.String name, JvmType type) {
		return new Constant.MethodRef(buildClass(owner), 
				new Constant.NameType(new Constant.Utf8(name),
				 new Constant.Utf8(ClassFile.descriptor(type,false))));
	}
		
	/**
	 * Creates a CONSTANT_InterfaceMethodRef object.
	 * 
	 * @param owner
	 * @param name
	 * @param type
	 * @return
	 */
	public static Constant.InterfaceMethodRef buildInterfaceMethodRef(JvmType.Clazz owner, java.lang.String name, JvmType type) {
		return new Constant.InterfaceMethodRef(buildClass(owner), 
				new Constant.NameType(new Constant.Utf8(name),
				 new Constant.Utf8(ClassFile.descriptor(type,false))));
	}
	
	/**
	 * Recursively add a CONSTANT_Info object to a constant pool. Items used by
	 * this item which are not already in the pool are also added.
	 * 	 
	 * @author djp
	 * 
	 * @return the index of the pool item
	 */
	public static void addPoolItem(Constant.Info item, Set<Constant.Info> constantPool) {						
		if(!constantPool.contains(item)) {			
			// item is not already in pool		
			if(item instanceof Constant.String) {
				Constant.String s = (Constant.String) item;
				addPoolItem(s.str, constantPool);				
			}else if(item instanceof Constant.Class) {				
				Constant.Class c = (Constant.Class) item;
				addPoolItem(c.name, constantPool);
			} else if(item instanceof Constant.FieldRef) {
				Constant.FieldRef f = (Constant.FieldRef) item;
				addPoolItem(f.classInfo, constantPool);
				addPoolItem(f.nameType, constantPool);				
			} else if(item instanceof Constant.MethodRef) {
				Constant.MethodRef m = (Constant.MethodRef) item;
				addPoolItem(m.classInfo, constantPool);
				addPoolItem(m.nameType, constantPool);		
			} else if(item instanceof Constant.InterfaceMethodRef) {
				Constant.InterfaceMethodRef m = (Constant.InterfaceMethodRef) item;
				addPoolItem(m.classInfo, constantPool);
				addPoolItem(m.nameType, constantPool);				
			} else if(item instanceof Constant.NameType) {
				Constant.NameType nt = (Constant.NameType) item;
				addPoolItem(nt.desc, constantPool);
				addPoolItem(nt.name, constantPool);								
			} 											
			constantPool.add(item);						
		} 
	}
		
	public static abstract class Info {
		/**
		 * Convert this pool item into bytes.
		 * 
		 * @param constantPool
		 * @return
		 */
		public abstract byte[] toBytes(Map<Info,java.lang.Integer> constantPool);
	}
	
	public final static class Utf8 extends Info {
		public final java.lang.String str;

		public Utf8(java.lang.String s) { str = s; }
		
		public boolean equals(Object o) {
			if(o instanceof Utf8) {
				return str.equals(((Utf8)o).str);
			} else return false;			
		}
		
		public int hashCode() { return str.hashCode(); }
		
		public byte[] toBytes(Map<Info,java.lang.Integer> constantPool) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			write_u1(bout,CONSTANT_Utf8);
			try {
				byte[] bytes = str.getBytes("UTF8");
				write_u2(bout,bytes.length);
				bout.write(bytes,0,bytes.length);			
			} catch(UnsupportedEncodingException e) {
				// hmmm, this ain pretty ;)
			}
			return bout.toByteArray();
		}
		
		public java.lang.String toString() { return "Utf8(\"" + str + "\")"; }
	}
	
	public final static class String extends Info {
		public final Utf8 str;		
		public String(Utf8 s) { str = s; }
		public boolean equals(Object o) {
			if(o instanceof String) {
				return str.equals(((String)o).str);
			} else return false;			
		}
		public int hashCode() { return str.hashCode(); }
		
		public byte[] toBytes(Map<Info,java.lang.Integer> constantPool)  {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			
			write_u1(bout,CONSTANT_String);
			write_u2(bout,constantPool.get(str));
			return bout.toByteArray();
		}
		public java.lang.String toString() { return "String(" + str + ")"; }
	}
	
	public static final class Integer extends Info {
		public final int value;		
		public Integer(int v) { value=v; }
		public boolean equals(Object o) {
			if(o instanceof Integer) {
				return value == ((Integer)o).value;
			} else return false;			
		}
		public int hashCode() { return value; }
		public byte[] toBytes(Map<Info,java.lang.Integer> constantPool)  {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			write_u1(bout,CONSTANT_Integer);
			write_u4(bout,value);
			return bout.toByteArray();
		}
	}
	public static final class Long extends Info {
		public final long value;		
		public Long(long v) { value=v; }
		public boolean equals(Object o) {
			if(o instanceof Long) {
				return value == ((Long)o).value;
			} else return false;			
		}
		public int hashCode() { return (int) value; }
		public byte[] toBytes(Map<Info,java.lang.Integer> constantPool)  {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			write_u1(bout,CONSTANT_Long);
			write_u8(bout,value);	
			return bout.toByteArray();
		}
	}
	
	public static final class Float extends Info {
		public final float value;		
		public Float(float v) { value=v; }
		public boolean equals(Object o) {
			if(o instanceof Float) {								
				float ov = ((Float)o).value;
				return value == ov || (java.lang.Float.isNaN(value) && java.lang.Float.isNaN(ov));
			} else return false;			
		}
		public int hashCode() { return Math.round(value); }		
		public byte[] toBytes(Map<Info,java.lang.Integer> constantPool)  {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			write_u1(bout,CONSTANT_Float);
			write_u4(bout,java.lang.Float.floatToIntBits(value));
			return bout.toByteArray();
		}
	}
	
	public static final class Double extends Info {
		public final double value;		
		public Double(double v) { value=v; }
		public boolean equals(Object o) {
			if(o instanceof Double) {
				double ov = ((Double)o).value;
				return value == ov || (java.lang.Double.isNaN(value) && java.lang.Double.isNaN(ov));				
			} else return false;			
		}
		public int hashCode() { return (int) Math.round(value); }		
		public byte[] toBytes(Map<Info,java.lang.Integer> constantPool)  {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			write_u1(bout,CONSTANT_Double);
			write_u8(bout,java.lang.Double.doubleToLongBits(value));
			return bout.toByteArray();
		}
	}
	
	public static final class Class extends Info {
		public final Utf8 name;		
		public Class(Utf8 n) { name = n; }
		public Class(java.lang.String n) { name = new Utf8(n); }
		
		public boolean equals(Object o) {
			if(o instanceof Constant.Class) {
				return name.equals(((Constant.Class)o).name);
			} else return false;			
		}
		public int hashCode() { return name.hashCode(); }		
		
		public byte[] toBytes(Map<Info,java.lang.Integer> constantPool)  {
			assert constantPool.containsKey(name);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			write_u1(bout,CONSTANT_Class);	
			write_u2(bout,constantPool.get(name));
			return bout.toByteArray();
		}
		public java.lang.String toString() { return "Class(" + name + ")"; }
	}
	
	public static final class FieldRef extends Info {
		public final Class classInfo;
		public final NameType nameType;		
		public FieldRef(Class ci,  NameType nt) { 
			nameType = nt;
			classInfo = ci;
		}
		public boolean equals(Object o) {
			if(o instanceof FieldRef) {
				FieldRef fr = (FieldRef) o;
				return nameType.equals(fr.nameType) &&
						classInfo.equals(fr.classInfo);	
			} else return false;			
		}
		public int hashCode() { return classInfo.hashCode() ^ nameType.hashCode(); }		
		public byte[] toBytes(Map<Info,java.lang.Integer> constantPool)  {
			assert constantPool.containsKey(classInfo);
			assert constantPool.containsKey(nameType);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			write_u1(bout,CONSTANT_FieldRef);			
			write_u2(bout,constantPool.get(classInfo));
			write_u2(bout,constantPool.get(nameType));
			return bout.toByteArray();
		}
	}
	
	public static final class MethodRef extends Info {
		public final Class classInfo;
		public final NameType nameType;		
		public MethodRef(Class ci,  NameType nt) { 
			nameType = nt;
			classInfo = ci;
		}
		public boolean equals(Object o) {
			if(o instanceof MethodRef) {
				MethodRef mr = (MethodRef) o;
				return nameType.equals(mr.nameType) &&
						classInfo.equals(mr.classInfo);	
			} else return false;			
		}
		public int hashCode() { return classInfo.hashCode() ^ nameType.hashCode(); }		
		public byte[] toBytes(Map<Info,java.lang.Integer> constantPool)  {
			assert constantPool.containsKey(classInfo);
			assert constantPool.containsKey(nameType);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			write_u1(bout,CONSTANT_MethodRef);			
			write_u2(bout,constantPool.get(classInfo));
			write_u2(bout,constantPool.get(nameType));
			return bout.toByteArray();
		}
		public java.lang.String toString() {
			return "MethodRef(" + classInfo + ", " + nameType + ")";
		}
	}
	
	public static final class InterfaceMethodRef extends Info {
		public final Class classInfo;
		public final NameType nameType;		
		public InterfaceMethodRef(Class ci,  NameType nt) { 
			nameType = nt;
			classInfo = ci;
		}
		public boolean equals(Object o) {
			if(o instanceof InterfaceMethodRef) {
				InterfaceMethodRef imr = (InterfaceMethodRef) o;
				return nameType.equals(imr.nameType) &&
						classInfo.equals(imr.classInfo);	
			} else return false;			
		}
		public int hashCode() { return classInfo.hashCode() ^ nameType.hashCode(); }		
		public byte[] toBytes(Map<Info,java.lang.Integer> constantPool) {
			assert constantPool.containsKey(classInfo);
			assert constantPool.containsKey(nameType);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			write_u1(bout,CONSTANT_InterfaceMethodRef);			
			write_u2(bout,constantPool.get(classInfo));
			write_u2(bout,constantPool.get(nameType));
			return bout.toByteArray();
		}
	}
	
	public static final class NameType extends Info {
		public final Utf8 name;
		public final Utf8 desc;		
		public NameType(Utf8 n, Utf8 d) { 
			name = n;
			desc = d;
		}
		public boolean equals(Object o) {
			if(o instanceof NameType) {
				NameType imr = (NameType) o;
				return name.equals(imr.name) && desc.equals(imr.desc);	
			} else return false;			
		}
		public int hashCode() { return name.hashCode() ^ desc.hashCode(); }		
		public byte[] toBytes(Map<Info,java.lang.Integer> constantPool) {
			assert constantPool.containsKey(name);
			assert constantPool.containsKey(desc);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			write_u1(bout,CONSTANT_NameAndType);						
			write_u2(bout,constantPool.get(name));
			write_u2(bout,constantPool.get(desc));
			return bout.toByteArray();
		}
		public java.lang.String toString() {
			return "NameType(" + name + ", " + desc + ")";
		}
	}	
	
	public static final class Dummy extends Info {
		public byte[] toBytes(Map<Info,java.lang.Integer> constantPool) {
			return new byte[0];
		}
		public java.lang.String toString() {
			return "Dummy()";
		}
	}
	
	// tags for constant pool entries	
	static final int CONSTANT_Class = 7;
	static final int CONSTANT_FieldRef = 9;
	static final int CONSTANT_MethodRef = 10;
	static final int CONSTANT_InterfaceMethodRef = 11;
	static final int CONSTANT_String = 8;
	static final int CONSTANT_Integer = 3;
	static final int CONSTANT_Float = 4;
	static final int CONSTANT_Long = 5;
	static final int CONSTANT_Double = 6;
	static final int CONSTANT_NameAndType = 12;
	static final int CONSTANT_Utf8 = 1;
	
	private static void write_u1(ByteArrayOutputStream output, int w) {
		output.write(w & 0xFF);
	}
	
	private static void write_u2(ByteArrayOutputStream output, int w) {
		output.write((w >> 8) & 0xFF);
		output.write(w & 0xFF);		
	}
	
	private static void write_u4(ByteArrayOutputStream output, long w) {
		output.write((int) (w >> 24) & 0xFF);
		output.write((int) (w >> 16) & 0xFF);
		output.write((int) (w >> 8) & 0xFF);
		output.write((int) (w & 0xFF));						
	}
	
	private static void write_u8(ByteArrayOutputStream output, long w) {
		write_u4(output, ((w >> 32) & 0xFFFFFFFFL));
		write_u4(output, (w & 0xFFFFFFFFL));
	}
}
