// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyil.transforms;

import static wyil.util.ErrorMessages.internalFailure;

import java.math.BigInteger;
import java.util.*;

import wybs.lang.Builder;
import wycc.lang.SyntaxError;
import wycc.lang.Transform;
import wycc.util.Pair;
import wyautl.util.BigRational;
import wyfs.lang.Path;
import wyil.lang.*;
import wyil.util.*;
import wyil.util.dfa.ForwardFlowAnalysis;

/**
 * <p>
 * Propagates constants throughout the bytecodes making up the functions,
 * methods and invariants of a WyIL file. This involves rewriting bytecodes
 * where appropriate, and possibly even removing them if they become redundant.
 * Consider the following simple bytecode sequence:
 * </p>
 *
 * <pre>
 * const %0 = 1
 * const %1 = 2
 * add %2 = %0, %2
 * assign %3 = %2
 * </pre>
 *
 * <p>
 * In this example, the constant propagation analysis will track the fact that
 * registers <code>%0</code> and <code>%1</code> hold constants. As such, it
 * will determine that register <code>%2</code> holds a constant (<code>3</code>
 * in this case). Hence, it will conclude that register <code>%3</code> holds a
 * constant. Therefore, it will rewrite the above bytecodes into the following:
 * </p>
 *
 * <pre>
 * const %0 = 1
 * const %1 = 2
 * const %2 = 3
 * const %3 = 3
 * </pre>
 *
 * <p>
 * Note that it doesn't eliminate the assignments to registers <code>%0</code> -
 * <code>%2</code> (even if these are no longer live). The reason for this is
 * simply that the constant propagation phase does not have access to liveness
 * information. Such redundant bytecodes will be eliminated by later phases.
 * </p>
 *
 *
 *
 * @author David J. Pearce
 *
 */
public class ConstantPropagation extends ForwardFlowAnalysis<ConstantPropagation.Env> implements Transform<WyilFile> {
	private static final HashMap<CodeBlock.Index,Rewrite> rewrites = new HashMap<CodeBlock.Index,Rewrite>();

	/**
	 * Determines whether constant propagation is enabled or not.
	 */
	private boolean enabled = getEnable();

	public ConstantPropagation(Builder builder) {

	}

	@Override
	public void apply(WyilFile module) {
		if(enabled) {
			super.apply(module);
		}
	}

	public static String describeEnable() {
		return "Enable/disable constant propagation";
	}

	public static boolean getEnable() {
		return true; // default value
	}

	public void setEnable(boolean flag) {
		this.enabled = flag;
	}

	@Override
	public WyilFile.Type propagate(WyilFile.Type type) {
		AttributedCodeBlock invariant = type.invariant();
		if (invariant != null) {
			invariant = propagate(invariant);
			return new WyilFile.Type(type.modifiers(), type.name(),
					type.type(), invariant, type.attributes());
		}
		return type;
	}

	public Env initialStore() {
		Env environment = new Env();
		int nvars = rootBlock.numSlots();

		for (int i=0; i != nvars; ++i) {
			environment.add(null);
		}

		return environment;
	}

	@Override
	public WyilFile.FunctionOrMethod propagate(WyilFile.FunctionOrMethod method) {
		ArrayList<AttributedCodeBlock> requires = new ArrayList<>(
				method.precondition());
		for (int i = 0; i != requires.size(); ++i) {
			AttributedCodeBlock tmp = propagate(requires.get(i));
			requires.set(i, tmp);
		}
		ArrayList<AttributedCodeBlock> ensures = new ArrayList<>(
				method.postcondition());
		for (int i = 0; i != ensures.size(); ++i) {
			AttributedCodeBlock tmp = propagate(ensures.get(i));
			ensures.set(i, tmp);
		}

		AttributedCodeBlock body = method.body();
		if (body != null) {
			body = propagate(body);
		}

		return new WyilFile.FunctionOrMethod(method.modifiers(), method.name(),
				method.type(), body, requires, ensures, method.attributes());
	}

	public AttributedCodeBlock propagate(AttributedCodeBlock body) {
		rootBlock = body;
		stores = new HashMap<String,Env>();
		rewrites.clear();

		// TODO: propagate constants through pre- and post-conditions.

		Env environment = initialStore();
		propagate(null, body, environment);

		// At this point, we apply the inserts		
		AttributedCodeBlock nbody = new AttributedCodeBlock();
		for(int i=0;i!=body.size();++i) {
			Rewrite rewrite = rewrites.get(i);
			if(rewrite != null) {
				nbody.add(rewrite.code);
			} else {
				nbody.add(body.get(i));
			}
		}

		return nbody;
	}

