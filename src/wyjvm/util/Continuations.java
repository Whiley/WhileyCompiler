package wyjvm.util;

import static wyjvm.lang.JvmTypes.JAVA_LANG_OBJECT;
import static wyjvm.lang.JvmTypes.T_BOOL;
import static wyjvm.lang.JvmTypes.T_INT;
import static wyjvm.lang.JvmTypes.T_VOID;

import java.util.ArrayList;
import java.util.List;

import wyil.util.Pair;
import wyjvm.attributes.Code;
import wyjvm.lang.Bytecode;
import wyjvm.lang.Bytecode.Dup;
import wyjvm.lang.Bytecode.If;
import wyjvm.lang.Bytecode.Invoke;
import wyjvm.lang.Bytecode.Label;
import wyjvm.lang.Bytecode.Load;
import wyjvm.lang.Bytecode.LoadConst;
import wyjvm.lang.Bytecode.Return;
import wyjvm.lang.Bytecode.Store;
import wyjvm.lang.Bytecode.Switch;
import wyjvm.lang.Bytecode.Throw;
import wyjvm.lang.BytecodeAttribute;
import wyjvm.lang.ClassFile;
import wyjvm.lang.ClassFile.Method;
import wyjvm.lang.JvmType;
import wyjvm.lang.JvmType.Clazz;
import wyjvm.lang.JvmType.Function;
import wyjvm.lang.JvmType.Int;

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
//		System.out.println(method.name());
		for (BytecodeAttribute attribute : method.attributes()) {
			if (attribute instanceof Code) {
				apply(method, (Code) attribute);
				
//				for (Bytecode bytecode : ((Code) attribute).bytecodes()) {
//					System.out.println(bytecode);
//				}
//				System.out.println();
			}
		}
	}

	public void apply(Method method, Code code) {
		List<Bytecode> bytecodes = code.bytecodes();
		List<JvmType> types = method.type().parameterTypes();

		int location = 0;

		for (int i = 0; i < bytecodes.size(); ++i) {
			Bytecode bytecode = bytecodes.get(i);

			if (bytecode instanceof Invoke) {
				Invoke invoke = (Invoke) bytecode;

				boolean yields = false;
				if (invoke.mode == Bytecode.STATIC) {
					List<JvmType> ptypes = invoke.type.parameterTypes();
					if (ptypes.size() >= 1 && ptypes.get(0).equals(PROCESS)) {
						yields = true;
					}
				}

				if (yields || invoke.owner.equals(MESSAGER)
				    && invoke.name.startsWith("send")) {
					List<Bytecode> add = new ArrayList<Bytecode>();
					Bytecode next =
					    i == bytecodes.size() - 1 ? null : bytecodes.get(i + 1);
					boolean isVoid = invoke.type.returnType().equals(T_VOID);
					boolean push =
					    !yields && !(next instanceof Return || next instanceof Throw)
					        && !isVoid;

					add.add(new Load(0, PROCESS));

					Function type = null;
					if (push) {
						add.add(new LoadConst(location));
						type = new Function(T_VOID, T_INT);
					} else {
						type = new Function(T_VOID);
					}

					add.add(new Invoke(MESSAGER, "yield", type, Bytecode.VIRTUAL));

					if (push) {
						addPushLocals(method.type().parameterTypes(), add);

						add.add(new Return(null));

						add.add(new Label("resume" + location++));
					}
				}
			}
		}

		// If we yielded at some point, we need to add the unpacking at the start.
		if (location-- > 0) {
			List<Bytecode> add = new ArrayList<Bytecode>();
			add.add(new Load(0, PROCESS));
			add.add(new Invoke(PROCESS, "isYielded", new Function(T_BOOL),
			    Bytecode.VIRTUAL));
			add.add(new If(If.EQ, "begin"));
			add.add(new Load(0, PROCESS));

			addPopLocals(types, add);

			List<Pair<Integer, String>> cases =
			    new ArrayList<Pair<Integer, String>>(location);
			for (int i = 0; i < location; ++i) {
				cases.add(new Pair<Integer, String>(i, "resume" + i));
			}

			add.add(new Invoke(YIELDER, "unyield", new Function(T_INT),
			    Bytecode.VIRTUAL));
			add.add(new Switch("resume" + location, cases));

			add.add(new Label("begin"));

			bytecodes.addAll(0, add);
		}
	}

	private void addPushLocals(List<JvmType> types, List<Bytecode> add) {
		int size = types.size();
		for (int i = 1; i < size; ++i) {
			JvmType type = types.get(i);
			add.add(new Load(0, PROCESS));
			add.add(new Load(i, type));

			JvmType fnType = null;
			if (type instanceof Clazz) {
				fnType = JAVA_LANG_OBJECT;
			} else if (type instanceof Int) {
				fnType = T_INT;
			}

			if (fnType == null) {
				throw new IllegalStateException("Unknown primitive in locals.");
			}

			add.add(new Invoke(YIELDER, "push", new Function(T_VOID, fnType),
			    Bytecode.VIRTUAL));
		}
	}

	private void addPopLocals(List<JvmType> types, List<Bytecode> add) {
		int size = types.size();
		for (int i = 1; i < size; ++i) {
			JvmType type = types.get(i);
			add.add(new Dup(PROCESS));

			String name = null;
			JvmType fnType = null;
			if (type instanceof Clazz) {
				name = "Object";
				fnType = JAVA_LANG_OBJECT;
			} else if (type instanceof Int) {
				name = "Int";
				fnType = T_INT;
			}

			if (name == null || fnType == null) {
				throw new IllegalStateException("Unknown primitive in locals.");
			}

			add.add(new Invoke(YIELDER, "pop" + name, new Function(fnType),
			    Bytecode.VIRTUAL));
			add.add(new Store(i, fnType));
		}
	}

}
