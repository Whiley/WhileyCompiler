// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyil.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;

import wybs.lang.CompilationUnit;
import wybs.lang.SyntacticItem;
import wybs.lang.SyntacticItem.Data;
import wybs.lang.SyntacticItem.Operands;
import wybs.lang.SyntacticItem.Schema;
import wybs.util.AbstractCompilationUnit;
import wyc.util.AbstractWhileyFile;
import wyc.util.AbstractWhileyFile.Stmt.Case;
import wyc.util.AbstractWhileyFile.Stmt.Switch;
import wycc.util.ArrayUtils;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.lang.Path.Entry;
import wyil.io.WyilFileReader;
import wyil.io.WyilFileWriter;

public class WyilFile extends AbstractWhileyFile<WyilFile> {

	// =========================================================================
	// Content Type
	// =========================================================================

	public static final Content.Type<WyilFile> ContentType = new Content.Type<WyilFile>() {

		/**
		 * This method simply parses a whiley file into an abstract syntax tree.
		 * It makes little effort to check whether or not the file is
		 * syntactically correct. In particular, it does not determine the
		 * correct type of all declarations, expressions, etc.
		 *
		 * @param file
		 * @return
		 * @throws IOException
		 */
		@Override
		public WyilFile read(Path.Entry<WyilFile> e, InputStream input) throws IOException {
			WyilFile wf = new WyilFileReader(e).read();
			// new SyntacticHeapPrinter(new PrintWriter(System.out)).print(wf);
			return wf;
		}

		@Override
		public void write(OutputStream output, WyilFile value) throws IOException {
			new WyilFileWriter(output).write(value);
		}

		@Override
		public String toString() {
			return "Content-Type: wyil";
		}

		@Override
		public String getSuffix() {
			return "wyil";
		}
	};

	// =========================================================================
	// Constructors
	// =========================================================================

	public WyilFile(Entry<WyilFile> entry) {
		super(entry);
	}

	public WyilFile(Entry<WyilFile> entry, CompilationUnit other) {
		super(entry, other);
	}

