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
import java.util.*;

import wyil.util.Pair;

public abstract class Bytecode {
	
	/**
	 * This method determines the change in stack resulting from this bytecode.
	 * For example, +1 indicates the instruction puts one thing onto the stack.
	 * Likewise, -2 means this instruction takes two things off the stack. The
	 * primary purpose of this method is to help compute the maxStack
	 * requirement for a method.
	 * 
	 * @return
	 */
	public abstract int stackDiff();
	
	/**
	 * This method adds any constant pool items required by the bytecode to the
	 * constantPool.
	 * 
	 * @param constantPool
	 */
	public void addPoolItems(Set<Constant.Info> constantPool) {
		// in the default case, we do nothing.
	}
	
	/** 
	 * Translate this Java bytecode into bytes. If the bytecode requires a
	 * constant pool item which is not present in the constantPool map, then the
	 * appropriate CONSTANT_Info object is added, and given the next available
	 * index.
	 * 
	 * @param offset
	 *            Offset of this bytecode
	 * @param labelOffsets
	 *            Offsets of any labels used in branch bytecodes
	 * @param constantPool
	 *            Indices of constant pool items used in various bytecodes (e.g.
	 *            ldc, putfield, etc)
	 * 
	 * @return
	 */
	public abstract byte[] toBytes(int offset, 
						Map<String,Integer> labelOffsets,
						Map<Constant.Info,Integer> constantPool);

	// ===============================
	// ======= JAVA BYTECODES ========
	// ===============================
	
	/**
     * Represents all bytecodes for storing to a local variable, including
     * istore, astore, lstore etc.
     */
	public final static class Store extends Bytecode {
		public final int slot;
		public final JvmType type;
		
		public Store(int slot, JvmType type) {
			typeChar(type); // check valid type
			this.slot=slot;
			this.type=type;			
		}
		
		public int stackDiff() {
			return -ClassFile.slotSize(type);
		}
		
		public String toString() {
			if(slot >= 0 && slot <= 3) {
 				return typeChar(type) + "store_" + slot;
			} else {
				return typeChar(type) + "store " + slot;
			}
		}
		
		public byte[] toBytes(int offset, Map<String,Integer> labelOffsets,  Map<Constant.Info,Integer> constantPool) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();					
			if(slot >= 0 && slot <= 3) { 
				write_u1(out,ISTORE_0 + (4*typeOffset(type)) + slot); 
			} else {
				write_u1(out,ISTORE + typeOffset(type));
				write_u1(out,slot);
			}		
			return out.toByteArray();
		}
		
		public boolean equals(Object o) {
			if (o instanceof Store) {
				Store s = (Store) o;
				return type.equals(s.type) && slot == s.slot;
			}
			return false;
		}
		
