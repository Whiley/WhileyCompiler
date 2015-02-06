package wycs.io;

import static wycc.lang.SyntaxError.internalFailure;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import wycc.io.Token;
import wycc.lang.SyntacticElement;
import wycc.util.Pair;
import wycc.util.Triple;
import wycs.core.Code;
import wycs.core.SemanticType;
import wycs.core.WycsFile;

public class WycsFilePrinter {
	private PrintWriter out;
	private boolean raw=false;

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
		} else if(s instanceof WycsFile.Macro) {
			write(wf,(WycsFile.Macro)s);
		} else if(s instanceof WycsFile.Type) {
			write(wf,(WycsFile.Type)s);
		} else if(s instanceof WycsFile.Assert) {
			write(wf,(WycsFile.Assert)s);
		} else {
			internalFailure("unknown statement encountered " + s,
					wf.filename(), (SyntacticElement) s);
		}
		out.println();
	}

	public void write(WycsFile wf, WycsFile.Function s) {
		out.print("function ");
		out.print(s.name);
		SemanticType[] generics = s.type.generics();
		if(generics.length > 0) {
			out.print("<");
			boolean firstTime=true;
			for(SemanticType g : generics) {
				if(!firstTime) {
					out.print(", ");
				}
				firstTime=false;
				out.print(((SemanticType.Var)g).name());
			}
			out.print(">");
		}
		out.print("(" + s.type.element(0) + ") => " + s.type.element(1));
		if(s.constraint != null) {
			out.println(" where:");
			indent(1);
			write(wf,s.constraint);
		}
	}

	public void write(WycsFile wf, WycsFile.Macro s) {
		out.print("define ");

		out.print(s.name);
		SemanticType[] generics = s.type.generics();
		if(generics.length > 0) {
			out.print("<");
			boolean firstTime=true;
			for(SemanticType g : generics) {
				if(!firstTime) {
					out.print(", ");
				}
				firstTime=false;
				out.print(((SemanticType.Var)g).name());
			}
			out.print(">");
		}
		out.print("(" + s.type.from() + ") => " + s.type.to());
		if(s.condition != null) {
			out.println(" as:");
			write(wf,s.condition);
		}
	}

	public void write(WycsFile wf, WycsFile.Type s) {
		out.print("type ");

		out.print(s.name);
		out.print(" is " + s.type);
		if(s.invariant != null) {
			out.println(" where:");
			write(wf,s.invariant);
		}
	}
	
	public void write(WycsFile wf, WycsFile.Assert s) {
		out.print("assert ");
		if(s.message != null) {
			out.print("\"" + s.message + "\"");
		}
		out.println(":");
		write(wf,s.condition);
		out.println();
	}

	public void write(WycsFile wf, Code<?> code) {
		if(raw) {
			writeRaw(wf,code,0);
		} else {
			indent(1);
			writeStructured(wf,code,1);
		}
	}

	public void writeStructured(WycsFile wf, Code<?> code, int indent) {
		if(code instanceof Code.Variable) {
			writeStructured(wf, (Code.Variable) code, indent);
		} else if(code instanceof Code.Constant) {
			writeStructured(wf, (Code.Constant) code, indent);
		} else if(code instanceof Code.Unary) {
			writeStructured(wf, (Code.Unary) code, indent);
		} else if(code instanceof Code.Binary) {
			writeStructured(wf, (Code.Binary) code, indent);
		} else if(code instanceof Code.Nary) {
			writeStructured(wf, (Code.Nary) code, indent);
		} else if(code instanceof Code.Load) {
			writeStructured(wf, (Code.Load) code, indent);
		} else if(code instanceof Code.Is) {
			writeStructured(wf, (Code.Is) code, indent);
		} else if(code instanceof Code.Cast) {
			writeStructured(wf, (Code.Cast) code, indent);
		} else if(code instanceof Code.FunCall) {
			writeStructured(wf, (Code.FunCall) code, indent);
		} else if(code instanceof Code.Quantifier) {
			writeStructured(wf, (Code.Quantifier) code, indent);
		} else {
			internalFailure("unknown bytecode encountered", wf.filename(), code);
		}
	}

	public void writeStructured(WycsFile wf, Code.Variable code, int indent) {
		out.print("r" + code.index);
	}

	public void writeStructured(WycsFile wf, Code.Constant code, int indent) {
		out.print(code.value);
	}

	public void writeStructured(WycsFile wf, Code.Unary code, int indent) {
		switch(code.opcode) {
		case NEG:
			out.print("-");
			writeStructured(wf,code.operands[0],indent);
			break;
		case NOT:
			out.println("not:");
			indent(indent+1);
			writeStructured(wf,code.operands[0],indent+1);
			break;
		case LENGTH:
			out.print("|");
			writeStructured(wf,code.operands[0],indent);
			out.print("|");
			break;
		default:
			internalFailure("unknown bytecode encountered", wf.filename(), code);
		}
	}

	public void writeStructured(WycsFile wf, Code.Binary code, int indent) {
		String op;
		switch(code.opcode) {
		case ADD:
			op = " + ";
			break;
		case SUB:
			op = " - ";
			break;
		case MUL:
			op = " * ";
			break;
		case DIV:
			op = " / ";
			break;
		case REM:
			op = " % ";
			break;
		case EQ:
			op = " == ";
			break;
		case NEQ:
			op = " != ";
			break;
		case LT:
			op = " < ";
			break;
		case LTEQ:
			op = " <= ";
			break;
		case IN:
			op = " in ";
			break;
		case SUBSET:
			op = " " + Token.sUC_SUBSET + " ";
			break;
		case SUBSETEQ:
			op = " " + Token.sUC_SUBSETEQ + " ";
			break;
		default:
			internalFailure("unknown bytecode encountered", wf.filename(), code);
			return;
		}

		writeStructured(wf,code.operands[0],indent);
		out.print(op);
		writeStructured(wf,code.operands[1],indent);
	}

	public void writeStructured(WycsFile wf, Code.Nary code, int indent) {
		switch(code.opcode) {
		case AND:
			for(int i=0;i!=code.operands.length;++i) {
				if(i!=0) {
					out.println();
					indent(indent);
				}
				writeStructured(wf,code.operands[i],indent);
			}
			break;
		case OR:
			for(int i=0;i!=code.operands.length;++i) {
				if(i!=0) {
					out.println();
					indent(indent);
				}
				out.println("case:");
				indent(indent+1);
				writeStructured(wf,code.operands[i],indent+1);
			}
			break;
		case TUPLE:
			out.print("(");
			for(int i=0;i!=code.operands.length;++i) {
				if(i!=0) {
					out.print(", ");
				}
				writeStructured(wf,code.operands[i],indent);
			}
			out.print(")");
			break;
		case SET:
			out.print("{");
			for(int i=0;i!=code.operands.length;++i) {
				if(i!=0) {
					out.print(", ");
				}
				writeStructured(wf,code.operands[i],indent);
			}
			out.print("}");
			break;
		default:
			internalFailure("unknown bytecode encountered", wf.filename(), code);
			return;
		}
	}

	public void writeStructured(WycsFile wf, Code.Load code, int indent) {
		writeStructured(wf,code.operands[0],indent);
		out.print("[" + code.index + "]");
	}

	public void writeStructured(WycsFile wf, Code.Is code, int indent) {
		writeStructured(wf,code.operands[0],indent);
		out.print(" is " + code.type);
	}
	
	public void writeStructured(WycsFile wf, Code.Cast code, int indent) {
		out.print("(" + code.type + ")");
		writeStructured(wf,code.operands[0],indent);		
	}
	
	public void writeStructured(WycsFile wf, Code.FunCall code, int indent) {
		out.print(code.nid + "(");
		writeStructured(wf,code.operands[0],indent);
		out.print(")");
	}

	public void writeStructured(WycsFile wf, Code.Quantifier code, int indent) {
		if(code.opcode == Code.Op.FORALL) {
			out.print("forall(");
		} else {
			out.print("some(");
		}
		boolean firstTime=true;
		for(Pair<SemanticType,Integer> p : code.types) {
			if(!firstTime) {
				out.print(", ");
			}
			firstTime=false;
			out.print(p.first() + " r" + p.second());
		}
		out.println("):");
		indent(indent+1);
		writeStructured(wf,code.operands[0],indent+1);
	}

	public int writeRaw(WycsFile wf, Code<?> code, int index) {
		int[] operands = new int[code.operands.length];
		int next = index;
		for(int i=0;i!=operands.length;++i) {
			next = writeRaw(wf,code.operands[i],next);
			operands[i] = next;
		}
		indent(1);
		next = next + 1;
		out.print("#" + next + " = ");
		out.print(code.opcode.toString());
		if(operands.length > 0) {
			out.print("(");
			for(int i=0;i!=operands.length;++i) {
				if(i != 0) {
					out.print(", ");
				}
				out.print("#" + operands[i]);
			}
			out.print(")");
		}
		if(code instanceof Code.Constant) {
			Code.Constant c = (Code.Constant) code;
			out.print(" " + c.value);
		} else if(code instanceof Code.Variable) {
			Code.Variable c = (Code.Variable) code;
			out.print(" " + c.index);
		}
		out.println(" : " + code.type);
		return next;
	}

	private void indent(int indent) {
		indent = indent * 4;
		for(int i=0;i<indent;++i) {
			out.print(" ");
		}
	}
}
