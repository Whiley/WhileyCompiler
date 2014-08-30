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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import wybs.lang.Builder;
import wycc.lang.Attribute;
import wycc.lang.NameID;
import wycc.lang.SyntacticElement;
import wycc.lang.SyntaxError;
import wycc.lang.Transform;
import wycc.util.ResolveError;
import wyfs.lang.Path;
import wyil.*;
import wyil.lang.*;
import wyil.lang.Code.Block.Entry;
import wyil.util.ErrorMessages;
import static wycc.lang.SyntaxError.*;
import static wyil.util.ErrorMessages.*;
import wyautl.util.BigRational;

/**
 * The purpose of this transform is two-fold:
 * <ol>
 * <li>To inline preconditions for method invocations.</li>
 * <li>To inline preconditions for division and list/dictionary access
 * expressions</li>
 * <li>To inline postcondition checks. This involves generating the appropriate
 * shadows for local variables referenced in post-conditions</li>
 * <li>To inline dispatch choices into call-sites. This offers a useful
 * optimisation in situations when we can statically determine that a subset of
 * cases is the dispatch target.</li>
 * </ol>
 * 
 * @author David J. Pearce
 * 
 */
public class RuntimeAssertions implements Transform<WyilFile> {
	private final Builder builder;	
	private String filename;
	
	/**
	 * Determines whether verification is enabled or not.
	 */
	private boolean enabled = getEnable();
	
	public RuntimeAssertions(Builder builder) {
		this.builder = builder;
	}
	
	public RuntimeAssertions(Builder builder, String filename) {
		this.builder = builder;
		this.filename = filename;
	}
	
	public static String describeEnable() {
		return "Enable/disable runtime assertions";
	}
	
	public static boolean getEnable() {
		return true; // default value
	}
	
	public void setEnable(boolean flag) {
		this.enabled = flag;
	}
	
	public void apply(WyilFile module) {
		if (enabled) {
			this.filename = module.filename();

			for (WyilFile.Block d : module.blocks()) {
				if (d instanceof WyilFile.TypeDeclaration) {
					WyilFile.TypeDeclaration td = (WyilFile.TypeDeclaration) d;
					module.replace(td, transform(td));
				} else if (d instanceof WyilFile.FunctionOrMethodDeclaration) {
					WyilFile.FunctionOrMethodDeclaration md = (WyilFile.FunctionOrMethodDeclaration) d;
					if (!md.hasModifier(Modifier.NATIVE)) {
						// native functions/methods don't have bodies
						module.replace(md, transform(md));
					}
				}
			}
		}
	}
	
	public WyilFile.TypeDeclaration transform(WyilFile.TypeDeclaration type) {
		Code.Block invariant = type.invariant();

		if (invariant != null) {
			int freeSlot = invariant.numSlots();
			Code.Block nInvariant = new Code.Block(1);
			for (int i = 0; i != invariant.size(); ++i) {
				Code.Block.Entry entry = invariant.get(i);
				Code.Block nblk = transform(entry, freeSlot, null, null);
				if (nblk != null) {
					nInvariant.addAll(nblk);
				}
				nInvariant.add(entry);
			}
			invariant = nInvariant;
		}

		return new WyilFile.TypeDeclaration(type.modifiers(), type.name(),
				type.type(), invariant, type.attributes());
	}
	
	public WyilFile.FunctionOrMethodDeclaration transform(WyilFile.FunctionOrMethodDeclaration method) {
		ArrayList<WyilFile.Case> cases = new ArrayList<WyilFile.Case>();
		for(WyilFile.Case c : method.cases()) {
			cases.add(transform(c,method));
		}
		return new WyilFile.FunctionOrMethodDeclaration(method.modifiers(), method.name(), method.type(), cases);
	}
	
	public WyilFile.Case transform(WyilFile.Case mcase,
			WyilFile.FunctionOrMethodDeclaration method) {
		Code.Block body = mcase.body();
		Code.Block nBody = new Code.Block(body.numInputs());
		int freeSlot = buildShadows(nBody, mcase, method);

		for (int i = 0; i != body.size(); ++i) {
			Code.Block.Entry entry = body.get(i);
			Code.Block nblk = transform(entry, freeSlot, mcase, method);
			if (nblk != null) {
				nBody.addAll(nblk);
			}
			nBody.add(entry);
		}


		return new WyilFile.Case(nBody, mcase.precondition(),
				mcase.postcondition(), mcase.attributes());
	}
	
