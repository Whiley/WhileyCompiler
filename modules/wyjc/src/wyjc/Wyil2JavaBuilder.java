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
import wycc.util.ResolveError;
import wycc.util.Triple;
import wyfs.io.BinaryOutputStream;
import wyfs.lang.Path;
import wyfs.util.Trie;
import wyil.lang.*;
import wyil.lang.Constant;
import wyil.lang.SyntaxTree.Location;
import wyil.lang.Bytecode.Operator;
import wyil.lang.Bytecode.VariableDeclaration;

import static wyil.lang.Bytecode.*;
import wyil.util.TypeSystem;

import static wyil.util.ErrorMessages.internalFailure;
import static wyjc.Wyil2JavaBuilder.WHILEYBOOL;
import static wyjc.Wyil2JavaBuilder.WHILEYTYPE;
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
	 * The type system is useful for managing nominal types and converting them
	 * into their underlying types.
	 */
	protected final TypeSystem typeSystem;

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
		this.typeSystem = new TypeSystem(project);
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
			//new ClassFileVerifier().apply(contents);

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
		Context context = new Context();
		
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
				JvmType type = toJvmType(constant.type());
				ClassFile.Field field = new ClassFile.Field(name, type, fmods);
				cf.fields().add(field);
				// Now, create code to intialise this field
				translateConstant(constant, context);
				context.add(new Bytecode.PutField(owner, name, type, Bytecode.FieldMode.STATIC));
			}
		}

		if (nvalues > 0) {
			// create static initialiser method, but only if we really need to.
			context.add(new Bytecode.Return(null));
			List<Modifier> modifiers = modifiers(ACC_PUBLIC, ACC_STATIC, ACC_SYNTHETIC);
			JvmType.Function ftype = new JvmType.Function(new JvmType.Void());
			ClassFile.Method clinit = new ClassFile.Method("<clinit>", ftype, modifiers);
			cf.methods().add(clinit);
			// finally add code for staticinitialiser method			
			jasm.attributes.Code code = new jasm.attributes.Code(context.getBytecodes(), Collections.EMPTY_LIST, clinit);
			clinit.attributes().add(code);
		}
	}

	private ClassFile.Method buildMainLauncher(JvmType.Clazz owner) {
		List<Modifier> modifiers = modifiers(ACC_PUBLIC, ACC_SYNTHETIC, ACC_STATIC);
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
		List<Location<Expr>> td_invariants = td.getInvariant();
		//
		JvmType underlyingType = toJvmType(td.type());
		List<Modifier> modifiers = modifiers(ACC_PUBLIC, ACC_STATIC, ACC_SYNTHETIC);
		JvmType.Function funType = new JvmType.Function(T_BOOL, underlyingType);
		ClassFile.Method cm = new ClassFile.Method(td.name() + "$typeof", funType, modifiers);
		// Generate code for testing implicit invariants of type (if any). That
		// is, invariants implied by (nominal) component types.
		Context context = new Context();
		String falseLabel = freshLabel();
		translateInvariantTest(falseLabel, td.type(), 0, 1, context);
		// Generate code for testing explicit invariants of type (if any). To do
		// this, we chain them together into a sequence of checks.
		for (int i = 0; i != td_invariants.size(); ++i) {
			translateExpression(td_invariants.get(i), context);
			JvmType.Function ft = new JvmType.Function(JvmTypes.T_BOOL);
			context.add(new Bytecode.Invoke(WHILEYBOOL, "value", ft, Bytecode.InvokeMode.VIRTUAL));
			context.add(new Bytecode.If(Bytecode.IfMode.EQ, falseLabel));
		}			
		// If we reach this point, then invariants must hold.
		context.add(new Bytecode.LoadConst(true));
		context.add(new Bytecode.Return(new JvmType.Bool()));
		// Add the false label (in case it was used)
		context.add(new Bytecode.Label(falseLabel));
		context.add(new Bytecode.LoadConst(false));
		context.add(new Bytecode.Return(new JvmType.Bool()));
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
			bytecodes.add(new Bytecode.Load(slot++, toJvmType(param)));
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
			returnType = toJvmType(ft.returns().get(0));
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
		Context context = new Context();		
		translateBlock(method.getBody(),context);
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
	 * @param bytecodes
	 *            List of bytecodes being accumulated
	 */
	private void translateBlock(SyntaxTree.Location<Block> block, Context context) {
		for(int i=0;i!=block.numberOfOperands();++i) {
			translateStatement(block.getOperand(i), context);
		}
	}

	/**
	 * Translate a WyIL bytecode at a given index into one or more JVM
	 * bytecodes. The bytecode index is given to help with debugging (i.e. to
	 * extract attributes associated with the given bytecode).
	 * 
	 */
	private void translateStatement(Location<?> stmt, Context context) {
		try {
			switch (stmt.getOpcode()) {
			case OPCODE_assert:
			case OPCODE_assume:
				translateAssertOrAssume((Location<AssertOrAssume>) stmt, context);
				break;
			case OPCODE_assign:
				translateAssign((Location<Assign>) stmt, context);
				break;
			case OPCODE_break:
				translateBreak((Location<Break>) stmt, context);
				break;
			case OPCODE_continue:
				translateContinue((Location<Continue>) stmt, context);
				break;
			case OPCODE_debug:
				translateDebug((Location<Debug>) stmt, context);
				break;
			case OPCODE_dowhile:
				translateDoWhile((Location<DoWhile>) stmt, context);
				break;
			case OPCODE_fail:
				translateFail((Location<Fail>) stmt, context);
				break;
			case OPCODE_if:
			case OPCODE_ifelse:
				translateIf((Location<If>) stmt, context);
				break;
			case OPCODE_invoke:
			case OPCODE_indirectinvoke:
				translateInvokeAsStmt(stmt,context);
				break;
			case OPCODE_namedblock:
				translateNamedBlock((Location<NamedBlock>) stmt, context);
				break;
			case OPCODE_while:
				translateWhile((Location<While>) stmt, context);
				break;
			case OPCODE_return:
				translateReturn((Location<Return>) stmt, context);
				break;
			case OPCODE_skip:
				translateSkip((Location<Skip>) stmt, context);
				break;
			case OPCODE_switch:
				translateSwitch((Location<Switch>) stmt, context);
				break;
			case OPCODE_vardecl:
			case OPCODE_vardeclinit:
				translateVariableDeclaration((Location<VariableDeclaration>) stmt, context);
				break;	
			default:
				internalFailure("unknown bytecode encountered (" + stmt + ")", filename,
						stmt.attribute(Attribute.Source.class));
			}
		} catch (InternalFailure ex) {
			throw ex;
		} catch (Exception ex) {
			internalFailure(ex.getMessage(), filename, ex, stmt.attribute(Attribute.Source.class));
		}
	}

	private void translateAssertOrAssume(Location<AssertOrAssume> c, Context context) {
		String trueLabel = freshLabel();
		translateCondition(c.getOperand(0), trueLabel, null, context);
		context.construct(JAVA_LANG_RUNTIMEEXCEPTION);
		context.add(new Bytecode.Throw());
		context.add(new Bytecode.Label(trueLabel));
	}

	/**
	 * Translate an assignment statement.
	 * 
	 * @param code
	 * @param context
	 */
	private void translateAssign(Location<Assign> code, Context context) {
		Location<?>[] lhs = code.getOperandGroup(0);
		Location<?>[] rhs = code.getOperandGroup(1);
		// Translate and construct the lvals for this assignment. This
		// will store all lval operands (e.g. array indices) into their
		// corresponding operand register. To preserve the semantics of Whiley,
		// we must translate the lhs before the rhs.
		LVal[] lvals = translateLVals(lhs, context);
		// Translate the operands in reverse order onto the stack, produce a
		// type for each operand produced. The number of types may be larger
		// than the number of rhs opeands in the case of an operand with
		// multiple return values.
		List<JvmType> types = translateExpressions(rhs, context);

		// Now, store each operand into the slot location so that we can more
		// easily access it later. This basically relies on an assumption that
		// translateSimpleAssign() does not use any free registers during its
		// translation.
		int freeRegister = getFirstFreeRegister(code.getEnclosingTree());
		for (int i = types.size() - 1; i >= 0; i = i - 1) {
			context.add(new Bytecode.Store(freeRegister+i, types.get(i)));
		}
		// Assign each operand to the target lval.
		int i = 0;
		for (; i != lhs.length; ++i) {
			translateSimpleAssign(lvals[i], freeRegister+i, types.get(i), context);
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
	private void translateSimpleAssign(LVal lhs, int rhs, JvmType type, Context context) {
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
			context.add(new Bytecode.Load(lval.index.getIndex(), WHILEYINT));
			JvmType.Function getFunType = new JvmType.Function(JAVA_LANG_OBJECT, WHILEYARRAY, WHILEYINT);
			context.add(new Bytecode.Invoke(WHILEYARRAY, "internal_get", getFunType, Bytecode.InvokeMode.STATIC));
			context.addReadConversion(lval.type.element());
			translateUpdate(iterator, rhs, context);
			context.add(new Bytecode.Load(lval.index.getIndex(), WHILEYINT));
			context.add(new Bytecode.Swap());
		} else {
			// This is the innermost case, hence we can avoid the unnecessary
			// read of the current value and, instead, just return the rhs value
			// directly.
			Type type = lval.type.element();
			context.add(new Bytecode.Load(lval.index.getIndex(), WHILEYINT));
			context.add(new Bytecode.Load(rhs, toJvmType(type)));
			context.addWriteConversion(type);
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
			context.addReadConversion(type.field(lval.field));
			translateUpdate(iterator, rhs, context);
			context.add(new Bytecode.LoadConst(lval.field));
			context.add(new Bytecode.Swap());
		} else {
			// This is the innermost case, hence we can avoid the unnecessary
			// read of the current value and, instead, just return the rhs value
			// directly.
			context.add(new Bytecode.LoadConst(lval.field));
			context.add(new Bytecode.Load(rhs, toJvmType(type.field(lval.field))));
			context.addWriteConversion(type.field(lval.field));
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
			context.addReadConversion(lval.type.element());
			translateUpdate(iterator, rhs, context);
		} else {
			// This is the innermost case, hence we can avoid the unnecessary
			// read of the current value and, instead, just return the rhs value
			// directly.
			JvmType rhsJvmType = toJvmType(lval.type.element());
			context.add(new Bytecode.Load(rhs, rhsJvmType));
		}
		JvmType.Function setFunType = new JvmType.Function(WHILEYOBJECT, JAVA_LANG_OBJECT);
		context.add(new Bytecode.Invoke(WHILEYOBJECT, "setState", setFunType, Bytecode.InvokeMode.VIRTUAL));
	}

	private void translateBreak(Location<Break> c, Context context) {
		context.add(new Bytecode.Goto(context.getBreakLabel()));
	}
	
	private void translateContinue(Location<Continue> c, Context context) {
		context.add(new Bytecode.Goto(context.getContinueLabel()));
	}
	
	private void translateDebug(Location<Debug> c, Context context) {
		JvmType.Function ftype = new JvmType.Function(T_VOID, WHILEYARRAY);
		translateExpression(c.getOperand(0), context);
		context.add(new Bytecode.Invoke(WHILEYUTIL, "print", ftype, Bytecode.InvokeMode.STATIC));
	}

	private void translateDoWhile(Location<DoWhile> c, Context context) {
		// Allocate header label for loop
		String headerLabel = freshLabel();
		String breakLabel = freshLabel();
		context.add(new Bytecode.Label(headerLabel));
		// Translate body of loop.
		translateBlock(c.getBlock(0),context.newLoopBlock(breakLabel, headerLabel));
		// Translate the loop condition.
		translateCondition(c.getOperand(0), headerLabel, null, context);
		context.add(new Bytecode.Label(breakLabel));
	}

	private void translateFail(Location<Fail> c, Context context) {
		context.add(new Bytecode.New(JAVA_LANG_RUNTIMEEXCEPTION));
		context.add(new Bytecode.Dup(JAVA_LANG_RUNTIMEEXCEPTION));
		context.add(new Bytecode.LoadConst("runtime fault encountered"));
		JvmType.Function ftype = new JvmType.Function(T_VOID, JAVA_LANG_STRING);
		context.add(new Bytecode.Invoke(JAVA_LANG_RUNTIMEEXCEPTION, "<init>", ftype, Bytecode.InvokeMode.SPECIAL));
		context.add(new Bytecode.Throw());
	}

	private void translateIf(Location<If> code, Context context) {
		If bytecode = code.getBytecode();
		String exitLabel = freshLabel();
		String falseLabel = bytecode.hasFalseBranch() ? freshLabel() : exitLabel;
		// First, translate the condition
		translateCondition(code.getOperand(0), null, falseLabel, context);
		// Second, translate the true branch
		translateBlock(code.getBlock(SyntaxTree.TRUEBRANCH), context);
		if (bytecode.hasFalseBranch()) {
			// Third, translate false branch (if applicable)
			context.add(new Bytecode.Goto(exitLabel));
			context.add(new Bytecode.Label(falseLabel));
			translateBlock(code.getBlock(SyntaxTree.FALSEBRANCH), context);
			context.add(new Bytecode.Label(exitLabel));
		} else {
			context.add(new Bytecode.Label(falseLabel));
		}
	}

	private void translateReturn(Location<Return> c, Context context) {
		Location<?>[] operands = c.getOperands();
		JvmType rt;
		//
		switch (operands.length) {
		case 0:
			// No return value.
			rt = null;
			break;
		case 1:
			// Exactly one return value, so we can (potentially) return it
			// directly.
			Location<?> operand = operands[0];
			if(operand.numberOfTypes() == 1) {
				// Yes, we can return directly.
				translateExpression(operand, context);
				// Determine return type
				rt = toJvmType(operand.getType());
				break;
			}
		default:
			// More than one return value. In this case, we need to encode the
			// return values into an object array. This is annoying, but it's
			// because Java doesn't support multiple return values.
			int freeRegister = getFirstFreeRegister(c.getEnclosingTree());
			translateExpressionsToArray(operands, freeRegister, context);
			rt = JAVA_LANG_OBJECT_ARRAY;			
		}
		// Done
		context.add(new Bytecode.Return(rt));
	}

	private void translateSkip(Location<Skip> code, Context context) {
		context.add(new Bytecode.Nop()); // easy
	}
	
	private void translateSwitch(Location<Switch> code, Context context) {
		String exitLabel = freshLabel();
		Location<?> condition = code.getOperand(0);
		JvmType type = toJvmType(condition.getType());
		// Translate condition into a value and store into a temporary register.
		// This is necessary because, according to the semantics of Whiley, we
		// can only execute the condition expression once.
		translateExpression(condition, context);
		context.add(new Bytecode.Store(condition.getIndex(), type));
		ArrayList<String> labels = new ArrayList<String>();
		// Generate the dispatch table which checks the condition value against
		// each of the case constants. If a match is found, we branch to a given
		// label demarking the start of the case body (which will be translated
		// later). In principle, using a tableswitch bytecode would be better
		// here.
		boolean hasDefault = false;
		Case[] cases = code.getBytecode().cases();
		for (int i = 0; i != code.numberOfBlocks(); ++i) {
			Constant[] values = cases[i].values();
			String caseLabel = freshLabel();
			if (values.length == 0) {
				// In this case, we have a default target which corresponds to
				// an unconditional branch
				context.add(new Bytecode.Goto(caseLabel));
				hasDefault = true;
			} else {
				for (Constant value : values) {
					translateConstant(value, context);
					context.add(new Bytecode.Load(condition.getIndex(), type));
					JvmType.Function ftype = new JvmType.Function(T_BOOL, JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);
					context.add(new Bytecode.Invoke(WHILEYUTIL, "equals", ftype, Bytecode.InvokeMode.STATIC));
					context.add(new Bytecode.If(Bytecode.IfMode.NE, caseLabel));
				}
			}
			labels.add(caseLabel);
		}
		// If there was no default case, then add an unconditional branch over
		// the case bodies to the end of the switch.
		if (!hasDefault) {
			context.add(new Bytecode.Goto(exitLabel));
		}
		// Translate each of the case bodies in turn.
		for (int i = 0; i != cases.length; ++i) {
			context.add(new Bytecode.Label(labels.get(i)));
			translateBlock(code.getBlock(i), context);
			context.add(new Bytecode.Goto(exitLabel));
		}
		// Finally, mark out the exit point of the switch
		context.add(new Bytecode.Label(exitLabel));
	}

	private void translateNamedBlock(Location<NamedBlock> c, Context context) {
		translateBlock(c.getBlock(0),context);
	}
	
	private void translateWhile(Location<While> c, Context context) {
		// Allocate header label for loop
		String headerLabel = freshLabel();
		String exitLabel = freshLabel();
		context.add(new Bytecode.Label(headerLabel));
		// Translate the loop condition.
		translateCondition(c.getOperand(0), null, exitLabel, context);
		// Translate body of loop.
		translateBlock(c.getBlock(0),context.newLoopBlock(exitLabel, headerLabel));
		// Terminate loop by branching back to head of loop
		context.add(new Bytecode.Goto(headerLabel));
		// This is where we exit the loop
		context.add(new Bytecode.Label(exitLabel));
	}

	private void translateInvokeAsStmt(Location<?> stmt, Context context) {
		// First, translate the invocation
		List<Type> returns;		
		if(stmt.getOpcode() == OPCODE_invoke) {
			Location<Invoke> e = (Location<Invoke>) stmt; 			
			translateInvoke(e, context);
			returns = e.getBytecode().type().returns();
		} else {			
			Location<IndirectInvoke> e = (Location<IndirectInvoke>) stmt;
			translateIndirectInvoke(e, context);
			returns = e.getBytecode().type().returns();
		}		
		// Second, if there are results, pop them off the stack		
		for(int i=0;i!=returns.size();++i) {
			JvmType returnType = context.toJvmType(returns.get(i));
			context.add(new Bytecode.Pop(returnType));
		} 
	}
	
	private void translateVariableDeclaration(Location<VariableDeclaration> code, Context context) {
		if(code.numberOfOperands() > 0) {
			JvmType type = context.toJvmType(code.getType());
			translateExpression(code.getOperand(0),context);
			context.add(new Bytecode.Store(code.getIndex(),type));
		}
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
	public LVal[] translateLVals(Location<?>[] lvals, Context context) {
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
				translateExpression(ae.index, context);
				context.add(new Bytecode.Store(ae.index.getIndex(), WHILEYINT));
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
	public LVal generateLVal(Location<?> lval, Context context) {
		ArrayList<LVal.Element<?>> path = new ArrayList<LVal.Element<?>>();
		while (lval.getOpcode() != OPCODE_varaccess) {
			try {
				wyil.lang.Bytecode.Expr code = (wyil.lang.Bytecode.Expr) lval.getBytecode();
				switch (code.getOpcode()) {
				case OPCODE_fieldload:
					lval = lval.getOperand(0);
					Type.EffectiveRecord recType = typeSystem.expandAsEffectiveRecord(lval.getType());
					wyil.lang.Bytecode.FieldLoad fl = (wyil.lang.Bytecode.FieldLoad) code;
					path.add(new LVal.Record(recType, fl.fieldName()));
					break;
				case OPCODE_arrayindex:
					Location<?> index = lval.getOperand(1);
					lval = lval.getOperand(0);
					Type.EffectiveArray arrayType = typeSystem.expandAsEffectiveArray(lval.getType());
					path.add(new LVal.Array(arrayType, index));
					break;
				case OPCODE_dereference:
					lval = lval.getOperand(0);
					Type.Reference refType = typeSystem.expandAsReference(lval.getType());
					path.add(new LVal.Reference(refType));
					break;
				default:
					internalFailure("unknown bytecode encountered (" + code + ")", filename,
							lval.attribute(Attribute.Source.class));
				}
			} catch (ResolveError e) {
				internalFailure(e.getMessage(), filename, e, lval.attribute(Attribute.Source.class));
			}
		}
		// At this point, we have to reverse the lvals because they were put
		// into the array in the wrong order.
		Collections.reverse(path);
		// Done
		Location<VariableDeclaration> decl = getVariableDeclaration(lval);
		return new LVal(decl.getIndex(), path);
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
			public final Location<?> index;

			public Array(Type.EffectiveArray type, Location<?> index) {
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
	private void translateCondition(Location<?> condition, String trueLabel, String falseLabel, Context enclosing) {
		// First, handle the special cases as necessar.
		switch (condition.getOpcode()) {
		case OPCODE_logicalnot:
			translateNotCondition((Location<Operator>) condition, trueLabel, falseLabel, enclosing);
			return;
		case OPCODE_logicaland:
			translateShortcircuitAndCondition((Location<Operator>) condition, trueLabel, falseLabel, enclosing);
			return;
		case OPCODE_logicalor:
			translateShortcircuitOrCondition((Location<Operator>) condition, trueLabel, falseLabel, enclosing);
			return;
		case OPCODE_all:
		case OPCODE_some:
			translateQuantifierCondition((Location<Quantifier>) condition, trueLabel, falseLabel, enclosing);
			return;
		case OPCODE_is:
			translateIsCondition((Location<Operator>) condition, trueLabel, falseLabel, enclosing);
			return;
		}
		// We can't use a condition branch, so fall back to the default. In this
		// case, we just evaluate the condition as normal which will result in a
		// 1 or 0 being loaded on the stack. We can then perform a condition
		// branch from that.
		translateExpression(condition, enclosing);
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
	private void translateNotCondition(Location<?> code, String trueLabel, String falseLabel,
			Context enclosing) {
		// This case is very easy, as we can just swap the true and false
		// labels.
		translateCondition(code.getOperand(0), falseLabel, trueLabel, enclosing);
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
	private void translateShortcircuitAndCondition(Location<?> code, String trueLabel, String falseLabel,
			Context enclosing) {
		if (trueLabel == null) {
			translateCondition(code.getOperand(0), null, falseLabel, enclosing);
			translateCondition(code.getOperand(1), null, falseLabel, enclosing);
		} else {
			// implies falseLabel should be null
			String exitLabel = freshLabel();
			translateCondition(code.getOperand(0), null, exitLabel, enclosing);
			translateCondition(code.getOperand(1), trueLabel, null, enclosing);
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
	private void translateShortcircuitOrCondition(Location<?> code, String trueLabel, String falseLabel,
			Context enclosing) {
		if (trueLabel == null) {
			// implies false label is non-null
			String exitLabel = freshLabel();
			translateCondition(code.getOperand(0), exitLabel, null, enclosing);
			translateCondition(code.getOperand(1), null, falseLabel, enclosing);
			enclosing.add(new Bytecode.Label(exitLabel));
		} else {
			// implies false label is null
			translateCondition(code.getOperand(0), trueLabel, null, enclosing);
			translateCondition(code.getOperand(1), trueLabel, null, enclosing);
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
	private void translateQuantifierCondition(Location<?> condition, String trueLabel, String falseLabel,
			Context context) {
		String exitLabel = freshLabel();
		if(trueLabel == null) {
			trueLabel = exitLabel;
		} else {
			falseLabel = exitLabel;
		}
		translateQuantifierCondition(0, condition, trueLabel, falseLabel, context);
		// Add complete branch (if necessary)
		switch(condition.getOpcode()) {
		case OPCODE_all:
			if(trueLabel != exitLabel) {
				context.add(new Bytecode.Goto(trueLabel));
			}
			break;
		case OPCODE_some:
		default:
			if(falseLabel != exitLabel) {
				context.add(new Bytecode.Goto(falseLabel));
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
	private void translateQuantifierCondition(int index, Location<?> condition, String trueLabel,
			String falseLabel, Context context) {
		if (index == condition.numberOfOperandGroups()) {
			// This is the innermost case. At this point, we are in the body of
			// the innermost loop.  First, determine what is true and false :) 
			String myTrueLabel = null;
			String myFalseLabel = null;
			switch(condition.getOpcode()) {
			case OPCODE_all:
				// if condition false, terminate early and return false.
				myFalseLabel = falseLabel;
				break;
			case OPCODE_some:
			default:
				// if condition true, terminate early and return true.
				myTrueLabel = trueLabel;
				break;
			}
			// Translate the quantifier condition and, depending on the kind of
			// quantifier, branch to either the true or false label.
			translateCondition(condition.getOperand(0),myTrueLabel,myFalseLabel,context);
		} else {
			// This is the recursive case. Here, we need to construct an
			// appropriate loop to iterate through the range.
			Location<?>[] range = condition.getOperandGroup(index);
			Location<?> var = range[SyntaxTree.VARIABLE];
			Location<?> start = range[SyntaxTree.START];
			Location<?> end = range[SyntaxTree.END];
			translateExpression(start,context);
			translateExpression(end,context);
			context.add(new Bytecode.Store(end.getIndex(),WHILEYINT));
			context.add(new Bytecode.Store(var.getIndex(),WHILEYINT));
			// Create loop
			String headerLabel = freshLabel();
			String exitLabel = freshLabel();
			// Generate loop condition
			context.add(new Bytecode.Label(headerLabel));
			context.add(new Bytecode.Load(var.getIndex(),WHILEYINT));
			context.add(new Bytecode.Load(end.getIndex(),WHILEYINT));
			JvmType.Function ftype = new JvmType.Function(WHILEYBOOL, JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);
			context.add(new Bytecode.Invoke(WHILEYUTIL, "equal", ftype, Bytecode.InvokeMode.STATIC));
			ftype = new JvmType.Function(JvmTypes.T_BOOL);
			context.add(new Bytecode.Invoke(WHILEYBOOL, "value", ftype, Bytecode.InvokeMode.VIRTUAL));
			context.add(new Bytecode.If(Bytecode.IfMode.NE, exitLabel));
			// Recursively translate remainder of quantifier
			translateQuantifierCondition(index+1,condition,trueLabel,falseLabel,context);
			// Increment index variable
			context.add(new Bytecode.Load(var.getIndex(), WHILEYINT));
			context.add(new Bytecode.GetField(WHILEYINT, "ONE", WHILEYINT, Bytecode.FieldMode.STATIC));
			ftype = new JvmType.Function(WHILEYINT, WHILEYINT);
			context.add(new Bytecode.Invoke(WHILEYINT, "add", ftype, Bytecode.InvokeMode.VIRTUAL));
			context.add(new Bytecode.Store(var.getIndex(), WHILEYINT));
			// Branch back to top of loop
			context.add(new Bytecode.Goto(headerLabel));
			context.add(new Bytecode.Label(exitLabel));
		}
	}
	
	/**
	 * Translate a type test condition. This is done here to ensure that we cast
	 * the type appropriately before moving on to the true/false branches. For
	 * example:
	 * 
	 * <pre>
	 * function f(int|null x) -> (int r):
	 *     if x is int:
	 *         return x
	 *     else:
	 *         return 0
	 * </pre>
	 * 
	 * In this case, the type of <code>x</code> will be <code>Object</code>, and
	 * it must be cast to <code>int</code> on the true branch of the type test.
	 * 
	 * @param condition
	 *            Type test to evaluate to see whether it is true or false
	 * @param trueLabel
	 *            Destination for when the condition is true. If null, execution
	 *            continues to next logical instruction when condition holds.
	 * @param falseLabel
	 *            Destination for when the condition is false. If null,
	 *            execution continues to next logical instruction when condition
	 *            doesn't hold.
	 * @param context
	 *            Enclosing context
	 */
	private void translateIsCondition(Location<Operator> test, String trueLabel, String falseLabel,
			Context context) {
		Location<Const> rhs = (Location<Const>) test.getOperand(1);
		Type rhsType = ((Constant.Type) rhs.getBytecode().constant()).value();
		//
		// Translate the type test itself
		translateExpressions(test.getOperands(), context);
		JvmType.Function ftype = new JvmType.Function(WHILEYBOOL, JAVA_LANG_OBJECT);
		context.add(new Bytecode.Swap());
		context.add(new Bytecode.Invoke(WHILEYTYPE, "is", ftype, Bytecode.InvokeMode.VIRTUAL));
		// Add the necessary branching instruction
		JvmType.Function ft = new JvmType.Function(JvmTypes.T_BOOL);
		context.add(new Bytecode.Invoke(WHILEYBOOL, "value", ft, Bytecode.InvokeMode.VIRTUAL));
		// Determine the appropriate types for the true and false branches
		Pair<Type,Type> flowTypes = determineFlowTypes(test,context);
		// Add the necessary branch
		if(flowTypes == null) {
			// FIXME: bug here in case where constrained type is being tested
			
			// In this case, no retyping is possible. Therefore, we branch
			// directly to the relevant destination.
			if(trueLabel == null) {
				context.add(new Bytecode.If(Bytecode.IfMode.EQ, falseLabel));
			} else {		
				context.add(new Bytecode.If(Bytecode.IfMode.NE, trueLabel));
			}
		} else {
			// In this case, a variable is being tested directly and, hence,
			// needs to be retyped on both branches.
			Location<VariableAccess> lhs = (Location<VariableAccess>) test.getOperand(0);
			Location<VariableDeclaration> decl = getVariableDeclaration(lhs);
			String tmpFalseLabel = freshLabel();
			String tmpTrueLabel = freshLabel();
			if(trueLabel == null) {		
				context.add(new Bytecode.If(Bytecode.IfMode.NE, tmpTrueLabel));
				context.add(new Bytecode.Label(tmpFalseLabel));
				// This is the false branch. Apply the necessary cast.
				retypeLocation(lhs,flowTypes.second(),context);
				context.add(new Bytecode.Goto(falseLabel));
				context.add(new Bytecode.Label(tmpTrueLabel));
				// This is the true branch. Apply the necessary cast.
				retypeLocation(lhs,flowTypes.first(),context);
				// Now, check any invariants hold (or not)
				int freeRegister = getFirstFreeRegister(test.getEnclosingTree());
				translateInvariantTest(tmpFalseLabel, rhsType, decl.getIndex(), freeRegister, context);
			} else {
				context.add(new Bytecode.If(Bytecode.IfMode.EQ, tmpFalseLabel));
				// This is the true branch. Apply the necessary cast.
				retypeLocation(lhs,flowTypes.first(),context);
				// Now, check any invariants hold (or not)
				int freeRegister = getFirstFreeRegister(test.getEnclosingTree());
				translateInvariantTest(tmpFalseLabel, rhsType, decl.getIndex(), freeRegister, context);
				context.add(new Bytecode.Goto(trueLabel));
				// This is the false branch. Apply the necessary cast.
				context.add(new Bytecode.Label(tmpFalseLabel));
				retypeLocation(lhs,flowTypes.second(),context);
			}
		}
	}

	/**
	 * Determine the appropriate types for the true and false branches of a type
	 * test.These are critical to determining the correct type to cast the
	 * variable's contents to. The presence of constrained types complicates
	 * this. For example, consider:
	 *
	 * <pre>
	 *	 type nat is (int n) where n >= 0
	 *	
	 *	 function f(int|bool|null x) -> bool:
	 *	 if x is nat|bool:
	 *	 ...
	 *	 else:
	 *	 ...
	 * </pre>
	 * 
	 * 
	 * Here, the type of x on the true branch is int|bool, whilst on the false
	 * branch it is int|null. To correctly handle this, we need to determine
	 * maximal type which is fully consumed by another. In this case, the
	 * maximal type fully consumed by nat|bool is bool and, hence, the type on
	 * the false branch is int|bool|null - bool == int|null.
	 * 
	 * @param test
	 * @param enclosing
	 * @return
	 */
	private Pair<Type, Type> determineFlowTypes(Location<Operator> test, Context enclosing) {
		Location<?> lhs = test.getOperand(0);
		Location<Const> rhs = (Location<Const>) test.getOperand(1);		
		
		if(lhs.getBytecode() instanceof VariableAccess) {					
			Type maximalConsumedType;
			Type expandedLhsType;
			Type expandedRhsType;
			Type lhsType = lhs.getType();
			Type rhsType = ((Constant.Type) rhs.getBytecode().constant()).value();
			// Determine the maximally consumed type, and the underlying type.
			try {
				maximalConsumedType = typeSystem.getMaximallyConsumedType(rhsType);
				expandedLhsType = typeSystem.getUnderlyingType(lhsType);
				expandedRhsType = typeSystem.getUnderlyingType(rhsType);
			} catch (Exception e) {
				internalFailure("error computing maximally consumed type: " + rhsType, filename, e);
				return null;
			}
			// Create the relevant types
			Type typeOnTrueBranch = Type.intersect(expandedLhsType, expandedRhsType);
			Type typeOnFalseBranch = Type.intersect(expandedLhsType, Type.Negation(maximalConsumedType));

			return new Pair<Type,Type>(typeOnTrueBranch,typeOnFalseBranch);
		} else {
			return null;
		}
	}
	
	/**
	 * Retype a given variable. This is done by casting the variable into the
	 * appropriate type and assigning this over the original value.
	 * 
	 * @param location
	 *            Variable to be retyped.
	 * @param type
	 *            Type variable should become
	 * @param enclosing
	 *            Enclosing context.
	 */
	private void retypeLocation(Location<VariableAccess> location, Type type, Context enclosing) {
		Location<VariableDeclaration> decl = getVariableDeclaration(location);
		enclosing.add(new Bytecode.Load(decl.getIndex(), toJvmType(type)));
		enclosing.addReadConversion(type);
		enclosing.add(new Bytecode.Store(decl.getIndex(), toJvmType(type)));
	}
	
	/**
	 * Translate any invariants contained in a given type. In the case the
	 * invariant doesn't hold, we dispatch to a given false destination.
	 * Otherwise, we fall through to the following instruction.
	 * 
	 * @param falseTarget
	 *            Destination to branch if invariant is false
	 * @param type
	 *            Type whose invariants are being tested
	 * @param variableRegister
	 *            JVM register slot of variable which is being tested
	 * @param freeRegister
	 *            First free JVM register slot which can be used by this method
	 * @param context
	 */
	private void translateInvariantTest(String falseTarget, Type type, int variableRegister, int freeRegister,
			Context context) {
		//
		JvmType underlyingType = toJvmType(type);
		//
		if (type instanceof Type.Nominal) {
			Type.Nominal c = (Type.Nominal) type;
			Path.ID mid = c.name().module();
			JvmType.Clazz owner = new JvmType.Clazz(mid.parent().toString().replace('/', '.'), mid.last());
			JvmType.Function fnType = new JvmType.Function(new JvmType.Bool(), toJvmType(c));
			context.add(new Bytecode.Load(variableRegister, toJvmType(type)));
			context.add(new Bytecode.Invoke(owner, c.name().name() + "$typeof", fnType, Bytecode.InvokeMode.STATIC));
			context.add(new Bytecode.If(Bytecode.IfMode.EQ, falseTarget));
		} else if (type instanceof Type.Leaf) {
			// Do nout
		} else if (type instanceof Type.Reference) {
			Type.Reference rt = (Type.Reference) type;
			JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT);
			context.add(new Bytecode.Load(variableRegister, underlyingType));
			context.add(new Bytecode.Invoke(WHILEYOBJECT, "state", ftype, Bytecode.InvokeMode.VIRTUAL));
			context.addReadConversion(rt.element());
			context.add(new Bytecode.Store(freeRegister, toJvmType(rt.element())));
			translateInvariantTest(falseTarget, rt.element(), freeRegister, freeRegister + 1, context);
		} else if (type instanceof Type.EffectiveArray) {
			Type.EffectiveArray ts = (Type.EffectiveArray) type;
			Triple<String, String, String> loopLabels = translateLoopBegin(variableRegister, freeRegister, context);
			context.addReadConversion(ts.element());
			context.add(new Bytecode.Store(freeRegister + 1, toJvmType(ts.element())));
			translateInvariantTest(falseTarget, ts.element(), freeRegister + 1, freeRegister + 2, context);
			translateLoopEnd(loopLabels, context);
		} else if (type instanceof Type.Record) {
			Type.Record tt = (Type.Record) type;
			HashMap<String, Type> fields = tt.fields();
			ArrayList<String> fieldNames = new ArrayList<>(fields.keySet());
			Collections.sort(fieldNames);
			for (int i = 0; i != fieldNames.size(); ++i) {
				String field = fieldNames.get(i);
				Type fieldType = fields.get(field);
				JvmType underlyingFieldType = toJvmType(fieldType);
				context.add(new Bytecode.Load(variableRegister, underlyingType));
				context.add(new Bytecode.LoadConst(field));
				JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT, JAVA_LANG_STRING);
				context.add(new Bytecode.Invoke(WHILEYRECORD, "get", ftype, Bytecode.InvokeMode.VIRTUAL));
				context.addReadConversion(fieldType);
				context.add(new Bytecode.Store(freeRegister, underlyingFieldType));
				translateInvariantTest(falseTarget, fieldType, freeRegister, freeRegister + 1, context);
			}
		} else if (type instanceof Type.FunctionOrMethod) {
			// FIXME: this is clearly a bug. However, it's not completely
			// straightforward to fix, since there is currently no way to get
			// runtime type information about a function or method reference. In
			// principle, this could be encoded in the WyLambda in some way.
		} else if (type instanceof Type.Negation) {
			Type.Reference rt = (Type.Reference) type;
			String trueTarget = freshLabel();
			translateInvariantTest(trueTarget, rt.element(), variableRegister, freeRegister, context);
			context.add(new Bytecode.Goto(falseTarget));
			context.add(new Bytecode.Label(trueTarget));
		} else if (type instanceof Type.Union) {
			Type.Union ut = (Type.Union) type;
			String trueLabel = freshLabel();
			for (Type bound : ut.bounds()) {
				try {
					Type underlyingBound = typeSystem.getUnderlyingType(bound);
					String nextLabel = freshLabel();
					context.add(new Bytecode.Load(variableRegister, toJvmType(type)));
					translateTypeTest(nextLabel, underlyingBound, context);
					context.add(new Bytecode.Load(variableRegister, toJvmType(type)));
					context.addReadConversion(bound);
					context.add(new Bytecode.Store(freeRegister, toJvmType(bound)));
					translateInvariantTest(nextLabel, bound, freeRegister, freeRegister + 1, context);
					context.add(new Bytecode.Goto(trueLabel));
					context.add(new Bytecode.Label(nextLabel));
				} catch (ResolveError e) {
					internalFailure(e.getMessage(), filename, e);
				}
			}
			context.add(new Bytecode.Goto(falseTarget));
			context.add(new Bytecode.Label(trueLabel));
		} else {
			internalFailure("unknown type encountered: " + type, filename);
		}
	}
	
	/**
	 * Construct generic code for iterating over a collection (e.g. a Whiley
	 * List or Set). This code will not leave anything on the stack and will
	 * store the iterator variable in a given slot. This means that things can
	 * be passed on the stack from before the loop into the loop body.
	 * 
	 * @param freeSlot
	 *            The variable slot into which the iterator variable should be
	 *            stored.
	 * @param exitLabel
	 *            The label which will represents after the end of the loop.
	 * @param context
	 *            The enclosing context
	 */
	private Triple<String, String, String> translateLoopBegin(
			int sourceSlot, int freeSlot, Context context) {
		String loopHeader = freshLabel();
		String loopFooter = freshLabel();
		String loopExit = freshLabel();

		// First, call Collection.iterator() on the source collection and write
		// it into the free slot.
		context.add(new Bytecode.Load(sourceSlot, JAVA_LANG_ITERABLE));
		context.add(new Bytecode.Invoke(JAVA_LANG_ITERABLE, "iterator",
				new JvmType.Function(JAVA_UTIL_ITERATOR),Bytecode.InvokeMode.INTERFACE));
		context.add(new Bytecode.Store(freeSlot, JAVA_UTIL_ITERATOR));

		// Second, construct the loop header, which consists of the test to
		// check whether or not there are any elements left in the collection to
		// visit.
		context.add(new Bytecode.Label(loopHeader));
		context.add(new Bytecode.Load(freeSlot, JAVA_UTIL_ITERATOR));
		context.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "hasNext",
				new JvmType.Function(T_BOOL), Bytecode.InvokeMode.INTERFACE));
		context.add(new Bytecode.If(Bytecode.IfMode.EQ, loopExit));

		// Finally, get the current element out of the iterator by invoking
		// Iterator.next();
		context.add(new Bytecode.Load(freeSlot, JAVA_UTIL_ITERATOR));
		context.add(new Bytecode.Invoke(JAVA_UTIL_ITERATOR, "next",
				new JvmType.Function(JAVA_LANG_OBJECT),
				Bytecode.InvokeMode.INTERFACE));

		// Done
		return new Triple<>(loopHeader, loopFooter, loopExit);
	}

	private void translateLoopEnd(Triple<String, String, String> labels, Context context) {
		context.add(new Bytecode.Label(labels.second()));
		context.add(new Bytecode.Goto(labels.first()));
		context.add(new Bytecode.Label(labels.third()));
	}
	
	/**
	 * The purpose of this method is to translate a type test. We're testing to
	 * see whether what's on the top of the stack (the value) is a subtype of
	 * the type being tested. Note, constants must be provided as a parameter
	 * 
	 * @param falseLabel
	 *            Destination to branch if not true
	 * @param type
	 *            Type being tested
	 * @param context
	 *            Enclosing context
	 */
	protected void translateTypeTest(String falseLabel, Type test, Context context) {
		// First, try for the easy cases
		if (test instanceof Type.Null) {
			// Easy case
			context.add(new Bytecode.If(Bytecode.IfMode.NONNULL, falseLabel));
		} else if (test instanceof Type.Bool) {
			context.add(new Bytecode.InstanceOf(WHILEYBOOL));
			context.add(new Bytecode.If(Bytecode.IfMode.EQ, falseLabel));
		} else if (test instanceof Type.Int) {
			context.add(new Bytecode.InstanceOf(WHILEYINT));
			context.add(new Bytecode.If(Bytecode.IfMode.EQ, falseLabel));
		} else {
			// Fall-back to an external (recursive) check
			Constant constant = new Constant.Type(test);
			int id = JvmValue.get(constant, constants);
			String name = "constant$" + id;
			context.add(new Bytecode.GetField(owner, name, WHILEYTYPE, Bytecode.FieldMode.STATIC));
			JvmType.Function ftype = new JvmType.Function(WHILEYBOOL, JAVA_LANG_OBJECT);
			context.add(new Bytecode.Swap());
			context.add(new Bytecode.Invoke(WHILEYTYPE, "is", ftype, Bytecode.InvokeMode.VIRTUAL));
			JvmType.Function ft = new JvmType.Function(JvmTypes.T_BOOL);
			context.add(new Bytecode.Invoke(WHILEYBOOL, "value", ft, Bytecode.InvokeMode.VIRTUAL));
			context.add(new Bytecode.If(Bytecode.IfMode.EQ, falseLabel));
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
	public List<JvmType> translateExpressions(Location<?>[] operands, Context enclosing) {
		ArrayList<JvmType> types = new ArrayList<JvmType>();
		for (int i = 0; i != operands.length; ++i) {
			Location<?> operand = operands[i];
			// Translate operand
			translateExpression(operand, enclosing);
			// Determine type(s) for operand
			for (int j = 0; j != operand.numberOfTypes(); ++j) {
				types.add(toJvmType(operand.getType(j)));
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
	 * @param freeRegister
	 *            First free JVM register slot which can be used by this method
	 * @param context
	 *            The enclosing context for this operand.
	 */
	private void translateExpressionsToArray(Location<?>[] operands, int freeRegister, Context context) {
		int size = countLocationTypes(operands);
		ArrayList<Type> types = new ArrayList<Type>();
		// Translate every operand giving size elements on stack
		for (int i = 0; i < operands.length; i = i + 1) {
			Location<?> operand = operands[i];
			// Translate expression itself
			translateExpression(operand, context);
			// record the types now loaded on the stack
			for (int j = 0; j != operand.numberOfTypes(); ++j) {
				types.add(operand.getType(j));
			}
		}
		// Construct the target array
		context.add(new Bytecode.LoadConst(size));
		context.add(new Bytecode.New(JAVA_LANG_OBJECT_ARRAY));
		context.add(new Bytecode.Store(freeRegister, JAVA_LANG_OBJECT_ARRAY));
		// Process each stack element in turn. This has to be done in reverse
		// order since that's how they're laid out on the stack
		for (int i = size - 1; i >= 0; i = i - 1) {
			context.add(new Bytecode.Load(freeRegister, JAVA_LANG_OBJECT_ARRAY));
			context.add(new Bytecode.Swap());
			context.add(new Bytecode.LoadConst(i));
			context.add(new Bytecode.Swap());
			context.addWriteConversion(types.get(i));
			context.add(new Bytecode.ArrayStore(JAVA_LANG_OBJECT_ARRAY));
		}
		// Done
		context.add(new Bytecode.Load(freeRegister, JAVA_LANG_OBJECT_ARRAY));
	}

	private int countLocationTypes(Location<?>...locations) {
		int count = 0 ;
		for(int i=0;i!=locations.length;++i) {
			count += locations[i].numberOfTypes();
		}
		return count;
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
	private void translateExpression(Location<?> expr, Context context) {
		try {
			switch (expr.getOpcode()) {
			case OPCODE_convert:
				translateConvert((Location<Convert>) expr, context);
				break;
			case OPCODE_const:
				translateConst((Location<Const>) expr, context);
				break;
			case OPCODE_fieldload:
				translateFieldLoad((Location<FieldLoad>) expr, context);
				break;
			case OPCODE_all:
			case OPCODE_some:
				translateQuantifier((Location<Quantifier>) expr, context);
				break;
			case OPCODE_indirectinvoke:
				translateIndirectInvoke((Location<IndirectInvoke>) expr, context);
				break;
			case OPCODE_invoke:
				translateInvoke((Location<Invoke>) expr, context);
				break;
			case OPCODE_lambda:
				translateLambda((Location<Lambda>) expr, context);
				break;
			case OPCODE_varaccess:
				translateVariableAccess((Location<VariableAccess>) expr, context);
				break;
			default:				
				translateOperator((Location<Operator>) expr, context);				
			}
		} catch(InternalFailure ex) {
			throw ex;
		} catch (Exception ex) {
			internalFailure(ex.getMessage(), filename, ex, expr.attributes());
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
	private void translateConst(Location<Const> c, Context context) throws ResolveError {
		Constant constant = c.getBytecode().constant();
		// At this point, we need to normalise the constant. This is because the
		// constant may involve one or more nominal types. These types don't
		// make sense at runtime, and we want to get rid of them where possible.
		constant = normalise(constant);		
		JvmType jt = toJvmType(constant.type());
		// Check whether this constant can be translated as a primitive, and
		// encoded directly within a bytecode.
		if (constant instanceof Constant.Bool || constant instanceof Constant.Null
				|| constant instanceof Constant.Byte) {
			// Yes, it can.
			translateConstant(constant, context);
		} else {
			// No, this needs to be constructed elsewhere as a static field.
			// Then, we can load it onto the stack here.
			int id = JvmValue.get(constant, constants);
			String name = "constant$" + id;
			context.add(new Bytecode.GetField(owner, name, jt, Bytecode.FieldMode.STATIC));
		}
	}

	/**
	 * Make sure any type constants are fully expanded. This is to ensure that
	 * nominal types are not present at runtime (unless they are specifically
	 * protected in some way).
	 * 
	 * @param constant
	 * @return
	 */
	private Constant normalise(Constant constant) throws ResolveError {		
		if(constant instanceof Constant.Type) {
			Constant.Type ct = (Constant.Type) constant;
			Type type = ct.value();
			Type underlyingType;			
			underlyingType = typeSystem.getUnderlyingType(type);			
			return new Constant.Type(underlyingType);
		} else {
			return constant;
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
	private void translateConvert(Location<Convert> c, Context context) {
		// Translate the operand
		translateExpression(c.getOperand(0), context);
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
	private void translateFieldLoad(Location<FieldLoad> c, Context context) throws ResolveError {
		Location<?> srcOperand = c.getOperand(0);
		Type.EffectiveRecord type = typeSystem.expandAsEffectiveRecord(srcOperand.getType());
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT, WHILEYRECORD, JAVA_LANG_STRING);
		// Translate the source operand
		translateExpression(srcOperand, context);
		context.add(new Bytecode.LoadConst(c.getBytecode().fieldName()));
		// Load the field out of the resulting record
		context.add(new Bytecode.Invoke(WHILEYRECORD, "get", ftype, Bytecode.InvokeMode.STATIC));
		// Add a read conversion (if necessary) to unbox the value
		context.addReadConversion(type.field(c.getBytecode().fieldName()));
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
	private void translateOperator(Location<Operator> c, Context context) throws ResolveError {
		// First, translate each operand and load its value onto the stack
		switch (c.getOpcode()) {
		case OPCODE_logicaland:
		case OPCODE_logicalor:
			// These two operators need to be handled specially because they
			// require short-circuiting semantics.
			translateExpressionAsCondition(c,context);
			break;
		case OPCODE_record:
			translateRecordConstructor(c, context);
			break;
		case OPCODE_array:
			translateArrayConstructor(c, context);
			break;
		default:			
			translateExpressions(c.getOperands(), context);
			// Second, dispatch to a specific translator for this opcode kind.
			generators[c.getOpcode()].translate(c, context);
		}
	}

	public void translateExpressionAsCondition(Location<Operator> bytecode, Context context) throws ResolveError {
		String trueLabel = freshLabel();
		String exitLabel = freshLabel();
		translateCondition(bytecode,trueLabel,null,context);
		context.add(new Bytecode.GetField(WHILEYBOOL, "FALSE", WHILEYBOOL, Bytecode.FieldMode.STATIC));
		context.add(new Bytecode.Goto(exitLabel));
		context.add(new Bytecode.Label(trueLabel));
		context.add(new Bytecode.GetField(WHILEYBOOL, "TRUE", WHILEYBOOL, Bytecode.FieldMode.STATIC));
		context.add(new Bytecode.Label(exitLabel));
	}
	
	/**
	 * Translate a RecordConstructor operand.
	 * 
	 * @param bytecode
	 * @param context
	 */
	public void translateRecordConstructor(Location<Operator> bytecode, Context context) throws ResolveError {
		Type.EffectiveRecord recType = typeSystem.expandAsEffectiveRecord(bytecode.getType());
		JvmType.Function ftype = new JvmType.Function(WHILEYRECORD, WHILEYRECORD, JAVA_LANG_STRING, JAVA_LANG_OBJECT);

		context.construct(WHILEYRECORD);

		ArrayList<String> keys = new ArrayList<String>(recType.fields().keySet());
		Collections.sort(keys);
		for (int i = 0; i != bytecode.getOperands().length; i++) {
			String key = keys.get(i);
			Type fieldType = recType.field(key);
			context.add(new Bytecode.LoadConst(key));
			translateExpression(bytecode.getOperand(i), context);
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
	private void translateArrayConstructor(Location<Operator> code, Context context) throws ResolveError {
		Type.EffectiveArray arrType = typeSystem.expandAsEffectiveArray(code.getType());
		JvmType.Function initJvmType = new JvmType.Function(T_VOID, T_INT);
		JvmType.Function ftype = new JvmType.Function(WHILEYARRAY, WHILEYARRAY, JAVA_LANG_OBJECT);

		context.add(new Bytecode.New(WHILEYARRAY));
		context.add(new Bytecode.Dup(WHILEYARRAY));
		context.add(new Bytecode.LoadConst(code.getOperands().length));
		context.add(new Bytecode.Invoke(WHILEYARRAY, "<init>", initJvmType, Bytecode.InvokeMode.SPECIAL));

		for (int i = 0; i != code.getOperands().length; ++i) {
			translateExpression(code.getOperand(i), context);
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
	private void translateLambda(Location<Lambda> c, Context context) {
		Lambda bytecode = c.getBytecode();
		Location<?>[] environment = c.getOperandGroup(1);
		int lambda_id = lambdaClasses.size();
		String lambdaMethod = "$lambda" + lambda_id;
		JvmType[] jvmEnvironment = buildLambdaEnvironment(environment);
		// First, we construct and instantiate the lambda
		ClassFile lambda = buildLambda(owner, lambdaMethod, bytecode.type(), jvmEnvironment);
		lambdaClasses.add(lambda);
		//
		context.add(new Bytecode.New(lambda.type()));
		context.add(new Bytecode.Dup(lambda.type()));
		for (int i = 0; i != environment.length; ++i) {
			Location<?> e = environment[i];
			context.add(new Bytecode.Load(e.getIndex(), jvmEnvironment[i]));
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
	private void translateInvoke(Location<Invoke> c, Context context) {
		Invoke bytecode = c.getBytecode();
		// Translate each operand and load its value onto the stack
		translateExpressions(c.getOperands(), context);
		// Construct the invocation bytecode
		context.add(createMethodInvocation(bytecode.name(), bytecode.type()));
		// 
		List<Type> returnTypes = bytecode.type().returns();
		if(returnTypes.size() > 1) {
			decodeOperandArray(returnTypes,context);
		}
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
	private void translateIndirectInvoke(Location<IndirectInvoke> c, Context context) {
		IndirectInvoke bytecode = c.getBytecode();
		Type.FunctionOrMethod ft = bytecode.type();
		JvmType.Clazz owner = (JvmType.Clazz) toJvmType(ft);
		// First, translate reference operand which returns the function/method
		// object we will dispatch upon. This extends the WyLambda class.
		translateExpression(c.getOperand(0), context);
		// Second, translate each argument and store it into an object array
		int freeRegister = getFirstFreeRegister(c.getEnclosingTree());
		translateExpressionsToArray(c.getOperandGroup(0), freeRegister, context);
		// Third, make the indirect method or function call. This is done by
		// invoking the "call" method on the function / method object returned
		// from the reference operand.
		JvmType.Function type = new JvmType.Function(JAVA_LANG_OBJECT, JAVA_LANG_OBJECT_ARRAY);
		context.add(new Bytecode.Invoke(owner, "call", type, Bytecode.InvokeMode.VIRTUAL));
		// Cast return value to expected type
		List<Type> returnTypes = bytecode.type().returns();
		if (returnTypes.size() == 1) {
			JvmType returnType = toJvmType(ft.returns().get(0));
			context.addCheckCast(returnType);
		} else if (returnTypes.size() > 1) {
			decodeOperandArray(returnTypes, context);
		}
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
	private void translateQuantifier(Location<Quantifier> condition, Context context) {
		String trueLabel = freshLabel();
		String exitLabel = freshLabel();
		translateQuantifierCondition(condition, trueLabel, null, context);
		context.add(new Bytecode.GetField(WHILEYBOOL, "FALSE", WHILEYBOOL, Bytecode.FieldMode.STATIC));
		context.add(new Bytecode.Goto(exitLabel));
		context.add(new Bytecode.Label(trueLabel));
		context.add(new Bytecode.GetField(WHILEYBOOL, "TRUE", WHILEYBOOL, Bytecode.FieldMode.STATIC));
		context.add(new Bytecode.Label(exitLabel));
	}
	
	/**
	 * Translate a variable access into a simple variable load instruction
	 * 
	 * @param condition
	 *            Operand to evaluate to see whether it is true or false
	 * @param enclosing
	 *            Enclosing context
	 */
	private void translateVariableAccess(Location<VariableAccess> expr, Context context) {		
		JvmType type = context.toJvmType(expr.getType());
		Location<VariableDeclaration> decl = getVariableDeclaration(expr);
		context.add(new Bytecode.Load(decl.getIndex(), type));
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
	private void translateConstant(Constant v, Context context) {
		if (v instanceof Constant.Null) {
			translateConstant((Constant.Null) v, context);
		} else if (v instanceof Constant.Bool) {
			translateConstant((Constant.Bool) v, context);
		} else if (v instanceof Constant.Byte) {
			translateConstant((Constant.Byte) v, context);
		} else if (v instanceof Constant.Integer) {
			translateConstant((Constant.Integer) v, context);
		} else if (v instanceof Constant.Type) {
			translateConstant((Constant.Type) v, context);
		} else if (v instanceof Constant.Array) {
			translateConstant((Constant.Array) v, context);
		} else if (v instanceof Constant.Record) {
			translateConstant((Constant.Record) v, context);
		} else if (v instanceof Constant.FunctionOrMethod) {
			translateConstant((Constant.FunctionOrMethod) v, context);
		} else if (v instanceof Constant.Type) {
			translateConstant((Constant.Type) v, context);
		} else {
			throw new IllegalArgumentException("unknown value encountered:" + v);
		}
	}

	protected void translateConstant(Constant.Null e, Context context) {
		context.add(new Bytecode.LoadConst(null));
	}

	protected void translateConstant(Constant.Bool e, Context context) {
		context.add(new Bytecode.LoadConst(e.value()));
		JvmType.Function ftype = new JvmType.Function(WHILEYBOOL, T_BOOL);
		context.add(new Bytecode.Invoke(WHILEYBOOL, "valueOf", ftype, Bytecode.InvokeMode.STATIC));
	}

	protected void translateConstant(Constant.Type e, Context context) {
		JavaIdentifierOutputStream jout = new JavaIdentifierOutputStream();
		BinaryOutputStream bout = new BinaryOutputStream(jout);
		Type.BinaryWriter writer = new Type.BinaryWriter(bout);
		try {
			writer.write(e.value());
			writer.close();
		} catch (IOException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}

		context.add(new Bytecode.LoadConst(jout.toString()));
		JvmType.Function ftype = new JvmType.Function(WHILEYTYPE, JAVA_LANG_STRING);
		context.add(new Bytecode.Invoke(WHILEYTYPE, "valueOf", ftype, Bytecode.InvokeMode.STATIC));
	}

	protected void translateConstant(Constant.Byte e, Context context) {
		context.add(new Bytecode.LoadConst(e.value()));
		JvmType.Function ftype = new JvmType.Function(WHILEYBYTE, T_BYTE);
		context.add(new Bytecode.Invoke(WHILEYBYTE, "valueOf", ftype, Bytecode.InvokeMode.STATIC));
	}

	protected void translateConstant(Constant.Integer e, Context context) {
		BigInteger num = e.value();

		if (num.bitLength() < 32) {
			context.add(new Bytecode.LoadConst(num.intValue()));
			context.add(new Bytecode.Conversion(T_INT, T_LONG));
			JvmType.Function ftype = new JvmType.Function(WHILEYINT, T_LONG);
			context.add(new Bytecode.Invoke(WHILEYINT, "valueOf", ftype, Bytecode.InvokeMode.STATIC));
		} else if (num.bitLength() < 64) {
			context.add(new Bytecode.LoadConst(num.longValue()));
			JvmType.Function ftype = new JvmType.Function(WHILEYINT, T_LONG);
			context.add(new Bytecode.Invoke(WHILEYINT, "valueOf", ftype, Bytecode.InvokeMode.STATIC));
		} else {
			// in this context, we need to use a byte array to construct the
			// integer object.
			byte[] bytes = num.toByteArray();
			JvmType.Array bat = new JvmType.Array(JvmTypes.T_BYTE);

			context.add(new Bytecode.New(WHILEYINT));
			context.add(new Bytecode.Dup(WHILEYINT));
			context.add(new Bytecode.LoadConst(bytes.length));
			context.add(new Bytecode.New(bat));
			for (int i = 0; i != bytes.length; ++i) {
				context.add(new Bytecode.Dup(bat));
				context.add(new Bytecode.LoadConst(i));
				context.add(new Bytecode.LoadConst(bytes[i]));
				context.add(new Bytecode.ArrayStore(bat));
			}

			JvmType.Function ftype = new JvmType.Function(T_VOID, bat);
			context.add(new Bytecode.Invoke(WHILEYINT, "<init>", ftype, Bytecode.InvokeMode.SPECIAL));
		}

	}

	protected void translateConstant(Constant.Array lv, Context context) {
		context.add(new Bytecode.New(WHILEYARRAY));
		context.add(new Bytecode.Dup(WHILEYARRAY));
		context.add(new Bytecode.LoadConst(lv.values().size()));
		JvmType.Function ftype = new JvmType.Function(T_VOID, T_INT);
		context.add(new Bytecode.Invoke(WHILEYARRAY, "<init>", ftype, Bytecode.InvokeMode.SPECIAL));

		ftype = new JvmType.Function(T_BOOL, JAVA_LANG_OBJECT);
		for (Constant e : lv.values()) {
			context.add(new Bytecode.Dup(WHILEYARRAY));
			translateConstant(e, context);
			context.addWriteConversion(e.type());
			context.add(new Bytecode.Invoke(WHILEYARRAY, "add", ftype, Bytecode.InvokeMode.VIRTUAL));
			context.add(new Bytecode.Pop(T_BOOL));
		}
	}

	protected void translateConstant(Constant.Record expr, Context context) {
		JvmType.Function ftype = new JvmType.Function(JAVA_LANG_OBJECT, JAVA_LANG_OBJECT, JAVA_LANG_OBJECT);
		context.construct(WHILEYRECORD);
		for (Map.Entry<String, Constant> e : expr.values().entrySet()) {
			Type et = e.getValue().type();
			context.add(new Bytecode.Dup(WHILEYRECORD));
			context.add(new Bytecode.LoadConst(e.getKey()));
			translateConstant(e.getValue(), context);
			context.addWriteConversion(et);
			context.add(new Bytecode.Invoke(WHILEYRECORD, "put", ftype, Bytecode.InvokeMode.VIRTUAL));
			context.add(new Bytecode.Pop(JAVA_LANG_OBJECT));
		}
	}

	/**
	 * Translate a constant representing a given function or method into an
	 * instance of a special lambda class. The lambda class extends WyLambda and
	 * looks just like any other lambda function. However, when invoked, it will
	 * call the function or method determined by the class directly.
	 */
	protected void translateConstant(Constant.FunctionOrMethod c, Context context) {
		// First, build the lambda
		NameID target = c.name();
		ClassFile lambda = buildLambda(getModuleClass(target.module()), target.name(), c.type());
		lambdaClasses.add(lambda);
		// Finally, construct an instance of the class itself
		context.construct(lambda.type());
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
	public ClassFile.Method buildLambdaMethod(Location<Lambda> e, String lambdaMethod, Context context) {
		Lambda l = e.getBytecode();
		JvmType.Function type = buildLambdaMethodType(l.type(), e.getOperandGroup(1));
		Context bodyContext = new Context();
		// Move environment into correct register slots. First, push them all
		// onto the stack
		shiftLambdaMethodParameters(e.getOperandGroup(0), e.getOperandGroup(1), bodyContext);
		// Translate the body of the lambda expression
		translateExpression(e.getOperand(0), bodyContext);
		// Add the return value (if applicable)
		if (type.returnType() instanceof JvmType.Void) {
			bodyContext.add(new Bytecode.Return(null));
		} else {
			bodyContext.add(new Bytecode.Return(type.returnType()));
		}
		// Create the method itself
		String lambdaMethodMangled = nameMangle(lambdaMethod, l.type());
		List<Modifier> modifiers = modifiers(ACC_PUBLIC, ACC_STATIC, ACC_FINAL);
		ClassFile.Method method = new ClassFile.Method(lambdaMethodMangled, type, modifiers);
		// Add the code attribute
		jasm.attributes.Code code = new jasm.attributes.Code(bodyContext.getBytecodes(), Collections.EMPTY_LIST,
				method);
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
	private void shiftLambdaMethodParameters(Location<?>[] parameters, Location<?>[] environment, Context bodyContext) {
		parameters = append(parameters,environment);
		for(int i=0;i!=parameters.length;++i) {
			Location<?> p = parameters[i];
			int slot = p.getIndex();
			if(slot != i) {
				bodyContext.add(new Bytecode.Load(i, toJvmType(p.getType())));
			}
		}
		// Second, pop them all into the right register
		for(int i=parameters.length-1;i>=0;--i) {
			Location<?> p = parameters[i];
			int slot = p.getIndex();
			if(slot != i) {
				bodyContext.add(new Bytecode.Store(slot, toJvmType(p.getType())));
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
	public JvmType.Function buildLambdaMethodType(Type.FunctionOrMethod type, Location<?>[] environment) {
		JvmType.Function jvmType = convertFunType(type);
		ArrayList<JvmType> actualParameterTypes = new ArrayList<JvmType>(jvmType.parameterTypes());
		for (int i = 0; i != environment.length; ++i) {
			Location<?> loc = environment[i];
			actualParameterTypes.add(toJvmType(loc.getType()));
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
	public JvmType[] buildLambdaEnvironment(Location<?>[] environment) {
		JvmType[] envTypes = new JvmType[environment.length];
		for (int i = 0; i != environment.length; ++i) {
			Location<?> loc = environment[i];
			envTypes[i] = toJvmType(loc.getType());
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


	public Location<VariableDeclaration> getVariableDeclaration(Location<?> decl) {
		switch (decl.getOpcode()) {
		case OPCODE_aliasdecl:
		case OPCODE_varaccess:
			return getVariableDeclaration(decl.getOperand(0));
		case OPCODE_vardecl:
		case OPCODE_vardeclinit:
			return (Location<VariableDeclaration>) decl;
		default:
			throw new RuntimeException("internal failure --- dead code reached");
		}
	}
	
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

	/**
	 * Given an object array on the stack, take everything out of it an leave it
	 * on the stack. This is needed to help handle multiple returns which are
	 * packaged into object arrays.
	 * 
	 * @param types
	 *            The list of types which are stored in the object array
	 * @param context
	 */
	private void decodeOperandArray(List<Type> types, Context context) {
		for (int i = 0; i != types.size(); ++i) {
			Type type = types.get(i);
			context.add(new Bytecode.Dup(JAVA_LANG_OBJECT_ARRAY));
			context.add(new Bytecode.LoadConst(i));
			context.add(new Bytecode.ArrayLoad(JAVA_LANG_OBJECT_ARRAY));
			context.addReadConversion(type);
			// At this point, we have the value on top of the stack and then the
			// array reference. So, we can just swap them to get the desired
			// order. 
			context.add(new Bytecode.Swap());			
		}
		context.add(new Bytecode.Pop(JAVA_LANG_OBJECT_ARRAY));
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
			paramTypes.add(toJvmType(pt));
		}
		JvmType rt;
		switch (ft.returns().size()) {
		case 0:
			rt = T_VOID;
			break;
		case 1:
			// Single return value
			rt = toJvmType(ft.returns().get(0));
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
	private JvmType toJvmType(Type t) {
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
				JvmType r = toJvmType(bound);
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
				Type expanded = typeSystem.getUnderlyingType(t);
				return toJvmType(expanded);
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

	private static <T> T[] append(T[] lhs, T... rhs) {
		T[] noperands = Arrays.copyOf(lhs,lhs.length+rhs.length);		
		System.arraycopy(rhs, 0, noperands, lhs.length, rhs.length);
		return noperands;
	}
	
	private static int getFirstFreeRegister(SyntaxTree tree) {
		return tree.getLocations().size();
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

	public class Context {
		/**
		 * The list of bytecodes that have been generated so far
		 */
		private final ArrayList<Bytecode> bytecodes;

		/**
		 * Determine the branch target for a break statement
		 */
		private final String breakLabel;
		
		/**
		 * Determine the branch target for a continue statement
		 */
		private final String continueLabel;

		public Context() {
			this(new ArrayList<Bytecode>(),null,null);
		}

		public Context(ArrayList<Bytecode> bytecodes, String breakLabel, String continueLabel) {
			this.bytecodes = bytecodes;
			this.breakLabel = breakLabel;
			this.continueLabel = continueLabel;
		}
	
		public List<Bytecode> getBytecodes() {
			return bytecodes;
		}

		public String getBreakLabel() {
			return breakLabel;
		}

		public String getContinueLabel() {
			return continueLabel;
		}
		
		public Context newLoopBlock(String breakLabel, String continueLabel) {
			return new Context(bytecodes, breakLabel, continueLabel);
		}

		/**
		 * The write conversion is necessary in situations where we're write a value
		 * from the stack into a collection (e.g. WhileyList, WhileySet, etc). In
		 * such case, we need to convert boolean values from bool primitives to
		 * Boolean objects.
		 */
		public void addWriteConversion(Type type) {
			// This currently does nothing since there are currently no data types
			// implemented as primitives.
		}
		
		/**
		 * The read conversion is necessary in situations where we're reading a
		 * value from a collection (e.g. WhileyList, WhileySet, etc) and then
		 * putting it on the stack. In such case, we need to convert boolean values
		 * from Boolean objects to bool primitives.
		 */
		public void addReadConversion(Type type) {
			// This doesn't do anything extra since there are currently no data
			// types implemented as primitives.
			addCheckCast(toJvmType(type));
		}
		
		private void addCheckCast(JvmType type) {
			// The following can happen in situations where a variable has type
			// void. In principle, we could remove this as obvious dead-code, but
			// for now I just avoid it.
			if (type instanceof JvmType.Void) {
				return;
			} else if (!type.equals(JAVA_LANG_OBJECT)) {
				// pointless to add a cast for object
				add(new Bytecode.CheckCast(type));
			}
		}
		
		/**
		 * The construct method provides a generic way to construct a Java object.
		 *
		 * @param owner
		 * @param context
		 * @param params
		 */
		private void construct(JvmType.Clazz owner) {
			add(new Bytecode.New(owner));
			add(new Bytecode.Dup(owner));
			ArrayList<JvmType> paramTypes = new ArrayList<JvmType>();
			JvmType.Function ftype = new JvmType.Function(T_VOID, paramTypes);
			add(new Bytecode.Invoke(owner, "<init>", ftype, Bytecode.InvokeMode.SPECIAL));
		}

		public JvmType toJvmType(Type type) {
			return Wyil2JavaBuilder.this.toJvmType(type);
		}
		
		public Type.EffectiveArray expandAsEffectiveArray(Type type) throws ResolveError {
			return typeSystem.expandAsEffectiveArray(type);
		}

		public Type.EffectiveRecord expandAsEffectiveRecord(Type type) throws ResolveError {
			return typeSystem.expandAsEffectiveRecord(type);
		}

		public Type.Reference expandAsReference(Type type) throws ResolveError {
			return typeSystem.expandAsReference(type);
		}

		public void add(Bytecode bytecode) {
			bytecodes.add(bytecode);
		}
	}

	/**
	 * Provides a simple interface for translating individual bytecodes.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public interface BytecodeTranslator {
		void translate(Location<Operator> bytecode, Context context) throws ResolveError;
	}
}
