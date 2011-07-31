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
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
// THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyjvm.util.dfa;

import java.util.Stack;

import wyjvm.lang.Bytecode;
import wyjvm.lang.Bytecode.Load;
import wyjvm.lang.Bytecode.LoadConst;
import wyjvm.lang.Bytecode.Pop;
import wyjvm.lang.Bytecode.Swap;
import wyjvm.lang.ClassFile.Method;
import wyjvm.lang.JvmType;

public class StackAnalysis extends TypeFlowAnalysis<Stack<JvmType>> {

	public StackAnalysis(Method method) {
		super(method);
	}

	@Override
	public TypeInformation initTypes() {
		return new StackTypes(new Stack<JvmType>(), true);
	}

	@Override
	public TypeInformation emptyTypes() {
		return new StackTypes(new Stack<JvmType>(), false);
	}

	@Override
	public TypeInformation respondTo(TypeInformation types, Bytecode code) {
		// TODO Respond to the rest of the bytecodes.
		
		if (code instanceof Load) {
			return addType(types, ((Load) code).type);
		} else if (code instanceof LoadConst) {
			// TODO Work out how to get type information out of LoadConst.
		} else if (code instanceof Pop) {
			Stack<JvmType> newTypes = copy(types.getTypeInformation());
			newTypes.pop();
			return new StackTypes(newTypes, types.isComplete());
		} else if (code instanceof Swap) {
			Stack<JvmType> newTypes = copy(types.getTypeInformation());
			JvmType top = newTypes.pop();
			newTypes.add(newTypes.size() - 2, top);
			return new StackTypes(newTypes, types.isComplete());
		}

		return types;
	}
	
	private Stack<JvmType> copy(Stack<JvmType> stack) {
		Stack<JvmType> newStack = new Stack<JvmType>();
		newStack.addAll(stack);
		return newStack;
	}

	private TypeInformation addType(TypeInformation types, JvmType type) {
		Stack<JvmType> newTypes = copy(types.getTypeInformation());
		newTypes.push(type);
		return new StackTypes(newTypes, types.isComplete());
	}

	private class StackTypes extends TypeInformation {

		public StackTypes(Stack<JvmType> types, boolean complete) {
			super(types, complete);
		}

		public StackTypes combineWith(TypeInformation types) {
			if (isComplete() || equals(types)) {
				return this;
			}

			if (types.isComplete()) {
				if (types instanceof StackTypes) {
					return (StackTypes) types;
				}

				return new StackTypes(types.getTypeInformation(), types.isComplete());
			}

			throw new IllegalStateException(
			    "Two different incomplete stacks cannot be combined.");
		}

	}

}
