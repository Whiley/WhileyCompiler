package wycs.io;

import java.io.*;
import java.util.*;

import wybs.util.Pair;
import wycs.lang.*;

public class WycsFilePrinter {
	public static final String INDENT = "  ";
	
	private PrintStream out;
	
	public WycsFilePrinter(PrintStream writer) {
		this.out = writer;
	}
		
	public WycsFilePrinter(OutputStream writer) {
		try {
			this.out = new PrintStream(writer, true, "UTF-8");
		} catch(UnsupportedEncodingException e) {
			this.out = new PrintStream(writer);
		}
	}
	
	public void write(WycsFile wf) {
		for(Stmt s : wf.stmts()) {
			write(s);
			out.println();
			out.println();
		}
		out.flush();
	}	
	
	private void write(Stmt s) {
		if(s instanceof Stmt.Define) {
			write((Stmt.Define)s);
		} else {
			write((Stmt.Assert)s);
		}
	}
	
	private void write(Stmt.Define s) {
		out.print("define " + s.name);
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
		out.print("(");
		boolean firstTime=true;
		for(Pair<Type,String> p : s.arguments) {
			if(!firstTime) {
				out.print(", ");
			}
			firstTime=false;
			out.print(p.first() + " " + p.second());
		}
		out.print(") as ");
		write(s.expr,1,true);
	}
	
	private void write(Stmt.Assert s) {
		out.print("assert ");
		if(s.message != null) {
			out.print("\"" + s.message + "\" ");
		}
		write(s.expr,1,true);
	}
	
	private void write(Expr e, int indent, boolean indented) {
		if(e instanceof Expr.Nary) {
			write((Expr.Nary)e,indent,indented);
		} else if(e instanceof Expr.Quantifier) {
			write((Expr.Quantifier)e,indent,indented);
		} else if(e instanceof Expr.Binary) {
			write((Expr.Binary)e,indent,indented);
		} else {
			indent(indent,indented);
			out.print(e);
		}
	}
	
	private void write(Expr.Nary e, int indent, boolean indented) {
		switch(e.op) {
		case AND:
		case OR:
			String op = e.op == Expr.Nary.Op.AND ? "&&" : "||";
			boolean firstTime=true;
			for(Expr operand : e.operands) {
				if(!firstTime) {
					out.println(" " + op);
					indented = false;
				} else {
					firstTime = false;
				}							
				write(operand,indent,indented);
			}
			return;
		}
		indent(indent,indented);
		out.print(e.toString());
	}
	
	private void write(Expr.Binary e, int indent, boolean indented) {
		switch(e.op) {
		case IMPLIES:
			write(e.leftOperand,indent,indented);
			out.println();
			indent(indent,false);
			out.println("==>");
			write(e.rightOperand,indent+1,false);
			return;
		}
		indent(indent,indented);
		out.print(e.toString());
	}
	
	private void write(Expr.Quantifier e, int indent, boolean indented) {
		indent(indent,indented);
		
		if(e instanceof Expr.ForAll) {
			out.print("forall [ ");
		} else {
			out.print("some [ ");
		}
		
		boolean firstTime=true;
		for(Pair<Type,String> p : e.vars) {
			if(!firstTime) {
				out.print(", ");
			} else {
				firstTime=false;
			}
			out.print(p.first() + " " + p.second());
		}
		out.println(" :");
		write(e.expr,indent + 1,false);
		out.println();
		indent(indent,false);
		out.print("]");
	}
	
	private void indent(int level, boolean indented) {
		if(!indented) {
			for(int i=0;i<level;++i) {
				out.print(INDENT);
			}		
		}
	}	
}
