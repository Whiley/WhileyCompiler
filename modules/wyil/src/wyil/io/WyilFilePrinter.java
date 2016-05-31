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
			for (int invariant : td.invariants()) {
				out.print("where ");
				write(invariant, td, out);
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
		for (int precondition : method.preconditions()) {
			out.println();
			out.print("requires ");
			write(precondition, method, out);
		}
		for (int postcondition : method.postconditions()) {
			out.println();
			out.print("ensures ");
			write(postcondition, method, out);
		}
		if (method.blocks().size() > 0) {
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
		if(verbose) {
			List<Location> locations = enclosing.locations();
			for(int i=0;i!=locations.size();++i) {
				Location l = (Location) locations.get(i);
				tabIndent(1,out);
				out.println("// %" + i + " = " + l);
			}
		}
		writeBlock(0,0,enclosing,out);
	}
	
	private void writeBlock(int indent, int blockID, WyilFile.Declaration enclosing, PrintWriter out) {
		Bytecode.Block block = enclosing.getBlock(blockID);
		for (int i = 0; i != block.size(); ++i) {
			Bytecode.Stmt code = block.get(i).code();
			write(indent, code, enclosing, out);
		}
	}

	private void write(int indent, Bytecode.Stmt c, WyilFile.Declaration enclosing, PrintWriter out) {
		tabIndent(indent+1,out); 
		if(c instanceof Bytecode.Assert) {
			write(indent,(Bytecode.Assert)c, enclosing, out);
		} else if(c instanceof Bytecode.Assume) {
			write(indent,(Bytecode.Assume)c, enclosing, out);
		} else if(c instanceof Bytecode.Assign) {
			write(indent,(Bytecode.Assign)c, enclosing, out);
		} else if(c instanceof Bytecode.Break) {
			write(indent,(Bytecode.Break)c, enclosing, out);
		} else if(c instanceof Bytecode.Continue) {
			write(indent,(Bytecode.Continue)c, enclosing, out);
		} else if(c instanceof Bytecode.Debug) {
			write(indent,(Bytecode.Debug)c, enclosing, out);
		} else if(c instanceof Bytecode.DoWhile) {
			write(indent,(Bytecode.DoWhile)c, enclosing, out);
		} else if(c instanceof Bytecode.Fail) {
			write(indent,(Bytecode.Fail)c, enclosing, out);
		} else if(c instanceof Bytecode.If) {
			write(indent,(Bytecode.If)c, enclosing, out);			
		} else if(c instanceof Bytecode.While) {
			write(indent,(Bytecode.While)c, enclosing, out);
		} else if(c instanceof Bytecode.Return) {
			write(indent,(Bytecode.Return)c, enclosing, out);
		} else if(c instanceof Bytecode.Switch) {
			write(indent,(Bytecode.Switch)c, enclosing, out);
		} else  {
			throw new IllegalArgumentException("unknown bytecode encountered");
		}
	}
	
	private void write(int indent, Bytecode.Assert c, WyilFile.Declaration enclosing, PrintWriter out) {
		out.print("assert ");
		write(c.operand(),enclosing,out);
		out.println();
	}

	private void write(int indent, Bytecode.Assume c, WyilFile.Declaration enclosing, PrintWriter out) {
		out.print("assume ");
		write(c.operand(),enclosing,out);
		out.println();
	}
	
	private void write(int indent, Bytecode.Assign c, WyilFile.Declaration enclosing, PrintWriter out) {
		int[] lhs = c.leftHandSide();
		int[] rhs = c.rightHandSide();
		if(lhs.length > 0) {
			for(int i=0;i!=lhs.length;++i) {
				if(i!=0) { out.print(", "); }
				write(lhs[i],enclosing,out);
			}
			out.print(" = ");
		}
		for(int i=0;i!=rhs.length;++i) {
			if(i!=0) { out.print(", "); }
			write(rhs[i],enclosing,out);
		}
		out.println();
	}
	
	private void write(int indent, Bytecode.Break b, WyilFile.Declaration enclosing, PrintWriter out) {
		out.println("break");
	}
	
	private void write(int indent, Bytecode.Continue b, WyilFile.Declaration enclosing, PrintWriter out) {
		out.println("continue");
	}
	
	private void write(int indent, Bytecode.Debug b, WyilFile.Declaration enclosing, PrintWriter out) {
		out.println("debug");
	}
	
	private void write(int indent, Bytecode.DoWhile b, WyilFile.Declaration enclosing, PrintWriter out) {
		out.println("do:");
		writeBlock(indent+1,b.body(),enclosing,out);
		tabIndent(indent+1,out);
		out.print("while ");
		write(b.condition(),enclosing,out);
		// FIXME: add invariants
		out.println();
	}

	private void write(int indent, Bytecode.Fail c, WyilFile.Declaration enclosing, PrintWriter out) {
		out.println("fail");
	}
	
	private void write(int indent, Bytecode.If b, WyilFile.Declaration enclosing, PrintWriter out) {
		out.print("if ");
		write(b.condition(),enclosing,out);
		out.println(":");
		writeBlock(indent+1,b.trueBranch(),enclosing,out);
		if(b.hasFalseBranch()) {
			tabIndent(indent+1,out);
			out.println("else:");
			writeBlock(indent+1,b.falseBranch(),enclosing,out);
		}
	}
	
	private void write(int indent, Bytecode.While b, WyilFile.Declaration enclosing, PrintWriter out) {
		out.print("while ");
		write(b.condition(),enclosing,out);
		out.println(":");
		// FIXME: add invariants
		writeBlock(indent+1,b.body(),enclosing,out);		
	}
	
	private void write(int indent, Bytecode.Return b, WyilFile.Declaration enclosing, PrintWriter out) {
		int[] operands = b.operands();
		out.print("return");
		if(operands.length > 0) {
			out.print(" ");
			for(int i=0;i!=operands.length;++i) {
				if(i != 0) {
					out.print(", ");
				}
				write(operands[i],enclosing,out);
			}
		}
		out.println();
	}
	
	private void write(int indent, Bytecode.Switch b, WyilFile.Declaration enclosing, PrintWriter out) {
		out.print("switch ");
		write(b.operand(),enclosing,out);
		out.println(":");
		Bytecode.Case[] cases = b.cases(); 
		for(int i=0;i!=cases.length;++i) {
			Bytecode.Case cAse = cases[i];
			Constant[] values = cAse.values();
			tabIndent(indent+2,out);
			if(values.length == 0) {
				out.println("default:");
			} else {			
				out.print("case ");
				for(int j=0;j!=values.length;++j) {
					if(j != 0) {
						out.print(", ");
					}
					out.print(values[j]);
				}
				out.println(":");
			}
			writeBlock(indent+2,cAse.block(),enclosing,out);			
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
	private void writeBracketed(int operand, WyilFile.Declaration enclosing, PrintWriter out) {
		Location loc = enclosing.locations().get(operand);
		if(loc instanceof Location.Variable) {
			Location.Variable v = (Location.Variable) loc;
			out.print(v.name());
		} else {
			Location.Operand op = (Location.Operand) loc;
			boolean needsBrackets = needsBrackets(op.value());
			if(needsBrackets) {
				out.print("(");
			}
			write(op,enclosing,out);
			if(needsBrackets) {
				out.print(")");
			}
		}
	}

	private void write(int operand, WyilFile.Declaration enclosing, PrintWriter out) {
		Location loc = enclosing.locations().get(operand);
		if(loc instanceof Location.Variable) {
			Location.Variable v = (Location.Variable) loc;
			out.print(v.name());
		} else {
			write((Location.Operand) loc,enclosing,out);
		}
	}
	
	private void write(Location.Operand op, WyilFile.Declaration enclosing, PrintWriter out) {
		Bytecode.Expr c = op.value();
		if(c instanceof Bytecode.Convert) {
			write((Bytecode.Convert)c, enclosing, out);
		} else if(c instanceof Bytecode.Const) {
			write((Bytecode.Const)c, enclosing, out);
		} else if(c instanceof Bytecode.FieldLoad) {
			write((Bytecode.FieldLoad)c, enclosing, out);
		} else if(c instanceof Bytecode.IndirectInvoke) {
			write((Bytecode.IndirectInvoke)c, enclosing, out);
		} else if(c instanceof Bytecode.Invoke) {
			write((Bytecode.Invoke)c, enclosing, out);
		} else if(c instanceof Bytecode.Lambda) {
			write((Bytecode.Lambda)c, enclosing, out);
		} else if(c instanceof Bytecode.Operator) {
			write((Bytecode.Operator)c, op, enclosing, out);
		} else if(c instanceof Bytecode.Quantifier) {
			write((Bytecode.Quantifier)c, enclosing, out);
		} else  {
			throw new IllegalArgumentException("unknown bytecode encountered");
		}
	}
	
	private void write(Bytecode.Convert c, WyilFile.Declaration enclosing, PrintWriter out) {
		out.print("(" + c.type() + ") ");
		write(c.operand(),enclosing,out);
	}
	private void write(Bytecode.Const c, WyilFile.Declaration enclosing, PrintWriter out) {
		out.print(c.constant());
	}
	private void write(Bytecode.FieldLoad c, WyilFile.Declaration enclosing, PrintWriter out) {
		write(c.operand(),enclosing,out);
		out.print("." + c.fieldName());		
	}
	private void write(Bytecode.IndirectInvoke c, WyilFile.Declaration enclosing, PrintWriter out) {
		int[] operands = c.operands();
		write(operands[0],enclosing,out);
		out.print("(");		
		for(int i=1;i!=operands.length;++i) {
			if(i!=1) {
				out.print(", ");
			}
			write(operands[i],enclosing,out);
		}
		out.print(")");
	}
	private void write(Bytecode.Invoke c, WyilFile.Declaration enclosing, PrintWriter out) {
		out.print(c.name() + "(");
		int[] operands = c.operands();
		for(int i=0;i!=operands.length;++i) {
			if(i!=0) {
				out.print(", ");
			}
			write(operands[i],enclosing,out);
		}
		out.print(")");
	}
	private void write(Bytecode.Lambda c, WyilFile.Declaration enclosing, PrintWriter out) {
		out.print("&(");
		int[] parameters = c.parameters();
		for(int i=0;i!=parameters.length;++i) {			
			Location.Variable var = (Location.Variable) enclosing.getLocation(parameters[i]);
			if(i != 0) {
				out.print(", ");
			}
			out.print(var.type(0));
			out.print(" ");
			out.print(var.name());
		}
		out.print(" -> ");
		write(c.body(),enclosing,out);
		out.print(")");
	}
	private void write(Bytecode.Operator c, Location.Operand op, WyilFile.Declaration enclosing, PrintWriter out) {
		switch (c.kind()) {
			case ARRAYLENGTH:
				writeArrayLength(c,enclosing,out);
				break;
			case ARRAYINDEX:
				writeArrayIndex(c,enclosing,out);
				break;
			case ARRAYCONSTRUCTOR:
				writeArrayInitialiser(c,enclosing,out);
				break;
			case ARRAYGENERATOR:
				writeArrayGenerator(c,enclosing,out);
				break;
			case RECORDCONSTRUCTOR:
				writeRecordConstructor(c,op,enclosing,out);
				break;
			case NEW:
				writeNewObject(c,enclosing,out);
				break;
			case DEREFERENCE:
			case NOT:
			case NEG:
			case BITWISEINVERT:
				writePrefixOperators(c,enclosing,out);
				break;
			default: {				
				writeInfixOperators(c,enclosing,out);
			}
			}
	}
	
	private void writeArrayLength(Bytecode.Operator c, WyilFile.Declaration enclosing, PrintWriter out) {
		out.print("|");
		write(c.operand(0), enclosing, out);
		out.print("|");		
	}
	
	private void writeArrayIndex(Bytecode.Operator c, WyilFile.Declaration enclosing, PrintWriter out) {
		write(c.operand(0), enclosing, out);
		out.print("[");
		write(c.operand(1), enclosing, out);
		out.print("]");
	}
	
	private void writeArrayInitialiser(Bytecode.Operator c, WyilFile.Declaration enclosing, PrintWriter out) {
		int[] operands = c.operands();
		out.print("[");
		for(int i=0;i!=operands.length;++i) {
			if(i != 0) {
				out.print(", ");
			}
			write(operands[i],enclosing,out);
		}
		out.print("]");
	}

	private void writeArrayGenerator(Bytecode.Operator c, WyilFile.Declaration enclosing, PrintWriter out) {
		out.print("[");
		write(c.operand(0), enclosing, out);
		out.print(" ; ");
		write(c.operand(1), enclosing, out);
		out.print("]");
	}

	private void writeRecordConstructor(Bytecode.Operator c, Location.Operand op, WyilFile.Declaration enclosing, PrintWriter out) {
		Type.EffectiveRecord t = (Type.EffectiveRecord) op.type(0);
		ArrayList<String> fields = new ArrayList<String>(t.fields().keySet());
		Collections.sort(fields);
		int[] operands = c.operands();
		out.print("{");
		for(int i=0;i!=operands.length;++i) {
			if(i != 0) {
				out.print(", ");
			}
			out.print(fields.get(i));
			out.print(" ");
			write(operands[i],enclosing,out);
		}
		out.print("}");
	}

	private void writeNewObject(Bytecode.Operator c, WyilFile.Declaration enclosing, PrintWriter out) {
		out.print("new ");
		write(c.operand(0), enclosing, out);
	}
	
	private void writePrefixOperators(Bytecode.Operator c, WyilFile.Declaration enclosing, PrintWriter out) {
		// Prefix operators
		out.print(opcode(c.kind()));
		writeBracketed(c.operand(0),enclosing,out);
	}
	
	private void writeInfixOperators(Bytecode.Operator c, WyilFile.Declaration enclosing, PrintWriter out) {
		writeBracketed(c.operand(0),enclosing,out);
		out.print(" ");
		out.print(opcode(c.kind()));
		out.print(" ");
		writeBracketed(c.operand(1),enclosing,out);
		
	}
	private void write(Bytecode.Quantifier c, WyilFile.Declaration enclosing, PrintWriter out) {
		out.print(quantifierKind(c));
		out.print(" { ");
		Bytecode.Range[] ranges = c.ranges();
		for(int i=0;i!=ranges.length;++i) {
			Bytecode.Range range = ranges[i];
			if(i != 0) {
				out.print(", ");
			}
			Location.Variable v = (Location.Variable) enclosing.getLocation(range.variable());
			out.print(v.name());
			out.print(" in ");
			write(range.startOperand(),enclosing,out);
			out.print("..");
			write(range.endOperand(),enclosing,out);
		}
		out.print(" | ");
		write(c.body(),enclosing,out);
		out.print(" } ");
	}
	
	private String quantifierKind(Bytecode.Quantifier c) {
		switch(c.opcode()) {
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
		switch(e.opcode()) {
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
