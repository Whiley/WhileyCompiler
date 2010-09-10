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
		out.println("module: " + module.id());
		out.println("source-file: " + module.filename());
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
				write(0,td.constraint(),out);				
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
		for (Case c : method.cases()) {
			write(c, method, out);
		}
	}
	
	public static void write(Case mcase, Method method, PrintWriter out) {
		Type.Fun ft = method.type(); 
		out.print(ft.ret + " " + method.name() + "(");
		List<Type> pts = ft.params;
		List<String> names = mcase.parameterNames();
		for(int i=0;i!=names.size();++i) {
			String n = names.get(i);
			Type t = pts.get(i);
			if(i!=0) {
				out.print(", ");
			}
			out.print(t + " " + n);
		}
		out.println("):");
		
		if(mcase.precondition() != null) {
			out.println("precondition: ");
			write(0,mcase.precondition(),out);			
		}
		if(mcase.postcondition() != null) {
			out.println("postcondition: ");
			write(0,mcase.postcondition(),out);			
		}
		out.println("body: ");
		write(0,mcase.body(),out);	
	}
	
	public static void write(int indent, Block blk, PrintWriter out) {
		for(Stmt s : blk) {
			if(s.code instanceof Code.End) {
				indent--;
			}
			write(indent,s.code,s.attributes(),out);
			if(s.code instanceof Code.Loop) {
				Code.Loop loop = (Code.Loop) s.code; 
				indent++;				
				if(loop.invariant != null) {
					tabIndent(indent+1,out);
					out.println("invariant:");
					write(indent+1,loop.invariant,out);
				}
			} else if(s.code instanceof Code.Start) {
				indent++;
			}
		}
	}
	
	public static void write(int indent, Code c, List<Attribute> attributes, PrintWriter out) {		
		String line;		
		if(c instanceof Code.End) {
			tabIndent(indent+1,out);
			out.println(c);
			return;
		} else {
			tabIndent(indent+1,out);
			line = c == null ? "null" : c.toString();
		}
		
		while(line.length() < 40) {
			line += " ";
		}
		out.print(line);
		if(attributes.size() > 0) {
			out.print(" # ");
			boolean firstTime=true;
			for(Attribute a : attributes) {
				if(!firstTime) {
					out.print(", ");
				}
				firstTime=false;
				out.print(a);			
			}
		}
		out.println();
	}	
	public static void tabIndent(int indent, PrintWriter out) {
		indent = indent * 4;
		for(int i=0;i<indent;++i) {
			out.print(" ");
		}
	}
}
