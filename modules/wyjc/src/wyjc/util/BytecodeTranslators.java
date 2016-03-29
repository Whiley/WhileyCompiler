package wyjc.util;

import static jasm.lang.JvmTypes.*;
import static wyjc.Wyil2JavaBuilder.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import jasm.lang.Bytecode;
import jasm.lang.JvmType;
import wyil.lang.Code;
import wyil.lang.Codes;
import wyil.lang.Type;
import wyjc.Wyil2JavaBuilder.BytecodeTranslator;
import wyjc.Wyil2JavaBuilder.Context;

/**
 * Provides implementations for the standard internal functions
 * 
 * @author David J. Pearce
 *
 */
public class BytecodeTranslators {
	/**
	 * The standard functions for use with the interpreter.
	 */
	public static final BytecodeTranslator[] standardFunctions = new BytecodeTranslator[255];
	
	static {
		standardFunctions[Code.OPCODE_neg] = new Negate();
		standardFunctions[Code.OPCODE_arrayinvert] = new Invert();	
		standardFunctions[Code.OPCODE_dereference] = new Dereference();
		standardFunctions[Code.OPCODE_arraylength] = new ArrayLength();	
		standardFunctions[Code.OPCODE_add ] = new Add();
		standardFunctions[Code.OPCODE_sub ] = new Subtract();
		standardFunctions[Code.OPCODE_mul ] = new Multiply();
		standardFunctions[Code.OPCODE_div ] = new Divide();
		standardFunctions[Code.OPCODE_rem ] = new Remainder();
		standardFunctions[Code.OPCODE_bitwiseor] = new BitwiseOr();
		standardFunctions[Code.OPCODE_bitwisexor] = new BitwiseXor();
		standardFunctions[Code.OPCODE_bitwiseand] = new BitwiseAnd();
		standardFunctions[Code.OPCODE_lshr] = new LeftShift();
		standardFunctions[Code.OPCODE_rshr] = new RightShift();
		standardFunctions[Code.OPCODE_arrayindex] = new ArrayIndex();	
		standardFunctions[Code.OPCODE_arrygen] = new ArrayGenerator();
		standardFunctions[Code.OPCODE_array] = new ArrayConstructor();
		standardFunctions[Code.OPCODE_record] = new RecordConstructor();
	};
	
	// ====================================================================================
	// References
	// ====================================================================================

