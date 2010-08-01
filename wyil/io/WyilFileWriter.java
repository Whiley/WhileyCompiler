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
			if(td.constraint() != null) {
				for(Code c : td.constraint()) {
					write(0,c,out);
				}
			}
		}
		if(!module.types().isEmpty()) {
			out.println();
		}		
		for(Method md : module.methods()) {
			write(md,out);
			out.println();
		}
		out.flush();
	}
	
	public static void write(Method method, PrintWriter out) {
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
			write(0,c,out);
		}
	}
	
	public static void write(int indent, Code c, PrintWriter out) {		
		if(c instanceof Code.Label) {
			tabIndent(indent,out);
			out.println(c);
		} else {
			tabIndent(indent+1,out);
			out.println(c);
		}
	}	
	
	public static void tabIndent(int indent, PrintWriter out) {
		indent = indent * 4;
		for(int i=0;i!=indent;++i) {
			out.print(" ");
		}
	}
}
