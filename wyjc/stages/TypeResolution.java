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

public class TypeResolution {
	private final ModuleLoader loader;	
	
	public TypeResolution(ModuleLoader loader) {
		this.loader = loader;
	}
	
	public void resolve(List<WhileyFile> files) {
		HashMap<ModuleID, WhileyFile> fileMap = new HashMap<ModuleID, WhileyFile>();
		
		for (WhileyFile f : files) {
			fileMap.put(f.module, f);
		}
		
		// Stage 1 ... resolve and check types of all named types and functions
		for(WhileyFile f : files) {
			for(WhileyFile.Decl d : f.declarations) {
				if(d instanceof ConstDecl) {
					resolve((ConstDecl)d);
				} else if(d instanceof TypeDecl) {
					resolve((TypeDecl)d);
				} else if(d instanceof FunDecl) {
					
				}
			}
		}
		
		// Stage 2 ... resolve, propagate and check types for all expressions.
	}
	
	protected void resolve(ConstDecl td) {
		resolve(td.constant,new HashMap<String,Type>());		
	}
	
	protected void resolve(TypeDecl td) throws SyntaxError {		
		Type t = resolve(td.type);
		td.attributes().add(new Attribute.Type(t));
		if(td.constraint != null) {
			// FIXME: at this point, would be good to add types for other
			// exposed variables.
			HashMap<String,Type> environment = new HashMap<String,Type>();
			environment.put("$", t);
			t = resolve(td.constraint, environment);
			checkType(t,Type.Bool.class, td.constraint);			
		}				
	}
	
	protected void resolve(FunDecl fd) {
		HashMap<String,Type> environment = new HashMap<String,Type>();
		
		// method parameter types
		for (WhileyFile.Parameter p : fd.parameters) {			
			Type t = resolve(p.type);
			environment.put(p.name(),t);			
		}
				
		// method return type
		Type ret = resolve(fd.ret);		
		
		// method receiver type (if applicable)						
		Type t = resolve(fd.receiver);
		environment.put("this",t);		
			
		if(fd.constraint != null) {			
			environment.put("$",ret);
			resolve(fd.constraint, environment);			
			environment.remove("$");
		}
		
		List<Stmt> stmts = fd.statements;
		for (int i=0;i!=stmts.size();++i) {
			resolve(stmts.get(i), environment);							
		}
	}
	
	public void resolve(Stmt s, HashMap<String,Type> environment) {
		try {
			if(s instanceof Skip) {				
			} else if(s instanceof VarDecl) {
				resolve((VarDecl)s, environment);
			} else if(s instanceof Assign) {
				resolve((Assign)s, environment);
			} else if(s instanceof Assert) {
				resolve((Assert)s, environment);
			} else if(s instanceof Debug) {
				resolve((Debug)s, environment);
			} else if(s instanceof IfElse) {
				resolve((IfElse)s, environment);
			} else if(s instanceof Invoke) {
				resolve((Invoke)s, environment);
			} else if(s instanceof Spawn) {
				resolve((UnOp)s, environment);
			} else {
				syntaxError("unknown statement encountered: "
						+ s.getClass().getName(), s);				
			}
		} catch (ResolveError e) {
			// Ok, we've hit a resolution error.
			syntaxError(e.getMessage(), s);			
		}
	}
	
	protected void resolve(VarDecl s, HashMap<String,Type> environment,
			ArrayList<PkgID> imports) throws SyntaxError {
		Expr init = s.initialiser;
		resolve(s.type);
		if(init != null) {
			resolve(init,environment);
		}
		environment.add(s.name);		
	}
	
	protected void resolve(Assign s, HashMap<String,Type> environment) {
		resolve(s.lhs, environment);
		resolve(s.rhs, environment);	
	}

	protected void resolve(Assert s, HashMap<String,Type> environment) {
		resolve(s.expr, environment);		
	}

	protected void resolve(Debug s, HashMap<String,Type> environment) {
		resolve(s.expr, environment);		
	}

	protected void resolve(IfElse s, HashMap<String,Type> environment,
			ArrayList<PkgID> imports) {
		Type t = resolve(s.condition, environment);
		checkType(t,Type.Bool.class,s.condition);
		
		// FIXME: need to perform some type inference here
		
		HashMap<String,Type> tenv = new HashMap<String,Type>(environment);		
		for (Stmt st : s.trueBranch) {
			resolve(st, tenv);
		}
		if (s.falseBranch != null) {			
			HashMap<String,Type> fenv = new HashMap<String,Type>(environment);
			for (Stmt st : s.falseBranch) {
				resolve(st, fenv);
			}
		}
	}
	
