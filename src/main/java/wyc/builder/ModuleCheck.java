// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyc.builder;

import java.util.*;

import wybs.lang.Build;
import wybs.lang.SyntacticElement;
import wybs.lang.SyntaxError;
import wybs.lang.SyntaxError.InternalFailure;
import wyc.lang.Expr;
import wyc.lang.Stmt;
import wyc.lang.WhileyFile;
import wycc.util.Pair;
import wycc.util.Triple;
import wyil.lang.Type;

import static wyil.util.ErrorMessages.*;

/**
 * <p>
 * Performs a number of simplistic checks that a module is syntactically
 * correct. This includes the following
 * </p>
 * <ul>
 * <li><b>Functions cannot have side-effects</b>. This includes sending messages
 * to actors, calling headless methods and spawning processes.</li>
 * <li><b>Functions/Methods cannot throw exceptions unless they are
 * declared</b>. Thus, if a method or function throws an exception an
 * appropriate throws clause is required.
 * <li><b>Every catch handler must catch something</b>. It is a syntax error if
 * a catch handler exists which can never catch anything (i.e. it is dead-code).
 * </li>
 * </ul>
 *
 * @author David J. Pearce
 *
 */
public class ModuleCheck {
	private WhileyFile file;

	public ModuleCheck(WhileyFile file) {
		this.file = file;
	}

	public void check() {
		for (WhileyFile.Declaration d : file.declarations) {
			check(d);
		}
	}

	public void check(WhileyFile.Declaration declaration) {
		if(declaration instanceof WhileyFile.Type) {
			checkTypeDeclaration((WhileyFile.Type) declaration);
		} else if(declaration instanceof WhileyFile.Property) {
			checkPropertyDeclaration((WhileyFile.Property) declaration);
		} else if(declaration instanceof WhileyFile.Function) {
			checkFunctionDeclaration((WhileyFile.Function) declaration);
		} else if(declaration instanceof WhileyFile.Method) {
			checkMethodDeclaration((WhileyFile.Method) declaration);
		} else {
			// Ignore others
		}
	}


	private enum Context {
		METHOD,
		FUNCTION,
		INVARIANT,
		REQUIRES,
		ENSURES,
		ASSERTION
	}


	private void checkTypeDeclaration(WhileyFile.Type declaration) {
		for(Expr e : declaration.invariant) {
			checkExpression(e, Context.INVARIANT);
		}
	}

	private void checkPropertyDeclaration(WhileyFile.Property declaration) {
		for(Expr e : declaration.requires) {
			checkExpression(e, Context.INVARIANT);
		}
	}

	private void checkFunctionDeclaration(WhileyFile.Function declaration) {
		for(Expr e : declaration.requires) {
			checkExpression(e, Context.REQUIRES);
		}
		for(Expr e : declaration.ensures) {
			checkExpression(e, Context.ENSURES);
		}
		checkStatements(declaration.statements,Context.FUNCTION);
	}

	private void checkMethodDeclaration(WhileyFile.Method declaration) {
		for(Expr e : declaration.requires) {
			checkExpression(e, Context.REQUIRES);
		}
		for(Expr e : declaration.ensures) {
			checkExpression(e, Context.ENSURES);
		}
		checkStatements(declaration.statements,Context.METHOD);
	}

	private void checkStatements(List<Stmt> statements, Context context) {
		for (Stmt s : statements) {
			checkStatement(s, context);
		}
	}

