package wyil.io;

import java.io.*;
import java.util.*;
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
		out.print(ft.ret + " " + method.name() + "(");
		List<Type> pts = ft.params;
		List<String> names = method.parameterNames();
		for(int i=0;i!=names.size();++i) {
			String n = names.get(i);
			Type t = pts.get(i);
			if(i!=0) {
				out.print(", ");
			}
			out.print(t + " " + n);
		}
		out.println("):");
		for(Code c : method.body()) {
			write(0,c);
		}
	}
	
	protected void write(int indent, Code c) {		
		if(c instanceof Code.Label) {
			tabIndent(indent);
			out.println(c);
		} else {
			tabIndent(indent+1);
			out.println(c);
		}
	}	
	
	protected void tabIndent(int indent) {
		indent = indent * 4;
		for(int i=0;i!=indent;++i) {
			out.print(" ");
		}
	}
}
