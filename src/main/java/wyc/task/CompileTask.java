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

import wyal.lang.WyalFile;
import wyal.util.Interpreter;
import wyal.util.NameResolver;
import wyal.util.SmallWorldDomain;
import wyal.util.TypeChecker;
import wyal.util.WyalFileResolver;
import wyfs.lang.Path;
import wyil.check.AmbiguousCoercionCheck;
import wyil.check.DefiniteAssignmentCheck;
import wyil.check.DefiniteUnassignmentCheck;
import wyil.check.FlowTypeCheck;
import wyil.check.FunctionalCheck;
import wyil.check.StaticVariableCheck;
import wyil.lang.WyilFile;
import wyil.lang.WyilFile.Decl;
import wyil.transform.MoveAnalysis;
import wyil.transform.NameResolution;
import wyil.transform.RecursiveTypeAnalysis;
import wyil.transform.VerificationConditionGenerator;
import wytp.provers.AutomatedTheoremProver;
import wytp.types.extractors.TypeInvariantExtractor;
import wybs.lang.*;
import wybs.lang.CompilationUnit.Name;
import wybs.lang.SyntaxError.InternalFailure;
import wyc.io.WhileyFileParser;
import wyc.lang.*;
import wycc.util.Logger;
import wycc.util.Pair;

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
public final class CompileTask implements Build.Task {

	/**
	 * The master project for identifying all resources available to the
	 * builder. This includes all modules declared in the project being compiled
	 * and/or defined in external resources (e.g. jar files).
	 */
	private final Build.Project project;

	/**
	 * The source root to find Whiley files. This is far from ideal.
	 */
	private final Path.Root sourceRoot;

	/**
	 * Specify whether verification enabled or not
	 */
	private boolean verification;
	/**
	 * Specify whether counterexample generation is enabled or not
	 */
	private boolean counterexamples;

	public CompileTask(Build.Project project, Path.Root sourceRoot) {
		this.project = project;
		this.sourceRoot = sourceRoot;
	}

	public String id() {
		return "wyc.builder";
	}

	@Override
	public Build.Project project() {
		return project;
	}

	public CompileTask setVerification(boolean flag) {
		this.verification = flag;
		return this;
	}

