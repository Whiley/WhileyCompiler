package wycs.solver;

import java.util.HashMap;
import java.util.List;

import static wybs.lang.SyntaxError.*;
import static wycs.solver.Solver.Var;
import wybs.lang.SyntacticElement;
import wycs.io.Lexer;
import wycs.lang.*;

public class TypePropagation {
	private String filename;
	private HashMap<String, SemanticType> fnEnvironment;

	public void propagate(WycsFile wf) {
		fnEnvironment = new HashMap<String, SemanticType>();
		this.filename = wf.filename();
		
		for (Stmt s : wf.stmts()) {
			propagate(s);
		}
	}

	private void propagate(Stmt s) {
		if(s instanceof Stmt.Function) {
			propagate((Stmt.Function)s);
		} else if(s instanceof Stmt.Assert) {
			propagate((Stmt.Assert)s);
		} else {
			internalFailure("unknown statement encountered (" + s + ")",
					filename, s);
		}
	}
	
	private void propagate(Stmt.Function s) {
		
	}
	
	private void propagate(Stmt.Assert s) {
		HashMap<String,SemanticType> environment = new HashMap<String,SemanticType>();
		SemanticType t = propagate(s.expr, environment);
		checkIsSubtype(SemanticType.Bool,t, s.expr);
	}
	
	private SemanticType propagate(Expr e, HashMap<String, SemanticType> environment) {
		SemanticType t;
		if(e instanceof Expr.Variable) {
			t = propagate((Expr.Variable)e,environment);
		} else if(e instanceof Expr.Constant) {
			t = propagate((Expr.Constant)e,environment);
		} else if(e instanceof Expr.Unary) {
			t = propagate((Expr.Unary)e,environment);
		} else if(e instanceof Expr.Binary) {
			t = propagate((Expr.Binary)e,environment);
		} else if(e instanceof Expr.Nary) {
			t = propagate((Expr.Nary)e,environment);
		} else if(e instanceof Expr.Quantifier) {
			t = propagate((Expr.Quantifier)e,environment);
		} else {
			internalFailure("unknown expression encountered (" + e + ")",
					filename, e);
			return null;
		}
		e.attributes().add(new TypeAttribute(t));
		return t;
	}
	
	private SemanticType propagate(Expr.Variable e, HashMap<String, SemanticType> environment) {
		SemanticType t = environment.get(e.name);
		if(t == null) {
			internalFailure("undeclared variable encountered (" + e + ")",
					filename, e);
		}
		return t;
	}
	
	private SemanticType propagate(Expr.Constant e, HashMap<String, SemanticType> environment) {
		return e.value.type();
	}

	private SemanticType propagate(Expr.Unary e, HashMap<String, SemanticType> environment) {
		SemanticType op_type = propagate(e.operand,environment);
		
		switch(e.op) {
		case NOT:
			checkIsSubtype(SemanticType.Bool,op_type,e);
			return op_type;
		case NEG:
			checkIsSubtype(SemanticType.IntOrReal,op_type,e);
			return op_type;
		case LENGTHOF:
			checkIsSubtype(SemanticType.SetAny,op_type,e);
			return SemanticType.Int;		
		}
		
		internalFailure("unknown unary expression encountered (" + e + ")",
				filename, e);
		return null; // deadcode
	}
	
	private SemanticType propagate(Expr.Binary e, HashMap<String, SemanticType> environment) {
		SemanticType lhs_type = propagate(e.leftOperand,environment);
		SemanticType rhs_type = propagate(e.rightOperand,environment);
		
		switch(e.op) {
		case ADD:
		case SUB:
		case MUL:
		case DIV:
		case REM:
			checkIsSubtype(SemanticType.IntOrReal,lhs_type,e);
			checkIsSubtype(SemanticType.IntOrReal,rhs_type,e);
			return SemanticType.Or(lhs_type,rhs_type);
		case EQ:
		case NEQ:
			return SemanticType.Bool;
		case IMPLIES:
			checkIsSubtype(SemanticType.Bool,lhs_type,e);
			checkIsSubtype(SemanticType.Bool,rhs_type,e);
			return SemanticType.Bool;
		case LT:
		case LTEQ:
		case GT:
		case GTEQ:
			checkIsSubtype(SemanticType.IntOrReal,lhs_type,e);
			checkIsSubtype(SemanticType.IntOrReal,rhs_type,e);
			return SemanticType.Bool;
		case IN:
			checkIsSubtype(SemanticType.SetAny,rhs_type,e);
			SemanticType.Set s = (SemanticType.Set) rhs_type;
			checkIsSubtype(s.element(),lhs_type,e);
			return SemanticType.Bool;
		case SUBSET:
		case SUBSETEQ:
		case SUPSET:
		case SUPSETEQ:
			checkIsSubtype(SemanticType.SetAny,lhs_type,e);
			checkIsSubtype(SemanticType.SetAny,rhs_type,e);
			return SemanticType.Bool;
		}
		
		internalFailure("unknown binary expression encountered (" + e + ")",
				filename, e);
		return null; // deadcode
	}
	
