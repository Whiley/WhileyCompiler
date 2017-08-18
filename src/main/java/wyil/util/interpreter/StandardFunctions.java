// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyil.util.interpreter;

import wyil.lang.WyilFile;
import static wyil.lang.WyilFile.*;
import wyil.util.interpreter.Interpreter.InternalFunction;
import static wyil.util.interpreter.Interpreter.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import wybs.util.ResolveError;

public class StandardFunctions {
	/**
	 * The standard functions for use with the interpreter.
	 */
	public static final InternalFunction[] standardFunctions = new InternalFunction[255];

	static {
		standardFunctions[WyilFile.EXPR_neg] = new Negate();
		standardFunctions[WyilFile.EXPR_add ] = new Add();
		standardFunctions[WyilFile.EXPR_sub ] = new Subtract();
		standardFunctions[WyilFile.EXPR_mul ] = new Multiply();
		standardFunctions[WyilFile.EXPR_div ] = new Divide();
		standardFunctions[WyilFile.EXPR_rem ] = new Remainder();

		standardFunctions[WyilFile.EXPR_eq ] = new Equal();
		standardFunctions[WyilFile.EXPR_neq ] = new NotEqual();
		standardFunctions[WyilFile.EXPR_lt ] = new LessThan();
		standardFunctions[WyilFile.EXPR_lteq ] = new LessThanEqual();
		standardFunctions[WyilFile.EXPR_gt ] = new GreaterThan();
		standardFunctions[WyilFile.EXPR_gteq ] = new GreaterThanEqual();

		standardFunctions[WyilFile.EXPR_not] = new LogicalNot();

		standardFunctions[WyilFile.EXPR_bitwisenot] = new BitwiseInvert();
		standardFunctions[WyilFile.EXPR_bitwiseor] = new BitwiseOr();
		standardFunctions[WyilFile.EXPR_bitwisexor] = new BitwiseXor();
		standardFunctions[WyilFile.EXPR_bitwiseand] = new BitwiseAnd();
		standardFunctions[WyilFile.EXPR_bitwiseshl] = new LeftShift();
		standardFunctions[WyilFile.EXPR_bitwiseshr] = new RightShift();

		standardFunctions[WyilFile.EXPR_arridx] = new ArrayIndex();
		standardFunctions[WyilFile.EXPR_arrgen] = new ArrayGenerator();
		standardFunctions[WyilFile.EXPR_arrlen] = new ArrayLength();
		standardFunctions[WyilFile.EXPR_arrinit] = new ArrayConstructor();

		standardFunctions[WyilFile.EXPR_new] = new New();
		standardFunctions[WyilFile.EXPR_deref] = new Dereference();
	};

	// ====================================================================================
	// References
	// ====================================================================================

	private static final class Dereference implements InternalFunction {
		@Override
		public RValue apply(RValue[] operands, Interpreter enclosing, Expr.Operator context) {
			RValue.Reference ref = checkType(operands[0], context, RValue.Reference.class);
			return ref.deref();
		}
	}

	private static final class New implements InternalFunction {
		@Override
		public RValue.Reference apply(RValue[] operands, Interpreter enclosing, Expr.Operator context) {
			RValue.Cell cell = RValue.Cell(operands[0]);
			return RValue.Reference(cell);
		}

	}

	// ====================================================================================
	// Logical
	// ====================================================================================

	private static final class LogicalNot implements InternalFunction {
		@Override
		public RValue apply(RValue[] operands, Interpreter enclosing, Expr.Operator context) {
			RValue.Bool i = checkType(operands[0], context, RValue.Bool.class);
			return i.not();
		}
	}

	// ====================================================================================
	// Arithmetic
	// ====================================================================================

	private static final class Negate implements InternalFunction {
		@Override
		public RValue apply(RValue[] operands, Interpreter enclosing, Expr.Operator context) {
			RValue.Int i = checkType(operands[0], context, RValue.Int.class);
			return i.negate();
		}
	}

