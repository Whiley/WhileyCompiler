// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyjx.stages;

import static wyil.util.SyntaxError.syntaxError;

import java.util.*;

import wyil.ModuleLoader;
import wyil.jvm.rt.BigRational;
import wyil.lang.*;
import wyil.lang.Code.*;
import wyil.lang.CExpr.*;
import wyil.util.*;
import wyil.util.dfa.*;
import wyjx.attributes.*;
import wyone.core.*;
import wyone.theory.congruence.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;
import wyone.theory.list.*;
import wyone.theory.set.*;
import wyone.theory.tuple.*;
import wyone.theory.quantifier.*;
import wyone.theory.type.*;

public class ConstraintPropagation extends ForwardFlowAnalysis<WFormula> {	
	private int timeout;

	/**
	 * The substitutions map is used to help in the translation of loops over
	 * lists. It's something of an ugly hack, but it works effectively. Probably
	 * a better solution would be to modify wyone to treat quantifiers over
	 * lists in a different fashion.
	 */
	private HashMap<String,WExpr> substitutions = new HashMap<String,WExpr>();

	/**
	 * Provide additional debug information
	 * @param loader
	 * @param minimal
	 * @param timeout
	 */
	private boolean debug = true;
	
	public ConstraintPropagation(ModuleLoader loader, int timeout) {
		super(loader);
		this.timeout = timeout;			
	}
	
	public WFormula initialStore() {
		WFormula init = WBool.TRUE;
		
		// First, generate appropriate type constraints
		List<String> paramNames = methodCase.parameterNames();
		List<Type> paramTypes = method.type().params;
		HashMap<String,CExpr> binding = new HashMap<String,CExpr>();
		for(int i=0;i!=paramNames.size();++i) {
			String n = paramNames.get(i);			
			Type t = paramTypes.get(i);
			binding.put("$" + i, CExpr.VAR(t,n));
			init = WFormulas.and(init, WTypes.subtypeOf(new WVariable(n),
					convert(t)));
		}

		// Second, initialise from precondition (if present)
		Precondition preattr = methodCase.attribute(Precondition.class);
		Block precondition = preattr != null ? preattr.constraint : null;
		
		if (precondition != null) {			
			precondition = Block.substitute(binding,precondition);			
			Pair<Block, WFormula> condition = propagate(precondition, init);

			// reset the stores map
			this.stores = new HashMap<String,WFormula>();			
			return condition.second();
		} else {			
			return init;
		}
	}
		
	protected Pair<Stmt,WFormula> propagate(Stmt stmt, WFormula store) {
		// First, add on the precondition attribute		
		ArrayList<Attribute> attributes = new ArrayList<Attribute>(stmt.attributes());
		
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
		WFormula rhs_c = rhs_p.second();

		LVar lvar = CExpr.extractLVar(code.lhs);

		// Create shadows
		HashMap<WExpr, WExpr> binding = new HashMap<WExpr, WExpr>();
		binding.put(new WVariable(lvar.name()), new WVariable(lvar.name() + "$"
				+ index++));
		
		rhs = rhs.substitute(binding);
		rhs_c = rhs_c.substitute(binding);
		WFormula postcondition = precondition.substitute(binding);

		// finally, put it altogether
		return WFormulas.and(postcondition, new WEquality(true, lhs, rhs),
				assignCondition(code.lhs, binding, elem), lhs_p.second(), rhs_c);
	}
		
