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
		public abstract Code.Unit remap(Map<Integer, Integer> binding);
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
	 * Represents the set of all bytecodes. Each bytecode consists of zero or
	 * more types, zero or more targets and zero or more operands. Furthermore,
	 * bytecodes may contain additional data.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 *            --- the type associated with this bytecode.
	 */
	public static abstract class AbstractBytecode<T> extends Code.Unit { 
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
		public final void registers(java.util.Set<Integer> registers) {
			for (int i = 0; i != targets().length; ++i) {
				registers.add(targets()[i]);
			}
			for (int i = 0; i != operands().length; ++i) {
				registers.add(operands[i]);
			}
		}

		@Override
		public final Code.Unit remap(Map<Integer, Integer> binding) {
			int[] nTargets = remapOperands(binding, targets());
			int[] nOperands = remapOperands(binding, operands());
			if (nTargets != targets() || nOperands != operands()) {
				return clone(nTargets, nOperands);
			}
			return this;
		}

		protected abstract Code.Unit clone(int[] nTargets, int[] nOperands);
		
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
			return targets[0];
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
	public static final int OPCODE_goto     = 3 + FMT_EMPTY; // +INT
	public static final int OPCODE_fail     = 4 + FMT_EMPTY;

	// =========================================================================
	// Unary Operators
	// =========================================================================
	public static final int OPCODE_debug    = 0 + FMT_UNARYOP;
	public static final int OPCODE_return   = 1 + FMT_NARYASSIGN;
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
//	public static final int OPCODE_numerator   = 7 + FMT_UNARYASSIGN;
//	public static final int OPCODE_denominator = 8 + FMT_UNARYASSIGN;
	public static final int OPCODE_not         = 9 + FMT_UNARYASSIGN;
//	public static final int OPCODE_tupleload   = 10 + FMT_UNARYASSIGN;
	public static final int OPCODE_fieldload   = 11 + FMT_UNARYASSIGN; // +STRINGIDX
	public static final int OPCODE_convert     = 12 + FMT_UNARYASSIGN; // +TYPEIDX
	public static final int OPCODE_const       = 14 + FMT_UNARYASSIGN; // +CONSTIDX
	
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
	public static final int OPCODE_listgen       = 12 + FMT_BINARYASSIGN;
//	public static final int OPCODE_unionl      = 13 + FMT_BINARYASSIGN;
//	public static final int OPCODE_unionr      = 14 + FMT_BINARYASSIGN;
//	public static final int OPCODE_intersect   = 15 + FMT_BINARYASSIGN;
//	public static final int OPCODE_intersectl  = 16 + FMT_BINARYASSIGN;
//	public static final int OPCODE_intersectr  = 17 + FMT_BINARYASSIGN;
//	public static final int OPCODE_difference  = 18 + FMT_BINARYASSIGN;
//	public static final int OPCODE_differencel = 19 + FMT_BINARYASSIGN;
//	public static final int OPCODE_append      = 20 + FMT_BINARYASSIGN;
//	public static final int OPCODE_appendl     = 21 + FMT_BINARYASSIGN;
//	public static final int OPCODE_appendr     = 22 + FMT_BINARYASSIGN;

	// =========================================================================
	// Nary Operators
	// =========================================================================
	public static final int OPCODE_loop              = 0 + FMT_NARYOP;
//	public static final int OPCODE_forall            = 1 + FMT_NARYOP;
	public static final int OPCODE_quantify          = 2 + FMT_NARYOP;	
	public static final int OPCODE_void              = 7 + FMT_NARYOP;	

	// =========================================================================
	// Nary Assignables
	// =========================================================================
	public static final int OPCODE_newlist          = 0 + FMT_NARYASSIGN;
//  public static final int OPCODE_newset          = 1 + FMT_NARYASSIGN;
//	public static final int OPCODE_newmap           = 2 + FMT_NARYASSIGN;
//	public static final int OPCODE_newtuple         = 3 + FMT_NARYASSIGN;
	public static final int OPCODE_indirectinvokefn = 4 + FMT_NARYASSIGN;
	public static final int OPCODE_indirectinvokemd = 5 + FMT_NARYASSIGN;
//	public static final int OPCODE_sublist          = 6 + FMT_NARYASSIGN;
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
