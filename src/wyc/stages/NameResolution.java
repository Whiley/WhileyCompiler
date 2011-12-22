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
import wyc.TypeResolver;
import wyc.lang.*;
import wyc.lang.WhileyFile.*;

public final class NameResolution {
	private final NameResolver resolver;	
	private String filename;
	private ModuleID module;
	
	public NameResolution(NameResolver resolver) {
		this.resolver = resolver;
	}
	
	public TypeResolver.Skeleton resolve(WhileyFile wf) {		
		module = wf.module;
		filename = wf.filename;
		
		ArrayList<Import> imports = new ArrayList<Import>();		
		imports.add(new Import(module.pkg(), module.module(), "*")); 
		imports.add(new Import(module.pkg(), "*")); 

		final HashMap<String,Type> types = new HashMap<String,Type>();
		final HashMap<String,Value> constants = new HashMap<String,Value>();
		final HashMap<String,List<Type.Function>> functions = new HashMap<String,List<Type.Function>>();
		
		for(Decl d : wf.declarations) {			
			try {
				if(d instanceof ImportDecl) {
					ImportDecl impd = (ImportDecl) d;
					imports.add(1,new Import(new PkgID(impd.pkg),impd.module,impd.name));
				} else if(d instanceof FunDecl) {
					Type.Function t = resolve((FunDecl)d,imports);					
					List<Type.Function> funs = functions.get(d.name()); 
					if(funs == null) {
						funs = new ArrayList<Type.Function>();
						functions.put(d.name(), funs);
					}
					funs.add(t);					
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
		
		return new TypeResolver.Skeleton(module) {
			public Type type(String name) {
				return types.get(name);
			}
			public Value constant(String name) {
				return constants.get(name);
			}
			public List<Type.Function> functionOrMethod(String name) {
				List<Type.Function> r = functions.get(name);
				if(r == null) {
					return Collections.EMPTY_LIST;
				} else {
					return r;
				}
			}			
		};
	}
	
	private Value resolve(ConstDecl td, ArrayList<Import> imports) throws ResolveError {	
		return td.value;			
	}
	
	private Type resolve(TypeDecl td, ArrayList<Import> imports) throws ResolveError {
		try {
			Type type = resolver.resolve(td.unresolvedType, imports); 
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
				Type type = resolver.resolve(p.type, imports);				
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
			ret = resolver.resolve(fd.ret, imports);			
			throwsClause = resolver.resolve(fd.throwType, imports);
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
					receiver = resolver.resolve(md.receiver, imports);			
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
