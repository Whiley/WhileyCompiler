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

import java.io.*;
import java.util.*;
import wyil.lang.*;
import wyjvm.io.*;
import wyjvm.lang.*;
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
		if(type instanceof Type.List) {
			Type.List lt = (Type.List) type;
			addPoolItems(lt.element,constantPool);
		} else if(type instanceof Type.Set) {
			Type.Set st = (Type.Set) type;
			addPoolItems(st.element,constantPool);
		} else if(type instanceof Type.Record) {
			Type.Record tt = (Type.Record) type;
			for(Map.Entry<String,Type> p : tt.types.entrySet()) {
				Constant.Utf8 utf8 = new Constant.Utf8(p.getKey());
				Constant.addPoolItem(utf8,constantPool);
				addPoolItems(p.getValue(),constantPool);
			}
		} else if(type instanceof Type.Union) {
			Type.Union ut = (Type.Union) type;			
			for(Type p : ut.bounds) {
				addPoolItems(p,constantPool);	
			}	
		} else if(type instanceof Type.Process) {
			Type.Process st = (Type.Process) type;
			addPoolItems(st.element,constantPool);
		} else if(type instanceof Type.Named) {
			Type.Named lt = (Type.Named) type;
			Constant.Utf8 utf8 = new Constant.Utf8(lt.module.toString());
			Constant.addPoolItem(utf8,constantPool);
			utf8 = new Constant.Utf8(lt.name);	
			Constant.addPoolItem(utf8,constantPool);
			addPoolItems(lt.type,constantPool);
		} else if(type instanceof Type.Recursive) {
			Type.Recursive lt = (Type.Recursive) type;
			String name = lt.name;
			Constant.Utf8 utf8 = new Constant.Utf8(name);
			Constant.addPoolItem(utf8,constantPool);					
			if(lt.type != null) {
				addPoolItems(lt.type,constantPool);
			}
		} else if(type instanceof Type.Fun) {
			Type.Fun ft = (Type.Fun) type;
			for(Type t : ft.params) {
				addPoolItems(t,constantPool);
			}
			addPoolItems(ft.ret,constantPool);
		}
	}
		
	public static void write(Type t, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		if(t == Type.T_ANY) {
			writer.write_u1(ANY_TYPE );
		} else if(t == Type.T_EXISTENTIAL) {
			writer.write_u1(EXISTENTIAL_TYPE );
		} else if(t == Type.T_VOID) {
			writer.write_u1(VOID_TYPE);
		} else if(t == Type.T_BOOL) {
			writer.write_u1(BOOL_TYPE );			
		} else if(t == Type.T_INT) {			
			writer.write_u1(INT_TYPE );		
		} else if(t == Type.T_REAL) {
			writer.write_u1(REAL_TYPE );			
		} else if(t instanceof Type.List) {
			Type.List lt = (Type.List) t;
			writer.write_u1(LIST_TYPE );
			write(lt.element,writer,constantPool);
		} else if(t instanceof Type.Set) {
			Type.Set st = (Type.Set) t;
			writer.write_u1(SET_TYPE );			
			write(st.element,writer,constantPool);
		} else if(t instanceof Type.Record) {
			Type.Record tt = (Type.Record) t;
			writer.write_u1(TUPLE_TYPE );
			// FIXME: bug here if number of entries > 64K
			writer.write_u2(tt.types.size());
			for(Map.Entry<String,Type> p : tt.types.entrySet()) {
				Constant.Utf8 utf8 = new Constant.Utf8(p.getKey());
				writer.write_u2(constantPool.get(utf8));
				write(p.getValue(),writer,constantPool);	
			}			
		} else if(t instanceof Type.Union) {
			Type.Union ut = (Type.Union) t;
			writer.write_u1(UNION_TYPE );			
			// FIXME: bug here if number of bounds > 64K
			writer.write_u2(ut.bounds.size());
			for(Type p : ut.bounds) {
				write(p,writer,constantPool);	
			}			
		} else if(t instanceof Type.Process) {
			Type.Process st = (Type.Process) t;
			writer.write_u1(PROCESS_TYPE );			
			write(st.element,writer,constantPool);
		} else if(t instanceof Type.Named) {			
			Type.Named st = (Type.Named) t;
			writer.write_u1(NAMED_TYPE );
			Constant.Utf8 utf8 = new Constant.Utf8(st.module.toString());
			writer.write_u2(constantPool.get(utf8));
			utf8 = new Constant.Utf8(st.name);
			writer.write_u2(constantPool.get(utf8));
			write(st.type,writer,constantPool);
		} else if(t instanceof Type.Recursive) {
			Type.Recursive st = (Type.Recursive) t;
			if(st.type != null) {
				writer.write_u1(RECURSIVE_TYPE );				
			} else {
				writer.write_u1(RECURSIVE_LEAF);
			}
			String name = st.name;
			Constant.Utf8 utf8 = new Constant.Utf8(name);
			writer.write_u2(constantPool.get(utf8));			
			if(st.type != null) {
				write(st.type,writer,constantPool);
			}
		} else if(t instanceof Type.Fun) {
			Type.Fun st = (Type.Fun) t;
			writer.write_u1(FUN_TYPE );	
			write(st.ret,writer,constantPool);
			writer.write_u2(st.params.size());
			for(Type p : st.params) {
				write(p,writer,constantPool);
			}
		} else {
			throw new RuntimeException("unknown type encountered: " + t);
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
		output.print(name() + ":");
	}

	public static class Reader implements BytecodeAttributeReader {
		public String name() {
			return "WhileyType";
		}

		public WhileyType read(BinaryInputStream input,
				Map<Integer, Constant.Info> constantPool) throws IOException {
			input.read_u2(); // attribute name index code
			input.read_u4(); // attribute length
			Type t = readType(input, constantPool);
			return new WhileyType(t);
		}

		public static Type readType(BinaryInputStream input,
				Map<Integer, Constant.Info> constantPool) throws IOException {
			int t = input.read_u1();

			switch (t) {
			case ANY_TYPE:
				return Type.T_ANY;
			case EXISTENTIAL_TYPE:
				return Type.T_EXISTENTIAL;
			case VOID_TYPE:
				return Type.T_VOID;
			case BOOL_TYPE:
				return Type.T_BOOL;
			case INT_TYPE:
				return Type.T_INT;
			case REAL_TYPE:
				return Type.T_REAL;
			case LIST_TYPE:
				Type et = readType(input, constantPool);
				return Type.T_LIST(et);
			case SET_TYPE:
				et = readType(input, constantPool);
				return Type.T_SET(et);
			case TUPLE_TYPE:
				int nents = input.read_u2();
				HashMap<String, Type> types = new HashMap<String, Type>();
				for (int i = 0; i != nents; ++i) {
					String key = ((Constant.Utf8) constantPool.get(input
							.read_u2())).str;
					et = readType(input, constantPool);
					types.put(key, et);
				}
				return Type.T_RECORD(types);
			case UNION_TYPE:
				nents = input.read_u2();
				ArrayList<Type.NonUnion> bounds = new ArrayList<Type.NonUnion>();
				for (int i = 0; i != nents; ++i) {
					et = readType(input, constantPool);
					bounds.add((Type.NonUnion) et);
				}
				return Type.leastUpperBound(bounds);
			case PROCESS_TYPE:
				et = readType(input, constantPool);
				return Type.T_PROCESS(et);
			case NAMED_TYPE: {
				ModuleID module = readModule(input, constantPool);
				String name = ((Constant.Utf8) constantPool
						.get(input.read_u2())).str;
				et = readType(input, constantPool);
				return Type.T_NAMED(module, name, et);
			}
			case RECURSIVE_TYPE: {
				String name = ((Constant.Utf8) constantPool
						.get(input.read_u2())).str;
				et = readType(input, constantPool);
				return Type.T_RECURSIVE(name, et);
			}
			case RECURSIVE_LEAF: {
				String name = ((Constant.Utf8) constantPool
						.get(input.read_u2())).str;
				return Type.T_RECURSIVE(name, null);
			}
			case FUN_TYPE: {
				Type ret = readType(input, constantPool);
				int count = input.read_u2();
				ArrayList<Type> ftypes = new ArrayList<Type>();
				for (int i = 0; i != count; ++i) {
					ftypes.add(readType(input, constantPool));
				}
				return Type.T_FUN(null, ret, ftypes);
			}
			case METH_TYPE: {
				Type.Process rec = (Type.Process) readType(input, constantPool);
				Type ret = readType(input, constantPool);
				int count = input.read_u2();
				ArrayList<Type> ftypes = new ArrayList<Type>();
				for (int i = 0; i != count; ++i) {
					ftypes.add(readType(input, constantPool));
				}
				return Type.T_FUN(rec, ret, ftypes);
			}
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
	public static final int METH_TYPE = 15;
	public static final int RECURSIVE_TYPE = 16;
	public static final int RECURSIVE_LEAF = 17;
	public static final int CONSTRAINT_MASK = 32;
}