	private void checkStatement(Stmt statement, Context context) {
		try {
			if(statement instanceof Stmt.Assert) {
				checkAssert((Stmt.Assert) statement, context);
			} else if(statement instanceof Stmt.Assign) {
				checkAssign((Stmt.Assign) statement, context);
			} else if(statement instanceof Stmt.Assume) {
				checkAssume((Stmt.Assume) statement, context);
			} else if(statement instanceof Stmt.Break) {
				checkBreak((Stmt.Break) statement, context);
			} else if(statement instanceof Stmt.Continue) {
				checkContinue((Stmt.Continue) statement, context);
			} else if(statement instanceof Stmt.Debug) {
				checkDebug((Stmt.Debug) statement, context);
			} else if(statement instanceof Stmt.DoWhile) {
				checkDoWhile((Stmt.DoWhile) statement, context);
			} else if(statement instanceof Stmt.Fail) {
				check((Stmt.Fail) statement, context);
			} else if(statement instanceof Expr.FunctionOrMethodCall) {
				checkFunctionOrMethodCall((Expr.FunctionOrMethodCall) statement, context);
			} else if(statement instanceof Stmt.IfElse) {
				checkIfElse((Stmt.IfElse) statement, context);
			} else if(statement instanceof Expr.IndirectFunctionOrMethodCall) {
				checkIndirectFunctionOrMethodCall((Expr.IndirectFunctionOrMethodCall) statement, context);
			} else if(statement instanceof Stmt.NamedBlock) {
				checkNamedBlock((Stmt.NamedBlock) statement, context);
			} else if(statement instanceof Stmt.Return) {
				checkReturn((Stmt.Return) statement, context);
			} else if(statement instanceof Stmt.Skip) {
				checkSkip((Stmt.Skip) statement, context);
			} else if(statement instanceof Stmt.Switch) {
				checkSwitch((Stmt.Switch) statement, context);
			} else if(statement instanceof Stmt.VariableDeclaration) {
				checkVariableDeclaration((Stmt.VariableDeclaration) statement, context);
			} else if(statement instanceof Stmt.While) {
				checkWhile((Stmt.While) statement, context);
			} else {
				throw new InternalFailure("unknown statement encountered",file.getEntry(),statement);
			}
		} catch(SyntaxError e) {
			throw e;
		} catch(Throwable t) {
			throw new InternalFailure(t.getMessage(),file.getEntry(),statement,t);
		}
	}

	private void checkAssert(Stmt.Assert stmt, Context context) {
		checkExpression(stmt.expr, context);
	}

	private void checkAssign(Stmt.Assign stmt, Context context) {
		if(context != Context.METHOD) {
			// left-hand side
			for (Expr lval : stmt.lvals) {
				if (lval instanceof Expr.LocalVariable) {
					// Skip local variables since they are being assigned
				} else {
					checkExpression(lval, context);
				}
			}
			// right-hand side
			for (Expr rval : stmt.rvals) {
				checkExpression(rval, context);
			}
		}
	}

	private void checkAssume(Stmt.Assume stmt, Context context) {
		checkExpression(stmt.expr, Context.ASSERTION);
	}

	private void checkBreak(Stmt.Break stmt, Context context) {
	}

	private void checkContinue(Stmt.Continue stmt, Context context) {

	}

	private void checkDebug(Stmt.Debug stmt, Context context) {
		if(context != Context.METHOD) {
			checkExpression(stmt.expr, context);
		}
	}

	private void checkDoWhile(Stmt.DoWhile stmt, Context context) {
		//
		checkStatements(stmt.body, context);
		//
		if(context != Context.METHOD) {
			checkExpression(stmt.condition, context);
		}
		//
		for(Expr e : stmt.invariants) {
			checkExpression(e,Context.INVARIANT);
		}
	}

	private void check(Stmt.Fail stmt, Context context) {

	}

	private void checkIfElse(Stmt.IfElse stmt, Context context) {
		if(context != Context.METHOD) {
			checkExpression(stmt.condition, context);
		}
		//
		checkStatements(stmt.trueBranch, context);
		checkStatements(stmt.falseBranch, context);
	}

	private void checkNamedBlock(Stmt.NamedBlock stmt, Context context) {
		checkStatements(stmt.body,context);
	}

	private void checkReturn(Stmt.Return stmt, Context context) {
		if(context != Context.METHOD) {
			for(Expr e : stmt.returns) {
				checkExpression(e, context);
			}
		}
	}

	private void checkSkip(Stmt.Skip stmt, Context context) {
	}

	private void checkSwitch(Stmt.Switch stmt, Context context) {
		if(context != Context.METHOD) {
			checkExpression(stmt.expr, context);
		}
		//
		for(Stmt.Case c : stmt.cases) {
			checkStatements(c.stmts, context);
		}
	}

	private void checkVariableDeclaration(Stmt.VariableDeclaration stmt, Context context) {
		if (stmt.expr != null && context != Context.METHOD) {
			checkExpression(stmt.expr, context);
		}
	}

