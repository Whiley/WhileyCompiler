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

package wyil.stages;

import java.util.*;

import wyjc.ast.ResolvedWhileyFile;
import wyjc.ast.ResolvedWhileyFile.*;
import wyjc.ast.attrs.*;
import wyjc.ast.exprs.*;
import wyjc.ast.exprs.integer.IntEquals;
import wyjc.ast.exprs.integer.IntVal;
import wyjc.ast.exprs.list.*;
import wyjc.ast.exprs.logic.*;
import wyjc.ast.exprs.process.*;
import wyjc.ast.exprs.real.*;
import wyjc.ast.exprs.set.*;
import wyjc.ast.exprs.tuple.*;
import wyjc.ast.stmts.*;
import wyjc.ast.types.*;
import wyjc.lang.Attribute;
import wyjc.lang.SourceAttr;
import wyjc.util.*;
import static wyjc.util.SyntaxError.*;

public class PostConditionGenerator {
	
	public void generate(ResolvedWhileyFile wf) {	
		for(Decl d : wf.declarations()) {
			if(d instanceof FunDecl) {
				generate((FunDecl)d);
			} 
		}
	}
		
	protected void generate(FunDecl f) {				
		Condition condition = null;
		if(f.precondition() != null) {
			condition = Exprs.splitPreCondition(f.precondition());			
		}
		HashMap<String,Type> environment = new HashMap<String,Type>();
				
		for(FunDecl.Parameter p : f.parameters()) {											
			environment.put(p.name(), p.type());
			Condition c = Types.expandConstraints(p.type());
			// i'm not complete sure where the best place to do this is. It
			// could actually be done later on, at the point of generating the
			// VC for a given condition.
			if(c != null) {
				HashMap<String,RVal> binding = new HashMap<String,RVal>();
				binding.put("$", new Variable(p.name(),p.attribute(SourceAttr.class)));
				c = c.substitute(binding);
				condition = Types.and(condition,c);
			}
		}
		if(f.receiver() != null) {
			environment.put("this",f.receiver().type());
		}
		
		if (condition == null) {			
			condition = new BoolVal(true);
		}
			
		for (Stmt s : f.statements()) {
			condition = infer(s, environment, f, condition);
		}				
	}
		
	protected Condition infer(Stmt s, HashMap<String, Type> environment,
			FunDecl f, Condition preCondition) {		
		// Simplify the precondition as much as possible.
		preCondition = preCondition.reduce(environment);
		preCondition = Exprs.simplify(preCondition);
		
		s.attributes().add(new PreConditionAttr(preCondition));
		Condition postCondition;
		
		try {			
			if (s instanceof Skip) {
				postCondition = infer((Skip)s,environment, f, preCondition);
			} else if(s instanceof Print) {
				postCondition = infer((Print)s,environment, f, preCondition);
			} else if (s instanceof Assign) {
				postCondition = infer((Assign)s, environment, f, preCondition);			
			} else if (s instanceof IfElse) {
				postCondition = infer((IfElse)s,environment,f, preCondition);	
			} else if (s instanceof UnresolvedType) {
				postCondition = infer((UnresolvedType)s,environment,f, preCondition);			
			} else if (s instanceof Assertion) {
				postCondition = infer((Assertion) s,environment, f, preCondition);
			} else if (s instanceof Check) {
				postCondition = infer((Check) s,environment, f, preCondition);
			} else if (s instanceof Invoke) {
				postCondition = infer((Invoke) s, environment, f, preCondition);
			} else if (s instanceof VarDecl) {
				postCondition = infer((VarDecl) s, environment, f, preCondition);
			} else if (s instanceof Spawn) {
				postCondition = infer((Spawn) s, environment, f, preCondition);
			} else {
				syntaxError("unknown statement encountered: " + s.getClass().getName(), s);
				return null;
			}
		} catch(SyntaxError se) {
			throw se;
		} catch(Exception e) {
			syntaxError("internal failure - " + e.getMessage(),s,e);		
			return null;
		}
		
		s.attributes().add(new PostConditionAttr(postCondition));
		return postCondition;
	}
	
	protected Condition infer(VarDecl s, HashMap<String, Type> environment,
			 FunDecl f, Condition condition) {
		
		RVal init = s.initialiser();
		if(init != null) {
			Type itype = init.type(environment);
			Type type = flattern(itype);
			
			Condition eq = equate(new Variable(s.name()),init, type, s.attributes());			
			condition = new And(condition,eq);
			environment.put(s.name(), itype);
		}
		
		return condition;
	}

	protected int shadow_label = 0;
	protected Condition infer(Assign s, HashMap<String, Type> environment,
			 FunDecl f, Condition condition) {			
		LVal lhs = s.lhs();
		RVal rhs = s.rhs();
		Type rhs_t = s.rhs().type(environment);		
		
		Variable v;		
		if(lhs instanceof Variable) {			
			v = (Variable) lhs;						
			environment.put(v.name(), rhs_t);
		} else if (lhs instanceof TupleAccess || lhs instanceof ListAccess) {			
			List<RVal> exprs = lhs.flattern(); 
			v = (Variable) exprs.get(0);
			
			// The following is a critical check to prevent inference of state
            // changes in processes other than the special receiver "this"			
			for(RVal e : exprs) {
				if(e instanceof ProcessAccess) {	
					ProcessAccess pa = (ProcessAccess) e;
					RVal mhs = pa.mhs();
					if(!isThis(mhs)) {
						return condition;
					}
				}
			}
		} else {
			syntaxError("assignment of " + rhs_t
					+ " types needs implementing",s);			
			return null;
		}
		
		
		// Now, create shadow
		String var = v.name();
		Variable shadowVar = new Variable(var + "$" + shadow_label++,v.attributes());
		environment.put(shadowVar.name(), environment.get(v.name()));
		HashMap<String,RVal> binding = new HashMap<String,RVal>();
		binding.put(var,shadowVar);
		condition = condition.substitute(binding);
		
		if(lhs instanceof TupleAccess || lhs instanceof ListAccess) {
			condition = new And(condition, updateCondition(lhs, shadowVar,
					binding, environment, s.attributes()));
		}
		
		rhs = rhs.substitute(binding);
		
		// Finally, construct equality constraint
		condition = new And(condition,equate(lhs,rhs,rhs_t,s.attributes()));
				
		return condition;		
	}
	
