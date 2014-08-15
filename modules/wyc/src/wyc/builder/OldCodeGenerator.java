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

package wyc.builder;

import java.util.*;

import static wyc.lang.WhileyFile.internalFailure;
import static wyc.lang.WhileyFile.syntaxError;
import static wyil.util.ErrorMessages.*;
import wyc.lang.*;
import wyc.lang.Stmt.*;
import wyc.lang.WhileyFile.Context;
import wycc.lang.Attribute;
import wycc.lang.NameID;
import wycc.lang.SyntacticElement;
import wycc.lang.SyntaxError;
import wycc.util.Pair;
import wycc.util.ResolveError;
import wycc.util.Triple;
import wyfs.lang.Path;
import wyil.lang.*;

/**
 * <p>
 * Responsible for compiling the declarations, statements and expression found
 * in a WhileyFile into WyIL declarations and bytecode blocks. For example:
 * </p>
 * 
 * <pre>
 * type nat is (int x) where x >= 0
 * 
 * int f(nat x):
 *    return x-1
 * </pre>
 * 
 * <p>
 * The code generator is responsible for generating the code for the constraint
 * on <code>nat</code>, as well as compiling the function's statements into
 * their corresponding WyIL bytecodes. For example, the code generated
 * constraint on type <code>nat</code> would look like this:
 * </p>
 * 
 * <pre>
 * type nat is int
 * where:
 *     load x
 *     const 0
 *     ifge goto exit
 *     fail("type constraint not satisfied")
 *  .exit:
 * </pre>
 * 
 * This WyIL bytecode simply compares the local variable x against 0. Here, x
 * represents the value held in a variable of type <code>nat</code>. If the
 * constraint fails, then the given message is printed.
 * 
 * @author David J. Pearce
 * 
 */
public final class OldCodeGenerator {
	
	/**
	 * The builder is needed to provide access to external resources (i.e.
	 * external WyIL files compiled separately). This is required for expanding
	 * types and their constraints in certain situations, such as runtime type
	 * tests (e.g. <code>x is T</code> where <code>T</code> is defined
	 * externally).
	 */
	private final WhileyBuilder builder;			
	
	/**
	 * The type checker provides access to the pool of resolved types.
	 */
	private final FlowTypeChecker resolver;	
	
	/**
	 * The lambdas are anonymous functions used within statements and
	 * expressions in the source file. These are compiled into anonymised WyIL
	 * functions, since WyIL does not have an internal notion of a lambda.
	 */
	private final ArrayList<WyilFile.FunctionOrMethodDeclaration> lambdas = new ArrayList<WyilFile.FunctionOrMethodDeclaration>();
	
	/**
	 * The scopes stack is used for determining the correct scoping for continue
	 * and break statements. Whenever we begin translating a loop of some kind,
	 * a <code>LoopScope</code> is pushed on the stack. Once the translation of
	 * that loop is complete, this is then popped off the stack.
	 */
	private Stack<Scope> scopes = new Stack<Scope>();
		
	/**
	 * The name cache stores the translations of any code associated with a
	 * named type or constant, which was previously computed.
	 */
	private final HashMap<NameID,Code.Block> cache = new HashMap<NameID,Code.Block>();

	/**
	 * Construct a code generator object for translating WhileyFiles into
	 * WyilFiles.
	 * 
	 * @param builder
	 *            The enclosing builder instance which provides access to the
	 *            global namespace.
	 * @param resolver
	 *            The relevant type checker instance which provides access to
	 *            the pool of previously determined types.
	 */
	public OldCodeGenerator(WhileyBuilder builder, FlowTypeChecker resolver) {
		this.builder = builder;		
		this.resolver = resolver;
	}

	// =========================================================================
	// WhileyFile
	// =========================================================================		
	
	/**
	 * Generate a WyilFile from a given WhileyFile by translating all of the
	 * declarations, statements and expressions into WyIL declarations and
	 * bytecode blocks.
	 * 
	 * @param wf
	 *            The WhileyFile to be translated.
	 * @return
	 */
	public WyilFile generate(WhileyFile wf) {		
		ArrayList<WyilFile.Block> declarations = new ArrayList<WyilFile.Block>();

		// Go through each declaration and translate in the order of appearance.
		for (WhileyFile.Declaration d : wf.declarations) {
			try {
				if (d instanceof WhileyFile.Type) {
					declarations.add(generate((WhileyFile.Type) d));
				} else if (d instanceof WhileyFile.Constant) {
					declarations.add(generate((WhileyFile.Constant) d));
				} else if (d instanceof WhileyFile.FunctionOrMethod) {
					declarations
							.addAll(generate((WhileyFile.FunctionOrMethod) d));
				}
			} catch (SyntaxError se) {
				throw se;
			} catch (Throwable ex) {
				WhileyFile.internalFailure(ex.getMessage(),
						(WhileyFile.Context) d, d, ex);
			}
		}
		
		// Add any lambda functions which were used within some expression. Each
		// of these is guaranteed to have been given a unique and valid WyIL
		// name.
		declarations.addAll(lambdas);
		
		// Done
		return new WyilFile(wf.module, wf.filename, declarations);				
	}

	// =========================================================================
	// Constant Declarations
	// =========================================================================		

	/**
	 * Generate a WyilFile constant declaration from a WhileyFile constant
	 * declaration. This requires evaluating the given expression to produce a
	 * constant value. If this cannot be done, then a syntax error is raised to
	 * indicate an invalid constant declaration was encountered.
	 */
	private WyilFile.ConstantDeclaration generate(WhileyFile.Constant cd) {
		// TODO: this the point where were should an evaluator
		return new WyilFile.ConstantDeclaration(cd.modifiers(), cd.name(), cd.resolvedValue);
	}

	// =========================================================================
	// Type Declarations
	// =========================================================================		
	
	/**
	 * Generate a WyilFile type declaration from a WhileyFile type declaration.
	 * If a type invariant is given, then this will need to be translated into
	 * Wyil bytecode.
	 * 
	 * @param td
	 * @return
	 * @throws Exception
	 */
	private WyilFile.TypeDeclaration generate(WhileyFile.Type td) throws Exception {		
		Code.Block invariant = null;
		
		if(td.invariant != null) {								
			NameID nid = new NameID(td.file().module,td.name());
			invariant = generate(nid);			
		}
		
		return new WyilFile.TypeDeclaration(td.modifiers(), td.name(),
				td.resolvedType.raw(), invariant);
	}

	public Code.Block generate(NameID nid) throws Exception {
		Code.Block blk = cache.get(nid);
		if(blk == EMPTY_BLOCK) {
			return null;
		} else if(blk != null) {
			return blk;
		}
		
		// check whether the item in question is in one of the source
		// files being compiled.
		Path.ID mid = nid.module();
		WhileyFile wf = builder.getSourceFile(mid);
		if(wf != null) {
			// FIXME: the following line is necessary to terminate infinite
			// recursion. However, we really need to do better in the
			// context of recursive types with constraints.
	
			WhileyFile.Type td = wf.typeDecl(nid.name());
			if(td != null) {
				cache.put(nid, EMPTY_BLOCK);
				blk = generate(td.pattern.toSyntacticType(),td);
				if(td.invariant != null) {			
					if(blk == null) {
						blk = new Code.Block(1);					
					}
					Environment environment = new Environment();
					int root = environment.allocate(td.resolvedType.raw());
					addDeclaredVariables(root, td.pattern,
							td.resolvedType.raw(), environment, blk);
					generateAssertion("constraint not satisfied",
							td.invariant, false, environment, blk, td);
				}
				cache.put(nid, blk);
				return blk;
			} else {
				Constant v = resolver.resolveAsConstant(nid);				
				if(v instanceof Constant.Set) {
					Constant.Set vs = (Constant.Set) v;
					Type.Set type = vs.type();
					blk = new Code.Block(1);					
					blk.add(Codes.Const(Codes.REG_1, v));
					blk.add(Codes.Assert(vs.type(), Codes.REG_0, Codes.REG_1,
							Codes.Comparator.IN, "constraint not satisfied"));
					cache.put(nid, blk);
					return blk;
				} 
			}			
		} else {
			// now check whether it's already compiled and available on the
			// WHILEYPATH.
			WyilFile m = builder.getModule(mid);
			WyilFile.TypeDeclaration td = m.type(nid.name());
			if(td != null && td.invariant() != null) {
				// should I cache this?
				return td.invariant();
			} else {
				return null;
			}
		}
		
		// FIXME: better error message?
		throw new ResolveError("name not found: " + nid);
	}
	
