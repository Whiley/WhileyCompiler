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
package wyll.io;

import java.io.*;
import java.util.*;

import wybs.lang.Build;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyll.core.WyllFile;
import wyll.util.AbstractConsumer;

import static wyll.core.WyllFile.*;

/**
 * Prints the Abstract Syntax Tree (AST) of a given Whiley File in a textual form.
 *
 * @author David J. Pearce
 *
 */
public final class WyllFilePrinter extends AbstractConsumer<Integer> {
	private final PrintWriter out;
	private boolean verbose = false;

	public WyllFilePrinter(PrintWriter visitr) {
		this.out = visitr;
	}

	public WyllFilePrinter(OutputStream stream) {
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

	public void apply(WyllFile module) {
		visitWyllFile(module,0);
	}

	@Override
	public void visitWyllFile(WyllFile module, Integer indent) {
		out.println();
		super.visitWyllFile(module, indent);
		out.flush();
	}

	@Override
	public void visitDeclaration(Decl decl, Integer indent) {
		super.visitDeclaration(decl, indent);
		out.println();
	}

	@Override
	public void visitStaticVariable(Decl.StaticVariable decl, Integer indent) {
		visitModifiers(decl.getModifiers());
		out.print(decl.getType());
		out.print(" ");
		out.println(decl.getName() + " = " + decl.getInitialiser());
	}

	@Override
	public void visitType(Decl.Type decl, Integer indent) {
		visitModifiers(decl.getModifiers());
		out.print("type " + decl.getName() + " is (");
		visitVariable(decl.getVariableDeclaration(), 0);
		out.println(")");
		for (Expr invariant : decl.getInvariant()) {
			out.print("where ");
			visitExpression(invariant, indent);
			out.println();
		}
		out.println();
	}

	@Override
	public void visitMethod(Decl.Method decl, Integer indent) {
		//
		visitModifiers(decl.getModifiers());
		out.print("method ");
		out.print(decl.getName());
		visitVariables(decl.getParameters(), indent);
		if (decl.getReturns().size() != 0) {
			out.print(" -> ");
			visitVariables(decl.getReturns(), indent);
		}
		//
		if (decl.getBody() != null) {
			visitBlock(decl.getBody(), indent);
		}
	}

	@Override
	public void visitVariables(Tuple<Decl.Variable> parameters, Integer indent) {
		out.print("(");
		for (int i = 0; i != parameters.size(); ++i) {
			if (i != 0) {
				out.print(", ");
			}
			visitVariable(parameters.get(i), indent);
		}
		out.print(")");
	}

	@Override
	public void visitVariable(Decl.Variable decl, Integer indent) {
		out.print(decl.getType());
		out.print(" ");
		out.print(decl.getName());
		if (decl.hasInitialiser()) {
			out.print(" = ");
			visitExpression(decl.getInitialiser(), indent);
		}
	}

	@Override
	public void visitBlock(Stmt.Block stmt, Integer indent) {
		out.println("{");
		super.visitBlock(stmt, indent+1);
		tabIndent(indent);
		out.println("}");
	}

	@Override
	public void visitStatement(Stmt stmt, Integer indent) {
		super.visitStatement(stmt, indent);
		// Touch up a few
		switch(stmt.getOpcode()) {
		case DECL_variable:
		case DECL_variableinitialiser:
			out.println();
		}
	}

	@Override
	public void visitAssert(Stmt.Assert stmt, Integer indent) {
		tabIndent(indent);
		out.print("assert ");
		visitExpression(stmt.getCondition(), indent);
		out.println();
	}

	@Override
	public void visitAssign(Stmt.Assign stmt, Integer indent) {
		tabIndent(indent);
		visitExpression(stmt.getLeftHandSide(), indent);
		out.print(" = ");
		visitExpression(stmt.getRightHandSide(), indent);
		out.println(";");
	}

	@Override
	public void visitBreak(Stmt.Break stmt, Integer indent) {
		tabIndent(indent);
		out.println("break;");
	}

	@Override
	public void visitContinue(Stmt.Continue stmt, Integer indent) {
		tabIndent(indent);
		out.println("continue;");
	}
//
//	@Override
//	public void visitDebug(Stmt.Debug stmt, Integer indent) {
//		tabIndent(indent);
//		out.println("debug");
//	}

	@Override
	public void visitDoWhile(Stmt.DoWhile stmt, Integer indent) {
		tabIndent(indent);
		// Location<?>[] modifiedOperands = b.getOperandGroup(1);
		out.println("do");
		//
		visitBlock(stmt.getBody(), indent);
		tabIndent(indent);
		out.print("while(");
		visitExpression(stmt.getCondition(), indent);
		out.println(");");
	}

	@Override
	public void visitFail(Stmt.Fail stmt, Integer indent) {
		tabIndent(indent);
		out.println("fail;");
	}

	@Override
	public void visitIfElse(Stmt.IfElse stmt, Integer indent) {
		Tuple<Pair<Expr,Stmt.Block>> branches = stmt.getBranches();
		tabIndent(indent);
		for(int i=0;i!=branches.size();++i) {
			Pair<Expr,Stmt.Block> branch = branches.get(i);
			if(i != 0) {
				out.print("else ");
			}
			out.print("if(");
			visitExpression(branch.getFirst(), indent);
			out.print(") ");
			visitBlock(branch.getSecond(), indent);
		}
		if(stmt.hasDefaultBranch()) {
			tabIndent(indent);
			out.println("else");
			visitBlock(stmt.getDefaultBranch(), indent);
		}
	}

	@Override
	public void visitWhile(Stmt.While stmt, Integer indent) {
		tabIndent(indent);
		out.print("while(");
		visitExpression(stmt.getCondition(), indent);
		out.println(")");
		visitBlock(stmt.getBody(), indent);
	}

	@Override
	public void visitReturn(Stmt.Return stmt, Integer indent) {
		tabIndent(indent);
		out.print("return");
		if(stmt.hasReturn()) {
			out.print(" ");
			visitExpression(stmt.getReturn(), indent);
		}
		out.println(";");
	}

//	@Override
//	public void visitSkip(Stmt.Skip stmt, Integer indent) {
//		tabIndent(indent);
//		out.println("skip");
//	}

	@Override
	public void visitSwitch(Stmt.Switch stmt, Integer indent) {
		tabIndent(indent);
		out.print("switch ");
		visitExpression(stmt.getCondition(), indent);
		out.println(":");
		for (Stmt.Case cAse : stmt.getCases()) {
			// FIXME: ugly
			Tuple<Value.Int> values = cAse.getConditions();
			tabIndent(indent + 1);
			if (values.size() == 0) {
				out.println("default:");
			} else {
				for (int j = 0; j != values.size(); ++j) {
					out.print("case ");
					out.print(values.get(j));
					out.println(":");
				}
			}
			visitBlock(cAse.getBlock(), indent + 1);
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
	public void visitBracketedExpression(Expr expr, Integer indent) {
		boolean needsBrackets = needsBrackets(expr);
		if (needsBrackets) {
			out.print("(");
		}
		visitExpression(expr, indent);
		if (needsBrackets) {
			out.print(")");
		}
	}

	@Override
	public void visitExpressions(Tuple<Expr> exprs, Integer indent) {
		for (int i = 0; i != exprs.size(); ++i) {
			if (i != 0) {
				out.print(", ");
			}
			visitExpression(exprs.get(i), indent);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void visitExpression(Expr expr, Integer indent) {
		switch (expr.getOpcode()) {
		case EXPR_dereference:
		case EXPR_logicalnot:
		case EXPR_integernegation:
		case EXPR_bitwisenot:
			visitPrefixLocations((Expr.UnaryOperator) expr, indent);
			break;
		case EXPR_bitwiseshl:
		case EXPR_bitwiseshr:
		case EXPR_integeraddition:
		case EXPR_integersubtraction:
		case EXPR_integermultiplication:
		case EXPR_integerdivision:
		case EXPR_integerremainder:
		case EXPR_equal:
		case EXPR_notequal:
		case EXPR_integerlessthan:
		case EXPR_integerlessequal:
		case EXPR_integergreaterthan:
		case EXPR_integergreaterequal:
			visitInfixLocations((Expr.BinaryOperator) expr, indent);
			break;
		case EXPR_logicaland:
		case EXPR_logicalor:
		case EXPR_bitwiseor:
		case EXPR_bitwisexor:
		case EXPR_bitwiseand:
			visitInfixLocations((Expr.NaryOperator) expr, indent);
			break;
		default:
			super.visitExpression(expr, indent);
		}
	}


	@Override
	public void visitArrayLength(Expr.ArrayLength expr, Integer indent) {
		out.print("|");
		visitExpression(expr.getOperand(), indent);
		out.print("|");
	}

	@Override
	public void visitArrayAccess(Expr.ArrayAccess expr, Integer indent) {
		visitExpression(expr.getFirstOperand(), indent);
		out.print("[");
		visitExpression(expr.getSecondOperand(), indent);
		out.print("]");
	}

	@Override
	public void visitArrayInitialiser(Expr.ArrayInitialiser expr, Integer indent) {
		Tuple<Expr> operands = expr.getOperands();
		out.print("[");
		for(int i=0;i!=operands.size();++i) {
			if(i != 0) {
				out.print(", ");
			}
			visitExpression(operands.get(i), indent);
		}
		out.print("]");
	}

	@Override
	public void visitArrayGenerator(Expr.ArrayGenerator expr, Integer indent) {
		out.print("[");
		visitExpression(expr.getFirstOperand(), indent);
		out.print(" ; ");
		visitExpression(expr.getSecondOperand(), indent);
		out.print("]");
	}

	@Override
	public void visitNullConstant(Expr.NullConstant expr, Integer indent) {
		out.print("null");
	}

	@Override
	public void visitBoolConstant(Expr.BoolConstant expr, Integer indent) {
		out.print(expr.getValue());
	}

	@Override
	public void visitIntConstant(Expr.IntConstant expr, Integer indent) {
		out.print(expr.getValue());
	}

	@Override
	public void visitRecordAccess(Expr.RecordAccess expr, Integer indent) {
		visitBracketedExpression(expr.getOperand(), indent);
		out.print("." + expr.getField());
	}

	@Override
	public void visitIndirectInvoke(Expr.IndirectInvoke expr, Integer indent) {
		Tuple<Expr> args = expr.getArguments();
		visitExpression(expr.getSource(), indent);
		out.print("(");
		for (int i = 0; i != args.size(); ++i) {
			if (i != 0) {
				out.print(", ");
			}
			visitExpression(args.get(i), indent);
		}
		out.print(")");
	}

	@Override
	public void visitInvoke(Expr.Invoke expr, Integer indent) {
		out.print(expr.getName() + "(");
		Tuple<Expr> args = expr.getOperands();
		for (int i = 0; i != args.size(); ++i) {
			if (i != 0) {
				out.print(", ");
			}
			visitExpression(args.get(i), indent);
		}
		out.print(")");
	}

	@Override
	public void visitRecordInitialiser(Expr.RecordInitialiser expr, Integer indent) {
		out.print("{");
		Tuple<WyllFile.Identifier> fields = expr.getFields();
		Tuple<WyllFile.Expr> operands = expr.getOperands();
		for (int i = 0; i != operands.size(); ++i) {
			Identifier field = fields.get(i);
			Expr operand = operands.get(i);
			if (i != 0) {
				out.print(", ");
			}
			out.print(field);
			out.print(": ");
			visitExpression(operand, indent);
		}
		out.print("}");
	}

	@Override
	public void visitNew(Expr.New expr, Integer indent) {
		out.print("new ");
		visitExpression(expr.getOperand(), indent);
	}

	public void visitPrefixLocations(Expr.UnaryOperator expr, Integer indent) {
		// Prefix operators
		out.print(opcode(expr.getOpcode()));
		visitBracketedExpression(expr.getOperand(), indent);
	}

	public void visitInfixLocations(Expr.BinaryOperator expr, Integer indent) {
		visitBracketedExpression(expr.getFirstOperand(), indent);
		out.print(" ");
		out.print(opcode(expr.getOpcode()));
		out.print(" ");
		visitBracketedExpression(expr.getSecondOperand(), indent);
	}

	public void visitInfixLocations(Expr.NaryOperator expr, Integer indent) {
		Tuple<Expr> operands = expr.getOperands();
		for (int i = 0; i != operands.size(); ++i) {
			if (i != 0) {
				out.print(" ");
				out.print(opcode(expr.getOpcode()));
				out.print(" ");
			}
			visitBracketedExpression(operands.get(i), indent);
		}
	}

	@Override
	public void visitVariableAccess(Expr.VariableAccess expr, Integer indent) {
		out.print(expr.getName());
	}

	private void visitModifiers(Tuple<Modifier> modifiers) {
		for(Modifier m : modifiers) {
			out.print(m.toString());
			out.print(" ");
		}
	}

	private boolean needsBrackets(Expr e) {
		switch(e.getOpcode()) {
		case EXPR_integeraddition:
		case EXPR_integersubtraction:
		case EXPR_integermultiplication:
		case EXPR_integerdivision:
		case EXPR_integerremainder:
		case EXPR_equal:
		case EXPR_notequal:
		case EXPR_integerlessthan:
		case EXPR_integerlessequal:
		case EXPR_integergreaterthan:
		case EXPR_integergreaterequal:
		case EXPR_logicaland:
		case EXPR_logicalor:
		case EXPR_bitwiseor:
		case EXPR_bitwisexor:
		case EXPR_bitwiseand:
		case EXPR_bitwiseshl:
		case EXPR_bitwiseshr:
		case EXPR_new:
		case EXPR_dereference:
			return true;
		}
		return false;
	}

	private static String opcode(int k) {
		switch(k) {
		case EXPR_integernegation:
			return "-";
		case EXPR_logicalnot:
			return "!";
		case EXPR_bitwisenot:
			return "~";
		case EXPR_dereference:
			return "*";
		// Binary
		case EXPR_integeraddition:
			return "+";
		case EXPR_integersubtraction:
			return "-";
		case EXPR_integermultiplication:
			return "*";
		case EXPR_integerdivision:
			return "/";
		case EXPR_integerremainder:
			return "%";
		case EXPR_equal:
			return "==";
		case EXPR_notequal:
			return "!=";
		case EXPR_integerlessthan:
			return "<";
		case EXPR_integerlessequal:
			return "<=";
		case EXPR_integergreaterthan:
			return ">";
		case EXPR_integergreaterequal:
			return ">=";
		case EXPR_logicaland:
			return "&&";
		case EXPR_logicalor:
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

	private void tabIndent(int indent) {
		indent = indent * 4;
		for(int i=0;i<indent;++i) {
			out.print(" ");
		}
	}
}
