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
import wyjc.ast.exprs.logic.*;
import wyjc.ast.stmts.*;
import wyjc.ast.types.*;
import wyjc.ast.types.unresolved.UserDefType;
import wyjc.util.*;
import wyone.core.*;
import wyone.theory.congruence.*;
import wyone.theory.list.WListType;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;
import wyone.theory.quantifier.*;
import wyone.theory.set.*;
import wyone.theory.tuple.WTupleType;
import wyone.theory.type.WTypes;
import static wyjc.util.SyntaxError.*;
import static wyone.theory.logic.WFormulas.*;
import static wyone.theory.numeric.WNumerics.*;

/**
 * <p>
 * Condition checking is the process of, for each method, assuming the
 * pre-condition for the method and ensuring its post-condition holds, including
 * those of any statements it contains. Thus, it is a more detailed form of
 * checking than simple type checking. For example, consider the following
 * method:
 * </p>
 * 
 * <pre>
 * int min(int x, int y) requires x &gt; 1, ensures $ &gt; 2
 * {
 *   return x;
 * }
 * </pre>
 * 
 * <p>
 * This method would pass <i>type checking</i>, since the returned value is of
 * type <code>int</code>. However, it will fail condition checking, since the
 * method does not always ensure that the return value is greater than 2.
 * </p>
 * <p>
 * In order to perform the detailed checking required, this stages employs an <a
 * href="http://en.wikipedia.org/wiki/Satisfiability_Modulo_Theories">SMT solver</a>.
 * This component accepts formulas written in first-order logic and determines
 * whether or not they are satisfiable.
 * </p>
 * 
 * @author djp
 */
public class VerificationConditionGenerator {
	private final ModuleLoader loader;
	private final Simplifier simplifier = new Simplifier();
	private final HashMap<String,Expr> constants = new HashMap<String,Expr>();
	private final HashMap<String,Condition> types = new HashMap<String,Condition>();

	public VerificationConditionGenerator(ModuleLoader loader) {
		this.loader = loader;
	}
	
	public void generate(ResolvedWhileyFile wf) {		
		for(Decl d : wf.declarations()) {
			if(d instanceof FunDecl) {
				generate((FunDecl)d);
			} else if(d instanceof ConstDecl) {
				ConstDecl dd = (ConstDecl) d;
				constants.put(dd.name(), dd.constant());
			} else if(d instanceof TypeDecl) {				
				TypeDecl dd = (TypeDecl) d;				
				Condition c = flatternConstraints(dd.type());

				if(c != null) {				
					types.put(dd.name(), c);
				}									
			}

		}
	}
	
	/**
	 * The purpose of this method is to check that the method's postcondition,
	 * and that of any statements it contains, is implied by the precondition.
	 * 
	 * @param f
	 *             Method in question.
	 * @return
	 */
	public void generate(FunDecl f) {
		HashMap<String,Type> environment = new HashMap<String,Type>();
		
		for(FunDecl.Parameter p : f.parameters()) {											
			environment.put(p.name(), p.type());						
		}
		
		if(f.receiver() != null) {
			environment.put("this", f.receiver().type());
		}
						
		for(Stmt s : f.statements()) {			
			generate(s,environment);								
		}		
	}
	
	protected int shadow_label = 0;
	protected void generate(Stmt s, HashMap<String,Type> environment) {
		try {
			if(s instanceof VarDecl) {
				VarDecl vd = (VarDecl) s;
				if(vd.initialiser() != null) {
					environment.put(vd.name(), vd.initialiser().type(environment));
				} else {
					environment.put(vd.name(), vd.type());
				}
			} else if(s instanceof Assign) {
				Assign a = (Assign) s;
				String var;
				var = ((Variable) a.lhs().flattern().get(0)).name();
				String shadowVar = var + "$" + shadow_label++;
				environment.put(shadowVar, environment.get(var));
				if (a.lhs() instanceof Variable) {
					Variable v = (Variable) a.lhs();
					Type rhs_t = a.rhs().type(environment);
					environment.put(v.name(), rhs_t);
				}
			} else if (s instanceof Check) {
				Check c = (Check) s;
				
				// First, extract the precondition previously generated.
				PreConditionAttr attr = s.attribute(PreConditionAttr.class);
				// Second, convert it into a pair of formulas
				Pair<WFormula,WFormula> pcPair = attr.preCondition().convertCondition(environment, loader);																						
				
				// Third, construct a single preCondition formula
				WFormula preCondition = and(pcPair.first(), pcPair
						.second());								
				
				// Now, convert the expression being checked to a formula
				Condition check = simplifier.simplify(new Not(c.condition()));					
				Pair<WFormula,WFormula> pcs = check.convertCondition(environment, loader);											
		
				// Generate initial verification condition.
				WFormula vc = and(preCondition,pcs.second(),pcs.first());																
				
				// Now, generate the typing constrains
				Set<Variable> uses = check.uses();
				uses.addAll(attr.preCondition().uses());
				vc = addEnvironment(vc,uses,environment);
				
				// Simplify vc where possible.							
//				vc = simplify(vc);				
				
				// Now, add the vc as an attribute
				s.attributes().add(
						new VerificationConditionAttr(vc));
			} else if (s instanceof IfElse) {
				// FIXME: bug here for recombining environments
				IfElse ife = (IfElse) s;
				for (Stmt ts : ife.trueBranch()) {
					generate(ts, environment);
				}
				if (ife.falseBranch() != null) {
					for (Stmt fs : ife.falseBranch()) {
						generate(fs, environment);
					}
				}
			}
		} catch(SyntaxError ex) {
			throw ex;
		} catch(Exception ex) {
			syntaxError("internal failure",s,ex);
		}
	}
	
