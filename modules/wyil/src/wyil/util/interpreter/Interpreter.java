package wyil.util.interpreter;

import static wyil.util.interpreter.Interpreter.error;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.Policy.Parameters;
import java.util.*;

import wyautl.util.BigRational;
import wybs.lang.Build;
import wybs.lang.NameID;
import wybs.lang.SyntacticElement;
import wybs.util.ResolveError;
import wycommon.util.Pair;
import wyfs.lang.Path;
import wyil.lang.*;
import wyil.lang.Bytecode.*;

import static wyil.lang.SyntaxTree.*;
import wyil.util.TypeSystem;
import wyil.util.interpreter.Interpreter.ConstantObject;

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
	 * Responsible for expanding types to determine their underlying type and
	 * constraints.
	 */
	private final TypeSystem expander;
	
	/**
	 * Implementations for the internal operators
	 */
	private final InternalFunction[] operators;

	/**
	 * The debug stream provides an I/O stream through which debug bytecodes can
	 * write their messages.
	 */
	private final PrintStream debug;

	public Interpreter(Build.Project project, PrintStream debug) {
		this.project = project;
		this.debug = debug;
		this.expander = new TypeSystem(project);
		this.operators = StandardFunctions.standardFunctions;
	}

	private enum Status {
		RETURN,
		BREAK,
		CONTINUE,
		NEXT
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
	public Constant[] execute(NameID nid, Type.FunctionOrMethod sig, Constant... args) {
		// First, find the enclosing WyilFile
		try {
			Path.Entry<WyilFile> entry = project.get(nid.module(), WyilFile.ContentType);
			if (entry == null) {
				throw new IllegalArgumentException("no WyIL file found: " + nid.module());
			}
			// Second, find the given function or method
			WyilFile wyilFile = entry.read();
			WyilFile.FunctionOrMethod fm = wyilFile.functionOrMethod(nid.name(), sig);
			if (fm == null) {
				throw new IllegalArgumentException("no function or method found: " + nid + ", " + sig);
			} else if (sig.params().size() != args.length) {
				throw new IllegalArgumentException("incorrect number of arguments: " + nid + ", " + sig);
			}
			// Third, get and check the function or method body
			if (fm.getBody() == null) {
				// FIXME: Add support for native functions or methods. That is,
				// allow native functions to be implemented and called from the
				// interpreter.
				throw new IllegalArgumentException("no function or method body found: " + nid + ", " + sig);
			}
			// Fourth, construct the stack frame for execution
			SyntaxTree tree = fm.getTree();
			Constant[] frame = new Constant[tree.getLocations().size()];
			System.arraycopy(args, 0, frame, 0, sig.params().size());			
			// Check the precondition
			checkInvariants(frame,fm.getPrecondition());
			// Execute the method or function body
			executeBlock(fm.getBody(), frame);
			// Extra the return values
			Constant[] returns = extractReturns(frame,fm.type());
			//
			// Check the postcondition holds
			System.arraycopy(args,0,frame,0,args.length);
			checkInvariants(frame, fm.getPostcondition());
			// 				
			return returns;			
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
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
	private Constant[] extractReturns(Constant[] frame, Type.FunctionOrMethod type) {
		int paramsSize = type.params().size();
		int returnsSize = type.returns().size();
		Constant[] returns = new Constant[returnsSize];
		for (int i = 0, j = paramsSize; i != returnsSize; ++i, ++j) {
			returns[i] = frame[j];
		}
		return returns;
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
	private Status executeBlock(Location<Block> block, Constant[] frame) {
		for (int i = 0; i != block.numberOfOperands(); ++i) {
			Location<Stmt> stmt = (Location<Stmt>) block.getOperand(i);
			Status r = executeStatement(stmt, frame);
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
	private Status executeStatement(Location<?> stmt, Constant[] frame) {
		switch (stmt.getOpcode()) {
		case Bytecode.OPCODE_assert:
		case Bytecode.OPCODE_assume:
			return executeAssertOrAssume((Location<AssertOrAssume>) stmt, frame);
		case Bytecode.OPCODE_assign:
			return executeAssign((Location<Assign>) stmt, frame);
		case Bytecode.OPCODE_break:
			return executeBreak((Location<Break>) stmt, frame);
		case Bytecode.OPCODE_continue:
			return executeContinue((Location<Continue>) stmt, frame);
		case Bytecode.OPCODE_debug:
			return executeDebug((Location<Debug>) stmt, frame);
		case Bytecode.OPCODE_dowhile:
			return executeDoWhile((Location<DoWhile>) stmt, frame);
		case Bytecode.OPCODE_fail:
			return executeFail((Location<Fail>) stmt, frame);
		case Bytecode.OPCODE_if:
		case Bytecode.OPCODE_ifelse:
			return executeIf((Location<If>) stmt, frame);
		case Bytecode.OPCODE_indirectinvoke:
			executeIndirectInvoke((Location<IndirectInvoke>) stmt, frame);
			return Status.NEXT;
		case Bytecode.OPCODE_invoke:
			executeInvoke((Location<Invoke>) stmt, frame);
			return Status.NEXT;
		case Bytecode.OPCODE_namedblock:
			return executeNamedBlock((Location<NamedBlock>) stmt, frame);
		case Bytecode.OPCODE_while:
			return executeWhile((Location<While>) stmt, frame);
		case Bytecode.OPCODE_return:
			return executeReturn((Location<Return>) stmt, frame);
		case Bytecode.OPCODE_skip:
			return executeSkip((Location<Skip>) stmt, frame);
		case Bytecode.OPCODE_switch:
			return executeSwitch((Location<Switch>) stmt, frame);
		case Bytecode.OPCODE_vardeclinit:
		case Bytecode.OPCODE_vardecl:
			return executeVariableDeclaration((Location<VariableDeclaration>) stmt, frame);
		}

		deadCode(stmt);
		return null; // deadcode
	}

	private Status executeAssign(Location<Assign> stmt, Constant[] frame) {
		// FIXME: handle multi-assignments properly
		SyntaxTree.Location<?>[] lhs = stmt.getOperandGroup(LEFTHANDSIDE);
		Constant[] rhs = executeExpressions(stmt.getOperandGroup(RIGHTHANDSIDE), frame);
		for (int i = 0; i != lhs.length; ++i) {
			// TODO: this is not a very efficient way of implement assignment.
			// To improve performance, it would help if values were mutable,
			// rather than immutable constants.
			LVal lval = constructLVal(lhs[i], frame);
			lval.write(frame, rhs[i]);
		}
		return Status.NEXT;
	}

	/**
	 * Execute an assert or assume statement. In both cases, if the condition
	 * evaluates to false an exception is thrown.
	 * 
	 * @param stmt
	 *            --- Assert or Assume statement.
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Status executeAssertOrAssume(Location<AssertOrAssume> stmt, Constant[] frame) {
		//
		checkInvariants(frame,(Location<Expr>) stmt.getOperand(CONDITION));
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
	private Status executeBreak(Location<Break> stmt, Constant[] frame) {
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
	private Status executeContinue(Location<Continue> stmt, Constant[] frame) {
		// TODO: the continue bytecode supports a non-nearest exit and eventually
		// this should be supported.
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
	private Status executeDebug(Location<Debug> stmt, Constant[] frame) {
		//
		Constant.Array arr = executeExpression(ARRAY_T, stmt.getOperand(0), frame);
		for (Constant item : arr.values()) {
			BigInteger b = ((Constant.Integer) item).value();
			char c = (char) b.intValue();
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
	private Status executeDoWhile(Location<DoWhile> stmt, Constant[] frame) {
		Status r = Status.NEXT;
		while (r == Status.NEXT || r == Status.CONTINUE) {
			r = executeBlock(stmt.getBlock(0), frame);
			if (r == Status.NEXT) {
				Constant.Bool operand = executeExpression(BOOL_T, stmt.getOperand(CONDITION), frame);
				if (!operand.value()) {
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
	private Status executeFail(Location<Fail> stmt, Constant[] frame) {
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
	private Status executeIf(Location<If> stmt, Constant[] frame) {
		Bytecode.If bytecode = stmt.getBytecode();
		Constant.Bool operand = executeExpression(BOOL_T, stmt.getOperand(CONDITION), frame);
		if (operand.value()) {
			// branch taken, so execute true branch
			return executeBlock(stmt.getBlock(TRUEBRANCH), frame);
		} else if (bytecode.hasFalseBranch()) {
			// branch not taken, so execute false branch
			return executeBlock(stmt.getBlock(FALSEBRANCH), frame);
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
	private Status executeNamedBlock(Location<NamedBlock> stmt, Constant[] frame) {
		Location<Block> block = stmt.getBlock(0);
		return executeBlock(block,frame);
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
	private Status executeWhile(Location<While> stmt, Constant[] frame) {
		Status r;
		do {
			Constant.Bool operand = executeExpression(BOOL_T, stmt.getOperand(CONDITION), frame);
			if (!operand.value()) {
				return Status.NEXT;
			}
			// Keep executing the loop body until we exit it somehow.
			r = executeBlock(stmt.getBlock(0), frame);
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
	private Status executeReturn(Location<Return> stmt, Constant[] frame) {
		// We know that a return statement can only appear in either a function
		// or method declaration. It cannot appear, for example, in a type
		// declaration. Therefore, the enclosing declaration is a function or
		// method.
		SyntaxTree tree = stmt.getEnclosingTree();
		WyilFile.FunctionOrMethod fm = (WyilFile.FunctionOrMethod) tree.getEnclosingDeclaration();
		Type.FunctionOrMethod type = fm.type();
		int paramsSize = type.params().size();
		Constant[] values = executeExpressions(stmt.getOperands(), frame);
		for (int i = 0, j = paramsSize; i != values.length; ++i, ++j) {
			frame[j] = values[i];
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
	private Status executeSkip(Location<Skip> stmt, Constant[] frame) {
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
	private Status executeSwitch(Location<Switch> stmt, Constant[] frame) {
		Bytecode.Switch bytecode = stmt.getBytecode();
		Bytecode.Case[] cases = bytecode.cases();
		//
		Constant value = executeExpression(ANY_T, stmt.getOperand(CONDITION), frame);
		for (int i = 0; i != cases.length; ++i) {
			Bytecode.Case c = cases[i];
			Location<Block> body = stmt.getBlock(i);
			if (c.isDefault()) {
				return executeBlock(body, frame);
			} else {
				for (Constant v : c.values()) {
					if (v.equals(value)) {
						return executeBlock(body, frame);
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
	private Status executeVariableDeclaration(Location<VariableDeclaration> stmt, Constant[] frame) {
		// We only need to do something if this has an initialiser
		if(stmt.numberOfOperands() > 0) {
			Constant value = executeExpression(ANY_T, stmt.getOperand(0), frame);
			frame[stmt.getIndex()] = value;
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
	private <T extends Constant> T executeExpression(Class<T> expected, Location<?> expr, Constant[] frame) {
		Constant val;
		Bytecode.Expr bytecode = (Bytecode.Expr) expr.getBytecode();
		switch (bytecode.getOpcode()) {
		case Bytecode.OPCODE_const:
			val = executeConst((Location<Const>) expr, frame);
			break;
		case Bytecode.OPCODE_convert:
			val = executeConvert((Location<Bytecode.Convert>) expr, frame);
			break;
		case Bytecode.OPCODE_fieldload:
			val = executeFieldLoad((Location<FieldLoad>) expr, frame);
			break;
		case Bytecode.OPCODE_indirectinvoke:
			val = executeIndirectInvoke((Location<IndirectInvoke>) expr, frame)[0];
			break;
		case Bytecode.OPCODE_invoke:
			val = executeInvoke((Location<Invoke>) expr, frame)[0];
			break;
		case Bytecode.OPCODE_lambda:
			val = executeLambda((Location<Lambda>) expr, frame);
			break;
		case Bytecode.OPCODE_some:
		case Bytecode.OPCODE_all:
			val = executeQuantifier((Location<Quantifier>) expr, frame);
			break;
		case Bytecode.OPCODE_varaccess:
			val = executeVariableAccess((Location<VariableAccess>) expr, frame);
			break;
		default:
			val = executeOperator((Location<Operator>) expr, frame);
		}
		return checkType(val, expr, expected);
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
	private Constant executeConst(Location<Const> expr, Constant[] frame) {
		return expr.getBytecode().constant();
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
	private Constant executeConvert(Location<Convert> expr, Constant[] frame) {
		try {
			Constant operand = executeExpression(ANY_T, expr.getOperand(0), frame);
			Type target = expander.getUnderlyingType(expr.getType());
			return convert(operand, target, expr);
		} catch (ResolveError e) {
			error(e.getMessage(), expr);
			return null;
		}
	}

	/**
	 * Execute a binary operator at a given point in the function or method
	 * body. This will check operands match their expected types.
	 *
	 * @param expr
	 *            --- The expression to execute
	 * @param frame
	 *            --- The current stack frame
	 * @return
	 */
	private Constant executeOperator(Location<Operator> expr, Constant[] frame) {
		Bytecode bytecode = expr.getBytecode();
		switch (bytecode.getOpcode()) {
		case Bytecode.OPCODE_logicaland: {
			// This is a short-circuiting operator
			Constant.Bool lhs = executeExpression(BOOL_T, expr.getOperand(0), frame);
			if (!lhs.value()) {
				// Short-circuit
				return Constant.False;
			}
			return executeExpression(BOOL_T, expr.getOperand(1), frame);
		}
		case Bytecode.OPCODE_logicalor: {
			// This is a short-circuiting operator
			Constant.Bool lhs = executeExpression(BOOL_T, expr.getOperand(0), frame);
			if (lhs.value()) {
				// Short-circuit
				return Constant.True;
			}
			return executeExpression(BOOL_T, expr.getOperand(1), frame);
		}
		default: {
			// This is the default case where can treat the operator as an
			// external function and just call it with the evaluated operands.
			SyntaxTree.Location<?>[] operands = expr.getOperands();
			Constant[] values = new Constant[operands.length];
			// Read all operands
			for (int i = 0; i != operands.length; ++i) {
				values[i] = executeExpression(ANY_T, operands[i], frame);
			}
			// Compute result
			return operators[bytecode.getOpcode()].apply(values, this, expr);
		}
		}
	}

	
	private Constant executeFieldLoad(Location<FieldLoad> loc, Constant[] frame) {
		Bytecode.FieldLoad bytecode = loc.getBytecode();
		Constant.Record rec = executeExpression(RECORD_T, loc.getOperand(0), frame);
		return rec.values().get(bytecode.fieldName());
	}

	private Constant executeQuantifier(Location<Quantifier> expr, Constant[] frame) {
		boolean r = executeQuantifier(0, expr, frame);
		// r ==> continued all the way through
		// ! ==> terminated early
		if (expr.getOpcode() == Bytecode.OPCODE_some) {
			return r ? Constant.False : Constant.True;
		} else {
			return r ? Constant.True : Constant.False;
		}
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
	private boolean executeQuantifier(int index, Location<Quantifier> expr, Constant[] frame) {
		Bytecode.Quantifier bytecode = expr.getBytecode();
		if (index == expr.numberOfOperandGroups()) {
			// This is the base case where we evaluate the condition itself.
			Constant.Bool r = executeExpression(BOOL_T, expr.getOperand(CONDITION), frame);
			int opcode = bytecode.getOpcode();
			if (r.value() && opcode == Bytecode.OPCODE_some) {
				return false;
			} else if (!r.value() && opcode == Bytecode.OPCODE_all) {
				return false;
			}
			// This means that, for the given quantifier kind, we have to
			// continue as is.
			return true;
		} else {
			SyntaxTree.Location<?>[] range = expr.getOperandGroup(index);
			int var = range[VARIABLE].getIndex();
			Constant.Integer start = executeExpression(INT_T, range[START], frame);
			Constant.Integer end = executeExpression(INT_T, range[END], frame);			
			long s = start.value().longValue();
			long e = end.value().longValue();
			for (long i = s; i < e; ++i) {
				frame[var] = new Constant.Integer(BigInteger.valueOf(i));
				boolean r = executeQuantifier(index + 1, expr, frame);
				if (!r) {
					// early termination
					return r;
				}
			}
			return true;
		}
	}
	
	private Constant executeLambda(Location<Lambda> expr, Constant[] frame) {
		// Clone the frame at this point, in order that changes seen after this
		// bytecode is executed are not propagated into the lambda itself.
		frame = Arrays.copyOf(frame, frame.length);
		return new ConstantLambda(expr, frame);
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
	private Constant executeVariableAccess(Location<VariableAccess> expr, Constant[] frame) {
		Location<VariableDeclaration> decl = getVariableDeclaration(expr);
		return frame[decl.getIndex()];
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
	 * @param operands
	 * @param frame
	 * @return
	 */
	private Constant[] executeExpressions(Location<?>[] operands, Constant[] frame) {
		Constant[][] results = new Constant[operands.length][];
		int count = 0;
		for(int i=0;i!=operands.length;++i) {
			results[i] = executeMultiReturnExpression(operands[i],frame);
			count += results[i].length;
		}
		Constant[] rs = new Constant[count];
		int j = 0;
		for(int i=0;i!=operands.length;++i) {
			Constant[] r = results[i];
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
	private Constant[] executeMultiReturnExpression(Location<?> expr, Constant[] frame) {
		Bytecode.Expr bytecode = (Expr) expr.getBytecode();
		switch (bytecode.getOpcode()) {
		case Bytecode.OPCODE_indirectinvoke:
			return executeIndirectInvoke((Location<IndirectInvoke>) expr, frame);
		case Bytecode.OPCODE_invoke:
			return executeInvoke((Location<Invoke>) expr, frame);
		case Bytecode.OPCODE_const:
		case Bytecode.OPCODE_convert:
		case Bytecode.OPCODE_fieldload:
		case Bytecode.OPCODE_lambda:
		case Bytecode.OPCODE_some:
		case Bytecode.OPCODE_all:
		default:
			Constant val = executeExpression(ANY_T, expr, frame);
			return new Constant[] { val };
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
	private Constant[] executeIndirectInvoke(Location<IndirectInvoke> expr, Constant[] frame) {
				
		// FIXME: This is implementation is *ugly* --- can we do better than
		// this? One approach is to register an anonymous function so that we
		// can reuse executeAllWithin in both bases. This is hard to setup
		// though.
		
		SyntaxTree.Location<?> src = expr.getOperand(0);
		Constant operand = executeExpression(ANY_T, src,frame);
		// Check that we have a function reference
		if(operand instanceof Constant.FunctionOrMethod) {
			Constant.FunctionOrMethod cl = checkType(operand, src, Constant.FunctionOrMethod.class);			
			Constant[] arguments = executeExpressions(expr.getOperandGroup(ARGUMENTS),frame);			
			return execute(cl.name(),cl.type(),arguments);
		} else {
			ConstantLambda cl = checkType(operand, src, ConstantLambda.class);
			// Yes we do; now construct the arguments. This requires merging the
			// constant arguments provided in the lambda itself along with those
			// operands provided for the "holes".
			Constant[] lambdaFrame = Arrays.copyOf(cl.frame, cl.frame.length); 
			int[] parameters = cl.lambda.getBytecode().getOperandGroup(PARAMETERS);
			Constant[] arguments = executeExpressions(expr.getOperandGroup(ARGUMENTS),frame);
			for(int i=0;i!=parameters.length;++i) {			
				lambdaFrame[parameters[i]] = arguments[i];
			}
			// Make the actual call. This may return multiple values since it is
			// a function/method invocation.
			return executeMultiReturnExpression(cl.lambda.getOperand(BODY), lambdaFrame);
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
	 */
	private Constant[] executeInvoke(Location<Invoke> expr, Constant[] frame) {
		Bytecode.Invoke bytecode = expr.getBytecode();
		SyntaxTree.Location<?>[] operands = expr.getOperands();
		Constant[] arguments = executeExpressions(operands,frame);		
		return execute(bytecode.name(), bytecode.type(), arguments);		
	}

	// =============================================================
	// Constants
	// =============================================================

	/**
	 * Convert a given value of one into a value of a different type. For
	 * example, converting a int value into a real value.
	 *
	 * @param from
	 * @param value
	 * @param to
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Constant convert(Constant value, Type to, SyntacticElement context) {
		Type type = value.type();
		if (Type.isSubtype(to, type)) {
			// In this case, we don't need to do anything because the value is
			// already of the correct type.
			return value;
		} else if (type instanceof Type.Reference && to instanceof Type.Reference) {
			if (Type.isSubtype(((Type.Reference) to).element(), ((Type.Reference) type).element())) {
				// OK, it's just the lifetime that differs.
				return value;
			}
		} else if (to instanceof Type.Record) {
			return convert(value, (Type.Record) to, context);
		} else if (to instanceof Type.Array) {
			return convert(value, (Type.Array) to, context);
		} else if (to instanceof Type.Union) {
			return convert(value, (Type.Union) to, context);
		} else if (to instanceof Type.FunctionOrMethod) {
			return convert(value, (Type.FunctionOrMethod) to, context);
		}
		deadCode(context);
		return null;
	}

	/**
	 * Convert a value into a record type. The value must be of record type for
	 * this to make sense and must either have the same fields or, in the case
	 * of an open record, have a superset of fields.
	 *
	 * @param value
	 * @param to
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Constant convert(Constant value, Type.Record to, SyntacticElement context) {
		checkType(value, context, Constant.Record.class);
		Constant.Record rv = (Constant.Record) value;
		HashSet<String> rv_fields = new HashSet<String>(rv.values().keySet());
		// Check fields in value are subset of those in target type
		if (!rv_fields.containsAll(to.keys())) {
			error("cannot convert between records with differing fields", context);
			return null; // deadcode
		} else {
			HashMap<String, Constant> nValues = new HashMap<String, Constant>();
			for (String field : to.keys()) {
				Constant nValue = convert(rv.values().get(field), to.field(field), context);
				nValues.put(field, nValue);
			}
			return new Constant.Record(nValues);
		}
	}

	/**
	 * Convert a value into a list type. The value must be of list type for this
	 * to make sense.
	 *
	 * @param value
	 * @param to
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Constant convert(Constant value, Type.Array to, SyntacticElement context) {
		checkType(value, context, Constant.Array.class);
		Constant.Array lv = (Constant.Array) value;
		ArrayList<Constant> values = new ArrayList<Constant>(lv.values());
		for (int i = 0; i != values.size(); ++i) {
			values.set(i, convert(values.get(i), to.element(), context));
		}
		return new Constant.Array(values);
	}

	/**
	 * Convert a value into a union type. In this case, we must find an
	 * appropriate bound for the type in question. If no such type can be found,
	 * then this is ambiguous or otherwise invalid.
	 *
	 * @param value
	 * @param to
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Constant convert(Constant value, Type.Union to, SyntacticElement context) {
		Type type = value.type();
		for (Type bound : to.bounds()) {
			if (Type.isExplicitCoerciveSubtype(bound, type)) {
				return convert(value, bound, context);
			}
		}
		deadCode(context);
		return null;
	}

	/**
	 * Convert a value into a function type. In this case, we actually do
	 * nothing for now.
	 *
	 * @param value
	 * @param to
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	private Constant convert(Constant value, Type.FunctionOrMethod to, SyntacticElement context) {
		return value;
	}
	
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
	private LVal constructLVal(SyntaxTree.Location<?> expr, Constant[] frame) {
		switch (expr.getOpcode()) {
		case Bytecode.OPCODE_arrayindex: {
			LVal src = constructLVal(expr.getOperand(0), frame);
			Constant.Integer index = executeExpression(INT_T, expr.getOperand(1), frame);
			int i = index.value().intValue();
			return new ArrayLVal(src, i);
		}
		case Bytecode.OPCODE_dereference: {
			LVal src = constructLVal(expr.getOperand(0), frame);
			return new DereferenceLVal(src);
		}
		case Bytecode.OPCODE_fieldload: {
			Bytecode.FieldLoad fl = (Bytecode.FieldLoad) expr.getBytecode();
			LVal src = constructLVal(expr.getOperand(0), frame);
			return new RecordLVal(src, fl.fieldName());
		}
		case Bytecode.OPCODE_varaccess: {
			Location<VariableDeclaration> decl = getVariableDeclaration(expr); 
			return new VariableLVal(decl.getIndex());
		}
		}
		deadCode(expr);
		return null; // deadcode
	}
		
	private abstract class LVal {
		abstract public Constant read(Constant[] frame);
		abstract public void write(Constant[] frame,Constant rhs);
	}
	
	private class VariableLVal extends LVal {
		private final int index;
		
		public VariableLVal(int index) {
			this.index = index;
		}
		
		@Override
		public Constant read(Constant[] frame) {
			return frame[index];
		}

		@Override
		public void write(Constant[] frame, Constant rhs) {
			frame[index] = rhs;
		}
		
	}
	
	private class ArrayLVal extends LVal {
		private final LVal src;
		private final int index;
		
		public ArrayLVal(LVal src, int index) {
			this.src = src;
			this.index = index;
		}

		@Override
		public Constant read(Constant[] frame) {
			Constant.Array src = checkType(this.src.read(frame),null,Constant.Array.class);
			return src.values().get(index);		
		}

		@Override
		public void write(Constant[] frame,Constant rhs) {
			Constant.Array arr = checkType(this.src.read(frame),null,Constant.Array.class);			
			ArrayList<Constant> values = new ArrayList<Constant>(arr.values());				
			values.set(index, rhs);
			src.write(frame,new Constant.Array(values));
		}
	}
	
	private class RecordLVal extends LVal {
		private final LVal src;
		private final String field;
		
		public RecordLVal(LVal src, String field) {
			this.src = src;
			this.field = field;
		}
		
		@Override
		public Constant read(Constant[] frame) {
			Constant.Record src = checkType(this.src.read(frame),null,Constant.Record.class);
			return src.values().get(field);
		}
		@Override
		public void write(Constant[] frame, Constant rhs) {
			Constant.Record rec = checkType(this.src.read(frame),null,Constant.Record.class);
			HashMap<String, Constant> values = new HashMap<String, Constant>(rec.values());			
			values.put(field, rhs);
			src.write(frame,new Constant.Record(values));			
		}
	}
	
	private class DereferenceLVal extends LVal {
		private final LVal src;

		public DereferenceLVal(LVal src) {
			this.src = src;
		}
		
		@Override
		public Constant read(Constant[] frame) {
			ConstantObject objecy = checkType(src.read(frame),null,ConstantObject.class);
			return objecy.read();
		}
 		
		@Override
		public void write(Constant[] frame, Constant rhs) {
			ConstantObject object = checkType(src.read(frame),null,ConstantObject.class);
			object.write(rhs);
		}
	}
	
	/**
	 * Determine whether a given value is a member of a given type. In the case
	 * of a nominal type, then we must also check that any invariant(s) for that
	 * type hold true as well.
	 * 
	 * @param value
	 * @param type
	 * @param context
	 *            --- Context in which bytecodes are executed
	 * @return
	 */
	public boolean isMemberOfType(Constant value, Type type, SyntacticElement context) {
		if (type instanceof Type.Any) {
			return true;
		} else if (type instanceof Type.Void) {
			return false;
		} else if (type instanceof Type.Null) {
			return value instanceof Constant.Null;
		} else if (type instanceof Type.Bool) {
			return value instanceof Constant.Bool;
		} else if (type instanceof Type.Byte) {
			return value instanceof Constant.Byte;
		} else if (type instanceof Type.Int) {
			return value instanceof Constant.Integer;
		} else if (type instanceof Type.Reference) {
			if (value instanceof ConstantObject) {
				ConstantObject obj = (ConstantObject) value;
				Type.Reference rt = (Type.Reference) type;
				return isMemberOfType(obj.value, rt.element(), context);
			}
			return false;
		} else if (type instanceof Type.Array) {
			if (value instanceof Constant.Array) {
				Constant.Array t = (Constant.Array) value;
				Type element = ((Type.Array) type).element();
				boolean r = true;
				for (Constant val : t.values()) {
					r &= isMemberOfType(val, element, context);
				}
				return r;
			}
			return false;
		} else if (type instanceof Type.Record) {
			if (value instanceof Constant.Record) {
				Type.Record rt = (Type.Record) type;
				Constant.Record t = (Constant.Record) value;
				Set<String> fields = t.values().keySet();
				if (!fields.containsAll(rt.keys()) || (!rt.keys().containsAll(fields) && !rt.isOpen())) {
					// In this case, the set of fields does not match properly
					return false;
				}
				boolean r = true;
				for (String field : fields) {
					r &= isMemberOfType(t.values().get(field), rt.field(field), context);
				}
				return r;
			}
			return false;
		} else if (type instanceof Type.Union) {
			Type.Union t = (Type.Union) type;
			for (Type element : t.bounds()) {
				if (isMemberOfType(value, element, context)) {
					return true;
				}
			}
			return false;
		} else if (type instanceof Type.Negation) {
			Type.Negation t = (Type.Negation) type;
			return !isMemberOfType(value, t.element(), context);
		} else if (type instanceof Type.FunctionOrMethod) {
			if (value instanceof Constant.FunctionOrMethod) {
				Constant.FunctionOrMethod l = (Constant.FunctionOrMethod) value;
				if (Type.isSubtype(type, l.type())) {
					return true;
				}
			}
			return false;
		} else if (type instanceof Type.Nominal) {
			Type.Nominal nt = (Type.Nominal) type;
			NameID nid = nt.name();
			try {
				// First, attempt to locate the enclosing module for this
				// nominal type.  
				Path.Entry<WyilFile> entry = project.get(nid.module(), WyilFile.ContentType);
				if (entry == null) {
					throw new IllegalArgumentException("no WyIL file found: " + nid.module());
				}
				// Read in the module. This may result in it being read from
				// disk, or from a cache in memory, or even from somewhere else.
				WyilFile wyilFile = entry.read();
				WyilFile.Type td = wyilFile.type(nid.name());
				if (td == null) {
					error("undefined nominal type encountered: " + nid, context);
				} else if (!isMemberOfType(value, td.type(), context)) {
					return false;
				}
				// Check every invariant associated with this type evaluates to
				// true. 
				List<Location<Expr>> invariants = td.getInvariant();
				if (invariants.size() > 0) {
					SyntaxTree tree = td.getTree();
					Constant[] frame = new Constant[tree.getLocations().size()];
					frame[0] = value;
					checkInvariants(frame, invariants);
				}
				// Done
				return true;
			} catch (IOException e) {
				error(e.getMessage(), context);
			} catch (Error e) {
				// This signals that a runtime fault occurred in the body of the
				// type invariant.
				return false;
			}
		}

		deadCode(context);
		return false; // deadcode
	}

	/**
	 * Evaluate zero or more conditional expressions, and check whether any is
	 * false. If so, raise an exception indicating a runtime fault.
	 * 
	 * @param frame
	 * @param context
	 * @param invariants
	 */
	public void checkInvariants(Constant[] frame, List<Location<Expr>> invariants) {
		for (int i = 0; i != invariants.size(); ++i) {
			Constant.Bool b = executeExpression(BOOL_T, invariants.get(i), frame);
			if (!b.value()) {
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
	public void checkInvariants(Constant[] frame, Location<Expr>... invariants) {
		for (int i = 0; i != invariants.length; ++i) {
			Constant.Bool b = executeExpression(BOOL_T, invariants[i], frame);
			if (!b.value()) {
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
	public static <T extends Constant> T checkType(Constant operand, SyntacticElement context, Class<T>... types) {
		// Got through each type in turn checking for a match
		for (int i = 0; i != types.length; ++i) {
			if (types[i].isInstance(operand)) {
				// Matched!
				return (T) operand;
			}
		}
		// No match, therefore through an error
		if(operand == null) {
			error("null operand", context);
		} else {
			error("operand returned " + operand.getClass().getName() + ", expecting one of " + Arrays.toString(types), context);
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
	private Object deadCode(SyntacticElement element) {
		// FIXME: do more here
		throw new RuntimeException("internal failure --- dead code reached");
	}

	public Location<VariableDeclaration> getVariableDeclaration(Location<?> decl) {
		switch (decl.getOpcode()) {
		case Bytecode.OPCODE_aliasdecl:
		case Bytecode.OPCODE_varaccess:
			return getVariableDeclaration(decl.getOperand(0));
		case Bytecode.OPCODE_vardecl:
		case Bytecode.OPCODE_vardeclinit:
			return (Location<VariableDeclaration>) decl;
		default:
			throw new RuntimeException("internal failure --- dead code reached");
		}
	}
	
	private static final Class<Constant> ANY_T = Constant.class;
	private static final Class<Constant.Bool> BOOL_T = Constant.Bool.class;
	private static final Class<Constant.Integer> INT_T = Constant.Integer.class;
	private static final Class<Constant.Array> ARRAY_T = Constant.Array.class;
	private static final Class<Constant.Record> RECORD_T = Constant.Record.class;
	
	/**
	 * Represents an object allocated on the heap.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class ConstantObject extends Constant {
		private Constant value;

		public ConstantObject(Constant value) {
			this.value = value;
		}

		public Constant read() {			
			return value;
		}

		public void write(Constant newValue) {			
			value = newValue;
		}

		public boolean equals(Object o) {
			return o == this;
		}

		public int hashCode() {
			return value.hashCode();
		}

		@Override
		public int compareTo(Constant o) {
			// This method cannot be implmened because it does not make sense to
			// compare a reference with another reference.
			throw new UnsupportedOperationException("ConstantObject.compare() cannot be implemented");
		}

		@Override
		public wyil.lang.Type type() {
			// TODO: extend wyil.lang.Codes.NewObject with a lifetime and use it
			return wyil.lang.Type.Reference(value.type(), "*");
		}
	}

	/**
	 * Represents an object allocated on the heap.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class ConstantLambda extends Constant {
		private final Location<Bytecode.Lambda> lambda;		
		private final Constant[] frame;		

		public ConstantLambda(Location<Bytecode.Lambda> lambda, Constant... frame) {
			this.lambda = lambda;
			this.frame = frame;
		}

		public boolean equals(Object o) {
			return o == this;
		}

		public int hashCode() {
			return Arrays.hashCode(frame);
		}

		@Override
		public int compareTo(Constant o) {
			// This method cannot be implmened because it does not make sense to
			// compare a reference with another reference.
			throw new UnsupportedOperationException("ConstantObject.compare() cannot be implemented");
		}

		@Override
		public wyil.lang.Type type() {
			return lambda.getType();
		}
	}

	/**
	 * An internal function is simply a named internal function. This reads a
	 * bunch of operands and returns a set of results.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public static interface InternalFunction {
		public Constant apply(Constant[] operands, Interpreter enclosing, Location<Operator> context);
	}
}