	protected WFormula assignCondition(LVal lval,
			HashMap<WExpr, WExpr> binding, SyntacticElement elem) {
		
		// the purpose of this method is to generate a condition which
		// identifies that everything in the lhs base variable is identical to
		// that in the shadowVariable, except for the particular element being
		// updated.
		List<CExpr> exprs = flattern(lval,elem);
		LVar variable = (LVar) exprs.get(0);
		WFormula constraint = WTypes.subtypeOf(new WVariable(variable.name()),
				convert(variable.type()));				
		for(int i=1;i!=exprs.size();++i) {
			CExpr access = exprs.get(i);			
			if(access instanceof RecordAccess){				
				RecordAccess ta = (RecordAccess) access;
				Type.Record tt = Type.effectiveRecordType(ta.lhs.type());
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
				ListAccess la = (ListAccess) access;
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
				constraint = WFormulas.and(constraint, new WEquality(true,
						new WLengthOf(src.first()), new WLengthOf(nsrc)),
						new WBoundedForall(true, srcs, body), src.second(),
						index.second());
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
		} else if (lval instanceof RecordAccess) {
			RecordAccess la = (RecordAccess) lval;
			List<CExpr> f = flattern(la.lhs, elem);
			f.add(lval);
			return f;
		} else if (lval instanceof UnOp) {
			UnOp la = (UnOp) lval;
			// should only be process access here
			List<CExpr> f = flattern(la.rhs, elem);
			f.add(lval);
			return f;
		}

		syntaxError("unknown lval encountered: " + lval, filename, elem);
		return null;
	}

	protected Pair<Block, WFormula> propagate(Code.Start start, Code.End end,
			Block body, Stmt stmt, WFormula store) {
		
		if(start instanceof Code.Forall){
			return propagate((Code.Forall)start,(Code.ForallEnd)end,body,stmt,store);
		} else if(start instanceof Code.Loop){
			return propagate((Code.Loop)start,(Code.LoopEnd)end,body,stmt,store);
		} 
		
		Block nblock = new Block();
		nblock.add(start);
		
		Pair<Block,WFormula> r = propagate(body,store);
		body = r.first();
		store = r.second();
	
		nblock.addAll(body);
		nblock.add(end);
		return new Pair<Block, WFormula>(nblock, store);
	}
	
	protected Pair<Block, WFormula> propagate(Code.Forall start, Code.ForallEnd end,
			Block body, Stmt stmt, WFormula store) {
		Block nblock = new Block();
		nblock.add(start);		
		Code.Forall fall = (Code.Forall) start;
		
		// Create loop variable shadows
		HashMap<WExpr, WExpr> binding = new HashMap<WExpr, WExpr>();
		for(LVar v : start.modifies) { 
			binding.put(new WVariable(v.name()), new WVariable(v.name() + "$"
				+ index++));
		}		
		
		store = store.substitute(binding);				
		
		for(LVar v : start.modifies) {
			WVariable var = new WVariable(v.name());
			WType var_t = convert(v.type());
			store = WFormulas.and(store,WTypes.subtypeOf(var,var_t));			
		}
		
		if(start.invariant != null) {			
			// Assume loop invariant
			store = propagate(start.invariant,store).second();
		}
		
		WFormula exitStore = store;
		
		WVariable var = new WVariable(fall.variable.name());			
		// Convert the source collection 
		Pair<WExpr,WFormula> src = infer(fall.source,stmt);		
		if (fall.source.type() instanceof Type.List) {
			// We have to treat lists differently from sets because of the
			// way wyone handles list quantification. It's kind of annoying,
			// but there's not much we can do.
			store = WFormulas.and(store, WNumerics.lessThanEq(WNumber.ZERO,
					var), WNumerics.lessThan(var,
					new WLengthOf(src.first())), WTypes.subtypeOf(var,
					WIntType.T_INT), src.second());
			substitutions
					.put(var.name(), new WListAccess(src.first(), var));								
		} else {
			store = WFormulas.and(store, WSets.elementOf(var, src.first()),
					WTypes.subtypeOf(var, convert(fall.variable.type())),
					src.second());
		}
				
		// Save the parent stores. We need to do this, so we can intercept
		// all stores being emitted from the for block, in order that we can
		// properly quantify them.			
		HashMap<String,WFormula> parentStores = stores;
		stores = new HashMap<String,WFormula>();
		// Propagate through the body
		Pair<Block,WFormula> r = propagate(body,store);
		substitutions.remove(var.name()); // remove any substitution made
										  // for a loop over list
		body = r.first();
					
		// Split for the formula into those bits which need to be
		// quantified, and those which don't
		Pair<WFormula, WFormula> split = splitFormula(var.name(),
				r.second());			
		// Universally quantify the condition emitted at the end of the
		// body, as this captures what is true for every element in the
		// source collection.
		store = new WBoundedForall(true, var, src.first(), split.first());			
		exitStore = WFormulas.and(exitStore,store,src.second());
		store = WFormulas.and(store,split.second());			
		
		// Existentially quantify any breaks out of the loop. These
		// represent what is true for some elements in the source
		// collection, but not necessarily all.
		for (Map.Entry<String, WFormula> e : stores.entrySet()) {
			String key = e.getKey();
			// Split for the formula into those bits which need to be
			// quantified, and those which don't
			split = splitFormula(fall.variable.name(), e.getValue()); 
			WFormula exit = new WBoundedForall(false, var, src.first(),
					split.first().not());				
			exit = WFormulas.and(exit,split.second());
			
			// Now, join the exit store with anything in the parent stores
			WFormula existing = stores.get(key);
			if (existing == null) {
				// was no existing store for parent
				parentStores.put(key, exit);
			} else {
				// was something in parent, so join them together
				parentStores.put(key, join(exit, existing));
			}
		}
		
		// finally, put parent stores back
		stores = parentStores;

		nblock.addAll(body);
		nblock.add(end);
		return new Pair<Block, WFormula>(nblock, exitStore);
	}
	
	protected Pair<Block, WFormula> propagate(Code.Loop start, Code.LoopEnd end,
			Block body, Stmt stmt, WFormula store) {
		
		// Create loop variable shadows
		HashMap<WExpr, WExpr> binding = new HashMap<WExpr, WExpr>();
		for(LVar v : start.modifies) { 
			binding.put(new WVariable(v.name()), new WVariable(v.name() + "$"
				+ index++));
		}		
		
		store = store.substitute(binding);
		
		for(LVar v : start.modifies) {
			WVariable var = new WVariable(v.name());
			WType var_t = convert(v.type());
			store = WFormulas.and(store,WTypes.subtypeOf(var,var_t));			
		}
		
		if(start.invariant != null) {			
			// Assume loop invariant
			store = propagate(start.invariant,store).second();
		}
		WFormula exitStore = store; // this is all we'll know at the end
		
		Block nblock = new Block();
		nblock.add(start);
		Pair<Block,WFormula> r = propagate(body,store);
		body = r.first();
		store = r.second();
		nblock.addAll(body);
		nblock.add(end);
		
		return new Pair<Block, WFormula>(nblock, exitStore);	
	}
	
	// The following method splits a formula into two components: those bits
	// which use the given variable (left), and those which don't (right). This
	// is done to avoid quantifying more than is necessary when dealing with loops.	
	protected Pair<WFormula,WFormula> splitFormula(String var, WFormula f) {		
		if(f instanceof WConjunct) {
			WConjunct c = (WConjunct) f;
			WFormula ts = WBool.TRUE;
			WFormula fs = WBool.TRUE;
			for(WFormula st : c.subterms()) {
				Pair<WFormula,WFormula> r = splitFormula(var,st);
				ts = WFormulas.and(ts,r.first());
				fs = WFormulas.and(fs,r.second());
			}			
			return new Pair<WFormula,WFormula>(ts,fs);
		} else {
			// not a conjunct, so check whether or not this uses var or not.
			Set<WVariable> uses = WExprs.match(WVariable.class, f);
			for (WVariable v : uses) {
				if (v.name().equals(var)) {
					return new Pair<WFormula, WFormula>(f, WBool.TRUE);
				}
			}
			// Ok, doesn't use variable
			return new Pair<WFormula, WFormula>(WBool.TRUE,f);
		}
	}
	
	protected Triple<Stmt, WFormula, WFormula> propagate(Code.IfGoto code,
			Stmt elem, WFormula store) {

		Pair<WExpr, WFormula> lhs_p = infer(code.lhs, elem);		
		Pair<WExpr, WFormula> rhs_p = infer(code.rhs, elem);
		WFormula precondition = WFormulas.and(store, lhs_p.second(), rhs_p
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
		case SUBTYPEEQ:
		{
			Value.TypeConst tc = (Value.TypeConst) code.rhs;
			condition = WTypes.subtypeOf(lhs_p.first(), convert(tc.type));
			break;
		}
		case NSUBTYPEEQ:
		{
			Value.TypeConst tc = (Value.TypeConst) code.rhs;
			condition = WTypes.subtypeOf(lhs_p.first(), convert(tc.type)).not();
			break;
		}
		}

		// Determine condition for true and false branches
		WFormula trueCondition = WFormulas.and(precondition, condition);
		WFormula falseCondition = WFormulas.and(precondition, condition.not());				
		BranchPredict expected = elem.attribute(BranchPredict.class);		
		
		if(expected != null && !expected.trueBranch) {			
			if(debug) {
				System.out.println("CHECKING(1): " + trueCondition);
			}
			Proof tp = Solver.checkUnsatisfiable(timeout, trueCondition,
					wyone.Main.heuristic, wyone.Main.theories);			
			if (tp instanceof Proof.Unsat) {
				if(debug) { System.out.println("UNSAT(1)"); }
				trueCondition = null;
			}
		}
				
		if(expected != null && !expected.falseBranch) {
			if(debug) { System.out.println("CHECKING(2): " + falseCondition); }
			Proof fp = Solver.checkUnsatisfiable(timeout, falseCondition,
					wyone.Main.heuristic, wyone.Main.theories);				
			if (fp instanceof Proof.Unsat) {
				if(debug) { System.out.println("UNSAT(2)"); }
				falseCondition = null;
			}					
		}
		
		// Update attribute information
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		attributes.addAll(elem.attributes());		
		
		// Now, create new statement
		Stmt stmt;		
		if (trueCondition == null) {
			stmt = new Stmt(new Code.Skip(),attributes);
		} else if (falseCondition == null) {
			stmt = new Stmt(new Code.Goto(code.target),attributes);
		} else {
			attributes.add(new BranchInfo(true, true));
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
		} else if (f1 == null) {
			return f2;
		}
		WFormula common = WFormulas.intersect(f1, f2);
		f1 = WFormulas.factorOut(f1, common);
		f2 = WFormulas.factorOut(f2, common);
		return WFormulas.and(common, WFormulas.or(f1, f2));
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
			} else if (e instanceof Record) {
				return infer((Record) e, elem);
			} else if (e instanceof RecordAccess) {
				return infer((RecordAccess) e, elem);
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
		} else if(v instanceof Value.Null) {
			// This is not a long term solution
			return new WNumber(0);
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
		} else if(v instanceof Value.Record) {
			Value.Record vt = (Value.Record) v;
			ArrayList<String> fields = new ArrayList<String>(vt.values.keySet());
			ArrayList<WValue> values = new ArrayList<WValue>();
			Collections.sort(fields);
			for(String f : fields) {			
				values.add(infer(vt.values.get(f),elem));
			}
			return new WTupleVal(fields,values);
		} else if(v instanceof Value.TypeConst) {
			return null;
		}
		
		syntaxError("unknown value encountered: " + v,filename,elem);
		return null; // unreachable
	}
	
