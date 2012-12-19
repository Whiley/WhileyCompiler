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
import wyil.util.*;

/**
 * Represents a WYIL bytecode. The Whiley Intermediate Language (WYIL) employs
 * register-based bytecodes (as opposed to say the Java Virtual Machine, which
 * uses stack-based bytecodes). During execution, one can think of the "machine"
 * as maintaining a call stack made up of "frames". For each function or method
 * on the call stack, the corresponding frame consists of zero or more <i>local
 * variables</i> (a.k.a registers). Bytecodes may read/write values from local
 * variables. Like the Java Virtual Machine, WYIL uses unstructured
 * control-flow. However, unlike the JVM, it also allows variables to take on
 * different types at different points. The following illustrates:
 * 
 * <pre>
 * int sum([int] data):
 *    r = 0
 *    for item in data:
 *       r = r + item
 *    return r
 * </pre>
 * 
 * This function is compiled into the following WYIL bytecode:
 * 
 * <pre>
 * int sum([int] data):
 * body: 
 *   const %1 = 0          : int                      
 *   assign %2 = %0        : [int]                 
 *   forall %3 in %2 ()    : [int]              
 *       assign %4 = %1    : int                                
 *       add %1 = %4, %3   : int                                     
 *   return %1             : int
 * </pre>
 * 
 * Here, we can see that every bytecode is associated with one (or more) types.
 * These types are inferred by the compiler during type propagation.
 * 
 * @author David J. Pearce
 */
public abstract class Code {
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
	 * Construct an <code>assert</code> bytecode which raises an assertion
	 * failure with the given if the given condition evaluates to false.
	 * 
	 * @param message
	 *            --- message to report upon failure.
	 * @return
	 */
	public static Assert Assert(Type type, int leftOperand, int rightOperand,
			Comparator cop, String message) {
		return get(new Assert(type, leftOperand, rightOperand, cop, message));
	}

	/**
	 * Construct an <code>assumet</code> bytecode which raises an assertion
	 * failure with the given if the given condition evaluates to false.
	 * 
	 * @param message
	 *            --- message to report upon failure.
	 * @return
	 */
	public static Assume Assume(Type type, int leftOperand, int rightOperand,
			Comparator cop, String message) {
		return get(new Assume(type, leftOperand, rightOperand, cop, message));
	}

	public static BinArithOp BinArithOp(Type type, int target, int leftOperand,
			int rightOperand, BinArithKind op) {
		return get(new BinArithOp(type, target, leftOperand, rightOperand, op));
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
		return get(new Const(target, constant));
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
		return get(new Assign(type, target, operand));
	}

	public static Convert Convert(Type from, int target, int operand, Type to) {
		return get(new Convert(from, target, operand, to));
	}

	public static final Debug Debug(int operand) {
		return get(new Debug(operand));
	}

	public static LoopEnd LoopEnd(String label) {
		return get(new LoopEnd(label));
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
		return get(new FieldLoad(type, target, operand, field));
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
		return get(new Goto(label));
	}

	public static Invoke Invoke(Type.FunctionOrMethod fun, int target,
			Collection<Integer> operands, NameID name) {
		return get(new Invoke(fun, target, toIntArray(operands), name));
	}

	public static Invoke Invoke(Type.FunctionOrMethod fun, int target,
			int[] operands, NameID name) {
		return get(new Invoke(fun, target, operands, name));
	}

	public static Lambda Lambda(Type.FunctionOrMethod fun, int target,
			Collection<Integer> operands, NameID name) {
		return get(new Lambda(fun, target, toIntArray(operands), name));
	}

	public static Lambda Lambda(Type.FunctionOrMethod fun, int target,
			int[] operands, NameID name) {
		return get(new Lambda(fun, target, operands, name));
	}
	
	public static Not Not(int target, int operand) {
		return get(new Not(target, operand));
	}

	public static LengthOf LengthOf(Type.EffectiveCollection type, int target,
			int operand) {
		return get(new LengthOf(type, target, operand));
	}

	public static Move Move(Type type, int target, int operand) {
		return get(new Move(type, target, operand));
	}

	public static SubList SubList(Type.EffectiveList type, int target,
			int sourceOperand, int leftOperand, int rightOperand) {
		int[] operands = new int[] { sourceOperand, leftOperand, rightOperand };
		return get(new SubList(type, target, operands));
	}

	private static SubList SubList(Type.EffectiveList type, int target,
			int[] operands) {
		return get(new SubList(type, target, operands));
	}

	public static BinListOp BinListOp(Type.EffectiveList type, int target,
			int leftOperand, int rightOperand, BinListKind dir) {
		return get(new BinListOp(type, target, leftOperand, rightOperand, dir));
	}

	/**
	 * Construct a <code>listload</code> bytecode which reads a value from a
	 * given index in a given list.
	 * 
	 * @param type
	 *            --- list type.
	 * @return
	 */
	public static IndexOf IndexOf(Type.EffectiveIndexible type, int target,
			int leftOperand, int rightOperand) {
		return get(new IndexOf(type, target, leftOperand, rightOperand));
	}

	public static Loop Loop(String label, Collection<Integer> operands) {
		return get(new Loop(label, toIntArray(operands)));
	}

	public static Loop Loop(String label, int[] modifies) {
		return get(new Loop(label, modifies));
	}

	public static ForAll ForAll(Type.EffectiveCollection type,
			int sourceOperand, int indexOperand,
			Collection<Integer> modifiedOperands, String label) {
		return get(new ForAll(type, sourceOperand, indexOperand,
				toIntArray(modifiedOperands), label));
	}

	public static ForAll ForAll(Type.EffectiveCollection type,
			int sourceOperand, int indexOperand, int[] modifiedOperands,
			String label) {
		return get(new ForAll(type, sourceOperand, indexOperand,
				modifiedOperands, label));
	}

	/**
	 * Construct a <code>newdict</code> bytecode which constructs a new
	 * map and puts it on the stack.
	 * 
	 * @param type
	 * @return
	 */
	public static NewMap NewMap(Type.Map type, int target,
			Collection<Integer> operands) {
		return get(new NewMap(type, target, toIntArray(operands)));
	}

	public static NewMap NewMap(Type.Map type, int target, int[] operands) {
		return get(new NewMap(type, target, operands));
	}

	/**
	 * Construct a <code>newset</code> bytecode which constructs a new set and
	 * puts it on the stack.
	 * 
	 * @param type
	 * @return
	 */
	public static NewSet NewSet(Type.Set type, int target,
			Collection<Integer> operands) {
		return get(new NewSet(type, target, toIntArray(operands)));
	}

	public static NewSet NewSet(Type.Set type, int target, int[] operands) {
		return get(new NewSet(type, target, operands));
	}

	/**
	 * Construct a <code>newlist</code> bytecode which constructs a new list and
	 * puts it on the stack.
	 * 
	 * @param type
	 * @return
	 */
	public static NewList NewList(Type.List type, int target,
			Collection<Integer> operands) {
		return get(new NewList(type, target, toIntArray(operands)));
	}

	public static NewList NewList(Type.List type, int target, int[] operands) {
		return get(new NewList(type, target, operands));
	}

	/**
	 * Construct a <code>newtuple</code> bytecode which constructs a new tuple
	 * and puts it on the stack.
	 * 
	 * @param type
	 * @return
	 */
	public static NewTuple NewTuple(Type.Tuple type, int target,
			Collection<Integer> operands) {
		return get(new NewTuple(type, target, toIntArray(operands)));
	}

	public static NewTuple NewTuple(Type.Tuple type, int target, int[] operands) {
		return get(new NewTuple(type, target, operands));
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
		return get(new NewRecord(type, target, toIntArray(operands)));
	}

	public static NewRecord NewRecord(Type.Record type, int target, int[] operands) {
		return get(new NewRecord(type, target, operands));
	}

	/**
	 * Construct a return bytecode which does return a value and, hence, its
	 * type automatically defaults to void.
	 * 
	 * @return
	 */
	public static Return Return() {
		return get(new Return(Type.T_VOID, NULL_REG));
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
		return get(new Return(type, operand));
	}

	public static If If(Type type, int leftOperand, int rightOperand,
			Comparator cop, String label) {
		return get(new If(type, leftOperand, rightOperand, cop, label));
	}

	public static IfIs IfIs(Type type, int leftOperand, Type rightOperand,
			String label) {
		return get(new IfIs(type, leftOperand, rightOperand, label));
	}

	public static IndirectInvoke IndirectInvoke(Type.FunctionOrMethod fun,
			int target, int operand, Collection<Integer> operands) {
		return get(new IndirectInvoke(fun, target, operand,
				toIntArray(operands)));
	}

	public static IndirectInvoke IndirectInvoke(Type.FunctionOrMethod fun,
			int target, int operand, int[] operands) {
		return get(new IndirectInvoke(fun, target, operand, operands));
	}

	public static Invert Invert(Type type, int target, int operand) {
		return get(new Invert(type, target, operand));
	}

	public static Label Label(String label) {
		return get(new Label(label));
	}

	public static final Nop Nop = new Nop();

	public static BinSetOp BinSetOp(Type.EffectiveSet type, int target,
			int leftOperand, int rightOperand, BinSetKind operation) {
		return get(new BinSetOp(type, target, leftOperand, rightOperand, operation));
	}

	public static BinStringOp BinStringOp(int target, int leftOperand,
			int rightOperand, BinStringKind operation) {
		return get(new BinStringOp(target, leftOperand, rightOperand, operation));
	}

	public static SubString SubString(int target, int sourceOperand,
			int leftOperand, int rightOperand) {
		int[] operands = new int[] { sourceOperand, leftOperand, rightOperand };
		return get(new SubString(target, operands));
	}

