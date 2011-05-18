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
		System.out.println(method.name());
		System.out.println(method.parameters().size());
	}
	
}
