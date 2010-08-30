package wyil.stages;

import static wyil.util.SyntaxError.syntaxError;

import java.util.*;

import wyil.ModuleLoader;
import wyil.jvm.rt.BigRational;
import wyil.lang.*;
import wyil.lang.Code.*;
import wyil.lang.CExpr.*;
import wyil.util.*;
import wyil.util.dfa.*;
import wyone.core.*;
import wyone.theory.congruence.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;
import wyone.theory.list.*;
import wyone.theory.set.*;
import wyone.theory.tuple.*;
import wyone.theory.quantifier.*;
import wyone.theory.type.*;

public class ConstraintPropagation extends AbstractPropagation<WFormula> {	
	private int timeout;
	
	public ConstraintPropagation(ModuleLoader loader, int timeout) {
		super(loader);
		this.timeout = timeout;	
	}
	
	public WFormula initialStore() {
		if (methodCase.precondition() != null) {
			Pair<Block, WFormula> precondition = propagate(methodCase
					.precondition(), WBool.TRUE);
			return precondition.second();
		} else {
			return WBool.TRUE;
		}
	}
		
	protected Pair<Stmt,WFormula> propagate(Stmt stmt, WFormula store) {
		// First, add on the precondition attribute
		ArrayList<Attribute> attributes = new ArrayList<Attribute>(stmt.attributes());
		attributes.add(new Attribute.PreCondition(store));
		stmt = new Stmt(stmt.code,attributes);
		
		// second, check for assignment statements
		if(stmt.code instanceof Assign) {
			return new Pair<Stmt, WFormula>(stmt, infer(
					(Code.Assign) stmt.code, stmt, store));
		} else {					
			return new Pair<Stmt,WFormula>(stmt,store);
		}
	}
	
	private static int index = 0; // probably could be better
	protected WFormula infer(Code.Assign code, SyntacticElement elem,
			WFormula precondition) {
	
		if(code.lhs == null) {
			return precondition;
		}
		
		Pair<WExpr, WFormula> lhs_p = infer(code.lhs, elem);
		Pair<WExpr, WFormula> rhs_p = infer(code.rhs, elem);

		WExpr rhs = rhs_p.first();
		WExpr lhs = lhs_p.first();

		LVar lvar = CExpr.extractLVar(code.lhs);

		// Create shadows
		HashMap<WExpr, WExpr> binding = new HashMap<WExpr, WExpr>();
		binding.put(new WVariable(lvar.name()), new WVariable(lvar.name() + "$"
				+ index));

		rhs = rhs.substitute(binding);
		WFormula postcondition = precondition.substitute(binding);

		// finally, put it altogether
		return WFormulas.and(postcondition, new WEquality(true, lhs, rhs),
				assignCondition(code.lhs, binding, elem), lhs_p.second(), rhs_p
						.second());
	}
		
	protected WFormula assignCondition(LVal lval,
			HashMap<WExpr, WExpr> binding, SyntacticElement elem) {
		
		// the purpose of this method is to generate a condition which
		// identifies that everything in the lhs base variable is identical to
		// that in the shadowVariable, except for the particular element being
		// updated.
		List<CExpr> exprs = flattern(lval,elem);
		LVar variable = (LVar) exprs.get(0);
		WFormula constraint = WTypes.subtypeOf(new WVariable(variable.name()),convert(variable.type()));				
		for(int i=1;i!=exprs.size();++i) {
			CExpr access = exprs.get(i);			
			if(access instanceof TupleAccess){				
				TupleAccess ta = (TupleAccess) lval;
				Type.Tuple tt = Type.effectiveTupleType(ta.lhs.type());
				Pair<WExpr, WFormula> src = infer(ta.lhs, elem);
				WExpr nsrc = src.first().substitute(binding);
				for(String field : tt.types.keySet()) {
					if(!field.equals(ta.field)) {
						WTupleAccess o = new WTupleAccess(src.first(),field);
						WTupleAccess n = new WTupleAccess(nsrc,field);
						constraint = WFormulas.and(constraint,new WEquality(true,o,n));
					}
				}
				constraint = WFormulas.and(constraint,src.second());
			} else if(access instanceof ListAccess) {
				ListAccess la = (ListAccess) lval;
				Pair<WExpr, WFormula> src = infer(la.src,elem);
				Pair<WExpr, WFormula> index = infer(la.index,elem);
				WExpr nsrc = src.first().substitute(binding);
				WVariable var = WVariable.freshVar();
				WExpr o = new WListAccess(src.first(),var);
				WExpr n = new WListAccess(nsrc,var);
				HashMap<WVariable,WExpr> srcs = new HashMap<WVariable,WExpr>();
				srcs.put(var, src.first());
				WFormula body = WFormulas.or(new WEquality(true, index.first(),
						var), new WEquality(true, o, n));
				constraint = WFormulas.and(constraint, new WBoundedForall(true,
						srcs, body),src.second(),index.second());
			}
		}
		
		return constraint;
	}
	
