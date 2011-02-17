package wyone.io;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.*;

import wyil.util.Pair;
import static wyil.util.SyntaxError.*;
import wyone.core.*;
import static wyone.core.SpecFile.*;

public class JavaFileWriter {
	private PrintWriter out;
	private SpecFile specfile;
	
	public JavaFileWriter(Writer os) {
		out = new PrintWriter(os);
	}
	
	public JavaFileWriter(OutputStream os) {
		out = new PrintWriter(os);
	}
	
	public void write(SpecFile spec) {
		specfile = spec;
		int lindex = spec.filename.lastIndexOf('.');
		String className = spec.filename.substring(0,lindex);	
		out.println("import java.util.*;");
		out.println("import wyone.core.*;");
		out.println();
		out.println("public final class " + className + " {");
		
		HashMap<String,Set<String>> hierarhcy = new HashMap<String,Set<String>>();
		for(Decl d : spec.declarations) {
			if(d instanceof ClassDecl) {
				ClassDecl cd = (ClassDecl) d;
				for(String child : cd.children) {
					Set<String> parents = hierarhcy.get(child);
					if(parents == null) {
						parents = new HashSet<String>();
						hierarhcy.put(child,parents);
					}
					parents.add(cd.name);
				}
			}
		}
		
		for(Decl d : spec.declarations) {
			if(d instanceof TermDecl) {
				write((TermDecl) d, hierarhcy);
			} else if(d instanceof ClassDecl) {
				write((ClassDecl) d, hierarhcy);
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
		
	public void write(TermDecl decl, HashMap<String,Set<String>> hierarchy) {
		indent(1);out.print("public final static class " + decl.name + " extends Constructor");
		Set<String> parents = hierarchy.get(decl.name);
		if(parents != null) {
			out.print(" implements ");
			boolean firstTime=true;
			for(String parent : parents) {
				if(!firstTime) {
					out.print(",");
				}
				firstTime=false;
				out.print(parent);
			}
		}
		out.println(" {");
		indent(2);out.println("public " + decl.name + "(Collection<Constructor> subterms) {");
		indent(3);out.println("super(\"" + decl.name + "\",subterms);");
		indent(2);out.println("}");
		indent(1);out.println("}\n");
	}
	
	public void write(ClassDecl decl, HashMap<String,Set<String>> hierarchy) {
		indent(1);out.print("public static interface " + decl.name);
		Set<String> parents = hierarchy.get(decl.name);
		if(parents != null) {
			out.print(" extends ");
			boolean firstTime=true;
			for(String parent : parents) {
				if(!firstTime) {
					out.print(",");
				}
				firstTime=false;
				out.print(parent);
			}
		}
		out.println(" {");
		// will need more here
		indent(1);out.println("}\n");
	}
	
	public void write(RewriteDecl decl) {		
		// FIRST COMMENT CODE FROM SPEC FILE
		indent(1);out.print("// rewrite " + decl.name + "(");
		for(Pair<TypeDecl,String> td : decl.types){						
			out.print(td.first().type);
			out.print(" ");
			out.print(td.second());
		}
		out.println("):");
		for(RuleDecl rd : decl.rules) {
			indent(1);out.print("// ");
			if(rd.condition != null) {
				out.println("=> " + rd.result + ", if " + rd.condition);
			} else {
				out.println("=> " + rd.result);
			}
		}
		
		// NOW PRINT REAL CODE
		String mangle = nameMangle(decl.types);				
		indent(1);out.print("public static Constructor rewrite" + mangle + "(final " + decl.name + " target");
		if(!decl.types.isEmpty()) {
			out.print(", ");
		}
		for(Pair<TypeDecl,String> td : decl.types){						
			out.print("final ");
			write(td.first().type);
			out.print(" ");
			out.print(td.second());
		}
		out.println(") {");
		boolean defCase = false; 
		for(RuleDecl rd : decl.rules) {
			indent(2);
			if(rd.condition != null && defCase) {
				// this indicates a syntax error since it means we've got a
				// default case before a conditional case.
				syntaxError("case cannot be reached",specfile.filename,rd);
			} else if(rd.condition != null) {				
				out.print("if(");
				write(rd.condition);
				out.println(") {");
				indent(3);
				out.print("return ");
				write(rd.result);
				out.println(";");
				indent(2);
				out.println("}");
			} else {
				defCase = true;
				out.print("return ");
				write(rd.result);
				out.println(";");
			}
		}
		if(!defCase) {
			indent(2);
			out.println("return target");
		}
		indent(1);out.println("}\n");
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
				indent(4);out.print("return rewrite");out.print(nameMangle(rd.types));
				out.print("((" + name + ") target");
				i=0;
				for(Pair<TypeDecl,String> p : rd.types) {
					out.print(",  (");
					write(p.first().type);
					out.print(") target.subterms.get(" + i++ + ")");
				}
				out.println(");");
				indent(3);out.println("}");
			}
			indent(2);out.println("}");
		}
		indent(2);out.println("return target;");
		indent(1);out.println("}");
	}
	
	public void write(Expr expr) {
		out.print("...");
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
	
	protected String nameMangle(Collection<Pair<TypeDecl,String>> types) {
		String mangle = "_";
		for(Pair<TypeDecl,String> td : types) {	
			String str = Type.type2str(td.first().type);
			for(int i=0;i!=str.length();++i) {
				char c = str.charAt(i);
				mangle = mangle + Integer.toHexString(c);
			}
		}		
		return mangle;
	}
	
	protected void indent(int level)  {
		for(int i=0;i!=level;++i) {
			out.print("\t");
		}
	}
}
