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
import java.util.List;
import java.util.Map;

import wybs.lang.SyntacticElement;
import wybs.lang.SyntaxError;
import wybs.util.ResolveError;
import wyc.lang.Expr;
import wyc.lang.UnresolvedType;
import wyc.lang.WhileyFile.Context;
import wyil.lang.Attribute;
import wyil.lang.Block;
import wyil.lang.Code;
import wyil.lang.Type;
import wyil.lang.Value;
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
	
	public LocalGenerator(GlobalGenerator global, Context context) {
		this.context = context;
		this.global = global;
	}
	
	public Context context() {
		return context;
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
	public Block generateAssertion(String message, Expr condition,
			boolean isAssumption, int freeRegister,
			HashMap<String, Integer> environment) {
		try {
			if (condition instanceof Expr.Constant
					|| condition instanceof Expr.ConstantAccess
					|| condition instanceof Expr.LocalVariable
					|| condition instanceof Expr.UnOp
					|| condition instanceof Expr.AbstractInvoke
					|| condition instanceof Expr.RecordAccess
					|| condition instanceof Expr.IndexOf
					|| condition instanceof Expr.Comprehension) {
				// fall through to default case
			} else if (condition instanceof Expr.BinOp) {
				return generateAssertion(message, (Expr.BinOp) condition, isAssumption, freeRegister, environment);
			} else {				
				syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context, condition);
			}
			
			// The default case simply compares the computed value against
			// true. In some cases, we could do better. For example, !(x < 5)
			// could be rewritten into x>=5. 
			
			Block blk = generate(condition,freeRegister,freeRegister+1,environment);
			blk.append(Code.Const(freeRegister+1,Value.V_BOOL(true)),attributes(condition));
			if(isAssumption) {
				blk.append(Code.Assume(Type.T_BOOL, freeRegister,
						freeRegister + 1, Code.Comparator.EQ, message),
						attributes(condition));
			} else {
				blk.append(Code.Assert(Type.T_BOOL, freeRegister, freeRegister + 1,
						Code.Comparator.EQ, message), attributes(condition));
			}
			return blk;
		} catch (SyntaxError se) {
			throw se;
		} catch (Exception ex) {			
			internalFailure(ex.getMessage(), context, condition, ex);
		}
		return null;
	}
	
	protected Block generateAssertion(String message, Expr.BinOp v,
			boolean isAssumption, int freeRegister,
			HashMap<String, Integer> environment) {
		Block blk = new Block(environment.size());
		Expr.BOp bop = v.op;
		
		if (bop == Expr.BOp.OR) {
			String lab = Block.freshLabel();
			blk.append(generateCondition(lab, v.lhs, freeRegister, environment));
			blk.append(generateAssertion(message, v.rhs, isAssumption, freeRegister,
					environment));
			blk.append(Code.Label(lab));
			return blk;
		} else if (bop == Expr.BOp.AND) {
			blk.append(generateAssertion(message, v.lhs, isAssumption, freeRegister,
					environment));
			blk.append(generateAssertion(message, v.rhs, isAssumption, freeRegister,
					environment));
			return blk;
		}
		
		// TODO: there are some cases which will break here. In particular,
		// those involving type tests. If/When WYIL changes to be register based
		// this should fall out in the wash.
		
		Code.Comparator cop = OP2COP(bop,v);
		
		blk.append(generate(v.lhs, freeRegister, freeRegister + 1, environment));
		blk.append(generate(v.rhs, freeRegister + 1, freeRegister + 2,
				environment));
		if (isAssumption) {
			blk.append(Code.Assume(v.srcType.raw(), freeRegister,
					freeRegister + 1, cop, message), attributes(v));
		} else {
			blk.append(Code.Assert(v.srcType.raw(), freeRegister,
					freeRegister + 1, cop, message), attributes(v));
		}

		return blk;
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
	 * @param freeRegister
	 *            --- All registers with and index equal or higher than this are
	 *            available for use as temporary storage.
	 * @param environment
	 *            --- Mapping from variable names to to slot numbers.
	 * @return
	 */
	public Block generateCondition(String target, Expr condition,
			 int freeRegister, HashMap<String, Integer> environment) {
		try {
			if (condition instanceof Expr.Constant) {
				return generateCondition(target, (Expr.Constant) condition, freeRegister, environment);
			} else if (condition instanceof Expr.ConstantAccess
					|| condition instanceof Expr.LocalVariable
					|| condition instanceof Expr.AbstractInvoke
					|| condition instanceof Expr.RecordAccess
					|| condition instanceof Expr.IndexOf) {
				// fall through to default case
			} else if (condition instanceof Expr.UnOp) {
				return generateCondition(target, (Expr.UnOp) condition, freeRegister, environment);
			} else if (condition instanceof Expr.BinOp) {
				return generateCondition(target, (Expr.BinOp) condition, freeRegister, environment);
			} else if (condition instanceof Expr.Comprehension) {
				return generateCondition(target, (Expr.Comprehension) condition, freeRegister, environment);
			} else {				
				syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context, condition);
			}
			
			// The default case simply compares the computed value against
			// true. In some cases, we could do better. For example, !(x < 5)
			// could be rewritten into x>=5. 
			
			Block blk = generate(condition, freeRegister, freeRegister + 1,
					environment);
			blk.append(Code.Const(freeRegister + 1, Value.V_BOOL(true)),
					attributes(condition));
			blk.append(Code.If(Type.T_BOOL, freeRegister, freeRegister + 1,
					Code.Comparator.EQ, target), attributes(condition));
			return blk;
			
		} catch (SyntaxError se) {
			throw se;
		} catch (Exception ex) {			
			internalFailure(ex.getMessage(), context, condition, ex);
		}

		return null;
	}

	private Block generateCondition(String target, Expr.Constant c, int freeRegister, HashMap<String,Integer> environment) {
		Value.Bool b = (Value.Bool) c.value;
		Block blk = new Block(environment.size());
		if (b.value) {
			blk.append(Code.Goto(target));
		} else {
			// do nout
		}
		return blk;
	}
	
	private Block generateCondition(String target, Expr.BinOp v, int freeRegister, HashMap<String,Integer> environment) throws Exception {
		
		Expr.BOp bop = v.op;
		Block blk = new Block(environment.size());

		if (bop == Expr.BOp.OR) {
			blk.append(generateCondition(target, v.lhs, freeRegister, environment));
			blk.append(generateCondition(target, v.rhs, freeRegister, environment));
			return blk;
		} else if (bop == Expr.BOp.AND) {
			String exitLabel = Block.freshLabel();
			blk.append(generateCondition(exitLabel, invert(v.lhs), freeRegister, environment));
			blk.append(generateCondition(target, v.rhs, freeRegister, environment));
			blk.append(Code.Label(exitLabel));
			return blk;
		} else if (bop == Expr.BOp.IS) {
			return generateTypeCondition(target, v, freeRegister, environment);
		}

		Code.Comparator cop = OP2COP(bop,v);
		
		if (cop == Code.Comparator.EQ && v.lhs instanceof Expr.LocalVariable
				&& v.rhs instanceof Expr.Constant
				&& ((Expr.Constant) v.rhs).value == Value.V_NULL) {
			// this is a simple rewrite to enable type inference.
			Expr.LocalVariable lhs = (Expr.LocalVariable) v.lhs;
			if (!environment.containsKey(lhs.var)) {
				syntaxError(errorMessage(UNKNOWN_VARIABLE), context, v.lhs);
			}
			int slot = environment.get(lhs.var);								
			blk.append(Code.IfIs(v.srcType.raw(), slot, Type.T_NULL, target), attributes(v));
		} else if (cop == Code.Comparator.NEQ && v.lhs instanceof Expr.LocalVariable
				&& v.rhs instanceof Expr.Constant
				&& ((Expr.Constant) v.rhs).value == Value.V_NULL) {			
			// this is a simple rewrite to enable type inference.
			String exitLabel = Block.freshLabel();
			Expr.LocalVariable lhs = (Expr.LocalVariable) v.lhs;
			if (!environment.containsKey(lhs.var)) {
				syntaxError(errorMessage(UNKNOWN_VARIABLE), context, v.lhs);
			}
			int slot = environment.get(lhs.var);			
			blk.append(Code.IfIs(v.srcType.raw(), slot, Type.T_NULL, exitLabel), attributes(v));
			blk.append(Code.Goto(target));
			blk.append(Code.Label(exitLabel));
		} else {
			blk.append(generate(v.lhs, freeRegister, freeRegister + 1,
					environment));
			blk.append(generate(v.rhs, freeRegister + 1, freeRegister + 2,
					environment));
			blk.append(Code.If(v.srcType.raw(), freeRegister,
					freeRegister + 1, cop, target), attributes(v));
		}
		return blk;
	}

	private Block generateTypeCondition(String target, Expr.BinOp v,
			int freeRegister, HashMap<String, Integer> environment)
			throws Exception {
		Block blk;
		int leftOperand;

		if (v.lhs instanceof Expr.LocalVariable) {
			Expr.LocalVariable lhs = (Expr.LocalVariable) v.lhs;
			if (!environment.containsKey(lhs.var)) {
				syntaxError(errorMessage(UNKNOWN_VARIABLE), context, v.lhs);
			}
			leftOperand = environment.get(lhs.var);
			blk = new Block(environment.size());
		} else {
			blk = generate(v.lhs, freeRegister, freeRegister + 1, environment);
			leftOperand = freeRegister++;
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

				blk.append(
						Code.IfIs(v.srcType.raw(), leftOperand,
								rhs.type.raw(), nextLabel), attributes(v));
				blk.append(Code.Goto(exitLabel));
				blk.append(Code.Label(nextLabel));
			}
			constraint = shiftBlockExceptionZero(environment.size() - 1,
					leftOperand, constraint);
			blk.append(chainBlock(exitLabel, constraint));
			blk.append(Code.Goto(target));
			blk.append(Code.Label(exitLabel));
		} else {
			blk.append(Code.IfIs(v.srcType.raw(), leftOperand,
					rhs.type.raw(), target), attributes(v));
		}
		return blk;
	}

	private Block generateCondition(String target, Expr.UnOp v,
			int freeRegister, HashMap<String, Integer> environment) {
		Expr.UOp uop = v.op;
		switch (uop) {
			case NOT :
				String label = Block.freshLabel();
				Block blk = generateCondition(label, v.mhs, freeRegister,
						environment);
				blk.append(Code.Goto(target));
				blk.append(Code.Label(label));
				return blk;
		}
		syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context, v);
		return null;
	}
	
	private Block generateCondition(String target, Expr.Comprehension e, int freeRegister,  
			HashMap<String,Integer> _environment) {
				
		if (e.cop != Expr.COp.NONE && e.cop != Expr.COp.SOME) {
			syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context, e);
		}
		
		HashMap<String,Integer> environment = new HashMap<String,Integer>(_environment);
		
		// Ok, non-boolean case.				
		Block blk = new Block(environment.size());
		ArrayList<Triple<Integer,Integer,Type.EffectiveCollection>> slots = new ArrayList();		
		
		for (Pair<String, Expr> src : e.sources) {
			int srcSlot;
			int varSlot = freeRegister++;
			environment.put(src.first(), varSlot);
			Nominal srcType = src.second().result();			
			
			if(src.second() instanceof Expr.LocalVariable) {
				// this is a little optimisation to produce slightly better
				// code.
				Expr.LocalVariable v = (Expr.LocalVariable) src.second();
				if(environment.containsKey(v.var)) {					
					srcSlot = environment.get(v.var);
				} else {					
					// fall-back plan ...
					srcSlot = freeRegister++;
					blk.append(generate(src.second(), srcSlot, freeRegister, environment));					
				}
			} else {
				srcSlot = freeRegister++;
				blk.append(generate(src.second(), srcSlot, freeRegister, environment));				
			}			
			slots.add(new Triple(varSlot,srcSlot,srcType.raw()));											
		}
				
		ArrayList<String> labels = new ArrayList<String>();
		String loopLabel = Block.freshLabel();
		
		for (Triple<Integer, Integer, Type.EffectiveCollection> p : slots) {
			Type.EffectiveCollection srcType = p.third();			
			String lab = loopLabel + "$" + p.first();															
			blk.append(Code.ForAll(srcType, p.second(), p.first(),
					Collections.EMPTY_LIST, lab), attributes(e));
			labels.add(lab);
		}
								
		if (e.cop == Expr.COp.NONE) {
			String exitLabel = Block.freshLabel();
			blk.append(generateCondition(exitLabel, e.condition, freeRegister,
					environment));
			for (int i = (labels.size() - 1); i >= 0; --i) {				
				blk.append(Code.End(labels.get(i)));
			}
			blk.append(Code.Goto(target));
			blk.append(Code.Label(exitLabel));
		} else { // SOME			
			blk.append(generateCondition(target, e.condition, freeRegister,
					environment));
			for (int i = (labels.size() - 1); i >= 0; --i) {
				blk.append(Code.End(labels.get(i)));
			}
		} // ALL, LONE and ONE will be harder					
		
		return blk;
	}
	
	/**
	 * Translate a source-level expression into a WYIL bytecode block, using a
	 * given environment mapping named variables to registers. The result of the
	 * expression is stored in a given target register.
	 * 
	 * @param expression
	 *            --- Source-level expression to be translated
	 * @param target
	 *            --- Register in to which the result from this expression
	 *            should be stored. This may not equal the freeRegsiter.
	 * @param freeRegister
	 *            --- All registers with and index equal or higher than this are
	 *            available for use as temporary storage.
	 * @param environment
	 *            --- Mapping from variable names to to slot numbers.
	 * @return
	 */
	public Block generate(Expr expression, int target, int freeRegister,
			HashMap<String, Integer> environment) {
		try {
			if (expression instanceof Expr.Constant) {
				return generate((Expr.Constant) expression, target, freeRegister, environment);
			} else if (expression instanceof Expr.LocalVariable) {
				return generate((Expr.LocalVariable) expression, target, freeRegister, environment);
			} else if (expression instanceof Expr.ConstantAccess) {
				return generate((Expr.ConstantAccess) expression, target, freeRegister, environment);
			} else if (expression instanceof Expr.Set) {
				return generate((Expr.Set) expression, target, freeRegister, environment);
			} else if (expression instanceof Expr.List) {
				return generate((Expr.List) expression, target, freeRegister, environment);
			} else if (expression instanceof Expr.SubList) {
				return generate((Expr.SubList) expression, target, freeRegister, environment);
			} else if (expression instanceof Expr.SubString) {
				return generate((Expr.SubString) expression, target, freeRegister, environment);
			} else if (expression instanceof Expr.BinOp) {
				return generate((Expr.BinOp) expression, target, freeRegister, environment);
			} else if (expression instanceof Expr.LengthOf) {
				return generate((Expr.LengthOf) expression, target, freeRegister, environment);
			} else if (expression instanceof Expr.Dereference) {
				return generate((Expr.Dereference) expression, target, freeRegister, environment);
			} else if (expression instanceof Expr.Convert) {
				return generate((Expr.Convert) expression, target, freeRegister, environment);
			} else if (expression instanceof Expr.IndexOf) {
				return generate((Expr.IndexOf) expression, target, freeRegister, environment);
			} else if (expression instanceof Expr.UnOp) {
				return generate((Expr.UnOp) expression, target, freeRegister, environment);
			} else if (expression instanceof Expr.FunctionCall) {
				return generate((Expr.FunctionCall) expression, target, freeRegister, environment);
			} else if (expression instanceof Expr.MethodCall) {
				return generate((Expr.MethodCall) expression, target, freeRegister, environment);
			} else if (expression instanceof Expr.IndirectFunctionCall) {
				return generate((Expr.IndirectFunctionCall) expression, target, freeRegister, environment);
			} else if (expression instanceof Expr.IndirectMethodCall) {
				return generate((Expr.IndirectMethodCall) expression, target, freeRegister, environment);
			} else if (expression instanceof Expr.IndirectMessageSend) {
				return generate((Expr.IndirectMessageSend) expression, target, freeRegister, environment);
			} else if (expression instanceof Expr.MessageSend) {
				return generate((Expr.MessageSend) expression, target, freeRegister, environment);
			} else if (expression instanceof Expr.Comprehension) {
				return generate((Expr.Comprehension) expression, target, freeRegister, environment);
			} else if (expression instanceof Expr.RecordAccess) {
				return generate((Expr.RecordAccess) expression, target, freeRegister, environment);
			} else if (expression instanceof Expr.Record) {
				return generate((Expr.Record) expression, target, freeRegister, environment);
			} else if (expression instanceof Expr.Tuple) {
				return generate((Expr.Tuple) expression, target, freeRegister, environment);
			} else if (expression instanceof Expr.Map) {
				return generate((Expr.Map) expression, target, freeRegister, environment);
			} else if (expression instanceof Expr.FunctionOrMethodOrMessage) {
				return generate((Expr.FunctionOrMethodOrMessage) expression, target, freeRegister, environment);
			} else if (expression instanceof Expr.New) {
				return generate((Expr.New) expression, target, freeRegister, environment);
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

		return null;
	}

	public Block generate(Expr.MessageSend fc, int target, int freeRegister,
			HashMap<String, Integer> environment) throws ResolveError {
		Block blk = new Block(environment.size());

		int[] operands = new int[fc.arguments.size() + 1];		
		blk.append(generate(fc.qualification, freeRegister, freeRegister+1, environment));
		operands[0] = freeRegister++;
		for (int i = 1; i != operands.length; ++i) {
			Expr arg = fc.arguments.get(i-1);
			blk.append(generate(arg, freeRegister, freeRegister + 1,
					environment));
			operands[i] = freeRegister++;
		}

		blk.append(Code.Send(fc.messageType.raw(), target, operands, fc.nid,
				fc.synchronous), attributes(fc));

		return blk;
	}
	
	public Block generate(Expr.MethodCall fc, int target, int freeRegister,
			HashMap<String, Integer> environment) throws ResolveError {
		Block blk = new Block(environment.size());
		
		int[] operands = generate(fc.arguments, freeRegister, freeRegister+1,
				environment, blk);
		
		blk.append(
				Code.Invoke(fc.methodType.raw(), target, operands, fc.nid()),
				attributes(fc));

		return blk;
	}
	
	public Block generate(Expr.FunctionCall fc, int target, int freeRegister,
			HashMap<String, Integer> environment) throws ResolveError {
		Block blk = new Block(environment.size());

		int[] operands = generate(fc.arguments, freeRegister, freeRegister+1,
				environment, blk);
		blk.append(
				Code.Invoke(fc.functionType.raw(), target, operands, fc.nid()),
				attributes(fc));

		return blk;
	}
	
	public Block generate(Expr.IndirectFunctionCall fc, int target,
			int freeRegister, HashMap<String, Integer> environment)
			throws ResolveError {
		Block blk = new Block(environment.size());

		blk.append(generate(fc.src, target, freeRegister, environment));

		int[] operands = generate(fc.arguments, freeRegister, freeRegister + 1,
				environment, blk);

		blk.append(Code.IndirectInvoke(fc.functionType.raw(), target, target,
				operands), attributes(fc));

		return blk;
	}
	
	public Block generate(Expr.IndirectMethodCall fc, int target,
			int freeRegister, HashMap<String, Integer> environment)
			throws ResolveError {
		Block blk = new Block(environment.size());

		blk.append(generate(fc.src, target, freeRegister, environment));

		int[] operands = generate(fc.arguments, freeRegister, freeRegister + 1,
				environment, blk);

		blk.append(Code.IndirectInvoke(fc.methodType.raw(), target, target,
				operands), attributes(fc));

		return blk;
	}
	
	public Block generate(Expr.IndirectMessageSend fc, int target,
			int freeRegister, boolean retval,
			HashMap<String, Integer> environment) throws ResolveError {
		Block blk = new Block(environment.size());

		blk.append(generate(fc.src, target, freeRegister, environment));

		int[] operands = new int[fc.arguments.size() + 1];
		blk.append(generate(fc.receiver, target, freeRegister, environment));
		operands[0] = freeRegister++;
		for (int i = 0; i != operands.length; ++i) {
			Expr arg = fc.arguments.get(i);
			blk.append(generate(arg, freeRegister, freeRegister + 1,
					environment));
			operands[i] = freeRegister++;
		}

		blk.append(Code.IndirectSend(fc.messageType.raw(), target, target,
				operands, fc.synchronous), attributes(fc));

		return blk;
	}
	
	private Block generate(Expr.Constant c, int target, int freeRegister,
			HashMap<String, Integer> environment) {
		Block blk = new Block(environment.size());
		blk.append(Code.Const(target, c.value), attributes(c));
		return blk;
	}

	private Block generate(Expr.FunctionOrMethodOrMessage s, int target, int freeRegister, HashMap<String,Integer> environment) {						
		Block blk = new Block(environment.size());
		blk.append(Code.Const(target, Value.V_FUN(s.nid, s.type.raw())),
				attributes(s));
		return blk;
	}
	
	private Block generate(Expr.ConstantAccess v, int target, int freeRegister, HashMap<String,Integer> environment) throws ResolveError {						
		Block blk = new Block(environment.size());
		Value val = v.value;				
		blk.append(Code.Const(target,val),attributes(v));
		return blk;
	}
	
	private Block generate(Expr.LocalVariable v, int target, int freeRegister, HashMap<String,Integer> environment) throws ResolveError {
		
		if (environment.containsKey(v.var)) {
			Block blk = new Block(environment.size());
			blk.append(Code.Assign(v.result().raw(), target, environment.get(v.var)), attributes(v));
			return blk;
		} else {
			syntaxError(errorMessage(VARIABLE_POSSIBLY_UNITIALISED), context,
					v);
		}
		
		// must be an error
		syntaxError("unknown variable \"" + v.var + "\"", context,v);
		return null;
	}

	private Block generate(Expr.UnOp v, int target, int freeRegister, HashMap<String,Integer> environment) {
		Block blk = generate(v.mhs,  target, freeRegister, environment);	
		switch (v.op) {
		case NEG:
			blk.append(Code.UnArithOp(v.result().raw(), target, target, Code.UnArithKind.NEG), attributes(v));
			break;
		case INVERT:
			blk.append(Code.Invert(v.result().raw(), target, target), attributes(v));
			break;
		case NOT:
			String falseLabel = Block.freshLabel();
			String exitLabel = Block.freshLabel();
			blk = generateCondition(falseLabel, v.mhs, freeRegister, environment);
			blk.append(Code.Const(target,Value.V_BOOL(true)), attributes(v));
			blk.append(Code.Goto(exitLabel));
			blk.append(Code.Label(falseLabel));
			blk.append(Code.Const(target,Value.V_BOOL(false)), attributes(v));
			blk.append(Code.Label(exitLabel));
			break;							
		default:
			// should be dead-code
			internalFailure("unexpected unary operator encountered", context, v);
			return null;
		}
		return blk;
	}
	
	private Block generate(Expr.LengthOf v, int target, int freeRegister,
			HashMap<String, Integer> environment) {
		Block blk = generate(v.src, target, freeRegister, environment);
		blk.append(Code.LengthOf(v.srcType.raw(), target, target),
				attributes(v));
		return blk;
	}
			
	private Block generate(Expr.Dereference v, int target, int freeRegister,
			HashMap<String, Integer> environment) {
		Block blk = generate(v.src, target, freeRegister, environment);
		blk.append(Code.Dereference(v.srcType.raw(), target, target),
				attributes(v));
		return blk;
	}
	
	private Block generate(Expr.IndexOf v, int target, int freeRegister,
			HashMap<String, Integer> environment) {
		Block blk = new Block(environment.size());
		blk.append(generate(v.src, freeRegister, freeRegister+1, environment));
		blk.append(generate(v.index, freeRegister+1, freeRegister+2, environment));
		blk.append(Code.IndexOf(v.srcType.raw(), target, freeRegister, freeRegister+1),
				attributes(v));
		return blk;
	}
	
	private Block generate(Expr.Convert v, int target, int freeRegister,
			HashMap<String, Integer> environment) {
		Block blk = new Block(environment.size());
		blk.append(generate(v.expr, target, freeRegister, environment));
		Type from = v.expr.result().raw();
		Type to = v.result().raw();
		// TODO: include constraints
		blk.append(Code.Convert(from, target, target, to), attributes(v));
		return blk;
	}
	
	private Block generate(Expr.BinOp v, int target, int freeRegister, HashMap<String,Integer> environment) throws Exception {

		// could probably use a range test for this somehow
		if (v.op == Expr.BOp.EQ || v.op == Expr.BOp.NEQ || v.op == Expr.BOp.LT
				|| v.op == Expr.BOp.LTEQ || v.op == Expr.BOp.GT || v.op == Expr.BOp.GTEQ
				|| v.op == Expr.BOp.SUBSET || v.op == Expr.BOp.SUBSETEQ
				|| v.op == Expr.BOp.ELEMENTOF || v.op == Expr.BOp.AND || v.op == Expr.BOp.OR) {
			String trueLabel = Block.freshLabel();
			String exitLabel = Block.freshLabel();
			Block blk = generateCondition(trueLabel, v, freeRegister, environment);
			blk.append(Code.Const(target,Value.V_BOOL(false)), attributes(v));			
			blk.append(Code.Goto(exitLabel));
			blk.append(Code.Label(trueLabel));
			blk.append(Code.Const(target,Value.V_BOOL(true)), attributes(v));				
			blk.append(Code.Label(exitLabel));			
			return blk;
		}

		Expr.BOp bop = v.op;
		Block blk = new Block(environment.size());
		blk.append(generate(v.lhs, freeRegister, freeRegister+1, environment));
		blk.append(generate(v.rhs, freeRegister+1, freeRegister+2, environment));
		Type result = v.result().raw();
		
		switch(bop) {		
		case UNION:
				blk.append(Code.BinSetOp((Type.EffectiveSet) result, target,
						freeRegister, freeRegister+1, Code.BinSetKind.UNION),
						attributes(v));			
			return blk;			
		case INTERSECTION:
				blk.append(Code.BinSetOp((Type.EffectiveSet) result, target,
						freeRegister, freeRegister+1, Code.BinSetKind.INTERSECTION),
						attributes(v));
				return blk;	
		case DIFFERENCE:
				blk.append(Code.BinSetOp((Type.EffectiveSet) result, target,
						freeRegister, freeRegister+1, Code.BinSetKind.DIFFERENCE),
						attributes(v));
				return blk;
		case LISTAPPEND:
				blk.append(Code.BinListOp((Type.EffectiveList) result, target,
						freeRegister, freeRegister+1, Code.BinListKind.APPEND),
						attributes(v));
				return blk;
		case STRINGAPPEND:
			Type lhs = v.lhs.result().raw();
			Type rhs = v.rhs.result().raw();
			Code.StringOperation op;
			if(lhs == Type.T_STRING && rhs == Type.T_STRING) {
				op = Code.StringOperation.APPEND;
			} else if(lhs == Type.T_STRING && Type.isSubtype(Type.T_CHAR, rhs)) {
				op = Code.StringOperation.LEFT_APPEND;
			} else if(rhs == Type.T_STRING && Type.isSubtype(Type.T_CHAR, lhs)) {
				op = Code.StringOperation.RIGHT_APPEND;
			} else {
				// this indicates that one operand must be explicitly converted
				// into a string.
				op = Code.StringOperation.APPEND;
			}
			blk.append(Code.StringOp(target,freeRegister, freeRegister+1,op),attributes(v));
			return blk;	
		default:
				blk.append(Code.BinArithOp(result, target, freeRegister, freeRegister+1,
						OP2BOP(bop, v)), attributes(v));			
			return blk;
		}		
	}

	private Block generate(Expr.Set v, int target, int freeRegister,
			HashMap<String, Integer> environment) {
		Block blk = new Block(environment.size());
		int[] operands = generate(v.arguments, target, freeRegister, environment,
				blk);		
		blk.append(Code.NewSet(v.type.raw(), target, operands), attributes(v));
		return blk;
	}
	
	private Block generate(Expr.List v, int target, int freeRegister,
			HashMap<String, Integer> environment) {
		Block blk = new Block(environment.size());		
		int[] operands = generate(v.arguments, target, freeRegister, environment,
				blk);		
		blk.append(Code.NewList(v.type.raw(), target, operands), attributes(v));
		return blk;
	}
	
	private Block generate(Expr.SubList v, int target, int freeRegister,
			HashMap<String, Integer> environment) {
		Block blk = new Block(environment.size());
		blk.append(generate(v.src, freeRegister, freeRegister + 1, environment));
		blk.append(generate(v.start, freeRegister + 1, freeRegister + 2,
				environment));
		blk.append(generate(v.end, freeRegister + 2, freeRegister + 3,
				environment));
		blk.append(Code.SubList(v.type.raw(), target, freeRegister,
				freeRegister + 1, freeRegister + 2), attributes(v));
		return blk;
	}
	
	private Block generate(Expr.SubString v, int target, int freeRegister,
			HashMap<String, Integer> environment) {
		Block blk = new Block(environment.size());
		blk.append(generate(v.src, freeRegister, freeRegister + 1, environment));
		blk.append(generate(v.start, freeRegister + 1, freeRegister + 2,
				environment));
		blk.append(generate(v.end, freeRegister + 2, freeRegister + 3,
				environment));
		blk.append(Code.SubString(target, freeRegister, freeRegister + 1,
				freeRegister + 2), attributes(v));
		return blk;
	}
	
	private Block generate(Expr.Comprehension e, int target, int freeRegister, HashMap<String,Integer> _environment) {

		// First, check for boolean cases which are handled mostly by
		// generateCondition.
		if (e.cop == Expr.COp.SOME || e.cop == Expr.COp.NONE) {
			String trueLabel = Block.freshLabel();
			String exitLabel = Block.freshLabel();			
			Block blk = generateCondition(trueLabel, e, freeRegister, _environment);					
			blk.append(Code.Const(target, Value.V_BOOL(false)), attributes(e));					
			blk.append(Code.Goto(exitLabel));
			blk.append(Code.Label(trueLabel));
			blk.append(Code.Const(target, Value.V_BOOL(true)), attributes(e));			
			blk.append(Code.Label(exitLabel));			
			return blk;
		}

		// Ok, non-boolean case.
		HashMap<String,Integer> environment = new HashMap<String,Integer>(_environment);
		Block blk = new Block(environment.size());
		ArrayList<Triple<Integer,Integer,Type.EffectiveCollection>> slots = new ArrayList();		
		
		for (Pair<String, Expr> p : e.sources) {
			int srcSlot;
			int varSlot = freeRegister++;
			environment.put(p.first(), varSlot);
			Expr src = p.second();
			Type rawSrcType = src.result().raw();
			
			if(src instanceof Expr.LocalVariable) {
				// this is a little optimisation to produce slightly better
				// code.
				Expr.LocalVariable v = (Expr.LocalVariable) src;
				if(environment.containsKey(v.var)) {
					srcSlot = environment.get(v.var);
				} else {
					// fall-back plan ...
					srcSlot = freeRegister++;					
					blk.append(generate(src, srcSlot, freeRegister, environment));													
				}
			} else {
				srcSlot = freeRegister++;
				blk.append(generate(src, srcSlot, freeRegister, environment));								
			}			
			slots.add(new Triple(varSlot,srcSlot,rawSrcType));											
		}
		
		Type resultType;
		
		if (e.cop == Expr.COp.LISTCOMP) {
			resultType = e.type.raw();
			blk.append(Code.NewList((Type.List) resultType, target,
					Collections.EMPTY_LIST), attributes(e));
		} else {
			resultType = e.type.raw();
			blk.append(Code.NewSet((Type.Set) resultType, target,
					Collections.EMPTY_LIST), attributes(e));
		}
		
		// At this point, it would be good to determine an appropriate loop
		// invariant for a set comprehension. This is easy enough in the case of
		// a single variable comprehension, but actually rather difficult for a
		// multi-variable comprehension.
		//
		// For example, consider <code>{x+y | x in xs, y in ys, x<0 && y<0}</code>
		// 
		// What is an appropriate loop invariant here?
		
		String continueLabel = Block.freshLabel();
		ArrayList<String> labels = new ArrayList<String>();
		String loopLabel = Block.freshLabel();
		
		for (Triple<Integer, Integer, Type.EffectiveCollection> p : slots) {
			String label = loopLabel + "$" + p.first();			
			blk.append(Code.ForAll(p.third(), p.second(), p.first(), 
					Collections.EMPTY_LIST, label), attributes(e));
			labels.add(label);
		}
		
		if (e.condition != null) {
			blk.append(generateCondition(continueLabel, invert(e.condition),
					freeRegister, environment));
		}
				
		blk.append(generate(e.value, freeRegister, freeRegister + 1,
				environment));

		// FIXME: following broken for list comprehensions
		blk.append(Code.BinSetOp((Type.Set) resultType, target, target,
				freeRegister, Code.BinSetKind.LEFT_UNION), attributes(e));
	
		if(e.condition != null) {
			blk.append(Code.Label(continueLabel));			
		} 

		for (int i = (labels.size() - 1); i >= 0; --i) {
			blk.append(Code.End(labels.get(i)));
		}

		return blk;
	}

	private Block generate(Expr.Record sg, int target, int freeRegister, HashMap<String,Integer> environment) {
		Block blk = new Block(environment.size());
		ArrayList<String> keys = new ArrayList<String>(sg.fields.keySet());
		Collections.sort(keys);		
		int[] operands = new int[sg.fields.size()];
		for (int i = 0; i != operands.length; ++i) {
			String key = keys.get(i);
			Expr arg = sg.fields.get(key);
			blk.append(generate(arg, freeRegister, freeRegister+1, environment));
			operands[i] = freeRegister++;
		}
		blk.append(Code.NewRecord(sg.result().raw(), target, operands), attributes(sg));
		return blk;
	}

	private Block generate(Expr.Tuple sg, int target, int freeRegister,
			HashMap<String, Integer> environment) {
		Block blk = new Block(environment.size());
		int[] operands = generate(sg.fields, target, freeRegister, environment,
				blk);
		blk.append(Code.NewTuple(sg.result().raw(), target, operands),
				attributes(sg));
		return blk;
	}

	private Block generate(Expr.Map sg, int target, int freeRegister,
			HashMap<String, Integer> environment) {
		Block blk = new Block(environment.size());
		int[] operands = new int[sg.pairs.size() * 2];
		for (int i = 0; i != sg.pairs.size(); ++i) {
			Pair<Expr, Expr> e = sg.pairs.get(i);
			blk.append(generate(e.first(), freeRegister, freeRegister + 1,
					environment));
			operands[i << 1] = freeRegister++;
			blk.append(generate(e.second(), freeRegister, freeRegister + 1,
					environment));
			operands[(i << 1) + 1] = freeRegister++;
		}
		blk.append(Code.NewMap(sg.result().raw(), target, operands),
				attributes(sg));
		return blk;
	}
	
	private Block generate(Expr.RecordAccess sg, int target, int freeRegister,
			HashMap<String, Integer> environment) {
		
		Block lhs = generate(sg.src, target, freeRegister, environment);
		lhs.append(Code.FieldLoad(sg.srcType.raw(), target, target, sg.name),
				attributes(sg));
		return lhs;
	}
	
	private Block generate(Expr.New expr, int target, int freeRegister,
			HashMap<String, Integer> environment) throws ResolveError {
		Block blk = generate(expr.expr, target, freeRegister, environment);
		blk.append(Code.NewObject(expr.type.raw(), target, target));
		return blk;
	}
	
	private int[] generate(List<Expr> arguments, int target, int freeRegister,
			HashMap<String, Integer> environment, Block blk) {
		int[] operands = new int[arguments.size()];
		for (int i = 0; i != operands.length; ++i) {
			Expr arg = arguments.get(i);
			blk.append(generate(arg, freeRegister, freeRegister + 1,
					environment));
			operands[i] = freeRegister++;
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
	
	private static int allocate(HashMap<String,Integer> environment) {
		return allocate("$" + environment.size(),environment);
	}
	
	private static int allocate(String var, HashMap<String,Integer> environment) {
		// this method is a bit of a hack
		Integer r = environment.get(var);
		if(r == null) {
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
				nbop = new Expr.BinOp(Expr.BOp.OR, invert(bop.lhs), invert(bop.rhs), attributes(e));
				break;
			case OR:
				nbop = new Expr.BinOp(Expr.BOp.AND, invert(bop.lhs), invert(bop.rhs), attributes(e));
				break;
			case EQ:
				nbop =  new Expr.BinOp(Expr.BOp.NEQ, bop.lhs, bop.rhs, attributes(e));
				break;
			case NEQ:
				nbop =  new Expr.BinOp(Expr.BOp.EQ, bop.lhs, bop.rhs, attributes(e));
				break;
			case LT:
				nbop =  new Expr.BinOp(Expr.BOp.GTEQ, bop.lhs, bop.rhs, attributes(e));
				break;
			case LTEQ:
				nbop =  new Expr.BinOp(Expr.BOp.GT, bop.lhs, bop.rhs, attributes(e));
				break;
			case GT:
				nbop =  new Expr.BinOp(Expr.BOp.LTEQ, bop.lhs, bop.rhs, attributes(e));
				break;
			case GTEQ:
				nbop =  new Expr.BinOp(Expr.BOp.LT, bop.lhs, bop.rhs, attributes(e));
				break;			
			}
			if(nbop != null) {
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
	private static Block shiftBlockExceptionZero(int amount, int zeroDest, Block blk) {
		HashMap<Integer,Integer> binding = new HashMap<Integer,Integer>();
		for(int i=1;i!=blk.numSlots();++i) {
			binding.put(i,i+amount);		
		}
		binding.put(0, zeroDest);
		Block nblock = new Block(blk.numInputs());
		for(Block.Entry e : blk) {
			Code code = e.code.remap(binding);
			nblock.append(code,e.attributes());
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
				if(iop != null) {
					nblock.append(Code.If(a.type,a.leftOperand,a.rightOperand,iop,target), e.attributes());
				} else {
					// FIXME: avoid the branch here. This can be done by
					// ensuring that every Code.COp is invertible.
					String lab = Block.freshLabel();
					nblock.append(Code.If(a.type,a.leftOperand,a.rightOperand,a.op,lab), e.attributes());
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
}
