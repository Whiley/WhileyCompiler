// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyil.jvm.attributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.*;

import wyil.jvm.rt.BigRational;
import wyil.lang.*;
import wyjvm.io.BinaryInputStream;
import wyjvm.io.BinaryOutputStream;
import wyjvm.lang.BytecodeAttribute;
import wyjvm.lang.BytecodeAttributeReader;
import wyjvm.lang.Constant;
import wyjx.jvm.attributes.WhileyDefine;

/**
 * The WhileyVersion attribute is simply a marker used to indicate that a class
 * file was generated from a whiley source file. This is useful in
 * multi-platform scenarios where we might have multiple source languages.
 * 
 * @author djp
 * 
 */
public class WhileyDefine implements BytecodeAttribute {
	private String defName;
	private Value value;	
	private Type type;
	
	public WhileyDefine(String name, Value expr) {
		this.defName = name;		
		this.value = expr;
	}
	
	public WhileyDefine(String name, Type type) {
		this.defName = name;
		this.type = type;	
	}
	
	public String name() {
		return "WhileyDefine";
	}
	
	public String defName() {
		return defName;
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
		
		writer.write_u2(constantPool.get(new Constant.Utf8(name())));
		writer.write_u4(out.size() + 2);		
		writer.write_u2(constantPool.get(new Constant.Utf8(defName)));
		writer.write(out.toByteArray());				
	}	
		
	public void addPoolItems(Set<Constant.Info> constantPool, ClassLoader loader) {		
		Constant.addPoolItem(new Constant.Utf8(name()), constantPool);	
		Constant.addPoolItem(new Constant.Utf8(defName), constantPool);
		
		/*
		 * Following will need to be put back in place to catch field names of record values
		if(value != null) {
			addPoolItems(value, constantPool);
		}
		*/
		if(type != null) {
			WhileyType.addPoolItems(type, constantPool);
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
	

	protected static void write(Value val, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		if(val instanceof Value.Null) {
			write((Value.Null) val, writer, constantPool);
		} else if(val instanceof Value.Bool) {
			write((Value.Bool) val, writer, constantPool);
		} else if(val instanceof Value.Int) {
			write((Value.Int) val, writer, constantPool);
		} else if(val instanceof Value.Real) {
			write((Value.Real) val, writer, constantPool);
		} else if(val instanceof Value.Set) {
			write((Value.Set) val, writer, constantPool);
		} else if(val instanceof Value.List) {
			write((Value.List) val, writer, constantPool);
		} else if(val instanceof Value.Record) {
			write((Value.Record) val, writer, constantPool);
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
	
	public static void write(Value.Int expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u1(INTVAL);
		BigInteger bi = expr.value;
		byte[] bibytes = bi.toByteArray();
		// FIXME: bug here for constants that require more than 65535 bytes
		writer.write_u2(bibytes.length);
		writer.write(bibytes);
	}
	
	public static void write(Value.Real expr, BinaryOutputStream writer,
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
	
	public static void write(Value.Record expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u1(RECORDVAL);
		writer.write_u2(expr.values.size());
		for(Map.Entry<String,Value> v : expr.values.entrySet()) {
			writer.write_u2(constantPool.get(new Constant.Utf8(v.getKey())));
			write(v.getValue(), writer, constantPool);
		}
	}
	
	
	public static class Reader implements BytecodeAttributeReader {		
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
			
			if(sw == 0) {				
				// Condition only
				Value value = readValue(input,constantPool);				
				return new WhileyDefine(name,value);			
			} else {
				// type only
				Type type = WhileyType.Reader.readType(input,constantPool);
				return new WhileyDefine(name,type);
			} 
		}
		
		protected static Value readValue(BinaryInputStream reader,
				Map<Integer, Constant.Info> constantPool) throws IOException {		
			int code = reader.read_u1();				
			switch (code) {			
			case INTVAL:			
			{
				int len = reader.read_u2();				
				byte[] bytes = new byte[len];
				reader.read(bytes);
				BigInteger bi = new BigInteger(bytes);
				return Value.V_INT(bi);
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
				return Value.V_REAL(br);
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
	
	private final static int NULL = 0;
	private final static int TRUE = 1;
	private final static int FALSE = 2;	
	private final static int INTVAL = 3;
	private final static int REALVAL = 4;
	private final static int SETVAL = 5;
	private final static int LISTVAL = 6;
	private final static int RECORDVAL = 7;		
}
