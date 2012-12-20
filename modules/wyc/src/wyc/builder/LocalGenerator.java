package wyc.builder;

import static wyc.lang.WhileyFile.*;
import static wyil.util.ErrorMessages.INVALID_BINARY_EXPRESSION;
import static wyil.util.ErrorMessages.INVALID_BOOLEAN_EXPRESSION;
import static wyil.util.ErrorMessages.INVALID_SET_OR_LIST_EXPRESSION;
import static wyil.util.ErrorMessages.UNKNOWN_VARIABLE;
import static wyil.util.ErrorMessages.VARIABLE_POSSIBLY_UNITIALISED;
import static wyil.util.ErrorMessages.errorMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import wybs.lang.Path;
import wybs.lang.SyntacticElement;
import wybs.lang.SyntaxError;
import wybs.util.ResolveError;
import wyc.lang.Expr;
import wyc.lang.Exprs;
import wyc.lang.UnresolvedType;
import wyc.lang.WhileyFile;
import wyc.lang.WhileyFile.Context;
import wyil.lang.Attribute;
import wyil.lang.Block;
import wyil.lang.Code;
import wyil.lang.Modifier;
import wyil.lang.NameID;
import wyil.lang.Type;
import wyil.lang.Constant;
import wyil.lang.WyilFile;
import wyil.util.Pair;
import wyil.util.Triple;

