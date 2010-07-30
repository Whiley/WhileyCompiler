package wyil.jvm.attributes;

import static wyil.util.SyntaxError.syntaxError;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import wyil.jvm.rt.BigRational;
import wyil.lang.*;
import wyjvm.io.*;
import wyjvm.lang.*;

public class WhileyBlock implements BytecodeAttribute {
	private final Block block;
	
	public WhileyBlock(Block block) {
		this.block = block;
	}
	
	public String name() {
		return "WhileyBlock";
	}
	
	public void print(PrintWriter output,
			Map<Constant.Info, Integer> constantPool, ClassLoader loader) {

	}
	
	public void addPoolItems(Set<Constant.Info> constantPool, ClassLoader loader) {
		addPoolItems(block, constantPool);
	}
	
	public static void addPoolItems(Block block, Set<Constant.Info> constantPool) {
		for(Code c : block) {
			addPoolItems(c,constantPool);
		}
	}
	
	public static void addPoolItems(Code code, Set<Constant.Info> constantPool) {
		
	}
	
	public static void addPoolItems(RVal rval, Set<Constant.Info> constantPool) {
		
	}
	
	public void write(BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool, ClassLoader loader) {

	}

	protected static void writeBlock(Block expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) {
		
	}
	
	protected static void writeCode(Code code, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) {
		
	}	
	
	protected static void writeRVal(RVal rval, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		if(rval instanceof Value) {
			writeValue((Value)rval,writer,constantPool);
		} else {
			writeRVal((RVal.Variable)rval,writer,constantPool);
		}
	}	
	
	public static void writeRVal(RVal.Variable expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		// The encoding of variables could be optimised to avoid using the
		// constant pool in most, if not all cases.
		writer.write_u2(VARIABLE);				
		int idx = constantPool.get(new Constant.Utf8(expr.name));							
		writer.write_u2(idx);						
	}
	
	protected static void writeValue(Value val, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		if(val instanceof Value.Bool) {
			writeValue((Value.Bool) val, writer, constantPool);
		} else if(val instanceof Value.Int) {
			writeValue((Value.Int) val, writer, constantPool);
		} else if(val instanceof Value.Real) {
			writeValue((Value.Real) val, writer, constantPool);
		} else if(val instanceof Value.Set) {
			writeValue((Value.Set) val, writer, constantPool);
		} else if(val instanceof Value.List) {
			writeValue((Value.List) val, writer, constantPool);
		} else if(val instanceof Value.Tuple) {
			writeValue((Value.Tuple) val, writer, constantPool);
		} 
	}
	
	public static void writeValue(Value.Bool expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		
		if(expr.value) {
			writer.write_u2(TRUE);
		} else {
			writer.write_u2(FALSE);
		}
	}
	
	public static void writeValue(Value.Int expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u2(INTVAL);
		BigInteger bi = expr.value;
		byte[] bibytes = bi.toByteArray();
		// FIXME: bug here for constants that require more than 65535 bytes
		writer.write_u2(bibytes.length);
		writer.write(bibytes);
	}
	
