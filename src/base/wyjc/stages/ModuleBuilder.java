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

package wyjc.stages;

import java.util.*;
import java.math.BigInteger;

import static wyil.util.SyntaxError.*;
import wyil.ModuleLoader;
import wyil.stages.TypePropagation;
import wyil.util.*;
import wyil.lang.*;
import wyil.lang.Code.IfGoto;
import wyjc.lang.*;
import wyjc.lang.WhileyFile.*;
import wyjc.lang.Stmt;
import wyjc.lang.Stmt.*;
import wyjc.lang.Expr.*;

public class ModuleBuilder {
	private final ModuleLoader loader;	
	private HashSet<ModuleID> modules;
	private HashMap<NameID, WhileyFile> filemap;
	private HashMap<NameID, List<Type.Fun>> functions;
	private HashMap<NameID, Type> types;
	private HashMap<NameID, Value> constants;
	private HashMap<NameID, UnresolvedType> unresolved;
	private Stack<Scope> scopes = new Stack<Scope>();
	private String filename;
	private FunDecl currentFunDecl;

	// The shadow set is used to (efficiently) aid the correct generation of
	// runtime checks for post conditions. The key issue is that a post
	// condition may refer to parameters of the method. However, if those
	// parameters are modified during the method, then we must store their
	// original value on entry for use in the post-condition runtime check.
	// These stored values are called "shadows".
	private final HashMap<String, CExpr> shadows = new HashMap<String, CExpr>();

	public ModuleBuilder(ModuleLoader loader) {
		this.loader = loader;		
	}

