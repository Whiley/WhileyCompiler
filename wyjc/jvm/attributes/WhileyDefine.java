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

package wyjc.jvm.attributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import wyjc.ast.exprs.Expr;
import wyjc.ast.types.*;
import wyjc.util.*;
import wyjvm.io.BinaryInputStream;
import wyjvm.io.BinaryOutputStream;
import wyjvm.lang.BytecodeAttribute;
import wyjvm.lang.BytecodeAttributeReader;
import wyjvm.lang.Constant;

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
	private Expr expr;
	private Type type;
	
	public WhileyDefine(String name, Type type, Expr expr) {
		this.defName = name;
		this.type = type;
		this.expr = expr;
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
	
	public Expr expr() {
		return expr;
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
			iw.write_u1(0); // CONDITION ONLY			
			write(expr,iw, constantPool);			
		} else if(expr == null) {
			iw.write_u1(1); // TYPE ONLY
			write(type, iw, constantPool);
		} else {
			iw.write_u1(2); // BOTH
			write(type, iw, constantPool);
			write(expr, iw, constantPool);			
		}						
		
		writer.write_u2(constantPool.get(new Constant.Utf8(name())));
		writer.write_u4(out.size() + 2);		
		writer.write_u2(constantPool.get(new Constant.Utf8(defName)));
		writer.write(out.toByteArray());		
	}	
		
	public void addPoolItems(Set<Constant.Info> constantPool, ClassLoader loader) {		
		Constant.addPoolItem(new Constant.Utf8(name()), constantPool);	
		Constant.addPoolItem(new Constant.Utf8(defName), constantPool);
		
		if(expr != null) {
			WhileyCondition.addPoolItems(expr, constantPool);
		}
		if(type != null) {
			addPoolItems(type, constantPool);
		}
	}
	
	protected static void addPoolItems(Type type,
			Set<Constant.Info> constantPool) {
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
		
		if(t == Types.T_EXISTENTIAL) {
			writer.write_u1(EXISTENTIAL_TYPE);
		} else if(t == Types.T_VOID) {
			writer.write_u1(VOID_TYPE);
		} else if(t == Types.T_BOOL) {
			writer.write_u1(BOOL_TYPE);
		} else if(t == Types.T_INT) {
			writer.write_u1(INT_TYPE);
		} else if(t == Types.T_REAL) {
			writer.write_u1(REAL_TYPE);
		} else if(t instanceof ListType) {
			ListType lt = (ListType) t;
			writer.write_u1(LIST_TYPE);
			write(lt.element(),writer,constantPool);
		} else if(t instanceof SetType) {
			SetType st = (SetType) t;
			writer.write_u1(SET_TYPE);
			write(st.element(),writer,constantPool);
		} else if(t instanceof TupleType) {
			TupleType tt = (TupleType) t;
			writer.write_u1(TUPLE_TYPE);
			// FIXME: bug here if number of entries > 64K
			writer.write_u2(tt.types().size());
			for(Map.Entry<String,Type> p : tt.types().entrySet()) {
				Constant.Utf8 utf8 = new Constant.Utf8(p.getKey());
				writer.write_u2(constantPool.get(utf8));
				write(p.getValue(),writer,constantPool);	
			}			
		} else if(t instanceof UnionType) {
			UnionType ut = (UnionType) t;
			writer.write_u1(UNION_TYPE);
			// FIXME: bug here if number of bounds > 64K
			writer.write_u2(ut.types().size());
			for(Type p : ut.types()) {
				write(p,writer,constantPool);	
			}			
		} else if(t instanceof ProcessType) {
			ProcessType st = (ProcessType) t;
			writer.write_u1(PROCESS_TYPE);
			write(st.element(),writer,constantPool);
		} else if(t instanceof NamedType) {
			NamedType st = (NamedType) t;
			writer.write_u1(NAMED_TYPE);
			Constant.Utf8 utf8 = new Constant.Utf8(st.module().toString());
			writer.write_u2(constantPool.get(utf8));
			utf8 = new Constant.Utf8(st.name());
			writer.write_u2(constantPool.get(utf8));
			write(st.type(),writer,constantPool);
		} else if(t instanceof RecursiveType) {
			RecursiveType st = (RecursiveType) t;
			if(st.type() != null) {
				writer.write_u1(RECURSIVE_TYPE);
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
			writer.write_u1(FUN_TYPE);
			write(st.returnType(),writer,constantPool);
			writer.write_u2(st.parameters().size());
			for(Type p : st.parameters()) {
				write(p,writer,constantPool);
			}
		} else {
			throw new RuntimeException("unknown type encountered: " + t);
		}
	}
	
	protected void write(Expr e, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {		
		WhileyCondition.writeCondition(e,writer,constantPool);				
	}
	
	public void print(PrintWriter output,
			Map<Constant.Info, Integer> constantPool, ClassLoader loader)
			throws IOException {

		if (type == null) {
			output.println("  WhileyDefine: " + defName + " as " + expr);
		} else if (expr == null) {
			output.println("  WhileyDefine: " + defName + " as " + type);
		} else {
			output.println("  WhileyDefine: " + defName + " as " + type
					+ " where " + expr);
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
			Type type = null;
			Expr expr = null;
						
			if(sw == 0) {				
				// Condition only
				expr = WhileyCondition.Reader.readExpr(input,constantPool);				
			} else if(sw == 1) {
				// type only
				type = readType(input,constantPool);
			} else {
				// both
				type = readType(input,constantPool);											
				expr = WhileyCondition.Reader.readCondition(input,constantPool);									
			}
			
			return new WhileyDefine(name,type,expr);
		}
		
		public static Type readType(BinaryInputStream input,
				Map<Integer, Constant.Info> constantPool) throws IOException {
			int t = input.read_u1();
			switch(t) {
				case EXISTENTIAL_TYPE:
					return Types.T_EXISTENTIAL;
				case VOID_TYPE:
					return Types.T_VOID;
				case BOOL_TYPE:
					return Types.T_BOOL;
				case INT_TYPE:
					return Types.T_INT;
				case REAL_TYPE:
					return Types.T_REAL;
				case LIST_TYPE:
					Type et = readType(input,constantPool);
					return new ListType(et);
				case SET_TYPE:
					et = readType(input,constantPool);
					return new SetType(et);
				case TUPLE_TYPE:
					int nents = input.read_u2();					
					HashMap<String,Type> types = new HashMap<String,Type>();
					for(int i=0;i!=nents;++i) {
						String key = ((Constant.Utf8) constantPool.get(input.read_u2())).str;
						et = readType(input,constantPool);
						types.put(key,et);
					}
					return new TupleType(types);
				case UNION_TYPE:
					nents = input.read_u2();					
					ArrayList<NonUnionType> bounds = new ArrayList<NonUnionType>();
					for(int i=0;i!=nents;++i) {						
						et = readType(input,constantPool);
						bounds.add((NonUnionType) et);
					}
					return new UnionType(bounds);
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
					return new RecursiveType(name,et);
				}
				case RECURSIVE_LEAF:
				{										
					String name = ((Constant.Utf8) constantPool.get(input.read_u2())).str;
					return new RecursiveType(name,null);					
				}
				case FUN_TYPE:
					Type ret = readType(input,constantPool);
					int count = input.read_u2();
					ArrayList<Type> ftypes = new ArrayList<Type>();
					for(int i=0;i!=count;++i) {
						ftypes.add(readType(input,constantPool));
					}
					return new FunType(ret,ftypes);				
			}
			
			throw new RuntimeException("Unknown type id encountered: " + t);
		}		
	}
	
	protected static ModuleID readModule(BinaryInputStream input,
			Map<Integer, Constant.Info> constantPool) throws IOException {
		String modstr = ((Constant.Utf8) constantPool.get(input.read_u2())).str;
		return ModuleID.fromString(modstr);
	}
	
	public static final int EXISTENTIAL_TYPE = 1;
	public static final int VOID_TYPE = 2;
	public static final int BOOL_TYPE = 3;
	public static final int INT_TYPE = 4;
	public static final int REAL_TYPE = 5;
	public static final int LIST_TYPE = 6;
	public static final int SET_TYPE = 7;
	public static final int TUPLE_TYPE = 8;
	public static final int UNION_TYPE = 9;
	public static final int INTERSECTION_TYPE = 10;
	public static final int PROCESS_TYPE = 11;
	public static final int NAMED_TYPE = 12;
	public static final int FUN_TYPE = 13;
	public static final int RECURSIVE_TYPE = 14;
	public static final int RECURSIVE_LEAF = 15;
}
