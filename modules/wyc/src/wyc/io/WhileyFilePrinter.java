package wyc.io;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import wyc.lang.Expr;
import wyc.lang.Stmt;
import wyc.lang.SyntacticType;
import wyc.lang.TypePattern;
import wyc.lang.WhileyFile;
import wycc.util.Pair;
import wyil.lang.*;

/**
 * Responsible for "pretty printing" a Whiley File. This is useful for
 * formatting Whiley Files. Also, it can be used to programatically generate
 * Whiley Files.
 *
 * @author David J. Pearce
 *
 */
public class WhileyFilePrinter {
	private PrintStream out;


	public WhileyFilePrinter(OutputStream stream) {
		try {
			this.out = new PrintStream(stream, true, "UTF-8");
		} catch(Exception e) {
			this.out = new PrintStream(stream);
		}
	}

	public void print(WhileyFile wf) {
		for(WhileyFile.Declaration d : wf.declarations) {
			print(d);
		}
		out.flush();
	}

	public void print(WhileyFile.Declaration decl) {
		if(decl instanceof WhileyFile.Import) {
			print((WhileyFile.Import)decl);
		} else if(decl instanceof WhileyFile.Constant) {
			print((WhileyFile.Constant)decl);
		} else if(decl instanceof WhileyFile.Type) {
			print((WhileyFile.Type)decl);
		} else if(decl instanceof WhileyFile.FunctionOrMethod) {
			print((WhileyFile.FunctionOrMethod)decl);
		} else {
			throw new RuntimeException("Unknown construct encountered: "
					+ decl.getClass().getName());
		}
	}

	public void print(WhileyFile.FunctionOrMethod fm) {
		out.println();
		print(fm.modifiers());

		int paramStart = 0;

		if(fm instanceof WhileyFile.Method) {
			out.print("method ");
		} else {
			out.print("function ");
		}

		out.print(fm.name());
		out.print("(");
		boolean firstTime = true;
		for(int i = paramStart; i < fm.parameters.size();++i) {
			WhileyFile.Parameter p = fm.parameters.get(i);
			if(!firstTime) {
				out.print(", ");
			}
			firstTime=false;
			print(p.type);
			out.print(" ");
			out.print(p.name);
		}
		out.print(") -> ");
		print(fm.ret);

		if(!(fm.throwType instanceof SyntacticType.Void)) {
			out.print(" throws ");
			print(fm.throwType);
		}

		for(Expr r : fm.requires) {
			out.println();
			out.print("requires ");
			print(r);
		}
		for(Expr r : fm.ensures) {
			out.println();
			firstTime = false;
			out.print("ensures ");
			print(r);
		}

		out.println(":");

		print(fm.statements,1);
	}

	public void print(WhileyFile.Import decl) {
		out.print("import ");
		if(decl.name != null) {
			out.print(decl.name);
			out.print(" from ");
		}
		for(int i=0;i!=decl.filter.size();++i) {
			if(i != 0) {
				out.print(".");
			}
			String item = decl.filter.get(i);
			if(!item.equals("**")) {
				out.print(decl.filter.get(i));
			}
		}
		out.println();
	}

	public void print(WhileyFile.Constant decl) {
		out.println();
		out.print("constant ");
		out.print(decl.name());
		out.print(" is ");
		print(decl.constant);
		out.println();
	}

	public void print(WhileyFile.Type decl) {
		out.println();
		out.print("type ");
		out.print(decl.name());
		out.print(" is ");
		print(decl.pattern);

		if(decl.invariant != null) {
			out.print(" where ");
			print(decl.invariant);
		}

		out.println();
	}

	public void print(List<Stmt> statements, int indent) {
		for(Stmt s : statements) {
			print(s,indent);
		}
	}

