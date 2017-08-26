package wyil.util;

import java.util.List;

import wybs.lang.Build;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyc.util.AbstractWhileyFile.Expr;
import wyc.util.AbstractWhileyFile.Type;
import wyil.lang.WyilFile;

import static wyc.util.AbstractWhileyFile.EXPR_add;
import static wyc.util.AbstractWhileyFile.EXPR_and;
import static wyc.util.AbstractWhileyFile.EXPR_arrgen;
import static wyc.util.AbstractWhileyFile.EXPR_arridx;
import static wyc.util.AbstractWhileyFile.EXPR_arrinit;
import static wyc.util.AbstractWhileyFile.EXPR_arrlen;
import static wyc.util.AbstractWhileyFile.EXPR_arrupdt;
import static wyc.util.AbstractWhileyFile.EXPR_bitwiseand;
import static wyc.util.AbstractWhileyFile.EXPR_bitwisenot;
import static wyc.util.AbstractWhileyFile.EXPR_bitwiseor;
import static wyc.util.AbstractWhileyFile.EXPR_bitwiseshl;
import static wyc.util.AbstractWhileyFile.EXPR_bitwiseshr;
import static wyc.util.AbstractWhileyFile.EXPR_bitwisexor;
import static wyc.util.AbstractWhileyFile.EXPR_cast;
import static wyc.util.AbstractWhileyFile.EXPR_const;
import static wyc.util.AbstractWhileyFile.EXPR_deref;
import static wyc.util.AbstractWhileyFile.EXPR_div;
import static wyc.util.AbstractWhileyFile.EXPR_eq;
import static wyc.util.AbstractWhileyFile.EXPR_exists;
import static wyc.util.AbstractWhileyFile.EXPR_forall;
import static wyc.util.AbstractWhileyFile.EXPR_gt;
import static wyc.util.AbstractWhileyFile.EXPR_gteq;
import static wyc.util.AbstractWhileyFile.EXPR_iff;
import static wyc.util.AbstractWhileyFile.EXPR_implies;
import static wyc.util.AbstractWhileyFile.EXPR_indirectinvoke;
import static wyc.util.AbstractWhileyFile.EXPR_invoke;
import static wyc.util.AbstractWhileyFile.EXPR_is;
import static wyc.util.AbstractWhileyFile.EXPR_lambda;
import static wyc.util.AbstractWhileyFile.EXPR_lt;
import static wyc.util.AbstractWhileyFile.EXPR_lteq;
import static wyc.util.AbstractWhileyFile.EXPR_mul;
import static wyc.util.AbstractWhileyFile.EXPR_neg;
import static wyc.util.AbstractWhileyFile.EXPR_neq;
import static wyc.util.AbstractWhileyFile.EXPR_new;
import static wyc.util.AbstractWhileyFile.EXPR_not;
import static wyc.util.AbstractWhileyFile.EXPR_or;
import static wyc.util.AbstractWhileyFile.EXPR_qualifiedinvoke;
import static wyc.util.AbstractWhileyFile.EXPR_qualifiedlambda;
import static wyc.util.AbstractWhileyFile.EXPR_recfield;
import static wyc.util.AbstractWhileyFile.EXPR_recinit;
import static wyc.util.AbstractWhileyFile.EXPR_recupdt;
import static wyc.util.AbstractWhileyFile.EXPR_rem;
import static wyc.util.AbstractWhileyFile.EXPR_staticvar;
import static wyc.util.AbstractWhileyFile.EXPR_sub;
import static wyc.util.AbstractWhileyFile.EXPR_var;
import static wyil.lang.WyilFile.*;

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
public class MoveAnalysis implements Build.Stage<WyilFile> {
	public MoveAnalysis(Build.Task builder) {

	}


	@Override
	public void apply(WyilFile module) {
		for(Declaration d : module.getDeclarations()) {
			check(d);
		}
	}

