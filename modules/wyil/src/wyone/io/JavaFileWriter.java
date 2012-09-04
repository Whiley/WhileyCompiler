package wyone.io;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.util.*;

import wyone.util.*;
import wyone.core.*;
import wyone.core.Type;
import static wyone.core.WyoneFile.*;
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

	public void write(WyoneFile spec) {
		write(spec, null);
	}

	public void write(WyoneFile spec, String pkgNam) {
		int lindex = spec.filename.lastIndexOf('.');
		String className = spec.filename.substring(0, lindex);
		int clipSpot = className.indexOf('/');
		if (clipSpot >= 0) {
			if (pkgNam == null) {
				pkgNam = className.substring(0, clipSpot);
			}
			className = className.substring(clipSpot + 1);
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
		HashMap<String, Set<String>> hierarchy = new HashMap<String, Set<String>>();
		HashSet<String> used = new HashSet<String>();
		for (Decl d : spDecl) {
			if (d instanceof ClassDecl) {
				ClassDecl cd = (ClassDecl) d;
				classSet.add(cd.name);
				for (String child : cd.children) {
					Set<String> parents = hierarchy.get(child);
					if (parents == null) {
						parents = new HashSet<String>();
						hierarchy.put(child, parents);
					}
					parents.add(cd.name);
				}
			}
		}

		for (Decl d : spDecl) {
			if (d instanceof TermDecl) {
				write((TermDecl) d, hierarchy);
			} else if (d instanceof ClassDecl) {
				write((ClassDecl) d, hierarchy);
			} else if (d instanceof FunDecl) {
				write((FunDecl) d, used);
			}
		}
		
		writeTypeTests(hierarchy);
		writeSchema();
		writeMainMethod();
		myOut("}");
		out.flush();
	}

	protected void writeImports() {
		myOut("import java.io.*;");
		myOut("import java.util.*;");
		myOut("import java.math.BigInteger;");
		myOut("import wyone.io.PrettyAutomataReader;");
		myOut("import wyone.io.PrettyAutomataWriter;");
		myOut("import wyone.core.*;");
		myOut("import static wyone.util.Runtime.*;");
		myOut();
	}

	public void write(TermDecl decl, HashMap<String, Set<String>> hierarchy) {
		myOut(1, "// term " + decl.type);
		myOut(1, "public final static int K_" + decl.type.name + " = "
				+ termCounter++ + ";");
		if (decl.type.data == null) {
			myOut(1, "public final static Automaton.Term " + decl.type.name
					+ " = new Automaton.Term(K_" + decl.type.name + ");");
		}
		myOut();
	}

	private static int termCounter = 0;

	public void write(ClassDecl decl, HashMap<String, Set<String>> hierarchy) {
		String lin = "// " + decl.name + " as ";
		for (int i = 0; i != decl.children.size(); ++i) {
			String child = decl.children.get(i);
			if (i != 0) {
				lin += " | ";
			}
			lin += child;
		}
		myOut(1, lin);
		myOut();
	}

	public void write(FunDecl decl, HashSet<String> used) {
		myOut(1, "// " + decl.type.ret + " " + decl.name + "(" + decl.type.param + ")");
		myOut(1, "static " + type2JavaType(decl.type.ret) + " " + decl.name + "_"
				+ nameMangle(decl.type.param, used) + "("
				+ type2JavaType(decl.type.param) + " r0, Automaton automaton) {");
		// first, declare variables
		for(int i=1;i<decl.types.size();++i) {			
			Type pt = decl.types.get(i);
			myOut(2,comment(type2JavaType(pt) + " r" + i + ";",pt.toString()));
		}
		// second, translate bytecodes
		myOut(1);
		for(Code code : decl.codes) {
			translate(2,code,decl);
		}
		myOut(1,"}");
		myOut();
	}
	
	public void writeSchema() {
		myOut(1,
				"// =========================================================================");
		myOut(1, "// Schema");
		myOut(1,
				"// =========================================================================");
		myOut();
		myOut(1, "public static final Type.Term[] SCHEMA = new Type.Term[]{");
		for (int i = 0, j = 0; i != spDecl.size(); ++i) {
			Decl d = spDecl.get(i);
			if (d instanceof TermDecl) {
				TermDecl td = (TermDecl) d;
				if (j++ != 0) {
					myOut(",");
				}
				indent(2);
				writeTypeSchema(td.type);				
			}
		}
		myOut();
		myOut(1, "};");		
	}
	
	public void writeTypeSchema(Type t) {
		if(t instanceof Type.Int) {
			out.print("Type.T_INT");
		} else if(t instanceof Type.Real) {
			out.print("Type.T_REAL");
		} else if(t instanceof Type.Strung) {
			out.print("Type.T_STRING");
		} else if(t instanceof Type.Any) {
			out.print("Type.T_ANY");
		} else if(t instanceof Type.Void) {
			out.print("Type.T_VOID");
		} else if(t instanceof Type.Ref) {
			Type.Ref ref = (Type.Ref) t;
			out.print("Type.T_REF(");
			writeTypeSchema(ref.element);
			out.print(")");
		} else if(t instanceof Type.Compound) {		
			Type.Compound compound = (Type.Compound) t;
			out.print("Type.T_COMPOUND(");
			switch(compound.kind) {
				case LIST:
					out.print("Type.Compound.Kind.LIST");
					break;
				case SET:
					out.print("Type.Compound.Kind.SET");
					break;			
			}
			if(compound.unbounded) {
				out.print(",true");
			} else {
				out.print(",false");
			}
			Type[] elements = compound.elements;
			for(int i=0;i!=elements.length;++i) {
				out.print(",");
				writeTypeSchema(elements[i]);
			}
			out.print(")");
		} else {
			Type.Term term = (Type.Term) t;
			out.print("Type.T_TERM(\"" + term.name + "\",");
			if(term.data != null) {
				writeTypeSchema(term.data);
			} else {
				out.print("null");
			}
			out.print(")");
		}
	}

	public void translate(int level, Code code, FunDecl fun) {
		if(code instanceof Code.Assign) {
			translate(level,(Code.Assign) code, fun);
		} else if (code instanceof Code.Constant) {
			translate(level,(Code.Constant) code, fun);
		} else if (code instanceof Code.TermContents) {
			translate(level,(Code.TermContents) code, fun);
		} else if (code instanceof Code.Invoke) {
			translate(level,(Code.Invoke) code, fun);
		} else if (code instanceof Code.If) {
			translate(level,(Code.If) code, fun);
		} else if (code instanceof Code.IfIs) {
			translate(level,(Code.IfIs) code, fun);
		} else if (code instanceof Code.IndexOf) {
			translate(level,(Code.IndexOf) code, fun);
		} else if (code instanceof Code.Deref) {
			translate(level,(Code.Deref) code, fun);
		} else if (code instanceof Code.UnOp) {
			translate(level,(Code.UnOp) code, fun);
		} else if (code instanceof Code.BinOp) {
			translate(level,(Code.BinOp) code, fun);
		} else if (code instanceof Code.NaryOp) {
			translate(level,(Code.NaryOp) code, fun);
		} else if (code instanceof Code.New) {
			translate(level,(Code.New) code, fun);
		} else if (code instanceof Code.Rewrite) {
			translate(level,(Code.Rewrite) code, fun);
		} else if (code instanceof Code.Return) {
			translate(level,(Code.Return) code, fun);
		} else if (code instanceof Code.Constructor) {
			translate(level,(Code.Constructor) code, fun);
		} else {
			throw new RuntimeException("unknown expression encountered - " + code);
		}
	}
	
	public void translate(int level, Code.Assign code, FunDecl fun) {
		// TODO: clone?
		myOut(level,comment("r" + code.target + " = r" + code.operand + ";",code.toString()));
	}

	public void translate(int level, Code.Deref code, FunDecl fun) {
		Type type = fun.types.get(code.target);
		String body = "(" + type2JavaType(type) +  ") automaton.get(r" + code.operand + ")";
		myOut(level,comment("r" + code.target + " = " + body + ";",code.toString()));
	}
	
	public void translate(int level, Code.Invoke code, FunDecl fun) {
		HashSet<String> used = new HashSet<String>();
		String body = code.name + "_" + nameMangle(code.type.param,used) + "(";
		for(int i=0;i!=code.operands.length;++i) {			
			body = body + "r" + code.operands[i];			
			body = body + ", ";
		}
		body = body + "automaton)";
		myOut(level,comment("r" + code.target + " = " + body + ";",code.toString()));
	}
	
	public void translate(int level, Code.IfIs code, FunDecl fun) {
		String mangle = type2HexStr(code.type);
		myOut(level,"if(typeof_" + mangle + "(r" + code.operand + ",automaton)) {");
		for(Code c : code.trueBranch) {
			translate(level+1,c,fun);
		}
		if(code.falseBranch.isEmpty()) {
			myOut(level,"}");
		} else {
			myOut(level,"} else {");
			for(Code c : code.falseBranch) {
				translate(level+1,c,fun);
			}
			myOut(level,"}");
		}
		typeTests.add(code.type);
	}
	
	public void translate(int level, Code.If code, FunDecl fun) {
		myOut(level,"if(r" + code.operand + ") {");
		for(Code c : code.trueBranch) {
			translate(level+1,c,fun);
		}
		if(code.falseBranch.isEmpty()) {
			myOut(level,"}");
		} else {
			myOut(level,"} else {");
			for(Code c : code.falseBranch) {
				translate(level+1,c,fun);
			}
			myOut(level,"}");
		}
	}
	
	public void translate(int level, Code.IndexOf code, FunDecl fun) {
		String body = "r" + code.source + ".get(r" + code.index + ".intValue())";
		myOut(level,comment("r" + code.target + " = " + body + ";",code.toString()));
	}
	
	public void translate(int level, Code.TermContents code, FunDecl fun) {
		String body = "r" + code.operand + ".contents";		
		myOut(level,comment("r" + code.target + " = " + body + ";",code.toString()));
	}
	
	public void translate(int level, Code.Constant code, FunDecl fun) {
		Object v = code.value;
		String rhs;
		
		if (v instanceof Boolean) {
			rhs = v.toString();
		} else if (v instanceof BigInteger) {
			BigInteger bi = (BigInteger) v;
			rhs = "new Automaton.Int(\"" + bi.toString() + "\")";
		} else {
			throw new RuntimeException("unknown constant encountered (" + v
					+ ")");
		}
		myOut(level,comment("r" + code.target + " = " + rhs + ";",code.toString()));
	}

	public void translate(int level, Code.UnOp code, FunDecl fun) {
		String rhs;
		switch (code.op) {
		case LENGTHOF:
			rhs = "BigInteger.valueOf(r" + code.operand + ".length)";
			break;
		case NEG:
			rhs = "r" + code.operand + ".negate()";
			break;
		case NOT:
			rhs = "!r" + code.operand;
			break;
		default:
			throw new RuntimeException("unknown unary expression encountered");
		}
		myOut(level,comment("r" + code.target + " = " + rhs + ";",code.toString()));
	}

	public void translate(int level, Code.BinOp code, FunDecl fun) {
		String rhs;
		
		switch (code.op) {
		case ADD:
			rhs = "r" + code.lhs + ".add(r" + code.rhs + ")";
			break;
		case SUB:
			rhs = "r" + code.lhs + ".subtract(r" + code.rhs + ")";
			break;
		case MUL:
			rhs = "r" + code.lhs + ".multiply(r" + code.rhs + ")";
			break;
		case DIV:
			rhs = "r" + code.lhs + ".divide(r" + code.rhs + ")";
			break;
		case AND:
			rhs = "r" + code.lhs + " && r" + code.rhs ;
			break;
		case OR:
			rhs = "r" + code.lhs + " || r" + code.rhs ;
			break;
		case EQ:
			// FIXME: support lists as well!
			rhs = "r" + code.lhs + " == r" + code.rhs ;
			break;
		case NEQ:
			// FIXME: support lists as well!
			rhs = "r" + code.lhs + " != r" + code.rhs ;
			break;
		case LT:
			rhs = "r" + code.lhs + ".compareTo(r" + code.rhs + ")<0";
			break;
		case LTEQ:
			rhs = "r" + code.lhs + ".compareTo(r" + code.rhs + ")<=0";
			break;
		case GT:
			rhs = "r" + code.lhs + ".compareTo(r" + code.rhs + ")>0";
			break;
		case GTEQ:
			rhs = "r" + code.lhs + ".compareTo(r" + code.rhs + ")>=0";
			break;
		case APPEND:
			rhs = "append(r" + code.lhs + ",r" + code.rhs + ")";
			break;
		default:
			throw new RuntimeException("unknown binary operator encountered: "
					+ code);
		}
		myOut(level,comment("r" + code.target + " = " + rhs + ";",code.toString()));
	}

	public void translate(int level, Code.NaryOp code, FunDecl fun) {
		String body = "new Automaton.List(";
		int[] operands = code.operands;
		for(int i=0;i!=operands.length;++i) {
			if(i != 0) {
				body += ", ";
			}
			body += "r" + operands[i];
		}
		myOut(level,comment("r" + code.target + " = " + body + ");",code.toString()));
	}

	public void translate(int level, Code.New code, FunDecl fun) {
		String body = "automaton.add(r" + code.operand + ")";
		myOut(level,comment("r" + code.target + " = " + body + ";",code.toString()));
	}
	
	public void translate(int level, Code.Constructor code, FunDecl fun) {
		String body;
		if (code.operand == -1) {
			body = code.name;
		} else {
			body = "new Automaton.Term(K_" + code.name + ",r" + code.operand
					+ ")";
		}
		myOut(level, "r" + code.target + " = " + body + ";");
	}
	
	public void translate(int level, Code.Rewrite code, FunDecl fun) {
		int toOperand = code.toOperand;
		Type type = fun.types.get(toOperand);		
		if (!(type instanceof Type.Ref)) {
			// this indicates a new kind of value has been created and we need
			// to allocate this.
			myOut(level, "int v" + toOperand + " = automaton.add(r" + toOperand + ");");
			myOut(level,comment("r" + code.target +" = automaton.rewrite(r" + code.fromOperand + ",v" + toOperand
					+ ");", code.toString()));
		} else {
			myOut(level,comment("r" + code.target +" = automaton.rewrite(r" + code.fromOperand + ",r" + toOperand
				+ ");", code.toString()));
		}
	}
	
	public void translate(int level, Code.Return code, FunDecl fun) {
		// TODO: implement
		myOut(level,comment("return r" + code.operand + ";",code.toString()));
	}

	protected String nameMangle(Type type, HashSet<String> used) {
		String mangle = null;
		String _mangle = type2HexStr(type);
		int i = 0;
		do {
			mangle = _mangle + "_" + i++;
		} while (used.contains(mangle));
		used.add(mangle);
		return mangle;
	}


	protected void writeTypeTests(HashMap<String, Set<String>> hierarchy) {
		myOut(1,
				"// =========================================================================");
		myOut(1, "// Type Tests");
		myOut(1,
				"// =========================================================================");
		myOut();

		HashSet<Type> worklist = new HashSet<Type>(typeTests);
		while (!worklist.isEmpty()) {
			Type t = worklist.iterator().next();
			worklist.remove(t);
			writeTypeTest(t, worklist, hierarchy);
		}
	}

	protected void writeTypeTest(Type type, HashSet<Type> worklist,
			HashMap<String, Set<String>> hierarchy) {
		
		if (type instanceof Type.Any) {
			writeTypeTest((Type.Any)type,worklist,hierarchy);
		} else if (type instanceof Type.Int) {
			writeTypeTest((Type.Int)type,worklist,hierarchy);
		} else if (type instanceof Type.Strung) {
			writeTypeTest((Type.Strung)type,worklist,hierarchy);
		} else if (type instanceof Type.Ref) {
			writeTypeTest((Type.Ref)type,worklist,hierarchy);							
		} else if (type instanceof Type.Term) {
			writeTypeTest((Type.Term)type,worklist,hierarchy);
		} else if (type instanceof Type.Compound) {
			writeTypeTest((Type.Compound)type,worklist,hierarchy);							
		} else {
			throw new RuntimeException(
					"internal failure --- type test not implemented (" + type
							+ ")");
		}		
	}
	
	protected void writeTypeTest(Type.Any type, HashSet<Type> worklist,
			HashMap<String, Set<String>> hierarchy) {
		String mangle = type2HexStr(type);
		myOut(1, "// " + type);
		myOut(1, "private static boolean typeof_" + mangle
				+ "(Automaton.State state, Automaton automaton) {");		
		myOut(2, "return true;");
		myOut(1, "}");
		myOut();
	}
	
	protected void writeTypeTest(Type.Int type, HashSet<Type> worklist,
			HashMap<String, Set<String>> hierarchy) {
		String mangle = type2HexStr(type);
		myOut(1, "// " + type);
		myOut(1, "private static boolean typeof_" + mangle
				+ "(Automaton.State state, Automaton automaton) {");		
		myOut(2, "return state.kind == Automaton.K_INT;");
		myOut(1, "}");
		myOut();
	}
	
	protected void writeTypeTest(Type.Strung type, HashSet<Type> worklist,
			HashMap<String, Set<String>> hierarchy) {
		String mangle = type2HexStr(type);
		myOut(1, "// " + type);
		myOut(1, "private static boolean typeof_" + mangle
				+ "(Automaton.State state, Automaton automaton) {");		
		myOut(2, "return state.kind == Automaton.K_STRING;");
		myOut(1, "}");
		myOut();
	}
	
	protected void writeTypeTest(Type.Ref type, HashSet<Type> worklist,
			HashMap<String, Set<String>> hierarchy) {
		String mangle = type2HexStr(type);
		String elementMangle = type2HexStr(type.element);
		myOut(1, "// " + type);
		myOut(1, "private static boolean typeof_" + mangle
				+ "(int index, Automaton automaton) {");		
		myOut(2, "return typeof_" + elementMangle + "(automaton.get(index),automaton);");
		myOut(1, "}");
		myOut();	
		
		if (typeTests.add(type.element)) {
			worklist.add(type.element);
		}
	}
	
	protected void writeTypeTest(Type.Term type, HashSet<Type> worklist,
			HashMap<String, Set<String>> hierarchy) {
		String mangle = type2HexStr(type);
		myOut(1, "// " + type);
		myOut(1, "private static boolean typeof_" + mangle
				+ "(Automaton.State state, Automaton automaton) {");
		
		HashSet<String> expanded = new HashSet<String>();
		expand(type.name, hierarchy, expanded);
		indent(2);
		out.print("if(state instanceof Automaton.Term && (");
		boolean firstTime = true;
		for (String n : expanded) {			
			myOut();
			indent(2);
			if(!firstTime) {
				out.print("   || state.kind == K_" + n);
			} else {
				firstTime=false;
				out.print(" state.kind == K_" + n);
			}
			
		}
		myOut(")) {");
		// FIXME: there is definitely a bug here since we need the offset within the automaton state
		if (type.data != null) {
			myOut(3,"int data = ((Automaton.Term)state).contents;");
			myOut(3,"if(typeof_" + type2HexStr(type.data) + "(data,automaton)) { return true; }");
			if (typeTests.add(type.data)) {
				worklist.add(type.data);
			}
		} else {
			myOut(3, "return true;");
		}		
		myOut(2, "}");
		myOut(2, "return false;");		
		myOut(1, "}");
		myOut();
	}
	
	protected void writeTypeTest(Type.Compound type, HashSet<Type> worklist,
			HashMap<String, Set<String>> hierarchy) {
		String mangle = type2HexStr(type);
		myOut(1, "// " + type);
		myOut(1, "private static boolean typeof_" + mangle
				+ "(Automaton.State _state, Automaton automaton) {");		
		myOut(2, "if(_state instanceof Automaton.Compound) {");
		myOut(3, "Automaton.Compound state = (Automaton.Compound) _state;");
		myOut(3, "int[] children = state.children;");
		
		Type[] tt_elements = type.elements;
		int min = tt_elements.length;
		if (type.unbounded) {
			myOut(3, "if(children.length < " + (min - 1)
					+ ") { return false; }");
		} else {
			myOut(3, "if(children.length != " + min + ") { return false; }");
		}
		
		int level = 3;
		if(type.kind == Type.Compound.Kind.LIST) {
			// easy, sequential match case
			for (int i = 0; i != tt_elements.length; ++i) {
				myOut(3, "int s" + i + " = " + i + ";");				
			}
		} else {
			for (int i = 0; i != tt_elements.length; ++i) {
				if(!type.unbounded || i+1 < tt_elements.length) {
					String idx = "s" + i;
					myOut(3+i, "for(int " + idx + "=0;" + idx + " < children.length;++" + idx + ") {");
					if(i > 0) {
						indent(3+i);out.print("if(");
						for(int j=0;j<i;++j) {
							if(j != 0) {
								out.print(" || ");
							}
							out.print(idx  + "==s" + j);
						}
						out.println(") { continue; }");
					}
					level++;
				}
			}			
		}
		
		myOut(level, "boolean result=true;");
		myOut(level, "for(int i=0;i!=children.length;++i) {");
		myOut(level+1, "int child = children[i];");
		for (int i = 0; i != tt_elements.length; ++i) {
			Type pt = tt_elements[i];
			String pt_mangle = type2HexStr(pt);
			if (type.unbounded && (i + 1) == tt_elements.length) {
				if(i == 0) {
					myOut(level+1, "{");
				} else {
					myOut(level+1, "else {");
				}
			} else if(i == 0){
				myOut(level+1, "if(i == s" + i + ") {");
			} else {
				myOut(level+1, "else if(i == s" + i + ") {");
			}
			myOut(level+2, "if(!typeof_" + pt_mangle
					+ "(child,automaton)) { result=false; break; }");
			myOut(level+1, "}");
			if (typeTests.add(pt)) {
				worklist.add(pt); 
			}
		}
		
		myOut(level,"}");
		myOut(level,"if(result) { return true; } // found match");
		if(type.kind != Type.Compound.Kind.LIST) {
			for (int i = 0; i != tt_elements.length; ++i) {
				if(!type.unbounded || i+1 < tt_elements.length) {
					myOut(level - (i+1),"}");
				}
			}
		}

		myOut(2, "}");
		myOut(2,"return false;");
		myOut(1, "}");		
		myOut();
	}

	protected void expand(String name, HashMap<String, Set<String>> hierarchy,
			HashSet<String> result) {
		//
		// FIXME: this could be made more efficient by not expanding things
		// which are already expanded!
		//
		ArrayList<String> worklist = new ArrayList<String>();
		worklist.add(name);
		while (!worklist.isEmpty()) {
			String n = worklist.get(0);
			worklist.remove(0);
			boolean matched = false;
			for (Map.Entry<String, Set<String>> e : hierarchy.entrySet()) {
				Set<String> parents = e.getValue();
				if (parents.contains(n)) {
					worklist.add(e.getKey());
					matched = true;
				}
			}
			if (!matched) {
				result.add(n);
			}
		}
	}

	protected void writeMainMethod() {
		myOut(1,
				"// =========================================================================");
		myOut(1, "// Main Method");
		myOut(1,
				"// =========================================================================");
		myOut();
		myOut(1, "public static void main(String[] args) throws IOException {");
		myOut(2, "try {");
		myOut(3,
				"PrettyAutomataReader reader = new PrettyAutomataReader(System.in,SCHEMA);");
		myOut(3,
				"PrettyAutomataWriter writer = new PrettyAutomataWriter(System.out,SCHEMA);");
		myOut(3, "Automaton automaton = reader.read();");
		myOut(3, "System.out.print(\"PARSED: \");");
		myOut(3, "writer.write(automaton);");
		myOut(3, "System.out.println();");
		myOut(3, "boolean changed = true;");
		myOut(3, "while(changed) {");
		myOut(4, "changed = false;");
		myOut(4, "for(int i=0;i<automaton.nStates();++i) {");
		myOut(5, "if(automaton.get(i) != null) {");
		myOut(6, "changed |= rewrite_" + nameMangle(Type.T_REFANY,new HashSet<String>()) + "(i,automaton);");
		myOut(5, "}");
		myOut(4, "}");
		myOut(3, "}");
		myOut(3, "System.out.print(\"REWROTE: \");");
		myOut(3, "writer.write(automaton);");
		myOut(3, "System.out.println();");
		myOut(2, "} catch(PrettyAutomataReader.SyntaxError ex) {");
		myOut(3, "System.err.println(ex.getMessage());");
		myOut(2, "}");
		myOut(1, "}");
	}

	public String comment(String code, String comment) {
		int nspaces = 30 - code.length();
		String r = "";
		for(int i=0;i<nspaces;++i) {
			r += " ";
		}
		return code + r + " // " + comment;
	}
	
	public String type2HexStr(Type t) {
		String mangle = "";
		String str = Type.type2str(t);		
		for (int i = 0; i != str.length(); ++i) {
			char c = str.charAt(i);
			mangle = mangle + Integer.toHexString(c);
		}
		return mangle;
	}
	
	/**
	 * Convert a Wyone type into its equivalent Java type.
	 * 
	 * @param type
	 * @return
	 */
	public String type2JavaType(Type type) {
		if (type instanceof Type.Any) {
			return "Object";
		} else if (type instanceof Type.Int) {
			return "Automaton.Int";
		} else if (type instanceof Type.Bool) {
			// FIXME: not sure what to do here?
			return "boolean";
		} else if (type instanceof Type.Strung) {
			return "Automaton.Strung";
		} else if (type instanceof Type.Term) {
			return "Automaton.Term";
		} else if (type instanceof Type.Ref) {
			return "int";
		} else if (type instanceof Type.Compound) {
			return "Automaton.Compound";
		} 
		throw new RuntimeException("unknown type encountered: " + type);
	}

	public String unboxedType(Type t) {
		if(t instanceof Type.Int) {
			return "BigInteger";
		} else if(t instanceof Type.Strung) {
			return "String";
		} else {
			// TODO: what should I do here?
			return null;
		}
	}
	
	public String unbox(Type t, String src) {
		if(t instanceof Type.Int) {
			return "(BigInteger) ((Item) automaton.get(" + src + ")).payload";
		} else if(t instanceof Type.Strung) {
			return "(String) ((Item) automaton.get(" + src + ")).payload";
		} else {
			// TODO: what should I do here?
			return null;
		}
	}
	
	protected List<String> concat(List<String> xs, List<String> ys) {
		ArrayList<String> zs = new ArrayList<String>();
		zs.addAll(xs);
		zs.addAll(ys);
		return zs;
	}

	protected void myOut() {
		myOut(0, "");
	}

	protected void myOut(int level) {
		myOut(level, "");
	}

	protected void myOut(String line) {
		myOut(0, line);
	}

	protected void myOut(int level, String line) {
		for (int i = 0; i < level; ++i) {
			out.print("\t");
		}
		out.println(line);
	}

	protected void indent(int level) {
		for (int i = 0; i < level; ++i) {
			out.print("\t");
		}
	}

	protected String indentStr(int level) {
		String r = "";
		for (int i = 0; i != level; ++i) {
			r = r + "\t";
		}
		return r;
	}

	protected int tmpIndex = 0;

	protected String freshVar() {
		return "tmp" + tmpIndex++;
	}
}
