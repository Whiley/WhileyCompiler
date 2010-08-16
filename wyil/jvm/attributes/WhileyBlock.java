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
import wyil.lang.Code.Assign;
import wyil.lang.Code.Debug;
import wyil.lang.Code.Forall;
import wyil.lang.Code.IfGoto;
import wyil.lang.Code.Return;
import wyil.util.SyntacticElement;
import wyjc.lang.Expr;
import wyjvm.io.*;
import wyjvm.lang.*;

public class WhileyBlock implements BytecodeAttribute {
	private final Block block;
	private final String name;
	
	public WhileyBlock(String name, Block block) {
		this.block = block;
		this.name = name;
	}
	
	public String name() {
		return name;
	}
	
	public Block block() {
		return block;
	}
	
	public void print(PrintWriter output,
			Map<Constant.Info, Integer> constantPool, ClassLoader loader) {

	}
	
	public void addPoolItems(Set<Constant.Info> constantPool, ClassLoader loader) {
		addPoolItems(block, constantPool);
	}
	
	public static void addPoolItems(Block block, Set<Constant.Info> constantPool) {
		for(Stmt s : block) {
			addPoolItems(s.code,constantPool);
		}
	}
	
	public static void addPoolItems(Code c, Set<Constant.Info> constantPool) {
		if(c instanceof Assign) {
			Assign a = (Assign) c;
			if(a.lhs != null) {
				addPoolItems(a.lhs,constantPool);
			} 								
			addPoolItems(a.rhs,constantPool);						
		} else if(c instanceof Code.Goto) {
			Code.Goto a = (Code.Goto) c;			
			constantPool.add(new Constant.Utf8(a.target));
		} else if(c instanceof Code.Fail) {
			Code.Fail a = (Code.Fail) c;			
			constantPool.add(new Constant.Utf8(a.msg));			
		} else if(c instanceof Code.Label) {
			Code.Label a = (Code.Label) c;
			constantPool.add(new Constant.Utf8(a.label));			
		} else if(c instanceof IfGoto) {
			IfGoto a = (IfGoto) c;
			addPoolItems(a.lhs,constantPool);			
			addPoolItems(a.rhs,constantPool);			
			constantPool.add(new Constant.Utf8(a.target));
		} else if(c instanceof Forall) {
			Forall a = (Forall) c;	
									
		}
	}
	
	public static void addPoolItems(CExpr rval, Set<Constant.Info> constantPool) {
		
	}
	
	public void write(BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool, ClassLoader loader)
			throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BinaryOutputStream iw = new BinaryOutputStream(out);

		writeBlock(block, iw, constantPool);

