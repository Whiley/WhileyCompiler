// Copyright (c) 2011, David J. Pearce (David J. Pearce@ecs.vuw.ac.nz)
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

package wyil.lang;

import java.util.*;

import wycc.lang.NameID;
import wycc.util.Pair;

/**
 * Represents a WyIL bytecode. The Whiley Intermediate Language (WyIL) employs
 * register-based bytecodes (as opposed to e.g. the Java Virtual Machine, which
 * uses stack-based bytecodes). During execution, one can think of the "machine"
 * as maintaining a call stack made up of "frames". For each function or method
 * on the call stack, the corresponding frame consists of zero or more
 * <i>registers</i>. Bytecodes may read/write values from registers. Like the
 * Java Virtual Machine, WyIL uses unstructured control-flow. However, unlike
 * the JVM, it also allows variables to be automatically retyped by runtime type
 * tests. The following illustrates:
 *
 * <pre>
 * function sum(int[] data) -> int:
 *    int r = 0
 *    for item in data:
 *       r = r + item
 *    return r
 * </pre>
 *
 * This function is compiled into the following WyIL bytecode:
 *
 * <pre>
 * function sum(int[] data) -> int:
 * body:
 *   const %1 = 0          : int
 *   assign %2 = %0        : [int]
 *   forall %3 in %2 ()    : [int]
 *       assign %4 = %1    : int
 *       add %1 = %4, %3   : int
 *   return %1             : int
 * </pre>
 *
 * <p>
 * Here, we can see that every bytecode is associated with one (or more) types.
 * These types are inferred by the compiler during type propagation.
 * </p>
 *
 * <p>
 * Each bytecode has a binary format which identifies the <i>opcode</i>,
 * <i>target registers</i>, <i>operand registers</i> <i>types</i> and <i>other
 * items</i> used (e.g. names, constants, etc). The generic organisation of a
 * bytecode is as follows:
 * </p>
 *
 * <pre>
 * +--------+---------+----------+-------+-------------+
 * | opcode | targets | operands | types | other items |
 * +--------+---------+----------+-------+-------------+
 * </pre>
 * <p>
 * The opcode is currently always 1 byte, whilst the remainder varies between
 * instructions. The opcode itself splits into two components:
 * </p>
 * 
 * <pre>
 *  7   6 5         0
 * +-----+-----------+
 * | fmt | operation |
 * +-----+-----------+
 * </pre>
 * <p>
 * Here, <i>operation</i> identifies the bytecode operation (e.g. add, invoke,
 * etc), whilst <i>fmt</i> identifies the bytecode format. Different formats are
 * used to specify common bytecode layouts:
 * </p>
 * <pre>
 * fmt | constaints
 * ----+-----------
 *  00 | none
 *  01 | zero targets
 *  10 | one target
 *  11 | unused
 * </pre>
 * 
 * @author David J. Pearce
 */
public abstract class Bytecode {
	protected final Type[] types;
	private final int[] targets;
	protected final int[] operands;		
	
	public Bytecode(Type type, int target, int... operands) {			
		this.types = new Type[]{type};
		this.targets = new int[] {target};
		this.operands = operands;
	}
	
