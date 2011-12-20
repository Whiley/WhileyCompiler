// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyc.stages;

import java.util.*;

import static wyil.util.SyntaxError.*;
import static wyil.util.ErrorMessages.*;
import wyil.util.*;
import wyil.lang.*;
import wyil.lang.Type;
import wyc.NameResolver;
import wyc.TypeExpander;
import wyc.lang.*;
import wyc.lang.WhileyFile.*;

public final class Resolution {
	private final NameResolver resolver;	
	private String filename;
	private ModuleID module;
	
	public Resolution(NameResolver resolver) {
		this.resolver = resolver;
	}
	
	public TypeExpander.Skeleton resolve(WhileyFile wf) {		
		module = wf.module;
		filename = wf.filename;
		
		ArrayList<Import> imports = new ArrayList<Import>();		
		imports.add(new Import(module.pkg(), module.module(), "*")); 
		imports.add(new Import(module.pkg(), "*")); 

		final HashMap<String,Type> types = new HashMap<String,Type>();
		final HashMap<String,Value> constants = new HashMap<String,Value>();
		final HashMap<String,Type.Function> functions = new HashMap<String,Type.Function>();
		
		for(Decl d : wf.declarations) {			
			try {
				if(d instanceof ImportDecl) {
					ImportDecl impd = (ImportDecl) d;
					imports.add(1,new Import(new PkgID(impd.pkg),impd.module,impd.name));
				} else if(d instanceof FunDecl) {
					Type.Function t = resolve((FunDecl)d,imports);
					functions.put(d.name(), t);
				} else if(d instanceof TypeDecl) {
					Type t = resolve((TypeDecl)d,imports);
					types.put(d.name(), t);
				} else if(d instanceof ConstDecl) {
					Value v = resolve((ConstDecl)d,imports);
					constants.put(d.name(), v);
				}
			} catch(ResolveError ex) {
				syntaxError(errorMessage(RESOLUTION_ERROR, ex.getMessage()),
						filename, d);
			}
		}		
		
		return new TypeExpander.Skeleton(module) {
			public Type type(String name) {
				return types.get(name);
			}
			public Value constant(String name) {
				return constants.get(name);
			}
			public Type.Function function(String name) {
				return functions.get(name);
			}
		};
	}
	
	private Value resolve(ConstDecl td, ArrayList<Import> imports) throws ResolveError {	
		return td.value;			
	}
	
	private Type resolve(TypeDecl td, ArrayList<Import> imports) throws ResolveError {
		try {
			Type type = resolve(td.unresolvedType, imports, resolver); 
			td.nominalType = type;			
			return type;
		} catch (ResolveError e) {												
			// Ok, we've hit a resolution error.
			syntaxError(errorMessage(RESOLUTION_ERROR, e.getMessage()),
					filename, td);
			return null; // dead-code
		}		
	}	
	
	private Type.Function resolve(FunDecl fd, ArrayList<Import> imports) {
		HashMap<String,Set<Expr>> environment = new HashMap<String,Set<Expr>>();
		
		// method parameter types
		ArrayList<Type> parameters = new ArrayList<Type>();
		for (WhileyFile.Parameter p : fd.parameters) {
			try {
				Type type = resolve(p.type, imports, resolver);				
				parameters.add(type);
				environment.put(p.name(),Collections.EMPTY_SET);
			} catch (ResolveError e) {												
				// Ok, we've hit a resolution error.
				syntaxError(errorMessage(RESOLUTION_ERROR, e.getMessage()), filename, p, e);
			}
		}
		
		if (fd instanceof MethDecl) {
			MethDecl md = (MethDecl) fd;
			if(md.receiver != null) {		
				environment.put("this",Collections.EMPTY_SET);
			}
		}
		
		// method return and throw types
		Type ret;
		Type throwsClause;
		try {
			ret = resolve(fd.ret, imports, resolver);			
			throwsClause = resolve(fd.throwType, imports, resolver);
		} catch (ResolveError e) {
			// Ok, we've hit a resolution error.
			syntaxError(errorMessage(RESOLUTION_ERROR, e.getMessage()), filename,
					fd.ret);
			return null; // dead-code
		}
		
		// method receiver type (if applicable)
		Type receiver = null;
		if(fd instanceof MethDecl) {			
			MethDecl md = (MethDecl) fd;
			// check whether this is a headless method or not
			if(md.receiver != null) {
				try {			
					receiver = resolve(md.receiver, imports, resolver);			
				} catch (ResolveError e) {
					// Ok, we've hit a resolution error.
					syntaxError(errorMessage(RESOLUTION_ERROR, e.getMessage()),
							filename, md.receiver);
				}
			}
		}
		
		Type.Function funType;
		
		if(fd instanceof MethDecl) {
			funType = checkType(Type.Method(receiver,ret,throwsClause,parameters),Type.Method.class,fd);
		} else {
			funType = checkType(Type.Function(ret,throwsClause,parameters),Type.Function.class,fd);
		}
		
		fd.nominalType = funType;		
		return funType;
	}
	
