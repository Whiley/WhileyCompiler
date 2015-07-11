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
import wyil.util.AttributedCodeBlock;
import wyil.util.TypeExpander;
import static wyil.util.ErrorMessages.internalFailure;
import wyjc.util.WyjcBuildTask;
import jasm.attributes.Code.Handler;
import jasm.attributes.LineNumberTable;
import jasm.attributes.SourceFile;
import jasm.lang.*;
import jasm.lang.Bytecode.Goto;
import jasm.lang.Modifier;
import jasm.lang.Bytecode.Load;
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
	protected String filename;

	/**
	 * Root of block being translated
	 */
	protected AttributedCodeBlock rootBlock;

	/**
	 * Type of enclosing class being generated
	 */
	protected JvmType.Clazz owner;

	/**
	 * Map of Constant values to their pool index
	 */
	protected HashMap<JvmConstant, Integer> constants;

	/**
	 * List of temporary classes created to implement lambda expressions
	 */
	protected ArrayList<ClassFile> lambdas;

	/**
	 * List of line number entries for current function / method being compiled.
	 */
	protected ArrayList<LineNumberTable.Entry> lineNumbers;

	public Wyil2JavaBuilder(Build.Project project) {
		this.project = project;
		this.expander = new TypeExpander(project);
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
		Type.Method wyft = Type.Method(Type.T_VOID, Type.T_VOID,
				WHILEY_SYSTEM_T);
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
		translateInvariantTest(falseBranch, td.type(), 0, 1, constants,
				bytecodes);
		// Second, generate code for invariant (if applicable).
		AttributedCodeBlock invariant = td.invariant();
		if (invariant != null) {
			invariant = patchInvariantBlock(falseBranch, invariant);
			translate(invariant, 1, bytecodes);
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
	 * Construct a modified version of the invariant block whereby all fail
	 * bytecodes are replaced with returning false, and all return bytecodes are
	 * replaced with returning true.
	 */
	private AttributedCodeBlock patchInvariantBlock(String falseBranch,
			AttributedCodeBlock block) {
		AttributedCodeBlock copy = new AttributedCodeBlock(block.bytecodes(),
				block.attributes());
		patchInvariantBlockHelper(falseBranch, copy);
		return copy;
	}

	private void patchInvariantBlockHelper(String falseBranch, CodeBlock block) {
		for (int i = 0; i != block.size(); ++i) {
			// This is still a valid index
			Code c = block.get(i);

			if (c instanceof Codes.Return) {
				// first patch point
				block.set(i, Codes.Nop);
			} else if (c instanceof Codes.Fail) {
				// second patch point
				block.set(i, Codes.Goto(falseBranch));
			} else if (c instanceof Code.Compound) {
				patchInvariantBlockHelper(falseBranch, (Code.Compound) c);
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

		if (ft.ret() == Type.T_VOID) {
			bytecodes.add(new Bytecode.Return(null));
		} else {
			bytecodes.add(new Bytecode.Return(convertUnderlyingType(ft.ret())));
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
		AttributedCodeBlock block = method.body();
		translate(block, block.numSlots(), bytecodes);
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
	private void translate(AttributedCodeBlock blk, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		rootBlock = blk;
		translate(null, blk, freeSlot, bytecodes);
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
	private void translate(CodeBlock.Index parentIndex, CodeBlock block,
			int freeSlot, ArrayList<Bytecode> bytecodes) {

		for (int i = 0; i != block.size(); ++i) {
			CodeBlock.Index index = new CodeBlock.Index(parentIndex, i);
			SourceLocation loc = rootBlock.attribute(index,
					SourceLocation.class);
			if (loc != null) {
				// FIXME: figure our how to get line number!
				// lineNumbers.add(new
				// LineNumberTable.Entry(bytecodes.size(),loc.line));
			}
			freeSlot = translate(index, block.get(i), freeSlot, bytecodes);
		}
	}

	/**
	 * Translate a WyIL bytecode at a given index into one or more JVM
	 * bytecodes. The bytecode index is given to help with debugging (i.e. to
	 * extract attributes associated with the given bytecode).
	 * 
	 * @param index
	 *            The index of the WyIL bytecode being translated in the
	 *            rootBlock.
	 * @param code
	 *            The WyIL bytecode being translated.
	 * @param freeSlot
	 *            The first available free JVM register slot
	 * @param bytecodes
	 *            The list of bytecodes being accumulated
	 * @return
	 */
	private int translate(CodeBlock.Index index, Code code, int freeSlot,
			ArrayList<Bytecode> bytecodes) {

		try {
			if (code instanceof Codes.BinaryOperator) {
				translate(index, (Codes.BinaryOperator) code, freeSlot,
						bytecodes);
			} else if (code instanceof Codes.Convert) {
				translate(index, (Codes.Convert) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.Const) {
				translate(index, (Codes.Const) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.Debug) {
				translate(index, (Codes.Debug) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.AssertOrAssume) {
				translate(index, (Codes.AssertOrAssume) code, freeSlot,
						bytecodes);
			} else if (code instanceof Codes.Fail) {
				translate(index, (Codes.Fail) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.FieldLoad) {
				translate(index, (Codes.FieldLoad) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.Quantify) {
				freeSlot = translate(index, (Codes.Quantify) code, freeSlot,
						bytecodes);
			} else if (code instanceof Codes.Goto) {
				translate(index, (Codes.Goto) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.If) {
				translateIfGoto(index, (Codes.If) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.IfIs) {
				translate(index, (Codes.IfIs) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.IndirectInvoke) {
				translate(index, (Codes.IndirectInvoke) code, freeSlot,
						bytecodes);
			} else if (code instanceof Codes.Invoke) {
				translate(index, (Codes.Invoke) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.Invert) {
				translate(index, (Codes.Invert) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.Label) {
				translate(index, (Codes.Label) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.ListOperator) {
				translate(index, (Codes.ListOperator) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.Lambda) {
				translate(index, (Codes.Lambda) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.LengthOf) {
				translate(index, (Codes.LengthOf) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.SubList) {
				translate(index, (Codes.SubList) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.IndexOf) {
				translate(index, (Codes.IndexOf) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.Assign) {
				translate(index, (Codes.Assign) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.Loop) {
				translate(index, (Codes.Loop) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.Move) {
				translate(index, (Codes.Move) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.Update) {
				translate(index, (Codes.Update) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.NewList) {
				translate(index, (Codes.NewList) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.NewRecord) {
				translate(index, (Codes.NewRecord) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.NewTuple) {
				translate(index, (Codes.NewTuple) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.UnaryOperator) {
				translate(index, (Codes.UnaryOperator) code, freeSlot,
						bytecodes);
			} else if (code instanceof Codes.Dereference) {
				translate(index, (Codes.Dereference) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.Return) {
				translate(index, (Codes.Return) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.Nop) {
				// do nothing
			} else if (code instanceof Codes.Switch) {
				translate(index, (Codes.Switch) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.NewObject) {
				translate(index, (Codes.NewObject) code, freeSlot, bytecodes);
			} else if (code instanceof Codes.TupleLoad) {
				translate(index, (Codes.TupleLoad) code, freeSlot, bytecodes);
			} else {
				internalFailure("unknown wyil code encountered (" + code + ")",
						filename,
						rootBlock.attribute(index, SourceLocation.class));
			}

		} catch (Exception ex) {
			internalFailure(ex.getMessage(), filename, ex,
					rootBlock.attribute(index, SourceLocation.class));
		}

		return freeSlot;
	}
	

	private void translate(CodeBlock.Index index, Codes.AssertOrAssume c,
			int freeSlot, ArrayList<Bytecode> bytecodes) {
		if(c instanceof Codes.Invariant) {
			// essentially a no-op for now			
		} else if(c instanceof Codes.Assert) { 
			Codes.Assert ca = (Codes.Assert) c;
			translate(index, (CodeBlock) c, freeSlot, bytecodes);
		} else {
			Codes.Assume ca = (Codes.Assume) c;
			translate(index, (CodeBlock) c, freeSlot, bytecodes);
		}
	}

	private void translate(CodeBlock.Index index, Codes.Const c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		Constant constant = c.constant;
		JvmType jt = convertUnderlyingType(constant.type());

		if (constant instanceof Constant.Decimal
				|| constant instanceof Constant.Bool
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

	private void translate(CodeBlock.Index index, Codes.Convert c,
			int freeSlot, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Load(c.operand(0), convertUnderlyingType(c
				.type())));
		addCoercion(c.type(), c.result, freeSlot, constants, bytecodes);
		bytecodes.add(new Bytecode.Store(c.target(),
				convertUnderlyingType(c.result)));
	}

	private void translate(CodeBlock.Index index, Codes.Update code,
			int freeSlot, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Load(code.target(),
				convertUnderlyingType(code.type())));
		translateUpdate(code.iterator(), code, bytecodes);
		bytecodes.add(new Bytecode.Store(code.target(),
				convertUnderlyingType(code.afterType)));
	}

	/**
	 * Iterate down the chain of updates reading values out, updating them, and
	 * writing them back.
	 *
	 * @param iterator
	 *            --- update iterator.
	 * @param rhsOperand
	 *            --- register operand of right-hand side
	 * @param bytecodes
	 *            --- list of bytecodes to append to.
	 */
	private void translateUpdate(Iterator<Codes.LVal> iterator,
			Codes.Update code, ArrayList<Bytecode> bytecodes) {
		Codes.LVal lv = iterator.next();
		if (lv instanceof Codes.ListLVal) {
			Codes.ListLVal l = (Codes.ListLVal) lv;
			if (iterator.hasNext()) {
				// In this case, we're partially updating the element at a
				// given position.
				bytecodes.add(new Bytecode.Dup(WHILEYLIST));
				bytecodes.add(new Bytecode.Load(l.indexOperand, WHILEYINT));
				JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
						WHILEYLIST, WHILEYINT);
				bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "internal_get",
						ftype, Bytecode.InvokeMode.STATIC));
				addReadConversion(l.rawType().element(), bytecodes);
				translateUpdate(iterator, code, bytecodes);
				bytecodes.add(new Bytecode.Load(l.indexOperand, WHILEYINT));
				bytecodes.add(new Bytecode.Swap());
			} else {
				bytecodes.add(new Bytecode.Load(l.indexOperand, WHILEYINT));
				bytecodes.add(new Bytecode.Load(code.result(),
						convertUnderlyingType(l.rawType().element())));
				addWriteConversion(code.rhs(), bytecodes);
			}

			JvmType.Function ftype = new JvmType.Function(WHILEYLIST,
					WHILEYLIST, WHILEYINT, JAVA_LANG_OBJECT);
			bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "set", ftype,
					Bytecode.InvokeMode.STATIC));

		} else if (lv instanceof Codes.RecordLVal) {
			Codes.RecordLVal l = (Codes.RecordLVal) lv;
			Type.EffectiveRecord type = l.rawType();

			if (iterator.hasNext()) {
				bytecodes.add(new Bytecode.Dup(WHILEYRECORD));
				bytecodes.add(new Bytecode.LoadConst(l.field));
				JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
						WHILEYRECORD, JAVA_LANG_STRING);
				bytecodes.add(new Bytecode.Invoke(WHILEYRECORD, "internal_get",
						ftype, Bytecode.InvokeMode.STATIC));
				addReadConversion(type.field(l.field), bytecodes);
				translateUpdate(iterator, code, bytecodes);
				bytecodes.add(new Bytecode.LoadConst(l.field));
				bytecodes.add(new Bytecode.Swap());
			} else {
				bytecodes.add(new Bytecode.LoadConst(l.field));
				bytecodes.add(new Bytecode.Load(code.result(),
						convertUnderlyingType(type.field(l.field))));
				addWriteConversion(type.field(l.field), bytecodes);
			}

			JvmType.Function ftype = new JvmType.Function(WHILEYRECORD,
					WHILEYRECORD, JAVA_LANG_STRING, JAVA_LANG_OBJECT);
			bytecodes.add(new Bytecode.Invoke(WHILEYRECORD, "put", ftype,
					Bytecode.InvokeMode.STATIC));
		} else {
			Codes.ReferenceLVal l = (Codes.ReferenceLVal) lv;
			bytecodes.add(new Bytecode.Dup(WHILEYOBJECT));
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT);
			bytecodes.add(new Bytecode.Invoke(WHILEYOBJECT, "state", ftype,
					Bytecode.InvokeMode.VIRTUAL));
			addReadConversion(l.rawType().element(), bytecodes);
			translateUpdate(iterator, code, bytecodes);
			ftype = new JvmType.Function(WHILEYOBJECT, JAVA_LANG_OBJECT);
			bytecodes.add(new Bytecode.Invoke(WHILEYOBJECT, "setState", ftype,
					Bytecode.InvokeMode.VIRTUAL));
		}
	}

	private void translate(CodeBlock.Index index, Codes.Return c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		if (c.type == Type.T_VOID) {
			bytecodes.add(new Bytecode.Return(null));
		} else {
			JvmType jt = convertUnderlyingType(c.type);
			bytecodes.add(new Bytecode.Load(c.operand, jt));
			bytecodes.add(new Bytecode.Return(jt));
		}
	}

	private void translate(CodeBlock.Index index, Codes.TupleLoad c,
			int freeSlot, ArrayList<Bytecode> bytecodes) {
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
				WHILEYTUPLE, T_INT);
		bytecodes.add(new Bytecode.Load(c.operand(0),
				convertUnderlyingType((Type) c.type())));
		bytecodes.add(new Bytecode.LoadConst(c.index));
		bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE, "get", ftype,
				Bytecode.InvokeMode.STATIC));
		addReadConversion(c.type().elements().get(c.index), bytecodes);
		bytecodes.add(new Bytecode.Store(c.target(), convertUnderlyingType(c
				.type().element(c.index))));
	}

	private void translate(CodeBlock.Index index, Codes.Switch c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {

		ArrayList<jasm.util.Pair<Integer, String>> cases = new ArrayList();
		boolean canUseSwitchBytecode = true;
		for (Pair<Constant, String> p : c.branches) {
			// first, check whether the switch value is indeed an integer.
			Constant v = (Constant) p.first();
			if (!(v instanceof Constant.Integer)) {
				canUseSwitchBytecode = false;
				break;
			}
			// second, check whether integer value can fit into a Java int
			Constant.Integer vi = (Constant.Integer) v;
			int iv = vi.value.intValue();
			if (!BigInteger.valueOf(iv).equals(vi.value)) {
				canUseSwitchBytecode = false;
				break;
			}
			// ok, we're all good so far
			cases.add(new jasm.util.Pair(iv, p.second()));
		}

		if (canUseSwitchBytecode) {
			JvmType.Function ftype = new JvmType.Function(T_INT);
			bytecodes.add(new Bytecode.Load(c.operand,
					convertUnderlyingType((Type) c.type)));
			bytecodes.add(new Bytecode.Invoke(WHILEYINT, "intValue", ftype,
					Bytecode.InvokeMode.VIRTUAL));
			bytecodes.add(new Bytecode.Switch(c.defaultTarget, cases));
		} else {
			// ok, in this case we have to fall back to series of the if
			// conditions. Not ideal.
			for (Pair<Constant, String> p : c.branches) {
				Constant value = p.first();
				String target = p.second();
				translate(value, freeSlot, bytecodes);
				bytecodes.add(new Bytecode.Load(c.operand,
						convertUnderlyingType(c.type)));
				translateIfGoto(index, value.type(), Codes.Comparator.EQ,
						target, freeSlot + 1, bytecodes);
			}
			bytecodes.add(new Bytecode.Goto(c.defaultTarget));
		}
	}

	private void translateIfGoto(CodeBlock.Index index, Codes.If code,
			int freeSlot, ArrayList<Bytecode> bytecodes) {
		JvmType jt = convertUnderlyingType(code.type);
		bytecodes.add(new Bytecode.Load(code.leftOperand, jt));
		bytecodes.add(new Bytecode.Load(code.rightOperand, jt));
		translateIfGoto(index, code.type, code.op, code.target, freeSlot,
				bytecodes);
	}

	private void translateIfGoto(CodeBlock.Index index, Type c_type,
			Codes.Comparator cop, String target, int freeSlot,
			ArrayList<Bytecode> bytecodes) {

		JvmType type = convertUnderlyingType(c_type);
		// Just use the Object.equals() method, followed
		// by "if" bytecode.
		Bytecode.IfMode op;
		switch (cop) {
		case EQ: {
			if (Type.isSubtype(c_type, Type.T_NULL)) {
				// this indicates an interesting special case. The left
				// handside of this equality can be null. Therefore, we
				// cannot directly call "equals()" on this method, since
				// this would cause a null pointer exception!
				JvmType.Function ftype = new JvmType.Function(T_BOOL,
						JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);
				bytecodes.add(new Bytecode.Invoke(WHILEYUTIL, "equals", ftype,
						Bytecode.InvokeMode.STATIC));
			} else {
				JvmType.Function ftype = new JvmType.Function(T_BOOL,
						JAVA_LANG_OBJECT);
				bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type,
						"equals", ftype, Bytecode.InvokeMode.VIRTUAL));
			}
			op = Bytecode.IfMode.NE;
			break;
		}
		case NEQ: {
			if (Type.isSubtype(c_type, Type.T_NULL)) {
				// this indicates an interesting special case. The left
				// handside of this equality can be null. Therefore, we
				// cannot directly call "equals()" on this method, since
				// this would cause a null pointer exception!
				JvmType.Function ftype = new JvmType.Function(T_BOOL,
						JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);
				bytecodes.add(new Bytecode.Invoke(WHILEYUTIL, "equals", ftype,
						Bytecode.InvokeMode.STATIC));
			} else {
				JvmType.Function ftype = new JvmType.Function(T_BOOL,
						JAVA_LANG_OBJECT);
				bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type,
						"equals", ftype, Bytecode.InvokeMode.VIRTUAL));
			}
			op = Bytecode.IfMode.EQ;
			break;
		}
		case LT: {
			JvmType.Function ftype = new JvmType.Function(T_INT, type);
			bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type,
					"compareTo", ftype, Bytecode.InvokeMode.VIRTUAL));
			op = Bytecode.IfMode.LT;
			break;
		}
		case LTEQ: {
			JvmType.Function ftype = new JvmType.Function(T_INT, type);
			bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type,
					"compareTo", ftype, Bytecode.InvokeMode.VIRTUAL));
			op = Bytecode.IfMode.LE;
			break;
		}
		case GT: {
			JvmType.Function ftype = new JvmType.Function(T_INT, type);
			bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type,
					"compareTo", ftype, Bytecode.InvokeMode.VIRTUAL));
			op = Bytecode.IfMode.GT;
			break;
		}
		case GTEQ: {
			JvmType.Function ftype = new JvmType.Function(T_INT, type);
			bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type,
					"compareTo", ftype, Bytecode.InvokeMode.VIRTUAL));
			op = Bytecode.IfMode.GE;
			break;
		}	
		case IN: {
			JvmType.Function ftype = new JvmType.Function(T_BOOL,
					JAVA_LANG_OBJECT);
			bytecodes.add(new Bytecode.Swap());
			bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_COLLECTION, "contains",
					ftype, Bytecode.InvokeMode.INTERFACE));
			op = Bytecode.IfMode.NE;
			break;
		}

		default:
			internalFailure("unknown if condition encountered", filename,
					rootBlock.attribute(index, SourceLocation.class));
			return;
		}

		// do the jump
		bytecodes.add(new Bytecode.If(op, target));
	}

	private void translate(CodeBlock.Index index, Codes.IfIs c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {

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
		Type maximalConsumedType;
		Type underlyingType;
		try {
			maximalConsumedType = expander
					.getMaximallyConsumedType(c.rightOperand);
			underlyingType = expander.getUnderlyingType(c.rightOperand);
		} catch (Exception e) {
			internalFailure("error computing maximally consumed type: "
					+ c.rightOperand, filename, e);
			return;
		}

		// The false label will determine the destination where the variable
		// will be retyped on the false branch begins.
		String falseLabel = freshLabel();

		// Second, translate the raw type test. This will direct all values
		// matching the underlying type towards the step label. At that point,
		// we need to check whether the necessary constrained (if applicable)
		// are met.
		bytecodes.add(new Bytecode.Load(c.operand,
				convertUnderlyingType(c.type)));
		translateTypeTest(falseLabel, underlyingType, constants, bytecodes);
		
		// Third, update the type of the variable on the true branch. This is
		// the intersection of its original type with that of the test to
		// produce the most precise type possible.
		Type typeOnTrueBranch = Type.intersect(c.type, underlyingType);
		bytecodes.add(new Bytecode.Load(c.operand,
				convertUnderlyingType(c.type)));
		addReadConversion(typeOnTrueBranch, bytecodes);
		bytecodes.add(new Bytecode.Store(c.operand,
				convertUnderlyingType(typeOnTrueBranch)));

		// Fourth handle constrained types by invoking a function which will
		// execute any and all constraints associated with the type. For
		// recursive types, this may result in recursive calls.
		translateInvariantTest(falseLabel, c.rightOperand, c.operand, freeSlot,
				constants, bytecodes);
		bytecodes.add(new Bytecode.Goto(c.target));
		// Finally, construct false branch and retype the variable on the false
		// branch to ensure it has the most precise type we know at this point.
		bytecodes.add(new Bytecode.Label(falseLabel));
		Type typeOnFalseBranch = Type.intersect(c.type,
				Type.Negation(maximalConsumedType));
		bytecodes.add(new Bytecode.Load(c.operand,
				convertUnderlyingType(c.type)));
		addReadConversion(typeOnFalseBranch, bytecodes);
		bytecodes.add(new Bytecode.Store(c.operand,
				convertUnderlyingType(typeOnFalseBranch)));
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
		} else if (test instanceof Type.Real) {
			bytecodes.add(new Bytecode.InstanceOf(WHILEYRAT));
			bytecodes.add(new Bytecode.If(Bytecode.IfMode.EQ, falseTarget));
		} else {
			// Fall-back to an external (recursive) check
			Constant constant = Constant.V_TYPE(test);
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

	private void translateInvariantTest(String falseTarget, Type type,
			int rootSlot, int freeSlot,
			HashMap<JvmConstant, Integer> constants,
			ArrayList<Bytecode> bytecodes) {
		//
		JvmType underlyingType = convertUnderlyingType(type);
		//
		if (type instanceof Type.Nominal) {
			Type.Nominal c = (Type.Nominal) type;
			Path.ID mid = c.name().module();
			JvmType.Clazz owner = new JvmType.Clazz(mid.parent().toString()
					.replace('/', '.'), mid.last());
			JvmType.Function fnType = new JvmType.Function(new JvmType.Bool(),
					convertUnderlyingType(c));
			bytecodes.add(new Bytecode.Load(rootSlot,
					convertUnderlyingType(type)));
			bytecodes.add(new Bytecode.Invoke(owner, c.name().name()
					+ "$typeof", fnType, Bytecode.InvokeMode.STATIC));
			bytecodes.add(new Bytecode.If(Bytecode.IfMode.EQ, falseTarget));
		} else if (type instanceof Type.Leaf) {
			// Do nout
		} else if (type instanceof Type.Reference) {
			Type.Reference rt = (Type.Reference) type;
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT);
			bytecodes.add(new Bytecode.Load(rootSlot, underlyingType));
			bytecodes.add(new Bytecode.Invoke(WHILEYOBJECT, "state", ftype,
					Bytecode.InvokeMode.VIRTUAL));
			addReadConversion(rt.element(), bytecodes);
			bytecodes.add(new Bytecode.Store(freeSlot, convertUnderlyingType(rt
					.element())));
			translateInvariantTest(falseTarget, rt.element(), freeSlot,
					freeSlot + 1, constants, bytecodes);
		} else if (type instanceof Type.EffectiveList) {
			Type.EffectiveList ts = (Type.EffectiveList) type;
			Triple<String, String, String> loopLabels = translateLoopBegin(
					bytecodes, rootSlot, freeSlot);
			addReadConversion(ts.element(), bytecodes);
			bytecodes.add(new Bytecode.Store(freeSlot + 1,
					convertUnderlyingType(ts.element())));
			translateInvariantTest(falseTarget, ts.element(), freeSlot + 1,
					freeSlot + 2, constants, bytecodes);
			translateLoopEnd(bytecodes, loopLabels);
		} else if (type instanceof Type.Tuple) {
			Type.Tuple tt = (Type.Tuple) type;
			for (int i = 0; i != tt.size(); ++i) {
				Type elementType = tt.element(i);
				JvmType underlyingElementType = convertUnderlyingType(elementType);
				bytecodes.add(new Bytecode.Load(rootSlot, underlyingType));
				bytecodes.add(new Bytecode.LoadConst(i));
				JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
						T_INT);
				bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE, "get", ftype,
						Bytecode.InvokeMode.VIRTUAL));
				addReadConversion(elementType, bytecodes);
				bytecodes.add(new Bytecode.Store(freeSlot,
						underlyingElementType));
				translateInvariantTest(falseTarget, elementType, freeSlot,
						freeSlot + 1, constants, bytecodes);
			}
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
				JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
						JAVA_LANG_STRING);
				bytecodes.add(new Bytecode.Invoke(WHILEYRECORD, "get", ftype,
						Bytecode.InvokeMode.VIRTUAL));
				addReadConversion(fieldType, bytecodes);
				bytecodes
						.add(new Bytecode.Store(freeSlot, underlyingFieldType));
				translateInvariantTest(falseTarget, fieldType, freeSlot,
						freeSlot + 1, constants, bytecodes);
			}
		} else if (type instanceof Type.FunctionOrMethod) {
			// FIXME: this is clearly a bug. However, it's not completely
			// straightforward to fix, since there is currently no way to get
			// runtime type information about a function or method reference. In
			// principle, this could be encoded in the WyLambda in some way.
		} else if (type instanceof Type.Negation) {
			Type.Reference rt = (Type.Reference) type;
			String trueTarget = freshLabel();
			translateInvariantTest(trueTarget, rt.element(), rootSlot,
					freeSlot, constants, bytecodes);
			bytecodes.add(new Bytecode.Goto(falseTarget));
			bytecodes.add(new Bytecode.Label(trueTarget));
		} else if (type instanceof Type.Union) {
			Type.Union ut = (Type.Union) type;
			String trueLabel = freshLabel();
			for (Type bound : ut.bounds()) {
				try {
					Type underlyingBound = expander.getUnderlyingType(bound);
					String nextLabel = freshLabel();
					bytecodes.add(new Bytecode.Load(rootSlot,
							convertUnderlyingType(type)));
					translateTypeTest(nextLabel, underlyingBound, constants,
							bytecodes);
					bytecodes.add(new Bytecode.Load(rootSlot,
							convertUnderlyingType(type)));
					addReadConversion(bound, bytecodes);
					bytecodes.add(new Bytecode.Store(freeSlot,
							convertUnderlyingType(bound)));
					translateInvariantTest(nextLabel, bound, freeSlot,
							freeSlot + 1, constants, bytecodes);
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

	private void translate(CodeBlock.Index index, Codes.Loop c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {

		// Allocate header label for loop
		String loopHeader = freshLabel();
		bytecodes.add(new Bytecode.Label(loopHeader));
		// Translate body of loop. The cast is required to ensure correct method
		// is called.
		translate(index, (CodeBlock) c, freeSlot, bytecodes);
		// Terminate loop by branching back to head of loop
		bytecodes.add(new Bytecode.Goto(loopHeader));
	}

	private int translate(CodeBlock.Index index, Codes.Quantify c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {

		bytecodes.add(new Bytecode.Load(c.startOperand,WHILEYINT));
		bytecodes.add(new Bytecode.Load(c.endOperand,WHILEYINT));
		JvmType.Function ftype = new JvmType.Function(WHILEYLIST, WHILEYINT, WHILEYINT);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "range", ftype,
				Bytecode.InvokeMode.STATIC));
		ftype = new JvmType.Function(JAVA_UTIL_ITERATOR);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_COLLECTION, "iterator",
				ftype, Bytecode.InvokeMode.INTERFACE));
		bytecodes.add(new Bytecode.Store(freeSlot, JAVA_UTIL_ITERATOR));
		String loopHeader = freshLabel();
		String loopExit = freshLabel();
		bytecodes.add(new Bytecode.Label(loopHeader));
		ftype = new JvmType.Function(T_BOOL);
		bytecodes.add(new Bytecode.Load(freeSlot, JAVA_UTIL_ITERATOR));
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "hasNext", ftype,
				Bytecode.InvokeMode.INTERFACE));
		bytecodes.add(new Bytecode.If(Bytecode.IfMode.EQ, loopExit));
		bytecodes.add(new Bytecode.Load(freeSlot, JAVA_UTIL_ITERATOR));
		ftype = new JvmType.Function(JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "next", ftype,
				Bytecode.InvokeMode.INTERFACE));
		addReadConversion(Type.T_INT, bytecodes);
		bytecodes.add(new Bytecode.Store(c.indexOperand,
				convertUnderlyingType(Type.T_INT)));
		// Translate body of loop. The cast is required to ensure correct method
		// is called.
		translate(index, (CodeBlock) c, freeSlot + 1, bytecodes);
		// Terminate loop by branching back to head of loop
		bytecodes.add(new Bytecode.Goto(loopHeader));
		bytecodes.add(new Bytecode.Label(loopExit));

		return freeSlot;
	}

	private void translate(CodeBlock.Index index, Codes.Goto c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Goto(c.target));
	}

	private void translate(CodeBlock.Index index, Codes.Label c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Label(c.label));
	}

	private void translate(CodeBlock.Index index, Codes.Debug c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		JvmType.Function ftype = new JvmType.Function(T_VOID, WHILEYLIST);
		bytecodes.add(new Bytecode.Load(c.operand, WHILEYLIST));
		bytecodes.add(new Bytecode.Invoke(WHILEYUTIL, "print", ftype,
				Bytecode.InvokeMode.STATIC));
	}

	private void translate(CodeBlock.Index index, Codes.Assign c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		JvmType jt = convertUnderlyingType(c.type());
		bytecodes.add(new Bytecode.Load(c.operand(0), jt));
		bytecodes.add(new Bytecode.Store(c.target(), jt));
	}

	private void translate(CodeBlock.Index index, Codes.Move c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		JvmType jt = convertUnderlyingType(c.type());
		bytecodes.add(new Bytecode.Load(c.operand(0), jt));
		bytecodes.add(new Bytecode.Store(c.target(), jt));
	}

	private void translate(CodeBlock.Index index, Codes.ListOperator c,
			int freeSlot, ArrayList<Bytecode> bytecodes) {
		JvmType leftType;
		JvmType rightType;

		switch (c.kind) {
		case APPEND:
			leftType = WHILEYLIST;
			rightType = WHILEYLIST;
			bytecodes.add(new Bytecode.Load(c.operand(0), leftType));
			bytecodes.add(new Bytecode.Load(c.operand(1), rightType));
			break;
		default:
			internalFailure("unknown list operation", filename,
					rootBlock.attribute(index, SourceLocation.class));
			return;
		}

		JvmType.Function ftype = new JvmType.Function(WHILEYLIST, leftType,
				rightType);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "append", ftype,
				Bytecode.InvokeMode.STATIC));
		bytecodes.add(new Bytecode.Store(c.target(), WHILEYLIST));
	}

	private void translate(CodeBlock.Index index, Codes.LengthOf c,
			int freeSlot, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Load(c.operand(0),
				convertUnderlyingType((Type) c.type())));
		JvmType.Clazz ctype = JAVA_LANG_OBJECT;
		JvmType.Function ftype = new JvmType.Function(WHILEYINT);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "length", ftype,
				Bytecode.InvokeMode.VIRTUAL));
		bytecodes.add(new Bytecode.Store(c.target(), WHILEYINT));
	}

	private void translate(CodeBlock.Index index, Codes.SubList c,
			int freeSlot, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.Load(c.operands()[0], WHILEYLIST));
		bytecodes.add(new Bytecode.Load(c.operands()[1], WHILEYINT));
		bytecodes.add(new Bytecode.Load(c.operands()[2], WHILEYINT));

		JvmType.Function ftype = new JvmType.Function(WHILEYLIST, WHILEYLIST,
				WHILEYINT, WHILEYINT);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "sublist", ftype,
				Bytecode.InvokeMode.STATIC));

		bytecodes.add(new Bytecode.Store(c.target(), WHILEYLIST));
	}

	private void translate(CodeBlock.Index index, Codes.IndexOf c,
			int freeSlot, ArrayList<Bytecode> bytecodes) {

		bytecodes.add(new Bytecode.Load(c.operand(0), WHILEYLIST));
		bytecodes.add(new Bytecode.Load(c.operand(1), WHILEYINT));
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
				WHILEYLIST, WHILEYINT);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "get", ftype,
				Bytecode.InvokeMode.STATIC));
		addReadConversion(c.type().element(), bytecodes);

		bytecodes.add(new Bytecode.Store(c.target(), convertUnderlyingType(c
				.type().element())));
	}

	private void translate(CodeBlock.Index index, Codes.Fail c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.New(JAVA_LANG_RUNTIMEEXCEPTION));
		bytecodes.add(new Bytecode.Dup(JAVA_LANG_RUNTIMEEXCEPTION));
		bytecodes.add(new Bytecode.LoadConst("runtime fault encountered"));
		JvmType.Function ftype = new JvmType.Function(T_VOID, JAVA_LANG_STRING);
		bytecodes.add(new Bytecode.Invoke(JAVA_LANG_RUNTIMEEXCEPTION, "<init>",
				ftype, Bytecode.InvokeMode.SPECIAL));
		bytecodes.add(new Bytecode.Throw());
	}

	private void translate(CodeBlock.Index index, Codes.FieldLoad c,
			int freeSlot, ArrayList<Bytecode> bytecodes) {

		bytecodes.add(new Bytecode.Load(c.operand(0), WHILEYRECORD));

		bytecodes.add(new Bytecode.LoadConst(c.field));
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
				WHILEYRECORD, JAVA_LANG_STRING);
		bytecodes.add(new Bytecode.Invoke(WHILEYRECORD, "get", ftype,
				Bytecode.InvokeMode.STATIC));
		addReadConversion(c.fieldType(), bytecodes);

		bytecodes.add(new Bytecode.Store(c.target(), convertUnderlyingType(c
				.fieldType())));
	}

	private void translate(CodeBlock.Index index, Codes.BinaryOperator c,
			int freeSlot, ArrayList<Bytecode> bytecodes) {

		JvmType type = convertUnderlyingType(c.type());
		JvmType.Function ftype = new JvmType.Function(type, type);

		// first, load operands
		switch (c.kind) {
		case ADD:
		case SUB:
		case MUL:
		case DIV:
		case REM:
		case BITWISEAND:
		case BITWISEOR:
		case BITWISEXOR:
			bytecodes.add(new Bytecode.Load(c.operand(0), type));
			bytecodes.add(new Bytecode.Load(c.operand(1), type));
			break;
		case LEFTSHIFT:
		case RIGHTSHIFT:
			bytecodes.add(new Bytecode.Load(c.operand(0), type));
			bytecodes.add(new Bytecode.Load(c.operand(1), WHILEYINT));
			break;
		}

		// second, apply operation
		switch (c.kind) {
		case ADD:
			bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type, "add",
					ftype, Bytecode.InvokeMode.VIRTUAL));
			break;
		case SUB:
			bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type, "subtract",
					ftype, Bytecode.InvokeMode.VIRTUAL));
			break;
		case MUL:
			bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type, "multiply",
					ftype, Bytecode.InvokeMode.VIRTUAL));
			break;
		case DIV:
			bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type, "divide",
					ftype, Bytecode.InvokeMode.VIRTUAL));
			break;
		case REM:
			bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) type,
					"remainder", ftype, Bytecode.InvokeMode.VIRTUAL));
			break;
		case BITWISEAND:
			ftype = new JvmType.Function(type, type);
			bytecodes.add(new Bytecode.Invoke(WHILEYBYTE, "and", ftype,
					Bytecode.InvokeMode.VIRTUAL));
			break;
		case BITWISEOR:
			ftype = new JvmType.Function(type, type);
			bytecodes.add(new Bytecode.Invoke(WHILEYBYTE, "or", ftype,
					Bytecode.InvokeMode.VIRTUAL));
			break;
		case BITWISEXOR:
			ftype = new JvmType.Function(type, type);
			bytecodes.add(new Bytecode.Invoke(WHILEYBYTE, "xor", ftype,
					Bytecode.InvokeMode.VIRTUAL));
			break;
		case LEFTSHIFT:
			ftype = new JvmType.Function(type, WHILEYINT);
			bytecodes.add(new Bytecode.Invoke(WHILEYBYTE, "leftShift", ftype,
					Bytecode.InvokeMode.VIRTUAL));
			break;
		case RIGHTSHIFT:
			ftype = new JvmType.Function(type, WHILEYINT);
			bytecodes.add(new Bytecode.Invoke(WHILEYBYTE, "rightShift", ftype,
					Bytecode.InvokeMode.VIRTUAL));
			break;
		default:
			internalFailure("unknown binary expression encountered", filename,
					rootBlock.attribute(index, SourceLocation.class));
		}

		bytecodes.add(new Bytecode.Store(c.target(), type));
	}

	private void translate(CodeBlock.Index index, Codes.Invert c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		JvmType type = convertUnderlyingType(c.type());
		bytecodes.add(new Bytecode.Load(c.operand(0), type));
		JvmType.Function ftype = new JvmType.Function(type);
		bytecodes.add(new Bytecode.Invoke(WHILEYBYTE, "compliment", ftype,
				Bytecode.InvokeMode.VIRTUAL));
		bytecodes.add(new Bytecode.Store(c.target(), type));
	}

	private void translate(CodeBlock.Index index, Codes.UnaryOperator c,
			int freeSlot, ArrayList<Bytecode> bytecodes) {
		JvmType srcType = convertUnderlyingType(c.type());
		JvmType targetType = null;
		String name = null;
		switch (c.kind) {
		case NEG:
			targetType = srcType;
			name = "negate";
			break;
		case NUMERATOR:
			targetType = WHILEYINT;
			name = "numerator";
			break;
		case DENOMINATOR:
			targetType = WHILEYINT;
			name = "denominator";
			break;
		}
		JvmType.Function ftype = new JvmType.Function(targetType);
		bytecodes.add(new Bytecode.Load(c.operand(0), srcType));
		bytecodes.add(new Bytecode.Invoke((JvmType.Clazz) srcType, name, ftype,
				Bytecode.InvokeMode.VIRTUAL));
		bytecodes.add(new Bytecode.Store(c.target(), targetType));
	}

	private void translate(CodeBlock.Index index, Codes.NewObject c,
			int freeSlot, ArrayList<Bytecode> bytecodes) {
		JvmType type = convertUnderlyingType(c.type());
		bytecodes.add(new Bytecode.New(WHILEYOBJECT));
		bytecodes.add(new Bytecode.Dup(WHILEYOBJECT));
		bytecodes.add(new Bytecode.Load(c.operand(0), convertUnderlyingType(c
				.type().element())));
		addWriteConversion(c.type().element(), bytecodes);
		JvmType.Function ftype = new JvmType.Function(T_VOID, JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYOBJECT, "<init>", ftype,
				Bytecode.InvokeMode.SPECIAL));
		bytecodes.add(new Bytecode.Store(c.target(), type));
	}

	private void translate(CodeBlock.Index index, Codes.Dereference c,
			int freeSlot, ArrayList<Bytecode> bytecodes) {
		JvmType type = convertUnderlyingType(c.type());
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Load(c.operand(0), type));
		bytecodes.add(new Bytecode.Invoke(WHILEYOBJECT, "state", ftype,
				Bytecode.InvokeMode.VIRTUAL));
		// finally, we need to cast the object we got back appropriately.
		Type.Reference pt = (Type.Reference) c.type();
		addReadConversion(pt.element(), bytecodes);
		bytecodes.add(new Bytecode.Store(c.target(), convertUnderlyingType(c
				.type().element())));
	}

	protected void translate(CodeBlock.Index index, Codes.NewList c,
			int freeSlot, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.New(WHILEYLIST));
		bytecodes.add(new Bytecode.Dup(WHILEYLIST));
		bytecodes.add(new Bytecode.LoadConst(c.operands().length));
		JvmType.Function ftype = new JvmType.Function(T_VOID, T_INT);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "<init>", ftype,
				Bytecode.InvokeMode.SPECIAL));

		ftype = new JvmType.Function(WHILEYLIST, WHILEYLIST, JAVA_LANG_OBJECT);
		for (int i = 0; i != c.operands().length; ++i) {
			bytecodes.add(new Bytecode.Load(c.operands()[i],
					convertUnderlyingType(c.type().element())));
			addWriteConversion(c.type().element(), bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "internal_add",
					ftype, Bytecode.InvokeMode.STATIC));
		}

		bytecodes.add(new Bytecode.Store(c.target(), WHILEYLIST));
	}

	private void translate(CodeBlock.Index index, Codes.NewRecord code,
			int freeSlot, ArrayList<Bytecode> bytecodes) {
		construct(WHILEYRECORD, freeSlot, bytecodes);
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
				JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);

		HashMap<String, Type> fields = code.type().fields();
		ArrayList<String> keys = new ArrayList<String>(fields.keySet());
		Collections.sort(keys);
		for (int i = 0; i != code.operands().length; i++) {
			int register = code.operands()[i];
			String key = keys.get(i);
			Type fieldType = fields.get(key);
			bytecodes.add(new Bytecode.Dup(WHILEYRECORD));
			bytecodes.add(new Bytecode.LoadConst(key));
			bytecodes.add(new Bytecode.Load(register,
					convertUnderlyingType(fieldType)));
			addWriteConversion(fieldType, bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYRECORD, "put", ftype,
					Bytecode.InvokeMode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
		}

		bytecodes.add(new Bytecode.Store(code.target(), WHILEYRECORD));
	}

	protected void translate(CodeBlock.Index index, Codes.NewTuple c,
			int freeSlot, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.New(WHILEYTUPLE));
		bytecodes.add(new Bytecode.Dup(WHILEYTUPLE));
		bytecodes.add(new Bytecode.LoadConst(c.operands().length));
		JvmType.Function ftype = new JvmType.Function(T_VOID, T_INT);
		bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE, "<init>", ftype,
				Bytecode.InvokeMode.SPECIAL));

		ftype = new JvmType.Function(WHILEYTUPLE, WHILEYTUPLE, JAVA_LANG_OBJECT);
		for (int i = 0; i != c.operands().length; ++i) {
			Type elementType = c.type().elements().get(i);
			bytecodes.add(new Bytecode.Load(c.operands()[i],
					convertUnderlyingType(elementType)));
			addWriteConversion(elementType, bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE, "internal_add",
					ftype, Bytecode.InvokeMode.STATIC));
		}

		bytecodes.add(new Bytecode.Store(c.target(), WHILEYTUPLE));
	}

	private void translate(CodeBlock.Index index, Codes.Lambda c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {

		// First, build and register lambda class which calls the given function
		// or method. This class will extend class wyjc.runtime.WyLambda.
		int lambda_id = lambdas.size();
		lambdas.add(buildLambda(c.name, c.type(), lambda_id));

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
		boolean hasBinding = false;

		for (int operand : c.operands()) {
			if (operand != Codes.NULL_REG) {
				hasBinding = true;
				break;
			}
		}

		if (hasBinding) {
			// Yes, binding is required.
			bytecodes.add(new Bytecode.LoadConst(c.operands().length));
			bytecodes.add(new Bytecode.New(JAVA_LANG_OBJECT_ARRAY));

			for (int i = 0; i != c.operands().length; ++i) {
				bytecodes.add(new Bytecode.Dup(JAVA_LANG_OBJECT_ARRAY));
				bytecodes.add(new Bytecode.LoadConst(i));
				int operand = c.operands()[i];

				if (operand != Codes.NULL_REG) {
					Type pt = c.type().params().get(i);
					bytecodes.add(new Bytecode.Load(operand,
							convertUnderlyingType(pt)));
					addWriteConversion(pt, bytecodes);
				} else {
					bytecodes.add(new Bytecode.LoadConst(null));
				}
				bytecodes.add(new Bytecode.ArrayStore(JAVA_LANG_OBJECT_ARRAY));
			}
		} else {
			// No, binding not required.
			bytecodes.add(new Bytecode.LoadConst(null));
		}

		// Fifth, invoke lambda class constructor
		JvmType.Function ftype = new JvmType.Function(T_VOID,
				JAVA_LANG_OBJECT_ARRAY);
		bytecodes.add(new Bytecode.Invoke(lambdaClassType, "<init>", ftype,
				Bytecode.InvokeMode.SPECIAL));

		// Sixth, assign newly created lambda object to target register
		JvmType.Clazz clazz = (JvmType.Clazz) convertUnderlyingType(c.type());
		bytecodes.add(new Bytecode.Store(c.target(), clazz));
	}

	private void translate(CodeBlock.Index index, Codes.Invoke c, int freeSlot,
			ArrayList<Bytecode> bytecodes) {

		for (int i = 0; i != c.operands().length; ++i) {
			int register = c.operands()[i];
			JvmType parameterType = convertUnderlyingType(c.type().params()
					.get(i));
			bytecodes.add(new Bytecode.Load(register, parameterType));
		}

		Path.ID mid = c.name.module();
		String mangled = nameMangle(c.name.name(), c.type());
		JvmType.Clazz owner = new JvmType.Clazz(mid.parent().toString()
				.replace('/', '.'), mid.last());
		JvmType.Function type = convertFunType(c.type());
		bytecodes.add(new Bytecode.Invoke(owner, mangled, type,
				Bytecode.InvokeMode.STATIC));

		// now, handle the case of an invoke which returns a value that should
		// be discarded.
		if (c.target() != Codes.NULL_REG) {
			bytecodes.add(new Bytecode.Store(c.target(),
					convertUnderlyingType(c.type().ret())));
		} else if (c.target() == Codes.NULL_REG
				&& c.type().ret() != Type.T_VOID) {
			bytecodes.add(new Bytecode.Pop(
					convertUnderlyingType(c.type().ret())));
		}
	}

	private void translate(CodeBlock.Index index, Codes.IndirectInvoke c,
			int freeSlot, ArrayList<Bytecode> bytecodes) {

		Type.FunctionOrMethod ft = c.type();
		JvmType.Clazz owner = (JvmType.Clazz) convertUnderlyingType(ft);
		bytecodes.add(new Bytecode.Load(c.reference(),
				convertUnderlyingType(ft)));
		bytecodes.add(new Bytecode.LoadConst(ft.params().size()));
		bytecodes.add(new Bytecode.New(JAVA_LANG_OBJECT_ARRAY));

		int[] parameters = c.parameters();
		for (int i = 0; i != parameters.length; ++i) {
			int register = parameters[i];
			Type pt = c.type().params().get(i);
			JvmType jpt = convertUnderlyingType(pt);
			bytecodes.add(new Bytecode.Dup(JAVA_LANG_OBJECT_ARRAY));
			bytecodes.add(new Bytecode.LoadConst(i));
			bytecodes.add(new Bytecode.Load(register, jpt));
			addWriteConversion(pt, bytecodes);
			bytecodes.add(new Bytecode.ArrayStore(JAVA_LANG_OBJECT_ARRAY));
		}

		JvmType.Function type = new JvmType.Function(JAVA_LANG_OBJECT,
				JAVA_LANG_OBJECT_ARRAY);

		bytecodes.add(new Bytecode.Invoke(owner, "call", type,
				Bytecode.InvokeMode.VIRTUAL));

		// now, handle the case of an invoke which returns a value that should
		// be discarded.
		if (c.target() != Codes.NULL_REG) {
			addReadConversion(ft.ret(), bytecodes);
			bytecodes.add(new Bytecode.Store(c.target(),
					convertUnderlyingType(c.type().ret())));
		} else if (c.target() == Codes.NULL_REG) {
			bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
		}
	}

	private void translate(Constant v, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
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
		} else if (v instanceof Constant.Decimal) {
			translate((Constant.Decimal) v, freeSlot, bytecodes);
		} else if (v instanceof Constant.List) {
			translate((Constant.List) v, freeSlot, lambdas, bytecodes);
		} else if (v instanceof Constant.Record) {
			translate((Constant.Record) v, freeSlot, lambdas, bytecodes);
		} else if (v instanceof Constant.Tuple) {
			translate((Constant.Tuple) v, freeSlot, lambdas, bytecodes);
		} else if (v instanceof Constant.Lambda) {
			translate((Constant.Lambda) v, freeSlot, lambdas, bytecodes);
		} else {
			throw new IllegalArgumentException("unknown value encountered:" + v);
		}
	}

	protected void translate(Constant.Null e, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.LoadConst(null));
	}

	protected void translate(Constant.Bool e, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		if (e.value) {
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
			writer.write(e.type);
			writer.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}

		bytecodes.add(new Bytecode.LoadConst(jout.toString()));
		JvmType.Function ftype = new JvmType.Function(WHILEYTYPE,
				JAVA_LANG_STRING);
		bytecodes.add(new Bytecode.Invoke(WHILEYTYPE, "valueOf", ftype,
				Bytecode.InvokeMode.STATIC));
	}

	protected void translate(Constant.Byte e, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.LoadConst(e.value));
		JvmType.Function ftype = new JvmType.Function(WHILEYBYTE, T_BYTE);
		bytecodes.add(new Bytecode.Invoke(WHILEYBYTE, "valueOf", ftype,
				Bytecode.InvokeMode.STATIC));
	}

	protected void translate(Constant.Integer e, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		BigInteger num = e.value;

		if (num.bitLength() < 32) {
			bytecodes.add(new Bytecode.LoadConst(num.intValue()));
			bytecodes.add(new Bytecode.Conversion(T_INT, T_LONG));
			JvmType.Function ftype = new JvmType.Function(WHILEYINT, T_LONG);
			bytecodes.add(new Bytecode.Invoke(WHILEYINT, "valueOf", ftype,
					Bytecode.InvokeMode.STATIC));
		} else if (num.bitLength() < 64) {
			bytecodes.add(new Bytecode.LoadConst(num.longValue()));
			JvmType.Function ftype = new JvmType.Function(WHILEYINT, T_LONG);
			bytecodes.add(new Bytecode.Invoke(WHILEYINT, "valueOf", ftype,
					Bytecode.InvokeMode.STATIC));
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
			bytecodes.add(new Bytecode.Invoke(WHILEYINT, "<init>", ftype,
					Bytecode.InvokeMode.SPECIAL));
		}

	}

	protected void translate(Constant.Decimal e, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		BigRational rat = new BigRational(e.value);
		BigInteger den = rat.denominator();
		BigInteger num = rat.numerator();
		if (rat.isInteger()) {
			// this
			if (num.bitLength() < 32) {
				bytecodes.add(new Bytecode.LoadConst(num.intValue()));
				JvmType.Function ftype = new JvmType.Function(WHILEYRAT, T_INT);
				bytecodes.add(new Bytecode.Invoke(WHILEYRAT, "valueOf", ftype,
						Bytecode.InvokeMode.STATIC));
			} else if (num.bitLength() < 64) {
				bytecodes.add(new Bytecode.LoadConst(num.longValue()));
				JvmType.Function ftype = new JvmType.Function(WHILEYRAT, T_LONG);
				bytecodes.add(new Bytecode.Invoke(WHILEYRAT, "valueOf", ftype,
						Bytecode.InvokeMode.STATIC));
			} else {
				// in this context, we need to use a byte array to construct the
				// integer object.
				byte[] bytes = num.toByteArray();
				JvmType.Array bat = new JvmType.Array(JvmTypes.T_BYTE);

				bytecodes.add(new Bytecode.New(WHILEYRAT));
				bytecodes.add(new Bytecode.Dup(WHILEYRAT));
				bytecodes.add(new Bytecode.LoadConst(bytes.length));
				bytecodes.add(new Bytecode.New(bat));
				for (int i = 0; i != bytes.length; ++i) {
					bytecodes.add(new Bytecode.Dup(bat));
					bytecodes.add(new Bytecode.LoadConst(i));
					bytecodes.add(new Bytecode.LoadConst(bytes[i]));
					bytecodes.add(new Bytecode.ArrayStore(bat));
				}

				JvmType.Function ftype = new JvmType.Function(T_VOID, bat);
				bytecodes.add(new Bytecode.Invoke(WHILEYRAT, "<init>", ftype,
						Bytecode.InvokeMode.SPECIAL));
			}
		} else if (num.bitLength() < 32 && den.bitLength() < 32) {
			bytecodes.add(new Bytecode.LoadConst(num.intValue()));
			bytecodes.add(new Bytecode.LoadConst(den.intValue()));
			JvmType.Function ftype = new JvmType.Function(WHILEYRAT, T_INT,
					T_INT);
			bytecodes.add(new Bytecode.Invoke(WHILEYRAT, "valueOf", ftype,
					Bytecode.InvokeMode.STATIC));
		} else if (num.bitLength() < 64 && den.bitLength() < 64) {
			bytecodes.add(new Bytecode.LoadConst(num.longValue()));
			bytecodes.add(new Bytecode.LoadConst(den.longValue()));
			JvmType.Function ftype = new JvmType.Function(WHILEYRAT, T_LONG,
					T_LONG);
			bytecodes.add(new Bytecode.Invoke(WHILEYRAT, "valueOf", ftype,
					Bytecode.InvokeMode.STATIC));
		} else {
			// First, do numerator bytes
			byte[] bytes = num.toByteArray();
			JvmType.Array bat = new JvmType.Array(JvmTypes.T_BYTE);

			bytecodes.add(new Bytecode.New(WHILEYRAT));
			bytecodes.add(new Bytecode.Dup(WHILEYRAT));
			bytecodes.add(new Bytecode.LoadConst(bytes.length));
			bytecodes.add(new Bytecode.New(bat));
			for (int i = 0; i != bytes.length; ++i) {
				bytecodes.add(new Bytecode.Dup(bat));
				bytecodes.add(new Bytecode.LoadConst(i));
				bytecodes.add(new Bytecode.LoadConst(bytes[i]));
				bytecodes.add(new Bytecode.ArrayStore(bat));
			}

			// Second, do denominator bytes
			bytes = den.toByteArray();
			bytecodes.add(new Bytecode.LoadConst(bytes.length));
			bytecodes.add(new Bytecode.New(bat));
			for (int i = 0; i != bytes.length; ++i) {
				bytecodes.add(new Bytecode.Dup(bat));
				bytecodes.add(new Bytecode.LoadConst(i));
				bytecodes.add(new Bytecode.LoadConst(bytes[i]));
				bytecodes.add(new Bytecode.ArrayStore(bat));
			}

			// Finally, construct BigRational object
			JvmType.Function ftype = new JvmType.Function(T_VOID, bat, bat);
			bytecodes.add(new Bytecode.Invoke(WHILEYRAT, "<init>", ftype,
					Bytecode.InvokeMode.SPECIAL));
		}
	}

	protected void translate(Constant.List lv, int freeSlot,
			ArrayList<ClassFile> lambdas, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.New(WHILEYLIST));
		bytecodes.add(new Bytecode.Dup(WHILEYLIST));
		bytecodes.add(new Bytecode.LoadConst(lv.values.size()));
		JvmType.Function ftype = new JvmType.Function(T_VOID, T_INT);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "<init>", ftype,
				Bytecode.InvokeMode.SPECIAL));

		ftype = new JvmType.Function(T_BOOL, JAVA_LANG_OBJECT);
		for (Constant e : lv.values) {
			bytecodes.add(new Bytecode.Dup(WHILEYLIST));
			translate(e, freeSlot, bytecodes);
			addWriteConversion(e.type(), bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "add", ftype,
					Bytecode.InvokeMode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(T_BOOL));
		}
	}

	protected void translate(Constant.Tuple lv, int freeSlot,
			ArrayList<ClassFile> lambdas, ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.New(WHILEYTUPLE));
		bytecodes.add(new Bytecode.Dup(WHILEYTUPLE));
		bytecodes.add(new Bytecode.LoadConst(lv.values.size()));
		JvmType.Function ftype = new JvmType.Function(T_VOID, T_INT);
		bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE, "<init>", ftype,
				Bytecode.InvokeMode.SPECIAL));

		ftype = new JvmType.Function(T_BOOL, JAVA_LANG_OBJECT);
		for (Constant e : lv.values) {
			bytecodes.add(new Bytecode.Dup(WHILEYTUPLE));
			translate(e, freeSlot, bytecodes);
			addWriteConversion(e.type(), bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE, "add", ftype,
					Bytecode.InvokeMode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(T_BOOL));
		}
	}

	protected void translate(Constant.Record expr, int freeSlot,
			ArrayList<ClassFile> lambdas, ArrayList<Bytecode> bytecodes) {
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
				JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);
		construct(WHILEYRECORD, freeSlot, bytecodes);
		for (Map.Entry<String, Constant> e : expr.values.entrySet()) {
			Type et = e.getValue().type();
			bytecodes.add(new Bytecode.Dup(WHILEYRECORD));
			bytecodes.add(new Bytecode.LoadConst(e.getKey()));
			translate(e.getValue(), freeSlot, bytecodes);
			addWriteConversion(et, bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYRECORD, "put", ftype,
					Bytecode.InvokeMode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
		}
	}
	
	protected void translate(Constant.Lambda c, int freeSlot,
			ArrayList<ClassFile> lambdas, ArrayList<Bytecode> bytecodes) {

		// First, build and register lambda class which calls the given function
		// or method. This class will extend class wyjc.runtime.WyLambda.
		int lambda_id = lambdas.size();
		lambdas.add(buildLambda(c.name, c.type, lambda_id));

		// Second, create and duplicate new lambda object. This will then stay
		// on the stack (whilst the parameters are constructed) until the
		// object's constructor is called.
		JvmType.Clazz lambdaClassType = new JvmType.Clazz(owner.pkg(), owner
				.lastComponent().first(), Integer.toString(lambda_id));

		bytecodes.add(new Bytecode.New(lambdaClassType));
		bytecodes.add(new Bytecode.Dup(lambdaClassType));

		// Third, invoke lambda class constructor
		JvmType.Function ftype = new JvmType.Function(T_VOID,
				JAVA_LANG_OBJECT_ARRAY);
		bytecodes.add(new Bytecode.LoadConst(null));
		bytecodes.add(new Bytecode.Invoke(lambdaClassType, "<init>", ftype,
				Bytecode.InvokeMode.SPECIAL));
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
	protected ClassFile buildLambda(NameID name, Type.FunctionOrMethod type,
			int id) {
		// === (1) Determine the fully qualified type of the lambda class ===

		// start with fully qualified type of this class.
		JvmType.Clazz lambdaClassType = new JvmType.Clazz(owner.pkg(), owner
				.lastComponent().first(), Integer.toString(id));

		// === (2) Construct an empty class ===
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.ACC_PUBLIC);
		modifiers.add(Modifier.ACC_FINAL);
		ClassFile cf = new ClassFile(CLASS_VERSION, lambdaClassType,
				WHILEYLAMBDA, new ArrayList<JvmType.Clazz>(), modifiers);

		// === (3) Add constructor ===
		modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.ACC_PUBLIC);
		JvmType.Function constructorType = new JvmType.Function(
				JvmTypes.T_VOID, JAVA_LANG_OBJECT_ARRAY);
		// Create constructor method
		ClassFile.Method constructor = new ClassFile.Method("<init>",
				constructorType, modifiers);
		cf.methods().add(constructor);
		// Create body of constructor
		ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
		bytecodes.add(new Bytecode.Load(0, lambdaClassType));
		bytecodes.add(new Bytecode.Load(1, JAVA_LANG_OBJECT_ARRAY));
		bytecodes.add(new Bytecode.Invoke(WHILEYLAMBDA, "<init>",
				constructorType, Bytecode.InvokeMode.SPECIAL));
		bytecodes.add(new Bytecode.Return(null));
		// Add code attribute to constructor
		jasm.attributes.Code code = new jasm.attributes.Code(bytecodes,
				new ArrayList<Handler>(), constructor);
		constructor.attributes().add(code);

		// === (4) Add implementation of WyLambda.call(Object[]) ===
		modifiers = new ArrayList<Modifier>();
		modifiers.add(Modifier.ACC_PUBLIC);
		modifiers.add(Modifier.ACC_FINAL);
		JvmType.Function callFnType = new JvmType.Function(
				JvmTypes.JAVA_LANG_OBJECT, JAVA_LANG_OBJECT_ARRAY);
		// Create constructor method
		ClassFile.Method callFn = new ClassFile.Method("call", callFnType,
				modifiers);
		cf.methods().add(callFn);
		// Create body of call method
		bytecodes = new ArrayList<Bytecode>();
		// Call WyFunction.bindParameters()
		bytecodes.add(new Bytecode.Load(0, lambdaClassType));
		bytecodes.add(new Bytecode.Load(1, JAVA_LANG_OBJECT_ARRAY));
		bytecodes.add(new Bytecode.Invoke(WHILEYLAMBDA, "bindParameters",
				new JvmType.Function(JAVA_LANG_OBJECT_ARRAY,
						JAVA_LANG_OBJECT_ARRAY), Bytecode.InvokeMode.VIRTUAL));
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
		JvmType.Clazz owner = new JvmType.Clazz(mid.parent().toString()
				.replace('/', '.'), mid.last());
		JvmType.Function fnType = convertFunType(type);
		bytecodes.add(new Bytecode.Invoke(owner, mangled, fnType,
				Bytecode.InvokeMode.STATIC));
		if (type.ret() instanceof Type.Void) {
			// Called function doesn't return anything, but we have to.
			// Therefore, push on dummy null value.
			bytecodes.add(new Bytecode.LoadConst(null));
		} else {
			addWriteConversion(type.ret(), bytecodes);
		}

		bytecodes.add(new Bytecode.Return(JAVA_LANG_OBJECT));

		// Add code attribute to call method
		code = new jasm.attributes.Code(bytecodes, new ArrayList<Handler>(),
				callFn);
		callFn.attributes().add(code);

		// Done
		return cf;
	}

	protected void addCoercion(Type from, Type to, int freeSlot,
			HashMap<JvmConstant, Integer> constants,
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
			buildCoercion((Type.Int) from, to, freeSlot, bytecodes);
		} else {
			// ok, it's a harder case so we use an explicit coercion function
			int id = JvmCoercion.get(from, to, constants);
			String name = "coercion$" + id;
			JvmType.Function ft = new JvmType.Function(
					convertUnderlyingType(to), convertUnderlyingType(from));
			bytecodes.add(new Bytecode.Invoke(owner, name, ft,
					Bytecode.InvokeMode.STATIC));
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

	private void buildCoercion(Type.Int fromType, Type toType, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		// coercion required!
		JvmType.Function ftype = new JvmType.Function(WHILEYRAT, WHILEYINT);
		bytecodes.add(new Bytecode.Invoke(WHILEYRAT, "valueOf", ftype,
				Bytecode.InvokeMode.STATIC));
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
		if (from instanceof Type.Tuple && to instanceof Type.Tuple) {
			buildCoercion((Type.Tuple) from, (Type.Tuple) to, freeSlot,
					constants, bytecodes);
		} else if (from instanceof Type.Reference
				&& to instanceof Type.Reference) {
			// TODO
		} else if (from instanceof Type.List && to instanceof Type.List) {
			buildCoercion((Type.List) from, (Type.List) to, freeSlot,
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

	protected void buildCoercion(Type.Tuple fromType, Type.Tuple toType,
			int freeSlot, HashMap<JvmConstant, Integer> constants,
			ArrayList<Bytecode> bytecodes) {
		int oldSlot = freeSlot++;
		int newSlot = freeSlot++;
		bytecodes.add(new Bytecode.Store(oldSlot, WHILEYTUPLE));
		construct(WHILEYTUPLE, freeSlot, bytecodes);
		bytecodes.add(new Bytecode.Store(newSlot, WHILEYTUPLE));
		List<Type> from_elements = fromType.elements();
		List<Type> to_elements = toType.elements();
		for (int i = 0; i != to_elements.size(); ++i) {
			Type from = from_elements.get(i);
			Type to = to_elements.get(i);
			bytecodes.add(new Bytecode.Load(newSlot, WHILEYTUPLE));
			bytecodes.add(new Bytecode.Load(oldSlot, WHILEYTUPLE));
			bytecodes.add(new Bytecode.LoadConst(i));
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT,
					T_INT);
			bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE, "get", ftype,
					Bytecode.InvokeMode.VIRTUAL));
			addReadConversion(from, bytecodes);
			// now perform recursive conversion
			addCoercion(from, to, freeSlot, constants, bytecodes);
			ftype = new JvmType.Function(T_BOOL, JAVA_LANG_OBJECT);
			bytecodes.add(new Bytecode.Invoke(WHILEYTUPLE, "add", ftype,
					Bytecode.InvokeMode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(T_BOOL));
		}
		bytecodes.add(new Bytecode.Load(newSlot, WHILEYTUPLE));
	}

	protected void buildCoercion(Type.List fromType, Type.List toType,
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
		construct(WHILEYLIST, freeSlot, bytecodes);
		bytecodes.add(new Bytecode.Store(tmp, WHILEYLIST));
		bytecodes.add(new Bytecode.Label(loopLabel));
		ftype = new JvmType.Function(T_BOOL);
		bytecodes.add(new Bytecode.Load(iter, JAVA_UTIL_ITERATOR));
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "hasNext", ftype,
				Bytecode.InvokeMode.INTERFACE));
		bytecodes.add(new Bytecode.If(Bytecode.IfMode.EQ, exitLabel));
		bytecodes.add(new Bytecode.Load(tmp, WHILEYLIST));
		bytecodes.add(new Bytecode.Load(iter, JAVA_UTIL_ITERATOR));
		ftype = new JvmType.Function(JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "next", ftype,
				Bytecode.InvokeMode.INTERFACE));
		addReadConversion(fromType.element(), bytecodes);
		addCoercion(fromType.element(), toType.element(), freeSlot, constants,
				bytecodes);
		ftype = new JvmType.Function(T_BOOL, JAVA_LANG_OBJECT);
		bytecodes.add(new Bytecode.Invoke(WHILEYLIST, "add", ftype,
				Bytecode.InvokeMode.VIRTUAL));
		bytecodes.add(new Bytecode.Pop(T_BOOL));
		bytecodes.add(new Bytecode.Goto(loopLabel));
		bytecodes.add(new Bytecode.Label(exitLabel));
		bytecodes.add(new Bytecode.Load(tmp, WHILEYLIST));
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
	private void construct(JvmType.Clazz owner, int freeSlot,
			ArrayList<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.New(owner));
		bytecodes.add(new Bytecode.Dup(owner));
		ArrayList<JvmType> paramTypes = new ArrayList<JvmType>();
		JvmType.Function ftype = new JvmType.Function(T_VOID, paramTypes);
		bytecodes.add(new Bytecode.Invoke(owner, "<init>", ftype,
				Bytecode.InvokeMode.SPECIAL));
	}

	private final static Type WHILEY_SYSTEM_T = Type.Nominal(new NameID(Trie
			.fromString("whiley/lang/System"), "Console"));

	private final static JvmType.Clazz WHILEYUTIL = new JvmType.Clazz(
			"wyjc.runtime", "Util");
	private final static JvmType.Clazz WHILEYLIST = new JvmType.Clazz(
			"wyjc.runtime", "WyList");
	private final static JvmType.Clazz WHILEYTUPLE = new JvmType.Clazz(
			"wyjc.runtime", "WyTuple");
	private final static JvmType.Clazz WHILEYTYPE = new JvmType.Clazz(
			"wyjc.runtime", "WyType");
	private final static JvmType.Clazz WHILEYRECORD = new JvmType.Clazz(
			"wyjc.runtime", "WyRecord");
	private final static JvmType.Clazz WHILEYOBJECT = new JvmType.Clazz(
			"wyjc.runtime", "WyObject");
	private final static JvmType.Clazz WHILEYBOOL = new JvmType.Clazz(
			"wyjc.runtime", "WyBool");
	private final static JvmType.Clazz WHILEYBYTE = new JvmType.Clazz(
			"wyjc.runtime", "WyByte");
	private final static JvmType.Clazz WHILEYINT = new JvmType.Clazz(
			"java.math", "BigInteger");
	private final static JvmType.Clazz WHILEYRAT = new JvmType.Clazz(
			"wyjc.runtime", "WyRat");
	private final static JvmType.Clazz WHILEYLAMBDA = new JvmType.Clazz(
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
		JvmType rt = convertUnderlyingType(ft.ret());
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
		} else if (t instanceof Type.Real) {
			return WHILEYRAT;
		} else if (t instanceof Type.Meta) {
			return WHILEYTYPE;
		} else if (t instanceof Type.EffectiveList) {
			return WHILEYLIST;
		} else if (t instanceof Type.EffectiveRecord) {
			return WHILEYRECORD;
		} else if (t instanceof Type.EffectiveTuple) {
			return WHILEYTUPLE;
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

	/*
	 * public static void testMangle1(Type.Fun ft) throws IOException {
	 * IdentifierOutputStream jout = new IdentifierOutputStream();
	 * BinaryOutputStream binout = new BinaryOutputStream(jout);
	 * Types.BinaryWriter tm = new Types.BinaryWriter(binout);
	 * Type.build(tm,ft); binout.close(); System.out.println("MANGLED: " + ft +
	 * " => " + jout.toString()); Type.Fun type = (Type.Fun) new
	 * Types.BinaryReader( new BinaryInputStream(new IdentifierInputStream(
	 * jout.toString()))).read(); System.out.println("UNMANGLED TO: " + type);
	 * if(!type.equals(ft)) { throw new
	 * RuntimeException("INVALID TYPE RECONSTRUCTED"); } }
	 */
}