	public void print(Stmt stmt, int indent) {
		indent(indent);

		if(stmt instanceof Stmt.Assert) {
			print((Stmt.Assert) stmt);
		} else if(stmt instanceof Stmt.Assign) {
			print((Stmt.Assign) stmt);
		} else if(stmt instanceof Stmt.Assume) {
			print((Stmt.Assume) stmt);
		} else if(stmt instanceof Stmt.Break) {
			print((Stmt.Break) stmt);
		} else if(stmt instanceof Stmt.Continue) {
			print((Stmt.Continue) stmt);
		} else if(stmt instanceof Stmt.Debug) {
			print((Stmt.Debug) stmt);
		} else if(stmt instanceof Stmt.DoWhile) {
			print((Stmt.DoWhile) stmt, indent);
		} else if(stmt instanceof Stmt.ForAll) {
			print((Stmt.ForAll) stmt, indent);
		} else if(stmt instanceof Stmt.IfElse) {
			print((Stmt.IfElse) stmt, indent);
		} else if(stmt instanceof Stmt.Return) {
			print((Stmt.Return) stmt);
		} else if(stmt instanceof Stmt.Skip) {
			print((Stmt.Skip) stmt);
		} else if(stmt instanceof Stmt.Switch) {
			print((Stmt.Switch) stmt, indent);
		} else if(stmt instanceof Stmt.While) {
			print((Stmt.While) stmt, indent);
		} else if(stmt instanceof Stmt.VariableDeclaration) {
			print((Stmt.VariableDeclaration) stmt, indent);
		} else if(stmt instanceof Expr.AbstractInvoke) {
			print((Expr.AbstractInvoke) stmt);
			out.println();
		} else {
			// should be dead-code
			throw new RuntimeException("Unknown statement kind encountered: "
					+ stmt.getClass().getName());
		}
	}

	public void print(Stmt.Assert s) {
		out.print("assert ");
		print(s.expr);
		out.println();
	}

	public void print(Stmt.Assume s) {
		out.print("assert ");
		print(s.expr);
		out.println();
	}

	public void print(Stmt.Debug s) {
		out.print("debug ");
		print(s.expr);
		out.println();
	}

	public void print(Stmt.Break s) {
		out.println("break");
	}

	public void print(Stmt.Continue s) {
		out.println("break");
	}

	public void print(Stmt.Skip s) {
		out.println("skip");
	}

	public void print(Stmt.Return s) {
		out.print("return");
		if(s.expr != null) {
			out.print(" ");
			print(s.expr);
		}
		out.println();
	}

	public void print(Stmt.Assign s) {
		print(s.lhs);
		out.print(" = ");
		print(s.rhs);
		out.println();
	}

	public void print(Stmt.IfElse s, int indent) {
		out.print("if ");
		print(s.condition);
		out.println(":");
		print(s.trueBranch,indent+1);
		if(!s.falseBranch.isEmpty()) {
			indent(indent);
			out.println("else:");
			print(s.falseBranch, indent+1);
		}
	}

	public void print(Stmt.DoWhile s, int indent) {
		out.println("do:");
		print(s.body,indent+1);
		indent(indent);
		// TODO: loop invariant
		out.print("while ");
		print(s.condition);
		out.println();
	}

	public void print(Stmt.While s, int indent) {
		out.print("while ");
		print(s.condition);

		boolean firstTime = true;
		for(Expr i : s.invariants) {
			if(!firstTime) {
				out.print(",");
			} else {
				firstTime = false;
			}
			out.print(" where ");
			print(i);
		}

		// TODO: loop invariant
		out.println(":");
		print(s.body,indent+1);
	}

	public void print(Stmt.ForAll s, int indent) {
		out.print("for ");
		boolean firstTime = true;
		for(String v : s.variables) {
			if(!firstTime) {
				out.print(", ");
			}
			firstTime=false;
			out.print(v);
		}
		out.print(" in ");
		print(s.source);

		if(s.invariant != null) {
			out.print(" where ");
			print(s.invariant);
		}

		out.println(":");
		print(s.body,indent+1);
	}

	public void print(Stmt.Switch s, int indent) {
		out.print("switch ");
		print(s.expr);
		out.println(":");
		for(Stmt.Case cas : s.cases) {
			indent(indent+1);
			boolean firstTime = true;
			if(cas.expr.isEmpty()) {
				out.print("default");
			} else {
				out.print("case ");
				for(Expr e : cas.expr) {
					if(!firstTime) {
						out.print(", ");
					}
					firstTime=false;
					print(e);
				}
			}

			out.println(":");
			print(cas.stmts,indent+2);
		}
	}

	public void print(Stmt.VariableDeclaration s, int indent) {
		print(s.pattern);
		if(s.expr != null) {
			out.print(" = ");
			print(s.expr);
		}
		out.println();
	}

	public void printWithBrackets(Expr expression, Class<? extends Expr>... matches) {
		boolean withBrackets = false;
		// First, decide whether brackets are needed or not
		for(Class<? extends Expr> match : matches) {
			if(match.isInstance(expression)) {
				withBrackets = true;
				break;
			}
		}
		// Second, print with brackets if needed
		if(withBrackets) {
			out.print("(");
			print(expression);
			out.print(")");
		} else {
			print(expression);
		}
	}