	protected WFormula addEnvironment(WFormula vc, Set<Variable> uses,
			HashMap<String, Type> environment) {		
		for (Variable v : uses) {
			Type t = environment.get(v.name());
			vc = WFormulas.and(WTypes.subtypeOf(new WVariable(v.name()), t
					.convert()), vc);
		}
		return vc;
	}
	
	protected HashSet<String> usedPreds(WFormula f) {
		if (f instanceof WBool
				|| f instanceof WEquality || f instanceof WInequality
				|| f instanceof WSubsetEq) {
			return new HashSet<String>();
		} else if (f instanceof WConjunct) {
			WConjunct c = (WConjunct) f;
			HashSet<String> r = new HashSet<String>();
			for(WFormula dl : c) {
				r.addAll(usedPreds(dl));
			}
			return r;
		} else if(f instanceof WDisjunct) {
			WDisjunct d = (WDisjunct) f;
			HashSet<String> r = new HashSet<String>();
			for(WFormula cl : d) {
				r.addAll(usedPreds(cl));
			}
			return r;
		} else if(f instanceof WForall) {
			return usedPreds(((WForall)f).formula());
		} else if(f instanceof WBoundedForall) {
			return usedPreds(((WBoundedForall)f).formula());
		} else if(f instanceof WPredicate) {
			WPredicate p = (WPredicate) f;
			HashSet<String> r = new HashSet<String>();
			r.add(p.name());
			return r;
		} else {
			throw new RuntimeException(
					"internal error --- unknown formula encountered: "
							+ f.getClass().getName());			
		}
	}
		
	private static WType convert(Type t, SyntacticElement elem) {
		if(t == Types.T_BOOL) {
			return WBoolType.T_BOOL;
		} else if(t == Types.T_INT) {
			return WIntType.T_INT;
		} else if (t == Types.T_REAL) {
			return WRealType.T_REAL;
		} else if (t instanceof ListType) {
			ListType lt = (ListType) t;
			return new WListType(convert(lt.element(),elem));
		} else if (t instanceof SetType) {
			SetType lt = (SetType) t;
			return new WSetType(convert(lt.element(),elem));
		} else if(t instanceof TupleType) {
			TupleType tt = (TupleType) t;
			ArrayList<String> fields = new ArrayList<String>(tt.types().keySet());
			Collections.sort(fields);
			ArrayList<wyone.util.Pair<String,WType>> types = new ArrayList();
			for(String field : fields) {
				WType ft = convert(tt.get(field),elem);
				types.add(new wyone.util.Pair(field,ft));
			}
			return new WTupleType(types);
		} else if(t instanceof ProcessType) {
			ProcessType pt = (ProcessType) t;			
			return convert(pt.element(),elem);
		} else {
			return WIntType.T_INT; // something of a hack
		}
	}
	
