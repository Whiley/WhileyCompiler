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

package wyjc.stages;

import java.util.*;

import wyjc.ModuleLoader;
import wyjc.ast.*;
import wyjc.ast.ResolvedWhileyFile.*;
import wyjc.ast.attrs.*;
import wyjc.ast.exprs.*;
import wyjc.ast.exprs.integer.*;
import wyjc.ast.exprs.list.*;
import wyjc.ast.exprs.logic.*;
import wyjc.ast.exprs.process.*;
import wyjc.ast.exprs.real.*;
import wyjc.ast.exprs.set.*;
import wyjc.ast.exprs.tuple.*;
import wyjc.ast.stmts.*;
import wyjc.ast.types.*;
import wyjc.jvm.rt.BigRational;
import wyjc.util.*;

/**
 * The purpose of the check generator is to generate all the necessary runtime
 * checks, and add then to the code in the form of internal check statements.
 * These statements can then be eliminated by the ConditionChecker (where
 * possible), with those remaining being turned into runtime checks.
 * 
 * @author djp
 * 
 */
public class RuntimeCheckGenerator {
	private ModuleLoader loader;
	private int nchecks = 0;
	
	// The shadow set is used to (efficiently) aid the correct generation of
	// runtime checks for post conditions. The key issue is that a post
	// condition may refer to parameters of the method. However, if those
	// parameters are modified during the method, then we must store their
	// original value on entry for use in the post-condition runtime check.
	// These stored values are called "shadows".   
	private final HashSet<Variable> shadows = new HashSet<Variable>();
	
	public RuntimeCheckGenerator(ModuleLoader loader) {
		this.loader = loader;
	}
	
	public int checkgen(ResolvedWhileyFile wf) {
		nchecks = 0;
		for(Decl d : wf.declarations()) {
			if(d instanceof FunDecl) {
				checkgen((FunDecl)d);
			} 
		}
		return nchecks;
	}
	
	public void checkgen(FunDecl f) {		
		List<Stmt> f_statements = f.statements();
		HashMap<String,Condition> constraints = new HashMap<String,Condition>();
		HashMap<String,Type> environment = new HashMap<String,Type>();
		
		for(FunDecl.Parameter p : f.parameters()) {						
			constraints.put(p.name(),p.constraint());
			environment.put(p.name(),p.type());
		}
		
		determineShadows(f, environment, constraints);
		for(int i=0;i!=f_statements.size();++i) {
			Stmt s = f_statements.get(i);
			Pair<List<Check>,List<Check>> checks = checkgen(s,f, constraints, environment);
			f_statements.addAll(i,checks.first());
			i = i + checks.first().size();
			f_statements.addAll(i+1,checks.second());
			i = i + checks.second().size();			
		}
		generateShadows(f);				
	}

	/**
	 * Determine which parameters require shadows. A parameter requires a shadow
	 * if: it is used in the post-condition: and, it is modified in the method
	 * body.
	 * 
	 * @param f
	 */
	protected void determineShadows(FunDecl f,
			HashMap<String, Type> environment,
			HashMap<String, Condition> constraints) {
		shadows.clear(); // reset old shadow information
		Condition pc = f.postCondition();
		if(pc == null) {
			return;
		}
		for(Variable var : pc.uses()) {
			String v = var.name();
			if(!v.equals("$")) {
				// at this point, we want to check whether or not this variable
				// is actually modified in the method body. For now, I just
				// avoid this optimisation --- which is fine, just not as
				// efficient as it might be. 
				String shadow = v + "$_";
				shadows.add(var);
				environment.put(shadow, environment.get(v));
				constraints.put(shadow, constraints.get(v));
			}
		}		
	}
	