	/**
	 * <p>
	 * The build shadows method is used to create "shadow" copies of a
	 * function/method's parameters on entry to the method. This is necessary
	 * when a postcondition exists, as the postcondition may refer to the
	 * parameter values. In such case, however, the semantics of the language
	 * dictate that the postcondition refers to the parameter values <i>as they
	 * were on entry to the method</i>.
	 * </p>
	 * 
	 * <p>
	 * Thus, we must copy the parameter values into their shadows in the case
	 * that they are modified later on. This is potentially inefficient if none,
	 * or only some of the parameters are mentioned in the postcondition.
	 * However, a later pass could optimise this away as the copying assignmens
	 * would be dead-code.
	 * </p>
	 * 
	 * @param body
	 * @param mcase
	 * @param method
	 * @return
	 */
	public int buildShadows(Code.Block body, WyilFile.Case mcase,
			WyilFile.FunctionOrMethodDeclaration method) {
		int freeSlot = mcase.body().numSlots();
		if (mcase.postcondition() != null) {
			//
			List<Type> params = method.type().params();
			for (int i = 0; i != params.size(); ++i) {
				Type t = params.get(i);
				body.add(Codes.Assign(t, i + freeSlot, i));
			}
			freeSlot += params.size();
		}
		return freeSlot;
	}
	
	public Code.Block transform(Code.Block.Entry entry, int freeSlot,
			WyilFile.Case methodCase, WyilFile.FunctionOrMethodDeclaration method) {
		Code code = entry.code;
		
		try {
			// TODO: add support for indirect invokes and sends
			if(code instanceof Codes.Invoke) {
				return transform((Codes.Invoke)code, freeSlot, entry);
			} else if(code instanceof Codes.IndexOf) {
				return transform((Codes.IndexOf)code,freeSlot,entry);
			} else if(code instanceof Codes.Update) {
				return transform((Codes.Update)code,freeSlot,entry);
			} else if(code instanceof Codes.BinaryOperator) {
				return transform((Codes.BinaryOperator)code,freeSlot,entry);
			} else if(code instanceof Codes.Return) {
				return transform((Codes.Return)code,freeSlot,entry,methodCase,method);
			}
		} catch(SyntaxError e) {
			throw e;
		} catch(ResolveError e) {
			syntaxError(e.getMessage(),filename,entry,e);
		} catch(Throwable e) {
			internalFailure(e.getMessage(),filename,entry,e);
		}
		
		return null;
	}

	/**
	 * For the invoke bytecode, we need to inline any preconditions associated
	 * with the target.
	 * 
	 * @param code
	 * @param elem
	 * @return
	 */
	public Code.Block transform(Codes.Invoke code, int freeSlot,
			SyntacticElement elem) throws Exception {
		Code.Block precondition = findPrecondition(code.name, code.type(), elem);
		if (precondition != null) {
			Code.Block blk = new Code.Block(0);
			List<Type> paramTypes = code.type().params();

			// TODO: mark as check block

			int[] code_operands = code.operands();
			HashMap<Integer, Integer> binding = new HashMap<Integer, Integer>();
			for (int i = 0; i != code_operands.length; ++i) {
				binding.put(i, code_operands[i]);
			}

			precondition = resource(precondition,
					elem.attribute(Attribute.Source.class));

			importExternalAssert(blk, precondition, binding);

			return blk;
		}

		return null;
	}
	
	/**
	 * For the return bytecode, we need to inline any postcondition associated
	 * with this function/method.
	 * 
	 * @param code
	 * @param elem
	 * @return
	 */
	public Code.Block transform(Codes.Return code, int freeSlot,
			SyntacticElement elem, WyilFile.Case methodCase,
			WyilFile.FunctionOrMethodDeclaration method) {

		if (code.type != Type.T_VOID) {
			Code.Block postcondition = methodCase.postcondition();
			if (postcondition != null) {
				Code.Block nBlock = new Code.Block(0);
				HashMap<Integer, Integer> binding = new HashMap<Integer, Integer>();
				binding.put(0, code.operand);
				Type.FunctionOrMethod mtype = method.type();
				int pIndex = 1;
				int shadowIndex = methodCase.body().numSlots();
				for (Type p : mtype.params()) {
					binding.put(pIndex++, shadowIndex++);
				}
				Code.Block block = resource(postcondition,
						elem.attribute(Attribute.Source.class));
				importExternalAssert(nBlock,block, binding);
				return nBlock;
			}
		}

		return null;
	}

