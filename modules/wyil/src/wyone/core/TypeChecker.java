package wyone.core;

import java.util.*;

import static wyone.core.WyoneFile.*;
import wyone.util.*;
import static wyone.util.SyntaxError.*;

public class TypeChecker {
	private String filename;

	// maps constructor names to their declared types.
	private final HashMap<String, Type.Term> terms = new HashMap<String, Type.Term>();

	// globals contains the list of global variables
	// private final HashMap<String,Type> globals = new HashMap();

	public void check(WyoneFile spec) {
		filename = spec.filename;

		for (Decl d : spec.declarations) {
			if (d instanceof ClassDecl) {
				ClassDecl cd = (ClassDecl) d;
				terms.put(cd.name, Type.T_TERM(cd.name, null));
			} else if (d instanceof TermDecl) {
				TermDecl td = (TermDecl) d;
				terms.put(td.type.name, td.type);
			}
		}

		for (Decl d : spec.declarations) {
			if (d instanceof FunDecl) {
				check((FunDecl) d);
			}
		}
	}

	public void check(FunDecl rd) {
		rd.types.set(0, rd.type.param);
		
		for (Code code : rd.codes) {
			resolve(code, rd.types);
		}
	}

	protected void resolve(Code code, ArrayList<Type> fun) {
		try {
			if(code instanceof Code.Assign) {
				resolve((Code.Assign) code, fun);
			} else if (code instanceof Code.Constant) {
				resolve((Code.Constant) code, fun);
			} else if (code instanceof Code.TermContents) {
				resolve((Code.TermContents) code, fun);
			} else if (code instanceof Code.Deref) {
				resolve((Code.Deref) code, fun);
			} else if (code instanceof Code.Invoke) {
				resolve((Code.Invoke) code, fun);
			} else if (code instanceof Code.If) {
				resolve((Code.If) code, fun);
			} else if (code instanceof Code.IfIs) {
				resolve((Code.IfIs) code, fun);
			} else if (code instanceof Code.IndexOf) {
				resolve((Code.IndexOf) code, fun);
			} else if (code instanceof Code.UnOp) {
				resolve((Code.UnOp) code, fun);
			} else if (code instanceof Code.BinOp) {
				resolve((Code.BinOp) code, fun);
			} else if (code instanceof Code.NaryOp) {
				resolve((Code.NaryOp) code, fun);
			} else if (code instanceof Code.Rewrite) {
				resolve((Code.Rewrite) code, fun);
			} else if (code instanceof Code.Return) {
				resolve((Code.Return) code, fun);
			} else if (code instanceof Code.Constructor) {
				resolve((Code.Constructor) code, fun);
			} else {
				syntaxError("unknown code encountered", filename, code);
			}
		} catch (SyntaxError se) {
			throw se;
		} catch (Exception ex) {
			syntaxError("internal failure", filename, code, ex);
		}
	}

	protected void resolve(Code.Constant code, ArrayList<Type> environment) {
		Object v = code.value;
		Type result;
		if (v instanceof Boolean) {
			result = Type.T_BOOL;
		} else if (v instanceof Integer) {
			result = Type.T_INT;
		} else if (v instanceof Double) {
			result = Type.T_REAL;
		} else if (v instanceof String) {
			result = Type.T_STRING;
		} else {
			syntaxError("unknown constant encountered ("
					+ v.getClass().getName() + ")", filename, code);
			return; // deadcode
		}
		environment.set(code.target, result);
	}

	protected void resolve(Code.Deref code, ArrayList<Type> environment) {
		Type type = environment.get(code.operand);
		checkSubtype(Type.T_REFANY,type,code);
		Type contents = ((Type.Ref)type).element;
		environment.set(code.target,contents);
	}
	
	protected void resolve(Code.Invoke code, ArrayList<Type> environment) {
		// FIXME: type check the operands!!
		environment.set(code.target,code.type.ret);
	}
	
	
	protected void resolve(Code.If code, ArrayList<Type> environment) {
		Type type = environment.get(code.operand);
		checkSubtype(Type.T_BOOL,type,code);
		for(Code c : code.trueBranch) {
			resolve(c,environment);
		}
		for(Code c : code.falseBranch) {
			resolve(c,environment);
		}
	}
	
	protected void resolve(Code.IfIs code, ArrayList<Type> environment) {
		Type type = environment.get(code.operand);
		for(Code c : code.trueBranch) {
			resolve(c,environment);
		}
		for(Code c : code.falseBranch) {
			resolve(c,environment);
		}
	}
	
	protected void resolve(Code.IndexOf code, ArrayList<Type> environment) {
		Type source = environment.get(code.source);
		Type index = environment.get(code.index);
		checkSubtype(Type.T_COMPOUNDANY, source, code);
		checkSubtype(Type.T_INT, index, code);		
		environment.set(code.target, ((Type.Compound) source).element());
	}
	
	protected void resolve(Code.Assign code, ArrayList<Type> environment) {
		Type type = environment.get(code.operand);
		environment.set(code.target,type);
	}
	