		public int hashCode() {
			return type.hashCode() + slot;
		}
	}
	
	/**
     * Represents all bytecodes for loading from a local variable, including
     * iload, aload, lload etc.
     */
	public final static class Load extends Bytecode {
		public final int slot;
		public final JvmType type;
		
		public Load(int slot, JvmType type) {
			typeChar(type); // check valid type
			this.slot=slot;
			this.type=type;			
		}
		
		public int stackDiff() {
			return ClassFile.slotSize(type);
		}		
		
		public String toString() {
			if(slot >= 0 && slot <= 3) {
				return typeChar(type) + "load_" + slot;
			} else {
				return typeChar(type) + "load " + slot;
			}
		}
		
		public byte[] toBytes(int offset, Map<String,Integer> labelOffsets,  Map<Constant.Info,Integer> constantPool) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();					
			if(slot >= 0 && slot <= 3) { 
				write_u1(out,ILOAD_0 + (4*typeOffset(type)) + slot); 
			} else {
				write_u1(out,ILOAD + typeOffset(type));
				write_u1(out,slot);
			}		
			return out.toByteArray();
		}
		
		public boolean equals(Object o) {
			if (o instanceof Load) {
				Load l = (Load) o;
				return type.equals(l.type) && slot == l.slot;
			}
			return false;
		}
		
		public int hashCode() {
			return type.hashCode() + slot;
		}
	}

	/**
     * Represents the iinc bytecode for incrementing a local variable.
     */
	public final static class Iinc extends Bytecode {
		public final int slot;
		public final int increment;
		
		public Iinc(int slot, int increment) {
			if(increment < Byte.MIN_VALUE || increment > Byte.MAX_VALUE) {
				throw new IllegalArgumentException("illegal iinc increment --- must be between -127 and 128");
			}
			this.slot=slot;
			this.increment = increment;			
		}
		

		public int stackDiff() {
			return 0;
		}
		
		public String toString() {
			return "iinc " + slot + ", " + increment;
		}
		
		public byte[] toBytes(int offset, Map<String,Integer> labelOffsets,  Map<Constant.Info,Integer> constantPool) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();					
			write_u1(out,IINC);
			write_u1(out,slot);
			write_i1(out,increment);
			return out.toByteArray();
		}
		
		public boolean equals(Object o) {
			if (o instanceof Iinc) {
				Iinc i = (Iinc) o;
				return increment == i.increment && slot == i.slot;
			}
			return false;
		}
		
		public int hashCode() {
			return increment + slot;
		}
	}

	/**
	 * Represents the arrayload bytecode. 
	 */
	public static final class ArrayLoad extends Bytecode {
		public final JvmType.Array type;
		
		public ArrayLoad(JvmType.Array type) { this.type = type; }
		
		public byte[] toBytes(int offset, Map<String,Integer> labelOffsets,  Map<Constant.Info,Integer> constantPool) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			write_u1(out,IALOAD + typeArrayOffset(type.element()));
			return out.toByteArray();
		}
		
		public int stackDiff() {
			return ClassFile.slotSize(type.element())-2;
		}
		
		public String toString() { return typeArrayChar(type.element()) + "aload"; }
		
		public boolean equals(Object o) {
			if (o instanceof ArrayLoad) {
				ArrayLoad i = (ArrayLoad) o;
				return type.equals(i.type);
			}
			return false;
		}
		
		public int hashCode() {
			return type.hashCode();
		}
	}
	
	/**
	 * Represents the arraystore bytecode.
	 */
	public static final class ArrayStore extends Bytecode {
		public final JvmType.Array type;
		
		public ArrayStore(JvmType.Array type) { this.type = type; }
		
		public int stackDiff() {			
			return -(2 + ClassFile.slotSize(type.element()));
		}
		
		public byte[] toBytes(int offset, Map<String,Integer> labelOffsets,  Map<Constant.Info,Integer> constantPool) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			write_u1(out,IASTORE + typeArrayOffset(type.element()));
			return out.toByteArray();
		}
		
		public String toString() { return typeArrayChar(type.element()) + "astore"; }
		
		public boolean equals(Object o) {
			if (o instanceof ArrayStore) {
				ArrayStore i = (ArrayStore) o;
				return type.equals(i.type);
			}
			return false;
		}
		
		public int hashCode() {
			return type.hashCode();
		}
	}
	
	/**
     * Represents all bytecodes for loading constants. Including, iconst, bipush,
     * sipush, ldc, ldc_w
     */
	public static final class LoadConst extends Bytecode {
		public final Object constant;
		
		public LoadConst(Object constant) {				
			if(constant instanceof Boolean) {
				Boolean b = (Boolean) constant;				
				constant = b ? 1 : 0;				
			} else if(constant instanceof Character) {
				constant = (int)((Character) constant);
			} else if(constant instanceof Byte || constant instanceof Short) {
				constant = ((Number)constant).intValue();
			}
			this.constant = constant; 
		}				
		
		public int stackDiff() {
			if (constant instanceof Long || constant instanceof Double) {
				return 2;
			}
			return 1;
		}
		
		public void addPoolItems(Set<Constant.Info> constantPool) {
			if (constant instanceof Integer) {
				int v = ((Number) constant).intValue();
				if (!(v >= -1 && v <= 5) && !(v >= -128 && v <= 127)
						&& !(v >= -32768 && v <= 32767)) { 					
					Constant.addPoolItem(new Constant.Integer(v),constantPool);										
				}
			} else if (constant instanceof Long) {
				long v = (Long) constant;
				if (v != 0 && v != 1) {
					Constant.addPoolItem(new Constant.Long(v), constantPool);
				}
			} else if(constant instanceof Float) {
				float v = (Float) constant;
				if(v != 0.0F && v != 1.0F && v != 2.0F) {					
					Constant.addPoolItem(new Constant.Float(v),constantPool);					
				}
			} else if(constant instanceof Double) {
				double v = (Double) constant;
				if (v != 0.0D && v != 1.0D) {
					Constant.addPoolItem(new Constant.Double(v), constantPool);
				}				
			} else if(constant instanceof String) {
				String v = (String) constant;
				Constant.addPoolItem(new Constant.String(new Constant.Utf8(v)),
						constantPool);				
			} else if(constant instanceof JvmType) {
				JvmType.Reference ref = (JvmType.Reference) constant;
				Constant.addPoolItem(Constant.buildClass(ref),constantPool);
			}
		}
		
		public byte[] toBytes(int offset, Map<String,Integer> labelOffsets,  
				Map<Constant.Info,Integer> constantPool) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();						
			
			if (constant instanceof Integer) {
				int v = (Integer) constant;
				if (v >= -1 && v <= 5) {
					write_u1(out, ICONST_0 + v);
				} else if (v >= -128 && v <= 127) {
					write_u1(out, BIPUSH);
					write_u1(out, v);
				} else if (v >= -32768 && v <= 32767) {
					write_u1(out, SIPUSH);
					write_u2(out, v);
				} else {
					int idx = constantPool.get(new Constant.Integer(v));
					if (idx < 255) {
						write_u1(out, LDC);
						write_u1(out, idx);
					} else {
						write_u1(out, LDC_W);
						write_u2(out, idx);
					}
				}
			} else if(constant instanceof Long) {
				long v = (Long) constant;
				if(v == 0 || v == 1) {
					write_u1(out,LCONST_0 + (int) v);
				} else {
					int idx = constantPool.get(new Constant.Long(v));
					write_u1(out,LDC2_W);
					write_u2(out,idx);
				}
			} else if(constant instanceof Float) {
				float v = (Float) constant;
				if(v == 0.0F) {
					write_u1(out,FCONST_0);
				} else if(v == 1.0F) {
					write_u1(out,FCONST_1);
				} else if(v == 2.0F) {
					write_u1(out,FCONST_2);
				} else {										
					int idx = constantPool.get(new Constant.Float(v));
					if(idx < 255) {
						write_u1(out,LDC);
						write_u1(out,idx);
					} else {
						write_u1(out,LDC_W);
						write_u2(out,idx);
					}
				}
			} else if(constant instanceof Double) {
				double v = (Double) constant;
				if(v == 0.0D) {
					write_u1(out,DCONST_0);
				} else if(v == 1.0D) {
					write_u1(out,DCONST_1);
				} else {
					int idx = constantPool.get(new Constant.Double(v));
					write_u1(out,LDC2_W);
					write_u2(out,idx);
				}				
			} else if(constant instanceof String) {
				String v = (String) constant;
				int idx = constantPool.get(new Constant.String(new Constant.Utf8(v)));
				if(idx < 255) {
					write_u1(out,LDC);				
					write_u1(out,idx);
				} else {
					write_u1(out,LDC_W);				
					write_u2(out,idx);
				}
			} else if(constant instanceof JvmType) {
				JvmType.Reference ref = (JvmType.Reference) constant;
				int idx = constantPool.get(Constant.buildClass(ref));
				write_u1(out, LDC_W);
				write_u2(out, idx);
			} else {
				write_u1(out,ACONST_NULL);
			} 
			return out.toByteArray();
		}
		
		public String toString() {
			if (constant instanceof Integer) {
				int v = (Integer) constant;
				if (v >= -1 && v <= 5) {
					return "iconst_" + v;
				} else if (v >= -128 && v <= 127) {
					return "bipush " + v;
				} else if (v >= -32768 && v <= 32767) {
					return "sipush " + v;
				} else {
					return "ldc " + v;
				}
			} else if (constant instanceof Long) {
				long v = (Long) constant;
				if (v == 0 || v == 1) {
					return "lconst_" + v;
				} else {
					return "ldc2_w " + v;
				}
			} else if (constant instanceof Float) {
				float v = (Float) constant;
				if (v == 0.0F) {
					return "fconst_0";
				} else if (v == 1.0F) {
					return "fconst_1";
				} else if (v == 2.0F) {
					return "fconst_2";
				} else {
					return "ldc " + v;
				}
			} else if (constant instanceof Double) {
				double v = (Double) constant;
				if (v == 0.0D) {
					return "dconst_0";
				} else if (v == 1.0D) {
					return "dconst_1";
				} else {
					return "ldc2_w " + v;
				}
			} else if (constant instanceof String) {
				String v = (String) constant;
				return "ldc " + v;
			} else {
				return "aconst_null";
			} 
		}
		
		public boolean equals(Object o) {
			if (o instanceof LoadConst) {
				LoadConst i = (LoadConst) o;
				if(constant == null) {
					return constant == i.constant;
				} else {
					return constant.equals(i.constant);
				}
			}
			return false;
		}
		
		public int hashCode() {
			if(constant == null) {
				return 0;
			} else {
				return constant.hashCode();
			}
		}
	}
	
	/**
	 * Represents return bytecodes, including ireturn, areturn, etc.
	 */
	public static final class Return extends Bytecode {
		public final JvmType type;
		public Return(JvmType type) {
			if(type != null) { typeChar(type); } // check valid
			this.type = type;			
		}		
		
		public int stackDiff() {
			if(type == null) { return 0; }
			else {
				return ClassFile.slotSize(type);
			}
		}
		
		public byte[] toBytes(int offset, Map<String, Integer> labelOffsets,
				Map<Constant.Info, Integer> constantPool) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			if (type == null) {
				write_u1(out, RETURN);
			} else {
				write_u1(out, IRETURN + typeOffset(type));
			}
			return out.toByteArray();
		}
		
		public String toString() {
			if(type == null) { return "return"; } 
			else { return typeChar(type) + "return"; }
		}
		
		public boolean equals(Object o) {
			if (o instanceof Return) {
				Return i = (Return) o;
				return type.equals(i.type);
			}
			return false;
		}
		
		public int hashCode() {
			return type.hashCode();
		}
	}
	
	/**
     * This does not actually correspond to a bytecode per se. Rather it is an
     * imaginary bytecode which is used to mark the destination of branching
     * instructions. 
     */
	public static final class Label extends Bytecode {
		public final String name;		
		public Label(String name) { this.name = name; }		
		public byte[] toBytes(int offset, Map<String,Integer> labelOffsets,  Map<Constant.Info,Integer> constantPool) {
			return new byte[0];
		}
		public String toString() { return name + ":"; }
		public int stackDiff() {
			return 0;
		}
		
		public boolean equals(Object o) {
			if (o instanceof Label) {
				Label i = (Label) o;
				return name.equals(i.name);
			}
			return false;
		}
		
		public int hashCode() {
			return name.hashCode();
		}
	}
	
	public static final class Neg extends Bytecode {
		public final JvmType type;
		
		public Neg(JvmType type) {
			typeChar(type); // check valid type
			this.type = type; 
		}
		
		public int stackDiff() {
			return 0;
		}
		
		public byte[] toBytes(int offset, Map<String,Integer> labelOffsets,  Map<Constant.Info,Integer> constantPool) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			write_u1(out,INEG + typeOffset(type));
			return out.toByteArray();
		}
		
		public String toString() { return typeChar(type) + "neg"; }
		
		public boolean equals(Object o) {
			if (o instanceof Neg) {
				Neg i = (Neg) o;
				return type.equals(i.type);
			}
			return false;
		}
		
		public int hashCode() {
			return type.hashCode();
		}
	}
	
	/**
     * This represents all binary operators involving two operands on the stack.
     * Examples include iadd, fsub, ldiv, ishr, lxor, etc.
     */
	public static final class BinOp extends Bytecode {
		public static final int ADD = 0;
		public static final int SUB = 1;
		public static final int MUL = 2;
		public static final int DIV = 3;
		public static final int REM = 4;
		public static final int SHL = 5;
		public static final int SHR = 6;
		public static final int USHR = 7;
		public static final int AND = 8;
		public static final int OR = 9;
		public static final int XOR = 10;
		
		private static final int[] base = {IADD, ISUB, IMUL, IDIV, IREM, ISHL,
				ISHR, IUSHR, IAND, IOR, IXOR};
		private static final String[] str = {"add", "sub", "mul", "div", "rem",
				"shl", "shr", "ushr", "and", "or", "xor"};

		public final JvmType type;
		public final int op;
		public BinOp(int op, JvmType type) {
			typeChar(type); // check valid type
			assert op >= 0 && op <= USHR;
			this.op = op;
			this.type = type;
		}
		
		public int stackDiff() {
			return -ClassFile.slotSize(type);
		}
		
		public byte[] toBytes(int offset, Map<String, Integer> labelOffsets,
				Map<Constant.Info, Integer> constantPool) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			write_u1(out, base[op] + typeOffset(type));
			return out.toByteArray();
		}
		public String toString() {
			return typeChar(type) + str[op];
		}
		
		public boolean equals(Object o) {
			if (o instanceof BinOp) {
				BinOp i = (BinOp) o;
				return op == i.op && type.equals(i.type);
			}
			return false;
		}
		
		public int hashCode() {
			return op + type.hashCode();
		}
	}
		

	/**
	 * Modifier to indicate get/put/invoke is static 
	 */
	public static final int STATIC = 1;
	/**
	 * Modifier to indicate get/put is non-static 
	 */
	public static final int NONSTATIC = 2;
	/**
	 * Modifier to indicate invoke is virtual 
	 */	
	public static final int VIRTUAL = 2;
	/**
	 * Modifier to indicate invoke is special 
	 */
	public static final int SPECIAL = 3;
	/**
	 * Modifier to indicate invoke is special 
	 */
	public static final int INTERFACE = 4;
	
	/**
	 * This represents the putfield and putstatic bytecodes.
	 */
	public static final class PutField extends Bytecode {
		public final JvmType.Clazz owner;
		public final JvmType type;
		public final String name;
		public final int mode;
		
		public PutField(JvmType.Clazz owner, String name, JvmType type, int mode) {
			assert mode >= 1 && mode <= 2;
			this.owner = owner;
			this.type = type;
			this.name = name;	
			this.mode = mode;
		}
		
		public int stackDiff() {
			if(mode == STATIC) {
				return -ClassFile.slotSize(type);
			} else {
				return -1 - ClassFile.slotSize(type);
			}
		}
		
		public void addPoolItems(Set<Constant.Info> constantPool) {
			Constant.addPoolItem(Constant.buildFieldRef(owner, name, type),
					constantPool);
		}
		
		public byte[] toBytes(int offset, 
				Map<String,Integer> labelOffsets,  
				Map<Constant.Info,Integer> constantPool) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();			
			int idx = constantPool.get(Constant.buildFieldRef(owner, name,
					type));
			if(mode == STATIC) {
				write_u1(out,PUTSTATIC);
			} else {
				write_u1(out,PUTFIELD);
			}
			write_u2(out,idx); 
			return out.toByteArray();
		}
		
		public String toString() {
			if(mode == STATIC) {
				return "putstatic " + owner + "." + name + ":" + ClassFile.descriptor(type,false);
			} else {
				return "putfield " + owner + "." + name + ":" + ClassFile.descriptor(type,false);
			}
		}
		
		public boolean equals(Object o) {
			if (o instanceof PutField) {
				PutField b = (PutField) o;
				return mode == b.mode && name.equals(b.name)
						&& owner.equals(b.owner) && type.equals(b.type);
			}
			return false;
		}
		
		public int hashCode() {
			return name.hashCode() + type.hashCode() + owner.hashCode();
		}
	}	
	
	/**
	 * This represents the getfield and getstatic bytecodes.
	 */
	public static final class GetField extends Bytecode {
		public final JvmType.Clazz owner;
		public final JvmType type;
		public final String name;
		public final int mode;
		
		public GetField(JvmType.Clazz owner, String name, JvmType type, int mode) {
			assert mode >= 1 && mode <= 2;
			this.owner = owner;
			this.type = type;
			this.name = name;
			this.mode = mode;
		}
		
		public int stackDiff() {
			if(mode == STATIC) {
				return ClassFile.slotSize(type);
			} else {
				return -1 + ClassFile.slotSize(type);
			}
		}
		
		public void addPoolItems(Set<Constant.Info> constantPool) {
			Constant.addPoolItem(Constant.buildFieldRef(owner, name, type),
					constantPool);
		}
		
		public byte[] toBytes(int offset, Map<String, Integer> labelOffsets,
				Map<Constant.Info, Integer> constantPool) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int idx = constantPool.get(Constant
					.buildFieldRef(owner, name, type));
			if (mode == STATIC) {
				write_u1(out, GETSTATIC);
			} else {
				write_u1(out, GETFIELD);
			}
			write_u2(out, idx);
			return out.toByteArray();
		}
		
		public String toString() {
			if(mode == STATIC) {
				return "getstatic " + owner + "." + name + ":" + ClassFile.descriptor(type,false);
			} else {
				return "getfield " + owner + "." + name + ":" + ClassFile.descriptor(type,false);
			}
		}
		
		public boolean equals(Object o) {
			if (o instanceof GetField) {
				GetField b = (GetField) o;
				return mode == b.mode && name.equals(b.name)
						&& owner.equals(b.owner) && type.equals(b.type);
			}
			return false;
		}
		
		public int hashCode() {
			return name.hashCode() + type.hashCode() + owner.hashCode();			
		}
	}	
	
	/**
     * This represents the invokevirtual, invokespecial, invokestatic and
     * invokeinterface bytecodes
     */
	public static final class Invoke extends Bytecode {
		public final JvmType.Clazz owner;
		public final JvmType.Function type;
		public final String name;
		public final int mode;
		
		public Invoke(JvmType.Clazz owner, String name, JvmType.Function type, int mode) {
			assert mode >= 1 && mode <= 3;
			this.owner = owner;
			this.type = type;
			this.name = name;
			this.mode = mode;
		}
		
		public int stackDiff() {
			int diff = mode == STATIC ? 0 : -1;
			
			if(!(type.returnType() instanceof JvmType.Void)) {
				diff += ClassFile.slotSize(type.returnType());
			}
			
			for(JvmType pt : type.parameterTypes()) {
				diff -= ClassFile.slotSize(pt);
			}
			
			return diff;
		}
		
		public void addPoolItems(Set<Constant.Info> constantPool) {
			if(mode != INTERFACE) {
				Constant.addPoolItem(Constant.buildMethodRef(owner, name,
					type),constantPool);
			} else {
				Constant.addPoolItem(Constant.buildInterfaceMethodRef(owner, name,
						type),constantPool);
			}
		}
		
		public byte[] toBytes(int offset, Map<String,Integer> labelOffsets,  Map<Constant.Info,Integer> constantPool) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();			
			int idx;
			if(mode != INTERFACE) {
				idx = constantPool.get(Constant.buildMethodRef(owner, name,
					type));
			} else {
				idx = constantPool.get(Constant.buildInterfaceMethodRef(owner, name,
						type));
			}
			if(mode == STATIC) {
				write_u1(out,INVOKESTATIC);				 
			} else if(mode == VIRTUAL) {
				write_u1(out,INVOKEVIRTUAL);
			} else if(mode == SPECIAL){
				write_u1(out,INVOKESPECIAL);	
			} else {
				write_u1(out,INVOKEINTERFACE);
			}
			write_u2(out,idx); 
			if(mode == INTERFACE) {
				int ps = 1; // 1 for the "this" reference!
				for(JvmType t : type.parameterTypes()) {
					ps += ClassFile.slotSize(t);
				}
				write_u1(out,ps);
				write_u1(out,0);
				return out.toByteArray();
			}			
			return out.toByteArray();
		}
		
		public String toString() {		
			if(mode == STATIC) {
				return "invokestatic " + owner + "." + name + " " + ClassFile.descriptor(type,false);
			} else if(mode == VIRTUAL) {
				return "invokevirtual " + owner + "." + name + " " + ClassFile.descriptor(type,false);
			} else if(mode == SPECIAL) {
				return "invokespecial " + owner + "." + name + " " + ClassFile.descriptor(type,false);
			} else {
				return "invokeinterface " + owner + "." + name + " " + ClassFile.descriptor(type,false);
			}
		}
		
		public boolean equals(Object o) {
			if (o instanceof Invoke) {
				Invoke b = (Invoke) o;
				return mode == b.mode && name.equals(b.name)
						&& owner.equals(b.owner) && type.equals(b.type);
			}
			return false;
		}
		
		public int hashCode() {
			return name.hashCode() + type.hashCode() + owner.hashCode();			
		}
	}
	
	/**
	 * This represents the family of primitive conversion operations, such as
	 * i2f, d2f, l2i etc. Observe that in some cases (e.g. converting from a
	 * long to a byte) several bytecodes will be produced (e.g. l2i,i2b).
	 */
	public static final class Conversion extends Bytecode {
		public final JvmType.Primitive from;
		public final JvmType.Primitive to;
		
		public Conversion(JvmType.Primitive from, JvmType.Primitive to) {			
			this.from = from;
			this.to = to;
			
			// Now, sanity check this conversion operator
			if(from instanceof JvmType.Int || from instanceof JvmType.Short
					|| from instanceof JvmType.Byte || from instanceof JvmType.Char) {
				// i2l, i2f, i2d, i2c, i2b, i2s
				if(to instanceof JvmType.Long) {
					return;
				} else if(to instanceof JvmType.Float) {
					return;
				} else if(to instanceof JvmType.Double) {
					return;
				} else if(to instanceof JvmType.Char && !(from instanceof JvmType.Char)) {
					return;
				} else if(to instanceof JvmType.Short && !(from instanceof JvmType.Short)) {
					return;
				} else if(to instanceof JvmType.Byte && !(from instanceof JvmType.Byte)) {
					return;
				}
			} else if(from instanceof JvmType.Long) {
				// l2i, l2f, l2d
				if(to instanceof JvmType.Char) {
					return;
				} else if(to instanceof JvmType.Byte) {
					return;				
				} else if(to instanceof JvmType.Short) {
					return;				
				} else if(to instanceof JvmType.Int) {
					return;
				} else if(to instanceof JvmType.Float) {
					return;
				} else if(to instanceof JvmType.Double) {
					return;
				}
			} else if(from instanceof JvmType.Float) {
				// f2i, f2l, f2d
				if(to instanceof JvmType.Char) {
					return;
				} else if(to instanceof JvmType.Byte) {
					return;				
				} else if(to instanceof JvmType.Short) {
					return;				
				} else if(to instanceof JvmType.Int) {
					return;
				} else if(to instanceof JvmType.Long) {
					return;
				} else if(to instanceof JvmType.Double) {
					return;
				}
			} else if(from instanceof JvmType.Double) {
				// d2i, d2l, d2f
				if(to instanceof JvmType.Char) {
					return;
				} else if(to instanceof JvmType.Byte) {
					return;				
				} else if(to instanceof JvmType.Short) {
					return;				
				} else if(to instanceof JvmType.Int) {
					return;
				} else if(to instanceof JvmType.Long) {
					return;
				} else if(to instanceof JvmType.Float) {
					return;
				}
			}			
			
			throw new IllegalArgumentException("no valid conversion from " + from + " into " + to);	
		}	
		
		public int stackDiff() {
			return ClassFile.slotSize(to) - ClassFile.slotSize(from);
		}
		
		public byte[] toBytes(int offset, Map<String, Integer> labelOffsets,
				Map<Constant.Info, Integer> constantPool) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			if (from instanceof JvmType.Int || from instanceof JvmType.Short					
					|| from instanceof JvmType.Byte || from instanceof JvmType.Char) {
				// i2l, i2f, i2d, i2c, i2b, i2s
				if(to instanceof JvmType.Long) {
					write_u1(out,I2L);
				} else if(to instanceof JvmType.Float) {
					write_u1(out,I2F);
				} else if(to instanceof JvmType.Double) {
					write_u1(out,I2D);
				} else if(to instanceof JvmType.Char && !(from instanceof JvmType.Char)) {
					write_u1(out,I2C);
				} else if(to instanceof JvmType.Short && !(from instanceof JvmType.Short)) {
					write_u1(out,I2S);
				} else if(to instanceof JvmType.Byte && !(from instanceof JvmType.Byte)) {
					write_u1(out,I2B);
				} 
			} else if(from instanceof JvmType.Long) {
				// l2i, l2f, l2d
				if(to instanceof JvmType.Char) {
					write_u1(out,L2I);
					write_u1(out,I2C);
				} else if(to instanceof JvmType.Byte) {
					write_u1(out,L2I);
					write_u1(out,I2B);				
				} else if(to instanceof JvmType.Short) {
					write_u1(out,L2I);
					write_u1(out,I2S);				
				} else if(to instanceof JvmType.Int) {
					write_u1(out,L2I);
				} else if(to instanceof JvmType.Float) {
					write_u1(out,L2F);
				} else if(to instanceof JvmType.Double) {
					write_u1(out,L2D);
				}
			} else if(from instanceof JvmType.Float) {
				// f2i, f2l, f2d
				if(to instanceof JvmType.Char) {
					write_u1(out,F2I);
					write_u1(out,I2C);
				} else if(to instanceof JvmType.Byte) {
					write_u1(out,F2I);
					write_u1(out,I2B);				
				} else if(to instanceof JvmType.Short) {
					write_u1(out,F2I);
					write_u1(out,I2S);				
				} else if(to instanceof JvmType.Int) {
					write_u1(out,F2I);
				} else if(to instanceof JvmType.Long) {
					write_u1(out,F2L);
				} else if(to instanceof JvmType.Double) {
					write_u1(out,F2D);
				}
			} else if(from instanceof JvmType.Double) {
				// d2i, d2l, d2f
				if(to instanceof JvmType.Char) {
					write_u1(out,D2I);
					write_u1(out,I2C);
				} else if(to instanceof JvmType.Byte) {
					write_u1(out,D2I);
					write_u1(out,I2B);				
				} else if(to instanceof JvmType.Short) {
					write_u1(out,D2I);
					write_u1(out,I2S);				
				} if(to instanceof JvmType.Int) {
					write_u1(out,D2I);
				} else if(to instanceof JvmType.Long) {
					write_u1(out,D2L);
				} else if(to instanceof JvmType.Float) {
					write_u1(out,D2F);
				}
			} 
				
			return out.toByteArray();
		}
		
		public String toString() {
			if(from instanceof JvmType.Int || from instanceof JvmType.Short
					|| from instanceof JvmType.Byte || from instanceof JvmType.Char) {
				// i2l, i2f, i2d, i2c, i2b, i2s
				if(to instanceof JvmType.Long) {
					return "i2l";
				} else if(to instanceof JvmType.Float) {
					return "i2f";
				} else if(to instanceof JvmType.Double) {
					return "i2d";
				} else if(to instanceof JvmType.Char && !(from instanceof JvmType.Char)) {
					return "i2c";
				} else if(to instanceof JvmType.Short && !(from instanceof JvmType.Short)) {
					return "i2s";
				} else if(to instanceof JvmType.Byte && !(from instanceof JvmType.Byte)) {
					return "i2b";
				}
			} else if(from instanceof JvmType.Long) {
				// l2i, l2f, l2d
				if(to instanceof JvmType.Char) {					
					return "l2i ; i2c";
				} else if(to instanceof JvmType.Byte) {					
					return "l2i ; i2b";
				} else if(to instanceof JvmType.Short) {
					return "l2i ; i2s";				
				} else if(to instanceof JvmType.Int) {
					return "l2i";
				} else if(to instanceof JvmType.Float) {
					return "l2f";
				} else if(to instanceof JvmType.Double) {
					return "l2d";
				}
			} else if(from instanceof JvmType.Float) {
				// f2i, f2l, f2d
				if(to instanceof JvmType.Char) {					
					return "f2i ; i2c";
				} else if(to instanceof JvmType.Byte) {					
					return "f2i ; i2b";
				} else if(to instanceof JvmType.Short) {
					return "f2i ; i2s";				
				} else if(to instanceof JvmType.Int) {
					return "f2i";
				} else if(to instanceof JvmType.Long) {
					return "f2l";
				} else if(to instanceof JvmType.Double) {
					return "f2d";
				}
			} else if(from instanceof JvmType.Double) {
				// d2i, d2l, d2f
				if(to instanceof JvmType.Char) {					
					return "d2i ; i2c";
				} else if(to instanceof JvmType.Byte) {					
					return "d2i ; i2b";
				} else if(to instanceof JvmType.Short) {
					return "d2i ; i2s";				
				} else if(to instanceof JvmType.Int) {
					return "d2i";
				} else if(to instanceof JvmType.Long) {
					return "d2l";
				} else if(to instanceof JvmType.Float) {
					return "d2f";
				}
			}			
			
			// following should be unreachable
			throw new RuntimeException("invalid conversion operator (" + from + "=>" + to + ")");					
		}
		
		public boolean equals(Object o) {
			if (o instanceof Conversion) {
				Conversion b = (Conversion) o;
				return from.equals(b.from) && to.equals(b.to);
			}
			return false;
		}
		
		public int hashCode() {
			return from.hashCode() + to.hashCode();			
		}
	}
	
	/**
     * This class abstracts different kinds of branching statements (e.g. goto,
     * ifacmp, etc). It probably should abstract switches as well, although it
     * currently doesn't,
     */
	public static abstract class Branch extends Bytecode {
		public final String label;
		public final boolean islong;
		
		public Branch(String label) {
			this.label = label;
			this.islong = false;
		}
		
		public Branch(String label, boolean islong) {
			this.label = label;
			this.islong = islong;
		}
		
		// The purpose of this method is to force a branch to be long.
		public abstract Branch fixLong();
	}
	
	/**
	 * This class abstracts the unconditional branch bytecode goto.
	 */
	public static class Goto extends Branch {
		public Goto(String label) { super(label); }
		public Goto(String label, boolean islong) { super(label,islong); }
		
		public int stackDiff() {
			return 0;
		}
		
		public byte[] toBytes(int offset, Map<String,Integer> labelOffsets,  Map<Constant.Info,Integer> constantPool) {
			if (!labelOffsets.keySet().contains(label)) {
				throw new IllegalArgumentException("unable to resolve label \"" + label
						+ "\" in labelOffsets");
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			// here, need to figure out how far away we're going
			int target = labelOffsets.get(label) - offset;
			if(-32768 <= target && target <= 32767 && !islong) {
				write_u1(out,GOTO);
				write_i2(out,target);
			} else {
				write_u1(out,GOTO_W);
				write_i4(out,target);
			}
			return out.toByteArray();
		}
		
		public Goto fixLong() {
			return new Goto(label,true);
		}
		
		
		public String toString() {
			return "goto " + label;
		}
		
		public boolean equals(Object o) {
			if (o instanceof Goto) {
				Goto b = (Goto) o;
				return label.equals(b.label) && islong == b.islong;
			}
			return false;
		}
		
		public int hashCode() {
			return label.hashCode();			
		}
	}
	
	/**
	 * This represents the bytecodes ifeq, ifne, iflt, ifge, ifgt, ifle.
	 */
	public static class If extends Branch {
		public final static int EQ=0;
		public final static int NE=1;
		public final static int LT=2;
		public final static int GE=3;
		public final static int GT=4;
		public final static int LE=5;
		public final static int NULL=6;
		public final static int NONNULL=7;
		public final static String[] str = { "eq", "ne", "lt", "ge", "gt", ",le", "null", "nonnull" };
		
		public final int cond;
		
		public If(int cond, String label) { 			 
			super(label);
			assert cond >=0 && cond <= LE;			
			this.cond=cond;
		}
		
		public If(int cond, String label, boolean islong) { 			 
			super(label,islong);
			assert cond >=0 && cond <= LE;			
			this.cond=cond;
		}
		
		public int stackDiff() {
			return -1;
		}
		
		public byte[] toBytes(int offset, Map<String,Integer> labelOffsets,  Map<Constant.Info,Integer> constantPool) {
			if (!labelOffsets.keySet().contains(label)) {
				throw new IllegalArgumentException("unable to resolve label \"" + label
						+ "\" in labelOffsets");
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			// here, need to figure out how far away we're going
			int target = labelOffsets.get(label) - offset;
			if(-32768 <= target && target <= 32767 && !islong) {
				if(cond < NULL) {
					write_u1(out,IFEQ + cond);
				} else {
					write_u1(out,IFNULL + (cond-NULL));
				}
				write_i2(out,target);
			} else {
				// In this case, we cannot perform a direct conditional branch.
				// Instead, we simply fake it by using a wide GOTO
				
				// first, invert comparison
				int c = cond;
				if((c % 2) == 0) {
					// even, so increment
					c++;
				} else {
					// odd, so decrement
					c--;
				}
				if(cond < NULL) {
					write_u1(out,IFEQ + c);
				} else {
					write_u1(out,IFNULL + (c-NULL));
				}
				write_i2(out, 8);
				
				// now do the big jump
				write_u1(out,GOTO_W);
				write_i4(out,target-3);				
			}
			
			return out.toByteArray();
		}
		
		public If fixLong() {
			return new If(cond,label,true);
		}
		
		public String toString() {
			return "if" + str[cond] + " " + label;
		}
		
		public boolean equals(Object o) {
			if (o instanceof If) {
				If b = (If) o;
				return label.equals(b.label) && cond == b.cond && islong == b.islong;
			}
			return false;
		}
		
		public int hashCode() {
			return label.hashCode() + cond;			
		}
	}
	
	/**
	 * This represents the bytecodes fcmpl, fcmpg, dcmpl, dcmpg and lcmp
	 */
	public static class Cmp extends Bytecode {
		public final static int EQ=0;
		public final static int LT=1;
		public final static int GT=2;		
		public final static String[] str = { "", "lt", "gt" };
		public final int op;
		public final JvmType type;
		
		/**
         * Construct a cmp bytecode.
         * 
         * @param type
         *            either Type.Float, Type.Double or Type.Long
         * @param op
         *            op == LT || OP == GT if type == Float or type == Double,
         *            otherwise op == EQ
         */
		public Cmp(JvmType type, int op){
			assert (type instanceof JvmType.Double && op >= 1 && op <= 2) ||
				(type instanceof JvmType.Float && op >= 1 && op <= 2) ||
				(type instanceof JvmType.Long && op == 0);
			this.type=type;
			this.op = op;
		}
		
		public int stackDiff() {
			return 1 - ClassFile.slotSize(type);
		}
		
		public byte[] toBytes(int offset, Map<String, Integer> labelOffsets,
				Map<Constant.Info, Integer> constantPool) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			if (type instanceof JvmType.Double) {
				if(op == LT) {
					write_u1(out, DCMPL);
				} else {
					write_u1(out, DCMPG);
				}
			} else if (type instanceof JvmType.Float) {
				if(op == LT) {
					write_u1(out, FCMPL);
				} else {
					write_u1(out, FCMPG);
				}
			} else if (type instanceof JvmType.Long) {
				write_u1(out, LCMP);
			}
			return out.toByteArray();
		}
		
		public String toString(){
			if(type instanceof JvmType.Double) {
				if(op == LT) {
					return "dcmpl";
				} else {
					return "fcmpg";
				}
			} else if(type instanceof JvmType.Float) {
				if(op == LT) {			
					return "fcmpl";
				} else {
					return "fcmpg";
				}
			} else  {
				return "lcmpl";
			} 
		}
		
		public boolean equals(Object o) {
			if (o instanceof Cmp) {
				Cmp b = (Cmp) o;
				return op == b.op && type.equals(b.type);
			}
			return false;
		}
		
		public int hashCode() {
			return op + type.hashCode();			
		}
	}
	
	/**
	 * This represents the bytecodes ifacmp_XX and ificmp_xx
	 */
	public static class IfCmp extends Branch {
		public final static int EQ=0;
		public final static int NE=1;
		public final static int LT=2;
		public final static int GE=3;
		public final static int GT=4;
		public final static int LE=5;
		public final static String[] str = { "eq", "ne", "lt", "ge", "gt", ",le" };
		
		public final int cond;
		public final JvmType type;
		
		public IfCmp(int cond, JvmType type, String label) { 			 
			super(label);		
			assert type instanceof JvmType.Int || type instanceof JvmType.Reference;
			assert cond >=0 && ((type instanceof JvmType.Int && cond <= LE)
					|| (type instanceof JvmType.Reference && cond <= NE));
			this.cond = cond;
			this.type = type;
		}
		
		public IfCmp(int cond, JvmType type, String label, boolean islong) { 			 
			super(label,islong);		
			assert type instanceof JvmType.Int || type instanceof JvmType.Reference;
			assert cond >=0 && ((type instanceof JvmType.Int && cond <= LE)
					|| (type instanceof JvmType.Reference && cond <= NE));
			this.cond = cond;
			this.type = type;
		}
		
		public int stackDiff() {
			return 1-(2*ClassFile.slotSize(type));
		}
		
		public byte[] toBytes(int offset, Map<String, Integer> labelOffsets,
				Map<Constant.Info, Integer> constantPool) {
			assert labelOffsets.containsKey(label);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			// here, need to figure out how far away we're going
			int target = labelOffsets.get(label) - offset;
			if (-32768 <= target && target <= 32767 && !islong) {
				if (type instanceof JvmType.Primitive) {
					write_u1(out, IF_ICMPEQ + cond);
				} else {
					write_u1(out, IF_ACMPEQ + cond);
				}
				write_i2(out, target);
			} else {
				// In this case, we cannot perform a direct conditional branch.
				// Instead, we simply fake it by using a wide GOTO
				
				// first, invert comparison
				int c = cond;
				if((c % 2) == 0) {
					// even, so increment
					c++;
				} else {
					// odd, so decrement
					c--;
				}
				if (type instanceof JvmType.Primitive) {
					write_u1(out, IF_ICMPEQ + c);
				} else {
					write_u1(out, IF_ACMPEQ + c);
				}
				write_i2(out, 8);
				
				// now do the big jump
				write_u1(out,GOTO_W);
				write_i4(out,target-3);				
			}
			
			return out.toByteArray();
		}
		
		public IfCmp fixLong() {
			return new IfCmp(cond,type,label,true);
		}
		
		public String toString() {
			if(type instanceof JvmType.Int) {
				return "if_icmp" + str[cond] + " " + label;				
			} else {
				return "if_acmp" + str[cond] + " " + label;				
			}			
		}
		
		public boolean equals(Object o) {
			if (o instanceof IfCmp) {
				IfCmp b = (IfCmp) o;
				return cond == b.cond && type.equals(b.type)
						&& label.equals(b.label) && islong == b.islong;
			}
			return false;
		}
		
		public int hashCode() {
			return label.hashCode() + type.hashCode();			
		}
	}
	
	public static final Comparator<Pair<Integer, String>> switchcomp = new Comparator<Pair<Integer, String>>() {
		public int compare(Pair<Integer, String> p1, Pair<Integer, String> p2) {
			return p1.first().compareTo(p2.first());
		}
	};
	
	/**
	 * This represents the bytecodes tableswitch and lookupswitch
	 */
	public static class Switch extends Bytecode {
		public final String defaultLabel;
		public final List<Pair<Integer, String>> cases;
		public final int type;
				
		public Switch(String def, List<Pair<Integer, String>> cases) {
			this.defaultLabel = def;
			this.cases = cases;
			
			Collections.sort(cases, switchcomp);			
			
			if(cases.size() > 0) {
				long lo = cases.get(0).first();
				long hi = cases.get(cases.size()-1).first();
				
				long tableSize = 4+4*(hi-lo+1);
				long lookupSize = 8*(cases.size());
				
				if (tableSize < lookupSize) {
					this.type = TABLESWITCH;
				}
				else {
					this.type = LOOKUPSWITCH;
				}	
			} else {
				// yes, whilst this case may seem completely perverse, it is
				// indeed possible to have a switch statement with no cases.
				// I have seen some real code which does this!
				this.type = LOOKUPSWITCH;
			}						
		}
		
		public int stackDiff() {
			return -1;
		}
		
		public int getSize(int offset) {			
			int padding = 3 - (offset%4);
			int len = 1 + padding + 4; 			
			if (type == LOOKUPSWITCH) {
				len += 4 + cases.size() * 8;				
			} else {
				int lo = cases.get(0).first();
				int hi = cases.get(cases.size()-1).first();
				len += 8 + 4 * (hi-lo+1);				
			}
			
			return len;
		}
		
		public byte[] toBytes(int offset, Map<String,Integer> labelOffsets,  
				Map<Constant.Info,Integer> constantPool) {
			if (!labelOffsets.keySet().contains(defaultLabel)) {
				throw new IllegalArgumentException("unable to resolve label \""
						+ defaultLabel + "\" in labelOffsets");
			}			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			write_u1(out, type);			
			for (int i = 0; i < (3-(offset%4)); i++) {				
				write_u1(out, 0); // padding
			}			
			
			int def = labelOffsets.get(defaultLabel) - offset;
			write_i4(out, def);
			if (type == LOOKUPSWITCH) {
				write_i4(out, cases.size());
				for (Pair<Integer, String> c : cases) {
					write_i4(out, c.first());
					write_i4(out, labelOffsets.get(c.second()) - offset);
				}
			} else {
				int lo = cases.get(0).first();
				int hi = cases.get(cases.size()-1).first();
				write_i4(out, lo);
				write_i4(out, hi);				
				
				int index = 0;
				for (int i = lo; i <= hi; i++) {
					Pair<Integer,String> c = cases.get(index);
					if (c.first() == i) {						
						int target = labelOffsets.get(c.second()) - offset;						
						write_i4(out, target);
						index++;
					} else {
						write_i4(out, def);
					}
				}
			}
			return out.toByteArray();
		}
		
		public String toString() {
			String out = "lookupswitch \n";
			out += "\tdefault\t: "+defaultLabel+"\n";
			for (Pair<Integer, String> c: cases) {
				out += "\t"+c.first()+"\t: "+c.second()+"\n"; 
			}			
			return out;
		}
		
		public boolean equals(Object o) {
			if (o instanceof Switch) {
				Switch b = (Switch) o;
				return type == b.type && defaultLabel.equals(b.defaultLabel)
						&& cases.equals(b.cases);
			}
			return false;
		}
		
		public int hashCode() {
			return defaultLabel.hashCode() + cases.hashCode();			
		}
	}
	
	/**
	 * Represents the pop and pop2 bytecodes
	 */
	public static final class Pop extends Bytecode {
		public final JvmType type;
		
		public Pop(JvmType type) { this.type = type; }
		
		public int stackDiff() {
			return -ClassFile.slotSize(type);
		}
		
		public byte[] toBytes(int offset, Map<String,Integer> labelOffsets,  
				Map<Constant.Info,Integer> constantPool) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			if(ClassFile.slotSize(type) > 1) {
				write_u1(out,POP2);
			} else {
				write_u1(out,POP);
			}
			return out.toByteArray();
		}
		
		public String toString() {			
			if(ClassFile.slotSize(type) > 1) { return "pop2"; } 
			else {
				return "pop";
			}			
		}
		
		public boolean equals(Object o) {
			if (o instanceof Pop) {
				Pop b = (Pop) o;
				return type.equals(b.type);
			}
			return false;
		}
		
		public int hashCode() {
			return type.hashCode();			
		}
	}
	
	/**
	 * Represents the dup and dup2 bytecodes
	 */
	public static final class Dup extends Bytecode {
		public final JvmType type;
		
		public Dup(JvmType type) { this.type = type; }
		
		public int stackDiff() {
			return ClassFile.slotSize(type);
		}
		
		public byte[] toBytes(int offset, Map<String,Integer> labelOffsets,  
				Map<Constant.Info,Integer> constantPool) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			if(ClassFile.slotSize(type) > 1) {
				write_u1(out,DUP2);
			} else {
				write_u1(out,DUP);
			}
			return out.toByteArray();
		}
		
		public String toString() {			
			if(ClassFile.slotSize(type) > 1) { return "dup2"; } 
			else {
				return "dup";
			}			
		}
		
		public boolean equals(Object o) {
			if (o instanceof Dup) {
				Dup b = (Dup) o;
				return type.equals(b.type);
			}
			return false;
		}
		
		public int hashCode() {
			return type.hashCode();			
		}
	}
	
	/**
	 * Represents the dupx1 bytecode
	 */
	public static final class DupX1 extends Bytecode {
		public int stackDiff() {
			return 1;
		}
		
		public byte[] toBytes(int offset, Map<String,Integer> labelOffsets,  
				Map<Constant.Info,Integer> constantPool) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			write_u1(out,DUP_X1);	
			return out.toByteArray();
		}
		
		public String toString() {			
			return "dup_x1";			
		}
		
		public boolean equals(Object o) {
			return o instanceof DupX1;
		}
		
		public int hashCode() {
			return 12231;			
		}
	}
	
	/**
	 * Represents the dupx2 bytecode
	 */
	public static final class DupX2 extends Bytecode {
		public int stackDiff() {
			return 2;
		}
		
		public byte[] toBytes(int offset, Map<String,Integer> labelOffsets,  
				Map<Constant.Info,Integer> constantPool) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			write_u1(out,DUP_X2);	
			return out.toByteArray();
		}
		
		public String toString() {			
			return "dup_x2";			
		}
		
		public boolean equals(Object o) {
			return o instanceof DupX2;
		}
		
		public int hashCode() {
			return 389282;			
		}
	}
	
	/**
	 * Represents the swap bytecode
	 */
	public static final class Swap extends Bytecode {
		public int stackDiff() {
			return 0;
		}
		
		public byte[] toBytes(int offset, Map<String,Integer> labelOffsets,  
				Map<Constant.Info,Integer> constantPool) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			write_u1(out,SWAP);	
			return out.toByteArray();
		}
		
		public String toString() {			
			return "swap";			
		}
		
		public boolean equals(Object o) {
			return o instanceof Swap;
		}
		
		public int hashCode() {
			return 389282;			
		}
	}
	
	/**
	 * Represents the new, newarray and anewarray, multinewarray bytecodes.
	 */
	public static final class New extends Bytecode {
		public final JvmType type;
		public final int dims;
		
		public New(JvmType type) {
			assert type instanceof JvmType.Reference || type instanceof JvmType.Array;
			this.type = type;
			this.dims = -1;
		}
		
		public New(JvmType type, int dims) {
			assert type instanceof JvmType.Reference || type instanceof JvmType.Array;
			this.type = type;
			this.dims = dims;
		}
		
		public int stackDiff() {
			return 1;
		}
		
		public void addPoolItems(Set<Constant.Info> constantPool) {
			if(type instanceof JvmType.Array) {
				JvmType.Array atype = (JvmType.Array) type;
				JvmType elementType = atype.element();
				if(dims > 1) {
					Constant.addPoolItem(Constant
							.buildClass((JvmType.Array) type),constantPool);
				} else if(elementType instanceof JvmType.Reference) {
					Constant.addPoolItem(Constant
							.buildClass((JvmType.Reference) elementType),constantPool);					
				}
			} else {
				Constant.addPoolItem(Constant
						.buildClass((JvmType.Clazz) type),constantPool);				
			}
		}
		
		public byte[] toBytes(int offset, Map<String,Integer> labelOffsets,  
				Map<Constant.Info,Integer> constantPool) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();			
			if(type instanceof JvmType.Array) {
				JvmType.Array atype = (JvmType.Array) type;
				if(dims > 1) {
					int idx = constantPool.get(Constant
								.buildClass((JvmType.Array) type));
					write_u1(out,MULTIANEWARRAY);
					write_u2(out,idx);
					write_u1(out,dims);
				} else {				
					JvmType elementType = atype.element();
					
					if(elementType instanceof JvmType.Reference) {
						int idx = constantPool.get(Constant
								.buildClass((JvmType.Reference) elementType));
						write_u1(out,ANEWARRAY);
						write_u2(out,idx);
					} else {
						write_u1(out,NEWARRAY);		
						
						if(elementType instanceof JvmType.Bool) {
							write_u1(out, T_BOOLEAN);
						} else if(elementType instanceof JvmType.Char) {
							write_u1(out, T_CHAR);
						} else if(elementType instanceof JvmType.Byte) {
							write_u1(out, T_BYTE);
						} else if(elementType instanceof JvmType.Int) {
							write_u1(out, T_INT);
						} else if(elementType instanceof JvmType.Short) {
							write_u1(out, T_SHORT);
						} else if(elementType instanceof JvmType.Long) {
							write_u1(out, T_LONG);
						} else if(elementType instanceof JvmType.Float) {
							write_u1(out, T_FLOAT);
						} else if(elementType instanceof JvmType.Double) {
							write_u1(out, T_DOUBLE);
						} else {
							throw new RuntimeException("internal failure constructing " + elementType);
						}
					}
				}									
			} else {
			int idx = constantPool.get(Constant
						.buildClass((JvmType.Reference) type));
				write_u1(out,NEW);
				write_u2(out,idx);
			}
			return out.toByteArray();
		}
		
		public String toString() {			
			if(type instanceof JvmType.Array) { 
				JvmType.Array atype = (JvmType.Array) type;
				JvmType elementType = atype.element();
				if(dims > 1) {
					return "multianewarray " + type + ", " + dims;
				} else if(elementType instanceof JvmType.Reference 
						|| elementType instanceof JvmType.Array) {				
					return "anewarray " + type; 
				} else {
					return "newarray " + type;
				}
			} else {
				return "new " + type;
			}			
		}
		
		public boolean equals(Object o) {
			if (o instanceof New) {
				New b = (New) o;
				return dims == b.dims && type.equals(b.type);
			}
			return false;
		}
		
		public int hashCode() {
			return type.hashCode() + dims;			
		}
	}
	
	/**
	 * Represents the arraylength bytecode
	 */
	public static final class ArrayLength extends Bytecode {
		public int stackDiff() {
			return 1;
		}
		
		public byte[] toBytes(int offset, Map<String,Integer> labelOffsets,  Map<Constant.Info,Integer> constantPool) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			write_u1(out,ARRAYLENGTH);
			return out.toByteArray();
		}
		
		public String toString() { return "arraylength"; }
		
		public boolean equals(Object o) {
			return o instanceof ArrayLength;
		}
		
		public int hashCode() {
			return 122199;			
		}
	}
	
	/**
	 * Represents the check cast bytecode.
	 */
	public static final class CheckCast extends Bytecode {
		public final JvmType type;
		
		/**
		 * Check a reference on the stack has the given type.
		 * 
		 * @param type --- must be either Type.Array or Type.Clazz 
		 */
		public CheckCast(JvmType type) {
			if(!(type instanceof JvmType.Clazz) && !(type instanceof JvmType.Array)) {
				throw new IllegalArgumentException("checkcast cannot accept " + type);
			}
			this.type = type; 
		}
		
		public int stackDiff() {
			return 0;
		}
		
		public void addPoolItems(Set<Constant.Info> constantPool) {
			Constant.addPoolItem(
						Constant.buildClass((JvmType.Reference) type),
						constantPool);
		}
		
		public byte[] toBytes(int offset, Map<String,Integer> labelOffsets,  
				Map<Constant.Info,Integer> constantPool) {
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();			
			int idx;
			
			idx = constantPool.get(Constant
						.buildClass((JvmType.Reference) type));
			
			write_u1(out,CHECKCAST);
			write_u2(out,idx);
			
			return out.toByteArray();
		}
		
		public String toString() {						
			return "checkcast " + ClassFile.descriptor(type,false);					
		}
		
		public boolean equals(Object o) {
			if (o instanceof CheckCast) {
				CheckCast b = (CheckCast) o;
				return type.equals(b.type);
			}
			return false;
		}
		
		public int hashCode() {
			return type.hashCode();			
		}
	}
	
	/**
	 * Represents the instanceof bytecode
	 */
	public static final class InstanceOf extends Bytecode {
		public final JvmType.Reference type;
		
		public InstanceOf(JvmType.Reference type) { this.type = type; }
		
		public int stackDiff() {
			return 0;
		}
		
		public void addPoolItems(Set<Constant.Info> constantPool) {			
				Constant.addPoolItem(Constant.buildClass(type), constantPool);
		}
		
		public byte[] toBytes(int offset, Map<String,Integer> labelOffsets,  
				Map<Constant.Info,Integer> constantPool) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();			
			
			int idx = constantPool.get(Constant
						.buildClass(type));		
			
			write_u1(out,INSTANCEOF);
			write_u2(out,idx);
			
			return out.toByteArray();
		}
		
		public String toString() {						
			return "instanceof " + type;					
		}
		
		public boolean equals(Object o) {
			if (o instanceof InstanceOf) {
				InstanceOf b = (InstanceOf) o;
				return type.equals(b.type);
			}
			return false;
		}
		
		public int hashCode() {
			return type.hashCode();			
		}
	}
	
	/**
	 * Represents the nop bytecode.
	 */
	public static final class Nop extends Bytecode {
		public int stackDiff() {
			return 0;
		}
		
		public byte[] toBytes(int offset, Map<String,Integer> labelOffsets,  
				Map<Constant.Info,Integer> constantPool) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			write_u1(out,NOP);		
			return out.toByteArray();
		}
		
		public String toString() {						
			return "nop";					
		}
		
		public boolean equals(Object o) {
			return o instanceof Nop;
		}
		
		public int hashCode() {
			return 97364;			
		}
	}
	
	/**
	 * Represents the athrow bytecode.
	 */
	public static final class Throw extends Bytecode {
		public int stackDiff() {
			return 1; // this is slightly annoying, since it's not always needed!!
		}
		
		public byte[] toBytes(int offset, Map<String,Integer> labelOffsets,  
				Map<Constant.Info,Integer> constantPool) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			write_u1(out,ATHROW);		
			return out.toByteArray();
		}
		
		public String toString() {						
			return "athrow";					
		}
		
		public boolean equals(Object o) {
			return o instanceof Throw;
		}
		
		public int hashCode() {
			return 44520;			
		}
	}
		
	/**
	 * Represents a monitorenter bytecode
	 */
	public static final class MonitorEnter extends Bytecode {
		public int stackDiff() {
			return -1;
		}
		public byte[] toBytes(int offset, Map<String,Integer> labelOffsets,  
				Map<Constant.Info,Integer> constantPool) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			write_u1(out,MONITORENTER);		
			return out.toByteArray();
		}
		
		public String toString() {						
			return "monitorenter";					
		}
		
		public boolean equals(Object o) {
			return o instanceof MonitorEnter;
		}
		
		public int hashCode() {
			return 998654;			
		}
	}
	
	/**
	 * Represents a monitorexit bytecode
	 */
	public static final class MonitorExit extends Bytecode {
		public int stackDiff() {
			return -1;
		}
		
		public byte[] toBytes(int offset, Map<String,Integer> labelOffsets,  
				Map<Constant.Info,Integer> constantPool) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			write_u1(out,MONITOREXIT);		
			return out.toByteArray();
		}
		
		public String toString() {						
			return "monitorexit";					
		}
		
		public boolean equals(Object o) {
			return o instanceof MonitorExit;
		}
		
		public int hashCode() {
			return 12354;			
		}
	}
	// ==============================
	// ======= HELPER METHODS =======
	// ==============================
	
	public static String[] get() {
		String[] map = new String[256];
		// initialise the map using reflection!
		try {
			Class<?> c = Class.forName("jkit.util.Bytecode");
			for(java.lang.reflect.Field f : c.getDeclaredFields()) {
				String name = f.getName();
				if(name.contains("STOP")) {
					
				} else {
					// instruction opcode
					int i = f.getInt(null);
					map[i] = name;
				}				
			}
		} catch(IllegalAccessException e) {
			throw new RuntimeException("illegal access exception");
		} catch(ClassNotFoundException e) {
			throw new RuntimeException("unable to initialise OpcodeMap");
		}
		return map;
	}
	
	private static char typeChar(JvmType type) {
		if(type instanceof JvmType.Reference 
				|| type instanceof JvmType.Null
				|| type instanceof JvmType.Array) {
			return 'a';
		} else if(type instanceof JvmType.Int || type instanceof JvmType.Byte
				|| type instanceof JvmType.Char || type instanceof JvmType.Short
				|| type instanceof JvmType.Bool || type instanceof JvmType.Byte) {
			return 'i';
		} else if(type instanceof JvmType.Long) {
			return 'l';
		} else if(type instanceof JvmType.Float) {
			return 'f';
		} else if(type instanceof JvmType.Double) {
			return 'd';
		} else {
			throw new RuntimeException("unknown type encountered (" + type + ")");
		}
	}
	
	private static int typeOffset(JvmType type) {
		if(type instanceof JvmType.Reference 
				|| type instanceof JvmType.Null 
				|| type instanceof JvmType.Array
				|| type instanceof JvmType.Variable) {
			return 4;
		} else if(type instanceof JvmType.Int || type instanceof JvmType.Byte
				|| type instanceof JvmType.Char || type instanceof JvmType.Short
				|| type instanceof JvmType.Bool || type instanceof JvmType.Byte) {
			return 0;
		} else if(type instanceof JvmType.Long) {
			return 1;
		} else if(type instanceof JvmType.Float) {
			return 2;
		} else if(type instanceof JvmType.Double) {
			return 3;
		} else {
			throw new RuntimeException("unknown type encountered (" + type + ")");
		}
	}

	private static int typeArrayOffset(JvmType type) {
		if (type instanceof JvmType.Int) {
			return 0;
		} else if (type instanceof JvmType.Long) {
			return 1;
		} else if(type instanceof JvmType.Float) {
			return 2;
		} else if(type instanceof JvmType.Double) {
			return 3;
		} else if(type instanceof JvmType.Array) {
			return 4;
		} else if (type instanceof JvmType.Reference
				|| type instanceof JvmType.Variable
				|| type instanceof JvmType.Wildcard) {
			return 4; //same as array
		} else if(type instanceof JvmType.Byte) {
			return 5;
		} else if(type instanceof JvmType.Bool) {
			return 5; //same as byte
		} else if(type instanceof JvmType.Char) {
			return 6;		
		} else if(type instanceof JvmType.Short) {
			return 7;
		} else {
			throw new RuntimeException("unknown type in array: " + type);
		}
	}
	
	private static char typeArrayChar(JvmType type) {
		if(type instanceof JvmType.Byte || type instanceof JvmType.Bool) {
			return 'b';
		} else if(type instanceof JvmType.Char) {
			return 'c';		
		} else if(type instanceof JvmType.Short) {
			return 's';
		} else {
			return typeChar(type);
		}
	}
	
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
	
	@SuppressWarnings("unused")
	private static void write_u8(ByteArrayOutputStream output, long w) {
		write_u4(output, ((w >> 32) & 0xFFFFFFFFL));
		write_u4(output, (w & 0xFFFFFFFFL));
	}
	
	private static void write_i1(ByteArrayOutputStream output, int w) {
		output.write(w);								
	}
	
	private static void write_i2(ByteArrayOutputStream output, int w) {		
		output.write(w >> 8);
		output.write(w);
	}
	
	private static void write_i4(ByteArrayOutputStream output, int w) {
		output.write(w >> 24);
		output.write(w >> 16);
		output.write(w >> 8);
		output.write(w);						
	}
			
    // opcodes

    public static final int NOP = 0; 
    public static final int ACONST_NULL = 1; 
    public static final int ICONST_M1 = 2; 
    public static final int ICONST_0 = 3; 
    public static final int ICONST_1 = 4; 
    public static final int ICONST_2 = 5; 
    public static final int ICONST_3 = 6; 
    public static final int ICONST_4 = 7; 
    public static final int ICONST_5 = 8; 
    public static final int LCONST_0 = 9; 
    public static final int LCONST_1 = 10; 
    public static final int FCONST_0 = 11; 
    public static final int FCONST_1 = 12; 
    public static final int FCONST_2 = 13; 
    public static final int DCONST_0 = 14; 
    public static final int DCONST_1 = 15; 
    public static final int BIPUSH = 16; 
    public static final int SIPUSH = 17; 
    public static final int LDC = 18; 
    public static final int LDC_W = 19; 
    public static final int LDC2_W = 20; 
    public static final int ILOAD = 21; 
    public static final int LLOAD = 22; 
    public static final int FLOAD = 23; 
    public static final int DLOAD = 24; 
    public static final int ALOAD = 25; 
    public static final int ILOAD_0 = 26; 
    public static final int ILOAD_1 = 27; 
    public static final int ILOAD_2 = 28; 
    public static final int ILOAD_3 = 29; 
    public static final int LLOAD_0 = 30; 
    public static final int LLOAD_1 = 31; 
    public static final int LLOAD_2 = 32; 
    public static final int LLOAD_3 = 33; 
    public static final int FLOAD_0 = 34; 
    public static final int FLOAD_1 = 35; 
    public static final int FLOAD_2 = 36; 
    public static final int FLOAD_3 = 37; 
    public static final int DLOAD_0 = 38; 
    public static final int DLOAD_1 = 39; 
    public static final int DLOAD_2 = 40; 
    public static final int DLOAD_3 = 41; 
    public static final int ALOAD_0 = 42; 
    public static final int ALOAD_1 = 43; 
    public static final int ALOAD_2 = 44; 
    public static final int ALOAD_3 = 45; 
    public static final int IALOAD = 46; 
    public static final int LALOAD = 47; 
    public static final int FALOAD = 48; 
    public static final int DALOAD = 49; 
    public static final int AALOAD = 50; 
    public static final int BALOAD = 51; 
    public static final int CALOAD = 52; 
    public static final int SALOAD = 53; 
    public static final int ISTORE = 54; 
    public static final int LSTORE = 55; 
    public static final int FSTORE = 56; 
    public static final int DSTORE = 57; 
    public static final int ASTORE = 58; 
    public static final int ISTORE_0 = 59; 
    public static final int ISTORE_1 = 60; 
    public static final int ISTORE_2 = 61; 
    public static final int ISTORE_3 = 62; 
    public static final int LSTORE_0 = 63; 
    public static final int LSTORE_1 = 64; 
    public static final int LSTORE_2 = 65; 
    public static final int LSTORE_3 = 66; 
    public static final int FSTORE_0 = 67; 
    public static final int FSTORE_1 = 68; 
    public static final int FSTORE_2 = 69; 
    public static final int FSTORE_3 = 70; 
    public static final int DSTORE_0 = 71; 
    public static final int DSTORE_1 = 72; 
    public static final int DSTORE_2 = 73; 
    public static final int DSTORE_3 = 74; 
    public static final int ASTORE_0 = 75; 
    public static final int ASTORE_1 = 76; 
    public static final int ASTORE_2 = 77; 
    public static final int ASTORE_3 = 78; 
    public static final int IASTORE = 79; 
    public static final int LASTORE = 80; 
    public static final int FASTORE = 81; 
    public static final int DASTORE = 82; 
    public static final int AASTORE = 83; 
    public static final int BASTORE = 84; 
    public static final int CASTORE = 85; 
    public static final int SASTORE = 86; 
    public static final int POP = 87; 
    public static final int POP2 = 88; 
    public static final int DUP = 89; 
    public static final int DUP_X1 = 90; 
    public static final int DUP_X2 = 91; 
    public static final int DUP2 = 92; 
    public static final int DUP2_X1 = 93; 
    public static final int DUP2_X2 = 94; 
    public static final int SWAP = 95; 
    public static final int IADD = 96; 
    public static final int LADD = 97; 
    public static final int FADD = 98; 
    public static final int DADD = 99; 
    public static final int ISUB = 100; 
    public static final int LSUB = 101; 
    public static final int FSUB = 102; 
    public static final int DSUB = 103; 
    public static final int IMUL = 104; 
    public static final int LMUL = 105; 
    public static final int FMUL = 106; 
    public static final int DMUL = 107; 
    public static final int IDIV = 108; 
    public static final int LDIV = 109; 
    public static final int FDIV = 110; 
    public static final int DDIV = 111; 
    public static final int IREM = 112; 
    public static final int LREM = 113; 
    public static final int FREM = 114; 
    public static final int DREM = 115; 
    public static final int INEG = 116; 
    public static final int LNEG = 117; 
    public static final int FNEG = 118; 
    public static final int DNEG = 119; 
    public static final int ISHL = 120; 
    public static final int LSHL = 121; 
    public static final int ISHR = 122; 
    public static final int LSHR = 123; 
    public static final int IUSHR = 124; 
    public static final int LUSHR = 125; 
    public static final int IAND = 126; 
    public static final int LAND = 127; 
    public static final int IOR = 128; 
    public static final int LOR = 129; 
    public static final int IXOR = 130; 
    public static final int LXOR = 131; 
    public static final int IINC = 132; 
    public static final int I2L = 133; 
    public static final int I2F = 134; 
    public static final int I2D = 135; 
    public static final int L2I = 136; 
    public static final int L2F = 137; 
    public static final int L2D = 138; 
    public static final int F2I = 139; 
    public static final int F2L = 140; 
    public static final int F2D = 141; 
    public static final int D2I = 142; 
    public static final int D2L = 143; 
    public static final int D2F = 144; 
    public static final int I2B = 145; 
    public static final int I2C = 146; 
    public static final int I2S = 147; 
    public static final int LCMP = 148; 
    public static final int FCMPL = 149; 
    public static final int FCMPG = 150; 
    public static final int DCMPL = 151; 
    public static final int DCMPG = 152; 
    public static final int IFEQ = 153; 
    public static final int IFNE = 154; 
    public static final int IFLT = 155; 
    public static final int IFGE = 156; 
    public static final int IFGT = 157; 
    public static final int IFLE = 158; 
    public static final int IF_ICMPEQ = 159; 
    public static final int IF_ICMPNE = 160; 
    public static final int IF_ICMPLT = 161; 
    public static final int IF_ICMPGE = 162; 
    public static final int IF_ICMPGT = 163; 
    public static final int IF_ICMPLE = 164; 
    public static final int IF_ACMPEQ = 165; 
    public static final int IF_ACMPNE = 166; 
    public static final int GOTO = 167; 
    public static final int JSR = 168; 
    public static final int RET = 169; 
    public static final int TABLESWITCH = 170; 
    public static final int LOOKUPSWITCH = 171; 
    public static final int IRETURN = 172; 
    public static final int LRETURN = 173; 
    public static final int FRETURN = 174; 
    public static final int DRETURN = 175; 
    public static final int ARETURN = 176; 
    public static final int RETURN = 177; 
    public static final int GETSTATIC = 178; 
    public static final int PUTSTATIC = 179; 
    public static final int GETFIELD = 180; 
    public static final int PUTFIELD = 181; 
    public static final int INVOKEVIRTUAL = 182; 
    public static final int INVOKESPECIAL = 183; 
    public static final int INVOKESTATIC = 184; 
    public static final int INVOKEINTERFACE = 185; 
    public static final int UNUSED = 186; 
    public static final int NEW = 187; 
    public static final int NEWARRAY = 188; 
    public static final int ANEWARRAY = 189; 
    public static final int ARRAYLENGTH = 190; 
    public static final int ATHROW = 191; 
    public static final int CHECKCAST = 192; 
    public static final int INSTANCEOF = 193; 
    public static final int MONITORENTER = 194; 
    public static final int MONITOREXIT = 195; 
    public static final int WIDE = 196; 
    public static final int MULTIANEWARRAY = 197; 
    public static final int IFNULL = 198;
    public static final int IFNONNULL = 199; 
    public static final int GOTO_W = 200; 
    public static final int JSR_W = 201; 
    public static final int BREAKPOINT = 202; // reserved
    
    public static final int IMPDEP1 = 254;    // reserved
    public static final int IMPDEP2 = 255;    // reserved
    
    // Array Types
    
    public static final int T_BOOLEAN = 4;
    public static final int T_CHAR = 5;
    public static final int T_FLOAT = 6;
    public static final int T_DOUBLE = 7;
    public static final int T_BYTE = 8;
    public static final int T_SHORT = 9;
    public static final int T_INT = 10;
    public static final int T_LONG = 11;        
}
