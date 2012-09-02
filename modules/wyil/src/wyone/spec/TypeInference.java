package wyone.spec;

import java.util.*;

import wyone.core.Attribute;
import wyone.core.Type;
import wyone.spec.*;
import wyone.util.*;
import static wyone.util.SyntaxError.*;

public class TypeInference {
	private String filename;

	// maps constructor names to their declared types.
	private final HashMap<String, Type.Term> terms = new HashMap<String, Type.Term>();

	// globals contains the list of global variables
	// private final HashMap<String,Type> globals = new HashMap();

	public void check(SpecFile spec) {
		filename = spec.filename;

		for (SpecFile.Decl d : spec.declarations) {
			if (d instanceof SpecFile.ClassDecl) {
				SpecFile.ClassDecl cd = (SpecFile.ClassDecl) d;
				terms.put(cd.name, Type.T_TERM(cd.name, null));
			} else if (d instanceof SpecFile.TermDecl) {
				SpecFile.TermDecl td = (SpecFile.TermDecl) d;
				terms.put(td.type.name, td.type);
			}
		}

		for (SpecFile.Decl d : spec.declarations) {
			if (d instanceof SpecFile.RewriteDecl) {
				check((SpecFile.RewriteDecl) d);
			}
		}
	}

	public void check(SpecFile.RewriteDecl rd) {
		Pattern pattern = rd.pattern;
		Type.Ref thisType = pattern.type();
		for(SpecFile.RuleDecl rule : rd.rules) {
			check(rule,thisType);
		}
	}
	
	public void check(SpecFile.RuleDecl rd, Type.Ref thisType) {
		HashMap<String,Type> environment = new HashMap<String,Type>();
		
		for(Pair<String,Expr> let : rd.lets) {
			Type expr = resolve(let.second(),environment);
			environment.put(let.first(), expr);
		}
	}

	protected Type resolve(Expr code, HashMap<String,Type> environment) {
		try {
			Type result;
			if (code instanceof Expr.Constant) {
				result = resolve((Expr.Constant) code, environment);
			} else if (code instanceof Expr.UnOp) {
				result = resolve((Expr.UnOp) code, environment);
			} else if (code instanceof Expr.BinOp) {
				result = resolve((Expr.BinOp) code, environment);
			} else if (code instanceof Expr.NaryOp) {
				result = resolve((Expr.NaryOp) code, environment);
			} else if (code instanceof Expr.Constructor) {
				result = resolve((Expr.Constructor) code, environment);
			} else {
				syntaxError("unknown code encountered", filename, code);
				return null;
			}
			code.attributes().add(new Attribute.Type(result));
			return result; // dead code
		} catch (SyntaxError se) {
			throw se;
		} catch (Exception ex) {
			syntaxError("internal failure", filename, code, ex);
		}
		return null; // dead code
	}

	protected Type resolve(Expr.Constant code, HashMap<String,Type> environment) {
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

	protected Type resolve(Expr.Constructor code, HashMap<String,Type> environment) {
	
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

	protected Type resolve(Expr.UnOp uop, HashMap<String,Type> environment) {		
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

	protected Type resolve(Expr.BinOp bop, HashMap<String,Type> environment) {

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
