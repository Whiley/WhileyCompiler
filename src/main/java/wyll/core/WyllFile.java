// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wyll.core;

import java.io.*;
import java.util.*;

import wybs.lang.CompilationUnit;
import wybs.lang.SyntacticItem;
import wybs.lang.SyntacticItem.Data;
import wybs.lang.SyntacticItem.Operands;
import wybs.lang.SyntacticItem.Schema;
import wybs.util.AbstractCompilationUnit;
import wybs.util.AbstractSyntacticItem;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Name;
import wybs.util.AbstractCompilationUnit.Ref;
import wybs.util.AbstractCompilationUnit.Tuple;
import wybs.util.AbstractCompilationUnit.Value;
import wyc.lang.WhileyFile;
import wyc.lang.WhileyFile.Decl;
import wyc.lang.WhileyFile.Expr;
import wyc.lang.WhileyFile.LVal;
import wyc.lang.WhileyFile.Modifier;
import wyc.lang.WhileyFile.Stmt;
import wyc.lang.WhileyFile.Type;
import wycc.util.ArrayUtils;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.lang.Path.Entry;
import wyil.io.WyilFileWriter;
import wyll.io.WyllFileReader;
import wyll.io.WyllFileWriter;


public class WyllFile extends AbstractCompilationUnit<WyllFile> {

	// =========================================================================
	// Source Content Type
	// =========================================================================

	public static final Content.Type<WyllFile> ContentType = new Content.Type<WyllFile>() {

		@Override
		public WyllFile read(Path.Entry<WyllFile> e, InputStream inputstream) throws IOException {
			return new WyllFileReader(e).read();
		}

		@Override
		public void write(OutputStream output, WyllFile value) throws IOException {
			new WyllFileWriter(output).write(value);
		}

		@Override
		public String toString() {
			return "Content-Type: wyll";
		}

		@Override
		public String getSuffix() {
			return "wyll";
		}
	};

	// DECLARATIONS: 00010000 (16) -- 00011111 (31)
	public static final int DECL_mask = 0b00010000;
	public static final int DECL_module = DECL_mask + 0;
	public static final int DECL_staticvar = DECL_mask + 3;
	public static final int DECL_type = DECL_mask + 4;
	public static final int DECL_rectype = DECL_mask + 5;
	public static final int DECL_method = DECL_mask + 6;
	public static final int DECL_variable = DECL_mask + 9;
	public static final int DECL_variableinitialiser = DECL_mask + 10;

	public static final int MOD_native = DECL_mask + 11;
	public static final int MOD_export = DECL_mask + 12;
	public static final int MOD_protected = DECL_mask + 13;
	public static final int MOD_private = DECL_mask + 14;
	public static final int MOD_public = DECL_mask + 15;
	// TYPES: 00100000 (32) -- 00111111 (63)
	public static final int TYPE_mask = 0b000100000;
	public static final int TYPE_void = TYPE_mask + 0;
	public static final int TYPE_null = TYPE_mask + 2;
	public static final int TYPE_bool = TYPE_mask + 3;
	public static final int TYPE_int = TYPE_mask + 4;
	public static final int TYPE_nominal = TYPE_mask + 6;
	public static final int TYPE_reference = TYPE_mask + 7;
	public static final int TYPE_array = TYPE_mask + 9;
	public static final int TYPE_record = TYPE_mask + 10;
	public static final int TYPE_method = TYPE_mask + 12;
	public static final int TYPE_union = TYPE_mask + 15;
	// STATEMENTS: 01000000 (64) -- 001011111 (95)
	public static final int STMT_mask = 0b01000000;
	public static final int STMT_block = STMT_mask + 0;
	public static final int STMT_caseblock = STMT_mask + 2;
	public static final int STMT_assert = STMT_mask + 3;
	public static final int STMT_assign = STMT_mask + 4;
	public static final int STMT_break = STMT_mask + 8;
	public static final int STMT_continue = STMT_mask + 9;
	public static final int STMT_dowhile = STMT_mask + 10;
	public static final int STMT_fail = STMT_mask + 11;
	public static final int STMT_for = STMT_mask + 12;
	public static final int STMT_foreach = STMT_mask + 13;
	public static final int STMT_if = STMT_mask + 14;
	public static final int STMT_ifelse = STMT_mask + 15;
	public static final int STMT_return = STMT_mask + 16;
	public static final int STMT_switch = STMT_mask + 17;
	public static final int STMT_while = STMT_mask + 18;
	// EXPRESSIONS: 01100000 (96) -- 10011111 (159)
	public static final int EXPR_mask = 0b01100000;
	public static final int EXPR_variableaccess = EXPR_mask + 0;
	public static final int EXPR_staticvariable = EXPR_mask + 1;
	public static final int EXPR_nullconstant = EXPR_mask + 2;
	public static final int EXPR_boolconstant = EXPR_mask + 3;
	public static final int EXPR_intconstant = EXPR_mask + 4;
	public static final int EXPR_clone = EXPR_mask + 5;
	public static final int EXPR_invoke = EXPR_mask + 6;
	public static final int EXPR_indirectinvoke = EXPR_mask + 7;
	// LOGICAL
	public static final int EXPR_logicalnot = EXPR_mask + 9;
	public static final int EXPR_logicaland = EXPR_mask + 10;
	public static final int EXPR_logicalor = EXPR_mask + 11;
	// UNIONS
	public static final int EXPR_unionaccess = EXPR_mask + 12;
	public static final int EXPR_unionenter = EXPR_mask + 13;
	public static final int EXPR_unionleave = EXPR_mask + 14;
	// COMPARATORS
	public static final int EXPR_equal = EXPR_mask + 16;
	public static final int EXPR_notequal = EXPR_mask + 17;
	public static final int EXPR_integerlessthan = EXPR_mask + 18;
	public static final int EXPR_integerlessequal = EXPR_mask + 19;
	public static final int EXPR_integergreaterthan = EXPR_mask + 20;
	public static final int EXPR_integergreaterequal = EXPR_mask + 21;
	// ARITHMETIC
	public static final int EXPR_integernegation = EXPR_mask + 24;
	public static final int EXPR_integeraddition = EXPR_mask + 25;
	public static final int EXPR_integersubtraction = EXPR_mask + 26;
	public static final int EXPR_integermultiplication = EXPR_mask + 27;
	public static final int EXPR_integerdivision = EXPR_mask + 28;
	public static final int EXPR_integerremainder = EXPR_mask + 29;
	public static final int EXPR_integercoercion = EXPR_mask + 30;
	// BITWISE
	public static final int EXPR_bitwisenot = EXPR_mask + 32;
	public static final int EXPR_bitwiseand = EXPR_mask + 33;
	public static final int EXPR_bitwiseor = EXPR_mask + 34;
	public static final int EXPR_bitwisexor = EXPR_mask + 35;
	public static final int EXPR_bitwiseshl = EXPR_mask + 36;
	public static final int EXPR_bitwiseshr = EXPR_mask + 37;
	// REFERENCES
	public static final int EXPR_dereference = EXPR_mask + 40;
	public static final int EXPR_new = EXPR_mask + 41;
	public static final int EXPR_lambda = EXPR_mask + 42;
	public static final int EXPR_lambdaaccess = EXPR_mask + 43;
	// RECORDS
	public static final int EXPR_recordaccess = EXPR_mask + 48;
	public static final int EXPR_recordinitialiser = EXPR_mask + 51;
	// ARRAYS
	public static final int EXPR_arrayaccess = EXPR_mask + 56;
	public static final int EXPR_arraylength = EXPR_mask + 59;
	public static final int EXPR_arraygenerator = EXPR_mask + 60;
	public static final int EXPR_arrayinitialiser = EXPR_mask + 61;
	public static final int EXPR_arrayrange = EXPR_mask + 62;

	// =========================================================================
	// Constructors
	// =========================================================================

	public WyllFile(Path.Entry<WyllFile> entry) {
		super(entry);
	}

	public WyllFile(Entry<WyllFile> entry, SyntacticItem[] items) {
		super(entry);
		for (int i = 0; i != items.length; ++i) {
			syntacticItems.add(items[i]);
			items[i].allocate(this, i);
		}
	}

	// =========================================================================
	// Accessors
	// =========================================================================

	public Tuple<Decl> getDeclarations() {
		// The first node is always the declaration root.
		List<Decl.Module> modules = getSyntacticItems(Decl.Module.class);
		if (modules.size() != 1) {
			throw new RuntimeException("expecting one module, found " + modules.size());
		}
		return modules.get(0).getDeclarations();
	}


	public <S extends Decl.Named> S getDeclaration(Identifier name, Type signature, Class<S> kind) {
		List<S> matches = super.getSyntacticItems(kind);
		for (int i = 0; i != matches.size(); ++i) {
			S match = matches.get(i);
			if (match.getName().equals(name)) {
				if (signature != null && signature.equals(match.getType())) {
					return match;
				} else if (signature == null) {
					return match;
				}
			}
		}
		throw new IllegalArgumentException("unknown declarataion (" + name + "," + signature + ")");
	}

	// ============================================================
	// Declarations
	// ============================================================

	/**
	 * <p>
	 * Represents a declaration within a Whiley source file. This includes <i>import
	 * declarations</i>, <i>function or method declarations</i>, <i>type
	 * declarations</i>, <i>variable declarations</i> and more.
	 * </p>
	 * <p>
	 * In general, a declaration is often a top-level entity within a module.
	 * However, this is not always the case. For example, variable declarations are
	 * used to represent local variables, function or method parameters, etc.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	public static interface Decl extends CompilationUnit.Declaration {

		/**
		 * Represents the top-level entity in a Whiley source file. All other
		 * declartions are contained within this.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Module extends AbstractSyntacticItem implements Decl {

			public Module(Name name, Tuple<Decl> declarations) {
				super(DECL_module, name, declarations);
			}

			public Name getName() {
				return (Name) get(0);
			}

			@SuppressWarnings("unchecked")
			public Tuple<Decl> getDeclarations() {
				return (Tuple<Decl>) get(1);
			}

			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Module((Name) operands[0], (Tuple<Decl>) operands[1]);
			}
		}

		/**
		 * A named declaration has an additional symbol name associated with it
		 *
		 * @author David J. Pearce
		 *
		 */
		public static abstract class Named extends AbstractSyntacticItem implements Decl {

			public Named(int opcode, Tuple<Modifier> modifiers, Identifier name, SyntacticItem... rest) {
				super(opcode, ArrayUtils.append(new SyntacticItem[] { modifiers, name }, rest));
			}

			@SuppressWarnings("unchecked")
			public Tuple<Modifier> getModifiers() {
				return (Tuple<Modifier>) super.get(0);
			}

			public Identifier getName() {
				return (Identifier) super.get(1);
			}

			abstract public WyllFile.Type getType();

			public Name getQualifiedName() {
				Module module = getAncestor(Decl.Module.class);
				Name name = module.getName();
				Identifier[] idents = name.getAll();
				idents = Arrays.copyOf(idents, idents.length + 1);
				idents[name.size()] = getName();
				return new Name(idents);
			}
		}

		public static class Method extends Named {
			public Method(Tuple<Modifier> modifiers, Identifier name, Tuple<Decl.Variable> parameters,
					WyllFile.Type ret, Stmt.Block body) {
				super(DECL_method, modifiers, name, parameters, ret, body);
			}