	public Bytecode(Type[] types, int[] targets, int... operands) {			
		this.types = types;
		this.targets = targets;
		this.operands = operands;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(types) + Arrays.hashCode(targets()) + Arrays.hashCode(operands());
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Bytecode) {
			Bytecode bo = (Bytecode) o;
			return Arrays.equals(targets(), bo.targets()) && Arrays.equals(operands(), bo.operands())
					&& Arrays.equals(types, bo.types);
		}
		return false;
	}

	public Type[] types() {
		return types;
	}
	
	public Type type(int i) {
		return (Type) types[i];
	}

	/**
	 * Return a specific target register assigned by this bytecode.
	 *
	 * @return
	 */
	public int target(int i) {
		return targets[i];
	}
	
	/**
	 * Return the target registers assigned by this bytecode.
	 *
	 * @return
	 */
	public int[] targets() {
		return targets;
	}		
	
	/**
	 * Return the operand registers assigned by this bytecode.
	 *
	 * @return
	 */
	public int[] operands() {
		return operands;
	}

	/**
	 * Return the ith operand read by this bytecode.
	 * @param i
	 * @return
	 */
	public int operand(int i) {
		return operands[i];
	}	

	/**
	 * Return the opcode value of this bytecode.
	 * @return
	 */
	public abstract int opcode();
	
	/**
	 * A compound bytecode represents a bytecode that contains sequence of zero
	 * or more bytecodes. For example, the loop bytecode contains its loop body.
	 * The nested block of bytecodes is represented as a block identifier in the
	 * enclosing forest.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static abstract class Compound extends Bytecode {
		protected final int block;

		public Compound(int block, Type[] types, int[] targets, int... operands) {
			super(types, targets, operands);
			this.block = block;
		}

		public int block() {
			return block;
		}
		
		public int hashCode() {
			return super.hashCode() ^ block;
		}
		
		public boolean equals(Object o) {
			if(o instanceof Compound) {
				Compound abc = (Compound) o;
				return block == abc.block && super.equals(o);
			}
			return false;
		}
	}
	
	/**
	 * A compound bytecode represents a bytecode that contains sequence of zero
	 * or more bytecodes. For example, the loop bytecode contains its loop body.
	 * The nested block of bytecodes is represented as a block identifier in the
	 * enclosing forest.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static abstract class Branching extends Bytecode {
		protected final String destination;

		public Branching(String destination, Type[] types, int[] targets, int... operands) {
			super(types, targets, operands);
			this.destination = destination;
		}

		public String destination() {
			return destination;
		}
		
		public int hashCode() {
			return super.hashCode() ^ destination.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof Branching) {
				Branching abc = (Branching) o;
				return destination.equals(abc.destination) && super.equals(o);
			}
			return false;
		}
	}


	// ===============================================================
	// Bytecode Implementations
	// ===============================================================

	/**
	 * Represents a binary operator (e.g. '+','-',etc) that is provided to a
	 * <code>BinOp</code> bytecode.
	 *
	 * @author David J. Pearce
	 *
	 */
	public enum OperatorKind {
		// Unary
		NEG(0) {
			public String toString() {
				return "neg";
			}
		},
		NOT(1) {
			public String toString() {
				return "not";
			}
		},
		BITWISEINVERT(2) {
			public String toString() {
				return "invert";
			}
		},
		DEREFERENCE(3) {
			public String toString() {
				return "deref";
			}
		},
		ARRAYLENGTH(4) {
			public String toString() {
				return "length";
			}
		},
		// Binary
		ADD(5) {
			public String toString() {
				return "add";
			}
		},
		SUB(6) {
			public String toString() {
				return "sub";
			}
		},
		MUL(7) {
			public String toString() {
				return "mul";
			}
		},
		DIV(8) {
			public String toString() {
				return "div";
			}
		},
		REM(9) {
			public String toString() {
				return "rem";
			}
		},
		EQ(10) {
			public String toString() {
				return "eq";
			}
		},
		NEQ(11) {
			public String toString() {
				return "ne";
			}
		},
		LT(12) {
			public String toString() {
				return "lt";
			}
		},
		LTEQ(13) {
			public String toString() {
				return "le";
			}
		},
		GT(14) {
			public String toString() {
				return "gt";
			}
		},
		GTEQ(15) {
			public String toString() {
				return "ge";
			}
		},
		BITWISEOR(16) {
			public String toString() {
				return "or";
			}
		},
		BITWISEXOR(17) {
			public String toString() {
				return "xor";
			}
		},
		BITWISEAND(18) {
			public String toString() {
				return "and";
			}
		},
		LEFTSHIFT(19) {
			public String toString() {
				return "shl";
			}
		},
		RIGHTSHIFT(20) {
			public String toString() {
				return "shr";
			}
		},
		ARRAYINDEX(21) {
			public String toString() {
				return "indexof";
			}
		},
		ARRAYGENERATOR(22) {
			public String toString() {
				return "arraygen";
			}
		},
		ARRAYCONSTRUCTOR(23) {
			public String toString() {
				return "array";
			}
		},
		RECORDCONSTRUCTOR(24) {
			public String toString() {
				return "record";
			}
		},
		NEW(25) {
			public String toString() {
				return "new";
			}
		},
		ASSIGN(26) {
			public String toString() {
				return "assign";
			}
		};
		public int offset;

		private OperatorKind(int offset) {
			this.offset = offset;
		}
	};

	/**
	 * <p>
	 * A binary operation which reads two numeric values from the operand
	 * registers, performs an operation on them and writes the result to the
	 * target register. The binary operators are:
	 * </p>
	 * <ul>
	 * <li><i>add, subtract, multiply, divide, remainder</i>. Both operands must
	 * be either integers or reals (but not one or the other). A value of the
	 * same type is produced.</li>
	 * <li><i>bitwiseor, bitwisexor, bitwiseand</i></li>
	 * <li><i>leftshift,rightshift</i></li>
	 * </ul>
	 * For example, the following Whiley code:
	 *
	 * <pre>
	 * function f(int x, int y) -> int:
	 *     return ((x * y) + 1) / 2
	 * </pre>
	 *
	 * can be translated into the following WyIL code:
	 *
	 * <pre>
	 * function f(int x, int y) -> int:
	 * body:
	 *     mul %2 = %0, %1   : int
	 *     const %3 = 1      : int
	 *     add %2 = %2, %3   : int
	 *     const %3 = 2      : int
	 *     const %4 = 0      : int
	 *     assertne %3, %4 "division by zero" : int
	 *     div %2 = %2, %3   : int
	 *     return %2         : int
	 * </pre>
	 *
	 * Here, the <code>assertne</code> bytecode has been included to check
	 * against division-by-zero. In this particular case the assertion is known
	 * true at compile time and, in practice, would be compiled away.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Operator extends Bytecode {
		private final OperatorKind kind;

		public Operator(Type type, int[] targets, int[] operands, OperatorKind bop) {
			super(new Type[] { type }, targets, operands);
			if (bop == null) {
				throw new IllegalArgumentException("Operator kind cannot be null");
			}
			this.kind = bop;
		}

		@Override
		public int opcode() {
			return OPCODE_neg + kind().offset;
		}

		public int hashCode() {
			return kind.hashCode() + super.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof Operator) {
				Operator bo = (Operator) o;
				return kind.equals(bo.kind) && super.equals(bo);
			}
			return false;
		}

		public String toString() {
			return kind() + " %" + target(0) + " = " + arrayToString(operands()) + " : " + type(0);
		}

		public OperatorKind kind() {
			return kind;
		}
	}

	/**
	 * Reads a value from the operand register, converts it to a given type and
	 * writes the result to the target register. This bytecode is the only way
	 * to change the type of a value. It's purpose is to simplify
	 * implementations which have different representations of data types. A
	 * convert bytecode must be inserted whenever the type of a register
	 * changes. This includes at control-flow meet points, when the value is
	 * passed as a parameter, assigned to a field, etc. For example, the
	 * following Whiley code:
	 *
	 * <pre>
	 * function f(int x) -> real:
	 *     return x + 1
	 * </pre>
	 *
	 * can be translated into the following WyIL code:
	 *
	 * <pre>
	 * function f(int x) -> real:
	 * body:
	 *     const %2 = 1           : int
	 *     add %1 = %0, %2        : int
	 *     convert %1 = %1 real   : int
	 *     return %1              : real
	 * </pre>
	 * <p>
	 * Here, we see that the <code>int</code> value in register <code>%1</code>
	 * must be explicitly converted into a <code>real</code> value before it can
	 * be returned from this function.
	 * </p>
	 * <p>
	 * <b>NOTE:</b> In many cases, this bytecode may correspond to a nop on the
	 * hardware. Consider converting from <code>[any]</code> to <code>any</code>
	 * . On the JVM, <code>any</code> translates to <code>Object</code>, whilst
	 * <code>[any]</code> translates to <code>List</code> (which is an instance
	 * of <code>Object</code>). Thus, no conversion is necessary since
	 * <code>List</code> can safely flow into <code>Object</code>.
	 * </p>
	 *
	 */
	public static final class Convert extends Bytecode {

		public Convert(Type from, int target, int operand, Type result) {
			super(new Type[] { from, result }, new int[] { target }, operand);
		}

		public Type result() {
			return type(1);
		}

		public int opcode() {
			return OPCODE_convert;
		}

		public boolean equals(Object o) {
			return o instanceof Convert && super.equals(o);
		}

		public String toString() {
			return "convert %" + target(0) + " = %" + operand(0) + " " + result() + " : " + type(0);
		}
	}

	/**
	 * Writes a constant value to a target register. This includes
	 * <i>integers</i>, <i>rationals</i>, <i>lists</i>, <i>sets</i>, <i>maps</i>
	 * , etc. For example, the following Whiley code:
	 *
	 * <pre>
	 * function f(int x) -> int:
	 *     xs = {1,2.12}
	 *     return |xs| + 1
	 * </pre>
	 *
	 * can be translated into the following WyIL code:
	 *
	 * <pre>
	 * function f(int x) -> int:
	 * body:
	 *     var xs
	 *     const %2 = 1               : int
	 *     convert %2 = % 2 int|real  : int
	 *     const %3 = 2.12            : real
	 *     convert %3 = % 3 int|real  : real
	 *     newset %1 = (%2, %3)       : {int|real}
	 *     assign %3 = %1             : {int|real}
	 *     lengthof %3 = % 3          : {int|real}
	 *     const %4 = 1               : int
	 *     add %2 = % 3, %4           : int
	 *     return %2                  : int
	 * </pre>
	 *
	 * Here, we see two kinds of constants values being used: integers (i.e.
	 * <code>const %2 = 1</code>) and rationals (i.e.
	 * <code>const %3 = 2.12</code>).
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Const extends Bytecode {
		private final Constant constant;

		public Const(int target, Constant constant) {
			super(new Type[0], new int[] { target }, new int[0]);
			this.constant = constant;
		}

		public int opcode() {
			return OPCODE_const;
		}

		public int target() {
			return targets()[0];
		}
		
		public Constant constant() {
			return constant;
		}

		public int hashCode() {
			return constant.hashCode() + targets()[0];
		}

		public boolean equals(Object o) {
			if (o instanceof Const) {
				Const c = (Const) o;
				return constant.equals(c.constant) && Arrays.equals(targets(), c.targets());
			}
			return false;
		}

		public String toString() {
			return "const %" + targets()[0] + " = " + constant + " : " + constant.type();
		}
	}

	/**
	 * Read a string from the operand register and prints it to the debug
	 * console. For example, the following Whiley code:
	 *
	 * <pre>
	 * method f(int x):
	 *     debug "X = " + x
	 * </pre>
	 *
	 * can be translated into the following WyIL code:
	 *
	 * <pre>
	 * method f(int x):
	 * body:
	 *     const %2 = "X = "       : string
	 *     convert %0 = %0 any     : int
	 *     invoke %0 (%0) whiley/lang/Any:toString : string(any)
	 *     strappend %1 = %2, %0   : string
	 *     debug %1                : string
	 *     return
	 * </pre>
	 *
	 * <b>NOTE</b> This bytecode is not intended to form part of the program's
	 * operation. Rather, it is to facilitate debugging within functions (since
	 * they cannot have side-effects). Furthermore, if debugging is disabled,
	 * this bytecode is a nop.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Debug extends Bytecode {

		public Debug(int operand) {
			super(new Type[] { Type.Array(Type.T_INT, false) }, new int[0], operand);
		}

		public int opcode() {
			return OPCODE_debug;
		}

		public boolean equals(Object o) {
			return o instanceof Debug && super.equals(o);
		}

		public String toString() {
			return "debug %" + operands[0] + " " + " : " + types[0];
		}
	}

	/**
	 * An abstract class representing either an <code>assert</code> or
	 * <code>assume</code> bytecode block.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static abstract class AssertOrAssume extends Compound {
		private AssertOrAssume(int block) {
			super(block, new Type[0], new int[0], new int[0]);
		}
	}

	/**
	 * Represents a block of bytecode instructions representing an assertion.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Assert extends AssertOrAssume {

		public Assert(int block) {
			super(block);
		}

		public int opcode() {
			return OPCODE_assert;
		}

		public String toString() {
			return "assert #" + block;
		}

		public boolean equals(Object o) {
			return o instanceof Assume && super.equals(o);
		}
	}

	/**
	 * Represents a block of bytecode instructions representing an assumption.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Assume extends AssertOrAssume {

		public Assume(int block) {
			super(block);
		}

		public int opcode() {
			return OPCODE_assume;
		}

		public String toString() {
			return "assume #" + block;
		}

		public boolean equals(Object o) {
			return o instanceof Assume && super.equals(o);
		}
	}

	/**
	 * A bytecode that halts execution by raising a runtime fault. This bytecode
	 * signals that some has gone wrong, and is typically used to signal an
	 * assertion failure.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Fail extends Bytecode {
		public Fail() {
			super(new Type[0], new int[0]);
		}

		@Override
		public int opcode() {
			return OPCODE_fail;
		}

		public String toString() {
			return "fail";
		}
	}

	/**
	 * Reads a record value from an operand register, extracts the value of a
	 * given field and writes this to the target register. For example, the
	 * following Whiley code:
	 *
	 * <pre>
	 * type Point is {int x, int y}
	 *
	 * function f(Point p) -> int:
	 *     return p.x + p.y
	 * </pre>
	 *
	 * can be translated into the following WyIL code:
	 *
	 * <pre>
	 * function f({int x,int y} p) -> int:
	 * body:
	 *     fieldload %2 = %0 x    : {int x,int y}
	 *     fieldload %3 = %0 y    : {int x,int y}
	 *     add %1 = %2, %3        : int
	 *     return %1              : int
	 * </pre>
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class FieldLoad extends Bytecode {
		private final String field;

		public FieldLoad(Type.EffectiveRecord type, int target, int operand, String field) {
			super((Type) type, target, operand);
			if (field == null) {
				throw new IllegalArgumentException("FieldLoad field argument cannot be null");
			}
			this.field = field;
		}

		public int opcode() {
			return OPCODE_fieldload;
		}

		public int hashCode() {
			return super.hashCode() + field.hashCode();
		}

		public Type fieldType() {
			Type.EffectiveRecord er = (Type.EffectiveRecord) type(0);
			return er.fields().get(field);
		}

		public String fieldName() {
			return field;
		}
		
		public boolean equals(Object o) {
			if (o instanceof FieldLoad) {
				FieldLoad i = (FieldLoad) o;
				return super.equals(i) && field.equals(i.field);
			}
			return false;
		}

		public String toString() {
			return "fieldload %" + target(0) + " = %" + operand(0) + " " + field + " : " + type(0);
		}
	}

	/**
	 * Branches unconditionally to the given label. This is typically used for
	 * if/else statements. For example, the following Whiley code:
	 *
	 * <pre>
	 * function f(int x) -> int:
	 *     if x >= 0:
	 *         x = 1
	 *     else:
	 *         x = -1
	 *     return x
	 * </pre>
	 *
	 * can be translated into the following WyIL code:
	 *
	 * <pre>
	 * function f(int x) -> int:
	 * body:
	 *     const %1 = 0             : int
	 *     iflt %0, %1 goto blklab0 : int
	 *     const %0 = 1             : int
	 *     goto blklab1
	 * .blklab0
	 *     const %0 = 1             : int
	 *     neg %0 = % 0             : int
	 * .blklab1
	 *     return %0                : int
	 * </pre>
	 *
	 * Here, we see the <code>goto</code> bytecode being used to jump from the
	 * end of the true branch over the false branch.
	 *
	 * <p>
	 * <b>Note:</b> in WyIL bytecode, <i>such branches may only go forward</i>.
	 * Thus, a <code>goto</code> bytecode cannot be used to implement the
	 * back-edge of a loop. Rather, a loop block must be used for this purpose.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Goto extends Branching {
		public Goto(String target) {
			super(target, new Type[0], new int[0]);
		}

		public int opcode() {
			return OPCODE_goto;
		}

		public Goto relabel(Map<String, String> labels) {
			String nlabel = labels.get(destination());
			if (nlabel == null) {
				return this;
			} else {
				return new Goto(nlabel);
			}
		}

		public boolean equals(Object o) {
			return o instanceof Goto && super.equals(o);
		}

		public String toString() {
			return "goto " + destination();
		}
	}

	/**
	 * <p>
	 * Branches conditionally to the given label by reading the values from two
	 * operand registers and comparing them. The possible comparators are:
	 * </p>
	 * <ul>
	 * <li><i>equals (eq) and not-equals (ne)</i>. Both operands must have the
	 * given type.</li>
	 * <li><i>less-than (lt), less-than-or-equals (le), greater-than (gt) and
	 * great-than-or-equals (ge).</i> Both operands must have the given type,
	 * which additionally must by either <code>char</code>, <code>int</code> or
	 * <code>real</code>.</li>
	 * <li><i>element of (in).</i> The second operand must be a set whose
	 * element type is that of the first.</li>
	 * </ul>
	 * For example, the following Whiley code:
	 *
	 * <pre>
	 * function f(int x, int y) -> int:
	 *     if x < y:
	 *         return -1
	 *     else if x > y:
	 *         return 1
	 *     else:
	 *         return 0
	 * </pre>
	 *
	 * can be translated into the following WyIL code:
	 *
	 * <pre>
	 * function f(int x, int y) -> int:
	 * body:
	 *     ifge %0, %1 goto blklab0 : int
	 *     const %2 = -1 : int
	 *     return %2 : int
	 * .blklab0
	 *     ifle %0, %1 goto blklab2 : int
	 *     const %2 = 1 : int
	 *     return %2 : int
	 * .blklab2
	 *     const %2 = 0 : int
	 *     return %2 : int
	 * </pre>
	 *
	 * <b>Note:</b> in WyIL bytecode, <i>such branches may only go forward</i>.
	 * Thus, an <code>ifgoto</code> bytecode cannot be used to implement the
	 * back-edge of a loop. Rather, a loop block must be used for this purpose.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class If extends Branching {
		public If(Type type, int operand, String target) {
			super(target, new Type[] { type }, new int[0], operand);
		}

		public If relabel(Map<String, String> labels) {
			String nlabel = labels.get(destination());
			if (nlabel == null) {
				return this;
			} else {
				return new If(types[0], operands[0], nlabel);
			}
		}

		public int opcode() {
			return OPCODE_if;
		}

		public boolean equals(Object o) {
			return o instanceof If && super.equals(o);			
		}

		public String toString() {
			return "if" + " %" + operands[0] + " goto " + destination() + " : " + types[0];
		}
	}

	/**
	 * Branches conditionally to the given label based on the result of a
	 * runtime type test against a value from the operand register. More
	 * specifically, it checks whether the value is a subtype of the type test.
	 * The operand register is automatically <i>retyped</i> as a result of the
	 * type test. On the true branch, its type is intersected with type test. On
	 * the false branch, its type is intersected with the <i>negation</i> of the
	 * type test. For example, the following Whiley code:
	 *
	 * <pre>
	 * function f(int|int[] x) -> int:
	 *     if x is int[]:
	 *         return |x|
	 *     else:
	 *         return x
	 * </pre>
	 *
	 * can be translated into the following WyIL code:
	 *
	 * <pre>
	 * function f(int|int[] x) -> int:
	 * body:
	 *     ifis %0, int[] goto lab    : int|int[]
	 *     return %0                  : int
	 * .lab
	 *     lengthof %0 = %0           : int[]
	 *     return %0                  : int
	 * </pre>
	 *
	 * Here, we see that, on the false branch, register <code>%0</code> is
	 * automatically given type <code>int</code>, whilst on the true branch it
	 * is automatically given type <code>int[]</code>.
	 *
	 * <p>
	 * <b>Note:</b> in WyIL bytecode, <i>such branches may only go forward</i>.
	 * Thus, an <code>ifis</code> bytecode cannot be used to implement the
	 * back-edge of a loop. Rather, a loop block must be used for this purpose.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class IfIs extends Branching {
		public IfIs(Type type, int leftOperand, Type rightOperand, String target) {
			super(target, new Type[] { type, rightOperand }, new int[0], leftOperand);
		}

		public int opcode() {
			return OPCODE_ifis;
		}

		public Type rightOperand() {
			return type(1);
		}

		public IfIs relabel(Map<String, String> labels) {
			String nlabel = labels.get(destination());
			if (nlabel == null) {
				return this;
			} else {
				return new IfIs(types[0], operands[0], types[1], nlabel);
			}
		}

		public boolean equals(Object o) {
			return o instanceof IfIs && super.equals(o);
		}

		public String toString() {
			return "ifis" + " %" + operands[0] + ", " + types[1] + " goto " + destination() + " : " + types[0];
		}
	}

	/**
	 * Represents an indirect function call. For example, consider the
	 * following:
	 *
	 * <pre>
	 * function fun(function (int)->int f, int x) -> int:
	 *    return f(x)
	 * </pre>
	 *
	 * Here, the function call <code>f(x)</code> is indirect as the called
	 * function is determined by the variable <code>f</code>.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class IndirectInvoke extends Bytecode {

		/**
		 * Construct an indirect invocation bytecode which assigns to an
		 * optional target register the result from indirectly invoking a
		 * function in a given operand with a given set of parameter operands.
		 *
		 * @param type
		 * @param target
		 *            Register (optional) to which result of invocation is
		 *            assigned.
		 * @param operand
		 *            Register holding function point through which indirect
		 *            invocation is made.
		 * @param operands
		 *            Registers holding parameters for the invoked function
		 */
		public IndirectInvoke(Type.FunctionOrMethod type, int[] targets, int operand, int[] operands) {
			super(new Type.FunctionOrMethod[] { type }, targets, append(operand, operands));
		}

		/**
		 * Return register holding the indirect function/method reference.
		 *
		 * @return
		 */
		public int reference() {
			return operands()[0];
		}

		/**
		 * Return register holding the ith parameter for the invoked function.
		 *
		 * @param i
		 * @return
		 */
		public int parameter(int i) {
			return operands()[i + 1];
		}

		/**
		 * Return registers holding parameters for the invoked function.
		 *
		 * @param i
		 * @return
		 */
		public int[] parameters() {
			return Arrays.copyOfRange(operands(), 1, operands().length);
		}

		public int opcode() {
			return OPCODE_indirectinvoke;
		}

		@Override
		public Type.FunctionOrMethod type(int i) {
			return (Type.FunctionOrMethod) super.type(i);
		}

		public boolean equals(Object o) {
			return o instanceof IndirectInvoke && super.equals(o);
		}

		public String toString() {
			return "indirectinvoke " + arrayToString(targets()) + " = %" + reference() + " "
					+ arrayToString(parameters()) + " : " + type(0);
		}
	}

	/**
	 * Represents a block of bytecode instructions representing an assertion.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Invariant extends Assert {

		public Invariant(int block) {
			super(block);
		}

		public int opcode() {
			return OPCODE_invariant;
		}

		public String toString() {
			return "invariant";
		}

		public int hashCode() {
			return block;
		}

		public boolean equals(Object o) {
			if (o instanceof Invariant) {
				Invariant f = (Invariant) o;
				return block == f.block;
			}
			return false;
		}
	}

	/**
	 * Corresponds to a function or method call whose parameters are read from
	 * zero or more operand registers. If a return value is required, this is
	 * written to a target register afterwards. For example, the following
	 * Whiley code:
	 *
	 * <pre>
	 * function g(int x, int y, int z) -> int:
	 *     return x * y * z
	 *
	 * function f(int x, int y) -> int:
	 *     r = g(x,y,3)
	 *     return r + 1
	 * </pre>
	 *
	 * can be translated into the following WyIL code:
	 *
	 * <pre>
	 * function g(int x, int y, int z) -> int:
	 * body:
	 *     mul %3 = %0, %1   : int
	 *     mul %3 = %3, %2   : int
	 *     return %3         : int
	 *
	 * function f(int x, int y) -> int:
	 * body:
	 *     const %2 = 3                    : int
	 *     invoke %2 = (%0, %1, %2) test:g   : int(int,int,int)
	 *     const %3 = 1                    : int
	 *     add %2 = (%2, %3)                : int
	 *     return %2                       : int
	 * </pre>
	 *
	 * Here, we see that arguments to the <code>invoke</code> bytecode are
	 * supplied in the order they are given in the function or method's
	 * declaration.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Invoke extends Bytecode {
		private final NameID name;

		public Invoke(Type.FunctionOrMethod type, int[] targets, int[] operands, NameID name) {
			super(new Type.FunctionOrMethod[] { type }, targets, operands);
			this.name = name;
		}

		public int opcode() {
			return OPCODE_invoke;
		}

		public NameID name() {
			return name;
		}
		
		public int hashCode() {
			return name.hashCode() + super.hashCode();
		}

		@Override
		public Type.FunctionOrMethod type(int i) {
			return (Type.FunctionOrMethod) super.type(i);
		}

		public boolean equals(Object o) {
			if (o instanceof Invoke) {
				Invoke i = (Invoke) o;
				return name().equals(i.name) && super.equals(i);
			}
			return false;
		}

		public String toString() {
			return "invoke " + arrayToString(targets()) + " = " + arrayToString(operands()) + " " + name + " : "
					+ type(0);
		}
	}

	public static final class Lambda extends Bytecode {
		private final NameID name;

		public Lambda(Type.FunctionOrMethod type, int target, int[] operands, NameID name) {
			super(type, target, operands);
			this.name = name;
		}

		public int opcode() {
			return OPCODE_lambda;
		}

		public NameID name() {
			return name;
		}
		
		public int hashCode() {
			return name.hashCode() + super.hashCode();
		}

		@Override
		public Type.FunctionOrMethod type(int i) {
			return (Type.FunctionOrMethod) super.type(i);
		}

		public boolean equals(Object o) {
			if (o instanceof Lambda) {
				Lambda i = (Lambda) o;
				return name().equals(i.name()) && super.equals(i);
			}
			return false;
		}

		public String toString() {
			return "lambda %" + target(0) + " = " + arrayToString(operands()) + " " + name + " : " + type(0);
		}
	}

	/**
	 * Represents the labelled destination of a branch or loop statement.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Label extends Bytecode {
		private final String label;

		public Label(String label) {
			super(new Type[0], new int[0], new int[0]);
			this.label = label;
		}

		public int opcode() {
			return -1;
		}

		public String label() {
			return label;
		}
		
		public Label relabel(Map<String, String> labels) {
			String nlabel = labels.get(label);
			if (nlabel == null) {
				return this;
			} else {
				return new Label(nlabel);
			}
		}

		public int hashCode() {
			return label().hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof Label) {
				return label.equals(((Label) o).label);
			}
			return false;
		}

		public String toString() {
			return "." + label;
		}
	}

	/**
	 * Represents a block of code which loops continuously until e.g. a
	 * conditional branch is taken out of the block. For example:
	 *
	 * <pre>
	 * function f() -> int:
	 *     r = 0
	 *     while r < 10:
	 *         r = r + 1
	 *     return r
	 * </pre>
	 *
	 * can be translated into the following WyIL code:
	 *
	 * <pre>
	 * function f() -> int:
	 * body:
	 *     const %0 = 0             : int
	 *     loop (%0)
	 *         const %1 = 10        : int
	 *         ifge %0, %1 goto blklab0 : int
	 *         const %1 = 1         : int
	 *         add %0 = %0, %1      : int
	 * .blklab0
	 *     return %0                : int
	 * </pre>
	 *
	 * <p>
	 * Here, we see a loop which increments an accumulator register
	 * <code>%0</code> until it reaches <code>10</code>, and then exits the loop
	 * block.
	 * </p>
	 * <p>
	 * The <i>modified operands</i> of a loop bytecode (shown in brackets
	 * alongside the bytecode) indicate those operands which are modified at
	 * some point within the loop.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Loop extends Compound {

		public Loop(int[] targets, int block, int... operands) {
			super(block, new Type[0], targets, operands);
		}

		public int opcode() {
			return OPCODE_loop;
		}

		public boolean equals(Object o) {
			if (o instanceof Loop) {
				Loop f = (Loop) o;
				return block == f.block && super.equals(f);
			}
			return false;
		}

		public int[] modifiedOperands() {
			return targets();
		}

		public String toString() {
			return "loop " + arrayToString(targets()) + " = " + block;
		}
	}

	public static final class Quantify extends Loop {

		public Quantify(int startOperand, int endOperand, int indexOperand, int[] targets, int block) {
			super(targets, block, startOperand, endOperand, indexOperand);
		}

		public int opcode() {
			return OPCODE_quantify;
		}

		public int startOperand() {
			return operands[0];
		}

		public int endOperand() {
			return operands[1];
		}

		public int indexOperand() {
			return operands[2];
		}

		public boolean equals(Object o) {
			if (o instanceof Quantify) {
				Quantify f = (Quantify) o;
				return super.equals(f);
			}
			return false;
		}

		public String toString() {
			return "quantify " + arrayToString(targets()) + " = #" + block() + arrayToString(operands());
		}
	}

	/**
	 * Represents a type which may appear on the left of an assignment
	 * expression. Arrays, Records and References are the only valid types for
	 * an lval.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static abstract class LVal<T> {
		protected T type;

		public LVal(T t) {
			this.type = t;
		}

		public T rawType() {
			return type;
		}
	}

	/**
	 * An LVal with array type.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class ArrayLVal extends LVal<Type.EffectiveArray> {
		public final int indexOperand;

		public ArrayLVal(Type.EffectiveArray t, int indexOperand) {
			super(t);
			this.indexOperand = indexOperand;
		}
	}

	/**
	 * An LVal with list type.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class ReferenceLVal extends LVal<Type.Reference> {
		public ReferenceLVal(Type.Reference t) {
			super(t);
		}
	}

	/**
	 * An LVal with record type.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class RecordLVal extends LVal<Type.EffectiveRecord> {
		public final String field;

		public RecordLVal(Type.EffectiveRecord t, String field) {
			super(t);
			this.field = field;
			if (!t.fields().containsKey(field)) {
				throw new IllegalArgumentException("invalid Record Type");
			}
		}
	}

	private static final class UpdateIterator implements Iterator<LVal> {
		private final ArrayList<String> fields;
		private final int[] operands;
		private Type iter;
		private int fieldIndex;
		private int operandIndex;
		private int index;

		public UpdateIterator(Type type, int level, int[] operands, ArrayList<String> fields) {
			this.fields = fields;
			this.iter = type;
			this.index = level;
			this.operands = operands;
		}

		public LVal next() {
			Type raw = iter;
			index--;
			if (iter instanceof Type.Reference) {
				Type.Reference ref = (Type.Reference) iter;
				iter = ref.element();
				return new ReferenceLVal(ref);
			} else if (iter instanceof Type.EffectiveArray) {
				Type.EffectiveArray list = (Type.EffectiveArray) iter;
				iter = list.element();
				return new ArrayLVal(list, operands[operandIndex++]);
			} else if (iter instanceof Type.EffectiveRecord) {
				Type.EffectiveRecord rec = (Type.EffectiveRecord) iter;
				String field = fields.get(fieldIndex++);
				iter = rec.fields().get(field);
				return new RecordLVal(rec, field);
			} else {
				throw new IllegalArgumentException("Invalid type for Update: " + iter);
			}
		}

		public boolean hasNext() {
			return index > 0;
		}

		public void remove() {
			throw new UnsupportedOperationException("UpdateIterator is unmodifiable");
		}
	}

	/**
	 * <p>
	 * Pops a compound structure, zero or more indices and a value from the
	 * stack and updates the compound structure with the given value. Valid
	 * compound structures are lists, dictionaries, strings, records and
	 * references.
	 * </p>
	 * <p>
	 * Ideally, this operation is done in-place, meaning the operation is
	 * constant time. However, to support Whiley's value semantics this bytecode
	 * may require (in some cases) a clone of the underlying data-structure.
	 * Thus, the worst-case runtime of this operation is linear in the size of
	 * the compound structure.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Update extends Bytecode implements Iterable<LVal> {
		public final ArrayList<String> fields;

		/**
		 * Construct an Update bytecode which assigns to a given operand to a
		 * set of target registers. For indirect map/list updates, an additional
		 * set of operands is used to generate the appropriate keys. For field
		 * assignments, a set of fields is provided.
		 *
		 * @param beforeType
		 * @param target
		 *            Register being assigned
		 * @param operands
		 *            Registers used for keys on left-hand side in map/list
		 *            updates
		 * @param operand
		 *            Register on right-hand side whose value is assigned
		 * @param afterType
		 * @param fields
		 *            Fields for record updates
		 */
		public Update(Type beforeType, int target, int[] operands, int operand, Type afterType,
				Collection<String> fields) {
			super(new Type[] { beforeType, afterType }, new int[] { target }, append(operands, operand));
			if (fields == null) {
				throw new IllegalArgumentException("FieldStore fields argument cannot be null");
			}
			this.fields = new ArrayList<String>(fields);
		}

		// Helper used for clone()
		private Update(Type[] types, int[] targets, int[] operands, Collection<String> fields) {
			super(types, targets, operands);
			if (fields == null) {
				throw new IllegalArgumentException("FieldStore fields argument cannot be null");
			}
			this.fields = new ArrayList<String>(fields);
		}

		public int opcode() {
			return OPCODE_update;
		}

		/**
		 * Returns register from which assigned value is read. This is also
		 * known as the "right-hand side".
		 *
		 * @return
		 */
		public int result() {
			return operands()[operands().length - 1];
		}

		/**
		 * Get the given key register (in order of appearance from the left)
		 * used in a map or list update.
		 *
		 * @param index
		 * @return
		 */
		public int key(int index) {
			return operands()[index];
		}

		/**
		 * Return the registers used to hold key values for map or list updates.
		 *
		 * @return
		 */
		public int[] keys() {
			return Arrays.copyOf(operands(), operands().length - 1);
		}

		public int level() {
			int base = -1; // because last operand is rhs
			if (type(0) instanceof Type.Reference) {
				base++;
			}
			return base + fields.size() + operands().length;
		}

		public Iterator<LVal> iterator() {
			return new UpdateIterator(afterType(), level(), keys(), fields);
		}

		public Type afterType() {
			return types[1];
		}

		/**
		 * Extract the type for the right-hand side of this assignment.
		 *
		 * @return
		 */
		public Type rhs() {
			Type iter = afterType();

			int fieldIndex = 0;
			for (int i = 0; i != level(); ++i) {
				if (iter instanceof Type.Reference) {
					Type.Reference proc = Type.effectiveReference(iter);
					iter = proc.element();
				} else if (iter instanceof Type.EffectiveArray) {
					Type.EffectiveArray list = (Type.EffectiveArray) iter;
					iter = list.element();
				} else if (iter instanceof Type.EffectiveRecord) {
					Type.EffectiveRecord rec = (Type.EffectiveRecord) iter;
					String field = fields.get(fieldIndex++);
					iter = rec.fields().get(field);
				} else {
					throw new IllegalArgumentException("Invalid type for Update: " + iter);
				}
			}
			return iter;
		}

		public boolean equals(Object o) {
			if (o instanceof Update) {
				Update i = (Update) o;
				return super.equals(o) && fields.equals(i.fields);
			}
			return false;
		}

		public String toString() {
			String r = "%" + target(0);
			for (LVal lv : this) {
				if (lv instanceof ArrayLVal) {
					ArrayLVal l = (ArrayLVal) lv;
					r = r + "[%" + l.indexOperand + "]";
				} else if (lv instanceof RecordLVal) {
					RecordLVal l = (RecordLVal) lv;
					r = r + "." + l.field;
				} else {
					ReferenceLVal l = (ReferenceLVal) lv;
					r = "(*" + r + ")";
				}
			}
			return "update " + r + " = %" + result() + " : " + type(0) + " -> " + afterType();
		}
	}

	/**
	 * Returns from the enclosing function or method, possibly returning a
	 * value. For example, the following Whiley code:
	 *
	 * <pre>
	 * function f(int x, int y) -> int:
	 *     return x + y
	 * </pre>
	 *
	 * can be translated into the following WyIL:
	 *
	 * <pre>
	 * function f(int x, int y) -> int:
	 * body:
	 *     assign %3 = %0    : int
	 *     assign %4 = %1    : int
	 *     add %2 = % 3, %4  : int
	 *     return %2         : int
	 * </pre>
	 *
	 * Here, the <code>return<code> bytecode returns the value of its operand
	 * register.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Return extends Bytecode {

		public Return() {
			super(new Type[0], new int[0], new int[0]);
		}
		
		public Return(Type[] types, int... operands) {
			super(types, new int[0], operands);
		}

		@Override
		public int opcode() {
			return OPCODE_return;
		}

		public boolean equals(Object o) {
			if (o instanceof Return) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			String r = "return";
			for (int i = 0; i != operands.length; ++i) {
				if (i != 0) {
					r += ",";
				}
				r += " %" + operands[i];
			}
			return r;
		}
	}

	/**
	 * Performs a multi-way branch based on the value contained in the operand
	 * register. A <i>dispatch table</i> is provided which maps individual
	 * matched values to their destination labels. For example, the following
	 * Whiley code:
	 *
	 * <pre>
	 * function f(int x) -> string:
	 *     switch x:
	 *         case 1:
	 *             return "ONE"
	 *         case 2:
	 *             return "TWO"
	 *         default:
	 *             return "OTHER"
	 * </pre>
	 *
	 * can be translated into the following WyIL code:
	 *
	 * <pre>
	 * function f(int x) -> string:
	 * body:
	 *     switch %0 1->blklab1, 2->blklab2, *->blklab3
	 * .blklab1
	 *     const %1 = "ONE" : string
	 *     return %1 : string
	 * .blklab2
	 *     const %1 = "TWO" : string
	 *     return %1 : string
	 * .blklab3
	 *     const %1 = "OTHER" : string
	 *     return %1 : string
	 * </pre>
	 *
	 * Here, we see how e.g. value <code>1</code> is mapped to the label
	 * <code>blklab1</code>. Thus, if the operand register <code>%0</code>
	 * contains value <code>1</code>, then control will be transferred to that
	 * label. The final mapping <code>*->blklab3</code> covers the default case
	 * where the value in the operand is not otherwise matched.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Switch extends Bytecode {
		private final ArrayList<Pair<Constant, String>> branches;
		private final String defaultTarget;

		public Switch(Type type, int operand, String defaultTarget, Collection<Pair<Constant, String>> branches) {
			super(new Type[] { type }, new int[0], operand);
			this.branches = new ArrayList<Pair<Constant, String>>(branches);
			this.defaultTarget = defaultTarget;
		}

		@Override
		public int opcode() {
			return OPCODE_switch;
		}

		public Switch relabel(Map<String, String> labels) {
			ArrayList<Pair<Constant, String>> nbranches = new ArrayList();
			for (Pair<Constant, String> p : branches()) {
				String nlabel = labels.get(p.second());
				if (nlabel == null) {
					nbranches.add(p);
				} else {
					nbranches.add(new Pair(p.first(), nlabel));
				}
			}

			String nlabel = labels.get(defaultTarget());
			if (nlabel == null) {
				return new Switch(types[0], operands[0], defaultTarget(), nbranches);
			} else {
				return new Switch(types[0], operands[0], nlabel, nbranches);
			}
		}

		public boolean equals(Object o) {
			if (o instanceof Switch) {
				Switch ig = (Switch) o;
				return operands[0] == ig.operands[0] && defaultTarget().equals(ig.defaultTarget())
						&& branches().equals(ig.branches()) && types[0].equals(ig.types[0]);
			}
			return false;
		}

		public String toString() {
			String table = "";
			boolean firstTime = true;
			for (Pair<Constant, String> p : branches()) {
				if (!firstTime) {
					table += ", ";
				}
				firstTime = false;
				table += p.first() + "->" + p.second();
			}
			table += ", *->" + defaultTarget();
			return "switch %" + operands[0] + " " + table;
		}

		
		public String defaultTarget() {
			return defaultTarget;
		}

		public ArrayList<Pair<Constant, String>> branches() {
			return branches;
		}
	}

	// =============================================================
	// Helpers
	// =============================================================

	/**
	 * Construct a mapping from labels to their block indices within a root
	 * block. This is useful so they can easily be resolved during the
	 * subsequent traversal of the block.
	 * 
	 * @param block
	 * @return
	 */
	public static Map<String, BytecodeForest.Index> buildLabelMap(BytecodeForest forest) {
		HashMap<String, BytecodeForest.Index> labels = new HashMap<String, BytecodeForest.Index>();
		for (int i = 0; i != forest.numBlocks(); ++i) {
			BytecodeForest.Block block = forest.get(i);
			for (int j = 0; j != block.size(); ++j) {
				Bytecode code = block.get(j).code();
				if (code instanceof Bytecode.Label) {
					// Found a label, so register it in the labels map
					Bytecode.Label label = (Bytecode.Label) code;
					labels.put(label.label(), new BytecodeForest.Index(i, j));
				}
			}
		}
		return labels;
	}
	
	private static String arrayToString(int... operands) {
		String r = "(";
		for (int i = 0; i != operands.length; ++i) {
			if (i != 0) {
				r = r + ", ";
			}
			r = r + "%" + operands[i];			
		}
		return r + ")";
	}

	private static int[] append(int[] operands, int operand) {
		int[] noperands = Arrays.copyOf(operands, operands.length + 1);
		noperands[operands.length] = operand;
		return noperands;
	}

	private static int[] append(int operand, int[] operands) {
		int[] noperands = new int[operands.length + 1];
		System.arraycopy(operands, 0, noperands, 1, operands.length);
		noperands[0] = operand;
		return noperands;
	}

	// =========================================================================
	// Opcodes
	// =========================================================================
	
	public static final int OPCODE_goto      = 1;
	public static final int OPCODE_fail      = 2;
	public static final int OPCODE_assert    = 4;
	public static final int OPCODE_assume    = 5;
	public static final int OPCODE_invariant = 6;

	// Unary Operators
	public static final int UNARY_OPERATOR = 7;
	
	public static final int OPCODE_debug    = UNARY_OPERATOR+0;
	public static final int OPCODE_return   = UNARY_OPERATOR+1;
	public static final int OPCODE_ifis     = UNARY_OPERATOR+2;
	public static final int OPCODE_switch   = UNARY_OPERATOR+3;

	// Unary Assignables
	public static final int UNARY_ASSIGNABLE = UNARY_OPERATOR+5;
		
	public static final int OPCODE_fieldload   = UNARY_ASSIGNABLE+8;
	public static final int OPCODE_convert     = UNARY_ASSIGNABLE+9;
	public static final int OPCODE_const       = UNARY_ASSIGNABLE+10;
	
	// Binary Operators
	public static final int BINARY_OPERATOR = UNARY_ASSIGNABLE+11;
	
	public static final int OPCODE_if         = BINARY_OPERATOR+0;
	
	// Binary Assignables
	public static final int BINARY_ASSIGNABLE = BINARY_OPERATOR+6;
	
	public static final int OPCODE_neg         = BINARY_ASSIGNABLE+0;
	public static final int OPCODE_not         = BINARY_ASSIGNABLE+1;
	public static final int OPCODE_arrayinvert = BINARY_ASSIGNABLE+2;	
	public static final int OPCODE_dereference = BINARY_ASSIGNABLE+3;
	public static final int OPCODE_arraylength = BINARY_ASSIGNABLE+4;	
	public static final int OPCODE_add         = BINARY_ASSIGNABLE+5;
	public static final int OPCODE_sub         = BINARY_ASSIGNABLE+6;
	public static final int OPCODE_mul         = BINARY_ASSIGNABLE+7;
	public static final int OPCODE_div         = BINARY_ASSIGNABLE+8;
	public static final int OPCODE_rem         = BINARY_ASSIGNABLE+9;
	public static final int OPCODE_eq          = BINARY_ASSIGNABLE+10;
	public static final int OPCODE_ne          = BINARY_ASSIGNABLE+11;
	public static final int OPCODE_lt          = BINARY_ASSIGNABLE+12;
	public static final int OPCODE_le          = BINARY_ASSIGNABLE+13;
	public static final int OPCODE_gt          = BINARY_ASSIGNABLE+14;
	public static final int OPCODE_ge          = BINARY_ASSIGNABLE+15;
	public static final int OPCODE_bitwiseor   = BINARY_ASSIGNABLE+16;
	public static final int OPCODE_bitwisexor  = BINARY_ASSIGNABLE+17;
	public static final int OPCODE_bitwiseand  = BINARY_ASSIGNABLE+18;
	public static final int OPCODE_lshr        = BINARY_ASSIGNABLE+19;
	public static final int OPCODE_rshr        = BINARY_ASSIGNABLE+20;
	public static final int OPCODE_arrayindex  = BINARY_ASSIGNABLE+21;	
	public static final int OPCODE_arrygen     = BINARY_ASSIGNABLE+22;
	public static final int OPCODE_array       = BINARY_ASSIGNABLE+23;
	public static final int OPCODE_record      = BINARY_ASSIGNABLE+24;
	public static final int OPCODE_newobject   = BINARY_ASSIGNABLE+25;
	public static final int OPCODE_assign      = BINARY_ASSIGNABLE+26;
	
	// Nary Assignables
	public static final int NARY_ASSIGNABLE = BINARY_ASSIGNABLE+26;
		
	public static final int OPCODE_invoke           = NARY_ASSIGNABLE+2;
	public static final int OPCODE_indirectinvoke   = NARY_ASSIGNABLE+3;
	public static final int OPCODE_lambda           = NARY_ASSIGNABLE+4;
	public static final int OPCODE_loop             = NARY_ASSIGNABLE+5;	
	public static final int OPCODE_quantify         = NARY_ASSIGNABLE+6;
	public static final int OPCODE_update           = NARY_ASSIGNABLE+7;	
		
	// =========================================================================
	// Bytecode Schemas
	// =========================================================================

	public enum Targets {
		ZERO, ONE, TWO, MANY
	}

	public enum Operands {
		ZERO, ONE, TWO, MANY
	}

	public enum Types {
		ZERO, ONE, TWO, MANY
	}

	public enum Extras {
		STRING, // index into string pool
		CONSTANT, // index into constant pool
		NAME, // index into name pool
		TARGET, // branch target
		BLOCK, // block index
		STRING_ARRAY, // determined on the fly
		SWITCH_ARRAY, // determined on the fly
	}

	public static abstract class Schema {
		private final Targets targets;
		private final Operands operands;
		private final Types types;
		private final Extras[] extras;

		public Schema(Targets targets, Operands operands, Types types, Extras... extras) {
			this.targets = targets;
			this.operands = operands;
			this.types = types;
			this.extras = extras;
		}
	
		public Extras[] extras() {
			return extras;
		}
		
		public abstract Bytecode construct(int opcode, int[] targets, int[] operands, Type[] types, Object[] extras);

		public String toString() {
			return "<" + targets.toString() + " targets, " + operands + " operands, " + types + " types, " + Arrays.toString(extras) + ">";
		}
	}
}
