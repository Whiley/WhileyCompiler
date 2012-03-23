// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// * Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
// * Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
// * Neither the name of the <organization> nor the
// names of its contributors may be used to endorse or promote products
// derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND
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

import static wyjvm.lang.JvmTypes.JAVA_LANG_OBJECT;
import static wyjvm.lang.JvmTypes.JAVA_LANG_THROWABLE;
import static wyjvm.lang.JvmTypes.T_BOOL;
import static wyjvm.lang.JvmTypes.T_INT;
import static wyjvm.lang.JvmTypes.T_VOID;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import wyil.util.Pair;
import wyjvm.attributes.Code;
import wyjvm.lang.Bytecode;
import wyjvm.lang.Bytecode.CheckCast;
import wyjvm.lang.Bytecode.Dup;
import wyjvm.lang.Bytecode.If;
import wyjvm.lang.Bytecode.Invoke;
import wyjvm.lang.Bytecode.Label;
import wyjvm.lang.Bytecode.Load;
import wyjvm.lang.Bytecode.LoadConst;
import wyjvm.lang.Bytecode.Return;
import wyjvm.lang.Bytecode.Store;
import wyjvm.lang.Bytecode.Swap;
import wyjvm.lang.Bytecode.Switch;
import wyjvm.lang.Bytecode.Throw;
import wyjvm.lang.BytecodeAttribute;
import wyjvm.lang.ClassFile;
import wyjvm.lang.ClassFile.Method;
import wyjvm.lang.JvmType;
import wyjvm.lang.JvmType.Bool;
import wyjvm.lang.JvmType.Char;
import wyjvm.lang.JvmType.Clazz;
import wyjvm.lang.JvmType.Function;
import wyjvm.lang.JvmType.Int;
import wyjvm.lang.JvmType.Reference;
import wyjvm.util.dfa.StackAnalysis;
import wyjvm.util.dfa.VariableAnalysis;

/**
 * Bytecode rewriter that adds yield and resumption points on actor
 * continuations.
 * 
 * @author Timothy Jones
 */
public class Continuations {
	
	private static final Clazz STRAND = new Clazz("wyjc.runtime.concurrency",
	    "Strand"), MESSAGER = new Clazz("wyjc.runtime.concurrency", "Messager"),
	    YIELDER = new Clazz("wyjc.runtime.concurrency", "Yielder"),
	    FUTURE = new Clazz("wyjc.runtime.concurrency", "Messager$MessageFuture");
	
	public void apply(ClassFile classfile) {
		for (Method method : classfile.methods()) {
			if (!method.name().equals("main")) {
				apply(method);
			}
		}
	}
	
	public void apply(Method method) {
		for (BytecodeAttribute attribute : method.attributes()) {
			if (attribute instanceof Code) {
				apply(method, (Code) attribute);
			}
		}
	}
	