	protected Pair<WExpr,WFormula> infer(Variable v, SyntacticElement elem) {		
		WExpr sub = substitutions.get(v.name());
		if(sub != null) {
			return new Pair<WExpr,WFormula>(sub,WBool.TRUE);
		} else {
			return new Pair<WExpr,WFormula>(new WVariable(v.name),WBool.TRUE);
		}
	}
	
	protected Pair<WExpr, WFormula> infer(Register v, SyntacticElement elem) {
		WExpr sub = substitutions.get(v.name());
		if (sub != null) {
			return new Pair<WExpr, WFormula>(sub, WBool.TRUE);
		} else {
			return new Pair<WExpr, WFormula>(new WVariable(v.name()),
					WBool.TRUE);
		}
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
		case PROCESSACCESS :
		{
				if (v.rhs instanceof Variable) {
					Variable var = (Variable) v.rhs;
					if (var.name.equals("this")) {
						// this is a very special case
						return new Pair<WExpr, WFormula>(new WVariable("this"),
								WBool.TRUE);
					}
				}
				WVariable wvar = WVariable.freshVar();
				return new Pair<WExpr, WFormula>(wvar, WBool.TRUE);
		}
		case PROCESSSPAWN:
		{
			// not sure what else we can do here
			WVariable wvar = WVariable.freshVar();
			return new Pair<WExpr, WFormula>(wvar, WBool.TRUE);
		}
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
		HashMap<WExpr, WExpr> binding = new HashMap<WExpr, WExpr>();		
		Module module = loader.loadModule(ivk.name.module());
		Module.Method method = module.method(ivk.name.name(), ivk.type);
		Module.Case mcase = method.cases().get(ivk.caseNum);
		
		// FIXME: I believe there is a bug here, in that register names may
		// interfere between the original condition and that being added (but I
		// could be wrong).
		
		int idx=0;
		for (CExpr e : ivk.args) {
			Pair<WExpr, WFormula> p = infer(e, elem);			
			binding.put(new WVariable("$" + idx++), p.first());
			args.add(p.first());
			constraints = WFormulas.and(constraints,p.second());
		}		
		constraints = constraints.substitute(binding);
		
		WVariable rv = new WVariable(ivk.name.toString(), args);
		Postcondition postattr = mcase.attribute(Postcondition.class);
		Block postcondition = postattr != null ? postattr.constraint : null;		
		if(postcondition != null) {
			WVariable var = new WVariable("$"); 
			WFormula pc = propagate(postcondition,
					WTypes.subtypeOf(var, convert(method.type().ret))).second();			
			binding.put(var, rv);						
			constraints = WFormulas.and(constraints, pc.substitute(binding));				
		}
		
		return new Pair<WExpr, WFormula>(rv, constraints);
	}
	
