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
		return true; // default value
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
		for(WyilFile.Type td : module.types()) {
			Type t = td.type();
			String t_str;
			t_str = t.toString();
			writeModifiers(td.modifiers(),out);
			out.println("type " + td.name() + " : " + t_str);						
			BytecodeForest forest = td.invariant();
			for(int i=0;i!=forest.numRoots();++i) {
				out.println("where " + forest.getRoot(i));
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
		BytecodeForest forest = method.code();
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
		out.println(":");
		//
		for (int precondition : method.preconditions()) {
			out.println("requires:");
			write(0, precondition, forest, out);
		}

		for (int postcondition : method.postconditions()) {
			out.println("ensures:");
			write(0, postcondition, forest, out);
		}
		
		if (method.body() != null) {
			out.println("body: ");
			write(forest, out);
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
	
	private void write(BytecodeForest forest, PrintWriter out) {
		if(verbose) {
			List<BytecodeForest.Location> locations = forest.locations();
			for(int i=0;i!=locations.size();++i) {
				BytecodeForest.Location l = (BytecodeForest.Location) locations.get(i);
				tabIndent(1,out);
				out.println("// %" + i + " = " + l);
			}
		}
		write(0,0,forest,out);
	}
	
	private void write(int indent, int blockID, BytecodeForest forest, PrintWriter out) {
		BytecodeForest.Block block = forest.get(blockID);
		for(int i=0;i!=block.size();++i) {
			Bytecode.Stmt code = block.get(i).code();
			write(indent,code,forest,out);			
		}
	}

	private void write(int operand, BytecodeForest forest, PrintWriter out) {
		BytecodeForest.Location l = forest.locations().get(operand);
		if(l instanceof BytecodeForest.Variable) {
			BytecodeForest.Variable v = (BytecodeForest.Variable) l;
			out.print(v.name());
		} else {
			BytecodeForest.Operand o = (BytecodeForest.Operand) l;
			write(o.value(),forest,out);
		}
	}
	
	private void write(int indent, Bytecode.Stmt c, BytecodeForest forest, PrintWriter out) {
		tabIndent(indent+1,out); 
		if(c instanceof Bytecode.Assert) {
			write(indent,(Bytecode.Assert)c, forest, out);
		} else if(c instanceof Bytecode.Assume) {
			write(indent,(Bytecode.Assume)c, forest, out);
		} else if(c instanceof Bytecode.Assign) {
			write(indent,(Bytecode.Assign)c, forest, out);
		} else if(c instanceof Bytecode.Break) {
			write(indent,(Bytecode.Break)c, forest, out);
		} else if(c instanceof Bytecode.Continue) {
			write(indent,(Bytecode.Continue)c, forest, out);
		} else if(c instanceof Bytecode.Debug) {
			write(indent,(Bytecode.Debug)c, forest, out);
		} else if(c instanceof Bytecode.DoWhile) {
			write(indent,(Bytecode.DoWhile)c, forest, out);
		} else if(c instanceof Bytecode.Fail) {
			write(indent,(Bytecode.Fail)c, forest, out);
		} else if(c instanceof Bytecode.If) {
			write(indent,(Bytecode.If)c, forest, out);			
		} else if(c instanceof Bytecode.While) {
			write(indent,(Bytecode.While)c, forest, out);
		} else if(c instanceof Bytecode.Return) {
			write(indent,(Bytecode.Return)c, forest, out);
		} else  {
			throw new IllegalArgumentException("unknown bytecode encountered");
		}
	}
	
	private void write(int indent, Bytecode.Assert c, BytecodeForest forest, PrintWriter out) {
		out.print("assert ");
		write(c.operand(),forest,out);
		out.println();
	}

	private void write(int indent, Bytecode.Assume c, BytecodeForest forest, PrintWriter out) {
		out.print("assume ");
		write(c.operand(),forest,out);
		out.println();
	}
	
	private void write(int indent, Bytecode.Assign c, BytecodeForest forest, PrintWriter out) {
		int[] lhs = c.leftHandSide();
		int[] rhs = c.rightHandSide();
		for(int i=0;i!=lhs.length;++i) {
			if(i!=0) { out.print(", "); }
			write(lhs[i],forest,out);
		}
		out.print(" = ");
		for(int i=0;i!=rhs.length;++i) {
			if(i!=0) { out.print(", "); }
			write(rhs[i],forest,out);
		}
		out.println();
	}
	
	private void write(int indent, Bytecode.Break b, BytecodeForest forest, PrintWriter out) {
		out.println("break");
	}
	
	private void write(int indent, Bytecode.Continue b, BytecodeForest forest, PrintWriter out) {
		out.println("continue");
	}
	
	private void write(int indent, Bytecode.Debug b, BytecodeForest forest, PrintWriter out) {
		out.println("debug");
	}
	
	private void write(int indent, Bytecode.DoWhile b, BytecodeForest forest, PrintWriter out) {
		out.println("do:");
		write(indent+1,b.body(),forest,out);
		out.print("while");
		write(b.condition(),forest,out);
		// FIXME: add invariants
		out.println();
	}

	private void write(int indent, Bytecode.Fail c, BytecodeForest forest, PrintWriter out) {
		out.println("fail");
	}
	
	private void write(int indent, Bytecode.If b, BytecodeForest forest, PrintWriter out) {
		out.print("if");
		write(b.condition(),forest,out);
		out.println(":");
		write(indent+1,b.trueBranch(),forest,out);
		if(b.hasFalseBranch()) {
			tabIndent(indent+1,out);
			out.println("else:");
			write(indent+1,b.falseBranch(),forest,out);
		}
	}
	
	private void write(int indent, Bytecode.While b, BytecodeForest forest, PrintWriter out) {
		out.print("while");
		write(b.condition(),forest,out);
		out.println(":");
		// FIXME: add invariants
		write(indent+1,b.body(),forest,out);		
		out.println();
	}
	
	private void write(int indent, Bytecode.Return b, BytecodeForest forest, PrintWriter out) {
		int[] operands = b.operands();
		out.print("return");
		if(operands.length > 0) {
			out.print(" ");
			for(int i=0;i!=operands.length;++i) {
				if(i != 0) {
					out.print(", ");
				}
				write(operands[i],forest,out);
			}
		}
		out.println();
	}
	private void write(Bytecode.Expr c, BytecodeForest forest, PrintWriter out) {
		if(c instanceof Bytecode.Convert) {
			write((Bytecode.Convert)c, forest, out);
		} else if(c instanceof Bytecode.Const) {
			write((Bytecode.Const)c, forest, out);
		} else if(c instanceof Bytecode.FieldLoad) {
			write((Bytecode.FieldLoad)c, forest, out);
		} else if(c instanceof Bytecode.IndirectInvoke) {
			write((Bytecode.IndirectInvoke)c, forest, out);
		} else if(c instanceof Bytecode.Invoke) {
			write((Bytecode.Invoke)c, forest, out);
		} else if(c instanceof Bytecode.Lambda) {
			write((Bytecode.Lambda)c, forest, out);
		} else if(c instanceof Bytecode.Operator) {
			write((Bytecode.Operator)c, forest, out);
		} else if(c instanceof Bytecode.Quantifier) {
			write((Bytecode.Quantifier)c, forest, out);
		} else  {
			throw new IllegalArgumentException("unknown bytecode encountered");
		}
	}
	
	private void write(Bytecode.Convert c, BytecodeForest forest, PrintWriter out) {
		out.print("(" + c.type() + ") ");
		write(c.operand(),forest,out);
	}
	private void write(Bytecode.Const c, BytecodeForest forest, PrintWriter out) {
		out.print(c.constant());
	}
	private void write(Bytecode.FieldLoad c, BytecodeForest forest, PrintWriter out) {
		write(c.operand(),forest,out);
		out.print("." + c.fieldName());		
	}
	private void write(Bytecode.IndirectInvoke c, BytecodeForest forest, PrintWriter out) {
		out.print("indirectinvoke");
	}
	private void write(Bytecode.Invoke c, BytecodeForest forest, PrintWriter out) {
		out.print("invoke");
	}
	private void write(Bytecode.Lambda c, BytecodeForest forest, PrintWriter out) {
		out.print("lambda");
	}
	private void write(Bytecode.Operator c, BytecodeForest forest, PrintWriter out) {
		int[] operands = c.operands();
		if(operands.length == 1) {
			out.print(opcode(c.kind()));
			write(operands[0],forest,out);
		} else {
			String opcode = opcode(c.kind());
			for(int i=0;i!=operands.length;++i) {
				if(i != 0) {
					out.print(opcode);
				}
				write(operands[i],forest,out);
			}
		}
	}
	private void write(Bytecode.Quantifier c, BytecodeForest forest, PrintWriter out) {
		out.print("quantifier");
	}
	private static void writeModifiers(List<Modifier> modifiers, PrintWriter out) {
		for(Modifier m : modifiers) {
			out.print(m.toString());
			out.print(" ");
		}
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
			throw new IllegalArgumentException("unknown operator kind");
		}
	}

	private static String getLocal(int index, List<String> locals) {
		if(index < locals.size()) {
			// is a named local
			return locals.get(index);
		} else {
			return "%" + (index - locals.size());
		}
	}

	private static void tabIndent(int indent, PrintWriter out) {
		indent = indent * 4;
		for(int i=0;i<indent;++i) {
			out.print(" ");
		}
	}
}
