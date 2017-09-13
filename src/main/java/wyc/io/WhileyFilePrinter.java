// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyc.io;

import java.io.*;
import java.util.*;

import wybs.lang.Build;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyc.lang.WhileyFile;
import static wyc.lang.WhileyFile.*;

/**
 * Writes the bytecodes of a WhileyFile in a textual from to a given file.
 *
 * @author David J. Pearce
 *
 */
public final class WhileyFilePrinter {
	private PrintWriter out;
	private boolean verbose = false;

	public WhileyFilePrinter(Build.Task builder) {

	}

	public WhileyFilePrinter(PrintWriter writer) {
		this.out = writer;
	}

	public WhileyFilePrinter(OutputStream stream) {
		this.out = new PrintWriter(new OutputStreamWriter(stream));
	}

	// ======================================================================
	// Configuration Methods
	// ======================================================================

	public void setVerbose(boolean flag) {
		this.verbose = flag;
	}

	// ======================================================================
	// Apply Method
	// ======================================================================

	public void apply(WhileyFile module) throws IOException {
		out.println();
		for(Decl d : module.getDeclarations()) {
			write(d,out);
		}
		out.flush();
	}

	private void write(Decl d, PrintWriter out) {
		if(d instanceof Decl.StaticVariable) {
			write((Decl.StaticVariable) d, out);
		} else if(d instanceof Decl.Type) {
			write((Decl.Type) d, out);
		} else if(d instanceof Decl.Property){
			write((Decl.Property) d, out);
		} else {
			write((Decl.FunctionOrMethod) d, out);
		}
	}

	private void write(Decl.StaticVariable decl, PrintWriter out) {
		writeModifiers(decl.getModifiers(), out);
		out.print(decl.getType());
		out.print(" ");
		out.println(decl.getName() + " = " + decl.getInitialiser());
	}

	private void write(Decl.Type decl, PrintWriter out) {
		writeModifiers(decl.getModifiers(), out);
		out.print("type " + decl.getName() + " is (");
		writeVariableDeclaration(0, decl.getVariableDeclaration(), out);
		out.println(")");
		for (Expr invariant : decl.getInvariant()) {
			out.print("where ");
			writeExpression(invariant, out);
			out.println();
		}
		out.println();
	}
	private void write(Decl.Property decl, PrintWriter out) {
		out.print("property ");
		out.print(decl.getName());
		writeParameters(decl.getParameters(),out);
	}

	private void write(Decl.FunctionOrMethod decl, PrintWriter out) {
		//
		writeModifiers(decl.getModifiers(), out);
		if (decl instanceof Decl.Function) {
			out.print("function ");
		} else {
			out.print("method ");
		}
		out.print(decl.getName());
		writeParameters(decl.getParameters(),out);
		if (decl.getReturns().size() != 0) {
			out.print(" -> ");
			writeParameters(decl.getReturns(),out);
		}
		//
		for (Expr precondition : decl.getRequires()) {
			out.println();
			out.print("requires ");
			writeExpression(precondition, out);
		}
		for (Expr postcondition : decl.getEnsures()) {
			out.println();
			out.print("ensures ");
			writeExpression(postcondition, out);
		}
		if (decl.getBody() != null) {
			out.println(": ");
			writeBlock(0, decl.getBody(), out);
		}
	}

	private void writeParameters(Tuple<Decl.Variable> parameters, PrintWriter out) {
		out.print("(");
		for (int i = 0; i != parameters.size(); ++i) {
			if (i != 0) {
				out.print(", ");
			}
			writeVariableDeclaration(0, parameters.getOperand(i), out);
		}
		out.print(")");
	}

	private void writeVariableDeclaration(int indent, Decl.Variable decl, PrintWriter out) {
		out.print(decl.getType());
		out.print(" ");
		out.print(decl.getName());
		if (decl.hasInitialiser()) {
			out.print(" = ");
			writeExpression(decl.getInitialiser(), out);
		}
		out.println();
	}

	private void writeBlock(int indent, Stmt.Block block, PrintWriter out) {
		for (int i = 0; i != block.size(); ++i) {
			writeStatement(indent, block.getOperand(i), out);
		}
	}

