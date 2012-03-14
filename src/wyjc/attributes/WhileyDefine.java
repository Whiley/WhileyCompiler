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

package wyjc.attributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.*;

import wyil.lang.*;
import wyjc.runtime.BigRational;
import wyjvm.io.BinaryInputStream;
import wyjvm.io.BinaryOutputStream;
import wyjvm.lang.BytecodeAttribute;
import wyjvm.lang.Constant;

/**
 * The WhileyVersion attribute is simply a marker used to indicate that a class
 * file was generated from a whiley source file. This is useful in
 * multi-platform scenarios where we might have multiple source languages.
 * 
 * @author David J. Pearce
 * 
 */
public class WhileyDefine implements BytecodeAttribute {
	private String defName;
	private Value value;	
	private Type type;
	
	// FIXME: at the moment we list BytecodeAttributes here. But these are very
	// JVM specific. What we really want are WyilAttributes here instead, and a
	// generic way of converting from bytes to them.
	private List<BytecodeAttribute> attributes;
	
	public WhileyDefine(String name, Value expr, BytecodeAttribute... attributes) {
		this.defName = name;		
		this.value = expr;
		this.attributes = Arrays.asList(attributes);
	}
	
	public WhileyDefine(String name, Value expr, Collection<BytecodeAttribute> attributes) {
		this.defName = name;		
		this.value = expr;
		this.attributes = new ArrayList<BytecodeAttribute>(attributes);
	}
	
	public WhileyDefine(String name, Type type, BytecodeAttribute... attributes) {
		this.defName = name;
		this.type = type;	
		this.attributes = Arrays.asList(attributes);		
	}
	
	public WhileyDefine(String name, Type type, Collection<BytecodeAttribute> attributes) {
		this.defName = name;
		this.type = type;	
		this.attributes = new ArrayList<BytecodeAttribute>(attributes);
	}
	
	public String name() {
		return "WhileyDefine";
	}
	
	public String defName() {
		return defName;
	}
	
	public List<BytecodeAttribute> attributes() {
		return Collections.unmodifiableList(attributes);
	}
	
	public Type type() {
		return type;
	}
	
	public Value value() {
		return value;
	}
		
	public void write(BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool, ClassLoader loader)
			throws IOException {		
		
		// First, construct a byte array for the type and/or condition being
        // defined. This must be done first as we need to determine its length
        // so this can be written as part of the attribute header.
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BinaryOutputStream iw = new BinaryOutputStream(out);

		if(type == null) {
			iw.write_u1(0); // CONSTANT ONLY				
			write(value,iw, constantPool);			
		} else {
			iw.write_u1(1); // TYPE ONLY
			WhileyType.write(type, iw, constantPool);
		} 					
		
		iw.write_u2(attributes.size());
		for(BytecodeAttribute a : attributes) {
			a.write(iw, constantPool, loader);
		}
		iw.close();
		writer.write_u2(constantPool.get(new Constant.Utf8(name())));
		writer.write_u4(out.size() + 2);		
		writer.write_u2(constantPool.get(new Constant.Utf8(defName)));				
		writer.write(out.toByteArray());			
	}	
		
	public void addPoolItems(Set<Constant.Info> constantPool, ClassLoader loader) {		
		Constant.addPoolItem(new Constant.Utf8(name()), constantPool);	
		Constant.addPoolItem(new Constant.Utf8(defName), constantPool);
		
		if(value != null) {
			addPoolItems(value, constantPool);
		}
	
		if(type != null) {
			WhileyType.addPoolItems(type, constantPool);
		}		
		
		for(BytecodeAttribute attr : attributes) {
			attr.addPoolItems(constantPool, loader);
		}
	}
	
	public static void addPoolItems(Value val, Set<Constant.Info> constantPool) {
		if(val instanceof Value.Set) {
			Value.Set vs = (Value.Set) val;
			for(Value v : vs.values) { 
				addPoolItems(v, constantPool);
			}
		} else if(val instanceof Value.List) {
			Value.List vs = (Value.List) val;
			for(Value v : vs.values) { 
				addPoolItems(v, constantPool);
			}
		} else if(val instanceof Value.Record) {
			Value.Record vr = (Value.Record) val;
			for(Map.Entry<String,Value> p : vr.values.entrySet()) {
				addPoolItems(p.getValue(), constantPool);
				Constant.addPoolItem(new Constant.Utf8(p.getKey()),
						constantPool);
			}
		} 
	}
	
	public void print(PrintWriter output,
			Map<Constant.Info, Integer> constantPool, ClassLoader loader)
			throws IOException {

		if (type == null) {
			output.println("  WhileyDefine: " + defName + " as " + value);
		} else {
			output.println("  WhileyDefine: " + defName + " as " + type);
		} 
	}
	

