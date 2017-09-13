package wyc.stage;

import wybs.lang.Build;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyc.lang.WhileyFile;

import static wyc.lang.WhileyFile.*;

/**
 * <p>
 * Responsible for determining when a value of a dynamically sized data type can
 * be "moved" or must be "copied". Moving is preferable preferable (when
 * permitted) because the original reference can be used without copying the
 * underlying data. The following provides a useful example:
 * </p>
 *
 * <pre>
 * function g(int[] xs, int i) -> (int[] ys):
 *     xs[j] = f(xs)
 *     return xs
 * </pre>
 *
 * <p>
 * Here, in the invocation <code>f(xs)</code> the array <code>xs</code> cannot
 * be moved since it is live afterwards. Instead, we must clone <code>xs</code>
 * at this point. However, the subsequent use of <code>xs</code> in the
 * <code>return</code> statement does not require a clone and can be moved
 * (since <code>xs</code> is no longer live).
 * </p>
 *
 * <p>
 * The following illustrates another situation where temporary moves or
 * "borrows" are permitted:
 * </p>
 *
 * <pre>
 * function get(int[] xs, int i, int j) -> (int r):
 *     int sum = xs[i]
 *     sum = sum + xs[j]
 *     return sum
 * </pre>
 *
 * Clearly, there is not need to clone <code>xs</code> when it is used in the
 * initialiser for <code>sum</code>. This is because the use is temporary and
 * does not modify <code>xs</code>. We say that <code>xs</code> is not
 * <em>consumed</em>.
 *
 * @author David J. Pearce
 *
 */
public class MoveAnalysis implements Build.Stage<WhileyFile> {
	public MoveAnalysis(Build.Task builder) {

	}


	@Override
	public void apply(WhileyFile module) {
		for(Decl d : module.getDeclarations()) {
			check(d);
		}
	}

	private void check(Decl decl) {
		if(decl instanceof Decl.Type) {
			check((Decl.Type) decl);
		} else if(decl instanceof Decl.FunctionOrMethod) {
			check((Decl.FunctionOrMethod) decl);
		} else {
			// do nothing?
		}
	}

	private void check(Decl.Type t) {
		for(Expr e : t.getInvariant()) {
			checkExpression(false,e);
		}
	}
	private void check(Decl.FunctionOrMethod fm) {
		// FIXME: check requires / ensures clauses?

		// Examine body of the function or method
		checkBlock((fm.getBody()));
	}

	private  void check(Stmt stmt) {
		switch(stmt.getOpcode()) {
		case STMT_assert:
			checkAssert((Stmt.Assert) stmt);
		case STMT_assign:
			checkAssign((Expr.Assign) stmt);
			break;
		case STMT_assume:
			checkAssume((Stmt.Assume) stmt);
			break;
		case STMT_block:
			checkBlock((Stmt.Block) stmt);
			break;
		case STMT_break:
			checkBreak((Stmt.Break) stmt);
			break;
		case STMT_continue:
			checkContinue((Stmt.Continue) stmt);
			break;
		case STMT_debug:
			checkDebug((Stmt.Debug) stmt);
			break;
		case STMT_dowhile:
			checkDoWhile((Stmt.DoWhile) stmt);
			break;
		case STMT_fail:
			checkFail((Stmt.Fail) stmt);
			break;
		case STMT_if:
		case STMT_ifelse:
			checkIfElse((Stmt.IfElse) stmt);
			break;
		case EXPR_indirectinvoke:
			checkIndirectInvoke(false, (Expr.IndirectInvoke) stmt);
			break;
		case EXPR_invoke:
			checkInvoke(false, (Expr.Invoke) stmt);
			break;
		case STMT_namedblock:
			checkNamedBlock((Stmt.NamedBlock) stmt);
			break;
		case STMT_return:
			checkReturn((Stmt.Return) stmt);
			break;
		case STMT_skip:
			checkSkip((Stmt.Skip) stmt);
			break;
		case STMT_switch:
			checkSwitch((Stmt.Switch) stmt);
		case DECL_var:
		case DECL_varinit:
			checkVariableDeclaration((Decl.Variable) stmt);
			break;
		case STMT_while:
			checkWhile((Stmt.While) stmt);
			break;
		default:
			throw new IllegalArgumentException("Unknown statement encountered: " + stmt);
		}
	}

	private void checkAssert(Stmt.Assert stmt) {
		checkExpression(false,stmt.getCondition());
	}

	private void checkAssign(Stmt.Assign stmt) {

	}

