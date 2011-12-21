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
import wyc.lang.*;
import wyc.lang.WhileyFile.*;
import wyc.lang.Stmt;
import wyc.lang.Stmt.*;

/**
 * <p>
 * Responsible for expanding all types and constraints for a given module(s), as
 * well as generating appropriate WYIL code. For example, consider these two
 * declarations:
 * </p>
 * 
 * <pre>
 * define Point2D as {int x, int y}
 * define Point3D as {int x, int y, int z}
 * define Point as Point2D | Point3D
 * </pre>
 * <p>
 * This stage will expand the type <code>Point</code> to give its full
 * structural definition. That is,
 * <code>{int x,int y}|{int x,int y,int z}</code>.
 * </p>
 * <p>
 * Type expansion must also account for any constraints on the types in
 * question. For example:
 * </p>
 * 
 * <pre>
 * define nat as int where $ >= 0
 * define natlist as [nat]
 * </pre>
 * <p>
 * The type <code>natlist</code> expands to <code>[int]</code>, whilst its
 * constraint is expanded to <code>all {x in $ | x >= 0}</code>.
 * </p>
 * <p>
 * <b>NOTE:</b> As the above description hints, this class currently has two
 * distinct responsibilities. Therefore, at some point in the future, it will be
 * split into two separate stages.
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public final class CodeGeneration {
	private final ModuleLoader loader;	
	private HashSet<ModuleID> modules;	
	private Stack<Scope> scopes = new Stack<Scope>();
	private String filename;	
	private FunDecl currentFunDecl;

	// The shadow set is used to (efficiently) aid the correct generation of
	// runtime checks for post conditions. The key issue is that a post
	// condition may refer to parameters of the method. However, if those
	// parameters are modified during the method, then we must store their
	// original value on entry for use in the post-condition runtime check.
	// These stored values are called "shadows".
	private final HashMap<String, Integer> shadows = new HashMap<String, Integer>();

	public CodeGeneration(ModuleLoader loader) {
		this.loader = loader;		
	}

	public Module generate(WhileyFile wf) {
		this.filename = wf.filename;
				
		HashMap<Pair<Type.Function, String>, Module.Method> methods = new HashMap();
		ArrayList<Module.TypeDef> types = new ArrayList<Module.TypeDef>();
		ArrayList<Module.ConstDef> constants = new ArrayList<Module.ConstDef>();
		
		for (WhileyFile.Decl d : wf.declarations) {
			try {
				if (d instanceof TypeDecl) {
					types.add(generate((TypeDecl) d, wf.module));
				} else if (d instanceof ConstDecl) {
					constants.add(generate((ConstDecl) d, wf.module));
				} else if (d instanceof FunDecl) {
					Module.Method mi = generate((FunDecl) d);
					Pair<Type.Function, String> key = new Pair(mi.type(), mi.name());
					Module.Method method = methods.get(key);
					if (method != null) {
						// coalesce cases
						ArrayList<Module.Case> ncases = new ArrayList<Module.Case>(
								method.cases());
						ncases.addAll(mi.cases());
						mi = new Module.Method(method.modifiers(), mi.name(),
								mi.type(), ncases);
					}
					methods.put(key, mi);
				}
			} catch (SyntaxError se) {
				throw se;
			} catch (Throwable ex) {
				internalFailure("internal failure", wf.filename, d, ex);
			}
		}
		
		return new Module(wf.module, wf.filename, methods.values(), types,
				constants);				
	}

	private Module.ConstDef generate(ConstDecl td, ModuleID module) {
		return new Module.ConstDef(td.modifiers, td.name, td.value);
	}

	private Module.TypeDef generate(TypeDecl td, ModuleID module) {		
		Block constraint = null;
		if(td.constraint != null) {
			HashMap<String,Integer> environment = new HashMap<String,Integer>();
			environment.put("$",0);
			String lab = Block.freshLabel();
			constraint = generateCondition(lab, td.constraint, environment);		
			constraint.append(Code.Fail("precondition not satisfied"), attributes(td.constraint));
			constraint.append(Code.Label(lab));
		}
		
		// FIXME: include the declared nominal type as an attribute?
		return new Module.TypeDef(td.modifiers, td.name(), td.rawType, constraint);
	}

	private Module.Method generate(FunDecl fd) {		
		HashMap<String,Integer> environment = new HashMap<String,Integer>();
		
		// method return type		
		int paramIndex = 0;
		int nparams = fd.parameters.size();
		// method receiver type (if applicable)
		if (fd instanceof MethDecl) {
			MethDecl md = (MethDecl) fd;
			if(md.receiver != null) {						
				// TODO: fix receiver constraints
				environment.put("this", paramIndex++);	
				nparams++;
			}
		}
		
		// ==================================================================
		// Generate pre-condition
		// ==================================================================
		
		for (WhileyFile.Parameter p : fd.parameters) {			
			// First, generate and inline any constraints associated with the type.	
			// Now, map the parameter to its index
			environment.put(p.name(),paramIndex++);
		}		
		
		// Resolve pre- and post-condition								
		Block precondition = null;		
		if(fd.precondition != null) {			
			precondition = new Block(nparams);				
			String lab = Block.freshLabel();
			HashMap<String,Integer> preEnv = new HashMap<String,Integer>(environment);						
			precondition.append(generateCondition(lab, fd.precondition, preEnv));		
			precondition.append(Code.Fail("precondition not satisfied"), attributes(fd.precondition));
			precondition.append(Code.Label(lab));			
		}
		
		// ==================================================================
		// Generate post-condition
		// ==================================================================
		Block postcondition = null;		
						
		if (fd.postcondition != null) {
			HashMap<String,Integer> postEnv = new HashMap<String,Integer>();
			postEnv.put("$", 0);
			for(String var : environment.keySet()) {
				postEnv.put(var, environment.get(var)+1);
			}
			String lab = Block.freshLabel();
			postcondition = new Block(postEnv.size());
			postcondition.append(generateCondition(lab, fd.postcondition,
					postEnv));
			postcondition.append(Code.Fail("postcondition not satisfied"),
					attributes(fd.postcondition));
			postcondition.append(Code.Label(lab));
		}
		
		// ==================================================================
		// Generate body
		// ==================================================================
		currentFunDecl = fd;
			
		Block body = new Block(environment.size());		
		for (Stmt s : fd.statements) {
			body.append(generate(s, environment));
		}

		currentFunDecl = null;
		
		// The following is sneaky. It guarantees that every method ends in a
		// return. For methods that actually need a value, this is either
		// removed as dead-code or remains and will cause an error.
		body.append(Code.Return(Type.T_VOID),attributes(fd));		
		
		List<Module.Case> ncases = new ArrayList<Module.Case>();				
		ArrayList<String> locals = new ArrayList<String>();
		
		for(int i=0;i!=environment.size();++i) {
			locals.add(null);
		}
		
		for(Map.Entry<String,Integer> e : environment.entrySet()) {
			locals.set(e.getValue(),e.getKey());
		}	
		
		ncases.add(new Module.Case(body,precondition,postcondition,locals));
		return new Module.Method(fd.modifiers, fd.name(), fd.rawType, ncases);
	}

	/**
	 * Translate a source-level statement into a wyil block, using a given
	 * environment mapping named variables to slots.
	 * 
	 * @param stmt
	 *            --- statement to be translated.
	 * @param environment
	 *            --- mapping from variable names to to slot numbers.
	 * @return
	 */
	private Block generate(Stmt stmt, HashMap<String,Integer> environment) {
		try {
			if (stmt instanceof Assign) {
				return generate((Assign) stmt, environment);
			} else if (stmt instanceof Assert) {
				return generate((Assert) stmt, environment);
			} else if (stmt instanceof Return) {
				return generate((Return) stmt, environment);
			} else if (stmt instanceof Debug) {
				return generate((Debug) stmt, environment);
			} else if (stmt instanceof IfElse) {
				return generate((IfElse) stmt, environment);
			} else if (stmt instanceof Switch) {
				return generate((Switch) stmt, environment);
			} else if (stmt instanceof TryCatch) {
				return generate((TryCatch) stmt, environment);
			} else if (stmt instanceof Break) {
				return generate((Break) stmt, environment);
			} else if (stmt instanceof Throw) {
				return generate((Throw) stmt, environment);
			} else if (stmt instanceof While) {
				return generate((While) stmt, environment);
			} else if (stmt instanceof DoWhile) {
				return generate((DoWhile) stmt, environment);
			} else if (stmt instanceof For) {
				return generate((For) stmt, environment);
			} else if (stmt instanceof Expr.MessageSend) {
				return generate((Expr.MessageSend) stmt,false,environment);								
			} else if (stmt instanceof Expr.MethodCall) {
				return generate((Expr.MethodCall) stmt,false,environment);								
			} else if (stmt instanceof Expr.FunctionCall) {
				return generate((Expr.FunctionCall) stmt,false,environment);								
			} else if (stmt instanceof Expr.Spawn) {
				return generate((Expr.UnOp) stmt, environment);
			} else if (stmt instanceof Skip) {
				return generate((Skip) stmt, environment);
			} else {
				// should be dead-code
				internalFailure("unknown statement encountered: "
						+ stmt.getClass().getName(), filename, stmt);
			}
		} catch (ResolveError rex) {
			syntaxError(rex.getMessage(), filename, stmt, rex);
		} catch (SyntaxError sex) {
			throw sex;
		} catch (Exception ex) {			
			internalFailure("internal failure", filename, stmt, ex);
		}
		return null;
	}
	
	private Block generate(Assign s, HashMap<String,Integer> environment) {
		Block blk = null;
		
		if(s.lhs instanceof Expr.LocalVariable) {			
			blk = generate(s.rhs, environment);			
			Expr.LocalVariable v = (Expr.LocalVariable) s.lhs;
			blk.append(Code.Store(v.rawType(), allocate(v.var, environment)),
					attributes(s));			
		} else if(s.lhs instanceof Expr.Tuple) {					
			Expr.Tuple tg = (Expr.Tuple) s.lhs;
			blk = generate(s.rhs, environment);			
			blk.append(Code.Destructure(null),attributes(s));
			ArrayList<Expr> fields = new ArrayList<Expr>(tg.fields);
			Collections.reverse(fields);
			
			for(Expr e : fields) {
				if(!(e instanceof Expr.LocalVariable)) {
					syntaxError(errorMessage(INVALID_TUPLE_LVAL),filename,e);
				}
				Expr.LocalVariable v = (Expr.LocalVariable) e;
				blk.append(Code.Store(null, allocate(v.var, environment)),
						attributes(s));				
			}
			return blk;
		} else if (s.lhs instanceof Expr.ListAccess
				|| s.lhs instanceof Expr.StringAccess
				|| s.lhs instanceof Expr.DictionaryAccess
				|| s.lhs instanceof Expr.RecordAccess) {
			// this is where we need a multistore operation						
			ArrayList<String> fields = new ArrayList<String>();
			blk = new Block(environment.size());
			Pair<Expr.AssignedVariable,Integer> l = extractLVal(s.lhs,fields,blk,environment);
			Expr.AssignedVariable lhs = l.first();
			if(!environment.containsKey(lhs.var)) {
				syntaxError("unknown variable",filename,l.first());
			}
			int slot = environment.get(lhs.var);
			blk.append(generate(s.rhs, environment));		
			blk.append(Code.Update(lhs.rawType,lhs.rawAfterType,slot,l.second(),fields),
					attributes(s));							
		} else {
			syntaxError("invalid assignment", filename, s);
		}
		
		return blk;
	}

	private Pair<Expr.AssignedVariable, Integer> extractLVal(Expr e,
			ArrayList<String> fields, Block blk, 
			HashMap<String, Integer> environment) {
		if (e instanceof Expr.AssignedVariable) {
			Expr.AssignedVariable v = (Expr.AssignedVariable) e;
			return new Pair(v,0);			
		} else if (e instanceof Expr.AbstractIndexAccess) {
			Expr.AbstractIndexAccess la = (Expr.AbstractIndexAccess) e;
			Pair<Expr.AssignedVariable,Integer> l = extractLVal(la.src, fields, blk, environment);
			blk.append(generate(la.index, environment));			
			return new Pair(l.first(),l.second() + 1);
		} else if (e instanceof Expr.RecordAccess) {
			Expr.RecordAccess ra = (Expr.RecordAccess) e;
			Pair<Expr.AssignedVariable,Integer> l = extractLVal(ra.src, fields, blk, environment);
			fields.add(ra.name);
			return new Pair(l.first(),l.second() + 1);			
		} else {
			syntaxError(errorMessage(INVALID_LVAL_EXPRESSION), filename, e);
			return null; // dead code
		}
	}
	
	private Block generate(Assert s, HashMap<String,Integer> environment) {
		String lab = Block.freshLabel();
		Block blk = new Block(environment.size());
		blk.append(Code.Assert(lab),attributes(s));
		blk.append(generateCondition(lab, s.expr, environment));		
		blk.append(Code.Fail("assertion failed"), attributes(s));
		blk.append(Code.Label(lab));			
		return blk;
	}

	private Block generate(Return s, HashMap<String,Integer> environment) {

		if (s.expr != null) {
			Block blk = generate(s.expr, environment);
	
			// Here, we don't put the type propagated for the return expression.
			// Instead, we use the declared return type of this function. This
			// has the effect of forcing an implicit coercion between the
			// actual value being returned and its required type. 
			
			Type ret = currentFunDecl.rawType.ret();
			blk.append(Code.Return(ret), attributes(s));
			return blk;
		} else {
			Block blk = new Block(environment.size());
			blk.append(Code.Return(Type.T_VOID), attributes(s));
			return blk;
		}
	}

	private Block generate(Skip s, HashMap<String,Integer> environment) {
		Block blk = new Block(environment.size());
		blk.append(Code.Skip, attributes(s));
		return blk;
	}

	private Block generate(Debug s, HashMap<String,Integer> environment) {		
		Block blk = generate(s.expr, environment);		
		blk.append(Code.debug, attributes(s));
		return blk;
	}

	private Block generate(IfElse s, HashMap<String,Integer> environment) {
		String falseLab = Block.freshLabel();
		String exitLab = s.falseBranch.isEmpty() ? falseLab : Block
				.freshLabel();
		Block blk = generateCondition(falseLab, invert(s.condition), environment);

		for (Stmt st : s.trueBranch) {
			blk.append(generate(st, environment));
		}
		if (!s.falseBranch.isEmpty()) {
			blk.append(Code.Goto(exitLab));
			blk.append(Code.Label(falseLab));
			for (Stmt st : s.falseBranch) {
				blk.append(generate(st, environment));
			}
		}

		blk.append(Code.Label(exitLab));

		return blk;
	}
	
	private Block generate(Throw s, HashMap<String,Integer> environment) {
		Block blk = generate(s.expr, environment);
		blk.append(Code.Throw(s.expr.rawType()), s.attributes());
		return blk;
	}
	
	private Block generate(Break s, HashMap<String,Integer> environment) {
		BreakScope scope = findEnclosingScope(BreakScope.class);
		if(scope == null) {
			syntaxError(errorMessage(BREAK_OUTSIDE_LOOP), filename, s);
		}
		Block blk = new Block(environment.size());
		blk.append(Code.Goto(scope.label));
		return blk;
	}
	
	private Block generate(Switch s, HashMap<String,Integer> environment) throws ResolveError {
		String exitLab = Block.freshLabel();		
		Block blk = generate(s.expr, environment);				
		Block cblk = new Block(environment.size());
		String defaultTarget = exitLab;
		HashSet<Value> values = new HashSet();
		ArrayList<Pair<Value,String>> cases = new ArrayList();	
		
		for(Stmt.Case c : s.cases) {			
			if(c.expr.isEmpty()) {
				// indicates the default block
				if(defaultTarget != exitLab) {
					syntaxError(errorMessage(DUPLICATE_DEFAULT_LABEL),filename,c);
				} else {
					defaultTarget = Block.freshLabel();	
					cblk.append(Code.Label(defaultTarget), attributes(c));
					for (Stmt st : c.stmts) {
						cblk.append(generate(st, environment));
					}
					cblk.append(Code.Goto(exitLab),attributes(c));
				}
			} else if(defaultTarget == exitLab) {
				String target = Block.freshLabel();	
				cblk.append(Code.Label(target), attributes(c));				
				
				for(Value constant : c.constants) { 					
					if(values.contains(constant)) {
						syntaxError(errorMessage(DUPLICATE_CASE_LABEL),filename,c);
					}									
					cases.add(new Pair(constant,target));
					values.add(constant);
				}
				
				for (Stmt st : c.stmts) {
					cblk.append(generate(st, environment));
				}
				cblk.append(Code.Goto(exitLab),attributes(c));
			} else {
				syntaxError(errorMessage(UNREACHABLE_CODE), filename, c);
			}
		}		
		blk.append(Code.Switch(s.expr.rawType(),defaultTarget,cases),attributes(s));
		blk.append(cblk);
		blk.append(Code.Label(exitLab), attributes(s));		
		return blk;
	}
	
	private Block generate(TryCatch s, HashMap<String,Integer> environment) throws ResolveError {
		String exitLab = Block.freshLabel();		
		Block cblk = new Block(environment.size());		
		for (Stmt st : s.body) {
			cblk.append(generate(st, environment));
		}		
		cblk.append(Code.Goto(exitLab),attributes(s));	
		String endLab = null;
		ArrayList<Pair<Type,String>> catches = new ArrayList<Pair<Type,String>>();
		for(Stmt.Catch c : s.catches) {
			int freeReg = allocate(c.variable,environment);
			Code.Label lab;
			
			if(endLab == null) {
				endLab = Block.freshLabel();
				lab = Code.TryEnd(endLab);
			} else {
				lab = Code.Label(Block.freshLabel());
			}
			Type pt = c.type;
			// TODO: deal with exception type constraints
			catches.add(new Pair<Type,String>(pt,lab.label));
			cblk.append(lab, attributes(c));
			cblk.append(Code.Store(pt, freeReg), attributes(c));
			for (Stmt st : c.stmts) {
				cblk.append(generate(st, environment));
			}
			cblk.append(Code.Goto(exitLab),attributes(c));
		}
		
		Block blk = new Block(environment.size());
		blk.append(Code.TryCatch(endLab,catches),attributes(s));
		blk.append(cblk);
		blk.append(Code.Label(exitLab), attributes(s));
		return blk;
	}
	
	private Block generate(While s, HashMap<String,Integer> environment) {		
		String label = Block.freshLabel();									
				
		Block blk = new Block(environment.size());
		
		
		if(s.invariant != null) {
			String invariantLabel = Block.freshLabel();
			blk.append(Code.Assert(invariantLabel),attributes(s));
			blk.append(generateCondition(invariantLabel, s.invariant, environment));		
			blk.append(Code.Fail("loop invariant not satisfied on entry"), attributes(s));
			blk.append(Code.Label(invariantLabel));			
		}
		
		blk.append(Code.Loop(label, Collections.EMPTY_SET),
				attributes(s));
				
		blk.append(generateCondition(label, invert(s.condition), environment));

		scopes.push(new BreakScope(label));		
		for (Stmt st : s.body) {
			blk.append(generate(st, environment));
		}		
		scopes.pop(); // break
		
		if(s.invariant != null) {
			String invariantLabel = Block.freshLabel();
			blk.append(Code.Assert(invariantLabel),attributes(s));
			blk.append(generateCondition(invariantLabel, s.invariant, environment));		
			blk.append(Code.Fail("loop invariant not restored"), attributes(s));
			blk.append(Code.Label(invariantLabel));			
		}
		
		blk.append(Code.End(label));

		return blk;
	}

	private Block generate(DoWhile s, HashMap<String,Integer> environment) {		
		String label = Block.freshLabel();				
				
		Block blk = new Block(environment.size());
		
		if(s.invariant != null) {
			String invariantLabel = Block.freshLabel();
			blk.append(Code.Assert(invariantLabel),attributes(s));
			blk.append(generateCondition(invariantLabel, s.invariant, environment));		
			blk.append(Code.Fail("loop invariant not satisfied on entry"), attributes(s));
			blk.append(Code.Label(invariantLabel));			
		}
		
		blk.append(Code.Loop(label, Collections.EMPTY_SET),
				attributes(s));
		
		scopes.push(new BreakScope(label));	
		for (Stmt st : s.body) {
			blk.append(generate(st, environment));
		}		
		scopes.pop(); // break
		
		if(s.invariant != null) {
			String invariantLabel = Block.freshLabel();
			blk.append(Code.Assert(invariantLabel),attributes(s));
			blk.append(generateCondition(invariantLabel, s.invariant, environment));		
			blk.append(Code.Fail("loop invariant not restored"), attributes(s));
			blk.append(Code.Label(invariantLabel));			
		}
		
		blk.append(generateCondition(label, invert(s.condition), environment));

		
		blk.append(Code.End(label));

		return blk;
	}
	
	private Block generate(For s, HashMap<String,Integer> environment) {		
		String label = Block.freshLabel();
		
		Block blk = new Block(1);
		
		if(s.invariant != null) {
			String invariantLabel = Block.freshLabel();
			blk.append(Code.Assert(invariantLabel),attributes(s));
			blk.append(generateCondition(invariantLabel, s.invariant, environment));		
			blk.append(Code.Fail("loop invariant not satisfied on entry"), attributes(s));
			blk.append(Code.Label(invariantLabel));			
		}
		
		blk.append(generate(s.source,environment));	
		int freeSlot = allocate(environment);
		if(s.variables.size() > 1) {
			// this is the destructuring case			
			blk.append(Code.ForAll(null, freeSlot, label, Collections.EMPTY_SET), attributes(s));
			blk.append(Code.Load(null, freeSlot), attributes(s));
			blk.append(Code.Destructure(null), attributes(s));
			for(int i=s.variables.size();i>0;--i) {
				String var = s.variables.get(i-1);
				int varReg = allocate(var,environment);
				blk.append(Code.Store(null, varReg), attributes(s));
			}										
		} else {
			// easy case.
			int freeReg = allocate(s.variables.get(0),environment);
			blk.append(Code.ForAll(s.source.rawType(), freeReg, label, Collections.EMPTY_SET), attributes(s));
		}		
		
		// FIXME: add a continue scope
		scopes.push(new BreakScope(label));		
		for (Stmt st : s.body) {			
			blk.append(generate(st, environment));
		}		
		scopes.pop(); // break
		
		if(s.invariant != null) {
			String invariantLabel = Block.freshLabel();
			blk.append(Code.Assert(invariantLabel),attributes(s));
			blk.append(generateCondition(invariantLabel, s.invariant, environment));		
			blk.append(Code.Fail("loop invariant not restored"), attributes(s));
			blk.append(Code.Label(invariantLabel));			
		}
		blk.append(Code.End(label), attributes(s));		

		return blk;
	}

	/**
	 * Translate a source-level condition into a wyil block, using a given
	 * environment mapping named variables to slots. If the condition evaluates
	 * to true, then control is transferred to the given target. Otherwise,
	 * control will fall through to the following bytecode.
	 * 
	 * @param target
	 *            --- target label to goto if condition is true.
	 * @param condition
	 *            --- source-level condition to be translated
	 * @param environment
	 *            --- mapping from variable names to to slot numbers.
	 * @return
	 */
	private Block generateCondition(String target, Expr condition,
			 HashMap<String, Integer> environment) {
		try {
			if (condition instanceof Expr.Constant) {
				return generateCondition(target, (Expr.Constant) condition, environment);
			} else if (condition instanceof Expr.LocalVariable) {
				return generateCondition(target, (Expr.LocalVariable) condition, environment);
			} else if (condition instanceof Expr.ConstantAccess) {
				return generateCondition(target, (Expr.ConstantAccess) condition, environment);
			} else if (condition instanceof Expr.BinOp) {
				return generateCondition(target, (Expr.BinOp) condition, environment);
			} else if (condition instanceof Expr.UnOp) {
				return generateCondition(target, (Expr.UnOp) condition, environment);
			} else if (condition instanceof Expr.AbstractInvoke) {
				return generateCondition(target, (Expr.AbstractInvoke) condition, environment);
			} else if (condition instanceof Expr.RecordAccess) {
				return generateCondition(target, (Expr.RecordAccess) condition, environment);
			} else if (condition instanceof Expr.Record) {
				return generateCondition(target, (Expr.Record) condition, environment);
			} else if (condition instanceof Expr.Tuple) {
				return generateCondition(target, (Expr.Tuple) condition, environment);
			} else if (condition instanceof Expr.ListAccess) {
				return generateCondition(target, (Expr.ListAccess) condition, environment);
			} else if (condition instanceof Expr.StringAccess) {
				return generateCondition(target, (Expr.StringAccess) condition, environment);
			} else if (condition instanceof Expr.DictionaryAccess) {
				return generateCondition(target, (Expr.DictionaryAccess) condition, environment);
			} else if (condition instanceof Expr.Comprehension) {
				return generateCondition(target, (Expr.Comprehension) condition, environment);
			} else {				
				syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), filename, condition);
			}
		} catch (SyntaxError se) {
			throw se;
		} catch (Exception ex) {
			internalFailure("internal failure", filename, condition, ex);
		}

		return null;
	}

	private Block generateCondition(String target, Expr.Constant c, HashMap<String,Integer> environment) {
		Value.Bool b = (Value.Bool) c.value;
		Block blk = new Block(environment.size());
		if (b.value) {
			blk.append(Code.Goto(target));
		} else {
			// do nout
		}
		return blk;
	}

	private Block generateCondition(String target, Expr.LocalVariable v, 
			HashMap<String, Integer> environment) throws ResolveError {
		
		Block blk = new Block(environment.size());				
		blk.append(Code.Load(Type.T_BOOL, environment.get(v.var)));
		blk.append(Code.Const(Value.V_BOOL(true)),attributes(v));
		blk.append(Code.IfGoto(Type.T_BOOL,Code.COp.EQ, target),attributes(v));			

		return blk;
	}
	
	private Block generateCondition(String target, Expr.ConstantAccess v, 
			HashMap<String, Integer> environment) throws ResolveError {
		
		Block blk = new Block(environment.size());		
		Value val = v.value;
		
		// Obviously, this will be evaluated one way or another.
		blk.append(Code.Const(val));
		blk.append(Code.Const(Value.V_BOOL(true)),attributes(v));
		blk.append(Code.IfGoto(v.rawType(),Code.COp.EQ, target),attributes(v));			
		return blk;
	}
		
	private Block generateCondition(String target, Expr.BinOp v, HashMap<String,Integer> environment) {
		
		Expr.BOp bop = v.op;
		Block blk = new Block(environment.size());

		if (bop == Expr.BOp.OR) {
			blk.append(generateCondition(target, v.lhs, environment));
			blk.append(generateCondition(target, v.rhs, environment));
			return blk;
		} else if (bop == Expr.BOp.AND) {
			String exitLabel = Block.freshLabel();
			blk.append(generateCondition(exitLabel, invert(v.lhs), environment));
			blk.append(generateCondition(target, v.rhs, environment));
			blk.append(Code.Label(exitLabel));
			return blk;
		} else if (bop == Expr.BOp.TYPEEQ || bop == Expr.BOp.TYPEIMPLIES) {
			return generateTypeCondition(target, v, environment);
		}

		Code.COp cop = OP2COP(bop,v);
		Type type = v.rawType();
		
		if (cop == Code.COp.EQ && v.lhs instanceof Expr.LocalVariable
				&& v.rhs instanceof Expr.Constant
				&& ((Expr.Constant) v.rhs).value == Value.V_NULL) {
			// this is a simple rewrite to enable type inference.
			Expr.LocalVariable lhs = (Expr.LocalVariable) v.lhs;
			if (!environment.containsKey(lhs.var)) {
				syntaxError(errorMessage(UNKNOWN_VARIABLE), filename, v.lhs);
			}
			int slot = environment.get(lhs.var);					
			blk.append(Code.IfType(v.rawSrcType, slot, Type.T_NULL, target), attributes(v));
		} else if (cop == Code.COp.NEQ && v.lhs instanceof Expr.LocalVariable
				&& v.rhs instanceof Expr.Constant
				&& ((Expr.Constant) v.rhs).value == Value.V_NULL) {			
			// this is a simple rewrite to enable type inference.
			String exitLabel = Block.freshLabel();
			Expr.LocalVariable lhs = (Expr.LocalVariable) v.lhs;
			if (!environment.containsKey(lhs.var)) {
				syntaxError(errorMessage(UNKNOWN_VARIABLE), filename, v.lhs);
			}
			int slot = environment.get(lhs.var);						
			blk.append(Code.IfType(v.rawSrcType, slot, Type.T_NULL, exitLabel), attributes(v));
			blk.append(Code.Goto(target));
			blk.append(Code.Label(exitLabel));
		} else {
			blk.append(generate(v.lhs, environment));			
			blk.append(generate(v.rhs, environment));					
			blk.append(Code.IfGoto(v.rawSrcType, cop, target), attributes(v));
		}
		return blk;
	}

	private Block generateTypeCondition(String target, Expr.BinOp v, HashMap<String,Integer> environment) {
		Block blk;
		int slot;
		
		if (v.lhs instanceof Expr.LocalVariable) {
			Expr.LocalVariable lhs = (Expr.LocalVariable) v.lhs;
			if (!environment.containsKey(lhs.var)) {
				syntaxError(errorMessage(UNKNOWN_VARIABLE), filename, v.lhs);
			}
			slot = environment.get(lhs.var);
			blk = new Block(environment.size());
		} else {
			blk = generate(v.lhs, environment);
			slot = -1;
		}

		Expr.TypeVal rhs =(Expr.TypeVal) v.rhs;		
		
		// TODO: fix type constraints
		blk.append(Code.IfType(v.rawType(), slot, rhs.rawType, target),
				attributes(v));
		return blk;
	}

	private Block generateCondition(String target, Expr.UnOp v, HashMap<String,Integer> environment) {
		Expr.UOp uop = v.op;
		switch (uop) {
		case NOT:
			String label = Block.freshLabel();
			Block blk = generateCondition(label, v.mhs, environment);
			blk.append(Code.Goto(target));
			blk.append(Code.Label(label));
			return blk;
		}
		syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), filename, v);
		return null;
	}

	private Block generateCondition(String target, Expr.ListAccess v, HashMap<String,Integer> environment) {
		Block blk = generate(v, environment);
		blk.append(Code.Const(Value.V_BOOL(true)),attributes(v));
		blk.append(Code.IfGoto(Type.T_BOOL, Code.COp.EQ, target),attributes(v));
		return blk;
	}

	private Block generateCondition(String target, Expr.StringAccess v, HashMap<String,Integer> environment) {
		Block blk = generate(v, environment);
		blk.append(Code.Const(Value.V_BOOL(true)),attributes(v));
		blk.append(Code.IfGoto(Type.T_BOOL, Code.COp.EQ, target),attributes(v));
		return blk;
	}
	
	private Block generateCondition(String target, Expr.DictionaryAccess v, HashMap<String,Integer> environment) {
		Block blk = generate(v, environment);
		blk.append(Code.Const(Value.V_BOOL(true)),attributes(v));
		blk.append(Code.IfGoto(Type.T_BOOL, Code.COp.EQ, target),attributes(v));
		return blk;
	}
	
	private Block generateCondition(String target, Expr.RecordAccess v, HashMap<String,Integer> environment) {
		Block blk = generate(v, environment);		
		blk.append(Code.Const(Value.V_BOOL(true)),attributes(v));
		blk.append(Code.IfGoto(Type.T_BOOL, Code.COp.EQ, target),attributes(v));		
		return blk;
	}

	private Block generateCondition(String target, Expr.AbstractInvoke v, HashMap<String,Integer> environment) throws ResolveError {
		Block blk = generate((Expr) v, environment);	
		blk.append(Code.Const(Value.V_BOOL(true)),attributes(v));
		blk.append(Code.IfGoto(Type.T_BOOL, Code.COp.EQ, target),attributes(v));
		return blk;
	}

	private Block generateCondition(String target, Expr.Comprehension e,  
			HashMap<String,Integer> environment) {
		
		if (e.cop != Expr.COp.NONE && e.cop != Expr.COp.SOME) {
			syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), filename, e);
		}
					
		// Ok, non-boolean case.				
		Block blk = new Block(environment.size());
		ArrayList<Pair<Integer,Integer>> slots = new ArrayList();		
		
		for (Pair<String, Expr> src : e.sources) {
			int srcSlot;
			int varSlot = allocate(src.first(),environment); 
			
			if(src.second() instanceof Expr.LocalVariable) {
				// this is a little optimisation to produce slightly better
				// code.
				Expr.LocalVariable v = (Expr.LocalVariable) src.second();
				if(environment.containsKey(v.var)) {					
					srcSlot = environment.get(v.var);
				} else {					
					// fall-back plan ...
					blk.append(generate(src.second(), environment));
					srcSlot = allocate(environment);
					blk.append(Code.Store(null, srcSlot),attributes(e));	
				}
			} else {
				blk.append(generate(src.second(), environment));
				srcSlot = allocate(environment);
				blk.append(Code.Store(null, srcSlot),attributes(e));	
			}			
			slots.add(new Pair(varSlot,srcSlot));											
		}
				
		ArrayList<String> labels = new ArrayList<String>();
		String loopLabel = Block.freshLabel();
		
		for (Pair<Integer, Integer> p : slots) {
			String lab = loopLabel + "$" + p.first();
			blk.append(Code.Load(null, p.second()), attributes(e));			
			blk.append(Code
					.ForAll(null, p.first(), lab, Collections.EMPTY_LIST),
					attributes(e));
			labels.add(lab);
		}
								
		if (e.cop == Expr.COp.NONE) {
			String exitLabel = Block.freshLabel();
			blk.append(generateCondition(exitLabel, e.condition, 
					environment));
			for (int i = (labels.size() - 1); i >= 0; --i) {				
				blk.append(Code.End(labels.get(i)));
			}
			blk.append(Code.Goto(target));
			blk.append(Code.Label(exitLabel));
		} else { // SOME			
			blk.append(generateCondition(target, e.condition, 
					environment));
			for (int i = (labels.size() - 1); i >= 0; --i) {
				blk.append(Code.End(labels.get(i)));
			}
		} // ALL, LONE and ONE will be harder					
		
		return blk;
	}

	/**
	 * Translate a source-level expression into a wyil block, using a given
	 * environment mapping named variables to slots. The result of the
	 * expression remains on the wyil stack.
	 * 
	 * @param expression
	 *            --- source-level expression to be translated
	 * @param environment
	 *            --- mapping from variable names to to slot numbers.
	 * @return
	 */
	private Block generate(Expr expression, HashMap<String,Integer> environment) {
		try {
			if (expression instanceof Expr.Constant) {
				return generate((Expr.Constant) expression, environment);
			} else if (expression instanceof Expr.LocalVariable) {
				return generate((Expr.LocalVariable) expression, environment);
			} else if (expression instanceof Expr.ConstantAccess) {
				return generate((Expr.ConstantAccess) expression, environment);
			} else if (expression instanceof Expr.Set) {
				return generate((Expr.Set) expression, environment);
			} else if (expression instanceof Expr.List) {
				return generate((Expr.List) expression, environment);
			} else if (expression instanceof Expr.SubList) {
				return generate((Expr.SubList) expression, environment);
			} else if (expression instanceof Expr.BinOp) {
				return generate((Expr.BinOp) expression, environment);
			} else if (expression instanceof Expr.SetLength) {
				return generate((Expr.SetLength) expression, environment);
			} else if (expression instanceof Expr.ListLength) {
				return generate((Expr.ListLength) expression, environment);
			} else if (expression instanceof Expr.StringLength) {
				return generate((Expr.StringLength) expression, environment);
			} else if (expression instanceof Expr.DictionaryLength) {
				return generate((Expr.DictionaryLength) expression, environment);
			} else if (expression instanceof Expr.ProcessAccess) {
				return generate((Expr.ProcessAccess) expression, environment);
			} else if (expression instanceof Expr.Convert) {
				return generate((Expr.Convert) expression, environment);
			} else if (expression instanceof Expr.ListAccess) {
				return generate((Expr.ListAccess) expression, environment);
			} else if (expression instanceof Expr.StringAccess) {
				return generate((Expr.StringAccess) expression, environment);
			} else if (expression instanceof Expr.DictionaryAccess) {
				return generate((Expr.DictionaryAccess) expression, environment);
			} else if (expression instanceof Expr.UnOp) {
				return generate((Expr.UnOp) expression, environment);
			} else if (expression instanceof Expr.FunctionCall) {
				return generate((Expr.FunctionCall) expression, true, environment);
			} else if (expression instanceof Expr.MessageSend) {
				return generate((Expr.MessageSend) expression, true, environment);
			} else if (expression instanceof Expr.MethodCall) {
				return generate((Expr.MethodCall) expression, true, environment);
			} else if (expression instanceof Expr.Comprehension) {
				return generate((Expr.Comprehension) expression, environment);
			} else if (expression instanceof Expr.RecordAccess) {
				return generate((Expr.RecordAccess) expression, environment);
			} else if (expression instanceof Expr.Record) {
				return generate((Expr.Record) expression, environment);
			} else if (expression instanceof Expr.Tuple) {
				return generate((Expr.Tuple) expression, environment);
			} else if (expression instanceof Expr.Dictionary) {
				return generate((Expr.Dictionary) expression, environment);
			} else if (expression instanceof Expr.Function) {
				return generate((Expr.Function) expression, environment);
			} else {
				// should be dead-code
				internalFailure("unknown expression encountered: "
						+ expression.getClass().getName() + " (" + expression
						+ ")", filename, expression);
			}
		} catch (ResolveError rex) {
			syntaxError(rex.getMessage(), filename, expression, rex);
		} catch (SyntaxError se) {
			throw se;
		} catch (Exception ex) {
			internalFailure("internal failure", filename, expression, ex);
		}

		return null;
	}

	private Block generate(Expr.MessageSend fc, boolean retval,
			HashMap<String, Integer> environment) throws ResolveError {
		Block blk = new Block(environment.size());

		blk.append(generate(fc.qualification, environment));
		
		for (Expr e : fc.arguments) {
			blk.append(generate(e, environment));
		}
		
		blk.append(Code.Send(fc.rawFunctionType(), fc.nid, fc.synchronous, retval),
				attributes(fc));		

		return blk;
	}
	
	private Block generate(Expr.MethodCall fc, boolean retval,
			HashMap<String, Integer> environment) throws ResolveError {
		Block blk = new Block(environment.size());

		for (Expr e : fc.arguments) {
			blk.append(generate(e, environment));
		}

		// FIXME: should split Code.Invoke into Code.FunCall and Code.MethCall.
		
		blk.append(Code.Invoke(fc.rawFunctionType(), fc.nid(), retval), attributes(fc));

		return blk;
	}
	
	private Block generate(Expr.FunctionCall fc, boolean retval,
			HashMap<String, Integer> environment) throws ResolveError {
		Block blk = new Block(environment.size());

		for (Expr e : fc.arguments) {
			blk.append(generate(e, environment));
		}

		blk.append(Code.Invoke(fc.rawFunctionType(), fc.nid(), retval), attributes(fc));

		return blk;
	}
	
	private Block generate(Expr.AbstractInvoke s, boolean retval, HashMap<String,Integer> environment) throws ResolveError {
		List<Expr> args = s.arguments;
		Block blk = new Block(environment.size());
		Type[] paramTypes = new Type[args.size()]; 
		
		boolean receiverIsThis = s.qualification != null && s.qualification instanceof Expr.LocalVariable && ((Expr.LocalVariable)s.qualification).var.equals("this");
		
		Attributes.Module modInfo = s.attribute(Attributes.Module.class);

		/**
		 * An indirect variable invoke represents an invoke statement on a local
		 * variable.
		 */
		boolean variableIndirectInvoke = environment.containsKey(s.name);

		/**
		 * A direct invoke indicates no receiver was provided, and there was a
		 * matching external symbol.
		 */
		boolean directInvoke = !variableIndirectInvoke && s.qualification == null && modInfo != null;		
		
		/**
		 * A method invoke indicates the receiver was this, and there was a
		 * matching external symbol.
		 */
		boolean methodInvoke = !variableIndirectInvoke && receiverIsThis && modInfo != null;
		
		/**
		 * An field indirect invoke indicates an invoke statement on a value
		 * coming out of a field.
		 */
		boolean fieldIndirectInvoke = !variableIndirectInvoke && s.qualification != null && modInfo == null;

		/**
		 * A direct send indicates a message send to a matching external symbol.
		 */
		boolean directSend = !variableIndirectInvoke && s.qualification != null
				&& !receiverIsThis && modInfo != null;
											
		if(variableIndirectInvoke) {
			blk.append(Code.Load(null, environment.get(s.name)),attributes(s));
		} 
		
		if (s.qualification != null) {			
			blk.append(generate(s.qualification, environment));
		}

		if(fieldIndirectInvoke) {
			blk.append(Code.FieldLoad(null, s.name),attributes(s));
		}
		
		int i = 0;
		for (Expr e : args) {
			blk.append(generate(e, environment));
			paramTypes[i++] = Type.T_ANY;
		}	
					
		if(variableIndirectInvoke) {			
			if(s.qualification != null) {
				Type.Method mt = checkType(Type.Method(null, Type.T_VOID, Type.T_VOID, paramTypes),Type.Method.class,s);
				blk.append(Code.IndirectSend(mt,s.synchronous, retval),attributes(s));
			} else {
				Type.Function ft = checkType(Type.Function(Type.T_VOID, Type.T_VOID, paramTypes),Type.Function.class,s);
				blk.append(Code.IndirectInvoke(ft, retval),attributes(s));
			}
		} else if(fieldIndirectInvoke) {
			Type.Function ft = checkType(Type.Function(Type.T_VOID, Type.T_VOID, paramTypes),Type.Function.class,s);
			blk.append(Code.IndirectInvoke(ft, retval),attributes(s));
		} else if(directInvoke || methodInvoke) {
			NameID name = new NameID(modInfo.module, s.name);
			if(receiverIsThis) {
				Type.Method mt = checkType(
						Type.Method(null, Type.T_VOID, Type.T_VOID, paramTypes),
						Type.Method.class, s);
				blk.append(Code.Invoke(mt, name, retval), attributes(s));
			} else {
				Type.Function ft = checkType(
						Type.Function(Type.T_VOID, Type.T_VOID, paramTypes),
						Type.Function.class, s);
				blk.append(Code.Invoke(ft, name, retval), attributes(s));
			}
		} else if(directSend) {						
			NameID name = new NameID(modInfo.module, s.name);
			Type.Method mt = checkType(
					Type.Method(null, Type.T_VOID, Type.T_VOID, paramTypes),
					Type.Method.class, s);
			blk.append(Code.Send(mt, name, s.synchronous, retval),
					attributes(s));
		} else {
			syntaxError(errorMessage(UNKNOWN_FUNCTION_OR_METHOD), filename, s);
		}
		
		return blk;
	}

	private Block generate(Expr.Constant c, HashMap<String,Integer> environment) {
		Block blk = new Block(environment.size());
		blk.append(Code.Const(c.value), attributes(c));		
		return blk;
	}

	private Block generate(Expr.Function s, HashMap<String,Integer> environment) {
		Attributes.Module modInfo = s.attribute(Attributes.Module.class);		
		NameID name = new NameID(modInfo.module, s.name);			
		Block blk = new Block(environment.size());
		blk.append(Code.Const(Value.V_FUN(name, s.rawType)),
				attributes(s));
		return blk;
	}
	
	private Block generate(Expr.ConstantAccess v, HashMap<String,Integer> environment) throws ResolveError {						
		Block blk = new Block(environment.size());
		Value val = v.value;				
		blk.append(Code.Const(val),attributes(v));
		return blk;
	}
	
	private Block generate(Expr.LocalVariable v, HashMap<String,Integer> environment) throws ResolveError {
		
		if (environment.containsKey(v.var)) {
			Block blk = new Block(environment.size());
			blk.append(Code.Load(v.rawType(), environment.get(v.var)), attributes(v));
			return blk;
		} else {
			syntaxError(errorMessage(VARIABLE_POSSIBLY_UNITIALISED), filename,
					v);
		}
		
		// must be an error
		syntaxError("unknown variable \"" + v.var + "\"",filename,v);
		return null;
	}

	private Block generate(Expr.UnOp v, HashMap<String,Integer> environment) {
		Block blk = generate(v.mhs,  environment);	
		switch (v.op) {
		case NEG:
			blk.append(Code.Negate(v.rawType()), attributes(v));
			break;
		case INVERT:
			blk.append(Code.Invert(v.rawType()), attributes(v));
			break;
		case NOT:
			String falseLabel = Block.freshLabel();
			String exitLabel = Block.freshLabel();
			blk = generateCondition(falseLabel, v.mhs, environment);
			blk.append(Code.Const(Value.V_BOOL(true)), attributes(v));
			blk.append(Code.Goto(exitLabel));
			blk.append(Code.Label(falseLabel));
			blk.append(Code.Const(Value.V_BOOL(false)), attributes(v));
			blk.append(Code.Label(exitLabel));
			break;							
		default:
			// should be dead-code
			internalFailure("unexpected unary operator encountered", filename, v);
			return null;
		}
		return blk;
	}
	
	private Block generate(Expr.ListLength v, HashMap<String,Integer> environment) {
		Block blk = generate(v.src,  environment);	
		blk.append(Code.ListLength(v.rawSrcType), attributes(v));
		return blk;
	}

	private Block generate(Expr.SetLength v, HashMap<String,Integer> environment) {
		Block blk = generate(v.src,  environment);	
		blk.append(Code.SetLength(v.rawSrcType), attributes(v));
		return blk;
	}	
	
	private Block generate(Expr.StringLength v, HashMap<String,Integer> environment) {
		Block blk = generate(v.src,  environment);	
		blk.append(Code.StringLength(), attributes(v));
		return blk;
	}	
	
	private Block generate(Expr.DictionaryLength v, HashMap<String,Integer> environment) {
		Block blk = generate(v.src,  environment);	
		blk.append(Code.DictLength(v.rawSrcType), attributes(v));
		return blk;
	}	
	
	private Block generate(Expr.ProcessAccess v, HashMap<String,Integer> environment) {
		Block blk = generate(v.src,  environment);	
		blk.append(Code.ProcLoad(v.rawSrcType), attributes(v));
		return blk;
	}	
	
	private Block generate(Expr.ListAccess v, HashMap<String,Integer> environment) {
		Block blk = new Block(environment.size());
		blk.append(generate(v.src, environment));
		blk.append(generate(v.index, environment));
		blk.append(Code.ListLoad(v.rawSrcType),attributes(v));
		return blk;
	}

	private Block generate(Expr.StringAccess v, HashMap<String,Integer> environment) {
		Block blk = new Block(environment.size());
		blk.append(generate(v.src, environment));
		blk.append(generate(v.index, environment));
		blk.append(Code.StringLoad(),attributes(v));
		return blk;
	}
	
	private Block generate(Expr.DictionaryAccess v, HashMap<String,Integer> environment) {
		Block blk = new Block(environment.size());
		blk.append(generate(v.src, environment));
		blk.append(generate(v.index, environment));
		blk.append(Code.DictLoad(v.rawSrcType),attributes(v));
		return blk;
	}
	
	private Block generate(Expr.Convert v, HashMap<String,Integer> environment) {
		Block blk = new Block(environment.size());
		blk.append(generate(v.expr, environment));		
		Type from = v.expr.rawType();
		Type to = v.rawType();
		// TODO: include constraints
		blk.append(Code.Convert(from,to),attributes(v));
		return blk;
	}
	
	private Block generate(Expr.BinOp v, HashMap<String,Integer> environment) {

		// could probably use a range test for this somehow
		if (v.op == Expr.BOp.EQ || v.op == Expr.BOp.NEQ || v.op == Expr.BOp.LT
				|| v.op == Expr.BOp.LTEQ || v.op == Expr.BOp.GT || v.op == Expr.BOp.GTEQ
				|| v.op == Expr.BOp.SUBSET || v.op == Expr.BOp.SUBSETEQ
				|| v.op == Expr.BOp.ELEMENTOF || v.op == Expr.BOp.AND || v.op == Expr.BOp.OR) {
			String trueLabel = Block.freshLabel();
			String exitLabel = Block.freshLabel();
			Block blk = generateCondition(trueLabel, v, environment);
			blk.append(Code.Const(Value.V_BOOL(false)), attributes(v));			
			blk.append(Code.Goto(exitLabel));
			blk.append(Code.Label(trueLabel));
			blk.append(Code.Const(Value.V_BOOL(true)), attributes(v));				
			blk.append(Code.Label(exitLabel));			
			return blk;
		}

		Expr.BOp bop = v.op;
		Block blk = new Block(environment.size());
		blk.append(generate(v.lhs, environment));
		blk.append(generate(v.rhs, environment));
		Type result = v.rawType();
		
		switch(bop) {		
		case UNION:
			blk.append(Code.SetUnion((Type.Set)result,Code.OpDir.UNIFORM),attributes(v));			
			return blk;			
		case INTERSECTION:
			blk.append(Code.SetIntersect((Type.Set)result,Code.OpDir.UNIFORM),attributes(v));
			return blk;			
		case DIFFERENCE:
			blk.append(Code.SetDifference((Type.Set)result,Code.OpDir.UNIFORM),attributes(v));
			return blk;			
		case LISTAPPEND:
			blk.append(Code.ListAppend((Type.List)result,Code.OpDir.UNIFORM),attributes(v));
			return blk;	
		case STRINGAPPEND:
			Type lhs = v.lhs.rawType();
			Type rhs = v.rhs.rawType();
			Code.OpDir dir;
			if(lhs == Type.T_STRING && rhs == Type.T_STRING) {
				dir = Code.OpDir.UNIFORM;
			} else if(lhs == Type.T_STRING) {
				dir = Code.OpDir.LEFT;
			} else {
				dir = Code.OpDir.RIGHT;
			}
			blk.append(Code.StringAppend(dir),attributes(v));
			return blk;	
		default:
			blk.append(Code.BinOp(result, OP2BOP(bop,v)),attributes(v));			
			return blk;
		}		
	}

	private Block generate(Expr.Set v, HashMap<String,Integer> environment) {
		Block blk = new Block(environment.size());		
		int nargs = 0;
		for (Expr e : v.arguments) {				
			nargs++;
			blk.append(generate(e, environment));
		}
		blk.append(Code.NewSet(v.rawType,nargs),attributes(v));		
		return blk;
	}
	
	private Block generate(Expr.List v, HashMap<String,Integer> environment) {
		Block blk = new Block(environment.size());		
		int nargs = 0;
		for (Expr e : v.arguments) {				
			nargs++;
			blk.append(generate(e, environment));
		}
		blk.append(Code.NewList(v.rawType,nargs),attributes(v));		
		return blk;
	}
	
	private Block generate(Expr.SubList v, HashMap<String, Integer> environment) {
		Block blk = new Block(environment.size());
		blk.append(generate(v.src, environment));
		blk.append(generate(v.start, environment));
		blk.append(generate(v.end, environment));
		blk.append(Code.SubList(v.rawType()), attributes(v));
		return blk;
	}
	
	private Block generate(Expr.Comprehension e, HashMap<String,Integer> environment) {

		// First, check for boolean cases which are handled mostly by
		// generateCondition.
		if (e.cop == Expr.COp.SOME || e.cop == Expr.COp.NONE) {
			String trueLabel = Block.freshLabel();
			String exitLabel = Block.freshLabel();
			int freeSlot = allocate(environment);
			Block blk = generateCondition(trueLabel, e, environment);					
			blk.append(Code.Const(Value.V_BOOL(false)), attributes(e));
			blk.append(Code.Store(null,freeSlot),attributes(e));			
			blk.append(Code.Goto(exitLabel));
			blk.append(Code.Label(trueLabel));
			blk.append(Code.Const(Value.V_BOOL(true)), attributes(e));
			blk.append(Code.Store(null,freeSlot),attributes(e));
			blk.append(Code.Label(exitLabel));
			blk.append(Code.Load(null,freeSlot),attributes(e));
			return blk;
		}

		// Ok, non-boolean case.				
		Block blk = new Block(environment.size());
		ArrayList<Pair<Integer,Integer>> slots = new ArrayList();		
		
		for (Pair<String, Expr> src : e.sources) {
			int srcSlot;
			int varSlot = allocate(src.first(),environment); 
			
			if(src.second() instanceof Expr.LocalVariable) {
				// this is a little optimisation to produce slightly better
				// code.
				Expr.LocalVariable v = (Expr.LocalVariable) src.second();
				if(environment.containsKey(v.var)) {
					srcSlot = environment.get(v.var);
				} else {
					// fall-back plan ...
					blk.append(generate(src.second(), environment));
					srcSlot = allocate(environment);				
					blk.append(Code.Store(null, srcSlot),attributes(e));	
				}
			} else {
				blk.append(generate(src.second(), environment));
				srcSlot = allocate(environment);
				blk.append(Code.Store(null, srcSlot),attributes(e));	
			}			
			slots.add(new Pair(varSlot,srcSlot));											
		}
		
		int resultSlot = allocate(environment);
		
		if (e.cop == Expr.COp.LISTCOMP) {
			blk.append(Code.NewList(null,0), attributes(e));
			blk.append(Code.Store(null,resultSlot),attributes(e));
		} else {
			blk.append(Code.NewSet(null,0), attributes(e));
			blk.append(Code.Store(null,resultSlot),attributes(e));			
		}
		
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
		String loopLabel = Block.freshLabel();
		
		for (Pair<Integer, Integer> p : slots) {
			String target = loopLabel + "$" + p.first();
			blk.append(Code.Load(null, p.second()), attributes(e));
			blk.append(Code
					.ForAll(null, p.first(), target, Collections.EMPTY_LIST),
					attributes(e));
			labels.add(target);
		}
		
		if (e.condition != null) {
			blk.append(generateCondition(continueLabel, invert(e.condition),
					environment));
		}
		
		blk.append(Code.Load(null,resultSlot),attributes(e));
		blk.append(generate(e.value, environment));
		blk.append(Code.SetUnion(null, Code.OpDir.LEFT),attributes(e));
		blk.append(Code.Store(null,resultSlot),attributes(e));
			
		if(e.condition != null) {
			blk.append(Code.Label(continueLabel));			
		} 

		for (int i = (labels.size() - 1); i >= 0; --i) {
			blk.append(Code.End(labels.get(i)));
		}

		blk.append(Code.Load(null,resultSlot),attributes(e));
		
		return blk;
	}

	private Block generate(Expr.Record sg, HashMap<String,Integer> environment) {
		Block blk = new Block(environment.size());
		ArrayList<String> keys = new ArrayList<String>(sg.fields.keySet());
		Collections.sort(keys);
		for (String key : keys) {		
			blk.append(generate(sg.fields.get(key), environment));
		}		
		blk.append(Code.NewRecord(sg.rawType()), attributes(sg));
		return blk;
	}

	private Block generate(Expr.Tuple sg, HashMap<String,Integer> environment) {		
		Block blk = new Block(environment.size());		
		for (Expr e : sg.fields) {									
			blk.append(generate(e, environment));
		}
		blk.append(Code.NewTuple(sg.rawType(),sg.fields.size()),attributes(sg));
		return blk;		
	}

	private Block generate(Expr.Dictionary sg, HashMap<String,Integer> environment) {		
		Block blk = new Block(environment.size());		
		for (Pair<Expr,Expr> e : sg.pairs) {			
			blk.append(generate(e.first(), environment));
			blk.append(generate(e.second(), environment));
		}
		blk.append(Code.NewDict(sg.rawType(),sg.pairs.size()),attributes(sg));
		return blk;
	}
	
	private Block generate(Expr.RecordAccess sg, HashMap<String,Integer> environment) {
		Block lhs = generate(sg.src, environment);		
		lhs.append(Code.FieldLoad(sg.rawSrcType,sg.name), attributes(sg));
		return lhs;
	}
	
	private static int allocate(HashMap<String,Integer> environment) {
		return allocate("$" + environment.size(),environment);
	}
	
	private static int allocate(String var, HashMap<String,Integer> environment) {
		// this method is a bit of a hack
		Integer r = environment.get(var);
		if(r == null) {
			int slot = environment.size();
			environment.put(var, slot);
			return slot;
		} else {
			return r;
		}
	}			
	
	private static Expr invert(Expr e) {
		if (e instanceof Expr.BinOp) {
			Expr.BinOp bop = (Expr.BinOp) e;
			Expr.BinOp nbop = null;
			switch (bop.op) {
			case AND:
				nbop = new Expr.BinOp(Expr.BOp.OR, invert(bop.lhs), invert(bop.rhs), attributes(e));
				break;
			case OR:
				nbop = new Expr.BinOp(Expr.BOp.AND, invert(bop.lhs), invert(bop.rhs), attributes(e));
				break;
			case EQ:
				nbop =  new Expr.BinOp(Expr.BOp.NEQ, bop.lhs, bop.rhs, attributes(e));
				break;
			case NEQ:
				nbop =  new Expr.BinOp(Expr.BOp.EQ, bop.lhs, bop.rhs, attributes(e));
				break;
			case LT:
				nbop =  new Expr.BinOp(Expr.BOp.GTEQ, bop.lhs, bop.rhs, attributes(e));
				break;
			case LTEQ:
				nbop =  new Expr.BinOp(Expr.BOp.GT, bop.lhs, bop.rhs, attributes(e));
				break;
			case GT:
				nbop =  new Expr.BinOp(Expr.BOp.LTEQ, bop.lhs, bop.rhs, attributes(e));
				break;
			case GTEQ:
				nbop =  new Expr.BinOp(Expr.BOp.LT, bop.lhs, bop.rhs, attributes(e));
				break;
			}
			nbop.rawSrcType = bop.rawSrcType;
			nbop.nominalType = bop.nominalType;
		} else if (e instanceof Expr.UnOp) {
			Expr.UnOp uop = (Expr.UnOp) e;
			switch (uop.op) {
			case NOT:
				return uop.mhs;
			}
		}
		return new Expr.UnOp(Expr.UOp.NOT, e);
	}

	private Code.BOp OP2BOP(Expr.BOp bop, SyntacticElement elem) {
		switch (bop) {
		case ADD:
			return Code.BOp.ADD;
		case SUB:
			return Code.BOp.SUB;		
		case MUL:
			return Code.BOp.MUL;
		case DIV:
			return Code.BOp.DIV;
		case REM:
			return Code.BOp.REM;
		case RANGE:
			return Code.BOp.RANGE;
		case BITWISEAND:
			return Code.BOp.BITWISEAND;
		case BITWISEOR:
			return Code.BOp.BITWISEOR;
		case BITWISEXOR:
			return Code.BOp.BITWISEXOR;
		case LEFTSHIFT:
			return Code.BOp.LEFTSHIFT;
		case RIGHTSHIFT:
			return Code.BOp.RIGHTSHIFT;
		}
		syntaxError(errorMessage(INVALID_BINARY_EXPRESSION), filename, elem);
		return null;
	}

	private Code.COp OP2COP(Expr.BOp bop, SyntacticElement elem) {
		switch (bop) {
		case EQ:
			return Code.COp.EQ;
		case NEQ:
			return Code.COp.NEQ;
		case LT:
			return Code.COp.LT;
		case LTEQ:
			return Code.COp.LTEQ;
		case GT:
			return Code.COp.GT;
		case GTEQ:
			return Code.COp.GTEQ;
		case SUBSET:
			return Code.COp.SUBSET;
		case SUBSETEQ:
			return Code.COp.SUBSETEQ;
		case ELEMENTOF:
			return Code.COp.ELEMOF;
		}
		syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), filename, elem);
		return null;
	}


	/**
	 * The shiftBlock method takes a block and shifts every slot a given amount
	 * to the right. The number of inputs remains the same. This method is used 
	 * 
	 * @param amount
	 * @param blk
	 * @return
	 */
	private static Block shiftBlock(int amount, Block blk) {
		HashMap<Integer,Integer> binding = new HashMap<Integer,Integer>();
		for(int i=0;i!=blk.numSlots();++i) {
			binding.put(i,i+amount);
		}
		Block nblock = new Block(blk.numInputs());
		for(Block.Entry e : blk) {
			Code code = e.code.remap(binding);
			nblock.append(code,e.attributes());
		}
		return nblock.relabel();
	}
	
	/**
	 * The chainBlock method takes a block and replaces every fail statement
	 * with a goto to a given label. This is useful for handling constraints in
	 * union types, since if the constraint is not met that doesn't mean its
	 * game over.
	 * 
	 * @param target
	 * @param blk
	 * @return
	 */
	private static Block chainBlock(String target, Block blk) {	
		Block nblock = new Block(blk.numInputs());
		for (Block.Entry e : blk) {
			if (e.code instanceof Code.Fail) {
				nblock.append(Code.Goto(target), e.attributes());
			} else {
				nblock.append(e.code, e.attributes());
			}
		}
		return nblock.relabel();
	}
	
	/**
	 * The attributes method extracts those attributes of relevance to wyil, and
	 * discards those which are only used for the wyc front end.
	 * 
	 * @param elem
	 * @return
	 */
	private static Collection<Attribute> attributes(SyntacticElement elem) {
		ArrayList<Attribute> attrs = new ArrayList<Attribute>();
		attrs.add(elem.attribute(Attribute.Source.class));
		return attrs;
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
	
	private <T extends Scope> T findEnclosingScope(Class<T> c) {
		for(int i=scopes.size()-1;i>=0;--i) {
			Scope s = scopes.get(i);
			if(c.isInstance(s)) {
				return (T) s;
			}
		}
		return null;
	}	
	
	private abstract class Scope {}
	
	private class BreakScope extends Scope {
		public String label;
		public BreakScope(String l) { label = l; }
	}

	private class ContinueScope extends Scope {
		public String label;
		public ContinueScope(String l) { label = l; }
	}
}