/**
 * <p>
 * Responsible for compiling source-level expressions into wyil bytecodes in a
 * given context. This includes generating wyil code for constraints as
 * necessary using a global generator. For example:
 * </p>
 * 
 * <pre>
 * define natlist as [nat]
 * 
 * natlist check([int] ls):
 *     if ls is natlist:
 *         return ls
 *     else:
 *         return []
 * </pre>
 * <p>
 * Here, a local generator will be called to compile the expression
 * <code>ls is natlist</code> into wyil bytecode (amongst other expressions). To
 * this, it must in turn obtain the bytecodes for the type <code>natlist</code>.
 * Since <code>natlist</code> is defined at the global level, the global
 * generator will be called to do this (which, in turn, may call a local
 * generator again).
 * </p>
 * <p>
 * <b>NOTE:</b> it is currently assumed that all expressions being generated are
 * already typed. This restriction may be lifted in the future.
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public final class LocalGenerator {
	private final GlobalGenerator global;
	private final Context context;
	private final ArrayList<WyilFile.MethodDeclaration> lambdas = new ArrayList<WyilFile.MethodDeclaration>();

	public LocalGenerator(GlobalGenerator global, Context context) {
		this.context = context;
		this.global = global;
	}

	public Context context() {
		return context;
	}

	public List<WyilFile.MethodDeclaration> lambdas() {
		return lambdas;
	}
	
	/**
	 * Translate a source-level assertion into a wyil block, using a given
	 * environment mapping named variables to slots. If the condition evaluates
	 * to true, then control continues as normal. Otherwise, an assertion
	 * failure is raised with the given message.
	 * 
	 * @param message
	 *            --- Message to report if condition is false.
	 * @param condition
	 *            --- Source-level condition to be translated
	 * @param isAssumption
	 *            --- indicates whether to generate an assumption or an
	 *            assertion.
	 * @param freeRegister
	 *            --- All registers with and index equal or higher than this are
	 *            available for use as temporary storage.
	 * @param environment
	 *            --- Mapping from variable names to to register indices.
	 * @return
	 */
	public void generateAssertion(String message, Expr condition,
			boolean isAssumption, Environment environment, Block codes) {
		try {
			if (condition instanceof Expr.BinOp) {
				generateAssertion(message, (Expr.BinOp) condition,
						isAssumption, environment, codes);
			} else if (condition instanceof Expr.Constant
					|| condition instanceof Expr.ConstantAccess
					|| condition instanceof Expr.LocalVariable
					|| condition instanceof Expr.UnOp
					|| condition instanceof Expr.AbstractInvoke
					|| condition instanceof Expr.RecordAccess
					|| condition instanceof Expr.IndexOf
					|| condition instanceof Expr.Comprehension) {

				// The default case simply compares the computed value against
				// true. In some cases, we could do better. For example, !(x <
				// 5)
				// could be rewritten into x>=5.

				int r1 = generate(condition, environment, codes);
				int r2 = environment.allocate(Type.T_BOOL);
				codes.append(Code.Const(r2, Constant.V_BOOL(true)),
						attributes(condition));
				if (isAssumption) {
					codes.append(Code.Assume(Type.T_BOOL, r1, r2,
							Code.Comparator.EQ, message), attributes(condition));
				} else {
					codes.append(Code.Assert(Type.T_BOOL, r1, r2,
							Code.Comparator.EQ, message), attributes(condition));
				}
			} else {
				syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context,
						condition);
			}

		} catch (SyntaxError se) {
			throw se;
		} catch (Exception ex) {
			internalFailure(ex.getMessage(), context, condition, ex);
		}
	}

	protected void generateAssertion(String message, Expr.BinOp v,
			boolean isAssumption, Environment environment, Block codes) {
		Expr.BOp bop = v.op;

		if (bop == Expr.BOp.OR) {
			String lab = Block.freshLabel();
			generateCondition(lab, v.lhs, environment, codes);
			generateAssertion(message, v.rhs, isAssumption, environment, codes);
			codes.append(Code.Label(lab));
		} else if (bop == Expr.BOp.AND) {
			generateAssertion(message, v.lhs, isAssumption, environment, codes);
			generateAssertion(message, v.rhs, isAssumption, environment, codes);
		} else {

			// TODO: there are some cases which will break here. In particular,
			// those involving type tests. If/When WYIL changes to be register
			// based
			// this should fall out in the wash.

			Code.Comparator cop = OP2COP(bop, v);

			int r1 = generate(v.lhs, environment, codes);
			int r2 = generate(v.rhs, environment, codes);
			if (isAssumption) {
				codes.append(
						Code.Assume(v.srcType.raw(), r1, r2, cop, message),
						attributes(v));
			} else {
				codes.append(
						Code.Assert(v.srcType.raw(), r1, r2, cop, message),
						attributes(v));
			}

		}
	}

	/**
	 * Translate a source-level condition into a wyil block, using a given
	 * environment mapping named variables to slots. If the condition evaluates
	 * to true, then control is transferred to the given target. Otherwise,
	 * control will fall through to the following bytecode.
	 * 
	 * @param target
	 *            --- Target label to goto if condition is true.
	 * @param condition
	 *            --- Source-level condition to be translated
	 * @param environment
	 *            --- Mapping from variable names to to slot numbers.
	 * @param codes
	 *            --- List of bytecodes onto which translation should be
	 *            appended.
	 * @return
	 */
	public void generateCondition(String target, Expr condition,
			Environment environment, Block codes) {
		try {
			if (condition instanceof Expr.Constant) {
				generateCondition(target, (Expr.Constant) condition,
						environment, codes);
			} else if (condition instanceof Expr.UnOp) {
				generateCondition(target, (Expr.UnOp) condition, environment,
						codes);
			} else if (condition instanceof Expr.BinOp) {
				generateCondition(target, (Expr.BinOp) condition, environment,
						codes);
			} else if (condition instanceof Expr.Comprehension) {
				generateCondition(target, (Expr.Comprehension) condition,
						environment, codes);
			} else if (condition instanceof Expr.ConstantAccess
					|| condition instanceof Expr.LocalVariable
					|| condition instanceof Expr.AbstractInvoke
					|| condition instanceof Expr.AbstractIndirectInvoke
					|| condition instanceof Expr.RecordAccess
					|| condition instanceof Expr.IndexOf) {

				// The default case simply compares the computed value against
				// true. In some cases, we could do better. For example, !(x <
				// 5)
				// could be rewritten into x>=5.

				int r1 = generate(condition, environment, codes);
				int r2 = environment.allocate(Type.T_BOOL);
				codes.append(Code.Const(r2, Constant.V_BOOL(true)),
						attributes(condition));
				codes.append(Code.If(Type.T_BOOL, r1, r2, Code.Comparator.EQ,
						target), attributes(condition));

			} else {
				syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context,
						condition);
			}

		} catch (SyntaxError se) {
			throw se;
		} catch (Exception ex) {
			internalFailure(ex.getMessage(), context, condition, ex);
		}

	}

	private void generateCondition(String target, Expr.Constant c,
			Environment environment, Block codes) {
		Constant.Bool b = (Constant.Bool) c.value;
		if (b.value) {
			codes.append(Code.Goto(target));
		} else {
			// do nout
		}
	}

	private void generateCondition(String target, Expr.BinOp v,
			Environment environment, Block codes) throws Exception {

		Expr.BOp bop = v.op;

		if (bop == Expr.BOp.OR) {
			generateCondition(target, v.lhs, environment, codes);
			generateCondition(target, v.rhs, environment, codes);

		} else if (bop == Expr.BOp.AND) {
			String exitLabel = Block.freshLabel();
			generateCondition(exitLabel, invert(v.lhs), environment, codes);
			generateCondition(target, v.rhs, environment, codes);
			codes.append(Code.Label(exitLabel));

		} else if (bop == Expr.BOp.IS) {
			generateTypeCondition(target, v, environment, codes);

		} else {

			Code.Comparator cop = OP2COP(bop, v);

			if (cop == Code.Comparator.EQ
					&& v.lhs instanceof Expr.LocalVariable
					&& v.rhs instanceof Expr.Constant
					&& ((Expr.Constant) v.rhs).value == Constant.V_NULL) {
				// this is a simple rewrite to enable type inference.
				Expr.LocalVariable lhs = (Expr.LocalVariable) v.lhs;
				if (environment.get(lhs.var) == null) {
					syntaxError(errorMessage(UNKNOWN_VARIABLE), context, v.lhs);
				}
				int slot = environment.get(lhs.var);
				codes.append(
						Code.IfIs(v.srcType.raw(), slot, Type.T_NULL, target),
						attributes(v));
			} else if (cop == Code.Comparator.NEQ
					&& v.lhs instanceof Expr.LocalVariable
					&& v.rhs instanceof Expr.Constant
					&& ((Expr.Constant) v.rhs).value == Constant.V_NULL) {
				// this is a simple rewrite to enable type inference.
				String exitLabel = Block.freshLabel();
				Expr.LocalVariable lhs = (Expr.LocalVariable) v.lhs;
				if (environment.get(lhs.var) == null) {
					syntaxError(errorMessage(UNKNOWN_VARIABLE), context, v.lhs);
				}
				int slot = environment.get(lhs.var);
				codes.append(Code.IfIs(v.srcType.raw(), slot, Type.T_NULL,
						exitLabel), attributes(v));
				codes.append(Code.Goto(target));
				codes.append(Code.Label(exitLabel));
			} else {
				int lhs = generate(v.lhs, environment, codes);
				int rhs = generate(v.rhs, environment, codes);
				codes.append(Code.If(v.srcType.raw(), lhs, rhs, cop, target),
						attributes(v));
			}
		}
	}

	private void generateTypeCondition(String target, Expr.BinOp v,
			Environment environment, Block codes) throws Exception {
		int leftOperand;

		if (v.lhs instanceof Expr.LocalVariable) {
			Expr.LocalVariable lhs = (Expr.LocalVariable) v.lhs;
			if (environment.get(lhs.var) == null) {
				syntaxError(errorMessage(UNKNOWN_VARIABLE), context, v.lhs);
			}
			leftOperand = environment.get(lhs.var);
		} else {
			leftOperand = generate(v.lhs, environment, codes);
		}

		Expr.TypeVal rhs = (Expr.TypeVal) v.rhs;
		Block constraint = global.generate(rhs.unresolvedType, context);
		if (constraint != null) {
			String exitLabel = Block.freshLabel();
			Type glb = Type.intersect(v.srcType.raw(),
					Type.Negation(rhs.type.raw()));

			if (glb != Type.T_VOID) {
				// Only put the actual type test in if it is necessary.
				String nextLabel = Block.freshLabel();

				// FIXME: should be able to just test the glb here and branch to
				// exit label directly. However, this currently doesn't work
				// because of limitations with intersection of open records.

				codes.append(Code.IfIs(v.srcType.raw(), leftOperand,
						rhs.type.raw(), nextLabel), attributes(v));
				codes.append(Code.Goto(exitLabel));
				codes.append(Code.Label(nextLabel));
			}
			constraint = shiftBlockExceptionZero(environment.size() - 1,
					leftOperand, constraint);
			codes.append(chainBlock(exitLabel, constraint));
			codes.append(Code.Goto(target));
			codes.append(Code.Label(exitLabel));
		} else {
			codes.append(Code.IfIs(v.srcType.raw(), leftOperand,
					rhs.type.raw(), target), attributes(v));
		}
	}

	private void generateCondition(String target, Expr.UnOp v,
			Environment environment, Block codes) {
		Expr.UOp uop = v.op;
		switch (uop) {
		case NOT:
			String label = Block.freshLabel();
			generateCondition(label, v.mhs, environment, codes);
			codes.append(Code.Goto(target));
			codes.append(Code.Label(label));
			return;
		}
		syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context, v);
	}

	private void generateCondition(String target, Expr.Comprehension e,
			Environment environment, Block codes) {
		if (e.cop != Expr.COp.NONE && e.cop != Expr.COp.SOME) {
			syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context, e);
		}

		ArrayList<Triple<Integer, Integer, Type.EffectiveCollection>> slots = new ArrayList();

		for (Pair<String, Expr> src : e.sources) {
			Nominal.EffectiveCollection srcType = (Nominal.EffectiveCollection) src
					.second().result();
			int srcSlot;
			int varSlot = environment.allocate(srcType.raw().element(),
					src.first());

			if (src.second() instanceof Expr.LocalVariable) {
				// this is a little optimisation to produce slightly better
				// code.
				Expr.LocalVariable v = (Expr.LocalVariable) src.second();
				if (environment.get(v.var) != null) {
					srcSlot = environment.get(v.var);
				} else {
					// fall-back plan ...
					srcSlot = generate(src.second(), environment, codes);
				}
			} else {
				srcSlot = generate(src.second(), environment, codes);
			}
			slots.add(new Triple(varSlot, srcSlot, srcType.raw()));
		}

		ArrayList<String> labels = new ArrayList<String>();
		String loopLabel = Block.freshLabel();

		for (Triple<Integer, Integer, Type.EffectiveCollection> p : slots) {
			Type.EffectiveCollection srcType = p.third();
			String lab = loopLabel + "$" + p.first();
			codes.append(Code.ForAll(srcType, p.second(), p.first(),
					Collections.EMPTY_LIST, lab), attributes(e));
			labels.add(lab);
		}

		if (e.cop == Expr.COp.NONE) {
			String exitLabel = Block.freshLabel();
			generateCondition(exitLabel, e.condition, environment, codes);
			for (int i = (labels.size() - 1); i >= 0; --i) {
				codes.append(Code.LoopEnd(labels.get(i)));
			}
			codes.append(Code.Goto(target));
			codes.append(Code.Label(exitLabel));
		} else { // SOME
			generateCondition(target, e.condition, environment, codes);
			for (int i = (labels.size() - 1); i >= 0; --i) {
				codes.append(Code.LoopEnd(labels.get(i)));
			}
		} // ALL, LONE and ONE will be harder
	}

	/**
	 * Translate a source-level expression into a WYIL bytecode block, using a
	 * given environment mapping named variables to registers. The result of the
	 * expression is stored in a given target register.
	 * 
	 * @param expression
	 *            --- Source-level expression to be translated
	 * @param environment
	 *            --- Mapping from variable names to to slot numbers.
	 * @param codes
	 *            --- List of bytecodes onto which translation should be
	 *            appended.
	 * 
	 * @return --- the register
	 */
	public int generate(Expr expression, Environment environment, Block codes) {
		try {
			if (expression instanceof Expr.Constant) {
				return generate((Expr.Constant) expression, environment, codes);
			} else if (expression instanceof Expr.LocalVariable) {
				return generate((Expr.LocalVariable) expression, environment,
						codes);
			} else if (expression instanceof Expr.ConstantAccess) {
				return generate((Expr.ConstantAccess) expression, environment,
						codes);
			} else if (expression instanceof Expr.Set) {
				return generate((Expr.Set) expression, environment, codes);
			} else if (expression instanceof Expr.List) {
				return generate((Expr.List) expression, environment, codes);
			} else if (expression instanceof Expr.SubList) {
				return generate((Expr.SubList) expression, environment, codes);
			} else if (expression instanceof Expr.SubString) {
				return generate((Expr.SubString) expression, environment, codes);
			} else if (expression instanceof Expr.BinOp) {
				return generate((Expr.BinOp) expression, environment, codes);
			} else if (expression instanceof Expr.LengthOf) {
				return generate((Expr.LengthOf) expression, environment, codes);
			} else if (expression instanceof Expr.Dereference) {
				return generate((Expr.Dereference) expression, environment,
						codes);
			} else if (expression instanceof Expr.Convert) {
				return generate((Expr.Convert) expression, environment, codes);
			} else if (expression instanceof Expr.IndexOf) {
				return generate((Expr.IndexOf) expression, environment, codes);
			} else if (expression instanceof Expr.UnOp) {
				return generate((Expr.UnOp) expression, environment, codes);
			} else if (expression instanceof Expr.FunctionCall) {
				return generate((Expr.FunctionCall) expression, environment,
						codes);
			} else if (expression instanceof Expr.MethodCall) {
				return generate((Expr.MethodCall) expression, environment,
						codes);
			} else if (expression instanceof Expr.IndirectFunctionCall) {
				return generate((Expr.IndirectFunctionCall) expression,
						environment, codes);
			} else if (expression instanceof Expr.IndirectMethodCall) {
				return generate((Expr.IndirectMethodCall) expression,
						environment, codes);
			} else if (expression instanceof Expr.Comprehension) {
				return generate((Expr.Comprehension) expression, environment,
						codes);
			} else if (expression instanceof Expr.RecordAccess) {
				return generate((Expr.RecordAccess) expression, environment,
						codes);
			} else if (expression instanceof Expr.Record) {
				return generate((Expr.Record) expression, environment, codes);
			} else if (expression instanceof Expr.Tuple) {
				return generate((Expr.Tuple) expression, environment, codes);
			} else if (expression instanceof Expr.Map) {
				return generate((Expr.Map) expression, environment, codes);
			} else if (expression instanceof Expr.FunctionOrMethod) {
				return generate((Expr.FunctionOrMethod) expression,
						environment, codes);
			} else if (expression instanceof Expr.Lambda) {
				return generate((Expr.Lambda) expression, environment, codes);
			} else if (expression instanceof Expr.New) {
				return generate((Expr.New) expression, environment, codes);
			} else {
				// should be dead-code
				internalFailure("unknown expression: "
						+ expression.getClass().getName(), context, expression);
			}
		} catch (ResolveError rex) {
			syntaxError(rex.getMessage(), context, expression, rex);
		} catch (SyntaxError se) {
			throw se;
		} catch (Exception ex) {
			internalFailure(ex.getMessage(), context, expression, ex);
		}

		return -1; // deadcode
	}

	public int generate(Expr.MethodCall expr, Environment environment,
			Block codes) throws ResolveError {
		int target = environment.allocate(expr.result().raw());
		generate(expr, target, environment, codes);
		return target;
	}

	public void generate(Expr.MethodCall expr, int target,
			Environment environment, Block codes) throws ResolveError {
		int[] operands = generate(expr.arguments, environment, codes);
		codes.append(Code.Invoke(expr.methodType.raw(), target, operands,
				expr.nid()), attributes(expr));
	}

	public int generate(Expr.FunctionCall expr, Environment environment,
			Block codes) throws ResolveError {
		int target = environment.allocate(expr.result().raw());
		generate(expr, target, environment, codes);
		return target;
	}

	public void generate(Expr.FunctionCall expr, int target,
			Environment environment, Block codes) throws ResolveError {
		int[] operands = generate(expr.arguments, environment, codes);
		codes.append(
				Code.Invoke(expr.functionType.raw(), target, operands,
						expr.nid()), attributes(expr));
	}

	public int generate(Expr.IndirectFunctionCall expr,
			Environment environment, Block codes) throws ResolveError {
		int target = environment.allocate(expr.result().raw());
		generate(expr, target, environment, codes);
		return target;
	}

	public void generate(Expr.IndirectFunctionCall expr, int target,
			Environment environment, Block codes) throws ResolveError {
		int operand = generate(expr.src, environment, codes);
		int[] operands = generate(expr.arguments, environment, codes);
		codes.append(Code.IndirectInvoke(expr.functionType.raw(), target,
				operand, operands), attributes(expr));
	}

	public int generate(Expr.IndirectMethodCall expr, Environment environment,
			Block codes) throws ResolveError {
		int target = environment.allocate(expr.result().raw());
		generate(expr, target, environment, codes);
		return target;
	}

	public void generate(Expr.IndirectMethodCall expr, int target,
			Environment environment, Block codes) throws ResolveError {
		int operand = generate(expr.src, environment, codes);
		int[] operands = generate(expr.arguments, environment, codes);
		codes.append(Code.IndirectInvoke(expr.methodType.raw(), target,
				operand, operands), attributes(expr));
	}

	private int generate(Expr.Constant expr, Environment environment,
			Block codes) {
		Constant val = expr.value;
		int target = environment.allocate(val.type());
		codes.append(Code.Const(target, expr.value), attributes(expr));
		return target;
	}

	private int generate(Expr.FunctionOrMethod expr, Environment environment,
			Block codes) {
		Type.FunctionOrMethod type = expr.type.raw();
		int target = environment.allocate(type);
		codes.append(
				Code.Lambda(type, target, Collections.EMPTY_LIST, expr.nid),
				attributes(expr));
		return target;
	}

	private int generate(Expr.Lambda expr, Environment environment, Block codes) {
		Type.FunctionOrMethod tfm = expr.type.raw();
		List<Type> tfm_params = tfm.params();
		List<WhileyFile.Parameter> expr_params = expr.parameters;
		
		// Create environment for the lambda body.
		ArrayList<Integer> operands = new ArrayList<Integer>();
		ArrayList<Type> paramTypes = new ArrayList<Type>();
		Environment benv = new Environment();
		for (int i = 0; i != tfm_params.size(); ++i) {
			Type type = tfm_params.get(i);
			benv.allocate(type, expr_params.get(i).name);
			paramTypes.add(type);
			operands.add(Code.NULL_REG);
		}
		for(Pair<Type,String> v : Exprs.uses(expr.body,context)) {
			if(benv.get(v.second()) == null) {
				Type type = v.first();
				benv.allocate(type,v.second());
				paramTypes.add(type);
				operands.add(environment.get(v.second()));
			}
		}

		// Generate body based on current environment
		Block body = new Block(expr_params.size());
		if(tfm.ret() != Type.T_VOID) {
			int target = generate(expr.body, benv, body);
			body.append(Code.Return(tfm.ret(), target), attributes(expr));		
		} else {
			body.append(Code.Return(), attributes(expr));
		}
		
		// Create concrete type for private lambda function
		Type.FunctionOrMethod cfm;
		if(tfm instanceof Type.Function) {
			cfm = Type.Function(tfm.ret(),tfm.throwsClause(),paramTypes);
		} else {
			cfm = Type.Method(tfm.ret(),tfm.throwsClause(),paramTypes);
		}
				
		// Construct private lambda function using generated body
		int id = lambdas.size();				
		String name = "$lambda" + id;
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.PRIVATE);
		ArrayList<WyilFile.Case> cases = new ArrayList<WyilFile.Case>();
		cases.add(new WyilFile.Case(body, null, null, Collections.EMPTY_LIST,
				attributes(expr)));
		WyilFile.MethodDeclaration lambda = new WyilFile.MethodDeclaration(
				modifiers, name, cfm, cases, attributes(expr));
		lambdas.add(lambda);
		Path.ID mid = context.file().module;
		NameID nid = new NameID(mid, name);
		
		// Finally, create the lambda
		int target = environment.allocate(tfm);
		codes.append(
				Code.Lambda(cfm, target, operands, nid),
				attributes(expr));
		return target;
	}

	private int generate(Expr.ConstantAccess expr, Environment environment,
			Block codes) throws ResolveError {
		Constant val = expr.value;
		int target = environment.allocate(val.type());
		codes.append(Code.Const(target, val), attributes(expr));
		return target;
	}

	private int generate(Expr.LocalVariable expr, Environment environment,
			Block codes) throws ResolveError {

		if (environment.get(expr.var) != null) {
			Type type = expr.result().raw();
			int operand = environment.get(expr.var);
			int target = environment.allocate(type);
			codes.append(Code.Assign(type, target, operand), attributes(expr));
			return target;
		} else {
			syntaxError(errorMessage(VARIABLE_POSSIBLY_UNITIALISED), context,
					expr);
			return -1;
		}
	}

	private int generate(Expr.UnOp expr, Environment environment, Block codes) {
		int operand = generate(expr.mhs, environment, codes);
		int target = environment.allocate(expr.result().raw());
		switch (expr.op) {
		case NEG:
			codes.append(Code.UnArithOp(expr.result().raw(), target, operand,
					Code.UnArithKind.NEG), attributes(expr));
			break;
		case INVERT:
			codes.append(Code.Invert(expr.result().raw(), target, operand),
					attributes(expr));
			break;
		case NOT:
			String falseLabel = Block.freshLabel();
			String exitLabel = Block.freshLabel();
			generateCondition(falseLabel, expr.mhs, environment, codes);
			codes.append(Code.Const(target, Constant.V_BOOL(true)),
					attributes(expr));
			codes.append(Code.Goto(exitLabel));
			codes.append(Code.Label(falseLabel));
			codes.append(Code.Const(target, Constant.V_BOOL(false)),
					attributes(expr));
			codes.append(Code.Label(exitLabel));
			break;
		default:
			// should be dead-code
			internalFailure("unexpected unary operator encountered", context,
					expr);
			return -1;
		}
		return target;
	}

	private int generate(Expr.LengthOf expr, Environment environment,
			Block codes) {
		int operand = generate(expr.src, environment, codes);
		int target = environment.allocate(expr.result().raw());
		codes.append(Code.LengthOf(expr.srcType.raw(), target, operand),
				attributes(expr));
		return target;
	}

	private int generate(Expr.Dereference expr, Environment environment,
			Block codes) {
		int operand = generate(expr.src, environment, codes);
		int target = environment.allocate(expr.result().raw());
		codes.append(Code.Dereference(expr.srcType.raw(), target, operand),
				attributes(expr));
		return target;
	}

	private int generate(Expr.IndexOf expr, Environment environment, Block codes) {
		int srcOperand = generate(expr.src, environment, codes);
		int idxOperand = generate(expr.index, environment, codes);
		int target = environment.allocate(expr.result().raw());
		codes.append(Code.IndexOf(expr.srcType.raw(), target, srcOperand,
				idxOperand), attributes(expr));
		return target;
	}

	private int generate(Expr.Convert expr, Environment environment, Block codes) {
		int operand = generate(expr.expr, environment, codes);
		Type from = expr.expr.result().raw();
		Type to = expr.result().raw();
		int target = environment.allocate(to);
		// TODO: include constraints
		codes.append(Code.Convert(from, target, operand, to), attributes(expr));
		return target;
	}

	private int generate(Expr.BinOp v, Environment environment, Block codes)
			throws Exception {

		// could probably use a range test for this somehow
		if (v.op == Expr.BOp.EQ || v.op == Expr.BOp.NEQ || v.op == Expr.BOp.LT
				|| v.op == Expr.BOp.LTEQ || v.op == Expr.BOp.GT
				|| v.op == Expr.BOp.GTEQ || v.op == Expr.BOp.SUBSET
				|| v.op == Expr.BOp.SUBSETEQ || v.op == Expr.BOp.ELEMENTOF
				|| v.op == Expr.BOp.AND || v.op == Expr.BOp.OR) {
			String trueLabel = Block.freshLabel();
			String exitLabel = Block.freshLabel();
			generateCondition(trueLabel, v, environment, codes);
			int target = environment.allocate(Type.T_BOOL);
			codes.append(Code.Const(target, Constant.V_BOOL(false)),
					attributes(v));
			codes.append(Code.Goto(exitLabel));
			codes.append(Code.Label(trueLabel));
			codes.append(Code.Const(target, Constant.V_BOOL(true)),
					attributes(v));
			codes.append(Code.Label(exitLabel));
			return target;

		} else {

			Expr.BOp bop = v.op;
			int leftOperand = generate(v.lhs, environment, codes);
			int rightOperand = generate(v.rhs, environment, codes);
			Type result = v.result().raw();
			int target = environment.allocate(result);

			switch (bop) {
			case UNION:
				codes.append(Code.BinSetOp((Type.EffectiveSet) result, target,
						leftOperand, rightOperand, Code.BinSetKind.UNION),
						attributes(v));
				break;

			case INTERSECTION:
				codes.append(Code
						.BinSetOp((Type.EffectiveSet) result, target,
								leftOperand, rightOperand,
								Code.BinSetKind.INTERSECTION), attributes(v));
				break;

			case DIFFERENCE:
				codes.append(Code.BinSetOp((Type.EffectiveSet) result, target,
						leftOperand, rightOperand, Code.BinSetKind.DIFFERENCE),
						attributes(v));
				break;

			case LISTAPPEND:
				codes.append(Code.BinListOp((Type.EffectiveList) result,
						target, leftOperand, rightOperand,
						Code.BinListKind.APPEND), attributes(v));
				break;

			case STRINGAPPEND:
				Type lhs = v.lhs.result().raw();
				Type rhs = v.rhs.result().raw();
				Code.BinStringKind op;
				if (lhs == Type.T_STRING && rhs == Type.T_STRING) {
					op = Code.BinStringKind.APPEND;
				} else if (lhs == Type.T_STRING
						&& Type.isSubtype(Type.T_CHAR, rhs)) {
					op = Code.BinStringKind.LEFT_APPEND;
				} else if (rhs == Type.T_STRING
						&& Type.isSubtype(Type.T_CHAR, lhs)) {
					op = Code.BinStringKind.RIGHT_APPEND;
				} else {
					// this indicates that one operand must be explicitly
					// converted
					// into a string.
					op = Code.BinStringKind.APPEND;
				}
				codes.append(
						Code.BinStringOp(target, leftOperand, rightOperand, op),
						attributes(v));
				break;

			default:
				codes.append(Code.BinArithOp(result, target, leftOperand,
						rightOperand, OP2BOP(bop, v)), attributes(v));
			}

			return target;
		}
	}

	private int generate(Expr.Set expr, Environment environment, Block codes) {
		int[] operands = generate(expr.arguments, environment, codes);
		int target = environment.allocate(expr.result().raw());
		codes.append(Code.NewSet(expr.type.raw(), target, operands),
				attributes(expr));
		return target;
	}

	private int generate(Expr.List expr, Environment environment, Block codes) {
		int[] operands = generate(expr.arguments, environment, codes);
		int target = environment.allocate(expr.result().raw());
		codes.append(Code.NewList(expr.type.raw(), target, operands),
				attributes(expr));
		return target;
	}

	private int generate(Expr.SubList expr, Environment environment, Block codes) {
		int srcOperand = generate(expr.src, environment, codes);
		int startOperand = generate(expr.start, environment, codes);
		int endOperand = generate(expr.end, environment, codes);
		int target = environment.allocate(expr.result().raw());
		codes.append(Code.SubList(expr.type.raw(), target, srcOperand,
				startOperand, endOperand), attributes(expr));
		return target;
	}

	private int generate(Expr.SubString v, Environment environment, Block codes) {
		int srcOperand = generate(v.src, environment, codes);
		int startOperand = generate(v.start, environment, codes);
		int endOperand = generate(v.end, environment, codes);
		int target = environment.allocate(v.result().raw());
		codes.append(
				Code.SubString(target, srcOperand, startOperand, endOperand),
				attributes(v));
		return target;
	}

	private int generate(Expr.Comprehension e, Environment environment,
			Block codes) {

		// First, check for boolean cases which are handled mostly by
		// generateCondition.
		if (e.cop == Expr.COp.SOME || e.cop == Expr.COp.NONE) {
			String trueLabel = Block.freshLabel();
			String exitLabel = Block.freshLabel();
			generateCondition(trueLabel, e, environment, codes);
			int target = environment.allocate(Type.T_BOOL);
			codes.append(Code.Const(target, Constant.V_BOOL(false)),
					attributes(e));
			codes.append(Code.Goto(exitLabel));
			codes.append(Code.Label(trueLabel));
			codes.append(Code.Const(target, Constant.V_BOOL(true)),
					attributes(e));
			codes.append(Code.Label(exitLabel));
			return target;
		} else {

			// Ok, non-boolean case.
			ArrayList<Triple<Integer, Integer, Type.EffectiveCollection>> slots = new ArrayList();

			for (Pair<String, Expr> p : e.sources) {
				Expr src = p.second();
				Type.EffectiveCollection rawSrcType = (Type.EffectiveCollection) src
						.result().raw();
				int varSlot = environment.allocate(rawSrcType.element(),
						p.first());
				int srcSlot;

				if (src instanceof Expr.LocalVariable) {
					// this is a little optimisation to produce slightly better
					// code.
					Expr.LocalVariable v = (Expr.LocalVariable) src;
					if (environment.get(v.var) != null) {
						srcSlot = environment.get(v.var);
					} else {
						// fall-back plan ...
						srcSlot = generate(src, environment, codes);
					}
				} else {
					srcSlot = generate(src, environment, codes);
				}
				slots.add(new Triple(varSlot, srcSlot, rawSrcType));
			}

			Type resultType;
			int target = environment.allocate(e.result().raw());

			if (e.cop == Expr.COp.LISTCOMP) {
				resultType = e.type.raw();
				codes.append(Code.NewList((Type.List) resultType, target,
						Collections.EMPTY_LIST), attributes(e));
			} else {
				resultType = e.type.raw();
				codes.append(Code.NewSet((Type.Set) resultType, target,
						Collections.EMPTY_LIST), attributes(e));
			}

			// At this point, it would be good to determine an appropriate loop
			// invariant for a set comprehension. This is easy enough in the
			// case of
			// a single variable comprehension, but actually rather difficult
			// for a
			// multi-variable comprehension.
			//
			// For example, consider <code>{x+y | x in xs, y in ys, x<0 &&
			// y<0}</code>
			//
			// What is an appropriate loop invariant here?

			String continueLabel = Block.freshLabel();
			ArrayList<String> labels = new ArrayList<String>();
			String loopLabel = Block.freshLabel();

			for (Triple<Integer, Integer, Type.EffectiveCollection> p : slots) {
				String label = loopLabel + "$" + p.first();
				codes.append(Code.ForAll(p.third(), p.second(), p.first(),
						Collections.EMPTY_LIST, label), attributes(e));
				labels.add(label);
			}

			if (e.condition != null) {
				generateCondition(continueLabel, invert(e.condition),
						environment, codes);
			}

			int operand = generate(e.value, environment, codes);

			// FIXME: following broken for list comprehensions
			codes.append(Code.BinSetOp((Type.Set) resultType, target, target,
					operand, Code.BinSetKind.LEFT_UNION), attributes(e));

			if (e.condition != null) {
				codes.append(Code.Label(continueLabel));
			}

			for (int i = (labels.size() - 1); i >= 0; --i) {
				codes.append(Code.LoopEnd(labels.get(i)));
			}

			return target;
		}
	}

	private int generate(Expr.Record expr, Environment environment, Block codes) {
		ArrayList<String> keys = new ArrayList<String>(expr.fields.keySet());
		Collections.sort(keys);
		int[] operands = new int[expr.fields.size()];
		for (int i = 0; i != operands.length; ++i) {
			String key = keys.get(i);
			Expr arg = expr.fields.get(key);
			operands[i] = generate(arg, environment, codes);
		}
		int target = environment.allocate(expr.result().raw());
		codes.append(Code.NewRecord(expr.result().raw(), target, operands),
				attributes(expr));
		return target;
	}

	private int generate(Expr.Tuple expr, Environment environment, Block codes) {
		int[] operands = generate(expr.fields, environment, codes);
		int target = environment.allocate(expr.result().raw());
		codes.append(Code.NewTuple(expr.result().raw(), target, operands),
				attributes(expr));
		return target;
	}

	private int generate(Expr.Map expr, Environment environment, Block codes) {
		int[] operands = new int[expr.pairs.size() * 2];
		for (int i = 0; i != expr.pairs.size(); ++i) {
			Pair<Expr, Expr> e = expr.pairs.get(i);
			operands[i << 1] = generate(e.first(), environment, codes);
			operands[(i << 1) + 1] = generate(e.second(), environment, codes);
		}
		int target = environment.allocate(expr.result().raw());
		codes.append(Code.NewMap(expr.result().raw(), target, operands),
				attributes(expr));
		return target;
	}

	private int generate(Expr.RecordAccess expr, Environment environment,
			Block codes) {
		int operand = generate(expr.src, environment, codes);
		int target = environment.allocate(expr.result().raw());
		codes.append(
				Code.FieldLoad(expr.srcType.raw(), target, operand, expr.name),
				attributes(expr));
		return target;
	}

	private int generate(Expr.New expr, Environment environment, Block codes)
			throws ResolveError {
		int operand = generate(expr.expr, environment, codes);
		int target = environment.allocate(expr.result().raw());
		codes.append(Code.NewObject(expr.type.raw(), target, operand));
		return target;
	}

	private int[] generate(List<Expr> arguments, Environment environment,
			Block codes) {
		int[] operands = new int[arguments.size()];
		for (int i = 0; i != operands.length; ++i) {
			Expr arg = arguments.get(i);
			operands[i] = generate(arg, environment, codes);
		}
		return operands;
	}

	private Code.BinArithKind OP2BOP(Expr.BOp bop, SyntacticElement elem) {
		switch (bop) {
		case ADD:
			return Code.BinArithKind.ADD;
		case SUB:
			return Code.BinArithKind.SUB;
		case MUL:
			return Code.BinArithKind.MUL;
		case DIV:
			return Code.BinArithKind.DIV;
		case REM:
			return Code.BinArithKind.REM;
		case RANGE:
			return Code.BinArithKind.RANGE;
		case BITWISEAND:
			return Code.BinArithKind.BITWISEAND;
		case BITWISEOR:
			return Code.BinArithKind.BITWISEOR;
		case BITWISEXOR:
			return Code.BinArithKind.BITWISEXOR;
		case LEFTSHIFT:
			return Code.BinArithKind.LEFTSHIFT;
		case RIGHTSHIFT:
			return Code.BinArithKind.RIGHTSHIFT;
		}
		syntaxError(errorMessage(INVALID_BINARY_EXPRESSION), context, elem);
		return null;
	}

	private Code.Comparator OP2COP(Expr.BOp bop, SyntacticElement elem) {
		switch (bop) {
		case EQ:
			return Code.Comparator.EQ;
		case NEQ:
			return Code.Comparator.NEQ;
		case LT:
			return Code.Comparator.LT;
		case LTEQ:
			return Code.Comparator.LTEQ;
		case GT:
			return Code.Comparator.GT;
		case GTEQ:
			return Code.Comparator.GTEQ;
		case SUBSET:
			return Code.Comparator.SUBSET;
		case SUBSETEQ:
			return Code.Comparator.SUBSETEQ;
		case ELEMENTOF:
			return Code.Comparator.ELEMOF;
		}
		syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context, elem);
		return null;
	}

	private static int allocate(HashMap<String, Integer> environment) {
		return allocate("$" + environment.size(), environment);
	}

	private static int allocate(String var, HashMap<String, Integer> environment) {
		// this method is a bit of a hack
		Integer r = environment.get(var);
		if (r == null) {
			int slot = environment.size();
			environment.put(var, slot);
			return slot;
		} else {
			return r;
		}
	}

	private static Expr invert(Expr e) {
		if (e instanceof Expr.BinOp) {
			Expr.BinOp bop = (Expr.BinOp) e;
			Expr.BinOp nbop = null;
			switch (bop.op) {
			case AND:
				nbop = new Expr.BinOp(Expr.BOp.OR, invert(bop.lhs),
						invert(bop.rhs), attributes(e));
				break;
			case OR:
				nbop = new Expr.BinOp(Expr.BOp.AND, invert(bop.lhs),
						invert(bop.rhs), attributes(e));
				break;
			case EQ:
				nbop = new Expr.BinOp(Expr.BOp.NEQ, bop.lhs, bop.rhs,
						attributes(e));
				break;
			case NEQ:
				nbop = new Expr.BinOp(Expr.BOp.EQ, bop.lhs, bop.rhs,
						attributes(e));
				break;
			case LT:
				nbop = new Expr.BinOp(Expr.BOp.GTEQ, bop.lhs, bop.rhs,
						attributes(e));
				break;
			case LTEQ:
				nbop = new Expr.BinOp(Expr.BOp.GT, bop.lhs, bop.rhs,
						attributes(e));
				break;
			case GT:
				nbop = new Expr.BinOp(Expr.BOp.LTEQ, bop.lhs, bop.rhs,
						attributes(e));
				break;
			case GTEQ:
				nbop = new Expr.BinOp(Expr.BOp.LT, bop.lhs, bop.rhs,
						attributes(e));
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
	 * The shiftBlock method takes a block and shifts every slot a given amount
	 * to the right. The number of inputs remains the same. This method is used
	 * 
	 * @param amount
	 * @param blk
	 * @return
	 */
	private static Block shiftBlockExceptionZero(int amount, int zeroDest,
			Block blk) {
		HashMap<Integer, Integer> binding = new HashMap<Integer, Integer>();
		for (int i = 1; i != blk.numSlots(); ++i) {
			binding.put(i, i + amount);
		}
		binding.put(0, zeroDest);
		Block nblock = new Block(blk.numInputs());
		for (Block.Entry e : blk) {
			Code code = e.code.remap(binding);
			nblock.append(code, e.attributes());
		}
		return nblock.relabel();
	}

	/**
	 * The chainBlock method takes a block and replaces every fail statement
	 * with a goto to a given label. This is useful for handling constraints in
	 * union types, since if the constraint is not met that doesn't mean its
	 * game over.
	 * 
	 * @param target
	 * @param blk
	 * @return
	 */
	private static Block chainBlock(String target, Block blk) {
		Block nblock = new Block(blk.numInputs());
		for (Block.Entry e : blk) {
			if (e.code instanceof Code.Assert) {
				Code.Assert a = (Code.Assert) e.code;
				Code.Comparator iop = Code.invert(a.op);
				if (iop != null) {
					nblock.append(Code.If(a.type, a.leftOperand,
							a.rightOperand, iop, target), e.attributes());
				} else {
					// FIXME: avoid the branch here. This can be done by
					// ensuring that every Code.COp is invertible.
					String lab = Block.freshLabel();
					nblock.append(Code.If(a.type, a.leftOperand,
							a.rightOperand, a.op, lab), e.attributes());
					nblock.append(Code.Goto(target));
					nblock.append(Code.Label(lab));
				}
			} else {
				nblock.append(e.code, e.attributes());
			}
		}
		return nblock.relabel();
	}

	/**
	 * The attributes method extracts those attributes of relevance to wyil, and
	 * discards those which are only used for the wyc front end.
	 * 
	 * @param elem
	 * @return
	 */
	private static Collection<Attribute> attributes(SyntacticElement elem) {
		ArrayList<Attribute> attrs = new ArrayList<Attribute>();
		attrs.add(elem.attribute(Attribute.Source.class));
		return attrs;
	}

	public static final class Environment {
		private final HashMap<String, Integer> var2idx;
		private final ArrayList<Type> idx2type;

		public Environment() {
			var2idx = new HashMap<String, Integer>();
			idx2type = new ArrayList<Type>();
		}
		
		public Environment(Environment env) {
			var2idx = new HashMap<String, Integer>(env.var2idx);
			idx2type = new ArrayList<Type>(env.idx2type);
		}
		
		public int allocate(Type t) {
			int idx = idx2type.size();
			idx2type.add(t);
			return idx;
		}

		public int allocate(Type t, String v) {
			int r = allocate(t);
			var2idx.put(v, r);
			return r;
		}

		public int size() {
			return idx2type.size();
		}

		public Integer get(String v) {
			return var2idx.get(v);
		}

		public String get(int idx) {
			for (Map.Entry<String, Integer> e : var2idx.entrySet()) {
				int jdx = e.getValue();
				if (jdx == idx) {
					return e.getKey();
				}
			}
			return null;
		}

		public void put(int idx, String v) {
			var2idx.put(v, idx);
		}

		public ArrayList<Type> asList() {
			return idx2type;
		}

		public String toString() {
			return idx2type.toString() + "," + var2idx.toString();
		}
	}
}