	private void checkWhile(Stmt.While stmt, Context context) {
		if(context != Context.METHOD) {
			checkExpression(stmt.condition, context);
		}
		//
		for(Expr e : stmt.invariants) {
			checkExpression(e,Context.INVARIANT);
		}
		//
		checkStatements(stmt.body, context);
	}

	private void checkLVal(Expr.LVal expression, Context context) {
		try {
			if(expression instanceof Expr.Dereference) {
				checkDereferenceLVal((Expr.Dereference) expression, context);
			} else if(expression instanceof Expr.FieldAccess) {
				checkFieldAccessLVal((Expr.FieldAccess) expression, context);
			} else if(expression instanceof Expr.IndexOf) {
				checkIndexOfLVal((Expr.IndexOf) expression, context);
			} else if(expression instanceof Expr.LocalVariable) {
				checkLocalVariableLVal((Expr.LocalVariable) expression, context);
			} else {
				throw new InternalFailure("unknown expression encountered",file.getEntry(),expression);
			}
		} catch(SyntaxError e) {
			throw e;
		} catch(Throwable t) {
			throw new InternalFailure("internal failure",file.getEntry(),expression,t);
		}
	}


	private void checkDereferenceLVal(Expr.Dereference expression, Context context) {
		if(context != Context.METHOD) {
			invalidReferenceAccess(expression,context);
		}
		checkLVal((Expr.LVal) expression.src,context);
	}

	private void checkFieldAccessLVal(Expr.FieldAccess expression, Context context) {
		checkExpression(expression.src,context);
	}

	private void checkIndexOfLVal(Expr.IndexOf expression, Context context) {
		checkLVal((Expr.LVal) expression.src,context);
		checkExpression(expression.index,context);
	}

	private void checkLocalVariableLVal(Expr.LocalVariable expression, Context context) {

	}

	private void checkExpression(Expr expression, Context context) {
		try {
			if(expression instanceof Expr.ArrayInitialiser) {
				checkArrayInitialiser((Expr.ArrayInitialiser) expression, context);
			} else if(expression instanceof Expr.ArrayGenerator) {
				checkArrayGenerator((Expr.ArrayGenerator) expression, context);
			} else if(expression instanceof Expr.BinOp) {
				checkBinOp((Expr.BinOp) expression, context);
			} else if(expression instanceof Expr.Cast) {
				checkCast((Expr.Cast) expression, context);
			} else if(expression instanceof Expr.Constant) {
				checkConstant((Expr.Constant) expression, context);
			} else if(expression instanceof Expr.ConstantAccess) {
				checkConstantAccess((Expr.ConstantAccess) expression, context);
			} else if(expression instanceof Expr.Dereference) {
				checkDereference((Expr.Dereference) expression, context);
			} else if(expression instanceof Expr.FieldAccess) {
				checkFieldAccess((Expr.FieldAccess) expression, context);
			} else if(expression instanceof Expr.FunctionOrMethod) {
				checkFunctionOrMethod((Expr.FunctionOrMethod) expression, context);
			} else if(expression instanceof Expr.FunctionOrMethodCall) {
				checkFunctionOrMethodCall((Expr.FunctionOrMethodCall) expression, context);
			} else if(expression instanceof Expr.IndexOf) {
				checkIndexOf((Expr.IndexOf) expression, context);
			} else if(expression instanceof Expr.IndirectFunctionOrMethodCall) {
				checkIndirectFunctionOrMethodCall((Expr.IndirectFunctionOrMethodCall) expression, context);
			} else if(expression instanceof Expr.Lambda) {
				checkLambda((Expr.Lambda) expression, context);
			} else if(expression instanceof Expr.LocalVariable) {
				checkLocalVariable((Expr.LocalVariable) expression, context);
			} else if(expression instanceof Expr.New) {
				checkNew((Expr.New) expression, context);
			} else if(expression instanceof Expr.Quantifier) {
				checkQuantifier((Expr.Quantifier) expression, context);
			} else if(expression instanceof Expr.Record) {
				checkRecord((Expr.Record) expression, context);
			} else if(expression instanceof Expr.TypeVal) {
				checkTypeVal((Expr.TypeVal) expression, context);
			} else if(expression instanceof Expr.UnOp) {
				checkUnOp((Expr.UnOp) expression, context);
			} else {
				throw new InternalFailure("unknown expression encountered",file.getEntry(),expression);
			}
		} catch(SyntaxError e) {
			throw e;
		} catch(Throwable t) {
			throw new InternalFailure("internal failure",file.getEntry(),expression,t);
		}
	}

