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

import static wybs.lang.SyntaxError.*;
import static wyil.util.ErrorMessages.*;

import java.util.*;

import wybs.lang.SyntacticElement;
import wybs.lang.SyntaxError;
import wybs.util.ResolveError;
import wyc.builder.*;
import wyc.lang.*;
import wyc.lang.WhileyFile.*;
import wyil.lang.NameID;
import wyil.lang.Type;
import wyil.lang.Value;
import wyil.util.Pair;

/**
 * Propagates type information in a flow-sensitive fashion from declared
 * parameter and return types through assigned expressions, to determine types
 * for all intermediate expressions and variables. For example:
 * 
 * <pre>
 * int sum([int] data):
 *     r = 0          // infers int type for r, based on type of constant
 *     for v in data: // infers int type for v, based on type of data
 *         r = r + v  // infers int type for r, based on type of operands 
 *     return r       // infers int type for r, based on type of r after loop
 * </pre>
 * 
 * The flash points here are the variables <code>r</code> and <code>v</code> as
 * <i>they do not have declared types</i>. Type propagation is responsible for
 * determing their type.
 * 
 * Loops present an interesting challenge for type propagation. Consider this
 * example:
 * 
 * <pre>
 * real loopy(int max):
 *     i = 0
 *     while i < max:
 *         i = i + 0.5
 *     return i
 * </pre>
 * 
 * On the first pass through the loop, variable <code>i</code> is inferred to
 * have type <code>int</code> (based on the type of the constant <code>0</code>
 * ). However, the add expression is inferred to have type <code>real</code>
 * (based on the type of the rhs) and, hence, the resulting type inferred for
 * <code>i</code> is <code>real</code>. At this point, the loop must be
 * reconsidered taking into account this updated type for <code>i</code>.
 *
 * <h3>References</h3>
 * <ul>
 * <li>
 * <p>
 * David J. Pearce and James Noble. Structural and Flow-Sensitive Types for
 * Whiley. Technical Report, Victoria University of Wellington, 2010.
 * </p>
 * </li>
 * </ul>
 * 
 * @author David J. Pearce
 * 
 */
public final class FlowTyping {	
	private final GlobalResolver resolver;
	private ArrayList<Scope> scopes = new ArrayList<Scope>();
	private String filename;
	private WhileyFile.FunctionOrMethodOrMessage current;
	
	public FlowTyping(GlobalResolver resolver) {		
		this.resolver = resolver;
	}
	
	public void propagate(WhileyFile wf) {
		this.filename = wf.filename;
		
		for(WhileyFile.Declaration decl : wf.declarations) {
			try {
				if(decl instanceof FunctionOrMethodOrMessage) {
					propagate((FunctionOrMethodOrMessage)decl);
				} else if(decl instanceof TypeDef) {
					propagate((TypeDef)decl);					
				} else if(decl instanceof Constant) {
					propagate((Constant)decl);					
				}			
			} catch(ResolveError e) {
				syntaxError(errorMessage(RESOLUTION_ERROR,e.getMessage()),filename,decl,e);
			} catch(SyntaxError e) {
				throw e;
			} catch(Throwable t) {
				internalFailure(t.getMessage(),filename,decl,t);
			}
		}
	}
	
	public void propagate(Constant cd) throws Exception {
		NameID nid = new NameID(cd.file().module, cd.name);
		cd.resolvedValue = resolver.resolveAsConstant(nid);
	}
	
	public void propagate(TypeDef td) throws Exception {		
		// first, resolve the declared type
		td.resolvedType = resolver.resolveAsType(td.unresolvedType, td);
		
		if(td.constraint != null) {						
			// second, construct the appropriate typing environment			
			Environment environment = new Environment();
			environment.put("$", td.resolvedType);
			environment = addExposedNames(td.unresolvedType,environment,td);
			// third, propagate type information through the constraint 			
			td.constraint = resolver.resolve(td.constraint,environment,td);
		}
	}

