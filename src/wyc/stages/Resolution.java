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
import wyil.ModuleLoader;
import wyil.util.*;
import wyil.lang.*;
import wyil.lang.Type;
import wyc.NameResolver;
import wyc.TypeExpander;
import wyc.lang.*;
import wyc.lang.WhileyFile.*;
import wyc.lang.Stmt;
import wyc.stages.WhileyLexer.Ampersand;
import wyc.util.*;

/**
 * Responsible for determining the fully-qualified names for all external
 * symbols in a module. For example, consider the following module:
 * 
 * <pre>
 * import whiley.lang.*
 * 
 * int f(int x):
 *    return Math.max(x,10)
 * </pre>
 * 
 * Here, <code>Math.max</code> is an external symbol and we must determine the
 * package where it is defined, or report a syntax error if none is found.
 * Therefore, name resolution walks the list of import declarations looking for
 * the first occurrence of a matching symbol, and records this as an attribute
 * on the corresponding Abstract Syntax Tree (AST) node.
 * 
 * @author David J. Pearce
 */
public final class Resolution {
	private final NameResolver resolver;	
	private String filename;
	private ModuleID module;
	
	public Resolution(NameResolver resolver) {
		this.resolver = resolver;
	}
	
	public TypeExpander.Skeleton resolve(WhileyFile wf) {
		ArrayList<Import> imports = new ArrayList<Import>();
		
		module = wf.module;
		filename = wf.filename;
		
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
			Type type = resolve(td.unresolvedType, imports); 
			td.type = type;
			if (td.constraint != null) {
				HashMap<String, Set<Expr>> environment = new HashMap<String, Set<Expr>>();
				environment.put("$", Collections.EMPTY_SET);
				addExposedNames(
						new Expr.LocalVariable("$", td.constraint
								.attribute(Attribute.Source.class)),
						td.unresolvedType, environment);
				resolve(td.constraint, environment, imports);
			}
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
				Type type = resolve(p.type, imports);				
				parameters.add(type);
				environment.put(p.name(),Collections.EMPTY_SET);
			} catch (ResolveError e) {												
				// Ok, we've hit a resolution error.
				syntaxError(e.getMessage(), filename, p, e);
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
			ret = resolve(fd.ret, imports);			
			throwsClause = resolve(fd.throwType, imports);
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
			try {			
				receiver = resolve(md.receiver, imports);			
			} catch (ResolveError e) {
				// Ok, we've hit a resolution error.
				syntaxError(errorMessage(RESOLUTION_ERROR, e.getMessage()),
						filename, md.receiver);
			}
		}
		
		if (fd.precondition != null) {
			resolve(fd.precondition, environment, imports);
		}

		if (fd.postcondition != null) {
			environment.put("$", Collections.EMPTY_SET);
			resolve(fd.postcondition, environment, imports);
			environment.remove("$");
		}

		List<Stmt> stmts = fd.statements;
		for (int i=0;i!=stmts.size();++i) {
			resolve(stmts.get(i), environment, imports);							
		}
		
		Type.Function funType;
		
		if(fd instanceof MethDecl) {
			funType = checkType(Type.Method(ret,receiver,throwsClause,parameters),Type.Method.class,fd);
		} else {
			funType = checkType(Type.Function(ret,throwsClause,parameters),Type.Function.class,fd);
		}
		