	protected List<CExpr> flattern(CExpr lval, SyntacticElement elem) {
		if (lval instanceof LVar) {
			ArrayList<CExpr> r = new ArrayList<CExpr>();
			r.add(lval);
			return r;
		} else if (lval instanceof ListAccess) {
			ListAccess la = (ListAccess) lval;
			List<CExpr> f = flattern(la.src, elem);
			f.add(lval);
			return f;
		} else if (lval instanceof TupleAccess) {
			TupleAccess la = (TupleAccess) lval;
			List<CExpr> f = flattern(la.lhs, elem);
			f.add(lval);
			return f;
		}

		syntaxError("unknown lval encountered: " + lval, filename, elem);
		return null;
	}

	protected Pair<Block, WFormula> propagate(Code.Start start, Code.End end,
			Block body, Stmt stmt, WFormula store) {
		Block nblock = new Block();
		nblock.add(start);
		if(start instanceof Code.Check) {
			Pair<Block,WFormula> r = propagate(body,store);
			body = r.first();
			store = r.second();
		} else {
			Pair<Block,WFormula> r = propagate(body,store);
			body = r.first();
			store = r.second();			
		}
		nblock.addAll(body);
		nblock.add(end);
		return new Pair<Block, WFormula>(nblock, store);
	}
		
	/*
	public void forall() {
		// Now, propagate through the body
		HashMap<String,WFormula> sets = new HashMap<String,WFormula>();
		Pair<Block,WFormula> r = transform(body,precondition,sets);										
		// FIXME: must simplify formula first to extract stuff
		// not-involving the quantified variable.
		precondition = new WBoundedForall(true, new WVariable(
				fall.variable.name()), src.first(), r.second());
		for(Map.Entry<String,WFormula> set : sets.entrySet()) {						
			WFormula c = new WBoundedForall(true, new WVariable(
					fall.variable.name()), src.first(), set.getValue());
			merge(set.getKey(),c,flowsets);
		}

	}
	*/
	
	protected Triple<Stmt, WFormula, WFormula> propagate(Code.IfGoto code,
			Stmt elem, WFormula precondition) {

		Pair<WExpr, WFormula> lhs_p = infer(code.lhs, elem);
		Pair<WExpr, WFormula> rhs_p = infer(code.rhs, elem);
		precondition = WFormulas.and(precondition, lhs_p.second(), rhs_p
				.second());
		WFormula condition = null;
		switch (code.op) {
		case EQ:
			condition = new WEquality(true, lhs_p.first(), rhs_p.first());
			break;
		case NEQ:
			condition = new WEquality(false, lhs_p.first(), rhs_p.first());
			break;
		case LT:
			condition = WNumerics.lessThan(lhs_p.first(), rhs_p.first());
			break;
		case LTEQ:
			condition = WNumerics.lessThanEq(lhs_p.first(), rhs_p.first());
			break;
		case GT:
			condition = WNumerics.greaterThan(lhs_p.first(), rhs_p.first());
			break;
		case GTEQ:
			condition = WNumerics.greaterThanEq(lhs_p.first(), rhs_p.first());
			break;
		case ELEMOF:
			condition = WSets.elementOf(lhs_p.first(), rhs_p.first());
			break;
		case SUBSET:
			condition = WSets.subset(lhs_p.first(), rhs_p.first());
			break;
		case SUBSETEQ:
			condition = WSets.subsetEq(lhs_p.first(), rhs_p.first());
			break;
		}

		// Determine condition for true branch, and check satisfiability
		WFormula trueCondition = WFormulas.and(precondition, condition);

		Proof tp = Solver.checkUnsatisfiable(timeout, trueCondition,
				wyone.Main.heuristic, wyone.Main.theories);
		if (tp instanceof Proof.Unsat) {
			trueCondition = null;
		}

		// Determine condition for false branch, and check satisfiability
		WFormula falseCondition = WFormulas.and(precondition, condition.not());
		Proof fp = Solver.checkUnsatisfiable(timeout, falseCondition,
				wyone.Main.heuristic, wyone.Main.theories);
		if (fp instanceof Proof.Unsat) {
			falseCondition = null;
		}

		Stmt stmt;
		if (trueCondition == null) {
			stmt = new Stmt(new Code.Skip(),elem.attributes());
		} else if (falseCondition == null) {
			stmt = new Stmt(new Code.Goto(code.target),elem.attributes());
		} else {
			// FIXME: this needs to be updated at some point
			ArrayList<Attribute> attributes = new ArrayList<Attribute>();
			attributes.addAll(elem.attributes());
			attributes.add(new Attribute.BranchInfo(true, true));			
			stmt = new Stmt(new Code.IfGoto(code.op, code.lhs, code.rhs,
					code.target), attributes);
		}

		// Finally, return what we've got
		return new Triple<Stmt, WFormula, WFormula>(stmt, trueCondition,
				falseCondition);
	}


