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
 * function f(nat x) -> int:
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
public final class CodeGenerator {

	/**
	 * The lambdas are anonymous functions used within statements and
	 * expressions in the source file. These are compiled into anonymised WyIL
	 * functions, since WyIL does not have an internal notion of a lambda.
	 */
	private final ArrayList<WyilFile.FunctionOrMethod> lambdas = new ArrayList<WyilFile.FunctionOrMethod>();

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
	public CodeGenerator(WhileyBuilder builder, FlowTypeChecker resolver) {
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
					declarations.add(generate((WhileyFile.FunctionOrMethod) d));
				}
			} catch (SyntaxError se) {
				throw se;
			} catch (Throwable ex) {
				WhileyFile.internalFailure(ex.getMessage(), (WhileyFile.Context) d, d, ex);
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
	private WyilFile.Constant generate(WhileyFile.Constant cd) {
		return new WyilFile.Constant(cd.modifiers(), cd.name(), cd.resolvedValue);
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
	private WyilFile.Type generate(WhileyFile.Type td) throws Exception {
		EnclosingScope scope = new EnclosingScope(td);
		// Allocate declared parameter
		scope.allocate(td.resolvedType, td.parameter.name());
		// Generate code for each invariant condition
		for (Expr invariant : td.invariant) {
			generateInvariantBlock(invariant, scope.createRootBlock());
		}
		// done
		return new WyilFile.Type(td.modifiers(), td.name(), td.resolvedType.nominal(), scope.getForest());
	}

	// =========================================================================
	// Function / Method Declarations
	// =========================================================================

	private WyilFile.FunctionOrMethod generate(WhileyFile.FunctionOrMethod fd) throws Exception {
		// ==================================================================
		// Construct environments
		// ==================================================================
		EnclosingScope scope = new EnclosingScope(fd);
		addDeclaredParameters(fd.parameters, fd.resolvedType().params(), scope);
		addDeclaredParameters(fd.returns, fd.resolvedType().returns(), scope);

		// ==================================================================
		// Generate pre-condition
		// ==================================================================
		for (Expr precondition : fd.requires) {
			generateInvariantBlock(precondition, scope.createRootBlock());			
		}

		// ==================================================================
		// Generate post-condition
		// ==================================================================
		for (Expr postcondition : fd.ensures) {
			generateInvariantBlock(postcondition, scope.createRootBlock());
		}

		// ==================================================================
		// Generate body
		// ==================================================================

		scope = scope.createRootBlock();
		for (Stmt s : fd.statements) {
			generate(s, scope);
		}

		// The following is sneaky. It guarantees that every method ends in a
		// return. For methods that actually need a value, this is either
		// removed as dead-code or remains and will cause an error.
		scope.add(new Bytecode.Return(), attributes(fd));

		WyilFile.FunctionOrMethod declaration;

		if (fd instanceof WhileyFile.Function) {
			WhileyFile.Function f = (WhileyFile.Function) fd;
			declaration = new WyilFile.FunctionOrMethod(fd.modifiers(), fd.name(), f.resolvedType.nominal(),
					scope.getForest(), fd.requires.size(), fd.ensures.size());
		} else {
			WhileyFile.Method md = (WhileyFile.Method) fd;
			declaration = new WyilFile.FunctionOrMethod(fd.modifiers(), fd.name(), md.resolvedType.nominal(),
					scope.getForest(), fd.requires.size(), fd.ensures.size());
		}

		// Done.
		return declaration;
	}

	/**
	 * Construct a new code block in a given forest corresponding to a
	 * precondition, postcondition or type invariant.
	 * 
	 * @param invariant
	 * @param scope
	 */
	private int generateInvariantBlock(Expr invariant, EnclosingScope scope) {
		String endLab = freshLabel();
		generateCondition(endLab, invariant, scope);
		scope.add(new Bytecode.Fail(), attributes(invariant));
		scope.add(new Bytecode.Label(endLab));
		scope.add(new Bytecode.Return());
		return scope.blockIndex();
	}

	/**
	 * Add a list of parameter declarations to a given environment
	 *
	 * @param parameters
	 *            --- List of parameters to add
	 * @param types
	 *            --- List of nominal parameter types
	 * @param declarations
	 *            --- List of declarations being constructed
	 */
	private void addDeclaredParameters(List<WhileyFile.Parameter> parameters, List<Nominal> types,
			EnclosingScope scope) {
		for (int i = 0; i != parameters.size(); ++i) {
			WhileyFile.Parameter parameter = parameters.get(i);
			// allocate parameter to register in the current block
			scope.allocate(types.get(i), parameter.name);
		}
	}

	// =========================================================================
	// Statements
	// =========================================================================

	/**
	 * Translate a source-level statement into a WyIL block, using a given
	 * environment mapping named variables to slots.
	 *
	 * @param stmt
	 *            --- Statement to be translated.
	 * @param scope
	 *            --- Enclosing scope of this statement.
	 * @return
	 */
	private void generate(Stmt stmt, EnclosingScope scope) {
		try {
			if (stmt instanceof VariableDeclaration) {
				generate((VariableDeclaration) stmt, scope);
			} else if (stmt instanceof Assign) {
				generate((Assign) stmt, scope);
			} else if (stmt instanceof Assert) {
				generate((Assert) stmt, scope);
			} else if (stmt instanceof Assume) {
				generate((Assume) stmt, scope);
			} else if (stmt instanceof Return) {
				generate((Return) stmt, scope);
			} else if (stmt instanceof Debug) {
				generate((Debug) stmt, scope);
			} else if (stmt instanceof Fail) {
				generate((Fail) stmt, scope);
			} else if (stmt instanceof IfElse) {
				generate((IfElse) stmt, scope);
			} else if (stmt instanceof Switch) {
				generate((Switch) stmt, scope);
			} else if (stmt instanceof Break) {
				generate((Break) stmt, scope);
			} else if (stmt instanceof Continue) {
				generate((Continue) stmt, scope);
			} else if (stmt instanceof NamedBlock) {
				generate((NamedBlock) stmt, scope);
			} else if (stmt instanceof While) {
				generate((While) stmt, scope);
			} else if (stmt instanceof DoWhile) {
				generate((DoWhile) stmt, scope);
			} else if (stmt instanceof Expr.FunctionOrMethodCall) {
				generate((Expr.Multi) stmt, scope);
			} else if (stmt instanceof Expr.IndirectFunctionOrMethodCall) {
				generate((Expr.Multi) stmt, scope);
			} else if (stmt instanceof Expr.New) {
				generate((Expr.New) stmt, scope);
			} else if (stmt instanceof Skip) {
				generate((Skip) stmt, scope);
			} else {
				// should be dead-code
				WhileyFile.internalFailure("unknown statement: " + stmt.getClass().getName(), scope.getSourceContext(), stmt);
			}
		} catch (ResolveError rex) {
			internalFailure(rex.getMessage(), scope.getSourceContext(), stmt, rex);
		} catch (SyntaxError sex) {
			throw sex;
		} catch (Exception ex) {
			internalFailure(ex.getMessage(), scope.getSourceContext(), stmt, ex);
		}
	}

	/**
	 * Translate a variable declaration statement into a WyIL block. This only
	 * has an effect if an initialiser expression is given; otherwise, it's
	 * effectively a no-op. Consider the following variable declaration:
	 *
	 * <pre>
	 * int v = x + 1
	 * </pre>
	 *
	 * This might be translated into the following WyIL bytecodes:
	 *
	 * <pre>
	 * const %3 = 1
	 * add %4 = %0, %3
	 * return %4
	 * </pre>
	 *
	 * Here, we see that variable <code>v</code> is allocated to register 4,
	 * whilst variable <code>x</code> is allocated to register 0.
	 *
	 * @param stmt
	 *            --- Statement to be translated.
	 * @param scope
	 *            --- Enclosing scope of this statement.
	 * @return
	 */
	private void generate(VariableDeclaration s, EnclosingScope scope) {
		// First, we allocate this variable to a given slot in the environment.		
		int[] targets = { scope.allocate(s.type,s.parameter.name) };
		// Second, translate initialiser expression if it exists.
		if (s.expr != null) {
			int[] operands = { generate(s.expr, scope) };
			scope.add(new Bytecode.Operator(s.expr.result().raw(), targets, operands, Bytecode.OperatorKind.ASSIGN),
					attributes(s));
		}
	}

	/**
	 * Translate an assignment statement into a WyIL block. This must consider
	 * the different forms of assignment which are permitted in Whiley,
	 * including:
	 *
	 * <pre>
	 * x = e     // variable assignment
	 * x,y = e   // tuple assignment
	 * x.f = e   // field assignment
	 * x[i] = e  // index-of assignment
	 * </pre>
	 *
	 * As an example, consider the following index assignment:
	 *
	 * <pre>
	 * xs[i + 1] = 1
	 * </pre>
	 *
	 * This might be translated into the following WyIL bytecodes:
	 *
	 * <pre>
	 * const %2 = 1
	 * const %4 = 1
	 * add %5 = %0, %4
	 * update %1[%5] %2
	 * const %6 = 0
	 * return %6
	 * </pre>
	 *
	 * Here, variable <code>i</code> is allocated to register 0, whilst variable
	 * <code>xs</code> is allocated to register 1. The result of the index
	 * expression <code>i+1</code> is stored in the temporary register 5.
	 *
	 * @param stmt
	 *            --- Statement to be translated.
	 * @param scope
	 *            --- Enclosing scope of this statement (i.e. type, constant,
	 *            function or method declaration). The scope is used to aid
	 *            with error reporting as it determines the enclosing file.
	 * @return
	 */
	private void generate(Assign s, EnclosingScope scope) {
		// First, we translate all right-hand side expressions and assign them
		// to temporary registers.
		ArrayList<Integer> operands = new ArrayList<Integer>();
		ArrayList<Type> types = new ArrayList<Type>();
		for (int i = 0; i != s.rvals.size(); ++i) {
			Expr e = s.rvals.get(i);
			// FIXME: this is a rather ugly
			if (e instanceof Expr.Multi) {
				// The assigned expression actually has multiple returns,
				// therefore extract them all.
				Expr.Multi me = (Expr.Multi) e;
				for (Nominal t : me.returns()) {
					types.add(t.raw());
				}
				operands.addAll(toIntegerList(generate(me, scope)));
			} else {
				// The assigned rval is a simple expression which returns a
				// single value
				operands.add(generate(e, scope));
				types.add(e.result().raw());
			}
		}

		// Second, update each expression on left-hand side of this assignment
		// appropriately. Note that we can safely assume here the number of
		// rvals and lvals matches as this has already been checked by
		// FlowTypeChecker.
		for (int i = 0; i != s.lvals.size(); ++i) {
			Expr.LVal lval = s.lvals.get(i);
			generateAssignment(lval, operands.get(i), types.get(i), scope);
		}
	}

	public void generateAssignment(Expr.LVal lval, int operand, Type type, EnclosingScope scope) {
		if (lval instanceof Expr.AssignedVariable) {
			Expr.AssignedVariable v = (Expr.AssignedVariable) lval;
			// This is the easiest case. Having translated the right-hand side
			// expression, we now assign it directly to the register allocated
			// for variable on the left-hand side.
			int[] targets = new int[] { scope.get(v.var) };
			int[] operands = new int[] { operand };
			scope.add(new Bytecode.Operator(type, targets, operands, Bytecode.OperatorKind.ASSIGN), attributes(lval));
		} else if (lval instanceof Expr.IndexOf || lval instanceof Expr.FieldAccess
				|| lval instanceof Expr.Dereference) {
			// This is the more complicated case, since the left-hand side
			// expression is recursive. However, the WyIL update bytecode comes
			// to the rescue here. All we need to do is extract the variable
			// being updated and give this to the update bytecode. For example,
			// in the expression "x.y.f = e" we have that variable "x" is being
			// updated.
			ArrayList<String> fields = new ArrayList<String>();
			ArrayList<Integer> operands = new ArrayList<Integer>();
			Expr.AssignedVariable lhs = extractLVal(lval, fields, operands, scope);
			int target = scope.get(lhs.var);
			scope.add(new Bytecode.Update(lhs.type.raw(), target, toIntArray(operands), operand, lhs.afterType.raw(),
					fields), attributes(lval));
		} else {
			internalFailure("invalid assignment", scope.getSourceContext(), lval);
		}
	}

	/**
	 * This function recurses down the left-hand side of an assignment (e.g.
	 * x[i] = e, x.f = e, etc) with a complex lval. The primary goal is to
	 * identify the left-most variable which is actually being updated. A
	 * secondary goal is to collect the sequence of field names being updated,
	 * and translate any index expressions and store them in temporary
	 * registers.
	 *
	 * @param e
	 *            The LVal being extract from.
	 * @param fields
	 *            The list of fields being used in the assignment. Initially,
	 *            this is empty and is filled by this method as it traverses the
	 *            lval.
	 * @param operands
	 *            The list of temporary registers in which evaluated index
	 *            expression are stored. Initially, this is empty and is filled
	 *            by this method as it traverses the lval.
	 * @param scope
	 *            Enclosing scope of this statement.
	 * @return
	 */
	private Expr.AssignedVariable extractLVal(Expr e, ArrayList<String> fields, ArrayList<Integer> operands,
			EnclosingScope scope) {

		if (e instanceof Expr.AssignedVariable) {
			Expr.AssignedVariable v = (Expr.AssignedVariable) e;
			return v;
		} else if (e instanceof Expr.Dereference) {
			Expr.Dereference pa = (Expr.Dereference) e;
			return extractLVal(pa.src, fields, operands, scope);
		} else if (e instanceof Expr.IndexOf) {
			Expr.IndexOf la = (Expr.IndexOf) e;
			int operand = generate(la.index, scope);
			Expr.AssignedVariable l = extractLVal(la.src, fields, operands, scope);
			operands.add(operand);
			return l;
		} else {
			Expr.FieldAccess ra = (Expr.FieldAccess) e;
			Expr.AssignedVariable r = extractLVal(ra.src, fields, operands, scope);
			fields.add(ra.name);
			return r;
		} 
	}

	/**
	 * Translate an assert statement into WyIL bytecodes.
	 *
	 * @param stmt
	 *            --- Statement to be translated.
	 * @param scope
	 *            --- Enclosing scope of this statement.
	 * @return
	 */
	private void generate(Stmt.Assert s, EnclosingScope scope) {
		// First, create assert block body
		EnclosingScope subscope = scope.createBlock();
		String endLab = freshLabel();
		generateCondition(endLab, s.expr, subscope);
		subscope.add(new Bytecode.Fail(), attributes(s.expr));
		subscope.add(new Bytecode.Label(endLab));
		// Second, create assert bytecode
		scope.add(new Bytecode.Assert(subscope.blockIndex()), attributes(s));
	}

	/**
	 * Translate an assume statement into WyIL bytecodes.
	 *
	 * @param stmt
	 *            --- Statement to be translated.
	 * @param scope
	 *            --- Enclosing scope of this statement.
	 * @return
	 */
	private void generate(Stmt.Assume s, EnclosingScope scope) {
		// First, create assume block body
		EnclosingScope subscope = scope.createBlock();
		String endLab = freshLabel();
		generateCondition(endLab, s.expr, subscope);
		subscope.add(new Bytecode.Fail(), attributes(s.expr));
		subscope.add(new Bytecode.Label(endLab));
		// Second, create assert bytecode
		scope.add(new Bytecode.Assume(subscope.blockIndex()), attributes(s));
	}

	/**
	 * Translate a return statement into WyIL bytecodes. In the case that a
	 * return expression is provided, then this is first translated and stored
	 * in a temporary register. Consider the following return statement:
	 *
	 * <pre>
	 * return i * 2
	 * </pre>
	 *
	 * This might be translated into the following WyIL bytecodes:
	 *
	 * <pre>
	 * const %3 = 2
	 * mul %4 = %0, %3
	 * return %4
	 * </pre>
	 *
	 * Here, we see that variable <code>I</code> is allocated to register 0,
	 * whilst the result of the expression <code>i * 2</code> is stored in
	 * register 4.
	 *
	 * @param stmt
	 *            --- Statement to be translated.
	 * @param scope
	 *            --- Enclosing scope of this statement.
	 * @return
	 */
	private void generate(Stmt.Return s, EnclosingScope scope) {
		List<Expr> returns = s.returns;
		// Here, we don't put the type propagated for the return expression.
		// Instead, we use the declared return type of this function. This
		// has the effect of forcing an implicit coercion between the
		// actual value being returned and its required type.
		List<Type> returnTypes = scope.getEnclosingFunctionType().raw().returns();
		Type[] types = returnTypes.toArray(new Type[returnTypes.size()]);
		int[] operands = new int[types.length];
		int index = 0;
		for (int i = 0; i != returns.size(); ++i) {
			Expr e = returns.get(i);
			// FIXME: this is a rather ugly
			if (e instanceof Expr.Multi) {
				int[] results = generate((Expr.Multi) e, scope);
				for (int r : results) {
					operands[index++] = r;
				}
			} else {
				operands[index++] = generate(e, scope);
			}
		}
		scope.add(new Bytecode.Return(types, operands), attributes(s));
	}

	/**
	 * Translate a skip statement into a WyIL nop bytecode.
	 *
	 * @param stmt
	 *            --- Statement to be translated.
	 * @param scope
	 *            --- Enclosing scope of this statement.
	 * @return
	 */
	private void generate(Stmt.Skip s, EnclosingScope scope) {
		// TODO: should actually generate a NOP bytecode. This is an assignment
		// from zero operands to zero targets. At the moment, I cannot encode
		// this however because it will fail in the interpreter.
	}

	/**
	 * Translate a debug statement into WyIL bytecodes. The debug expression is
	 * first translated and stored in a temporary register. Consider the
	 * following debug statement:
	 *
	 * <pre>
	 * debug "Hello World"
	 * </pre>
	 *
	 * This might be translated into the following WyIL bytecodes:
	 *
	 * <pre>
	 * const %2 = "Hello World"
	 * debug %2
	 * </pre>
	 *
	 * Here, we see that debug expression is first stored into the temporary
	 * register 2.
	 *
	 * @param stmt
	 *            --- Statement to be translated.
	 * @param scope
	 *            --- Enclosing scope of this statement.
	 * @return
	 */
	private void generate(Stmt.Debug s, EnclosingScope scope) {
		int operand = generate(s.expr, scope);
		scope.add(new Bytecode.Debug(operand), attributes(s));
	}

	/**
	 * Translate a fail statement into WyIL bytecodes.
	 *
	 * <pre>
	 * fail
	 * </pre>
	 *
	 * A fail statement is always translated into a WyIL fail bytecode:
	 *
	 * <pre>
	 * fail
	 * </pre>
	 *
	 * @param stmt
	 *            --- Statement to be translated.
	 * @param scope
	 *            --- Enclosing scope of this statement.
	 * @return
	 */
	private void generate(Stmt.Fail s, EnclosingScope scope) {
		scope.add(new Bytecode.Fail(), attributes(s));
	}

	/**
	 * Translate an if statement into WyIL bytecodes. This is done by first
	 * translating the condition into one or more conditional branches. The true
	 * and false blocks are then translated and marked with labels. Finally, an
	 * exit label is provided to catch the fall-through case. Consider the
	 * following if statement:
	 *
	 * <pre>
	 * if x+1 < 2:
	 *     x = x + 1
	 * ...
	 * </pre>
	 *
	 * This might be translated into the following WyIL bytecodes:
	 *
	 * <pre>
	 * const %3 = 1
	 * add %4 = %0, %3
	 * const %5 = 2
	 * ifge %4, %5 goto label0
	 * const %7 = 1
	 * add %8 = %0, %7
	 * assign %0 = %8
	 * .label0
	 *    ...
	 * </pre>
	 *
	 * Here, we see that result of the condition is stored into temporary
	 * register 4, which is then used in the comparison. In the case the
	 * condition is false, control jumps over the true block; otherwise, it
	 * enters the true block and then (because there is no false block) falls
	 * through.
	 *
	 * @param stmt
	 *            --- Statement to be translated.
	 * @param scope
	 *            --- Enclosing scope of this statement.
	 * @return
	 */
	private void generate(Stmt.IfElse s, EnclosingScope scope) {
		// We need to clone the scope's here to isolate variables declared in
		// the true/false branches from the enclosing scope. In particular,
		// the case where two variables of the same name are declared with
		// different types.
		EnclosingScope trueScope = scope.clone();
		EnclosingScope falseScope = scope.clone();
		String falseLab = freshLabel();
		String exitLab = s.falseBranch.isEmpty() ? falseLab : freshLabel();

		generateCondition(falseLab, invert(s.condition), scope);

		for (Stmt st : s.trueBranch) {
			generate(st, trueScope);
		}
		if (!s.falseBranch.isEmpty()) {
			scope.add(new Bytecode.Goto(exitLab));
			scope.add(new Bytecode.Label(falseLab));
			for (Stmt st : s.falseBranch) {
				generate(st, falseScope);
			}
		}

		scope.add(new Bytecode.Label(exitLab));
	}

	/**
	 * Translate a break statement into a WyIL unconditional branch bytecode.
	 * This requires examining the scope stack to determine the correct target
	 * for the branch. Consider the following use of a break statement:
	 *
	 * <pre>
	 * while x < 10:
	 *    if x == 0:
	 *       break
	 *    x = x + 1
	 * ...
	 * </pre>
	 *
	 * This might be translated into the following WyIL bytecodes:
	 *
	 * <pre>
	 * loop (%0)
	 *     const %3 = 10
	 *     ifge %0, %3 goto label0
	 *     const %5 = 0
	 *     ifne %0, %5 goto label1
	 *     goto label0
	 *     .label1
	 *     const %7 = 1
	 *     add %8 = %0, %7
	 *     assign %0 = %8
	 * .label0
	 * ...
	 * </pre>
	 *
	 * Here, we see that the break statement is translated into the bytecode
	 * "goto label0", which exits the loop.
	 *
	 * @param stmt
	 *            --- Statement to be translated.
	 * @param scope
	 *            --- Enclosing scope of this statement.
	 * @return
	 */
	private void generate(Stmt.Break s, EnclosingScope scope) {
		String breakLabel = scope.getBreakLabel();		
		scope.add(new Bytecode.Goto(breakLabel));
	}

	/**
	 * Translate a continue statement into a WyIL unconditional branch bytecode.
	 * This requires examining the scope stack to determine the correct target
	 * for the branch. Consider the following use of a continue statement:
	 *
	 * <pre>
	 * while x < 10:
	 *    x = x + 1
	 *    if x == 0:
	 *       continue
	 *    ...
	 * ...
	 * </pre>
	 *
	 * This might be translated into the following WyIL bytecodes:
	 *
	 * <pre>
	 * loop (%0)
	 *     const %3 = 10
	 *     ifge %0, %3 goto label0
	 *     const %7 = 1
	 *     add %8 = %0, %7
	 *     assign %0 = %8
	 *     const %5 = 0
	 *     ifne %0, %5 goto label1
	 *     goto label2
	 *     .label1
	 *     ...
	 *     .label2
	 * .label0
	 * ...
	 * </pre>
	 *
	 * Here, we see that the continue statement is translated into the bytecode
	 * "goto label2", which skips the loop body for one iteration.
	 *
	 * @param stmt
	 *            --- Statement to be translated.
	 * @param scope
	 *            --- Enclosing scope of this statement.
	 * @return
	 */
	private void generate(Stmt.Continue s, EnclosingScope scope) {
		String continueLabel = scope.getContinueLabel();		
		scope.add(new Bytecode.Goto(continueLabel));
	}

	/**
	 * Translate a switch statement into WyIL bytecodes. This is done by first
	 * translating the switch expression and storing its result in a temporary
	 * register. Then, each case is translated in order of appearance. Consider
	 * the following switch statement:
	 *
	 * <pre>
	 * switch x+1:
	 *     case 0,1:
	 *         return x+1
	 *     case 2:
	 *         x = x - 1
	 *     default:
	 *         x = 0
	 * </pre>
	 *
	 * This might be translated into the following WyIL bytecodes:
	 *
	 * <pre>
	 *     const %2 = 1
	 *     add %3 = %0, %2
	 *     switch %3 0->label1, 1->label1, 2->label2, *->label0
	 * .label1
	 *     const %3 = 1
	 *     add %4 = %0, %3
	 *     return %4
	 * .label2
	 *     const %6 = 1
	 *     sub %7 = %0, %6
	 *     assign %0 = %7
	 *     goto label3
	 * .label0
	 *     const %8 = 0
	 *     assign %0 = %8
	 *     goto label3
	 * .label3
	 * </pre>
	 *
	 * Here, we see that switch expression is first stored into the temporary
	 * register 3. Then, each of the values 0 -- 2 is routed to the start of its
	 * block, with * representing the default case.
	 *
	 * @param stmt
	 *            --- Statement to be translated.
	 * @param scope
	 *            --- Enclosing scope of this statement.
	 * @return
	 */
	private void generate(Stmt.Switch s, EnclosingScope scope) throws Exception {
		String exitLab = freshLabel();
		int operand = generate(s.expr, scope);
		String defaultTarget = exitLab;
		ArrayList<Pair<Constant, String>> cases = new ArrayList<>();
		int start = scope.getBlock().size();

		// FIXME: the following check should really occur earlier in the
		// pipeline. However, it is difficult to do it earlier because it's only
		// after FlowTypeChecker that we have determined the concrete values.
		// See #628
		checkNoDuplicateLabels(s.cases,scope);
		
		for (Stmt.Case c : s.cases) {
			if (c.expr.isEmpty()) {
				// A case with an empty match represents the default label. We
				// must check that we have not already seen a case with an empty
				// match (otherwise, we'd have two default labels ;)
				defaultTarget = freshLabel();
				scope.add(new Bytecode.Label(defaultTarget), attributes(c));
				// We need to clone the scope here to isolate variables
				// declared in the default block from the enclosing scope
				EnclosingScope defaultScope = scope.clone();
				for (Stmt st : c.stmts) {
					generate(st, defaultScope);
				}
				scope.add(new Bytecode.Goto(exitLab), attributes(c));
			} else if (defaultTarget == exitLab) {
				String target = freshLabel();
				scope.add(new Bytecode.Label(target), attributes(c));

				// Case statements in Whiley may have multiple matching constant
				// values. Therefore, we iterate each matching value and
				// construct a mapping from that to a label indicating the start
				// of the case body.

				for (Constant constant : c.constants) {					
					cases.add(new Pair<>(constant, target));
				}
				// We need to clone the scope here to isolate variables
				// declared in the case block from the enclosing scope
				EnclosingScope caseScope = scope.clone();
				for (Stmt st : c.stmts) {
					generate(st, caseScope);
				}
				scope.add(new Bytecode.Goto(exitLab), attributes(c));

			} else {
				// This represents the case where we have another non-default
				// case after the default case. Such code cannot be executed,
				// and is therefore reported as an error.
				internalFailure(errorMessage(UNREACHABLE_CODE), scope.getSourceContext(), c);
			}
		}

		scope.add(start, new Bytecode.Switch(s.expr.result().raw(), operand, defaultTarget, cases), attributes(s));
		scope.add(new Bytecode.Label(exitLab), attributes(s));
	}

	/**
	 * Check that not two case statements have the same constant label.  
	 * 
	 * @param cases
	 * @param indent
	 */
	private void checkNoDuplicateLabels(List<Stmt.Case> cases, EnclosingScope scope) {
		// The set of seen case labels captures those which have been seen
		// already in some previous case block. Thus, if we see one again
		// then we have a syntax error.
		HashSet<Constant> labels = new HashSet<Constant>();
		//
		for(int i=0;i!=cases.size();++i) {
			Stmt.Case caseBlock = cases.get(i);
			List<Constant> caseLabels = caseBlock.constants;
			if(caseLabels != null) {
				for (int j = 0; j != caseLabels.size(); ++j) {
					Constant c = caseLabels.get(j);
					if (labels.contains(c)) {
						WhileyFile.syntaxError(errorMessage(DUPLICATE_CASE_LABEL), scope.getSourceContext(), caseBlock);
					} else {
						labels.add(c);
					}
				}
			}
		}
	}
	
	
	/**
	 * Translate a named block into WyIL bytecodes.
	 */
	private void generate(Stmt.NamedBlock s, EnclosingScope scope) {
		for (Stmt st : s.body) {
			generate(st, scope);
		}
	}

	/**
	 * Translate a while loop into WyIL bytecodes. Consider the following use of
	 * a while statement:
	 *
	 * <pre>
	 * while x < 10:
	 *    x = x + 1
	 * ...
	 * </pre>
	 *
	 * This might be translated into the following WyIL bytecodes:
	 *
	 * <pre>
	 * loop (%0)
	 *     const %3 = 10
	 *     ifge %0, %3 goto label0
	 *     const %7 = 1
	 *     add %8 = %0, %7
	 *     assign %0 = %8
	 * .label0
	 * ...
	 * </pre>
	 *
	 * Here, we see that the evaluated loop condition is stored into temporary
	 * register 3 and that the condition is implemented using a conditional
	 * branch. Note that there is no explicit goto statement at the end of the
	 * loop body which loops back to the head (this is implicit in the loop
	 * bytecode).
	 *
	 * @param stmt
	 *            --- Statement to be translated.
	 * @param scope
	 *            --- Enclosing scope of this statement.
	 * @return
	 */
	private void generate(Stmt.While s, EnclosingScope scope) {
		// A label marking where execution continues after the while
		// loop finishes. Used when the loop condition evaluates to false
		// or when a break statement is encountered.
		String exitLab = freshLabel();
		// A label marking the end of the current loop iteration. Used
		// by the continue statement.
		String continueLab = freshLabel();

		EnclosingScope subscope = scope.createBlock(exitLab,continueLab);

		for (Expr condition : s.invariants) {
			int invariant = generateInvariantBlock(condition,subscope.createBlock());
			subscope.add(new Bytecode.Invariant(invariant), attributes(condition));
		}

		generateCondition(exitLab, invert(s.condition), subscope);

		for (Stmt st : s.body) {
			generate(st, subscope);
		}

		subscope.add(new Bytecode.Label(continueLab), attributes(s));
		scope.add(new Bytecode.Loop(new int[] {}, subscope.blockIndex()), attributes(s));
		scope.add(new Bytecode.Label(exitLab), attributes(s));
	}

	/**
	 * Translate a do-while loop into WyIL bytecodes. Consider the following use
	 * of a do-while statement:
	 *
	 * <pre>
	 * do:
	 *    x = x + 1
	 * while x < 10
	 * ...
	 * </pre>
	 *
	 * This might be translated into the following WyIL bytecodes:
	 *
	 * <pre>
	 * loop (%0)
	 *     const %2 = 1
	 *     add %3 = %0, %2
	 *     assign %0 = %3
	 *     const %5 = 10
	 *     ifge %3, %5 goto label0
	 * .label0
	 * ...
	 * </pre>
	 *
	 * Here, we see that the evaluated loop condition is stored into temporary
	 * register 3 and that the condition is implemented using a conditional
	 * branch. Note that there is no explicit goto statement at the end of the
	 * loop body which loops back to the head (this is implicit in the loop
	 * bytecode).
	 *
	 * @param stmt
	 *            --- Statement to be translated.
	 * @param scope
	 *            --- Enclosing scope of this statement.
	 * @return
	 */
	private void generate(Stmt.DoWhile s, EnclosingScope scope) {
		// A label marking where execution continues after the do-while
		// loop finishes. Used when the loop condition evaluates to false
		// or when a break statement is encountered.
		String exitLab = freshLabel();
		// A label marking the end of the current loop iteration. Used
		// by the continue statement.
		String continueLab = freshLabel();

		EnclosingScope subscope = scope.createBlock(exitLab,continueLab);

		for (Stmt st : s.body) {
			generate(st, subscope);
		}

		for (Expr condition : s.invariants) {
			int invariant = generateInvariantBlock(condition, subscope.createBlock());
			subscope.add(new Bytecode.Invariant(invariant), attributes(condition));
		}

		subscope.add(new Bytecode.Label(continueLab), attributes(s));
		generateCondition(exitLab, invert(s.condition), subscope);

		scope.add(new Bytecode.Loop(new int[] {}, subscope.blockIndex()), attributes(s));
		scope.add(new Bytecode.Label(exitLab), attributes(s));
	}

	// =========================================================================
	// Conditions
	// =========================================================================

	/**
	 * Translate a source-level condition into a WyIL block, using a given
	 * environment mapping named variables to slots. If the condition evaluates
	 * to true, then control is transferred to the given target. Otherwise,
	 * control will fall through to the following bytecode. This method is
	 * necessary because the WyIL bytecode implementing comparisons are only
	 * available as conditional branches. For example, consider this if
	 * statement:
	 *
	 * <pre>
	 * if x < y || x == y:
	 *     x = y
	 * else:
	 *     x = -y
	 * </pre>
	 *
	 * This might be translated into the following WyIL bytecodes:
	 *
	 * <pre>
	 *     iflt %0, %1 goto label0
	 *     ifne %0, %1 goto label1
	 * .label0
	 *     assign %0 = %1
	 *     goto label2
	 * .label1
	 *     neg %8 = %1
	 *     assign %0 = %8
	 * .label2
	 * </pre>
	 *
	 * Here, we see that the condition "x < y || x == y" is broken down into two
	 * conditional branches (which additionally implement short-circuiting). The
	 * branches are carefully selected implement the semantics of the logical OR
	 * operator '||'. This function is responsible for translating conditional
	 * expressions like this into sequences of conditional branches using
	 * short-circuiting.
	 *
	 * @param target
	 *            --- Target label to goto if condition is true. When the
	 *            condition is false, control falls simply through to the next
	 *            bytecode in sqeuence.
	 * @param condition
	 *            --- Source-level condition to be translated into a sequence of
	 *            one or more conditional branches.
	 * @param scope
	 *            --- Enclosing scope of the condition
	 * @return
	 */
	public void generateCondition(String target, Expr condition, EnclosingScope scope) {
		try {

			// First, we see whether or not we can employ a special handler for
			// translating this condition.

			if (condition instanceof Expr.Constant) {
				generateCondition(target, (Expr.Constant) condition, scope);
			} else if (condition instanceof Expr.UnOp) {
				generateCondition(target, (Expr.UnOp) condition, scope);
			} else if (condition instanceof Expr.BinOp) {
				generateCondition(target, (Expr.BinOp) condition, scope);
			} else if (condition instanceof Expr.Quantifier) {
				generateCondition(target, (Expr.Quantifier) condition, scope);
			} else if (condition instanceof Expr.ConstantAccess || condition instanceof Expr.LocalVariable
					|| condition instanceof Expr.AbstractInvoke || condition instanceof Expr.AbstractIndirectInvoke
					|| condition instanceof Expr.FieldAccess || condition instanceof Expr.IndexOf) {

				// This is the default case where no special handler applies. In
				// this case, we simply compares the computed value against
				// true. In some cases, we could actually do better. For
				// example, !(x < 5) could be rewritten into x >= 5.

				int result = generate(condition, scope);
				scope.add(new Bytecode.If(Type.T_BOOL, result, target), attributes(condition));

			} else {
				internalFailure(errorMessage(INVALID_BOOLEAN_EXPRESSION), scope.getSourceContext(), condition);
			}

		} catch (SyntaxError se) {
			throw se;
		} catch (Exception ex) {
			internalFailure(ex.getMessage(), scope.getSourceContext(), condition, ex);
		}

	}

	/**
	 * <p>
	 * Translate a source-level condition which is a constant (i.e.
	 * <code>true</code> or <code>false</code>) into a WyIL block, using a given
	 * environment mapping named variables to slots. This may seem like a
	 * perverse case, but it is permitted to allow selective commenting of code.
	 * </p>
	 *
	 * <p>
	 * When the constant is true, an unconditional branch to the target is
	 * generated. Otherwise, nothing is generated and control falls through to
	 * the next bytecode in sequence.
	 * </p>
	 *
	 * @param target
	 *            --- Target label to goto if condition is true. When the
	 *            condition is false, control falls simply through to the next
	 *            bytecode in sqeuence.
	 * @param condition
	 *            --- Source-level condition to be translated into a sequence of
	 *            one or more conditional branches.
	 * @param scope
	 *            --- Enclosing scope of the condition
	 * @return
	 */
	private void generateCondition(String target, Expr.Constant c, EnclosingScope scope) {
		Constant.Bool b = (Constant.Bool) c.value;
		if (b.value()) {
			scope.add(new Bytecode.Goto(target));
		} else {
			// do nout
		}
	}

	/**
	 * <p>
	 * Translate a source-level condition which is a binary expression into WyIL
	 * bytecodes, using a given environment mapping named variables to slots.
	 * </p>
	 *
	 * @param target
	 *            --- Target label to goto if condition is true. When the
	 *            condition is false, control falls simply through to the next
	 *            bytecode in sqeuence.
	 * @param condition
	 *            --- Source-level condition to be translated into a sequence of
	 *            one or more conditional branches.
	 * @param scope
	 *            --- Enclosing scope of the condition
	 * @return
	 */
	private void generateCondition(String target, Expr.BinOp v, EnclosingScope scope) throws Exception {

		Expr.BOp bop = v.op;

		if (bop == Expr.BOp.OR) {
			generateCondition(target, v.lhs, scope);
			generateCondition(target, v.rhs, scope);

		} else if (bop == Expr.BOp.AND) {
			String exitLabel = freshLabel();
			generateCondition(exitLabel, invert(v.lhs), scope);
			generateCondition(target, v.rhs, scope);
			scope.add(new Bytecode.Label(exitLabel));

		} else if (bop == Expr.BOp.IS) {
			generateTypeCondition(target, v, scope);

		} else {
			if (bop == Expr.BOp.EQ && v.lhs instanceof Expr.LocalVariable && v.rhs instanceof Expr.Constant
					&& ((Expr.Constant) v.rhs).value == Constant.Null) {
				// this is a simple rewrite to enable type inference.
				Expr.LocalVariable lhs = (Expr.LocalVariable) v.lhs;
				int slot = scope.get(lhs.var);
				scope.add(new Bytecode.IfIs(v.srcType.raw(), slot, Type.T_NULL, target), attributes(v));
			} else if (bop == Expr.BOp.NEQ && v.lhs instanceof Expr.LocalVariable
					&& v.rhs instanceof Expr.Constant && ((Expr.Constant) v.rhs).value == Constant.Null) {
				// this is a simple rewrite to enable type inference.
				String exitLabel = freshLabel();
				Expr.LocalVariable lhs = (Expr.LocalVariable) v.lhs;				
				int slot = scope.get(lhs.var);
				scope.add(new Bytecode.IfIs(v.srcType.raw(), slot, Type.T_NULL, exitLabel), attributes(v));
				scope.add(new Bytecode.Goto(target));
				scope.add(new Bytecode.Label(exitLabel));
			} else {
				int result = generate(v, scope);
				scope.add(new Bytecode.If(v.srcType.raw(), result, target), attributes(v));
			}
		}
	}

	/**
	 * <p>
	 * Translate a source-level condition which represents a runtime type test
	 * (e.g. <code>x is int</code>) into WyIL bytecodes, using a given
	 * environment mapping named variables to slots. One subtlety of this arises
	 * when the lhs is a single variable. In this case, the variable will be
	 * retyped and, in order for this to work, we *must* perform the type test
	 * on the actual varaible, rather than a temporary.
	 * </p>
	 *
	 * @param target
	 *            --- Target label to goto if condition is true. When the
	 *            condition is false, control falls simply through to the next
	 *            bytecode in sqeuence.
	 * @param condition
	 *            --- Source-level binary condition to be translated into a
	 *            sequence of one or more conditional branches.
	 * @param scope
	 *            --- Enclosing scope of the condition
	 * @return
	 */
	private void generateTypeCondition(String target, Expr.BinOp condition, EnclosingScope scope) throws Exception {
		int leftOperand;

		if (condition.lhs instanceof Expr.LocalVariable) {
			// This is the case where the lhs is a single variable and, hence,
			// will be retyped by this operation. In this case, we must operate
			// on the original variable directly, rather than a temporary
			// variable (since, otherwise, we'll retype the temporary but not
			// the intended variable).
			Expr.LocalVariable lhs = (Expr.LocalVariable) condition.lhs;			
			leftOperand = scope.get(lhs.var);
		} else {
			// This is the general case whether the lhs is an arbitrary variable
			// and, hence, retyping does not apply. Therefore, we can simply
			// evaluate the lhs into a temporary register as per usual.
			leftOperand = generate(condition.lhs, scope);
		}

		// Note, the type checker guarantees that the rhs is a type val, so the
		// following cast is always safe.
		Expr.TypeVal rhs = (Expr.TypeVal) condition.rhs;

		scope.add(new Bytecode.IfIs(condition.srcType.raw(), leftOperand, rhs.type.nominal(), target), attributes(condition));
	}

	/**
	 * <p>
	 * Translate a source-level condition which represents a unary condition
	 * into WyIL bytecodes, using a given environment mapping named variables to
	 * slots. Note, the only valid unary condition is logical not. To implement
	 * this, we simply generate the underlying condition and reroute its branch
	 * targets.
	 * </p>
	 *
	 * @param target
	 *            --- Target label to goto if condition is true. When the
	 *            condition is false, control falls simply through to the next
	 *            bytecode in sqeuence.
	 * @param condition
	 *            --- Source-level condition to be translated into a sequence of
	 *            one or more conditional branches.
	 * @param scope
	 *            --- Enclosing scope of the condition
	 * @return
	 */
	private void generateCondition(String target, Expr.UnOp v, EnclosingScope scope) {
		Expr.UOp uop = v.op;
		switch (uop) {
		case NOT:
			// What we do is generate the underlying expression whilst setting
			// its true destination to a temporary label. Then, for the fall
			// through case we branch to our true destination.

			String label = freshLabel();
			generateCondition(label, v.mhs, scope);
			scope.add(new Bytecode.Goto(target));
			scope.add(new Bytecode.Label(label));
			return;
		default:
			// Nothing else is a valud boolean condition here.
			internalFailure(errorMessage(INVALID_BOOLEAN_EXPRESSION), scope.getSourceContext(), v);
		}
	}

	/**
	 * <p>
	 * Translate a source-level condition which represents a quantifier
	 * expression into WyIL bytecodes, using a given environment mapping named
	 * variables to slots.
	 * </p>
	 *
	 * @param target
	 *            --- Target label to goto if condition is true. When the
	 *            condition is false, control falls simply through to the next
	 *            bytecode in sqeuence.
	 * @param condition
	 *            --- Source-level condition to be translated into a sequence of
	 *            one or more conditional branches.
	 * @param scope
	 *            --- Enclosing scope of the condition
	 * @return
	 */
	private void generateCondition(String target, Expr.Quantifier e, EnclosingScope scope) {

		String exit = freshLabel();
		// Note, we must clone the scope below at this point. This is to avoid
		// the variable name percolating into the enclosing scope.  
		generate(e.sources.iterator(), target, exit, e, scope.clone());

		switch (e.cop) {
		case NONE:
			scope.add(new Bytecode.Goto(target));
			scope.add(new Bytecode.Label(exit));
			break;
		case SOME:
			break;
		case ALL:
			scope.add(new Bytecode.Goto(target));
			scope.add(new Bytecode.Label(exit));
			break;
		}
	}

	private void generate(Iterator<Triple<String, Expr, Expr>> srcIterator, String trueLabel, String falseLabel,
			Expr.Quantifier e, EnclosingScope scope) {

		if (srcIterator.hasNext()) {
			// This is the inductive case (i.e. an outer loop)
			Triple<String, Expr, Expr> src = srcIterator.next();

			// First, determine the src slot.
			int varSlot = scope.allocate(Nominal.T_INT);
			// FIXME: the following line is a hack to deal with the relatively
			// primitive way that VcGenerator determines the type of a variable.
			// This should be removed when VcGenerator is reworked. 
			scope.environment.put(src.first(), varSlot);
			//
			int startSlot = generate(src.second(), scope);
			int endSlot = generate(src.third(), scope);

			// Second, recursively generate remaining parts
			EnclosingScope subscope = scope.createBlock();
			generate(srcIterator, trueLabel, falseLabel, e, subscope);
			// Finally, create the forall loop bytecode
			scope.add(new Bytecode.Quantify(startSlot, endSlot, varSlot, new int[0], subscope.blockIndex()), attributes(e));
		} else {
			// This is the base case (i.e. the innermost loop)
			switch (e.cop) {
			case NONE:
				generateCondition(falseLabel, e.condition, scope);
				break;
			case SOME:
				generateCondition(trueLabel, e.condition, scope);
				break;
			case ALL:
				generateCondition(falseLabel, invert(e.condition), scope);
				break;
			}
		}
	}

	// =========================================================================
	// Multi-Expressions
	// =========================================================================

	public int[] generate(Expr.Multi expression, EnclosingScope scope) {
		List<Nominal> returns = expression.returns();
		int[] targets = new int[returns.size()];
		for (int i = 0; i != targets.length; ++i) {
			targets[i] = scope.allocate(returns.get(i));
		}
		try {
			if (expression instanceof Expr.FunctionOrMethodCall) {
				Expr.FunctionOrMethodCall fmc = (Expr.FunctionOrMethodCall) expression;
				generateStmt(fmc, scope, targets);
			} else if (expression instanceof Expr.IndirectFunctionOrMethodCall) {
				Expr.IndirectFunctionOrMethodCall fmc = (Expr.IndirectFunctionOrMethodCall) expression;
				generateStmt(fmc, scope, targets);
			} else {
				// should be dead-code
				internalFailure("unknown expression: " + expression.getClass().getName(), scope.getSourceContext(), expression);
			}
		} catch (ResolveError rex) {
			internalFailure(rex.getMessage(), scope.getSourceContext(), expression, rex);
		} catch (SyntaxError se) {
			throw se;
		} catch (Exception ex) {
			internalFailure(ex.getMessage(), scope.getSourceContext(), expression, ex);
		}
		// done
		return targets;
	}

	public void generateStmt(Expr.FunctionOrMethodCall expr, EnclosingScope scope, int... targets) throws ResolveError {
		//
		int[] operands = generate(expr.arguments, scope);
		scope.add(new Bytecode.Invoke(expr.type().nominal(), targets, operands, expr.nid()), attributes(expr));
	}

	public void generateStmt(Expr.IndirectFunctionOrMethodCall expr, EnclosingScope scope, int... targets) throws ResolveError {
		//
		int operand = generate(expr.src, scope);
		int[] operands = generate(expr.arguments, scope);
		scope.add(new Bytecode.IndirectInvoke(expr.type().raw(), targets, operand, operands), attributes(expr));
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
	 * @param scope
	 *            --- Enclosing scope of the condition
	 *
	 * @return --- the register
	 */
	public int generate(Expr expression, EnclosingScope scope) {
		try {
			if (expression instanceof Expr.Constant) {
				return generate((Expr.Constant) expression, scope);
			} else if (expression instanceof Expr.LocalVariable) {
				return generate((Expr.LocalVariable) expression, scope);
			} else if (expression instanceof Expr.ConstantAccess) {
				return generate((Expr.ConstantAccess) expression, scope);
			} else if (expression instanceof Expr.ArrayInitialiser) {
				return generate((Expr.ArrayInitialiser) expression, scope);
			} else if (expression instanceof Expr.ArrayGenerator) {
				return generate((Expr.ArrayGenerator) expression, scope);
			} else if (expression instanceof Expr.BinOp) {
				return generate((Expr.BinOp) expression, scope);
			} else if (expression instanceof Expr.Dereference) {
				return generate((Expr.Dereference) expression, scope);
			} else if (expression instanceof Expr.Cast) {
				return generate((Expr.Cast) expression, scope);
			} else if (expression instanceof Expr.IndexOf) {
				return generate((Expr.IndexOf) expression, scope);
			} else if (expression instanceof Expr.UnOp) {
				return generate((Expr.UnOp) expression, scope);
			} else if (expression instanceof Expr.FunctionOrMethodCall) {
				return generate((Expr.FunctionOrMethodCall) expression, scope);
			} else if (expression instanceof Expr.IndirectFunctionCall) {
				return generate((Expr.IndirectFunctionCall) expression, scope);
			} else if (expression instanceof Expr.IndirectMethodCall) {
				return generate((Expr.IndirectMethodCall) expression, scope);
			} else if (expression instanceof Expr.Quantifier) {
				return generate((Expr.Quantifier) expression, scope);
			} else if (expression instanceof Expr.FieldAccess) {
				return generate((Expr.FieldAccess) expression, scope);
			} else if (expression instanceof Expr.Record) {
				return generate((Expr.Record) expression, scope);
			} else if (expression instanceof Expr.FunctionOrMethod) {
				return generate((Expr.FunctionOrMethod) expression, scope);
			} else if (expression instanceof Expr.Lambda) {
				return generate((Expr.Lambda) expression, scope);
			} else if (expression instanceof Expr.New) {
				return generate((Expr.New) expression, scope);
			} else {
				// should be dead-code
				internalFailure("unknown expression: " + expression.getClass().getName(), scope.getSourceContext(), expression);
			}
		} catch (ResolveError rex) {
			internalFailure(rex.getMessage(), scope.getSourceContext(), expression, rex);
		} catch (SyntaxError se) {
			throw se;
		} catch (Exception ex) {
			internalFailure(ex.getMessage(), scope.getSourceContext(), expression, ex);
		}

		return -1; // deadcode
	}

	public int generate(Expr.FunctionOrMethodCall expr, EnclosingScope scope) throws ResolveError {
		int target = scope.allocate(expr.result());
		generateStmt(expr, scope, target);
		return target;
	}

	public int generate(Expr.IndirectFunctionOrMethodCall expr, EnclosingScope scope) throws ResolveError {
		int target = scope.allocate(expr.result());
		generateStmt(expr, scope, target);
		return target;
	}

	private int generate(Expr.Constant expr, EnclosingScope scope) {
		Constant val = expr.value;
		int target = scope.allocate(Nominal.construct(val.type(),val.type()));
		scope.add(new Bytecode.Const(target, expr.value), attributes(expr));
		return target;
	}

	private int generate(Expr.FunctionOrMethod expr, EnclosingScope scope) {
		Type.FunctionOrMethod nominalType = expr.type.nominal();
		int target = scope.allocate(expr.type);
		scope.add(new Bytecode.Lambda(nominalType, target, new int[0], expr.nid), attributes(expr));
		return target;
	}

	private int generate(Expr.Lambda expr, EnclosingScope scope) {
		Nominal.FunctionOrMethod lambdaType = expr.type;
		Type.FunctionOrMethod rawLambdaType = lambdaType.raw();

		// Variables contains the list of variables from the enclosing scope
		// which are used in the lambda body
		ArrayList<String> variables = new ArrayList<String>();
		// Create a new root scope for the lambda body
		EnclosingScope lambdaScope = new EnclosingScope(scope.getSourceContext()).createRootBlock();
		Type.FunctionOrMethod concreteLambdaType = determineLambdaParametersAndOperands(expr,variables,lambdaScope);		
		// Generate body based on current environment
		if (lambdaType.returns().isEmpty()) {
			lambdaScope.add(new Bytecode.Return(), attributes(expr));
		} else {
			int target = generate(expr.body, lambdaScope);
			lambdaScope
					.add(new Bytecode.Return(rawLambdaType.returns().toArray(new Type[rawLambdaType.returns().size()]),
							target), attributes(expr));
		}

		// Construct private lambda function using generated body
		int id = expr.attribute(Attribute.Source.class).start;
		String name = "$lambda" + id;
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.PRIVATE);
		WyilFile.FunctionOrMethod lambda = new WyilFile.FunctionOrMethod(modifiers, name, concreteLambdaType,
				lambdaScope.getForest(), 0, 0, attributes(expr));
		lambdas.add(lambda);
		Path.ID mid = scope.getSourceContext().file().module;
		NameID nid = new NameID(mid, name);
		// Initialise the operands array
		int[] operands = new int[variables.size()];
		for(int i=0;i!=operands.length;++i) {
			operands[i] = scope.get(variables.get(i));
		}
		// Finally, create the lambda
		int target = scope.allocate(lambdaType);
		scope.add(new Bytecode.Lambda(concreteLambdaType, target, operands, nid), attributes(expr));
		return target;
	}

	/**
	 * Determine the set of parameters for the lambda function itself. This
	 * includes the declared parameters in the lambda expression, but also any
	 * variables from the enclosing scope which are used within the lambda. For
	 * example:
	 * 
	 * <pre>
	 * type FunT is function(int)->int
	 * 
	 * method f(int x) -> FunT:
	 *   return &(int y -> x + y)
	 * </pre>
	 * 
	 * Here, the concrete lambda type is function(int,int)->int, where the first
	 * parameter is <code>y</code> and the second is <code>x</code>.
	 * 
	 * @param expr
	 * @param operands
	 * @param scope
	 * @return
	 */
	private Type.FunctionOrMethod determineLambdaParametersAndOperands(Expr.Lambda expr, List<String> operands, EnclosingScope scope) {
		Nominal.FunctionOrMethod lambdaType = expr.type;
		Type.FunctionOrMethod rawLambdaType = lambdaType.raw();
		List<WhileyFile.Parameter> lambdaParameters = expr.parameters;
		ArrayList<Nominal> paramTypes = new ArrayList<Nominal>(lambdaType.params());
		// First, add declared parameters
		HashSet<String> declaredVariables = new HashSet<String>();
		for (int i = 0; i != lambdaParameters.size(); ++i) {
			WhileyFile.Parameter parameter = lambdaParameters.get(i);
			// allocate parameter to register in the current block
			scope.allocate(paramTypes.get(i), parameter.name);
			declaredVariables.add(parameter.name);
		}
		// Second add used variables (which are then parameters)
		for (Pair<Nominal, String> v : Exprs.uses(expr.body, scope.getSourceContext())) {
			if (!declaredVariables.contains(v.second())) {
				scope.allocate(v.first(), v.second());
				paramTypes.add(v.first());
				operands.add(v.second());
				declaredVariables.add(v.second());
			}
		}
		// Convert all nominal parameters to raw parameters (ugly)
		ArrayList<Type> rawParamTypes = new ArrayList<Type>();
		for(Nominal t : paramTypes) { rawParamTypes.add(t.raw()); }
		// Finally, create the concrete lambda type		 
		if(lambdaType instanceof Nominal.Function) {
			return Type.Function(rawLambdaType.returns(),rawParamTypes);
		} else {
			return Type.Method(rawLambdaType.returns(),rawLambdaType.contextLifetimes(),
					rawLambdaType.lifetimeParams(),rawParamTypes);
		}
	}
	
	private int generate(Expr.ConstantAccess expr, EnclosingScope scope) throws ResolveError {
		Constant val = expr.value;
		int target = scope.allocate(Nominal.construct(val.type(),val.type()));
		scope.add(new Bytecode.Const(target, val), attributes(expr));
		return target;
	}

	private int generate(Expr.LocalVariable expr, EnclosingScope scope) throws ResolveError {
		return scope.get(expr.var);
	}

	private int generate(Expr.UnOp expr, EnclosingScope scope) {
		int[] operands = new int[] { generate(expr.mhs, scope) };
		int[] targets = new int[] { scope.allocate(expr.result()) };
		switch (expr.op) {
		case NEG:
			scope.add(new Bytecode.Operator(expr.result().raw(), targets, operands, Bytecode.OperatorKind.NEG),
					attributes(expr));
			break;
		case INVERT:
			scope.add(new Bytecode.Operator(expr.result().raw(), targets, operands, Bytecode.OperatorKind.BITWISEINVERT),
					attributes(expr));
			break;
		case NOT:
			scope.add(new Bytecode.Operator(expr.result().raw(), targets, operands, Bytecode.OperatorKind.NOT),
					attributes(expr));
			break;
		case ARRAYLENGTH:
			scope.add(new Bytecode.Operator(expr.type.raw(), targets, operands, Bytecode.OperatorKind.ARRAYLENGTH), attributes(expr));
			break;
		default:
			// should be dead-code
			internalFailure("unexpected unary operator encountered", scope.getSourceContext(), expr);
			return -1;
		}
		return targets[0];
	}

	private int generate(Expr.Dereference expr, EnclosingScope scope) {
		int[] operands = new int[] { generate(expr.src, scope) };
		int[] targets = new int[] { scope.allocate(expr.result()) };
		scope.add(new Bytecode.Operator(expr.srcType.raw(), targets, operands, Bytecode.OperatorKind.DEREFERENCE),
				attributes(expr));
		return targets[0];
	}

	private int generate(Expr.IndexOf expr, EnclosingScope scope) {
		int[] operands = { generate(expr.src, scope),
				generate(expr.index, scope) };
		int[] targets = new int[] { scope.allocate(expr.result()) };
		scope.add(new Bytecode.Operator(expr.srcType.raw(), targets, operands, Bytecode.OperatorKind.ARRAYINDEX), attributes(expr));
		return targets[0];
	}

	private int generate(Expr.Cast expr, EnclosingScope scope) {
		int operand = generate(expr.expr, scope);
		Nominal from = expr.expr.result();
		Nominal to = expr.result();
		int target = scope.allocate(to);
		scope.add(new Bytecode.Convert(from.raw(), target, operand, to.raw()), attributes(expr));
		return target;
	}

	private int generate(Expr.BinOp v, EnclosingScope scope) throws Exception {
		// could probably use a range test for this somehow
		if(v.op == Expr.BOp.AND || v.op == Expr.BOp.OR) {
			String trueLabel = freshLabel();
			String exitLabel = freshLabel();
			generateCondition(trueLabel, v, scope);
			int target = scope.allocate(Nominal.T_BOOL);
			scope.add(new Bytecode.Const(target, Constant.Bool(false)), attributes(v));
			scope.add(new Bytecode.Goto(exitLabel));
			scope.add(new Bytecode.Label(trueLabel));
			scope.add(new Bytecode.Const(target, Constant.Bool(true)), attributes(v));
			scope.add(new Bytecode.Label(exitLabel));
			return target;
		} else {
			Nominal result = v.result();
			int[] targets = new int[] { scope.allocate(result) };
			int[] operands = { 
					generate(v.lhs, scope),
					generate(v.rhs, scope) 
			};

			scope.add(new Bytecode.Operator(result.raw(), targets, operands, OP2BOP(v.op, v, scope.getSourceContext())), attributes(v));

			return targets[0];
		}
	}

	private int generate(Expr.ArrayInitialiser expr, EnclosingScope scope) {
		int[] operands = generate(expr.arguments, scope);
		int[] targets = new int[] { scope.allocate(expr.result()) };
		scope.add(new Bytecode.Operator(expr.type.raw(), targets, operands, Bytecode.OperatorKind.ARRAYCONSTRUCTOR),
				attributes(expr));
		return targets[0];
	}

	private int generate(Expr.ArrayGenerator expr, EnclosingScope scope) {
		int[] operands = new int[] { generate(expr.element, scope),
				generate(expr.count, scope) };
		int[] targets = new int[] { scope.allocate(expr.result()) };
		scope.add(new Bytecode.Operator(expr.type.raw(), targets, operands, Bytecode.OperatorKind.ARRAYGENERATOR), attributes(expr));
		return targets[0];
	}

	private int generate(Expr.Quantifier e, EnclosingScope scope) {
		String trueLabel = freshLabel();
		String exitLabel = freshLabel();
		generateCondition(trueLabel, e, scope);
		int target = scope.allocate(Nominal.T_BOOL);
		scope.add(new Bytecode.Const(target, Constant.Bool(false)), attributes(e));
		scope.add(new Bytecode.Goto(exitLabel));
		scope.add(new Bytecode.Label(trueLabel));
		scope.add(new Bytecode.Const(target, Constant.Bool(true)), attributes(e));
		scope.add(new Bytecode.Label(exitLabel));
		return target;
	}

	private int generate(Expr.Record expr, EnclosingScope scope) {
		ArrayList<String> keys = new ArrayList<String>(expr.fields.keySet());
		Collections.sort(keys);
		int[] operands = new int[expr.fields.size()];
		for (int i = 0; i != operands.length; ++i) {
			String key = keys.get(i);
			Expr arg = expr.fields.get(key);
			operands[i] = generate(arg, scope);
		}
		int[] targets = new int[] { scope.allocate(expr.result()) };
		scope.add(new Bytecode.Operator(expr.result().raw(), targets, operands, Bytecode.OperatorKind.RECORDCONSTRUCTOR),
				attributes(expr));
		return targets[0];
	}

	private int generate(Expr.FieldAccess expr, EnclosingScope scope) {
		int operand = generate(expr.src, scope);
		int target = scope.allocate(expr.result());
		scope.add(new Bytecode.FieldLoad((Type.EffectiveRecord) expr.srcType.raw(), target, operand, expr.name),
				attributes(expr));
		return target;
	}

	private int generate(Expr.New expr, EnclosingScope scope) throws ResolveError {
		int[] operands = new int[] { generate(expr.expr, scope) };
		int[] targets = new int[] { scope.allocate(expr.result()) };
		scope.add(new Bytecode.Operator(expr.type.raw(), targets, operands, Bytecode.OperatorKind.NEW));
		return targets[0];
	}

	private int[] generate(List<Expr> arguments, EnclosingScope scope) {
		int[] operands = new int[arguments.size()];
		for (int i = 0; i != operands.length; ++i) {
			Expr arg = arguments.get(i);
			operands[i] = generate(arg, scope);
		}
		return operands;
	}

	// =========================================================================
	// Helpers
	// =========================================================================

	private Bytecode.OperatorKind OP2BOP(Expr.BOp bop, SyntacticElement elem, Context scope) {
		switch (bop) {
		case ADD:
			return Bytecode.OperatorKind.ADD;
		case SUB:
			return Bytecode.OperatorKind.SUB;
		case MUL:
			return Bytecode.OperatorKind.MUL;
		case DIV:
			return Bytecode.OperatorKind.DIV;
		case REM:
			return Bytecode.OperatorKind.REM;
		case EQ:
			return Bytecode.OperatorKind.EQ;
		case NEQ:
			return Bytecode.OperatorKind.NEQ;
		case LT:
			return Bytecode.OperatorKind.LT;
		case LTEQ:
			return Bytecode.OperatorKind.LTEQ;
		case GT:
			return Bytecode.OperatorKind.GT;
		case GTEQ:
			return Bytecode.OperatorKind.GTEQ;
		case BITWISEAND:
			return Bytecode.OperatorKind.BITWISEAND;
		case BITWISEOR:
			return Bytecode.OperatorKind.BITWISEOR;
		case BITWISEXOR:
			return Bytecode.OperatorKind.BITWISEXOR;
		case LEFTSHIFT:
			return Bytecode.OperatorKind.LEFTSHIFT;
		case RIGHTSHIFT:
			return Bytecode.OperatorKind.RIGHTSHIFT;
		default:
			internalFailure(errorMessage(INVALID_BINARY_EXPRESSION), scope, elem);
		}
		// dead-code
		return null;
	}

	@SuppressWarnings("incomplete-switch")
	private static Expr invert(Expr e) {
		if (e instanceof Expr.BinOp) {
			Expr.BinOp bop = (Expr.BinOp) e;
			Expr.BinOp nbop = null;
			switch (bop.op) {
			case AND:
				nbop = new Expr.BinOp(Expr.BOp.OR, invert(bop.lhs), invert(bop.rhs), e.attributes());
				break;
			case OR:
				nbop = new Expr.BinOp(Expr.BOp.AND, invert(bop.lhs), invert(bop.rhs), e.attributes());
				break;
			case EQ:
				nbop = new Expr.BinOp(Expr.BOp.NEQ, bop.lhs, bop.rhs, e.attributes());
				break;
			case NEQ:
				nbop = new Expr.BinOp(Expr.BOp.EQ, bop.lhs, bop.rhs, e.attributes());
				break;
			case LT:
				nbop = new Expr.BinOp(Expr.BOp.GTEQ, bop.lhs, bop.rhs, e.attributes());
				break;
			case LTEQ:
				nbop = new Expr.BinOp(Expr.BOp.GT, bop.lhs, bop.rhs, e.attributes());
				break;
			case GT:
				nbop = new Expr.BinOp(Expr.BOp.LTEQ, bop.lhs, bop.rhs, e.attributes());
				break;
			case GTEQ:
				nbop = new Expr.BinOp(Expr.BOp.LT, bop.lhs, bop.rhs, e.attributes());
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
	 * The attributes method extracts those attributes of relevance to WyIL, and
	 * discards those which are only used for the wyc front end.
	 *
	 * @param elem
	 * @return
	 */
	private static List<wyil.lang.Attribute> attributes(SyntacticElement elem) {
		ArrayList<wyil.lang.Attribute> attrs = new ArrayList<wyil.lang.Attribute>();
		Attribute.Source s = elem.attribute(Attribute.Source.class);
		if (s != null) {
			// TODO: need to identify the file here
			attrs.add(new wyil.attributes.SourceLocation(0, s.start, s.end));
		}
		return attrs;
	}

	public List<Integer> toIntegerList(int... items) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i != items.length; ++i) {
			list.add(items[i]);
		}
		return list;
	}

	private int[] toIntArray(List<Integer> items) {
		int[] arr = new int[items.size()];
		for(int i=0;i!=arr.length;++i) {
			arr[i] = items.get(i);
		}
		return arr;
	}
	
	private static int _idx=0;
	public static String freshLabel() {
		return "blklab" + _idx++;
	}

	/**
	 * Captures all useful information about the scope in which a statement or
	 * expression is being translated. For example, it determines which WyIL
	 * register all visible variables and parameters map to. Furthermore, it
	 * determines where break and continue statements will jump to.
	 * 
	 * @author David J. Pearce
	 *
	 */
	private static final class EnclosingScope {
		/**
		 * Maps variables to their WyIL register number and type.
		 */
		private final HashMap<String,Integer> environment;
		/**
		 * The outermost forest (needed for creating new subblocks).
		 */
		private final BytecodeForest forest;
		/**
		 * The enclosing source file scope (needed for error reporting)
		 */
		private final WhileyFile.Context context;
		/**
		 * The enclosing bytecode block into which bytecodes are being written.
		 */
		private final BytecodeForest.Block block;		
		/**
		 * Get the index of the bytecode block into which bytecodes are being written
		 */
		private final int blockIndex;		
		/**
		 * Get the target for any continue statement encountered
		 */
		private final String continueLabel;		
		/**
		 * Get the target for any break statement encountered
		 */
		private final String breakLabel;
		
		public EnclosingScope(WhileyFile.Context context) {
			this(new HashMap<String,Integer>(), new BytecodeForest(), context, -1);
		}
		
		private EnclosingScope(Map<String, Integer> environment, BytecodeForest forest, WhileyFile.Context context,
				int blockIndex) {
			this(environment, forest, context, blockIndex, null, null);
		}
		
		private EnclosingScope(Map<String, Integer> environment, BytecodeForest forest, WhileyFile.Context context,
				int blockIndex, String breakLabel, String continueLabel) {
			this.environment = new HashMap<String, Integer>(environment);
			this.forest = forest;
			this.context = context;
			this.blockIndex = blockIndex;
			this.block = blockIndex == -1 ? null : forest.get(blockIndex);
			this.breakLabel = breakLabel;
			this.continueLabel = continueLabel;
		}
		
		public int blockIndex() {
			return blockIndex;
		}
		
		public BytecodeForest getForest() {
			return forest;
		}
		
		public BytecodeForest.Block getBlock() {
			return block;
		}
		
		public WhileyFile.Context getSourceContext() {
			return context;
		}
		
		public String getBreakLabel() {
			return breakLabel;
		}
		
		public String getContinueLabel() {
			return breakLabel;
		}
		
		public Nominal.FunctionOrMethod getEnclosingFunctionType() {
			WhileyFile.FunctionOrMethod m = (WhileyFile.FunctionOrMethod) context;
			return m.resolvedType();
		}
		
		public Integer get(String name) {
			return environment.get(name);
		}	
		
		public int allocate(Nominal type) {
			List<BytecodeForest.Register> registers = forest.registers(); 
			int index = registers.size();
			registers.add(new BytecodeForest.Register(type.nominal(), null));			
			return index;
		}
		
		public int allocate(Nominal type, String name) {
			List<BytecodeForest.Register> registers = forest.registers(); 
			int index = registers.size();
			registers.add(new BytecodeForest.Register(type.nominal(), name));
			environment.put(name, index);
			return index;
		}
		
		public void add(Bytecode b) {
			block.add(b);
		}
		
		public void add(Bytecode b, List<wyil.lang.Attribute> attributes) {
			block.add(b,attributes);
		}		
		
		public void add(int index, Bytecode b, List<wyil.lang.Attribute> attributes) {
			block.add(index, b,attributes);
		}		
		
		public EnclosingScope createRootBlock() {
			BytecodeForest.Block block = new BytecodeForest.Block();
			int index = forest.addAsRoot(block);
			return new EnclosingScope(environment,forest,context,index);
		}
		
		public EnclosingScope createBlock() {
			BytecodeForest.Block block = new BytecodeForest.Block();
			int index = forest.add(block);
			return new EnclosingScope(environment,forest,context,index);
		}
		
		public EnclosingScope createBlock(String breakLabel, String continueLabel) {
			BytecodeForest.Block block = new BytecodeForest.Block();
			int index = forest.add(block);
			return new EnclosingScope(environment,forest,context,index,breakLabel,continueLabel);
		}
		
		public EnclosingScope clone() {
			return new EnclosingScope(environment,forest,context,blockIndex,breakLabel,continueLabel);
		}
	}
}
