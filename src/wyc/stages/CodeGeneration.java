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

import static wyc.lang.WhileyFile.*;
import static wyil.util.ErrorMessages.*;
import wybs.lang.SyntacticElement;
import wybs.lang.SyntaxError;
import wybs.util.ResolveError;
import wyc.builder.*;
import wyc.lang.*;
import wyc.lang.Stmt.*;
import wyil.util.*;
import wyil.lang.*;
import wyc.lang.WhileyFile.*;

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
	private final WhileyBuilder builder;	
	private final GlobalResolver resolver;
	private GlobalGenerator globalGenerator;
	private LocalGenerator localGenerator;
	private Stack<Scope> scopes = new Stack<Scope>();
	private FunctionOrMethodOrMessage currentFunDecl;

	// The shadow set is used to (efficiently) aid the correct generation of
	// runtime checks for post conditions. The key issue is that a post
	// condition may refer to parameters of the method. However, if those
	// parameters are modified during the method, then we must store their
	// original value on entry for use in the post-condition runtime check.
	// These stored values are called "shadows".
	private final HashMap<String, Integer> shadows = new HashMap<String, Integer>();

	public CodeGeneration(WhileyBuilder builder, GlobalGenerator generator, GlobalResolver resolver) {
		this.builder = builder;		
		this.resolver = resolver;
		this.globalGenerator = generator;
	}

	public WyilFile generate(WhileyFile wf) {
		HashMap<Pair<Type.Function, String>, WyilFile.Method> methods = new HashMap();
		ArrayList<WyilFile.TypeDef> types = new ArrayList<WyilFile.TypeDef>();
		ArrayList<WyilFile.ConstDef> constants = new ArrayList<WyilFile.ConstDef>();

		for (WhileyFile.Declaration d : wf.declarations) {
			try {
				if (d instanceof TypeDef) {
					types.add(generate((TypeDef) d));
				} else if (d instanceof Constant) {
					constants.add(generate((Constant) d));
				} else if (d instanceof FunctionOrMethodOrMessage) {
					WyilFile.Method mi = generate((FunctionOrMethodOrMessage) d);
					Pair<Type.Function, String> key = new Pair(mi.type(), mi.name());
					WyilFile.Method method = methods.get(key);
					if (method != null) {
						// coalesce cases
						ArrayList<WyilFile.Case> ncases = new ArrayList<WyilFile.Case>(
								method.cases());
						ncases.addAll(mi.cases());
						mi = new WyilFile.Method(method.modifiers(), mi.name(),
								mi.type(), ncases);
					}
					methods.put(key, mi);
				}
			} catch (SyntaxError se) {
				throw se;
			} catch (Throwable ex) {
				internalFailure(ex.getMessage(), localGenerator.context(), d, ex);
			}
		}
		
		return new WyilFile(wf.module, wf.filename, methods.values(), types,
				constants);				
	}

	private WyilFile.ConstDef generate(Constant cd) {
		// TODO: this the point where were should an evaluator
		return new WyilFile.ConstDef(cd.modifiers, cd.name, cd.resolvedValue);
	}

	private WyilFile.TypeDef generate(TypeDef td) throws Exception {		
		Block constraint = null;
		if(td.constraint != null) {			
			localGenerator = new LocalGenerator(globalGenerator,td);			
			NameID nid = new NameID(td.file().module,td.name);
			constraint = globalGenerator.generate(nid);			
		}
		
		return new WyilFile.TypeDef(td.modifiers, td.name(), td.resolvedType.raw(), constraint);
	}

	private WyilFile.Method generate(FunctionOrMethodOrMessage fd) throws Exception {		
		localGenerator = new LocalGenerator(globalGenerator,fd);	
		
		HashMap<String,Integer> environment = new HashMap<String,Integer>();
		
		// method return type		
		int paramIndex = 0;
		int nparams = fd.parameters.size();
		// method receiver type (if applicable)
		if (fd instanceof Message) {
			Message md = (Message) fd;
			if(md.receiver != null) {						
				// TODO: fix receiver constraints
				environment.put("this", paramIndex++);	
				nparams++;
			}
		}
		
		// ==================================================================
		// Generate pre-condition
		// ==================================================================
		
		Block precondition = null;
		for (WhileyFile.Parameter p : fd.parameters) {
			// First, generate and inline any constraints associated with the
			// type.
			// Now, map the parameter to its index

			Block constraint = globalGenerator.generate(p.type, p);
			if (constraint != null) {
				if (precondition == null) {
					precondition = new Block(nparams);
				}
				constraint = shiftBlockExceptionZero(nparams, paramIndex,
						constraint);
				precondition.append(constraint);
			}
			environment.put(p.name(), paramIndex++);
		}
		
		// Resolve pre- and post-condition								
		
		if(fd.precondition != null) {	
			if(precondition == null) {
				precondition = new Block(nparams);
			}
			String lab = Block.freshLabel();
			HashMap<String,Integer> preEnv = new HashMap<String,Integer>(environment);						
			precondition.append(localGenerator.generateCondition(lab, fd.precondition, preEnv));		
			precondition.append(Code.Fail("precondition not satisfied"), attributes(fd.precondition));
			precondition.append(Code.Label(lab));			
		}
		
		// ==================================================================
		// Generate post-condition
		// ==================================================================
		Block postcondition = globalGenerator.generate(fd.ret,fd);						
		
		if (fd.postcondition != null) {
			HashMap<String,Integer> postEnv = new HashMap<String,Integer>();
			postEnv.put("$", 0);
			for(String var : environment.keySet()) {
				postEnv.put(var, environment.get(var)+1);
			}
			String lab = Block.freshLabel();
			postcondition = new Block(postEnv.size());
			postcondition.append(localGenerator.generateCondition(lab, fd.postcondition,
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
		
		List<WyilFile.Case> ncases = new ArrayList<WyilFile.Case>();				
		ArrayList<String> locals = new ArrayList<String>();
		
		for(int i=0;i!=environment.size();++i) {
			locals.add(null);
		}
		
		for(Map.Entry<String,Integer> e : environment.entrySet()) {
			locals.set(e.getValue(),e.getKey());
		}	
		
		ncases.add(new WyilFile.Case(body,precondition,postcondition,locals));
				
		if(fd instanceof WhileyFile.Function) {
			WhileyFile.Function f = (WhileyFile.Function) fd;
			return new WyilFile.Method(fd.modifiers, fd.name(), f.resolvedType.raw(), ncases);
		} else if(fd instanceof WhileyFile.Method) {
			WhileyFile.Method md = (WhileyFile.Method) fd;			
			return new WyilFile.Method(fd.modifiers, fd.name(), md.resolvedType.raw(), ncases);
		} else {
			WhileyFile.Message md = (WhileyFile.Message) fd;					
			return new WyilFile.Method(fd.modifiers, fd.name(), md.resolvedType.raw(), ncases);
		}		
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
			} else if (stmt instanceof ForAll) {
				return generate((ForAll) stmt, environment);
			} else if (stmt instanceof Expr.MessageSend) {
				return localGenerator.generate((Expr.MessageSend) stmt,false,environment);								
			} else if (stmt instanceof Expr.MethodCall) {
				return localGenerator.generate((Expr.MethodCall) stmt,false,environment);								
			} else if (stmt instanceof Expr.FunctionCall) {
				return localGenerator.generate((Expr.FunctionCall) stmt,false,environment);								
			} else if (stmt instanceof Expr.IndirectMethodCall) {
				return localGenerator.generate((Expr.IndirectMethodCall) stmt,false,environment);								
			} else if (stmt instanceof Expr.IndirectFunctionCall) {
				return localGenerator.generate((Expr.IndirectFunctionCall) stmt,false,environment);								
			} else if (stmt instanceof Expr.New) {
				return localGenerator.generate((Expr.New) stmt, environment);
			} else if (stmt instanceof Skip) {
				return generate((Skip) stmt, environment);
			} else {
				// should be dead-code
				internalFailure("unknown statement: "
						+ stmt.getClass().getName(), localGenerator.context(), stmt);
			}
		} catch (ResolveError rex) {
			syntaxError(rex.getMessage(), localGenerator.context(), stmt, rex);
		} catch (SyntaxError sex) {
			throw sex;
		} catch (Exception ex) {			
			internalFailure(ex.getMessage(), localGenerator.context(), stmt, ex);
		}
		return null;
	}
	
	private Block generate(Assign s, HashMap<String,Integer> environment) {
		Block blk = null;
		
		if(s.lhs instanceof Expr.AssignedVariable) {			
			blk = localGenerator.generate(s.rhs, environment);			
			Expr.AssignedVariable v = (Expr.AssignedVariable) s.lhs;
			blk.append(Code.Store(v.afterType.raw(), allocate(v.var, environment)),
					attributes(s));			
		} else if(s.lhs instanceof Expr.Tuple) {					
			Expr.Tuple tg = (Expr.Tuple) s.lhs;
			blk = localGenerator.generate(s.rhs, environment);			
			blk.append(Code.Destructure(s.rhs.result().raw()),attributes(s));
			ArrayList<Expr> fields = new ArrayList<Expr>(tg.fields);
			Collections.reverse(fields);
			
			for(Expr e : fields) {
				if(!(e instanceof Expr.AssignedVariable)) {
					syntaxError(errorMessage(INVALID_TUPLE_LVAL),localGenerator.context(),e);
				}
				Expr.AssignedVariable v = (Expr.AssignedVariable) e;
				blk.append(
						Code.Store(v.afterType.raw(),
								allocate(v.var, environment)), attributes(s));
			}
			return blk;
		} else if (s.lhs instanceof Expr.IndexOf
				|| s.lhs instanceof Expr.RecordAccess) {
				
			ArrayList<String> fields = new ArrayList<String>();
			blk = new Block(environment.size());
			Pair<Expr.AssignedVariable,Integer> l = extractLVal(s.lhs,fields,blk,environment);
			Expr.AssignedVariable lhs = l.first();
			if(!environment.containsKey(lhs.var)) {
				syntaxError("unknown variable",localGenerator.context(),l.first());
			}
			int slot = environment.get(lhs.var);
			blk.append(localGenerator.generate(s.rhs, environment));		
			blk.append(Code.Update(lhs.type.raw(),lhs.afterType.raw(),slot,l.second(),fields),
					attributes(s));							
		} else {
			syntaxError("invalid assignment", localGenerator.context(), s);
		}
		
		return blk;
	}

	private Pair<Expr.AssignedVariable, Integer> extractLVal(Expr e,
			ArrayList<String> fields, Block blk, 
			HashMap<String, Integer> environment) {
		if (e instanceof Expr.AssignedVariable) {
			Expr.AssignedVariable v = (Expr.AssignedVariable) e;
			return new Pair(v,0);			
		} else if (e instanceof Expr.Dereference) {
			Expr.Dereference pa = (Expr.Dereference) e;
			Pair<Expr.AssignedVariable,Integer> p = extractLVal(pa.src, fields, blk, environment);
			return new Pair(p.first(),p.second() + 1);			
		} else if (e instanceof Expr.IndexOf) {
			Expr.IndexOf la = (Expr.IndexOf) e;
			Pair<Expr.AssignedVariable,Integer> l = extractLVal(la.src, fields, blk, environment);
			blk.append(localGenerator.generate(la.index, environment));			
			return new Pair(l.first(),l.second() + 1);
		} else if (e instanceof Expr.RecordAccess) {
			Expr.RecordAccess ra = (Expr.RecordAccess) e;
			Pair<Expr.AssignedVariable,Integer> l = extractLVal(ra.src, fields, blk, environment);
			fields.add(ra.name);
			return new Pair(l.first(),l.second() + 1);			
		} else {
			syntaxError(errorMessage(INVALID_LVAL_EXPRESSION), localGenerator.context(), e);
			return null; // dead code
		}
	}
	
	private Block generate(Assert s, HashMap<String,Integer> environment) {
		String lab = Block.freshLabel();
		Block blk = new Block(environment.size());
		blk.append(Code.Assert(lab),attributes(s));
		blk.append(localGenerator.generateCondition(lab, s.expr, environment));		
		blk.append(Code.Fail("assertion failed"), attributes(s));
		blk.append(Code.Label(lab));			
		return blk;
	}

	private Block generate(Return s, HashMap<String,Integer> environment) {

		if (s.expr != null) {
			Block blk = localGenerator.generate(s.expr, environment);
	
			// Here, we don't put the type propagated for the return expression.
			// Instead, we use the declared return type of this function. This
			// has the effect of forcing an implicit coercion between the
			// actual value being returned and its required type. 
			
			Type ret = currentFunDecl.resolvedType().raw().ret();	
			
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
		Block blk = localGenerator.generate(s.expr, environment);		
		blk.append(Code.debug, attributes(s));
		return blk;
	}

	private Block generate(IfElse s, HashMap<String,Integer> environment) {
		String falseLab = Block.freshLabel();
		String exitLab = s.falseBranch.isEmpty() ? falseLab : Block
				.freshLabel();
		Block blk = localGenerator.generateCondition(falseLab, invert(s.condition), environment);

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
		Block blk = localGenerator.generate(s.expr, environment);
		blk.append(Code.Throw(s.expr.result().raw()), s.attributes());
		return blk;
	}
	
	private Block generate(Break s, HashMap<String,Integer> environment) {
		BreakScope scope = findEnclosingScope(BreakScope.class);
		if(scope == null) {
			syntaxError(errorMessage(BREAK_OUTSIDE_LOOP), localGenerator.context(), s);
		}
		Block blk = new Block(environment.size());
		blk.append(Code.Goto(scope.label));
		return blk;
	}
	
	private Block generate(Switch s, HashMap<String,Integer> environment) throws Exception {
		String exitLab = Block.freshLabel();		
		Block blk = localGenerator.generate(s.expr, environment);				
		Block cblk = new Block(environment.size());
		String defaultTarget = exitLab;
		HashSet<Value> values = new HashSet();
		ArrayList<Pair<Value,String>> cases = new ArrayList();	
		
		for(Stmt.Case c : s.cases) {			
			if(c.expr.isEmpty()) {
				// indicates the default block
				if(defaultTarget != exitLab) {
					syntaxError(errorMessage(DUPLICATE_DEFAULT_LABEL),localGenerator.context(),c);
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
						syntaxError(errorMessage(DUPLICATE_CASE_LABEL),localGenerator.context(),c);
					}									
					cases.add(new Pair(constant,target));
					values.add(constant);
				}
				
				for (Stmt st : c.stmts) {
					cblk.append(generate(st, environment));
				}
				cblk.append(Code.Goto(exitLab),attributes(c));
			} else {
				syntaxError(errorMessage(UNREACHABLE_CODE), localGenerator.context(), c);
			}
		}		
		
		blk.append(Code.Switch(s.expr.result().raw(),defaultTarget,cases),attributes(s));
		blk.append(cblk);
		blk.append(Code.Label(exitLab), attributes(s));		
		return blk;
	}
	
	private Block generate(TryCatch s, HashMap<String,Integer> environment) throws Exception {
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
			Type pt = c.type.raw();
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
			blk.append(localGenerator.generateCondition(invariantLabel, s.invariant, environment));		
			blk.append(Code.Fail("loop invariant not satisfied on entry"), attributes(s));
			blk.append(Code.Label(invariantLabel));			
		}
		
		blk.append(Code.Loop(label, Collections.EMPTY_SET),
				attributes(s));
				
		blk.append(localGenerator.generateCondition(label, invert(s.condition), environment));

		scopes.push(new BreakScope(label));		
		for (Stmt st : s.body) {
			blk.append(generate(st, environment));
		}		
		scopes.pop(); // break
		
		if(s.invariant != null) {
			String invariantLabel = Block.freshLabel();
			blk.append(Code.Assert(invariantLabel),attributes(s));
			blk.append(localGenerator.generateCondition(invariantLabel, s.invariant, environment));		
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
			blk.append(localGenerator.generateCondition(invariantLabel, s.invariant, environment));		
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
			blk.append(localGenerator.generateCondition(invariantLabel, s.invariant, environment));		
			blk.append(Code.Fail("loop invariant not restored"), attributes(s));
			blk.append(Code.Label(invariantLabel));			
		}
		
		blk.append(localGenerator.generateCondition(label, invert(s.condition), environment));

		
		blk.append(Code.End(label));

		return blk;
	}
	
	private Block generate(ForAll s, HashMap<String,Integer> environment) {		
		String label = Block.freshLabel();
		
		Block blk = new Block(1);
		
		if(s.invariant != null) {
			String invariantLabel = Block.freshLabel();
			blk.append(Code.Assert(invariantLabel),attributes(s));
			blk.append(localGenerator.generateCondition(invariantLabel, s.invariant, environment));		
			blk.append(Code.Fail("loop invariant not satisfied on entry"), attributes(s));
			blk.append(Code.Label(invariantLabel));			
		}
		
		blk.append(localGenerator.generate(s.source,environment));	
		int freeSlot = allocate(environment);
		if(s.variables.size() > 1) {
			// this is the destructuring case		
			
			// FIXME: loss of nominal information
			Type.EffectiveCollection rawSrcType = s.srcType.raw();
			
			// FIXME: support destructuring of lists and sets			
			if(!(rawSrcType instanceof Type.EffectiveDictionary)) {
				syntaxError(errorMessage(INVALID_DICTIONARY_EXPRESSION),localGenerator.context(),s.source);
			}
			Type.EffectiveDictionary dict = (Type.EffectiveDictionary) rawSrcType;
			Type.Tuple element = (Type.Tuple) Type.Tuple(dict.key(),dict.value());
			List<Type> elements = element.elements();
			blk.append(Code.ForAll((Type.EffectiveDictionary) rawSrcType, freeSlot, label, Collections.EMPTY_SET), attributes(s));
			blk.append(Code.Load(element, freeSlot), attributes(s));
			blk.append(Code.Destructure(element), attributes(s));
			for(int i=s.variables.size();i>0;--i) {
				String var = s.variables.get(i-1);
				int varReg = allocate(var,environment);
				blk.append(Code.Store(elements.get(i-1), varReg), attributes(s));
			}										
		} else {
			// easy case.
			int freeReg = allocate(s.variables.get(0),environment);
			blk.append(Code.ForAll(s.srcType.raw(), freeReg, label, Collections.EMPTY_SET), attributes(s));
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
			blk.append(localGenerator.generateCondition(invariantLabel, s.invariant, environment));		
			blk.append(Code.Fail("loop invariant not restored"), attributes(s));
			blk.append(Code.Label(invariantLabel));			
		}
		blk.append(Code.End(label), attributes(s));		

		return blk;
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
			if(nbop != null) {
				nbop.srcType = bop.srcType;				
				return nbop;
			}
		} else if (e instanceof Expr.UnOp) {
			Expr.UnOp uop = (Expr.UnOp) e;
			switch (uop.op) {
			case NOT:
				return uop.mhs;
			}
		}
		
		Expr.UnOp r = new Expr.UnOp(Expr.UOp.NOT, e);
		r.type = Nominal.T_BOOL;		
		return r;
	}
	
	/**
	 * The shiftBlock method takes a block and shifts every slot a given amount
	 * to the right. The number of inputs remains the same. This method is used 
	 * 
	 * @param amount
	 * @param blk
	 * @return
	 */
	private static Block shiftBlockExceptionZero(int amount, int zeroDest, Block blk) {
		HashMap<Integer,Integer> binding = new HashMap<Integer,Integer>();
		for(int i=1;i!=blk.numSlots();++i) {
			binding.put(i,i+amount);		
		}
		binding.put(0, zeroDest);
		Block nblock = new Block(blk.numInputs());
		for(Block.Entry e : blk) {
			Code code = e.code.remap(binding);
			nblock.append(code,e.attributes());
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