	protected Pair<WExpr,WFormula> infer(RecordAccess ta, SyntacticElement elem) {
		Pair<WExpr,WFormula> rhs = infer(ta.lhs,elem);
		return new Pair<WExpr, WFormula>(
				new WTupleAccess(rhs.first(), ta.field), rhs.second());
	}
	
	protected Pair<WExpr,WFormula> infer(Record t, SyntacticElement elem) {
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
		case APPEND: {
			WVariable retVar = WVariable.freshVar();
			
			// first, identify new length					
			WFormula lenConstraints = WExprs.equals(new WLengthOf(retVar),
					WNumerics.add(new WLengthOf(lhs.first()), new WLengthOf(rhs
							.first())));

			// second, pump from left src into retVar
			WVariable i = WVariable.freshVar();
			HashMap<WVariable,WExpr> variables = new HashMap();
			variables.put(i,lhs.first());
			WFormula wlhs = WExprs.equals(new WListAccess(lhs.first(), i),
					new WListAccess(retVar, i));
			WFormula forall1 = new WBoundedForall(true,variables,wlhs);
			
			// third, pump from right src into retVar
			i = WVariable.freshVar();
			variables = new HashMap();
			variables.put(i,rhs.first());
			WFormula wrhs = WExprs.equals(new WListAccess(rhs.first(), i),
					new WListAccess(retVar, WNumerics.add(i,new WLengthOf(lhs.first()))));
			WFormula forall2 = new WBoundedForall(true,variables,wrhs);
			
				// finally, pump from retvar into left src
			i = WVariable.freshVar();
			variables = new HashMap();
			variables.put(i, retVar);
			WFormula l1 = WNumerics.lessThan(i, new WLengthOf(lhs.first()));
			WFormula r1 = WExprs.equals(new WListAccess(lhs.first(), i),
					new WListAccess(retVar, i));
			WFormula l2 = WNumerics
					.greaterThanEq(i, new WLengthOf(lhs.first()));
			WFormula r2 = WExprs.equals(new WListAccess(rhs.first(), WNumerics
					.subtract(i, new WLengthOf(lhs.first()))), new WListAccess(
					retVar, i));

			WFormula forall3 = new WBoundedForall(true, variables, WFormulas
					.and(WFormulas.implies(l1, r1), WFormulas.implies(l2, r2)));

			constraints = WFormulas.and(constraints, lenConstraints, forall1,
					forall2, forall3, WTypes.subtypeOf(retVar,
							convert(v.type())));

			return new Pair<WExpr, WFormula>(retVar, constraints);
		}
		case UNION: {
			WVariable rv = WVariable.freshVar();
			WVariable rs = WVariable.freshVar();

			HashMap<WVariable, WExpr> vars = new HashMap();
			vars.put(rv, rs);
			WSetConstructor sc = new WSetConstructor(rv);
			WFormula allc = WFormulas.or(WSets.subsetEq(sc, lhs.first()), WSets
					.subsetEq(sc, rhs.first()));
			constraints = WFormulas.and(constraints, WSets.subsetEq(
					lhs.first(), rs), WSets.subsetEq(rhs.first(), rs),
					new WBoundedForall(true, vars, allc));
			return new Pair<WExpr, WFormula>(rs, constraints);
		}
		case INTERSECT: {
			WVariable rv = WVariable.freshVar();
			WVariable rs = WVariable.freshVar();
			HashMap<WVariable, WExpr> vars = new HashMap();
			vars.put(rv, rs);				
			WSetConstructor sc = new WSetConstructor(rv);
			WFormula left = new WBoundedForall(true, vars, WFormulas.and(WSets
					.subsetEq(sc, lhs.first()), WSets.subsetEq(sc, rhs.first())));
			
			vars = new HashMap();
			vars.put(rv, lhs.first());
			WFormula right = new WBoundedForall(true, vars, WFormulas.implies(WSets
					.subsetEq(sc, rhs.first()), WSets.subsetEq(sc, rs)));
			
			constraints = WFormulas
					.and(constraints, left, right, WSets.subsetEq(rs, lhs
							.first()), WSets.subsetEq(rs, rhs.first()));

			return new Pair<WExpr, WFormula>(rs, constraints);
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
		if(type == Type.T_VOID) {
			return WVoidType.T_VOID;
		} else if(type == Type.T_BOOL) {
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
		} else if(type instanceof Type.Record) {
			Type.Record tl = (Type.Record) type;
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
