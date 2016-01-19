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

import java.util.*;

import static wycc.lang.SyntaxError.*;
import static wyil.util.ErrorMessages.*;
import wybs.lang.Builder;
import wycc.lang.Transform;
import wycc.util.Pair;
import wyfs.lang.Path;
import wyil.lang.Code;
import wyil.lang.CodeBlock;
import wyil.lang.Codes;
import wyil.lang.WyilFile;
import wyil.lang.Type;
import wyil.util.AttributedCodeBlock;
import wyil.util.dfa.*;

/**
 * <p>
 * Implements a classic live variables analysis to enable efficient reference
 * counting. Compound structures in Whiley (e.g. lists, sets, records, etc) are
 * <i>reference counted</i>. This means we count the number of aliases to them
 * in the heap and on the stack. Then, when a compound structure is updated, we
 * can perform an <i>inplace update</i> if the reference count is 1; otherwise,
 * we have to clone the structure. Cloning is potentially expensive, and we want
 * to avoid it as much as possible. Therefore, as soon as a variable is no
 * longer live, we should decrement its reference count. This is signalled using
 * the special <code>void</code> bytecode which, literally, "voids" a given
 * register thereby allowing us to release it.
 * </p>
 * <p>
 * The purpose of this class is to determine when a variable is live, and when
 * it is not. The live variables analysis operates as a backwards analysis,
 * propagating information from variable uses (i.e. <code>load</code> bytecodes)
 * back to their definitions (i.e. <code>store</code> bytecodes). For more
 * information on Live Variables Analysis, see <a
 * href="http://en.wikipedia.org/wiki/Live_variable_analysis">the Wikipedia
 * page</a>.
 * </p>
 *
 * @author David J. Pearce, 2011
 *
 */
public class LiveVariablesAnalysis extends BackwardFlowAnalysis<LiveVariablesAnalysis.Env> implements Transform<WyilFile> {
	private static final HashMap<CodeBlock.Index,Code> rewrites = new HashMap<CodeBlock.Index,Code>();

	/**
	 * Determines whether constant propagation is enabled or not.
	 */
	private boolean enabled = getEnable();

	private boolean nops = getNops();

	public LiveVariablesAnalysis(Builder builder) {

	}

	@Override
	public void apply(WyilFile module) {
		if(enabled) {
			super.apply(module);
		}
	}


	public static String describeEnable() {
		return "Enable/disable live variables analysis";
	}

	public static boolean getEnable() {
		return true; // default value
	}

	public void setEnable(boolean flag) {
		this.enabled = flag;
	}

	public static String describeNops() {
		return "Enable/disable replacement with nops (helpful for debugging)";
	}

	public static boolean getNops() {
		return false; // default value
	}

