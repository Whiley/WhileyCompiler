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
import wyjc.lang.*;
import wyjc.lang.WhileyFile.*;
import wyjc.lang.Stmt;
import wyjc.lang.Stmt.*;
import wyjc.lang.Expr.*;

public class ModuleBuilder {
	private final ModuleLoader loader;	
	private HashSet<ModuleID> modules;
	private HashMap<NameID,WhileyFile> filemap;
	private HashMap<NameID,List<Type.Fun>> functions;	
	private HashMap<NameID,Pair<Type,Block>> types;	
	private HashMap<NameID,Value> constants;
	private HashMap<NameID,Pair<UnresolvedType,Expr>> unresolved;
	private String filename;
	
	public ModuleBuilder(ModuleLoader loader) {
		this.loader = loader;
	}
	
	public List<Module> resolve(List<WhileyFile> files) {			
		modules = new HashSet<ModuleID>();
		filemap = new HashMap<NameID,WhileyFile>();
		functions = new HashMap<NameID,List<Type.Fun>>();
		types = new HashMap<NameID,Pair<Type,Block>>();
		constants = new HashMap<NameID,Value>();
		unresolved = new HashMap<NameID,Pair<UnresolvedType,Expr>>();
		
		// now, init data
		for (WhileyFile f : files) {			
			modules.add(f.module);
		}
						
		// Stage 1 ... resolve and check types of all named types + constants
		generateConstants(files);
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
		this.filename = wf.filename;
		HashMap<Pair<Type.Fun, String>, Module.Method> methods = new HashMap();
		ArrayList<Module.TypeDef> types = new ArrayList<Module.TypeDef>();
		ArrayList<Module.ConstDef> constants = new ArrayList<Module.ConstDef>();
		for (WhileyFile.Decl d : wf.declarations) {
			try {
				if (d instanceof TypeDecl) {
					types.add(resolve((TypeDecl) d, wf.module));
				} else if (d instanceof ConstDecl) {
					constants.add(resolve((ConstDecl) d, wf.module));
				} else if (d instanceof FunDecl) {
					Module.Method mi = resolve((FunDecl) d);
					Pair<Type.Fun, String> key = new Pair(mi.type(), mi.name());
					Module.Method method = methods.get(key);
					if (method != null) {
						// coalesce cases
						ArrayList<Module.Case> ncases = new ArrayList<Module.Case>(
								method.cases());
						ncases.addAll(mi.cases());
						mi = new Module.Method(mi.name(), mi.type(), ncases);
					}
					methods.put(key, mi);
				}
			} catch (SyntaxError se) {
				throw se;
			} catch (Throwable ex) {
				syntaxError("internal failure", wf.filename, d, ex);
			}
		}
		return new Module(wf.module, wf.filename, methods.values(), types,
				constants);
	}
	
	/**
	 * The following method visits every define constant statement in every whiley
	 * file being compiled, and determines its true and value.  
	 * 
	 * @param files
	 */
	protected void generateConstants(List<WhileyFile> files) {
		HashMap<NameID,Expr> exprs = new HashMap();
		
		// first construct list.
		for(WhileyFile f : files) {
			for(Decl d : f.declarations) {
				if(d instanceof ConstDecl) {
					ConstDecl cd = (ConstDecl) d;
					NameID key = new NameID(f.module,cd.name());
					exprs.put(key, cd.constant);
					filemap.put(key, f);
				}
			}
		}

		for(NameID k : exprs.keySet()) {	
			try {
				Value v = expandConstant(k,exprs, new HashSet<NameID>());
				constants.put(k,v);
				Type t = v.type();
				if(t instanceof Type.Set) {
					Type.Set st = (Type.Set) t;
					Block block = new Block();
					String label = Block.freshLabel();
					CExpr var = CExpr.VAR(st.element,"$");
					Attribute attr = exprs.get(k).attribute(Attribute.Source.class); 
					block.add(new Code.IfGoto(st.element, Code.COP.ELEMOF, var, v, label),attr);
					block.add(new Code.Fail("type constraint not satisfied"),attr);
					block.add(new Code.Label(label));
					types.put(k, new Pair<Type,Block>(st.element, block));
				}
			} catch(ResolveError rex) {
				syntaxError(rex.getMessage(),filemap.get(k).filename,exprs.get(k),rex);
			}
		}
	}

	/**
	 * The expand constant method is responsible for turning a named constant
	 * expression into a value. This is done by traversing the constant's
	 * expression and recursively expanding any named constants it contains.
	 * Simplification of constants is also performed where possible.
	 * 
	 * @param key
	 *            --- name of constant we are expanding.
	 * @param exprs
	 *            --- mapping of all names to their( declared) expressions
	 * @param visited
	 *            --- set of all constants seen during this traversal (used to
	 *            detect cycles).
	 * @return
	 * @throws ResolveError
	 */
	protected Value expandConstant(NameID key, HashMap<NameID, Expr> exprs, HashSet<NameID> visited)
			throws ResolveError {
		Expr e = exprs.get(key);
		Value value = constants.get(key);
		if(value != null) {
			return value;
		} else if(!modules.contains(key.module())) {
			// indicates a non-local key
			Module mi = loader.loadModule(key.module());
			return mi.constant(key.name()).constant();
		} else if(visited.contains(key)) {
			// this indicates a cyclic definition.
			syntaxError("cyclic constant definition encountered", filemap
					.get(key).filename, exprs.get(key));
		} else {
			visited.add(key); // mark this node as visited
		}
			
		// At this point, we need to replace every unresolved variable with a
		// constant definition.
		Value v = expandConstantHelper(e,filemap.get(key).filename,exprs,visited);
		constants.put(key, v);
		return v;
	}

