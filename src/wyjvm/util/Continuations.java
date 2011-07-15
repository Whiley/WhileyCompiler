package wyjvm.util;

import static wyjvm.lang.JvmTypes.T_INT;
import static wyjvm.lang.JvmTypes.T_VOID;

import java.util.ArrayList;
import java.util.List;

import wyil.util.Pair;
import wyjvm.attributes.Code;
import wyjvm.lang.Bytecode;
import wyjvm.lang.Bytecode.Dup;
import wyjvm.lang.Bytecode.Invoke;
import wyjvm.lang.Bytecode.Label;
import wyjvm.lang.Bytecode.Load;
import wyjvm.lang.Bytecode.LoadConst;
import wyjvm.lang.Bytecode.Return;
import wyjvm.lang.Bytecode.Switch;
import wyjvm.lang.Bytecode.Throw;
import wyjvm.lang.BytecodeAttribute;
import wyjvm.lang.ClassFile;
import wyjvm.lang.ClassFile.Method;
import wyjvm.lang.JvmType.Clazz;
import wyjvm.lang.JvmType.Function;

public class Continuations {

	private static final Clazz PROCESS = new Clazz("wyjc.runtime", "Actor"),
	    MESSAGER = new Clazz("wyjc.runtime.concurrency", "Messager"),
	    YIELDER = new Clazz("wyjc.runtime.concurrency", "Yielder");

	public void apply(ClassFile classfile) {
		for (Method method : classfile.methods()) {
			if (!method.name().equals("main")) {
				apply(method);
			}
		}
	}

	public void apply(Method method) {
		for (BytecodeAttribute attribute : method.attributes()) {
			if (attribute instanceof Code) {
				apply(method, (Code) attribute);
				break;
			}
		}
	}

	public void apply(Method method, Code code) {
		List<Bytecode> bytecodes = code.bytecodes();
//		List<JvmType> types = method.type().parameterTypes();

		int location = 0;

		for (int i = 0; i < bytecodes.size(); ++i) {
			Bytecode bytecode = bytecodes.get(i);

			if (bytecode instanceof Invoke) {
				Invoke invoke = (Invoke) bytecode;

				if (invoke.owner.equals(MESSAGER) && invoke.name.startsWith("send")) {
					List<Bytecode> add = new ArrayList<Bytecode>();
					Bytecode next =
					    i == bytecodes.size() - 1 ? null : bytecodes.get(i + 1);

					boolean push =
					    invoke.name.equals("sendSync")
					        || !(next instanceof Return || next instanceof Throw);

					add.add(new Dup(PROCESS));

					if (push) {
						add.add(new LoadConst(location));
						add.add(new Invoke(MESSAGER, "yield", new Function(T_VOID, T_INT),
						    Bytecode.VIRTUAL));
					} else {
						add.add(new Invoke(MESSAGER, "cleanYield", new Function(T_VOID),
						    Bytecode.VIRTUAL));
					}
					
					// This is a bit of a hack. If the number of bytecode operations
					// needed to set up the parameters changes, this will break.
					bytecodes.addAll(i - 4, add);
					
					i += add.size();

					if (push) {
						bytecodes.add(++i, new Return(null));
						bytecodes.add(++i, new Label("resume" + location++));
					}
				}
			}
		}

		// If we yielded at some point, we need to add the unpacking at the start.
		if (location > 0) {
			List<Bytecode> add = new ArrayList<Bytecode>();
			add.add(new Load(0, PROCESS));
			add.add(new Invoke(YIELDER, "getCurrentStateLocation", new Function(T_INT),
					Bytecode.VIRTUAL));

			List<Pair<Integer, String>> cases =
			    new ArrayList<Pair<Integer, String>>(location);
			for (int i = 0; i < location; ++i) {
				cases.add(new Pair<Integer, String>(i, "resume" + i));
			}

			add.add(new Switch("begin", cases));
			add.add(new Label("begin"));

			bytecodes.addAll(0, add);
		}
	}
//
//	private void addPushLocals(List<JvmType> types, List<Bytecode> add) {
//		int size = types.size();
//		for (int i = 1; i < size; ++i) {
//			JvmType type = types.get(i);
//			add.add(new Load(0, PROCESS));
//			add.add(new Load(i, type));
//
//			JvmType fnType = null;
//			if (type instanceof Clazz) {
//				fnType = JAVA_LANG_OBJECT;
//			} else if (type instanceof Int) {
//				fnType = T_INT;
//			}
//
//			if (fnType == null) {
//				throw new IllegalStateException("Unknown primitive in locals.");
//			}
//
//			add.add(new Invoke(YIELDER, "push", new Function(T_VOID, fnType),
//			    Bytecode.VIRTUAL));
//		}
//	}
//
//	private void addPopLocals(List<JvmType> types, List<Bytecode> add) {
//		int size = types.size();
//		for (int i = 1; i < size; ++i) {
//			JvmType type = types.get(i);
//			add.add(new Dup(PROCESS));
//
//			String name = null;
//			JvmType fnType = null;
//			if (type instanceof Clazz) {
//				name = "Object";
//				fnType = JAVA_LANG_OBJECT;
//			} else if (type instanceof Int) {
//				name = "Int";
//				fnType = T_INT;
//			}
//
//			if (name == null || fnType == null) {
//				throw new IllegalStateException("Unknown primitive in locals.");
//			}
//
//			add.add(new Invoke(YIELDER, "pop" + name, new Function(fnType),
//			    Bytecode.VIRTUAL));
//			add.add(new Store(i, fnType));
//		}
//	}

}
