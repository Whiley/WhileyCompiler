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

import static wyjc.lang.WhileyFile.*;
import wyil.ModuleLoader;
import wyil.util.*;
import wyil.lang.*;
import wyjc.lang.*;
import wyjc.lang.Attributes;
import wyjc.lang.WhileyFile.*;
import wyjc.lang.Stmt.*;
import wyjc.lang.Expr.*;
import wyjc.util.*;

public class NameResolution {
	private final ModuleLoader loader;	
	private String filename;
	
	public NameResolution(ModuleLoader loader) {
		this.loader = loader;
	}
	
	public void resolve(WhileyFile wf) {
		ArrayList<PkgID> imports = new ArrayList<PkgID>();
		
		ModuleID id = wf.module;
		filename = wf.filename;
		
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
				syntaxError(ex.getMessage(),filename,d);
			}
		}				
	}
	
	protected void resolve(ConstDecl td, ArrayList<PkgID> imports) {
		resolve(td.constant,new HashMap<String,Set<Expr>>(), imports);		
	}
	
	protected void resolve(TypeDecl td, ArrayList<PkgID> imports) throws ResolveError {
		try {
			resolve(td.type, imports);			
			if(td.constraint != null) {
				HashMap<String,Set<Expr>> environment = new HashMap<String,Set<Expr>>();
				environment.put("$", Collections.EMPTY_SET);
				addExposedNames(new Expr.Variable("$", td.constraint
						.attribute(Attribute.Source.class)), td.type,
						environment);
				resolve(td.constraint,environment,imports);
			}		
		} catch (ResolveError e) {												
			// Ok, we've hit a resolution error.
			syntaxError(e.getMessage(), filename,  td);			
		}
	}	
	
	protected void resolve(FunDecl fd, ArrayList<PkgID> imports) {
		HashMap<String,Set<Expr>> environment = new HashMap<String,Set<Expr>>();
		
		// method parameter types
		for (WhileyFile.Parameter p : fd.parameters) {
			try {
				resolve(p.type, imports);
				environment.put(p.name(),Collections.EMPTY_SET);
			} catch (ResolveError e) {												
				// Ok, we've hit a resolution error.
				syntaxError(e.getMessage(), filename, p, e);
			}
		}
		
		if(fd.receiver != null) {
			environment.put("this",Collections.EMPTY_SET);
		}
		
		// method return type
		try {
			resolve(fd.ret, imports);
		} catch (ResolveError e) {
			// Ok, we've hit a resolution error.
			syntaxError(e.getMessage(), filename, fd.ret);
		}
		
		// method receiver type (if applicable)
		try {			
			resolve(fd.receiver, imports);			
		} catch (ResolveError e) {
			// Ok, we've hit a resolution error.
			syntaxError(e.getMessage(),filename,fd.receiver);
		}
			
		if(fd.precondition != null) {
			environment.put("$",Collections.EMPTY_SET);
			resolve(fd.precondition, environment,imports);			
			environment.remove("$");
		}
		
		List<Stmt> stmts = fd.statements;
		for (int i=0;i!=stmts.size();++i) {
			resolve(stmts.get(i), environment, imports);							
		}
	}
	
	public void resolve(Stmt s, HashMap<String,Set<Expr>> environment, ArrayList<PkgID> imports) {
		try {
			if(s instanceof VarDecl) {
				resolve((VarDecl)s, environment, imports);
			} else if(s instanceof Assign) {
				resolve((Assign)s, environment, imports);
			} else if(s instanceof Assert) {
				resolve((Assert)s, environment, imports);
			} else if(s instanceof Return) {
				resolve((Return)s, environment, imports);
			} else if(s instanceof Debug) {
				resolve((Debug)s, environment, imports);
			} else if(s instanceof Skip) {
				// do nothing
			} else if(s instanceof IfElse) {
				resolve((IfElse)s, environment, imports);
			} else if(s instanceof Invoke) {
				resolve((Invoke)s, environment, imports);
			} else if(s instanceof Spawn) {
				resolve((UnOp)s, environment, imports);
			} else {
				syntaxError("unknown statement encountered: "
						+ s.getClass().getName(), filename, s);				
			}
		} catch (ResolveError e) {
			// Ok, we've hit a resolution error.
			syntaxError(e.getMessage(), filename, s);			
		}
	}
	
	protected void resolve(VarDecl s, HashMap<String,Set<Expr>> environment,
			ArrayList<PkgID> imports) throws ResolveError {
		Expr init = s.initialiser;
		resolve(s.type, imports);
		if(init != null) {
			resolve(init,environment, imports);
		}
		environment.put(s.name,Collections.EMPTY_SET);		
	}
	
	protected void resolve(Assign s, HashMap<String,Set<Expr>> environment,
			ArrayList<PkgID> imports) {
		resolve(s.lhs, environment, imports);
		resolve(s.rhs, environment, imports);	
	}

	protected void resolve(Assert s, HashMap<String,Set<Expr>> environment,
			ArrayList<PkgID> imports) {
		resolve(s.expr, environment, imports);		
	}

	protected void resolve(Return s, HashMap<String,Set<Expr>> environment,
			ArrayList<PkgID> imports) {
		if(s.expr != null) {
			resolve(s.expr, environment, imports);
		}
	}
	
	protected void resolve(Debug s, HashMap<String,Set<Expr>> environment,
			ArrayList<PkgID> imports) {
		resolve(s.expr, environment, imports);		
	}

	protected void resolve(IfElse s, HashMap<String,Set<Expr>> environment,
			ArrayList<PkgID> imports) {
		resolve(s.condition, environment, imports);
		environment = new HashMap<String,Set<Expr>>(environment);
		for (Stmt st : s.trueBranch) {
			resolve(st, environment, imports);
		}
		if (s.falseBranch != null) {			
			for (Stmt st : s.falseBranch) {
				resolve(st, environment, imports);
			}
		}
	}
	
	protected void resolve(Expr e, HashMap<String,Set<Expr>> environment, ArrayList<PkgID> imports) {
		try {
			if (e instanceof Constant) {
				
			} else if (e instanceof Variable) {
				resolve((Variable)e, environment, imports);
			} else if (e instanceof NaryOp) {
				resolve((NaryOp)e, environment, imports);
			} else if (e instanceof Comprehension) {
				resolve((Comprehension) e, environment, imports);
			} else if (e instanceof BinOp) {
				resolve((BinOp)e, environment, imports);
			} else if (e instanceof ListAccess) {
				resolve((ListAccess)e, environment, imports);
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
			} else if(e instanceof TypeConst) {
				resolve((TypeConst) e, environment, imports);
			} else {				
				syntaxError("unknown expression encountered: "
							+ e.getClass().getName(), filename, e);								
			}
		} catch(ResolveError re) {
			syntaxError(re.getMessage(),filename,e,re);			
		} catch(SyntaxError se) {
			throw se;
		} catch(Exception ex) {
			syntaxError("internal failure", filename, e, ex);			
		}	
	}
	
	protected void resolve(Invoke ivk, HashMap<String,Set<Expr>> environment,
			ArrayList<PkgID> imports) throws ResolveError {
					
		for(Expr e : ivk.arguments) {						
			resolve(e, environment, imports);
		}
		
		ModuleID mid = loader.resolve(ivk.name,imports);
		Expr target = ivk.receiver;
		
		if(target != null) {
			resolve(target,environment,imports);
		}
		
		// Ok, resolve the module for this invoke
		ivk.attributes().add(new Attributes.Module(mid));		
	}
	
	protected void resolve(Variable v, HashMap<String, Set<Expr>> environment,
			ArrayList<PkgID> imports) throws ResolveError {
		Set<Expr> aliases = environment.get(v.var);
		if (aliases == null) {
			// This variable access must correspond with a constant definition
			// in some module. Therefore, we must determine which module this
			// is, and then store that information for future use.
			ModuleID mid = loader.resolve(v.var, imports);
			v.attributes().add(new Attributes.Module(mid));
		} else if (aliases.size() == 1) {
			v.attributes().add(new Attributes.Alias(aliases.iterator().next()));
		} else if (aliases.size() > 1) {
			syntaxError("ambigous variable name", filename, v);
		}
	}
	
	protected void resolve(UnOp v, HashMap<String,Set<Expr>> environment,
			ArrayList<PkgID> imports) throws ResolveError {
		resolve(v.mhs, environment, imports);		
	}
	
	protected void resolve(BinOp v, HashMap<String,Set<Expr>> environment, ArrayList<PkgID> imports) {
		resolve(v.lhs, environment, imports);
		resolve(v.rhs, environment, imports);		
	}
	
	protected void resolve(ListAccess v, HashMap<String,Set<Expr>> environment,
			ArrayList<PkgID> imports) {
		resolve(v.src, environment, imports);
		resolve(v.index, environment, imports);
	}
	
	protected void resolve(NaryOp v, HashMap<String,Set<Expr>> environment,
			ArrayList<PkgID> imports) throws ResolveError {				
		for(Expr e : v.arguments) {
			resolve(e, environment, imports);
		}		
	}
	
	protected void resolve(Comprehension e, HashMap<String,Set<Expr>> environment, ArrayList<PkgID> imports) throws ResolveError {						
		HashMap<String,Set<Expr>> nenv = new HashMap<String,Set<Expr>>(environment);
		for(Pair<String,Expr> me : e.sources) {														
			resolve(me.second(),nenv,imports); 			
			nenv.put(me.first(),Collections.EMPTY_SET);
		}		
		if(e.value != null) {			
			resolve(e.value,nenv,imports);
		}
		if(e.condition != null) {
			resolve(e.condition,nenv,imports);
		}	
	}	
		
	protected void resolve(TupleGen sg, HashMap<String,Set<Expr>> environment,
			ArrayList<PkgID> imports) throws ResolveError {		
		for(Map.Entry<String,Expr> e : sg.fields.entrySet()) {
			resolve(e.getValue(),environment,imports);
		}			
	}
	
	protected void resolve(TypeConst tc, HashMap<String,Set<Expr>> environment,
			ArrayList<PkgID> imports) throws ResolveError {		
		resolve(tc.type,imports);			
	}
	
	
	protected void resolve(TupleAccess sg, HashMap<String,Set<Expr>> environment, ArrayList<PkgID> imports) throws ResolveError {
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
			// This case corresponds to a user-defined type. This will be
			// defined in some module (possibly ours), and we need to identify
			// what module that is here, and save it for future use.
			UnresolvedType.Named dt = (UnresolvedType.Named) t;						
			ModuleID mid = loader.resolve(dt.name, imports);			
			t.attributes().add(new Attributes.Module(mid));
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

	/**
	 * The purpose of the exposed names method is capture the case when we have
	 * a define statement like this:
	 * 
	 * <pre>
	 * define tup as (int x, int y) where x < y
	 * </pre>
	 * 
	 * In this case, <code>x</code> and <code>y</code> are "exposed" --- meaning
	 * they're real names are different in some way. In this case, the aliases
	 * we have are: x->$.x and y->$.y
	 * 
	 * @param src
	 * @param t
	 * @param environment
	 */
	private static void addExposedNames(Expr src, UnresolvedType t,
			HashMap<String, Set<Expr>> environment) {
		// Extended this method to handle lists and sets etc, is very difficult.
		// The primary problem is that we need to expand expressions involved
		// names exposed in this way into quantified
		// expressions.
		if(t instanceof UnresolvedType.Tuple) {
			UnresolvedType.Tuple tt = (UnresolvedType.Tuple) t;
			for(Map.Entry<String,UnresolvedType> e : tt.types.entrySet()) {
				Expr s = new Expr.TupleAccess(src, e
						.getKey(), src.attribute(Attribute.Source.class));
				addExposedNames(s,e.getValue(),environment);
				Set<Expr> aliases = environment.get(e.getKey());
				if(aliases == null) {
					aliases = new HashSet<Expr>();
					environment.put(e.getKey(),aliases);
				}
				aliases.add(s);
			}
		} else if(t instanceof UnresolvedType.Process) {	
			UnresolvedType.Process ut = (UnresolvedType.Process) t;
			addExposedNames(new Expr.UnOp(Expr.UOp.PROCESSACCESS,src),ut.element,environment);					
		}  
	}
}
