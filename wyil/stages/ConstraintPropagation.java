package wyil.stages;

import static wyil.util.SyntaxError.syntaxError;

import java.util.*;

import wyil.ModuleLoader;
import wyil.jvm.rt.BigRational;
import wyil.lang.*;
import wyil.lang.Code.*;
import wyil.lang.CExpr.*;
import wyil.util.Pair;
import wyil.util.ResolveError;
import wyil.util.SyntacticElement;
import wyil.util.SyntaxError;
import wyil.util.Triple;
import wyil.util.dfa.*;
import wyone.core.*;
import wyone.theory.congruence.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;
import wyone.theory.list.*;
import wyone.theory.set.*;
import wyone.theory.tuple.*;
import wyone.theory.quantifier.*;

public class ConstraintPropagation implements ModuleTransform {	
	private ModuleLoader loader;
	private String filename;
	private int timeout;
	
	public ConstraintPropagation(ModuleLoader loader, int timeout) {
		this.timeout = timeout;
		this.loader = loader;
	}
	
	public Module apply(Module module) {	
		ArrayList<Module.Method> methods = new ArrayList<Module.Method>();
		
		filename = module.filename();
		
		for(Module.Method method : module.methods()) {
			methods.add(transform(method));
		}
		return new Module(module.id(), module.filename(), methods, module.types(),
				module.constants());
	}
	
	public Module.Method transform(Module.Method method) {
		ArrayList<Module.Case> cases = new ArrayList<Module.Case>();
		for (Module.Case c : method.cases()) {
			cases.add(transform(c,method));
		}
		return new Module.Method(method.name(), method.type(), cases);
	}
	
	public Module.Case transform(Module.Case mcase, Module.Method method) {
		if (mcase.precondition() != null) {
			Pair<Block, WFormula> precondition = transform(
					mcase.precondition(), WBool.TRUE);
			Block body = transform(mcase.body(), precondition.second()).first();
			return new Module.Case(mcase.parameterNames(),
					precondition.first(), mcase.postcondition(), body);
		} else {
			Block body = transform(mcase.body(), WBool.TRUE).first();
			return new Module.Case(mcase.parameterNames(),
					mcase.precondition(), mcase.postcondition(), body);
		}
	}
	
	protected Pair<Block,WFormula> transform(Block block, WFormula precondition) {
		Block nblock = new Block();
		HashMap<String, WFormula> flowsets = new HashMap();
		
		for (int i = 0; i != block.size(); ++i) {
			Stmt stmt = block.get(i);
			try {				
				Code code = stmt.code;

				ArrayList<Attribute> attributes = new ArrayList<Attribute>();

				if (code instanceof Label) {
					Label label = (Label) code;								
					precondition = join(precondition, flowsets.get(label.label));				
				}

				WFormula tmp = precondition;

				if (precondition == null) {
					continue; // this indicates dead-code
				} else if (code instanceof Goto) {
					Goto got = (Goto) code;
					merge(got.target, precondition, flowsets);
					precondition = null;
				} else if (code instanceof IfGoto) {
					IfGoto igot = (IfGoto) code;	

					Triple<Stmt, WFormula, WFormula> r = infer((Code.IfGoto) code,
							precondition, stmt);				
					stmt = r.first();
					code = stmt.code;
					precondition = r.third();
					if(r.first() != null && precondition != null) {
						merge(igot.target, r.second(), flowsets);
					} else if(r.first() != null) {
						merge(igot.target, r.second(), flowsets);
					}

				} else if (code instanceof Assign) {
					precondition = infer((Code.Assign) code, i, stmt, precondition);
				} else if (code instanceof Fail || code instanceof Return) {
					precondition = null;
				} 

				attributes.addAll(stmt.attributes());
				attributes.add(new Attribute.PreCondition(tmp));
				nblock.add(code, attributes);
			} catch(SyntaxError se) {
				throw se;
			} catch(Throwable ex) {
				syntaxError("internal failure",filename,stmt,ex);
			}
		}
		
		return new Pair<Block,WFormula>(nblock,precondition);
	}
	
	protected void merge(String target, WFormula precondition,
			HashMap<String, WFormula> flowsets) {						
		WFormula pc = flowsets.get(target);
		if(pc == null) {						
			flowsets.put(target, precondition);
		} else {
			flowsets.put(target, join(pc,precondition));
		}
	}
	
	public static WFormula join(WFormula f1, WFormula f2) {
		if (f2 == null) {
			return f1;
		} else if(f1 == null) {
			return f2;
		}
		return WFormulas.or(f1,f2);
	}
	