	private SemanticType propagate(Expr.Nary e, HashMap<String, SemanticType> environment) {
		Expr[] e_operands = e.operands;
		SemanticType[] op_types = new SemanticType[e_operands.length];
		
		for(int i=0;i!=e_operands.length;++i) {
			op_types[i] = propagate(e_operands[i],environment);
		}
		
		switch(e.op) {
		case AND:
		case OR:
			for(int i=0;i!=e_operands.length;++i) {
				checkIsSubtype(SemanticType.Bool,op_types[i],e_operands[i]);
			}
			return SemanticType.Bool;
		case SET:
			return SemanticType.Set(SemanticType.Or(op_types));
		case TUPLE:
			return SemanticType.Tuple(op_types);
		}
		
		internalFailure("unknown nary expression encountered (" + e + ")",
				filename, e);
		return null; // deadcode
	}
	
	private SemanticType propagate(Expr.Quantifier e, HashMap<String, SemanticType> environment) {
		environment = new HashMap<String,SemanticType>(environment);
		List<SyntacticType> expr_vars = e.vars;
		
		for (int i = 0; i != expr_vars.size(); ++i) {
			SyntacticType p = expr_vars.get(i);
			if (p.name == null) {
				internalFailure("missing support for nested type names",
						filename, p);
			} 
			environment.put(p.name, convert(p));
		}
		
		return SemanticType.Bool;
	}
	
	/**
	 * <p>
	 * Convert a syntactic type into a semantic type. A syntactic type
	 * represents something written at the source-level which may be invalid, or
	 * not expressed in the minial form.
	 * </p>
	 * <p>
	 * For example, consider a syntactic type <code>int | !int</code>. This is a
	 * valid type at the source level, and appears to be a union of two types.
	 * In fact, semantically, this type is equivalent to <code>any</code> and,
	 * for the purposes of subtype testing, needs to be represented as such.
	 * </p>
	 * 
	 * 
	 * @param type
	 *            --- Syntactic type to be converted
	 * @return
	 */
	private SemanticType convert(SyntacticType type) {
		
		if (type instanceof SyntacticType.Primitive) {
			SyntacticType.Primitive p = (SyntacticType.Primitive) type;
			return p.type;
		} else if(type instanceof SyntacticType.Not) {
			SyntacticType.Not t = (SyntacticType.Not) type;
			return SemanticType.Not(convert(t.element));
		} else if(type instanceof SyntacticType.Set) {
			SyntacticType.Set t = (SyntacticType.Set) type;
			return SemanticType.Set(convert(t.element));
		} else if(type instanceof SyntacticType.Or) {
			SyntacticType.Tuple t = (SyntacticType.Tuple) type;
			SemanticType[] types = new SemanticType[t.elements.size()];
			for(int i=0;i!=t.elements.size();++i) {
				types[i] = convert(t.elements.get(i));
			}
			return SemanticType.Or(types);
		} else if(type instanceof SyntacticType.And) {
			SyntacticType.Tuple t = (SyntacticType.Tuple) type;
			SemanticType[] types = new SemanticType[t.elements.size()];
			for(int i=0;i!=t.elements.size();++i) {
				types[i] = convert(t.elements.get(i));
			}
			return SemanticType.And(types);
		} else if(type instanceof SyntacticType.Tuple) {
			SyntacticType.Tuple t = (SyntacticType.Tuple) type;
			SemanticType[] types = new SemanticType[t.elements.size()];
			for(int i=0;i!=t.elements.size();++i) {
				types[i] = convert(t.elements.get(i));
			}
			return SemanticType.Tuple(types);
		}
		
		internalFailure("unknown syntactic type encountered",
				filename, type);
		return null; // deadcode
	}
	
	/**
	 * Check that t1 :> t2 or, equivalently, that t2 is a subtype of t1. A type
	 * <code>t1</code> is said to be a subtype of another type <code>t2</code>
	 * iff the semantic set described by <code>t1</code> contains that described
	 * by <code>t2</code>.
	 * 
	 * @param t1
	 *            --- Semantic type that should contain <code>t2</code>.
	 * @param t2
	 *            --- Semantic type that shold be contained by <code>t1/code>.
	 * @param element
	 *            --- Syntax error is reported against this element if
	 *            <code>t1</code> does not contain <code>t2</code>.
	 */
	private void checkIsSubtype(SemanticType t1, SemanticType t2, SyntacticElement element) {
		if(!SemanticType.isSubtype(t1,t2)) {
			syntaxError("expected type " + t1 + ", got type " + t2,filename,element);
		}
	}
}
