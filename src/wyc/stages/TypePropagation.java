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

import static wyil.util.SyntaxError.*;
import static wyil.util.ErrorMessages.*;

import java.util.*;

import wyc.Resolver;
import wyc.lang.*;
import wyc.lang.WhileyFile.*;
import wyc.util.Nominal;
import wyc.util.RefCountedHashMap;
import wyil.ModuleLoader;
import wyil.lang.Attribute;
import wyil.lang.ModuleID;
import wyil.lang.NameID;
import wyil.lang.PkgID;
import wyil.lang.Type;
import wyil.lang.Value;
import wyil.util.Pair;
import wyil.util.ResolveError;
import wyil.util.SyntacticElement;
import wyil.util.SyntaxError;

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
 * In some cases, this process must update the underlying expressions to reflect
 * the correct operator. For example:
 * 
 * <pre>
 * {int} f({int} x, {int} y):
 *    return x+y
 * </pre>
 * 
 * Initially, the expression <code>x+y</code> is assumed to be arithmetic
 * addition. During type propagation, however, it becomes apparent that its
 * operands are both sets. Therefore, the underlying AST node is updated to
 * represent a set union.
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
public final class TypePropagation {
	private final ModuleLoader loader;
	private final Resolver resolver;
	private ArrayList<Scope> scopes = new ArrayList<Scope>();
	private String filename;
	private WhileyFile.FunctionOrMethodOrMessage current;
	
	public TypePropagation(ModuleLoader loader, Resolver resolver) {
		this.loader = loader;
		this.resolver = resolver;
	}
	
