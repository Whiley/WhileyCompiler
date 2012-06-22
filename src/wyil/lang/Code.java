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
 * stack-based bytecodes, similar to the Java Virtual Machine. The frame of each
 * function/method consists of zero or more <i>local variables</i> (a.k.a
 * registers) and a <i>stack of unbounded depth</i>. Bytecodes may push/pop
 * values from the stack, and store/load them from local variables. Like Java
 * bytecode, WYIL uses unstructured control-flow and allows variables to take on
 * different types at different points. For example:
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
 *   var r, $2, item
 *   const 0 : int                           
 *   store r : int                           
 *   move data : [int]                       
 *   forall item [r] : [int]                 
 *       move r : int                            
 *       load item : int                         
 *       add : int                               
 *       store r : int                           
 *   move r : int                            
 *   return : int
 * </pre>
 * 
 * Here, we can see that every bytecode is associated with one (or more) types.
 * These types are inferred by the compiler during type propagation.
 * 
 * @author David J. Pearce
 * 
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
			COp cop, String message) {
		return get(new Assert(type, leftOperand, rightOperand, cop, message));
	}

	public static BinOp BinOp(Type type, int target, int leftOperand,
			int rightOperand, BOp op) {
		return get(new BinOp(type, target, leftOperand, rightOperand, op));
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
	public static Const Const(int target, Value constant) {
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
	public static Copy Copy(Type type, int target, int operand) {
		return get(new Copy(type, target, operand));
	}

	public static Convert Convert(Type from, int target, int operand, Type to) {
		return get(new Convert(from, target, operand, to));
	}

	public static final Debug Debug(int operand) {
		return get(new Debug(operand));
	}

	public static LoopEnd End(String label) {
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
	
	public static ListOp ListOp(Type.EffectiveList type, int target,
			int leftOperand, int rightOperand, ListOperation dir) {
		return get(new ListOp(type, target, leftOperand, rightOperand, dir));
	}

	/**
	 * Construct a <code>listload</code> bytecode which reads a value from a
	 * given index in a given list.
	 * 
	 * @param type
	 *            --- list type.
	 * @return
	 */
	public static IndexOf IndexOf(Type.EffectiveMap type, int target,
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
			int sourceOperand, int indexOperand, Collection<Integer> modifiedOperands, String label) {
		return get(new ForAll(type, sourceOperand, indexOperand,
				toIntArray(modifiedOperands), label));
	}

	public static ForAll ForAll(Type.EffectiveCollection type,
			int sourceOperand, int indexOperand, int[] modifiedOperands, String label) {
		return get(new ForAll(type, sourceOperand, indexOperand,
				modifiedOperands, label));
	}

	/**
	 * Construct a <code>newdict</code> bytecode which constructs a new
	 * dictionary and puts it on the stack.
	 * 
	 * @param type
	 * @return
	 */
	public static NewDict NewDict(Type.Dictionary type, int target,
			Collection<Integer> operands) {
		return get(new NewDict(type, target, toIntArray(operands)));
	}

	public static NewDict NewDict(Type.Dictionary type, int target,
			int[] operands) {
		return get(new NewDict(type, target, operands));
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
	
	public static NewRecord NewRecord(Type.Record type, int target,
			int[] operands) {
		return get(new NewRecord(type, target, operands));
	}

	/**
	 * Construct a return bytecode which does return a value and, hence, its
	 * type automatically defaults to void.
	 * 
	 * @return
	 */
	public static Return Return() {
		return get(new Return(Type.T_VOID,NULL_REG));
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
		return get(new Return(type,operand));
	}

	public static IfGoto IfGoto(Type type, int leftOperand, int rightOperand,
			COp cop, String label) {
		return get(new IfGoto(type, leftOperand, rightOperand, cop, label));
	}

	public static IfType IfType(Type type, int leftOperand, Type rightOperand,
			String label) {
		return get(new IfType(type, leftOperand, rightOperand, label));
	}

	public static IndirectSend IndirectSend(Type.Message msg, int target,
			int operand, Collection<Integer> operands, boolean synchronous) {
		return get(new IndirectSend(msg, synchronous, target, operand,
				toIntArray(operands)));
	}

	public static IndirectSend IndirectSend(Type.Message msg, int target,
			int operand, int[] operands, boolean synchronous) {
	return get(new IndirectSend(msg, synchronous, target, operand, operands));
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
		return get(new Invert(type,target,operand));
	}

	public static Label Label(String label) {
		return get(new Label(label));
	}

	public static final Nop Nop = new Nop();

	public static SetOp SetOp(Type.EffectiveSet type, int target,
			int leftOperand, int rightOperand, SetOperation operation) {
		return get(new SetOp(type, target, leftOperand, rightOperand, operation));
	}

	public static StringOp StringOp(int target, int leftOperand,
			int rightOperand, StringOperation operation) {
		return get(new StringOp(target, leftOperand, rightOperand, operation));
	}

	public static SubString SubString(int target, int sourceOperand,
			int leftOperand, int rightOperand) {
		int[] operands = new int[] {sourceOperand, leftOperand, rightOperand};
		return get(new SubString(target, operands));
	}

	private static SubString SubString(int target, int[] operands) {		
		return get(new SubString(target, operands));
	}
	
	/**
	 * Construct an <code>send</code> bytecode which sends a message to an
	 * actor. This may be either synchronous or asynchronous.
	 * 
	 * @param label
	 *            --- destination label.
	 * @return
	 */
	public static Send Send(Type.Message meth, int target,
			Collection<Integer> operands, NameID name, boolean synchronous) {
		return get(new Send(meth, target, toIntArray(operands), name,
				synchronous));
	}
	
	public static Send Send(Type.Message meth, int target, int[] operands,
			NameID name, boolean synchronous) {
		return get(new Send(meth, target, operands, name, synchronous));
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
			Collection<Pair<Value, String>> cases) {
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
		return get(new Throw(type,operand));
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

	/**
	 * Construct a <code>tupleload</code> bytecode which reads the value at a
	 * given index in a tuple
	 * 
	 * @param type
	 *            --- dictionary type.
	 * @return
	 */
	public static TupleLoad TupleLoad(Type.EffectiveTuple type, int target,
			int operand, int index) {
		return get(new TupleLoad(type, target, operand, index));
	}

	public static Negate Negate(Type type, int target, int operand) {
		return get(new Negate(type, target, operand));
	}

	public static New New(Type.Reference type, int target, int operand) {
		return get(new New(type, target, operand));
	}

	public static Dereference Dereference(Type.Reference type, int target,
			int operand) {
		return get(new Dereference(type, target, operand));
	}

	/**
	 * Construct a <code>update</code> bytecode which writes a value into a
	 * compound structure, as determined by a given access path.
	 * 
	 * @param afterType
	 *            --- record type.
	 * @param field
	 *            --- field to write.
	 * @return
	 */
	public static Update Update(Type beforeType, int target, int operand,
			Collection<Integer> operands, Type afterType,
			Collection<String> fields) {
		return get(new Update(beforeType, target, operand,
				toIntArray(operands), afterType, fields));
	}
	
	public static Update Update(Type beforeType, int target,
			int operand, int[] operands, Type afterType, Collection<String> fields) {
		return get(new Update(beforeType, target, operand, operands, afterType, 
				fields));
	}

	public static Void Void(Type type, int[] operands) {
		return get(new Void(type, operands));
	}

	// ===============================================================
	// Abstract Methods
	// ===============================================================

	// The following method adds any slots used by a given bytecode
	public void slots(Set<Integer> slots) {
		// default implementation does nothing
	}

	/**
	 * The remap method remaps all slots according to a given binding. Slots not
	 * mentioned in the binding retain their original value.
	 * 
	 * @param binding
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

	// ===============================================================
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
	public static abstract class AbstractUnaryAssignable<T> extends AbstractAssignable {
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
		public final void slots(Set<Integer> slots) {
			slots.add(target);
			slots.add(operand);
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
		
		protected abstract String codeString();
		
		public String toString() {
			if(operand != Code.NULL_REG) { 
				return "%" + target + " = " + codeString() + " %" + operand + " : " + type;
			} else {
				return "%" + target + " = " + codeString()  + " : " + type;
			}
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
		public final void slots(Set<Integer> slots) {			
			slots.add(operand);
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
		
		protected abstract String codeString();
		
		public String toString() {
			if(operand != Code.NULL_REG) {
				return codeString() + " %" + operand + " : " + type;
			} else {
				return codeString();
			}
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
	public static abstract class AbstractBinaryAssignable<T> extends AbstractAssignable {
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
		public final void slots(Set<Integer> slots) {
			slots.add(target);
			slots.add(leftOperand);
			slots.add(rightOperand);
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
		
		protected abstract String codeString();
		
		public String toString() {			
			return "%" + target + " = " + codeString() + " %" + leftOperand
					+ ", %" + rightOperand + " : " + type;			
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
	public static abstract class AbstractNaryAssignable<T> extends AbstractAssignable {
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
		public final void slots(Set<Integer> slots) {
			if (target >= 0) {
				slots.add(target);
			}
			for (int i = 0; i != operands.length; ++i) {
				slots.add(operands[i]);
			}
		}

		@Override
		public final Code remap(Map<Integer, Integer> binding) {
			Integer nTarget = binding.get(target);
			int[] nOperands = remap(binding, operands);
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
		
		protected abstract String codeString();
		
		public String toString() {
			String soperands = " ";
			for (int i = 0; i != operands.length; ++i) {
				if (i != 0) {
					soperands += ", ";
				}
				soperands += "%" + operands[i];
			}
			return "%" + target + " = " + codeString() + soperands + " : "
					+ type;
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
	public static abstract class AbstractSplitNaryAssignable<T> extends AbstractAssignable {
		public final T type;
		public final int operand;
		public final int[] operands;

		private AbstractSplitNaryAssignable(T type, int target, int operand, int[] operands) {
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
		public final void slots(Set<Integer> slots) {
			if (target >= 0) {
				slots.add(target);
			}
			slots.add(operand);
			for (int i = 0; i != operands.length; ++i) {
				slots.add(operands[i]);
			}
		}

		@Override
		public final Code remap(Map<Integer, Integer> binding) {
			Integer nTarget = binding.get(target);
			Integer nOperand = binding.get(target);
			int[] nOperands = remap(binding, operands);
			if (nTarget != null || nOperand != null || nOperands != operands) {
				nTarget = nTarget != null ? nTarget : target;
				nOperand = nOperand != null ? nOperand : operand;
				return clone(nTarget, nOperand, nOperands);
			}
			return this;
		}

		protected abstract Code clone(int nTarget, int nOperand, int[] nOperands);

		public int hashCode() {
			return type.hashCode() + target + operand + Arrays.hashCode(operands);
		}

		public boolean equals(Object o) {
			if (o instanceof AbstractSplitNaryAssignable) {
				AbstractSplitNaryAssignable bo = (AbstractSplitNaryAssignable) o;
				return target == bo.target
						&& operand == bo.operand
						&& Arrays.equals(operands, bo.operands)
						&& type.equals(bo.type);
			}
			return false;
		}
		
		protected abstract String codeString();
		
		public String toString() {
			String soperands = " ";
			for (int i = 0; i != operands.length; ++i) {
				if (i != 0) {
					soperands += ", ";
				}
				soperands += "%" + operands[i];
			}
			return "%" + target + " = " + codeString() + " %" + operand
					+ soperands + " : " + type;
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
		public final void slots(Set<Integer> slots) {
			slots.add(leftOperand);
			slots.add(rightOperand);
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
		
		protected abstract String codeString();
		
		public String toString() {
			return codeString() + " %" + leftOperand + ", %" + rightOperand
					+ " : " + type;
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
	public enum BOp {
		ADD {
			public String toString() {
				return "add";
			}
		},
		SUB {
			public String toString() {
				return "sub";
			}
		},
		MUL {
			public String toString() {
				return "mul";
			}
		},
		DIV {
			public String toString() {
				return "div";
			}
		},
		REM {
			public String toString() {
				return "rem";
			}
		},
		RANGE {
			public String toString() {
				return "range";
			}
		},
		BITWISEOR {
			public String toString() {
				return "or";
			}
		},
		BITWISEXOR {
			public String toString() {
				return "xor";
			}
		},
		BITWISEAND {
			public String toString() {
				return "and";
			}
		},
		LEFTSHIFT {
			public String toString() {
				return "shl";
			}
		},
		RIGHTSHIFT {
			public String toString() {
				return "shr";
			}
		},
	};

	/**
	 * <p>
	 * A binary operation takes reads values from two operand registers,
	 * performs an operation and writes the result to target register. The
	 * binary operators are:
	 * </p>
	 * <ul>
	 * <li><i>add, subtract, multiply, divide, remainder</i>. Both operands must
	 * be either integers or reals (but not one or the other). A value of the
	 * same type is produced.</li>
	 * <li><i>range</i></li>
	 * <li><i>bitwiseor, bitwisexor, bitwiseand</i></li>
	 * <li><i>leftshift,rightshift</i></li>
	 * </ul>
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class BinOp extends AbstractBinaryAssignable<Type> {
		public final BOp bop;

		private BinOp(Type type, int target, int lhs, int rhs, BOp bop) {
			super(type, target, lhs, rhs);
			if (bop == null) {
				throw new IllegalArgumentException(
						"BinOp bop argument cannot be null");
			}
			this.bop = bop;
		}

		@Override
		public Code clone(int nTarget, int nLeftOperand, int nRightOperand) {
			return Code.BinOp(type, nTarget, nLeftOperand, nRightOperand, bop);
		}

		public int hashCode() {
			return bop.hashCode() + super.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof BinOp) {
				BinOp bo = (BinOp) o;
				return bop.equals(bo.bop) && super.equals(bo);
			}
			return false;
		}

		public String codeString() {
			return bop.toString();			
		}
	}

	/**
	 * <p>
	 * Reads a value from the operand register, converts it to a given type and
	 * writes it the target register. This bytecode is the only way to change
	 * the type of a value. It's purpose is to simplify implementations which
	 * have different representations of data types.
	 * </p>
	 * 
	 * <p>
	 * In many cases, this bytecode may correspond to a nop on the hardware.
	 * Consider converting from <code>[any]</code> to <code>any</code>. On the
	 * JVM, <code>any</code> translates to <code>Object</code>, whilst
	 * <code>[any]</code> translates to <code>List</code> (which is an instance
	 * of <code>Object</code>). Thus, no conversion is necessary since
	 * <code>List</code> can safely flow into <code>Object</code>.
	 * </p>
	 * 
	 * <p>
	 * A convert bytecode must be inserted whenever the type of a register
	 * changes. This includes at control-flow meet points, when the value is
	 * passed as a parameter, assigned to a field, etc.
	 * </p>
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

		public String codeString() {
			return "convert " + result + "";
		}
	}

	/**
	 * Writes a constant value to a target register. This includes integer
	 * constants, rational constants, list constants, set constants, dictionary
	 * constants, function constants, etc.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Const extends AbstractAssignable {
		public final Value constant;		

		private Const(int target, Value constant) {
			super(target);
			this.constant = constant;			
		}

		@Override
		public void slots(Set<Integer> slots) {
			slots.add(target);
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
			return "%" + target + " = const " + constant;
		}
	}

	/**
	 * Copy the contents from a given operand register into a given target
	 * register.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Copy extends AbstractUnaryAssignable<Type> {

		private Copy(Type type, int target, int operand) {
			super(type, target, operand);
		}

		public Code clone(int nTarget, int nOperand) {
			return Code.Copy(type, nTarget, nOperand);
		}

		public boolean equals(Object o) {
			if (o instanceof Copy) {
				return super.equals(o);
			}
			return false;
		}

		public String codeString() {
			return "copy";
		}
	}

	/**
	 * Read a string from the operand and writes it to the debug console. This
	 * bytecode is not intended to form part of the programs operation. Rather,
	 * it is to facilitate debugging within functions (since they cannot have
	 * side-effects). Furthermore, if debugging is disabled, this bytecode is a
	 * nop.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Debug extends AbstractUnaryOp<Type.Strung> {

		private Debug(int operand) {
			super(Type.T_STRING, operand);
		}

		@Override
		public Code clone(int nOperand) {
			return Code.Debug(nOperand);
		}

		public boolean equals(Object o) {
			return o instanceof Debug && super.equals(o);
		}

		public String codeString() {
			return "debug";
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
				return End(nlabel);
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
	 * Reads values from two operand registers and compares them. An assertion
	 * failure with the given message is raised if comparison is false.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Assert extends AbstractBinaryOp<Type> {
		public final COp op;
		public final String msg;

		private Assert(Type type, int leftOperand, int rightOperand, COp cop,
				String msg) {
			super(type, leftOperand, rightOperand);
			if (cop == null) {
				throw new IllegalArgumentException(
						"Assert op argument cannot be null");
			}
			this.op = cop;
			this.msg = msg;
		}

		@Override
		public Code clone(int nLeftOperand, int nRightOperand) {
			return Code.Assert(type, nLeftOperand, nRightOperand, op, msg);
		}

		public int hashCode() {
			return op.hashCode() + msg.hashCode() + super.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof Assert) {
				Assert ig = (Assert) o;
				return op == ig.op && msg.equals(ig.msg) && super.equals(ig);
			}
			return false;
		}

		public String codeString() {
			return "assert " + op + " \"" + msg + "\"";
		}
	}

	/**
	 * Reads a record from an operand register, reads the value of given field
	 * and writes its target register.
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

		public String codeString() {
			return "fieldload " + field;
		}
	}

	/**
	 * <p>
	 * Branches unconditionally to the given label.
	 * </p>
	 * 
	 * <b>Note:</b> in WYIL bytecode, <i>such branches may only go forward</i>.
	 * Thus, a <code>goto</code> bytecode cannot be used to implement the
	 * back-edge of a loop. Rather, a loop block must be used for this purpose.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Goto extends Code {
		public final String target;

		private Goto(String target) {
			this.target = target;
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
	 * Branches conditionally to the given label by read values from two operand
	 * registers and comparing them. The possible comparators are:
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
	 * 
	 * <b>Note:</b> in WYIL bytecode, <i>such branches may only go forward</i>.
	 * Thus, an <code>ifgoto</code> bytecode cannot be used to implement the
	 * back-edge of a loop. Rather, a loop block must be used for this purpose.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class IfGoto extends AbstractBinaryOp<Type> {
		public final String target;
		public final COp op;

		private IfGoto(Type type, int leftOperand, int rightOperand, COp op,
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

		public IfGoto relabel(Map<String, String> labels) {
			String nlabel = labels.get(target);
			if (nlabel == null) {
				return this;
			} else {
				return IfGoto(type, leftOperand, rightOperand, op, nlabel);
			}
		}

		@Override
		public Code clone(int nLeftOperand, int nRightOperand) {
			return Code.IfGoto(type, nLeftOperand, nRightOperand, op, target);
		}

		public int hashCode() {
			return super.hashCode() + op.hashCode() + target.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof IfGoto) {
				IfGoto ig = (IfGoto) o;
				return op == ig.op && target.equals(ig.target)
						&& super.equals(ig);
			}
			return false;
		}

		public String codeString() {
			return "if " + op + " goto " + target;
		}
	}

	/**
	 * Represents a comparison operator (e.g. '==','!=',etc) that is provided to
	 * a <code>IfGoto</code> bytecode.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public enum COp {
		EQ() {
			public String toString() {
				return "eq";
			}
		},
		NEQ {
			public String toString() {
				return "ne";
			}
		},
		LT {
			public String toString() {
				return "lt";
			}
		},
		LTEQ {
			public String toString() {
				return "le";
			}
		},
		GT {
			public String toString() {
				return "gt";
			}
		},
		GTEQ {
			public String toString() {
				return "ge";
			}
		},
		ELEMOF {
			public String toString() {
				return "in";
			}
		},
		SUBSET {
			public String toString() {
				return "sb";
			}
		},
		SUBSETEQ {
			public String toString() {
				return "sbe";
			}
		}
	};

	/**
	 * Determine the inverse comparator, or null if no inverse exists.
	 * 
	 * @param cop
	 * @return
	 */
	public static Code.COp invert(Code.COp cop) {
		switch (cop) {
		case EQ:
			return Code.COp.NEQ;
		case NEQ:
			return Code.COp.EQ;
		case LT:
			return Code.COp.GTEQ;
		case LTEQ:
			return Code.COp.GT;
		case GT:
			return Code.COp.LTEQ;
		case GTEQ:
			return Code.COp.LT;
		}
		return null;
	}

	/**
	 * <p>
	 * Branches conditionally to the given label based on the result of a
	 * runtime type test against a value from the operand register. More
	 * specifically, it checks whether the value is a subtype of the type test.
	 * </p>
	 * <p>
	 * The operand register is automatically <i>retyped</i> as a result of the
	 * type test. On the true branch, its type is intersected with type test. On
	 * the false branch, its type is intersected with the <i>negation</i> of the
	 * type test.
	 * </p>
	 * <b>Note:</b> in WYIL bytecode, <i>such branches may only go forward</i>.
	 * Thus, an <code>iftype</code> bytecode cannot be used to implement the
	 * back-edge of a loop. Rather, a loop block must be used for this purpose.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class IfType extends Code {
		public final Type type;
		public final String target;
		public final int leftOperand;
		public final Type rightOperand;

		private IfType(Type type, int leftOperand, Type rightOperand,
				String target) {
			if (type == null) {
				throw new IllegalArgumentException(
						"IfGoto tpe argument cannot be null");
			}
			if (rightOperand == null) {
				throw new IllegalArgumentException(
						"IfGoto test argument cannot be null");
			}
			if (target == null) {
				throw new IllegalArgumentException(
						"IfGoto target argument cannot be null");
			}
			this.type = type;
			this.target = target;
			this.leftOperand = leftOperand;
			this.rightOperand = rightOperand;
		}

		public IfType relabel(Map<String, String> labels) {
			String nlabel = labels.get(target);
			if (nlabel == null) {
				return this;
			} else {
				return IfType(type, leftOperand, rightOperand, nlabel);
			}
		}

		@Override
		public void slots(Set<Integer> slots) {
			slots.add(leftOperand);
		}

		@Override
		public Code remap(Map<Integer, Integer> binding) {
			Integer nLeftOperand = binding.get(leftOperand);
			if (nLeftOperand != null) {
				return Code.IfType(type, nLeftOperand, rightOperand, target);
			}
			return this;
		}

		public int hashCode() {
			return type.hashCode() + rightOperand.hashCode()
					+ target.hashCode() + leftOperand + rightOperand.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof IfType) {
				IfType ig = (IfType) o;
				return leftOperand == ig.leftOperand
						&& rightOperand.equals(ig.rightOperand)
						&& target.equals(ig.target) && type.equals(ig.type);
			}
			return false;
		}

		public String codeString() {
			return "if " + leftOperand + " is " + rightOperand
					+ " goto " + target;
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
	public static final class IndirectInvoke extends AbstractSplitNaryAssignable<Type.FunctionOrMethod> {
		
		private IndirectInvoke(Type.FunctionOrMethod type, int target,
				int operand, int[] operands) {
			super(type,target,operand,operands);				
		}

		@Override
		public Code clone(int nTarget, int nOperand, int[] nOperands) {
			return IndirectInvoke(type, nTarget, nOperand, nOperands);			
		}

		public boolean equals(Object o) {
			return o instanceof IndirectInvoke && super.equals(o);						
		}

		public String codeString() {
			if (target != Code.NULL_REG) {
				return "indirectinvoke";
			} else {
				return "vindirectinvoke";
			}
		}
	}

	/**
	 * Represents an indirect message send (either synchronous or asynchronous).
	 * For example, consider the following:
	 * 
	 * <pre>
	 * int ::method(Rec::int(int) m, Rec r, int x):
	 *    return r.m(x)
	 * </pre>
	 * 
	 * Here, the message send <code>r.m(x)</code> is indirect as the message
	 * sent is determined by the variable <code>m</code>.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class IndirectSend extends AbstractSplitNaryAssignable<Type.Message> {
		public final boolean synchronous;
		
		private IndirectSend(Type.Message type, boolean synchronous,
				int target, int operand, int[] operands) {
			super(type,target,operand,operands);					
			this.synchronous = synchronous;
		}

		@Override
		public Code clone(int nTarget, int nOperand, int[] nOperands) {
			return IndirectSend(type, nTarget, nOperand, nOperands, synchronous);			
		}

		public boolean equals(Object o) {
			return o instanceof IndirectSend && super.equals(o);						
		}
		
		public String codeString() {
			if (synchronous) {
				if (target != Code.NULL_REG) {
					return "isend";
				} else {
					return "ivsend";
				}
			} else {
				return "iasend";
			}
		}
	}

	/**
	 * Represents the boolean not expression which reads a boolean operand,
	 * inverts its value and writes back to a target regiseter.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Not extends AbstractUnaryAssignable<Type.Bool> {

		private Not(int target, int operand) {
			super(Type.T_BOOL, target, operand);
		}

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

		public String codeString() {
			return "not";
		}
	}

	/**
	 * Corresponds to a direct function call whose parameters are found on the
	 * stack in the order corresponding to the function type. If a return value
	 * is required, this is written to a target register after the function
	 * call.
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

		public String codeString() {
			if (target != Code.NULL_REG) {
				return "invoke " + name;
			} else {
				return "vinvoke " + name;
			}
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

	public enum ListOperation {
		LEFT_APPEND {
			public String toString() {
				return "listappend_l";
			}
		},
		RIGHT_APPEND {
			public String toString() {
				return "listappend_r";
			}
		},
		APPEND {
			public String toString() {
				return "listappend";
			}
		}
	}

	
	/**
	 * Reads the (effective) list values from two operand registers, appends
	 * them and write the result back to a target register.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class ListOp extends
			AbstractBinaryAssignable<Type.EffectiveList> {
		public final ListOperation operation;

		private ListOp(Type.EffectiveList type, int target, int leftOperand,
				int rightOperand, ListOperation operation) {
			super(type, target, leftOperand, rightOperand);
			if (operation == null) {
				throw new IllegalArgumentException(
						"ListAppend direction cannot be null");
			}
			this.operation = operation;
		}

		@Override
		public Code clone(int nTarget, int nLeftOperand, int nRightOperand) {
			return Code.ListOp(type, nTarget, nLeftOperand, nRightOperand,
					operation);
		}

		public int hashCode() {
			return super.hashCode() + operation.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof ListOp) {
				ListOp setop = (ListOp) o;
				return super.equals(setop) && operation.equals(setop.operation);
			}
			return false;
		}

		public String codeString() {
			return operation.toString();
		}
	}

	/**
	 * Reads an (effective) collection from the operand register, and writes its
	 * length into the target register.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class LengthOf extends
			AbstractUnaryAssignable<Type.EffectiveCollection> {
		private LengthOf(Type.EffectiveCollection type, int target, int operand) {
			super(type, target, operand);
		}

		protected Code clone(int nTarget, int nOperand) {
			return Code.LengthOf(type, nTarget, nOperand);
		}

		public boolean equals(Object o) {
			if (o instanceof LengthOf) {
				return super.equals(o);
			}
			return false;
		}

		public String codeString() {
			return "lengthof";
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
	public static final class SubList extends AbstractNaryAssignable<Type.EffectiveList> {
		
		private SubList(Type.EffectiveList type, int target, int[] operands) {
			super(type,target,operands);					
		}

		@Override
		public final Code clone(int nTarget, int[] nOperands) {
			return Code.SubList(type, nTarget, nOperands);			
		}

		public boolean equals(Object o) {
			return o instanceof SubList && super.equals(o);
		}

		public String codeString() {
			return "sublist";
		}
	}

	/**
	 * Reads an effective map from the source (left) operand register, and a key
	 * value from the key (right) operand register and returns the value
	 * associated with that key in the map. If the key does not exist, then a
	 * fault is raised.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class IndexOf extends AbstractBinaryAssignable<Type.EffectiveMap> {
		private IndexOf(Type.EffectiveMap type, int target, int sourceOperand,
				int keyOperand) {
			super(type, target, sourceOperand, keyOperand);
		}

		protected Code clone(int nTarget, int nLeftOperand, int nRightOperand) {
			return Code.IndexOf(type, nTarget, nLeftOperand, nRightOperand);
		}

		public boolean equals(Object o) {
			if (o instanceof IndexOf) {
				return super.equals(o);
			}
			return false;
		}

		public String codeString() {
			return "indexof";
		}
	}

	/**
	 * Moves the contents of a given operand register into a given target
	 * register. This is similar to a <code>copy</code> bytecode, except that
	 * the register's contents are "voided" afterwards. This guarantees that the
	 * register is no longer live, which is useful for determining the live
	 * ranges of register in a function or method.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Move extends AbstractUnaryAssignable<Type> {

		private Move(Type type, int target, int operand) {
			super(type, target, operand);
		}

		protected Code clone(int nTarget, int nOperand) {
			return Code.Move(type, nTarget, nOperand);
		}

		public boolean equals(Object o) {
			if (o instanceof Move) {
				return super.equals(o);
			}
			return false;
		}

		public String codeString() {
			return "move";
		}
	}

	public static class Loop extends Code {
		public final String target;
		public final int[] modifiedOperands;

		private Loop(String target, int[] modifies) {
			this.target = target;
			this.modifiedOperands = modifies;
		}

		public Loop relabel(Map<String, String> labels) {
			String nlabel = labels.get(target);
			if (nlabel == null) {
				return this;
			} else {
				return Loop(nlabel, modifiedOperands);
			}
		}

		@Override
		public void slots(Set<Integer> slots) {
			for (int operand : modifiedOperands) {
				slots.add(operand);
			}
		}

		@Override
		public Code remap(Map<Integer, Integer> binding) {
			int[] nOperands = remap(binding, modifiedOperands);
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
			return "loop " + modifiedOperands;
		}
	}

	/**
	 * Pops a set, list or dictionary from the stack and iterates over every
	 * element it contains. An register is identified to hold the current value
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

		public ForAll relabel(Map<String, String> labels) {
			String nlabel = labels.get(target);
			if (nlabel == null) {
				return this;
			} else {
				return ForAll(type, sourceOperand, indexOperand, modifiedOperands, nlabel);
			}
		}

		@Override
		public void slots(Set<Integer> slots) {
			slots.add(indexOperand);
			slots.add(sourceOperand);
			super.slots(slots);
		}

		@Override
		public Code remap(Map<Integer, Integer> binding) {
			int[] nModifiedOperands = remap(binding, modifiedOperands);
			Integer nIndexOperand = binding.get(indexOperand);
			Integer nSourceOperand = binding.get(sourceOperand);
			if (nSourceOperand != null || nIndexOperand != null
					|| nModifiedOperands != modifiedOperands) {
				nSourceOperand = nSourceOperand != null ? nSourceOperand
						: sourceOperand;
				nIndexOperand = nIndexOperand != null ? nIndexOperand
						: indexOperand;
				return Code.ForAll(type, sourceOperand, nIndexOperand,
						nModifiedOperands, target);
			} else {
				return this;
			}
		}

		public int hashCode() {
			return super.hashCode() + indexOperand
					+ Arrays.hashCode(modifiedOperands);
		}

		public boolean equals(Object o) {
			if (o instanceof ForAll) {
				ForAll f = (ForAll) o;
				return target.equals(f.target) && type.equals(f.type)
						&& indexOperand == f.indexOperand
						&& Arrays.equals(modifiedOperands, f.modifiedOperands);
			}
			return false;
		}

		public String codeString() {
			return "forall ";
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
	public static abstract class LVal {
		protected Type type;

		public LVal(Type t) {
			this.type = t;
		}

		public Type rawType() {
			return type;
		}
	}

	/**
	 * An LVal with dictionary type.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class DictLVal extends LVal {
		public DictLVal(Type t) {
			super(t);
			if (!(t instanceof Type.EffectiveDictionary)) {
				throw new IllegalArgumentException("Invalid Dictionary Type");
			}
		}

		public Type.EffectiveDictionary type() {
			return (Type.EffectiveDictionary) type;
		}
	}

	/**
	 * An LVal with list type.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class ListLVal extends LVal {
		public ListLVal(Type t) {
			super(t);
			if (!(t instanceof Type.EffectiveList)) {
				throw new IllegalArgumentException("invalid List Type");
			}
		}

		public Type.EffectiveList type() {
			return (Type.EffectiveList) type;
		}
	}

	/**
	 * An LVal with list type.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class ReferenceLVal extends LVal {
		public ReferenceLVal(Type t) {
			super(t);
			if (Type.effectiveReference(t) == null) {
				throw new IllegalArgumentException("invalid reference type");
			}
		}

		public Type.Reference type() {
			return Type.effectiveReference(type);
		}
	}

	/**
	 * An LVal with string type.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class StringLVal extends LVal {
		public StringLVal() {
			super(Type.T_STRING);
		}
	}

	/**
	 * An LVal with record type.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class RecordLVal extends LVal {
		public final String field;

		public RecordLVal(Type t, String field) {
			super(t);
			this.field = field;
			if (!(t instanceof Type.EffectiveRecord)
					|| !((Type.EffectiveRecord) t).fields().containsKey(field)) {
				throw new IllegalArgumentException("Invalid Record Type");
			}
		}

		public Type.EffectiveRecord type() {
			return (Type.EffectiveRecord) type;
		}
	}

	private static final class UpdateIterator implements Iterator<LVal> {
		private final ArrayList<String> fields;
		private Type iter;
		private int fieldIndex;
		private int index;

		public UpdateIterator(Type type, int level, ArrayList<String> fields) {
			this.fields = fields;
			this.iter = type;
			this.index = level;
		}

		public LVal next() {
			Type raw = iter;
			index--;
			if (Type.isSubtype(Type.T_STRING, iter)) {
				iter = Type.T_CHAR;
				return new StringLVal();
			} else if (Type.isSubtype(Type.Reference(Type.T_ANY), iter)) {
				Type.Reference proc = Type.effectiveReference(iter);
				iter = proc.element();
				return new ReferenceLVal(raw);
			} else if (iter instanceof Type.EffectiveList) {
				Type.EffectiveList list = (Type.EffectiveList) iter;
				iter = list.element();
				return new ListLVal(raw);
			} else if (iter instanceof Type.EffectiveDictionary) {
				Type.EffectiveDictionary dict = (Type.EffectiveDictionary) iter;
				iter = dict.value();
				return new DictLVal(raw);
			} else if (iter instanceof Type.EffectiveRecord) {
				Type.EffectiveRecord rec = (Type.EffectiveRecord) iter;
				String field = fields.get(fieldIndex++);
				iter = rec.fields().get(field);
				return new RecordLVal(raw, field);
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
	public static final class Update extends AbstractSplitNaryAssignable<Type> implements Iterable<LVal> {		
		public final Type afterType;		
		public final ArrayList<String> fields;

		private Update(Type beforeType, int target, int operand,
				int[] operands, Type afterType, Collection<String> fields) {
			super(beforeType,target,operand,operands);
			if (fields == null) {
				throw new IllegalArgumentException(
						"FieldStore fields argument cannot be null");
			}			
			this.afterType = afterType;			
			this.fields = new ArrayList<String>(fields);
		}

		public int level() {
			int base = 0;
			if(type instanceof Type.Reference) {
				base++;
			}
			return base + fields.size() + operands.length;
		}
		
		public Iterator<LVal> iterator() {
			return new UpdateIterator(afterType, level(), fields);
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
				} else if (iter instanceof Type.EffectiveDictionary) {
					Type.EffectiveDictionary dict = (Type.EffectiveDictionary) iter;
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
			return Code.Update(type,nTarget, nOperand,nOperands,afterType,fields);			
		}

		public boolean equals(Object o) {
			if (o instanceof Update) {
				Update i = (Update) o;
				return super.equals(o)
						&& afterType.equals(i.afterType)
						&& fields.equals(i.fields);
			}
			return false;
		}

		public String codeString() {
			String fs = fields.isEmpty() ? "" : " ";
			boolean firstTime = true;
			for (String f : fields) {
				if (!firstTime) {
					fs += ".";
				}
				firstTime = false;
				fs += f;
			}
			return "update " + fs + " => " + afterType;
		}
	}

	/**
	 * Constructs a new dictionary value from zero or more key-value pairs on
	 * the stack. For each pair, the key must occur directly before the value on
	 * the stack. For example:
	 * 
	 * <pre>
	 *   const 1 : int                           
	 *   const "Hello" : string                  
	 *   const 2 : int                           
	 *   const "World" : string                  
	 *   newdict #2 : {int->string}
	 * </pre>
	 * 
	 * Pushes the dictionary value <code>{1->"Hello",2->"World"}</code> onto the
	 * stack.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class NewDict extends AbstractNaryAssignable<Type.Dictionary> {

		private NewDict(Type.Dictionary type, int target, int[] operands) {
			super(type, target, operands);
		}

		protected Code clone(int nTarget, int[] nOperands) {
			return Code.NewDict(type, nTarget, nOperands);
		}

		public boolean equals(Object o) {
			if (o instanceof NewDict) {
				return super.equals(o);
			}
			return false;
		}

		public String codeString() {
			return "map";
		}
	}

	/**
	 * Constructs a new record value from zero or more values on the stack. Each
	 * value is associated with a field name, and will be popped from the stack
	 * in the reverse order. For example:
	 * 
	 * <pre>
	 *   const 1 : int                           
	 *   const 2 : int                           
	 *   newrec : {int x,int y}
	 * </pre>
	 * 
	 * Pushes the record value <code>{x:1,y:2}</code> onto the stack.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class NewRecord extends AbstractNaryAssignable<Type.Record> {
		private NewRecord(Type.Record type, int target, int[] operands) {
			super(type,target, operands);
		}

		protected Code clone(int nTarget, int[] nOperands) {
			return Code.NewRecord(type, nTarget, nOperands);
		}

		public boolean equals(Object o) {
			if (o instanceof NewRecord) {
				return super.equals(o);
			}
			return false;
		}

		public String codeString() {
			return "record";
		}
	}

	/**
	 * Constructs a new tuple value from two or more values on the stack. Values
	 * are popped from the stack in the reverse order they occur in the tuple.
	 * For example:
	 * 
	 * <pre>
	 *   const 1 : int                           
	 *   const 2 : int                           
	 *   newtuple #2 : (int,int)
	 * </pre>
	 * 
	 * Pushes the tuple value <code>(1,2)</code> onto the stack.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class NewTuple extends AbstractNaryAssignable<Type.Tuple> {

		private NewTuple(Type.Tuple type, int target, int[] operands) {
			super(type,target,operands);
		}
		
		protected Code clone(int nTarget, int[] nOperands) {
			return Code.NewTuple(type, nTarget, nOperands);
		}

		public boolean equals(Object o) {
			if (o instanceof NewTuple) {
				return super.equals(o);
			}
			return false;
		}

		public String codeString() {
			return "tuple";
		}
	}

	/**
	 * Constructs a new set value from zero or more register operands. The new
	 * set is written to the target register. For example:
	 * 
	 * <pre>
	 *     int %1 = 1                            
	 *     int %2 = 2                           
	 *     int %3 = 3                           
	 *     {int} %4 = newset %1,%2,%3
	 * </pre>
	 * 
	 * Writes the set value <code>{1,2,3}</code> into register %4.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class NewSet extends AbstractNaryAssignable<Type.Set> {

		private NewSet(Type.Set type, int target, int[] operands) {
			super(type,target,operands);
		}

		protected Code clone(int nTarget, int[] nOperands) {
			return Code.NewSet(type, nTarget, nOperands);
		}
		
		public boolean equals(Object o) {
			if (o instanceof NewSet) {
				return super.equals(o);
			}
			return false;
		}

		public String codeString() {
			return "set";
		}
	}

	/**
	 * Constructs a new list value from zero or more values on the stack. The
	 * values are popped from the stack in the reverse order they will occur in
	 * the new list. The new list is load onto the stack. For example:
	 * 
	 * <pre>
	 *     const 1 : int                           
	 *     const 2 : int                           
	 *     const 3 : int                           
	 *     newlist #3 : [int]
	 * </pre>
	 * 
	 * Pushes the list value <code>[1,2,3]</code> onto the stack.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class NewList extends AbstractNaryAssignable<Type.List> {

		private NewList(Type.List type, int target, int[] operands) {
			super(type,target,operands);
		}
		
		protected Code clone(int nTarget, int[] nOperands) {
			return Code.NewList(type, nTarget, nOperands);
		}		

		public boolean equals(Object o) {
			if (o instanceof NewList) {
				return super.equals(operands);
			}
			return false;
		}

		public String codeString() {
			return "list";
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

		public String toString() {
			return "nop";
		}
	}

	public static final class Return extends AbstractUnaryOp<Type> {
		
		private Return(Type type, int operand) {
			super(type,operand);
			if (type == Type.T_VOID && operand != NULL_REG) {
				throw new IllegalArgumentException(
						"Return with void type cannot have target register.");
			} else if (type != Type.T_VOID && operand == NULL_REG) {
				throw new IllegalArgumentException(
						"Return with non-void type must have target register.");
			}			
		}
		
		public Code clone(int nOperand) {
			return new Return(type,nOperand);			
		}
		
		public boolean equals(Object o) {
			if (o instanceof Return) {
				return super.equals(o);
			}
			return false;
		}

		public String codeString() {
			return "return";
		}
	}

	public enum SetOperation {
		LEFT_UNION {
			public String toString() {
				return "union_l";
			}
		},
		RIGHT_UNION {
			public String toString() {
				return "union_r";
			}
		},
		UNION {
			public String toString() {
				return "union";
			}
		},
		LEFT_INTERSECTION {
			public String toString() {
				return "intersect_l";
			}
		},
		RIGHT_INTERSECTION {
			public String toString() {
				return "intersect_r";
			}
		},
		INTERSECTION {
			public String toString() {
				return "intersect";
			}
		},
		LEFT_DIFFERENCE {
			public String toString() {
				return "difference_l";
			}
		},
		DIFFERENCE {
			public String toString() {
				return "difference";
			}
		}
	}

	public static final class SetOp extends AbstractBinaryAssignable<Type.EffectiveSet> {
		public final SetOperation operation;

		private SetOp(Type.EffectiveSet type, int target, int leftOperand,
				int rightOperand, SetOperation operation) {
			super(type, target, leftOperand, rightOperand);
			if (operation == null) {
				throw new IllegalArgumentException(
						"SetOp operation cannot be null");
			}
			this.operation = operation;
		}

		protected Code clone(int nTarget, int nLeftOperand, int nRightOperand) {
			return Code.SetOp(type, nTarget, nLeftOperand, nRightOperand, operation);
		}

		public int hashCode() {
			return operation.hashCode() + super.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof SetOp) {
				SetOp setop = (SetOp) o;
				return operation.equals(setop.operation) && super.equals(o);
			}
			return false;
		}

		public String codeString() {
			return operation.toString();
		}
	}

	public enum StringOperation {
		LEFT_APPEND {
			public String toString() {
				return "strappend_l";
			}
		},
		RIGHT_APPEND {
			public String toString() {
				return "strappend_r";
			}
		},
		APPEND {
			public String toString() {
				return "strappend";
			}
		}
	}
	
	public static final class StringOp extends AbstractBinaryAssignable<Type.Strung> {
		public final StringOperation operation;

		private StringOp(int target, int leftOperand, int rightOperand, StringOperation operation) {
			super(Type.T_STRING,target,leftOperand,rightOperand);
			if (operation == null) {
				throw new IllegalArgumentException(
						"StringBinOp operation cannot be null");
			}
			this.operation = operation;
		}

		protected Code clone(int nTarget, int nLeftOperand, int nRightOperand) {
			return Code.StringOp(nTarget,nLeftOperand,nRightOperand,operation);
		}
		
		public boolean equals(Object o) {
			if (o instanceof StringOp) {
				StringOp setop = (StringOp) o;
				return operation.equals(setop.operation) && super.equals(o);
			}
			return false;
		}

		public String codeString() {
			return operation.toString();
		}
	}
	
	/**
	 * Reads the string value from a source operand register, and the
	 * integer values from two index operand registers, computes the substring and
	 * writes the result back to a target register.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class SubString extends AbstractNaryAssignable {		
		
		private SubString(int target, int[] operands) {
			super(Type.T_STRING,target,operands);			
		}

		@Override
		public final Code clone(int nTarget, int[] nOperands) {
			return Code.SubString(nTarget, nOperands);			
		}

		public boolean equals(Object o) {
			return o instanceof SubString && super.equals(o);				
		}

		public String codeString() {
			return "substr";
		}
	}

	public static final class Switch extends Code {
		public final Type type;
		public final int operand;
		public final ArrayList<Pair<Value, String>> branches;
		public final String defaultTarget;

		Switch(Type type, int operand, String defaultTarget,
				Collection<Pair<Value, String>> branches) {
			this.type = type;
			this.operand = operand;
			this.branches = new ArrayList<Pair<Value, String>>(branches);
			this.defaultTarget = defaultTarget;
		}

		public Switch relabel(Map<String, String> labels) {
			ArrayList<Pair<Value, String>> nbranches = new ArrayList();
			for (Pair<Value, String> p : branches) {
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
						&& branches.equals(ig.branches)
						&& type.equals(ig.type);
			}
			return false;
		}

		public String toString() {
			String table = "";
			boolean firstTime = true;
			for (Pair<Value, String> p : branches) {
				if (!firstTime) {
					table += ", ";
				}
				firstTime = false;
				table += p.first() + "->" + p.second();
			}
			table += ", *->" + defaultTarget;
			return "switch " + table;
		}
		
		@Override
		public void slots(Set<Integer> slots) {
			slots.add(operand);
		}
		
		@Override
		public Code remap(Map<Integer,Integer> binding) {
			Integer nOperand = binding.get(operand);
			if(nOperand != null) {
				return new Return(type,nOperand);
			}
			return this;
		}
		
	}

	public static final class Send extends AbstractNaryAssignable<Type.Message> {
		public final boolean synchronous;
		public final NameID name;
		
		private Send(Type.Message type, int target, 
				int[] operands, NameID name, boolean synchronous) {
			super(type,target,operands);
			this.name = name;
			this.synchronous = synchronous;
		}
		
		@Override
		public Code clone(int nTarget, int[] nOperands) {
			return Send(type, nTarget, nOperands, name, synchronous);			
		}

		public boolean equals(Object o) {
			if (o instanceof Send) {
				Send i = (Send) o;
				return synchronous == i.synchronous
						&& type.equals(i.type) && name.equals(i.name)
						&& super.equals(o);
			}
			return false;
		}

		public String codeString() {
			if (synchronous) {
				if (target != Code.NULL_REG) {
					return "send " + name;
				} else {
					return "vsend " + name;
				}
			} else {
				return "asend " + name;
			}
		}
	}

	public static final class Throw extends AbstractUnaryOp<Type> {		
		private Throw(Type type, int operand) {
			super(type,operand);
		}
		
		@Override
		public Code clone(int nOperand) {
			return Code.Throw(type,nOperand);			
		}
		
		public boolean equals(Object o) {
			if (o instanceof Throw) {				
				return super.equals(o);
			}
			return false;
		}

		public String codeString() {
			return "throw";
		}
	}
	
	public static final class TryCatch extends Code {
		public final int operand;
		public final String label;
		public final ArrayList<Pair<Type, String>> catches;

		TryCatch(int operand, String label, Collection<Pair<Type, String>> catches) {
			this.operand = operand;
			this.catches = new ArrayList<Pair<Type, String>>(catches);
			this.label = label;
		}

		@Override
		public void slots(Set<Integer> slots) {
			slots.add(operand);
		}

		@Override
		public Code remap(Map<Integer,Integer> binding) {
			Integer nOperand = binding.get(operand);
			if(nOperand != null) {
				return Code.TryCatch(nOperand,label,catches);
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

			String ntarget = labels.get(label);
			if (ntarget != null) {
				return TryCatch(operand, ntarget, nbranches);
			} else {
				return TryCatch(operand, label, nbranches);
			}
		}

		public int hashCode() {
			return operand + label.hashCode() + catches.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof TryCatch) {
				TryCatch ig = (TryCatch) o;
				return operand == ig.operand && label.equals(ig.label)
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
	 * Pops a number (int or real) from the stack, negates it and pushes the
	 * result back on.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Negate extends AbstractUnaryAssignable<Type> {

		private Negate(Type type, int target, int operand) {
			super(type,target,operand);
		}

		protected Code clone(int nTarget, int nOperand) {
			return Code.Negate(type, nTarget, nOperand);
		}
		
		public boolean equals(Object o) {
			if (o instanceof Negate) {
				return super.equals(o);
			}
			return false;
		}

		public String codeString() {
			return "neg";
		}
	}

	/**
	 * Corresponds to a bitwise inversion operation, which pops a byte off the
	 * stack and pushes the result back on. For example:
	 * 
	 * <pre>
	 * byte f(byte x):
	 *    return ~x
	 * </pre>
	 * 
	 * Here, the expression <code>~x</code> generates an inverstion bytecode.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Invert extends AbstractUnaryAssignable<Type> {
		
		private Invert(Type type, int target, int operand) {
			super(type,target,operand);
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

		public String codeString() {
			return "invert";
		}
	}
	
	/**
	 * Instantiate a new object from the value in a given operand register, and
	 * write the result to a given target register.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class New extends AbstractUnaryAssignable<Type.Reference> {
		
		private New(Type.Reference type, int target, int operand) {
			super(type,target,operand);
		}

		protected Code clone(int nTarget, int nOperand) {
			return Code.New(type, nTarget, nOperand);
		}
		
		public boolean equals(Object o) {
			if (o instanceof New) {
				return super.equals(o);
			}
			return false;
		}

		public String codeString() {
			return "new";
		}
	}
	
	public static final class TupleLoad extends
			AbstractUnaryAssignable<Type.EffectiveTuple> {
		public final int index;

		private TupleLoad(Type.EffectiveTuple type, int target, int operand,
				int index) {
			super(type, target, operand);
			this.index = index;
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

		public String codeString() {
			return "tupleload " + index;
		}
	}

	/**
	 * Dereference the reference value from the operand register and write the
	 * result to the target register.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Dereference extends AbstractUnaryAssignable<Type.Reference> {

		private Dereference(Type.Reference type, int target, int operand) {
			super(type, target, operand);
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

		public String codeString() {
			return "deref";
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

		protected Code clone(int nTarget, int[] nOperands) {
			return Code.Void(type, nOperands);
		}

		public boolean equals(Object o) {
			if (o instanceof Void) {
				return super.equals(o);
			}
			return false;
		}

		public String codeString() {
			return "void";
		}
	}

	private static int[] toIntArray(Collection<Integer> operands) {
		int[] ops = new int[operands.size()];
		int i = 0;
		for (Integer o : operands) {
			ops[i++] = o;
		}
		return ops;
	}

	private static int[] remap(Map<Integer, Integer> binding, int[] operands) {
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
}
