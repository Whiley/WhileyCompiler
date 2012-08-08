package wyone.io;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.util.*;

import wyone.util.*;
import wyone.core.*;
import wyone.core.SpecFile.TypeDecl;
import static wyone.core.Expr.*;
import static wyone.core.SpecFile.*;
import static wyone.core.Attribute.*;

public class JavaFileWriter {
	private PrintWriter out;
	private ArrayList<Decl> spDecl;
	private String pkgName = null;
	private HashSet<Type> typeTests = new HashSet<Type>();
	private HashSet<String> classSet = new HashSet<String>();
		
	public JavaFileWriter(Writer os) {
		out = new PrintWriter(os);
	}
	
	public JavaFileWriter(OutputStream os) {
		out = new PrintWriter(os);
	}
	
	public void write(SpecFile spec) {
		write(spec, null);
	}

	public void write(SpecFile spec, String pkgNam) {		
		int lindex = spec.filename.lastIndexOf('.');
		String className = spec.filename.substring(0,lindex);
		int clipSpot = className.indexOf('/');
		if (clipSpot >=0) {
			if (pkgNam == null) {
				pkgNam = className.substring(0,clipSpot);
			}
			className = className.substring(clipSpot+1);
		}
		write(spec.declarations, pkgNam, className);
	}

