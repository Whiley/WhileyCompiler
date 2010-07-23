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

import static wyjc.util.SyntaxError.syntaxError;

import java.io.*;
import java.math.*;
import java.util.*;

import wyjc.ast.exprs.*;
import wyjc.ast.exprs.integer.*;
import wyjc.ast.exprs.list.*;
import wyjc.ast.exprs.logic.*;
import wyjc.ast.exprs.real.*;
import wyjc.ast.exprs.set.*;
import wyjc.ast.exprs.tuple.*;
import wyjc.ast.types.*;
import wyjc.jvm.rt.BigRational;
import wyjc.util.*;
import wyjvm.io.BinaryInputStream;
import wyjvm.io.BinaryOutputStream;
import wyjvm.lang.BytecodeAttribute;
import wyjvm.lang.BytecodeAttributeReader;
import wyjvm.lang.Constant;
/**
 * A WhileyCondition attribute corresponds to a whiley.ast.exprs.Condition
 * expression. The purpose of the attribute is to allow the conditions to be
 * stored into a class file, such that they can be retrieved and checked against
 * during compilation.
 * 
 * @author djp
 * 
 */

public class WhileyType implements BytecodeAttribute {	
	private Type type;
	
	public WhileyType(Type type) {		
		this.type = type;
	}
	
	public Type type() {
		return type;
	}
	
	public String name() {
		return "WhileyType";
	}
	
	public void addPoolItems(Set<Constant.Info> constantPool, ClassLoader loader) {
		Constant.addPoolItem(new Constant.Utf8(name()), constantPool);						
		addPoolItems(type, constantPool);
	}
	

	protected static void addPoolItems(Type type,
			Set<Constant.Info> constantPool) {
		if(type.constraint() != null) {
			addPoolItems(type.constraint(),constantPool);
		}
		if(type instanceof ListType) {
			ListType lt = (ListType) type;
			addPoolItems(lt.element(),constantPool);
		} else if(type instanceof SetType) {
			SetType st = (SetType) type;
			addPoolItems(st.element(),constantPool);
		} else if(type instanceof TupleType) {
			TupleType tt = (TupleType) type;
			for(Map.Entry<String,Type> p : tt.types().entrySet()) {
				Constant.Utf8 utf8 = new Constant.Utf8(p.getKey());
				Constant.addPoolItem(utf8,constantPool);
				addPoolItems(p.getValue(),constantPool);
			}
		} else if(type instanceof UnionType) {
			UnionType ut = (UnionType) type;			
			for(Type p : ut.types()) {
				addPoolItems(p,constantPool);	
			}	
		} else if(type instanceof ProcessType) {
			ProcessType st = (ProcessType) type;
			addPoolItems(st.element(),constantPool);
		} else if(type instanceof NamedType) {
			NamedType lt = (NamedType) type;
			Constant.Utf8 utf8 = new Constant.Utf8(lt.module().toString());
			Constant.addPoolItem(utf8,constantPool);
			utf8 = new Constant.Utf8(lt.name());	
			Constant.addPoolItem(utf8,constantPool);
			addPoolItems(lt.type(),constantPool);
		} else if(type instanceof RecursiveType) {
			RecursiveType lt = (RecursiveType) type;
			String name = lt.name();
			Constant.Utf8 utf8 = new Constant.Utf8(name);
			Constant.addPoolItem(utf8,constantPool);					
			if(lt.type() != null) {
				addPoolItems(lt.type(),constantPool);
			}
		} else if(type instanceof FunType) {
			FunType ft = (FunType) type;
			for(Type t : ft.parameters()) {
				addPoolItems(t,constantPool);
			}
			addPoolItems(ft.returnType(),constantPool);
		}
	}
		
