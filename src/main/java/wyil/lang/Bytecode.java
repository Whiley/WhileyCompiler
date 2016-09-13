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

import wyil.util.AbstractBytecode;
import wybs.lang.NameID;

/**
 * <p>
 * Each bytecode has a binary format which identifies the <i>opcode</i>,
 * <i>operand registers</i>, <i>operand groups</i>, <i>blocks</code> and
 * <i>other items</i> used (e.g. names, constants, etc). The generic
 * organisation of a bytecode is as follows:
 * </p>
 *
 * <pre>
 * +--------+----------+----------------+--------+-------------+
 * | opcode | operands | operand groups | blocks | other items |
 * +--------+----------+----------------+--------+-------------+
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
 * </p>
 * 
 * @author David J. Pearce
 */
public interface Bytecode {
	
	/**
	 * Return the top-level operands in this bytecode.
	 *
	 * @return
	 */
	public int[] getOperands();

	/**
	 * Get the number of operands in this bytecode
	 * 
	 * @return
	 */
	public int numberOfOperands();
	
	/**
	 * Return the ith top-level operand in this bytecode.
	 * 
	 * @param i
	 * @return
	 */
	public int getOperand(int i);

	/**
	 * Get the number of operand groups in this bytecode
	 * 
	 * @return
	 */
	public int numberOfOperandGroups();

	/**
	 * Get the ith operand group in this bytecode
	 * 
	 * @param i
	 * @return
	 */
	public int[] getOperandGroup(int i);

	/**
	 * Return the opcode value of this bytecode.
	 * 
	 * @return
	 */
	public int getOpcode();
	
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
	public interface Expr extends Bytecode {
		
	}

	/**
	 * <p>
	 * A convert bytecode has the following layout:
	 * </p>
	 * 
	 * <pre>
	 * +--------+---------+
	 * | opcode | operand |
	 * +--------+---------+
	 * </pre>
	 *
	 * <p>
	 * This corresponds to an explicit or implicit cast in the source language.
	 * This bytecode is the only way to change the type of a value. It's purpose
	 * is to simplify implementations which have different representations of
	 * data types. A convert bytecode must be inserted whenever the type of a
	 * value changes. For example, when a variable is retyped using the
	 * <code>is</code> operator, we must convert its value into a value of the
	 * new type.
	 * </p>
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
	public static final class Convert extends AbstractBytecode implements Expr {
		public Convert(int operand) {
			super(operand);
		}

		public int operand() {
			return getOperand(0);
		}

		public int getOpcode() {
			return OPCODE_convert;
		}

		@Override
		public String toString() {
			return "castt " + Util.arrayToString(getOperands());
		}
	}

	/**
	 * <p>
	 * A constant bytecode has the following layout:
	 * </p>
	 * 
	 * <pre>
	 * +--------+----------+
	 * | opcode | constant |
	 * +--------+----------+
	 * </pre>
	 * 
	 * Here, constant represents a value, such as an <i>integer</i>,
	 * <i>array</i>, etc.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Const extends AbstractBytecode implements Expr {
		private final Constant constant;

		public Const(Constant constant) {
			this.constant = constant;
		}

		public int getOpcode() {
			return OPCODE_const;
		}

		public Constant constant() {
			return constant;
		}

		@Override
		public int hashCode() {
			return constant.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Const) {
				Const c = (Const) o;
				return constant.equals(c.constant);
			}
			return false;
		}

		@Override
		public String toString() {
			return "const " + constant.toString();
		}
	}

	/**
	 * <p>
	 * A field load bytecode has the following layout:
	 * </p>
	 * 
	 * <pre>
	 * +--------+---------+-----------+
	 * | opcode | operand | fieldName |
	 * +--------+---------+-----------+
	 * </pre>
	 * 
	 * <p>
	 * The bytecode reads the field of the given name out of the record value
	 * returned by the operand
	 * </p>
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static final class FieldLoad extends AbstractBytecode implements Expr {
		private final String field;

		public FieldLoad(int operand, String field) {
			super(operand);
			if (field == null) {
				throw new IllegalArgumentException("FieldLoad field argument cannot be null");
			}
			this.field = field;
		}

		public int getOpcode() {
			return OPCODE_fieldload;
		}

		public int operand() {
			return getOperand(0);
		}

		public String fieldName() {
			return field;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof FieldLoad) {
				FieldLoad i = (FieldLoad) o;
				return super.equals(i) && field.equals(i.field);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return super.hashCode() + field.hashCode();
		}

		@Override
		public String toString() {
			return "recfield %" + getOperand(0) + " " + field;
		}
	}

	/**
	 * <p>
	 * A lambda bytecode has the following layout:
	 * </p>
	 * 
	 * <pre>
	 * +--------+------+-------------+---------------+
	 * | opcode | body | parameter[] | environment[] |
	 * +--------+------+-------------+---------------+
	 * </pre>
	 * 
	 * <p>
	 * Here, the body operand identifies an expression which constitutes the
	 * body of this lambda. The parameters are those variables which are
	 * declared by the lambda itself for use within the lambda body. The
	 * environment identifies those variables from the surrounding environment.
	 * </p>
	 */
	public static final class Lambda extends AbstractBytecode implements Expr {
		private final Type.FunctionOrMethod type;