	protected Triple<Stmt, WFormula, WFormula> infer(Code.IfGoto code,
			WFormula precondition, SyntacticElement elem) {
		
		Pair<WExpr, WFormula> lhs_p = infer(code.lhs, elem);
		Pair<WExpr, WFormula> rhs_p = infer(code.rhs, elem);
		precondition = WFormulas.and(precondition,lhs_p.second(),rhs_p.second());		
		WFormula condition = null;
		switch(code.op) {
		case EQ:		
			condition = new WEquality(true,lhs_p.first(),rhs_p.first());
			break;
		case NEQ:
			condition = new WEquality(false,lhs_p.first(),rhs_p.first());
			break;
		case LT:
			condition = WNumerics.lessThan(lhs_p.first(),rhs_p.first());
			break;
		case LTEQ:
			condition = WNumerics.lessThanEq(lhs_p.first(),rhs_p.first());
			break;
		case GT:
			condition = WNumerics.greaterThan(lhs_p.first(),rhs_p.first());
			break;
		case GTEQ:
			condition = WNumerics.greaterThanEq(lhs_p.first(),rhs_p.first());
			break;
		case ELEMOF:
			condition = WSets.elementOf(lhs_p.first(),rhs_p.first());
			break;
		case SUBSET:
			condition = WSets.subset(lhs_p.first(),rhs_p.first());
			break;
		case SUBSETEQ:
			condition = WSets.subsetEq(lhs_p.first(),rhs_p.first());
			break;
		}
		
		// Determine condition for true branch, and check satisfiability
		WFormula trueCondition = WFormulas.and(precondition,condition);
		
		Proof tp = Solver.checkUnsatisfiable(timeout, trueCondition,
				wyone.Main.heuristic, wyone.Main.theories);		
		if(tp instanceof Proof.Unsat) { trueCondition = null; }
		
		// Determine condition for false branch, and check satisfiability
		WFormula falseCondition = WFormulas.and(precondition,condition.not());			
		Proof fp = Solver.checkUnsatisfiable(timeout, falseCondition,
				wyone.Main.heuristic, wyone.Main.theories);		
		if(fp instanceof Proof.Unsat) {	falseCondition = null; }
		
		Stmt stmt;
		if(trueCondition == null) {
			stmt = new Stmt(new Code.Skip());
		} else if(falseCondition == null) {
			stmt = new Stmt(new Code.Goto(code.target));
		} else {
			// FIXME: this needs to be updated at some point
			Attribute.BranchInfo bi = new Attribute.BranchInfo(true,true);
			stmt = new Stmt(new Code.IfGoto(code.op, code.lhs, code.rhs,
					code.target), bi, elem.attribute(Attribute.Source.class));
		}
		
		// Finally, return what we've got
		return new Triple<Stmt, WFormula, WFormula>(stmt, trueCondition,
				falseCondition);
	}
	
	protected WFormula infer(Code.Assign code, int index,
			SyntacticElement elem, WFormula precondition) {
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
		WFormula constraint = WBool.TRUE;				
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
			WFormula pc = transform(postcondition, WBool.TRUE).second();
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
		}

		syntaxError("unknown binary operation encountered: " + v, filename,
				elem);
		return null;
	}
	
	/*
	protected CExpr infer(NaryOp v, Stmt stmt, HashMap<String,Type> environment) {
		ArrayList<CExpr> args = new ArrayList<CExpr>();
		for(CExpr arg : v.args) {
			args.add(infer(arg,stmt,environment));
		}
		
		switch(v.op) {
			case SETGEN:				
			case LISTGEN:
				return CExpr.NARYOP(v.op, args);
			case SUBLIST:						
				if(args.size() != 3) {
					syntaxError("Invalid arguments for sublist operation",filename,stmt);
				}
				checkIsSubtype(Type.T_LIST(Type.T_ANY),args.get(0).type(),stmt);
				checkIsSubtype(Type.T_INT,args.get(1).type(),stmt);
				checkIsSubtype(Type.T_INT,args.get(2).type(),stmt);
				return CExpr.NARYOP(v.op, args);
		}
		
		syntaxError("Unknown nary operation",filename,stmt);
		return null;
	}
	
	protected CExpr infer(ListAccess e, Stmt stmt, HashMap<String,Type> environment) {
		CExpr src = infer(e.src,stmt,environment);
		CExpr idx = infer(e.index,stmt,environment);
		checkIsSubtype(Type.T_LIST(Type.T_ANY),src.type(),stmt);
		checkIsSubtype(Type.T_INT,idx.type(),stmt);
		return CExpr.LISTACCESS(src,idx);
	}
		
	protected CExpr infer(TupleAccess e, Stmt stmt, HashMap<String,Type> environment) {
		CExpr lhs = infer(e.lhs,stmt,environment);				
		Type.Tuple ett = Type.effectiveTupleType(lhs.type());				
		if (ett == null) {
			syntaxError("tuple type required, got: " + lhs.type(), filename, stmt);
		}
		Type ft = ett.types.get(e.field);
		if (ft == null) {
			syntaxError("type has no field named " + e.field, filename, stmt);
		}
		return CExpr.TUPLEACCESS(lhs, e.field);
	}
	
	protected CExpr infer(Tuple e, Stmt stmt, HashMap<String,Type> environment) {
		HashMap<String, CExpr> args = new HashMap<String, CExpr>();
		for (Map.Entry<String, CExpr> v : e.values.entrySet()) {
			args.put(v.getKey(), infer(v.getValue(), stmt, environment));
		}
		return CExpr.TUPLE(args);
	}
	
	protected CExpr infer(Invoke ivk, Stmt stmt,
			HashMap<String,Type> environment) {
		
		ArrayList<CExpr> args = new ArrayList<CExpr>();
		ArrayList<Type> types = new ArrayList<Type>();
		CExpr receiver = ivk.receiver;
		Type.ProcessName receiverT = null;
		if(receiver != null) {
			receiver = infer(receiver, stmt, environment);
			receiverT = checkType(receiver.type(),Type.ProcessName.class,stmt);
		}
		for (CExpr arg : ivk.args) {
			arg = infer(arg, stmt, environment);
			args.add(arg);
			types.add(arg.type());
		}
		
		try {
			Type.Fun funtype = bindFunction(ivk.name, receiverT, types, stmt);

			if (funtype == null) {
				if (receiver == null) {
					syntaxError("invalid or ambiguous function call", filename,
							stmt);
				} else {
					syntaxError("invalid or ambiguous method call", filename,
							stmt);
				}
			}
			
			return CExpr.INVOKE(funtype, ivk.name, ivk.caseNum, receiver, args);
		} catch (ResolveError ex) {
			syntaxError(ex.getMessage(), filename, stmt);
			return null; // unreachable
		}
	}
	*/		
}