	/**
	 * Generate parameter shadows.
	 * @return
	 */
	protected void generateShadows(FunDecl f) {
		Condition pc = f.postCondition();
		if(pc == null) {
			return;
		}
		HashMap<String,Condition> constraints = new HashMap<String,Condition>();
		HashMap<String,Type> environment = new HashMap<String,Type>();
		
		for(FunDecl.Parameter p : f.parameters()) {						
			constraints.put(p.name(),p.constraint());
			environment.put(p.name(),p.type());
		}		
		ArrayList<Stmt> assignments = new ArrayList<Stmt>();
		for(Variable var : shadows) {
			String v = var.name();
			String shadow = v + "$_";
			VarDecl decl = new VarDecl(environment.get(v), constraints.get(v),
					shadow, var, pc.attributes());
			assignments.add(decl);
		}
		f.statements().addAll(0,assignments);
	}
	protected Pair<List<Check>,List<Check>> checkgen(Stmt s, FunDecl f,
			HashMap<String, Condition> constraints,
			HashMap<String, Type> environment) {
		List<Check> preChecks;
		List<Check> postChecks = Collections.EMPTY_LIST;
		try {
			if (s instanceof Skip 
					|| s instanceof Read				
					|| s instanceof Assertion) {
				// these statements can have no requirements
				preChecks = Collections.EMPTY_LIST;
			} else if(s instanceof Print) {
				preChecks = checkgen((Print)s,environment);
			} else if (s instanceof Assign) {
				Pair<List<Check>,List<Check>> tmp = checkgen((Assign)s, constraints, environment);
				preChecks = tmp.first();
				postChecks = tmp.second();
			} else if (s instanceof VarDecl) {
				preChecks = checkgen((VarDecl)s, constraints, environment);					
			} else if (s instanceof IfElse) {
				preChecks = checkgen((IfElse) s, f, constraints, environment);	
			} else if (s instanceof Return) {
				preChecks = checkgen((Return)s, environment, f);			
			} else if (s instanceof Invoke) {
				preChecks = checkgen((Invoke) s, environment);
			} else if (s instanceof Spawn) {
				preChecks = checkgen((Spawn) s, environment);
			} else {
				syntaxError("Unknown statement encountered: " + s, s);
				return null;
			}
		} catch(ResolveError ex) {
			syntaxError(ex.getMessage(),s);
			return null;
		}
		
		return new Pair(preChecks,postChecks);
	}	
	
	private List<Check> checkgen(VarDecl as,
			HashMap<String, Condition> constraints,
			HashMap<String, Type> environment) {
		Expr init = as.initialiser();
				
		constraints.put(as.name(), as.constraint());		
		
		if(init != null) {
			ArrayList<Check> checks = new ArrayList<Check>();
			checks.addAll(checkgen(init,environment));
										
			if (as.constraint() != null) {
				HashMap<String, Expr> binding = new HashMap<String,Expr>();
				binding.put("$", init);			
				Condition constraint = as.constraint().substitute(binding);				
				addCheck("constraint for variable " + as.name()
						+ " not satisfied", constraint, environment, as, checks);
			}						
			
			environment.put(as.name(), init.type(environment));
			
			return checks;
		} else {
			environment.put(as.name(), as.type());
		}
				
		return Collections.EMPTY_LIST;
	}
	
	private Pair<List<Check>,List<Check>> checkgen(Assign as,
			HashMap<String, Condition> constraints,
			HashMap<String, Type> environment) {
		LVal lhs = as.lhs();
		Expr rhs = as.rhs();
		
		List<Check> postChecks = Collections.EMPTY_LIST;
		List<Check> preChecks = checkgen(lhs,environment);
		preChecks.addAll(checkgen(rhs,environment));
					
		List<Expr> flat = lhs.flattern();
		Variable v = (Variable) flat.get(0);
		Condition lhs_c = constraints.get(v.name());
		if (lhs_c != null) {
			postChecks = new ArrayList<Check>();
			HashMap<String,Expr> binding = new HashMap<String,Expr>();
			binding.put("$",v);
			lhs_c = lhs_c.substitute(binding);
			addCheck("constraint for variable " + v
					+ " not satisfied", lhs_c, environment, as, postChecks);
		}

		if (lhs instanceof Variable) {			
			environment.put(v.name(), rhs.type(environment));
		}
		
		return new Pair<List<Check>,List<Check>>(preChecks,postChecks);
	}	
	