	private Expr resolve(Expr.AbstractDotAccess sg,
			HashMap<String, Set<Expr>> environment, ArrayList<Import> imports)
			throws ResolveError {		
		Expr lhs = null; //resolve(sg.src, environment, imports);
		if(lhs instanceof Expr.PackageAccess) {
			// this indicates we're constructing a more complex package access.
			Expr.PackageAccess pa = (Expr.PackageAccess) lhs;			
			try {				
				ModuleID mid = new ModuleID(pa.pid,sg.name);
				NameResolver.Skeleton m = resolver.loadSkeleton(mid);
				return new Expr.ModuleAccess(pa,sg.name,mid,sg.attributes());
			} catch(ResolveError err) {}
			PkgID pid = pa.pid.append(sg.name);			
			if(resolver.isPackage(pid)) {				
				return new Expr.PackageAccess(pa,sg.name,pid,sg.attributes());				
			} else {
				syntaxError(errorMessage(INVALID_PACKAGE_ACCESS),filename,pa);
				return null; // deadcode
			}
		} else if(lhs instanceof Expr.ModuleAccess) {
			// this indicates we're constructing a constant access
			Expr.ModuleAccess ma = (Expr.ModuleAccess) lhs;			
			try {				
				NameResolver.Skeleton m = resolver.loadSkeleton(ma.mid);				
				if(m.hasName(sg.name)) {
					NameID nid = new NameID(ma.mid,sg.name);
					return new Expr.ConstantAccess(ma,sg.name,nid,sg.attributes());
				}				
			} catch(ResolveError err) {}			
			syntaxError(errorMessage(INVALID_MODULE_ACCESS),filename,ma);	
			return null; // deadcode
		} else {
			// must be a standard record lookup
			return new Expr.RecordAccess(lhs,sg.name,sg.attributes());
		}			
	}
	