		/**
		 * Create a new lambda bytecode
		 * 
		 * @param type
		 *            The type of the resulting lambda.
		 * @param body
		 *            The expression corresponding to the body of the lambda
		 *            expression.
		 * @param parameters
		 *            The set of declared variables using within the lambda
		 *            expression.
		 * @param environment
		 *            The set of variables from the enclosing scope which are
		 *            used within the lambda body.
		 */
		public Lambda(Type.FunctionOrMethod type, int body, int[] parameters, int[] environment) {
			super(body, new int[][]{parameters, environment});
			this.type = type;
		}

		public int getOpcode() {
			return OPCODE_lambda;
		}

		public Type.FunctionOrMethod type() {
			return type;
		}

		public int body() {
			return getOperand(0);
		};

		public int[] parameters() {
			return getOperandGroup(0);
		}

		public int[] environment() {
			return getOperandGroup(1);
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Lambda) {
				Lambda i = (Lambda) o;
				return type.equals(i.type) && super.equals(i);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return type.hashCode() + super.hashCode();
		}

		@Override
		public String toString() {
			return "lambda " + Util.arrayToString(getOperands()) + " " + type;
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
				return "arrlen";
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
				return "neq";
			}
		},
		LT(OPCODE_lt) {
			public String toString() {
				return "lt";
			}
		},
		LTEQ(OPCODE_le) {
			public String toString() {
				return "lteq";
			}
		},
		GT(OPCODE_gt) {
			public String toString() {
				return "gt";
			}
		},
		GTEQ(OPCODE_ge) {
			public String toString() {
				return "gteq";
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
				return "bshl";
			}
		},
		RIGHTSHIFT(OPCODE_shr) {
			public String toString() {
				return "bshr";
			}
		},
		ARRAYINDEX(OPCODE_arrayindex) {
			public String toString() {
				return "arridx";
			}
		},
		ARRAYGENERATOR(OPCODE_arraygen) {
			public String toString() {
				return "arrgen";
			}
		},
		ARRAYCONSTRUCTOR(OPCODE_array) {
			public String toString() {
				return "arrinit";
			}
		},
		RECORDCONSTRUCTOR(OPCODE_record) {
			public String toString() {
				return "recinit";
			}
		},
		IS(OPCODE_is) {
			public String toString() {
				return "istype";
			}
		},
		NEW(OPCODE_newobject) {
			public String toString() {
				return "new";
			}
		};
		public int opcode;

