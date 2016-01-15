package wyil.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import wycc.lang.NameID;
import wycc.util.Pair;
import wyil.lang.Code.*;
import static wyil.lang.Code.*;
import static wyil.lang.CodeUtils.*;

public abstract class Codes {

	/**
	 * Provided to aid readability of client code.
	 */
	public final static int NULL_REG = -1;
	/**
	 * Provided to aid readability of client code.
	 */
	public final static int REG_0 = 0;
	/**
	 * Provided to aid readability of client code.
	 */
	public final static int REG_1 = 1;
	/**
	 * Provided to aid readability of client code.
	 */
	public final static int REG_2 = 2;
	/**
	 * Provided to aid readability of client code.
	 */
	public final static int REG_3 = 3;
	/**
	 * Provided to aid readability of client code.
	 */
	public final static int REG_4 = 4;
	/**
	 * Provided to aid readability of client code.
	 */
	public final static int REG_5 = 5;
	/**
	 * Provided to aid readability of client code.
	 */
	public final static int REG_6 = 6;

	/**
	 * Provided to aid readability of client code.
	 */
	public final static int REG_7 = 7;

	/**
	 * Provided to aid readability of client code.
	 */
	public final static int REG_8 = 8;

	/**
	 * Provided to aid readability of client code.
	 */
	public final static int REG_9 = 9;

	// ===============================================================
	// Bytecode Constructors
	// ===============================================================

	/**
	 * Construct an <code>assert</code> bytecode which represents a user-defined
	 * assertion check.
	 *
	 * @param message
	 *            --- message to report upon failure.
	 * @return
	 */
	public static Assert Assert(Collection<Code> bytecodes) {
		return new Assert(bytecodes);
	}

	/**
	 * Construct an <code>assume</code> bytecode which represents a user-defined
	 * assumption.
	 *
	 * @param message
	 *            --- message to report upon failure.
	 * @return
	 */
	public static Assume Assume(Collection<Code> bytecodes) {
		return new Assume(bytecodes);
	}

	public static BinaryOperator BinaryOperator(Type type, int target, int leftOperand,
			int rightOperand, BinaryOperatorKind op) {
		return new BinaryOperator(type, target, leftOperand,
				rightOperand, op);
	}

	/**
	 * Construct a <code>const</code> bytecode which loads a given constant onto
	 * the stack.
	 *
	 * @param afterType
	 *            --- record type.
	 * @param field
	 *            --- field to write.
	 * @return
	 */
	public static Const Const(int target, Constant constant) {
		return new Const(target, constant);
	}

	/**
	 * Construct a <code>copy</code> bytecode which copies the value from a
	 * given operand register into a given target register.
	 *
	 * @param type
	 *            --- record type.
	 * @param reg
	 *            --- reg to load.
	 * @return
	 */
	public static Assign Assign(Type type, int target, int operand) {
		return new Assign(type, target, operand);
	}

	public static Convert Convert(Type from, int target, int operand, Type to) {
		return new Convert(from, target, operand, to);
	}

	public static final Debug Debug(int operand) {
		return new Debug(operand);
	}

	/**
	 * Construct a <code>fail</code> bytecode which halts execution by raising a
	 * fault.
	 *
	 * @param string
	 *            --- Message to give on error.
	 * @return
	 */
	public static Fail Fail() {
		return new Fail();
	}

	/**
	 * Construct a <code>fieldload</code> bytecode which reads a given field
	 * from a record of a given type.
	 *
	 * @param type
	 *            --- record type.
	 * @param field
	 *            --- field to load.
	 * @return
	 */
	public static FieldLoad FieldLoad(Type.EffectiveRecord type, int target,
			int operand, String field) {
		return new FieldLoad(type, target, operand, field);
	}

	/**
	 * Construct a <code>goto</code> bytecode which branches unconditionally to
	 * a given label.
	 *
	 * @param label
	 *            --- destination label.
	 * @return
	 */
	public static Goto Goto(String label) {
		return new Goto(label);
	}

	public static Invoke Invoke(Type.FunctionOrMethod fun, int target,
			Collection<Integer> operands, NameID name) {
		return new Invoke(fun, target, CodeUtils.toIntArray(operands), name);
	}

	public static Invoke Invoke(Type.FunctionOrMethod fun, int target,
			int[] operands, NameID name) {
		return new Invoke(fun, target, operands, name);
	}
	
	/**
	 * Construct an <code>invariant</code> bytecode which represents a user-defined
	 * loop invariant.
	 *
	 * @param message
	 *            --- message to report upon failure.
	 * @return
	 */
	public static Invariant Invariant(Collection<Code> bytecodes) {
		return new Invariant(bytecodes);
	}


	public static Lambda Lambda(Type.FunctionOrMethod fun, int target,
			Collection<Integer> operands, NameID name) {
		return new Lambda(fun, target, CodeUtils.toIntArray(operands), name);
	}

	public static Lambda Lambda(Type.FunctionOrMethod fun, int target,
			int[] operands, NameID name) {
		return new Lambda(fun, target, operands, name);
	}

	public static Not Not(int target, int operand) {
		return new Not(target, operand);
	}

	public static LengthOf LengthOf(Type.EffectiveArray type, int target,
			int operand) {
		return new LengthOf(type, target, operand);
	}

	public static Move Move(Type type, int target, int operand) {
		return new Move(type, target, operand);
	}

	/**
	 * Construct a <code>listgen</code> bytecode which constructs a new list
	 * initialised to a given length, with each element containing a given item.
	 *
	 * @param type
	 * @return
	 */
	public static ArrayGenerator ArrayGenerator(Type.Array type, int target,
			int element, int count) {
		return new ArrayGenerator(type, target, element, count);
	}
	
	/**
	 * Construct a <code>listload</code> bytecode which reads a value from a
	 * given index in a given list.
	 *
	 * @param type
	 *            --- list type.
	 * @return
	 */
	public static IndexOf IndexOf(Type.EffectiveArray type, int target,
			int leftOperand, int rightOperand) {
		return new IndexOf(type, target, leftOperand, rightOperand);
	}

	public static Loop Loop(int[] operands, Collection<Code> bytecodes) {
		return new Loop(operands,bytecodes);
	}

	public static Loop Loop(int[] operands, Code... bytecodes) {
		return new Loop(operands,bytecodes);
	}

	/**
	 * Construct a <code>NewArray</code> bytecode which constructs a new array and
	 * puts it on the stack.
	 *
	 * @param type
	 * @return
	 */
	public static NewArray NewArray(Type.Array type, int target,
			Collection<Integer> operands) {
		return new NewArray(type, target, CodeUtils.toIntArray(operands));
	}

	/**
	 * Construct a <code>NewArray</code> bytecode which constructs a new array and
	 * puts it on the stack.
	 *
	 * @param type
	 * @return
	 */
	public static NewArray NewArray(Type.Array type, int target, int[] operands) {
		return new NewArray(type, target, operands);
	}

