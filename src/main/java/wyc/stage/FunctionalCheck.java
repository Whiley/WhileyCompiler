// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyc.stage;

import java.util.*;

import wybs.lang.SyntacticItem;
import wybs.lang.SyntaxError;
import wybs.lang.SyntaxError.InternalFailure;
import wyc.lang.WhileyFile;

import static wyc.lang.WhileyFile.*;
import static wyc.util.ErrorMessages.*;

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
public class FunctionalCheck {
	private WhileyFile file;

	public FunctionalCheck(WhileyFile file) {
		this.file = file;
	}

	public void check() {
		for (WhileyFile.Declaration d : file.getDeclarations()) {
			check(d);
		}
	}

	public void check(WhileyFile.Declaration declaration) {
		if(declaration instanceof Declaration.Type) {
			checkTypeDeclaration((Declaration.Type) declaration);
		} else if(declaration instanceof Declaration.Property) {
			checkPropertyDeclaration((Declaration.Property) declaration);
		} else if(declaration instanceof Declaration.Function) {
			checkFunctionDeclaration((Declaration.Function) declaration);
		} else if(declaration instanceof Declaration.Method) {
			checkMethodDeclaration((Declaration.Method) declaration);
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


	private void checkTypeDeclaration(Declaration.Type declaration) {
		for(Expr e : declaration.getInvariant()) {
			checkExpression(e, Context.INVARIANT);
		}
	}

	private void checkPropertyDeclaration(Declaration.Property declaration) {
		for(Expr e : declaration.getInvariant()) {
			checkExpression(e, Context.INVARIANT);
		}
	}

	private void checkFunctionDeclaration(Declaration.Function declaration) {
		for(Expr e : declaration.getRequires()) {
			checkExpression(e, Context.REQUIRES);
		}
		for(Expr e : declaration.getEnsures()) {
			checkExpression(e, Context.ENSURES);
		}
		checkBlock(declaration.getBody(),Context.FUNCTION);
	}

	private void checkMethodDeclaration(Declaration.Method declaration) {
		for(Expr e : declaration.getRequires()) {
			checkExpression(e, Context.REQUIRES);
		}
		for(Expr e : declaration.getEnsures()) {
			checkExpression(e, Context.ENSURES);
		}
		checkBlock(declaration.getBody(),Context.METHOD);
	}

	private void checkBlock(Stmt.Block block, Context context) {
		for (int i=0;i!=block.size();++i) {
			checkStatement(block.getOperand(i), context);
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
			} else if(statement instanceof Expr.Invoke) {
				checkInvoke((Expr.Invoke) statement, context);
			} else if(statement instanceof Stmt.IfElse) {
				checkIfElse((Stmt.IfElse) statement, context);
			} else if(statement instanceof Expr.IndirectInvoke) {
				checkIndirectFunctionOrMethodCall((Expr.IndirectInvoke) statement, context);
			} else if(statement instanceof Stmt.NamedBlock) {
				checkNamedBlock((Stmt.NamedBlock) statement, context);
			} else if(statement instanceof Stmt.Return) {
				checkReturn((Stmt.Return) statement, context);
			} else if(statement instanceof Stmt.Skip) {
				checkSkip((Stmt.Skip) statement, context);
			} else if(statement instanceof Stmt.Switch) {
				checkSwitch((Stmt.Switch) statement, context);
			} else if(statement instanceof Declaration.Variable) {
				checkVariableDeclaration((Declaration.Variable) statement, context);
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
		checkExpression(stmt.getCondition(), context);
	}

	private void checkAssign(Stmt.Assign stmt, Context context) {
		if(context != Context.METHOD) {
			// left-hand side
			for (Expr lval : stmt.getLeftHandSide()) {
				if (lval instanceof Expr.VariableAccess) {
					// Skip local variables since they are being assigned
				} else {
					checkExpression(lval, context);
				}
			}
			// right-hand side
			for (Expr rval : stmt.getRightHandSide()) {
				checkExpression(rval, context);
			}
		}
	}

	private void checkAssume(Stmt.Assume stmt, Context context) {
		checkExpression(stmt.getCondition(), Context.ASSERTION);
	}

	private void checkBreak(Stmt.Break stmt, Context context) {
	}

	private void checkContinue(Stmt.Continue stmt, Context context) {

	}

	private void checkDebug(Stmt.Debug stmt, Context context) {
		if(context != Context.METHOD) {
			checkExpression(stmt.getCondition(), context);
		}
	}

	private void checkDoWhile(Stmt.DoWhile stmt, Context context) {
		//
		checkBlock(stmt.getBody(), context);
		//
		if(context != Context.METHOD) {
			checkExpression(stmt.getCondition(), context);
		}
		//
		for(Expr e : stmt.getInvariant()) {
			checkExpression(e,Context.INVARIANT);
		}
	}

	private void check(Stmt.Fail stmt, Context context) {

	}

	private void checkIfElse(Stmt.IfElse stmt, Context context) {
		if(context != Context.METHOD) {
			checkExpression(stmt.getCondition(), context);
		}
		//
		checkBlock(stmt.getTrueBranch(), context);
		if(stmt.hasFalseBranch()) {
			checkBlock(stmt.getFalseBranch(), context);
		}
	}

	private void checkNamedBlock(Stmt.NamedBlock stmt, Context context) {
		checkBlock(stmt.getBlock(),context);
	}

	private void checkReturn(Stmt.Return stmt, Context context) {
		if(context != Context.METHOD) {
			checkExpressions(stmt.getOperand(), context);
		}
	}

	private void checkSkip(Stmt.Skip stmt, Context context) {
	}

	private void checkSwitch(Stmt.Switch stmt, Context context) {
		if(context != Context.METHOD) {
			checkExpression(stmt.getCondition(), context);
		}
		//
		for(Stmt.Case c : stmt.getCases()) {
			checkExpressions(c.getConditions(), context);
			checkBlock(c.getBlock(), context);
		}
	}

	private void checkVariableDeclaration(Declaration.Variable stmt, Context context) {
		if (stmt.hasInitialiser() && context != Context.METHOD) {
			checkExpression(stmt.getInitialiser(), context);
		}
	}

	private void checkWhile(Stmt.While stmt, Context context) {
		if(context != Context.METHOD) {
			checkExpression(stmt.getCondition(), context);
		}
		//
		checkExpressions(stmt.getInvariant(),Context.INVARIANT);
		//
		checkBlock(stmt.getBody(), context);
	}

	private void checkLVal(LVal expression, Context context) {
		try {
			if(expression instanceof Expr.Dereference) {
				checkDereferenceLVal((Expr.Dereference) expression, context);
			} else if(expression instanceof Expr.RecordAccess) {
				checkFieldAccessLVal((Expr.RecordAccess) expression, context);
			} else if(expression instanceof Expr.ArrayAccess) {
				checkIndexOfLVal((Expr.ArrayAccess) expression, context);
			} else if(expression instanceof Expr.VariableAccess) {
				checkLocalVariableLVal((Expr.VariableAccess) expression, context);
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
		checkLVal((LVal) expression.getOperand(),context);
	}

	private void checkFieldAccessLVal(Expr.RecordAccess expression, Context context) {
		checkExpression(expression.getSource(),context);
	}

	private void checkIndexOfLVal(Expr.ArrayAccess expression, Context context) {
		checkLVal((LVal) expression.getSource(),context);
		checkExpression(expression.getSubscript(),context);
	}

	private void checkLocalVariableLVal(Expr.VariableAccess expression, Context context) {

	}

	private void checkExpressions(Tuple<Expr> expressions, Context context) {
		for(Expr e : expressions) {
			checkExpression(e,context);
		}
	}

	private void checkExpression(Expr expression, Context context) {
		try {
			if(expression instanceof Expr.ArrayInitialiser) {
				checkArrayInitialiser((Expr.ArrayInitialiser) expression, context);
			} else if(expression instanceof Expr.ArrayGenerator) {
				checkArrayGenerator((Expr.ArrayGenerator) expression, context);
			} else if(expression instanceof Expr.Operator) {
				checkBinOp((Expr.Operator) expression, context);
			} else if(expression instanceof Expr.Cast) {
				checkCast((Expr.Cast) expression, context);
			} else if(expression instanceof Expr.Constant) {
				checkConstant((Expr.Constant) expression, context);
			} else if(expression instanceof Expr.StaticVariableAccess) {
				checkConstantAccess((Expr.StaticVariableAccess) expression, context);
			} else if(expression instanceof Expr.RecordAccess) {
				checkFieldAccess((Expr.RecordAccess) expression, context);
			} else if(expression instanceof Expr.Invoke) {
				checkInvoke((Expr.Invoke) expression, context);
			} else if(expression instanceof Expr.Is) {
				checkIs((Expr.Is) expression, context);
			} else if(expression instanceof Expr.ArrayAccess) {
				checkIndexOf((Expr.ArrayAccess) expression, context);
			} else if(expression instanceof Expr.IndirectInvoke) {
				checkIndirectFunctionOrMethodCall((Expr.IndirectInvoke) expression, context);
			} else if(expression instanceof Expr.LambdaAccess) {
				checkLambdaAccess((Expr.LambdaAccess) expression, context);
			} else if(expression instanceof Declaration.Lambda) {
				checkLambdaDeclaration((Declaration.Lambda) expression, context);
			} else if(expression instanceof Expr.VariableAccess) {
				checkLocalVariable((Expr.VariableAccess) expression, context);
			} else if(expression instanceof Expr.New) {
				checkNew((Expr.New) expression, context);
			} else if(expression instanceof Expr.Quantifier) {
				checkQuantifier((Expr.Quantifier) expression, context);
			} else if(expression instanceof Expr.RecordInitialiser) {
				checkRecord((Expr.RecordInitialiser) expression, context);
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
		for(int i=0;i!=expression.size();++i) {
			checkExpression(expression.getOperand(i),context);
		}
	}

	private void checkArrayGenerator(Expr.ArrayGenerator expression, Context context) {
		checkExpression(expression.getValue(),context);
		checkExpression(expression.getLength(),context);
	}

	private void checkBinOp(Expr.Operator expression, Context context) {
		for(int i=0;i!=expression.size();++i) {
			checkExpression(expression.getOperand(i),context);
		}
	}

	private void checkCast(Expr.Cast expression, Context context) {
		checkExpression(expression.getCastedExpr(),context);
	}

	private void checkConstant(Expr.Constant expression, Context context) {

	}

	private void checkConstantAccess(Expr.StaticVariableAccess expression, Context context) {

	}

	private void checkFieldAccess(Expr.RecordAccess expression, Context context) {
		checkExpression(expression.getSource(),context);
	}

	private void checkInvoke(Expr.Invoke expression, Context context) {
		if (context != Context.METHOD && expression.getSignatureType() instanceof Type.Method) {
			invalidMethodCall(expression, context);
		}
		for (Expr p : expression.getArguments()) {
			checkExpression(p, context);
		}
	}

	private void checkIndexOf(Expr.ArrayAccess expression, Context context) {
		checkExpression(expression.getSource(),context);
		checkExpression(expression.getSubscript(),context);
	}

	private void checkIndirectFunctionOrMethodCall(Expr.IndirectInvoke expression, Context context) {
		checkExpression(expression.getSource(),context);
		for(Expr p : expression.getArguments()) {
			checkExpression(p,context);
		}
	}

	private void checkIs(Expr.Is expression, Context context) {
		checkExpression(expression.getTestExpr(),context);
	}

	private void checkLambdaAccess(Expr.LambdaAccess expression, Context context) {

	}

	private void checkLambdaDeclaration(Declaration.Lambda expression, Context context) {
		// Check body of the lambda
		checkExpression(expression.getBody(),context);
	}

	private void checkLocalVariable(Expr.VariableAccess expression, Context context) {

	}

	private void checkNew(Expr.New expression, Context context) {
		if(context != Context.METHOD) {
			invalidObjectAllocation(expression,context);
		}
		checkExpression(expression.getOperand(),context);
	}

	private void checkQuantifier(Expr.Quantifier expression, Context context) {
		for(Declaration.Variable p : expression.getParameters()) {
			checkExpression(p.getInitialiser(),context);
		}
		checkExpression(expression.getBody(),context);
	}

	private void checkRecord(Expr.RecordInitialiser expression, Context context) {
		for (int i = 0; i != expression.size(); ++i) {
			Pair<Identifier,Expr> e = expression.getOperand(i);
			checkExpression(e.getSecond(),context);
		}
	}

	private void invalidObjectAllocation(SyntacticItem expression, Context context) {
		String msg = errorMessage(ALLOCATION_NOT_PERMITTED);
		throw new SyntaxError(msg, file.getEntry(), expression);
	}

	private void invalidMethodCall(SyntacticItem expression, Context context) {
		String msg = errorMessage(METHODCALL_NOT_PERMITTED);
		throw new SyntaxError(msg, file.getEntry(), expression);
	}

	private void invalidReferenceAccess(SyntacticItem expression, Context context) {
		String msg= errorMessage(REFERENCE_ACCESS_NOT_PERMITTED);
		throw new SyntaxError(msg, file.getEntry(), expression);
	}
}