		writer.write_u2(constantPool.get(new Constant.Utf8(name())));
		writer.write_u4(out.size() + 2);
		writer.write(out.toByteArray());
	}

	protected static void writeBlock(Block expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u4(expr.size());
		for(Stmt s : expr) {
			writeCode(s.code, writer, constantPool);
		}
	}
	
	protected static void writeCode(Code c, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		if(c instanceof Assign) {
			Assign a = (Assign) c;
			if(a.lhs != null) {
				writer.write_u2(ASSIGN);
				writeCExpr(a.lhs,writer,constantPool);
				writeCExpr(a.rhs,writer,constantPool);
			} else {
				writer.write_u2(CODEEXPR);				
				writeCExpr(a.rhs,writer,constantPool);
			}			
		} else if(c instanceof Code.Goto) {
			Code.Goto a = (Code.Goto) c;			
			writer.write_u2(GOTO);
			writer.write_u2(constantPool.get(new Constant.Utf8(a.target)));
		} else if(c instanceof Code.Fail) {
			Code.Fail a = (Code.Fail) c;			
			writer.write_u2(FAIL);
			writer.write_u2(constantPool.get(new Constant.Utf8(a.msg)));
		} else if(c instanceof Code.Label) {
			Code.Label a = (Code.Label) c;			
			writer.write_u2(LABEL);
			writer.write_u2(constantPool.get(new Constant.Utf8(a.label)));
		} else if(c instanceof IfGoto) {
			IfGoto a = (IfGoto) c;			
			writer.write_u2(IFGOTO);
			WhileyType.write(a.type, writer, constantPool);
			writer.write_u2(COP2INT(a.op));
			writer.write_u2(constantPool.get(new Constant.Utf8(a.target)));
			writeCExpr(a.lhs,writer,constantPool);
			writeCExpr(a.rhs,writer,constantPool);
		} else if(c instanceof Forall) {
			Forall a = (Forall) c;	
									
		} 
	}	
	
	protected static void writeCExpr(CExpr rval, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		if(rval instanceof Value) {
			writeValue((Value)rval,writer,constantPool);
		} else if(rval instanceof CExpr.Variable) {
			writeRVal((CExpr.Variable)rval,writer,constantPool);
		} else {
			writeRVal((CExpr.Register)rval,writer,constantPool);
		}
	}	
	
	public static void writeRVal(CExpr.Variable expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		// The encoding of variables could be optimised to avoid using the
		// constant pool in most, if not all cases.
		writer.write_u2(VARIABLE);				
		int idx = constantPool.get(new Constant.Utf8(expr.name));							
		writer.write_u2(idx);						
	}
	
	public static void writeRVal(CExpr.Register expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		// The encoding of variables could be optimised to avoid using the
		// constant pool in most, if not all cases.
		writer.write_u2(REGISTER);												
		writer.write_u2(expr.index);						
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
		private final String name;
		
		public Reader(String name) {
			this.name = name;
		}
		public String name() {			
			return name;
		}
		
		public WhileyBlock read(BinaryInputStream reader,
				Map<Integer, Constant.Info> constantPool) throws IOException {
			int idx = reader.read_u2();
			Constant.Utf8 utf8 = (Constant.Utf8) constantPool.get(idx);
			return new WhileyBlock(utf8.str, readBlock(reader, constantPool));
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
		protected static CExpr readRVal(BinaryInputStream reader,
				Map<Integer, Constant.Info> constantPool) throws IOException {		
			int code = reader.read_u2();				
			switch (code) {
			case VARIABLE:
			{
				// The encoding of variables could be optimised to avoid using the
				// constant pool in most, if not all cases.
				int idx = reader.read_u2();			
				Constant.Utf8 utf8 = (Constant.Utf8) constantPool.get(idx);
				Type type = WhileyType.Reader.readType(reader, constantPool);
				return CExpr.VAR(type,utf8.str);
			}
			case REGISTER:
			{
				// The encoding of variables could be optimised to avoid using the
				// constant pool in most, if not all cases.
				int idx = reader.read_u2();							
				Type type = WhileyType.Reader.readType(reader, constantPool);
				return CExpr.REG(type,idx);
			}
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
			{
				len = reader.read_u2();
				HashMap<String,Value> tvs = new HashMap<String,Value>();
				for(int i=0;i!=len;++i) {
					int idx = reader.read_u2();
					Constant.Utf8 utf8 = (Constant.Utf8) constantPool.get(idx);
					Value lhs = (Value) readRVal(reader, constantPool);
					tvs.put(utf8.str, lhs);
				}
				return Value.V_TUPLE(tvs);
			}
			default:
				throw new RuntimeException("Unknown RVal encountered in WhileyBlock");
			}		
		}
	}
	
	public static int COP2INT(Code.COP op) {
		switch (op) {
		case EQ:
			return EQ;
		case NEQ:
			return NEQ;
		case LT:
			return LT;
		case LTEQ:
			return LTEQ;
		case GT:
			return GT;
		case GTEQ:
			return GTEQ;
		case SUBSET:
			return SUBSET;
		case SUBSETEQ:
			return SUBSETEQ;
		case ELEMOF:
			return ELEMOF;
		}
		
		throw new IllegalArgumentException("Invalid Code.COP encountered: " + op);
	}
	
	// =========== CODES ===============
	
	private final static int ASSIGN = 0;
	private final static int CODEEXPR = 1;
	private final static int GOTO = 2;
	private final static int IFGOTO = 3;	
	private final static int LABEL = 4;
	private final static int FAIL = 5;
	private final static int FORALL = 6;
	
	// =========== COP ===============
	
	private final static int EQ = 12;
	private final static int NEQ = 13;
	private final static int LT = 14;
	private final static int LTEQ = 15;
	private final static int GT = 16;
	private final static int GTEQ = 17;
	private final static int SUBSET = 18;
	private final static int SUBSETEQ = 19;
	private final static int ELEMOF = 20;
	
	
	// =========== CEXPR ===============
		
	private final static int NULL = 0;
	private final static int VARIABLE = 1;
	private final static int REGISTER = 2;
	private final static int INVOKE = 3;		
	
	private final static int TRUE = 4;
	private final static int FALSE = 5;
	private final static int NONE = 7;
	private final static int SOME = 10;
	
	
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