	public void apply(Method method, Code code) {
		List<Bytecode> bytecodes = code.bytecodes();
		
		int location = 0;
		
		VariableAnalysis variableAnalysis = new VariableAnalysis(method);
		StackAnalysis stackAnalysis = new StackAnalysis(method);
		
		for (int i = 0; i < bytecodes.size(); ++i) {
			Bytecode bytecode = bytecodes.get(i);
			
			if (bytecode instanceof Invoke) {
				Invoke invoke = (Invoke) bytecode;
				String name = invoke.name;
				
				if (invoke.owner.equals(MESSAGER) && name.equals("sendSync")) {
					// Synchronous message send.
					
					i = addStrand(bytecodes, i);
					bytecodes.add(++i, new Invoke(YIELDER, "shouldYield", new Function(
					    T_BOOL), Bytecode.VIRTUAL));
					bytecodes.add(++i, new If(If.EQ, "skip" + location));
					
					Map<Integer, JvmType> types = variableAnalysis.typesAt(i + 1);
					Stack<JvmType> stack = stackAnalysis.typesAt(i + 1);
					
					i =
					    addResume(bytecodes,
					        addYield(method, bytecodes, i, location, types, stack),
					        location, types, stack);
					
					bytecodes.add(++i, new Label("skip" + location));
					
					if (name.startsWith("sendSync")) {
						String success = "success" + location;
						
						bytecodes.add(++i, new Dup(FUTURE));
						bytecodes.add(++i, new Invoke(FUTURE, "isFailed", new Function(
						    T_BOOL), Bytecode.VIRTUAL));
						bytecodes.add(++i, new If(If.EQ, success));
						
						// If the method sends any synchronous messages, then it will check
						// for failure and respond by not jumping to the success label.
						bytecodes.add(++i, new Invoke(FUTURE, "getCause", new Function(
						    JAVA_LANG_THROWABLE), Bytecode.VIRTUAL));
						bytecodes.add(++i, new Throw());
						bytecodes.add(++i, new Label(success));
					}
					
					location += 1;
					// } else {
					// List<JvmType> pTypes = invoke.type.parameterTypes();
					// if (pTypes.size() > 0 && pTypes.get(0).equals(ACTOR)) {
					// // Internal method call.
					//
					// Map<Integer, JvmType> types = variableAnalysis.typesAt(i);
					// Stack<JvmType> stack = stackAnalysis.typesAt(i);
					//
					// pTypes = invoke.type.parameterTypes();
					// int size = pTypes.size();
					//
					// // Remove the values that invoking the method will remove for us.
					// for (int j = 0; j < size; ++j) {
					// stack.pop();
					// }
					//
					// // If the method isn't resuming, it needs to skip over the resume.
					// bytecodes.add(i++, new Goto("invoke" + location));
					//
					// i = addResume(bytecodes, i - 1, location, types, stack) + 1;
					//
					// // The first argument of any internal method is the actor.
					// bytecodes.add(i++, new Load(0, ACTOR));
					//
					// // Load in null values. The unyielding will put the real values
					// // in.
					// for (int j = 1; j < size; ++j) {
					// bytecodes.add(i++, addNullValue(pTypes.get(j)));
					// }
					//
					// bytecodes.add(i++, new Label("invoke" + location));
					//
					// // Now the method has been invoked, this method needs to check if
					// // it caused the actor to yield.
					// bytecodes.add(++i, new Load(0, ACTOR));
					// bytecodes.add(++i, new Invoke(YIELDER, "isYielded", new Function(
					// T_BOOL), Bytecode.VIRTUAL));
					// bytecodes.add(++i, new If(If.EQ, "skip" + location));
					//
					// i = addYield(method, bytecodes, i, location, types, stack);
					//
					// bytecodes.add(++i, new Label("skip" + location));
					//
					// location += 1;
					// }
				}
			}
		}
		
		// If the method may resume at some point, then the start needs to be
		// updated in order to cause the next invocation to jump to the right
		// point in the code.
		if (location > 0) {
			int i = -1;
			
			i = addStrand(bytecodes, i);
			bytecodes.add(++i, new Invoke(YIELDER, "getCurrentStateLocation",
			    new Function(T_INT), Bytecode.VIRTUAL));
			
			List<Pair<Integer, String>> cases =
			    new ArrayList<Pair<Integer, String>>(location);
			for (int j = 0; j < location; ++j) {
				cases.add(new Pair<Integer, String>(j, "resume" + j));
			}
			
			bytecodes.add(++i, new Switch("begin", cases));
			bytecodes.add(++i, new Label("begin"));
		}
	}
	
	private int addStrand(List<Bytecode> bytecodes, int i) {
		// Ugh. Until we can tell whether a Java method operates on a Whiley actor,
		// this is the only way to retrieve a strand for any method.
		
		bytecodes.add(++i, new Bytecode.Invoke(STRAND, "getCurrentStrand",
		    new Function(STRAND), Bytecode.STATIC));
		
		return i;
	}
	
