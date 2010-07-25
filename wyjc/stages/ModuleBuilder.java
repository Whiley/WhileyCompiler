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
import wyjc.lang.Stmt.*;
import wyjc.lang.Expr.*;
import wyjc.util.*;

public class ModuleBuilder {
	private final ModuleLoader loader;
	
	public ModuleBuilder(ModuleLoader loader) {
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
			resolve(fd.ret, imports);
		} catch (ResolveError e) {
			// Ok, we've hit a resolution error.
			syntaxError(e.getMessage(), fd.ret);
		}
		
		// method receiver type (if applicable)
		try {			
			resolve(fd.receiver, imports);			
		} catch (ResolveError e) {
			// Ok, we've hit a resolution error.
			syntaxError(e.getMessage(), fd.receiver);
		}
			
		if(fd.constraint != null) {
			environment.add("$");
			resolve(fd.constraint, environment,imports);			
			environment.remove("$");
		}
		
		List<Stmt> stmts = fd.statements;
		for (int i=0;i!=stmts.size();++i) {
			Stmt s = resolve(stmts.get(i), environment, imports);
			stmts.set(i,s);					
		}
	}
	
	public void resolve(Stmt s, HashSet<String> environment, ArrayList<PkgID> imports) {
		try {
			if(s instanceof Skip) {				
			} else if(s instanceof VarDecl) {
				resolve((VarDecl)s, environment, imports);
			} else if(s instanceof Assign) {
				resolve((Assign)s, environment, imports);
			} else if(s instanceof Assert) {
				resolve((Assert)s, environment, imports);
			} else if(s instanceof Debug) {
				resolve((Debug)s, environment, imports);
			} else if(s instanceof IfElse) {
				resolve((IfElse)s, environment, imports);
			} else if(s instanceof Invoke) {
				resolve((Invoke)s, environment, imports);
			} else if(s instanceof Spawn) {
				resolve((UnOp)s, environment, imports);
			} else {
				syntaxError("unknown statement encountered: "
						+ s.getClass().getName(), s);				
			}
		} catch (ResolveError e) {
			// Ok, we've hit a resolution error.
			syntaxError(e.getMessage(), s);			
		}
	}
	
	protected void resolve(VarDecl s, HashSet<String> environment,
			ArrayList<PkgID> imports) throws ResolveError {
		Expr init = s.initialiser;
		resolve(s.type, imports);
		if(init != null) {
			resolve(init,environment, imports);
		}
		environment.add(s.name);		
	}
	
	protected void resolve(Assign s, HashSet<String> environment,
			ArrayList<PkgID> imports) {
		resolve(s.lhs, environment, imports);
		resolve(s.rhs, environment, imports);	
	}

	protected void resolve(Assert s, HashSet<String> environment,
			ArrayList<PkgID> imports) {
		resolve(s.expr, environment, imports);		
	}

	protected void resolve(Debug s, HashSet<String> environment,
			ArrayList<PkgID> imports) {
		resolve(s.expr, environment, imports);		
	}

	protected void resolve(IfElse s, HashSet<String> environment,
			ArrayList<PkgID> imports) {
		resolve(s.condition, environment, imports);
		environment = new HashSet<String>(environment);
		for (Stmt st : s.trueBranch) {
			resolve(st, environment, imports);
		}
		if (s.falseBranch != null) {			
			for (Stmt st : s.falseBranch) {
				resolve(st, environment, imports);
			}
		}
	}
	
	protected void resolve(Expr e, HashSet<String> environment, ArrayList<PkgID> imports) {
		try {
			if (e instanceof Constant) {
				
			} else if (e instanceof Variable) {
				resolve((Variable)e, environment, imports);
			} else if (e instanceof NaryOp) {
				resolve((BinOp)e, environment, imports);
			} else if (e instanceof BinOp) {
				resolve((BinOp)e, environment, imports);
			} else if (e instanceof UnOp) {
				resolve((UnOp)e, environment, imports);
			} else if (e instanceof Invoke) {
				resolve((Invoke)e, environment, imports);
			} else if (e instanceof Comprehension) {
				resolve((Comprehension) e, environment, imports);
			} else if (e instanceof TupleAccess) {
				resolve((TupleAccess) e, environment, imports);
			} else if (e instanceof TupleGen) {
				resolve((TupleGen) e, environment, imports);
			} else {				
				syntaxError("unknown expression encountered: "
							+ e.getClass().getName(), e);								
			}
		} catch(ResolveError re) {
			syntaxError(re.getMessage(),e,re);			
		} catch(SyntaxError se) {
			throw se;
		} catch(Exception ex) {
			syntaxError("internal failure", e, ex);			
		}	
	}
	
	protected void resolve(Invoke s, HashSet<String> environment,
			ArrayList<PkgID> imports) throws ResolveError {
						
		for(Expr e : s.arguments) {						
			resolve(e, environment, imports);
		}
		
		ModuleID mid = loader.resolve(s.name,imports);
		Expr target = s.receiver;
		if(target != null) {
			resolve(target,environment,imports);
		}
		
		// FIXME: need to actually resolve the invocation
		// ivk.resolve(mid,null);		
	}
	
	protected void resolve(Variable v, HashSet<String> environment, ArrayList<PkgID> imports) throws ResolveError {
		if(!environment.contains(v.var)) {		
			// this is a constant variable access (or other)	
			
			// FIXME: need to actually resolve this variable
			ModuleID mid = loader.resolve(v.var, imports);			
		}
	}
	
	protected void resolve(UnOp v, HashSet<String> environment,
			ArrayList<PkgID> imports) throws ResolveError {
		resolve(v.mhs, environment, imports);		
	}
	
	protected void resolve(BinOp v, HashSet<String> environment, ArrayList<PkgID> imports) {
		resolve(v.lhs, environment, imports);
		resolve(v.rhs, environment, imports);		
	}
	
	protected void resolve(NaryOp v, HashSet<String> environment,
			ArrayList<PkgID> imports) throws ResolveError {		
		for(Expr e : v.arguments) {
			resolve(e, environment, imports);
		}		
	}
	
	protected void resolve(Comprehension e, HashSet<String> environment, ArrayList<PkgID> imports) throws ResolveError {				
		HashSet<String> nenv = new HashSet<String>(environment);
		for(Pair<String,Expr> me : e.sources) {						
			String s = me.first();						
			resolve(me.second(),nenv,imports); 			
			nenv.add(me.first());
		}
		
		if(e.value != null) {
			resolve(e.condition,nenv,imports);
		}
		if(e.condition != null) {
			resolve(e.condition,nenv,imports);
		}
	}
	
		
	protected void resolve(TupleGen sg, HashSet<String> environment,
			ArrayList<PkgID> imports) throws ResolveError {		
		for(Map.Entry<String,Expr> e : sg.fields.entrySet()) {
			resolve(e.getValue(),environment,imports);
		}			
	}
	
	protected void resolve(TupleAccess sg, HashSet<String> environment, ArrayList<PkgID> imports) throws ResolveError {
		resolve(sg.lhs,environment,imports);			
	}
	
	protected void resolve(UnresolvedType t, ArrayList<PkgID> imports) throws ResolveError {
		if(t instanceof UnresolvedType.List) {
			UnresolvedType.List lt = (UnresolvedType.List) t;
			resolve(lt.element,imports);
		} else if(t instanceof UnresolvedType.Set) {
			UnresolvedType.Set st = (UnresolvedType.Set) t;
			resolve(st.element,imports);
		} else if(t instanceof UnresolvedType.Tuple) {
			UnresolvedType.Tuple tt = (UnresolvedType.Tuple) t;
			for(Map.Entry<String,UnresolvedType> e : tt.types.entrySet()) {
				resolve(e.getValue(),imports);
			}
		} else if(t instanceof UnresolvedType.Named) {
			UnresolvedType.Named dt = (UnresolvedType.Named) t;						
			ModuleID owner = loader.resolve(dt.name, imports);		
			// FIXME: need to actually resolve stuff!!
			// dt.resolve(owner);
		} else if(t instanceof UnresolvedType.Union) {
			UnresolvedType.Union ut = (UnresolvedType.Union) t;
			for(UnresolvedType b : ut.bounds) {
				resolve(b,imports);
			}
		} else if(t instanceof UnresolvedType.Process) {	
			UnresolvedType.Process ut = (UnresolvedType.Process) t;
			resolve(ut.element,imports);			
		}  
	}
}