	/**
	 * For the listload bytecode, we need to add a check that the index is
	 * within the bounds of the list. 
	 * 
	 * @param code
	 * @param elem
	 * @return
	 */
	public Code.Block transform(Codes.IndexOf code, int freeSlot,
			SyntacticElement elem) {
		
		if (code.type() instanceof Type.EffectiveList || code.type() instanceof Type.Strung) {
			Code.Block blk = new Code.Block(0);
			String lab1 = CodeUtils.freshLabel();
			String lab2 = CodeUtils.freshLabel();
			blk.add(Codes.Assert(lab2));
			blk.add(Codes.Const(freeSlot, Constant.V_INTEGER(BigInteger.ZERO)),
					attributes(elem));
			blk.add(Codes.If(Type.T_INT, code.operand(1), freeSlot,
					Codes.Comparator.GTEQ, lab1), attributes(elem));
			blk.add(Codes.Fail("index out of bounds (negative)"),
					attributes(elem));
			blk.add(Codes.Label(lab1), attributes(elem));
			blk.add(Codes.LengthOf(code.type(), freeSlot + 1, code.operand(0)),
					attributes(elem));
			blk.add(Codes.If(Type.T_INT, code.operand(1), freeSlot + 1,
					Codes.Comparator.LT, lab2), attributes(elem));
			blk.add(Codes.Fail("index out of bounds (not less than length)"),
					attributes(elem));
			blk.add(Codes.Label(lab2), attributes(elem));
			return blk;
		} else {
			return null; // FIXME
		}
	}

	/**
	 * For the update bytecode, we need to add a check the indices of any lists 
	 * used in the update are within bounds.
	 * 
	 * @param code
	 * @param elem
	 * @return
	 */
	public Code.Block transform(Codes.Update code, int freeSlot, SyntacticElement elem) {		
		Code.Block blk = new Code.Block(0);
		blk.add(Codes.Assign(code.type(), freeSlot, code.target()));
		
		for(Codes.LVal l : code) {
			
			if (l instanceof Codes.ListLVal || l instanceof Codes.StringLVal) {
				int indexOperand;
				Type.EffectiveIndexible rawType;
				
				if(l instanceof Codes.ListLVal) {
					indexOperand = ((Codes.ListLVal)l).indexOperand;
					rawType = ((Codes.ListLVal)l).rawType();
				} else {
					indexOperand = ((Codes.StringLVal)l).indexOperand;
					rawType = ((Codes.StringLVal)l).rawType();
				}
				String lab1 = CodeUtils.freshLabel();
				String lab2 = CodeUtils.freshLabel();
				blk.add(Codes.Assert(lab2));
				blk.add(Codes.Const(freeSlot + 1, Constant.V_INTEGER(BigInteger.ZERO)),
						attributes(elem));
				blk.add(Codes.If(Type.T_INT, indexOperand, freeSlot + 1,
						Codes.Comparator.GTEQ, lab1), attributes(elem));
				blk.add(Codes.Fail("index out of bounds (negative)"),
						attributes(elem));
				blk.add(Codes.Label(lab1), attributes(elem));
				blk.add(Codes.LengthOf(rawType, freeSlot + 1, freeSlot),
						attributes(elem));
				blk.add(Codes.If(Type.T_INT, indexOperand, freeSlot + 1,
						Codes.Comparator.LT, lab2), attributes(elem));
				blk.add(Codes.Fail("index out of bounds (not less than length)"),
						attributes(elem));
				blk.add(Codes.Label(lab2), attributes(elem));				
				// Update
				blk.add(Codes.IndexOf(rawType, freeSlot, freeSlot,
						indexOperand));
			} else if (l instanceof Codes.MapLVal) {
				Codes.MapLVal rl = (Codes.MapLVal) l;
				blk.add(Codes.IndexOf(rl.rawType(), freeSlot, freeSlot,
						rl.keyOperand));
			} else if (l instanceof Codes.RecordLVal) {
				Codes.RecordLVal rl = (Codes.RecordLVal) l;
				blk.add(Codes.FieldLoad(rl.rawType(), freeSlot, freeSlot,
						rl.field));
			} else if (l instanceof Codes.ReferenceLVal) {
				Codes.ReferenceLVal rl = (Codes.ReferenceLVal) l;
				blk.add(Codes.Dereference(rl.rawType(), freeSlot, freeSlot));
			} else {
				internalFailure("Missing cases for Codes.Update",filename,elem);
			}
		}
		
		return blk;
	}

	/**
	 * For the case of a division operation, we need to check that the divisor
	 * is not zero.
	 * 
	 * @param code
	 * @param elem
	 * @return
	 */
	public Code.Block transform(Codes.BinaryOperator code, int freeSlot, SyntacticElement elem) {
		
		if(code.kind == Codes.BinaryOperatorKind.DIV) {
			Code.Block blk = new Code.Block(0);
			String lab1 = CodeUtils.freshLabel();
			blk.add(Codes.Assert(lab1));
			if (code.type() instanceof Type.Int) {
				blk.add(Codes.Const(freeSlot,
						Constant.V_INTEGER(BigInteger.ZERO)), attributes(elem));
			} else {
				blk.add(Codes.Const(freeSlot,
						Constant.V_DECIMAL(BigDecimal.ZERO)), attributes(elem));
			}
			blk.add(Codes.If(code.type(), code.operand(1), freeSlot,
					Codes.Comparator.NEQ, lab1), attributes(elem));
			blk.add(Codes.Fail("division by zero"), attributes(elem));
			blk.add(Codes.Label(lab1), attributes(elem));
			return blk;
		} 
		
		// not a division bytecode, so ignore
		return null;					
	}
	