	/**
	 * The following is a helper method for expandConstant. It takes a given
	 * expression (rather than the name of a constant) and expands to a value
	 * (where possible). If the expression contains, for example, method or
	 * function declarations then this will certainly fail (producing a syntax
	 * error).
	 *  
	 * @param key
	 *            --- name of constant we are expanding.
	 * @param exprs
	 *            --- mapping of all names to their( declared) expressions
	 * @param visited
	 *            --- set of all constants seen during this traversal (used to
	 *            detect cycles).
	 */
	protected Value expandConstantHelper(Expr expr, String filename, HashMap<NameID, Expr> exprs,
			HashSet<NameID> visited) throws ResolveError {		
		if(expr instanceof Constant) {
			Constant c = (Constant) expr;
			return c.value;
		} else if(expr instanceof Variable) {
			// Note, this must be a constant definition of some sort
			Variable v = (Variable) expr;
			Attributes.Module mid = expr.attribute(Attributes.Module.class);
			if(mid != null) {
				NameID name = new NameID(mid.module,v.var);
				return expandConstant(name,exprs,visited);
			}
		} else if(expr instanceof BinOp) {
			BinOp bop = (BinOp) expr;
			Value lhs = expandConstantHelper(bop.lhs,filename,exprs,visited);
			Value rhs = expandConstantHelper(bop.rhs,filename,exprs,visited);
			Value v = Value.evaluate(OP2BOP(bop.op,expr),lhs,rhs);
			if(v != null) {
				return v;
			}
		} else if(expr instanceof NaryOp) {
			Expr.NaryOp nop = (NaryOp) expr;
			ArrayList<Value> values = new ArrayList<Value>();
			for(Expr arg : nop.arguments) {
				values.add(expandConstantHelper(arg,filename,exprs,visited));
			}
			if(nop.nop == Expr.NOp.LISTGEN) {
				return Value.V_LIST(values);
			} else if(nop.nop == Expr.NOp.SETGEN) {
				return Value.V_SET(values);
			} 
		} 
		
		syntaxError("invalid expression in constant definition",filename,expr);
		return null;			
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
					filemap.put(key, f);
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
				syntaxError(ex.getMessage(),filemap.get(key).filename,srcs.get(key),ex);
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
		t = expandType(ut.first(), filemap.get(key).filename, cache);		
		
		// Resolve the constraint and generate an appropriate block.			
		Block blk = t.second();
		if(ut.second() != null) {			
			HashMap<String,Type> env = new HashMap<String,Type>();
			env.put("$",t.first());
			String trueLabel = Block.freshLabel();
			Block constraint = resolveCondition(trueLabel, ut.second(), env,
					new HashMap());
			constraint.add(new Code.Fail("type constraint not satisfied"), ut
					.second().attribute(Attribute.Source.class));
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
	
	protected Pair<Type, Block> expandType(UnresolvedType t, String filename,
			HashMap<NameID, Type> cache) {		
		if(t instanceof UnresolvedType.List) {
			UnresolvedType.List lt = (UnresolvedType.List) t;
			Pair<Type,Block> p = expandType(lt.element, filename, cache);
			return new Pair<Type,Block>(Type.T_LIST(p.first()),p.second());
		} else if(t instanceof UnresolvedType.Set) {
			UnresolvedType.Set st = (UnresolvedType.Set) t;
			Pair<Type,Block> p = expandType(st.element, filename, cache);
			return new Pair<Type,Block>(Type.T_SET(p.first()),p.second());
		} else if(t instanceof UnresolvedType.Tuple) {
			UnresolvedType.Tuple tt = (UnresolvedType.Tuple) t;
			HashMap<String,Type> types = new HashMap<String,Type>();
			for(Map.Entry<String,UnresolvedType> e : tt.types.entrySet()) {
				Pair<Type,Block> p = expandType(e.getValue(),filename, cache);
				types.put(e.getKey(),p.first());
			}
			return new Pair<Type,Block>(Type.T_TUPLE(types),null);
		} else if(t instanceof UnresolvedType.Union) {
			UnresolvedType.Union ut = (UnresolvedType.Union) t;
			HashSet<Type.NonUnion> bounds = new HashSet<Type.NonUnion>();
			for(UnresolvedType b : ut.bounds) {
				Pair<Type,Block> p = expandType(b,filename, cache);
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
				return new Pair<Type,Block>(Type.leastUpperBound(bounds),null);
			}
		} else if(t instanceof UnresolvedType.Process) {	
			UnresolvedType.Process ut = (UnresolvedType.Process) t;
			Pair<Type,Block> p = expandType(ut.element,filename, cache);
			return new Pair<Type, Block>(Type.T_PROCESS(p.first()), p.second());			
		} else if(t instanceof UnresolvedType.Named) {
			UnresolvedType.Named dt = (UnresolvedType.Named) t;						
			Attributes.Module modInfo = dt.attribute(Attributes.Module.class);
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
				syntaxError(rex.getMessage(), filename, t, rex);
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
		NameID name = new NameID(module, fd.name);
		List<Type.Fun> types = functions.get(name);
		if(types == null) {
			types = new ArrayList<Type.Fun>();
			functions.put(name, types);
		}		
		types.add(ft);		
		fd.attributes().add(new Attributes.Fun(ft));
	}

	protected Module.ConstDef resolve(ConstDecl td, ModuleID module) {		
		Value v = constants.get(new NameID(module, td.name()));		
		return new Module.ConstDef(td.name(),v);
	}
	
	protected Module.TypeDef resolve(TypeDecl td, ModuleID module) {
		Pair<Type, Block> p = types.get(new NameID(module, td.name()));
		Type t = p.first();
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
				
		// method parameter types
		for (WhileyFile.Parameter p : fd.parameters) {						
			Pair<Type,Block> t = resolve(p.type);
			environment.put(p.name(),t.first());
			declared.put(p.name(),t);
			parameterNames.add(p.name());
			if(t.second() != null) {
				if(precondition == null) {
					precondition = new Block();
				}				
				HashMap<String,CExpr> binding = new HashMap<String,CExpr>();
				binding.put("$",CExpr.VAR(t.first(),p.name));				
				precondition.addAll(Block.relabel(Block.substitute(binding, t
						.second())));				
			}
		}
				
		// method return type
		Pair<Type,Block> ret = resolve(fd.ret);		
		Block postcondition = ret.second();
		
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
			tmp.add(new Code.Fail("function precondition not satisfied"),
					fd.precondition.attribute(Attribute.Source.class));
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
			tmp.add(new Code.Fail("function postcondition not satisfied"),
					fd.postcondition.attribute(Attribute.Source.class));
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
		
		Type.Fun tf = fd.attribute(Attributes.Fun.class).type;
		
		if(tf.ret == Type.T_VOID) {
			// need to terminate method
			blk.add(new Code.Return(null));
		}
		
		Module.Case ncase = new Module.Case(parameterNames, precondition,
				postcondition, blk);
		return new Module.Method(fd.name(), tf, ncase);
	}
	
	public Block resolve(Stmt stmt, FunDecl fd, HashMap<String, Type> environment,
			HashMap<String,Pair<Type,Block>> declared) {			
		try {
			if(stmt instanceof VarDecl) {
				return resolve((VarDecl)stmt, environment, declared);
			} else if(stmt instanceof Assign) {
				return resolve((Assign)stmt, environment, declared);
			} else if(stmt instanceof Assert) {
				return resolve((Assert)stmt, environment, declared);
			} else if(stmt instanceof Return) {
				return resolve((Return)stmt, fd, environment, declared);
			} else if(stmt instanceof Debug) {
				return resolve((Debug)stmt, environment, declared);
			} else if(stmt instanceof IfElse) {
				return resolve((IfElse)stmt, fd, environment, declared);
			} else if(stmt instanceof Invoke) {
				Pair<CExpr,Block> p = resolve(0, (Invoke)stmt, environment, declared);
				Block blk = p.second();
				blk.add(new Code.Assign(null, p.first()), stmt
						.attribute(Attribute.Source.class));
				return blk;
			} else if(stmt instanceof Spawn) {
				return resolve(0, (UnOp)stmt, environment, declared).second();
			} else if(stmt instanceof ExternJvm) {	
				return resolve((ExternJvm)stmt, fd, environment, declared);
			} else if(stmt instanceof Skip) {	
				return resolve((Skip)stmt, fd, environment, declared);				
			} else {
				syntaxError("unknown statement encountered: "
						+ stmt.getClass().getName(), fd.name, stmt);				
			}
		} catch(ResolveError rex) {
			syntaxError(rex.getMessage(),filename,stmt,rex);
		} catch(SyntaxError sex) {
			throw sex;
		} catch(Exception ex) {
			syntaxError("internal failure", filename, stmt, ex);			
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
			constraint = Block.relabel(constraint);
			constraint = Block.substitute("$", CExpr.VAR(type, s.name), constraint);
		}
		Block blk = new Block();
		if(init != null) {
			Pair<CExpr,Block> init_tb = resolve(0, init,environment, declared);
			Type init_t = init_tb.first().type();
			checkIsSubtype(type,init_t,init);
			environment.put(s.name,init_t);
			blk.addAll(init_tb.second());
			blk
					.add(new Code.Assign(CExpr.VAR(init_t, s.name), init_tb
							.first()), s.attribute(Attribute.Source.class));			
			// Finally, need to actually check the constraints!						
			if(constraint != null) {
				blk.addAll(constraint);
			}
		} else {
			environment.put(s.name,type);
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
				syntaxError("unknown variable", filename, v);
			}
			Pair<CExpr, Block> rhs_tb = resolve(0, s.rhs, environment, declared);
			Type rhs_t = rhs_tb.first().type();
			checkIsSubtype(declared_t, rhs_t, s.rhs);
			environment.put(v.var, rhs_t);
			blk.addAll(rhs_tb.second());
			blk.add(new Code.Assign(CExpr.VAR(rhs_t, v.var), rhs_tb.first()), s
					.attribute(Attribute.Source.class));
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
					.first()), rhs_tb.first()), s
					.attribute(Attribute.Source.class));		
		} else if(s.lhs instanceof TupleAccess) {
			TupleAccess la = (TupleAccess) s.lhs;
			Pair<CExpr,Block> src_tb = resolve(0, la.lhs, environment, declared);			
			Pair<CExpr,Block> rhs_tb = resolve(1, s.rhs, environment, declared);	
			// FIXME: problem with effective tuple types I think ?
			Type.Tuple la_t = checkType(src_tb.first().type(),Type.Tuple.class,la.lhs);
			Type field_t = la_t.types.get(la.name);
			if(field_t == null) {
				syntaxError("field does not exist",filename,s.lhs);
			}
			checkIsSubtype(field_t,rhs_tb.first().type(),s.rhs);			
			blk.addAll(src_tb.second());			
			blk.addAll(rhs_tb.second());
			blk.add(new Code.Assign(CExpr.TUPLEACCESS(src_tb.first(), la.name),
					rhs_tb.first()), s
					.attribute(Attribute.Source.class));		
		} else {
			syntaxError("invalid lval encountered",filename,s.lhs);
		}
		