	public static void write(Type t, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		
		Condition constraint = t.constraint();
		byte mask = (byte) (constraint == null ? 0 : CONSTRAINT_MASK);		
		
		if(t == Types.T_ANY) {
			writer.write_u1(ANY_TYPE | mask);
		} else if(t == Types.T_EXISTENTIAL) {
			writer.write_u1(EXISTENTIAL_TYPE | mask);
		} else if(t == Types.T_VOID) {
			writer.write_u1(VOID_TYPE);
		} else if(t instanceof BoolType) {
			writer.write_u1(BOOL_TYPE | mask);
			if(constraint != null) { writeCondition(constraint,writer,constantPool); }
		} else if(t instanceof IntType) {
			writer.write_u1(INT_TYPE | mask);
			if(constraint != null) { writeCondition(constraint,writer,constantPool); }			
		} else if(t instanceof RealType) {
			writer.write_u1(REAL_TYPE | mask);
			if(constraint != null) { writeCondition(constraint,writer,constantPool); }
		} else if(t instanceof ListType) {
			ListType lt = (ListType) t;
			writer.write_u1(LIST_TYPE | mask);
			if(constraint != null) { writeCondition(constraint,writer,constantPool); }
			write(lt.element(),writer,constantPool);
		} else if(t instanceof SetType) {
			SetType st = (SetType) t;
			writer.write_u1(SET_TYPE | mask);
			if(constraint != null) { writeCondition(constraint,writer,constantPool); }
			write(st.element(),writer,constantPool);
		} else if(t instanceof TupleType) {
			TupleType tt = (TupleType) t;
			writer.write_u1(TUPLE_TYPE | mask);
			if(constraint != null) { writeCondition(constraint,writer,constantPool); }
			// FIXME: bug here if number of entries > 64K
			writer.write_u2(tt.types().size());
			for(Map.Entry<String,Type> p : tt.types().entrySet()) {
				Constant.Utf8 utf8 = new Constant.Utf8(p.getKey());
				writer.write_u2(constantPool.get(utf8));
				write(p.getValue(),writer,constantPool);	
			}			
		} else if(t instanceof UnionType) {
			UnionType ut = (UnionType) t;
			writer.write_u1(UNION_TYPE | mask);
			if(constraint != null) { writeCondition(constraint,writer,constantPool); }
			// FIXME: bug here if number of bounds > 64K
			writer.write_u2(ut.types().size());
			for(Type p : ut.types()) {
				write(p,writer,constantPool);	
			}			
		} else if(t instanceof ProcessType) {
			ProcessType st = (ProcessType) t;
			writer.write_u1(PROCESS_TYPE | mask);
			if(constraint != null) { writeCondition(constraint,writer,constantPool); }
			write(st.element(),writer,constantPool);
		} else if(t instanceof NamedType) {
			NamedType st = (NamedType) t;
			writer.write_u1(NAMED_TYPE | mask);
			Constant.Utf8 utf8 = new Constant.Utf8(st.module().toString());
			writer.write_u2(constantPool.get(utf8));
			utf8 = new Constant.Utf8(st.name());
			writer.write_u2(constantPool.get(utf8));
			write(st.type(),writer,constantPool);
		} else if(t instanceof RecursiveType) {
			RecursiveType st = (RecursiveType) t;
			if(st.type() != null) {
				writer.write_u1(RECURSIVE_TYPE | mask);
				if(constraint != null) { writeCondition(constraint,writer,constantPool); }
			} else {
				writer.write_u1(RECURSIVE_LEAF);
			}
			String name = st.name();
			Constant.Utf8 utf8 = new Constant.Utf8(name);
			writer.write_u2(constantPool.get(utf8));			
			if(st.type() != null) {
				write(st.type(),writer,constantPool);
			}
		} else if(t instanceof FunType) {
			FunType st = (FunType) t;
			writer.write_u1(FUN_TYPE | mask);
			if(constraint != null) { writeCondition(constraint,writer,constantPool); }
			write(st.returnType(),writer,constantPool);
			writer.write_u2(st.parameters().size());
			for(Type p : st.parameters()) {
				write(p,writer,constantPool);
			}
		} else {
			throw new RuntimeException("unknown type encountered: " + t);
		}
	}
	
	
	public static void addPoolItems(Expr expr, Set<Constant.Info> constantPool) {
		try {
			if (expr instanceof BoolVal || expr instanceof IntVal
					|| expr instanceof RealVal) {
				// nop
			} else if (expr instanceof UnOp) {
				addPoolItems(((UnOp) expr).mhs(), constantPool);
			} else if(expr instanceof BinOp) {
				addPoolItems(((BinOp)expr).lhs(),constantPool);
				addPoolItems(((BinOp)expr).rhs(),constantPool);				
			} else if(expr instanceof SetVal) {
				SetVal sg = (SetVal) expr;
				for(Expr e : sg.getValues()) {
					addPoolItems(e,constantPool);
				}				
			} else if(expr instanceof SetGenerator) {
				SetGenerator sg = (SetGenerator) expr;
				for(Expr e : sg.getValues()) {
					addPoolItems(e,constantPool);
				}				
			} else if(expr instanceof SetComprehension) {
				SetComprehension sc = (SetComprehension) expr;
				for(Pair<String,Expr> s : sc.sources()) {
					Constant.addPoolItem(new Constant.Utf8(s.first()), constantPool);					
					addPoolItems(s.second(),constantPool);
				}
				addPoolItems(sc.condition(),constantPool);
			} else if(expr instanceof ListVal) {
				ListVal sg = (ListVal) expr;
				for(Expr e : sg.getValues()) {
					addPoolItems(e,constantPool);
				}				
			} else if(expr instanceof ListGenerator) {
				ListGenerator sg = (ListGenerator) expr;
				for(Expr e : sg.getValues()) {
					addPoolItems(e,constantPool);
				}				
			} else if(expr instanceof ListAccess) {
				ListAccess la = (ListAccess) expr;
				addPoolItems(la.source(),constantPool);
				addPoolItems(la.index(),constantPool);
			} else if(expr instanceof TupleAccess) {
				TupleAccess ta = (TupleAccess) expr;
				Constant.addPoolItem(new Constant.Utf8(ta.name()), constantPool);				
				addPoolItems(ta.source(), constantPool);
			} else if(expr instanceof TupleVal) {
				TupleVal tv = (TupleVal) expr;
				for(Map.Entry<String,Value> v : tv.values().entrySet()) {
					Constant.addPoolItem(new Constant.Utf8(v.getKey()), constantPool);					
					addPoolItems(v.getValue(),constantPool);
				}
			} else if(expr instanceof TupleGenerator) {				
				TupleGenerator tv = (TupleGenerator) expr;
				for(Map.Entry<String,Expr> v : tv.values().entrySet()) {
					Constant.addPoolItem(new Constant.Utf8(v.getKey()), constantPool);					
					addPoolItems(v.getValue(),constantPool);
				}
			} else if(expr instanceof Variable) {
				Variable v = (Variable) expr;
				Constant.addPoolItem(new Constant.Utf8(v.name()), constantPool);				
			} else if(expr instanceof Invoke) {
				Invoke v = (Invoke) expr;
				addPoolItems(v.funType(), constantPool);
				Constant.addPoolItem(new Constant.Utf8(v.module().toString()), constantPool);
				Constant.addPoolItem(new Constant.Utf8(v.name()), constantPool);
				for(Expr e : v.arguments()) {
					addPoolItems(e,constantPool);
				}	
			} else if(expr instanceof TypeGate) {
				TypeGate la = (TypeGate) expr;
				addPoolItems(la.lhsTest(), constantPool);
				Constant.addPoolItem(new Constant.Utf8(la.variable()), constantPool);
				addPoolItems(la.lhs(),constantPool);
				addPoolItems(la.rhs(),constantPool);
			} else if(expr instanceof TypeEquals) {
				TypeEquals la = (TypeEquals) expr;
				addPoolItems(la.lhsTest(), constantPool);
				Constant.addPoolItem(new Constant.Utf8(la.variable()), constantPool);
				addPoolItems(la.lhs(),constantPool);
				addPoolItems(la.rhs(),constantPool);
			} else {							
				syntaxError("unknown expression encountered (" + expr.getClass().getName() + ")",expr);			
			}
		} catch(SyntaxError se) {
			throw se;
		} catch(Exception ex) {
			syntaxError("internal failure",expr,ex);
		}
	}
	
