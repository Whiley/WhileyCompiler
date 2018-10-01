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

import static wyc.util.ErrorMessages.RESOLUTION_ERROR;
import static wyc.util.ErrorMessages.errorMessage;

import java.io.*;
import java.util.*;

import wyal.lang.WyalFile;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.Trie;
import wyil.lang.WyilFile;
import wyil.stage.MoveAnalysis;
import wyil.stage.RecursiveTypeAnalysis;
import wybs.lang.*;
import wybs.lang.SyntaxError.InternalFailure;
import wybs.util.*;
import wyc.check.AmbiguousCoercionCheck;
import wyc.check.DefiniteAssignmentCheck;
import wyc.check.DefiniteUnassignmentCheck;
import wyc.check.FlowTypeCheck;
import wyc.check.FunctionalCheck;
import wyc.check.StaticVariableCheck;
import wyc.io.WhileyFileParser;
import wyc.lang.*;
import wyc.util.WhileyFileResolver;
import wycc.cfg.Configuration;
import wycc.util.ArrayUtils;
import wycc.util.Logger;
import wycc.util.Pair;

/**
 * Responsible for managing the process of turning source files into binary code
 * for execution. Each source file is passed through a pipeline of stages that
 * modify it in a variety of ways. The main stages are:
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
public final class CompileTask implements Build.Task {

	/**
	 * The master project for identifying all resources available to the
	 * builder. This includes all modules declared in the project being compiled
	 * and/or defined in external resources (e.g. jar files).
	 */
	private final Build.Project project;

	/**
	 * Provides mechanism for operating on types. For example, expanding them
	 * and performing subtype tests, etc. This object may cache results to
	 * improve performance of some operations.
	 */
	private final NameResolver resolver;

	/**
	 * The logger used for logging system events
	 */
	private Logger logger;

	public CompileTask(Build.Project project) {
		this.logger = Logger.NULL;
		this.project = project;
		this.resolver = new WhileyFileResolver(project);
	}

	public String id() {
		return "wyc.builder";
	}

	@Override
	public Build.Project project() {
		return project;
	}

	/**
	 * Access the type system object this compile task is using.
	 *
	 * @return
	 */
	public NameResolver getNameResolver() {
		return resolver;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<Path.Entry<?>> build(Collection<Pair<Path.Entry<?>, Path.Root>> delta, Build.Graph graph)
			throws IOException {
		System.out.println("BUILDING: " + delta.size() + " files");
		// Identify the source compilation groups
		HashSet<Path.Entry<?>> targets = new HashSet<>();
		for (Pair<Path.Entry<?>, Path.Root> p : delta) {
			Path.Entry<?> entry = p.first();
			if (entry.contentType() == WhileyFile.ContentType) {
				targets.addAll(graph.getChildren((Path.Entry<WhileyFile>) entry));
			}
		}
		// Compile each one in turn
		for (Path.Entry<?> target : targets) {
			// FIXME: there is a problem here. That's because not every parent will be in
			// the delta. Therefore, this is forcing every file to be recompiled.
			List sources = graph.getParents(target);
			build((Path.Entry<WyilFile>) target, (List<Path.Entry<WhileyFile>>) sources);
		}
		// Done
		return targets;
	}

	public void build(Path.Entry<WyilFile> target, List<Path.Entry<WhileyFile>> sources) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		long startTime = System.currentTimeMillis();
		long startMemory = runtime.freeMemory();
		long tmpTime = startTime;
		long tmpMemory = startMemory;

		// ========================================================================
		// Parse source files
		// ========================================================================

		WyilFile wf = compile(sources, target);

		logger.logTimedMessage("Parsed " + sources.size() + " source file(s).", System.currentTimeMillis() - tmpTime,
				tmpMemory - runtime.freeMemory());

		// ========================================================================
		// Type Checking & Code Generation
		// ========================================================================

		runtime = Runtime.getRuntime();
		tmpTime = System.currentTimeMillis();
		tmpMemory = runtime.freeMemory();

		new FlowTypeCheck(this).check(wf);
		new DefiniteAssignmentCheck().check(wf);
		new DefiniteUnassignmentCheck(this).check(wf);
		new FunctionalCheck(this).check(wf);
		new StaticVariableCheck(this).check(wf);
		new AmbiguousCoercionCheck(this).check(wf);
		new MoveAnalysis(this).apply(wf);
		new RecursiveTypeAnalysis(this).apply(wf);
		// new CoercionCheck(this);

		logger.logTimedMessage("Generated code for " + sources.size() + " source file(s).",
				System.currentTimeMillis() - tmpTime, tmpMemory - runtime.freeMemory());

		// ========================================================================
		// Done
		// ========================================================================

		long endTime = System.currentTimeMillis();
		logger.logTimedMessage("Whiley => Wyil: compiled " + sources.size() + " file(s)", endTime - startTime,
				startMemory - runtime.freeMemory());
	}

	/**
	 * Compile one or more WhileyFiles into a given WyilFile
	 *
	 * @param source The source file being compiled.
	 * @param target The target file being generated.
	 * @return
	 * @throws IOException
	 */
	private WyilFile compile(List<Path.Entry<WhileyFile>> sources, Path.Entry<WyilFile> target) throws IOException {
		// Read target WyilFile. This may have already been compiled in a previous run
		// and, in such case, we are invalidating some or all of the existing file.
		WyilFile wyil = target.read();
		// Parse all modules
		for(int i=0;i!=sources.size();++i) {
			Path.Entry<WhileyFile> source = sources.get(i);
			WhileyFileParser wyp = new WhileyFileParser(wyil, source.read());
			// FIXME: what to do with module added to heap? The problem is that this might
			// be replaced a module, for example.
			WyilFile.Decl.Module module = wyp.read();
		}
		//
		target.flush();
		//
		return wyil;
	}
}
