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

import wycc.lang.NameID;
import wycc.util.Pair;

/**
 * <p>
 * Each bytecode has a binary format which identifies the <i>opcode</i>,
 * <i>operand registers</i> <i>types</i> and <i>other
 * items</i> used (e.g. names, constants, etc). The generic organisation of a
 * bytecode is as follows:
 * </p>
 *
 * <pre>
 * +--------+----------+-------+-------------+
 * | opcode | operands | types | other items |
 * +--------+----------+-------+-------------+
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
 * etc), whilst <i>fmt</i> identifies the bytecode format. 
 * 
 * @author David J. Pearce
 */
public abstract class Bytecode {
	protected final int[] operands;		
	
	public Bytecode(int... operands) {			
		this.operands = operands;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(operands());
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Bytecode) {
			Bytecode bo = (Bytecode) o;
			return Arrays.equals(operands(), bo.operands());
		}
		return false;
	}

	/**
	 * Return the operands of this bytecode.
	 *
	 * @return
	 */
	public int[] operands() {
		return operands;
	}

	/**
	 * Return the ith operand of this bytecode.
	 * 
	 * @param i
	 * @return
	 */
	public int operand(int i) {
		return operands[i];
	}	

	/**
	 * Return the opcode value of this bytecode.
	 * @return
	 */
	public abstract int opcode();
	

	/**
	 * A compound bytecode represents a bytecode that contains sequence of zero
	 * or more bytecodes. For example, the loop bytecode contains its loop body.
	 * The nested block of bytecodes is represented as a block identifier in the
	 * enclosing forest.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static abstract class Branching extends Bytecode {
		protected final String destination;

		public Branching(String destination, int... operands) {
			super(operands);
			this.destination = destination;
		}

		public String destination() {
			return destination;
		}
		
		public int hashCode() {
			return super.hashCode() ^ destination.hashCode();
		}
		
		public boolean equals(Object o) {
			if(o instanceof Branching) {
				Branching abc = (Branching) o;
				return destination.equals(abc.destination) && super.equals(o);
			}
			return false;
		}
	}


	// ===============================================================
	// Bytecode Expressions
	// ===============================================================

	/**
	 * Represents the class of bytecodes which correspond to expressions in the
	 * source language.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public abstract static class Expr extends Bytecode {	
		public Expr(int... operands) {
			super(operands);			
		}
	}
	
	/**
	 * Represents the set of valid operators (e.g. '+','-',etc).
	 *
	 * @author David J. Pearce
	 *
	 */
	public enum OperatorKind {
		// Unary
		NEG(OPCODE_neg) {
			public String toString() {
				return "neg";
			}
		},
		NOT(OPCODE_logicalnot) {
			public String toString() {
				return "not";
			}
		},
		BITWISEINVERT(OPCODE_bitwiseinvert) {
			public String toString() {
				return "invert";
			}
		},
		DEREFERENCE(OPCODE_dereference) {
			public String toString() {
				return "deref";
			}
		},
		ARRAYLENGTH(OPCODE_arraylength) {
			public String toString() {
				return "length";
			}
		},
		// Binary
		ADD(OPCODE_add) {
			public String toString() {
				return "add";
			}
		},
		SUB(OPCODE_sub) {
			public String toString() {
				return "sub";
			}
		},
		MUL(OPCODE_mul) {
			public String toString() {
				return "mul";
			}
		},
		DIV(OPCODE_div) {
			public String toString() {
				return "div";
			}
		},
		REM(OPCODE_rem) {
			public String toString() {
				return "rem";
			}
		},
		EQ(OPCODE_eq) {
			public String toString() {
				return "eq";
			}
		},
		NEQ(OPCODE_ne) {
			public String toString() {
				return "ne";
			}
		},
		LT(OPCODE_lt) {
			public String toString() {
				return "lt";
			}
		},
		LTEQ(OPCODE_le) {
			public String toString() {
				return "le";
			}
		},
		GT(OPCODE_gt) {
			public String toString() {
				return "gt";
			}
		},
		GTEQ(OPCODE_ge) {
			public String toString() {
				return "ge";
			}
		},
		AND(OPCODE_logicaland) {
			public String toString() {
				return "land";
			}
		},
		OR(OPCODE_logicalor) {
			public String toString() {
				return "lor";
			}
		},
		BITWISEOR(OPCODE_bitwiseor) {
			public String toString() {
				return "bor";
			}
		},
		BITWISEXOR(OPCODE_bitwisexor) {
			public String toString() {
				return "bxor";
			}
		},
		BITWISEAND(OPCODE_bitwiseand) {
			public String toString() {
				return "band";
			}
		},
		LEFTSHIFT(OPCODE_shl) {
			public String toString() {
				return "shl";
			}
		},
		RIGHTSHIFT(OPCODE_shr) {
			public String toString() {
				return "shr";
			}
		},
		ARRAYINDEX(OPCODE_arrayindex) {
			public String toString() {
				return "indexof";
			}
		},
		ARRAYGENERATOR(OPCODE_arraygen) {
			public String toString() {
				return "arraygen";
			}
		},
		ARRAYCONSTRUCTOR(OPCODE_array) {
			public String toString() {
				return "array";
			}
		},
		RECORDCONSTRUCTOR(OPCODE_record) {
			public String toString() {
				return "record";
			}
		},
		IS(OPCODE_is) {
			public String toString() {
				return "is";
			}
		},
		NEW(OPCODE_newobject) {
			public String toString() {
				return "new";
			}
		},
		ASSIGN(OPCODE_assign) {
			public String toString() {
				return "assign";
			}
		};
		public int offset;

		private OperatorKind(int offset) {
			this.offset = offset;
		}
	};

	/**
	 * <p>
	 * An operator reads one or more operand values, and applies a given
	 * function them to produce a value.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Operator extends Expr {
		private final OperatorKind kind;

		public Operator(int[] operands, OperatorKind bop) {
			super(operands);
			this.kind = bop;
		}

		@Override
		public int opcode() {
			return kind().offset;
		}

		public int hashCode() {
			return kind.hashCode() + super.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof Operator) {
				Operator bo = (Operator) o;
				return kind.equals(bo.kind) && super.equals(bo);
			}
			return false;
		}

		public String toString() {
			return kind() + arrayToString(operands());
		}

		public OperatorKind kind() {
			return kind;
		}
	}

	/**
	 * This corresponds to an explicit or implicit cast in the source language.
	 * This bytecode is the only way to change the type of a value. It's purpose
	 * is to simplify implementations which have different representations of
	 * data types. A convert bytecode must be inserted whenever the type of a
	 * value changes. For example, when a variable is retyped using the
	 * <code>is</code> operator, we must convert its value into a value of the
	 * new type.
	 * 
	 * <p>
	 * <b>NOTE:</b> In many cases, this bytecode may correspond to a nop on the
	 * hardware. Consider converting from <code>any[]</code> to <code>any</code>
	 * . On the JVM, <code>any</code> translates to <code>Object</code>, whilst
	 * <code>any[]</code> translates to <code>List</code> (which is an instance
	 * of <code>Object</code>). Thus, no conversion is necessary since
	 * <code>List</code> can safely flow into <code>Object</code>.
	 * </p>
	 *
	 */
	public static final class Convert extends Expr {
		private final Type type;
		
		public Convert(int operand, Type type) {
			super(operand);
			this.type = type;
		}

		public Type type() {
			return type;
		}

		public int opcode() {
			return OPCODE_convert;
		}

		public boolean equals(Object o) {
			return o instanceof Convert && super.equals(o);
		}

		public String toString() {
			return "convert %" + operand(0) + " " + type();
		}
	}

	/**
	 * Read a constant value, such as an <i>integer</i>, <i>array</i>, etc.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Const extends Expr {
		private final Constant constant;

		public Const(Constant constant) {
			this.constant = constant;
		}

		public int opcode() {
			return OPCODE_const;
		}

		public Constant constant() {
			return constant;
		}

		public int hashCode() {
			return constant.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof Const) {
				Const c = (Const) o;
				return constant.equals(c.constant);
			}
			return false;
		}

		public String toString() {
			return constant + " : " + constant.type();
		}
	}

	/**
	 * Reads a given field from a record.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class FieldLoad extends Expr {
		private final String field;

		public FieldLoad(Type.EffectiveRecord type, int operand, String field) {
			super(operand);
			if (field == null) {
				throw new IllegalArgumentException("FieldLoad field argument cannot be null");
			}
			this.field = field;
		}

		public int opcode() {
			return OPCODE_fieldload;
		}

		public int hashCode() {
			return super.hashCode() + field.hashCode();
		}

		public String fieldName() {
			return field;
		}
		
		public boolean equals(Object o) {
			if (o instanceof FieldLoad) {
				FieldLoad i = (FieldLoad) o;
				return super.equals(i) && field.equals(i.field);
			}
			return false;
		}

		public String toString() {
			return "fieldload %" + operand(0) + " " + field;
		}
	}


	/**
	 * Represents an indirect function or method call. For example, consider the
	 * following:
	 *
	 * <pre>
	 * type func_t is function(int)->int
	 * 
	 * function fun(func_t f, int x) -> int:
	 *    return f(x)
	 * </pre>
	 *
	 * Here, the function call <code>f(x)</code> is indirect as the called
	 * function is determined by the variable <code>f</code>.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class IndirectInvoke extends Expr {
		private final Type.FunctionOrMethod type;
		
		/**
		 * Construct an indirect invocation bytecode which assigns to an
		 * optional target register the result from indirectly invoking a
		 * function in a given operand with a given set of parameter operands.
		 *
		 * @param type.
		 *            Function or method type.
		 * @param operand
		 *            Operand holding function pointer through which indirect
		 *            invocation is made.
		 * @param operands
		 *            Operands holding parameters for the invoked function
		 */
		public IndirectInvoke(Type.FunctionOrMethod type, int operand, int[] operands) {
			super(append(operand, operands));
			this.type = type;
		}

		/**
		 * Return operand holding the indirect function/method reference.
		 *
		 * @return
		 */
		public int reference() {
			return operand(0);
		}

		/**
		 * Return operand holding the ith parameter for the invoked function.
		 *
		 * @param i
		 * @return
		 */
		public int parameter(int i) {
			return operand(i + 1);
		}

		/**
		 * Return operands holding parameters for the invoked function.
		 *
		 * @return
		 */
		public int[] parameters() {
			return Arrays.copyOfRange(operands(), 1, operands().length);
		}

		public int opcode() {
			return OPCODE_indirectinvoke;
		}

		public Type.FunctionOrMethod type() {
			return type;
		}

		public boolean equals(Object o) {
			return o instanceof IndirectInvoke && super.equals(o);
		}

		public String toString() {
			return "indirectinvoke %" + reference() + " " + arrayToString(parameters());
		}
	}

	/**
	 * Corresponds to a function or method call whose parameters are read from
	 * zero or more operands. A return value may or may not be given.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static final class Invoke extends Expr {
		private final NameID name;
		private final Type.FunctionOrMethod type;

		public Invoke(Type.FunctionOrMethod type, int[] operands, NameID name) {
			super(operands);
			this.name = name;
			this.type = type;
		}

		public int opcode() {
			return OPCODE_invoke;
		}

		public NameID name() {
			return name;
		}
		
		public int hashCode() {
			return name.hashCode() + super.hashCode();
		}

		public Type.FunctionOrMethod type() {
			return type;
		}

		public boolean equals(Object o) {
			if (o instanceof Invoke) {
				Invoke i = (Invoke) o;
				return name().equals(i.name) && super.equals(i);
			}
			return false;
		}

		public String toString() {
			return "invoke " + arrayToString(operands()) + " " + name;
		}
	}

	public static final class Lambda extends Expr {
		private final NameID name;
		private final Type.FunctionOrMethod type;

		public Lambda(Type.FunctionOrMethod type, int[] operands, NameID name) {
			super(operands);
			this.name = name;
			this.type = type;
		}

		public int opcode() {
			return OPCODE_lambda;
		}

		public NameID name() {
			return name;
		}
		
		public int hashCode() {
			return name.hashCode() + super.hashCode();
		}

		public Type.FunctionOrMethod type() {
			return type;
		}

		public boolean equals(Object o) {
			if (o instanceof Lambda) {
				Lambda i = (Lambda) o;
				return name().equals(i.name()) && super.equals(i);
			}
			return false;
		}

		public String toString() {
			return "lambda " + arrayToString(operands()) + " " + name;
		}
	}


	public static final class Quantify extends Loop {

		public Quantify(int startOperand, int endOperand, int indexOperand, int block) {
			super(block, startOperand, endOperand, indexOperand);
		}

		public int opcode() {
			return OPCODE_quantify;
		}

		public int startOperand() {
			return operands[0];
		}

		public int endOperand() {
			return operands[1];
		}

		public int indexOperand() {
			return operands[2];
		}

		public boolean equals(Object o) {
			if (o instanceof Quantify) {
				Quantify f = (Quantify) o;
				return super.equals(f);
			}
			return false;
		}

		public String toString() {
			return "quantify #" + blocks[0] + arrayToString(operands());
		}
	}

	// ===============================================================
	// Bytecode Statements
	// ===============================================================

	public abstract static class Stmt extends Bytecode {	
		public Stmt(int... operands) {
			super(operands);			
		}
	}
	
	/**
	 * A compound bytecode represents a bytecode that contains sequence of zero
	 * or more bytecodes. For example, a loop bytecode contains its loop body.
	 * The nested block of bytecodes is represented as a block identifier in the
	 * enclosing forest.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static abstract class Compound extends Stmt {
		protected final int[] blocks;

		public Compound(int[] blocks, int... operands) {
			super(operands);
			this.blocks = blocks;
		}

		public int block(int i) {
			return blocks[i];
		}
		
		public int[] blocks() {
			return blocks;
		}
		
		public int hashCode() {
			return super.hashCode() ^ Arrays.hashCode(blocks);
		}
		
		public boolean equals(Object o) {
			if(o instanceof Compound) {
				Compound abc = (Compound) o;
				return Arrays.equals(blocks, abc.blocks) && super.equals(o);
			}
			return false;
		}
	}
	
	
	/**
	 * Read a string from the operand and print it to the debug console.
	 *
	 * <b>NOTE</b> This bytecode is not intended to form part of the program's
	 * operation. Rather, it is to facilitate debugging within functions (since
	 * they cannot have side-effects). Furthermore, if debugging is disabled,
	 * this bytecode is a nop.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Debug extends Stmt {

		public Debug(int operand) {
			super(operand);
		}

		public int opcode() {
			return OPCODE_debug;
		}

		public boolean equals(Object o) {
			return o instanceof Debug && super.equals(o);
		}

		public String toString() {
			return "debug %" + operands[0];
		}
	}

	/**
	 * An abstract class representing either an <code>assert</code> or
	 * <code>assume</code> bytecode.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static abstract class AssertOrAssume extends Bytecode.Stmt {
		private AssertOrAssume(int operand) {
			super(operand);
		}
		public int operand() {
			return operand(0);
		}
	}

	/**
	 * Represents a block of bytecode instructions representing an assertion.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Assert extends AssertOrAssume {
		
		public Assert(int operand) {
			super(operand);
		}

		public int opcode() {
			return OPCODE_assert;
		}

		public String toString() {
			return "assert %" + operand(0);
		}

		public boolean equals(Object o) {
			return o instanceof Assert && super.equals(o);
		}
	}

	/**
	 * Represents a block of bytecode instructions representing an assumption.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Assume extends AssertOrAssume {

		public Assume(int operand) {
			super(operand);
		}

		public int opcode() {
			return OPCODE_assume;
		}

		public String toString() {
			return "assume %" + operand();
		}

		public boolean equals(Object o) {
			return o instanceof Assume && super.equals(o);
		}
	}

	/**
	 * A bytecode that halts execution by raising a runtime fault. This bytecode
	 * signals that some has gone wrong and can be used to mark dead-code, etc.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Fail extends Stmt {
		@Override
		public int opcode() {
			return OPCODE_fail;
		}

		public String toString() {
			return "fail";
		}
	}

	/**
	 * This should have been deleted by the time you read this.
	 * @author David J. Pearce
	 *
	 */
	public static final class Goto extends Branching {
		public Goto(String target) {
			super(target);
		}

		public int opcode() {
			return OPCODE_goto;
		}

		public boolean equals(Object o) {
			return o instanceof Goto && super.equals(o);
		}

		public String toString() {
			return "goto " + destination();
		}
	}

	/**
	 * Represents an if-else statement, which has a true branch and an optional
	 * false branch.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static final class If extends Compound {
		public If(int operand, int trueBlock) {
			super(new int[] { trueBlock }, operand);
		}
		
		public If(int operand, int trueBlock, int falseBlock) {
			super(new int[] { trueBlock, falseBlock }, operand);
		}

		public int opcode() {
			return OPCODE_if;
		}

		public boolean hasFalseBlock() {
			return blocks.length > 1;
		}
		
		public int trueBranch() {
			return blocks[0];
		}
		
		public int falseBranch() {
			return blocks[1];
		}
		
		public boolean equals(Object o) {
			return o instanceof If && super.equals(o);			
		}

		public String toString() {
			return "if" + " %" + operands[0] + " " + trueBranch() + " else " + falseBranch();
		}
	}

	/**
	 * Represents a block of bytecode instructions representing an assertion.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Invariant extends Assert {

		public Invariant(int block) {
			super(block);
		}

		public int opcode() {
			return OPCODE_invariant;
		}

		public String toString() {
			return "invariant";
		}

		public boolean equals(Object o) {
			if (o instanceof Invariant) {
				return super.equals(o);
			}
			return false;
		}
	}

	/**
	 * Represents the labelled destination of a branch or loop statement.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Label extends Stmt {
		private final String label;

		public Label(String label) {
			this.label = label;
		}

		public int opcode() {
			return -1;
		}

		public String label() {
			return label;
		}
		
		public int hashCode() {
			return label().hashCode();
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
	
	public static class Loop extends Compound {

		public Loop(int block, int... operands) {
			super(new int[]{block},operands);
		}

		public int opcode() {
			return OPCODE_loop;
		}

		public boolean equals(Object o) {
			if (o instanceof Loop) {
				return super.equals(o);
			}
			return false;
		}

		public int body() {
			return blocks[0];
		}
		
		public int[] modifiedOperands() {
			return operands;
		}

		public String toString() {
			return "loop " + arrayToString(operands) + " = " + blocks[0];
		}
	}

	/**
	 * Returns from the enclosing function or method, possibly returning one or
	 * more values.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static final class Return extends Stmt {

		public Return() {
		}
		
		public Return(int... operands) {
			super(operands);
		}

		@Override
		public int opcode() {
			return OPCODE_return;
		}

		public boolean equals(Object o) {
			if (o instanceof Return) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			String r = "return";
			for (int i = 0; i != operands.length; ++i) {
				if (i != 0) {
					r += ",";
				}
				r += " %" + operands[i];
			}
			return r;
		}
	}

	/**
	 * Performs a multi-way branch based on the value contained in the operand.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Switch extends Stmt {
		private final ArrayList<Pair<Constant, String>> branches;
		private final String defaultTarget;

		public Switch(int operand, String defaultTarget, Collection<Pair<Constant, String>> branches) {
			super(operand);
			this.branches = new ArrayList<Pair<Constant, String>>(branches);
			this.defaultTarget = defaultTarget;
		}

		@Override
		public int opcode() {
			return OPCODE_switch;
		}

		public boolean equals(Object o) {
			if (o instanceof Switch) {
				Switch ig = (Switch) o;
				return operands[0] == ig.operands[0] && defaultTarget().equals(ig.defaultTarget())
						&& branches().equals(ig.branches());
			}
			return false;
		}

		public String toString() {
			String table = "";
			boolean firstTime = true;
			for (Pair<Constant, String> p : branches()) {
				if (!firstTime) {
					table += ", ";
				}
				firstTime = false;
				table += p.first() + "->" + p.second();
			}
			table += ", *->" + defaultTarget();
			return "switch %" + operands[0] + " " + table;
		}

		
		public String defaultTarget() {
			return defaultTarget;
		}

		public ArrayList<Pair<Constant, String>> branches() {
			return branches;
		}
	}

	/**
	 * Represents an assignment statement in the source language.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static final class Update extends Stmt {
		/**
		 * Construct an Update bytecode which assigns to a given operand to a
		 * set of target registers. For indirect map/list updates, an additional
		 * set of operands is used to generate the appropriate keys. For field
		 * assignments, a set of fields is provided.
		 *
		 * @param lhs
		 *            LVal on left-hand side which is assigned to
		 * @param rhs
		 *            Operand on right-hand side whose value is assigned
		 */
		public Update(int lhs, int rhs) {
			super(lhs,rhs);			
		}

		public int opcode() {
			return OPCODE_update;
		}

		/**
		 * Returns operand from which assigned value is written to. This is also
		 * known as the "left-hand side".
		 *
		 * @return
		 */
		public int lhsOperand() {
			return operand(0);
		}
		
		/**
		 * Returns operand from which assigned value is read. This is also
		 * known as the "right-hand side".
		 *
		 * @return
		 */
		public int rhsOperand() {
			return operand(1);
		}

		public boolean equals(Object o) {
			if (o instanceof Update) {
				return super.equals(o);
			}
			return false;
		}

		public String toString() {
			return "%" + lhsOperand() + " = %" + rhsOperand();
		}
	}

	// =============================================================
	// Helpers
	// =============================================================

	/**
	 * Construct a mapping from labels to their block indices within a root
	 * block. This is useful so they can easily be resolved during the
	 * subsequent traversal of the block.
	 * 
	 * @param block
	 * @return
	 */
	public static Map<String, BytecodeForest.Index> buildLabelMap(BytecodeForest forest) {
		HashMap<String, BytecodeForest.Index> labels = new HashMap<String, BytecodeForest.Index>();
		for (int i = 0; i != forest.numBlocks(); ++i) {
			BytecodeForest.Block block = forest.get(i);
			for (int j = 0; j != block.size(); ++j) {
				Bytecode code = block.get(j).code();
				if (code instanceof Bytecode.Label) {
					// Found a label, so register it in the labels map
					Bytecode.Label label = (Bytecode.Label) code;
					labels.put(label.label(), new BytecodeForest.Index(i, j));
				}
			}
		}
		return labels;
	}
	
	private static String arrayToString(int... operands) {
		String r = "(";
		for (int i = 0; i != operands.length; ++i) {
			if (i != 0) {
				r = r + ", ";
			}
			r = r + "%" + operands[i];			
		}
		return r + ")";
	}

	private static int[] append(int operand, int[] operands) {
		int[] noperands = new int[operands.length + 1];
		System.arraycopy(operands, 0, noperands, 1, operands.length);
		noperands[0] = operand;
		return noperands;
	}

	// =========================================================================
	// Opcodes
	// =========================================================================
	
	public static final int OPCODE_goto      = 1;
	public static final int OPCODE_fail      = 2;
	public static final int OPCODE_assert    = 4;
	public static final int OPCODE_assume    = 5;
	public static final int OPCODE_invariant = 6;

	// Unary Operators
	public static final int UNARY_OPERATOR = 7;
	
	public static final int OPCODE_debug    = UNARY_OPERATOR+0;
	public static final int OPCODE_return   = UNARY_OPERATOR+1;
	public static final int OPCODE_ifis     = UNARY_OPERATOR+2;
	public static final int OPCODE_switch   = UNARY_OPERATOR+3;

	// Unary Assignables
	public static final int UNARY_ASSIGNABLE = UNARY_OPERATOR+5;
		
	public static final int OPCODE_fieldload   = UNARY_ASSIGNABLE+8;
	public static final int OPCODE_convert     = UNARY_ASSIGNABLE+9;
	public static final int OPCODE_const       = UNARY_ASSIGNABLE+10;
	
	// Binary Operators
	public static final int BINARY_OPERATOR = UNARY_ASSIGNABLE+11;
	
	public static final int OPCODE_if         = BINARY_OPERATOR+0;
	
	// Binary Assignables
	public static final int BINARY_ASSIGNABLE = BINARY_OPERATOR+6;
	
	public static final int OPCODE_neg         = BINARY_ASSIGNABLE+0;	
	public static final int OPCODE_add         = BINARY_ASSIGNABLE+1;
	public static final int OPCODE_sub         = BINARY_ASSIGNABLE+2;
	public static final int OPCODE_mul         = BINARY_ASSIGNABLE+3;
	public static final int OPCODE_div         = BINARY_ASSIGNABLE+4;
	public static final int OPCODE_rem         = BINARY_ASSIGNABLE+5;
	public static final int OPCODE_eq          = BINARY_ASSIGNABLE+6;
	public static final int OPCODE_ne          = BINARY_ASSIGNABLE+7;
	public static final int OPCODE_lt          = BINARY_ASSIGNABLE+8;
	public static final int OPCODE_le          = BINARY_ASSIGNABLE+9;
	public static final int OPCODE_gt          = BINARY_ASSIGNABLE+10;
	public static final int OPCODE_ge          = BINARY_ASSIGNABLE+11;
	
	public static final int OPCODE_logicalnot  = BINARY_ASSIGNABLE+12;
	public static final int OPCODE_logicaland  = BINARY_ASSIGNABLE+13;
	public static final int OPCODE_logicalor   = BINARY_ASSIGNABLE+14;
	
	public static final int OPCODE_bitwiseinvert = BINARY_ASSIGNABLE+15;
	public static final int OPCODE_bitwiseor   = BINARY_ASSIGNABLE+16;
	public static final int OPCODE_bitwisexor  = BINARY_ASSIGNABLE+17;
	public static final int OPCODE_bitwiseand  = BINARY_ASSIGNABLE+18;
	public static final int OPCODE_shl         = BINARY_ASSIGNABLE+19;
	public static final int OPCODE_shr         = BINARY_ASSIGNABLE+20;
	
	public static final int OPCODE_arraylength = BINARY_ASSIGNABLE+21;	
	public static final int OPCODE_arrayindex  = BINARY_ASSIGNABLE+22;	
	public static final int OPCODE_arraygen    = BINARY_ASSIGNABLE+23;
	public static final int OPCODE_array       = BINARY_ASSIGNABLE+24;
	public static final int OPCODE_record      = BINARY_ASSIGNABLE+25;
	public static final int OPCODE_is          = BINARY_ASSIGNABLE+26;
	
	public static final int OPCODE_dereference = BINARY_ASSIGNABLE+27;
	public static final int OPCODE_newobject   = BINARY_ASSIGNABLE+28;
	public static final int OPCODE_assign      = BINARY_ASSIGNABLE+29;
	
	// Nary Assignables
	public static final int NARY_ASSIGNABLE = BINARY_ASSIGNABLE+30;
		
	public static final int OPCODE_invoke           = NARY_ASSIGNABLE+2;
	public static final int OPCODE_indirectinvoke   = NARY_ASSIGNABLE+3;
	public static final int OPCODE_lambda           = NARY_ASSIGNABLE+4;
	public static final int OPCODE_loop             = NARY_ASSIGNABLE+5;	
	public static final int OPCODE_quantify         = NARY_ASSIGNABLE+6;
	public static final int OPCODE_update           = NARY_ASSIGNABLE+7;	
		
	// =========================================================================
	// Bytecode Schemas
	// =========================================================================

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
		BLOCK_ARRAY, // block index
		STRING_ARRAY, // determined on the fly
		SWITCH_ARRAY, // determined on the fly
	}

	public static abstract class Schema {
		private final Operands operands;
		private final Types types;
		private final Extras[] extras;

		public Schema( Operands operands, Types types, Extras... extras) {
			this.operands = operands;
			this.types = types;
			this.extras = extras;
		}
	
		public Extras[] extras() {
			return extras;
		}
		
		public abstract Bytecode construct(int opcode, int[] operands, Type[] types, Object[] extras);

		public String toString() {
			return "<" + operands + " operands, " + types + " types, " + Arrays.toString(extras) + ">";
		}
	}
}
