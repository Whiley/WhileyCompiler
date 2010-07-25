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

import static wyil.util.SyntaxError.*;
import wyil.ModuleLoader;
import wyil.util.*;
import wyil.lang.*;
import wyjc.lang.*;
import wyjc.lang.WhileyFile.*;
import wyjc.util.*;

public class NameResolution {
	private final ModuleLoader loader;
	
	public NameResolution(ModuleLoader loader) {
		this.loader = loader;
	}
	
	public void resolve(WhileyFile wf) {
		ArrayList<PkgID> imports = new ArrayList<PkgID>();
		
		ModuleID id = wf.module;
		
		imports.add(id.pkg().append(id.module()));
		imports.add(id.pkg().append("*"));
		imports.add(new PkgID(new String[]{"whiley","lang"}).append("*"));
						
		for(Decl d : wf.declarations) {			
			try {
				if(d instanceof ImportDecl) {
					ImportDecl impd = (ImportDecl) d;
					imports.add(0,new PkgID(impd.pkg));
				} else if(d instanceof FunDecl) {
					resolve((FunDecl)d,imports);
				} else if(d instanceof TypeDecl) {
					resolve((TypeDecl)d,imports);					
				} else if(d instanceof ConstDecl) {
					resolve((ConstDecl)d,imports);					
				}
			} catch(ResolveError ex) {
				syntaxError(ex.getMessage(),d);
			}
		}				
	}
	
	protected void resolve(ConstDecl td, ArrayList<PkgID> imports) {
		resolve(td.constant,new HashSet<String>(), imports);		
	}
	
	protected void resolve(TypeDecl td, ArrayList<PkgID> imports) throws ResolveError {
		try {
			resolve(td.type, imports);			
			if(td.constraint != null) {
				resolve(td.constraint,imports);
			}		
		} catch (ResolveError e) {												
			// Ok, we've hit a resolution error.
			syntaxError(e.getMessage(), td);			
		}
	}
	
	protected void resolve(FunDecl fd, ArrayList<PkgID> imports) {
		HashSet<String> environment = new HashSet<String>();
		
		// method parameter types
		for (WhileyFile.Parameter p : fd.parameters) {
			try {
				resolve(p.type, imports);
				environment.add(p.name());
			} catch (ResolveError e) {												
				// Ok, we've hit a resolution error.
				syntaxError(e.getMessage(), p, e);
			}
		}
		
		if(fd.receiver != null) {
			environment.add("this");
		}
		
		// method return type
		try {
			resolve(fd.returnType().type(), imports);
		} catch (ResolveError e) {
			// Ok, we've hit a resolution error.
			syntaxError(e.getMessage(), fd.returnType());
		}
		
		// method receiver type (if applicable)
		try {
			if(fd.receiver() != null) {
				resolve(fd.receiver().type(), imports);
			}
		} catch (ResolveError e) {
			// Ok, we've hit a resolution error.
			syntaxError(e.getMessage(), fd.receiver());
		}
			
		if(fd.constraint() != null) {
			environment.add("$");
			Condition c = (Condition) resolve(fd.constraint(), environment,imports);
			fd.setConstraint(c);
			environment.remove("$");
		}
		
		List<Stmt> stmts = fd.statements();
		for (int i=0;i!=stmts.size();++i) {
			Stmt s = resolve(stmts.get(i), environment, imports);
			stmts.set(i,s);					
		}
	}
	
