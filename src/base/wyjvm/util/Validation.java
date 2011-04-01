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

package wyjvm.util;

import java.util.*;
import wyjvm.lang.*;
import wyjvm.attributes.*;

/**
 * <p>
 * The purpose of validation is to check that a given class conforms to the JVM
 * spec. There are numerous checks that need to be made, which break into
 * several categories:
 * </p>
 * <h2>Class-Level Checks</h2>
 * <ul>
 * <li>That no class contains two methods with identical types.</li>
 * <li>That every referenced constant-pool item exists, and is of the correc
 * type.</li>
 * </ul>
 * <h2>Method-Level Checks</h2>
 * <ul>
 * <li>That no method contains two identical labels.</li>
 * <li>That the stack height is always consistent</li>
 * <li>That type modifiers on bytecodes are correct</li>
 * </ul>
 * 
 * @author djp
 * 
 */
public class Validation {
	public void apply(ClassFile cf) {		
		checkNoIdenticalMethod(cf);
		for(ClassFile.Method m : cf.methods()) {
			checkMethod(m,cf);
		}		
	}
	
	public void checkNoIdenticalMethod(ClassFile cf) {
		HashMap<String,HashSet<JvmType>> map = new HashMap<String,HashSet<JvmType>>();
		for(ClassFile.Method m : cf.methods()) {
			HashSet<JvmType> types = map.get(m.name());
			if(types == null) {
				types = new HashSet<JvmType>();
				map.put(m.name(), types);
			} else if(types.contains(m.type())) {
				// error
				throw new IllegalArgumentException("Duplicate method: "
						+ m.name() + ", " + m.type());
			}
			types.add(m.type());
		}		
	}
	
	public void checkMethod(ClassFile.Method method, ClassFile parent) {
		Code code = method.attribute(Code.class);
		if(code != null) {
			checkCode(code,method,parent);
		}
	}
	
	public void checkCode(Code code, ClassFile.Method method, ClassFile parent) {
		checkLabels(code,method,parent);
	}
	
	public void checkLabels(Code code, ClassFile.Method method, ClassFile parent) {
		HashSet<String> labels = new HashSet<String>();
		
		// First, check no two identical labels
		for(Bytecode b : code.bytecodes()) {
			if(b instanceof Bytecode.Label) {
				Bytecode.Label lab = (Bytecode.Label) b;
				if(labels.contains(lab.name)) {
					// need better error reporting system.
					throw new IllegalArgumentException("Duplicate label \""
							+ lab.name + "\" in method " + method.name() + ", "
							+ method.type());
				}
				labels.add(lab.name);
			}
		}
		
		// Second, check every branch target exists
		for(Bytecode b : code.bytecodes()) {
			if(b instanceof Bytecode.Branch) {
				Bytecode.Branch br = (Bytecode.Branch) b;
				if(!labels.contains(br.label)){
					throw new IllegalArgumentException("Unknown branch target \""
							+ br.label + "\" in method " + method.name() + ", "
							+ method.type());
				}
			} else if(b instanceof Bytecode.Switch) {
				Bytecode.Switch sw = (Bytecode.Switch) b;
				if(!labels.contains(sw.defaultLabel)){
					throw new IllegalArgumentException("Unknown branch target \""
							+ sw.defaultLabel + "\" in method " + method.name() + ", "
							+ method.type());
				}
				for(wyil.util.Pair<Integer,String> c : sw.cases) {
					if(!labels.contains(c.second())){
						throw new IllegalArgumentException("Unknown branch target \""
								+ c.second() + "\" in method " + method.name() + ", "
								+ method.type());
					}
				}
			}
		}
	}
}
