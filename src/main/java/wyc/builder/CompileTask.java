// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyc.builder;

import static wyil.util.ErrorMessages.RESOLUTION_ERROR;
import static wyil.util.ErrorMessages.errorMessage;

import java.io.*;
import java.util.*;

import wyal.lang.WyalFile;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.Trie;
import wyil.checks.CoercionCheck;
import wyil.lang.*;
import wyil.util.MoveAnalysis;
import wybs.lang.*;
import wybs.lang.SyntaxError.InternalFailure;
import wybs.util.*;
import wyc.lang.*;
import wyc.type.TypeSystem;
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
	private final TypeSystem typeSystem;

	/**
	 * The logger used for logging system events
	 */
	private Logger logger;

	/**
	 * A map of the source files currently being compiled.
	 */
	private final HashMap<Path.ID, Path.Entry<WhileyFile>> srcFiles = new HashMap<>();

	/**
	 * The import cache caches specific import queries to their result sets.
	 * This is extremely important to avoid recomputing these result sets every
	 * time. For example, the statement <code>import whiley.lang.*</code>
	 * corresponds to the triple <code>("whiley.lang",*,null)</code>.
	 */
	private final HashMap<Trie, ArrayList<Path.ID>> importCache = new HashMap<>();

	public CompileTask(Build.Project project) {
		this.logger = Logger.NULL;
		this.project = project;
		this.typeSystem = new TypeSystem(project);
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
	public TypeSystem getTypeSystem() {
		return typeSystem;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<Path.Entry<?>> build(Collection<Pair<Path.Entry<?>, Path.Root>> delta, Build.Graph graph)
			throws IOException {
		Runtime runtime = Runtime.getRuntime();
		long startTime = System.currentTimeMillis();
		long startMemory = runtime.freeMemory();
		long tmpTime = startTime;
		long tmpMemory = startMemory;

		// ========================================================================
		// Parse and register source files
		// ========================================================================

		srcFiles.clear();
		int count = 0;
		for (Pair<Path.Entry<?>, Path.Root> p : delta) {
			Path.Entry<?> src = p.first();
			if (src.contentType() == WhileyFile.ContentType) {
				Path.Entry<WhileyFile> sf = (Path.Entry<WhileyFile>) src;
				sf.read(); // force file to be parsed
				count++;
				srcFiles.put(sf.id(), sf);
			}
		}

		logger.logTimedMessage("Parsed " + count + " source file(s).", System.currentTimeMillis() - tmpTime,
				tmpMemory - runtime.freeMemory());

		// ========================================================================
		// Flow Type source files
		// ========================================================================

		runtime = Runtime.getRuntime();
		tmpTime = System.currentTimeMillis();
		tmpMemory = runtime.freeMemory();

		ArrayList<WyilFile> files = new ArrayList<>();
		for (Pair<Path.Entry<?>, Path.Root> p : delta) {
			Path.Entry<?> entry = p.first();
			if (entry.contentType() == WhileyFile.ContentType) {
				Path.Entry<WhileyFile> source = (Path.Entry<WhileyFile>) entry;
				// Parse Whiley source file. This may produce errors at this
				// stage, which means compilation of this file cannot proceed
				WhileyFile wf = source.read();
				// Write WyIL skeleton. This is a stripped down version of the
				// source file which is easily translated into a temporary
				// WyilFile. This is needed for resolution.
				Path.Root dst = p.second();
				Path.Entry<WyilFile> target = dst.create(entry.id(), WyilFile.ContentType);
				WyilFile wif = new WyilFile(target,wf);
				files.add(wif);
				target.write(wif);
				// Register the derivation in the build graph. This is important
				// to understand what a particular intermediate file was
				// derived from.
				graph.registerDerivation(source, target);
			}
		}

		try {
			FlowTypeChecker flowChecker = new FlowTypeChecker(this);
			flowChecker.check(files);

			logger.logTimedMessage("Typed " + count + " source file(s).", System.currentTimeMillis() - tmpTime,
					tmpMemory - runtime.freeMemory());

			// ========================================================================
			// Code Generation
			// ========================================================================

			runtime = Runtime.getRuntime();
			tmpTime = System.currentTimeMillis();
			tmpMemory = runtime.freeMemory();

			HashSet<Path.Entry<?>> generatedFiles = new HashSet<>();
			for (WyilFile wf : files) {
				new DefiniteAssignmentAnalysis(wf).check();
				new FunctionalCheck(wf).check();
				// new MoveAnalysis(this).apply(wyil);
			}

			logger.logTimedMessage("Generated code for " + count + " source file(s).", System.currentTimeMillis() - tmpTime,
					tmpMemory - runtime.freeMemory());

			// ========================================================================
			// Pipeline Stages
			// ========================================================================

			for (Pair<Path.Entry<?>, Path.Root> p : delta) {
				Path.Entry<?> src = p.first();
				Path.Root dst = p.second();
				Path.Entry<WyilFile> wf = dst.get(src.id(), WyilFile.ContentType);
				// process(wf.read(), new CoercionCheck(this));
			}

			// ========================================================================
			// Done
			// ========================================================================

			long endTime = System.currentTimeMillis();
			logger.logTimedMessage("Whiley => Wyil: compiled " + delta.size() + " file(s)", endTime - startTime,
					startMemory - runtime.freeMemory());

			return generatedFiles;
		} catch(SyntaxError e) {
			// FIXME: this is not super pretty but, for now, it works. The goal
			// is to translate the entry from the WyIL file to the originating
			// WhileyFile.
			Path.Entry<?> source = graph.parent(e.getEntry());
			throw new SyntaxError(e.getMessage(),source,e.getElement(),e);
		}
	}
}