	public void propagate(FunctionOrMethodOrMessage d) throws Exception {		
		this.current = d; // ugly		
		Environment environment = new Environment();					
		
		for (WhileyFile.Parameter p : d.parameters) {							
			environment = environment.put(p.name,resolver.resolveAsType(p.type,d));
		}
		
		if(d instanceof Message) {
			Message md = (Message) d;							
			environment = environment.put("this",resolver.resolveAsType(md.receiver,d));			
		}
		
		if(d.precondition != null) {
			d.precondition = resolver.resolve(d.precondition,environment.clone(),d);
		}
		
		if(d.postcondition != null) {			
			environment = environment.put("$", resolver.resolveAsType(d.ret,d));
			d.postcondition = resolver.resolve(d.postcondition,environment.clone(),d);
			// The following is a little sneaky and helps to avoid unnecessary
			// copying of environments. 
			environment = environment.remove("$");
		}

		if(d instanceof Function) {
			Function f = (Function) d;
			f.resolvedType = resolver.resolveAsType(f.unresolvedType(),d);					
		} else if(d instanceof Method) {
			Method m = (Method) d;			
			m.resolvedType = resolver.resolveAsType(m.unresolvedType(),d);		
		} else {
			Message m = (Message) d;
			m.resolvedType = resolver.resolveAsType(m.unresolvedType(),d);		
		}
		
		propagate(d.statements,environment);
	}
	
	private Environment propagate(
			ArrayList<Stmt> body,
			Environment environment) {
		
		
		for (int i=0;i!=body.size();++i) {
			Stmt stmt = body.get(i);
			if(stmt instanceof Expr) {
				body.set(i,(Stmt) resolver.resolve((Expr)stmt,environment,current));
			} else {
				environment = propagate(stmt, environment);
			}
		}
		
		return environment;
	}
	
	private Environment propagate(Stmt stmt,
			Environment environment) {
				
		try {
			if(stmt instanceof Stmt.Assign) {
				return propagate((Stmt.Assign) stmt,environment);
			} else if(stmt instanceof Stmt.Return) {
				return propagate((Stmt.Return) stmt,environment);
			} else if(stmt instanceof Stmt.IfElse) {
				return propagate((Stmt.IfElse) stmt,environment);
			} else if(stmt instanceof Stmt.While) {
				return propagate((Stmt.While) stmt,environment);
			} else if(stmt instanceof Stmt.ForAll) {
				return propagate((Stmt.ForAll) stmt,environment);
			} else if(stmt instanceof Stmt.Switch) {
				return propagate((Stmt.Switch) stmt,environment);
			} else if(stmt instanceof Stmt.DoWhile) {
				return propagate((Stmt.DoWhile) stmt,environment);
			} else if(stmt instanceof Stmt.Break) {
				return propagate((Stmt.Break) stmt,environment);
			} else if(stmt instanceof Stmt.Throw) {
				return propagate((Stmt.Throw) stmt,environment);
			} else if(stmt instanceof Stmt.TryCatch) {
				return propagate((Stmt.TryCatch) stmt,environment);
			} else if(stmt instanceof Stmt.Assert) {
				return propagate((Stmt.Assert) stmt,environment);
			} else if(stmt instanceof Stmt.Debug) {
				return propagate((Stmt.Debug) stmt,environment);
			} else if(stmt instanceof Stmt.Skip) {
				return propagate((Stmt.Skip) stmt,environment);
			} else {
				internalFailure("unknown statement: " + stmt.getClass().getName(),filename,stmt);
				return null; // deadcode
			}
		} catch(ResolveError e) {
			syntaxError(errorMessage(RESOLUTION_ERROR,e.getMessage()),filename,stmt,e);
			return null; // dead code
		} catch(SyntaxError e) {
			throw e;
		} catch(Throwable e) {
			internalFailure(e.getMessage(),filename,stmt,e);
			return null; // dead code
		}
	}
	
