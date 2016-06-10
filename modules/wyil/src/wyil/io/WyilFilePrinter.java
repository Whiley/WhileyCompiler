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

package wyil.io;

import java.io.*;
import java.util.*;

import wybs.lang.Builder;
import wycc.lang.Transform;
import wyfs.lang.Path;
import wyil.lang.*;
import wyil.lang.Constant;
import wyil.lang.SyntaxTree.Expr;
import wyil.lang.SyntaxTree.Operator;
import wyil.lang.SyntaxTree.Stmt;
import wyil.lang.Type;
import wyil.lang.WyilFile.*;

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
public final class WyilFilePrinter implements Transform<WyilFile> {
	private PrintWriter out;
	private boolean verbose = getVerbose();
	
	public WyilFilePrinter(wybs.lang.Builder builder) {

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

	public static String describeVerbose() {
		return "Enable/disable verbose output";
	}

	public static boolean getVerbose() {
		return false; // default value
	}

	public void setVerbose(boolean flag) {
		this.verbose = flag;
	}
	
	// ======================================================================
	// Apply Method
	// ======================================================================
	
	public void apply(WyilFile module) throws IOException {

		if(out == null) {

			// TODO: where does this go?

			String filename = module.filename().replace(".whiley", ".wyasm");
			out = new PrintWriter(new FileOutputStream(filename));
		}

		//out.println("module: " + module.id());
		out.println("source-file: " + module.filename());
		out.println();
		for(WyilFile.Constant cd : module.constants()) {
			writeModifiers(cd.modifiers(),out);
			out.println("constant " + cd.name() + " = " + cd.constant());
		}
		if(!module.constants().isEmpty()) {
			out.println();
		}
		for (WyilFile.Type td : module.types()) {
			Type t = td.type();
			String t_str;
			t_str = t.toString();
			writeModifiers(td.modifiers(), out);
			out.println("type " + td.name() + " : " + t_str);
			for (SyntaxTree.Expr invariant : td.getInvariants()) {
				out.print("where ");
				writeExpression(invariant, out);
				out.println();
			}
			out.println();
		}

		for(FunctionOrMethod md : module.functionOrMethods()) {
			write(md,out);
			out.println();			
		}
		out.flush();
	}

	private void write(FunctionOrMethod method, PrintWriter out) {
		//
		writeModifiers(method.modifiers(), out);
		Type.FunctionOrMethod ft = method.type();
		if (ft instanceof Type.Function) {
			out.print("function ");
		} else {
			out.print("method ");
		}
		out.print(method.name());
		writeParameters(ft.params(),out);		
		if (!ft.returns().isEmpty()) {
			out.print(" -> ");
			writeParameters(ft.returns(),out);
		}				
		//
		for (SyntaxTree.Expr precondition : method.getPrecondition()) {
			out.println();
			out.print("requires ");
			writeExpression(precondition, out);
		}
		for (SyntaxTree.Expr postcondition : method.getPostcondition()) {
			out.println();
			out.print("ensures ");
			writeExpression(postcondition, out);
		}
		if (method.getBlocks().size() > 0) {
			out.println(": ");
			writeOuterBlock(method, out);
		}
	}

	private void writeParameters(List<Type> parameters, PrintWriter out) {
		out.print("(");
		for (int i = 0; i != parameters.size(); ++i) {
			if (i != 0) {
				out.print(", ");
			}
			out.print(parameters.get(i));
		}
		out.print(")");
	}
	
	private void writeOuterBlock(WyilFile.Declaration enclosing, PrintWriter out) {
		if (verbose) {
			List<Expr> locations = enclosing.getExpressions();
			for (int i = 0; i != locations.size(); ++i) {
				Expr l = (Expr) locations.get(i);
				tabIndent(1, out);
				out.println("// %" + i + " = " + l);
			}
		}
		writeBlock(0, enclosing.getBlock(0), out);
	}
	
	private void writeBlock(int indent, SyntaxTree.Block block, PrintWriter out) {
		for (int i = 0; i != block.size(); ++i) {
			writeStatement(indent, block.get(i), out);
		}
	}

	private void writeStatement(int indent, Stmt<?> c, PrintWriter out) {
		tabIndent(indent+1,out); 
		switch(c.getOpcode()) {
		case Bytecode.OPCODE_assert:
			writeAssert(indent, (Stmt<Bytecode.Assert>) c, out);
			break;
		case Bytecode.OPCODE_assume:
			writeAssume(indent, (Stmt<Bytecode.Assume>) c, out);
			break;
		case Bytecode.OPCODE_assign:
			writeAssign(indent, (Stmt<Bytecode.Assign>) c, out);
			break;
		case Bytecode.OPCODE_break:
			writeBreak(indent, (Stmt<Bytecode.Break>) c, out);
			break;
		case Bytecode.OPCODE_continue:
			writeContinue(indent, (Stmt<Bytecode.Continue>) c, out);
			break;
		case Bytecode.OPCODE_debug:
			writeDebug(indent, (Stmt<Bytecode.Debug>) c, out);
			break;
		case Bytecode.OPCODE_dowhile:
			writeDoWhile(indent, (Stmt<Bytecode.DoWhile>) c, out);
			break;
		case Bytecode.OPCODE_fail:
			writeFail(indent, (Stmt<Bytecode.Fail>) c, out);
			break;
		case Bytecode.OPCODE_if:
		case Bytecode.OPCODE_ifelse:
			writeIf(indent, (Stmt<Bytecode.If>) c, out);
			break;
		case Bytecode.OPCODE_indirectinvoke:
			writeIndirectInvoke(indent, (Stmt<Bytecode.IndirectInvoke>) c, out);
			break;
		case Bytecode.OPCODE_invoke:
			writeInvoke(indent, (Stmt<Bytecode.Invoke>) c, out);
			break;
		case Bytecode.OPCODE_while:
			writeWhile(indent, (Stmt<Bytecode.While>) c, out);
			break;
		case Bytecode.OPCODE_return:
			writeReturn(indent, (Stmt<Bytecode.Return>) c, out);
			break;
		case Bytecode.OPCODE_switch:
			writeSwitch(indent, (Stmt<Bytecode.Switch>) c, out);
			break;
		default:
			throw new IllegalArgumentException("unknown bytecode encountered");
		}
	}
	
	private void writeAssert(int indent, Stmt<Bytecode.Assert> c, PrintWriter out) {
		out.print("assert ");
		writeExpression(c.getOperand(0),out);
		out.println();
	}

	private void writeAssume(int indent, Stmt<Bytecode.Assume> c, PrintWriter out) {
		out.print("assume ");
		writeExpression(c.getOperand(0),out);
		out.println();
	}
	
	private void writeAssign(int indent, Stmt<Bytecode.Assign> stmt, PrintWriter out) {
		Expr[] lhs = stmt.getOperandGroup(SyntaxTree.LEFTHANDSIDE);
		Expr[] rhs = stmt.getOperandGroup(SyntaxTree.RIGHTHANDSIDE);
		if(lhs.length > 0) {
			for(int i=0;i!=lhs.length;++i) {
				if(i!=0) { out.print(", "); }
				writeExpression(lhs[i],out);
			}
			out.print(" = ");
		}
		writeExpressions(rhs,out);
		out.println();
	}
	
	private void writeBreak(int indent, Stmt<Bytecode.Break> b, PrintWriter out) {
		out.println("break");
	}
	
	private void writeContinue(int indent, Stmt<Bytecode.Continue> b, PrintWriter out) {
		out.println("continue");
	}
	
	private void writeDebug(int indent, Stmt<Bytecode.Debug> b, PrintWriter out) {
		out.println("debug");
	}
	
	private void writeDoWhile(int indent, Stmt<Bytecode.DoWhile> b, PrintWriter out) {
		SyntaxTree.Expr[] loopInvariant = b.getOperandGroup(0);
		SyntaxTree.Expr[] modifiedOperands = b.getOperandGroup(1);		
		out.println("do:");
		//				
		writeBlock(indent+1,b.getBlock(0),out);
		tabIndent(indent+1,out);
		out.print("while ");
		writeExpression(b.getOperand(0),out);
		out.print(" modifies ");
		writeExpressions(modifiedOperands,out);
		for(SyntaxTree.Expr invariant : loopInvariant) {
			out.println();
			tabIndent(indent+1,out);
			out.print("where ");
			writeExpression(invariant,out);
		}
		// FIXME: add invariants
		out.println();
	}

	private void writeFail(int indent, Stmt<Bytecode.Fail> c, PrintWriter out) {
		out.println("fail");
	}
	
	private void writeIf(int indent, Stmt<Bytecode.If> b, PrintWriter out) {
		out.print("if ");
		writeExpression(b.getOperand(0),out);
		out.println(":");
		writeBlock(indent+1,b.getBlock(0),out);
		if(b.numberOfBlocks() > 1) {
			tabIndent(indent+1,out);
			out.println("else:");
			writeBlock(indent+1,b.getBlock(1),out);
		}
	}
	
	private void writeIndirectInvoke(int indent, Stmt<Bytecode.IndirectInvoke> stmt, PrintWriter out) {
		Expr[] operands = stmt.getOperands();
		writeExpression(operands[0],out);
		out.print("(");		
		for(int i=1;i!=operands.length;++i) {
			if(i!=1) {
				out.print(", ");
			}
			writeExpression(operands[i],out);
		}
		out.println(")");
	}
	private void writeInvoke(int indent, Stmt<Bytecode.Invoke> stmt, PrintWriter out) {
		out.print(stmt.getBytecode().name() + "(");
		Expr[] operands = stmt.getOperands();
		for(int i=0;i!=operands.length;++i) {
			if(i!=0) {
				out.print(", ");
			}
			writeExpression(operands[i],out);
		}
		out.println(")");
	}
	
	private void writeWhile(int indent, Stmt<Bytecode.While> b, PrintWriter out) {
		out.print("while ");
		writeExpression(b.getOperand(0),out);
		SyntaxTree.Expr[] loopInvariant = b.getOperandGroup(0);
		SyntaxTree.Expr[] modifiedOperands = b.getOperandGroup(1);
		out.print(" modifies ");
		writeExpressions(modifiedOperands,out);
		//
		for(SyntaxTree.Expr invariant : loopInvariant) {
			out.println();
			tabIndent(indent+1,out);
			out.print("where ");
			writeExpression(invariant,out);
		}
		out.println(":");
		// FIXME: add invariants
		writeBlock(indent+1,b.getBlock(0),out);		
	}
	
	private void writeReturn(int indent, Stmt<Bytecode.Return> b, PrintWriter out) {
		SyntaxTree.Expr[] operands = b.getOperands();
		out.print("return");
		if(operands.length > 0) {
			out.print(" ");
			writeExpressions(operands,out);			
		}
		out.println();
	}
	
	private void writeSwitch(int indent, Stmt<Bytecode.Switch> b, PrintWriter out) {
		out.print("switch ");
		writeExpression(b.getOperand(0), out);
		out.println(":");
		for (int i = 0; i != b.numberOfBlocks(); ++i) {
			// FIXME: ugly
			Bytecode.Case cAse = b.getBytecode().cases()[i];
			Constant[] values = cAse.values();
			tabIndent(indent + 2, out);
			if (values.length == 0) {
				out.println("default:");
			} else {
				out.print("case ");
				for (int j = 0; j != values.length; ++j) {
					if (j != 0) {
						out.print(", ");
					}
					out.print(values[j]);
				}
				out.println(":");
			}
			writeBlock(indent + 2, b.getBlock(i), out);
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
	private void writeBracketedExpression(SyntaxTree.Expr expr, PrintWriter out) {
		if (expr instanceof SyntaxTree.Variable) {
			SyntaxTree.Variable v = (SyntaxTree.Variable) expr;
			out.print(v.name());
		} else {
			SyntaxTree.Operator<?> op = (SyntaxTree.Operator<?>) expr;
			boolean needsBrackets = needsBrackets(op.getBytecode());
			if (needsBrackets) {
				out.print("(");
			}
			write(op, out);
			if (needsBrackets) {
				out.print(")");
			}
		}
	}

	private void writeExpressions(SyntaxTree.Expr[] exprs, PrintWriter out) {
		Bytecode last = null;
		for (int i = 0; i != exprs.length; ++i) {
			if (i != 0) {
				out.print(", ");
			}
			SyntaxTree.Expr e = exprs[i];

			if (e instanceof SyntaxTree.PositionalOperator<?>) {
				SyntaxTree.PositionalOperator<?> p = (SyntaxTree.PositionalOperator<?>) e;
				if (p.getBytecode() == last) {
					continue;
				} else {
					last = p.getBytecode();
				}
			}
			writeExpression(exprs[i], out);
		}
	}
	
	private void writeExpression(SyntaxTree.Expr expr, PrintWriter out) {
		if (expr instanceof SyntaxTree.Variable) {
			SyntaxTree.Variable v = (SyntaxTree.Variable) expr;
			out.print(v.name());
		} else {
			write((SyntaxTree.Operator<?>) expr, out);
		}
	}
	
	private void write(SyntaxTree.Operator<?> expr, PrintWriter out) {
		switch (expr.getOpcode()) {
		case Bytecode.OPCODE_arraylength:
			writeArrayLength((Operator<Bytecode.Operator>) expr,out);
			break;
		case Bytecode.OPCODE_arrayindex:
			writeArrayIndex((Operator<Bytecode.Operator>) expr,out);
			break;
		case Bytecode.OPCODE_array:
			writeArrayInitialiser((Operator<Bytecode.Operator>) expr,out);
			break;
		case Bytecode.OPCODE_arraygen:
			writeArrayGenerator((Operator<Bytecode.Operator>) expr,out);
			break;
		case Bytecode.OPCODE_convert:
			writeConvert((Operator<Bytecode.Convert>) expr, out);
			break;
		case Bytecode.OPCODE_const:
			writeConst((Operator<Bytecode.Const>) expr, out);
			break;
		case Bytecode.OPCODE_fieldload:
			writeFieldLoad((Operator<Bytecode.FieldLoad>) expr, out);
			break;
		case Bytecode.OPCODE_indirectinvoke:
			writeIndirectInvoke((Operator<Bytecode.IndirectInvoke>) expr, out);
			break;
		case Bytecode.OPCODE_invoke:
			writeInvoke((Operator<Bytecode.Invoke>) expr, out);
			break;
		case Bytecode.OPCODE_lambda:
			writeLambda((Operator<Bytecode.Lambda>) expr, out);
			break;
		case Bytecode.OPCODE_record:
			writeRecordConstructor((Operator<Bytecode.Operator>) expr, out);
			break;
		case Bytecode.OPCODE_newobject:
			writeNewObject((Operator<Bytecode.Operator>) expr,out);
			break;
		case Bytecode.OPCODE_dereference:
		case Bytecode.OPCODE_logicalnot:
		case Bytecode.OPCODE_neg:
		case Bytecode.OPCODE_bitwiseinvert:
			writePrefixOperators((Operator<Bytecode.Operator>) expr,out);
			break;
		case Bytecode.OPCODE_all:
		case Bytecode.OPCODE_some:
		case Bytecode.OPCODE_none:
			writeQuantifier((Operator<Bytecode.Quantifier>) expr, out);
			break;
		case Bytecode.OPCODE_add:
		case Bytecode.OPCODE_sub:
		case Bytecode.OPCODE_mul:
		case Bytecode.OPCODE_div:
		case Bytecode.OPCODE_rem:
		case Bytecode.OPCODE_eq:
		case Bytecode.OPCODE_ne:
		case Bytecode.OPCODE_lt:
		case Bytecode.OPCODE_le:
		case Bytecode.OPCODE_gt:
		case Bytecode.OPCODE_ge:
		case Bytecode.OPCODE_logicaland:
		case Bytecode.OPCODE_logicalor:
		case Bytecode.OPCODE_bitwiseor:
		case Bytecode.OPCODE_bitwisexor:
		case Bytecode.OPCODE_bitwiseand:
		case Bytecode.OPCODE_shl:
		case Bytecode.OPCODE_shr:
		case Bytecode.OPCODE_is:
			writeInfixOperators((Operator<Bytecode.Operator>) expr, out);
			break;
		default:
			throw new IllegalArgumentException("unknown bytecode encountered");
		}
	}
	

	private void writeArrayLength(Operator<Bytecode.Operator> expr, PrintWriter out) {
		out.print("|");
		writeExpression(expr.getOperand(0), out);
		out.print("|");		
	}
	
	private void writeArrayIndex(Operator<Bytecode.Operator> expr, PrintWriter out) {
		writeExpression(expr.getOperand(0), out);
		out.print("[");
		writeExpression(expr.getOperand(1), out);
		out.print("]");
	}
	
	private void writeArrayInitialiser(Operator<Bytecode.Operator> expr, PrintWriter out) {
		Expr[] operands = expr.getOperands();
		out.print("[");
		for(int i=0;i!=operands.length;++i) {
			if(i != 0) {
				out.print(", ");
			}
			writeExpression(operands[i],out);
		}
		out.print("]");
	}

	private void writeArrayGenerator(Operator<Bytecode.Operator> expr, PrintWriter out) {
		out.print("[");
		writeExpression(expr.getOperand(0), out);
		out.print(" ; ");
		writeExpression(expr.getOperand(1), out);
		out.print("]");
	}
	private void writeConvert(Operator<Bytecode.Convert> expr, PrintWriter out) {
		out.print("(" + expr.getType() + ") ");
		writeExpression(expr.getOperand(0),out);
	}
	private void writeConst(Operator<Bytecode.Const> expr, PrintWriter out) {
		out.print(expr.getBytecode().constant());
	}
	private void writeFieldLoad(Operator<Bytecode.FieldLoad> expr, PrintWriter out) {
		writeBracketedExpression(expr.getOperand(0),out);
		out.print("." + expr.getBytecode().fieldName());		
	}
	private void writeIndirectInvoke(Operator<Bytecode.IndirectInvoke> expr, PrintWriter out) {
		Expr[] operands = expr.getOperands();
		writeExpression(operands[0],out);
		out.print("(");		
		for(int i=1;i!=operands.length;++i) {
			if(i!=1) {
				out.print(", ");
			}
			writeExpression(operands[i],out);
		}
		out.print(")");
	}
	private void writeInvoke(Operator<Bytecode.Invoke> expr, PrintWriter out) {
		out.print(expr.getBytecode().name() + "(");
		Expr[] operands = expr.getOperands();
		for(int i=0;i!=operands.length;++i) {
			if(i!=0) {
				out.print(", ");
			}
			writeExpression(operands[i],out);
		}
		out.print(")");
	}

	private void writeLambda(Operator<Bytecode.Lambda> expr, PrintWriter out) {
		out.print("&[");
		Expr[] environment = expr.getOperandGroup(SyntaxTree.ENVIRONMENT);
		for (int i = 0; i != environment.length; ++i) {
			SyntaxTree.Variable var = (SyntaxTree.Variable) environment[i];
			if (i != 0) {
				out.print(", ");
			}
			out.print(var.getType());
			out.print(" ");
			out.print(var.name());
		}
		out.print("](");
		Expr[] parameters = expr.getOperandGroup(SyntaxTree.PARAMETERS);
		for (int i = 0; i != parameters.length; ++i) {
			SyntaxTree.Variable var = (SyntaxTree.Variable) parameters[i];
			if (i != 0) {
				out.print(", ");
			}
			out.print(var.getType());
			out.print(" ");
			out.print(var.name());
		}
		out.print(" -> ");
		writeExpression(expr.getOperand(0), out);
		out.print(")");
	}
	
	private void writeRecordConstructor(Operator<Bytecode.Operator> expr, PrintWriter out) {
		Type.EffectiveRecord t = (Type.EffectiveRecord) expr.getType();
		ArrayList<String> fields = new ArrayList<String>(t.fields().keySet());
		Collections.sort(fields);
		Expr[] operands = expr.getOperands();
		out.print("{");
		for(int i=0;i!=operands.length;++i) {
			if(i != 0) {
				out.print(", ");
			}
			out.print(fields.get(i));
			out.print(" ");
			writeExpression(operands[i],out);
		}
		out.print("}");
	}

	private void writeNewObject(Operator<Bytecode.Operator> expr, PrintWriter out) {
		out.print("new ");
		writeExpression(expr.getOperand(0), out);
	}
	
	private void writePrefixOperators(Operator<Bytecode.Operator> expr, PrintWriter out) {
		// Prefix operators
		out.print(opcode(expr.getBytecode().kind()));
		writeBracketedExpression(expr.getOperand(0),out);
	}
	
	private void writeInfixOperators(Operator<Bytecode.Operator> c, PrintWriter out) {
		writeBracketedExpression(c.getOperand(0),out);
		out.print(" ");
		out.print(opcode(c.getBytecode().kind()));
		out.print(" ");
		writeBracketedExpression(c.getOperand(1),out);
		
	}

	private void writeQuantifier(Operator<Bytecode.Quantifier> c, PrintWriter out) {
		out.print(quantifierKind(c));
		out.print(" { ");
		for (int i = 0; i != c.numberOfOperandGroups(); ++i) {
			Expr[] range = c.getOperandGroup(i);
			if (i != 0) {
				out.print(", ");
			}
			SyntaxTree.Variable v = (SyntaxTree.Variable) range[SyntaxTree.VARIABLE];
			out.print(v.name());
			out.print(" in ");
			writeExpression(range[SyntaxTree.START], out);
			out.print("..");
			writeExpression(range[SyntaxTree.END], out);
		}
		out.print(" | ");
		writeExpression(c.getOperand(SyntaxTree.CONDITION), out);
		out.print(" } ");
	}
	
	private String quantifierKind(Operator<Bytecode.Quantifier> c) {
		switch(c.getOpcode()) {
		case Bytecode.OPCODE_none:
			return "no";
		case Bytecode.OPCODE_some:
			return "some";
		case Bytecode.OPCODE_all:
			return "all";
		}
		throw new IllegalArgumentException();
	}
	
	private static void writeModifiers(List<Modifier> modifiers, PrintWriter out) {
		for(Modifier m : modifiers) {
			out.print(m.toString());
			out.print(" ");
		}
	}
	
	private boolean needsBrackets(Bytecode.Expr e) {
		switch(e.getOpcode()) {
		case Bytecode.OPCODE_convert:
		case Bytecode.OPCODE_add:
		case Bytecode.OPCODE_sub:
		case Bytecode.OPCODE_mul:
		case Bytecode.OPCODE_div:
		case Bytecode.OPCODE_rem:
		case Bytecode.OPCODE_eq:
		case Bytecode.OPCODE_ne:
		case Bytecode.OPCODE_lt:
		case Bytecode.OPCODE_le:
		case Bytecode.OPCODE_gt:
		case Bytecode.OPCODE_ge:		
		case Bytecode.OPCODE_logicaland:
		case Bytecode.OPCODE_logicalor:		
		case Bytecode.OPCODE_bitwiseor:
		case Bytecode.OPCODE_bitwisexor:
		case Bytecode.OPCODE_bitwiseand:
		case Bytecode.OPCODE_shl:
		case Bytecode.OPCODE_shr:		
		case Bytecode.OPCODE_is:		
		case Bytecode.OPCODE_newobject:
		case Bytecode.OPCODE_dereference:
			return true;
		}
		return false;
	}
	
	private static String opcode(Bytecode.OperatorKind k) {
		switch(k) {
		case NEG:
			return "-";
		case NOT:
			return "!";
		case BITWISEINVERT:
			return "~";
		case DEREFERENCE:
			return "*";
		// Binary
		case ADD:
			return "+";
		case SUB:
			return "-";
		case MUL:
			return "*";
		case DIV:
			return "/";
		case REM:
			return "%";
		case EQ:
			return "==";
		case NEQ:
			return "!=";
		case LT:
			return "<";
		case LTEQ:
			return "<=";
		case GT:
			return ">";
		case GTEQ:
			return ">=";
		case AND:
			return "&&";
		case OR:
			return "||";
		case BITWISEOR:
			return "|";
		case BITWISEXOR:
			return "^";
		case BITWISEAND:
			return "&";
		case LEFTSHIFT:
			return "<<";
		case RIGHTSHIFT:
			return ">>";		
		case IS:
			return "is";
		case NEW:
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