	private boolean tupleAccessEquivalent(TupleAccess t1, TupleAccess t2) {
		if(t1.name().equals(t2.name())) {
			Expr s1 = t1.source();
			Expr s2 = t2.source();
			if(s1 instanceof Variable && s2 instanceof Variable) {
				Variable v1 = (Variable) s1;
				Variable v2 = (Variable) s2;
				return v1.name().equals(v2.name());
			} else if(s1 instanceof TupleAccess && s2 instanceof TupleAccess) {
				return tupleAccessEquivalent((TupleAccess)s1,(TupleAccess)s2);
			}
		}
		
		return false;			
	}
	
	private Condition extractLocalConstraint(LVal lv, Condition constraint) {
		if(lv instanceof Variable) {
			return constraint;
		} else if(lv instanceof ListAccess && constraint instanceof None) {
			SetComprehension sc = ((None) constraint).mhs();
			if (sc.sign() instanceof Variable && sc.sources().size() == 1) {
				Condition c = sc.condition();
				String var = sc.variables().iterator().next();
				HashMap<String,Expr> binding = new HashMap<String,Expr>();
				binding.put(var,
						new Variable("$", lv.attribute(TypeAttr.class)));				
				c = c.substitute(binding);
				if(c instanceof Not) {
					return ((Not)c).mhs();
				} else {
					return new Not(c);
				}
			}
		} 
		
		// FIXME: could improve this for tuple accesses
		return null;		
	}
	
	private List<Check> checkgen(IfElse ie, FunDecl f,
			HashMap<String, Condition> constraints,
			HashMap<String, Type> environment) {
		List<Check> checks = checkgen(ie.condition(),environment);		
		List<Stmt> t_statements = ie.trueBranch();
		List<Stmt> f_statements = ie.falseBranch();
		
		if(t_statements != null) {			
			for(int i=0;i!=t_statements.size();++i) {				
				Stmt s = t_statements.get(i);
				Pair<List<Check>,List<Check>> tchecks = checkgen(s,f,constraints, environment);
				t_statements.addAll(i,tchecks.first());
				i = i + tchecks.first().size();
				t_statements.addAll(i+1,tchecks.second());
				i = i + tchecks.second().size();				
			}
		}
		
		if(f_statements != null) {
			for(int i=0;i!=f_statements.size();++i) {
				Stmt s = f_statements.get(i);
				Pair<List<Check>,List<Check>> fchecks = checkgen(s,f, constraints, environment);
				f_statements.addAll(i,fchecks.first());
				i = i + fchecks.first().size();
				f_statements.addAll(i+1,fchecks.second());
				i = i + fchecks.second().size();
			}
		}
		
		return checks;
	}
	
	private List<Check> checkgen(Print ps, HashMap<String, Type> environment) {
		return checkgen(ps.expr(),environment);
	}
	
	private List<Check> checkgen(Spawn ps, HashMap<String, Type> environment) {
		return checkgen(ps.mhs(),environment);
	}
	
	private List<Check> checkgen(ProcessAccess ps, HashMap<String, Type> environment) {
		return checkgen(ps.mhs(),environment);
	}
	
	private List<Check> checkgen(Return rs, HashMap<String, Type> environment,
			FunDecl f) {
		Expr e = rs.expr();
		
		ArrayList<Check> checks = new ArrayList<Check>();
		
		if(e == null) {
			return checks;
		} else {
			checks.addAll(checkgen(e,environment));
		}
		
		if(f.postCondition() != null) {
			Condition postCondition = f.postCondition();			
			HashMap<String,Expr> binding = new HashMap<String,Expr>();
			binding.put("$", e);
			for (Variable var : postCondition.uses()) {				
				if(shadows.contains(var)){
					String v = var.name();
					// Observe that the following is required, since the
					// parameter may have been modified in the method body.
					// However, the post condition must always refer to the
					// value on entry; therefore, we have stored that variable
					// into a shadow.
					binding.put(v, new Variable(v + "$_", var.attributes()));
				}
			}			
			Condition pc = postCondition.substitute(binding);			
			addCheck("function postcondition not satisfied",pc,environment,e,checks);								
		}		
		
		return checks;
	}
	