	public void print(Expr expression) {
		if (expression instanceof Expr.Constant) {
			print ((Expr.Constant) expression);
		} else if (expression instanceof Expr.AbstractVariable) {
			print ((Expr.AbstractVariable) expression);
		} else if (expression instanceof Expr.ConstantAccess) {
			print ((Expr.ConstantAccess) expression);
		} else if (expression instanceof Expr.List) {
			print ((Expr.List) expression);
		} else if (expression instanceof Expr.SubList) {
			print ((Expr.SubList) expression);
		} else if (expression instanceof Expr.BinOp) {
			print ((Expr.BinOp) expression);
		} else if (expression instanceof Expr.LengthOf) {
			print ((Expr.LengthOf) expression);
		} else if (expression instanceof Expr.Dereference) {
			print ((Expr.Dereference) expression);
		} else if (expression instanceof Expr.Cast) {
			print ((Expr.Cast) expression);
		} else if (expression instanceof Expr.IndexOf) {
			print ((Expr.IndexOf) expression);
		} else if (expression instanceof Expr.UnOp) {
			print ((Expr.UnOp) expression);
		} else if (expression instanceof Expr.AbstractInvoke) {
			print ((Expr.AbstractInvoke) expression);
		} else if (expression instanceof Expr.IndirectFunctionCall) {
			print ((Expr.IndirectFunctionCall) expression);
		} else if (expression instanceof Expr.IndirectMethodCall) {
			print ((Expr.IndirectMethodCall) expression);
		} else if (expression instanceof Expr.Quantifier) {
			print ((Expr.Quantifier) expression);
		} else if (expression instanceof Expr.FieldAccess) {
			print ((Expr.FieldAccess) expression);
		} else if (expression instanceof Expr.Record) {
			print ((Expr.Record) expression);
		} else if (expression instanceof Expr.Tuple) {
			print ((Expr.Tuple) expression);
		} else if (expression instanceof Expr.AbstractFunctionOrMethod) {
			print ((Expr.AbstractFunctionOrMethod) expression);
		} else if (expression instanceof Expr.Lambda) {
			print ((Expr.Lambda) expression);
		} else if (expression instanceof Expr.New) {
			print ((Expr.New) expression);
		} else if (expression instanceof Expr.TypeVal) {
			print ((Expr.TypeVal) expression);
		} else if (expression instanceof Expr.RationalLVal) {
			print ((Expr.RationalLVal) expression);
		} else {
			// should be dead-code
			throw new RuntimeException("Unknown expression kind encountered: " + expression.getClass().getName());
		}
	}

	public void print(Expr.Constant c) {
		out.print(c.value);
	}

	public void print(Expr.AbstractVariable v) {
		out.print(v);
	}

	public void print(Expr.ConstantAccess v) {
		if(v.qualification != null) {
			out.print(v.qualification + "." + v.name);
		} else {
			out.print(v.name);
		}
	}

	public void print(Expr.List e) {
		out.print("[");
		boolean firstTime = true;
		for(Expr i : e.arguments) {
			if(!firstTime) {
				out.print(", ");
			}
			firstTime=false;
			print(i);
		}
		out.print("]");
	}

	public void print(Expr.SubList e) {
		print(e.src);
		out.print("[");
		print(e.start);
		out.print("..");
		print(e.end);
		out.print("]");
	}

	public void print(Expr.BinOp e) {
		printWithBrackets(e.lhs, Expr.BinOp.class, Expr.Cast.class);
		out.print(" ");
		out.print(e.op);
		out.print(" ");
		printWithBrackets(e.rhs, Expr.BinOp.class, Expr.Cast.class);
	}

	public void print(Expr.LengthOf e) {
		out.print("|");
		print(e.src);
		out.print("|");
	}

	public void print(Expr.Dereference e) {
		out.print("*");
		print(e.src);
	}

	public void print(Expr.Cast e) {
		out.print("(");
		print(e.unresolvedType);
		out.print(") ");
		printWithBrackets(e.expr,Expr.BinOp.class,Expr.Cast.class);
	}

	public void print(Expr.IndexOf e) {
		print(e.src);
		out.print("[");
		print(e.index);
		out.print("]");
	}

	public void print(Expr.UnOp e) {
		switch(e.op) {
		case NEG:
			out.print("-");
			break;
		case NOT:
			out.print("!");
			break;
		case INVERT:
			out.print("~");
			break;
		}
		printWithBrackets(e.mhs,Expr.BinOp.class,Expr.Cast.class);
	}