	protected WFormula join(WFormula f1, WFormula f2) {
		if (f2 == null) {
			return f1;
		} else if(f1 == null) {
			return f2;
		}
		return WFormulas.or(f1,f2);
	}		
	
	protected Pair<WExpr,WFormula> infer(CExpr e, SyntacticElement elem) {
		try {
			if (e instanceof Value) {
				return new Pair<WExpr, WFormula>(infer((Value) e, elem), WBool.TRUE);
			} else if (e instanceof Variable) {
				return infer((Variable) e, elem);
			} else if (e instanceof Register) {
				return infer((Register) e, elem);
			} else if (e instanceof BinOp) {
				return infer((BinOp) e, elem);
			} else if (e instanceof UnOp) {
				return infer((UnOp) e, elem);
			} else if (e instanceof NaryOp) {
				return infer((NaryOp) e, elem);
			} else if (e instanceof ListAccess) {
				return infer((ListAccess) e, elem);
			} else if (e instanceof Tuple) {
				return infer((Tuple) e, elem);
			} else if (e instanceof TupleAccess) {
				return infer((TupleAccess) e, elem);
			} else if (e instanceof Invoke) {
				return infer((Invoke) e, elem);
			}
		} catch(SyntaxError se) {
			throw se;
		} catch(Exception ex) {
			syntaxError("internal failure",filename,elem,ex);
		}
		
		syntaxError("unknown condition encountered: " + e,filename,elem);
		return null; // unreachable
	}
	
	protected WValue infer(Value v, SyntacticElement elem) {		
		if(v instanceof Value.Bool) {
			Value.Bool vb = (Value.Bool) v;
			if(vb.value) {
				return WBool.TRUE;
			} else {
				return WBool.FALSE;
			}
		} else if(v instanceof Value.Int) {
			Value.Int vi = (Value.Int) v;
			return new WNumber(vi.value);
		} else if(v instanceof Value.Real) {
			Value.Real vr = (Value.Real) v;
			BigRational br = vr.value;
			return new WNumber(br.numerator(),br.denominator());			
		} else if(v instanceof Value.Set) {
			Value.Set vs = (Value.Set) v;			
			HashSet<WValue> vals = new HashSet<WValue>();
			for(Value e : vs.values) {
				vals.add(infer(e,elem));
			}
			return new WSetVal(vals);
		} else if(v instanceof Value.List) {
			Value.List vl = (Value.List) v;
			ArrayList<WValue> vals = new ArrayList<WValue>();
			for(Value e : vl.values) {
				vals.add(infer(e,elem));
			}
			return new WListVal(vals);
		} else if(v instanceof Value.Tuple) {
			Value.Tuple vt = (Value.Tuple) v;
			ArrayList<String> fields = new ArrayList<String>(vt.values.keySet());
			ArrayList<WValue> values = new ArrayList<WValue>();
			Collections.sort(fields);
			for(String f : fields) {			
				values.add(infer(vt.values.get(f),elem));
			}
			return new WTupleVal(fields,values);
		}
		
		syntaxError("unknown value encountered: " + v,filename,elem);
		return null; // unreachable
	}
	
	protected Pair<WExpr,WFormula> infer(Variable v, SyntacticElement elem) {		
		return new Pair<WExpr,WFormula>(new WVariable(v.name),WBool.TRUE);
	}
	
	protected Pair<WExpr,WFormula> infer(Register v, SyntacticElement elem) {
		String name = "%" + v.index;
		return new Pair<WExpr,WFormula>(new WVariable(name),WBool.TRUE);
	}
	
	protected Pair<WExpr,WFormula> infer(UnOp v, SyntacticElement elem) {
		Pair<WExpr,WFormula> rhs = infer(v.rhs,elem);		
		switch (v.op) {
		case NEG:
			return new Pair<WExpr, WFormula>(WNumerics.negate(rhs.first()), rhs
					.second());
		case LENGTHOF:
			return new Pair<WExpr, WFormula>(new WLengthOf(rhs.first()), rhs
					.second());
		}
		syntaxError("unknown unary operation: " + v.op,filename,elem);
		return null;
	}
	