	@SuppressWarnings("unchecked")
	private void writeStatement(int indent, Stmt c, PrintWriter out) {
		tabIndent(indent+1,out);
		switch(c.getOpcode()) {
		case STMT_assert:
			writeAssert(indent, (Stmt.Assert) c, out);
			break;
		case STMT_assume:
			writeAssume(indent, (Stmt.Assume) c, out);
			break;
		case STMT_assign:
			writeAssign(indent, (Stmt.Assign) c, out);
			break;
		case STMT_break:
			writeBreak(indent, (Stmt.Break) c, out);
			break;
		case STMT_continue:
			writeContinue(indent, (Stmt.Continue) c, out);
			break;
		case STMT_debug:
			writeDebug(indent, (Stmt.Debug) c, out);
			break;
		case STMT_dowhile:
			writeDoWhile(indent, (Stmt.DoWhile) c, out);
			break;
		case STMT_fail:
			writeFail(indent, (Stmt.Fail) c, out);
			break;
		case STMT_if:
		case STMT_ifelse:
			writeIf(indent, (Stmt.IfElse) c, out);
			break;
		case STMT_namedblock:
			writeNamedBlock(indent, (Stmt.NamedBlock) c, out);
			break;
		case STMT_while:
			writeWhile(indent, (Stmt.While) c, out);
			break;
		case STMT_return:
			writeReturn(indent, (Stmt.Return) c, out);
			break;
		case STMT_skip:
			writeSkip(indent, (Stmt.Skip) c, out);
			break;
		case STMT_switch:
			writeSwitch(indent, (Stmt.Switch) c, out);
			break;
		case EXPR_indirectinvoke:
			writeIndirectInvoke((Expr.IndirectInvoke) c, out);
			break;
		case EXPR_invoke:
			writeInvoke((Expr.Invoke) c, out);
			break;
		case DECL_var:
		case DECL_varinit:
			writeVariableDeclaration(indent, (Decl.Variable) c, out);
			break;
		default:
			throw new IllegalArgumentException("unknown bytecode encountered");
		}
	}

	private void writeAssert(int indent, Stmt.Assert stmt, PrintWriter out) {
		out.print("assert ");
		writeExpression(stmt.getCondition(),out);
		out.println();
	}

	private void writeAssume(int indent, Stmt.Assume stmt, PrintWriter out) {
		out.print("assume ");
		writeExpression(stmt.getCondition(),out);
		out.println();
	}

	private void writeAssign(int indent, Stmt.Assign stmt, PrintWriter out) {
		Tuple<LVal> lhs = stmt.getLeftHandSide();
		Tuple<Expr> rhs = stmt.getRightHandSide();
		if(lhs.size() > 0) {
			for(int i=0;i!=lhs.size();++i) {
				if(i!=0) { out.print(", "); }
				writeExpression(lhs.getOperand(i),out);
			}
			out.print(" = ");
		}
		writeExpressions(rhs,out);
		out.println();
	}

	private void writeBreak(int indent, Stmt.Break stmt, PrintWriter out) {
		out.println("break");
	}

	private void writeContinue(int indent, Stmt.Continue stmt, PrintWriter out) {
		out.println("continue");
	}

	private void writeDebug(int indent, Stmt.Debug stmt, PrintWriter out) {
		out.println("debug");
	}

	private void writeDoWhile(int indent, Stmt.DoWhile stmt, PrintWriter out) {
		Tuple<Expr> loopInvariant = stmt.getInvariant();
		// Location<?>[] modifiedOperands = b.getOperandGroup(1);
		out.println("do:");
		//
		writeBlock(indent+1,stmt.getBody(),out);
		tabIndent(indent+1,out);
		out.print("while ");
		writeExpression(stmt.getCondition(),out);
		out.print(" modifies ");
		//writeExpressions(modifiedOperands,out);
		for(Expr invariant : loopInvariant) {
			out.println();
			tabIndent(indent+1,out);
			out.print("where ");
			writeExpression(invariant,out);
		}
		// FIXME: add invariants
		out.println();
	}

	private void writeFail(int indent, Stmt.Fail stmt, PrintWriter out) {
		out.println("fail");
	}

	private void writeIf(int indent, Stmt.IfElse stmt, PrintWriter out) {
		out.print("if ");
		writeExpression(stmt.getCondition(),out);
		out.println(":");
		writeBlock(indent+1,stmt.getTrueBranch(),out);
		if(stmt.hasFalseBranch()) {
			tabIndent(indent+1,out);
			out.println("else:");
			writeBlock(indent+1,stmt.getFalseBranch(),out);
		}
	}

	private void writeNamedBlock(int indent, Stmt.NamedBlock stmt, PrintWriter out) {
		out.print(stmt.getName());
		out.println(":");
		writeBlock(indent+1,stmt.getBlock(),out);
	}