	public void setNops(boolean flag) {
		this.nops = flag;
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

	/**
	 * Last store for the live variables analysis is empty, because all
	 * variables are considered to be dead at the end of a method/function.
	 *
	 * @return
	 */
	@Override
	public Env lastStore() { return EMPTY_ENV; }

	@Override
	public WyilFile.FunctionOrMethod propagate(WyilFile.FunctionOrMethod mcase) {

		ArrayList<AttributedCodeBlock> requires = new ArrayList<AttributedCodeBlock>(
				mcase.precondition());
		for (int i = 0; i != requires.size(); ++i) {
			AttributedCodeBlock tmp = propagate(requires.get(i));
			requires.set(i, tmp);
		}
		ArrayList<AttributedCodeBlock> ensures = new ArrayList<AttributedCodeBlock>(
				mcase.postcondition());
		for (int i = 0; i != ensures.size(); ++i) {
			AttributedCodeBlock tmp = propagate(ensures.get(i));
			ensures.set(i, tmp);
		}
		AttributedCodeBlock body = mcase.body();
		if (body != null) {
			body = propagate(body);
		}

		return new WyilFile.FunctionOrMethod(method.modifiers(), method.name(),
				method.type(), body, requires, ensures, mcase.attributes());
	}

	public AttributedCodeBlock propagate(AttributedCodeBlock body) {
		rootBlock = body;
		stores = new HashMap<String,Env>();
		rewrites.clear();
		Env environment = lastStore();
		propagate(null, body, environment, Collections.EMPTY_LIST);

		// At this point, we apply the inserts
		AttributedCodeBlock nbody = new AttributedCodeBlock();
		for(int i=0;i!=body.size();++i) {
			Code rewrite = rewrites.get(i);
			if(rewrite != null) {
				if (!(rewrite instanceof Codes.Nop) || nops) {
					// FIXME: missing attributes !?
					nbody.add(rewrite);
				}
			} else {
				nbody.add(body.get(i));
			}
		}

		return nbody;
	}

	@Override
	public Env propagate(CodeBlock.Index index, Code code, Env environment) {
		rewrites.put(index,null);
		boolean isLive = true;
		environment = (Env) environment.clone();

		if (code instanceof Code.AbstractAssignable) {
			Code.AbstractAssignable aa = (Code.AbstractAssignable) code;
			if(code instanceof Codes.Update) {
				Codes.Update cu = (Codes.Update) aa;
				// In the normal case, this bytecode is considered live if the
				// assigned register is live. However, in the case of an
				// indirect assignment, then it is always considered live.
				if(!(cu.type() instanceof Type.Reference)) {
					// No, this is not an indirect assignment through a
					// reference
					isLive = environment.contains(cu.target());
				}
			} else {
				for(int target : aa.targets()) {
					isLive = environment.remove(target);
				}
			}
		}

		if(isLive && code instanceof Code.AbstractUnaryOp) {
			Code.AbstractUnaryOp c = (Code.AbstractUnaryOp) code;
			if(c.operand != Codes.NULL_REG) {
				// return bytecode has an optional operand.
				environment.add(c.operand);
			}
		} else if(isLive && code instanceof Code.AbstractBinaryOp) {
			Code.AbstractBinaryOp c = (Code.AbstractBinaryOp) code;
			environment.add(c.leftOperand);
			environment.add(c.rightOperand);
		} else if ((isLive && code instanceof Code.AbstractNaryAssignable)
				|| (code instanceof Codes.Invoke && ((Codes.Invoke) code).type() instanceof Type.Method)
				|| (code instanceof Codes.IndirectInvoke && ((Codes.IndirectInvoke) code).type() instanceof Type.Method)) {
			Code.AbstractNaryAssignable c = (Code.AbstractNaryAssignable) code;
			for(int operand : c.operands()) {
				environment.add(operand);
			}
		} else if(!isLive) {
			rewrites.put(index, Codes.Nop);
		} else {
			// const
		}

		return environment;
	}

	@Override
	public Env propagate(CodeBlock.Index index, Codes.If code, Env trueEnv,
			Env falseEnv) {
		Env r = join(trueEnv, falseEnv);

		r.add(code.leftOperand);
		r.add(code.rightOperand);

		return r;
	}

	@Override
	protected Env propagate(Type handler, Env normalEnv, Env exceptionEnv) {

		return join(normalEnv, exceptionEnv);
	}

	@Override
	public Env propagate(CodeBlock.Index index, Codes.IfIs code, Env trueEnv, Env falseEnv) {
		Env r = join(trueEnv,falseEnv);

		r.add(code.operand);

		return r;
	}

	@Override
	public Env propagate(CodeBlock.Index index, Codes.Switch code,
			List<Env> environments, Env defEnv) {
		Env environment = defEnv;

		for(int i=0;i!=code.branches.size();++i) {
			environment = join(environment,environments.get(i));
		}

		environment.add(code.operand);

		return environment;
	}

	public Env propagate(CodeBlock.Index index, Codes.Loop loop, Env environment,
			List<Pair<Type, String>> handlers) {
		rewrites.put(index,null); // to overrule any earlier rewrites

		Env oldEnv = null;
		Env newEnv = null;

		if(loop instanceof Codes.Quantify) {
			Codes.Quantify fall = (Codes.Quantify) loop;
			environment = new Env(environment);
			environment.add(fall.startOperand);
			environment.add(fall.endOperand);
		} else {
			environment = EMPTY_ENV;
		}
		CodeBlock block = (CodeBlock) loop;
		do {
			// iterate until a fixed point reached
			oldEnv = newEnv != null ? newEnv : environment;
			newEnv = propagate(index, block, oldEnv, handlers);
			newEnv = join(environment,newEnv);
		} while (!newEnv.equals(oldEnv));

		environment = newEnv;

		if(loop instanceof Codes.Quantify) {
			Codes.Quantify fall = (Codes.Quantify) loop;
			// FIXME: is the following really necessary?
			environment.remove(fall.indexOperand);
		}

		// Now, check whether any of the modified operands are no longer live.
		int nInvalidatedOperands = 0;
		for(int mo : loop.modifiedOperands) {
			if(!environment.contains(mo)) {
				nInvalidatedOperands++;
			}
		}
		if(nInvalidatedOperands > 0) {
			// ok, yes, at least one is not live
			int[] nModifiedOperands = new int[loop.modifiedOperands.length - nInvalidatedOperands];
			int j = 0;
			for(int mo : loop.modifiedOperands) {
				if(environment.contains(mo)) {
					nModifiedOperands[j++] = mo;
				}
			}
			Code code;
			if(loop instanceof Codes.Quantify) {
				Codes.Quantify fall = (Codes.Quantify) loop;

				// FIXME: the following is surely broken ?

				code = Codes.Quantify(fall.startOperand, fall.endOperand,
						fall.indexOperand, nModifiedOperands, loop.bytecodes());
			} else {

				// FIXME: the following is surely broken ?

				code = Codes.Loop(nModifiedOperands,loop.bytecodes());
			}
			rewrites.put(index, code);
		}

		return environment;
	}

	private Env join(Env env1, Env env2) {
		// implements set union
		Env r = new Env(env1);
		r.addAll(env2);
		return r;
	}

	private static final Env EMPTY_ENV = new Env();

	public static final class Env extends HashSet<Integer> {
		public Env() {
			super();
		}

		public Env(Env env) {
			super(env);
		}
	}
}
