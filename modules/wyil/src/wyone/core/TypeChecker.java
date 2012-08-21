package wyone.core;

import java.util.*;

import static wyone.core.Expr.*;
import static wyone.core.SpecFile.*;
import wyone.util.*;
import static wyone.util.SyntaxError.*;

public class TypeChecker {
	private String filename;

	// maps constructor names to their declared types.
	private final HashMap<String, Type.Term> terms = new HashMap<String, Type.Term>();

	// globals contains the list of global variables
	// private final HashMap<String,Type> globals = new HashMap();

	public void check(SpecFile spec) {
		filename = spec.filename;

		for (Decl d : spec.declarations) {
			if (d instanceof ClassDecl) {
				ClassDecl cd = (ClassDecl) d;
				terms.put(cd.name, Type.T_TERM(cd.name, Type.T_VOID));
			} else if (d instanceof TermDecl) {
				TermDecl td = (TermDecl) d;
				terms.put(td.type.name, td.type);
			}
		}

		for (Decl d : spec.declarations) {
			if (d instanceof RewriteDecl) {
				check((RewriteDecl) d);
			}
		}
	}

	public void check(RewriteDecl rd) {
		HashMap<String, Type> environment = new HashMap<String, Type>();
		resolve(rd.pattern, environment);

		for (RuleDecl rule : rd.rules) {
			check(rule, environment);
		}
	}

	protected Type resolve(Pattern pattern, HashMap<String, Type> environment) {
		Type type;
		if (pattern instanceof Pattern.Term) {
			Pattern.Term pt = (Pattern.Term) pattern;
			type = resolve(pt, environment);
		} else if (pattern instanceof Pattern.Compound) {
			Pattern.Compound pt = (Pattern.Compound) pattern;
			type = resolve(pt, environment);
		} else {
			Pattern.Leaf l = (Pattern.Leaf) pattern;
			type = l.type;
			pattern.attributes().add(new Attribute.TypeAttr(type));
		}
		return type;
	}

	protected Type.Term resolve(Pattern.Term pt,
			HashMap<String, Type> environment) {
		Type.Term declared = terms.get(pt.name);
		if (declared == null) {
			syntaxError("unknown term encountered", filename, pt);
		}

		// FIXME: intersect parameter types with actual types
		Type.Term type = Type.T_TERM(pt.name, resolve(pt.data,environment));

		if (!Type.isSubtype(type, declared)) {
			syntaxError("invalid usage of term", filename, pt);
		}
		
		pt.attributes().add(new Attribute.TypeAttr(type));
		
		if(pt.var != null) {
			environment.put(pt.var, type.data);
		}
		
		return type;
	}

	protected Type resolve(Pattern.Compound pt,
			HashMap<String, Type> environment) {

		Type[] ps = new Type[pt.elements.size()];
		for (int i = 0; i != pt.elements.size(); ++i) {
			Pair<Pattern, String> p = pt.elements.get(i);
			Pattern pattern = p.first();
			ps[i] = resolve(pattern, environment);
			String var = p.second();
			if (var != null) {
				if (pt.unbounded && (i + 1) == pt.elements.size()) {
					environment.put(var, Type.T_COMPOUND(pt.kind, true, ps[i]));
				} else {
					environment.put(var, ps[i]);
				}
			}
		}

		Type.Compound type = Type.T_COMPOUND(pt.kind, pt.unbounded, ps);

		pt.attributes().add(new Attribute.TypeAttr(type));

		return type;
	}

	public void check(RuleDecl rule, HashMap<String, Type> environment) {
		for (Pair<String, Expr> let : rule.lets) {
			environment.put(let.first(), resolve(let.second(), environment));
		}
		if (rule.condition != null) {
			resolve(rule.condition, environment);
		}
		resolve(rule.result, environment);
	}