	protected void resolve(Code.Constructor code, ArrayList<Type> environment) {
	
		if(code.operand != -1) {
			Type arg_t = environment.get(code.operand);			
			// TODO: type check parameter argument!
		}

		Type.Term type = terms.get(code.name);
		if (type == null) {
			syntaxError("function not declared", filename, code);
		}
		environment.set(code.target,type);
	}

	protected void resolve(Code.UnOp uop, ArrayList<Type> environment) {		
		Type t = environment.get(uop.operand);
		switch (uop.op) {
		case LENGTHOF:
			checkSubtype(Type.T_COMPOUNDANY, t, uop);
			t = Type.T_INT;
			break;
		case NEG:
			checkSubtype(Type.T_REAL, t, uop);
			break;
		case NOT:
			checkSubtype(Type.T_BOOL, t, uop);
			break;
		default:
			syntaxError("unknown unary expression encountered", filename, uop);
		}
		environment.set(uop.target, t);
	}

	protected void resolve(Code.BinOp bop, ArrayList<Type> environment) {

		Type lhs_t = environment.get(bop.lhs);
		Type rhs_t = environment.get(bop.rhs);
		Type result;
		// FIXME: really need to add coercions somehow

		switch (bop.op) {
		case ADD: {
			checkSubtype(Type.T_REAL, lhs_t, bop);
			checkSubtype(Type.T_REAL, rhs_t, bop);
			result = Type.leastUpperBound(lhs_t, rhs_t);
			break;
		}
		case DIV:
		case MUL: {
			checkSubtype(Type.T_REAL, lhs_t, bop);
			checkSubtype(Type.T_REAL, rhs_t, bop);
			result = Type.leastUpperBound(lhs_t, rhs_t);
			break;
		}
		case EQ:
		case NEQ: {
			result = Type.T_BOOL;
			break;
		}
		case LT:
		case LTEQ:
		case GT:
		case GTEQ: {
			checkSubtype(Type.T_REAL, lhs_t, bop);
			checkSubtype(Type.T_REAL, rhs_t, bop);
			result = Type.T_BOOL;
			break;
		}
		case AND:
		case OR: {
			checkSubtype(Type.T_BOOL, lhs_t, bop);
			checkSubtype(Type.T_BOOL, rhs_t, bop);
			result = Type.T_BOOL;
			break;
		}
		case APPEND: {
			if (lhs_t instanceof Type.Compound
					&& rhs_t instanceof Type.Compound) {
				result = Type.leastUpperBound(lhs_t, rhs_t);
			} else if (rhs_t instanceof Type.Compound) {
				// right append
				Type.Compound rhs_tc = (Type.Compound) rhs_t;
				result = Type.T_COMPOUND(rhs_tc.kind, rhs_tc.unbounded,
						append(lhs_t, rhs_tc.elements));
			} else {
				// left append
				Type.Compound lhs_tc = (Type.Compound) rhs_t;
				if (!lhs_tc.unbounded) {
					result = Type.T_COMPOUND(lhs_tc.kind, false,
							append(lhs_tc.elements, lhs_t));

				} else {
					int length = lhs_tc.elements.length;
					Type[] nelements = Arrays.copyOf(lhs_tc.elements, length);
					length--;
					nelements[length] = Type.leastUpperBound(rhs_t,
							nelements[length]);
					result = Type.T_COMPOUND(lhs_tc.kind, true, nelements);
				}
			}		
			break;
		}
		case TYPEEQ: {
			checkSubtype(lhs_t, rhs_t, bop);
			result = Type.T_BOOL;
			break;
		}
		default:
			syntaxError("unknown binary expression encountered", filename, bop);
			return; // dead-code
		}
		environment.set(bop.target,result);
	}

	public void resolve(Code.TermContents code, ArrayList<Type> environment) {
		Type type = environment.get(code.operand);
		if (!(type instanceof Type.Term)) {
			syntaxError("expecting term type, got type " + type, filename, code);
		}
		Type contents = ((Type.Term) type).data;
		environment.set(code.target, contents);
	}
	
	public void resolve(Code.Rewrite code, ArrayList<Type> environment) {
		//Type type = environment.get(code.operand);
		//checkSubtype(Type.T_REFANY,type,code);
		environment.set(code.target,Type.T_BOOL);
	}
	
	public void resolve(Code.Return code, ArrayList<Type> environment) {
		// TODO: should do something here!		
	}
	
	public Type[] append(Type head, Type[] tail) {
		Type[] r = new Type[tail.length+1];
		System.arraycopy(tail,0,r,1,tail.length);
		r[0] = head;
		return r;
	}
	
	public Type[] append(Type[] head, Type tail) {
		Type[] r = new Type[head.length+1];
		System.arraycopy(head,0,r,0,head.length);
		r[head.length] = tail;
		return r;
	}
	
	/**
	 * Check whether t1 :> t2; that is, whether t2 is a subtype of t1.
	 * 
	 * @param t1
	 * @param t2
	 * @param elem
	 */
	public void checkSubtype(Type t1, Type t2, SyntacticElement elem) {
		if (!Type.isSubtype(t1, t2)) {
			syntaxError("expecting type " + t1 + ", got type " + t2, filename,
					elem);
		}
	}
}
