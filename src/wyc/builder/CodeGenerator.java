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

import wybs.lang.Attribute;
import wybs.lang.NameID;
import wybs.lang.SyntacticElement;
import wybs.lang.SyntaxError;
import wybs.util.ResolveError;

import static wyc.lang.WhileyFile.internalFailure;
import static wyil.util.ErrorMessages.*;
import wyc.lang.*;
import wyc.lang.Stmt.*;
import wyc.lang.WhileyFile.Context;
import wycommon.util.Pair;
import wycommon.util.Triple;
import wyfs.lang.Path;
import wyil.lang.*;
import wyil.lang.Bytecode.AliasDeclaration;
import wyil.lang.SyntaxTree.Location;

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
	public WyilFile generate(WhileyFile whileyFile, Path.Entry<WyilFile> target) {
		WyilFile wyilFile = new WyilFile(target);

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
		SyntaxTree tree = declaration.getTree();
		//
		EnclosingScope scope = new EnclosingScope(tree, td);
		// Allocate declared parameter
		if (td.parameter.name() != null) {
			// If no parameter declared, then there will no invariant either
			scope.declare(td.resolvedType, td.parameter.name(), td.attributes());
			// Generate code for each invariant condition
			for (Expr invariant : td.invariant) {
				int index = generateCondition(invariant, scope).operand;
				Location<Bytecode.Expr> loc = (Location<Bytecode.Expr>) tree.getLocation(index); 
				declaration.getInvariant().add(loc);
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
		SyntaxTree tree = declaration.getTree();
		// Construct environments
		EnclosingScope scope = new EnclosingScope(tree,fmd);
		addDeclaredParameters(fmd.parameters, fmd.resolvedType().params(), scope);
		addDeclaredParameters(fmd.returns, fmd.resolvedType().returns(), scope);

		// Generate precondition(s)		
		for (Expr precondition : fmd.requires) {
			int index = generateCondition(precondition, scope).operand;
			Location<Bytecode.Expr> loc = (Location<Bytecode.Expr>) tree.getLocation(index);
			declaration.getPrecondition().add(loc);
		}
		// Generate postcondition(s)		
		for (Expr postcondition : fmd.ensures) {
			int index = generateCondition(postcondition, scope).operand;
			Location<Bytecode.Expr> loc = (Location<Bytecode.Expr>) tree.getLocation(index);
			declaration.getPostcondition().add(loc);
		}
		// Generate function or method body
		scope = scope.clone();
		int bodyIndex = generateBlock(fmd.statements,scope);
		SyntaxTree.Location<Bytecode.Block> body = (SyntaxTree.Location<Bytecode.Block>) tree.getLocation(bodyIndex);  
		declaration.setBody(body);
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
	// Blocks
	// =========================================================================

	/**
	 * Translate a sequence of zero or more statements into a bytecode block.
	 * 
	 * @param stmts
	 * @param scope
	 * @return
	 */
	private int generateBlock(List<Stmt> stmts, EnclosingScope scope) {
		int[] block = new int[stmts.size()];
		for (int i = 0; i != stmts.size(); ++i) {
			Stmt st = stmts.get(i);
			block[i] = generate(st, scope);
		}
		return scope.add(new Bytecode.Block(block));
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
	private int generate(Stmt stmt, EnclosingScope scope) {
		try {
			if (stmt instanceof VariableDeclaration) {
				return generateVariableDeclaration((VariableDeclaration) stmt, scope);
			} else if (stmt instanceof Assign) {
				return generateAssign((Assign) stmt, scope);
			} else if (stmt instanceof Assert) {
				return generateAssert((Assert) stmt, scope);
			} else if (stmt instanceof Assume) {
				return generateAssume((Assume) stmt, scope);
			} else if (stmt instanceof Return) {
				return generateReturn((Return) stmt, scope);
			} else if (stmt instanceof Debug) {
				return generateDebug((Debug) stmt, scope);
			} else if (stmt instanceof Fail) {
				return generateFail((Fail) stmt, scope);
			} else if (stmt instanceof IfElse) {
				return generateIfElse((IfElse) stmt, scope);
			} else if (stmt instanceof Switch) {
				return generateSwitch((Switch) stmt, scope);
			} else if (stmt instanceof Break) {
				return generateBreak((Break) stmt, scope);
			} else if (stmt instanceof Continue) {
				return generateContinue((Continue) stmt, scope);
			} else if (stmt instanceof NamedBlock) {
				return generateNamedBlock((NamedBlock) stmt, scope);
			} else if (stmt instanceof While) {
				return generateWhile((While) stmt, scope);
			} else if (stmt instanceof DoWhile) {
				return generateDoWhile((DoWhile) stmt, scope);
			} else if (stmt instanceof Expr.FunctionOrMethodCall) {
				return generateAsStmt((Expr.FunctionOrMethodCall) stmt, scope);
			} else if (stmt instanceof Expr.IndirectFunctionOrMethodCall) {
				return generateAsStmt((Expr.IndirectFunctionOrMethodCall) stmt, scope);
			} else if (stmt instanceof Expr.New) {
				return generateNew((Expr.New) stmt, scope);
			} else if (stmt instanceof Skip) {
				return generateSkip((Skip) stmt, scope);
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
		return -1; // deadcode
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
	private int generateVariableDeclaration(VariableDeclaration s, EnclosingScope scope) {
		// Translate initialiser expression (if applicable).
		if (s.expr != null) {
			int operand = generateExpression(s.expr, scope);
			return scope.add(s.type,new Bytecode.VariableDeclaration(s.parameter.name, operand), s.attributes());
		} else {
			return scope.add(s.type,new Bytecode.VariableDeclaration(s.parameter.name), s.attributes());
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
	private int generateAssign(Stmt.Assign s, EnclosingScope scope) throws ResolveError {
		int[] lhs = generate((List) s.lvals, scope);
		int[] rhs = generateMultipleReturns(s.rvals, scope);
		return scope.add(new Bytecode.Assign(lhs, rhs), s.attributes());
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
	private int generateAssert(Stmt.Assert s, EnclosingScope scope) {
		// First, translate assertion
		int operand = generateExpression(s.expr, scope);
		// Second, create assert bytecode
		return scope.add(new Bytecode.Assert(operand), s.attributes());
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
	private int generateAssume(Stmt.Assume s, EnclosingScope scope) {
		// First, translate assumption
		int operand = generateExpression(s.expr, scope);
		// Second, create assert bytecode
		return scope.add(new Bytecode.Assume(operand), s.attributes());
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
	private int generateReturn(Stmt.Return s, EnclosingScope scope) throws ResolveError {
		List<Expr> returns = s.returns;
		// Here, we don't put the type propagated for the return expression.
		// Instead, we use the declared return type of this function. This
		// has the effect of forcing an implicit coercion between the
		// actual value being returned and its required type.		
		int[] operands = generateMultipleReturns(returns,scope);
		return scope.add(new Bytecode.Return(operands), s.attributes());
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
	private int generateSkip(Stmt.Skip s, EnclosingScope scope) {
		return scope.add(new Bytecode.Skip(),s.attributes());
	}

	/**
	 * @param stmt
	 *            --- Statement to be translated.
	 * @param scope
	 *            --- Enclosing scope of this statement.
	 * @return
	 */
	private int generateDebug(Stmt.Debug s, EnclosingScope scope) {
		int operand = generateExpression(s.expr, scope);
		return scope.add(new Bytecode.Debug(operand), s.attributes());
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
	private int generateFail(Stmt.Fail s, EnclosingScope scope) {
		return scope.add(new Bytecode.Fail(), s.attributes());
	}

	/**
	 * @param stmt
	 *            --- Statement to be translated.
	 * @param scope
	 *            --- Enclosing scope of this statement.
	 * @return
	 */
	private int generateIfElse(Stmt.IfElse s, EnclosingScope scope) throws ResolveError {
		// translate condition itself
		FlowResult fr = generateCondition(s.condition, scope);
		// the true/false branches from the enclosing scope. In particular,
		// the case where two variables of the same name are declared with
		// different types.
		int trueBlockIndex = generateBlock(s.trueBranch, fr.trueScope);
		//
		if (!s.falseBranch.isEmpty()) {
			// There is a false branch, so translate that as well
			int falseBlockIndex = generateBlock(s.falseBranch, fr.falseScope);
			//
			return scope.add(new Bytecode.If(fr.operand, trueBlockIndex, falseBlockIndex), s.attributes());
		} else {
			// No false branch to translate
			return scope.add(new Bytecode.If(fr.operand, trueBlockIndex), s.attributes());
		}
	}

	private int generateBreak(Stmt.Break s, EnclosingScope scope) {
		return scope.add(new Bytecode.Break(), s.attributes());
	}

	private int generateContinue(Stmt.Continue s, EnclosingScope scope) {
		return scope.add(new Bytecode.Continue(), s.attributes());
	}

	private int generateSwitch(Stmt.Switch s, EnclosingScope scope) throws Exception {

		int operand = generateExpression(s.expr, scope);
		Bytecode.Case[] cases = new Bytecode.Case[s.cases.size()];

		// FIXME: the following check should really occur earlier in the
		// pipeline. However, it is difficult to do it earlier because it's only
		// after FlowTypeChecker that we have determined the concrete values.
		// See #628
		checkNoDuplicateLabels(s.cases, scope);

		for (int i = 0; i != cases.length; ++i) {
			Stmt.Case c = s.cases.get(i);
			EnclosingScope bodyScope = scope.clone();
			int body = generateBlock(c.stmts, bodyScope);
			cases[i] = new Bytecode.Case(body, c.constants);
		}

		return scope.add(new Bytecode.Switch(operand, cases), s.attributes());
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
	private int generateNamedBlock(Stmt.NamedBlock s, EnclosingScope scope) {
		EnclosingScope bodyScope = scope.clone();
		int block = generateBlock(s.body, bodyScope);
		return scope.add(new Bytecode.NamedBlock(block,s.name),s.attributes());
	}

	/**
	 * @param stmt
	 *            --- Statement to be translated.
	 * @param scope
	 *            --- Enclosing scope of this statement.
	 * @return
	 */
	private int generateWhile(Stmt.While s, EnclosingScope scope) {
		// Translate loop invariant(s)
		int[] invariants = generate(s.invariants, scope);

		// Determine set of modified variables. This is done by
		// traversing the loop body to see which variables are assigned.
		int[] modified = determineModifiedVariables(s.body, scope);

		// Translate loop condition
		int condition = generateExpression(s.condition, scope);

		// Translate loop body
		EnclosingScope bodyScope = scope.clone();
		int body = generateBlock(s.body, bodyScope);
		//
		return scope.add(new Bytecode.While(body, condition, invariants, modified), s.attributes());
	}

	/**
	 * 
	 * @param stmt
	 *            --- Statement to be translated.
	 * @param scope
	 *            --- Enclosing scope of this statement.
	 * @return
	 */
	private int generateDoWhile(Stmt.DoWhile s, EnclosingScope scope) {
		// Determine set of modified variables. This is done by
		// traversing the loop body to see which variables are assigned.
		int[] modified = determineModifiedVariables(s.body, scope);
		// Translate loop body
		EnclosingScope bodyScope = scope.clone();
		int body = generateBlock(s.body, bodyScope);
		// Translate loop invariant(s)
		int[] invariants = generate(s.invariants, scope);
		// Translate loop condition
		int condition = generateExpression(s.condition, scope);
		//
		return scope.add(Nominal.T_VOID,new Bytecode.DoWhile(body, condition, invariants, modified), s.attributes());
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
	public int generateAsStmt(Expr.FunctionOrMethodCall expr, EnclosingScope scope) throws ResolveError {
		//
		int[] operands = generate(expr.arguments, scope);
		Nominal.FunctionOrMethod type = expr.type();
		return scope.add(Nominal.T_VOID,new Bytecode.Invoke(type.nominal(), operands, expr.nid()), expr.attributes());
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
	public int generateAsStmt(Expr.IndirectFunctionOrMethodCall expr, EnclosingScope scope) throws ResolveError {
		//
		int operand = generateExpression(expr.src, scope);
		int[] operands = generate(expr.arguments, scope);
		Nominal.FunctionOrMethod type = expr.type();
		return scope.add(Nominal.T_VOID,new Bytecode.IndirectInvoke(type.nominal(), operand, operands), expr.attributes());
	}

	// =========================================================================
	// Conditions
	// =========================================================================

	/**
	 * Translate a source-level conditional expression into WyIL bytecodes,
	 * using a given scope mapping named variables to locations. This produces a
	 * location index, and updates two environments which represent two sides of
	 * the same coin.
	 * 
	 * @param condition
	 * @param scope
	 * @return A flow result where both true and false scopes are unaliased.
	 * @throws ResolveError
	 */
	public FlowResult generateCondition(Expr condition, EnclosingScope scope) throws ResolveError {
		if (condition instanceof Expr.BinOp) {
			Expr.BinOp bop = (Expr.BinOp) condition;
			switch (bop.op) {
			case AND:
				return generateAndCondition(bop, scope);
			case OR:
				return generateOrCondition(bop, scope);
			case IS:
				return generateIsCondition(bop, scope);
			}

		} else if (condition instanceof Expr.UnOp) {
			Expr.UnOp uop = (Expr.UnOp) condition;
			if (uop.op == Expr.UOp.NOT) {
				return generateNotCondition(uop, scope);
			}
		}
		// default: fall back to standard generation
		int index = generateExpression(condition, scope);
		// We have to clone the two scopes here to prevent them from being
		// aliases.
		return new FlowResult(index, scope.clone(), scope.clone());
	}
	
	/**
	 * Translate a source-level conjunction into a sequence of WyIL bytecodes.
	 * The key challenge here is to correctly propagate the scope information
	 * into the lhs and rhs. Since the rhs is only executed when the lhs holds,
	 * we use the "true scope" from the lhs when translating the rhs. For
	 * example:
	 * 
	 * <pre>
	 * x is int && x >= 0
	 * </pre>
	 * 
	 * Here, the true scope coming out of the lhs will identify
	 * <code>x<code> with type <code>int</code>. This is necessary for the rhs
	 * to make sense. Observe that this is exploiting the fact that operators
	 * have short circuiting behaviour in Whiley.
	 * 
	 * @param condition
	 *            Condition being translated
	 * @param scope
	 *            Enclosing scope going into this condition.
	 * @return
	 * @throws ResolveError
	 */
	public FlowResult generateAndCondition(Expr.BinOp condition, EnclosingScope scope) throws ResolveError {
		FlowResult lhs = generateCondition(condition.lhs, scope);
		FlowResult rhs = generateCondition(condition.rhs, lhs.trueScope);
		int[] operands = new int[] { lhs.operand, rhs.operand };
		int result = scope.add(condition.result(), new Bytecode.Operator(operands, Bytecode.OperatorKind.AND),
				condition.attributes());
		// Must join lhs.falseScope and rhs.falseScope; this can result in the
		// creation of new alias declarations.
		EnclosingScope falseScope = join(scope, lhs.falseScope, rhs.falseScope);
		return new FlowResult(result, rhs.trueScope, falseScope);
	}

	/**
	 * Translate a source-level disjunction into a sequence of WyIL bytecodes.
	 * The key challenge here is to correctly propagate the scope information
	 * into the lhs and rhs. Since the rhs is only executed when the lhs doesn't
	 * hold, we use the "false scope" from the lhs when translating the rhs. For
	 * example:
	 * 
	 * <pre>
	 * x is null || x >= 0
	 * </pre>
	 * 
	 * Here, assume x is declared with type <code>int|null</code>. Then, the
	 * false scope coming out of the lhs will identify
	 * <code>x<code> with type <code>int</code>. This is necessary for the rhs
	 * to make sense. Observe that this is exploiting the fact that operators
	 * have short circuiting behaviour in Whiley.
	 * 
	 * @param condition
	 *            Condition being translated
	 * @param scope
	 *            Enclosing scope going into this condition.
	 * @return
	 * @throws ResolveError
	 */
	public FlowResult generateOrCondition(Expr.BinOp condition, EnclosingScope scope) throws ResolveError {
		FlowResult lhs = generateCondition(condition.lhs, scope);
		FlowResult rhs = generateCondition(condition.rhs, lhs.falseScope);
		int[] operands = new int[] { lhs.operand, rhs.operand };
		int result = scope.add(condition.result(),new Bytecode.Operator(operands, Bytecode.OperatorKind.OR), condition.attributes());
		// Must join lhs.trueScope and rhs.trueScope; this can result in the
		// creation of new alias declarations.
		EnclosingScope trueScope = join(scope,lhs.trueScope,rhs.trueScope);
		return new FlowResult(result, trueScope, rhs.falseScope);
	}
	
	/**
	 * Translate a source-level type test. This produces two potentially updated
	 * scopes, one for the true branch and one for the false branch. In the case
	 * of a variable being retyped, then the true branch contains the updated
	 * type whilst the false branch contains the negated type. For example:
	 * 
	 * <pre>
	 * x is int
	 * </pre>
	 * 
	 * Assum <code>x</code> is declared with type <code>int|null</code>. Then on
	 * the true branch <code>x</code> has type <code>int&(int|null)</code> which
	 * reduces to <code>int</code>. And, on the false branch, <code>x</code> has
	 * type <code>!int&(int|null)</code> which reduces to <code>null</code>.
	 * 
	 * @param condition
	 * @param scope
	 * @return
	 * @throws ResolveError
	 */
	public FlowResult generateIsCondition(Expr.BinOp condition, EnclosingScope scope) throws ResolveError {
		int lhs = generateExpression(condition.lhs,scope);
		int rhs = generateExpression(condition.rhs,scope);
		EnclosingScope trueScope = scope.clone();
		EnclosingScope falseScope = scope.clone();
		// Check to see whether the lhs is a variable being retyped. If so, we
		// need to construct the true/false scopes accordingly.
		if (condition.lhs instanceof Expr.LocalVariable) {
			Expr.LocalVariable var = (Expr.LocalVariable) condition.lhs;
			Nominal varType = var.result();
			Expr.TypeVal typeTest = (Expr.TypeVal) condition.rhs;
			Nominal trueBranchType = Nominal.intersect(varType, typeTest.type);
			Nominal falseBranchType = Nominal.intersect(varType, Nominal.Negation(typeTest.type));
			trueScope.createAlias(trueBranchType, var.var, condition.attributes());
			falseScope.createAlias(falseBranchType, var.var, condition.attributes());
		}
		// do something
		int[] operands = new int[] { lhs, rhs };
		int result = scope.add(condition.result(),new Bytecode.Operator(operands, Bytecode.OperatorKind.IS), condition.attributes());
		return new FlowResult(result, trueScope, falseScope);
	}
	
	public FlowResult generateNotCondition(Expr.UnOp condition, EnclosingScope scope) throws ResolveError {
		FlowResult mhs = generateCondition(condition.mhs, scope);
		int[] operands = new int[] { mhs.operand };
		int result = scope.add(condition.result(), new Bytecode.Operator(operands, Bytecode.OperatorKind.NOT),
				condition.attributes());
		return new FlowResult(result, mhs.falseScope, mhs.trueScope);
	}
	
	/**
	 * Join two scopes together, creating new alias declarations as necessary.
	 * Each scope maps variables to their location index. An ancestor scope is
	 * included, which must be an ancestor of both. When the index of a given
	 * variable differs between the two scopes, this indicates at least one of
	 * them has diverged from the ancestor by introducing an alias. Note that
	 * the only situation in which they have identify the same location for a
	 * given variable is when that matches the ancestor as well.
	 * 
	 * @param leftChild
	 * @param rightChild
	 * @return
	 */
	private EnclosingScope join(EnclosingScope ancestor, EnclosingScope leftChild, EnclosingScope rightChild) {
		EnclosingScope result = ancestor.clone();
		for (String var : ancestor.environment.keySet()) {
			int leftLocation = leftChild.get(var);
			int rightLocation = rightChild.get(var);
			if (leftLocation != rightLocation) {
				// Here, we need to do something.
				Location<?> origDecl = ancestor.getLocation(var);
				Location<?> lhsDecl = leftChild.getLocation(var);
				Location<?> rhsDecl = rightChild.getLocation(var);
				Type type = Type.Union(lhsDecl.getType(), rhsDecl.getType());
				if (type.equals(origDecl.getType())) {
					// Easy case, as no new alias required. Therefore, we can
					// simply reuse the original declaration.
					result.environment.put(var, origDecl.getIndex());
				} else {
					// Harder case. Since the combine type differs from the
					// original declaration, a new alias declaration is
					// required.
					Nominal nominal = Nominal.construct(type, type);
					int newDecl = result.createAlias(nominal, var, Collections.EMPTY_LIST);
					result.environment.put(var, newDecl);
				}
			} 
		}
		return result;
	}
	
	/**
	 * The flow result is essentially a triple being returned from the
	 * generateCondition() family of functions. It's purpose is just to make
	 * their signatures a little neater.
	 * 
	 * @author David J. Pearce
	 *
	 */
	private static class FlowResult {
		/**
		 * Location index for generated expression
		 */
		public final int operand;
		
		/**
		 * Scope which holds on the true branch
		 */
		public final EnclosingScope trueScope;
		
		/**
		 * Scope which holds on the false branch
		 */
		public final EnclosingScope falseScope;
		
		public FlowResult(int operand, EnclosingScope trueScope,  EnclosingScope falseScope) {
			if(trueScope == falseScope) {
				throw new IllegalArgumentException("true/false scopes cannot be aliases");
			}
			this.operand = operand;
			this.trueScope = trueScope;
			this.falseScope = falseScope;
		}
	}
	
	// =========================================================================
	// Expressions
	// =========================================================================

	/**
	 * Translate a source-level expression into a WyIL bytecode block, using a
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
				returns = append(returns,generateFunctionOrMethodCall((Expr.FunctionOrMethodCall) expression,scope));
			} else if(expression instanceof Expr.IndirectFunctionOrMethodCall) {
				returns = append(returns,generateIndirectFunctionOrMethodCall((Expr.IndirectFunctionOrMethodCall) expression,scope));
			} else {
				returns = append(returns,generateExpression(expression,scope));
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
	public int generateExpression(Expr expression, EnclosingScope scope) {
		try {
			if (expression instanceof Expr.Constant) {
				return generateConstant((Expr.Constant) expression, scope);
			} else if (expression instanceof Expr.LocalVariable) {
				return generateLocalVariable((Expr.LocalVariable) expression, scope);
			} else if (expression instanceof Expr.ConstantAccess) {
				return generateConstantAccess((Expr.ConstantAccess) expression, scope);
			} else if (expression instanceof Expr.ArrayInitialiser) {
				return generateArrayInitialiser((Expr.ArrayInitialiser) expression, scope);
			} else if (expression instanceof Expr.ArrayGenerator) {
				return generateArrayGenerator((Expr.ArrayGenerator) expression, scope);
			} else if (expression instanceof Expr.BinOp) {
				return generateBinaryOperator((Expr.BinOp) expression, scope);
			} else if (expression instanceof Expr.Dereference) {
				return generateDereference((Expr.Dereference) expression, scope);
			} else if (expression instanceof Expr.Cast) {
				return generateCast((Expr.Cast) expression, scope);
			} else if (expression instanceof Expr.IndexOf) {
				return generateIndexOf((Expr.IndexOf) expression, scope);
			} else if (expression instanceof Expr.UnOp) {
				return generateUnaryOperator((Expr.UnOp) expression, scope);
			} else if (expression instanceof Expr.FunctionOrMethodCall) {
				return generateFunctionOrMethodCall((Expr.FunctionOrMethodCall) expression, scope);
			} else if (expression instanceof Expr.IndirectFunctionOrMethodCall) {
				return generateIndirectFunctionOrMethodCall((Expr.IndirectFunctionOrMethodCall) expression, scope);
			} else if (expression instanceof Expr.Quantifier) {
				return generateQuantifier((Expr.Quantifier) expression, scope);
			} else if (expression instanceof Expr.FieldAccess) {
				return generateFieldAccess((Expr.FieldAccess) expression, scope);
			} else if (expression instanceof Expr.Record) {
				return generateRecord((Expr.Record) expression, scope);
			} else if (expression instanceof Expr.FunctionOrMethod) {
				return generateFunctionOrMethod((Expr.FunctionOrMethod) expression, scope);
			} else if (expression instanceof Expr.Lambda) {
				return generateLambda((Expr.Lambda) expression, scope);
			} else if (expression instanceof Expr.New) {
				return generateNew((Expr.New) expression, scope);
			} else if (expression instanceof Expr.TypeVal) {
				return generateTypeVal((Expr.TypeVal) expression, scope);
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

	public int generateFunctionOrMethodCall(Expr.FunctionOrMethodCall expr, EnclosingScope scope) throws ResolveError {
		int[] operands = generate(expr.arguments, scope);
		Nominal.FunctionOrMethod type = expr.type();
		return scope.add(type.returns(), new Bytecode.Invoke(type.nominal(), operands, expr.nid()),
				expr.attributes());
	}

	public int generateIndirectFunctionOrMethodCall(Expr.IndirectFunctionOrMethodCall expr, EnclosingScope scope) throws ResolveError {
		int operand = generateExpression(expr.src, scope);
		int[] operands = generate(expr.arguments, scope);
		Nominal.FunctionOrMethod type = expr.type();
		return scope.add(type.returns(), new Bytecode.IndirectInvoke(type.nominal(), operand, operands),
				expr.attributes());
	}

	private int generateConstant(Expr.Constant expr, EnclosingScope scope) {
		Constant val = expr.value;
		Bytecode.Expr operand = new Bytecode.Const(val);
		return scope.add(expr.result(), operand, expr.attributes());
	}

	private int generateTypeVal(Expr.TypeVal expr, EnclosingScope scope) {
		Constant val = new Constant.Type(expr.type.nominal());
		return scope.add(expr.result(), new Bytecode.Const(val), expr.attributes());
	}

	private int generateFunctionOrMethod(Expr.FunctionOrMethod expr, EnclosingScope scope) {
		// FIXME: should really remove Expr.FunctionOrMethod from the AST. This
		// should be just an Expr.Constant
		Type.FunctionOrMethod type = expr.type.nominal();
		Constant.FunctionOrMethod val = new Constant.FunctionOrMethod(expr.nid, type);
		Bytecode.Expr operand = new Bytecode.Const(val);
		return scope.add(expr.result(), operand, expr.attributes());
	}

	private int generateLambda(Expr.Lambda expr, EnclosingScope scope) {
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
		int body = generateExpression(expr.body, lambdaScope);
		//
		return scope.add(lambdaType,
				new Bytecode.Lambda(lambdaType.nominal(), body, parameters, toIntArray(environment)),
				expr.attributes());
	}

	private int generateConstantAccess(Expr.ConstantAccess expr, EnclosingScope scope) throws ResolveError {
		// FIXME: the concept of a constant access should propagate through to
		// the bytecode, rather than having the constants inlined here.
		Constant val = expr.value;
		return scope.add(expr.result(), new Bytecode.Const(val), expr.attributes());
	}

	private int generateLocalVariable(Expr.LocalVariable expr, EnclosingScope scope) throws ResolveError {
		int decl = scope.get(expr.var);
		Location<?> vd = scope.enclosing.getLocation(decl);		
		return scope.add(expr.result(),new Bytecode.VariableAccess(decl), expr.attributes());
	}

	private int generateUnaryOperator(Expr.UnOp expr, EnclosingScope scope) {
		int[] operands = new int[] { generateExpression(expr.mhs, scope) };
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
		return scope.add(expr.result(), new Bytecode.Operator(operands, op), expr.attributes());
	}

	private int generateDereference(Expr.Dereference expr, EnclosingScope scope) {
		int[] operands = new int[] { generateExpression(expr.src, scope) };
		return scope.add(expr.result(), new Bytecode.Operator(operands, Bytecode.OperatorKind.DEREFERENCE),
				expr.attributes());
	}

	private int generateIndexOf(Expr.IndexOf expr, EnclosingScope scope) {
		int[] operands = { generateExpression(expr.src, scope), generateExpression(expr.index, scope) };
		return scope.add(expr.result(), new Bytecode.Operator(operands, Bytecode.OperatorKind.ARRAYINDEX),
				expr.attributes());
	}

	private int generateCast(Expr.Cast expr, EnclosingScope scope) {
		int operand = generateExpression(expr.expr, scope);
		return scope.add(expr.result(), new Bytecode.Convert(operand), expr.attributes());
	}

	private int generateBinaryOperator(Expr.BinOp v, EnclosingScope scope) throws Exception {
		Nominal result = v.result();
		int[] operands = { generateExpression(v.lhs, scope), generateExpression(v.rhs, scope) };
		return scope.add(result, new Bytecode.Operator(operands, OP2BOP(v.op, v, scope.getSourceContext())),
				v.attributes());
	}

	private int generateArrayInitialiser(Expr.ArrayInitialiser expr, EnclosingScope scope) {
		int[] operands = generate(expr.arguments, scope);
		return scope.add(expr.result(), new Bytecode.Operator(operands, Bytecode.OperatorKind.ARRAYCONSTRUCTOR),
				expr.attributes());
	}

	private int generateArrayGenerator(Expr.ArrayGenerator expr, EnclosingScope scope) {
		int[] operands = new int[] { generateExpression(expr.element, scope), generateExpression(expr.count, scope) };
		return scope.add(expr.result(), new Bytecode.Operator(operands, Bytecode.OperatorKind.ARRAYGENERATOR),
				expr.attributes());
	}

	private int generateQuantifier(Expr.Quantifier expr, EnclosingScope scope) {
		EnclosingScope quantifierScope = scope.clone();
		// First, translate sources and declare variables in the quantifier
		// scope.
		Bytecode.Range[] ranges = new Bytecode.Range[expr.sources.size()];
		for (int i = 0; i != ranges.length; ++i) {
			Triple<String, Expr, Expr> t = expr.sources.get(i);
			int start = generateExpression(t.second(), quantifierScope);
			int end = generateExpression(t.third(), quantifierScope);
			// FIXME: the attributes provided here are not very "precise".
			int var = quantifierScope.declare(Nominal.T_INT, t.first(), expr.attributes());
			ranges[i] = new Bytecode.Range(var, start, end);
		}
		// Second, translate the quantifier body in the context of the new
		// scope.
		int body = generateExpression(expr.condition, quantifierScope);
		//
		Bytecode.QuantifierKind kind = Bytecode.QuantifierKind.valueOf(expr.cop.name());
		return scope.add(expr.result(), new Bytecode.Quantifier(kind, body, ranges), expr.attributes());
	}

	private int generateRecord(Expr.Record expr, EnclosingScope scope) {
		ArrayList<String> keys = new ArrayList<String>(expr.fields.keySet());
		Collections.sort(keys);
		int[] operands = new int[expr.fields.size()];
		for (int i = 0; i != operands.length; ++i) {
			String key = keys.get(i);
			Expr arg = expr.fields.get(key);
			operands[i] = generateExpression(arg, scope);
		}
		return scope.add(expr.result(), new Bytecode.Operator(operands, Bytecode.OperatorKind.RECORDCONSTRUCTOR),
				expr.attributes());
	}

	private int generateFieldAccess(Expr.FieldAccess expr, EnclosingScope scope) {
		int operand = generateExpression(expr.src, scope);
		return scope.add(expr.result(), new Bytecode.FieldLoad(operand, expr.name), expr.attributes());
	}

	private int generateNew(Expr.New expr, EnclosingScope scope) throws ResolveError {
		int[] operands = new int[] { generateExpression(expr.expr, scope) };
		return scope.add(expr.result(), new Bytecode.Operator(operands, Bytecode.OperatorKind.NEW), expr.attributes());
	}

	private int[] generate(List<Expr> arguments, EnclosingScope scope) {
		int[] operands = new int[arguments.size()];
		for (int i = 0; i != operands.length; ++i) {
			Expr arg = arguments.get(i);
			operands[i] = generateExpression(arg, scope);
		}
		return operands;
	}

	// =========================================================================
	// Helpers
	// =========================================================================

	/**
	 * Determine the list of variables which are assigned in a statement block,
	 * or any child block.
	 * 
	 * @param block
	 * @return
	 */
	private int[] determineModifiedVariables(List<Stmt> block, EnclosingScope scope) {
		SyntaxTree tree = scope.getSyntaxTree();
		HashSet<Integer> modified = new HashSet<Integer>();
		determineModifiedVariables(block,scope,modified);
		int[] result = new int[modified.size()];
		int index = 0;
		for(Integer i : modified) {
			Bytecode.VariableAccess va = new Bytecode.VariableAccess(i);
			Location<?> location = tree.getLocation(i);
			Nominal type = Nominal.construct(location.getType(),location.getType());
			result[index++] = scope.add(type,va);
		}
		return result;
	}
	
	private void determineModifiedVariables(List<Stmt> block, EnclosingScope scope, Set<Integer> modified) {
		for(Stmt stmt : block) {
			if(stmt instanceof Stmt.Assign) {
				Stmt.Assign s = (Stmt.Assign) stmt;
				for(Expr.LVal lval : s.lvals) {
					Expr.LocalVariable lv = extractAssignedVariable(lval,scope);
					if(lv == null) {
						// FIXME: this is not an ideal solution long term. In
						// particular, we really need this method to detect not
						// just modified variables, but also modified locations
						// in general (e.g. assignments through references, etc)  
						continue;
					}
					Integer variableIndex = scope.get(lv.var); 
					if(lv != null && variableIndex != null) {
						modified.add(variableIndex);
					}
				}
			} else if(stmt instanceof Stmt.DoWhile) {
				Stmt.DoWhile s = (Stmt.DoWhile) stmt; 
				determineModifiedVariables(s.body,scope,modified);
			} else if(stmt instanceof Stmt.IfElse) {
				Stmt.IfElse s = (Stmt.IfElse) stmt; 
				determineModifiedVariables(s.trueBranch,scope,modified);
				determineModifiedVariables(s.falseBranch,scope,modified);
			} else if(stmt instanceof Stmt.NamedBlock) {
				Stmt.NamedBlock s = (Stmt.NamedBlock) stmt;
				determineModifiedVariables(s.body,scope,modified);
			} else if(stmt instanceof Stmt.Switch) {
				Stmt.Switch s = (Stmt.Switch) stmt;
				for(Stmt.Case c : s.cases) {
					determineModifiedVariables(c.stmts,scope,modified);
				}
			} else if(stmt instanceof Stmt.While) {
				Stmt.While s = (Stmt.While) stmt; 
				determineModifiedVariables(s.body,scope,modified);
			} 
		}
	}
	
	private Expr.LocalVariable extractAssignedVariable(Expr.LVal lval, EnclosingScope scope) {
		if (lval instanceof Expr.LocalVariable) {
			return (Expr.LocalVariable) lval;
		} else if (lval instanceof Expr.FieldAccess) {
			Expr.FieldAccess e = (Expr.FieldAccess) lval;
			return extractAssignedVariable((Expr.LVal) e.src, scope);
		} else if (lval instanceof Expr.IndexOf) {
			Expr.IndexOf e = (Expr.IndexOf) lval;
			return extractAssignedVariable((Expr.LVal) e.src, scope);
		} else if (lval instanceof Expr.Dereference) {
			return null;
		} else {
			internalFailure(errorMessage(INVALID_LVAL_EXPRESSION), scope.getSourceContext(), (Expr) lval);
			return null; // dead code
		}
	}
	
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
		 * Maps variables to their WyIL location.
		 */
		private final HashMap<String, Integer> environment;		

		/**
		 * The enclosing source file scope (needed for error reporting)
		 */
		private final WhileyFile.Context context;
		
		/**
		 * The enclosing syntax tree
		 */
		private final SyntaxTree enclosing;

		public EnclosingScope(SyntaxTree enclosing, WhileyFile.Context context) {
			this(new HashMap<String, Integer>(), enclosing, context);
		}

		private EnclosingScope(Map<String, Integer> environment, SyntaxTree enclosing, WhileyFile.Context context) {
			this.environment = new HashMap<String, Integer>(environment);
			this.enclosing = enclosing;
			this.context = context;
		}

		public SyntaxTree getSyntaxTree() {
			return enclosing;
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

		public Location<?> getLocation(String name) {
			return enclosing.getLocation(environment.get(name));
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
			List<SyntaxTree.Location<?>> locations = enclosing.getLocations();
			int index = locations.size();
			environment.put(name, index);
			Bytecode.VariableDeclaration decl = new Bytecode.VariableDeclaration(name);
			Type locationType = normalise(type);
			locations.add(new SyntaxTree.Location<Bytecode>(enclosing, locationType, decl, attributes));
			return index;
		}

		/**
		 * Declare a variable alias in the enclosing bytecode forest.
		 * 
		 * @param type
		 *            The declared type of the variable
		 * @param name
		 *            The declare name of the variable
		 * @return
		 */
		public int createAlias(Nominal type, String name, List<Attribute> attributes) {
			List<SyntaxTree.Location<?>> locations = enclosing.getLocations();
			int original = environment.get(name);
			int index = locations.size();
			environment.put(name, index);
			Bytecode.AliasDeclaration alias = new Bytecode.AliasDeclaration(original);
			Type locationType = normalise(type);			
			locations.add(new SyntaxTree.Location<Bytecode>(enclosing, locationType, alias, attributes));
			return index;
		}

		public int add(Bytecode stmt, Attribute... attributes) {
			return add(stmt,Arrays.asList(attributes));
		}

		public int add(Nominal type, Bytecode stmt, Attribute... attributes) {
			return add(type,stmt,Arrays.asList(attributes));
		}

		public int add(Bytecode operand, List<Attribute> attributes) {
			return add(new Nominal[0],operand,attributes);
		}
		
		public int add(Nominal type, Bytecode operand, List<Attribute> attributes) {
			return add(new Nominal[]{type},operand,attributes);
		}

		/**
		 * Allocate an operand on the stack.
		 * 
		 * @param Type
		 * @param operand
		 * @return
		 */
		public int add(Nominal[] nominals, Bytecode operand, List<Attribute> attributes) {
			List<SyntaxTree.Location<?>> locations =  enclosing.getLocations();
			Type[] types = new Type[nominals.length];
			for (int i = 0; i != types.length; ++i) {
				types[i] = normalise(nominals[i]);
			}
			return add(types,operand,attributes);
		}

		/**
		 * Allocate a multi-operand on the stack.
		 * 
		 * @param Type
		 * @param operand
		 * @return
		 */
		public int add(List<Nominal> types, Bytecode operand, List<Attribute> attributes) {
			Type[] nominals = new Type[types.size()];
			for (int i = 0; i != nominals.length; ++i) {
				nominals[i] = normalise(types.get(i));
			}
			return add(nominals,operand,attributes);
		}

		private int add(Type[] types, Bytecode operand, List<Attribute> attributes) {
			List<SyntaxTree.Location<?>> locations = enclosing.getLocations();
			int index = locations.size();
			locations.add(new SyntaxTree.Location<Bytecode>(enclosing, types, operand, attributes));
			// Check whether this is declaring a new variable or not.
			if (operand instanceof Bytecode.VariableDeclaration) {
				Bytecode.VariableDeclaration vd = (Bytecode.VariableDeclaration) operand;
				environment.put(vd.getName(), index);
			}
			return index;
		}

		/**
		 * Create a new clone scope. This is a subscope where new variables
		 * can be declared and, furthermore, it corresponds to a new block in
		 * the underlying forest.
		 * 
		 * @return
		 */
		public EnclosingScope clone() {
			return new EnclosingScope(environment, enclosing, context);
		}
		
		private Type normalise(Nominal type) {
			Type result = type.nominal();
			// FIXME: the following check is a hack to handle limitations
			// in the way Nominal types are currently handled.  #629
			if(result instanceof Type.Void) {
				result = type.raw();
			}
			return result;
		}
	}
}
