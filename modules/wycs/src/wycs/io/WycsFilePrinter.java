package wycs.io;

import static wybs.lang.SyntaxError.internalFailure;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import wybs.lang.SyntacticElement;
import wycs.core.Code;
import wycs.core.SemanticType;
import wycs.core.WycsFile;

public class WycsFilePrinter {
private PrintWriter out;
	
	public WycsFilePrinter(OutputStream writer) throws UnsupportedEncodingException {
		this(new OutputStreamWriter(writer,"UTF-8"));		
	}
	
	public WycsFilePrinter(Writer writer) {
		this.out = new PrintWriter(writer);		
	}
	
	public void write(WycsFile wf) {
		for(WycsFile.Declaration d : wf.declarations()) {
			write(wf, d);
			out.println();
		}
		out.flush();
	}
	
	private void write(WycsFile wf, WycsFile.Declaration s) {
		if(s instanceof WycsFile.Function) {
			write(wf,(WycsFile.Function)s);
		} else if(s instanceof WycsFile.Macro) {
			write(wf,(WycsFile.Macro)s);
		} else if(s instanceof WycsFile.Assert) {
			write(wf,(WycsFile.Assert)s);
		} else {
			internalFailure("unknown statement encountered " + s,
					wf.filename(), (SyntacticElement) s);
		}
		out.println();
	}
	
	public void write(WycsFile wf, WycsFile.Function s) {
		out.print("function ");		
		out.print(s.name);
		SemanticType.Var[] generics = s.type.generics();
		if(generics.length > 0) {
			out.print("<");
			boolean firstTime=true;
			for(SemanticType.Var g : generics) {
				if(!firstTime) {
					out.print(", ");
				}
				firstTime=false;
				out.print(g.name());
			}
			out.print(">");
		}
		out.print(s.type.element(0) + " => " + s.type.element(1));		
		if(s.constraint != null) {
			out.println(" where:");
			indent(1);
			write(wf,s.constraint,0);
		}
	}
	
	public void write(WycsFile wf, WycsFile.Macro s) {
		out.print("define ");
		
		out.print(s.name);
		SemanticType.Var[] generics = s.type.generics();
		if(generics.length > 0) {
			out.print("<");
			boolean firstTime=true;
			for(SemanticType.Var g : generics) {
				if(!firstTime) {
					out.print(", ");
				}
				firstTime=false;
				out.print(g.name());
			}
			out.print(">");
		}
		out.print(s.type.from() + " => " + s.type.to());
		if(s.condition != null) {
			out.println(" as:");
			write(wf,s.condition,0);
		}
	}
	
	public void write(WycsFile wf, WycsFile.Assert s) {
		out.print("assert ");
		if(s.message != null) {
			out.print("\"" + s.message + "\"");
		}
		out.println(":");
		indent(1);
		write(wf,s.condition,0);		
		out.println();
	}
	
	public int write(WycsFile wf, Code<?> code, int index) {
		int[] operands = new int[code.operands.length];
		int next = index;
		for(int i=0;i!=operands.length;++i) {
			next = write(wf,code.operands[i],next);
			operands[i] = next;
		}
		indent(1);
		next = next + 1;
		out.print("#" + next + " = ");
		out.print(code.opcode.toString() + "(");
		for(int i=0;i!=operands.length;++i) {
			if(i != 0) {
				out.print(", ");
			}
			out.print("#" + operands[i]);
		}
		out.println(") : " + code.type);
		return next;
	}
	
	private void indent(int indent) {
		indent = indent * 4;
		for(int i=0;i<indent;++i) {
			out.print(" ");
		}
	}
}
