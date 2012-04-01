// Copyright (c) 2012, David J. Pearce (djp@ecs.vuw.ac.nz)
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

import wybs.lang.Builder;
import wyil.lang.*;
import wyil.lang.Code.*;
import static wybs.lang.SyntaxError.internalFailure;
import static wyil.lang.Code.*;
import wyil.Transform;
import wyone.core.*;
import wyone.core.Value;
import wyone.theory.logic.Logic;

/**
 * Responsible for compile-time checking of constraints. This involves
 * converting WYIL into the appropriate form for the automated theorem prover
 * (wyone).  
 * 
 * @author David J. Pearce
 * 
 */
public class VerificationCheck implements Transform {
	private String filename;
	
	public VerificationCheck(Builder builder) {
		
	}
	
	public void apply(WyilFile module) {
		this.filename = module.filename();
		for(WyilFile.TypeDef type : module.types()) {
			transform(type);
		}		
		for(WyilFile.Method method : module.methods()) {
			//transform(method);
		}		
	}
	
	protected void transform(WyilFile.TypeDef def) {
		
	}
	
	protected void transform(WyilFile.Method method) {
		for(WyilFile.Case c : method.cases()) {
			transform(c);
		}
	}
	
	protected void transform(WyilFile.Case methodCase) {
		Block body = methodCase.body();				
		Formula constraint = Value.V_BOOL(true);		
		int[] environment = new int[body.numSlots()];
		ArrayList<Constructor> stack = new ArrayList<Constructor>();
		
		for (int i = 0; i != body.size(); ++i) {			
			Block.Entry entry = body.get(i);
			Code code = entry.code;
			
			if(code instanceof Code.IfGoto) {
				// TODO: implement me!
			} else if(code instanceof Code.IfType) {
				// TODO: implement me!
			} else if(code instanceof Code.Loop) {
				// TODO: implement me!
			} else if(code instanceof Code.Return) {
				// don't need to do anything for a return!
				return;
			} else if(code instanceof Code.Fail) {
				// TODO: implement me!
				return;
			} else {
				constraint = transform(body.get(i), constraint, environment, stack);
			}
		}
	}
	