	private void checkArrayInitialiser(Expr.ArrayInitialiser expression, Context context) {
		for(Expr e : expression.arguments) {
			checkExpression(e,context);
		}
	}

	private void checkArrayGenerator(Expr.ArrayGenerator expression, Context context) {
		checkExpression(expression.element,context);
		checkExpression(expression.count,context);
	}

	private void checkBinOp(Expr.BinOp expression, Context context) {
		checkExpression(expression.lhs,context);
		checkExpression(expression.rhs,context);
	}

	private void checkCast(Expr.Cast expression, Context context) {
		checkExpression(expression.expr,context);
	}

	private void checkConstant(Expr.Constant expression, Context context) {

	}

	private void checkConstantAccess(Expr.ConstantAccess expression, Context context) {

	}

	private void checkDereference(Expr.Dereference expression, Context context) {
		checkExpression(expression.src,context);
	}

	private void checkFieldAccess(Expr.FieldAccess expression, Context context) {
		checkExpression(expression.src,context);
	}

	private void checkFunctionOrMethod(Expr.FunctionOrMethod expression, Context context) {

	}

	private void checkFunctionOrMethodCall(Expr.FunctionOrMethodCall expression, Context context) {
		if(context != Context.METHOD && expression.type() instanceof Type.Method) {
			invalidMethodCall(expression,context);
		}
		for(Expr p : expression.arguments) {
			checkExpression(p,context);
		}
	}

	private void checkIndexOf(Expr.IndexOf expression, Context context) {
		checkExpression(expression.src,context);
		checkExpression(expression.index,context);
	}

	private void checkIndirectFunctionOrMethodCall(Expr.IndirectFunctionOrMethodCall expression, Context context) {
		if(context != Context.METHOD && expression.type() instanceof Type.Method) {
			invalidMethodCall(expression,context);
		}
		checkExpression(expression.src,context);
		for(Expr p : expression.arguments) {
			checkExpression(p,context);
		}
	}

	private void checkLambda(Expr.Lambda expression, Context context) {
		// Check body of the lambda
		checkExpression(expression.body,context);
	}

	private void checkLocalVariable(Expr.LocalVariable expression, Context context) {

	}

	private void checkNew(Expr.New expression, Context context) {
		if(context != Context.METHOD) {
			invalidObjectAllocation(expression,context);
		}
		checkExpression(expression.expr,context);
	}

	private void checkQuantifier(Expr.Quantifier expression, Context context) {
		for(Triple<String,Expr,Expr> p : expression.sources) {
			checkExpression(p.second(),context);
			checkExpression(p.third(),context);
		}
		checkExpression(expression.condition,context);
	}

	private void checkRecord(Expr.Record expression, Context context) {
		for(Map.Entry<String,Expr> e : expression.fields.entrySet()) {
			checkExpression(e.getValue(),context);
		}
	}

	private void checkTypeVal(Expr.TypeVal expression, Context context) {

	}

	private void checkUnOp(Expr.UnOp expression, Context context) {
		checkExpression(expression.mhs,context);
	}

	private void invalidObjectAllocation(SyntacticElement expression, Context context) {
		String msg = errorMessage(ALLOCATION_NOT_PERMITTED);
		throw new SyntaxError(msg, file.getEntry(), expression);
	}

	private void invalidMethodCall(SyntacticElement expression, Context context) {
		String msg = errorMessage(METHODCALL_NOT_PERMITTED);
		throw new SyntaxError(msg, file.getEntry(), expression);
	}

	private void invalidReferenceAccess(SyntacticElement expression, Context context) {
		String msg= errorMessage(REFERENCE_ACCESS_NOT_PERMITTED);
		throw new SyntaxError(msg, file.getEntry(), expression);
	}
}