	protected Type resolve(Expr e, HashMap<String,Type> environment) {
		Type t;
		try {
			if (e instanceof Constant) {
				t = resolve((Constant)e, environment);
			} else if (e instanceof Variable) {
				t = resolve((Variable)e, environment);
			} else if (e instanceof NaryOp) {
				t = resolve((BinOp)e, environment);
			} else if (e instanceof BinOp) {
				t = resolve((BinOp)e, environment);
			} else if (e instanceof UnOp) {
				t = resolve((UnOp)e, environment);
			} else if (e instanceof Invoke) {
				t = resolve((Invoke)e, environment);
			} else if (e instanceof Comprehension) {
				t = resolve((Comprehension) e, environment);
			} else if (e instanceof TupleAccess) {
				t = resolve((TupleAccess) e, environment);
			} else if (e instanceof TupleGen) {
				t = resolve((TupleGen) e, environment);
			} else {				
				syntaxError("unknown expression encountered: "
							+ e.getClass().getName(), e);
				return null;
			}
		} catch(SyntaxError se) {
			throw se;
		} catch(Exception ex) {
			syntaxError("internal failure", e, ex);
			return null;
		}	
		
		// Save the type for use later in wyil generation
		e.attributes().add(new Attribute.Type(t));
		return t;
	}
	
	protected Type resolve(Invoke s, HashMap<String, Type> environment)
			throws SyntaxError {
						
		for(Expr e : s.arguments) {						
			resolve(e, environment);
		}
				
		Expr target = s.receiver;
		if(target != null) {
			resolve(target,environment);
		}
		
		
	}
	
	protected Type resolve(Constant c, HashMap<String,Type> environment) throws SyntaxError {
		return c.val.type();
	}
	
	protected Type resolve(Variable v, HashMap<String,Type> environment) throws SyntaxError {
		Type t = environment.get(v.var);
		if(t == null) {		
			syntaxError("unknown variable",v);			
		}
		return t;
	}
	
	protected Type resolve(UnOp v, HashMap<String,Type> environment,
			ArrayList<PkgID> imports) throws SyntaxError {
		Expr mhs = v.mhs;
		Type t = resolve(mhs, environment);
		
		if(v.op == UOp.NEG) {
			checkIsSubtype(Type.T_REAL,t,mhs);
		} else if(v.op == UOp.NOT) {
			checkIsSubtype(Type.T_BOOL,t,mhs);			
		} else if(v.op == UOp.LENGTHOF) {
			checkIsSubtype(Type.T_SET(Type.T_ANY),t,mhs);
			return Type.T_INT;
		} else if(v.op == UOp.PROCESSACCESS) {
			Type.Process tp = checkType(t,Type.Process.class,mhs);
			return tp.element;
		} else if(v.op == UOp.PROCESSSPAWN){
			return Type.T_PROCESS(t);
		} 
		
		return t;
	}
	
	protected Type resolve(BinOp v, HashMap<String,Type> environment) {
		Type lhs_t = resolve(v.lhs, environment);
		Type rhs_t = resolve(v.rhs, environment);
		BOp bop = v.op;
			
		if(bop == BOp.OR || bop == BOp.AND) {
			checkIsSubtype(Type.T_BOOL, lhs_t, v);
			checkIsSubtype(Type.T_BOOL, rhs_t, v);
			return Type.T_BOOL;
		} else if (bop == BOp.ADD || bop == BOp.SUB || bop == BOp.MUL
				|| bop == BOp.DIV) {
			checkIsSubtype(Type.T_REAL, lhs_t, v);
			checkIsSubtype(Type.T_REAL, rhs_t, v);
			return Type.leastUpperBound(lhs_t,rhs_t);
		} else if (bop == BOp.LT || bop == BOp.LTEQ || bop == BOp.GT
				|| bop == BOp.GTEQ) {
			checkIsSubtype(Type.T_REAL, lhs_t, v);
			checkIsSubtype(Type.T_REAL, rhs_t, v);
			return Type.T_BOOL;
		} else if(bop == BOp.UNION || bop == BOp.INTERSECTION) {
			checkIsSubtype(Type.T_SET(Type.T_ANY), lhs_t, v);
			checkIsSubtype(Type.T_SET(Type.T_ANY), rhs_t, v);
			return Type.leastUpperBound(lhs_t,rhs_t);
		} else if (bop == BOp.SUBSET || bop == BOp.SUBSETEQ) {
			checkIsSubtype(Type.T_SET(Type.T_ANY), lhs_t, v);
			checkIsSubtype(Type.T_SET(Type.T_ANY), rhs_t, v);
			return Type.T_BOOL;
		} else if(bop == BOp.EQ || bop == BOp.NEQ){
			if(!Type.isSubtype(lhs_t, rhs_t) && !Type.isSubtype(rhs_t, lhs_t)) {
				syntaxError("Cannot compare types",v);
			}
			return Type.T_BOOL;
		} else if(bop == BOp.ELEMENTOF) {
			checkType(rhs_t, Type.Set.class, v);
			Type.Set st = (Type.Set) rhs_t;
			if(!Type.isSubtype(lhs_t, st.element) && !Type.isSubtype(st.element, lhs_t)) {
				syntaxError("Cannot compare types",v);
			}
			return Type.T_BOOL;
		} else if(bop == BOp.LISTACCESS) {
			checkType(lhs_t, Type.List.class, v);
			checkIsSubtype(Type.T_INT, rhs_t, v);
			Type.List lt = (Type.List) lhs_t;  
			return lt.element;		
		} 			
		
		throw new RuntimeException("NEED TO ADD MORE CASES TO TYPE RESOLUTION BINOP");
	}
	
