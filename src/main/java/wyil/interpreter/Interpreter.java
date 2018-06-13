// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wyil.interpreter;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.*;

import wybs.lang.Build;
import wybs.lang.NameID;
import wybs.lang.NameResolver;
import wybs.lang.NameResolver.ResolutionError;
import wybs.lang.SyntacticElement;
import wyfs.lang.Path;
import wyc.util.WhileyFileResolver;

import static wyc.lang.WhileyFile.*;
import static wyil.interpreter.ConcreteSemantics.LValue;
import static wyil.interpreter.ConcreteSemantics.RValue;

import wyc.lang.WhileyFile;
import wyc.lang.WhileyFile.Decl;

/**
 * <p>
 * A simple interpreter for WyIL bytecodes. The purpose of this interpreter is
 * to provide a reference implementation for the semantics of WyIL bytecodes.
 * </p>
 * <p>
 * The interpreter is not intended to be time or space efficient. It also assume
 * the underlying WyIL bytecodes are well formed and does not attempt to check
 * them. Thus, malformed bytecodes can reuslt in the interpreter executing in an
 * unpredictable fashion.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class Interpreter {
	/**
	 * The build project provides access to compiled WyIL files.
	 */
	private final Build.Project project;

	/**
	 * Provides mechanism for resolving names.
	 */
	private final NameResolver resolver;

	/**
	 * Determines the underlying semantics used for this interpreter.
	 */
	private final ConcreteSemantics semantics;

	/**
	 * The debug stream provides an I/O stream through which debug bytecodes can
	 * write their messages.
	 */
	private final PrintStream debug;

	public Interpreter(Build.Project project, PrintStream debug) {
		this.project = project;
		this.debug = debug;
		this.resolver = new WhileyFileResolver(project);
		this.semantics = new ConcreteSemantics();
	}

	private enum Status {
		RETURN,
		BREAK,
		CONTINUE,
		NEXT
	}

	public NameResolver getNameResolver() {
		return resolver;
	}

	/**
	 * Execute a function or method identified by a name and type signature with
	 * the given arguments, producing a return value or null (if none). If the
	 * function or method cannot be found, or the number of arguments is
	 * incorrect then an exception is thrown.
	 *
	 * @param nid
	 *            The fully qualified identifier of the function or method
	 * @param sig
	 *            The exact type signature identifying the method.
	 * @param args
	 *            The supplied arguments
	 * @return
	 */
	public RValue[] execute(NameID nid, Type.Callable sig, CallStack frame, RValue... args) {
		// First, find the enclosing WyilFile
		try {
			// FIXME: NameID needs to be deprecated
			Identifier name = new Identifier(nid.name());
			// NOTE: need to read WyilFile here as, otherwose, it forces a
			// rereading of the Whiley source file and a loss of all generation
			// information.
			Path.Entry<WhileyFile> entry = project.get(nid.module(), WhileyFile.BinaryContentType);
			if (entry == null) {
				throw new IllegalArgumentException("no WyIL file found: " + nid.module());
			}
			// Second, find the given function or method
			WhileyFile wyilFile = entry.read();
			Decl.Callable fmp = wyilFile.getDeclaration(name, sig,
					Decl.Callable.class);
			if (fmp == null) {
				throw new IllegalArgumentException("no function or method found: " + nid + ", " + sig);
			} else if (sig.getParameters().size() != args.length) {
				throw new IllegalArgumentException("incorrect number of arguments: " + nid + ", " + sig);
			}
			// Fourth, construct the stack frame for execution
			frame = frame.enter(fmp);
			extractParameters(frame,args,fmp);
			// Check the precondition
			if(fmp instanceof Decl.FunctionOrMethod) {
				Decl.FunctionOrMethod fm = (Decl.FunctionOrMethod) fmp;
				checkInvariants(frame,fm.getRequires());
				// check function or method body exists
				if (fm.getBody() == null) {
					// FIXME: Add support for native functions or methods. That is,
					// allow native functions to be implemented and called from the
					// interpreter.
					throw new IllegalArgumentException("no function or method body found: " + nid + ", " + sig);
				}
				// Execute the method or function body
				executeBlock(fm.getBody(), frame, new FunctionOrMethodScope(fm));
				// Extra the return values
				RValue[] returns = packReturns(frame,fmp);
				// Restore original parameter values
				extractParameters(frame,args,fmp);
				// Check the postcondition holds
				checkInvariants(frame, fm.getEnsures());
				return returns;
			} else {
				// Properties always return true (provided their preconditions hold)
				return new RValue[]{RValue.True};
			}
			//
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private void extractParameters(CallStack frame, RValue[] args, Decl.Callable decl) {
		Tuple<Decl.Variable> parameters = decl.getParameters();
		for(int i=0;i!=parameters.size();++i) {
			Decl.Variable parameter = parameters.get(i);
			frame.putLocal(parameter.getName(), args[i]);
		}
	}

	/**
	 * Given an execution frame, extract the return values from a given function
	 * or method. The parameters of the function or method are located first in
	 * the frame, followed by the return values.
	 *
	 * @param frame
	 * @param type
	 * @return
	 */
	private RValue[] packReturns(CallStack frame, Decl.Callable decl) {
		if (decl instanceof Decl.Property) {
			return new RValue[] { RValue.True };
		} else {
			Tuple<Decl.Variable> returns = decl.getReturns();
			RValue[] values = new RValue[returns.size()];
			for (int i = 0; i != values.length; ++i) {
				values[i] = frame.getLocal(returns.get(i).getName());
			}
			return values;
		}
	}

	/**
	 * Execute a given block of statements starting from the beginning. Control
	 * may terminate prematurely in a number of situations. For example, when a
	 * return or break statement is encountered.
	 *
	 * @param block
	 *            --- Statement block to execute
	 * @param frame
	 *            --- The current stack frame
	 *
	 * @return
	 */
	private Status executeBlock(Stmt.Block block, CallStack frame, EnclosingScope scope) {
		for (int i = 0; i != block.size(); ++i) {
			Stmt stmt = block.get(i);
			Status r = executeStatement(stmt, frame, scope);
			// Now, see whether we are continuing or not
			if (r != Status.NEXT) {
				return r;
			}
		}
		return Status.NEXT;
	}

	/**
	 * Execute a statement at a given point in the function or method body
	 *
	 * @param stmt
	 *            --- The statement to be executed
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeStatement(Stmt stmt, CallStack frame, EnclosingScope scope) {
		try {
			switch (stmt.getOpcode()) {
			case WhileyFile.STMT_assert:
				return executeAssert((Stmt.Assert) stmt, frame, scope);
			case WhileyFile.STMT_assume:
				return executeAssume((Stmt.Assume) stmt, frame, scope);
			case WhileyFile.STMT_assign:
				return executeAssign((Stmt.Assign) stmt, frame, scope);
			case WhileyFile.STMT_break:
				return executeBreak((Stmt.Break) stmt, frame, scope);
			case WhileyFile.STMT_continue:
				return executeContinue((Stmt.Continue) stmt, frame, scope);
			case WhileyFile.STMT_debug:
				return executeDebug((Stmt.Debug) stmt, frame, scope);
			case WhileyFile.STMT_dowhile:
				return executeDoWhile((Stmt.DoWhile) stmt, frame, scope);
			case WhileyFile.STMT_fail:
				return executeFail((Stmt.Fail) stmt, frame, scope);
			case WhileyFile.STMT_if:
			case WhileyFile.STMT_ifelse:
				return executeIf((Stmt.IfElse) stmt, frame, scope);
			case WhileyFile.EXPR_indirectinvoke:
				executeIndirectInvoke((Expr.IndirectInvoke) stmt, frame);
				return Status.NEXT;
			case WhileyFile.EXPR_invoke:
				executeInvoke((Expr.Invoke) stmt, frame);
				return Status.NEXT;
			case WhileyFile.STMT_namedblock:
				return executeNamedBlock((Stmt.NamedBlock) stmt, frame, scope);
			case WhileyFile.STMT_while:
				return executeWhile((Stmt.While) stmt, frame, scope);
			case WhileyFile.STMT_return:
				return executeReturn((Stmt.Return) stmt, frame, scope);
			case WhileyFile.STMT_skip:
				return executeSkip((Stmt.Skip) stmt, frame, scope);
			case WhileyFile.STMT_switch:
				return executeSwitch((Stmt.Switch) stmt, frame, scope);
			case WhileyFile.DECL_variableinitialiser:
			case WhileyFile.DECL_variable:
				return executeVariableDeclaration((Decl.Variable) stmt, frame);
			}
		}
		catch (ResolutionError e) {
			error(e.getMessage(), stmt);
			return null;
		}

		deadCode(stmt);
		return null; // deadcode
	}

	private Status executeAssign(Stmt.Assign stmt, CallStack frame, EnclosingScope scope) {
		// FIXME: handle multi-assignments properly
		Tuple<WhileyFile.LVal> lhs = stmt.getLeftHandSide();
		RValue[] rhs = executeExpressions(stmt.getRightHandSide(), frame);
		for (int i = 0; i != lhs.size(); ++i) {
			LValue lval = constructLVal(lhs.get(i), frame);
			lval.write(frame, rhs[i]);
		}
		return Status.NEXT;
	}

	/**
	 * Execute an assert or assume statement. In both cases, if the condition
	 * evaluates to false an exception is thrown.
	 *
	 * @param stmt
	 *            --- Assert statement.
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeAssert(Stmt.Assert stmt, CallStack frame, EnclosingScope scope) {
		//
		checkInvariants(frame,stmt.getCondition());
		return Status.NEXT;
	}

	/**
	 * Execute an assert or assume statement. In both cases, if the condition
	 * evaluates to false an exception is thrown.
	 *
	 * @param stmt
	 *            --- Assert statement.
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeAssume(Stmt.Assume stmt, CallStack frame, EnclosingScope scope) {
		//
		checkInvariants(frame,stmt.getCondition());
		return Status.NEXT;
	}

	/**
	 * Execute a break statement. This transfers to control out of the nearest
	 * enclosing loop.
	 *
	 * @param stmt
	 *            --- Break statement.
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeBreak(Stmt.Break stmt, CallStack frame, EnclosingScope scope) {
		// TODO: the break bytecode supports a non-nearest exit and eventually
		// this should be supported.
		return Status.BREAK;
	}

	/**
	 * Execute a continue statement. This transfers to control back to the start
	 * the nearest enclosing loop.
	 *
	 * @param stmt
	 *            --- Break statement.
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeContinue(Stmt.Continue stmt, CallStack frame, EnclosingScope scope) {
		return Status.CONTINUE;
	}

	/**
	 * Execute a Debug statement at a given point in the function or method
	 * body. This will write the provided string out to the debug stream.
	 *
	 * @param stmt
	 *            --- Debug statement to executed
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeDebug(Stmt.Debug stmt, CallStack frame, EnclosingScope scope) {
		//
		// FIXME: need to do something with this
		RValue.Array arr = executeExpression(ARRAY_T, stmt.getOperand(), frame);
		for (RValue item : arr.getElements()) {
			RValue.Int i = (RValue.Int) item;
			char c = (char) i.intValue();
			debug.print(c);
		}
		//
		return Status.NEXT;
	}

	/**
	 * Execute a DoWhile statement at a given point in the function or method
	 * body. This will loop over the body zero or more times.
	 *
	 * @param stmt
	 *            --- Loop statement to executed
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeDoWhile(Stmt.DoWhile stmt, CallStack frame, EnclosingScope scope) {
		Status r = Status.NEXT;
		while (r == Status.NEXT || r == Status.CONTINUE) {
			r = executeBlock(stmt.getBody(), frame, scope);
			if (r == Status.NEXT) {
				RValue.Bool operand = executeExpression(BOOL_T, stmt.getCondition(), frame);
				if (operand == RValue.False) {
					return Status.NEXT;
				}
			}
		}

		// If we get here, then we have exited the loop body without falling
		// through to the next bytecode.
		if (r == Status.BREAK) {
			return Status.NEXT;
		} else {
			return r;
		}
	}

	/**
	 * Execute a fail statement at a given point in the function or method body.
	 * This will generate a runtime fault.
	 *
	 * @param stmt
	 *            --- The fail statement to execute
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeFail(Stmt.Fail stmt, CallStack frame, EnclosingScope scope) {
		throw new AssertionError("Runtime fault occurred");
	}

	/**
	 * Execute an if statement at a given point in the function or method body.
	 * This will proceed done either the true or false branch.
	 *
	 * @param stmt
	 *            --- The if statement to execute
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeIf(Stmt.IfElse stmt, CallStack frame, EnclosingScope scope) {
		RValue.Bool operand = executeExpression(BOOL_T, stmt.getCondition(), frame);
		if (operand == RValue.True) {
			// branch taken, so execute true branch
			return executeBlock(stmt.getTrueBranch(), frame, scope);
		} else if (stmt.hasFalseBranch()) {
			// branch not taken, so execute false branch
			return executeBlock(stmt.getFalseBranch(), frame, scope);
		} else {
			return Status.NEXT;
		}
	}

	/**
	 * Execute a named block which is simply a block of statements.
	 *
	 * @param stmt
	 *            --- Block statement to executed
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeNamedBlock(Stmt.NamedBlock stmt, CallStack frame, EnclosingScope scope) {
		return executeBlock(stmt.getBlock(),frame,scope);
	}

	/**
	 * Execute a While statement at a given point in the function or method
	 * body. This will loop over the body zero or more times.
	 *
	 * @param stmt
	 *            --- Loop statement to executed
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeWhile(Stmt.While stmt, CallStack frame, EnclosingScope scope) {
		Status r;
		do {
			RValue.Bool operand = executeExpression(BOOL_T, stmt.getCondition(), frame);
			if (operand == RValue.False) {
				return Status.NEXT;
			}
			// Keep executing the loop body until we exit it somehow.
			r = executeBlock(stmt.getBody(), frame, scope);
		} while (r == Status.NEXT || r == Status.CONTINUE);
		// If we get here, then we have exited the loop body without falling
		// through to the next bytecode.
		if (r == Status.BREAK) {
			return Status.NEXT;
		} else {
			return r;
		}
	}

	/**
	 * Execute a Return statement at a given point in the function or method
	 * body
	 *
	 * @param stmt
	 *            --- The return statement to execute
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeReturn(Stmt.Return stmt, CallStack frame, EnclosingScope scope) {
		// We know that a return statement can only appear in either a function
		// or method declaration. It cannot appear, for example, in a type
		// declaration. Therefore, the enclosing declaration is a function or
		// method.
		Decl.Callable context = scope.getEnclosingScope(FunctionOrMethodScope.class).getContext();
		Tuple<Decl.Variable> returns = context.getReturns();
		RValue[] values = executeExpressions(stmt.getReturns(), frame);
		for (int i = 0; i != returns.size(); ++i) {
			frame.putLocal(returns.get(i).getName(), values[i]);
		}
		return Status.RETURN;
	}

	/**
	 * Execute a skip statement at a given point in the function or method
	 * body
	 *
	 * @param stmt
	 *            --- The skip statement to execute
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeSkip(Stmt.Skip stmt, CallStack frame, EnclosingScope scope) {
		// skip !
		return Status.NEXT;
	}

	/**
	 * Execute a Switch statement at a given point in the function or method
	 * body
	 *
	 * @param stmt
	 *            --- The swithc statement to execute
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeSwitch(Stmt.Switch stmt, CallStack frame, EnclosingScope scope) {
		Tuple<Stmt.Case> cases = stmt.getCases();
		//
		Object value = executeExpression(ANY_T, stmt.getCondition(), frame);
		for (int i = 0; i != cases.size(); ++i) {
			Stmt.Case c = cases.get(i);
			Stmt.Block body = c.getBlock();
			if (c.isDefault()) {
				return executeBlock(body, frame, scope);
			} else {
				// FIXME: this is a temporary hack until a proper notion of
				// ConstantExpr is introduced.
				RValue[] values = executeExpressions(c.getConditions(), frame);
				for (RValue v : values) {
					if (v.equals(value)) {
						return executeBlock(body, frame, scope);
					}
				}
			}
		}
		return Status.NEXT;
	}

	/**
	 * Execute a variable declaration statement at a given point in the function or method
	 * body
	 *
	 * @param stmt
	 *            --- The statement to execute
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeVariableDeclaration(Decl.Variable stmt, CallStack frame) {
		// We only need to do something if this has an initialiser
		if(stmt.hasInitialiser()) {
			RValue value = executeExpression(ANY_T, stmt.getInitialiser(), frame);
			frame.putLocal(stmt.getName(),value);
		}
		return Status.NEXT;
	}

	// =============================================================
	// Single expressions
	// =============================================================

	/**
	 * Execute a single expression which is expected to return a single result
	 * of an expected type. If a result of an incorrect type is returned, then
	 * an exception is raised.
	 *
	 * @param expected
	 *            The expected type of the result
	 * @param expr
	 *            The expression to be executed
	 * @param frame
	 *            The frame in which the expression is executing
	 * @return
	 */
	public <T extends RValue> T executeExpression(Class<T> expected, Expr expr, CallStack frame) {
		try {
			RValue val;
			switch (expr.getOpcode()) {
			case WhileyFile.EXPR_constant:
				val = executeConst((Expr.Constant) expr, frame);
				break;
			case WhileyFile.EXPR_cast:
				val = executeConvert((Expr.Cast) expr, frame);
				break;
			case WhileyFile.EXPR_recordinitialiser:
				val = executeRecordInitialiser((Expr.RecordInitialiser) expr, frame);
				break;
			case WhileyFile.EXPR_recordaccess:
			case WhileyFile.EXPR_recordborrow:
				val = executeRecordAccess((Expr.RecordAccess) expr, frame);
				break;
			case WhileyFile.EXPR_indirectinvoke:
				val = executeIndirectInvoke((Expr.IndirectInvoke) expr, frame)[0];
				break;
			case WhileyFile.EXPR_invoke:
				val = executeInvoke((Expr.Invoke) expr, frame)[0];
				break;
			case WhileyFile.EXPR_variablemove:
			case WhileyFile.EXPR_variablecopy:
				val = executeVariableAccess((Expr.VariableAccess) expr, frame);
				break;
			case WhileyFile.EXPR_staticvariable:
				val = executeStaticVariableAccess((Expr.StaticVariableAccess) expr, frame);
				break;
			case WhileyFile.EXPR_is:
				val = executeIs((Expr.Is) expr, frame);
				break;
			case WhileyFile.EXPR_logicalnot:
				val = executeLogicalNot((Expr.LogicalNot) expr, frame);
				break;
			case WhileyFile.EXPR_logicaland:
				val = executeLogicalAnd((Expr.LogicalAnd) expr, frame);
				break;
			case WhileyFile.EXPR_logicalor:
				val = executeLogicalOr((Expr.LogicalOr) expr, frame);
				break;
			case WhileyFile.EXPR_logiaclimplication:
				val = executeLogicalImplication((Expr.LogicalImplication) expr, frame);
				break;
			case WhileyFile.EXPR_logicaliff:
				val = executeLogicalIff((Expr.LogicalIff) expr, frame);
				break;
			case WhileyFile.EXPR_logicalexistential:
			case WhileyFile.EXPR_logicaluniversal:
				val = executeQuantifier((Expr.Quantifier) expr, frame);
				break;
			case WhileyFile.EXPR_equal:
				val = executeEqual((Expr.Equal) expr, frame);
				break;
			case WhileyFile.EXPR_notequal:
				val = executeNotEqual((Expr.NotEqual) expr, frame);
				break;
			case WhileyFile.EXPR_integernegation:
				val = executeIntegerNegation((Expr.IntegerNegation) expr, frame);
				break;
			case WhileyFile.EXPR_integeraddition:
				val = executeIntegerAddition((Expr.IntegerAddition) expr, frame);
				break;
			case WhileyFile.EXPR_integersubtraction:
				val = executeIntegerSubtraction((Expr.IntegerSubtraction) expr, frame);
				break;
			case WhileyFile.EXPR_integermultiplication:
				val = executeIntegerMultiplication((Expr.IntegerMultiplication) expr, frame);
				break;
			case WhileyFile.EXPR_integerdivision:
				val = executeIntegerDivision((Expr.IntegerDivision) expr, frame);
				break;
			case WhileyFile.EXPR_integerremainder:
				val = executeIntegerRemainder((Expr.IntegerRemainder) expr, frame);
				break;
			case WhileyFile.EXPR_integerlessthan:
				val = executeIntegerLessThan((Expr.IntegerLessThan) expr, frame);
				break;
			case WhileyFile.EXPR_integerlessequal:
				val = executeIntegerLessThanOrEqual((Expr.IntegerLessThanOrEqual) expr, frame);
				break;
			case WhileyFile.EXPR_integergreaterthan:
				val = executeIntegerGreaterThan((Expr.IntegerGreaterThan) expr, frame);
				break;
			case WhileyFile.EXPR_integergreaterequal:
				val = executeIntegerGreaterThanOrEqual((Expr.IntegerGreaterThanOrEqual) expr, frame);
				break;
			case WhileyFile.EXPR_bitwisenot:
				val = executeBitwiseNot((Expr.BitwiseComplement) expr, frame);
				break;
			case WhileyFile.EXPR_bitwiseor:
				val = executeBitwiseOr((Expr.BitwiseOr) expr, frame);
				break;
			case WhileyFile.EXPR_bitwisexor:
				val = executeBitwiseXor((Expr.BitwiseXor) expr, frame);
				break;
			case WhileyFile.EXPR_bitwiseand:
				val = executeBitwiseAnd((Expr.BitwiseAnd) expr, frame);
				break;
			case WhileyFile.EXPR_bitwiseshl:
				val = executeBitwiseShiftLeft((Expr.BitwiseShiftLeft) expr, frame);
				break;
			case WhileyFile.EXPR_bitwiseshr:
				val = executeBitwiseShiftRight((Expr.BitwiseShiftRight) expr, frame);
				break;
			case WhileyFile.EXPR_arrayborrow:
			case WhileyFile.EXPR_arrayaccess:
				val = executeArrayAccess((Expr.ArrayAccess) expr, frame);
				break;
			case WhileyFile.EXPR_arraygenerator:
				val = executeArrayGenerator((Expr.ArrayGenerator) expr, frame);
				break;
			case WhileyFile.EXPR_arraylength:
				val = executeArrayLength((Expr.ArrayLength) expr, frame);
				break;
			case WhileyFile.EXPR_arrayinitialiser:
				val = executeArrayInitialiser((Expr.ArrayInitialiser) expr, frame);
				break;
			case WhileyFile.EXPR_arrayrange:
				val = executeArrayRange((Expr.ArrayRange) expr, frame);
				break;
			case WhileyFile.EXPR_new:
				val = executeNew((Expr.New) expr, frame);
				break;
			case WhileyFile.EXPR_dereference:
				val = executeDereference((Expr.Dereference) expr, frame);
				break;
			case WhileyFile.EXPR_lambdaaccess:
				val = executeLambdaAccess((Expr.LambdaAccess) expr, frame);
				break;
			case WhileyFile.DECL_lambda:
				val = executeLambdaDeclaration((Decl.Lambda) expr, frame);
				break;
			default:
				return deadCode(expr);
			}
			return checkType(val, expr, expected);
		} catch (ResolutionError e) {
			error(e.getMessage(), expr);
			return null;
		}
	}


	/**
	 * Execute a Constant expression at a given point in the function or
	 * method body
	 *
	 * @param expr
	 *            --- The expression to execute
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private RValue executeConst(Expr.Constant expr, CallStack frame) {
		Value v = expr.getValue();
		switch (v.getOpcode()) {
		case ITEM_null:
			return RValue.Null;
		case ITEM_bool: {
			Value.Bool b = (Value.Bool) v;
			if (b.get()) {
				return RValue.True;
			} else {
				return RValue.False;
			}
		}
		case ITEM_byte: {
			Value.Byte b = (Value.Byte) v;
			return semantics.Byte(b.get());
		}
		case ITEM_int: {
			Value.Int i = (Value.Int) v;
			return semantics.Int(i.get());
		}
		case ITEM_utf8: {
			Value.UTF8 s = (Value.UTF8) v;
			byte[] bytes = s.get();
			RValue[] elements = new RValue[bytes.length];
			for (int i = 0; i != elements.length; ++i) {
				// FIXME: something tells me this is wrong for signed byte
				// values?
				elements[i] = semantics.Int(BigInteger.valueOf(bytes[i]));
			}
			return semantics.Array(elements);
		}
		default:
			throw new RuntimeException("unknown value encountered (" + expr + ")");
		}
	}

	/**
	 * Execute a type conversion at a given point in the function or method body
	 *
	 * @param expr
	 *            --- The expression to execute
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private RValue executeConvert(Expr.Cast expr, CallStack frame) {
		RValue operand = executeExpression(ANY_T, expr.getOperand(), frame);
		return operand.convert(expr.getType());
	}

	private RValue executeRecordAccess(Expr.RecordAccess expr, CallStack frame) {
		RValue.Record rec = executeExpression(RECORD_T, expr.getOperand(), frame);
		return rec.read(expr.getField());
	}

	private RValue executeRecordInitialiser(Expr.RecordInitialiser expr, CallStack frame) {
		Tuple<Identifier> fields = expr.getFields();
		Tuple<Expr> operands = expr.getOperands();
		RValue.Field[] values = new RValue.Field[operands.size()];
		for (int i = 0; i != operands.size(); ++i) {
			Identifier field = fields.get(i);
			Expr operand = operands.get(i);
			RValue value = executeExpression(ANY_T, operand, frame);
			values[i] = semantics.Field(field, value);
		}
		return semantics.Record(values);
	}

	private RValue executeQuantifier(Expr.Quantifier expr, CallStack frame) {
		boolean r = executeQuantifier(0, expr, frame);
		boolean q = (expr instanceof Expr.UniversalQuantifier);
		return r == q ? RValue.True : RValue.False;
	}

	/**
	 * Execute one range of the quantifier, or the body if no ranges remain.
	 *
	 * @param index
	 * @param expr
	 * @param frame
	 * @param context
	 * @return
	 */
	private boolean executeQuantifier(int index, Expr.Quantifier expr, CallStack frame) {
		Tuple<Decl.Variable> vars = expr.getParameters();
		if (index == vars.size()) {
			// This is the base case where we evaluate the condition itself.
			RValue.Bool r = executeExpression(BOOL_T, expr.getOperand(), frame);
			boolean q = (expr instanceof Expr.UniversalQuantifier);
			// If this evaluates to true, then we will continue executing the
			// quantifier.
			return r.boolValue() == q;
		} else {
			Decl.Variable var = vars.get(index);
			RValue.Array range = executeExpression(ARRAY_T, var.getInitialiser(), frame);
			RValue[] elements = range.getElements();
			for (int i = 0; i != elements.length; ++i) {
				frame.putLocal(var.getName(), elements[i]);
				boolean r = executeQuantifier(index + 1, expr, frame);
				if (!r) {
					// early termination
					return r;
				}
			}
			return true;
		}
	}

	/**
	 * Execute a variable access expression at a given point in the function or
	 * method body. This simply loads the value of the given variable from the
	 * frame.
	 *
	 * @param expr
	 *            --- The expression to execute
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private RValue executeVariableAccess(Expr.VariableAccess expr, CallStack frame) {
		Decl.Variable decl = expr.getVariableDeclaration();
		return frame.getLocal(decl.getName());
	}

	private RValue executeStaticVariableAccess(Expr.StaticVariableAccess expr, CallStack frame) throws ResolutionError {
		Decl.StaticVariable decl = resolver.resolveExactly(expr.getName(), Decl.StaticVariable.class);
		NameID nid = decl.getQualifiedName().toNameID();
		return frame.getStatic(nid);
	}

	private RValue executeIs(Expr.Is expr, CallStack frame) throws ResolutionError {
		RValue lhs = executeExpression(ANY_T, expr.getOperand(), frame);
		return lhs.is(expr.getTestType(), this);
	}

	public RValue executeIntegerNegation(Expr.IntegerNegation expr, CallStack frame) {
		RValue.Int lhs = executeExpression(INT_T, expr.getOperand(), frame);
		return lhs.negate();
	}

	public RValue executeIntegerAddition(Expr.IntegerAddition expr, CallStack frame) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame);
		return lhs.add(rhs);
	}

	public RValue executeIntegerSubtraction(Expr.IntegerSubtraction expr, CallStack frame) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame);
		return lhs.subtract(rhs);
	}

	public RValue executeIntegerMultiplication(Expr.IntegerMultiplication expr, CallStack frame) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame);
		return lhs.multiply(rhs);
	}

	public RValue executeIntegerDivision(Expr.IntegerDivision expr, CallStack frame) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame);
		return lhs.divide(rhs);
	}

	public RValue executeIntegerRemainder(Expr.IntegerRemainder expr, CallStack frame) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame);
		return lhs.remainder(rhs);
	}

	public RValue executeEqual(Expr.Equal expr, CallStack frame) {
		RValue lhs = executeExpression(ANY_T, expr.getFirstOperand(), frame);
		RValue rhs = executeExpression(ANY_T, expr.getSecondOperand(), frame);
		return lhs.equal(rhs);
	}

	public RValue executeNotEqual(Expr.NotEqual expr, CallStack frame) {
		RValue lhs = executeExpression(ANY_T, expr.getFirstOperand(), frame);
		RValue rhs = executeExpression(ANY_T, expr.getSecondOperand(), frame);
		return lhs.notEqual(rhs);
	}

	public RValue executeIntegerLessThan(Expr.IntegerLessThan expr, CallStack frame) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame);
		return lhs.lessThan(rhs);
	}

	public RValue executeIntegerLessThanOrEqual(Expr.IntegerLessThanOrEqual expr, CallStack frame) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame);
		return lhs.lessThanOrEqual(rhs);
	}

	public RValue executeIntegerGreaterThan(Expr.IntegerGreaterThan expr, CallStack frame) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame);
		return rhs.lessThan(lhs);
	}

	public RValue executeIntegerGreaterThanOrEqual(Expr.IntegerGreaterThanOrEqual expr, CallStack frame) {
		RValue.Int lhs = executeExpression(INT_T, expr.getFirstOperand(), frame);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame);
		return rhs.lessThanOrEqual(lhs);
	}

	public RValue executeLogicalNot(Expr.LogicalNot expr, CallStack frame) {
		RValue.Bool lhs = executeExpression(BOOL_T, expr.getOperand(), frame);
		return lhs.not();
	}

	public RValue executeLogicalAnd(Expr.LogicalAnd expr, CallStack frame) {
		// This is a short-circuiting operator. Therefore, we fail as soon as one
		// argument fails.
		Tuple<Expr> operands = expr.getOperands();
		for(int i=0;i!=operands.size();++i) {
			RValue.Bool b = executeExpression(BOOL_T, operands.get(i), frame);
			if(b == RValue.False) {
				return b;
			}
		}
		return RValue.True;
	}

	public RValue executeLogicalOr(Expr.LogicalOr expr, CallStack frame) {
		// This is a short-circuiting operator. Therefore, we succeed as soon as one
		// argument succeeds.
		Tuple<Expr> operands = expr.getOperands();
		for(int i=0;i!=operands.size();++i) {
			RValue.Bool b = executeExpression(BOOL_T, operands.get(i), frame);
			if(b == RValue.True) {
				return b;
			}
		}
		return RValue.False;
	}

	public RValue executeLogicalImplication(Expr.LogicalImplication expr, CallStack frame) {
		RValue.Bool lhs = executeExpression(BOOL_T, expr.getFirstOperand(), frame);
		if(lhs == RValue.False) {
			return RValue.True;
		} else {
			RValue.Bool rhs = executeExpression(BOOL_T, expr.getSecondOperand(), frame);
			return lhs.equal(rhs);
		}
	}

	public RValue executeLogicalIff(Expr.LogicalIff expr, CallStack frame) {
		RValue.Bool lhs = executeExpression(BOOL_T, expr.getFirstOperand(), frame);
		RValue.Bool rhs = executeExpression(BOOL_T, expr.getSecondOperand(), frame);
		return lhs.equal(rhs);
	}

	public RValue executeBitwiseNot(Expr.BitwiseComplement expr, CallStack frame) {
		RValue.Byte lhs = executeExpression(BYTE_T, expr.getOperand(), frame);
		return lhs.invert();
	}

	public RValue executeBitwiseAnd(Expr.BitwiseAnd expr, CallStack frame) {
		Tuple<Expr> operands = expr.getOperands();
		RValue.Byte val = executeExpression(BYTE_T, operands.get(0), frame);
		for (int i = 1; i != operands.size(); ++i) {
			val = val.and(executeExpression(BYTE_T, operands.get(i), frame));
		}
		return val;
	}

	public RValue executeBitwiseOr(Expr.BitwiseOr expr, CallStack frame) {
		Tuple<Expr> operands = expr.getOperands();
		RValue.Byte val = executeExpression(BYTE_T, operands.get(0), frame);
		for (int i = 1; i != operands.size(); ++i) {
			val = val.or(executeExpression(BYTE_T, operands.get(i), frame));
		}
		return val;
	}

	public RValue executeBitwiseXor(Expr.BitwiseXor expr, CallStack frame) {
		Tuple<Expr> operands = expr.getOperands();
		RValue.Byte val = executeExpression(BYTE_T, operands.get(0), frame);
		for (int i = 1; i != operands.size(); ++i) {
			val = val.xor(executeExpression(BYTE_T, operands.get(i), frame));
		}
		return val;
	}

	public RValue executeBitwiseShiftLeft(Expr.BitwiseShiftLeft expr, CallStack frame) {
		RValue.Byte lhs = executeExpression(BYTE_T, expr.getFirstOperand(), frame);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame);
		return lhs.shl(rhs);
	}
	public RValue executeBitwiseShiftRight(Expr.BitwiseShiftRight expr, CallStack frame) {
		RValue.Byte lhs = executeExpression(BYTE_T, expr.getFirstOperand(), frame);
		RValue.Int rhs = executeExpression(INT_T, expr.getSecondOperand(), frame);
		return lhs.shr(rhs);
	}

	public RValue executeArrayLength(Expr.ArrayLength expr, CallStack frame) {
		RValue.Array array = executeExpression(ARRAY_T, expr.getOperand(), frame);
		return array.length();
	}

	public RValue executeArrayAccess(Expr.ArrayAccess expr, CallStack frame) {
		RValue.Array array = executeExpression(ARRAY_T, expr.getFirstOperand(), frame);
		RValue.Int index = executeExpression(INT_T, expr.getSecondOperand(), frame);
		return array.read(index);
	}

	public RValue executeArrayGenerator(Expr.ArrayGenerator expr, CallStack frame) {
		RValue element = executeExpression(ANY_T, expr.getFirstOperand(), frame);
		RValue.Int count = executeExpression(INT_T, expr.getSecondOperand(), frame);
		int n = count.intValue();
		if (n < 0) {
			throw new AssertionError("negative array length");
		}
		RValue[] values = new RValue[n];
		for (int i = 0; i != n; ++i) {
			values[i] = element;
		}
		return semantics.Array(values);
	}

	public RValue executeArrayInitialiser(Expr.ArrayInitialiser expr, CallStack frame) {
		Tuple<Expr> operands = expr.getOperands();
		RValue[] elements = new RValue[operands.size()];
		for (int i = 0; i != elements.length; ++i) {
			elements[i] = executeExpression(ANY_T, operands.get(i), frame);
		}
		return semantics.Array(elements);
	}

	public RValue executeArrayRange(Expr.ArrayRange expr, CallStack frame) {
		int start = executeExpression(INT_T, expr.getFirstOperand(), frame).intValue();
		int end = executeExpression(INT_T, expr.getSecondOperand(), frame).intValue();
		RValue[] elements = new RValue[end - start];
		for (int i = start; i < end; ++i) {
			elements[i - start] = semantics.Int(BigInteger.valueOf(i));
		}
		return semantics.Array(elements);
	}

	public RValue executeNew(Expr.New expr, CallStack frame) {
		RValue initialiser = executeExpression(ANY_T, expr.getOperand(), frame);
		RValue.Cell cell = semantics.Cell(initialiser);
		return semantics.Reference(cell);
	}

	public RValue executeDereference(Expr.Dereference expr, CallStack frame) {
		RValue.Reference ref = executeExpression(REF_T, expr.getOperand(), frame);
		return ref.deref().read();
	}

	public RValue executeLambdaAccess(Expr.LambdaAccess expr, CallStack frame) throws ResolutionError {
		// Locate the function or method body in order to execute it
		// FIXME: This is horrendous. Should be able to use descriptor here!!
		Decl.FunctionOrMethod decl = resolveExactly(expr.getName(), expr.getSignature(),
				Decl.FunctionOrMethod.class);
		// Clone frame to ensure it executes in this exact environment.
		return semantics.Lambda(decl, frame.clone(), decl.getBody());
	}

	private RValue executeLambdaDeclaration(Decl.Lambda decl, CallStack frame) {
		// FIXME: this needs a clone of the frame? Otherwise, it's just
		// executing in the later environment.
		return semantics.Lambda(decl, frame.clone(), decl.getBody());
	}

	// =============================================================
	// Multiple expressions
	// =============================================================

	/**
	 * Execute one or more expressions. This is slightly more complex than for
	 * the single expression case because of the potential to encounter
	 * "positional operands". That is, severals which arise from executing the
	 * same expression.
	 *
	 * @param expressions
	 * @param frame
	 * @return
	 */
	private RValue[] executeExpressions(Tuple<Expr> expressions, CallStack frame) {
		RValue[][] results = new RValue[expressions.size()][];
		int count = 0;
		for(int i=0;i!=expressions.size();++i) {
			results[i] = executeMultiReturnExpression(expressions.get(i),frame);
			count += results[i].length;
		}
		RValue[] rs = new RValue[count];
		int j = 0;
		for(int i=0;i!=expressions.size();++i) {
			Object[] r = results[i];
			System.arraycopy(r, 0, rs, j, r.length);
			j += r.length;
		}
		return rs;
	}

	/**
	 * Execute an expression which has the potential to return more than one
	 * result. Thus the return type must accommodate this by allowing zero or
	 * more returned values.
	 *
	 * @param expr
	 * @param frame
	 * @return
	 */
	private RValue[] executeMultiReturnExpression(Expr expr, CallStack frame) {
		try {
			switch (expr.getOpcode()) {
			case WhileyFile.EXPR_indirectinvoke:
				return executeIndirectInvoke((Expr.IndirectInvoke) expr, frame);
			case WhileyFile.EXPR_invoke:
				return executeInvoke((Expr.Invoke) expr, frame);
			case WhileyFile.EXPR_constant:
			case WhileyFile.EXPR_cast:
			case WhileyFile.EXPR_recordaccess:
			case WhileyFile.EXPR_recordborrow:
			case WhileyFile.DECL_lambda:
			case WhileyFile.EXPR_logicalexistential:
			case WhileyFile.EXPR_logicaluniversal:
			default:
				RValue val = executeExpression(ANY_T, expr, frame);
				return new RValue[] { val };
			}
		} catch (ResolutionError e) {
			error(e.getMessage(), expr);
			return null;
		}
	}

	/**
	 * Execute an IndirectInvoke bytecode instruction at a given point in the
	 * function or method body. This first checks the operand is a function
	 * reference, and then generates a recursive call to execute the given
	 * function. If the function does not exist, or is provided with the wrong
	 * number of arguments, then a runtime fault will occur.
	 *
	 * @param expr
	 *            --- The expression to execute
	 * @param frame
	 *            --- The current stack frame
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private RValue[] executeIndirectInvoke(Expr.IndirectInvoke expr, CallStack frame) {
		RValue.Lambda src = executeExpression(LAMBDA_T, expr.getSource(),frame);
		RValue[] arguments = executeExpressions(expr.getArguments(), frame);
		// Here we have to use the enclosing frame when the lambda was created.
		// The reason for this is that the lambda may try to access enclosing
		// variables in the scope it was created.
		frame = src.getFrame();
		extractParameters(frame,arguments,src.getContext());
		// Execute the method or function body
		Stmt body = src.getBody();
		if(body instanceof Stmt.Block) {
			executeBlock((Stmt.Block) body, frame, new FunctionOrMethodScope(src.getContext()));
			// Extra the return values
			return packReturns(frame,src.getContext());
		} else {
			RValue retval = executeExpression(ANY_T,(Expr) body, frame);
			return new RValue[]{retval};
		}
	}

	/**
	 * Execute an Invoke bytecode instruction at a given point in the function
	 * or method body. This generates a recursive call to execute the given
	 * function. If the function does not exist, or is provided with the wrong
	 * number of arguments, then a runtime fault will occur.
	 *
	 * @param expr
	 *            --- The expression to execute
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 * @throws ResolutionError
	 */
	private RValue[] executeInvoke(Expr.Invoke expr, CallStack frame) throws ResolutionError {
		// Resolve function or method being invoked to a concrete declaration
		Decl.Callable decl = resolveExactly(expr.getName(), expr.getSignature(),
				Decl.Callable.class);
		// Evaluate argument expressions
		RValue[] arguments = executeExpressions(expr.getOperands(), frame);
		// Invoke the function or method in question
		return execute(decl.getQualifiedName().toNameID(), decl.getType(), frame, arguments);
	}

	// =============================================================
	// Constants
	// =============================================================

	/**
	 * This method constructs a "mutable" representation of the lval. This is a
	 * bit strange, but is necessary because values in the frame are currently
	 * immutable.
	 *
	 * @param operand
	 * @param frame
	 * @param context
	 * @return
	 */
	private LValue constructLVal(Expr expr, CallStack frame) {
		switch (expr.getOpcode()) {
		case EXPR_arrayborrow:
		case EXPR_arrayaccess: {
			Expr.ArrayAccess e = (Expr.ArrayAccess) expr;
			LValue src = constructLVal(e.getFirstOperand(), frame);
			RValue.Int index = executeExpression(INT_T, e.getSecondOperand(), frame);
			return new LValue.Array(src, index);
		}
		case EXPR_dereference: {
			Expr.Dereference e = (Expr.Dereference) expr;
			LValue src = constructLVal(e.getOperand(), frame);
			return new LValue.Dereference(src);
		}
		case EXPR_recordaccess:
		case EXPR_recordborrow: {
			Expr.RecordAccess e = (Expr.RecordAccess) expr;
			LValue src = constructLVal(e.getOperand(), frame);
			return new LValue.Record(src, e.getField());
		}
		case EXPR_variablemove:
		case EXPR_variablecopy: {
			Expr.VariableAccess e = (Expr.VariableAccess) expr;
			Decl.Variable decl = e.getVariableDeclaration();
			return new LValue.Variable(decl.getName());
		}
		}
		deadCode(expr);
		return null; // deadcode
	}

	/**
	 * Evaluate zero or more conditional expressions, and check whether any is
	 * false. If so, raise an exception indicating a runtime fault.
	 *
	 * @param frame
	 * @param context
	 * @param invariants
	 */
	public void checkInvariants(CallStack frame, Tuple<Expr> invariants) {
		for (int i = 0; i != invariants.size(); ++i) {
			RValue.Bool b = executeExpression(BOOL_T, invariants.get(i), frame);
			if (b == RValue.False) {
				// FIXME: need to do more here
				throw new AssertionError();
			}
		}
	}

	/**
	 * Evaluate zero or more conditional expressions, and check whether any is
	 * false. If so, raise an exception indicating a runtime fault.
	 *
	 * @param frame
	 * @param context
	 * @param invariants
	 */
	public void checkInvariants(CallStack frame, Expr... invariants) {
		for (int i = 0; i != invariants.length; ++i) {
			RValue.Bool b = executeExpression(BOOL_T, invariants[i], frame);
			if (b == RValue.False) {
				// FIXME: need to do more here
				throw new AssertionError();
			}
		}
	}

	/**
	 * Check that a given operand value matches an expected type.
	 *
	 * @param operand
	 *            --- bytecode operand to be checked
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @param types
	 *            --- Types to be checked against
	 */
	@SafeVarargs
	public static <T extends RValue> T checkType(RValue operand, SyntacticElement context, Class<T>... types) {
		// Got through each type in turn checking for a match
		for (int i = 0; i != types.length; ++i) {
			if (types[i].isInstance(operand)) {
				// Matched!
				return (T) operand;
			}
		}
		// No match, therefore through an error
		if (operand == null) {
			error("null operand", context);
		} else {
			error("operand returned " + operand.getClass().getName() + ", expecting one of " + Arrays.toString(types),
					context);
		}
		return null;
	}

	/**
	 * This method is provided as a generic mechanism for reporting runtime
	 * errors within the interpreter.
	 *
	 * @param msg
	 *            --- Message to be printed when error arises.
	 * @param context
	 *            --- Context in which bytecodes are executed
	 */
	public static Object error(String msg, SyntacticElement context) {
		// FIXME: do more here
		throw new RuntimeException(msg);
	}

	/**
	 * This method is provided to properly handled positions which should be
	 * dead code.
	 *
	 * @param context
	 *            --- Context in which bytecodes are executed
	 */
	private <T> T deadCode(SyntacticElement element) {
		// FIXME: do more here
		throw new RuntimeException("internal failure --- dead code reached");
	}

	private static final Class<RValue> ANY_T = RValue.class;
	private static final Class<RValue.Bool> BOOL_T = RValue.Bool.class;
	private static final Class<RValue.Byte> BYTE_T = RValue.Byte.class;
	private static final Class<RValue.Int> INT_T = RValue.Int.class;
	private static final Class<RValue.Reference> REF_T = RValue.Reference.class;
	private static final Class<RValue.Array> ARRAY_T = RValue.Array.class;
	private static final Class<RValue.Record> RECORD_T = RValue.Record.class;
	private static final Class<RValue.Lambda> LAMBDA_T = RValue.Lambda.class;

	public final class CallStack {
		private final Set<Path.ID> modules;
		private final Decl.Callable context;
		private final Map<Identifier, RValue> locals;
		private final Map<NameID, RValue> globals;

		public CallStack() {
			this.locals = new HashMap<>();
			this.globals = new HashMap<>();
			this.modules = new HashSet<>();
			this.context = null;
		}

		private CallStack(CallStack parent, Decl.Callable context) {
			this.context = context;
			this.locals = new HashMap<>();
			this.globals = parent.globals;
			this.modules = parent.modules;
		}

		public RValue getLocal(Identifier name) {
			return locals.get(name);
		}

		public void putLocal(Identifier name, RValue value) {
			locals.put(name, value);
		}

		public RValue getStatic(NameID name) {
			RValue v = globals.get(name);
			if(v == null) {
				load(name.module());
				v  = globals.get(name);
			}
			return v;
		}

		public void putStatic(NameID name, RValue value) {
			load(name.module());
			globals.put(name, value);
		}

		public CallStack enter(Decl.Callable context) {
			load(context.getQualifiedName().toNameID().module());
			return new CallStack(this, context);
		}

		@Override
		public CallStack clone() {
			CallStack frame = new CallStack(this, this.context);
			frame.locals.putAll(locals);
			return frame;
		}

		/**
		 * Load a given module and make sure that all static variables are
		 * properly initialised.
		 *
		 * @param mid
		 * @param frame
		 */
		private void load(Path.ID mid) {
			if (!modules.contains(mid)) {
				// NOTE: must add module before attempting to load it.
				// Otherwise, static initialisers it contains will force itself
				// to be loaded.
				modules.add(mid);
				try {
					WhileyFile module = project.get(mid, WhileyFile.BinaryContentType).read();
					for (WhileyFile.Decl d : module.getDeclarations()) {
						if (d instanceof Decl.StaticVariable) {
							Decl.StaticVariable decl = (Decl.StaticVariable) d;
							RValue value = executeExpression(ANY_T, decl.getInitialiser(), this);
							globals.put(new NameID(mid, decl.getName().toString()), value);
						}
					}
					//
				} catch (IOException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * An enclosing scope captures the nested of declarations, blocks and other
	 * staments (e.g. loops). It is used to store information associated with
	 * these things such they can be accessed further down the chain. It can
	 * also be used to propagate information up the chain (for example, the
	 * environments arising from a break or continue statement).
	 *
	 * @author David J. Pearce
	 *
	 */
	private abstract static class EnclosingScope {
		private final EnclosingScope parent;

		public EnclosingScope(EnclosingScope parent) {
			this.parent = parent;
		}

		/**
		 * Get the innermost enclosing block of a given kind. For example, when
		 * processing a return statement we may wish to get the enclosing
		 * function or method declaration such that we can type check the return
		 * types.
		 *
		 * @param kind
		 */
		public <T extends EnclosingScope> T getEnclosingScope(Class<T> kind) {
			if (kind.isInstance(this)) {
				return (T) this;
			} else if (parent != null) {
				return parent.getEnclosingScope(kind);
			} else {
				// FIXME: better error propagation?
				return null;
			}
		}
	}

	/**
	 * Represents the enclosing scope for a function or method declaration.
	 *
	 * @author David J. Pearce
	 *
	 */
	private static class FunctionOrMethodScope extends EnclosingScope {
		private final Decl.Callable context;;

		public FunctionOrMethodScope(Decl.Callable context) {
			super(null);
			this.context = context;
		}

		public Decl.Callable getContext() {
			return context;
		}
	}

	public <T extends Decl.Callable> T resolveExactly(Name name, Type.Callable signature, Class<T> kind)
			throws ResolutionError {
		for (T decl : resolveAll(name, kind)) {
			if (decl.getType().equals(signature)) {
				return decl;
			}
		}
		return null;
	}

	public <T extends Decl.Named> List<T> resolveAll(Name name, Class<T> kind) throws ResolutionError {
		return resolver.resolveAll(name, kind);
	}
}