	public CompileTask setCounterExamples(boolean flag) {
		this.counterexamples = flag;
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<Path.Entry<?>> build(Collection<Pair<Path.Entry<?>, Path.Root>> delta, Build.Graph graph)
			throws IOException {
		// Identify the source compilation groups
		HashSet<Path.Entry<?>> targets = new HashSet<>();
		for (Pair<Path.Entry<?>, Path.Root> p : delta) {
			Path.Entry<?> entry = p.first();
			if (entry.contentType() == WhileyFile.ContentType) {
				targets.addAll(graph.getChildren(entry));
			}
		}
		// Determine which were successfully built
		HashSet<Path.Entry<?>> built = new HashSet<>();
		// Compile each one in turn
		for (Path.Entry<?> target : targets) {
			// FIXME: there is a problem here. That's because not every parent will be in
			// the delta. Therefore, this is forcing every file to be recompiled.
			List sources = graph.getParents(target);
			boolean ok = build((Path.Entry<WyilFile>) target, (List<Path.Entry<WhileyFile>>) sources);
			// Record whether target built successfully or not
			if(ok) {
				built.add(target);
			}
		}
		// Done
		return built;
	}

	public boolean build(Path.Entry<WyilFile> target, List<Path.Entry<WhileyFile>> sources) throws IOException {
		if(!build(sourceRoot, target, sources)) {
			return false;
		} else if (verification) {
			verify(sourceRoot, target, sources);
		}
		return true;
	}

	public boolean build(Path.Root sourceRoot, Path.Entry<WyilFile> target, List<Path.Entry<WhileyFile>> sources)
			throws IOException {
		Logger logger = project.getLogger();

		try {
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

			boolean r = new NameResolution(project,wf).apply();
			// Compiler checks
			r = r && new FlowTypeCheck().check(wf);
			r = r && new DefiniteAssignmentCheck().check(wf);
			r = r && new DefiniteUnassignmentCheck().check(wf);
			r = r && new FunctionalCheck().check(wf);
			r = r && new StaticVariableCheck().check(wf);
			r = r && new AmbiguousCoercionCheck().check(wf);
			// Transforms
			if(r) {
				// Only apply if previous stages have all passed.
				new MoveAnalysis().apply(wf);
				new RecursiveTypeAnalysis().apply(wf);
			}

			// ========================================================================
			// Done
			// ========================================================================

			// Flush any changes to disk
			target.flush();

			long endTime = System.currentTimeMillis();
			logger.logTimedMessage("Whiley => Wyil: compiled " + sources.size() + " file(s)", endTime - startTime,
					startMemory - runtime.freeMemory());
			//
			return r;
		} catch(InternalFailure e) {
			SyntacticItem item = e.getElement();
			// FIXME: translate from WyilFile to WhileyFile. This is a temporary hack
			if(e.getEntry().contentType() == WyilFile.ContentType) {
				Decl.Unit unit = item.getAncestor(Decl.Unit.class);
				// Determine which source file this entry is contained in
				Path.Entry sf = getWhileySourceFile(sourceRoot,unit.getName(),sources);
				//
				throw new InternalFailure(e.getMessage(), sf, item, e.getCause());
			} else {
				throw e;
			}
		} catch(SyntaxError e) {
			//
			SyntacticItem item = e.getElement();
			// FIXME: translate from WyilFile to WhileyFile. This is a temporary hack
			if(e.getEntry().contentType() == WyilFile.ContentType) {
				Decl.Unit unit = item.getAncestor(Decl.Unit.class);
				// Determine which source file this entry is contained in
				Path.Entry<WhileyFile> sf = getWhileySourceFile(sourceRoot,unit.getName(),sources);
				//
				throw new SyntaxError(e.getMessage(), sf, item, e.getCause());
			} else {
				throw e;
			}
		}
	}


	public  void verify(Path.Root sourceRoot, Path.Entry<WyilFile> target, List<Path.Entry<WhileyFile>> sources)
			throws IOException {
		Logger logger = project.getLogger();
		// FIXME: this is really a bit of a kludge right now. The basic issue is that,
		// in the near future, the VerificationConditionGenerator will operate directly
		// on the WyilFile rather than creating a WyalFile. Then, the theorem prover can
		// work on the WyilFile directly as well and, hence, this will become more like
		// a compilation stage (as per others above).
		try {
			Runtime runtime = Runtime.getRuntime();
			long startTime = System.currentTimeMillis();
			long startMemory = runtime.freeMemory();
			//
			wytp.types.TypeSystem typeSystem = new wytp.types.TypeSystem(project);
			// FIXME: this unfortunately puts it in the wrong directory.
			Path.Entry<WyalFile> wyalTarget = project.getRoot().get(target.id(),WyalFile.ContentType);
			if (wyalTarget == null) {
				wyalTarget = project.getRoot().create(target.id(), WyalFile.ContentType);
				wyalTarget.write(new WyalFile(wyalTarget));
			}
			WyalFile contents = new VerificationConditionGenerator(new WyalFile(wyalTarget)).translate(target.read());
			new TypeChecker(typeSystem, contents, target).check();
			wyalTarget.write(contents);
			wyalTarget.flush();
			// Now try to verfify it
			AutomatedTheoremProver prover = new AutomatedTheoremProver(typeSystem);
			// FIXME: this is horrendous :(
			prover.check(contents, sourceRoot);

			long endTime = System.currentTimeMillis();
			logger.logTimedMessage("verified code for 1 file(s)", endTime - startTime,
					startMemory - runtime.freeMemory());
		} catch(SyntaxError e) {
			//
			SyntacticItem item = e.getElement();
			String message = e.getMessage();
			if(counterexamples && item instanceof WyalFile.Declaration.Assert) {
				message += " (" + findCounterexamples((WyalFile.Declaration.Assert) item) + ")";
			}
			// FIXME: translate from WyilFile to WhileyFile. This is a temporary hack
			if(item != null && e.getEntry() != null && e.getEntry().contentType() == WyilFile.ContentType) {
				Decl.Unit unit = item.getAncestor(Decl.Unit.class);
				// Determine which source file this entry is contained in
				Path.Entry<WhileyFile> sf = getWhileySourceFile(sourceRoot, unit.getName(), sources);
				//
				throw new SyntaxError(message,sf,item,e.getCause());
			} else {
				throw new SyntaxError(message,e.getEntry(),item,e.getCause());
			}
		}
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

	public String findCounterexamples(WyalFile.Declaration.Assert assertion) {
		// FIXME: it doesn't feel right creating new instances here.
		NameResolver resolver = new WyalFileResolver(project);
		TypeInvariantExtractor extractor = new TypeInvariantExtractor(resolver);
		Interpreter interpreter = new Interpreter(new SmallWorldDomain(resolver), resolver, extractor);
		try {
			Interpreter.Result result = interpreter.evaluate(assertion);
			if (!result.holds()) {
				// FIXME: this is broken
				return result.getEnvironment().toString();
			}
		} catch (Interpreter.UndefinedException e) {
			// do nothing for now
		}
		return "no counterexample";
	}

	private static Path.Entry<WhileyFile> getWhileySourceFile(Path.Root root, Name name,
			List<Path.Entry<WhileyFile>> sources) throws IOException {
		String nameStr = name.toString().replace("::", "/");
		//
		for (Path.Entry<WhileyFile> e : sources) {
			if (root.contains(e) && e.id().toString().endsWith(nameStr)) {
				return e;
			}
		}
		throw new IllegalArgumentException("unknown unit");
	}

	private static void throwSyntaxError(SyntacticItem item) {
		throwSyntaxError(item, new BitSet());
	}

	private static void throwSyntaxError(SyntacticItem item, BitSet visited) {
		int index = item.getIndex();
		if(visited.get(index)) {
			// Indicates we've already traversed this item and, hence, we are in some kind
			// of loop.
			return;
		} else {
			visited.set(index);
			// Recursive children looking for other syntactic markers
			for (int i = 0; i != item.size(); ++i) {
				throwSyntaxError(item.get(i),visited);
			}
			SyntacticItem.Marker marker = item.getParent(SyntacticItem.Marker.class);
			// Check whether this item has a marker associated with it.
			if (marker != null) {
				// At least one marked assocaited with item.
				CompilationUnit cu = (CompilationUnit) item.getHeap();
				throw new SyntaxError(marker.getMessage(),cu.getEntry(),item);
			}
		}
	}
}
