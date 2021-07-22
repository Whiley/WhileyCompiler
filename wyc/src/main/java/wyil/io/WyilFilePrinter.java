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
package wyil.io;

import static wyil.lang.WyilFile.*;

import java.io.*;
import java.util.*;

import wycc.lang.Build;
import wycc.lang.SyntacticItem;
import wycc.util.AbstractCompilationUnit.Identifier;
import wycc.util.AbstractCompilationUnit.Tuple;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.util.AbstractConsumer;
import wyil.util.AbstractVisitor;

/**
 * Prints the Abstract Syntax Tree (AST) of a given Whiley File in a textual form.
 *
 * @author David J. Pearce
 *
 */
public final class WyilFilePrinter extends AbstractConsumer<Integer> {
	private final PrintWriter out;
	/**
	 * Show qualified names at all places where names are resolved (e.g. at
	 * invocations).
	 */
	private boolean showQualifiedNames = true;

	private boolean verbose = false;

	public WyilFilePrinter(PrintWriter visitr) {
		super(Build.NULL_METER);
		this.out = visitr;
	}

	public WyilFilePrinter(OutputStream stream) {
		super(Build.NULL_METER);
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

	public void apply(WyilFile module) {
		visitModule(module, 0);
	}

	@Override
	public void visitModule(WyilFile wf, Integer indent) {
		int major = wf.getMajorVersion();
		int minor = wf.getMinorVersion();
		out.println("// wyil version " + major + "." + minor);
		Decl.Module module = wf.getModule();
		for (Decl.Unit decl : module.getUnits()) {
			visitDeclaration(decl, indent);
		}
		for (Decl.Unit decl : module.getExterns()) {
			visitExtern(decl, indent);
		}
		//
		out.flush();
	}

	@Override
	public void visitDeclaration(Decl decl, Integer indent) {
		super.visitDeclaration(decl, indent);
		out.println();
	}


	@Override
	public void visitUnit(Decl.Unit decl, Integer indent) {
		println('=',80);
		out.println(decl.getName() + ".whiley");
		println('=',80);
		super.visitUnit(decl, indent);
	}

	public void visitExtern(Decl.Unit decl, Integer indent) {
		println('=',80);
		out.println(decl.getName() + " (external)");
		println('=',80);
		super.visitUnit(decl, indent);
	}

	@Override
	public void visitImport(Decl.Import decl, Integer indent) {
		out.print("import ");
		if(decl.hasFrom()) {
			out.print(WyilFile.toString(decl.getNames()));
			out.print(" from ");
		}
		Tuple<Identifier> path= decl.getPath();
		for(int i=0;i!=path.size();++i) {
			if(i != 0) {
				out.print("::");
			}
			out.print(path.get(i));
		}
		if(decl.hasWith()) {
			out.print(" with ");
			out.print(WyilFile.toString(decl.getNames()));
		}
		out.println();
	}

	@Override
	public void visitStaticVariable(Decl.StaticVariable decl, Integer indent) {
		visitModifiers(decl.getModifiers());
		out.print(decl.getType());
		out.print(" ");
		out.print(decl.getName());
		out.print(" = ");
		visitExpression(decl.getInitialiser(), indent);
		out.println();
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
	}

	@Override
	public void visitProperty(Decl.Property decl, Integer indent) {
		out.print("property ");
		out.print(decl.getName());
		visitVariables(decl.getParameters(), indent);
	}

	@Override
	public void visitFunctionOrMethod(Decl.FunctionOrMethod decl, Integer indent) {
		//
		tabIndent(indent);
		visitModifiers(decl.getModifiers());
		if (decl instanceof Decl.Function) {
			out.print("function ");
		} else {
			out.print("method ");
		}
		out.print(decl.getName());
		visitVariables(decl.getParameters(), 0);
		if (decl.getReturns().size() != 0) {
			out.print(" -> ");
			visitVariables(decl.getReturns(), 0);
		}
		//
		for (Expr precondition : decl.getRequires()) {
			out.println();
			out.print("requires ");
			visitExpression(precondition, indent);
		}
		for (Expr postcondition : decl.getEnsures()) {
			out.println();
			out.print("ensures ");
			visitExpression(postcondition, indent);
		}
		if (decl.getBody().size() > 0) {
			out.println(": ");
			visitBlock(decl.getBody(), indent);
		} else {
			out.println();
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
		tabIndent(indent);
		out.print(decl.getType());
		out.print(" ");
		out.print(decl.getName());
	}

	@Override
	public void visitBlock(Stmt.Block stmt, Integer indent) {
		super.visitBlock(stmt, indent+1);
	}

	@Override
	public void visitStatement(Stmt stmt, Integer indent) {
		switch(stmt.getOpcode()) {
		case EXPR_indirectinvoke:
		case EXPR_invoke:
			tabIndent(indent);
		}
		super.visitStatement(stmt, indent);
		// Touch up a few
		switch(stmt.getOpcode()) {
		case DECL_variable:
		case EXPR_indirectinvoke:
		case EXPR_invoke:
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
	public void visitAssume(Stmt.Assume stmt, Integer indent) {
		tabIndent(indent);
		out.print("assume ");
		visitExpression(stmt.getCondition(), indent);
		out.println();
	}

	@Override
	public void visitAssign(Stmt.Assign stmt, Integer indent) {
		tabIndent(indent);
		Tuple<LVal> lhs = stmt.getLeftHandSide();
		Tuple<Expr> rhs = stmt.getRightHandSide();
		if(lhs.size() > 0) {
			for(int i=0;i!=lhs.size();++i) {
				if(i!=0) { out.print(", "); }
				visitExpression(lhs.get(i), indent);
			}
			out.print(" = ");
		}
		visitExpressions(rhs, indent);
		out.println();
	}

	@Override
	public void visitBreak(Stmt.Break stmt, Integer indent) {
		tabIndent(indent);
		out.println("break");
	}

	@Override
	public void visitContinue(Stmt.Continue stmt, Integer indent) {
		tabIndent(indent);
		out.println("continue");
	}

	@Override
	public void visitDebug(Stmt.Debug stmt, Integer indent) {
		tabIndent(indent);
		out.println("debug");
	}

	@Override
	public void visitDoWhile(Stmt.DoWhile stmt, Integer indent) {
		tabIndent(indent);
		Tuple<Expr> loopInvariant = stmt.getInvariant();
		// Location<?>[] modifiedOperands = b.getOperandGroup(1);
		out.println("do:");
		//
		visitBlock(stmt.getBody(), indent);
		tabIndent(indent);
		out.print("while ");
		visitExpression(stmt.getCondition(), indent);
		out.print(" modifies ");
		//visitExpressions(modifiedOperands);
		for(Expr invariant : loopInvariant) {
			out.println();
			tabIndent(indent);
			out.print("where ");
			visitExpression(invariant, indent);
		}
		// FIXME: add invariants
		out.println();
	}

	@Override
	public void visitFail(Stmt.Fail stmt, Integer indent) {
		tabIndent(indent);
		out.println("fail");
	}

	@Override
	public void visitIfElse(Stmt.IfElse stmt, Integer indent) {
		tabIndent(indent);
		out.print("if ");
		visitExpression(stmt.getCondition(), indent);
		out.println(":");
		visitBlock(stmt.getTrueBranch(), indent);
		if(stmt.hasFalseBranch()) {
			tabIndent(indent);
			out.println("else:");
			visitBlock(stmt.getFalseBranch(), indent);
		}
	}

	@Override
	public void visitNamedBlock(Stmt.NamedBlock stmt, Integer indent) {
		tabIndent(indent);
		out.print(stmt.getName());
		out.println(":");
		visitBlock(stmt.getBlock(), indent+1);
	}

	@Override
	public void visitWhile(Stmt.While stmt, Integer indent) {
		tabIndent(indent);
		out.print("while ");
		visitExpression(stmt.getCondition(), indent);
		Tuple<Expr> loopInvariant = stmt.getInvariant();
		// Location<?>[] modifiedOperands = b.getOperandGroup(1);
		// out.print(" modifies ");
		// visitExpressions(modifiedOperands);
		//
		for (Expr invariant : loopInvariant) {
			out.println();
			tabIndent(indent);
			out.print("where ");
			visitExpression(invariant, indent);
		}
		out.println(":");
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
		out.println();
	}

	@Override
	public void visitSkip(Stmt.Skip stmt, Integer indent) {
		tabIndent(indent);
		out.println("skip");
	}

	@Override
	public void visitSwitch(Stmt.Switch stmt, Integer indent) {
		tabIndent(indent);
		out.print("switch ");
		visitExpression(stmt.getCondition(), indent);
		out.println(":");
		for (Stmt.Case cAse : stmt.getCases()) {
			// FIXME: ugly
			Tuple<Expr> values = cAse.getConditions();
			tabIndent(indent + 1);
			if (values.size() == 0) {
				out.println("default:");
			} else {
				out.print("case ");
				for (int j = 0; j != values.size(); ++j) {
					if (j != 0) {
						out.print(", ");
					}
					visitExpression(values.get(j), indent);
				}
				out.println(":");
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
		case EXPR_logicalimplication:
		case EXPR_logicaliff:
			visitInfixLocations((Expr.BinaryOperator) expr, indent);
			break;
		case EXPR_logicaland:
		case EXPR_logicalor:
		case EXPR_bitwiseor:
		case EXPR_bitwisexor:
		case EXPR_bitwiseand:
			visitInfixLocations((Expr.NaryOperator) expr, indent);
			break;
		case EXPR_is:
			visitInfixLocations((Expr.Is) expr, indent);
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
	public void visitArrayRange(Expr.ArrayRange expr, Integer indent) {
		visitExpression(expr.getFirstOperand(), indent);
		out.print(" .. ");
		visitExpression(expr.getSecondOperand(), indent);
	}

	@Override
	public void visitCast(Expr.Cast expr, Integer indent) {
		out.print("(" + expr.getType() + ") ");
		visitExpression(expr.getOperand(), indent);
	}

	@Override
	public void visitConstant(Expr.Constant expr, Integer indent) {
		out.print(toString(expr.getValue()));
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
		Decl.Link<Decl.Callable> link = expr.getLink();
		if(showQualifiedNames) {
			out.print(link.getTarget().getQualifiedName() + "(");
		} else {
			out.print(link.getName() + "(");
		}
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
	@SuppressWarnings("unchecked")
	public void visitLambda(Decl.Lambda expr, Integer indent) {
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
//		visitExpression(expr.getOperand(0));
//		out.print(")");
	}

	@Override
	public void visitRecordInitialiser(Expr.RecordInitialiser expr, Integer indent) {
		out.print("{");
		Tuple<WyilFile.Identifier> fields = expr.getFields();
		Tuple<WyilFile.Expr> operands = expr.getOperands();
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

	public void visitInfixLocations(Expr.Is expr, Integer indent) {
		visitBracketedExpression(expr.getOperand(), indent);
		out.print(" is ");
		out.print(expr.getTestType());
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
	@SuppressWarnings("unchecked")
	public void visitUniversalQuantifier(Expr.UniversalQuantifier expr, Integer indent) {
		visitQuantifier(expr,indent);
	}

	@Override
	public void visitExistentialQuantifier(Expr.ExistentialQuantifier expr, Integer indent) {
		visitQuantifier(expr,indent);
	}

	public void visitQuantifier(Expr.Quantifier expr, Integer indent) {
		out.print(quantifierKind(expr));
		out.print(" { ");
		Tuple<Decl.StaticVariable> params = expr.getParameters();
		for (int i = 0; i != params.size(); ++i) {
			Decl.StaticVariable v = params.get(i);
			if (i != 0) {
				out.print(", ");
			}
			out.print(v.getName());
			out.print(" in ");
			visitExpression(v.getInitialiser(), indent);
		}
		out.print(" | ");
		visitExpression(expr.getOperand(), indent);
		out.print(" } ");
	}


	@Override
	public void visitStaticVariableAccess(Expr.StaticVariableAccess expr, Integer indent) {
		out.print(expr.getLink().getName());
	}

	@Override
	public void visitVariableAccess(Expr.VariableAccess expr, Integer indent) {
		out.print(expr.getVariableDeclaration().getName());
	}

	private String quantifierKind(Expr.Quantifier c) {
		switch(c.getOpcode()) {
		case EXPR_logicalexistential:
			return "exists";
		case EXPR_logicaluniversal:
			return "all";
		}
		throw new IllegalArgumentException();
	}

	private void visitModifiers(Tuple<Modifier> modifiers) {
		for(Modifier m : modifiers) {
			out.print(m.toString());
			out.print(" ");
		}
	}

	private boolean needsBrackets(Expr e) {
		switch(e.getOpcode()) {
		case EXPR_cast:
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
		case EXPR_is:
		case EXPR_new:
		case EXPR_dereference:
			return true;
		}
		return false;
	}

	private static String toString(Value v) {
		switch (v.getOpcode()) {

		case ITEM_utf8:
			return "\"" + v.toString() + "\"";
		case ITEM_array: {
			Value.Array arr = (Value.Array) v;
			String r = "[";
			for (int i = 0; i != arr.size(); ++i) {
				if(i!=0) {
					r = r + ", ";
				}
				r = r + toString(arr.get(i));
			}
			return r + "]";
		}
		case ITEM_dictionary: {
			Value.Dictionary rec = (Value.Dictionary) v;
			String r = "{";
			for (int i = 0; i != rec.size(); ++i) {
				if(i!=0) {
					r = r + ", ";
				}
				Pair<Identifier,Value> p = rec.get(i);
				r += p.getFirst() + ": " + toString(p.getSecond());
			}
			return r + "}";
		}
		default:
			return v.toString();
		}
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
		case EXPR_logicalimplication:
			return "==>";
		case EXPR_logicaliff:
			return "<==>";
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
		print(' ',indent*4);
	}

	private void println(char c, int repeat) {
		print(c,repeat);
		out.println();
	}
	private void print(char c, int repeat) {
		for(int i=0;i<repeat;++i) {
			out.print(c);
		}
	}
}