	public static void write(Value val, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		if(val instanceof Value.Null) {
			write((Value.Null) val, writer, constantPool);
		} else if(val instanceof Value.Bool) {
			write((Value.Bool) val, writer, constantPool);
		} else if(val instanceof Value.Byte) {
			write((Value.Byte) val, writer, constantPool);
		} else if(val instanceof Value.Char) {
			write((Value.Char) val, writer, constantPool);
		} else if(val instanceof Value.Integer) {
			write((Value.Integer) val, writer, constantPool);
		} else if(val instanceof Value.Rational) {
			write((Value.Rational) val, writer, constantPool);
		} else if(val instanceof Value.Strung) {
			write((Value.Strung) val, writer, constantPool);
		} else if(val instanceof Value.Set) {
			write((Value.Set) val, writer, constantPool);
		} else if(val instanceof Value.List) {
			write((Value.List) val, writer, constantPool);
		} else if(val instanceof Value.Dictionary) {
			write((Value.Dictionary) val, writer, constantPool);
		} else if(val instanceof Value.Record) {
			write((Value.Record) val, writer, constantPool);
		} else if(val instanceof Value.Tuple) {
			write((Value.Tuple) val, writer, constantPool);
		} else if(val instanceof Value.FunctionOrMethodOrMessage) {
			write((Value.FunctionOrMethodOrMessage) val, writer, constantPool);
		} else {
			throw new RuntimeException("Unknown value encountered - " + val);
		}
	}
	