	protected boolean isThis(RVal e) {
		return e instanceof Variable && ((Variable)e).name().equals("this");
	}
	
	protected Condition equate(RVal lhs, RVal rhs, Type type,
			List<Attribute> attributes) {
		if (type instanceof BoolType) {
			return new BoolEquals(lhs, rhs, attributes);
		} else if (type instanceof IntType) {
			return new IntEquals(lhs, rhs, attributes);
		} else if (type instanceof RealType) {
			return new RealEquals(lhs, rhs, attributes);
		} else if (type instanceof TupleType) {
			return new TupleEquals(lhs, rhs, attributes);
		} else if (type instanceof ListType) {
			return new ListEquals(lhs, rhs, attributes);
		} else if (type instanceof SetType) {
			return new SetEquals(lhs, rhs, attributes);
		} else {
			// Note, i use integer here for unknown types. This works as well as
			// any.
			return new IntEquals(lhs, rhs, attributes);			
		}
	}
	
	protected Condition updateCondition(LVal lhs, Variable shadow,
			HashMap<String, RVal> binding, HashMap<String, Type> environment,
			List<Attribute> attributes) {
		// the purpose of this method is to generate a condition which
		// identifies that everything in the lhs base variable is identical to
		// that in the shadowVariable, except for the particular element being
		// updated.
		List<RVal> exprs = lhs.flattern();				
		Condition c = new BoolVal(true);
		IntVal zero = new IntVal(0);
		
		for (int i = 1; i < exprs.size(); ++i) {			
			RVal access = exprs.get(i);			
			if (access instanceof ListAccess) {				
				ListAccess la = (ListAccess) access;
				Type type = access.type(environment);
				RVal nsrc = la.source().substitute(binding);
				String idx = wyone.core.WVariable.freshVar().name();
				Variable idx_v = new Variable(idx); // hack
				ArrayList<Pair<String, RVal>> srcs = new ArrayList();
				srcs.add(new Pair(idx, new RangeGenerator(zero, new ListLength(
						nsrc))));
				Condition cond = equate(new ListAccess(la.source(), idx_v),
						new ListAccess(nsrc, idx_v), type, attributes);
				cond = new Or(cond,new IntEquals(idx_v,la.index()));
				cond = Exprs.simplify(new Not(cond));
				c = new And(c, new None(new SetComprehension(zero, srcs, cond)));
				// finally assert lists are same length
				c = new And(c, new IntEquals(new ListLength(nsrc),
						new ListLength(la.source()), attributes));
			} else if(access instanceof TupleAccess){
				TupleAccess ta = (TupleAccess) access;
				TupleType tt = (TupleType) ta.source().type(environment).flattern();
				RVal nsrc = ta.source().substitute(binding);											
				for (String name : tt.types().keySet()) {
					if (!name.equals(ta.name())) {	
						Type type = tt.get(name);
						Condition eq = equate(new TupleAccess(nsrc, name),
								new TupleAccess(ta.source(), name), type,
								attributes);
						c = new And(c,eq);
					}
				}				
			}
		}
		
		return c;
	}
	
	protected Condition infer(UnresolvedType r, HashMap<String, Type> environment,
			 FunDecl f, Condition condition) {			
		return new BoolVal(false);		
	}
	
	protected Condition infer(Check c, HashMap<String, Type> environment,
			 FunDecl f, Condition condition) {			
		return condition;		
	}
	
	protected Condition infer(Skip c, HashMap<String, Type> environment,
			 FunDecl f, Condition condition) {			
		return condition;		
	}
	
	protected Condition infer(Print p, HashMap<String, Type> environment,
			 FunDecl f, Condition condition) {			
		return condition;		
	}
	
	protected Condition infer(Spawn p, HashMap<String, Type> environment,
			 FunDecl f, Condition condition) {			
		return condition;		
	}
	
	protected Condition infer(Invoke i, HashMap<String, Type> environment,
			 FunDecl f, Condition condition) {					
		// need to include method post condition here		
		return condition;		
	}
	
	protected Condition infer(Assertion s, HashMap<String, Type> environment,
			 FunDecl f, Condition condition) {			
		return new And(condition,s.condition());			
	}
	
	protected Condition infer(IfElse s, HashMap<String, Type> environment,
			 FunDecl f, Condition condition) {			
		Condition tCond = new And(condition,s.condition());
		Condition fCond = new And(condition,new Not(s.condition()));
		
		for(Stmt ts : s.trueBranch()) {
			tCond = infer(ts,environment,f,tCond);
		}
		if(s.falseBranch() != null) {			
			for(Stmt ts : s.falseBranch()) {
				fCond = infer(ts,environment,f,fCond);				
			}	
		}
				
		return new Or(tCond,fCond);						
	}		
	

	protected Type flattern(Type t) {
		if(t instanceof NamedType) {
			return ((NamedType)t).type();
		}
		return t;
	}
}
