package wycs.io;

import java.io.*;
import java.util.*;

import static wycc.lang.SyntaxError.*;
import wycc.util.Pair;
import wycs.syntax.*;
import wycs.syntax.Expr.Quantifier;

public class WyalFileClassicalPrinter {
	public static final String INDENT = "  ";
	
	private PrintWriter out;
	
	public WyalFileClassicalPrinter(OutputStream writer) throws UnsupportedEncodingException {
		this(new OutputStreamWriter(writer,"UTF-8"));		
	}
	
	public WyalFileClassicalPrinter(Writer writer) {
		this.out = new PrintWriter(writer);		
	}
	
	public void write(WyalFile wf) {
		for(WyalFile.Declaration d : wf.declarations()) {
			write(wf, d);
			out.println();
		}
		out.flush();
	}	
	
	private void write(WyalFile wf, WyalFile.Declaration s) {
		if(s instanceof WyalFile.Function) {
			write(wf,(WyalFile.Function)s);
		} else if(s instanceof WyalFile.Define) {
			write(wf,(WyalFile.Define)s);
		} else if(s instanceof WyalFile.Assert) {
			write(wf,(WyalFile.Assert)s);
		} else if(s instanceof WyalFile.Import) {
			write(wf,(WyalFile.Import)s);
		} else {
			internalFailure("unknown statement encountered " + s,
					wf.filename(), s);
		}
		out.println();
	}
	
	public void write(WyalFile wf, WyalFile.Import s) {
		String str = s.filter.toString();
		str = str.replace('/', '.');
		if (s.name == null) {
			out.print("import " + str);
		} else {			
			out.print("import " + s.name + " from " + str);
		}
	}
	
	public void write(WyalFile wf, WyalFile.Function s) {
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
	
	public void write(WyalFile wf, WyalFile.Define s) {
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
		if(s.body != null) {
			out.print(" as ");
			writeWithoutBraces(wf,s.body);
		}
	}
	
	public void write(WyalFile wf, WyalFile.Assert s) {
		out.print("assert ");		
		writeWithoutBraces(wf,s.expr);
		if(s.message != null) {
			out.print("; \"" + s.message + "\" ");
		}
		out.println();
	}
	
	public void writeWithBraces(WyalFile wf, Expr e) {
		boolean needsBraces = needsBraces(e);
		if(needsBraces) {
			out.print("(");
			writeWithoutBraces(wf,e);
			out.print(")");
		} else {
			writeWithoutBraces(wf,e);
		}
	}
	
	public void writeWithoutBraces(WyalFile wf, Expr e) {
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
		} else if(e instanceof Expr.IndexOf) {
			write(wf, (Expr.IndexOf)e);
		} else {
			internalFailure("unknown expression encountered " + e,
					wf.filename(), e);
		}
	}
	
	private void write(WyalFile wf, Expr.Unary e) {
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
	
	private void write(WyalFile wf, Expr.Nary e) {
		switch(e.op) {
		case AND:
		case OR: {
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
		case SET: {
			boolean firstTime=true;
			out.print("{");
			for(Expr operand : e.operands) {
				if(!firstTime) {
					out.print(", ");
				} else {
					firstTime = false;
				}			
				writeWithBraces(wf,operand);				
			}
			out.print("}");
			return;
		}
		case TUPLE:
		{
			boolean firstTime=true;
			out.print("(");
			for(Expr operand : e.operands) {
				if(!firstTime) {
					out.print(", ");
				} else {
					firstTime = false;
				}			
				writeWithoutBraces(wf,operand);				
			}
			out.print(")");
			return;
		}
		}
		internalFailure("unknown expression encountered " + e, wf.filename(), e);
	}
	
	private void write(WyalFile wf, Expr.Binary e) {
		writeWithBraces(wf,e.leftOperand);
		out.print(" " + e.op + " ");			
		writeWithBraces(wf,e.rightOperand);
	}
	
	private void write(WyalFile wf, Expr.Quantifier e) {
		if(e instanceof Expr.ForAll) {
			out.print("forall [ ");
		} else {
			out.print("exists [ ");
		}
		
		boolean firstTime=true;
		writeWithoutBraces(wf, e.pattern);				
		out.print(" : ");
		writeWithoutBraces(wf,e.operand);
		out.print("]");
	}
	
	private void write(WyalFile wf, Expr.FunCall e) {
		out.print(e.name);
		writeWithoutBraces(wf,e.operand);		
	}
	
	private void write(WyalFile wf, Expr.IndexOf e) {
		writeWithBraces(wf,e.operand);
		out.print("[");
		out.print(e.index);
		out.print("]");
	}		
	
	protected void writeWithBraces(WyalFile wf, TypePattern pattern) {
		if(pattern instanceof TypePattern.Tuple) {
			out.print("(");
			writeWithoutBraces(wf,pattern);
			out.print(")");
		} else {
			writeWithoutBraces(wf,pattern);
		}
	}
	
	protected void writeWithoutBraces(WyalFile wf, TypePattern p) {
		if(p instanceof TypePattern.Tuple) {
			TypePattern.Tuple t = (TypePattern.Tuple) p;
			for(int i=0;i!=t.patterns.length;++i) {
				if(i!=0) {
					out.print(", ");
				}
				writeWithBraces(wf,t.patterns[i]);
			}			
		} else {
			TypePattern.Leaf l = (TypePattern.Leaf) p; 
			out.print(l.type);
		}	
		if(p.var != null) {
			out.print(" " + p.var);		
		}
		
		if(p.constraint != null) {
			out.print(" where ");
			writeWithoutBraces(wf,p.constraint);
		} else if(p.source != null) {
			out.print(" in ");
			writeWithoutBraces(wf,p.source);
		}
	}
		
	private static boolean needsBraces(Expr e) {
		 if(e instanceof Expr.Binary) {			
			 Expr.Binary be = (Expr.Binary) e;
			 switch(be.op) {
			 case IMPLIES:
			 case LISTAPPEND:
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
