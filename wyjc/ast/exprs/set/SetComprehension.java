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

package wyjc.ast.exprs.set;

import java.util.*;

import wyjc.ModuleLoader;
import wyjc.ast.attrs.Attribute;
import wyjc.ast.attrs.SourceAttr;
import wyjc.ast.attrs.SyntacticElementImpl;
import wyjc.ast.exprs.*;
import wyjc.ast.exprs.list.*;
import wyjc.ast.exprs.logic.*;
import wyjc.ast.types.*;
import wyjc.util.*;
import wyone.core.*;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;
import wyone.theory.quantifier.*;
import wyone.theory.set.*;
import static wyjc.util.SyntaxError.*;
import static wyone.theory.logic.WFormulas.*;
import static wyone.theory.numeric.WNumerics.*;

public class SetComprehension extends SyntacticElementImpl implements Expr {
	private final Expr value;
	private final List<Pair<String,Expr>> srcs;
	private final Condition condition;
	
	public SetComprehension(Expr value, List<Pair<String, Expr>> srcs,
			Condition condition, Attribute... attributes) {
		super(attributes);
		this.value = value;
		this.srcs = srcs;
		this.condition = condition;
	}

	public SetComprehension(Expr value, List<Pair<String, Expr>> srcs,
			Condition condition, Collection<Attribute> attributes) {
		super(attributes);
		this.value = value;
		this.srcs = srcs;
		this.condition = condition;
	}	
	
	public Expr sign() {
		return value;
	}

	public Type type(Map<String,Type> environment) {
		HashMap<String,Type> nenv = new HashMap(environment);
		for(Pair<String,Expr> e : srcs) {
			SetType et = (SetType) e.second().type(nenv).flattern();			
			nenv.put(e.first(), et.element());
		}
		return new SetType(value.type(nenv));
	}
	
	public List<Pair<String,Expr>> sources() {
		return srcs;
	}
	
	public Expr source(String name) {
		HashSet<String> vars = new HashSet<String>();
		for(Pair<String,Expr> s : srcs) {
			if(s.first().equals(name)) {
				return s.second();
			}
		}
		return null;
	}
	
	public Set<String> variables() {
		HashSet<String> vars = new HashSet<String>();
		for(Pair<String,Expr> s : srcs) {
			vars.add(s.first());
		}
		return vars;
	}
	
	public Condition condition() {
		return condition;
	}

	public SetComprehension substitute(Map<String, Expr> binding) {
		// One of the key challenges in this piece of code is to eliminate any
		// potential clashes. There are two situations where this can occur:
		// (1), we're renaming a variable with the same name as a src var; (2) a
		// variable is renamed to an expression containing a src var. In such
		// case, we rename the src var as necessary.
		HashMap<String, Expr> nbinding = new HashMap<String, Expr>();
		HashSet<String> uses = new HashSet<String>();
		Set<String> srcVars = variables();
		for (Map.Entry<String, Expr> e : binding.entrySet()) {
			String k = e.getKey();
			if (!srcVars.contains(k)) {
				nbinding.put(k, e.getValue());
			}
			for (Variable v : e.getValue().uses()) {
				uses.add(v.name());
			}
		}

		HashMap<String, Expr> tbinding = new HashMap<String, Expr>();
		for (Pair<String, Expr> e : srcs) {
			String k = e.first();
			if (uses.contains(k)) {				
				// this indicates a clash, so find best replacement
				int i = 0;
				String c = k + "$" + i;
				while (uses.contains(c)) {
					i = i + 1;
				}
				tbinding.put(k, new Variable(c, this
						.attribute(SourceAttr.class)));
			}
		}

		ArrayList<Pair<String, Expr>> ss = new ArrayList<Pair<String, Expr>>();

		for (Pair<String, Expr> e : srcs) {
			String k = e.first();
			Expr v = tbinding.get(k);
			if(v != null) {
				k = ((Variable)v).name();
			}
			ss.add(new Pair<String, Expr>(k, e.second().substitute(tbinding)
					.substitute(nbinding)));
		}

		Condition c = condition;
		if (c != null) {
			c = condition.substitute(tbinding).substitute(nbinding);
		}

		return new SetComprehension(value.substitute(tbinding).substitute(
				nbinding), ss, c, attributes());
	}
	public Expr replace(Map<Expr,Expr> binding) {
		Expr t = binding.get(this);
		if(t != null) {
			return t;
		} else {
			ArrayList<Pair<String,Expr>> ss = new ArrayList<Pair<String,Expr>>();
			
			for(Pair<String,Expr> e : srcs) {
				ss.add(new Pair<String, Expr>(e.first(), e.second().replace(
						binding)));
			}
			
			Condition c = condition;
			if(c != null) {
				c = (Condition) condition.replace(binding);
			}
			
			return new SetComprehension(value.replace(binding), ss, c, attributes());
		}
	}
    