	private int addYield(Method method, List<Bytecode> bytecodes, int i,
	    int location, Map<Integer, JvmType> types, Stack<JvmType> stack) {
		i = addStrand(bytecodes, i);
		
		bytecodes.add(++i, new LoadConst(location));
		bytecodes.add(++i, new Invoke(YIELDER, "yield",
		    new Function(T_VOID, T_INT), Bytecode.VIRTUAL));
		
		for (int var : types.keySet()) {
			JvmType type = types.get(var);
			i = addStrand(bytecodes, i);
			bytecodes.add(++i, new LoadConst(var));
			bytecodes.add(++i, new Load(var, type));
			
			if (type instanceof Reference) {
				type = JAVA_LANG_OBJECT;
			}
			
			bytecodes.add(++i, new Invoke(YIELDER, "set", new Function(T_VOID, T_INT,
			    type), Bytecode.VIRTUAL));
		}
		
		for (int j = stack.size() - 1; j >= 0; --j) {
			JvmType type = stack.get(j);
			i = addStrand(bytecodes, i);
			bytecodes.add(++i, new Swap());
			
			if (type instanceof Reference) {
				type = JAVA_LANG_OBJECT;
			}
			
			bytecodes.add(++i, new Invoke(YIELDER, "push",
			    new Function(T_VOID, type), Bytecode.VIRTUAL));
		}
		
		JvmType returnType = method.type().returnType();
		if (returnType.equals(T_VOID)) {
			bytecodes.add(++i, new Return(null));
		} else {
			bytecodes.add(++i, addNullValue(returnType));
			bytecodes.add(++i, new Return(returnType));
		}
		
		return i;
	}
	
	private int addResume(List<Bytecode> bytecodes, int i, int location,
	    Map<Integer, JvmType> types, Stack<JvmType> stack) {
		bytecodes.add(++i, new Label("resume" + location));
		
		for (JvmType type : stack) {
			JvmType methodType = type;
			i = addStrand(bytecodes, i);
			
			String name;
			if (type instanceof Reference) {
				name = "popObject";
				methodType = JAVA_LANG_OBJECT;
			} else {
				// This is a bit of a hack. Method names in Yielder MUST match the
				// class names in JvmType.
				name = "pop" + type.getClass().getSimpleName();
			}
			
			bytecodes.add(++i, new Invoke(YIELDER, name, new Function(methodType),
			    Bytecode.VIRTUAL));
			if (type instanceof Reference) {
				bytecodes.add(++i, new CheckCast(type));
			}
		}
		
		for (int var : types.keySet()) {
			JvmType type = types.get(var), methodType = type;
			i = addStrand(bytecodes, i);
			bytecodes.add(++i, new LoadConst(var));
			
			String name;
			if (type instanceof Reference) {
				name = "getObject";
				methodType = JAVA_LANG_OBJECT;
			} else {
				// This is a bit of a hack. Method names in Yielder MUST match the
				// class names in JvmType.
				name = "get" + type.getClass().getSimpleName();
			}
			
			bytecodes.add(++i, new Invoke(YIELDER, name, new Function(methodType,
			    T_INT), Bytecode.VIRTUAL));
			if (type instanceof Reference) {
				bytecodes.add(++i, new CheckCast(type));
			}
			bytecodes.add(++i, new Store(var, type));
		}
		
		i = addStrand(bytecodes, i);
		bytecodes.add(++i, new Invoke(YIELDER, "unyield", new Function(T_VOID),
		    Bytecode.VIRTUAL));
		
		return i;
	}
	
	private Bytecode addNullValue(JvmType type) {
		Object value;
		
		if (type instanceof Reference) {
			value = null;
		} else if (type instanceof Bool) {
			value = false;
		} else if (type instanceof Char) {
			value = '\u0000';
		} else if (type instanceof JvmType.Double) {
			value = (Double) 0.0;
		} else if (type instanceof Int) {
			value = (Integer) 0;
		} else if (type instanceof JvmType.Float) {
			value = (Float) 0f;
		} else if (type instanceof JvmType.Long) {
			value = (Long) 0l;
		} else {
			throw new UnsupportedOperationException(
			    "Non-reference types not yet supported.");
		}
		
		return new LoadConst(value);
	}
	
}