	public Code.Block generate(SyntacticType t, Context context) throws Exception {
		Nominal nt = resolver.resolveAsType(t, context);
		Type raw = nt.raw();
		if (t instanceof SyntacticType.List) {
			SyntacticType.List lt = (SyntacticType.List) t;
			Code.Block blk = generate(lt.element, context);			
			if (blk != null) {
				Code.Block nblk = new Code.Block(1);
				String label = CodeUtils.freshLabel();
				nblk.add(Codes.ForAll((Type.EffectiveCollection) raw,
						Codes.REG_0, Codes.REG_1, Collections.EMPTY_LIST, label),
						t.attributes());
				nblk.addAll(shiftBlock(1, blk));
				// Must add NOP before loop end to ensure labels at the boundary
				// get written into Wyil files properly. See Issue #253.
				nblk.add(Codes.Nop);
				nblk.add(Codes.LoopEnd(label));
				blk = nblk;
			}
			return blk;
		} else if (t instanceof SyntacticType.Set) {
			SyntacticType.Set st = (SyntacticType.Set) t;
			Code.Block blk = generate(st.element, context);
			if (blk != null) {
				Code.Block nblk = new Code.Block(1);
				String label = CodeUtils.freshLabel();
				nblk.add(Codes.ForAll((Type.EffectiveCollection) raw,
						Codes.REG_0, Codes.REG_1, Collections.EMPTY_LIST, label),
						t.attributes());
				nblk.addAll(shiftBlock(1, blk));
				// Must add NOP before loop end to ensure labels at the boundary
				// get written into Wyil files properly. See Issue #253.
				nblk.add(Codes.Nop);
				nblk.add(Codes.LoopEnd(label));
				blk = nblk;
			}
			return blk;
		} else if (t instanceof SyntacticType.Map) {
			SyntacticType.Map st = (SyntacticType.Map) t;
			Code.Block blk = null;
			// FIXME: put in constraints. REQUIRES ITERATION OVER DICTIONARIES
			Code.Block key = generate(st.key, context);
			Code.Block value = generate(st.value, context);
			return blk;
		} else if (t instanceof SyntacticType.Tuple) {
			// At the moment, a tuple is compiled down to a WyIL record.
			SyntacticType.Tuple tt = (SyntacticType.Tuple) t;
			Type.EffectiveTuple ett = (Type.EffectiveTuple) raw;
			List<Type> ettElements = ett.elements();
			Code.Block blk = null;
			
			int i = 0;
			for (SyntacticType e : tt.types) {
				Code.Block p = generate(e, context);
				if (p != null) {
					if (blk == null) {
						blk = new Code.Block(1);
					}
					blk.add(Codes.TupleLoad(ett, Codes.REG_1, Codes.REG_0, i),
							t.attributes());
					blk.addAll(shiftBlock(1, p));
				}
				i = i + 1;
			}

			return blk;
		} else if (t instanceof SyntacticType.Record) {
			SyntacticType.Record tt = (SyntacticType.Record) t;
			Type.EffectiveRecord ert = (Type.EffectiveRecord) raw;
			Map<String,Type> fields = ert.fields();
			Code.Block blk = null;			
			for (Map.Entry<String, SyntacticType> e : tt.types.entrySet()) {
				Code.Block p = generate(e.getValue(), context);
				if (p != null) {
					if (blk == null) {
						blk = new Code.Block(1);
					}					
					blk.add(
							Codes.FieldLoad(ert, Codes.REG_1, Codes.REG_0,
									e.getKey()), t.attributes());
					blk.addAll(shiftBlock(1, p));
				}
			}
			return blk;
		} else if (t instanceof SyntacticType.Union) {
			SyntacticType.Union ut = (SyntacticType.Union) t;			
			
			boolean constraints = false;
			DecisionTree tree = new DecisionTree(raw);
			
			for (SyntacticType b : ut.bounds) {
				Type type = resolver.resolveAsType(b, context).raw();
				Code.Block constraint = generate(b, context);
				constraints |= constraint != null;
				tree.add(type,constraint);
			}
			
			if(constraints) {
				return tree.flattern();				
			} else {
				// no constraints, must not do anything!
				return null;
			}
		} else if (t instanceof SyntacticType.Negation) {
			SyntacticType.Negation st = (SyntacticType.Negation) t;
			Code.Block p = generate(st.element, context);
			Code.Block blk = null;
			// TODO: need to fix not constraints
			return blk;
		} else if (t instanceof SyntacticType.Intersection) {
			SyntacticType.Intersection ut = (SyntacticType.Intersection) t;
			Code.Block blk = null;			
			for (int i = 0; i != ut.bounds.size(); ++i) {
				SyntacticType b = ut.bounds.get(i);
				Code.Block p = generate(b, context);
				// TODO: add intersection constraints				
			}
			return blk;
		} else if (t instanceof SyntacticType.Reference) {
			SyntacticType.Reference ut = (SyntacticType.Reference) t;			
			Code.Block blk = generate(ut.element, context);
			// TODO: fix process constraints
			return null;
		} else if (t instanceof SyntacticType.Nominal) {
			SyntacticType.Nominal dt = (SyntacticType.Nominal) t;
			
			try {
				NameID nid = resolver.resolveAsName(dt.names,context);				
				Code.Block other = generate(nid);
				if(other != null) {
					Code.Block blk = new Code.Block(1);
					blk.addAll(other);
					return blk;
				} else {
					return null;
				}
			} catch (ResolveError rex) {
				syntaxError(rex.getMessage(), context, t, rex);
				return null;
			}
		} else {
			// for base cases
			return null;
		}
	}
			
	// =========================================================================
	// Function / Method Declarations
	// =========================================================================		
	
	private List<WyilFile.FunctionOrMethodDeclaration> generate(WhileyFile.FunctionOrMethod fd) throws Exception {		
		Type.FunctionOrMethod ftype = fd.resolvedType().raw();		
		Environment environment = new Environment();
		
		// method return type		
		int paramIndex = 0;
		int nparams = fd.parameters.size();
		
		// ==================================================================
		// Generate pre-condition
		// ==================================================================
		
		Code.Block precondition = null;
		for (WhileyFile.Parameter p : fd.parameters) {
			// First, generate and inline any constraints associated with the
			// type.
			// Now, map the parameter to its index

			Code.Block constraint = generate(p.type, p);
			if (constraint != null) {
				if (precondition == null) {
					precondition = new Code.Block(nparams);
				}
				constraint = shiftBlockExceptionZero(nparams, paramIndex,
						constraint);
				precondition.addAll(constraint);
			}
			environment.allocate(ftype.params().get(paramIndex++),p.name());
		}
		
		// Resolve pre- and post-condition								
		
		for (Expr condition : fd.requires) {
			if (precondition == null) {
				precondition = new Code.Block(nparams);
			}
			generateAssertion("precondition not satisfied",
					condition, false, environment, precondition, fd);
		}
		
		// ==================================================================
		// Generate post-condition
		// ==================================================================
		Code.Block postcondition = generate(fd.ret.toSyntacticType(),fd);								
		if (fd.ensures.size() > 0) {
			Environment postEnv = new Environment();
			int root = postEnv.allocate(fd.resolvedType().ret().raw(), "$");

			paramIndex = 0;
			for (WhileyFile.Parameter p : fd.parameters) {
				postEnv.allocate(ftype.params().get(paramIndex), p.name());
				paramIndex++;
			}
			postcondition = new Code.Block(postEnv.size());
			addDeclaredVariables(root, fd.ret, fd.resolvedType().ret().raw(),
					postEnv, postcondition);

			for (Expr condition : fd.ensures) {
				generateAssertion("postcondition not satisfied",
						condition, false, postEnv, postcondition, fd);
			}
		}

		// ==================================================================
		// Generate body
		// ==================================================================
			
		Code.Block body = new Code.Block(fd.parameters.size());		
		for (Stmt s : fd.statements) {
			generate(s, environment, body, fd);
		}		
		
		// The following is sneaky. It guarantees that every method ends in a
		// return. For methods that actually need a value, this is either
		// removed as dead-code or remains and will cause an error.
		body.add(Codes.Return(),attributes(fd));				
		
		List<WyilFile.Case> ncases = new ArrayList<WyilFile.Case>();				

		ncases.add(new WyilFile.Case(body,precondition,postcondition));
		ArrayList<WyilFile.FunctionOrMethodDeclaration> declarations = new ArrayList(); 
		
		if (fd instanceof WhileyFile.Function) {
			WhileyFile.Function f = (WhileyFile.Function) fd;
			declarations.add(new WyilFile.FunctionOrMethodDeclaration(fd
					.modifiers(), fd.name(), f.resolvedType.raw(), ncases));
		} else {
			WhileyFile.Method md = (WhileyFile.Method) fd;
			declarations.add(new WyilFile.FunctionOrMethodDeclaration(fd
					.modifiers(), fd.name(), md.resolvedType.raw(), ncases));
		}
		
		return declarations;
	}

	// =========================================================================
	// Statements
	// =========================================================================		
	
	/**
	 * Translate a source-level statement into a WyIL block, using a given
	 * environment mapping named variables to slots.
	 * 
	 * @param stmt
	 *            --- statement to be translated.
	 * @param environment
	 *            --- mapping from variable names to to slot numbers.
	 * @return
	 */
	private void generate(Stmt stmt, Environment environment, Code.Block codes, Context context) {
		try {
			if (stmt instanceof VariableDeclaration) {
				generate((VariableDeclaration) stmt, environment, codes, context);
			} else if (stmt instanceof Assign) {
				generate((Assign) stmt, environment, codes, context);
			} else if (stmt instanceof Assert) {
				generate((Assert) stmt, environment, codes, context);
			} else if (stmt instanceof Assume) {
				generate((Assume) stmt, environment, codes, context);
			} else if (stmt instanceof Return) {
				generate((Return) stmt, environment, codes, context);
			} else if (stmt instanceof Debug) {
				generate((Debug) stmt, environment, codes, context);
			} else if (stmt instanceof IfElse) {
				generate((IfElse) stmt, environment, codes, context);
			} else if (stmt instanceof Switch) {
				generate((Switch) stmt, environment, codes, context);
			} else if (stmt instanceof TryCatch) {
				generate((TryCatch) stmt, environment, codes, context);
			} else if (stmt instanceof Break) {
				generate((Break) stmt, environment, codes, context);
			} else if (stmt instanceof Throw) {
				generate((Throw) stmt, environment, codes, context);
			} else if (stmt instanceof While) {
				generate((While) stmt, environment, codes, context);
			} else if (stmt instanceof DoWhile) {
				generate((DoWhile) stmt, environment, codes, context);
			} else if (stmt instanceof ForAll) {
				generate((ForAll) stmt, environment, codes, context);
			} else if (stmt instanceof Expr.MethodCall) {
				generate((Expr.MethodCall) stmt, Codes.NULL_REG, environment, codes, context);								
			} else if (stmt instanceof Expr.FunctionCall) {
				generate((Expr.FunctionCall) stmt, Codes.NULL_REG, environment, codes, context);								
			} else if (stmt instanceof Expr.IndirectMethodCall) {
				generate((Expr.IndirectMethodCall) stmt, Codes.NULL_REG, environment, codes, context);								
			} else if (stmt instanceof Expr.IndirectFunctionCall) {
				generate((Expr.IndirectFunctionCall) stmt, Codes.NULL_REG, environment, codes, context);								
			} else if (stmt instanceof Expr.New) {
				generate((Expr.New) stmt, environment, codes, context);
			} else if (stmt instanceof Skip) {
				generate((Skip) stmt, environment, codes, context);
			} else {
				// should be dead-code
				WhileyFile.internalFailure("unknown statement: "
						+ stmt.getClass().getName(), context, stmt);
			}
		} catch (ResolveError rex) {
			WhileyFile.syntaxError(rex.getMessage(), context, stmt, rex);
		} catch (SyntaxError sex) {
			throw sex;
		} catch (Exception ex) {			
			WhileyFile.internalFailure(ex.getMessage(), context, stmt, ex);
		}
	}
	
	private void generate(VariableDeclaration s, Environment environment, Code.Block codes, Context context) {
		// First, we allocate this variable to a given slot in the environment.
		int root = environment.allocate(s.type.raw());

		// Second, translate initialiser expression if it exists.
		if(s.expr != null) {
			int operand = generate(s.expr, environment, codes, context);						
			codes.add(Codes.Assign(s.expr.result().raw(), root, operand),
					attributes(s));
			addDeclaredVariables(root, s.pattern, s.type.raw(), environment, codes);			
		} else {
			// The following is a little sneaky. Since we don't have an
			// initialiser, we cannot generate any codes for destructuring it.
			// Therefore, we create a dummy block into which any such codes are
			// placed and then we discard it. This is essentially a hack to
			// reuse the existing addDeclaredVariables method.
			addDeclaredVariables(root, s.pattern, s.type.raw(), environment,
					new Code.Block(codes.numInputs()));			
		}		
	}
	