			@SuppressWarnings("unchecked")
			public Tuple<Decl.Variable> getParameters() {
				return (Tuple<Decl.Variable>) get(2);
			}

			@SuppressWarnings("unchecked")
			public WyllFile.Type getReturn() {
				return (WyllFile.Type) get(3);
			}

			public Stmt.Block getBody() {
				return (Stmt.Block) get(4);
			}

			@Override
			public WyllFile.Type.Method getType() {
			  Tuple<WyllFile.Type> projectedParameters = getParameters().map((Decl.Variable d) -> d.getType());
			  return new WyllFile.Type.Method(projectedParameters, getReturn());
			}

			@SuppressWarnings("unchecked")
			@Override
			public Method clone(SyntacticItem[] operands) {
				return new Method((Tuple<Modifier>) operands[0], (Identifier) operands[1],
						(Tuple<Decl.Variable>) operands[2], (WyllFile.Type) operands[3], (Stmt.Block) operands[4]);
			}
		}

		/**
		 * <p>
		 * Represents a type declaration in a Whiley source file. A simple example to
		 * illustrate is:
		 * </p>
		 *
		 * <pre>
		 * type nat is (int x) where x >= 0
		 * </pre>
		 *
		 * <p>
		 * This defines a <i>constrained type</i> called <code>nat</code> which
		 * represents the set of natural numbers (i.e the non-negative integers). The
		 * "where" clause is optional and is often referred to as the type's
		 * "constraint".
		 * </p>
		 *
		 * <p>
		 * Type declarations may also have modifiers, such as <code>public</code> and
		 * <code>private</code>.
		 * </p>
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Type extends Named {
			public Type(Tuple<Modifier> modifiers, Identifier name, WyllFile.Type type, boolean recursive) {
				super(recursive ? DECL_rectype : DECL_type, modifiers, name, type);
			}

			@Override
			public WyllFile.Type getType() {
				return (WyllFile.Type) get(2);
			}

			public boolean isRecursive() {
				return getOpcode() == DECL_rectype;
			}

			@SuppressWarnings("unchecked")
			@Override
			public Decl.Type clone(SyntacticItem[] operands) {
				return new Decl.Type((Tuple<Modifier>) operands[0], (Identifier) operands[1],
						(WyllFile.Type) operands[2], isRecursive());
			}
		}

		// ============================================================
		// Variable Declaration
		// ============================================================

		/**
		 * <p>
		 * Represents a variable declaration which has an optional expression assignment
		 * referred to as an <i>initialiser</i>. If an initialiser is given, then this
		 * will be evaluated and assigned to the variable when the declaration is
		 * executed. Some example declarations:
		 * </p>
		 *
		 * <pre>
		 * int x
		 * int y = 1
		 * int z = x + y
		 * </pre>
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Variable extends Named implements Stmt {
			public Variable(Tuple<Modifier> modifiers, Identifier name, WyllFile.Type type) {
				super(DECL_variable, modifiers, name, type);
			}

			public Variable(Tuple<Modifier> modifiers, Identifier name, WyllFile.Type type, Expr initialiser) {
				super(DECL_variableinitialiser, modifiers, name, type, initialiser);
			}

			protected Variable(int opcode, Tuple<Modifier> modifiers, Identifier name, WyllFile.Type type,
					Expr initialiser) {
				super(opcode, modifiers, name, type, initialiser);
			}

			public boolean hasInitialiser() {
				return getOpcode() == DECL_variableinitialiser;
			}

			@Override
			public WyllFile.Type getType() {
				return (WyllFile.Type) get(2);
			}

			public Expr getInitialiser() {
				return (Expr) get(3);
			}

			@SuppressWarnings("unchecked")
			@Override
			public Decl.Variable clone(SyntacticItem[] operands) {
				if (operands.length == 3) {
					return new Decl.Variable((Tuple<Modifier>) operands[0], (Identifier) operands[1],
							(WyllFile.Type) operands[2]);
				} else {
					return new Decl.Variable((Tuple<Modifier>) operands[0], (Identifier) operands[1],
							(WyllFile.Type) operands[2], (Expr) operands[3]);
				}
			}

			@Override
			public String toString() {
				String r = getType().toString();
				r += " " + getName().toString();
				if (hasInitialiser()) {
					r += " = " + getInitialiser().toString();
				}
				return r;
			}
		}

		public class StaticVariable extends Variable {
			public StaticVariable(Tuple<Modifier> modifiers, Identifier name, WyllFile.Type type, Expr initialiser) {
				super(DECL_staticvar, modifiers, name, type, initialiser);
			}

			@Override
			public boolean hasInitialiser() {
				return true;
			}

			@SuppressWarnings("unchecked")
			@Override
			public StaticVariable clone(SyntacticItem[] operands) {
				return new StaticVariable((Tuple<Modifier>) operands[0], (Identifier) operands[1],
						(WyllFile.Type) operands[2], (Expr) operands[3]);
			}
		}
	}
	// ============================================================
	// Stmt
	// ============================================================

	/**
	 * Provides classes for representing statements in Whiley's source language.
	 * Examples include <i>assignments</i>, <i>for-loops</i>, <i>conditions</i>,
	 * etc. Each class is an instance of <code>SyntacticItem</code> and, hence,
	 * can be adorned with certain information (such as source location, etc).
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Stmt extends SyntacticItem {

		public interface Loop extends Stmt {
			Expr getCondition();

			Stmt getBody();
		}

		/**
		 * <p>
		 * A statement block represents a sequence of zero or more consecutive
		 * statements at the same indentation level. The following illustrates:
		 * </p>
		 *
		 * <pre>
		 * function abs(int x) -> (int r):
		 * // ---------------------------+
		 *    if x > 0:               // |
		 *       // ----------------+    |
		 *       return x        // |    |
		 *       // ----------------+    |
		 *    else:                   // |
		 *       // ----------------+    |
		 *       return -x       // |    |
		 *       // ----------------+    |
		 * // ---------------------------+
		 * </pre>
		 * <p>
		 * This example contains three statement blocks. The outermost block defines the
		 * body of the function and contains exactly one statement (i.e. the
		 * <code>if</code> statement). Two inner blocks are used to represent the true
		 * and false branches of the conditional.
		 * </p>
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Block extends AbstractSyntacticItem implements Stmt {
			public Block(Stmt... stmts) {
				super(STMT_block, stmts);
			}

			@Override
			public Stmt get(int i) {
				return (Stmt) super.get(i);
			}

			@Override
			public Block clone(SyntacticItem[] operands) {
				return new Block(ArrayUtils.toArray(Stmt.class, operands));
			}
		}

		/**
		 * <p>
		 * Represents a assert statement of the form "<code>assert e</code>", where
		 * <code>e</code> is a boolean expression. The following illustrates:
		 * </p>
		 *
		 * <pre>
		 * function abs(int x) -> int:
		 *     if x < 0:
		 *         x = -x
		 *     assert x >= 0
		 *     return x
		 * </pre>
		 *
		 * <p>
		 * Assertions are either statically checked by the verifier, or turned into
		 * runtime checks.
		 * </p>
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Assert extends AbstractSyntacticItem implements Stmt {
			public Assert(Expr condition) {
				super(STMT_assert, condition);
			}

			public Expr getCondition() {
				return (Expr) super.get(0);
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Assert((Expr) operands[0]);
			}
		}

		/**
		 * <p>
		 * Represents an assignment statement of the form "<code>lhs = rhs</code>".
		 * Here, the <code>rhs</code> is any expression, whilst the <code>lhs</code>
		 * must be an <code>LVal</code> --- that is, an expression permitted on the
		 * left-side of an assignment. The following illustrates different possible
		 * assignment statements:
		 * </p>
		 *
		 * <pre>
		 * x = y       // variable assignment
		 * x.f = y     // field assignment
		 * x[i] = y    // list assignment
		 * x[i].f = y  // compound assignment
		 * </pre>
		 *
		 * <p>
		 * The last assignment here illustrates that the left-hand side of an assignment
		 * can be arbitrarily complex, involving nested assignments into lists and
		 * records.
		 * </p>
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Assign extends AbstractSyntacticItem implements Stmt {
			public Assign(LVal lval, Expr rval) {
				super(STMT_assign, lval, rval);
			}

			@SuppressWarnings("unchecked")
			public LVal getLeftHandSide() {
				return (LVal) super.get(0);
			}

			@SuppressWarnings("unchecked")
			public Expr getRightHandSide() {
				return (Expr) super.get(1);
			}

			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Assign((LVal) operands[0], (Expr) operands[1]);
			}
		}

		/**
		 * Represents a classical break statement of the form "<code>break</code>" which
		 * can be used to force the termination of a loop or switch statement.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Break extends AbstractSyntacticItem implements Stmt {
			public Break() {
				super(STMT_break);
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Break();
			}
		}

		/**
		 * Represents a classical continue statement of the form "<code>continue</code>"
		 * which can be used to proceed to the next iteration of a loop or the next case
		 * of a switch statement.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Continue extends AbstractSyntacticItem implements Stmt {
			public Continue() {
				super(STMT_continue);
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Continue();
			}
		}

		/**
		 * <p>
		 * Represents a do-while statement whose body is made up from a block of
		 * statements separated by indentation. As an example:
		 * </p>
		 *
		 * <pre>
		 * function sum([int] xs) -> int
		 * requires |xs| > 0:
		 *   int r = 0
		 *   int i = 0
		 *   do:
		 *     r = r + xs[i]
		 *     i = i + 1
		 *   while i < |xs|
		 *   return r
		 * </pre>
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class DoWhile extends AbstractSyntacticItem implements Loop {
			public DoWhile(Expr condition, Stmt.Block body) {
				super(STMT_dowhile, condition, body);
			}

			@Override
			public Expr getCondition() {
				return (Expr) super.get(0);
			}

			@Override
			public Stmt.Block getBody() {
				return (Stmt.Block) super.get(1);
			}

			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new DoWhile((Expr) operands[0], (Stmt.Block) operands[1]);
			}
		}

		/**
		 * Represents a fail statement for the form "<code>fail</code>". This causes an
		 * abrupt termination of the program and should represent dead-code if present.
		 */
		public static class Fail extends AbstractSyntacticItem implements Stmt {
			public Fail() {
				super(STMT_fail);
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Fail();
			}
		}

		public static class ForEach extends AbstractSyntacticItem implements Stmt {
			public ForEach(Decl.Variable var, Expr start, Expr end, Stmt.Block body) {
				super(STMT_foreach, var, start, end, body);
			}

			public Decl.Variable getVariable() {
				return (Decl.Variable) operands[0];
			}

			public Expr getStart() {
				return  (Expr) operands[1];
			}

			public Expr getEnd() {
				return  (Expr) operands[2];
			}

			public Stmt.Block getBody() {
				return (Stmt.Block) operands[3];
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new ForEach((Decl.Variable) operands[0], (Expr) operands[1], (Expr) operands[2], (Stmt.Block) operands[3]);
			}
		}

