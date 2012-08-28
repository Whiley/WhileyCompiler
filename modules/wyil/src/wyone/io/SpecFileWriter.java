package wyone.io;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import wyone.core.SpecFile;

public class SpecFileWriter {
	private PrintWriter out;

	public SpecFileWriter(Writer os) {
		out = new PrintWriter(os);
	}

	public SpecFileWriter(OutputStream os) {
		out = new PrintWriter(os);
	}

	public void flush() {
		out.flush();
	}
	
	public void write(SpecFile spec) {		
		for(SpecFile.Decl d : spec.declarations) {			
			write(d);
		}
		flush();
	}
	
	protected void write(SpecFile.Decl decl) {		
		if(decl instanceof SpecFile.TermDecl) {
			write((SpecFile.TermDecl) decl);
		} else if(decl instanceof SpecFile.ClassDecl){
			write((SpecFile.ClassDecl) decl);
		} else if(decl instanceof SpecFile.FunDecl) {
			write((SpecFile.FunDecl) decl);
		} 
	}
	
	protected void write(SpecFile.TermDecl decl) {
		myOut("term " + decl.type);
		myOut("");
	}
	
	protected void write(SpecFile.ClassDecl decl) {
		myOut("class " + decl.name + " as " + decl.children);
		myOut("");
	}

	protected void write(SpecFile.FunDecl decl) {
		myOut(decl.type.ret + " " + decl.name + "(" + decl.type.param + "):");
		myOut(1,"// declarations");
		for(int i=1;i!=decl.types.size();++i) {
			myOut(1,decl.types.get(i) + " %" + i);
		}
		myOut(1,"// bytecodes");
		for(int i=0;i!=decl.codes.size();++i) {
			myOut(1,decl.codes.get(i).toString());
		}
		myOut("");
	}
	
	protected void myOut(String line) {
		myOut(0, line);
	}
	
	protected void myOut(int level, String line) {
		for (int i = 0; i < level; ++i) {
			out.print("\t");
		}
		out.println(line);
	}

	protected void indent(int level) {
		for (int i = 0; i < level; ++i) {
			out.print("\t");
		}
	}
}