	@Override
	public Env propagate(CodeBlock.Index index, Code code, Env environment) {

		// reset the rewrites for this code, in case it changes
		rewrites.remove(index);

		environment = (Env) environment.clone();

		if(code instanceof Codes.BinaryOperator) {
			infer(index,(Codes.BinaryOperator)code,environment);
		} else if(code instanceof Codes.Convert) {
			infer(index,(Codes.Convert)code,environment);
		} else if(code instanceof Codes.Const) {
			infer(index, (Codes.Const)code,environment);
		} else if(code instanceof Codes.Debug) {
			infer(index, (Codes.Debug)code,environment);
		} else if(code instanceof Codes.AssertOrAssume) {
			infer(index, (Codes.AssertOrAssume)code,environment);
		} else if(code instanceof Codes.Fail) {
			infer(index,(Codes.Fail)code,environment);
		} else if(code instanceof Codes.FieldLoad) {
			infer(index,(Codes.FieldLoad)code,environment);
		} else if(code instanceof Codes.IndirectInvoke) {
			infer(index, (Codes.IndirectInvoke)code,environment);
		} else if(code instanceof Codes.Invoke) {
			infer(index, (Codes.Invoke)code,environment);
		} else if(code instanceof Codes.Invert) {
			infer(index,(Codes.Invert)code,environment);
		} else if(code instanceof Codes.Lambda) {
			// skip
		} else if(code instanceof Codes.Label) {
			// skip
		} else if(code instanceof Codes.LengthOf) {
			infer(index,(Codes.LengthOf)code,environment);
		} else if(code instanceof Codes.IndexOf) {
			infer(index,(Codes.IndexOf)code,environment);
		} else if(code instanceof Codes.Assign) {
			infer(index,(Codes.Assign)code,environment);
		} else if(code instanceof Codes.Update) {
			infer(index, (Codes.Update)code,environment);
		} else if(code instanceof Codes.NewArray) {
			infer(index,(Codes.NewArray)code,environment);
		} else if(code instanceof Codes.NewRecord) {
			infer(index,(Codes.NewRecord)code,environment);
		} else if(code instanceof Codes.UnaryOperator) {
			infer(index,(Codes.UnaryOperator)code,environment);
		} else if(code instanceof Codes.Dereference) {
			infer(index,(Codes.Dereference)code,environment);
		} else if(code instanceof Codes.Return) {
			infer(index, (Codes.Return)code,environment);
		} else if(code instanceof Codes.Nop) {
			// skip
		} else if(code instanceof Codes.NewObject) {
			infer(index,(Codes.NewObject)code,environment);
		} else {
			throw new SyntaxError.InternalFailure("unknown: " + code.getClass().getName(),filename,0,-1);
		}

		return environment;
	}

	public void infer(CodeBlock.Index index, Codes.AssertOrAssume code, Env environment) {
	}

	public void infer(CodeBlock.Index index, Codes.BinaryOperator code, Env environment) {
		Constant lhs = environment.get(code.operand(0));
		Constant rhs = environment.get(code.operand(1));
		Constant result = null;

		if (lhs instanceof Constant.Decimal && rhs instanceof Constant.Decimal) {
			Constant.Decimal lnum = (Constant.Decimal) lhs;
			Constant.Decimal rnum = (Constant.Decimal) rhs;

			switch (code.kind) {
			case ADD: {
				result = lnum.add(rnum);
				break;
			}
			case SUB: {
				result = lnum.subtract(rnum);
				break;
			}
			case MUL: {
				result = lnum.multiply(rnum);
				break;
			}
			case DIV: {
				result = lnum.divide(rnum);
				break;
			}
			}
		} else if (lhs instanceof Constant.Integer
				&& rhs instanceof Constant.Integer) {
			Constant.Integer lnum = (Constant.Integer) lhs;
			Constant.Integer rnum = (Constant.Integer) rhs;

			switch (code.kind) {
			case ADD: {
				result = lnum.add(rnum);
				break;
			}
			case SUB: {
				result = lnum.subtract(rnum);
				break;
			}
			case MUL: {
				result = lnum.multiply(rnum);
				break;
			}
			case DIV: {
				result = lnum.divide(rnum);
				break;
			}
			case REM: {
				result = lnum.remainder(rnum);
				break;
			}			
			}
		}

		assign(code.target(), result, environment, index);
	}

	public void infer(CodeBlock.Index index, Codes.Convert code,
			Env environment) {
		// TODO: implement this
		Constant val = environment.get(code.operand(0));

		invalidate(code.target(),environment);
	}

	public void infer(CodeBlock.Index index, Codes.Const code,
			Env environment) {
		invalidate(code.target(),environment);
	}

	public void infer(CodeBlock.Index index, Codes.Debug code,  Env environment) {
	}

	public void infer(CodeBlock.Index index, Codes.Fail code,
			Env environment) {
	}

