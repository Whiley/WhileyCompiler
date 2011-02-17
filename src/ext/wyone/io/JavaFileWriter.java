package wyone.io;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.util.*;

import wyil.jvm.rt.BigRational;
import wyil.util.Pair;
import wyil.util.SyntacticElement;
import wyil.util.SyntaxError;
import static wyil.util.SyntaxError.*;
import wyone.core.*;
import static wyone.core.Expr.*;
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
		out.println("import java.math.*;");
		out.println();
		out.println("public final class " + className + " {");
		
		indent(1);out.println("public interface Constructor {}\n");
		
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
		writeParser();
		writeMainMethod();
		out.println("}");
		out.flush();
	}
		
	public void write(TermDecl decl, HashMap<String,Set<String>> hierarchy) {
		indent(1);out.print("// " + decl.name);
		if(!decl.params.isEmpty()) {
			out.print("(");
			boolean firstTime=true;
			for(Type t : decl.params) {
				if(!firstTime) {
					out.print(",");
				}
				out.print(t);
			}	
			out.print(")");
		}
		out.println();
		indent(1);out.print("public final static class " + decl.name + " implements Constructor" );
		Set<String> parents = hierarchy.get(decl.name);
		if(parents != null) {				
			for(String parent : parents) {				
				out.print(",");								
				out.print(parent);
			}
		}
		out.println(" {");
		int idx=0;
		for(Type t : decl.params) {
			indent(2);out.print("public final ");
			write(t);			
			out.println(" c" + idx++ + ";");
		}		
		indent(2);out.print("private " + decl.name + "(");
		boolean firstTime=true;
		idx=0;
		for(Type t : decl.params) {
			if(!firstTime) {
				out.print(",");
			}
			firstTime=false;
			write(t);
			out.print(" c" + idx++);
		}
		out.println(") {");
		idx = 0;
		for(Type t : decl.params) {									
			indent(3);out.println("this.c" + idx + "=c" + idx++ + ";");
		}
		indent(2);out.println("}");				
		// now write the equals method
		indent(2);out.println("public boolean equals(Object o) {");
		indent(3);out.println("if(o instanceof " + decl.name + ") {");
		indent(4);out.println(decl.name + " v = (" + decl.name + ") o;");
		indent(4);out.print("return ");
		if(decl.params.isEmpty()) {
			out.println("true;");
		} else {
			idx = 0;
			firstTime=true;
			for(Type t : decl.params) {		
				if(!firstTime) {
					out.println(" && ");				
				}
				firstTime=false;
				out.print("c" + idx + ".equals(v.c" + idx++ + ")");
			}		
			out.println(";");
		}
		indent(3);out.println("}");
		indent(3);out.println("return false;");
		indent(2);out.println("}");
		// now write the hashCode method
		indent(2);out.println("public int hashCode() {");
		indent(3);out.print("return " + decl.name.hashCode());
		idx = 0;		
		for(Type t : decl.params) {					
			out.print(" + ");							
			firstTime=false;
			out.print("c" + idx++ + ".hashCode()");
		}		
		out.println(";");				
		indent(2);out.println("}");		
		// now write the toString method
		indent(2);out.println("public String toString() {");
		indent(3);out.print("return \"" + decl.name + "\"");
		if(decl.params.isEmpty()) {		
			out.println(";");
		} else {
			out.print(" + \"(\"");
			idx = 0;		
			for(Type t : decl.params) {					
				out.print(" + ");							
				firstTime=false;
				out.print("c" + idx++);
			}		
			out.println(" + \")\";");							
		}
		indent(2);out.println("}");
		indent(1);out.println("}\n");
		// now write the generator method
		if(decl.params.isEmpty()) {
			indent(1);out.println("public static final " + decl.name + " " + decl.name + " = new " + decl.name + "();\n");
		} else {
			indent(1);out.print("public static " + decl.name + " " + decl.name + "(");
			firstTime=true;
			idx=0;
			for(Type t : decl.params) {
				if(!firstTime) {
					out.print(",");
				}
				firstTime=false;
				write(t);
				out.print(" c" + idx++);
			}
			out.println(") {");
			indent(2);out.print("return new " + decl.name + "(");
			idx = 0;
			firstTime=true;
			for(Type t : decl.params) {	
				if(!firstTime) {
					out.print(",");
				}
				firstTime=false;
				out.print("c" + idx++);
			}
			out.println(");");			
			indent(1);out.println("}\n");
		}
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
			out.println("return target;");
		}
		indent(1);out.println("}\n");
	}
	
	public void write(HashMap<String,List<RewriteDecl>> dispatchTable) {
		// First, write outermost dispatch
		indent(1);out.println("public static Constructor rewrite(Constructor target) {");
		boolean firstTime=true;
		for(Map.Entry<String,List<RewriteDecl>> e : dispatchTable.entrySet()) {
			String name = e.getKey();			
			indent(2);
			if(!firstTime) {
				out.print("else ");
			}
			firstTime=false;
			out.println("if(target instanceof " + name + ") {");			
			indent(3);out.println("return rewrite((" + name + ") target);");							
			indent(2);out.println("}");
		}
		indent(2);out.println("return target;");
		indent(1);out.println("}\n");
		
		for(Map.Entry<String,List<RewriteDecl>> e : dispatchTable.entrySet()) {
			write(e.getKey(),e.getValue());
		}
	}
	
	public void write(String name, List<RewriteDecl> rules) {
		indent(1);out.println("public static Constructor rewrite(" + name + " target) {");
		for(RewriteDecl r : rules) {
			String mangle = nameMangle(r.types);
			indent(2);out.print("if(");
			int idx=0;
			boolean firstTime=true;
			for(Pair<TypeDecl,String> t : r.types) {
				if(!firstTime) {
					out.print(" && ");
				}
				firstTime=false;
				out.print("target.c" + idx++ + " instanceof ");
				write(t.first().type);				
			}
			out.println(") {");
			indent(3);out.print("return rewrite" + mangle + "(target");
			idx=0;			
			for(Pair<TypeDecl,String> t : r.types) {				
				out.print(", ");				
				firstTime=false;
				out.print("(");
				write(t.first().type);
				out.print(") target.c" + idx++);				
			}
			out.println(");");
			indent(2);out.println("}");
		}
		indent(2);out.println("return target;");
		indent(1);out.println("}\n");
	}
	
	public void write(Expr expr) {
		if(expr instanceof Constant) {
			write((Constant)expr);			
		} else if(expr instanceof Variable) {
			write((Variable)expr);
		} else if(expr instanceof BinOp) {
			write((BinOp)expr);
		}  else if(expr instanceof NaryOp) {
			write((NaryOp)expr);
		} else {		
			out.print("...");
		}
	}
	
	public void write(Constant c) {
		writeConstant(c.value,c);
	}
	public void writeConstant(Object v, SyntacticElement elem) {				
		if(v instanceof Boolean) {
			out.print(v);
		} else if(v instanceof BigInteger) {
			BigInteger bi = (BigInteger) v;
			out.print("new BigInteger(\"" + bi.toString() + "\")");
		} else if(v instanceof HashSet) {
			HashSet hs = (HashSet) v;
			out.print("new HashSet(){{");
			for(Object o : hs) {
				out.print("add(");
				writeConstant(o,elem);
				out.print(");");
			}
			out.print("}}");
		} else {
			syntaxError("unknown constant encountered (" + v + ")",specfile.filename,elem);
		}
	}
	
	public void write(Variable v) {
		out.print(v.var);
	}
	
	public void write(BinOp bop) {
		
		switch(bop.op) {
		case ADD:
			write(bop.lhs);
			out.print(".add(");
			write(bop.rhs);
			out.print(")");
			break;
		case SUB:
			write(bop.lhs);
			out.print(".subtract(");
			write(bop.rhs);
			out.print(")");
			break;
		case MUL:
			write(bop.lhs);
			out.print(".multiply(");
			write(bop.rhs);
			out.print(")");
			break;
		case DIV:
			write(bop.lhs);
			out.print(".divide(");
			write(bop.rhs);
			out.print(")");
			break;
		case EQ:
			write(bop.lhs);
			out.print(".equals(");
			write(bop.rhs);
			out.print(")");
			break;
		case NEQ:
			out.print("!");
			write(bop.lhs);
			out.print(".equals(");
			write(bop.rhs);
			out.print(")");
			break;
		case LT:
			write(bop.lhs);
			out.print(".compareTo(");
			write(bop.rhs);
			out.print(")<0");
			break;
		case LTEQ:
			write(bop.lhs);
			out.print(".compareTo(");
			write(bop.rhs);
			out.print(")<=0");
			break;
		case GT:
			write(bop.lhs);
			out.print(".compareTo(");
			write(bop.rhs);
			out.print(")>0");
			break;
		case GTEQ:
			write(bop.lhs);
			out.print(".compareTo(");
			write(bop.rhs);
			out.print(")>=0");
			break;
		case ELEMENTOF:
			write(bop.rhs);
			out.print(".contains(");
			write(bop.lhs);
			out.print(")");
			break;
		default:
			syntaxError("unknown binary operator encountered: " + bop,specfile.filename,bop);
		}		
	}
	
	public void write(NaryOp nop) {				
		
		switch(nop.nop) {
		case SETGEN:
			out.print("new HashSet(){{");
			for(Expr e : nop.arguments) {
				out.print("add(");
				write(e);
				out.print(");");
			}
			out.print("}}");
			break;
		case LISTGEN:
			out.print("new ArrayList(){{");
			for(Expr e : nop.arguments) {
				out.print("add(");
				write(e);
				out.print(");");
			}
			out.print("}}");
			break;
		default:
			syntaxError("need to implement sublist!",specfile.filename,nop);
		} 
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
	
	protected void writeMainMethod() {
		indent(1);out.println("public static void main(String[] args) {");
		indent(2);out.println("Parser parser = new Parser(args[0]);");
		indent(2);out.println("Constructor c = parser.parseTerm();");
		indent(2);out.println("System.out.println(\"PARSED: \" + c);");
		indent(2);out.println("System.out.println(\"REWROTE: \" + rewrite(c));");
		indent(1);out.println("}");
	}
	
	protected void writeParser() {
		indent(1);out.println("public static final class Parser {");
		indent(2);out.println("private final String input;");
		indent(2);out.println("private int pos;");		
		writeParserConstructor();
		writeParseTerm();
		writeParseSet();
		writeParseNumber();		
		writeParseIdentifier();
		writeMatch();
		writeSkipWhiteSpace();
		indent(1);out.println("}");
		writeSyntaxError();
	}
	
	protected void writeParserConstructor() {
		indent(2);out.println("public Parser(String input) {");
		indent(3);out.println("this.input = input;");
		indent(3);out.println("this.pos = 0;");
		indent(2);out.println("}");
	}
	
	protected void writeParseSet() {
		indent(2);out.println("protected HashSet parseSet() {");
		indent(3);out.println("HashSet r = new HashSet();");
		indent(3);out.println("match(\"{\");");
		indent(3);out.println("boolean firstTime=true;");
		indent(3);out.println("while(pos < input.length() && input.charAt(pos) != '}') {");
		indent(4);out.println("if(!firstTime) {");
		indent(5);out.println("match(\",\");");
		indent(4);out.println("}");
		indent(4);out.println("firstTime=false;");
		indent(4);out.println("r.add(parseTerm());");
		indent(3);out.println("}");
		indent(3);out.println("match(\"}\");");
		indent(3);out.println("return r;");
		indent(2);out.println("}");
	}
	
	protected void writeParseNumber() {
		indent(2);out.println("protected Number parseNumber() {");		
		indent(3);out.println("int start = pos;");
		indent(3);out.println("while (pos < input.length() && Character.isDigit(input.charAt(pos))) {");
		indent(4);out.println("pos = pos + 1;");
		indent(3);out.println("}");		
		indent(3);out.println("return new BigInteger(input.substring(start, pos));");								
		indent(2);out.println("}");		
	}
	
	protected void writeParseTerm() {
		indent(2);out.println("protected Constructor parseTerm() {");
		indent(3);out.println("int start = pos;");
		indent(3);out.println("String name = parseIdentifier();");
		for(Decl d : specfile.declarations) {
			if(d instanceof TermDecl) {
				TermDecl td = (TermDecl) d;
				indent(3);out.println("if(name.equals(\"" + td.name + "\")) { return parse" + td.name + "(); }");
			}
		}
		indent(3);out.println("throw new SyntaxError(\"unrecognised term: \" + name,start,pos);");
		indent(2);out.println("}");
		for(Decl d : specfile.declarations) {
			if(d instanceof TermDecl) {				
				writeParseTerm((TermDecl) d);
			}
		}
	}
	
	protected void writeParseTerm(TermDecl term) {
		indent(2);out.println("public " + term.name +" parse" + term.name + "() {");		
		
		if(term.params.isEmpty()) {
			indent(3);out.println("return " + term.name + ";");
		} else {
			indent(3);out.println("match(\"(\");");
			int idx=0;
			for(Type t : term.params) {
				indent(3);write(t);out.print(" e" + idx++ + " = ");
				writeTypeDispatch(t);
				out.println(";");				
			}
			indent(3);out.println("match(\")\");");
			indent(3);out.print("return " + term.name + "(");		
			for(int i=0;i!=term.params.size();i++) {
				if(i != 0) {
					out.print(", ");
				}
				out.print("e" + i);
			}
			out.println(");");
		}	
		indent(2);out.println("}");
	}
	
	public void writeTypeDispatch(Type t) {
		if(t instanceof Type.Set) {
			out.print("parseSet()");
		} else if(t instanceof Type.Int) {
			out.print("parseNumber()");
		} else 	{	
			Type.Named tn = (Type.Named) t;
			out.print("(" + tn.name + ") parseTerm()");
		}
	}
	
	public void writeParseIdentifier() {
		indent(2);out.println("protected String parseIdentifier() {");
		indent(3);out.println("int start = pos;");		
		indent(3);out.println("while (pos < input.length() &&");
		indent(4);out.println("Character.isJavaIdentifierPart(input.charAt(pos))) {");
		indent(4);out.println("pos++;");								
		indent(3);out.println("}");		
		indent(3);out.println("return input.substring(start,pos);");
		indent(2);out.println("}");
	}	
	protected void writeMatch() {
		indent(2);out.println("protected void match(String x) {");
		indent(3);out.println("skipWhiteSpace();");			
		indent(3);out.println("if((input.length()-pos) < x.length()) {");
		indent(4);out.println("throw new SyntaxError(\"expecting \" + x,pos,pos);");
		indent(3);out.println("}");
		indent(3);out.println("if(!input.substring(pos,pos+x.length()).equals(x)) {");
		indent(4);out.println("throw new SyntaxError(\"expecting \" + x,pos,pos);");			
		indent(3);out.println("}");
		indent(3);out.println("pos += x.length();");
		indent(2);out.println("}");
	}
	
	protected void writeSkipWhiteSpace() {
		indent(2);out.println("protected void skipWhiteSpace() {");
		indent(3);out.println("while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {");			
		indent(4);out.println("pos = pos + 1;");
		indent(3);out.println("}");
		indent(2);out.println("}");		
	}
	
	protected void writeSyntaxError() {
		indent(1);out.println("public static final class SyntaxError extends RuntimeException {");				
		indent(2);out.println("public final int start;");
		indent(2);out.println("public final int end;");		
			
		indent(2);out.println("public SyntaxError(String msg, int start, int end) {");
		indent(3);out.println("super(msg);");		
		indent(3);out.println("this.start=start;");		
		indent(3);out.println("this.end=end;");		
		indent(2);out.println("}");
		indent(1);out.println("}");
	}
	
	protected void indent(int level)  {
		for(int i=0;i!=level;++i) {
			out.print("\t");
		}
	}
}
