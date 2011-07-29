package wyjvm.util.dfa;

import java.util.List;

import wyjvm.attributes.Code;
import wyjvm.lang.Bytecode;
import wyjvm.lang.BytecodeAttribute;
import wyjvm.lang.ClassFile.Method;

public abstract class TypeFlowAnalysis {

	protected final Method method;
	protected final List<Bytecode> code;
	
	public TypeFlowAnalysis(Method method) {
		this.method = method;
		
		for (BytecodeAttribute attribute : method.attributes()) {
			if (attribute instanceof Code) {
				code = ((Code) attribute).bytecodes();
				return;
			}
		}

		throw new IllegalArgumentException("Method has no Code attribute.");
	}
	
}