	public void infer(CodeBlock.Index index, Codes.FieldLoad code,
			Env environment) {
		Constant src = environment.get(code.operand(0));

		Constant result = null;
		if (src instanceof Constant.Record) {
			Constant.Record rec = (Constant.Record) src;
			result = rec.values.get(code.field);
		}

		assign(code.target(),result,environment,index);
	}

	public void infer(CodeBlock.Index index, Codes.IndirectInvoke code,
			Env environment) {

		// TODO: in principle we can do better here in the case that the target
		// is a constant. This seems pretty unlikely though ...

		for(int target : code.targets()) {		
			invalidate(target,environment);
		}
	}

	public void infer(CodeBlock.Index index, Codes.Invoke code,
			Env environment) {

		for(int target : code.targets()) {		
			invalidate(target,environment);
		}		
	}

	public void infer(CodeBlock.Index index, Codes.Lambda code,
			Env environment) {
		// For now, don't do anything!
		assign(code.target(),null,environment,index);
	}

	public void infer(CodeBlock.Index index, Codes.LengthOf code, Env environment) {
		Constant val = environment.get(code.operand(0));
		Constant.Array list = (Constant.Array) val;
		Constant result = Constant.V_INTEGER(BigInteger.valueOf(list.values.size()));
		assign(code.target(), result, environment, index);
	}

	public void infer(CodeBlock.Index index, Codes.IndexOf code,
			Env environment) {
		Constant src = environment.get(code.operand(0));
		Constant idx = environment.get(code.operand(1));
		Constant result = null;
		if (idx instanceof Constant.Decimal && src instanceof Constant.Array) {
			Constant.Decimal num = (Constant.Decimal) idx;
			Constant.Array list = (Constant.Array) src;
			if (num.value.scale() <= 0) {
				int i = num.value.intValue();
				if (BigRational.valueOf(i).equals(num.value) && i >= 0
						&& i < list.values.size()) {
					result = list.values.get(i);
				}
			}
		} 

		assign(code.target(),result,environment,index);
	}

	public void infer(CodeBlock.Index index, Codes.Assign code,
			Env environment) {
		Constant result = environment.get(code.operand(0));
		assign(code.target(),result,environment,index);
	}

	public void infer(CodeBlock.Index index, Codes.Update code,
			Env environment) {
		// TODO: implement this!
		invalidate(code.target(),environment);
	}

	public void infer(CodeBlock.Index index, Codes.NewRecord code,
			Env environment) {
		HashMap<String, Constant> values = new HashMap<String, Constant>();
		ArrayList<String> keys = new ArrayList<String>(code.type().keys());
		Collections.sort(keys);
		boolean isValue = true;
		int[] code_operands = code.operands();
		for (int i=0;i!=code_operands.length;++i) {
			Constant val = environment.get(code_operands[i]);
			if (isRealConstant(val)) {
				values.put(keys.get(i), val);
			} else {
				isValue = false;
			}
		}

		Constant result = null;
		if (isValue) {
			result = Constant.V_RECORD(values);
		}

		assign(code.target(), result, environment, index);
	}

	public void infer(CodeBlock.Index index, Codes.NewArray code,
			Env environment) {
		ArrayList<Constant> values = new ArrayList<Constant>();

		boolean isValue = true;
		int[] code_operands = code.operands();
		for (int i = 0; i != code_operands.length; ++i) {
			Constant val = environment.get(code_operands[i]);
			if (isRealConstant(val)) {
				values.add(val);
			} else {
				isValue = false;
			}
		}

		Constant result = null;
		if (isValue) {
			result = Constant.V_ARRAY(values);
		}
		assign(code.target(),result,environment,index);
	}

	public void infer(CodeBlock.Index index, Codes.Return code, Env environment) {
	}

	public void infer(CodeBlock.Index index, Codes.Invert code, Env environment) {
		Constant val = environment.get(code.operand(0));
		Constant result = null;

		if (val instanceof Constant.Byte) {
			Constant.Byte num = (Constant.Byte) val;
			result = Constant.V_BYTE((byte) ~num.value);
		}

		assign(code.target(), result, environment, index);
	}

	public void infer(CodeBlock.Index index, Codes.UnaryOperator code, Env environment) {
		// needs to be updated to support numerator and denominator
		Constant val = environment.get(code.operand(0));
		Constant result = null;

		switch (code.kind) {
		case NEG:
			if (val instanceof Constant.Decimal) {
				Constant.Decimal num = (Constant.Decimal) val;
				result = Constant.V_DECIMAL(num.value.negate());
			} else if (val instanceof Constant.Integer) {
				Constant.Integer num = (Constant.Integer) val;
				result = Constant.V_INTEGER(num.value.negate());
			}
		}

		assign(code.target(), result, environment, index);
	}

