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
		out.println("module: " + module.id());
		out.println("source file: " + module.filename());
		out.println();
		for(ConstDef cd : module.constants()) {
			System.out.println("define " + cd.name() + " as " + cd.constant());
		}
		out.println();
		for(TypeDef td : module.types()) {
			System.out.println("define " + td.name() + " as " + td.type());
		}
		out.println();
		for(Method md : module.methods()) {
			write(md);
		}
	}
	
	protected void write(Method method) {
		Type.Fun ft = method.type(); 
		out.print(method.name() + " : " + ft);
	}
}