	public static Type resolve(UnresolvedType t, ArrayList<Import> imports,
			NameResolver resolver) throws ResolveError {		
		if (t instanceof UnresolvedType.Any) {
			return Type.T_ANY;
		} else if (t instanceof UnresolvedType.Void) {
			return Type.T_VOID;
		} else if (t instanceof UnresolvedType.Null) {
			return Type.T_NULL;
		} else if (t instanceof UnresolvedType.Bool) {
			return Type.T_BOOL;
		} else if (t instanceof UnresolvedType.Byte) {
			return Type.T_BYTE;
		} else if (t instanceof UnresolvedType.Char) {
			return Type.T_CHAR;
		} else if (t instanceof UnresolvedType.Int) {
			return Type.T_INT;
		} else if (t instanceof UnresolvedType.Real) {
			return Type.T_REAL;
		} else if (t instanceof UnresolvedType.Strung) {
			return Type.T_STRING;
		} else if(t instanceof UnresolvedType.List) {
			UnresolvedType.List lt = (UnresolvedType.List) t;
			Type element = resolve(lt.element,imports,resolver);
			return Type.List(element,false);
		} else if(t instanceof UnresolvedType.Set) {
			UnresolvedType.Set st = (UnresolvedType.Set) t;
			Type element = resolve(st.element,imports,resolver);
			return Type.Set(element,false);
		} else if(t instanceof UnresolvedType.Dictionary) {
			UnresolvedType.Dictionary st = (UnresolvedType.Dictionary) t;
			Type key = resolve(st.key,imports,resolver);
			Type value = resolve(st.value,imports,resolver);
			return Type.Dictionary(key,value);
		} else if(t instanceof UnresolvedType.Record) {
			UnresolvedType.Record tt = (UnresolvedType.Record) t;
			HashMap<String,Type> fields = new HashMap<String,Type>();
			for(Map.Entry<String,UnresolvedType> e : tt.types.entrySet()) {				
				Type type = resolve(e.getValue(),imports,resolver);
				fields.put(e.getKey(), type);
			}
			return Type.Record(tt.isOpen,fields);
		} else if(t instanceof UnresolvedType.Tuple) {
			UnresolvedType.Tuple tt = (UnresolvedType.Tuple) t;
			ArrayList<Type> types = new ArrayList<Type>();
			for(UnresolvedType e : tt.types) {
				Type type = resolve(e,imports,resolver);
				types.add(type);
			}
			return Type.Tuple(types);
		} else if(t instanceof UnresolvedType.Nominal) {
			// This case corresponds to a user-defined type. This will be
			// defined in some module (possibly ours), and we need to identify
			// what module that is here, and save it for future use.
			UnresolvedType.Nominal dt = (UnresolvedType.Nominal) t;						
			NameID nid = resolver.resolveAsName(dt.names, imports);			
			return Type.Nominal(nid);
		} else if(t instanceof UnresolvedType.Not) {	
			UnresolvedType.Not ut = (UnresolvedType.Not) t;
			Type type = resolve(ut.element,imports,resolver);
			return Type.Negation(type);
		} else if(t instanceof UnresolvedType.Union) {
			UnresolvedType.Union ut = (UnresolvedType.Union) t;
			ArrayList<Type> bounds = new ArrayList<Type>();
			for(UnresolvedType b : ut.bounds) {
				Type bound = resolve(b,imports,resolver);
				bounds.add(bound);
			}
			return Type.Union(bounds);
		} else if(t instanceof UnresolvedType.Intersection) {
			UnresolvedType.Intersection ut = (UnresolvedType.Intersection) t;
			for(UnresolvedType b : ut.bounds) {
				resolve(b,imports,resolver);
			}
			throw new RuntimeException("CANNOT COPE WITH INTERSECTIONS!");
		} else if(t instanceof UnresolvedType.Process) {	
			UnresolvedType.Process ut = (UnresolvedType.Process) t;
			Type type = resolve(ut.element,imports,resolver);
			return Type.Process(type);
		} else {	
			UnresolvedType.Fun ut = (UnresolvedType.Fun) t;
			Type ret = resolve(ut.ret,imports,resolver);			
			
			ArrayList<Type> params = new ArrayList<Type>();
			for(UnresolvedType p : ut.paramTypes) {
				params.add(resolve(p,imports,resolver));
			}
			
			if(ut.receiver != null) {
				Type receiver = resolve(ut.receiver,imports,resolver);
				return Type.Method(receiver, ret, Type.T_VOID, params);
			} else {
				return Type.Function(ret, Type.T_VOID, params);
			}
		} 
	}
	
	private <T extends Type> T checkType(Type t, Class<T> clazz,
			SyntacticElement elem) {		
		if (clazz.isInstance(t)) {
			return (T) t;
		} else {
			// TODO: need a better error message here.
			String errMsg = errorMessage(SUBTYPE_ERROR,clazz.getName().replace('$',' '),t);
			syntaxError(errMsg, filename, elem);
			return null;
		}
	}
}
