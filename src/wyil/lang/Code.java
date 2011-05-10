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

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Code {
	
	public static BinOp BinOp(Type type, BOp op) {
		return get(new BinOp(type,op));
	}
	
	public static Const Const(Value value) {
		return get(new Const(value));
	}
	
	public static Convert Convert(Type from, Type to) {
		return get(new Convert(from,to));
	}
	
	public static Goto Goto(String label) {
		return get(new Goto(label));
	}

	public static Return Return(Type t) {
		return get(new Return(t));
	}
	
	public static IfGoto IfGoto(Type type, COp cop, String label) {
		return get(new IfGoto(type,cop,label));
	}
	
	public static Label Label(String label) {
		return get(new Label(label));
	}
		
	public static UnOp UnOp(Type type, UOp op) {
		return get(new UnOp(type,op));
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
			this.bop = bop;
			this.type = type;
		}
		
		public int hashCode() {
			return type.hashCode() + bop.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof BinOp) {
				BinOp bo = (BinOp) o;
				return type.equals(bo.type) && bop.equals(bo.bop); 
			}
			return false;
		}
				
		public String toString() {
			return bop + " " + type;
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
			this.from = from;
			this.to = to;
		}
		
		public int hashCode() {
			return from.hashCode() + to.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof Convert) {
				Convert c = (Convert) o;
				return from.equals(c.from) && to.equals(c.to);  
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
			return "const " + constant;
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
		public final Type type;		
		public final String field;
				
		private FieldLoad(Type type, String field) {
			this.type = type;
			this.field = field;
		}
		
		public int hashCode() {
			return type.hashCode() + field.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof FieldLoad) {
				FieldLoad i = (FieldLoad) o;
				return type.equals(i.type) && field.equals(i.field);
			}
			return false;
		}
	
		public String toString() {
			return "fieldload " + field + " " + type;			
		}	
	}

	/**
	 * The fieldstore bytecode pops a value from the stack, followed by a record
	 * alias. It then overwrites the given field with that value.
	 * 
	 * @author djp
	 * 
	 */
	public static final class FieldStore extends Code {
		public final Type type;
		public final String field;

		private FieldStore(Type type, String field) {
			this.type = type;
			this.field = field;
		}

		public int hashCode() {
			return type.hashCode() + field.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof FieldStore) {
				FieldStore i = (FieldStore) o;
				return type.equals(i.type) && field.equals(i.field);
			}
			return false;
		}

		public String toString() {
			return "fieldstore " + field + " " + type;
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
			this.type = type;
			this.op = op;						
			this.target = target;
		}
		
		public int hashCode() {
			return op.hashCode() + target.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof IfGoto) {
				IfGoto ig = (IfGoto) o;
				return op == ig.op
						&& type.equals(ig.type)
						&& target.equals(ig.target);
			}
			return false;
		}
	
		public String toString() {
			return "if" + op + " " + target;
		}
	}
	
	public enum COp { 
		EQ() {
			public String toString() { return "=="; }
		},
		NEQ{
			public String toString() { return "!="; }
		},
		LT{
			public String toString() { return "<"; }
		},
		LTEQ{
			public String toString() { return "<="; }
		},
		GT{
			public String toString() { return ">"; }
		},
		GTEQ{
			public String toString() { return ">="; }
		},
		ELEMOF{
			public String toString() { return "in"; }
		},
		SUBSET{
			public String toString() { return "<"; }
		},
		SUBSETEQ{
			public String toString() { return "<="; }
		},
		SUBTYPEEQ() {
			public String toString() { return "<:"; }
		},
		NSUBTYPEEQ() {
			public String toString() { return "<!"; }
		}
	};		
	
	public static final class IndirectInvoke extends Code {		
		public final Type.Fun type;
		
		private IndirectInvoke(Type.Fun type) {
			this.type = type;
		}
		
		public int hashCode() {
			return type.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof IndirectInvoke) {
				IndirectInvoke i = (IndirectInvoke) o;
				return type.equals(i.type);
			}
			return false;
		}
	
		public String toString() {
			return "indirectinvoke " + type;
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
			 return type.hashCode();
		 }

		 public boolean equals(Object o) {
			 if(o instanceof IndirectSend) {
				 IndirectSend i = (IndirectSend) o;
				 return type.equals(i.type);
			 }
			 return false;
		 }

		 public String toString() {
			 if(asynchronous) {
				 return "iasend " + type;
			 } else {
				 return "isend " + type;
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
			return type.hashCode() + name.hashCode();
		}
		
		public boolean equals(Object o) {
			if (o instanceof Invoke) {
				Invoke i = (Invoke) o;
				return type.equals(i.type) && name.equals(i.name);
			}
			return false;
		}
	
		public String toString() {
			return "invoke " + name + ":" + type;
		}	
		
	}
	
	public static final class Label extends Code {
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
		public final Type type;				
		
		private ListLoad(Type type) {
			this.type = type;
		}
		
		public int hashCode() {
			return type.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof ListLoad) {
				ListLoad i = (ListLoad) o;
				return type.equals(i.type);
			}
			return false;
		}
	
		public String toString() {
			return "listload " + type;
		}	
	}
	
	public static final class ListStore extends Code {
		public final Type type;				
		
		private ListStore(Type type) {
			this.type = type;
		}
		
		public int hashCode() {
			return type.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof ListStore) {
				ListStore i = (ListStore) o;
				return type.equals(i.type);
			}
			return false;
		}
	
		public String toString() {
			return "liststore " + type;
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
			return type.hashCode() + slot;
		}
		
		public boolean equals(Object o) {
			if(o instanceof Load) {
				Load i = (Load) o;
				return type.equals(i.type) && slot == i.slot;
			}
			return false;
		}
	
		public String toString() {
			return "load " + type;
		}	
	}		
	
	public static final class Loop extends Code {
		
	}	

	public static final class New extends Code {
		public final Type type;
		
		private New(Type type) {
			this.type = type;
		}
		
		public int hashCode() {
			return type.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof New) {
				New i = (New) o;
				return type.equals(i.type);
			}
			return false;
		}
	
		public String toString() {
			return "new " + type;
		}	
	}
	
	public static final class Nop extends Code {
		private Nop() {}
		public String toString() { return "nop"; }
	}	
	
	public static final class Pop extends Code {
		private final Type type;
		
		private Pop(Type type) {
			this.type = type;
		}
		
		public int hashCode() {
			return type.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof Pop) {
				Pop i = (Pop) o;
				return type.equals(i.type);
			}
			return false;
		}
	
		public String toString() {
			return "pop " + type;
		}
	}

	public static final class Return extends Code {
		private final Type type;
		
		private Return(Type type) {
			this.type = type;
		}
		
		public int hashCode() {
			return type.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof Return) {
				Return i = (Return) o;
				return type.equals(i.type);
			}
			return false;
		}
	
		public String toString() {
			return "return " + type;
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
			return type.hashCode() + slot;
		}
		
		public boolean equals(Object o) {
			if(o instanceof Store) {
				Store i = (Store) o;
				return type.equals(i.type) && slot == i.slot;
			}
			return false;
		}
	
		public String toString() {
			return "store " + type;
		}	
	}	
	
	public static final class SubList extends Code {
		
	}

	public static final class Switch extends Code {
		
	}

	public static final class Send extends Code {		 
		 public final boolean asynchronous;		 
		 public final Type.Fun type;
			
		 private Send(Type.Fun type, boolean asynchronous) {
			 this.type = type;
			 this.asynchronous = asynchronous;
		 }

		 public int hashCode() {
			 return type.hashCode();
		 }

		 public boolean equals(Object o) {
			 if(o instanceof Send) {
				 Send i = (Send) o;
				 return type.equals(i.type);
			 }
			 return false;
		 }

		 public String toString() {
			 if(asynchronous) {
				 return "asend " + type;
			 } else {
				 return "send " + type;
			 }
		 }	
	}

	public static final class Throw extends Code {
		
	}
	
	public static final class UnOp extends Code {
		public final Type type;
		public final UOp uop; 
		
		private UnOp(Type type, UOp uop) {
			this.uop = uop;
			this.type = type;
		}
		
		public int hashCode() {
			return type.hashCode() + uop.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof UnOp) {
				UnOp bo = (UnOp) o;
				return type.equals(bo.type) && uop.equals(bo.uop); 
			}
			return false;
		}
				
		public String toString() {
			return uop + " " + type;
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
			return "void " + slot + " " + type;
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