	private static SubString SubString(int target, int[] operands) {
		return get(new SubString(target, operands));
	}

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
		return get(new Switch(type, operand, defaultLabel, cases));
	}

	/**
	 * Construct a <code>throw</code> bytecode which pops a value off the stack
	 * and throws it.
	 * 
	 * @param afterType
	 *            --- value type to throw
	 * @return
	 */
	public static Throw Throw(Type type, int operand) {
		return get(new Throw(type, operand));
	}

	/**
	 * Construct a <code>trycatch</code> bytecode which defines a region of
	 * bytecodes which are covered by one or more catch handles.
	 * 
	 * @param target
	 *            --- identifies end-of-block label.
	 * @param catches
	 *            --- map from types to destination labels.
	 * @return
	 */
	public static TryCatch TryCatch(int operand, String target,
			Collection<Pair<Type, String>> catches) {
		return get(new TryCatch(operand, target, catches));
	}

	public static TryEnd TryEnd(String label) {
		return get(new TryEnd(label));
	}

	public static TupleLoad TupleLoad(Type.EffectiveTuple type, int target,
			int operand, int index) {
		return get(new TupleLoad(type, target, operand, index));
	}

	public static NewObject NewObject(Type.Reference type, int target, int operand) {
		return get(new NewObject(type, target, operand));
	}

	public static Dereference Dereference(Type.Reference type, int target,
			int operand) {
		return get(new Dereference(type, target, operand));
	}
	
	public static Update Update(Type beforeType, int target, int operand,
			Collection<Integer> operands, Type afterType,
			Collection<String> fields) {
		return get(new Update(beforeType, target, operand,
				toIntArray(operands), afterType, fields));
	}

	public static Update Update(Type beforeType, int target, int operand,
			int[] operands, Type afterType, Collection<String> fields) {
		return get(new Update(beforeType, target, operand, operands, afterType,
				fields));
	}

	public static UnArithOp UnArithOp(Type type, int target, int operand,
			UnArithKind uop) {
		return get(new UnArithOp(type, target, operand, uop));
	}
	
	public static Void Void(Type type, int[] operands) {
		return get(new Void(type, operands));
	}

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
	public void registers(java.util.Set<Integer> register) {
		// default implementation does nothing
	}

	/**
	 * Remaps all registers according to a given binding. Registers not
	 * mentioned in the binding retain their original value. Note, if the
	 * returned bytecode is unchanged then it must be <code>this</code>.
	 * 
	 * @param binding
	 *            --- map from (existing) registers to (new) registers.
	 * @return
	 */
	public Code remap(Map<Integer, Integer> binding) {
		return this;
	}

	/**
	 * Relabel all labels according to the given map.
	 * 
	 * @param labels
	 * @return
	 */
	public Code relabel(Map<String, String> labels) {
		return this;
	}

	/**
	 * Return the opcode value of this bytecode
	 * @return
	 */
	public abstract int opcode();

	// ===============================================================C
	// Abstract Bytecodes
	// ===============================================================

	public static abstract class AbstractAssignable extends Code {
		public final int target;

		private AbstractAssignable(int target) {
			this.target = target;
		}
	}

	/**
	 * Represents the set of bytcodes which take a single register operand and
	 * write a result to the target register.
	 * 
	 * @author David J. Pearce
	 * 
	 * @param <T>
	 *            --- the type associated with this bytecode.
	 */
	public static abstract class AbstractUnaryAssignable<T> extends
			AbstractAssignable {
		public final T type;
		public final int operand;

		private AbstractUnaryAssignable(T type, int target, int operand) {
			super(target);
			if (type == null) {
				throw new IllegalArgumentException(
						"AbstractUnOp type argument cannot be null");
			}
			this.type = type;
			this.operand = operand;
		}

		@Override
		public final void registers(java.util.Set<Integer> registers) {
			registers.add(target);
			registers.add(operand);
		}

		@Override
		public final Code remap(Map<Integer, Integer> binding) {
			Integer nTarget = binding.get(target);
			Integer nOperand = binding.get(operand);
			if (nTarget != null || nOperand != null) {
				nTarget = nTarget != null ? nTarget : target;
				nOperand = nOperand != null ? nOperand : operand;
				return clone(nTarget, nOperand);
			}
			return this;
		}

		protected abstract Code clone(int nTarget, int nOperand);

		public int hashCode() {
			return type.hashCode() + target + operand;
		}

		public boolean equals(Object o) {
			if (o instanceof AbstractUnaryAssignable) {
				AbstractUnaryAssignable bo = (AbstractUnaryAssignable) o;
				return target == bo.target && operand == bo.operand
						&& type.equals(bo.type);
			}
			return false;
		}
	}

	/**
	 * Represents the set of bytcodes which take a single register operand, and
	 * do not write any result.
	 * 
	 * @author David J. Pearce
	 * 
	 * @param <T>
	 *            --- the type associated with this bytecode.
	 */
	public static abstract class AbstractUnaryOp<T> extends Code {
		public final T type;
		public final int operand;

		private AbstractUnaryOp(T type, int operand) {
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
		public final Code remap(Map<Integer, Integer> binding) {
			Integer nOperand = binding.get(operand);
			if (nOperand != null) {
				return clone(nOperand);
			}
			return this;
		}

		protected abstract Code clone(int nOperand);

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
	 * Represents the set of bytcodes which take two register operands and write
	 * a result to the target register.
	 * 
	 * @author David J. Pearce
	 * 
	 * @param <T>
	 *            --- the type associated with this bytecode.
	 */
	public static abstract class AbstractBinaryAssignable<T> extends
			AbstractAssignable {
		public final T type;
		public final int leftOperand;
		public final int rightOperand;

		private AbstractBinaryAssignable(T type, int target, int leftOperand,
				int rightOperand) {
			super(target);
			if (type == null) {
				throw new IllegalArgumentException(
						"AbstractBinOp type argument cannot be null");
			}
			this.type = type;
			this.leftOperand = leftOperand;
			this.rightOperand = rightOperand;
		}

		@Override
		public final void registers(java.util.Set<Integer> registers) {
			registers.add(target);
			registers.add(leftOperand);
			registers.add(rightOperand);
		}

		@Override
		public final Code remap(Map<Integer, Integer> binding) {
			Integer nTarget = binding.get(target);
			Integer nLeftOperand = binding.get(leftOperand);
			Integer nRightOperand = binding.get(rightOperand);
			if (nTarget != null || nLeftOperand != null
					|| nRightOperand != null) {
				nTarget = nTarget != null ? nTarget : target;
				nLeftOperand = nLeftOperand != null ? nLeftOperand
						: leftOperand;
				nRightOperand = nRightOperand != null ? nRightOperand
						: rightOperand;
				return clone(nTarget, nLeftOperand, nRightOperand);
			}
			return this;
		}

		protected abstract Code clone(int nTarget, int nLeftOperand,
				int nRightOperand);

		public int hashCode() {
			return type.hashCode() + target + leftOperand + rightOperand;
		}

		public boolean equals(Object o) {
			if (o instanceof AbstractBinaryAssignable) {
				AbstractBinaryAssignable bo = (AbstractBinaryAssignable) o;
				return target == bo.target && leftOperand == bo.leftOperand
						&& rightOperand == bo.rightOperand
						&& type.equals(bo.type);
			}
			return false;
		}
	}

	/**
	 * Represents the set of bytcodes which take an arbitrary number of register
	 * operands and write a result to the target register.
	 * 
	 * @author David J. Pearce
	 * 
	 * @param <T>
	 *            --- the type associated with this bytecode.
	 */
	public static abstract class AbstractNaryAssignable<T> extends
			AbstractAssignable {
		public final T type;
		public final int[] operands;

		private AbstractNaryAssignable(T type, int target, int[] operands) {
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
			if (target >= 0) {
				registers.add(target);
			}
			for (int i = 0; i != operands.length; ++i) {
				registers.add(operands[i]);
			}
		}

		@Override
		public final Code remap(Map<Integer, Integer> binding) {
			Integer nTarget = binding.get(target);
			int[] nOperands = remapOperands(binding, operands);
			if (nTarget != null || nOperands != operands) {
				nTarget = nTarget != null ? nTarget : target;
				return clone(nTarget, nOperands);
			}
			return this;
		}

		protected abstract Code clone(int nTarget, int[] nOperands);

		public int hashCode() {
			return type.hashCode() + target + Arrays.hashCode(operands);
		}

		public boolean equals(Object o) {
			if (o instanceof AbstractNaryAssignable) {
				AbstractNaryAssignable bo = (AbstractNaryAssignable) o;
				return target == bo.target
						&& Arrays.equals(operands, bo.operands)
						&& type.equals(bo.type);
			}
			return false;
		}
	}

	/**
	 * Represents the set of bytcodes which take an arbitrary number of register
	 * operands and write a result to the target register.
	 * 
	 * @author David J. Pearce
	 * 
	 * @param <T>
	 *            --- the type associated with this bytecode.
	 */
	public static abstract class AbstractSplitNaryAssignable<T> extends
			AbstractAssignable {
		public final T type;
		public final int operand;
		public final int[] operands;

		private AbstractSplitNaryAssignable(T type, int target, int operand,
				int[] operands) {
			super(target);
			if (type == null) {
				throw new IllegalArgumentException(
						"AbstractSplitNaryAssignable type argument cannot be null");
			}
			this.type = type;
			this.operand = operand;
			this.operands = operands;
		}

		@Override
		public final void registers(java.util.Set<Integer> registers) {
			if (target >= 0) {
				registers.add(target);
			}
			registers.add(operand);
			for (int i = 0; i != operands.length; ++i) {
				registers.add(operands[i]);
			}
		}

		@Override
		public final Code remap(java.util.Map<Integer, Integer> binding) {
			Integer nTarget = binding.get(target);
			Integer nOperand = binding.get(target);
			int[] nOperands = remapOperands(binding, operands);
			if (nTarget != null || nOperand != null || nOperands != operands) {
				nTarget = nTarget != null ? nTarget : target;
				nOperand = nOperand != null ? nOperand : operand;
				return clone(nTarget, nOperand, nOperands);
			}
			return this;
		}

		protected abstract Code clone(int nTarget, int nOperand, int[] nOperands);

		public int hashCode() {
			return type.hashCode() + target + operand
					+ Arrays.hashCode(operands);
		}

		public boolean equals(Object o) {
			if (o instanceof AbstractSplitNaryAssignable) {
				AbstractSplitNaryAssignable bo = (AbstractSplitNaryAssignable) o;
				return target == bo.target && operand == bo.operand
						&& Arrays.equals(operands, bo.operands)
						&& type.equals(bo.type);
			}
			return false;
		}
	}

	/**
	 * Represents the set of bytcodes which take two register operands and
	 * perform a comparison of their values.
	 * 
	 * @author David J. Pearce
	 * 
	 * @param <T>
	 *            --- the type associated with this bytecode.
	 */
	public static abstract class AbstractBinaryOp<T> extends Code {
		public final T type;
		public final int leftOperand;
		public final int rightOperand;

		private AbstractBinaryOp(T type, int leftOperand, int rightOperand) {
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
		public final Code remap(Map<Integer, Integer> binding) {
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

		protected abstract Code clone(int nLeftOperand, int nRightOperand);

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
	public enum BinArithKind {
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
		RANGE(5) {
			public String toString() {
				return "range";
			}
		},
		BITWISEOR(6) {
			public String toString() {
				return "or";
			}
		},
		BITWISEXOR(7) {
			public String toString() {
				return "xor";
			}
		},
		BITWISEAND(8) {
			public String toString() {
				return "and";
			}
		},
		LEFTSHIFT(9) {
			public String toString() {
				return "shl";
			}
		},
		RIGHTSHIFT(10) {
			public String toString() {
				return "shr";
			}
		};
		public int offset;
		
		private BinArithKind(int offset) {
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
	 * <li><i>range</i></li>
	 * <li><i>bitwiseor, bitwisexor, bitwiseand</i></li>
	 * <li><i>leftshift,rightshift</i></li>
	 * </ul>
	 * For example, the following Whiley code:
	 * 
	 * <pre>
	 * int f(int x, int y):
	 *     return ((x * y) + 1) / 2
	 * </pre>
	 * 
	 * can be translated into the following WYIL code:
	 * 
	 * <pre>
	 * int f(int x, int y):
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
	public static final class BinArithOp extends AbstractBinaryAssignable<Type> {
		public final BinArithKind kind;

		private BinArithOp(Type type, int target, int lhs, int rhs, BinArithKind bop) {
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
		public Code clone(int nTarget, int nLeftOperand, int nRightOperand) {
			return Code.BinArithOp(type, nTarget, nLeftOperand, nRightOperand, kind);
		}

		public int hashCode() {
			return kind.hashCode() + super.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof BinArithOp) {
				BinArithOp bo = (BinArithOp) o;
				return kind.equals(bo.kind) && super.equals(bo);
			}
			return false;
		}

		public String toString() {
			return kind + " %" + target + " = %" + leftOperand + ", %"
					+ rightOperand + " : " + type;
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
	 * real f(int x):
	 *     return x + 1
	 * </pre>
	 * 
	 * can be translated into the following WYIL code:
	 * 
	 * <pre>
	 * real f(int x):
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

		public Code clone(int nTarget, int nOperand) {
			return Code.Convert(type, nTarget, nOperand, result);
		}

		public int opcode() { return OPCODE_convert; }
		
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
			return "convert %" + target + " = %" + operand + " " + result
					+ " : " + type;
		}
	}

	/**
	 * Writes a constant value to a target register. This includes
	 * <i>integers</i>, <i>rationals</i>, <i>lists</i>, <i>sets</i>,
	 * <i>maps</i>, etc. For example, the following Whiley code:
	 * 
	 * <pre>
	 * int f(int x):
	 *     xs = {1,2.12}
	 *     return |xs| + 1
	 * </pre>
	 * 
	 * can be translated into the following WYIL code:
	 * 
	 * <pre>
	 * int f(int x):
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
	 * <code>const %2 = 1</code>) and rationals (i.e. <code>const %3 = 2.12</code>).
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

		public int opcode() { return OPCODE_const; }
		
		@Override
		public void registers(java.util.Set<Integer> registers) {
			registers.add(target);
		}

		@Override
		public Code remap(Map<Integer, Integer> binding) {
			Integer nTarget = binding.get(target);
			if (nTarget != null) {
				return Code.Const(nTarget, constant);
			}
			return this;
		}

		public int hashCode() {
			return constant.hashCode() + target;
		}

		public boolean equals(Object o) {
			if (o instanceof Const) {
				Const c = (Const) o;
				return constant.equals(c.constant) && target == c.target;
			}
			return false;
		}

		public String toString() {
			return "const %" + target + " = " + constant + " : "
					+ constant.type();
		}
	}

	/**
	 * Copy the contents from a given operand register into a given target
	 * register. For example, the following Whiley code:
	 * 
	 * <pre>
	 * int f(int x):
	 *     x = x + 1
	 *     return x
	 * </pre>
	 * 
	 * can be translated into the following WYIL code:
	 * 
	 * <pre>
	 * int f(int x):
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

		public int opcode() { return OPCODE_assign; }
		
		public Code clone(int nTarget, int nOperand) {
			return Code.Assign(type, nTarget, nOperand);
		}

		public boolean equals(Object o) {
			if (o instanceof Assign) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			return "assign %" + target + " = %" + operand + " " + " : " + type;
		}
	}

	/**
	 * Read a string from the operand register and prints it to the debug
	 * console. For example, the following Whiley code:
	 * 
	 * <pre>
	 * void f(int x):
	 *     debug "X = " + x
	 * </pre>
	 * 
	 * can be translated into the following WYIL code:
	 * 
	 * <pre>
	 * void f(int x):
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
	public static final class Debug extends AbstractUnaryOp<Type.Strung> {

		private Debug(int operand) {
			super(Type.T_STRING, operand);
		}

		public int opcode() { return OPCODE_debug; }
		
		@Override
		public Code clone(int nOperand) {
			return Code.Debug(nOperand);
		}

		public boolean equals(Object o) {
			return o instanceof Debug && super.equals(o);
		}

		public String toString() {
			return "debug %" + operand + " " + " : " + type;
		}
	}

	/**
	 * Marks the end of a loop block.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class LoopEnd extends Label {
		LoopEnd(String label) {
			super(label);
		}

		public LoopEnd relabel(Map<String, String> labels) {
			String nlabel = labels.get(label);
			if (nlabel == null) {
				return this;
			} else {
				return LoopEnd(nlabel);
			}
		}

		public int hashCode() {
			return label.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof LoopEnd) {
				LoopEnd e = (LoopEnd) o;
				return e.label.equals(label);
			}
			return false;
		}

		public String toString() {
			return "end " + label;
		}
	}

	/**
	 * An abstract class representing either an <code>assert</code> or
	 * <code>assume</code> bytecode.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static abstract class AssertOrAssume extends AbstractBinaryOp<Type> {
		public final Comparator op;
		public final String msg;

		private AssertOrAssume(Type type, int leftOperand, int rightOperand,
				Comparator cop, String msg) {
			super(type, leftOperand, rightOperand);
			if (cop == null) {
				throw new IllegalArgumentException(
						"Assert op argument cannot be null");
			}
			this.op = cop;
			this.msg = msg;
		}
	}

	/**
	 * Reads two operand registers, compares their values and raises an
	 * assertion failure with the given message if comparison is false. For
	 * example, the following Whiley code:
	 * 
	 * <pre>
	 * int f([int] xs, int i):
	 *     return xs[i]
	 * </pre>
	 * 
	 * can be translated into the following WYIL code:
	 * 
	 * <pre>
	 * int f([int] xs, int i):
	 * body:          
	 *    const %2 = 0         : int                                           
	 *    assertge %1, %2 "index out of bounds (negative)" : int
	 *    lengthof %3 = %0     : [int]               
	 *    assertlt %2, %3 "index out of bounds (not less than length)" : int
	 *    indexof %1 = %0, %1  : [int]            
	 *    return %1            : int
	 * </pre>
	 * 
	 * Here, we see <code>assert</code> bytecodes being used to check list
	 * access is not out-of-bounds.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Assert extends AssertOrAssume {

		private Assert(Type type, int leftOperand, int rightOperand, Comparator cop,
				String msg) {
			super(type, leftOperand, rightOperand, cop, msg);
		}

		public int opcode() { return OPCODE_asserteq + op.offset; }
		
		@Override
		public Code clone(int nLeftOperand, int nRightOperand) {
			return Code.Assert(type, nLeftOperand, nRightOperand, op, msg);
		}

		public boolean equals(Object o) {
			if (o instanceof Assert) {
				Assert ig = (Assert) o;
				return op == ig.op && msg.equals(ig.msg) && super.equals(ig);
			}
			return false;
		}

		public String toString() {
			return "assert" + op + " %" + leftOperand + ", %" + rightOperand
					+ " \"" + msg + "\"" + " : " + type;
		}
	}

	/**
	 * Reads two operand registers, compares their values and raises an
	 * assertion failure with the given message is raised if comparison is
	 * false. Whilst this is very similar to an assert statement, it causes a
	 * slightly different interaction with the type checker and/or theorem
	 * prover. More specifically, they will not attempt to show the condition is
	 * true and, instead, will simply assume it is (and leave an appropriate
	 * runtime check). This is useful for override these processes in situations
	 * where they are not smart enough to prove something is true.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Assume extends AssertOrAssume {

		private Assume(Type type, int leftOperand, int rightOperand, Comparator cop,
				String msg) {
			super(type, leftOperand, rightOperand, cop, msg);
		}

		public int opcode() { return OPCODE_assumeeq + op.offset; }
		
		@Override
		public Code clone(int nLeftOperand, int nRightOperand) {
			return Code.Assume(type, nLeftOperand, nRightOperand, op, msg);
		}

		public boolean equals(Object o) {
			if (o instanceof Assume) {
				Assume ig = (Assume) o;
				return op == ig.op && msg.equals(ig.msg) && super.equals(ig);
			}
			return false;
		}

		public String toString() {
			return "assume" + op + " %" + leftOperand + ", %" + rightOperand
					+ " \"" + msg + "\"" + " : " + type;
		}
	}

	/**
	 * Reads a record value from an operand register, extracts the value of a
	 * given field and writes this to the target register. For example, the
	 * following Whiley code:
	 * 
	 * <pre>
	 * define Point as {int x, int y}
	 * 
	 * int f(Point p):
	 *     return p.x + p.y
	 * </pre>
	 * 
	 * can be translated into the following WYIL code:
	 * 
	 * <pre>
	 * int f({int x,int y} p):
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
		public Code clone(int nTarget, int nOperand) {
			return Code.FieldLoad(type, nTarget, nOperand, field);
		}

		public int opcode() { return OPCODE_fieldload; }
		
		public int hashCode() {
			return super.hashCode() + field.hashCode();
		}

		public Type fieldType() {
			return type.fields().get(field);
		}

		public boolean equals(Object o) {
			if (o instanceof FieldLoad) {
				FieldLoad i = (FieldLoad) o;
				return super.equals(i) && field.equals(i.field);
			}
			return false;
		}

		public String toString() {
			return "fieldload %" + target + " = %" + operand + " " + field
					+ " : " + type;
		}
	}

	/**
	 * Branches unconditionally to the given label. This is typically used for
	 * if/else statements. For example, the following Whiley code:
	 * 
	 * <pre>
	 * int f(int x):
	 *     if x >= 0:
	 *         x = 1
	 *     else:
	 *         x = -1
	 *     return x
	 * </pre>
	 * 
	 * can be translated into the following WYIL code:
	 * 
	 * <pre>
	 * int f(int x):
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
	 * <b>Note:</b> in WYIL bytecode, <i>such branches may only go forward</i>.
	 * Thus, a <code>goto</code> bytecode cannot be used to implement the
	 * back-edge of a loop. Rather, a loop block must be used for this purpose.
	 * </p>
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Goto extends Code {
		public final String target;

		private Goto(String target) {
			this.target = target;
		}

		public int opcode() { return OPCODE_goto; }
		
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
	 * <li><i>subset (ss) and subset-equals (sse)</i>. Both operands must have
	 * the given type, which additionally must be a set.</li>
	 * </ul>
	 * For example, the following Whiley code:
	 * 
	 * <pre>
	 * int f(int x, int y):
	 *     if x < y:
	 *         return -1
	 *     else if x > y:
	 *         return 1
	 *     else:
	 *         return 0
	 * </pre>
	 * 
	 * can be translated into the following WYIL code:
	 * 
	 * <pre>
	 * int f(int x, int y):
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
	 * <b>Note:</b> in WYIL bytecode, <i>such branches may only go forward</i>.
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

		public int opcode() { return OPCODE_ifeq + op.offset; }
		
		@Override
		public Code clone(int nLeftOperand, int nRightOperand) {
			return Code.If(type, nLeftOperand, nRightOperand, op, target);
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
		},
		ELEMOF(6) {
			public String toString() {
				return "in";
			}
		},
		SUBSET(7) {
			public String toString() {
				return "sb";
			}
		},
		SUBSETEQ(8) {
			public String toString() {
				return "sbe";
			}
		};
		public int offset;
		
		private Comparator(int offset) {
			this.offset = offset;
		}
	};

	/**
	 * Determine the inverse comparator, or null if no inverse exists.
	 * 
	 * @param cop
	 * @return
	 */
	public static Code.Comparator invert(Code.Comparator cop) {
		switch (cop) {
		case EQ:
			return Code.Comparator.NEQ;
		case NEQ:
			return Code.Comparator.EQ;
		case LT:
			return Code.Comparator.GTEQ;
		case LTEQ:
			return Code.Comparator.GT;
		case GT:
			return Code.Comparator.LTEQ;
		case GTEQ:
			return Code.Comparator.LT;
		}
		return null;
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
	 * int f(int|[int] x):
	 *     if x is [int]:
	 *         return |x|
	 *     else:
	 *         return x
	 * </pre>
	 * 
	 * can be translated into the following WYIL code:
	 * 
	 * <pre>
	 * int f(int|[int] x):
	 * body: 
	 *     ifis %0, [int] goto lab    : int|[int] 
	 *     return %0                  : int     
	 * .lab                                                
	 *     lengthof %0 = %0           : [int]               
	 *     return %0                  : int
	 * </pre>
	 * 
	 * Here, we see that, on the false branch, register <code>%0</code> is
	 * automatically given type <code>int</code>, whilst on the true branch it
	 * is automatically given type <code>[int]</code>.
	 * 
	 * <p>
	 * <b>Note:</b> in WYIL bytecode, <i>such branches may only go forward</i>.
	 * Thus, an <code>iftype</code> bytecode cannot be used to implement the
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
			super(type,leftOperand);			
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

		public int opcode() { return OPCODE_ifis; }
		
		public IfIs relabel(Map<String, String> labels) {
			String nlabel = labels.get(target);
			if (nlabel == null) {
				return this;
			} else {
				return IfIs(type, operand, rightOperand, nlabel);
			}
		}

		@Override
		public Code clone(int nOperand) {			
			return Code.IfIs(type, nOperand, rightOperand, target);
		}

		public int hashCode() {
			return type.hashCode() + 
					+ target.hashCode() + super.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof IfIs) {
				IfIs ig = (IfIs) o;
				return super.equals(o)
						&& rightOperand.equals(ig.rightOperand)
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
	 * int function(int(int) f, int x):
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
			AbstractSplitNaryAssignable<Type.FunctionOrMethod> {

		private IndirectInvoke(Type.FunctionOrMethod type, int target,
				int operand, int[] operands) {
			super(type, target, operand, operands);
		}

		public int opcode() {
			if(type instanceof Type.Function) {
				if(type.ret() != Type.T_VOID) {
					return OPCODE_indirectinvokefn;
				} else {
					return OPCODE_indirectinvokefnv;
				}
			} else {
				if(type.ret() != Type.T_VOID) {
					return OPCODE_indirectinvokemd;
				} else {
					return OPCODE_indirectinvokemdv;
				}
			}
		}
		
		@Override
		public Code clone(int nTarget, int nOperand, int[] nOperands) {
			return IndirectInvoke(type, nTarget, nOperand, nOperands);
		}

		public boolean equals(Object o) {
			return o instanceof IndirectInvoke && super.equals(o);
		}

		public String toString() {
			if (target != Code.NULL_REG) {
				return "indirectinvoke " + target + " = " + operand + " "
						+ arrayToString(operands) + " : " + type;
			} else {
				return "indirectinvoke " + operand + " = " + arrayToString(operands)
						+ " : " + type;
			}
		}
	}

	/**
	 * Read a boolean value from the operand register, inverts it and writes the
	 * result to the target register. For example, the following Whiley code:
	 * 
	 * <pre>
	 * bool f(bool x):
	 *     return !x
	 * </pre>
	 * 
	 * can be translated into the following WYIL:
	 * 
	 * <pre>
	 * bool f(bool x):
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

		public int opcode() { return OPCODE_not; }
		
		@Override
		public Code clone(int nTarget, int nOperand) {
			return Code.Not(nTarget, nOperand);
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
			return "not %" + target + " = %" + operand + " : " + type;
		}
	}

	/**
	 * Corresponds to a function or method call whose parameters are read from
	 * zero or more operand registers. If a return value is required, this is
	 * written to a target register afterwards. For example, the following
	 * Whiley code:
	 * 
	 * <pre>
	 * int g(int x, int y, int z):
	 *     return x * y * z
	 * 
	 * int f(int x, int y):
	 *     r = g(x,y,3)
	 *     return r + 1
	 * </pre>
	 * 
	 * can be translated into the following WYIL code:
	 * 
	 * <pre>
	 * int g(int x, int y, int z):
	 * body: 
	 *     mul %3 = %0, %1   : int                                    
	 *     mul %3 = %3, %2   : int                  
	 *     return %3         : int                         
	 * 
	 * int f(int x, int y):
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
			if(type instanceof Type.Function) {
				if(type.ret() != Type.T_VOID) {
					return OPCODE_invokefn;
				} else {
					return OPCODE_invokefnv;
				}				
			} else {
				if(type.ret() != Type.T_VOID) {
					return OPCODE_invokemd;
				} else {
					return OPCODE_invokemdv;
				}
			}
		}
		
		public int hashCode() {
			return name.hashCode() + super.hashCode();
		}

		@Override
		public Code clone(int nTarget, int[] nOperands) {
			return Code.Invoke(type, nTarget, nOperands, name);
		}

		public boolean equals(Object o) {
			if (o instanceof Invoke) {
				Invoke i = (Invoke) o;
				return name.equals(i.name) && super.equals(i);
			}
			return false;
		}

		public String toString() {
			if (target != Code.NULL_REG) {
				return "invoke %" + target + " = " + arrayToString(operands) + " "
						+ name + " : " + type;
			} else {
				return "invoke %" + arrayToString(operands) + " " + name + " : "
						+ type;
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
			if (type instanceof Type.Function) {
				return OPCODE_lambdafn;
			} else {
				return OPCODE_lambdamd;				
			}
		}

		public int hashCode() {
			return name.hashCode() + super.hashCode();
		}

		@Override
		public Code clone(int nTarget, int[] nOperands) {
			return Code.Lambda(type, nTarget, nOperands, name);
		}

		public boolean equals(Object o) {
			if (o instanceof Lambda) {
				Lambda i = (Lambda) o;
				return name.equals(i.name) && super.equals(i);
			}
			return false;
		}

		public String toString() {
			return "lambda %" + target + " = " + arrayToString(operands) + " "
					+ name + " : " + type;			
		}
	}
	
	/**
	 * Represents the labelled destination of a branch or loop statement.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static class Label extends Code {
		public final String label;

		private Label(String label) {
			this.label = label;
		}

		public int opcode() { return -1; }
		
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

	public enum BinListKind {
		APPEND(0) {
			public String toString() {
				return "append";
			}
		},
		LEFT_APPEND(1) {
			public String toString() {
				return "appendl";
			}
		},
		RIGHT_APPEND(2) {
			public String toString() {
				return "appendr";
			}
		};		
		public final int offset;
		private BinListKind(int offset) {
			this.offset = offset;
		}
	}

	/**
	 * Reads the (effective) list values from two operand registers, performs an
	 * operation (e.g. append) on them and writes the result back to a target
	 * register. For example, the following Whiley code:
	 * 
	 * <pre>
	 * [int] f([int] xs, [int] ys):
	 *    return xs ++ ys
	 * </pre>
	 * 
	 * can be translated into the following WYIL code:
	 * 
	 * <pre>
	 * [int] f([int] xs, [int] ys):
	 * body: 
	 *    append %2 = %0, %1   : [int]             
	 *    return %2            : [int]
	 * </pre>
	 * 
	 * This appends two the parameter lists together writting the new list into
	 * register <code>%2</code>.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class BinListOp extends
			AbstractBinaryAssignable<Type.EffectiveList> {
		public final BinListKind kind;

		private BinListOp(Type.EffectiveList type, int target, int leftOperand,
				int rightOperand, BinListKind operation) {
			super(type, target, leftOperand, rightOperand);
			if (operation == null) {
				throw new IllegalArgumentException(
						"ListAppend direction cannot be null");
			}
			this.kind = operation;
		}

		public int opcode() { return OPCODE_append + kind.offset; }
		
		@Override
		public Code clone(int nTarget, int nLeftOperand, int nRightOperand) {
			return Code.BinListOp(type, nTarget, nLeftOperand, nRightOperand,
					kind);
		}

		public int hashCode() {
			return super.hashCode() + kind.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof BinListOp) {
				BinListOp setop = (BinListOp) o;
				return super.equals(setop) && kind.equals(setop.kind);
			}
			return false;
		}

		public String toString() {
			return kind + " %" + target + " = %" + leftOperand + ", %"
					+ rightOperand + " : " + type;
		}
	}

	/**
	 * Reads an (effective) collection (i.e. a set, list or map) from the
	 * operand register, and writes its length into the target register. For
	 * example, the following Whiley code:
	 * 
	 * <pre>
	 * int f([int] ls):
	 *     return |ls|
	 * </pre>
	 * 
	 * translates to the following WYIL code:
	 * 
	 * <pre>
	 * int f([int] ls):
	 * body:                  
	 *     lengthof %0 = %0   : [int]               
	 *     return %0          : int
	 * </pre>
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class LengthOf extends
			AbstractUnaryAssignable<Type.EffectiveCollection> {
		private LengthOf(Type.EffectiveCollection type, int target, int operand) {
			super(type, target, operand);
		}

		public int opcode() { return OPCODE_lengthof; }
		
		protected Code clone(int nTarget, int nOperand) {
			return Code.LengthOf(type, nTarget, nOperand);
		}

		public boolean equals(Object o) {
			if (o instanceof LengthOf) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			return "lengthof %" + target + " = %" + operand + " : " + type;
		}
	}

	/**
	 * Reads the (effective) list value from a source operand register, and the
	 * integer values from two index operand registers, computes the sublist and
	 * writes the result back to a target register.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class SubList extends
			AbstractNaryAssignable<Type.EffectiveList> {

		private SubList(Type.EffectiveList type, int target, int[] operands) {
			super(type, target, operands);
		}

		public int opcode() { return OPCODE_sublist; }
		
		@Override
		public final Code clone(int nTarget, int[] nOperands) {
			return Code.SubList(type, nTarget, nOperands);
		}

		public boolean equals(Object o) {
			return o instanceof SubList && super.equals(o);
		}

		public String toString() {
			return "sublist %" + target + " = %" + operands[0] + ", %"
					+ operands[1] + ", %" + operands[2] + " : " + type;
		}
	}

	/**
	 * Reads an effective list or map from the source (left) operand register,
	 * and a key value from the key (right) operand register and returns the
	 * value associated with that key. If the key does not exist, then a fault
	 * is raised. For example, the following Whiley code:
	 * 
	 * <pre>
	 * string f({int=>string} map, int key):
	 *     return map[key]
	 * </pre>
	 * 
	 * can be translated into the following WYIL code:
	 * 
	 * <pre>
	 * string f({int->string} map, int key):
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
			AbstractBinaryAssignable<Type.EffectiveIndexible> {
		private IndexOf(Type.EffectiveIndexible type, int target, int sourceOperand,
				int keyOperand) {
			super(type, target, sourceOperand, keyOperand);
		}

		public int opcode() { return OPCODE_indexof; }
		
		protected Code clone(int nTarget, int nLeftOperand, int nRightOperand) {
			return Code.IndexOf(type, nTarget, nLeftOperand, nRightOperand);
		}

		public boolean equals(Object o) {
			if (o instanceof IndexOf) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			return "indexof %" + target + " = %" + leftOperand + ", %"
					+ rightOperand + " : " + type;
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
	 * int f(int x, int y):
	 *     x = x + 1
	 *     return x
	 * </pre>
	 * 
	 * can be translated into the following WYIL code:
	 * 
	 * <pre>
	 * int f(int x, int y):
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
		
		public int opcode() { return OPCODE_move; }

		protected Code clone(int nTarget, int nOperand) {
			return Code.Move(type, nTarget, nOperand);
		}

		public boolean equals(Object o) {
			if (o instanceof Move) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			return "move %" + target + " = %" + operand + " : " + type;
		}
	}

	/**
	 * Represents a block of code which loops continuously until e.g. a
	 * conditional branch is taken out of the block. For example:
	 * 
	 * <pre>
	 * int f():
	 *     r = 0
	 *     while r < 10:
	 *         r = r + 1
	 *     return r
	 * </pre>
	 * can be translated into the following WYIL code:
	 * <pre>
	 * int f():
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
	public static class Loop extends Code {
		public final String target;
		public final int[] modifiedOperands;

		private Loop(String target, int[] modifies) {
			this.target = target;
			this.modifiedOperands = modifies;
		}

		public int opcode() { return OPCODE_loop; }
		
		public Loop relabel(Map<String, String> labels) {
			String nlabel = labels.get(target);
			if (nlabel == null) {
				return this;
			} else {
				return Loop(nlabel, modifiedOperands);
			}
		}

		@Override
		public void registers(java.util.Set<Integer> registers) {
			for (int operand : modifiedOperands) {
				registers.add(operand);
			}
		}

		@Override
		public Code remap(Map<Integer, Integer> binding) {
			int[] nOperands = remapOperands(binding, modifiedOperands);
			if (nOperands != modifiedOperands) {
				return Code.Loop(target, nOperands);
			} else {
				return this;
			}
		}

		public int hashCode() {
			return target.hashCode() + Arrays.hashCode(modifiedOperands);
		}

		public boolean equals(Object o) {
			if (o instanceof Loop) {
				Loop f = (Loop) o;
				return target.equals(f.target)
						&& Arrays.equals(modifiedOperands, f.modifiedOperands);
			}
			return false;
		}

		public String toString() {
			return "loop " + arrayToString(modifiedOperands);
		}
	}

	/**
	 * Pops a set, list or map from the stack and iterates over every
	 * element it contains. A register is identified to hold the current value
	 * being iterated over.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class ForAll extends Loop {
		public final int sourceOperand;
		public final int indexOperand;
		public final Type.EffectiveCollection type;

		private ForAll(Type.EffectiveCollection type, int sourceOperand,
				int indexOperand, int[] modifies, String target) {
			super(target, modifies);
			this.type = type;
			this.sourceOperand = sourceOperand;
			this.indexOperand = indexOperand;
		}

		public int opcode() { return OPCODE_forall; }
		
		public ForAll relabel(Map<String, String> labels) {
			String nlabel = labels.get(target);
			if (nlabel == null) {
				return this;
			} else {
				return ForAll(type, sourceOperand, indexOperand,
						modifiedOperands, nlabel);
			}
		}

		@Override
		public void registers(java.util.Set<Integer> registers) {
			registers.add(indexOperand);
			registers.add(sourceOperand);
			super.registers(registers);
		}

		@Override
		public Code remap(Map<Integer, Integer> binding) {
			int[] nModifiedOperands = remapOperands(binding, modifiedOperands);
			Integer nIndexOperand = binding.get(indexOperand);
			Integer nSourceOperand = binding.get(sourceOperand);
			if (nSourceOperand != null || nIndexOperand != null
					|| nModifiedOperands != modifiedOperands) {
				nSourceOperand = nSourceOperand != null ? nSourceOperand
						: sourceOperand;
				nIndexOperand = nIndexOperand != null ? nIndexOperand
						: indexOperand;
				
				return Code.ForAll(type, nSourceOperand, nIndexOperand,
						nModifiedOperands, target);
			} else {
				return this;
			}
		}

		public int hashCode() {
			return super.hashCode() + sourceOperand + indexOperand
					+ Arrays.hashCode(modifiedOperands);
		}

		public boolean equals(Object o) {
			if (o instanceof ForAll) {
				ForAll f = (ForAll) o;
				return target.equals(f.target) && type.equals(f.type)
						&& sourceOperand == f.sourceOperand
						&& indexOperand == f.indexOperand
						&& Arrays.equals(modifiedOperands, f.modifiedOperands);
			}
			return false;
		}

		public String toString() {
			return "forall %" + indexOperand + " in %" + sourceOperand + " "
					+ arrayToString(modifiedOperands) + " : " + type;
		}
	}

	/**
	 * Represents a type which may appear on the left of an assignment
	 * expression. Lists, Dictionaries, Strings, Records and References are the
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
	 * An LVal with map type.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class MapLVal extends LVal<Type.EffectiveMap> {
		public final int keyOperand;

		public MapLVal(Type.EffectiveMap t, int keyOperand) {
			super(t);			
			this.keyOperand = keyOperand;
		}
	}

	/**
	 * An LVal with list type.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class ListLVal extends LVal<Type.EffectiveList> {
		public final int indexOperand;

		public ListLVal(Type.EffectiveList t, int indexOperand) {
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
	 * An LVal with string type.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class StringLVal extends LVal {
		public final int indexOperand;

		public StringLVal(int indexOperand) {
			super(Type.T_STRING);
			this.indexOperand = indexOperand;
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
			if (Type.isSubtype(Type.T_STRING, iter)) {
				iter = Type.T_CHAR;
				return new StringLVal(operands[operandIndex++]);
			} else if (Type.isSubtype(Type.Reference(Type.T_ANY), iter)) {
				Type.Reference proc = Type.effectiveReference(iter);
				iter = proc.element();
				return new ReferenceLVal(proc);
			} else if (iter instanceof Type.EffectiveList) {
				Type.EffectiveList list = (Type.EffectiveList) iter;
				iter = list.element();
				return new ListLVal(list, operands[operandIndex++]);
			} else if (iter instanceof Type.EffectiveMap) {
				Type.EffectiveMap dict = (Type.EffectiveMap) iter;
				iter = dict.value();
				return new MapLVal(dict, operands[operandIndex++]);
			} else if (iter instanceof Type.EffectiveRecord) {
				Type.EffectiveRecord rec = (Type.EffectiveRecord) iter;
				String field = fields.get(fieldIndex++);
				iter = rec.fields().get(field);
				return new RecordLVal(rec, field);
			} else {
				throw new IllegalArgumentException(
						"Invalid type for Code.Update");
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
	public static final class Update extends AbstractSplitNaryAssignable<Type>
			implements Iterable<LVal> {
		public final Type afterType;
		public final ArrayList<String> fields;

		private Update(Type beforeType, int target, int operand,
				int[] operands, Type afterType, Collection<String> fields) {
			super(beforeType, target, operand, operands);
			if (fields == null) {
				throw new IllegalArgumentException(
						"FieldStore fields argument cannot be null");
			}
			this.afterType = afterType;
			this.fields = new ArrayList<String>(fields);			
		}

		public int opcode() { return OPCODE_update; }
		
		public int level() {
			int base = 0;
			if (type instanceof Type.Reference) {
				base++;
			}
			return base + fields.size() + operands.length;
		}

		public Iterator<LVal> iterator() {
			return new UpdateIterator(afterType, level(), operands, fields);
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
				if (Type.isSubtype(Type.T_STRING, iter)) {
					iter = Type.T_CHAR;
				} else if (Type.isSubtype(Type.Reference(Type.T_ANY), iter)) {
					Type.Reference proc = Type.effectiveReference(iter);
					iter = proc.element();
				} else if (iter instanceof Type.EffectiveList) {
					Type.EffectiveList list = (Type.EffectiveList) iter;
					iter = list.element();
				} else if (iter instanceof Type.EffectiveMap) {
					Type.EffectiveMap dict = (Type.EffectiveMap) iter;
					iter = dict.value();
				} else if (iter instanceof Type.EffectiveRecord) {
					Type.EffectiveRecord rec = (Type.EffectiveRecord) iter;
					String field = fields.get(fieldIndex++);
					iter = rec.fields().get(field);
				} else {
					throw new IllegalArgumentException(
							"Invalid type for Code.Update");
				}
			}
			return iter;
		}
	
		@Override
		public final Code clone(int nTarget, int nOperand, int[] nOperands) {
			return Code.Update(type, nTarget, nOperand, nOperands, afterType,
					fields);
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
			String r = "%" + target;
			for (LVal lv : this) {
				if (lv instanceof ListLVal) {
					ListLVal l = (ListLVal) lv;
					r = r + "[%" + l.indexOperand + "]";
				} else if (lv instanceof StringLVal) {
					StringLVal l = (StringLVal) lv;
					r = r + "[%" + l.indexOperand + "]";
				} else if (lv instanceof MapLVal) {
					MapLVal l = (MapLVal) lv;
					r = r + "[%" + l.keyOperand + "]";
				} else if (lv instanceof RecordLVal) {
					RecordLVal l = (RecordLVal) lv;
					r = r + "." + l.field;
				} else {
					ReferenceLVal l = (ReferenceLVal) lv;
					r = "(*" + r + ")";
				}
			}
			return "update " + r + " %" + operand + " : " + type + " -> "
					+ afterType;
		}
	}

	/**
	 * Constructs a map value from zero or more key-value pairs on the
	 * stack. For each pair, the key must occur directly before the value on the
	 * stack. For example, consider the following Whiley function
	 * <code>f()</code>:
	 * 
	 * <pre>
	 * {int=>string} f():
	 *     return {1=>"Hello",2=>"World"}
	 * </pre>
	 * 
	 * This could be compiled into the following WYIL code using this bytecode:
	 * 
	 * <pre>
	 * {int->string} f():
	 * body:
	 *   const %1 = 1                   : int                           
	 *   const %2 = "Hello"             : string                  
	 *   const %3 = 2                   : int                           
	 *   const %4 = "World"             : string                  
	 *   newmap %0 = (%1, %2, %3, %4)   : {int=>string}
	 *   return %0                      : {int=>string}
	 * </pre>
	 * 
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class NewMap extends
			AbstractNaryAssignable<Type.Map> {

		private NewMap(Type.Map type, int target, int[] operands) {
			super(type, target, operands);
		}

		public int opcode() { return OPCODE_newmap; }		
		
		protected Code clone(int nTarget, int[] nOperands) {
			return Code.NewMap(type, nTarget, nOperands);
		}

		public boolean equals(Object o) {
			if (o instanceof NewMap) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			return "newmap %" + target + " = " + arrayToString(operands) + " : " + type;
		}
	}

	/**
	 * Constructs a new record value from the values of zero or more operand
	 * register, each of which is associated with a field name. The new record
	 * value is then written into the target register. For example, the
	 * following Whiley code:
	 * 
	 * <pre>
	 * define Point as {real x, real y}
	 * 
	 * Point f(real x, real y):
	 *     return {x: x, y: x}
	 * </pre>
	 * 
	 * can be translated into the following WYIL:
	 * 
	 * <pre>
	 * {real x,real y} f(real x, real y):
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

		protected Code clone(int nTarget, int[] nOperands) {
			return Code.NewRecord(type, nTarget, nOperands);
		}

		public int opcode() { return OPCODE_newrecord; }
		
		public boolean equals(Object o) {
			if (o instanceof NewRecord) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			return "newrecord %" + target + " = " + arrayToString(operands) + " : "
					+ type;
		}
	}

	/**
	 * Constructs a new tuple value from the values given by zero or more
	 * operand registers. The new tuple is then written into the target
	 * register. For example, the following Whiley code:
	 * 
	 * <pre>
	 * (int,int) f(int x, int y):
	 *     return x,y
	 * </pre>
	 * 
	 * can be translated into the following WYIL code:
	 * 
	 * <pre>
	 * (int,int) f(int x, int y):
	 * body: 
	 *     assign %3 = %0          : int                   
	 *     assign %4 = %1          : int                   
	 *     newtuple %2 = (%3, %4)  : (int,int)         
	 *     return %2               : (int,int)
	 * </pre>
	 * 
	 * This writes the tuple value generated from <code>(x,y)</code> into
	 * register <code>%2</code> and returns it.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class NewTuple extends AbstractNaryAssignable<Type.Tuple> {

		private NewTuple(Type.Tuple type, int target, int[] operands) {
			super(type, target, operands);
		}

		public int opcode() { return OPCODE_newtuple; }
		
		protected Code clone(int nTarget, int[] nOperands) {
			return Code.NewTuple(type, nTarget, nOperands);
		}

		public boolean equals(Object o) {
			if (o instanceof NewTuple) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			return "newtuple %" + target + " = " + arrayToString(operands) + " : "
					+ type;
		}
	}

	/**
	 * Constructs a new set value from the values given by zero or more operand
	 * registers. The new set is then written into the target register. For
	 * example, the following Whiley code:
	 * 
	 * <pre>
	 * {int} f(int x, int y, int z):
	 *     return {x,y,z}
	 * </pre>
	 * 
	 * can be translated into the following WYIL code:
	 * 
	 * <pre>
	 * [int] f(int x, int y, int z):
	 * body: 
	 *    assign %4 = %0             : int                   
	 *    assign %5 = %1             : int                   
	 *    assign %6 = %2             : int                   
	 *    newset %3 = (%4, %5, %6)   : [int]            
	 *    return %3                  : [int]
	 * </pre>
	 * 
	 * Writes the set value given by <code>{x,y,z}</code> into register
	 * <code>%3</code> and returns it.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class NewSet extends AbstractNaryAssignable<Type.Set> {

		private NewSet(Type.Set type, int target, int[] operands) {
			super(type, target, operands);
		}
		
		public int opcode() { return OPCODE_newset; }

		protected Code clone(int nTarget, int[] nOperands) {
			return Code.NewSet(type, nTarget, nOperands);
		}

		public boolean equals(Object o) {
			if (o instanceof NewSet) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			return "newset %" + target + " = " + arrayToString(operands) + " : " + type;
		}
	}

	/**
	 * Constructs a new list value from the values given by zero or more operand
	 * registers. The new list is then written into the target register. For
	 * example, the following Whiley code:
	 * 
	 * <pre>
	 * [int] f(int x, int y, int z):
	 *     return [x,y,z]
	 * </pre>
	 * 
	 * can be translated into the following WYIL code:
	 * 
	 * <pre>
	 * [int] f(int x, int y, int z):
	 * body: 
	 *    assign %4 = %0             : int                   
	 *    assign %5 = %1             : int                   
	 *    assign %6 = %2             : int                   
	 *    newlist %3 = (%4, %5, %6)  : [int]            
	 *    return %3                  : [int]
	 * </pre>
	 * 
	 * Writes the list value given by <code>[x,y,z]</code> into register
	 * <code>%3</code> and returns it.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class NewList extends AbstractNaryAssignable<Type.List> {

		private NewList(Type.List type, int target, int[] operands) {
			super(type, target, operands);
		}

		public int opcode() { return OPCODE_newlist; }
		
		protected Code clone(int nTarget, int[] nOperands) {
			return Code.NewList(type, nTarget, nOperands);
		}

		public boolean equals(Object o) {
			if (o instanceof NewList) {
				return super.equals(operands);
			}
			return false;
		}

		public String toString() {
			return "newlist %" + target + " = " + arrayToString(operands) + " : "
					+ type;
		}
	}

	/**
	 * Represents a no-operation bytecode which, as the name suggests, does
	 * nothing.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Nop extends Code {
		private Nop() {
		}

		@Override
		public int opcode() { return OPCODE_nop; }
		
		public String toString() {
			return "nop";
		}
	}

	/**
	 * Returns from the enclosing function or method, possibly returning a
	 * value. For example, the following Whiley code:
	 * 
	 * <pre>
	 * int f(int x, int y):
	 *     return x + y
	 * </pre>
	 * 
	 * can be translated into the following WYIL:
	 * 
	 * <pre>
	 * int f(int x, int y):
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
			if (type == Type.T_VOID && operand != NULL_REG) {
				throw new IllegalArgumentException(
						"Return with void type cannot have target register.");
			} else if (type != Type.T_VOID && operand == NULL_REG) {
				throw new IllegalArgumentException(
						"Return with non-void type must have target register.");
			}
		}

		@Override
		public int opcode() {
			if(type == Type.T_VOID) {
				return OPCODE_returnv;
			} else {
				return OPCODE_return; 
			}
		}

		public Code clone(int nOperand) {
			return new Return(type, nOperand);
		}

		public boolean equals(Object o) {
			if (o instanceof Return) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			if (operand != Code.NULL_REG) {
				return "return %" + operand + " : " + type;
			} else {
				return "return";
			}
		}
	}

	public enum BinSetKind {
		UNION(0) {
			public String toString() {
				return "union";
			}
		}, 
		LEFT_UNION(1) {
			public String toString() {
				return "unionl";
			}
		},
		RIGHT_UNION(2) {
			public String toString() {
				return "unionl";
			}
		},
		INTERSECTION(3) {
			public String toString() {
				return "intersect";
			}
		},
		LEFT_INTERSECTION(4) {
			public String toString() {
				return "intersectl";
			}
		},
		RIGHT_INTERSECTION(5) {
			public String toString() {
				return "intersectr";
			}
		},
		DIFFERENCE(6) {
			public String toString() {
				return "diff";
			}
		},
		LEFT_DIFFERENCE(7) {
			public String toString() {
				return "diffl";
			}
		};
		public final int offset;
		
		private BinSetKind(int offset) {
			this.offset = offset;
		}
	}

	/**
	 * <p>
	 * A binary operation which reads two set values from the operand registers,
	 * performs an operation on them and writes the result to the target
	 * register. The binary set operators are:
	 * </p>
	 * <ul>
	 * <li><i>union, intersection, difference</i>. Both operands must be have
	 * the given (effective) set type. same type is produced.</li>
	 * <li><i>left union, left intersection, left difference</i>. The left
	 * operand must have the given (effective) set type, whilst the right
	 * operand has the given (effective) set element type.</li>
	 * <li><i>right union, right intersection</i>. The right operand must have
	 * the given (effective) set type, whilst the left operand has the given
	 * (effective) set element type.</li>
	 * </ul>
	 * For example, the following Whiley code:
	 * 
	 * <pre>
	 * {int} f({int} xs, {int} ys):
	 *     return xs + ys // set union
	 * 
	 * {int} g(int x, {int} ys):
	 *     return {x} & ys // set intersection
	 * 
	 * {int} h({int} xs, int y):
	 *     return xs - {y} // set difference
	 * </pre>
	 * 
	 * can be translated into the following WYIL code:
	 * 
	 * <pre>
	 * {int} f({int} xs, {int} ys):
	 * body: 
	 *     union %2 = %0, %1  : {int}               
	 *     return %2          : {int}                       
	 * 
	 * {int} g(int x, {int} ys):
	 * body: 
	 *     rintersect %2 = %0, %1  : {int}           
	 *     return %2               : {int}
	 *     
	 * {int} h({int} xs, int y):
	 * body: 
	 *     ldiff %2 = %0, %1    : {int}                
	 *     return %2            : {int}
	 * </pre>
	 * 
	 * Here, we see that the purpose of the <i>left-</i> and <i>right-</i>
	 * operations is to avoid creating a temporary set in the common case of a
	 * single element set on one side. This is largely an optimisation and it is
	 * expected that the front-end of the compiler will spots such situations
	 * and compile them down appropriately.
	 * 
	 * @author David J. Pearce
	 */
	public static final class BinSetOp extends
			AbstractBinaryAssignable<Type.EffectiveSet> {
		public final BinSetKind kind;

		private BinSetOp(Type.EffectiveSet type, int target, int leftOperand,
				int rightOperand, BinSetKind operation) {
			super(type, target, leftOperand, rightOperand);
			if (operation == null) {
				throw new IllegalArgumentException(
						"SetOp operation cannot be null");
			}
			this.kind = operation;
		}

		@Override
		public int opcode() {			
			return OPCODE_union + kind.offset;			
		}
		
		protected Code clone(int nTarget, int nLeftOperand, int nRightOperand) {
			return Code.BinSetOp(type, nTarget, nLeftOperand, nRightOperand,
					kind);
		}

		public int hashCode() {
			return kind.hashCode() + super.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof BinSetOp) {
				BinSetOp setop = (BinSetOp) o;
				return kind.equals(setop.kind) && super.equals(o);
			}
			return false;
		}

		public String toString() {
			return kind + " %" + target + " = %" + leftOperand + ", %"
					+ rightOperand + " : " + type;
		}
	}

	public enum BinStringKind {
		APPEND(0) {
			public String toString() {
				return "sappend";
			}
		},
		LEFT_APPEND(1) {
			public String toString() {
				return "sappendl";
			}
		},
		RIGHT_APPEND(2) {
			public String toString() {
				return "sappendr";
			}
		};
		public final int offset;
		
		private BinStringKind(int offset) {
			this.offset = offset;
		}
	}

	/**
	 * <p>
	 * A binary operation which reads two string values from the operand
	 * registers, performs an operation (append) on them and writes the result
	 * to the target register. The binary set operators are:
	 * </p>
	 * <ul>
	 * <li><i>append</i>. Both operands must be have string type.</li>
	 * <li><i>left append</i>. The left operand must have string type, whilst
	 * the right operand has char type.</li>
	 * <li><i>right append</i>. The right operand must have string type, whilst
	 * the left operand has char type.</li>
	 * </ul>
	 * For example, the following Whiley code:
	 * 
	 * <pre>
	 * string f(string xs, string ys):
	 *     return xs + ys
	 * 
	 * string g(string xs, char y):
	 *     return xs + y
	 * </pre>
	 * 
	 * can be translated into the following WYIL code:
	 * 
	 * <pre>
	 * string f(string xs, string ys):
	 * body: 
	 *     strappend %2 = %0, %2    : string         
	 *     return %2                : string
	 *     
	 * string g(string xs, char y):
	 * body: 
	 *     strappend_l %2 = %0, %1  : string       
	 *     return %2                : string
	 * </pre>
	 * 
	 * Here, we see that the purpose of the <i>left-</i> and <i>right-</i>
	 * operations is to avoid creating a temporary string in the common case of
	 * a single char being appended.
	 * 
	 * @author David J. Pearce
	 */
	public static final class BinStringOp extends
			AbstractBinaryAssignable<Type.Strung> {
		public final BinStringKind kind;

		private BinStringOp(int target, int leftOperand, int rightOperand,
				BinStringKind operation) {
			super(Type.T_STRING, target, leftOperand, rightOperand);
			if (operation == null) {
				throw new IllegalArgumentException(
						"StringBinOp operation cannot be null");
			}
			this.kind = operation;
		}

		@Override
		public int opcode() {			
			return OPCODE_sappend + kind.offset;			
		}
		
		protected Code clone(int nTarget, int nLeftOperand, int nRightOperand) {
			return Code.BinStringOp(nTarget, nLeftOperand, nRightOperand,
					kind);
		}

		public boolean equals(Object o) {
			if (o instanceof BinStringOp) {
				BinStringOp setop = (BinStringOp) o;
				return kind.equals(setop.kind) && super.equals(o);
			}
			return false;
		}

		public String toString() {
			return kind + " %" + target + " = %" + leftOperand + ", %"
					+ rightOperand + " : " + type;
		}
	}

	/**
	 * Reads the string value from a source operand register, and the integer
	 * values from two index operand registers, computes the substring and
	 * writes the result back to a target register.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class SubString extends AbstractNaryAssignable {

		private SubString(int target, int[] operands) {
			super(Type.T_STRING, target, operands);
		}

		@Override
		public int opcode() {			
			return OPCODE_substring;			
		}
				
		@Override
		public final Code clone(int nTarget, int[] nOperands) {
			return Code.SubString(nTarget, nOperands);
		}

		public boolean equals(Object o) {
			return o instanceof SubString && super.equals(o);
		}

		public String toString() {
			return "substr %" + target + " = %" + operands[0] + ", %"
					+ operands[1] + ", %" + operands[2] + " : " + type;
		}
	}

	/**
	 * Performs a multi-way branch based on the value contained in the operand
	 * register. A <i>dispatch table</i> is provided which maps individual
	 * matched values to their destination labels. For example, the following
	 * Whiley code:
	 * 
	 * <pre>
	 * string f(int x):
	 *     switch x:
	 *         case 1:
	 *             return "ONE"
	 *         case 2:
	 *             return "TWO"
	 *         default:
	 *             return "OTHER"
	 * </pre>
	 * 
	 * can be translated into the following WYIL code:
	 * 
	 * <pre>
	 * string f(int x):
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
			super(type,operand);
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
				return Code.Switch(type, operand, defaultTarget, nbranches);
			} else {
				return Code.Switch(type, operand, nlabel, nbranches);
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
		public Code clone(int nOperand) {
			return new Switch(type, nOperand, defaultTarget, branches);
		}

	}

	/**
	 * Throws an exception containing the value in the given operand register.
	 * For example, the following Whiley Code:
	 * 
	 * <pre>
	 * int f(int x) throws string:
	 *     if x < 0:
	 *         throw "ERROR"
	 *     else:
	 *         return 1
	 * </pre>
	 * 
	 * can be translated into the following WYIL code:
	 * 
	 * <pre>
	 * int f(int x) throws string:
	 * body:             
	 *     const %1 = 0 : int                      
	 *     ifge %0, %1 goto blklab0 : int          
	 *     const %1 = "ERROR" : string             
	 *     throw %1 : string                       
	 * .blklab0                                
	 *     const %1 = 1 : int                      
	 *     return %1 : int
	 * </pre>
	 * 
	 * Here, we see an exception containing a <code>string</code> value will be
	 * thrown when the parameter <code>x</code> is negative.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Throw extends AbstractUnaryOp<Type> {
		private Throw(Type type, int operand) {
			super(type, operand);
		}

		@Override
		public int opcode() {			
			return OPCODE_throw;			
		}
		
		@Override
		public Code clone(int nOperand) {
			return Code.Throw(type, nOperand);
		}

		public boolean equals(Object o) {
			if (o instanceof Throw) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			return "throw %" + operand + " : " + type;
		}
	}

	/**
	 * Represents a try-catch block within which specified exceptions will
	 * caught and processed within a handler. For example, the following Whiley
	 * code:
	 * 
	 * <pre>
	 * int f(int x) throws Error:
	 *     ...
	 * 
	 * int g(int x):
	 *     try:
	 *         x = f(x)
	 *     catch(Error e):
	 *         return 0
	 * </pre>
	 * 
	 * can be translated into the following WYIL code:
	 * 
	 * <pre>
	 * int f(int x):
	 * body: 
	 *     ...                        
	 * 
	 * int g(int x):
	 * body: 
	 *     trycatch Error -> lab2          
	 *        assign %3 = %0           : int                   
	 *        invoke %0 = (%3) test:f  : int(int) throws {string msg}
	 *     return
	 * .lab2                          
	 *     const %3 = 0                : int                      
	 *     return %3                   : int
	 * </pre>
	 * 
	 * Here, we see that within the try-catch block control is transferred to
	 * <code>lab2</code> if an exception of type <code>Error</code> is thrown.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class TryCatch extends Code {
		public final int operand;
		public final String target;
		public final ArrayList<Pair<Type, String>> catches;

		TryCatch(int operand, String label,
				Collection<Pair<Type, String>> catches) {
			this.operand = operand;
			this.catches = new ArrayList<Pair<Type, String>>(catches);
			this.target = label;
		}

		@Override
		public int opcode() {			
			return OPCODE_trycatch;			
		}
		
		@Override
		public void registers(java.util.Set<Integer> registers) {
			registers.add(operand);
		}

		@Override
		public Code remap(Map<Integer, Integer> binding) {
			Integer nOperand = binding.get(operand);
			if (nOperand != null) {
				return Code.TryCatch(nOperand, target, catches);
			}
			return this;
		}

		public TryCatch relabel(Map<String, String> labels) {
			ArrayList<Pair<Type, String>> nbranches = new ArrayList();
			for (Pair<Type, String> p : catches) {
				String nlabel = labels.get(p.second());
				if (nlabel == null) {
					nbranches.add(p);
				} else {
					nbranches.add(new Pair(p.first(), nlabel));
				}
			}

			String ntarget = labels.get(target);
			if (ntarget != null) {
				return TryCatch(operand, ntarget, nbranches);
			} else {
				return TryCatch(operand, target, nbranches);
			}
		}

		public int hashCode() {
			return operand + target.hashCode() + catches.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof TryCatch) {
				TryCatch ig = (TryCatch) o;
				return operand == ig.operand && target.equals(ig.target)
						&& catches.equals(ig.catches);
			}
			return false;
		}

		public String toString() {
			String table = "";
			boolean firstTime = true;
			for (Pair<Type, String> p : catches) {
				if (!firstTime) {
					table += ", ";
				}
				firstTime = false;
				table += p.first() + "->" + p.second();
			}
			return "trycatch " + table;
		}
	}

	/**
	 * Marks the end of a try-catch block.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class TryEnd extends Label {
		TryEnd(String label) {
			super(label);
		}

		public TryEnd relabel(Map<String, String> labels) {
			String nlabel = labels.get(label);
			if (nlabel == null) {
				return this;
			} else {
				return TryEnd(nlabel);
			}
		}

		public int hashCode() {
			return label.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof TryEnd) {
				TryEnd e = (TryEnd) o;
				return e.label.equals(label);
			}
			return false;
		}

		public String toString() {
			return "tryend " + label;
		}
	}

		
	/**
	 * Corresponds to a bitwise inversion operation, which reads a byte value
	 * from the operand register, inverts it and writes the result to the target
	 * resgister. For example, the following Whiley code:
	 * 
	 * <pre>
	 * byte f(byte x):
	 *    return ~x
	 * </pre>
	 * 
	 * can be translated into the following WYIL code:
	 * 
	 * <pre>
	 * byte f(byte x):
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
		
		protected Code clone(int nTarget, int nOperand) {
			return Code.Invert(type, nTarget, nOperand);
		}

		public boolean equals(Object o) {
			if (o instanceof Invert) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			return "invert %" + target + " = %" + operand + " : " + type;
		}
	}

	/**
	 * Instantiate a new object from the value in a given operand register, and
	 * write the result (a reference to that object) to a given target register.
	 * For example, the following Whiley code:
	 * 
	 * <pre>
	 * define PointObj as ref {real x, real y}
	 * 
	 * PointObj f(real x, real y):
	 *     return new {x: x, y: y}
	 * </pre>
	 * 
	 * can be translated into the following WYIL code:
	 * 
	 * <pre>
	 * ref {real x,real y} f(int x, int y):
	 * body: 
	 *     newrecord %2 = (%0, %1)  : {real x,real y}   
	 *     newobject %2 = %2        : ref {real x,real y}  
	 *     return %2                : ref {real x,real y}
	 * </pre>
	 * 
	 * <b>NOTE:</b> objects are unlike other data types in WYIL, in that they
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
		
		protected Code clone(int nTarget, int nOperand) {
			return Code.NewObject(type, nTarget, nOperand);
		}

		public boolean equals(Object o) {
			if (o instanceof NewObject) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			return "newobject %" + target + " = %" + operand + " : " + type;
		}
	}

	/**
	 * Read a tuple value from the operand register, extract the value it
	 * contains at a given index and write that to the target register. For
	 * example, the following Whiley code:
	 * 
	 * <pre>
	 * int f(int,int tup):
	 *     return tup[0]
	 * </pre>
	 * 
	 * can be translated into the following WYIL code:
	 * 
	 * <pre>
	 * int f(int,int tup):
	 * body: 
	 *     tupleload %0 = %0 0  : int,int            
	 *     return %0            : int
	 * </pre>
	 * 
	 * This simply reads the parameter <code>x</code> stored in register
	 * <code>%0</code>, and returns the value stored at index <code>0</code>.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class TupleLoad extends
			AbstractUnaryAssignable<Type.EffectiveTuple> {
		public final int index;

		private TupleLoad(Type.EffectiveTuple type, int target, int operand,
				int index) {
			super(type, target, operand);
			this.index = index;
		}

		@Override
		public int opcode() {			
			return OPCODE_tupleload;			
		}
		
		protected Code clone(int nTarget, int nOperand) {
			return Code.TupleLoad(type, nTarget, nOperand, index);
		}

		public boolean equals(Object o) {
			if (o instanceof TupleLoad) {
				TupleLoad i = (TupleLoad) o;
				return index == i.index && super.equals(o);
			}
			return false;
		}

		public String toString() {
			return "tupleload %" + target + " = %" + operand + " " + index
					+ " : " + type;
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

		protected Code clone(int nTarget, int nOperand) {
			return Code.Dereference(type, nTarget, nOperand);
		}

		public boolean equals(Object o) {
			if (o instanceof Dereference) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			return "deref %" + target + " = %" + operand + " : " + type;
		}
	}

	public enum UnArithKind {
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
		
		private UnArithKind(int offset) {
			this.offset = offset;
		}
	};
	
	/**
	 * Read a number (int or real) from the operand register, perform a unary
	 * arithmetic operation on it (e.g. negation) and writes the result to the
	 * target register. For example, the following Whiley code:
	 * 
	 * <pre>
	 * int f(int x):
	 *     return -x
	 * </pre>
	 * 
	 * can be translated into the following WYIL:
	 * 
	 * <pre>
	 * int f(int x):
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
	public static final class UnArithOp extends AbstractUnaryAssignable<Type> {
		public final UnArithKind kind;

		private UnArithOp(Type type, int target, int operand, UnArithKind uop) {
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
		public Code clone(int nTarget, int nOperand) {
			return Code.UnArithOp(type, nTarget, nOperand, kind);
		}

		public int hashCode() {
			return kind.hashCode() + super.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof UnArithOp) {
				UnArithOp bo = (UnArithOp) o;
				return kind.equals(bo.kind) && super.equals(bo);
			}
			return false;
		}

		public String toString() {
			return kind + " %" + target + " = %" + operand + " : " + type;
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
			super(type, NULL_REG, operands);
		}

		@Override
		public int opcode() {
			return OPCODE_void;
		}
		
		protected Code clone(int nTarget, int[] nOperands) {
			return Code.Void(type, nOperands);
		}

		public boolean equals(Object o) {
			if (o instanceof Void) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			return "void " + arrayToString(operands);
		}
	}

	private static String arrayToString(int... operands) {
		String r = "(";
		for (int i = 0; i != operands.length; ++i) {
			if (i != 0) {
				r = r + ", ";
			}
			if(operands[i] == Code.NULL_REG) {
				r = r + "_";
			} else {
				r = r + "%" + operands[i];
			}
		}
		return r + ")";
	}

	private static int[] toIntArray(Collection<Integer> operands) {
		int[] ops = new int[operands.size()];
		int i = 0;
		for (Integer o : operands) {
			ops[i++] = o;
		}
		return ops;
	}

	private static int[] remapOperands(Map<Integer, Integer> binding, int[] operands) {
		int[] nOperands = operands;
		for (int i = 0; i != nOperands.length; ++i) {
			int o = operands[i];
			Integer nOperand = binding.get(o);
			if (nOperand != null) {
				if (nOperands == operands) {
					nOperands = Arrays.copyOf(operands, operands.length);
				}
				nOperands[i] = nOperand;
			}
		}
		return nOperands;
	}

	private static final ArrayList<Code> values = new ArrayList<Code>();
	private static final HashMap<Code, Integer> cache = new HashMap<Code, Integer>();

	private static <T extends Code> T get(T type) {
		Integer idx = cache.get(type);
		if (idx != null) {
			return (T) values.get(idx);
		} else {
			cache.put(type, values.size());
			values.add(type);
			return type;
		}
	}
	
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
	public static final int OPCODE_asserteq = 9  + FMT_BINARYOP; // +STRINGIDX
	public static final int OPCODE_assertne = 10 + FMT_BINARYOP; // +STRINGIDX
	public static final int OPCODE_assertlt = 11 + FMT_BINARYOP; // +STRINGIDX
	public static final int OPCODE_assertle = 12 + FMT_BINARYOP; // +STRINGIDX
	public static final int OPCODE_assertgt = 13 + FMT_BINARYOP; // +STRINGIDX
	public static final int OPCODE_assertge = 14 + FMT_BINARYOP; // +STRINGIDX
	public static final int OPCODE_assertel = 15 + FMT_BINARYOP; // +STRINGIDX
	public static final int OPCODE_assertss = 16 + FMT_BINARYOP; // +STRINGIDX
	public static final int OPCODE_assertse = 17 + FMT_BINARYOP; // +STRINGIDX	
	public static final int OPCODE_assumeeq = 18 + FMT_BINARYOP; // +STRINGIDX
	public static final int OPCODE_assumene = 19 + FMT_BINARYOP; // +STRINGIDX
	public static final int OPCODE_assumelt = 20 + FMT_BINARYOP; // +STRINGIDX
	public static final int OPCODE_assumele = 21 + FMT_BINARYOP; // +STRINGIDX
	public static final int OPCODE_assumegt = 22 + FMT_BINARYOP; // +STRINGIDX
	public static final int OPCODE_assumege = 23 + FMT_BINARYOP; // +STRINGIDX
	public static final int OPCODE_assumeel = 24 + FMT_BINARYOP; // +STRINGIDX
	public static final int OPCODE_assumess = 25 + FMT_BINARYOP; // +STRINGIDX
	public static final int OPCODE_assumese = 26 + FMT_BINARYOP; // +STRINGIDX
	
	// =========================================================================
	// Binary Assignables
	// =========================================================================
	public static final int OPCODE_add         = 0  + FMT_BINARYASSIGN;
	public static final int OPCODE_sub         = 1  + FMT_BINARYASSIGN;  
	public static final int OPCODE_mul         = 2  + FMT_BINARYASSIGN; 
	public static final int OPCODE_div         = 3  + FMT_BINARYASSIGN; 
	public static final int OPCODE_rem         = 4  + FMT_BINARYASSIGN; 
	public static final int OPCODE_range       = 5  + FMT_BINARYASSIGN; 
	public static final int OPCODE_bitwiseor   = 6  + FMT_BINARYASSIGN; 
	public static final int OPCODE_bitwisexor  = 7  + FMT_BINARYASSIGN; 
	public static final int OPCODE_bitwiseand  = 8  + FMT_BINARYASSIGN; 
	public static final int OPCODE_lshr        = 9  + FMT_BINARYASSIGN; 
	public static final int OPCODE_rshr        = 10 + FMT_BINARYASSIGN; 	
	public static final int OPCODE_indexof     = 11 + FMT_BINARYASSIGN;	
	public static final int OPCODE_union       = 12 + FMT_BINARYASSIGN;   	
	public static final int OPCODE_unionl      = 13 + FMT_BINARYASSIGN; 
	public static final int OPCODE_unionr      = 14 + FMT_BINARYASSIGN; 
	public static final int OPCODE_intersect   = 15 + FMT_BINARYASSIGN; 
	public static final int OPCODE_intersectl  = 16 + FMT_BINARYASSIGN; 
	public static final int OPCODE_intersectr  = 17 + FMT_BINARYASSIGN; 	
	public static final int OPCODE_difference  = 18 + FMT_BINARYASSIGN; 
	public static final int OPCODE_differencel = 19 + FMT_BINARYASSIGN; 					
	public static final int OPCODE_append      = 20 + FMT_BINARYASSIGN;
	public static final int OPCODE_appendl     = 21 + FMT_BINARYASSIGN;
	public static final int OPCODE_appendr     = 22 + FMT_BINARYASSIGN;
	public static final int OPCODE_sappend     = 23 + FMT_BINARYASSIGN;
	public static final int OPCODE_sappendl    = 24 + FMT_BINARYASSIGN;
	public static final int OPCODE_sappendr    = 25 + FMT_BINARYASSIGN;
	
	// =========================================================================
	// Nary Operators
	// =========================================================================	
	public static final int OPCODE_loop              = 0 + FMT_NARYOP;	
	public static final int OPCODE_forall            = 1 + FMT_NARYOP;	
	public static final int OPCODE_void              = 2 + FMT_NARYOP;
	public static final int OPCODE_indirectinvokefnv = 3 + FMT_NARYOP;
	public static final int OPCODE_indirectinvokemdv = 4 + FMT_NARYOP;
	public static final int OPCODE_invokefnv         = 5 + FMT_NARYOP; // +NAMEIDX	
	public static final int OPCODE_invokemdv         = 6 + FMT_NARYOP; // +NAMEIDX	
		
	// =========================================================================
	// Nary Assignables
	// =========================================================================		
	public static final int OPCODE_newlist          = 0 + FMT_NARYASSIGN;
	public static final int OPCODE_newset           = 1 + FMT_NARYASSIGN;
	public static final int OPCODE_newmap           = 2 + FMT_NARYASSIGN;
	public static final int OPCODE_newtuple         = 3 + FMT_NARYASSIGN;	
	public static final int OPCODE_indirectinvokefn = 4 + FMT_NARYASSIGN;
	public static final int OPCODE_indirectinvokemd = 5 + FMT_NARYASSIGN;
	public static final int OPCODE_sublist          = 6 + FMT_NARYASSIGN;
	public static final int OPCODE_substring        = 7 + FMT_NARYASSIGN;	
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
	
	// this is where I will locate the WIDE and WIDEWIDE Markers
	public static final int OPCODE_wide                = 29 + FMT_OTHER;
	public static final int OPCODE_widerest            = 30 + FMT_OTHER;
	public static final int OPCODE_widewide            = 31 + FMT_OTHER;	
}