	public void infer(CodeBlock.Index index, Codes.NewObject code,
			Env environment) {
		invalidate(code.target(), environment);
	}

	public void infer(CodeBlock.Index index, Codes.Dereference code, Env environment) {
		invalidate(code.target(), environment);
	}

	@Override
	public Pair<Env, Env> propagate(CodeBlock.Index index, Codes.If code, Env environment) {
		environment = (Env) environment.clone();
		return new Pair<>(environment, environment);
	}

	@Override
	public Pair<Env, Env> propagate(CodeBlock.Index index, Codes.IfIs code, Env environment) {
		environment = (Env) environment.clone();
		return new Pair<>(environment, environment);
	}

	@Override
	public List<Env> propagate(CodeBlock.Index index, Codes.Switch code, Env environment) {
		environment = (Env) environment.clone();

		ArrayList<Env> stores = new ArrayList<>();
		for (int i = 0; i != code.branches.size(); ++i) {
			stores.add(environment);
		}

		return stores;
	}

	@Override
	public Env propagate(CodeBlock.Index index, Codes.Loop loop, Env environment) {

		environment = new Env(environment);

		if(loop instanceof Codes.Quantify) {
			Codes.Quantify fall = (Codes.Quantify) loop;

			// TO DO: could unroll loop if src collection is a value.
			invalidate(fall.indexOperand,environment);
		}

		// Now, kill every variable which is modified in the loop. This is a
		// safety precaution, and it's possible we could do better here in some
		// circumstances (e.g. by unrolling the loop).

		for(int modifiedRegister : loop.modifiedOperands) {
			invalidate(modifiedRegister,environment);
		}

		Env oldEnv = null;
		Env newEnv = environment;
		CodeBlock block = (CodeBlock) loop;

		do {
			// iterate until a fixed point reached
			oldEnv = newEnv;
			newEnv = propagate(index, block, oldEnv);
			newEnv = join(environment, newEnv);
		} while (!newEnv.equals(oldEnv));

		return newEnv;
	}

	/**
	 * Invalidate the record for a given register in the environment.
	 * Specifically, if that register was considered to hold a constant
	 * beforehand, it no longer will be.
	 *
	 * @param register
	 *            Register being invalidated
	 * @param environment
	 *            Environment in which it is to be invalidated
	 */
	public void invalidate(int register, Env environment) {
		environment.set(register, null);
	}

	/**
	 * Update the environment to record an assignment of a constant to a given
	 * register.
	 *
	 * @param register
	 *            Register being assigned
	 * @param constant
	 *            Constant being assigned. This may be null, in which case it
	 *            means the register is assigned something which isn't a
	 *            constant
	 * @param environment
	 *            Environment recording which variables are constants
	 * @param index
	 *            Index of bytecode in question from root Block
	 */
	public void assign(int register, Constant constant, Env environment,
			CodeBlock.Index index) {
		environment.set(register, constant);

		if (isRealConstant(constant)) {
			Code code = Codes.Const(register, constant);
			rewrites.put(index, new Rewrite(code));
		}
	}

	public static boolean isRealConstant(Constant c) {
		return c != null && !(c instanceof Alias);
	}

	public Env join(Env env1, Env env2) {
		if (env2 == null) {
			return env1;
		} else if (env1 == null) {
			return env2;
		}
		Env env = new Env();
		for (int i = 0; i != Math.min(env1.size(), env2.size()); ++i) {
			Constant mt = env1.get(i);
			Constant ot = env2.get(i);
			if (ot instanceof Constant && mt instanceof Constant && ot.equals(mt)) {
				env.add(mt);
			} else {
				env.add(null);
			}
		}
		return env;
	}

	public final static class Env extends ArrayList<Constant> {
		public Env() {
		}
		public Env(Collection<Constant> v) {
			super(v);
		}
		public Env clone() {
			return new Env(this);
		}
	}

	private static class Rewrite {
		public final Code code;

		public Rewrite(Code rewrite) {
			this.code = rewrite;
		}
	}

	private static class Alias extends Constant {
		public final int reg;

		public Alias(int reg) {
			this.reg = reg;
		}

		public int hashCode() {
			return reg;
		}

		public boolean equals(Object o) {
			if(o instanceof Alias) {
				Alias a = (Alias) o;
				return reg == a.reg;
			}
			return false;
		}

		public wyil.lang.Type type() {
			return wyil.lang.Type.T_ANY;
		}

		public int compareTo(Constant c) {
			if(c instanceof Alias) {
				Alias a = (Alias) c;
				if(reg < a.reg) {
					return -1;
				} else if(reg == a.reg) {
					return 0;
				}
			}
			return 1;
		}
	}
}
