package wycs.io;

import java.io.*;
import java.util.*;

import static wybs.lang.SyntaxError.*;
import wybs.util.Pair;
import wycs.lang.*;
import wycs.lang.Expr.Quantifier;

public class WycsFilePrinter {
	public static final String INDENT = "  ";
	
	private PrintWriter out;
	
	public WycsFilePrinter(OutputStream writer) throws UnsupportedEncodingException {
		this(new OutputStreamWriter(writer,"UTF-8"));		
	}
	
	public WycsFilePrinter(Writer writer) {
		this.out = new PrintWriter(writer);		
	}
	
	public void write(WycsFile wf) {
		for(WycsFile.Declaration d : wf.declarations()) {
			write(wf, d);
			out.println();
		}
		out.flush();
	}	
	
	private void write(WycsFile wf, WycsFile.Declaration s) {
		if(s instanceof WycsFile.Function) {
			write(wf,(WycsFile.Function)s);
		} else if(s instanceof WycsFile.Define) {
			write(wf,(WycsFile.Define)s);
		} else if(s instanceof WycsFile.Assert) {
			write(wf,(WycsFile.Assert)s);
		} else if(s instanceof WycsFile.Import) {
			write(wf,(WycsFile.Import)s);
		} else {
			internalFailure("unknown statement encountered " + s,
					wf.filename(), s);
		}
		out.println();
	}
	
	public void write(WycsFile wf, WycsFile.Import s) {
		String str = s.filter.toString();
		str = str.replace('/', '.');
		if (s.name == null) {
			out.print("import " + str);
		} else {			
			out.print("import " + s.name + " from " + str);
		}
	}
	
	public void write(WycsFile wf, WycsFile.Function s) {
		out.print("function ");		
		out.print(s.name);
		if(s.generics.size() > 0) {
			out.print("<");
			boolean firstTime=true;
			for(String g : s.generics) {
				if(!firstTime) {
					out.print(", ");
				}
				firstTime=false;
				out.print(g);
			}
			out.print("> ");
		}
		out.print(s.from + " => " + s.to);		
		if(s.constraint != null) {
			out.print(" where ");
			writeWithoutBraces(wf,s.constraint);
		}
	}
	
	public void write(WycsFile wf, WycsFile.Define s) {
		out.print("define ");
		
		out.print(s.name);
		if(s.generics.size() > 0) {
			out.print("<");
			boolean firstTime=true;
			for(String g : s.generics) {
				if(!firstTime) {
					out.print(", ");
				}
				firstTime=false;
				out.print(g);
			}
			out.print(">");
		}
		out.print(s.from);
		if(s.condition != null) {
			out.print(" as ");
			writeWithoutBraces(wf,s.condition);
		}
	}
	
	public void write(WycsFile wf, WycsFile.Assert s) {
		out.print("assert ");		
		writeWithoutBraces(wf,s.expr);
		if(s.message != null) {
			out.print("; \"" + s.message + "\" ");
		}
		out.println();
	}
	
	public void writeWithBraces(WycsFile wf, Expr e) {
		boolean needsBraces = needsBraces(e);
		if(needsBraces) {
			out.print("(");
			writeWithoutBraces(wf,e);
			out.print(")");
		} else {
			writeWithoutBraces(wf,e);
		}
	}
	
	public void writeWithoutBraces(WycsFile wf, Expr e) {
		if(e instanceof Expr.Constant || e instanceof Expr.Variable) {
			out.print(e);
		} else if(e instanceof Expr.Unary) {
			write(wf, (Expr.Unary)e);
		} else if(e instanceof Expr.Nary) {
			write(wf, (Expr.Nary)e);
		} else if(e instanceof Expr.Quantifier) {
			write(wf, (Expr.Quantifier)e);
		} else if(e instanceof Expr.Binary) {
			write(wf, (Expr.Binary)e);
		} else if(e instanceof Expr.FunCall) {
			write(wf, (Expr.FunCall)e);
		} else {
			internalFailure("unknown expression encountered " + e,
					wf.filename(), e);
		}
	}
	
	private void write(WycsFile wf, Expr.Unary e) {
		switch(e.op) {
		case NOT:
			out.print("!");
			break;
		case NEG:
			out.print("-");
			break;
		case LENGTHOF:
			out.print("|");
			writeWithBraces(wf,e.operand);
			out.print("|");
			return;
		}
		writeWithBraces(wf,e.operand);					
	}
	
	private void write(WycsFile wf, Expr.Nary e) {
		switch(e.op) {
		case AND:
		case OR:
			String op = e.op == Expr.Nary.Op.AND ? "&&" : "||";
			boolean firstTime=true;
			for(Expr operand : e.operands) {
				if(!firstTime) {
					out.print(" " + op + " ");
				} else {
					firstTime = false;
				}			
				writeWithBraces(wf,operand);				
			}
			return;
		}
		out.print(e.toString());
	}
	
	private void write(WycsFile wf, Expr.Binary e) {
		writeWithBraces(wf,e.leftOperand);
		out.print(" " + e.op + " ");			
		writeWithBraces(wf,e.rightOperand);
	}
	
	private void write(WycsFile wf, Expr.Quantifier e) {
		if(e instanceof Expr.ForAll) {
			out.print("forall [ ");
		} else {
			out.print("exists [ ");
		}
		
		boolean firstTime=true;
		for (Pair<TypePattern,Expr> p : e.variables) {			
			if (!firstTime) {
				out.print(", ");
			} else {
				firstTime = false;
			}
			out.print(p.first());
			if(p.second() != null) {
				out.print(" in " + p.second());
			}
		}		
		out.print(" : ");
		writeWithoutBraces(wf,e.operand);
		out.print("]");
	}
	
	private void write(WycsFile wf, Expr.FunCall e) {
		out.print(e.name);
		out.print("(");
		writeWithoutBraces(wf,e.operand);		
		out.print(")");
	}
	
	private static boolean needsBraces(Expr e) {
		 if(e instanceof Expr.Binary) {			
			 Expr.Binary be = (Expr.Binary) e;
			 switch(be.op) {
			 case IMPLIES:
				 return true;
			 }
		 } else if(e instanceof Expr.Nary) {
			 Expr.Nary ne = (Expr.Nary) e;
			 switch(ne.op) {
			 case AND:
			 case OR:
				 return true;
			 }
		 } 
		 return false;
	}
}