	private void generate(Assign s, Environment environment, Code.Block codes, Context context) {
		if (s.lhs instanceof Expr.AssignedVariable) {
			Expr.AssignedVariable v = (Expr.AssignedVariable) s.lhs;
			int operand = generate(s.rhs, environment, codes, context);
			
			if (environment.get(v.var) == null) {
				environment.put(operand,v.var);
			} else {
				int target = environment.get(v.var);
				codes.add(Codes.Assign(s.rhs.result().raw(), target, operand),
						attributes(s));
			}
		} else if(s.lhs instanceof Expr.RationalLVal) {
			Expr.RationalLVal tg = (Expr.RationalLVal) s.lhs;
			
			Expr.AssignedVariable lv = (Expr.AssignedVariable) tg.numerator;
			Expr.AssignedVariable rv = (Expr.AssignedVariable) tg.denominator;
			
			if (environment.get(lv.var) == null) {
				environment.allocate(Type.T_INT,lv.var);
			}
			if (environment.get(rv.var) == null) {
				environment.allocate(Type.T_INT,rv.var);
			}
									
			int operand = generate(s.rhs, environment, codes, context);
			
			codes.add(Codes.UnaryOperator(s.rhs.result()
					.raw(), environment.get(lv.var), operand, Codes.UnaryOperatorKind.NUMERATOR),
					attributes(s));
			
			codes.add(Codes.UnaryOperator(s.rhs.result().raw(),
					environment.get(rv.var), operand,
					Codes.UnaryOperatorKind.DENOMINATOR), attributes(s));
						
		} else if(s.lhs instanceof Expr.Tuple) {					
			Expr.Tuple tg = (Expr.Tuple) s.lhs;
			ArrayList<Expr> fields = new ArrayList<Expr>(tg.fields);
			for (int i = 0; i != fields.size(); ++i) {
				Expr e = fields.get(i);
				if (!(e instanceof Expr.AssignedVariable)) {
					WhileyFile.syntaxError(errorMessage(INVALID_TUPLE_LVAL),
							context, e);
				}
				Expr.AssignedVariable v = (Expr.AssignedVariable) e;
				if (environment.get(v.var) == null) {
					environment.allocate(v.afterType.raw(), v.var);
				}
			}
			int operand = generate(s.rhs, environment, codes, context);
			for (int i = 0; i != fields.size(); ++i) {
				Expr.AssignedVariable v = (Expr.AssignedVariable) fields.get(i);
				codes.add(Codes.TupleLoad((Type.EffectiveTuple) s.rhs
						.result().raw(), environment.get(v.var), operand, i),
						attributes(s));
			}		
		} else if (s.lhs instanceof Expr.IndexOf
				|| s.lhs instanceof Expr.FieldAccess
				|| s.lhs instanceof Expr.Dereference) {
				
			ArrayList<String> fields = new ArrayList<String>();
			ArrayList<Integer> operands = new ArrayList<Integer>();
			Expr.AssignedVariable lhs = extractLVal(s.lhs, fields, operands,
					environment, codes, context);
			if (environment.get(lhs.var) == null) {
				WhileyFile.syntaxError("unknown variable",
						context, lhs);
			}
			int target = environment.get(lhs.var);
			int rhsRegister = generate(s.rhs, environment, codes, context);

			codes.add(Codes.Update(lhs.type.raw(), target, operands,
					rhsRegister, lhs.afterType.raw(), fields), attributes(s));
		} else {
			WhileyFile.syntaxError("invalid assignment", context, s);
		}
	}

	private Expr.AssignedVariable extractLVal(Expr e, ArrayList<String> fields,
			ArrayList<Integer> operands, Environment environment, Code.Block codes,
			Context context) {

		if (e instanceof Expr.AssignedVariable) {
			Expr.AssignedVariable v = (Expr.AssignedVariable) e;
			return v;
		} else if (e instanceof Expr.Dereference) {
			Expr.Dereference pa = (Expr.Dereference) e;
			return extractLVal(pa.src, fields, operands, environment, codes, context);
		} else if (e instanceof Expr.IndexOf) {
			Expr.IndexOf la = (Expr.IndexOf) e;
			int operand = generate(la.index, environment, codes, context);
			Expr.AssignedVariable l = extractLVal(la.src, fields, operands,
					environment, codes, context);
			operands.add(operand);
			return l;
		} else if (e instanceof Expr.FieldAccess) {
			Expr.FieldAccess ra = (Expr.FieldAccess) e;
			Expr.AssignedVariable r = extractLVal(ra.src, fields, operands,
					environment, codes, context);
			fields.add(ra.name);
			return r;
		} else {
			WhileyFile.syntaxError(errorMessage(INVALID_LVAL_EXPRESSION),
					context, e);
			return null; // dead code
		}
	}
	
	private void generate(Assert s, Environment environment, Code.Block codes,
			Context context) {
		generateAssertion("assertion failed", s.expr, false, environment, codes, context);
		// TODO: the following is a temporary fix to ensure that manual
		// assertions and assumed.  See #377
		generateAssertion("assumption failed", s.expr, true, environment, codes, context);
	}

	private void generate(Assume s, Environment environment, Code.Block codes,
			Context context) {
		generateAssertion("assumption failed", s.expr, true,
				environment, codes, context);
	}
	
	private void generate(Return s, Environment environment, Code.Block codes,
			Context context) {

		if (s.expr != null) {
			int operand = generate(s.expr, environment, codes, context);

			// Here, we don't put the type propagated for the return expression.
			// Instead, we use the declared return type of this function. This
			// has the effect of forcing an implicit coercion between the
			// actual value being returned and its required type.

			Type ret = ((WhileyFile.FunctionOrMethod) context).resolvedType()
					.raw().ret();

			codes.add(Codes.Return(ret, operand), attributes(s));
		} else {
			codes.add(Codes.Return(), attributes(s));
		}
	}

	private void generate(Skip s, Environment environment, Code.Block codes,
			Context context) {
		codes.add(Codes.Nop, attributes(s));
	}

	private void generate(Debug s, Environment environment,
			Code.Block codes, Context context) {
		int operand = generate(s.expr, environment, codes, context);
		codes.add(Codes.Debug(operand), attributes(s));
	}

	private void generate(IfElse s, Environment environment, Code.Block codes,
			Context context) {
		String falseLab = CodeUtils.freshLabel();
		String exitLab = s.falseBranch.isEmpty() ? falseLab : CodeUtils
				.freshLabel();

		generateCondition(falseLab, invert(s.condition), environment, codes, context);

		for (Stmt st : s.trueBranch) {
			generate(st, environment, codes, context);
		}
		if (!s.falseBranch.isEmpty()) {
			codes.add(Codes.Goto(exitLab));
			codes.add(Codes.Label(falseLab));
			for (Stmt st : s.falseBranch) {
				generate(st, environment, codes, context);
			}
		}

		codes.add(Codes.Label(exitLab));
	}
	
	private void generate(Throw s, Environment environment, Code.Block codes, Context context) {
		int operand = generate(s.expr, environment, codes, context);
		codes.add(Codes.Throw(s.expr.result().raw(), operand),
				s.attributes());
	}
	
	private void generate(Break s, Environment environment, Code.Block codes, Context context) {
		BreakScope scope = findEnclosingScope(BreakScope.class);
		if (scope == null) {
			WhileyFile.syntaxError(errorMessage(BREAK_OUTSIDE_LOOP),
					context, s);
		}
		codes.add(Codes.Goto(scope.label));
	}
	
	private void generate(Switch s, Environment environment,
			Code.Block codes, Context context) throws Exception {
		String exitLab = CodeUtils.freshLabel();
		int operand = generate(s.expr, environment, codes, context);
		String defaultTarget = exitLab;
		HashSet<Constant> values = new HashSet();
		ArrayList<Pair<Constant, String>> cases = new ArrayList();
		int start = codes.size();

		for (Stmt.Case c : s.cases) {
			if (c.expr.isEmpty()) {
				// A case with an empty match represents the default label. We
				// must check that we have not already seen a case with an empty
				// match (otherwise, we'd have two default labels ;)
				if (defaultTarget != exitLab) {
					WhileyFile.syntaxError(
							errorMessage(DUPLICATE_DEFAULT_LABEL),
							context, c);
				} else {
					defaultTarget = CodeUtils.freshLabel();
					codes.add(Codes.Label(defaultTarget), attributes(c));
					for (Stmt st : c.stmts) {
						generate(st, environment, codes, context);
					}
					codes.add(Codes.Goto(exitLab), attributes(c));
				}
				
			} else if (defaultTarget == exitLab) {
				String target = CodeUtils.freshLabel();
				codes.add(Codes.Label(target), attributes(c));

				// Case statements in Whiley may have multiple matching constant
				// values. Therefore, we iterate each matching value and
				// construct a mapping from that to a label indicating the start
				// of the case body. 
				
				for (Constant constant : c.constants) {
					// Check whether this case constant has already been used as
					// a case constant elsewhere. If so, then report an error.
					if (values.contains(constant)) {
						WhileyFile.syntaxError(
								errorMessage(DUPLICATE_CASE_LABEL),
								context, c);
					}
					cases.add(new Pair(constant, target));
					values.add(constant);
				}

				for (Stmt st : c.stmts) {
					generate(st, environment, codes, context);
				}
				codes.add(Codes.Goto(exitLab), attributes(c));
				
			} else {
				// This represents the case where we have another non-default
				// case after the default case. Such code cannot be executed,
				// and is therefore reported as an error.
				WhileyFile.syntaxError(errorMessage(UNREACHABLE_CODE),
						context, c);
			}
		}

		codes.add(start, Codes.Switch(s.expr.result().raw(), operand,
				defaultTarget, cases), attributes(s));
		codes.add(Codes.Label(exitLab), attributes(s));
	}
	
	private void generate(TryCatch s, Environment environment, Code.Block codes, Context context) throws Exception {
		int start = codes.size();
		int exceptionRegister = environment.allocate(Type.T_ANY);
		String exitLab = CodeUtils.freshLabel();		
		
		for (Stmt st : s.body) {
			generate(st, environment, codes, context);
		}		
		codes.add(Codes.Goto(exitLab),attributes(s));	
		String endLab = null;
		ArrayList<Pair<Type,String>> catches = new ArrayList<Pair<Type,String>>();
		for(Stmt.Catch c : s.catches) {			
			Codes.Label lab;
			
			if(endLab == null) {
				endLab = CodeUtils.freshLabel();
				lab = Codes.TryEnd(endLab);
			} else {
				lab = Codes.Label(CodeUtils.freshLabel());
			}
			Type pt = c.type.raw();
			// TODO: deal with exception type constraints
			catches.add(new Pair<Type,String>(pt,lab.label));
			codes.add(lab, attributes(c));
			environment.put(exceptionRegister, c.variable);
			for (Stmt st : c.stmts) {
				generate(st, environment, codes, context);
			}
			codes.add(Codes.Goto(exitLab),attributes(c));
		}
		
		codes.add(start, Codes.TryCatch(exceptionRegister,endLab,catches),attributes(s));
		codes.add(Codes.Label(exitLab), attributes(s));
	}
	
