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
import wyjvm.attributes.Code;
import wyjvm.lang.Bytecode;
import wyjvm.lang.Bytecode.Branch;
import wyjvm.lang.Bytecode.Goto;
import wyjvm.lang.Bytecode.Label;
import wyjvm.lang.Bytecode.Return;
import wyjvm.lang.Bytecode.Switch;
import wyjvm.lang.Bytecode.Throw;
import wyjvm.lang.BytecodeAttribute;
import wyjvm.lang.ClassFile.Method;

/**
 * Analyses a Java bytecode method to determine the types of a particular kind
 * at any given point in the code. Which kind should be determined by the
 * specific subclass.
 * 
 * @author Timothy Jones
 * 
 * @param <Types> The collection type of the types content
 */
public abstract class TypeFlowAnalysis<Types> {

	protected final Method method;
	protected final List<Bytecode> codes;

	public TypeFlowAnalysis(Method method) {
		this.method = method;

		for (BytecodeAttribute attribute : method.attributes()) {
			if (attribute instanceof Code) {
				codes = ((Code) attribute).bytecodes();
				return;
			}
		}

		throw new IllegalArgumentException("Method has no Code attribute.");
	}

	/**
	 * Analyses the bytecode of the given method in order to find the types of
	 * whatever is being analysed are before the the code at the given position
	 * would run.
	 * 
	 * @param at The position in the code to obtain the variable types at.
	 * @return A map from variable number to type.
	 */
	public Types typesAt(int at) {
		Map<String, TypeInformation> labelTypes =
				new HashMap<String, TypeInformation>();

		int size = codes.size();
		while (true) {
			Map<String, TypeInformation> comparison =
					new HashMap<String, TypeInformation>(labelTypes);

			TypeInformation currentTypes = initTypes();

			for (int i = 0; i < size; ++i) {
				Bytecode code = codes.get(i);

				if (i > 0) {
					Bytecode code2 = codes.get(i - 1);
					if (code2 instanceof Label) {
						if (((Label) code2).name.equals("resume0")) {

						}
					}
				}

				if (i == at && currentTypes.isComplete()) {
					return currentTypes.getTypeInformation();
				}

				currentTypes = respondTo(currentTypes, code);

				if (code instanceof Label) {
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

				if (i < size - 1) {
					Bytecode next = codes.get(i + 1);
					boolean dead =
							code instanceof Goto || code instanceof Return
									|| code instanceof Throw;

					if (dead) {
						if (!(next instanceof Label)) {
							throw new IllegalStateException("Deadcode found.");
						}

						if (i == at - 1) {
							// Really there's no type information, because it's impossible
							// for the flow to actually reach this point, but this makes
							// debugging much easier. No one should care about this point.
							return emptyTypes().getTypeInformation();
						}
					}

					if (next instanceof Label) {
						String labelName = ((Label) codes.get(i + 1)).name;

						if (labelTypes.containsKey(labelName)) {
							if (dead || !currentTypes.isComplete()) {
								currentTypes = labelTypes.get(labelName);
							}
						} else if (dead) {
							// The current type information is now useless.
							// Note that this must be partial, because we really don't
							// have
							// any information at all at this point.
							currentTypes = emptyTypes();
						}
					}
				}
			}

			// If there's no change after running through, there's no more
			// information
			// to collect.
			if (comparison.equals(labelTypes)) {
				for (TypeInformation types : labelTypes.values()) {
					types.setComplete(true);
				}
			}
		}
	}

	/**
	 * @return The types at the very start of the method.
	 */
	protected abstract TypeInformation initTypes();

	/**
	 * @return An empty, incomplete type representation.
	 */
	protected abstract TypeInformation emptyTypes();

	/**
	 * A template method on how the analysis should respond to the given
	 * bytecode with the current types by producing a new current types value.
	 * 
	 * @param types The current types in the flow analysis.
	 * @param code The code to respond to.
	 * @return The new current types once the code is executed.
	 */
	protected abstract TypeInformation respondTo(TypeInformation types,
			Bytecode code);

	private void addLabelInformation(Map<String, TypeInformation> labelTypes,
			String labelName, TypeInformation currentTypes) {
		// The analysis tends to be run before the switch for resumes is added,
		// so this allows the types inside the resume code to complete.
		if (labelName.matches("resume\\d+")) {
			TypeInformation types = emptyTypes();
			types.setComplete(true);
			labelTypes.put(labelName, types);
		}

		if (labelTypes.containsKey(labelName)) {
			TypeInformation labelType = labelTypes.get(labelName);
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

	/**
	 * Stores both the type information and whether the information given is
	 * complete. Extend this class in a subclass of
	 * <code>TypeFlowAnalysis</code>.
	 * 
	 * @author Timothy Jones
	 */
	protected abstract class TypeInformation {

		private final Types typeInformation;
		private boolean complete;

		public TypeInformation(Types typeInformation, boolean complete) {
			this.typeInformation = typeInformation;
			this.complete = complete;
		}

		/**
		 * @return The stored type information.
		 */
		public Types getTypeInformation() {
			return typeInformation;
		}

		/**
		 * @return Whether the type information is complete.
		 */
		public boolean isComplete() {
			return complete;
		}

		/**
		 * @param complete Whether the type information is complete.
		 */
		public void setComplete(boolean complete) {
			this.complete = complete;
		}

		/**
		 * Given another type information, combine it with this one to produce an
		 * entirely new type information.
		 * 
		 * Remember that because <code>TypeInformation</code> is an inner class,
		 * the parameter must have type information of type <code>Types</code>.
		 * 
		 * @param types The type information to combine with.
		 * @return The combined type information as a new object.
		 */
		public abstract TypeInformation combineWith(TypeInformation types);

		@Override
		public boolean equals(Object o) {
			if (o instanceof TypeFlowAnalysis.TypeInformation) {
				// Ant can't handle wild cards here, it seems.
				@SuppressWarnings("rawtypes") TypeFlowAnalysis.TypeInformation type =
						(TypeFlowAnalysis.TypeInformation) o;
				return typeInformation.equals(type.typeInformation)
						&& complete == type.complete;
			}

			return false;
		}

	}

}