	protected Type resolve(Expr e, HashMap<String, Type> environment) {
		Type type = null;
		try {
			if (e instanceof Constant) {
				type = resolve((Constant) e, environment);
			} else if (e instanceof Variable) {
				type = resolve((Variable) e, environment);
			} else if (e instanceof UnOp) {
				type = resolve((UnOp) e, environment);
			} else if (e instanceof Constructor) {
				type = resolve((Constructor) e, environment);
			} else if (e instanceof TypeConst) {
				type = resolve((TypeConst) e, environment);
			} else if (e instanceof BinOp) {
				type = resolve((BinOp) e, environment);
			} else if (e instanceof NaryOp) {
				type = resolve((NaryOp) e, environment);
			} else if (e instanceof Comprehension) {
				type = resolve((Comprehension) e, environment);
			} else if (e instanceof ListAccess) {
				type = resolve((ListAccess) e, environment);
			} else if (e instanceof TermAccess) {
				type = resolve((TermAccess) e, environment);
			} else {
				syntaxError("unknown expression encountered", filename, e);
			}
		} catch (SyntaxError se) {
			throw se;
		} catch (Exception ex) {
			syntaxError("internal failure", filename, e, ex);
		}
		e.attributes().add(new Attribute.TypeAttr(type));
		return type;
	}

	protected Type resolve(Constant c, HashMap<String, Type> environment) {
		Object v = c.value;
		if (v instanceof Boolean) {
			return Type.T_BOOL;
		} else if (v instanceof Integer) {
			return Type.T_INT;
		} else if (v instanceof Double) {
			return Type.T_REAL;
		} else if (v instanceof String) {
			return Type.T_STRING;
		}
		syntaxError("unknown constant encountered (" + v.getClass().getName()
				+ ")", filename, c);
		return null;
	}

	protected Type resolve(Variable v, HashMap<String, Type> environment) {
		Type v_t = environment.get(v.var);
		if (v_t != null) {
			return v_t;
		} 
		Type.Term type = terms.get(v.var);
		if(type != null && type.data == Type.T_VOID) {
			v.isConstructor = true;
			return type;
		}
		syntaxError("variable not defined", filename, v);
		return null;
	}

	protected Type resolve(Constructor ivk, HashMap<String, Type> environment) {
	
		Type arg_t = resolve(ivk.argument, environment);			

		// Second, we assume it's not a local variable and look outside the
		// scope.

		// TODO: type check parameter argument

		Type.Term type = terms.get(ivk.name);
		if (type == null) {
			syntaxError("function not declared", filename, ivk);
		}
		return type;
	}

	protected Type resolve(TypeConst tc, HashMap<String, Type> environment) {
		return tc.type;
	}

	protected Type resolve(UnOp uop, HashMap<String, Type> environment) {
		if (uop.op == UOp.NOT) {
			// this is necessary to prevent type inference in the presence of
			// invert type tests. e.g. <code>!x~=Var(*) && y~=Var(*)</code>
			// should not update the type of x.
			environment = new HashMap<String, Type>(environment);
		}
		Type t = resolve(uop.mhs, environment);
		switch (uop.op) {
		case LENGTHOF:
			checkSubtype(Type.T_COMPOUNDANY, t, uop.mhs);
			return Type.T_INT;
		case NEG:
			checkSubtype(Type.T_REAL, t, uop.mhs);
			return t;
		case NOT:
			checkSubtype(Type.T_BOOL, t, uop.mhs);
			return t;
		}
		syntaxError("unknown unary expression encountered", filename, uop);
		return null;
	}

