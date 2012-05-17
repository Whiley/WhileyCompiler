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
	public final static int THIS_SLOT = 0;
	
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
	public static Assert Assert(Type type, COp cop, String message) {
		return get(new Assert(type,cop,message));
	}
	
	public static BinOp BinOp(Type type, BOp op) {
		return get(new BinOp(type,op));
	}
	
	/**
	 * Construct a <code>const</code> bytecode which loads a given constant
	 * onto the stack.
	 * 
	 * @param afterType
	 *            --- record type.
	 * @param field
	 *            --- field to write.
	 * @return
	 */
	public static Const Const(Value constant) {
		return get(new Const(constant));
	}
	
	public static Convert Convert(Type from, Type to) {
		return get(new Convert(from,to));
	}
	
	public static final Debug debug = new Debug();

	public static Destructure Destructure(Type from) {
		return get(new Destructure(from));
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
	public static FieldLoad FieldLoad(Type.EffectiveRecord type, String field) {
		return get(new FieldLoad(type,field));
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

	/**
	 * Construct an <code>invoke</code> bytecode which invokes a method.
	 * 
	 * @param label
	 *            --- destination label.
	 * @return
	 */
	public static Invoke Invoke(Type.FunctionOrMethod fun, NameID name, boolean retval) {
		return get(new Invoke(fun,name,retval));
	}

	public static Not Not() {
		return get(new Not());
	}
	
	/**
	 * Construct a <code>load</code> bytecode which pushes a given register onto
	 * the stack.
	 * 
	 * @param type
	 *            --- record type.
	 * @param reg
	 *            --- reg to load.
	 * @return
	 */
	public static Load Load(Type type, int reg) {
		return get(new Load(type,reg));
	}
	
	public static LengthOf LengthOf(Type.EffectiveCollection type) {
		return get(new LengthOf(type));
	}
	
	/**
	 * Construct a <code>move</code> bytecode which moves a given register onto
	 * the stack. The register contents of the register are voided after this
	 * operation.
	 * 
	 * @param type
	 *            --- record type.
	 * @param reg
	 *            --- reg to load.
	 * @return
	 */
	public static Move Move(Type type, int reg) {
		return get(new Move(type,reg));
	}
	
	public static SubList SubList(Type.EffectiveList type) {
		return get(new SubList(type));
	}
	
	public static ListAppend ListAppend(Type.EffectiveList type, OpDir dir) {
		return get(new ListAppend(type,dir));
	}
	
	/**
	 * Construct a <code>listload</code> bytecode which reads a value from a
	 * given index in a given list.
	 * 
	 * @param type
	 *            --- list type.
	 * @return
	 */
	public static IndexOf IndexOf(Type.EffectiveMap type) {
		return get(new IndexOf(type));
	}

	/**
	 * Construct a <code>loop</code> bytecode which iterates the sequence of
	 * bytecodes upto the exit label.
	 * 
	 * @param label
	 *            --- exit label.
	 * @return
	 */
	public static Loop Loop(String label, Collection<Integer> modifies) {
		return get(new Loop(label,modifies));
	}

	/**
	 * Construct a <code>forall</code> bytecode which iterates over a given
	 * source collection stored on top of the stack. The supplied variable
	 * <code>var</code> is used as the iterator. The exit label denotes the end
	 * of the loop block.
	 * 
	 * 
	 * @param label
	 *            --- exit label.
	 * @return
	 */
	public static ForAll ForAll(Type.EffectiveCollection type, int var,
			String label, Collection<Integer> modifies) {
		return get(new ForAll(type, var, label, modifies));
	}				
	
	/**
	 * Construct a <code>newdict</code> bytecode which constructs a new dictionary
	 * and puts it on the stack.
	 * 
	 * @param type
	 * @return
	 */
	public static NewDict NewDict(Type.Dictionary type, int nargs) {
		return get(new NewDict(type,nargs));
	}
	
	/**
	 * Construct a <code>newset</code> bytecode which constructs a new set
	 * and puts it on the stack.
	 * 
	 * @param type
	 * @return
	 */
	public static NewSet NewSet(Type.Set type, int nargs) {
		return get(new NewSet(type,nargs));
	}
	
	/**
	 * Construct a <code>newlist</code> bytecode which constructs a new list
	 * and puts it on the stack.
	 * 
	 * @param type
	 * @return
	 */
	public static NewList NewList(Type.List type, int nargs) {
		return get(new NewList(type,nargs));
	}
	
	/**
	 * Construct a <code>newtuple</code> bytecode which constructs a new tuple
	 * and puts it on the stack.
	 * 
	 * @param type
	 * @return
	 */
	public static NewTuple NewTuple(Type.Tuple type, int nargs) {
		return get(new NewTuple(type, nargs));
	}
	
	/**
	 * Construct a <code>newrecord</code> bytecode which constructs a new record
	 * and puts it on the stack.
	 * 
	 * @param type
	 * @return
	 */
	public static NewRecord NewRecord(Type.Record type) {
		return get(new NewRecord(type));
	}
	
	public static Return Return(Type t) {
		return get(new Return(t));
	}
	
	public static IfGoto IfGoto(Type type, COp cop, String label) {
		return get(new IfGoto(type,cop,label));
	}

	public static IfType IfType(Type type, int slot, Type test, String label) {
		return get(new IfType(type,slot,test,label));
	}
	
	/**
	 * Construct an <code>indirectsend</code> bytecode which sends an indirect
	 * message to an actor. This may be either synchronous or asynchronous.
	 * 
	 * @param label
	 *            --- destination label.
	 * @return
	 */
	public static IndirectSend IndirectSend(Type.Message msg, boolean synchronous, boolean retval) {
		return get(new IndirectSend(msg,synchronous,retval));
	}
	
	/**
	 * Construct an <code>indirectinvoke</code> bytecode which sends an indirect
	 * message to an actor. This may be either synchronous or asynchronous.
	 * 
	 * @param label
	 *            --- destination label.
	 * @return
	 */
	public static IndirectInvoke IndirectInvoke(Type.FunctionOrMethod fun, boolean retval) {
		return get(new IndirectInvoke(fun,retval));
	}
	
	public static Invert Invert(Type type) {
		return get(new Invert(type));
	}	
	
	public static Label Label(String label) {
		return get(new Label(label));
	}
	
	public static final Skip Skip = new Skip();
	
	public static SetUnion SetUnion(Type.EffectiveSet type, OpDir dir) {
		return get(new SetUnion(type,dir));
	}
	
	public static SetIntersect SetIntersect(Type.EffectiveSet type, OpDir dir) {
		return get(new SetIntersect(type,dir));
	}
	
	public static SetDifference SetDifference(Type.EffectiveSet type, OpDir dir) {
		return get(new SetDifference(type,dir));
	}
	
	public static StringAppend StringAppend(OpDir dir) {
		return get(new StringAppend(dir));
	}
	
	public static SubString SubString() {
		return get(new SubString());
	}
	
	/**
	 * Construct an <code>send</code> bytecode which sends a message to an
	 * actor. This may be either synchronous or asynchronous.
	 * 
	 * @param label
	 *            --- destination label.
	 * @return
	 */
	public static Send Send(Type.Message meth, NameID name, boolean synchronous, boolean retval) {
		return get(new Send(meth,name,synchronous,retval));
	}	
	
	/**
	 * Construct a <code>store</code> bytecode which writes a given register.
	 * 
	 * @param type
	 *            --- record type.
	 * @param reg
	 *            --- reg to load.
	 * @return
	 */
	public static Store Store(Type type, int reg) {
		return get(new Store(type,reg));
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
	public static Switch Switch(Type type, String defaultLabel,
			Collection<Pair<Value, String>> cases) {
		return get(new Switch(type,defaultLabel,cases));
	}

	/**
	 * Construct a <code>throw</code> bytecode which pops a value off the
	 * stack and throws it.
	 * 
	 * @param afterType
	 *            --- value type to throw 
	 * @return
	 */
	public static Throw Throw(Type t) {
		return get(new Throw(t));
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
	public static TryCatch TryCatch(String target,
			Collection<Pair<Type, String>> catches) {
		return get(new TryCatch(target, catches));
	}
	
	public static TryEnd TryEnd(String label) {
		return get(new TryEnd(label));
	}
	
	/**
	 * Construct a <code>tupleload</code> bytecode which reads the value
	 * at a given index in a tuple
	 * 
	 * @param type
	 *            --- dictionary type.
	 * @return
	 */
	public static TupleLoad TupleLoad(Type.EffectiveTuple type, int index) {
		return get(new TupleLoad(type,index));
	}
	
	public static Negate Negate(Type type) {
		return get(new Negate(type));
	}		
	
	public static New New(Type.Reference type) {
		return get(new New(type));
	}
	
	public static Dereference Dereference(Type.Reference type) {
		return get(new Dereference(type));
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
	public static Update Update(Type beforeType, Type afterType, int slot,
			int level, Collection<String> fields) {
		return get(new Update(beforeType, afterType, slot, level, fields));
	}

	public static Void Void(Type type, int slot) {
		return get(new Void(type,slot));
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
	public Code remap(Map<Integer,Integer> binding) {
		return this;
	}

	/**
	 * Relabel all labels according to the given map.
	 * 
	 * @param labels
	 * @return
	 */
	public Code relabel(Map<String,String> labels) {
		return this;
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
		ADD{
			public String toString() { return "add"; }
		},
		SUB{
			public String toString() { return "sub"; }
		},
		MUL{
			public String toString() { return "mul"; }
		},
		DIV{
			public String toString() { return "div"; }
		},		
		REM{
			public String toString() { return "rem"; }
		},
		RANGE{
			public String toString() { return "range"; }
		},
		BITWISEOR{
			public String toString() { return "or"; }
		},
		BITWISEXOR{
			public String toString() { return "xor"; }
		},
		BITWISEAND{
			public String toString() { return "and"; }
		},
		LEFTSHIFT{
			public String toString() { return "shl"; }
		},
		RIGHTSHIFT{
			public String toString() { return "shr"; }
		},
	};

	/**
	 * <p>
	 * A binary operation takes two items off the stack and pushes a single
	 * result. The binary operators are:
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
	public static final class BinOp extends Code {		
		public final BOp bop;
		public final Type type;
		
		private BinOp(Type type, BOp bop) {
			if(bop == null) {
				throw new IllegalArgumentException("BinOp bop argument cannot be null");
			}
			this.bop = bop;
			this.type = type;
		}
		
		public int hashCode() {
			if(type == null) {
				return bop.hashCode();
			} else {
				return type.hashCode() + bop.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if(o instanceof BinOp) {
				BinOp bo = (BinOp) o;
				return (type == bo.type || (type != null && type
						.equals(bo.type))) && bop.equals(bo.bop); 
			}
			return false;
		}
				
		public String toString() {
			return toString(bop.toString(),type);
		}
	}

	/**
	 * <p>
	 * Pops a value from the stack, converts it to a given type and pushes it
	 * back on. This bytecode is the only way to change the type of a value.
	 * It's purpose is to simplify implementations which have different
	 * representations of data types.
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
	public static final class Convert extends Code {
		public final Type from;
		public final Type to;
		
		private Convert(Type from, Type to) {
			if(to == null) {
				throw new IllegalArgumentException("Convert to argument cannot be null");
			}
			this.from = from;
			this.to = to;
		}
		
		public int hashCode() {
			if(from == null) {
				return to.hashCode();
			} else {
				return from.hashCode() + to.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if(o instanceof Convert) {
				Convert c = (Convert) o;
				return (from == c.from || (from != null && from.equals(c.from)))
						&& to.equals(c.to);  
			}
			return false;
		}
				
		public String toString() {
			return "convert " + from + " to " + to;
		}
	}

	/**
	 * Pushes a constant value onto the stack. This includes integer constants,
	 * rational constants, list constants, set constants, dictionary constants,
	 * function constants, etc.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Const extends Code {		
		public final Value constant;
		
		private Const(Value constant) {
			this.constant = constant;
		}
		
		public int hashCode() {
			return constant.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof Const) {
				Const c = (Const) o;
				return constant.equals(c.constant);  
			}
			return false;
		}
		
		public String toString() {
			return toString("const " + constant,constant.type());
		}
	}

	/**
	 * Pops a string from the stack and writes it to the debug console. This
	 * bytecode is not intended to form part of the programs operation. Rather,
	 * it is to facilitate debugging within functions (since they cannot have
	 * side-effects). Furthermore, if debugging is disabled, this bytecode is a
	 * nop.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Debug extends Code {
		Debug() {}
		public int hashCode() {
			return 101;
		}
		public boolean equals(Object o) {
			return o instanceof Debug;
		}
		public String toString() {
			return "debug";
		}
	}

	/**
	 * Pops a compound value from the stack "destructures" it into multiple
	 * values which are pushed back on the stack. For example, a rational can be
	 * destructured into two integers (the <i>numerator</i> and
	 * <i>denominator</i>). Or, an n-tuple can be destructured into n values.
	 * 
	 * Probably should be deprecated in favour of tupeload bytecode.
	 */
	public static final class Destructure extends Code {
		public final Type type;
		
		private Destructure(Type from) {			
			this.type = from;			
		}
		
		public int hashCode() {
			if(type == null) {
				return 12345;
			} else {
				return type.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if(o instanceof Destructure) {
				Destructure c = (Destructure) o;
				return (type == c.type || (type != null && type.equals(c.type)));  
			}
			return false;
		}
				
		public String toString() {
			return "destructure " + type;
		}
	}
	
	/**
	 * Marks the end of a loop block.
	 * @author David J. Pearce
	 *
	 */
	public static final class LoopEnd extends Label {
		LoopEnd(String label) {
			super(label);
		}
		
		public LoopEnd relabel(Map<String,String> labels) {
			String nlabel = labels.get(label);
			if(nlabel == null) {
				return this;
			} else {
				return End(nlabel);
			}
		}
		
		public int hashCode() {
			return label.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof LoopEnd) {
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
	 * Raises an assertion failure if the given condition is false with the
	 * given message.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Assert extends Code {
		public final Type type;
		public final COp op;
		public final String msg;
		
		private Assert(Type type, COp cop, String msg) {
			if(cop == null) {
				throw new IllegalArgumentException("Assert op argument cannot be null");
			}			
			this.type = type;
			this.op = cop;
			this.msg = msg;
		}
		
		public int hashCode() {
			if(type == null) {
				return op.hashCode() + msg.hashCode();
			} else {
				return type.hashCode() + op.hashCode() + msg.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if (o instanceof Assert) {
				Assert ig = (Assert) o;
				return op == ig.op
						&& msg.equals(ig.msg)
						&& (type == ig.type || (type != null && type
								.equals(ig.type)));
			}
			return false;
		}
	
		public String toString() {
			return toString("assert " + op + " \"" + msg + "\"",type);
		}			
	}

	/**
	 * Pops a record from the stack and pushes the value from the given
	 * field back on.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class FieldLoad extends Code {
		public final Type.EffectiveRecord type;		
		public final String field;
				
		private FieldLoad(Type.EffectiveRecord type, String field) {
			if (field == null) {
				throw new IllegalArgumentException(
						"FieldLoad field argument cannot be null");
			}
			this.type = type;
			this.field = field;
		}
		
		public int hashCode() {
			if(type != null) {
				return type.hashCode() + field.hashCode();
			} else {
				return field.hashCode();
			}
		}
		
		public Type fieldType() {
			return type.fields().get(field);
		}
		
		public boolean equals(Object o) {
			if(o instanceof FieldLoad) {
				FieldLoad i = (FieldLoad) o;
				return (i.type == type || (type != null && type.equals(i.type)))
						&& field.equals(i.field);
			}
			return false;
		}
	
		public String toString() {
			return toString("fieldload " + field,(Type) type);			
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
		
		private  Goto(String target) {
			this.target = target;
		}
		
		public Goto relabel(Map<String,String> labels) {
			String nlabel = labels.get(target);
			if(nlabel == null) {
				return this;
			} else {
				return Goto(nlabel);
			}
		}
		
		public int hashCode() {
			return target.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof Goto) {
				return target.equals(((Goto)o).target);
			}
			return false;
		}
		
		public String toString() {
			return "goto " + target;		
		}		
	}

	/**
	 * <p>
	 * Branches conditionally to the given label by popping two operands from
	 * the stack and comparing them. The possible comparators are:
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
	public static final class IfGoto extends Code {
		public final Type type;
		public final COp op;
		public final String target;

		private  IfGoto(Type type, COp op, String target) {
			if(op == null) {
				throw new IllegalArgumentException("IfGoto op argument cannot be null");
			}
			if(target == null) {
				throw new IllegalArgumentException("IfGoto target argument cannot be null");
			}
			this.type = type;
			this.op = op;						
			this.target = target;
		}
		
		public IfGoto relabel(Map<String,String> labels) {
			String nlabel = labels.get(target);
			if(nlabel == null) {
				return this;
			} else {
				return IfGoto(type,op,nlabel);
			}
		}
		
		public int hashCode() {
			if(type == null) {
				return op.hashCode() + target.hashCode();
			} else {
				return type.hashCode() + op.hashCode() + target.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if(o instanceof IfGoto) {
				IfGoto ig = (IfGoto) o;
				return op == ig.op
					&& target.equals(ig.target)
						&& (type == ig.type || (type != null && type
								.equals(ig.type)));
			}
			return false;
		}
	
		public String toString() {
			return toString("if" + op + " goto " + target,type);
		}
	}
	
	/**
	 * Represents a comparison operator (e.g. '==','!=',etc) that is provided to a
	 * <code>IfGoto</code> bytecode.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public enum COp { 
		EQ() {
			public String toString() { return "eq"; }
		},
		NEQ{
			public String toString() { return "ne"; }
		},
		LT{
			public String toString() { return "lt"; }
		},
		LTEQ{
			public String toString() { return "le"; }
		},
		GT{
			public String toString() { return "gt"; }
		},
		GTEQ{
			public String toString() { return "ge"; }
		},
		ELEMOF{
			public String toString() { return "in"; }
		},
		SUBSET{
			public String toString() { return "sb"; }
		},
		SUBSETEQ{
			public String toString() { return "sbe"; }
		}		
	};

	/**
	 * Determine the inverse comparator, or null if no inverse exists.
	 * 
	 * @param cop
	 * @return
	 */
	public static Code.COp invert(Code.COp cop) {
		switch(cop) {
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
	 * runtime type test against a given value. More specifically, it checks
	 * whether the value is a subtype of the type test. The value in question is
	 * either loaded directly from a register, or popped off the stack.
	 * </p>
	 * <p>
	 * In the case that the value is obtained from a register, then that
	 * variable is automatically <i>retyped</i> as a result of the type test. On
	 * the true branch, its type is intersected with type test. On the false
	 * branch, its type is intersected with the <i>negation</i> of the type
	 * test.
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
		public final int slot;
		public final Type test;		
		public final String target;

		private  IfType(Type type, int slot, Type test, String target) {
			if(test == null) {
				throw new IllegalArgumentException("IfGoto op argument cannot be null");
			}
			if(target == null) {
				throw new IllegalArgumentException("IfGoto target argument cannot be null");
			}
			this.type = type;
			this.slot = slot;
			this.test = test;						
			this.target = target;
		}
		
		public IfType relabel(Map<String,String> labels) {
			String nlabel = labels.get(target);
			if(nlabel == null) {
				return this;
			} else {
				return IfType(type,slot,test,nlabel);
			}
		}
		
		public void slots(Set<Integer> slots) {
			if(slot >= 0) {
				slots.add(slot);
			}
		}
		
		public Code remap(Map<Integer, Integer> binding) {
			if (slot >= 0) {
				Integer nslot = binding.get(slot);
				if (nslot != null) {
					return Code.IfType(type, nslot, test, target);
				}
			}
			return this;
		}
		
		public int hashCode() {
			if(type == null) {
				return test.hashCode() + target.hashCode();
			} else {
				return type.hashCode() + test.hashCode() + target.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if(o instanceof IfType) {
				IfType ig = (IfType) o;
				return test.equals(ig.test)
					&& slot == ig.slot
					&& target.equals(ig.target)
						&& (type == ig.type || (type != null && type
								.equals(ig.type)));
			}
			return false;
		}
	
		public String toString() {
			if(slot >= 0) {
				return toString("if " + slot + " is " + test + " goto " + target,type);
			} else {				
				return toString("if " + test + " goto " + target,type);
			}
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
	public static final class IndirectInvoke extends Code {		
		public final Type.FunctionOrMethod type;
		public final boolean retval;
		
		private IndirectInvoke(Type.FunctionOrMethod type, boolean retval) {
			this.type = type;
			this.retval = retval;
		}
		
		public int hashCode() {
			if(type != null) {
				return type.hashCode();
			} else {
				return 123;
			}
		}
		
		public boolean equals(Object o) {
			if(o instanceof IndirectInvoke) {
				IndirectInvoke i = (IndirectInvoke) o;				
				return retval == i.retval
						&& (type == i.type || (type != null && type
								.equals(i.type)));
			}
			return false;
		}
	
		public String toString() {
			if(retval) {
				return toString("indirectinvoke",type);
			} else {
				return toString("vindirectinvoke",type);
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
	public static final class IndirectSend extends Code {
		 public final boolean synchronous;
		 public final boolean retval;
		 public final Type.Message type;
			
		 private IndirectSend(Type.Message type, boolean synchronous, boolean retval) {
			 this.type = type;
			 this.synchronous = synchronous;
			 this.retval = retval;
		 }

		 public int hashCode() {
			 if(type != null) {
					return type.hashCode();
				} else {
					return 123;
				}
		 }

		 public boolean equals(Object o) {
			 if(o instanceof IndirectSend) {
				 IndirectSend i = (IndirectSend) o;
				 return type == i.type || (type != null && type.equals(i.type));
			 }
			 return false;
		 }

		 public String toString() {			 
			 if(synchronous) {		
				 if(retval) {
					 return toString("isend",type);
				 } else {
					 return toString("ivsend",type);
				 }
			 } else {				 
				 return toString("iasend",type);				 
			 }
		 }		
	}
	
	public static final class Not extends Code {		
		private Not() {						
		}
		
		public int hashCode() {			
			return 12875;			
		}
		
		public boolean equals(Object o) {
			return o instanceof Not;
		}
				
		public String toString() {
			return toString("not",Type.T_BYTE);
		}
	}

	/**
	 * Corresponds to a direct function call whose parameters are found on the
	 * stack in the order corresponding to the function type. If a return value
	 * is required, this is pushed onto the stack after the function call.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Invoke extends Code {		
		public final Type.FunctionOrMethod type;
		public final NameID name;
		public final boolean retval;
				
		private Invoke(Type.FunctionOrMethod type, NameID name, boolean retval) {
			this.type = type;
			this.name = name;
			this.retval = retval;
		}
		
		public int hashCode() {
			if(type == null) {
				return name.hashCode();
			} else {
				return type.hashCode() + name.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if (o instanceof Invoke) {
				Invoke i = (Invoke) o;
				return name.equals(i.name)
						&& retval == i.retval
						&& (type == i.type || (type != null && type
								.equals(i.type)));
			}
			return false;
		}
	
		public String toString() {
			if(retval) {
				return toString("invoke " + name,type);
			} else {
				return toString("vinvoke " + name,type);
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
		
		public Label relabel(Map<String,String> labels) {
			String nlabel = labels.get(label);
			if(nlabel == null) {
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
	 * Pops two lists from the stack, appends them together and pushes the
	 * result back onto the stack.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class ListAppend extends Code {				
		public final OpDir dir;
		public final Type.EffectiveList type;
		
		private ListAppend(Type.EffectiveList type, OpDir dir) {			
			if(dir == null) {
				throw new IllegalArgumentException("ListAppend direction cannot be null");
			}			
			this.type = type;
			this.dir = dir;
		}
		
		public int hashCode() {
			if(type == null) {
				return dir.hashCode(); 
			} else {
				return type.hashCode() + dir.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if (o instanceof ListAppend) {
				ListAppend setop = (ListAppend) o;
				return (type == setop.type || (type != null && type
						.equals(setop.type)))						
						&& dir.equals(setop.dir);
			}
			return false;
		}
				
		public String toString() {
			return toString("listappend" + dir.toString(), (Type) type);
		}
	}

	/**
	 * Pops a list from the stack and pushes its length back on.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class LengthOf extends Code {						
		public final Type.EffectiveCollection type;
		
		private LengthOf(Type.EffectiveCollection type) {									
			this.type = type;			
		}
		
		public int hashCode() {
			if(type == null) {
				return 124987; 
			} else {
				return type.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if (o instanceof LengthOf) {
				LengthOf setop = (LengthOf) o;
				return (type == setop.type || (type != null && type
						.equals(setop.type)));
			}
			return false;
		}
				
		public String toString() {
			return toString("length", (Type) type);
		}
	}
	
	public static final class SubList extends Code {						
		public final Type.EffectiveList type;
		
		private SubList(Type.EffectiveList type) {									
			this.type = type;			
		}
		
		public int hashCode() {
			if(type == null) {
				return 124987; 
			} else {
				return type.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if (o instanceof SubList) {
				SubList setop = (SubList) o;
				return (type == setop.type || (type != null && type
						.equals(setop.type)));
			}
			return false;
		}
				
		public String toString() {
			return toString("sublist", (Type) type);
		}
	}

	/**
	 * Pops am integer index from the stack, followed by a list and pushes the
	 * element at the given index back on.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class IndexOf extends Code {
		public final Type.EffectiveMap type;				
		
		private IndexOf(Type.EffectiveMap type) {
			this.type = type;
		}
		
		public int hashCode() {
			if(type == null) {
				return 235;
			} else {
				return type.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if(o instanceof IndexOf) {
				IndexOf i = (IndexOf) o;
				return type == i.type || (type != null && type.equals(i.type));
			}
			return false;
		}
	
		public String toString() {
			return toString("indexof", (Type) type);
		}	
	}

	/**
	 * Loads the contents of the given register onto the stack.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Load extends Code {		
		public final Type type;
		public final int slot;		
		
		private Load(Type type, int slot) {
			this.type = type;
			this.slot = slot;
		}
		
		public void slots(Set<Integer> slots) {
			slots.add(slot);
		}
		
		public Code remap(Map<Integer,Integer> binding) {
			Integer nslot = binding.get(slot);
			if(nslot != null) {
				return Code.Load(type, nslot);
			} else {
				return this;
			}
		}
		
		public int hashCode() {
			if(type == null) {
				return slot; 
			} else { 
				return type.hashCode() + slot;
			}
		}
		
		public boolean equals(Object o) {
			if(o instanceof Load) {
				Load i = (Load) o;
				return slot == i.slot
						&& (type == i.type || (type != null && type
								.equals(i.type)));
			}
			return false;
		}
	
		public String toString() {
			return toString("load " + slot,type);
		}	
	}

	/**
	 * Moves the contents of the given register onto the stack. This is similar
	 * to a <code>load</code> bytecode, except that the register's contents are
	 * "voided" afterwards. This guarantees that the register is no longer live,
	 * which is useful for determining the live ranges of register in a
	 * function or method.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Move extends Code {		
		public final Type type;
		public final int slot;		
		
		private Move(Type type, int slot) {
			this.type = type;
			this.slot = slot;
		}
		
		public void slots(Set<Integer> slots) {
			slots.add(slot);
		}
		
		public Code remap(Map<Integer,Integer> binding) {
			Integer nslot = binding.get(slot);
			if(nslot != null) {
				return Code.Move(type, nslot);
			} else {
				return this;
			}
		}
		
		public int hashCode() {
			if(type == null) {
				return slot; 
			} else { 
				return type.hashCode() + slot;
			}
		}
		
		public boolean equals(Object o) {
			if(o instanceof Move) {
				Move i = (Move) o;
				return slot == i.slot
						&& (type == i.type || (type != null && type
								.equals(i.type)));
			}
			return false;
		}
	
		public String toString() {
			return toString("move " + slot,type);
		}	
	}		
	
	public static class Loop extends Code {
		public final String target;
		public final HashSet<Integer> modifies;
		
		private Loop(String target, Collection<Integer> modifies) {
			this.target = target;
			this.modifies = new HashSet<Integer>(modifies);
		}
		
		public Loop relabel(Map<String,String> labels) {
			String nlabel = labels.get(target);
			if(nlabel == null) {
				return this;
			} else {
				return Loop(nlabel,modifies);
			}
		}
		
		public int hashCode() {
			return target.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof Loop) {
				Loop f = (Loop) o;
				return target.equals(f.target)
						&& modifies.equals(f.modifies);
			}
			return false;
		}
		
		public String toString() {
			return "loop " + modifies;
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
		public final int slot;
		public final Type.EffectiveCollection type;
				
		private ForAll(Type.EffectiveCollection type, int slot, String target, Collection<Integer> modifies) {
			super(target,modifies);
			this.type = type;
			this.slot = slot;			
		}
		
		public ForAll relabel(Map<String,String> labels) {
			String nlabel = labels.get(target);
			if(nlabel == null) {
				return this;
			} else {
				return ForAll(type,slot,nlabel,modifies);
			}
		}
		
		public void slots(Set<Integer> slots) {
			slots.add(slot);
		}
		
		public Code remap(Map<Integer,Integer> binding) {
			Integer nslot = binding.get(slot);
			if(nslot != null) {
				return Code.ForAll(type, nslot, target, modifies);
			} else {
				return this;
			}
		}
		
		public int hashCode() {
			return super.hashCode() + slot;
		}
		
		public boolean equals(Object o) {
			if (o instanceof ForAll) {
				ForAll f = (ForAll) o;
				return target.equals(f.target)
						&& (type == f.type || (type != null && type
								.equals(f.type))) && slot == f.slot
						&& modifies.equals(f.modifies);
			}
			return false;
		}
		
		public String toString() {			
			return toString("forall " + slot + " " + modifies,(Type) type);
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
	 * @author David J. Pearce
	 *
	 */
	public static final class DictLVal extends LVal {
		public DictLVal(Type t) {
			super(t);			
			if(!(t instanceof Type.EffectiveDictionary)) {
				throw new IllegalArgumentException("Invalid Dictionary Type");
			}
		}
		
		public Type.EffectiveDictionary type() {			
			return (Type.EffectiveDictionary) type;
		}
	}
	
	/**
	 * An LVal with list type.
	 * @author David J. Pearce
	 *
	 */
	public static final class ListLVal extends LVal {
		public ListLVal(Type t) {
			super(t);
			if(!(t instanceof Type.EffectiveList)) {
				throw new IllegalArgumentException("invalid List Type");
			}
		}
		
		public Type.EffectiveList type() {
			return (Type.EffectiveList) type;
		}
	}
	
	/**
	 * An LVal with list type.
	 * @author David J. Pearce
	 *
	 */
	public static final class ReferenceLVal extends LVal {
		public ReferenceLVal(Type t) {
			super(t);
			if(Type.effectiveReference(t) == null) {
				throw new IllegalArgumentException("invalid reference type");
			}
		}
		
		public Type.Reference type() {
			return Type.effectiveReference(type);
		}
	}
	
	/**
	 * An LVal with string type.
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
			if(Type.isSubtype(Type.T_STRING,iter)) {
				iter = Type.T_CHAR;
				return new StringLVal();
			} else if(Type.isSubtype(Type.Reference(Type.T_ANY),iter)) {			
				Type.Reference proc = Type.effectiveReference(iter);											
				iter = proc.element();
				return new ReferenceLVal(raw);
			} else if(iter instanceof Type.EffectiveList) {			
				Type.EffectiveList list = (Type.EffectiveList) iter;											
				iter = list.element();
				return new ListLVal(raw);
			} else if(iter instanceof Type.EffectiveDictionary) {					
				Type.EffectiveDictionary dict = (Type.EffectiveDictionary) iter; 											
				iter = dict.value();	
				return new DictLVal(raw);
			} else  if(iter instanceof Type.EffectiveRecord) {
				Type.EffectiveRecord rec = (Type.EffectiveRecord) iter;				
				String field = fields.get(fieldIndex++);
				iter = rec.fields().get(field);
				return new RecordLVal(raw,field);
			} else {
				throw new IllegalArgumentException("Invalid type for Code.Update");
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
	public static final class Update extends Code implements Iterable<LVal> {
		public final Type beforeType;
		public final Type afterType;
		public final int level;
		public final int slot;
		public final ArrayList<String> fields;

		private Update(Type beforeType, Type afterType, int slot, int level, Collection<String> fields) {
			if (fields == null) {
				throw new IllegalArgumentException(
						"FieldStore fields argument cannot be null");
			}
			this.beforeType = beforeType;
			this.afterType = afterType;
			this.slot = slot;
			this.level = level;
			this.fields = new ArrayList<String>(fields);
		}

		public void slots(Set<Integer> slots) {
			slots.add(slot);
		}
		
		public Iterator<LVal> iterator() {			
			return new UpdateIterator(afterType,level,fields);
		}
				
		/**
		 * Extract the type for the right-hand side of this assignment.
		 * @return
		 */
		public Type rhs() {
			Type iter = afterType;
			
			int fieldIndex = 0;
			for (int i = 0; i != level; ++i) {
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
		
		public Code remap(Map<Integer,Integer> binding) {
			Integer nslot = binding.get(slot);
			if(nslot != null) {
				return Code.Update(beforeType, afterType, nslot, level, fields);
			} else {
				return this;
			}
		}
				
		public int hashCode() {
			if(afterType == null) {
				return level + fields.hashCode();
			} else {
				return afterType.hashCode() + slot + level + fields.hashCode();
			}
		}

		public boolean equals(Object o) {
			if (o instanceof Update) {
				Update i = (Update) o;
				return (i.beforeType == beforeType || (beforeType != null && beforeType
						.equals(i.beforeType)))
						&& (i.afterType == afterType || (afterType != null && afterType
								.equals(i.afterType)))
						&& level == i.level
						&& slot == i.slot && fields.equals(i.fields);
			}
			return false;
		}

		public String toString() {
			String fs = fields.isEmpty() ? "" : " ";
			boolean firstTime=true;
			for(String f : fields) {
				if(!firstTime) {
					fs += ".";
				}
				firstTime=false;
				fs += f;
			}
			return toString("update " + slot + " #" + level + fs,beforeType,afterType);
		}
	}

	/**
	 * Constructs a new dictionary value from zero or more key-value pairs on
	 * the stack. For each pair, the key must occur directly before the value on
	 * the stack.  For example:
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
	public static final class NewDict extends Code {
		public final Type.Dictionary type;
		public final int nargs;
		
		private NewDict(Type.Dictionary type, int nargs) {
			this.type = type;
			this.nargs = nargs;
		}
		
		public int hashCode() {
			if(type == null) {
				return nargs;
			} else {
				return type.hashCode() + nargs;
			}
		}
		
		public boolean equals(Object o) {
			if(o instanceof NewDict) {
				NewDict i = (NewDict) o;
				return (type == i.type || (type != null && type.equals(i.type))) && nargs == i.nargs;
			}
			return false;
		}
	
		public String toString() {
			return toString("newdict #" + nargs,type);
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
	public static final class NewRecord extends Code {
		public final Type.Record type;
		
		private NewRecord(Type.Record type) {
			this.type = type;
		}
		
		public int hashCode() {
			if(type == null) {
				return 952;
			} else {
				return type.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if(o instanceof NewRecord) {
				NewRecord i = (NewRecord) o;
				return type == i.type || (type != null && type.equals(i.type));
			}
			return false;
		}
	
		public String toString() {
			return toString("newrec",type);
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
	public static final class NewTuple extends Code {
		public final Type.Tuple type;
		public final int nargs;
		
		private NewTuple(Type.Tuple type, int nargs) {
			this.type = type;
			this.nargs = nargs;
		}
		
		public int hashCode() {
			if(type == null) {
				return nargs;
			} else {
				return type.hashCode() + nargs;
			}
		}
		
		public boolean equals(Object o) {
			if(o instanceof NewTuple) {
				NewTuple i = (NewTuple) o;
				return nargs == i.nargs && (type == i.type || (type != null && type.equals(i.type)));
			}
			return false;
		}
	
		public String toString() {
			return toString("newtuple #" + nargs,type);
		}	
	}

	/**
	 * Constructs a new set value from zero or more values on the stack. The new
	 * set is load onto the stack. For example:
	 * 
	 * <pre>
	 *     const 1 : int                           
	 *     const 2 : int                           
	 *     const 3 : int                           
	 *     newset #3 : {int}
	 * </pre>
	 * 
	 * Pushes the set value <code>{1,2,3}</code> onto the stack.
	 * 
	 * @author David J. Pearce
	 * 
	 */		
	public static final class NewSet extends Code {
		public final Type.Set type;
		public final int nargs;
		
		private NewSet(Type.Set type, int nargs) {
			this.type = type;
			this.nargs = nargs;
		}
		
		public int hashCode() {
			if(type == null) {
				return nargs;
			} else {
				return type.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if(o instanceof NewSet) {
				NewSet i = (NewSet) o;
				return type == i.type || (type != null && type.equals(i.type))
						&& nargs == i.nargs;
			}
			return false;
		}
	
		public String toString() {
			return toString("newset #" + nargs,type);
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
	public static final class NewList extends Code {
		public final Type.List type;
		public final int nargs;
		
		private NewList(Type.List type, int nargs) {
			this.type = type;
			this.nargs = nargs;
		}
		
		public int hashCode() {
			if(type == null) {
				return nargs;
			} else {
				return type.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if(o instanceof NewList) {
				NewList i = (NewList) o;
				return type == i.type || (type != null && type.equals(i.type))
						&& nargs == i.nargs;
			}
			return false;
		}
	
		public String toString() {
			return toString("newlist #" + nargs,type);
		}	
	}
	
	public static final class Nop extends Code {
		private Nop() {}
		public String toString() { return "nop"; }
	}	
		
	public static final class Return extends Code {
		public final Type type;
		
		private Return(Type type) {
			this.type = type;
		}
		
		public int hashCode() {
			if(type == null) {
				return 996;
			} else {
				return type.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if(o instanceof Return) {
				Return i = (Return) o;
				return type == i.type || (type != null && type.equals(i.type));
			}
			return false;
		}
	
		public String toString() {
			return toString("return",type);
		}
	}
	
	public enum OpDir {
		UNIFORM {
			public String toString() { return ""; }
		},
		LEFT {
			public String toString() { return "_l"; }
		},
		RIGHT {
			public String toString() { return "_r"; }
		}
	}
	
	
	public static final class SetUnion extends Code {		
		public final OpDir dir;
		public final Type.EffectiveSet type;
		
		private SetUnion(Type.EffectiveSet type, OpDir dir) {
			if(dir == null) {
				throw new IllegalArgumentException("SetAppend direction cannot be null");
			}			
			this.type = type;
			this.dir = dir;
		}
		
		public int hashCode() {
			if(type == null) {
				return dir.hashCode(); 
			} else {
				return type.hashCode() + dir.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if (o instanceof SetUnion) {
				SetUnion setop = (SetUnion) o;
				return (type == setop.type || (type != null && type
						.equals(setop.type)))						
						&& dir.equals(setop.dir);
			}
			return false;
		}
				
		public String toString() {
			return toString("union" + dir.toString(), (Type) type);
		}
	}
	
	public static final class SetIntersect extends Code {		
		public final OpDir dir;
		public final Type.EffectiveSet type;
		
		private SetIntersect(Type.EffectiveSet type, OpDir dir) {
			if(dir == null) {
				throw new IllegalArgumentException("SetAppend direction cannot be null");
			}			
			this.type = type;
			this.dir = dir;
		}
		
		public int hashCode() {
			if(type == null) {
				return dir.hashCode(); 
			} else {
				return type.hashCode() + dir.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if (o instanceof SetIntersect) {
				SetIntersect setop = (SetIntersect) o;
				return (type == setop.type || (type != null && type
						.equals(setop.type)))						
						&& dir.equals(setop.dir);
			}
			return false;
		}
				
		public String toString() {
			return toString("intersect" + dir.toString(), (Type) type);
		}
	}
	
	public static final class SetDifference extends Code {		
		public final OpDir dir;
		public final Type.EffectiveSet type;
		
		private SetDifference(Type.EffectiveSet type, OpDir dir) {
			if(dir == null) {
				throw new IllegalArgumentException("SetAppend direction cannot be null");
			}			
			this.type = type;
			this.dir = dir;
		}
		
		public int hashCode() {
			if(type == null) {
				return dir.hashCode(); 
			} else {
				return type.hashCode() + dir.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if (o instanceof SetDifference) {
				SetDifference setop = (SetDifference) o;
				return (type == setop.type || (type != null && type
						.equals(setop.type)))						
						&& dir.equals(setop.dir);
			}
			return false;
		}
				
		public String toString() {
			return toString("difference" + dir.toString(), (Type) type);
		}
	}
		
	public static final class StringAppend extends Code {			
		public final OpDir dir;		
		
		private StringAppend(OpDir dir) {			
			if(dir == null) {
				throw new IllegalArgumentException("StringAppend direction cannot be null");
			}			
			this.dir = dir;
		}
		
		public int hashCode() {			
			return dir.hashCode();			
		}
		
		public boolean equals(Object o) {
			if (o instanceof StringAppend) {
				StringAppend setop = (StringAppend) o;
				return dir.equals(setop.dir);
			}
			return false;
		}
				
		public String toString() {
			return toString("stringappend" + dir.toString(),Type.T_STRING);
		}
	}
	
	public static final class SubString extends Code {		
		private SubString() {
		}
		
		public int hashCode() {			
			return 983745;			
		}
		
		public boolean equals(Object o) {
			return o instanceof SubString;
		}
				
		public String toString() {
			return toString("substring",Type.T_STRING);
		}
	}
		
	public static final class Skip extends Code {
		Skip() {}
		public int hashCode() {
			return 101;
		}
		public boolean equals(Object o) {
			return o instanceof Skip;
		}
		public String toString() {
			return "skip";
		}
	}

	public static final class Store extends Code {		
		public final Type type;
		public final int slot;		
		
		private Store(Type type, int slot) {
			this.type = type;
			this.slot = slot;
		}
		
		public void slots(Set<Integer> slots) {
			slots.add(slot);
		}
		
		public Code remap(Map<Integer,Integer> binding) {
			Integer nslot = binding.get(slot);
			if(nslot != null) {
				return Code.Store(type, nslot);	
			} else {
				return this;
			}
		}
		
		public int hashCode() {
			if(type == null) {
				return slot;
			} else {
				return type.hashCode() + slot;
			}
		}
		
		public boolean equals(Object o) {
			if(o instanceof Store) {
				Store i = (Store) o;
				return (type == i.type || (type != null && type.equals(i.type)))
						&& slot == i.slot;
			}
			return false;
		}
	
		public String toString() {
			return toString("store " + slot,type);
		}	
	}	

	public static final class Switch extends Code {
		public final Type type;
		public final ArrayList<Pair<Value,String>> branches;
		public final String defaultTarget;

		Switch(Type type, String defaultTarget, Collection<Pair<Value,String>> branches) {			
			this.type = type;
			this.branches = new ArrayList<Pair<Value,String>>(branches);
			this.defaultTarget = defaultTarget;
		}
	
		public Switch relabel(Map<String,String> labels) {
			ArrayList<Pair<Value,String>> nbranches = new ArrayList();
			for(Pair<Value,String> p : branches) {
				String nlabel = labels.get(p.second());
				if(nlabel == null) {
					nbranches.add(p);
				} else {
					nbranches.add(new Pair(p.first(),nlabel));
				}
			}
			
			String nlabel = labels.get(defaultTarget);
			if(nlabel == null) {
				return Switch(type,defaultTarget,nbranches);
			} else {
				return Switch(type,nlabel,nbranches);
			}
		}
		
		public int hashCode() {
			if(type == null) {
				return defaultTarget.hashCode() + branches.hashCode();
			} else {
				return type.hashCode() + defaultTarget.hashCode() + branches.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if (o instanceof Switch) {
				Switch ig = (Switch) o;
				return defaultTarget.equals(ig.defaultTarget)
						&& branches.equals(ig.branches)
						&& (type == ig.type || (type != null && type
								.equals(ig.type)));						
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
	}

	public static final class Send extends Code {		 
		 public final boolean synchronous;
		 public final boolean retval;
		 public final NameID name;
		 public final Type.Message type;
			
		 private Send(Type.Message type, NameID name, boolean synchronous, boolean retval) {
			 this.type = type;
			 this.name = name;
			 this.synchronous = synchronous;
			 this.retval = retval;
		 }

		 public int hashCode() {
			 return type.hashCode() + name.hashCode();
		 }

		 public boolean equals(Object o) {
			 if(o instanceof Send) {
				 Send i = (Send) o;
				return retval == i.retval && synchronous == i.synchronous
						&& (type.equals(i.type) && name.equals(i.name));
			 }
			 return false;
		 }

		 public String toString() {
			 if(synchronous) {
				 if(retval) {
					 return toString("send " + name,type);
				 } else {
					 return toString("vsend " + name,type);
				 }
			 } else {
				 return toString("asend " + name,type);
			 }
		 }	
	}

	public static final class Throw extends Code {
		public final Type type;

		private Throw(Type type) {
			this.type = type;
		}
		
		public int hashCode() {
			if(type == null) {
				return 98923;
			} else {
				return type.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if(o instanceof Throw) {
				Throw i = (Throw) o;
				return type == i.type || (type != null && type.equals(i.type));
			}
			return false;
		}
	
		public String toString() {
			return toString("throw",type);
		}
	}
	
	public static final class TryCatch extends Code {		
		public final String target;
		public final ArrayList<Pair<Type,String>> catches;

		TryCatch(String target, Collection<Pair<Type,String>> catches) {						
			this.catches = new ArrayList<Pair<Type,String>>(catches);
			this.target = target;
		}
	
		public TryCatch relabel(Map<String,String> labels) {
			ArrayList<Pair<Type,String>> nbranches = new ArrayList();
			for(Pair<Type,String> p : catches) {
				String nlabel = labels.get(p.second());
				if(nlabel == null) {
					nbranches.add(p);
				} else {
					nbranches.add(new Pair(p.first(),nlabel));
				}
			}
			
			String ntarget = labels.get(target);
			if(ntarget != null) {
				return TryCatch(ntarget,nbranches);
			} else {
				return TryCatch(target,nbranches);
			}
		}
		
		public int hashCode() {
			return target.hashCode() + catches.hashCode();			
		}
		
		public boolean equals(Object o) {
			if (o instanceof TryCatch) {
				TryCatch ig = (TryCatch) o;
				return target.equals(ig.target)
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
	 * @author David J. Pearce
	 *
	 */
	public static final class TryEnd extends Label {
		TryEnd(String label) {
			super(label);
		}
		
		public TryEnd relabel(Map<String,String> labels) {
			String nlabel = labels.get(label);
			if(nlabel == null) {
				return this;
			} else {
				return TryEnd(nlabel);
			}
		}
		
		public int hashCode() {
			return label.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof TryEnd) {
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
	public static final class Negate extends Code {
		public final Type type;		
		
		private Negate(Type type) {			
			this.type = type;
		}
		
		public int hashCode() {
			if(type == null) {
				return 239487;
			} else {
				return type.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if(o instanceof Negate) {
				Negate bo = (Negate) o;
				return (type == bo.type || (type != null && type
						.equals(bo.type))); 
			}
			return false;
		}
				
		public String toString() {
			return toString("neg",type);
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
	public static final class Invert extends Code {
		public final Type type;		
		
		private Invert(Type type) {			
			this.type = type;
		}
		
		public int hashCode() {
			if(type == null) {
				return 239487;
			} else {
				return type.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if(o instanceof Invert) {
				Invert bo = (Invert) o;
				return (type == bo.type || (type != null && type
						.equals(bo.type))); 
			}
			return false;
		}
				
		public String toString() {
			return toString("invert",type);
		}
	}
	
	public static final class New extends Code {
		public final Type.Reference type;		
		
		private New(Type.Reference type) {			
			this.type = type;
		}
		
		public int hashCode() {
			if(type == null) {
				return 239487;
			} else {
				return type.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if(o instanceof New) {
				New bo = (New) o;
				return (type == bo.type || (type != null && type
						.equals(bo.type))); 
			}
			return false;
		}
				
		public String toString() {
			return toString("new",type);
		}
	}
	
	public static final class TupleLoad extends Code {
		public final Type.EffectiveTuple type;
		public final int index;
		
		private TupleLoad(Type.EffectiveTuple type, int index) {
			this.type = type;
			this.index = index;
		}
		
		public int hashCode() {
			if(type == null) {
				return 235;
			} else {
				return type.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if (o instanceof TupleLoad) {
				TupleLoad i = (TupleLoad) o;
				return index == i.index
						&& (type == i.type || (type != null && type
								.equals(i.type)));
			}
			return false;
		}
	
		public String toString() {
			return toString("tupleload " + index, (Type) type);
		}	
	}
	
	public static final class Dereference extends Code {
		public final Type.Reference type;		
		
		private Dereference(Type.Reference type) {			
			this.type = type;
		}
		
		public int hashCode() {
			if(type == null) {
				return 239487;
			} else {
				return type.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if(o instanceof Dereference) {
				Dereference bo = (Dereference) o;
				return (type == bo.type || (type != null && type
						.equals(bo.type))); 
			}
			return false;
		}
				
		public String toString() {
			return toString("deref",type);
		}
	}
	
	/**
	 * The void bytecode is used to indicate that a given register is no longer live.
	 * @author David J. Pearce
	 *
	 */
	public static class Void extends Code {
		public final Type type;
		public final int slot;
		
		private Void(Type type, int slot) {
			this.type = type;
			this.slot = slot;
		}
		
		public void slots(Set<Integer> slots) {
			slots.add(slot);
		}
		
		public Code remap(Map<Integer,Integer> binding) {
			Integer nslot = binding.get(slot);
			if(nslot != null) {
				return Code.Void(type, nslot);	
			} else {
				return this;
			}
		}
		
		public int hashCode() {
			return type.hashCode() + slot;
		}
		
		public boolean equals(Object o) {
			if(o instanceof Void) {
				Void i = (Void) o;
				return type.equals(i.type) && slot == i.slot;
			}
			return false;
		}
	
		public String toString() {
			return toString("void " + slot,type);
		}
	}
	
	public static String toString(String str, Type t) {
		if(t == null) {
			return str + " : ?";
		} else {
			return str + " : " + t;
		}
	}
	
	public static String toString(String str, Type before, Type after) {
		if(before == null || after == null) {
			return str + " : ?";
		} else {
			return str + " : " + before + " => " + after;
		}
	}
	
	private static final ArrayList<Code> values = new ArrayList<Code>();
	private static final HashMap<Code,Integer> cache = new HashMap<Code,Integer>();
	
	private static <T extends Code> T get(T type) {
		Integer idx = cache.get(type);
		if(idx != null) {
			return (T) values.get(idx);
		} else {					
			cache.put(type, values.size());
			values.add(type);
			return type;
		}
	}
}