	private List<Check> checkgen(Invoke ivk, HashMap<String,Type> environment) throws ResolveError {
		// First, we'll check the requirements of the argument expressions.
		ArrayList<Check> checks = new ArrayList<Check>();
		List<Expr> args = ivk.arguments(); 
		for(Expr e : args) {
			checks.addAll(checkgen(e,environment));					
		}
		
		// Now, check the requirements for the method itself.
		Condition preCond = null;
		
		ModuleInfo mi = loader.loadModule(ivk.module());		
		
		FunType funType = ivk.funType();
		for(ModuleInfo.Method method : mi.method(ivk.name(),funType)) {			
			Condition cond = null;
			if(method.preCondition() != null) {
				HashMap<String,Expr> binding = new HashMap<String,Expr>();
				List<String> params = method.parameterNames();

				// first, we build the appropriate binding.
				for (int i = 0; i != params.size(); ++i) {
					String param = params.get(i);
					Expr e = args.get(i);
					binding.put(param,e);
				}

				// second, we substitute the method pre-condition using this binding
				// to generate an expression which makes sense in terms of the
				// current method.
				
				cond = (Condition) method.preCondition().substitute(binding);				
			}
	
			// finally, combine the generated condition as necessary	
			if(cond != null) {
				preCond = preCond == null ? cond : new Or(preCond,cond);
			} else {
				preCond = new BoolVal(true);
			}
		}
		
		if(preCond != null) {			
			addCheck("function precondition not satisfied",preCond,environment,ivk,checks);
		}		
		
		return checks;
	}
	
	public List<Check> checkgen(Expr e, HashMap<String,Type> environment) {		
		List<Check> checks = null;
		try {
			if (e instanceof IntVal
					|| e instanceof RealVal
					|| e instanceof BoolVal
					|| e instanceof RangeVal
					|| e instanceof Variable				
					|| e instanceof TupleAccess) {
				// These ones do nothing.
				checks = new ArrayList<Check>();
			} else if(e instanceof BinOp) {
				checks = checkgen((BinOp)e,environment);
			} else if(e instanceof IntNegate) {
				checks = checkgen((IntNegate)e,environment);
			} else if(e instanceof RealNegate) {
				checks = checkgen((RealNegate)e,environment);
			} else if(e instanceof Invoke) {
				checks = checkgen((Invoke)e,environment);			 		
			} else if(e instanceof Not) {
				checks = checkgen((Not)e,environment);
			} else if(e instanceof ListVal) {
				checks = checkgen((ListVal)e,environment);
			} else if(e instanceof ListGenerator) {
				checks = checkgen((ListGenerator)e,environment);
			} else if(e instanceof ListAccess) {
				checks = checkgen((ListAccess)e,environment);
			} else if(e instanceof ListSublist) {
				checks = checkgen((ListSublist)e,environment);
			} else if(e instanceof ListLength) {
				checks = checkgen((ListLength)e,environment);
			} else if(e instanceof SetVal) {
				checks = checkgen((SetVal)e,environment);
			} else if(e instanceof SetGenerator) {
				checks = checkgen((SetGenerator)e,environment);
			} else if(e instanceof SetComprehension) {
				checks = checkgen((SetComprehension)e,environment);
			} else if(e instanceof SetLength) {
				checks = checkgen((SetLength)e,environment);
			} else if(e instanceof None) {
				checks = checkgen((None)e,environment);
			} else if(e instanceof Some) {
				checks = checkgen((Some)e,environment);
			} else if(e instanceof ListElementOf) {
				checks = checkgen((ListElementOf)e,environment);
			} else if(e instanceof TupleVal) {				
				checks = checkgen((TupleVal)e,environment);
			} else if(e instanceof TupleGenerator) {				
				checks = checkgen((TupleGenerator)e,environment);
			} else if(e instanceof Spawn) {				
				checks = checkgen((Spawn)e,environment);
			} else if (e instanceof ProcessAccess) {
				checks = checkgen((ProcessAccess) e,environment);
			} else {
				syntaxError("Unknown expression encountered (" + e.getClass().getName() +")",e);
				return null;
			}
		} catch(ResolveError ex) {
			syntaxError(ex.getMessage(),e);
		}
		
		return checks;
	}
	