		private OperatorKind(int offset) {
			this.opcode = offset;
		}
	};

	/**
	 * <p>
	 * An operator bytecode has the following layout:
	 * </p>
	 * 
	 * <pre>
	 * +--------+-----------+
	 * | opcode | operand[] |
	 * +--------+-----------+
	 * </pre>
	 * 
	 * <p>
	 * Here, the operand array identifies one or more operands in which this
	 * operator operators.  The operator produces exactly one value.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Operator extends AbstractBytecode implements Expr {
		private final OperatorKind kind;

		public Operator(int[] operands, OperatorKind bop) {
			super(operands);
			this.kind = bop;
		}

		@Override
		public int getOpcode() {
			return kind().opcode;
		}

		public OperatorKind kind() {
			return kind;
		}

		public String toString() {
			return kind() + " " + Util.arrayToString(getOperands());
		}
	}

	public enum QuantifierKind {
		SOME(OPCODE_some), ALL(OPCODE_all);
		public int opcode;

		private QuantifierKind(int offset) {
			this.opcode = offset;
		}
	}

	/**
	 * <p>
	 * A quantifier bytecode has the following layout:
	 * </p>
	 * 
	 * <pre>
	 * +--------+-----------+---------+-----+---------+
	 * | opcode | condition | range[] | ... | range[] |
	 * +--------+-----------+---------+-----+---------+
	 * </pre>
	 * 
	 * <p>
	 * Here, the condition operand identifies an expression which the quantifier
	 * is asserting over the given ranges. The ranges themselves identifier one
	 * or more operand groups, each of which holds three entries: the declared
	 * variable, the start operand, the end operand.
	 * </p>
	 */
	public static final class Quantifier extends AbstractBytecode implements Expr {
		private final QuantifierKind kind;

		public Quantifier(QuantifierKind kind, int operand, Range... ranges) {
			super(operand, extract(ranges));
			this.kind = kind;
		}

		public QuantifierKind kind() {
			return kind;
		}
		
		public int getOpcode() {
			return kind.opcode;
		}

		public int body() {
			return getOperand(0);
		}

		public Range[] ranges() {
			Bytecode.Range[] ranges = new Bytecode.Range[numberOfOperandGroups()];
			for (int i = 0; i != ranges.length; i = i + 1) {
				int[] group = getOperandGroup(i);
				ranges[i] = new Bytecode.Range(group[0], group[1], group[2]);
			}
			return ranges;
		}

		private static int[][] extract(Range[] ranges) {
			int[][] groups = new int[ranges.length][3];
			for (int i = 0; i != ranges.length; ++i) {
				Range r = ranges[i];
				groups[i][0] = r.variable;
				groups[i][1] = r.startOperand;
				groups[i][2] = r.endOperand;
			}
			return groups;
		}

		@Override
		public String toString() {
			return "quantifier";
		}
	}

	public static final class Range {
		private final int variable;
		private final int startOperand;
		private final int endOperand;

		public Range(int variable, int startOperand, int endOperand) {
			this.variable = variable;
			this.startOperand = startOperand;
			this.endOperand = endOperand;
		}

		/**
		 * Return the location index for the variable this range is declaring.
		 * 
		 * @return
		 */
		public int variable() {
			return variable;
		}

		/**
		 * Return the start operand of this range.
		 * 
		 * @return
		 */
		public int startOperand() {
			return startOperand;
		}

		/**
		 * Return the end operand of this range.
		 * 
		 * @return
		 */
		public int endOperand() {
			return endOperand;
		}

		public boolean equals(Object o) {
			if (o instanceof Range) {
				Range r = (Range) o;
				return variable == r.variable && startOperand == r.startOperand && r.endOperand == endOperand;
			}
			return false;
		}

		public int hashCode() {
			return variable ^ startOperand ^ endOperand;
		}
	}


	/**
	 * <p>
	 * A variable access bytecode represents a specific read of a given variable.
	 * </p>
	 *
	 * <pre>
	 * +--------+---------+
	 * | opcode | operand |
	 * +--------+---------+
	 * </pre>
	 *
	 * <p>
	 * Here, the operand refers to the variable declaration corresponding to
	 * this variable Access.
	 * </p>
	 */
	public static final class VariableAccess extends AbstractBytecode implements Expr {

		public VariableAccess(int operand) {
			super(operand);
		}

		@Override
		public int getOpcode() {
			return OPCODE_varaccess;
		}

		public String toString() {
			return "read " + Util.arrayToString(getOperands());
		}
	}

	// ===============================================================
	// Bytecode Statements
	// ===============================================================

	/**
	 * A statement bytecode represents a bytecode that contains a sequence of
	 * zero or more bytecodes. For example, a loop bytecode contains its loop
	 * body. The nested blocks of bytecodes are represented as a block
	 * identifier in the enclosing forest.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public interface Stmt extends Bytecode {

		/**
		 * Determine the number of blocks contained in this bytecode.
		 * 
		 * @return
		 */
		public int numberOfBlocks();

		/**
		 * Get the ith block contained in this statement
		 * 
		 * @param i
		 * @return
		 */
		public int getBlock(int i);

		/**
		 * Get the blocks contained in this statement
		 * 
		 * @param i
		 * @return
		 */
		public int[] getBlocks();
	}

	/**
	 * <p>
	 * An alias declaration bytecode has the following form:
	 * </p>
	 *
	 * <pre>
	 * +--------+---------+
	 * | opcode | operand |
	 * +--------+---------+
	 * </pre>
	 * 
	 * <p>
	 * Here, the operand identifies the variable declaration being aliased
	 * (which may be an alias declaration itself).
	 * </p>
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static final class AliasDeclaration extends AbstractBytecode implements Stmt {

		public AliasDeclaration(int initialiser) {
			super(initialiser);
		}


		@Override
		public int getOpcode() {			
			return OPCODE_aliasdecl;
		}
		
		public String toString() {
			return "alias (%" + getOperand(0) + ")";
		}
	}
	
	/**
	 * An abstract class representing either an <code>assert</code> or
	 * <code>assume</code> bytecode.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static abstract class AssertOrAssume extends AbstractBytecode implements Stmt {
		private AssertOrAssume(int operand) {
			super(operand);
		}

		public int operand() {
			return getOperand(0);
		}
	}

	/**
	 * <p>
	 * An assert bytecode has the following layout:
	 * </p>
	 * 
	 * <pre>
	 * +--------+-----------+
	 * | opcode | condition |
	 * +--------+-----------+
	 * </pre>
	 * 
	 * <p>
	 * Here, the condition identifies an operand which should always evaluate to
	 * true. This condition should be enforced at compile time.
	 * </p>
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static class Assert extends AssertOrAssume {

		public Assert(int operand) {
			super(operand);
		}

		public int getOpcode() {
			return OPCODE_assert;
		}

		@Override
		public String toString() {
			return "assert %" + getOperand(0);
		}

	}

	/**
	 * <p>
	 * An assignment bytecode has the following layout:
	 * </p>
	 * 
	 * <pre>
	 * +--------+----------------+-----------------+
	 * | opcode | leftHandSide[] | rightHandSide[] |
	 * +--------+----------------+-----------------+
	 * </pre>
	 * 
	 * <p>
	 * Here, the left-hand side identifies zero or more operands which are being
	 * assigned to, whilst the right-hand side identifies one or more operands
	 * which whose results are being assigned. The left-hand side may have fewer
	 * operands than the right-hand side. This happens, for example, in the case
	 * of an invocation where the result is ignored.
	 * </p>
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static final class Assign extends AbstractBytecode implements Stmt {
		/**
		 * Construct an assignment from a right-hand operand to a left-hand
		 * operand.
		 *
		 * @param lhs
		 *            LVal on left-hand side which is assigned to
		 * @param rhs
		 *            Operand on right-hand side whose value is assigned
		 */
		public Assign(int lhs, int rhs) {
			this(new int[] { lhs }, new int[] { rhs });
		}

		/**
		 * Construct an assignment from a right-hand operand to a left-hand
		 * operand.
		 *
		 * @param lhs
		 *            LVal on left-hand side which is assigned to
		 * @param rhs
		 *            Operand on right-hand side whose value is assigned
		 */
		public Assign(int[] lhs, int[] rhs) {
			super(new int[][] { lhs, rhs });
		}

		public int getOpcode() {
			return OPCODE_assign;
		}

		/**
		 * Returns operand(s) from which assigned value is written to. This is
		 * also known as the "left-hand side".
		 *
		 * @return
		 */
		public int[] leftHandSide() {
			return getOperandGroup(0);
		}

		/**
		 * Returns operand(s) from which assigned value is read. This is also
		 * known as the "right-hand side".
		 *
		 * @return
		 */
		public int[] rightHandSide() {
			return getOperandGroup(1);
		}

		@Override
		public String toString() {
			return "assign " + Util.arrayToString(leftHandSide()) + " = " + Util.arrayToString(rightHandSide());
		}
	}

	/**
	 * <p>
	 * An assume bytecode has the following layout:
	 * </p>
	 * 
	 * <pre>
	 * +--------+-----------+
	 * | opcode | condition |
	 * +--------+-----------+
	 * </pre>
	 * 
	 * <p>
	 * Here, the condition identifies an operand which is assumed to always
	 * evaluate to true.  This assumption should be tested at runtime.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Assume extends AssertOrAssume {

		public Assume(int operand) {
			super(operand);
		}

		public int getOpcode() {
			return OPCODE_assume;
		}

		@Override
		public String toString() {
			return "assume %" + operand();
		}
	}

	/**
	 * <p>
	 * A break bytecode has the following layout:
	 * </p>
	 * 
	 * <pre>
	 * +--------+
	 * | opcode |
	 * +--------+
	 * </pre>
	 * 
	 * <p>
	 * Here, control will immediately exit the enclosing loop upon executing
	 * this bytecode.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Break extends AbstractBytecode implements Stmt {
		
		public Break() {
			super(new int[0]);
		}

		public int getOpcode() {
			return OPCODE_break;
		}		
		
		@Override
		public String toString() {
			return "break ";
		}
	}

	/**
	 * <p>
	 * A continue bytecode has the following layout:
	 * </p>
	 * 
	 * <pre>
	 * +--------+
	 * | opcode |
	 * +--------+
	 * </pre>
	 * 
	 * <p>
	 * Here, control will immediately complete the enclosing loop body upon
	 * executing this bytecode.
	 * </p>
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static final class Continue extends AbstractBytecode implements Stmt {

		public Continue() {
			super(new int[]{});
		}

		public int getOpcode() {
			return OPCODE_continue;
		}

		@Override
		public String toString() {
			return "cont";
		}
	}

	/**
	 * <p>
	 * A debug bytecode has the following layout:
	 * </p>
	 * 
	 * <pre>
	 * +--------+--------+
	 * | opcode | string |
	 * +--------+--------+
	 * </pre>
	 * 
	 * <p>
	 * Here, the string identifies an operand returning a string result which
	 * will be printed to the debug console.
	 * </p>
	 * 
	 * <b>NOTE</b> This bytecode is not intended to form part of the program's
	 * operation. Rather, it is to facilitate debugging within functions (since
	 * they cannot have side-effects). Furthermore, if debugging is disabled,
	 * this bytecode is a nop.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Debug extends AbstractBytecode implements Stmt {

		public Debug(int operand) {
			super(operand);
		}

		public int getOpcode() {
			return OPCODE_debug;
		}
		
		@Override
		public String toString() {
			return "debug %" + getOperand(0);
		}
	}

	/**
	 * A Do While bytecode has the same format as the underlying loop bytecode.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class DoWhile extends Loop {
		public DoWhile(int body, int condition, int[] invariants, int[] modified) {
			super(body, condition, invariants, modified);
		}

		public int getOpcode() {
			return OPCODE_dowhile;
		}

		@Override
		public String toString() {
			return "dowhile";
		}
	}

	/**
	 * <p>
	 * A panic bytecode has the following layout:
	 * </p>
	 * 
	 * <pre>
	 * +--------+
	 * | opcode |
	 * +--------+
	 * </pre>
	 * 
	 * <p>
	 * Upon execution of this bytecode, the machine will halt immediately and
	 * indicate an unrecoverable error. At this time, there is no way to recover
	 * from a panic, though this may be supported in the future.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Fail extends AbstractBytecode implements Stmt {

		// FIXME: should be renamed to panic as this is a more descriptive name

		@Override
		public int getOpcode() {
			return OPCODE_fail;
		}

		@Override
		public String toString() {
			return "fail";
		}
	}

	/**
	 * <p>
	 * An if bytecode has one of the the following layouts:
	 * </p>
	 * 
	 * <pre>
	 * +--------+-----------+------------+
	 * | opcode | condition | trueBranch |
	 * +--------+-----------+------------+
	 * </pre>
	 * 
	 * <pre>
	 * +--------+-----------+------------+-------------+
	 * | opcode | condition | trueBranch | falseBranch |
	 * +--------+-----------+------------+-------------+
	 * </pre>
	 * 
	 * <p>
	 * Here, the condition identifies the condition which determines whether the
	 * true branch is taken or not. If not, and there is a false branch, then
	 * that is executed. Otherwise, control proceeds to the next logical
	 * statement in the enclosing block.
	 * </p>
	 */
	 public static final class If extends AbstractBytecode implements Stmt {
		public If(int operand, int trueBranch) {
			super(operand, null, new int[] {trueBranch});			
		}

		public If(int operand, int trueBranch, int falseBranch) {
			super(operand, null, new int[]{trueBranch, falseBranch});
		}

		public int getOpcode() {
			return numberOfBlocks() == 1 ? OPCODE_if : OPCODE_ifelse;
		}

		public int condition() {
			return getOperand(0);
		}

		/**
		 * Check whether this bytecode has a false branch of not.
		 * 
		 * @return
		 */
		public boolean hasFalseBranch() {
			return numberOfBlocks() > 1;
		}

		/**
		 * Return the block identifier for the true branch associated with this
		 * bytecode.
		 * 
		 * @return
		 */
		public int trueBranch() {
			return getBlock(0);
		}

		/**
		 * Return the block identifier for the false branch associated with this
		 * bytecode.
		 * 
		 * @return
		 */
		public int falseBranch() {
			return getBlock(1);
		}

		@Override
		public String toString() {
			int c = getOperand(0);
			int tb = trueBranch();
			if (hasFalseBranch()) {
				int fb = falseBranch();
				return "if" + " (%" + c + ", %" + tb + ", %" + fb + ")";
			} else {
				return "if" + " (%" + c + ", %" + tb + ")";
			}
		}
	}

	/**
	 * <p>
	 * A loop bytecode has the following layout:
	 * </p>
	 * 
	 * <pre>
	 * +--------+-----------+--------------+------------+------+
	 * | opcode | condition | invariants[] | modified[] | body |
	 * +--------+-----------+--------------+------------+------+
	 * </pre>
	 * 
	 * <p>
	 * Here, the condition identifies the loop condition which, when false, will
	 * cause the loop to terminate. The invariants identifies zero or more
	 * operands which must be true on every iteration of the loop. The modified
	 * variables are those variables which are, in some sense, modified in the
	 * body of the loop. The body identifies a block which forms the body of the
	 * loop.
	 * </p>
	 */
	public static abstract class Loop extends AbstractBytecode implements Stmt {
		public Loop(int body, int condition, int[] invariants, int[] modified) {
			super(condition, new int[][]{invariants,modified}, new int[]{body});			
		}

		/**
		 * Return the block identifier of the loop body.
		 * 
		 * @return
		 */
		public int body() {
			return getBlock(0);
		}

		/**
		 * Return the loop condition operand. This must be true for iteration to
		 * continue around the loop.
		 * 
		 * @return
		 */
		public int condition() {
			return getOperand(0);
		}

		/**
		 * Return the array of operands making up the loop invariant. Each of
		 * these corresponds to a "where" clause in the original program.
		 * 
		 * @return
		 */
		public int[] invariants() {
			return getOperandGroup(0);
		}

		/**
		 * Return the array of modified variables which are those assigned (in
		 * some way) in the body of the loop. These cannot be operands, they
		 * must be variables. These are needed for verification.
		 */
		public int[] modifiedVariables() {
			return getOperandGroup(1);
		}		
	}

	/**
	 * A While bytecode has the same format as the underlying loop bytecode. The
	 * loop invariant must hold on entry to the loop, and will hold on normal
	 * exit (though not necessarily for a break exit).
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class While extends Loop {
		public While(int body, int condition, int[] invariants, int[] modified) {
			super(body, condition, invariants, modified);
		}

		@Override
		public int getOpcode() {
			return OPCODE_while;
		}

		@Override
		public String toString() {
			return "while " + condition() + " do " + getBlock(0);
		}
	}

	/**
	 * <p>
	 * A return bytecode has the following layout:
	 * </p>
	 * 
	 * <pre>
	 * +--------+------------+
	 * | opcode | operands[] |
	 * +--------+------------+
	 * </pre>
	 * 
	 * <p>
	 * Here, there are zero or more operands which can be returned.
	 * </p>
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static final class Return extends AbstractBytecode implements Stmt {

		public Return() {
		}

		public Return(int... operands) {
			super(operands);
		}

		@Override
		public int getOpcode() {
			return OPCODE_return;
		}

		@Override
		public String toString() {			
			return "return " + Util.arrayToString(getOperands());			
		}
	}

	/**
	 * <p>
	 * A skip bytecode has the following layout:
	 * </p>
	 * 
	 * <pre>
	 * +--------+
	 * | opcode |
	 * +--------+
	 * </pre>
	 * 
	 * <p>
	 * Upon execution of this bytecode, the machine simply moves on to the next
	 * instruction.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Skip extends AbstractBytecode implements Stmt {

		// FIXME: should be renamed to panic as this is a more descriptive name

		@Override
		public int getOpcode() {
			return OPCODE_skip;
		}

		@Override
		public String toString() {
			return "skip";
		}
	}
	/**
	 * <p>
	 * A switch bytecode has the following layout:
	 * </p>
	 * 
	 * <pre>
	 * +--------+-----------+-------+-----+-------+------------+-----+------------+
	 * | opcode | condition | block | ... | block | Constant[] | ... | Constant[] |
	 * +--------+-----------+-------+-----+-------+------------+-----+------------+
	 * </pre>
	 * 
	 * <p>
	 * Here, the condition identifies the expression being switched on. There
	 * are zero or more case blocks, and the same number of constant arrays. An
	 * empty constant array indicates the default block.
	 * </p>
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static final class Switch extends AbstractBytecode implements Stmt {
		private final Constant[][] constants;

		public Switch(int operand, Case[] cases) {
			super(operand, null, extractBlocks(cases));
			this.constants = extractConstants(cases);
		}

		@Override
		public int getOpcode() {
			return OPCODE_switch;
		}

		public int operand() {
			return getOperand(0);
		}

		public Case[] cases() {
			Case[] cases = new Case[numberOfBlocks()];
			for (int i = 0; i != cases.length; ++i) {
				cases[i] = new Case(getBlock(i), constants[i]);
			}
			return cases;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Switch) {
				Switch s = (Switch) o;
				return Arrays.deepEquals(constants, s.constants) && super.equals(s);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(constants) ^ super.hashCode();
		}

		@Override
		public String toString() {
			return "switch";
		}
		
		private static int[] extractBlocks(Case[] cases) {
			int[] blocks = new int[cases.length];
			for(int i=0;i!=cases.length;++i) {
				blocks[i] = cases[i].block;
			}
			return blocks;
		}
		
		private static Constant[][] extractConstants(Case[] cases) {
			Constant[][] blocks = new Constant[cases.length][];
			for (int i = 0; i != cases.length; ++i) {
				blocks[i] = cases[i].values;
			}
			return blocks;
		}
	}

	public static final class Case {
		private final Constant[] values;
		private final int block;

		public Case(int block, Constant... values) {
			this.block = block;
			this.values = values;
		}

		public Case(int block, List<Constant> values) {
			this.block = block;
			this.values = values.toArray(new Constant[values.size()]);
		}

		public boolean isDefault() {
			return values.length == 0;
		}

		public int block() {
			return block;
		}

		public Constant[] values() {
			return values;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Case) {
				Case c = (Case) o;
				return block == c.block && Arrays.equals(values, c.values);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return block ^ Arrays.hashCode(values);
		}
	}

	/**
	 * <p>
	 * A variable declaration bytecode has one of the following two layouts:
	 * </p>
	 *
	 * <pre>
	 * +--------+------+
	 * | opcode | name |
	 * +--------+------+
	 * </pre>
	 * 
	 * Or, with an initialiser operand:
	 * 
	 * <pre>
	 * +--------+---------+------+
	 * | opcode | operand | name |
	 * +--------+---------+------+
	 * </pre>
	 * 
	 * <p>
	 * Here, the condition identifies the expression being switched on. There
	 * are zero or more case blocks, and the same number of constant arrays. An
	 * empty constant array indicates the default block.
	 * </p>
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static final class VariableDeclaration extends AbstractBytecode implements Stmt {
		/**
		 * Variable name
		 */
		private final String name;

		public VariableDeclaration(String name) {
			super();
			this.name = name;
		}

		public VariableDeclaration(String name, int initialiser) {
			super(initialiser);
			this.name = name;
		}

		public String getName() {
			return name;
		}

		@Override
		public int getOpcode() {
			if (numberOfOperands() == 0) {
				return OPCODE_vardecl;
			} else {
				return OPCODE_vardeclinit;
			}
		}
		
		public boolean equals(Object o) {
			if(o instanceof VariableDeclaration) {
				VariableDeclaration vd = (VariableDeclaration) o;
				return name.equals(vd.name) && super.equals(o);
			}
			return false;
		}
		
		public int hashCode() {
			return name.hashCode() ^ super.hashCode();
		}
		
		public String toString() {
			if(numberOfOperands() == 0) {
				return "decl " + name;
			} else {
				return "decl " + name + " = " + getOperand(0);
			}
		}
	}
		
	// ===============================================================
	// Bytecode Block & Index
	// ===============================================================

	public static class Block extends AbstractBytecode implements Bytecode {

		public Block(int... operands) {
			super(operands);
		}
		
		@Override
		public int getOpcode() {
			return OPCODE_block;
		}	
		
		public String toString() {
			return "block " + Util.arrayToString(getOperands());
		}
	}
	
	public static final class NamedBlock extends AbstractBytecode implements Bytecode.Stmt {
		private final String name;		
		
		public NamedBlock(int block, String name) {
			super(new int[0], new int[0][], new int[] { block });
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		@Override
		public int getOpcode() {
			return OPCODE_namedblock;
		}	
		
		@Override
		public boolean equals(Object o) {
			if(o instanceof NamedBlock) {
				NamedBlock n = (NamedBlock) o;
				return name.equals(n.name) && super.equals(o);
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return super.hashCode() ^ name.hashCode();
		}
		
		public String toString() {
			return "block(" + name +") " + Util.arrayToString(getBlocks());
		}
	}
	
	/**
	 * Represents a bytecode location within a code forest. This is simply a
	 * pair of the block identifier and the position within that block.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static final class Index {
		private int block;
		private int offset;

		public Index(int block, int offset) {
			this.block = block;
			this.offset = offset;
		}

		public int block() {
			return block;
		}

		public int offset() {
			return offset;
		}

		public boolean equals(Object o) {
			if (o instanceof Index) {
				Index i = (Index) o;
				return block == i.block && offset == i.offset;
			}
			return false;
		}

		public int hashCode() {
			return block ^ offset;
		}

		public Index next() {
			return new Index(block, offset + 1);
		}

		public Index next(int i) {
			return new Index(block, offset + i);
		}

		public String toString() {
			return block + ":" + offset;
		}
	}

	// ===============================================================
	// Bytecode "Statement Expressions"
	// ===============================================================

	/**
	 * A "statement expression" is a rather unusual beast. It is both a
	 * statement and an expression! There are very few bytecodes which can be
	 * classified in this way.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public interface StmtExpr extends Expr,Stmt {
	}
	

	/**
	 * <p>
	 * An indirect invocation bytecode has the following layout:
	 * </p>
	 * 
	 * <pre>
	 * +--------+---------+-------------+------+
	 * | opcode | operand | parameter[] | type |
	 * +--------+---------+-------------+------+
	 * </pre>
	 * 
	 * <p>
	 * Here, the operand returns the function pointer which this bytecode
	 * indirects upon. The parameter array identifies zero or more operands
	 * which are pass as arguments, whilst the type gives the signature of the
	 * target function/method. For example, consider the following:
	 * </p>
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
	public static final class IndirectInvoke extends AbstractBytecode implements StmtExpr {
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
			super(operand, new int[][]{operands});
			this.type = type;
		}

		/**
		 * Return operand holding the indirect function/method reference.
		 *
		 * @return
		 */
		public int reference() {
			return getOperand(0);
		}

		/**
		 * Return operand holding the ith parameter for the invoked function.
		 *
		 * @param i
		 * @return
		 */
		public int argument(int i) {
			return getOperandGroup(0)[i];
		}

		/**
		 * Return operands holding parameters for the invoked function.
		 *
		 * @return
		 */
		public int[] arguments() {
			return getOperandGroup(0);
		}

		public int getOpcode() {
			return OPCODE_indirectinvoke;
		}

		public Type.FunctionOrMethod type() {
			return type;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof IndirectInvoke) {
				IndirectInvoke i = (IndirectInvoke) o;
				return type.equals(i.type) && super.equals(o);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return super.hashCode() + type.hashCode();
		}

		@Override
		public String toString() {
			return "icall %" + reference() + " " + Util.arrayToString(arguments());
		}
	}

	/**
	 * <p>
	 * An indirect invocation bytecode has the following layout:
	 * </p>
	 * 
	 * <pre>
	 * +--------+-------------+------+------+
	 * | opcode | parameter[] | type | name |
	 * +--------+-------------+------+------+
	 * </pre>
	 * 
	 * <p>
	 * Here, the parameter array identifies zero or more operands which are pass
	 * as arguments, whilst the type gives the signature of the target
	 * function/method and name identifies it's fully qualitified name.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static final class Invoke extends AbstractBytecode implements StmtExpr {
		private final NameID name;
		private final Type.FunctionOrMethod type;

		public Invoke(Type.FunctionOrMethod type, int[] operands, NameID name) {
			super(operands);
			this.name = name;
			this.type = type;
		}

		public int getOpcode() {
			return OPCODE_invoke;
		}

		public NameID name() {
			return name;
		}

		public Type.FunctionOrMethod type() {
			return type;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof Invoke) {
				Invoke i = (Invoke) o;
				return name().equals(i.name) && super.equals(i);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return name.hashCode() + super.hashCode();
		}

		@Override
		public String toString() {
			return "call " + name + Util.arrayToString(getOperands());
		}
	}

	
	// ===============================================================
	// Helpers
	// ===============================================================

	public static final class Util {

		public static String arrayToString(int... operands) {
			String r = "(";
			for (int i = 0; i != operands.length; ++i) {
				if (i != 0) {
					r = r + ", ";
				}
				r = r + "%" + operands[i];
			}
			return r + ")";
		}
	}
	// =========================================================================
	// Opcodes
	// =========================================================================

	public static final int OPCODE_vardecl = 0;
	public static final int OPCODE_fail = 1;
	public static final int OPCODE_assert = 2;
	public static final int OPCODE_assume = 3;
	public static final int OPCODE_break = 4;
	public static final int OPCODE_continue = 5;
	public static final int OPCODE_vardeclinit = 6;
	public static final int OPCODE_aliasdecl = 7;
	
	// Unary Operators
	public static final int UNARY_OPERATOR = 8;


	public static final int OPCODE_return = UNARY_OPERATOR + 0;
	public static final int OPCODE_ifis = UNARY_OPERATOR + 1;
	public static final int OPCODE_switch = UNARY_OPERATOR + 2;
	public static final int OPCODE_skip = UNARY_OPERATOR + 3;
	public static final int OPCODE_debug = UNARY_OPERATOR + 4;
	// Unary Assignables
	public static final int UNARY_ASSIGNABLE = UNARY_OPERATOR + 5;

	public static final int OPCODE_fieldload = UNARY_ASSIGNABLE + 7;
	public static final int OPCODE_convert = UNARY_ASSIGNABLE + 8;
	public static final int OPCODE_const = UNARY_ASSIGNABLE + 9;

	// Binary Operators
	public static final int BINARY_OPERATOR = UNARY_ASSIGNABLE + 10;

	public static final int OPCODE_if = BINARY_OPERATOR + 0;
	public static final int OPCODE_ifelse = BINARY_OPERATOR + 1;

	// Binary Assignables
	public static final int BINARY_ASSIGNABLE = BINARY_OPERATOR + 6;

	public static final int OPCODE_neg = BINARY_ASSIGNABLE + 0;
	public static final int OPCODE_add = BINARY_ASSIGNABLE + 1;
	public static final int OPCODE_sub = BINARY_ASSIGNABLE + 2;
	public static final int OPCODE_mul = BINARY_ASSIGNABLE + 3;
	public static final int OPCODE_div = BINARY_ASSIGNABLE + 4;
	public static final int OPCODE_rem = BINARY_ASSIGNABLE + 5;
	public static final int OPCODE_eq = BINARY_ASSIGNABLE + 6;
	public static final int OPCODE_ne = BINARY_ASSIGNABLE + 7;
	public static final int OPCODE_lt = BINARY_ASSIGNABLE + 8;
	public static final int OPCODE_le = BINARY_ASSIGNABLE + 9;
	public static final int OPCODE_gt = BINARY_ASSIGNABLE + 10;
	public static final int OPCODE_ge = BINARY_ASSIGNABLE + 11;

	public static final int OPCODE_logicalnot = BINARY_ASSIGNABLE + 12;
	public static final int OPCODE_logicaland = BINARY_ASSIGNABLE + 13;
	public static final int OPCODE_logicalor = BINARY_ASSIGNABLE + 14;

	public static final int OPCODE_bitwiseinvert = BINARY_ASSIGNABLE + 15;
	public static final int OPCODE_bitwiseor = BINARY_ASSIGNABLE + 16;
	public static final int OPCODE_bitwisexor = BINARY_ASSIGNABLE + 17;
	public static final int OPCODE_bitwiseand = BINARY_ASSIGNABLE + 18;
	public static final int OPCODE_shl = BINARY_ASSIGNABLE + 19;
	public static final int OPCODE_shr = BINARY_ASSIGNABLE + 20;

	public static final int OPCODE_arraylength = BINARY_ASSIGNABLE + 21;
	public static final int OPCODE_arrayindex = BINARY_ASSIGNABLE + 22;
	public static final int OPCODE_arraygen = BINARY_ASSIGNABLE + 23;
	public static final int OPCODE_array = BINARY_ASSIGNABLE + 24;
	public static final int OPCODE_record = BINARY_ASSIGNABLE + 25;
	public static final int OPCODE_is = BINARY_ASSIGNABLE + 26;

	public static final int OPCODE_dereference = BINARY_ASSIGNABLE + 27;
	public static final int OPCODE_newobject = BINARY_ASSIGNABLE + 28;
	public static final int OPCODE_varaccess = BINARY_ASSIGNABLE + 29;
	
	// Nary Assignables
	public static final int NARY_ASSIGNABLE = BINARY_ASSIGNABLE + 30;

	public static final int OPCODE_invoke = NARY_ASSIGNABLE + 2;
	public static final int OPCODE_indirectinvoke = NARY_ASSIGNABLE + 3;
	public static final int OPCODE_lambda = NARY_ASSIGNABLE + 4;
	public static final int OPCODE_while = NARY_ASSIGNABLE + 5;
	public static final int OPCODE_dowhile = NARY_ASSIGNABLE + 6;
	public static final int OPCODE_some = NARY_ASSIGNABLE + 8;
	public static final int OPCODE_all = NARY_ASSIGNABLE + 9;
	public static final int OPCODE_assign = NARY_ASSIGNABLE + 10;
	public static final int OPCODE_block = NARY_ASSIGNABLE + 11;
	public static final int OPCODE_namedblock = NARY_ASSIGNABLE + 12;

	// =========================================================================
	// Bytecode Schemas
	// =========================================================================

	public enum Operands {
		ZERO, ONE, TWO, MANY
	}
	
	public enum OperandGroups {
		ZERO, ONE, TWO, MANY
	}
	
	public enum Blocks {
		ZERO, ONE, TWO, MANY
	}

	public enum Extras {
		STRING, // index into string pool
		CONSTANT, // index into constant pool
		TYPE, // index into type pool
		NAME, // index into name pool
		STRING_ARRAY, // determined on the fly
		SWITCH_ARRAY, // determined on the fly
	}

	public static abstract class Schema {
		private final Operands operands;
		private final OperandGroups groups;
		private final Blocks blocks;
		private final Extras[] extras;

		public Schema(Operands operands, Extras... extras) {
			this.operands = operands;
			this.groups = OperandGroups.ZERO;
			this.blocks = Blocks.ZERO;
			this.extras = extras;
		}

		public Schema(Operands operands, OperandGroups groups, Extras... extras) {
			this.operands = operands;
			this.groups = groups;
			this.blocks = Blocks.ZERO;
			this.extras = extras;
		}
		
		public Schema(Operands operands, OperandGroups groups, Blocks blocks, Extras... extras) {
			this.operands = operands;
			this.groups = groups;
			this.blocks = blocks;
			this.extras = extras;
		}
		
		public Extras[] extras() {
			return extras;
		}

		public Operands getOperands() {
			return operands;
		}
		
		public OperandGroups getOperandGroups() {
			return groups;
		}
		
		public Blocks getBlocks() {
			return blocks;
		}
		
		public abstract Bytecode construct(int opcode, int[] operands, int[][] groups, int[] blocks, Object[] extras);

		public String toString() {
			return "<" + operands + " operands, " + Arrays.toString(extras) + ">";
		}
	}
}
