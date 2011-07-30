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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wyil.util.Pair;
import wyjvm.lang.Bytecode;
import wyjvm.lang.Bytecode.Branch;
import wyjvm.lang.Bytecode.Goto;
import wyjvm.lang.Bytecode.Label;
import wyjvm.lang.Bytecode.Return;
import wyjvm.lang.Bytecode.Store;
import wyjvm.lang.Bytecode.Switch;
import wyjvm.lang.Bytecode.Throw;
import wyjvm.lang.ClassFile.Method;
import wyjvm.lang.JvmType;

public class VariableAnalysis extends TypeFlowAnalysis {

	public VariableAnalysis(Method method) {
		super(method);
	}

	/**
	 * Analyses the bytecode of the given method in order to find the types of the
	 * local variables before the the code at the given position would run.
	 * 
	 * @param at The position in the code to obtain the variable types at.
	 * @return A map from variable number to type.
	 */
	public Map<Integer, JvmType> typesAt(int at) {
		// TODO Optimise this algorithm to ignore variables that won't be used
		// again.

		Map<String, VariableTypes> labelTypes = new HashMap<String, VariableTypes>();

		int size = codes.size();
		while (true) {
			Map<String, VariableTypes> comparison = new HashMap<String, VariableTypes>(
			    labelTypes);

			VariableTypes currentTypes = new VariableTypes(parameterTypes(), true);

			for (int i = 0; i < size; ++i) {
				Bytecode code = codes.get(i);

				if (i == at && currentTypes.isComplete()) {
					return currentTypes.getTypeInformation();
				}

				if (code instanceof Store) {
					Store store = (Store) code;
					// Generate new type information, and store it as the current one.
					currentTypes = currentTypes.newType(store.slot, store.type);
				} else if (code instanceof Label) {
					addLabelInformation(labelTypes, ((Label) code).name, currentTypes);
				} else if (code instanceof Branch) {
					addLabelInformation(labelTypes, ((Branch) code).label, currentTypes);
				} else if (code instanceof Switch) {
					Switch branch = (Switch) code;
					addLabelInformation(labelTypes, branch.defaultLabel, currentTypes);
					for (Pair<Integer, String> label : branch.cases) {
						addLabelInformation(labelTypes, label.second(), currentTypes);
					}
				}

				if ((code instanceof Goto || code instanceof Return || code instanceof Throw)
				    && i < size - 1) {
					if (!(codes.get(i + 1) instanceof Label)) {
						throw new IllegalStateException("Deadcode found.");
					}

					String labelName = ((Label) codes.get(i + 1)).name;

					if (labelTypes.containsKey(labelName)) {
						currentTypes = labelTypes.get(labelName);
					} else {
						// The current type information is now useless.
						// Note that this must be partial, because we really don't have any
						// information at all at this point.
						currentTypes = new VariableTypes(new HashMap<Integer, JvmType>(),
						    false);
					}
				}
			}

			// If there's no change after running through, there's no more information
			// to collect.
			if (comparison.equals(labelTypes)) {
				for (VariableTypes types : labelTypes.values()) {
					types.setComplete(true);
				}
			}
		}
	}

	private Map<Integer, JvmType> parameterTypes() {
		Map<Integer, JvmType> types = new HashMap<Integer, JvmType>();

		List<JvmType> parameterTypes = method.type().parameterTypes();
		for (int i = 0; i < parameterTypes.size(); ++i) {
			types.put(i, parameterTypes.get(i));
		}

		return types;
	}

	private void addLabelInformation(Map<String, VariableTypes> labelTypes,
	    String labelName, VariableTypes currentTypes) {
		if (labelTypes.containsKey(labelName)) {
			VariableTypes labelType = labelTypes.get(labelName);
			if (!labelType.isComplete()) {
				if (currentTypes.isComplete()) {
					// This information is better - replace the old information.
					labelTypes.put(labelName, currentTypes);
				} else {
					// Both the information is partial, so we need to combine it.
					labelTypes.put(labelName, labelType.combineWith(currentTypes));
				}
			}
		} else {
			// There's no existing information. Use the current one.
			labelTypes.put(labelName, currentTypes);
		}
	}

	private class VariableTypes extends TypeInformation<Map<Integer, JvmType>> {

		public VariableTypes(Map<Integer, JvmType> types, boolean complete) {
			super(types, complete);
		}

		public VariableTypes newType(int location, JvmType type) {
			Map<Integer, JvmType> types = getTypeInformation();
			types.put(location, type);
			return new VariableTypes(types, isComplete());
		}

		public VariableTypes combineWith(VariableTypes types) {
			Map<Integer, JvmType> types1 = getTypeInformation();
			Map<Integer, JvmType> types2 = types.getTypeInformation();

			for (Integer i : types2.keySet()) {
				if (!types1.containsKey(i)) {
					types1.put(i, types2.get(i));
				}
			}

			return new VariableTypes(types1, isComplete() || types.isComplete());
		}

	}

}
