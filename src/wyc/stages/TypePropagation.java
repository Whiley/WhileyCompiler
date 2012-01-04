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
import wyil.lang.Code;
import wyil.lang.Module;
import wyil.lang.ModuleID;
import wyil.lang.NameID;
import wyil.lang.PkgID;
import wyil.lang.Type;
import wyil.lang.Code.OpDir;
import wyil.lang.Value;
import wyil.util.Pair;
import wyil.util.ResolveError;
import wyil.util.SyntacticElement;
import wyil.util.SyntaxError;
import wyil.util.Triple;
import static wyil.util.SyntaxError.*;

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
		imports.add(new WhileyFile.Import(PkgID.fromString("whiley.lang"), "*", null)); 
		
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
					propagate((Constant)decl,imports);					
				}			
			} catch(SyntaxError e) {
				throw e;
			} catch(Throwable t) {
				internalFailure("internal failure",filename,decl,t);
			}
		}
	}
	
	public void propagate(Constant cd, ArrayList<WhileyFile.Import> imports) {
		
	}
	
	public void propagate(TypeDef td, ArrayList<WhileyFile.Import> imports) throws ResolveError {		
		// first, expand the declared type
		td.resolvedType = resolver.resolveAsType(td.unresolvedType, imports);
		
		if(td.constraint != null) {						
			// second, construct the appropriate typing environment			
			RefCountedHashMap<String,Nominal<Type>> environment = new RefCountedHashMap<String,Nominal<Type>>();
			environment.put("$", td.resolvedType);
			
			// FIXME: add names exposed from records and other types
			
			// third, propagate type information through the constraint 
			propagate(td.constraint,environment,imports);
		}
	}

	public void propagate(FunctionOrMethodOrMessage d, ArrayList<WhileyFile.Import> imports) throws ResolveError {
		this.current = d;
		RefCountedHashMap<String,Nominal<Type>> environment = new RefCountedHashMap<String,Nominal<Type>>();
						
		for (WhileyFile.Parameter p : d.parameters) {							
			environment = environment.put(p.name,resolver.resolveAsType(p.type,imports));
		}
		
		if(d instanceof Message) {
			Message md = (Message) d;							
			environment = environment.put("this",resolver.resolveAsType(md.receiver,imports));			
		}
		
		if(d.precondition != null) {
			propagate(d.precondition,environment.clone(),imports);
		}
		
		if(d.postcondition != null) {			
			environment = environment.put("$", resolver.resolveAsType(d.ret,imports));
			propagate(d.postcondition,environment.clone(),imports);
			// The following is a little sneaky and helps to avoid unnecessary
			// copying of environments. 
			environment = environment.remove("$");
		}

		if(d instanceof Function) {
			Function f = (Function) d;
			f.resolvedType = (Nominal) resolver.resolveAsType(f.unresolvedType(),imports);					
		} else if(d instanceof Method) {
			Method m = (Method) d;			
			m.resolvedType = (Nominal) resolver.resolveAsType(m.unresolvedType(),imports);		
		} else {
			Message m = (Message) d;
			m.resolvedType = (Nominal) resolver.resolveAsType(m.unresolvedType(),imports);		
		}
			
		propagate(d.statements,environment,imports);
	}
	
	private RefCountedHashMap<String, Nominal<Type>> propagate(
			ArrayList<Stmt> body,
			RefCountedHashMap<String, Nominal<Type>> environment,
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
	
	private RefCountedHashMap<String,Nominal<Type>> propagate(Stmt stmt,
			RefCountedHashMap<String, Nominal<Type>> environment,
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
			} else if(stmt instanceof Stmt.For) {
				return propagate((Stmt.For) stmt,environment,imports);
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
				internalFailure("unknown statement encountered",filename,stmt);
				return null; // deadcode
			}
		} catch(ResolveError e) {
			syntaxError(errorMessage(RESOLUTION_ERROR,e.getMessage()),filename,stmt);
			return null; // dead code
		} catch(SyntaxError e) {
			throw e;
		} catch(Throwable e) {
			internalFailure("internal failure",filename,stmt,e);
			return null; // dead code
		}
	}
	
	private RefCountedHashMap<String,Nominal<Type>> propagate(Stmt.Assert stmt,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) {
		stmt.expr = propagate(stmt.expr,environment,imports);
		checkIsSubtype(Type.T_BOOL,stmt.expr);
		return environment;
	}
	
	private RefCountedHashMap<String,Nominal<Type>> propagate(Stmt.Assign stmt,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) {
			
		Expr.LVal lhs = stmt.lhs;
		Expr rhs = propagate(stmt.rhs,environment,imports);
		
		if(lhs instanceof Expr.AbstractVariable) {
			// An assignment to a local variable is slightly different from
			// other kinds of assignments. That's because in this case only it
			// is permitted that the variable does not exist a priori.
			// Therefore, whatever type the rhs has, the variable in question
			// will have after the assignment.
			Expr.AbstractVariable av = (Expr.AbstractVariable) lhs;
			Expr.LocalVariable lv;
			if(lhs instanceof Expr.LocalVariable) {
				// this case just avoids creating another object everytime we
				// visit this statement.
				lv = (Expr.LocalVariable) lhs; 
			} else {
				lv = new Expr.LocalVariable(av.var, av.attributes());
			}
			lv.type = (Nominal) rhs.type();			
			environment = environment.put(lv.var, lv.type);
			lhs = lv;
		} else {
			lhs = propagate(lhs,environment,imports);			
			Expr.AssignedVariable av = inferBeforeAfterType(lhs,
					(Nominal) lhs.type(), (Nominal) rhs.type());
			environment = environment.put(av.var, av.afterType);
		}
		
		stmt.lhs = (Expr.LVal) lhs;
		stmt.rhs = rhs;	
		
		return environment;
	}
	
	private Expr.AssignedVariable inferBeforeAfterType(Expr.LVal lv,
			Nominal<Type> beforeType, Nominal<Type> afterType) {
		if (lv instanceof Expr.AssignedVariable) {
			Expr.AssignedVariable v = (Expr.AssignedVariable) lv;
			v.type = beforeType;
			v.afterType = afterType;			
			return v;
		} else if (lv instanceof Expr.StringAccess) {
			Expr.StringAccess la = (Expr.StringAccess) lv;
			return inferBeforeAfterType((Expr.LVal) la.src, Nominal.T_STRING,
					Nominal.T_STRING);
		} else if (lv instanceof Expr.ListAccess) {
			Expr.ListAccess la = (Expr.ListAccess) lv;
			Type nominalAfterType = Type.List(
					Type.Union(la.type().nominal(), afterType.nominal()), false);
			Type rawAfterType = Type.List(
					Type.Union(la.type().raw(), afterType.raw()), false);
			// FIXME: loss of nominal information here.
			afterType = new Nominal<Type>(nominalAfterType,rawAfterType);
			return inferBeforeAfterType((Expr.LVal) la.src, (Nominal) la.srcType, afterType);
		} else if(lv instanceof Expr.DictionaryAccess)  {
			Expr.DictionaryAccess da = (Expr.DictionaryAccess) lv;		
			Type.Dictionary rawSrcType = da.srcType.raw();
			// FIXME: loss of nominal information here
			Type nominalAfterType = Type.Dictionary(
					Type.Union(rawSrcType.key(), da.index.type().nominal()),
					Type.Union(da.elementType.nominal(), afterType.nominal()));
			Type rawAfterType = Type.Dictionary(
					Type.Union(rawSrcType.key(), da.index.type().raw()),
					Type.Union(rawSrcType.value(), afterType.raw()));
			// FIXME: loss of nominal information here.
			afterType = new Nominal<Type>(nominalAfterType,rawAfterType);
			return inferBeforeAfterType((Expr.LVal) da.src, (Nominal) da.srcType, afterType);
		} else if(lv instanceof Expr.RecordAccess) {
			Expr.RecordAccess la = (Expr.RecordAccess) lv;
			Type.Record srcType = la.srcType.raw();
			HashMap<String,Type> beforeFields = new HashMap<String,Type>(srcType.fields());
			HashMap<String,Type> afterFields = new HashMap<String,Type>(srcType.fields());
			beforeFields.put(la.name, beforeType.raw());
			afterFields.put(la.name, afterType.raw());
			
			Type rawBeforeType = Type.Record(srcType.isOpen(), beforeFields);
			Type rawAfterType = Type.Record(srcType.isOpen(), afterFields);
			
			// FIXME: loss of nominal information here.
			return inferBeforeAfterType((Expr.LVal) la.src, new Nominal<Type>(
					rawBeforeType, rawBeforeType), new Nominal<Type>(
					rawAfterType, rawAfterType));
		} else {
			internalFailure("unknown lval encountered ("
					+ lv.getClass().getName() + ")", filename, lv);
			return null; //deadcode
		}
	}
	
	private RefCountedHashMap<String,Nominal<Type>> propagate(Stmt.Break stmt,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) {
		// nothing to do
		return environment;
	}
	
	private RefCountedHashMap<String,Nominal<Type>> propagate(Stmt.Debug stmt,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) {
		stmt.expr = propagate(stmt.expr,environment,imports);				
		checkIsSubtype(Type.T_STRING,stmt.expr);
		return environment;
	}
	
	private RefCountedHashMap<String,Nominal<Type>> propagate(Stmt.DoWhile stmt,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) {
		
		if (stmt.invariant != null) {
			stmt.invariant = propagate(stmt.invariant, environment, imports);
			checkIsSubtype(Type.T_BOOL,stmt.invariant);
		}
		
		// FIXME: need to iterate to a fixed point		
		environment = propagate(stmt.body,environment,imports);
		
		stmt.condition = propagate(stmt.condition,environment,imports);
		checkIsSubtype(Type.T_BOOL,stmt.condition);			
		
		return environment;
	}
	
	private RefCountedHashMap<String,Nominal<Type>> propagate(Stmt.For stmt,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) {
		
		stmt.source = propagate(stmt.source,environment,imports);
		Type rawType = stmt.source.type().raw(); 		
		
		// At this point, the major task is to determine what the types for the
		// iteration variables declared in the for loop. More than one variable
		// is permitted in some cases.
		
		Nominal<Type>[] elementTypes = new Nominal[stmt.variables.size()];		
		if(Type.isSubtype(Type.List(Type.T_ANY, false),rawType)) {
			Type.List lt = Type.effectiveListType(rawType);
			if(elementTypes.length == 1) {
				// FIXME: loss of nominal information
				elementTypes[0] = new Nominal<Type>(lt.element(),lt.element());
			} else {
				syntaxError(errorMessage(VARIABLE_POSSIBLY_UNITIALISED),filename,stmt);
			}			
		} else if(Type.isSubtype(Type.Set(Type.T_ANY, false),rawType)) {
			Type.Set st = Type.effectiveSetType(rawType);
			if(elementTypes.length == 1) {
				// FIXME: loss of nominal information
				elementTypes[0] = new Nominal<Type>(st.element(),st.element());
			} else {
				syntaxError(errorMessage(VARIABLE_POSSIBLY_UNITIALISED),filename,stmt);
			}					
		} else if(Type.isSubtype(Type.Dictionary(Type.T_ANY, Type.T_ANY),rawType)) {
			Type.Dictionary dt = Type.effectiveDictionaryType(rawType);
			if(elementTypes.length == 1) {
				Type elemType = Type.Tuple(dt.key(),dt.value()); 
				elementTypes[0] = new Nominal<Type>(elemType,elemType);			
			} else if(elementTypes.length == 2) {					
				elementTypes[0] = new Nominal<Type>(dt.key(),dt.key());
				elementTypes[1] = new Nominal<Type>(dt.value(),dt.value());
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
		
		if (stmt.invariant != null) {
			stmt.invariant = propagate(stmt.invariant, environment, imports);
			checkIsSubtype(Type.T_BOOL,stmt.invariant);
		}
		
		// FIXME: need to iterate to a fixed point
		environment = propagate(stmt.body,environment,imports);
		
		// Remove loop variables from the environment, since they are only
		// declared for the duration of the body but not beyond.
		for(int i=0;i!=elementTypes.length;++i) {
			String var = stmtVariables.get(i);				
			environment = environment.remove(var);
		} 
		
		return environment;
	}
	
	private RefCountedHashMap<String,Nominal<Type>> propagate(Stmt.IfElse stmt,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) {
		
		stmt.condition = propagate(stmt.condition,environment,imports);
		checkIsSubtype(Type.T_BOOL,stmt.condition);
		
		// FIXME: need to deal with retyping of variables
		
		RefCountedHashMap<String,Nominal<Type>> trueEnv;
		RefCountedHashMap<String,Nominal<Type>> falseEnv;
		
		if(stmt.trueBranch != null && stmt.trueBranch != null) {
			falseEnv = environment.clone();
			trueEnv = propagate(stmt.trueBranch,environment.clone(),imports);
			falseEnv = propagate(stmt.falseBranch,environment,imports);						
		} else if(stmt.trueBranch != null) {			
			trueEnv = propagate(stmt.trueBranch,environment.clone(),imports);
			falseEnv = environment;
			environment = join(environment,trueEnv);			
		} else if(stmt.falseBranch != null){								
			trueEnv = environment;
			falseEnv = propagate(stmt.falseBranch,environment.clone(),imports);		
			environment = join(environment,falseEnv);
		} else {
			trueEnv = environment;
			falseEnv = environment.clone();
		}
		
		environment = join(trueEnv,falseEnv);			
		trueEnv.free();
		falseEnv.free(); 
		
		return environment;
	}
	
	private RefCountedHashMap<String, Nominal<Type>> propagate(
			Stmt.Return stmt,
			RefCountedHashMap<String, Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {
		
		if (stmt.expr != null) {
			stmt.expr = propagate(stmt.expr, environment,imports);
			Type rawType;
			Type nominalType;
			// FIXME: loss of nominal information
			nominalType = current.resolvedType().raw().ret();
			rawType = current.resolvedType().raw().ret();			
			Nominal<Type> lhs = new Nominal<Type>(nominalType,rawType);
			Nominal<Type> rhs = (Nominal) stmt.expr.type();
			checkIsSubtype(lhs,rhs, stmt.expr);
		}
		
		environment.free();
		return BOTTOM;
	}
	
	private RefCountedHashMap<String,Nominal<Type>> propagate(Stmt.Skip stmt,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) {
		return environment;
	}
	
	private RefCountedHashMap<String,Nominal<Type>> propagate(Stmt.Switch stmt,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) {
		return environment;
	}
	
	private RefCountedHashMap<String,Nominal<Type>> propagate(Stmt.Throw stmt,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) {
		stmt.expr = propagate(stmt.expr,environment,imports);
		return BOTTOM;
	}
	
	private RefCountedHashMap<String,Nominal<Type>> propagate(Stmt.TryCatch stmt,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) {
		return environment;
	}
	
	private RefCountedHashMap<String,Nominal<Type>> propagate(Stmt.While stmt,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) {

		stmt.condition = propagate(stmt.condition,environment,imports);
		checkIsSubtype(Type.T_BOOL,stmt.condition);			
		
		if (stmt.invariant != null) {
			stmt.invariant = propagate(stmt.invariant, environment, imports);
			checkIsSubtype(Type.T_BOOL,stmt.invariant);
		}		
		
		// FIXME: need to iterate to a fixed point
		environment = propagate(stmt.body,environment,imports);
		
		return environment;
	}
	
	private Expr.LVal propagate(Expr.LVal lval,
			RefCountedHashMap<String, Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) {
		try {
			if(lval instanceof Expr.AbstractVariable) {
				Expr.AbstractVariable av = (Expr.AbstractVariable) lval;
				Nominal<Type> p = environment.get(av.var);
				if(p == null) {
					syntaxError(errorMessage(UNKNOWN_VARIABLE),filename,av);
				}				
				Expr.AssignedVariable lv = new Expr.AssignedVariable(av.var, av.attributes());
				lv.type = p;				
				return lv;
			} else if(lval instanceof Expr.AbstractIndexAccess) {
				// this indicates either a list, string or dictionary update
				Expr.AbstractIndexAccess ai = (Expr.AbstractIndexAccess) lval;				
				Expr.LVal src = propagate((Expr.LVal) ai.src,environment,imports);				
				Expr index = propagate(ai.index,environment,imports);				
				Type rawSrcType = src.type().raw();
				// FIXME: problem if list is only an effective list, similarly
				// for dictionaries.
				if(Type.isSubtype(Type.T_STRING, rawSrcType)) {
					return new Expr.StringAccess(src,index,lval.attributes());
				} else if(Type.isSubtype(Type.List(Type.T_ANY,false), rawSrcType)) {
					Expr.ListAccess la = new Expr.ListAccess(src,index,lval.attributes());
					Type.List lt = Type.effectiveListType(rawSrcType); 
					la.srcType = new Nominal<Type.List>(lt,lt);
					la.elementType = new Nominal<Type>(lt.element(),lt.element());
					return la;
				} else  if(Type.isSubtype(Type.Dictionary(Type.T_ANY, Type.T_ANY), rawSrcType)) {
					Expr.DictionaryAccess da = new Expr.DictionaryAccess(src,index,lval.attributes());
					Type.Dictionary lt = Type.effectiveDictionaryType(rawSrcType);
					da.srcType = new Nominal<Type.Dictionary>(lt,lt);
					da.elementType = new Nominal<Type>(lt.value(),lt.value());
					return da;
				} else {				
					syntaxError(errorMessage(INVALID_LVAL_EXPRESSION),filename,src);
				}
			} else if(lval instanceof Expr.AbstractDotAccess) {
				// this indicates a record update
				Expr.AbstractDotAccess ad = (Expr.AbstractDotAccess) lval;
				Expr.LVal src = propagate((Expr.LVal) ad.src,environment,imports);
				Expr.RecordAccess ra = new Expr.RecordAccess(src, ad.name, ad.attributes());
				Type.Record rawSrcType = Type.effectiveRecordType(src.type().raw());
				if(rawSrcType == null) {								
					syntaxError(errorMessage(INVALID_LVAL_EXPRESSION),filename,src);					
				}
				ra.srcType = new Nominal<Type.Record>(src.type().nominal(),rawSrcType);
				// FIXME: loss of nominal information
				Type fieldType = rawSrcType.fields().get(ad.name);
				ra.fieldType = new Nominal<Type>(fieldType,fieldType);
				return ra;
			}
		} catch(SyntaxError e) {
			throw e;
		} catch(Throwable e) {
			internalFailure("internal failure",filename,lval,e);
			return null; // dead code
		}		
		internalFailure("unknown lval encountered (" + lval.getClass().getName() +")",filename,lval);
		return null; // dead code
	}
	
	private Expr propagate(Expr expr,
			RefCountedHashMap<String,Nominal<Type>> environment,
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
			} else if(expr instanceof Expr.Function) {
				return propagate((Expr.Function) expr,environment,imports); 
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
			} else if(expr instanceof Expr.AbstractDotAccess) {
				return propagate((Expr.AbstractDotAccess) expr,environment,imports); 
			} else if(expr instanceof Expr.Record) {
				return propagate((Expr.Record) expr,environment,imports); 
			} else if(expr instanceof Expr.Spawn) {
				return propagate((Expr.Spawn) expr,environment,imports); 
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
			internalFailure("internal failure",filename,expr,e);
			return null; // dead code
		}		
		internalFailure("unknown expression encountered (" + expr.getClass().getName() +")",filename,expr);
		return null; // dead code
	}
	
	private Expr propagate(Expr.BinOp expr,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {
		expr.lhs = propagate(expr.lhs,environment,imports);
		expr.rhs = propagate(expr.rhs,environment,imports);
		Type lhsExpanded = expr.lhs.type().raw();
		Type rhsExpanded = expr.rhs.type().raw();
	
		boolean lhs_set = Type.isSubtype(Type.Set(Type.T_ANY, false),lhsExpanded);
		boolean rhs_set = Type.isSubtype(Type.Set(Type.T_ANY, false),rhsExpanded);		
		boolean lhs_list = Type.isSubtype(Type.List(Type.T_ANY, false),lhsExpanded);
		boolean rhs_list = Type.isSubtype(Type.List(Type.T_ANY, false),rhsExpanded);
		boolean lhs_str = Type.isSubtype(Type.T_STRING,lhsExpanded);
		boolean rhs_str = Type.isSubtype(Type.T_STRING,rhsExpanded);
		
		Type result;
		if(lhs_str || rhs_str) {						
			if (expr.op == Expr.BOp.ADD) {
				expr.op = Expr.BOp.STRINGAPPEND;
			} else {
				syntaxError("Invalid string operation: " + expr.op, filename,
						expr);
			}
			
			result = Type.T_STRING;
		} else if(lhs_set && rhs_set) {		
			Type type = Type.effectiveSetType(Type.Union(lhsExpanded,rhsExpanded));
			
			switch(expr.op) {				
				case ADD:																				
					expr.op = Expr.BOp.UNION;
					break;
				case BITWISEAND:																				
					expr.op = Expr.BOp.INTERSECTION;
					break;
				case SUB:																				
					expr.op = Expr.BOp.DIFFERENCE;
					break;
				case SUBSET:
				case SUBSETEQ:					
					break;
				default:
					syntaxError("Invalid set operation: " + expr.op,filename,expr);		
			}
			
			result = type;
		} else if(lhs_list && rhs_list) {
			Type.List type = Type.effectiveListType(Type.Union(lhsExpanded,rhsExpanded));
			
			if(expr.op == Expr.BOp.ADD){ 																							
					expr.op = Expr.BOp.LISTAPPEND;
			} else {
					syntaxError("Invalid set operation: " + expr.op,filename,expr);		
			}
			
			result = type;			
		} else {			
			switch(expr.op) {	
			case AND:
			case OR:
			case XOR:
				checkIsSubtype(Type.T_BOOL,expr.lhs);
				checkIsSubtype(Type.T_BOOL,expr.rhs);
				result = Type.T_BOOL;
				break;
			case BITWISEAND:
			case BITWISEOR:
			case BITWISEXOR:
				checkIsSubtype(Type.T_BYTE,expr.lhs);
				checkIsSubtype(Type.T_BYTE,expr.rhs);
				result = Type.T_BYTE;
				break;
			case LEFTSHIFT:
			case RIGHTSHIFT:
				checkIsSubtype(Type.T_BYTE,expr.lhs);
				checkIsSubtype(Type.T_INT,expr.rhs);
				result = Type.T_BYTE;
				break;
			case RANGE:
				checkIsSubtype(Type.T_INT,expr.lhs);
				checkIsSubtype(Type.T_INT,expr.rhs);
				result = Type.List(Type.T_INT, false);
				break;
			case REM:
				checkIsSubtype(Type.T_INT,expr.lhs);
				checkIsSubtype(Type.T_INT,expr.rhs);
				result = Type.T_INT;
				break;
			default:
				// all other operations go through here
				if(Type.isImplicitCoerciveSubtype(lhsExpanded,rhsExpanded)) {
					checkIsSubtype(Type.T_REAL,expr.lhs);
					if(Type.isSubtype(Type.T_CHAR, lhsExpanded)) {
						result = Type.T_INT;
					} else if(Type.isSubtype(Type.T_INT, lhsExpanded)) {
						result = Type.T_INT;
					} else {
						result = Type.T_REAL;
					}				
				} else {
					checkIsSubtype(Type.T_REAL,expr.lhs);
					checkIsSubtype(Type.T_REAL,expr.rhs);				
					if(Type.isSubtype(Type.T_CHAR, rhsExpanded)) {
						result = Type.T_INT;
					} else if(Type.isSubtype(Type.T_INT, rhsExpanded)) {
						result = Type.T_INT;
					} else {
						result = Type.T_REAL;
					}
				}				
			}
		}	
		
		/**
		 * Finally, save the resulting types for this expression.
		 */
		
		expr.srcType = new Nominal<Type>(result,result);
		
		return expr;
	}
	
	private Expr propagate(Expr.UnOp expr,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {
		Expr src = propagate(expr.mhs, environment, imports);
		expr.mhs = src;
		
		switch(expr.op) {
		case NEG:
			checkIsSubtype(Type.T_REAL,src);			
			break;
		case NOT:
			checkIsSubtype(Type.T_BOOL,src);
			break;
		case INVERT:
			checkIsSubtype(Type.T_BYTE,src);
			break;
		default:		
			internalFailure("unknown unary operator ("
					+ expr.op.getClass().getName() + ")" + expr.op, filename,
					expr);
		}
		
		expr.type = (Nominal) src.type();		
		
		return expr;
	}
	
	private Expr propagate(Expr.Comprehension expr,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) {
		return expr;
	}
	
	private Expr propagate(Expr.Constant expr,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) {
		return expr;
	}

	private Expr propagate(Expr.Convert c,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {
		c.expr = propagate(c.expr,environment,imports);		
		c.type = resolver.resolveAsType(c.unresolvedType, imports);
		Type from = c.expr.type().raw();		
		Type to = c.type.raw();
		if (!Type.isExplicitCoerciveSubtype(to, from)) {			
			syntaxError(errorMessage(SUBTYPE_ERROR, to, from), filename, c);
		}	
		return c;
	}
	
	private Expr propagate(Expr.Function expr,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) {
		return null;
	}
	
	private Expr propagate(Expr.AbstractInvoke expr,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {
		
		// first, propagate through receiver and parameters.
		
		Expr receiver = expr.qualification;
		
		if(receiver != null) {
			receiver = propagate(receiver,environment,imports);
			expr.qualification = receiver;						
		}
		
		ArrayList<Expr> exprArgs = expr.arguments;
		ArrayList<Nominal<Type>> paramTypes = new ArrayList<Nominal<Type>>();
		for(int i=0;i!=exprArgs.size();++i) {
			Expr arg = propagate(exprArgs.get(i),environment,imports);
			exprArgs.set(i, arg);
			paramTypes.add((Nominal) arg.type());			
		}
		
		// second, determine whether we already have a fully qualified name and
		// then lookup the appropriate function.
		
		if(receiver instanceof Expr.ModuleAccess) {
			// Yes, this function or method is qualified
			Expr.ModuleAccess ma = (Expr.ModuleAccess) receiver;
			NameID name = new NameID(ma.mid,expr.name);
			Nominal<Type.Function> funType = resolver.resolveAsFunctionOrMethod(name,  paramTypes);	
			Expr.FunctionCall r = new Expr.FunctionCall(name, ma, exprArgs, expr.attributes());
			r.functionType = funType;
			// FIXME: loss of nominal information
			r.returnType = new Nominal<Type>(funType.raw().ret(),funType.raw().ret());
			return r;
		} else {
			// no, function is not qualified ... need to search for it.
			
			// third, lookup the function					
			if(receiver != null) {
				Type.Process rawRecType = checkType(expr.qualification.type().raw(),Type.Process.class,receiver);							
				Pair<NameID, Nominal<Type.Method>> p = resolver
						.resolveAsMessage(expr.name, rawRecType, paramTypes,
								imports);				
				Expr.MessageSend r = new Expr.MessageSend(p.first(), receiver,
						exprArgs, expr.synchronous, expr.attributes());			
				r.messageType = p.second();
				// FIXME: loss of nominal information
				r.returnType = new Nominal<Type>(p.second().raw().ret(),p.second().raw().ret());
				return r;
			} else {
				Pair<NameID, Nominal<Type.Function>> p = resolver.resolveAsFunctionOrMethod(expr.name, paramTypes, imports);
				Type.Function funType = p.second().raw();							
				if(funType instanceof Type.Method) {					
					Expr.MethodCall mc = new Expr.MethodCall(p.first(), null, exprArgs, expr.attributes());					
					mc.methodType = (Nominal) p.second();					
					// FIXME: loss of nominal information
					mc.returnType = new Nominal<Type>(p.second().raw().ret(),p.second().raw().ret());
					return mc;
				} else {
					Expr.FunctionCall mc = new Expr.FunctionCall(p.first(), null, exprArgs, expr.attributes());					
					mc.functionType = (Nominal) p.second();
					mc.returnType = new Nominal<Type>(p.second().raw().ret(),p.second().raw().ret());
					return mc;									
				}								
			}											
		}		
	}			
	
	private Expr propagate(Expr.AbstractIndexAccess expr,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {			
		expr.src = propagate(expr.src,environment,imports);
		expr.index = propagate(expr.index,environment,imports);		
		Type src = expr.src.type().raw();			
		
		// First, check whether this is still only an abstract access and, in
		// such case, upgrade it to the appropriate access expression.
		
		if (!(expr instanceof Expr.StringAccess
				|| expr instanceof Expr.ListAccess || expr instanceof Expr.DictionaryAccess)) {
			// first time through
			if (Type.isImplicitCoerciveSubtype(Type.T_STRING, src)) {
				expr = new Expr.StringAccess(expr.src, expr.index,
						expr.attributes());
			} else if (Type.isImplicitCoerciveSubtype(
					Type.List(Type.T_ANY, false), src)) {
				expr = new Expr.ListAccess(expr.src, expr.index,
						expr.attributes());
			} else if (Type.isImplicitCoerciveSubtype(
					Type.Dictionary(Type.T_ANY, Type.T_ANY), src)) {
				expr = new Expr.DictionaryAccess(expr.src, expr.index,
						expr.attributes());
			} else {
				syntaxError("invalid index expression", filename, expr);
			}
		}
		
		// Second, determine the expanded src type for this access expression
		// and check the key value.
		
		if(expr instanceof Expr.StringAccess) {
			checkIsSubtype(Type.T_STRING,expr.src);	
			checkIsSubtype(Type.T_INT,expr.index);				
		} else if(expr instanceof Expr.ListAccess) {
			Expr.ListAccess la = (Expr.ListAccess) expr; 
			Type.List list = Type.effectiveListType(src);			
			if(list == null) {
				syntaxError(errorMessage(INVALID_LIST_EXPRESSION),filename,expr);				
			}
			checkIsSubtype(Type.T_INT,expr.index);	
			// FIXME: lost nominal information
			la.srcType = new Nominal<Type.List>(list,list);			
			la.elementType = new Nominal<Type>(list.element(),list.element());
		} else {
			Expr.DictionaryAccess da = (Expr.DictionaryAccess) expr; 
			Type.Dictionary dict = Type.effectiveDictionaryType(src);
			if(dict == null) {
				syntaxError(errorMessage(INVALID_DICTIONARY_EXPRESSION),filename,expr);
			}			
			checkIsSubtype(dict.key(),expr.index);			
			// FIXME: lost nominal information
			da.srcType = new Nominal<Type.Dictionary>(dict,dict);			
			da.elementType = new Nominal<Type>(dict.value(),dict.value());
		}
		
		return expr;
	}
	
	private Expr propagate(Expr.AbstractLength expr,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {			
		expr.src = propagate(expr.src,environment,imports);			
		Type src = expr.src.type().raw();				
	
		// First, check whether this is still only an abstract access and, in
				// such case, upgrade it to the appropriate access expression.

		if (!(expr instanceof Expr.StringLength)
				&& Type.isImplicitCoerciveSubtype(Type.T_STRING, src)) {
			expr = new Expr.StringLength(expr.src,expr.attributes());
		} else if (!(expr instanceof Expr.ListLength)
				&& Type.isImplicitCoerciveSubtype(Type.List(Type.T_ANY, false),
						src)) {
			expr = new Expr.ListLength(expr.src,expr.attributes());
		} else if (!(expr instanceof Expr.SetLength)
				&& Type.isImplicitCoerciveSubtype(Type.Set(Type.T_ANY, false),
						src)) {
			expr = new Expr.SetLength(expr.src,expr.attributes());
		} else if (!(expr instanceof Expr.DictionaryLength)) {
			expr = new Expr.DictionaryLength(expr.src,expr.attributes());
		}

		// Second, determine the expanded src type for this access expression
		// and check the key value.

		if(expr instanceof Expr.StringLength) {
			checkIsSubtype(Type.T_STRING,expr.src);								
		} else if(expr instanceof Expr.ListLength) {
			Expr.ListLength ll = (Expr.ListLength) expr; 
			Type.List list = Type.effectiveListType(src);			
			if(list == null) {
				syntaxError(errorMessage(INVALID_LIST_EXPRESSION),filename,expr);				
			}
			ll.srcType = new Nominal<Type.List>(expr.type().nominal(),list);
		} else if(expr instanceof Expr.SetLength) {
			Expr.SetLength sl = (Expr.SetLength) expr; 
			Type.Set set = Type.effectiveSetType(src);			
			if(set == null) {
				syntaxError(errorMessage(INVALID_SET_EXPRESSION),filename,expr);				
			}
			sl.srcType = new Nominal<Type.Set>(expr.type().nominal(),set);			
		} else {
			Expr.DictionaryLength dl = (Expr.DictionaryLength) expr; 
			Type.Dictionary dict = Type.effectiveDictionaryType(src);
			if(dict == null) {
				syntaxError(errorMessage(INVALID_DICTIONARY_EXPRESSION),filename,expr);
			}				
			dl.srcType = new Nominal<Type.Dictionary>(expr.type().nominal(),dict);	
		}
		
		return expr;
	}
	
	private Expr propagate(Expr.AbstractVariable expr,
			RefCountedHashMap<String, Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {

		Nominal<Type> type = environment.get(expr.var);

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
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) {
		Type nominalElementType = Type.T_VOID;
		Type rawElementType = Type.T_VOID;
		
		ArrayList<Expr> exprs = expr.arguments;
		for(int i=0;i!=exprs.size();++i) {
			Expr e = propagate(exprs.get(i),environment,imports);
			Nominal<Type> t = (Nominal) e.type();
			exprs.set(i,e);
			nominalElementType = Type.Union(t.nominal(),nominalElementType);
			rawElementType = Type.Union(t.raw(),rawElementType);
		}
				
		Type nominalType = Type.Set(nominalElementType, false);
		Type.Set rawType = (Type.Set) Type.Set(rawElementType, false);
		
		expr.type = new Nominal<Type.Set>(nominalType,rawType);
		
		return expr;
	}
	
	private Expr propagate(Expr.List expr,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) {
		
		Type nominalElementType = Type.T_VOID;
		Type rawElementType = Type.T_VOID;
		
		ArrayList<Expr> exprs = expr.arguments;
		for(int i=0;i!=exprs.size();++i) {
			Expr e = propagate(exprs.get(i),environment,imports);
			Nominal<Type> t = (Nominal) e.type();
			exprs.set(i,e);
			nominalElementType = Type.Union(t.nominal(),nominalElementType);
			rawElementType = Type.Union(t.raw(),rawElementType);
		}
		
		Type nominalType = Type.List(nominalElementType, false);
		Type.List rawType = (Type.List) Type.List(rawElementType, false);
		
		expr.type = new Nominal<Type.List>(nominalType,rawType);
		
		
		return expr;
	}
	
	
	private Expr propagate(Expr.Dictionary expr,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) {
		Nominal<Type> keyType = Nominal.T_VOID;
		Nominal<Type> valueType = Nominal.T_VOID;		
				
		ArrayList<Pair<Expr,Expr>> exprs = expr.pairs;
		for(int i=0;i!=exprs.size();++i) {
			Pair<Expr,Expr> p = exprs.get(i);
			Expr key = propagate(p.first(),environment,imports);
			Expr value = propagate(p.second(),environment,imports);
			Nominal<Type> kt = (Nominal) key.type();
			Nominal<Type> vt = (Nominal) value.type();
			exprs.set(i,new Pair<Expr,Expr>(key,value));
			
			keyType = Nominal.Union(kt,keyType);			
			valueType = Nominal.Union(vt,valueType);
		}
		
		expr.type = Nominal.Dictionary(keyType,valueType);
		
		return expr;
	}
	

	private Expr propagate(Expr.Record expr,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) {
		
		HashMap<String,Expr> exprFields = expr.fields;
		HashMap<String,Type> nominalFieldTypes = new HashMap<String,Type>();
		HashMap<String,Type> rawFieldTypes = new HashMap<String,Type>();
		
		ArrayList<String> fields = new ArrayList<String>(exprFields.keySet());
		for(String field : fields) {
			Expr e = propagate(exprFields.get(field),environment,imports);
			Nominal<Type> t = (Nominal) e.type();
			exprFields.put(field,e);
			nominalFieldTypes.put(field,t.nominal());
			rawFieldTypes.put(field,t.raw());			
		}		
		
		Type nominalType = Type.Record(false, nominalFieldTypes);
		Type.Record rawType = (Type.Record) Type.Record(false, rawFieldTypes);
		expr.type = new Nominal<Type.Record>(nominalType,rawType);
		
		return expr;
	}
	
	private Expr propagate(Expr.Tuple expr,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) {
		ArrayList<Expr> exprFields = expr.fields;
		ArrayList<Type> nominalFieldTypes = new ArrayList<Type>();
		ArrayList<Type> rawFieldTypes = new ArrayList<Type>();
		
		for(int i=0;i!=exprFields.size();++i) {
			Expr e = propagate(exprFields.get(i),environment,imports);
			Nominal<Type> t = (Nominal) e.type();
			exprFields.set(i,e);
			nominalFieldTypes.add(t.nominal());
			rawFieldTypes.add(t.raw());
		}
		
		Type nominalType = Type.Tuple(nominalFieldTypes);
		Type.Tuple rawType = (Type.Tuple) Type.Tuple(rawFieldTypes);
		
		expr.type = new Nominal<Type.Tuple>(nominalType,rawType);
		
		return expr;
	}
	
	private Expr propagate(Expr.SubList expr,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) {	
		
		expr.src = propagate(expr.src,environment,imports);
		expr.start = propagate(expr.start,environment,imports);
		expr.end = propagate(expr.end,environment,imports);
		
		checkIsSubtype(Type.List(Type.T_ANY, false),expr.src);
		checkIsSubtype(Type.T_INT,expr.start);
		checkIsSubtype(Type.T_INT,expr.end);
		
		return expr;
	}
	
	private Expr propagate(Expr.AbstractDotAccess expr,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) {	
		
		if (expr instanceof Expr.PackageAccess
				|| expr instanceof Expr.ModuleAccess) {
			// don't need to do anything in these cases.
			return expr;
		}
		
		Expr src = propagate(expr.src,environment,imports);
		expr.src = src;
				
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
				syntaxError(errorMessage(INVALID_PACKAGE_ACCESS),filename,expr);
				return null; // deadcode
			}			
		} else if(src instanceof Expr.ModuleAccess) {
			// must be a constant access
			Expr.ModuleAccess ma = (Expr.ModuleAccess) src; 													
			NameID nid = new NameID(ma.mid,expr.name);
			if (resolver.isName(nid)) {
				Expr.ConstantAccess ca = new Expr.ConstantAccess(ma,
						expr.name, nid, expr.attributes());
				// FIXME: there is definitely a bug here, as we need to initialise the value.
				// ca.value = ?
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
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) {
		Nominal<Type> srcType = (Nominal) ra.src.type();
		Type.Record rawType = Type.effectiveRecordType(srcType.raw());
		if(rawType == null) {
			syntaxError(errorMessage(RECORD_TYPE_REQUIRED,srcType.raw()),filename,ra);
		} 
		Type fieldType = rawType.fields().get(ra.name);
		if(fieldType == null) {
			syntaxError(errorMessage(RECORD_MISSING_FIELD),filename,ra);
		}
		ra.srcType = new Nominal<Type.Record>(srcType.nominal(),rawType);		
		// FIXME: loss of nominal information here
		ra.fieldType = new Nominal<Type>(fieldType,fieldType);
		return ra;
	}	
	
	private Expr propagate(Expr.ConstantAccess expr,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) {
		return null;
	}			

	private Expr propagate(Expr.Spawn expr,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) {
		return null;
	}
	
	private Expr propagate(Expr.TypeVal expr,
			RefCountedHashMap<String,Nominal<Type>> environment,
			ArrayList<WhileyFile.Import> imports) throws ResolveError {
		expr.type = resolver.resolveAsType(expr.unresolvedType, imports); 
		return expr;
	}
	
	private <T extends Type> T checkType(Type t, Class<T> clazz,
			SyntacticElement elem) {
		if (clazz.isInstance(t)) {
			return (T) t;
		} else {
			syntaxError(errorMessage(SUBTYPE_ERROR, clazz.getName(), t),
					filename, elem);
			return null;
		}
	}
	
	// Check t1 :> t2
	private void checkIsSubtype(Nominal<Type> t1, Nominal<Type> t2,
			SyntacticElement elem) {
		if (!Type.isImplicitCoerciveSubtype(t1.raw(), t2.raw())) {
			syntaxError(
					errorMessage(SUBTYPE_ERROR, t1.nominal(), t2.nominal()),
					filename, elem);
		}
	}	
	
	private void checkIsSubtype(Type t1, Expr t2) {
		if (!Type.isImplicitCoerciveSubtype(t1, t2.type().raw())) {
			// We use the nominal type for error reporting, since this includes
			// more helpful names.
			syntaxError(errorMessage(SUBTYPE_ERROR, t1, t2.type().nominal()),
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
		} else if (t instanceof UnresolvedType.Process) {			
			UnresolvedType.Process ut = (UnresolvedType.Process) t;
			addExposedNames(new Expr.ProcessAccess(src),
					ut.element, environment);
		}
	}
	
	private abstract static class Scope {
		public abstract void free();
	}
	
	private static final class Handler {
		public final Type exception;
		public final String variable;
		public RefCountedHashMap<String,Nominal<Type>> environment;
		
		public Handler(Type exception, String variable) {
			this.exception = exception;
			this.variable = variable;
			this.environment = new RefCountedHashMap<String,Nominal<Type>>();
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
		public RefCountedHashMap<String,Nominal<Type>> environment;
		
		public void free() {
			environment.free();
		}
	}

	private static final class ContinueScope extends Scope {
		public RefCountedHashMap<String,Nominal<Type>> environment;
		
		public void free() {
			environment.free();
		}
	}
	
	private static final RefCountedHashMap<String,Nominal<Type>> BOTTOM = new RefCountedHashMap<String,Nominal<Type>>();
	
	private static final RefCountedHashMap<String, Nominal<Type>> join(
			RefCountedHashMap<String, Nominal<Type>> lhs,
			RefCountedHashMap<String, Nominal<Type>> rhs) {
		
		// first, need to check for the special bottom value case.
		
		if(lhs == BOTTOM) {
			return rhs;
		} else if(rhs == BOTTOM) {
			return lhs;
		}
		
		// ok, not bottom so compute intersection.
		
		RefCountedHashMap<String,Nominal<Type>> result = new RefCountedHashMap<String,Nominal<Type>>();
		for(String key : lhs.keySet()) {
			if(rhs.containsKey(key)) {
				Nominal<Type> lhs_t = lhs.get(key);
				Nominal<Type> rhs_t = rhs.get(key);
				Type nominalType = Type.Union(lhs_t.first(),rhs_t.first());
				Type rawType = Type.Union(lhs_t.second(),rhs_t.second());
				result.put(key, new Nominal<Type>(nominalType,rawType));
			}
		}
		
		return result;
	}	
}
