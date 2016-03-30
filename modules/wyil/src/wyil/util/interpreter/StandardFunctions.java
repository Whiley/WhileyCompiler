package wyil.util.interpreter;

import wyil.lang.Bytecode;
import wyil.lang.Constant;
import wyil.lang.Type;
import wyil.util.interpreter.Interpreter.ConstantObject;
import wyil.util.interpreter.Interpreter.Context;
import wyil.util.interpreter.Interpreter.InternalFunction;
import static wyil.util.interpreter.Interpreter.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class StandardFunctions {
	/**
	 * The standard functions for use with the interpreter.
	 */
	public static final InternalFunction[] standardFunctions = new InternalFunction[255];
	
	static {
		standardFunctions[Bytecode.OPCODE_assign] = new Assign();
		standardFunctions[Bytecode.OPCODE_neg] = new Negate();
		standardFunctions[Bytecode.OPCODE_arrayinvert] = new BitwiseInvert();	
		standardFunctions[Bytecode.OPCODE_dereference] = new Dereference();
		standardFunctions[Bytecode.OPCODE_arraylength] = new ArrayLength();	
		standardFunctions[Bytecode.OPCODE_add ] = new Add();
		standardFunctions[Bytecode.OPCODE_sub ] = new Subtract();
		standardFunctions[Bytecode.OPCODE_mul ] = new Multiply();
		standardFunctions[Bytecode.OPCODE_div ] = new Divide();
		standardFunctions[Bytecode.OPCODE_rem ] = new Remainder();
		standardFunctions[Bytecode.OPCODE_bitwiseor] = new BitwiseOr();
		standardFunctions[Bytecode.OPCODE_bitwisexor] = new BitwiseXor();
		standardFunctions[Bytecode.OPCODE_bitwiseand] = new BitwiseAnd();
		standardFunctions[Bytecode.OPCODE_lshr] = new LeftShift();
		standardFunctions[Bytecode.OPCODE_rshr] = new RightShift();
		standardFunctions[Bytecode.OPCODE_arrayindex] = new ArrayIndex();	
		standardFunctions[Bytecode.OPCODE_arrygen] = new ArrayGenerator();
		standardFunctions[Bytecode.OPCODE_array] = new ArrayConstructor();
		standardFunctions[Bytecode.OPCODE_record] = new RecordConstructor();
		standardFunctions[Bytecode.OPCODE_newobject] = new ObjectConstructor();
	};

	// ====================================================================================
	// General
	// ====================================================================================

	private static final class Assign implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Context context) {			
			return operands[0];
		}		
	}
	
	// ====================================================================================
	// References
	// ====================================================================================

	private static final class Dereference implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Context context) {
			ConstantObject ref = checkType(operands[0], context, ConstantObject.class);
			return ref.read();
		}
	}
	
	private static final class ObjectConstructor implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Context context) {
			return new ConstantObject(operands[0]);
		}
		
	}
	
	// ====================================================================================
	// Arithmetic
	// ====================================================================================
		
	private static final class Negate implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Context context) {
			Constant.Integer i = checkType(operands[0], context, Constant.Integer.class);
			return i.negate();
		}		
	}
	
	private static final class Add implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Context context) {
			Constant.Integer lhs = checkType(operands[0], context, Constant.Integer.class);
			Constant.Integer rhs = checkType(operands[1], context, Constant.Integer.class);
			return lhs.add(rhs);
		}
		
	}
	private static final class Subtract implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Context context) {
			Constant.Integer lhs = checkType(operands[0], context, Constant.Integer.class);
			Constant.Integer rhs = checkType(operands[1], context, Constant.Integer.class);
			return lhs.subtract(rhs);
		}
		
	}
	private static final class Multiply implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Context context) {
			Constant.Integer lhs = checkType(operands[0], context, Constant.Integer.class);
			Constant.Integer rhs = checkType(operands[1], context, Constant.Integer.class);
			return lhs.multiply(rhs);
		}		
	}
	private static final class Divide implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Context context) {
			Constant.Integer lhs = checkType(operands[0], context, Constant.Integer.class);
			Constant.Integer rhs = checkType(operands[1], context, Constant.Integer.class);
			return lhs.divide(rhs);
		}
		
	}
	private static final class Remainder implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Context context) {
			Constant.Integer lhs = checkType(operands[0], context, Constant.Integer.class);
			Constant.Integer rhs = checkType(operands[1], context, Constant.Integer.class);
			return lhs.remainder(rhs);
		}
	}
	
	// ====================================================================================
	// Bytes
	// ====================================================================================
		

	private static final class BitwiseInvert implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Context context) {
			Constant.Byte b = checkType(operands[0], context, Constant.Byte.class);
			return Constant.V_BYTE((byte) ~b.value);
		}		
	}
	
	private static final class BitwiseOr implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Context context) {
			Constant.Byte lhs = checkType(operands[0], context, Constant.Byte.class);
			Constant.Byte rhs = checkType(operands[1], context, Constant.Byte.class);
			int result = lhs.value | rhs.value;
			return Constant.V_BYTE((byte) result);
		}		
	}
	private static final class BitwiseXor implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Context context) {
			Constant.Byte lhs = checkType(operands[0], context, Constant.Byte.class);
			Constant.Byte rhs = checkType(operands[1], context, Constant.Byte.class);
			int result = lhs.value ^ rhs.value;
			return Constant.V_BYTE((byte) result);
		}		
	}
	private static final class BitwiseAnd implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Context context) {
			Constant.Byte lhs = checkType(operands[0], context, Constant.Byte.class);
			Constant.Byte rhs = checkType(operands[1], context, Constant.Byte.class);
			int result = lhs.value & rhs.value;
			return Constant.V_BYTE((byte) result);
		}		
	}
	private static final class LeftShift implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Context context) {
			Constant.Byte lhs = checkType(operands[0], context, Constant.Byte.class);
			Constant.Integer rhs = checkType(operands[1], context, Constant.Integer.class);
			int result = lhs.value << rhs.value.intValue();
			return Constant.V_BYTE((byte) result);
		}		
	}
	private static final class RightShift implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Context context) {
			Constant.Byte lhs = checkType(operands[0], context, Constant.Byte.class);
			Constant.Integer rhs = checkType(operands[1], context, Constant.Integer.class);
			int result = lhs.value >> rhs.value.intValue();
			return Constant.V_BYTE((byte) result);
		}		
	}
	
	// ====================================================================================
	// Arrays
	// ====================================================================================
	private static final class ArrayLength implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Context context) {
			Constant.Array array = checkType(operands[0], context, Constant.Array.class);
			BigInteger length = BigInteger.valueOf(array.values.size());
			return Constant.V_INTEGER(length);
		}		
	}	
	private static final class ArrayIndex implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Context context) {
			Constant.Array src = checkType(operands[0], context, Constant.Array.class);
			Constant.Integer index = checkType(operands[1], context, Constant.Integer.class);
			int i = index.value.intValue();
			if (i < 0 || i >= src.values.size()) {
				error("index-out-of-bounds", context);
			}
			// Ok, get the element at that index
			return src.values.get(index.value.intValue());
		}		
	}
	private static final class ArrayGenerator implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Context context) {
			Constant element = operands[0];
			Constant.Integer count = checkType(operands[1], context, Constant.Integer.class);
			// Check that we have a integer count
			int n = count.value.intValue();
			ArrayList<Constant> values = new ArrayList<Constant>();
			for (int i = 0; i != n; ++i) {
				values.add(element);
			}
			return Constant.V_ARRAY(values);
		}		
	}
	private static final class ArrayConstructor implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Context context) {
			ArrayList<Constant> values = new ArrayList<Constant>();
			for (Constant operand : operands) {
				values.add(operand);
			}
			return Constant.V_ARRAY(values);
		}		
	}
	
	// ====================================================================================
	// Records
	// ====================================================================================
	private static final class RecordConstructor implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Context context) {
			Bytecode.Operator bytecode = (Bytecode.Operator) context.getBytecode();
			Type.EffectiveRecord rType = (Type.EffectiveRecord) bytecode.type(0); 
			HashMap<String, Constant> values = new HashMap<String, Constant>();
			ArrayList<String> fields = new ArrayList<String>(rType.fields().keySet());
			Collections.sort(fields);
			for (int i = 0; i != operands.length; ++i) {
				values.put(fields.get(i), operands[i]);
			}
			return Constant.V_RECORD(values);
		}		
	}	
}