    public <T> List<T> match(Class<T> match) {
		ArrayList<T> matches = new ArrayList<T>();
		for(Pair<String,Expr> e : srcs) {
			matches.addAll(e.second().match(match));			
		}		
		matches.addAll(condition.match(match));
		matches.addAll(value.match(match));
		if(match.isInstance(this)) {
			matches.add((T)this);
		}
		return matches;
	}
	
	public Set<Variable> uses() {
		HashSet<Variable> r = new HashSet<Variable>();
		
		for(Pair<String,Expr> e : srcs) {
			r.addAll(e.second().uses());
		}
				
		if(condition != null) {
			r.addAll(condition.uses());
		}
		
		Set<String> keys = variables();
		HashSet<Variable> rs = new HashSet<Variable>();
		for(Variable v : r) {
			if(!keys.contains(v.name())) {
				rs.add(v);
			}			
		}
		
		return rs;
	}
	
	public Expr reduce(Map<String, Type> environment) {
		HashMap<String,Type> nenv = new HashMap<String,Type>(environment);
		ArrayList<Pair<String,Expr>> nsrcs = new ArrayList<Pair<String,Expr>>();
		Set<String> srcvars = variables();
		boolean ready = true;
		for(Pair<String,Expr> me : srcs) {
			Expr e = (Expr) me.second().reduce(nenv);
			SetType st = (SetType) me.second().type(nenv);			
			nenv.put(me.first(), st.element());
			nsrcs.add(new Pair<String,Expr>(me.first(),e));
			ready &= e instanceof SetVal || e instanceof ListVal;
		}
		
		Condition c = condition == null ? new BoolVal(true) : condition.reduce(nenv);	
		
		HashSet<String> c_uses = new HashSet<String>();
		for(Variable v : c.uses()) {
			c_uses.add(v.name());
		}
		c_uses.removeAll(srcvars);
		ready &= c_uses.isEmpty();		
		
		Expr nvalue = value.reduce(nenv);
		Set<String> uses = new HashSet<String>();
		for(Variable v : nvalue.uses()) {
			uses.add(v.name());
		}
		uses.removeAll(srcvars);						
		ready &= uses.isEmpty();
		
		if(ready) {			
			
			// this means we can actually compute the answer.
			// Make the process of evaluating set comprehensions more efficient
			// would be very useful.   
				
			int[] counters = new int[nsrcs.size()];
			String[] vars = new String[counters.length];
			List<Value>[] data = new List[counters.length];
			int i = 0;
			
			for(Pair<String,Expr> m : nsrcs) {			
				counters[i] = 0;
				vars[i] = m.first();
				Expr ve = m.second();
				if(ve instanceof SetVal) {
					SetVal sv = (SetVal) ve;
					data[i] = new ArrayList(sv.getValues());
				} else {
					ListVal sv = (ListVal) ve;
					data[i] = new ArrayList(sv.getValues());
				}
				
				if (data[i].isEmpty()) {
					// if any of the input sources are empty, then the result is
                    // the
					// empty set.
					return new SetVal(new HashSet(), attributes());
				}
				i=i+1;
			}
						
			HashSet<Value> vals = new HashSet<Value>();
						
			do {
				HashMap<String,Expr> binding = new HashMap<String,Expr>();
				for(i=0;i!=counters.length;++i) {				
					binding.put(vars[i], data[i].get(counters[i]));
				}															
				BoolVal b = (BoolVal) condition.substitute(binding).reduce(environment);					
				if(b.value()) {				
					vals.add((Value) nvalue.substitute(binding).reduce(environment));
				}
			} while(!countersIncrement(counters,data));
			
			return new SetVal(vals,attributes());
		} else {									
			return new SetComprehension(nvalue,nsrcs,c,attributes());
		}
	}
	
