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

import java.io.*;
import java.util.*;

import wyfs.lang.Path;
import wyil.check.AmbiguousCoercionCheck;
import wyil.check.DefiniteAssignmentCheck;
import wyil.check.DefiniteUnassignmentCheck;
import wyil.check.FlowTypeCheck;
import wyil.check.FunctionalCheck;
import wyil.check.StaticVariableCheck;
import wyil.check.VerificationCheck;
import wyil.lang.WyilFile;
import wyil.lang.Compiler;
import wyil.transform.MoveAnalysis;
import wyil.transform.NameResolution;
import wyil.transform.RecursiveTypeAnalysis;
import wybs.lang.*;
import wybs.util.AbstractBuildTask;
import wyc.io.WhileyFileParser;
import wyc.lang.*;

/**
 * Responsible for managing the process of turning source files into binary code
 * for execution. Each source file is passed through a pipeline of stages that
 * modify it in a variet	y of ways. The main stages are:
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
public final class CompileTask extends AbstractBuildTask<WhileyFile,WyilFile> {

	/**
	 * Specify whether verification enabled or not
	 */
	private boolean verification;
	/**
	 * Specify whether counterexample generation is enabled or not
	 */
	private boolean counterexamples;

	/**
	 * The source root to find Whiley files. This is far from ideal.
	 */
	private final Path.Root sourceRoot;

	/**
	 * Type checking stage. After name resolution, this must run before any other
	 * stage, as all other stages depend on it.
	 */
	private final FlowTypeCheck checker;

	/**
	 * The set of compiler checks. These check the generated WyilFile is valid.
	 */
	private final Compiler.Check[] stages;

	/**
	 * The set of transforms. These perform certain transformations on the generated
	 * WyilFile.
	 */
	private final Compiler.Transform[] transforms;

	public CompileTask(Build.Project project, Path.Root sourceRoot, Path.Entry<WyilFile> target,
			Collection<Path.Entry<WhileyFile>> sources) {
		super(project,target,sources);
		// FIXME: shouldn't need source root
		this.sourceRoot = sourceRoot;
		// Instantiate type checker
		this.checker = new FlowTypeCheck();
		// Instantiate other checks
		this.stages = new Compiler.Check[]{
				new DefiniteAssignmentCheck(),
				new DefiniteUnassignmentCheck(),
				new FunctionalCheck(),
				new StaticVariableCheck(),
				new AmbiguousCoercionCheck()
		};
		// Instantiate various transformations
		this.transforms = new Compiler.Transform[] {
				new MoveAnalysis(),
				new RecursiveTypeAnalysis()
		};
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
	public boolean apply() {
		Runtime runtime = Runtime.getRuntime();
		long start = System.currentTimeMillis();
		long memory = runtime.freeMemory();
		//
		boolean r;
		//
		try {
			// ========================================================================
			// Parsing
			// ========================================================================
			WyilFile wf = compile(sources, target);
			// ========================================================================
			// Name Resolution
			// ========================================================================
			r = new NameResolution(project, wf).apply();
			// ========================================================================
			// Flow Type Checking
			// ========================================================================
			r = r && checker.check(wf);
			// ========================================================================
			// Compiler Checks
			// ========================================================================
			for (int i = 0; i != stages.length; ++i) {
				r = r && stages[i].check(wf);
			}
			//
			if (verification) {
				// FIXME: this obviously doesn't fit.
				new VerificationCheck(project, sourceRoot, counterexamples).apply(target, sources);
			}
			// Transforms
			if (r) {
				// Only apply if previous stages have all passed.
				for (int i = 0; i != transforms.length; ++i) {
					transforms[i].apply(wf);
				}
			}
			// ========================================================================
			// Done
			// ========================================================================
			long endTime = System.currentTimeMillis();
			project.getLogger().logTimedMessage("Whiley => Wyil: compiled " + sources.size() + " file(s)",
					endTime - start, memory - runtime.freeMemory());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		//
		return r;
	}

	/**
	 * Compile one or more WhileyFiles into a given WyilFile
	 *
	 * @param source The source file being compiled.
	 * @param target The target file being generated.
	 * @return
	 * @throws IOException
	 */
	private static WyilFile compile(List<Path.Entry<WhileyFile>> sources, Path.Entry<WyilFile> target) throws IOException {
		// Read target WyilFile. This may have already been compiled in a previous run
		// and, in such case, we are invalidating some or all of the existing file.
		WyilFile wyil = target.read();
		// Parse all modules
		for(int i=0;i!=sources.size();++i) {
			Path.Entry<WhileyFile> source = sources.get(i);
			WhileyFileParser wyp = new WhileyFileParser(wyil, source.read());
			// FIXME: what to do with module added to heap? The problem is that this might
			// be replaced a module, for example.
			wyil.getModule().putUnit(wyp.read());
		}
		//
		return wyil;
	}
}