	public Stmt resolve(Stmt s, HashSet<String> environment, ArrayList<PkgID> imports) {
		try {
			if(s instanceof Skip) {
				return s;
			} else if(s instanceof UnresolvedVarDecl) {
				return resolve((UnresolvedVarDecl)s, environment, imports);
			} else if(s instanceof Assign) {
				return resolve((Assign)s, environment, imports);
			} else if(s instanceof Assertion) {
				return resolve((Assertion)s, environment, imports);
			} else if(s instanceof Print) {
				return resolve((Print)s, environment, imports);
			} else if(s instanceof IfElse) {
				return resolve((IfElse)s, environment, imports);
			} else if(s instanceof UnresolvedType) {
				return resolve((UnresolvedType)s, environment, imports);
			} else if(s instanceof Invoke) {
				return (Stmt) resolve((Invoke)s, environment, imports);
			} else if(s instanceof Spawn) {
				return (Stmt) resolve((UnOp)s, environment, imports);
			} else {
				syntaxError("unknown statement encountered: "
						+ s.getClass().getName(), s);
				return null;
			}
		} catch (ResolveError e) {
			// Ok, we've hit a resolution error.
			syntaxError(e.getMessage(), s);
			return null;
		}
	}
	
	protected Stmt resolve(UnresolvedVarDecl s, HashSet<String> environment,
			ArrayList<PkgID> imports) throws ResolveError {
		Expr init = s.initialiser();
		resolve(s.type(), imports);
		if(init != null) {
			init = resolve(init,environment, imports);
		}
		environment.add(s.name());
		return new UnresolvedVarDecl(s.type(),s.name(),init,s.attributes());
	}
	
	protected Stmt resolve(Assign s, HashSet<String> environment,
			ArrayList<PkgID> imports) {
		LVal lhs = (LVal) resolve(s.lhs(), environment, imports);
		Expr rhs = resolve(s.rhs(), environment, imports);
		return new Assign(lhs, rhs, s.attributes());
	}

	protected Stmt resolve(Assertion s, HashSet<String> environment,
			ArrayList<PkgID> imports) {
		Condition c = (Condition) resolve(s.condition(), environment, imports);
		return new Assertion(c, s.attributes());
	}

	protected Stmt resolve(UnresolvedType s, HashSet<String> environment,
			ArrayList<PkgID> imports) {
		Expr e = s.expr();
		if (e != null) {
			e = resolve(e, environment, imports);
		}
		return new UnresolvedType(e, s.attributes());
	}

	protected Stmt resolve(Print s, HashSet<String> environment,
			ArrayList<PkgID> imports) {
		Expr e = resolve(s.expr(), environment, imports);
		return new Print(e, s.attributes());
	}

	protected Stmt resolve(IfElse s, HashSet<String> environment,
			ArrayList<PkgID> imports) {
		ArrayList<Stmt> tb = new ArrayList<Stmt>();
		ArrayList<Stmt> fb = null;
		for (Stmt st : s.trueBranch()) {
			tb.add(resolve(st, environment, imports));
		}
		if (s.falseBranch() != null) {
			fb = new ArrayList<Stmt>();
			for (Stmt st : s.falseBranch()) {
				fb.add(resolve(st, environment, imports));
			}
		}

		Condition cond = (Condition) resolve(s.condition(), environment, imports);

		return new IfElse(cond, tb, fb, s.attributes());
	}
	
	protected Expr resolve(Expr e, HashSet<String> environment, ArrayList<PkgID> imports) {
		try {
			if (e instanceof Value) {
				return e;
			} else if (e instanceof Variable) {
				return resolve((Variable)e, environment, imports);
			} else if (e instanceof BinOp) {
				return resolve((BinOp)e, environment, imports);
			} else if (e instanceof UnOp) {
				return resolve((UnOp)e, environment, imports);
			} else if (e instanceof Invoke) {
				return resolve((Invoke)e, environment, imports);
			} else if (e instanceof RangeGenerator) {
				return resolve((RangeGenerator) e, environment, imports);
			} else if (e instanceof ListGenerator) {
				return resolve((ListGenerator) e, environment, imports);
			} else if (e instanceof ListAccess) {
				return resolve((ListAccess) e, environment, imports);
			} else if (e instanceof ListSublist) {
				return resolve((ListSublist) e, environment, imports);
			} else if (e instanceof SetVal) {
				return resolve((SetVal) e, environment, imports);
			} else if (e instanceof SetGenerator) {
				return resolve((SetGenerator) e, environment, imports);
			} else if (e instanceof SetComprehension) {
				return resolve((SetComprehension) e, environment, imports);
			} else if (e instanceof TupleVal) {
				return resolve((TupleVal) e, environment, imports);
			} else if (e instanceof TupleGenerator) {
				return resolve((TupleGenerator) e, environment, imports);
			} else if (e instanceof TupleAccess) {
				return resolve((TupleAccess) e, environment, imports);
			} else if (e instanceof UnresolvedTypeEquals) {
				return resolve((UnresolvedTypeEquals) e, environment, imports);
			} else {				
				syntaxError("unknown expression encountered: "
							+ e.getClass().getName(), e);				
				return null;
			}
		} catch(ResolveError re) {
			syntaxError(re.getMessage(),e,re);
			return null;
		} catch(SyntaxError se) {
			throw se;
		} catch(Exception ex) {
			syntaxError("internal failure", e, ex);
			return null;
		}	
	}
	