	protected Type resolve(NaryOp v, HashMap<String,Type> environment,
			ArrayList<PkgID> imports) throws SyntaxError {		
		for(Expr e : v.arguments) {
			resolve(e, environment);
		}		
	}
	
	protected Type resolve(Comprehension e, HashMap<String,Type> environment) throws SyntaxError {				
		HashSet<String> nenv = new HashSet<String>(environment);
		for(Pair<String,Expr> me : e.sources) {						
			String s = me.first();						
			resolve(me.second(),nenv); 			
			nenv.add(me.first());
		}
		
		if(e.value != null) {
			resolve(e.condition,nenv);
		}
		if(e.condition != null) {
			resolve(e.condition,nenv);
		}
	}
	
		
	protected Type resolve(TupleGen sg, HashMap<String,Type> environment,
			ArrayList<PkgID> imports) throws SyntaxError {		
		for(Map.Entry<String,Expr> e : sg.fields.entrySet()) {
			resolve(e.getValue(),environment);
		}			
	}
	
	protected Type resolve(TupleAccess sg, HashMap<String,Type> environment) throws SyntaxError {
		Type lhs = resolve(sg.lhs,environment);
		lhs = checkType(lhs,Type.Tuple.class,sg.lhs);
		
	}
	
	protected Type resolve(UnresolvedType t) throws SyntaxError {
		if(t instanceof UnresolvedType.List) {
			UnresolvedType.List lt = (UnresolvedType.List) t;
			resolve(lt.element);
		} else if(t instanceof UnresolvedType.Set) {
			UnresolvedType.Set st = (UnresolvedType.Set) t;
			resolve(st.element);
		} else if(t instanceof UnresolvedType.Tuple) {
			UnresolvedType.Tuple tt = (UnresolvedType.Tuple) t;
			for(Map.Entry<String,UnresolvedType> e : tt.types.entrySet()) {
				resolve(e.getValue());
			}
		} else if(t instanceof UnresolvedType.Named) {
			UnresolvedType.Named dt = (UnresolvedType.Named) t;						
			ModuleID owner = loader.resolve(dt.name);		
			// FIXME: need to actually resolve stuff!!
			// dt.resolve(owner);
		} else if(t instanceof UnresolvedType.Union) {
			UnresolvedType.Union ut = (UnresolvedType.Union) t;
			for(UnresolvedType b : ut.bounds) {
				resolve(b);
			}
		} else if(t instanceof UnresolvedType.Process) {	
			UnresolvedType.Process ut = (UnresolvedType.Process) t;
			resolve(ut.element);			
		}  
	}
	
	protected <T extends Type> T checkType(Type t, Class<T> clazz,
			SyntacticElement elem) {
		if(t instanceof Type.Named) {
			t = ((Type.Named)t).type;
		}
		if (clazz.isInstance(t)) {
			return (T) t;
		} else {
			syntaxError("expected type " + clazz.getName() + " found " + t,
					elem);
			return null;
		}
	}
	
	// Check t1 :> t2
	protected void checkIsSubtype(Type t1, Type t2,
			SyntacticElement elem) {
		// FIXME: to do
	}
}
