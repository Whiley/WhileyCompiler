package wyone.io;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.*;

import wyil.util.Pair;
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
			if(d instanceof TermDecl) {
				write((TermDecl) d);
			} else if(d instanceof RewriteDecl) {
				write((RewriteDecl) d);
			}
		}		
		HashMap<String,List<RewriteDecl>> dispatchTable = new HashMap();
		for(Decl d : spec.declarations) {
			if(d instanceof RewriteDecl) {
				RewriteDecl rd = (RewriteDecl) d;
				List<RewriteDecl> ls = dispatchTable.get(rd.name);
				if(ls == null) {
					ls =  new ArrayList<RewriteDecl>();
					dispatchTable.put(rd.name, ls);	
				}
				ls.add(rd);				
			}
		}
		write(dispatchTable);
		out.println("}");
		out.flush();
	}
		
	public void write(TermDecl decl) {
		indent(1);out.println("public static " + decl.name + " extends Constructor {");
		indent(2);out.println("public " + decl.name + "(Collection<Constructor> subterms) {");
		indent(3);out.println("super(" + decl.name + ",subterms);");
		indent(2);out.println("}");
		indent(1);out.println("}");
	}
	
	public void write(ClassDecl decl) {
		// not sure what I should do here.
	}
	
	public void write(RewriteDecl decl) {		
		indent(1);out.print("public static Constructor rewrite(" + decl.name + " target");
		if(!decl.types.isEmpty()) {
			out.print(", ");
		}
		for(Pair<TypeDecl,String> td : decl.types){						
			write(td.first().type);
			out.print(" ");
			out.print(td.second());
		}
		out.println(") {");
		
		indent(1);out.println("}");
	}
	
	public void write(HashMap<String,List<RewriteDecl>> dispatchTable) {
		indent(1);out.println("public static Constructor rewrite(Constructor target) {");
		boolean firstTime=true;
		for(Map.Entry<String,List<RewriteDecl>> e : dispatchTable.entrySet()) {
			String name = e.getKey();
			List<RewriteDecl> rules = e.getValue();
			indent(2);
			if(!firstTime) {
				out.print("else ");
			}
			firstTime=false;
			out.println("if(target instanceof " + name + ") {");
			int i=0;
			for(RewriteDecl rd : rules) {
				indent(3);out.println("if(target.subterms.size() == " + Integer.toString(rd.types.size()));
				for(Pair<TypeDecl,String> p : rd.types) {
					indent(3);out.print(" && target.subterms.get(" + i++ + ") instanceof ");
					write(p.first().type);
					if(i != rd.types.size()) {
						out.println();
					}
				}
				out.println(") {");
				indent(4);out.print("rewrite((" + name + ") target");
				i=0;
				for(Pair<TypeDecl,String> p : rd.types) {
					out.print(",  target.subterms.get(" + i++ + ")");
				}
				out.println(");");
				indent(3);out.println("}");
			}
			indent(2);out.println("}");
		}
		indent(1);out.println("}");
	}
	public void write(Type type) {
		if(type instanceof Type.Int) {
			out.write("BigInteger");
		} else if(type instanceof Type.Bool) {
			out.write("boolean");
		} else if(type instanceof Type.Named) {
			out.write(((Type.Named)type).name);
		} else if(type instanceof Type.List){
			out.write("ArrayList");
		} else if(type instanceof Type.Set){
			out.write("HashSet");
		} 
	}
	
	protected void indent(int level)  {
		for(int i=0;i!=level;++i) {
			out.print("\t");
		}
	}
}
