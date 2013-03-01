package wycs.transforms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static wybs.lang.SyntaxError.*;
import static wycs.solver.Solver.Var;
import wybs.lang.SyntacticElement;
import wybs.util.Pair;
import wycs.io.Lexer;
import wycs.lang.*;

public class TypePropagation {
	private String filename;
	private HashMap<String, SemanticType.Tuple> fnEnvironment;

	public void propagate(WycsFile wf) {
		fnEnvironment = new HashMap<String, SemanticType.Tuple>();
		this.filename = wf.filename();
		
		for (WycsFile.Declaration s : wf.declarations()) {
			propagate(s);
		}
	}

	private void propagate(WycsFile.Declaration s) {
		if(s instanceof WycsFile.Function) {
			propagate((WycsFile.Function)s);
		} else if(s instanceof WycsFile.Assert) {
			propagate((WycsFile.Assert)s);
		} else if(s instanceof WycsFile.Import) {
			// can ignore for now
		} else {
			internalFailure("unknown statement encountered (" + s + ")",
					filename, s);
		}
	}
	
	private void propagate(WycsFile.Function s) {
		HashSet<String> generics = new HashSet<String>(s.generics);		
		SemanticType from = convert(s.from,generics);
		SemanticType to = convert(s.to,generics);
		
		fnEnvironment.put(s.name, SemanticType.Tuple(from,to));
				
		HashMap<String,SemanticType> environment = new HashMap<String,SemanticType>();		
		addNamedVariables(s.from, environment,generics);
		addNamedVariables(s.to, environment,generics);
		SemanticType r = propagate(s.condition,environment,generics);
		checkIsSubtype(SemanticType.Bool,r,s.condition);		
	}
	
	private void addNamedVariables(SyntacticType type,
			HashMap<String,SemanticType> environment, HashSet<String> generics) {
		
		if(type.name != null) {
			if(environment.containsKey(type.name)) {
				internalFailure("duplicate variable name encountered",filename,type);
			}
			environment.put(type.name, convert(type,generics));
		}
		
		if(type instanceof SyntacticType.And) {
			// Don't go further here, since we currently can't use the names.
		} else if(type instanceof SyntacticType.Or) {
			// Don't go further here, since we currently can't use the names.
		} else if(type instanceof SyntacticType.Not) {
			SyntacticType.Not st = (SyntacticType.Not) type;			
			addNamedVariables(st.element,environment,generics);
		} else if(type instanceof SyntacticType.Set) {
			// Don't go further here, since we currently can't use the names.
		} else if(type instanceof SyntacticType.Tuple) {
			SyntacticType.Tuple st = (SyntacticType.Tuple) type;
			for(SyntacticType t : st.elements) {
				addNamedVariables(t,environment,generics);
			}
		}
	}
	
	private void propagate(WycsFile.Assert s) {
		HashMap<String,SemanticType> environment = new HashMap<String,SemanticType>();
		SemanticType t = propagate(s.expr, environment, new HashSet<String>());
		checkIsSubtype(SemanticType.Bool,t, s.expr);
	}
	
	private SemanticType propagate(Expr e, HashMap<String, SemanticType> environment, HashSet<String> generics) {
		SemanticType t;
		if(e instanceof Expr.Variable) {
			t = propagate((Expr.Variable)e,environment,generics);
		} else if(e instanceof Expr.Constant) {
			t = propagate((Expr.Constant)e,environment,generics);
		} else if(e instanceof Expr.Unary) {
			t = propagate((Expr.Unary)e,environment,generics);
		} else if(e instanceof Expr.Binary) {
			t = propagate((Expr.Binary)e,environment,generics);
		} else if(e instanceof Expr.Nary) {
			t = propagate((Expr.Nary)e,environment,generics);
		} else if(e instanceof Expr.Quantifier) {
			t = propagate((Expr.Quantifier)e,environment,generics);
		} else if(e instanceof Expr.FunCall) {
			t = propagate((Expr.FunCall)e,environment,generics);
		} else {
			internalFailure("unknown expression encountered (" + e + ")",
					filename, e);
			return null;
		}
		e.attributes().add(new TypeAttribute(t));
		return t;
	}
	
	private SemanticType propagate(Expr.Variable e, HashMap<String, SemanticType> environment, HashSet<String> generics) {
		SemanticType t = environment.get(e.name);
		if(t == null) {
			internalFailure("undeclared variable encountered (" + e + ")",
					filename, e);
		}
		return t;
	}
	
	private SemanticType propagate(Expr.Constant e, HashMap<String, SemanticType> environment, HashSet<String> generics) {
		return e.value.type();
	}

