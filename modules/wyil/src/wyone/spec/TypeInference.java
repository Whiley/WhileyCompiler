package wyone.spec;

import java.util.*;

import wyone.core.Attribute;
import wyone.core.Type;
import wyone.util.*;
import static wyone.util.SyntaxError.*;

public class TypeInference {
	private String filename;

	// maps constructor names to their declared types.
	private final HashMap<String, Type.Term> terms = new HashMap<String, Type.Term>();

	// globals contains the list of global variables
	// private final HashMap<String,Type> globals = new HashMap();

	public void infer(SpecFile spec) {
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
				infer((SpecFile.RewriteDecl) d);
			}
		}
	}

	public void infer(SpecFile.RewriteDecl rd) {
		
		HashMap<String,Type> environment = new HashMap<String,Type>();
		
		infer(rd.pattern,environment);
				
		for(SpecFile.RuleDecl rule : rd.rules) {
			infer(rule,environment);
		}
	}
	
	public Type.Ref infer(Pattern pattern, HashMap<String,Type> environment) {
		if(pattern instanceof Pattern.Leaf) {
			Pattern.Leaf p = (Pattern.Leaf) pattern; 
			return Type.T_REF(p.type);
		} else if(pattern instanceof Pattern.Term) {
			Pattern.Term p = (Pattern.Term) pattern;
			Type.Ref d = null;
			if(p.data != null) {
				d = infer(p.data,environment);
			}
			if(p.variable != null) {
				environment.put(p.variable, d);
			}
			return Type.T_REF(Type.T_TERM(p.name, d));
		} else {
			Pattern.Compound p = (Pattern.Compound) pattern;
			ArrayList<Type> types = new ArrayList<Type>();
			for (Pair<Pattern, String> ps : p.elements) {
				String var = ps.second();
				Pattern pat = ps.first();
				Type type = infer(pat,environment);				
				types.add(type);
				if(var != null) {
					environment.put(var,type);
				}
			}
			return Type.T_REF(Type.T_COMPOUND(p.kind, p.unbounded, types));
		}
	}
	
	public void infer(SpecFile.RuleDecl rd, HashMap<String,Type> environment) {
		 environment = new HashMap<String,Type>(environment);
		
		for(Pair<String,Expr> let : rd.lets) {
			Type expr = resolve(let.second(),environment);
			environment.put(let.first(), expr);
		}
		
		if(rd.condition != null) {
			Type condition = resolve(rd.condition,environment);
			checkSubtype(Type.T_BOOL,condition,rd.condition);
		}
		
		Type result = resolve(rd.result,environment);

		// TODO: check result is a ref?
	}

	protected Type resolve(Expr expr, HashMap<String,Type> environment) {
		try {
			Type result;
			if (expr instanceof Expr.Constant) {
				result = resolve((Expr.Constant) expr, environment);
			} else if (expr instanceof Expr.UnOp) {
				result = resolve((Expr.UnOp) expr, environment);
			} else if (expr instanceof Expr.BinOp) {
				result = resolve((Expr.BinOp) expr, environment);
			} else if (expr instanceof Expr.NaryOp) {
				result = resolve((Expr.NaryOp) expr, environment);
			} else if (expr instanceof Expr.Constructor) {
				result = resolve((Expr.Constructor) expr, environment);
			} else if (expr instanceof Expr.Variable) {
				result = resolve((Expr.Variable) expr, environment);
			} else {
				syntaxError("unknown code encountered", filename, expr);
				return null;
			}
			expr.attributes().add(new Attribute.Type(result));
			return result; // dead code
		} catch (SyntaxError se) {
			throw se;
		} catch (Exception ex) {
			syntaxError("internal failure", filename, expr, ex);
		}
		return null; // dead code
	}

	protected Type resolve(Expr.Constant expr, HashMap<String, Type> environment) {
		Object v = expr.value;
		if (v instanceof Boolean) {
			return Type.T_BOOL;
		} else if (v instanceof Integer) {
			return Type.T_INT;
		} else if (v instanceof Double) {
			return Type.T_REAL;
		} else if (v instanceof String) {
			return Type.T_STRING;
		} else {
			syntaxError("unknown constant encountered ("
					+ v.getClass().getName() + ")", filename, expr);
			return null; // deadcode
		}
	}

	protected Type resolve(Expr.Constructor code, HashMap<String,Type> environment) {
	
		if(code.argument != null) {
			Type arg_t = environment.get(code.argument);
			
			// TODO: type check parameter argument
		}

		Type.Term type = terms.get(code.name);
		if (type == null) {
			syntaxError("function not declared", filename, code);
		}
		
		return type;
	}

	protected Type resolve(Expr.UnOp uop, HashMap<String,Type> environment) {		
		Type t = environment.get(uop.mhs);
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
		
		return t;
	}

	protected Type resolve(Expr.BinOp bop, HashMap<String,Type> environment) {

		Type lhs_t = environment.get(bop.lhs);
		Type rhs_t = environment.get(bop.rhs);
		Type result;
		
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
			return null; // dead-code
		}
		return result;
	}
	
	protected Type resolve(Expr.Variable code, HashMap<String, Type> environment) {
		Type result = environment.get(code.var);
		if (result == null) {
			Type.Term tmp = terms.get(code.var);
			if (tmp == null || tmp.data != null) {
				syntaxError("unknown variable encountered", filename, code);
			}
			return tmp;
		} else {
			return result;
		}
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
