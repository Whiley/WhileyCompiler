package wyil.jvm.attributes;


import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import wyil.jvm.rt.BigRational;
import wyil.lang.*;
import wyil.lang.Stmt;
import wyil.lang.Code.*;
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
									
		} else {
			throw new IllegalArgumentException("Invalid code for WhileyBlock: " + c);
		}
	}
	
	public static void addPoolItems(CExpr rval, Set<Constant.Info> constantPool) {
		if(rval instanceof CExpr.Variable) {
			CExpr.Variable var = (CExpr.Variable) rval;
			constantPool.add(new Constant.Utf8(var.name));
		} else if(rval instanceof CExpr.UnOp) {
			CExpr.UnOp c = (CExpr.UnOp)rval;
			addPoolItems(c.rhs,constantPool);			
		} else if(rval instanceof CExpr.BinOp) {
			CExpr.BinOp bop = (CExpr.BinOp)rval; 
			addPoolItems(bop.lhs,constantPool);
			addPoolItems(bop.rhs,constantPool);			
		} else if(rval instanceof CExpr.NaryOp) {
			CExpr.NaryOp nop = (CExpr.NaryOp)rval; 
			for(CExpr arg : nop.args) {
				addPoolItems(arg,constantPool);
			}
		} else if(rval instanceof CExpr.Invoke) {
			CExpr.Invoke ivk = (CExpr.Invoke)rval; 
			for(CExpr arg : ivk.args) {
				addPoolItems(arg,constantPool);
			}			
		} else if(rval instanceof CExpr.Convert) {
			CExpr.Convert c = (CExpr.Convert)rval;
			addPoolItems(c.rhs,constantPool);			
		} else {
			throw new IllegalArgumentException("Unknown expression encountered: " + rval);
		}	
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
				writer.write_u1(ASSIGN);
				writeCExpr(a.lhs,writer,constantPool);
				writeCExpr(a.rhs,writer,constantPool);
			} else {
				writer.write_u1(CODEEXPR);				
				writeCExpr(a.rhs,writer,constantPool);
			}			
		} else if(c instanceof Code.Goto) {
			Code.Goto a = (Code.Goto) c;			
			writer.write_u1(GOTO);
			writer.write_u2(constantPool.get(new Constant.Utf8(a.target)));
		} else if(c instanceof Code.Fail) {
			Code.Fail a = (Code.Fail) c;			
			writer.write_u1(FAIL);
			writer.write_u2(constantPool.get(new Constant.Utf8(a.msg)));
		} else if(c instanceof Code.Label) {
			Code.Label a = (Code.Label) c;			
			writer.write_u1(LABEL);
			writer.write_u2(constantPool.get(new Constant.Utf8(a.label)));
		} else if(c instanceof IfGoto) {
			IfGoto a = (IfGoto) c;			
			writer.write_u1(IFGOTO);
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
			writeCExpr((CExpr.Variable)rval,writer,constantPool);
		} else if(rval instanceof CExpr.Register) {
			writeCExpr((CExpr.Register)rval,writer,constantPool);
		} else if(rval instanceof CExpr.UnOp) {
			writeCExpr((CExpr.UnOp)rval,writer,constantPool);
		} else if(rval instanceof CExpr.BinOp) {
			writeCExpr((CExpr.BinOp)rval,writer,constantPool);
		} else if(rval instanceof CExpr.NaryOp) {
			writeCExpr((CExpr.NaryOp)rval,writer,constantPool);
		} else if(rval instanceof CExpr.Invoke) {
			writeCExpr((CExpr.Invoke)rval,writer,constantPool);
		} else if(rval instanceof CExpr.Convert) {
			writeCExpr((CExpr.Convert)rval,writer,constantPool);
		} else {
			throw new IllegalArgumentException("Unknown expression encountered: " + rval);
		}
	}	
	
	public static void writeCExpr(CExpr.Variable expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u1(VARIABLE);				
		int idx = constantPool.get(new Constant.Utf8(expr.name));							
		writer.write_u2(idx);						
	}
	
	public static void writeCExpr(CExpr.Register expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u1(REGISTER);												
		writer.write_u2(expr.index);						
	}
	
	public static void writeCExpr(CExpr.UnOp expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u1(UOP2INT(expr.op));																			
		writeCExpr(expr.rhs,writer,constantPool);
	}
	
	public static void writeCExpr(CExpr.BinOp expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u1(BOP2INT(expr.op));												
		writeCExpr(expr.lhs,writer,constantPool);						
		writeCExpr(expr.rhs,writer,constantPool);
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
		
		throw new IllegalArgumentException(
				"Invalid conditional operation encountered: " + op);
	}
	
	public static int BOP2INT(CExpr.BOP op) {
		switch (op) {
		case ADD:
			return ADD;
		case SUB:
			return SUB;
		case MUL:
			return MUL;
		case DIV:
			return DIV;
		case UNION:
			return UNION;
		case INTERSECT:
			return INTERSECT;
		case DIFFERENCE:
			return DIFFERENCE;
		case APPEND:
			return APPEND;
		}

		throw new IllegalArgumentException(
				"Invalid binary operation encountered: " + op);
	}
	
	public static int UOP2INT(CExpr.UOP op) {
		switch (op) {
		case NEG:
			return NEG;
		case NOT:
			return NOT;
		case LENGTHOF:
			return LENGTHOF;
		case PROCESSACCESS:
			return PROCESSACCESS;
		case PROCESSSPAWN:
			return PROCESSSPAWN;
		}

		throw new IllegalArgumentException(
				"Invalid binary operation encountered: " + op);
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
	
	// =========== UOP ===============
	private final static int NEG = 0;
	private final static int NOT = 1;
	private final static int LENGTHOF = 2;
	private final static int PROCESSACCESS = 3;
	private final static int PROCESSSPAWN = 4;
	
	// =========== BOP ===============
	private final static int ADD = 0;
	private final static int SUB = 1;
	private final static int MUL = 2;
	private final static int DIV = 3;
	private final static int UNION = 4;
	private final static int INTERSECT = 5;
	private final static int DIFFERENCE = 6;
	private final static int APPEND = 7;
	
	
	// =========== CEXPR ===============
		
	private final static int NULL = 0;
	private final static int TRUE = 1;
	private final static int FALSE = 2;	
	private final static int INTVAL = 3;
	private final static int REALVAL = 4;
	private final static int SETVAL = 5;
	private final static int LISTVAL = 6;
	private final static int TUPLEVAL = 7;
	private final static int TYPEVAL = 8;
	
	private final static int VARIABLE = 10;
	private final static int REGISTER = 11;
	private final static int INVOKE = 12;		
	private final static int UNOP = 13;
	private final static int BINOP = 14;
	private final static int NARYOP = 15;
	private final static int LISTACCESS = 16;
	private final static int TUPLEACCESS = 17;
	private final static int CONVERT = 18;
	
				
}