	protected Invoke resolve(Invoke s, HashSet<String> environment,
			ArrayList<PkgID> imports) throws ResolveError {
		List<Expr> args = s.arguments();		
		ArrayList<Expr> nargs = new ArrayList();
		for(Expr e : args) {						
			nargs.add(resolve(e, environment, imports));
		}
		
		ModuleID mid = loader.resolve(s.name(),imports);
		Expr target = s.target();
		if(target != null) {
			target = resolve(target,environment,imports);
		}
		Invoke ivk = new Invoke(s.name(),target,nargs,s.attributes());
						
		ivk.resolve(mid,null);
		
		return ivk;
	}
	
	protected Expr resolve(Variable v, HashSet<String> environment, ArrayList<PkgID> imports) throws ResolveError {
		if(environment.contains(v.name())) {
			// this is a local variable access
			return v;
		} else {
			// this is a constant variable access (or other)			
			ModuleID mid = loader.resolve(v.name(), imports);
			return new Constant(v.name(),mid,v.attributes());
		}
	}
	
	protected Expr resolve(None e, HashSet<String> environment,
			ArrayList<PkgID> imports) throws ResolveError {
		SetComprehension sc = (SetComprehension) resolve(e.mhs(), environment,
				imports);
		return new None(sc, e.attributes());
	}

	protected Expr resolve(Some e, HashSet<String> environment,
			ArrayList<PkgID> imports) throws ResolveError {
		SetComprehension sc = (SetComprehension) resolve(e.mhs(), environment,
				imports);
		return new Some(sc, e.attributes());
	}
	
	protected Expr resolve(UnOp v, HashSet<String> environment,
			ArrayList<PkgID> imports) throws ResolveError {
		Expr lhs = resolve(v.mhs(), environment, imports);
		if(v instanceof Not) {
			return new Not((Condition)lhs,v.attributes());
		} else if (v instanceof Some) {
			return resolve((Some) v, environment, imports);
		} else if (v instanceof None) {
			return resolve((None) v, environment, imports);
		} else if(v instanceof IntNegate) {
			return new IntNegate(lhs,v.attributes());
		} else if(v instanceof ListLength) {
			return new ListLength(lhs,v.attributes());
		} else if(v instanceof SetLength) {
			return new SetLength(lhs,v.attributes());
		} else if(v instanceof Spawn) {
			return new Spawn(lhs,v.attributes());
		} else if(v instanceof ProcessAccess) {
			return new ProcessAccess(lhs,v.attributes());
		} else {
			syntaxError("unrecognised unary operator: " + v.getClass().getName(),v);			
			return null;
		}
	}
	
