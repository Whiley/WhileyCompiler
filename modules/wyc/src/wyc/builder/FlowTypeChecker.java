package wyc.builder;

import static wybs.lang.SyntaxError.internalFailure;
import static wybs.lang.SyntaxError.syntaxError;
import static wyc.lang.WhileyFile.internalFailure;
import static wyc.lang.WhileyFile.syntaxError;
import static wyil.util.ErrorMessages.*;

import java.math.BigDecimal;
import java.util.*;

import wyautl_old.lang.Automata;
import wyautl_old.lang.Automaton;
import wybs.lang.*;
import wybs.util.*;
import wyc.lang.*;
import wyc.lang.WhileyFile.Context;
import wyil.lang.Constant;
import wyil.lang.Type;
import wyil.lang.WyilFile;

/**
 * Propagates type information in a <i>flow-sensitive</i> fashion from declared
 * parameter and return types through variable declarations and assigned
 * expressions, to determine types for all intermediate expressions and
 * variables. During this propagation, type checking is performed to ensure
 * types are used soundly. For example:
 * 
 * <pre>
 * function sum([int] data) => int:
 *     int r = 0      // declared int type for r
 *     for v in data: // infers int type for v, based on type of data
 *         r = r + v  // infers int type for r + v, based on type of operands 
 *     return r       // infers int type for return expression
 * </pre>
 * 
 * <p>
 * The flow typing algorithm distinguishes between the <i>declared type</i> of a
 * variable and its <i>known type</i>. That is, the known type at any given
 * point is permitted to be more precise than the declared type (but not vice
 * versa). For example:
 * </p>
 * 
 * <pre>
 * function id(int x) => int:
 *    return x
 *    
 * function f(int y) => int:
 *    int|null x = y
 *    f(x)
 * </pre>
 * 
 * <p>
 * The above example is considered type safe because the known type of
 * <code>x</code> at the function call is <code>int</code>, which differs from
 * its declared type (i.e. <code>int|null</code>).
 * </p>
 * 
 * <p>
 * Loops present an interesting challenge for type propagation. Consider this
 * example:
 * </p>
 * 
 * <pre>
 * function loopy(int max) => real:
 *     var i = 0
 *     while i < max:
 *         i = i + 0.5
 *     return i
 * </pre>
 * 
 * <p>
 * On the first pass through the loop, variable <code>i</code> is inferred to
 * have type <code>int</code> (based on the type of the constant <code>0</code>
 * ). However, the add expression is inferred to have type <code>real</code>
 * (based on the type of the rhs) and, hence, the resulting type inferred for
 * <code>i</code> is <code>real</code>. At this point, the loop must be
 * reconsidered taking into account this updated type for <code>i</code>.
 * </p>
 * 
 * <p>
 * The operation of the flow type checker splits into two stages:
 * </p>
 * <ul>
 * <li><b>Global Propagation.</b> During this stage, all named types are checked
 * and expanded.</li>
 * <li><b>Local Propagation.</b> During this stage, types are propagated through
 * statements and expressions (as above).</li>
 * </ul>
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
public class FlowTypeChecker {
	
	private WhileyBuilder builder;
	private String filename;
	private WhileyFile.FunctionOrMethod current;
	
	/**
	 * The constant cache contains a cache of expanded constant values.
	 */
	private final HashMap<NameID, Constant> constantCache = new HashMap<NameID, Constant>();
	
	public FlowTypeChecker(WhileyBuilder builder) {
		this.builder = builder;
	}
	
	// =========================================================================
	// WhileyFile(s) 
	// =========================================================================
	
	public void propagate(List<WhileyFile> files) {
		for(WhileyFile wf : files) {
			propagate(wf);
		}
	}
	
	public void propagate(WhileyFile wf) {
		this.filename = wf.filename;

		for (WhileyFile.Declaration decl : wf.declarations) {
			try {
				if (decl instanceof WhileyFile.FunctionOrMethod) {
					propagate((WhileyFile.FunctionOrMethod) decl);
				} else if (decl instanceof WhileyFile.Type) {
					propagate((WhileyFile.Type) decl);
				} else if (decl instanceof WhileyFile.Constant) {
					propagate((WhileyFile.Constant) decl);
				}
			} catch (ResolveError e) {
				syntaxError(errorMessage(RESOLUTION_ERROR, e.getMessage()),
						filename, decl, e);
			} catch (SyntaxError e) {
				throw e;
			} catch (Throwable t) {
				internalFailure(t.getMessage(), filename, decl, t);
			}
		}
	}
	
	// =========================================================================
	// Type Declarations
	// =========================================================================

	/**
	 * Propagate and check types for a given type declaration.
	 * 
	 * @param td
	 *            Type declaration to check.
	 * @throws Exception
	 */
	public void propagate(WhileyFile.Type td) throws Exception {		
		// first, resolve the declared type
		td.resolvedType = resolveAsType(td.pattern.toSyntacticType(), td);
		
		if(td.constraint != null) {						
			// second, construct the appropriate typing environment			
			Environment environment = new Environment();			
			environment = addDeclaredVariables(td.pattern,environment,td);			
			// third, propagate type information through the constraint 			
			td.constraint = resolve(td.constraint,environment,td);
		}
	}

	// =========================================================================
	// Constrant Declarations
	// =========================================================================

	public void propagate(WhileyFile.Constant cd) throws Exception {
		NameID nid = new NameID(cd.file().module, cd.name);
		cd.resolvedValue = resolveAsConstant(nid);
	}
	
	// =========================================================================
	// Function Declarations
	// =========================================================================

	public void propagate(WhileyFile.FunctionOrMethod d) throws Exception {
		this.current = d; // ugly
		Environment environment = new Environment();

		for (WhileyFile.Parameter p : d.parameters) {
			environment = environment.put(p.name, resolveAsType(p.type, d));
		}

		final List<Expr> d_requires = d.requires;
		for (int i = 0; i != d_requires.size(); ++i) {
			Expr condition = d_requires.get(i);
			condition = resolve(condition, environment.clone(), d);
			d_requires.set(i, condition);
		}

		final List<Expr> d_ensures = d.ensures;
		if (d_ensures.size() > 0) {
			Environment ensuresEnvironment = addDeclaredVariables(d.ret,
					environment.clone(), d);

			for (int i = 0; i != d_ensures.size(); ++i) {
				Expr condition = d_ensures.get(i);
				condition = resolve(condition, ensuresEnvironment, d);
				d_ensures.set(i, condition);
			}
		}

		if (d instanceof WhileyFile.Function) {
			WhileyFile.Function f = (WhileyFile.Function) d;
			f.resolvedType = resolveAsType(f.unresolvedType(), d);
		} else {
			WhileyFile.Method m = (WhileyFile.Method) d;
			m.resolvedType = resolveAsType(m.unresolvedType(), d);
		}

		propagate(d.statements, environment);
	}
	
	// =========================================================================
	// Blocks & Statements
	// =========================================================================

	private Environment propagate(ArrayList<Stmt> body, Environment environment) {

		for (int i = 0; i != body.size(); ++i) {
			Stmt stmt = body.get(i);
			if (stmt instanceof Expr) {
				body.set(i, (Stmt) resolve((Expr) stmt, environment, current));
			} else {
				environment = propagate(stmt, environment);
			}
		}

		return environment;
	}
	
	private Environment propagate(Stmt stmt,
			Environment environment) {
				
		try {
			if(stmt instanceof Stmt.VariableDeclaration) {
				return propagate((Stmt.VariableDeclaration) stmt,environment);
			} else if(stmt instanceof Stmt.Assign) {
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
			} else if(stmt instanceof Stmt.Assume) {
				return propagate((Stmt.Assume) stmt,environment);
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
		stmt.expr = resolve(stmt.expr,environment,current);
		checkIsSubtype(Type.T_BOOL,stmt.expr);
		return environment;
	}
	
	private Environment propagate(Stmt.Assume stmt,
			Environment environment) {
		stmt.expr = resolve(stmt.expr,environment,current);
		checkIsSubtype(Type.T_BOOL,stmt.expr);
		return environment;
	}
	
	private Environment propagate(Stmt.VariableDeclaration stmt,
			Environment environment) throws Exception {

		// First, resolve declared type
		Nominal type = resolveAsType(stmt.type,current);
		
		// First, resolve type of initialiser
		if(stmt.expr != null) {
			stmt.expr = resolve(stmt.expr,environment,current);
			checkIsSubtype(type,stmt.expr);
		}
		
		// Second, update environment accordingly. Observe that we can safely
		// assume the variable is not already declared in the enclosing scope
		// because the parser checks this for us.		
		environment = environment.put(stmt.name, type);	
		
		// Done.
		return environment;
	}
	
	private Environment propagate(Stmt.Assign stmt,
			Environment environment) throws Exception {

		Expr.LVal lhs = propagate(stmt.lhs,environment);
		Expr rhs = resolve(stmt.rhs,environment,current);
				
		if(lhs instanceof Expr.RationalLVal) {
			// represents a destructuring assignment
			Expr.RationalLVal tv = (Expr.RationalLVal) lhs;

			if(!Type.isImplicitCoerciveSubtype(Type.T_REAL, rhs.result().raw())) {
				syntaxError("real value expected, got " + rhs.result(),filename,rhs);				
			} 

			if (tv.numerator instanceof Expr.AssignedVariable
					&& tv.denominator instanceof Expr.AssignedVariable) {
				Expr.AssignedVariable lv = (Expr.AssignedVariable) tv.numerator; 				
				Expr.AssignedVariable rv = (Expr.AssignedVariable) tv.denominator;
				lv.type = Nominal.T_VOID;
				rv.type = Nominal.T_VOID;
				lv.afterType = Nominal.T_INT; 
				rv.afterType = Nominal.T_INT;
				environment = environment.put(lv.var, Nominal.T_INT);
				environment = environment.put(rv.var, Nominal.T_INT);
			} else {
				syntaxError(errorMessage(INVALID_TUPLE_LVAL),filename,lhs);
			}

		} else if(lhs instanceof Expr.Tuple) {
			// represents a destructuring assignment
			Expr.Tuple tv = (Expr.Tuple) lhs;
			ArrayList<Expr> tvFields = tv.fields;
			
			// FIXME: loss of nominal information here			
			Type rawRhs = rhs.result().raw();		
			Nominal.EffectiveTuple tupleRhs = expandAsEffectiveTuple(rhs.result());
			
			// FIXME: the following is something of a kludge. It would also be
			// nice to support more expressive destructuring assignment
			// operations.
			if(tupleRhs == null) {
				syntaxError("tuple value expected, got " + rhs.result().nominal(),filename,rhs);
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
			Nominal.EffectiveIndexible srcType = la.srcType;
			afterType = (Nominal) srcType.update(la.index.result(), afterType);
			return inferAfterType((Expr.LVal) la.src, 
					afterType);
		} else if(lv instanceof Expr.FieldAccess) {
			Expr.FieldAccess la = (Expr.FieldAccess) lv;
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
		stmt.expr = resolve(stmt.expr,environment,current);				
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
				tmp = resolve(stmt.condition,true,old.clone(),current).second();
				environment = join(orig.clone(),propagate(stmt.body,tmp));
			} else {
				firstTime=false;
				environment = join(orig.clone(),propagate(stmt.body,old));
			}					
			old.free(); // hacky, but safe
		} while(!environment.equals(old));

		List<Expr> stmt_invariants = stmt.invariants;
		for (int i = 0; i != stmt_invariants.size(); ++i) {
			Expr invariant = stmt_invariants.get(i);
			invariant = resolve(invariant, environment, current);
			stmt_invariants.set(i, invariant);
			checkIsSubtype(Type.T_BOOL, invariant);
		}
		
		Pair<Expr,Environment> p = resolve(stmt.condition,false,environment,current);
		stmt.condition = p.first();
		environment = p.second();
		
		return environment;
	}
	
	private Environment propagate(Stmt.ForAll stmt,
			Environment environment) throws Exception {
		
		stmt.source = resolve(stmt.source,environment,current);
		Nominal.EffectiveCollection srcType = expandAsEffectiveCollection(stmt.source.result()); 		
		stmt.srcType = srcType;
		
		if(srcType == null) {
			syntaxError(errorMessage(INVALID_SET_OR_LIST_EXPRESSION),filename,stmt);
		}
		
		// At this point, the major task is to determine what the types for the
		// iteration variables declared in the for loop. More than one variable
		// is permitted in some cases.
		
		Nominal[] elementTypes = new Nominal[stmt.variables.size()];
		if(elementTypes.length == 2 && srcType instanceof Nominal.EffectiveMap) {
			Nominal.EffectiveMap dt = (Nominal.EffectiveMap) srcType;
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
			stmt.invariant = resolve(stmt.invariant, environment, current);
			checkIsSubtype(Type.T_BOOL,stmt.invariant);
		}
				
		return environment;
	}
	
	private Environment propagate(Stmt.IfElse stmt,
			Environment environment) {
		
		// First, check condition and apply variable retypings.
		Pair<Expr,Environment> p1,p2;
		
		p1 = resolve(stmt.condition,true,environment.clone(),current);
		p2 = resolve(stmt.condition,false,environment,current);
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
	
	private Environment propagate(Stmt.Return stmt, Environment environment)
			throws Exception {

		if (stmt.expr != null) {
			stmt.expr = resolve(stmt.expr, environment, current);
			Nominal rhs = stmt.expr.result();
			checkIsSubtype(current.resolvedType().ret(), rhs, stmt.expr);
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
		
		stmt.expr = resolve(stmt.expr,environment,current);		
		
		Environment finalEnv = null;
		boolean hasDefault = false;
		
		for(Stmt.Case c : stmt.cases) {
			
			// first, resolve the constants
			
			ArrayList<Constant> values = new ArrayList<Constant>();
			for(Expr e : c.expr) {
				values.add(resolveAsConstant(e,current));				
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
		stmt.expr = resolve(stmt.expr,environment,current);
		return BOTTOM;
	}
	
	private Environment propagate(Stmt.TryCatch stmt,
			Environment environment) throws Exception {
		

		for(Stmt.Catch handler : stmt.catches) {
			
			// FIXME: need to deal with handler environments properly!
			try {
				Nominal type = resolveAsType(handler.unresolvedType,current); 
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
			tmp = resolve(stmt.condition,true,old.clone(),current).second();			
			environment = join(orig.clone(),propagate(stmt.body,tmp));			
			old.free(); // hacky, but safe
		} while(!environment.equals(old));
		
		List<Expr> stmt_invariants = stmt.invariants;
		for (int i = 0; i != stmt_invariants.size(); ++i) {
			Expr invariant = stmt_invariants.get(i);
			invariant = resolve(invariant, environment, current);
			stmt_invariants.set(i, invariant);
			checkIsSubtype(Type.T_BOOL, invariant);
		}
				
		Pair<Expr,Environment> p = resolve(stmt.condition,false,environment,current);
		stmt.condition = p.first();
		environment = p.second();			
		
		return environment;
	}

	// =========================================================================
	// LVals
	// =========================================================================
	
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
			} else if(lval instanceof Expr.RationalLVal) {
				Expr.RationalLVal av = (Expr.RationalLVal) lval;
				av.numerator = propagate(av.numerator,environment);
				av.denominator = propagate(av.denominator,environment);
				return av;
			} else if(lval instanceof Expr.Dereference) {
				Expr.Dereference pa = (Expr.Dereference) lval;
				Expr.LVal src = propagate((Expr.LVal) pa.src,environment);												
				pa.src = src;
				pa.srcType = expandAsReference(src.result());							
				return pa;
			} else if(lval instanceof Expr.IndexOf) {
				// this indicates either a list, string or dictionary update
				Expr.IndexOf ai = (Expr.IndexOf) lval;				
				Expr.LVal src = propagate((Expr.LVal) ai.src,environment);				
				Expr index = resolve(ai.index,environment,current);
				ai.src = src;
				ai.index = index;
				Nominal.EffectiveIndexible srcType = expandAsEffectiveMap(src.result());
				if(srcType == null) {
					syntaxError(errorMessage(INVALID_LVAL_EXPRESSION),filename,lval);
				}
				ai.srcType = srcType;
				return ai;
			} else if(lval instanceof Expr.AbstractDotAccess) {
				// this indicates a record update
				Expr.AbstractDotAccess ad = (Expr.AbstractDotAccess) lval;
				Expr.LVal src = propagate((Expr.LVal) ad.src,environment);
				Expr.FieldAccess ra = new Expr.FieldAccess(src, ad.name, ad.attributes());
				Nominal.EffectiveRecord srcType = expandAsEffectiveRecord(src.result());
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
	
	/**
	 * The purpose of this method is to add variable names declared within a
	 * type pattern. For example, as follows:
	 * 
	 * <pre>
	 * define tup as {int x, int y} where x < y
	 * </pre>
	 * 
	 * In this case, <code>x</code> and <code>y</code> are variable names
	 * declared as part of the pattern.
	 * 
	 * @param src
	 * @param t
	 * @param environment
	 */
	private Environment addDeclaredVariables(TypePattern pattern,
			Environment environment, WhileyFile.Context context) {
		
		if(pattern instanceof TypePattern.Leaf) {						
			// do nout!
		} else if(pattern instanceof TypePattern.Union) {						
			// FIXME: in principle, we can do better here. However, I leave this
			// unusual case for the future.
		} else if(pattern instanceof TypePattern.Intersection) {						
			// FIXME: in principle, we can do better here. However, I leave this
			// unusual case for the future.
		} else if(pattern instanceof TypePattern.Record) {
			TypePattern.Record tp = (TypePattern.Record) pattern;
			for(TypePattern element : tp.elements) {
				addDeclaredVariables(element,environment,context);
			}
		} else {
			TypePattern.Tuple tp = (TypePattern.Tuple) pattern;
			for(TypePattern element : tp.elements) {
				addDeclaredVariables(element,environment,context);
			}
		}
		
		if (pattern.var != null) {
			Nominal type = resolveAsType(pattern.toSyntacticType(),
					context);
			environment = environment.put(pattern.var, type);
		}
		
		return environment;
	}
		
	// =========================================================================
	// Expressions
	// =========================================================================
	
	public Pair<Expr, Environment> resolve(Expr expr, boolean sign,
			Environment environment, Context context) {
		
		if(expr instanceof Expr.UnOp) {
			return resolve((Expr.UnOp)expr,sign,environment,context);		
		} else if(expr instanceof Expr.BinOp) {  
			return resolve((Expr.BinOp)expr,sign,environment,context);
		} else {
			// for all others just default back to the base rules for expressions.
			expr = resolve(expr,environment,context);
			checkIsSubtype(Type.T_BOOL,expr,context);
			return new Pair(expr,environment);
		}		
	}
	
	private Pair<Expr, Environment> resolve(Expr.UnOp expr, boolean sign,
			Environment environment, Context context) {
		Expr.UnOp uop = (Expr.UnOp) expr; 
		if(uop.op == Expr.UOp.NOT) { 
			Pair<Expr,Environment> p = resolve(uop.mhs,!sign,environment,context);
			uop.mhs = p.first();			
			checkIsSubtype(Type.T_BOOL,uop.mhs,context);
			uop.type = Nominal.T_BOOL;
			return new Pair(uop,p.second());
		} else {
			syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION),context,expr);
			return null; // deadcode
		}	
	}
	
	private Pair<Expr, Environment> resolve(Expr.BinOp bop, boolean sign,
			Environment environment, Context context) {		
		Expr.BOp op = bop.op;
		
		switch (op) {
		case AND:
		case OR:
		case XOR:
			return resolveNonLeafCondition(bop,sign,environment,context);
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
			return resolveLeafCondition(bop,sign,environment,context);
		default:
			syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context, bop);
			return null; // dead code
		}		
	}
	
	private Pair<Expr, Environment> resolveNonLeafCondition(
			Expr.BinOp bop,
			boolean sign,
			Environment environment, Context context) {
		Expr.BOp op = bop.op;
		Pair<Expr,Environment> p;
		boolean followOn = (sign && op == Expr.BOp.AND) || (!sign && op == Expr.BOp.OR);
		
		if(followOn) {			
			p = resolve(bop.lhs,sign,environment.clone(),context);			
			bop.lhs = p.first();
			p = resolve(bop.rhs,sign,p.second(),context);
			bop.rhs = p.first();
			environment = p.second();
		} else {
			// We could do better here
			p = resolve(bop.lhs,sign,environment.clone(),context);
			bop.lhs = p.first();
			Environment local = p.second();
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
			p = resolve(bop.lhs,!sign,environment.clone(),context);
			p = resolve(bop.rhs,sign,p.second(),context);
			bop.rhs = p.first();
			environment = join(local,p.second());
		}
		
		checkIsSubtype(Type.T_BOOL,bop.lhs,context);
		checkIsSubtype(Type.T_BOOL,bop.rhs,context);	
		bop.srcType = Nominal.T_BOOL;
		
		return new Pair<Expr,Environment>(bop,environment);
	}
	
	private Pair<Expr, Environment> resolveLeafCondition(Expr.BinOp bop,
			boolean sign, Environment environment, Context context) {
		Expr.BOp op = bop.op;
		
		Expr lhs = resolve(bop.lhs,environment,context);
		Expr rhs = resolve(bop.rhs,environment,context);
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
				Nominal unconstrainedTestType = resolveAsUnconstrainedType(tv.unresolvedType,context);
				
				/**
				 * Determine the types guaranteed to hold on the true and false
				 * branches respectively. We have to use the negated
				 * unconstrainedTestType for the false branch because only that
				 * is guaranteed if the test fails. For example:
				 * 
				 * <pre>
				 * define nat as int where $ &gt;= 0
				 * define listnat as [int]|nat
				 * 
				 * int f([int]|int x):
				 *    if x if listnat:
				 *        x : [int]|int
				 *        ...
				 *    else:
				 *        x : int
				 * </pre>
				 * 
				 * The unconstrained type of listnat is [int], since nat is a
				 * constrained type.
				 */
				Nominal glbForFalseBranch = Nominal.intersect(lhs.result(),
						Nominal.Negation(unconstrainedTestType));
				Nominal glbForTrueBranch = Nominal.intersect(lhs.result(),
						tv.type);

				if(glbForFalseBranch.raw() == Type.T_VOID) {					
					// DEFINITE TRUE CASE										
					syntaxError(errorMessage(BRANCH_ALWAYS_TAKEN), context, bop);
				} else if (glbForTrueBranch.raw() == Type.T_VOID) {				
					// DEFINITE FALSE CASE	
					syntaxError(errorMessage(INCOMPARABLE_OPERANDS, lhsRawType, tv.type.raw()),
							context, bop);			
				} 
				
				// Finally, if the lhs is local variable then update its
				// type in the resulting environment. 
				if(lhs instanceof Expr.LocalVariable) {
					Expr.LocalVariable lv = (Expr.LocalVariable) lhs;
					Nominal newType;
					if(sign) {
						newType = glbForTrueBranch;
					} else {						
						newType = glbForFalseBranch;						
					}										
					environment = environment.put(lv.var,newType);
				}
			} else {
				// In this case, we can't update the type of the lhs since
				// we don't know anything about the rhs. It may be possible
				// to support bounds here in order to do that, but frankly
				// that's future work :)
				checkIsSubtype(Type.T_META,rhs,context);
			}	

			bop.srcType = lhs.result();
			break;
		case ELEMENTOF:			
			Type.EffectiveList listType = rhsRawType instanceof Type.EffectiveList ? (Type.EffectiveList) rhsRawType : null;
			Type.EffectiveSet setType = rhsRawType instanceof Type.EffectiveSet ? (Type.EffectiveSet) rhsRawType : null;			
			
			if (listType != null && !Type.isImplicitCoerciveSubtype(listType.element(), lhsRawType)) {
				syntaxError(errorMessage(INCOMPARABLE_OPERANDS, lhsRawType,listType.element()),
						context, bop);
			} else if (setType != null && !Type.isImplicitCoerciveSubtype(setType.element(), lhsRawType)) {
				syntaxError(errorMessage(INCOMPARABLE_OPERANDS, lhsRawType,setType.element()),
						context, bop);
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
				checkIsSubtype(Type.T_SET_ANY,lhs,context);
				checkIsSubtype(Type.T_SET_ANY,rhs,context);
			} else {
				checkIsSubtype(Type.T_REAL,lhs,context);
				checkIsSubtype(Type.T_REAL,rhs,context);
			}
			if(Type.isImplicitCoerciveSubtype(lhsRawType,rhsRawType)) {
				bop.srcType = lhs.result();
			} else if(Type.isImplicitCoerciveSubtype(rhsRawType,lhsRawType)) {
				bop.srcType = rhs.result();
			} else {
				syntaxError(errorMessage(INCOMPARABLE_OPERANDS,lhsRawType,rhsRawType),context,bop);	
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
					&& ((Expr.Constant) rhs).value == Constant.V_NULL) {
				// bingo, special case
				Expr.LocalVariable lv = (Expr.LocalVariable) lhs;
				Nominal newType;
				Nominal glb = Nominal.intersect(lhs.result(), Nominal.T_NULL);
				if(glb.raw() == Type.T_VOID) {
					syntaxError(errorMessage(INCOMPARABLE_OPERANDS,lhs.result().raw(),Type.T_NULL),context,bop);	
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
					syntaxError(errorMessage(INCOMPARABLE_OPERANDS,lhsRawType,rhsRawType),context,bop);	
					return null; // dead code
				}		
			}
		}			
		
		return new Pair(bop,environment);
	}
	
	public Expr resolve(Expr expr, Environment environment, Context context) {
		
		try {
			if(expr instanceof Expr.BinOp) {
				return resolve((Expr.BinOp) expr,environment,context); 
			} else if(expr instanceof Expr.UnOp) {
				return resolve((Expr.UnOp) expr,environment,context); 
			} else if(expr instanceof Expr.Comprehension) {
				return resolve((Expr.Comprehension) expr,environment,context); 
			} else if(expr instanceof Expr.Constant) {
				return resolve((Expr.Constant) expr,environment,context); 
			} else if(expr instanceof Expr.Cast) {
				return resolve((Expr.Cast) expr,environment,context); 
			} else if(expr instanceof Expr.Map) {
				return resolve((Expr.Map) expr,environment,context); 
			} else if(expr instanceof Expr.AbstractFunctionOrMethod) {
				return resolve((Expr.AbstractFunctionOrMethod) expr,environment,context); 
			} else if(expr instanceof Expr.AbstractInvoke) {
				return resolve((Expr.AbstractInvoke) expr,environment,context); 
			} else if(expr instanceof Expr.AbstractIndirectInvoke) {
				return resolve((Expr.AbstractIndirectInvoke) expr,environment,context); 
			} else if(expr instanceof Expr.IndexOf) {
				return resolve((Expr.IndexOf) expr,environment,context); 
			} else if(expr instanceof Expr.Lambda) {
				return resolve((Expr.Lambda) expr,environment,context); 
			} else if(expr instanceof Expr.LengthOf) {
				return resolve((Expr.LengthOf) expr,environment,context); 
			} else if(expr instanceof Expr.AbstractVariable) {
				return resolve((Expr.AbstractVariable) expr,environment,context); 
			} else if(expr instanceof Expr.List) {
				return resolve((Expr.List) expr,environment,context); 
			} else if(expr instanceof Expr.Set) {
				return resolve((Expr.Set) expr,environment,context); 
			} else if(expr instanceof Expr.SubList) {
				return resolve((Expr.SubList) expr,environment,context); 
			} else if(expr instanceof Expr.SubString) {
				return resolve((Expr.SubString) expr,environment,context); 
			} else if(expr instanceof Expr.AbstractDotAccess) {
				return resolve((Expr.AbstractDotAccess) expr,environment,context); 
			} else if(expr instanceof Expr.Dereference) {
				return resolve((Expr.Dereference) expr,environment,context); 
			} else if(expr instanceof Expr.Record) {
				return resolve((Expr.Record) expr,environment,context); 
			} else if(expr instanceof Expr.New) {
				return resolve((Expr.New) expr,environment,context); 
			} else if(expr instanceof Expr.Tuple) {
				return  resolve((Expr.Tuple) expr,environment,context); 
			} else if(expr instanceof Expr.TypeVal) {
				return resolve((Expr.TypeVal) expr,environment,context); 
			} 
		} catch(ResolveError e) {
			syntaxError(errorMessage(RESOLUTION_ERROR,e.getMessage()),context,expr,e);
		} catch(SyntaxError e) {
			throw e;
		} catch(Throwable e) {
			internalFailure(e.getMessage(),context,expr,e);
			return null; // dead code
		}		
		internalFailure("unknown expression: " + expr.getClass().getName(),context,expr);
		return null; // dead code
	}
	
	private Expr resolve(Expr.BinOp expr,
			Environment environment, Context context) throws Exception {
		
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
			return resolve(expr,true,environment,context).first();
		}
		
		Expr lhs = resolve(expr.lhs,environment,context);
		Expr rhs = resolve(expr.rhs,environment,context);
		expr.lhs = lhs;
		expr.rhs = rhs;
		Type lhsRawType = lhs.result().raw();
		Type rhsRawType = rhs.result().raw();
	
		boolean lhs_set = Type.isImplicitCoerciveSubtype(Type.T_SET_ANY,lhsRawType);
		boolean rhs_set = Type.isImplicitCoerciveSubtype(Type.T_SET_ANY,rhsRawType);		
		boolean lhs_list = Type.isImplicitCoerciveSubtype(Type.T_LIST_ANY,lhsRawType);
		boolean rhs_list = Type.isImplicitCoerciveSubtype(Type.T_LIST_ANY,rhsRawType);
		boolean lhs_str = Type.isSubtype(Type.T_STRING,lhsRawType);
		boolean rhs_str = Type.isSubtype(Type.T_STRING,rhsRawType);
		
		Type srcType;

		if(lhs_str || rhs_str) {
			
			switch(expr.op) {				
			case LISTAPPEND:								
				expr.op = Expr.BOp.STRINGAPPEND;
			case STRINGAPPEND:
				break;
			default:			
				syntaxError("Invalid string operation: " + expr.op, context,
						expr);
			}
			
			srcType = Type.T_STRING;
		} else if(lhs_list && rhs_list) {
			checkIsSubtype(Type.T_LIST_ANY,lhs,context);
			checkIsSubtype(Type.T_LIST_ANY,rhs,context);
			Type.EffectiveList lel = (Type.EffectiveList) lhsRawType;
			Type.EffectiveList rel = (Type.EffectiveList) rhsRawType;
			
			switch(expr.op) {	
			case LISTAPPEND:				
				srcType = Type.List(Type.Union(lel.element(),rel.element()),false);
				break;
			default:
				syntaxError("invalid list operation: " + expr.op,context,expr);	
				return null; // dead-code
			}										
		} else if(lhs_set && rhs_set) {	
			checkIsSubtype(Type.T_SET_ANY,lhs,context);
			checkIsSubtype(Type.T_SET_ANY,rhs,context);						
			
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
					syntaxError("invalid set operation: " + expr.op,context,expr);	
					return null; // deadcode
			}							
		} else {			
			switch(expr.op) {
			case IS:
			case AND:
			case OR:
			case XOR:
				return resolve(expr,true,environment,context).first();				
			case BITWISEAND:
			case BITWISEOR:
			case BITWISEXOR:
				checkIsSubtype(Type.T_BYTE,lhs,context);
				checkIsSubtype(Type.T_BYTE,rhs,context);
				srcType = Type.T_BYTE;
				break;
			case LEFTSHIFT:
			case RIGHTSHIFT:
				checkIsSubtype(Type.T_BYTE,lhs,context);
				checkIsSubtype(Type.T_INT,rhs,context);
				srcType = Type.T_BYTE;
				break;
			case RANGE:
				checkIsSubtype(Type.T_INT,lhs,context);
				checkIsSubtype(Type.T_INT,rhs,context);
				srcType = Type.List(Type.T_INT, false);
				break;
			case REM:
				checkIsSubtype(Type.T_INT,lhs,context);
				checkIsSubtype(Type.T_INT,rhs,context);
				srcType = Type.T_INT;
				break;			
			default:
				// all other operations go through here
				if(Type.isImplicitCoerciveSubtype(lhsRawType,rhsRawType)) {
					checkIsSubtype(Type.T_REAL,lhs,context);
					if(Type.isSubtype(Type.T_CHAR, lhsRawType)) {
						srcType = Type.T_INT;
					} else if(Type.isSubtype(Type.T_INT, lhsRawType)) {
						srcType = Type.T_INT;
					} else {
						srcType = Type.T_REAL;
					}				
				} else {
					checkIsSubtype(Type.T_REAL,lhs,context);
					checkIsSubtype(Type.T_REAL,rhs,context);				
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
	
	private Expr resolve(Expr.UnOp expr,
			Environment environment, Context context) throws Exception {
		
		if(expr.op == Expr.UOp.NOT) {
			// hand off to special method for conditions
			return resolve(expr,true,environment,context).first();	
		}
		
		Expr src = resolve(expr.mhs, environment,context);
		expr.mhs = src;
		
		switch(expr.op) {
		case NEG:
			checkIsSubtype(Type.T_REAL,src,context);			
			break;
		case INVERT:
			checkIsSubtype(Type.T_BYTE,src,context);
			break;
				
		default:		
			internalFailure(
					"unknown operator: " + expr.op.getClass().getName(),
					context, expr);
		}
		
		expr.type = src.result();		
		
		return expr;
	}
	
	private Expr resolve(Expr.Comprehension expr,
			Environment environment, Context context) throws Exception {
		
		ArrayList<Pair<String,Expr>> sources = expr.sources;
		Environment local = environment.clone();
		for(int i=0;i!=sources.size();++i) {
			Pair<String,Expr> p = sources.get(i);
			Expr e = resolve(p.second(),local,context);			
			p = new Pair<String,Expr>(p.first(),e);
			sources.set(i,p);
			Nominal element;
			Nominal type = e.result();
			Nominal.EffectiveCollection colType = expandAsEffectiveCollection(type);			
			if(colType == null) {
				syntaxError(errorMessage(INVALID_SET_OR_LIST_EXPRESSION),context,e);
				return null; // dead code
			}
			// update environment for subsequent source expressions, the
			// condition and the value.
			local = local.put(p.first(),colType.element());
		}
		
		if(expr.condition != null) {
			expr.condition = resolve(expr.condition,local,context);
		}
		
		if (expr.cop == Expr.COp.SETCOMP || expr.cop == Expr.COp.LISTCOMP) {						
			expr.value = resolve(expr.value,local,context);
			expr.type = Nominal.Set(expr.value.result(), false);
		} else {
			expr.type = Nominal.T_BOOL;
		}
		
		local.free();				
		
		return expr;
	}
	
	private Expr resolve(Expr.Constant expr,
			Environment environment, Context context) {
		return expr;
	}

	private Expr resolve(Expr.Cast c,
			Environment environment, Context context) throws Exception {
		c.expr = resolve(c.expr,environment,context);		
		c.type = resolveAsType(c.unresolvedType, context);
		Type from = c.expr.result().raw();		
		Type to = c.type.raw();
		if (!Type.isExplicitCoerciveSubtype(to, from)) {			
			syntaxError(errorMessage(SUBTYPE_ERROR, to, from), context, c);
		}	
		return c;
	}
			
	private Expr resolve(Expr.AbstractFunctionOrMethod expr,
			Environment environment, Context context) throws Exception {
		
		if(expr instanceof Expr.FunctionOrMethod) {
			return expr;
		} 		
		
		Pair<NameID, Nominal.FunctionOrMethod> p;
		
		if (expr.paramTypes != null) {
			ArrayList<Nominal> paramTypes = new ArrayList<Nominal>();
			for (SyntacticType t : expr.paramTypes) {
				paramTypes.add(resolveAsType(t, context));
			}
			// FIXME: clearly a bug here in the case of message reference
			p = (Pair) resolveAsFunctionOrMethod(expr.name, paramTypes, context);			
		} else {
			p = resolveAsFunctionOrMethod(expr.name, context);			
		}
		
		expr = new Expr.FunctionOrMethod(p.first(),expr.paramTypes,expr.attributes());
		expr.type = p.second();
		return expr;
	}
	
	private Expr resolve(Expr.Lambda expr,
			Environment environment, Context context) throws Exception {
		
		ArrayList<Type> rawTypes = new ArrayList<Type>();
		ArrayList<Type> nomTypes = new ArrayList<Type>();
		
		for(WhileyFile.Parameter p : expr.parameters) {
			Nominal n = resolveAsType(p.type,context);
			rawTypes.add(n.raw());
			nomTypes.add(n.nominal());
			// Now, update the environment to include those declared variables
			String var = p.name();
			if (environment.containsKey(var)) {
				syntaxError(errorMessage(VARIABLE_ALREADY_DEFINED,var),
						context, p);
			}			
			environment = environment.put(var, n);
		}
				
		expr.body = resolve(expr.body,environment,context);

		Type.FunctionOrMethod rawType;
		Type.FunctionOrMethod nomType;

		if(Exprs.isPure(expr.body, context)) { 		
			rawType = Type.Function(expr.body.result().raw(),
					Type.T_VOID, rawTypes);
			nomType = Type.Function(expr.body.result().nominal(),
					Type.T_VOID, nomTypes);
		} else {			
			rawType = Type.Method(expr.body.result().raw(),
					Type.T_VOID, rawTypes);
			nomType = Type.Method(expr.body.result().nominal(),
					Type.T_VOID, nomTypes);
		}

		expr.type = (Nominal.FunctionOrMethod) Nominal.construct(nomType,rawType);
		return expr;
	}
	
	private Expr resolve(Expr.AbstractIndirectInvoke expr,
			Environment environment, Context context) throws Exception {

		expr.src = resolve(expr.src, environment, context);
		Nominal type = expr.src.result();
		if (!(type instanceof Nominal.FunctionOrMethod)) {
			syntaxError("function or method type expected", context, expr.src);
		}

		Nominal.FunctionOrMethod funType = (Nominal.FunctionOrMethod) type;

		List<Nominal> paramTypes = funType.params();
		ArrayList<Expr> exprArgs = expr.arguments;
		for (int i = 0; i != exprArgs.size(); ++i) {
			Nominal pt = paramTypes.get(i);
			Expr arg = resolve(exprArgs.get(i), environment, context);			
			checkIsSubtype(pt, arg, context);
			exprArgs.set(i, arg);

		}

		if (funType instanceof Nominal.Function) {
			Expr.IndirectFunctionCall ifc = new Expr.IndirectFunctionCall(expr.src, exprArgs,
					expr.attributes());
			ifc.functionType = (Nominal.Function) funType;
			return ifc;
		} else {
			Expr.IndirectMethodCall imc = new Expr.IndirectMethodCall(expr.src, exprArgs,
					expr.attributes());
			imc.methodType = (Nominal.Method) funType;
			return imc;
		}

	}
	
	private Expr resolve(Expr.AbstractInvoke expr,
			Environment environment, Context context) throws Exception {
		
		// first, resolve through receiver and parameters.
		
		Expr receiver = expr.qualification;
		
		if(receiver != null) {
			receiver = resolve(receiver,environment,context);
			expr.qualification = receiver;						
		}
		
		ArrayList<Expr> exprArgs = expr.arguments;
		ArrayList<Nominal> paramTypes = new ArrayList<Nominal>();
		for(int i=0;i!=exprArgs.size();++i) {
			Expr arg = resolve(exprArgs.get(i),environment,context);
			exprArgs.set(i, arg);
			paramTypes.add(arg.result());			
		}
		
		// second, determine whether we already have a fully qualified name and
		// then lookup the appropriate function.
		
		if(receiver instanceof Expr.ModuleAccess) {
			// Yes, this function or method is qualified
			Expr.ModuleAccess ma = (Expr.ModuleAccess) receiver;
			NameID name = new NameID(ma.mid,expr.name);
			Nominal.FunctionOrMethod funType = resolveAsFunctionOrMethod(name,  paramTypes, context);			
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
			
			Nominal.EffectiveRecord recType = expandAsEffectiveRecord(expr.qualification.result());
			
			if(recType != null) {
				
				Nominal fieldType = recType.field(expr.name);
				
				if(fieldType == null) {
					syntaxError(errorMessage(RECORD_MISSING_FIELD,expr.name),context,expr);
				} else if(!(fieldType instanceof Nominal.FunctionOrMethod)) {
					syntaxError("function or method type expected",context,expr);
				}
				
				Nominal.FunctionOrMethod funType = (Nominal.FunctionOrMethod) fieldType;
				Expr.FieldAccess ra = new Expr.FieldAccess(receiver, expr.name, expr.attributes());
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
				checkIsSubtype(Type.T_REF_ANY,expr.qualification,context);
				Type.Reference procType = (Type.Reference) expr.qualification.result().raw(); 						
				
				exprArgs.add(0,receiver);
				paramTypes.add(0,receiver.result());
				
				Pair<NameID, Nominal.FunctionOrMethod> p = resolveAsFunctionOrMethod(
						expr.name, paramTypes, context);
				
				// TODO: problem if not Nominal.Method!
				
				Expr.MethodCall r = new Expr.MethodCall(p.first(), null,
						exprArgs, expr.attributes());
				r.methodType = (Nominal.Method) p.second();
				return r;
			}
		} else {

			// no, function is not qualified ... so, it's either a local
			// variable or a function call the location of which we need to
			// identify.

			Nominal type = environment.get(expr.name);			
			Nominal.FunctionOrMethod funType = type != null ? expandAsFunctionOrMethod(type) : null;
			
			// FIXME: bad idea to use instanceof Nominal.FunctionOrMethod here
			if(funType != null) {
				// ok, matching local variable of function type.				
				List<Nominal> funTypeParams = funType.params();
				if(paramTypes.size() != funTypeParams.size()) {
					syntaxError("insufficient arguments to function call",context,expr);
				}
				for (int i = 0; i != funTypeParams.size(); ++i) {
					Nominal fpt = funTypeParams.get(i);
					checkIsSubtype(fpt, paramTypes.get(i), exprArgs.get(i),context);
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
				Pair<NameID, Nominal.FunctionOrMethod> p = resolveAsFunctionOrMethod(expr.name, paramTypes, context);
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
	
	private Expr resolve(Expr.IndexOf expr,
			Environment environment, Context context) throws Exception {			
		expr.src = resolve(expr.src,environment,context);
		expr.index = resolve(expr.index,environment,context);		
		Nominal.EffectiveIndexible srcType = expandAsEffectiveMap(expr.src.result());
		
		if(srcType == null) {
			syntaxError(errorMessage(INVALID_SET_OR_LIST_EXPRESSION), context, expr.src);
		} else {
			expr.srcType = srcType;
		}
		
		checkIsSubtype(srcType.key(),expr.index,context);
				
		return expr;
	}
	
	private Expr resolve(Expr.LengthOf expr, Environment environment,
			Context context) throws Exception {			
		expr.src = resolve(expr.src,environment, context);			
		Nominal srcType = expr.src.result();
		Type rawSrcType = srcType.raw();				
	
		// First, check whether this is still only an abstract access and, in
		// such case, upgrade it to the appropriate access expression.

		if (rawSrcType instanceof Type.EffectiveCollection) {
			expr.srcType = expandAsEffectiveCollection(srcType);
			return expr;
		} else {
			syntaxError("found " + expr.src.result().nominal()
					+ ", expected string, set, list or dictionary.", context,
					expr.src);
		}

		// Second, determine the expanded src type for this access expression
		// and check the key value.
		
		checkIsSubtype(Type.T_STRING,expr.src,context);								
		
		return expr;
	}
	
	private Expr resolve(Expr.AbstractVariable expr,
			Environment environment, Context context) throws Exception {

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
				NameID nid = resolveAsName(expr.var, context);					
				Expr.ConstantAccess ca = new Expr.ConstantAccess(null, expr.var, nid,
						expr.attributes());
				ca.value = resolveAsConstant(nid);
				return ca;
			} catch (ResolveError err) {
			}
			// In this case, we may still be OK if this corresponds to an
			// explicit module or package access.
			try {
				Path.ID mid = resolveAsModule(expr.var, context);
				return new Expr.ModuleAccess(null, expr.var, mid,
						expr.attributes());
			} catch (ResolveError err) {
			}
			Path.ID pid = Trie.ROOT.append(expr.var);
			if (builder.exists(pid)) {
				return new Expr.PackageAccess(null, expr.var, pid,
						expr.attributes());
			}
			// ok, failed.
			syntaxError(errorMessage(UNKNOWN_VARIABLE), context, expr);
			return null; // deadcode
		}
	}
	
	private Expr resolve(Expr.Set expr,
			Environment environment, Context context) {
		Nominal element = Nominal.T_VOID;		
		
		ArrayList<Expr> exprs = expr.arguments;
		for(int i=0;i!=exprs.size();++i) {
			Expr e = resolve(exprs.get(i),environment,context);
			Nominal t = e.result();
			exprs.set(i,e);
			element = Nominal.Union(t,element);			
		}
		
		expr.type = Nominal.Set(element,false);
		
		return expr;
	}
	
	private Expr resolve(Expr.List expr,
			Environment environment, Context context) {		
		Nominal element = Nominal.T_VOID;		
		
		ArrayList<Expr> exprs = expr.arguments;
		for(int i=0;i!=exprs.size();++i) {
			Expr e = resolve(exprs.get(i),environment,context);
			Nominal t = e.result();
			exprs.set(i,e);
			element = Nominal.Union(t,element);			
		}	
		
		expr.type = Nominal.List(element,false);
				
		return expr;
	}
	
	private Expr resolve(Expr.Map expr,
			Environment environment, Context context) {
		Nominal keyType = Nominal.T_VOID;
		Nominal valueType = Nominal.T_VOID;		
				
		ArrayList<Pair<Expr,Expr>> exprs = expr.pairs;
		for(int i=0;i!=exprs.size();++i) {
			Pair<Expr,Expr> p = exprs.get(i);
			Expr key = resolve(p.first(),environment,context);
			Expr value = resolve(p.second(),environment,context);
			Nominal kt = key.result();
			Nominal vt = value.result();
			exprs.set(i,new Pair<Expr,Expr>(key,value));
			
			keyType = Nominal.Union(kt,keyType);			
			valueType = Nominal.Union(vt,valueType);
		}
		
		expr.type = Nominal.Map(keyType,valueType);
		
		return expr;
	}
	
	private Expr resolve(Expr.Record expr,
			Environment environment, Context context) {
		
		HashMap<String,Expr> exprFields = expr.fields;
		HashMap<String,Nominal> fieldTypes = new HashMap<String,Nominal>();
				
		ArrayList<String> fields = new ArrayList<String>(exprFields.keySet());
		for(String field : fields) {
			Expr e = resolve(exprFields.get(field),environment,context);
			Nominal t = e.result();
			exprFields.put(field,e);
			fieldTypes.put(field,t);				
		}		
		
		expr.type = Nominal.Record(false,fieldTypes);
		
		return expr;
	}
	
	private Expr resolve(Expr.Tuple expr,
			Environment environment, Context context) {
		ArrayList<Expr> exprFields = expr.fields;
		ArrayList<Nominal> fieldTypes = new ArrayList<Nominal>();
				
		for(int i=0;i!=exprFields.size();++i) {
			Expr e = resolve(exprFields.get(i),environment,context);
			Nominal t = e.result();
			exprFields.set(i,e);
			fieldTypes.add(t);			
		}
				
		expr.type = Nominal.Tuple(fieldTypes);
		
		return expr;
	}
	
	private Expr resolve(Expr.SubList expr,
			Environment environment, Context context) throws Exception {	
		
		expr.src = resolve(expr.src,environment,context);
		expr.start = resolve(expr.start,environment,context);
		expr.end = resolve(expr.end,environment,context);
		
		checkIsSubtype(Type.T_LIST_ANY,expr.src,context);
		checkIsSubtype(Type.T_INT,expr.start,context);
		checkIsSubtype(Type.T_INT,expr.end,context);
		
		expr.type = expandAsEffectiveList(expr.src.result());
		if(expr.type == null) {
			// must be a substring
			return new Expr.SubString(expr.src,expr.start,expr.end,expr.attributes());
		}
		
		return expr;
	}
	
	private Expr resolve(Expr.SubString expr,
			Environment environment, Context context) throws Exception {	
		
		expr.src = resolve(expr.src,environment,context);
		expr.start = resolve(expr.start,environment,context);
		expr.end = resolve(expr.end,environment,context);
		
		checkIsSubtype(Type.T_STRING,expr.src,context);
		checkIsSubtype(Type.T_INT,expr.start,context);
		checkIsSubtype(Type.T_INT,expr.end,context);
		
		return expr;
	}
	
	private Expr resolve(Expr.AbstractDotAccess expr,
			Environment environment, Context context) throws Exception {	
				
		if (expr instanceof Expr.PackageAccess
				|| expr instanceof Expr.ModuleAccess) {			
			// don't need to do anything in these cases.
			return expr;
		}
		
		Expr src = expr.src;
		
		if(src != null) {
			src = resolve(expr.src,environment,context);
			expr.src = src;
		}
				
		if(expr instanceof Expr.FieldAccess) {			
			return resolve((Expr.FieldAccess)expr,environment,context);
		} else if(expr instanceof Expr.ConstantAccess) {
			return resolve((Expr.ConstantAccess)expr,environment,context);
		} else if(src instanceof Expr.PackageAccess) {
			// either a package access, module access or constant access
			// This variable access may correspond to an external access.			
			Expr.PackageAccess pa = (Expr.PackageAccess) src; 
			Path.ID pid = pa.pid.append(expr.name);
			if (builder.exists(pid)) {
				return new Expr.PackageAccess(pa, expr.name, pid,
						expr.attributes());
			}			
			Path.ID mid = pa.pid.append(expr.name);
			if (builder.exists(mid)) {
				return new Expr.ModuleAccess(pa, expr.name, mid,
						expr.attributes());
			} else {
				syntaxError(errorMessage(INVALID_PACKAGE_ACCESS), context, expr);
				return null; // deadcode
			}		
		} else if(src instanceof Expr.ModuleAccess) {
			// must be a constant access
			Expr.ModuleAccess ma = (Expr.ModuleAccess) src; 													
			NameID nid = new NameID(ma.mid,expr.name);
			if (builder.isName(nid)) {
				Expr.ConstantAccess ca = new Expr.ConstantAccess(ma,
						expr.name, nid, expr.attributes());
				ca.value = resolveAsConstant(nid);
				return ca;
			}						
			syntaxError(errorMessage(INVALID_MODULE_ACCESS),context,expr);			
			return null; // deadcode
		} else {
			// must be a RecordAccess
			Expr.FieldAccess ra = new Expr.FieldAccess(src,expr.name,expr.attributes());			
			return resolve(ra,environment,context);
		}
	}
		
	private Expr resolve(Expr.FieldAccess ra,
			Environment environment, Context context) throws Exception {
		ra.src = resolve(ra.src,environment,context);
		Nominal srcType = ra.src.result();
		Nominal.EffectiveRecord recType = expandAsEffectiveRecord(srcType);
		if(recType == null) {
			syntaxError(errorMessage(RECORD_TYPE_REQUIRED,srcType.raw()),context,ra);
		} 
		Nominal fieldType = recType.field(ra.name);
		if(fieldType == null) {
			syntaxError(errorMessage(RECORD_MISSING_FIELD,ra.name),context,ra);
		}
		ra.srcType = recType;		
		return ra;
	}	
	
	private Expr resolve(Expr.ConstantAccess expr,
			Environment environment, Context context) throws Exception {
		// we don't need to do anything here, since the value is already
		// resolved by case for AbstractDotAccess.
		return expr;
	}			

	private Expr resolve(Expr.Dereference expr,
			Environment environment, Context context) throws Exception {
		Expr src = resolve(expr.src,environment,context);
		expr.src = src;
		Nominal.Reference srcType = expandAsReference(src.result());
		if(srcType == null) {
			syntaxError("invalid reference expression",context,src);
		}
		expr.srcType = srcType;		
		return expr;
	}
	
	private Expr resolve(Expr.New expr,
			Environment environment, Context context) {
		expr.expr = resolve(expr.expr,environment,context);
		expr.type = Nominal.Reference(expr.expr.result());
		return expr;
	}
	
	private Expr resolve(Expr.TypeVal expr,
			Environment environment, Context context) throws Exception {
		expr.type = resolveAsType(expr.unresolvedType, context); 
		return expr;
	}
	
	/**
	 * Responsible for determining the true type of a method or function being
	 * invoked. To do this, it must find the function/method with the most
	 * precise type that matches the argument types.
	 * 
	 * @param nid
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	private Nominal.FunctionOrMethod resolveAsFunctionOrMethod(NameID nid, 
			List<Nominal> parameters, Context context) throws Exception {
		HashSet<Pair<NameID, Nominal.FunctionOrMethod>> candidates = new HashSet<Pair<NameID, Nominal.FunctionOrMethod>>();

		addCandidateFunctionsAndMethods(nid, parameters, candidates, context);

		return selectCandidateFunctionOrMethod(nid.name(), parameters,
				candidates,context).second();		
	}

	/**
	 * Responsible for determining the true type of a method or function being
	 * invoked. In this case, no argument types are given. This means that any
	 * match is returned. However, if there are multiple matches, then an
	 * ambiguity error is reported.
	 * 
	 * @param name
	 *            --- function or method name whose type to determine.
	 * @param context
	 *            --- context in which to resolve this name.
	 * @return
	 * @throws Exception
	 */
	public Pair<NameID,Nominal.FunctionOrMethod> resolveAsFunctionOrMethod(String name, 
			Context context) throws Exception {
		return resolveAsFunctionOrMethod(name,null,context);
	}
	
	/**
	 * Responsible for determining the true type of a method or function being
	 * invoked. To do this, it must find the function/method with the most
	 * precise type that matches the argument types.
	 * 
	 * @param name
	 *            --- name of function or method whose type to determine.
	 * @param parameters
	 *            --- required parameter types for the function or method.
	 * @param context
	 *            --- context in which to resolve this name.
	 * @return
	 * @throws Exception
	 */
	public Pair<NameID,Nominal.FunctionOrMethod> resolveAsFunctionOrMethod(String name, 
			List<Nominal> parameters, Context context) throws Exception {

		HashSet<Pair<NameID,Nominal.FunctionOrMethod>> candidates = new HashSet<Pair<NameID, Nominal.FunctionOrMethod>>(); 		
		// first, try to find the matching message
		for (WhileyFile.Import imp : context.imports()) {
			String impName = imp.name;
			if (impName == null || impName.equals(name) || impName.equals("*")) {
				Trie filter = imp.filter;
				if(impName == null) {
					// import name is null, but it's possible that a module of
					// the given name exists, in which case any matching names
					// are automatically imported. 
					filter = filter.parent().append(name);
				}
				for (Path.ID mid : builder.imports(filter)) {					
					NameID nid = new NameID(mid,name);				
					addCandidateFunctionsAndMethods(nid,parameters,candidates,context);					
				}
			} 
		}

		return selectCandidateFunctionOrMethod(name,parameters,candidates,context);
	}
	
	private boolean paramSubtypes(Type.FunctionOrMethod f1, Type.FunctionOrMethod f2) {		
		List<Type> f1_params = f1.params();
		List<Type> f2_params = f2.params();
		if(f1_params.size() == f2_params.size()) {			
			for(int i=0;i!=f1_params.size();++i) {
				Type f1_param = f1_params.get(i);
				Type f2_param = f2_params.get(i);				
				if(!Type.isImplicitCoerciveSubtype(f1_param,f2_param)) {				
					return false;
				}
			}			

			return true;
		}
		return false;
	}

	private boolean paramStrictSubtypes(Type.FunctionOrMethod f1, Type.FunctionOrMethod f2) {		
		List<Type> f1_params = f1.params();
		List<Type> f2_params = f2.params();
		if(f1_params.size() == f2_params.size()) {
			boolean allEqual = true;
			for(int i=0;i!=f1_params.size();++i) {
				Type f1_param = f1_params.get(i);
				Type f2_param = f2_params.get(i);				
				if(!Type.isImplicitCoerciveSubtype(f1_param,f2_param)) {				
					return false;
				}
				allEqual &= f1_param.equals(f2_param);
			}			

			// This function returns true if the parameters are a strict
			// subtype. Therefore, if they are all equal it must return false.

			return !allEqual;
		}
		return false;
	}

	private String parameterString(List<Nominal> paramTypes) {
		String paramStr = "(";
		boolean firstTime = true;
		if(paramTypes == null) {
			paramStr += "...";
		} else {
			for(Nominal t : paramTypes) {
				if(!firstTime) {
					paramStr += ",";
				}
				firstTime=false;
				paramStr += t.nominal();
			}
		}
		return paramStr + ")";		
	}

	private Pair<NameID, Nominal.FunctionOrMethod> selectCandidateFunctionOrMethod(
			String name, List<Nominal> parameters,
			Collection<Pair<NameID, Nominal.FunctionOrMethod>> candidates,
			Context context) throws Exception {

		List<Type> rawParameters; 
		Type.Function target;

		if (parameters != null) {
			rawParameters = stripNominal(parameters);
			target = (Type.Function) Type.Function(Type.T_ANY, Type.T_ANY,
					rawParameters);
		} else {
			rawParameters = null;
			target = null;
		}

		NameID candidateID = null;
		Nominal.FunctionOrMethod candidateType = null;					
		for (Pair<NameID,Nominal.FunctionOrMethod> p : candidates) {			
			Nominal.FunctionOrMethod nft = p.second();
			Type.FunctionOrMethod ft = nft.raw();			
			if (parameters == null || paramSubtypes(ft, target)) {
				// this is now a genuine candidate
				if(candidateType == null || paramStrictSubtypes(candidateType.raw(), ft)) {
					candidateType = nft;
					candidateID = p.first();
				} else if(!paramStrictSubtypes(ft, candidateType.raw())){ 
					// this is an ambiguous error
					String msg = name + parameterString(parameters) + " is ambiguous";
					// FIXME: should report all ambiguous matches here
					msg += "\n\tfound: " + candidateID + " : " + candidateType.nominal();
					msg += "\n\tfound: " + p.first() + " : " + p.second().nominal();
					throw new ResolveError(msg);
				}
			}			
		}				

		if(candidateType == null) {
			// second, didn't find matching message so generate error message
			String msg = "no match for " + name + parameterString(parameters);			

			for (Pair<NameID, Nominal.FunctionOrMethod> p : candidates) {
				msg += "\n\tfound: " + p.first() + " : " + p.second().nominal();
			}

			throw new ResolveError(msg);
		} else {
			// now check protection modified
			WhileyFile wf = builder.getSourceFile(candidateID.module());
			if(wf != null) {
				if(wf != context.file()) {
					for (WhileyFile.FunctionOrMethod d : wf.declarations(
							WhileyFile.FunctionOrMethod.class, candidateID.name())) {
						if(d.parameters.equals(candidateType.params())) {
							if(!d.isPublic() && !d.isProtected()) {
								String msg = candidateID.module() + "." + name + parameterString(parameters) + " is not visible";
								throw new ResolveError(msg);
							}
						}
					}
				}
			} else {				
				WyilFile m = builder.getModule(candidateID.module());
				WyilFile.MethodDeclaration d = m.method(candidateID.name(),candidateType.raw());
				if(!d.isPublic() && !d.isProtected()) {
					String msg = candidateID.module() + "." + name + parameterString(parameters) + " is not visible";
					throw new ResolveError(msg);
				}
			}
		}

		return new Pair<NameID,Nominal.FunctionOrMethod>(candidateID,candidateType);
	}

	private void addCandidateFunctionsAndMethods(NameID nid,
			List<?> parameters,
			Collection<Pair<NameID, Nominal.FunctionOrMethod>> candidates, Context context)
					throws Exception {
		Path.ID mid = nid.module();

		int nparams = parameters != null ? parameters.size() : -1;				

		WhileyFile wf = builder.getSourceFile(mid);
		if (wf != null) {
			for (WhileyFile.FunctionOrMethod f : wf.declarations(
					WhileyFile.FunctionOrMethod.class, nid.name())) {
				if (nparams == -1 || f.parameters.size() == nparams) {
					Nominal.FunctionOrMethod ft = (Nominal.FunctionOrMethod) resolveAsType(
							f.unresolvedType(), f);
					candidates.add(new Pair<NameID, Nominal.FunctionOrMethod>(
							nid, ft));
				}
			}
		} else {
			try {
				WyilFile m = builder.getModule(mid);
				for (WyilFile.MethodDeclaration mm : m.methods()) {
					if ((mm.isFunction() || mm.isMethod())
							&& mm.name().equals(nid.name())
							&& (nparams == -1 || mm.type().params().size() == nparams)) {
						// FIXME: loss of nominal information
						Type.FunctionOrMethod t = (Type.FunctionOrMethod) mm
								.type();
						Nominal.FunctionOrMethod fom;
						if (t instanceof Type.Function) {
							Type.Function ft = (Type.Function) t;
							fom = new Nominal.Function(ft, ft);
						} else {
							Type.Method mt = (Type.Method) t;
							fom = new Nominal.Method(mt, mt);
						}
						candidates
								.add(new Pair<NameID, Nominal.FunctionOrMethod>(
										nid, fom));
					}
				}
			} catch (ResolveError e) {

			}
		}
	}

	private static List<Type> stripNominal(List<Nominal> types) {
		ArrayList<Type> r = new ArrayList<Type>();
		for (Nominal t : types) {
			r.add(t.raw());
		}
		return r;
	}

	// =========================================================================
		// ResolveAsName
		// =========================================================================		
		
		/**
		 * This methods attempts to resolve the correct package for a named item in
		 * a given context. Resolving the correct package may require loading
		 * modules as necessary from the WHILEYPATH and/or compiling modules for
		 * which only source code is currently available.
		 * 
		 * @param name
		 *            A module name without package specifier.
		 * @param context
		 *            --- context in which to resolve.
		 * @return The resolved name.
		 * @throws Exception
		 *             if it couldn't resolve the name
		 */
		public NameID resolveAsName(String name, Context context)
				throws Exception {	
			for (WhileyFile.Import imp : context.imports()) {
				String impName = imp.name;
				if (impName == null || impName.equals(name) || impName.equals("*")) {
					Trie filter = imp.filter;
					if (impName == null) {
						// import name is null, but it's possible that a module of
						// the given name exists, in which case any matching names
						// are automatically imported.
						filter = filter.parent().append(name);
					}
					for (Path.ID mid : builder.imports(filter)) {					
						NameID nid = new NameID(mid, name);
						if (builder.isName(nid)) {
							// ok, we have found the name in question. But, is it
							// visible?
							if(isVisible(nid,context)) {
								return nid;
							} else {
								throw new ResolveError(nid + " is not visible");	
							}
						}
					}
				}
			}

			throw new ResolveError("name not found: " + name);
		}

		/**
		 * This methods attempts to resolve the given list of names into a single
		 * named item (e.g. type, method, constant, etc). For example,
		 * <code>["whiley","lang","Math","max"]</code> would be resolved, since
		 * <code>whiley.lang.Math.max</code> is a valid function name. In contrast,
		 * <code>["whiley","lang","Math"]</code> does not resolve since
		 * <code>whiley.lang.Math</code> refers to a module.
		 * 
		 * @param names
		 *            A list of components making up the name, which may include the
		 *            package and enclosing module.
		 * @param context
		 *            --- context in which to resolve *
		 * @return The resolved name.
		 * @throws Exception
		 *             if it couldn't resolve the name
		 */
		public NameID resolveAsName(List<String> names, Context context) throws Exception {		
			if(names.size() == 1) {
				return resolveAsName(names.get(0),context);
			} else if(names.size() == 2) {
				String name = names.get(1);
				Path.ID mid = resolveAsModule(names.get(0),context);		
				NameID nid = new NameID(mid, name); 
				if (builder.isName(nid)) {
					if(isVisible(nid,context)) {
						return nid;
					} else {
						throw new ResolveError(nid + " is not visible");	
					}
				} 
			} else {
				String name = names.get(names.size()-1);
				String module = names.get(names.size()-2);
				Path.ID pkg = Trie.ROOT;
				for(int i=0;i!=names.size()-2;++i) {
					pkg = pkg.append(names.get(i));
				}
				Path.ID mid = pkg.append(module);
				NameID nid = new NameID(mid, name); 
				if (builder.isName(nid)) {
					if(isVisible(nid,context)) {
						return nid;
					} else {
						throw new ResolveError(nid + " is not visible");	
					}
				} 			
			}
			
			String name = null;
			for(String n : names) {
				if(name != null) {
					name = name + "." + n;
				} else {
					name = n;
				}			
			}
			throw new ResolveError("name not found: " + name);
		}	
		
		/**
		 * This method attempts to resolve a name as a module in a given name
		 * context.
		 * 
		 * @param name
		 *            --- name to be resolved
		 * @param context
		 *            --- context in which to resolve
		 * @return
		 * @throws Exception
		 */
		public Path.ID resolveAsModule(String name, Context context)
				throws Exception {
			
			for (WhileyFile.Import imp : context.imports()) {
				Trie filter = imp.filter;
				String last = filter.last();			
				if (last.equals("*")) {
					// this is generic import, so narrow the filter.
					filter = filter.parent().append(name);
				} else if(!last.equals(name)) {
					continue; // skip as not relevant
				}
				
				for(Path.ID mid : builder.imports(filter)) {								
					return mid;				
				}
			}
					
			throw new ResolveError("module not found: " + name);
		}
		
		// =========================================================================
		// ResolveAsType
		// =========================================================================
		
		public Nominal.Function resolveAsType(SyntacticType.Function t,
				Context context) {
			return (Nominal.Function) resolveAsType((SyntacticType)t,context);
		}
		
		public Nominal.Method resolveAsType(SyntacticType.Method t,
				Context context) {		
			return (Nominal.Method) resolveAsType((SyntacticType)t,context);
		}

		/**
		 * Resolve a type in a given context by identifying all unknown names and
		 * replacing them with nominal types.
		 * 
		 * @param type
		 *            --- type to be resolved.
		 * @param context
		 *            --- context in which to resolve the type.
		 * @return
		 * @throws Exception
		 */
		public Nominal resolveAsType(SyntacticType type, Context context) {
			Type nominalType = resolveAsType(type, context, true, false);
			Type rawType = resolveAsType(type, context, false, false);
			return Nominal.construct(nominalType, rawType);
		}
		
		/**
		 * Resolve a type in a given context by identifying all unknown names and
		 * replacing them with nominal types. In this case, any constrained types
		 * are treated as void. This is critical for properly dealing with type
		 * tests, which may otherwise assume types are unconstrained.
		 * 
		 * @param type
		 *            --- type to be resolved.
		 * @param context
		 *            --- context in which to resolve the type.
		 * @return
		 * @throws Exception
		 */
		public Nominal resolveAsUnconstrainedType(SyntacticType type, Context context) {
			Type nominalType = resolveAsType(type, context, true, true);
			Type rawType = resolveAsType(type, context, false, true);
			return Nominal.construct(nominalType, rawType);
		}
		
		private Type resolveAsType(SyntacticType t, Context context,
				boolean nominal, boolean unconstrained) {
			
			if(t instanceof SyntacticType.Primitive) { 
				if (t instanceof SyntacticType.Any) {
					return Type.T_ANY;
				} else if (t instanceof SyntacticType.Void) {
					return Type.T_VOID;
				} else if (t instanceof SyntacticType.Null) {
					return Type.T_NULL;
				} else if (t instanceof SyntacticType.Bool) {
					return Type.T_BOOL;
				} else if (t instanceof SyntacticType.Byte) {
					return Type.T_BYTE;
				} else if (t instanceof SyntacticType.Char) {
					return Type.T_CHAR;
				} else if (t instanceof SyntacticType.Int) {
					return Type.T_INT;
				} else if (t instanceof SyntacticType.Real) {
					return Type.T_REAL;
				} else if (t instanceof SyntacticType.Strung) {
					return Type.T_STRING;
				} else {
					internalFailure("unrecognised type encountered ("
							+ t.getClass().getName() + ")",context,t);
					return null; // deadcode
				}
			} else {
				ArrayList<Automaton.State> states = new ArrayList<Automaton.State>();
				HashMap<NameID,Integer> roots = new HashMap<NameID,Integer>();
				resolveAsType(t,context,states,roots,nominal,unconstrained);
				return Type.construct(new Automaton(states));
			}
		}
		
		/**
		 * The following method resolves a type in a given context.
		 * 
		 * @param type
		 *            --- type to be resolved
		 * @param context
		 *            --- context in which to resolve the type
		 * @return
		 * @throws Exception
		 */
		private int resolveAsType(SyntacticType type, Context context,
				ArrayList<Automaton.State> states, HashMap<NameID, Integer> roots,
				boolean nominal, boolean unconstrained) {			
			
			if(type instanceof SyntacticType.Primitive) {
				return resolveAsType((SyntacticType.Primitive)type,context,states);
			} 
			
			int myIndex = states.size();
			int myKind;
			int[] myChildren;
			Object myData = null;
			boolean myDeterministic = true;
			
			states.add(null); // reserve space for me
			
			if(type instanceof SyntacticType.List) {
				SyntacticType.List lt = (SyntacticType.List) type;
				myKind = Type.K_LIST;
				myChildren = new int[1];
				myChildren[0] = resolveAsType(lt.element,context,states,roots,nominal,unconstrained);
				myData = false;
			} else if(type instanceof SyntacticType.Set) {
				SyntacticType.Set st = (SyntacticType.Set) type;
				myKind = Type.K_SET;
				myChildren = new int[1];
				myChildren[0] = resolveAsType(st.element,context,states,roots,nominal,unconstrained);
				myData = false;
			} else if(type instanceof SyntacticType.Map) {
				SyntacticType.Map st = (SyntacticType.Map) type;
				myKind = Type.K_MAP;
				myChildren = new int[2];
				myChildren[0] = resolveAsType(st.key,context,states,roots,nominal,unconstrained);
				myChildren[1] = resolveAsType(st.value,context,states,roots,nominal,unconstrained);			
			} else if(type instanceof SyntacticType.Record) {
				SyntacticType.Record tt = (SyntacticType.Record) type;
				HashMap<String,SyntacticType> ttTypes = tt.types;			
				Type.Record.State fields = new Type.Record.State(tt.isOpen,ttTypes.keySet());
				Collections.sort(fields);			
				myKind = Type.K_RECORD;
				myChildren = new int[fields.size()];
				for(int i=0;i!=fields.size();++i) {	
					String field = fields.get(i);
					myChildren[i] = resolveAsType(ttTypes.get(field),context,states,roots,nominal,unconstrained);
				}						
				myData = fields;
			} else if(type instanceof SyntacticType.Tuple) {
				SyntacticType.Tuple tt = (SyntacticType.Tuple) type;
				ArrayList<SyntacticType> ttTypes = tt.types;
				myKind = Type.K_TUPLE;
				myChildren = new int[ttTypes.size()];
				for(int i=0;i!=ttTypes.size();++i) {
					myChildren[i] = resolveAsType(ttTypes.get(i),context,states,roots,nominal,unconstrained);				
				}			
			} else if(type instanceof SyntacticType.Nominal) {
				// This case corresponds to a user-defined type. This will be
				// defined in some module (possibly ours), and we need to identify
				// what module that is here, and save it for future use.
				SyntacticType.Nominal dt = (SyntacticType.Nominal) type;									
				NameID nid;
				try {
					nid = resolveAsName(dt.names, context);

					if(nominal) {
						myKind = Type.K_NOMINAL;
						myData = nid;
						myChildren = Automaton.NOCHILDREN;
					} else {
						// At this point, we're going to expand the given nominal type.
						// We're going to use resolveAsType(NameID,...) to do this which
						// will load the expanded type onto states at the current point.
						// Therefore, we need to remove the initial null we loaded on.
						states.remove(myIndex); 
						return resolveAsType(nid,states,roots,unconstrained);				
					}	
				} catch(ResolveError e) {
					syntaxError(e.getMessage(),context,dt,e);
					return 0; // dead-code
				} catch(SyntaxError e) {
					throw e;
				} catch(Throwable e) {
					internalFailure(e.getMessage(),context,dt,e);
					return 0; // dead-code
				}
			} else if(type instanceof SyntacticType.Negation) {	
				SyntacticType.Negation ut = (SyntacticType.Negation) type;
				myKind = Type.K_NEGATION;
				myChildren = new int[1];
				myChildren[0] = resolveAsType(ut.element,context,states,roots,nominal,unconstrained);			
			} else if(type instanceof SyntacticType.Union) {
				SyntacticType.Union ut = (SyntacticType.Union) type;
				ArrayList<SyntacticType.NonUnion> utTypes = ut.bounds;
				myKind = Type.K_UNION;
				myChildren = new int[utTypes.size()];
				for(int i=0;i!=utTypes.size();++i) {
					myChildren[i] = resolveAsType(utTypes.get(i),context,states,roots,nominal,unconstrained);				
				}	
				myDeterministic = false;
			} else if(type instanceof SyntacticType.Intersection) {
				internalFailure("intersection types not supported yet",context,type);
				return 0; // dead-code
			} else if(type instanceof SyntacticType.Reference) {	
				SyntacticType.Reference ut = (SyntacticType.Reference) type;
				myKind = Type.K_REFERENCE;
				myChildren = new int[1];
				myChildren[0] = resolveAsType(ut.element,context,states,roots,nominal,unconstrained);		
			} else {			
				SyntacticType.FunctionOrMethod ut = (SyntacticType.FunctionOrMethod) type;			
				ArrayList<SyntacticType> utParamTypes = ut.paramTypes;
				int start = 0;
				
				if(ut instanceof SyntacticType.Method) {
					myKind = Type.K_METHOD;
				} else {
					myKind = Type.K_FUNCTION;
				}
				
				myChildren = new int[start + 2 + utParamTypes.size()];
						
				myChildren[start++] = resolveAsType(ut.ret,context,states,roots,nominal,unconstrained);
				if(ut.throwType == null) {
					// this case indicates the user did not provide a throws clause.
					myChildren[start++] = resolveAsType(new SyntacticType.Void(),context,states,roots,nominal,unconstrained);
				} else {
					myChildren[start++] = resolveAsType(ut.throwType,context,states,roots,nominal,unconstrained);
				}
				for(SyntacticType pt : utParamTypes) {
					myChildren[start++] = resolveAsType(pt,context,states,roots,nominal,unconstrained);				
				}						
			}
			
			states.set(myIndex,new Automaton.State(myKind,myData,myDeterministic,myChildren));
			
			return myIndex;
		}
		
		private int resolveAsType(NameID key, ArrayList<Automaton.State> states,
				HashMap<NameID, Integer> roots, boolean unconstrained) throws Exception {
			
			// First, check the various caches we have
			Integer root = roots.get(key);			
			if (root != null) { return root; } 		
			
			// check whether this type is external or not
			WhileyFile wf = builder.getSourceFile(key.module());
			if (wf == null) {						
				// indicates a non-local key which we can resolve immediately	
				
				// FIXME: need to properly support unconstrained types here
				
				WyilFile mi = builder.getModule(key.module());
				WyilFile.TypeDeclaration td = mi.type(key.name());	
				return append(td.type(),states);			
			} 
			
			WhileyFile.Type td = wf.typeDecl(key.name());
			if(td == null) {
				Type t = resolveAsConstant(key).type();			
				if(t instanceof Type.Set) {
					if(unconstrained) {
						// crikey this is ugly
						int myIndex = states.size();
						int kind = Type.leafKind(Type.T_VOID);			
						Object data = null;
						states.add(new Automaton.State(kind,data,true,Automaton.NOCHILDREN));
						return myIndex;					
					}
					Type.Set ts = (Type.Set) t;
					return append(ts.element(),states);	
				} else {
					throw new ResolveError("type not found: " + key);
				}
			}
			
			// following is needed to terminate any recursion
			roots.put(key, states.size());
			SyntacticType type = td.pattern.toSyntacticType();
			
			// now, expand the given type fully	
			if(unconstrained && td.constraint != null) {
				int myIndex = states.size();
				int kind = Type.leafKind(Type.T_VOID);			
				Object data = null;
				states.add(new Automaton.State(kind,data,true,Automaton.NOCHILDREN));
				return myIndex;
			} else if(type instanceof Type.Leaf) {
				//
				// FIXME: I believe this code is now redundant, and should be
				// removed or updated. The problem is that SyntacticType no longer
				// extends Type.
				//
				int myIndex = states.size();
				int kind = Type.leafKind((Type.Leaf)type);			
				Object data = Type.leafData((Type.Leaf)type);
				states.add(new Automaton.State(kind,data,true,Automaton.NOCHILDREN));
				return myIndex;
			} else {						
				return resolveAsType(type,td,states,roots,false,unconstrained);			
			}
			
			// TODO: performance can be improved here, but actually assigning the
			// constructed type into a cache of previously expanded types cache.
			// This is challenging, in the case that the type may not be complete at
			// this point. In particular, if it contains any back-links above this
			// index there could be an issue.
		}	
		
		private int resolveAsType(SyntacticType.Primitive t,
				Context context, ArrayList<Automaton.State> states) {
			int myIndex = states.size();
			int kind;
			if (t instanceof SyntacticType.Any) {
				kind = Type.K_ANY;
			} else if (t instanceof SyntacticType.Void) {
				kind = Type.K_VOID;
			} else if (t instanceof SyntacticType.Null) {
				kind = Type.K_NULL;
			} else if (t instanceof SyntacticType.Bool) {
				kind = Type.K_BOOL;
			} else if (t instanceof SyntacticType.Byte) {
				kind = Type.K_BYTE;
			} else if (t instanceof SyntacticType.Char) {
				kind = Type.K_CHAR;
			} else if (t instanceof SyntacticType.Int) {
				kind = Type.K_INT;
			} else if (t instanceof SyntacticType.Real) {
				kind = Type.K_RATIONAL;
			} else if (t instanceof SyntacticType.Strung) {
				kind = Type.K_STRING;
			} else {		
				internalFailure("unrecognised type encountered ("
						+ t.getClass().getName() + ")",context,t);
				return 0; // dead-code
			}
			states.add(new Automaton.State(kind, null, true,
					Automaton.NOCHILDREN));
			return myIndex;
		}
		
		private static int append(Type type, ArrayList<Automaton.State> states) {
			int myIndex = states.size();
			Automaton automaton = Type.destruct(type);
			Automaton.State[] tStates = automaton.states;
			int[] rmap = new int[tStates.length];
			for (int i = 0, j = myIndex; i != rmap.length; ++i, ++j) {
				rmap[i] = j;
			}
			for (Automaton.State state : tStates) {
				states.add(Automata.remap(state, rmap));
			}
			return myIndex;
		}
		
		// =========================================================================
		// ResolveAsConstant
		// =========================================================================
		
		public Constant resolveAsConstant(NameID nid) throws Exception {				
			return resolveAsConstant(nid,new HashSet<NameID>());		
		}

		public Constant resolveAsConstant(Expr e, Context context) {		
			e = resolve(e, new Environment(),context);
			return resolveAsConstant(e,context,new HashSet<NameID>());		
		}

		/**
		 * Responsible for turning a named constant expression into a value. This is
		 * done by traversing the constant's expression and recursively expanding
		 * any named constants it contains. Simplification of constants is also
		 * performed where possible.
		 * 
		 * @param key
		 *            --- name of constant we are expanding.
		 * @param exprs
		 *            --- mapping of all names to their( declared) expressions
		 * @param visited
		 *            --- set of all constants seen during this traversal (used to
		 *            detect cycles).
		 * @return
		 * @throws Exception
		 */
		private Constant resolveAsConstant(NameID key, HashSet<NameID> visited) throws Exception {				
			Constant result = constantCache.get(key);
			if(result != null) {
				return result;
			} else if(visited.contains(key)) {
				throw new ResolveError("cyclic constant definition encountered (" + key + " -> " + key + ")");
			} else {
				visited.add(key);
			}

			WhileyFile wf = builder.getSourceFile(key.module());

			if (wf != null) {			
				WhileyFile.Declaration decl = wf.declaration(key.name());
				if(decl instanceof WhileyFile.Constant) {
					WhileyFile.Constant cd = (WhileyFile.Constant) decl; 				
					if (cd.resolvedValue == null) {			
						cd.constant = resolve(cd.constant, new Environment(), cd);
						cd.resolvedValue = resolveAsConstant(cd.constant,
								cd, visited);
					}
					result = cd.resolvedValue;
				} else {
					throw new ResolveError("unable to find constant " + key);
				}
			} else {		
				WyilFile module = builder.getModule(key.module());
				WyilFile.ConstantDeclaration cd = module.constant(key.name());
				if(cd != null) {
					result = cd.constant();
				} else {
					throw new ResolveError("unable to find constant " + key);
				}
			}		

			constantCache.put(key, result);

			return result;
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
		 * @param context
		 *            --- context in which to resolve this constant.
		 * @param visited
		 *            --- set of all constants seen during this traversal (used to
		 *            detect cycles).
		 */
		private Constant resolveAsConstant(Expr expr, Context context,
				HashSet<NameID> visited) {
			try {
				if (expr instanceof Expr.Constant) {
					Expr.Constant c = (Expr.Constant) expr;
					return c.value;
				} else if (expr instanceof Expr.ConstantAccess) {
					Expr.ConstantAccess c = (Expr.ConstantAccess) expr;
					return resolveAsConstant(c.nid,visited);				
				} else if (expr instanceof Expr.BinOp) {
					Expr.BinOp bop = (Expr.BinOp) expr;
					Constant lhs = resolveAsConstant(bop.lhs, context, visited);
					Constant rhs = resolveAsConstant(bop.rhs, context, visited);
					return evaluate(bop,lhs,rhs,context);			
				} else if (expr instanceof Expr.UnOp) {
					Expr.UnOp uop = (Expr.UnOp) expr;
					Constant lhs = resolveAsConstant(uop.mhs, context, visited);				
					return evaluate(uop,lhs,context);			
				} else if (expr instanceof Expr.Set) {
					Expr.Set nop = (Expr.Set) expr;
					ArrayList<Constant> values = new ArrayList<Constant>();
					for (Expr arg : nop.arguments) {
						values.add(resolveAsConstant(arg,context, visited));
					}			
					return Constant.V_SET(values);			
				} else if (expr instanceof Expr.List) {
					Expr.List nop = (Expr.List) expr;
					ArrayList<Constant> values = new ArrayList<Constant>();
					for (Expr arg : nop.arguments) {
						values.add(resolveAsConstant(arg,context, visited));
					}			
					return Constant.V_LIST(values);			
				} else if (expr instanceof Expr.Record) {
					Expr.Record rg = (Expr.Record) expr;
					HashMap<String,Constant> values = new HashMap<String,Constant>();
					for(Map.Entry<String,Expr> e : rg.fields.entrySet()) {
						Constant v = resolveAsConstant(e.getValue(),context,visited);
						if(v == null) {
							return null;
						}
						values.put(e.getKey(), v);
					}
					return Constant.V_RECORD(values);
				} else if (expr instanceof Expr.Tuple) {
					Expr.Tuple rg = (Expr.Tuple) expr;			
					ArrayList<Constant> values = new ArrayList<Constant>();			
					for(Expr e : rg.fields) {
						Constant v = resolveAsConstant(e, context,visited);
						if(v == null) {
							return null;
						}
						values.add(v);				
					}
					return Constant.V_TUPLE(values);
				}  else if (expr instanceof Expr.Map) {
					Expr.Map rg = (Expr.Map) expr;			
					HashSet<Pair<Constant,Constant>> values = new HashSet<Pair<Constant,Constant>>();			
					for(Pair<Expr,Expr> e : rg.pairs) {
						Constant key = resolveAsConstant(e.first(), context,visited);
						Constant value = resolveAsConstant(e.second(), context,visited);
						if(key == null || value == null) {
							return null;
						}
						values.add(new Pair<Constant,Constant>(key,value));				
					}
					return Constant.V_MAP(values);
				} else if (expr instanceof Expr.FunctionOrMethod) {
					// TODO: add support for proper lambdas
					Expr.FunctionOrMethod f = (Expr.FunctionOrMethod) expr;
					return Constant.V_LAMBDA(f.nid, f.type.raw());
				} 
			} catch(SyntaxError.InternalFailure e) {
				throw e;
			} catch(ResolveError e) {
				syntaxError(e.getMessage(),context,expr,e);
			} catch(Throwable e) {
				internalFailure(e.getMessage(),context,expr,e);
			}

			internalFailure("unknown constant expression: " + expr.getClass().getName(),context,expr);
			return null; // deadcode
		}

		// =========================================================================
		// expandAsType
		// =========================================================================	

		public Nominal.EffectiveSet expandAsEffectiveSet(Nominal lhs) throws Exception {
			Type raw = lhs.raw();
			if(raw instanceof Type.EffectiveSet) {
				Type nominal = expandOneLevel(lhs.nominal());
				if(!(nominal instanceof Type.EffectiveSet)) {
					nominal = raw; // discard nominal information
				}
				return (Nominal.EffectiveSet) Nominal.construct(nominal,raw);
			} else {
				return null;
			}
		}

		public Nominal.EffectiveList expandAsEffectiveList(Nominal lhs) throws Exception {
			Type raw = lhs.raw();
			if(raw instanceof Type.EffectiveList) {
				Type nominal = expandOneLevel(lhs.nominal());
				if(!(nominal instanceof Type.EffectiveList)) {
					nominal = raw; // discard nominal information
				}
				return (Nominal.EffectiveList) Nominal.construct(nominal,raw);
			} else {
				return null;
			}
		}

		public Nominal.EffectiveCollection expandAsEffectiveCollection(Nominal lhs) throws Exception {
			Type raw = lhs.raw();
			if(raw instanceof Type.EffectiveCollection) {
				Type nominal = expandOneLevel(lhs.nominal());
				if(!(nominal instanceof Type.EffectiveCollection)) {
					nominal = raw; // discard nominal information
				}
				return (Nominal.EffectiveCollection) Nominal.construct(nominal,raw);
			} else {
				return null;
			}
		}
		
		public Nominal.EffectiveIndexible expandAsEffectiveMap(Nominal lhs) throws Exception {
			Type raw = lhs.raw();
			if(raw instanceof Type.EffectiveIndexible) {
				Type nominal = expandOneLevel(lhs.nominal());
				if(!(nominal instanceof Type.EffectiveIndexible)) {
					nominal = raw; // discard nominal information
				}
				return (Nominal.EffectiveIndexible) Nominal.construct(nominal,raw);
			} else {
				return null;
			}
		}
		
		public Nominal.EffectiveMap expandAsEffectiveDictionary(Nominal lhs) throws Exception {
			Type raw = lhs.raw();
			if(raw instanceof Type.EffectiveMap) {
				Type nominal = expandOneLevel(lhs.nominal());
				if(!(nominal instanceof Type.EffectiveMap)) {
					nominal = raw; // discard nominal information
				}
				return (Nominal.EffectiveMap) Nominal.construct(nominal,raw);
			} else {
				return null;
			}
		}

		public Nominal.EffectiveRecord expandAsEffectiveRecord(Nominal lhs) throws Exception {		
			Type raw = lhs.raw();

			if(raw instanceof Type.Record) {
				Type nominal = expandOneLevel(lhs.nominal());
				if(!(nominal instanceof Type.Record)) {
					nominal = (Type) raw; // discard nominal information
				}			
				return (Nominal.Record) Nominal.construct(nominal,raw);
			} else if(raw instanceof Type.UnionOfRecords) {
				Type nominal = expandOneLevel(lhs.nominal());
				if(!(nominal instanceof Type.UnionOfRecords)) {
					nominal = (Type) raw; // discard nominal information
				}			
				return (Nominal.UnionOfRecords) Nominal.construct(nominal,raw);
			} {
				return null;
			}
		}

		public Nominal.EffectiveTuple expandAsEffectiveTuple(Nominal lhs) throws Exception {
			Type raw = lhs.raw();
			if(raw instanceof Type.EffectiveTuple) {
				Type nominal = expandOneLevel(lhs.nominal());
				if(!(nominal instanceof Type.EffectiveTuple)) {
					nominal = raw; // discard nominal information
				}
				return (Nominal.EffectiveTuple) Nominal.construct(nominal,raw);
			} else {
				return null;
			}
		}

		public Nominal.Reference expandAsReference(Nominal lhs) throws Exception {
			Type.Reference raw = Type.effectiveReference(lhs.raw());
			if(raw != null) {
				Type nominal = expandOneLevel(lhs.nominal());
				if(!(nominal instanceof Type.Reference)) {
					nominal = raw; // discard nominal information
				}
				return (Nominal.Reference) Nominal.construct(nominal,raw);
			} else {
				return null;
			}
		}

		public Nominal.FunctionOrMethod expandAsFunctionOrMethod(Nominal lhs) throws Exception {
			Type.FunctionOrMethod raw = Type.effectiveFunctionOrMethod(lhs.raw());
			if(raw != null) {
				Type nominal = expandOneLevel(lhs.nominal());
				if(!(nominal instanceof Type.FunctionOrMethod)) {
					nominal = raw; // discard nominal information
				}
				return (Nominal.FunctionOrMethod) Nominal.construct(nominal,raw);
			} else {
				return null;
			}
		}

		private Type expandOneLevel(Type type) throws Exception {
			if(type instanceof Type.Nominal){
				Type.Nominal nt = (Type.Nominal) type;
				NameID nid = nt.name();			
				Path.ID mid = nid.module();

				WhileyFile wf = builder.getSourceFile(mid);
				Type r = null;

				if (wf != null) {			
					WhileyFile.Declaration decl = wf.declaration(nid.name());
					if(decl instanceof WhileyFile.Type) {
						WhileyFile.Type td = (WhileyFile.Type) decl;
						r = resolveAsType(td.pattern.toSyntacticType(), td)
								.nominal();
					} 
				} else {
					WyilFile m = builder.getModule(mid);
					WyilFile.TypeDeclaration td = m.type(nid.name());
					if(td != null) {
						r = td.type();
					}
				}
				if(r == null) {
					throw new ResolveError("unable to locate " + nid);
				}
				return expandOneLevel(r);
			} else if(type instanceof Type.Leaf 
					|| type instanceof Type.Reference
					|| type instanceof Type.Tuple
					|| type instanceof Type.Set
					|| type instanceof Type.List
					|| type instanceof Type.Map
					|| type instanceof Type.Record
					|| type instanceof Type.FunctionOrMethod
					|| type instanceof Type.Negation) {
				return type;
			} else {
				Type.Union ut = (Type.Union) type;
				ArrayList<Type> bounds = new ArrayList<Type>();
				for(Type b : ut.bounds()) {
					bounds.add(expandOneLevel(b));
				}
				return Type.Union(bounds);
			} 
		}

		// =========================================================================
		// Constant Evaluation [this should not be located here?]
		// =========================================================================	
				
		private Constant evaluate(Expr.UnOp bop, Constant v, Context context) {
			switch(bop.op) {
				case NOT:
					if(v instanceof Constant.Bool) {
						Constant.Bool b = (Constant.Bool) v;
						return Constant.V_BOOL(!b.value);
					}
					syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION),context,bop);
					break;
				case NEG:
					if(v instanceof Constant.Integer) {
						Constant.Integer b = (Constant.Integer) v;
						return Constant.V_INTEGER(b.value.negate());
					} else if(v instanceof Constant.Decimal) {
						Constant.Decimal b = (Constant.Decimal) v;
						return Constant.V_DECIMAL(b.value.negate());
					}
					syntaxError(errorMessage(INVALID_NUMERIC_EXPRESSION),context,bop);
					break;
				case INVERT:
					if(v instanceof Constant.Byte) {
						Constant.Byte b = (Constant.Byte) v;
						return Constant.V_BYTE((byte) ~b.value);
					}
					break;
			}
			syntaxError(errorMessage(INVALID_UNARY_EXPRESSION),context,bop);
			return null;
		}
		
		private Constant evaluate(Expr.BinOp bop, Constant v1, Constant v2, Context context) {
			Type v1_type = v1.type();
			Type v2_type = v2.type();
			Type lub = Type.Union(v1_type, v2_type);

			// FIXME: there are bugs here related to coercions.

			if(Type.isSubtype(Type.T_BOOL, lub)) {
				return evaluateBoolean(bop,(Constant.Bool) v1,(Constant.Bool) v2, context);
			} else if(Type.isSubtype(Type.T_INT, lub)) {
				return evaluate(bop,(Constant.Integer) v1, (Constant.Integer) v2, context);
			} else if (Type.isImplicitCoerciveSubtype(Type.T_REAL, v1_type)
					&& Type.isImplicitCoerciveSubtype(Type.T_REAL, v1_type)) {			
				if(v1 instanceof Constant.Integer) {
					Constant.Integer i1 = (Constant.Integer) v1;
					v1 = Constant.V_DECIMAL(new BigDecimal(i1.value));
				} else if(v2 instanceof Constant.Integer) {
					Constant.Integer i2 = (Constant.Integer) v2;
					v2 = Constant.V_DECIMAL(new BigDecimal(i2.value));
				}
				return evaluate(bop,(Constant.Decimal) v1, (Constant.Decimal) v2, context);
			} else if(Type.isSubtype(Type.T_LIST_ANY, lub)) {
				return evaluate(bop,(Constant.List)v1,(Constant.List)v2, context);
			} else if(Type.isSubtype(Type.T_SET_ANY, lub)) {
				return evaluate(bop,(Constant.Set) v1, (Constant.Set) v2, context);
			} 
			syntaxError(errorMessage(INVALID_BINARY_EXPRESSION),context,bop);
			return null;
		}

		private Constant evaluateBoolean(Expr.BinOp bop, Constant.Bool v1, Constant.Bool v2, Context context) {				
			switch(bop.op) {
			case AND:
				return Constant.V_BOOL(v1.value & v2.value);
			case OR:		
				return Constant.V_BOOL(v1.value | v2.value);
			case XOR:
				return Constant.V_BOOL(v1.value ^ v2.value);
			}
			syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION),context,bop);
			return null;
		}

		private Constant evaluate(Expr.BinOp bop, Constant.Integer v1, Constant.Integer v2, Context context) {		
			switch(bop.op) {
			case ADD:
				return Constant.V_INTEGER(v1.value.add(v2.value));
			case SUB:
				return Constant.V_INTEGER(v1.value.subtract(v2.value));
			case MUL:
				return Constant.V_INTEGER(v1.value.multiply(v2.value));
			case DIV:
				return Constant.V_INTEGER(v1.value.divide(v2.value));
			case REM:
				return Constant.V_INTEGER(v1.value.remainder(v2.value));	
			}
			syntaxError(errorMessage(INVALID_NUMERIC_EXPRESSION),context,bop);
			return null;
		}
		
		private Constant evaluate(Expr.BinOp bop, Constant.Decimal v1, Constant.Decimal v2, Context context) {		
			switch(bop.op) {
			case ADD:
				return Constant.V_DECIMAL(v1.value.add(v2.value));
			case SUB:
				return Constant.V_DECIMAL(v1.value.subtract(v2.value));
			case MUL:
				return Constant.V_DECIMAL(v1.value.multiply(v2.value));
			case DIV:
				return Constant.V_DECIMAL(v1.value.divide(v2.value));			
			}
			syntaxError(errorMessage(INVALID_NUMERIC_EXPRESSION),context,bop);
			return null;
		}

		private Constant evaluate(Expr.BinOp bop, Constant.List v1, Constant.List v2, Context context) {
			switch(bop.op) {
			case ADD:
				ArrayList<Constant> vals = new ArrayList<Constant>(v1.values);
				vals.addAll(v2.values);
				return Constant.V_LIST(vals);
			}
			syntaxError(errorMessage(INVALID_LIST_EXPRESSION),context,bop);
			return null;
		}

		private Constant evaluate(Expr.BinOp bop, Constant.Set v1, Constant.Set v2, Context context) {		
			switch(bop.op) {
			case UNION:
			{
				HashSet<Constant> vals = new HashSet<Constant>(v1.values);			
				vals.addAll(v2.values);
				return Constant.V_SET(vals);
			}
			case INTERSECTION:
			{
				HashSet<Constant> vals = new HashSet<Constant>();			
				for(Constant v : v1.values) {
					if(v2.values.contains(v)) {
						vals.add(v);
					}
				}			
				return Constant.V_SET(vals);
			}
			case SUB:
			{
				HashSet<Constant> vals = new HashSet<Constant>();			
				for(Constant v : v1.values) {
					if(!v2.values.contains(v)) {
						vals.add(v);
					}
				}			
				return Constant.V_SET(vals);
			}
			}
			syntaxError(errorMessage(INVALID_SET_EXPRESSION),context,bop);
			return null;
		}		
		
		public boolean isVisible(NameID nid, Context context) throws Exception {		
			Path.ID mid = nid.module();
			if(mid.equals(context.file().module)) {
				return true;
			}
			WhileyFile wf = builder.getSourceFile(mid);
			if(wf != null) {
				WhileyFile.Declaration d = wf.declaration(nid.name());
				if(d instanceof WhileyFile.Constant) {
					WhileyFile.Constant td = (WhileyFile.Constant) d;
					return td.isPublic() || td.isProtected();
				} else if(d instanceof WhileyFile.Type) {
					WhileyFile.Type td = (WhileyFile.Type) d;
					return td.isPublic() || td.isProtected();	
				}
				return false;
			} else {
				// we have to do the following basically because we don't load
				// modifiers properly out of jvm class files (at the moment).
				return true;
//				WyilFile w = builder.getModule(mid);
//				WyilFile.ConstDef c = w.constant(nid.name());
//				WyilFile.TypeDef t = w.type(nid.name());
//				if(c != null) {
//					return c.isPublic() || c.isProtected();
//				} else {
//					return t.isPublic() || t.isProtected();
//				}
			}		
		}

	// =========================================================================
	// Misc
	// =========================================================================

	// Check t1 :> t2
	private void checkIsSubtype(Nominal t1, Nominal t2, SyntacticElement elem) {
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

	// Check t1 :> t2
	private void checkIsSubtype(Nominal t1, Nominal t2, SyntacticElement elem,
			Context context) {
		if (!Type.isImplicitCoerciveSubtype(t1.raw(), t2.raw())) {
			syntaxError(
					errorMessage(SUBTYPE_ERROR, t1.nominal(), t2.nominal()),
					context, elem);
		}
	}

	private void checkIsSubtype(Nominal t1, Expr t2, Context context) {
		if (!Type.isImplicitCoerciveSubtype(t1.raw(), t2.result().raw())) {
			// We use the nominal type for error reporting, since this includes
			// more helpful names.
			syntaxError(
					errorMessage(SUBTYPE_ERROR, t1.nominal(), t2.result()
							.nominal()), context, t2);
		}
	}

	private void checkIsSubtype(Type t1, Expr t2, Context context) {
		if (!Type.isImplicitCoerciveSubtype(t1, t2.result().raw())) {
			// We use the nominal type for error reporting, since this includes
			// more helpful names.
			syntaxError(errorMessage(SUBTYPE_ERROR, t1, t2.result().nominal()),
					context, t2);
		}
	}

	private static final Environment BOTTOM = new Environment();

	private static final Environment join(Environment lhs, Environment rhs) {

		// first, need to check for the special bottom value case.

		if (lhs == BOTTOM) {
			return rhs;
		} else if (rhs == BOTTOM) {
			return lhs;
		}

		// ok, not bottom so compute intersection.

		lhs.free();
		rhs.free();

		Environment result = new Environment();
		for (String key : lhs.keySet()) {
			if (rhs.containsKey(key)) {
				Nominal lhs_t = lhs.get(key);
				Nominal rhs_t = rhs.get(key);
				result.put(key, Nominal.Union(lhs_t, rhs_t));
			}
		}

		return result;
	}
}