	private void checkAssume(Stmt.Assume stmt) {
		checkExpression(false,stmt.getCondition());
	}

	private void checkBlock(Stmt.Block stmt) {
		for(int i=0;i!=stmt.size();++i) {
			check(stmt.getOperand(i));
		}
	}

	private void checkBreak(Stmt.Break stmt) {

	}

	private void checkContinue(Stmt.Continue stmt) {

	}

	private void checkDebug(Stmt.Debug stmt) {
		checkExpression(false,stmt.getOperand());
	}

	private void checkDoWhile(Stmt.DoWhile stmt) {
		checkExpression(false,stmt.getCondition());
		checkBlock(stmt.getBody());
	}

	private void checkFail(Stmt.Fail stmt) {

	}

	private void checkIfElse(Expr.IfElse stmt) {
		checkExpression(false,stmt.getCondition());
		checkBlock(stmt.getTrueBranch());
		if(stmt.hasFalseBranch()) {
			checkBlock(stmt.getFalseBranch());
		}
	}

	private void checkNamedBlock(Stmt.NamedBlock stmt) {
		checkBlock(stmt.getBlock());
	}

	private void checkReturn(Stmt.Return stmt) {
		checkExpressions(true, stmt.getReturns());
	}

	private void checkSkip(Stmt.Skip stmt) {

	}

	private void checkSwitch(Stmt.Switch stmt) {
		checkExpression(false,stmt.getCondition());
		for(Stmt.Case c : stmt.getCases()) {
			checkBlock(c.getBlock());
		}
	}

	private void checkWhile(Stmt.While stmt) {
		checkExpression(false,stmt.getCondition());
		checkBlock(stmt.getBody());
	}

	private void checkVariableDeclaration(Decl.Variable stmt) {
		if(stmt.hasInitialiser()) {
			checkExpression(true,stmt.getInitialiser());
		}
	}

	private void checkExpressions(boolean consumed, Tuple<Expr> expressions) {
		for(int i=0;i!=expressions.size();++i) {
			checkExpression(consumed, expressions.getOperand(i));
		}
	}

	private void checkExpression(boolean consumed, Expr expr) {
		switch(expr.getOpcode()) {
		case EXPR_constant:
			checkConstant(consumed, (Expr.Constant) expr);
			break;
		case EXPR_varcopy:
			checkVariable(consumed, (Expr.VariableAccess) expr);
			break;
		case EXPR_staticvar:
			checkStaticVariable(consumed, (Expr.StaticVariableAccess) expr);
			break;
		case EXPR_cast:
			checkCast(consumed, (Expr.Cast) expr);
			break;
		case EXPR_invoke:
			checkInvoke(consumed, (Expr.Invoke) expr);
			break;
		case EXPR_indirectinvoke:
			checkIndirectInvoke(consumed, (Expr.IndirectInvoke) expr);
		// Conditions
		case EXPR_lnot:
			checkUnaryOperator(consumed, (Expr.UnaryOperator) expr);
			break;
		case EXPR_lor:
		case EXPR_land:
		case EXPR_liff:
		case EXPR_limplies:
			checkNaryOperator(consumed, (Expr.NaryOperator) expr);
			break;
		case EXPR_lall:
		case EXPR_lsome:
			checkQuantifier(consumed, (Expr.Quantifier) expr);
			break;
		case EXPR_is:
			checkIs(consumed, (Expr.Is) expr);
			break;
		// Comparators
		case EXPR_eq:
		case EXPR_neq:
		case EXPR_ilt:
		case EXPR_ile:
		case EXPR_igt:
		case EXPR_igteq:
			checkNaryOperator(consumed, (Expr.NaryOperator) expr);
			break;
		// Arithmetic Operators
		case EXPR_ineg:
			checkUnaryOperator(consumed, (Expr.UnaryOperator) expr);
			break;
		case EXPR_iadd:
		case EXPR_isub:
		case EXPR_imul:
		case EXPR_idiv:
		case EXPR_irem:
			checkNaryOperator(consumed, (Expr.NaryOperator) expr);
			break;
		// Bitwise expressions
		case EXPR_bnot:
			checkUnaryOperator(consumed, (Expr.UnaryOperator) expr);
			break;
		case EXPR_band:
		case EXPR_bor:
		case EXPR_bxor:
			checkNaryOperator(consumed, (Expr.NaryOperator) expr);
			break;
		case EXPR_bshl:
		case EXPR_bshr:
			checkBinaryOperator(consumed, (Expr.BinaryOperator) expr);
			break;
		// Record Expressions
		case EXPR_rinit:
			checkRecordInitialiser(consumed, (Expr.RecordInitialiser) expr);
			break;
		case EXPR_rread:
			checkRecordAccess(consumed, (Expr.RecordAccess) expr);
			break;
		case EXPR_rwrite:
			checkRecordUpdate(consumed, (Expr.RecordUpdate) expr);
			break;
			// Array expressions
		case EXPR_alen:
			checkUnaryOperator(consumed, (Expr.UnaryOperator) expr);
		case EXPR_ainit:
			checkNaryOperator(consumed, (Expr.NaryOperator) expr);
			break;
		case EXPR_agen:
			checkArrayGenerator(consumed, (Expr.ArrayGenerator) expr);
			break;
		case EXPR_aread:
			checkArrayAccess(consumed, (Expr.ArrayAccess) expr);
			break;
		case EXPR_awrite:
			checkArrayUpdate(consumed, (Expr.ArrayUpdate) expr);
			break;
			// Reference expressions
		case EXPR_pread:
		case EXPR_pinit:
			checkUnaryOperator(consumed, (Expr.Dereference) expr);
			break;
		case EXPR_lread:
			checkLambdaConstant(consumed, (Expr.LambdaAccess) expr);
			break;
		default:
			throw new IllegalArgumentException("Unknown expression encountered: " + expr);
		}
	}

