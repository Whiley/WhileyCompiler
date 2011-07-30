package wyjvm.util.dfa;

import java.util.List;

import wyjvm.attributes.Code;
import wyjvm.lang.Bytecode;
import wyjvm.lang.BytecodeAttribute;
import wyjvm.lang.ClassFile.Method;

public abstract class TypeFlowAnalysis {

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

	protected abstract class TypeInformation<C> {

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

		@Override
		public boolean equals(Object o) {
			if (o instanceof TypeInformation<?>) {
				TypeInformation<?> type = (TypeInformation<?>) o;
				return typeInformation.equals(type.typeInformation)
				    && complete == type.complete;
			}

			return false;
		}

	}

}
