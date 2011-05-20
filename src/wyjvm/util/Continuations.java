package wyjvm.util;

import java.util.List;

import wyjvm.attributes.Code;
import wyjvm.lang.Bytecode;
import wyjvm.lang.BytecodeAttribute;
import wyjvm.lang.ClassFile;
import wyjvm.lang.ClassFile.Method;

public class Continuations {
	
	public void apply(ClassFile classfile) {
		for (Method method : classfile.methods()) {
			if (method.name().startsWith("main$")) {
				apply(method);
			}
		}
	}
	
	public void apply(Method method) {
		for (BytecodeAttribute attribute : method.attributes()) {
			if (attribute instanceof Code) {
				apply((Code) attribute);
			}
		}
	}
	
	public void apply(Code code) {
		List<Bytecode> bytecodes = code.bytecodes();
		
		for (Bytecode bytecode : bytecodes) {
			System.out.println(bytecode);
		}
	}
	
}