	protected Pair<WExpr,WFormula> infer(NaryOp v, SyntacticElement elem) {
		switch (v.op) {
		case LISTGEN: {
			WFormula constraints = WBool.TRUE;
			ArrayList<WExpr> args = new ArrayList<WExpr>();
			for (CExpr e : v.args) {
				Pair<WExpr, WFormula> p = infer(e, elem);
				args.add(p.first());
				constraints = WFormulas.and(p.second());
			}

			return new Pair<WExpr, WFormula>(new WListConstructor(args),
					constraints);
		}
		case SETGEN: {
			WFormula constraints = WBool.TRUE;
			HashSet<WExpr> args = new HashSet<WExpr>();
			for (CExpr e : v.args) {
				Pair<WExpr, WFormula> p = infer(e, elem);
				args.add(p.first());
				constraints = WFormulas.and(p.second());
			}

			return new Pair<WExpr, WFormula>(new WSetConstructor(args),
					constraints);
		}		
		case SUBLIST: {
			Pair<WExpr,WFormula> src = infer(v.args.get(0), elem);
			Pair<WExpr,WFormula> start = infer(v.args.get(1), elem);
			Pair<WExpr,WFormula> end = infer(v.args.get(2), elem);

			WVariable retVar = WVariable.freshVar();

			// first, identify new length
			WFormula lenConstraints = WExprs.equals(new WLengthOf(retVar),
					WNumerics.subtract(end.first(), start.first()));

			// second, pump from src into retVar
			WVariable i = WVariable.freshVar();
			HashMap<WVariable, WExpr> variables = new HashMap();
			variables.put(i, src.first());
			WFormula lhs = WFormulas.and(
					WNumerics.lessThanEq(start.first(), i), WNumerics.lessThan(
							i, end.first()));
			WFormula rhs = WExprs.equals(new WListAccess(src.first(), i),
					new WListAccess(retVar, WNumerics
							.subtract(i, start.first())));
			WFormula forall1 = new WBoundedForall(true, variables, WFormulas
					.implies(lhs, rhs));

			// third, pump from retVar into src
			variables = new HashMap<WVariable,WExpr>();
			variables.put(i, retVar);
			rhs = WExprs.equals(new WListAccess(src.first(), WNumerics.add(i,
					start.first())), new WListAccess(retVar, i));
			WFormula forall2 = new WBoundedForall(true, variables, rhs);

			WFormula constraints = WFormulas.and(lenConstraints, forall1,
					forall2, src.second(), start.second(), end.second());

			return new Pair<WExpr,WFormula>(retVar,constraints);
			}							
		}
		syntaxError("unknown nary operation: " + v.op,filename,elem);
		return null;
	}
	
	protected Pair<WExpr, WFormula> infer(Invoke ivk, SyntacticElement elem)
			throws ResolveError {
		WFormula constraints = WBool.TRUE;
		ArrayList<WExpr> args = new ArrayList<WExpr>();
		for (CExpr e : ivk.args) {
			Pair<WExpr, WFormula> p = infer(e, elem);
			args.add(p.first());
			constraints = WFormulas.and(p.second());
		}

		WVariable rv = new WVariable("&" + ivk.name, args);
		Module module = loader.loadModule(ivk.name.module());
		Module.Method method = module.method(ivk.name.name(), ivk.type);
		Module.Case mcase = method.cases().get(ivk.caseNum);
		Block postcondition = mcase.postcondition();
		if(postcondition != null) {
			WFormula pc = propagate(postcondition, WBool.TRUE).second();
			HashMap<WExpr, WExpr> binding = new HashMap<WExpr, WExpr>();
			binding.put(new WVariable("$"), rv);
			constraints = WFormulas.and(constraints, pc.substitute(binding));
		}
		return new Pair<WExpr, WFormula>(rv, constraints);
	}
	
	protected Pair<WExpr,WFormula> infer(TupleAccess ta, SyntacticElement elem) {
		Pair<WExpr,WFormula> rhs = infer(ta.lhs,elem);
		return new Pair<WExpr, WFormula>(
				new WTupleAccess(rhs.first(), ta.field), rhs.second());
	}
	
	protected Pair<WExpr,WFormula> infer(Tuple t, SyntacticElement elem) {
		ArrayList<String> fields = new ArrayList<String>(t.values.keySet());
		ArrayList<WExpr> values = new ArrayList<WExpr>();
		WFormula constraints = WBool.TRUE;
		Collections.sort(fields);
		for(String f : fields) {
			Pair<WExpr,WFormula> p = infer(t.values.get(f),elem);
			values.add(p.first());
			constraints = WFormulas.and(constraints,p.second());
		}		
		return new Pair<WExpr, WFormula>(new WTupleConstructor(fields, values),
				constraints);
	}
	