	private void writeWhile(int indent, Stmt.While stmt, PrintWriter out) {
		out.print("while ");
		writeExpression(stmt.getCondition(), out);
		Tuple<Expr> loopInvariant = stmt.getInvariant();
		// Location<?>[] modifiedOperands = b.getOperandGroup(1);
		// out.print(" modifies ");
		// writeExpressions(modifiedOperands,out);
		//
		for (Expr invariant : loopInvariant) {
			out.println();
			tabIndent(indent + 1, out);
			out.print("where ");
			writeExpression(invariant, out);
		}
		out.println(":");
		writeBlock(indent + 1, stmt.getBody(), out);
	}

	private void writeReturn(int indent, Stmt.Return stmt, PrintWriter out) {
		Tuple<Expr> returns = stmt.getReturns();
		out.print("return");
		if(returns.size() > 0) {
			out.print(" ");
			writeExpressions(returns,out);
		}
		out.println();
	}

	private void writeSkip(int indent, Stmt.Skip stmt, PrintWriter out) {
		out.println("skip");
	}

	private void writeSwitch(int indent, Stmt.Switch stmt, PrintWriter out) {
		out.print("switch ");
		writeExpression(stmt.getCondition(), out);
		out.println(":");
		for (Stmt.Case cAse : stmt.getCases()) {
			// FIXME: ugly
			Tuple<Expr> values = cAse.getConditions();
			tabIndent(indent + 2, out);
			if (values.size() == 0) {
				out.println("default:");
			} else {
				out.print("case ");
				for (int j = 0; j != values.size(); ++j) {
					if (j != 0) {
						out.print(", ");
					}
					writeExpression(values.getOperand(j), out);
				}
				out.println(":");
			}
			writeBlock(indent + 2, cAse.getBlock(), out);
		}
	}

	/**
	 * Write a bracketed operand if necessary. Any operand whose human-readable
	 * representation can contain whitespace must have brackets around it.
	 *
	 * @param operand
	 * @param enclosing
	 * @param out
	 */
	private void writeBracketedExpression(Expr expr, PrintWriter out) {
		boolean needsBrackets = needsBrackets(expr);
		if (needsBrackets) {
			out.print("(");
		}
		writeExpression(expr, out);
		if (needsBrackets) {
			out.print(")");
		}
	}

	private void writeExpressions(Tuple<Expr> exprs, PrintWriter out) {
		for (int i = 0; i != exprs.size(); ++i) {
			if (i != 0) {
				out.print(", ");
			}
			writeExpression(exprs.getOperand(i), out);
		}
	}

	@SuppressWarnings("unchecked")
	private void writeExpression(Expr expr, PrintWriter out) {
		switch (expr.getOpcode()) {
		case EXPR_alen:
			writeArrayLength((Expr.ArrayLength) expr,out);
			break;
		case EXPR_aread:
			writeArrayIndex((Expr.ArrayAccess) expr,out);
			break;
		case EXPR_ainit:
			writeArrayInitialiser((Expr.ArrayInitialiser) expr,out);
			break;
		case EXPR_agen:
			writeArrayGenerator((Expr.ArrayGenerator) expr,out);
			break;
		case EXPR_cast:
			writeConvert((Expr.Cast) expr, out);
			break;
		case EXPR_constant:
			writeConst((Expr.Constant) expr, out);
			break;
		case EXPR_rread:
			writeFieldLoad((Expr.RecordAccess) expr, out);
			break;
		case EXPR_indirectinvoke:
			writeIndirectInvoke((Expr.IndirectInvoke) expr, out);
			break;
		case EXPR_invoke:
			writeInvoke((Expr.Invoke) expr, out);
			break;
		case DECL_lambda:
			writeLambda((Decl.Lambda) expr, out);
			break;
		case EXPR_rinit:
			writeRecordConstructor((Expr.RecordInitialiser) expr, out);
			break;
		case EXPR_pinit:
			writeNewObject((Expr.New) expr,out);
			break;
		case EXPR_pread:
		case EXPR_lnot:
		case EXPR_ineg:
		case EXPR_bnot:
			writePrefixLocations((Expr.UnaryOperator) expr,out);
			break;
		case EXPR_lall:
		case EXPR_lsome:
			writeQuantifier((Expr.Quantifier) expr, out);
			break;
		case EXPR_iadd:
		case EXPR_isub:
		case EXPR_imul:
		case EXPR_idiv:
		case EXPR_irem:
		case EXPR_eq:
		case EXPR_neq:
		case EXPR_ilt:
		case EXPR_ile:
		case EXPR_igt:
		case EXPR_igteq:
		case EXPR_land:
		case EXPR_lor:
		case EXPR_bor:
		case EXPR_bxor:
		case EXPR_band:
		case EXPR_is:
			writeInfixLocations((Expr.NaryOperator) expr, out);
			break;
		case EXPR_bshl:
		case EXPR_bshr:
			writeInfixLocations((Expr.BinaryOperator) expr, out);
			break;
		case EXPR_varcopy:
			writeVariableAccess((Expr.VariableAccess) expr, out);
			break;
		default:
			throw new IllegalArgumentException("unknown bytecode encountered: " + expr);
		}
	}


