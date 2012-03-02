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
	 * Translate a source-level condition into a wyil block, using a given
	 * environment mapping named variables to slots. If the condition evaluates
	 * to true, then control is transferred to the given target. Otherwise,
	 * control will fall through to the following bytecode.
	 * 
	 * @param target
	 *            --- target label to goto if condition is true.
	 * @param condition
	 *            --- source-level condition to be translated
	 * @param environment
	 *            --- mapping from variable names to to slot numbers.
	 * @return
	 */
	public Block generateCondition(String target, Expr condition,
			 HashMap<String, Integer> environment) {
		try {
			if (condition instanceof Expr.Constant) {
				return generateCondition(target, (Expr.Constant) condition, environment);
			} else if (condition instanceof Expr.LocalVariable) {
				return generateCondition(target, (Expr.LocalVariable) condition, environment);
			} else if (condition instanceof Expr.ConstantAccess) {
				return generateCondition(target, (Expr.ConstantAccess) condition, environment);
			} else if (condition instanceof Expr.BinOp) {
				return generateCondition(target, (Expr.BinOp) condition, environment);
			} else if (condition instanceof Expr.UnOp) {
				return generateCondition(target, (Expr.UnOp) condition, environment);
			} else if (condition instanceof Expr.AbstractInvoke) {
				return generateCondition(target, (Expr.AbstractInvoke) condition, environment);
			} else if (condition instanceof Expr.RecordAccess) {
				return generateCondition(target, (Expr.RecordAccess) condition, environment);
			} else if (condition instanceof Expr.Record) {
				return generateCondition(target, (Expr.Record) condition, environment);
			} else if (condition instanceof Expr.Tuple) {
				return generateCondition(target, (Expr.Tuple) condition, environment);
			} else if (condition instanceof Expr.IndexOf) {
				return generateCondition(target, (Expr.IndexOf) condition, environment);
			} else if (condition instanceof Expr.Comprehension) {
				return generateCondition(target, (Expr.Comprehension) condition, environment);
			} else if (condition instanceof Expr.New) {
				return generate((Expr.New) condition, environment);
			} else {				
				syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context, condition);
			}
		} catch (SyntaxError se) {
			throw se;
		} catch (Exception ex) {			
			internalFailure(ex.getMessage(), context, condition, ex);
		}

		return null;
	}

	private Block generateCondition(String target, Expr.Constant c, HashMap<String,Integer> environment) {
		Value.Bool b = (Value.Bool) c.value;
		Block blk = new Block(environment.size());
		if (b.value) {
			blk.append(Code.Goto(target));
		} else {
			// do nout
		}
		return blk;
	}

	private Block generateCondition(String target, Expr.LocalVariable v, 
			HashMap<String, Integer> environment) throws ResolveError {
		
		Block blk = new Block(environment.size());				
		blk.append(Code.Load(Type.T_BOOL, environment.get(v.var)));
		blk.append(Code.Const(Value.V_BOOL(true)),attributes(v));
		blk.append(Code.IfGoto(Type.T_BOOL,Code.COp.EQ, target),attributes(v));			

		return blk;
	}
	
	private Block generateCondition(String target, Expr.ConstantAccess v, 
			HashMap<String, Integer> environment) throws ResolveError {
		
		Block blk = new Block(environment.size());		
		Value val = v.value;
		
		// Obviously, this will be evaluated one way or another.
		blk.append(Code.Const(val));
		blk.append(Code.Const(Value.V_BOOL(true)),attributes(v));
		blk.append(Code.IfGoto(v.result().raw(),Code.COp.EQ, target),attributes(v));			
		return blk;
	}
		
	private Block generateCondition(String target, Expr.BinOp v, HashMap<String,Integer> environment) throws Exception {
		
		Expr.BOp bop = v.op;
		Block blk = new Block(environment.size());

		if (bop == Expr.BOp.OR) {
			blk.append(generateCondition(target, v.lhs, environment));
			blk.append(generateCondition(target, v.rhs, environment));
			return blk;
		} else if (bop == Expr.BOp.AND) {
			String exitLabel = Block.freshLabel();
			blk.append(generateCondition(exitLabel, invert(v.lhs), environment));
			blk.append(generateCondition(target, v.rhs, environment));
			blk.append(Code.Label(exitLabel));
			return blk;
		} else if (bop == Expr.BOp.IS) {
			return generateTypeCondition(target, v, environment);
		}

		Code.COp cop = OP2COP(bop,v);
		
		if (cop == Code.COp.EQ && v.lhs instanceof Expr.LocalVariable
				&& v.rhs instanceof Expr.Constant
				&& ((Expr.Constant) v.rhs).value == Value.V_NULL) {
			// this is a simple rewrite to enable type inference.
			Expr.LocalVariable lhs = (Expr.LocalVariable) v.lhs;
			if (!environment.containsKey(lhs.var)) {
				syntaxError(errorMessage(UNKNOWN_VARIABLE), context, v.lhs);
			}
			int slot = environment.get(lhs.var);								
			blk.append(Code.IfType(v.srcType.raw(), slot, Type.T_NULL, target), attributes(v));
		} else if (cop == Code.COp.NEQ && v.lhs instanceof Expr.LocalVariable
				&& v.rhs instanceof Expr.Constant
				&& ((Expr.Constant) v.rhs).value == Value.V_NULL) {			
			// this is a simple rewrite to enable type inference.
			String exitLabel = Block.freshLabel();
			Expr.LocalVariable lhs = (Expr.LocalVariable) v.lhs;
			if (!environment.containsKey(lhs.var)) {
				syntaxError(errorMessage(UNKNOWN_VARIABLE), context, v.lhs);
			}
			int slot = environment.get(lhs.var);			
			blk.append(Code.IfType(v.srcType.raw(), slot, Type.T_NULL, exitLabel), attributes(v));
			blk.append(Code.Goto(target));
			blk.append(Code.Label(exitLabel));
		} else {
			blk.append(generate(v.lhs, environment));			
			blk.append(generate(v.rhs, environment));					
			blk.append(Code.IfGoto(v.srcType.raw(), cop, target), attributes(v));
		}
		return blk;
	}

	private Block generateTypeCondition(String target, Expr.BinOp v, HashMap<String,Integer> environment) throws Exception {
		Block blk;
		int slot;
		
		if (v.lhs instanceof Expr.LocalVariable) {
			Expr.LocalVariable lhs = (Expr.LocalVariable) v.lhs;
			if (!environment.containsKey(lhs.var)) {
				syntaxError(errorMessage(UNKNOWN_VARIABLE), context, v.lhs);
			}
			slot = environment.get(lhs.var);
			blk = new Block(environment.size());
		} else {
			blk = generate(v.lhs, environment);
			slot = -1;
		}
		
		Expr.TypeVal rhs = (Expr.TypeVal) v.rhs;
		Block constraint = global.generate(rhs.unresolvedType, context);
		if(constraint != null) {
			String exitLabel = Block.freshLabel();			
			Type glb = Type.intersect(v.srcType.raw(), Type.Negation(rhs.type.raw()));
			
			if(glb != Type.T_VOID) {
				// Only put the actual type test in if it is necessary.
				String nextLabel = Block.freshLabel();
				
				// FIXME: should be able to just test the glb here and branch to
				// exit label directly. However, this currently doesn't work
				// because of limitations with intersection of open records.
				
				blk.append(Code.IfType(v.srcType.raw(), slot, rhs.type.raw(), nextLabel),
						attributes(v));		
				blk.append(Code.Goto(exitLabel));
				blk.append(Code.Label(nextLabel));
			}
			// FIXME: I think there's a bug here when slot == -1
			constraint = shiftBlockExceptionZero(environment.size()-1,slot,constraint);
			blk.append(chainBlock(exitLabel,constraint)); 
			blk.append(Code.Goto(target));
			blk.append(Code.Label(exitLabel));
		} else {
			blk.append(Code.IfType(v.srcType.raw(), slot, rhs.type.raw(), target),
					attributes(v));
		}
		return blk;
	}

	private Block generateCondition(String target, Expr.UnOp v, HashMap<String,Integer> environment) {
		Expr.UOp uop = v.op;
		switch (uop) {
		case NOT:
			String label = Block.freshLabel();
			Block blk = generateCondition(label, v.mhs, environment);
			blk.append(Code.Goto(target));
			blk.append(Code.Label(label));
			return blk;
		}
		syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context, v);
		return null;
	}

	private Block generateCondition(String target, Expr.IndexOf v, HashMap<String,Integer> environment) {
		Block blk = generate(v, environment);
		blk.append(Code.Const(Value.V_BOOL(true)),attributes(v));
		blk.append(Code.IfGoto(Type.T_BOOL, Code.COp.EQ, target),attributes(v));
		return blk;
	}
	
	private Block generateCondition(String target, Expr.RecordAccess v, HashMap<String,Integer> environment) {
		Block blk = generate(v, environment);		
		blk.append(Code.Const(Value.V_BOOL(true)),attributes(v));
		blk.append(Code.IfGoto(Type.T_BOOL, Code.COp.EQ, target),attributes(v));		
		return blk;
	}

	private Block generateCondition(String target, Expr.AbstractInvoke v, HashMap<String,Integer> environment) throws ResolveError {
		Block blk = generate((Expr) v, environment);	
		blk.append(Code.Const(Value.V_BOOL(true)),attributes(v));
		blk.append(Code.IfGoto(Type.T_BOOL, Code.COp.EQ, target),attributes(v));
		return blk;
	}

	private Block generateCondition(String target, Expr.Comprehension e,  
			HashMap<String,Integer> environment) {
		
		if (e.cop != Expr.COp.NONE && e.cop != Expr.COp.SOME) {
			syntaxError(errorMessage(INVALID_BOOLEAN_EXPRESSION), context, e);
		}
					
		// Ok, non-boolean case.				
		Block blk = new Block(environment.size());
		ArrayList<Triple<Integer,Integer,Type.EffectiveCollection>> slots = new ArrayList();		
		
		for (Pair<String, Expr> src : e.sources) {
			int srcSlot;
			int varSlot = allocate(src.first(),environment); 
			Nominal srcType = src.second().result();			
			
			if(src.second() instanceof Expr.LocalVariable) {
				// this is a little optimisation to produce slightly better
				// code.
				Expr.LocalVariable v = (Expr.LocalVariable) src.second();
				if(environment.containsKey(v.var)) {					
					srcSlot = environment.get(v.var);
				} else {					
					// fall-back plan ...
					blk.append(generate(src.second(), environment));
					srcSlot = allocate(environment);
					blk.append(Code.Store(srcType.raw(), srcSlot),attributes(e));	
				}
			} else {
				blk.append(generate(src.second(), environment));
				srcSlot = allocate(environment);
				blk.append(Code.Store(srcType.raw(), srcSlot),attributes(e));	
			}			
			slots.add(new Triple(varSlot,srcSlot,srcType.raw()));											
		}
				
		ArrayList<String> labels = new ArrayList<String>();
		String loopLabel = Block.freshLabel();
		
		for (Triple<Integer, Integer, Type.EffectiveCollection> p : slots) {
			Type.EffectiveCollection srcType = p.third();			
			String lab = loopLabel + "$" + p.first();									
			blk.append(Code.Load((Type) srcType, p.second()), attributes(e));			
			blk.append(Code
					.ForAll(srcType, p.first(), lab, Collections.EMPTY_LIST),
					attributes(e));
			labels.add(lab);
		}
								
		if (e.cop == Expr.COp.NONE) {
			String exitLabel = Block.freshLabel();
			blk.append(generateCondition(exitLabel, e.condition, 
					environment));
			for (int i = (labels.size() - 1); i >= 0; --i) {				
				blk.append(Code.End(labels.get(i)));
			}
			blk.append(Code.Goto(target));
			blk.append(Code.Label(exitLabel));
		} else { // SOME			
			blk.append(generateCondition(target, e.condition, 
					environment));
			for (int i = (labels.size() - 1); i >= 0; --i) {
				blk.append(Code.End(labels.get(i)));
			}
		} // ALL, LONE and ONE will be harder					
		
		return blk;
	}
	
	/**
	 * Translate a source-level expression into a wyil bytecode block, using a
	 * given environment mapping named variables to slots. The result of the
	 * expression remains on the wyil stack.
	 * 
	 * @param expression
	 *            --- source-level expression to be translated
	 * @param environment
	 *            --- mapping from variable names to to slot numbers.
	 * @return
	 */
	public Block generate(Expr expression, HashMap<String,Integer> environment) {
		try {
			if (expression instanceof Expr.Constant) {
				return generate((Expr.Constant) expression, environment);
			} else if (expression instanceof Expr.LocalVariable) {
				return generate((Expr.LocalVariable) expression, environment);
			} else if (expression instanceof Expr.ConstantAccess) {
				return generate((Expr.ConstantAccess) expression, environment);
			} else if (expression instanceof Expr.Set) {
				return generate((Expr.Set) expression, environment);
			} else if (expression instanceof Expr.List) {
				return generate((Expr.List) expression, environment);
			} else if (expression instanceof Expr.SubList) {
				return generate((Expr.SubList) expression, environment);
			} else if (expression instanceof Expr.SubString) {
				return generate((Expr.SubString) expression, environment);
			} else if (expression instanceof Expr.BinOp) {
				return generate((Expr.BinOp) expression, environment);
			} else if (expression instanceof Expr.LengthOf) {
				return generate((Expr.LengthOf) expression, environment);
			} else if (expression instanceof Expr.Dereference) {
				return generate((Expr.Dereference) expression, environment);
			} else if (expression instanceof Expr.Convert) {
				return generate((Expr.Convert) expression, environment);
			} else if (expression instanceof Expr.IndexOf) {
				return generate((Expr.IndexOf) expression, environment);
			} else if (expression instanceof Expr.UnOp) {
				return generate((Expr.UnOp) expression, environment);
			} else if (expression instanceof Expr.FunctionCall) {
				return generate((Expr.FunctionCall) expression, true, environment);
			} else if (expression instanceof Expr.MethodCall) {
				return generate((Expr.MethodCall) expression, true, environment);
			} else if (expression instanceof Expr.IndirectFunctionCall) {
				return generate((Expr.IndirectFunctionCall) expression, true, environment);
			} else if (expression instanceof Expr.IndirectMethodCall) {
				return generate((Expr.IndirectMethodCall) expression, true, environment);
			} else if (expression instanceof Expr.IndirectMessageSend) {
				return generate((Expr.IndirectMessageSend) expression, true, environment);
			} else if (expression instanceof Expr.MessageSend) {
				return generate((Expr.MessageSend) expression, true, environment);
			} else if (expression instanceof Expr.Comprehension) {
				return generate((Expr.Comprehension) expression, environment);
			} else if (expression instanceof Expr.RecordAccess) {
				return generate((Expr.RecordAccess) expression, environment);
			} else if (expression instanceof Expr.Record) {
				return generate((Expr.Record) expression, environment);
			} else if (expression instanceof Expr.Tuple) {
				return generate((Expr.Tuple) expression, environment);
			} else if (expression instanceof Expr.Dictionary) {
				return generate((Expr.Dictionary) expression, environment);
			} else if (expression instanceof Expr.FunctionOrMethodOrMessage) {
				return generate((Expr.FunctionOrMethodOrMessage) expression, environment);
			} else if (expression instanceof Expr.New) {
				return generate((Expr.New) expression, environment);
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

	public Block generate(Expr.MessageSend fc, boolean retval,
			HashMap<String, Integer> environment) throws ResolveError {
		Block blk = new Block(environment.size());

		blk.append(generate(fc.qualification, environment));
		
		for (Expr e : fc.arguments) {
			blk.append(generate(e, environment));
		}
		
		blk.append(Code.Send(fc.messageType.raw(), fc.nid, fc.synchronous, retval),
				attributes(fc));		

		return blk;
	}
	
	public Block generate(Expr.MethodCall fc, boolean retval,
			HashMap<String, Integer> environment) throws ResolveError {
		Block blk = new Block(environment.size());

		for (Expr e : fc.arguments) {
			blk.append(generate(e, environment));
		}

		blk.append(Code.Invoke(fc.methodType.raw(), fc.nid(), retval), attributes(fc));

		return blk;
	}
	
	public Block generate(Expr.FunctionCall fc, boolean retval,
			HashMap<String, Integer> environment) throws ResolveError {
		Block blk = new Block(environment.size());

		for (Expr e : fc.arguments) {
			blk.append(generate(e, environment));
		}

		blk.append(Code.Invoke(fc.functionType.raw(), fc.nid(), retval), attributes(fc));

		return blk;
	}
	
	public Block generate(Expr.IndirectFunctionCall fc, boolean retval,
			HashMap<String, Integer> environment) throws ResolveError {
		Block blk = new Block(environment.size());

		blk.append(generate(fc.src,environment));
		
		for (Expr e : fc.arguments) {
			blk.append(generate(e, environment));
		}

		blk.append(Code.IndirectInvoke(fc.functionType.raw(), retval), attributes(fc));

		return blk;
	}
	
	public Block generate(Expr.IndirectMethodCall fc, boolean retval,
			HashMap<String, Integer> environment) throws ResolveError {
		Block blk = new Block(environment.size());

		blk.append(generate(fc.src,environment));
		
		for (Expr e : fc.arguments) {
			blk.append(generate(e, environment));
		}

		blk.append(Code.IndirectInvoke(fc.methodType.raw(), retval), attributes(fc));

		return blk;
	}
	
	public Block generate(Expr.IndirectMessageSend fc, boolean retval,
			HashMap<String, Integer> environment) throws ResolveError {
		Block blk = new Block(environment.size());

		blk.append(generate(fc.src,environment));
		
		blk.append(generate(fc.receiver,environment));
		
		for (Expr e : fc.arguments) {
			blk.append(generate(e, environment));
		}

		blk.append(Code.IndirectSend(fc.messageType.raw(), fc.synchronous, retval), attributes(fc));

		return blk;
	}
	
	private Block generate(Expr.Constant c, HashMap<String,Integer> environment) {
		Block blk = new Block(environment.size());
		blk.append(Code.Const(c.value), attributes(c));		
		return blk;
	}

	private Block generate(Expr.FunctionOrMethodOrMessage s, HashMap<String,Integer> environment) {						
		Block blk = new Block(environment.size());
		blk.append(Code.Const(Value.V_FUN(s.nid, s.type.raw())),
				attributes(s));
		return blk;
	}
	
	private Block generate(Expr.ConstantAccess v, HashMap<String,Integer> environment) throws ResolveError {						
		Block blk = new Block(environment.size());
		Value val = v.value;				
		blk.append(Code.Const(val),attributes(v));
		return blk;
	}
	
	private Block generate(Expr.LocalVariable v, HashMap<String,Integer> environment) throws ResolveError {
		
		if (environment.containsKey(v.var)) {
			Block blk = new Block(environment.size());
			blk.append(Code.Load(v.result().raw(), environment.get(v.var)), attributes(v));
			return blk;
		} else {
			syntaxError(errorMessage(VARIABLE_POSSIBLY_UNITIALISED), context,
					v);
		}
		
		// must be an error
		syntaxError("unknown variable \"" + v.var + "\"", context,v);
		return null;
	}

	private Block generate(Expr.UnOp v, HashMap<String,Integer> environment) {
		Block blk = generate(v.mhs,  environment);	
		switch (v.op) {
		case NEG:
			blk.append(Code.Negate(v.result().raw()), attributes(v));
			break;
		case INVERT:
			blk.append(Code.Invert(v.result().raw()), attributes(v));
			break;
		case NOT:
			String falseLabel = Block.freshLabel();
			String exitLabel = Block.freshLabel();
			blk = generateCondition(falseLabel, v.mhs, environment);
			blk.append(Code.Const(Value.V_BOOL(true)), attributes(v));
			blk.append(Code.Goto(exitLabel));
			blk.append(Code.Label(falseLabel));
			blk.append(Code.Const(Value.V_BOOL(false)), attributes(v));
			blk.append(Code.Label(exitLabel));
			break;							
		default:
			// should be dead-code
			internalFailure("unexpected unary operator encountered", context, v);
			return null;
		}
		return blk;
	}
	
	private Block generate(Expr.LengthOf v, HashMap<String,Integer> environment) {
		Block blk = generate(v.src,  environment);	
		blk.append(Code.LengthOf(v.srcType.raw()), attributes(v));
		return blk;
	}
			
	private Block generate(Expr.Dereference v, HashMap<String,Integer> environment) {
		Block blk = generate(v.src,  environment);	
		blk.append(Code.Dereference(v.srcType.raw()), attributes(v));
		return blk;
	}	
	
	private Block generate(Expr.IndexOf v, HashMap<String,Integer> environment) {
		Block blk = new Block(environment.size());
		blk.append(generate(v.src, environment));
		blk.append(generate(v.index, environment));
		blk.append(Code.IndexOf(v.srcType.raw()),attributes(v));
		return blk;
	}
	
	private Block generate(Expr.Convert v, HashMap<String,Integer> environment) {
		Block blk = new Block(environment.size());
		blk.append(generate(v.expr, environment));		
		Type from = v.expr.result().raw();
		Type to = v.result().raw();
		// TODO: include constraints
		blk.append(Code.Convert(from,to),attributes(v));
		return blk;
	}
	
	private Block generate(Expr.BinOp v, HashMap<String,Integer> environment) throws Exception {

		// could probably use a range test for this somehow
		if (v.op == Expr.BOp.EQ || v.op == Expr.BOp.NEQ || v.op == Expr.BOp.LT
				|| v.op == Expr.BOp.LTEQ || v.op == Expr.BOp.GT || v.op == Expr.BOp.GTEQ
				|| v.op == Expr.BOp.SUBSET || v.op == Expr.BOp.SUBSETEQ
				|| v.op == Expr.BOp.ELEMENTOF || v.op == Expr.BOp.AND || v.op == Expr.BOp.OR) {
			String trueLabel = Block.freshLabel();
			String exitLabel = Block.freshLabel();
			Block blk = generateCondition(trueLabel, v, environment);
			blk.append(Code.Const(Value.V_BOOL(false)), attributes(v));			
			blk.append(Code.Goto(exitLabel));
			blk.append(Code.Label(trueLabel));
			blk.append(Code.Const(Value.V_BOOL(true)), attributes(v));				
			blk.append(Code.Label(exitLabel));			
			return blk;
		}

		Expr.BOp bop = v.op;
		Block blk = new Block(environment.size());
		blk.append(generate(v.lhs, environment));
		blk.append(generate(v.rhs, environment));
		Type result = v.result().raw();
		
		switch(bop) {		
		case UNION:
			blk.append(Code.SetUnion((Type.EffectiveSet)result,Code.OpDir.UNIFORM),attributes(v));			
			return blk;			
		case INTERSECTION:
			blk.append(Code.SetIntersect((Type.EffectiveSet)result,Code.OpDir.UNIFORM),attributes(v));
			return blk;			
		case DIFFERENCE:
			blk.append(Code.SetDifference((Type.EffectiveSet)result,Code.OpDir.UNIFORM),attributes(v));
			return blk;			
		case LISTAPPEND:
			blk.append(Code.ListAppend((Type.EffectiveList)result,Code.OpDir.UNIFORM),attributes(v));
			return blk;	
		case STRINGAPPEND:
			Type lhs = v.lhs.result().raw();
			Type rhs = v.rhs.result().raw();
			Code.OpDir dir;
			if(lhs == Type.T_STRING && rhs == Type.T_STRING) {
				dir = Code.OpDir.UNIFORM;
			} else if(lhs == Type.T_STRING && Type.isSubtype(Type.T_CHAR, rhs)) {
				dir = Code.OpDir.LEFT;
			} else if(rhs == Type.T_STRING && Type.isSubtype(Type.T_CHAR, lhs)) {
				dir = Code.OpDir.RIGHT;
			} else {
				// this indicates that one operand must be explicitly converted
				// into a string.
				dir = Code.OpDir.UNIFORM;
			}
			blk.append(Code.StringAppend(dir),attributes(v));
			return blk;	
		default:
			blk.append(Code.BinOp(result, OP2BOP(bop,v)),attributes(v));			
			return blk;
		}		
	}

	private Block generate(Expr.Set v, HashMap<String,Integer> environment) {
		Block blk = new Block(environment.size());		
		int nargs = 0;
		for (Expr e : v.arguments) {				
			nargs++;
			blk.append(generate(e, environment));
		}
		blk.append(Code.NewSet(v.type.raw(),nargs),attributes(v));		
		return blk;
	}
	
	private Block generate(Expr.List v, HashMap<String,Integer> environment) {
		Block blk = new Block(environment.size());		
		int nargs = 0;
		for (Expr e : v.arguments) {				
			nargs++;
			blk.append(generate(e, environment));
		}
		blk.append(Code.NewList(v.type.raw(),nargs),attributes(v));		
		return blk;
	}
	
	private Block generate(Expr.SubList v, HashMap<String, Integer> environment) {
		Block blk = new Block(environment.size());
		blk.append(generate(v.src, environment));
		blk.append(generate(v.start, environment));
		blk.append(generate(v.end, environment));
		blk.append(Code.SubList(v.type.raw()), attributes(v));
		return blk;
	}
	
	private Block generate(Expr.SubString v, HashMap<String, Integer> environment) {
		Block blk = new Block(environment.size());
		blk.append(generate(v.src, environment));
		blk.append(generate(v.start, environment));
		blk.append(generate(v.end, environment));
		blk.append(Code.SubString(), attributes(v));
		return blk;
	}
	
	private Block generate(Expr.Comprehension e, HashMap<String,Integer> environment) {

		// First, check for boolean cases which are handled mostly by
		// generateCondition.
		if (e.cop == Expr.COp.SOME || e.cop == Expr.COp.NONE) {
			String trueLabel = Block.freshLabel();
			String exitLabel = Block.freshLabel();
			int freeSlot = allocate(environment);
			Block blk = generateCondition(trueLabel, e, environment);					
			blk.append(Code.Const(Value.V_BOOL(false)), attributes(e));
			blk.append(Code.Store(Type.T_BOOL,freeSlot),attributes(e));			
			blk.append(Code.Goto(exitLabel));
			blk.append(Code.Label(trueLabel));
			blk.append(Code.Const(Value.V_BOOL(true)), attributes(e));
			blk.append(Code.Store(Type.T_BOOL,freeSlot),attributes(e));
			blk.append(Code.Label(exitLabel));
			blk.append(Code.Load(Type.T_BOOL,freeSlot),attributes(e));
			return blk;
		}

		// Ok, non-boolean case.				
		Block blk = new Block(environment.size());
		ArrayList<Triple<Integer,Integer,Type.EffectiveCollection>> slots = new ArrayList();		
		
		for (Pair<String, Expr> p : e.sources) {
			int srcSlot;
			int varSlot = allocate(p.first(),environment); 
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
					blk.append(generate(src, environment));
					srcSlot = allocate(environment);				
					blk.append(Code.Store(rawSrcType, srcSlot),attributes(e));	
				}
			} else {
				blk.append(generate(src, environment));
				srcSlot = allocate(environment);
				blk.append(Code.Store(rawSrcType, srcSlot),attributes(e));	
			}			
			slots.add(new Triple(varSlot,srcSlot,rawSrcType));											
		}
		
		Type resultType;
		int resultSlot = allocate(environment);
		
		if (e.cop == Expr.COp.LISTCOMP) {
			resultType = e.type.raw();
			blk.append(Code.NewList((Type.List) resultType,0), attributes(e));
			blk.append(Code.Store((Type.List) resultType,resultSlot),attributes(e));
		} else {
			resultType = e.type.raw();
			blk.append(Code.NewSet((Type.Set) resultType,0), attributes(e));
			blk.append(Code.Store((Type.Set) resultType,resultSlot),attributes(e));			
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
			String target = loopLabel + "$" + p.first();
			blk.append(Code.Load((Type) p.third(), p.second()), attributes(e));
			blk.append(Code.ForAll(p.third(), p.first(), target,
					Collections.EMPTY_LIST), attributes(e));
			labels.add(target);
		}
		
		if (e.condition != null) {
			blk.append(generateCondition(continueLabel, invert(e.condition),
					environment));
		}
		
		blk.append(Code.Load(resultType,resultSlot),attributes(e));
		blk.append(generate(e.value, environment));
		// FIXME: following broken for list comprehensions
		blk.append(Code.SetUnion((Type.Set) resultType, Code.OpDir.LEFT),attributes(e));
		blk.append(Code.Store(resultType,resultSlot),attributes(e));
			
		if(e.condition != null) {
			blk.append(Code.Label(continueLabel));			
		} 

		for (int i = (labels.size() - 1); i >= 0; --i) {
			blk.append(Code.End(labels.get(i)));
		}

		blk.append(Code.Load(resultType,resultSlot),attributes(e));
		
		return blk;
	}

	private Block generate(Expr.Record sg, HashMap<String,Integer> environment) {
		Block blk = new Block(environment.size());
		ArrayList<String> keys = new ArrayList<String>(sg.fields.keySet());
		Collections.sort(keys);
		for (String key : keys) {		
			blk.append(generate(sg.fields.get(key), environment));
		}		
		blk.append(Code.NewRecord(sg.result().raw()), attributes(sg));
		return blk;
	}

	private Block generate(Expr.Tuple sg, HashMap<String,Integer> environment) {		
		Block blk = new Block(environment.size());		
		for (Expr e : sg.fields) {									
			blk.append(generate(e, environment));
		}
		blk.append(Code.NewTuple(sg.result().raw(),sg.fields.size()),attributes(sg));
		return blk;		
	}

	private Block generate(Expr.Dictionary sg, HashMap<String,Integer> environment) {		
		Block blk = new Block(environment.size());		
		for (Pair<Expr,Expr> e : sg.pairs) {			
			blk.append(generate(e.first(), environment));
			blk.append(generate(e.second(), environment));
		}
		blk.append(Code.NewDict(sg.result().raw(),sg.pairs.size()),attributes(sg));
		return blk;
	}
	
	private Block generate(Expr.RecordAccess sg, HashMap<String,Integer> environment) {
		Block lhs = generate(sg.src, environment);		
		lhs.append(Code.FieldLoad(sg.srcType.raw(),sg.name), attributes(sg));
		return lhs;
	}
	
	private Block generate(Expr.New expr,
			HashMap<String, Integer> environment) throws ResolveError {
		Block blk = generate(expr.expr,environment);
		blk.append(Code.New(expr.type.raw()));
		return blk;
	}
	
	private Code.BOp OP2BOP(Expr.BOp bop, SyntacticElement elem) {
		switch (bop) {
		case ADD:
			return Code.BOp.ADD;
		case SUB:
			return Code.BOp.SUB;		
		case MUL:
			return Code.BOp.MUL;
		case DIV:
			return Code.BOp.DIV;
		case REM:
			return Code.BOp.REM;
		case RANGE:
			return Code.BOp.RANGE;
		case BITWISEAND:
			return Code.BOp.BITWISEAND;
		case BITWISEOR:
			return Code.BOp.BITWISEOR;
		case BITWISEXOR:
			return Code.BOp.BITWISEXOR;
		case LEFTSHIFT:
			return Code.BOp.LEFTSHIFT;
		case RIGHTSHIFT:
			return Code.BOp.RIGHTSHIFT;
		}
		syntaxError(errorMessage(INVALID_BINARY_EXPRESSION), context, elem);
		return null;
	}
	
	private Code.COp OP2COP(Expr.BOp bop, SyntacticElement elem) {
		switch (bop) {
		case EQ:
			return Code.COp.EQ;
		case NEQ:
			return Code.COp.NEQ;
		case LT:
			return Code.COp.LT;
		case LTEQ:
			return Code.COp.LTEQ;
		case GT:
			return Code.COp.GT;
		case GTEQ:
			return Code.COp.GTEQ;
		case SUBSET:
			return Code.COp.SUBSET;
		case SUBSETEQ:
			return Code.COp.SUBSETEQ;
		case ELEMENTOF:
			return Code.COp.ELEMOF;
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
	
	private static Block chainBlock(String target, Block blk) {	
		Block nblock = new Block(blk.numInputs());
		for (Block.Entry e : blk) {
			if (e.code instanceof Code.Fail) {
				nblock.append(Code.Goto(target), e.attributes());
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