	protected Pair<WExpr,WFormula> infer(ListAccess ta, SyntacticElement elem) {
		Pair<WExpr,WFormula> src = infer(ta.src,elem);
		Pair<WExpr,WFormula> idx = infer(ta.index,elem);
		return new Pair<WExpr, WFormula>(new WListAccess(src.first(), idx
				.first()), WFormulas.and(src.second(), idx.second()));
	}
	
	protected Pair<WExpr,WFormula> infer(BinOp v, SyntacticElement elem) {
		Pair<WExpr,WFormula> lhs = infer(v.lhs, elem);
		Pair<WExpr,WFormula> rhs = infer(v.rhs, elem);
		WFormula constraints = WFormulas.and(lhs.second(),rhs.second());
		switch (v.op) {
		case ADD:
			return new Pair<WExpr, WFormula>(WNumerics.add(lhs.first(), rhs
					.first()), constraints);
		case SUB:
			return new Pair<WExpr, WFormula>(WNumerics.subtract(lhs.first(),
					rhs.first()), constraints);
		case DIV:
			return new Pair<WExpr, WFormula>(WNumerics.divide(lhs.first(), rhs
					.first()), constraints);
		case MUL:
			return new Pair<WExpr, WFormula>(WNumerics.multiply(lhs.first(),
					rhs.first()), constraints);
		case UNION: {
			WVariable rv = WVariable.freshVar();
			WVariable vs = WVariable.freshVar();

			HashMap<WVariable, WExpr> vars = new HashMap();
			vars.put(rv, vs);
			WSetConstructor sc = new WSetConstructor(rv);
			WFormula allc = WFormulas.or(WSets.subsetEq(sc, lhs.first()), WSets
					.subsetEq(sc, rhs.first()));
			constraints = WFormulas.and(constraints, WSets.subsetEq(
					lhs.first(), vs), WSets.subsetEq(rhs.first(), vs),
					new WBoundedForall(true, vars, allc));
			return new Pair<WExpr, WFormula>(rv, constraints);
		}
		case INTERSECT: {
			WVariable rv = WVariable.freshVar();
			WVariable vs = WVariable.freshVar();
			HashMap<WVariable, WExpr> vars = new HashMap();
			vars.put(rv, vs);				
			WSetConstructor sc = new WSetConstructor(rv);
			WFormula left = new WBoundedForall(true, vars, WFormulas.and(WSets
					.subsetEq(sc, lhs.first()), WSets.subsetEq(sc, rhs.first())));
			
			vars = new HashMap();
			vars.put(rv, lhs.first());
			WFormula right = new WBoundedForall(true, vars, WFormulas.implies(WSets
					.subsetEq(sc, rhs.first()), WSets.subsetEq(sc, vs)));
			
			constraints = WFormulas
					.and(constraints, left, right, WSets.subsetEq(vs, lhs
							.first()), WSets.subsetEq(vs, rhs.first()));

			return new Pair<WExpr, WFormula>(rv, constraints);
		}
		
		}

		syntaxError("unknown binary operation encountered: " + v, filename,
				elem);
		return null;
	}

	/**
	 * Convert a Wyil type into a Wyone type. Mostly, the conversion is
	 * straightforward and obvious.
	 * 
	 * @param type
	 * @return
	 */
	protected WType convert(Type type) {
		if(type == Type.T_BOOL) {
			return WBoolType.T_BOOL;
		} else if(type == Type.T_INT) {
			return WIntType.T_INT;
		} else if(type == Type.T_REAL) {
			return WRealType.T_REAL;
		} else if(type instanceof Type.List) {
			Type.List tl = (Type.List) type;
			return new WListType(convert(tl.element));			
		} else if(type instanceof Type.Set) {
			Type.Set tl = (Type.Set) type;
			return new WSetType(convert(tl.element));			
		} else if(type instanceof Type.Tuple) {
			Type.Tuple tl = (Type.Tuple) type;
			ArrayList<String> keys = new ArrayList<String>(tl.types.keySet());
			ArrayList<wyone.util.Pair<String,WType>> types = new ArrayList();
			Collections.sort(keys);
			for(String key : keys) {
				types.add(new wyone.util.Pair(key,convert(tl.types.get(key))));
			}
			return new WTupleType(types);
		} else {
			return WAnyType.T_ANY;
		}
	}
}