	private static final class Dereference implements BytecodeTranslator {
		@Override
		public void translate(Codes.Operator bytecode, Context context) {
			JvmType.Clazz type = (JvmType.Clazz)  context.toJvmType(bytecode.type(0));
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT);
			context.add(new Bytecode.Load(bytecode.operand(0), type));
			context.add(new Bytecode.Invoke(WHILEYOBJECT, "state", ftype, Bytecode.InvokeMode.VIRTUAL));
			// finally, we need to cast the object we got back appropriately.
			Type.Reference pt = (Type.Reference) bytecode.type(0);
			context.addReadConversion(pt.element());
			context.add(new Bytecode.Store(bytecode.target(0), type));
		}		
	}
	// ====================================================================================
	// Arithmetic
	// ====================================================================================
	
	private static final class Negate implements BytecodeTranslator {
		@Override
		public void translate(Codes.Operator bytecode, Context context) {
			JvmType.Clazz type = (JvmType.Clazz) context.toJvmType(bytecode.type(0));
			JvmType.Function ftype = new JvmType.Function(type);
			context.add(new Bytecode.Load(bytecode.operand(0), type));
			context.add(new Bytecode.Invoke(type, "negate", ftype, Bytecode.InvokeMode.VIRTUAL));
			context.add(new Bytecode.Store(bytecode.target(0), type));
		}		
	}
	private static final class Add implements BytecodeTranslator {
		@Override
		public void translate(Codes.Operator bytecode, Context context) {
			JvmType.Clazz type = (JvmType.Clazz)  context.toJvmType(bytecode.type(0));
			JvmType.Function ftype = new JvmType.Function(type, type);
			context.add(new Bytecode.Load(bytecode.operand(0), type));
			context.add(new Bytecode.Load(bytecode.operand(1), type));
			context.add(new Bytecode.Invoke((JvmType.Clazz) type, "add", ftype, Bytecode.InvokeMode.VIRTUAL));
			context.add(new Bytecode.Store(bytecode.target(0), type));
		}		
	}
	private static final class Subtract implements BytecodeTranslator {
		@Override
		public void translate(Codes.Operator bytecode, Context context) {
			JvmType.Clazz type = (JvmType.Clazz)  context.toJvmType(bytecode.type(0));
			JvmType.Function ftype = new JvmType.Function(type, type);
			context.add(new Bytecode.Load(bytecode.operand(0), type));
			context.add(new Bytecode.Load(bytecode.operand(1), type));
			context.add(new Bytecode.Invoke((JvmType.Clazz) type, "subtract", ftype, Bytecode.InvokeMode.VIRTUAL));
			context.add(new Bytecode.Store(bytecode.target(0), type));
		}		
	}
	private static final class Multiply implements BytecodeTranslator {
		@Override
		public void translate(Codes.Operator bytecode, Context context) {
			JvmType.Clazz type = (JvmType.Clazz)  context.toJvmType(bytecode.type(0));
			JvmType.Function ftype = new JvmType.Function(type, type);
			context.add(new Bytecode.Load(bytecode.operand(0), type));
			context.add(new Bytecode.Load(bytecode.operand(1), type));
			context.add(new Bytecode.Invoke((JvmType.Clazz) type, "multiply", ftype, Bytecode.InvokeMode.VIRTUAL));
			context.add(new Bytecode.Store(bytecode.target(0), type));
		}		
	}
	private static final class Divide implements BytecodeTranslator {
		@Override
		public void translate(Codes.Operator bytecode, Context context) {
			JvmType.Clazz type = (JvmType.Clazz)  context.toJvmType(bytecode.type(0));
			JvmType.Function ftype = new JvmType.Function(type, type);
			context.add(new Bytecode.Load(bytecode.operand(0), type));
			context.add(new Bytecode.Load(bytecode.operand(1), type));
			context.add(new Bytecode.Invoke((JvmType.Clazz) type, "divide", ftype, Bytecode.InvokeMode.VIRTUAL));
			context.add(new Bytecode.Store(bytecode.target(0), type));
		}		
	}
	private static final class Remainder implements BytecodeTranslator {
		@Override
		public void translate(Codes.Operator bytecode, Context context) {
			JvmType.Clazz type = (JvmType.Clazz)  context.toJvmType(bytecode.type(0));
			JvmType.Function ftype = new JvmType.Function(type, type);
			context.add(new Bytecode.Load(bytecode.operand(0), type));
			context.add(new Bytecode.Load(bytecode.operand(1), type));
			context.add(new Bytecode.Invoke((JvmType.Clazz) type, "remainder", ftype, Bytecode.InvokeMode.VIRTUAL));
			context.add(new Bytecode.Store(bytecode.target(0), type));
		}		
	}

	// ====================================================================================
	// Bytes
	// ====================================================================================
	private static final class Invert implements BytecodeTranslator {
		@Override
		public void translate(Codes.Operator bytecode, Context context) {
			JvmType.Clazz type = (JvmType.Clazz)  context.toJvmType(bytecode.type(0));
			JvmType.Function ftype = new JvmType.Function(type);
			context.add(new Bytecode.Load(bytecode.operand(0), type));
			context.add(new Bytecode.Invoke(WHILEYBYTE, "compliment", ftype, Bytecode.InvokeMode.VIRTUAL));
			context.add(new Bytecode.Store(bytecode.target(0), type));
		}		
	}
	private static final class BitwiseOr implements BytecodeTranslator {
		@Override
		public void translate(Codes.Operator bytecode, Context context) {
			JvmType.Clazz type = (JvmType.Clazz)  context.toJvmType(bytecode.type(0));
			JvmType.Function ftype = new JvmType.Function(type, type);
			context.add(new Bytecode.Load(bytecode.operand(0), type));
			context.add(new Bytecode.Load(bytecode.operand(1), type));
			context.add(new Bytecode.Invoke(WHILEYBYTE, "or", ftype, Bytecode.InvokeMode.VIRTUAL));
			context.add(new Bytecode.Store(bytecode.target(0), type));
		}		
	}
	private static final class BitwiseXor implements BytecodeTranslator {
		@Override
		public void translate(Codes.Operator bytecode, Context context) {
			JvmType.Clazz type = (JvmType.Clazz)  context.toJvmType(bytecode.type(0));
			JvmType.Function ftype = new JvmType.Function(type, type);
			context.add(new Bytecode.Load(bytecode.operand(0), type));
			context.add(new Bytecode.Load(bytecode.operand(1), type));
			context.add(new Bytecode.Invoke(WHILEYBYTE, "xor", ftype, Bytecode.InvokeMode.VIRTUAL));
			context.add(new Bytecode.Store(bytecode.target(0), type));
		}		
	}
	private static final class BitwiseAnd implements BytecodeTranslator {
		@Override
		public void translate(Codes.Operator bytecode, Context context) {
			JvmType.Clazz type = (JvmType.Clazz)  context.toJvmType(bytecode.type(0));
			JvmType.Function ftype = new JvmType.Function(type, type);
			context.add(new Bytecode.Load(bytecode.operand(0), type));
			context.add(new Bytecode.Load(bytecode.operand(1), type));
			context.add(new Bytecode.Invoke(WHILEYBYTE, "and", ftype, Bytecode.InvokeMode.VIRTUAL));
			context.add(new Bytecode.Store(bytecode.target(0), type));
		}		
	}
	private static final class LeftShift implements BytecodeTranslator {
		@Override
		public void translate(Codes.Operator bytecode, Context context) {
			JvmType.Clazz type = (JvmType.Clazz)  context.toJvmType(bytecode.type(0));
			JvmType.Function ftype = new JvmType.Function(type, WHILEYINT);
			context.add(new Bytecode.Load(bytecode.operand(0), type));
			context.add(new Bytecode.Load(bytecode.operand(1), WHILEYINT));
			context.add(new Bytecode.Invoke(WHILEYBYTE, "leftShift", ftype, Bytecode.InvokeMode.VIRTUAL));
			context.add(new Bytecode.Store(bytecode.target(0), type));
		}		
	}
	private static final class RightShift implements BytecodeTranslator {
		@Override
		public void translate(Codes.Operator bytecode, Context context) {
			JvmType.Clazz type = (JvmType.Clazz)  context.toJvmType(bytecode.type(0));
			JvmType.Function ftype = new JvmType.Function(type, WHILEYINT);
			context.add(new Bytecode.Load(bytecode.operand(0), type));
			context.add(new Bytecode.Load(bytecode.operand(1), WHILEYINT));
			context.add(new Bytecode.Invoke(WHILEYBYTE, "rightShift", ftype, Bytecode.InvokeMode.VIRTUAL));
			context.add(new Bytecode.Store(bytecode.target(0), type));
		}		
	}
	// ====================================================================================
	// Arrays
	// ====================================================================================
	private static final class ArrayLength implements BytecodeTranslator {
		@Override
		public void translate(Codes.Operator bytecode, Context context) {
			JvmType.Clazz type = (JvmType.Clazz)  context.toJvmType(bytecode.type(0));
			JvmType.Function ftype = new JvmType.Function(WHILEYINT);
			context.add(new Bytecode.Load(bytecode.operand(0), type));			
			context.add(new Bytecode.Invoke(WHILEYARRAY, "length", ftype, Bytecode.InvokeMode.VIRTUAL));
			context.add(new Bytecode.Store(bytecode.target(0), type));
		}		
	}
	private static final class ArrayIndex implements BytecodeTranslator {
		@Override
		public void translate(Codes.Operator bytecode, Context context) {
			Type.EffectiveArray arrType = (Type.EffectiveArray) bytecode.type(0);
			JvmType elementType = context.toJvmType(arrType.element());
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT, WHILEYARRAY, WHILEYINT);
			context.add(new Bytecode.Load(bytecode.operand(0), WHILEYARRAY));
			context.add(new Bytecode.Load(bytecode.operand(1), WHILEYINT));			
			context.add(new Bytecode.Invoke(WHILEYARRAY, "get", ftype, Bytecode.InvokeMode.STATIC));
			context.addReadConversion(arrType.element());			
			context.add(new Bytecode.Store(bytecode.target(0), elementType));
		}		
	}
	private static final class ArrayGenerator implements BytecodeTranslator {
		@Override
		public void translate(Codes.Operator bytecode, Context context) {
			Type elementType = ((Type.Array) bytecode.type(0)).element();			
			JvmType.Function ftype = new JvmType.Function(WHILEYARRAY, JAVA_LANG_OBJECT, WHILEYINT);
			context.add(new Bytecode.Load(bytecode.operand(0), context.toJvmType(elementType)));
			context.addWriteConversion(elementType);		
			context.add(new Bytecode.Load(bytecode.operand(1), WHILEYINT));
			context.add(new Bytecode.Invoke(WHILEYARRAY, "generate", ftype, Bytecode.InvokeMode.STATIC));
			context.add(new Bytecode.Store(bytecode.target(0), WHILEYARRAY));
		}		
	}
	private static final class ArrayConstructor implements BytecodeTranslator {
		@Override
		public void translate(Codes.Operator bytecode, Context context) {
			Type.Array arrType = (Type.Array) bytecode.type(0);
			JvmType elementType = context.toJvmType(arrType.element());
			JvmType.Function initJvmType = new JvmType.Function(T_VOID, T_INT);
			JvmType.Function ftype = new JvmType.Function(WHILEYARRAY, WHILEYARRAY, JAVA_LANG_OBJECT);
			
			context.add(new Bytecode.New(WHILEYARRAY));
			context.add(new Bytecode.Dup(WHILEYARRAY));
			context.add(new Bytecode.LoadConst(bytecode.operands().length));			
			context.add(new Bytecode.Invoke(WHILEYARRAY, "<init>", initJvmType, Bytecode.InvokeMode.SPECIAL));
			
			for (int i = 0; i != bytecode.operands().length; ++i) {
				context.add(new Bytecode.Load(bytecode.operands()[i], elementType));
				context.addWriteConversion(arrType.element());
				context.add(new Bytecode.Invoke(WHILEYARRAY, "internal_add", ftype, Bytecode.InvokeMode.STATIC));
			}

			context.add(new Bytecode.Store(bytecode.target(0), WHILEYARRAY));
		}		
	}
	
	private static final class RecordConstructor implements BytecodeTranslator {
		@Override
		public void translate(Codes.Operator code, Context context) {
			Type.EffectiveRecord recType = (Type.EffectiveRecord) code.type(0); 
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT, JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);
			
			context.construct(WHILEYRECORD);
			
			ArrayList<String> keys = new ArrayList<String>(recType.fields().keySet());
			Collections.sort(keys);
			for (int i = 0; i != code.operands().length; i++) {
				int register = code.operands()[i];
				String key = keys.get(i);
				Type fieldType = recType.field(key);
				context.add(new Bytecode.Dup(WHILEYRECORD));
				context.add(new Bytecode.LoadConst(key));
				context.add(new Bytecode.Load(register, context.toJvmType(fieldType)));
				context.addWriteConversion(fieldType);
				context.add(new Bytecode.Invoke(WHILEYRECORD, "put", ftype, Bytecode.InvokeMode.VIRTUAL));
				context.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
			}

			context.add(new Bytecode.Store(code.target(0), WHILEYRECORD));
		}
	}
}
