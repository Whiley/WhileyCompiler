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

import static wybs.lang.SyntaxError.internalFailure;

import java.math.BigInteger;
import java.util.*;

import wybs.lang.Builder;
import wybs.lang.Path;
import wyil.lang.*;
import wyil.lang.Block.Entry;
import wyil.lang.Code;
import wyil.util.*;
import wyil.util.dfa.ForwardFlowAnalysis;
import wyjc.runtime.BigRational;

public class ConstantPropagation extends ForwardFlowAnalysis<ConstantPropagation.Env> {	
	private static final HashMap<Integer,Rewrite> rewrites = new HashMap<Integer,Rewrite>();
	
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
		return false; // default value
	}
	
	public void setEnable(boolean flag) {
		this.enabled = flag;
	}
		
	public WyilFile.TypeDef transform(WyilFile.TypeDef type) {
		// TODO: propagate constants through type constraints
		return type;		
	}
	
	public Env initialStore() {				
		Env environment = new Env();		
		int nvars = methodCase.body().numSlots();
		
		for (int i=0; i != nvars; ++i) {			
			environment.add(null);			
		}				
					
		return environment;				
	}
	
	public WyilFile.Case propagate(WyilFile.Case mcase) {		
		methodCase = mcase;
		block = mcase.body();
		stores = new HashMap<String,Env>();		
		rewrites.clear();

		// TODO: propagate constants through pre- and post-conditions.
		
		Env environment = initialStore();		
		propagate(0,mcase.body().size(), environment, Collections.EMPTY_LIST);	
		
		// At this point, we apply the inserts
		Block body = mcase.body();
		Block nbody = new Block(body.numInputs());		
		for(int i=0;i!=body.size();++i) {
			Rewrite rewrite = rewrites.get(i);			
			if(rewrite != null) {					
				nbody.append(rewrite.rewrite);				
			} else {				
				nbody.append(body.get(i));
			}
		}
		
		return new WyilFile.Case(nbody, mcase.precondition(),
				mcase.postcondition(), mcase.locals(), mcase.attributes());
	}
	
	/*
	
	protected Block unrollFor(Code.ForAll fall, Block body) {		
		Block blk = new Block();
		Collection<Value> values;
		if(fall.source instanceof Value.List) {
			Value.List l = (Value.List) fall.source;
			values = l.values;
		} else {
			Value.Set s = (Value.Set) fall.source;
			values = s.values;
		}
		HashMap<String,CExpr> binding = new HashMap<String,CExpr>();
		String var = fall.variable.name();
		for(Value v : values) {
			// first, relabel to avoid conflicts
			Block tmp = Block.relabel(body);
			// second, substitute value
			binding.put(var, v);			
			tmp = Block.substitute(binding,tmp);			
			// finally,add to the target blk
			blk.addAll(tmp);
		}
		return blk;
	}		
	
	*/
	

	@Override
	public Env propagate(int index, Entry entry, Env environment) {						
		Code code = entry.code;			
		
		// reset the rewrites for this code, in case it changes
		rewrites.remove(index);
		
		environment = (Env) environment.clone();
		
		if(code instanceof Code.ArithOp) {
			infer(index,(Code.ArithOp)code,entry,environment);
		} else if(code instanceof Code.Convert) {
			infer(index,(Code.Convert)code,entry,environment);
		} else if(code instanceof Code.Const) {
			infer((Code.Const)code,entry,environment);
		} else if(code instanceof Code.Debug) {
			infer((Code.Debug)code,entry,environment);
		} else if(code instanceof Code.Assert) {
			// skip
		} else if(code instanceof Code.FieldLoad) {
			infer(index,(Code.FieldLoad)code,entry,environment);			
		} else if(code instanceof Code.IndirectInvoke) {
			infer((Code.IndirectInvoke)code,entry,environment);
		} else if(code instanceof Code.IndirectSend) {
			infer((Code.IndirectSend)code,entry,environment);
		} else if(code instanceof Code.Invoke) {
			infer((Code.Invoke)code,entry,environment);
		} else if(code instanceof Code.Invert) {
			infer(index,(Code.Invert)code,entry,environment);
		} else if(code instanceof Code.Label) {
			// skip			
		} else if(code instanceof Code.ListOp) {
			infer(index,(Code.ListOp)code,entry,environment);
		} else if(code instanceof Code.LengthOf) {
			infer(index,(Code.LengthOf)code,entry,environment);
		} else if(code instanceof Code.SubList) {
			infer(index,(Code.SubList)code,entry,environment);
		} else if(code instanceof Code.IndexOf) {
			infer(index,(Code.IndexOf)code,entry,environment);
		} else if(code instanceof Code.Assign) {
			infer(index,(Code.Assign)code,entry,environment);
		} else if(code instanceof Code.Update) {
			infer((Code.Update)code,entry,environment);
		} else if(code instanceof Code.NewMap) {
			infer(index,(Code.NewMap)code,entry,environment);
		} else if(code instanceof Code.NewList) {
			infer(index,(Code.NewList)code,entry,environment);
		} else if(code instanceof Code.NewRecord) {
			infer(index,(Code.NewRecord)code,entry,environment);
		} else if(code instanceof Code.NewSet) {
			infer(index,(Code.NewSet)code,entry,environment);
		} else if(code instanceof Code.NewTuple) {
			infer(index,(Code.NewTuple)code,entry,environment);
		} else if(code instanceof Code.Neg) {
			infer(index,(Code.Neg)code,entry,environment);
		} else if(code instanceof Code.Dereference) {
			infer(index,(Code.Dereference)code,entry,environment);
		} else if(code instanceof Code.Return) {
			infer((Code.Return)code,entry,environment);
		} else if(code instanceof Code.Send) {
			infer((Code.Send)code,entry,environment);
		} else if(code instanceof Code.SetOp) {
			infer(index,(Code.SetOp)code,entry,environment);
		} else if(code instanceof Code.StringOp) {
			infer(index,(Code.StringOp)code,entry,environment);
		} else if(code instanceof Code.SubString) {
			infer(index,(Code.SubString)code,entry,environment);
		} else if(code instanceof Code.Nop) {
			// skip			
		} else if(code instanceof Code.NewObject) {
			infer(index,(Code.NewObject)code,entry,environment);
		} else if(code instanceof Code.Throw) {
			infer(index,(Code.Throw)code,entry,environment);
		} else {
			internalFailure("unknown: " + code.getClass().getName(),filename,entry);
			return null;
		}	
		
		return environment;
	}
	
	public void infer(int index, Code.ArithOp code, Block.Entry entry,
			Env environment) {		
		Value lhs = environment.get(code.leftOperand);
		Value rhs = environment.get(code.rightOperand);
		Value result = null;
		
		if(lhs instanceof Value.Rational && rhs instanceof Value.Rational) {
			Value.Rational lnum = (Value.Rational) lhs;
			Value.Rational rnum = (Value.Rational) rhs;
			
			switch (code.bop) {
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
			entry = new Block.Entry(Code.Const(code.target,result),entry.attributes());
			rewrites.put(index, new Rewrite(entry));
		} else if(lhs instanceof Value.Integer && rhs instanceof Value.Integer) {
			Value.Integer lnum = (Value.Integer) lhs;
			Value.Integer rnum = (Value.Integer) rhs;
			
			switch (code.bop) {
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
			case RANGE:
			{				
				int start = lnum.value.intValue();
				int end = rnum.value.intValue();
				if (BigInteger.valueOf(start).equals(lnum.value)
						&& BigInteger.valueOf(end).equals(rnum.value)) {
					if(start > -100 && start < 100 && end > -100 && end < 100) {
						int dir = start < end ? 1 : -1;
						ArrayList<Value> values = new ArrayList<Value>();
						while(start != end) {
							values.add(Value.V_INTEGER(BigInteger
									.valueOf(start)));
							start = start + dir;
						}
						result = Value.V_LIST(values);
						break;
					}
				} 
				environment.set(code.target,null);
				return;
			}	
			}		
			entry = new Block.Entry(Code.Const(code.target,result),entry.attributes());
			rewrites.put(index, new Rewrite(entry));
		} 
		
		environment.set(code.target,result);
	}
	
	public void infer(int index, Code.Convert code, Block.Entry entry,
			Env environment) {
		// TODO: implement this		
		Value val = environment.get(code.operand);
		environment.set(code.target,null);
	}
	
	public void infer(Code.Const code, Block.Entry entry,
			Env environment) {
		environment.set(code.target,code.constant);		
	}
	
	public void infer(Code.Debug code, Block.Entry entry,
			Env environment) {		
	}
			
	public void infer(int index, Code.FieldLoad code, Block.Entry entry,
			Env environment) {
		Value src = environment.get(code.operand);
		
		Value result = null;
		if (src instanceof Value.Record) {
			Value.Record rec = (Value.Record) src;
			result = rec.values.get(code.field);
			entry = new Block.Entry(Code.Const(code.target, result),
					entry.attributes());
			rewrites.put(index, new Rewrite(entry));
		}
		
		environment.set(code.target,result);		
	}
	
	public void infer(Code.IndirectInvoke code, Block.Entry entry,
			Env environment) {
		
		// TODO: in principle we can do better here in the case that the target
		// is a constant. This seems pretty unlikely though ...
		
		if (code.target != Code.NULL_REG) {
			environment.set(code.target, null);
		}
	}
	
	public void infer(Code.IndirectSend code, Block.Entry entry,
			Env environment) {
		if (code.target != Code.NULL_REG) {
			environment.set(code.target, null);
		}
	}
	
	public void infer(Code.Invoke code, Block.Entry entry,
			Env environment) {
				
		if (code.target != Code.NULL_REG) {
			environment.set(code.target, null);
		}
	}
	
	public void infer(int index, Code.ListOp code, Block.Entry entry,
			Env environment) {
		Value lhs = environment.get(code.leftOperand);
		Value rhs = environment.get(code.rightOperand);
		Value result = null;
		switch(code.operation) {
		case APPEND:
			if (lhs instanceof Value.List && rhs instanceof Value.List) {
				Value.List left = (Value.List) lhs;
				Value.List right = (Value.List) rhs;
				ArrayList<Value> values = new ArrayList<Value>(left.values);
				values.addAll(right.values);
				result = Value.V_LIST(values);
			}
			break;
		case LEFT_APPEND:
			if (lhs instanceof Value.List && rhs instanceof Value) {
				Value.List left = (Value.List) lhs;
				Value right = (Value) rhs;
				ArrayList<Value> values = new ArrayList<Value>(left.values);
				values.add(right);
				result = Value.V_LIST(values);
			}
			break;
		case RIGHT_APPEND:
			if (lhs instanceof Value && rhs instanceof Value.List) {
				Value left = (Value) lhs;
				Value.List right = (Value.List) rhs;
				ArrayList<Value> values = new ArrayList<Value>();
				values.add(left);
				values.addAll(right.values);
				result = Value.V_LIST(values);
			} 
		}
		
		if(result != null) {
			entry = new Block.Entry(Code.Const(code.target, result),
					entry.attributes());
			rewrites.put(index, new Rewrite(entry));
		}
		
		environment.set(code.target,result);
	}
	
	public void infer(int index, Code.LengthOf code, Block.Entry entry,
			Env environment) {
		Value val = environment.get(code.operand);
		Value result = null;
		
		if(val instanceof Value.List) {
			Value.List list = (Value.List) val;
			result = Value.V_INTEGER(BigInteger.valueOf(list.values.size()));
			entry = new Block.Entry(Code.Const(code.target,result),entry.attributes());
			rewrites.put(index, new Rewrite(entry));
		} else if(val instanceof Value.Set) {
			Value.Set list = (Value.Set) val;
			result = Value.V_INTEGER(BigInteger.valueOf(list.values.size()));
			entry = new Block.Entry(Code.Const(code.target,result),entry.attributes());
			rewrites.put(index, new Rewrite(entry));
		} 
		
		environment.set(code.target,result);
	}
	
	public void infer(int index, Code.SubList code, Block.Entry entry,
			Env environment) {
		Value list = environment.get(code.operands[0]);
		Value start = environment.get(code.operands[1]);
		Value end = environment.get(code.operands[2]);
		Value result = null;
		if (list instanceof Value.List && start instanceof Value.Rational
				&& end instanceof Value.Rational) {
			Value.Rational en = (Value.Rational) end;
			Value.Rational st = (Value.Rational) start;
			if (en.value.isInteger() && st.value.isInteger()) {
				Value.List li = (Value.List) list;
				int eni = en.value.intValue();
				int sti = st.value.intValue();
				if (BigRational.valueOf(eni).equals(en.value) && eni >= 0
						&& eni <= li.values.size()
						&& BigRational.valueOf(sti).equals(st.value)
						&& sti >= 0 && sti <= li.values.size()) {
					ArrayList<Value> nvals = new ArrayList<Value>();
					for (int i = sti; i < eni; ++i) {
						nvals.add(li.values.get(i));
					}
					result = Value.V_LIST(nvals);
					entry = new Block.Entry(Code.Const(code.target, result),
							entry.attributes());
					rewrites.put(index, new Rewrite(entry));
				}
			}
		}
		environment.set(code.target, result);
	}
	
	public void infer(int index, Code.IndexOf code, Block.Entry entry,
			Env environment) {		
		Value src = environment.get(code.leftOperand);
		Value idx = environment.get(code.rightOperand);
		Value result = null;
		if (idx instanceof Value.Rational && src instanceof Value.List) {
			Value.Rational num = (Value.Rational) idx;
			Value.List list = (Value.List) src;
			if(num.value.isInteger()) {
				int i = num.value.intValue();
				if (BigRational.valueOf(i).equals(num.value) && i >= 0
						&& i < list.values.size()) {
					result = list.values.get(i);
					entry = new Block.Entry(Code.Const(code.target,result),entry.attributes());
					rewrites.put(index, new Rewrite(entry));
				}
			}			
		} else if(src instanceof Value.Strung && idx instanceof Value.Rational) {
				Value.Strung str = (Value.Strung) src;
				Value.Rational num = (Value.Rational) idx;			
				if (num.value.isInteger()) {
					int i = num.value.intValue();
					if(i >=0 && i < str.value.length()) {
						// TO DO: need to actually push a character here
					}
				}
			} 
		
		environment.set(code.target,result);		
	}
	
	public void infer(int index, Code.Assign code, Block.Entry entry,
			Env environment) {

		Value result = environment.get(code.operand);
		if (result != null) {
			// register rewrite
			entry = new Block.Entry(Code.Const(code.target, result),
					entry.attributes());
			rewrites.put(index, new Rewrite(entry));
		}

		environment.set(code.target, result);
	}
	
	public void infer(Code.Update code, Block.Entry entry,
			Env environment) {		
		// TODO: implement this!		
		environment.set(code.target,null);
	}
	
	public void infer(int index, Code.NewMap code, Block.Entry entry,
			Env environment) {
		HashMap<Value, Value> values = new HashMap<Value, Value>();
		boolean isValue = true;
		int[] code_operands = code.operands;
		for (int i = 0; i != code_operands.length; i = i + 2) {
			Value val = environment.get(code_operands[i]);
			Value key = environment.get(code_operands[i + 1]);
			if (key instanceof Value && val instanceof Value) {
				values.put(key, val);
			} else {
				isValue = false;
			}
		}
		Value result = null;
		if (isValue) {
			result = Value.V_MAP(values);
			entry = new Block.Entry(Code.Const(code.target, result),
					entry.attributes());
			rewrites.put(index, new Rewrite(entry));
		}
		environment.set(code.target, result);
	}
	
	public void infer(int index, Code.NewRecord code, Block.Entry entry,
			Env environment) {
		HashMap<String, Value> values = new HashMap<String, Value>();
		ArrayList<String> keys = new ArrayList<String>(code.type.keys());
		Collections.sort(keys);
		boolean isValue = true;
		int[] code_operands = code.operands;
		for (int i=0;i!=code_operands.length;++i) {
			Value val = environment.get(code_operands[i]);
			if (val != null) {
				values.put(keys.get(i), val);
			} else {
				isValue = false;
			}
		}

		Value result = null;
		if (isValue) {
			result = Value.V_RECORD(values);
			entry = new Block.Entry(Code.Const(code.target, result),
					entry.attributes());
			rewrites.put(index, new Rewrite(entry));
		}
		
		environment.set(code.target,result);
	}
	
	public void infer(int index, Code.NewList code, Block.Entry entry,
			Env environment) {
		ArrayList<Value> values = new ArrayList<Value>();

		boolean isValue = true;
		int[] code_operands = code.operands;
		for (int i = 0; i != code_operands.length; ++i) {
			Value val = environment.get(code_operands[i]);
			if (val != null) {
				values.add(val);
			} else {
				isValue = false;
			}
		}

		Value result = null;
		if (isValue) {
			result = Value.V_LIST(values);
			entry = new Block.Entry(Code.Const(code.target, result),
					entry.attributes());
			rewrites.put(index, new Rewrite(entry));
		}
		environment.set(code.target, result);
	}
	
	public void infer(int index, Code.NewSet code, Block.Entry entry,
			Env environment) {
		HashSet<Value> values = new HashSet<Value>();

		boolean isValue = true;
		int[] code_operands = code.operands;
		for (int i = 0; i != code_operands.length; ++i) {
			Value val = environment.get(code_operands[i]);
			if (val != null) {
				values.add(val);
			} else {
				isValue = false;
			}
		}

		Value result = null;
		if (isValue) {
			result = Value.V_SET(values);
			entry = new Block.Entry(Code.Const(code.target, result),
					entry.attributes());
			rewrites.put(index, new Rewrite(entry));
		}
		environment.set(code.target, result);
	}
	
	public void infer(int index, Code.NewTuple code, Block.Entry entry,
			Env environment) {
		ArrayList<Value> values = new ArrayList<Value>();		

		boolean isValue=true;
		int[] code_operands = code.operands;
		for (int i = 0; i != code_operands.length; ++i) {
			Value val = environment.get(code_operands[i]);
			if (val != null) {
				values.add(val);
			} else {
				isValue = false;
			}
		}		
		
		Value result = null;
		if (isValue) {	
			result = Value.V_TUPLE(values);
			entry = new Block.Entry(Code.Const(code.target,result),entry.attributes());
			rewrites.put(index, new Rewrite(entry));
		}
		environment.set(code.target,result);
	}
	
	public void infer(Code.Return code, Block.Entry entry,
			Env environment) {
		
	}
	
	public void infer(Code.Send code, Block.Entry entry,
			Env environment) {
		if (code.target != Code.NULL_REG) {
			environment.set(code.target,null);
		}
	}
	
	public void infer(int index, Code.SetOp code, Block.Entry entry,
			Env environment) {						
		Value result = null;
		Value lhs = environment.get(code.leftOperand);
		Value rhs = environment.get(code.rightOperand);
		switch(code.operation) {
		case UNION:
			if (lhs instanceof Value.Set
					&& rhs instanceof Value.Set) {
				Value.Set lv = (Value.Set) lhs;
				Value.Set rv = (Value.Set) rhs;
				result = lv.union(rv);
			}
			break;
		case LEFT_UNION:
			if(lhs instanceof Value.Set && rhs instanceof Value) {
				Value.Set lv = (Value.Set) lhs;
				Value rv = (Value) rhs;
				result = lv.add(rv);
			} 
			break;
		case RIGHT_UNION:
			if(lhs instanceof Value && rhs instanceof Value.Set) {
				Value lv = (Value) lhs;
				Value.Set rv = (Value.Set) rhs;
				result = rv.add(lv);
			}
			break;
		case INTERSECTION:
			if (lhs instanceof Value.Set
					&& rhs instanceof Value.Set) {
				Value.Set lv = (Value.Set) lhs;
				Value.Set rv = (Value.Set) rhs;
				result = lv.intersect(rv);
			} 
			break;
		case LEFT_INTERSECTION:
			if (lhs instanceof Value.Set && rhs instanceof Value) {
				Value.Set lv = (Value.Set) lhs;
				Value rv = (Value) rhs;
				if (lv.values.contains(rv)) {
					HashSet<Value> nset = new HashSet<Value>();
					nset.add(rv);
					result = Value.V_SET(nset);
				} else {
					result = Value.V_SET(Collections.EMPTY_SET);
				}
			}
			break;
		case RIGHT_INTERSECTION:
			if(lhs instanceof Value && rhs instanceof Value.Set) {
				Value lv = (Value) lhs;
				Value.Set rv = (Value.Set) rhs;
				if(rv.values.contains(lv)) {
					HashSet<Value> nset = new HashSet<Value>();
					nset.add(lv);
					result = Value.V_SET(nset);
				} else {
					result = Value.V_SET(Collections.EMPTY_SET);
				}
			}
			break;
		case DIFFERENCE:
			if (lhs instanceof Value.Set && rhs instanceof Value.Set) {
				Value.Set lv = (Value.Set) lhs;
				Value.Set rv = (Value.Set) rhs;
				result = lv.difference(rv);
			}
			break;
		case LEFT_DIFFERENCE:
			if(lhs instanceof Value.Set && rhs instanceof Value) {
				Value.Set lv = (Value.Set) lhs;
				Value rv = (Value) rhs;
				result = lv.remove(rv);
			} 
			break;
		}
		 
		
		if(result != null) {
			entry = new Block.Entry(Code.Const(code.target,result),entry.attributes());
			rewrites.put(index, new Rewrite(entry));
		}
		
		environment.set(code.target,null);
	}
	
	public void infer(int index, Code.StringOp code, Block.Entry entry,
			Env environment) {
		Value lhs = environment.get(code.leftOperand);
		Value rhs = environment.get(code.rightOperand);
		Value result = null;
		switch(code.operation) {
		case APPEND:
			if(lhs instanceof Value.Strung && rhs instanceof Value.Strung) {
				Value.Strung left = (Value.Strung) lhs;
				Value.Strung right = (Value.Strung) rhs;
				result = Value.V_STRING(left.value + right.value);
			} 
			break;
		case LEFT_APPEND:
			// TODO: need to add Value.Char
			break;
		case RIGHT_APPEND:
			// TODO: need to add Value.Char
			break;
		}

		if (result != null) {
			entry = new Block.Entry(Code.Const(code.target, result),
					entry.attributes());
			rewrites.put(index, new Rewrite(entry));
		}
		environment.set(code.target, result);
	}
	
	public void infer(int index, Code.SubString code, Block.Entry entry,
			Env environment) {		
		
		Value src = environment.get(code.operands[0]);
		Value start = environment.get(code.operands[1]);
		Value end = environment.get(code.operands[2]);
		
		Value result = null;
		if (src instanceof Value.Strung && start instanceof Value.Rational
				&& end instanceof Value.Rational) {
			Value.Rational en = (Value.Rational) end;
			Value.Rational st = (Value.Rational) start;
			if (en.value.isInteger() && st.value.isInteger()) {
				Value.Strung str = (Value.Strung) src;
				int eni = en.value.intValue();
				int sti = st.value.intValue();
				if (BigRational.valueOf(eni).equals(en.value)
						&& eni >= 0 && eni <= str.value.length()
						&& BigRational.valueOf(sti).equals(st.value)
						&& sti >= 0 && sti <= str.value.length()) {
					String nval = "";							
					for (int i = sti; i < eni; ++i) {
						nval += str.value.charAt(i);
					}
					result = Value.V_STRING(nval);
					entry = new Block.Entry(Code.Const(code.target,result),entry.attributes());
					rewrites.put(index, new Rewrite(entry));
				}
			}
		} 
		
		environment.set(code.target,result);
	}
	
	public void infer(int index, Code.Invert code, Block.Entry entry,
			Env environment) {
		Value val = environment.get(code.operand);
		Value result = null;
		
		if (val instanceof Value.Byte) {
			Value.Byte num = (Value.Byte) val;
			result = Value.V_BYTE((byte) ~num.value);
			entry = new Block.Entry(Code.Const(code.target, result),
					entry.attributes());
			rewrites.put(index, new Rewrite(entry));
		}

		environment.set(code.target,result);
	}
	
	public void infer(int index, Code.Neg code, Block.Entry entry,
			Env environment) {
		Value val = environment.get(code.operand);
		Value result = null;
		
		if(val instanceof Value.Rational) {
			Value.Rational num = (Value.Rational) val;
			result = Value.V_RATIONAL(num.value.negate());
		} else if (val instanceof Value.Integer) {
			Value.Integer num = (Value.Integer) val;
			result = Value.V_INTEGER(num.value.negate());
		}
		
		if(result != null) {
			entry = new Block.Entry(Code.Const(code.target,result),entry.attributes());
			rewrites.put(index, new Rewrite(entry));
		}
		
		environment.set(code.target,result);
	}
	
	public void infer(int index, Code.NewObject code, Block.Entry entry,
			Env environment) {
		environment.set(code.target, null);
	}
	
	public void infer(int index, Code.Throw code, Block.Entry entry,
			Env environment) {		
	}
	
	public void infer(int index, Code.Dereference code, Block.Entry entry,
			Env environment) {
		environment.set(code.target, null);
	}	
	
	@Override
	public Pair<Env, Env> propagate(int index, Code.If code, Entry stmt,
			Env environment) {
		environment = (Env) environment.clone();

		Value lhs = environment.get(code.leftOperand);
		Value rhs = environment.get(code.rightOperand);

		// TODO: could do more here to eliminate conditionals which must either
		// be taken or untaken.

		return new Pair(environment, environment);
	}
	
	@Override
	public Pair<Env, Env> propagate(int index,
			Code.IfIs code, Entry stmt, Env environment) {
		environment = (Env) environment.clone();
		Value lhs = environment.get(code.leftOperand);
		
		// TODO: could do more here to eliminate conditionals which must either
		// be taken or untaken.
		
		return new Pair(environment, environment);
	}
	
	@Override
	public List<Env> propagate(int index, Code.Switch code, Entry stmt,
			Env environment) {
		environment = (Env) environment.clone();

		Value val = environment.get(code.operand);

		ArrayList<Env> stores = new ArrayList();
		for (int i = 0; i != code.branches.size(); ++i) {
			stores.add(environment);
		}

		return stores;
	}

	@Override
	public Env propagate(Type handler, Env environment) {		
		Env catchEnvironment = (Env) environment.clone();		
		// TODO: implement me!
		return catchEnvironment;
	}
	
	@Override
	public Env propagate(int start, int end, Code.Loop loop,
			Entry stmt, Env environment, List<Pair<Type,String>> handlers) {
		
		environment = new Env(environment);
		
		if(loop instanceof Code.ForAll) {
			Code.ForAll fall = (Code.ForAll) loop; 	
			
			// TO DO: could unroll loop if src collection is a value.
			
			environment.set(fall.indexOperand,null);
		} 
		
		// Now, kill every variable which is modified in the loop. This is a
		// safety precaution, and it's possible we could do better here in some
		// circumstances (e.g. by unrolling the loop).
		
		for(int slot : loop.modifiedOperands) {
			environment.set(slot,null);
		}
		
		Env oldEnv = null;
		Env newEnv = null;				
		
		do {						
			// iterate until a fixed point reached
			oldEnv = newEnv != null ? newEnv : environment;
			newEnv = propagate(start+1,end, oldEnv, handlers);									
		} while (!newEnv.equals(oldEnv));
		
		return join(environment,newEnv);		
	}
	
	public Env join(Env env1, Env env2) {
		if (env2 == null) {
			return env1;
		} else if (env1 == null) {
			return env2;
		}
		Env env = new Env();
		for (int i = 0; i != Math.min(env1.size(), env2.size()); ++i) {
			Value mt = env1.get(i);
			Value ot = env2.get(i);
			if (ot instanceof Value && mt instanceof Value && ot.equals(mt)) {
				env.add(mt);
			} else {
				env.add(null);
			}			
		}

		return env;
	}	
	
	public final static class Env extends ArrayList<Value> {
		public Env() {
		}
		public Env(Collection<Value> v) {
			super(v);
		}		
		public Env clone() {
			return new Env(this);
		}
	}
	

	private static class Rewrite {		
		public final Block.Entry rewrite;
		
		public Rewrite(Block.Entry rewrite) {
			this.rewrite = rewrite;
		}
	}
}