	public WyilFile(Entry<WyilFile> entry, SyntacticItem[] items) {
		super(entry);
		for (int i = 0; i != items.length; ++i) {
			syntacticItems.add(items[i]);
			items[i].allocate(this, i);
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
		schema[DECL_module] = new Schema(Operands.ONE, Data.ZERO, "DECL_import") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Declaration.Module((Tuple<Declaration>) operands[0]);
			}
		};
		schema[DECL_import] = new Schema(Operands.MANY, Data.ZERO, "DECL_import") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Declaration.Import(ArrayUtils.toArray(Identifier.class, operands));
			}
		};
		schema[DECL_constant] = new Schema(Operands.THREE, Data.ZERO, "DECL_constant") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Declaration.Constant((Tuple<Modifier>) operands[0], (Identifier) operands[1],
						(Expr) operands[2]);
			}
		};
		schema[DECL_type] = new Schema(Operands.FOUR, Data.ZERO, "DECL_type") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Declaration.Type((Tuple<Modifier>) operands[0], (Identifier) operands[1],
						(Declaration.Variable) operands[2], (Tuple<Expr>) operands[3]);
			}
		};
		schema[DECL_function] = new Schema(Operands.SEVEN, Data.ZERO, "DECL_function") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Declaration.Function((Tuple<Modifier>) operands[0], (Identifier) operands[1],
						(Tuple<Declaration.Variable>) operands[2], (Tuple<Declaration.Variable>) operands[3],
						(Tuple<Expr>) operands[4], (Tuple<Expr>) operands[5], (Stmt.Block) operands[6]);
			}
		};
		schema[DECL_method] = new Schema(Operands.EIGHT, Data.ZERO, "DECL_method") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Declaration.Method((Tuple<Modifier>) operands[0], (Identifier) operands[1],
						(Tuple<Declaration.Variable>) operands[2], (Tuple<Declaration.Variable>) operands[3],
						(Tuple<Expr>) operands[4], (Tuple<Expr>) operands[5], (Stmt.Block) operands[6],
						(Tuple<Identifier>) operands[7]);
			}
		};
		schema[DECL_property] = new Schema(Operands.FIVE, Data.ZERO, "DECL_property") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Declaration.Property((Tuple<Modifier>) operands[0], (Identifier) operands[1],
						(Tuple<Declaration.Variable>) operands[2], (Tuple<Declaration.Variable>) operands[3],
						(Tuple<Expr>) operands[4]);
			}
		};
		schema[DECL_lambda] = new Schema(Operands.SEVEN, Data.ZERO, "EXPR_lambdainit") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Declaration.Lambda((Tuple<Modifier>) operands[0], (Identifier) operands[1],
						(Tuple<Declaration.Variable>) operands[2], (Tuple<Declaration.Variable>) operands[3],
						(Tuple<Identifier>) operands[4], (Tuple<Identifier>) operands[5], (Expr) operands[6]);
			}
		};
		schema[DECL_variable] = new Schema(Operands.THREE, Data.ZERO, "DECL_variable") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Declaration.Variable((Tuple<Modifier>) operands[0], (Identifier) operands[1],
						(Type) operands[2]);
			}
		};
		schema[DECL_variableinitialiser] = new Schema(Operands.FOUR, Data.ZERO, "DECL_variableinitialiser") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Declaration.Variable((Tuple<Modifier>) operands[0], (Identifier) operands[1],
						(Type) operands[2], (Expr) operands[3]);
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
		schema[TYPE_any] = new Schema(Operands.ZERO, Data.ZERO, "TYPE_any") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Type.Any();
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
		schema[TYPE_nom] = new Schema(Operands.ONE, Data.ZERO, "TYPE_nominal") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Type.Nominal((Name) operands[0]);
			}
		};
		schema[TYPE_ref] = new Schema(Operands.MANY, Data.ZERO, "TYPE_reference") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				// FIXME: many operand modifier is not optimal. Observe that,
				// for simplicity of subtyping, want to preserve reference types
				// as having the same opcode.
				if(operands.length == 1) {
					return new Type.Reference((Type) operands[0]);
				} else {
					return new Type.Reference((Type) operands[0], (Identifier) operands[1]);
				}
			}
		};
		schema[TYPE_arr] = new Schema(Operands.ONE, Data.ZERO, "TYPE_array") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Type.Array((Type) operands[0]);
			}
		};
		schema[TYPE_rec] = new Schema(Operands.TWO, Data.ZERO, "TYPE_record") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Type.Record((Value.Bool) operands[0], (Tuple<Declaration.Variable>) operands[1]);
			}
		};
		schema[TYPE_fun] = new Schema(Operands.TWO, Data.ZERO, "TYPE_function") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Type.Function((Tuple<Type>) operands[0], (Tuple<Type>) operands[1]);
			}
		};
		schema[TYPE_meth] = new Schema(Operands.FOUR, Data.ZERO, "TYPE_method") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Type.Method((Tuple<Type>) operands[0], (Tuple<Type>) operands[1],
						(Tuple<Identifier>) operands[2], (Tuple<Identifier>) operands[3]);
			}
		};
		schema[TYPE_property] = new Schema(Operands.TWO, Data.ZERO, "TYPE_property") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Type.Property((Tuple<Type>) operands[0], (Tuple<Type>) operands[1]);
			}
		};
		schema[TYPE_or] = new Schema(Operands.MANY, Data.ZERO, "TYPE_or") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Type.Union(ArrayUtils.toArray(Type.class, operands));
			}
		};
		schema[TYPE_and] = new Schema(Operands.MANY, Data.ZERO, "TYPE_and") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Type.Intersection(ArrayUtils.toArray(Type.class, operands));
			}
		};
		schema[TYPE_not] = new Schema(Operands.ONE, Data.ZERO, "TYPE_not") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Type.Negation((Type) operands[0]);
			}
		};
		schema[TYPE_byte] = new Schema(Operands.ZERO, Data.ZERO, "TYPE_byte") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Type.Byte();
			}
		};
		// STATEMENTS: 01000000 (64) -- 001011111 (95)
		schema[STMT_block] = new Schema(Operands.MANY, Data.ZERO, "STMT_block") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.Block(ArrayUtils.toArray(Stmt.class, operands));
			}
		};
		schema[STMT_namedblock] = new Schema(Operands.TWO, Data.ZERO, "STMT_namedblock") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.NamedBlock((Identifier) operands[0], (Stmt.Block) operands[1]);
			}
		};
		schema[STMT_caseblock] = new Schema(Operands.TWO, Data.ZERO, "STMT_caseblock") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Case((Tuple<Expr>) operands[0], (Stmt.Block) operands[1]);
			}
		};
		schema[STMT_assert] = new Schema(Operands.ONE, Data.ZERO, "STMT_assert") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.Assert((Expr) operands[0]);
			}
		};
		schema[STMT_assign] = new Schema(Operands.TWO, Data.ZERO, "STMT_assign") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.Assign((Tuple<LVal>) operands[0], (Tuple<Expr>) operands[1]);
			}
		};
		schema[STMT_assume] = new Schema(Operands.ONE, Data.ZERO, "STMT_assume") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.Assume((Expr) operands[0]);
			}
		};
		schema[STMT_debug] = new Schema(Operands.ONE, Data.ZERO, "STMT_debug") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.Debug((Expr) operands[0]);
			}
		};
		schema[STMT_skip] = new Schema(Operands.ZERO, Data.ZERO, "STMT_skip") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.Skip();
			}
		};
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
		schema[STMT_dowhile] = new Schema(Operands.THREE, Data.ZERO, "STMT_dowhile") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.DoWhile((Expr) operands[0], (Tuple<Expr>) operands[1], (Stmt.Block) operands[2]);
			}
		};
		schema[STMT_fail] = new Schema(Operands.ZERO, Data.ZERO, "STMT_fail") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.Fail();
			}
		};
		schema[STMT_if] = new Schema(Operands.TWO, Data.ZERO, "STMT_if") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.IfElse((Expr) operands[0], (Stmt.Block) operands[1]);
			}
		};
		schema[STMT_ifelse] = new Schema(Operands.THREE, Data.ZERO, "STMT_ifelse") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.IfElse((Expr) operands[0], (Stmt.Block) operands[1], (Stmt.Block) operands[2]);
			}
		};
		schema[STMT_return] = new Schema(Operands.MANY, Data.ZERO, "STMT_return") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.Return((Tuple<Expr>) operands[0]);
			}
		};
		schema[STMT_switch] = new Schema(Operands.TWO, Data.ZERO, "STMT_switch") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Switch((Expr) operands[0], (Tuple<Case>) operands[1]);
			}
		};
		schema[STMT_while] = new Schema(Operands.THREE, Data.ZERO, "STMT_while") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Stmt.While((Expr) operands[0], (Tuple<Expr>) operands[1], (Stmt.Block) operands[2]);
			}
		};
		// EXPRESSIONS: 01100000 (96) -- 10011111 (159)
		schema[EXPR_var] = new Schema(Operands.ONE, Data.ZERO, "EXPR_variable") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.VariableAccess((Declaration.Variable) operands[0]);
			}
		};
		schema[EXPR_staticvar] = new Schema(Operands.ONE, Data.ZERO, "EXPR_staticvariable") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.StaticVariableAccess((Name) operands[0]);
			}
		};
		schema[EXPR_const] = new Schema(Operands.ONE, Data.ZERO, "EXPR_const") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.Constant((Value) operands[0]);
			}
		};
		schema[EXPR_cast] = new Schema(Operands.TWO, Data.ZERO, "EXPR_cast") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.Cast((Type) operands[0], (Expr) operands[1]);
			}
		};
		schema[EXPR_invoke] = new Schema(Operands.THREE, Data.ZERO, "EXPR_invoke") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				// FIXME: update to WyilFile.Invoke
				return new AbstractWhileyFile.Expr.Invoke((Name) operands[0], (Tuple<Identifier>) operands[1],
						(Tuple<Expr>) operands[2]);
			}
		};
		schema[EXPR_qualifiedinvoke] = new Schema(Operands.FOUR, Data.ZERO, "EXPR_qualifiedinvoke") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				// FIXME: update to WyilFile.Invoke
				return new AbstractWhileyFile.Expr.Invoke((Name) operands[0], (Tuple<Identifier>) operands[1],
						(Tuple<Expr>) operands[2], (Type.Callable) operands[3]);
			}
		};
		schema[EXPR_indirectinvoke] = new Schema(Operands.THREE, Data.ZERO, "EXPR_indirectinvoke") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.IndirectInvoke((Expr) operands[0], (Tuple<Identifier>) operands[1],
						(Tuple<Expr>) operands[2]);
			}
		};
		// LOGICAL
		schema[EXPR_not] = new Schema(Operands.ONE, Data.ZERO, "EXPR_not") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.LogicalNot((Expr) operands[0]);
			}
		};
		schema[EXPR_and] = new Schema(Operands.MANY, Data.ZERO, "EXPR_and") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.LogicalAnd(ArrayUtils.toArray(Expr.class, operands));
			}
		};
		schema[EXPR_or] = new Schema(Operands.MANY, Data.ZERO, "EXPR_or") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.LogicalOr(ArrayUtils.toArray(Expr.class, operands));
			}
		};
		schema[EXPR_implies] = new Schema(Operands.TWO, Data.ZERO, "EXPR_implies") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.LogicalImplication(ArrayUtils.toArray(Expr.class, operands));
			}
		};
		schema[EXPR_iff] = new Schema(Operands.TWO, Data.ZERO, "EXPR_iff") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.LogicalIff(ArrayUtils.toArray(Expr.class, operands));
			}
		};
		schema[EXPR_exists] = new Schema(Operands.TWO, Data.ZERO, "EXPR_exists") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.ExistentialQuantifier((Tuple<Declaration.Variable>) operands[0], (Expr) operands[1]);
			}
		};
		schema[EXPR_forall] = new Schema(Operands.TWO, Data.ZERO, "EXPR_forall") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.UniversalQuantifier((Tuple<Declaration.Variable>) operands[0], (Expr) operands[1]);
			}
		};
		// COMPARATORS
		schema[EXPR_eq] = new Schema(Operands.MANY, Data.ZERO, "EXPR_eq") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.Equal(ArrayUtils.toArray(Expr.class, operands));
			}
		};
		schema[EXPR_neq] = new Schema(Operands.MANY, Data.ZERO, "EXPR_neq") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.NotEqual(ArrayUtils.toArray(Expr.class, operands));
			}
		};
		schema[EXPR_lt] = new Schema(Operands.MANY, Data.ZERO, "EXPR_lt") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.LessThan(ArrayUtils.toArray(Expr.class, operands));
			}
		};
		schema[EXPR_lteq] = new Schema(Operands.MANY, Data.ZERO, "EXPR_lteq") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.LessThanOrEqual(ArrayUtils.toArray(Expr.class, operands));
			}
		};
		schema[EXPR_gt] = new Schema(Operands.MANY, Data.ZERO, "EXPR_gt") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.GreaterThan(ArrayUtils.toArray(Expr.class, operands));
			}
		};
		schema[EXPR_gteq] = new Schema(Operands.MANY, Data.ZERO, "EXPR_gteq") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.GreaterThanOrEqual(ArrayUtils.toArray(Expr.class, operands));
			}
		};
		schema[EXPR_is] = new Schema(Operands.TWO, Data.ZERO, "EXPR_is") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.Is((Expr) operands[0], (Type) operands[1]);
			}
		};
		// ARITHMETIC
		schema[EXPR_neg] = new Schema(Operands.ONE, Data.ZERO, "EXPR_neg") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.Negation((Expr) operands[0]);
			}
		};
		schema[EXPR_add] = new Schema(Operands.MANY, Data.ZERO, "EXPR_add") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.Addition(ArrayUtils.toArray(Expr.class, operands));
			}
		};
		schema[EXPR_sub] = new Schema(Operands.MANY, Data.ZERO, "EXPR_sub") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.Subtraction(ArrayUtils.toArray(Expr.class, operands));
			}
		};
		schema[EXPR_mul] = new Schema(Operands.MANY, Data.ZERO, "EXPR_mul") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.Multiplication(ArrayUtils.toArray(Expr.class, operands));
			}
		};
		schema[EXPR_div] = new Schema(Operands.MANY, Data.ZERO, "EXPR_div") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.Division(ArrayUtils.toArray(Expr.class, operands));
			}
		};
		schema[EXPR_rem] = new Schema(Operands.MANY, Data.ZERO, "EXPR_rem") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.Remainder(ArrayUtils.toArray(Expr.class, operands));
			}
		};
		// BITWISE
		schema[EXPR_bitwisenot] = new Schema(Operands.ONE, Data.ZERO, "EXPR_bitwisenot") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.BitwiseComplement((Expr) operands[0]);
			}
		};
		schema[EXPR_bitwiseand] = new Schema(Operands.MANY, Data.ZERO, "EXPR_bitwiseand") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.BitwiseAnd(ArrayUtils.toArray(Expr.class, operands));
			}
		};
		schema[EXPR_bitwiseor] = new Schema(Operands.MANY, Data.ZERO, "EXPR_bitwiseor") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.BitwiseOr(ArrayUtils.toArray(Expr.class, operands));
			}
		};
		schema[EXPR_bitwisexor] = new Schema(Operands.MANY, Data.ZERO, "EXPR_bitwisexor") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.BitwiseXor(ArrayUtils.toArray(Expr.class, operands));
			}
		};
		schema[EXPR_bitwiseshl] = new Schema(Operands.TWO, Data.ZERO, "EXPR_bitwiseshl") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.BitwiseShiftLeft((Expr) operands[0], (Expr) operands[1]);
			}
		};
		schema[EXPR_bitwiseshr] = new Schema(Operands.TWO, Data.ZERO, "EXPR_bitwiseshr") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.BitwiseShiftRight((Expr) operands[0], (Expr) operands[1]);
			}
		};
		// REFERENCES
		schema[EXPR_deref] = new Schema(Operands.ONE, Data.ZERO, "EXPR_deref") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.Dereference((Expr) operands[0]);
			}
		};
		schema[EXPR_new] = new Schema(Operands.TWO, Data.ZERO, "EXPR_new") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.New((Expr) operands[0], (Identifier) operands[1]);
			}
		};
		schema[EXPR_lambda] = new Schema(Operands.TWO, Data.ZERO, "EXPR_lambda") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.LambdaAccess((Name) operands[0], (Tuple<Type>) operands[1]);
			}
		};
		schema[EXPR_qualifiedlambda] = new Schema(Operands.THREE, Data.ZERO, "EXPR_lambda") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.LambdaAccess((Name) operands[0], (Tuple<Type>) operands[1],
						(Type.Callable) operands[2]);
			}
		};

		// RECORDS
		schema[EXPR_recfield] = new Schema(Operands.TWO, Data.ZERO, "EXPR_recfield") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.RecordAccess((Expr) operands[0], (Identifier) operands[1]);
			}
		};
		schema[EXPR_recupdt] = new Schema(Operands.THREE, Data.ZERO, "EXPR_recupdt") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.RecordUpdate((Expr) operands[0], (Identifier) operands[1], (Expr) operands[2]);
			}
		};
		schema[EXPR_recinit] = new Schema(Operands.MANY, Data.ZERO, "EXPR_recinit") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.RecordInitialiser(ArrayUtils.toArray(Pair.class, operands));
			}
		};
		// ARRAYS
		schema[EXPR_arridx] = new Schema(Operands.TWO, Data.ZERO, "EXPR_arridx") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.ArrayAccess((Expr) operands[0], (Expr) operands[1]);
			}
		};
		schema[EXPR_arrlen] = new Schema(Operands.ONE, Data.ZERO, "EXPR_arrlen") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.ArrayLength((Expr) operands[0]);
			}
		};
		schema[EXPR_arrupdt] = new Schema(Operands.THREE, Data.ZERO, "EXPR_arrupdt") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.ArrayUpdate((Expr) operands[0], (Expr) operands[1], (Expr) operands[2]);
			}
		};
		schema[EXPR_arrgen] = new Schema(Operands.TWO, Data.ZERO, "EXPR_arrgen") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.ArrayGenerator((Expr) operands[0], (Expr) operands[1]);
			}
		};
		schema[EXPR_arrinit] = new Schema(Operands.MANY, Data.ZERO, "EXPR_arrinit") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.ArrayInitialiser(ArrayUtils.toArray(Expr.class, operands));
			}
		};
		schema[EXPR_arrrange] = new Schema(Operands.TWO, Data.ZERO, "EXPR_arrrange") {
			@Override
			public SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data) {
				return new Expr.ArrayRange((Expr) operands[0], (Expr) operands[1]);
			}
		};
		return schema;
	}
}