	protected static final IntVal INT_ZERO = new IntVal(0, new TypeAttr(
			Types.T_INT));
	protected static final RealVal REAL_ZERO = new RealVal(BigRational.ZERO,
			new TypeAttr(Types.T_REAL));
	
	protected List<Check> checkgen(BinOp bop, HashMap<String,Type> environment) {
		List<Check> checks = checkgen(bop.lhs(),environment);
		checks.addAll(checkgen(bop.rhs(),environment));		
		
		if(bop instanceof IntDiv) {					
			Condition c = new IntNotEquals(bop.rhs(),INT_ZERO);
			addCheck("divide by zero",c,environment,bop,checks);						
		} else if(bop instanceof RealDiv) {			
			Condition c = new RealNotEquals(bop.rhs(),REAL_ZERO);			
			addCheck("divide by zero",c,environment,bop,checks);			
		}
		return checks;
	}
	
	protected List<Check> checkgen(IntNegate e, HashMap<String,Type> environment) {
		return checkgen(e.mhs(),environment);
	}
	
	protected List<Check> checkgen(RealNegate e, HashMap<String,Type> environment) {
		return checkgen(e.mhs(),environment);
	}
	
	protected List<Check> checkgen(Not e, HashMap<String,Type> environment) {
		return checkgen(e.mhs(),environment);
	}
	
	protected List<Check> checkgen(ListVal lv, HashMap<String,Type> environment) {
		ArrayList<Check> checks = new ArrayList<Check>();
		
		for(Expr e : lv.getValues()) {
			checks.addAll(checkgen(e,environment));
		}
		
		return checks;
	}
	
	protected List<Check> checkgen(ListGenerator lv, HashMap<String,Type> environment) {
		ArrayList<Check> checks = new ArrayList<Check>();
		
		for(Expr e : lv.getValues()) {
			checks.addAll(checkgen(e,environment));
		}
		
		return checks;
	}
	
	protected List<Check> checkgen(ListAccess la, HashMap<String,Type> environment) {
		SourceAttr attr = la.attribute(SourceAttr.class);
		ArrayList<Check> checks = new ArrayList<Check>();
		
		checks.addAll(checkgen(la.source(),environment));
		checks.addAll(checkgen(la.index(),environment));
		
		Condition ltz = new IntGreaterThanEquals(la.index(),INT_ZERO);
		Condition gtel = new IntLessThan(la.index(),new ListLength(la.source()));
		addCheck("list index might be negative",ltz,environment,la,checks);
		addCheck("list index might exceed length",gtel,environment,la,checks);		
		return checks;
	}
	
	protected List<Check> checkgen(ListSublist la, HashMap<String,Type> environment) {
		SourceAttr attr = la.attribute(SourceAttr.class);
		ArrayList<Check> checks = new ArrayList<Check>();
		
		checks.addAll(checkgen(la.source(),environment));
		checks.addAll(checkgen(la.start(),environment));
		checks.addAll(checkgen(la.end(),environment));
		
		Condition ltz = new IntGreaterThanEquals(la.start(),INT_ZERO);
		Condition gtel = new IntLessThanEquals(la.end(),new ListLength(la.source()));
		Condition ord = new IntLessThanEquals(la.start(),la.end());
		addCheck("sublist start might be negative",ltz,environment,la,checks);
		addCheck("sublist end might exceed length",gtel,environment,la,checks);		
		addCheck("sublist may have negative size",ord,environment,la,checks);
		return checks;
	}
	
	protected List<Check> checkgen(ListLength e, HashMap<String,Type> environment) {
		return checkgen(e.mhs(),environment);
	}
		
	protected List<Check> checkgen(SetVal sv, HashMap<String,Type> environment) {
		ArrayList<Check> checks = new ArrayList<Check>();
		
		for(Expr e : sv.getValues()) {
			checks.addAll(checkgen(e,environment));
		}
		
		return checks;
	}
	
