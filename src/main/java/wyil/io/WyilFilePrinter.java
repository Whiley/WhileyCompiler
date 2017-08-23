// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyil.io;

import java.io.*;
import java.util.*;

import wybs.lang.Build;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyil.lang.*;
import static wyil.lang.WyilFile.*;

/**
 * Writes WYIL bytecodes in a textual from to a given file.
 *
 * <b>NOTE:</b> currently, this class is somewhat broken since it does not
 * provide any way to specify the output directory. Rather, it simply puts the
 * WYIL file in the same place as the Whiley file.
 *
 * @author David J. Pearce
 *
 */
public final class WyilFilePrinter {
	private PrintWriter out;
	private boolean verbose = false;

	public WyilFilePrinter(Build.Task builder) {

	}

	public WyilFilePrinter(PrintWriter writer) {
		this.out = writer;
	}

	public WyilFilePrinter(OutputStream stream) {
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

	public void apply(WyilFile module) throws IOException {
		out.println();
		for(Declaration d : module.getDeclarations()) {
			write(d,out);
		}
		out.flush();
	}

	private void write(Declaration d, PrintWriter out) {
		if(d instanceof Declaration.Constant) {
			write((Declaration.Constant) d, out);
		} else if(d instanceof Declaration.Type) {
			write((Declaration.Type) d, out);
		} else if(d instanceof Declaration.Property){
			write((Declaration.Property) d, out);
		} else {
			write((Declaration.FunctionOrMethod) d, out);
		}
	}

	private void write(Declaration.Constant decl, PrintWriter out) {
		writeModifiers(decl.getModifiers(),out);
		out.println("constant " + decl.getName() + " = " + decl.getConstantExpr());
	}

	private void write(Declaration.Type decl, PrintWriter out) {
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
	private void write(Declaration.Property decl, PrintWriter out) {
		out.print("property ");
		out.print(decl.getName());
		writeParameters(decl.getParameters(),out);
	}

	private void write(Declaration.FunctionOrMethod decl, PrintWriter out) {
		//
		writeModifiers(decl.getModifiers(), out);
		if (decl instanceof Declaration.Function) {
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

	private void writeParameters(Tuple<Declaration.Variable> parameters, PrintWriter out) {
		out.print("(");
		for (int i = 0; i != parameters.size(); ++i) {
			if (i != 0) {
				out.print(", ");
			}
			writeVariableDeclaration(0, parameters.getOperand(i), out);
		}
		out.print(")");
	}

	private void writeVariableDeclaration(int indent, Declaration.Variable decl, PrintWriter out) {
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
		case DECL_variable:
		case DECL_variableinitialiser:
			writeVariableDeclaration(indent, (Declaration.Variable) c, out);
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
		Tuple<Expr> returns = stmt.getOperand();
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
		case EXPR_arrlen:
			writeArrayLength((Expr.ArrayLength) expr,out);
			break;
		case EXPR_arridx:
			writeArrayIndex((Expr.ArrayAccess) expr,out);
			break;
		case EXPR_arrinit:
			writeArrayInitialiser((Expr.ArrayInitialiser) expr,out);
			break;
		case EXPR_arrgen:
			writeArrayGenerator((Expr.ArrayGenerator) expr,out);
			break;
		case EXPR_cast:
			writeConvert((Expr.Cast) expr, out);
			break;
		case EXPR_const:
			writeConst((Expr.Constant) expr, out);
			break;
		case EXPR_recfield:
			writeFieldLoad((Expr.RecordAccess) expr, out);
			break;
		case EXPR_indirectinvoke:
			writeIndirectInvoke((Expr.IndirectInvoke) expr, out);
			break;
		case EXPR_invoke:
			writeInvoke((Expr.Invoke) expr, out);
			break;
		case EXPR_lambdainit:
			writeLambda((Expr.LambdaInitialiser) expr, out);
			break;
		case EXPR_recinit:
			writeRecordConstructor((Expr.RecordInitialiser) expr, out);
			break;
		case EXPR_new:
			writeNewObject((Expr.New) expr,out);
			break;
		case EXPR_deref:
		case EXPR_not:
		case EXPR_neg:
		case EXPR_bitwisenot:
			writePrefixLocations((Expr.Operator) expr,out);
			break;
		case EXPR_forall:
		case EXPR_exists:
			writeQuantifier((Expr.Quantifier) expr, out);
			break;
		case EXPR_add:
		case EXPR_sub:
		case EXPR_mul:
		case EXPR_div:
		case EXPR_rem:
		case EXPR_eq:
		case EXPR_neq:
		case EXPR_lt:
		case EXPR_lteq:
		case EXPR_gt:
		case EXPR_gteq:
		case EXPR_and:
		case EXPR_or:
		case EXPR_bitwiseor:
		case EXPR_bitwisexor:
		case EXPR_bitwiseand:
		case EXPR_bitwiseshl:
		case EXPR_bitwiseshr:
		case EXPR_is:
			writeInfixLocations((Expr.InfixOperator) expr, out);
			break;
		case EXPR_var:
			writeVariableAccess((Expr.VariableAccess) expr, out);
			break;
		default:
			throw new IllegalArgumentException("unknown bytecode encountered: " + expr);
		}
	}


	private void writeArrayLength(Expr.ArrayLength expr, PrintWriter out) {
		out.print("|");
		writeExpression(expr.getOperand(0), out);
		out.print("|");
	}

	private void writeArrayIndex(Expr.ArrayAccess expr, PrintWriter out) {
		writeExpression(expr.getOperand(0), out);
		out.print("[");
		writeExpression(expr.getOperand(1), out);
		out.print("]");
	}

	private void writeArrayInitialiser(Expr.ArrayInitialiser expr, PrintWriter out) {
		out.print("[");
		for(int i=0;i!=expr.size();++i) {
			if(i != 0) {
				out.print(", ");
			}
			writeExpression(expr.getOperand(i),out);
		}
		out.print("]");
	}

	private void writeArrayGenerator(Expr.ArrayGenerator expr, PrintWriter out) {
		out.print("[");
		writeExpression(expr.getOperand(0), out);
		out.print(" ; ");
		writeExpression(expr.getOperand(1), out);
		out.print("]");
	}
	private void writeConvert(Expr.Cast expr, PrintWriter out) {
		out.print("(" + expr.getCastType() + ") ");
		writeExpression(expr.getCastedExpr(),out);
	}
	private void writeConst(Expr.Constant expr, PrintWriter out) {
		out.print(expr.getValue());
	}
	private void writeFieldLoad(Expr.RecordAccess expr, PrintWriter out) {
		writeBracketedExpression(expr.getSource(),out);
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
	private void writeLambda(Expr.LambdaInitialiser expr, PrintWriter out) {
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
		for (int i = 0; i != expr.size(); ++i) {
			Pair<Identifier,Expr> initialiser = expr.getOperand(i);
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

	private void writePrefixLocations(Expr.Operator expr, PrintWriter out) {
		// Prefix operators
		out.print(opcode(expr.getOpcode()));
		writeBracketedExpression(expr.getOperand(0),out);
	}

	private void writeInfixLocations(Expr.InfixOperator expr, PrintWriter out) {
		writeBracketedExpression(expr.getOperand(0),out);
		out.print(" ");
		out.print(opcode(expr.getOpcode()));
		out.print(" ");
		writeBracketedExpression(expr.getOperand(1),out);

	}

	@SuppressWarnings("unchecked")
	private void writeQuantifier(Expr.Quantifier expr, PrintWriter out) {
		out.print(quantifierKind(expr));
		out.print(" { ");
		Tuple<Declaration.Variable> params = expr.getParameters();
		for (int i = 0; i != params.size(); ++i) {
			Declaration.Variable v = params.getOperand(i);
			if (i != 0) {
				out.print(", ");
			}
			out.print(v.getName());
			out.print(" in ");
			writeExpression(v.getInitialiser(), out);
		}
		out.print(" | ");
		writeExpression(expr.getBody(), out);
		out.print(" } ");
	}

	private void writeVariableAccess(Expr.VariableAccess loc, PrintWriter out) {
		out.print(loc.getVariableDeclaration().getName());
	}

	private String quantifierKind(Expr.Quantifier c) {
		switch(c.getOpcode()) {
		case EXPR_exists:
			return "exists";
		case EXPR_forall:
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
		case EXPR_add:
		case EXPR_sub:
		case EXPR_mul:
		case EXPR_div:
		case EXPR_rem:
		case EXPR_eq:
		case EXPR_neq:
		case EXPR_lt:
		case EXPR_lteq:
		case EXPR_gt:
		case EXPR_gteq:
		case EXPR_and:
		case EXPR_or:
		case EXPR_bitwiseor:
		case EXPR_bitwisexor:
		case EXPR_bitwiseand:
		case EXPR_bitwiseshl:
		case EXPR_bitwiseshr:
		case EXPR_is:
		case EXPR_new:
		case EXPR_deref:
			return true;
		}
		return false;
	}

	private static String opcode(int k) {
		switch(k) {
		case EXPR_neg:
			return "-";
		case EXPR_not:
			return "!";
		case EXPR_bitwisenot:
			return "~";
		case EXPR_deref:
			return "*";
		// Binary
		case EXPR_add:
			return "+";
		case EXPR_sub:
			return "-";
		case EXPR_mul:
			return "*";
		case EXPR_div:
			return "/";
		case EXPR_rem:
			return "%";
		case EXPR_eq:
			return "==";
		case EXPR_neq:
			return "!=";
		case EXPR_lt:
			return "<";
		case EXPR_lteq:
			return "<=";
		case EXPR_gt:
			return ">";
		case EXPR_gteq:
			return ">=";
		case EXPR_and:
			return "&&";
		case EXPR_or:
			return "||";
		case EXPR_bitwiseor:
			return "|";
		case EXPR_bitwisexor:
			return "^";
		case EXPR_bitwiseand:
			return "&";
		case EXPR_bitwiseshl:
			return "<<";
		case EXPR_bitwiseshr:
			return ">>";
		case EXPR_new:
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
