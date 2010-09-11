// This file is part of the Wyjvm bytecode manipulation library.
//
// Wyjvm is free software; you can redistribute it and/or modify 
// it under the terms of the GNU General Public License as published 
// by the Free Software Foundation; either version 3 of the License, 
// or (at your option) any later version.
//
// Wyjvm is distributed in the hope that it will be useful, but 
// WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See 
// the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with Wyjvm. If not, see <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

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
	}
}