	public void checkConstant(boolean consumed, Expr.Constant expr) {
	}

	public void checkVariable(boolean consumed, Expr.VariableAccess expr) {
		if (!consumed) {
			// In this case, we have identified a variable access which is
			// not consumed and therefore can be implemented as a move.

			// FIXME: implement change to variable move
		}
	}

	public void checkStaticVariable(boolean consumed, Expr.StaticVariableAccess expr) {
		// FIXME: unsure what to do here?
	}

	public void checkCast(boolean consumed, Expr.Cast expr) {
		checkExpression(consumed, expr.getOperand());
	}

	public void checkInvoke(boolean consumed, Expr.Invoke expr) {
		checkExpressions(true, expr.getArguments());
	}

	public void checkIndirectInvoke(boolean consumed, Expr.IndirectInvoke expr) {
		checkExpression(false, expr.getSource());
		checkExpressions(true, expr.getArguments());
	}

	public void checkIs(boolean consumed, Expr.Is expr) {
		checkExpression(false, expr.getOperand());
	}

	public void checkQuantifier(boolean consumed, Expr.Quantifier expr) {
		checkExpression(false, expr.getOperand());
	}

	public void checkNaryOperator(boolean consumed, Expr.NaryOperator expr) {
		Tuple<Expr> operands = expr.getArguments();
		for(int i=0;i!=operands.size();++i) {
			checkExpression(false, operands.getOperand(i));
		}
	}

	public void checkUnaryOperator(boolean consumed, Expr.UnaryOperator expr) {
		checkExpression(false, expr.getOperand());
	}

	public void checkBinaryOperator(boolean consumed, Expr.BinaryOperator expr) {
		checkExpression(false, expr.getFirstOperand());
		checkExpression(false, expr.getSecondOperand());
	}

	public void checkRecordInitialiser(boolean consumed, Expr.RecordInitialiser expr) {
		Tuple<Pair<Identifier,Expr>> operands = expr.getFields();
		for(int i=0;i!=operands.size();++i) {
			checkExpression(consumed, operands.getOperand(i).getSecond());
		}
	}

	public void checkRecordAccess(boolean consumed, Expr.RecordAccess expr) {
		checkExpression(false, expr.getOperand());
	}

	public void checkRecordUpdate(boolean consumed, Expr.RecordUpdate expr) {
		checkExpression(false, expr.getFirstOperand());
		checkExpression(consumed, expr.getSecondOperand());
	}

	public void checkArrayGenerator(boolean consumed, Expr.ArrayGenerator expr) {
		checkExpression(consumed, expr.getFirstOperand());
		checkExpression(false, expr.getSecondOperand());
	}

	public void checkArrayAccess(boolean consumed, Expr.ArrayAccess expr) {
		checkExpression(false, expr.getFirstOperand());
		checkExpression(false, expr.getSecondOperand());
	}

	public void checkArrayUpdate(boolean consumed, Expr.ArrayUpdate expr) {
		checkExpression(false, expr.getFirstOperand());
		checkExpression(false, expr.getSecondOperand());
		checkExpression(consumed, expr.getThirdOperand());
	}

	public void checkLambdaConstant(boolean consumed, Expr.LambdaAccess expr) {
	}
}
