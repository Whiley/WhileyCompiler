package wyone.io;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import wyone.core.*;
import static wyone.core.SpecFile.*;

public class JavaFileWriter {
	private PrintWriter out;
	
	public JavaFileWriter(Writer os) {
		out = new PrintWriter(os);
	}
	
	public JavaFileWriter(OutputStream os) {
		out = new PrintWriter(os);
	}
	
	public void write(SpecFile spec) {
		int lindex = spec.filename.lastIndexOf('.');
		String className = spec.filename.substring(0,lindex);	
		out.println("import wyone.core.*;");
		out.println();
		out.println("public class " + className + " {");
		for(Decl d : spec.declarations) {
			write(d);
		}
		out.println("}");
		out.flush();
	}
	
	public void write(Decl decl) {
		if(decl instanceof TermDecl) {
			write((TermDecl)decl);
		}
	}
	
	public void write(TermDecl decl) {
		indent(1);out.println("public static " + decl.name + " extends Constructor {");
		indent(2);out.println("public " + decl.name + "(Collection<Constructor> subterms) {");
		indent(3);out.println("super(" + decl.name + ",subterms);");
		indent(2);out.println("}");
		indent(1);out.println("}");
	}
	
	protected void indent(int level)  {
		for(int i=0;i!=level;++i) {
			out.print("\t");
		}
	}
}
