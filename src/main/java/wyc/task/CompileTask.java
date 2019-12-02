// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wyc.task;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;

import wyal.lang.WyalFile;
import wybs.lang.*;
import wybs.lang.Build.Meter;
import wybs.util.AbstractBuildTask;
import wybs.util.AbstractCompilationUnit.Name;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyc.io.WhileyFileParser;
import wyc.lang.WhileyFile;
import wycc.util.Logger;
import wyfs.lang.Path;
import wyil.check.*;
import wyil.lang.Compiler;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
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
public final class CompileTask extends AbstractBuildTask<WhileyFile, WyilFile> {
	private final Logger logger;
	/**
	 * Specify whether verification enabled or not
	 */
	private boolean verification;
	/**
	 * Specify whether counterexample generation is enabled or not
	 */
	private boolean counterexamples;
	/**
	 * Optional stage
	 */
	private final VerificationCheck verifier;
	/**
	 * The source root to find Whiley files. This is far from ideal.
	 */
	private final Path.Root sourceRoot;

	public CompileTask(Build.Project project, Path.Root sourceRoot, Path.Entry<WyilFile> target,
			Collection<Path.Entry<WhileyFile>> sources) throws IOException {
		super(project, target, sources);
		// FIXME: shouldn't need source root
		this.sourceRoot = sourceRoot;
		// Extract the logger for debug information
		this.logger = project.getEnvironment().getLogger();
		//
		this.verifier = new VerificationCheck(Build.NULL_METER,project,target);
	}

	public CompileTask setVerification(boolean flag) {
		this.verification = flag;
		return this;
	}

	public CompileTask setCounterExamples(boolean flag) {
		this.counterexamples = flag;
		return this;
	}

	@Override
	public Function<Meter,Boolean> initialise() throws IOException {
		// Extract target and source files for compilation. This is the component which
		// requires I/O.
		WyilFile wyil = target.read();
		WhileyFile[] whileys = new WhileyFile[sources.size()];
		for (int i = 0; i != whileys.length; ++i) {
			whileys[i] = sources.get(i).read();
		}
		// Construct the lambda for subsequent execution. This will eventually make its
		// way into some kind of execution pool, possibly for concurrent execution with
		// other tasks.
		return (Meter meter) -> execute(meter, wyil, whileys);
	}

	/**
	 * The business end of a compilation task. The intention is that this
	 * computation can proceed without performing any blocking I/O. This means it
	 * can be used in e.g. a forkjoin task safely.
	 *
	 * @param meter --- Records profiling information
	 * @param target  --- The WyilFile being written.
	 * @param sources --- The WhileyFiles being compiled.
	 * @return
	 */
	public boolean execute(Meter meter, WyilFile target, WhileyFile... sources) {
		try {
			meter = meter.fork("WhileyCompiler");
			// FIXME: this is something of a hack to handle the fact that this is not an
			// incremental compiler! Basically, we always start from scratch no matter what.
			WyilFile.Decl.Module module = (WyilFile.Decl.Module) target.getRootItem();
			target.setRootItem(new WyilFile.Decl.Module(module.getName(), new Tuple<>(), new Tuple<>(), new Tuple<>()));
			//
			boolean r = true;
			// Parse source files into target
			Meter parserMeter = meter.fork(WhileyFileParser.class.getSimpleName());
			for (int i = 0; i != sources.length; ++i) {
				// NOTE: this is somehow where we work out the initial deltas for incremental
				// compilation.
				WhileyFile source = sources[i];
				WhileyFileParser wyp = new WhileyFileParser(target, source);
				//
				r &= wyp.read(parserMeter);
			}
			parserMeter.done();
			// Perform name resolution.
			try {
				r = r && new NameResolution(meter, project, target).apply();
			} catch(IOException e) {
				// FIXME: this is clearly broken.
				throw new RuntimeException(e);
			}
			// ========================================================================
			// Flow Type Checking
			// ========================================================================
			// Instantiate type checker
			FlowTypeCheck checker = new FlowTypeCheck(meter);
			r = r && checker.check(target);
			// ========================================================================
			// Compiler Checks
			// ========================================================================
			Compiler.Check[] stages = instantiateChecks(meter);
			for (int i = 0; i != stages.length; ++i) {
				r = r && stages[i].check(target);
			}
			if(r && verification) {
				// NOTE: cannot generate verification conditions if WyilFile is in a bad state
				// (e.g. has unresolved links).
				WyalFile obligations = verifier.initialise(target);
				r = verifier.check(obligations,counterexamples);
			}
			// Transforms
			if (r) {
				Compiler.Transform[] transforms = instantiateTransforms(meter);
				// Only apply if previous stages have all passed.
				for (int i = 0; i != transforms.length; ++i) {
					transforms[i].apply(target);
				}
			}
			// Collect garbage
			//target.gc();
			//
			meter.done();
			// Done
			return r;
		} catch (SyntacticException e) {
			// FIXME: This conversion from WyilFile entry to WhileyFile entry seems like a
			// hack. WOuld be nicer if there was a different way.
			SyntacticItem item = e.getElement();
			Decl.Unit unit = item.getAncestor(Decl.Unit.class);
			for(int i=0;i!=sources.length;++i) {
				WhileyFile wf = sources[i];
				String n = new Name(wf.getEntry().id()).toString();
				if(n.endsWith(unit.getName().toString())) {
					throw new SyntacticException(e.getMessage(),wf.getEntry(),item,e.getCause());
				}
			}
			// Didn't find, default to fall back
			throw e;
		}
	}

	private static Compiler.Check[] instantiateChecks(Build.Meter m) {
		return new Compiler.Check[] {
				new DefiniteAssignmentCheck(m),
				new DefiniteUnassignmentCheck(m),
				new FunctionalCheck(m),
				new SignatureCheck(m),
				new StaticVariableCheck(m),
				new AmbiguousCoercionCheck(m)
		};
	}

	private static Compiler.Transform[] instantiateTransforms(Build.Meter meter) {
		return new Compiler.Transform[] {
				new MoveAnalysis(meter),
//				new RecursiveTypeAnalysis(meter)
		};
	}
}