	private Environment propagate(Stmt.Assert stmt,
			Environment environment) {
		stmt.expr = resolver.resolve(stmt.expr,environment,current);
		checkIsSubtype(Type.T_BOOL,stmt.expr);
		return environment;
	}
	
	private Environment propagate(Stmt.Assign stmt,
			Environment environment) throws Exception {
			
		Expr.LVal lhs = stmt.lhs;
		Expr rhs = resolver.resolve(stmt.rhs,environment,current);
		
		if(lhs instanceof Expr.AbstractVariable) {
			// An assignment to a local variable is slightly different from
			// other kinds of assignments. That's because in this case only it
			// is permitted that the variable does not exist a priori.
			// Therefore, whatever type the rhs has, the variable in question
			// will have after the assignment.
			Expr.AbstractVariable av = (Expr.AbstractVariable) lhs;
			Expr.AssignedVariable lv;
			if(lhs instanceof Expr.AssignedVariable) {
				// this case just avoids creating another object everytime we
				// visit this statement.
				lv = (Expr.AssignedVariable) lhs; 
			} else {
				lv = new Expr.AssignedVariable(av.var, av.attributes());
			}
			lv.type = Nominal.T_VOID;
			lv.afterType = rhs.result();			
			environment = environment.put(lv.var, lv.afterType);
			lhs = lv;
		} else if(lhs instanceof Expr.Tuple) {
			// represents a destructuring assignment
			Expr.Tuple tv = (Expr.Tuple) lhs;
			ArrayList<Expr> tvFields = tv.fields;
			
			// FIXME: loss of nominal information here			
			Type rawRhs = rhs.result().raw();		
			Nominal.EffectiveTuple tupleRhs = resolver.expandAsEffectiveTuple(rhs.result());
			
			// FIXME: the following is something of a kludge. It would also be
			// nice to support more expressive destructuring assignment
			// operations.
			if(Type.isImplicitCoerciveSubtype(Type.T_REAL, rawRhs)) {
				tupleRhs = Nominal.Tuple(Nominal.T_INT,Nominal.T_INT);
			} else if(tupleRhs == null) {
				syntaxError("tuple value expected, got " + tupleRhs.nominal(),filename,rhs);
				return null; // deadcode
			} 
			
			List<Nominal> rhsElements = tupleRhs.elements();
			if(rhsElements.size() != tvFields.size()) {
				syntaxError("incompatible tuple assignment",filename,rhs);
			}			
			for(int i=0;i!=tvFields.size();++i) {
				Expr f = tvFields.get(i);
				Nominal t = rhsElements.get(i);
				
				if(f instanceof Expr.AbstractVariable) {
					Expr.AbstractVariable av = (Expr.AbstractVariable) f; 				
					Expr.AssignedVariable lv;
					if(lhs instanceof Expr.AssignedVariable) {
						// this case just avoids creating another object everytime we
						// visit this statement.
						lv = (Expr.AssignedVariable) lhs; 
					} else {
						lv = new Expr.AssignedVariable(av.var, av.attributes());
					}
					lv.type = Nominal.T_VOID;
					lv.afterType = t; 
					environment = environment.put(lv.var, t);					
					tvFields.set(i, lv);
				} else {
					syntaxError(errorMessage(INVALID_TUPLE_LVAL),filename,f);
				}								
			}										
		} else {	
			lhs = propagate(lhs,environment);			
			Expr.AssignedVariable av = inferAfterType(lhs, rhs.result());
			environment = environment.put(av.var, av.afterType);
		}
		
		stmt.lhs = (Expr.LVal) lhs;
		stmt.rhs = rhs;	
		
		return environment;
	}
	
