package wyc.io;

import static wyc.lang.WhileyFile.internalFailure;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import wybs.util.Pair;
import wyc.lang.Expr;
import wyc.lang.Stmt;
import wyc.lang.SyntacticType;
import wyc.lang.WhileyFile;
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
	private PrintWriter out;
	
	public WhileyFilePrinter(PrintWriter writer) {
		this.out = writer;
	}
	
	public WhileyFilePrinter(OutputStream stream) {
		this.out = new PrintWriter(new OutputStreamWriter(stream));
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
		} else if(decl instanceof WhileyFile.FunctionOrMethod) {
			print((WhileyFile.FunctionOrMethod)decl);
		} else {
			throw new RuntimeException("Unknown construct encountered: "
					+ decl.getClass().getName());
		}
	}
	
	public void print(WhileyFile.FunctionOrMethod fm) {
		print(fm.modifiers);
		print(fm.ret);
		out.print(" ");
		
		if(fm instanceof WhileyFile.Method) {
			WhileyFile.Method m = (WhileyFile.Method) fm;
			out.print("::");
		}
		
		out.print(fm.name);
		out.print("(");
		boolean firstTime = true;		
		for(WhileyFile.Parameter p : fm.parameters) {
			if(!firstTime) {
				out.print(", ");
			}
			firstTime=false;
			print(p.type);
			out.print(" ");
			out.print(p.name);
		}
		out.println("):");
		print(fm.statements,1);
	}
	
	public void print(WhileyFile.Import decl) {		
		out.print("import ");
		if(decl.name != null) {
			out.print(decl.name);
			out.print(" from ");
		}
		out.println(decl.filter.toString().replace('/','.'));
	}
	
	public void print(WhileyFile.Constant decl) {
		out.print("define ");
		out.print(decl.name);
		out.print(" as ");
		print(decl.constant);
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
		} else if(stmt instanceof Stmt.Debug) {
			print((Stmt.Debug) stmt);
		} else if(stmt instanceof Stmt.DoWhile) {
			// TODO
		} else if(stmt instanceof Stmt.ForAll) {
			// TODO
		} else if(stmt instanceof Stmt.IfElse) {
			// TODO
		} else if(stmt instanceof Stmt.Return) {
			print((Stmt.Return) stmt);
		} else if(stmt instanceof Stmt.Skip) {
			print((Stmt.Skip) stmt);
		} else if(stmt instanceof Stmt.Switch) {
			// TODO
		} else if(stmt instanceof Stmt.Throw) {
			print((Stmt.Throw) stmt);
		} else if(stmt instanceof Stmt.TryCatch) {
			// TODO
		} else if(stmt instanceof Stmt.While) {
			// TODO
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
	
	public void print(Stmt.Throw s) {
		out.print("throw ");
		print(s.expr);
		out.println();
	}
	
	public void print(Stmt.Break s) {
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
	
	public void print(Expr expression) {
		if (expression instanceof Expr.Constant) {
			print ((Expr.Constant) expression);
		} else if (expression instanceof Expr.AbstractVariable) {
			print ((Expr.AbstractVariable) expression);
		} else if (expression instanceof Expr.ConstantAccess) {
			print ((Expr.ConstantAccess) expression);
		} else if (expression instanceof Expr.Set) {
			print ((Expr.Set) expression);
		} else if (expression instanceof Expr.List) {
			print ((Expr.List) expression);
		} else if (expression instanceof Expr.SubList) {
			print ((Expr.SubList) expression);
		} else if (expression instanceof Expr.SubString) {
			print ((Expr.SubString) expression);
		} else if (expression instanceof Expr.BinOp) {
			print ((Expr.BinOp) expression);
		} else if (expression instanceof Expr.LengthOf) {
			print ((Expr.LengthOf) expression);
		} else if (expression instanceof Expr.Dereference) {
			print ((Expr.Dereference) expression);
		} else if (expression instanceof Expr.Convert) {
			print ((Expr.Convert) expression);
		} else if (expression instanceof Expr.IndexOf) {
			print ((Expr.IndexOf) expression);
		} else if (expression instanceof Expr.UnOp) {
			print ((Expr.UnOp) expression);
		} else if (expression instanceof Expr.FunctionCall) {
			print ((Expr.FunctionCall) expression);
		} else if (expression instanceof Expr.MethodCall) {
			print ((Expr.MethodCall) expression);
		} else if (expression instanceof Expr.IndirectFunctionCall) {
			print ((Expr.IndirectFunctionCall) expression);
		} else if (expression instanceof Expr.IndirectMethodCall) {
			print ((Expr.IndirectMethodCall) expression);
		} else if (expression instanceof Expr.Comprehension) {
			print ((Expr.Comprehension) expression);
		} else if (expression instanceof Expr.RecordAccess) {
			print ((Expr.RecordAccess) expression);
		} else if (expression instanceof Expr.Record) {
			print ((Expr.Record) expression);
		} else if (expression instanceof Expr.Tuple) {
			print ((Expr.Tuple) expression);
		} else if (expression instanceof Expr.Map) {
			print ((Expr.Map) expression);
		} else if (expression instanceof Expr.FunctionOrMethod) {
			print ((Expr.FunctionOrMethod) expression);
		} else if (expression instanceof Expr.Lambda) {
			print ((Expr.Lambda) expression);
		} else if (expression instanceof Expr.New) {
			print ((Expr.New) expression);
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
		out.print(v.nid);
	}
	
	public void print(Expr.Set e) {
		out.print("{");
		boolean firstTime = true;
		for(Expr i : e.arguments) {
			if(!firstTime) {
				out.print(", ");
			}
			firstTime=false;
			print(i);
		}
		out.print("}");
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
	
	public void print(Expr.SubString e) {
		print(e.src);
		out.print("[");
		print(e.start);
		out.print("..");
		print(e.end);
		out.print("]");
	}
	
	public void print(Expr.BinOp e) {
		print(e.lhs);
		out.print(" ");
		out.print(e.op);
		out.print(" ");
		print(e.rhs);
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
	
	public void print(Expr.Convert e) {
		// TODO
		throw new RuntimeException("TODO: " + e.getClass().getName());
	}
	
	public void print(Expr.IndexOf e) {
		print(e.src);
		out.print("[");
		print(e.index);
		out.print("]");
	}
	
	public void print(Expr.UnOp e) {
		out.print(e.op);
		print(e.mhs);
	}
	
	public void print(Expr.FunctionCall e) {
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
	
	public void print(Expr.MethodCall e) {
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
	
	public void print(Expr.Comprehension e) {
		// TODO
		throw new RuntimeException("TODO: " + e.getClass().getName());
	}
	
	public void print(Expr.RecordAccess e) {
		print(e.src);
		out.print(".");
		out.print(e.name);
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
	
	public void print(Expr.Map e) {
		out.print("{");
		boolean firstTime = true;
		for(Pair<Expr,Expr> p : e.pairs) {
			if(!firstTime) {
				out.print(", ");
			}
			firstTime=false;
			print(p.first());
			out.print("=>");
			print(p.second());
		}
		out.print("}");
	}
	
	public void print(Expr.FunctionOrMethod e) {
		// TODO
		throw new RuntimeException("TODO: " + e.getClass().getName());
	}
	
	public void print(Expr.Lambda e) {
		// TODO
		throw new RuntimeException("TODO: " + e.getClass().getName());
	}
	
	public void print(Expr.New e) {
		out.print("new ");
		print(e.expr);
	}
	
	public void print(List<Modifier> modifiers) {
		for(Modifier m : modifiers) {							
			out.print(m);
			out.print(" ");
		}
	}
	
	public void print(SyntacticType t) {
		if(t instanceof SyntacticType.Any) {
			out.print("any");
		} else if(t instanceof SyntacticType.Bool) {
			out.print("bool");
		} else if(t instanceof SyntacticType.Byte) {
			out.print("byte");
		} else if(t instanceof SyntacticType.Char) {
			out.print("char");
		} else if(t instanceof SyntacticType.Int) {
			out.print("int");
		} else if(t instanceof SyntacticType.Null) {
			out.print("null");
		} else if(t instanceof SyntacticType.Strung) {
			out.print("string");
		} else if(t instanceof SyntacticType.Real) {
			out.print("real");
		} else if(t instanceof SyntacticType.Void) {
			out.print("void");
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
