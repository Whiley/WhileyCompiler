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
		HashMap<String, List<RewriteDecl>> dispatchTable = new HashMap();
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
			} else if (d instanceof RewriteDecl) {
				write((RewriteDecl) d, used);
			}
		}
		for (Decl d : spDecl) {
			if (d instanceof RewriteDecl) {
				RewriteDecl rd = (RewriteDecl) d;
				List<RewriteDecl> ls = dispatchTable.get(rd.pattern.name);
				if (ls == null) {
					ls = new ArrayList<RewriteDecl>();
					dispatchTable.put(rd.pattern.name, ls);
				}
				ls.add(rd);
			}
		}
		write(dispatchTable);
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
		myOut("import wyautl.io.PrettyAutomataReader;");
		myOut("import wyautl.io.PrettyAutomataReader.DataReader;");
		myOut("import wyautl.io.PrettyAutomataWriter;");
		myOut("import wyautl.lang.*;");
		myOut("import static wyautl.lang.Automata.*;");
		myOut("import static wyone.util.Runtime.*;");
		myOut();
	}

	public void write(TermDecl decl, HashMap<String, Set<String>> hierarchy) {
		myOut(1, "// term " + decl.type);
		myOut(1, "public final static int K_" + decl.type.name + " = "
				+ termCounter++ + ";");
		if (decl.type.data instanceof Type.Void) {
			myOut(1, "public final static Automaton.State " + decl.type.name
					+ " = new Automaton.State(K_" + decl.type.name + ");");
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

	public void write(RewriteDecl decl, HashSet<String> used) {
		// FIRST COMMENT CODE FROM SPEC FILE
		indent(1);
		out.print("// rewrite " + decl.pattern);
		myOut(":");
		String lin;
		for (RuleDecl rd : decl.rules) {
			lin = "// => " + rd.result;
			if (rd.condition != null) {
				;
				lin += ", if " + rd.condition;
			}
			myOut(1, lin);
		}

		// NOW PRINT REAL CODE
		String mangle = nameMangle(decl.pattern, used);
		myOut(1, "public static boolean rewrite" + mangle
				+ "(final int index, final Automaton automaton) {");
		myOut(2, "Automaton.State[] states = automaton.states;");
		myOut(2, "Automaton.State state = states[index];");
		
		write(decl.pattern,"state");
		
		boolean defCase = false;
		int casNo = 1;
		for (RuleDecl rd : decl.rules) {
			for (Pair<String, Expr> p : rd.lets) {
				Pair<List<String>, String> r = translate(p.second());
				write(r.first(), 2);
				indent(2);
				Type t = p.second().attribute(TypeAttr.class).type;
				out.print("final ");
				write(t);
				myOut(" " + p.first() + " = " + r.second() + ";");
			}
			if (rd.condition != null && defCase) {
				// this indicates a syntax error since it means we've got a
				// default case before a conditional case.
				// syntaxError("case cannot be reached",specfile.filename,rd);
				throw new RuntimeException("Unreachable condition in "
						+ decl.pattern.name);
			} else if (rd.condition != null) {
				Pair<List<String>, String> r = translate(rd.condition);
				write(r.first(), 2);
				myOut(2, "if(" + r.second() + ") {");
				r = translate(rd.result);
				write(r.first(), 3);
				myOut(3, "final int idx = " + r.second() + ";");
				myOut(3, "automaton.states[index] = automaton.states[idx];");
				myOut(3, "return true;");
				myOut(2, "}");
			} else {
				defCase = true;
				Pair<List<String>, String> r = translate(rd.result);
				write(r.first(), 2);
				myOut(2, "final int idx = " + r.second() + ";");
				myOut(2, "automaton.states[index] = automaton.states[idx];");
				myOut(2, "return true;");
			}
		}
		if (!defCase) {
			myOut(2, "return false;");
		}
		myOut(1, "}\n");
	}

	/**
	 * Write out the code for destructuring a given pattern. That is, we
	 * traverse from a given input state matching the pattern and extracting and
	 * declaring any variables it defines.
	 * 
	 * @param pattern
	 */
	public void write(Pattern pattern, String root) {
		if (pattern instanceof Pattern.Leaf) {
			// nothing to do
		} else if (pattern instanceof Pattern.Term) {
			Pattern.Term term = (Pattern.Term) pattern;
			write(term.data,root);
		} else if (pattern instanceof Pattern.Compound) {
			Pattern.Compound compound = (Pattern.Compound) pattern;
			int i = 0;
			for (Pair<Pattern, String> p : compound.elements) {
				String name = root + "_" + i;
				// FIXME: problem with unbound compounds.
				myOut(2, "final int " + name + " = " + root + ".children[" + i + "];");
				write(p.first(), name);				
				i = i + 1;
			}
			i = 0;
			for (Pair<Pattern, String> p : compound.elements) {				
				String var = p.second();
				if(var != null) {
					String name = root + "_" + i;
					myOut(2, "final int " + var + " = " + name + ";");								
				}
				i = i + 1;
			}
		}
	}

	public void write(HashMap<String, List<RewriteDecl>> dispatchTable) {
		// First, write individual dispatches
		for (Map.Entry<String, List<RewriteDecl>> e : dispatchTable.entrySet()) {
			write(e.getKey(), e.getValue());
		}

		// Second, write outermost dispatch
		myOut(1,
				"// =========================================================================");
		myOut(1, "// Rewrite Dispatcher");
		myOut(1,
				"// =========================================================================");
		myOut();
		myOut(1, "public static Automaton rewrite(Automaton automaton) {");
		myOut(2, "Automaton old;");
		myOut(2, "automaton = Automata.minimise(automaton);");
		myOut(2, "do {");
		myOut(2, "old = automaton;");
		myOut(2, "boolean changed = false;");
		myOut(3, "for(int index=0;index!=automaton.states.length;++index) {");
		myOut(4, "Automaton.State state = automaton.states[index];");
		myOut(4, "switch(state.kind) {");
		for (Map.Entry<String, List<RewriteDecl>> e : dispatchTable.entrySet()) {
			String name = e.getKey();
			myOut(4, "case K_" + name + ": {");
			myOut(5, "changed |= rewrite_" + name + "(index,automaton);");
			myOut(5, "break;");
			myOut(4, "}");
		}
		myOut(4, "}");
		myOut(3, "}");
		myOut(2, "if(changed) { automaton = Automata.minimise(automaton); }");
		myOut(2, "} while(!automaton.equals(old));");
		myOut(2, "return automaton;");
		myOut(1, "}\n");

	}

	public void write(String name, List<RewriteDecl> rules) {
		myOut(1, "// Rewrite dispatcher for " + name);
		myOut(1, "private static boolean rewrite_" + name
				+ "(int index, Automaton automaton) {");
		myOut(2, "boolean changed = false;\n");

		myOut(2, "// Now rewrite me");
		HashSet<String> used = new HashSet<String>();
		for (RewriteDecl r : rules) {
			Type type = r.pattern.attribute(TypeAttr.class).type;
			String mangle = nameMangle(r.pattern, used);
			indent(2);
			out.print("if(typeof_" + type2HexStr(type) + "(index,automaton)");
			typeTests.add(type);
			myOut(") {");
			myOut(3, "changed |= rewrite" + mangle + "(index,automaton);");
			myOut(2, "}");
		}
		myOut(2, "");
		myOut(2, "// done");
		myOut(2, "return changed;");
		myOut(1, "}\n");
	}

	public void write(List<String> inserts, int level) {
		for (String s : inserts) {
			myOut(level, s);
		}
	}

	public void writeSchema() {
		myOut(1,
				"// =========================================================================");
		myOut(1, "// Schema");
		myOut(1,
				"// =========================================================================");
		myOut();
		myOut(1, "public static final String[] SCHEMA = new String[]{");
		for (int i = 0, j = 0; i != spDecl.size(); ++i) {
			Decl d = spDecl.get(i);
			if (d instanceof TermDecl) {
				TermDecl td = (TermDecl) d;
				if (j++ != 0) {
					myOut(",");
				}
				indent(2);
				out.print("\"" + td.type.name + "\"");
			}
		}
		myOut();
		myOut(1, "};");
		myOut();
		myOut(1,
				"// =========================================================================");
		myOut(1, "// Readers");
		myOut(1,
				"// =========================================================================");
		myOut();
		myOut(1, "public static final DataReader[] READERS = new DataReader[]{");
		for (int i = 0, j = 0; i != spDecl.size(); ++i) {
			Decl d = spDecl.get(i);
			if (d instanceof TermDecl) {
				TermDecl td = (TermDecl) d;
				if (j++ != 0) {
					myOut(",");
				}
				indent(2);
				out.print(readerStr(td.type));
			}
		}
		myOut();
		myOut(1, "};");
		myOut();
	}

	public Pair<List<String>, String> translate(Expr expr) {
		if (expr instanceof Constant) {
			return translate((Constant) expr);
		} else if (expr instanceof Variable) {
			return translate((Variable) expr);
		} else if (expr instanceof UnOp) {
			return translate((UnOp) expr);
		} else if (expr instanceof BinOp) {
			return translate((BinOp) expr);
		} else if (expr instanceof NaryOp) {
			return translate((NaryOp) expr);
		} else if (expr instanceof Comprehension) {
			return translate((Comprehension) expr);
		} else if (expr instanceof Constructor) {
			return translate((Constructor) expr);
		} else if (expr instanceof TermAccess) {
			return translate((TermAccess) expr);
		} else {
			throw new RuntimeException("unknown expression encountered");
		}
	}

	public Pair<List<String>, String> translate(Constant c) {
		return translate(c.value, c);
	}

	public Pair<List<String>, String> translate(Object v, SyntacticElement elem) {
		String r = null;
		List<String> inserts = Collections.EMPTY_LIST;
		if (v instanceof Boolean) {
			r = v.toString();
		} else if (v instanceof BigInteger) {
			BigInteger bi = (BigInteger) v;
			r = "new BigInteger(\"" + bi.toString() + "\")";
		} else if (v instanceof HashSet) {
			HashSet hs = (HashSet) v;
			r = "new HashSet(){{";
			for (Object o : hs) {
				Pair<List<String>, String> is = translate(o, elem);
				inserts = concat(inserts, is.first());
				r = r + "add(" + is.second() + ");";

			}
			r = r + "}}";
		} else {
			throw new RuntimeException("unknown constant encountered (" + v
					+ ")");
		}
		return new Pair(inserts, r);
	}

	public Pair<List<String>, String> translate(Variable v) {
		return new Pair(Collections.EMPTY_LIST, v.var);
	}

	public Pair<List<String>, String> translate(UnOp uop) {
		Pair<List<String>, String> mhs = translate(uop.mhs);
		switch (uop.op) {
		case LENGTHOF:
			return new Pair(mhs.first(), "BigInteger.valueOf(" + mhs.second()
					+ ".length)");
		case NEG:
			return new Pair(mhs.first(), mhs.second() + ".negate()");
		case NOT:
			return new Pair(mhs.first(), "!" + mhs.second());
		default:
			throw new RuntimeException("unknown unary expression encountered");
		}
	}

	public Pair<List<String>, String> translate(BinOp bop) {
		if (bop.op == BOp.TYPEEQ) {
			return translateTypeEquals(bop.lhs,
					bop.rhs.attribute(TypeAttr.class).type);
		}
		Pair<List<String>, String> lhs = translate(bop.lhs);
		Pair<List<String>, String> rhs = translate(bop.rhs);
		List<String> inserts = concat(lhs.first(), rhs.first());
		switch (bop.op) {
		case ADD:
			return new Pair(inserts, lhs.second() + ".add(" + rhs.second()
					+ ")");
		case SUB:
			return new Pair(inserts, lhs.second() + ".subtract(" + rhs.second()
					+ ")");
		case MUL:
			return new Pair(inserts, lhs.second() + ".multiply(" + rhs.second()
					+ ")");
		case DIV:
			return new Pair(inserts, lhs.second() + ".divide(" + rhs.second()
					+ ")");
		case AND:
			return new Pair(inserts, lhs.second() + " && " + rhs.second() + "");
		case OR:
			return new Pair(inserts, lhs.second() + " || " + rhs.second() + "");
		case EQ:
			// FIXME: support lists as well!
			return new Pair(inserts, lhs.second() + " == " + rhs.second());
		case NEQ:
			// FIXME: support lists as well!
			return new Pair(inserts, lhs.second() + " != " + rhs.second());
		case LT:
			return new Pair(inserts, lhs.second() + ".compareTo("
					+ rhs.second() + ")<0");
		case LTEQ:
			return new Pair(inserts, lhs.second() + ".compareTo("
					+ rhs.second() + ")<=0");
		case GT:
			return new Pair(inserts, lhs.second() + ".compareTo("
					+ rhs.second() + ")>0");
		case GTEQ:
			return new Pair(inserts, lhs.second() + ".compareTo("
					+ rhs.second() + ")>=0");
		case APPEND:
			return new Pair(inserts, "append(" + lhs.second() + ","
					+ rhs.second() + ")");
			// case ELEMENTOF:
			// return new Pair(inserts,rhs.second() + ".contains(" +
			// lhs.second() + ")");
			// case UNION:
			// return new Pair(inserts,"new HashSet(){{addAll(" + lhs.second() +
			// ");addAll(" + rhs.second() + ");}}");
			// case DIFFERENCE:
			// return new Pair(inserts,"new HashSet(){{addAll(" + lhs.second() +
			// ");removeAll(" + rhs.second() + ");}}");
			// case INTERSECTION:
			// return new Pair(inserts,"new HashSet(){{for(Object o : " +
			// lhs.second() + "){if(" + rhs.second() +
			// ".contains(o)){add(o);}}}}");
		default:
			throw new RuntimeException("unknown binary operator encountered: "
					+ bop);
		}
	}

	public Pair<List<String>, String> translateTypeEquals(Expr src, Type rhs) {
		Pair<List<String>, String> lhs = translate(src);
		String mangle = type2HexStr(rhs);
		typeTests.add(rhs);
		return new Pair(lhs.first(), "typeof_" + mangle + "(" + lhs.second()
				+ ",automaton)");
	}

	public Pair<List<String>, String> translate(NaryOp nop) {
		List<String> inserts = Collections.EMPTY_LIST;
		String r = null;
		switch (nop.op) {
		case LISTGEN:
			r = "new ArrayList(){{";
			for (Expr e : nop.arguments) {
				Pair<List<String>, String> p = translate(e);
				inserts = concat(inserts, p.first());
				r = r + "add(" + p.second() + ");";
			}
			r = r + "}}";
			break;
		default:
			throw new RuntimeException("sublist encountered: ");
		}

		return new Pair(inserts, r);
	}

	public Pair<List<String>, String> translate(Constructor ivk) {
		String r = "inplaceAppend(automaton,new Automaton.State(K_" + ivk.name;
		List<String> inserts = Collections.EMPTY_LIST;
		if (!ivk.arguments.isEmpty()) {
			boolean firstTime = true;
			Type.Term type = (Type.Term) ivk.attribute(TypeAttr.class).type;
			List<Expr> arguments = ivk.arguments;
			int end = arguments.size();
			if (type.data != Type.T_VOID) {
				end = end - 1;
				r += ", " + arguments.get(end);
			}
			r += ", true, append(";
			for (int i = 0; i != end; ++i) {
				Expr e = arguments.get(i);
				Pair<List<String>, String> es = translate(e);
				inserts = concat(inserts, es.first());
				if (!firstTime) {
					r += ", ";
				} else {
					firstTime = false;
				}
				r += es.second();
			}
			r += ")";
		}

		return new Pair(inserts, r + "))");
	}

	// public Pair<List<String>,String> translateSome(Comprehension c,
	// HashMap<String,Type> environment) {
	// ArrayList<String> inserts = new ArrayList<String>();
	// String tmp = freshVar();
	// inserts.add("boolean " + tmp + " = false;");
	// int l=0;
	// for(Pair<String,Expr> src : c.sources) {
	// Pair<List<String>,String> r = translate(src.second(), environment);
	// Type.Set type = (Type.Set) src.second().attribute(TypeAttr.class).type;
	// for(String i : r.first()) {
	// inserts.add(indentStr(l) + i);
	// }
	// inserts.add(indentStr(l++) + "for(" + typeStr(type.element) + " "
	// + src.first() + " : (HashSet<" + typeStr(type.element)
	// + ">) " + r.second() + ") {");
	// }
	// Pair<List<String>,String> r = translate(c.condition, environment);
	// for(String i : r.first()) {
	// inserts.add(indentStr(l) + i);
	// }
	// inserts.add(indentStr(l) + "if(" + r.second() + ") { " + tmp +
	// " = true; break; }");
	//
	// for(Pair<String,Expr> src : c.sources) {
	// inserts.add(indentStr(--l) + "}");
	// }
	// return new Pair(inserts,tmp);
	// }

	// public Pair<List<String>,String> translateSetComp(Comprehension c,
	// HashMap<String,Type> environment) {
	// ArrayList<String> inserts = new ArrayList<String>();
	// String tmp = freshVar();
	// inserts.add("HashSet " + tmp + " = new HashSet();");
	// int l=0;
	// for(Pair<String,Expr> src : c.sources) {
	// Pair<List<String>,String> r = translate(src.second(), environment);
	// Type.Set type = (Type.Set) src.second().attribute(TypeAttr.class).type;
	// for(String i : r.first()) {
	// inserts.add(indentStr(l) + i);
	// }
	// inserts.add(indentStr(l++) + "for(" + typeStr(type.element) + " "
	// + src.first() + " : (HashSet<" + typeStr(type.element)
	// + ">) " + r.second() + ") {");
	// }
	// Pair<List<String>,String> val = translate(c.value, environment);
	// for(String i : val.first()) {
	// inserts.add(indentStr(l) + i);
	// }
	// if(c.condition != null) {
	// Pair<List<String>,String> r = translate(c.condition, environment);
	// for(String i : r.first()) {
	// inserts.add(indentStr(l) + i);
	// }
	// inserts.add(indentStr(l) + "if(" + r.second() + ") { " + tmp + ".add(" +
	// val.second() + ");}");
	// } else {
	// inserts.add(indentStr(l) + tmp + ".add(" + val.second() + ");");
	// }
	//
	// for(Pair<String,Expr> src : c.sources) {
	// inserts.add(indentStr(--l) + "}");
	// }
	// return new Pair(inserts,tmp);
	// }

	public Pair<List<String>, String> translate(TermAccess ta) {
		Pair<List<String>, String> src = translate(ta.src);
		Type.Term srcType = (Type.Term) ta.src.attribute(TypeAttr.class).type;
		if (ta.index >= 0) {
			return new Pair(src.first(), "automaton.states[" + src.second()
					+ "].children[" + ta.index + "]");
		} else {
			return new Pair(src.first(), "((" + typeStr(srcType.data)
					+ ") automaton.states[" + src.second() + "].data)");
		}
	}

	public void write(Type type) {
		out.print(typeStr(type));
	}

	public String typeStr(Type type) {
		if (type instanceof Type.Term) {
			return "int";
		} else if (type instanceof Type.Int) {
			return "BigInteger";
		} else if (type instanceof Type.Bool) {
			return "boolean";
		} else if (type instanceof Type.Strung) {
			return "String";
		} else if (type instanceof Type.Compound) {
			return "ArrayList";
		}
		throw new RuntimeException("unknown type encountered: " + type);
	}

	public String readerStr(Type type) {
		if (type instanceof Type.Int) {
			return "INT_READER";
		} else if (type instanceof Type.Bool) {
			return "BOOL_READER";
		} else if (type instanceof Type.Strung) {
			return "STRING_READER";
		} else if (type instanceof Type.Compound) {
			return "COMPOUND_READER";
		}
		return "null";
	}

	protected String nameMangle(Pattern pattern, HashSet<String> used) {
		String mangle = null;
		String _mangle = type2HexStr(pattern.attribute(TypeAttr.class).type);
		int i = 0;
		do {
			mangle = _mangle + "_" + i++;
		} while (used.contains(mangle));
		used.add(mangle);
		return mangle;
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
		String mangle = type2HexStr(type);
		myOut(1, "// " + type);
		myOut(1, "private static boolean typeof_" + mangle
				+ "(int index, Automaton automaton) {");
		myOut(2, "Automaton.State state = automaton.states[index];");
		myOut(2, "int[] children = state.children;");

		if (type instanceof Type.Any) {
			myOut(2, "return true;");
		} else if (type instanceof Type.Term) {
			Type.Term tt = (Type.Term) type;
			HashSet<String> expanded = new HashSet<String>();
			expand(tt.name, hierarchy, expanded);
			indent(2);
			out.print("if(");
			boolean firstTime = true;
			for (String n : expanded) {
				if (!firstTime) {
					myOut();
					indent(2);
					out.print("   || state.kind == K_" + n);
				} else {
					firstTime = false;
					out.print("state.kind == K_" + n);
				}
			}
			// FIXME: there is definitely a bug here since we need the offset within the automaton state 
			out.print(" && typeof_" + type2HexStr(tt.data) + "(index,automaton)");
			myOut(") {");
			myOut(3, "return true;");
			myOut(2, "}");
			myOut(2, "return false;");
			if (typeTests.add(tt.data)) {
				worklist.add(tt.data);
			}
		} else if (type instanceof Type.Compound) {
			Type.Compound tt = (Type.Compound) type;
			Type[] tt_elements = tt.elements;
			int min = tt_elements.length;
			if (tt.unbounded) {
				myOut(2, "if(children.length < " + (min - 1)
						+ ") { return false; }");
			} else {
				myOut(2, "if(children.length != " + min + ") { return false; }");
			}
			for (int i = 0; i != tt_elements.length; ++i) {
				Type pt = tt_elements[i];
				String pt_mangle = type2HexStr(pt);
				if (tt.unbounded && (i + 1) == tt_elements.length) {
					myOut(2, "for(int i=" + i + ";i!=children.length;++i) {");
					myOut(3, "if(!typeof_" + pt_mangle
							+ "(children[i],automaton)) { return false; }");
					myOut(2, "}");
				} else {
					myOut(2, "if(!typeof_" + pt_mangle + "(children[" + i
							+ "],automaton)) { return false; }");
				}
				if (typeTests.add(pt)) { worklist.add(pt); }
			}
			myOut(2,"return true;");							
		} else {
			throw new RuntimeException(
					"internal failure --- type test not implemented (" + type
							+ ")");
		}
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
				"PrettyAutomataReader reader = new PrettyAutomataReader(System.in,SCHEMA,READERS);");
		myOut(3,
				"PrettyAutomataWriter writer = new PrettyAutomataWriter(System.out,SCHEMA);");
		myOut(3, "Automaton a = reader.read();");
		myOut(3, "System.out.print(\"PARSED: \");");
		myOut(3, "writer.write(a);");
		myOut(3, "System.out.println();");
		myOut(3, "a = rewrite(a);");
		myOut(3, "System.out.print(\"REWROTE: \");");
		myOut(3, "writer.write(a);");
		myOut(3, "System.out.println();");
		myOut(2, "} catch(PrettyAutomataReader.SyntaxError ex) {");
		myOut(3, "System.err.println(ex.getMessage());");
		myOut(2, "}");
		myOut(1, "}");
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
