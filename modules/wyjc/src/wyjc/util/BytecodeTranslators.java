package wyjc.util;

import static jasm.lang.JvmTypes.*;
import static wyjc.Wyil2JavaBuilder.*;
import static wyil.lang.Bytecode.*;

import java.util.ArrayList;
import java.util.Collections;

import jasm.lang.Bytecode;
import jasm.lang.JvmType;
import wycc.util.ResolveError;
import wyil.lang.SyntaxTree;
import wyil.lang.SyntaxTree.Location;
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
		standardFunctions[OPCODE_neg] = new Negate();
		standardFunctions[OPCODE_add ] = new Add();
		standardFunctions[OPCODE_sub ] = new Subtract();
		standardFunctions[OPCODE_mul ] = new Multiply();
		standardFunctions[OPCODE_div ] = new Divide();
		standardFunctions[OPCODE_rem ] = new Remainder();
		standardFunctions[OPCODE_eq ] = new Equal();
		standardFunctions[OPCODE_ne ] = new NotEqual();
		standardFunctions[OPCODE_lt ] = new LessThan();
		standardFunctions[OPCODE_le ] = new LessThanEqual();
		standardFunctions[OPCODE_gt ] = new GreaterThan();
		standardFunctions[OPCODE_ge ] = new GreaterThanEqual();
		
		standardFunctions[OPCODE_logicalnot] = new LogicalNot();		
		standardFunctions[OPCODE_logicalor] = new LogicalOr();
		standardFunctions[OPCODE_logicaland] = new LogicalAnd();
		
		standardFunctions[OPCODE_bitwiseinvert] = new Invert();			
		standardFunctions[OPCODE_bitwiseor] = new BitwiseOr();
		standardFunctions[OPCODE_bitwisexor] = new BitwiseXor();
		standardFunctions[OPCODE_bitwiseand] = new BitwiseAnd();
		standardFunctions[OPCODE_shl] = new LeftShift();
		standardFunctions[OPCODE_shr] = new RightShift();
		
		standardFunctions[OPCODE_arraylength] = new ArrayLength();			
		standardFunctions[OPCODE_arrayindex] = new ArrayIndex();	
		standardFunctions[OPCODE_arraygen] = new ArrayGenerator();
		
		standardFunctions[OPCODE_dereference] = new Dereference();		
		standardFunctions[OPCODE_newobject] = new New();
		standardFunctions[OPCODE_is] = new Is();	
	};
		
	// ====================================================================================
	// References
	// ====================================================================================

	private static final class Dereference implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) throws ResolveError {
			Location<?> srcOperand = expr.getOperand(0);
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT);
			context.add(new Bytecode.Invoke(WHILEYOBJECT, "state", ftype, Bytecode.InvokeMode.VIRTUAL));
			// finally, we need to cast the object we got back appropriately.
			Type.Reference pt = context.expandAsReference(srcOperand.getType());
			context.addReadConversion(pt.element());
		}		
	}
	
	private static final class New implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) throws ResolveError {
			Type.Reference refType = context.expandAsReference(expr.getType()); 
			JvmType elementType = context.toJvmType(refType.element());
			JvmType.Function ftype = new JvmType.Function(T_VOID, JAVA_LANG_OBJECT);
			context.add(new Bytecode.New(WHILEYOBJECT));
			context.add(new Bytecode.DupX1());
			context.add(new Bytecode.Swap());
			context.addWriteConversion(refType.element());
			context.add(new Bytecode.Invoke(WHILEYOBJECT, "<init>", ftype, Bytecode.InvokeMode.SPECIAL));
		}		
	}
	// ====================================================================================
	// Logical
	// ====================================================================================

	private static final class LogicalNot implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) {
			JvmType.Clazz type = (JvmType.Clazz) context.toJvmType(expr.getType());
			JvmType.Function ftype = new JvmType.Function(type);
			context.add(new Bytecode.Invoke(type, "not", ftype, Bytecode.InvokeMode.VIRTUAL));
		}		
	}
	private static final class LogicalOr implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) {
			JvmType.Clazz type = (JvmType.Clazz)  context.toJvmType(expr.getType());
			JvmType.Function ftype = new JvmType.Function(type, type);
			context.add(new Bytecode.Invoke(WHILEYBOOL, "or", ftype, Bytecode.InvokeMode.VIRTUAL));
		}		
	}
	private static final class LogicalAnd implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) {
			JvmType.Clazz type = (JvmType.Clazz)  context.toJvmType(expr.getType());
			JvmType.Function ftype = new JvmType.Function(type, type);
			context.add(new Bytecode.Invoke(WHILEYBOOL, "and", ftype, Bytecode.InvokeMode.VIRTUAL));
		}		
	}
	// ====================================================================================
	// Arithmetic
	// ====================================================================================
	
	private static final class Negate implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) {
			JvmType.Clazz type = (JvmType.Clazz) context.toJvmType(expr.getType());
			JvmType.Function ftype = new JvmType.Function(type);
			context.add(new Bytecode.Invoke(type, "negate", ftype, Bytecode.InvokeMode.VIRTUAL));
		}		
	}
	private static final class Add implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) {
			JvmType.Clazz type = (JvmType.Clazz)  context.toJvmType(expr.getType());
			JvmType.Function ftype = new JvmType.Function(type, type);
			context.add(new Bytecode.Invoke((JvmType.Clazz) type, "add", ftype, Bytecode.InvokeMode.VIRTUAL));
		}		
	}
	private static final class Subtract implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) {
			JvmType.Clazz type = (JvmType.Clazz)  context.toJvmType(expr.getType());
			JvmType.Function ftype = new JvmType.Function(type, type);
			context.add(new Bytecode.Invoke((JvmType.Clazz) type, "subtract", ftype, Bytecode.InvokeMode.VIRTUAL));
		}		
	}
	private static final class Multiply implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) {
			JvmType.Clazz type = (JvmType.Clazz)  context.toJvmType(expr.getType());
			JvmType.Function ftype = new JvmType.Function(type, type);
			context.add(new Bytecode.Invoke((JvmType.Clazz) type, "multiply", ftype, Bytecode.InvokeMode.VIRTUAL));
		}		
	}
	private static final class Divide implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) {
			JvmType.Clazz type = (JvmType.Clazz)  context.toJvmType(expr.getType());
			JvmType.Function ftype = new JvmType.Function(type, type);
			context.add(new Bytecode.Invoke((JvmType.Clazz) type, "divide", ftype, Bytecode.InvokeMode.VIRTUAL));
		}		
	}
	private static final class Remainder implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) {
			JvmType.Clazz type = (JvmType.Clazz)  context.toJvmType(expr.getType());
			JvmType.Function ftype = new JvmType.Function(type, type);
			context.add(new Bytecode.Invoke((JvmType.Clazz) type, "remainder", ftype, Bytecode.InvokeMode.VIRTUAL));
		}		
	}

	private static final class Equal implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) {
			JvmType.Function ftype = new JvmType.Function(WHILEYBOOL, JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);
			context.add(new Bytecode.Invoke(WHILEYUTIL, "equal", ftype, Bytecode.InvokeMode.STATIC));
		}		
	}
	private static final class NotEqual implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) {
			JvmType.Function ftype = new JvmType.Function(WHILEYBOOL, JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);
			context.add(new Bytecode.Invoke(WHILEYUTIL, "notEqual", ftype, Bytecode.InvokeMode.STATIC));
		}		
	}
	private static final class LessThan implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) {
			JvmType.Function ftype = new JvmType.Function(WHILEYBOOL, WHILEYINT, WHILEYINT);
			context.add(new Bytecode.Invoke(WHILEYUTIL, "lessThan", ftype, Bytecode.InvokeMode.STATIC));
		}		
	}
	private static final class LessThanEqual implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) {
			JvmType.Function ftype = new JvmType.Function(WHILEYBOOL, WHILEYINT, WHILEYINT);
			context.add(new Bytecode.Invoke(WHILEYUTIL, "lessThanEqual", ftype, Bytecode.InvokeMode.STATIC));
		}		
	}
	private static final class GreaterThan implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) {
			JvmType.Function ftype = new JvmType.Function(WHILEYBOOL, WHILEYINT, WHILEYINT);
			context.add(new Bytecode.Invoke(WHILEYUTIL, "greaterThan", ftype, Bytecode.InvokeMode.STATIC));
		}		
	}
	private static final class GreaterThanEqual implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) {
			JvmType.Function ftype = new JvmType.Function(WHILEYBOOL, WHILEYINT, WHILEYINT);
			context.add(new Bytecode.Invoke(WHILEYUTIL, "greaterThanEqual", ftype, Bytecode.InvokeMode.STATIC));
		}		
	}
	// ====================================================================================
	// Bytes
	// ====================================================================================
	private static final class Invert implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) {
			JvmType.Clazz type = (JvmType.Clazz)  context.toJvmType(expr.getType());
			JvmType.Function ftype = new JvmType.Function(type);
			context.add(new Bytecode.Invoke(WHILEYBYTE, "compliment", ftype, Bytecode.InvokeMode.VIRTUAL));
		}		
	}
	private static final class BitwiseOr implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) {
			JvmType.Clazz type = (JvmType.Clazz)  context.toJvmType(expr.getType());
			JvmType.Function ftype = new JvmType.Function(type, type);
			context.add(new Bytecode.Invoke(WHILEYBYTE, "or", ftype, Bytecode.InvokeMode.VIRTUAL));
		}		
	}
	private static final class BitwiseXor implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) {
			JvmType.Clazz type = (JvmType.Clazz)  context.toJvmType(expr.getType());
			JvmType.Function ftype = new JvmType.Function(type, type);
			context.add(new Bytecode.Invoke(WHILEYBYTE, "xor", ftype, Bytecode.InvokeMode.VIRTUAL));
		}		
	}
	private static final class BitwiseAnd implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) {
			JvmType.Clazz type = (JvmType.Clazz)  context.toJvmType(expr.getType());
			JvmType.Function ftype = new JvmType.Function(type, type);
			context.add(new Bytecode.Invoke(WHILEYBYTE, "and", ftype, Bytecode.InvokeMode.VIRTUAL));
		}		
	}
	private static final class LeftShift implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) {
			JvmType.Clazz type = (JvmType.Clazz)  context.toJvmType(expr.getType());
			JvmType.Function ftype = new JvmType.Function(type, WHILEYINT);
			context.add(new Bytecode.Invoke(WHILEYBYTE, "leftShift", ftype, Bytecode.InvokeMode.VIRTUAL));
		}		
	}
	private static final class RightShift implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) {
			JvmType.Clazz type = (JvmType.Clazz)  context.toJvmType(expr.getType());
			JvmType.Function ftype = new JvmType.Function(type, WHILEYINT);
			context.add(new Bytecode.Invoke(WHILEYBYTE, "rightShift", ftype, Bytecode.InvokeMode.VIRTUAL));
		}		
	}
	// ====================================================================================
	// Arrays
	// ====================================================================================
	private static final class ArrayLength implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) {
			JvmType.Function ftype = new JvmType.Function(WHILEYINT);	
			context.add(new Bytecode.Invoke(WHILEYARRAY, "length", ftype, Bytecode.InvokeMode.VIRTUAL));
		}		
	}
	private static final class ArrayIndex implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) throws ResolveError {
			Location<?> srcOperand = expr.getOperand(0);
			Type.EffectiveArray arrType = context.expandAsEffectiveArray(srcOperand.getType());
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT, WHILEYARRAY, WHILEYINT);	
			context.add(new Bytecode.Invoke(WHILEYARRAY, "get", ftype, Bytecode.InvokeMode.STATIC));
			context.addReadConversion(arrType.element());			
		}		
	}
	private static final class ArrayGenerator implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) throws ResolveError {
			Type.EffectiveArray sourceType = context.expandAsEffectiveArray(expr.getType());
			Type elementType = sourceType.element();			
			JvmType.Function ftype = new JvmType.Function(WHILEYARRAY, WHILEYINT, JAVA_LANG_OBJECT);
			context.add(new Bytecode.Swap());
			context.addWriteConversion(elementType);
			context.add(new Bytecode.Invoke(WHILEYARRAY, "generate", ftype, Bytecode.InvokeMode.STATIC));
		}		
	}	
	
	// ====================================================================================
	// Other
	// ====================================================================================

	private static final class Is implements BytecodeTranslator {
		@Override
		public void translate(Location<Operator> expr, Context context) {
			JvmType.Function ftype = new JvmType.Function(WHILEYBOOL, JAVA_LANG_OBJECT);
			context.add(new Bytecode.Swap());
			context.add(new Bytecode.Invoke(WHILEYTYPE, "is", ftype, Bytecode.InvokeMode.VIRTUAL));		
		}		
	}
	
}