	private SemanticType propagate(Expr.Unary e, HashMap<String, SemanticType> environment, HashSet<String> generics) {
		SemanticType op_type = propagate(e.operand,environment,generics);
		
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
	
	private SemanticType propagate(Expr.Binary e, HashMap<String, SemanticType> environment, HashSet<String> generics) {
		SemanticType lhs_type = propagate(e.leftOperand,environment,generics);
		SemanticType rhs_type = propagate(e.rightOperand,environment,generics);
		
		switch(e.op) {
		case ADD:
		case SUB:
		case MUL:
		case DIV:
		case REM:
			checkIsSubtype(SemanticType.IntOrReal,lhs_type,e.leftOperand);
			checkIsSubtype(SemanticType.IntOrReal,rhs_type,e.rightOperand);
			return SemanticType.Or(lhs_type,rhs_type);
		case EQ:
		case NEQ:
			return SemanticType.Bool;
		case IMPLIES:
		case IFF:
			checkIsSubtype(SemanticType.Bool,lhs_type,e.leftOperand);
			checkIsSubtype(SemanticType.Bool,rhs_type,e.rightOperand);
			return SemanticType.Bool;
		case LT:
		case LTEQ:
		case GT:
		case GTEQ:
			checkIsSubtype(SemanticType.IntOrReal,lhs_type,e.leftOperand);
			checkIsSubtype(SemanticType.IntOrReal,rhs_type,e.rightOperand);
			return SemanticType.Bool;
		case IN: {
			checkIsSubtype(SemanticType.SetAny,rhs_type,e.rightOperand);
			SemanticType.Set s = (SemanticType.Set) rhs_type;
			checkIsSubtype(s.element(),lhs_type,e.leftOperand);
			return SemanticType.Bool;
		}
		case SUBSET:
		case SUBSETEQ:
		case SUPSET:
		case SUPSETEQ:
			checkIsSubtype(SemanticType.SetAny,lhs_type,e.leftOperand);
			checkIsSubtype(SemanticType.SetAny,rhs_type,e.rightOperand);
			return SemanticType.Bool;				
		}
		
		internalFailure("unknown binary expression encountered (" + e + ")",
				filename, e);
		return null; // deadcode
	}
	
	private SemanticType propagate(Expr.Nary e, HashMap<String, SemanticType> environment, HashSet<String> generics) {
		Expr[] e_operands = e.operands;
		SemanticType[] op_types = new SemanticType[e_operands.length];
		
		for(int i=0;i!=e_operands.length;++i) {
			op_types[i] = propagate(e_operands[i],environment,generics);
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
	
	private SemanticType propagate(Expr.Quantifier e, HashMap<String, SemanticType> environment, HashSet<String> generics) {
		environment = new HashMap<String,SemanticType>(environment);
		List<SyntacticType> unboundedVariables = e.unboundedVariables;
		
		for (int i = 0; i != unboundedVariables.size(); ++i) {
			SyntacticType p = unboundedVariables.get(i);
			if (p.name == null) {
				internalFailure("missing support for nested type names",
						filename, p);
			} 
			environment.put(p.name, convert(p, generics));
		}
		
		List<Pair<String,Expr>> boundedVariables = e.boundedVariables;
		
		for (int i = 0; i != boundedVariables.size(); ++i) {
			Pair<String,Expr> p = boundedVariables.get(i);
			SemanticType t = propagate(p.second(),environment,generics);
			checkIsSubtype(SemanticType.SetAny,t,p.second());
			SemanticType.Set st = (SemanticType.Set) t;
			environment.put(p.first(), st.element());
		}
		
		SemanticType r = propagate(e.operand,environment,generics);
		checkIsSubtype(SemanticType.Bool,r,e.operand);
		
		return SemanticType.Bool;
	}
	
	private SemanticType propagate(Expr.FunCall e, HashMap<String, SemanticType> environment, HashSet<String> generics) {
		SemanticType.Tuple funType = fnEnvironment.get(e.name);
		if (funType == null) {
			internalFailure("unknown function call encountered",
					filename, e);
		}
		SemanticType argument = propagate(e.operand,environment,generics);
		// TODO: generate generic binding here
		checkIsSubtype(funType.element(0),argument,e.operand);
		return funType.element(1);
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
	 *            --- Syntactic type to be converted.
	 * @param generics
	 *            --- Set of declared generic variables.
	 * @return
	 */
	private SemanticType convert(SyntacticType type, HashSet<String> generics) {
		
		if (type instanceof SyntacticType.Primitive) {
			SyntacticType.Primitive p = (SyntacticType.Primitive) type;
			return p.type;
		} else if (type instanceof SyntacticType.Var) {
			SyntacticType.Var p = (SyntacticType.Var) type;
			if(!generics.contains(p.var)) {
				internalFailure("undeclared generic variable encountered",
						filename, type);
				return null; // deadcode		
			}
			return SemanticType.Var(p.var);
		} else if(type instanceof SyntacticType.Not) {
			SyntacticType.Not t = (SyntacticType.Not) type;
			return SemanticType.Not(convert(t.element,generics));
		} else if(type instanceof SyntacticType.Set) {
			SyntacticType.Set t = (SyntacticType.Set) type;
			return SemanticType.Set(convert(t.element,generics));
		} else if(type instanceof SyntacticType.Or) {
			SyntacticType.Tuple t = (SyntacticType.Tuple) type;
			SemanticType[] types = new SemanticType[t.elements.size()];
			for(int i=0;i!=t.elements.size();++i) {
				types[i] = convert(t.elements.get(i),generics);
			}
			return SemanticType.Or(types);
		} else if(type instanceof SyntacticType.And) {
			SyntacticType.Tuple t = (SyntacticType.Tuple) type;
			SemanticType[] types = new SemanticType[t.elements.size()];
			for(int i=0;i!=t.elements.size();++i) {
				types[i] = convert(t.elements.get(i),generics);
			}
			return SemanticType.And(types);
		} else if(type instanceof SyntacticType.Tuple) {
			SyntacticType.Tuple t = (SyntacticType.Tuple) type;
			SemanticType[] types = new SemanticType[t.elements.size()];
			for(int i=0;i!=t.elements.size();++i) {
				types[i] = convert(t.elements.get(i),generics);
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