	protected Type resolve(BinOp bop, HashMap<String, Type> environment) {

		if (bop.op == BOp.OR) {
			// TODO: the following is needed because of type inference.
			// unfortunately, it is still a bit of a hack.
			environment = new HashMap<String, Type>(environment);
		}

		Type lhs_t = resolve(bop.lhs, environment);
		Type rhs_t = resolve(bop.rhs, environment);

		// FIXME: really need to add coercions somehow

		switch (bop.op) {
		case ADD: {
			checkSubtype(Type.T_REAL, lhs_t, bop.lhs);
			checkSubtype(Type.T_REAL, rhs_t, bop.rhs);
			return Type.leastUpperBound(lhs_t, rhs_t);
		}
		case DIV:
		case MUL: {
			checkSubtype(Type.T_REAL, lhs_t, bop.lhs);
			checkSubtype(Type.T_REAL, rhs_t, bop.rhs);
			return Type.leastUpperBound(lhs_t, rhs_t);
		}
		case EQ:
		case NEQ: {
			return Type.T_BOOL;
		}
		case LT:
		case LTEQ:
		case GT:
		case GTEQ: {
			checkSubtype(Type.T_REAL, lhs_t, bop.lhs);
			checkSubtype(Type.T_REAL, rhs_t, bop.rhs);
			return Type.T_BOOL;
		}
		case AND:
		case OR: {
			checkSubtype(Type.T_BOOL, lhs_t, bop.lhs);
			checkSubtype(Type.T_BOOL, rhs_t, bop.rhs);
			return Type.T_BOOL;
		}
		case APPEND: {
			if(lhs_t instanceof Type.Compound && rhs_t instanceof Type.Compound) {				
				return Type.leastUpperBound(lhs_t, rhs_t);					
			} else if(rhs_t instanceof Type.Compound) {
				// right append
				Type.Compound rhs_tc = (Type.Compound) rhs_t; 
				return Type.T_COMPOUND(rhs_tc.kind,rhs_tc.unbounded,append(lhs_t,rhs_tc.elements));
			} else {
				// left append
				Type.Compound lhs_tc = (Type.Compound) rhs_t; 
				if(!lhs_tc.unbounded) {
					return Type.T_COMPOUND(lhs_tc.kind,false,append(lhs_tc.elements,lhs_t));
				} else {
					int length = lhs_tc.elements.length;
					Type[] nelements = Arrays.copyOf(lhs_tc.elements,length);
					length--;
					nelements[length] = Type.leastUpperBound(rhs_t, nelements[length]);
					return Type.T_COMPOUND(lhs_tc.kind,true,nelements);
				}
			}			
		}
		case TYPEEQ: {
			checkSubtype(lhs_t, rhs_t, bop.lhs);
			if (bop.lhs instanceof Variable) {
				// type inference
				Variable v = (Variable) bop.lhs;
				// FIXME: should compute glb here
				environment.put(v.var, rhs_t);
			}
			return Type.T_BOOL;
		}
		}

		syntaxError("unknown binary expression encountered", filename, bop);
		return null;
	}

	protected Type resolve(NaryOp nop, HashMap<String, Type> environment) {
		if (nop.op == NOp.SUBLIST) {
			Expr src = nop.arguments.get(0);
			Expr start = nop.arguments.get(1);
			Expr end = nop.arguments.get(2);
			Type src_t = resolve(src, environment);
			Type start_t = resolve(start, environment);
			Type end_t = resolve(end, environment);
			checkSubtype(Type.T_COMPOUNDANY, src_t, src);
			checkSubtype(Type.T_INT, start_t, start);
			checkSubtype(Type.T_INT, end_t, end);
			return src_t;
		} else {
			// Must be a set or list generator
			ArrayList<Type> types = new ArrayList<Type>();
			for (Expr e : nop.arguments) {
				types.add(resolve(e, environment));
			}

			return Type.T_COMPOUND(Type.Compound.Kind.LIST, false, types);
		}
	}

	protected Type resolve(Comprehension comp, HashMap<String, Type> environment) {
		// HashMap<String,Type> nenv = new HashMap(environment);
		// for(Pair<String,Expr> src : comp.sources) {
		// if(environment.containsKey(src.first())) {
		// syntaxError("variable " + src.first() +
		// " already declared",filename,comp);
		// }
		// Type t = resolve(src.second(),nenv);
		// checkSubtype(Type.T_COMPOUNDANY,t, src.second());
		// Type.SetList sl = (Type.SetList) t;
		// nenv.put(src.first(), sl.element());
		// }
		// if(comp.condition != null) {
		// resolve(comp.condition,nenv);
		// }
		// if(comp.cop == COp.LISTCOMP) {
		// Type r_t = resolve(comp.value,nenv);
		// return Type.T_LIST(r_t);
		// }
		return Type.T_BOOL;
	}

	protected Type resolve(ListAccess ra, HashMap<String, Type> environment) {
		Type src_t = resolve(ra.src, environment);
		Type idx_t = resolve(ra.index, environment);
		if (!(src_t instanceof Type.Compound)) {
			syntaxError("expected list of term type, got " + src_t, filename,
					ra.src);
		}

		// TODO: we could do better if we knew that idx was a constant.

		checkSubtype(Type.T_INT, idx_t, ra.index);
		Type.Compound rt = (Type.Compound) src_t;
		return rt.element();
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