	public String toString() { 
		String r = "{" + value + " : ";
		boolean firstTime=true;
		
		for(Pair<String,Expr> e : srcs) {
			if(!firstTime) {
				r += ", ";
			}
			firstTime=false;
			r += e.first() + " in " + e.second();
		}
				
		return r + " | " + condition + "}";		
	}
	
	public Triple<WExpr, WFormula, WEnvironment> convert(Map<String, Type> environment, ModuleLoader loader) throws ResolveError {
		
		// STEP 1 --- forward direction
		// FIXME: forward direction is definitely broken.				
		
		HashMap<WVariable,WExpr> variables = new HashMap();
		HashMap<String,Type> nenv = new HashMap<String,Type>(environment);
		WEnvironment wenv = new wyone.util.WHashEnv();
		WFormula cond = WBool.TRUE;
		WFormula constraints = WBool.TRUE;
		WVariable rv = WVariable.freshVar(); // return set
		wenv.put(rv.name(),type(environment).convert());
		
		for (Pair<String, Expr> src : srcs) {
			WVariable v = new WVariable(src.first());
			Triple<WExpr, WFormula, WEnvironment> re = src.second().convert(nenv, loader);
			SetType st = (SetType) src.second().type(nenv);			
			wenv.putAll(re.third());
			nenv.put(v.name(), st.element());
			variables.put(v,re.first());			
			constraints = WFormulas.and(constraints,re.second());
		}

		if(condition != null) {
			Triple<WFormula, WFormula, WEnvironment> fp = condition.convertCondition(nenv, loader);
			cond = WFormulas.and(cond,fp.first(),fp.second());
		}
		
		Triple<WExpr, WFormula, WEnvironment> val = value.convert(nenv, loader);			
		cond = WFormulas.and(cond,val.second());
		
		WFormula formula = WFormulas.implies(cond, WSets.subsetEq(new WSetConstructor(val.first()), rv));
		constraints = WFormulas.and(constraints, new WBoundedForall(true, variables,
				formula));
		
		// Step 2 --- reverse direction
		//
		// Note, in the following the existential is required because the
		// theorem prover currently cannot matched arbitrary values in
		// predicates. Ideally, it will be removed at some point.
				
		WVariable valvar = WVariable.freshVar();
		HashMap<WVariable,WExpr> nvariables = new HashMap();
		nvariables.put(valvar,rv); // FIXME: type		
		cond = new WBoundedForall(false, variables, WFormulas.and(cond, WExprs
				.equals(valvar, val.first())));
					
		constraints = WFormulas.and(constraints, new WBoundedForall(true, nvariables,
				cond));

		
		return new Triple<WExpr,WFormula,WEnvironment>(rv, constraints, wenv);
	}  
	
	private boolean countersIncrement(int[] counters, Collection[] data) {
		for (int i = 0; i != counters.length; ++i) {
			counters[i] = counters[i] + 1;
			if (counters[i] < data[i].size()) {
				return false;
			} else {
				counters[i] = 0;
			}
		}
		return true;
	}	
	
	public int hashCode() {
		return value.hashCode() + srcs.hashCode() + condition.hashCode();
	}
	
	public boolean equals(Object o) {
		if(o instanceof SetComprehension) {
			SetComprehension sc = (SetComprehension) o;
			if(value == null && sc.value != null) {
				return false;
			} else {
				return srcs.equals(sc.srcs) && value.equals(sc.value)
					&& condition.equals(sc.condition);
			}
		}
		return false;
	}
}
