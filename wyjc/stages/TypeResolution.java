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
	private HashSet<ModuleID> modules;
	private HashMap<NameID,List<Type.Fun>> functions;	
	private HashMap<NameID,Type> types;	
	private HashMap<NameID,UnresolvedType> unresolved;
	
	public TypeResolution(ModuleLoader loader) {
		this.loader = loader;
	}
	
	public void resolve(List<WhileyFile> files) {
		HashMap<ModuleID, WhileyFile> fileMap = new HashMap<ModuleID, WhileyFile>();		
		modules = new HashSet<ModuleID>();
		functions = new HashMap<NameID,List<Type.Fun>>();
		types = new HashMap<NameID,Type>();		
		unresolved = new HashMap<NameID,UnresolvedType>();
		
		// now, init data
		for (WhileyFile f : files) {
			fileMap.put(f.module, f);
			modules.add(f.module);
		}
						
		// Stage 1 ... resolve and check types of all named types and functions
		generateTypes(files);
		
				

		// Stage 2 ... resolve, propagate and check types for all expressions.
		for(WhileyFile f : files) {
			for(WhileyFile.Decl d : f.declarations) {
				if(d instanceof ConstDecl) {
					resolve((ConstDecl)d);
				} else if(d instanceof TypeDecl) {
					resolve((TypeDecl)d);
				} else if(d instanceof FunDecl) {
					partResolve(f.module,(FunDecl)d);
				}
			}
		}
		
	}

	/**
	 * The following method visits every define type statement in every whiley
	 * file being compiled, and determines its true type.  
	 * 
	 * @param files
	 */
	protected void generateTypes(List<WhileyFile> files) {		
		HashMap<NameID,SyntacticElement> srcs = new HashMap();
		
		// second construct list.
		for(WhileyFile f : files) {
			for(Decl d : f.declarations) {
				if(d instanceof TypeDecl) {
					TypeDecl td = (TypeDecl) d;
					NameID key = new NameID(f.module,td.name());					
					unresolved.put(key, td.type);
					srcs.put(key,d);
				}
			}
		}
		
		// third expand all types
		for(NameID key : unresolved.keySet()) {
			try {
				HashMap<NameID, Type> cache = new HashMap<NameID,Type>();			
				Type t = expandType(key,cache);		
				// t = simplifyRecursiveTypes(t);		
				types.put(key,t);				
			} catch(ResolveError ex) {
				syntaxError(ex.getMessage(),srcs.get(key),ex);
			}
		}
	}
	
	/**
	 * Determine whether or not a type is recursive
	 * 
	 * @param type
	 * @return
	 */
	private static boolean isRecursive(Type type) {
		return !type.match(RecursiveType.class).isEmpty();
	}
	
	protected Type expandType(NameID key,
			HashMap<NameID, Type> cache) throws ResolveError {

		Type t = types.get(key);
		
		if(t != null && !isRecursive(t)) {
			return t;
		} else if(!modules.contains(key.module())) {			
			// indicates a non-local key which we can resolve immediately
			Module mi = loader.loadModule(key.module());
			Module.TypeDef td = mi.type(key.name());			
			return td.type();
		}
		
		// Now, check for a cached expansion.
		Type cached = cache.get(key);					
		if(cached != null) { return cached; }		
		
		// following is needed to terminate any recursion
		cache.put(key, Type.T_RECURSIVE(key.toString(),null));
		
		// Ok, expand the type properly then
		UnresolvedType ut = unresolved.get(key);
		
		t = expandType(ut, cache);
				 		
		if(isRecursive(key,t)) {
			// recursive case
			t = Type.T_RECURSIVE(key.toString(),t);			
		} 
		
		cache.put(key, t);
		
		// Done
		return t;		
	}	
	
	protected Type expandType(UnresolvedType t, HashMap<NameID, Type> cache) {
		if(t instanceof UnresolvedType.List) {
			UnresolvedType.List lt = (UnresolvedType.List) t;			
			return Type.T_LIST(expandType(lt.element, cache));
		} else if(t instanceof UnresolvedType.Set) {
			UnresolvedType.Set st = (UnresolvedType.Set) t;			
			return Type.T_SET(expandType(st.element, cache));
		} else if(t instanceof UnresolvedType.Tuple) {
			UnresolvedType.Tuple tt = (UnresolvedType.Tuple) t;
			HashMap<String,Type> types = new HashMap<String,Type>();
			for(Map.Entry<String,UnresolvedType> e : tt.types.entrySet()) {
				types.put(e.getKey(),expandType(e.getValue(),cache));
			}
			return Type.T_TUPLE(types);
		} else if(t instanceof UnresolvedType.Union) {
			UnresolvedType.Union ut = (UnresolvedType.Union) t;
			HashSet<Type.NonUnion> bounds = new HashSet<Type.NonUnion>();
			for(UnresolvedType b : ut.bounds) {
				Type bt = expandType(b,cache);
				if(bt instanceof Type.NonUnion) {
					bounds.add((Type.NonUnion)t);
				} else {
					bounds.addAll(((Type.Union)bt).bounds);
				}
			}
			if(bounds.size() == 1) {
				return bounds.iterator().next();
			} else {
				return Type.T_UNION(bounds);
			}
		} else if(t instanceof UnresolvedType.Process) {	
			UnresolvedType.Process ut = (UnresolvedType.Process) t;
			return Type.T_PROCESS(expandType(ut.element,cache));			
		} else if(t instanceof UnresolvedType.Named) {
			UnresolvedType.Named dt = (UnresolvedType.Named) t;						
			Attribute.Module modInfo = dt.attribute(Attribute.Module.class);
			NameID name = new NameID(modInfo.module,dt.name);
			Type et = expandType(name,cache);
			
			if(Type.isExistential(et)) {
				return Type.T_NAMED(modInfo.module,dt.name,et);
			} else {
				return et;
			}
			
		}  else {
			// for base cases
			return resolve(t);
		}
	}
	
	protected void partResolve(ModuleID module, FunDecl fd) {
		
		ArrayList<Type> parameters = new ArrayList<Type>();
		for (WhileyFile.Parameter p : fd.parameters) {			
			parameters.add(resolve(p.type));
		}
		
		// method return type
		Type ret = resolve(fd.ret);		
		
		// method receiver type (if applicable)
		Type.Process rec = null;
		if(fd.receiver != null) {
			Type t = resolve(fd.receiver);
			checkType(t,Type.Process.class,fd.receiver);
			rec = (Type.Process) rec;
		}
		
		List<Type.Fun> types = functions.get(fd.name);
		if(types == null) {
			types = new ArrayList<Type.Fun>();
			functions.put(new NameID(module, fd.name), types);
		}
		
		types.add(Type.T_FUN(rec, ret, parameters));
	}

	protected void resolve(ConstDecl td) {
		// FIXME: this looks problematic if the constant contains method calls.
		// The problem is that we may not have generated the type for the given
		// method call, which means we won't be able to bind it. One option is
		// simply to disallow function calls in constant definitions.
		resolve(td.constant,new HashMap<String,Type>());		
	}
	
	protected void resolve(TypeDecl td) throws ResolveError {		
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
		
	protected void fullResolve(FunDecl fd) {		
		HashMap<String,Type> environment = new HashMap<String,Type>();
		
		// method parameter types
		for (WhileyFile.Parameter p : fd.parameters) {			
			try {
				Type t = resolve(p.type);
				environment.put(p.name(),t);
			} catch(ResolveError rex) {
				syntaxError(rex.getMessage(),p,rex);
			}
		}
				
		// method return type
		Type ret = null;
		try {
			ret = resolve(fd.ret);
		} catch(ResolveError rex) {
			syntaxError(rex.getMessage(),fd.ret,rex);
		}
		
		// method receiver type (if applicable)			
		if(fd.receiver != null) {
			try {
				Type rec = resolve(fd.receiver);
				environment.put("this", rec);
			} catch(ResolveError rex) {
				syntaxError(rex.getMessage(),fd.receiver,rex);
			}
		}
			
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
		} catch(ResolveError rex) {
			syntaxError(rex.getMessage(),s,rex);
		} catch(Exception ex) {
			syntaxError("internal failure", s, ex);			
		}
	}
	
	protected void resolve(VarDecl s, HashMap<String,Type> environment) throws ResolveError {
		Expr init = s.initialiser;
		Type type = resolve(s.type);
		if(init != null) {
			Type initT = resolve(init,environment);
			checkIsSubtype(type,initT,init);
		}
		environment.put(s.name,type);		
	}
	
	protected void resolve(Assign s, HashMap<String,Type> environment) {
		Type lhs_t = resolve(s.lhs, environment);
		Type rhs_t = resolve(s.rhs, environment);
		checkIsSubtype(lhs_t,rhs_t,s.rhs);
		
		// FIXME: need to perform type inference here, which means updating the
		// environment and also carrying around a declared environment
	}

	protected void resolve(Assert s, HashMap<String,Type> environment) {
		Type t = resolve(s.expr, environment);
		checkIsSubtype(t,Type.T_BOOL,s.expr);
	}

	protected void resolve(Debug s, HashMap<String,Type> environment) {
		Type t = resolve(s.expr, environment);
		checkIsSubtype(t,Type.T_LIST(Type.T_INT),s.expr);
	}

	protected void resolve(IfElse s, HashMap<String,Type> environment) {
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
			throws ResolveError {
		List<Expr> args = s.arguments;
		
		ArrayList<Type> ptypes = new ArrayList<Type>();		
		for(Expr e : args) {			
			Type e_t = resolve(e,environment);
			ptypes.add(e_t);			
		}
		
		Type.Process receiver = null;
		if(s.receiver != null) {			
			Type tmp = resolve(s.receiver,environment);
			checkType(tmp,Type.Process.class,s.receiver);
			receiver = (Type.Process) tmp;
		}
		
		Attribute.Module modInfo = s.attribute(Attribute.Module.class);
		Type.Fun funtype = bindFunction(modInfo.module, s.name, receiver, ptypes,s);
		
		if(funtype == null) {
			syntaxError("invalid or ambiguous method call",s);
		}
		
		s.attributes().add(new Attribute.Fun(funtype));
		
		return funtype.ret;									
	}
			
	protected Type resolve(Constant c, HashMap<String,Type> environment)  {
		return c.val.type();
	}
	
	protected Type resolve(Variable v, HashMap<String,Type> environment)  {
		Type t = environment.get(v.var);
		if(t == null) {		
			syntaxError("unknown variable",v);			
		}
		return t;
	}
	
	protected Type resolve(UnOp v, HashMap<String,Type> environment,
			ArrayList<PkgID> imports)  {
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
	
	protected Type resolve(NaryOp v, HashMap<String,Type> environment)  {		
		if(v.nop == NOp.SUBLIST) {
			if(v.arguments.size() != 3) {
				syntaxError("incorrect number of arguments",v);
			}
			Type src = resolve(v.arguments.get(0), environment);
			Type start = resolve(v.arguments.get(1), environment);
			Type end = resolve(v.arguments.get(2), environment);
			checkType(src,Type.List.class,v.arguments.get(0));
			checkType(start,Type.Int.class,v.arguments.get(1));
			checkType(end,Type.Int.class,v.arguments.get(2));
			return ((Type.List)src).element;
		} else {

			Type etype = Type.T_VOID;

			for(Expr e : v.arguments) {
				Type t = resolve(e, environment);
				etype = Type.leastUpperBound(t,etype);
			}		

			if(v.nop == NOp.LISTGEN) {
				return Type.T_LIST(etype);
			} else {
				return Type.T_SET(etype);
			} 
		}
	}
	
	protected Type resolve(Comprehension e, HashMap<String,Type> environment)  {				
		throw new RuntimeException("Need to implement type resolution for Comprehension");
	}
		
	protected Type resolve(TupleGen sg, HashMap<String, Type> environment)
			 {
		HashMap<String, Type> types = new HashMap<String, Type>();
		for (Map.Entry<String, Expr> e : sg.fields.entrySet()) {
			Type t = resolve(e.getValue(), environment);
			types.put(e.getKey(), t);
		}
		return Type.T_TUPLE(types);
	}
	
	protected Type resolve(TupleAccess sg, HashMap<String, Type> environment)
			 {
		Type lhs = resolve(sg.lhs, environment);
		// FIXME: will need to determine effective tuple type here
		Type.Tuple tup = checkType(lhs, Type.Tuple.class, sg.lhs);
		Type ft = tup.types.get(sg.name);
		if (ft == null) {
			syntaxError("type has no field named: " + sg.name, sg.lhs);
		}
		return ft;
	}
	
	protected Type resolve(UnresolvedType t) throws ResolveError {
		if(t instanceof UnresolvedType.Any) {
			return Type.T_ANY;
		} else if(t instanceof UnresolvedType.Existential) {
			return Type.T_EXISTENTIAL;
		} else if(t instanceof UnresolvedType.Void) {
			return Type.T_VOID;
		} else if(t instanceof UnresolvedType.Bool) {
			return Type.T_BOOL;
		} else if(t instanceof UnresolvedType.Int) {
			return Type.T_INT;
		} else if(t instanceof UnresolvedType.Real) {
			return Type.T_REAL;
		} else if(t instanceof UnresolvedType.List) {
			UnresolvedType.List lt = (UnresolvedType.List) t;			
			return Type.T_LIST(resolve(lt.element));
		} else if(t instanceof UnresolvedType.Set) {
			UnresolvedType.Set st = (UnresolvedType.Set) t;			
			return Type.T_SET(resolve(st.element));
		} else if(t instanceof UnresolvedType.Tuple) {
			UnresolvedType.Tuple tt = (UnresolvedType.Tuple) t;
			HashMap<String,Type> types = new HashMap<String,Type>();
			for(Map.Entry<String,UnresolvedType> e : tt.types.entrySet()) {
				types.put(e.getKey(),resolve(e.getValue()));
			}
			return Type.T_TUPLE(types);
		} else if(t instanceof UnresolvedType.Named) {
			UnresolvedType.Named dt = (UnresolvedType.Named) t;						
			ModuleID mid = dt.attribute(Attribute.Module.class).module;
			if(modules.contains(mid)) {
				return types.get(new NameID(mid,dt.name));
			} else {
				Module mi = loader.loadModule(mid);
				Module.TypeDef td = mi.type(dt.name);			
				return td.type();
			}
		} else if(t instanceof UnresolvedType.Union) {
			UnresolvedType.Union ut = (UnresolvedType.Union) t;
			HashSet<Type.NonUnion> bounds = new HashSet<Type.NonUnion>();
			for(UnresolvedType b : ut.bounds) {
				Type bt = resolve(b);
				if(bt instanceof Type.NonUnion) {
					bounds.add((Type.NonUnion)t);
				} else {
					bounds.addAll(((Type.Union)bt).bounds);
				}
			}
			if(bounds.size() == 1) {
				return bounds.iterator().next();
			} else {
				return Type.T_UNION(bounds);
			}
		} else {	
			UnresolvedType.Process ut = (UnresolvedType.Process) t;
			return Type.T_PROCESS(resolve(ut.element));			
		} 
	}
	
	protected Type.Fun bindFunction(ModuleID mid, String name,
			Type.Process receiver,
			List<Type> paramTypes,
			SyntacticElement elem) throws ResolveError {
		
		Type.Fun target = Type.T_FUN(receiver, Type.T_ANY,paramTypes);
		Type.Fun candidate = null;
								
		for (Type.Fun ft : lookupMethod(mid,name)) {										
			Type funrec = ft.receiver;
			
			if (receiver == funrec
					|| (receiver != null && funrec != null && Type.isSubtype(
							funrec, receiver))) {
				// receivers match up OK ...
				if (ft.params.size() == paramTypes.size()						
						&& Type.isSubtype(ft, target)
						&& (candidate == null || Type.isSubtype(candidate, ft))) {
					// This declaration is a candidate. Now, we need to see if
					// our
					// candidate type signature is as precise as possible.
					if (candidate == null) {
						candidate = ft;
					} else if (Type.isSubtype(candidate, ft)) {
						candidate = ft;
					}
				}
			}
		}				
		
		return candidate;
	}
	
	protected List<Type.Fun> lookupMethod(ModuleID mid, String name)
			throws ResolveError {
		if (modules.contains(mid)) {
			NameID key = new NameID(mid, name);
			return functions.get(key);
		} else {
			Module module = loader.loadModule(mid);
			ArrayList<Type.Fun> rs = new ArrayList<Type.Fun>();
			for (Module.Method m : module.method(name)) {
				rs.add(m.type());
			}
			return rs;
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