	protected Expr resolve(BinOp v, HashSet<String> environment, ArrayList<PkgID> imports) {
		Expr lhs = resolve(v.lhs(), environment, imports);
		Expr rhs = resolve(v.rhs(), environment, imports);
		
		if(v instanceof And) {
			return new And((Condition)lhs,(Condition)rhs,v.attributes());
		} else if(v instanceof Or) {
			return new Or((Condition)lhs,(Condition)rhs,v.attributes());
		} else if(v instanceof IntAdd) {
			return new IntAdd(lhs,rhs,v.attributes());
		} else if(v instanceof IntSub) {
			return new IntSub(lhs,rhs,v.attributes());
		} else if(v instanceof IntMul) {
			return new IntMul(lhs,rhs,v.attributes());
		} else if(v instanceof IntDiv) {
			return new IntDiv(lhs,rhs,v.attributes());
		} else if(v instanceof IntLessThan) {
			return new IntLessThan(lhs,rhs,v.attributes());
		} else if(v instanceof IntLessThanEquals) {
			return new IntLessThanEquals(lhs,rhs,v.attributes());
		} else if(v instanceof IntGreaterThan) {
			return new IntGreaterThan(lhs,rhs,v.attributes());
		} else if(v instanceof IntGreaterThanEquals) {
			return new IntGreaterThanEquals(lhs,rhs,v.attributes());
		} else if(v instanceof IntEquals) {
			return new IntEquals(lhs,rhs,v.attributes());
		} else if(v instanceof ProcessEquals) {
			return new ProcessEquals(lhs,rhs,v.attributes());
		} else if(v instanceof IntNotEquals) {
			return new IntNotEquals(lhs,rhs,v.attributes());
		} else if(v instanceof SetUnion) {
			return new SetUnion(lhs,rhs,v.attributes());
		} else if(v instanceof SetIntersection) {
			return new SetIntersection(lhs,rhs,v.attributes());
		} else if(v instanceof SetDifference) {
			return new SetDifference(lhs,rhs,v.attributes());
		} else if(v instanceof SetElementOf) {
			return new SetElementOf(lhs,rhs,v.attributes());
		} else if(v instanceof Subset) {
			return new Subset(lhs,rhs,v.attributes());
		} else if(v instanceof SubsetEq) {
			return new SubsetEq(lhs,rhs,v.attributes());
		} else if(v instanceof ListElementOf) {
			return new ListElementOf(lhs,rhs,v.attributes());
		} else {
			syntaxError("unrecognised binary operator: " + v.getClass().getName(),v);
			return null;
		}
	}
	
	protected Expr resolve(SetGenerator sg, HashSet<String> environment, ArrayList<PkgID> imports) throws ResolveError {
		ArrayList<Expr> nexprs = new ArrayList<Expr>();
		for(Expr e : sg.getValues()) {
			nexprs.add(resolve(e, environment, imports));
		}
		return new SetGenerator(nexprs,sg.attributes());
	}
	
	protected Expr resolve(SetComprehension e, HashSet<String> environment, ArrayList<PkgID> imports) throws ResolveError {				
		List<Pair<String,Expr>> sources = e.sources();		
		List<Pair<String,Expr>> nsources = new ArrayList<Pair<String,Expr>>();
		HashSet<String> nenv = new HashSet<String>(environment);			
		
		for(Pair<String,Expr> me : sources) {						
			String s = me.first();						
			Expr src = resolve(me.second(),nenv,imports); 
			nsources.add(new Pair<String,Expr>(s, src));
			nenv.add(me.first());
		}
		
		Condition c = e.condition();
		if(c != null) {
			c = (Condition) resolve(e.condition(),nenv,imports);
		}
		Expr p = resolve(e.sign(),nenv,imports);
		
		return new SetComprehension(p,nsources,c,e.attributes());		
	}
	
	protected Expr resolve(RangeGenerator sg, HashSet<String> environment, ArrayList<PkgID> imports) throws ResolveError {
		Expr start = resolve(sg.start(),environment,imports);
		Expr end = resolve(sg.end(),environment,imports);
		
		return new RangeGenerator(start,end,sg.attributes());
	}
	
