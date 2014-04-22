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

import static wycc.lang.SyntaxError.*;

import java.math.BigDecimal;
import java.util.*;

import wyautl.util.BigRational;
import wybs.lang.*;
import wycc.lang.NameID;
import wycc.lang.SyntacticElement;
import wycc.lang.Transform;
import wycc.util.Pair;
import wyfs.util.Trie;
import wyil.lang.*;
import wyil.lang.CodeBlock.Entry;
import wyil.util.dfa.BackwardFlowAnalysis;

/**
 * <p>
 * Inserts implict coercions into those methods and functions of a WYIL module.
 * This is done using a backwards dataflow analysis (hence, the name). The idea
 * is that, at any point where the type of a variable changes, a coercion should
 * be inserted. As a simple example, consider the following Whiley function:
 * </p>
 * 
 * <pre>
 * real f(int x):
 *    return x
 * </pre>
 * <p>
 * Here, the type of variable <code>x</code> changes from <code>int</code> to
 * <code>real</code> in the <code>return</code> statement. Before back
 * propagation is applied, the WYIL code for this function looks like this:
 * </p>
 * 
 * <pre>
 * real f(int x):
 *     load x : int                            
 *     return : real
 * </pre>
 * <p>
 * We can see that, at one point, the variable <code>x</code> has type
 * <code>int</code> and it is then immediately assumed to have type
 * <code>real</code>. The semantics of WYIL code dictates that we must
 * explicitly coerce variables in such situations. Thus, after back propagation
 * is applied, we have the following:
 * </p>
 * 
 * <pre>
 * real f(int x):
 *     load x : int 
 *     convert int to real             
 *     return : real
 * </pre>
 * <p>
 * Here, the <code>convert</code> bytecode is responsible for changing the
 * representation of <code>x</code> from <code>int</code> to <code>real</code>.
 * On some architectures, this will correspond to a physical change in the
 * underlying representation. On others, it may simply correspond to a no-op.
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public final class BackPropagation extends BackwardFlowAnalysis<BackPropagation.Env> implements Transform<WyilFile> {	
	private static final HashMap<Integer,CodeBlock> afterInserts = new HashMap<Integer,CodeBlock>();
	private static final HashMap<Integer,CodeBlock.Entry> rewrites = new HashMap<Integer,CodeBlock.Entry>();
	
	public BackPropagation(Builder builder) {
		super();
	}
	
	@Override
	protected WyilFile.TypeDeclaration propagate(WyilFile.TypeDeclaration type) {		
		// TODO: back propagate through type constraints
		return type;		
	}
	
	@Override
	protected Env lastStore() {				
		Env environment = new Env();		

		for (int i = 0; i < block.numSlots(); i++) {
			environment.add(Type.T_VOID);
		}		
		
		return environment;				
	}
	
	@Override
	protected WyilFile.Case propagate(WyilFile.Case mcase) {		

		// TODO: back propagate through pre- and post-conditions
		
		methodCase = mcase;
		
		List<CodeBlock> nprecondition = propagate(mcase.precondition());
		List<CodeBlock> npostcondition = propagate(mcase.postcondition());
		List<CodeBlock> nbody = propagate(mcase.body());
		
		return new WyilFile.Case(nbody, nprecondition,
				npostcondition, mcase.attributes());
	}

	protected List<CodeBlock> propagate(List<CodeBlock> blocks) {
		// Quick sanity check		
		if(blocks.size() == 0) { return Collections.EMPTY_LIST; }
		// FIXME: this is broken
		CodeBlock block = blocks.get(0);
		
		// Setup global items
		stores = new HashMap<String,Env>();
		afterInserts.clear();
		rewrites.clear();
		this.block = block;
		
		// Now, propagate through the block
		propagate(0,block.size(), lastStore(), Collections.EMPTY_LIST);	
		
		// At this point, we apply the inserts
		CodeBlock nblock = new CodeBlock(block.numInputs());
		
		for(int i=0;i!=block.size();++i) {
			CodeBlock.Entry rewrite = rewrites.get(i);			
			if(rewrite != null) {								
				nblock.append(rewrite);				
			} else {
				nblock.append(block.get(i));
			}
			CodeBlock afters = afterInserts.get(i);			
			if(afters != null) {								
				nblock.append(afters);				
			} 							
		}
		
		ArrayList<CodeBlock> nblocks = new ArrayList<CodeBlock>();
		nblocks.add(nblock);
		return nblocks;
	}
	
	@Override
	protected Env propagate(int index, Entry entry, Env environment) {						
		Code code = entry.code;							
		
		// reset the rewrites for this code, in case it changes
		afterInserts.remove(index);		
		environment = (Env) environment.clone();
		
		if(code instanceof Code.BinArithOp) {
			infer(index,(Code.BinArithOp)code,entry,environment);
		} else if(code instanceof Code.Convert) {
			infer(index,(Code.Convert)code,entry,environment);
		} else if(code instanceof Code.Const) {
			infer(index,(Code.Const)code,entry,environment);
		} else if(code instanceof Code.Debug) {
			infer(index,(Code.Debug)code,entry,environment);
		} else if(code instanceof Code.AssertOrAssume) {
			infer(index,(Code.AssertOrAssume)code,entry,environment);
		} else if(code instanceof Code.FieldLoad) {
			infer(index,(Code.FieldLoad)code,entry,environment);			
		} else if(code instanceof Code.IndirectInvoke) {
			infer(index,(Code.IndirectInvoke)code,entry,environment);
		} else if(code instanceof Code.Invoke) {
			infer(index,(Code.Invoke)code,entry,environment);
		} else if(code instanceof Code.Invert) {
			infer(index,(Code.Invert)code,entry,environment);
		} else if(code instanceof Code.Lambda) {
			infer(index,(Code.Lambda)code,entry,environment);
		} else if(code instanceof Code.Label) {
			// skip			
		} else if(code instanceof Code.BinListOp) {
			infer(index,(Code.BinListOp)code,entry,environment);
		} else if(code instanceof Code.LengthOf) {
			infer(index,(Code.LengthOf)code,entry,environment);
		} else if(code instanceof Code.SubList) {
			infer(index,(Code.SubList)code,entry,environment);
		} else if(code instanceof Code.IndexOf) {
			infer(index,(Code.IndexOf)code,entry,environment);
		} else if(code instanceof Code.Assign) {
			infer(index,(Code.Assign)code,entry,environment);
		} else if(code instanceof Code.Update) {
			infer(index,(Code.Update)code,entry,environment);
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
		} else if(code instanceof Code.UnArithOp) {
			infer(index,(Code.UnArithOp)code,entry,environment);
		} else if(code instanceof Code.Dereference) {
			infer(index,(Code.Dereference)code,entry,environment);
		} else if(code instanceof Code.Return) {
			infer(index,(Code.Return)code,entry,environment);
		} else if(code instanceof Code.Nop) {
			// skip			
		} else if(code instanceof Code.BinSetOp) {
			infer(index,(Code.BinSetOp)code,entry,environment);
		} else if(code instanceof Code.BinStringOp) {
			infer(index,(Code.BinStringOp)code,entry,environment);
		} else if(code instanceof Code.SubString) {
			infer(index,(Code.SubString)code,entry,environment);
		} else if(code instanceof Code.NewObject) {
			infer(index,(Code.NewObject)code,entry,environment);
		} else if(code instanceof Code.Throw) {
			infer(index,(Code.Throw)code,entry,environment);
		} else if(code instanceof Code.TupleLoad) {
			infer(index,(Code.TupleLoad)code,entry,environment);
		} else {			
			internalFailure("unknown: " + code.getClass().getName(),filename,entry);
			return null;
		}	
		
		// Now, update requirement for assignment register (if any)
		
		if(code instanceof Code.AbstractAssignable) {
			Code.AbstractAssignable aa = (Code.AbstractAssignable) code;
			if(aa.target != Code.NULL_REG) {
				environment.set(aa.target,Type.T_VOID);
			}
		}
		
		return environment;
	}

	protected Env infer(int index,
			Code.AssertOrAssume code, CodeBlock.Entry stmt, Env environment) {
		if(code.op == Code.Comparator.IN) {
			Type.EffectiveCollection src = (Type.EffectiveCollection) code.type;		
			environment.set(code.leftOperand,src.element());
			environment.set(code.rightOperand,code.type);
		} else {		
			environment.set(code.leftOperand,code.type);
			environment.set(code.rightOperand,code.type);
		}
		
		return environment;
	}
	
	private void infer(int index, Code.BinArithOp code, CodeBlock.Entry entry,
			Env environment) {
		Type req = environment.get(code.target);		
		coerceAfter(req,code.type,code.target,index,entry);	
		if(code.kind == Code.BinArithKind.LEFTSHIFT || code.kind == Code.BinArithKind.RIGHTSHIFT) {
			environment.set(code.leftOperand,code.type);
			environment.set(code.rightOperand,Type.T_INT);
		} else if(code.kind == Code.BinArithKind.RANGE){
			environment.set(code.leftOperand,Type.T_INT);
			environment.set(code.rightOperand,Type.T_INT);
		} else {		
			environment.set(code.leftOperand,code.type);
			environment.set(code.rightOperand,code.type);
		}
	}
	
	private void infer(int index, Code.Convert code, CodeBlock.Entry entry,
			Env environment) {		
		Type req = environment.get(code.target);
		// TODO: add insertion?
		environment.set(code.operand,code.type);
	}
	
	private void infer(int index, Code.Const code, CodeBlock.Entry entry,
			Env environment) {
		Type req = environment.get(code.target);

		if (req.equals(code.constant.type()) || req == Type.T_VOID) {
			// do nout!
		} else if(req == Type.T_ANY) {
			// This is really a strange hack which will eventually be eliminated
			// when all registers have fixed types. The issue is that we cannot
			// perform any conversion on the constant itself, since every
			// constant has a fixed type. Therefore, to ensure that the correct
			// (e.g. JVM) register type is given we must perform an explicit
			// coercion. 
			coerceAfter(req,code.constant.type(),code.target,index,entry);
		} else {
			Constant nconstant;
			if (req == Type.T_STRING) {
				// String coercion!
				nconstant = Constant.V_STRING(code.constant.toString());
			} else {
				nconstant = convert(req, code.constant, entry);
			}
			
			if(!nconstant.equals(code.constant)) {
				// Something has changed
				rewrites.put(
						index,
						new CodeBlock.Entry(Code.Const(code.target, nconstant), entry
								.attributes()));
			}
			
			if(!nconstant.type().equals(req)) {
				// After the conversion, we may still need to use an explicit
				// coercion bytecode if the types still don't match. This can
				// happen, for example, if the required type is "any", then
				// we'll always need an explicit coercion since there is no
				// constant which yields the type "any".
				coerceAfter(req, nconstant.type(), code.target, index,
						entry);
			}
		}
	}
	
	private void infer(int index, Code.Debug code, CodeBlock.Entry entry,
			Env environment) {
		environment.set(code.operand,Type.T_STRING);
	}
	
	private void infer(int index, Code.FieldLoad code, CodeBlock.Entry entry,
			Env environment) {		
		Type req = environment.get(code.target);
		Type field = code.type.fields().get(code.field);
		coerceAfter(req,field,code.target,index,entry);		
		environment.set(code.operand, (Type) code.type);				
	}
	
	private void infer(int index, Code.IndirectInvoke code, CodeBlock.Entry entry,
			Env environment) {

		if(code.type.ret() != Type.T_VOID && code.target >= 0) {
			Type req = environment.get(code.target);
			coerceAfter(req,code.type.ret(),code.target,index,entry);			
		}
		
		environment.set(code.operand,code.type);
				
		for(int i=0;i!=code.operands.length;++i) {
			int operand = code.operands[i];
			Type type = code.type.params().get(i);
			environment.set(operand,type);
		}		
	}
	
	private void infer(int index, Code.Invoke code, CodeBlock.Entry entry,
			Env environment) {

		if(code.type.ret() != Type.T_VOID && code.target >= 0) {
			Type req = environment.get(code.target);
			coerceAfter(req,code.type.ret(),code.target,index,entry);			
		}	
		
		for(int i=0;i!=code.operands.length;++i) {
			int operand = code.operands[i];
			Type type = code.type.params().get(i);
			environment.set(operand,type);
		}			
	}
	
	private void infer(int index, Code.Invert code, CodeBlock.Entry entry,
			Env environment) {
		Type req = environment.get(code.target);
		// FIXME: add support for dictionaries
		coerceAfter(req,code.type,code.target,index,entry);
		environment.set(code.operand, code.type);
	}
	
	private void infer(int index, Code.BinListOp code, CodeBlock.Entry entry,
			Env environment) {		
		Type req = environment.get(code.target);
		Type codeType = (Type) code.type;
		coerceAfter(req,codeType,code.target,index,entry);
		switch(code.kind) {
		case APPEND:
			environment.set(code.leftOperand,codeType);
			environment.set(code.rightOperand,codeType);
			break;
		case LEFT_APPEND:
			environment.set(code.leftOperand,codeType);
			environment.set(code.rightOperand,code.type.element());
			break;
		case RIGHT_APPEND:
			environment.set(code.leftOperand,code.type.element());
			environment.set(code.rightOperand,codeType);
			break;
		}
	}
	
	private void infer(int index, Code.Lambda code, CodeBlock.Entry entry,
			Env environment) {
		List<Type> params = code.type.params();
		int[] operands = code.operands;
		for (int i = 0; i != operands.length; ++i) {
			int operand = operands[i];
			if (operand != Code.NULL_REG) {
				environment.set(operand, params.get(i));
			}
		}
	}
	
	private void infer(int index, Code.LengthOf code, CodeBlock.Entry entry,
			Env environment) {		
		Type req = environment.get(code.target);
		coerceAfter(req,Type.T_INT,code.target,index,entry);
		environment.set(code.operand,(Type) code.type);
	}
	
	private void infer(int index, Code.SubList code, CodeBlock.Entry entry,
			Env environment) {		
		Type req = environment.get(code.target);
		Type codeType = (Type) code.type;
		coerceAfter(req,codeType,code.target,index,entry);
		environment.set(code.operands[0],codeType);
		environment.set(code.operands[1],Type.T_INT);
		environment.set(code.operands[2],Type.T_INT);
	}
	
	private void infer(int index, Code.IndexOf code, CodeBlock.Entry entry,
			Env environment) {
		Type req = environment.get(code.target);
		coerceAfter(req,code.type.value(),code.target,index,entry);		
		environment.set(code.leftOperand,(Type) code.type);
		environment.set(code.rightOperand,code.type.key());				
	}
	
	private void infer(int index, Code.Assign code, CodeBlock.Entry entry,
			Env environment) {		
		Type req = environment.get(code.target);
		coerceAfter(req,code.type,code.target,index,entry);
		environment.set(code.operand,code.type);		
	}
	
	private void infer(int index, Code.Update code, CodeBlock.Entry stmt,
			Env environment) {
		
		Type src = environment.get(code.target);
		
		if(src == Type.T_VOID) {
			src = code.afterType;
		}
		
		// The first job is to make sure we've got the right types for indices
		// and key values loaded onto the stack.
				
		int i = 0;
		for (Code.LVal lv : code) {
			if (lv instanceof Code.StringLVal || lv instanceof Code.ListLVal) {
				environment.set(code.operands[i++], Type.T_INT);
			} else if (lv instanceof Code.MapLVal) {
				Code.MapLVal dlv = (Code.MapLVal) lv;
				environment.set(code.operands[i++], dlv.rawType().key());
			} else {
				// RecordLVal and ProcessLVal have no stack requirement
			}
		}
		
		// The second job is to try and determine whether there is any general
		// requirement on the value being assigned.
		
		environment.set(code.operand,code.rhs());		
		environment.set(code.target, code.type);		
	}
	
	private void infer(int index, Code.NewMap code, CodeBlock.Entry entry,
			Env environment) {
		Type req = environment.get(code.target);
		
		// TODO: could do better here by rewriting bytecode. For example, if we
		// require a set then changing bytecode to newset makes sense!
	 	
		coerceAfter(req,code.type,code.target,index,entry);
		
		Type key = code.type.key();
		Type value = code.type.value();
		for(int i=0;i!=code.operands.length;i=i+2) {
			int keyOperand = code.operands[i];
			int valueOperand = code.operands[i+1];
			environment.set(keyOperand,key);
			environment.set(valueOperand,value);					
		}					
	}
	
	private void infer(int index, Code.NewRecord code, CodeBlock.Entry entry,
			Env environment) {
		Type req = environment.get(code.target);
		coerceAfter(req, code.type,code.target,index, entry);
		ArrayList<String> keys = new ArrayList<String>(code.type.keys());
		Collections.sort(keys);
		Map<String, Type> fields = code.type.fields();
		for (int i = 0; i != keys.size(); ++i) {
			String key = keys.get(i);
			int operand = code.operands[i];
			environment.set(operand, fields.get(key));
		}
	}
	
	private void infer(int index, Code.NewList code, CodeBlock.Entry entry,
			Env environment) {
		Type req = environment.get(code.target);
		
		coerceAfter(req, code.type, code.target, index, entry);
		Type value = code.type.element();
		for (int operand : code.operands) {
			environment.set(operand, value);
		}
	}
	
	private void infer(int index, Code.NewSet code, CodeBlock.Entry entry,
			Env environment) {
		Type req = environment.get(code.target);
		coerceAfter(req,code.type,code.target,index,entry);		
		Type value = code.type.element();
		for (int operand : code.operands) {
			environment.set(operand, value);
		}		
	}
	
	private void infer(int index, Code.NewTuple code, CodeBlock.Entry entry,
			Env environment) {
		Type req = environment.get(code.target);
		coerceAfter(req,code.type,code.target,index,entry);				
		for(int i=0;i!=code.operands.length;++i) {
			int operand = code.operands[i];
			Type type = code.type.element(i);
			environment.set(operand,type);					
		}
	}
	
	private void infer(int index, Code.Return code, CodeBlock.Entry entry,
			Env environment) {
		if(code.type != Type.T_VOID) {
			environment.set(code.operand,code.type);
		}
	}

	private void infer(int index, Code.BinSetOp code, CodeBlock.Entry entry,
			Env environment) {		
		Type req = environment.get(code.target);
		Type codeType = (Type) code.type;
		coerceAfter(req,codeType,code.target,index,entry);
		switch(code.kind) {
		case UNION:
		case INTERSECTION:
		case DIFFERENCE:
			environment.set(code.leftOperand,codeType);
			environment.set(code.rightOperand,codeType);
			break;
		case LEFT_UNION:
		case LEFT_INTERSECTION:
		case LEFT_DIFFERENCE:
			environment.set(code.leftOperand,codeType);
			environment.set(code.rightOperand,code.type.element());
			break;
		case RIGHT_UNION:
		case RIGHT_INTERSECTION:	
			environment.set(code.leftOperand,code.type.element());
			environment.set(code.rightOperand,codeType);
			break;
		}
	}
	
	private void infer(int index, Code.BinStringOp code, CodeBlock.Entry entry,
			Env environment) {				
		Type req = environment.get(code.target);
		coerceAfter(req,Type.T_STRING,code.target,index,entry);
		switch(code.kind) {
		case APPEND:
			environment.set(code.leftOperand,Type.T_STRING);
			environment.set(code.rightOperand,Type.T_STRING);
			break;
		case LEFT_APPEND:
			environment.set(code.leftOperand,Type.T_STRING);
			environment.set(code.rightOperand,Type.T_CHAR);
			break;
		case RIGHT_APPEND:					
			environment.set(code.leftOperand,Type.T_CHAR);
			environment.set(code.rightOperand,Type.T_STRING);
			break;
		}
	}
		
	private void infer(int index, Code.SubString code, CodeBlock.Entry entry,
			Env environment) {				
		Type req = environment.get(code.target);
		coerceAfter(req,Type.T_STRING,code.target,index,entry);
		environment.set(code.operands[0],Type.T_STRING);
		environment.set(code.operands[1],Type.T_INT);
		environment.set(code.operands[2],Type.T_INT);		
	}
	
	private void infer(int index, Code.UnArithOp code, CodeBlock.Entry entry,
			Env environment) {
		switch(code.kind) {
			case NEG: {
				Type req = environment.get(code.target);
				coerceAfter(req,code.type,code.target,index,entry);
				environment.set(code.operand,code.type);
				break;
			}
			case NUMERATOR:
			case DENOMINATOR: {
				Type req = environment.get(code.target);
				coerceAfter(req,Type.T_INT,code.target,index,entry);
				environment.set(code.operand,Type.T_REAL);
			}
		}
		
	}
	
	private void infer(int index, Code.NewObject code, CodeBlock.Entry entry,
			Env environment) {
		Type req = environment.get(code.target);
		if(req instanceof Type.Reference) { 
			Type.Reference tp = (Type.Reference) req;
			coerceAfter(tp.element(), code.type.element(), code.operand, index,
					entry);	
			environment.set(code.operand,tp.element());
		} else {
			// default
			environment.set(code.operand,code.type.element());
		}
	}
	
	private void infer(int index, Code.Throw code, CodeBlock.Entry entry,
			Env environment) {
		environment.set(code.operand,code.type);
	}
	
	private void infer(int index, Code.TupleLoad code, CodeBlock.Entry entry,
			Env environment) {
		Type req = environment.get(code.target);
		coerceAfter(req,code.type.elements().get(code.index),code.target,index,entry);		
		environment.set(code.operand,(Type) code.type);
	}
	
	private void infer(int index, Code.Dereference code, CodeBlock.Entry entry,
			Env environment) {		
		Type req = environment.get(code.target);	
		coerceAfter(req,code.type.element(),code.target,index,entry);		
		environment.set(code.operand,code.type);
	}	
	
	@Override
	protected Env propagate(int index,
			Code.If code, Entry stmt, Env trueEnv, Env falseEnv) {
		
		Env environment = join(trueEnv,falseEnv);
		
		if(code.op == Code.Comparator.IN) {
			Type.EffectiveCollection src = (Type.EffectiveCollection) code.type;		
			environment.set(code.leftOperand,src.element());
			environment.set(code.rightOperand,code.type);
		} else {		
			environment.set(code.leftOperand,code.type);
			environment.set(code.rightOperand,code.type);
		}
		
		return environment;
	}
	
	@Override
	protected Env propagate(int index,
			Code.IfIs code, Entry stmt, Env trueEnv, Env falseEnv) {
		
		Env environment = join(trueEnv,falseEnv);
		
		environment.set(code.operand,code.type);
		
		return environment;
	}
	
	@Override
	protected Env propagate(int index, Code.Switch code, Entry stmt,
			List<Env> environments, Env defEnv) {

		Env environment = defEnv;

		for (int i = 0; i != code.branches.size(); ++i) {
			environment = join(environment, environments.get(i));
		}

		environment.set(code.operand, code.type);
		return environment;
	}
	
	@Override
	protected Env propagate(Type handler, Env normalEnv, Env exceptionEnv) {
		// FIXME: if there are any constraints coming from the exceptional
		// environment, then we need to translate them into coercions at the
		// beginning of the catch handler.
		return normalEnv;
	}
		
	@Override
	protected Env propagate(int start, int end, Code.Loop loop,
			Entry stmt, Env environment, List<Pair<Type,String>> handlers) {

		environment = new Env(environment); 

		Env oldEnv = null;
		Env newEnv = null;
		
		do {			
			// iterate until a fixed point reached
			oldEnv = newEnv != null ? newEnv : environment;
			newEnv = propagate(start+1,end, oldEnv, handlers);
			newEnv = join(environment,newEnv);
		} while (!newEnv.equals(oldEnv));
		
		if(loop instanceof Code.ForAll) {
			Code.ForAll fall = (Code.ForAll) loop; 								
			environment.set(fall.sourceOperand, (Type) fall.type);			
			// FIXME: a conversion here might be necessary?			
			environment.set(fall.indexOperand,Type.T_VOID);
		} 		
		
		return environment;		
	}
	
	/**
	 * Explicitly coerce a constant to a given type.
	 * 
	 * @param to
	 * @param from
	 * @param elem
	 * @return
	 */
	public Constant convert(Type to, Constant from, SyntacticElement elem) {
		if(to.equals(from.type()) || to == Type.T_ANY) {
			return from;
		} else if(to == Type.T_REAL && from instanceof Constant.Integer) {
			Constant.Integer i = (Constant.Integer) from;
			return Constant.V_DECIMAL(new BigDecimal(i.value));
		} else if(to instanceof Type.Set && from instanceof Constant.Set) {
			Type.Set ts = (Type.Set) to;		
			Constant.Set cs = (Constant.Set) from;
			Type ts_element = ts.element();
			HashSet<Constant> values = new HashSet<Constant>();
			for(Constant c : cs.values) {
				values.add(convert(ts_element,c,elem));
			}
			return Constant.V_SET(values);
		} else if(to instanceof Type.Map && from instanceof Constant.Map) {
			Type.Map tm = (Type.Map) to;		
			Constant.Map cm = (Constant.Map) from;
			Type tm_key = tm.key();
			Type tm_value = tm.value();
			HashMap<Constant,Constant> values = new HashMap<Constant,Constant>();
			for(Map.Entry<Constant,Constant> c : cm.values.entrySet()) {
				values.put(convert(tm_key, c.getKey(), elem),
						convert(tm_value, c.getValue(), elem));
			}
			return Constant.V_MAP(values);
		} else if(to instanceof Type.List && from instanceof Constant.List) {
			Type.List tl = (Type.List) to;		
			Constant.List cl = (Constant.List) from;
			Type tl_element = tl.element();
			ArrayList<Constant> values = new ArrayList<Constant>();
			for(Constant c : cl.values) {
				values.add(convert(tl_element,c,elem));
			}
			return Constant.V_LIST(values);
		} else if(to instanceof Type.Record && from instanceof Constant.Record) {
			Type.Record tr = (Type.Record) to;
			Constant.Record cr = (Constant.Record) from;
			HashMap<String, Type> tm_fields = tr.fields();
			HashMap<String, Constant> values = new HashMap<String, Constant>();
			for (Map.Entry<String, Constant> c : cr.values.entrySet()) {
				String field = c.getKey();
				values.put(field,
						convert(tm_fields.get(field), c.getValue(), elem));
			}
			return Constant.V_RECORD(values);
		} else {
			// Observe that this is always safe, although we probably can do
			// better in some cases. The reason that it's safe is simply that an
			// explicit coercion bytecode will always be added if the type of
			// the generated constant does not exactly match the required type.
			return from;
		}
	}
	
	/**
	 * Insert an implicit coercion after a given bytecode.
	 * 
	 * @param to
	 *            --- type being coerced into.
	 * @param from
	 *            --- type being coerced from.
	 * @param target
	 *            --- target register being coerced.
	 * @param index
	 *            --- index of bytecode in function/method body.
	 * @param elem
	 *            --- syntactic element for bytecode.
	 */
	private void coerceAfter(Type to, Type from, int target, int index, SyntacticElement elem) {					
		
		if (to.equals(from) || to == Type.T_VOID) {
			afterInserts.remove(index);
		} else if(to == Type.T_STRING) {
			// this indicates a string conversion is required
			Pair<Type.Function, NameID> p = choseToString(from);
			to = p.first().params().get(0);

			CodeBlock block = new CodeBlock(0);
			if (!from.equals(to)) {
				block.append(Code.Convert(from, target, target, to),
						elem.attributes());
			}
			block.append(
					Code.Invoke(p.first(), target, new int[] { target },
							p.second()), elem.attributes());
			afterInserts.put(index, block);
		} else {
			CodeBlock block = new CodeBlock(0);
			block.append(Code.Convert(from, target, target, to), elem.attributes());
			afterInserts.put(index,block);
		}
		
		// this method *should* be structured as follows:
		
//		if (to.equals(from)) {
//			///
//		} else if(Type.isExplicitCoerciveSubtype(to,from)) {					
//			...
//		} else if(to == Type.T_STRING) {
//			...
//		} else {
//			...
//		}
	}	
	
	/**
	 * Choose the best toString method based on the given type.
	 * 
	 * @param key
	 * @return
	 */
	private static Pair<Type.Function,NameID> choseToString(Type type) {
		Type.Function ft;
		NameID name;
		
		if (type == Type.T_BYTE) {
			ft = (Type.Function) Type.Function(Type.T_STRING, Type.T_VOID,
					Type.T_BYTE);
			name = new NameID(Trie.fromString("whiley/lang/Byte"),
					"toString");
		} else if (type == Type.T_CHAR) {
			ft = (Type.Function) Type.Function(Type.T_STRING, Type.T_VOID,
					Type.T_CHAR);
			name = new NameID(Trie.fromString("whiley/lang/Char"),
					"toString");
		} else {
			ft = (Type.Function) Type.Function(Type.T_STRING, Type.T_VOID,
					Type.T_ANY);
			name = new NameID(Trie.fromString("whiley/lang/Any"),
					"toString");
		}
		
		return new Pair<Type.Function,NameID>(ft,name);
	}
	
	private Env join(Env env1, Env env2) {
		if (env2 == null) {
			return env1;
		} else if (env1 == null) {
			return env2;
		}
		
		Env env = new Env();
		for (int i = 0; i != Math.min(env1.size(), env2.size()); ++i) {
			env.add(Type.Union(env1.get(i), env2.get(i)));
		}

		/**
		 * <p>
		 * The following may seem strange, but it's necessary to support
		 * constraint failures which can happen in the middle of expressions. In
		 * such case, a conditional is inserted by the "constraint inline"
		 * phase, where it fails on one side but succeeds on the other. Stack
		 * requirements may be present from the succeeding branch and we need to
		 * propagate those backwards still.
		 * </p>
		 * <p>
		 * There are possibly other ways this could be handled. For example, we
		 * might use null to signal that a particular branch is heading
		 * immediately into a fail statement.
		 * </p>
		 */		
		if(env1.size() > env2.size()) {
			for(int i=env.size();i!=env1.size();++i) {
				env.add(env1.get(i));
			}
		} else if(env2.size() > env1.size()) {
			for(int i=env.size();i!=env2.size();++i) {
				env.add(env2.get(i));
			}
		}				
		
		return env;
	}
	
	protected static class Env extends ArrayList<Type> {
		public Env() {
		}
		public Env(Collection<Type> v) {
			super(v);
		}		
		public Env clone() {
			return new Env(this);
		}
	}
}
