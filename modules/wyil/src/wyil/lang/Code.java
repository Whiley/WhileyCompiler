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

import wycc.lang.SyntacticElement;
import static wyil.lang.CodeUtils.*;

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
 * function sum([int] data) -> int:
 *    int r = 0
 *    for item in data:
 *       r = r + item
 *    return r
 * </pre>
 *
 * This function is compiled into the following WyIL bytecode:
 *
 * <pre>
 * function sum([int] data) -> int:
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
 * <i>registers</i> used and <i>pool items</i> used (e.g. names, constants,
 * etc). The generic organisation of a bytecode is as follows:
 * </p>
 *
 * <pre>
 * +--------+-----------+------------+
 * | opcode | registers | pool items |
 * +--------+-----------+------------+
 * </pre>
 * <p>
 * The opcode is currently always 1 byte, whilst the remainder varies between
 * instructions. Many bytecodes assign to a <i>target</i> register and read
 * values from <i>operand</i> registers. Such bytecodes are organised as
 * follows:
 * </p>
 *
 * <pre>
 * +--------+--------+----------+------------+
 * | opcode | target | operands | pool items |
 * +--------+--------+----------+------------+
 * </pre>
 *
 *
 * @author David J. Pearce
 */
public interface Code {

	// ===============================================================
	// Abstract Methods
	// ===============================================================

	/**
	 * Determine which registers are used in this bytecode. This can be used,
	 * for example, to determine the size of the register file required for a
	 * given method.
	 *
	 * @param register
	 */
	public void registers(java.util.Set<Integer> register);

	/**
	 * Remaps all registers according to a given binding. Registers not
	 * mentioned in the binding retain their original value. Note, if the
	 * returned bytecode is unchanged then it must be <code>this</code>.
	 *
	 * @param binding
	 *            --- map from (existing) registers to (new) registers.
	 * @return
	 */
	public Code remap(Map<Integer, Integer> binding);

	/**
	 * Return the opcode value of this bytecode.
	 * @return
	 */
	public abstract int opcode();

	// ===============================================================
	// Subtypes
	// ===============================================================

	/**
	 * A Unit bytecode is one which does not contain other bytecodes.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static abstract class Unit implements Code {

		@Override
		public void registers(java.util.Set<Integer> register) {
			// default implementation does nothing
		}

		@Override
		public Code.Unit remap(Map<Integer, Integer> binding) {
			return this;
		}
	}

	/**
	 * A compound bytecode represents a bytecode that contains sequence of zero
	 * or more bytecodes. For example, the loop bytecode contains its loop body.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static abstract class Compound extends CodeBlock implements Code {

		public Compound(Code... bytecodes) {
			super(bytecodes);
		}

		public Compound(Collection<Code> bytecodes) {
			super(bytecodes);
		}

		@Override
		public void registers(java.util.Set<Integer> register) {
			for(int i=0;i!=size();++i) {
				get(i).registers(register);
			}
		}

		@Override
		public Code.Compound remap(Map<Integer, Integer> binding) {
			Code.Compound block = clone();
			for(int i=0;i!=size();++i) {
				Code code = get(i);
				block.set(i,code.remap(binding));
			}
			return block;
		}

		public abstract Code.Compound clone();
	}



	// ===============================================================C
	// Abstract Bytecodes
	// ===============================================================

	/**
	 * Represents the set of all bytecodes which assign a result to a single
	 * target register.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static abstract class AbstractAssignable extends Code.Unit {
		private final int target;

		/**
		 * Construct an abstract bytecode which assigns to a given target
		 * register.
		 *
		 * @param target
		 */
		public AbstractAssignable(int target) {
			this.target = target;
		}

		/**
		 * Return the type of value assigned to the target register by this
		 * bytecode.
		 *
		 * @return
		 */
		public abstract Type assignedType();