	private Expr.AssignedVariable inferAfterType(Expr.LVal lv,
			Nominal afterType) {
		if (lv instanceof Expr.AssignedVariable) {
			Expr.AssignedVariable v = (Expr.AssignedVariable) lv;			
			v.afterType = afterType;			
			return v;
		} else if (lv instanceof Expr.Dereference) {
			Expr.Dereference pa = (Expr.Dereference) lv;
			// NOTE: the before and after types are the same since an assignment
			// through a reference does not change its type.
			checkIsSubtype(pa.srcType,Nominal.Reference(afterType),lv);
			return inferAfterType((Expr.LVal) pa.src, pa.srcType);
		} else if (lv instanceof Expr.IndexOf) {
			Expr.IndexOf la = (Expr.IndexOf) lv;
			Nominal.EffectiveMap srcType = la.srcType;
			afterType = (Nominal) srcType.update(la.index.result(), afterType);
			return inferAfterType((Expr.LVal) la.src, 
					afterType);
		} else if(lv instanceof Expr.RecordAccess) {
			Expr.RecordAccess la = (Expr.RecordAccess) lv;
			Nominal.EffectiveRecord srcType = la.srcType;			
			// NOTE: I know I can modify this hash map, since it's created fresh
			// in Nominal.Record.fields().
			afterType = (Nominal) srcType.update(la.name, afterType);			
			return inferAfterType((Expr.LVal) la.src, afterType);
		} else {
			internalFailure("unknown lval: "
					+ lv.getClass().getName(), filename, lv);
			return null; //deadcode
		}
	}
	
	private Environment propagate(Stmt.Break stmt,
			Environment environment) {
		// FIXME: need to propagate environment to the break destination
		return BOTTOM;
	}
	
	private Environment propagate(Stmt.Debug stmt,
			Environment environment) {
		stmt.expr = resolver.resolve(stmt.expr,environment,current);				
		checkIsSubtype(Type.T_STRING,stmt.expr);
		return environment;
	}
	
	private Environment propagate(Stmt.DoWhile stmt,
			Environment environment) {
								
		// Iterate to a fixed point
		Environment old = null;
		Environment tmp = null;
		Environment orig = environment.clone();
		boolean firstTime=true;
		do {
			old = environment.clone();
			if(!firstTime) {
				// don't do this on the first go around, to mimick how the
				// do-while loop works.
				tmp = resolver.resolve(stmt.condition,true,old.clone(),current).second();
				environment = join(orig.clone(),propagate(stmt.body,tmp));
			} else {
				firstTime=false;
				environment = join(orig.clone(),propagate(stmt.body,old));
			}					
			old.free(); // hacky, but safe
		} while(!environment.equals(old));

		if (stmt.invariant != null) {
			stmt.invariant = resolver.resolve(stmt.invariant, environment, current);
			checkIsSubtype(Type.T_BOOL,stmt.invariant);
		}		

		Pair<Expr,Environment> p = resolver.resolve(stmt.condition,false,environment,current);
		stmt.condition = p.first();
		environment = p.second();
		
		return environment;
	}
	
	private Environment propagate(Stmt.ForAll stmt,
			Environment environment) throws Exception {
		
		stmt.source = resolver.resolve(stmt.source,environment,current);
		Nominal.EffectiveCollection srcType = resolver.expandAsEffectiveCollection(stmt.source.result()); 		
		stmt.srcType = srcType;
		
		if(srcType == null) {
			syntaxError(errorMessage(INVALID_SET_OR_LIST_EXPRESSION),filename,stmt);
		}
		
		// At this point, the major task is to determine what the types for the
		// iteration variables declared in the for loop. More than one variable
		// is permitted in some cases.
		
		Nominal[] elementTypes = new Nominal[stmt.variables.size()];
		if(elementTypes.length == 2 && srcType instanceof Nominal.EffectiveDictionary) {
			Nominal.EffectiveDictionary dt = (Nominal.EffectiveDictionary) srcType;
			elementTypes[0] = dt.key();
			elementTypes[1] = dt.value();
		} else {			
			if(elementTypes.length == 1) {
				elementTypes[0] = srcType.element();
			} else {
				syntaxError(errorMessage(VARIABLE_POSSIBLY_UNITIALISED),filename,stmt);
			}
		} 		
		
		// Now, update the environment to include those declared variables
		ArrayList<String> stmtVariables = stmt.variables;
		for(int i=0;i!=elementTypes.length;++i) {
			String var = stmtVariables.get(i);
			if (environment.containsKey(var)) {
				syntaxError(errorMessage(VARIABLE_ALREADY_DEFINED,var),
						filename, stmt);
			}			
			environment = environment.put(var, elementTypes[i]);
		} 
				
		// Iterate to a fixed point
		Environment old = null;
		Environment orig = environment.clone();
		do {
			old = environment.clone();
			environment = join(orig.clone(),propagate(stmt.body,old));
			old.free(); // hacky, but safe
		} while(!environment.equals(old));
		
		// Remove loop variables from the environment, since they are only
		// declared for the duration of the body but not beyond.
		for(int i=0;i!=elementTypes.length;++i) {
			String var = stmtVariables.get(i);				
			environment = environment.remove(var);
		} 
		
		if (stmt.invariant != null) {
			stmt.invariant = resolver.resolve(stmt.invariant, environment, current);
			checkIsSubtype(Type.T_BOOL,stmt.invariant);
		}
				
		return environment;
	}
	