	private static final class Add implements InternalFunction {
		@Override
		public RValue apply(RValue[] operands, Interpreter enclosing, Expr.Operator context) {
			RValue.Int lhs = checkType(operands[0], context, RValue.Int.class);
			RValue.Int rhs = checkType(operands[1], context, RValue.Int.class);
			return lhs.add(rhs);
		}

	}
	private static final class Subtract implements InternalFunction {
		@Override
		public RValue apply(RValue[] operands, Interpreter enclosing, Expr.Operator context) {
			RValue.Int lhs = checkType(operands[0], context, RValue.Int.class);
			RValue.Int rhs = checkType(operands[1], context, RValue.Int.class);
			return lhs.subtract(rhs);
		}

	}
	private static final class Multiply implements InternalFunction {
		@Override
		public RValue apply(RValue[] operands, Interpreter enclosing, Expr.Operator context) {
			RValue.Int lhs = checkType(operands[0], context, RValue.Int.class);
			RValue.Int rhs = checkType(operands[1], context, RValue.Int.class);
			return lhs.multiply(rhs);
		}
	}
	private static final class Divide implements InternalFunction {
		@Override
		public RValue apply(RValue[] operands, Interpreter enclosing, Expr.Operator context) {
			RValue.Int lhs = checkType(operands[0], context, RValue.Int.class);
			RValue.Int rhs = checkType(operands[1], context, RValue.Int.class);
			return lhs.divide(rhs);
		}

	}
	private static final class Remainder implements InternalFunction {
		@Override
		public RValue apply(RValue[] operands, Interpreter enclosing, Expr.Operator context) {
			RValue.Int lhs = checkType(operands[0], context, RValue.Int.class);
			RValue.Int rhs = checkType(operands[1], context, RValue.Int.class);
			return lhs.remainder(rhs);
		}
	}
	private static final class Equal implements InternalFunction {
		@Override
		public RValue apply(RValue[] operands, Interpreter enclosing, Expr.Operator context) {
			return operands[0].equal(operands[1]);
		}
	}
	private static final class NotEqual implements InternalFunction {
		@Override
		public RValue apply(RValue[] operands, Interpreter enclosing, Expr.Operator context) {
			return operands[0].notEqual(operands[1]);
		}
	}
	private static final class LessThan implements InternalFunction {
		@Override
		public RValue apply(RValue[] operands, Interpreter enclosing, Expr.Operator context) {
			RValue.Int lhs = checkType(operands[0], context, RValue.Int.class);
			RValue.Int rhs = checkType(operands[1], context, RValue.Int.class);
			return lhs.lessThan(rhs);
		}
	}
	private static final class LessThanEqual implements InternalFunction {
		@Override
		public RValue apply(RValue[] operands, Interpreter enclosing, Expr.Operator context) {
			RValue.Int lhs = checkType(operands[0], context, RValue.Int.class);
			RValue.Int rhs = checkType(operands[1], context, RValue.Int.class);
			return lhs.lessThanOrEqual(rhs);
		}
	}
	private static final class GreaterThan implements InternalFunction {
		@Override
		public RValue apply(RValue[] operands, Interpreter enclosing, Expr.Operator context) {
			RValue.Int lhs = checkType(operands[0], context, RValue.Int.class);
			RValue.Int rhs = checkType(operands[1], context, RValue.Int.class);
			return rhs.lessThan(lhs);
		}
	}
	private static final class GreaterThanEqual implements InternalFunction {
		@Override
		public RValue apply(RValue[] operands, Interpreter enclosing, Expr.Operator context) {
			RValue.Int lhs = checkType(operands[0], context, RValue.Int.class);
			RValue.Int rhs = checkType(operands[1], context, RValue.Int.class);
			return rhs.lessThanOrEqual(lhs);
		}
	}
	// ====================================================================================
	// Bytes
	// ====================================================================================


	private static final class BitwiseInvert implements InternalFunction {
		@Override
		public RValue apply(RValue[] operands, Interpreter enclosing, Expr.Operator context) {
			RValue.Byte b = checkType(operands[0], context, RValue.Byte.class);
			return b.invert();
		}
	}

	private static final class BitwiseOr implements InternalFunction {
		@Override
		public RValue apply(RValue[] operands, Interpreter enclosing, Expr.Operator context) {
			RValue.Byte lhs = checkType(operands[0], context, RValue.Byte.class);
			RValue.Byte rhs = checkType(operands[1], context, RValue.Byte.class);
			return lhs.or(rhs);
		}
	}
	private static final class BitwiseXor implements InternalFunction {
		@Override
		public RValue apply(RValue[] operands, Interpreter enclosing, Expr.Operator context) {
			RValue.Byte lhs = checkType(operands[0], context, RValue.Byte.class);
			RValue.Byte rhs = checkType(operands[1], context, RValue.Byte.class);
			return lhs.xor(rhs);
		}
	}
	private static final class BitwiseAnd implements InternalFunction {
		@Override
		public RValue apply(RValue[] operands, Interpreter enclosing, Expr.Operator context) {
			RValue.Byte lhs = checkType(operands[0], context, RValue.Byte.class);
			RValue.Byte rhs = checkType(operands[1], context, RValue.Byte.class);
			return lhs.and(rhs);
		}
	}
	private static final class LeftShift implements InternalFunction {
		@Override
		public RValue apply(RValue[] operands, Interpreter enclosing, Expr.Operator context) {
			RValue.Byte lhs = checkType(operands[0], context, RValue.Byte.class);
			RValue.Int rhs = checkType(operands[1], context, RValue.Int.class);
			return lhs.shl(rhs);
		}
	}
	private static final class RightShift implements InternalFunction {
		@Override
		public RValue apply(RValue[] operands, Interpreter enclosing, Expr.Operator context) {
			RValue.Byte lhs = checkType(operands[0], context, RValue.Byte.class);
			RValue.Int rhs = checkType(operands[1], context, RValue.Int.class);
			return lhs.shr(rhs);
		}
	}

	// ====================================================================================
	// Arrays
	// ====================================================================================
	private static final class ArrayLength implements InternalFunction {
		@Override
		public RValue apply(RValue[] operands, Interpreter enclosing, Expr.Operator context) {
			RValue.Array array = checkType(operands[0], context, RValue.Array.class);
			return array.length();
		}
	}
	private static final class ArrayIndex implements InternalFunction {
		@Override
		public RValue apply(RValue[] operands, Interpreter enclosing, Expr.Operator context) {
			RValue.Array src = checkType(operands[0], context, RValue.Array.class);
			RValue.Int index = checkType(operands[1], context, RValue.Int.class);
			// Ok, get the element at that index
			return src.read(index);
		}
	}
	private static final class ArrayGenerator implements InternalFunction {
		@Override
		public RValue apply(RValue[] operands, Interpreter enclosing, Expr.Operator context) {
			RValue element = operands[0];
			RValue.Int count = checkType(operands[1], context, RValue.Int.class);
			// Check that we have a integer count
			int n = count.intValue();
			RValue[] values = new RValue[n];
			for (int i = 0; i != n; ++i) {
				values[i] = element;
			}
			return RValue.Array(values);
		}
	}
	private static final class ArrayConstructor implements InternalFunction {
		@Override
		public RValue apply(RValue[] operands, Interpreter enclosing, Expr.Operator context) {
			return RValue.Array(operands);
		}
	}
}