		/**
		 * Return the target register assigned by this bytecode.
		 *
		 * @return
		 */
		public int target() {
			return target;
		}
	}

	/**
	 * Represents the set of all bytcodes which take an arbitrary number of
	 * register operands and write a result to the target register.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 *            --- the type associated with this bytecode.
	 */
	public static abstract class AbstractNaryAssignable<T> extends
			AbstractAssignable {
		private final T type;
		private final int[] operands;

		public AbstractNaryAssignable(T type, int target, int... operands) {
			super(target);
			if (type == null) {
				throw new IllegalArgumentException(
						"AbstractBinOp type argument cannot be null");
			}
			this.type = type;
			this.operands = operands;
		}

		@Override
		public final void registers(java.util.Set<Integer> registers) {
			if (target() >= 0) {
				registers.add(target());
			}
			for (int i = 0; i != operands().length; ++i) {
				registers.add(operands()[i]);
			}
		}

		@Override
		public final Code.Unit remap(Map<Integer, Integer> binding) {
			Integer nTarget = binding.get(target());
			int[] nOperands = remapOperands(binding, operands());
			if (nTarget != null || nOperands != operands()) {
				nTarget = nTarget != null ? nTarget : target();
				return clone(nTarget, nOperands);
			}
			return this;
		}

		public Type assignedType() {
			return (Type) this.type();
		}

		protected abstract Code.Unit clone(int nTarget, int[] nOperands);

		public int hashCode() {
			return type().hashCode() + target() + Arrays.hashCode(operands());
		}

		public boolean equals(Object o) {
			if (o instanceof AbstractNaryAssignable) {
				AbstractNaryAssignable bo = (AbstractNaryAssignable) o;
				return target() == bo.target()
						&& Arrays.equals(operands(), bo.operands())
						&& type().equals(bo.type());
			}
			return false;
		}

		public T type() {
			return type;
		}

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
	}

	/**
	 * Represents the set of all bytcodes which take a single register operand
	 * and assign a result to the target register.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 *            --- the type associated with this bytecode.
	 */
	public static abstract class AbstractUnaryAssignable<T> extends
			AbstractNaryAssignable<T> {

		public AbstractUnaryAssignable(T type, int target, int operand) {
			super(type,target,operand);
			if (type == null) {
				throw new IllegalArgumentException(
						"AbstractUnOp type argument cannot be null");
			}
		}
	}

	/**
	 * Represents the set of all bytcodes which take a single register operand,
	 * and do not assign to a target register.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 *            --- the type associated with this bytecode.
	 */
	public static abstract class AbstractUnaryOp<T> extends Code.Unit {
		public final T type;
		public final int operand;

		public AbstractUnaryOp(T type, int operand) {
			if (type == null) {
				throw new IllegalArgumentException(
						"AbstractUnaryOp type argument cannot be null");
			}
			this.type = type;
			this.operand = operand;
		}

		@Override
		public final void registers(java.util.Set<Integer> registers) {
			registers.add(operand);
		}

		@Override
		public final Code.Unit remap(Map<Integer, Integer> binding) {
			Integer nOperand = binding.get(operand);
			if (nOperand != null) {
				return clone(nOperand);
			}
			return this;
		}

		protected abstract Code.Unit clone(int nOperand);

		public int hashCode() {
			return type.hashCode() + operand;
		}

		public boolean equals(Object o) {
			if (o instanceof AbstractUnaryOp) {
				AbstractUnaryOp bo = (AbstractUnaryOp) o;
				return operand == bo.operand && type.equals(bo.type);
			}
			return false;
		}
	}

	/**
	 * Represents the set of all bytcodes which take two register operands and
	 * write a result to the target register.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 *            --- the type associated with this bytecode.
	 */
	public static abstract class AbstractBinaryAssignable<T> extends
			AbstractNaryAssignable<T> {

		public AbstractBinaryAssignable(T type, int target, int leftOperand,
				int rightOperand) {
			super(type,target,leftOperand,rightOperand);
		}
	}

	/**
	 * Represents the set of all bytcodes which take an arbitrary number of
	 * register operands and write a result to the target register.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 *            --- the type associated with this bytecode.
	 */

	/**
	 * Represents the set of all bytcodes which take two register operands and
	 * perform a comparison of their values.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 *            --- the type associated with this bytecode.
	 */
	public static abstract class AbstractBinaryOp<T> extends Code.Unit {
		public final T type;
		public final int leftOperand;
		public final int rightOperand;

		public AbstractBinaryOp(T type, int leftOperand, int rightOperand) {
			if (type == null) {
				throw new IllegalArgumentException(
						"AbstractBinCond type argument cannot be null");
			}
			this.type = type;
			this.leftOperand = leftOperand;
			this.rightOperand = rightOperand;
		}

		@Override
		public final void registers(java.util.Set<Integer> registers) {
			registers.add(leftOperand);
			registers.add(rightOperand);
		}

		@Override
		public final Code.Unit remap(Map<Integer, Integer> binding) {
			Integer nLeftOperand = binding.get(leftOperand);
			Integer nRightOperand = binding.get(rightOperand);
			if (nLeftOperand != null || nRightOperand != null) {
				nLeftOperand = nLeftOperand != null ? nLeftOperand
						: leftOperand;
				nRightOperand = nRightOperand != null ? nRightOperand
						: rightOperand;
				return clone(nLeftOperand, nRightOperand);
			}
			return this;
		}

		protected abstract Code.Unit clone(int nLeftOperand, int nRightOperand);

		public int hashCode() {
			return type.hashCode() + leftOperand + rightOperand;
		}

		public boolean equals(Object o) {
			if (o instanceof AbstractBinaryOp) {
				AbstractBinaryOp bo = (AbstractBinaryOp) o;
				return leftOperand == bo.leftOperand
						&& rightOperand == bo.rightOperand
						&& type.equals(bo.type);
			}
			return false;
		}
	}


	// =========================================================================
	// Opcodes
	// =========================================================================

	public static final int FMT_SHIFT        = 5;
	public static final int FMT_MASK         = 7 << FMT_SHIFT;

	public static final int FMT_EMPTY        = 0 << FMT_SHIFT;
	public static final int FMT_UNARYOP      = 1 << FMT_SHIFT;
	public static final int FMT_UNARYASSIGN  = 2 << FMT_SHIFT;
	public static final int FMT_BINARYOP     = 3 << FMT_SHIFT;
	public static final int FMT_BINARYASSIGN = 4 << FMT_SHIFT;
	public static final int FMT_NARYOP       = 5 << FMT_SHIFT;
	public static final int FMT_NARYASSIGN   = 6 << FMT_SHIFT;
	public static final int FMT_OTHER        = 7 << FMT_SHIFT;

	// =========================================================================
	// Empty Bytecodes
	// =========================================================================
	public static final int OPCODE_nop      = 0 + FMT_EMPTY;
	public static final int OPCODE_returnv  = 1 + FMT_EMPTY;
	public static final int OPCODE_const    = 2 + FMT_EMPTY; // +CONSTIDX
	public static final int OPCODE_goto     = 3 + FMT_EMPTY; // +INT
	public static final int OPCODE_fail     = 4 + FMT_EMPTY;

	// =========================================================================
	// Unary Operators
	// =========================================================================
	public static final int OPCODE_debug    = 0 + FMT_UNARYOP;
	public static final int OPCODE_return   = 1 + FMT_UNARYOP;
	public static final int OPCODE_ifis     = 3 + FMT_UNARYOP; // +TYPEIDX
	public static final int OPCODE_switch   = 4 + FMT_UNARYOP; // +OTHER
	public static final int OPCODE_throw    = 5 + FMT_UNARYOP;

	// =========================================================================
	// Unary Assignables
	// =========================================================================
	public static final int OPCODE_assign      = 0 + FMT_UNARYASSIGN;
	public static final int OPCODE_dereference = 1 + FMT_UNARYASSIGN;
	public static final int OPCODE_invert      = 2 + FMT_UNARYASSIGN;
	public static final int OPCODE_lengthof    = 3 + FMT_UNARYASSIGN;
	public static final int OPCODE_move        = 4 + FMT_UNARYASSIGN;
	public static final int OPCODE_newobject   = 5 + FMT_UNARYASSIGN;
	public static final int OPCODE_neg         = 6 + FMT_UNARYASSIGN;
	public static final int OPCODE_numerator   = 7 + FMT_UNARYASSIGN;
	public static final int OPCODE_denominator = 8 + FMT_UNARYASSIGN;
	public static final int OPCODE_not         = 9 + FMT_UNARYASSIGN;
	public static final int OPCODE_tupleload   = 10 + FMT_UNARYASSIGN;
	public static final int OPCODE_fieldload   = 11 + FMT_UNARYASSIGN; // +STRINGIDX
	public static final int OPCODE_convert     = 12 + FMT_UNARYASSIGN; // +TYPEIDX

	// =========================================================================
	// Binary Operators
	// =========================================================================
	public static final int OPCODE_ifeq     = 0  + FMT_BINARYOP; // +INT
	public static final int OPCODE_ifne     = 1  + FMT_BINARYOP; // +INT
	public static final int OPCODE_iflt     = 2  + FMT_BINARYOP; // +INT
	public static final int OPCODE_ifle     = 3  + FMT_BINARYOP; // +INT
	public static final int OPCODE_ifgt     = 4  + FMT_BINARYOP; // +INT
	public static final int OPCODE_ifge     = 5  + FMT_BINARYOP; // +INT
	public static final int OPCODE_ifel     = 6  + FMT_BINARYOP; // +INT
	public static final int OPCODE_ifss     = 7  + FMT_BINARYOP; // +INT
	public static final int OPCODE_ifse     = 8  + FMT_BINARYOP; // +INT

	// =========================================================================
	// Binary Assignables
	// =========================================================================
	public static final int OPCODE_add         = 0  + FMT_BINARYASSIGN;
	public static final int OPCODE_sub         = 1  + FMT_BINARYASSIGN;
	public static final int OPCODE_mul         = 2  + FMT_BINARYASSIGN;
	public static final int OPCODE_div         = 3  + FMT_BINARYASSIGN;
	public static final int OPCODE_rem         = 4  + FMT_BINARYASSIGN;
	public static final int OPCODE_bitwiseor   = 5  + FMT_BINARYASSIGN;
	public static final int OPCODE_bitwisexor  = 6  + FMT_BINARYASSIGN;
	public static final int OPCODE_bitwiseand  = 7  + FMT_BINARYASSIGN;
	public static final int OPCODE_lshr        = 8  + FMT_BINARYASSIGN;
	public static final int OPCODE_rshr        = 9  + FMT_BINARYASSIGN;
	public static final int OPCODE_indexof     = 11 + FMT_BINARYASSIGN;
