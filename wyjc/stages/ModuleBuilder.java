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
import java.math.BigInteger;

import static wyil.util.SyntaxError.*;
import wyil.ModuleLoader;
import wyil.util.*;
import wyil.lang.*;
import wyil.lang.Type.Tuple;
import wyjc.lang.*;
import wyjc.lang.WhileyFile.*;
import wyjc.lang.Stmt.*;
import wyjc.lang.Expr.*;
import wyjc.util.*;

public class ModuleBuilder {
	private final ModuleLoader loader;
	private HashSet<ModuleID> modules;
	private HashMap<NameID,List<Type.Fun>> functions;	
	private HashMap<NameID,Pair<Type,Block>> types;	
	private HashMap<NameID,Pair<UnresolvedType,Expr>> unresolved;
	
	public ModuleBuilder(ModuleLoader loader) {
		this.loader = loader;
	}
	
	public List<Module> resolve(List<WhileyFile> files) {			
		modules = new HashSet<ModuleID>();
		functions = new HashMap<NameID,List<Type.Fun>>();
		types = new HashMap<NameID,Pair<Type,Block>>();		
		unresolved = new HashMap<NameID,Pair<UnresolvedType,Expr>>();
		
		// now, init data
		for (WhileyFile f : files) {			
			modules.add(f.module);
		}
						
		// Stage 1 ... resolve and check types of all named types
		generateTypes(files);
					
		// Stage 2 ... resolve and check types for all functions / methods
		for(WhileyFile f : files) {
			for(WhileyFile.Decl d : f.declarations) {				
				if(d instanceof FunDecl) {
					partResolve(f.module,(FunDecl)d);
				}				
			}
		}
		
		// Stage 3 ... resolve, propagate types for all expressions
		ArrayList<Module> modules = new ArrayList<Module>();
		for(WhileyFile f : files) {
			modules.add(resolve(f));			
		}
		
		return modules;
	}