	private Environment propagate(Stmt.IfElse stmt,
			Environment environment) {
		
		// First, check condition and apply variable retypings.
		Pair<Expr,Environment> p1,p2;
		
		p1 = resolver.resolve(stmt.condition,true,environment.clone(),current);
		p2 = resolver.resolve(stmt.condition,false,environment,current);
		stmt.condition = p1.first();
		
		Environment trueEnvironment = p1.second();
		Environment falseEnvironment = p2.second();
				
		// Second, update environments for true and false branches
		if(stmt.trueBranch != null && stmt.falseBranch != null) {
			trueEnvironment = propagate(stmt.trueBranch,trueEnvironment);
			falseEnvironment = propagate(stmt.falseBranch,falseEnvironment);						
		} else if(stmt.trueBranch != null) {			
			trueEnvironment = propagate(stmt.trueBranch,trueEnvironment);
		} else if(stmt.falseBranch != null){								
			trueEnvironment = environment;
			falseEnvironment = propagate(stmt.falseBranch,falseEnvironment);		
		} 
		
		// Finally, join results back together		
		return join(trueEnvironment,falseEnvironment);							
	}
	
	private Environment propagate(
			Stmt.Return stmt,
			Environment environment) throws Exception {
		
		if (stmt.expr != null) {
			stmt.expr = resolver.resolve(stmt.expr, environment,current);
			Nominal rhs = stmt.expr.result();
			checkIsSubtype(current.resolvedType().ret(),rhs, stmt.expr);
		}	
		
		environment.free();
		return BOTTOM;
	}
	
	private Environment propagate(Stmt.Skip stmt,
			Environment environment) {		
		return environment;
	}
	
	private Environment propagate(Stmt.Switch stmt,
			Environment environment) throws Exception {
		
		stmt.expr = resolver.resolve(stmt.expr,environment,current);		
		
		Environment finalEnv = null;
		boolean hasDefault = false;
		
		for(Stmt.Case c : stmt.cases) {
			
			// first, resolve the constants
			
			ArrayList<Value> values = new ArrayList<Value>();
			for(Expr e : c.expr) {
				values.add(resolver.resolveAsConstant(e,current));				
			}
			c.constants = values;

			// second, propagate through the statements
			
			Environment localEnv = environment.clone();
			localEnv = propagate(c.stmts,localEnv);
			
			if(finalEnv == null) {
				finalEnv = localEnv;
			} else {
				finalEnv = join(finalEnv,localEnv);
			} 
			
			// third, keep track of whether a default
			hasDefault |= c.expr.isEmpty();
		}
		
		if(!hasDefault) {
			
			// in this case, there is no default case in the switch. We must
			// therefore assume that there are values which will fall right
			// through the switch statement without hitting a case. Therefore,
			// we must include the original environment to accound for this. 
			
			finalEnv = join(finalEnv,environment);
		} else {
			environment.free();
		}
		
		return finalEnv;
	}
	
