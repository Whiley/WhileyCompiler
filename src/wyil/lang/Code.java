// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
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

public abstract class Code {
	public final static int THIS_SLOT = 0;
	
	// ===============================================================
	// Bytecode Constructors
	// ===============================================================
	
	/**
	 * Construct an <code>assert</code> bytecode which identifies a sequence of
	 * bytecodes which represent a runtime assertion.
	 * 
	 * @param label
	 *            --- end of block.
	 * @return
	 */
	public static Assert Assert(String label) {
		return get(new Assert(label));
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
	
	/**
	 * Construct a <code>dictload</code> bytecode which reads the value
	 * associated with a given key in a dictionary.
	 * 
	 * @param type
	 *            --- dictionary type.
	 * @return
	 */
	public static DictLoad DictLoad(Type.Dictionary type) {
		return get(new DictLoad(type));
	}
	
	public static LoopEnd End(String label) {
		return get(new LoopEnd(label));
	}
	
	public static ExternJvm ExternJvm(List<wyjvm.lang.Bytecode> bytecodes) {
		return get(new ExternJvm(bytecodes));
	}

	/**
	 * Construct a <code>fail</code> bytecode which indicates a runtime failure.
	 * 
	 * @param label
	 *            --- end of block.
	 * @return
	 */
	public static Fail Fail(String label) {
		return get(new Fail(label));
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
	public static FieldLoad FieldLoad(Type.Record type, String field) {
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
	public static Invoke Invoke(Type.Function fun, NameID name, boolean retval) {
		return get(new Invoke(fun,name,retval));
	}

	public static Not Not() {
		return get(new Not());
	}
	
	/**
	 * Construct a <code>load</code> bytecode which reads a given register.
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
	
	public static ListLength ListLength(Type.List type) {
		return get(new ListLength(type));
	}
	
	public static SubList SubList(Type.List type) {
		return get(new SubList(type));
	}
	
	public static ListAppend ListAppend(Type.List type, OpDir dir) {
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
	public static ListLoad ListLoad(Type.List type) {
		return get(new ListLoad(type));
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
	public static ForAll ForAll(Type type, int var,
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
	public static IndirectSend IndirectSend(Type.Method meth, boolean synchronous, boolean retval) {
		return get(new IndirectSend(meth,synchronous,retval));
	}
	
	/**
	 * Construct an <code>indirectinvoke</code> bytecode which sends an indirect
	 * message to an actor. This may be either synchronous or asynchronous.
	 * 
	 * @param label
	 *            --- destination label.
	 * @return
	 */
	public static IndirectInvoke IndirectInvoke(Type.Function fun, boolean retval) {
		return get(new IndirectInvoke(fun,retval));
	}
	
	public static Invert Invert(Type type) {
		return get(new Invert(type));
	}	
	
	public static Label Label(String label) {
		return get(new Label(label));
	}
	
	public static final Skip Skip = new Skip();
	
	public static SetLength SetLength(Type.Set type) {
		return get(new SetLength(type));
	}
	
	public static SetUnion SetUnion(Type.Set type, OpDir dir) {
		return get(new SetUnion(type,dir));
	}
	
	public static SetIntersect SetIntersect(Type.Set type, OpDir dir) {
		return get(new SetIntersect(type,dir));
	}
	
	public static SetDifference SetDifference(Type.Set type, OpDir dir) {
		return get(new SetDifference(type,dir));
	}
	
	public static StringAppend StringAppend(OpDir dir) {
		return get(new StringAppend(dir));
	}
	
	public static SubString SubString() {
		return get(new SubString());
	}
	
	public static StringLength StringLength() {
		return get(new StringLength());
	}
	
	public static StringLoad StringLoad() {
		return get(new StringLoad());
	}
	
	/**
	 * Construct an <code>send</code> bytecode which sends a message to an
	 * actor. This may be either synchronous or asynchronous.
	 * 
	 * @param label
	 *            --- destination label.
	 * @return
	 */
	public static Send Send(Type.Method meth, NameID name, boolean synchronous, boolean retval) {
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
	
	public static Negate Negate(Type type) {
		return get(new Negate(type));
	}		
	
	public static Spawn Spawn(Type.Process type) {
		return get(new Spawn(type));
	}
	
	public static ProcLoad ProcLoad(Type.Process type) {
		return get(new ProcLoad(type));
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
	
	public static final class Assert extends Code {
		public final String target;
		
		private  Assert(String target) {
			this.target = target;
		}
	
		public Assert relabel(Map<String,String> labels) {
			String nlabel = labels.get(target);
			if(nlabel == null) {
				return this;
			} else {
				return Assert(nlabel);
			}
		}
		
		public int hashCode() {
			return target.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof Assert) {
				return target.equals(((Assert)o).target);
			}
			return false;
		}
		
		public String toString() {
			return "assert " + target;
		}		
	}
	
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
	 * A binary operation (e.g. +,-,*,/) takes two items off the stack and
	 * pushes a single result.
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
	 * A catch bytecode is similar to a switch. It identifies a block within
	 * which exception handlers are active.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Catch extends Code {
		
	}

	/**
	 * A convert operation represents a conversion from a value of one type to
	 * another. The purpose of the conversion bytecode is to make it easy for
	 * implementations which have different representations of data types to
	 * convert between them.
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
	 * A const bytecode pushes a constant value onto the stack. This includes
	 * integer constants, rational constants, list constants, set constants,
	 * dictionary constants, function constants, etc.
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
	 * A destructure operation takes a compound value and "destructures" it into
	 * multiple values. For example, a rational can be destructured into two
	 * integers. Or, an n-tuple can be structured into n values.
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
	
	public static final class DictLoad extends Code {
		public final Type.Dictionary type;				
		
		private DictLoad(Type.Dictionary type) {
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
			if(o instanceof DictLoad) {
				DictLoad i = (DictLoad) o;
				return type == i.type || (type != null && type.equals(i.type));
			}
			return false;
		}
	
		public String toString() {
			return toString("dictload",type);
		}	
	}	
	
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
	
	public static final class ExternJvm extends Code {
		public final List<wyjvm.lang.Bytecode> bytecodes;
		
		ExternJvm(List<wyjvm.lang.Bytecode> bytecodes) {
			this.bytecodes = new ArrayList(bytecodes);
		}
		public int hashCode() {
			return bytecodes.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof ExternJvm) {
				ExternJvm e = (ExternJvm) o;
				return e.bytecodes.equals(bytecodes);
			}
			return false;
		}
		public String toString() {
			return "externjvm";
		}
	}
	
	public static final class Fail extends Code {
		public final String msg;
		
		private Fail(String msg) {
			this.msg = msg;
		}
		
		public int hashCode() {
			return msg.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof Fail) {
				return msg.equals(((Fail)o).msg);
			}
			return false;
		}
		
		public String toString() {
			return "fail \"" + msg + "\"";
		}		
	}
	
	/**
	 * The fieldload bytecode pops a record alias from the stack and reads the
	 * value from the given field, pusing it back onto the stack.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class FieldLoad extends Code {
		public final Type.Record type;		
		public final String field;
				
		private FieldLoad(Type.Record type, String field) {
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
			return toString("fieldload " + field,type);			
		}	
	}
	
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
	
	public static final class IndirectInvoke extends Code {		
		public final Type.Function type;
		public final boolean retval;
		
		private IndirectInvoke(Type.Function type, boolean retval) {
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
	
	public static final class IndirectSend extends Code {
		 public final boolean synchronous;
		 public final boolean retval;
		 public final Type.Method type;
			
		 private IndirectSend(Type.Method type, boolean synchronous, boolean retval) {
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
	
	public static final class Invoke extends Code {		
		public final Type.Function type;
		public final NameID name;
		public final boolean retval;
				
		private Invoke(Type.Function type, NameID name, boolean retval) {
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
		
	public static final class ListAppend extends Code {				
		public final OpDir dir;
		public final Type.List type;
		
		private ListAppend(Type.List type, OpDir dir) {			
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
			return toString("listappend" + dir.toString(),type);
		}
	}
	
	public static final class ListLength extends Code {						
		public final Type.List type;
		
		private ListLength(Type.List type) {									
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
			if (o instanceof ListLength) {
				ListLength setop = (ListLength) o;
				return (type == setop.type || (type != null && type
						.equals(setop.type)));
			}
			return false;
		}
				
		public String toString() {
			return toString("listlength",type);
		}
	}
	
	public static final class SubList extends Code {						
		public final Type.List type;
		
		private SubList(Type.List type) {									
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
			return toString("sublist",type);
		}
	}
	
	public static final class ListLoad extends Code {
		public final Type.List type;				
		
		private ListLoad(Type.List type) {
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
			if(o instanceof ListLoad) {
				ListLoad i = (ListLoad) o;
				return type == i.type || (type != null && type.equals(i.type));
			}
			return false;
		}
	
		public String toString() {
			return toString("listload",type);
		}	
	}		
	
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

	public static final class ForAll extends Loop {
		public final int slot;
		public final Type type;
				
		private ForAll(Type type, int slot, String target, Collection<Integer> modifies) {
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
			if(o instanceof ForAll) {
				ForAll f = (ForAll) o;
				return target.equals(f.target) && type.equals(f.type)
						&& slot == f.slot && modifies.equals(f.modifies);
			}
			return false;
		}
		
		public String toString() {			
			return toString("forall " + slot + " " + modifies,type);
		}		
	}

	public static abstract class LVal {
		protected Type type;
		
		public LVal(Type t) {
			this.type = t;
		}
		
		public Type rawType() {
			return type;
		}
	}
	
	public static final class DictLVal extends LVal {
		public DictLVal(Type t) {
			super(t);			
			if(Type.effectiveDictionaryType(t) == null) {
				throw new IllegalArgumentException("Invalid Dictionary Type");
			}
		}
		
		public Type.Dictionary type() {			
			return Type.effectiveDictionaryType(type);
		}
	}
	
	public static final class ListLVal extends LVal {
		public ListLVal(Type t) {
			super(t);
			if(Type.effectiveListType(t) == null) {
				throw new IllegalArgumentException("Invalid List Type");
			}
		}
		
		public Type.List type() {
			return Type.effectiveListType(type);
		}
	}
	
	public static final class StringLVal extends LVal {
		public StringLVal() {
			super(Type.T_STRING);			
		}		
	}
	
	public static final class RecordLVal extends LVal {
		public final String field;
		
		public RecordLVal(Type t, String field) {
			super(t);
			this.field = field;
			Type.Record rt = Type.effectiveRecordType(t);
			if(rt == null || !rt.fields().containsKey(field)) {
				throw new IllegalArgumentException("Invalid Record Type");
			}		
		}
		
		public Type.Record type() {
			return Type.effectiveRecordType(type);
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
			
			// TODO: sort out this hack
			if(Type.isSubtype(Type.Process(Type.T_ANY), iter)) {
				Type.Process p = (Type.Process) iter;
				iter = p.element();
			}	
		}
		
		public LVal next() {
			Type raw = iter;
			index--;
			if(Type.isSubtype(Type.T_STRING,iter)) {
				iter = Type.T_CHAR;
				return new StringLVal();
			} else if(Type.isSubtype(Type.List(Type.T_ANY),iter)) {			
				Type.List list = Type.effectiveListType(iter);											
				iter = list.element();
				return new ListLVal(raw);
			} else if(Type.isSubtype(Type.Dictionary(Type.T_ANY, Type.T_ANY),iter)) {			
				// this indicates a dictionary access, rather than a list access			
				Type.Dictionary dict = Type.effectiveDictionaryType(iter);											
				iter = dict.value();	
				return new DictLVal(raw);
			} else  if(Type.effectiveRecordType(iter) != null) {
				Type.Record rec = Type.effectiveRecordType(iter);				
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
			
			// TODO: sort out this hack
			if (Type.isSubtype(Type.Process(Type.T_ANY), iter)) {
				Type.Process p = (Type.Process) iter;
				iter = p.element();
			}
			
			int fieldIndex = 0;
			for (int i = 0; i != level; ++i) {
				if (Type.isSubtype(Type.T_STRING, iter)) {
					iter = Type.T_CHAR;
				} else if (Type.isSubtype(Type.List(Type.T_ANY), iter)) {
					Type.List list = Type.effectiveListType(iter);
					iter = list.element();
				} else if (Type.isSubtype(
						Type.Dictionary(Type.T_ANY, Type.T_ANY), iter)) {
					// this indicates a dictionary access, rather than a list
					// access
					Type.Dictionary dict = Type.effectiveDictionaryType(iter);
					iter = dict.value();
				} else if (Type.effectiveRecordType(iter) != null) {
					Type.Record rec = Type.effectiveRecordType(iter);
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
		public final Type.Set type;
		
		private SetUnion(Type.Set type, OpDir dir) {
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
			return toString("union" + dir.toString(),type);
		}
	}
	
	public static final class SetIntersect extends Code {		
		public final OpDir dir;
		public final Type.Set type;
		
		private SetIntersect(Type.Set type, OpDir dir) {
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
			return toString("intersect" + dir.toString(),type);
		}
	}
	
	public static final class SetDifference extends Code {		
		public final OpDir dir;
		public final Type.Set type;
		
		private SetDifference(Type.Set type, OpDir dir) {
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
			return toString("difference" + dir.toString(),type);
		}
	}
	
	public static final class SetLength extends Code {				
		public final Type.Set type;
		
		private SetLength(Type.Set type) {
			this.type = type;			
		}
		
		public int hashCode() {
			if(type == null) {
				return 558723; 
			} else {
				return type.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if (o instanceof SetLength) {
				SetLength setop = (SetLength) o;
				return (type == setop.type || (type != null && type
						.equals(setop.type)));
			}
			return false;
		}
				
		public String toString() {
			return toString("setlength",type);
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
	
	public static final class StringLength extends Code {		
		private StringLength() {
		}
		
		public int hashCode() {			
			return 982345;			
		}
		
		public boolean equals(Object o) {
			return o instanceof StringLength;
		}
				
		public String toString() {
			return toString("stringlen",Type.T_STRING);
		}
	}
	
	public static final class StringLoad extends Code {		
		private StringLoad() {
		}
		
		public int hashCode() {			
			return 12387;			
		}
		
		public boolean equals(Object o) {
			return o instanceof StringLoad;
		}
				
		public String toString() {
			return toString("stringload",Type.T_STRING);
		}
	}
	
	public static final class Skip extends Code {
		Skip() {}
		public int hashCode() {
			return 101;
		}
		public boolean equals(Object o) {
			return o instanceof Debug;
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
		 public final Type.Method type;
			
		 private Send(Type.Method type, NameID name, boolean synchronous, boolean retval) {
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
			return "switch " + table;
		}
	}
	
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
	
	public static final class Spawn extends Code {
		public final Type.Process type;		
		
		private Spawn(Type.Process type) {			
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
			if(o instanceof Spawn) {
				Spawn bo = (Spawn) o;
				return (type == bo.type || (type != null && type
						.equals(bo.type))); 
			}
			return false;
		}
				
		public String toString() {
			return toString("spawn",type);
		}
	}
	
	public static final class ProcLoad extends Code {
		public final Type.Process type;		
		
		private ProcLoad(Type.Process type) {			
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
			if(o instanceof ProcLoad) {
				ProcLoad bo = (ProcLoad) o;
				return (type == bo.type || (type != null && type
						.equals(bo.type))); 
			}
			return false;
		}
				
		public String toString() {
			return toString("procload",type);
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