	/**
	 * Construct a <code>newrecord</code> bytecode which constructs a new record
	 * and puts it on the stack.
	 *
	 * @param type
	 * @return
	 */
	public static NewRecord NewRecord(Type.Record type, int target,
			Collection<Integer> operands) {
		return new NewRecord(type, target, CodeUtils.toIntArray(operands));
	}

	public static NewRecord NewRecord(Type.Record type, int target,
			int[] operands) {
		return new NewRecord(type, target, operands);
	}

	/**
	 * Construct a return bytecode which does return a value and, hence, its
	 * type automatically defaults to void.
	 *
	 * @return
	 */
	public static Return Return() {
		return new Return(Type.T_VOID, Codes.NULL_REG);
	}

	/**
	 * Construct a return bytecode which reads a value from the operand register
	 * and returns it.
	 *
	 * @param type
	 *            --- type of the value to be returned (cannot be void).
	 * @param operand
	 *            --- register to read return value from.
	 * @return
	 */
	public static Return Return(Type type, int operand) {
		return new Return(type, operand);
	}

	public static If If(Type type, int leftOperand, int rightOperand,
			Comparator cop, String label) {
		return new If(type, leftOperand, rightOperand, cop, label);
	}

	public static IfIs IfIs(Type type, int leftOperand, Type rightOperand,
			String label) {
		return new IfIs(type, leftOperand, rightOperand, label);
	}

	public static IndirectInvoke IndirectInvoke(Type.FunctionOrMethod fun,
			int target, int operand, Collection<Integer> operands) {
		return new IndirectInvoke(fun, target, operand, CodeUtils
				.toIntArray(operands));
	}

	public static IndirectInvoke IndirectInvoke(Type.FunctionOrMethod fun,
			int target, int operand, int[] operands) {
		return new IndirectInvoke(fun, target, operand, operands);
	}

	public static Invert Invert(Type type, int target, int operand) {
		return new Invert(type, target, operand);
	}

	public static Label Label(String label) {
		return new Label(label);
	}

	public static final Nop Nop = new Nop();

	/**
	 * Construct a <code>switch</code> bytecode which pops a value off the
	 * stack, and switches to a given label based on it.
	 *
	 * @param type
	 *            --- value type to switch on.
	 * @param defaultLabel
	 *            --- target for the default case.
	 * @param cases
	 *            --- map from values to destination labels.
	 * @return
	 */
	public static Switch Switch(Type type, int operand, String defaultLabel,
			Collection<Pair<Constant, String>> cases) {
		return new Switch(type, operand, defaultLabel, cases);
	}
	
	public static NewObject NewObject(Type.Reference type, int target,
			int operand) {
		return new NewObject(type, target, operand);
	}

	public static Dereference Dereference(Type.Reference type, int target,
			int operand) {
		return new Dereference(type, target, operand);
	}


	public static Quantify Quantify(
			int startOperand, int endOperand, int indexOperand,
			int[] modifiedOperands, Collection<Code> bytecodes) {
		return new Quantify(startOperand, endOperand, indexOperand,
				modifiedOperands, bytecodes);
	}

	public static Quantify Quantify(
			int startOperand, int endOperand, int indexOperand, int[] modifiedOperands,
			Code... bytecodes) {
		return new Quantify(startOperand, endOperand, indexOperand,
				modifiedOperands, bytecodes);
	}

	
	public static Update Update(Type beforeType, int target,
			Collection<Integer> operands, int operand, Type afterType,
			Collection<String> fields) {
		return new Update(beforeType, target,
				CodeUtils.toIntArray(operands), operand, afterType, fields);
	}

	public static Update Update(Type beforeType, int target, int[] operands,
			int operand, Type afterType, Collection<String> fields) {
		return new Update(beforeType, target, operands, operand,
				afterType, fields);
	}

	public static UnaryOperator UnaryOperator(Type type, int target, int operand,
			UnaryOperatorKind uop) {
		return new UnaryOperator(type, target, operand, uop);
	}

