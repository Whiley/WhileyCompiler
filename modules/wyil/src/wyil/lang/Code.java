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

	// ===============================================================C
	// Abstract Bytecodes
	// ===============================================================

	/**
	 * Represents the set of all bytecodes. Each bytecode consists of zero or
	 * more types, zero or more targets and zero or more operands. Furthermore,
	 * bytecodes may contain additional data.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 *            --- the type associated with this bytecode.
	 */
	public static abstract class AbstractBytecode<T> implements Code { 
		protected final Type[] types;
		private final int[] targets;
		protected final int[] operands;		
		
		public AbstractBytecode(Type type, int target, int... operands) {			
			this.types = new Type[]{type};
			this.targets = new int[] {target};
			this.operands = operands;
		}
		
		public AbstractBytecode(Type[] types, int[] targets, int... operands) {			
			this.types = types;
			this.targets = targets;
			this.operands = operands;
		}

		@Override
		public void registers(java.util.Set<Integer> registers) {
			for (int i = 0; i != targets().length; ++i) {
				registers.add(targets()[i]);
			}
			for (int i = 0; i != operands().length; ++i) {
				registers.add(operands[i]);
			}
		}

		@Override
		public Code remap(Map<Integer, Integer> binding) {
			int[] nTargets = remapOperands(binding, targets());
			int[] nOperands = remapOperands(binding, operands());
			if (nTargets != targets() || nOperands != operands()) {
				return clone(nTargets, nOperands);
			}
			return this;
		}

		protected abstract Code clone(int[] nTargets, int[] nOperands);
		
		@Override
		public int hashCode() {
			return Arrays.hashCode(types) + Arrays.hashCode(targets()) + Arrays.hashCode(operands());
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof AbstractBytecode) {
				AbstractBytecode bo = (AbstractBytecode) o;
				return Arrays.equals(targets(), bo.targets()) && Arrays.equals(operands(), bo.operands())
						&& Arrays.equals(types, bo.types);
			}
			return false;
		}

		public Type[] types() {
			return types;
		}
		
		public T type(int i) {
			return (T) types[i];
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
	public static abstract class AbstractCompoundBytecode extends AbstractBytecode {
		protected final int block;

		public AbstractCompoundBytecode(int block, Type[] types, int[] targets, int... operands) {
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
			if(o instanceof AbstractCompoundBytecode) {
				AbstractCompoundBytecode abc = (AbstractCompoundBytecode) o;
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
	public static abstract class AbstractBranchingBytecode extends AbstractBytecode<Type> {
		protected final String destination;

		public AbstractBranchingBytecode(String destination, Type[] types, int[] targets, int... operands) {
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
			if(o instanceof AbstractBranchingBytecode) {
				AbstractBranchingBytecode abc = (AbstractBranchingBytecode) o;
				return destination.equals(abc.destination) && super.equals(o);
			}
			return false;
		}
	}

	// =========================================================================
	// Empty Bytecodes
	// =========================================================================
	public static final int OPCODE_nop       = 0;
	public static final int OPCODE_goto      = 1;
	public static final int OPCODE_fail      = 2;
	public static final int OPCODE_assert    = 4;
	public static final int OPCODE_assume    = 5;
	public static final int OPCODE_invariant = 6;

	// =========================================================================
	// Unary Operators.
	// =========================================================================
	public static final int UNARY_OPERATOR = 7;
	
	public static final int OPCODE_debug    = UNARY_OPERATOR+0;
	public static final int OPCODE_return   = UNARY_OPERATOR+1;
	public static final int OPCODE_ifis     = UNARY_OPERATOR+2;
	public static final int OPCODE_switch   = UNARY_OPERATOR+3;

	// =========================================================================
	// Unary Assignables
	// =========================================================================
	public static final int UNARY_ASSIGNABLE = UNARY_OPERATOR+5;
	
	public static final int OPCODE_assign      = UNARY_ASSIGNABLE+0;

	public static final int OPCODE_lengthof    = UNARY_ASSIGNABLE+3;
	public static final int OPCODE_move        = UNARY_ASSIGNABLE+4;
	public static final int OPCODE_newobject   = UNARY_ASSIGNABLE+5;
	
	//public static final int OPCODE_not         = UNARY_ASSIGNABLE+7;
	public static final int OPCODE_fieldload   = UNARY_ASSIGNABLE+8;
	public static final int OPCODE_convert     = UNARY_ASSIGNABLE+9;
	public static final int OPCODE_const       = UNARY_ASSIGNABLE+10;
	
	// =========================================================================
	// Binary Operators
	// =========================================================================
	public static final int BINARY_OPERATOR = UNARY_ASSIGNABLE+11;
	
	public static final int OPCODE_ifeq     = BINARY_OPERATOR+0;
	public static final int OPCODE_ifne     = BINARY_OPERATOR+1;
	public static final int OPCODE_iflt     = BINARY_OPERATOR+2;
	public static final int OPCODE_ifle     = BINARY_OPERATOR+3;
	public static final int OPCODE_ifgt     = BINARY_OPERATOR+4;
	public static final int OPCODE_ifge     = BINARY_OPERATOR+5;
	
	// =========================================================================
	// Binary Assignables
	// =========================================================================
	public static final int BINARY_ASSIGNABLE = BINARY_OPERATOR+6;
	
	public static final int OPCODE_neg         = BINARY_ASSIGNABLE+0;
	public static final int OPCODE_invert      = BINARY_ASSIGNABLE+1;	
	public static final int OPCODE_dereference = BINARY_ASSIGNABLE+2;
	public static final int OPCODE_add         = BINARY_ASSIGNABLE+3;
	public static final int OPCODE_sub         = BINARY_ASSIGNABLE+4;
	public static final int OPCODE_mul         = BINARY_ASSIGNABLE+5;
	public static final int OPCODE_div         = BINARY_ASSIGNABLE+6;
	public static final int OPCODE_rem         = BINARY_ASSIGNABLE+7;
	public static final int OPCODE_bitwiseor   = BINARY_ASSIGNABLE+8;
	public static final int OPCODE_bitwisexor  = BINARY_ASSIGNABLE+9;
	public static final int OPCODE_bitwiseand  = BINARY_ASSIGNABLE+10;
	public static final int OPCODE_lshr        = BINARY_ASSIGNABLE+11;
	public static final int OPCODE_rshr        = BINARY_ASSIGNABLE+12;
	public static final int OPCODE_indexof     = BINARY_ASSIGNABLE+13;
	public static final int OPCODE_arrygen     = BINARY_ASSIGNABLE+14;

	// =========================================================================
	// Nary Assignables
	// =========================================================================
	public static final int NARY_ASSIGNABLE = BINARY_ASSIGNABLE+15;
	
	public static final int OPCODE_newarray         = NARY_ASSIGNABLE+0;
	public static final int OPCODE_newrecord        = NARY_ASSIGNABLE+1;
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
		
		public abstract Code construct(int opcode, int[] targets, int[] operands, Type[] types, Object[] extras);

		public String toString() {
			return "<" + targets.toString() + " targets, " + operands + " operands, " + types + " types, " + Arrays.toString(extras) + ">";
		}
	}
}