	private void writeArrayLength(Expr.ArrayLength expr, PrintWriter out) {
		out.print("|");
		writeExpression(expr.getOperand(), out);
		out.print("|");
	}

	private void writeArrayIndex(Expr.ArrayAccess expr, PrintWriter out) {
		writeExpression(expr.getFirstOperand(), out);
		out.print("[");
		writeExpression(expr.getSecondOperand(), out);
		out.print("]");
	}

	private void writeArrayInitialiser(Expr.ArrayInitialiser expr, PrintWriter out) {
		Tuple<Expr> operands = expr.getArguments();
		out.print("[");
		for(int i=0;i!=operands.size();++i) {
			if(i != 0) {
				out.print(", ");
			}
			writeExpression(operands.getOperand(i),out);
		}
		out.print("]");
	}

	private void writeArrayGenerator(Expr.ArrayGenerator expr, PrintWriter out) {
		out.print("[");
		writeExpression(expr.getFirstOperand(), out);
		out.print(" ; ");
		writeExpression(expr.getSecondOperand(), out);
		out.print("]");
	}
	private void writeConvert(Expr.Cast expr, PrintWriter out) {
		out.print("(" + expr.getType() + ") ");
		writeExpression(expr.getOperand(),out);
	}
	private void writeConst(Expr.Constant expr, PrintWriter out) {
		out.print(expr.getValue());
	}
	private void writeFieldLoad(Expr.RecordAccess expr, PrintWriter out) {
		writeBracketedExpression(expr.getOperand(),out);
		out.print("." + expr.getField());
	}

	private void writeIndirectInvoke(Expr.IndirectInvoke expr, PrintWriter out) {
		Tuple<Expr> args = expr.getArguments();
		writeExpression(expr.getSource(), out);
		out.print("(");
		for (int i = 0; i != args.size(); ++i) {
			if (i != 0) {
				out.print(", ");
			}
			writeExpression(args.getOperand(i), out);
		}
		out.print(")");
	}

	private void writeInvoke(Expr.Invoke expr, PrintWriter out) {
		out.print(expr.getName() + "(");
		Tuple<Expr> args = expr.getArguments();
		for (int i = 0; i != args.size(); ++i) {
			if (i != 0) {
				out.print(", ");
			}
			writeExpression(args.getOperand(i), out);
		}
		out.print(")");
	}

	@SuppressWarnings("unchecked")
	private void writeLambda(Decl.Lambda expr, PrintWriter out) {
//		out.print("&[");
//		Location<?>[] environment = expr.getOperandGroup(SyntaxTree.ENVIRONMENT);
//		for (int i = 0; i != environment.length; ++i) {
//			Expr.VariableDeclaration> var = (Location<VariableDeclaration>) environment[i];
//			if (i != 0) {
//				out.print(", ");
//			}
//			out.print(var.getType());
//			out.print(" ");
//			out.print(var.getBytecode().getName());
//		}
//		out.print("](");
//		Location<?>[] parameters = expr.getOperandGroup(SyntaxTree.PARAMETERS);
//		for (int i = 0; i != parameters.length; ++i) {
//			Location<VariableDeclaration> var = (Location<VariableDeclaration>) parameters[i];
//			if (i != 0) {
//				out.print(", ");
//			}
//			out.print(var.getType());
//			out.print(" ");
//			out.print(var.getBytecode().getName());
//		}
//		out.print(" -> ");
//		writeExpression(expr.getOperand(0), out);
//		out.print(")");
	}

	private void writeRecordConstructor(Expr.RecordInitialiser expr, PrintWriter out) {
		out.print("{");
		Tuple<Pair<Identifier,Expr>> initialisers = expr.getFields();
		for (int i = 0; i != initialisers.size(); ++i) {
			Pair<Identifier,Expr> initialiser = initialisers.getOperand(i);
			if (i != 0) {
				out.print(", ");
			}
			out.print(initialiser.getFirst());
			out.print(": ");
			writeExpression(initialiser.getSecond(), out);
		}
		out.print("}");
	}