		/**
		 * <p>
		 * Represents a classical if-else statement consisting of a <i>condition</i>, a
		 * <i>true branch</i> and an optional <i>false branch</i>. The following
		 * illustrates:
		 * </p>
		 *
		 * <pre>
		 * function max(int x, int y) -> int:
		 *   if(x > y):
		 *     return x
		 *   else if(x == y):
		 *   	return 0
		 *   else:
		 *     return y
		 * </pre>
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class IfElse extends AbstractSyntacticItem implements Stmt {
			public IfElse(Tuple<Pair<Expr,Stmt.Block>> branches) {
				super(STMT_if, branches);
			}

			public IfElse(Tuple<Pair<Expr,Stmt.Block>> branches, Stmt.Block defaultBranch) {
				super(STMT_ifelse, branches, defaultBranch);
			}

			public boolean hasDefaultBranch() {
				return getOpcode() == STMT_ifelse;
			}

			public Tuple<Pair<Expr,Block>> getBranches() {
				return (Tuple<Pair<Expr,Block>>) super.get(0);
			}

			public Stmt.Block getDefaultBranch() {
				return (Stmt.Block) super.get(1);
			}

			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				if (operands.length == 1) {
					return new IfElse((Tuple<Pair<Expr,Block>>) operands[0]);
				} else {
					return new IfElse((Tuple<Pair<Expr,Block>>) operands[0], (Stmt.Block) operands[1]);
				}
			}
		}

		/**
		 * <p>
		 * Represents a return statement which has one or more optional return
		 * expressions referred to simply as the "returns". Note that, the returned
		 * expression (if there is one) must begin on the same line as the return
		 * statement itself. The following illustrates:
		 * </p>
		 *
		 * <pre>
		 * function f(int x) -> int:
		 * 	  return x + 1
		 * </pre>
		 *
		 * <p>
		 * Here, we see a simple <code>return</code> statement which returns an
		 * <code>int</code> value.
		 * </p>
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Return extends AbstractSyntacticItem implements Stmt {
			public Return() {
				super(STMT_return);
			}

			public Return(Expr ret) {
				super(STMT_return, ret);
			}

			public boolean hasReturn() {
				return size() > 0;
			}

			public Expr getReturn() {
				return (Expr) super.get(0);
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				if(hasReturn() ) {
					return new Return((Expr) operands[0]);
				} else {
					return new Return();
				}
			}
		}

		/**
		 * <p>
		 * Represents a classical switch statement made of up a condition and one or
		 * more case blocks. Each case consists of zero or more constant expressions.
		 * The following illustrates:
		 * </p>
		 * <pre>
		 * switch x:
		 *   case 1:
		 *     y = -1
		 *   case 2:
		 *     y = -2
		 *   default:
		 *     y = 0
		 * </pre>
		 * @author David J. Pearce
		 *
		 */
		public static class Switch extends AbstractSyntacticItem implements Stmt {
			public Switch(Expr condition, Tuple<Case> cases) {
				super(STMT_switch, condition, cases);
			}

			public Expr getCondition() {
				return (Expr) get(0);
			}

