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
	
	private WhileyType(Type type) {		
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
	

	public static void addPoolItems(Type type,
			Set<Constant.Info> constantPool) {
		// FIXME: this is completely broken and will likely infinite loop.
		if(type instanceof Type.List) {
			Type.List lt = (Type.List) type;
			addPoolItems(lt.element(),constantPool);
		} else if(type instanceof Type.Set) {
			Type.Set st = (Type.Set) type;
			addPoolItems(st.element(),constantPool);
		} else if(type instanceof Type.Record) {
			Type.Record tt = (Type.Record) type;
			for(Map.Entry<String,Type> p : tt.fields().entrySet()) {
				Constant.Utf8 utf8 = new Constant.Utf8(p.getKey());
				Constant.addPoolItem(utf8,constantPool);
				addPoolItems(p.getValue(),constantPool);
			}
		} else if(type instanceof Type.Union) {
			Type.Union ut = (Type.Union) type;			
			for(Type p : ut.bounds()) {
				addPoolItems(p,constantPool);	
			}	
		} else if(type instanceof Type.Process) {
			Type.Process st = (Type.Process) type;
			addPoolItems(st.element(),constantPool);
		} else if(type instanceof Type.Fun) {
			Type.Fun ft = (Type.Fun) type;
			for(Type t : ft.params()) {
				addPoolItems(t,constantPool);
			}
			addPoolItems(ft.ret(),constantPool);
		}
	}
		
	public static void write(Type t, BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool) throws IOException {
		if(t == Type.T_ANY) {
			writer.write_u1(ANY_TYPE );
		} else if(t == Type.T_VOID) {
			writer.write_u1(VOID_TYPE);
		} else if(t == Type.T_NULL) {
			writer.write_u1(NULL_TYPE );
		} else if(t == Type.T_BOOL) {
			writer.write_u1(BOOL_TYPE );			
		} else if(t == Type.T_INT) {			
			writer.write_u1(INT_TYPE );		
		} else if(t == Type.T_REAL) {
			writer.write_u1(REAL_TYPE );			
		} else if(t instanceof Type.Existential) {
			Type.Existential et = (Type.Existential) t;
			writer.write_u1(EXISTENTIAL_TYPE);
			Constant.Utf8 utf8 = new Constant.Utf8(et.name().module().toString());
			writer.write_u2(constantPool.get(utf8));
			utf8 = new Constant.Utf8(et.name().name());
			writer.write_u2(constantPool.get(utf8));
		} else if(t instanceof Type.List) {
			Type.List lt = (Type.List) t;
			writer.write_u1(LIST_TYPE );
			write(lt.element(),writer,constantPool);
		} else if(t instanceof Type.Set) {
			Type.Set st = (Type.Set) t;
			writer.write_u1(SET_TYPE );			
			write(st.element(),writer,constantPool);
		} else if(t instanceof Type.Record) {
			Type.Record tt = (Type.Record) t;
			writer.write_u1(TUPLE_TYPE );
			// FIXME: bug here if number of entries > 64K
			writer.write_u2(tt.fields().size());
			for(Map.Entry<String,Type> p : tt.fields().entrySet()) {
				Constant.Utf8 utf8 = new Constant.Utf8(p.getKey());
				writer.write_u2(constantPool.get(utf8));
				write(p.getValue(),writer,constantPool);	
			}			
		} else if(t instanceof Type.Union) {
			Type.Union ut = (Type.Union) t;
			writer.write_u1(UNION_TYPE );			
			// FIXME: bug here if number of bounds > 64K
			writer.write_u2(ut.bounds().size());
			for(Type p : ut.bounds()) {
				write(p,writer,constantPool);	
			}			
		} else if(t instanceof Type.Process) {
			Type.Process st = (Type.Process) t;
			writer.write_u1(PROCESS_TYPE );			
			write(st.element(),writer,constantPool);
		} else if(t instanceof Type.Fun) {
			Type.Fun st = (Type.Fun) t;
			writer.write_u1(FUN_TYPE );	
			write(st.receiver(),writer,constantPool);
			write(st.ret(),writer,constantPool);
			writer.write_u2(st.params().size());
			for(Type p : st.params()) {
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

	public static class Reader implements BytecodeAttribute.Reader {
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
			case VOID_TYPE:
				return Type.T_VOID;
			case NULL_TYPE:
				return Type.T_NULL;
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
				ArrayList<Type> bounds = new ArrayList<Type>();
				for (int i = 0; i != nents; ++i) {
					et = readType(input, constantPool);
					bounds.add((Type) et);
				}
				return Type.T_UNION(bounds);
			case PROCESS_TYPE:
				et = readType(input, constantPool);
				return Type.T_PROCESS(et);			
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
	public static final int NULL_TYPE = 4;
	public static final int BOOL_TYPE = 5;
	public static final int INT_TYPE = 6;
	public static final int REAL_TYPE = 7;
	public static final int LIST_TYPE = 8;
	public static final int SET_TYPE = 9;
	public static final int TUPLE_TYPE = 10;
	public static final int UNION_TYPE = 11;
	public static final int INTERSECTION_TYPE = 12;
	public static final int PROCESS_TYPE = 13;	
	public static final int FUN_TYPE = 14;
	public static final int METH_TYPE = 15;
	public static final int RECURSIVE_TYPE = 16;
	public static final int RECURSIVE_LEAF = 17;
	public static final int CONSTRAINT_MASK = 32;
}
