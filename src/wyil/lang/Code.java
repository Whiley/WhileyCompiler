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
	 * @param type
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
	
	public static End End(String label) {
		return get(new End(label));
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
	public static Invoke Invoke(Type.Fun fun, NameID name) {
		return get(new Invoke(fun,name));
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
	public static ForAll ForAll(Type type, int var, String label, Collection<Integer> modifies) {
		return get(new ForAll(type, var, label,modifies));
	}

	/**
	 * Construct a <code>multistore</code> bytecode which writes a value into a
	 * compound structure, as determined by a given access path.
	 * 
	 * @param type
	 *            --- record type.
	 * @param field
	 *            --- field to write.
	 * @return
	 */
	public static MultiStore MultiStore(Type type, int slot, int level, Collection<String> fields) {
		return get(new MultiStore(type,slot,level,fields));
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
	public static NewTuple NewTuple(Type.Record type) {
		return get(new NewTuple(type));
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
	
	public static Pop Pop(Type t) {
		return get(new Pop(t));
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
	public static IndirectSend IndirectSend(Type.Fun fun, boolean synchronous) {
		return get(new IndirectSend(fun,synchronous));
	}
	
	/**
	 * Construct an <code>indirectinvoke</code> bytecode which sends an indirect
	 * message to an actor. This may be either synchronous or asynchronous.
	 * 
	 * @param label
	 *            --- destination label.
	 * @return
	 */
	public static IndirectInvoke IndirectInvoke(Type.Fun fun) {
		return get(new IndirectInvoke(fun));
	}
	
	public static Label Label(String label) {
		return get(new Label(label));
	}
	
	public static final Skip Skip = new Skip();

	/**
	 * Construct an <code>send</code> bytecode which sends a message to an
	 * actor. This may be either synchronous or asynchronous.
	 * 
	 * @param label
	 *            --- destination label.
	 * @return
	 */
	public static Send Send(Type.Fun fun, NameID name, boolean synchronous) {
		return get(new Send(fun,name,synchronous));
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
	
	public static SubList SubList() {
		return null;
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
	public static Switch Switch(Type type, String defaultLabel, Map<Value,String> cases) {
		return get(new Switch(type,defaultLabel,cases));
	}

	/**
	 * Construct a <code>throw</code> bytecode which pops a value off the
	 * stack and throws it.
	 * 
	 * @param type
	 *            --- value type to throw 
	 * @return
	 */
	public static Throw Throw(Type t) {
		return get(new Throw(t));
	}
	
	public static UnOp UnOp(Type type, UOp op) {
		return get(new UnOp(type,op));
	}		
	
	// ===============================================================
	// Bytecode Implementations
	// ===============================================================
	
	public static final class Assert extends Code {
		public final String target;
		
		private  Assert(String target) {
			this.target = target;
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
		UNION{
			public String toString() { return "union"; }
		},
		INTERSECT{
			public String toString() { return "intersect"; }
		},
		DIFFERENCE{
			public String toString() { return "difference"; }
		},
		APPEND{
			public String toString() { return "append"; }
		},
		RANGE{
			public String toString() { return "range"; }
		},
		AND{
			public String toString() { return "and"; }
		},
		OR{
			public String toString() { return "or"; }
		},
		XOR{
			public String toString() { return "xor"; }
		}		
	};

	/**
	 * A binary operation (e.g. +,-,*,/) takes two items off the stack and
	 * pushes a single result.
	 * 
	 * @author djp
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
	 * @author djp
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
			return "convert " + from + " => " + to;
		}
	}
	
	/**
	 * A const bytecode pushes a constant value onto the stack. This includes
	 * integer constants, rational constants, list constants, set constants,
	 * dictionary constants, function constants, etc.
	 * 
	 * @author djp
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
	
	public static final class End extends Label {
		End(String label) {
			super(label);
		}
		
		public int hashCode() {
			return label.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof End) {
				End e = (End) o;
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
			return "fail " + msg;
		}		
	}
	
	/**
	 * The fieldload bytecode pops a record alias from the stack and reads the
	 * value from the given field, pusing it back onto the stack.
	 * 
	 * @author djp
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
			return toString("if" + op + " " + target,type);
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
				return toString("iftype " + slot + " " + test + " " + target,type);
			} else {
				return toString("iftype " + test + " " + target,type);
			}
		}
	}
	
	public static final class IndirectInvoke extends Code {		
		public final Type.Fun type;
		
		private IndirectInvoke(Type.Fun type) {
			this.type = type;
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
				return type == i.type || (type != null && type.equals(i.type));
			}
			return false;
		}
	
		public String toString() {
			return toString("indirectinvoke",type);
		}		
	}
	
	public static final class IndirectSend extends Code {
		 public final boolean asynchronous;		 
		 public final Type.Fun type;
			
		 private IndirectSend(Type.Fun type, boolean asynchronous) {
			 this.type = type;
			 this.asynchronous = asynchronous;
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
			 if(asynchronous) {
				 return toString("asend",type);
			 } else {
				 return toString("iasend",type);
			 }
		 }		
	}
	
	public static final class Invoke extends Code {		
		public final Type.Fun type;
		public final NameID name;
				
		private Invoke(Type.Fun type, NameID name) {
			this.type = type;
			this.name = name;
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
						&& (type == i.type || (type != null && type
								.equals(i.type)));
			}
			return false;
		}
	
		public String toString() {
			return toString("invoke " + name,type);
		}	
		
	}
	
	public static class Label extends Code {
		public final String label;
		
		private Label(String label) {
			this.label = label;
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
		public final HashSet<Integer> modified;
		
		private Loop(String target, Collection<Integer> modified) {
			this.target = target;
			this.modified = new HashSet<Integer>(modified);
		}
		
		public int hashCode() {
			return target.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof Loop) {
				Loop f = (Loop) o;
				return target.equals(f.target)
						&& modified.equals(f.modified);
			}
			return false;
		}
		
		public String toString() {
			return "loop " + target;
		}		
	}		

	public static final class ForAll extends Loop {
		public final int var;
		public final Type type;
				
		private ForAll(Type type, int var, String target, Collection<Integer> modified) {
			super(target,modified);
			this.type = type;
			this.var = var;			
		}
		
		public int hashCode() {
			return super.hashCode() + var;
		}
		
		public boolean equals(Object o) {
			if(o instanceof ForAll) {
				ForAll f = (ForAll) o;
				return target.equals(f.target) && type.equals(f.type)
						&& var == f.var && modified.equals(f.modified);
			}
			return false;
		}
		
		public String toString() {
			return toString("forall " + var + " " + target,type);
		}		
	}

	
	public static final class MultiStore extends Code {
		public final Type type;
		public final int level;
		public final int slot;
		public final ArrayList<String> fields;

		private MultiStore(Type type, int slot, int level, Collection<String> fields) {
			if (fields == null) {
				throw new IllegalArgumentException(
						"FieldStore fields argument cannot be null");
			}
			this.type = type;
			this.slot = slot;
			this.level = level;
			this.fields = new ArrayList<String>(fields);
		}

		public int hashCode() {
			if(type == null) {
				return level + fields.hashCode();
			} else {
				return type.hashCode() + slot + level + fields.hashCode();
			}
		}

		public boolean equals(Object o) {
			if (o instanceof MultiStore) {
				MultiStore i = (MultiStore) o;
				return (i.type == type || (type != null && type.equals(i.type)))
						&& level == i.level && slot == i.slot && fields.equals(i.fields);
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
			return toString("multistore " + slot + " #" + level + fs,type);
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
		public final Type.Record type;
		
		private NewTuple(Type.Record type) {
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
			if(o instanceof NewTuple) {
				NewTuple i = (NewTuple) o;
				return type == i.type || (type != null && type.equals(i.type));
			}
			return false;
		}
	
		public String toString() {
			return toString("newtuple",type);
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
	
	public static final class Pop extends Code {
		public final Type type;
		
		private Pop(Type type) {
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
			if (o instanceof Pop) {
				Pop i = (Pop) o;
				return type == i.type || (type != null && type.equals(i.type));
			}
			return false;
		}
	
		public String toString() {
			return toString("pop",type);
		}
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
	
	public static final class SubList extends Code {
		
	}

	public static final class Switch extends Code {
		public final Type type;
		public final HashMap<Value,String> branches;
		public final String defaultTarget;

		public Switch(Type type, String defaultTarget, Map<Value,String> branches) {			
			this.type = type;
			this.branches = new HashMap<Value,String>(branches);
			this.defaultTarget = defaultTarget;
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
			for (Map.Entry<Value, String> p : branches.entrySet()) {
				if (!firstTime) {
					table += ", ";
				}
				firstTime = false;
				table += p.getKey() + "->" + p.getValue();
			}
			table += ", *->" + defaultTarget;
			return "switch " + table;
		}
	}

	public static final class Send extends Code {		 
		 public final boolean synchronous;		 
		 public final NameID name;
		 public final Type.Fun type;
			
		 private Send(Type.Fun type, NameID name, boolean synchronous) {
			 this.type = type;
			 this.name = name;
			 this.synchronous = synchronous;
		 }

		 public int hashCode() {
			 return type.hashCode() + name.hashCode();
		 }

		 public boolean equals(Object o) {
			 if(o instanceof Send) {
				 Send i = (Send) o;
				 return type.equals(i.type) && name.equals(i.name);
			 }
			 return false;
		 }

		 public String toString() {
			 if(synchronous) {
				 return toString("send " + name,type);				 
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
			return type.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof Throw) {
				Throw i = (Throw) o;
				return type.equals(i.type);
			}
			return false;
		}
	
		public String toString() {
			return toString("throw",type);
		}
	}
	
	public static final class UnOp extends Code {
		public final Type type;
		public final UOp uop; 
		
		private UnOp(Type type, UOp uop) {
			this.uop = uop;
			this.type = type;
		}
		
		public int hashCode() {
			if(type == null) {
				return uop.hashCode();
			} else {
				return type.hashCode() + uop.hashCode();
			}
		}
		
		public boolean equals(Object o) {
			if(o instanceof UnOp) {
				UnOp bo = (UnOp) o;
				return (type == bo.type || (type != null && type
						.equals(bo.type))) && uop.equals(bo.uop); 
			}
			return false;
		}
				
		public String toString() {
			return toString(uop.toString(),type);
		}
	}
	
	public enum UOp { 
		NEG() {
			public String toString() { return "neg"; }
		},
		FLOOR() {
			public String toString() { return "floor"; }
		},
		CEIL() {
			public String toString() { return "ceil"; }
		},
		SPLIT() {
			public String toString() { return "split"; }
		},
		LENGTHOF() {
			public String toString() { return "lengthof"; }
		},
		PROCESSACCESS() {
			public String toString() { return "procload"; }
		},
		PROCESSSPAWN() {
			public String toString() { return "spawn"; }
		},		
		DEBUG() {
			public String toString() { return "debug"; }
		},
		FAIL() {
			public String toString() { return "fail"; }
		}
	}
	
	/**
	 * The void bytecode is used to indicate that a given register is no longer live.
	 * @author djp
	 *
	 */
	public static class Void extends Code {
		public final Type type;
		public final int slot;
		
		private Void(Type type, int slot) {
			this.type = type;
			this.slot = slot;
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