	private Environment propagate(Stmt.Throw stmt,
			Environment environment) {
		stmt.expr = resolver.resolve(stmt.expr,environment,current);
		return BOTTOM;
	}
	
	private Environment propagate(Stmt.TryCatch stmt,
			Environment environment) throws Exception {
		

		for(Stmt.Catch handler : stmt.catches) {
			
			// FIXME: need to deal with handler environments properly!
			try {
				Nominal type = resolver.resolveAsType(handler.unresolvedType,current); 
				handler.type = type;
				Environment local = environment.clone();
				local = local.put(handler.variable, type);									
				propagate(handler.stmts,local);
				local.free();
			} catch(SyntaxError e) {
				throw e;
			} catch(Throwable t) {
				internalFailure(t.getMessage(),filename,handler,t);
			}
		}
		
		environment = propagate(stmt.body,environment);
				
		// need to do handlers here
		
		return environment;
	}
	
	private Environment propagate(Stmt.While stmt,
			Environment environment) {

		// Iterate to a fixed point
		Environment old = null;
		Environment tmp = null;
		Environment orig = environment.clone();
		do {
			old = environment.clone();
			tmp = resolver.resolve(stmt.condition,true,old.clone(),current).second();			
			environment = join(orig.clone(),propagate(stmt.body,tmp));			
			old.free(); // hacky, but safe
		} while(!environment.equals(old));
		
		if (stmt.invariant != null) {
			stmt.invariant = resolver.resolve(stmt.invariant, environment, current);
			checkIsSubtype(Type.T_BOOL,stmt.invariant);
		}		
				
		Pair<Expr,Environment> p = resolver.resolve(stmt.condition,false,environment,current);
		stmt.condition = p.first();
		environment = p.second();			
		
		return environment;
	}
	
	private Expr.LVal propagate(Expr.LVal lval,
			Environment environment) {
		try {
			if(lval instanceof Expr.AbstractVariable) {
				Expr.AbstractVariable av = (Expr.AbstractVariable) lval;
				Nominal p = environment.get(av.var);
				if(p == null) {
					syntaxError(errorMessage(UNKNOWN_VARIABLE),filename,lval);
				}				
				Expr.AssignedVariable lv = new Expr.AssignedVariable(av.var, av.attributes());
				lv.type = p;				
				return lv;
			} else if(lval instanceof Expr.Dereference) {
				Expr.Dereference pa = (Expr.Dereference) lval;
				Expr.LVal src = propagate((Expr.LVal) pa.src,environment);												
				pa.src = src;
				pa.srcType = resolver.expandAsReference(src.result());							
				return pa;
			} else if(lval instanceof Expr.IndexOf) {
				// this indicates either a list, string or dictionary update
				Expr.IndexOf ai = (Expr.IndexOf) lval;				
				Expr.LVal src = propagate((Expr.LVal) ai.src,environment);				
				Expr index = resolver.resolve(ai.index,environment,current);
				ai.src = src;
				ai.index = index;
				Nominal.EffectiveMap srcType = resolver.expandAsEffectiveMap(src.result());
				if(srcType == null) {
					syntaxError(errorMessage(INVALID_LVAL_EXPRESSION),filename,lval);
				}
				ai.srcType = srcType;
				return ai;
			} else if(lval instanceof Expr.AbstractDotAccess) {
				// this indicates a record update
				Expr.AbstractDotAccess ad = (Expr.AbstractDotAccess) lval;
				Expr.LVal src = propagate((Expr.LVal) ad.src,environment);
				Expr.RecordAccess ra = new Expr.RecordAccess(src, ad.name, ad.attributes());
				Nominal.EffectiveRecord srcType = resolver.expandAsEffectiveRecord(src.result());
				if(srcType == null) {								
					syntaxError(errorMessage(INVALID_LVAL_EXPRESSION),filename,lval);					
				} else if(srcType.field(ra.name) == null) {
					syntaxError(errorMessage(RECORD_MISSING_FIELD,ra.name),filename,lval);
				}
				ra.srcType = srcType;
				return ra;
			}
		} catch(SyntaxError e) {
			throw e;
		} catch(Throwable e) {
			internalFailure(e.getMessage(),filename,lval,e);
			return null; // dead code
		}		
		internalFailure("unknown lval: " + lval.getClass().getName(),filename,lval);
		return null; // dead code
	}			
	