	public Module resolve(WhileyFile wf) {		
		HashMap<Pair<Type.Fun,String>,Module.Method> methods = new HashMap();
		ArrayList<Module.TypeDef> types = new ArrayList<Module.TypeDef>();
		ArrayList<Module.ConstDef> constants = new ArrayList<Module.ConstDef>();
		for(WhileyFile.Decl d : wf.declarations) {				
			try {
				if(d instanceof TypeDecl) {
					types.add(resolve((TypeDecl)d,wf.module));
				} else if(d instanceof ConstDecl) {
					constants.add(resolve((ConstDecl)d));
				} else if(d instanceof FunDecl) {
					Module.Method mi = resolve((FunDecl)d);
					Pair<Type.Fun,String> key = new Pair(mi.type(),mi.name());
					Module.Method method = methods.get(key);
					if(method != null) {					
						// coalesce cases
						ArrayList<Module.Case> ncases = new ArrayList<Module.Case>(method.cases());
						ncases.addAll(mi.cases());
						mi = new Module.Method(mi.name(), mi.type(), ncases);					
					}
					methods.put(key, mi);
				}				
			} catch(SyntaxError se) {
				throw se;
			} catch(Throwable ex) {
				syntaxError("internal failure",d,ex);
			}
		}
		return new Module(wf.module, wf.filename, methods.values(), types,
				constants);
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
					unresolved.put(key, new Pair<UnresolvedType, Expr>(td.type,
							td.constraint));
					srcs.put(key,d);
				}
			}
		}
		
		// third expand all types
		for(NameID key : unresolved.keySet()) {
			try {
				HashMap<NameID, Type> cache = new HashMap<NameID,Type>();			
				Pair<Type,Block> p = expandType(key,cache);								
				Type t = simplifyRecursiveTypes(p.first());				
				if (Type.isExistential(t)) {					
					t = Type.T_NAMED(key.module(),key.name(), t);
				} 								
				types.put(key,new Pair<Type,Block>(t,p.second()));				
			} catch(ResolveError ex) {
				syntaxError(ex.getMessage(),srcs.get(key),ex);
			}
		}
	}
		
	protected Pair<Type,Block> expandType(NameID key,
			HashMap<NameID, Type> cache) throws ResolveError {

		Pair<Type,Block> t = types.get(key);
		Type cached = cache.get(key);					
		
		if(cached != null) { 
			return new Pair<Type,Block>(cached,null); 
		} else if(t != null) {
			return t;
		} else if(!modules.contains(key.module())) {			
			// indicates a non-local key which we can resolve immediately
			Module mi = loader.loadModule(key.module());
			Module.TypeDef td = mi.type(key.name());			
			return new Pair<Type,Block>(td.type(),null);
		}

		// following is needed to terminate any recursion
		cache.put(key, Type.T_RECURSIVE(key.toString(),null));
		
		// Ok, expand the type properly then
		Pair<UnresolvedType,Expr> ut = unresolved.get(key);
		t = expandType(ut.first(), cache);		
		
		// Resolve the constraint and generate an appropriate block.			
		Block blk = t.second();
		if(ut.second() != null) {			
			HashMap<String,Type> env = new HashMap<String,Type>();
			env.put("$",t.first());
			String trueLabel = Block.freshLabel();
			Block constraint = resolveCondition(trueLabel, ut.second(), env,
					new HashMap());
			constraint.add(new Code.Fail("type constraint not satisfied"));
			constraint.add(new Code.Label(trueLabel));
			if(blk == null) {				
				t = new Pair<Type,Block>(t.first(),constraint);
			} else {
				blk.addAll(constraint); // affects t
			}			
		}
		
		// Now, we need to test whether the current type is open and recursive
		// on this name. In such case, we must close it in order to complete the
		// recursive type.
		if(Type.isOpenRecursive(key,t.first())) {
			t = new Pair<Type,Block>(Type.T_RECURSIVE(key.toString(),t.first()),null);			
		} 
				
		// finally, store it in the cache
		cache.put(key, t.first());
		
		// Done
		return t;		
	}	
	
	protected Pair<Type,Block> expandType(UnresolvedType t, HashMap<NameID, Type> cache) {		
		if(t instanceof UnresolvedType.List) {
			UnresolvedType.List lt = (UnresolvedType.List) t;
			Pair<Type,Block> p = expandType(lt.element, cache);
			return new Pair<Type,Block>(Type.T_LIST(p.first()),p.second());
		} else if(t instanceof UnresolvedType.Set) {
			UnresolvedType.Set st = (UnresolvedType.Set) t;
			Pair<Type,Block> p = expandType(st.element, cache);
			return new Pair<Type,Block>(Type.T_SET(p.first()),p.second());
		} else if(t instanceof UnresolvedType.Tuple) {
			UnresolvedType.Tuple tt = (UnresolvedType.Tuple) t;
			HashMap<String,Type> types = new HashMap<String,Type>();
			for(Map.Entry<String,UnresolvedType> e : tt.types.entrySet()) {
				Pair<Type,Block> p = expandType(e.getValue(),cache);
				types.put(e.getKey(),p.first());
			}
			return new Pair<Type,Block>(Type.T_TUPLE(types),null);
		} else if(t instanceof UnresolvedType.Union) {
			UnresolvedType.Union ut = (UnresolvedType.Union) t;
			HashSet<Type.NonUnion> bounds = new HashSet<Type.NonUnion>();
			for(UnresolvedType b : ut.bounds) {
				Pair<Type,Block> p = expandType(b,cache);
				Type bt = p.first();
				if(bt instanceof Type.NonUnion) {
					bounds.add((Type.NonUnion)bt);
				} else {
					bounds.addAll(((Type.Union)bt).bounds);
				}
			}
			if(bounds.size() == 1) {
				return new Pair<Type,Block>(bounds.iterator().next(),null);
			} else {
				return new Pair<Type,Block>(Type.T_UNION(bounds),null);
			}
		} else if(t instanceof UnresolvedType.Process) {	
			UnresolvedType.Process ut = (UnresolvedType.Process) t;
			Pair<Type,Block> p = expandType(ut.element,cache);
			return new Pair<Type, Block>(Type.T_PROCESS(p.first()), p.second());			
		} else if(t instanceof UnresolvedType.Named) {
			UnresolvedType.Named dt = (UnresolvedType.Named) t;						
			Attribute.Module modInfo = dt.attribute(Attribute.Module.class);
			NameID name = new NameID(modInfo.module,dt.name);
			
			try {
				Pair<Type, Block> et = expandType(name, cache);				
				if (Type.isExistential(et.first())) {					
					return new Pair<Type, Block>(Type.T_NAMED(modInfo.module,
							dt.name, et.first()), et.second());
				} else {
					return et;
				}
			} catch (ResolveError rex) {
				syntaxError(rex.getMessage(), t, rex);
				return null;
			}			
		}  else {
			// for base cases
			return resolve(t);
		}
	}
	
	protected void partResolve(ModuleID module, FunDecl fd) {
		
		ArrayList<Type> parameters = new ArrayList<Type>();
		for (WhileyFile.Parameter p : fd.parameters) {						
			parameters.add(resolve(p.type).first());			
		}
		
		// method return type
		Type ret = resolve(fd.ret).first();
		
		// method receiver type (if applicable)
		Type.ProcessName rec = null;
		if (fd.receiver != null) {			
			Type t = resolve(fd.receiver).first();
			checkType(t, Type.Process.class, fd.receiver);
			rec = (Type.ProcessName) t;					
		}
		
		Type.Fun ft = Type.T_FUN(rec, ret, parameters);
		
		List<Type.Fun> types = functions.get(fd.name);
		if(types == null) {
			types = new ArrayList<Type.Fun>();
			functions.put(new NameID(module, fd.name), types);
		}
		
		types.add(ft);		
		fd.attributes().add(new Attribute.Fun(ft));
	}

	protected Module.ConstDef resolve(ConstDecl td) {
		// FIXME: this looks problematic if the constant contains method calls.
		// The problem is that we may not have generated the type for the given
		// method call, which means we won't be able to bind it. One option is
		// simply to disallow function calls in constant definitions.
		resolve(0, td.constant, new HashMap<String, Type>(),
				new HashMap());
		
		// FIXME: broken
		
		return new Module.ConstDef(td.name(),Value.V_INT(BigInteger.ONE));
	}
	
	protected Module.TypeDef resolve(TypeDecl td, ModuleID module) {
		Pair<Type, Block> p = types.get(new NameID(module, td.name()));
		Type t = p.first();
		td.attributes().add(new Attribute.Type(t));
		if (td.constraint != null) {
			// FIXME: at this point, would be good to add types for other
			// exposed variables.
			HashMap<String, Type> environment = new HashMap<String, Type>();
			HashMap<String, Pair<Type, Block>> declared = new HashMap();
			environment.put("$", t);
			CExpr tmp = resolve(0, td.constraint, environment, declared)
					.first();
			checkType(tmp.type(), Type.Bool.class, td.constraint);
		}
		return new Module.TypeDef(td.name(), t, p.second());
	}	
		
	protected Module.Method resolve(FunDecl fd) {
		
		// The declared environment holds the declared type of a given local
		// variable. This is separate from the environment (see below), whose
		// types may change as a result of assignment and type inference.
		HashMap<String,Pair<Type,Block>> declared = new HashMap();
		
		// The environment holds the current type of a given local variable. The
		// current type is affected by assignments to that variable.
		HashMap<String,Type> environment = new HashMap<String,Type>();		
				
		ArrayList<String> parameterNames = new ArrayList<String>();
		Block precondition = null;
		Block postcondition = null;
		
		// method parameter types
		for (WhileyFile.Parameter p : fd.parameters) {						
			Pair<Type,Block> t = resolve(p.type);
			environment.put(p.name(),t.first());
			declared.put(p.name(),t);
			parameterNames.add(p.name());
			// FIXME: need to update the pre-condition here
		}
				
		// method return type
		Pair<Type,Block> ret = resolve(fd.ret);		
		
		// method receiver type (if applicable)			
		if(fd.receiver != null) {			
			Pair<Type,Block> rec = resolve(fd.receiver);
			environment.put("this", rec.first());
			declared.put("this", rec);
		}
				
		if (fd.precondition != null) {			
			String trueLabel = Block.freshLabel();
			Block tmp = resolveCondition(trueLabel, fd.precondition, environment,
					declared);
			tmp.add(new Code.Fail("function precondition not satisfied"));
			tmp.add(new Code.Label(trueLabel));			
			if (precondition == null) {
				precondition = tmp;
			} else {
				precondition.addAll(tmp);
			}
		}
		
		if (fd.postcondition != null) {
			environment.put("$", ret.first());
			String trueLabel = Block.freshLabel();
			Block tmp = resolveCondition(trueLabel, fd.postcondition, environment,
					declared);
			tmp.add(new Code.Fail("function postcondition not satisfied"));
			tmp.add(new Code.Label(trueLabel));
			environment.remove("$");
			if (postcondition == null) {
				postcondition = tmp;
			} else {
				postcondition.addAll(tmp);
			}
		}
		
		Block blk = new Block();
		for (Stmt s : fd.statements) {						
			blk.addAll(resolve(s, fd, environment, declared));
		}				
		
		Type.Fun tf = fd.attribute(Attribute.Fun.class).type;
		
		if(tf.ret == Type.T_VOID) {
			// need to terminate method
			blk.add(new Code.Return(tf.ret,null));
		}
		
		Module.Case ncase = new Module.Case(parameterNames, precondition,
				postcondition, blk);
		return new Module.Method(fd.name(), tf, ncase);
	}
	
	public Block resolve(Stmt s, FunDecl fd, HashMap<String, Type> environment,
			HashMap<String,Pair<Type,Block>> declared) {			
		try {
			if(s instanceof VarDecl) {
				return resolve((VarDecl)s, environment, declared);
			} else if(s instanceof Assign) {
				return resolve((Assign)s, environment, declared);
			} else if(s instanceof Assert) {
				return resolve((Assert)s, environment, declared);
			} else if(s instanceof Return) {
				return resolve((Return)s, fd, environment, declared);
			} else if(s instanceof Debug) {
				return resolve((Debug)s, environment, declared);
			} else if(s instanceof IfElse) {
				return resolve((IfElse)s, fd, environment, declared);
			} else if(s instanceof Invoke) {
				return resolve(0, (Invoke)s, environment, declared).second();
			} else if(s instanceof Spawn) {
				return resolve(0, (UnOp)s, environment, declared).second();
			} else if(s instanceof ExternJvm) {	
				return resolve((ExternJvm)s, fd, environment, declared);
			} else if(s instanceof Skip) {	
				return resolve((Skip)s, fd, environment, declared);				
			} else {
				syntaxError("unknown statement encountered: "
						+ s.getClass().getName(), s);				
			}
		} catch(ResolveError rex) {
			syntaxError(rex.getMessage(),s,rex);
		} catch(SyntaxError sex) {
			throw sex;
		} catch(Exception ex) {
			syntaxError("internal failure", s, ex);			
		}
		return null;
	}
	
	protected Block resolve(VarDecl s, HashMap<String, Type> environment,
			HashMap<String,Pair<Type,Block>> declared) throws ResolveError {
		Expr init = s.initialiser;
		Pair<Type,Block> tb = resolve(s.type);
		Type type = tb.first();
		Block constraint = tb.second();
		if(constraint != null) {
			constraint = Block.substitute("$", CExpr.VAR(type, s.name), tb.second());
		}
		Block blk = new Block();
		if(init != null) {
			Pair<CExpr,Block> initT = resolve(0, init,environment, declared);
			checkIsSubtype(type,initT.first().type(),init);
			environment.put(s.name,type);
			blk.addAll(initT.second());
			blk.add(new Code.Assign(CExpr.VAR(type, s.name), initT.first()));			
			// Finally, need to actually check the constraints!						
			if(constraint != null) {
				blk.addAll(constraint);
			}
		} 

		declared.put(s.name,new Pair<Type,Block>(type,constraint));
				
		return blk;
	}
	
	protected Block resolve(Assign s, HashMap<String, Type> environment,
			HashMap<String,Pair<Type,Block>> declared) {
		
		Block blk = new Block();		
		if (s.lhs instanceof Variable) {
			// perform type inference as a result of this assignment
			Variable v = (Variable) s.lhs;
			Type declared_t = declared.get(v.var).first();
			if (declared_t == null) {
				syntaxError("unknown variable", v);
			}
			Pair<CExpr, Block> rhs_tb = resolve(0, s.rhs, environment, declared);
			Type rhs_t = rhs_tb.first().type();
			checkIsSubtype(declared_t, rhs_t, s.rhs);
			environment.put(v.var, rhs_t);
			blk.addAll(rhs_tb.second());
			blk.add(new Code.Assign(CExpr.VAR(rhs_t, v.var), rhs_tb.first()));
		} else if(s.lhs instanceof ListAccess) {
			ListAccess la = (ListAccess) s.lhs;
			Pair<CExpr,Block> src_tb = resolve(0, la.src, environment, declared);			
			Pair<CExpr,Block> index_tb = resolve(1, la.index, environment, declared);
			Pair<CExpr,Block> rhs_tb = resolve(2, s.rhs, environment, declared);			
			Type.List la_t = checkType(src_tb.first().type(),Type.List.class,la.src);
			checkIsSubtype(la_t.element,rhs_tb.first().type(),s.rhs);
			checkIsSubtype(Type.T_INT,index_tb.first().type(),la.index);
			blk.addAll(src_tb.second());
			blk.addAll(index_tb.second());
			blk.addAll(rhs_tb.second());
			blk.add(new Code.Assign(CExpr.LISTACCESS(src_tb.first(), index_tb
					.first()), rhs_tb.first()));		
		} else {
			Pair<CExpr,Block> rhs_tb = resolve(2, s.rhs, environment, declared);
			Pair<CExpr,Block> lhs_tb = resolve(0, s.lhs, environment, declared);
			checkIsSubtype(lhs_tb.first().type(), rhs_tb.first().type(), s.rhs);							
			System.out.println("WARNING: Assign is missing cases");
		}
		
		// Finally, we need to add any constraints that may be coming from the
		// declared type.
		Variable target = (Variable) flattern(s.lhs); // FIXME
		Type declared_t = declared.get(target.var).first();
		Block constraint = declared.get(target.var).second();
		if(constraint != null) {
			constraint = Block.substitute("$", CExpr.VAR(declared_t, target.var), constraint);
			blk.addAll(constraint);
		}
		
		return blk;
	}

	protected Block resolve(Assert s, HashMap<String, Type> environment,
			HashMap<String, Pair<Type, Block>> declared) {
		Pair<CExpr, Block> t = resolve(0, s.expr, environment, declared);
		checkIsSubtype(t.first().type(), Type.T_BOOL, s.expr);
		return null;
	}

	protected Block resolve(Return s, FunDecl fd,
			HashMap<String, Type> environment,
			HashMap<String, Pair<Type, Block>> declared) {
		if (s.expr != null) {			
			Pair<CExpr, Block> t = resolve(0, s.expr, environment, declared);
			Type.Fun ft = fd.attribute(Attribute.Fun.class).type;
			checkIsSubtype(ft.ret, t.first().type(), s.expr);
			Block blk = new Block();
			blk.addAll(t.second());
			blk.add(new Code.Return(ft.ret, t.first()));
			return blk;
		} else {
			Block blk = new Block();
			blk.add(new Code.Return(Type.T_VOID, null));
			return blk;
		}
	}
	
	protected Block resolve(ExternJvm s, FunDecl fd,
			HashMap<String, Type> environment,
			HashMap<String, Pair<Type, Block>> declared) {
		Block blk = new Block();
		blk.add(new Code.ExternJvm(s.bytecodes));
		return blk;
	}
	protected Block resolve(Skip s, FunDecl fd,
			HashMap<String, Type> environment,
			HashMap<String, Pair<Type, Block>> declared) {
		Block blk = new Block();
		blk.add(new Code.Skip());
		return blk;
	}
	
	protected Block resolve(Debug s, HashMap<String, Type> environment,
			HashMap<String, Pair<Type, Block>> declared) {
		Pair<CExpr, Block> t = resolve(0, s.expr, environment, declared);
		checkIsSubtype(t.first().type(), Type.T_LIST(Type.T_INT), s.expr);
		Block blk = t.second();
		blk.add(new Code.Debug(t.first()));
		return blk;
	}

	protected Block resolve(IfElse s, FunDecl fd,
			HashMap<String, Type> environment,
			HashMap<String, Pair<Type, Block>> declared) {
		String falseLab = Block.freshLabel();
		String exitLab = s.falseBranch.isEmpty() ? falseLab : Block
				.freshLabel();
		Block blk = resolveCondition(falseLab, invert(s.condition),
				environment, declared);

		// FIXME: need to perform some type inference here

		HashMap<String, Type> tenv = new HashMap<String, Type>(environment);
		HashMap<String, Pair<Type, Block>> tdec = new HashMap<String, Pair<Type, Block>>(
				declared);
		for (Stmt st : s.trueBranch) {
			blk.addAll(resolve(st, fd, tenv, tdec));
		}
		if (!s.falseBranch.isEmpty()) {
			blk.add(new Code.Goto(exitLab));
			blk.add(new Code.Label(falseLab));
			HashMap<String, Type> fenv = new HashMap<String, Type>(environment);
			HashMap<String, Pair<Type, Block>> fdec = new HashMap<String, Pair<Type, Block>>(
					declared);
			for (Stmt st : s.falseBranch) {
				blk.addAll(resolve(st, fd, fenv, fdec));
			}
		}

		blk.add(new Code.Label(exitLab));

		return blk;
	}

	/**
	 * Target gives the name of the register to use to store the result of this
	 * expression in.
	 * 
	 * @param target
	 * @param e
	 * @param environment
	 * @param declared
	 * @return
	 */
	protected Block resolveCondition(String target, Expr e,
			HashMap<String, Type> environment, HashMap<String,Pair<Type,Block>> declared) {
		try {
			if (e instanceof Constant) {
				return resolveCondition(target,(Constant)e, environment, declared);
			} else if (e instanceof Variable) {
				return resolveCondition(target,(Variable)e, environment, declared);
			} else if (e instanceof BinOp) {
				return resolveCondition(target,(BinOp)e, environment, declared);
			} else if (e instanceof UnOp) {
				return resolveCondition(target,(UnOp)e, environment, declared);
			} else if (e instanceof Invoke) {
				return resolveCondition(target,(Invoke)e, environment, declared);
			} else if (e instanceof TupleAccess) {
				return resolveCondition(target,(TupleAccess) e, environment, declared);
			} else if (e instanceof TupleGen) {
				return resolveCondition(target,(TupleGen) e, environment, declared);
			} else if (e instanceof ListAccess) {
				return resolveCondition(target,(ListAccess) e, environment, declared);
			} else {				
				syntaxError("expected boolean expression, got: "
							+ e.getClass().getName(), e);			
			}
		} catch(SyntaxError se) {
			throw se;
		} catch(Exception ex) {
			syntaxError("internal failure", e, ex);			
		}	
		
		return null;
	}
	
	protected Block resolveCondition(String target, Constant c,
			HashMap<String, Type> environment, HashMap<String,Pair<Type,Block>> declared) {
		checkType(c.value.type(),Type.Bool.class,c);
		Value.Bool b = (Value.Bool) c.value;
		Block blk = new Block();
		if(b.value) {
			blk.add(new Code.Goto(target));
		} else {
			// do nout
		}
		return blk;
	}
		
	protected Block resolveCondition(String target, Variable v,
			HashMap<String, Type> environment, HashMap<String,Pair<Type,Block>> declared) {
		Type t = environment.get(v.var);
		if (t == null) {
			syntaxError("unknown variable", v);
		}
		checkType(t, Type.Bool.class, v);
		Block blk = new Block();
		blk.add(new Code.IfGoto(t, Code.COP.EQ, CExpr.VAR(t, v.var), Value
				.V_BOOL(true), target));
		return blk;
	}
	
	protected Block resolveCondition(String target, BinOp v,
			HashMap<String, Type> environment, HashMap<String,Pair<Type,Block>> declared) {		
		BOp bop = v.op;		
		Block blk = new Block();
		
		if (bop == BOp.OR) {
			blk.addAll(resolveCondition(target, v.lhs, environment, declared));
			blk.addAll(resolveCondition(target, v.rhs, environment, declared));
			return blk;
		} else if (bop == BOp.AND) {
			String exitLabel = Block.freshLabel();
			blk.addAll(resolveCondition(exitLabel, invert(v.lhs), environment,
					declared));
			blk.addAll(resolveCondition(target, v.rhs, environment, declared));
			blk.add(new Code.Label(exitLabel));
			return blk;
		}
		
		Pair<CExpr,Block> lhs_tb = resolve(0,v.lhs, environment, declared);
		Pair<CExpr,Block> rhs_tb = resolve(1,v.rhs, environment, declared);
		blk.addAll(lhs_tb.second());
		blk.addAll(rhs_tb.second());
		Type lhs_t = lhs_tb.first().type();
		Type rhs_t = rhs_tb.first().type();
		Type lub = Type.leastUpperBound(lhs_t,rhs_t);

		if (bop == BOp.LT || bop == BOp.LTEQ || bop == BOp.GT
				|| bop == BOp.GTEQ) {
			checkIsSubtype(Type.T_REAL, lhs_t, v);
			checkIsSubtype(Type.T_REAL, rhs_t, v);
			blk.add(new Code.IfGoto(lub, OP2COP(bop, v), lhs_tb.first(), rhs_tb
					.first(), target));
			return blk;
		} else if (bop == BOp.SUBSET || bop == BOp.SUBSETEQ) {
			checkIsSubtype(Type.T_SET(Type.T_ANY), lhs_t, v);
			checkIsSubtype(Type.T_SET(Type.T_ANY), rhs_t, v);
			blk.add(new Code.IfGoto(lub, OP2COP(bop, v), lhs_tb.first(), rhs_tb
					.first(), target));
			return blk;
		} else if (bop == BOp.EQ || bop == BOp.NEQ) {
			if (!Type.isSubtype(lhs_t, rhs_t) && !Type.isSubtype(rhs_t, lhs_t)) {
				syntaxError("Cannot compare types", v);
			}
			blk.add(new Code.IfGoto(lub, OP2COP(bop, v), lhs_tb.first(), rhs_tb
					.first(), target));
			return blk;
		} else if (bop == BOp.ELEMENTOF) {
			checkIsSubtype(Type.T_SET(Type.T_ANY), rhs_t, v);
			Type element;
			if (rhs_t instanceof Type.Set) {
				Type.Set st = (Type.Set) rhs_t;
				element = st.element;
			} else {
				Type.List st = (Type.List) rhs_t;
				element = st.element;
			}
			if (!Type.isSubtype(lhs_t, element)
					&& !Type.isSubtype(element, lhs_t)) {
				syntaxError("Cannot compare types", v);
			}
			lub = Type.leastUpperBound(lhs_t, element);
			blk.add(new Code.IfGoto(lub, OP2COP(bop, v), lhs_tb.first(), rhs_tb
					.first(), target));
			return blk;
		}

		syntaxError("expected boolean expression",v);
		return null;
	}
	
	protected Block resolveCondition(String target, UnOp v,
			HashMap<String, Type> environment, HashMap<String,Pair<Type,Block>> declared) {		
		UOp uop = v.op;		
		switch(uop) {
		case NOT:						
			String label = Block.freshLabel();
			Block blk = resolveCondition(label,v.mhs,environment,declared);
			blk.add(new Code.Goto(target));
			blk.add(new Code.Label(label));
			return blk;
		}
		syntaxError("expected boolean expression",v);
		return null;
	}	
	
	protected Block resolveCondition(String target, ListAccess v,
			HashMap<String, Type> environment,
			HashMap<String, Pair<Type, Block>> declared) {
		Pair<CExpr, Block> la = resolve(0, v, environment, declared);
		CExpr lhs = la.first();
		checkType(lhs.type(), Type.Bool.class, v);
		Block blk = la.second();
		blk.add(new Code.IfGoto(lhs.type(), Code.COP.EQ, lhs, Value
				.V_BOOL(true), target));
		return blk;
	}
	
	/**
	 * Target gives the name of the register to use to store the result of this
	 * expression in.
	 * 
	 * @param target
	 * @param e
	 * @param environment
	 * @param declared
	 * @return
	 */
	protected Pair<CExpr, Block> resolve(int target, Expr e,			
			HashMap<String, Type> environment, HashMap<String,Pair<Type,Block>> declared) {
		try {
			if (e instanceof Constant) {
				return resolve(target,(Constant)e, environment, declared);
			} else if (e instanceof Variable) {
				return resolve(target,(Variable)e, environment, declared);
			} else if (e instanceof NaryOp) {
				return resolve(target,(NaryOp)e, environment, declared);
			} else if (e instanceof BinOp) {
				return resolve(target,(BinOp)e, environment, declared);
			} else if (e instanceof ListAccess) {
				return resolve(target,(ListAccess)e, environment, declared);
			} else if (e instanceof UnOp) {
				return resolve(target,(UnOp)e, environment, declared);
			} else if (e instanceof Invoke) {
				return resolve(target,(Invoke)e, environment, declared);
			} else if (e instanceof Comprehension) {
				return resolve(target,(Comprehension) e, environment, declared);
			} else if (e instanceof TupleAccess) {
				return resolve(target,(TupleAccess) e, environment, declared);
			} else if (e instanceof TupleGen) {
				return resolve(target,(TupleGen) e, environment, declared);
			} else {				
				syntaxError("unknown expression encountered: "
							+ e.getClass().getName(), e);			
			}
		} catch(SyntaxError se) {
			throw se;
		} catch(Exception ex) {
			syntaxError("internal failure", e, ex);			
		}	
		
		return null;
	}
	
	protected Pair<CExpr, Block> resolve(int target, Invoke s,
			HashMap<String, Type> environment, HashMap<String,Pair<Type,Block>> declared)
			throws ResolveError {
		List<Expr> args = s.arguments;
		Block blk = new Block();
				
		int idx=target;
		ArrayList<CExpr> nargs = new ArrayList<CExpr>();
		
		Type.Process receiver = null;
		if(s.receiver != null) {			
			Pair<CExpr,Block> tb = resolve(idx++,s.receiver,environment, declared);
			Type type = tb.first().type();
			nargs.add(tb.first());
			checkType(type,Type.Process.class,s.receiver);
			receiver = (Type.Process) type;
			blk.addAll(tb.second());			
		}
		
		ArrayList<Type> ptypes = new ArrayList<Type>();
		for(Expr e : args) {			
			Pair<CExpr,Block> e_tb = resolve(idx++,e,environment, declared);
			nargs.add(e_tb.first());
			ptypes.add(e_tb.first().type());
			blk.addAll(e_tb.second());
		}
		
		// First, determine the (static) type of the method/function being
		// invoked
		Attribute.Module modInfo = s.attribute(Attribute.Module.class);		
		Type.Fun funtype = bindFunction(modInfo.module, s.name, receiver, ptypes,s);
		
		if(funtype == null) {
			syntaxError("invalid or ambiguous method call",s);
		}			
		s.attributes().add(new Attribute.Fun(funtype));
		NameID name = new NameID(modInfo.module,s.name);	
		CExpr.LVal lhs = null;
		if(funtype.ret != Type.T_VOID) { lhs = CExpr.REG(funtype.ret, target);}
		
		// Now, if this method/function has one or more "cases" then we need to
		// select the right one, based on the pre / post conditions. 
		
		blk.add(new Code.Invoke(funtype, name, 0, lhs, nargs));
		
		return new Pair<CExpr,Block>(lhs,blk);									
	}
			
	protected Pair<CExpr, Block> resolve(int target, Constant c,
			HashMap<String, Type> environment, HashMap<String,Pair<Type,Block>> declared) {
		return new Pair<CExpr,Block>(c.value,new Block());
	}
	
	protected Pair<CExpr, Block> resolve(int target, Variable v,
			HashMap<String, Type> environment, HashMap<String,Pair<Type,Block>> declared) {
		Type t = environment.get(v.var);
		if(t == null) {		
			syntaxError("unknown variable",v);			
		}		
		return new Pair<CExpr, Block>(CExpr.VAR(t, v.var), new Block());
	}		

	protected Pair<CExpr, Block> resolve(int target, UnOp v,
			HashMap<String, Type> environment, HashMap<String,Pair<Type,Block>> declared) {
		Pair<CExpr,Block> mhs = resolve(target,v.mhs,environment,declared);
		Block blk = mhs.second();
		Type mhs_t = mhs.first().type();
		switch(v.op) {
		case NEG:
			checkIsSubtype(Type.T_REAL,mhs_t,v.mhs);			
			return new Pair<CExpr, Block>(CExpr.UNOP(CExpr.UOP.NEG,
					mhs.first()), blk);
		case NOT:
			checkIsSubtype(Type.T_BOOL,mhs_t,v.mhs);
			return new Pair<CExpr, Block>(CExpr.UNOP(CExpr.UOP.NOT,
					mhs.first()), blk);
		case LENGTHOF:
			checkIsSubtype(Type.T_SET(Type.T_ANY), mhs_t, v.mhs);
			return new Pair<CExpr, Block>(CExpr.UNOP(CExpr.UOP.LENGTHOF, mhs
					.first()), blk);
		default:
			syntaxError("unexpected unary operator encountered",v);
			return null;
		}		
	}
	
	protected Pair<CExpr, Block> resolve(int target, ListAccess v,
			HashMap<String, Type> environment,
			HashMap<String, Pair<Type, Block>> declared) {
		Pair<CExpr, Block> lhs_tb = resolve(target, v.src, environment,
				declared);
		Pair<CExpr, Block> rhs_tb = resolve(target + 1, v.index, environment,
				declared);
		Type lhs_t = lhs_tb.first().type();
		Type rhs_t = rhs_tb.first().type();
		checkType(lhs_t, Type.List.class, v);
		checkIsSubtype(Type.T_INT, rhs_t, v);
		Block blk = new Block();
		blk.addAll(lhs_tb.second());
		blk.addAll(rhs_tb.second());
		return new Pair<CExpr, Block>(CExpr.LISTACCESS(lhs_tb.first(), rhs_tb
				.first()), blk);
	}
	
	protected Pair<CExpr, Block> resolve(int target, BinOp v,
			HashMap<String, Type> environment,
			HashMap<String, Pair<Type, Block>> declared) {
	
		// could probably use a range test for this somehow
		if (v.op == BOp.EQ || v.op == BOp.NEQ || v.op == BOp.LT
				|| v.op == BOp.LTEQ || v.op == BOp.GT || v.op == BOp.GTEQ
				|| v.op == BOp.SUBSET || v.op == BOp.SUBSETEQ
				|| v.op == BOp.ELEMENTOF || v.op == BOp.AND || v.op == BOp.OR) {
			String trueLabel = Block.freshLabel();
			String exitLabel = Block.freshLabel();
			Block blk = resolveCondition(trueLabel, v, environment, declared);
			blk.add(new Code.Assign(CExpr.REG(Type.T_BOOL, target), Value
					.V_BOOL(false)));
			blk.add(new Code.Goto(exitLabel));
			blk.add(new Code.Label(trueLabel));
			blk.add(new Code.Assign(CExpr.REG(Type.T_BOOL, target), Value
					.V_BOOL(true)));
			blk.add(new Code.Label(exitLabel));
			return new Pair<CExpr, Block>(CExpr.REG(Type.T_BOOL, target), blk);
		}
		
		Pair<CExpr,Block> lhs_tb = resolve(target,v.lhs, environment, declared);
		Pair<CExpr,Block> rhs_tb = resolve(target+1,v.rhs, environment, declared);		
		Type lhs_t = lhs_tb.first().type();
		Type rhs_t = rhs_tb.first().type();		
		BOp bop = v.op;
		
		Block blk = new Block();
		blk.addAll(lhs_tb.second());
		blk.addAll(rhs_tb.second());
		Type lub = Type.leastUpperBound(lhs_t, rhs_t);
		CExpr lhs = convert(lub,lhs_tb.first());
		CExpr rhs = convert(lub,rhs_tb.first());
		
		if (bop == BOp.SUB && Type.isSubtype(Type.T_SET(Type.T_ANY), lhs_t)) {
			checkIsSubtype(Type.T_SET(Type.T_ANY), rhs_t, v);
			CExpr r = CExpr.BINOP(CExpr.BOP.DIFFERENCE, lhs, rhs);
			return new Pair<CExpr, Block>(r,blk);
		} else if (bop == BOp.ADD || bop == BOp.SUB || bop == BOp.MUL
				|| bop == BOp.DIV) {
			checkIsSubtype(Type.T_REAL, lhs_t, v);
			checkIsSubtype(Type.T_REAL, rhs_t, v);
			CExpr r = CExpr.BINOP(OP2BOP(bop, v), lhs, rhs);
			return new Pair<CExpr, Block>(r,blk);			
		} else if (bop == BOp.UNION || bop == BOp.INTERSECTION) {
			checkIsSubtype(Type.T_SET(Type.T_ANY), lhs_t, v);
			checkIsSubtype(Type.T_SET(Type.T_ANY), rhs_t, v);
			CExpr r = CExpr.BINOP(OP2BOP(bop, v), lhs, rhs);
			return new Pair<CExpr, Block>(r,blk);						
		} 
			
		throw new RuntimeException("NEED TO ADD MORE CASES TO TYPE RESOLUTION BINOP");
	}
	
	protected Pair<CExpr, Block> resolve(int target, NaryOp v,
			HashMap<String, Type> environment, HashMap<String,Pair<Type,Block>> declared) {		
		Block blk = new Block();
		if(v.nop == NOp.SUBLIST) {
			if(v.arguments.size() != 3) {
				syntaxError("incorrect number of arguments",v);
			}
			Pair<CExpr,Block> src = resolve(target,v.arguments.get(0), environment, declared);
			Pair<CExpr,Block> start = resolve(target+1,v.arguments.get(1), environment, declared);
			Pair<CExpr,Block> end = resolve(target+2,v.arguments.get(2), environment, declared);
			checkType(src.first().type(),Type.List.class,v.arguments.get(0));
			checkType(start.first().type(),Type.Int.class,v.arguments.get(1));
			checkType(end.first().type(),Type.Int.class,v.arguments.get(2));
			blk.addAll(src.second());
			blk.addAll(start.second());
			blk.addAll(end.second());
			CExpr r = CExpr.NARYOP(CExpr.NOP.SUBLIST, src.first(), start
					.first(), end.first());
			return new Pair<CExpr, Block>(r,blk);			
		} else {
			int idx = target;
			ArrayList<CExpr> args = new ArrayList<CExpr>();
			for(Expr e : v.arguments) {				
				Pair<CExpr,Block> t = resolve(idx++,e, environment, declared);
				args.add(t.first());				
				blk.addAll(t.second());				
			}		

			if (v.nop == NOp.LISTGEN) {
				return new Pair<CExpr, Block>(CExpr.NARYOP(CExpr.NOP.LISTGEN,
						args), blk);
			} else {
				return new Pair<CExpr, Block>(CExpr.NARYOP(CExpr.NOP.SETGEN,
						args), blk);
			}
		}
	}
	
	protected Pair<CExpr, Block> resolve(int target, Comprehension e,
			HashMap<String, Type> environment, HashMap<String,Pair<Type,Block>> declared) {				
		throw new RuntimeException("Need to implement type resolution for Comprehension");
	}
		
	protected Pair<CExpr, Block> resolve(int target, TupleGen sg,
			HashMap<String, Type> environment, HashMap<String,Pair<Type,Block>> declared) {
		HashMap<String, Type> types = new HashMap<String, Type>();
		for (Map.Entry<String, Expr> e : sg.fields.entrySet()) {
			Pair<CExpr, Block> tb = resolve(target, e.getValue(), environment,
					declared);
			types.put(e.getKey(), tb.first().type());
		}
		throw new RuntimeException("Need to implement type resolution for tuple generation");		
	}
	
	protected Pair<CExpr, Block> resolve(int target, TupleAccess sg,
			HashMap<String, Type> environment,
			HashMap<String, Pair<Type, Block>> declared) {
		Pair<CExpr, Block> lhs = resolve(target, sg.lhs, environment, declared);
		// FIXME: will need to determine effective tuple type here
		Type.Tuple tup = checkType(lhs.first().type(), Type.Tuple.class, sg.lhs);
		Type ft = tup.types.get(sg.name);
		if (ft == null) {
			syntaxError("type has no field named: " + sg.name, sg.lhs);
		}
		throw new RuntimeException(
				"Need to implement type resolution for tuple access");
	}
	
	protected Pair<Type,Block> resolve(UnresolvedType t) {		
		if(t instanceof UnresolvedType.Any) {
			return new Pair<Type,Block>(Type.T_ANY,null);
		} else if(t instanceof UnresolvedType.Existential) {
			return new Pair<Type,Block>(Type.T_EXISTENTIAL,null);
		} else if(t instanceof UnresolvedType.Void) {
			return new Pair<Type,Block>(Type.T_VOID,null);
		} else if(t instanceof UnresolvedType.Bool) {
			return new Pair<Type,Block>(Type.T_BOOL,null);
		} else if(t instanceof UnresolvedType.Int) {			
			return new Pair<Type,Block>(Type.T_INT,null);
		} else if(t instanceof UnresolvedType.Real) {
			return new Pair<Type,Block>(Type.T_REAL,null);
		} else if(t instanceof UnresolvedType.List) {
			UnresolvedType.List lt = (UnresolvedType.List) t;
			Pair<Type,Block> p = resolve(lt.element);
			return new Pair<Type,Block>(Type.T_LIST(p.first()),null);
		} else if(t instanceof UnresolvedType.Set) {
			UnresolvedType.Set st = (UnresolvedType.Set) t;
			Pair<Type,Block> p = resolve(st.element);
			return new Pair<Type,Block>(Type.T_SET(p.first()),null);			
		} else if(t instanceof UnresolvedType.Tuple) {
			UnresolvedType.Tuple tt = (UnresolvedType.Tuple) t;
			HashMap<String,Type> types = new HashMap<String,Type>();
			for(Map.Entry<String,UnresolvedType> e : tt.types.entrySet()) {
				Pair<Type,Block> p = resolve(e.getValue());
				types.put(e.getKey(),p.first());
			}
			return new Pair<Type,Block>(Type.T_TUPLE(types),null);
		} else if(t instanceof UnresolvedType.Named) {
			UnresolvedType.Named dt = (UnresolvedType.Named) t;						
			ModuleID mid = dt.attribute(Attribute.Module.class).module;
			if(modules.contains(mid)) {
				return types.get(new NameID(mid,dt.name));
			} else {
				try {
					Module mi = loader.loadModule(mid);
					Module.TypeDef td = mi.type(dt.name);			
					return new Pair<Type,Block>(td.type(),null);
				} catch(ResolveError rex) {
					syntaxError(rex.getMessage(),t,rex);
					return null;
				}
			}
		} else if(t instanceof UnresolvedType.Union) {
			UnresolvedType.Union ut = (UnresolvedType.Union) t;
			HashSet<Type.NonUnion> bounds = new HashSet<Type.NonUnion>();
			for(UnresolvedType b : ut.bounds) {
				Pair<Type,Block> p = resolve(b);
				Type bt = p.first();
				if(bt instanceof Type.NonUnion) {
					bounds.add((Type.NonUnion)bt);
				} else {
					bounds.addAll(((Type.Union)bt).bounds);
				}
			}
			if(bounds.size() == 1) {
				return new Pair<Type,Block>(bounds.iterator().next(),null);
			} else {
				return new Pair<Type,Block>(Type.T_UNION(bounds),null);
			}
		} else {	
			UnresolvedType.Process ut = (UnresolvedType.Process) t;
			Pair<Type,Block> p = resolve(ut.element);
			return new Pair<Type,Block>(Type.T_PROCESS(p.first()),null);			
		} 
	}
	
	protected CExpr convert(Type toType, CExpr expr) {
		Type fromType = expr.type();		
		if (toType.equals(fromType)) {			
			return expr;
		} else {			
			return CExpr.CONVERT(toType, expr);
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
			syntaxError("expected type " + clazz.getName() + ", found " + t,
					elem);
			return null;
		}
	}
	
	// Check t1 :> t2
	protected void checkIsSubtype(Type t1, Type t2, SyntacticElement elem) {
		if (!Type.isSubtype(t1, t2)) {
			syntaxError("expected type " + t1 + ", found " + t2, elem);
		}
	}

	/**
	 * The purpose of this method is to making the naming of recursive types a
	 * little more human-readable.
	 * 
	 * @param t
	 * @return
	 */
	public static Type simplifyRecursiveTypes(Type t) {
		Set<String> _names = Type.recursiveTypeNames(t);
		ArrayList<String> names = new ArrayList<String>(_names);
		HashMap<String,String> binding = new HashMap<String,String>();
		
		for(int i=0;i!=names.size();++i) {
			int let = (i+20) % 26;
			int num = i / 26;
			String n = "" + (char) ('A' + let);
			if(num > 0) {
				n += num;
			}
			binding.put(names.get(i), n);
		}
		
		return Type.substituteRecursiveTypes(t, binding);
	}	
	
	public static Variable flattern(Expr e) {
		if(e instanceof Variable) {
			return (Variable) e;
		} else if(e instanceof ListAccess) {
			ListAccess la = (ListAccess) e;
			return flattern(la.src);
		} else if(e instanceof TupleAccess) {
			TupleAccess la = (TupleAccess) e;
			return flattern(la.lhs);
		} else {
			syntaxError("invalid lval",e);
			return null;
		}
	}
	
	public static Expr invert(Expr e) {
		if(e instanceof Expr.BinOp) {
			BinOp bop = (BinOp) e;
			switch(bop.op) {
			case AND:
				return new BinOp(BOp.OR,invert(bop.lhs),invert(bop.rhs),e.attributes());
			case OR:				
				return new BinOp(BOp.AND,invert(bop.lhs),invert(bop.rhs),e.attributes());
			case EQ:
				return new BinOp(BOp.NEQ,bop.lhs,bop.rhs,e.attributes());
			case NEQ:
				return new BinOp(BOp.EQ,bop.lhs,bop.rhs,e.attributes());
			case LT:
				return new BinOp(BOp.GTEQ,bop.lhs,bop.rhs,e.attributes());
			case LTEQ:
				return new BinOp(BOp.GT,bop.lhs,bop.rhs,e.attributes());
			case GT:
				return new BinOp(BOp.LTEQ,bop.lhs,bop.rhs,e.attributes());
			case GTEQ:
				return new BinOp(BOp.LT,bop.lhs,bop.rhs,e.attributes());
			// FIXME: add more cases
			}
		} else if(e instanceof Expr.UnOp) {
			UnOp uop = (UnOp) e;
			switch(uop.op) {
			case NOT:
				return uop.mhs;
			}
		} 
		return new Expr.UnOp(Expr.UOp.NOT,e);				
	}
	
	public static CExpr.BOP OP2BOP(Expr.BOp bop, SyntacticElement elem) {
		switch(bop) {
		case ADD:
			return CExpr.BOP.ADD;
		case SUB:
			return CExpr.BOP.SUB;
		case DIV:
			return CExpr.BOP.DIV;
		case MUL:
			return CExpr.BOP.MUL;
		case UNION:
			return CExpr.BOP.UNION;			
		case INTERSECTION:
			return CExpr.BOP.INTERSECT;	
		}
		syntaxError("unrecognised binary operation",elem);
		return null;
	}
	
	public static Code.COP OP2COP(Expr.BOp bop, SyntacticElement elem) {
		switch (bop) {
		case EQ:
			return Code.COP.EQ;
		case NEQ:
			return Code.COP.NEQ;
		case LT:
			return Code.COP.LT;
		case LTEQ:
			return Code.COP.LTEQ;
		case GT:
			return Code.COP.GT;
		case GTEQ:
			return Code.COP.GTEQ;
		case SUBSET:
			return Code.COP.SUBSET;
		case SUBSETEQ:
			return Code.COP.SUBSETEQ;
		case ELEMENTOF:
			return Code.COP.ELEMOF;
		}
		syntaxError("unrecognised binary operation", elem);
		return null;
	}
}
