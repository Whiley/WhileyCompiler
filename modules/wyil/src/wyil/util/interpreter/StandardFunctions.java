package wyil.util.interpreter;

import wyil.lang.Bytecode;
import wyil.lang.Bytecode.Expr;
import wyil.lang.Bytecode.Operator;
import wyil.lang.Constant;
import wyil.lang.SyntaxTree;
import wyil.lang.SyntaxTree.Location;
import wyil.lang.Type;
import wyil.util.interpreter.Interpreter.ConstantObject;
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
		standardFunctions[Bytecode.OPCODE_neg] = new Negate();	
		standardFunctions[Bytecode.OPCODE_add ] = new Add();
		standardFunctions[Bytecode.OPCODE_sub ] = new Subtract();
		standardFunctions[Bytecode.OPCODE_mul ] = new Multiply();
		standardFunctions[Bytecode.OPCODE_div ] = new Divide();
		standardFunctions[Bytecode.OPCODE_rem ] = new Remainder();
		
		standardFunctions[Bytecode.OPCODE_eq ] = new Equal();
		standardFunctions[Bytecode.OPCODE_ne ] = new NotEqual();
		standardFunctions[Bytecode.OPCODE_lt ] = new LessThan();
		standardFunctions[Bytecode.OPCODE_le ] = new LessThanEqual();
		standardFunctions[Bytecode.OPCODE_gt ] = new GreaterThan();
		standardFunctions[Bytecode.OPCODE_ge ] = new GreaterThanEqual();
		
		standardFunctions[Bytecode.OPCODE_logicalnot] = new LogicalNot();
		
		standardFunctions[Bytecode.OPCODE_bitwiseinvert] = new BitwiseInvert();
		standardFunctions[Bytecode.OPCODE_bitwiseor] = new BitwiseOr();
		standardFunctions[Bytecode.OPCODE_bitwisexor] = new BitwiseXor();
		standardFunctions[Bytecode.OPCODE_bitwiseand] = new BitwiseAnd();
		standardFunctions[Bytecode.OPCODE_shl] = new LeftShift();
		standardFunctions[Bytecode.OPCODE_shr] = new RightShift();
		
		standardFunctions[Bytecode.OPCODE_arrayindex] = new ArrayIndex();	
		standardFunctions[Bytecode.OPCODE_arraygen] = new ArrayGenerator();
		standardFunctions[Bytecode.OPCODE_arraylength] = new ArrayLength();
		standardFunctions[Bytecode.OPCODE_array] = new ArrayConstructor();
		
		standardFunctions[Bytecode.OPCODE_record] = new RecordConstructor();
		standardFunctions[Bytecode.OPCODE_newobject] = new ObjectConstructor();
		standardFunctions[Bytecode.OPCODE_dereference] = new Dereference();		
		standardFunctions[Bytecode.OPCODE_is] = new Is();
	};

	// ====================================================================================
	// References
	// ====================================================================================

	private static final class Dereference implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			ConstantObject ref = checkType(operands[0], context, ConstantObject.class);
			return ref.read();
		}
	}
	
	private static final class ObjectConstructor implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			return new ConstantObject(operands[0]);
		}
		
	}
	
	// ====================================================================================
	// Logical
	// ====================================================================================
		
	private static final class LogicalNot implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			Constant.Bool i = checkType(operands[0], context, Constant.Bool.class);
			return Constant.Bool(!i.value());
		}		
	}
			
	// ====================================================================================
	// Arithmetic
	// ====================================================================================
		
	private static final class Negate implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			Constant.Integer i = checkType(operands[0], context, Constant.Integer.class);
			return new Constant.Integer(i.value().negate());
		}		
	}
		
	private static final class Add implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			Constant.Integer lhs = checkType(operands[0], context, Constant.Integer.class);
			Constant.Integer rhs = checkType(operands[1], context, Constant.Integer.class);
			return new Constant.Integer(lhs.value().add(rhs.value()));
		}
		
	}
	private static final class Subtract implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			Constant.Integer lhs = checkType(operands[0], context, Constant.Integer.class);
			Constant.Integer rhs = checkType(operands[1], context, Constant.Integer.class);
			return new Constant.Integer(lhs.value().subtract(rhs.value()));
		}
		
	}
	private static final class Multiply implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			Constant.Integer lhs = checkType(operands[0], context, Constant.Integer.class);
			Constant.Integer rhs = checkType(operands[1], context, Constant.Integer.class);
			return new Constant.Integer(lhs.value().multiply(rhs.value()));
		}		
	}
	private static final class Divide implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			Constant.Integer lhs = checkType(operands[0], context, Constant.Integer.class);
			Constant.Integer rhs = checkType(operands[1], context, Constant.Integer.class);
			return new Constant.Integer(lhs.value().divide(rhs.value()));
		}
		
	}
	private static final class Remainder implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			Constant.Integer lhs = checkType(operands[0], context, Constant.Integer.class);
			Constant.Integer rhs = checkType(operands[1], context, Constant.Integer.class);
			return new Constant.Integer(lhs.value().remainder(rhs.value()));
		}
	}

	
	private static final class Equal implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			return Constant.Bool(operands[0].equals(operands[1]));
		}
	}
	private static final class NotEqual implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			return Constant.Bool(!operands[0].equals(operands[1]));
		}
	}
	private static final class LessThan implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			return lessThan(operands[0],operands[1],true,context);
		}
	}
	private static final class LessThanEqual implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			return lessThan(operands[0],operands[1],false,context);
		}
	}
	private static final class GreaterThan implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			return lessThan(operands[1],operands[0],true,context);
		}
	}
	private static final class GreaterThanEqual implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			return lessThan(operands[1],operands[0],false,context);
		}
	}
	// ====================================================================================
	// Bytes
	// ====================================================================================
		

	private static final class BitwiseInvert implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			Constant.Byte b = checkType(operands[0], context, Constant.Byte.class);
			return new Constant.Byte((byte) ~b.value());
		}		
	}
	
	private static final class BitwiseOr implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			Constant.Byte lhs = checkType(operands[0], context, Constant.Byte.class);
			Constant.Byte rhs = checkType(operands[1], context, Constant.Byte.class);
			int result = lhs.value() | rhs.value();
			return new Constant.Byte((byte) result);
		}		
	}
	private static final class BitwiseXor implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			Constant.Byte lhs = checkType(operands[0], context, Constant.Byte.class);
			Constant.Byte rhs = checkType(operands[1], context, Constant.Byte.class);
			int result = lhs.value() ^ rhs.value();
			return new Constant.Byte((byte) result);
		}		
	}
	private static final class BitwiseAnd implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			Constant.Byte lhs = checkType(operands[0], context, Constant.Byte.class);
			Constant.Byte rhs = checkType(operands[1], context, Constant.Byte.class);
			int result = lhs.value() & rhs.value();
			return new Constant.Byte((byte) result);
		}		
	}
	private static final class LeftShift implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			Constant.Byte lhs = checkType(operands[0], context, Constant.Byte.class);
			Constant.Integer rhs = checkType(operands[1], context, Constant.Integer.class);
			int result = lhs.value() << rhs.value().intValue();
			return new Constant.Byte((byte) result);
		}		
	}
	private static final class RightShift implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			Constant.Byte lhs = checkType(operands[0], context, Constant.Byte.class);
			Constant.Integer rhs = checkType(operands[1], context, Constant.Integer.class);
			int result = lhs.value() >> rhs.value().intValue();
			return new Constant.Byte((byte) result);
		}		
	}
	
	// ====================================================================================
	// Arrays
	// ====================================================================================
	private static final class ArrayLength implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			Constant.Array array = checkType(operands[0], context, Constant.Array.class);
			BigInteger length = BigInteger.valueOf(array.values().size());
			return new Constant.Integer(length);
		}		
	}	
	private static final class ArrayIndex implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			Constant.Array src = checkType(operands[0], context, Constant.Array.class);
			Constant.Integer index = checkType(operands[1], context, Constant.Integer.class);
			int i = index.value().intValue();
			if (i < 0 || i >= src.values().size()) {
				error("index-out-of-bounds", context);
			}
			// Ok, get the element at that index
			return src.values().get(index.value().intValue());
		}		
	}
	private static final class ArrayGenerator implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			Constant element = operands[0];
			Constant.Integer count = checkType(operands[1], context, Constant.Integer.class);
			// Check that we have a integer count
			int n = count.value().intValue();
			ArrayList<Constant> values = new ArrayList<Constant>();
			for (int i = 0; i != n; ++i) {
				values.add(element);
			}
			return new Constant.Array(values);
		}		
	}
	private static final class ArrayConstructor implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			ArrayList<Constant> values = new ArrayList<Constant>();
			for (Constant c : operands) {
				values.add(c);
			}
			return new Constant.Array(values);
		}		
	}
	
	// ====================================================================================
	// Records
	// ====================================================================================
	private static final class RecordConstructor implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			Type.EffectiveRecord type = (Type.EffectiveRecord) context.getType();
			HashMap<String, Constant> values = new HashMap<String, Constant>();
			ArrayList<String> fields = new ArrayList<String>(type.fields().keySet());
			Collections.sort(fields);
			for (int i = 0; i != operands.length; ++i) {
				values.put(fields.get(i), operands[i]);
			}
			return new Constant.Record(values);
		}
	}	
	
	// ====================================================================================
	// Other
	// ====================================================================================
		
	private static final class Is implements InternalFunction {
		@Override
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context) {
			Constant.Type ct = checkType(operands[1], context, Constant.Type.class);			
			boolean r = enclosing.isMemberOfType(operands[0], ct.value(), context);			
			return Constant.Bool(r);
		}
	}

	// ====================================================================================
	// Helpers
	// ====================================================================================

	
	private static Constant.Bool lessThan(Constant lhs, Constant rhs, boolean isStrict, Location<Operator> context) {
		checkType(lhs, context, Constant.Integer.class);
		checkType(rhs, context, Constant.Integer.class);
		Constant.Integer lhs_i = (Constant.Integer) lhs;
		Constant.Integer rhs_i = (Constant.Integer) rhs;
		int result = lhs_i.compareTo(rhs_i);
		// In the strict case, the lhs must be strictly below the rhs. In the
		// non-strict case, they can be equal.
		if (isStrict) {
			return Constant.Bool(result < 0);
		} else {
			return Constant.Bool(result <= 0);
		}
	}
	

}