	// Check t1 :> t2
	private void checkIsSubtype(Nominal t1, Nominal t2,
			SyntacticElement elem) {
		if (!Type.isImplicitCoerciveSubtype(t1.raw(), t2.raw())) {
			syntaxError(
					errorMessage(SUBTYPE_ERROR, t1.nominal(), t2.nominal()),
					filename, elem);
		}
	}	
	
	private void checkIsSubtype(Nominal t1, Expr t2) {
		if (!Type.isImplicitCoerciveSubtype(t1.raw(), t2.result().raw())) {
			// We use the nominal type for error reporting, since this includes
			// more helpful names.
			syntaxError(
					errorMessage(SUBTYPE_ERROR, t1.nominal(), t2.result()
							.nominal()), filename, t2);
		}
	}
	
	private void checkIsSubtype(Type t1, Expr t2) {
		if (!Type.isImplicitCoerciveSubtype(t1, t2.result().raw())) {
			// We use the nominal type for error reporting, since this includes
			// more helpful names.
			syntaxError(errorMessage(SUBTYPE_ERROR, t1, t2.result().nominal()),
					filename, t2);
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
	private Environment addExposedNames(UnresolvedType t,
			Environment environment, Context context) {
		// Extended this method to handle lists and sets etc, is very difficult.
		// The primary problem is that we need to expand expressions involved
		// names exposed in this way into quantified
		// expressions.		
		if(t instanceof UnresolvedType.Record) {
			UnresolvedType.Record tt = (UnresolvedType.Record) t;
			for(Map.Entry<String,UnresolvedType> e : tt.types.entrySet()) {
				Nominal alias = environment.get(e.getKey());
				if(alias == null) {
					alias = resolver.resolveAsType(e.getValue(),context);
					environment = environment.put(e.getKey(),alias);
				}				
			}
		} 
		
		return environment;
	}
	
	private abstract static class Scope {
		public abstract void free();
	}
	
	private static final class Handler {
		public final Type exception;
		public final String variable;
		public Environment environment;
		
		public Handler(Type exception, String variable) {
			this.exception = exception;
			this.variable = variable;
			this.environment = new Environment();
		}
	}
	
	private static final class TryCatchScope extends Scope {
		public final ArrayList<Handler> handlers = new ArrayList<Handler>();
						
		public void free() {
			for(Handler handler : handlers) {
				handler.environment.free();
			}
		}
	}
	
	private static final class BreakScope extends Scope {
		public Environment environment;
		
		public void free() {
			environment.free();
		}
	}

	private static final class ContinueScope extends Scope {
		public Environment environment;
		
		public void free() {
			environment.free();
		}
	}
	
	private static final Environment BOTTOM = new Environment();
	
	private static final Environment join(Environment lhs,Environment rhs) {
		
		// first, need to check for the special bottom value case.
		
		if(lhs == BOTTOM) {
			return rhs;
		} else if(rhs == BOTTOM) {
			return lhs;
		}
		
		// ok, not bottom so compute intersection.
		
		lhs.free();
		rhs.free(); 		
		
		Environment result = new Environment();
		for(String key : lhs.keySet()) {
			if(rhs.containsKey(key)) {
				Nominal lhs_t = lhs.get(key);
				Nominal rhs_t = rhs.get(key);				
				result.put(key, Nominal.Union(lhs_t, rhs_t));
			}
		}
		
		return result;
	}	
}