	public List<Module> resolve(List<WhileyFile> files) {
		modules = new HashSet<ModuleID>();
		filemap = new HashMap<NameID, WhileyFile>();
		functions = new HashMap<NameID, List<Type.Fun>>();
		types = new HashMap<NameID, Type>();
		constants = new HashMap<NameID, Value>();
		unresolved = new HashMap<NameID, UnresolvedType>();

		// now, init data
		for (WhileyFile f : files) {
			modules.add(f.module);
		}

		// Stage 1 ... resolve and check types of all named types + constants
		generateConstants(files);
		generateTypes(files);

		// Stage 2 ... resolve and check types for all functions / methods
		for (WhileyFile f : files) {
			for (WhileyFile.Decl d : f.declarations) {
				if (d instanceof FunDecl) {
					partResolve(f.module, (FunDecl) d);
				}
			}
		}

		// Stage 3 ... resolve, propagate types for all expressions
		ArrayList<Module> modules = new ArrayList<Module>();
		for (WhileyFile f : files) {
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
	 * The following method visits every define constant statement in every
	 * whiley file being compiled, and determines its true and value.
	 * 
	 * @param files
	 */
	protected void generateConstants(List<WhileyFile> files) {
		HashMap<NameID, Expr> exprs = new HashMap();

		// first construct list.
		for (WhileyFile f : files) {
			for (Decl d : f.declarations) {
				if (d instanceof ConstDecl) {
					ConstDecl cd = (ConstDecl) d;
					NameID key = new NameID(f.module, cd.name());
					exprs.put(key, cd.constant);
					filemap.put(key, f);
				}
			}
		}

		for (NameID k : exprs.keySet()) {
			try {
				Value v = expandConstant(k, exprs, new HashSet<NameID>());
				constants.put(k, v);
				Type t = v.type();
				if (t instanceof Type.Set) {
					Type.Set st = (Type.Set) t;					
					types.put(k, st.element);
				}
			} catch (ResolveError rex) {
				syntaxError(rex.getMessage(), filemap.get(k).filename, exprs
						.get(k), rex);
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
	protected Value expandConstant(NameID key, HashMap<NameID, Expr> exprs,
			HashSet<NameID> visited) throws ResolveError {
		Expr e = exprs.get(key);
		Value value = constants.get(key);
		if (value != null) {
			return value;
		} else if (!modules.contains(key.module())) {
			// indicates a non-local key
			Module mi = loader.loadModule(key.module());
			return mi.constant(key.name()).constant();
		} else if (visited.contains(key)) {
			// this indicates a cyclic definition.
			syntaxError("cyclic constant definition encountered", filemap
					.get(key).filename, exprs.get(key));
		} else {
			visited.add(key); // mark this node as visited
		}

		// At this point, we need to replace every unresolved variable with a
		// constant definition.
		Value v = expandConstantHelper(e, filemap.get(key).filename, exprs,
				visited);
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
	protected Value expandConstantHelper(Expr expr, String filename,
			HashMap<NameID, Expr> exprs, HashSet<NameID> visited)
			throws ResolveError {
		if (expr instanceof Constant) {
			Constant c = (Constant) expr;
			return c.value;
		} else if (expr instanceof Variable) {
			// Note, this must be a constant definition of some sort
			Variable v = (Variable) expr;
			Attributes.Module mid = expr.attribute(Attributes.Module.class);
			if (mid != null) {
				NameID name = new NameID(mid.module, v.var);
				return expandConstant(name, exprs, visited);
			}
		} else if (expr instanceof BinOp) {
			BinOp bop = (BinOp) expr;
			Value lhs = expandConstantHelper(bop.lhs, filename, exprs, visited);
			Value rhs = expandConstantHelper(bop.rhs, filename, exprs, visited);
			Value v = Value.evaluate(OP2BOP(bop.op, expr), lhs, rhs);
			if (v != null) {
				return v;
			}
		} else if (expr instanceof NaryOp) {
			Expr.NaryOp nop = (NaryOp) expr;
			ArrayList<Value> values = new ArrayList<Value>();
			for (Expr arg : nop.arguments) {
				values.add(expandConstantHelper(arg, filename, exprs, visited));
			}
			if (nop.nop == Expr.NOp.LISTGEN) {
				return Value.V_LIST(values);
			} else if (nop.nop == Expr.NOp.SETGEN) {
				return Value.V_SET(values);
			}
		} else if (expr instanceof RecordGen) {
			RecordGen rg = (RecordGen) expr;
			HashMap<String,Value> values = new HashMap<String,Value>();
			for(Map.Entry<String,Expr> e : rg.fields.entrySet()) {
				Value v = expandConstantHelper(e.getValue(),filename,exprs,visited);
				if(v == null) {
					return null;
				}
				values.put(e.getKey(), v);
			}
			return Value.V_RECORD(values);
		} else if(expr instanceof FunConst) {
			FunConst f = (FunConst) expr;
			Attributes.Module mid = expr.attribute(Attributes.Module.class);
			if (mid != null) {
				NameID name = new NameID(mid.module, f.name);
				ArrayList<Type> paramTypes = new ArrayList<Type>();
				for(UnresolvedType p : f.paramTypes) {
					paramTypes.add(resolve(p));
				}				
				return Value.V_FUN(name, Type.T_FUN(null,Type.T_ANY, paramTypes));	
			}					
		}

		syntaxError("invalid expression in constant definition", filename, expr);
		return null;
	}

	/**
	 * The following method visits every define type statement in every whiley
	 * file being compiled, and determines its true type.
	 * 
	 * @param files
	 */
	protected void generateTypes(List<WhileyFile> files) {
		HashMap<NameID, SyntacticElement> srcs = new HashMap();
		
		// The declOrder list is basically a hack. It ensures that types are
		// visited in the order that they are declared. This helps give some
		// sense to the way recursive types are handled, but a more general
		// solution could easily be found.
		ArrayList<NameID> declOrder = new ArrayList<NameID>();
		
		// second construct list.
		for (WhileyFile f : files) {
			for (Decl d : f.declarations) {
				if (d instanceof TypeDecl) {
					TypeDecl td = (TypeDecl) d;					
					NameID key = new NameID(f.module, td.name());
					declOrder.add(key);
					unresolved.put(key, td.type);
					srcs.put(key, d);
					filemap.put(key, f);
				}
			}
		}

		// third expand all types
		for (NameID key : declOrder) {			
			try {
				HashMap<NameID, Type> cache = new HashMap<NameID, Type>();				
				Type t = expandType(key, cache);								
				if (Type.isExistential(t)) {
					t = Type.T_NAMED(key, t);
				}
				types.put(key, t);
			} catch (ResolveError ex) {
				syntaxError(ex.getMessage(), filemap.get(key).filename, srcs
						.get(key), ex);
			}
		}
	}

	/**
	 * This is a deeply complex method!
	 * 
	 * @param key
	 * @param cache
	 * @return A triple of the form <T,B,C>, where T is the type, B is the
	 *         constraint block and C indicates whether or not this is in fact a
	 *         constrained type. The latter is useful since it means we can
	 *         throw away unnecessary constraint blocks when the type in
	 *         question is not actually constrained.
	 * @throws ResolveError
	 */
	protected Type expandType(NameID key,
			HashMap<NameID, Type> cache) throws ResolveError {
		
		Type cached = cache.get(key);
		Type t = types.get(key);
		
		if (cached != null) {			
			return cached;
		} else if(t != null) {
			return t;
		} else if (!modules.contains(key.module())) {
			// indicates a non-local key which we can resolve immediately
			Module mi = loader.loadModule(key.module());
			Module.TypeDef td = mi.type(key.name());
			return td.type();
		}

		// following is needed to terminate any recursion
		cache.put(key, Type.T_RECURSIVE(key.toString(), null));

		// now, expand the type fully		
		t = expandType(unresolved.get(key), filemap.get(key).filename,
				cache);

		// Now, we need to test whether the current type is open and recursive
		// on this name. In such case, we must close it in order to complete the
		// recursive type.
		boolean isOpenRecursive = Type.isOpenRecursive(key.toString(), t);
		if (isOpenRecursive) {
			t = Type.T_RECURSIVE(key.toString(), t);
		}
					
		// finally, store it in the cache
		cache.put(key, t);

		// Done
		return t;
	}

	protected Type expandType(UnresolvedType t, String filename,
			HashMap<NameID, Type> cache) {
		if (t instanceof UnresolvedType.List) {
			UnresolvedType.List lt = (UnresolvedType.List) t;			
			return Type.T_LIST(expandType(lt.element, filename, cache));			
		} else if (t instanceof UnresolvedType.Set) {
			UnresolvedType.Set st = (UnresolvedType.Set) t;			
			return Type.T_SET(expandType(st.element, filename, cache));					
		} else if (t instanceof UnresolvedType.Dictionary) {
			UnresolvedType.Dictionary st = (UnresolvedType.Dictionary) t;			
			return Type.T_DICTIONARY(expandType(st.key, filename, cache),
					expandType(st.value, filename, cache));					
		} else if (t instanceof UnresolvedType.Record) {
			UnresolvedType.Record tt = (UnresolvedType.Record) t;
			HashMap<String, Type> types = new HashMap<String, Type>();								
			for (Map.Entry<String, UnresolvedType> e : tt.types.entrySet()) {
				Type p = expandType(e.getValue(), filename, cache);
				types.put(e.getKey(), p);				
			}
			return Type.T_RECORD(types);						
		} else if (t instanceof UnresolvedType.Union) {
			UnresolvedType.Union ut = (UnresolvedType.Union) t;
			HashSet<Type.NonUnion> bounds = new HashSet<Type.NonUnion>();
			for(int i=0;i!=ut.bounds.size();++i) {
				UnresolvedType b = ut.bounds.get(i);
				
				Type bt = expandType(b, filename, cache);				
				if (bt instanceof Type.NonUnion) {
					bounds.add((Type.NonUnion) bt);
				} else {
					bounds.addAll(((Type.Union) bt).bounds);
				}				
			}
			
			Type type;
			if (bounds.size() == 1) {
				return bounds.iterator().next();
			} else {				
				return Type.leastUpperBound(bounds);
			}			
		} else if (t instanceof UnresolvedType.Process) {
			UnresolvedType.Process ut = (UnresolvedType.Process) t;
			return Type.T_PROCESS(expandType(ut.element, filename, cache));							
		} else if (t instanceof UnresolvedType.Named) {
			UnresolvedType.Named dt = (UnresolvedType.Named) t;
			Attributes.Module modInfo = dt.attribute(Attributes.Module.class);
			NameID name = new NameID(modInfo.module, dt.name);

			try {
				Type et = expandType(name, cache);								
				
				if (Type.isExistential(et)) {
					return Type.T_NAMED(new NameID(modInfo.module, dt.name), et);
				} else {
					return et;
				}
								
			} catch (ResolveError rex) {
				syntaxError(rex.getMessage(), filename, t, rex);
				return null;
			}
		} else {
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
		Type.ProcessName rec = null;
		if (fd.receiver != null) {
			Type t = resolve(fd.receiver);
			checkType(t, Type.Process.class, fd.receiver);
			rec = (Type.ProcessName) t;
		}

		Type.Fun ft = Type.T_FUN(rec, ret, parameters);
		NameID name = new NameID(module, fd.name);
		List<Type.Fun> types = functions.get(name);
		if (types == null) {
			types = new ArrayList<Type.Fun>();
			functions.put(name, types);
		}
		types.add(ft);
		fd.attributes().add(new Attributes.Fun(ft));
	}

	protected Module.ConstDef resolve(ConstDecl td, ModuleID module) {
		Value v = constants.get(new NameID(module, td.name()));
		return new Module.ConstDef(td.name(), v);
	}

	protected Module.TypeDef resolve(TypeDecl td, ModuleID module) {
		return new Module.TypeDef(td.name(), types.get(new NameID(module, td.name())));
	}

	protected Module.Method resolve(FunDecl fd) {
		ArrayList<String> parameterNames = new ArrayList<String>();
		
		// method parameter types
		for (WhileyFile.Parameter p : fd.parameters) {
			parameterNames.add(p.name());			
		}

		// method return type
		Type ret = resolve(fd.ret);		

		// method receiver type (if applicable)
		if (fd.receiver != null) {
			Type rec = resolve(fd.receiver);					
		}
		
		currentFunDecl = fd;
		Type.Fun tf = fd.attribute(Attributes.Fun.class).type;

		Block blk = new Block();
		
		for (Stmt s : fd.statements) {
			blk.addAll(resolve(s, 0));
		}

		currentFunDecl = null;
		
		// The following is sneaky. It guarantees that every method ends in a
		// return. For methods that actually need a value, this is either
		// removed as dead-code or remains and will cause an error.
		blk.add(new Code.Return(null),fd.attribute(Attribute.Source.class));

		List<Module.Case> ncases = new ArrayList<Module.Case>();
		ncases.add(new Module.Case(parameterNames, blk));
		return new Module.Method(fd.name(), tf, ncases);
	}

	public Block resolve(Stmt stmt, int freeReg) {
		try {
			if (stmt instanceof Assign) {
				return resolve((Assign) stmt, freeReg);
			} else if (stmt instanceof Assert) {
				return resolve((Assert) stmt, freeReg);
			} else if (stmt instanceof Return) {
				return resolve((Return) stmt, freeReg);
			} else if (stmt instanceof Debug) {
				return resolve((Debug) stmt, freeReg);
			} else if (stmt instanceof IfElse) {
				return resolve((IfElse) stmt, freeReg);
			} else if (stmt instanceof Switch) {
				return resolve((Switch) stmt, freeReg);
			} else if (stmt instanceof Break) {
				return resolve((Break) stmt, freeReg);
			} else if (stmt instanceof While) {
				return resolve((While) stmt, freeReg);
			} else if (stmt instanceof For) {
				return resolve((For) stmt, freeReg);
			} else if (stmt instanceof Invoke) {
				Pair<CExpr, Block> p = resolve(freeReg, (Invoke) stmt);
				Block blk = p.second();
				blk.add(new Code.Assign(null, p.first()), stmt
						.attribute(Attribute.Source.class));				
				return blk;
			} else if (stmt instanceof Spawn) {
				return resolve(freeReg, (UnOp) stmt).second();
			} else if (stmt instanceof ExternJvm) {
				return resolve((ExternJvm) stmt, freeReg);
			} else if (stmt instanceof Skip) {
				return resolve((Skip) stmt, freeReg);
			} else {
				syntaxError("unknown statement encountered: "
						+ stmt.getClass().getName(), filename, stmt);
			}
		} catch (ResolveError rex) {
			syntaxError(rex.getMessage(), filename, stmt, rex);
		} catch (SyntaxError sex) {
			throw sex;
		} catch (Exception ex) {			
			syntaxError("internal failure", filename, stmt, ex);
		}
		return null;
	}
	
	protected Block resolve(Assign s, int freeReg) {

		Block blk = new Block();		
		Pair<CExpr, Block> rhs_tb = resolve(freeReg + 1, s.rhs);
		
		if(s.lhs instanceof TupleGen) {
			// this indicates a tuple assignment which must be treated specially.
			TupleGen tg = (TupleGen) s.lhs;			
			CExpr.Register reg = CExpr.REG(Type.T_ANY, freeReg);
			blk.addAll(rhs_tb.second());
			blk.add(new Code.Assign(reg, rhs_tb.first()), s
					.attribute(Attribute.Source.class));
			int idx=0;
			for(Expr e : tg.fields) {
				if(!(e instanceof Variable)) {
					syntaxError("variable expected",filename,e);
				}
				Variable v = (Variable) e;
				blk.add(new Code.Assign(CExpr.VAR(Type.T_ANY, v.var), CExpr
						.RECORDACCESS(reg, "$" + idx++)), e
						.attribute(Attribute.Source.class));					
			}
			return blk;
		}
		
		if(s.lhs instanceof Variable) {
			// This is a special case, needed to prevent field inference from
			// thinking it's a field.
			Variable v = (Variable) s.lhs;
			blk.addAll(rhs_tb.second());
			blk.add(new Code.Assign(CExpr.VAR(Type.T_ANY,v.var), rhs_tb
					.first()), s.attribute(Attribute.Source.class));			
		} else {

			Pair<CExpr, Block> lhs_tb = resolve(freeReg, s.lhs);

			if(lhs_tb.first() instanceof CExpr.LVal) {

				blk.addAll(lhs_tb.second());
				blk.addAll(rhs_tb.second());
				blk.add(new Code.Assign(((CExpr.LVal)lhs_tb.first()), rhs_tb
						.first()), s.attribute(Attribute.Source.class));				
			} else {
				syntaxError("invalid assignment", filename, s);
			}
		}
		
		return blk;
	}

	protected Block resolve(Assert s, int freeReg) {
		String lab = Block.freshLabel();
		String clab = Block.freshLabel();
		Block blk = new Block();
		blk.add(new Code.Check(clab),s.attribute(Attribute.Source.class));
		blk.addAll(resolveCondition(lab, s.expr, freeReg));		
		blk.add(new Code.Fail("assertion failed"), s
				.attribute(Attribute.Source.class));
		blk.add(new Code.Label(lab));
		blk.add(new Code.CheckEnd(clab),s.attribute(Attribute.Source.class));		
		return blk;
	}

	protected Block resolve(Return s, int freeReg) {

		if (s.expr != null) {
			Pair<CExpr, Block> t = resolve(freeReg, s.expr);
			Block blk = new Block();
			blk.addAll(t.second());

			Type ret = resolve(currentFunDecl.ret);
						
			blk.add(new Code.Return(t.first()), s
					.attribute(Attribute.Source.class));
			return blk;			
		} else {
			Block blk = new Block();
			blk.add(new Code.Return(null), s.attribute(Attribute.Source.class));
			return blk;
		}
	}

	protected Block resolve(ExternJvm s, int freeReg) {
		Block blk = new Block();
		blk.add(new Code.ExternJvm(s.bytecodes), s
				.attribute(Attribute.Source.class));
		return blk;
	}

	protected Block resolve(Skip s, int freeReg) {
		Block blk = new Block();
		blk.add(new Code.Skip(), s.attribute(Attribute.Source.class));
		return blk;
	}

	protected Block resolve(Debug s, int freeReg) {
		Pair<CExpr, Block> t = resolve(freeReg, s.expr);
		Block blk = t.second();
		blk.add(new Code.Debug(t.first()), s.attribute(Attribute.Source.class));
		return blk;
	}

	protected Block resolve(IfElse s, int freeReg) {
		String falseLab = Block.freshLabel();
		String exitLab = s.falseBranch.isEmpty() ? falseLab : Block
				.freshLabel();
		Block blk = resolveCondition(falseLab, invert(s.condition), freeReg);

		for (Stmt st : s.trueBranch) {
			blk.addAll(resolve(st, freeReg));
		}
		if (!s.falseBranch.isEmpty()) {
			blk.add(new Code.Goto(exitLab));
			blk.add(new Code.Label(falseLab));
			for (Stmt st : s.falseBranch) {
				blk.addAll(resolve(st, freeReg));
			}
		}

		blk.add(new Code.Label(exitLab));

		return blk;
	}

	protected Block resolve(Break s, int freeReg) {
		BreakScope scope = findEnclosingScope(BreakScope.class);
		if(scope == null) {
			syntaxError("break outside switch or loop",filename,s);
		}
		Block blk = new Block();
		blk.add(new Code.Goto(scope.label));
		return blk;
	}
	
	protected Block resolve(Switch s, int freeReg) {
		String exitLab = Block.freshLabel();		
		Pair<CExpr,Block> _blk = resolve(freeReg, s.expr);				
		Block cblk = new Block();
		String defaultTarget = exitLab;
		ArrayList<Pair<Value,String>> cases = new ArrayList();
		HashSet<Value> caseValues = new HashSet<Value>();
		scopes.push(new BreakScope(exitLab));
		for(Stmt.Case c : s.cases) {			
			if(c.value == null) {
				// indicates the default block
				if(defaultTarget != exitLab) {
					syntaxError("duplicate default label",filename,c);
				} else {
					defaultTarget = Block.freshLabel();	
					cblk.add(new Code.Label(defaultTarget), c.attributes());
					for (Stmt st : c.stmts) {
						cblk.addAll(resolve(st, freeReg));
					}
					cblk.add(new Code.Goto(exitLab),c.attributes());
				}
			} else if(defaultTarget == exitLab) {
				Pair<CExpr,Block> b = resolve(freeReg, c.value);				
				if(b.first() instanceof Value) {
					String target = Block.freshLabel();	
					cblk.add(new Code.Label(target), c.attributes());
					Value v = (Value) b.first();
					if(caseValues.contains(v)) {
						syntaxError("duplicate case label",filename,c);
					}
					caseValues.add(v);
					cases.add(new Pair(v,target));
					for (Stmt st : c.stmts) {
						cblk.addAll(resolve(st, freeReg));
					}				
				} else {
					syntaxError("constant expression required",filename,c);
				}
			} else {
				syntaxError("unreachable code",filename,c);
			}
		}
		Block blk = _blk.second();		
		blk.add(new Code.Switch(_blk.first(),defaultTarget,cases));
		blk.addAll(cblk);
		blk.add(new Code.Label(exitLab), s.attributes());
		scopes.pop();
		return blk;
	}
	
	protected Block resolve(While s, int freeReg) {		
		String chklab = Block.freshLabel();
		String entry = Block.freshLabel();
		String label = Block.freshLabel();
		String loopend = Block.freshLabel();
		String exitLab = Block.freshLabel();
		
		Block invariant = null;
		Block blk = new Block();
		
		blk.add(new Code.Loop(label, invariant, Collections.EMPTY_SET), s
				.attribute(Attribute.Source.class));
		
		blk.addAll(resolveCondition(exitLab, invert(s.condition), freeReg));

		for (Stmt st : s.body) {
			blk.addAll(resolve(st, freeReg));
		}		
		
		blk.add(new Code.LoopEnd(label), s.attribute(Attribute.Source.class));		
		blk.add(new Code.Label(exitLab));

		return blk;
	}

	protected Block resolve(For s, int freeReg) {		
		String label = Block.freshLabel();
		Pair<CExpr,Block> source = resolve(freeReg,s.source);
		Block blk = new Block();
		Block invariant = null;
		
		blk.addAll(source.second());
		CExpr.Register reg = CExpr.REG(Type.T_ANY, freeReg); 
		blk.add(new Code.Forall(label, invariant, reg, source.first()), s
				.attribute(Attribute.Source.class));
				
		HashMap<String,CExpr> binding = new HashMap<String,CExpr>();
		binding.put(s.variable,reg);
		// FIXME: add a continue scope
		scopes.push(new BreakScope(label));		
		for (Stmt st : s.body) {
			Block b = resolve(st, freeReg+1);
			blk.addAll(Block.substitute(binding, b));
		}		
		scopes.pop(); // break
		blk.add(new Code.ForallEnd(label), s.attribute(Attribute.Source.class));		

		return blk;
	}
	
	/**
	 * Target gives the name of the register to use to store the result of this
	 * expression in.
	 * 
	 * @param target
	 * @param e
	 * @param environment
	 * @return
	 */
	protected Block resolveCondition(String target, Expr e, int freeReg) {
		try {
			if (e instanceof Constant) {
				return resolveCondition(target, (Constant) e, freeReg);
			} else if (e instanceof Variable) {
				return resolveCondition(target, (Variable) e, freeReg);
			} else if (e instanceof BinOp) {
				return resolveCondition(target, (BinOp) e, freeReg);
			} else if (e instanceof UnOp) {
				return resolveCondition(target, (UnOp) e, freeReg);
			} else if (e instanceof Invoke) {
				return resolveCondition(target, (Invoke) e, freeReg);
			} else if (e instanceof RecordAccess) {
				return resolveCondition(target, (RecordAccess) e, freeReg);
			} else if (e instanceof RecordGen) {
				return resolveCondition(target, (RecordGen) e, freeReg);
			} else if (e instanceof TupleGen) {
				return resolveCondition(target, (TupleGen) e, freeReg);
			} else if (e instanceof ListAccess) {
				return resolveCondition(target, (ListAccess) e, freeReg);
			} else if (e instanceof Comprehension) {
				return resolveCondition(target, (Comprehension) e, freeReg);
			} else {
				syntaxError("expected boolean expression, got: "
						+ e.getClass().getName(), filename, e);
			}
		} catch (SyntaxError se) {
			throw se;
		} catch (Exception ex) {
			syntaxError("internal failure", filename, e, ex);
		}

		return null;
	}

	protected Block resolveCondition(String target, Constant c, int freeReg) {
		Value.Bool b = (Value.Bool) c.value;
		Block blk = new Block();
		if (b.value) {
			blk.add(new Code.Goto(target));
		} else {
			// do nout
		}
		return blk;
	}

	protected Block resolveCondition(String target, Variable v, int freeReg) throws ResolveError {
		CExpr lhs = null;
		Block blk = new Block();
		// First, check whether this is an alias or a 
		Attributes.Alias alias = v.attribute(Attributes.Alias.class);					
		Attributes.Module mod = v.attribute(Attributes.Module.class);
		Type.Fun tf = null;
		
		if(currentFunDecl != null) {
			tf = currentFunDecl.attribute(Attributes.Fun.class).type;
		}			
		
		// Second, see if it's a field of the receiver
		if (alias != null) {
			if(alias.alias != null) {				
				Pair<CExpr, Block> p = resolve(freeReg, alias.alias);
				blk.addAll(p.second());
				lhs = p.first();
			} else {				
				// Ok, must be a local variable
				lhs = CExpr.VAR(Type.T_ANY, v.var);	
			}
		} else if(tf != null && tf.receiver != null) {
			Type pt = tf.receiver;
			if(pt instanceof Type.Named) {
				pt = ((Type.Named)pt).type;
			}
			if(pt instanceof Type.Process) {
				Type.Record ert = Type.effectiveRecordType(((Type.Process)pt).element);
				if(ert != null && ert.types.containsKey(v.var)) {
					// Bingo, this is an implicit field dereference
					CExpr thiz = CExpr.UNOP(CExpr.UOP.PROCESSACCESS, CExpr.VAR(
							Type.T_ANY, "this"));					
					lhs = CExpr.RECORDACCESS(thiz, v.var);					
				}
			}
		} else if (mod != null) {
			NameID name = new NameID(mod.module, v.var);
			Value val = constants.get(name);
			if (val == null) {
				// indicates a non-local constant definition
				Module mi = loader.loadModule(mod.module);
				val = mi.constant(v.var).constant();
			}
			lhs = val;
		} 
		
		if(lhs == null) {
			syntaxError("unknown variable \"" + v.var + "\"",filename,v);
			return null;
		}
						
		blk.add(new Code.IfGoto(Code.COP.EQ, lhs, Value.V_BOOL(true), target),
				v.attribute(Attribute.Source.class));			
		
		return blk;
	}

	protected Block resolveCondition(String target, BinOp v, int freeReg) {
		BOp bop = v.op;
		Block blk = new Block();

		if (bop == BOp.OR) {
			blk.addAll(resolveCondition(target, v.lhs, freeReg));
			blk.addAll(resolveCondition(target, v.rhs, freeReg));
			return blk;
		} else if (bop == BOp.AND) {
			String exitLabel = Block.freshLabel();
			blk.addAll(resolveCondition(exitLabel, invert(v.lhs), freeReg));
			blk.addAll(resolveCondition(target, v.rhs, freeReg));
			blk.add(new Code.Label(exitLabel));			
			return blk;
		} else if (bop == BOp.TYPEEQ || bop == BOp.TYPEIMPLIES) {
			return resolveTypeCondition(target, v, freeReg);
		}

		Pair<CExpr, Block> lhs_tb = resolve(freeReg, v.lhs);
		Pair<CExpr, Block> rhs_tb = resolve(freeReg + 1, v.rhs);
		blk.addAll(lhs_tb.second());
		blk.addAll(rhs_tb.second());

		if (bop == BOp.LT || bop == BOp.LTEQ || bop == BOp.GT
				|| bop == BOp.GTEQ) {
			blk.add(new Code.IfGoto(OP2COP(bop, v), lhs_tb.first(), rhs_tb
					.first(), target), v.attribute(Attribute.Source.class));
			return blk;
		} else if (bop == BOp.SUBSET || bop == BOp.SUBSETEQ) {
			blk.add(new Code.IfGoto(OP2COP(bop, v), lhs_tb.first(), rhs_tb
					.first(), target), v.attribute(Attribute.Source.class));
			return blk;
		} else if (bop == BOp.EQ || bop == BOp.NEQ) {
			blk.add(new Code.IfGoto(OP2COP(bop, v), lhs_tb.first(), rhs_tb
					.first(), target), v.attribute(Attribute.Source.class));
			return blk;
		} else if (bop == BOp.ELEMENTOF) {
			blk.add(new Code.IfGoto(OP2COP(bop, v), lhs_tb.first(), rhs_tb
					.first(), target), v.attribute(Attribute.Source.class));
			return blk;
		}

		syntaxError("expected boolean expression", filename, v);
		return null;
	}

	protected Block resolveTypeCondition(String target, BinOp v, int freeReg) {

		Pair<CExpr, Block> lhs_tb = resolve(freeReg, v.lhs);
		Type rhs_t = resolve(((Expr.TypeConst) v.rhs).type);
		
		Block blk = new Block();
		String exitLabel = Block.freshLabel();
		blk.addAll(lhs_tb.second());
		blk.add(new Code.IfGoto(Code.COP.NSUBTYPEEQ, lhs_tb.first(), Value
				.V_TYPE(rhs_t), exitLabel), v
				.attribute(Attribute.Source.class));
		
		blk.add(new Code.Goto(target));	
		blk.add(new Code.Label(exitLabel));
				
		return blk;
	}

	protected Block resolveCondition(String target, UnOp v, int freeReg) {
		UOp uop = v.op;
		switch (uop) {
		case NOT:
			String label = Block.freshLabel();
			Block blk = resolveCondition(label, v.mhs, freeReg);
			blk.add(new Code.Goto(target));
			blk.add(new Code.Label(label));
			return blk;
		}
		syntaxError("expected boolean expression", filename, v);
		return null;
	}

	protected Block resolveCondition(String target, ListAccess v, int freeReg) {
		Pair<CExpr, Block> la = resolve(freeReg, v);
		CExpr lhs = la.first();
		Block blk = la.second();
		blk.add(new Code.IfGoto(Code.COP.EQ, lhs, Value.V_BOOL(true), target),
				v.attribute(Attribute.Source.class));
		return blk;
	}

	protected Block resolveCondition(String target, RecordAccess v, int freeReg) {
		Pair<CExpr, Block> la = resolve(freeReg, v);
		CExpr lhs = la.first();
		Block blk = la.second();
		blk.add(new Code.IfGoto(Code.COP.EQ, lhs, Value.V_BOOL(true), target),
				v.attribute(Attribute.Source.class));
		return blk;
	}

	protected Block resolveCondition(String target, Invoke v, int freeReg) throws ResolveError {
		Pair<CExpr, Block> la = resolve(freeReg, v);
		CExpr lhs = la.first();
		Block blk = la.second();
		blk.add(new Code.IfGoto(Code.COP.EQ, lhs, Value.V_BOOL(true), target),
				v.attribute(Attribute.Source.class));
		return blk;
	}

	protected Block resolveCondition(String target, Comprehension e,
			int freeReg) {
		if (e.cop != Expr.COp.NONE && e.cop != Expr.COp.SOME) {
			syntaxError("expected boolean expression", filename, e);
		}
		
		Block blk = new Block();
		ArrayList<Pair<CExpr.Register, CExpr>> sources = new ArrayList();
		HashMap<String, CExpr> binding = new HashMap<String, CExpr>();
		for (Pair<String, Expr> src : e.sources) {
			Pair<CExpr, Block> r = resolve(freeReg, src.second());
			CExpr.Register reg = CExpr.REG(Type.T_ANY, freeReg++);
			sources.add(new Pair<CExpr.Register, CExpr>(reg, r.first()));
			binding.put(src.first(), reg);
			blk.addAll(r.second());			
		}

		ArrayList<String> labels = new ArrayList<String>();
		for (Pair<CExpr.Register, CExpr> ent : sources) {
			String loopLabel = Block.freshLabel();
			labels.add(loopLabel);
			blk
					.add(new Code.Forall(loopLabel, null, ent.first(), ent
							.second()), e.attribute(Attribute.Source.class));
		}
		if (e.cop == Expr.COp.NONE) {
			String exitLabel = Block.freshLabel();
			blk.addAll(resolveCondition(exitLabel, e.condition, freeReg));
			for (int i = (labels.size() - 1); i >= 0; --i) {
				blk.add(new Code.ForallEnd(labels.get(i)));
			}
			blk.add(new Code.Goto(target));
			blk.add(new Code.Label(exitLabel));
		} else { // SOME
			blk.addAll(resolveCondition(target, e.condition, freeReg));
			for (int i = (labels.size() - 1); i >= 0; --i) {
				blk.add(new Code.ForallEnd(labels.get(i)));
			}
		} // ALL, LONE and ONE will be harder

		// Finally, we need to substitute the block to rename all occurrences of
		// the quantified variables to be their actual registers.
		blk = Block.substitute(binding, blk);

		return blk;
	}

	/**
	 * Translate an expression in the context of a given type environment. The
	 * "freeReg" --- free register --- identifies the first free register for
	 * use as temporary storage. An expression differs from a statement in that
	 * it may consume a register as part of the translation. Thus, compound
	 * expressions, such as binop, will save the freeReg of one expression from
	 * being used when translating a subsequent expression.
	 * 
	 * @param freeReg
	 * @param e
	 * @param environment
	 * @return
	 */
	protected Pair<CExpr, Block> resolve(int freeReg, Expr e) {
		try {
			if (e instanceof Constant) {
				return resolve(freeReg, (Constant) e);
			} else if (e instanceof Variable) {
				return resolve(freeReg, (Variable) e);
			} else if (e instanceof NaryOp) {
				return resolve(freeReg, (NaryOp) e);
			} else if (e instanceof BinOp) {
				return resolve(freeReg, (BinOp) e);
			} else if (e instanceof ListAccess) {
				return resolve(freeReg, (ListAccess) e);
			} else if (e instanceof UnOp) {
				return resolve(freeReg, (UnOp) e);
			} else if (e instanceof Invoke) {
				return resolve(freeReg, (Invoke) e);
			} else if (e instanceof Comprehension) {
				return resolve(freeReg, (Comprehension) e);
			} else if (e instanceof RecordAccess) {
				return resolve(freeReg, (RecordAccess) e);
			} else if (e instanceof RecordGen) {
				return resolve(freeReg, (RecordGen) e);
			} else if (e instanceof TupleGen) {
				return resolve(freeReg, (TupleGen) e);
			} else if (e instanceof DictionaryGen) {
				return resolve(freeReg, (DictionaryGen) e);
			} else if (e instanceof FunConst) {
				return resolve(freeReg, (FunConst) e);
			} else {
				syntaxError("unknown expression encountered: "
						+ e.getClass().getName(), filename, e);
			}
		} catch (SyntaxError se) {
			throw se;
		} catch (Exception ex) {
			syntaxError("internal failure", filename, e, ex);
		}

		return null;
	}

	protected Pair<CExpr, Block> resolve(int freeReg, Invoke s) throws ResolveError {
		List<Expr> args = s.arguments;
		Block blk = new Block();

		int idx = freeReg;
		ArrayList<CExpr> nargs = new ArrayList<CExpr>();
		CExpr receiver = null;
		
		if (s.receiver != null) {
			Pair<CExpr, Block> tb = resolve(idx++, s.receiver);
			receiver = tb.first();
			blk.addAll(tb.second());
		}

		for (Expr e : args) {
			Pair<CExpr, Block> e_tb = resolve(idx++, e);
			nargs.add(e_tb.first());
			blk.addAll(e_tb.second());
		}	
		
		Attributes.Module modInfo = s.attribute(Attributes.Module.class);
		if(modInfo != null) {
			NameID name = new NameID(modInfo.module, s.name);

			return new Pair<CExpr, Block>(CExpr.DIRECTINVOKE(
					Type.T_FUN(null, Type.T_ANY), name, 0, receiver, nargs), blk);
		} else {
			// FIXME: need some way to support non-variable targets.
			CExpr target = CExpr.VAR(Type.T_ANY,s.name);
			return new Pair<CExpr, Block>(CExpr.INDIRECTINVOKE(target,
					receiver, nargs), blk);	
		}
	}

	protected Pair<CExpr, Block> resolve(int freeReg, Constant c) {
		return new Pair<CExpr, Block>(c.value, new Block());
	}

	protected Pair<CExpr, Block> resolve(int freeReg, FunConst s) {
		Attributes.Module modInfo = s.attribute(Attributes.Module.class);		
		NameID name = new NameID(modInfo.module, s.name);		
		ArrayList<Type> paramTypes = new ArrayList<Type>();
		for(UnresolvedType p : s.paramTypes) {
			paramTypes.add(resolve(p));
		}
		Type.Fun tf = Type.T_FUN(null, Type.T_ANY, paramTypes);
		
		return new Pair<CExpr, Block>(Value.V_FUN(
				name, tf), new Block());
	}
	
	protected Pair<CExpr, Block> resolve(int target, Variable v) throws ResolveError {
		// First, check if this is an alias or not				
		
		Attributes.Alias alias = v.attribute(Attributes.Alias.class);
		if (alias != null) {
			// Must be a local variable	
			if(alias.alias == null) {				
				return new Pair<CExpr, Block>(CExpr.VAR(Type.T_ANY, v.var), new Block());
			} else {								
				return resolve(0, alias.alias);
			}
		}
		
		if(currentFunDecl != null) {
			Type.Fun tf = currentFunDecl.attribute(Attributes.Fun.class).type;

			// Second, see if it's a field of the receiver
			if(tf.receiver != null) {
				Type pt = tf.receiver;
				if(pt instanceof Type.Named) {
					pt = ((Type.Named)pt).type;
				}
				if(pt instanceof Type.Process) {
					Type.Record ert = Type.effectiveRecordType(((Type.Process)pt).element);
					if(ert != null && ert.types.containsKey(v.var)) {						
						// Bingo, this is an implicit field dereference
						CExpr thiz = CExpr.UNOP(CExpr.UOP.PROCESSACCESS, CExpr.VAR(
								Type.T_ANY, "this"));					
						CExpr.RecordAccess ra = CExpr.RECORDACCESS(thiz, v.var);
						return new Pair<CExpr,Block>(ra, new Block());
					}
				}
			}
		}
		
		// Third, see if it's a constant
		Attributes.Module mod = v.attribute(Attributes.Module.class);
		if (mod != null) {
			NameID name = new NameID(mod.module, v.var);
			Value val = constants.get(name);
			if (val == null) {
				// indicates a non-local constant definition
				Module mi = loader.loadModule(mod.module);
				val = mi.constant(v.var).constant();
			}
			return new Pair<CExpr, Block>(val, new Block());
		}
				
		// must be an error
		syntaxError("unknown variable \"" + v.var + "\"",filename,v);
		return null;
	}

	protected Pair<CExpr, Block> resolve(int freeReg, UnOp v) {
		Pair<CExpr, Block> mhs = resolve(freeReg, v.mhs);
		Block blk = mhs.second();
		switch (v.op) {
		case NEG:
			return new Pair<CExpr, Block>(CExpr
					.UNOP(CExpr.UOP.NEG, mhs.first()), blk);
		case NOT:
			String falseLabel = Block.freshLabel();
			String exitLabel = Block.freshLabel();
			// Ok, so there's a bit of inefficiency here ... so what?
			blk = resolveCondition(falseLabel, v.mhs, freeReg);
			blk.add(new Code.Assign(CExpr.REG(Type.T_BOOL, freeReg), Value
					.V_BOOL(true)), v.attribute(Attribute.Source.class));
			blk.add(new Code.Goto(exitLabel));
			blk.add(new Code.Label(falseLabel));
			blk.add(new Code.Assign(CExpr.REG(Type.T_BOOL, freeReg), Value
					.V_BOOL(false)), v.attribute(Attribute.Source.class));
			blk.add(new Code.Label(exitLabel));
			return new Pair<CExpr, Block>(CExpr.REG(Type.T_BOOL, freeReg), blk);
		case LENGTHOF:
			return new Pair<CExpr, Block>(CExpr.UNOP(CExpr.UOP.LENGTHOF, mhs
					.first()), blk);
		case PROCESSACCESS:			
			return new Pair<CExpr, Block>(CExpr.UNOP(CExpr.UOP.PROCESSACCESS,
					mhs.first()), blk);
		case PROCESSSPAWN:
			return new Pair<CExpr, Block>(CExpr.UNOP(CExpr.UOP.PROCESSSPAWN,
					mhs.first()), blk);
		default:
			syntaxError("unexpected unary operator encountered", filename, v);
			return null;
		}
	}

	protected Pair<CExpr, Block> resolve(int freeReg, ListAccess v) {
		Pair<CExpr, Block> lhs_tb = resolve(freeReg, v.src);
		Pair<CExpr, Block> rhs_tb = resolve(freeReg + 1, v.index);
		Block blk = new Block();
		blk.addAll(lhs_tb.second());
		blk.addAll(rhs_tb.second());
		return new Pair<CExpr, Block>(CExpr.LISTACCESS(lhs_tb.first(), rhs_tb
				.first()), blk);
	}

	protected Pair<CExpr, Block> resolve(int freeReg, BinOp v) {

		// could probably use a range test for this somehow
		if (v.op == BOp.EQ || v.op == BOp.NEQ || v.op == BOp.LT
				|| v.op == BOp.LTEQ || v.op == BOp.GT || v.op == BOp.GTEQ
				|| v.op == BOp.SUBSET || v.op == BOp.SUBSETEQ
				|| v.op == BOp.ELEMENTOF || v.op == BOp.AND || v.op == BOp.OR) {
			String trueLabel = Block.freshLabel();
			String exitLabel = Block.freshLabel();
			Block blk = resolveCondition(trueLabel, v, freeReg);
			blk.add(new Code.Assign(CExpr.REG(Type.T_BOOL, freeReg), Value
					.V_BOOL(false)), v.attribute(Attribute.Source.class));
			blk.add(new Code.Goto(exitLabel));
			blk.add(new Code.Label(trueLabel));
			blk.add(new Code.Assign(CExpr.REG(Type.T_BOOL, freeReg), Value
					.V_BOOL(true)), v.attribute(Attribute.Source.class));
			blk.add(new Code.Label(exitLabel));			
			return new Pair<CExpr, Block>(CExpr.REG(Type.T_BOOL, freeReg), blk);
		}

		Pair<CExpr, Block> lhs_tb = resolve(freeReg, v.lhs);
		Pair<CExpr, Block> rhs_tb = resolve(freeReg + 1, v.rhs);
		BOp bop = v.op;

		Block blk = new Block();
		blk.addAll(lhs_tb.second());
		blk.addAll(rhs_tb.second());

		if (bop == BOp.ADD || bop == BOp.SUB || bop == BOp.MUL
				|| bop == BOp.DIV) {
			CExpr r = CExpr.BINOP(OP2BOP(bop, v), lhs_tb.first(), rhs_tb
					.first());
			return new Pair<CExpr, Block>(r, blk);			
		} else if (bop == BOp.UNION || bop == BOp.INTERSECTION) {
			CExpr r = CExpr.BINOP(OP2BOP(bop, v), lhs_tb.first(), rhs_tb
					.first());
			return new Pair<CExpr, Block>(r, blk);
		}

		syntaxError("unknown binary operation encountered", filename, v);
		return null;
	}

	protected Pair<CExpr, Block> resolve(int freeReg, NaryOp v) {
		Block blk = new Block();
		if (v.nop == NOp.SUBLIST) {
			if (v.arguments.size() != 3) {
				syntaxError("incorrect number of arguments", filename, v);
			}
			Pair<CExpr, Block> src = resolve(freeReg, v.arguments.get(0));
			Pair<CExpr, Block> start = resolve(freeReg + 1, v.arguments.get(1));
			Pair<CExpr, Block> end = resolve(freeReg + 2, v.arguments.get(2));
			blk.addAll(src.second());
			blk.addAll(start.second());
			blk.addAll(end.second());
			CExpr r = CExpr.NARYOP(CExpr.NOP.SUBLIST, src.first(), start
					.first(), end.first());
			return new Pair<CExpr, Block>(r, blk);
		} else {
			int idx = freeReg;
			ArrayList<CExpr> args = new ArrayList<CExpr>();
			for (Expr e : v.arguments) {
				Pair<CExpr, Block> t = resolve(idx++, e);
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

	protected Pair<CExpr, Block> resolve(int freeReg, Comprehension e) {

		// First, check for boolean cases which are handled mostly by
		// resolveCondition.
		if (e.cop == Expr.COp.SOME || e.cop == Expr.COp.NONE) {
			String trueLabel = Block.freshLabel();
			String exitLabel = Block.freshLabel();
			Block blk = resolveCondition(trueLabel, e, freeReg);
			blk.add(new Code.Assign(CExpr.REG(Type.T_BOOL, freeReg), Value
					.V_BOOL(false)), e.attribute(Attribute.Source.class));
			blk.add(new Code.Goto(exitLabel));
			blk.add(new Code.Label(trueLabel));
			blk.add(new Code.Assign(CExpr.REG(Type.T_BOOL, freeReg), Value
					.V_BOOL(true)), e.attribute(Attribute.Source.class));
			blk.add(new Code.Label(exitLabel));
			return new Pair<CExpr, Block>(CExpr.REG(Type.T_BOOL, freeReg), blk);
		}

		// Ok, non-boolean case.		
		ArrayList<Pair<CExpr.Register, CExpr>> sources = new ArrayList();
		Block blk = new Block();
		HashMap<String, CExpr> binding = new HashMap<String, CExpr>();
		for (Pair<String, Expr> src : e.sources) {
			Pair<CExpr, Block> r = resolve(0, src.second());
			CExpr.Register reg = CExpr.REG(Type.T_ANY, freeReg++);
			sources.add(new Pair<CExpr.Register, CExpr>(reg, r.first()));
			binding.put(src.first(), reg);
			blk.addAll(r.second());			
		}

		Pair<CExpr, Block> value = resolve(freeReg + 1, e.value);
		Type type = value.first().type();
		
		CExpr.Register lhs;

		if (e.cop == Expr.COp.LISTCOMP) {
			lhs = CExpr.REG(Type.T_LIST(type), freeReg);
			blk.add(new Code.Assign(lhs, CExpr.NARYOP(CExpr.NOP.LISTGEN)), e
					.attribute(Attribute.Source.class));
		} else {
			lhs = CExpr.REG(Type.T_SET(type), freeReg);
			blk.add(new Code.Assign(lhs, CExpr.NARYOP(CExpr.NOP.SETGEN)), e
					.attribute(Attribute.Source.class));
		}

		Block loopInvariant = null;
		
		// At this point, it would be good to determine an appropriate loop
		// invariant for a set comprehension. This is easy enough in the case of
		// a single variable comprehension, but actually rather difficult for a
		// multi-variable comprehension.
		//
		// For example, consider <code>{x+y | x in xs, y in ys, x<0 && y<0}</code>
		// 
		// What is an appropriate loop invariant here?
		
		String continueLabel = Block.freshLabel();
		ArrayList<String> labels = new ArrayList<String>();
		for (Pair<CExpr.Register, CExpr> ent : sources) {
			String loopLabel = Block.freshLabel();
			labels.add(loopLabel);

			blk
					.add(new Code.Forall(loopLabel, loopInvariant, ent.first(), ent
							.second()), e.attribute(Attribute.Source.class));
		}
		
		if (e.condition != null) {
			blk.addAll(resolveCondition(continueLabel, invert(e.condition),
					freeReg));
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

		for (int i = (labels.size() - 1); i >= 0; --i) {
			blk.add(new Code.ForallEnd(labels.get(i)));
		}

		// Finally, we need to substitute the block to rename all occurrences of
		// the quantified variables to be their actual registers.
		blk = Block.substitute(binding, blk);

		return new Pair<CExpr, Block>(lhs, blk);
	}

	protected Pair<CExpr, Block> resolve(int freeReg, RecordGen sg) {
		HashMap<String, CExpr> values = new HashMap<String, CExpr>();
		Block blk = new Block();
		for (Map.Entry<String, Expr> e : sg.fields.entrySet()) {
			Pair<CExpr, Block> tb = resolve(freeReg, e.getValue());
			values.put(e.getKey(), tb.first());
			blk.addAll(tb.second());
		}
		return new Pair<CExpr, Block>(CExpr.RECORD(values), blk);
	}

	protected Pair<CExpr, Block> resolve(int freeReg, TupleGen sg) {
		HashMap<String, CExpr> values = new HashMap<String, CExpr>();
		Block blk = new Block();
		int idx=0;
		for (Expr e : sg.fields) {
			String name = "$" + idx++;
			Pair<CExpr, Block> tb = resolve(freeReg, e	);
			values.put(name, tb.first());
			blk.addAll(tb.second());
		}
		return new Pair<CExpr, Block>(CExpr.RECORD(values), blk);
	}

	protected Pair<CExpr, Block> resolve(int freeReg, DictionaryGen sg) {
		HashSet<Pair<CExpr, CExpr>> values = new HashSet<Pair<CExpr, CExpr>>();
		Block blk = new Block();		
		for (Pair<Expr,Expr> e : sg.pairs) {			
			Pair<CExpr, Block> kb = resolve(freeReg, e.first());
			Pair<CExpr, Block> vb = resolve(freeReg, e.second());
			values.add(new Pair<CExpr,CExpr>(kb.first(),vb.first()));
			blk.addAll(kb.second());
			blk.addAll(vb.second());
		}
		return new Pair<CExpr, Block>(CExpr.DICTIONARY(values), blk);
	}
	
	protected Pair<CExpr, Block> resolve(int freeReg, RecordAccess sg) {
		Pair<CExpr, Block> lhs = resolve(freeReg, sg.lhs);		
		return new Pair<CExpr, Block>(CExpr.RECORDACCESS(lhs.first(), sg.name),
				lhs.second());
	}

	protected Type resolve(UnresolvedType t) {
		if (t instanceof UnresolvedType.Any) {
			return Type.T_ANY;
		} else if (t instanceof UnresolvedType.Existential) {
			return Type.T_EXISTENTIAL;
		} else if (t instanceof UnresolvedType.Void) {
			return Type.T_VOID;
		} else if (t instanceof UnresolvedType.Null) {
			return Type.T_NULL;
		} else if (t instanceof UnresolvedType.Bool) {
			return Type.T_BOOL;
		} else if (t instanceof UnresolvedType.Int) {
			return Type.T_INT;
		} else if (t instanceof UnresolvedType.Real) {
			return Type.T_REAL;
		} else if (t instanceof UnresolvedType.List) {
			UnresolvedType.List lt = (UnresolvedType.List) t;			
			return Type.T_LIST(resolve(lt.element));			
		} else if (t instanceof UnresolvedType.Set) {
			UnresolvedType.Set st = (UnresolvedType.Set) t;			
			return Type.T_SET(resolve(st.element));			
		} else if (t instanceof UnresolvedType.Dictionary) {
			UnresolvedType.Dictionary st = (UnresolvedType.Dictionary) t;			
			return Type.T_DICTIONARY(resolve(st.key),resolve(st.value));					
		} else if (t instanceof UnresolvedType.Tuple) {
			// At the moment, a tuple is compiled down to a wyil record.
			UnresolvedType.Tuple tt = (UnresolvedType.Tuple) t;
			HashMap<String,Type> types = new HashMap<String,Type>();			
			int idx=0;
			for (UnresolvedType e : tt.types) {
				String name = "$" + idx++;
				types.put(name, resolve(e));				
			}
			return Type.T_RECORD(types);			
		} else if (t instanceof UnresolvedType.Record) {		
			UnresolvedType.Record tt = (UnresolvedType.Record) t;
			HashMap<String, Type> types = new HashMap<String, Type>();			
			for (Map.Entry<String, UnresolvedType> e : tt.types.entrySet()) {
				types.put(e.getKey(), resolve(e.getValue()));				
			}
			return Type.T_RECORD(types);
		} else if (t instanceof UnresolvedType.Named) {
			UnresolvedType.Named dt = (UnresolvedType.Named) t;			
			ModuleID mid = dt.attribute(Attributes.Module.class).module;
			if (modules.contains(mid)) {
				return types.get(new NameID(mid, dt.name));								
			} else {
				try {
					Module mi = loader.loadModule(mid);
					Module.TypeDef td = mi.type(dt.name);
					return td.type();
				} catch (ResolveError rex) {
					syntaxError(rex.getMessage(), filename, t, rex);
					return null;
				}
			}
		} else if (t instanceof UnresolvedType.Union) {
			UnresolvedType.Union ut = (UnresolvedType.Union) t;
			HashSet<Type.NonUnion> bounds = new HashSet<Type.NonUnion>();			
			for (UnresolvedType b : ut.bounds) {				
				Type bt = resolve(b);
				if (bt instanceof Type.NonUnion) {
					bounds.add((Type.NonUnion) bt);
				} else {
					bounds.addAll(((Type.Union) bt).bounds);
				}			
			}

			Type type;
			if (bounds.size() == 1) {
				return bounds.iterator().next();
			} else {
				return Type.leastUpperBound(bounds);
			}			
		} else if(t instanceof UnresolvedType.Process) {
			UnresolvedType.Process ut = (UnresolvedType.Process) t;			
			return Type.T_PROCESS(resolve(ut.element));							
		} else {
			UnresolvedType.Fun ut = (UnresolvedType.Fun) t;			
			ArrayList<Type> paramTypes = new ArrayList<Type>();
			for(UnresolvedType p : ut.paramTypes) {
				paramTypes.add(resolve(p));
			}
			// FIXME: need to add support for receiver types
			return Type.T_FUN(null,resolve(ut.ret),paramTypes);							
		}
	}
	
	public Variable flattern(Expr e) {
		if (e instanceof Variable) {
			return (Variable) e;
		} else if (e instanceof ListAccess) {
			ListAccess la = (ListAccess) e;
			return flattern(la.src);
		} else if (e instanceof RecordAccess) {
			RecordAccess la = (RecordAccess) e;
			return flattern(la.lhs);
		} else if (e instanceof UnOp) {
			UnOp la = (UnOp) e;
			if (la.op == Expr.UOp.PROCESSACCESS) {
				return flattern(la.mhs);
			}
		}
		syntaxError("invalid lval", filename, e);
		return null;
	}

	public static Expr invert(Expr e) {
		if (e instanceof Expr.BinOp) {
			BinOp bop = (BinOp) e;
			switch (bop.op) {
			case AND:
				return new BinOp(BOp.OR, invert(bop.lhs), invert(bop.rhs), e
						.attributes());
			case OR:
				return new BinOp(BOp.AND, invert(bop.lhs), invert(bop.rhs), e
						.attributes());
			case EQ:
				return new BinOp(BOp.NEQ, bop.lhs, bop.rhs, e.attributes());
			case NEQ:
				return new BinOp(BOp.EQ, bop.lhs, bop.rhs, e.attributes());
			case LT:
				return new BinOp(BOp.GTEQ, bop.lhs, bop.rhs, e.attributes());
			case LTEQ:
				return new BinOp(BOp.GT, bop.lhs, bop.rhs, e.attributes());
			case GT:
				return new BinOp(BOp.LTEQ, bop.lhs, bop.rhs, e.attributes());
			case GTEQ:
				return new BinOp(BOp.LT, bop.lhs, bop.rhs, e.attributes());
			}
		} else if (e instanceof Expr.UnOp) {
			UnOp uop = (UnOp) e;
			switch (uop.op) {
			case NOT:
				return uop.mhs;
			}
		}
		return new Expr.UnOp(Expr.UOp.NOT, e);
	}

	public CExpr.BOP OP2BOP(Expr.BOp bop, SyntacticElement elem) {
		switch (bop) {
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
		syntaxError("unrecognised binary operation", filename, elem);
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
		syntaxError("unrecognised binary operation", filename, elem);
		return null;
	}

	protected <T extends Type> T checkType(Type t, Class<T> clazz,
			SyntacticElement elem) {
		if (t instanceof Type.Named) {
			t = ((Type.Named) t).type;
		}
		if (clazz.isInstance(t)) {
			return (T) t;
		} else {
			syntaxError("expected type " + clazz.getName() + ", found " + t,
					filename, elem);
			return null;
		}
	}
	
	protected <T extends Scope> T findEnclosingScope(Class<T> c) {
		for(int i=scopes.size()-1;i>=0;--i) {
			Scope s = scopes.get(i);
			if(c.isInstance(s)) {
				return (T) s;
			}
		}
		return null;
	}	
	
	public abstract class Scope {}
	
	public class BreakScope extends Scope {
		public String label;
		public BreakScope(String l) { label = l; }
	}

	public class ContinueScope extends Scope {
		public String label;
		public ContinueScope(String l) { label = l; }
	}
}