	public static Void Void(Type type, int[] operands) {
		return new Void(type, operands);
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
	public enum BinaryOperatorKind {
		ADD(0) {
			public String toString() {
				return "add";
			}
		},
		SUB(1) {
			public String toString() {
				return "sub";
			}
		},
		MUL(2) {
			public String toString() {
				return "mul";
			}
		},
		DIV(3) {
			public String toString() {
				return "div";
			}
		},
		REM(4) {
			public String toString() {
				return "rem";
			}
		},
		BITWISEOR(5) {
			public String toString() {
				return "or";
			}
		},
		BITWISEXOR(6) {
			public String toString() {
				return "xor";
			}
		},
		BITWISEAND(7) {
			public String toString() {
				return "and";
			}
		},
		LEFTSHIFT(8) {
			public String toString() {
				return "shl";
			}
		},
		RIGHTSHIFT(9) {
			public String toString() {
				return "shr";
			}
		};
		public int offset;

		private BinaryOperatorKind(int offset) {
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
	public static final class BinaryOperator extends AbstractBinaryAssignable<Type> {
		public final BinaryOperatorKind kind;

		private BinaryOperator(Type type, int target, int lhs, int rhs,
				BinaryOperatorKind bop) {
			super(type, target, lhs, rhs);
			if (bop == null) {
				throw new IllegalArgumentException(
						"BinOp bop argument cannot be null");
			}
			this.kind = bop;
		}

		@Override
		public int opcode() {
			return OPCODE_add + kind.offset;
		}

		@Override
		public Code.Unit clone(int nTarget, int[] nOperands) {
			return BinaryOperator(type(), nTarget, nOperands[0], nOperands[1],
					kind);
		}

		public int hashCode() {
			return kind.hashCode() + super.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof BinaryOperator) {
				BinaryOperator bo = (BinaryOperator) o;
				return kind.equals(bo.kind) && super.equals(bo);
			}
			return false;
		}

		public String toString() {
			return kind + " %" + target() + " = %" + operand(0) + ", %"
					+ operand(1) + " : " + type();
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
	public static final class Convert extends AbstractUnaryAssignable<Type> {
		public final Type result;

		private Convert(Type from, int target, int operand, Type result) {
			super(from, target, operand);
			if (result == null) {
				throw new IllegalArgumentException(
						"Convert to argument cannot be null");
			}
			this.result = result;
		}

		public Code.Unit clone(int nTarget, int[] nOperands) {
			return Convert(type(), nTarget, nOperands[0], result);
		}

		public int opcode() {
			return OPCODE_convert;
		}

		public int hashCode() {
			return result.hashCode() + super.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof Convert) {
				Convert c = (Convert) o;
				return super.equals(c) && result.equals(c.result);
			}
			return false;
		}

		public String toString() {
			return "convert %" + target() + " = %" + operand(0) + " " + result
					+ " : " + type();
		}
	}

	/**
	 * Writes a constant value to a target register. This includes
	 * <i>integers</i>, <i>rationals</i>, <i>lists</i>, <i>sets</i>,
	 * <i>maps</i>, etc. For example, the following Whiley code:
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
	public static final class Const extends AbstractAssignable {
		public final Constant constant;

		private Const(int target, Constant constant) {
			super(target);
			this.constant = constant;
		}

		public int opcode() {
			return OPCODE_const;
		}

		@Override
		public void registers(java.util.Set<Integer> registers) {
			registers.add(target());
		}

		@Override
		public Code.Unit remap(Map<Integer, Integer> binding) {
			Integer nTarget = binding.get(target());
			if (nTarget != null) {
				return Const(nTarget, constant);
			}
			return this;
		}

		public Type assignedType() {
			return (Type) constant.type();
		}

		public int hashCode() {
			return constant.hashCode() + target();
		}

		public boolean equals(Object o) {
			if (o instanceof Const) {
				Const c = (Const) o;
				return constant.equals(c.constant) && target() == c.target();
			}
			return false;
		}

		public String toString() {
			return "const %" + target() + " = " + constant + " : "
					+ constant.type();
		}
	}

	/**
	 * Copy the contents from a given operand register into a given target
	 * register. For example, the following Whiley code:
	 *
	 * <pre>
	 * function f(int x) -> int:
	 *     x = x + 1
	 *     return x
	 * </pre>
	 *
	 * can be translated into the following WyIL code:
	 *
	 * <pre>
	 * function f(int x) -> int:
	 * body:
	 *     assign %1 = %0      : int
	 *     const %2 = 1        : int
	 *     add %0 = %1, %2     : int
	 *     return %0           : int
	 * </pre>
	 *
	 * Here we see that an initial assignment is made from register
	 * <code>%0</code> to register <code>%1</code>. In fact, this assignment is
	 * unecessary but is useful to illustrate the <code>assign</code> bytecode.
	 *
	 * <p>
	 * <b>NOTE:</b> on many architectures this operation may not actually clone
	 * the data in question. Rather, it may copy the <i>reference</i> to the
	 * data and then increment its <i>reference count</i>. This is to ensure
	 * efficient treatment of large compound structures (e.g. lists, sets, maps
	 * and records).
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Assign extends AbstractUnaryAssignable<Type> {

		private Assign(Type type, int target, int operand) {
			super(type, target, operand);
		}

		public int opcode() {
			return OPCODE_assign;
		}

		public Code.Unit clone(int nTarget, int[] nOperands) {
			return Assign(type(), nTarget, nOperands[0]);
		}

		public boolean equals(Object o) {
			if (o instanceof Assign) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			return "assign %" + target() + " = %" + operand(0) + " " + " : " + type();
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
	public static final class Debug extends AbstractUnaryOp<Type> {

		private Debug(int operand) {
			super(Type.Array(Type.T_INT,false), operand);
		}

		public int opcode() {
			return OPCODE_debug;
		}

		@Override
		public Code.Unit clone(int nOperand) {
			return Debug(nOperand);
		}

		public boolean equals(Object o) {
			return o instanceof Debug && super.equals(o);
		}

		public String toString() {
			return "debug %" + operand + " " + " : " + type;
		}
	}

	/**
	 * An abstract class representing either an <code>assert</code> or
	 * <code>assume</code> bytecode block.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static abstract class AssertOrAssume extends Code.Compound {
		private AssertOrAssume(Collection<Code> bytecodes) {
			super(bytecodes);
		}
	}
	/**
	 * Represents a block of bytecode instructions representing an assertion.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Assert extends AssertOrAssume {

		private Assert(Collection<Code> bytecodes) {
			super(bytecodes);
		}

		public int opcode() {
			return OPCODE_assertblock;
		}

		public String toString() {
			return "assert";
		}

		public int hashCode() {
			return bytecodes.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof Assert) {
				Assert f = (Assert) o;
				return bytecodes.equals(f.bytecodes);
			}
			return false;
		}

		@Override
		public Assert clone() {
			return new Assert(bytecodes);
		}
	}

	/**
	 * Represents a block of bytecode instructions representing an assumption.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Assume extends AssertOrAssume {

		private Assume(Collection<Code> bytecodes) {
			super(bytecodes);
		}

		public int opcode() {
			return OPCODE_assumeblock;
		}

		public String toString() {
			return "assume ";
		}
		
		public int hashCode() {
			return bytecodes.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof Assume) {
				Assume f = (Assume) o;
				return bytecodes.equals(f.bytecodes);
			}
			return false;
		}

		@Override
		public Assume clone() {
			return new Assume(bytecodes);
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
	public static final class Fail extends Code.Unit {
		private Fail() {		
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
	public static final class FieldLoad extends
			AbstractUnaryAssignable<Type.EffectiveRecord> {
		public final String field;

		private FieldLoad(Type.EffectiveRecord type, int target, int operand,
				String field) {
			super(type, target, operand);
			if (field == null) {
				throw new IllegalArgumentException(
						"FieldLoad field argument cannot be null");
			}
			this.field = field;
		}

		@Override
		public Code.Unit clone(int nTarget, int[] nOperands) {
			return FieldLoad(type(), nTarget, nOperands[0], field);
		}

		public int opcode() {
			return OPCODE_fieldload;
		}

		public int hashCode() {
			return super.hashCode() + field.hashCode();
		}

		public Type fieldType() {
			return type().fields().get(field);
		}

		public Type assignedType() {
			return type().fields().get(field);
		}

		public boolean equals(Object o) {
			if (o instanceof FieldLoad) {
				FieldLoad i = (FieldLoad) o;
				return super.equals(i) && field.equals(i.field);
			}
			return false;
		}

		public String toString() {
			return "fieldload %" + target() + " = %" + operand(0) + " " + field
					+ " : " + type();
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
	public static final class Goto extends Code.Unit {
		public final String target;

		private Goto(String target) {
			this.target = target;
		}

		public int opcode() {
			return OPCODE_goto;
		}

		public Goto relabel(Map<String, String> labels) {
			String nlabel = labels.get(target);
			if (nlabel == null) {
				return this;
			} else {
				return Goto(nlabel);
			}
		}

		public int hashCode() {
			return target.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof Goto) {
				return target.equals(((Goto) o).target);
			}
			return false;
		}

		public String toString() {
			return "goto " + target;
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
	public static final class If extends AbstractBinaryOp<Type> {
		public final String target;
		public final Comparator op;

		private If(Type type, int leftOperand, int rightOperand, Comparator op,
				String target) {
			super(type, leftOperand, rightOperand);
			if (op == null) {
				throw new IllegalArgumentException(
						"IfGoto op argument cannot be null");
			}
			if (target == null) {
				throw new IllegalArgumentException(
						"IfGoto target argument cannot be null");
			}
			this.op = op;
			this.target = target;
		}

		public If relabel(Map<String, String> labels) {
			String nlabel = labels.get(target);
			if (nlabel == null) {
				return this;
			} else {
				return If(type, leftOperand, rightOperand, op, nlabel);
			}
		}

		public int opcode() {
			return OPCODE_ifeq + op.offset;
		}

		@Override
		public Code.Unit clone(int nLeftOperand, int nRightOperand) {
			return If(type, nLeftOperand, nRightOperand, op, target);
		}

		public int hashCode() {
			return super.hashCode() + op.hashCode() + target.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof If) {
				If ig = (If) o;
				return op == ig.op && target.equals(ig.target)
						&& super.equals(ig);
			}
			return false;
		}

		public String codeString() {
			return null;
		}

		public String toString() {
			return "if" + op + " %" + leftOperand + ", %" + rightOperand
					+ " goto " + target + " : " + type;
		}
	}

	/**
	 * Represents a comparison operator (e.g. '==','!=',etc) that is provided to
	 * a <code>IfGoto</code> bytecode.
	 *
	 * @author David J. Pearce
	 *
	 */
	public enum Comparator {
		EQ(0) {
			public String toString() {
				return "eq";
			}
		},
		NEQ(1) {
			public String toString() {
				return "ne";
			}
		},
		LT(2) {
			public String toString() {
				return "lt";
			}
		},
		LTEQ(3) {
			public String toString() {
				return "le";
			}
		},
		GT(4) {
			public String toString() {
				return "gt";
			}
		},
		GTEQ(5) {
			public String toString() {
				return "ge";
			}
		};
		public int offset;

		private Comparator(int offset) {
			this.offset = offset;
		}
	};

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
	public static final class IfIs extends AbstractUnaryOp<Type> {
		public final String target;
		public final Type rightOperand;

		private IfIs(Type type, int leftOperand, Type rightOperand,
				String target) {
			super(type, leftOperand);
			if (rightOperand == null) {
				throw new IllegalArgumentException(
						"IfIs test argument cannot be null");
			}
			if (target == null) {
				throw new IllegalArgumentException(
						"IfIs target argument cannot be null");
			}
			this.target = target;
			this.rightOperand = rightOperand;
		}

		public int opcode() {
			return OPCODE_ifis;
		}

		public IfIs relabel(Map<String, String> labels) {
			String nlabel = labels.get(target);
			if (nlabel == null) {
				return this;
			} else {
				return IfIs(type, operand, rightOperand, nlabel);
			}
		}

		@Override
		public Code.Unit clone(int nOperand) {
			return IfIs(type, nOperand, rightOperand, target);
		}

		public int hashCode() {
			return type.hashCode() + +target.hashCode() + super.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof IfIs) {
				IfIs ig = (IfIs) o;
				return super.equals(o) && rightOperand.equals(ig.rightOperand)
						&& target.equals(ig.target);
			}
			return false;
		}

		public String toString() {
			return "ifis" + " %" + operand + ", " + rightOperand + " goto "
					+ target + " : " + type;
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
	public static final class IndirectInvoke extends
			AbstractNaryAssignable<Type.FunctionOrMethod> {

		/**
		 * Construct an indirect invocation bytecode which assigns to an
		 * optional target register the result from indirectly invoking a
		 * function in a given operand with a given set of parameter operands.
		 *
		 * @param type
		 * @param target Register (optional) to which result of invocation is assigned.
		 * @param operand Register holding function point through which indirect invocation is made.
		 * @param operands Registers holding parameters for the invoked function
		 */
		private IndirectInvoke(Type.FunctionOrMethod type, int target,
				int operand, int[] operands) {
			super(type, target, append(operand,operands));
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
			return Arrays.copyOfRange(operands(),1,operands().length);
		}

		public int opcode() {
			if (type() instanceof Type.Function) {
				if (target() != Codes.NULL_REG) {
					return OPCODE_indirectinvokefn;
				} else {
					return OPCODE_indirectinvokefnv;
				}
			} else {
				if (target() != Codes.NULL_REG) {
					return OPCODE_indirectinvokemd;
				} else {
					return OPCODE_indirectinvokemdv;
				}
			}
		}

		@Override
		public Code.Unit clone(int nTarget, int[] nOperands) {
			return IndirectInvoke(type(), nTarget, nOperands[0],
					Arrays.copyOfRange(nOperands, 1, nOperands.length));
		}

		public boolean equals(Object o) {
			return o instanceof IndirectInvoke && super.equals(o);
		}

		public Type assignedType() {
			return type().returns().get(0);
		}

		public String toString() {
			if (target() != Codes.NULL_REG) {
				return "indirectinvoke " + target() + " = " + reference() + " "
						+ arrayToString(parameters()) + " : " + type();
			} else {
				return "indirectinvoke %" + reference() + " "
						+ arrayToString(parameters()) + " : " + type();
			}
		}
	}
	
	/**
	 * Represents a block of bytecode instructions representing an assertion.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Invariant extends Assert {

		private Invariant(Collection<Code> bytecodes) {
			super(bytecodes);
		}

		public int opcode() {
			return OPCODE_invariantblock;
		}

		public String toString() {
			return "invariant";
		}

		public int hashCode() {
			return bytecodes.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof Invariant) {
				Invariant f = (Invariant) o;
				return bytecodes.equals(f.bytecodes);
			}
			return false;
		}

		@Override
		public Invariant clone() {
			return new Invariant(bytecodes);
		}
	}

	/**
	 * Read a boolean value from the operand register, inverts it and writes the
	 * result to the target register. For example, the following Whiley code:
	 *
	 * <pre>
	 * function f(bool x) -> bool:
	 *     return !x
	 * </pre>
	 *
	 * can be translated into the following WyIL:
	 *
	 * <pre>
	 * function f(bool x) -> bool:
	 * body:
	 *     not %0 = %0     : int
	 *     return %0       : int
	 * </pre>
	 *
	 * This simply reads the parameter <code>x</code> stored in register
	 * <code>%0</code>, inverts it and then returns the inverted value.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Not extends AbstractUnaryAssignable<Type.Bool> {

		private Not(int target, int operand) {
			super(Type.T_BOOL, target, operand);
		}

		public int opcode() {
			return OPCODE_not;
		}

		@Override
		public Code.Unit clone(int nTarget, int[] nOperands) {
			return Not(nTarget, nOperands[0]);
		}

		public int hashCode() {
			return super.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof Not) {
				Not n = (Not) o;
				return super.equals(n);
			}
			return false;
		}

		public String toString() {
			return "not %" + target() + " = %" + operand(0) + " : " + type();
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
	public static final class Invoke extends
			AbstractNaryAssignable<Type.FunctionOrMethod> {
		public final NameID name;

		private Invoke(Type.FunctionOrMethod type, int target, int[] operands,
				NameID name) {
			super(type, target, operands);
			this.name = name;
		}

		public int opcode() {
			if (type() instanceof Type.Function) {
				if (target() != Codes.NULL_REG) {
					return OPCODE_invokefn;
				} else {
					return OPCODE_invokefnv;
				}
			} else {
				if (target() != Codes.NULL_REG) {
					return OPCODE_invokemd;
				} else {
					return OPCODE_invokemdv;
				}
			}
		}

		public Type assignedType() {
			return type().returns().get(0);
		}

		public int hashCode() {
			return name.hashCode() + super.hashCode();
		}

		@Override
		public Code.Unit clone(int nTarget, int[] nOperands) {
			return Invoke(type(), nTarget, nOperands, name);
		}

		public boolean equals(Object o) {
			if (o instanceof Invoke) {
				Invoke i = (Invoke) o;
				return name.equals(i.name) && super.equals(i);
			}
			return false;
		}

		public String toString() {
			if (target() != Codes.NULL_REG) {
				return "invoke %" + target() + " = " + arrayToString(operands())
						+ " " + name + " : " + type();
			} else {
				return "invoke %" + arrayToString(operands()) + " " + name
						+ " : " + type();
			}
		}
	}

	public static final class Lambda extends
			AbstractNaryAssignable<Type.FunctionOrMethod> {
		public final NameID name;

		private Lambda(Type.FunctionOrMethod type, int target, int[] operands,
				NameID name) {
			super(type, target, operands);
			this.name = name;
		}

		public int opcode() {
			if (type() instanceof Type.Function) {
				return OPCODE_lambdafn;
			} else {
				return OPCODE_lambdamd;
			}
		}

		public Type assignedType() {
			return type().returns().get(0);
		}

		public int hashCode() {
			return name.hashCode() + super.hashCode();
		}

		@Override
		public Code.Unit clone(int nTarget, int[] nOperands) {
			return Lambda(type(), nTarget, nOperands, name);
		}

		public boolean equals(Object o) {
			if (o instanceof Lambda) {
				Lambda i = (Lambda) o;
				return name.equals(i.name) && super.equals(i);
			}
			return false;
		}

		public String toString() {
			return "lambda %" + target() + " = " + arrayToString(operands()) + " "
					+ name + " : " + type();
		}
	}

	/**
	 * Represents the labelled destination of a branch or loop statement.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Label extends Code.Unit {
		public final String label;

		private Label(String label) {
			this.label = label;
		}

		public int opcode() {
			return -1;
		}

		public Label relabel(Map<String, String> labels) {
			String nlabel = labels.get(label);
			if (nlabel == null) {
				return this;
			} else {
				return Label(nlabel);
			}
		}

		public int hashCode() {
			return label.hashCode();
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
	 * Constructs a new array value from the values given by zero or more operand
	 * registers. The new list is then written into the target register. For
	 * example, the following Whiley code:
	 *
	 * <pre>
	 * function f(int x, int y, int z) -> int[]:
	 *     return [x,y,z]
	 * </pre>
	 *
	 * can be translated into the following WyIL code:
	 *
	 * <pre>
	 * function f(int x, int y, int z) -> int[]:
	 * body:
	 *    assign %4 = %0             : int
	 *    assign %5 = %1             : int
	 *    assign %6 = %2             : int
	 *    newlist %3 = (%4, %5, %6)  : int[]
	 *    return %3                  : int[]
	 * </pre>
	 *
	 * Writes the array value given by <code>[x,y,z]</code> into register
	 * <code>%3</code> and returns it.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class ArrayGenerator extends AbstractBinaryAssignable<Type.Array> {

		private ArrayGenerator(Type.Array type, int target, int element, int count) {
			super(type, target, element, count);
		}

		public int opcode() {
			return OPCODE_listgen;
		}

		protected Code.Unit clone(int nTarget, int[] nOperands) {
			return ArrayGenerator(type(), nTarget, nOperands[0],nOperands[1]);
		}

		public boolean equals(Object o) {
			if (o instanceof ArrayGenerator) {
				return super.equals(operands());
			}
			return false;
		}

		public String toString() {
			return "listgen %" + target() + " = [" + operand(0) + "; " + operand(1) + "]" + " : " + type();
		}
	}

	/**
	 * Reads an (effective) collection (i.e. a set, list or map) from the
	 * operand register, and writes its length into the target register. For
	 * example, the following Whiley code:
	 *
	 * <pre>
	 * function f(int[] ls) -> int:
	 *     return |ls|
	 * </pre>
	 *
	 * translates to the following WyIL code:
	 *
	 * <pre>
	 * function f(int[] ls) -> int:
	 * body:
	 *     lengthof %0 = %0   : int[]
	 *     return %0          : int
	 * </pre>
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class LengthOf extends
			AbstractUnaryAssignable<Type.EffectiveArray> {
		private LengthOf(Type.EffectiveArray type, int target, int operand) {
			super(type, target, operand);
		}

		public int opcode() {
			return OPCODE_lengthof;
		}

		protected Code.Unit clone(int nTarget, int[] nOperands) {
			return LengthOf(type(), nTarget, nOperands[0]);
		}

		public Type assignedType() {
			return Type.T_INT;
		}

		public boolean equals(Object o) {
			if (o instanceof LengthOf) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			return "lengthof %" + target() + " = %" + operand(0) + " : " + type();
		}
	}
	
	/**
	 * Reads an effective list or map from the source (left) operand register,
	 * and a key value from the key (right) operand register and returns the
	 * value associated with that key. If the key does not exist, then a fault
	 * is raised. For example, the following Whiley code:
	 *
	 * <pre>
	 * function f({int=>string} map, int key) -> string:
	 *     return map[key]
	 * </pre>
	 *
	 * can be translated into the following WyIL code:
	 *
	 * <pre>
	 * function f({int->string} map, int key) -> string:
	 * body:
	 *     assertky %1, %0 "invalid key"       : {int->string}
	 *     indexof %2 = %0, %1                 : {int->string}
	 *     return %2                          : string
	 * </pre>
	 *
	 * Here, we see the <code>assertky</code> bytecode is used to first check
	 * that the given key exists in <code>map</code>, otherwise a fault is
	 * raised.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class IndexOf extends
			AbstractBinaryAssignable<Type.EffectiveArray> {
		private IndexOf(Type.EffectiveArray type, int target,
				int sourceOperand, int keyOperand) {
			super(type, target, sourceOperand, keyOperand);
		}

		public int opcode() {
			return OPCODE_indexof;
		}

		protected Code.Unit clone(int nTarget, int[] nOperands) {
			return IndexOf(type(), nTarget, nOperands[0], nOperands[1]);
		}

		public Type assignedType() {
			return type().element();
		}

		public boolean equals(Object o) {
			if (o instanceof IndexOf) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			return "indexof %" + target() + " = %" + operand(0) + ", %"
					+ operand(1) + " : " + type();
		}
	}

	/**
	 * Moves the contents of a given operand register into a given target
	 * register. This is similar to an <code>assign</code> bytecode, except that
	 * the register's contents are <i>voided</i> afterwards. This guarantees
	 * that the register is no longer live, which is useful for determining the
	 * live ranges of registers in a function or method. For example, the
	 * following Whiley code:
	 *
	 * <pre>
	 * function f(int x, int y) -> int:
	 *     x = x + 1
	 *     return x
	 * </pre>
	 *
	 * can be translated into the following WyIL code:
	 *
	 * <pre>
	 * function f(int x, int y) -> int:
	 * body:
	 *     ifge %0, %1 goto blklab0  : int
	 *     move %0 = %1              : int
	 * .blklab0
	 *     return %0                 : int
	 * </pre>
	 *
	 * Here we see that when <code>x < y</code> the value of <code>y</code>
	 * (held in register <code>%1</code>) is <i>moved</i> into variable
	 * <code>x</code> (held in register <code>%0</code>). This is safe because
	 * register <code>%1</code> is no longer live at that point.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Move extends AbstractUnaryAssignable<Type> {

		private Move(Type type, int target, int operand) {
			super(type, target, operand);
		}

		public int opcode() {
			return OPCODE_move;
		}

		protected Code.Unit clone(int nTarget, int[] nOperands) {
			return Move(type(), nTarget, nOperands[0]);
		}

		public boolean equals(Object o) {
			if (o instanceof Move) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			return "move %" + target() + " = %" + operand(0) + " : " + type();
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
	public static class Loop extends Code.Compound {
		public final int[] modifiedOperands;

		private Loop(int[] modifies, Code... codes) {
			super(codes);
			this.modifiedOperands = modifies;
		}

		private Loop(int[] modifies, Collection<Code> codes) {
			super(codes);
			this.modifiedOperands = modifies;
		}

		public int opcode() {
			return OPCODE_loop;
		}

		@Override
		public Code.Compound remap(Map<Integer, Integer> binding) {
			int[] nOperands = remapOperands(binding, modifiedOperands);
			ArrayList<Code> bytecodes = this.bytecodes;

			for (int i = 0; i != bytecodes.size(); ++i) {
				Code code = bytecodes.get(i);
				Code nCode = code.remap(binding);
				if (code != nCode) {
					if (bytecodes == this.bytecodes) {
						bytecodes = new ArrayList<Code>(bytecodes);
					}
					bytecodes.set(i, nCode);
				}
			}

			if (nOperands != modifiedOperands || bytecodes != this.bytecodes) {
				return Loop(nOperands, bytecodes);
			} else {
				return this;
			}
		}

		public int hashCode() {
			return bytecodes.hashCode() + Arrays.hashCode(modifiedOperands);
		}

		public boolean equals(Object o) {
			if (o instanceof Loop) {
				Loop f = (Loop) o;
				return bytecodes.equals(f.bytecodes)
						&& Arrays.equals(modifiedOperands, f.modifiedOperands);
			}
			return false;
		}

		@Override
		public Loop clone() {
			return new Loop(modifiedOperands,bytecodes);
		}

		public String toString() {
			return "loop " + arrayToString(modifiedOperands);
		}
	}

	public static final class Quantify extends Loop {
		
		public final int startOperand;
		public final int endOperand;
		public final int indexOperand;

		private Quantify(int startOperand,int endOperand,
				int indexOperand, int[] modifies, Collection<Code> bytecodes) {
			super(modifies, bytecodes);
			this.startOperand = startOperand;
			this.endOperand = endOperand;
			this.indexOperand = indexOperand;
		}

		private Quantify(int startOperand, int endOperand, int indexOperand,
				int[] modifies, Code[] bytecodes) {
			super(modifies, bytecodes);
			this.startOperand = startOperand;
			this.endOperand = endOperand;
			this.indexOperand = indexOperand;
		}
		
		public int opcode() {
			return OPCODE_quantify;
		}
		
		@Override
		public void registers(java.util.Set<Integer> registers) {
			registers.add(indexOperand);
			registers.add(startOperand);
			registers.add(endOperand);
			super.registers(registers);
		}

		@Override
		public Code.Compound remap(Map<Integer, Integer> binding) {
			int[] nModifiedOperands = remapOperands(binding, modifiedOperands);
			ArrayList<Code> bytecodes = this.bytecodes;

			for (int i = 0; i != bytecodes.size(); ++i) {
				Code code = bytecodes.get(i);
				Code nCode = code.remap(binding);
				if (code != nCode) {
					if (bytecodes == this.bytecodes) {
						bytecodes = new ArrayList<Code>(bytecodes);
					}
					bytecodes.set(i, nCode);
				}
			}
			Integer nIndexOperand = binding.get(indexOperand);
			Integer nStartOperand = binding.get(startOperand);
			Integer nEndOperand = binding.get(endOperand);
			if (nStartOperand != null || nEndOperand != null || nIndexOperand != null
					|| nModifiedOperands != modifiedOperands || bytecodes != this.bytecodes) {
				nStartOperand = nStartOperand != null ? nStartOperand
						: startOperand;
				nEndOperand = nEndOperand != null ? nEndOperand
						: endOperand;
				nIndexOperand = nIndexOperand != null ? nIndexOperand
						: indexOperand;
				return Quantify(nStartOperand, nEndOperand, nIndexOperand,
						nModifiedOperands, bytecodes);
			} else {
				return this;
			}
		}
		
		public int hashCode() {
			return super.hashCode() + startOperand + endOperand + indexOperand
					+ Arrays.hashCode(modifiedOperands);
		}

		public boolean equals(Object o) {
			if (o instanceof Quantify) {
				Quantify f = (Quantify) o;
				return startOperand == f.startOperand
						&& endOperand == f.endOperand
						&& indexOperand == f.indexOperand
						&& Arrays.equals(modifiedOperands, f.modifiedOperands)
						&& bytecodes.equals(f.bytecodes);
			}
			return false;
		}

		public String toString() {
			return "quantify %" + indexOperand + " in %" + startOperand + "..%"
					+ endOperand + arrayToString(modifiedOperands);
		}
	}
	
	/**
	 * Represents a type which may appear on the left of an assignment
	 * expression. Arrays, Records and References are the
	 * only valid types for an lval.
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

		public UpdateIterator(Type type, int level, int[] operands,
				ArrayList<String> fields) {
			this.fields = fields;
			this.iter = type;
			this.index = level;
			this.operands = operands;
		}

		public LVal next() {
			Type raw = iter;
			index--;
			if (Type.isSubtype(Type.Reference(Type.T_ANY), iter)) {
				Type.Reference proc = Type.effectiveReference(iter);
				iter = proc.element();
				return new ReferenceLVal(proc);
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
				throw new IllegalArgumentException(
						"Invalid type for Update");
			}
		}

		public boolean hasNext() {
			return index > 0;
		}

		public void remove() {
			throw new UnsupportedOperationException(
					"UpdateIterator is unmodifiable");
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
	public static final class Update extends AbstractNaryAssignable<Type>
			implements Iterable<LVal> {
		public final Type afterType;
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
		private Update(Type beforeType, int target, int[] operands,
				int operand, Type afterType, Collection<String> fields) {
			super(beforeType, target, append(operands,operand));
			if (fields == null) {
				throw new IllegalArgumentException(
						"FieldStore fields argument cannot be null");
			}
			this.afterType = afterType;
			this.fields = new ArrayList<String>(fields);
		}

		// Helper used for clone()
		private Update(Type beforeType, int target, int[] operands,
				Type afterType, Collection<String> fields) {
			super(beforeType, target, operands);
			if (fields == null) {
				throw new IllegalArgumentException(
						"FieldStore fields argument cannot be null");
			}
			this.afterType = afterType;
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
			return operands()[operands().length-1];
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
			return Arrays.copyOf(operands(),operands().length-1);
		}

		public int level() {
			int base = -1; // because last operand is rhs
			if (type() instanceof Type.Reference) {
				base++;
			}
			return base + fields.size() + operands().length;
		}

		public Iterator<LVal> iterator() {
			return new UpdateIterator(afterType, level(), keys(), fields);
		}

		public Type assignedType() {
			return afterType;
		}

		/**
		 * Extract the type for the right-hand side of this assignment.
		 *
		 * @return
		 */
		public Type rhs() {
			Type iter = afterType;

			int fieldIndex = 0;
			for (int i = 0; i != level(); ++i) {
				if (Type.isSubtype(Type.Reference(Type.T_ANY), iter)) {
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
					throw new IllegalArgumentException(
							"Invalid type for Update");
				}
			}
			return iter;
		}

		@Override
		public final Code.Unit clone(int nTarget, int[] nOperands) {
			return Update(type(), nTarget,
					Arrays.copyOf(nOperands, nOperands.length - 1),
					nOperands[nOperands.length - 1], afterType, fields);
		}

		public boolean equals(Object o) {
			if (o instanceof Update) {
				Update i = (Update) o;
				return super.equals(o) && afterType.equals(i.afterType)
						&& fields.equals(i.fields);
			}
			return false;
		}

		public String toString() {
			String r = "%" + target();
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
			return "update " + r + " = %" + result() + " : " + type() + " -> "
					+ afterType;
		}
	}

	/**
	 * Constructs a new record value from the values of zero or more operand
	 * register, each of which is associated with a field name. The new record
	 * value is then written into the target register. For example, the
	 * following Whiley code:
	 *
	 * <pre>
	 * type Point is {real x, real y}
	 *
	 * function f(real x, real y) -> Point:
	 *     return {x: x, y: x}
	 * </pre>
	 *
	 * can be translated into the following WyIL:
	 *
	 * <pre>
	 * function f(real x, real y) -> Point:
	 * body:
	 *     assign %3 = %0         : real
	 *     assign %4 = %0         : real
	 *     newrecord %2 (%3, %4)  : {real x,real y}
	 *     return %2              : {real x,real y}
	 * </pre>
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class NewRecord extends
			AbstractNaryAssignable<Type.Record> {
		private NewRecord(Type.Record type, int target, int[] operands) {
			super(type, target, operands);
		}

		protected Code.Unit clone(int nTarget, int[] nOperands) {
			return NewRecord(type(), nTarget, nOperands);
		}

		public int opcode() {
			return OPCODE_newrecord;
		}

		public boolean equals(Object o) {
			if (o instanceof NewRecord) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			return "newrecord %" + target() + " = " + arrayToString(operands())
					+ " : " + type();
		}
	}

	
	/**
	 * Constructs a new array value from the values given by zero or more operand
	 * registers. The new list is then written into the target register. For
	 * example, the following Whiley code:
	 *
	 * <pre>
	 * function f(int x, int y, int z) -> int[]:
	 *     return [x,y,z]
	 * </pre>
	 *
	 * can be translated into the following WyIL code:
	 *
	 * <pre>
	 * function f(int x, int y, int z) -> int[]:
	 * body:
	 *    assign %4 = %0             : int
	 *    assign %5 = %1             : int
	 *    assign %6 = %2             : int
	 *    newlist %3 = (%4, %5, %6)  : int[]
	 *    return %3                  : int[]
	 * </pre>
	 *
	 * Writes the array value given by <code>[x,y,z]</code> into register
	 * <code>%3</code> and returns it.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class NewArray extends AbstractNaryAssignable<Type.Array> {

		private NewArray(Type.Array type, int target, int[] operands) {
			super(type, target, operands);
		}

		public int opcode() {
			return OPCODE_newlist;
		}

		protected Code.Unit clone(int nTarget, int[] nOperands) {
			return NewArray(type(), nTarget, nOperands);
		}

		public boolean equals(Object o) {
			if (o instanceof NewArray) {
				return super.equals(operands());
			}
			return false;
		}

		public String toString() {
			return "newlist %" + target() + " = " + arrayToString(operands())
					+ " : " + type();
		}
	}

	/**
	 * Represents a no-operation bytecode which, as the name suggests, does
	 * nothing.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Nop extends Code.Unit {
		private Nop() {
		}

		@Override
		public int opcode() {
			return OPCODE_nop;
		}

		public String toString() {
			return "nop";
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
	 * Here, the
	 * <code>return<code> bytecode returns the value of its operand register.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Return extends AbstractUnaryOp<Type> {

		private Return(Type type, int operand) {
			super(type, operand);
			if (type == Type.T_VOID && operand != Codes.NULL_REG) {
				throw new IllegalArgumentException(
						"Return with void type cannot have target register.");
			} else if (type != Type.T_VOID && operand == Codes.NULL_REG) {
				throw new IllegalArgumentException(
						"Return with non-void type must have target register.");
			}
		}

		@Override
		public int opcode() {
			if (type == Type.T_VOID) {
				return OPCODE_returnv;
			} else {
				return OPCODE_return;
			}
		}

		public Code.Unit clone(int nOperand) {
			return new Return(type, nOperand);
		}

		public boolean equals(Object o) {
			if (o instanceof Return) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			if (operand != Codes.NULL_REG) {
				return "return %" + operand + " : " + type;
			} else {
				return "return";
			}
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
	public static final class Switch extends AbstractUnaryOp<Type> {
		public final ArrayList<Pair<Constant, String>> branches;
		public final String defaultTarget;

		Switch(Type type, int operand, String defaultTarget,
				Collection<Pair<Constant, String>> branches) {
			super(type, operand);
			this.branches = new ArrayList<Pair<Constant, String>>(branches);
			this.defaultTarget = defaultTarget;
		}

		@Override
		public int opcode() {
			return OPCODE_switch;
		}

		public Switch relabel(Map<String, String> labels) {
			ArrayList<Pair<Constant, String>> nbranches = new ArrayList();
			for (Pair<Constant, String> p : branches) {
				String nlabel = labels.get(p.second());
				if (nlabel == null) {
					nbranches.add(p);
				} else {
					nbranches.add(new Pair(p.first(), nlabel));
				}
			}

			String nlabel = labels.get(defaultTarget);
			if (nlabel == null) {
				return Switch(type, operand, defaultTarget, nbranches);
			} else {
				return Switch(type, operand, nlabel, nbranches);
			}
		}

		public int hashCode() {
			return type.hashCode() + operand + defaultTarget.hashCode()
					+ branches.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof Switch) {
				Switch ig = (Switch) o;
				return operand == ig.operand
						&& defaultTarget.equals(ig.defaultTarget)
						&& branches.equals(ig.branches) && type.equals(ig.type);
			}
			return false;
		}

		public String toString() {
			String table = "";
			boolean firstTime = true;
			for (Pair<Constant, String> p : branches) {
				if (!firstTime) {
					table += ", ";
				}
				firstTime = false;
				table += p.first() + "->" + p.second();
			}
			table += ", *->" + defaultTarget;
			return "switch %" + operand + " " + table;
		}

		@Override
		public Code.Unit clone(int nOperand) {
			return new Switch(type, nOperand, defaultTarget, branches);
		}

	}

	/**
	 * Corresponds to a bitwise inversion operation, which reads a byte value
	 * from the operand register, inverts it and writes the result to the target
	 * resgister. For example, the following Whiley code:
	 *
	 * <pre>
	 * function f(byte x) -> byte:
	 *    return ~x
	 * </pre>
	 *
	 * can be translated into the following WyIL code:
	 *
	 * <pre>
	 * function f(byte x) -> byte:
	 * body:
	 *     invert %0 = %0   : byte
	 *     return %0        : byte
	 * </pre>
	 *
	 * Here, the expression <code>~x</code> generates an <code>invert</code>
	 * bytecode.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Invert extends AbstractUnaryAssignable<Type> {

		private Invert(Type type, int target, int operand) {
			super(type, target, operand);
		}

		@Override
		public int opcode() {
			return OPCODE_invert;
		}

		protected Code.Unit clone(int nTarget, int[] nOperands) {
			return Invert(type(), nTarget, nOperands[0]);
		}

		public boolean equals(Object o) {
			if (o instanceof Invert) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			return "invert %" + target() + " = %" + operand(0) + " : " + type();
		}
	}

	/**
	 * Instantiate a new object from the value in a given operand register, and
	 * write the result (a reference to that object) to a given target register.
	 * For example, the following Whiley code:
	 *
	 * <pre>
	 * type PointObj as &{real x, real y}
	 *
	 * method f(real x, real y) -> PointObj:
	 *     return new {x: x, y: y}
	 * </pre>
	 *
	 * can be translated into the following WyIL code:
	 *
	 * <pre>
	 * method f(int x, int y) -> &{real x,real y}:
	 * body:
	 *     newrecord %2 = (%0, %1)  : {real x,real y}
	 *     newobject %2 = %2        : ref {real x,real y}
	 *     return %2                : ref {real x,real y}
	 * </pre>
	 *
	 * <b>NOTE:</b> objects are unlike other data types in WyIL, in that they
	 * represent mutable state allocated on a heap. Thus, changes to an object
	 * within a method are visible to those outside of the method.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class NewObject extends
			AbstractUnaryAssignable<Type.Reference> {

		private NewObject(Type.Reference type, int target, int operand) {
			super(type, target, operand);
		}

		@Override
		public int opcode() {
			return OPCODE_newobject;
		}

		protected Code.Unit clone(int nTarget, int[] nOperands) {
			return NewObject(type(), nTarget, nOperands[0]);
		}

		public boolean equals(Object o) {
			if (o instanceof NewObject) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			return "newobject %" + target() + " = %" + operand(0) + " : " + type();
		}
	}

	/**
	 * Reads a reference value from the operand register, dereferences it (i.e.
	 * extracts the value it refers to) and writes this to the target register.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Dereference extends
			AbstractUnaryAssignable<Type.Reference> {

		private Dereference(Type.Reference type, int target, int operand) {
			super(type, target, operand);
		}

		@Override
		public int opcode() {
			return OPCODE_dereference;
		}

		public Type assignedType() {
			return type().element();
		}

		protected Code.Unit clone(int nTarget, int[] nOperands) {
			return Dereference(type(), nTarget, nOperands[0]);
		}

		public boolean equals(Object o) {
			if (o instanceof Dereference) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			return "deref %" + target() + " = %" + operand(0) + " : " + type();
		}
	}

	public enum UnaryOperatorKind {
		NEG(0) {
			public String toString() {
				return "neg";
			}
		},

		NUMERATOR(1) {
			public String toString() {
				return "num";
			}
		},

		DENOMINATOR(2) {
			public String toString() {
				return "den";
			}
		};
		public final int offset;

		private UnaryOperatorKind(int offset) {
			this.offset = offset;
		}
	};

	/**
	 * Read a number (int or real) from the operand register, perform a unary
	 * arithmetic operation on it (e.g. negation) and writes the result to the
	 * target register. For example, the following Whiley code:
	 *
	 * <pre>
	 * function f(int x) -> int:
	 *     return -x
	 * </pre>
	 *
	 * can be translated into the following WyIL:
	 *
	 * <pre>
	 * function f(int x) -> int:
	 * body:
	 *     neg %0 = %0     : int
	 *     return %0       : int
	 * </pre>
	 *
	 * This simply reads the parameter <code>x</code> stored in register
	 * <code>%0</code>, negates it and then returns the negated value.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class UnaryOperator extends AbstractUnaryAssignable<Type> {
		public final UnaryOperatorKind kind;

		private UnaryOperator(Type type, int target, int operand, UnaryOperatorKind uop) {
			super(type, target, operand);
			if (uop == null) {
				throw new IllegalArgumentException(
						"UnaryArithOp bop argument cannot be null");
			}
			this.kind = uop;
		}

		@Override
		public int opcode() {
			return OPCODE_neg + kind.offset;
		}

		@Override
		public Code.Unit clone(int nTarget, int[] nOperands) {
			return UnaryOperator(type(), nTarget, nOperands[0], kind);
		}

		public int hashCode() {
			return kind.hashCode() + super.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof UnaryOperator) {
				UnaryOperator bo = (UnaryOperator) o;
				return kind.equals(bo.kind) && super.equals(bo);
			}
			return false;
		}

		public String toString() {
			return kind + " %" + target() + " = %" + operand(0) + " : " + type();
		}
	}

	/**
	 * The void bytecode is used to indicate that the given register(s) are no
	 * longer live. This is useful for communicating information to the memory
	 * management system about which values could in principle be collected.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Void extends AbstractNaryAssignable<Type> {

		private Void(Type type, int[] operands) {
			super(type, Codes.NULL_REG, operands);
		}

		@Override
		public int opcode() {
			return OPCODE_void;
		}

		protected Code.Unit clone(int nTarget, int[] nOperands) {
			return Void(type(), nOperands);
		}

		public Type assignedType() {
			return Type.T_VOID;
		}

		public boolean equals(Object o) {
			if (o instanceof Void) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			return "void " + arrayToString(operands());
		}
	}

	// =============================================================
	// Helpers
	// =============================================================
	private static int[] append(int[] operands, int operand) {
		int[] noperands = Arrays.copyOf(operands, operands.length+1);
		noperands[operands.length] = operand;
		return noperands;
	}

	private static int[] append(int operand, int[] operands) {
		int[] noperands = new int[operands.length+1];
		System.arraycopy(operands,0,noperands,1,operands.length);
		noperands[0] = operand;
		return noperands;
	}
}
