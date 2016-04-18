// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyjc;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

import wybs.lang.Build;
import wybs.lang.Builder;
import wycc.lang.NameID;
import wycc.util.Logger;
import wycc.util.Pair;
import wycc.util.ResolveError;
import wyautl.util.BigRational;
import wyfs.io.BinaryOutputStream;
import wyfs.lang.Path;
import wyfs.util.Trie;
import wyil.attributes.SourceLocation;
import wyil.lang.*;
import wyil.lang.Constant;
import static wyil.lang.Bytecode.*;
import wyil.util.TypeExpander;
import static wyil.util.ErrorMessages.internalFailure;
import static wyjc.Wyil2JavaBuilder.WHILEYUTIL;

import wyjc.util.BytecodeTranslators;
import wyjc.util.WyjcBuildTask;
import jasm.attributes.Code.Handler;
import jasm.attributes.LineNumberTable;
import jasm.attributes.SourceFile;
import jasm.lang.*;
import jasm.lang.Bytecode;
import jasm.lang.Modifier;
import jasm.util.Triple;
import jasm.verifier.ClassFileVerifier;
import wyrl.io.JavaIdentifierOutputStream;
import static jasm.lang.JvmTypes.*;

/**
 * Responsible for converting WYIL files into Java Classfiles. This is a
 * relatively straightforward process, given the all the hard work has already
 * been done by the Whiley-2-Wyil builder.
 *
 * @author David J. Pearce
 *
 */
public class Wyil2JavaBuilder implements Builder {
	private static int CLASS_VERSION = 49;

	/**
	 * The master project for identifying all resources available to the
	 * builder. This includes all modules declared in the project being verified
	 * and/or defined in external resources (e.g. jar files).
	 */
	protected final Build.Project project;

	/**
	 * The type expander is useful for managing nominal types and converting
	 * them into their underlying types.
	 */
	protected final TypeExpander expander;

	/**
	 * For logging information.
	 */
	private Logger logger = Logger.NULL;

	/**
	 * Filename of module being translated
	 */
	private String filename;

	/**
	 * Type of enclosing class being generated
	 */
	private JvmType.Clazz owner;

	/**
	 * The set of generators for individual WyIL bytecodes
	 */
	private BytecodeTranslator[] generators;
	
	/**
	 * Map of Constant values to their pool index
	 */
	private HashMap<JvmConstant, Integer> constants;

	/**
	 * List of temporary classes created to implement lambda expressions
	 */
	private ArrayList<ClassFile> lambdas;

	/**
	 * List of line number entries for current function / method being compiled.
	 */
	private ArrayList<LineNumberTable.Entry> lineNumbers;

	public Wyil2JavaBuilder(Build.Project project) {
		this.project = project;
		this.expander = new TypeExpander(project);
		this.generators = BytecodeTranslators.standardFunctions;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public Build.Project project() {
		return project;
	}

	public Set<Path.Entry<?>> build(
			Collection<Pair<Path.Entry<?>, Path.Root>> delta)
			throws IOException {

		Runtime runtime = Runtime.getRuntime();
		long start = System.currentTimeMillis();
		long memory = runtime.freeMemory();

		// ========================================================================
		// Translate files
		// ========================================================================
		HashSet<Path.Entry<?>> generatedFiles = new HashSet<Path.Entry<?>>();

		for (Pair<Path.Entry<?>, Path.Root> p : delta) {
			Path.Root dst = p.second();
			Path.Entry<WyilFile> sf = (Path.Entry<WyilFile>) p.first();
			Path.Entry<ClassFile> df = dst.create(sf.id(),
					WyjcBuildTask.ContentType);
			generatedFiles.add(df);

			// Translate WyilFile into JVM ClassFile
			lambdas = new ArrayList<ClassFile>();
			ClassFile contents = build(sf.read());

			// FIXME: deadCode elimination is currently unsafe because the
			// LineNumberTable and Exceptions attributes do not deal with
			// rewrites
			// properly.

			// eliminate any dead code that was introduced.
			// new DeadCodeElimination().apply(file);

			// Verify the generated file being written
			// new ClassFileVerifier().apply(contents);

			// Write class file into its destination
			df.write(contents);

			// Finally, write out any lambda classes created to support the
			// main class. This is necessary because every occurrence of a
			// lambda expression in the WyilFile generates an inner class
			// responsible for calling the given function.
			Path.ID parent = df.id();
			Path.ID pkg = parent.subpath(0, parent.size() - 1);
			for (int i = 0; i != lambdas.size(); ++i) {
				Path.ID id = pkg.append(parent.last() + "$" + i);
				Path.Entry<ClassFile> lf = dst.create(id,
						WyjcBuildTask.ContentType);
				lf.write(lambdas.get(i));
				generatedFiles.add(lf);
			}
		}

		// ========================================================================
		// Done
		// ========================================================================

		long endTime = System.currentTimeMillis();
		logger.logTimedMessage("Wyil => Java: compiled " + delta.size()
				+ " file(s)", endTime - start, memory - runtime.freeMemory());

		return generatedFiles;
	}

	private ClassFile build(WyilFile module) {
		owner = new JvmType.Clazz(module.id().parent().toString()
				.replace('.', '/'), module.id().last());
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.ACC_PUBLIC);
		modifiers.add(Modifier.ACC_FINAL);
		ClassFile cf = new ClassFile(CLASS_VERSION, owner, JAVA_LANG_OBJECT,
				new ArrayList<JvmType.Clazz>(), modifiers);

		this.filename = module.filename();

		if (filename != null) {
			cf.attributes().add(new SourceFile(filename));
		}

		boolean addMainLauncher = false;

		constants = new HashMap<JvmConstant, Integer>();
		for (WyilFile.Block blk : module.blocks()) {
			if (blk instanceof WyilFile.FunctionOrMethod) {
				WyilFile.FunctionOrMethod method = (WyilFile.FunctionOrMethod) blk;
				if (method.name().equals("main")) {
					addMainLauncher = true;
				}
				cf.methods().addAll(build(method));
			} else if (blk instanceof WyilFile.Type) {
				cf.methods().add(build((WyilFile.Type) blk));
			}
		}

		buildConstants(constants, lambdas, cf);

		if (addMainLauncher) {
			cf.methods().add(buildMainLauncher(owner));
		}

		return cf;
	}

	private void buildConstants(HashMap<JvmConstant, Integer> constants,
			ArrayList<ClassFile> lambdas, ClassFile cf) {
		buildCoercions(constants, cf);
		buildValues(constants, lambdas, cf);
	}

	private void buildCoercions(HashMap<JvmConstant, Integer> constants,
			ClassFile cf) {
		HashSet<JvmConstant> done = new HashSet<JvmConstant>();
		HashMap<JvmConstant, Integer> original = constants;

		// this could be a little more efficient I think!!
		while (done.size() != constants.size()) {
			// We have to clone the constants map, since it may be expanded as a
			// result of buildCoercion(). This will occur if the coercion
			// constructed requires a helper coercion that was not in the
			// original constants map.
			HashMap<JvmConstant, Integer> nconstants = new HashMap<JvmConstant, Integer>(
					constants);
			for (Map.Entry<JvmConstant, Integer> entry : constants.entrySet()) {
				JvmConstant e = entry.getKey();
				if (!done.contains(e) && e instanceof JvmCoercion) {
					JvmCoercion c = (JvmCoercion) e;
					buildCoercion(c.from, c.to, entry.getValue(), nconstants,
							cf);
				}
				done.add(e);
			}
			constants = nconstants;
		}
		original.putAll(constants);
	}

	private void buildValues(HashMap<JvmConstant, Integer> constants,
			ArrayList<ClassFile> lambdas, ClassFile cf) {
		int nvalues = 0;
		ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();

		for (Map.Entry<JvmConstant, Integer> entry : constants.entrySet()) {
			JvmConstant c = entry.getKey();
			if (c instanceof JvmValue) {
				nvalues++;
				Constant constant = ((JvmValue) c).value;
				int index = entry.getValue();

				// First, create the static final field that will hold this
				// constant
				String name = "constant$" + index;
				ArrayList<Modifier> fmods = new ArrayList<Modifier>();
				fmods.add(Modifier.ACC_PRIVATE);
				fmods.add(Modifier.ACC_STATIC);
				fmods.add(Modifier.ACC_FINAL);
				JvmType type = convertUnderlyingType(constant.type());
				ClassFile.Field field = new ClassFile.Field(name, type, fmods);
				cf.fields().add(field);

				// Now, create code to intialise this field
				translate(constant, 0, bytecodes);
				bytecodes.add(new Bytecode.PutField(owner, name, type,
						Bytecode.FieldMode.STATIC));
			}
		}

		if (nvalues > 0) {
			// create static initialiser method, but only if we really need to.
			bytecodes.add(new Bytecode.Return(null));

			ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
			modifiers.add(Modifier.ACC_PUBLIC);
			modifiers.add(Modifier.ACC_STATIC);
			modifiers.add(Modifier.ACC_SYNTHETIC);
			JvmType.Function ftype = new JvmType.Function(new JvmType.Void());
			ClassFile.Method clinit = new ClassFile.Method("<clinit>", ftype,
					modifiers);
			cf.methods().add(clinit);

			// finally add code for staticinitialiser method
			jasm.attributes.Code code = new jasm.attributes.Code(bytecodes,
					new ArrayList(), clinit);
			clinit.attributes().add(code);
		}
	}

	private ClassFile.Method buildMainLauncher(JvmType.Clazz owner) {
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.ACC_PUBLIC);
		modifiers.add(Modifier.ACC_STATIC);
		modifiers.add(Modifier.ACC_SYNTHETIC);
		JvmType.Function ft1 = new JvmType.Function(T_VOID, new JvmType.Array(
				JAVA_LANG_STRING));
		ClassFile.Method cm = new ClassFile.Method("main", ft1, modifiers);
		JvmType.Array strArr = new JvmType.Array(JAVA_LANG_STRING);
		ArrayList<Bytecode> codes = new ArrayList<Bytecode>();
		ft1 = new JvmType.Function(WHILEYRECORD, new JvmType.Array(
				JAVA_LANG_STRING));
		codes.add(new Bytecode.Load(0, strArr));
		codes.add(new Bytecode.Invoke(WHILEYUTIL, "systemConsole", ft1,
				Bytecode.InvokeMode.STATIC));
		Type.Method wyft = Type.Method(new Type[0], WHILEY_SYSTEM_T);
		JvmType.Function ft3 = convertFunType(wyft);
		codes.add(new Bytecode.Invoke(owner, nameMangle("main", wyft), ft3,
				Bytecode.InvokeMode.STATIC));
		codes.add(new Bytecode.Return(null));

		jasm.attributes.Code code = new jasm.attributes.Code(codes,
				new ArrayList(), cm);
		cm.attributes().add(code);

