package wyil.io;

import java.io.*;
import wyil.lang.*;
import wyil.lang.Module.*;

public class WyilFileWriter {
	private PrintWriter out;
	
	public WyilFileWriter(Writer os) {
		out = new PrintWriter(os);
	}
	
	public WyilFileWriter(OutputStream os) {
		out = new PrintWriter(os);
	}
	
	public void write(Module module) {
		out.println("module " + module.id());
		out.println("source file " + module.filename());
		out.println();
		for(ConstDef cd : module.constants()) {
			out.println("define " + cd.name() + " as " + cd.constant());
		}
		if(!module.constants().isEmpty()) {
			out.println();
		}
		for(TypeDef td : module.types()) {
			out.println("define " + td.name() + " as " + td.type());
		}
		if(!module.types().isEmpty()) {
			out.println();
		}		
		for(Method md : module.methods()) {
			write(md);
		}
		out.flush();
	}
	
	protected void write(Method method) {
		Type.Fun ft = method.type(); 
		out.println(method.name() + " " + ft + ":");
		for(Code c : method.body()) {
			write(1,c);
		}
	}
	
	protected void write(int indent, Code c) {
		tabIndent(indent);		
		out.println(c);
	}	
	
	protected void tabIndent(int indent) {
		indent = indent * 4;
		for(int i=0;i!=indent;++i) {
			out.print(" ");
		}
	}
}
