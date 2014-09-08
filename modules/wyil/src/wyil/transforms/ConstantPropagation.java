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

import static wycc.lang.SyntaxError.internalFailure;

import java.math.BigInteger;
import java.util.*;

import wybs.lang.Builder;
import wycc.lang.Transform;
import wycc.util.Pair;
import wyautl.util.BigRational;
import wyfs.lang.Path;
import wyil.lang.*;
import wyil.lang.Code.Block.Entry;
import wyil.util.*;
import wyil.util.dfa.ForwardFlowAnalysis;

public class ConstantPropagation extends ForwardFlowAnalysis<ConstantPropagation.Env> implements Transform<WyilFile> {	
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
		return true; // default value
	}
	
	public void setEnable(boolean flag) {
		this.enabled = flag;
	}
		
	@Override
	public WyilFile.TypeDeclaration propagate(WyilFile.TypeDeclaration type) {
		Code.Block invariant = type.invariant();
		if (invariant != null) {
			invariant = propagate(invariant);
			return new WyilFile.TypeDeclaration(type.modifiers(), type.name(),
					type.type(), invariant, type.attributes());
		}
		return type;
	}
	
	public Env initialStore() {				
		Env environment = new Env();		
		int nvars = block.numSlots();
		
		for (int i=0; i != nvars; ++i) {			
			environment.add(null);			
		}				
					
		return environment;				
	}
	
	@Override
	public WyilFile.Case propagate(WyilFile.Case mcase) {
		ArrayList<Code.Block> requires = new ArrayList<Code.Block>(
				mcase.precondition());
		for (int i = 0; i != requires.size(); ++i) {
			Code.Block tmp = propagate(requires.get(i));
			requires.set(i, tmp);
		}
		ArrayList<Code.Block> ensures = new ArrayList<Code.Block>(
				mcase.postcondition());
		for (int i = 0; i != ensures.size(); ++i) {
			Code.Block tmp = propagate(ensures.get(i));
			ensures.set(i, tmp);
		}

		Code.Block body = mcase.body();
		if (body != null) {
			body = propagate(body);
		}

		return new WyilFile.Case(body, requires, ensures, mcase.attributes());
	}
	
	public Code.Block propagate(Code.Block body) {				
		block = body;
		stores = new HashMap<String,Env>();		
		rewrites.clear();

		// TODO: propagate constants through pre- and post-conditions.
		
		Env environment = initialStore();		
		propagate(0,body.size(), environment, Collections.EMPTY_LIST);	
		
		// At this point, we apply the inserts
		Code.Block nbody = new Code.Block(body.numInputs());		
		for(int i=0;i!=body.size();++i) {
			Rewrite rewrite = rewrites.get(i);			
			if(rewrite != null) {					
				nbody.add(rewrite.rewrite);				
			} else {				
				nbody.add(body.get(i));
			}
		}
		
		return nbody;
	}
	/*
	
	protected Block unrollFor(Codes.ForAll fall, Block body) {		
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
		
		if(code instanceof Codes.BinaryOperator) {
			infer(index,(Codes.BinaryOperator)code,entry,environment);
		} else if(code instanceof Codes.Convert) {
			infer(index,(Codes.Convert)code,entry,environment);
		} else if(code instanceof Codes.Const) {
			infer(index, (Codes.Const)code,entry,environment);
		} else if(code instanceof Codes.Debug) {
			infer(index, (Codes.Debug)code,entry,environment);
		} else if(code instanceof Codes.AssertOrAssume) {
			infer(index, (Codes.AssertOrAssume)code,entry,environment);
		} else if(code instanceof Codes.Fail) {
			infer(index,(Codes.Fail)code,entry,environment);			
		} else if(code instanceof Codes.FieldLoad) {
			infer(index,(Codes.FieldLoad)code,entry,environment);			
		} else if(code instanceof Codes.TupleLoad) {
			infer(index,(Codes.TupleLoad)code,entry,environment);			
		} else if(code instanceof Codes.IndirectInvoke) {
			infer(index, (Codes.IndirectInvoke)code,entry,environment);
		} else if(code instanceof Codes.Invoke) {
			infer(index, (Codes.Invoke)code,entry,environment);
		} else if(code instanceof Codes.Invert) {
			infer(index,(Codes.Invert)code,entry,environment);
		} else if(code instanceof Codes.Lambda) {
			// skip			
		} else if(code instanceof Codes.Label) {
			// skip			
		} else if(code instanceof Codes.ListOperator) {
			infer(index,(Codes.ListOperator)code,entry,environment);
		} else if(code instanceof Codes.LengthOf) {
			infer(index,(Codes.LengthOf)code,entry,environment);
		} else if(code instanceof Codes.SubList) {
			infer(index,(Codes.SubList)code,entry,environment);
		} else if(code instanceof Codes.IndexOf) {
			infer(index,(Codes.IndexOf)code,entry,environment);
		} else if(code instanceof Codes.Assign) {
			infer(index,(Codes.Assign)code,entry,environment);
		} else if(code instanceof Codes.Update) {
			infer(index, (Codes.Update)code,entry,environment);
		} else if(code instanceof Codes.NewMap) {
			infer(index,(Codes.NewMap)code,entry,environment);
		} else if(code instanceof Codes.NewList) {
			infer(index,(Codes.NewList)code,entry,environment);
		} else if(code instanceof Codes.NewRecord) {
			infer(index,(Codes.NewRecord)code,entry,environment);
		} else if(code instanceof Codes.NewSet) {
			infer(index,(Codes.NewSet)code,entry,environment);
		} else if(code instanceof Codes.NewTuple) {
			infer(index,(Codes.NewTuple)code,entry,environment);
		} else if(code instanceof Codes.UnaryOperator) {
			infer(index,(Codes.UnaryOperator)code,entry,environment);
		} else if(code instanceof Codes.Dereference) {
			infer(index,(Codes.Dereference)code,entry,environment);
		} else if(code instanceof Codes.Return) {
			infer(index, (Codes.Return)code,entry,environment);
		} else if(code instanceof Codes.SetOperator) {
			infer(index,(Codes.SetOperator)code,entry,environment);
		} else if(code instanceof Codes.StringOperator) {
			infer(index,(Codes.StringOperator)code,entry,environment);
		} else if(code instanceof Codes.SubString) {
			infer(index,(Codes.SubString)code,entry,environment);
		} else if(code instanceof Codes.Nop) {
			// skip			
		} else if(code instanceof Codes.NewObject) {
			infer(index,(Codes.NewObject)code,entry,environment);
		} else if(code instanceof Codes.Throw) {
			infer(index,(Codes.Throw)code,entry,environment);
		} else {
			internalFailure("unknown: " + code.getClass().getName(),filename,entry);
			return null;
		}	
		
		return environment;
	}
	
	public void infer(int index, Codes.AssertOrAssume code, Code.Block.Entry entry, Env environment) {
		remap(environment, index, entry);
	}
	
	public void infer(int index, Codes.BinaryOperator code, Code.Block.Entry entry,
			Env environment) {		
		Constant lhs = environment.get(code.operand(0));
		Constant rhs = environment.get(code.operand(1));
		Constant result = null;
		
		if(lhs instanceof Constant.Decimal && rhs instanceof Constant.Decimal) {
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
		} else if(lhs instanceof Constant.Integer && rhs instanceof Constant.Integer) {
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
			case RANGE:
			{				
				int start = lnum.value.intValue();
				int end = rnum.value.intValue();
				if (BigInteger.valueOf(start).equals(lnum.value)
						&& BigInteger.valueOf(end).equals(rnum.value)) {
					if(start > -100 && start < 100 && end > -100 && end < 100) {
						int dir = start < end ? 1 : -1;
						ArrayList<Constant> values = new ArrayList<Constant>();
						while(start != end) {
							values.add(Constant.V_INTEGER(BigInteger
									.valueOf(start)));
							start = start + dir;
						}
						result = Constant.V_LIST(values);
						break;
					}
				} 
			}	
			}		
		} 
		
		assign(code.target(),result,environment,index,entry);
	}
	
	public void infer(int index, Codes.Convert code, Code.Block.Entry entry,
			Env environment) {
		// TODO: implement this		
		Constant val = environment.get(code.operand(0));
		
		invalidate(code.target(),environment,index,entry);
	}
	
	public void infer(int index, Codes.Const code, Code.Block.Entry entry,
			Env environment) {
		invalidate(code.target(),environment,index,entry);
	}
	
	public void infer(int index, Codes.Debug code, Code.Block.Entry entry, Env environment) {
		remap(environment, index, entry);
	}
	
	public void infer(int index, Codes.Fail code, Code.Block.Entry entry,
			Env environment) {
		remap(environment, index, entry);		
	}
			
	public void infer(int index, Codes.FieldLoad code, Code.Block.Entry entry,
			Env environment) {
		Constant src = environment.get(code.operand(0));
		
		Constant result = null;
		if (src instanceof Constant.Record) {
			Constant.Record rec = (Constant.Record) src;
			result = rec.values.get(code.field);
		}
		
		assign(code.target(),result,environment,index,entry);		
	}
	
	public void infer(int index, Codes.TupleLoad code, Code.Block.Entry entry,
			Env environment) {
		Constant src = environment.get(code.operand(0));
		
		Constant result = null;
		if (src instanceof Constant.Tuple) {
			Constant.Tuple tup = (Constant.Tuple) src;
			result = tup.values.get(code.index);
		}
		
		assign(code.target(),result,environment,index,entry);	
	}
	
	public void infer(int index, Codes.IndirectInvoke code, Code.Block.Entry entry,
			Env environment) {
		
		// TODO: in principle we can do better here in the case that the target
		// is a constant. This seems pretty unlikely though ...
		
		if (code.target() != Codes.NULL_REG) {
			invalidate(code.target(),environment,index,entry);
		} 		
	}
	
	public void infer(int index, Codes.Invoke code, Code.Block.Entry entry,
			Env environment) {
				
		if (code.target() != Codes.NULL_REG) {
			invalidate(code.target(),environment,index,entry);
		}		
	}
	
	public void infer(int index, Codes.ListOperator code, Code.Block.Entry entry,
			Env environment) {
		Constant lhs = environment.get(code.operand(0));
		Constant rhs = environment.get(code.operand(1));
		Constant result = null;
		switch(code.kind) {
		case APPEND:
			if (lhs instanceof Constant.List && rhs instanceof Constant.List) {
				Constant.List left = (Constant.List) lhs;
				Constant.List right = (Constant.List) rhs;
				ArrayList<Constant> values = new ArrayList<Constant>(left.values);
				values.addAll(right.values);
				result = Constant.V_LIST(values);
			}
			break;
		case LEFT_APPEND:
			if (lhs instanceof Constant.List && isRealConstant(rhs)) {
				Constant.List left = (Constant.List) lhs;
				Constant right = (Constant) rhs;
				ArrayList<Constant> values = new ArrayList<Constant>(left.values);
				values.add(right);
				result = Constant.V_LIST(values);
			}
			break;
		case RIGHT_APPEND:
			if (isRealConstant(lhs) && rhs instanceof Constant.List) {
				Constant left = (Constant) lhs;
				Constant.List right = (Constant.List) rhs;
				ArrayList<Constant> values = new ArrayList<Constant>();
				values.add(left);
				values.addAll(right.values);
				result = Constant.V_LIST(values);
			} 
		}
		
		assign(code.target(),result,environment,index,entry);
	}
	
	public void infer(int index, Codes.Lambda code, Code.Block.Entry entry,
			Env environment) {
		// For now, don't do anything!
		assign(code.target(),null,environment,index,entry);
	}
	
	public void infer(int index, Codes.LengthOf code, Code.Block.Entry entry,
			Env environment) {
		Constant val = environment.get(code.operand(0));
		Constant result = null;
		
		if(val instanceof Constant.List) {
			Constant.List list = (Constant.List) val;
			result = Constant.V_INTEGER(BigInteger.valueOf(list.values.size()));			
		} else if(val instanceof Constant.Set) {
			Constant.Set list = (Constant.Set) val;
			result = Constant.V_INTEGER(BigInteger.valueOf(list.values.size()));			
		} 
		
		assign(code.target(),result,environment,index,entry);
	}
	
	public void infer(int index, Codes.SubList code, Code.Block.Entry entry,
			Env environment) {
		Constant list = environment.get(code.operands()[0]);
		Constant start = environment.get(code.operands()[1]);
		Constant end = environment.get(code.operands()[2]);
		Constant result = null;
		if (list instanceof Constant.List && start instanceof Constant.Decimal
				&& end instanceof Constant.Decimal) {
			Constant.Decimal en = (Constant.Decimal) end;
			Constant.Decimal st = (Constant.Decimal) start;
			if (en.value.scale() <= 0 && st.value.scale() <= 0) {
				Constant.List li = (Constant.List) list;
				int eni = en.value.intValue();
				int sti = st.value.intValue();
				if (BigRational.valueOf(eni).equals(en.value) && eni >= 0
						&& eni <= li.values.size()
						&& BigRational.valueOf(sti).equals(st.value)
						&& sti >= 0 && sti <= li.values.size()) {
					ArrayList<Constant> nvals = new ArrayList<Constant>();
					for (int i = sti; i < eni; ++i) {
						nvals.add(li.values.get(i));
					}
					result = Constant.V_LIST(nvals);
				}
			}
		}
		
		assign(code.target(),result,environment,index,entry);
	}
	
	public void infer(int index, Codes.IndexOf code, Code.Block.Entry entry,
			Env environment) {		
		Constant src = environment.get(code.operand(0));
		Constant idx = environment.get(code.operand(1));
		Constant result = null;
		if (idx instanceof Constant.Decimal && src instanceof Constant.List) {
			Constant.Decimal num = (Constant.Decimal) idx;
			Constant.List list = (Constant.List) src;
			if (num.value.scale() <= 0) {
				int i = num.value.intValue();
				if (BigRational.valueOf(i).equals(num.value) && i >= 0
						&& i < list.values.size()) {
					result = list.values.get(i);
				}
			}
		} else if (src instanceof Constant.Strung
				&& idx instanceof Constant.Decimal) {
			Constant.Strung str = (Constant.Strung) src;
			Constant.Decimal num = (Constant.Decimal) idx;
			if (num.value.scale() <= 0) {
				int i = num.value.intValue();
				if (i >= 0 && i < str.value.length()) {
					// TO DO: need to actually push a character here
				}
			}
		}
		
		assign(code.target(),result,environment,index,entry);	
	}
	
	public void infer(int index, Codes.Assign code, Code.Block.Entry entry,
			Env environment) {

		Constant result = environment.get(code.operand(0));
		
		if (result == null) {
			result = new Alias(code.operand(0));
		}

		// FIXME: problem with remapping here?
		
		assign(code.target(),result,environment,index,entry);
	}
	
	public void infer(int index, Codes.Update code, Code.Block.Entry entry,
			Env environment) {		
		// TODO: implement this!		
		invalidate(code.target(),environment,index,entry);
	}
	
	public void infer(int index, Codes.NewMap code, Code.Block.Entry entry,
			Env environment) {
		HashMap<Constant, Constant> values = new HashMap<Constant, Constant>();
		boolean isValue = true;
		int[] code_operands = code.operands();
		for (int i = 0; i != code_operands.length; i = i + 2) {
			Constant key = environment.get(code_operands[i]);
			Constant val = environment.get(code_operands[i+1]);
			if (isRealConstant(key) && isRealConstant(val)) {
				values.put(key, val);
			} else {
				isValue = false;
			}
		}
		Constant result = null;
		if (isValue) {
			result = Constant.V_MAP(values);
		}
		
		assign(code.target(),result,environment,index,entry);
	}
	
	public void infer(int index, Codes.NewRecord code, Code.Block.Entry entry,
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
		
		assign(code.target(),result,environment,index,entry);
	}
	
	public void infer(int index, Codes.NewList code, Code.Block.Entry entry,
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
			result = Constant.V_LIST(values);			
		}
		assign(code.target(),result,environment,index,entry);
	}
	
	public void infer(int index, Codes.NewSet code, Code.Block.Entry entry,
			Env environment) {
		HashSet<Constant> values = new HashSet<Constant>();

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
			result = Constant.V_SET(values);			
		}
		assign(code.target(),result,environment,index,entry);
	}
	
	public void infer(int index, Codes.NewTuple code, Code.Block.Entry entry,
			Env environment) {
		ArrayList<Constant> values = new ArrayList<Constant>();		

		boolean isValue=true;
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
			result = Constant.V_TUPLE(values);			
		}
		assign(code.target(),result,environment,index,entry);
	}
	
	public void infer(int index, Codes.Return code, Code.Block.Entry entry,
			Env environment) {
		remap(environment,index,entry);
	}

	public void infer(int index, Codes.SetOperator code, Code.Block.Entry entry,
			Env environment) {						
		Constant result = null;
		Constant lhs = environment.get(code.operand(0));
		Constant rhs = environment.get(code.operand(1));
		switch(code.kind) {
		case UNION:
			if (lhs instanceof Constant.Set
					&& rhs instanceof Constant.Set) {
				Constant.Set lv = (Constant.Set) lhs;
				Constant.Set rv = (Constant.Set) rhs;
				result = lv.union(rv);
			}
			break;
		case LEFT_UNION:
			if(lhs instanceof Constant.Set && isRealConstant(rhs)) {
				Constant.Set lv = (Constant.Set) lhs;
				Constant rv = (Constant) rhs;
				result = lv.add(rv);
			} 
			break;
		case RIGHT_UNION:
			if(isRealConstant(lhs) && rhs instanceof Constant.Set) {
				Constant lv = (Constant) lhs;
				Constant.Set rv = (Constant.Set) rhs;
				result = rv.add(lv);
			}
			break;
		case INTERSECTION:
			if (lhs instanceof Constant.Set
					&& rhs instanceof Constant.Set) {
				Constant.Set lv = (Constant.Set) lhs;
				Constant.Set rv = (Constant.Set) rhs;
				result = lv.intersect(rv);
			} 
			break;
		case LEFT_INTERSECTION:
			if (lhs instanceof Constant.Set && isRealConstant(rhs)) {
				Constant.Set lv = (Constant.Set) lhs;
				Constant rv = (Constant) rhs;
				if (lv.values.contains(rv)) {
					HashSet<Constant> nset = new HashSet<Constant>();
					nset.add(rv);
					result = Constant.V_SET(nset);
				} else {
					result = Constant.V_SET(Collections.EMPTY_SET);
				}
			}
			break;
		case RIGHT_INTERSECTION:
			if(isRealConstant(lhs) && rhs instanceof Constant.Set) {
				Constant lv = (Constant) lhs;
				Constant.Set rv = (Constant.Set) rhs;
				if(rv.values.contains(lv)) {
					HashSet<Constant> nset = new HashSet<Constant>();
					nset.add(lv);
					result = Constant.V_SET(nset);
				} else {
					result = Constant.V_SET(Collections.EMPTY_SET);
				}
			}
			break;
		case DIFFERENCE:
			if (lhs instanceof Constant.Set && rhs instanceof Constant.Set) {
				Constant.Set lv = (Constant.Set) lhs;
				Constant.Set rv = (Constant.Set) rhs;
				result = lv.difference(rv);
			}
			break;
		case LEFT_DIFFERENCE:
			if(lhs instanceof Constant.Set && isRealConstant(rhs)) {
				Constant.Set lv = (Constant.Set) lhs;
				Constant rv = (Constant) rhs;
				result = lv.remove(rv);
			} 
			break;
		}
		 		
		assign(code.target(),result,environment,index,entry);
	}
	
	public void infer(int index, Codes.StringOperator code, Code.Block.Entry entry,
			Env environment) {
		Constant lhs = environment.get(code.operand(0));
		Constant rhs = environment.get(code.operand(1));
		Constant result = null;
		switch(code.kind) {
		case APPEND:
			if(lhs instanceof Constant.Strung && rhs instanceof Constant.Strung) {
				Constant.Strung left = (Constant.Strung) lhs;
				Constant.Strung right = (Constant.Strung) rhs;
				result = Constant.V_STRING(left.value + right.value);
			} 
			break;
		case LEFT_APPEND:
			// TODO: need to add Value.Char
			break;
		case RIGHT_APPEND:
			// TODO: need to add Value.Char
			break;
		}

		assign(code.target(),result,environment,index,entry);
	}
	
	public void infer(int index, Codes.SubString code, Code.Block.Entry entry,
			Env environment) {		
		
		Constant src = environment.get(code.operands()[0]);
		Constant start = environment.get(code.operands()[1]);
		Constant end = environment.get(code.operands()[2]);
		
		Constant result = null;
		if (src instanceof Constant.Strung && start instanceof Constant.Decimal
				&& end instanceof Constant.Decimal) {
			Constant.Decimal en = (Constant.Decimal) end;
			Constant.Decimal st = (Constant.Decimal) start;
			if (en.value.scale() <= 0 && st.value.scale() <= 0) {
				Constant.Strung str = (Constant.Strung) src;
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
					result = Constant.V_STRING(nval);					
				}
			}
		} 
		
		assign(code.target(),result,environment,index,entry);
	}
	
	public void infer(int index, Codes.Invert code, Code.Block.Entry entry,
			Env environment) {
		Constant val = environment.get(code.operand(0));
		Constant result = null;
		
		if (val instanceof Constant.Byte) {
			Constant.Byte num = (Constant.Byte) val;
			result = Constant.V_BYTE((byte) ~num.value);			
		}

		assign(code.target(),result,environment,index,entry);
	}
	
	public void infer(int index, Codes.UnaryOperator code, Code.Block.Entry entry,
			Env environment) {
		// needs to be updated to support numerator and denominator
		Constant val = environment.get(code.operand(0));
		Constant result = null;
		
		switch(code.kind) {
			case NEG:
				if(val instanceof Constant.Decimal) {
					Constant.Decimal num = (Constant.Decimal) val;
					result = Constant.V_DECIMAL(num.value.negate());
				} else if (val instanceof Constant.Integer) {
					Constant.Integer num = (Constant.Integer) val;
					result = Constant.V_INTEGER(num.value.negate());
				}
		}
				
		assign(code.target(),result,environment,index,entry);
	}
	
	public void infer(int index, Codes.NewObject code, Code.Block.Entry entry,
			Env environment) {
		invalidate(code.target(),environment,index,entry);
	}
	
	public void infer(int index, Codes.Throw code, Code.Block.Entry entry,
			Env environment) {		
		remap(environment,index,entry);
	}
	
	public void infer(int index, Codes.Dereference code, Code.Block.Entry entry,
			Env environment) {
		invalidate(code.target(),environment,index,entry);
	}	
	
	@Override
	public Pair<Env, Env> propagate(int index, Codes.If code, Entry entry,
			Env environment) {
		environment = (Env) environment.clone();

		// TODO: could do more here!
		
		remap(environment, index, entry);

		return new Pair(environment, environment);
	}
	
	@Override
	public Pair<Env, Env> propagate(int index,
			Codes.IfIs code, Entry entry, Env environment) {
		environment = (Env) environment.clone();
		
		// TODO: could do more here!
		
		// NOTE: it's unsafe to remap here since the variable being retyped will
		// need to be subsequently cast. Furthermore, any remapping which has
		// occurred on this variable should be undone.
		Constant c = environment.get(code.operand);
		if(c instanceof Alias) {
			environment.set(code.operand,null);
		}
		
		return new Pair(environment, environment);
	}
	
	@Override
	public List<Env> propagate(int index, Codes.Switch code, Entry entry,
			Env environment) {
		environment = (Env) environment.clone();

		Constant val = environment.get(code.operand);

		// TODO: could do more here!
		
		remap(environment, index, entry);
		
		ArrayList<Env> stores = new ArrayList();
		for (int i = 0; i != code.branches.size(); ++i) {
			stores.add(environment);
		}

		return stores;
	}

	@Override
	public Env propagate(Type handler, Codes.TryCatch tc, Env environment) {		
		Env catchEnvironment = (Env) environment.clone();		

		// TODO: implement me!
		return catchEnvironment;
	}
	
	@Override
	public Env propagate(int start, int end, Codes.Loop loop,
			Entry entry, Env environment, List<Codes.TryCatch> handlers) {
		
		environment = new Env(environment);
		
		if(loop instanceof Codes.ForAll) {
			Codes.ForAll fall = (Codes.ForAll) loop; 	
			
			// TO DO: could unroll loop if src collection is a value.
			invalidate(fall.indexOperand,environment,start,entry);
		} 
		
		// Now, kill every variable which is modified in the loop. This is a
		// safety precaution, and it's possible we could do better here in some
		// circumstances (e.g. by unrolling the loop).
		
		for(int slot : loop.modifiedOperands) {
			invalidate(slot,environment,start,entry);			
		}
		
		remap(environment, start, entry);
		
		Env oldEnv = null;
		Env newEnv = environment;				
		
		do {						
			// iterate until a fixed point reached
			oldEnv = newEnv;
			newEnv = propagate(start+1,end, oldEnv, handlers);
			newEnv = join(environment,newEnv);
		} while (!newEnv.equals(oldEnv));
		
		return newEnv;		
	}
	
	public void invalidate(int slot, Env environment, int index, Code.Block.Entry entry) {
		assign(slot,null,environment,index,entry);
	}
	
	public void assign(int target, Constant constant, Env environment, int index, Code.Block.Entry entry) {
		environment.set(target, constant);
		
		for (int i = 0; i != environment.size(); ++i) {
			Constant c = environment.get(i);
			if (c instanceof Alias) {
				Alias a = (Alias) c;
				if (a.reg == target) {
					environment.set(i, null);
				}
			}
		}
		
		if (isRealConstant(constant)) {
			entry = new Code.Block.Entry(Codes.Const(target, constant),
					entry.attributes());
			rewrites.put(index, new Rewrite(entry));
		} else if(constant == null) {
			
			// TODO: it's always safe to call remap after we have set target to
			// null. However, we could do better in some cases (i.e. when the
			// target register is also an operand register).
			
			remap(environment,index,entry);
		}
	}
	
	public static boolean isRealConstant(Constant c) {
		return c != null && !(c instanceof Alias);
	}
	
	public void remap(Env environment, int index, Code.Block.Entry entry) {
		Map<Integer,Integer> binding = new HashMap<Integer,Integer>();
		for(int i=0;i!=environment.size();++i) {
			Constant c = environment.get(i);
			if(c instanceof Alias) {
				Alias a = (Alias) c;
				binding.put(i,a.reg);
			}
		}
		Code old = entry.code;
		Code code = old.remap(binding);
		if(code != old) {
			entry = new Code.Block.Entry(code, entry.attributes());
			rewrites.put(index, new Rewrite(entry));
		} else {
			rewrites.put(index, null);
		}
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
		public final Code.Block.Entry rewrite;
		
		public Rewrite(Code.Block.Entry rewrite) {
			this.rewrite = rewrite;
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
