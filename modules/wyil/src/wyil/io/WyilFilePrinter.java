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
	private boolean writeLabels = getLabels();
	private boolean writeAttributes = getAttributes();
	private boolean writeSlots = getSlots();

	public WyilFilePrinter(wybs.lang.Builder builder) {

	}

	public WyilFilePrinter(PrintWriter writer) {
		this.out = writer;
	}

	public WyilFilePrinter(OutputStream stream) {
		this.out = new PrintWriter(new OutputStreamWriter(stream));
	}

	public void setLabels(boolean flag) {
		writeLabels = flag;
	}

	public static boolean getLabels() {
		return true;
	}

	public static String describeLabels() {
		return "Include all labels in output";
	}

	public void setAttributes(boolean flag) {
		writeAttributes = flag;
	}

	public static boolean getAttributes() {
		return false;
	}

	public static String describeAttributes() {
		return "Include bytecode attributes in output";
	}

	public void setSlots(boolean flag) {
		writeSlots = flag;
	}

	public static boolean getSlots() {
		return false;
	}

	public static String describeSlots() {
		return "Include raw slow numbers in output";
	}

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
				out.println("where:");
				write(0, forest.getRoot(i), forest, out);
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
			write(0, method.body(), forest, out);
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
	
	private void write(int indent, int blockID, BytecodeForest forest, PrintWriter out) {
		BytecodeForest.Block block = forest.get(blockID);
		for(int i=0;i!=block.size();++i) {
			Bytecode code = block.get(i).code();
			if(code instanceof Bytecode.Label) {
				write(indent-1,code,forest,out);
			} else {
				write(indent,code,forest,out);
			}			
		}
	}

	private void write(int indent, Bytecode c, BytecodeForest forest, PrintWriter out) {
		tabIndent(indent+1,out);
		// First, write out code		
		if(c instanceof Bytecode.Assert) {
			Bytecode.Assert b = (Bytecode.Assert) c;
			out.println("assert:");
			write(indent+1,b.body(),forest,out);
		} else if(c instanceof Bytecode.Assume) {
			Bytecode.Assume b = (Bytecode.Assume) c;
			out.println("assume:");
			write(indent+1,b.body(),forest,out);
		} else if(c instanceof Bytecode.If) {
			Bytecode.If b = (Bytecode.If) c;
			out.println("if %" + b.operand(0) + ":");
			write(indent+1,b.trueBranch(),forest,out);
			if(b.hasFalseBlock()) {
				tabIndent(indent+1,out);
				out.println("else:");
				write(indent+1,b.falseBranch(),forest,out);
			}
		} else if(c instanceof Bytecode.Invariant) {
			Bytecode.Invariant b = (Bytecode.Invariant) c;
			out.println("invariant:");
			write(indent+1,b.body(),forest,out);
		} else if(c instanceof Bytecode.Quantify) {
			Bytecode.Quantify b = (Bytecode.Quantify) c;
			out.println("quantify(" + b.indexOperand() + " in " + b.startOperand() + ".." + b.endOperand() + "):");
			write(indent+1,b.body(),forest,out);
		} else if(c instanceof Bytecode.Loop) {
			Bytecode.Loop b = (Bytecode.Loop) c;
			out.println("loop:");
			write(indent+1,b.body(),forest,out);
		} else  {
			out.println(c.toString());

		}
	}

	private static void writeModifiers(List<Modifier> modifiers, PrintWriter out) {
		for(Modifier m : modifiers) {
			out.print(m.toString());
			out.print(" ");
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