//	public static final int OPCODE_union       = 12 + FMT_BINARYASSIGN;
//	public static final int OPCODE_unionl      = 13 + FMT_BINARYASSIGN;
//	public static final int OPCODE_unionr      = 14 + FMT_BINARYASSIGN;
//	public static final int OPCODE_intersect   = 15 + FMT_BINARYASSIGN;
//	public static final int OPCODE_intersectl  = 16 + FMT_BINARYASSIGN;
//	public static final int OPCODE_intersectr  = 17 + FMT_BINARYASSIGN;
//	public static final int OPCODE_difference  = 18 + FMT_BINARYASSIGN;
//	public static final int OPCODE_differencel = 19 + FMT_BINARYASSIGN;
	public static final int OPCODE_append      = 20 + FMT_BINARYASSIGN;
	public static final int OPCODE_appendl     = 21 + FMT_BINARYASSIGN;
	public static final int OPCODE_appendr     = 22 + FMT_BINARYASSIGN;

	// =========================================================================
	// Nary Operators
	// =========================================================================
	public static final int OPCODE_loop              = 0 + FMT_NARYOP;
//	public static final int OPCODE_forall            = 1 + FMT_NARYOP;
	public static final int OPCODE_quantify          = 2 + FMT_NARYOP;	
	public static final int OPCODE_indirectinvokefnv = 3 + FMT_NARYOP;
	public static final int OPCODE_indirectinvokemdv = 4 + FMT_NARYOP;
	public static final int OPCODE_invokefnv         = 5 + FMT_NARYOP; // +NAMEIDX
	public static final int OPCODE_invokemdv         = 6 + FMT_NARYOP; // +NAMEIDX
	public static final int OPCODE_void              = 7 + FMT_NARYOP;	

	// =========================================================================
	// Nary Assignables
	// =========================================================================
	public static final int OPCODE_newlist          = 0 + FMT_NARYASSIGN;
