package wyc.task;

import java.io.IOException;
import java.util.*;

import wycc.util.ArrayUtils;
import wycc.util.Pair;
import wycc.util.Trie;
import wycc.util.AbstractCompilationUnit.Name;
import wycc.util.AbstractCompilationUnit.Tuple;
import wyc.io.WhileyFileParser;
import wyc.lang.WhileyFile;
import wyil.check.DefiniteAssignmentCheck;
import wyil.check.DefiniteUnassignmentCheck;
import wyil.check.FlowTypeCheck;
import wyil.check.FunctionalCheck;
import wyil.check.RecursiveTypeCheck;
import wyil.check.SignatureCheck;
import wyil.check.StaticVariableCheck;
import wyil.check.UnsafeCheck;
import wyil.lang.Compiler;
import wyil.lang.WyilFile;
import wyil.transform.MoveAnalysis;
import wyil.transform.NameResolution;

/**
 * Responsible for managing the process of turning source files into binary code
 * for execution. Each source file is passed through a pipeline of stages that
 * modify it in a variet y of ways. The main stages are:
 * <ol>
 * <li>
 * <p>
 * <b>Lexing and Parsing</b>, where the source file is converted into an
 * Abstract Syntax Tree (AST) representation.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>Name Resolution</b>, where the fully qualified names of all external
 * symbols are determined.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>Type Propagation</b>, where the types of all expressions are determined by
 * propagation from e.g. declared parameter types.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>WYIL Generation</b>, where the the AST is converted into the Whiley
 * Intermediate Language (WYIL). A number of passes are then made over this
 * before it is ready for code generation.
 * </p>
 * </li>
 * <li>
 * <p>
 * <b>Code Generation</b>. Here, the executable code is finally generated. This
 * could be Java bytecode, or something else (e.g. JavaScript).
 * </p>
 * </li>
 * </ol>
 * Every stage of the compiler can be configured by setting various options.
 * Stages can also be bypassed (typically for testing) and new ones can be
 * added.
 *
 * @author David J. Pearce
 *
 */
public class CompileTask {
	/**
	 * The set of build packages that this task relies on.
	 */
	private final List<WyilFile> packages;
	/**
	 * Identifier for target of this build task.
	 */
	private final Trie target;
	/**
	 * Determines strictness around unsafe
	 */
	private boolean strict = false;
	/**
	 * Determines whether to fully a link a binary (or not).
	 */
	private boolean linking = false;

	public CompileTask(Trie target) {
		this.target = target;
		this.packages = Collections.emptyList();
	}

	public CompileTask(Trie target, Collection<WyilFile> dependencies) {
		this.target = target;
		this.packages = new ArrayList<>(dependencies);
	}

	public CompileTask setStrict(boolean flag) {
		this.strict = flag;
		return this;
	}

	public CompileTask setLinking(boolean flag) {
		if(flag) {
			throw new IllegalArgumentException("Linking is not supported as it is currently not viable. See #1131");
		}
		this.linking = flag;
		return this;
	}

	public Pair<WyilFile, Boolean> compile(List<WhileyFile> sources) {
		WyilFile target = new WyilFile(sources);
		// Construct root entry
		target.setRootItem(
				new WyilFile.Decl.Module(new Name(this.target), new Tuple<>(), new Tuple<>(), new Tuple<>()));
		// Identify success or failure
		boolean r = true;
		// Parse all source files
		for (int i = 0; i != sources.size(); ++i) {
			// Read ith source file
			WhileyFile source = sources.get(i);
			// Construct parser
			WhileyFileParser wyp = new WhileyFileParser(target, source);
			// Parse it
			r &= wyp.read();
		}
		// Perform name resolution.
		try {
			r = r && new NameResolution(packages, target, linking).apply();
		} catch (IOException e) {
			// FIXME: this is clearly broken.
			throw new RuntimeException(e);
		}
		// ========================================================================
		// Recursive Type Check
		// ========================================================================
		new RecursiveTypeCheck().check(target);
		// ========================================================================
		// Flow Type Checking
		// ========================================================================
		// Instantiate type checker
		FlowTypeCheck checker = new FlowTypeCheck();
		r = r && checker.check(target);
		// ========================================================================
		// Compiler Checks
		// ========================================================================
		Compiler.Check[] stages = instantiateChecks(strict);
		for (int i = 0; i != stages.length; ++i) {
			r = r && stages[i].check(target);
		}
//		if(r && verification) {
//			// NOTE: cannot generate verification conditions if WyilFile is in a bad state
//			// (e.g. has unresolved links).
//			WyalFile obligations = verifier.initialise(target);
//			r = verifier.check(obligations,counterexamples);
//		}
		// Transforms
		if (r) {
			Compiler.Transform[] transforms = instantiateTransforms();
			// Only apply if previous stages have all passed.
			for (int i = 0; i != transforms.length; ++i) {
				transforms[i].apply(target);
			}
		}
		// Collect garbage
		// target.gc();
		// FIXME: how to handle errors?
		//
		return new Pair<>(target, r);
	}

	private static Compiler.Check[] instantiateChecks(boolean strict) {
		Compiler.Check[] checks = new Compiler.Check[] { new DefiniteAssignmentCheck(),
				new DefiniteUnassignmentCheck(), new FunctionalCheck(), new SignatureCheck(),
				new StaticVariableCheck() };
		if (strict) {
			checks = ArrayUtils.append(checks, new UnsafeCheck());
		}
		return checks;
	}

	private static Compiler.Transform[] instantiateTransforms() {
		return new Compiler.Transform[] { new MoveAnalysis(),
//				new RecursiveTypeAnalysis(meter)
		};
	}
}