	/*
	protected Formula simplify(Formula f) {
		
		// The purpose of this method is to try and simplify the verification
        // condition as much as possible using simple rewrites. This serves two
        // functions:
		//
		// 1) It helps the theorem prover!
		// 2) It helps the user reading the verification conditions.
		boolean changed = true;
		
		while(changed) {		
			changed = false;				
			if(f instanceof Conjunct) {
				Conjunct c = (Conjunct) f;
				ArrayList<Pair<String,String>> eqs = new ArrayList();
				
				simplifyPredicates(c,eqs);				
				
				Formula nf = f;
				
				HashMap<String,String> binding = new HashMap<String,String>();
				
				for(Pair<String,String> p : eqs) {
					String x = p.first();
					String y = p.second();
										
					if(x.startsWith("$")) {
						String t = x;
						x = y;
						y = t;
					} 
					
					String t = binding.get(y);
					t = t == null ? y : t;
					
					if(!t.equals(x)) {
						binding.put(x,t);
					}
				}
				
				if(binding.size() != 0) { 
					nf = nf.substitute(binding);															
				}
				
				changed = nf != f;
				f = nf;
			}
		}
		
		return f;
	}
*/
	/*
	protected void simplifyEqualities(Conjunct c, ArrayList<Pair<String,String>> eqs) {		
		HashMap<Polynomial,ArrayList<String>> eqclasses = new HashMap();
		
		// The following implements something similar to a hash-join operation.
        // The reason for this is that it dramatically improves performance.
		
		for(DisjunctLiteral dl : c) {
			if(dl instanceof Equality) {
				Equality eq = (Equality) dl;
				Polynomial eq_lhs = eq.lhs();
				Polynomial eq_rhs = eq.rhs();
	
				if(!eq.sign()) { continue; }
				
				if(eq_lhs.isVariable()) {
					String v = eq_lhs.variable();
					ArrayList<String> eqclass = eqclasses.get(eq_rhs);
					if(eqclass == null) {
						eqclass = new ArrayList();
						eqclasses.put(eq_rhs,eqclass);
					}
					eqclass.add(v);
				} 

				if(eq_rhs.isVariable()) {
					String v = eq_rhs.variable();
					ArrayList<String> eqclass = eqclasses.get(eq_lhs);
					if(eqclass == null) {
						eqclass = new ArrayList();
						eqclasses.put(eq_lhs,eqclass);
					}
					eqclass.add(v);
				}

			}
		}
						
		// now, we simply combine all of the equivalence classes where possible.
		for(Map.Entry<Polynomial,ArrayList<String>> e : eqclasses.entrySet()) {
			ArrayList<String> ss = e.getValue();
			Polynomial p = e.getKey();
			
			if(p.isVariable()) {
				String s1 = p.variable();
				for(String s2 : ss) {
					if(!s1.equals(s2)) {						
						eqs.add(new Pair<String,String>(s1,s2));
					}
				}
			} else {
				for(String s1 : ss) {
					for(String s2 : ss) {
						if(!s1.equals(s2)) {							
							eqs.add(new Pair<String,String>(s1,s2));
						}
					}
				}
			}			
		}
	}
	
	
	protected void simplifyPredicates(Conjunct c, ArrayList<Pair<String,String>> eqs) {				
		for(DisjunctLiteral dl : c) {
			if(dl instanceof Predicate) {
				Predicate p = (Predicate) dl;		

				if(p.function().equals("get") || p.function().equals("length")) {					
					for(Predicate m : matches(p,c)) {						
						eqs.add(new Pair(p.parameters().get(0),m.parameters().get(0)));
					}			
				} 
			}					 				
		}		
	}
	
	protected List<Predicate> matches(Predicate p, Formula f) {
		ArrayList<Predicate> r = new ArrayList();
		
		if(f instanceof Predicate) {
			Predicate fp = (Predicate) f;
			
			if(!fp.equals(p) && fp.function().equals(p.function())) {
				boolean failed = false;
				List<String> fp_parameters = fp.parameters();
				List<String> p_parameters = p.parameters();
				for(int i=1;i!=fp_parameters.size();++i) {
					String fpp = fp_parameters.get(i);
					String pp = p_parameters.get(i);
					if(!fpp.equals(pp)) {
						failed = true;
						break;
					}
				}
				if(!failed) {					
					r.add(fp); // found a match!
				}
			}			
		} else if(f instanceof Conjunct) {
			Conjunct c = (Conjunct) f;
			for(DisjunctLiteral l : c) {
				r.addAll(matches(p,l));
			}
		} 
		
		return r; 
	}
	*/
	public Condition flatternConstraints(Type t) {
		if(t instanceof UserDefType) {
			UserDefType dt = (UserDefType) t;
			return types.get(dt.name());
		} else if(t instanceof UnionType) {
			UnionType ut = (UnionType) t;
			Condition c = null;
			for(Type lb : ut.types()) {
				if(c == null) {
					c = flatternConstraints(lb);
				} else {
					Condition nc = flatternConstraints(lb);
					if(nc != null) {
						c = new Or(c,nc);
					}
				}
			}
			return c;
		} 
		
		// FIXME: need to add IS_TYPE constraint
		return null;
	}	
}