	protected List<Check> checkgen(SetGenerator sv, HashMap<String,Type> environment) {
		ArrayList<Check> checks = new ArrayList<Check>();
		
		for(Expr e : sv.getValues()) {
			checks.addAll(checkgen(e,environment));
		}
		
		return checks;
	}
	
	protected List<Check> checkgen(SetComprehension sc, HashMap<String,Type> environment) {
		ArrayList<Check> checks = new ArrayList<Check>();
						
		checks.addAll(checkgen(sc.sign(),environment));
		if(sc.condition() != null) {
			checks.addAll(checkgen(sc.condition(),environment));
		}
		
		for(int i=0;i!=checks.size();++i) {
			Check c = checks.get(i);
			
			HashSet<String> vars = new HashSet<String>();
			Set<String> srcs = sc.variables(); 
			for(Variable s : c.condition().uses()) {
				if(srcs.contains(s.name())) {
					vars.add(s.name());
				}
			}			
			
			if(vars.size() != 0) {
				// only update the check if it ranges over one or more variables
                // in the comprehension
				List<Pair<String,Expr>> nsources = new ArrayList<Pair<String,Expr>>();
				HashMap<String,Expr> tmap = new HashMap<String,Expr>();
				
				for(String v : vars) {
					nsources.add(new Pair<String,Expr>(v, sc.source(v)));
					tmap.put(v,new Variable(v));
				}
								
				Condition nc = new Not(c.condition());
				Expr val = vars.size() == 1 ? new Variable(vars.iterator()
						.next()) : new TupleGenerator(tmap);
				SetComprehension e = new SetComprehension(val, nsources, nc, c
						.attributes());
				checks.set(i,new Check(c.message(),new None(e),c.attributes()));
			}
		}
				
		return checks;		
	}
	
	protected List<Check> checkgen(SetLength e, HashMap<String,Type> environment) {
		return checkgen(e.mhs(),environment);
	}
	
	protected List<Check> checkgen(None e, HashMap<String,Type> environment) {
		return checkgen(e.mhs(),environment);
	}
	
	protected List<Check> checkgen(Some e, HashMap<String,Type> environment) {
		return checkgen(e.mhs(),environment);
	}
	
	protected List<Check> checkgen(ListElementOf e, HashMap<String,Type> environment) {
		List<Check> checks = checkgen(e.lhs(),environment);
		checks.addAll(checkgen(e.rhs(),environment));
		return checks;
	}
	
	protected List<Check> checkgen(TupleVal tv, HashMap<String,Type> environment) {
		ArrayList<Check> checks = new ArrayList<Check>();

		for (Map.Entry<String, Value> e : tv.values().entrySet()) {
			checks.addAll(checkgen(e.getValue(),environment));
		}

		return checks;
	}			
	
	protected List<Check> checkgen(TupleGenerator tv, HashMap<String,Type> environment) {
		ArrayList<Check> checks = new ArrayList<Check>();

		for (Map.Entry<String, Expr> e : tv.values().entrySet()) {
			checks.addAll(checkgen(e.getValue(),environment));
		}

		return checks;
	}	
	
	protected void addCheck(String msg, Condition c,
			HashMap<String, Type> environment, SyntacticElement elem,
			List<Check> checks) {		
		c = c.reduce(environment);		
		if(c instanceof BoolVal) {
			BoolVal v = (BoolVal) c;			
			if(!v.value()) {
				syntaxError(msg,elem);
			}
		} else {
			SourceAttr attr = elem.attribute(SourceAttr.class);
			checks.add(new Check(msg,c,attr));
			nchecks++;
		}
	}
	
	private static void syntaxError(String msg, SyntacticElement elem) {
		int start = -1;
		int end = -1;
		String filename = "unknown";
		
		SourceAttr attr = (SourceAttr) elem.attribute(SourceAttr.class);
		if(attr != null) {
			start=attr.start();
			end=attr.end();
			filename = attr.filename();
		}
		
		throw new SyntaxError(msg, filename, start, end);
	}
}
