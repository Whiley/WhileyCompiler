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

package wyil.checks;

import java.util.*;

import wybs.lang.Builder;
import wycc.lang.Transform;
import wycc.util.Pair;
import wyfs.lang.Path;
import wyil.util.*;
import wyil.util.dfa.*;
import wyil.attributes.SourceLocation;
import wyil.lang.*;
import static wycc.lang.SyntaxError.*;
import static wyil.lang.CodeBlock.*;
import static wyil.util.ErrorMessages.*;

/**
 * <p>
 * The purpose of this class is to check that all variables are defined before
 * being used. For example:
 * </p>
 *
 * <pre>
 * function f() => int:
 * 	int z
 * 	return z + 1
 * </pre>
 *
 * <p>
 * In the above example, variable z is used in the return statement before it
 * has been defined any value. This is considered a syntax error in whiley.
 * </p>
 * @author David J. Pearce
 *
 */
public class DefiniteAssignmentCheck extends
		ForwardFlowAnalysis<HashSet<Integer>> implements Transform<WyilFile> {

	/**
	 * Determines whether constant propagation is enabled or not.
	 */
	private boolean enabled = getEnable();
	
	public DefiniteAssignmentCheck(Builder builder) {

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
	public void apply(WyilFile module) {
		if(enabled) {
			super.apply(module);
		}
	}
	
	@Override
	public HashSet<Integer> initialStore() {
		HashSet<Integer> defined = new HashSet<Integer>();
		int diff = 0;

		for(int i=0;i!=method.type().params().size();++i) {
			defined.add(i+diff);
		}
		
		return defined;
	}

	@Override
	public HashSet<Integer> propagate(CodeBlock.Index index, Code code,
			HashSet<Integer> in) {
		checkUses(index, code, in);

		int[] defs = defs(code);
		
		if (defs.length >= 0) {
			for(int def : defs) {
				in = new HashSet<Integer>(in);
				in.add(def);
			}
		}

		return in;
	}

	@Override
	public Pair<HashSet<Integer>, HashSet<Integer>> propagate(CodeBlock.Index index,
			Codes.If igoto, HashSet<Integer> in) {

		if (!in.contains(igoto.leftOperand) || !in.contains(igoto.rightOperand)) {
			syntaxError(errorMessage(VARIABLE_POSSIBLY_UNITIALISED), filename,
					rootBlock.attribute(index,SourceLocation.class));
		}

		return new Pair(in, in);
	}

	@Override
	public Pair<HashSet<Integer>, HashSet<Integer>> propagate(CodeBlock.Index index,
			Codes.IfIs iftype, HashSet<Integer> in) {

		if (!in.contains(iftype.operand)) {
			syntaxError(errorMessage(VARIABLE_POSSIBLY_UNITIALISED), filename,
					rootBlock.attribute(index,SourceLocation.class));
		}

		return new Pair(in,in);
	}

	@Override
	public List<HashSet<Integer>> propagate(CodeBlock.Index index, Codes.Switch sw,
			HashSet<Integer> in) {

		if (!in.contains(sw.operand)) {
			syntaxError(errorMessage(VARIABLE_POSSIBLY_UNITIALISED), filename,
					rootBlock.attribute(index,SourceLocation.class));
		}

		ArrayList<HashSet<Integer>> stores = new ArrayList();
		for (int i = 0; i != sw.branches.size(); ++i) {
			stores.add(in);
		}
		return stores;
	}

	@Override
	public HashSet<Integer> propagate(CodeBlock.Index index, Codes.Loop loop,
			HashSet<Integer> in) {
		if (loop instanceof Codes.Quantify) {
			Codes.Quantify fall = (Codes.Quantify) loop;

			if (!in.contains(fall.startOperand) || !in.contains(fall.endOperand)) {
				syntaxError(errorMessage(VARIABLE_POSSIBLY_UNITIALISED),
						filename,
						rootBlock.attribute(index, SourceLocation.class));
			} 

			in = new HashSet<Integer>(in);
			in.add(fall.indexOperand);
		}

		CodeBlock blk = loop;
		HashSet<Integer> r = propagate(index, blk, in);
		return join(in, r);
	}

	protected HashSet<Integer> join(HashSet<Integer> s1, HashSet<Integer> s2) {
		HashSet<Integer> r = new HashSet<Integer>();
		// set intersection
		for (Integer s : s1) {
			if (s2.contains(s)) {
				r.add(s);
			}
		}
		return r;
	}

	public void checkUses(CodeBlock.Index index, Code code, HashSet<Integer> in) {
		if(code instanceof Code.AbstractUnaryOp) {
			Code.AbstractUnaryOp a = (Code.AbstractUnaryOp) code;
			if(a.operand == Codes.NULL_REG || in.contains(a.operand)) {
				return;
			}
		} else if(code instanceof Code.AbstractBinaryOp) {
			Code.AbstractBinaryOp a = (Code.AbstractBinaryOp) code;
			if (in.contains(a.leftOperand) && in.contains(a.rightOperand)) {
				return;
			}
		} else if(code instanceof Code.AbstractNaryAssignable) {
			Code.AbstractNaryAssignable a = (Code.AbstractNaryAssignable) code;
			for(int operand : a.operands()) {
				if(operand != Codes.NULL_REG && !in.contains(operand)) {
					syntaxError(errorMessage(VARIABLE_POSSIBLY_UNITIALISED),
	                        filename, rootBlock.attribute(index,SourceLocation.class));
				}
			}
			if(code instanceof Codes.Update && !in.contains(a.target())) {
				// In this case, we are assigning to an index or field.
				// Therefore, the target register must already be defined.
				syntaxError(errorMessage(VARIABLE_POSSIBLY_UNITIALISED),
                        filename, rootBlock.attribute(index,SourceLocation.class));
			}
			return;
		} else if(code instanceof Code.AbstractMultiNaryAssignable) {
			Code.AbstractMultiNaryAssignable a = (Code.AbstractMultiNaryAssignable) code;
			for(int operand : a.operands()) {
				if(operand != Codes.NULL_REG && !in.contains(operand)) {
					syntaxError(errorMessage(VARIABLE_POSSIBLY_UNITIALISED),
	                        filename, rootBlock.attribute(index,SourceLocation.class));
				}
			}			
			return;
		} else {
			// includes abstract-assignables and branching bytecodes
			return;
		}

		syntaxError(errorMessage(VARIABLE_POSSIBLY_UNITIALISED),
                filename, rootBlock.attribute(index,SourceLocation.class));
	}

	public int[] defs(Code code) {
		if (code instanceof Code.AbstractAssignable) {
			Code.AbstractAssignable aa = (Code.AbstractAssignable) code;
			return aa.targets();
		}
		return new int[0];
	}
}