	public void print(Expr.AbstractInvoke e) {
		if(e.qualification != null) {
			out.print(e.qualification.toString());
			out.print(".");
		}
		out.print(e.name);
		out.print("(");
		boolean firstTime = true;
		for(Expr i : e.arguments) {
			if(!firstTime) {
				out.print(", ");
			}
			firstTime=false;
			print(i);
		}
		out.print(")");
	}

	public void print(Expr.IndirectFunctionCall e) {
		print(e.src);
		out.print("(");
		boolean firstTime = true;
		for(Expr i : e.arguments) {
			if(!firstTime) {
				out.print(", ");
			}
			firstTime=false;
			print(i);
		}
		out.print(")");
	}

	public void print(Expr.IndirectMethodCall e) {
		print(e.src);
		out.print("(");
		boolean firstTime = true;
		for(Expr i : e.arguments) {
			if(!firstTime) {
				out.print(", ");
			}
			firstTime=false;
			print(i);
		}
		out.print(")");
	}

	public void print(Expr.Quantifier e) {
		switch(e.cop) {
		case NONE:
			out.print("no ");
			break;
		case SOME:
			out.print("some ");
			break;
		case ALL:
			out.print("all ");
			break;
		}

		out.print("{ ");

		boolean firstTime=true;
		for(Pair<String,Expr> src : e.sources) {
			if(!firstTime) {
				out.print(", ");
			}
			firstTime=false;
			out.print(src.first());
			out.print(" in ");
			print(src.second());
		}
		out.print(" | ");
		print(e.condition);
		
		out.print(" }");
	}

	public void print(Expr.FieldAccess e) {
		if(e.src instanceof Expr.Dereference) {
			printWithBrackets(((Expr.Dereference)e.src).src,Expr.New.class);
			out.print("->");
			out.print(e.name);
		} else {
			printWithBrackets(e.src,Expr.New.class);
			out.print(".");
			out.print(e.name);
		}
	}

	public void print(Expr.Record e) {
		out.print("{");
		boolean firstTime = true;
		for(Map.Entry<String,Expr> i : e.fields.entrySet()) {
			if(!firstTime) {
				out.print(", ");
			}
			firstTime=false;
			out.print(i.getKey());
			out.print(": ");
			print(i.getValue());
		}
		out.print("}");
	}

	public void print(Expr.Tuple e) {
		out.print("(");
		boolean firstTime = true;
		for(Expr i : e.fields) {
			if(!firstTime) {
				out.print(", ");
			}
			firstTime=false;
			print(i);
		}
		out.print(")");
	}

	public void print(Expr.AbstractFunctionOrMethod e) {
		out.print("&");
		out.print(e.name);
		if(e.paramTypes != null && e.paramTypes.size() > 0) {
			out.print("(");
			boolean firstTime = true;
			for(SyntacticType t : e.paramTypes) {
				if(!firstTime) {
					out.print(", ");
				}
				firstTime=false;
				print(t);
			}
			out.print(")");
		}
	}

	public void print(Expr.Lambda e) {
		out.print("&(");
		boolean firstTime = true;
		for(WhileyFile.Parameter p : e.parameters) {
			if(!firstTime) {
				out.print(", ");
			}
			firstTime=false;
			print(p.type);
			out.print(" ");
			out.print(p.name);
		}
		out.print(" -> ");
		print(e.body);
		out.print(")");
	}

	public void print(Expr.New e) {
		out.print("new ");
		print(e.expr);
	}

	public void print(Expr.TypeVal e) {
		print(e.unresolvedType);
	}

	public void print(Expr.RationalLVal e) {
		print(e.numerator);
		out.print(" / ");
		print(e.denominator);
	}

	public void print(List<Modifier> modifiers) {
		for(Modifier m : modifiers) {
			out.print(m);
			out.print(" ");
		}
	}

	public void print(TypePattern t) {
		if (t instanceof TypePattern.Leaf) {
			TypePattern.Leaf tp = (TypePattern.Leaf) t;
			print(tp.type);
			if (tp.var != null) {
				out.print(" ");
				out.print(tp.var);
			}
		} else if(t instanceof TypePattern.Union) {
			TypePattern.Union tp = (TypePattern.Union) t;
			boolean firstTime = true;
			for (TypePattern element : tp.elements) {
				if (!firstTime) {
					out.print(" | ");
				}
				firstTime = false;
				print(element);
			}
		} else if(t instanceof TypePattern.Intersection) {
			TypePattern.Intersection tp = (TypePattern.Intersection) t;
			boolean firstTime = true;
			for (TypePattern element : tp.elements) {
				if (!firstTime) {
					out.print(" & ");
				}
				firstTime = false;
				print(element);
			}
		} else if(t instanceof TypePattern.Record) {
			TypePattern.Record tp = (TypePattern.Record) t;
			boolean firstTime = true;
			out.print("{");
			for (TypePattern element : tp.elements) {
				if (!firstTime) {
					out.print(", ");
				}
				firstTime = false;
				print(element);
			}
			out.print("}");
		} else {
			TypePattern.Tuple tp = (TypePattern.Tuple) t;
			boolean firstTime = true;
			out.print("(");
			for (TypePattern element : tp.elements) {
				if (!firstTime) {
					out.print(", ");
				}
				firstTime = false;
				print(element);
			}
			out.print(")");
		}
	}

