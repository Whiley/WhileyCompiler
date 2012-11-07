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

package wyil.checks;

import java.util.*;

import wybs.lang.Builder;
import wybs.lang.Path;
import wybs.lang.SyntacticElement;
import wybs.lang.SyntaxError;
import wyil.lang.*;
import wyil.transforms.RuntimeAssertions;
import wyil.util.ErrorMessages;
import wyone.util.BigRational;
import wyil.util.Pair;
import static wybs.lang.SyntaxError.*;
import static wyil.util.ErrorMessages.errorMessage;
import wyil.Transform;
import wyone.core.Automaton;
import wyone.io.PrettyAutomataWriter;
import wyil.util.ConstraintSolver;
import static wyil.util.ConstraintSolver.*;

/**
 * Responsible for compile-time checking of constraints. This involves
 * converting WYIL into the appropriate form for the automated theorem prover
 * (wyone).  
 * 
 * @author David J. Pearce
 * 
 */
public class VerificationCheck implements Transform {	
	
	/**
	 * Determines whether verification is enabled or not.
	 */
	private boolean enabled = getEnable();
	
	/**
	 * Enables debugging information to be printed.
	 */
	private boolean debug = getDebug();
		
	private final Builder builder;
		
	private String filename;
	
	public VerificationCheck(Builder builder) {
		this.builder = builder;
	}
	
	public static String describeEnable() {
		return "Enable/disable compile-time verification";
	}
	
	public static boolean getEnable() {
		return false; // default value
	}
	
	public void setEnable(boolean flag) {
		this.enabled = flag;
	}
	
	public static String describeDebug() {
		return "Enable/disable debugging information";
	}
	
	public static boolean getDebug() {
		return false; // default value
	}
	
	public void setDebug(boolean flag) {
		this.debug = flag;
	}
	
	public static String describeMaxSteps() {
		return "Set maximum number of steps constraint solver can apply for a given assertion";
	}
	
	public static long getMaxSteps() {
		return ConstraintSolver.MAX_STEPS;
	}
	
	public void setMaxSteps(long steps) {		
		ConstraintSolver.MAX_STEPS = steps;
	}
	
	public void setMaxSteps(int steps) {		
		ConstraintSolver.MAX_STEPS = steps;
	}
	
	public void apply(WyilFile module) {
		if(enabled) {
			this.filename = module.filename();
			for(WyilFile.TypeDeclaration type : module.types()) {
				transform(type);
			}		
			for(WyilFile.MethodDeclaration method : module.methods()) {
				transform(method);
			}		
		}
	}
	
	protected void transform(WyilFile.TypeDeclaration def) {
		
	}
	
	protected void transform(WyilFile.MethodDeclaration method) {		
		for(WyilFile.Case c : method.cases()) {
			transform(c,method);
		}
	}
	
	protected void transform(WyilFile.Case methodCase,
			WyilFile.MethodDeclaration method) {
		if (!RuntimeAssertions.getEnable()) {
			// inline constraints if they have not already been done.
			RuntimeAssertions rac = new RuntimeAssertions(builder, filename);
			methodCase = rac.transform(methodCase, method);
		}
		
		// add type information available from parameters
		if(debug) {
			System.err.println("============================================");
			Type.FunctionOrMethod fmt = method.type();
			String paramString = fmt.params().toString();
			paramString = paramString.substring(1,paramString.length()-1);
			if(method.type() instanceof Type.Function) {
				System.err.println("FUNCTION: " + fmt.ret() + " " + method.name() + "(" + paramString + ")");
			} else {
				System.err.println("METHOD: " + fmt.ret() + " " + method.name() + "(" + paramString + ")");
			}
		}
		
		Type.FunctionOrMethod fmm = method.type();
		int paramStart = 0;
		for(int i=paramStart;i!=fmm.params().size();++i) {
			Type paramType = fmm.params().get(i);
			
			// FIXME: add type information
			
//			WVariable pv = new WVariable(i + "$" + 0);
//			constraint = WFormulas.and(branch.automaton(),
//					WTypes.subtypeOf(pv, convert(paramType)));
		}
		
		Block body = methodCase.body();
		Block precondition = methodCase.precondition();				
		
		VerificationBranch master = new VerificationBranch("", new Automaton(
				ConstraintSolver.SCHEMA), body);
		
		if (precondition != null) {
			VerificationBranch precond = new VerificationBranch("",
					master.automaton(), precondition);
			int constraint = precond.transform(new VerificationTransformer(
					builder, methodCase, filename, true, debug));
			master.assume(constraint);
			// the following is necessary to avoid clashes between temporary
			// variables used in the precondition and temporary variables used
			// here.
			master.invalidate(body.numInputs(),
					Math.min(body.numSlots(), precondition.numSlots()));
		}
		
		master.transform(new VerificationTransformer(builder, methodCase,
				filename, false, debug));
	}
}