		fd.nominalType = funType;		
		return funType;
	}
	
	private void resolve(Stmt s, HashMap<String,Set<Expr>> environment, ArrayList<Import> imports) {
		try {
			if(s instanceof Stmt.Assign) {
				resolve((Stmt.Assign)s, environment, imports);
			} else if(s instanceof Stmt.Assert) {
				resolve((Stmt.Assert)s, environment, imports);
			} else if(s instanceof Stmt.Return) {
				resolve((Stmt.Return)s, environment, imports);
			} else if(s instanceof Stmt.Debug) {
				resolve((Stmt.Debug)s, environment, imports);
			} else if(s instanceof Stmt.Skip || s instanceof Stmt.Break) {
				// do nothing
			} else if(s instanceof Stmt.Throw) {
				resolve((Stmt.Throw)s, environment, imports);
			} else if(s instanceof Stmt.IfElse) {
				resolve((Stmt.IfElse)s, environment, imports);
			} else if(s instanceof Stmt.Switch) {
				resolve((Stmt.Switch)s, environment, imports);
			} else if(s instanceof Stmt.TryCatch) {
				resolve((Stmt.TryCatch)s, environment, imports);
			} else if(s instanceof Stmt.While) {
				resolve((Stmt.While)s, environment, imports);
			} else if(s instanceof Stmt.DoWhile) {
				resolve((Stmt.DoWhile)s, environment, imports);
			} else if(s instanceof Stmt.For) {
				resolve((Stmt.For)s, environment, imports);
			} else if(s instanceof Expr.Invoke) {
				resolve((Expr.Invoke)s, environment, imports);
			} else if(s instanceof Expr.Spawn) {
				resolve((Expr.Spawn)s, environment, imports);
			} else {
				internalFailure("unknown statement encountered: "
						+ s.getClass().getName(), filename, s);				
			}
		} catch (ResolveError e) {
			// Ok, we've hit a resolution error.
			syntaxError(errorMessage(RESOLUTION_ERROR, e.getMessage()),
					filename, s);			
		}
	}	

	private void resolve(Stmt.Assign s, HashMap<String,Set<Expr>> environment,
			ArrayList<Import> imports) {
		if(s.lhs instanceof Expr.AbstractVariable) {
			Expr.AbstractVariable v = (Expr.AbstractVariable) s.lhs;
			environment.put(v.var, Collections.EMPTY_SET);
			s.lhs = new Expr.LocalVariable(v.var,v.attributes());
		} else if(s.lhs instanceof Expr.Tuple) {
			Expr.Tuple tg = (Expr.Tuple) s.lhs;
			for(int i=0;i!=tg.fields.size();++i) {
				Expr e = tg.fields.get(i);
				if(e instanceof Expr.AbstractVariable) {
					Expr.AbstractVariable v = (Expr.AbstractVariable) e;
					tg.fields.set(i,new Expr.LocalVariable(v.var,e.attributes()));
					environment.put(v.var, Collections.EMPTY_SET);
				} else {
					syntaxError(errorMessage(INVALID_TUPLE_LVAL), filename, e);
				}
			}
		} else {
			s.lhs = (Expr.LVal) resolve(s.lhs, environment, imports);
		}
		s.rhs = resolve(s.rhs, environment, imports);	
	}

	private void resolve(Stmt.Assert s, HashMap<String,Set<Expr>> environment,
			ArrayList<Import> imports) {
		s.expr = resolve(s.expr, environment, imports);		
	}

	private void resolve(Stmt.Return s, HashMap<String,Set<Expr>> environment,
			ArrayList<Import> imports) {
		if(s.expr != null) {
			s.expr = resolve(s.expr, environment, imports);
		}
	}
	
	private void resolve(Stmt.Debug s, HashMap<String,Set<Expr>> environment,
			ArrayList<Import> imports) {
		s.expr = resolve(s.expr, environment, imports);		
	}

	private void resolve(Stmt.Throw s, HashMap<String, Set<Expr>> environment,
			ArrayList<Import> imports) {
		s.expr = resolve(s.expr, environment, imports);
	}
	
	private void resolve(Stmt.IfElse s, HashMap<String, Set<Expr>> environment,
			ArrayList<Import> imports) {
		s.condition = resolve(s.condition, environment, imports);
		for (Stmt st : s.trueBranch) {
			resolve(st, environment, imports);
		}
		if (s.falseBranch != null) {
			for (Stmt st : s.falseBranch) {
				resolve(st, environment, imports);
			}			
		}
	}
	
	private void resolve(Stmt.Switch s, HashMap<String, Set<Expr>> environment,
			ArrayList<Import> imports) {		
		
		s.expr = resolve(s.expr, environment, imports);
		
		for(Stmt.Case c : s.cases){			
			for(int i=0;i!=c.expr.size();++i) {
				Expr v = c.expr.get(i);
				v = resolve(v,environment,imports);
				c.expr.set(i, v);
			}
			for (Stmt st : c.stmts) {
				resolve(st, environment, imports);
			}			
		}		
	}
	
	private void resolve(Stmt.TryCatch s, HashMap<String, Set<Expr>> environment,
			ArrayList<Import> imports) throws ResolveError {		
		
		for(Stmt st : s.body) {
			resolve(st, environment, imports);
		}
			
		for (Stmt.Catch c : s.catches) {
			HashMap<String, Set<Expr>> nenvironment = new HashMap<String, Set<Expr>>(environment);
			if (nenvironment.containsKey(c.variable)) {
				syntaxError(errorMessage(VARIABLE_ALREADY_DEFINED, c.variable),
						filename, s);
			}
			nenvironment.put(c.variable, Collections.EMPTY_SET);
			c.type = resolve(c.unresolvedType, imports);
			for (Stmt st : c.stmts) {
				resolve(st, nenvironment, imports);
			}
		}
	}
	
	private void resolve(Stmt.While s, HashMap<String,Set<Expr>> environment,
			ArrayList<Import> imports) {
		s.condition = resolve(s.condition, environment, imports);
		if (s.invariant != null) {
			s.invariant = resolve(s.invariant, environment, imports);
		}
		environment = new HashMap<String,Set<Expr>>(environment);
		for (Stmt st : s.body) {
			resolve(st, environment, imports);
		}
	}
	
	private void resolve(Stmt.DoWhile s, HashMap<String,Set<Expr>> environment,
			ArrayList<Import> imports) {
		s.condition = resolve(s.condition, environment, imports);
		if (s.invariant != null) {
			s.invariant = resolve(s.invariant, environment, imports);
		}
		environment = new HashMap<String,Set<Expr>>(environment);
		for (Stmt st : s.body) {
			resolve(st, environment, imports);
		}
	}
	
	private void resolve(Stmt.For s, HashMap<String,Set<Expr>> environment,
			ArrayList<Import> imports) {
		s.source = resolve(s.source, environment, imports);		
		if (s.invariant != null) {
			s.invariant = resolve(s.invariant, environment, imports);
		}
		environment = new HashMap<String,Set<Expr>>(environment);
		for(String var : s.variables) {
			if (environment.containsKey(var)) {
				syntaxError(errorMessage(VARIABLE_ALREADY_DEFINED,var),
						filename, s);
			}
			environment.put(var, Collections.EMPTY_SET);
		}				
		for (Stmt st : s.body) {
			resolve(st, environment, imports);
		}
	}
	private Expr resolve(Expr e, HashMap<String,Set<Expr>> environment, ArrayList<Import> imports) {
		try {
			if (e instanceof Expr.Constant) {
				
			} else if (e instanceof Expr.LocalVariable) {
				// this case is possible because of the way WhileyParser handles
				// sublist expressions.
			} else if (e instanceof Expr.AbstractVariable) {
				e = resolve((Expr.AbstractVariable)e, environment, imports);
			} else if (e instanceof Expr.Set) {
				e = resolve((Expr.Set)e, environment, imports);
			} else if (e instanceof Expr.List) {
				e = resolve((Expr.List)e, environment, imports);
			} else if (e instanceof Expr.SubList) {
				e = resolve((Expr.SubList)e, environment, imports);
			} else if (e instanceof Expr.Comprehension) {
				e = resolve((Expr.Comprehension) e, environment, imports);
			} else if (e instanceof Expr.BinOp) {
				e = resolve((Expr.BinOp)e, environment, imports);
			} else if (e instanceof Expr.Convert) {
				e = resolve((Expr.Convert)e, environment, imports);
			} else if (e instanceof Expr.AbstractIndexAccess) {
				e = resolve((Expr.AbstractIndexAccess)e, environment, imports);
			} else if (e instanceof Expr.UnOp) {
				e = resolve((Expr.UnOp)e, environment, imports);
			} else if (e instanceof Expr.Spawn) {
				e = resolve((Expr.Spawn)e, environment, imports);
			} else if (e instanceof Expr.Invoke) {
				e = resolve((Expr.Invoke)e, environment, imports);
			} else if (e instanceof Expr.AbstractLength) {
				e = resolve((Expr.AbstractLength) e, environment, imports);
			} else if (e instanceof Expr.ProcessAccess) {
				e = resolve((Expr.ProcessAccess) e, environment, imports);
			}else if (e instanceof Expr.AbstractDotAccess) {
				e = resolve((Expr.AbstractDotAccess) e, environment, imports);
			} else if (e instanceof Expr.Record) {
				e = resolve((Expr.Record) e, environment, imports);
			} else if (e instanceof Expr.Tuple) {
				e = resolve((Expr.Tuple) e, environment, imports);
			} else if (e instanceof Expr.Dictionary) {
				e = resolve((Expr.Dictionary) e, environment, imports);
			} else if(e instanceof Expr.TypeVal) {
				e = resolve((Expr.TypeVal) e, environment, imports);
			} else if(e instanceof Expr.Function) {
				e = resolve((Expr.Function) e, environment, imports);
			} else {				
				internalFailure("unknown expression encountered: "
							+ e.getClass().getName(), filename, e);								
			}
		} catch(ResolveError re) {
			syntaxError(re.getMessage(),filename,e,re);			
		} catch(SyntaxError se) {
			throw se;
		} catch(Exception ex) {
			internalFailure("internal failure", filename, e, ex);			
		}	
		
		return e;
	}
	
	private Expr resolve(Expr.Invoke ivk, HashMap<String,Set<Expr>> environment,
			ArrayList<Import> imports) throws ResolveError {
					
		for(int i=0;i!=ivk.arguments.size();++i) {
			Expr e = ivk.arguments.get(i);
			e = resolve(e, environment, imports);
			ivk.arguments.set(i,e);
		}

		if(!environment.containsKey(ivk.name)) {			
			// only look for non-local function binding if there is not a local
			// variable with the same name.
			Expr target = ivk.receiver;			
			if(target != null) {
				target = resolve(target,environment,imports);
				if(target instanceof Expr.ModuleAccess) {
					// In this case, an explicit module identifier has been
					// provided, so we don't need to resolve.
					Expr.ModuleAccess maccess = (Expr.ModuleAccess) target;
					ivk.receiver = null;					
					ivk.attributes().add(new Attributes.Module(maccess.mid));
				} else {
					ivk.receiver = target;
					try {						
						NameID nid = resolver.resolveAsName(ivk.name,imports);
						ivk.attributes().add(new Attributes.Module(nid.module()));	
					} catch(ResolveError e) {
						// in this case, we've been unable to resolve the method
						// being called. However, this does not necessarily indicate
						// an error --- this could be a field dereferences, followed
						// by an indirect function call.
					}
				}
			} else {				
				NameID nid = resolver.resolveAsName(ivk.name,imports);				
				// Ok, resolve the module for this invoke
				ivk.attributes().add(new Attributes.Module(nid.module()));		
			}
		} else if(ivk.receiver != null) {
			ivk.receiver = resolve(ivk.receiver,environment,imports);
		} 
		
		return ivk;
	}
	
	private Expr resolve(Expr.AbstractVariable v, HashMap<String, Set<Expr>> environment,
			ArrayList<Import> imports) throws ResolveError {
		
		Set<Expr> aliases = environment.get(v.var);		
		if (aliases == null) {
			// This variable access may correspond to an external access.
			// Therefore, we must determine which module this
			// is, and update the tree accordingly.
			try {
				NameID nid = resolver.resolveAsName(v.var, imports);				
				return new Expr.ConstantAccess(null,v.var,nid,v.attributes());				
			} catch(ResolveError err) {}
			// In this case, we may still be OK if this corresponds to an
			// explicit module or package access.
			try {
				ModuleID mid = resolver.resolveAsModule(v.var, imports);				
				return new Expr.ModuleAccess(null,v.var,mid,v.attributes());
			} catch(ResolveError err) {}			
			PkgID pid = new PkgID(v.var);
			if (resolver.isPackage(pid)) {
				return new Expr.PackageAccess(null,v.var,pid, v.attributes());
			}
			// ok, failed.
			syntaxError(errorMessage(UNKNOWN_VARIABLE), filename, v);			
		} else if (aliases.size() == 1) {			
			return aliases.iterator().next();			
		} else if (aliases.size() > 1) {
			syntaxError(errorMessage(AMBIGUOUS_VARIABLE), filename, v);
		} else {
			// following signals a local variable	
			return new Expr.LocalVariable(v.var,v.attributes());
		}
		
		return v;
	}
	
	private Expr resolve(Expr.UnOp v, HashMap<String,Set<Expr>> environment,
			ArrayList<Import> imports) throws ResolveError {
		v.mhs = resolve(v.mhs, environment, imports);
		return v;
	}
	
	private Expr resolve(Expr.Spawn v, HashMap<String,Set<Expr>> environment,
			ArrayList<Import> imports) throws ResolveError {
		v.expr = resolve(v.expr, environment, imports);
		return v;
	}
	
	private Expr resolve(Expr.AbstractLength v, HashMap<String,Set<Expr>> environment,
			ArrayList<Import> imports) throws ResolveError {
		v.src = resolve(v.src, environment, imports);
		return v;
	}
	
	private Expr resolve(Expr.ProcessAccess v, HashMap<String,Set<Expr>> environment,
			ArrayList<Import> imports) throws ResolveError {
		v.src = resolve(v.src, environment, imports);
		return v;
	}
	
	private Expr resolve(Expr.BinOp v, HashMap<String,Set<Expr>> environment, ArrayList<Import> imports) {
		v.lhs = resolve(v.lhs, environment, imports);
		v.rhs = resolve(v.rhs, environment, imports);
		return v;
	}
	
	private Expr resolve(Expr.Convert c, HashMap<String,Set<Expr>> environment, ArrayList<Import> imports) throws ResolveError {
		c.nominalType = resolve(c.unresolvedType, imports);
		c.expr = resolve(c.expr, environment, imports);
		return c;
	}
	
	private Expr resolve(Expr.AbstractIndexAccess v, HashMap<String,Set<Expr>> environment,
			ArrayList<Import> imports) {
		v.src = resolve(v.src, environment, imports);
		v.index = resolve(v.index, environment, imports);
		return v;
	}
	
	private Expr resolve(Expr.Set v, HashMap<String,Set<Expr>> environment,
			ArrayList<Import> imports) throws ResolveError {				
		for(int i=0;i!=v.arguments.size();++i) {
			Expr e = v.arguments.get(i);						
			e = resolve(e, environment, imports);
			v.arguments.set(i,e);
		}		
		return v;
	}
	
	private Expr resolve(Expr.List v, HashMap<String,Set<Expr>> environment,
			ArrayList<Import> imports) throws ResolveError {				
		for(int i=0;i!=v.arguments.size();++i) {
			Expr e = v.arguments.get(i);						
			e = resolve(e, environment, imports);
			v.arguments.set(i,e);
		}		
		return v;
	}
	
	private Expr resolve(Expr.SubList v, HashMap<String,Set<Expr>> environment,
			ArrayList<Import> imports) throws ResolveError {				
		v.src = resolve(v.src, environment, imports);
		v.start = resolve(v.start, environment, imports);
		v.end = resolve(v.end, environment, imports);				
		return v;
	}
	private Expr resolve(Expr.Comprehension e, HashMap<String,Set<Expr>> environment, ArrayList<Import> imports) throws ResolveError {						
		HashMap<String,Set<Expr>> nenv = new HashMap<String,Set<Expr>>(environment);
		for(int i=0;i!=e.sources.size();++i) {	
			Pair<String,Expr> me = e.sources.get(i);
			if (environment.containsKey(me.first())) {
				syntaxError(errorMessage(VARIABLE_ALREADY_DEFINED, me.first()),
						filename, e);
			}
			Expr me_second = resolve(me.second(),nenv,imports);
			e.sources.set(i, new Pair(me.first(),me_second));
			nenv.put(me.first(),Collections.EMPTY_SET);
		}		
		if(e.value != null) {			
			e.value = resolve(e.value,nenv,imports);
		}
		if(e.condition != null) {
			e.condition = resolve(e.condition,nenv,imports);
		}	
		return e;
	}	
		
	private Expr resolve(Expr.Record sg, HashMap<String,Set<Expr>> environment,
			ArrayList<Import> imports) throws ResolveError {		
		ArrayList<String> keys = new ArrayList<String>(sg.fields.keySet());
		for(String key : keys) {
			Expr val = sg.fields.get(key); 
			val = resolve(val,environment,imports);
			sg.fields.put(key,val);
		}			
		return sg;
	}

	private Expr resolve(Expr.Tuple sg, HashMap<String,Set<Expr>> environment,
			ArrayList<Import> imports) throws ResolveError {		
		for(int i=0;i!=sg.fields.size();++i) {
			Expr e = sg.fields.get(i);
			e = resolve(e,environment,imports);
			sg.fields.set(i,e);
		}			
		return sg;
	}
	
	private Expr resolve(Expr.Dictionary sg, HashMap<String,Set<Expr>> environment,
			ArrayList<Import> imports) throws ResolveError {		
		for(int i=0;i!=sg.pairs.size();++i) {
			Pair<Expr,Expr> e = sg.pairs.get(i);
			Expr e_first = resolve(e.first(),environment,imports);
			Expr e_second = resolve(e.second(),environment,imports);
			sg.pairs.set(i, new Pair(e_first,e_second));
		}			
		return sg;
	}
	
	private Expr resolve(Expr.TypeVal tc, HashMap<String,Set<Expr>> environment,
			ArrayList<Import> imports) throws ResolveError {		
		tc.nominalType = resolve(tc.unresolvedType,imports);
		return tc;
	}
	
	private Expr resolve(Expr.Function tc, HashMap<String, Set<Expr>> environment,
			ArrayList<Import> imports) throws ResolveError {
		
		if (tc.paramTypes != null) {
			ArrayList<Type> types = new ArrayList<Type>();
			for (UnresolvedType t : tc.paramTypes) {
				types.add(resolve(t, imports));
			}
			// FIXME: include throws clause
			tc.nominalType = checkType(Type.Function(Type.T_VOID, Type.T_VOID, types),
					Type.Function.class, tc);
		}

		NameID nid = resolver.resolveAsName(tc.name, imports);
		tc.attributes().add(new Attributes.Module(nid.module()));

		return tc;
	}
	
	private Expr resolve(Expr.AbstractDotAccess sg,
			HashMap<String, Set<Expr>> environment, ArrayList<Import> imports)
			throws ResolveError {		
		Expr lhs = resolve(sg.src, environment, imports);
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
	
	private Type resolve(UnresolvedType t, ArrayList<Import> imports) throws ResolveError {
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
			Type element = resolve(lt.element,imports);
			return Type.List(element,false);
		} else if(t instanceof UnresolvedType.Set) {
			UnresolvedType.Set st = (UnresolvedType.Set) t;
			Type element = resolve(st.element,imports);
			return Type.Set(element,false);
		} else if(t instanceof UnresolvedType.Dictionary) {
			UnresolvedType.Dictionary st = (UnresolvedType.Dictionary) t;
			Type key = resolve(st.key,imports);
			Type value = resolve(st.value,imports);
			return Type.Dictionary(key,value);
		} else if(t instanceof UnresolvedType.Record) {
			UnresolvedType.Record tt = (UnresolvedType.Record) t;
			HashMap<String,Type> fields = new HashMap<String,Type>();
			for(Map.Entry<String,UnresolvedType> e : tt.types.entrySet()) {				
				Type type = resolve(e.getValue(),imports);
				fields.put(e.getKey(), type);
			}
			return Type.Record(tt.isOpen,fields);
		} else if(t instanceof UnresolvedType.Tuple) {
			UnresolvedType.Tuple tt = (UnresolvedType.Tuple) t;
			ArrayList<Type> types = new ArrayList<Type>();
			for(UnresolvedType e : tt.types) {
				Type type = resolve(e,imports);
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
			Type type = resolve(ut.element,imports);
			return Type.Negation(type);
		} else if(t instanceof UnresolvedType.Union) {
			UnresolvedType.Union ut = (UnresolvedType.Union) t;
			ArrayList<Type> bounds = new ArrayList<Type>();
			for(UnresolvedType b : ut.bounds) {
				Type bound = resolve(b,imports);
				bounds.add(bound);
			}
			return Type.Union(bounds);
		} else if(t instanceof UnresolvedType.Intersection) {
			UnresolvedType.Intersection ut = (UnresolvedType.Intersection) t;
			for(UnresolvedType b : ut.bounds) {
				resolve(b,imports);
			}
			throw new RuntimeException("CANNOT COPE WITH INTERSECTIONS!");
		} else if(t instanceof UnresolvedType.Process) {	
			UnresolvedType.Process ut = (UnresolvedType.Process) t;
			Type type = resolve(ut.element,imports);
			return Type.Process(type);
		} else {	
			UnresolvedType.Fun ut = (UnresolvedType.Fun) t;
			Type ret = resolve(ut.ret,imports);			
			
			ArrayList<Type> params = new ArrayList<Type>();
			for(UnresolvedType p : ut.paramTypes) {
				params.add(resolve(p,imports));
			}
			
			if(ut.receiver != null) {
				Type receiver = resolve(ut.receiver,imports);
				return Type.Method(receiver, ret, Type.T_VOID, params);
			} else {
				return Type.Function(ret, Type.T_VOID, params);
			}
		} 
	}

	
	/**
	 * The purpose of the exposed names method is capture the case when we have
	 * a define statement like this:
	 * 
	 * <pre>
	 * define tup as {int x, int y} where x < y
	 * </pre>
	 * 
	 * In this case, <code>x</code> and <code>y</code> are "exposed" --- meaning
	 * their real names are different in some way. In this case, the aliases we
	 * have are: x->$.x and y->$.y
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
		if(t instanceof UnresolvedType.Record) {
			UnresolvedType.Record tt = (UnresolvedType.Record) t;
			for(Map.Entry<String,UnresolvedType> e : tt.types.entrySet()) {
				Expr s = new Expr.RecordAccess(src, e
						.getKey(), src.attribute(Attribute.Source.class));
				addExposedNames(s,e.getValue(),environment);
				Set<Expr> aliases = environment.get(e.getKey());
				if(aliases == null) {
					aliases = new HashSet<Expr>();
					environment.put(e.getKey(),aliases);
				}
				aliases.add(s);
			}
		} else if (t instanceof UnresolvedType.Process) {			
			UnresolvedType.Process ut = (UnresolvedType.Process) t;
			addExposedNames(new Expr.ProcessAccess(src),
					ut.element, environment);
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
