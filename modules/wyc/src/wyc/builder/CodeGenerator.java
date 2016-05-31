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
	 * @param whileyFile
	 *            The WhileyFile to be translated.
	 * @return
	 */
	public WyilFile generate(WhileyFile whileyFile) {
		WyilFile wyilFile = new WyilFile(whileyFile.module, whileyFile.filename);

		// Go through each declaration and translate in the order of appearance.
		for (WhileyFile.Declaration d : whileyFile.declarations) {
			try {
				if (d instanceof WhileyFile.Type) {
					generate(wyilFile, (WhileyFile.Type) d);
				} else if (d instanceof WhileyFile.Constant) {
					generate(wyilFile, (WhileyFile.Constant) d);
				} else if (d instanceof WhileyFile.FunctionOrMethod) {
					generate(wyilFile, (WhileyFile.FunctionOrMethod) d);
				}
			} catch (SyntaxError se) {
				throw se;
			} catch (Throwable ex) {
				WhileyFile.internalFailure(ex.getMessage(), (WhileyFile.Context) d, d, ex);
			}
		}

		// Done
		return wyilFile;
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
	private void generate(WyilFile enclosing, WhileyFile.Constant declaration) {
		WyilFile.Constant block = new WyilFile.Constant(enclosing, declaration.modifiers(), declaration.name(),
				declaration.resolvedValue);
		enclosing.blocks().add(block);
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
	private void generate(WyilFile enclosing, WhileyFile.Type td) throws Exception {
		// Construct new WyIL type declaration
		WyilFile.Type declaration = new WyilFile.Type(enclosing, td.modifiers(), td.name(), td.resolvedType.nominal());
		//
		EnclosingScope scope = new EnclosingScope(declaration, td);
		// Allocate declared parameter
		if (td.parameter.name() != null) {
			// If no parameter declared, then there will no invariant either
			scope.declare(td.resolvedType, td.parameter.name(), td.attributes());
			// Generate code for each invariant condition
			for (Expr invariant : td.invariant) {
				declaration.getInvariantIndices().add(generate(invariant, scope));
			}
		}
		// done
		enclosing.blocks().add(declaration);
	}

	// =========================================================================
	// Function / Method Declarations
	// =========================================================================

	private void generate(WyilFile enclosing, WhileyFile.FunctionOrMethod fmd) throws Exception {
		// Construct new WyIL function or method
		WyilFile.FunctionOrMethod declaration = new WyilFile.FunctionOrMethod(enclosing, fmd.modifiers(), fmd.name(),
				fmd.resolvedType().nominal());
		// Construct environments
		EnclosingScope scope = new EnclosingScope(declaration,fmd);
		addDeclaredParameters(fmd.parameters, fmd.resolvedType().params(), scope);
		addDeclaredParameters(fmd.returns, fmd.resolvedType().returns(), scope);

		// Generate precondition(s)		
		for (Expr precondition : fmd.requires) {
			declaration.getPreconditionIndices().add(generate(precondition, scope));
		}
		// Generate postcondition(s)		
		for (Expr postcondition : fmd.ensures) {
			declaration.getPostconditionIndices().add(generate(postcondition, scope));
		}
		// Generate function or method body
		scope = scope.newRootScope();
		for (Stmt s : fmd.statements) {
			generate(s, scope);
		}		
		// Add declaration itself to enclosing file
		enclosing.blocks().add(declaration);
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
			String name = parameter.name;
			if (name == null) {
				// This can happen for an unnamed return value. If named return
				// values become mandatory, this check will be redundant.
				name = "$";
			}
			// allocate parameter to register in the current block
			scope.declare(types.get(i), name, parameter.attributes());
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
				generateAsStmt((Expr.FunctionOrMethodCall) stmt, scope);
			} else if (stmt instanceof Expr.IndirectFunctionOrMethodCall) {
				generateAsStmt((Expr.IndirectFunctionOrMethodCall) stmt, scope);
			} else if (stmt instanceof Expr.New) {
				generate((Expr.New) stmt, scope);
			} else if (stmt instanceof Skip) {
				generate((Skip) stmt, scope);
			} else {
				// should be dead-code
				WhileyFile.internalFailure("unknown statement: " + stmt.getClass().getName(), scope.getSourceContext(),
						stmt);
			}
		} catch (ResolveError ex) {
			internalFailure(ex.getMessage(), scope.getSourceContext(), stmt, ex);
		} catch (SyntaxError ex) {
			throw ex;
		} catch (Exception ex) {
			internalFailure(ex.getMessage(), scope.getSourceContext(), stmt, ex);
		}
	}

	/**
	 * Translate a variable declaration statement into WyIL bytecodes.
	 *
	 * @param stmt
	 *            --- Statement to be translated.
	 * @param scope
	 *            --- Enclosing scope of this statement.
	 * @return
	 */
	private void generate(VariableDeclaration s, EnclosingScope scope) {
		// First, we allocate this variable to a given slot in the environment.
		int var = scope.declare(s.type, s.parameter.name, s.attributes());
		// Second, translate initialiser expression (if applicable).
		if (s.expr != null) {
			int operand = generate(s.expr, scope);
			scope.add(new Bytecode.Assign(var, operand), s.attributes());
		}
	}

	/**
	 * Translate an assignment statement into WyIL bytecodes.
	 *
	 * @param stmt
	 *            --- Statement to be translated.
	 * @param scope
	 *            --- Enclosing scope of this statement (i.e. type, constant,
	 *            function or method declaration). The scope is used to aid with
	 *            error reporting as it determines the enclosing file.
	 * @return
	 */
	private void generate(Assign s, EnclosingScope scope) throws ResolveError {
		int[] lhs = generate((List) s.lvals, scope);
		int[] rhs = generateMultipleReturns(s.rvals, scope);
		scope.add(new Bytecode.Assign(lhs, rhs), s.attributes());
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
		// First, translate assertion
		int operand = generate(s.expr, scope);
		// Second, create assert bytecode
		scope.add(new Bytecode.Assert(operand), s.attributes());
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
		// First, translate assumption
		int operand = generate(s.expr, scope);
		// Second, create assert bytecode
		scope.add(new Bytecode.Assume(operand), s.attributes());
	}

	/**
	 * Translate a return statement into WyIL bytecodes.
	 * 
	 * @param stmt
	 *            --- Statement to be translated.
	 * @param scope
	 *            --- Enclosing scope of this statement.
	 * @return
	 */
	private void generate(Stmt.Return s, EnclosingScope scope) throws ResolveError {
		List<Expr> returns = s.returns;
		// Here, we don't put the type propagated for the return expression.
		// Instead, we use the declared return type of this function. This
		// has the effect of forcing an implicit coercion between the
		// actual value being returned and its required type.		
		int[] operands = generateMultipleReturns(returns,scope);
		scope.add(new Bytecode.Return(operands), s.attributes());
	}

	/**
	 * Translate a skip statement into a WyIL bytecode.
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
	 * @param stmt
	 *            --- Statement to be translated.
	 * @param scope
	 *            --- Enclosing scope of this statement.
	 * @return
	 */
	private void generate(Stmt.Debug s, EnclosingScope scope) {
		int operand = generate(s.expr, scope);
		scope.add(new Bytecode.Debug(operand), s.attributes());
	}

	/**
	 * Translate a fail statement into WyIL bytecodes.
	 *
	 * @param stmt
	 *            --- Statement to be translated.
	 * @param scope
	 *            --- Enclosing scope of this statement.
	 * @return
	 */
	private void generate(Stmt.Fail s, EnclosingScope scope) {
		scope.add(new Bytecode.Fail(), s.attributes());
	}

	/**
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
		EnclosingScope trueScope = scope.newBlockScope();

		int operand = generate(s.condition, scope);

		for (Stmt st : s.trueBranch) {
			generate(st, trueScope);
		}

		if (!s.falseBranch.isEmpty()) {
			// There is a false branch, so translate that as well
			EnclosingScope falseScope = scope.newBlockScope();
			for (Stmt st : s.falseBranch) {
				generate(st, falseScope);
			}
			scope.add(new Bytecode.If(operand, trueScope.blockIndex(), falseScope.blockIndex()), s.attributes());
		} else {
			// No false branch to translate
			scope.add(new Bytecode.If(operand, trueScope.blockIndex()), s.attributes());
		}
	}

	private void generate(Stmt.Break s, EnclosingScope scope) {
		scope.add(new Bytecode.Break(), s.attributes());
	}

	private void generate(Stmt.Continue s, EnclosingScope scope) {
		scope.add(new Bytecode.Continue(), s.attributes());
	}

	private void generate(Stmt.Switch s, EnclosingScope scope) throws Exception {

		int operand = generate(s.expr, scope);
		Bytecode.Case[] cases = new Bytecode.Case[s.cases.size()];

		// FIXME: the following check should really occur earlier in the
		// pipeline. However, it is difficult to do it earlier because it's only
		// after FlowTypeChecker that we have determined the concrete values.
		// See #628
		checkNoDuplicateLabels(s.cases, scope);

		for (int i = 0; i != cases.length; ++i) {
			Stmt.Case c = s.cases.get(i);
			EnclosingScope bodyScope = scope.newBlockScope();
			for (Stmt st : c.stmts) {
				generate(st, bodyScope);
			}
			cases[i] = new Bytecode.Case(bodyScope.blockIndex(), c.constants);
		}

		scope.add(new Bytecode.Switch(operand, cases), s.attributes());
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
		for (int i = 0; i != cases.size(); ++i) {
			Stmt.Case caseBlock = cases.get(i);
			List<Constant> caseLabels = caseBlock.constants;
			if (caseLabels != null) {
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
		// FIXME: this should correspond to a WyIL bytecode of some kind
		for (Stmt st : s.body) {
			generate(st, scope);
		}
	}

	/**
	 * @param stmt
	 *            --- Statement to be translated.
	 * @param scope
	 *            --- Enclosing scope of this statement.
	 * @return
	 */
	private void generate(Stmt.While s, EnclosingScope scope) {
		EnclosingScope subscope = scope.newLoopScope();

		// Translate loop invariant(s)
		int[] invariants = generate(s.invariants, scope);
		
		// FIXME: determine set of modified variables. This should be done by
		// traversing the loop body to see which variables are assigned.
		int[] modified = new int[0];
		
		// Translate loop condition
		int condition = generate(s.condition, scope);

		// Translate loop body
		for (Stmt st : s.body) {
			generate(st, subscope);
		}

		scope.add(new Bytecode.While(subscope.blockIndex(), condition, invariants, modified), s.attributes());
	}

	/**
	 * 
	 * @param stmt
	 *            --- Statement to be translated.
	 * @param scope
	 *            --- Enclosing scope of this statement.
	 * @return
	 */
	private void generate(Stmt.DoWhile s, EnclosingScope scope) {
		EnclosingScope subscope = scope.newLoopScope();

		// FIXME: determine set of modified variables. This should be done by
		// traversing the loop body to see which variables are assigned.
		int[] modified = new int[0];
		
		// Translate loop body
		for (Stmt st : s.body) {
			generate(st, subscope);
		}

		// Translate loop invariant(s)
		int[] invariants = generate(s.invariants, scope);

		// Translate loop condition
		int condition = generate(s.condition, scope);

		//
		scope.add(new Bytecode.DoWhile(subscope.blockIndex(), condition, invariants, modified), s.attributes());
	}

	// =========================================================================
	// Multi-Expressions
	// =========================================================================

	/**
	 * Generate an invoke expression as a statement. There are only limited
	 * cases where this can arise. In particular, when a method is invoked and
	 * the return value is ignored. In such case, we generate an assignment with
	 * and empty left-hand side.
	 * 
	 * @param expr
	 *            The expression to be translated as a statement
	 * @param scope
	 *            The enclosing scope of the expression
	 */
	public void generateAsStmt(Expr.FunctionOrMethodCall expr, EnclosingScope scope) throws ResolveError {
		//
		int[] operands = generate(expr.arguments, scope);
		Nominal.FunctionOrMethod type = expr.type();
		scope.add(new Bytecode.Invoke(type.nominal(), operands, expr.nid()), expr.attributes());
	}

	/**
	 * Generate an indirect invoke expression as a statement. There are only limited
	 * cases where this can arise. In particular, when a method is invoked and
	 * the return value is ignored. In such case, we generate an assignment with
	 * and empty left-hand side.
	 * 
	 * @param expr
	 *            The expression to be translated as a statement
	 * @param scope
	 *            The enclosing scope of the expression
	 */
	public void generateAsStmt(Expr.IndirectFunctionOrMethodCall expr, EnclosingScope scope) throws ResolveError {
		//
		int operand = generate(expr.src, scope);
		int[] operands = generate(expr.arguments, scope);
		Nominal.FunctionOrMethod type = expr.type();
		scope.add(new Bytecode.IndirectInvoke(type.nominal(), operand, operands), expr.attributes());
	}
	
	// =========================================================================
	// Expressions
	// =========================================================================

	/**
	 * Translate a source-level expression into a WYIL bytecode block, using a
	 * given environment mapping named variables to registers. This expression
	 * may generate zero or more results.
	 * 
	 * @param expression
	 * @param scope
	 * @return
	 */
	public int[] generateMultipleReturns(List<Expr> expressions, EnclosingScope scope) throws ResolveError {
		int[] returns = new int[0];
		for(int i=0;i!=expressions.size();++i) {
			Expr expression = expressions.get(i);
			if(expression instanceof Expr.FunctionOrMethodCall) {
				returns = append(returns,generate((Expr.FunctionOrMethodCall) expression,scope));
			} else if(expression instanceof Expr.IndirectFunctionOrMethodCall) {
				returns = append(returns,generate((Expr.IndirectFunctionOrMethodCall) expression,scope));
			} else {
				returns = append(returns,generate(expression,scope));
			}
		}
		return returns;
	}
	
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
				int[] returns = generate((Expr.FunctionOrMethodCall) expression, scope);
				// Flow Type Checker should ensure only one here
				return returns[0];
			} else if (expression instanceof Expr.IndirectFunctionOrMethodCall) {
				int[] returns = generate((Expr.IndirectFunctionOrMethodCall) expression, scope);
				// Flow Type Checker should ensure only one here
				return returns[0];
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
			} else if (expression instanceof Expr.TypeVal) {
				return generate((Expr.TypeVal) expression, scope);
			} else {
				// should be dead-code
				internalFailure("unknown expression: " + expression.getClass().getName(), scope.getSourceContext(),
						expression);
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

	public int[] generate(Expr.FunctionOrMethodCall expr, EnclosingScope scope) throws ResolveError {
		int[] operands = generate(expr.arguments, scope);
		Nominal.FunctionOrMethod type = expr.type();
		return scope.allocate(type.returns(), new Bytecode.Invoke(type.nominal(), operands, expr.nid()),
				expr.attributes());
	}

	public int[] generate(Expr.IndirectFunctionOrMethodCall expr, EnclosingScope scope) throws ResolveError {
		int operand = generate(expr.src, scope);
		int[] operands = generate(expr.arguments, scope);
		Nominal.FunctionOrMethod type = expr.type();
		return scope.allocate(type.returns(), new Bytecode.IndirectInvoke(type.nominal(), operand, operands),
				expr.attributes());
	}

	private int generate(Expr.Constant expr, EnclosingScope scope) {
		Constant val = expr.value;
		Bytecode.Expr operand = new Bytecode.Const(val);
		return scope.allocate(Nominal.construct(val.type(), val.type()), operand, expr.attributes());
	}

	private int generate(Expr.TypeVal expr, EnclosingScope scope) {
		Constant val = new Constant.Type(expr.type.nominal());
		return scope.allocate(Nominal.construct(val.type(), val.type()), new Bytecode.Const(val), expr.attributes());
	}

	private int generate(Expr.FunctionOrMethod expr, EnclosingScope scope) {
		// FIXME: should really remove Expr.FunctionOrMethod from the AST. This
		// should be just an Expr.Constant
		Type.FunctionOrMethod type = expr.type.nominal();
		Constant.FunctionOrMethod val = new Constant.FunctionOrMethod(expr.nid, type);
		Bytecode.Expr operand = new Bytecode.Const(val);
		return scope.allocate(Nominal.construct(val.type(), val.type()), operand, expr.attributes());
	}

	private int generate(Expr.Lambda expr, EnclosingScope scope) {
		Nominal.FunctionOrMethod lambdaType = expr.type;
		// Create a new scope for the lambda body. This will contain any
		// parameters which are declared as part of the lambda expression.
		EnclosingScope lambdaScope = scope.clone();
		// Now, declare lambda parameters parameters
		HashSet<String> declaredVariables = new HashSet<String>();
		int[] parameters = new int[expr.parameters.size()];
		for (int i = 0; i != parameters.length; ++i) {
			WhileyFile.Parameter parameter = expr.parameters.get(i);
			// allocate parameter to register in the lambda scope
			parameters[i] = lambdaScope.declare(lambdaType.param(i), parameter.name, parameter.attributes());
			declaredVariables.add(parameter.name);
		}
		// Now, determine the set of used variables from the enclosing scope
		// which forms the environment of the lambda
		ArrayList<Integer> environment = new ArrayList<Integer>();
		for (Pair<Nominal, String> v : Exprs.uses(expr.body, scope.getSourceContext())) {
			if (!declaredVariables.contains(v.second())) {
				int variable = scope.get(v.second());
				environment.add(variable);
			}
		}
		// Translate the lambda body
		int body = generate(expr.body, lambdaScope);
		//
		return scope.allocate(lambdaType,
				new Bytecode.Lambda(lambdaType.nominal(), body, parameters, toIntArray(environment)),
				expr.attributes());
	}

	private int generate(Expr.ConstantAccess expr, EnclosingScope scope) throws ResolveError {
		// FIXME: the concept of a constant access should propagate through to
		// the bytecode, rather than having the constants inlined here.
		Constant val = expr.value;
		return scope.allocate(Nominal.construct(val.type(), val.type()), new Bytecode.Const(val), expr.attributes());
	}

	private int generate(Expr.LocalVariable expr, EnclosingScope scope) throws ResolveError {
		return scope.get(expr.var);
	}

	private int generate(Expr.UnOp expr, EnclosingScope scope) {
		int[] operands = new int[] { generate(expr.mhs, scope) };
		Bytecode.OperatorKind op;
		switch (expr.op) {
		case NEG:
			op = Bytecode.OperatorKind.NEG;
			break;
		case INVERT:
			op = Bytecode.OperatorKind.BITWISEINVERT;
			break;
		case NOT:
			op = Bytecode.OperatorKind.NOT;
			break;
		case ARRAYLENGTH:
			op = Bytecode.OperatorKind.ARRAYLENGTH;
			break;
		default:
			// should be dead-code
			internalFailure("unexpected unary operator encountered", scope.getSourceContext(), expr);
			return -1;
		}
		return scope.allocate(expr.result(), new Bytecode.Operator(operands, op), expr.attributes());
	}

	private int generate(Expr.Dereference expr, EnclosingScope scope) {
		int[] operands = new int[] { generate(expr.src, scope) };
		return scope.allocate(expr.srcType, new Bytecode.Operator(operands, Bytecode.OperatorKind.DEREFERENCE),
				expr.attributes());
	}

	private int generate(Expr.IndexOf expr, EnclosingScope scope) {
		int[] operands = { generate(expr.src, scope), generate(expr.index, scope) };
		return scope.allocate(expr.srcType, new Bytecode.Operator(operands, Bytecode.OperatorKind.ARRAYINDEX),
				expr.attributes());
	}

	private int generate(Expr.Cast expr, EnclosingScope scope) {
		int operand = generate(expr.expr, scope);
		Nominal to = expr.result();
		return scope.allocate(to, new Bytecode.Convert(operand), expr.attributes());
	}

	private int generate(Expr.BinOp v, EnclosingScope scope) throws Exception {
		Nominal result = v.result();
		int[] operands = { generate(v.lhs, scope), generate(v.rhs, scope) };
		return scope.allocate(result, new Bytecode.Operator(operands, OP2BOP(v.op, v, scope.getSourceContext())),
				v.attributes());
	}

	private int generate(Expr.ArrayInitialiser expr, EnclosingScope scope) {
		int[] operands = generate(expr.arguments, scope);
		return scope.allocate(expr.type, new Bytecode.Operator(operands, Bytecode.OperatorKind.ARRAYCONSTRUCTOR),
				expr.attributes());
	}

	private int generate(Expr.ArrayGenerator expr, EnclosingScope scope) {
		int[] operands = new int[] { generate(expr.element, scope), generate(expr.count, scope) };
		return scope.allocate(expr.type, new Bytecode.Operator(operands, Bytecode.OperatorKind.ARRAYGENERATOR),
				expr.attributes());
	}

	private int generate(Expr.Quantifier e, EnclosingScope scope) {
		EnclosingScope quantifierScope = scope.clone();
		// First, translate sources and declare variables in the quantifier
		// scope.
		Bytecode.Range[] ranges = new Bytecode.Range[e.sources.size()];
		for (int i = 0; i != ranges.length; ++i) {
			Triple<String, Expr, Expr> t = e.sources.get(i);
			int start = generate(t.second(), quantifierScope);
			int end = generate(t.third(), quantifierScope);
			// FIXME: the attributes provided here are not very "precise".
			int var = quantifierScope.declare(Nominal.T_INT, t.first(), e.attributes());
			ranges[i] = new Bytecode.Range(var, start, end);
		}
		// Second, translate the quantifier body in the context of the new
		// scope.
		int body = generate(e.condition, quantifierScope);
		//
		Bytecode.QuantifierKind kind = Bytecode.QuantifierKind.valueOf(e.cop.name());
		return scope.allocate(Nominal.T_BOOL, new Bytecode.Quantifier(kind, body, ranges), e.attributes());
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
		return scope.allocate(expr.result(), new Bytecode.Operator(operands, Bytecode.OperatorKind.RECORDCONSTRUCTOR),
				expr.attributes());
	}

	private int generate(Expr.FieldAccess expr, EnclosingScope scope) {
		int operand = generate(expr.src, scope);
		return scope.allocate(expr.srcType, new Bytecode.FieldLoad(operand, expr.name), expr.attributes());
	}

	private int generate(Expr.New expr, EnclosingScope scope) throws ResolveError {
		int[] operands = new int[] { generate(expr.expr, scope) };
		return scope.allocate(expr.type, new Bytecode.Operator(operands, Bytecode.OperatorKind.NEW), expr.attributes());
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

	private int[] append(int[] lhs, int... rhs) {
		int[] rs = new int[lhs.length + rhs.length];
		System.arraycopy(lhs, 0, rs, 0, lhs.length);
		System.arraycopy(rhs, 0, rs, lhs.length, rhs.length);
		return rs;
	}
	
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
		case AND:
			return Bytecode.OperatorKind.AND;
		case OR:
			return Bytecode.OperatorKind.OR;
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
		case IS:
			return Bytecode.OperatorKind.IS;
		default:
			internalFailure(errorMessage(INVALID_BINARY_EXPRESSION), scope, elem);
		}
		// dead-code
		return null;
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
		for (int i = 0; i != arr.length; ++i) {
			arr[i] = items.get(i);
		}
		return arr;
	}

	private static int _idx = 0;

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
		private final HashMap<String, Integer> environment;		
		/**
		 * The enclosing source file scope (needed for error reporting)
		 */
		private final WhileyFile.Context context;
		/**
		 * The enclosing declaration in which we are writing new bytecode blocks
		 */
		private final WyilFile.Declaration enclosing;
		/**
		 * The enclosing bytecode block into which bytecodes are being written.
		 */
		private final SyntaxTree.Block block;
		/**
		 * Get the index of the bytecode block into which bytecodes are being
		 * written
		 */
		private final int blockIndex;

		public EnclosingScope(WyilFile.Declaration enclosing, WhileyFile.Context context) {
			this(new HashMap<String, Integer>(), enclosing, context, -1);
		}

		private EnclosingScope(Map<String, Integer> environment, WyilFile.Declaration enclosing, WhileyFile.Context context,
				int blockIndex) {
			this.environment = new HashMap<String, Integer>(environment);
			this.enclosing = enclosing;
			this.context = context;
			this.blockIndex = blockIndex;
			this.block = blockIndex == -1 ? null : enclosing.getBlock(blockIndex);
		}

		public int blockIndex() {
			return blockIndex;
		}

		public WyilFile.Declaration getEnclosingDeclaration() {
			return enclosing;
		}

		public SyntaxTree.Block getBlock() {
			return block;
		}

		public WhileyFile.Context getSourceContext() {
			return context;
		}

		public Nominal.FunctionOrMethod getEnclosingFunctionType() {
			WhileyFile.FunctionOrMethod m = (WhileyFile.FunctionOrMethod) context;
			return m.resolvedType();
		}

		public Integer get(String name) {
			return environment.get(name);
		}

		/**
		 * Declare a new variable in the enclosing bytecode forest.
		 * 
		 * @param type
		 *            The declared type of the variable
		 * @param name
		 *            The declare name of the variable
		 * @return
		 */
		public int declare(Nominal type, String name, List<Attribute> attributes) {
			List<SyntaxTree.Expr> expressions = enclosing.getExpressions();
			int index = expressions.size();
			expressions.add(SyntaxTree.Variable(type.nominal(), name, enclosing, attributes));
			environment.put(name, index);
			return index;
		}

		/**
		 * Allocate an operand on the stack.
		 * 
		 * @param Type
		 * @param operand
		 * @return
		 */
		public int allocate(Nominal type, Bytecode.Expr operand, List<Attribute> attributes) {
			List<SyntaxTree.Expr> expressions = enclosing.getExpressions();
			int index = expressions.size();
			expressions.add(SyntaxTree.Operator(type.nominal(), operand, enclosing, attributes));
			return index;
		}

		/**
		 * Allocate a multi-operand on the stack.
		 * 
		 * @param Type
		 * @param operand
		 * @return
		 */
		public int[] allocate(List<Nominal> types, Bytecode.Expr operand, List<Attribute> attributes) {
			Type[] nominals = new Type[types.size()];
			for (int i = 0; i != nominals.length; ++i) {
				nominals[i] = types.get(i).nominal();
			}
			List<SyntaxTree.Expr> expressions = enclosing.getExpressions();
			int[] indices = new int[types.size()];
			for (int i = 0; i != types.size(); ++i) {
				indices[i] = expressions.size();
				expressions.add(SyntaxTree.PositionalOperator(nominals[i], operand, i, enclosing, attributes));
			}
			return indices;
		}

		public void add(Bytecode.Stmt b, List<Attribute> attributes) {
			block.add(SyntaxTree.Stmt(b, block, attributes));
		}

		/**
		 * Create a new "block" scope. This is a subscope where new variables
		 * can be declared and, furthermore, it corresponds to a new block in
		 * the underlying forest.
		 * 
		 * @return
		 */
		public EnclosingScope newBlockScope() {
			SyntaxTree.Block block = new SyntaxTree.Block(enclosing);
			int index = enclosing.getBlocks().size();
			enclosing.getBlocks().add(block);
			return new EnclosingScope(environment, enclosing, context, index);
		}

		/**
		 * Create a new "loop" scope. This is a block scope which additionally
		 * becomes the nearest enclosing loop body (i.e. for nested continue or
		 * break statements).
		 * 
		 * @return
		 */
		public EnclosingScope newLoopScope() {
			SyntaxTree.Block block = new SyntaxTree.Block(enclosing);
			int index = enclosing.getBlocks().size();
			enclosing.getBlocks().add(block);
			return new EnclosingScope(environment, enclosing, context, index);
		}

		/**
		 * Create a new "root" scope. This is a block scope which is
		 * additionally a "root" in the enclosing forest.
		 * 
		 * @return
		 */
		public EnclosingScope newRootScope() {
			SyntaxTree.Block block = new SyntaxTree.Block(enclosing);
			int index = enclosing.getBlocks().size();
			enclosing.getBlocks().add(block);
			return new EnclosingScope(environment, enclosing, context, index);
		}

		public EnclosingScope clone() {
			return new EnclosingScope(environment, enclosing, context, blockIndex);
		}
	}
}