			@SuppressWarnings("unchecked")
			public Tuple<Case> getCases() {
				return (Tuple<Case>) get(1);
			}

			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Switch((Expr) operands[0], (Tuple<Case>) operands[1]);
			}
		}

		public static class Case extends AbstractSyntacticItem {

			public Case(Tuple<Value.Int> conditions, Stmt.Block block) {
				super(STMT_caseblock, conditions, block);
			}

			public boolean isDefault() {
				return getConditions().size() == 0;
			}

			@SuppressWarnings("unchecked")
			public Tuple<Value.Int> getConditions() {
				return (Tuple<Value.Int>) get(0);
			}

			public Stmt.Block getBlock() {
				return (Stmt.Block) get(1);
			}

			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Case((Tuple<Value.Int>) operands[0], (Stmt.Block) operands[1]);
			}
		}

		/**
		 * <p>
		 * Represents a while statement made up a condition and a block of statements
		 * referred to as the <i>body</i>. The following illustrates:
		 * </p>
		 *
		 * <pre>
		 * function sum([int] xs) -> int:
		 *   int r = 0
		 *   int i = 0
		 *   while i < |xs| where i >= 0:
		 *     r = r + xs[i]
		 *     i = i + 1
		 *   return r
		 * </pre>
		 *
		 * <p>
		 * The optional <code>where</code> clause(s) are commonly referred to as the
		 * "loop invariant". When multiple clauses are given, these are combined using a
		 * conjunction. The combined invariant defines a condition which must be true on
		 * every iteration of the loop.
		 * </p>
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class While extends AbstractSyntacticItem implements Loop {
			public While(Expr condition, Stmt.Block body) {
				super(STMT_while, condition, body);
			}

			@Override
			public Expr getCondition() {
				return (Expr) super.get(0);
			}

			@Override
			public Stmt.Block getBody() {
				return (Stmt.Block) super.get(1);
			}

			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new While((Expr) operands[0], (Stmt.Block) operands[1]);
			}
		}
	}

	/**
	 * <p>
	 * Represents an arbitrary expression permissible on the left-hand side of an
	 * assignment statement. For example, consider the following method:
	 * </p>
	 *
	 * <pre>
	 * method f(int[] xs, int x, int y):
	 *   x = y + 1
	 *   xs[i] = x
	 * </pre>
	 * <p>
	 * This contains two assignment statements with the lval's <code>x</code> and
	 * <code>xs[i]</code> respectively. The set of lvals is a subset of the set of
	 * all expressions, since not every expression can be assigned. For example, an
	 * assignment "<code>f() = x</code>" does not make sense.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface LVal extends Expr {

	}

	/**
	 * Represents an arbitrary expression within a Whiley source file. Every
	 * expression has a known type and zero or more expression operands alongside
	 * other syntactic information.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Expr extends Stmt {

		/**
		 * Get the type to which this expression is guaranteed to evaluate. That is, the
		 * result type of this expression.
		 *
		 * @return
		 */
		public Type getType();

		// =========================================================================
		// General Expressions
		// =========================================================================

		/**
		 * Represents an abstract operator expression over exactly one <i>operand
		 * expression</i>. For example, <code>!x</code> is a unary operator expression.
		 *
		 * @author David J. Pearce
		 *
		 */
		public interface UnaryOperator extends Expr {
			public Expr getOperand();
		}

		/**
		 * Represents an abstract operator expression over exactly two <i>operand
		 * expressions</i>. For example, <code>x &lt;&lt; 1</code> is a binary operator
		 * expression.
		 *
		 * @author David J. Pearce
		 *
		 */
		public interface BinaryOperator extends Expr {
			public Expr getFirstOperand();

			public Expr getSecondOperand();
		}

		/**
		 * Represents an abstract operator expression over exactly three <i>operand
		 * expressions</i>. For example, <code>xs[i:=1]</code> is a ternary operator
		 * expression.
		 *
		 * @author David J. Pearce
		 *
		 */
		public interface TernaryOperator extends Expr {
			public Expr getFirstOperand();

			public Expr getSecondOperand();

			public Expr getThirdOperand();
		}

		/**
		 * Represents an abstract operator expression over one or more <i>operand
		 * expressions</i>. For example. in <code>arr[i+1]</code> the expression
		 * <code>i+1</code> would be an nary operator expression.
		 *
		 * @author David J. Pearce
		 *
		 */
		public interface NaryOperator extends Expr {
			public Tuple<Expr> getOperands();
		}

		public abstract static class AbstractExpr extends AbstractSyntacticItem implements Expr {
			public AbstractExpr(int opcode, Type type, SyntacticItem... items) {
				super(opcode, ArrayUtils.append(type, items));
			}

			@Override
			public Type getType() {
				return (Type) super.get(0);
			}
		}

		/**
		 * Represents a cast expression of the form "<code>(T) e</code>" where
		 * <code>T</code> is the <i>cast type</i> and <code>e</code> the <i>casted
		 * expression</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Clone extends AbstractExpr implements Expr, UnaryOperator {
			public Clone(Type type, Expr rhs) {
				super(EXPR_clone, type, rhs);
			}

			@Override
			public Expr getOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Clone clone(SyntacticItem[] operands) {
				return new Clone((Type) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				return "@(" + getOperand() + ")";
			}
		}

		public static class NullConstant extends AbstractExpr implements Expr {
			public NullConstant() {
				super(EXPR_nullconstant, Type.Null);
			}

			@Override
			public NullConstant clone(SyntacticItem[] operands) {
				return new NullConstant();
			}

			@Override
			public String toString() {
				return "null";
			}
		}

		public static class BoolConstant extends AbstractExpr implements Expr {
			public BoolConstant(Value.Bool value) {
				super(EXPR_boolconstant, Type.Bool, value);
			}

			public Value.Bool getValue() {
				return (Value.Bool) get(1);
			}

			@Override
			public BoolConstant clone(SyntacticItem[] operands) {
				return new BoolConstant((Value.Bool) operands[1]);
			}

			@Override
			public String toString() {
				return getValue().toString();
			}
		}


		public static class IntConstant extends AbstractExpr implements Expr {
			public IntConstant(Type.Int type, Value.Int value) {
				super(EXPR_intconstant, type, value);
			}

			public Value.Int getValue() {
				return (Value.Int) get(1);
			}

			@Override
			public IntConstant clone(SyntacticItem[] operands) {
				return new IntConstant((Type.Int) operands[0], (Value.Int) operands[1]);
			}

			@Override
			public String toString() {
				return getValue().toString();
			}
		}

		/**
		 * Represents the use of a static variable within an expression. A static
		 * variable is effectively a global variable which may or may not be defined
		 * within the enclosing module.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class StaticVariableAccess extends AbstractExpr implements LVal, Expr {
			public StaticVariableAccess(Type type, Name name) {
				super(EXPR_staticvariable, type, name);
			}

			public Name getName() {
				return (Name) get(1);
			}

			@Override
			public StaticVariableAccess clone(SyntacticItem[] operands) {
				return new StaticVariableAccess((Type) operands[0], (Name) operands[1]);
			}

			@Override
			public String toString() {
				return getName().toString();
			}
		}

		/**
		 * Represents an invocation of the form "<code>x.y.f(e1,..en)</code>". Here,
		 * <code>x.y.f</code> constitute a <i>partially-</i> or <i>fully-qualified
		 * name</i> and <code>e1</code> ... <code>en</code> are the <i>argument
		 * expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Invoke extends AbstractSyntacticItem implements Expr, NaryOperator {

			public Invoke(Name name, Tuple<Expr> arguments, Type.Method signature) {
				super(EXPR_invoke, name, arguments, signature);
			}

			@Override
			public Type getType() {
				return getSignature().getReturn();
			}

			public Name getName() {
				return (Name) get(0);
			}

			@Override
			@SuppressWarnings("unchecked")
			public Tuple<Expr> getOperands() {
				return (Tuple<Expr>) get(1);
			}

			public Type.Method getSignature() {
				return (Type.Method) get(2);
			}

			@SuppressWarnings("unchecked")
			@Override
			public Invoke clone(SyntacticItem[] operands) {
				return new Invoke((Name) operands[0], (Tuple<Expr>) operands[1], (Type.Method) operands[2]);
			}

			@Override
			public String toString() {
				String r = getName().toString();
				r += getOperands();
				return r;
			}
		}

		/**
		 * Represents an indirect invocation of the form "<code>x.y(e1,..en)</code>".
		 * Here, <code>x.y</code> returns a function value and <code>e1</code> ...
		 * <code>en</code> are the <i>argument expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class IndirectInvoke extends AbstractSyntacticItem implements Expr {

			public IndirectInvoke(Type type, Expr source, Tuple<Expr> arguments) {
				super(EXPR_indirectinvoke, type, source, arguments);
			}

			@Override
			public Type getType() {
				return (Type) get(0);
			}

			public Expr getSource() {
				return (Expr) get(1);
			}

			@SuppressWarnings("unchecked")
			public Tuple<Expr> getArguments() {
				return (Tuple<Expr>) get(2);
			}

			@SuppressWarnings("unchecked")
			@Override
			public IndirectInvoke clone(SyntacticItem[] operands) {
				return new IndirectInvoke((Type) operands[0], (Expr) operands[1], (Tuple<Expr>) operands[2]);
			}

			@Override
			public String toString() {
				String r = getSource().toString();
				r += getArguments();
				return r;
			}
		}

		public static class UnionAccess extends AbstractExpr implements Expr, UnaryOperator {
			public UnionAccess(Type type, Expr rhs) {
				super(EXPR_unionaccess, type, rhs);
			}

			@Override
			public Expr getOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public UnionAccess clone(SyntacticItem[] operands) {
				return new UnionAccess((Type) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				return getOperand() + "?";
			}
		}

		public static class UnionEnter extends AbstractExpr implements Expr, UnaryOperator {
			public UnionEnter(Type type, Value.Int tag, Expr rhs) {
				super(EXPR_unionenter, type, tag, rhs);
			}

			public Value.Int getTag() {
				return (Value.Int) super.get(1);
			}

			@Override
			public Expr getOperand() {
				return (Expr) super.get(2);
			}

			@Override
			public UnionEnter clone(SyntacticItem[] operands) {
				return new UnionEnter((Type) operands[0], (Value.Int) operands[1], (Expr) operands[2]);
			}

			@Override
			public String toString() {
				return getOperand() + "#" + getTag();
			}
		}


		public static class UnionLeave extends AbstractExpr implements Expr, UnaryOperator {
			public UnionLeave(Type type, Value.Int tag, Expr rhs) {
				super(EXPR_unionleave, type, tag, rhs);
			}

			public Value.Int getTag() {
				return (Value.Int) super.get(1);
			}

			@Override
			public Expr getOperand() {
				return (Expr) super.get(2);
			}

			@Override
			public UnionLeave clone(SyntacticItem[] operands) {
				return new UnionLeave((Type) operands[0], (Value.Int) operands[1], (Expr) operands[2]);
			}

			@Override
			public String toString() {
				return getOperand() + "?" + getTag();
			}
		}

		/**
		 * Represents the use of some variable within an expression. For example, in
		 * <code>x + 1</code> the expression <code>x</code> is a variable access
		 * expression. Every variable access is associated with a <i>variable
		 * declaration</i> that unique identifies which variable is being accessed.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class VariableAccess extends AbstractExpr implements LVal {
			public VariableAccess(Type type, Identifier name) {
				// FIXME: this needs to be fixed
				super(EXPR_variableaccess, type, name);
			}

			public Identifier getName() {
				return (Identifier) get(1);
			}

			@Override
			public VariableAccess clone(SyntacticItem[] operands) {
				return new VariableAccess((Type) operands[0], (Identifier) operands[1]);
			}

			@Override
			public String toString() {
				return getName().toString();
			}
		}

		// =========================================================================
		// Logical Expressions
		// =========================================================================
		/**
		 * Represents a <i>logical conjunction</i> of the form
		 * "<code>e1 && .. && en</code>" where <code>e1</code> ... <code>en</code> are
		 * the <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class LogicalAnd extends AbstractSyntacticItem implements NaryOperator {
			public LogicalAnd(Tuple<Expr> operands) {
				super(EXPR_logicaland, operands);
			}

			@Override
			public Type getType() {
				return Type.Bool;
			}

			@Override
			public Tuple<Expr> getOperands() {
				return (Tuple<Expr>) super.get(0);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new LogicalAnd((Tuple<Expr>) operands[0]);
			}

			@Override
			public String toString() {
				return " && ";
			}
		}

		/**
		 * Represents a <i>logical disjunction</i> of the form
		 * "<code>e1 || .. || en</code>" where <code>e1</code> ... <code>en</code> are
		 * the <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class LogicalOr extends AbstractSyntacticItem implements NaryOperator {
			public LogicalOr(Tuple<Expr> operands) {
				super(EXPR_logicalor, operands);
			}

			@Override
			public Type getType() {
				return Type.Bool;
			}

			@Override
			public Tuple<Expr> getOperands() {
				return (Tuple<Expr>) super.get(0);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new LogicalOr((Tuple<Expr>) operands[0]);
			}

			@Override
			public String toString() {
				return " || ";
			}
		}

		/**
		 * Represents a <i>logical negation</i> of the form "<code>!e</code>" where
		 * <code>e</code> is the <i>operand expression</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class LogicalNot extends AbstractSyntacticItem implements UnaryOperator {
			public LogicalNot(Expr operand) {
				super(EXPR_logicalnot, operand);
			}

			@Override
			public Type getType() {
				return Type.Bool;
			}

			@Override
			public Expr getOperand() {
				return (Expr) get(0);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new LogicalNot((Expr) operands[0]);
			}
		}

		// =========================================================================
		// Comparator Expressions
		// =========================================================================

		/**
		 * Represents an equality expression of the form "<code>e1 == e2</code>" where
		 * <code>e1</code> and <code>e2</code> are the <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Equal extends AbstractSyntacticItem implements BinaryOperator {
			public Equal(Expr lhs, Expr rhs) {
				super(EXPR_equal, lhs, rhs);
			}

			@Override
			public Type getType() {
				return Type.Bool;
			}

			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(0);
			}

			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new Equal((Expr) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				return " == ";
			}
		}

		/**
		 * Represents an unequality expression of the form "<code>e1 != e2</code>" where
		 * <code>e1</code> and <code>e2</code> are the <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class NotEqual extends AbstractSyntacticItem implements BinaryOperator {
			public NotEqual(Expr lhs, Expr rhs) {
				super(EXPR_notequal, lhs, rhs);
			}

			@Override
			public Type getType() {
				return Type.Bool;
			}

			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(0);
			}

			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new NotEqual((Expr) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				return " != ";
			}
		}

		/**
		 * Represents a strict <i>inequality expression</i> of the form
		 * "<code>e1 < e2</code>" where <code>e1</code> and <code>e2</code> are the
		 * <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class IntegerLessThan extends AbstractSyntacticItem implements BinaryOperator {
			public IntegerLessThan(Expr lhs, Expr rhs) {
				super(EXPR_integerlessthan, lhs, rhs);
			}

			@Override
			public Type getType() {
				return Type.Bool;
			}

			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(0);
			}

			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new IntegerLessThan((Expr) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				return " < ";
			}
		}

		/**
		 * Represents a non-strict <i>inequality expression</i> of the form
		 * "<code>e1 <= e2</code>" where <code>e1</code> and <code>e2</code> are the
		 * <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class IntegerLessThanOrEqual extends AbstractSyntacticItem implements BinaryOperator {
			public IntegerLessThanOrEqual(Expr lhs, Expr rhs) {
				super(EXPR_integerlessequal, lhs, rhs);
			}

			@Override
			public Type getType() {
				return Type.Bool;
			}

			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(0);
			}

			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new IntegerLessThanOrEqual((Expr) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				return " <= ";
			}
		}

		/**
		 * Represents a strict <i>inequality expression</i> of the form
		 * "<code>e1 > e2</code>" where <code>e1</code> and <code>e2</code> are the
		 * <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class IntegerGreaterThan extends AbstractSyntacticItem implements BinaryOperator {
			public IntegerGreaterThan(Expr lhs, Expr rhs) {
				super(EXPR_integergreaterthan, lhs, rhs);
			}

			@Override
			public Type getType() {
				return Type.Bool;
			}

			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(0);
			}

			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new IntegerGreaterThan((Expr) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				return " > ";
			}
		}

		/**
		 * Represents a non-strict <i>inequality expression</i> of the form
		 * "<code>e1 >= e2</code>" where <code>e1</code> and <code>e2</code> are the
		 * <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class IntegerGreaterThanOrEqual extends AbstractSyntacticItem implements BinaryOperator {
			public IntegerGreaterThanOrEqual(Expr lhs, Expr rhs) {
				super(EXPR_integergreaterequal, lhs, rhs);
			}

			@Override
			public Type getType() {
				return Type.Bool;
			}

			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(0);
			}

			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new IntegerGreaterThanOrEqual((Expr) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				return " >= ";
			}
		}

		// =========================================================================
		// Integer Expressions
		// =========================================================================

		/**
		 * Represents an integer <i>addition expression</i> of the form
		 * "<code>e1 + e2</code>" where <code>e1</code> and <code>e2</code> are the
		 * <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class IntegerAddition extends AbstractExpr implements BinaryOperator {
			public IntegerAddition(Type type, Expr lhs, Expr rhs) {
				super(EXPR_integeraddition, type, lhs, rhs);
			}

			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(2);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new IntegerAddition((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}

			@Override
			public String toString() {
				return " + ";
			}
		}

		/**
		 * Represents an integer <i>subtraction expression</i> of the form
		 * "<code>e1 - e2</code>" where <code>e1</code> and <code>e2</code> are the
		 * <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class IntegerSubtraction extends AbstractExpr implements BinaryOperator {
			public IntegerSubtraction(Type type, Expr lhs, Expr rhs) {
				super(EXPR_integersubtraction, type, lhs, rhs);
			}

			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(2);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new IntegerSubtraction((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}

			@Override
			public String toString() {
				return " - ";
			}
		}

		/**
		 * Represents an integer <i>multiplication expression</i> of the form
		 * "<code>e1 * e2</code>" where <code>e1</code> and <code>e2</code> are the
		 * <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class IntegerMultiplication extends AbstractExpr implements BinaryOperator {
			public IntegerMultiplication(Type type, Expr lhs, Expr rhs) {
				super(EXPR_integermultiplication, type, lhs, rhs);
			}

			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(2);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new IntegerMultiplication((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}

			@Override
			public String toString() {
				return " * ";
			}
		}

		/**
		 * Represents an integer <i>division expression</i> of the form
		 * "<code>e1 / e2</code>" where <code>e1</code> and <code>e2</code> are the
		 * <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class IntegerDivision extends AbstractExpr implements BinaryOperator {
			public IntegerDivision(Type type, Expr lhs, Expr rhs) {
				super(EXPR_integerdivision, type, lhs, rhs);
			}

			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(2);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new IntegerDivision((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}

			@Override
			public String toString() {
				return " / ";
			}
		}

		/**
		 * Represents an integer <i>remainder expression</i> of the form
		 * "<code>e1 % e2</code>" where <code>e1</code> and <code>e2</code> are the
		 * <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class IntegerRemainder extends AbstractExpr implements BinaryOperator {
			public IntegerRemainder(Type type, Expr lhs, Expr rhs) {
				super(EXPR_integerremainder, type, lhs, rhs);
			}

			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(2);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new IntegerRemainder((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}

			@Override
			public String toString() {
				return " % ";
			}
		}

		/**
		 * Represents an integer <i>negation expression</i> of the form
		 * "<code>-e</code>" where <code>e</code> is the <i>operand expression</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class IntegerNegation extends AbstractExpr implements UnaryOperator {
			public IntegerNegation(Type type, Expr operand) {
				super(EXPR_integernegation, type, operand);
			}

			@Override
			public Expr getOperand() {
				return (Expr) get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new IntegerNegation((Type) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				return "-" + getOperand();
			}
		}

		public static class IntegerCoercion extends AbstractExpr implements UnaryOperator {
			public IntegerCoercion(Type.Int to, Type.Int from, Expr operand) {
				super(EXPR_integercoercion, to, from, operand);
			}

			public Type.Int getTarget() {
				return (Type.Int) get(0);
			}

			public Type.Int getActual() {
				return (Type.Int) get(1);
			}

			@Override
			public Expr getOperand() {
				return (Expr) get(2);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new IntegerCoercion((Type.Int) operands[0], (Type.Int) operands[1], (Expr) operands[2]);
			}

			@Override
			public String toString() {
				return "(" + getTarget() + ")" + getOperand();
			}
		}

		// =========================================================================
		// Bitwise Expressions
		// =========================================================================

		/**
		 * Represents a <i>bitwise shift left</i> of the form "<code>e << i</code>"
		 * where <code>e</code> is the expression being shifted and <code>i</code> the
		 * amount it is shifted by.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class BitwiseShiftLeft extends AbstractExpr implements BinaryOperator {
			public BitwiseShiftLeft(Type type, Expr lhs, Expr rhs) {
				super(EXPR_bitwiseshl, type, lhs, rhs);
			}

			/**
			 * Get the value operand to be shifted. That is <code>x</code> in
			 * <code>x << i</code>.
			 */
			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(1);
			}

			/**
			 * Get the operand indicating the amount to shift. That is <code>i</code> in
			 * <code>x << i</code>.
			 */
			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(2);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new BitwiseShiftLeft((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}

			@Override
			public String toString() {
				return " << ";
			}
		}

		/**
		 * Represents a <i>bitwise shift right</i> of the form "<code>e >> i</code>"
		 * where <code>e</code> is the expression being shifted and <code>i</code> the
		 * amount it is shifted by.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class BitwiseShiftRight extends AbstractExpr implements BinaryOperator {
			public BitwiseShiftRight(Type type, Expr lhs, Expr rhs) {
				super(EXPR_bitwiseshr, type, lhs, rhs);
			}

			/**
			 * Get the value operand to be shifted. That is <code>x</code> in
			 * <code>x >> i</code>.
			 */
			@Override
			public Expr getFirstOperand() {
				return (Expr) super.get(1);
			}

			/**
			 * Get the operand indicating the amount to shift. That is <code>i</code> in
			 * <code>x >> i</code>.
			 */
			@Override
			public Expr getSecondOperand() {
				return (Expr) super.get(2);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new BitwiseShiftRight((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}

			@Override
			public String toString() {
				return " >> ";
			}
		}

		/**
		 * Represents a <i>bitwise and</i> of the form "<code>e1 & .. & en</code>" where
		 * <code>e1</code> ... <code>en</code> are the <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class BitwiseAnd extends AbstractExpr implements NaryOperator {
			public BitwiseAnd(Type type, Tuple<Expr> operands) {
				super(EXPR_bitwiseand, type, operands);
			}

			@Override
			public Tuple<Expr> getOperands() {
				return (Tuple<Expr>) super.get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new BitwiseAnd((Type) operands[0], (Tuple<Expr>) operands[1]);
			}

			@Override
			public String toString() {
				return " & ";
			}
		}

		/**
		 * Represents a <i>bitwise or</i> of the form "<code>e1 | .. | en</code>" where
		 * <code>e1</code> ... <code>en</code> are the <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class BitwiseOr extends AbstractExpr implements NaryOperator {
			public BitwiseOr(Type type, Tuple<Expr> operands) {
				super(EXPR_bitwiseor, type, operands);
			}

			@Override
			public Tuple<Expr> getOperands() {
				return (Tuple<Expr>) super.get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new BitwiseOr((Type) operands[0], (Tuple<Expr>) operands[1]);
			}

			@Override
			public String toString() {
				return " | ";
			}
		}

		/**
		 * Represents a <i>bitwise xor</i> of the form "<code>e1 ^ .. ^ en</code>" where
		 * <code>e1</code> ... <code>en</code> are the <i>operand expressions</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class BitwiseXor extends AbstractExpr implements NaryOperator {
			public BitwiseXor(Type type, Tuple<Expr> operands) {
				super(EXPR_bitwisexor, type, operands);
			}

			@Override
			public Tuple<Expr> getOperands() {
				return (Tuple<Expr>) super.get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new BitwiseXor((Type) operands[0], (Tuple<Expr>) operands[1]);
			}

			@Override
			public String toString() {
				return " ^ ";
			}
		}

		/**
		 * Represents a <i>bitwise complement</i> of the form "<code>~e</code>" where
		 * <code>e</code> is the <i>operand expression</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class BitwiseComplement extends AbstractExpr implements UnaryOperator {
			public BitwiseComplement(Type type, Expr operand) {
				super(EXPR_bitwisenot, type, operand);
			}

			/**
			 * Get the operand to be complimented. That is,
			 * <code>e<code> in </code>!e</code>.
			 */
			@Override
			public Expr getOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new BitwiseComplement((Type) operands[0], (Expr) operands[1]);
			}
		}

		// =========================================================================
		// Reference Expressions
		// =========================================================================

		/**
		 * Represents an object dereference expression of the form "<code>*e</code>" where
		 * <code>e</code> is the <i>operand expression</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Dereference extends AbstractExpr implements LVal, UnaryOperator {
			public Dereference(Type type, Expr operand) {
				super(EXPR_dereference, type, operand);
			}

			/**
			 * Get the operand to be dereferenced. That is,
			 * <code>e<code> in </code>*e</code>.
			 */
			@Override
			public Expr getOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new Dereference((Type) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				return "*" + getOperand();
			}
		}

		/**
		 * Represents an <i>object allocation</i> expression of the form
		 * <code>new e</code> or <code>l:new e</code> where <code>e</code> is the
		 * operand expression and <code>l</code> the optional lifetime argument.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class New extends AbstractExpr implements LVal, UnaryOperator {
			public New(Type type, Expr operand) {
				super(EXPR_new, type, operand);
			}

			/**
			 * Get the operand to be evaluated and stored in the heap. That is,
			 * <code>e<code> in </code>new e</code>.
			 */
			@Override
			public Expr getOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public Expr clone(SyntacticItem[] operands) {
				return new New((Type) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				return "new " + getOperand();
			}
		}

		public static class LambdaAccess extends AbstractSyntacticItem implements Expr {

			public LambdaAccess(Name name, Type.Method signature) {
				super(EXPR_lambdaaccess, name, signature);
			}

			@Override
			public Type getType() {
				return getSignature();
			}

			public Name getName() {
				return (Name) get(0);
			}

			public Type.Method getSignature() {
				return (Type.Method) get(1);
			}

			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new LambdaAccess((Name) operands[0], (Type.Method) operands[1]);
			}
		}


		/**
		 * <p>
		 * Represents a lambda declaration within a Whiley source file. Sometimes also
		 * known as closures, these are anonymous function or method declarations
		 * declared within an expression. The following illustrates:
		 * </p>
		 *
		 * <pre>
		 * type func is function(int)->int
		*
		* function g() -> func:
		*    return &(int x -> x + 1)
		 * </pre>
		 * <p>
		 * This defines a lambda which accepts one parameter <code>x</code> and returns
		 * its increment.
		 * </p>
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Lambda extends AbstractSyntacticItem implements Expr {

			public Lambda(Tuple<Decl.Variable> parameters,
					Tuple<Decl.Variable> returns, Tuple<Identifier> captures, Tuple<Identifier> lifetimes, Expr body) {
				super(EXPR_lambda, parameters, returns, captures, lifetimes, body);
			}

			public Tuple<Decl.Variable> getParameters() {
				return (Tuple<Decl.Variable>) get(0);
			}

			public Tuple<Decl.Variable> getReturns() {
				return (Tuple<Decl.Variable>) get(1);
			}

			@SuppressWarnings("unchecked")
			public Tuple<Identifier> getCaptures() {
				return (Tuple<Identifier>) get(2);
			}

			@SuppressWarnings("unchecked")
			public Tuple<Identifier> getLifetimes() {
				return (Tuple<Identifier>) get(3);
			}

			public Expr getBody() {
				return (Expr) get(4);
			}

			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Expr.Lambda((Tuple<Decl.Variable>) operands[0], (Tuple<Decl.Variable>) operands[1],
						(Tuple<Identifier>) operands[2], (Tuple<Identifier>) operands[3], (Expr) operands[4]);
			}

			@Override
			public WyllFile.Type getType() {
				// TODO Auto-generated method stub
				return null;
			}
		}

		// =========================================================================
		// Array Expressions
		// =========================================================================

		/**
		 * Represents an <i>array access expression</i> of the form
		 * "<code>arr[e]</code>" where <code>arr</code> is the <i>source array</i> and
		 * <code>e</code> the <i>subscript expression</i>. This returns the value held
		 * in the element determined by <code>e</code>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class ArrayAccess extends AbstractExpr implements LVal, BinaryOperator {
			public ArrayAccess(Type.Array type, Expr src, Expr index) {
				super(EXPR_arrayaccess, type, src, index);
			}

			@Override
			public Type getType() {
				return ((Type.Array) super.getType()).getElement();
			}

			/**
			 * Get the source array operand for this access. That is <code>xs</code> in
			 * <code>xs[i]</code>.
			 */
			@Override
			public Expr getFirstOperand() {
				return (Expr) get(1);
			}

			/**
			 * Get the index operand for this access. That is <code>i</code> in
			 * <code>xs[i]</code>.
			 */
			@Override
			public Expr getSecondOperand() {
				return (Expr) get(2);
			}

			@Override
			public ArrayAccess clone(SyntacticItem[] operands) {
				return new ArrayAccess((Type.Array) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}

			@Override
			public String toString() {
				return getFirstOperand() + "[" + getSecondOperand() + "]";
			}
		}

		/**
		 * Represents an <i>array initialiser expression</i> of the form
		 * "<code>[e1,...,en]</code>" where <code>e1</code> ... <code>en</code> are the
		 * <i>initialiser expressions</i>. Thus returns a new array made up from those
		 * values resulting from the initialiser expressions.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class ArrayInitialiser extends AbstractExpr implements NaryOperator {
			public ArrayInitialiser(Type.Array type, Tuple<Expr> elements) {
				super(EXPR_arrayinitialiser, type, elements);
			}

			@Override
			public Type.Array getType() {
				return (Type.Array) super.getType();
			}

			@Override
			public Tuple<Expr> getOperands() {
				return (Tuple<Expr>) super.get(1);
			}

			@Override
			public ArrayInitialiser clone(SyntacticItem[] operands) {
				return new ArrayInitialiser((Type.Array) operands[0], (Tuple<Expr>) operands[1]);
			}

			@Override
			public String toString() {
				return Arrays.toString(toArray(Expr.class));
			}
		}

		/**
		 * Represents an <i>array generator expression</i> of the form
		 * "<code>[e1;e2]</code>" where <code>e1</code> is the <i>element expression</i>
		 * and <code>e2</code> is the <i>length expression</i>. This returns a new array
		 * whose length is determined by <code>e2</code> and where every element has
		 * contains the value determined by <code>e1</code>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class ArrayGenerator extends AbstractExpr implements BinaryOperator {
			public ArrayGenerator(Type.Array type, Expr value, Expr length) {
				super(EXPR_arraygenerator, type, value, length);
			}

			@Override
			public Type.Array getType() {
				return (Type.Array) super.getType();
			}

			/**
			 * Get the value operand for this generator. That is <code>e</code> in
			 * <code>[e; n]</code>.
			 */
			@Override
			public Expr getFirstOperand() {
				return (Expr) get(1);
			}

			/**
			 * Get the length operand for this generator. That is <code>n</code> in
			 * <code>[e; n]</code>.
			 */
			@Override
			public Expr getSecondOperand() {
				return (Expr) get(2);
			}

			@Override
			public ArrayGenerator clone(SyntacticItem[] operands) {
				return new ArrayGenerator((Type.Array) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}
		}

		/**
		 * Represents an <i>array length expression</i> of the form "<code>|arr|</code>"
		 * where <code>arr</code> is the <i>source array</i>. This simply returns the
		 * length of array <code>arr</code>. <code>e</code>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class ArrayLength extends AbstractExpr implements Expr.UnaryOperator {
			public ArrayLength(Type.Array type, Expr src) {
				super(EXPR_arraylength, type, src);
			}

			@Override
			public Type.Int getType() {
				return Type.Int;
			}

			public Type.Array getArrayType() {
				return (Type.Array) super.getType();
			}

			@Override
			public Expr getOperand() {
				return (Expr) super.get(1);
			}

			@Override
			public ArrayLength clone(SyntacticItem[] operands) {
				return new ArrayLength((Type.Array) operands[0], (Expr) operands[1]);
			}

			@Override
			public String toString() {
				return "|" + getOperand() + "|";
			}
		}

		// =========================================================================
		// Record Expressions
		// =========================================================================

		/**
		 * Represents a <i>record access expression</i> of the form "<code>rec.f</code>"
		 * where <code>rec</code> is the <i>source record</i> and <code>f</code> is the
		 * <i>field</i>.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class RecordAccess extends AbstractExpr implements LVal, UnaryOperator {
			public RecordAccess(Type.Record type, Expr lhs, Identifier rhs) {
				super(EXPR_recordaccess, type, lhs, rhs);
			}

			@Override
			public Type getType() {
				Type.Record rec_t = (Type.Record) super.getType();
				return rec_t.getField(getField());
			}

			/**
			 * Get the source operand for this access. That is <code>e</code> in
			 * <code>e.f/code>.
			 */
			@Override
			public Expr getOperand() {
				return (Expr) get(1);
			}

			/**
			 * Get the field name for this access. That is <code>f</code> in
			 * <code>e.f/code>.
			 */
			public Identifier getField() {
				return (Identifier) get(2);
			}

			@Override
			public RecordAccess clone(SyntacticItem[] operands) {
				return new RecordAccess((Type.Record) operands[0], (Expr) operands[1], (Identifier) operands[2]);
			}

			@Override
			public String toString() {
				return getOperand() + "." + getField();
			}
		}

		/**
		 * Represents a <i>record initialiser</i> expression of the form
		 * "<code>{ f1:e1, ..., fn:en }</code>" where <code>f1:e1</code> ...
		 * <code>fn:en</code> are <i>field initialisers</i>. This returns a new
		 * record where each field holds the value resulting from its corresponding
		 * expression.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class RecordInitialiser extends AbstractExpr implements Expr, NaryOperator {

			public RecordInitialiser(Type.Record type, Tuple<Identifier> fields, Tuple<Expr> operands) {
				super(EXPR_recordinitialiser, type, fields, operands);
			}

			@Override
			public Type.Record getType() {
				return (Type.Record) super.getType();
			}

			public Tuple<Identifier> getFields() {
				return (Tuple<Identifier>) super.get(1);
			}

			@Override
			public Tuple<Expr> getOperands() {
				return (Tuple<Expr>) super.get(2);
			}

			@SuppressWarnings("unchecked")
			@Override
			public RecordInitialiser clone(SyntacticItem[] operands) {
				return new RecordInitialiser((Type.Record) operands[0], (Tuple<Identifier>) operands[1],
						(Tuple<Expr>) operands[2]);
			}
		}
	}

	// =========================================================================
	// Types
	// =========================================================================

	public interface Type extends SyntacticItem {

		public static final Void Void = new Void();
		public static final Bool Bool = new Bool();
		public static final Int Int = new Int();
		public static final Null Null = new Null();

		public interface Atom extends Type {
		}

		public interface Primitive extends Atom {

		}

		/**
		 * A void type represents the type whose variables cannot exist! That is, they
		 * cannot hold any possible value. Void is used to represent the return type of
		 * a function which does not return anything. However, it is also used to
		 * represent the element type of an empty list of set. <b>NOTE:</b> the void
		 * type is a subtype of everything; that is, it is bottom in the type lattice.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Void extends AbstractSyntacticItem implements Primitive {
			public Void() {
				super(TYPE_void);
			}

			@Override
			public Void clone(SyntacticItem[] operands) {
				return new Void();
			}

			@Override
			public String toString() {
				return "void";
			}
		}

		/**
		 * The null type is a special type which should be used to show the absence of
		 * something. It is distinct from void, since variables can hold the special
		 * <code>null</code> value (where as there is no special "void" value). With all
		 * of the problems surrounding <code>null</code> and
		 * <code>NullPointerException</code>s in languages like Java and C, it may seem
		 * that this type should be avoided. However, it remains a very useful
		 * abstraction to have around and, in Whiley, it is treated in a completely safe
		 * manner (unlike e.g. Java).
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Null extends AbstractSyntacticItem implements Primitive {
			public Null() {
				super(TYPE_null);
			}

			@Override
			public Null clone(SyntacticItem[] operands) {
				return new Null();
			}

			@Override
			public String toString() {
				return "null";
			}
		}

		/**
		 * Represents the set of boolean values (i.e. true and false)
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Bool extends AbstractSyntacticItem implements Primitive {
			public Bool() {
				super(TYPE_bool);
			}

			@Override
			public Bool clone(SyntacticItem[] operands) {
				return new Bool();
			}

			@Override
			public String toString() {
				return "bool";
			}
		}

		/**
		 * Represents the set of (unbound) integer values. Since integer types in Whiley
		 * are unbounded, there is no equivalent to Java's <code>MIN_VALUE</code> and
		 * <code>MAX_VALUE</code> for <code>int</code> types.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Int extends AbstractSyntacticItem implements Primitive {
			public Int() {
				super(TYPE_int);
			}

			@Override
			public Int clone(SyntacticItem[] operands) {
				return new Int();
			}

			@Override
			public String toString() {
				return "int";
			}
		}

		/**
		 * Represents an array type, which is of the form:
		 *
		 * <pre>
		 * ArrayType ::= Type '[' ']'
		 * </pre>
		 *
		 * An array type describes array values whose elements are subtypes of the
		 * element type. For example, <code>[1,2,3]</code> is an instance of array type
		 * <code>int[]</code>; however, <code>[false]</code> is not.
		 *
		 * @return
		 */
		public static class Array extends AbstractSyntacticItem implements Atom {
			public Array(Type element) {
				super(TYPE_array, element);
			}

			public Type getElement() {
				return (Type) get(0);
			}

			@Override
			public Array clone(SyntacticItem[] operands) {
				return new Array((Type) operands[0]);
			}

			@Override
			public String toString() {
				return braceAsNecessary(getElement()) + "[]";
			}
		}

		/**
		 * Parse a reference type, which is of the form:
		 *
		 * <pre>
		 * ReferenceType ::= '&' Type
		 * </pre>
		 *
		 * Represents a reference to an object in Whiley. For example,
		 * <code>&this:int</code> is the type of a reference to a location allocated in
		 * the enclosing scope which holds an integer value.
		 *
		 * @return
		 */
		public static class Reference extends AbstractSyntacticItem implements Atom {
			public Reference(Type element) {
				super(TYPE_reference, element);
			}

			public Type getElement() {
				return (Type) get(0);
			}

			@Override
			public Reference clone(SyntacticItem[] operands) {
				return new Reference((Type) operands[0]);
			}

			@Override
			public String toString() {
				return "&" + braceAsNecessary(getElement());
			}
		}

		/**
		 * Represents record type, which is of the form:
		 *
		 * <pre>
		 * RecordType ::= '{' Type Identifier (',' Type Identifier)* [ ',' "..." ] '}'
		 * </pre>
		 *
		 * A record is made up of a number of fields, each of which has a unique name.
		 * Each field has a corresponding type. One can think of a record as a special
		 * kind of "fixed" map (i.e. where we know exactly which entries we have).
		 *
		 * @return
		 */
		public static class Record extends AbstractSyntacticItem implements Atom {
			public Record(boolean isOpen, Tuple<Decl.Variable> fields) {
				this(new Value.Bool(isOpen), fields);
			}

			public Record(Value.Bool isOpen, Tuple<Decl.Variable> fields) {
				super(TYPE_record, isOpen, fields);
			}

			private Record(SyntacticItem[] operands) {
				super(TYPE_record, operands);
			}

			public boolean isOpen() {
				Value.Bool flag = (Value.Bool) get(0);
				return flag.get();
			}

			@SuppressWarnings("unchecked")
			public Tuple<Decl.Variable> getFields() {
				return (Tuple<Decl.Variable>) get(1);
			}

			public Type getField(Identifier fieldName) {
				Tuple<Decl.Variable> fields = getFields();
				for (int i = 0; i != fields.size(); ++i) {
					Decl.Variable vd = fields.get(i);
					Identifier declaredFieldName = vd.getName();
					if (declaredFieldName.equals(fieldName)) {
						return vd.getType();
					}
				}
				return null;
			}

			@Override
			public Record clone(SyntacticItem[] operands) {
				return new Record(operands);
			}

			@Override
			public String toString() {
				String r = "{";
				Tuple<Decl.Variable> fields = getFields();
				for (int i = 0; i != fields.size(); ++i) {
					if (i != 0) {
						r += ",";
					}
					Decl.Variable field = fields.get(i);
					r += field.getType() + " " + field.getName();
				}
				if (isOpen()) {
					if (fields.size() > 0) {
						r += ", ...";
					} else {
						r += "...";
					}
				}
				return r + "}";
			}
		}

		/**
		 * Represents a nominal type, which is of the form:
		 *
		 * <pre>
		 * NominalType ::= Identifier ('.' Identifier)*
		 * </pre>
		 *
		 * A nominal type specifies the name of a type defined elsewhere. In some cases,
		 * this type can be expanded (or "inlined"). However, visibility modifiers can
		 * prevent this and, thus, give rise to true nominal types.
		 *
		 * @return
		 */
		public static class Nominal extends AbstractSyntacticItem implements Type {

			public Nominal(Name name) {
				super(TYPE_nominal, name);
			}

			public Name getName() {
				return (Name) get(0);
			}

			public void setName(Name name) {
				operands[1] = name;
			}

			@Override
			public Nominal clone(SyntacticItem[] operands) {
				return new Nominal((Name) operands[0]);
			}

			@Override
			public String toString() {
				return getName().toString();
			}
		}

		/**
		 * Represents a union type, which is of the form:
		 *
		 * <pre>
		 * UnionType ::= IntersectionType ('|' IntersectionType)*
		 * </pre>
		 *
		 * Union types are used to compose types together. For example, the type
		 * <code>int|null</code> represents the type which is either an <code>int</code>
		 * or <code>null</code>.
		 *
		 * Represents the union of one or more types together. For example, the union of
		 * <code>int</code> and <code>null</code> is <code>int|null</code>. Any variable
		 * of this type may hold any integer or the null value. Furthermore, the types
		 * <code>int</code> and <code>null</code> are collectively referred to as the
		 * "bounds" of this type.
		 *
		 * @return
		 */
		public static class Union extends AbstractSyntacticItem implements Type {
			public Union(Type... types) {
				super(TYPE_union, types);
			}

			@Override
			public Type get(int index) {
				return (Type) super.get(index);
			}

			@Override
			public Type[] getAll() {
				return (Type[]) super.operands;
			}

			@Override
			public Union clone(SyntacticItem[] operands) {
				return new Union(ArrayUtils.toArray(Type.class, operands));
			}

			@Override
			public String toString() {
				String r = "";
				for (int i = 0; i != size(); ++i) {
					if (i != 0) {
						r += "|";
					}
					r += get(i);
				}
				return "(" + r + ")";
			}
		}

		/**
		 * Represents the set of all method values. These are impure and may have
		 * side-effects (e.g. performing I/O, updating non-local state, etc). A method
		 * may have zero returns and, in such case, the effect of a method comes through
		 * other side-effects. Methods may also have captured lifetime arguments, and
		 * may themselves declare lifetime arguments.
		 *
		 * @author David J. Pearce
		 *
		 */
		public static class Method extends AbstractSyntacticItem implements Type {

			public Method(Tuple<Type> parameters, Type returns) {
				super(TYPE_method, parameters, returns);
			}

			@SuppressWarnings("unchecked")
			public Tuple<Type> getParameters() {
				return (Tuple<Type>) get(0);
			}

			@SuppressWarnings("unchecked")
			public Type getReturn() {
				return (Type) get(1);
			}

			@Override
			public String toString() {
				String r = "method";
				return r + getParameters().toString() + "->" + getReturn();
			}

			@SuppressWarnings("unchecked")
			@Override
			public Method clone(SyntacticItem[] operands) {
				return new Method((Tuple<Type>) operands[0], (Type) operands[1]);
			}
		}
	}

	// ============================================================
	// Modifiers
	// ============================================================

	/**
	 * <p>
	 * Represents an arbitrary modifier on a declaration. For example, all
	 * declarations (e.g. functions, types, etc) can be marked as
	 * <code>public</code> or <code>private</code>. The following illustrates:
	 * </p>
	 *
	 * <pre>
	 * public function square(int x, int y) -> int:
	 *    return x * y
	 * </pre>
	 * <p>
	 * The <code>public</code> modifier used above indicates that the function
	 * <code>square</code> can be accessed from outside its enclosing module.
	 * </p>
	 * <p>
	 * The modifiers <code>native</code> and <code>export</code> are used to enable
	 * inter-operation with other languages. By declaring a function or method as
	 * <code>native</code> you are signaling that its implementation is provided
	 * elsewhere (e.g. it's implemented in Java code directly). By marking a
	 * function or method with <code>export</code>, you are declaring that external
	 * code may call it. For example, you have some Java code that needs to call it.
	 * The modifier is required because, by default, all the names of all methods
	 * and functions are <i>mangled</i> to include type information and enable
	 * overloading. Therefore, a method/function marked with <code>export</code>
	 * will generate a function without name mangling.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Modifier extends SyntacticItem {

		public static final class Public extends AbstractSyntacticItem implements Modifier {
			public Public() {
				super(MOD_public);
			}

			@Override
			public String toString() {
				return "public";
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Public();
			}
		}

		public static final class Private extends AbstractSyntacticItem implements Modifier {
			public Private() {
				super(MOD_private);
			}

			@Override
			public String toString() {
				return "private";
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Private();
			}
		}

		public static final class Native extends AbstractSyntacticItem implements Modifier {
			public Native() {
				super(MOD_native);
			}

			@Override
			public String toString() {
				return "native";
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Native();
			}
		}

		public static final class Export extends AbstractSyntacticItem implements Modifier {
			public Export() {
				super(MOD_export);
			}

			@Override
			public String toString() {
				return "export";
			}

			@Override
			public SyntacticItem clone(SyntacticItem[] operands) {
				return new Export();
			}
		}
	}

	// ==============================================================================
	//
	// ==============================================================================

	private static String braceAsNecessary(Type type) {
		String str = type.toString();
		if (needsBraces(type)) {
			return "(" + str + ")";
		} else {
			return str;
		}
	}

	private static boolean needsBraces(Type type) {
		if (type instanceof Type.Atom || type instanceof Type.Nominal) {
			return false;
		} else {
			return true;
		}
	}

	// =========================================================================
	// Schema
	// =========================================================================
	private static volatile SyntacticItem.Schema[] SCHEMA = null;

	public static SyntacticItem.Schema[] getSchema() {
		if (SCHEMA == null) {
			SCHEMA = createSchema();
		}
		return SCHEMA;
	}

	private static SyntacticItem.Schema[] createSchema() {
		SyntacticItem.Schema[] schema = AbstractCompilationUnit.getSchema();
		schema = Arrays.copyOf(schema, 256);
		// ==========================================================================
		schema[DECL_module] = new Schema(Operands.TWO, Data.ZERO, "DECL_module") {
			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Decl.Module((Name) operands[0], (Tuple<Decl>) operands[1]);
			}
		};
//		schema[DECL_import] = new Schema(Operands.ONE, Data.ZERO, "DECL_import") {
//			@Override
//			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
//				return new Decl.Import((Tuple<Identifier>) operands[0]);
//			}
//		};
//		schema[DECL_importfrom] = new Schema(Operands.TWO, Data.ZERO, "DECL_importfrom") {
//			@Override
//			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
//				return new Decl.Import((Tuple<Identifier>) operands[0], (Identifier) operands[1]);
//			}
//		};
		schema[DECL_staticvar] = new Schema(Operands.FOUR, Data.ZERO, "DECL_staticvar") {
			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Decl.StaticVariable((Tuple<Modifier>) operands[0], (Identifier) operands[1],
						(Type) operands[2], (Expr) operands[3]);
			}
		};
		schema[DECL_type] = new Schema(Operands.THREE, Data.ZERO, "DECL_type") {
			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Decl.Type((Tuple<Modifier>) operands[0], (Identifier) operands[1],
						(Type) operands[2],false);
			}
		};
		schema[DECL_rectype] = new Schema(Operands.THREE, Data.ZERO, "DECL_rectype") {
			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				Decl.Type r = new Decl.Type((Tuple<Modifier>) operands[0], (Identifier) operands[1],
						(Type) operands[2],true);
				return r;
			}
		};
		schema[DECL_method] = new Schema(Operands.FIVE, Data.ZERO, "DECL_method") {
			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Decl.Method((Tuple<Modifier>) operands[0], (Identifier) operands[1],
						(Tuple<Decl.Variable>) operands[2], (Type) operands[3], (Stmt.Block) operands[4]);
			}
		};
//		schema[DECL_lambda] = new Schema(Operands.EIGHT, Data.ZERO, "DECL_lambda") {
//			@SuppressWarnings("unchecked")
//			@Override
//			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
//				return new Decl.Lambda((Tuple<Modifier>) operands[0], (Identifier) operands[1],
//						(Tuple<Decl.Variable>) operands[2], (Tuple<Decl.Variable>) operands[3],
//						(Tuple<Identifier>) operands[4], (Tuple<Identifier>) operands[5], (Expr) operands[6],
//						(Type.Callable) operands[7]);
//			}
//		};
		schema[DECL_variable] = new Schema(Operands.THREE, Data.ZERO, "DECL_var") {
			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Decl.Variable((Tuple<Modifier>) operands[0], (Identifier) operands[1], (Type) operands[2]);
			}
		};
		schema[DECL_variableinitialiser] = new Schema(Operands.FOUR, Data.ZERO, "DECL_varinit") {
			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Decl.Variable((Tuple<Modifier>) operands[0], (Identifier) operands[1], (Type) operands[2],
						(Expr) operands[3]);
			}
		};
		schema[MOD_native] = new Schema(Operands.ZERO, Data.ZERO, "MOD_native") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Modifier.Native();
			}
		};
		schema[MOD_export] = new Schema(Operands.ZERO, Data.ZERO, "MOD_export") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Modifier.Export();
			}
		};
		schema[MOD_private] = new Schema(Operands.ZERO, Data.ZERO, "MOD_private") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Modifier.Private();
			}
		};
		schema[MOD_public] = new Schema(Operands.ZERO, Data.ZERO, "MOD_public") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Modifier.Public();
			}
		};
		// TYPES: 00100000 (32) -- 00111111 (63)
		schema[TYPE_void] = new Schema(Operands.ZERO, Data.ZERO, "TYPE_void") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Type.Void();
			}
		};
		schema[TYPE_null] = new Schema(Operands.ZERO, Data.ZERO, "TYPE_null") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Type.Null();
			}
		};
		schema[TYPE_bool] = new Schema(Operands.ZERO, Data.ZERO, "TYPE_bool") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Type.Bool();
			}
		};
		schema[TYPE_int] = new Schema(Operands.ZERO, Data.ZERO, "TYPE_int") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Type.Int();
			}
		};
		schema[TYPE_nominal] = new Schema(Operands.ONE, Data.ZERO, "TYPE_nominal") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Type.Nominal((Name) operands[0]);
			}
		};
		schema[TYPE_reference] = new Schema(Operands.ONE, Data.ZERO, "TYPE_reference") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Type.Reference((Type) operands[0]);
			}
		};
		schema[TYPE_array] = new Schema(Operands.ONE, Data.ZERO, "TYPE_array") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Type.Array((Type) operands[0]);
			}
		};
		schema[TYPE_record] = new Schema(Operands.TWO, Data.ZERO, "TYPE_record") {
			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Type.Record((Value.Bool) operands[0], (Tuple<Decl.Variable>) operands[1]);
			}
		};
		schema[TYPE_method] = new Schema(Operands.TWO, Data.ZERO, "TYPE_method") {
			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Type.Method((Tuple<Type>) operands[0], (Type) operands[1]);
			}
		};
		schema[TYPE_union] = new Schema(Operands.MANY, Data.ZERO, "TYPE_union") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Type.Union(ArrayUtils.toArray(Type.class, operands));
			}
		};
//		schema[TYPE_recursive] = new Schema(Operands.ONE, Data.ZERO, "TYPE_recursive") {
//			@Override
//			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
//				return new Type.Recursive((Ref<Type>) operands[0]);
//			}
//		};
		// STATEMENTS: 01000000 (64) -- 001011111 (95)
		schema[STMT_block] = new Schema(Operands.MANY, Data.ZERO, "STMT_block") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.Block(ArrayUtils.toArray(Stmt.class, operands));
			}
		};
		schema[STMT_caseblock] = new Schema(Operands.TWO, Data.ZERO, "STMT_caseblock") {
			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.Case((Tuple<Value.Int>) operands[0], (Stmt.Block) operands[1]);
			}
		};
		schema[STMT_assert] = new Schema(Operands.ONE, Data.ZERO, "STMT_assert") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.Assert((Expr) operands[0]);
			}
		};
		schema[STMT_assign] = new Schema(Operands.TWO, Data.ZERO, "STMT_assign") {
			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.Assign((LVal) operands[0], (Expr) operands[1]);
			}
		};
//		schema[STMT_debug] = new Schema(Operands.ONE, Data.ZERO, "STMT_debug") {
//			@Override
//			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
//				return new Stmt.Debug((Expr) operands[0]);
//			}
//		};
//		schema[STMT_skip] = new Schema(Operands.ZERO, Data.ZERO, "STMT_skip") {
//			@Override
//			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
//				return new Stmt.Skip();
//			}
//		};
		schema[STMT_break] = new Schema(Operands.ZERO, Data.ZERO, "STMT_break") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.Break();
			}
		};
		schema[STMT_continue] = new Schema(Operands.ZERO, Data.ZERO, "STMT_continue") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.Continue();
			}
		};
		schema[STMT_dowhile] = new Schema(Operands.TWO, Data.ZERO, "STMT_dowhile") {
			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.DoWhile((Expr) operands[0], (Stmt.Block) operands[1]);
			}
		};
		schema[STMT_fail] = new Schema(Operands.ZERO, Data.ZERO, "STMT_fail") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.Fail();
			}
		};
		schema[STMT_foreach] = new Schema(Operands.FOUR, Data.ZERO, "STMT_fail") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.ForEach((Decl.Variable) operands[0], (Expr) operands[1], (Expr) operands[2],
						(Stmt.Block) operands[3]);
			}
		};
		schema[STMT_if] = new Schema(Operands.ONE, Data.ZERO, "STMT_if") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.IfElse((Tuple<Pair<Expr,Stmt.Block>>) operands[0]);
			}
		};
		schema[STMT_ifelse] = new Schema(Operands.TWO, Data.ZERO, "STMT_ifelse") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.IfElse((Tuple<Pair<Expr,Stmt.Block>>) operands[0], (Stmt.Block) operands[1]);
			}
		};
		schema[STMT_return] = new Schema(Operands.MANY, Data.ZERO, "STMT_return") {
			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.Return((Expr) operands[0]);
			}
		};
		schema[STMT_switch] = new Schema(Operands.TWO, Data.ZERO, "STMT_switch") {
			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.Switch((Expr) operands[0], (Tuple<Stmt.Case>) operands[1]);
			}
		};
		schema[STMT_while] = new Schema(Operands.TWO, Data.ZERO, "STMT_while") {
			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.While((Expr) operands[0], (Stmt.Block) operands[1]);
			}
		};
		// EXPRESSIONS: 01100000 (96) -- 10011111 (159)
		schema[EXPR_variableaccess] = new Schema(Operands.TWO, Data.ZERO, "EXPR_variableaccess") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.VariableAccess((Type) operands[0], (Identifier) operands[1]);
			}
		};
		schema[EXPR_staticvariable] = new Schema(Operands.TWO, Data.ZERO, "EXPR_staticvariable") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.StaticVariableAccess((Type) operands[0], (Name) operands[1]);
			}
		};
		schema[EXPR_nullconstant] = new Schema(Operands.ZERO, Data.ZERO, "EXPR_nullconstant") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.NullConstant();
			}
		};
		schema[EXPR_boolconstant] = new Schema(Operands.TWO, Data.ZERO, "EXPR_boolconstant") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.BoolConstant((Value.Bool) operands[0]);
			}
		};
		schema[EXPR_intconstant] = new Schema(Operands.TWO, Data.ZERO, "EXPR_intconstant") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.IntConstant((Type.Int) operands[0], (Value.Int) operands[1]);
			}
		};
//		schema[EXPR_cast] = new Schema(Operands.TWO, Data.ZERO, "EXPR_cast") {
//			@Override
//			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
//				return new Expr.Cast((Type) operands[0], (Expr) operands[1]);
//			}
//		};
		schema[EXPR_invoke] = new Schema(Operands.THREE, Data.ZERO, "EXPR_invoke") {
			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.Invoke((Name) operands[0], (Tuple<Expr>) operands[1], (Type.Method) operands[2]);
			}
		};
		schema[EXPR_indirectinvoke] = new Schema(Operands.THREE, Data.ZERO, "EXPR_indirectinvoke") {
			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.IndirectInvoke((Type) operands[0], (Expr) operands[1], (Tuple<Expr>) operands[2]);
			}
		};
		// LOGICAL
		schema[EXPR_logicalnot] = new Schema(Operands.ONE, Data.ZERO, "EXPR_logicalnot") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.LogicalNot((Expr) operands[0]);
			}
		};
		schema[EXPR_logicaland] = new Schema(Operands.ONE, Data.ZERO, "EXPR_logicaland") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.LogicalAnd((Tuple<Expr>) operands[0]);
			}
		};
		schema[EXPR_logicalor] = new Schema(Operands.ONE, Data.ZERO, "EXPR_logicalor") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.LogicalOr((Tuple<Expr>) operands[0]);
			}
		};
		// UNIONS
		schema[EXPR_unionaccess] = new Schema(Operands.TWO, Data.ZERO, "EXPR_unionaccess") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.UnionAccess((Type) operands[0], (Expr) operands[1]);
			}
		};
		// COMPARATORS
		schema[EXPR_equal] = new Schema(Operands.TWO, Data.ZERO, "EXPR_equal") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.Equal((Expr) operands[0], (Expr) operands[1]);
			}
		};
		schema[EXPR_notequal] = new Schema(Operands.TWO, Data.ZERO, "EXPR_notequal") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.NotEqual((Expr) operands[0], (Expr) operands[1]);
			}
		};
		schema[EXPR_integerlessthan] = new Schema(Operands.TWO, Data.ZERO, "EXPR_integerlessthan") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.IntegerLessThan((Expr) operands[0], (Expr) operands[1]);
			}
		};
		schema[EXPR_integerlessequal] = new Schema(Operands.TWO, Data.ZERO, "EXPR_integerlessequal") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.IntegerLessThanOrEqual((Expr) operands[0], (Expr) operands[1]);
			}
		};
		schema[EXPR_integergreaterthan] = new Schema(Operands.TWO, Data.ZERO, "EXPR_integergreaterthan") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.IntegerGreaterThan((Expr) operands[0], (Expr) operands[1]);
			}
		};
		schema[EXPR_integergreaterequal] = new Schema(Operands.TWO, Data.ZERO, "EXPR_integergreaterequal") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.IntegerGreaterThanOrEqual((Expr) operands[0], (Expr) operands[1]);
			}
		};
		// ARITHMETIC
		schema[EXPR_integernegation] = new Schema(Operands.TWO, Data.ZERO, "EXPR_integernegation") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.IntegerNegation((Type) operands[0], (Expr) operands[1]);
			}
		};
		schema[EXPR_integeraddition] = new Schema(Operands.THREE, Data.ZERO, "EXPR_integeraddition") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.IntegerAddition((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}
		};
		schema[EXPR_integersubtraction] = new Schema(Operands.THREE, Data.ZERO, "EXPR_integersubtraction") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.IntegerSubtraction((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}
		};
		schema[EXPR_integermultiplication] = new Schema(Operands.THREE, Data.ZERO, "EXPR_integermultiplication") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.IntegerMultiplication((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}
		};
		schema[EXPR_integerdivision] = new Schema(Operands.THREE, Data.ZERO, "EXPR_integerdivision") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.IntegerDivision((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}
		};
		schema[EXPR_integerremainder] = new Schema(Operands.THREE, Data.ZERO, "EXPR_integerremainder") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.IntegerRemainder((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}
		};
		// BITWISE
		schema[EXPR_bitwisenot] = new Schema(Operands.TWO, Data.ZERO, "EXPR_bitwisenot") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.BitwiseComplement((Type) operands[0], (Expr) operands[1]);
			}
		};
		schema[EXPR_bitwiseand] = new Schema(Operands.TWO, Data.ZERO, "EXPR_bitwiseand") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.BitwiseAnd((Type) operands[0], (Tuple<Expr>) operands[1]);
			}
		};
		schema[EXPR_bitwiseor] = new Schema(Operands.TWO, Data.ZERO, "EXPR_bitwiseor") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.BitwiseOr((Type) operands[0], (Tuple<Expr>) operands[1]);
			}
		};
		schema[EXPR_bitwisexor] = new Schema(Operands.TWO, Data.ZERO, "EXPR_bitwisexor") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.BitwiseXor((Type) operands[0], (Tuple<Expr>) operands[1]);
			}
		};
		schema[EXPR_bitwiseshl] = new Schema(Operands.THREE, Data.ZERO, "EXPR_bitwiseshl") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.BitwiseShiftLeft((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}
		};
		schema[EXPR_bitwiseshr] = new Schema(Operands.THREE, Data.ZERO, "EXPR_bitwiseshr") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.BitwiseShiftRight((Type) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}
		};
		// REFERENCES
		schema[EXPR_dereference] = new Schema(Operands.TWO, Data.ZERO, "EXPR_dereference") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.Dereference((Type) operands[0], (Expr) operands[1]);
			}
		};
		schema[EXPR_new] = new Schema(Operands.TWO, Data.ZERO, "EXPR_new") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.New((Type) operands[0], (Expr) operands[1]);
			}
		};
		schema[EXPR_lambdaaccess] = new Schema(Operands.TWO, Data.ZERO, "EXPR_lambdaaccess") {
			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.LambdaAccess((Name) operands[0], (Type.Method) operands[1]);
			}
		};
		// RECORDS
		schema[EXPR_recordaccess] = new Schema(Operands.THREE, Data.ZERO, "EXPR_recordaccess") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.RecordAccess((Type.Record) operands[0], (Expr) operands[1], (Identifier) operands[2]);
			}
		};
		schema[EXPR_recordinitialiser] = new Schema(Operands.THREE, Data.ZERO, "EXPR_recordinitialiser") {
			@SuppressWarnings("unchecked")
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.RecordInitialiser((Type.Record) operands[0], (Tuple<Identifier>) operands[1],
						(Tuple<Expr>) operands[2]);
			}
		};
		// ARRAYS
		schema[EXPR_arrayaccess] = new Schema(Operands.THREE, Data.ZERO, "EXPR_arrayaccess") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.ArrayAccess((Type.Array) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}
		};
		schema[EXPR_arraylength] = new Schema(Operands.TWO, Data.ZERO, "EXPR_arraylength") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.ArrayLength((Type.Array) operands[0], (Expr) operands[1]);
			}
		};
		schema[EXPR_arraygenerator] = new Schema(Operands.THREE, Data.ZERO, "EXPR_arraygenerator") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.ArrayGenerator((Type.Array) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}
		};
		schema[EXPR_arrayinitialiser] = new Schema(Operands.TWO, Data.ZERO, "EXPR_arrayinitialiser") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.ArrayInitialiser((Type.Array) operands[0], (Tuple<Expr>) operands[1]);
			}
		};
		return schema;
	}
}