	public void write(ArrayList<Decl> spDe, String pkgNam, String clsNam) {	
		spDecl = spDe;
		if (pkgNam != null) {
			myOut("package " + pkgNam + ";");
			myOut("");
		}
		pkgName = pkgNam;
		writeImports();		
		myOut("public final class " + clsNam + " {");
		HashMap<String,Set<String>> hierarhcy = new HashMap<String,Set<String>>();
		HashSet<String> used = new HashSet<String>();
		HashMap<String,List<RewriteDecl>> dispatchTable = new HashMap();
		for(Decl d : spDecl) {
			if(d instanceof ClassDecl) {
				ClassDecl cd = (ClassDecl) d;
				classSet.add(cd.name);
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
					
		for (Decl d : spDecl) {
			if(d instanceof TermDecl) {
				write((TermDecl) d, hierarhcy);
			} else if(d instanceof ClassDecl) {
				write((ClassDecl) d, hierarhcy);
			} else if(d instanceof RewriteDecl) {
				write((RewriteDecl) d, used);
			}
		}
		for (Decl d : spDecl) {
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
		writeTypeTests();		
		//writeParser();
		//optStatics();
		//optCheckers();				
		//writeMainMethod();
		myOut("}");
		out.flush();
	}
	
	protected void writeImports()  {		
		myOut("import java.io.*;");
		myOut("import java.util.*;");
		myOut("import wyautl.lang.Automaton;");
		myOut();
	}
	
	public void write(TermDecl decl, HashMap<String,Set<String>> hierarchy) {
		String lin;
		String linN, linV, linT, linQ;
		linN = "";		// line of parameter names
		linV = "";		// line of internal variable names
		linT = "";		// line of typed intenals
		linQ = "";		// line of quoted internals
		linN = "";		//
		if(!decl.params.isEmpty()) {
			boolean firstTime = true;
			int idx = 0;
			for(Type t : decl.params) {
				if(!firstTime) {
					linN += ",";
					linV += ",";
					linT += ",";
					linQ += "+ \",\"";
				}
				firstTime = false;
				String xName = typeStr(t);
				// linN += xName;
				linN += t;
				String iName = "c" + idx;
				linV += iName;
				linT += xName + " " + iName;
				linQ += " + " + iName;
				idx += 1;
			}
		}	
		lin = "// " + decl.name;
		if (!decl.params.isEmpty()) {
			lin += "(" + linN + ")";
		}
		myOut(1, lin);		
		myOut(1, "public final static int K_" + decl.name + " = 0;\n");		
	}
	
	public void write(ClassDecl decl, HashMap<String,Set<String>> hierarchy) {
		String lin = "// " + decl.name + " as ";
		for(int i=0;i!=decl.children.size();++i) {
			String child = decl.children.get(i);
			if(i != 0) {
				lin += " | ";
			}
			lin += child;
		}
		myOut(1,lin);
		myOut(1, "public final static int K_" + decl.name + " = 0;\n");		
	}
	
	public void write(RewriteDecl decl, HashSet<String> used) {
		// FIRST COMMENT CODE FROM SPEC FILE
		indent(1);out.print("// rewrite " + decl.name + "(");
		for(Pair<TypeDecl,String> td : decl.types){
			// why doesn't this loop have a firsttime flag
			// and a separator string
			out.print(td.first().type);
			out.print(" ");
			out.print(td.second());
		}
		myOut("):");
		String lin;
		for(RuleDecl rd : decl.rules) {
			lin = "// => " + rd.result;
			if(rd.condition != null) {
				;
				lin += ", if " + rd.condition;
			}
			myOut(1, lin);
		}
		
		// NOW PRINT REAL CODE				
		String mangle = nameMangle(decl.types, used, decl.name);
		myOut(1,"public static boolean rewrite" + mangle + "(final int index, final Automaton automaton) {");
		myOut(2,"boolean changed = false;");
		HashMap<String,Type> environment = new HashMap<String,Type>();
		for(Pair<TypeDecl,String> td : decl.types){			
			environment.put(td.second(), td.first().type);
		}
		boolean defCase = false;
		int casNo = 1;
//		for(RuleDecl rd : decl.rules) {
//			for(Pair<String,Expr> p : rd.lets) {				
//				Pair<List<String>,String> r = translate(p.second(),environment);
//				write(r.first(),2);
//				indent(2);
//				Type t = p.second().attribute(TypeAttr.class).type;
//				environment.put(p.first(), t);
//				out.print("final ");
//				write(t);
//				myOut(" " + p.first() + " = " + r.second() + ";");								
//			}
//			if(rd.condition != null && defCase) {
//				// this indicates a syntax error since it means we've got a
//				// default case before a conditional case.
//				// syntaxError("case cannot be reached",specfile.filename,rd);
//				throw new RuntimeException("Unreachable condition in " + decl.name);
//			} else if(rd.condition != null) {
//				Pair<List<String>,String> r = translate(rd.condition, environment);
//				write(r.first(),2);
//				myOut(2, "if(" + r.second() + ") {");
//				r = translate(rd.result, environment);
//				write(r.first(),3);
//				myOut(3, "return " + r.second() + ";");				
//				myOut(2, "}");
//			} else {
//				defCase = true;				
//				Pair<List<String>,String> r = translate(rd.result, environment);
//				write(r.first(),2);
//				myOut(2, "return " + r.second() + ";");				
//			}
//		}		
		myOut(2, "return changed;");
		myOut(1, "}\n");
	}
	
	public void write(HashMap<String,List<RewriteDecl>> dispatchTable) {
		// First, write individual dispatches		
		for(Map.Entry<String,List<RewriteDecl>> e : dispatchTable.entrySet()) {
			write(e.getKey(),e.getValue());
		}
		
		// Second, write outermost dispatch
		myOut(1, "// =========================================================================");
		myOut(1, "// Rewrite Dispatchers");
		myOut(1, "// =========================================================================");		
		myOut();
		myOut(1, "public static boolean rewrite(int index, Automaton automaton) {");		
		myOut(2, "boolean changed = false;");
		myOut(2, "boolean lchanged;");
		myOut(2, "do {");
		myOut(3, "lchanged = false;");
		myOut(3, "Automaton.State state = automaton.states[index];");		
		for(Map.Entry<String,List<RewriteDecl>> e : dispatchTable.entrySet()) {
			String name = e.getKey();			
			myOut(3, "if(state.kind == K_" + name + ") {");
			myOut(4, "lchanged |= rewrite_" + name + "(index,automaton);");			
			myOut(3, "}");
		}
		myOut(3, "changed |= lchanged;");
		myOut(2, "} while(lchanged);");
		myOut(2, "return changed;");
		myOut(1, "}\n");
		
	}
	
	public void write(String name, List<RewriteDecl> rules) {
		List<Type> params = null;
		// FIXME: this is a hack
		for (Decl d : spDecl) {
			if(d instanceof TermDecl) {
				TermDecl td = (TermDecl) d;
				if(td.name.equals(name)) {
					params = td.params;
				}
			}
		}
		myOut(1, "// Rewrite dispatcher for " + name);
		myOut(1, "public static boolean rewrite_" + name + "(int index, Automaton automaton) {");
		myOut(2, "boolean changed = false;\n");
		myOut(2, "Automaton.State state = automaton.states[index];");
		myOut(2, "int[] children = state.children;\n");
		myOut(2, "// Recursively rewrite children");		
		myOut(2,"for(int i=0;i!=children.length;++i) {");
		myOut(3,"changed |= rewrite(children[i],automaton);");
		myOut(2,"}\n");
		
		myOut(2, "// Now rewrite me");
		HashSet<String> used = new HashSet<String>();
		for(RewriteDecl r : rules) {
			String mangle = nameMangle(r.types, used, r.name);
			indent(2);out.print("if(");
			int idx=0;
			boolean firstTime=true;
			for(Pair<TypeDecl,String> t : r.types) {
				if(!firstTime) {
					out.print(" && ");
				}
				firstTime=false;				
				typeTests.add(t.first().type);
				out.print("typeof_" + type2HexStr(t.first().type) + "(children[" + idx++ + "],automaton)");							
			}
			myOut(") {");
			myOut(3,"changed |= rewrite" + mangle + "(index,automaton);");								
			myOut(2, "}");
		}
		myOut(2, "");
		myOut(2, "// done");
		myOut(2, "return changed;");
		myOut(1, "}\n");
	}
	
	public void write(List<String> inserts, int level) {
		for(String s : inserts) {
			myOut(level, s);
		}
	}
	
	public Pair<List<String>,String> translate(Expr expr, HashMap<String,Type> environment) {
		if(expr instanceof Constant) {
			return translate((Constant)expr);			
		} else if(expr instanceof Variable) {
			return translate((Variable)expr, environment);
		} else if(expr instanceof UnOp) {
			return translate((UnOp)expr, environment);
		} else if(expr instanceof BinOp) {
			return translate((BinOp)expr, environment);
		} else if(expr instanceof NaryOp) {
			return translate((NaryOp)expr, environment);
		} else if(expr instanceof Comprehension) {
			return translate((Comprehension)expr, environment);
		} else if(expr instanceof Invoke) {
			return translate((Invoke)expr, environment);
		} else if(expr instanceof TermAccess) {
			return translate((TermAccess)expr, environment);
		} else {		
			throw new RuntimeException("unknown expression encountered");
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
			throw new RuntimeException("unknown constant encountered (" + v + ")");
		}
		return new Pair(inserts,r);
	}
	
	public Pair<List<String>,String> translate(Variable v, HashMap<String,Type> environment) {
		Type actual = v.attribute(TypeAttr.class).type;
		Type declared = environment.get(v.var);
		if(actual != declared) {
			// the need to insert this case arises from the possibility of type
			// inference resulting in the type of a variable being updated.
			return new Pair(Collections.EMPTY_LIST,"((" + typeStr(actual) + ") " + v.var + ")");
		} else {
			return new Pair(Collections.EMPTY_LIST,v.var);
		}
	}
	
	public Pair<List<String>,String> translate(UnOp uop, HashMap<String,Type> environment) {
		Pair<List<String>,String> mhs = translate(uop.mhs, environment);
		switch(uop.op) {
		case NEG:
			return new Pair(mhs.first(), mhs.second()+ ".negate()");
		case NOT:
			return new Pair(mhs.first(),"!" + mhs.second());
		default:
			throw new RuntimeException("unknown unary expression encountered");
		}
	}
	
	public Pair<List<String>,String> translate(BinOp bop, HashMap<String,Type> environment) {
		if(bop.op == BOp.TYPEEQ) {			
			return translateTypeEquals(bop.lhs,bop.rhs.attribute(TypeAttr.class).type, environment);
		}
		Pair<List<String>,String> lhs = translate(bop.lhs, environment);
		Pair<List<String>,String> rhs = translate(bop.rhs, environment);
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
		case AND:
			return new Pair(inserts,lhs.second() + " && " + rhs.second() + "");
		case OR:
			return new Pair(inserts,lhs.second() + " || " + rhs.second() + "");
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
		case UNION:
			return new Pair(inserts,"new HashSet(){{addAll(" + lhs.second() + ");addAll(" + rhs.second() + ");}}");
		case DIFFERENCE:
			return new Pair(inserts,"new HashSet(){{addAll(" + lhs.second() + ");removeAll(" + rhs.second() + ");}}");			
		case INTERSECTION:
			return new Pair(inserts,"new HashSet(){{for(Object o : " + lhs.second() + "){if(" + rhs.second() + ".contains(o)){add(o);}}}}");		
		default:
			throw new RuntimeException("unknown binary operator encountered: " + bop);
		}		
	}
	
	public Pair<List<String>,String> translateTypeEquals(Expr src, Type rhs, HashMap<String,Type> environment) {
		Pair<List<String>,String> lhs = translate(src, environment);
		String mangle = type2HexStr(rhs);
		typeTests.add(rhs);
		return new Pair(lhs.first(),"typeof_" + mangle + "(" + lhs.second() +")");		
	}
	
	public Pair<List<String>,String> translate(NaryOp nop, HashMap<String,Type> environment) {				
		List<String> inserts = Collections.EMPTY_LIST;
		String r = null;
		switch(nop.op) {
		case SETGEN:
			r="new HashSet(){{";
			for(Expr e : nop.arguments) {
				Pair<List<String>,String> p = translate(e, environment);
				inserts = concat(inserts,p.first());
				r = r + "add(" + p.second() + ");";								
			}
			r = r + "}}";
			break;
		case LISTGEN:
			r="new ArrayList(){{";
			for(Expr e : nop.arguments) {
				Pair<List<String>,String> p = translate(e, environment);
				inserts = concat(inserts,p.first());
				r = r + "add(" + p.second() + ");";								
			}
			r = r + "}}";			
			break;
		default:
			throw new RuntimeException("sublist encountered: ");
		} 
		
		return new Pair(inserts,r);
	}
	
	public  Pair<List<String>,String> translate(Invoke ivk, HashMap<String,Type> environment) {
		List<String> inserts = Collections.EMPTY_LIST;
		String r = ivk.name + "(";
		boolean firstTime=true;
		for(Expr e : ivk.arguments) {
			Pair<List<String>,String> es = translate(e, environment);
			inserts = concat(inserts,es.first());
			if(!firstTime) {
				r += ", ";
			}
			firstTime=false;
			r += es.second();
		}
		
		return new Pair(inserts,r + ")");
	}
	
	public  Pair<List<String>,String> translate(Comprehension c, HashMap<String,Type> environment) {
		if(c.cop == COp.SOME) {
			return translateSome(c, environment);
		} else if(c.cop == COp.SETCOMP){
			return translateSetComp(c, environment);
		} else {
			return null;
		}
	}
	
	public Pair<List<String>,String> translateSome(Comprehension c, HashMap<String,Type> environment) {		
		ArrayList<String> inserts = new ArrayList<String>();		
		String tmp = freshVar();
		inserts.add("boolean " + tmp + " = false;");
		int l=0;
		for(Pair<String,Expr> src : c.sources) {
			Pair<List<String>,String> r = translate(src.second(), environment);
			Type.Set type = (Type.Set) src.second().attribute(TypeAttr.class).type;
			for(String i : r.first()) {
				inserts.add(indentStr(l) + i);
			}			
			inserts.add(indentStr(l++) + "for(" + typeStr(type.element) + " "
					+ src.first() + " : (HashSet<" + typeStr(type.element)
					+ ">) " + r.second() + ") {");			
		}
		Pair<List<String>,String> r = translate(c.condition, environment);
		for(String i : r.first()) {
			inserts.add(indentStr(l) + i);
		}
		inserts.add(indentStr(l) + "if(" + r.second() + ") { " + tmp + " = true; break; }");
		
		for(Pair<String,Expr> src : c.sources) {
			inserts.add(indentStr(--l) + "}");
		}
		return new Pair(inserts,tmp);
	}
	
	public Pair<List<String>,String> translateSetComp(Comprehension c, HashMap<String,Type> environment) {
		ArrayList<String> inserts = new ArrayList<String>();		
		String tmp = freshVar();
		inserts.add("HashSet " + tmp + " = new HashSet();");
		int l=0;
		for(Pair<String,Expr> src : c.sources) {
			Pair<List<String>,String> r = translate(src.second(), environment);
			Type.Set type = (Type.Set) src.second().attribute(TypeAttr.class).type;
			for(String i : r.first()) {
				inserts.add(indentStr(l) + i);
			}			
			inserts.add(indentStr(l++) + "for(" + typeStr(type.element) + " "
					+ src.first() + " : (HashSet<" + typeStr(type.element)
					+ ">) " + r.second() + ") {");			
		}
		Pair<List<String>,String> val = translate(c.value, environment);
		for(String i : val.first()) {
			inserts.add(indentStr(l) + i);
		}
		if(c.condition != null) {
			Pair<List<String>,String> r = translate(c.condition, environment);
			for(String i : r.first()) {
				inserts.add(indentStr(l) + i);
			}
			inserts.add(indentStr(l) + "if(" + r.second() + ") { " + tmp + ".add(" + val.second() + ");}");
		} else {
			inserts.add(indentStr(l) + tmp + ".add(" + val.second() + ");");
		}
		
		for(Pair<String,Expr> src : c.sources) {
			inserts.add(indentStr(--l) + "}");
		}
		return new Pair(inserts,tmp);
	}
	
	public Pair<List<String>,String> translate(TermAccess ta, HashMap<String,Type> environment) {
		Pair<List<String>,String> src = translate(ta.src, environment);
		return new Pair(src.first(),src.second() + ".c" + ta.index);
	}
	
	public void write(Type type) {
		out.print(typeStr(type));
	}
	
	public String typeStr(Type type) {
		if(type instanceof Type.Any) {
			return "Object";
		} else if(type instanceof Type.Int) {
			return "BigInteger";
		} else if(type instanceof Type.Bool) {
			return "boolean";
		} else if(type instanceof Type.Strung) {
			return "String";
		} else if(type instanceof Type.Term) {
			return ((Type.Term)type).name;
		} else if(type instanceof Type.List){
			return "ArrayList";
		} else if(type instanceof Type.Set){
			return "HashSet";
		} 
		throw new RuntimeException("unknown type encountered: " + type);
	}
	
	protected String nameMangle(Collection<Pair<TypeDecl,String>> types, HashSet<String> used, String name) {
		String mangle = null;
		String _mangle = nameMangle(types);
		int i=0;
		do {			
			mangle = _mangle + "_" + i++;
		} while(used.contains(name + mangle));
		used.add(name + mangle);
		return mangle;
	}
	protected String nameMangle(Collection<Pair<TypeDecl,String>> types) {
		String mangle = "_";
		for(Pair<TypeDecl,String> td : types) {	
			mangle = mangle + type2HexStr(td.first().type);			
		}		
		return mangle;
	}
	
	public String type2HexStr(Type t) {
		String mangle = "";
		String str = Type.type2str(t);
		for(int i=0;i!=str.length();++i) {
			char c = str.charAt(i);
			mangle = mangle + Integer.toHexString(c);
		}
		return mangle;
	}	
	
	protected void writeTypeTests() {
		myOut(1, "// =========================================================================");
		myOut(1, "// Type Tests");
		myOut(1, "// =========================================================================");		
		myOut();
		
		HashSet<Type> worklist = new HashSet<Type>(typeTests);
		while(!worklist.isEmpty()) {			
			Type t = worklist.iterator().next();
			worklist.remove(t);
			writeTypeTest(t,worklist);
		}
	}
	
	protected void writeTypeTest(Type type, HashSet<Type> worklist) {
		String mangle = type2HexStr(type);
		myOut(1, "// " + type);
		myOut(1, "protected static boolean typeof_" + mangle + "(int index, Automaton automaton) {");
		myOut(2, "Automaton.State state = automaton.states[index];");
		myOut(3, "int[] children = state.children;");
		
		if(type instanceof Type.Any) {
			myOut(2, "return true;");
		} else if(type instanceof Type.Term) {
			Type.Term tt = (Type.Term) type;
			myOut(2, "if(state.kind == K_" + tt.name + ") {");
			for(int i=0;i!=tt.params.size();++i) {			
				Type pt = tt.params.get(i);
				String pt_mangle = type2HexStr(pt);
				myOut(3, "if(!typeof_" + pt_mangle + "(children[" + i +"],automaton)) { return false; }");								
				if(typeTests.add(pt)) {				
					worklist.add(pt);
				}
			}
			myOut(3, "return true;");
			myOut(2, "}");
			myOut(2, "return false;");											
		} else if(type instanceof Type.List){
			Type.List tl = (Type.List) type;
			mangle = type2HexStr(tl.element());
			myOut(2, "if(value instanceof ArrayList) {");
			myOut(3, "ArrayList ls = (ArrayList) value;");
			myOut(3, "for(Object o : ls) {");
			myOut(4, "if(!typeof_" + mangle + "(o)) { return false; }");
			myOut(3, "}");
			myOut(3, "return true;");
			myOut(2, "}");
			myOut(2, "return false;");		
			if(typeTests.add(tl.element)) {				
				worklist.add(tl.element);
			}
		} else if(type instanceof Type.Set){
			Type.Set tl = (Type.Set) type;
			mangle = type2HexStr(tl.element());
			myOut(2, "if(value instanceof HashSet) {");
			myOut(3, "HashSet ls = (HashSet) value;");
			myOut(3, "for(Object o : ls) {");
			myOut(4, "if(!typeof_" + mangle + "(o)) { return false; }");
			myOut(3, "}");
			myOut(3, "return true;");
			myOut(2, "}");
			myOut(2, "return false;");
			if(typeTests.add(tl.element)) {				
				worklist.add(tl.element);
			}
		} else {
			throw new RuntimeException("internal failure --- type test not implemented (" + type + ")");
		}		
		myOut(1, "}");
	}
	
	protected void writeMainMethod() {
		myOut(1, "private static void getIn(String fName, StringBuffer place)");
		myOut(1, "    throws IOException {");
		myOut(2, "BufferedReader in = new BufferedReader(new FileReader(fName));");
		myOut(2, "while (in.ready()) {");
		myOut(3, "place.append(in.readLine());");
		myOut(3, "place.append(\"\\n\");");
		myOut(2, "}");
		myOut(1, "}");
		myOut();
		myOut(1, "private static void getStdIn(StringBuffer place)");
		myOut(1, "    throws IOException {");
		myOut(2, "BufferedReader in = new BufferedReader(new InputStreamReader(System.in));");
		myOut(2, "while (in.ready()) {");
		myOut(3, "place.append(in.readLine());");
		myOut(3, "place.append(\"\\n\");");
		myOut(2, "}");
		myOut(1, "}");
		myOut();
		myOut(1, "private static boolean logCheck(String tags) {");
		myOut(2, "if (tags.length() <= 0){");
		myOut(3, "return true;");
		myOut(2, "}");
		myOut(2, "String[] tLst = tags.split(\":\");");
		myOut(2, "for (String itm : tLst) {");
		myOut(3, "if (logTypes.containsKey(itm)){");
		myOut(4, "return true;");
		myOut(3, "}");
		myOut(2, "}");
		myOut(2, "return false;");
		myOut(1, "}");
		myOut();
		myOut(1, "public static void main(String[] args) throws IOException {");
		myOut(2, "StringBuffer text = new StringBuffer();");
		myOut(2, "LinkedList<String> waitParm = new LinkedList<String>();");
		myOut(2, "for (int i = 0; i < args.length; ++i) {");
		myOut(3, "String arg = args[i];");
		myOut(3, "if (optionTags.containsKey(arg)) {");
		myOut(4, "if (optionTags.get(arg) != \"false\") {");
		myOut(5, "waitParm.add(arg);");
		myOut(4, "} else {");
		myOut(5, "optionStuff.put(arg, \"true\");");
		myOut(4, "}");
		myOut(4, "continue;");
		myOut(3, "}");
		myOut(3, "if (arg.startsWith(\"-\")) {");
		myOut(4, "throw new RuntimeException(\"Unknown option: \" + arg);");
		myOut(3, "}");

		myOut(3, "if (waitParm.size() > 0) {");
		myOut(4, "optionStuff.put(waitParm.remove(), arg);");
		myOut(3, "} else {");
		myOut(4, "getIn(arg, text);");
		myOut(3, "}");
		myOut(2, "}");
		myOut(2, "if (optBool(\"-stdin\")){");
		myOut(3, "getStdIn(text);");
		myOut(2, "}");
		myOut(2, "String tmp;");
		myOut(2, "tmp = optString(\"-logtypes\");");
		myOut(2, "if (tmp.length() > 0){");
		myOut(3, "optionStuff.put(\"-debug\", \"true\");");
		myOut(3, "String[] tmpLst = tmp.split(\":\");");
		myOut(3, "for (String tag : tmpLst) {");
		myOut(4, "logTypes.put(tag, \"true\");");
		myOut(3, "}");
		myOut(2, "}");
		myOut(2, "tmp = optString(\"-logfile\");");
		myOut(2, "if (tmp.length() > 0){");
		myOut(3, "logOut = new PrintStream(tmp);");
		myOut(2, "} else {");
		myOut(3, "logOut = System.err;");
		myOut(2, "}");		
		myOut(2, "try {");
		myOut(3, "Parser parser = new Parser(text.toString());");
		myOut(3, "Constructor c = parser.parseTop();");
		myOut(3, "System.out.println(\"PARSED: \" + c);");
		myOut(3, "System.out.println(\"REWROTE: \" + rewrite(c));");
		myOut(2, "} catch(SyntaxError ex) {");
		myOut(3, "System.err.println(ex.getMessage());");
		myOut(2, "}");
		myOut(1, "}");
	}
	
	protected void writeParser() {
		myOut(1, "public static final class Parser {");
		writeParserStatics();
		writeParserConstructor();
		writeParseTop();
		writeParseTerm();
		writeParseSet();
		writeParseNumber();		
		writeParseStrung();
		writeParseIdentifier();
		writeMatch();
		writeSkipWhiteSpace();
		myOut(1, "}");
		writeSyntaxError();
	}
	
	protected void writeParserStatics() {
		myOut(2, "private final String input;");
		myOut(2, "private int pos;");
	}

	protected void writeParserConstructor() {
		myOut(2, "public Parser(String input) {");
		myOut(3, "this.input = input;");
		myOut(3, "this.pos = 0;");
		myOut(2, "}");
	}
	
	
	
	protected void writeParseTop() {
		myOut(2, "protected Constructor parseTop() {");
		myOut(3, "return parseTerm();");
		myOut(2, "}");
		myOut();				
	}
	
	
	protected void writeParseStatics() {
		myOut(2, "private final String input;");
		myOut(2, "private int pos;");
	}
		
	protected void writeParseSet() {
		myOut(2, "protected HashSet parseSet() {");
		myOut(3, "HashSet r = new HashSet();");
		myOut(3, "match(\"{\");");
		myOut(3, "skipWhiteSpace();");
		myOut(3, "boolean firstTime=true;");
		myOut(3, "while(pos < input.length() && input.charAt(pos) != '}') {");
		myOut(4, "if(!firstTime) {");
		myOut(5, "match(\",\");");
		myOut(4, "skipWhiteSpace();");
		myOut(4, "}");
		myOut(4, "firstTime=false;");
		myOut(4, "r.add(parseTerm());");
		myOut(3, "skipWhiteSpace();");
		myOut(3, "}");
		myOut(3, "match(\"}\");");
		myOut(3, "return r;");
		myOut(2, "}");
	}
	
	protected void writeParseNumber() {
		myOut(2, "protected BigInteger parseNumber() {");		
		myOut(3, "int start = pos;");
		myOut(3, "while (pos < input.length() && Character.isDigit(input.charAt(pos))) {");
		myOut(4, "pos = pos + 1;");
		myOut(3, "}");		
		myOut(3, "return new BigInteger(input.substring(start, pos));");								
		myOut(2, "}");		
	}
	
	protected void writeParseStrung() {
		myOut(2, "protected String parseStrung() {");
		myOut(3, "match(\"\\\"\");");
		myOut(3, "String r = \"\";");
		myOut(3, "while(pos < input.length() && input.charAt(pos) != \'\\\"\') {");
		myOut(4, "if (input.charAt(pos) == '\\\\') {");
		myOut(6, "pos=pos+1;");
		myOut(6, "switch (input.charAt(pos)) {");
		myOut(6, "case 'b' :");
		myOut(7, "r = r + '\\b';");
		myOut(7, "break;");
		myOut(6, "case 't' :");
		myOut(7, "r = r + '\\t';");
		myOut(7, "break;");
		myOut(6, "case 'n' :");
		myOut(7, "r = r + '\\n';");
		myOut(7, "break;");
		myOut(6, "case 'f' :");
		myOut(7, "r = r + '\\f';");
		myOut(7, "break;");
		myOut(6, "case 'r' :");
		myOut(7, "r = r + '\\r';");
		myOut(7, "break;");
		myOut(6, "case '\"' :");
		myOut(7, "r = r + '\\\"';");
		myOut(7, "break;");
		myOut(6, "case '\\\'' :");
		myOut(7, "r = r + '\\'';");
		myOut(7, "break;");
		myOut(6, "case '\\\\' :");
		myOut(7, "r = r + '\\\\';");
		myOut(7, "break;");
		myOut(6, "default :");
		myOut(7, "throw new SyntaxError(\"unknown escape character\",pos,pos);");							
		myOut(6, "}");
		myOut(5, "} else {");
		myOut(6, "r = r + input.charAt(pos);");
		myOut(4, "}");
		myOut(4, "pos=pos+1;");
		myOut(3, "}");
		myOut(3, "pos=pos+1;");
		myOut(3, "return r;");
		myOut(2, "}");
	}
	
	protected void writeParseTerm() {
		;
		writeParseTerm(spDecl);
	}
	
	protected void writeParseTerm(ArrayList<Decl> declLst) {
		myOut(2, "protected Constructor parseTerm() {");
		myOut(3, "skipWhiteSpace();");
		myOut(3, "int start = pos;");
		myOut(3, "String name = parseIdentifier();");
		for(Decl d : declLst) {
			if(d instanceof TermDecl) {
				TermDecl td = (TermDecl) d;
				myOut(3, "if(name.equals(\"" + td.name + "\")) { return parse" + td.name + "(); }");
			}
		}
		myOut(3, "throw new SyntaxError(\"unrecognised term: \" + name,start,pos);");
		myOut(2, "}");
		for(Decl d : declLst) {
			if(d instanceof TermDecl) {				
				writeParseTerm((TermDecl) d);
			}
		}
	}
	
	protected void writeParseTerm(TermDecl term) {
		myOut(2, "public " + term.name +" parse" + term.name + "() {");
		if(term.params.isEmpty()) {
			myOut(3, "return " + term.name + ";");
		} else {
			myOut(3, "match(\"(\");");
			int idx=0;
			boolean firstTime=true;
			for(Type t : term.params) {
				if(!firstTime) {
					myOut(3, "match(\",\");");
				}
				firstTime=false;
				indent(3);write(t);out.print(" e" + idx++ + " = ");
				writeTypeDispatch(t);
				myOut(";");				
			}
			myOut(3, "match(\")\");");
			indent(3);out.print("return " + term.name + "(");		
			for(int i=0;i!=term.params.size();i++) {
				if(i != 0) {
					out.print(", ");
				}
				out.print("e" + i);
			}
			myOut(");");
		}	
		myOut(2, "}");
	}
	
	public void writeTypeDispatch(Type t) {
		if(t instanceof Type.Set) {
			out.print("parseSet()");
		} else if(t instanceof Type.Int) {
			out.print("parseNumber()");
		} else if(t instanceof Type.Strung) {
			out.print("parseStrung()");
		} else 	{	
			Type.Term tn = (Type.Term) t;
			out.print("(" + tn.name + ") parseTerm()");
		}
	}
	
	public void writeParseIdentifier() {
		myOut(2, "protected String parseIdentifier() {");
		myOut(3, "int start = pos;");		
		myOut(3, "while (pos < input.length() &&");
		myOut(4, "Character.isJavaIdentifierPart(input.charAt(pos))) {");
		myOut(4, "pos++;");								
		myOut(3, "}");		
		myOut(3, "return input.substring(start,pos);");
		myOut(2, "}");
	}	
	protected void writeMatch() {
		myOut(2, "protected void match(String x) {");
		myOut(3, "skipWhiteSpace();");
		myOut(3, "if((input.length()-pos) < x.length()) {");
		myOut(4, "throw new SyntaxError(\"expecting \" + x,pos,pos);");
		myOut(3, "}");
		myOut(3, "if(!input.substring(pos,pos+x.length()).equals(x)) {");
		myOut(4, "throw new SyntaxError(\"expecting \" + x,pos,pos);");
		myOut(3, "}");
		myOut(3, "pos += x.length();");
		myOut(2, "}");
	}
	
	protected void writeSkipWhiteSpace() {
		myOut(2, "protected void skipWhiteSpace() {");
		myOut(3, "while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {");			
		myOut(4, "pos = pos + 1;");
		myOut(3, "}");
		myOut(2, "}");		
	}
	
	protected void writeSyntaxError() {
		myOut(1, "public static final class SyntaxError extends RuntimeException {");				
		myOut(2, "public final int start;");
		myOut(2, "public final int end;");		
			
		myOut(2, "public SyntaxError(String msg, int start, int end) {");
		myOut(3, "super(msg);");		
		myOut(3, "this.start=start;");		
		myOut(3, "this.end=end;");		
		myOut(2, "}");
		myOut(1, "}");
	}

	// no assumption of .*
	// initial - to mark flag, initial + to mark parameter
	private static String[] parserOpts = {
		"-verbose",
		"-debug",
		"-stdin",
		"+logfile",
		"+logtypes"
	};	
	
	protected void optCheckers()  {
		myOut();
		myOut(1, "private static boolean optBool(String nam) {");
		myOut(2, "if (! optionStuff.containsKey(nam)) {");
		myOut(3, "return false;");
		myOut(2, "}");
		myOut(2, "String val = optionStuff.get(nam);");
		myOut(2, "if ((val != null) && (val != \"\")) {");
		myOut(3, "return true;");
		myOut(2, "}");
		myOut(2, "return false;");
		myOut(1, "}");
		myOut(1, "private static String optString(String nam) {");
		myOut(2, "if (! optionStuff.containsKey(nam)) {");
		myOut(3, "return null;");
		myOut(2, "}");
		myOut(2, "return optionStuff.get(nam);");
		myOut(1, "}");
	}
	protected void optStatics()  {
		myOut(1, "private static Map<String, String> optionTags;");
		myOut(1, "private static Map<String, String> optionStuff;");
		myOut(1, "private static Map<String, String> logTypes;");
		myOut(1, "private static PrintStream logOut;");
		myOut(1, "static {");
		myOut(2, "optionTags = new HashMap<String, String>();");
		myOut(2, "optionStuff = new HashMap<String, String>();");
		myOut(2, "logTypes = new HashMap<String, String>();");
		for (int i=0; i < parserOpts.length; i++) {
			String tag = parserOpts[i];
			String name = tag.substring(1);
			String val = "true";
			if (tag.startsWith("-")) {
				val = "false";
			}
			myOut(2, "optionTags.put(\"-" + name + "\", \"" + val + "\");");
			myOut(2, "optionStuff.put(\"-" + name + "\", \"\");");
		}
		myOut(1, "}");
	}
	protected List<String> concat(List<String> xs, List<String> ys) {
		ArrayList<String> zs = new ArrayList<String>();
		zs.addAll(xs);
		zs.addAll(ys);
		return zs;
	}
	
	protected void myOut()  {
		myOut(0, "");
	}
	protected void myOut(int level)  {
		myOut(level, "");
	}
	protected void myOut(String line)  {
		myOut(0, line);
	}
	protected void myOut(int level, String line)  {
		for(int i=0;i<level;++i) {
			out.print("\t");
		}
		out.println(line);
	}
	
	protected void indent(int level)  {
		for(int i=0;i<level;++i) {
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