//	public static final int OPCODE_newset           = 1 + FMT_NARYASSIGN;
//	public static final int OPCODE_newmap           = 2 + FMT_NARYASSIGN;
	public static final int OPCODE_newtuple         = 3 + FMT_NARYASSIGN;
	public static final int OPCODE_indirectinvokefn = 4 + FMT_NARYASSIGN;
	public static final int OPCODE_indirectinvokemd = 5 + FMT_NARYASSIGN;
	public static final int OPCODE_sublist          = 6 + FMT_NARYASSIGN;
	public static final int OPCODE_invokefn         = 8 + FMT_NARYASSIGN; // +NAMEIDX
	public static final int OPCODE_invokemd         = 9 + FMT_NARYASSIGN; // +NAMEIDX
	public static final int OPCODE_lambdafn         = 10 + FMT_NARYASSIGN; // +NAMEIDX
	public static final int OPCODE_lambdamd         = 11 + FMT_NARYASSIGN; // +NAMEIDX
	public static final int OPCODE_newrecord        = 12 + FMT_NARYASSIGN;// +OTHER

	// =========================================================================
	// Other
	// =========================================================================
	public static final int OPCODE_trycatch            = 0 + FMT_OTHER;
	public static final int OPCODE_update              = 1 + FMT_OTHER;
	public static final int OPCODE_assertblock         = 2 + FMT_OTHER;
	public static final int OPCODE_assumeblock         = 3 + FMT_OTHER;
	public static final int OPCODE_invariantblock      = 4 + FMT_OTHER;

	// this is where I will locate the WIDE and WIDEWIDE Markers
	public static final int OPCODE_wide                = 29 + FMT_OTHER;
	public static final int OPCODE_widerest            = 30 + FMT_OTHER;
	public static final int OPCODE_widewide            = 31 + FMT_OTHER;
}
