package wyc.io;

import static wyc.lang.WhileyFile.internalFailure;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import wyc.lang.Expr;
import wyc.lang.WhileyFile;

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
		} else {
			throw new RuntimeException("Unknown construct encountered: "
					+ decl.getClass().getName());
		}
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
		throw new RuntimeException("TODO: " + e.getClass().getName());
	}
	
	public void print(Expr.SubString e) {
		throw new RuntimeException("TODO: " + e.getClass().getName());
	}
	
	public void print(Expr.BinOp e) {
		throw new RuntimeException("TODO: " + e.getClass().getName());
	}
	
	public void print(Expr.LengthOf e) {
		throw new RuntimeException("TODO: " + e.getClass().getName());
	}
	
	public void print(Expr.Dereference e) {
		throw new RuntimeException("TODO: " + e.getClass().getName());
	}
	
	public void print(Expr.Convert e) {
		throw new RuntimeException("TODO: " + e.getClass().getName());
	}
	
	public void print(Expr.IndexOf e) {
		throw new RuntimeException("TODO: " + e.getClass().getName());
	}
	
	public void print(Expr.UnOp e) {
		throw new RuntimeException("TODO: " + e.getClass().getName());
	}
	
	public void print(Expr.FunctionCall e) {
		throw new RuntimeException("TODO: " + e.getClass().getName());
	}
	
	public void print(Expr.MethodCall e) {
		throw new RuntimeException("TODO: " + e.getClass().getName());
	}
	
	public void print(Expr.IndirectFunctionCall e) {
		throw new RuntimeException("TODO: " + e.getClass().getName());
	}
	
	public void print(Expr.IndirectMethodCall e) {
		throw new RuntimeException("TODO: " + e.getClass().getName());
	}
	
	public void print(Expr.Comprehension e) {
		throw new RuntimeException("TODO: " + e.getClass().getName());
	}
	
	public void print(Expr.RecordAccess e) {
		throw new RuntimeException("TODO: " + e.getClass().getName());
	}
	
	public void print(Expr.Record e) {
		throw new RuntimeException("TODO: " + e.getClass().getName());
	}
	
	public void print(Expr.Tuple e) {
		throw new RuntimeException("TODO: " + e.getClass().getName());
	}
	
	public void print(Expr.Map e) {
		throw new RuntimeException("TODO: " + e.getClass().getName());
	}
	
	public void print(Expr.FunctionOrMethod e) {
		throw new RuntimeException("TODO: " + e.getClass().getName());
	}
	
	public void print(Expr.Lambda e) {
		throw new RuntimeException("TODO: " + e.getClass().getName());
	}
	
	public void print(Expr.New e) {
		throw new RuntimeException("TODO: " + e.getClass().getName());
	}
}