	private void check(Declaration decl) {
		if(decl instanceof Declaration.Type) {
			check((Declaration.Type) decl);
		} else if(decl instanceof Declaration.FunctionOrMethod) {
			check((Declaration.FunctionOrMethod) decl);
		} else {
			// do nothing?
		}
	}

	private void check(Declaration.Type t) {
		for(Expr e : t.getInvariant()) {
			checkExpression(false,e);
		}
	}
	private void check(Declaration.FunctionOrMethod fm) {
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
		case DECL_variable:
		case DECL_variableinitialiser:
			checkVariableDeclaration((Declaration.Variable) stmt);
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
		checkExpression(false,stmt.getCondition());
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
		checkExpressions(true, stmt.getOperand());
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

	private void checkVariableDeclaration(Declaration.Variable stmt) {
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
		case EXPR_const:
			checkConstant(consumed, (Expr.Constant) expr);
			break;
		case EXPR_var:
			checkVariable(consumed, (Expr.VariableAccess) expr);
			break;
		case EXPR_staticvar:
			checkStaticVariable(consumed, (Expr.StaticVariableAccess) expr);
			break;
		case EXPR_cast:
			checkCast(consumed, (Expr.Cast) expr);
			break;
		case EXPR_qualifiedinvoke:
		case EXPR_invoke:
			checkInvoke(consumed, (Expr.Invoke) expr);
			break;
		case EXPR_indirectinvoke:
			checkIndirectInvoke(consumed, (Expr.IndirectInvoke) expr);
		// Conditions
		case EXPR_not:
		case EXPR_or:
		case EXPR_and:
		case EXPR_iff:
		case EXPR_implies:
			checkLogicalOperator(consumed, (Expr.Operator) expr);
			break;
		case EXPR_forall:
		case EXPR_exists:
			checkQuantifier(consumed, (Expr.Quantifier) expr);
			break;
		case EXPR_is:
			checkIs(consumed, (Expr.Is) expr);
			break;
		// Comparators
		case EXPR_eq:
		case EXPR_neq:
		case EXPR_lt:
		case EXPR_lteq:
		case EXPR_gt:
		case EXPR_gteq:
			checkComparisonOperator(consumed, (Expr.Operator) expr);
			break;
		// Arithmetic Operators
		case EXPR_neg:
		case EXPR_add:
		case EXPR_sub:
		case EXPR_mul:
		case EXPR_div:
		case EXPR_rem:
			checkArithmeticOperator(consumed, (Expr.Operator) expr);
			break;
		// Bitwise expressions
		case EXPR_bitwisenot:
		case EXPR_bitwiseand:
		case EXPR_bitwiseor:
		case EXPR_bitwisexor:
			checkBitwiseOperator(consumed, (Expr.Operator) expr);
			break;
		case EXPR_bitwiseshl:
		case EXPR_bitwiseshr:
			checkBitwiseShift(consumed, (Expr.Operator) expr);
			break;
		// Record Expressions
		case EXPR_recinit:
			checkRecordInitialiser(consumed, (Expr.RecordInitialiser) expr);
			break;
		case EXPR_recfield:
			checkRecordAccess(consumed, (Expr.RecordAccess) expr);
			break;
		case EXPR_recupdt:
			checkRecordUpdate(consumed, (Expr.RecordUpdate) expr);
			break;
			// Array expressions
		case EXPR_arrlen:
			checkArrayLength(consumed, (Expr.ArrayLength) expr);
			break;
		case EXPR_arrinit:
			checkArrayInitialiser(consumed, (Expr.ArrayInitialiser) expr);
			break;
		case EXPR_arrgen:
			checkArrayGenerator(consumed, (Expr.ArrayGenerator) expr);
			break;
		case EXPR_arridx:
			checkArrayAccess(consumed, (Expr.ArrayAccess) expr);
			break;
		case EXPR_arrupdt:
			checkArrayUpdate(consumed, (Expr.ArrayUpdate) expr);
			break;
			// Reference expressions
		case EXPR_deref:
			checkDereference(consumed, (Expr.Dereference) expr);
			break;
		case EXPR_new:
			checkNew(consumed, (Expr.New) expr);
			break;
		case EXPR_lambda:
		case EXPR_qualifiedlambda:
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
		checkExpression(consumed, expr.getCastedExpr());
	}

	public void checkInvoke(boolean consumed, Expr.Invoke expr) {
		checkExpressions(true, expr.getArguments());
	}

	public void checkIndirectInvoke(boolean consumed, Expr.IndirectInvoke expr) {
		checkExpression(false, expr.getSource());
		checkExpressions(true, expr.getArguments());
	}

	public void checkIs(boolean consumed, Expr.Is expr) {
		checkExpression(false, expr.getTestExpr());
	}

	public void checkLogicalOperator(boolean consumed, Expr.Operator expr) {
		for(int i=0;i!=expr.size();++i) {
			checkExpression(false, expr.getOperand(i));
		}
	}

	public void checkQuantifier(boolean consumed, Expr.Quantifier expr) {
		checkExpression(false, expr.getBody());
	}

	public void checkComparisonOperator(boolean consumed, Expr.Operator expr) {
		for(int i=0;i!=expr.size();++i) {
			checkExpression(false, expr.getOperand(i));
		}
	}

	public void checkArithmeticOperator(boolean consumed, Expr.Operator expr) {
		for(int i=0;i!=expr.size();++i) {
			checkExpression(false, expr.getOperand(i));
		}
	}

	public void checkBitwiseOperator(boolean consumed, Expr.Operator expr) {
		for(int i=0;i!=expr.size();++i) {
			checkExpression(false, expr.getOperand(i));
		}
	}

	public void checkBitwiseShift(boolean consumed, Expr.Operator expr) {
		for(int i=0;i!=expr.size();++i) {
			checkExpression(false, expr.getOperand(i));
		}
	}

	public void checkRecordInitialiser(boolean consumed, Expr.RecordInitialiser expr) {
		for(int i=0;i!=expr.size();++i) {
			checkExpression(consumed, expr.getOperand(i).getSecond());
		}
	}

	public void checkRecordAccess(boolean consumed, Expr.RecordAccess expr) {
		checkExpression(false, expr.getSource());
	}

	public void checkRecordUpdate(boolean consumed, Expr.RecordUpdate expr) {
		checkExpression(false, expr.getSource());
		checkExpression(consumed, expr.getValue());
	}

	public void checkArrayLength(boolean consumed, Expr.ArrayLength expr) {
		checkExpression(false, expr.getSource());
	}

	public void checkArrayInitialiser(boolean consumed, Expr.ArrayInitialiser expr) {
		for(int i=0;i!=expr.size();++i) {
			checkExpression(consumed, expr.getOperand(i));
		}
	}

	public void checkArrayGenerator(boolean consumed, Expr.ArrayGenerator expr) {
		checkExpression(false, expr.getLength());
		checkExpression(consumed, expr.getValue());
	}

	public void checkArrayAccess(boolean consumed, Expr.ArrayAccess expr) {
		checkExpression(false, expr.getSource());
		checkExpression(false, expr.getSubscript());
	}

	public void checkArrayUpdate(boolean consumed, Expr.ArrayUpdate expr) {
		checkExpression(false, expr.getSource());
		checkExpression(false, expr.getSubscript());
		checkExpression(consumed, expr.getValue());
	}

	public void checkDereference(boolean consumed, Expr.Dereference expr) {
		checkExpression(false, expr.getOperand());
	}

	public void checkNew(boolean consumed, Expr.New expr) {
		checkExpression(consumed, expr.getOperand());
	}

	public void checkLambdaConstant(boolean consumed, Expr.LambdaAccess expr) {
	}
}
