package wyil.stage;

import java.math.BigInteger;

import wyal.lang.WyalFile;
import wyal.lang.WyalFile.Expr;
import wybs.lang.NameResolver;
import wybs.lang.SyntacticItem;
import wybs.lang.SyntaxError.InternalFailure;
import wybs.util.AbstractCompilationUnit.Tuple;
import wybs.util.AbstractCompilationUnit.Value;
import wyc.util.AbstractConsumer;
import wycc.util.Pair;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Type;
import wyil.stage.VerificationConditionGenerator.Context;
import wyil.stage.VerificationConditionGenerator.VerificationCondition;

/**
 * <p>
 * Generate necessary checks to ensure that expressions are well-formed. The
 * generated checks depend very much on the context in which the expression
 * occurs. A typical example is something like this:
 * </p>
 *
 * <pre>
 * function get(int[] items, int i) -> (int r):
 *   return items[i]
 * </pre>
 *
 * <p>
 * This function should fail verification because <code>i</code> is not known to
 * be within the bounds of <code>items</code>. The preconditions for the
 * expression <code>items[i]</code> are <code>i >= 0</code> and
 * <code>i < |items|</code>.
 * </p>
 * <p>
 * The interesting aspect of this proces is that we do not generate
 * preconditions for expressions used in preconditions, postconditions or loop
 * invariants. For example, we do not need to generate a precondition check for
 * <code>items[i]</code> in this context:
 * </p>
 *
 * <pre>
 * function get(int[] items, int i) -> (int r)
 * requires items[i] >= 0:
 *    //
 *    ...
 * </pre>
 *
 * <p>
 * The reason for this is simply that, if <code>items[i] >= 0</code> holds, then
 * it immediately follows that <code>i >= 0</code> and <code>i < |items</code>.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class PreconditionGenerator {
	private final VerificationConditionGenerator vcg;

	public PreconditionGenerator(VerificationConditionGenerator vcg) {
		this.vcg = vcg;
	}

	public void apply(WyilFile.Expr expr, Context context) {
		AbstractConsumer<Context> visitor = new AbstractConsumer<Context>() {
			@Override
			public void visitLogicalAnd(WyilFile.Expr.LogicalAnd expr, Context context) {
				// In the case of a logical and condition we need to propagate
				// the left-hand side as an assumption into the right-hand side.
				// This is an artifact of short-circuiting whereby terms on the
				// right-hand side only execute when the left-hand side is known
				// to hold.
				Tuple<WyilFile.Expr> operands = expr.getOperands();
				for (int i = 0; i != operands.size(); ++i) {
					super.visitExpression(operands.get(i), context);
					Expr e = vcg.translateExpression(operands.get(i), null, context.getEnvironment());
					context = context.assume(e);
				}
			}

			@Override
			public void visitExistentialQuantifier(WyilFile.Expr.ExistentialQuantifier expr, Context context) {
				visitQuantifier(expr,context);
			}
			@Override
			public void visitUniversalQuantifier(WyilFile.Expr.UniversalQuantifier expr, Context context) {
				visitQuantifier(expr,context);
			}

			public void visitQuantifier(WyilFile.Expr.Quantifier expr, Context context) {
				Tuple<WyilFile.Decl.Variable> parameters = expr.getParameters();
				for(int i=0;i!=parameters.size();++i) {
					Decl.Variable parameter = parameters.get(i);
					WyilFile.Expr.ArrayRange range = (WyilFile.Expr.ArrayRange) parameter.getInitialiser();
					super.visitExpression(range, context);
					// Now generate appropriate bounds for parameter to ensure any subsequently
					// generate precondition checks hold.  For example, consider the expression
					// all { k in 0..|xs| | xs[k] >= 0 }. We don't want xs[k] to generate a
					// precondition check that will fail.
					VerificationConditionGenerator.LocalEnvironment env = context.getEnvironment();
					WyalFile.Expr.VariableAccess var = new WyalFile.Expr.VariableAccess(env.read(parameter));
					Expr start = vcg.translateExpression(range.getFirstOperand(), null, env);
					Expr end = vcg.translateExpression(range.getSecondOperand(), null, env);
					//
					Expr above = new Expr.LessThanOrEqual(start, var);
					Expr below = new Expr.LessThan(var, end);
					context = context.assume(new WyalFile.Expr.LogicalAnd(above,below));
				}
				super.visitExpression(expr.getOperand(), context);
			}


			@Override
			public void visitInvoke(WyilFile.Expr.Invoke expr, Context context) {
				super.visitInvoke(expr, context);
				checkInvokePreconditions(expr, context);
			}

			@Override
			public void visitIntegerDivision(WyilFile.Expr.IntegerDivision expr, Context context) {
				super.visitIntegerDivision(expr, context);
				checkDivideByZero(expr, context);
			}

			@Override
			public void visitIntegerRemainder(WyilFile.Expr.IntegerRemainder expr, Context context) {
				super.visitIntegerRemainder(expr, context);
				checkDivideByZero(expr, context);
			}

			@Override
			public void visitArrayAccess(WyilFile.Expr.ArrayAccess expr, Context context) {
				super.visitArrayAccess(expr, context);
				checkIndexOutOfBounds(expr, context);
			}

			@Override
			public void visitArrayGenerator(WyilFile.Expr.ArrayGenerator expr, Context context) {
				super.visitArrayGenerator(expr, context);
				checkArrayGeneratorLength(expr, context);
			}

			@Override
			public void visitType(WyilFile.Type type, Context context) {
				// NOTE: don't need to visit types.
			}
		};
		visitor.visitExpression(expr, context);
	}

	private void checkInvokePreconditions(WyilFile.Expr.Invoke expr, Context context) {
		try {
			WyilFile.Tuple<Type> parameterTypes = expr.getSignature().getParameters();
			//
			WyilFile.Decl.Callable fmp = vcg.lookupFunctionOrMethodOrProperty(expr.getName(), expr.getSignature(),
					expr);
			if (fmp instanceof WyilFile.Decl.FunctionOrMethod) {
				WyilFile.Decl.FunctionOrMethod fm = (WyilFile.Decl.FunctionOrMethod) fmp;
				int numPreconditions = fm.getRequires().size();
				// There is at least one precondition for the function/method being
				// called. Therefore, we need to generate a verification condition
				// which will check that the precondition holds.
				Pair<Expr[],Context> r = vcg.translateExpressionsWithChecks(expr.getOperands(), context);
				Expr[] arguments = r.first();
				context = r.second();
				String prefix = fm.getName() + "_requires_";
				// Finally, generate an appropriate verification condition to check
				// each precondition clause
				for (int i = 0; i != numPreconditions; ++i) {
					// FIXME: name needs proper path information
					WyalFile.Name name = vcg.convert(fm.getQualifiedName().toNameID().module(), prefix + i, expr);
					Expr clause = new Expr.Invoke(null, name, null, arguments);
					context.emit(new VerificationCondition("precondition not satisfied", context.getAssumptions(),
							clause, expr.getParent(WyilFile.Attribute.Span.class)));
				}
				// Perform parameter checks
				for (int i = 0; i != parameterTypes.size(); ++i) {
					vcg.generateTypeInvariantCheck(parameterTypes.get(i), arguments[i], context);
				}
			}
		} catch (NameResolver.ResolutionError e) {
			// FIXME: this should eventually be unnecessary
			throw new InternalFailure(e.getMessage(), ((WyilFile) expr.getHeap()).getEntry(), expr, e);
		}
	}

	private void checkDivideByZero(WyilFile.Expr.BinaryOperator expr, Context context) {
		Pair<Expr,Context> rhs = vcg.translateExpressionWithChecks(expr.getSecondOperand(), null, context);
		context = rhs.second();
		Value zero = new Value.Int(BigInteger.ZERO);
		Expr.Constant constant = new Expr.Constant(zero);
		Expr neqZero = new Expr.NotEqual(rhs.first(), constant);
		//
		context.emit(new VerificationCondition("division by zero", context.getAssumptions(), neqZero,
				expr.getParent(WyilFile.Attribute.Span.class)));
	}

	private void checkIndexOutOfBounds(WyilFile.Expr.ArrayAccess expr, Context context) {
		Pair<Expr,Context> src = vcg.translateExpressionWithChecks(expr.getFirstOperand(), null, context);
		Pair<Expr,Context> idx = vcg.translateExpressionWithChecks(expr.getSecondOperand(), null, src.second());
		context = idx.second();
		Expr zero = new Expr.Constant(new Value.Int(BigInteger.ZERO));
		Expr length = new Expr.ArrayLength(src.first());
		//
		Expr negTest = new Expr.GreaterThanOrEqual(idx.first(), zero);
		Expr lenTest = new Expr.LessThan(idx.first(), length);
		//
		context.emit(new VerificationCondition("index out of bounds (negative)", context.getAssumptions(), negTest,
				expr.getParent(WyilFile.Attribute.Span.class)));
		context.emit(new VerificationCondition("index out of bounds (not less than length)", context.getAssumptions(),
				lenTest, expr.getParent(WyilFile.Attribute.Span.class)));
	}

	private void checkArrayGeneratorLength(WyilFile.Expr.ArrayGenerator expr, Context context) {
		Pair<Expr,Context> len = vcg.translateExpressionWithChecks(expr.getSecondOperand(), null, context);
		context = len.second();
		Value zero = new Value.Int(BigInteger.ZERO);
		Expr.Constant constant = new Expr.Constant(zero);
		Expr neqZero = new Expr.GreaterThanOrEqual(len.first(), constant);
		//
		context.emit(new VerificationCondition("negative length possible", context.getAssumptions(), neqZero,
				expr.getParent(WyilFile.Attribute.Span.class)));
	}
}
