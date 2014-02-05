package wyc.builder;

import static wybs.lang.SyntaxError.internalFailure;
import static wybs.lang.SyntaxError.syntaxError;
import static wyc.lang.WhileyFile.internalFailure;
import static wyc.lang.WhileyFile.syntaxError;
import static wyil.util.ErrorMessages.*;

import java.math.BigDecimal;
import java.util.*;

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
	
	private NameResolution resolver;
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
	// Declarations
	// =========================================================================

	/**
	 * Resolve types for a given type declaration. If an invariant expression is
	 * given, then we have to propagate and resolve types throughout the
	 * expression.
	 * 
	 * @param td
	 *            Type declaration to check.
	 * @throws Exception
	 */
	public void propagate(WhileyFile.Type td) throws Exception {
		
		// First, resolve the declared syntactic type into the corresponding
		// nominal type.
		td.resolvedType = resolver.resolveAsType(td.pattern.toSyntacticType(), td);
		
		if(td.invariant != null) {						
			// Second, an invariant expression is given, so propagate through
			// that.  
			
			// Construct the appropriate typing environment			
			Environment environment = new Environment();			
			environment = addDeclaredVariables(td.pattern,environment,td);			
			// Propagate type information through the constraint 			
			td.invariant = propagate(td.invariant,environment,td);
		}
	}

	/**
	 * Propagate and check types for a given constant declaration.
	 * 
	 * @param cd
	 *            Constant declaration to check.
	 * @throws Exception
	 */
	public void propagate(WhileyFile.Constant cd) throws Exception {
		NameID nid = new NameID(cd.file().module, cd.name);
		cd.resolvedValue = resolver.resolveAsConstant(nid);
	}
	
	/**
	 * Propagate and check types for a given function or method declaration.
	 * 
	 * @param fd
	 *            Function or method declaration to check.
	 * @throws Exception
	 */
	public void propagate(WhileyFile.FunctionOrMethod d) throws Exception {
		this.current = d; // ugly
		Environment environment = new Environment();

		// Resolve the types of all parameters and construct an appropriate
		// environment for use in the flow-sensitive type propagation.
		for (WhileyFile.Parameter p : d.parameters) {
			environment = environment.put(p.name, resolver.resolveAsType(p.type, d));
		}

		// Resolve types for any preconditions (i.e. requires clauses) provided.
		final List<Expr> d_requires = d.requires;
		for (int i = 0; i != d_requires.size(); ++i) {
			Expr condition = d_requires.get(i);
			condition = propagate(condition, environment.clone(), d);
			d_requires.set(i, condition);
		}

		// Resolve types for any postconditions (i.e. ensures clauses) provided.
		final List<Expr> d_ensures = d.ensures;
		if (d_ensures.size() > 0) {
			// At least one ensures clause is provided; so, first, construct an
			// appropriate environment from the initial one create.
			Environment ensuresEnvironment = addDeclaredVariables(d.ret,
					environment.clone(), d);

			// Now, type check each ensures clause
			for (int i = 0; i != d_ensures.size(); ++i) {
				Expr condition = d_ensures.get(i);
				condition = propagate(condition, ensuresEnvironment, d);
				d_ensures.set(i, condition);
			}
		}

		// Resolve the overall type for the function or method.
		if (d instanceof WhileyFile.Function) {
			WhileyFile.Function f = (WhileyFile.Function) d;
			f.resolvedType = resolver.resolveAsType(f.unresolvedType(), d);
		} else {
			WhileyFile.Method m = (WhileyFile.Method) d;
			m.resolvedType = resolver.resolveAsType(m.unresolvedType(), d);
		}

		// Finally, propagate type information throughout all statements in the
		// function / method body. 
		propagate(d.statements, environment);
	}
	
	// =========================================================================
	// Blocks & Statements
	// =========================================================================

	/**
	 * Propagate type information in a flow-sensitive fashion through a block of
	 * statements, whilst type checking each statement and expression.
	 * 
	 * @param block
	 *            Block of statements to flow sensitively type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	private Environment propagate(ArrayList<Stmt> block, Environment environment) {

		for (int i = 0; i != block.size(); ++i) {
			Stmt stmt = block.get(i);
			if (stmt instanceof Expr) {
				block.set(i, (Stmt) propagate((Expr) stmt, environment, current));
			} else {
				environment = propagate(stmt, environment);
			}
		}

		return environment;
	}
	
	/**
	 * Propagate type information in a flow-sensitive fashion through a given
	 * statement, whilst type checking it at the same time. For statements which
	 * contain other statements (e.g. if, while, etc), then this will
	 * recursively propagate type information through them as well.
	 * 
	 * 
	 * @param block
	 *            Block of statements to flow-sensitively type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
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
	
	/**
	 * Type check an assertion statement. This requires checking that the
	 * expression being asserted is well-formed and has boolean type.
	 * 
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	private Environment propagate(Stmt.Assert stmt,
			Environment environment) {
		stmt.expr = propagate(stmt.expr,environment,current);
		checkIsSubtype(Type.T_BOOL,stmt.expr);
		return environment;
	}
	
	/**
	 * Type check an assume statement. This requires checking that the
	 * expression being asserted is well-formed and has boolean type.
	 * 
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	private Environment propagate(Stmt.Assume stmt,
			Environment environment) {
		stmt.expr = propagate(stmt.expr,environment,current);
		checkIsSubtype(Type.T_BOOL,stmt.expr);
		return environment;
	}
	
	/**
	 * Type check a variable declaration statement. This must associate the
	 * given variable with either its declared and actual type in the
	 * environment. If no initialiser is given, then the actual type is the void
	 * (since the variable is not yet defined). Otherwise, the actual type is
	 * the type of the initialiser expression. Additionally, when an initialiser
	 * is given we must check it is well-formed and that it is a subtype of the
	 * declared type.
	 * 
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	private Environment propagate(Stmt.VariableDeclaration stmt,
			Environment environment) throws Exception {

		// First, resolve declared type
		stmt.type = resolver.resolveAsType(stmt.unresolvedType,current);
		
		// First, resolve type of initialiser
		if(stmt.expr != null) {
			stmt.expr = propagate(stmt.expr,environment,current);
			checkIsSubtype(stmt.type,stmt.expr);
		}
		
		// Second, update environment accordingly. Observe that we can safely
		// assume the variable is not already declared in the enclosing scope
		// because the parser checks this for us.		
		environment = environment.put(stmt.name, stmt.type);	
		
		// Done.
		return environment;
	}
	
	/**
	 * Type check an assignment statement. 
	 * 
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	private Environment propagate(Stmt.Assign stmt,
			Environment environment) throws Exception {

		Expr.LVal lhs = propagate(stmt.lhs,environment);
		Expr rhs = propagate(stmt.rhs,environment,current);
				
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
	
	/**
	 * Type check a break statement. This requires propagating the current
	 * environment to the block destination, to ensure that the actual types of
	 * all variables at that point are precise.
	 * 
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	private Environment propagate(Stmt.Break stmt,
			Environment environment) {
		// FIXME: need to propagate environment to the break destination
		return BOTTOM;
	}
	
	/**
	 * Type check an assume statement. This requires checking that the
	 * expression being printed is well-formed and has string type.
	 * 
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	private Environment propagate(Stmt.Debug stmt,
			Environment environment) {
		stmt.expr = propagate(stmt.expr,environment,current);				
		checkIsSubtype(Type.T_STRING,stmt.expr);
		return environment;
	}
	
	/**
	 * Type check a do-while statement. 
	 * 
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
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
				tmp = propagateCondition(stmt.condition,true,old.clone(),current).second();
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
			invariant = propagate(invariant, environment, current);
			stmt_invariants.set(i, invariant);
			checkIsSubtype(Type.T_BOOL, invariant);
		}
		
		Pair<Expr,Environment> p = propagateCondition(stmt.condition,false,environment,current);
		stmt.condition = p.first();
		environment = p.second();
		
		return environment;
	}

	/**
	 * Type check a <code>for</code> statement. 
	 * 
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	private Environment propagate(Stmt.ForAll stmt,
			Environment environment) throws Exception {
		
		stmt.source = propagate(stmt.source,environment,current);
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
			stmt.invariant = propagate(stmt.invariant, environment, current);
			checkIsSubtype(Type.T_BOOL,stmt.invariant);
		}
				
		return environment;
	}
	
	/**
	 * Type check an if-statement. To do this, we propagate the environment
	 * through both sides of condition expression. Each can produce a different
	 * environment in the case that runtime type tests are used. These
	 * potentially updated environments are then passed through the true and
	 * false blocks which, in turn, produce updated environments. Finally, these
	 * two environments are joined back together. The following illustrates:
	 * 
	 * <pre>
	 *                    //  Environment
	 * function f(int|null x) => int:
	 *                    // {x : int|null}
	 *    if x is null:
	 *                    // {x : null} 
	 *        x = 0
	 *                    // {x : int}      
	 *    else:
	 *                    // {x : int}
	 *        x = x + 1
	 *                    // {x : int}
	 *    // --------------------------------------------------
	 *                    // {x : int} o {x : int} => {x : int} 
	 *    return x
	 * </pre>
	 * 
	 * Here, we see that the type of <code>x</code> is initially
	 * <code>int|null</code> before the first statement of the function body. On
	 * the true branch of the type test this is updated to <code>null</code>,
	 * whilst on the false branch it is updated to <code>int</code>. Finally,
	 * the type of <code>x</code> at the end of each block is <code>int</code>
	 * and, hence, its type after the if-statement is <code>int</code>.
	 * 
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	
	private Environment propagate(Stmt.IfElse stmt,
			Environment environment) {
		
		// First, check condition and apply variable retypings.
		Pair<Expr,Environment> p1,p2;
		
		p1 = propagateCondition(stmt.condition,true,environment.clone(),current);
		p2 = propagateCondition(stmt.condition,false,environment,current);
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
	
	/**
	 * Type check a <code>return</code> statement. If a return expression is
	 * given, then we must check that this is well-formed and is a subtype of
	 * the enclosing function or method's declared return type. The environment
	 * after a return statement is "bottom" because that represents an
	 * unreachable program point.
	 * 
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	private Environment propagate(Stmt.Return stmt, Environment environment)
			throws Exception {

		if (stmt.expr != null) {
			stmt.expr = propagate(stmt.expr, environment, current);
			Nominal rhs = stmt.expr.result();
			checkIsSubtype(current.resolvedType().ret(), rhs, stmt.expr);
		}

		environment.free();
		return BOTTOM;
	}
	
	/**
	 * Type check a <code>skip</code> statement, which has no effect on the
	 * environment.
	 * 
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	private Environment propagate(Stmt.Skip stmt,
			Environment environment) {		
		return environment;
	}
	
	/**
	 * Type check a <code>switch</code> statement. This is similar, in some
	 * ways, to the handling of if-statements except that we have n code blocks
	 * instead of just two. Therefore, we propagate type information through
	 * each block, which produces n potentially different environments and these
	 * are all joined together to produce the environment which holds after this
	 * statement. For example:
	 * 
	 * <pre>
	 *                    //  Environment
	 * function f(int x) => int|null:
	 *    int|null y
	 *                    // {x : int, y : void}
	 *    switch x:
	 *       case 0:
	 *                    // {x : int, y : void}                          
	 *           return 0 
	 *                    // { }
	 *       case 1,2,3:
	 *                    // {x : int, y : void}       
	 *           y = x
	 *                    // {x : int, y : int}
	 *       default:
	 *                    // {x : int, y : void}       
	 *           y = null
	 *                    // {x : int, y : null}
	 *    // --------------------------------------------------
	 *                    // {} o
	 *                    // {x : int, y : int} o 
	 *                    // {x : int, y : null} 
	 *                    // => {x : int, y : int|null} 
	 *    return y
	 * </pre>
	 * 
	 * Here, the environment after the declaration of <code>y</code> has its
	 * actual type as <code>void</code> since no value has been assigned yet.
	 * For each of the case blocks, this initial environment is (separately)
	 * updated to produce three different environments. Finally, each of these
	 * is joined back together to produce the environment going into the
	 * <code>return</code> statement.
	 * 
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	private Environment propagate(Stmt.Switch stmt,
			Environment environment) throws Exception {
		
		stmt.expr = propagate(stmt.expr,environment,current);		
		
		Environment finalEnv = null;
		boolean hasDefault = false;
		
		for(Stmt.Case c : stmt.cases) {
			
			// first, resolve the constants
			
			ArrayList<Constant> values = new ArrayList<Constant>();
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
	
	/**
	 * Type check a <code>throw</code> statement. We must check that the throw
	 * expression is well-formed. The environment after a throw statement is
	 * "bottom" because that represents an unreachable program point.
	 * 
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	private Environment propagate(Stmt.Throw stmt,
			Environment environment) {
		stmt.expr = propagate(stmt.expr,environment,current);
		return BOTTOM;
	}
	
	/**
	 * Type check a try-catch statement.
	 * 
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
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
	
	/**
	 * Type check a <code>whiley</code> statement.
	 * 
	 * @param stmt
	 *            Statement to type check
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this block
	 * @return
	 */
	private Environment propagate(Stmt.While stmt,
			Environment environment) {

		// Iterate to a fixed point
		Environment old = null;
		Environment tmp = null;
		Environment orig = environment.clone();
		do {
			old = environment.clone();
			tmp = propagateCondition(stmt.condition,true,old.clone(),current).second();			
			environment = join(orig.clone(),propagate(stmt.body,tmp));			
			old.free(); // hacky, but safe
		} while(!environment.equals(old));
		
		List<Expr> stmt_invariants = stmt.invariants;
		for (int i = 0; i != stmt_invariants.size(); ++i) {
			Expr invariant = stmt_invariants.get(i);
			invariant = propagate(invariant, environment, current);
			stmt_invariants.set(i, invariant);
			checkIsSubtype(Type.T_BOOL, invariant);
		}
				
		Pair<Expr,Environment> p = propagateCondition(stmt.condition,false,environment,current);
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
				Expr index = propagate(ai.index,environment,current);
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
			Nominal type = resolver.resolveAsType(pattern.toSyntacticType(),
					context);
			environment = environment.put(pattern.var, type);
		}
		
		return environment;
	}
		
	// =========================================================================
	// Condition
	// =========================================================================
	
	/**
	 * <p>
	 * Propagate type information through an expression being used as a
	 * condition, whilst checking it is well-typed at the same time. When used
	 * as a condition (e.g. of an if-statement) an expression may update the
	 * environment in accordance with any type tests used within. This is
	 * important to ensure that variables are retyped in e.g. if-statements. For
	 * example:
	 * </p>
	 * 
	 * <pre>
	 * if x is int && x >= 0
	 *    // x is int
	 * else:
	 *    //
	 * </pre>
	 * <p>
	 * Here, the if-condition must update the type of x in the true branch, but
	 * *cannot* update the type of x in the false branch.
	 * </p>
	 * <p>
	 * To handle conditions on the false branch, this function uses a sign flag
	 * rather than expanding them using DeMorgan's laws (for efficiency). When
	 * determining type for the false branch, the sign flag is initially false.
	 * This prevents falsely concluding that e.g. "x is int" holds in the false
	 * branch.
	 * </p>
	 * 
	 * @param expr
	 *            Condition expression to type check and propagate through
	 * @param sign
	 *            Indicates how expression should be treated. If true, then
	 *            expression is treated "as is"; if false, then expression
	 *            should be treated as negated
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this expression
	 * @param context
	 *            Enclosing context of this expression (e.g. type declaration,
	 *            function declaration, etc)
	 * @return
	 */
	public Pair<Expr, Environment> propagateCondition(Expr expr, boolean sign,
			Environment environment, Context context) {
		
		// Split up into the compound and non-compound forms.
		
		if(expr instanceof Expr.UnOp) {
			return propagateCondition((Expr.UnOp)expr,sign,environment,context);		
		} else if(expr instanceof Expr.BinOp) {  
			return propagateCondition((Expr.BinOp)expr,sign,environment,context);
		} else {
			// For non-compound forms, can just default back to the base rules
			// for general expressions.
			expr = propagate(expr,environment,context);
			checkIsSubtype(Type.T_BOOL,expr,context);
			return new Pair<Expr, Environment>(expr,environment);
		}		
	}
	
	/**
	 * <p>
	 * Propagate type information through a unary expression being used as a
	 * condition and, in fact, only logical not is syntactically valid here.
	 * </p>
	 * 
	 * @param expr
	 *            Condition expression to type check and propagate through
	 * @param sign
	 *            Indicates how expression should be treated. If true, then
	 *            expression is treated "as is"; if false, then expression
	 *            should be treated as negated
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this expression
	 * @param context
	 *            Enclosing context of this expression (e.g. type declaration,
	 *            function declaration, etc)
	 * @return
	 */
	private Pair<Expr, Environment> propagateCondition(Expr.UnOp expr, boolean sign,
			Environment environment, Context context) {
		Expr.UnOp uop = (Expr.UnOp) expr; 
		
		// Check whether we have logical not
		
		if(uop.op == Expr.UOp.NOT) { 
			Pair<Expr,Environment> p = propagateCondition(uop.mhs,!sign,environment,context);
			uop.mhs = p.first();			
			checkIsSubtype(Type.T_BOOL,uop.mhs,context);
			uop.type = Nominal.T_BOOL;
			return new Pair(uop,p.second());
		} else {
			// Nothing else other than logical not is valid at this point.			
			syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION),context,expr);
			return null; // deadcode
		}	
	}
	
	/**
	 * <p>
	 * Propagate type information through a binary expression being used as a
	 * condition. In this case, only logical connectives ("&&", "||", "^") and
	 * comparators (e.g. "==", "<=", etc) are permitted here.
	 * </p>
	 * 
	 * @param expr
	 *            Condition expression to type check and propagate through
	 * @param sign
	 *            Indicates how expression should be treated. If true, then
	 *            expression is treated "as is"; if false, then expression
	 *            should be treated as negated
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this expression
	 * @param context
	 *            Enclosing context of this expression (e.g. type declaration,
	 *            function declaration, etc)
	 * @return
	 */
	private Pair<Expr, Environment> propagateCondition(Expr.BinOp bop, boolean sign,
			Environment environment, Context context) {		
		Expr.BOp op = bop.op;
		
		// Split into the two broard cases: logical connectives and primitives.
		
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
	
	/**
	 * <p>
	 * Propagate type information through a binary expression being used as a
	 * logical connective ("&&", "||", "^").
	 * </p>
	 * 
	 * @param bop
	 *            Binary operator for this expression.
	 * @param sign
	 *            Indicates how expression should be treated. If true, then
	 *            expression is treated "as is"; if false, then expression
	 *            should be treated as negated
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this expression
	 * @param context
	 *            Enclosing context of this expression (e.g. type declaration,
	 *            function declaration, etc)
	 * @return
	 */
	private Pair<Expr, Environment> resolveNonLeafCondition(Expr.BinOp bop,
			boolean sign, Environment environment, Context context) {
		Expr.BOp op = bop.op;
		Pair<Expr,Environment> p;
		boolean followOn = (sign && op == Expr.BOp.AND) || (!sign && op == Expr.BOp.OR);
		
		if(followOn) {			
			// In this case, the environment feeds directly from the result of
			// propagating through the lhs into the rhs, and then into the
			// result of this expression. This means that updates to the
			// environment by either the lhs or rhs are visible outside of this
			// method.
			p = propagateCondition(bop.lhs,sign,environment.clone(),context);			
			bop.lhs = p.first();
			p = propagateCondition(bop.rhs,sign,p.second(),context);
			bop.rhs = p.first();
			environment = p.second();
		} else {
			// We could do better here
			p = propagateCondition(bop.lhs,sign,environment.clone(),context);
			bop.lhs = p.first();
			Environment local = p.second();
			// Recompute the lhs assuming that it is false. This is necessary to
			// generate the right environment going into the rhs, which is only
			// evaluated if the lhs is false.  For example:
			//
			// if(e is int && e > 0):
			//     //
			// else:
			//     // <-
			// 
			// In the false branch, we're determing the environment for 
			// !(e is int && e > 0).  This becomes !(e is int) || (e <= 0) where 
			// on the rhs we require (e is int).
			p = propagateCondition(bop.lhs,!sign,environment.clone(),context);
			// Note, the following is intentional since we're specifically
			// considering the case where the lhs was false, and this case is
			// true.
			p = propagateCondition(bop.rhs,sign,p.second(),context);
			bop.rhs = p.first();
			environment = join(local,p.second());
		}
		
		checkIsSubtype(Type.T_BOOL,bop.lhs,context);
		checkIsSubtype(Type.T_BOOL,bop.rhs,context);	
		bop.srcType = Nominal.T_BOOL;
		
		return new Pair<Expr,Environment>(bop,environment);
	}
	
	/**
	 * <p>
	 * Propagate type information through a binary expression being used as a
	 * comparators (e.g. "==", "<=", etc).
	 * </p>
	 * 
	 * @param bop
	 *            Binary operator for this expression.
	 * @param sign
	 *            Indicates how expression should be treated. If true, then
	 *            expression is treated "as is"; if false, then expression
	 *            should be treated as negated
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this expression
	 * @param context
	 *            Enclosing context of this expression (e.g. type declaration,
	 *            function declaration, etc)
	 * @return
	 */
	private Pair<Expr, Environment> resolveLeafCondition(Expr.BinOp bop,
			boolean sign, Environment environment, Context context) {
		Expr.BOp op = bop.op;
		
		Expr lhs = propagate(bop.lhs,environment,context);
		Expr rhs = propagate(bop.rhs,environment,context);
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
				Nominal unconstrainedTestType = resolver.resolveAsUnconstrainedType(tv.unresolvedType,context);
				
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
			Type.EffectiveList listType = rhsRawType instanceof Type.EffectiveList ? (Type.EffectiveList) rhsRawType
					: null;
			Type.EffectiveSet setType = rhsRawType instanceof Type.EffectiveSet ? (Type.EffectiveSet) rhsRawType
					: null;

			if (listType != null
					&& !Type.isImplicitCoerciveSubtype(listType.element(),
							lhsRawType)) {
				syntaxError(
						errorMessage(INCOMPARABLE_OPERANDS, lhsRawType,
								listType.element()), context, bop);
			} else if (setType != null
					&& !Type.isImplicitCoerciveSubtype(setType.element(),
							lhsRawType)) {
				syntaxError(
						errorMessage(INCOMPARABLE_OPERANDS, lhsRawType,
								setType.element()), context, bop);
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
		
		return new Pair<Expr, Environment>(bop,environment);
	}
	
	// =========================================================================
	// Expressions
	// =========================================================================
		
	/**
	 * Propagate types through a given expression, whilst checking that it is
	 * well typed. In this case, any use of a runtime type test cannot effect
	 * callers of this function.
	 * 
	 * @param expr
	 *            Expression to propagate types through.
	 * @param environment
	 *            Determines the type of all variables immediately going into
	 *            this expression
	 * @param context
	 *            Enclosing context of this expression (e.g. type declaration,
	 *            function declaration, etc)
	 * @return
	 */
	public Expr propagate(Expr expr, Environment environment, Context context) {
		
		try {
			if(expr instanceof Expr.BinOp) {
				return propagate((Expr.BinOp) expr,environment,context); 
			} else if(expr instanceof Expr.UnOp) {
				return propagate((Expr.UnOp) expr,environment,context); 
			} else if(expr instanceof Expr.Comprehension) {
				return propagate((Expr.Comprehension) expr,environment,context); 
			} else if(expr instanceof Expr.Constant) {
				return propagate((Expr.Constant) expr,environment,context); 
			} else if(expr instanceof Expr.Cast) {
				return propagate((Expr.Cast) expr,environment,context); 
			} else if(expr instanceof Expr.Map) {
				return propagate((Expr.Map) expr,environment,context); 
			} else if(expr instanceof Expr.AbstractFunctionOrMethod) {
				return propagate((Expr.AbstractFunctionOrMethod) expr,environment,context); 
			} else if(expr instanceof Expr.AbstractInvoke) {
				return propagate((Expr.AbstractInvoke) expr,environment,context); 
			} else if(expr instanceof Expr.AbstractIndirectInvoke) {
				return propagate((Expr.AbstractIndirectInvoke) expr,environment,context); 
			} else if(expr instanceof Expr.IndexOf) {
				return propagate((Expr.IndexOf) expr,environment,context); 
			} else if(expr instanceof Expr.Lambda) {
				return propagate((Expr.Lambda) expr,environment,context); 
			} else if(expr instanceof Expr.LengthOf) {
				return propagate((Expr.LengthOf) expr,environment,context); 
			} else if(expr instanceof Expr.AbstractVariable) {
				return propagate((Expr.AbstractVariable) expr,environment,context); 
			} else if(expr instanceof Expr.List) {
				return propagate((Expr.List) expr,environment,context); 
			} else if(expr instanceof Expr.Set) {
				return propagate((Expr.Set) expr,environment,context); 
			} else if(expr instanceof Expr.SubList) {
				return propagate((Expr.SubList) expr,environment,context); 
			} else if(expr instanceof Expr.SubString) {
				return propagate((Expr.SubString) expr,environment,context); 
			} else if(expr instanceof Expr.AbstractDotAccess) {
				return propagate((Expr.AbstractDotAccess) expr,environment,context); 
			} else if(expr instanceof Expr.Dereference) {
				return propagate((Expr.Dereference) expr,environment,context); 
			} else if(expr instanceof Expr.Record) {
				return propagate((Expr.Record) expr,environment,context); 
			} else if(expr instanceof Expr.New) {
				return propagate((Expr.New) expr,environment,context); 
			} else if(expr instanceof Expr.Tuple) {
				return  propagate((Expr.Tuple) expr,environment,context); 
			} else if(expr instanceof Expr.TypeVal) {
				return propagate((Expr.TypeVal) expr,environment,context); 
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
	
	private Expr propagate(Expr.BinOp expr,
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
			return propagateCondition(expr,true,environment,context).first();
		}
		
		Expr lhs = propagate(expr.lhs,environment,context);
		Expr rhs = propagate(expr.rhs,environment,context);
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
				return propagateCondition(expr,true,environment,context).first();				
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
	
	private Expr propagate(Expr.UnOp expr, Environment environment,
			Context context) throws Exception {

		if (expr.op == Expr.UOp.NOT) {
			// hand off to special method for conditions
			return propagateCondition(expr, true, environment, context).first();
		}
		
		Expr src = propagate(expr.mhs, environment,context);
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
	
	private Expr propagate(Expr.Comprehension expr,
			Environment environment, Context context) throws Exception {
		
		ArrayList<Pair<String,Expr>> sources = expr.sources;
		Environment local = environment.clone();
		for(int i=0;i!=sources.size();++i) {
			Pair<String,Expr> p = sources.get(i);
			Expr e = propagate(p.second(),local,context);			
			p = new Pair<String,Expr>(p.first(),e);
			sources.set(i,p);
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
			expr.condition = propagate(expr.condition,local,context);
		}
		
		if (expr.cop == Expr.COp.SETCOMP || expr.cop == Expr.COp.LISTCOMP) {						
			expr.value = propagate(expr.value,local,context);
			expr.type = Nominal.Set(expr.value.result(), false);
		} else {
			expr.type = Nominal.T_BOOL;
		}
		
		local.free();				
		
		return expr;
	}
	
	private Expr propagate(Expr.Constant expr,
			Environment environment, Context context) {
		return expr;
	}

	private Expr propagate(Expr.Cast c,
			Environment environment, Context context) throws Exception {
		c.expr = propagate(c.expr,environment,context);		
		c.type = resolver.resolveAsType(c.unresolvedType, context);
		Type from = c.expr.result().raw();		
		Type to = c.type.raw();
		if (!Type.isExplicitCoerciveSubtype(to, from)) {			
			syntaxError(errorMessage(SUBTYPE_ERROR, to, from), context, c);
		}	
		return c;
	}
			
	private Expr propagate(Expr.AbstractFunctionOrMethod expr,
			Environment environment, Context context) throws Exception {
		
		if(expr instanceof Expr.FunctionOrMethod) {
			return expr;
		} 		
		
		Pair<NameID, Nominal.FunctionOrMethod> p;
		
		if (expr.paramTypes != null) {
			ArrayList<Nominal> paramTypes = new ArrayList<Nominal>();
			for (SyntacticType t : expr.paramTypes) {
				paramTypes.add(resolver.resolveAsType(t, context));
			}
			// FIXME: clearly a bug here in the case of message reference
			p = (Pair<NameID, Nominal.FunctionOrMethod>) resolver.resolveAsFunctionOrMethod(
					expr.name, paramTypes, context);
		} else {
			p = resolver.resolveAsFunctionOrMethod(expr.name, context);
		}
		
		expr = new Expr.FunctionOrMethod(p.first(),expr.paramTypes,expr.attributes());
		expr.type = p.second();
		return expr;
	}
	
	private Expr propagate(Expr.Lambda expr,
			Environment environment, Context context) throws Exception {
		
		ArrayList<Type> rawTypes = new ArrayList<Type>();
		ArrayList<Type> nomTypes = new ArrayList<Type>();
		
		for(WhileyFile.Parameter p : expr.parameters) {
			Nominal n = resolver.resolveAsType(p.type,context);
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
				
		expr.body = propagate(expr.body,environment,context);

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
	
	private Expr propagate(Expr.AbstractIndirectInvoke expr,
			Environment environment, Context context) throws Exception {

		expr.src = propagate(expr.src, environment, context);
		Nominal type = expr.src.result();
		if (!(type instanceof Nominal.FunctionOrMethod)) {
			syntaxError("function or method type expected", context, expr.src);
		}

		Nominal.FunctionOrMethod funType = (Nominal.FunctionOrMethod) type;

		List<Nominal> paramTypes = funType.params();
		ArrayList<Expr> exprArgs = expr.arguments;
		for (int i = 0; i != exprArgs.size(); ++i) {
			Nominal pt = paramTypes.get(i);
			Expr arg = propagate(exprArgs.get(i), environment, context);			
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
	
	private Expr propagate(Expr.AbstractInvoke expr,
			Environment environment, Context context) throws Exception {
		
		// first, resolve through receiver and parameters.
		
		Expr receiver = expr.qualification;
		
		if(receiver != null) {
			receiver = propagate(receiver,environment,context);
			expr.qualification = receiver;						
		}
		
		ArrayList<Expr> exprArgs = expr.arguments;
		ArrayList<Nominal> paramTypes = new ArrayList<Nominal>();
		for(int i=0;i!=exprArgs.size();++i) {
			Expr arg = propagate(exprArgs.get(i),environment,context);
			exprArgs.set(i, arg);
			paramTypes.add(arg.result());			
		}
		
		// second, determine whether we already have a fully qualified name and
		// then lookup the appropriate function.
		
		if (receiver instanceof Expr.ModuleAccess) {
			// Yes, this function or method is qualified
			Expr.ModuleAccess ma = (Expr.ModuleAccess) receiver;
			NameID name = new NameID(ma.mid, expr.name);
			Nominal.FunctionOrMethod funType = resolver
					.resolveAsFunctionOrMethod(name, paramTypes, context);
			if (funType instanceof Nominal.Function) {
				Expr.FunctionCall r = new Expr.FunctionCall(name, ma, exprArgs,
						expr.attributes());
				r.functionType = (Nominal.Function) funType;
				return r;
			} else {
				Expr.MethodCall r = new Expr.MethodCall(name, ma, exprArgs,
						expr.attributes());
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
				
				Pair<NameID, Nominal.FunctionOrMethod> p = resolver.resolveAsFunctionOrMethod(
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
				Pair<NameID, Nominal.FunctionOrMethod> p = resolver.resolveAsFunctionOrMethod(expr.name, paramTypes, context);
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
	
	private Expr propagate(Expr.IndexOf expr, Environment environment,
			Context context) throws Exception {
		expr.src = propagate(expr.src, environment, context);
		expr.index = propagate(expr.index, environment, context);
		Nominal.EffectiveIndexible srcType = expandAsEffectiveMap(expr.src
				.result());

		if (srcType == null) {
			syntaxError(errorMessage(INVALID_SET_OR_LIST_EXPRESSION), context,
					expr.src);
		} else {
			expr.srcType = srcType;
		}

		checkIsSubtype(srcType.key(), expr.index, context);

		return expr;
	}
	
	private Expr propagate(Expr.LengthOf expr, Environment environment,
			Context context) throws Exception {			
		expr.src = propagate(expr.src,environment, context);			
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
	
	private Expr propagate(Expr.AbstractVariable expr,
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
				NameID nid = resolver.resolveAsName(expr.var, context);					
				Expr.ConstantAccess ca = new Expr.ConstantAccess(null, expr.var, nid,
						expr.attributes());
				ca.value = resolver.resolveAsConstant(nid);
				return ca;
			} catch (ResolveError err) {
			}
			// In this case, we may still be OK if this corresponds to an
			// explicit module or package access.
			try {
				Path.ID mid = resolver.resolveAsModule(expr.var, context);
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
	
	private Expr propagate(Expr.Set expr,
			Environment environment, Context context) {
		Nominal element = Nominal.T_VOID;		
		
		ArrayList<Expr> exprs = expr.arguments;
		for(int i=0;i!=exprs.size();++i) {
			Expr e = propagate(exprs.get(i),environment,context);
			Nominal t = e.result();
			exprs.set(i,e);
			element = Nominal.Union(t,element);			
		}
		
		expr.type = Nominal.Set(element,false);
		
		return expr;
	}
	
	private Expr propagate(Expr.List expr,
			Environment environment, Context context) {		
		Nominal element = Nominal.T_VOID;		
		
		ArrayList<Expr> exprs = expr.arguments;
		for(int i=0;i!=exprs.size();++i) {
			Expr e = propagate(exprs.get(i),environment,context);
			Nominal t = e.result();
			exprs.set(i,e);
			element = Nominal.Union(t,element);			
		}	
		
		expr.type = Nominal.List(element,false);
				
		return expr;
	}
	
	private Expr propagate(Expr.Map expr,
			Environment environment, Context context) {
		Nominal keyType = Nominal.T_VOID;
		Nominal valueType = Nominal.T_VOID;		
				
		ArrayList<Pair<Expr,Expr>> exprs = expr.pairs;
		for(int i=0;i!=exprs.size();++i) {
			Pair<Expr,Expr> p = exprs.get(i);
			Expr key = propagate(p.first(),environment,context);
			Expr value = propagate(p.second(),environment,context);
			Nominal kt = key.result();
			Nominal vt = value.result();
			exprs.set(i,new Pair<Expr,Expr>(key,value));
			
			keyType = Nominal.Union(kt,keyType);			
			valueType = Nominal.Union(vt,valueType);
		}
		
		expr.type = Nominal.Map(keyType,valueType);
		
		return expr;
	}
	
	private Expr propagate(Expr.Record expr,
			Environment environment, Context context) {
		
		HashMap<String,Expr> exprFields = expr.fields;
		HashMap<String,Nominal> fieldTypes = new HashMap<String,Nominal>();
				
		ArrayList<String> fields = new ArrayList<String>(exprFields.keySet());
		for(String field : fields) {
			Expr e = propagate(exprFields.get(field),environment,context);
			Nominal t = e.result();
			exprFields.put(field,e);
			fieldTypes.put(field,t);				
		}		
		
		expr.type = Nominal.Record(false,fieldTypes);
		
		return expr;
	}
	
	private Expr propagate(Expr.Tuple expr,
			Environment environment, Context context) {
		ArrayList<Expr> exprFields = expr.fields;
		ArrayList<Nominal> fieldTypes = new ArrayList<Nominal>();
				
		for(int i=0;i!=exprFields.size();++i) {
			Expr e = propagate(exprFields.get(i),environment,context);
			Nominal t = e.result();
			exprFields.set(i,e);
			fieldTypes.add(t);			
		}
				
		expr.type = Nominal.Tuple(fieldTypes);
		
		return expr;
	}
	
	private Expr propagate(Expr.SubList expr,
			Environment environment, Context context) throws Exception {	
		
		expr.src = propagate(expr.src,environment,context);
		expr.start = propagate(expr.start,environment,context);
		expr.end = propagate(expr.end,environment,context);
		
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
	
	private Expr propagate(Expr.SubString expr,
			Environment environment, Context context) throws Exception {	
		
		expr.src = propagate(expr.src,environment,context);
		expr.start = propagate(expr.start,environment,context);
		expr.end = propagate(expr.end,environment,context);
		
		checkIsSubtype(Type.T_STRING,expr.src,context);
		checkIsSubtype(Type.T_INT,expr.start,context);
		checkIsSubtype(Type.T_INT,expr.end,context);
		
		return expr;
	}
	
	private Expr propagate(Expr.AbstractDotAccess expr,
			Environment environment, Context context) throws Exception {	
				
		if (expr instanceof Expr.PackageAccess
				|| expr instanceof Expr.ModuleAccess) {			
			// don't need to do anything in these cases.
			return expr;
		}
		
		Expr src = expr.src;
		
		if(src != null) {
			src = propagate(expr.src,environment,context);
			expr.src = src;
		}
				
		if(expr instanceof Expr.FieldAccess) {			
			return propagate((Expr.FieldAccess)expr,environment,context);
		} else if(expr instanceof Expr.ConstantAccess) {
			return propagate((Expr.ConstantAccess)expr,environment,context);
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
				ca.value = resolver.resolveAsConstant(nid);
				return ca;
			}						
			syntaxError(errorMessage(INVALID_MODULE_ACCESS),context,expr);			
			return null; // deadcode
		} else {
			// must be a RecordAccess
			Expr.FieldAccess ra = new Expr.FieldAccess(src,expr.name,expr.attributes());			
			return propagate(ra,environment,context);
		}
	}
		
	private Expr propagate(Expr.FieldAccess ra,
			Environment environment, Context context) throws Exception {
		ra.src = propagate(ra.src,environment,context);
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
	
	private Expr propagate(Expr.ConstantAccess expr,
			Environment environment, Context context) throws Exception {
		// we don't need to do anything here, since the value is already
		// resolved by case for AbstractDotAccess.
		return expr;
	}			

	private Expr propagate(Expr.Dereference expr,
			Environment environment, Context context) throws Exception {
		Expr src = propagate(expr.src,environment,context);
		expr.src = src;
		Nominal.Reference srcType = expandAsReference(src.result());
		if(srcType == null) {
			syntaxError("invalid reference expression",context,src);
		}
		expr.srcType = srcType;		
		return expr;
	}
	
	private Expr propagate(Expr.New expr,
			Environment environment, Context context) {
		expr.expr = propagate(expr.expr,environment,context);
		expr.type = Nominal.Reference(expr.expr.result());
		return expr;
	}
	
	private Expr propagate(Expr.TypeVal expr,
			Environment environment, Context context) throws Exception {
		expr.type = resolver.resolveAsType(expr.unresolvedType, context); 
		return expr;
	}
	
	

	// =========================================================================
	// Constant Evaluation [this should not be located here?]
	// =========================================================================

	private Constant evaluate(Expr.UnOp bop, Constant v, Context context) {
		switch (bop.op) {
		case NOT:
			if (v instanceof Constant.Bool) {
				Constant.Bool b = (Constant.Bool) v;
				return Constant.V_BOOL(!b.value);
			}
			syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context, bop);
			break;
		case NEG:
			if (v instanceof Constant.Integer) {
				Constant.Integer b = (Constant.Integer) v;
				return Constant.V_INTEGER(b.value.negate());
			} else if (v instanceof Constant.Decimal) {
				Constant.Decimal b = (Constant.Decimal) v;
				return Constant.V_DECIMAL(b.value.negate());
			}
			syntaxError(errorMessage(INVALID_NUMERIC_EXPRESSION), context, bop);
			break;
		case INVERT:
			if (v instanceof Constant.Byte) {
				Constant.Byte b = (Constant.Byte) v;
				return Constant.V_BYTE((byte) ~b.value);
			}
			break;
		}
		syntaxError(errorMessage(INVALID_UNARY_EXPRESSION), context, bop);
		return null;
	}

	private Constant evaluate(Expr.BinOp bop, Constant v1, Constant v2,
			Context context) {
		Type v1_type = v1.type();
		Type v2_type = v2.type();
		Type lub = Type.Union(v1_type, v2_type);

		// FIXME: there are bugs here related to coercions.

		if (Type.isSubtype(Type.T_BOOL, lub)) {
			return evaluateBoolean(bop, (Constant.Bool) v1, (Constant.Bool) v2,
					context);
		} else if (Type.isSubtype(Type.T_INT, lub)) {
			return evaluate(bop, (Constant.Integer) v1, (Constant.Integer) v2,
					context);
		} else if (Type.isImplicitCoerciveSubtype(Type.T_REAL, v1_type)
				&& Type.isImplicitCoerciveSubtype(Type.T_REAL, v1_type)) {
			if (v1 instanceof Constant.Integer) {
				Constant.Integer i1 = (Constant.Integer) v1;
				v1 = Constant.V_DECIMAL(new BigDecimal(i1.value));
			} else if (v2 instanceof Constant.Integer) {
				Constant.Integer i2 = (Constant.Integer) v2;
				v2 = Constant.V_DECIMAL(new BigDecimal(i2.value));
			}
			return evaluate(bop, (Constant.Decimal) v1, (Constant.Decimal) v2,
					context);
		} else if (Type.isSubtype(Type.T_LIST_ANY, lub)) {
			return evaluate(bop, (Constant.List) v1, (Constant.List) v2,
					context);
		} else if (Type.isSubtype(Type.T_SET_ANY, lub)) {
			return evaluate(bop, (Constant.Set) v1, (Constant.Set) v2, context);
		}
		syntaxError(errorMessage(INVALID_BINARY_EXPRESSION), context, bop);
		return null;
	}

	private Constant evaluateBoolean(Expr.BinOp bop, Constant.Bool v1,
			Constant.Bool v2, Context context) {
		switch (bop.op) {
		case AND:
			return Constant.V_BOOL(v1.value & v2.value);
		case OR:
			return Constant.V_BOOL(v1.value | v2.value);
		case XOR:
			return Constant.V_BOOL(v1.value ^ v2.value);
		}
		syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context, bop);
		return null;
	}

	private Constant evaluate(Expr.BinOp bop, Constant.Integer v1,
			Constant.Integer v2, Context context) {
		switch (bop.op) {
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
		syntaxError(errorMessage(INVALID_NUMERIC_EXPRESSION), context, bop);
		return null;
	}

	private Constant evaluate(Expr.BinOp bop, Constant.Decimal v1,
			Constant.Decimal v2, Context context) {
		switch (bop.op) {
		case ADD:
			return Constant.V_DECIMAL(v1.value.add(v2.value));
		case SUB:
			return Constant.V_DECIMAL(v1.value.subtract(v2.value));
		case MUL:
			return Constant.V_DECIMAL(v1.value.multiply(v2.value));
		case DIV:
			return Constant.V_DECIMAL(v1.value.divide(v2.value));
		}
		syntaxError(errorMessage(INVALID_NUMERIC_EXPRESSION), context, bop);
		return null;
	}

	private Constant evaluate(Expr.BinOp bop, Constant.List v1,
			Constant.List v2, Context context) {
		switch (bop.op) {
		case ADD:
			ArrayList<Constant> vals = new ArrayList<Constant>(v1.values);
			vals.addAll(v2.values);
			return Constant.V_LIST(vals);
		}
		syntaxError(errorMessage(INVALID_LIST_EXPRESSION), context, bop);
		return null;
	}

	private Constant evaluate(Expr.BinOp bop, Constant.Set v1, Constant.Set v2,
			Context context) {
		switch (bop.op) {
		case UNION: {
			HashSet<Constant> vals = new HashSet<Constant>(v1.values);
			vals.addAll(v2.values);
			return Constant.V_SET(vals);
		}
		case INTERSECTION: {
			HashSet<Constant> vals = new HashSet<Constant>();
			for (Constant v : v1.values) {
				if (v2.values.contains(v)) {
					vals.add(v);
				}
			}
			return Constant.V_SET(vals);
		}
		case SUB: {
			HashSet<Constant> vals = new HashSet<Constant>();
			for (Constant v : v1.values) {
				if (!v2.values.contains(v)) {
					vals.add(v);
				}
			}
			return Constant.V_SET(vals);
		}
		}
		syntaxError(errorMessage(INVALID_SET_EXPRESSION), context, bop);
		return null;
	}

	// =========================================================================
	// expandAsType
	// =========================================================================

	public Nominal.EffectiveSet expandAsEffectiveSet(Nominal lhs)
			throws Exception {
		Type raw = lhs.raw();
		if (raw instanceof Type.EffectiveSet) {
			Type nominal = expandOneLevel(lhs.nominal());
			if (!(nominal instanceof Type.EffectiveSet)) {
				nominal = raw; // discard nominal information
			}
			return (Nominal.EffectiveSet) Nominal.construct(nominal, raw);
		} else {
			return null;
		}
	}

	public Nominal.EffectiveList expandAsEffectiveList(Nominal lhs)
			throws Exception {
		Type raw = lhs.raw();
		if (raw instanceof Type.EffectiveList) {
			Type nominal = expandOneLevel(lhs.nominal());
			if (!(nominal instanceof Type.EffectiveList)) {
				nominal = raw; // discard nominal information
			}
			return (Nominal.EffectiveList) Nominal.construct(nominal, raw);
		} else {
			return null;
		}
	}

	public Nominal.EffectiveCollection expandAsEffectiveCollection(Nominal lhs)
			throws Exception {
		Type raw = lhs.raw();
		if (raw instanceof Type.EffectiveCollection) {
			Type nominal = expandOneLevel(lhs.nominal());
			if (!(nominal instanceof Type.EffectiveCollection)) {
				nominal = raw; // discard nominal information
			}
			return (Nominal.EffectiveCollection) Nominal
					.construct(nominal, raw);
		} else {
			return null;
		}
	}

	public Nominal.EffectiveIndexible expandAsEffectiveMap(Nominal lhs)
			throws Exception {
		Type raw = lhs.raw();
		if (raw instanceof Type.EffectiveIndexible) {
			Type nominal = expandOneLevel(lhs.nominal());
			if (!(nominal instanceof Type.EffectiveIndexible)) {
				nominal = raw; // discard nominal information
			}
			return (Nominal.EffectiveIndexible) Nominal.construct(nominal, raw);
		} else {
			return null;
		}
	}

	public Nominal.EffectiveMap expandAsEffectiveDictionary(Nominal lhs)
			throws Exception {
		Type raw = lhs.raw();
		if (raw instanceof Type.EffectiveMap) {
			Type nominal = expandOneLevel(lhs.nominal());
			if (!(nominal instanceof Type.EffectiveMap)) {
				nominal = raw; // discard nominal information
			}
			return (Nominal.EffectiveMap) Nominal.construct(nominal, raw);
		} else {
			return null;
		}
	}

	public Nominal.EffectiveRecord expandAsEffectiveRecord(Nominal lhs)
			throws Exception {
		Type raw = lhs.raw();

		if (raw instanceof Type.Record) {
			Type nominal = expandOneLevel(lhs.nominal());
			if (!(nominal instanceof Type.Record)) {
				nominal = (Type) raw; // discard nominal information
			}
			return (Nominal.Record) Nominal.construct(nominal, raw);
		} else if (raw instanceof Type.UnionOfRecords) {
			Type nominal = expandOneLevel(lhs.nominal());
			if (!(nominal instanceof Type.UnionOfRecords)) {
				nominal = (Type) raw; // discard nominal information
			}
			return (Nominal.UnionOfRecords) Nominal.construct(nominal, raw);
		}
		{
			return null;
		}
	}

	public Nominal.EffectiveTuple expandAsEffectiveTuple(Nominal lhs)
			throws Exception {
		Type raw = lhs.raw();
		if (raw instanceof Type.EffectiveTuple) {
			Type nominal = expandOneLevel(lhs.nominal());
			if (!(nominal instanceof Type.EffectiveTuple)) {
				nominal = raw; // discard nominal information
			}
			return (Nominal.EffectiveTuple) Nominal.construct(nominal, raw);
		} else {
			return null;
		}
	}

	public Nominal.Reference expandAsReference(Nominal lhs) throws Exception {
		Type.Reference raw = Type.effectiveReference(lhs.raw());
		if (raw != null) {
			Type nominal = expandOneLevel(lhs.nominal());
			if (!(nominal instanceof Type.Reference)) {
				nominal = raw; // discard nominal information
			}
			return (Nominal.Reference) Nominal.construct(nominal, raw);
		} else {
			return null;
		}
	}

	public Nominal.FunctionOrMethod expandAsFunctionOrMethod(Nominal lhs)
			throws Exception {
		Type.FunctionOrMethod raw = Type.effectiveFunctionOrMethod(lhs.raw());
		if (raw != null) {
			Type nominal = expandOneLevel(lhs.nominal());
			if (!(nominal instanceof Type.FunctionOrMethod)) {
				nominal = raw; // discard nominal information
			}
			return (Nominal.FunctionOrMethod) Nominal.construct(nominal, raw);
		} else {
			return null;
		}
	}

	private Type expandOneLevel(Type type) throws Exception {
		if (type instanceof Type.Nominal) {
			Type.Nominal nt = (Type.Nominal) type;
			NameID nid = nt.name();
			Path.ID mid = nid.module();

			WhileyFile wf = builder.getSourceFile(mid);
			Type r = null;

			if (wf != null) {
				WhileyFile.Declaration decl = wf.declaration(nid.name());
				if (decl instanceof WhileyFile.Type) {
					WhileyFile.Type td = (WhileyFile.Type) decl;
					r = resolver
							.resolveAsType(td.pattern.toSyntacticType(), td)
							.nominal();
				}
			} else {
				WyilFile m = builder.getModule(mid);
				WyilFile.TypeDeclaration td = m.type(nid.name());
				if (td != null) {
					r = td.type();
				}
			}
			if (r == null) {
				throw new ResolveError("unable to locate " + nid);
			}
			return expandOneLevel(r);
		} else if (type instanceof Type.Leaf || type instanceof Type.Reference
				|| type instanceof Type.Tuple || type instanceof Type.Set
				|| type instanceof Type.List || type instanceof Type.Map
				|| type instanceof Type.Record
				|| type instanceof Type.FunctionOrMethod
				|| type instanceof Type.Negation) {
			return type;
		} else {
			Type.Union ut = (Type.Union) type;
			ArrayList<Type> bounds = new ArrayList<Type>();
			for (Type b : ut.bounds()) {
				bounds.add(expandOneLevel(b));
			}
			return Type.Union(bounds);
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

	// ==========================================================================
	// Environment Class
	// ==========================================================================

	/**
	 * <p>
	 * Responsible for mapping source-level variables to their declared and
	 * actual types, at any given program point. Since the flow-type checker
	 * uses a flow-sensitive approach to type checking, then the typing
	 * environment will change as we move through the statements of a function
	 * or method.
	 * </p>
	 * 
	 * <p>
	 * This class is implemented in a functional style to minimise possible
	 * problems related to aliasing (which have been a problem in the past). To
	 * improve performance, reference counting is to ensure that cloning the
	 * underling map is only performed when actually necessary.
	 * </p>
	 * 
	 * @author David J. Pearce
	 * 
	 */
	private static final class Environment {

		/**
		 * The underlying collection for this environment.
		 */
		private final HashMap<String, Nominal> map;

		/**
		 * The reference count, which indicate how many references to this
		 * environment there are. When there is only one reference, then the put
		 * and putAll operations will perform an "inplace" update (i.e. without
		 * cloning the underlying collection).
		 */
		private int count; // refCount

		/**
		 * Construct an empty environment. Initially the reference count is 1.
		 */
		public Environment() {
			count = 1;
			map = new HashMap<String, Nominal>();
		}

		/**
		 * Construct a fresh environment as a copy of another map. Initially the
		 * reference count is 1.
		 */
		private Environment(HashMap<String, Nominal> types) {
			count = 1;
			this.map = (HashMap<String, Nominal>) types.clone();
		}

		/**
		 * Get the type associated with a given variable, or null if that
		 * variable is not declared.
		 * 
		 * @param variable
		 *            Variable to return type for.
		 * @return
		 */
		public Nominal get(String variable) {
			return map.get(variable);
		}

		/**
		 * Check whether a given variable is declared within this environment.
		 * 
		 * @param variable
		 * @return
		 */
		public boolean containsKey(String variable) {
			return map.containsKey(variable);
		}

		/**
		 * Return the set of declared variables in this environment (a.k.a the
		 * domain).
		 * 
		 * @return
		 */
		public Set<String> keySet() {
			return map.keySet();
		}

		/**
		 * Associate a type with a given variable. If that variable already had
		 * a type, then this is overwritten. In the case that this environment
		 * has a reference count of 1, then an "in place" update is performed.
		 * Otherwise, a fresh copy of this environment is returned with the
		 * given variable associated with the given type, whilst this
		 * environment is unchanged.
		 * 
		 * @param variable
		 *            Name of variable to be associated with given type
		 * @param type
		 *            Type to associated with given variable
		 * @return An updated version of the environment which contains the new
		 *         association.
		 */
		public Environment put(String variable, Nominal type) {
			if (count == 1) {
				map.put(variable, type);
				return this;
			} else {
				Environment nenv = new Environment(map);
				nenv.map.put(variable, type);
				count--;
				return nenv;
			}
		}

		/**
		 * Copy all variable-type associations from the given environment into
		 * this environment. The type of any variable already associated with a
		 * type is overwritten. In the case that this environment has a
		 * reference count of 1, then an "in place" update is performed.
		 * Otherwise, a fresh copy of this environment is returned with the
		 * given variables associated with the given types, whilst this
		 * environment is unchanged.
		 * 
		 * @param variable
		 *            Name of variable to be associated with given type
		 * @param type
		 *            Type to associated with given variable
		 * @return An updated version of the environment which contains all the
		 *         associations from the given environment.
		 */
		public Environment putAll(Environment env) {
			if (count == 1) {
				HashMap<String, Nominal> envTypes = env.map;
				map.putAll(envTypes);
				return this;
			} else {
				Environment nenv = new Environment(map);
				HashMap<String, Nominal> envTypes = env.map;
				nenv.map.putAll(envTypes);
				count--;
				return nenv;
			}
		}

		/**
		 * Remove a variable and any associated type from this environment. In
		 * the case that this environment has a reference count of 1, then an
		 * "in place" update is performed. Otherwise, a fresh copy of this
		 * environment is returned with the given variable and any association
		 * removed.
		 * 
		 * @param variable
		 *            Name of variable to be removed from the environment
		 * @return An updated version of the environment in which the given
		 *         variable no longer exists.
		 */
		public Environment remove(String key) {
			if (count == 1) {
				map.remove(key);
				return this;
			} else {
				Environment nenv = new Environment(map);
				nenv.map.remove(key);
				count--;
				return nenv;
			}
		}

		/**
		 * Create a fresh copy of this environment. In fact, this operation
		 * simply increments the reference count of this environment and returns
		 * it.
		 */
		public Environment clone() {
			count++;
			return this;
		}

		/**
		 * Decrease the reference count of this environment by one.
		 */
		public void free() {
			--count;
		}

		public String toString() {
			return map.toString();
		}

		public int hashCode() {
			return map.hashCode();
		}

		public boolean equals(Object o) {
			if (o instanceof Environment) {
				Environment r = (Environment) o;
				return map.equals(r.map);
			}
			return false;
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