	protected Code.Block findPrecondition(NameID name, Type.FunctionOrMethod fun,
			SyntacticElement elem) throws Exception {		
		Path.Entry<WyilFile> e = builder.project().get(name.module(),WyilFile.ContentType);
		if(e == null) {
			syntaxError(
					errorMessage(ErrorMessages.RESOLUTION_ERROR, name.module()
							.toString()), filename, elem);
		}
		WyilFile m = e.read();
		WyilFile.FunctionOrMethodDeclaration method = m.functionOrMethod(name.name(),fun);
	
		for(WyilFile.Case c : method.cases()) {
			// FIXME: this is a hack for now, since method cases don't do
			// anything.
			if(c.precondition() != null) {
				return c.precondition();
			} 
		}
		return null;
	}
	
	private java.util.List<Attribute> attributes(SyntacticElement elem) {
		return elem.attributes();
	}
	
	// ===================================================================
	// Import Methods
	// ===================================================================

	/**
	 * <p>
	 * Import an external block into an existing block, using a given
	 * <i>binding</i>. The binding indicates how the input variables for the
	 * external block should be mapped into the variables of this block. There
	 * are two considerations when importing one block into another:
	 * </p>
	 * 
	 * <ul>
	 * <li>Firstly, we cannot assume identical slot allocations. For example,
	 * the block representing a constraint on some type might have a single
	 * input mapped to slot zero, and a temporary mapped to slot one. When this
	 * block is imported into the pre-condition of some function, a collision
	 * would occur if e.g. that function has multiple parameters. This is
	 * because the second parameter would be mapped to the same register as the
	 * temporary in the constraint. We have to <i>shift</i> the slot number of
	 * that temporary variable up in order to avoid this collision.</li>
	 * <li>
	 * Secondly, we cannot all labels are distinct across both blocks. In
	 * otherwise, both blocks may contain two identical labels. In such case, we
	 * need to relabel one of the blocks in order to avoid this collision.</li>
	 * </ul>
	 * 
	 * <p>
	 * Every input variable in the block must be bound to something in the
	 * binding. Otherwise, an IllegalArgumentException is raised. In the case of
	 * an input bound to a slot >= numSlots(), then the number of slots is
	 * increased automatically.
	 * </p>
	 * <p>
	 * <b>NOTE:</b> temporary variables used in the external block will be
	 * mapped automatically to unused slots in this environment to prevent
	 * collisions. Therefore, temporary variables should not be specified in the
	 * binding.
	 * </p>
	 */
	public void importExternalAssert(Code.Block block, Code.Block external,
			Map<Integer, Integer> binding) {
		int freeSlot = block.numSlots();

		// First, sanity check that all input variables are bound
		HashMap<Integer, Integer> nbinding = new HashMap<Integer, Integer>();
		for (int i = 0; i != external.numInputs(); ++i) {
			Integer target = binding.get(i);
			if (target == null) {
				throw new IllegalArgumentException("Input not mapped by input");
			}
			nbinding.put(i, target);
			freeSlot = Math.max(target + 1, freeSlot);
		}

		// Second, determine binding for temporary variables
		for (int i = external.numInputs(); i != external.numSlots(); ++i) {
			nbinding.put(i, i + freeSlot);
		}

		// Third, determine relabelling
		HashMap<String, String> labels = new HashMap<String, String>();

		for (Entry s : external) {
			if (s.code instanceof Codes.Label) {
				Codes.Label l = (Codes.Label) s.code;
				labels.put(l.label, CodeUtils.freshLabel());
			}
		}

		// Next, create the assertion block
		String endLabel = CodeUtils.freshLabel();
		block.add(Codes.Assert(endLabel));
		
		// Finally, apply the binding and relabel any labels as well.
		for (Entry s : external) {
			Code ncode = s.code.remap(nbinding).relabel(labels);
			block.add(ncode, s.attributes());
		}
		
		block.add(Codes.Label(endLabel));
	}
	
	/**
	 * This method updates the source attributes for all statements in a block.
	 * This is typically done in conjunction with a substitution, when we're
	 * inlining constraints from e.g. pre- and post-conditions.
	 * 
	 * @param block
	 * @param nsrc
	 * @return
	 */
	public static Code.Block resource(Code.Block block, Attribute.Source nsrc) {
		if (block == null) {
			return null;
		}
		Code.Block nblock = new Code.Block(block.numInputs());
		for (Entry e : block) {
			nblock.add(e.code, nsrc);
		}
		return nblock;
	}
}
