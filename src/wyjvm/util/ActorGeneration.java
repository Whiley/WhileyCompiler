package wyjvm.util;

import wyjvm.lang.ClassFile;
import wyjvm.lang.ClassFile.Method;
import wyjvm.lang.ClassFile.Parameter;

public class ActorGeneration {
	
	public void apply(ClassFile classfile) {
		for (Method method : classfile.methods()) {
			apply(method);
		}
	}
	
	public void apply(Method method) {
		method.parameters().add(0, new Parameter());
	}
	
}