	/**
	 * Transform the given constraint according to the abstract semantics of the
	 * given (simple) instruction (entry). The environment maps slots to their
	 * current single assignment number. Likewise, the stack models the stack
	 * and accumulates expressions.
	 * 
	 * @param entry
	 * @param constraint
	 * @param environment
	 * @param stack
	 *            --- current stack of intermediate expressions.
	 * @return
	 */
	protected Formula transform(Block.Entry entry, Formula constraint,
			int[] environment, ArrayList<Constructor> stack) {
		Code code = entry.code;		
		
		if(code instanceof BinOp) {
			constraint = transform((BinOp)code,entry,constraint,environment,stack);
		} else if(code instanceof Convert) {
			constraint = transform((Convert)code,entry,constraint,environment,stack);
		} else if(code instanceof Const) {
			constraint = transform((Const)code,entry,constraint,environment,stack);
		} else if(code instanceof Debug) {
			constraint = transform((Debug)code,entry,constraint,environment,stack);
		} else if(code instanceof Destructure) {
			constraint = transform((Destructure)code,entry,constraint,environment,stack);
		} else if(code instanceof FieldLoad) {
			constraint = transform((FieldLoad)code,entry,constraint,environment,stack);			
		} else if(code instanceof IndirectInvoke) {
			constraint = transform((IndirectInvoke)code,entry,constraint,environment,stack);
		} else if(code instanceof IndirectSend) {
			constraint = transform((IndirectSend)code,entry,constraint,environment,stack);
		} else if(code instanceof Invoke) {
			constraint = transform((Invoke)code,entry,constraint,environment,stack);
		} else if(code instanceof Invert) {
			constraint = transform((Invert)code,entry,constraint,environment,stack);
		} else if(code instanceof Label) {
			// skip			
		} else if(code instanceof ListAppend) {
			constraint = transform((ListAppend)code,entry,constraint,environment,stack);
		} else if(code instanceof LengthOf) {
			constraint = transform((LengthOf)code,entry,constraint,environment,stack);
		} else if(code instanceof SubList) {
			constraint = transform((SubList)code,entry,constraint,environment,stack);
		} else if(code instanceof IndexOf) {
			constraint = transform((IndexOf)code,entry,constraint,environment,stack);
		} else if(code instanceof Load) {
			constraint = transform((Load)code,entry,constraint,environment,stack);
		} else if(code instanceof Update) {
			constraint = transform((Update)code,entry,constraint,environment,stack);
		} else if(code instanceof NewDict) {
			constraint = transform((NewDict)code,entry,constraint,environment,stack);
		} else if(code instanceof NewList) {
			constraint = transform((NewList)code,entry,constraint,environment,stack);
		} else if(code instanceof NewRecord) {
			constraint = transform((NewRecord)code,entry,constraint,environment,stack);
		} else if(code instanceof NewSet) {
			constraint = transform((NewSet)code,entry,constraint,environment,stack);
		} else if(code instanceof NewTuple) {
			constraint = transform((NewTuple)code,entry,constraint,environment,stack);
		} else if(code instanceof Negate) {
			constraint = transform((Negate)code,entry,constraint,environment,stack);
		} else if(code instanceof Dereference) {
			constraint = transform((Dereference)code,entry,constraint,environment,stack);
		} else if(code instanceof Return) {
			constraint = transform((Return)code,entry,constraint,environment,stack);
		} else if(code instanceof Skip) {
			// skip			
		} else if(code instanceof Send) {
			constraint = transform((Send)code,entry,constraint,environment,stack);
		} else if(code instanceof Store) {
			constraint = transform((Store)code,entry,constraint,environment,stack);
		} else if(code instanceof SetUnion) {
			constraint = transform((SetUnion)code,entry,constraint,environment,stack);
		} else if(code instanceof SetDifference) {
			constraint = transform((SetDifference)code,entry,constraint,environment,stack);
		} else if(code instanceof SetIntersect) {
			constraint = transform((SetIntersect)code,entry,constraint,environment,stack);
		} else if(code instanceof StringAppend) {
			constraint = transform((StringAppend)code,entry,constraint,environment,stack);
		} else if(code instanceof SubString) {
			constraint = transform((SubString)code,entry,constraint,environment,stack);
		} else if(code instanceof New) {
			constraint = transform((New)code,entry,constraint,environment,stack);
		} else if(code instanceof Throw) {
			constraint = transform((Throw)code,entry,constraint,environment,stack);
		} else if(code instanceof TupleLoad) {
			constraint = transform((TupleLoad)code,entry,constraint,environment,stack);
		} else {			
			internalFailure("unknown: " + code.getClass().getName(),filename,entry);
			return null;
		}
		return constraint;
	}
	
	protected Formula transform(Code.BinOp code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.Convert code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.Const code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform		
		return constraint;
	}

	protected Formula transform(Code.Debug code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.Destructure code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.FieldLoad code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.IndirectInvoke code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.IndirectSend code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.Invoke code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.Invert code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.ListAppend code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.LengthOf code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.SubList code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.IndexOf code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.Load code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		int slot = code.slot;
		stack.add(new Constructor.Variable(slot + "$" + environment[slot]));
		return constraint;
	}

	protected Formula transform(Code.Update code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.NewDict code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.NewList code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.NewSet code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.NewRecord code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.NewTuple code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.Negate code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.Dereference code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.Return code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.Send code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.Store code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		int slot = code.slot;		
		environment[slot] = environment[slot] + 1;
		Constructor lhs = new Constructor.Variable(slot + "$" + environment[slot]);
		Constructor rhs = pop(stack);
		constraint = Logic.and(constraint,Equality.equals(lhs, rhs));
		return constraint;
	}

	protected Formula transform(Code.SetUnion code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.SetDifference code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.SetIntersect code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.StringAppend code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.SubString code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.New code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.Throw code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected Formula transform(Code.TupleLoad code, Block.Entry blk,
			Formula constraint, int[] environment, ArrayList<Constructor> stack) {
		// TODO: complete this transform
		return constraint;
	}
	
	private static Constructor pop(ArrayList<Constructor> stack) {
		int last = stack.size()-1;
		Constructor c = stack.get(last);
		stack.remove(last);
		return c;
	}
}