		return cm;
	}

	/**
	 * Construct a method for accepting the constraints on this particular type.
	 * 
	 * @param td
	 */
	private ClassFile.Method build(WyilFile.Type td) {
		JvmType underlyingType = convertUnderlyingType(td.type());
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.ACC_PUBLIC);
		modifiers.add(Modifier.ACC_STATIC);
		modifiers.add(Modifier.ACC_SYNTHETIC);
		JvmType.Function funType = new JvmType.Function(T_BOOL, underlyingType);
		ClassFile.Method cm = new ClassFile.Method(td.name() + "$typeof",
				funType, modifiers);
		ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
		// First, generate code for testing elements of type (if any)
		String falseBranch = freshLabel();
		// FIXME: this is inefficient in cases where there are no invariants in
		// component types (e.g. there are no component types).
		translateInvariantTest(falseBranch, td.type(), 0, 1, constants, bytecodes);
		// Second, generate code for invariant (if applicable).
		// FIXME: use of patchInvariantBlock is not ideal
		BytecodeForest invariant = patchInvariantBlock(falseBranch, td.invariant());
		for(int i=0;i!=invariant.numRoots();++i) {				
			translate(invariant.getRoot(i), 1, invariant, bytecodes);
		}
		bytecodes.add(new Bytecode.LoadConst(true));
		bytecodes.add(new Bytecode.Return(new JvmType.Bool()));
		bytecodes.add(new Bytecode.Label(falseBranch));
		bytecodes.add(new Bytecode.LoadConst(false));
		bytecodes.add(new Bytecode.Return(new JvmType.Bool()));
		// Done
		jasm.attributes.Code code = new jasm.attributes.Code(bytecodes,
				new ArrayList(), cm);
		cm.attributes().add(code);
		return cm;
	}

	/**
	 * Update the invariant block to replace fail and return statements with
	 * goto's and nops (respectively).
	 * 
	 * @param falseBranch
	 * @param forest
	 */
	private BytecodeForest patchInvariantBlock(String falseBranch, BytecodeForest forest) {
		BytecodeForest nForest = new BytecodeForest(forest);
		for(int i=0;i!=forest.numBlocks();++i) {
			patchInvariantBlockHelper(falseBranch, nForest.get(i));
		}
		return nForest;
	}

	private void patchInvariantBlockHelper(String falseBranch, BytecodeForest.Block block) {
		for (int i = 0; i != block.size(); ++i) {
			// This is still a valid index
			BytecodeForest.Entry e = block.get(i);
			wyil.lang.Bytecode c = e.code();

			if (c instanceof Return) {
				// first patch point
				block.set(i, new Operator(Type.T_VOID, new int[0], new int[0], OperatorKind.ASSIGN));
			} else if (c instanceof Fail) {
				// second patch point
				block.set(i, new Goto(falseBranch));
			} 
		}
	}

	private List<ClassFile.Method> build(WyilFile.FunctionOrMethod method) {
		ArrayList<ClassFile.Method> methods = new ArrayList<ClassFile.Method>();
		
		// Firstly, check to see whether or not this is a native method. Native
		// methods are treated specially and redirect to the same-named methods,
		// but with "$native" appended. 
		if (method.hasModifier(wyil.lang.Modifier.NATIVE)) {
			methods.add(buildNativeOrExport(method, constants));
		} else {
			// Secondly, check to whether or not this is an exported method.
			// Exported methods generate a single stub without name mangling
			// that redirects to the actual method. This means that the actual
			// method can be called as usual from within Whiley code, whilst
			// external calls are correctly redirected.
			if (method.hasModifier(wyil.lang.Modifier.EXPORT)) {
				methods.add(buildNativeOrExport(method, constants));
			}
			// Finally, translate the method and its body.
			methods.add(translate(method));
		}
		
		return methods;
	}

	private ClassFile.Method buildNativeOrExport(WyilFile.FunctionOrMethod method,
			HashMap<JvmConstant, Integer> constants) {
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		if (method.hasModifier(wyil.lang.Modifier.PUBLIC)
				|| method.hasModifier(wyil.lang.Modifier.PUBLIC)) {
			modifiers.add(Modifier.ACC_PUBLIC);
		}
		modifiers.add(Modifier.ACC_STATIC);
		JvmType.Function ft = convertFunType(method.type());

		String name = method.name();
		if (method.hasModifier(wyil.lang.Modifier.NATIVE)) {
			name = nameMangle(method.name(), method.type());
		}

		ClassFile.Method cm = new ClassFile.Method(name, ft, modifiers);

		ArrayList<Bytecode> codes;
		codes = translateNativeOrExport(method);
		jasm.attributes.Code code = new jasm.attributes.Code(codes,
				Collections.EMPTY_LIST, cm);

		cm.attributes().add(code);

		return cm;
	}

	private ArrayList<Bytecode> translateNativeOrExport(
			WyilFile.FunctionOrMethod method) {

		ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
		Type.FunctionOrMethod ft = method.type();
		int slot = 0;

		for (Type param : ft.params()) {
			bytecodes.add(new Bytecode.Load(slot++,
					convertUnderlyingType(param)));
		}

		if (method.hasModifier(wyil.lang.Modifier.NATIVE)) {
			JvmType.Clazz redirect = new JvmType.Clazz(owner.pkg(), owner
					.components().get(0).first(), "native");
			bytecodes.add(new Bytecode.Invoke(redirect, method.name(),
					convertFunType(ft), Bytecode.InvokeMode.STATIC));
		} else {
			JvmType.Clazz redirect = new JvmType.Clazz(owner.pkg(), owner
					.components().get(0).first());
			bytecodes.add(new Bytecode.Invoke(redirect, nameMangle(
					method.name(), method.type()), convertFunType(ft),
					Bytecode.InvokeMode.STATIC));
		}

		if (ft.returns().isEmpty()) {
			bytecodes.add(new Bytecode.Return(null));
		} else {
			bytecodes.add(new Bytecode.Return(convertUnderlyingType(ft.returns().get(0))));
		}

		return bytecodes;
	}

	private ClassFile.Method translate(WyilFile.FunctionOrMethod method) {

		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		if (method.hasModifier(wyil.lang.Modifier.PUBLIC)) {
			modifiers.add(Modifier.ACC_PUBLIC);
		}
		modifiers.add(Modifier.ACC_STATIC);
		JvmType.Function ft = convertFunType(method.type());

		String name = nameMangle(method.name(), method.type());
		
		ClassFile.Method cm = new ClassFile.Method(name, ft, modifiers);

		lineNumbers = new ArrayList<LineNumberTable.Entry>();
		ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
		BytecodeForest forest = method.code();
		translate(method.body(), forest.numRegisters(), forest, bytecodes);
		jasm.attributes.Code code = new jasm.attributes.Code(bytecodes,
				Collections.EMPTY_LIST, cm);
		if (!lineNumbers.isEmpty()) {
			code.attributes().add(new LineNumberTable(lineNumbers));
		}
		cm.attributes().add(code);

		return cm;
	}

	/**
	 * Translate the given block into bytecodes.
	 *
	 * @param blk
	 *            --- wyil block to be translated.
	 * @param freeSlot
	 *            --- identifies the first unsused bytecode register.
	 * @param bytecodes
	 *            --- list to insert bytecodes into *
	 */
	private void translate(int blk, int freeSlot, BytecodeForest forest, ArrayList<Bytecode> bytecodes) {
		translate(new BytecodeForest.Index(blk, 0), freeSlot, forest, bytecodes);
	}

	/**
	 * Translates all bytecodes in a given code block. This may be an outermost
	 * block, or a nested block as part of e.g. a loop.
	 * 
	 * @param parentIndex
	 *            The index of the enclosing bytecode (e.g. loop), or null (if
	 *            outermost)
	 * @param block
	 *            WyIL bytecodes to be translated.
	 * @param freeSlot
	 *            First available free slot
	 * @param bytecodes
	 *            List of bytecodes being accumulated
	 */
	private void translate(BytecodeForest.Index pc, int freeSlot, BytecodeForest forest, ArrayList<Bytecode> bytecodes) {
		BytecodeForest.Block block = forest.get(pc.block());
		for (int i = 0; i != block.size(); ++i) {
			BytecodeForest.Index index = new BytecodeForest.Index(pc.block(), i);
			SourceLocation loc = forest.get(index).attribute(SourceLocation.class);
			if (loc != null) {
				// FIXME: figure our how to get line number!
				// lineNumbers.add(new
				// LineNumberTable.Entry(bytecodes.size(),loc.line));
			}
			freeSlot = translate(index, block.get(i).code(), freeSlot, forest, bytecodes);
		}
	}

	/**
	 * Translate a WyIL bytecode at a given index into one or more JVM
	 * bytecodes. The bytecode index is given to help with debugging (i.e. to
	 * extract attributes associated with the given bytecode).
	 * 
	 * @param pc
	 *            The index of the WyIL bytecode being translated in the
	 *            forest.
	 * @param code
	 *            The WyIL bytecode being translated.
	 * @param freeSlot
	 *            The first available free JVM register slot
	 * @param bytecodes
	 *            The list of bytecodes being accumulated
	 * @return
	 */
	private int translate(BytecodeForest.Index pc, wyil.lang.Bytecode code, int freeSlot, BytecodeForest forest,
			ArrayList<Bytecode> bytecodes) {

		try {
			if (code instanceof Operator) {
				translate(pc, (Operator) code, freeSlot, forest, bytecodes);
			} else if (code instanceof Convert) {
				translate(pc, (Convert) code, freeSlot, forest, bytecodes);
			} else if (code instanceof Const) {
				translate(pc, (Const) code, freeSlot, forest,bytecodes);
			} else if (code instanceof Debug) {
				translate(pc, (Debug) code, freeSlot, forest, bytecodes);
			} else if (code instanceof AssertOrAssume) {
				translate(pc, (AssertOrAssume) code, freeSlot, forest,
						bytecodes);
			} else if (code instanceof Fail) {
				translate(pc, (Fail) code, freeSlot, forest, bytecodes);
			} else if (code instanceof FieldLoad) {
				translate(pc, (FieldLoad) code, freeSlot, forest, bytecodes);
			} else if (code instanceof Quantify) {
				freeSlot = translate(pc, (Quantify) code, freeSlot,
						 forest, bytecodes);
			} else if (code instanceof Goto) {
				translate(pc, (Goto) code, freeSlot, forest, bytecodes);
			} else if (code instanceof If) {
				translateIfGoto(pc, (If) code, freeSlot, forest, bytecodes);
			} else if (code instanceof IfIs) {
				translate(pc, (IfIs) code, freeSlot, forest, bytecodes);
			} else if (code instanceof IndirectInvoke) {
				translate(pc, (IndirectInvoke) code, freeSlot, forest, bytecodes);
			} else if (code instanceof Invoke) {
				translate(pc, (Invoke) code, freeSlot, forest, bytecodes);
			} else if (code instanceof Label) {
				translate(pc, (Label) code, freeSlot, forest, bytecodes);
			} else if (code instanceof Lambda) {
				translate(pc, (Lambda) code, freeSlot, forest, bytecodes);
			} else if (code instanceof Loop) {
				translate(pc, (Loop) code, freeSlot, forest, bytecodes);
			} else if (code instanceof Update) {
				translate(pc, (Update) code, freeSlot, forest, bytecodes);
			} else if (code instanceof Return) {
				translate(pc, (Return) code, freeSlot, forest, bytecodes);
			} else if (code instanceof Switch) {
				translate(pc, (Switch) code, freeSlot, forest, bytecodes);
			} else {
				internalFailure("unknown wyil code encountered (" + code + ")",
						filename,
						forest.get(pc).attribute(SourceLocation.class));
			}

		} catch (Exception ex) {
			internalFailure(ex.getMessage(), filename, ex,
					forest.get(pc).attribute(SourceLocation.class));
		}

		return freeSlot;
	}
	

	private void translate(BytecodeForest.Index index, AssertOrAssume c,
			int freeSlot, BytecodeForest forest, ArrayList<Bytecode> bytecodes) {
		BytecodeForest.Index pc = new BytecodeForest.Index(c.block(), 0);
		if(c instanceof Invariant) {
			// essentially a no-op for now			
		} else {
			translate(pc, freeSlot, forest, bytecodes);
		}
	}

	private void translate(BytecodeForest.Index index, Const c, int freeSlot,
			BytecodeForest forest, ArrayList<Bytecode> bytecodes) {
		Constant constant = c.constant();
		JvmType jt = convertUnderlyingType(constant.type());

		if (constant instanceof Constant.Bool
				|| constant instanceof Constant.Null
				|| constant instanceof Constant.Byte) {
			translate(constant, freeSlot, bytecodes);
		} else {
			int id = JvmValue.get(constant, constants);
			String name = "constant$" + id;
			bytecodes.add(new Bytecode.GetField(owner, name, jt,
					Bytecode.FieldMode.STATIC));
		}
		bytecodes.add(new Bytecode.Store(c.target(), jt));
	}

	private void translate(BytecodeForest.Index index, Convert c, int freeSlot, BytecodeForest forest,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Load(c.operand(0), convertUnderlyingType(c.type(0))));
		addCoercion(c.type(0), c.result(), freeSlot, constants, bytecodes);
		bytecodes.add(new Bytecode.Store(c.target(0), convertUnderlyingType(c.result())));
	}

	private void translate(BytecodeForest.Index index, Update code, int freeSlot, BytecodeForest forest,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Load(code.target(0), convertUnderlyingType(code.type(0))));
		translateUpdate(code.iterator(), code, bytecodes);
		bytecodes.add(new Bytecode.Store(code.target(0), convertUnderlyingType(code.afterType())));
	}

	/**
	 * <p>
	 * Translate the assignment to an LVal by iterating through the chain of
	 * LVals, starting at the outermost and working inwards. For example:
	 * </p>
	 * 
	 * <pre>
	 * xs[i].f = 0
	 * </pre>
	 * <p>
	 * In the above update, we have two LVals: an ArrayLVal and a RecordLVal.
	 * The ArrayLVal is considered the "outermost" and the RecordLVal is
	 * consider the "innermost". To translate this we pass the "current" value
	 * for each level onto the next, which returns the "updated" value that we
	 * then apply.
	 * </p>
	 * 
	 * @param iterator
	 *            --- Update iterator.
	 * @param code
	 *            --- The enclosing bytecode.
	 * @param bytecodes
	 *            --- List of bytecodes to append to.
	 */
	private void translateUpdate(Iterator<LVal> iterator,
			Update code, ArrayList<Bytecode> bytecodes) {
		// At this point, we have not yet reached the "innermost" position.
		// Therefore, we keep recursing down the chain of LVals.
		LVal lv = iterator.next();
		if (lv instanceof ArrayLVal) {
			translateUpdate((ArrayLVal) lv,iterator,code,bytecodes);
		} else if (lv instanceof RecordLVal) {
			translateUpdate((RecordLVal) lv,iterator,code,bytecodes);
		} else {
			translateUpdate((ReferenceLVal) lv,iterator,code,bytecodes);
		}		
	}

	/**
	 * <p>
	 * Translate an assignment to an Array Lval. There are two essential cases
	 * to consider:
	 * </p>
	 * 
	 * <pre>
	 * x[i] = 0
	 * x.f[i] = 0
	 * </pre>
	 * 
	 * <p>
	 * In the first one, we have the array LVal as the "outermost" component of
	 * the assignment. In the second one, it is part of an assignment to a
	 * larger LVal which contains it (in this case, a record LVal).
	 * </p>
	 * 
	 * @param lval
	 * @param iterator
	 * @param code
	 * @param bytecodes
	 */
	private void translateUpdate(ArrayLVal lval, Iterator<LVal> iterator, Update code,
			ArrayList<Bytecode> bytecodes) {
		
		if(iterator.hasNext()) {
			// This is not the innermost case, hence we read out the current
			// value of the location being assigned and pass that forward to the
			// next stage.
			bytecodes.add(new Bytecode.Dup(WHILEYARRAY));
			bytecodes.add(new Bytecode.Load(lval.indexOperand, WHILEYINT));
			JvmType.Function getFunType = new JvmType.Function(JAVA_LANG_OBJECT, WHILEYARRAY, WHILEYINT);
			bytecodes.add(new Bytecode.Invoke(WHILEYARRAY, "internal_get", getFunType, Bytecode.InvokeMode.STATIC));
			addReadConversion(lval.rawType().element(), bytecodes);
			translateUpdate(iterator, code, bytecodes);
			bytecodes.add(new Bytecode.Load(lval.indexOperand, WHILEYINT));
			bytecodes.add(new Bytecode.Swap());
		} else {
			// This is the innermost case, hence we can avoid the unnecessary
			// read of the current value and, instead, just return the rhs value
			// directly.
			bytecodes.add(new Bytecode.Load(lval.indexOperand, WHILEYINT));
			bytecodes.add(new Bytecode.Load(code.result(), convertUnderlyingType(lval.rawType().element())));
			addWriteConversion(code.rhs(), bytecodes);
		}
		JvmType.Function setFunType = new JvmType.Function(WHILEYARRAY, WHILEYARRAY, WHILEYINT, JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYARRAY, "set", setFunType, Bytecode.InvokeMode.STATIC));
	}
	
	private void translateUpdate(RecordLVal lval, Iterator<LVal> iterator, Update code,
			ArrayList<Bytecode> bytecodes) {
		Type.EffectiveRecord type = lval.rawType();

		if(iterator.hasNext()) {
			// This is not the innermost case, hence we read out the current
			// value of the location being assigned and pass that forward to the
			// next stage.
			bytecodes.add(new Bytecode.Dup(WHILEYRECORD));
			bytecodes.add(new Bytecode.LoadConst(lval.field));
			JvmType.Function getFunType = new JvmType.Function(JAVA_LANG_OBJECT, WHILEYRECORD, JAVA_LANG_STRING);
			bytecodes.add(new Bytecode.Invoke(WHILEYRECORD, "internal_get", getFunType, Bytecode.InvokeMode.STATIC));
			addReadConversion(type.field(lval.field), bytecodes);
			translateUpdate(iterator, code, bytecodes);
			bytecodes.add(new Bytecode.LoadConst(lval.field));
			bytecodes.add(new Bytecode.Swap());		
		} else {
			// This is the innermost case, hence we can avoid the unnecessary
			// read of the current value and, instead, just return the rhs value
			// directly.
			bytecodes.add(new Bytecode.LoadConst(lval.field));
			bytecodes.add(new Bytecode.Load(code.result(), convertUnderlyingType(type.field(lval.field))));
			addWriteConversion(type.field(lval.field), bytecodes);
		}
		JvmType.Function putFunType = new JvmType.Function(WHILEYRECORD, WHILEYRECORD, JAVA_LANG_STRING, JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYRECORD, "put", putFunType, Bytecode.InvokeMode.STATIC));
	}

	private void translateUpdate(ReferenceLVal lval, Iterator<LVal> iterator, Update code,
			ArrayList<Bytecode> bytecodes) {
		if(iterator.hasNext()) {
			// This is not the innermost case, hence we read out the current
			// value of the location being assigned and pass that forward to the
			// next stage.
			bytecodes.add(new Bytecode.Dup(WHILEYOBJECT));
			JvmType.Function getFunType = new JvmType.Function(JAVA_LANG_OBJECT);
			bytecodes.add(new Bytecode.Invoke(WHILEYOBJECT, "state", getFunType, Bytecode.InvokeMode.VIRTUAL));
			addReadConversion(lval.rawType().element(), bytecodes);
			translateUpdate(iterator, code, bytecodes);
		} else {
			// This is the innermost case, hence we can avoid the unnecessary
			// read of the current value and, instead, just return the rhs value
			// directly.
			JvmType rhsJvmType = convertUnderlyingType(code.rhs());
			bytecodes.add(new Bytecode.Load(code.result(),rhsJvmType));
		}
		JvmType.Function setFunType = new JvmType.Function(WHILEYOBJECT, JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYOBJECT, "setState", setFunType, Bytecode.InvokeMode.VIRTUAL));
	}

	private void translate(BytecodeForest.Index index, Return c, int freeSlot,
			BytecodeForest forest, ArrayList<Bytecode> bytecodes) {
		JvmType jt = null;
		int[] operands = c.operands();
		 if(operands.length == 1) {
			jt = convertUnderlyingType(c.type(0));
			bytecodes.add(new Bytecode.Load(operands[0], jt));
			bytecodes.add(new Bytecode.Return(jt));
		} else if(operands.length > 1){
			jt = JAVA_LANG_OBJECT_ARRAY;
			encodeOperandArray(c.types(),c.operands(),bytecodes);			
		}
		bytecodes.add(new Bytecode.Return(jt));
	}

	private void translate(BytecodeForest.Index index, Switch c, int freeSlot,
			BytecodeForest forest, ArrayList<Bytecode> bytecodes) {

		ArrayList<jasm.util.Pair<Integer, String>> cases = new ArrayList();
		boolean canUseSwitchBytecode = true;
		for (Pair<Constant, String> p : c.branches()) {
			// first, check whether the switch value is indeed an integer.
			Constant v = (Constant) p.first();
			if (!(v instanceof Constant.Integer)) {
				canUseSwitchBytecode = false;
				break;
			}
			// second, check whether integer value can fit into a Java int
			Constant.Integer vi = (Constant.Integer) v;
			int iv = vi.value().intValue();
			if (!BigInteger.valueOf(iv).equals(vi.value())) {
				canUseSwitchBytecode = false;
				break;
			}
			// ok, we're all good so far
			cases.add(new jasm.util.Pair(iv, p.second()));
		}

		if (canUseSwitchBytecode) {
			JvmType.Function ftype = new JvmType.Function(T_INT);
			bytecodes.add(new Bytecode.Load(c.operand(0), convertUnderlyingType(c.type(0))));
			bytecodes.add(new Bytecode.Invoke(WHILEYINT, "intValue", ftype, Bytecode.InvokeMode.VIRTUAL));
			bytecodes.add(new Bytecode.Switch(c.defaultTarget(), cases));
		} else {
			// ok, in this case we have to fall back to series of the if
			// conditions. Not ideal.
			for (Pair<Constant, String> p : c.branches()) {
				Constant value = p.first();
				String target = p.second();
				translate(value, freeSlot, bytecodes);
				bytecodes.add(new Bytecode.Load(c.operand(0), convertUnderlyingType(c.type(0))));
				JvmType.Function ftype = new JvmType.Function(T_BOOL, JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);
				bytecodes.add(new Bytecode.Invoke(WHILEYUTIL, "equals", ftype, Bytecode.InvokeMode.STATIC));
				bytecodes.add(new Bytecode.If(Bytecode.IfMode.NE, target));

			}
			bytecodes.add(new Bytecode.Goto(c.defaultTarget()));
		}
	}

	private void translateIfGoto(BytecodeForest.Index index, If code, int freeSlot, BytecodeForest forest,
			ArrayList<Bytecode> bytecodes) {
		JvmType jt = convertUnderlyingType(code.type(0));
		bytecodes.add(new Bytecode.Load(code.operand(0), jt));
		JvmType.Function ftype = new JvmType.Function(T_BOOL);
		bytecodes.add(new Bytecode.Invoke(WHILEYBOOL, "value", ftype, Bytecode.InvokeMode.VIRTUAL));
		bytecodes.add(new Bytecode.If(Bytecode.IfMode.NE, code.destination()));
	}

	
	private void translate(BytecodeForest.Index index, IfIs c, int freeSlot,
			BytecodeForest forest, ArrayList<Bytecode> bytecodes) {

		// In this case, we're updating the type of a local variable. To
		// make this work, we must update the JVM type of that slot as well
		// using a checkcast on both branches. The key challenge here lies with
		// the resulting types on the true and false branches of the
		// conditional. These are critical to determining the correct type to
		// cast the variable's contents to. The presence of constrained types
		// complicates this. For example, consider:
		//
		// <pre>
		// type nat is (int n) where n >= 0
		//
		// function f(int|bool|null x) -> bool:
		// if x is nat|bool:
		// ...
		// else:
		// ...
		// </pre>
		//
		// Here, the type of x on the true branch is int|bool, whilst on the
		// false
		// branch it is int|null. To correctly handle this, we need to determine
		// maximal type which is fully consumed by another. In this case, the
		// maximal type fully consumed by nat|bool is bool and, hence, the type
		// on the false branch is int|bool|null - bool == int|null.
		//
		// First, calculate the underlying and maximal consumed types, which
		// we'll need later.
		Type targetType = c.rightOperand();
		Type maximalConsumedType;
		Type underlyingType;
		try {
			maximalConsumedType = expander.getMaximallyConsumedType(targetType);
			underlyingType = expander.getUnderlyingType(targetType);
		} catch (Exception e) {
			internalFailure("error computing maximally consumed type: " + targetType, filename, e);
			return;
		}
		// The false label will determine the destination where the variable
		// will be retyped on the false branch begins.
		String falseLabel = freshLabel();

		// Second, translate the raw type test. This will direct all values
		// matching the underlying type towards the step label. At that point,
		// we need to check whether the necessary constrained (if applicable)
		// are met.
		bytecodes.add(new Bytecode.Load(c.operand(0), convertUnderlyingType(c.type(0))));
		translateTypeTest(falseLabel, underlyingType, constants, bytecodes);
		
		// Third, update the type of the variable on the true branch. This is
		// the intersection of its original type with that of the test to
		// produce the most precise type possible.
		Type typeOnTrueBranch = Type.intersect(c.type(0), underlyingType);
		bytecodes.add(new Bytecode.Load(c.operand(0), convertUnderlyingType(c.type(0))));
		addReadConversion(typeOnTrueBranch, bytecodes);
		bytecodes.add(new Bytecode.Store(c.operand(0), convertUnderlyingType(typeOnTrueBranch)));

		// Fourth handle constrained types by invoking a function which will
		// execute any and all constraints associated with the type. For
		// recursive types, this may result in recursive calls.
		translateInvariantTest(falseLabel, targetType, c.operand(0), freeSlot, constants, bytecodes);
		bytecodes.add(new Bytecode.Goto(c.destination()));
		// Finally, construct false branch and retype the variable on the false
		// branch to ensure it has the most precise type we know at this point.
		bytecodes.add(new Bytecode.Label(falseLabel));
		Type typeOnFalseBranch = Type.intersect(c.type(0), Type.Negation(maximalConsumedType));
		bytecodes.add(new Bytecode.Load(c.operand(0), convertUnderlyingType(c.type(0))));
		addReadConversion(typeOnFalseBranch, bytecodes);
		bytecodes.add(new Bytecode.Store(c.operand(0), convertUnderlyingType(typeOnFalseBranch)));
	}

	// The purpose of this method is to translate a type test. We're testing to
	// see whether what's on the top of the stack (the value) is a subtype of
	// the type being tested. Note, constants must be provided as a parameter
	// since this function may be called from buildCoercion()
	protected void translateTypeTest(String falseTarget, Type test,
			HashMap<JvmConstant, Integer> constants,
			ArrayList<Bytecode> bytecodes) {

		// First, try for the easy cases
		if (test instanceof Type.Null) {
			// Easy case
			bytecodes
					.add(new Bytecode.If(Bytecode.IfMode.NONNULL, falseTarget));
		} else if (test instanceof Type.Bool) {
			bytecodes.add(new Bytecode.InstanceOf(WHILEYBOOL));
			bytecodes.add(new Bytecode.If(Bytecode.IfMode.EQ, falseTarget));
		} else if (test instanceof Type.Int) {
			bytecodes.add(new Bytecode.InstanceOf(WHILEYINT));
			bytecodes.add(new Bytecode.If(Bytecode.IfMode.EQ, falseTarget));
		} else {
			// Fall-back to an external (recursive) check
			Constant constant = new Constant.Type(test);
			int id = JvmValue.get(constant, constants);
			String name = "constant$" + id;
			bytecodes.add(new Bytecode.GetField(owner, name, WHILEYTYPE,
					Bytecode.FieldMode.STATIC));

			JvmType.Function ftype = new JvmType.Function(T_BOOL,
					JAVA_LANG_OBJECT, WHILEYTYPE);
			bytecodes.add(new Bytecode.Invoke(WHILEYUTIL, "instanceOf", ftype,
					Bytecode.InvokeMode.STATIC));
			bytecodes.add(new Bytecode.If(Bytecode.IfMode.EQ, falseTarget));
		}
	}

	private void translateInvariantTest(String falseTarget, Type type, int rootSlot, int freeSlot,
			HashMap<JvmConstant, Integer> constants, ArrayList<Bytecode> bytecodes) {
		//
		JvmType underlyingType = convertUnderlyingType(type);
		//
		if (type instanceof Type.Nominal) {
			Type.Nominal c = (Type.Nominal) type;
			Path.ID mid = c.name().module();
			JvmType.Clazz owner = new JvmType.Clazz(mid.parent().toString().replace('/', '.'), mid.last());
			JvmType.Function fnType = new JvmType.Function(new JvmType.Bool(), convertUnderlyingType(c));
			bytecodes.add(new Bytecode.Load(rootSlot, convertUnderlyingType(type)));
			bytecodes.add(new Bytecode.Invoke(owner, c.name().name() + "$typeof", fnType, Bytecode.InvokeMode.STATIC));
			bytecodes.add(new Bytecode.If(Bytecode.IfMode.EQ, falseTarget));
		} else if (type instanceof Type.Leaf) {
			// Do nout
		} else if (type instanceof Type.Reference) {
			Type.Reference rt = (Type.Reference) type;
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT);
			bytecodes.add(new Bytecode.Load(rootSlot, underlyingType));
			bytecodes.add(new Bytecode.Invoke(WHILEYOBJECT, "state", ftype, Bytecode.InvokeMode.VIRTUAL));
			addReadConversion(rt.element(), bytecodes);
			bytecodes.add(new Bytecode.Store(freeSlot, convertUnderlyingType(rt.element())));
			translateInvariantTest(falseTarget, rt.element(), freeSlot, freeSlot + 1, constants, bytecodes);
		} else if (type instanceof Type.EffectiveArray) {
			Type.EffectiveArray ts = (Type.EffectiveArray) type;
			Triple<String, String, String> loopLabels = translateLoopBegin(bytecodes, rootSlot, freeSlot);
			addReadConversion(ts.element(), bytecodes);
			bytecodes.add(new Bytecode.Store(freeSlot + 1, convertUnderlyingType(ts.element())));
			translateInvariantTest(falseTarget, ts.element(), freeSlot + 1, freeSlot + 2, constants, bytecodes);
			translateLoopEnd(bytecodes, loopLabels);
		} else if (type instanceof Type.Record) {
			Type.Record tt = (Type.Record) type;
			HashMap<String, Type> fields = tt.fields();
			ArrayList<String> fieldNames = new ArrayList<>(fields.keySet());
			Collections.sort(fieldNames);
			for (int i = 0; i != fieldNames.size(); ++i) {
				String field = fieldNames.get(i);
				Type fieldType = fields.get(field);
				JvmType underlyingFieldType = convertUnderlyingType(fieldType);
				bytecodes.add(new Bytecode.Load(rootSlot, underlyingType));
				bytecodes.add(new Bytecode.LoadConst(field));
				JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT, JAVA_LANG_STRING);
				bytecodes.add(new Bytecode.Invoke(WHILEYRECORD, "get", ftype, Bytecode.InvokeMode.VIRTUAL));
				addReadConversion(fieldType, bytecodes);
				bytecodes.add(new Bytecode.Store(freeSlot, underlyingFieldType));
				translateInvariantTest(falseTarget, fieldType, freeSlot, freeSlot + 1, constants, bytecodes);
			}
		} else if (type instanceof Type.FunctionOrMethod) {
			// FIXME: this is clearly a bug. However, it's not completely
			// straightforward to fix, since there is currently no way to get
			// runtime type information about a function or method reference. In
			// principle, this could be encoded in the WyLambda in some way.
		} else if (type instanceof Type.Negation) {
			Type.Reference rt = (Type.Reference) type;
			String trueTarget = freshLabel();
			translateInvariantTest(trueTarget, rt.element(), rootSlot, freeSlot, constants, bytecodes);
			bytecodes.add(new Bytecode.Goto(falseTarget));
			bytecodes.add(new Bytecode.Label(trueTarget));
		} else if (type instanceof Type.Union) {
			Type.Union ut = (Type.Union) type;
			String trueLabel = freshLabel();
			for (Type bound : ut.bounds()) {
				try {
					Type underlyingBound = expander.getUnderlyingType(bound);
					String nextLabel = freshLabel();
					bytecodes.add(new Bytecode.Load(rootSlot, convertUnderlyingType(type)));
					translateTypeTest(nextLabel, underlyingBound, constants, bytecodes);
					bytecodes.add(new Bytecode.Load(rootSlot, convertUnderlyingType(type)));
					addReadConversion(bound, bytecodes);
					bytecodes.add(new Bytecode.Store(freeSlot, convertUnderlyingType(bound)));
					translateInvariantTest(nextLabel, bound, freeSlot, freeSlot + 1, constants, bytecodes);
					bytecodes.add(new Bytecode.Goto(trueLabel));
					bytecodes.add(new Bytecode.Label(nextLabel));
				} catch (ResolveError e) {
					internalFailure(e.getMessage(), filename, e);
				} catch (IOException e) {
					internalFailure(e.getMessage(), filename, e);
				}
			}
			bytecodes.add(new Bytecode.Goto(falseTarget));
			bytecodes.add(new Bytecode.Label(trueLabel));
		} else {
			internalFailure("unknown type encountered: " + type, filename);
		}
	}

	private void translate(BytecodeForest.Index index, Loop c, int freeSlot, BytecodeForest forest,
			ArrayList<Bytecode> bytecodes) {
		// Allocate header label for loop
		String loopHeader = freshLabel();
		bytecodes.add(new Bytecode.Label(loopHeader));
		// Translate body of loop. The cast is required to ensure correct method
		// is called.
		translate(new BytecodeForest.Index(c.block(), 0), freeSlot, forest, bytecodes);
		// Terminate loop by branching back to head of loop
		bytecodes.add(new Bytecode.Goto(loopHeader));
	}

	private int translate(BytecodeForest.Index index, Quantify c, int freeSlot,
			BytecodeForest forest, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Load(c.startOperand(), WHILEYINT));
		bytecodes.add(new Bytecode.Load(c.endOperand(), WHILEYINT));
		JvmType.Function ftype = new JvmType.Function(WHILEYARRAY, WHILEYINT, WHILEYINT);
		bytecodes.add(new Bytecode.Invoke(WHILEYARRAY, "range", ftype, Bytecode.InvokeMode.STATIC));
		ftype = new JvmType.Function(JAVA_UTIL_ITERATOR);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_COLLECTION, "iterator", ftype, Bytecode.InvokeMode.INTERFACE));
		bytecodes.add(new Bytecode.Store(freeSlot, JAVA_UTIL_ITERATOR));
		String loopHeader = freshLabel();
		String loopExit = freshLabel();
		bytecodes.add(new Bytecode.Label(loopHeader));
		ftype = new JvmType.Function(T_BOOL);
		bytecodes.add(new Bytecode.Load(freeSlot, JAVA_UTIL_ITERATOR));
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "hasNext", ftype, Bytecode.InvokeMode.INTERFACE));
		bytecodes.add(new Bytecode.If(Bytecode.IfMode.EQ, loopExit));
		bytecodes.add(new Bytecode.Load(freeSlot, JAVA_UTIL_ITERATOR));
		ftype = new JvmType.Function(JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "next", ftype, Bytecode.InvokeMode.INTERFACE));
		addReadConversion(Type.T_INT, bytecodes);
		bytecodes.add(new Bytecode.Store(c.indexOperand(), convertUnderlyingType(Type.T_INT)));
		// Translate body of loop. The cast is required to ensure correct method
		// is called.
		translate(new BytecodeForest.Index(c.block(), 0), freeSlot + 1, forest, bytecodes);
		// Terminate loop by branching back to head of loop
		bytecodes.add(new Bytecode.Goto(loopHeader));
		bytecodes.add(new Bytecode.Label(loopExit));

		return freeSlot;
	}

	private void translate(BytecodeForest.Index index, Goto c, int freeSlot, BytecodeForest forest,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Goto(c.destination()));
	}

	private void translate(BytecodeForest.Index index, Label c, int freeSlot,
			BytecodeForest forest, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Label(c.label()));
	}

	private void translate(BytecodeForest.Index index, Debug c, int freeSlot,
			BytecodeForest forest, ArrayList<Bytecode> bytecodes) {
		JvmType.Function ftype = new JvmType.Function(T_VOID, WHILEYARRAY);
		bytecodes.add(new Bytecode.Load(c.operand(0), WHILEYARRAY));
		bytecodes.add(new Bytecode.Invoke(WHILEYUTIL, "print", ftype, Bytecode.InvokeMode.STATIC));
	}

	private void translate(BytecodeForest.Index index, Fail c, int freeSlot, BytecodeForest forest, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.New(JAVA_LANG_RUNTIMEEXCEPTION));
		bytecodes.add(new Bytecode.Dup(JAVA_LANG_RUNTIMEEXCEPTION));
		bytecodes.add(new Bytecode.LoadConst("runtime fault encountered"));
		JvmType.Function ftype = new JvmType.Function(T_VOID, JAVA_LANG_STRING);
		bytecodes.add(new Bytecode.Invoke(JAVA_LANG_RUNTIMEEXCEPTION, "<init>", ftype, Bytecode.InvokeMode.SPECIAL));
		bytecodes.add(new Bytecode.Throw());
	}

	private void translate(BytecodeForest.Index index, FieldLoad c, int freeSlot, BytecodeForest forest, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Load(c.operand(0), WHILEYRECORD));
		bytecodes.add(new Bytecode.LoadConst(c.fieldName()));
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT, WHILEYRECORD, JAVA_LANG_STRING);
		bytecodes.add(new Bytecode.Invoke(WHILEYRECORD, "get", ftype, Bytecode.InvokeMode.STATIC));
		addReadConversion(c.fieldType(), bytecodes);
		bytecodes.add(new Bytecode.Store(c.target(0), convertUnderlyingType(c.fieldType())));
	}

	private void translate(BytecodeForest.Index index, Operator c, int freeSlot, BytecodeForest forest,
			ArrayList<Bytecode> bytecodes) {
		Context context = new Context(forest, index, freeSlot, bytecodes);
		generators[c.opcode()].translate(c, context);
	}

	private void translate(BytecodeForest.Index index, Lambda c, int freeSlot, BytecodeForest forest,
			ArrayList<Bytecode> bytecodes) {

		// First, build and register lambda class which calls the given function
		// or method. This class will extend class wyjc.runtime.WyLambda.
		Type.FunctionOrMethod lamType = (Type.FunctionOrMethod) c.type(0); 
		int lambda_id = lambdas.size();
		lambdas.add(buildLambda(c.name(), lamType, lambda_id));

		// Second, create and duplicate new lambda object. This will then stay
		// on the stack (whilst the parameters are constructed) until the
		// object's constructor is called.
		JvmType.Clazz lambdaClassType = new JvmType.Clazz(owner.pkg(), owner
				.lastComponent().first(), Integer.toString(lambda_id));

		bytecodes.add(new Bytecode.New(lambdaClassType));
		bytecodes.add(new Bytecode.Dup(lambdaClassType));

		// Third, construct the parameter for lambda class constructor. In the
		// case that a binding is given for this lambda, then we need to supply
		// this as an argument to the lambda class constructor; otherwise, we
		// just pass null. To do this, we first check whether or not a binding
		// is required.
		int[] c_operands = c.operands();
		
		if (c_operands.length > 0) {
			// Yes, binding is required.				
			int numParams = lamType.params().size();
			int numBlanks = numParams - c_operands.length;
			bytecodes.add(new Bytecode.LoadConst(numParams));
			bytecodes.add(new Bytecode.New(JAVA_LANG_OBJECT_ARRAY));

			for(int i=0;i<numParams;++i) {
				bytecodes.add(new Bytecode.Dup(JAVA_LANG_OBJECT_ARRAY));				
				bytecodes.add(new Bytecode.LoadConst(i));
				if(i < numBlanks) {
					bytecodes.add(new Bytecode.LoadConst(null));
				} else {
					int operand = c.operands()[i-numBlanks];
					Type pt = c.type(0).params().get(i);
					bytecodes.add(new Bytecode.Load(operand, convertUnderlyingType(pt)));
					addWriteConversion(pt, bytecodes);
				}
				bytecodes.add(new Bytecode.ArrayStore(JAVA_LANG_OBJECT_ARRAY));
			}								
		} else {
			// No, binding not required.
			bytecodes.add(new Bytecode.LoadConst(null));
		}

		// Fifth, invoke lambda class constructor
		JvmType.Function ftype = new JvmType.Function(T_VOID, JAVA_LANG_OBJECT_ARRAY);
		bytecodes.add(new Bytecode.Invoke(lambdaClassType, "<init>", ftype, Bytecode.InvokeMode.SPECIAL));

		// Sixth, assign newly created lambda object to target register
		JvmType.Clazz clazz = (JvmType.Clazz) convertUnderlyingType(c.type(0));
		bytecodes.add(new Bytecode.Store(c.target(0), clazz));
	}

	private void translate(BytecodeForest.Index index, Invoke c, int freeSlot, BytecodeForest forest,
			ArrayList<Bytecode> bytecodes) {

		for (int i = 0; i != c.operands().length; ++i) {
			int register = c.operands()[i];
			JvmType parameterType = convertUnderlyingType(c.type(0).params().get(i));
			bytecodes.add(new Bytecode.Load(register, parameterType));
		}

		Path.ID mid = c.name().module();
		String mangled = nameMangle(c.name().name(), c.type(0));
		JvmType.Clazz owner = new JvmType.Clazz(mid.parent().toString().replace('/', '.'), mid.last());
		JvmType.Function type = convertFunType(c.type(0));
		bytecodes.add(new Bytecode.Invoke(owner, mangled, type, Bytecode.InvokeMode.STATIC));

		int[] targets = c.targets();
		List<Type> returns = c.type(0).returns();
		if (targets.length == 0 && !returns.isEmpty()) {
			bytecodes.add(new Bytecode.Pop(convertUnderlyingType(c.type(0).returns().get(0))));
		} else if (targets.length == 1) {
			bytecodes.add(new Bytecode.Store(targets[0], convertUnderlyingType(returns.get(0))));
		} else if (targets.length > 1) {
			// Multiple return values are provided, and these will have been
			// encoded into an object array.
			decodeOperandArray(c.type(0).returns(), targets, bytecodes);
		}		
	}

	private void translate(BytecodeForest.Index index, IndirectInvoke c, int freeSlot, BytecodeForest forest, ArrayList<Bytecode> bytecodes) {
		Type.FunctionOrMethod ft = c.type(0);
		JvmType.Clazz owner = (JvmType.Clazz) convertUnderlyingType(ft);
		bytecodes.add(new Bytecode.Load(c.reference(), convertUnderlyingType(ft)));
		encodeOperandArray(ft.params(), c.parameters(), bytecodes);

		JvmType.Function type = new JvmType.Function(JAVA_LANG_OBJECT, JAVA_LANG_OBJECT_ARRAY);

		bytecodes.add(new Bytecode.Invoke(owner, "call", type, Bytecode.InvokeMode.VIRTUAL));

		int[] targets = c.targets();
		List<Type> returns = ft.returns();
		if (targets.length == 0) {
			// handles the case of an invoke which returns a value that should
			// be discarded.
			bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
		} else if (targets.length == 1) {
			// Only a single return value, which means it is passed directly as
			// a return value rather then being encoded into an object array.
			addReadConversion(returns.get(0), bytecodes);
			bytecodes.add(new Bytecode.Store(targets[0], convertUnderlyingType(returns.get(0))));
		} else {
			// Multiple return values, which must be encoded into an object
			// array.
			internalFailure("multiple returns not supported", filename,
					forest.get(index).attribute(SourceLocation.class));
		}
	}

	private void translate(Constant v, int freeSlot, ArrayList<Bytecode> bytecodes) {
		if (v instanceof Constant.Null) {
			translate((Constant.Null) v, freeSlot, bytecodes);
		} else if (v instanceof Constant.Bool) {
			translate((Constant.Bool) v, freeSlot, bytecodes);
		} else if (v instanceof Constant.Byte) {
			translate((Constant.Byte) v, freeSlot, bytecodes);
		} else if (v instanceof Constant.Integer) {
			translate((Constant.Integer) v, freeSlot, bytecodes);
		} else if (v instanceof Constant.Type) {
			translate((Constant.Type) v, freeSlot, bytecodes);
		} else if (v instanceof Constant.Array) {
			translate((Constant.Array) v, freeSlot, lambdas, bytecodes);
		} else if (v instanceof Constant.Record) {
			translate((Constant.Record) v, freeSlot, lambdas, bytecodes);
		} else if (v instanceof Constant.Lambda) {
			translate((Constant.Lambda) v, freeSlot, lambdas, bytecodes);
		} else {
			throw new IllegalArgumentException("unknown value encountered:" + v);
		}
	}

	protected void translate(Constant.Null e, int freeSlot, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.LoadConst(null));
	}

	protected void translate(Constant.Bool e, int freeSlot, ArrayList<Bytecode> bytecodes) {
		if (e.value()) {
			bytecodes.add(new Bytecode.LoadConst(1));
		} else {
			bytecodes.add(new Bytecode.LoadConst(0));
		}
		JvmType.Function ftype = new JvmType.Function(WHILEYBOOL, T_BOOL);
		bytecodes.add(new Bytecode.Invoke(WHILEYBOOL, "valueOf", ftype,
				Bytecode.InvokeMode.STATIC));
	}

	protected void translate(Constant.Type e, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		JavaIdentifierOutputStream jout = new JavaIdentifierOutputStream();
		BinaryOutputStream bout = new BinaryOutputStream(jout);
		Type.BinaryWriter writer = new Type.BinaryWriter(bout);
		try {
			writer.write(e.value());
			writer.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}

		bytecodes.add(new Bytecode.LoadConst(jout.toString()));
		JvmType.Function ftype = new JvmType.Function(WHILEYTYPE, JAVA_LANG_STRING);
		bytecodes.add(new Bytecode.Invoke(WHILEYTYPE, "valueOf", ftype, Bytecode.InvokeMode.STATIC));
	}

	protected void translate(Constant.Byte e, int freeSlot, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.LoadConst(e.value()));
		JvmType.Function ftype = new JvmType.Function(WHILEYBYTE, T_BYTE);
		bytecodes.add(new Bytecode.Invoke(WHILEYBYTE, "valueOf", ftype, Bytecode.InvokeMode.STATIC));
	}

	protected void translate(Constant.Integer e, int freeSlot, ArrayList<Bytecode> bytecodes) {
		BigInteger num = e.value();

		if (num.bitLength() < 32) {
			bytecodes.add(new Bytecode.LoadConst(num.intValue()));
			bytecodes.add(new Bytecode.Conversion(T_INT, T_LONG));
			JvmType.Function ftype = new JvmType.Function(WHILEYINT, T_LONG);
			bytecodes.add(new Bytecode.Invoke(WHILEYINT, "valueOf", ftype, Bytecode.InvokeMode.STATIC));
		} else if (num.bitLength() < 64) {
			bytecodes.add(new Bytecode.LoadConst(num.longValue()));
			JvmType.Function ftype = new JvmType.Function(WHILEYINT, T_LONG);
			bytecodes.add(new Bytecode.Invoke(WHILEYINT, "valueOf", ftype, Bytecode.InvokeMode.STATIC));
		} else {
			// in this context, we need to use a byte array to construct the
			// integer object.
			byte[] bytes = num.toByteArray();
			JvmType.Array bat = new JvmType.Array(JvmTypes.T_BYTE);

			bytecodes.add(new Bytecode.New(WHILEYINT));
			bytecodes.add(new Bytecode.Dup(WHILEYINT));
			bytecodes.add(new Bytecode.LoadConst(bytes.length));
			bytecodes.add(new Bytecode.New(bat));
			for (int i = 0; i != bytes.length; ++i) {
				bytecodes.add(new Bytecode.Dup(bat));
				bytecodes.add(new Bytecode.LoadConst(i));
				bytecodes.add(new Bytecode.LoadConst(bytes[i]));
				bytecodes.add(new Bytecode.ArrayStore(bat));
			}

			JvmType.Function ftype = new JvmType.Function(T_VOID, bat);
			bytecodes.add(new Bytecode.Invoke(WHILEYINT, "<init>", ftype, Bytecode.InvokeMode.SPECIAL));
		}

	}

	protected void translate(Constant.Array lv, int freeSlot, ArrayList<ClassFile> lambdas,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.New(WHILEYARRAY));
		bytecodes.add(new Bytecode.Dup(WHILEYARRAY));
		bytecodes.add(new Bytecode.LoadConst(lv.values().size()));
		JvmType.Function ftype = new JvmType.Function(T_VOID, T_INT);
		bytecodes.add(new Bytecode.Invoke(WHILEYARRAY, "<init>", ftype, Bytecode.InvokeMode.SPECIAL));

		ftype = new JvmType.Function(T_BOOL, JAVA_LANG_OBJECT);
		for (Constant e : lv.values()) {
			bytecodes.add(new Bytecode.Dup(WHILEYARRAY));
			translate(e, freeSlot, bytecodes);
			addWriteConversion(e.type(), bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYARRAY, "add", ftype, Bytecode.InvokeMode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(T_BOOL));
		}
	}

	protected void translate(Constant.Record expr, int freeSlot, ArrayList<ClassFile> lambdas,
			ArrayList<Bytecode> bytecodes) {
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT, JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);
		construct(WHILEYRECORD, freeSlot, bytecodes);
		for (Map.Entry<String, Constant> e : expr.values().entrySet()) {
			Type et = e.getValue().type();
			bytecodes.add(new Bytecode.Dup(WHILEYRECORD));
			bytecodes.add(new Bytecode.LoadConst(e.getKey()));
			translate(e.getValue(), freeSlot, bytecodes);
			addWriteConversion(et, bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYRECORD, "put", ftype, Bytecode.InvokeMode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
		}
	}

	protected void translate(Constant.Lambda c, int freeSlot, ArrayList<ClassFile> lambdas,
			ArrayList<Bytecode> bytecodes) {

		// First, build and register lambda class which calls the given function
		// or method. This class will extend class wyjc.runtime.WyLambda.
		int lambda_id = lambdas.size();
		lambdas.add(buildLambda(c.name(), c.type(), lambda_id));

		// Second, create and duplicate new lambda object. This will then stay
		// on the stack (whilst the parameters are constructed) until the
		// object's constructor is called.
		JvmType.Clazz lambdaClassType = new JvmType.Clazz(owner.pkg(), owner.lastComponent().first(),
				Integer.toString(lambda_id));

		bytecodes.add(new Bytecode.New(lambdaClassType));
		bytecodes.add(new Bytecode.Dup(lambdaClassType));

		// Third, invoke lambda class constructor
		JvmType.Function ftype = new JvmType.Function(T_VOID, JAVA_LANG_OBJECT_ARRAY);
		bytecodes.add(new Bytecode.LoadConst(null));
		bytecodes.add(new Bytecode.Invoke(lambdaClassType, "<init>", ftype, Bytecode.InvokeMode.SPECIAL));
	}

	/**
	 * Construct a class which implements a lambda expression. This must be a
	 * subtype of wyjc.runtime.WyLambda and must call the given function, whilst
	 * decoding and passing through the appropriate parameters.
	 *
	 * @param name
	 *            Name of function or method which this lambda should invoke.
	 * @param type
	 *            Type of function or method which this lambda should invoke.
	 * @return
	 */
	protected ClassFile buildLambda(NameID name, Type.FunctionOrMethod type, int id) {
		// === (1) Determine the fully qualified type of the lambda class ===

		// start with fully qualified type of this class.
		JvmType.Clazz lambdaClassType = new JvmType.Clazz(owner.pkg(), owner.lastComponent().first(),
				Integer.toString(id));

		// === (2) Construct an empty class ===
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.ACC_PUBLIC);
		modifiers.add(Modifier.ACC_FINAL);
		ClassFile cf = new ClassFile(CLASS_VERSION, lambdaClassType, WHILEYLAMBDA, new ArrayList<JvmType.Clazz>(),
				modifiers);

		// === (3) Add constructor ===
		modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.ACC_PUBLIC);
		JvmType.Function constructorType = new JvmType.Function(JvmTypes.T_VOID, JAVA_LANG_OBJECT_ARRAY);
		// Create constructor method
		ClassFile.Method constructor = new ClassFile.Method("<init>", constructorType, modifiers);
		cf.methods().add(constructor);
		// Create body of constructor
		ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
		bytecodes.add(new Bytecode.Load(0, lambdaClassType));
		bytecodes.add(new Bytecode.Load(1, JAVA_LANG_OBJECT_ARRAY));
		bytecodes.add(new Bytecode.Invoke(WHILEYLAMBDA, "<init>", constructorType, Bytecode.InvokeMode.SPECIAL));
		bytecodes.add(new Bytecode.Return(null));
		// Add code attribute to constructor
		jasm.attributes.Code code = new jasm.attributes.Code(bytecodes, new ArrayList<Handler>(), constructor);
		constructor.attributes().add(code);

		// === (4) Add implementation of WyLambda.call(Object[]) ===
		modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.ACC_PUBLIC);
		modifiers.add(Modifier.ACC_FINAL);
		JvmType.Function callFnType = new JvmType.Function(JvmTypes.JAVA_LANG_OBJECT, JAVA_LANG_OBJECT_ARRAY);
		// Create constructor method
		ClassFile.Method callFn = new ClassFile.Method("call", callFnType, modifiers);
		cf.methods().add(callFn);
		// Create body of call method
		bytecodes = new ArrayList<Bytecode>();
		// Call WyFunction.bindParameters()
		bytecodes.add(new Bytecode.Load(0, lambdaClassType));
		bytecodes.add(new Bytecode.Load(1, JAVA_LANG_OBJECT_ARRAY));
		bytecodes.add(new Bytecode.Invoke(WHILEYLAMBDA, "bindParameters",
				new JvmType.Function(JAVA_LANG_OBJECT_ARRAY, JAVA_LANG_OBJECT_ARRAY), Bytecode.InvokeMode.VIRTUAL));
		bytecodes.add(new Bytecode.Store(1, JAVA_LANG_OBJECT_ARRAY));
		// Load parameters onto stack
		List<Type> type_params = type.params();
		for (int i = 0; i != type_params.size(); ++i) {
			bytecodes.add(new Bytecode.Load(1, JAVA_LANG_OBJECT_ARRAY));
			bytecodes.add(new Bytecode.LoadConst(i));
			bytecodes.add(new Bytecode.ArrayLoad(JAVA_LANG_OBJECT_ARRAY));
			addReadConversion(type_params.get(i), bytecodes);
		}

		Path.ID mid = name.module();
		String mangled = nameMangle(name.name(), type);
		JvmType.Clazz owner = new JvmType.Clazz(mid.parent().toString().replace('/', '.'), mid.last());
		JvmType.Function fnType = convertFunType(type);
		bytecodes.add(new Bytecode.Invoke(owner, mangled, fnType, Bytecode.InvokeMode.STATIC));
		if (type.returns().isEmpty()) {
			// Called function doesn't return anything, but we have to.
			// Therefore, push on dummy null value.
			bytecodes.add(new Bytecode.LoadConst(null));
		} else {
			addWriteConversion(type.returns().get(0), bytecodes);
		}

		bytecodes.add(new Bytecode.Return(JAVA_LANG_OBJECT));

		// Add code attribute to call method
		code = new jasm.attributes.Code(bytecodes, new ArrayList<Handler>(), callFn);
		callFn.attributes().add(code);

		// Done
		return cf;
	}

	protected void addCoercion(Type from, Type to, int freeSlot, HashMap<JvmConstant, Integer> constants,
			ArrayList<Bytecode> bytecodes) {

		// First, deal with coercions which require a change of representation
		// when going into a union. For example, bool must => Boolean.
		if (!(to instanceof Type.Bool) && from instanceof Type.Bool) {
			// this is either going into a union type, or the any type
			buildCoercion((Type.Bool) from, to, freeSlot, bytecodes);
		} else if (from == Type.T_BYTE) {
			buildCoercion((Type.Byte) from, to, freeSlot, bytecodes);
		} else if (Type.intersect(from, to).equals(from)) {
			// do nothing!
			// (note, need to check this after primitive types to avoid risk of
			// missing coercion to any)
		} else if (from == Type.T_INT) {
			// do nothing!
		} else {
			// ok, it's a harder case so we use an explicit coercion function
			int id = JvmCoercion.get(from, to, constants);
			String name = "coercion$" + id;
			JvmType.Function ft = new JvmType.Function(convertUnderlyingType(to), convertUnderlyingType(from));
			bytecodes.add(new Bytecode.Invoke(owner, name, ft, Bytecode.InvokeMode.STATIC));
		}
	}

	private void buildCoercion(Type.Bool fromType, Type toType, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		// done deal!
	}

	private void buildCoercion(Type.Byte fromType, Type toType, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_BYTE, T_BYTE);
		bytecodes.add(new Bytecode.Invoke(JAVA_LANG_BYTE, "valueOf", ftype,
				Bytecode.InvokeMode.STATIC));
		// done deal!
	}

	/**
	 * The build coercion method constructs a static final private method which
	 * accepts a value of type "from", and coerces it into a value of type "to".
	 *
	 * @param to
	 * @param from
	 *
	 */
	protected void buildCoercion(Type from, Type to, int id,
			HashMap<JvmConstant, Integer> constants, ClassFile cf) {
		ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();

		int freeSlot = 1;
		bytecodes.add(new Bytecode.Load(0, convertUnderlyingType(from)));
		buildCoercion(from, to, freeSlot, constants, bytecodes);
		bytecodes.add(new Bytecode.Return(convertUnderlyingType(to)));

		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.ACC_PRIVATE);
		modifiers.add(Modifier.ACC_STATIC);
		modifiers.add(Modifier.ACC_SYNTHETIC);
		JvmType.Function ftype = new JvmType.Function(
				convertUnderlyingType(to), convertUnderlyingType(from));
		String name = "coercion$" + id;
		ClassFile.Method method = new ClassFile.Method(name, ftype, modifiers);
		cf.methods().add(method);
		jasm.attributes.Code code = new jasm.attributes.Code(bytecodes,
				new ArrayList(), method);
		method.attributes().add(code);
	}

	protected void buildCoercion(Type from, Type to, int freeSlot,
			HashMap<JvmConstant, Integer> constants,
			ArrayList<Bytecode> bytecodes) {

		// Second, case analysis on the various kinds of coercion
		if (from instanceof Type.Reference
				&& to instanceof Type.Reference) {
			// TODO
		} else if (from instanceof Type.Array && to instanceof Type.Array) {
			buildCoercion((Type.Array) from, (Type.Array) to, freeSlot,
					constants, bytecodes);
		} else if (to instanceof Type.Record && from instanceof Type.Record) {
			buildCoercion((Type.Record) from, (Type.Record) to, freeSlot,
					constants, bytecodes);
		} else if (to instanceof Type.Function && from instanceof Type.Function) {
			// TODO
		} else if (from instanceof Type.Negation || to instanceof Type.Negation) {
			// no need to do anything, since convertType on a negation returns
			// java/lang/Object
		} else if (from instanceof Type.Union) {
			buildCoercion((Type.Union) from, to, freeSlot, constants, bytecodes);
		} else if (to instanceof Type.Union) {
			buildCoercion(from, (Type.Union) to, freeSlot, constants, bytecodes);
		} else {
			throw new RuntimeException("invalid coercion encountered: " + from
					+ " => " + to);
		}
	}

	protected void buildCoercion(Type.Array fromType, Type.Array toType,
			int freeSlot, HashMap<JvmConstant, Integer> constants,
			ArrayList<Bytecode> bytecodes) {

		if (fromType.element() == Type.T_VOID) {
			// nothing to do, in this particular case
			return;
		}

		// The following piece of code implements a java for-each loop which
		// iterates every element of the input collection, and recursively
		// converts it before loading it back onto a new WhileyList.

		String loopLabel = freshLabel();
		String exitLabel = freshLabel();
		int iter = freeSlot++;
		int tmp = freeSlot++;
		JvmType.Function ftype = new JvmType.Function(JAVA_UTIL_ITERATOR);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_COLLECTION, "iterator",
				ftype, Bytecode.InvokeMode.INTERFACE));
		bytecodes.add(new Bytecode.Store(iter, JAVA_UTIL_ITERATOR));
		construct(WHILEYARRAY, freeSlot, bytecodes);
		bytecodes.add(new Bytecode.Store(tmp, WHILEYARRAY));
		bytecodes.add(new Bytecode.Label(loopLabel));
		ftype = new JvmType.Function(T_BOOL);
		bytecodes.add(new Bytecode.Load(iter, JAVA_UTIL_ITERATOR));
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "hasNext", ftype,
				Bytecode.InvokeMode.INTERFACE));
		bytecodes.add(new Bytecode.If(Bytecode.IfMode.EQ, exitLabel));
		bytecodes.add(new Bytecode.Load(tmp, WHILEYARRAY));
		bytecodes.add(new Bytecode.Load(iter, JAVA_UTIL_ITERATOR));
		ftype = new JvmType.Function(JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "next", ftype,
				Bytecode.InvokeMode.INTERFACE));
		addReadConversion(fromType.element(), bytecodes);
		addCoercion(fromType.element(), toType.element(), freeSlot, constants,
				bytecodes);
		ftype = new JvmType.Function(T_BOOL, JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYARRAY, "add", ftype,
				Bytecode.InvokeMode.VIRTUAL));
		bytecodes.add(new Bytecode.Pop(T_BOOL));
		bytecodes.add(new Bytecode.Goto(loopLabel));
		bytecodes.add(new Bytecode.Label(exitLabel));
		bytecodes.add(new Bytecode.Load(tmp, WHILEYARRAY));
	}

	private void buildCoercion(Type.Record fromType, Type.Record toType,
			int freeSlot, HashMap<JvmConstant, Integer> constants,
			ArrayList<Bytecode> bytecodes) {
		int oldSlot = freeSlot++;
		int newSlot = freeSlot++;
		bytecodes.add(new Bytecode.Store(oldSlot, WHILEYRECORD));
		construct(WHILEYRECORD, freeSlot, bytecodes);
		bytecodes.add(new Bytecode.Store(newSlot, WHILEYRECORD));
		Map<String, Type> toFields = toType.fields();
		Map<String, Type> fromFields = fromType.fields();
		for (String key : toFields.keySet()) {
			Type to = toFields.get(key);
			Type from = fromFields.get(key);
			bytecodes.add(new Bytecode.Load(newSlot, WHILEYRECORD));
			bytecodes.add(new Bytecode.LoadConst(key));
			bytecodes.add(new Bytecode.Load(oldSlot, WHILEYRECORD));
			bytecodes.add(new Bytecode.LoadConst(key));
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
					JAVA_LANG_OBJECT);
			bytecodes.add(new Bytecode.Invoke(WHILEYRECORD, "get", ftype,
					Bytecode.InvokeMode.VIRTUAL));
			// TODO: in cases when the read conversion is a no-op, we can do
			// better here.
			addReadConversion(from, bytecodes);
			addCoercion(from, to, freeSlot, constants, bytecodes);
			addWriteConversion(from, bytecodes);
			ftype = new JvmType.Function(JAVA_LANG_OBJECT, JAVA_LANG_OBJECT,
					JAVA_LANG_OBJECT);
			bytecodes.add(new Bytecode.Invoke(WHILEYRECORD, "put", ftype,
					Bytecode.InvokeMode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
		}
		bytecodes.add(new Bytecode.Load(newSlot, WHILEYRECORD));
	}

	private void buildCoercion(Type.Union from, Type to, int freeSlot,
			HashMap<JvmConstant, Integer> constants,
			ArrayList<Bytecode> bytecodes) {

		String exitLabel = freshLabel();
		List<Type> fromBounds = new ArrayList<Type>(from.bounds());

		// For each bound in the union, go through and check whether the given
		// value (which is currently on the stack) is an instance of that bound.
		// When we find the right one, then we recursively build a coercion from
		// that type to the target type. In general, this could certainly be
		// more efficient.
		for (int i = 0; i != fromBounds.size(); ++i) {
			Type bound = fromBounds.get(i);
			if ((i + 1) == fromBounds.size()) {
				// In the last case, we just assume that the value matches
				// (since otherwise something very strange has happened).
				addReadConversion(bound, bytecodes);
				addCoercion(bound, to, freeSlot, constants, bytecodes);
				bytecodes.add(new Bytecode.Goto(exitLabel));
			} else {
				// There is at least one more bound after this. In such case, we
				// check whether the given value matches this bound. If so, we
				// recursively build a coercion for that to the target type. If
				// not, we branch to the next bound
				String nextLabel = freshLabel();
				bytecodes.add(new Bytecode.Dup(convertUnderlyingType(from)));
				translateTypeTest(nextLabel, bound, constants, bytecodes);
				addReadConversion(bound, bytecodes);
				addCoercion(bound, to, freeSlot, constants, bytecodes);
				bytecodes.add(new Bytecode.Goto(exitLabel));
				bytecodes.add(new Bytecode.Label(nextLabel));
			}
		}

		bytecodes.add(new Bytecode.Label(exitLabel));
	}

	private void buildCoercion(Type from, Type.Union to, int freeSlot,
			HashMap<JvmConstant, Integer> constants,
			ArrayList<Bytecode> bytecodes) {
		Type.Union t2 = (Type.Union) to;

		// First, check for identical type (i.e. no coercion necessary)
		for (Type b : t2.bounds()) {
			if (from.equals(b)) {
				// nothing to do
				return;
			}
		}

		// Second, check for single non-coercive match
		for (Type b : t2.bounds()) {
			if (Type.isSubtype(b, from)) {
				buildCoercion(from, b, freeSlot, constants, bytecodes);
				return;
			}
		}

		// Third, test for single coercive match
		for (Type b : t2.bounds()) {
			if (Type.isExplicitCoerciveSubtype(b, from)) {
				buildCoercion(from, b, freeSlot, constants, bytecodes);
				return;
			}
		}

		// I don't think we should be able to get here!
	}

	/**
	 * Construct generic code for iterating over a collection (e.g. a Whiley
	 * List or Set). This code will not leave anything on the stack and will
	 * store the iterator variable in a given slot. This means that things can
	 * be passed on the stack from before the loop into the loop body.
	 * 
	 * @param bytecodes
	 *            The list of bytecodes onto which the loop code should be added
	 * @param freeSlot
	 *            The variable slot into which the iterator variable should be
	 *            stored.
	 * @param exitLabel
	 *            The label which will represents after the end of the loop.
	 */
	private Triple<String, String, String> translateLoopBegin(
			ArrayList<Bytecode> bytecodes, int sourceSlot, int freeSlot) {
		String loopHeader = freshLabel();
		String loopFooter = freshLabel();
		String loopExit = freshLabel();

		// First, call Collection.iterator() on the source collection and write
		// it into the free slot.
		bytecodes.add(new Bytecode.Load(sourceSlot, JAVA_LANG_ITERABLE));
		bytecodes.add(new Bytecode.Invoke(JAVA_LANG_ITERABLE, "iterator",
				new JvmType.Function(JAVA_UTIL_ITERATOR),Bytecode.InvokeMode.INTERFACE));
		bytecodes.add(new Bytecode.Store(freeSlot, JAVA_UTIL_ITERATOR));

		// Second, construct the loop header, which consists of the test to
		// check whether or not there are any elements left in the collection to
		// visit.
		bytecodes.add(new Bytecode.Label(loopHeader));
		bytecodes.add(new Bytecode.Load(freeSlot, JAVA_UTIL_ITERATOR));
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "hasNext",
				new JvmType.Function(T_BOOL), Bytecode.InvokeMode.INTERFACE));
		bytecodes.add(new Bytecode.If(Bytecode.IfMode.EQ, loopExit));

		// Finally, get the current element out of the iterator by invoking
		// Iterator.next();
		bytecodes.add(new Bytecode.Load(freeSlot, JAVA_UTIL_ITERATOR));
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "next",
				new JvmType.Function(JAVA_LANG_OBJECT),
				Bytecode.InvokeMode.INTERFACE));

		// Done
		return new Triple<>(loopHeader, loopFooter, loopExit);
	}

	private void translateLoopEnd(ArrayList<Bytecode> bytecodes,
			Triple<String, String, String> labels) {
		bytecodes.add(new Bytecode.Label(labels.second()));
		bytecodes.add(new Bytecode.Goto(labels.first()));
		bytecodes.add(new Bytecode.Label(labels.third()));
	}

	private void encodeOperandArray(List<Type> types, int[] operands, ArrayList<Bytecode> bytecodes) {
		encodeOperandArray(types.toArray(new Type[types.size()]),operands,bytecodes);
	}
	
	/**
	 * Create an Object[] array from a list of operands. This involves
	 * appropriately coercing primitives as necessary.
	 * 
	 * @param types
	 * @param operands
	 * @param bytecodes
	 */
	private void encodeOperandArray(Type[] types, int[] operands, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.LoadConst(operands.length));
		bytecodes.add(new Bytecode.New(JAVA_LANG_OBJECT_ARRAY));
		for (int i = 0; i != operands.length; ++i) {
			int register = operands[i];
			Type type = types[i];
			JvmType jvmType = convertUnderlyingType(type);
			bytecodes.add(new Bytecode.Dup(JAVA_LANG_OBJECT_ARRAY));
			bytecodes.add(new Bytecode.LoadConst(i));
			bytecodes.add(new Bytecode.Load(register, jvmType));
			addWriteConversion(type, bytecodes);
			bytecodes.add(new Bytecode.ArrayStore(JAVA_LANG_OBJECT_ARRAY));
		}
	}
	
	private void decodeOperandArray(List<Type> types, int[] targets, ArrayList<Bytecode> bytecodes) {
		for (int i = 0; i != targets.length; ++i) {
			int register = targets[i];
			Type type = types.get(i);
			bytecodes.add(new Bytecode.Dup(JAVA_LANG_OBJECT_ARRAY));
			bytecodes.add(new Bytecode.LoadConst(i));
			bytecodes.add(new Bytecode.ArrayLoad(JAVA_LANG_OBJECT_ARRAY));			
			addReadConversion(type, bytecodes);
			JvmType jvmType = convertUnderlyingType(type);
			bytecodes.add(new Bytecode.Store(register, jvmType));
		}
		bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT_ARRAY));
	}
		
	/**
	 * The read conversion is necessary in situations where we're reading a
	 * value from a collection (e.g. WhileyList, WhileySet, etc) and then
	 * putting it on the stack. In such case, we need to convert boolean values
	 * from Boolean objects to bool primitives.
	 */
	private void addReadConversion(Type et, ArrayList<Bytecode> bytecodes) {
		// This doesn't do anything extrac since there are currently no data
		// types implemented as primitives.
		addCheckCast(convertUnderlyingType(et), bytecodes);
	}

	/**
	 * The write conversion is necessary in situations where we're write a value
	 * from the stack into a collection (e.g. WhileyList, WhileySet, etc). In
	 * such case, we need to convert boolean values from bool primitives to
	 * Boolean objects.
	 */
	private void addWriteConversion(Type et, ArrayList<Bytecode> bytecodes) {
		// This currently does nothing since there are currently no data types
		// implemented as primitives.
	}

	private void addCheckCast(JvmType type, ArrayList<Bytecode> bytecodes) {
		// The following can happen in situations where a variable has type
		// void. In principle, we could remove this as obvious dead-code, but
		// for now I just avoid it.
		if (type instanceof JvmType.Void) {
			return;
		} else if (!type.equals(JAVA_LANG_OBJECT)) {
			// pointless to add a cast for object
			bytecodes.add(new Bytecode.CheckCast(type));
		}
	}

	/**
	 * The construct method provides a generic way to construct a Java object.
	 *
	 * @param owner
	 * @param freeSlot
	 * @param bytecodes
	 * @param params
	 */
	private void construct(JvmType.Clazz owner, int freeSlot, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.New(owner));
		bytecodes.add(new Bytecode.Dup(owner));
		ArrayList<JvmType> paramTypes = new ArrayList<JvmType>();
		JvmType.Function ftype = new JvmType.Function(T_VOID, paramTypes);
		bytecodes.add(new Bytecode.Invoke(owner, "<init>", ftype, Bytecode.InvokeMode.SPECIAL));
	}

	public final static Type WHILEY_SYSTEM_T = Type.Nominal(new NameID(Trie
			.fromString("whiley/lang/System"), "Console"));

	public final static JvmType.Clazz WHILEYUTIL = new JvmType.Clazz(
			"wyjc.runtime", "Util");
	public final static JvmType.Clazz WHILEYARRAY = new JvmType.Clazz(
			"wyjc.runtime", "WyArray");
	public final static JvmType.Clazz WHILEYTYPE = new JvmType.Clazz(
			"wyjc.runtime", "WyType");
	public final static JvmType.Clazz WHILEYRECORD = new JvmType.Clazz(
			"wyjc.runtime", "WyRecord");
	public final static JvmType.Clazz WHILEYOBJECT = new JvmType.Clazz(
			"wyjc.runtime", "WyObject");
	public final static JvmType.Clazz WHILEYBOOL = new JvmType.Clazz(
			"wyjc.runtime", "WyBool");
	public final static JvmType.Clazz WHILEYBYTE = new JvmType.Clazz(
			"wyjc.runtime", "WyByte");
	public final static JvmType.Clazz WHILEYINT = new JvmType.Clazz(
			"java.math", "BigInteger");
	public final static JvmType.Clazz WHILEYLAMBDA = new JvmType.Clazz(
			"wyjc.runtime", "WyLambda");

	private static final JvmType.Clazz JAVA_LANG_CHARACTER = new JvmType.Clazz(
			"java.lang", "Character");
	private static final JvmType.Clazz JAVA_LANG_SYSTEM = new JvmType.Clazz(
			"java.lang", "System");
	private static final JvmType.Clazz JAVA_LANG_ITERABLE = new JvmType.Clazz(
			"java.lang", "Iterable");
	private static final JvmType.Array JAVA_LANG_OBJECT_ARRAY = new JvmType.Array(
			JAVA_LANG_OBJECT);
	private static final JvmType.Clazz JAVA_UTIL_LIST = new JvmType.Clazz(
			"java.util", "List");
	private static final JvmType.Clazz JAVA_UTIL_SET = new JvmType.Clazz(
			"java.util", "Set");
	// private static final JvmType.Clazz JAVA_LANG_REFLECT_METHOD = new
	// JvmType.Clazz("java.lang.reflect","Method");
	private static final JvmType.Clazz JAVA_IO_PRINTSTREAM = new JvmType.Clazz(
			"java.io", "PrintStream");
	private static final JvmType.Clazz JAVA_LANG_RUNTIMEEXCEPTION = new JvmType.Clazz(
			"java.lang", "RuntimeException");
	private static final JvmType.Clazz JAVA_LANG_ASSERTIONERROR = new JvmType.Clazz(
			"java.lang", "AssertionError");
	private static final JvmType.Clazz JAVA_UTIL_COLLECTION = new JvmType.Clazz(
			"java.util", "Collection");

	private JvmType.Function convertFunType(Type.FunctionOrMethod ft) {
		ArrayList<JvmType> paramTypes = new ArrayList<JvmType>();
		for (Type pt : ft.params()) {
			paramTypes.add(convertUnderlyingType(pt));
		}
		JvmType rt;
		switch(ft.returns().size()) {
		case 0:
			rt = T_VOID;
			break;
		case 1:
			// Single return value
			rt = convertUnderlyingType(ft.returns().get(0));
			break;
		default:
			// Multiple return value
			rt = JAVA_LANG_OBJECT_ARRAY;
		}
		
		return new JvmType.Function(rt, paramTypes);
	}

	/**
	 * Convert a given WyIL type into a JVM type by expanding it into its
	 * underlying form as far as possible.
	 * 
	 * @param t
	 *            the type to be expanded.
	 * @return
	 */
	private JvmType convertUnderlyingType(Type t) {
		if (t == Type.T_VOID) {
			return T_VOID;
		} else if (t == Type.T_ANY) {
			return JAVA_LANG_OBJECT;
		} else if (t == Type.T_NULL) {
			return JAVA_LANG_OBJECT;
		} else if (t instanceof Type.Bool) {
			return WHILEYBOOL;
		} else if (t instanceof Type.Byte) {
			return WHILEYBYTE;
		} else if (t instanceof Type.Int) {
			return WHILEYINT;
		} else if (t instanceof Type.Meta) {
			return WHILEYTYPE;
		} else if (t instanceof Type.EffectiveArray) {
			return WHILEYARRAY;
		} else if (t instanceof Type.EffectiveRecord) {
			return WHILEYRECORD;
		} else if (t instanceof Type.Reference) {
			return WHILEYOBJECT;
		} else if (t instanceof Type.Negation) {
			// can we do any better?
			return JAVA_LANG_OBJECT;
		} else if (t instanceof Type.Union) {
			// What we need to do here, is determine whether or not a common
			// specific type exists to all bounds. For example, if all bounds
			// are records then the true result is WHILEYRECORD. However, in the
			// general case we just return object.
			Type.Union ut = (Type.Union) t;
			JvmType result = null;
			for(Type bound : ut.bounds()) {
				JvmType r = convertUnderlyingType(bound);
				if(result == null) {
					result = r; 
				} else if (!r.equals(result)) {
					result = JAVA_LANG_OBJECT;
				}
			}
			return result;
		} else if (t instanceof Type.Meta) {
			return JAVA_LANG_OBJECT;
		} else if (t instanceof Type.FunctionOrMethod) {
			return WHILEYLAMBDA;
		} else if (t instanceof Type.Nominal) {
			try {
				Type expanded = expander.getUnderlyingType(t);
				return convertUnderlyingType(expanded);
			} catch (Exception e) {
				internalFailure("error expanding type: " + t, filename, e);
				return null; // deadcode
			}
		} else {
			throw new RuntimeException("unknown type encountered: " + t);
		}
	}

	protected int label = 0;

	protected String freshLabel() {
		return "cfblab" + label++;
	}

	private static String nameMangle(String name, Type.FunctionOrMethod ft) {
		try {
			return name + "$" + typeMangle(ft);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static String typeMangle(Type.FunctionOrMethod ft)
			throws IOException {
		JavaIdentifierOutputStream jout = new JavaIdentifierOutputStream();
		BinaryOutputStream binout = new BinaryOutputStream(jout);
		Type.BinaryWriter tm = new Type.BinaryWriter(binout);
		tm.write(ft);
		binout.close(); // force flush
		return jout.toString();
	}

	/**
	 * A constant is some kind of auxillary functionality used in generated
	 * code, which can be reused at multiple sites. This includes value
	 * constants, and coercion functions.
	 * 
	 * @author David J. Pearce
	 *
	 */
	private abstract static class JvmConstant {
	}

	private static final class JvmValue extends JvmConstant {
		public final Constant value;

		public JvmValue(Constant v) {
			value = v;
		}

		public boolean equals(Object o) {
			if (o instanceof JvmValue) {
				JvmValue vc = (JvmValue) o;
				return value.equals(vc.value);
			}
			return false;
		}

		public int hashCode() {
			return value.hashCode();
		}

		public static int get(Constant value,
				HashMap<JvmConstant, Integer> constants) {
			JvmValue vc = new JvmValue(value);
			Integer r = constants.get(vc);
			if (r != null) {
				return r;
			} else {
				int x = constants.size();
				constants.put(vc, x);
				return x;
			}
		}
	}

	private static final class JvmCoercion extends JvmConstant {
		public final Type from;
		public final Type to;

		public JvmCoercion(Type from, Type to) {
			this.from = from;
			this.to = to;
		}

		public boolean equals(Object o) {
			if (o instanceof JvmCoercion) {
				JvmCoercion c = (JvmCoercion) o;
				return from.equals(c.from) && to.equals(c.to);
			}
			return false;
		}

		public int hashCode() {
			return from.hashCode() + to.hashCode();
		}

		public static int get(Type from, Type to,
				HashMap<JvmConstant, Integer> constants) {
			JvmCoercion vc = new JvmCoercion(from, to);
			Integer r = constants.get(vc);
			if (r != null) {
				return r;
			} else {
				int x = constants.size();
				constants.put(vc, x);
				return x;
			}
		}
	}

	private static class UnresolvedHandler {
		public String start;
		public String end;
		public String target;
		public JvmType.Clazz exception;

		public UnresolvedHandler(String start, String end, String target,
				JvmType.Clazz exception) {
			this.start = start;
			this.end = end;
			this.target = target;
			this.exception = exception;
		}
	}

	public final class Context {
		/**
		 * The code forest in which we are currently operating
		 */
		private final BytecodeForest forest;
		
		/**
		 * The index of the bytecode being translated
		 */
		private final BytecodeForest.Index pc;
		
		/**
		 * The list of bytecodes that have been generated so far
		 */
		private final ArrayList<Bytecode> bytecodes;
		
		/**
		 * The next available free register slot
		 */
		private final int freeSlot;
		
		public Context(BytecodeForest forest, BytecodeForest.Index pc, int freeSlot, ArrayList<Bytecode> bytecodes) {
			this.forest = forest;
			this.bytecodes = bytecodes;
			this.pc = pc;
			this.freeSlot = freeSlot;
		}
		
		public void add(Bytecode bytecode) {
			bytecodes.add(bytecode);
		}
		
		public void addReadConversion(Type type) {
			Wyil2JavaBuilder.this.addReadConversion(type,bytecodes);
		}
		
		public void addWriteConversion(Type type) {
			Wyil2JavaBuilder.this.addWriteConversion(type,bytecodes);
		}
		
		public void construct(JvmType.Clazz type) {
			Wyil2JavaBuilder.this.construct(type,freeSlot,bytecodes);
		}
		
		public JvmType toJvmType(Type type) {
			return convertUnderlyingType(type);
		}
	}
	
	/**
	 * Provides a simple interface for translating individual bytecodes.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public interface BytecodeTranslator {
		void translate(Operator bytecode, Context context);
	}
}
