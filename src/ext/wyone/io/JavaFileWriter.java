package wyone.io;

import java.io.BufferedReader;
import java.io.FileReader;
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
		out.println("import java.io.*;");
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
		indent(1);out.print("public static interface " + decl.name + " extends Constructor");
		Set<String> parents = hierarchy.get(decl.name);
		if(parents != null) {			
			for(String parent : parents) {				
				out.print(",");								
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
			for(Pair<String,Expr> p : rd.lets) {				
				// FIXME: need to know the type!!
				Pair<List<String>,String> r = translate(p.second());
				write(r.first(),2);
				indent(2);out.println("Object " + p.first() + " = " + r.second() + ";");								
			}			
			if(rd.condition != null && defCase) {
				// this indicates a syntax error since it means we've got a
				// default case before a conditional case.
				syntaxError("case cannot be reached",specfile.filename,rd);
			} else if(rd.condition != null) {								
				Pair<List<String>,String> r = translate(rd.condition);
				write(r.first(),2);						
				indent(2);out.println("if(" + r.second() + ") {");								
				r = translate(rd.result);
				write(r.first(),3);
				indent(3);out.println("return " + r.second() + ";");				
				indent(2);out.println("}");
			} else {
				defCase = true;				
				Pair<List<String>,String> r = translate(rd.result);
				write(r.first(),2);
				indent(2);out.println("return " + r.second() + ";");				
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
		
		writeRewriteSet();		
	}
	
	public void writeRewriteSet() {
		indent(1);out.println("public static HashSet rewrite(HashSet os) {");
		indent(2);out.println("HashSet rs = new HashSet();");
		indent(2);out.println("for(Object o : os) {");
		// FIXME: the following line is broken
		indent(3);out.println("rs.add(rewrite((Constructor) o));");
		indent(2);out.println("}");
		indent(2);out.println("return rs;");
		indent(1);out.println("}");
	}
	
	public void write(String name, List<RewriteDecl> rules) {
		List<Type> params = null;
		// FIXME: this is a hack
		for(Decl d : specfile.declarations) {
			if(d instanceof TermDecl) {
				TermDecl td = (TermDecl) d;
				if(td.name.equals(name)) {
					params = td.params;
				}
			}
		}
		indent(1);out.println("public static Constructor rewrite(" + name + " target) {");
		indent(2);out.print("target = " + name + "(");
		for(int i=0;i!=params.size();++i) {
			Type t= params.get(i);
			if(i != 0) {
				out.print(", ");
			}
			out.print("(");write(t);out.print(")");			
			out.print("rewrite(target.c" + i + ")");
		}
		out.println(");");
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
	
	public void write(List<String> inserts, int level) {
		for(String s : inserts) {
			indent(level);out.println(s);
		}
	}
	
	public Pair<List<String>,String> translate(Expr expr) {
		if(expr instanceof Constant) {
			return translate((Constant)expr);			
		} else if(expr instanceof Variable) {
			return translate((Variable)expr);
		} else if(expr instanceof BinOp) {
			return translate((BinOp)expr);
		} else if(expr instanceof NaryOp) {
			return translate((NaryOp)expr);
		} else if(expr instanceof Comprehension) {
			return translate((Comprehension)expr);
		} else if(expr instanceof Invoke) {
			return translate((Invoke)expr);
		} else {		
			syntaxError("unknown expression encountered",specfile.filename,expr);
			return null;
		}
	}
	
	public Pair<List<String>,String> translate(Constant c) {
		return translate(c.value,c);
	}
	public Pair<List<String>,String> translate(Object v, SyntacticElement elem) {				
		String r=null;
		List<String> inserts = Collections.EMPTY_LIST;
		if(v instanceof Boolean) {
			r = v.toString();
		} else if(v instanceof BigInteger) {
			BigInteger bi = (BigInteger) v;
			r = "new BigInteger(\"" + bi.toString() + "\")";
		} else if(v instanceof HashSet) {
			HashSet hs = (HashSet) v;
			r = "new HashSet(){{";
			for(Object o : hs) {
				Pair<List<String>,String> is = translate(o,elem);
				inserts = concat(inserts,is.first());
				r = r + "add(" + is.second() + ");";
								
			}
			r = r + "}}";
		} else {
			syntaxError("unknown constant encountered (" + v + ")",specfile.filename,elem);			
		}
		return new Pair(inserts,r);
	}
	
	public Pair<List<String>,String> translate(Variable v) {
		return new Pair(Collections.EMPTY_LIST,v.var);
	}
	
	public Pair<List<String>,String> translate(BinOp bop) {
		Pair<List<String>,String> lhs = translate(bop.lhs);
		Pair<List<String>,String> rhs = translate(bop.rhs);
		List<String> inserts = concat(lhs.first(),rhs.first());
		switch(bop.op) {
		case ADD:						
			return new Pair(inserts,lhs.second() + ".add(" + rhs.second() + ")");						
		case SUB:
			return new Pair(inserts,lhs.second() + ".subtract(" + rhs.second() + ")");			
		case MUL:
			return new Pair(inserts,lhs.second() + ".multiply(" + rhs.second() + ")");			
		case DIV:
			return new Pair(inserts,lhs.second() + ".divide(" + rhs.second() + ")");			
		case EQ:
			return new Pair(inserts,lhs.second() + ".equals(" + rhs.second() + ")");						
		case NEQ:
			return new Pair(inserts,"!" + lhs.second() + ".equals(" + rhs.second() + ")");			
		case LT:
			return new Pair(inserts,lhs.second() + ".compareTo(" + rhs.second() + ")<0");			
		case LTEQ:
			return new Pair(inserts,lhs.second() + ".compareTo(" + rhs.second() + ")<=0");			
		case GT:
			return new Pair(inserts,lhs.second() + ".compareTo(" + rhs.second() + ")>0");			
		case GTEQ:
			return new Pair(inserts,lhs.second() + ".compareTo(" + rhs.second() + ")>=0");			
		case ELEMENTOF:
			return new Pair(inserts,rhs.second() + ".contains(" + lhs.second() + ")");			
		default:
			syntaxError("unknown binary operator encountered: " + bop,specfile.filename,bop);
			return null;
		}		
	}
	
	public Pair<List<String>,String> translate(NaryOp nop) {				
		List<String> inserts = Collections.EMPTY_LIST;
		String r = null;
		switch(nop.nop) {
		case SETGEN:
			r="new HashSet(){{";
			for(Expr e : nop.arguments) {
				Pair<List<String>,String> p = translate(e);
				inserts = concat(inserts,p.first());
				r = r + "add(" + p.second() + ");";								
			}
			r = r + "}}";
			break;
		case LISTGEN:
			r="new ArrayList(){{";
			for(Expr e : nop.arguments) {
				Pair<List<String>,String> p = translate(e);
				inserts = concat(inserts,p.first());
				r = r + "add(" + p.second() + ");";								
			}
			r = r + "}}";			
			break;
		default:
			syntaxError("need to implement sublist!",specfile.filename,nop);
		} 
		
		return new Pair(inserts,r);
	}
	
	public  Pair<List<String>,String> translate(Invoke ivk) {
		List<String> inserts = Collections.EMPTY_LIST;
		String r = ivk.name + "(";
		boolean firstTime=true;
		for(Expr e : ivk.arguments) {
			Pair<List<String>,String> es = translate(e);
			inserts = concat(inserts,es.first());
			if(!firstTime) {
				r += ", ";
			}
			firstTime=false;
			r += es.second();
		}
		
		return new Pair(inserts,r + ")");
	}
	
	public  Pair<List<String>,String> translate(Comprehension c) {
		if(c.cop == COp.SOME) {
			return translateSome(c);
		} else {
			return translateAll(c);
		}
	}
	
	public Pair<List<String>,String> translateSome(Comprehension c) {
		ArrayList<String> inserts = new ArrayList<String>();
		String tmp = freshVar();
		inserts.add("boolean " + tmp + " = false;");
		int l=0;
		for(Pair<String,Expr> src : c.sources) {
			Pair<List<String>,String> r = translate(src.second());
			for(String i : r.first()) {
				inserts.add(indentStr(l) + i);
			}
			String tmp2 = freshVar();
			inserts.add(indentStr(l++) + "for(Object " + tmp2 + " : " + r.second() + ") {");
			// FIXME: need type information here
			inserts.add(indentStr(l) + "Object " + src.first() + " = " + tmp2 + ";");
		}
		Pair<List<String>,String> r = translate(c.condition);
		for(String i : r.first()) {
			inserts.add(indentStr(l) + i);
		}
		inserts.add(indentStr(l) + "if(" + r.second() + ") { " + tmp + " = true; }");
		
		for(Pair<String,Expr> src : c.sources) {
			inserts.add(indentStr(--l) + "}");
		}
		return new Pair(inserts,tmp);
	}
	
	public Pair<List<String>,String> translateAll(Comprehension c) {
		ArrayList<String> inserts = new ArrayList<String>();
		String tmp = freshVar();
		inserts.add("boolean " + tmp + " = true;");
		int l=0;
		for(Pair<String,Expr> src : c.sources) {
			Pair<List<String>,String> r = translate(src.second());
			for(String i : r.first()) {
				inserts.add(indentStr(l) + i);
			}
			String tmp2 = freshVar();
			inserts.add(indentStr(l++) + "for(Object " + tmp2 + " : " + r.second() + ") {");
			// FIXME: need type information here
			inserts.add(indentStr(l) + "Object " + src.first() + " = " + tmp2 + ";");
		}
		Pair<List<String>,String> r = translate(c.condition);
		for(String i : r.first()) {
			inserts.add(indentStr(l) + i);
		}
		inserts.add(indentStr(l) + "if(" + r.second() + ") { " + tmp + " = false; }");
		
		for(Pair<String,Expr> src : c.sources) {
			inserts.add(indentStr(--l) + "}");
		}
		return new Pair(inserts,tmp);
	}
	
	public void write(Type type) {
		if(type instanceof Type.Int) {
			out.write("BigInteger");
		} else if(type instanceof Type.Bool) {
			out.write("boolean");
		} else if(type instanceof Type.Strung) {
			out.write("String");
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
		indent(1);out.println("public static void main(String[] args) throws IOException {");
		indent(3);out.println("BufferedReader in = new BufferedReader(new FileReader(args[0]));");		
		indent(3);out.println("StringBuffer text = new StringBuffer();");
		indent(3);out.println("while (in.ready()) {");
		indent(4);out.println("text.append(in.readLine());");
		indent(4);out.println("text.append(\"\\n\");");
		indent(3);out.println("}");
		indent(2);out.println("try {");
		indent(3);out.println("Parser parser = new Parser(text.toString());");
		indent(3);out.println("Constructor c = parser.parseTerm();");
		indent(3);out.println("System.out.println(\"PARSED: \" + c);");
		indent(3);out.println("System.out.println(\"REWROTE: \" + rewrite(c));");
		indent(2);out.println("} catch(SyntaxError ex) {");
		indent(3);out.println("System.err.println(ex.getMessage());");
		indent(2);out.println("}");
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
		writeParseStrung();
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
	
	protected void writeParseStrung() {
		indent(2);out.println("protected String parseStrung() {");		
		indent(3);out.println("match(\"\\\"\");");				
		indent(3);out.println("String r = \"\";");
		indent(3);out.println("while(pos < input.length() && input.charAt(pos) != \'\\\"\') {");
		indent(4);out.println("if (input.charAt(pos) == '\\\\') {");
		indent(6);out.println("pos=pos+1;");
		indent(6);out.println("switch (input.charAt(pos)) {");
		indent(6);out.println("case 'b' :");
		indent(7);out.println("r = r + '\\b';");
		indent(7);out.println("break;");
		indent(6);out.println("case 't' :");
		indent(7);out.println("r = r + '\\t';");
		indent(7);out.println("break;");
		indent(6);out.println("case 'n' :");
		indent(7);out.println("r = r + '\\n';");
		indent(7);out.println("break;");
		indent(6);out.println("case 'f' :");
		indent(7);out.println("r = r + '\\f';");
		indent(7);out.println("break;");
		indent(6);out.println("case 'r' :");
		indent(7);out.println("r = r + '\\r';");
		indent(7);out.println("break;");
		indent(6);out.println("case '\"' :");
		indent(7);out.println("r = r + '\\\"';");
		indent(7);out.println("break;");
		indent(6);out.println("case '\\\'' :");
		indent(7);out.println("r = r + '\\'';");
		indent(7);out.println("break;");
		indent(6);out.println("case '\\\\' :");
		indent(7);out.println("r = r + '\\\\';");
		indent(7);out.println("break;");
		indent(6);out.println("default :");
		indent(7);out.println("throw new SyntaxError(\"unknown escape character\",pos,pos);");							
		indent(6);out.println("}");		
		indent(5);out.println("} else {");
		indent(6);out.println("r = r + input.charAt(pos);");
		indent(4);out.println("}");
		indent(4);out.println("pos=pos+1;");
		indent(3);out.println("}");
		indent(3);out.println("pos=pos+1;");
		indent(3);out.println("return r;");
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
		} else if(t instanceof Type.Strung) {
			out.print("parseStrung()");
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
	
	protected List<String> concat(List<String> xs, List<String> ys) {
		ArrayList<String> zs = new ArrayList<String>();
		zs.addAll(xs);
		zs.addAll(ys);
		return zs;
	}
	
	protected void indent(int level)  {
		for(int i=0;i!=level;++i) {
			out.print("\t");
		}
	}
	
	protected String indentStr(int level)  {
		String r="";
		for(int i=0;i!=level;++i) {
			r = r + "\t";
		}
		return r;
	}
	
	protected int tmpIndex = 0;
	protected String freshVar() {
		return "tmp" + tmpIndex++;
	}
}