	private void writeNewObject(Expr.New expr, PrintWriter out) {
		out.print("new ");
		writeExpression(expr.getOperand(), out);
	}

	private void writePrefixLocations(Expr.UnaryOperator expr, PrintWriter out) {
		// Prefix operators
		out.print(opcode(expr.getOpcode()));
		writeBracketedExpression(expr.getOperand(),out);
	}

	private void writeInfixLocations(Expr.BinaryOperator expr, PrintWriter out) {
		writeBracketedExpression(expr.getFirstOperand(),out);
		out.print(" ");
		out.print(opcode(expr.getOpcode()));
		out.print(" ");
		writeBracketedExpression(expr.getSecondOperand(),out);
	}

	private void writeInfixLocations(Expr.NaryOperator expr, PrintWriter out) {
		Tuple<Expr> operands = expr.getArguments();
		for (int i = 0; i != operands.size(); ++i) {
			if (i != 0) {
				out.print(" ");
				out.print(opcode(expr.getOpcode()));
				out.print(" ");
			}
			writeBracketedExpression(operands.getOperand(i), out);
		}
	}

	@SuppressWarnings("unchecked")
	private void writeQuantifier(Expr.Quantifier expr, PrintWriter out) {
		out.print(quantifierKind(expr));
		out.print(" { ");
		Tuple<Decl.Variable> params = expr.getParameters();
		for (int i = 0; i != params.size(); ++i) {
			Decl.Variable v = params.getOperand(i);
			if (i != 0) {
				out.print(", ");
			}
			out.print(v.getName());
			out.print(" in ");
			writeExpression(v.getInitialiser(), out);
		}
		out.print(" | ");
		writeExpression(expr.getOperand(), out);
		out.print(" } ");
	}

	private void writeVariableAccess(Expr.VariableAccess loc, PrintWriter out) {
		out.print(loc.getVariableDeclaration().getName());
	}

	private String quantifierKind(Expr.Quantifier c) {
		switch(c.getOpcode()) {
		case EXPR_lsome:
			return "exists";
		case EXPR_lall:
			return "all";
		}
		throw new IllegalArgumentException();
	}

	private static void writeModifiers(Tuple<Modifier> modifiers, PrintWriter out) {
		for(Modifier m : modifiers) {
			out.print(m.toString());
			out.print(" ");
		}
	}

	private boolean needsBrackets(Expr e) {
		switch(e.getOpcode()) {
		case EXPR_cast:
		case EXPR_iadd:
		case EXPR_isub:
		case EXPR_imul:
		case EXPR_idiv:
		case EXPR_irem:
		case EXPR_eq:
		case EXPR_neq:
		case EXPR_ilt:
		case EXPR_ile:
		case EXPR_igt:
		case EXPR_igteq:
		case EXPR_land:
		case EXPR_lor:
		case EXPR_bor:
		case EXPR_bxor:
		case EXPR_band:
		case EXPR_bshl:
		case EXPR_bshr:
		case EXPR_is:
		case EXPR_pinit:
		case EXPR_pread:
			return true;
		}
		return false;
	}

	private static String opcode(int k) {
		switch(k) {
		case EXPR_ineg:
			return "-";
		case EXPR_lnot:
			return "!";
		case EXPR_bnot:
			return "~";
		case EXPR_pread:
			return "*";
		// Binary
		case EXPR_iadd:
			return "+";
		case EXPR_isub:
			return "-";
		case EXPR_imul:
			return "*";
		case EXPR_idiv:
			return "/";
		case EXPR_irem:
			return "%";
		case EXPR_eq:
			return "==";
		case EXPR_neq:
			return "!=";
		case EXPR_ilt:
			return "<";
		case EXPR_ile:
			return "<=";
		case EXPR_igt:
			return ">";
		case EXPR_igteq:
			return ">=";
		case EXPR_land:
			return "&&";
		case EXPR_lor:
			return "||";
		case EXPR_bor:
			return "|";
		case EXPR_bxor:
			return "^";
		case EXPR_band:
			return "&";
		case EXPR_bshl:
			return "<<";
		case EXPR_bshr:
			return ">>";
		case EXPR_pinit:
			return "new";
		default:
			throw new IllegalArgumentException("unknown operator kind : " + k);
		}
	}

	private static void tabIndent(int indent, PrintWriter out) {
		indent = indent * 4;
		for(int i=0;i<indent;++i) {
			out.print(" ");
		}
	}
}
