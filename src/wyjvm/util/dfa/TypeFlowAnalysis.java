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

public abstract class TypeFlowAnalysis<C> {

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
	public C typesAt(int at) {
		Map<String, TypeInformation> labelTypes =
		    new HashMap<String, TypeInformation>();

		int size = codes.size();
		while (true) {
			Map<String, TypeInformation> comparison =
			    new HashMap<String, TypeInformation>(labelTypes);

			TypeInformation currentTypes = initTypes();

			for (int i = 0; i < size; ++i) {
				Bytecode code = codes.get(i);

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
						currentTypes = emptyTypes();
					}
				}
			}

			// If there's no change after running through, there's no more information
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
	 * How the analysis should respond to the given bytecode with the current
	 * types by producing a new current types value.
	 * 
	 * @param types The current types in the flow analysis.
	 * @param code The code to respond to.
	 * @return The new current types once the code is executed.
	 */
	protected abstract TypeInformation respondTo(TypeInformation types,
	    Bytecode code);

	private void addLabelInformation(Map<String, TypeInformation> labelTypes,
	    String labelName, TypeInformation currentTypes) {
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

	protected abstract class TypeInformation {

		private final C typeInformation;
		private boolean complete;

		public TypeInformation(C typeInformation, boolean complete) {
			this.typeInformation = typeInformation;
			this.complete = complete;
		}

		public C getTypeInformation() {
			return typeInformation;
		}

		public boolean isComplete() {
			return complete;
		}

		public void setComplete(boolean complete) {
			this.complete = complete;
		}

		public abstract TypeInformation combineWith(TypeInformation types);

		@Override
		public boolean equals(Object o) {
			if (o instanceof TypeFlowAnalysis.TypeInformation) {
				TypeFlowAnalysis<?>.TypeInformation type =
				    (TypeFlowAnalysis<?>.TypeInformation) o;
				return typeInformation.equals(type.typeInformation)
				    && complete == type.complete;
			}

			return false;
		}

	}

}
