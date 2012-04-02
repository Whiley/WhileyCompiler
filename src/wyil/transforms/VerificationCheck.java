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
import wybs.lang.SyntacticElement;
import wyil.lang.*;
import wyil.lang.Code.*;
import static wybs.lang.SyntaxError.internalFailure;
import static wyil.lang.Code.*;
import wyil.Transform;
import wyjc.runtime.BigRational;
import wyone.core.*;
import wyone.theory.list.WListVal;
import wyone.theory.logic.*;
import wyone.theory.numeric.*;
import wyone.theory.set.WSetVal;
import wyone.theory.tuple.WTupleVal;

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
			transform(method);
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
		WFormula constraint = WBool.TRUE;	
		int[] environment = new int[body.numSlots()];
		ArrayList<WExpr> stack = new ArrayList<WExpr>();
		
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
			} else if(code instanceof Code.Assert) {
				// TODO: implement me!
				return;
			} else {
				constraint = transform(body.get(i), constraint, environment, stack);
			}
			System.out.println("CONSTRAINT: " + constraint);
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
	protected WFormula transform(Block.Entry entry, WFormula constraint,
			int[] environment, ArrayList<WExpr> stack) {
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
	
	protected WFormula transform(Code.BinOp code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.Convert code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.Const code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		stack.add(convert(code.constant,entry));
		return constraint;
	}

	protected WFormula transform(Code.Debug code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.Destructure code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.FieldLoad code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.IndirectInvoke code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.IndirectSend code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.Invoke code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.Invert code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.ListAppend code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.LengthOf code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.SubList code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.IndexOf code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.Load code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		int slot = code.slot;
		stack.add(new WVariable(slot + "$" + environment[slot]));
		return constraint;
	}

	protected WFormula transform(Code.Update code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.NewDict code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.NewList code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.NewSet code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.NewRecord code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.NewTuple code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.Negate code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.Dereference code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.Return code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.Send code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.Store code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		int slot = code.slot;		
		environment[slot] = environment[slot] + 1;
		WExpr lhs = new WVariable(slot + "$" + environment[slot]);
		WExpr rhs = pop(stack);
		constraint = WFormulas.and(constraint,WExprs.equals(lhs, rhs));
		return constraint;
	}

	protected WFormula transform(Code.SetUnion code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.SetDifference code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.SetIntersect code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.StringAppend code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.SubString code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.New code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.Throw code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}

	protected WFormula transform(Code.TupleLoad code, Block.Entry entry,
			WFormula constraint, int[] environment, ArrayList<WExpr> stack) {
		// TODO: complete this transform
		return constraint;
	}
	

	/**
	 * Convert between a WYIL value and a WYONE value. Basically, this is really
	 * stupid and it would be good for them to be the same.
	 * 
	 * @param value
	 * @return
	 */
	private WValue convert(wyil.lang.Value value, SyntacticElement elem) {
		if(value instanceof wyil.lang.Value.Bool) {
			wyil.lang.Value.Bool b = (wyil.lang.Value.Bool) value;
			return b.value ? WBool.TRUE : WBool.FALSE;
		} else if(value instanceof wyil.lang.Value.Byte) {
			wyil.lang.Value.Byte v = (wyil.lang.Value.Byte) value;
			return new WNumber(v.value);
		} else if(value instanceof wyil.lang.Value.Char) {
			wyil.lang.Value.Char v = (wyil.lang.Value.Char) value;
			// Simple, but mostly good translation
			return new WNumber(v.value);
		} else if(value instanceof wyil.lang.Value.Dictionary) {
			return WBool.FALSE; // FIXME
		} else if(value instanceof wyil.lang.Value.FunctionOrMethodOrMessage) {
			return WBool.FALSE; // FIXME
		} else if(value instanceof wyil.lang.Value.Integer) {
			wyil.lang.Value.Integer v = (wyil.lang.Value.Integer) value;
			return new WNumber(v.value);
		} else if(value instanceof wyil.lang.Value.List) {
			Value.List vl = (Value.List) value;
			ArrayList<WValue> vals = new ArrayList<WValue>();
			for(Value e : vl.values) {
				vals.add(convert(e,elem));
			}
			return new WListVal(vals);
		} else if(value instanceof wyil.lang.Value.Null) {
			return WBool.FALSE; // FIXME
		} else if(value instanceof wyil.lang.Value.Rational) {
			wyil.lang.Value.Rational v = (wyil.lang.Value.Rational) value;
			BigRational br = v.value;
			return new WNumber(br.numerator(),br.denominator());
		} else if(value instanceof wyil.lang.Value.Record) {
			Value.Record vt = (Value.Record) value;
			ArrayList<String> fields = new ArrayList<String>(vt.values.keySet());
			ArrayList<WValue> values = new ArrayList<WValue>();
			Collections.sort(fields);
			for(String f : fields) {			
				values.add(convert(vt.values.get(f),elem));
			}
			return new WTupleVal(fields,values);
		} else if(value instanceof wyil.lang.Value.Set) {
			Value.Set vs = (Value.Set) value;			
			HashSet<WValue> vals = new HashSet<WValue>();
			for(Value e : vs.values) {
				vals.add(convert(e,elem));
			}
			return new WSetVal(vals);
		} else if(value instanceof wyil.lang.Value.Strung) {
			return WBool.FALSE; // FIXME
		} else if(value instanceof wyil.lang.Value.Tuple) {
			Value.Tuple vt = (Value.Tuple) value;
			ArrayList<String> fields = new ArrayList<String>();
			ArrayList<WValue> values = new ArrayList<WValue>();
			int i = 0;
			for(Value e : vt.values) {			
				values.add(convert(e,elem));
				fields.add(""+i++);
			}
			return new WTupleVal(fields,values);
		} else {
			internalFailure("unknown value encountered (" + value + ")",filename,elem);
			return null;
		}
	}
	
	private static WExpr pop(ArrayList<WExpr> stack) {
		int last = stack.size()-1;
		WExpr c = stack.get(last);
		stack.remove(last);
		return c;
	}
}