		// Finally, we need to add any constraints that may be coming from the
		// declared type.
		Variable target = (Variable) flattern(s.lhs); // FIXME
		Type declared_t = declared.get(target.var).first();
		Block constraint = declared.get(target.var).second();
		if (constraint != null) {
			constraint = Block.relabel(constraint);
			constraint = Block.substitute("$", CExpr
					.VAR(declared_t, target.var), constraint);
			blk.addAll(constraint);
		}
		
		return blk;
	}

	protected Block resolve(Assert s, HashMap<String, Type> environment,
			HashMap<String, Pair<Type, Block>> declared) {
		String lab = Block.freshLabel();
		Block blk = resolveCondition(lab, s.expr, environment, declared);
		blk.add(new Code.Fail("assertion failed"),s
				.attribute(Attribute.Source.class));
		blk.add(new Code.Label(lab));
		return blk;
	}

	protected Block resolve(Return s, FunDecl fd,
			HashMap<String, Type> environment,
			HashMap<String, Pair<Type, Block>> declared) {
		
		if (s.expr != null) {			
			Pair<CExpr, Block> t = resolve(0, s.expr, environment, declared);
			Type.Fun ft = fd.attribute(Attributes.Fun.class).type;
			checkIsSubtype(ft.ret, t.first().type(), s.expr);
			Block blk = new Block();
			blk.addAll(t.second());

			CExpr r = convert(ft.ret, t.first()); 
			Pair<Type,Block> ret = resolve(fd.ret);		
			Block postcondition = ret.second();
			
			// First, check direct post condition.
			if(fd.postcondition != null) {
				// FIXME: need to handle shadows!				
				String trueLabel = Block.freshLabel();
				environment.put("$",ft.ret);
				Block tmp = resolveCondition(trueLabel, fd.postcondition, environment,
						declared);
				if(postcondition == null) {
					postcondition = tmp;
				} else {
					postcondition.addAll(tmp);
				}				
				environment.remove("$");
				postcondition.add(new Code.Fail("function postcondition not satisfied"),
						fd.postcondition.attribute(Attribute.Source.class));
				postcondition.add(new Code.Label(trueLabel));				
			}
			
			if(postcondition != null) {
				HashMap<String,CExpr> binding = new HashMap<String,CExpr>();
				binding.put("$",r);				
				blk.addAll(Block.relabel(Block.substitute(binding, postcondition)));	
			}
			
			// Second, check 
			
			blk.add(new Code.Return(r),s
					.attribute(Attribute.Source.class));
			return blk;
		} else {
			Block blk = new Block();
			blk.add(new Code.Return(null),s
					.attribute(Attribute.Source.class));
			return blk;
		}
	}
	
	protected Block resolve(ExternJvm s, FunDecl fd,
			HashMap<String, Type> environment,
			HashMap<String, Pair<Type, Block>> declared) {
		Block blk = new Block();
		blk.add(new Code.ExternJvm(s.bytecodes),s
				.attribute(Attribute.Source.class));
		return blk;
	}
	protected Block resolve(Skip s, FunDecl fd,
			HashMap<String, Type> environment,
			HashMap<String, Pair<Type, Block>> declared) {
		Block blk = new Block();
		blk.add(new Code.Skip(),s
				.attribute(Attribute.Source.class));
		return blk;
	}
	
	protected Block resolve(Debug s, HashMap<String, Type> environment,
			HashMap<String, Pair<Type, Block>> declared) {
		Pair<CExpr, Block> t = resolve(0, s.expr, environment, declared);
		checkIsSubtype(Type.T_LIST(Type.T_INT), t.first().type(), s.expr);
		Block blk = t.second();
		blk.add(new Code.Debug(t.first()),s
				.attribute(Attribute.Source.class));
		return blk;
	}

	protected Block resolve(IfElse s, FunDecl fd,
			HashMap<String, Type> environment,
			HashMap<String, Pair<Type, Block>> declared) {
		String falseLab = Block.freshLabel();
		String exitLab = s.falseBranch.isEmpty() ? falseLab : Block
				.freshLabel();
		HashMap<String, Type> tenv = new HashMap<String,Type>(environment);
		Block blk = resolveCondition(falseLab, invert(s.condition),
				tenv, declared);

		// The form of type inference that i'm performing here is quite limited.
		// In principle, I would like to perform more interesting forms of type
		// inference. To really make this happen, I think we need to properly
		// separate the wyil generation from type checking. That is, we first
		// generate wyil code (without type checking it), and then type check it
		// using a realy data-flow based type inference algorithm.
		
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
			} else if (e instanceof Comprehension) {
				return resolveCondition(target,(Comprehension) e, environment, declared);
			} else {				
				syntaxError("expected boolean expression, got: "
							+ e.getClass().getName(), filename, e);			
			}
		} catch(SyntaxError se) {
			throw se;
		} catch(Exception ex) {
			syntaxError("internal failure", filename, e, ex);			
		}	
		
		return null;
	}
	
	protected Block resolveCondition(String target, Constant c,
			HashMap<String, Type> environment,
			HashMap<String, Pair<Type, Block>> declared) {
		checkType(c.value.type(), Type.Bool.class, c);
		Value.Bool b = (Value.Bool) c.value;
		Block blk = new Block();
		if (b.value) {
			blk.add(new Code.Goto(target));
		} else {
			// do nout
		}
		return blk;
	}
		
	protected Block resolveCondition(String target, Variable v,
			HashMap<String, Type> environment,
			HashMap<String, Pair<Type, Block>> declared) throws ResolveError {
		Type t = environment.get(v.var);
		CExpr lhs;
		Block blk = new Block();
		if (t == null) {
			// Definitely not a variable. Could be an alias, or a constant
			// though.
			Attributes.Alias alias = v.attribute(Attributes.Alias.class);
			if(alias != null) {				
				Pair<CExpr,Block> p = resolve(0,alias.alias,environment,declared);
				blk.addAll(p.second());
				lhs = p.first();
			} else {
				Attributes.Module mod = v.attribute(Attributes.Module.class);			
				if(mod != null) {
					NameID name = new NameID(mod.module,v.var);
					Value val = constants.get(name);
					if(val == null) {
						// indicates a non-local constant definition
						Module mi = loader.loadModule(mod.module);
						val = mi.constant(v.var).constant();					
					} 
					lhs = val;				
				} else {
					syntaxError("unknown variable", filename, v);
					return null; // dead code
				}
			}
		} else {
			lhs = CExpr.VAR(t,v.var);
		}
		checkType(t, Type.Bool.class, v);		
		blk.add(new Code.IfGoto(t, Code.COP.EQ, lhs, Value
				.V_BOOL(true), target),v
				.attribute(Attribute.Source.class));
		return blk;
	}
	
	protected Block resolveCondition(String target, BinOp v,
			HashMap<String, Type> environment, HashMap<String,Pair<Type,Block>> declared) {		
		BOp bop = v.op;		
		Block blk = new Block();
		
		if (bop == BOp.OR) {			
			blk.addAll(resolveCondition(target, v.lhs, new HashMap(environment), declared));
			blk.addAll(resolveCondition(target, v.rhs, new HashMap(environment), declared));
			return blk;
		} else if (bop == BOp.AND) {			
			String exitLabel = Block.freshLabel();
			blk.addAll(resolveCondition(exitLabel, invert(v.lhs), environment,
					declared));			
			blk.addAll(resolveCondition(target, v.rhs, environment, declared));
			blk.add(new Code.Label(exitLabel));
			return blk;
		} else if (bop == BOp.TYPEEQ || bop == BOp.TYPEIMPLIES) {
			return resolveTypeCondition(target,v,environment,declared);
		}
		
		Pair<CExpr,Block> lhs_tb = resolve(0,v.lhs, environment, declared);
		Pair<CExpr,Block> rhs_tb = resolve(1,v.rhs, environment, declared);
		blk.addAll(lhs_tb.second());
		blk.addAll(rhs_tb.second());
		Type lhs_t = lhs_tb.first().type();
		Type rhs_t = rhs_tb.first().type();
		Type lub = Type.leastUpperBound(lhs_t,rhs_t);
		CExpr lhs = convert(lub,lhs_tb.first());
		CExpr rhs = convert(lub,rhs_tb.first());
		
		if (bop == BOp.LT || bop == BOp.LTEQ || bop == BOp.GT
				|| bop == BOp.GTEQ) {
			checkIsSubtype(Type.T_REAL, lhs_t, v);
			checkIsSubtype(Type.T_REAL, rhs_t, v);
			blk.add(new Code.IfGoto(lub, OP2COP(bop, v), lhs, rhs, target),v
					.attribute(Attribute.Source.class));
			return blk;
		} else if (bop == BOp.SUBSET || bop == BOp.SUBSETEQ) {
			checkIsSubtype(Type.T_SET(Type.T_ANY), lhs_t, v);
			checkIsSubtype(Type.T_SET(Type.T_ANY), rhs_t, v);
			blk.add(new Code.IfGoto(lub, OP2COP(bop, v), lhs, rhs, target),v
					.attribute(Attribute.Source.class));
			return blk;
		} else if (bop == BOp.EQ || bop == BOp.NEQ) {
			if (!Type.isSubtype(lhs_t, rhs_t) && !Type.isSubtype(rhs_t, lhs_t)) {
				syntaxError("Cannot compare types", filename, v);
			}
			blk.add(new Code.IfGoto(lub, OP2COP(bop, v), lhs, rhs, target),v
					.attribute(Attribute.Source.class));
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
				syntaxError("Cannot compare types", filename, v);
			}
			lub = Type.leastUpperBound(lhs_t, element);
			blk.add(new Code.IfGoto(lub, OP2COP(bop, v), lhs, rhs, target),v
					.attribute(Attribute.Source.class));
			return blk;
		}

		syntaxError("expected boolean expression",filename,v);
		return null;
	}
	
	protected Block resolveTypeCondition(String target, BinOp v,
			HashMap<String, Type> environment, HashMap<String,Pair<Type,Block>> declared) {
		
		Pair<CExpr,Block> lhs_tb = resolve(0,v.lhs, environment, declared);				
		Pair<Type,Block> rhs_tb = resolve(((Expr.TypeConst) v.rhs).type);
		Type lhs_t = lhs_tb.first().type();
		Type rhs_t = rhs_tb.first();
		if(!Type.isSubtype(lhs_t, rhs_t) && !Type.isSubtype(rhs_t, lhs_t)) {
			syntaxError("incomparable types",filename,v);
		}
		
		Block blk = new Block();
		String exitLabel = Block.freshLabel();
		String trueLabel = Block.freshLabel();
		blk.addAll(lhs_tb.second());
		blk.add(new Code.IfGoto(Type.T_META, Code.COP.SUBTYPEEQ,
				lhs_tb.first(), Value.V_TYPE(rhs_tb.first()), trueLabel),v
				.attribute(Attribute.Source.class));
		
		// Perform the limited form of type inference currently supported
		typeInference(lhs_tb.first(),rhs_t,environment);
				
		blk.add(new Code.Goto(exitLabel));
		blk.add(new Code.Label(trueLabel));
		if(rhs_tb.second() != null) {
			Block constraint = rhs_tb.second();
			
			// recompute with updated environment.
			lhs_tb = resolve(0,v.lhs, environment, declared);
			// now substitute $ for the lhs
			HashMap<String,CExpr> binding = new HashMap<String,CExpr>();
			binding.put("$", lhs_tb.first());
			constraint = Block.substitute(binding,constraint);
			
			// Similarly, we need to make sure any labels used in the constraint do
			// not collide with labels used at the inline point.
			constraint = Block.relabel(constraint);
			
			// Now, chain failures to redirect to the exit point.
			constraint = Block.chain(exitLabel, constraint);
			
			// all done!
			blk.addAll(constraint);
		}
		blk.add(new Code.Goto(target));
		blk.add(new Code.Label(exitLabel));
		return blk;
	}
	
	protected void typeInference(CExpr e, Type type, HashMap<String, Type> environment) {		
		if(e instanceof CExpr.Variable) {
			CExpr.Variable var = (CExpr.Variable) e; 			
			environment.put(var.name,type);			
		} else if(e instanceof CExpr.TupleAccess){
			CExpr.TupleAccess et = (CExpr.TupleAccess) e;
			Type t = et.lhs.type();
			// FIXME: major problems here with named and union types.
			if(t instanceof Type.Tuple) {
				Type.Tuple tt = (Type.Tuple) t;
				HashMap<String,Type> types = new HashMap<String,Type>(tt.types);
				types.put(et.field,type);
				typeInference(et.lhs,Type.T_TUPLE(types),environment);
			}
		}
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
		syntaxError("expected boolean expression",filename,v);
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
				.V_BOOL(true), target),v.attribute(Attribute.Source.class));
		return blk;
	}
	
	protected Block resolveCondition(String target, TupleAccess v,
			HashMap<String, Type> environment,
			HashMap<String, Pair<Type, Block>> declared) {
		Pair<CExpr, Block> la = resolve(0, v, environment, declared);
		CExpr lhs = la.first();
		checkType(lhs.type(), Type.Bool.class, v);
		Block blk = la.second();
		blk.add(new Code.IfGoto(lhs.type(), Code.COP.EQ, lhs, Value
				.V_BOOL(true), target),v.attribute(Attribute.Source.class));
		return blk;
	}
	
	protected Block resolveCondition(String target, Invoke v,
			HashMap<String, Type> environment,
			HashMap<String, Pair<Type, Block>> declared) throws ResolveError {
		Pair<CExpr, Block> la = resolve(0, v, environment, declared);
		CExpr lhs = la.first();
		checkType(lhs.type(), Type.Bool.class, v);
		Block blk = la.second();
		blk.add(new Code.IfGoto(lhs.type(), Code.COP.EQ, lhs, Value
				.V_BOOL(true), target),v.attribute(Attribute.Source.class));
		return blk;
	}
	
	protected Block resolveCondition(String target, Comprehension e,
			HashMap<String, Type> environment, HashMap<String,Pair<Type,Block>> declared) {				
		if(e.cop != Expr.COp.NONE && e.cop != Expr.COp.SOME) {
			syntaxError("expected boolean expression",filename,e);
		}
		
		environment = new HashMap<String,Type>(environment);
		Block blk = new Block();
		ArrayList<Pair<CExpr.LVar,CExpr>> sources = new ArrayList();		
		for(Pair<String,Expr> src : e.sources) {
			Pair<CExpr,Block> r = resolve(0,src.second(),environment,declared);
			Type src_t = r.first().type();
			checkIsSubtype(Type.T_SET(Type.T_ANY),src_t,src.second());
			if(src_t instanceof Type.Set) {
				src_t = ((Type.Set)src_t).element;
			} else if(src_t instanceof Type.List) {
				src_t = ((Type.List)src_t).element;
			} else {
				// FIXME: missing cases here
			}
			sources.add(new Pair(CExpr.VAR(src_t, src.first()), r.first()));
			blk.addAll(r.second());
			environment.put(src.first(), src_t);
		}
							
		ArrayList<String> labels = new  ArrayList<String>();
		for(Pair<CExpr.LVar, CExpr> ent : sources) {
			String loopLabel = Block.freshLabel();
			labels.add(loopLabel);
			blk.add(new Code.Forall(loopLabel, ent.first(), ent.second()), e
					.attribute(Attribute.Source.class));
		}
		if(e.cop == Expr.COp.NONE) { 			
			String exitLabel = Block.freshLabel();						
			blk.addAll(resolveCondition(exitLabel,e.condition,environment,declared));
			for(int i=(labels.size()-1);i>=0;--i) {			
				blk.add(new Code.End(labels.get(i)));
			}								
			blk.add(new Code.Goto(target));
			blk.add(new Code.Label(exitLabel));
		} else { // SOME									
			blk.addAll(resolveCondition(target,e.condition,environment,declared));
			for(int i=(labels.size()-1);i>=0;--i) {			
				blk.add(new Code.End(labels.get(i)));
			}		
		} // ALL, LONE and ONE will be harder
				
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
							+ e.getClass().getName(), filename,e);			
			}
		} catch(SyntaxError se) {
			throw se;
		} catch(Exception ex) {
			syntaxError("internal failure", filename,e, ex);			
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
			receiver = checkType(type,Type.Process.class,s.receiver);			
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
		Attributes.Module modInfo = s.attribute(Attributes.Module.class);		
		Type.Fun funtype = bindFunction(modInfo.module, s.name, receiver, ptypes,s);
		
		if(funtype == null) {
			syntaxError("invalid or ambiguous method call",filename,s);
		}	
		
		// Apply parameter conversions as necessary
		int offset = s.receiver == null ? 0 : 1;
		for(int i=offset;i!=nargs.size();++i) {
			Type p_t = funtype.params.get(i-offset);
			CExpr arg = nargs.get(i);
			nargs.set(i, convert(p_t,arg));
		}
		
		s.attributes().add(new Attributes.Fun(funtype));
		NameID name = new NameID(modInfo.module,s.name);	
		
		// Now, if this method/function has one or more "cases" then we need to
		// select the right one, based on the pre / post conditions. 
		
		
		return new Pair<CExpr, Block>(CExpr.INVOKE(funtype, name, 0, nargs),
				blk);									
	}
			
	protected Pair<CExpr, Block> resolve(int target, Constant c,
			HashMap<String, Type> environment, HashMap<String,Pair<Type,Block>> declared) {
		return new Pair<CExpr,Block>(c.value,new Block());
	}
	
	protected Pair<CExpr, Block> resolve(int target, Variable v,
			HashMap<String, Type> environment,
			HashMap<String, Pair<Type, Block>> declared) throws ResolveError {
		Type t = environment.get(v.var);
		if(t == null) {
			// Definitely not a variable. Could be an alias, or a constant
			// though.
			Attributes.Alias alias = v.attribute(Attributes.Alias.class);
			if(alias != null) {
				return resolve(0,alias.alias,environment,declared);				
			} 
			Attributes.Module mod = v.attribute(Attributes.Module.class);			
			if(mod != null) {
				NameID name = new NameID(mod.module,v.var);
				Value val = constants.get(name);
				if(val == null) {
					// indicates a non-local constant definition
					Module mi = loader.loadModule(mod.module);
					val = mi.constant(v.var).constant();					
				} 
				return new Pair<CExpr, Block>(val,new Block());
			}
			// Give up!
			syntaxError("unknown variable",filename,v);			
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
		case PROCESSACCESS:
			Type.Process t = checkType(mhs_t, Type.Process.class, v.mhs);
			return new Pair<CExpr, Block>(CExpr.UNOP(CExpr.UOP.PROCESSACCESS, mhs
					.first()), blk);
		case PROCESSSPAWN:			
			return new Pair<CExpr, Block>(CExpr.UNOP(CExpr.UOP.PROCESSSPAWN, mhs
					.first()), blk);		
		default:
			syntaxError("unexpected unary operator encountered",filename,v);
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
					.V_BOOL(false)),v.attribute(Attribute.Source.class));
			blk.add(new Code.Goto(exitLabel));
			blk.add(new Code.Label(trueLabel));
			blk.add(new Code.Assign(CExpr.REG(Type.T_BOOL, target), Value
					.V_BOOL(true)),v.attribute(Attribute.Source.class));
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
			
			if (bop == BOp.ADD
					&& Type.isSubtype(Type.T_LIST(Type.T_ANY), lub)) {
				CExpr r = CExpr.BINOP(CExpr.BOP.APPEND, lhs, rhs);
				return new Pair<CExpr, Block>(r, blk);
			} else if ((bop == BOp.ADD || bop == BOp.SUB)
					&& Type.isSubtype(Type.T_SET(Type.T_ANY), lub)) {
				CExpr.BOP op = bop == BOp.ADD ? CExpr.BOP.UNION
						: CExpr.BOP.DIFFERENCE;
				CExpr r = CExpr.BINOP(op, lhs, rhs);
				return new Pair<CExpr, Block>(r, blk);
			} else {
				checkIsSubtype(Type.T_REAL, lub, v);			
				CExpr r = CExpr.BINOP(OP2BOP(bop, v), lhs, rhs);
				return new Pair<CExpr, Block>(r,blk);
			}
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
				syntaxError("incorrect number of arguments",filename,v);
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
		
		// First, check for boolean cases which are handled mostly by
		// resolveCondition.
		if (e.cop == Expr.COp.SOME || e.cop == Expr.COp.NONE) {
			String trueLabel = Block.freshLabel();
			String exitLabel = Block.freshLabel();
			Block blk = resolveCondition(trueLabel, e, environment, declared);
			blk.add(new Code.Assign(CExpr.REG(Type.T_BOOL, target), Value
					.V_BOOL(false)),e.attribute(Attribute.Source.class));
			blk.add(new Code.Goto(exitLabel));
			blk.add(new Code.Label(trueLabel));
			blk.add(new Code.Assign(CExpr.REG(Type.T_BOOL, target), Value
					.V_BOOL(true)),e.attribute(Attribute.Source.class));
			blk.add(new Code.Label(exitLabel));
			return new Pair<CExpr, Block>(CExpr.REG(Type.T_BOOL, target), blk);
		}
		
		// Ok, non-boolean case.
		
		environment = new HashMap<String,Type>(environment);
		ArrayList<Pair<CExpr.LVar,CExpr>> sources = new ArrayList();
		Block blk = new Block();
		for(Pair<String,Expr> src : e.sources) {
			Pair<CExpr,Block> r = resolve(0,src.second(),environment,declared);
			Type src_t = r.first().type();
			checkIsSubtype(Type.T_SET(Type.T_ANY),src_t,src.second());
			if(src_t instanceof Type.Set) {
				src_t = ((Type.Set)src_t).element;
			} else if(src_t instanceof Type.List) {
				src_t = ((Type.List)src_t).element;
			} else {
				// FIXME: missing cases here
			}
			sources.add(new Pair(CExpr.VAR(src_t, src.first()), r.first()));
			blk.addAll(r.second());
			environment.put(src.first(), src_t);
		}
		
		Pair<CExpr,Block> value = resolve(target+1,e.value,environment,declared);
		Type type = value.first().type();;
		CExpr.Register lhs;
		
		if(e.cop == Expr.COp.LISTCOMP) { 
			lhs = CExpr.REG(Type.T_LIST(type),target);
			blk.add(new Code.Assign(lhs, CExpr.NARYOP(CExpr.NOP.LISTGEN)), e
					.attribute(Attribute.Source.class));
		} else {
			lhs = CExpr.REG(Type.T_SET(type),target);			
			blk.add(new Code.Assign(lhs, CExpr.NARYOP(CExpr.NOP.SETGEN)), e
					.attribute(Attribute.Source.class));
		}		
								
		String continueLabel = Block.freshLabel();
		ArrayList<String> labels = new  ArrayList<String>();
		for(Pair<CExpr.LVar, CExpr> ent : sources) {
			String loopLabel = Block.freshLabel();
			labels.add(loopLabel);
			blk.add(new Code.Forall(loopLabel, ent.first(), ent.second()), e
					.attribute(Attribute.Source.class));
		}
		if (e.condition != null) {
			blk.addAll(resolveCondition(continueLabel, invert(e.condition),
					environment, declared));
			blk.addAll(value.second());
			blk.add(new Code.Assign(lhs, CExpr.BINOP(CExpr.BOP.UNION, lhs,
					CExpr.NARYOP(CExpr.NOP.SETGEN, value.first()))), e
					.attribute(Attribute.Source.class));
			blk.add(new Code.Label(continueLabel));
		} else {			
			blk.addAll(value.second());
			blk.add(new Code.Assign(lhs, CExpr.BINOP(CExpr.BOP.UNION, lhs,
					CExpr.NARYOP(CExpr.NOP.SETGEN, value.first()))), e
					.attribute(Attribute.Source.class));
		}
		
		for(int i=(labels.size()-1);i>=0;--i) {			
			blk.add(new Code.End(labels.get(i)));
		}
				
		return new Pair<CExpr,Block>(lhs,blk);
	}
		
	protected Pair<CExpr, Block> resolve(int target, TupleGen sg,
			HashMap<String, Type> environment, HashMap<String,Pair<Type,Block>> declared) {
		HashMap<String, CExpr> values = new HashMap<String, CExpr>();
		Block blk = new Block();
		for (Map.Entry<String, Expr> e : sg.fields.entrySet()) {
			Pair<CExpr, Block> tb = resolve(target, e.getValue(), environment,
					declared);
			values.put(e.getKey(), tb.first());
			blk.addAll(tb.second());
		}
		return new Pair<CExpr,Block>(CExpr.TUPLE(values),blk);		
	}
	
	protected Pair<CExpr, Block> resolve(int target, TupleAccess sg,
			HashMap<String, Type> environment,
			HashMap<String, Pair<Type, Block>> declared) {
		Pair<CExpr, Block> lhs = resolve(target, sg.lhs, environment, declared);
		// FIXME: will need to determine effective tuple type here
		Type.Tuple ett = Type.effectiveTupleType(lhs.first().type());		
		Type.Tuple tup = checkType(ett, Type.Tuple.class, sg.lhs);
		Type ft = tup.types.get(sg.name);
		if (ft == null) {
			syntaxError("type has no field named: " + sg.name, filename, sg.lhs);
		}
		return new Pair<CExpr, Block>(CExpr.TUPLEACCESS(lhs.first(), sg.name), lhs
				.second());
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
			ModuleID mid = dt.attribute(Attributes.Module.class).module;
			if(modules.contains(mid)) {
				return types.get(new NameID(mid,dt.name));
			} else {
				try {
					Module mi = loader.loadModule(mid);
					Module.TypeDef td = mi.type(dt.name);			
					return new Pair<Type,Block>(td.type(),null);
				} catch(ResolveError rex) {
					syntaxError(rex.getMessage(),filename,t,rex);
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
				return new Pair<Type,Block>(Type.leastUpperBound(bounds),null);
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
			syntaxError("expected type " + clazz.getName() + ", found " + t,filename,
					elem);
			return null;
		}
	}
	
	// Check t1 :> t2
	protected void checkIsSubtype(Type t1, Type t2, SyntacticElement elem) {
		if (!Type.isSubtype(t1, t2)) {
			syntaxError("expected type " + t1 + ", found " + t2, filename, elem);
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
		
		return Type.renameRecursiveTypes(t, binding);
	}	
	
	public Variable flattern(Expr e) {
		if(e instanceof Variable) {
			return (Variable) e;
		} else if(e instanceof ListAccess) {
			ListAccess la = (ListAccess) e;
			return flattern(la.src);
		} else if(e instanceof TupleAccess) {
			TupleAccess la = (TupleAccess) e;
			return flattern(la.lhs);
		} else if(e instanceof UnOp) {
			UnOp la = (UnOp) e;
			if(la.op == Expr.UOp.PROCESSACCESS) {
				return flattern(la.mhs);
			}
		} 
		syntaxError("invalid lval",filename,e);
		return null;		
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
		
	public CExpr.BOP OP2BOP(Expr.BOp bop, SyntacticElement elem) {
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
		syntaxError("unrecognised binary operation",filename,elem);
		return null;
	}
	
	public Code.COP OP2COP(Expr.BOp bop, SyntacticElement elem) {
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
		syntaxError("unrecognised binary operation", filename,elem);
		return null;
	}	
}
