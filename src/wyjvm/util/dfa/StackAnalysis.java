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

import static wyjvm.lang.JvmTypes.JAVA_LANG_OBJECT;
import static wyjvm.lang.JvmTypes.JAVA_LANG_STRING;
import static wyjvm.lang.JvmTypes.T_BOOL;
import static wyjvm.lang.JvmTypes.T_BYTE;
import static wyjvm.lang.JvmTypes.T_CHAR;
import static wyjvm.lang.JvmTypes.T_DOUBLE;
import static wyjvm.lang.JvmTypes.T_FLOAT;
import static wyjvm.lang.JvmTypes.T_INT;
import static wyjvm.lang.JvmTypes.T_LONG;
import static wyjvm.lang.JvmTypes.T_SHORT;

import java.util.Stack;

import wyjvm.lang.Bytecode;
import wyjvm.lang.Bytecode.ArrayLength;
import wyjvm.lang.Bytecode.ArrayLoad;
import wyjvm.lang.Bytecode.ArrayStore;
import wyjvm.lang.Bytecode.BinOp;
import wyjvm.lang.Bytecode.CheckCast;
import wyjvm.lang.Bytecode.Cmp;
import wyjvm.lang.Bytecode.Conversion;
import wyjvm.lang.Bytecode.Dup;
import wyjvm.lang.Bytecode.DupX1;
import wyjvm.lang.Bytecode.DupX2;
import wyjvm.lang.Bytecode.GetField;
import wyjvm.lang.Bytecode.If;
import wyjvm.lang.Bytecode.IfCmp;
import wyjvm.lang.Bytecode.InstanceOf;
import wyjvm.lang.Bytecode.Invoke;
import wyjvm.lang.Bytecode.Load;
import wyjvm.lang.Bytecode.LoadConst;
import wyjvm.lang.Bytecode.MonitorEnter;
import wyjvm.lang.Bytecode.MonitorExit;
import wyjvm.lang.Bytecode.New;
import wyjvm.lang.Bytecode.Pop;
import wyjvm.lang.Bytecode.PutField;
import wyjvm.lang.Bytecode.Store;
import wyjvm.lang.Bytecode.Swap;
import wyjvm.lang.Bytecode.Switch;
import wyjvm.lang.ClassFile.Method;
import wyjvm.lang.JvmType;
import wyjvm.lang.JvmType.Array;
import wyjvm.lang.JvmType.Void;

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
	protected TypeInformation respondTo(TypeInformation types, Bytecode code) {
		if (code instanceof ArrayLoad) {
			return newTypes(types, 2, ((ArrayLoad) code).type);
		} else if (code instanceof ArrayLength) {
			return newTypes(types, 1, T_INT);
		} else if (code instanceof ArrayStore) {
			return newTypes(types, 3);
		} else if (code instanceof BinOp) {
			return newTypes(types, 1);
		} else if (code instanceof CheckCast) {
			return newTypes(types, 1, ((CheckCast) code).type);
		} else if (code instanceof Conversion) {
			return newTypes(types, 1, ((Conversion) code).to);
		} else if (code instanceof Cmp) {
			return newTypes(types, 2, T_BOOL);
		} else if (code instanceof Dup) {
			return newTypes(types, 0, types.getTypeInformation().peek());
		} else if (code instanceof DupX1 || code instanceof DupX2) {
			int by = code instanceof DupX1 ? 2 : 3;
			Stack<JvmType> newStack = copy(types);
			newStack.add(newStack.size() - by, newStack.peek());
			return new StackTypes(newStack, types.isComplete());
		} else if (code instanceof GetField) {
			return newTypes(types, ((GetField) code).mode == Bytecode.STATIC ? 0 : 1,
					((GetField) code).type);
		} else if (code instanceof If) {
			return newTypes(types, 1);
		} else if (code instanceof IfCmp) {
			return newTypes(types, 2);
		} else if (code instanceof InstanceOf) {
			return newTypes(types, 1, T_BOOL);
		} else if (code instanceof Invoke) {
			Invoke invoke = (Invoke) code;
			JvmType returnType = invoke.type.returnType();
			try {
				boolean isStatic = invoke.mode == Bytecode.STATIC;
				return newTypes(types, invoke.type.parameterTypes().size()
						+ (isStatic ? 0 : 1),
						returnType instanceof Void ? null : returnType);
			} catch (RuntimeException rex) {
				// System.out.println(invoke);
				throw rex;
			}
		} else if (code instanceof Load) {
			return newTypes(types, 0, ((Load) code).type);
		} else if (code instanceof LoadConst) {
			Object constant = ((LoadConst) code).constant;
			JvmType type;
			if (constant instanceof Boolean) {
				type = T_BOOL;
			} else if (constant instanceof Character) {
				type = T_CHAR;
			} else if (constant instanceof Byte) {
				type = T_BYTE;
			} else if (constant instanceof Short) {
				type = T_SHORT;
			} else if (constant instanceof Long) {
				type = T_LONG;
			} else if (constant instanceof Double) {
				type = T_DOUBLE;
			} else if (constant instanceof Integer) {
				type = T_INT;
			} else if (constant instanceof Float) {
				type = T_FLOAT;
			} else if (constant instanceof String) {
				type = JAVA_LANG_STRING;
			} else if (constant == null) {
				type = JAVA_LANG_OBJECT;
			} else {
				throw new UnsupportedOperationException("Unknown constant type.");
			}

			return newTypes(types, 0, type);
		} else if (code instanceof MonitorEnter || code instanceof MonitorExit) {
			return newTypes(types, 1);
		} else if (code instanceof New) {
			New n = (New) code;
			if (n.type instanceof Array) {
				return newTypes(types, n.dims > 1 ? n.dims : 1, n.type);
			} else {
				return newTypes(types, 0, n.type);
			}
		} else if (code instanceof Pop) {
			return newTypes(types, 1);
		} else if (code instanceof PutField) {
			return newTypes(types, 2);
		} else if (code instanceof Store) {
			return newTypes(types, 1);
		} else if (code instanceof Swap) {
			Stack<JvmType> stack = copy(types);
			stack.add(stack.size() - 2, stack.pop());
			return new StackTypes(stack, types.isComplete());
		} else if (code instanceof Switch) {
			return newTypes(types, 1);
		}

		return types;
	}

	private StackTypes newTypes(TypeInformation types, int popCount) {
		return newTypes(types, popCount, null);
	}

	private StackTypes newTypes(TypeInformation types, int popCount,
			JvmType newType) {
		Stack<JvmType> newStack = copy(types);

		for (int i = 0; i < popCount; ++i) {
			newStack.pop();
		}

		if (newType != null) {
			newStack.push(newType);
		}

		return new StackTypes(newStack, types.isComplete());
	}

	private Stack<JvmType> copy(TypeInformation types) {
		Stack<JvmType> copy = new Stack<JvmType>();
		copy.addAll(types.getTypeInformation());
		return copy;
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