	private void generate(While s, Environment environment, Code.Block codes,
			Context context) {
		String label = CodeUtils.freshLabel();
		String exit = CodeUtils.freshLabel();

		for (Expr invariant : s.invariants) {
			// FIXME: this should be added to RuntimeAssertions
			generateAssertion(
					"loop invariant not satisfied on entry", invariant, false,
					environment, codes, context);
		}

		codes.add(Codes.Loop(label, Collections.EMPTY_SET), attributes(s));

		for (Expr invariant : s.invariants) {
			// FIXME: this should be added to RuntimeAssertions
			generateAssertion("", invariant, true, environment,
					codes, context);
		}

		generateCondition(exit, invert(s.condition),
				environment, codes, context);
		
		scopes.push(new BreakScope(exit));
		for (Stmt st : s.body) {
			generate(st, environment, codes, context);
		}
		scopes.pop(); // break

		for (Expr invariant : s.invariants) {
			// FIXME: this should be added to RuntimeAssertions
			generateAssertion("loop invariant not restored",
					invariant, false, environment, codes, context);
		}

		// Must add NOP before loop end to ensure labels at the boundary
		// get written into Wyil files properly. See Issue #253.
		codes.add(Codes.Nop);
		codes.add(Codes.LoopEnd(label), attributes(s));
		codes.add(Codes.Label(exit), attributes(s));
	}

	private void generate(DoWhile s, Environment environment, Code.Block codes,
			Context context) {		
		String label = CodeUtils.freshLabel();				
		String exit = CodeUtils.freshLabel();
		
		for (Expr invariant : s.invariants) {
			// FIXME: this should be added to RuntimeAssertions
			generateAssertion(
					"loop invariant not satisfied on entry", invariant,
					false, environment, codes, context);
		}
		
		codes.add(Codes.Loop(label, Collections.EMPTY_SET),
				attributes(s));
		
		for (Expr invariant : s.invariants) {
			// FIXME: this should be added to RuntimeAssertions
			generateAssertion("", invariant, true, environment,
					codes, context);
		}
		
		scopes.push(new BreakScope(exit));	
		for (Stmt st : s.body) {
			generate(st, environment, codes, context);
		}		
		scopes.pop(); // break
		
		for (Expr invariant : s.invariants) {
			// FIXME: this should be added to RuntimeAssertions
			generateAssertion("loop invariant not restored",
					invariant, false, environment, codes, context);
		}
		
		generateCondition(exit, invert(s.condition),
				environment, codes, context);
		
		// Must add NOP before loop end to ensure labels at the boundary
		// get written into Wyil files properly. See Issue #253.
		codes.add(Codes.Nop);
		codes.add(Codes.LoopEnd(label), attributes(s));
		codes.add(Codes.Label(exit), attributes(s));		
	}
	
	private void generate(ForAll s, Environment environment,
			Code.Block codes, Context context) {
		String label = CodeUtils.freshLabel();
		String exit = CodeUtils.freshLabel();
		
		if (s.invariant != null) {
			// FIXME: this should be added to RuntimeAssertions
			String invariantLabel = CodeUtils.freshLabel();
			generateAssertion(
					"loop invariant not satisfied on entry", s.invariant,
					false, environment, codes, context);
		}

		int sourceRegister = generate(s.source, environment,
				codes, context);

		// FIXME: loss of nominal information
		Type.EffectiveCollection rawSrcType = s.srcType.raw();

		if (s.variables.size() > 1) {
			// this is the destructuring case

			// FIXME: support destructuring of lists and sets
			if (!(rawSrcType instanceof Type.EffectiveMap)) {
				WhileyFile.syntaxError(errorMessage(INVALID_MAP_EXPRESSION),
						context, s.source);
			}
			Type.EffectiveMap dict = (Type.EffectiveMap) rawSrcType;
			Type.Tuple element = (Type.Tuple) Type.Tuple(dict.key(),
					dict.value());
			int indexRegister = environment.allocate(element);
			codes.add(Codes
					.ForAll((Type.EffectiveMap) rawSrcType, sourceRegister,
							indexRegister, Collections.EMPTY_SET, label),
					attributes(s));

			for (int i = 0; i < s.variables.size(); ++i) {
				String var = s.variables.get(i);
				int varReg = environment.allocate(element.element(i), var);
				codes.add(Codes.TupleLoad(element, varReg, indexRegister, i),
						attributes(s));
			}
		} else {
			// easy case.
			int indexRegister = environment.allocate(rawSrcType.element(),
					s.variables.get(0));
			codes.add(Codes.ForAll(s.srcType.raw(), sourceRegister,
					indexRegister, Collections.EMPTY_SET, label), attributes(s));
		}

		if (s.invariant != null) {
			// FIXME: this should be added to RuntimeAssertions
			generateAssertion(
					"", s.invariant,
					true, environment, codes, context);
		}
		
		// FIXME: add a continue scope
		scopes.push(new BreakScope(exit));
		for (Stmt st : s.body) {
			generate(st, environment, codes, context);
		}
		scopes.pop(); // break

		if (s.invariant != null) {
			// FIXME: this should be added to RuntimeAssertions
			generateAssertion("loop invariant not restored",
					s.invariant, false, environment, codes, context);
		}
		
		// Must add NOP before loop end to ensure labels at the boundary
		// get written into Wyil files properly. See Issue #253.
		codes.add(Codes.Nop);
		codes.add(Codes.LoopEnd(label), attributes(s));
		codes.add(Codes.Label(exit), attributes(s));				
	}

	// =========================================================================
	// Assertions
	// =========================================================================		
	
