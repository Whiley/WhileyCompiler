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
import wycc.lang.Attribute;
import wycc.lang.NameID;
import wycc.lang.SyntaxError.InternalFailure;
import wycc.util.Logger;
import wycc.util.Pair;
import wyfs.io.BinaryOutputStream;
import wyfs.lang.Path;
import wyfs.util.Trie;
import wyil.lang.*;
import wyil.lang.Constant;
import wyil.lang.Bytecode.Operator;

import static wyil.lang.Bytecode.*;
import wyil.util.TypeExpander;

import static wyil.util.ErrorMessages.internalFailure;
import static wyjc.Wyil2JavaBuilder.WHILEYBOOL;
import static wyjc.Wyil2JavaBuilder.WHILEYUTIL;

import wyjc.util.BytecodeTranslators;
import wyjc.util.LambdaTemplate;
import wyjc.util.WyjcBuildTask;
import jasm.attributes.LineNumberTable;
import jasm.attributes.SourceFile;
import jasm.lang.*;
import jasm.lang.Bytecode;
import jasm.lang.Modifier;
import static jasm.lang.Modifier.*;
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
	private ArrayList<ClassFile> lambdaClasses;

	/**
	 * List of temporary methods created to implement lambda expressions
	 */
	private ArrayList<ClassFile.Method> lambdaMethods;
	
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

	public Set<Path.Entry<?>> build(Collection<Pair<Path.Entry<?>, Path.Root>> delta) throws IOException {

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
			Path.Entry<ClassFile> df = dst.create(sf.id(), WyjcBuildTask.ContentType);
			generatedFiles.add(df);

			// Translate WyilFile into JVM ClassFile
			
			// FIXME: put these in the context?
			
			lambdaClasses = new ArrayList<ClassFile>();
			lambdaMethods = new ArrayList<ClassFile.Method>();
			ClassFile contents = build(sf.read());
			contents.methods().addAll(lambdaMethods);

			// Verify the generated file being written
			new ClassFileVerifier().apply(contents);

			// Write class file into its destination
			df.write(contents);

			// Finally, write out any lambda classes + methods created to
			// support the main class. This is necessary because every
			// occurrence of a lambda expression in the WyilFile generates an
			// inner class responsible for calling the given function.
			Path.ID parent = df.id();
			Path.ID pkg = parent.subpath(0, parent.size() - 1);
			for (int i = 0; i != lambdaClasses.size(); ++i) {
				Path.ID id = pkg.append(parent.last() + "$" + i);
				Path.Entry<ClassFile> lf = dst.create(id, WyjcBuildTask.ContentType);
				ClassFile lc = lambdaClasses.get(i);
				// Verify generated class file
				new ClassFileVerifier().apply(lc);
				lf.write(lc);
				generatedFiles.add(lf);
			}			
		}

		// ========================================================================
		// Done
		// ========================================================================

		long endTime = System.currentTimeMillis();
		logger.logTimedMessage("Wyil => Java: compiled " + delta.size() + " file(s)", endTime - start,
				memory - runtime.freeMemory());

		return generatedFiles;
	}

	private ClassFile build(WyilFile module) {
		owner = new JvmType.Clazz(module.id().parent().toString().replace('.', '/'), module.id().last());
		List<Modifier> modifiers = modifiers(ACC_PUBLIC, ACC_FINAL);
		ClassFile cf = new ClassFile(CLASS_VERSION, owner, JAVA_LANG_OBJECT, new ArrayList<JvmType.Clazz>(), modifiers);

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

		buildConstants(constants, lambdaClasses, cf);

		if (addMainLauncher) {
			cf.methods().add(buildMainLauncher(owner));
		}

		return cf;
	}

	private void buildConstants(HashMap<JvmConstant, Integer> constants, ArrayList<ClassFile> lambdas, ClassFile cf) {
		buildValues(constants, lambdas, cf);
	}

	private void buildValues(HashMap<JvmConstant, Integer> constants, ArrayList<ClassFile> lambdas, ClassFile cf) {
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
				List<Modifier> fmods = modifiers(ACC_PRIVATE, ACC_STATIC, ACC_FINAL);
				JvmType type = convertUnderlyingType(constant.type());
				ClassFile.Field field = new ClassFile.Field(name, type, fmods);
				cf.fields().add(field);
				// Now, create code to intialise this field
				translateConstant(constant, bytecodes);
				bytecodes.add(new Bytecode.PutField(owner, name, type, Bytecode.FieldMode.STATIC));
			}
		}

		if (nvalues > 0) {
			// create static initialiser method, but only if we really need to.
			bytecodes.add(new Bytecode.Return(null));
			List<Modifier> modifiers = modifiers(ACC_PUBLIC, ACC_STATIC, ACC_SYNTHETIC);
			JvmType.Function ftype = new JvmType.Function(new JvmType.Void());
			ClassFile.Method clinit = new ClassFile.Method("<clinit>", ftype, modifiers);
			cf.methods().add(clinit);

			// finally add code for staticinitialiser method
			jasm.attributes.Code code = new jasm.attributes.Code(bytecodes, new ArrayList(), clinit);
			clinit.attributes().add(code);
		}
	}

	private ClassFile.Method buildMainLauncher(JvmType.Clazz owner) {
		List<Modifier> modifiers = modifiers(ACC_PUBLIC, ACC_SYNTHETIC);
		JvmType.Function ft1 = new JvmType.Function(T_VOID, new JvmType.Array(JAVA_LANG_STRING));
		ClassFile.Method cm = new ClassFile.Method("main", ft1, modifiers);
		JvmType.Array strArr = new JvmType.Array(JAVA_LANG_STRING);
		ArrayList<Bytecode> codes = new ArrayList<Bytecode>();
		ft1 = new JvmType.Function(WHILEYRECORD, new JvmType.Array(JAVA_LANG_STRING));
		codes.add(new Bytecode.Load(0, strArr));
		codes.add(new Bytecode.Invoke(WHILEYUTIL, "systemConsole", ft1, Bytecode.InvokeMode.STATIC));
		Type.Method wyft = Type.Method(new Type[0], Collections.<String> emptySet(), Collections.<String> emptyList(),
				WHILEY_SYSTEM_T);
		JvmType.Function ft3 = convertFunType(wyft);
		codes.add(new Bytecode.Invoke(owner, nameMangle("main", wyft), ft3, Bytecode.InvokeMode.STATIC));
		codes.add(new Bytecode.Return(null));

		jasm.attributes.Code code = new jasm.attributes.Code(codes, new ArrayList(), cm);
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
		List<Modifier> modifiers = modifiers(ACC_PUBLIC, ACC_STATIC, ACC_SYNTHETIC);
		JvmType.Function funType = new JvmType.Function(T_BOOL, underlyingType);
		ClassFile.Method cm = new ClassFile.Method(td.name() + "$typeof", funType, modifiers);
		// Generate code for testing elements of type (if any)
		BytecodeForest invariant = td.invariant();
		Context context = new Context(-1, invariant);
		if (invariant.numRoots() > 0) {
			// In this case, there is one or more invariants to check. To do
			// this, we chain them together into a sequence of checks.
			String falseLabel = freshLabel();
			for (int i = 0; i != invariant.numRoots(); ++i) {
				translateOperand(invariant.getRoot(i), context);
				JvmType.Function ft = new JvmType.Function(JvmTypes.T_BOOL);
				context.add(new Bytecode.Invoke(WHILEYBOOL, "value", ft, Bytecode.InvokeMode.VIRTUAL));
				if ((i + 1) == invariant.numRoots()) {
					// This indicates the end of the chain, so we can just
					// return what we have here.
					context.add(new Bytecode.Return(new JvmType.Bool()));
				} else {
					// This indicates an internal point in the chain, in which
					// case we can terminate early if we already know the
					// invariant doesn't hold.
					context.add(new Bytecode.If(Bytecode.IfMode.EQ, falseLabel));
				}
			}
			if (invariant.numRoots() > 1) {
				// The following handles cases where one of the checks has
				// failed. We only need it if more than one invariant was
				// chained together.
				context.add(new Bytecode.Label(falseLabel));
				context.add(new Bytecode.LoadConst(false));
				context.add(new Bytecode.Return(new JvmType.Bool()));
			}
		} else {
			// No, there are no invariants for this type. Hence, we just return
			// true to indicate success.
			context.add(new Bytecode.LoadConst(true));
			context.add(new Bytecode.Return(new JvmType.Bool()));
		}
		// Done
		jasm.attributes.Code code = new jasm.attributes.Code(context.getBytecodes(), new ArrayList(), cm);
		cm.attributes().add(code);
		return cm;
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

	/**
	 * Construct a trampoline for handling native or export methods. In the
	 * context of native methods, this generates a method which redirects to a
	 * method which has the same name, but with "$native" appended on the end.
	 * This method is implemented elsewhere and must be provided by the author
	 * of this module. For export methods, this means providing a method without
	 * name mangling which redirects to the method with name mangling.
	 * 
	 * @param method
	 * @param constants
	 * @return
	 */
	private ClassFile.Method buildNativeOrExport(WyilFile.FunctionOrMethod method,
			HashMap<JvmConstant, Integer> constants) {
		// Preliminaries
		List<Modifier> modifiers = modifiers(ACC_STATIC,
				method.hasModifier(wyil.lang.Modifier.PUBLIC) ? ACC_PUBLIC : null);
		JvmType.Function ft = convertFunType(method.type());
		// Apply name mangling (if applicable)
		String name = method.name();
		if (method.hasModifier(wyil.lang.Modifier.NATIVE)) {
			name = nameMangle(method.name(), method.type());
		}
		// Construct method body
		ClassFile.Method cm = new ClassFile.Method(name, ft, modifiers);
		ArrayList<Bytecode> codes = translateNativeOrExport(method);
		jasm.attributes.Code code = new jasm.attributes.Code(codes, Collections.EMPTY_LIST, cm);
		cm.attributes().add(code);
		// Done
		return cm;
	}

	/**
	 * Construct the body a "trampoline" method for handling native and export
	 * methods.
	 * 
	 * @param method
	 * @return
	 */
	private ArrayList<Bytecode> translateNativeOrExport(WyilFile.FunctionOrMethod method) {
		ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
		Type.FunctionOrMethod ft = method.type();
		int slot = 0;
		// Load all parameters provided to this method ontot he stack
		for (Type param : ft.params()) {
			bytecodes.add(new Bytecode.Load(slot++, convertUnderlyingType(param)));
		}
		// Determine the target class and method for the invocation
		JvmType.Clazz targetClass;
		String targetMethod;
		if (method.hasModifier(wyil.lang.Modifier.NATIVE)) {
			targetClass = new JvmType.Clazz(owner.pkg(), owner.components().get(0).first(), "native");
			targetMethod = method.name();
		} else {
			targetClass = new JvmType.Clazz(owner.pkg(), owner.components().get(0).first());
			targetMethod = nameMangle(method.name(), method.type());
		}
		// Perform the invocation itself which constitutes the "trampoline"
		bytecodes.add(new Bytecode.Invoke(targetClass, targetMethod, convertFunType(ft), Bytecode.InvokeMode.STATIC));
		// Finally, return any values obtained from the invocation as necessary
		JvmType returnType = null;
		if (!ft.returns().isEmpty()) {
			returnType = convertUnderlyingType(ft.returns().get(0));
		}
		bytecodes.add(new Bytecode.Return(returnType));
		// Done
		return bytecodes;
	}

	/**
	 * Translate a given method or function. This generates a corresponding
	 * function or method on the JVM which has the same name, plus a type
	 * mangle.
	 * 
	 * @param method
	 * @return
	 */
	private ClassFile.Method translate(WyilFile.FunctionOrMethod method) {
		// Preliminaries
		List<Modifier> modifiers = modifiers(ACC_STATIC,
				method.hasModifier(wyil.lang.Modifier.PUBLIC) ? ACC_PUBLIC : null);
		JvmType.Function ft = convertFunType(method.type());
		// Construct the method itself
		String name = nameMangle(method.name(), method.type());
		ClassFile.Method cm = new ClassFile.Method(name, ft, modifiers);
		// Translate the method body
		lineNumbers = new ArrayList<LineNumberTable.Entry>();
		Context context = new Context(method.body(), method.code());
		translateAllWithin(context);
		// Add return bytecode (if necessary)
		addReturnBytecode(context);
		//
		jasm.attributes.Code code = new jasm.attributes.Code(context.getBytecodes(), Collections.EMPTY_LIST, cm);
		if (!lineNumbers.isEmpty()) {
			code.attributes().add(new LineNumberTable(lineNumbers));
		}
		cm.attributes().add(code);
		// Done
		return cm;
	}

	/**
	 * Every JVM method must be terminated by a return bytecode. This method
	 * simply adds one if none was already generated.
	 * 
	 * @param context
	 */
	private void addReturnBytecode(Context context) {
		List<Bytecode> bytecodes = context.getBytecodes();
		if (bytecodes.size() > 0) {
			Bytecode last = bytecodes.get(bytecodes.size() - 1);
			if (last instanceof Bytecode.Return) {
				// No return bytecode is needed
				return;
			}
		}
		// A return bytecode is needed
		bytecodes.add(new Bytecode.Return(null));
	}

	// ===============================================================================
	// Statements & Blocks
	// ===============================================================================

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
	private void translateAllWithin(Context context) {
		while (context.hasNext()) {
			translate(context.getStatement(), context);
			context.nextStatement();
		}
	}

	/**
	 * Translate a WyIL bytecode at a given index into one or more JVM
	 * bytecodes. The bytecode index is given to help with debugging (i.e. to
	 * extract attributes associated with the given bytecode).
	 * 
	 */
	private void translate(wyil.lang.Bytecode.Stmt code, Context context) {
		try {
			switch (code.opcode()) {
			case OPCODE_assert:
			case OPCODE_assume:
				translate((AssertOrAssume) code, context);
				break;
			case OPCODE_assign:
				translate((Assign) code, context);
				break;
			case OPCODE_break:
				translate((Break) code, context);
				break;
			case OPCODE_continue:
				translate((Continue) code, context);
				break;
			case OPCODE_debug:
				translate((Debug) code, context);
				break;
			case OPCODE_dowhile:
				translate((DoWhile) code, context);
				break;
			case OPCODE_fail:
				translate((Fail) code, context);
				break;
			case OPCODE_if:
				translate((If) code, context);
				break;
			case OPCODE_while:
				translate((While) code, context);
				break;
			case OPCODE_return:
				translate((Return) code, context);
				break;
			case OPCODE_switch:
				translate((Switch) code, context);
				break;
			default:
				internalFailure("unknown wyil code encountered (" + code + ")", filename,
						context.attribute(Attribute.Source.class));
			}
		} catch (InternalFailure ex) {
			throw ex;
		} catch (Exception ex) {
			internalFailure(ex.getMessage(), filename, ex, context.attribute(Attribute.Source.class));
		}
	}

	private void translate(AssertOrAssume c, Context context) {
		String trueLabel = freshLabel();
		translateCondition(c.operand(), trueLabel, null, context);
		construct(JAVA_LANG_RUNTIMEEXCEPTION, context.getBytecodes());
		context.add(new Bytecode.Throw());
		context.add(new Bytecode.Label(trueLabel));
	}

	/**
	 * Translate an assignment statement.
	 * 
	 * @param code
	 * @param context
	 */
	private void translate(Assign code, Context context) {
		int[] lhs = code.leftHandSide();
		int[] rhs = code.rightHandSide();
		// Translate and construct the lvals for this assignment. This
		// will store all lval operands (e.g. array indices) into their
		// corresponding operand register. To preserve the semantics of Whiley,
		// we must translate the lhs before the rhs.
		LVal[] lvals = translateLVals(lhs, context);
		// Translate the operands in reverse order onto the stack, produce a
		// type for each operand produced. The number of types may be larger
		// than the number of rhs opeands in the case of an operand with
		// multiple return values.
		List<JvmType> types = translateOperands(rhs, context);

		if (types.size() > rhs.length) {
			// Should ensure multiple returns span multiple operands, rather
			// than just one as currently so.
			throw new IllegalArgumentException("Implement support for multiple returns");
		}		
		
		// FIXME: need proper support for multiple operands
		
		// Now, store each operand into the slot location so that we can more
		// easily access it later.
		for (int i = types.size() - 1; i >= 0; i = i - 1) {
			context.add(new Bytecode.Store(rhs[i], types.get(i)));
		}
		// Assign each operand to the target lval.
		int i = 0;
		for (; i != lhs.length; ++i) {
			translateSimpleAssign(lvals[i], rhs[i], context);
		}
		// Finally, pop any remaining operands off the stack. This can happen if
		// values are being discarded.
		for (; i < types.size(); ++i) {
			context.add(new Bytecode.Pop(types.get(i)));
		}
	}

	/**
	 * Translate a simple assignment statement. This is one which is assigning a
	 * single value into an lval, which could be either a variable, a field
	 * assignment, an array element assignment or a dereference assignment. The
	 * assigned value is assumed to have already been loaded on the stack.
	 * 
	 * @param lhs
	 * @param rhsType
	 */
	private void translateSimpleAssign(LVal lhs, int rhs, Context context) {
		JvmType type = convertUnderlyingType(context.getLocation(lhs.variable).type(0));
		if (lhs.path.size() > 0) {
			// This is the complex case of an assignment to an element of a
			// compound.
			context.add(new Bytecode.Load(lhs.variable, type));
			translateUpdate(lhs.path.iterator(), rhs, context);
			context.add(new Bytecode.Store(lhs.variable, type));
		} else {
			// This is the simple case of a direct assignment to a single
			// variable.
			context.add(new Bytecode.Load(rhs, type));
			context.add(new Bytecode.Store(lhs.variable, type));
		}
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
	 * @param context
	 */
	private void translateUpdate(Iterator<LVal.Element<?>> iterator, int rhs, Context context) {
		// At this point, we have not yet reached the "innermost" position.
		// Therefore, we keep recursing down the chain of LVals.
		LVal.Element<?> lv = iterator.next();
		if (lv instanceof LVal.Array) {
			translateUpdate((LVal.Array) lv, iterator, rhs, context);
		} else if (lv instanceof LVal.Record) {
			translateUpdate((LVal.Record) lv, iterator, rhs, context);
		} else {
			translateUpdate((LVal.Reference) lv, iterator, rhs, context);
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
	private void translateUpdate(LVal.Array lval, Iterator<LVal.Element<?>> iterator, int rhs, Context context) {
		if (iterator.hasNext()) {
			// This is not the innermost case, hence we read out the current
			// value of the location being assigned and pass that forward to the
			// next stage.
			context.add(new Bytecode.Dup(WHILEYARRAY));
			context.add(new Bytecode.Load(lval.index, WHILEYINT));
			JvmType.Function getFunType = new JvmType.Function(JAVA_LANG_OBJECT, WHILEYARRAY, WHILEYINT);
			context.add(new Bytecode.Invoke(WHILEYARRAY, "internal_get", getFunType, Bytecode.InvokeMode.STATIC));
			addReadConversion(lval.type.element(), context.getBytecodes());
			translateUpdate(iterator, rhs, context);
			context.add(new Bytecode.Load(lval.index, WHILEYINT));
			context.add(new Bytecode.Swap());
		} else {
			// This is the innermost case, hence we can avoid the unnecessary
			// read of the current value and, instead, just return the rhs value
			// directly.
			Type type = lval.type.element();
			context.add(new Bytecode.Load(lval.index, WHILEYINT));
			context.add(new Bytecode.Load(rhs, convertUnderlyingType(type)));
			addWriteConversion(type, context.getBytecodes());
		}
		JvmType.Function setFunType = new JvmType.Function(WHILEYARRAY, WHILEYARRAY, WHILEYINT, JAVA_LANG_OBJECT);
		context.add(new Bytecode.Invoke(WHILEYARRAY, "set", setFunType, Bytecode.InvokeMode.STATIC));
	}

	private void translateUpdate(LVal.Record lval, Iterator<LVal.Element<?>> iterator, int rhs, Context context) {
		Type.EffectiveRecord type = lval.type;
		if (iterator.hasNext()) {
			// This is not the innermost case, hence we read out the current
			// value of the location being assigned and pass that forward to the
			// next stage.
			context.add(new Bytecode.Dup(WHILEYRECORD));
			context.add(new Bytecode.LoadConst(lval.field));
			JvmType.Function getFunType = new JvmType.Function(JAVA_LANG_OBJECT, WHILEYRECORD, JAVA_LANG_STRING);
			context.add(new Bytecode.Invoke(WHILEYRECORD, "internal_get", getFunType, Bytecode.InvokeMode.STATIC));
			addReadConversion(type.field(lval.field), context.getBytecodes());
			translateUpdate(iterator, rhs, context);
			context.add(new Bytecode.LoadConst(lval.field));
			context.add(new Bytecode.Swap());
		} else {
			// This is the innermost case, hence we can avoid the unnecessary
			// read of the current value and, instead, just return the rhs value
			// directly.
			context.add(new Bytecode.LoadConst(lval.field));
			context.add(new Bytecode.Load(rhs, convertUnderlyingType(type.field(lval.field))));
			addWriteConversion(type.field(lval.field), context.getBytecodes());
		}
		JvmType.Function putFunType = new JvmType.Function(WHILEYRECORD, WHILEYRECORD, JAVA_LANG_STRING,
				JAVA_LANG_OBJECT);
		context.add(new Bytecode.Invoke(WHILEYRECORD, "put", putFunType, Bytecode.InvokeMode.STATIC));
	}

	private void translateUpdate(LVal.Reference lval, Iterator<LVal.Element<?>> iterator, int rhs, Context context) {
		if (iterator.hasNext()) {
			// This is not the innermost case, hence we read out the current
			// value of the location being assigned and pass that forward to the
			// next stage.
			context.add(new Bytecode.Dup(WHILEYOBJECT));
			JvmType.Function getFunType = new JvmType.Function(JAVA_LANG_OBJECT);
			context.add(new Bytecode.Invoke(WHILEYOBJECT, "state", getFunType, Bytecode.InvokeMode.VIRTUAL));
			addReadConversion(lval.type.element(), context.getBytecodes());
			translateUpdate(iterator, rhs, context);
		} else {
			// This is the innermost case, hence we can avoid the unnecessary
			// read of the current value and, instead, just return the rhs value
			// directly.
			JvmType rhsJvmType = convertUnderlyingType(lval.type.element());
			context.add(new Bytecode.Load(rhs, rhsJvmType));
		}
		JvmType.Function setFunType = new JvmType.Function(WHILEYOBJECT, JAVA_LANG_OBJECT);
		context.add(new Bytecode.Invoke(WHILEYOBJECT, "setState", setFunType, Bytecode.InvokeMode.VIRTUAL));
	}

	private void translate(Break c, Context context) {
		context.add(new Bytecode.Goto(context.getBreakLabel()));
	}
	
	private void translate(Continue c, Context context) {
		context.add(new Bytecode.Goto(context.getContinueLabel()));
	}
	
	private void translate(Debug c, Context context) {
		JvmType.Function ftype = new JvmType.Function(T_VOID, WHILEYARRAY);
		translateOperand(c.operand(0), context);
		context.add(new Bytecode.Invoke(WHILEYUTIL, "print", ftype, Bytecode.InvokeMode.STATIC));
	}

	private void translate(DoWhile c, Context context) {
		// Allocate header label for loop
		String headerLabel = freshLabel();
		String breakLabel = freshLabel();
		context.add(new Bytecode.Label(headerLabel));
		// Translate body of loop.
		Context bodyContext = context.newLoopBlock(c.body(),breakLabel,headerLabel);
		translateAllWithin(bodyContext);
		// Translate the loop condition.
		translateCondition(c.condition(), headerLabel, null, context);
		context.add(new Bytecode.Label(breakLabel));
	}

	private void translate(Fail c, Context context) {
		context.add(new Bytecode.New(JAVA_LANG_RUNTIMEEXCEPTION));
		context.add(new Bytecode.Dup(JAVA_LANG_RUNTIMEEXCEPTION));
		context.add(new Bytecode.LoadConst("runtime fault encountered"));
		JvmType.Function ftype = new JvmType.Function(T_VOID, JAVA_LANG_STRING);
		context.add(new Bytecode.Invoke(JAVA_LANG_RUNTIMEEXCEPTION, "<init>", ftype, Bytecode.InvokeMode.SPECIAL));
		context.add(new Bytecode.Throw());
	}

	private void translate(If code, Context context) {
		String exitLabel = freshLabel();
		String falseLabel = code.hasFalseBranch() ? freshLabel() : exitLabel;
		// First, translate the condition
		translateCondition(code.condition(), null, falseLabel, context);
		// Second, translate the true branch
		translateAllWithin(context.newBlock(code.trueBranch()));
		if (code.hasFalseBranch()) {
			// Third, translate false branch (if applicable)
			context.add(new Bytecode.Goto(exitLabel));
			context.add(new Bytecode.Label(falseLabel));
			translateAllWithin(context.newBlock(code.falseBranch()));
			context.add(new Bytecode.Label(exitLabel));
		} else {
			context.add(new Bytecode.Label(falseLabel));
		}
	}

	private void translate(Return c, Context context) {
		int[] operands = c.operands();
		JvmType rt;
		//
		switch (operands.length) {
		case 0:
			// No return value.
			rt = null;
			break;
		case 1:
			// Exactly one return value, so we can return it directly.
			translateOperand(operands[0], context);
			// Determine return type
			BytecodeForest.Location operand = context.getLocation(operands[0]);
			rt = convertUnderlyingType(operand.type(0));
			break;
		default:
			// More than one return value. In this case, we need to encode the
			// return values into an object array. This is annoying, but it's
			// because Java doesn't support multiple return values.
			rt = JAVA_LANG_OBJECT_ARRAY;
			// FIXME: implement this
			throw new RuntimeException("Multiple returns not implemented!");
		}
		// Done
		context.add(new Bytecode.Return(rt));
	}

	private void translate(Switch code, Context context) {
		String exitLabel = freshLabel();
		BytecodeForest.Location loc = context.getLocation(code.operand());
		JvmType type = convertUnderlyingType(loc.type(0));
		// Translate condition into a value and store into a temporary register.
		// This is necessary because, according to the semantics of Whiley, we
		// can only execute the condition expression once.
		translateOperand(code.operand(), context);
		context.add(new Bytecode.Store(code.operand(), type));
		ArrayList<Pair<String, Case>> cases = new ArrayList<Pair<String, Case>>();
		// Generate the dispatch table which checks the condition value against
		// each of the case constants. If a match is found, we branch to a given
		// label demarking the start of the case body (which will be translated
		// later). In principle, using a tableswitch bytecode would be better
		// here.
		boolean hasDefault = false;
		for (Case c : code.cases()) {
			Constant[] values = c.values();
			String caseLabel = freshLabel();
			if (values.length == 0) {
				// In this case, we have a default target which corresponds to
				// an unconditional branch
				context.add(new Bytecode.Goto(caseLabel));
				hasDefault = true;
			} else {
				for (Constant value : values) {
					translateConstant(value, context.getBytecodes());
					context.add(new Bytecode.Load(code.operand(), type));
					JvmType.Function ftype = new JvmType.Function(T_BOOL, JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);
					context.add(new Bytecode.Invoke(WHILEYUTIL, "equals", ftype, Bytecode.InvokeMode.STATIC));
					context.add(new Bytecode.If(Bytecode.IfMode.NE, caseLabel));
				}
			}
			cases.add(new Pair<String, Case>(caseLabel, c));
		}
		// If there was no default case, then add an unconditional branch over
		// the case bodies to the end of the switch.
		if (!hasDefault) {
			context.add(new Bytecode.Goto(exitLabel));
		}
		// Translate each of the case bodies in turn.
		for (Pair<String, Case> p : cases) {
			context.add(new Bytecode.Label(p.first()));
			translateAllWithin(context.newBlock(p.second().block()));
			context.add(new Bytecode.Goto(exitLabel));
		}
		// Finally, mark out the exit point of the switch
		context.add(new Bytecode.Label(exitLabel));
	}

	private void translate(While c, Context context) {
		// Allocate header label for loop
		String headerLabel = freshLabel();
		String exitLabel = freshLabel();
		context.add(new Bytecode.Label(headerLabel));
		// Translate the loop condition.
		translateCondition(c.condition(), null, exitLabel, context);
		// Translate body of loop.
		Context bodyContext = context.newLoopBlock(c.body(),exitLabel,headerLabel);
		translateAllWithin(bodyContext);
		// Terminate loop by branching back to head of loop
		context.add(new Bytecode.Goto(headerLabel));
		// This is where we exit the loop
		context.add(new Bytecode.Label(exitLabel));
	}

	// ===============================================================================
	// LVals
	// ===============================================================================

	/**
	 * Construct an array of LVals, one for each operand on the left-hand side
	 * of an assignment. Each LVal provides a simple "path" representation of
	 * the left-hand side with which we can more easily generate code for
	 * implement the update.
	 * 
	 * @param lval
	 *            LVal operand to translate
	 * @param context
	 * @return
	 */
	public LVal[] translateLVals(int[] lvals, Context context) {
		LVal[] vals = new LVal[lvals.length];
		for (int i = 0; i != vals.length; ++i) {
			LVal lval = generateLVal(lvals[i], context);
			translateLVal(lval, context);
			vals[i] = lval;
		}
		return vals;
	}

	/**
	 * <p>
	 * Translate any operands for an LVal expression in left-to-right order.
	 * This only applies to array lvals, as these have operands which need to be
	 * evaluated.
	 * </p>
	 * 
	 * <p>
	 * <b>NOTE:</b> For simplicity, we just load any lval operands into their
	 * corresponding bytecode registers.
	 * </p>
	 * 
	 * @param lval
	 * @param context
	 */
	public void translateLVal(LVal lval, Context context) {
		for (LVal.Element<?> e : lval.path) {
			if (e instanceof LVal.Array) {
				LVal.Array ae = (LVal.Array) e;
				translateOperand(ae.index, context);
				context.add(new Bytecode.Store(ae.index, WHILEYINT));
			}
		}
	}

	/**
	 * Generate an LVal expression from the left-hand side of an assignment. An
	 * LVal expression is basically just a path representation of the lhs
	 * expression.
	 * 
	 * @param lval
	 *            LVal operand to translate
	 * @param context
	 * @return
	 */
	public LVal generateLVal(int lval, Context context) {
		BytecodeForest.Location loc = context.getLocation(lval);
		ArrayList<LVal.Element<?>> path = new ArrayList<LVal.Element<?>>();
		while (loc instanceof BytecodeForest.Operand) {
			BytecodeForest.Operand lv = (BytecodeForest.Operand) loc;
			wyil.lang.Bytecode.Expr code = lv.value();
			switch (code.opcode()) {
			case OPCODE_fieldload:
				Type.EffectiveRecord recType = (Type.EffectiveRecord) lv.type(0);
				wyil.lang.Bytecode.FieldLoad fl = (wyil.lang.Bytecode.FieldLoad) code;
				path.add(new LVal.Record(recType, fl.fieldName()));
				lval = fl.operand();
				break;
			case OPCODE_arrayindex:
				Type.EffectiveArray arrayType = (Type.EffectiveArray) lv.type(0);
				wyil.lang.Bytecode.Operator al = (wyil.lang.Bytecode.Operator) code;
				path.add(new LVal.Array(arrayType, al.operand(1)));
				lval = al.operand(0);
				break;
			case OPCODE_dereference:
				Type.Reference refType = (Type.Reference) lv.type(0);
				wyil.lang.Bytecode.Operator rl = (wyil.lang.Bytecode.Operator) code;
				path.add(new LVal.Reference(refType));
				lval = rl.operand(0);
				break;
			default:
				internalFailure("unknown wyil code encountered (" + code + ")", filename,
						context.attribute(Attribute.Source.class));
			}
			loc = context.getLocation(lval);
		}
		// At this point, we have to reverse the lvals because they were put
		// into the array in the wrong order.
		Collections.reverse(path);
		// Done
		return new LVal(lval, path);
	}

	/**
	 * Represents a type which may appear on the left of an assignment
	 * expression. Arrays, Records and References are the only valid types for
	 * an lval.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class LVal {
		public final int variable;
		public final List<Element<?>> path;

		public LVal(int variable, List<Element<?>> path) {
			this.variable = variable;
			this.path = new ArrayList<Element<?>>(path);
		}

		public static class Element<T> {
			public final T type;

			public Element(T type) {
				this.type = type;
			}
		}

		public static class Record extends Element<Type.EffectiveRecord> {
			public final String field;

			public Record(Type.EffectiveRecord type, String field) {
				super(type);
				this.field = field;
			}
		}

		public static class Array extends Element<Type.EffectiveArray> {
			public final int index;

			public Array(Type.EffectiveArray type, int index) {
				super(type);
				this.index = index;
			}
		}

		public static class Reference extends Element<Type.Reference> {
			public Reference(Type.Reference type) {
				super(type);
			}
		}
	}

	// ===============================================================================
	// Conditions
	// ===============================================================================

	/**
	 * <p>
	 * Translate a given condition to determine whether it holds or not, then
	 * branch to an appropriate label. If so, branch to the given true label, or
	 * continue to next logical instruction if null. If not, branch to the given
	 * false label, or continue to next logical instruction if null.
	 * </p>
	 * 
	 * <p>
	 * <b>NOTE:</b> Exactly one of trueLabel or falseLabel must be null to
	 * represent the fall-thru case.
	 * </p>
	 * 
	 * @param condition
	 *            Operand to evaluate to see whether it is true or false
	 * @param trueLabel
	 *            Destination for when the condition is true. If null, execution
	 *            continues to next logical instruction when condition holds.
	 * @param falseLabel
	 *            Destination for when the condition is false. If null,
	 *            execution continues to next logical instruction when condition
	 *            doesn't hold.
	 * @param enclosing
	 *            Enclosing context
	 */
	private void translateCondition(int condition, String trueLabel, String falseLabel, Context enclosing) {
		BytecodeForest.Location loc = enclosing.getLocation(condition);
		if (loc instanceof BytecodeForest.Operand) {
			BytecodeForest.Operand operand = (BytecodeForest.Operand) loc;
			wyil.lang.Bytecode.Expr code = operand.value();
			// First, attempt to use a conditional branch to implement this
			// operation. This will produce more attractive bytecode.
			if (code instanceof Operator) {
				// This is an operator and, in some cases, we can optimise the
				// translation using one or more conditional branches.
				wyil.lang.Bytecode.Operator op = (wyil.lang.Bytecode.Operator) code;
				switch (op.opcode()) {
				case OPCODE_logicalnot:
					translateNotCondition(op, trueLabel, falseLabel, enclosing);
					return;
				case OPCODE_logicaland:
					translateShortcircuitAndCondition(op, trueLabel, falseLabel, enclosing);
					return;
				case OPCODE_logicalor:
					translateShortcircuitOrCondition(op, trueLabel, falseLabel, enclosing);
					return;
				}
			} else if(code instanceof Quantifier) {
				translateQuantifierCondition((Quantifier) code, trueLabel, falseLabel, enclosing);
				return;
			}
		}
		// We can't use a condition branch, so fall back to the default. In this
		// case, we just evaluate the condition as normal which will result in a
		// 1 or 0 being loaded on the stack. We can then perform a condition
		// branch from that.
		translateOperand(condition, enclosing);
		// Now, dig a true boolean about of the WyBool object
		JvmType.Function ft = new JvmType.Function(JvmTypes.T_BOOL);
		enclosing.add(new Bytecode.Invoke(WHILEYBOOL, "value", ft, Bytecode.InvokeMode.VIRTUAL));
		// Finally, branch as necessary
		if (trueLabel == null) {
			enclosing.add(new Bytecode.If(Bytecode.IfMode.EQ, falseLabel));
		} else {
			enclosing.add(new Bytecode.If(Bytecode.IfMode.NE, trueLabel));
		}
	}

	/**
	 * Translate a logical not condition.
	 * 
	 * @param condition
	 *            Operand to evaluate to see whether it is true or false
	 * @param trueLabel
	 *            Destination for when the condition is true. If null, execution
	 *            continues to next logical instruction when condition holds.
	 * @param falseLabel
	 *            Destination for when the condition is false. If null,
	 *            execution continues to next logical instruction when condition
	 *            doesn't hold.
	 * @param enclosing
	 *            Enclosing context
	 */
	private void translateNotCondition(Operator code, String trueLabel, String falseLabel, Context enclosing) {
		// This case is very easy, as we can just swap the true and false
		// labels.
		translateCondition(code.operand(0), falseLabel, trueLabel, enclosing);
	}

	/**
	 * Translate a logical and condition with short circuiting semantics.
	 * 
	 * @param condition
	 *            Operand to evaluate to see whether it is true or false
	 * @param trueLabel
	 *            Destination for when the condition is true. If null, execution
	 *            continues to next logical instruction when condition holds.
	 * @param falseLabel
	 *            Destination for when the condition is false. If null,
	 *            execution continues to next logical instruction when condition
	 *            doesn't hold.
	 * @param enclosing
	 *            Enclosing context
	 */
	private void translateShortcircuitAndCondition(Operator code, String trueLabel, String falseLabel,
			Context enclosing) {
		if (trueLabel == null) {
			translateCondition(code.operand(0), null, falseLabel, enclosing);
			translateCondition(code.operand(1), null, falseLabel, enclosing);
		} else {
			// implies falseLabel should be null
			String exitLabel = freshLabel();
			translateCondition(code.operand(0), null, exitLabel, enclosing);
			translateCondition(code.operand(1), trueLabel, null, enclosing);
			enclosing.add(new Bytecode.Label(exitLabel));
		}
	}

	/**
	 * Translate a logical or condition with short circuiting semantics.
	 * 
	 * @param condition
	 *            Operand to evaluate to see whether it is true or false
	 * @param trueLabel
	 *            Destination for when the condition is true. If null, execution
	 *            continues to next logical instruction when condition holds.
	 * @param falseLabel
	 *            Destination for when the condition is false. If null,
	 *            execution continues to next logical instruction when condition
	 *            doesn't hold.
	 * @param enclosing
	 *            Enclosing context
	 */
	private void translateShortcircuitOrCondition(Operator code, String trueLabel, String falseLabel,
			Context enclosing) {
		if (trueLabel == null) {
			// implies false label is non-null
			String exitLabel = freshLabel();
			translateCondition(code.operand(0), exitLabel, null, enclosing);
			translateCondition(code.operand(1), null, falseLabel, enclosing);
			enclosing.add(new Bytecode.Label(exitLabel));
		} else {
			// implies false label is null
			translateCondition(code.operand(0), trueLabel, null, enclosing);
			translateCondition(code.operand(1), trueLabel, null, enclosing);
		}
	}


	/**
	 * Translate a quantifier condition into a set of nested loops with the
	 * condition itself located in the innermost position.
	 * 
	 * @param condition
	 *            Operand to evaluate to see whether it is true or false
	 * @param trueLabel
	 *            Destination for when the condition is true. If null, execution
	 *            continues to next logical instruction when condition holds.
	 * @param falseLabel
	 *            Destination for when the condition is false. If null,
	 *            execution continues to next logical instruction when condition
	 *            doesn't hold.
	 * @param enclosing
	 *            Enclosing context
	 */
	private void translateQuantifierCondition(Quantifier condition, String trueLabel, String falseLabel,
			Context context) {
		String exitLabel = freshLabel();
		if(trueLabel == null) {
			trueLabel = exitLabel;
		} else {
			falseLabel = exitLabel;
		}
		translateQuantifierCondition(0, condition, trueLabel, falseLabel, context);
		// Add complete branch (if necessary)
		switch(condition.opcode()) {
		case OPCODE_all:
			if(trueLabel != exitLabel) {
				context.add(new Bytecode.Goto(trueLabel));
			}
			break;
		case OPCODE_some:
			if(falseLabel != exitLabel) {
				context.add(new Bytecode.Goto(falseLabel));
			}
			break;
		case OPCODE_none:
		default:				
			if(trueLabel != exitLabel) {
				context.add(new Bytecode.Goto(trueLabel));
			}
			break;
		}
		context.add(new Bytecode.Label(exitLabel));
	}

	/**
	 * Helper function for translating quantifiers. The range index indicates
	 * which range we are currently translating. We essentially recursve for
	 * each range translating them one at a time into an enclosing loop. When
	 * the innermost loop body is reached, we can then evaluate the condition
	 * for the given range positions.
	 * 
	 * @param index
	 *            Index into quantifier ranges. If matches number of ranges,
	 *            then innermost position is reached.
	 * @param condition
	 *            Operand to evaluate to see whether it is true or false
	 * @param trueLabel
	 *            Destination for when the condition is true. If null, execution
	 *            continues to next logical instruction when condition holds.
	 * @param falseLabel
	 *            Destination for when the condition is false. If null,
	 *            execution continues to next logical instruction when condition
	 *            doesn't hold.
	 * @param enclosing
	 *            Enclosing context
	 */
	private void translateQuantifierCondition(int index, Quantifier condition, String trueLabel, String falseLabel,
			Context context) {
		wyil.lang.Bytecode.Range[] ranges = condition.ranges();
		if (index == ranges.length) {
			// This is the innermost case. At this point, we are in the body of
			// the innermost loop.  First, determine what is true and false :) 
			String myTrueLabel = null;
			String myFalseLabel = null;
			switch(condition.opcode()) {
			case OPCODE_all:
				// if condition false, terminate early and return false.
				myFalseLabel = falseLabel;
				break;
			case OPCODE_some:
				// if condition true, terminate early and return true.
				myTrueLabel = trueLabel;
				break;
			case OPCODE_none:
			default:				
				// if condition true, terminate early and return false.
				myTrueLabel = falseLabel;
				break;
			}
			// Translate the quantifier condition and, depending on the kind of
			// quantifier, branch to either the true or false label.
			translateCondition(condition.body(),myTrueLabel,myFalseLabel,context);
		} else {
			// This is the recursive case. Here, we need to construct an
			// appropriate loop to iterate through the range.
			wyil.lang.Bytecode.Range range = ranges[index];
			translateOperand(range.startOperand(),context);
			translateOperand(range.endOperand(),context);
			context.add(new Bytecode.Store(range.endOperand(),WHILEYINT));
			context.add(new Bytecode.Store(range.variable(),WHILEYINT));
			// Create loop
			String headerLabel = freshLabel();
			String exitLabel = freshLabel();
			// Generate loop condition
			context.add(new Bytecode.Label(headerLabel));
			context.add(new Bytecode.Load(range.variable(),WHILEYINT));
			context.add(new Bytecode.Load(range.endOperand(),WHILEYINT));
			JvmType.Function ftype = new JvmType.Function(WHILEYBOOL, JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);
			context.add(new Bytecode.Invoke(WHILEYUTIL, "equal", ftype, Bytecode.InvokeMode.STATIC));
			ftype = new JvmType.Function(JvmTypes.T_BOOL);
			context.add(new Bytecode.Invoke(WHILEYBOOL, "value", ftype, Bytecode.InvokeMode.VIRTUAL));
			context.add(new Bytecode.If(Bytecode.IfMode.NE, exitLabel));
			// Recursively translate remainder of quantifier
			translateQuantifierCondition(index+1,condition,trueLabel,falseLabel,context);
			// Increment index variable
			context.add(new Bytecode.Load(range.variable(),WHILEYINT));
			context.add(new Bytecode.GetField(WHILEYINT, "ONE", WHILEYINT, Bytecode.FieldMode.STATIC));
			ftype = new JvmType.Function(WHILEYINT, WHILEYINT);
			context.add(new Bytecode.Invoke(WHILEYINT,"add",ftype,Bytecode.InvokeMode.VIRTUAL));
			context.add(new Bytecode.Store(range.variable(),WHILEYINT));
			// Branch back to top of loop
			context.add(new Bytecode.Goto(headerLabel));
			context.add(new Bytecode.Label(exitLabel));
		}
	}
	
	
	// ===============================================================================
	// Expressions
	// ===============================================================================

	/**
	 * Translate one or more operands into JVM Bytecodes. Execution follows the
	 * order of operands given, with the first operand being evaluated before
	 * the others. Likewise, the results are pushed on the stack in the order of
	 * operands given. So, the result of the first operand is pushed onto the
	 * stack first, etc.
	 * 
	 * @param code
	 *            The WyIL operand being translated.
	 * @param enclosing
	 *            The enclosing context for this operand.
	 * @return The set of JVM types representing the actual operands pushed on
	 *         the stack. This maybe be larger than the number of operands
	 *         provided in the case that one or more of those operands had
	 *         multiple return values.
	 */
	public List<JvmType> translateOperands(int[] operands, Context enclosing) {
		ArrayList<JvmType> types = new ArrayList<JvmType>();
		for (int i = 0; i != operands.length; ++i) {
			int operand = operands[i];
			// Translate operand
			translateOperand(operand, enclosing);
			// Determine type(s) for operand
			BytecodeForest.Location l = enclosing.getLocation(operand);
			for (int j = 0; j != l.size(); ++j) {
				types.add(convertUnderlyingType(l.type(j)));
			}
		}
		return types;
	}

	/**
	 * Translate one or more operands into JVM Bytecodes and store them in an
	 * object array. Execution follows the order of operands given, with the
	 * first operand being evaluated before the others. Likewise, the results
	 * are pushed on the stack in the order of operands given. So, the result of
	 * the first operand is pushed onto the stack first, etc.
	 * 
	 * @param code
	 *            The WyIL operand being translated.
	 * @param enclosing
	 *            The enclosing context for this operand.
	 * @return The set of JVM types representing the actual operands pushed on
	 *         the stack. This maybe be larger than the number of operands
	 *         provided in the case that one or more of those operands had
	 *         multiple return values.
	 */
	private void translateOperandsToArray(int[] operands, Context context) {
		context.add(new Bytecode.LoadConst(operands.length));
		context.add(new Bytecode.New(JAVA_LANG_OBJECT_ARRAY));
		for (int i = 0; i < operands.length; ++i) {
			int operand = operands[i];
			BytecodeForest.Location l = context.getLocation(operand);
			context.add(new Bytecode.Dup(JAVA_LANG_OBJECT_ARRAY));
			context.add(new Bytecode.LoadConst(i));
			translateOperand(operand, context);
			// FIXME: handle multiple returns
			addWriteConversion(l.type(0), context.getBytecodes());
			context.add(new Bytecode.ArrayStore(JAVA_LANG_OBJECT_ARRAY));
		}
	}

	/**
	 * Translate an operand into one or more JVM Bytecodes. The result of this
	 * operand will be pushed onto the stack at the end.
	 * 
	 * @param code
	 *            The WyIL operand being translated.
	 * @param enclosing
	 *            The enclosing context for this operand.
	 * @return
	 */
	private void translateOperand(int operand, Context enclosing) {
		BytecodeForest.Location location = enclosing.getLocation(operand);
		if (location instanceof BytecodeForest.Variable) {
			BytecodeForest.Variable var = (BytecodeForest.Variable) location;
			JvmType jvmType = convertUnderlyingType(var.type(0));
			enclosing.add(new Bytecode.Load(operand, jvmType));
		} else {
			Context.Operand context = enclosing.newOperand(operand);
			wyil.lang.Bytecode.Expr code = context.getOperand().value();
			try {
				switch (code.opcode()) {
				case OPCODE_convert:
					translateOperand((Convert) code, context);
					break;
				case OPCODE_const:
					translateOperand((Const) code, context);
					break;
				case OPCODE_fieldload:
					translateOperand((FieldLoad) code, context);
					break;
				case OPCODE_all:
				case OPCODE_some:
				case OPCODE_none:
					translateOperand((Quantifier) code, context);
					break;
				case OPCODE_indirectinvoke:
					translateOperands((IndirectInvoke) code, context);
					break;
				case OPCODE_invoke:
					translateOperand((Invoke) code, context);
					break;
				case OPCODE_lambda:
					translateOperand((Lambda) code, context);
					break;
				default:
					if (code instanceof Operator) {
						translateOperand((Operator) code, context);
					} else {
						internalFailure("unknown wyil code encountered (" + code + ")", filename,
								context.attribute(Attribute.Source.class));
					}
				}
			} catch(InternalFailure ex) {
				throw ex;
			} catch (Exception ex) {
				internalFailure(ex.getMessage(), filename, ex, context.attribute(Attribute.Source.class));
			}
		}
	}

	/**
	 * Translate a constant into JVM bytecodes and load it onto the stack. In
	 * some cases, this can be done directly. For example, <code>null</code> can
	 * be loaded onto the stack using the <code>aconst_null</code> bytecode. In
	 * other cases (e.g. for array constants), we have to construct one or more
	 * objects to represent the constant. To avoid doing this everytime such a
	 * constant is encountered, we create the constants in the static
	 * initialiser of this class, and store them in static fields for later
	 * recall.
	 * 
	 * @param code
	 *            The WyIL operand being translated.
	 * @param enclosing
	 *            The enclosing context for this operand.
	 * @return
	 */
	private void translateOperand(Const c, Context.Operand context) {
		Constant constant = c.constant();
		JvmType jt = convertUnderlyingType(constant.type());
		// Check whether this constant can be translated as a primitive, and
		// encoded directly within a bytecode.
		if (constant instanceof Constant.Bool || constant instanceof Constant.Null
				|| constant instanceof Constant.Byte) {
			// Yes, it can.
			translateConstant(constant, context.getBytecodes());
		} else {
			// No, this needs to be constructed elsewhere as a static field.
			// Then, we can load it onto the stack here.
			int id = JvmValue.get(constant, constants);
			String name = "constant$" + id;
			context.add(new Bytecode.GetField(owner, name, jt, Bytecode.FieldMode.STATIC));
		}
	}

	/**
	 * Coerce one data value into another. In what situations do we actually
	 * have to do work?
	 * 
	 * @param code
	 *            The WyIL operand being translated.
	 * @param enclosing
	 *            The enclosing context for this operand.
	 * @return
	 */
	private void translateOperand(Convert c, Context.Operand context) {
		// Translate the operand
		translateOperand(c.operand(), context);
		// Do nothing :)
	}

	/**
	 * Load the value of a given field from a record.
	 * 
	 * @param code
	 *            The WyIL operand being translated.
	 * @param enclosing
	 *            The enclosing context for this operand.
	 * @return
	 */
	private void translateOperand(FieldLoad c, Context.Operand context) {
		BytecodeForest.Operand operand = context.getOperand();
		Type.Record type = (Type.Record) operand.type(0);
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT, WHILEYRECORD, JAVA_LANG_STRING);
		// Translate the source operand
		translateOperand(c.operand(), context);
		context.add(new Bytecode.LoadConst(c.fieldName()));
		// Load the field out of the resulting record
		context.add(new Bytecode.Invoke(WHILEYRECORD, "get", ftype, Bytecode.InvokeMode.STATIC));
		// Add a read conversion (if necessary) to unbox the value
		context.addReadConversion(type.field(c.fieldName()));
	}

	/**
	 * Apply a unary, binary or ternary operator to a given set of operands.
	 * 
	 * @param code
	 *            The WyIL operand being translated.
	 * @param enclosing
	 *            The enclosing context for this operand.
	 * @return
	 */
	private void translateOperand(Operator c, Context.Operand context) {
		// First, translate each operand and load its value onto the stack
		switch (c.opcode()) {
		case OPCODE_record:
			translateRecordConstructor(c, context);
			break;
		case OPCODE_array:
			translateArrayConstructor(c, context);
			break;
		default:
			translateOperands(c.operands(), context);
			// Second, dispatch to a specific translator for this opcode kind.
			generators[c.opcode()].translate(c, context);
		}

	}

	/**
	 * Translate a RecordConstructor operand.
	 * 
	 * @param bytecode
	 * @param context
	 */
	public void translateRecordConstructor(Operator bytecode, Context.Operand context) {
		BytecodeForest.Operand operand = context.getOperand();
		Type.EffectiveRecord recType = (Type.EffectiveRecord) operand.type(0);
		JvmType.Function ftype = new JvmType.Function(WHILEYRECORD, WHILEYRECORD, JAVA_LANG_STRING, JAVA_LANG_OBJECT);

		context.construct(WHILEYRECORD);

		ArrayList<String> keys = new ArrayList<String>(recType.fields().keySet());
		Collections.sort(keys);
		for (int i = 0; i != bytecode.operands().length; i++) {
			String key = keys.get(i);
			Type fieldType = recType.field(key);
			context.add(new Bytecode.LoadConst(key));
			translateOperand(bytecode.operand(i), context);
			context.addWriteConversion(fieldType);
			context.add(new Bytecode.Invoke(WHILEYRECORD, "put", ftype, Bytecode.InvokeMode.STATIC));
		}
	}

	/**
	 * Translate an ArrayConstructor operand
	 * 
	 * @param code
	 *            The WyIL operand being translated.
	 * @param enclosing
	 *            The enclosing context for this operand.
	 * @return
	 */
	private void translateArrayConstructor(Operator code, Context.Operand context) {
		BytecodeForest.Operand operand = context.getOperand();
		Type.Array arrType = (Type.Array) operand.type(0);
		JvmType.Function initJvmType = new JvmType.Function(T_VOID, T_INT);
		JvmType.Function ftype = new JvmType.Function(WHILEYARRAY, WHILEYARRAY, JAVA_LANG_OBJECT);

		context.add(new Bytecode.New(WHILEYARRAY));
		context.add(new Bytecode.Dup(WHILEYARRAY));
		context.add(new Bytecode.LoadConst(code.operands().length));
		context.add(new Bytecode.Invoke(WHILEYARRAY, "<init>", initJvmType, Bytecode.InvokeMode.SPECIAL));

		for (int i = 0; i != code.operands().length; ++i) {
			translateOperand(code.operand(i), context);
			context.addWriteConversion(arrType.element());
			context.add(new Bytecode.Invoke(WHILEYARRAY, "internal_add", ftype, Bytecode.InvokeMode.STATIC));
		}
	}

	/**
	 * Translate a lambda expression into a sequence of JVM bytecodes. This is
	 * done by constructing a separate class extending WyLambda. This class
	 * contains as fields any local variables from the current method/function
	 * which are accessed within the body of the lambda (the so-called
	 * "binding").
	 * 
	 * @param c
	 * @param context
	 */
	private void translateOperand(Lambda c, Context.Operand context) {
		int[] environment = c.environment();
		int lambda_id = lambdaClasses.size();
		String lambdaMethod = "$lambda" + lambda_id;
		JvmType[] jvmEnvironment = buildLambdaEnvironment(c.environment(),context);
		// First, we construct and instantiate the lambda
		ClassFile lambda = buildLambda(owner, lambdaMethod, c.type(), jvmEnvironment);
		lambdaClasses.add(lambda);
		//
		context.add(new Bytecode.New(lambda.type()));
		context.add(new Bytecode.Dup(lambda.type()));
		for(int i=0;i!=environment.length;++i) {
			context.add(new Bytecode.Load(environment[i], jvmEnvironment[i]));
		}
		JvmType.Function ftype = new JvmType.Function(T_VOID, jvmEnvironment);
		context.add(new Bytecode.Invoke(lambda.type(), "<init>", ftype, Bytecode.InvokeMode.SPECIAL));
		
		// Second, we translate the body of the lambda and construct a new
		// method to represent it. This method will then be called from the
		// lambda class created above.		
		ClassFile.Method method = buildLambdaMethod(c, lambdaMethod, context);
		//
		lambdaMethods.add(method);
	}

	/**
	 * Perform a direct method or function invocation.
	 * 
	 * @param code
	 *            The WyIL operand being translated.
	 * @param enclosing
	 *            The enclosing context for this operand.
	 * @return
	 */
	private void translateOperand(Invoke c, Context.Operand context) {
		// Translate each operand and load its value onto the stack
		translateOperands(c.operands(), context);
		// Construct the invocation bytecode
		context.add(createMethodInvocation(c.name(), c.type()));
	}

	/**
	 * Perform an indirect method or function invocation.
	 * 
	 * @param code
	 *            The WyIL operand being translated.
	 * @param enclosing
	 *            The enclosing context for this operand.
	 * @return
	 */
	private void translateOperands(IndirectInvoke c, Context.Operand context) {
		Type.FunctionOrMethod ft = c.type();
		// FIXME: need to implement multiple returns
		JvmType returnType = convertUnderlyingType(ft.returns().get(0));
		JvmType.Clazz owner = (JvmType.Clazz) convertUnderlyingType(ft);
		// First, translate reference operand which returns the function/method
		// object we will dispatch upon. This extends the WyLambda class.
		translateOperand(c.reference(), context);
		// Second, translate each argument and store it into an object array
		translateOperandsToArray(c.arguments(), context);
		// Third, make the indirect method or function call. This is done by
		// invoking the "call" method on the function / method object returned
		// from the reference operand.
		JvmType.Function type = new JvmType.Function(JAVA_LANG_OBJECT, JAVA_LANG_OBJECT_ARRAY);
		context.add(new Bytecode.Invoke(owner, "call", type, Bytecode.InvokeMode.VIRTUAL));
		// Cast return value to expected type
		addCheckCast(returnType,context.getBytecodes());
	}

	/**
	 * Translate a quantifier into a set of nested loops with the condition
	 * itself located in the innermost position.
	 * 
	 * @param condition
	 *            Operand to evaluate to see whether it is true or false 
	 * @param enclosing
	 *            Enclosing context
	 */
	private void translateOperand(Quantifier condition, Context context) {
		String trueLabel = freshLabel();
		String exitLabel = freshLabel();
		translateQuantifierCondition(condition, trueLabel, null, context);
		context.add(new Bytecode.GetField(WHILEYBOOL, "FALSE", WHILEYBOOL, Bytecode.FieldMode.STATIC));
		context.add(new Bytecode.Goto(exitLabel));
		context.add(new Bytecode.Label(trueLabel));
		context.add(new Bytecode.GetField(WHILEYBOOL, "TRUE", WHILEYBOOL, Bytecode.FieldMode.STATIC));
		context.add(new Bytecode.Label(exitLabel));
	}
	
	// ===============================================================================
	// Constants
	// ===============================================================================

	/**
	 * Translate a give constant value and load it onto the stack. Some of the
	 * translations are more expensive that others. Primitive types (e.g. null,
	 * bool, integer) can be loaded directly. Others require constructing
	 * objects and should, ideally, be done only once.
	 * 
	 * @param v
	 * @param context
	 */
	private void translateConstant(Constant v, List<Bytecode> bytecodes) {
		if (v instanceof Constant.Null) {
			translateConstant((Constant.Null) v, bytecodes);
		} else if (v instanceof Constant.Bool) {
			translateConstant((Constant.Bool) v, bytecodes);
		} else if (v instanceof Constant.Byte) {
			translateConstant((Constant.Byte) v, bytecodes);
		} else if (v instanceof Constant.Integer) {
			translateConstant((Constant.Integer) v, bytecodes);
		} else if (v instanceof Constant.Type) {
			translateConstant((Constant.Type) v, bytecodes);
		} else if (v instanceof Constant.Array) {
			translateConstant((Constant.Array) v, bytecodes);
		} else if (v instanceof Constant.Record) {
			translateConstant((Constant.Record) v, bytecodes);
		} else if (v instanceof Constant.FunctionOrMethod) {
			translateConstant((Constant.FunctionOrMethod) v, bytecodes);
		} else if (v instanceof Constant.Type) {
			translateConstant((Constant.Type) v, bytecodes);
		} else {
			throw new IllegalArgumentException("unknown value encountered:" + v);
		}
	}

	protected void translateConstant(Constant.Null e, List<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.LoadConst(null));
	}

	protected void translateConstant(Constant.Bool e, List<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.LoadConst(e.value()));
		JvmType.Function ftype = new JvmType.Function(WHILEYBOOL, T_BOOL);
		bytecodes.add(new Bytecode.Invoke(WHILEYBOOL, "valueOf", ftype, Bytecode.InvokeMode.STATIC));
	}

	protected void translateConstant(Constant.Type e, List<Bytecode> bytecodes) {
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

	protected void translateConstant(Constant.Byte e, List<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.LoadConst(e.value()));
		JvmType.Function ftype = new JvmType.Function(WHILEYBYTE, T_BYTE);
		bytecodes.add(new Bytecode.Invoke(WHILEYBYTE, "valueOf", ftype, Bytecode.InvokeMode.STATIC));
	}

	protected void translateConstant(Constant.Integer e, List<Bytecode> bytecodes) {
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

	protected void translateConstant(Constant.Array lv, List<Bytecode> context) {
		context.add(new Bytecode.New(WHILEYARRAY));
		context.add(new Bytecode.Dup(WHILEYARRAY));
		context.add(new Bytecode.LoadConst(lv.values().size()));
		JvmType.Function ftype = new JvmType.Function(T_VOID, T_INT);
		context.add(new Bytecode.Invoke(WHILEYARRAY, "<init>", ftype, Bytecode.InvokeMode.SPECIAL));

		ftype = new JvmType.Function(T_BOOL, JAVA_LANG_OBJECT);
		for (Constant e : lv.values()) {
			context.add(new Bytecode.Dup(WHILEYARRAY));
			translateConstant(e, context);
			addWriteConversion(e.type(), context);
			context.add(new Bytecode.Invoke(WHILEYARRAY, "add", ftype, Bytecode.InvokeMode.VIRTUAL));
			context.add(new Bytecode.Pop(T_BOOL));
		}
	}

	protected void translateConstant(Constant.Record expr, List<Bytecode> bytecodes) {
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT, JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);
		construct(WHILEYRECORD, bytecodes);
		for (Map.Entry<String, Constant> e : expr.values().entrySet()) {
			Type et = e.getValue().type();
			bytecodes.add(new Bytecode.Dup(WHILEYRECORD));
			bytecodes.add(new Bytecode.LoadConst(e.getKey()));
			translateConstant(e.getValue(), bytecodes);
			addWriteConversion(et, bytecodes);
			bytecodes.add(new Bytecode.Invoke(WHILEYRECORD, "put", ftype, Bytecode.InvokeMode.VIRTUAL));
			bytecodes.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
		}
	}

	/**
	 * Translate a constant representing a given function or method into an
	 * instance of a special lambda class. The lambda class extends WyLambda and
	 * looks just like any other lambda function. However, when invoked, it will
	 * call the function or method determined by the class directly.
	 */
	protected void translateConstant(Constant.FunctionOrMethod c, List<Bytecode> bytecodes) {
		// First, build the lambda
		NameID target = c.name();
		ClassFile lambda = buildLambda(getModuleClass(target.module()), target.name(), c.type());
		lambdaClasses.add(lambda);
		// Finally, construct an instance of the class itself
		construct(lambda.type(), bytecodes);
	}

	// ===============================================================================
	// Lambdas
	// ===============================================================================


	/**
	 * Create a method representing the body of a lambda. This will then be
	 * called by the lambda class created from the LambdaTemplate. Creating a
	 * method like this is not necessarily optimal, but it allows me to simplify
	 * the translation process (e.g. as locations still correspond to register
	 * numbers, etc).
	 * 
	 * @param type
	 * @param lambdaMethod
	 * @param context
	 * @return
	 */
	public ClassFile.Method buildLambdaMethod(Lambda l, String lambdaMethod, Context context) {
		JvmType.Function type = buildLambdaMethodType(l.type(),l.environment(),context);
		Context bodyContext = new Context(-1,context.getBytecodeForest());		
		// Move environment into correct register slots. First, push them all
		// onto the stack 
		shiftLambdaMethodParameters(l.parameters(), l.environment(), bodyContext);		
		// Translate the body of the lambda expression
		translateOperand(l.body(),bodyContext);		
		// Add the return value (if applicable)
		if (type.returnType() instanceof JvmType.Void) {
			bodyContext.add(new Bytecode.Return(null));
		} else {
			bodyContext.add(new Bytecode.Return(type.returnType()));
		}
		// Create the method itself
		String lambdaMethodMangled = nameMangle(lambdaMethod,l.type());
		List<Modifier> modifiers = modifiers(ACC_PUBLIC, ACC_STATIC, ACC_FINAL);
		ClassFile.Method method = new ClassFile.Method(lambdaMethodMangled, type, modifiers);
		// Add the code attribute
		jasm.attributes.Code code = new jasm.attributes.Code(bodyContext.getBytecodes(), Collections.EMPTY_LIST, method);
		method.attributes().add(code);
		//
		return method;
	}

	/**
	 * Shift the parameters passed into this lambda method into the desired
	 * target registers.
	 * 
	 * @param parameters
	 *            Actual parameter of the lambda itself
	 * @param environment
	 *            Environment variables used from the enclosing scope within the
	 *            lambda body
	 * @param bodyContext
	 */
	private void shiftLambdaMethodParameters(int[] parameters, int[] environment, Context bodyContext) {
		parameters = append(parameters,environment);
		for(int i=0;i!=parameters.length;++i) {
			int slot = parameters[i];
			if(slot != i) {
				BytecodeForest.Location loc = bodyContext.getLocation(parameters[i]);
				bodyContext.add(new Bytecode.Load(i, convertUnderlyingType(loc.type(0))));
			}
		}
		// Second, pop them all into the right register
		for(int i=parameters.length-1;i>=0;--i) {
			int slot = parameters[i];
			if(slot != i) {
				BytecodeForest.Location loc = bodyContext.getLocation(parameters[i]);
				bodyContext.add(new Bytecode.Store(parameters[i], convertUnderlyingType(loc.type(0))));
			}
		}
	}
	
	/**
	 * Construct the true method type for a lambda body. This is the type of the
	 * lambda itself, along with the types for any environment variables which
	 * are required.
	 * 
	 * @param type
	 * @param environment
	 * @param context
	 * @return
	 */
	public JvmType.Function buildLambdaMethodType(Type.FunctionOrMethod type, int[] environment, Context context) {
		JvmType.Function jvmType = convertFunType(type);
		ArrayList<JvmType> actualParameterTypes = new ArrayList<JvmType>(jvmType.parameterTypes());
		for (int i = 0; i != environment.length; ++i) {
			BytecodeForest.Location loc = context.getLocation(environment[i]);
			actualParameterTypes.add(convertUnderlyingType(loc.type(0)));
		}
		//
		return new JvmType.Function(jvmType.returnType(), actualParameterTypes);
	}
	
	/**
	 * Determine the types for the environment variables of a lambda expression.
	 * 
	 * @param environment
	 * @param context
	 * @return
	 */
	public JvmType[] buildLambdaEnvironment(int[] environment, Context context) {
		JvmType[] envTypes = new JvmType[environment.length];
		for (int i = 0; i != environment.length; ++i) {
			BytecodeForest.Location loc = context.getLocation(environment[i]);
			envTypes[i] = convertUnderlyingType(loc.type(0));
		}
		return envTypes;
	}
	
	public ClassFile buildLambda(JvmType.Clazz targetClass, String targetMethod, Type.FunctionOrMethod type,
			JvmType... environment) {
		int lambda_id = lambdaClasses.size();
		// Determine name of the lambda class. This class will extend class
		// wyjc.runtime.WyLambda.
		JvmType.Clazz lambdaClass = new JvmType.Clazz(owner.pkg(), owner.lastComponent().first(),
				Integer.toString(lambda_id));
		// Determine mangled name of function or method
		String targetMethodMangled = nameMangle(targetMethod, type);
		// Determine JvmType of function or method
		JvmType.Function jvmType = convertFunType(type);
		// Determine the Jvm types for the environment		
		// Create the lambda template
		LambdaTemplate template = new LambdaTemplate(CLASS_VERSION, lambdaClass, targetClass, targetMethodMangled,
				jvmType, environment);
		// Instantiate the template to generate the lambda class
		return template.generateClass();
	}

	// ===============================================================================
	// Helpers
	// ===============================================================================


	/**
	 * Create an invocation bytecode for a given WyIL function or method. This
	 * is just a convenience method which takes care of name mangling, etc.
	 * 
	 * @param name
	 *            Name and module of function or method to be invoked
	 * @param type
	 *            Type of function or method to be invoked
	 * @return
	 */
	public Bytecode.Invoke createMethodInvocation(NameID name, Type.FunctionOrMethod type) {
		// Determine name of class corresponding to the given module
		JvmType.Clazz owner = getModuleClass(name.module());
		// Determine mangled name for function/method being invoked
		String mangled = nameMangle(name.name(), type);
		// Convert the type of the function/method being invoked
		JvmType.Function fnType = convertFunType(type);
		// Create the invocation!
		return new Bytecode.Invoke(owner, mangled, fnType, Bytecode.InvokeMode.STATIC);
	}

	private void decodeOperandArray(List<Type> types, int[] targets, Context context) {
		for (int i = 0; i != targets.length; ++i) {
			int register = targets[i];
			Type type = types.get(i);
			context.add(new Bytecode.Dup(JAVA_LANG_OBJECT_ARRAY));
			context.add(new Bytecode.LoadConst(i));
			context.add(new Bytecode.ArrayLoad(JAVA_LANG_OBJECT_ARRAY));
			addReadConversion(type, context.getBytecodes());
			JvmType jvmType = convertUnderlyingType(type);
			context.add(new Bytecode.Store(register, jvmType));
		}
		context.add(new Bytecode.Pop(JAVA_LANG_OBJECT_ARRAY));
	}

	/**
	 * The read conversion is necessary in situations where we're reading a
	 * value from a collection (e.g. WhileyList, WhileySet, etc) and then
	 * putting it on the stack. In such case, we need to convert boolean values
	 * from Boolean objects to bool primitives.
	 */
	private void addReadConversion(Type et, List<Bytecode> bytecodes) {
		// This doesn't do anything extra since there are currently no data
		// types implemented as primitives.
		addCheckCast(convertUnderlyingType(et), bytecodes);
	}

	/**
	 * The write conversion is necessary in situations where we're write a value
	 * from the stack into a collection (e.g. WhileyList, WhileySet, etc). In
	 * such case, we need to convert boolean values from bool primitives to
	 * Boolean objects.
	 */
	private void addWriteConversion(Type et, List<Bytecode> bytecodes) {
		// This currently does nothing since there are currently no data types
		// implemented as primitives.
	}

	private void addCheckCast(JvmType type, List<Bytecode> bytecodes) {
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
	private void construct(JvmType.Clazz owner, List<Bytecode> bytecodes) {
		bytecodes.add(new Bytecode.New(owner));
		bytecodes.add(new Bytecode.Dup(owner));
		ArrayList<JvmType> paramTypes = new ArrayList<JvmType>();
		JvmType.Function ftype = new JvmType.Function(T_VOID, paramTypes);
		bytecodes.add(new Bytecode.Invoke(owner, "<init>", ftype, Bytecode.InvokeMode.SPECIAL));
	}

	public final static Type WHILEY_SYSTEM_T = Type
			.Nominal(new NameID(Trie.fromString("whiley/lang/System"), "Console"));

	public final static JvmType.Clazz WHILEYUTIL = new JvmType.Clazz("wyjc.runtime", "Util");
	public final static JvmType.Clazz WHILEYARRAY = new JvmType.Clazz("wyjc.runtime", "WyArray");
	public final static JvmType.Clazz WHILEYTYPE = new JvmType.Clazz("wyjc.runtime", "WyType");
	public final static JvmType.Clazz WHILEYRECORD = new JvmType.Clazz("wyjc.runtime", "WyRecord");
	public final static JvmType.Clazz WHILEYOBJECT = new JvmType.Clazz("wyjc.runtime", "WyObject");
	public final static JvmType.Clazz WHILEYBOOL = new JvmType.Clazz("wyjc.runtime", "WyBool");
	public final static JvmType.Clazz WHILEYBYTE = new JvmType.Clazz("wyjc.runtime", "WyByte");
	public final static JvmType.Clazz WHILEYINT = new JvmType.Clazz("java.math", "BigInteger");
	public final static JvmType.Clazz WHILEYLAMBDA = new JvmType.Clazz("wyjc.runtime", "WyLambda");

	private static final JvmType.Clazz JAVA_LANG_CHARACTER = new JvmType.Clazz("java.lang", "Character");
	private static final JvmType.Clazz JAVA_LANG_SYSTEM = new JvmType.Clazz("java.lang", "System");
	private static final JvmType.Clazz JAVA_LANG_ITERABLE = new JvmType.Clazz("java.lang", "Iterable");
	public static final JvmType.Array JAVA_LANG_OBJECT_ARRAY = new JvmType.Array(JAVA_LANG_OBJECT);
	private static final JvmType.Clazz JAVA_UTIL_LIST = new JvmType.Clazz("java.util", "List");
	private static final JvmType.Clazz JAVA_UTIL_SET = new JvmType.Clazz("java.util", "Set");
	// private static final JvmType.Clazz JAVA_LANG_REFLECT_METHOD = new
	// JvmType.Clazz("java.lang.reflect","Method");
	private static final JvmType.Clazz JAVA_IO_PRINTSTREAM = new JvmType.Clazz("java.io", "PrintStream");
	private static final JvmType.Clazz JAVA_LANG_RUNTIMEEXCEPTION = new JvmType.Clazz("java.lang", "RuntimeException");
	private static final JvmType.Clazz JAVA_LANG_ASSERTIONERROR = new JvmType.Clazz("java.lang", "AssertionError");
	private static final JvmType.Clazz JAVA_UTIL_COLLECTION = new JvmType.Clazz("java.util", "Collection");

	private JvmType.Function convertFunType(Type.FunctionOrMethod ft) {
		ArrayList<JvmType> paramTypes = new ArrayList<JvmType>();
		for (Type pt : ft.params()) {
			paramTypes.add(convertUnderlyingType(pt));
		}
		JvmType rt;
		switch (ft.returns().size()) {
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
			for (Type bound : ut.bounds()) {
				JvmType r = convertUnderlyingType(bound);
				if (result == null) {
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
			} catch (InternalFailure ex) {
				throw ex;
			} catch (Exception e) {
				internalFailure("error expanding type: " + t, filename, e);
				return null; // deadcode
			}
		} else {
			throw new RuntimeException("unknown type encountered: " + t);
		}
	}

	/**
	 * Get the JvmType.Clazz associated with a given WyIL module.
	 * 
	 * @param mid
	 * @return
	 */
	private JvmType.Clazz getModuleClass(Path.ID mid) {
		return new JvmType.Clazz(mid.parent().toString().replace('/', '.'), mid.last());
	}

	/**
	 * Reverse an array of integer items
	 * 
	 * @param items
	 * @return
	 */
	private static int[] reverse(int[] items) {
		int[] rs = new int[items.length];
		int last = items.length - 1;
		for (int i = 0; i != rs.length; ++i) {
			rs[i] = items[last - i];
		}
		return rs;
	}

	/**
	 * Construct a list of modifiers from an array of (potentially null)
	 * modifiers.
	 * 
	 * @param mods
	 * @return
	 */
	private static List<Modifier> modifiers(Modifier... mods) {
		ArrayList<Modifier> modifiers = new ArrayList<Modifier>();
		for (Modifier m : mods) {
			if (m != null) {
				modifiers.add(m);
			}
		}
		return modifiers;
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

	private static String typeMangle(Type.FunctionOrMethod ft) throws IOException {
		JavaIdentifierOutputStream jout = new JavaIdentifierOutputStream();
		BinaryOutputStream binout = new BinaryOutputStream(jout);
		Type.BinaryWriter tm = new Type.BinaryWriter(binout);
		tm.write(ft);
		binout.close(); // force flush
		return jout.toString();
	}

	private static int[] append(int[] lhs, int... rhs) {
		int[] noperands = new int[lhs.length + rhs.length];
		System.arraycopy(lhs, 0, noperands, 0, lhs.length);
		System.arraycopy(rhs, 0, noperands, lhs.length, rhs.length);
		return noperands;
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

		public static int get(Constant value, HashMap<JvmConstant, Integer> constants) {
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

		public static int get(Type from, Type to, HashMap<JvmConstant, Integer> constants) {
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

		public UnresolvedHandler(String start, String end, String target, JvmType.Clazz exception) {
			this.start = start;
			this.end = end;
			this.target = target;
			this.exception = exception;
		}
	}

	public class Context {
		/**
		 * The code forest in which we are currently operating
		 */
		private final BytecodeForest forest;

		/**
		 * The list of bytecodes that have been generated so far
		 */
		private final ArrayList<Bytecode> bytecodes;

		/**
		 * The next available free register slot
		 */
		private final int freeSlot;
		
		/**
		 * Determine the branch target for a break statement
		 */
		private final String breakLabel;
		
		/**
		 * Determine the branch target for a continue statement
		 */
		private final String continueLabel;

		/**
		 * The index of the bytecode being translated
		 */
		private BytecodeForest.Index pc;

		public Context(int block, BytecodeForest forest) {
			this(forest,new BytecodeForest.Index(block, 0),forest.numLocations(),new ArrayList<Bytecode>(),null,null);
		}

		public Context(BytecodeForest forest, BytecodeForest.Index pc, int freeSlot, ArrayList<Bytecode> bytecodes,
				String breakLabel, String continueLabel) {
			this.forest = forest;
			this.bytecodes = bytecodes;
			this.pc = pc;
			this.freeSlot = freeSlot;
			this.breakLabel = breakLabel;
			this.continueLabel = continueLabel;
		}
		
		private Context(Context context) {
			this.forest = context.forest;
			this.bytecodes = context.bytecodes;
			this.pc = context.pc;
			this.freeSlot = context.freeSlot;
			this.breakLabel = context.breakLabel;
			this.continueLabel = context.continueLabel;
		}

		public Attribute attribute(Class<? extends Attribute> kind) {
			return forest.get(pc).attribute(kind);
		}

		public wyil.lang.Bytecode.Stmt getStatement() {
			return forest.get(pc).first();
		}

		public BytecodeForest.Location getLocation(int operand) {
			return forest.getLocation(operand);
		}

		public List<Bytecode> getBytecodes() {
			return bytecodes;
		}

		public BytecodeForest getBytecodeForest() {
			return forest;
		}
		
		public String getBreakLabel() {
			return breakLabel;
		}

		public String getContinueLabel() {
			return continueLabel;
		}
		
		public boolean hasNext() {
			BytecodeForest.Block b = forest.get(pc.block());
			return pc.offset() < b.size();
		}

		public void nextStatement() {
			pc = pc.next();
		}

		public Context newBlock(int block) {
			BytecodeForest.Index npc = new BytecodeForest.Index(block, 0);
			return new Context(forest, npc, freeSlot, bytecodes, breakLabel, continueLabel);
		}

		public Context newLoopBlock(int block, String breakLabel, String continueLabel) {
			BytecodeForest.Index npc = new BytecodeForest.Index(block, 0);
			return new Context(forest, npc, freeSlot, bytecodes, breakLabel, continueLabel);
		}
		
		public Context.Operand newOperand(int operand) {
			return new Context.Operand(operand, this);
		}

		public void add(Bytecode bytecode) {
			bytecodes.add(bytecode);
		}

		public void addReadConversion(Type type) {
			Wyil2JavaBuilder.this.addReadConversion(type, bytecodes);
		}

		public void addWriteConversion(Type type) {
			Wyil2JavaBuilder.this.addWriteConversion(type, bytecodes);
		}

		public void construct(JvmType.Clazz type) {
			Wyil2JavaBuilder.this.construct(type, bytecodes);
		}

		public JvmType toJvmType(Type type) {
			return convertUnderlyingType(type);
		}

		public class Operand extends Context {
			/**
			 * Index of operand within the bytecode statement we are executing
			 */
			private final int operand;

			public Operand(int operand, Context context) {
				super(context);
				this.operand = operand;
			}

			public BytecodeForest.Operand getOperand() {
				return (BytecodeForest.Operand) forest.getLocation(operand);
			}
		}
	}

	/**
	 * Provides a simple interface for translating individual bytecodes.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public interface BytecodeTranslator {
		void translate(Operator bytecode, Context.Operand context);
	}
}