	public void write(BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool, ClassLoader loader) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BinaryOutputStream iw = new BinaryOutputStream(out);
		write(type,iw,constantPool);
		writer.write_u2(constantPool.get(new Constant.Utf8(name())));
		writer.write_u4(out.size());		
		writer.write(out.toByteArray());						
	}
	
	
	public void print(PrintWriter output,
			Map<Constant.Info, Integer> constantPool, ClassLoader loader)
			throws IOException {
		output.print(  name() + ":");
	}	
	
	protected static void writeCondition(Expr expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) {
		try {
			if(expr instanceof BoolVal) {
				writeCondition((BoolVal)expr, writer, constantPool);
			} else if(expr instanceof UnOp) {
				writeCondition((UnOp)expr, writer, constantPool);
			} else if(expr instanceof BinOp) {
				writeCondition((BinOp)expr, writer, constantPool);
			} else if(expr instanceof IntVal) {			
				writeCondition((IntVal)expr, writer, constantPool);
			} else if(expr instanceof RealVal) {			
				writeCondition((RealVal)expr, writer, constantPool);
			} else if(expr instanceof SetVal) {
				writeCondition((SetVal)expr, writer, constantPool);
			} else if(expr instanceof SetGenerator) {
				writeCondition((SetGenerator)expr, writer, constantPool);
			} else if(expr instanceof SetComprehension) {
				writeCondition((SetComprehension)expr, writer, constantPool);
			} else if(expr instanceof Subset) {
				writeCondition((Subset)expr, writer, constantPool);
			} else if(expr instanceof ListVal) {
				writeCondition((ListVal)expr, writer, constantPool);
			} else if(expr instanceof ListGenerator) {
				writeCondition((ListGenerator)expr, writer, constantPool);
			} else if(expr instanceof ListAccess) {
				writeCondition((ListAccess)expr, writer, constantPool);
			} else if(expr instanceof TupleVal) {
				writeCondition((TupleVal)expr, writer, constantPool);
			} else if(expr instanceof TupleGenerator) {
				writeCondition((TupleGenerator)expr, writer, constantPool);
			} else if(expr instanceof TupleAccess) {
				writeCondition((TupleAccess)expr, writer, constantPool);
			} else if(expr instanceof Invoke) {
				writeCondition((Invoke)expr, writer, constantPool);
			} else if(expr instanceof Variable) {
				writeCondition((Variable)expr, writer, constantPool);
			} else if(expr instanceof TypeGate) {
				writeCondition((TypeGate)expr, writer, constantPool);
			} else if(expr instanceof TypeEquals) {
				writeCondition((TypeEquals)expr, writer, constantPool);
			} else {
				syntaxError("unknown expression encountered (" + expr.getClass().getName() + ")",expr);			
			}
		} catch(Exception ex) {
			syntaxError("internal failure",expr,ex);
		}
	}
	
	public static void writeCondition(BoolVal expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		
		if(expr.value()) {
			writer.write_u2(TRUE);
		} else {
			writer.write_u2(FALSE);
		}
	}
	
	public static void writeCondition(IntVal expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u2(INTVAL);
		BigInteger bi = expr.value();
		byte[] bibytes = bi.toByteArray();
		// FIXME: bug here for constants that require more than 65535 bytes
		writer.write_u2(bibytes.length);
		writer.write(bibytes);
	}
	
	public static void writeCondition(RealVal expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u2(REALVAL);
		BigRational br = expr.value();
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
	
	public static void writeCondition(SetVal expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u2(SETVAL);
		writer.write_u2(expr.getValues().size());
		for(Value v : expr.getValues()) {
			writeCondition(v,writer,constantPool);
		}
	}
	
	public static void writeCondition(SetGenerator expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u2(SETGEN);
		writer.write_u2(expr.getValues().size());
		for(Expr v : expr.getValues()) {
			writeCondition(v,writer,constantPool);
		}
	}
	
	public static void writeCondition(SetComprehension expr,
			BinaryOutputStream writer, Map<Constant.Info, Integer> constantPool)
			throws IOException {
		writer.write_u2(SETCOMPREHENSION);
		writeCondition(expr.sign(), writer, constantPool);
		writer.write_u2(expr.sources().size());
		for (Pair<String, Expr> s : expr.sources()) {
			writer.write_u2(constantPool.get(new Constant.Utf8(s.first())));
			writeCondition(s.second(), writer, constantPool);
		}
		if (expr.condition() == null) {
			writer.write_u2(NULL);
		} else {
			writeCondition(expr.condition(), writer, constantPool);
		}
	}
	
	public static void writeCondition(ListVal expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u2(LISTVAL);
		writer.write_u2(expr.getValues().size());
		for(Value v : expr.getValues()) {
			writeCondition(v,writer,constantPool);
		}
	}
	
	public static void writeCondition(ListGenerator expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u2(LISTGEN);
		writer.write_u2(expr.getValues().size());
		for(Expr v : expr.getValues()) {
			writeCondition(v,writer,constantPool);
		}
	}
	
	public static void writeCondition(TupleVal expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u2(TUPLEVAL);
		writer.write_u2(expr.values().size());
		for(Map.Entry<String,Value> v : expr.values().entrySet()) {
			writer.write_u2(constantPool.get(new Constant.Utf8(v.getKey())));
			writeCondition(v.getValue(), writer, constantPool);
		}
	}
	public static void writeCondition(TupleGenerator expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {		
		writer.write_u2(TUPLEGEN);
		writer.write_u2(expr.values().size());
		for(Map.Entry<String,Expr> v : expr.values().entrySet()) {
			writer.write_u2(constantPool.get(new Constant.Utf8(v.getKey())));
			writeCondition(v.getValue(), writer, constantPool);
		}
	}
	
	public static void writeCondition(UnOp expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		
		if(expr instanceof Not) {
			writer.write_u2(NOT);
		} else if(expr instanceof Some) {
			writer.write_u2(SOME);
		} else if(expr instanceof None) {			
			writer.write_u2(NONE);
		} else if(expr instanceof IntNegate) {
			writer.write_u2(INTNEG);
		} else if(expr instanceof RealNegate) {
			writer.write_u2(REALNEG);
		} else if(expr instanceof SetLength) {
			writer.write_u2(SETLENGTH);
		} else if(expr instanceof ListLength) {
			writer.write_u2(LISTLENGTH);
		} else {
			syntaxError("unknown expression encountered: " + expr.getClass().getName(),expr);
		}
		
		writeCondition(expr.mhs(),writer,constantPool);
	}
	
	public static void writeCondition(BinOp expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {		
		if(expr instanceof And) {
			writer.write_u2(AND);
		} else if(expr instanceof BoolEquals) {			
			writer.write_u2(BOOLEQ);
		} else if(expr instanceof BoolNotEquals) {
			writer.write_u2(BOOLNEQ);
		} else if(expr instanceof Or) {
			writer.write_u2(OR);
		} else if(expr instanceof IntAdd) {
			writer.write_u2(INTADD);
		} else if(expr instanceof IntDiv) {
			writer.write_u2(INTDIV);
		} else if(expr instanceof IntMul) {
			writer.write_u2(INTMUL);
		} else if(expr instanceof IntSub) {
			writer.write_u2(INTSUB);
		} else if(expr instanceof IntEquals) {			
			writer.write_u2(INTEQ);
		} else if(expr instanceof IntNotEquals) {
			writer.write_u2(INTNEQ);
		} else if(expr instanceof IntLessThan) {			
			writer.write_u2(INTLT);
		} else if(expr instanceof IntLessThanEquals) {
			writer.write_u2(INTLTEQ);
		} else if(expr instanceof IntGreaterThan) {
			writer.write_u2(INTGT);
		} else if(expr instanceof IntGreaterThanEquals) {
			writer.write_u2(INTGTEQ);
		} else if(expr instanceof RealAdd) {
			writer.write_u2(REALADD);
		} else if(expr instanceof RealDiv) {
			writer.write_u2(REALDIV);
		} else if(expr instanceof RealMul) {
			writer.write_u2(REALMUL);
		} else if(expr instanceof RealSub) {
			writer.write_u2(REALSUB);
		} else if(expr instanceof RealEquals) {
			writer.write_u2(REALEQ);
		} else if(expr instanceof RealNotEquals) {
			writer.write_u2(REALNEQ);
		} else if(expr instanceof RealLessThan) {
			writer.write_u2(REALLT);
		} else if(expr instanceof RealLessThanEquals) {
			writer.write_u2(REALLTEQ);
		} else if(expr instanceof RealGreaterThan) {
			writer.write_u2(REALGT);
		} else if(expr instanceof RealGreaterThanEquals) {
			writer.write_u2(REALGTEQ);
		} else if(expr instanceof SetEquals) {
			writer.write_u2(SETEQ);
		} else if(expr instanceof SetNotEquals) {
			writer.write_u2(SETNEQ);
		} else if(expr instanceof Subset) {
			writer.write_u2(SETSUBSET);
		} else if(expr instanceof SubsetEq) {
			writer.write_u2(SETSUBSETEQ);
		} else if(expr instanceof SetElementOf) {
			writer.write_u2(SETELEMOF);
		} else if(expr instanceof SetUnion) {
			writer.write_u2(SETUNION);
		} else if(expr instanceof SetIntersection) {
			writer.write_u2(SETINTERSECT);
		} else if(expr instanceof SetDifference) {
			writer.write_u2(SETDIFFERENCE);
		} else if(expr instanceof ListEquals) {
			writer.write_u2(LISTEQ);
		} else if(expr instanceof ListNotEquals) {
			writer.write_u2(LISTNEQ);
		} else if(expr instanceof ListElementOf) {
			writer.write_u2(LISTELEMOF);
		} else if(expr instanceof TupleEquals) {
			writer.write_u2(TUPLEEQ);
		} else if(expr instanceof TupleNotEquals) {
			writer.write_u2(TUPLENEQ);
		} else {
			syntaxError("unknown expression encountered: ",expr);
		}
		
		writeCondition(expr.lhs(),writer,constantPool);
		writeCondition(expr.rhs(),writer,constantPool);
	}
	
	public static void writeCondition(ListAccess expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u2(LISTACCESS);
		writeCondition(expr.source(),writer,constantPool);
		writeCondition(expr.index(),writer,constantPool);
	}
	
	public static void writeCondition(TupleAccess expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u2(TUPLEACCESS);
		writer.write_u2(constantPool.get(new Constant.Utf8(expr.name())));		
		writeCondition(expr.source(),writer,constantPool);		
	}
	
	public static void writeCondition(Variable expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		// The encoding of variables could be optimised to avoid using the
		// constant pool in most, if not all cases.
		writer.write_u2(VARIABLE);				
		int idx = constantPool.get(new Constant.Utf8(expr.name()));							
		writer.write_u2(idx);				
	}
	
	public static void writeCondition(TypeGate expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u2(TYPEGATE);		
		write(expr.lhsTest(),writer,constantPool);
		writer.write_u2(constantPool.get(new Constant.Utf8(expr.variable())));
		writeCondition(expr.lhs(),writer,constantPool);
		writeCondition(expr.rhs(),writer,constantPool);
	}
	
	public static void writeCondition(TypeEquals expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		writer.write_u2(TYPEEQUALS);		
		write(expr.lhsTest(),writer,constantPool);
		writer.write_u2(constantPool.get(new Constant.Utf8(expr.variable())));
		writeCondition(expr.lhs(),writer,constantPool);
		writeCondition(expr.rhs(),writer,constantPool);
	}
	
	public static void writeCondition(Invoke expr, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {		
		writer.write_u2(INVOKE);
		Constant.Utf8 utf8 = new Constant.Utf8(expr.module().toString()); 
		writer.write_u2(constantPool.get(utf8));
		write(expr.funType(),writer,constantPool);
		utf8 = new Constant.Utf8(expr.name()); 
		writer.write_u2(constantPool.get(utf8));
		writer.write_u2(expr.arguments().size());
		for(Expr e : expr.arguments()) {
			writeCondition(e,writer,constantPool);
		}
	}
	
	public static class Reader implements BytecodeAttributeReader {
		public String name() {
			return "WhileyType";
		}
		
		public WhileyType read(BinaryInputStream input,
				Map<Integer, Constant.Info> constantPool) throws IOException {
			input.read_u2(); // attribute name index code
			input.read_u4(); // attribute length 						
			Type t = readType(input,constantPool);
			return new WhileyType(t);		
		}

		protected static Condition readCondition(BinaryInputStream reader,
				Map<Integer, Constant.Info> constantPool) throws IOException {							
			int code = reader.read_u2();
			return readCondition(code,reader,constantPool);
		}
		
		protected static Condition readCondition(int code, BinaryInputStream reader,
				Map<Integer, Constant.Info> constantPool) throws IOException {							
			switch (code) {
				case TRUE:
					return new BoolVal(true);
				case FALSE:
					return new BoolVal(false);
				case AND:
					Condition lhs = readCondition(reader,constantPool);
					Condition rhs = readCondition(reader,constantPool);
					return new And(lhs,rhs);				
				case NONE:
					Expr expr = readExpr(reader,constantPool);					
					return new None((SetComprehension)expr);
				case NOT:
					lhs = readCondition(reader,constantPool);
					return new Not(lhs);
				case OR:
					lhs = readCondition(reader,constantPool);
					rhs = readCondition(reader,constantPool);
					return new Or(lhs,rhs);
				case SOME:
					expr = readExpr(reader,constantPool);
					return new Some((SetComprehension)expr);
				case BOOLEQ:
					Expr e1 = readExpr(reader, constantPool);
					Expr e2 = readExpr(reader, constantPool);
					return new BoolEquals(e1,e2);
				case BOOLNEQ:
					e1 = readExpr(reader, constantPool);
					e2 = readExpr(reader, constantPool);
					return new BoolNotEquals(e1,e2);				
				case INTEQ:
					e1 = readExpr(reader, constantPool);
					e2 = readExpr(reader, constantPool);
					return new IntEquals(e1,e2);
				case INTGT:
					e1 = readExpr(reader, constantPool);
					e2 = readExpr(reader, constantPool);
					return new IntGreaterThan(e1,e2);
				case INTGTEQ:
					e1 = readExpr(reader, constantPool);
					e2 = readExpr(reader, constantPool);
					return new IntGreaterThanEquals(e1,e2);
				case INTLT:				
					e1 = readExpr(reader, constantPool);
					e2 = readExpr(reader, constantPool);
					return new IntLessThan(e1,e2);
				case INTLTEQ:
					e1 = readExpr(reader, constantPool);
					e2 = readExpr(reader, constantPool);
					return new IntLessThanEquals(e1,e2);
				case INTNEQ:
					e1 = readExpr(reader, constantPool);
					e2 = readExpr(reader, constantPool);
					return new IntNotEquals(e1,e2);
				case REALEQ:
					e1 = readExpr(reader, constantPool);
					e2 = readExpr(reader, constantPool);
					return new RealEquals(e1,e2);
				case REALGT:
					e1 = readExpr(reader, constantPool);
					e2 = readExpr(reader, constantPool);
					return new RealGreaterThan(e1,e2);
				case REALGTEQ:
					e1 = readExpr(reader, constantPool);
					e2 = readExpr(reader, constantPool);
					return new RealGreaterThanEquals(e1,e2);
				case REALLT:				
					e1 = readExpr(reader, constantPool);
					e2 = readExpr(reader, constantPool);
					return new RealLessThan(e1,e2);
				case REALLTEQ:
					e1 = readExpr(reader, constantPool);
					e2 = readExpr(reader, constantPool);
					return new RealLessThanEquals(e1,e2);
				case REALNEQ:
					e1 = readExpr(reader, constantPool);
					e2 = readExpr(reader, constantPool);
					return new RealNotEquals(e1,e2);
				case LISTELEMOF:
					e1 = readExpr(reader, constantPool);
					e2 = readExpr(reader, constantPool);
					return new ListElementOf(e1,e2);
				case LISTEQ:
					e1 = readExpr(reader, constantPool);
					e2 = readExpr(reader, constantPool);
					return new ListEquals(e1,e2);
				case LISTNEQ:			
					e1 = readExpr(reader, constantPool);
					e2 = readExpr(reader, constantPool);
					return new ListNotEquals(e1,e2);
				case SETELEMOF:
					e1 = readExpr(reader, constantPool);
					e2 = readExpr(reader, constantPool);
					return new SetElementOf(e1,e2);
				case SETEQ:
					e1 = readExpr(reader, constantPool);
					e2 = readExpr(reader, constantPool);
					return new SetEquals(e1,e2);
				case SETNEQ:			
					e1 = readExpr(reader, constantPool);
					e2 = readExpr(reader, constantPool);
					return new SetNotEquals(e1,e2);
				case SETSUBSET:			
					e1 = readExpr(reader, constantPool);
					e2 = readExpr(reader, constantPool);
					return new Subset(e1,e2);
				case SETSUBSETEQ:			
					e1 = readExpr(reader, constantPool);
					e2 = readExpr(reader, constantPool);
					return new SubsetEq(e1,e2);		
				case TUPLEEQ:
					e1 = readExpr(reader, constantPool);
					e2 = readExpr(reader, constantPool);
					return new TupleEquals(e1,e2);
				case TUPLENEQ:			
					e1 = readExpr(reader, constantPool);
					e2 = readExpr(reader, constantPool);
					return new TupleNotEquals(e1,e2);
				case TYPEGATE:			
					Type t = readType(reader,constantPool);
					int idx = reader.read_u2(); 
					e1 = readExpr(reader, constantPool);
					rhs = readCondition(reader, constantPool);
					Constant.Utf8 utf8 = (Constant.Utf8) constantPool.get(idx);
					return new TypeGate(t,utf8.str,e1,rhs);
				case TYPEEQUALS:			
					t = readType(reader,constantPool);
					idx = reader.read_u2(); 
					e1 = readExpr(reader, constantPool);
					rhs = readCondition(reader, constantPool);
					utf8 = (Constant.Utf8) constantPool.get(idx);
					return new TypeEquals(t,utf8.str,e1,rhs);
			} 
			throw new RuntimeException("unknown whiley condition encountered: " + code);	
		}

		protected static Expr readExpr(BinaryInputStream reader,
				Map<Integer, Constant.Info> constantPool) throws IOException {		
			int code = reader.read_u2();				
			switch (code) {
				case VARIABLE:			
					// The encoding of variables could be optimised to avoid using the
					// constant pool in most, if not all cases.
					int idx = reader.read_u2();			
					Constant.Utf8 utf8 = (Constant.Utf8) constantPool.get(idx);
					return new Variable(utf8.str);
				case INVOKE:							
					Constant.Utf8 module = (Constant.Utf8) constantPool.get(reader.read_u2());
					FunType t = (FunType) readType(reader,constantPool);
					Constant.Utf8 name = (Constant.Utf8) constantPool.get(reader.read_u2());
					int size = reader.read_u2();
					ArrayList<Expr> args = new ArrayList<Expr>();
					for(int i=0;i!=size;++i) {
						args.add(readExpr(reader,constantPool));
					}
					return new Invoke(name.str,ModuleID.fromString(module.str),t,null,args);																	
				case INTADD:
					Expr lhs = readExpr(reader, constantPool);
					Expr rhs = readExpr(reader, constantPool);
					return new IntAdd(lhs, rhs);
				case INTDIV:
					lhs = readExpr(reader, constantPool);
					rhs = readExpr(reader, constantPool);
					return new IntDiv(lhs, rhs);			
				case INTMUL:
					lhs = readExpr(reader, constantPool);
					rhs = readExpr(reader, constantPool);
					return new IntMul(lhs, rhs);
				case INTNEG:	
					lhs = readExpr(reader, constantPool);				
					return new IntNegate(lhs);
				case INTSUB:
					lhs = readExpr(reader, constantPool);
					rhs = readExpr(reader, constantPool);
					return new IntSub(lhs, rhs);
				case INTVAL:			
					int len = reader.read_u2();
					byte[] bytes = new byte[len];
					reader.read(bytes);
					BigInteger bi = new BigInteger(bytes);
					return new IntVal(bi);
				case REALADD:
					lhs = readExpr(reader, constantPool);
					rhs = readExpr(reader, constantPool);
					return new RealAdd(lhs, rhs);
				case REALDIV:
					lhs = readExpr(reader, constantPool);
					rhs = readExpr(reader, constantPool);
					return new RealDiv(lhs, rhs);			
				case REALMUL:
					lhs = readExpr(reader, constantPool);
					rhs = readExpr(reader, constantPool);
					return new RealMul(lhs, rhs);
				case REALNEG:	
					lhs = readExpr(reader, constantPool);				
					return new RealNegate(lhs);
				case REALSUB:
					lhs = readExpr(reader, constantPool);
					rhs = readExpr(reader, constantPool);
					return new RealSub(lhs, rhs);
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
					return new RealVal(br);
				case LISTACCESS:			
					lhs = readExpr(reader, constantPool);
					rhs = readExpr(reader, constantPool);
					return new ListAccess(lhs, rhs);		
				case LISTLENGTH:
					lhs = readExpr(reader, constantPool);			
					return new ListLength(lhs);
				case LISTGEN:
					len = reader.read_u2();
					ArrayList<Expr> exprs = new ArrayList<Expr>();
					for(int i=0;i!=len;++i) {
						exprs.add((Expr) readExpr(reader,constantPool));
					}
					return new ListGenerator(exprs);		
				case LISTVAL:
					len = reader.read_u2();
					ArrayList<Value> values = new ArrayList<Value>();
					for(int i=0;i!=len;++i) {
						values.add((Value) readExpr(reader,constantPool));
					}
					return new ListVal(values);
				case SETLENGTH:
					lhs = readExpr(reader, constantPool);			
					return new SetLength(lhs);
				case SETGEN:
					len = reader.read_u2();
					exprs = new ArrayList<Expr>();
					for(int i=0;i!=len;++i) {
						exprs.add((Expr) readExpr(reader,constantPool));
					}
					return new SetGenerator(exprs);		
				case SETVAL:
					len = reader.read_u2();
					values = new ArrayList<Value>();
					for(int i=0;i!=len;++i) {
						values.add((Value) readExpr(reader,constantPool));
					}
					return new SetVal(values);
				case SETUNION:
					lhs = readExpr(reader, constantPool);			
					rhs = readExpr(reader, constantPool);
					return new SetUnion(lhs,rhs);
				case SETINTERSECT:
					lhs = readExpr(reader, constantPool);			
					rhs = readExpr(reader, constantPool);
					return new SetIntersection(lhs,rhs);
				case SETDIFFERENCE:
					lhs = readExpr(reader, constantPool);			
					rhs = readExpr(reader, constantPool);
					return new SetDifference(lhs,rhs);
				case SETCOMPREHENSION:			
					lhs = readExpr(reader, constantPool);
					len = reader.read_u2();		
					ArrayList<Pair<String,Expr>> srcs = new ArrayList<Pair<String,Expr>>();
					for(int i=0;i!=len;++i) {
						idx = reader.read_u2();
						utf8 = (Constant.Utf8) constantPool.get(idx);
						lhs = readExpr(reader, constantPool);
						srcs.add(new Pair<String,Expr>(utf8.str, lhs));
					}
					Condition c = readCondition(reader, constantPool);
					return new SetComprehension(lhs,srcs,c);
				case TUPLEACCESS:			
					idx = reader.read_u2();
					lhs = readExpr(reader, constantPool);
					utf8 = (Constant.Utf8) constantPool.get(idx);
					return new TupleAccess(lhs, utf8.str);		
				case TUPLEGEN:
					len = reader.read_u2();
					HashMap<String,Expr> tgs = new HashMap<String,Expr>();
					for(int i=0;i!=len;++i) {
						idx = reader.read_u2();
						utf8 = (Constant.Utf8) constantPool.get(idx);
						lhs = readExpr(reader, constantPool);
						tgs.put(utf8.str, lhs);
					}
					return new TupleGenerator(tgs);
				case TUPLEVAL:
					len = reader.read_u2();
					HashMap<String,Value> tvs = new HashMap<String,Value>();
					for(int i=0;i!=len;++i) {
						idx = reader.read_u2();
						utf8 = (Constant.Utf8) constantPool.get(idx);
						lhs = readExpr(reader, constantPool);
						tvs.put(utf8.str, (Value) lhs);
					}
					return new TupleVal(tvs);
			}
			return readCondition(code,reader,constantPool);
		}
		
		public static Type readType(BinaryInputStream input,
				Map<Integer, Constant.Info> constantPool) throws IOException {
			int t = input.read_u1();
			Condition constraint = null;
			
			if((t & CONSTRAINT_MASK) != 0) {
				constraint = readCondition(input,constantPool);
			}

			t &= ~CONSTRAINT_MASK;
			
			switch(t) {
				case ANY_TYPE:
					return Types.T_ANY;
				case EXISTENTIAL_TYPE:
					return Types.T_EXISTENTIAL;
				case VOID_TYPE:
					return Types.T_VOID;
				case BOOL_TYPE:
					return Types.T_BOOL(constraint);
				case INT_TYPE:
					return Types.T_INT(constraint);
				case REAL_TYPE:
					return Types.T_REAL(constraint);
				case LIST_TYPE:
					Type et = readType(input,constantPool);
					return new ListType(et,constraint);
				case SET_TYPE:
					et = readType(input,constantPool);
					return new SetType(et,constraint);
				case TUPLE_TYPE:
					int nents = input.read_u2();					
					HashMap<String,Type> types = new HashMap<String,Type>();
					for(int i=0;i!=nents;++i) {
						String key = ((Constant.Utf8) constantPool.get(input.read_u2())).str;
						et = readType(input,constantPool);
						types.put(key,et);
					}
					return new TupleType(types,constraint);
				case UNION_TYPE:
					nents = input.read_u2();					
					ArrayList<NonUnionType> bounds = new ArrayList<NonUnionType>();
					for(int i=0;i!=nents;++i) {						
						et = readType(input,constantPool);
						bounds.add((NonUnionType) et);
					}
					return new UnionType(bounds,constraint);
				case PROCESS_TYPE:
					et = readType(input,constantPool);
					return new ProcessType(et);
				case NAMED_TYPE:
				{					
					ModuleID module = readModule(input,constantPool);
					String name = ((Constant.Utf8) constantPool.get(input.read_u2())).str;
					et = readType(input,constantPool);
					return new NamedType(module,name,et);
				}
				case RECURSIVE_TYPE:
				{										
					String name = ((Constant.Utf8) constantPool.get(input.read_u2())).str;
					et = readType(input,constantPool);
					return new RecursiveType(name,et,constraint);
				}
				case RECURSIVE_LEAF:
				{										
					String name = ((Constant.Utf8) constantPool.get(input.read_u2())).str;
					return new RecursiveType(name,null,null);					
				}
				case FUN_TYPE:
					Type ret = readType(input,constantPool);
					int count = input.read_u2();
					ArrayList<Type> ftypes = new ArrayList<Type>();
					for(int i=0;i!=count;++i) {
						ftypes.add(readType(input,constantPool));
					}
					return new FunType(ret,ftypes,constraint);				
			}
			
			throw new RuntimeException("Unknown type id encountered: " + t);
		}
		
		protected static ModuleID readModule(BinaryInputStream input,
				Map<Integer, Constant.Info> constantPool) throws IOException {
			String modstr = ((Constant.Utf8) constantPool.get(input.read_u2())).str;
			return ModuleID.fromString(modstr);
		}
	}
	
	public static final int EXISTENTIAL_TYPE = 1;
	public static final int ANY_TYPE = 2;
	public static final int VOID_TYPE = 3;
	public static final int BOOL_TYPE = 4;
	public static final int INT_TYPE = 5;
	public static final int REAL_TYPE = 6;
	public static final int LIST_TYPE = 7;
	public static final int SET_TYPE = 8;
	public static final int TUPLE_TYPE = 9;
	public static final int UNION_TYPE = 10;
	public static final int INTERSECTION_TYPE = 11;
	public static final int PROCESS_TYPE = 12;
	public static final int NAMED_TYPE = 13;
	public static final int FUN_TYPE = 14;
	public static final int RECURSIVE_TYPE = 15;
	public static final int RECURSIVE_LEAF = 16;
	public static final int CONSTRAINT_MASK = 32;
	
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
