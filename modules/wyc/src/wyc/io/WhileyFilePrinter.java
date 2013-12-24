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
		} else if(decl instanceof WhileyFile.TypeDef) {
			print((WhileyFile.TypeDef)decl);
		} else if(decl instanceof WhileyFile.FunctionOrMethod) {
			print((WhileyFile.FunctionOrMethod)decl);
		} else {
			throw new RuntimeException("Unknown construct encountered: "
					+ decl.getClass().getName());
		}
	}
	
	public void print(WhileyFile.FunctionOrMethod fm) {
		out.println();
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
		out.println();
		out.print("define ");
		out.print(decl.name);
		out.print(" as ");
		print(decl.constant);
		out.println();
	}
	
	public void print(WhileyFile.TypeDef decl) {
		out.println();
		out.print("define ");
		out.print(decl.name);
		out.print(" as ");
		print(decl.unresolvedType);
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
			// TODO
		} else if(stmt instanceof Stmt.Throw) {
			print((Stmt.Throw) stmt);
		} else if(stmt instanceof Stmt.TryCatch) {
			// TODO
		} else if(stmt instanceof Stmt.While) {
			print((Stmt.While) stmt, indent);
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
		out.print("while ");
		print(s.condition);
		out.println();
	}
	
	public void print(Stmt.While s, int indent) {
		out.print("while ");
		print(s.condition);
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
		out.println(":");
		print(s.body,indent+1);
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
		} else if (expression instanceof Expr.AbstractInvoke) {
			print ((Expr.AbstractInvoke) expression);
		} else if (expression instanceof Expr.IndirectFunctionCall) {
			print ((Expr.IndirectFunctionCall) expression);
		} else if (expression instanceof Expr.IndirectMethodCall) {
			print ((Expr.IndirectMethodCall) expression);
		} else if (expression instanceof Expr.Comprehension) {
			print ((Expr.Comprehension) expression);
		} else if (expression instanceof Expr.AbstractDotAccess) {
			print ((Expr.AbstractDotAccess) expression);
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
	
	public void print(Expr.AbstractInvoke<Expr> e) {
		if(e.qualification != null) {
			print(e.qualification);
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
	
	public void print(Expr.Comprehension e) {
		// TODO
		throw new RuntimeException("TODO: " + e.getClass().getName());
	}
	
	public void print(Expr.AbstractDotAccess e) {
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
		} else if(t instanceof SyntacticType.Not) {
			out.print("!");
			print(((SyntacticType.Not) t).element);
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