	public static void writeValue(Value.Real expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u2(REALVAL);
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
	
	public static void writeValue(Value.Set expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u2(SETVAL);
		writer.write_u2(expr.values.size());
		for(Value v : expr.values) {
			writeValue(v,writer,constantPool);
		}
	}
	
	public static void writeValue(Value.List expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u2(LISTVAL);
		writer.write_u2(expr.values.size());
		for(Value v : expr.values) {
			writeValue(v,writer,constantPool);
		}
	}
	
	public static void writeValue(Value.Tuple expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u2(TUPLEVAL);
		writer.write_u2(expr.values.size());
		for(Map.Entry<String,Value> v : expr.values.entrySet()) {
			writer.write_u2(constantPool.get(new Constant.Utf8(v.getKey())));
			writeValue(v.getValue(), writer, constantPool);
		}
	}
	
	public static class Reader implements BytecodeAttributeReader { 
		public String name() {
			return "WhileyBlock";
		}
		
		public WhileyBlock read(BinaryInputStream reader,
				Map<Integer, Constant.Info> constantPool) throws IOException {
			return new WhileyBlock(readBlock(reader,constantPool));
		}
		
		protected static Block readBlock(BinaryInputStream reader,
				Map<Integer, Constant.Info> constantPool) throws IOException {							
			int code = reader.read_u2();
			return readCodes(code,reader,constantPool);
		}

		protected static Block readCodes(int code, BinaryInputStream reader,
				Map<Integer, Constant.Info> constantPool) throws IOException {							
			Block blk = new Block();
			return blk;
		}

		protected static Value readValue(BinaryInputStream reader,
				Map<Integer, Constant.Info> constantPool) throws IOException {
			return (Value) readRVal(reader,constantPool);
		}
		protected static RVal readRVal(BinaryInputStream reader,
				Map<Integer, Constant.Info> constantPool) throws IOException {		
			int code = reader.read_u2();				
			switch (code) {
			case VARIABLE:			
				// The encoding of variables could be optimised to avoid using the
				// constant pool in most, if not all cases.
				int idx = reader.read_u2();			
				Constant.Utf8 utf8 = (Constant.Utf8) constantPool.get(idx);
				Type type = WhileyType.Reader.readType(reader, constantPool);
				return RVal.VAR(type,utf8.str);			
			case INTVAL:			
				int len = reader.read_u2();
				byte[] bytes = new byte[len];
				reader.read(bytes);
				BigInteger bi = new BigInteger(bytes);
				return Value.V_INT(bi);			
			case REALVAL:			
				len = reader.read_u2();
				bytes = new byte[len];
				reader.read(bytes);
				BigInteger num = new BigInteger(bytes);
				len = reader.read_u2();
				bytes = new byte[len];
				reader.read(bytes);
				BigInteger den = new BigInteger(bytes);
				BigRational br = new BigRational(num,den);
				return Value.V_REAL(br);				
			case LISTVAL:
				len = reader.read_u2();
				ArrayList<Value> values = new ArrayList<Value>();
				for(int i=0;i!=len;++i) {
					values.add((Value) readRVal(reader,constantPool));
				}
				return Value.V_LIST(values);			
			case SETVAL:
				len = reader.read_u2();
				values = new ArrayList<Value>();
				for(int i=0;i!=len;++i) {
					values.add((Value) readRVal(reader,constantPool));
				}
				return Value.V_SET(values);			
			case TUPLEVAL:
				len = reader.read_u2();
				HashMap<String,Value> tvs = new HashMap<String,Value>();
				for(int i=0;i!=len;++i) {
					idx = reader.read_u2();
					utf8 = (Constant.Utf8) constantPool.get(idx);
					Value lhs = (Value) readRVal(reader, constantPool);
					tvs.put(utf8.str, lhs);
				}
				return Value.V_TUPLE(tvs);
			default:
				throw new RuntimeException("Unknown RVal encountered in WhileyBlock");
			}		
		}
	}
	
	private final static int NULL = 0;
	private final static int VARIABLE = 1;
	private final static int INVOKE = 2;		
	
	private final static int TRUE = 4;
	private final static int FALSE = 5;
	private final static int AND = 6;		
	private final static int NONE = 7;
	private final static int NOT = 8;
	private final static int OR = 9;
	private final static int SOME = 10;
	private final static int UPDATE = 11;
	private final static int BOOLEQ = 12;
	private final static int BOOLNEQ = 13;
	private final static int TYPEGATE = 14;
	private final static int TYPEEQUALS = 15;
	
	private final static int INTADD = 20;
	private final static int INTDIV = 21;
	private final static int INTEQ = 22;
	private final static int INTGT = 23;
	private final static int INTGTEQ = 24;
	private final static int INTLT = 25;
	private final static int INTLTEQ = 26;		
	private final static int INTMUL = 27;
	private final static int INTNEG = 28;
	private final static int INTNEQ = 29;
	private final static int INTSUB = 30;	
	private final static int INTVAL = 31;
	
	private final static int REALADD = 40;
	private final static int REALDIV = 41;
	private final static int REALEQ = 42;
	private final static int REALGT = 43;
	private final static int REALGTEQ = 44;
	private final static int REALLT = 45;
	private final static int REALLTEQ = 46;		
	private final static int REALMUL = 47;
	private final static int REALNEG = 48;
	private final static int REALNEQ = 49;
	private final static int REALSUB = 50;	
	private final static int REALVAL = 51;
	
	private final static int LISTACCESS = 60;
	private final static int LISTELEMOF = 61;	
	private final static int LISTEQ = 62;
	private final static int LISTGEN = 63;
	private final static int LISTLENGTH = 64;
	private final static int LISTNEQ = 65;
	private final static int LISTVAL = 66;
		
	private final static int SETCOMPREHENSION = 70;
	private final static int SETDIFFERENCE = 71;
	private final static int SETELEMOF = 72;	
	private final static int SETEQ = 73;
	private final static int SETGEN = 74;
	private final static int SETINTERSECT = 75;
	private final static int SETLENGTH = 76;
	private final static int SETNEQ = 77;
	private final static int SETSUBSET = 78;
	private final static int SETSUBSETEQ = 79;
	private final static int SETSUPSET = 80;
	private final static int SETSUPSETEQ = 81;
	private final static int SETUNION = 82;
	private final static int SETVAL = 83;		
		
	private final static int TUPLEACCESS = 90;
	private final static int TUPLEEQ = 91;
	private final static int TUPLEGEN = 92;
	private final static int TUPLENEQ = 93;	
	private final static int TUPLEVAL = 94;

}