	public void print(SyntacticType t) {
		if(t instanceof SyntacticType.Any) {
			out.print("any");
		} else if(t instanceof SyntacticType.Bool) {
			out.print("bool");
		} else if(t instanceof SyntacticType.Byte) {
			out.print("byte");
		} else if(t instanceof SyntacticType.Int) {
			out.print("int");
		} else if(t instanceof SyntacticType.Null) {
			out.print("null");
		} else if(t instanceof SyntacticType.Real) {
			out.print("real");
		} else if(t instanceof SyntacticType.Void) {
			out.print("void");
		} else if(t instanceof SyntacticType.Nominal) {
			SyntacticType.Nominal nt = (SyntacticType.Nominal) t;
			boolean firstTime = true;
			for(String name : nt.names) {
				if(!firstTime) {
					out.print(".");
				}
				firstTime=false;
				out.print(name);
			}
		} else if(t instanceof SyntacticType.Set) {
			out.print("{");
			print(((SyntacticType.Set)t).element);
			out.print("}");
		} else if(t instanceof SyntacticType.List) {
			out.print("[");
			print(((SyntacticType.List)t).element);
			out.print("]");
		} else if(t instanceof SyntacticType.Map) {
			out.print("{");
			print(((SyntacticType.Map)t).key);
			out.print("=>");
			print(((SyntacticType.Map)t).value);
			out.print("}");
		} else if(t instanceof SyntacticType.Tuple) {
			SyntacticType.Tuple tt = (SyntacticType.Tuple) t;
			out.print("(");
			boolean firstTime = true;
			for(SyntacticType et : tt.types) {
				if(!firstTime) {
					out.print(", ");
				}
				firstTime=false;
				print(et);
			}
			out.print(")");
		} else if(t instanceof SyntacticType.FunctionOrMethod) {
			SyntacticType.FunctionOrMethod tt = (SyntacticType.FunctionOrMethod) t;

			print(tt.ret);

			if(t instanceof SyntacticType.Method) {
				out.print(" ::");
			}
			out.print("(");
			boolean firstTime = true;
			for(SyntacticType et : tt.paramTypes) {
				if(!firstTime) {
					out.print(", ");
				}
				firstTime=false;
				print(et);
			}
			out.print(")");
		} else if(t instanceof SyntacticType.Record) {
			SyntacticType.Record tt = (SyntacticType.Record) t;
			out.print("{");
			boolean firstTime = true;
			for(Map.Entry<String, SyntacticType> et : tt.types.entrySet()) {
				if(!firstTime) {
					out.print(", ");
				}
				firstTime=false;
				print(et.getValue());
				out.print(" ");
				out.print(et.getKey());
			}
			if(tt.isOpen) {
				out.print(", ...");
			}
			out.print("}");
		} else if(t instanceof SyntacticType.Reference) {
			out.print("ref ");
			print(((SyntacticType.Reference) t).element);
		} else if(t instanceof SyntacticType.Negation) {
			out.print("!");
			print(((SyntacticType.Negation) t).element);
		} else if(t instanceof SyntacticType.Union) {
			SyntacticType.Union ut = (SyntacticType.Union) t;
			boolean firstTime = true;
			for(SyntacticType et : ut.bounds) {
				if(!firstTime) {
					out.print(" | ");
				}
				firstTime=false;
				print(et);
			}
		} else if(t instanceof SyntacticType.Intersection) {
			SyntacticType.Intersection ut = (SyntacticType.Intersection) t;
			boolean firstTime = true;
			for(SyntacticType et : ut.bounds) {
				if(!firstTime) {
					out.print(" & ");
				}
				firstTime=false;
				print(et);
			}
		} else {
			// should be dead-code
			throw new RuntimeException("Unknown type kind encountered: " + t.getClass().getName());
		}
	}

	public void indent(int level) {
		for(int i=0;i!=level;++i) {
			out.print("    ");
		}
	}
}