	public void propagate(WhileyFile wf) {
		this.filename = wf.filename;
		
		ModuleID mid = wf.module;
		ArrayList<WhileyFile.Import> imports = new ArrayList<WhileyFile.Import>();
		
		imports.add(new WhileyFile.Import(mid.pkg(), mid.module(), "*")); 
		// other import statements are inserted here
		imports.add(new WhileyFile.Import(mid.pkg(), "*", null)); 
		imports.add(new WhileyFile.Import(new PkgID("whiley","lang"), "*", null)); 
				
		for(WhileyFile.Declaration decl : wf.declarations) {
			try {
				if (decl instanceof Import) {
					Import impd = (Import) decl;
					// insert import statement into the appropriate space
					// between that for this file, and those for its package and
					// whiley.lang (see above).
					imports.add(1, impd);
				} else if(decl instanceof FunctionOrMethodOrMessage) {
					propagate((FunctionOrMethodOrMessage)decl,imports);
				} else if(decl instanceof TypeDef) {
					propagate((TypeDef)decl,imports);					
				} else if(decl instanceof Constant) {
					propagate((Constant)decl,wf,imports);					
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
	
	public void propagate(Constant cd, WhileyFile wf,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {
		NameID nid = new NameID(wf.module, cd.name);
		cd.resolvedValue = resolver.resolveAsConstant(nid);
	}
	
	public void propagate(TypeDef td, ArrayList<WhileyFile.Import> imports) throws ResolveError {		
		// first, expand the declared type
		td.resolvedType = resolver.resolveAsType(td.unresolvedType, imports,filename);
		
		if(td.constraint != null) {						
			// second, construct the appropriate typing environment			
			RefCountedHashMap<String,Nominal> environment = new RefCountedHashMap<String,Nominal>();
			environment.put("$", td.resolvedType);
			
			// FIXME: add names exposed from records and other types
			
			// third, propagate type information through the constraint 
			propagate(td.constraint,environment,imports);
		}
	}

	public void propagate(FunctionOrMethodOrMessage d, ArrayList<WhileyFile.Import> imports) throws ResolveError {
		this.current = d;
		RefCountedHashMap<String,Nominal> environment = new RefCountedHashMap<String,Nominal>();
						
		for (WhileyFile.Parameter p : d.parameters) {							
			environment = environment.put(p.name,resolver.resolveAsType(p.type,imports,filename));
		}
		
		if(d instanceof Message) {
			Message md = (Message) d;							
			environment = environment.put("this",resolver.resolveAsType(md.receiver,imports,filename));			
		}
		
		if(d.precondition != null) {
			propagate(d.precondition,environment.clone(),imports);
		}
		
		if(d.postcondition != null) {			
			environment = environment.put("$", resolver.resolveAsType(d.ret,imports,filename));
			propagate(d.postcondition,environment.clone(),imports);
			// The following is a little sneaky and helps to avoid unnecessary
			// copying of environments. 
			environment = environment.remove("$");
		}

		if(d instanceof Function) {
			Function f = (Function) d;
			f.resolvedType = resolver.resolveAsType(f.unresolvedType(),imports,filename);					
		} else if(d instanceof Method) {
			Method m = (Method) d;			
			m.resolvedType = resolver.resolveAsType(m.unresolvedType(),imports,filename);		
		} else {
			Message m = (Message) d;
			m.resolvedType = resolver.resolveAsType(m.unresolvedType(),imports,filename);		
		}
		
		propagate(d.statements,environment,imports);
	}
	
	private RefCountedHashMap<String, Nominal> propagate(
			ArrayList<Stmt> body,
			RefCountedHashMap<String, Nominal> environment,
			ArrayList<WhileyFile.Import> imports) {
		
		
		for (int i=0;i!=body.size();++i) {
			Stmt stmt = body.get(i);
			if(stmt instanceof Expr) {
				body.set(i,(Stmt) propagate((Expr)stmt,environment,imports));
			} else {
				environment = propagate(stmt, environment, imports);
			}
		}
		
		return environment;
	}
	
	private RefCountedHashMap<String,Nominal> propagate(Stmt stmt,
			RefCountedHashMap<String, Nominal> environment,
			ArrayList<WhileyFile.Import> imports) {
				
		try {
			if(stmt instanceof Stmt.Assign) {
				return propagate((Stmt.Assign) stmt,environment,imports);
			} else if(stmt instanceof Stmt.Return) {
				return propagate((Stmt.Return) stmt,environment,imports);
			} else if(stmt instanceof Stmt.IfElse) {
				return propagate((Stmt.IfElse) stmt,environment,imports);
			} else if(stmt instanceof Stmt.While) {
				return propagate((Stmt.While) stmt,environment,imports);
			} else if(stmt instanceof Stmt.ForAll) {
				return propagate((Stmt.ForAll) stmt,environment,imports);
			} else if(stmt instanceof Stmt.Switch) {
				return propagate((Stmt.Switch) stmt,environment,imports);
			} else if(stmt instanceof Stmt.DoWhile) {
				return propagate((Stmt.DoWhile) stmt,environment,imports);
			} else if(stmt instanceof Stmt.Break) {
				return propagate((Stmt.Break) stmt,environment,imports);
			} else if(stmt instanceof Stmt.Throw) {
				return propagate((Stmt.Throw) stmt,environment,imports);
			} else if(stmt instanceof Stmt.TryCatch) {
				return propagate((Stmt.TryCatch) stmt,environment,imports);
			} else if(stmt instanceof Stmt.Assert) {
				return propagate((Stmt.Assert) stmt,environment,imports);
			} else if(stmt instanceof Stmt.Debug) {
				return propagate((Stmt.Debug) stmt,environment,imports);
			} else if(stmt instanceof Stmt.Skip) {
				return propagate((Stmt.Skip) stmt,environment,imports);
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
	
	private RefCountedHashMap<String,Nominal> propagate(Stmt.Assert stmt,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) {
		stmt.expr = propagate(stmt.expr,environment,imports);
		checkIsSubtype(Type.T_BOOL,stmt.expr);
		return environment;
	}
	
	private RefCountedHashMap<String,Nominal> propagate(Stmt.Assign stmt,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {
			
		Expr.LVal lhs = stmt.lhs;
		Expr rhs = propagate(stmt.rhs,environment,imports);
		
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
			lhs = propagate(lhs,environment,imports);			
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
		} else if (lv instanceof Expr.StringAccess) {
			Expr.StringAccess la = (Expr.StringAccess) lv;
			checkIsSubtype(Nominal.T_CHAR,afterType,lv);
			return inferAfterType((Expr.LVal) la.src, 
					Nominal.T_STRING);
		} else if (lv instanceof Expr.ListAccess) {
			Expr.ListAccess la = (Expr.ListAccess) lv;
			Nominal.EffectiveList srcType = la.srcType;
			afterType = (Nominal) srcType.update(afterType);								
			return inferAfterType((Expr.LVal) la.src, afterType);
		} else if(lv instanceof Expr.DictionaryAccess)  {
			Expr.DictionaryAccess da = (Expr.DictionaryAccess) lv;		
			Nominal.EffectiveDictionary srcType = da.srcType;
			afterType = (Nominal) srcType.update(da.index.result(),afterType);
			return inferAfterType((Expr.LVal) da.src, afterType);
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
	
	private RefCountedHashMap<String,Nominal> propagate(Stmt.Break stmt,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) {
		// FIXME: need to propagate environment to the break destination
		return BOTTOM;
	}
	
	private RefCountedHashMap<String,Nominal> propagate(Stmt.Debug stmt,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) {
		stmt.expr = propagate(stmt.expr,environment,imports);				
		checkIsSubtype(Type.T_STRING,stmt.expr);
		return environment;
	}
	
	private RefCountedHashMap<String,Nominal> propagate(Stmt.DoWhile stmt,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) {
								
		// Iterate to a fixed point
		RefCountedHashMap<String,Nominal> old = null;
		RefCountedHashMap<String,Nominal> tmp = null;
		RefCountedHashMap<String,Nominal> orig = environment.clone();
		boolean firstTime=true;
		do {
			old = environment.clone();
			if(!firstTime) {
				// don't do this on the first go around, to mimick how the
				// do-while loop works.
				tmp = propagate(stmt.condition,true,old.clone(),imports).second();
				environment = join(orig.clone(),propagate(stmt.body,tmp,imports));
			} else {
				firstTime=false;
				environment = join(orig.clone(),propagate(stmt.body,old,imports));
			}					
			old.free(); // hacky, but safe
		} while(!environment.equals(old));

		if (stmt.invariant != null) {
			stmt.invariant = propagate(stmt.invariant, environment, imports);
			checkIsSubtype(Type.T_BOOL,stmt.invariant);
		}		

		Pair<Expr,RefCountedHashMap<String,Nominal>> p = propagate(stmt.condition,false,environment,imports);
		stmt.condition = p.first();
		environment = p.second();
		
		return environment;
	}
	
	private RefCountedHashMap<String,Nominal> propagate(Stmt.ForAll stmt,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {
		
		stmt.source = propagate(stmt.source,environment,imports);
		Type rawType = stmt.source.result().raw(); 		
		
		// At this point, the major task is to determine what the types for the
		// iteration variables declared in the for loop. More than one variable
		// is permitted in some cases.
		
		Nominal[] elementTypes = new Nominal[stmt.variables.size()];		
		if(Type.isSubtype(Type.List(Type.T_ANY, false),rawType)) {			
			Nominal.EffectiveList lt = resolver.expandAsEffectiveList(stmt.source.result());
			if(elementTypes.length == 1) {
				elementTypes[0] = lt.element();
			} else {
				syntaxError(errorMessage(VARIABLE_POSSIBLY_UNITIALISED),filename,stmt);
			}			
		} else if(Type.isSubtype(Type.Set(Type.T_ANY, false),rawType)) {
			Nominal.EffectiveSet st = resolver.expandAsEffectiveSet(stmt.source.result());
			if(elementTypes.length == 1) {
				elementTypes[0] = st.element();
			} else {
				syntaxError(errorMessage(VARIABLE_POSSIBLY_UNITIALISED),filename,stmt);
			}					
		} else if(Type.isSubtype(Type.Dictionary(Type.T_ANY, Type.T_ANY),rawType)) {
			Nominal.EffectiveDictionary dt = resolver.expandAsEffectiveDictionary(stmt.source.result());
			if(elementTypes.length == 1) {
				elementTypes[0] = Nominal.Tuple(dt.key(),dt.value());			
			} else if(elementTypes.length == 2) {					
				elementTypes[0] = dt.key();
				elementTypes[1] = dt.value();
			} else {
				syntaxError(errorMessage(VARIABLE_POSSIBLY_UNITIALISED),filename,stmt);
			}
			
		} else if(Type.isSubtype(Type.T_STRING,rawType)) {
			if(elementTypes.length == 1) {
				elementTypes[0] = Nominal.T_CHAR;
			} else {
				syntaxError(errorMessage(VARIABLE_POSSIBLY_UNITIALISED),filename,stmt);
			}				
		} else {
			syntaxError(errorMessage(INVALID_SET_OR_LIST_EXPRESSION),filename,stmt);
			return null; // deadcode
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
		RefCountedHashMap<String,Nominal> old = null;
		RefCountedHashMap<String,Nominal> orig = environment.clone();
		do {
			old = environment.clone();
			environment = join(orig.clone(),propagate(stmt.body,old,imports));
			old.free(); // hacky, but safe
		} while(!environment.equals(old));
		
		// Remove loop variables from the environment, since they are only
		// declared for the duration of the body but not beyond.
		for(int i=0;i!=elementTypes.length;++i) {
			String var = stmtVariables.get(i);				
			environment = environment.remove(var);
		} 
		
		if (stmt.invariant != null) {
			stmt.invariant = propagate(stmt.invariant, environment, imports);
			checkIsSubtype(Type.T_BOOL,stmt.invariant);
		}
				
		return environment;
	}
	
	private RefCountedHashMap<String,Nominal> propagate(Stmt.IfElse stmt,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) {
		
		// First, check condition and apply variable retypings.
		Pair<Expr,RefCountedHashMap<String,Nominal>> p1,p2;
		
		p1 = propagate(stmt.condition,true,environment.clone(),imports);
		p2 = propagate(stmt.condition,false,environment,imports);
		stmt.condition = p1.first();
		
		RefCountedHashMap<String,Nominal> trueEnvironment = p1.second();
		RefCountedHashMap<String,Nominal> falseEnvironment = p2.second();
				
		// Second, update environments for true and false branches
		if(stmt.trueBranch != null && stmt.falseBranch != null) {
			trueEnvironment = propagate(stmt.trueBranch,trueEnvironment,imports);
			falseEnvironment = propagate(stmt.falseBranch,falseEnvironment,imports);						
		} else if(stmt.trueBranch != null) {			
			trueEnvironment = propagate(stmt.trueBranch,trueEnvironment,imports);
		} else if(stmt.falseBranch != null){								
			trueEnvironment = environment;
			falseEnvironment = propagate(stmt.falseBranch,falseEnvironment,imports);		
		} 
		
		// Finally, join results back together		
		return join(trueEnvironment,falseEnvironment);							
	}
	
	private RefCountedHashMap<String, Nominal> propagate(
			Stmt.Return stmt,
			RefCountedHashMap<String, Nominal> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {
		
		if (stmt.expr != null) {
			stmt.expr = propagate(stmt.expr, environment,imports);
			Nominal rhs = stmt.expr.result();
			checkIsSubtype(current.resolvedType().ret(),rhs, stmt.expr);
		}	
		
		environment.free();
		return BOTTOM;
	}
	
	private RefCountedHashMap<String,Nominal> propagate(Stmt.Skip stmt,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) {		
		return environment;
	}
	
	private RefCountedHashMap<String,Nominal> propagate(Stmt.Switch stmt,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {
		
		stmt.expr = propagate(stmt.expr,environment,imports);		
		
		RefCountedHashMap<String,Nominal> finalEnv = null;
		boolean hasDefault = false;
		
		for(Stmt.Case c : stmt.cases) {
			
			// first, resolve the constants
			
			ArrayList<Value> values = new ArrayList<Value>();
			for(Expr e : c.expr) {
				values.add(resolver.resolveAsConstant(e,filename,imports));				
			}
			c.constants = values;

			// second, propagate through the statements
			
			RefCountedHashMap<String,Nominal> localEnv = environment.clone();
			localEnv = propagate(c.stmts,localEnv,imports);
			
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
	
	private RefCountedHashMap<String,Nominal> propagate(Stmt.Throw stmt,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) {
		stmt.expr = propagate(stmt.expr,environment,imports);
		return BOTTOM;
	}
	
	private RefCountedHashMap<String,Nominal> propagate(Stmt.TryCatch stmt,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {
		

		for(Stmt.Catch handler : stmt.catches) {
			
			// FIXME: need to deal with handler environments properly!
			try {
				Nominal type = resolver.resolveAsType(handler.unresolvedType, imports, filename); 
				handler.type = type;
				RefCountedHashMap<String,Nominal> local = environment.clone();
				local = local.put(handler.variable, type);									
				propagate(handler.stmts,local,imports);
				local.free();
			} catch(ResolveError e) {
				syntaxError(errorMessage(RESOLUTION_ERROR,e.getMessage()),filename,handler,e);
			} catch(SyntaxError e) {
				throw e;
			} catch(Throwable t) {
				internalFailure(t.getMessage(),filename,handler,t);
			}
		}
		
		environment = propagate(stmt.body,environment,imports);
		
		
		// need to do handlers here
		
		return environment;
	}
	
	private RefCountedHashMap<String,Nominal> propagate(Stmt.While stmt,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) {

		// Iterate to a fixed point
		RefCountedHashMap<String,Nominal> old = null;
		RefCountedHashMap<String,Nominal> tmp = null;
		RefCountedHashMap<String,Nominal> orig = environment.clone();
		do {
			old = environment.clone();
			tmp = propagate(stmt.condition,true,old.clone(),imports).second();			
			environment = join(orig.clone(),propagate(stmt.body,tmp,imports));			
			old.free(); // hacky, but safe
		} while(!environment.equals(old));
		
		if (stmt.invariant != null) {
			stmt.invariant = propagate(stmt.invariant, environment, imports);
			checkIsSubtype(Type.T_BOOL,stmt.invariant);
		}		
				
		Pair<Expr,RefCountedHashMap<String,Nominal>> p = propagate(stmt.condition,false,environment,imports);
		stmt.condition = p.first();
		environment = p.second();			
		
		return environment;
	}
	
	private Expr.LVal propagate(Expr.LVal lval,
			RefCountedHashMap<String, Nominal> environment,
			ArrayList<WhileyFile.Import> imports) {
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
				Expr.LVal src = propagate((Expr.LVal) pa.src,environment,imports);												
				pa.src = src;
				pa.srcType = resolver.expandAsReference(src.result());							
				return pa;
			} else if(lval instanceof Expr.AbstractIndexAccess) {
				// this indicates either a list, string or dictionary update
				Expr.AbstractIndexAccess ai = (Expr.AbstractIndexAccess) lval;				
				Expr.LVal src = propagate((Expr.LVal) ai.src,environment,imports);				
				Expr index = propagate(ai.index,environment,imports);				
				Type rawSrcType = src.result().raw();
				// FIXME: problem if list is only an effective list, similarly
				// for dictionaries.
				if(Type.isSubtype(Type.T_STRING, rawSrcType)) {
					return new Expr.StringAccess(src,index,lval.attributes());
				} else if(Type.isSubtype(Type.List(Type.T_ANY,false), rawSrcType)) {
					Expr.ListAccess la = new Expr.ListAccess(src,index,lval.attributes());
					la.srcType = resolver.expandAsEffectiveList(src.result()); 			
					return la;
				} else  if(Type.isSubtype(Type.Dictionary(Type.T_ANY, Type.T_ANY), rawSrcType)) {
					Expr.DictionaryAccess da = new Expr.DictionaryAccess(src,index,lval.attributes());
					da.srcType = resolver.expandAsEffectiveDictionary(src.result());										
					return da;
				} else {				
					syntaxError(errorMessage(INVALID_LVAL_EXPRESSION),filename,lval);
				}
			} else if(lval instanceof Expr.AbstractDotAccess) {
				// this indicates a record update
				Expr.AbstractDotAccess ad = (Expr.AbstractDotAccess) lval;
				Expr.LVal src = propagate((Expr.LVal) ad.src,environment,imports);
				Expr.RecordAccess ra = new Expr.RecordAccess(src, ad.name, ad.attributes());
				Nominal.EffectiveRecord srcType = resolver.expandAsEffectiveRecord(src.result());
				if(srcType == null) {								
					syntaxError(errorMessage(INVALID_LVAL_EXPRESSION),filename,lval);					
				} else if(srcType.field(ra.name) == null) {
					syntaxError(errorMessage(RECORD_MISSING_FIELD),filename,lval);
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
	

	private Pair<Expr, RefCountedHashMap<String, Nominal>> propagate(
			Expr expr, boolean sign,
			RefCountedHashMap<String, Nominal> environment,
			ArrayList<WhileyFile.Import> imports) {
		
		if(expr instanceof Expr.UnOp) {
			return propagate((Expr.UnOp)expr,sign,environment,imports);		
		} else if(expr instanceof Expr.BinOp) {  
			return propagate((Expr.BinOp)expr,sign,environment,imports);
		} else {
			// for all others just default back to the base rules for expressions.
			expr = propagate(expr,environment,imports);
			checkIsSubtype(Type.T_BOOL,expr);
			return new Pair(expr,environment);
		}		
	}
	
	private Pair<Expr, RefCountedHashMap<String, Nominal>> propagate(
			Expr.UnOp expr,
			boolean sign,
			RefCountedHashMap<String, Nominal> environment,
			ArrayList<WhileyFile.Import> imports) {
		Expr.UnOp uop = (Expr.UnOp) expr; 
		if(uop.op == Expr.UOp.NOT) { 
			Pair<Expr,RefCountedHashMap<String, Nominal>> p = propagate(uop.mhs,!sign,environment,imports);
			uop.mhs = p.first();			
			checkIsSubtype(Type.T_BOOL,uop.mhs);
			uop.type = Nominal.T_BOOL;
			return new Pair(uop,p.second());
		} else {
			syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION),filename,expr);
			return null; // deadcode
		}	
	}
	
	private Pair<Expr, RefCountedHashMap<String, Nominal>> propagate(
			Expr.BinOp bop,
			boolean sign,
			RefCountedHashMap<String, Nominal> environment,
			ArrayList<WhileyFile.Import> imports) {		
		Expr.BOp op = bop.op;
		
		switch (op) {
		case AND:
		case OR:
		case XOR:
			return propagateNonLeafCondition(bop,sign,environment,imports);
		case EQ:
		case NEQ:
		case LT:
		case LTEQ:
		case GT:
		case GTEQ:
		case ELEMENTOF:
		case SUBSET:
		case SUBSETEQ:
		case IS:
			return propagateLeafCondition(bop,sign,environment,imports);
		default:
			syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), filename, bop);
			return null; // dead code
		}		
	}
	
	private Pair<Expr, RefCountedHashMap<String, Nominal>> propagateNonLeafCondition(
			Expr.BinOp bop,
			boolean sign,
			RefCountedHashMap<String, Nominal> environment,
			ArrayList<WhileyFile.Import> imports) {
		Expr.BOp op = bop.op;
		Pair<Expr,RefCountedHashMap<String, Nominal>> p;
		boolean followOn = (sign && op == Expr.BOp.AND) || (!sign && op == Expr.BOp.OR);
		
		if(followOn) {			
			p = propagate(bop.lhs,sign,environment.clone(),imports);			
			bop.lhs = p.first();
			p = propagate(bop.rhs,sign,p.second(),imports);
			bop.rhs = p.first();
			environment = p.second();
		} else {
			// We could do better here
			p = propagate(bop.lhs,sign,environment.clone(),imports);
			bop.lhs = p.first();
			RefCountedHashMap<String, Nominal> local = p.second();
			// Recompue the lhs assuming that it is false. This is necessary to
			// generate the right environment going into the rhs, which is only
			// evaluated if the lhs is false.  For example:
			//
			// if(e is int && e > 0):
			//     //
			// else:
			//     // <-
			// 
			// In the false branch, we're determing the environment for 
			// !(e is int && e > 0).  This becomes !(e is int) || (e > 0) where 
			// on the rhs we require (e is int).
			p = propagate(bop.lhs,!sign,environment.clone(),imports);
			p = propagate(bop.rhs,sign,p.second(),imports);
			bop.rhs = p.first();
			environment = join(local,p.second());
		}
		
		checkIsSubtype(Type.T_BOOL,bop.lhs);
		checkIsSubtype(Type.T_BOOL,bop.rhs);	
		bop.srcType = Nominal.T_BOOL;
		
		return new Pair<Expr,RefCountedHashMap<String, Nominal>>(bop,environment);
	}
	
	private Pair<Expr, RefCountedHashMap<String, Nominal>> propagateLeafCondition(
			Expr.BinOp bop,
			boolean sign,
			RefCountedHashMap<String, Nominal> environment,
			ArrayList<WhileyFile.Import> imports) {
		Expr.BOp op = bop.op;
		
		Expr lhs = propagate(bop.lhs,environment,imports);
		Expr rhs = propagate(bop.rhs,environment,imports);
		bop.lhs = lhs;
		bop.rhs = rhs;
		
		Type lhsRawType = lhs.result().raw();
		Type rhsRawType = rhs.result().raw();
		
		switch(op) {					
		case IS:
			// this one is slightly more difficult. In the special case that
			// we have a type constant on the right-hand side then we want
			// to check that it makes sense. Otherwise, we just check that
			// it has type meta.								
			
			if(rhs instanceof Expr.TypeVal) {									
				// yes, right-hand side is a constant
				Expr.TypeVal tv = (Expr.TypeVal) rhs;
				Type testRawType = tv.type.raw();					
				Nominal glb = Nominal.intersect(lhs.result(), tv.type);	
				
				if(Type.isSubtype(testRawType,lhsRawType)) {					
					// DEFINITE TRUE CASE										
					syntaxError(errorMessage(BRANCH_ALWAYS_TAKEN), filename, bop);
				} else if (glb.raw() == Type.T_VOID) {				
					// DEFINITE FALSE CASE	
					syntaxError(errorMessage(INCOMPARABLE_OPERANDS, lhsRawType, testRawType),
							filename, bop);			
				} 
				
				// Finally, if the lhs is local variable then update its
				// type in the resulting environment. 
				if(lhs instanceof Expr.LocalVariable) {
					Expr.LocalVariable lv = (Expr.LocalVariable) lhs;
					Nominal newType;
					if(sign) {
						newType = glb;
					} else {						
						newType = Nominal.intersect(lhs.result(), Nominal.Negation(tv.type));						
					}										
					environment = environment.put(lv.var,newType);
				}
			} else {
				// In this case, we can't update the type of the lhs since
				// we don't know anything about the rhs. It may be possible
				// to support bounds here in order to do that, but frankly
				// that's future work :)
				checkIsSubtype(Type.T_META,rhs);
			}	

			bop.srcType = lhs.result();
			break;
		case ELEMENTOF:			
			Type.EffectiveList listType = rhsRawType instanceof Type.EffectiveList ? (Type.EffectiveList) rhsRawType : null;
			Type.EffectiveSet setType = rhsRawType instanceof Type.EffectiveSet ? (Type.EffectiveSet) rhsRawType : null;			
			
			if (listType != null && !Type.isImplicitCoerciveSubtype(listType.element(), lhsRawType)) {
				syntaxError(errorMessage(INCOMPARABLE_OPERANDS, lhsRawType,listType.element()),
						filename, bop);
			} else if (setType != null && !Type.isImplicitCoerciveSubtype(setType.element(), lhsRawType)) {
				syntaxError(errorMessage(INCOMPARABLE_OPERANDS, lhsRawType,setType.element()),
						filename, bop);
			}						
			bop.srcType = rhs.result();
			break;
		case SUBSET:
		case SUBSETEQ:
		case LT:
		case LTEQ:
		case GTEQ:
		case GT:
			if(op == Expr.BOp.SUBSET || op == Expr.BOp.SUBSETEQ) {
				checkIsSubtype(Type.Set(Type.T_ANY,false),lhs);
				checkIsSubtype(Type.Set(Type.T_ANY,false),rhs);
			} else {
				checkIsSubtype(Type.T_REAL,lhs);
				checkIsSubtype(Type.T_REAL,rhs);
			}
			if(Type.isImplicitCoerciveSubtype(lhsRawType,rhsRawType)) {
				bop.srcType = lhs.result();
			} else if(Type.isImplicitCoerciveSubtype(rhsRawType,lhsRawType)) {
				bop.srcType = rhs.result();
			} else {
				syntaxError(errorMessage(INCOMPARABLE_OPERANDS),filename,bop);	
				return null; // dead code
			}	
			break;
		case NEQ:
			// following is a sneaky trick for the special case below
			sign = !sign;
		case EQ:		
			
			// first, check for special case of e.g. x != null. This is then
			// treated the same as !(x is null) 
			
			if (lhs instanceof Expr.LocalVariable
					&& rhs instanceof Expr.Constant
					&& ((Expr.Constant) rhs).value == Value.V_NULL) {
				// bingo, special case
				Expr.LocalVariable lv = (Expr.LocalVariable) lhs;
				Nominal newType;
				Nominal glb = Nominal.intersect(lhs.result(), Nominal.T_NULL);
				if(glb.raw() == Type.T_VOID) {
					syntaxError(errorMessage(INCOMPARABLE_OPERANDS),filename,bop);	
					return null;
				} else if(sign) {					
					newType = glb;
				} else {					
					newType = Nominal.intersect(lhs.result(), Nominal.T_NOTNULL);												
				}
				bop.srcType = lhs.result();
				environment = environment.put(lv.var,newType);
			} else {
				// handle general case
				if(Type.isImplicitCoerciveSubtype(lhsRawType,rhsRawType)) {
					bop.srcType = lhs.result();
				} else if(Type.isImplicitCoerciveSubtype(rhsRawType,lhsRawType)) {
					bop.srcType = rhs.result();
				} else {
					syntaxError(errorMessage(INCOMPARABLE_OPERANDS),filename,bop);	
					return null; // dead code
				}		
			}
		}			
		
		return new Pair(bop,environment);

	}
	
	private Expr propagate(Expr expr,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) {
		
		try {
			if(expr instanceof Expr.BinOp) {
				return propagate((Expr.BinOp) expr,environment,imports); 
			} else if(expr instanceof Expr.UnOp) {
				return propagate((Expr.UnOp) expr,environment,imports); 
			} else if(expr instanceof Expr.Comprehension) {
				return propagate((Expr.Comprehension) expr,environment,imports); 
			} else if(expr instanceof Expr.Constant) {
				return propagate((Expr.Constant) expr,environment,imports); 
			} else if(expr instanceof Expr.Convert) {
				return propagate((Expr.Convert) expr,environment,imports); 
			} else if(expr instanceof Expr.Dictionary) {
				return propagate((Expr.Dictionary) expr,environment,imports); 
			} else if(expr instanceof Expr.AbstractFunctionOrMethodOrMessage) {
				return propagate((Expr.AbstractFunctionOrMethodOrMessage) expr,environment,imports); 
			} else if(expr instanceof Expr.AbstractInvoke) {
				return propagate((Expr.AbstractInvoke) expr,environment,imports); 
			} else if(expr instanceof Expr.AbstractIndexAccess) {
				return propagate((Expr.AbstractIndexAccess) expr,environment,imports); 
			} else if(expr instanceof Expr.AbstractLength) {
				return propagate((Expr.AbstractLength) expr,environment,imports); 
			} else if(expr instanceof Expr.AbstractVariable) {
				return propagate((Expr.AbstractVariable) expr,environment,imports); 
			} else if(expr instanceof Expr.List) {
				return propagate((Expr.List) expr,environment,imports); 
			} else if(expr instanceof Expr.Set) {
				return propagate((Expr.Set) expr,environment,imports); 
			} else if(expr instanceof Expr.SubList) {
				return propagate((Expr.SubList) expr,environment,imports); 
			} else if(expr instanceof Expr.SubString) {
				return propagate((Expr.SubString) expr,environment,imports); 
			} else if(expr instanceof Expr.AbstractDotAccess) {
				return propagate((Expr.AbstractDotAccess) expr,environment,imports); 
			} else if(expr instanceof Expr.Dereference) {
				return propagate((Expr.Dereference) expr,environment,imports); 
			} else if(expr instanceof Expr.Record) {
				return propagate((Expr.Record) expr,environment,imports); 
			} else if(expr instanceof Expr.New) {
				return propagate((Expr.New) expr,environment,imports); 
			} else if(expr instanceof Expr.Tuple) {
				return  propagate((Expr.Tuple) expr,environment,imports); 
			} else if(expr instanceof Expr.TypeVal) {
				return propagate((Expr.TypeVal) expr,environment,imports); 
			} 
		} catch(ResolveError e) {
			syntaxError(errorMessage(RESOLUTION_ERROR,e.getMessage()),filename,expr,e);
		} catch(SyntaxError e) {
			throw e;
		} catch(Throwable e) {
			internalFailure(e.getMessage(),filename,expr,e);
			return null; // dead code
		}		
		internalFailure("unknown expression: " + expr.getClass().getName(),filename,expr);
		return null; // dead code
	}
	
	private Expr propagate(Expr.BinOp expr,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {
		
		// TODO: split binop into arithmetic and conditional operators. This
		// would avoid the following case analysis since conditional binary
		// operators and arithmetic binary operators actually behave quite
		// differently.
		
		switch(expr.op) {
		case AND:
		case OR:
		case XOR:
		case EQ:
		case NEQ:
		case LT:	
		case LTEQ:
		case GT:	
		case GTEQ:
		case ELEMENTOF:
		case SUBSET:	
		case SUBSETEQ:
		case IS:								
			return propagate(expr,true,environment,imports).first();
		}
		
		Expr lhs = propagate(expr.lhs,environment,imports);
		Expr rhs = propagate(expr.rhs,environment,imports);
		expr.lhs = lhs;
		expr.rhs = rhs;
		Type lhsRawType = lhs.result().raw();
		Type rhsRawType = rhs.result().raw();
	
		boolean lhs_set = Type.isImplicitCoerciveSubtype(Type.Set(Type.T_ANY, false),lhsRawType);
		boolean rhs_set = Type.isImplicitCoerciveSubtype(Type.Set(Type.T_ANY, false),rhsRawType);		
		boolean lhs_list = Type.isImplicitCoerciveSubtype(Type.List(Type.T_ANY, false),lhsRawType);
		boolean rhs_list = Type.isImplicitCoerciveSubtype(Type.List(Type.T_ANY, false),rhsRawType);
		boolean lhs_str = Type.isSubtype(Type.T_STRING,lhsRawType);
		boolean rhs_str = Type.isSubtype(Type.T_STRING,rhsRawType);
		
		Type srcType;

		if(lhs_str || rhs_str) {
			
			switch(expr.op) {				
			case ADD:								
				expr.op = Expr.BOp.STRINGAPPEND;
			case STRINGAPPEND:
				break;
			default:			
				syntaxError("Invalid string operation: " + expr.op, filename,
						expr);
			}
			
			srcType = Type.T_STRING;
		} else if(lhs_list && rhs_list) {
			checkIsSubtype(Type.List(Type.T_ANY,false),lhs);
			checkIsSubtype(Type.List(Type.T_ANY,false),rhs);
			
			switch(expr.op) {	
			case ADD:
				expr.op = Expr.BOp.LISTAPPEND;
			case LISTAPPEND:				
				srcType = Type.Union(lhsRawType,rhsRawType);
				break;
			default:
				syntaxError("invalid list operation: " + expr.op,filename,expr);	
				return null; // dead-code
			}										
		} else if(lhs_set && rhs_set) {	
			checkIsSubtype(Type.Set(Type.T_ANY,false),lhs);
			checkIsSubtype(Type.Set(Type.T_ANY,false),rhs);						
			
			// FIXME: something tells me there should be a function for doing
			// this.  Perhaps effectiveSetType?
			
			if(lhs_list) {
				 Type.EffectiveList tmp = (Type.EffectiveList) lhsRawType;
				 lhsRawType = Type.Set(tmp.element(),false);
			} 
			
			if(rhs_list) {
				 Type.EffectiveList tmp = (Type.EffectiveList) rhsRawType;
				 rhsRawType = Type.Set(tmp.element(),false);
			}  
			
			// FIXME: loss of nominal information here
			Type.EffectiveSet ls = (Type.EffectiveSet) lhsRawType;
			Type.EffectiveSet rs = (Type.EffectiveSet) rhsRawType;	
			
			switch(expr.op) {				
				case ADD:																				
					expr.op = Expr.BOp.UNION;					
				case UNION:					
					// TODO: this forces unnecessary coercions, which would be
					// good to remove.
					srcType = Type.Set(Type.Union(ls.element(),rs.element()),false);					
					break;
				case BITWISEAND:																				
					expr.op = Expr.BOp.INTERSECTION;
				case INTERSECTION:
					// FIXME: this is just plain wierd.
					if(Type.isSubtype(lhsRawType, rhsRawType)) {
						srcType = rhsRawType;
					} else {
						srcType = lhsRawType;
					}					
					break;
				case SUB:																				
					expr.op = Expr.BOp.DIFFERENCE;
				case DIFFERENCE:
					srcType = lhsRawType;
					break;								
				default:
					syntaxError("invalid set operation: " + expr.op,filename,expr);	
					return null; // deadcode
			}							
		} else {			
			switch(expr.op) {
			case IS:
			case AND:
			case OR:
			case XOR:
				return propagate(expr,true,environment,imports).first();				
			case BITWISEAND:
			case BITWISEOR:
			case BITWISEXOR:
				checkIsSubtype(Type.T_BYTE,lhs);
				checkIsSubtype(Type.T_BYTE,rhs);
				srcType = Type.T_BYTE;
				break;
			case LEFTSHIFT:
			case RIGHTSHIFT:
				checkIsSubtype(Type.T_BYTE,lhs);
				checkIsSubtype(Type.T_INT,rhs);
				srcType = Type.T_BYTE;
				break;
			case RANGE:
				checkIsSubtype(Type.T_INT,lhs);
				checkIsSubtype(Type.T_INT,rhs);
				srcType = Type.List(Type.T_INT, false);
				break;
			case REM:
				checkIsSubtype(Type.T_INT,lhs);
				checkIsSubtype(Type.T_INT,rhs);
				srcType = Type.T_INT;
				break;			
			default:
				// all other operations go through here
				if(Type.isImplicitCoerciveSubtype(lhsRawType,rhsRawType)) {
					checkIsSubtype(Type.T_REAL,lhs);
					if(Type.isSubtype(Type.T_CHAR, lhsRawType)) {
						srcType = Type.T_INT;
					} else if(Type.isSubtype(Type.T_INT, lhsRawType)) {
						srcType = Type.T_INT;
					} else {
						srcType = Type.T_REAL;
					}				
				} else {
					checkIsSubtype(Type.T_REAL,lhs);
					checkIsSubtype(Type.T_REAL,rhs);				
					if(Type.isSubtype(Type.T_CHAR, rhsRawType)) {
						srcType = Type.T_INT;
					} else if(Type.isSubtype(Type.T_INT, rhsRawType)) {
						srcType = Type.T_INT;
					} else {
						srcType = Type.T_REAL;
					}
				}				
			}
		}	
		
		// FIXME: loss of nominal information
		expr.srcType = Nominal.construct(srcType,srcType);
		
		return expr;
	}
	
	private Expr propagate(Expr.UnOp expr,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {
		
		if(expr.op == Expr.UOp.NOT) {
			// hand off to special method for conditions
			return propagate(expr,true,environment,imports).first();	
		}
		
		Expr src = propagate(expr.mhs, environment, imports);
		expr.mhs = src;
		
		switch(expr.op) {
		case NEG:
			checkIsSubtype(Type.T_REAL,src);			
			break;
		case INVERT:
			checkIsSubtype(Type.T_BYTE,src);
			break;
				
		default:		
			internalFailure(
					"unknown operator: " + expr.op.getClass().getName(),
					filename, expr);
		}
		
		expr.type = src.result();		
		
		return expr;
	}
	
	private Expr propagate(Expr.Comprehension expr,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {
		
		ArrayList<Pair<String,Expr>> sources = expr.sources;
		RefCountedHashMap<String,Nominal> local = environment.clone();
		for(int i=0;i!=sources.size();++i) {
			Pair<String,Expr> p = sources.get(i);
			Expr e = propagate(p.second(),local,imports);			
			p = new Pair<String,Expr>(p.first(),e);
			sources.set(i,p);
			Nominal element;
			Nominal type = e.result();
			Nominal.EffectiveList listType = resolver.expandAsEffectiveList(type);
			Nominal.EffectiveSet setType = resolver.expandAsEffectiveSet(type);
			if(listType != null) {
				element = listType.element();
			} else if(setType != null) {
				element = setType.element();
			} else {
				syntaxError(errorMessage(INVALID_SET_OR_LIST_EXPRESSION),filename,e);
				return null; // dead code
			}
			// update environment for subsequent source expressions, the
			// condition and the value.
			local = local.put(p.first(),element);
		}
		
		if(expr.condition != null) {
			expr.condition = propagate(expr.condition,local,imports);
		}
		
		if (expr.cop == Expr.COp.SETCOMP || expr.cop == Expr.COp.LISTCOMP) {						
			expr.value = propagate(expr.value,local,imports);
			expr.type = Nominal.Set(expr.value.result(), false);
		} else {
			expr.type = Nominal.T_BOOL;
		}
		
		local.free();				
		
		return expr;
	}
	
	private Expr propagate(Expr.Constant expr,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) {
		return expr;
	}

	private Expr propagate(Expr.Convert c,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {
		c.expr = propagate(c.expr,environment,imports);		
		c.type = resolver.resolveAsType(c.unresolvedType, imports, filename);
		Type from = c.expr.result().raw();		
		Type to = c.type.raw();
		if (!Type.isExplicitCoerciveSubtype(to, from)) {			
			syntaxError(errorMessage(SUBTYPE_ERROR, to, from), filename, c);
		}	
		return c;
	}
	
	private Expr propagate(Expr.AbstractFunctionOrMethodOrMessage expr,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {
		
		if(expr instanceof Expr.FunctionOrMethodOrMessage) {
			return expr;
		} 		
		
		Pair<NameID, Nominal.FunctionOrMethodOrMessage> p;
		
		if (expr.paramTypes != null) {
			ArrayList<Nominal> paramTypes = new ArrayList<Nominal>();
			for (UnresolvedType t : expr.paramTypes) {
				paramTypes.add(resolver.resolveAsType(t, imports, filename));
			}
			// FIXME: clearly a bug here in the case of message reference
			p = (Pair) resolver
					.resolveAsFunctionOrMethod(expr.name, paramTypes, imports);			
		} else {
			p = resolver.resolveAsFunctionOrMethodOrMessage(expr.name, imports);			
		}
		
		expr = new Expr.FunctionOrMethodOrMessage(p.first(),expr.paramTypes,expr.attributes());
		expr.type = p.second();
		return expr;
	}
	
	private Expr propagate(Expr.AbstractInvoke expr,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {
		
		// first, propagate through receiver and parameters.
		
		Expr receiver = expr.qualification;
		
		if(receiver != null) {
			receiver = propagate(receiver,environment,imports);
			expr.qualification = receiver;						
		}
		
		ArrayList<Expr> exprArgs = expr.arguments;
		ArrayList<Nominal> paramTypes = new ArrayList<Nominal>();
		for(int i=0;i!=exprArgs.size();++i) {
			Expr arg = propagate(exprArgs.get(i),environment,imports);
			exprArgs.set(i, arg);
			paramTypes.add(arg.result());			
		}
		
		// second, determine whether we already have a fully qualified name and
		// then lookup the appropriate function.
		
		if(receiver instanceof Expr.ModuleAccess) {
			// Yes, this function or method is qualified
			Expr.ModuleAccess ma = (Expr.ModuleAccess) receiver;
			NameID name = new NameID(ma.mid,expr.name);
			Nominal.FunctionOrMethod funType = resolver.resolveAsFunctionOrMethod(name,  paramTypes);			
			if(funType instanceof Nominal.Function) {
				Expr.FunctionCall r = new Expr.FunctionCall(name, ma, exprArgs, expr.attributes());
				r.functionType = (Nominal.Function) funType;				
				return r;
			} else {
				Expr.MethodCall r = new Expr.MethodCall(name, ma, exprArgs, expr.attributes());
				r.methodType = (Nominal.Method) funType;
				return r;
			}
		} else if(receiver != null) {
			
			// function is qualified, so this is used as the scope for resolving
			// what the function is.
			
			Nominal.EffectiveRecord recType = resolver.expandAsEffectiveRecord(expr.qualification.result());
			
			if(recType != null) {
				
				Nominal fieldType = recType.field(expr.name);
				
				if(fieldType == null) {
					syntaxError(errorMessage(RECORD_MISSING_FIELD),filename,expr);
				} else if(!(fieldType instanceof Nominal.FunctionOrMethod)) {
					syntaxError("function or method type expected",filename,expr);
				}
				
				Nominal.FunctionOrMethod funType = (Nominal.FunctionOrMethod) fieldType;
				Expr.RecordAccess ra = new Expr.RecordAccess(receiver, expr.name, expr.attributes());
				ra.srcType = recType;
						
				if(funType instanceof Nominal.Method) { 
					Expr.IndirectMethodCall nexpr = new Expr.IndirectMethodCall(ra,expr.arguments,expr.attributes());
					// FIXME: loss of nominal information
					nexpr.methodType = (Nominal.Method) funType; 
					return nexpr;
				} else {
					Expr.IndirectFunctionCall nexpr = new Expr.IndirectFunctionCall(ra,expr.arguments,expr.attributes());
					// FIXME: loss of nominal information
					nexpr.functionType = (Nominal.Function) funType;
					return nexpr;
				}
				
			} else {
				// In this case, we definitely have an object type. 
				
				Type.Reference procType = checkType(expr.qualification.result().raw(),Type.Reference.class,receiver);							

				// ok, it's a message send (possibly indirect)
				Nominal type = environment.get(expr.name);
				Nominal.Message msgType = type != null ? resolver.expandAsMessage(type) : null;
				
				// FIXME: bad idea to use instanceof Nominal.Message here
				if(msgType != null) {
					// ok, matching local variable of message type.
					List<Nominal> funTypeParams = msgType.params();
					// FIXME: is this broken since should be equivalent, not subtype?
					checkIsSubtype(msgType.receiver(),expr.qualification);
					if(paramTypes.size() != funTypeParams.size()) {
						syntaxError("insufficient arguments to message send",filename,expr);
					}
					for (int i = 0; i != funTypeParams.size(); ++i) {
						Nominal fpt = funTypeParams.get(i);
						checkIsSubtype(fpt, paramTypes.get(i), exprArgs.get(i));
					}
					
					Expr.LocalVariable lv = new Expr.LocalVariable(expr.name,expr.attributes());
					lv.type = type;								
					Expr.IndirectMessageSend nexpr = new Expr.IndirectMessageSend(
							lv, expr.qualification, expr.arguments,
							expr.synchronous, expr.attributes());
					nexpr.messageType = msgType; 
					return nexpr;					
				} else {

					Pair<NameID, Nominal.Message> p = resolver
							.resolveAsMessage(expr.name, procType, paramTypes,
									imports);				
					Expr.MessageSend r = new Expr.MessageSend(p.first(), receiver,
							exprArgs, expr.synchronous, expr.attributes());			
					r.messageType = p.second();
					return r;
				}
			}
		} else {

			// no, function is not qualified ... so, it's either a local
			// variable or a function call the location of which we need to
			// identify.

			Nominal type = environment.get(expr.name);			
			Nominal.FunctionOrMethod funType = type != null ? resolver.expandAsFunctionOrMethod(type) : null;
			
			// FIXME: bad idea to use instanceof Nominal.FunctionOrMethod here
			if(funType != null) {
				// ok, matching local variable of function type.				
				List<Nominal> funTypeParams = funType.params();
				if(paramTypes.size() != funTypeParams.size()) {
					syntaxError("insufficient arguments to function call",filename,expr);
				}
				for (int i = 0; i != funTypeParams.size(); ++i) {
					Nominal fpt = funTypeParams.get(i);
					checkIsSubtype(fpt, paramTypes.get(i), exprArgs.get(i));
				}
				
				Expr.LocalVariable lv = new Expr.LocalVariable(expr.name,expr.attributes());
				lv.type = type;
							
				if(funType instanceof Nominal.Method) { 
					Expr.IndirectMethodCall nexpr = new Expr.IndirectMethodCall(lv,expr.arguments,expr.attributes());				
					nexpr.methodType = (Nominal.Method) funType; 
					return nexpr;
				} else {
					Expr.IndirectFunctionCall nexpr = new Expr.IndirectFunctionCall(lv,expr.arguments,expr.attributes());
					nexpr.functionType = (Nominal.Function) funType;
					return nexpr;					
				}

			} else {				
				// no matching local variable, so attempt to resolve as direct
				// call.
				Pair<NameID, Nominal.FunctionOrMethod> p = resolver.resolveAsFunctionOrMethod(expr.name, paramTypes, imports);
				funType = p.second();							
				if(funType instanceof Nominal.Function) {					
					Expr.FunctionCall mc = new Expr.FunctionCall(p.first(), null, exprArgs, expr.attributes());					
					mc.functionType = (Nominal.Function) funType;
					return mc;
				} else {								
					Expr.MethodCall mc = new Expr.MethodCall(p.first(), null, exprArgs, expr.attributes());					
					mc.methodType = (Nominal.Method) funType;					
					return mc;
				}																				
			}
		}		
	}			
	
	private Expr propagate(Expr.AbstractIndexAccess expr,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {			
		expr.src = propagate(expr.src,environment,imports);
		expr.index = propagate(expr.index,environment,imports);		
		Nominal srcType = expr.src.result();
		Type rawSrcType = srcType.raw();			
		
		// First, check whether this is still only an abstract access and, in
		// such case, upgrade it to the appropriate access expression.
		
		if (!(expr instanceof Expr.StringAccess
				|| expr instanceof Expr.ListAccess || expr instanceof Expr.DictionaryAccess)) {
			// first time through
			if (Type.isImplicitCoerciveSubtype(Type.T_STRING, rawSrcType)) {
				expr = new Expr.StringAccess(expr.src, expr.index,
						expr.attributes());
			} else if (Type.isImplicitCoerciveSubtype(
					Type.List(Type.T_ANY, false), rawSrcType)) {
				expr = new Expr.ListAccess(expr.src, expr.index,
						expr.attributes());
			} else if (Type.isImplicitCoerciveSubtype(
					Type.Dictionary(Type.T_ANY, Type.T_ANY), rawSrcType)) {
				expr = new Expr.DictionaryAccess(expr.src, expr.index,
						expr.attributes());
			} else {
				syntaxError(errorMessage(INVALID_SET_OR_LIST_EXPRESSION), filename, expr.src);
			}
		}
		
		// Second, determine the expanded src type for this access expression
		// and check the key value.
		
		if(expr instanceof Expr.StringAccess) {
			checkIsSubtype(Type.T_STRING,expr.src);	
			checkIsSubtype(Type.T_INT,expr.index);				
		} else if(expr instanceof Expr.ListAccess) {
			Expr.ListAccess la = (Expr.ListAccess) expr; 
			Nominal.EffectiveList list = resolver.expandAsEffectiveList(srcType);			
			if(list == null) {
				syntaxError(errorMessage(INVALID_LIST_EXPRESSION),filename,expr);				
			}
			checkIsSubtype(Type.T_INT,expr.index);	
			la.srcType = list;			
		} else {
			Expr.DictionaryAccess da = (Expr.DictionaryAccess) expr; 
			Nominal.EffectiveDictionary dict = resolver.expandAsEffectiveDictionary(srcType);
			if(dict == null) {
				syntaxError(errorMessage(INVALID_DICTIONARY_EXPRESSION),filename,expr);
			}			
			checkIsSubtype(dict.key(),expr.index);			
			da.srcType = dict;						
		}
		
		return expr;
	}
	
	private Expr propagate(Expr.AbstractLength expr,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {			
		expr.src = propagate(expr.src,environment,imports);			
		Nominal srcType = expr.src.result();
		Type rawSrcType = srcType.raw();				
	
		// First, check whether this is still only an abstract access and, in
		// such case, upgrade it to the appropriate access expression.

		if (Type.isImplicitCoerciveSubtype(Type.T_STRING, rawSrcType)) {
			if(!(expr instanceof Expr.StringLength)) {
				expr = new Expr.StringLength(expr.src, expr.attributes());
			}
		} else if (Type.isImplicitCoerciveSubtype(Type.List(Type.T_ANY, false),
				rawSrcType)) {
			if(!(expr instanceof Expr.ListLength)) {
				expr = new Expr.ListLength(expr.src, expr.attributes());
			}
		} else if (Type.isImplicitCoerciveSubtype(Type.Set(Type.T_ANY, false),
				rawSrcType)) {
			if(!(expr instanceof Expr.SetLength)) {
				expr = new Expr.SetLength(expr.src, expr.attributes());
			}
		} else if (Type.isImplicitCoerciveSubtype(
				Type.Dictionary(Type.T_ANY, Type.T_ANY), rawSrcType)) {
			if(!(expr instanceof Expr.DictionaryLength)) {
				expr = new Expr.DictionaryLength(expr.src, expr.attributes());
			}
		} else {
			syntaxError("found " + expr.src.result().nominal()
					+ ", expected string, set, list or dictionary.", filename,
					expr.src);
		}

		// Second, determine the expanded src type for this access expression
		// and check the key value.

		if(expr instanceof Expr.StringLength) {
			checkIsSubtype(Type.T_STRING,expr.src);								
		} else if(expr instanceof Expr.ListLength) {
			Expr.ListLength ll = (Expr.ListLength) expr; 
			Nominal.EffectiveList list = resolver.expandAsEffectiveList(srcType);			
			if(list == null) {
				syntaxError(errorMessage(INVALID_LIST_EXPRESSION),filename,expr);				
			}
			ll.srcType = list;
		} else if(expr instanceof Expr.SetLength) {
			Expr.SetLength sl = (Expr.SetLength) expr; 
			Nominal.EffectiveSet set = resolver.expandAsEffectiveSet(srcType);			
			if(set == null) {
				syntaxError(errorMessage(INVALID_SET_EXPRESSION),filename,expr);				
			}
			sl.srcType = set;			
		} else {
			Expr.DictionaryLength dl = (Expr.DictionaryLength) expr; 
			Nominal.EffectiveDictionary dict = resolver.expandAsEffectiveDictionary(srcType);
			if(dict == null) {
				syntaxError(errorMessage(INVALID_DICTIONARY_EXPRESSION),filename,expr);
			}				
			dl.srcType = dict;	
		}
		
		return expr;
	}
	
	private Expr propagate(Expr.AbstractVariable expr,
			RefCountedHashMap<String, Nominal> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {

		Nominal type = environment.get(expr.var);

		if (expr instanceof Expr.LocalVariable) {
			Expr.LocalVariable lv = (Expr.LocalVariable) expr;			
			lv.type = type;			
			return lv;
		} else if (type != null) {
			// yes, this is a local variable
			Expr.LocalVariable lv = new Expr.LocalVariable(expr.var,
					expr.attributes());	
			lv.type = type;			
			return lv;
		} else {
			// This variable access may correspond to an external access.
			// Therefore, we must determine which module this
			// is, and update the tree accordingly.
			try {
				NameID nid = resolver.resolveAsName(expr.var, imports);					
				Expr.ConstantAccess ca = new Expr.ConstantAccess(null, expr.var, nid,
						expr.attributes());
				ca.value = resolver.resolveAsConstant(nid);
				return ca;
			} catch (ResolveError err) {
			}
			// In this case, we may still be OK if this corresponds to an
			// explicit module or package access.
			try {
				ModuleID mid = resolver.resolveAsModule(expr.var, imports);
				return new Expr.ModuleAccess(null, expr.var, mid,
						expr.attributes());
			} catch (ResolveError err) {
			}
			PkgID pid = new PkgID(expr.var);
			if (resolver.isPackage(pid)) {
				return new Expr.PackageAccess(null, expr.var, pid,
						expr.attributes());
			}
			// ok, failed.
			syntaxError(errorMessage(UNKNOWN_VARIABLE), filename, expr);
			return null; // deadcode
		}
	}
	
	private Expr propagate(Expr.Set expr,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) {
		Nominal element = Nominal.T_VOID;		
		
		ArrayList<Expr> exprs = expr.arguments;
		for(int i=0;i!=exprs.size();++i) {
			Expr e = propagate(exprs.get(i),environment,imports);
			Nominal t = e.result();
			exprs.set(i,e);
			element = Nominal.Union(t,element);			
		}
		
		expr.type = Nominal.Set(element,false);
		
		return expr;
	}
	
	private Expr propagate(Expr.List expr,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) {		
		Nominal element = Nominal.T_VOID;		
		
		ArrayList<Expr> exprs = expr.arguments;
		for(int i=0;i!=exprs.size();++i) {
			Expr e = propagate(exprs.get(i),environment,imports);
			Nominal t = e.result();
			exprs.set(i,e);
			element = Nominal.Union(t,element);			
		}	
		
		expr.type = Nominal.List(element,false);
				
		return expr;
	}
	
	
	private Expr propagate(Expr.Dictionary expr,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) {
		Nominal keyType = Nominal.T_VOID;
		Nominal valueType = Nominal.T_VOID;		
				
		ArrayList<Pair<Expr,Expr>> exprs = expr.pairs;
		for(int i=0;i!=exprs.size();++i) {
			Pair<Expr,Expr> p = exprs.get(i);
			Expr key = propagate(p.first(),environment,imports);
			Expr value = propagate(p.second(),environment,imports);
			Nominal kt = key.result();
			Nominal vt = value.result();
			exprs.set(i,new Pair<Expr,Expr>(key,value));
			
			keyType = Nominal.Union(kt,keyType);			
			valueType = Nominal.Union(vt,valueType);
		}
		
		expr.type = Nominal.Dictionary(keyType,valueType);
		
		return expr;
	}
	

	private Expr propagate(Expr.Record expr,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) {
		
		HashMap<String,Expr> exprFields = expr.fields;
		HashMap<String,Nominal> fieldTypes = new HashMap<String,Nominal>();
				
		ArrayList<String> fields = new ArrayList<String>(exprFields.keySet());
		for(String field : fields) {
			Expr e = propagate(exprFields.get(field),environment,imports);
			Nominal t = e.result();
			exprFields.put(field,e);
			fieldTypes.put(field,t);				
		}		
		
		expr.type = Nominal.Record(false,fieldTypes);
		
		return expr;
	}
	
	private Expr propagate(Expr.Tuple expr,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) {
		ArrayList<Expr> exprFields = expr.fields;
		ArrayList<Nominal> fieldTypes = new ArrayList<Nominal>();
				
		for(int i=0;i!=exprFields.size();++i) {
			Expr e = propagate(exprFields.get(i),environment,imports);
			Nominal t = e.result();
			exprFields.set(i,e);
			fieldTypes.add(t);			
		}
				
		expr.type = Nominal.Tuple(fieldTypes);
		
		return expr;
	}
	
	private Expr propagate(Expr.SubList expr,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {	
		
		expr.src = propagate(expr.src,environment,imports);
		expr.start = propagate(expr.start,environment,imports);
		expr.end = propagate(expr.end,environment,imports);
		
		checkIsSubtype(Type.List(Type.T_ANY, false),expr.src);
		checkIsSubtype(Type.T_INT,expr.start);
		checkIsSubtype(Type.T_INT,expr.end);
		
		expr.type = resolver.expandAsEffectiveList(expr.src.result());
		if(expr.type == null) {
			// must be a substring
			return new Expr.SubString(expr.src,expr.start,expr.end,expr.attributes());
		}
		
		return expr;
	}
	
	private Expr propagate(Expr.SubString expr,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {	
		
		expr.src = propagate(expr.src,environment,imports);
		expr.start = propagate(expr.start,environment,imports);
		expr.end = propagate(expr.end,environment,imports);
		
		checkIsSubtype(Type.T_STRING,expr.src);
		checkIsSubtype(Type.T_INT,expr.start);
		checkIsSubtype(Type.T_INT,expr.end);
		
		return expr;
	}
	
	private Expr propagate(Expr.AbstractDotAccess expr,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {	
				
		if (expr instanceof Expr.PackageAccess
				|| expr instanceof Expr.ModuleAccess) {			
			// don't need to do anything in these cases.
			return expr;
		}
		
		Expr src = expr.src;
		
		if(src != null) {
			src = propagate(expr.src,environment,imports);
			expr.src = src;
		}
				
		if(expr instanceof Expr.RecordAccess) {			
			return propagate((Expr.RecordAccess)expr,environment,imports);
		} else if(expr instanceof Expr.ConstantAccess) {
			return propagate((Expr.ConstantAccess)expr,environment,imports);
		} else if(src instanceof Expr.PackageAccess) {
			// either a package access, module access or constant access
			// This variable access may correspond to an external access.			
			Expr.PackageAccess pa = (Expr.PackageAccess) src; 
			PkgID pid = pa.pid.append(expr.name);
			if (resolver.isPackage(pid)) {
				return new Expr.PackageAccess(pa, expr.name, pid,
						expr.attributes());
			}
			try {				
				ModuleID mid = new ModuleID(pa.pid,expr.name);
				loader.loadModule(mid);
				return new Expr.ModuleAccess(pa, expr.name, mid,
						expr.attributes());
			} catch (ResolveError err) {
				syntaxError(errorMessage(INVALID_PACKAGE_ACCESS),filename,expr,err);
				return null; // deadcode
			}			
		} else if(src instanceof Expr.ModuleAccess) {
			// must be a constant access
			Expr.ModuleAccess ma = (Expr.ModuleAccess) src; 													
			NameID nid = new NameID(ma.mid,expr.name);
			if (resolver.isName(nid)) {
				Expr.ConstantAccess ca = new Expr.ConstantAccess(ma,
						expr.name, nid, expr.attributes());
				ca.value = resolver.resolveAsConstant(nid);
				return ca;
			}						
			syntaxError(errorMessage(INVALID_MODULE_ACCESS),filename,expr);			
			return null; // deadcode
		} else {
			// must be a RecordAccess
			Expr.RecordAccess ra = new Expr.RecordAccess(src,expr.name,expr.attributes());			
			return propagate(ra,environment,imports);
		}
	}
		
	private Expr propagate(Expr.RecordAccess ra,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {
		Nominal srcType = ra.src.result();
		Nominal.EffectiveRecord recType = resolver.expandAsEffectiveRecord(srcType);
		if(recType == null) {
			syntaxError(errorMessage(RECORD_TYPE_REQUIRED,srcType.raw()),filename,ra);
		} 
		Nominal fieldType = recType.field(ra.name);
		if(fieldType == null) {
			syntaxError(errorMessage(RECORD_MISSING_FIELD),filename,ra);
		}
		ra.srcType = recType;		
		return ra;
	}	
	
	private Expr propagate(Expr.ConstantAccess expr,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {
		// we don't need to do anything here, since the value is already
		// resolved by case for AbstractDotAccess.
		return expr;
	}			

	private Expr propagate(Expr.Dereference expr,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {
		Expr src = propagate(expr.src,environment,imports);
		expr.src = src;
		Nominal.Reference srcType = resolver.expandAsReference(src.result());
		if(srcType == null) {
			syntaxError("invalid reference expression",filename,src);
		}
		expr.srcType = srcType;		
		return expr;
	}
	
	private Expr propagate(Expr.New expr,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) {
		expr.expr = propagate(expr.expr,environment,imports);
		expr.type = Nominal.Reference(expr.expr.result());
		return expr;
	}
	
	private Expr propagate(Expr.TypeVal expr,
			RefCountedHashMap<String,Nominal> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {
		expr.type = resolver.resolveAsType(expr.unresolvedType, imports, filename); 
		return expr;
	}
	
	private <T extends Type> T checkType(Type t, Class<T> clazz,
			SyntacticElement elem) {
		if (clazz.isInstance(t)) {
			return (T) t;
		} else {
			syntaxError(errorMessage(SUBTYPE_ERROR, clazz.getName().replace('$', '.'), t),
					filename, elem);
			return null;
		}
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
		} else if (t instanceof UnresolvedType.Reference) {			
			UnresolvedType.Reference ut = (UnresolvedType.Reference) t;
			addExposedNames(new Expr.Dereference(src),
					ut.element, environment);
		}
	}
	
	private abstract static class Scope {
		public abstract void free();
	}
	
	private static final class Handler {
		public final Type exception;
		public final String variable;
		public RefCountedHashMap<String,Nominal> environment;
		
		public Handler(Type exception, String variable) {
			this.exception = exception;
			this.variable = variable;
			this.environment = new RefCountedHashMap<String,Nominal>();
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
		public RefCountedHashMap<String,Nominal> environment;
		
		public void free() {
			environment.free();
		}
	}

	private static final class ContinueScope extends Scope {
		public RefCountedHashMap<String,Nominal> environment;
		
		public void free() {
			environment.free();
		}
	}
	
	private static final RefCountedHashMap<String,Nominal> BOTTOM = new RefCountedHashMap<String,Nominal>();
	
	private static final RefCountedHashMap<String, Nominal> join(
			RefCountedHashMap<String, Nominal> lhs,
			RefCountedHashMap<String, Nominal> rhs) {
		
		// first, need to check for the special bottom value case.
		
		if(lhs == BOTTOM) {
			return rhs;
		} else if(rhs == BOTTOM) {
			return lhs;
		}
		
		// ok, not bottom so compute intersection.
		
		lhs.free();
		rhs.free(); 		
		
		RefCountedHashMap<String,Nominal> result = new RefCountedHashMap<String,Nominal>();
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