	protected Expr resolve(ListAccess sg, HashSet<String> environment, ArrayList<PkgID> imports) throws ResolveError {
		Expr src = resolve(sg.source(),environment,imports);
		Expr idx = resolve(sg.index(),environment,imports);
		return new ListAccess(src,idx,sg.attributes());
	}
	
	protected Expr resolve(ListSublist sg, HashSet<String> environment, ArrayList<PkgID> imports) throws ResolveError {
		Expr src = resolve(sg.source(),environment,imports);
		Expr start = resolve(sg.start(),environment,imports);
		Expr end = resolve(sg.end(),environment,imports);
		return new ListSublist(src,start,end,sg.attributes());
	}
	
	protected Expr resolve(ListGenerator sg, HashSet<String> environment, ArrayList<PkgID> imports) throws ResolveError {
		ArrayList<Expr> nexprs = new ArrayList<Expr>();
		for(Expr e : sg.getValues()) {
			nexprs.add(resolve(e, environment, imports));
		}
		return new ListGenerator(nexprs,sg.attributes());
	}
		
	protected Expr resolve(TupleGenerator sg, HashSet<String> environment, ArrayList<PkgID> imports) throws ResolveError {
		HashMap<String,Expr> vals = new HashMap<String,Expr>();
		for(Map.Entry<String,Expr> e : sg.values().entrySet()) {
			vals.put(e.getKey(), resolve(e.getValue(),environment,imports));
		}		
		return new TupleGenerator(vals,sg.attributes());
	}
	
	protected Expr resolve(TupleAccess sg, HashSet<String> environment, ArrayList<PkgID> imports) throws ResolveError {
		Expr src = resolve(sg.source(),environment,imports);		
		return new TupleAccess(src,sg.name(),sg.attributes());
	}
	
	protected Expr resolve(UnresolvedTypeEquals sg, HashSet<String> environment, ArrayList<PkgID> imports) throws ResolveError {
		Expr lhs = resolve(sg.lhs(),environment,imports);
		Condition rhs = (Condition) resolve(sg.rhs(),environment,imports);		
		resolve(sg.type(),imports);
		return new UnresolvedTypeEquals(lhs,sg.type(),rhs,sg.attributes());
	}
	
	protected void resolve(UnresolvedType t, ArrayList<PkgID> imports) throws ResolveError {
		if(t instanceof ListType) {
			ListType lt = (ListType) t;
			resolve(lt.element(),imports);
		} else if(t instanceof SetType) {
			SetType st = (SetType) t;
			resolve(st.element(),imports);
		} else if(t instanceof UnresolvedSetType) {
			UnresolvedSetType st = (UnresolvedSetType) t;
			resolve(st.element(),imports);
		} else if(t instanceof UnresolvedListType) {
			UnresolvedListType lt = (UnresolvedListType) t;
			resolve(lt.element(),imports);
		} else if(t instanceof TupleType) {
			TupleType tt = (TupleType) t;
			for(Map.Entry<String,Type> e : tt.types().entrySet()) {
				resolve(e.getValue(),imports);
			}
		} else if(t instanceof UnresolvedTupleType) {
			UnresolvedTupleType tt = (UnresolvedTupleType) t;
			for(Map.Entry<String,UnresolvedType> e : tt.types().entrySet()) {
				resolve(e.getValue(),imports);
			}
		} else if(t instanceof UserDefType) {
			UserDefType dt = (UserDefType) t;						
			ModuleID owner = loader.resolve(dt.name(), imports);		
			dt.resolve(owner);
		} else if(t instanceof UnionType) {
			UnionType ut = (UnionType) t;
			for(Type b : ut.types()) {
				resolve(b,imports);
			}
		} else if(t instanceof UnresolvedUnionType) {
			UnresolvedUnionType ut = (UnresolvedUnionType) t;
			for(UnresolvedType b : ut.types()) {
				resolve(b,imports);
			}
		}  else if(t instanceof UnresolvedProcessType) {	
			UnresolvedProcessType ut = (UnresolvedProcessType) t;
			resolve(ut.element(),imports);			
		}  
	}
}