	/**
	 * Translate a source-level assertion into a WyIL block, using a given
	 * environment mapping named variables to slots. If the condition evaluates
	 * to true, then control continues as normal. Otherwise, an assertion
	 * failure is raised with the given message.
	 * 
	 * @param message
	 *            --- Message to report if condition is false.
	 * @param condition
	 *            --- Source-level condition to be translated
	 * @param isAssumption
	 *            --- indicates whether to generate an assumption or an
	 *            assertion.
	 * @param freeRegister
	 *            --- All registers with and index equal or higher than this are
	 *            available for use as temporary storage.
	 * @param environment
	 *            --- Mapping from variable names to to register indices.
	 * @return
	 */
	public void generateAssertion(String message, Expr condition,
			boolean isAssumption, Environment environment, Code.Block codes,
			Context context) {
		try {
			if (condition instanceof Expr.BinOp) {
				generateAssertion(message, (Expr.BinOp) condition,
						isAssumption, environment, codes, context);
			} else if (condition instanceof Expr.Constant
					|| condition instanceof Expr.ConstantAccess
					|| condition instanceof Expr.LocalVariable
					|| condition instanceof Expr.UnOp
					|| condition instanceof Expr.AbstractInvoke
					|| condition instanceof Expr.FieldAccess
					|| condition instanceof Expr.IndexOf
					|| condition instanceof Expr.Comprehension) {

				// The default case simply compares the computed value against
				// true. In some cases, we could do better. For example, !(x <
				// 5)
				// could be rewritten into x>=5.

				int r1 = generate(condition, environment, codes, context);
				int r2 = environment.allocate(Type.T_BOOL);
				codes.add(Codes.Const(r2, Constant.V_BOOL(true)),
						attributes(condition));
				if (isAssumption) {
					codes.add(Codes.Assume(Type.T_BOOL, r1, r2,
							Codes.Comparator.EQ, message), attributes(condition));
				} else {
					codes.add(Codes.Assert(Type.T_BOOL, r1, r2,
							Codes.Comparator.EQ, message), attributes(condition));
				}
			} else {
				syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context,
						condition);
			}

		} catch (SyntaxError se) {
			throw se;
		} catch (Exception ex) {
			internalFailure(ex.getMessage(), context, condition, ex);
		}
	}

	protected void generateAssertion(String message, Expr.BinOp v,
			boolean isAssumption, Environment environment, Code.Block codes,
			Context context) {
		Expr.BOp bop = v.op;

		if (bop == Expr.BOp.OR) {
			String lab = CodeUtils.freshLabel();
			generateCondition(lab, v.lhs, environment, codes, context);
			generateAssertion(message, v.rhs, isAssumption, environment, codes, context);
			codes.add(Codes.Label(lab));
		} else if (bop == Expr.BOp.AND) {
			generateAssertion(message, v.lhs, isAssumption, environment, codes, context);
			generateAssertion(message, v.rhs, isAssumption, environment, codes, context);
		} else {

			// TODO: there are some cases which will break here. In particular,
			// those involving type tests. If/When WYIL changes to be register
			// based this should fall out in the wash.

			Codes.Comparator cop = OP2COP(bop, v, context);

			int r1 = generate(v.lhs, environment, codes, context);
			int r2 = generate(v.rhs, environment, codes, context);
			if (isAssumption) {
				codes.add(
						Codes.Assume(v.srcType.raw(), r1, r2, cop, message),
						attributes(v));
			} else {
				codes.add(
						Codes.Assert(v.srcType.raw(), r1, r2, cop, message),
						attributes(v));
			}

		}
	}

	/**
	 * Translate a source-level condition into a WyIL block, using a given
	 * environment mapping named variables to slots. If the condition evaluates
	 * to true, then control is transferred to the given target. Otherwise,
	 * control will fall through to the following bytecode.
	 * 
	 * @param target
	 *            --- Target label to goto if condition is true.
	 * @param condition
	 *            --- Source-level condition to be translated
	 * @param environment
	 *            --- Mapping from variable names to to slot numbers.
	 * @param codes
	 *            --- List of bytecodes onto which translation should be
	 *            appended.
	 * @return
	 */
	public void generateCondition(String target, Expr condition,
			Environment environment, Code.Block codes, Context context) {
		try {
			if (condition instanceof Expr.Constant) {
				generateCondition(target, (Expr.Constant) condition,
						environment, codes, context);
			} else if (condition instanceof Expr.UnOp) {
				generateCondition(target, (Expr.UnOp) condition, environment,
						codes, context);
			} else if (condition instanceof Expr.BinOp) {
				generateCondition(target, (Expr.BinOp) condition, environment,
						codes, context);
			} else if (condition instanceof Expr.Comprehension) {
				generateCondition(target, (Expr.Comprehension) condition,
						environment, codes, context);
			} else if (condition instanceof Expr.ConstantAccess
					|| condition instanceof Expr.LocalVariable
					|| condition instanceof Expr.AbstractInvoke
					|| condition instanceof Expr.AbstractIndirectInvoke
					|| condition instanceof Expr.FieldAccess
					|| condition instanceof Expr.IndexOf) {

				// The default case simply compares the computed value against
				// true. In some cases, we could do better. For example, !(x <
				// 5)
				// could be rewritten into x>=5.

				int r1 = generate(condition, environment, codes, context);
				int r2 = environment.allocate(Type.T_BOOL);
				codes.add(Codes.Const(r2, Constant.V_BOOL(true)),
						attributes(condition));
				codes.add(Codes.If(Type.T_BOOL, r1, r2, Codes.Comparator.EQ,
						target), attributes(condition));

			} else {
				syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context,
						condition);
			}

		} catch (SyntaxError se) {
			throw se;
		} catch (Exception ex) {
			internalFailure(ex.getMessage(), context, condition, ex);
		}

	}

	private void generateCondition(String target, Expr.Constant c,
			Environment environment, Code.Block codes, Context context) {
		Constant.Bool b = (Constant.Bool) c.value;
		if (b.value) {
			codes.add(Codes.Goto(target));
		} else {
			// do nout
		}
	}

	private void generateCondition(String target, Expr.BinOp v,
			Environment environment, Code.Block codes, Context context) throws Exception {

		Expr.BOp bop = v.op;

		if (bop == Expr.BOp.OR) {
			generateCondition(target, v.lhs, environment, codes, context);
			generateCondition(target, v.rhs, environment, codes, context);

		} else if (bop == Expr.BOp.AND) {
			String exitLabel = CodeUtils.freshLabel();
			generateCondition(exitLabel, invert(v.lhs), environment, codes, context);
			generateCondition(target, v.rhs, environment, codes, context);
			codes.add(Codes.Label(exitLabel));

		} else if (bop == Expr.BOp.IS) {
			generateTypeCondition(target, v, environment, codes, context);

		} else {

			Codes.Comparator cop = OP2COP(bop, v, context);

			if (cop == Codes.Comparator.EQ
					&& v.lhs instanceof Expr.LocalVariable
					&& v.rhs instanceof Expr.Constant
					&& ((Expr.Constant) v.rhs).value == Constant.V_NULL) {
				// this is a simple rewrite to enable type inference.
				Expr.LocalVariable lhs = (Expr.LocalVariable) v.lhs;
				if (environment.get(lhs.var) == null) {
					syntaxError(errorMessage(UNKNOWN_VARIABLE), context, v.lhs);
				}
				int slot = environment.get(lhs.var);
				codes.add(
						Codes.IfIs(v.srcType.raw(), slot, Type.T_NULL, target),
						attributes(v));
			} else if (cop == Codes.Comparator.NEQ
					&& v.lhs instanceof Expr.LocalVariable
					&& v.rhs instanceof Expr.Constant
					&& ((Expr.Constant) v.rhs).value == Constant.V_NULL) {
				// this is a simple rewrite to enable type inference.
				String exitLabel = CodeUtils.freshLabel();
				Expr.LocalVariable lhs = (Expr.LocalVariable) v.lhs;
				if (environment.get(lhs.var) == null) {
					syntaxError(errorMessage(UNKNOWN_VARIABLE), context, v.lhs);
				}
				int slot = environment.get(lhs.var);
				codes.add(Codes.IfIs(v.srcType.raw(), slot, Type.T_NULL,
						exitLabel), attributes(v));
				codes.add(Codes.Goto(target));
				codes.add(Codes.Label(exitLabel));
			} else {
				int lhs = generate(v.lhs, environment, codes, context);
				int rhs = generate(v.rhs, environment, codes, context);
				codes.add(Codes.If(v.srcType.raw(), lhs, rhs, cop, target),
						attributes(v));
			}
		}
	}

	private void generateTypeCondition(String target, Expr.BinOp v,
			Environment environment, Code.Block codes, Context context) throws Exception {
		int leftOperand;

		if (v.lhs instanceof Expr.LocalVariable) {
			Expr.LocalVariable lhs = (Expr.LocalVariable) v.lhs;
			if (environment.get(lhs.var) == null) {
				syntaxError(errorMessage(UNKNOWN_VARIABLE), context, v.lhs);
			}
			leftOperand = environment.get(lhs.var);
		} else {
			leftOperand = generate(v.lhs, environment, codes, context);
		}

		Expr.TypeVal rhs = (Expr.TypeVal) v.rhs;
		Code.Block constraint = generate(rhs.unresolvedType, context);
		if (constraint != null) {
			String exitLabel = CodeUtils.freshLabel();
			Type glb = Type.intersect(v.srcType.raw(),
					Type.Negation(rhs.type.raw()));

			if (glb != Type.T_VOID) {
				// Only put the actual type test in if it is necessary.
				String nextLabel = CodeUtils.freshLabel();

				// FIXME: should be able to just test the glb here and branch to
				// exit label directly. However, this currently doesn't work
				// because of limitations with intersection of open records.

				codes.add(Codes.IfIs(v.srcType.raw(), leftOperand,
						rhs.type.raw(), nextLabel), attributes(v));
				codes.add(Codes.Goto(exitLabel));
				codes.add(Codes.Label(nextLabel));
			}
			constraint = shiftBlockExceptionZero(environment.size() - 1,
					leftOperand, constraint);
			codes.addAll(chainBlock(exitLabel, constraint));
			codes.add(Codes.Goto(target));
			codes.add(Codes.Label(exitLabel));
		} else {
			codes.add(Codes.IfIs(v.srcType.raw(), leftOperand,
					rhs.type.raw(), target), attributes(v));
		}
	}

	private void generateCondition(String target, Expr.UnOp v,
			Environment environment, Code.Block codes, Context context) {
		Expr.UOp uop = v.op;
		switch (uop) {
		case NOT:
			String label = CodeUtils.freshLabel();
			generateCondition(label, v.mhs, environment, codes, context);
			codes.add(Codes.Goto(target));
			codes.add(Codes.Label(label));
			return;
		}
		syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context, v);
	}

	private void generateCondition(String target, Expr.Comprehension e,
			Environment environment, Code.Block codes, Context context) {
		if (e.cop != Expr.COp.NONE && e.cop != Expr.COp.SOME && e.cop != Expr.COp.ALL) {
			syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context, e);
		}

		ArrayList<Triple<Integer, Integer, Type.EffectiveCollection>> slots = new ArrayList();

		for (Pair<String, Expr> src : e.sources) {
			Nominal.EffectiveCollection srcType = (Nominal.EffectiveCollection) src
					.second().result();
			int srcSlot;
			int varSlot = environment.allocate(srcType.raw().element(),
					src.first());

			if (src.second() instanceof Expr.LocalVariable) {
				// this is a little optimisation to produce slightly better
				// code.
				Expr.LocalVariable v = (Expr.LocalVariable) src.second();
				if (environment.get(v.var) != null) {
					srcSlot = environment.get(v.var);
				} else {
					// fall-back plan ...
					srcSlot = generate(src.second(), environment, codes, context);
				}
			} else {
				srcSlot = generate(src.second(), environment, codes, context);
			}
			slots.add(new Triple(varSlot, srcSlot, srcType.raw()));
		}

		ArrayList<String> labels = new ArrayList<String>();
		String loopLabel = CodeUtils.freshLabel();

		for (Triple<Integer, Integer, Type.EffectiveCollection> p : slots) {
			Type.EffectiveCollection srcType = p.third();
			String lab = loopLabel + "$" + p.first();
			codes.add(Codes.ForAll(srcType, p.second(), p.first(),
					Collections.EMPTY_LIST, lab), attributes(e));
			labels.add(lab);
		}

		if (e.cop == Expr.COp.NONE) {
			String exitLabel = CodeUtils.freshLabel();
			generateCondition(exitLabel, e.condition, environment, codes, context);
			for (int i = (labels.size() - 1); i >= 0; --i) {
				// Must add NOP before loop end to ensure labels at the boundary
				// get written into Wyil files properly. See Issue #253.
				codes.add(Codes.Nop);
				codes.add(Codes.LoopEnd(labels.get(i)));
			}
			codes.add(Codes.Goto(target));
			codes.add(Codes.Label(exitLabel));
		} else if (e.cop == Expr.COp.SOME) {
			generateCondition(target, e.condition, environment, codes, context);
			for (int i = (labels.size() - 1); i >= 0; --i) {
				// Must add NOP before loop end to ensure labels at the boundary
				// get written into Wyil files properly. See Issue #253.
				codes.add(Codes.Nop);
				codes.add(Codes.LoopEnd(labels.get(i)));
			}
		} else if (e.cop == Expr.COp.ALL) {
			String exitLabel = CodeUtils.freshLabel();
			generateCondition(exitLabel, invert(e.condition), environment, codes, context);
			for (int i = (labels.size() - 1); i >= 0; --i) {
				// Must add NOP before loop end to ensure labels at the boundary
				// get written into Wyil files properly. See Issue #253.
				codes.add(Codes.Nop);
				codes.add(Codes.LoopEnd(labels.get(i)));
			}
			codes.add(Codes.Goto(target));
			codes.add(Codes.Label(exitLabel));		
		} // LONE and ONE will be harder
	}

	// =========================================================================
	// Expressions
	// =========================================================================		

	/**
	 * Translate a source-level expression into a WYIL bytecode block, using a
	 * given environment mapping named variables to registers. The result of the
	 * expression is stored in a given target register.
	 * 
	 * @param expression
	 *            --- Source-level expression to be translated
	 * @param environment
	 *            --- Mapping from variable names to to slot numbers.
	 * @param codes
	 *            --- List of bytecodes onto which translation should be
	 *            appended.
	 * 
	 * @return --- the register
	 */
	public int generate(Expr expression, Environment environment, Code.Block codes, Context context) {
		try {
			if (expression instanceof Expr.Constant) {
				return generate((Expr.Constant) expression, environment, codes, context);
			} else if (expression instanceof Expr.LocalVariable) {
				return generate((Expr.LocalVariable) expression, environment,
						codes, context);
			} else if (expression instanceof Expr.ConstantAccess) {
				return generate((Expr.ConstantAccess) expression, environment,
						codes, context);
			} else if (expression instanceof Expr.Set) {
				return generate((Expr.Set) expression, environment, codes, context);
			} else if (expression instanceof Expr.List) {
				return generate((Expr.List) expression, environment, codes, context);
			} else if (expression instanceof Expr.SubList) {
				return generate((Expr.SubList) expression, environment, codes, context);
			} else if (expression instanceof Expr.SubString) {
				return generate((Expr.SubString) expression, environment, codes, context);
			} else if (expression instanceof Expr.BinOp) {
				return generate((Expr.BinOp) expression, environment, codes, context);
			} else if (expression instanceof Expr.LengthOf) {
				return generate((Expr.LengthOf) expression, environment, codes, context);
			} else if (expression instanceof Expr.Dereference) {
				return generate((Expr.Dereference) expression, environment,
						codes, context);
			} else if (expression instanceof Expr.Cast) {
				return generate((Expr.Cast) expression, environment, codes, context);
			} else if (expression instanceof Expr.IndexOf) {
				return generate((Expr.IndexOf) expression, environment, codes, context);
			} else if (expression instanceof Expr.UnOp) {
				return generate((Expr.UnOp) expression, environment, codes, context);
			} else if (expression instanceof Expr.FunctionCall) {
				return generate((Expr.FunctionCall) expression, environment,
						codes, context);
			} else if (expression instanceof Expr.MethodCall) {
				return generate((Expr.MethodCall) expression, environment,
						codes, context);
			} else if (expression instanceof Expr.IndirectFunctionCall) {
				return generate((Expr.IndirectFunctionCall) expression,
						environment, codes, context);
			} else if (expression instanceof Expr.IndirectMethodCall) {
				return generate((Expr.IndirectMethodCall) expression,
						environment, codes, context);
			} else if (expression instanceof Expr.Comprehension) {
				return generate((Expr.Comprehension) expression, environment,
						codes, context);
			} else if (expression instanceof Expr.FieldAccess) {
				return generate((Expr.FieldAccess) expression, environment,
						codes, context);
			} else if (expression instanceof Expr.Record) {
				return generate((Expr.Record) expression, environment, codes, context);
			} else if (expression instanceof Expr.Tuple) {
				return generate((Expr.Tuple) expression, environment, codes, context);
			} else if (expression instanceof Expr.Map) {
				return generate((Expr.Map) expression, environment, codes, context);
			} else if (expression instanceof Expr.FunctionOrMethod) {
				return generate((Expr.FunctionOrMethod) expression,
						environment, codes, context);
			} else if (expression instanceof Expr.Lambda) {
				return generate((Expr.Lambda) expression, environment, codes, context);
			} else if (expression instanceof Expr.New) {
				return generate((Expr.New) expression, environment, codes, context);
			} else {
				// should be dead-code
				internalFailure("unknown expression: "
						+ expression.getClass().getName(), context, expression);
			}
		} catch (ResolveError rex) {
			syntaxError(rex.getMessage(), context, expression, rex);
		} catch (SyntaxError se) {
			throw se;
		} catch (Exception ex) {
			internalFailure(ex.getMessage(), context, expression, ex);
		}

		return -1; // deadcode
	}

	public int generate(Expr.MethodCall expr, Environment environment,
			Code.Block codes, Context context) throws ResolveError {
		int target = environment.allocate(expr.result().raw());
		generate(expr, target, environment, codes, context);
		return target;
	}

	public void generate(Expr.MethodCall expr, int target,
			Environment environment, Code.Block codes, Context context) throws ResolveError {
		int[] operands = generate(expr.arguments, environment, codes, context);
		codes.add(Codes.Invoke(expr.methodType.raw(), target, operands,
				expr.nid()), attributes(expr));
	}

	public int generate(Expr.FunctionCall expr, Environment environment,
			Code.Block codes, Context context) throws ResolveError {
		int target = environment.allocate(expr.result().raw());
		generate(expr, target, environment, codes, context);
		return target;
	}

	public void generate(Expr.FunctionCall expr, int target,
			Environment environment, Code.Block codes, Context context) throws ResolveError {
		int[] operands = generate(expr.arguments, environment, codes, context);
		codes.add(
				Codes.Invoke(expr.functionType.raw(), target, operands,
						expr.nid()), attributes(expr));
	}

	public int generate(Expr.IndirectFunctionCall expr,
			Environment environment, Code.Block codes, Context context) throws ResolveError {
		int target = environment.allocate(expr.result().raw());
		generate(expr, target, environment, codes, context);
		return target;
	}

	public void generate(Expr.IndirectFunctionCall expr, int target,
			Environment environment, Code.Block codes, Context context) throws ResolveError {
		int operand = generate(expr.src, environment, codes, context);
		int[] operands = generate(expr.arguments, environment, codes, context);
		codes.add(Codes.IndirectInvoke(expr.functionType.raw(), target,
				operand, operands), attributes(expr));
	}

	public int generate(Expr.IndirectMethodCall expr, Environment environment,
			Code.Block codes, Context context) throws ResolveError {
		int target = environment.allocate(expr.result().raw());
		generate(expr, target, environment, codes, context);
		return target;
	}

	public void generate(Expr.IndirectMethodCall expr, int target,
			Environment environment, Code.Block codes, Context context) throws ResolveError {
		int operand = generate(expr.src, environment, codes, context);
		int[] operands = generate(expr.arguments, environment, codes, context);
		codes.add(Codes.IndirectInvoke(expr.methodType.raw(), target,
				operand, operands), attributes(expr));
	}

	private int generate(Expr.Constant expr, Environment environment,
			Code.Block codes, Context context) {
		Constant val = expr.value;
		int target = environment.allocate(val.type());
		codes.add(Codes.Const(target, expr.value), attributes(expr));
		return target;
	}

	private int generate(Expr.FunctionOrMethod expr, Environment environment,
			Code.Block codes, Context context) {
		Type.FunctionOrMethod type = expr.type.raw();
		int target = environment.allocate(type);
		codes.add(
				Codes.Lambda(type, target, Collections.EMPTY_LIST, expr.nid),
				attributes(expr));
		return target;
	}

	private int generate(Expr.Lambda expr, Environment environment, Code.Block codes, Context context) {
		Type.FunctionOrMethod tfm = expr.type.raw();
		List<Type> tfm_params = tfm.params();
		List<WhileyFile.Parameter> expr_params = expr.parameters;
		
		// Create environment for the lambda body.
		ArrayList<Integer> operands = new ArrayList<Integer>();
		ArrayList<Type> paramTypes = new ArrayList<Type>();
		Environment benv = new Environment();
		for (int i = 0; i != tfm_params.size(); ++i) {
			Type type = tfm_params.get(i);
			benv.allocate(type, expr_params.get(i).name);
			paramTypes.add(type);
			operands.add(Codes.NULL_REG);
		}
		for(Pair<Type,String> v : Exprs.uses(expr.body,context)) {
			if(benv.get(v.second()) == null) {
				Type type = v.first();
				benv.allocate(type,v.second());
				paramTypes.add(type);
				operands.add(environment.get(v.second()));
			}
		}

		// Generate body based on current environment
		Code.Block body = new Code.Block(expr_params.size());
		if(tfm.ret() != Type.T_VOID) {
			int target = generate(expr.body, benv, body, context);
			body.add(Codes.Return(tfm.ret(), target), attributes(expr));		
		} else {
			body.add(Codes.Return(), attributes(expr));
		}
		
		// Create concrete type for private lambda function
		Type.FunctionOrMethod cfm;
		if(tfm instanceof Type.Function) {
			cfm = Type.Function(tfm.ret(),tfm.throwsClause(),paramTypes);
		} else {
			cfm = Type.Method(tfm.ret(),tfm.throwsClause(),paramTypes);
		}
				
		// Construct private lambda function using generated body
		int id = expr.attribute(Attribute.Source.class).start;
		String name = "$lambda" + id;
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.PRIVATE);
		ArrayList<WyilFile.Case> cases = new ArrayList<WyilFile.Case>();		
		cases.add(new WyilFile.Case(body, null, null, attributes(expr)));
		WyilFile.FunctionOrMethodDeclaration lambda = new WyilFile.FunctionOrMethodDeclaration(
				modifiers, name, cfm, cases, attributes(expr));
		lambdas.add(lambda);
		Path.ID mid = context.file().module;
		NameID nid = new NameID(mid, name);
		
		// Finally, create the lambda
		int target = environment.allocate(tfm);
		codes.add(
				Codes.Lambda(cfm, target, operands, nid),
				attributes(expr));
		return target;
	}

	private int generate(Expr.ConstantAccess expr, Environment environment,
			Code.Block codes, Context context) throws ResolveError {
		Constant val = expr.value;
		int target = environment.allocate(val.type());
		codes.add(Codes.Const(target, val), attributes(expr));
		return target;
	}

	private int generate(Expr.LocalVariable expr, Environment environment,
			Code.Block codes, Context context) throws ResolveError {

		if (environment.get(expr.var) != null) {
			Type type = expr.result().raw();
			int operand = environment.get(expr.var);
			int target = environment.allocate(type);
			codes.add(Codes.Assign(type, target, operand), attributes(expr));
			return target;
		} else {
			syntaxError(errorMessage(VARIABLE_POSSIBLY_UNITIALISED), context,
					expr);
			return -1;
		}
	}

	private int generate(Expr.UnOp expr, Environment environment, Code.Block codes, Context context) {
		int operand = generate(expr.mhs, environment, codes, context);
		int target = environment.allocate(expr.result().raw());
		switch (expr.op) {
		case NEG:
			codes.add(Codes.UnaryOperator(expr.result().raw(), target, operand,
					Codes.UnaryOperatorKind.NEG), attributes(expr));
			break;
		case INVERT:
			codes.add(Codes.Invert(expr.result().raw(), target, operand),
					attributes(expr));
			break;
		case NOT:
			String falseLabel = CodeUtils.freshLabel();
			String exitLabel = CodeUtils.freshLabel();
			generateCondition(falseLabel, expr.mhs, environment, codes, context);
			codes.add(Codes.Const(target, Constant.V_BOOL(true)),
					attributes(expr));
			codes.add(Codes.Goto(exitLabel));
			codes.add(Codes.Label(falseLabel));
			codes.add(Codes.Const(target, Constant.V_BOOL(false)),
					attributes(expr));
			codes.add(Codes.Label(exitLabel));
			break;
		default:
			// should be dead-code
			internalFailure("unexpected unary operator encountered", context,
					expr);
			return -1;
		}
		return target;
	}

	private int generate(Expr.LengthOf expr, Environment environment,
			Code.Block codes, Context context) {
		int operand = generate(expr.src, environment, codes, context);
		int target = environment.allocate(expr.result().raw());
		codes.add(Codes.LengthOf(expr.srcType.raw(), target, operand),
				attributes(expr));
		return target;
	}

	private int generate(Expr.Dereference expr, Environment environment,
			Code.Block codes, Context context) {
		int operand = generate(expr.src, environment, codes, context);
		int target = environment.allocate(expr.result().raw());
		codes.add(Codes.Dereference(expr.srcType.raw(), target, operand),
				attributes(expr));
		return target;
	}

	private int generate(Expr.IndexOf expr, Environment environment, Code.Block codes, Context context) {
		int srcOperand = generate(expr.src, environment, codes, context);
		int idxOperand = generate(expr.index, environment, codes, context);
		int target = environment.allocate(expr.result().raw());
		codes.add(Codes.IndexOf(expr.srcType.raw(), target, srcOperand,
				idxOperand), attributes(expr));
		return target;
	}

	private int generate(Expr.Cast expr, Environment environment, Code.Block codes, Context context) {
		int operand = generate(expr.expr, environment, codes, context);
		Type from = expr.expr.result().raw();
		Type to = expr.result().raw();
		int target = environment.allocate(to);
		// TODO: include constraints
		codes.add(Codes.Convert(from, target, operand, to), attributes(expr));
		return target;
	}

	private int generate(Expr.BinOp v, Environment environment, Code.Block codes, Context context)
			throws Exception {

		// could probably use a range test for this somehow
		if (v.op == Expr.BOp.EQ || v.op == Expr.BOp.NEQ || v.op == Expr.BOp.LT
				|| v.op == Expr.BOp.LTEQ || v.op == Expr.BOp.GT
				|| v.op == Expr.BOp.GTEQ || v.op == Expr.BOp.SUBSET
				|| v.op == Expr.BOp.SUBSETEQ || v.op == Expr.BOp.ELEMENTOF
				|| v.op == Expr.BOp.AND || v.op == Expr.BOp.OR) {
			String trueLabel = CodeUtils.freshLabel();
			String exitLabel = CodeUtils.freshLabel();
			generateCondition(trueLabel, v, environment, codes, context);
			int target = environment.allocate(Type.T_BOOL);
			codes.add(Codes.Const(target, Constant.V_BOOL(false)),
					attributes(v));
			codes.add(Codes.Goto(exitLabel));
			codes.add(Codes.Label(trueLabel));
			codes.add(Codes.Const(target, Constant.V_BOOL(true)),
					attributes(v));
			codes.add(Codes.Label(exitLabel));
			return target;

		} else {

			Expr.BOp bop = v.op;
			int leftOperand = generate(v.lhs, environment, codes, context);
			int rightOperand = generate(v.rhs, environment, codes, context);
			Type result = v.result().raw();
			int target = environment.allocate(result);

			switch (bop) {
			case UNION:
				codes.add(Codes.SetOperator((Type.EffectiveSet) result, target,
						leftOperand, rightOperand, Codes.SetOperatorKind.UNION),
						attributes(v));
				break;

			case INTERSECTION:
				codes.add(Codes
						.SetOperator((Type.EffectiveSet) result, target,
								leftOperand, rightOperand,
								Codes.SetOperatorKind.INTERSECTION), attributes(v));
				break;

			case DIFFERENCE:
				codes.add(Codes.SetOperator((Type.EffectiveSet) result, target,
						leftOperand, rightOperand, Codes.SetOperatorKind.DIFFERENCE),
						attributes(v));
				break;

			case LISTAPPEND:
				codes.add(Codes.ListOperator((Type.EffectiveList) result,
						target, leftOperand, rightOperand,
						Codes.ListOperatorKind.APPEND), attributes(v));
				break;

			case STRINGAPPEND:
				Type lhs = v.lhs.result().raw();
				Type rhs = v.rhs.result().raw();
				Codes.StringOperatorKind op;
				if (lhs == Type.T_STRING && rhs == Type.T_STRING) {
					op = Codes.StringOperatorKind.APPEND;
				} else if (lhs == Type.T_STRING
						&& Type.isSubtype(Type.T_CHAR, rhs)) {
					op = Codes.StringOperatorKind.LEFT_APPEND;
				} else if (rhs == Type.T_STRING
						&& Type.isSubtype(Type.T_CHAR, lhs)) {
					op = Codes.StringOperatorKind.RIGHT_APPEND;
				} else {
					// this indicates that one operand must be explicitly
					// converted
					// into a string.
					op = Codes.StringOperatorKind.APPEND;
				}
				codes.add(
						Codes.StringOperator(target, leftOperand, rightOperand, op),
						attributes(v));
				break;

			default:
				codes.add(Codes.BinaryOperator(result, target, leftOperand,
						rightOperand, OP2BOP(bop, v, context)), attributes(v));
			}

			return target;
		}
	}

	private int generate(Expr.Set expr, Environment environment, Code.Block codes, Context context) {
		int[] operands = generate(expr.arguments, environment, codes, context);
		int target = environment.allocate(expr.result().raw());
		codes.add(Codes.NewSet(expr.type.raw(), target, operands),
				attributes(expr));
		return target;
	}

	private int generate(Expr.List expr, Environment environment, Code.Block codes, Context context) {
		int[] operands = generate(expr.arguments, environment, codes, context);
		int target = environment.allocate(expr.result().raw());
		codes.add(Codes.NewList(expr.type.raw(), target, operands),
				attributes(expr));
		return target;
	}

	private int generate(Expr.SubList expr, Environment environment, Code.Block codes, Context context) {
		int srcOperand = generate(expr.src, environment, codes, context);
		int startOperand = generate(expr.start, environment, codes, context);
		int endOperand = generate(expr.end, environment, codes, context);
		int target = environment.allocate(expr.result().raw());
		codes.add(Codes.SubList(expr.type.raw(), target, srcOperand,
				startOperand, endOperand), attributes(expr));
		return target;
	}

	private int generate(Expr.SubString v, Environment environment, Code.Block codes, Context context) {
		int srcOperand = generate(v.src, environment, codes, context);
		int startOperand = generate(v.start, environment, codes, context);
		int endOperand = generate(v.end, environment, codes, context);
		int target = environment.allocate(v.result().raw());
		codes.add(
				Codes.SubString(target, srcOperand, startOperand, endOperand),
				attributes(v));
		return target;
	}

	private int generate(Expr.Comprehension e, Environment environment,
			Code.Block codes, Context context) {

		// First, check for boolean cases which are handled mostly by
		// generateCondition.
		if (e.cop == Expr.COp.SOME || e.cop == Expr.COp.NONE || e.cop == Expr.COp.ALL) {
			String trueLabel = CodeUtils.freshLabel();
			String exitLabel = CodeUtils.freshLabel();
			generateCondition(trueLabel, e, environment, codes, context);
			int target = environment.allocate(Type.T_BOOL);
			codes.add(Codes.Const(target, Constant.V_BOOL(false)),
					attributes(e));
			codes.add(Codes.Goto(exitLabel));
			codes.add(Codes.Label(trueLabel));
			codes.add(Codes.Const(target, Constant.V_BOOL(true)),
					attributes(e));
			codes.add(Codes.Label(exitLabel));
			return target;
		} else {

			// Ok, non-boolean case.
			ArrayList<Triple<Integer, Integer, Type.EffectiveCollection>> slots = new ArrayList();

			for (Pair<String, Expr> p : e.sources) {
				Expr src = p.second();
				Type.EffectiveCollection rawSrcType = (Type.EffectiveCollection) src
						.result().raw();
				int varSlot = environment.allocate(rawSrcType.element(),
						p.first());
				int srcSlot;

				if (src instanceof Expr.LocalVariable) {
					// this is a little optimisation to produce slightly better
					// code.
					Expr.LocalVariable v = (Expr.LocalVariable) src;
					if (environment.get(v.var) != null) {
						srcSlot = environment.get(v.var);
					} else {
						// fall-back plan ...
						srcSlot = generate(src, environment, codes, context);
					}
				} else {
					srcSlot = generate(src, environment, codes, context);
				}
				slots.add(new Triple(varSlot, srcSlot, rawSrcType));
			}

			Type resultType;
			int target = environment.allocate(e.result().raw());

			if (e.cop == Expr.COp.LISTCOMP) {
				resultType = e.type.raw();
				codes.add(Codes.NewList((Type.List) resultType, target,
						Collections.EMPTY_LIST), attributes(e));
			} else {
				resultType = e.type.raw();
				codes.add(Codes.NewSet((Type.Set) resultType, target,
						Collections.EMPTY_LIST), attributes(e));
			}

			// At this point, it would be good to determine an appropriate loop
			// invariant for a set comprehension. This is easy enough in the
			// case of
			// a single variable comprehension, but actually rather difficult
			// for a
			// multi-variable comprehension.
			//
			// For example, consider <code>{x+y | x in xs, y in ys, x<0 &&
			// y<0}</code>
			//
			// What is an appropriate loop invariant here?

			String continueLabel = CodeUtils.freshLabel();
			ArrayList<String> labels = new ArrayList<String>();
			String loopLabel = CodeUtils.freshLabel();

			for (Triple<Integer, Integer, Type.EffectiveCollection> p : slots) {
				String label = loopLabel + "$" + p.first();
				codes.add(Codes.ForAll(p.third(), p.second(), p.first(),
						Collections.EMPTY_LIST, label), attributes(e));
				labels.add(label);
			}

			if (e.condition != null) {
				generateCondition(continueLabel, invert(e.condition),
						environment, codes, context);
			}

			int operand = generate(e.value, environment, codes, context);

			// FIXME: following broken for list comprehensions
			codes.add(Codes.SetOperator((Type.Set) resultType, target, target,
					operand, Codes.SetOperatorKind.LEFT_UNION), attributes(e));

			if (e.condition != null) {
				codes.add(Codes.Label(continueLabel));
			}

			for (int i = (labels.size() - 1); i >= 0; --i) {
				// Must add NOP before loop end to ensure labels at the boundary
				// get written into Wyil files properly. See Issue #253.
				codes.add(Codes.Nop);
				codes.add(Codes.LoopEnd(labels.get(i)));
			}

			return target;
		}
	}

	private int generate(Expr.Record expr, Environment environment, Code.Block codes, Context context) {
		ArrayList<String> keys = new ArrayList<String>(expr.fields.keySet());
		Collections.sort(keys);
		int[] operands = new int[expr.fields.size()];
		for (int i = 0; i != operands.length; ++i) {
			String key = keys.get(i);
			Expr arg = expr.fields.get(key);
			operands[i] = generate(arg, environment, codes, context);
		}
		int target = environment.allocate(expr.result().raw());
		codes.add(Codes.NewRecord(expr.result().raw(), target, operands),
				attributes(expr));
		return target;
	}

	private int generate(Expr.Tuple expr, Environment environment, Code.Block codes, Context context) {
		int[] operands = generate(expr.fields, environment, codes, context);
		int target = environment.allocate(expr.result().raw());
		codes.add(Codes.NewTuple(expr.result().raw(), target, operands),
				attributes(expr));
		return target;
	}

	private int generate(Expr.Map expr, Environment environment, Code.Block codes, Context context) {
		int[] operands = new int[expr.pairs.size() * 2];
		for (int i = 0; i != expr.pairs.size(); ++i) {
			Pair<Expr, Expr> e = expr.pairs.get(i);
			operands[i << 1] = generate(e.first(), environment, codes, context);
			operands[(i << 1) + 1] = generate(e.second(), environment, codes, context);
		}
		int target = environment.allocate(expr.result().raw());
		codes.add(Codes.NewMap(expr.result().raw(), target, operands),
				attributes(expr));
		return target;
	}

	private int generate(Expr.FieldAccess expr, Environment environment,
			Code.Block codes, Context context) {
		int operand = generate(expr.src, environment, codes, context);
		int target = environment.allocate(expr.result().raw());
		codes.add(
				Codes.FieldLoad(expr.srcType.raw(), target, operand, expr.name),
				attributes(expr));
		return target;
	}

	private int generate(Expr.New expr, Environment environment, Code.Block codes, Context context)
			throws ResolveError {
		int operand = generate(expr.expr, environment, codes, context);
		int target = environment.allocate(expr.result().raw());
		codes.add(Codes.NewObject(expr.type.raw(), target, operand));
		return target;
	}

	private int[] generate(List<Expr> arguments, Environment environment,
			Code.Block codes, Context context) {
		int[] operands = new int[arguments.size()];
		for (int i = 0; i != operands.length; ++i) {
			Expr arg = arguments.get(i);
			operands[i] = generate(arg, environment, codes, context);
		}
		return operands;
	}

	// =========================================================================
	// Helpers
	// =========================================================================		
	
	private Codes.BinaryOperatorKind OP2BOP(Expr.BOp bop, SyntacticElement elem, Context context) {
		switch (bop) {
		case ADD:
			return Codes.BinaryOperatorKind.ADD;
		case SUB:
			return Codes.BinaryOperatorKind.SUB;
		case MUL:
			return Codes.BinaryOperatorKind.MUL;
		case DIV:
			return Codes.BinaryOperatorKind.DIV;
		case REM:
			return Codes.BinaryOperatorKind.REM;
		case RANGE:
			return Codes.BinaryOperatorKind.RANGE;
		case BITWISEAND:
			return Codes.BinaryOperatorKind.BITWISEAND;
		case BITWISEOR:
			return Codes.BinaryOperatorKind.BITWISEOR;
		case BITWISEXOR:
			return Codes.BinaryOperatorKind.BITWISEXOR;
		case LEFTSHIFT:
			return Codes.BinaryOperatorKind.LEFTSHIFT;
		case RIGHTSHIFT:
			return Codes.BinaryOperatorKind.RIGHTSHIFT;
		}
		syntaxError(errorMessage(INVALID_BINARY_EXPRESSION), context, elem);
		return null;
	}

	private Codes.Comparator OP2COP(Expr.BOp bop, SyntacticElement elem, Context context) {
		switch (bop) {
		case EQ:
			return Codes.Comparator.EQ;
		case NEQ:
			return Codes.Comparator.NEQ;
		case LT:
			return Codes.Comparator.LT;
		case LTEQ:
			return Codes.Comparator.LTEQ;
		case GT:
			return Codes.Comparator.GT;
		case GTEQ:
			return Codes.Comparator.GTEQ;
		case SUBSET:
			return Codes.Comparator.SUBSET;
		case SUBSETEQ:
			return Codes.Comparator.SUBSETEQ;
		case ELEMENTOF:
			return Codes.Comparator.IN;
		}
		syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context, elem);
		return null;
	}

	private static int allocate(HashMap<String, Integer> environment) {
		return allocate("$" + environment.size(), environment);
	}

	private static int allocate(String var, HashMap<String, Integer> environment) {
		// this method is a bit of a hack
		Integer r = environment.get(var);
		if (r == null) {
			int slot = environment.size();
			environment.put(var, slot);
			return slot;
		} else {
			return r;
		}
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
	private static Code.Block chainBlock(String target, Code.Block blk) {
		Code.Block nblock = new Code.Block(blk.numInputs());
		for (Code.Block.Entry e : blk) {
			if (e.code instanceof Codes.Assert) {
				Codes.Assert a = (Codes.Assert) e.code;
				Codes.Comparator iop = CodeUtils.invert(a.op);
				if (iop != null) {
					nblock.add(Codes.If(a.type, a.leftOperand,
							a.rightOperand, iop, target), e.attributes());
				} else {
					// FIXME: avoid the branch here. This can be done by
					// ensuring that every Codes.COp is invertible.
					String lab = CodeUtils.freshLabel();
					nblock.add(Codes.If(a.type, a.leftOperand,
							a.rightOperand, a.op, lab), e.attributes());
					nblock.add(Codes.Goto(target));
					nblock.add(Codes.Label(lab));
				}
			} else {
				nblock.add(e.code, e.attributes());
			}
		}
		return CodeUtils.relabel(nblock);
	}
	
	/**
	 * The shiftBlock method takes a block and shifts every slot a given amount
	 * to the right. The number of inputs remains the same. This method is used 
	 * 
	 * @param amount
	 * @param blk
	 * @return
	 */
	private static Code.Block shiftBlock(int amount, Code.Block blk) {
		HashMap<Integer,Integer> binding = new HashMap<Integer,Integer>();
		for(int i=0;i!=blk.numSlots();++i) {
			binding.put(i,i+amount);
		}
		Code.Block nblock = new Code.Block(blk.numInputs());
		for(Code.Block.Entry e : blk) {
			Code code = e.code.remap(binding);
			nblock.add(code,e.attributes());
		}
		return CodeUtils.relabel(nblock);
	}
	
	/**
	 * The purpose of this method is to construct aliases for variables declared
	 * as part of type patterns. For example:
	 * 
	 * <pre>
	 * type tup as {int x, int y} where x < y
	 * </pre>
	 * 
	 * Here, variables <code>x</code> and <code>y</code> are declared as part of
	 * the type pattern, and we translate them into the aliases : $.x and $.y,
	 * where "$" is the root variable passed as a parameter.
	 * 
	 * @param src
	 * @param t
	 * @param environment
	 */
	public static void addDeclaredVariables(int root, TypePattern pattern, Type type,
			Environment environment, Code.Block blk) {
		
		if(pattern instanceof TypePattern.Record) {
			TypePattern.Record tp = (TypePattern.Record) pattern;
			Type.Record tt = (Type.Record) type;
			for(TypePattern.Leaf element : tp.elements) {
				String fieldName = element.var.var;
				Type fieldType = tt.field(fieldName);
				int target = environment.allocate(fieldType);
				blk.add(Codes.FieldLoad(tt, target, root, fieldName));
				addDeclaredVariables(target, element, fieldType, environment, blk);							
			}
		} else if(pattern instanceof TypePattern.Tuple){
			TypePattern.Tuple tp = (TypePattern.Tuple) pattern;
			Type.Tuple tt = (Type.Tuple) type;
			for(int i=0;i!=tp.elements.size();++i) {
				TypePattern element = tp.elements.get(i);
				Type elemType = tt.element(i);
				int target = environment.allocate(elemType);
				blk.add(Codes.TupleLoad(tt, target, root, i));
				addDeclaredVariables(target, element, elemType, environment, blk);							
			}
		} else if(pattern instanceof TypePattern.Rational){
			TypePattern.Rational tp = (TypePattern.Rational) pattern;
			int num = environment.allocate(Type.T_INT);
			int den = environment.allocate(Type.T_INT);
			blk.add(Codes.UnaryOperator(Type.T_REAL, num, root, Codes.UnaryOperatorKind.NUMERATOR));
			blk.add(Codes.UnaryOperator(Type.T_REAL, den, root, Codes.UnaryOperatorKind.DENOMINATOR));
			addDeclaredVariables(num,tp.numerator,Type.T_INT,environment,blk);
			addDeclaredVariables(den,tp.denominator,Type.T_INT,environment,blk);			
		} else {
			// do nothing for leaf
			TypePattern.Leaf lp = (TypePattern.Leaf) pattern;
			if (lp.var != null) {
				environment.put(root, lp.var.var);
			}
		}				
	}
	
	private static final Code.Block EMPTY_BLOCK = new Code.Block(1);
	

	private static Expr invert(Expr e) {
		if (e instanceof Expr.BinOp) {
			Expr.BinOp bop = (Expr.BinOp) e;
			Expr.BinOp nbop = null;
			switch (bop.op) {
			case AND:
				nbop = new Expr.BinOp(Expr.BOp.OR, invert(bop.lhs),
						invert(bop.rhs), attributes(e));
				break;
			case OR:
				nbop = new Expr.BinOp(Expr.BOp.AND, invert(bop.lhs),
						invert(bop.rhs), attributes(e));
				break;
			case EQ:
				nbop = new Expr.BinOp(Expr.BOp.NEQ, bop.lhs, bop.rhs,
						attributes(e));
				break;
			case NEQ:
				nbop = new Expr.BinOp(Expr.BOp.EQ, bop.lhs, bop.rhs,
						attributes(e));
				break;
			case LT:
				nbop = new Expr.BinOp(Expr.BOp.GTEQ, bop.lhs, bop.rhs,
						attributes(e));
				break;
			case LTEQ:
				nbop = new Expr.BinOp(Expr.BOp.GT, bop.lhs, bop.rhs,
						attributes(e));
				break;
			case GT:
				nbop = new Expr.BinOp(Expr.BOp.LTEQ, bop.lhs, bop.rhs,
						attributes(e));
				break;
			case GTEQ:
				nbop = new Expr.BinOp(Expr.BOp.LT, bop.lhs, bop.rhs,
						attributes(e));
				break;
			}
			if (nbop != null) {
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
	private static Code.Block shiftBlockExceptionZero(int amount, int zeroDest, Code.Block blk) {
		HashMap<Integer,Integer> binding = new HashMap<Integer,Integer>();
		for(int i=1;i!=blk.numSlots();++i) {
			binding.put(i,i+amount);		
		}
		binding.put(0, zeroDest);
		
		Code.Block nblock = new Code.Block(blk.numInputs());
		for(Code.Block.Entry e : blk) {
			Code code = e.code.remap(binding);
			nblock.add(code,e.attributes());
		}
		return CodeUtils.relabel(nblock);
	}
	
	/**
	 * The attributes method extracts those attributes of relevance to WyIL, and
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
	
	public static final class Environment {
		private final HashMap<String, Integer> var2idx;
		private final ArrayList<Type> idx2type;

		public Environment() {
			var2idx = new HashMap<String, Integer>();
			idx2type = new ArrayList<Type>();
		}
		
		public Environment(Environment env) {
			var2idx = new HashMap<String, Integer>(env.var2idx);
			idx2type = new ArrayList<Type>(env.idx2type);
		}
		
		public int allocate(Type t) {
			int idx = idx2type.size();
			idx2type.add(t);
			return idx;
		}

		public int allocate(Type t, String v) {
			int r = allocate(t);
			var2idx.put(v, r);
			return r;
		}

		public int size() {
			return idx2type.size();
		}

		public Integer get(String v) {
			return var2idx.get(v);
		}

		public String get(int idx) {
			for (Map.Entry<String, Integer> e : var2idx.entrySet()) {
				int jdx = e.getValue();
				if (jdx == idx) {
					return e.getKey();
				}
			}
			return null;
		}

		public void put(int idx, String v) {
			var2idx.put(v, idx);
		}

		public ArrayList<Type> asList() {
			return idx2type;
		}

		public String toString() {
			return idx2type.toString() + "," + var2idx.toString();
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