	public static void write(Value.Null expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {				
		writer.write_u1(NULL);
	}
	
	public static void write(Value.Bool expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		
		if(expr.value) {
			writer.write_u1(TRUE);
		} else {
			writer.write_u1(FALSE);
		}
	}
	
	public static void write(Value.Byte expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {		
		writer.write_u1(BYTEVAL);		
		writer.write_u1(expr.value);		
	}
	
	public static void write(Value.Char expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {		
		writer.write_u1(CHARVAL);		
		writer.write_u2(expr.value);		
	}
	
	public static void write(Value.Integer expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {		
		writer.write_u1(INTVAL);		
		BigInteger num = expr.value;
		byte[] numbytes = num.toByteArray();
		// FIXME: bug here for constants that require more than 65535 bytes
		writer.write_u2(numbytes.length);
		writer.write(numbytes);
	}

	public static void write(Value.Rational expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {		
		
		writer.write_u1(REALVAL);
		BigRational br = expr.value;
		BigInteger num = br.numerator();
		BigInteger den = br.denominator();

		byte[] numbytes = num.toByteArray();
		// FIXME: bug here for constants that require more than 65535 bytes
		writer.write_u2(numbytes.length);
		writer.write(numbytes);

		byte[] denbytes = den.toByteArray();
		// FIXME: bug here for constants that require more than 65535 bytes
		writer.write_u2(denbytes.length);
		writer.write(denbytes);
	}
		
	public static void write(Value.Strung expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {	
		writer.write_u1(STRINGVAL);
		String value = expr.value;
		int valueLength = value.length();		
		writer.write_u2(valueLength);
		for(int i=0;i!=valueLength;++i) {
			writer.write_u2(value.charAt(i));
		}
	}
	public static void write(Value.Set expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u1(SETVAL);
		writer.write_u2(expr.values.size());
		for(Value v : expr.values) {
			write(v,writer,constantPool);
		}
	}
	
	public static void write(Value.List expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u1(LISTVAL);
		writer.write_u2(expr.values.size());
		for(Value v : expr.values) {
			write(v,writer,constantPool);
		}
	}
	
	public static void write(Value.Dictionary expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u1(DICTIONARYVAL);
		writer.write_u2(expr.values.size());
		for(Map.Entry<Value,Value> e : expr.values.entrySet()) {
			write(e.getKey(),writer,constantPool);
			write(e.getValue(),writer,constantPool);
		}
	}
	
	public static void write(Value.Record expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u1(RECORDVAL);
		writer.write_u2(expr.values.size());
		for(Map.Entry<String,Value> v : expr.values.entrySet()) {
			writer.write_u2(constantPool.get(new Constant.Utf8(v.getKey())));
			write(v.getValue(), writer, constantPool);
		}
	}
	
	public static void write(Value.Tuple expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u1(TUPLEVAL); // FIXME: should be TUPLE!!!
		writer.write_u2(expr.values.size());
		for(Value v : expr.values) {
			write(v,writer,constantPool);
		}
	}
	
	public static void write(Value.FunctionOrMethodOrMessage expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		Type.FunctionOrMethodOrMessage t = expr.type();
		if(t instanceof Type.Function) {
			writer.write_u1(FUNCTIONVAL);			
		} else if(t instanceof Type.Method) {
			writer.write_u1(METHODVAL);
		} else {
			writer.write_u1(MESSAGEVAL);
		}
		
		WhileyType.write(t, writer, constantPool);
		String value = expr.name.toString();
		int valueLength = value.length();		
		writer.write_u2(valueLength);
		for(int i=0;i!=valueLength;++i) {
			writer.write_u2(value.charAt(i));
		}
		
	}
	
	public static final class Reader implements BytecodeAttribute.Reader {		
		private HashMap<String,BytecodeAttribute.Reader> attributeReaders;
		
		public Reader(Collection<BytecodeAttribute.Reader> readers) {
			this.attributeReaders = new HashMap<String,BytecodeAttribute.Reader>();
			for(BytecodeAttribute.Reader r : readers) {
				attributeReaders.put(r.name(),r);
			}	
		}
		
		public Reader(BytecodeAttribute.Reader... readers) {
			this.attributeReaders = new HashMap<String,BytecodeAttribute.Reader>();
			for(BytecodeAttribute.Reader r : readers) {
				attributeReaders.put(r.name(),r);
			}	
		}
		
		public String name() {
			return "WhileyDefine";
		}
		
		
		public WhileyDefine read(BinaryInputStream input,
				Map<Integer, Constant.Info> constantPool) throws IOException {
			input.read_u2(); // attribute name index code
			input.read_u4(); // attribute length 
			int nameIdx = input.read_u2();
			
			String name = ((Constant.Utf8) constantPool.get(nameIdx)).str;
			int sw = input.read_u1();			
			
			if (sw == 0) {
				// Condition only
				Value value = readValue(input, constantPool);				
				int nattrs = input.read_u2();
				List<BytecodeAttribute> attrs = BytecodeAttribute.Fn.read(
						nattrs, input, constantPool, attributeReaders);
				return new WhileyDefine(name, value, attrs);
			} else {
				// type only
				Type type = Type.construct(new WhileyType.TypeReader(input,
						constantPool).read());
				int nattrs = input.read_u2();
				List<BytecodeAttribute> attrs = BytecodeAttribute.Fn.read(
						nattrs, input, constantPool, attributeReaders);
				return new WhileyDefine(name, type, attrs);
			}
		}
		
		public static Value readValue(BinaryInputStream reader,
				Map<Integer, Constant.Info> constantPool) throws IOException {		
			int code = reader.read_u1();				
			switch (code) {			
			case NULL:
				return Value.V_NULL;
			case FALSE:
				return Value.V_BOOL(false);
			case TRUE:
				return Value.V_BOOL(true);				
			case BYTEVAL:			
			{
				byte val = (byte) reader.read_u1();				
				return Value.V_BYTE(val);
			}
			case CHARVAL:			
			{
				char val = (char) reader.read_u2();				
				return Value.V_CHAR(val);
			}
			case INTVAL:			
			{
				int len = reader.read_u2();				
				byte[] bytes = new byte[len];
				reader.read(bytes);
				BigInteger bi = new BigInteger(bytes);
				return Value.V_INTEGER(bi);
			}
			case REALVAL:			
			{
				int len = reader.read_u2();
				byte[] bytes = new byte[len];
				reader.read(bytes);
				BigInteger num = new BigInteger(bytes);
				len = reader.read_u2();
				bytes = new byte[len];
				reader.read(bytes);
				BigInteger den = new BigInteger(bytes);
				BigRational br = new BigRational(num,den);
				return Value.V_RATIONAL(br);
			}
			case STRINGVAL:
			{
				int len = reader.read_u2();
				StringBuffer sb = new StringBuffer();
				for(int i=0;i!=len;++i) {
					char c = (char) reader.read_u2();
					sb.append(c);
				}
				return Value.V_STRING(sb.toString());
			}
			case LISTVAL:
			{
				int len = reader.read_u2();
				ArrayList<Value> values = new ArrayList<Value>();
				for(int i=0;i!=len;++i) {
					values.add((Value) readValue(reader,constantPool));
				}
				return Value.V_LIST(values);
			}
			case SETVAL:
			{
				int len = reader.read_u2();
				ArrayList<Value> values = new ArrayList<Value>();
				for(int i=0;i!=len;++i) {
					values.add((Value) readValue(reader,constantPool));
				}
				return Value.V_SET(values);
			}
			case TUPLEVAL:
			{
				int len = reader.read_u2();
				ArrayList<Value> values = new ArrayList<Value>();
				for(int i=0;i!=len;++i) {
					values.add((Value) readValue(reader,constantPool));
				}
				return Value.V_TUPLE(values);
			}
			case RECORDVAL:
			{
				int len = reader.read_u2();
				HashMap<String,Value> tvs = new HashMap<String,Value>();
				for(int i=0;i!=len;++i) {
					int idx = reader.read_u2();
					Constant.Utf8 utf8 = (Constant.Utf8) constantPool.get(idx);
					Value lhs = (Value) readValue(reader, constantPool);
					tvs.put(utf8.str, lhs);
				}
				return Value.V_RECORD(tvs);
			}			
			}
			throw new RuntimeException("Unknown Value encountered in WhileyDefine: " + code);
		}
	}	
	
	// =========================================================================
	// Value Identifiers
	// =========================================================================
	
	public final static int NULL = 0;
	public final static int TRUE = 1;
	public final static int FALSE = 2;	
	public final static int CHARVAL = 3;
	public final static int INTVAL = 4;
	public final static int REALVAL = 5;
	public final static int SETVAL = 6;
	public final static int LISTVAL = 7;	
	public final static int RECORDVAL = 8;
	public final static int TUPLEVAL = 9;
	public final static int BYTEVAL = 10;
	public final static int STRINGVAL = 11;
	public final static int DICTIONARYVAL = 12;
	public final static int FUNCTIONVAL = 13;	
	public final static int METHODVAL = 14;
	public final static int MESSAGEVAL = 15;
}
