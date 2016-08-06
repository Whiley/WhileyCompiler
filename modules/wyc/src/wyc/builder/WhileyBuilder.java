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

package wyc.builder;

import java.io.*;
import java.util.*;

import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.util.Trie;
import wyil.checks.CoercionCheck;
import wyil.checks.ModuleCheck;
import wyil.lang.*;
import wybs.lang.*;
import wybs.util.*;
import wyc.lang.*;
import wycommon.util.Logger;
import wycommon.util.Pair;

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
public final class WhileyBuilder implements Build.Task {

	/**
	 * The master project for identifying all resources available to the
	 * builder. This includes all modules declared in the project being compiled
	 * and/or defined in external resources (e.g. jar files).
	 */
	private final Build.Project project;

	/**
	 * The logger used for logging system events
	 */
	private Logger logger;

	/**
	 * A map of the source files currently being compiled.
	 */
	private final HashMap<Path.ID, Path.Entry<WhileyFile>> srcFiles = new HashMap<Path.ID, Path.Entry<WhileyFile>>();

	/**
	 * The import cache caches specific import queries to their result sets.
	 * This is extremely important to avoid recomputing these result sets every
	 * time. For example, the statement <code>import whiley.lang.*</code>
	 * corresponds to the triple <code>("whiley.lang",*,null)</code>.
	 */
	private final HashMap<Trie,ArrayList<Path.ID>> importCache = new HashMap<>();

	public WhileyBuilder(Build.Project namespace) {
		this.logger = Logger.NULL;
		this.project = namespace;
	}
	
	public String id() {
		return "wyc.builder";
	}

	public Build.Project project() {
		return project;
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
		int count=0;
		for (Pair<Path.Entry<?>,Path.Root> p : delta) {
			Path.Entry<?> src = p.first();
			if (src.contentType() == WhileyFile.ContentType) {
				Path.Entry<WhileyFile> sf = (Path.Entry<WhileyFile>) src;
				sf.read(); // force file to be parsed
				count++;
				srcFiles.put(sf.id(), sf);
			}
		}

		logger.logTimedMessage("Parsed " + count + " source file(s).",
				System.currentTimeMillis() - tmpTime, tmpMemory - runtime.freeMemory());

		// ========================================================================
		// Flow Type source files
		// ========================================================================

		runtime = Runtime.getRuntime();
		tmpTime = System.currentTimeMillis();
		tmpMemory = runtime.freeMemory();

		ArrayList<WhileyFile> files = new ArrayList<WhileyFile>();
		for(Pair<Path.Entry<?>,Path.Root> p : delta) {
			Path.Entry<?> f = p.first();
			if (f.contentType() == WhileyFile.ContentType) {
				Path.Entry<WhileyFile> sf = (Path.Entry<WhileyFile>) f;
				WhileyFile wf = sf.read();
				files.add(wf);
			}
		}

		FlowTypeChecker flowChecker = new FlowTypeChecker(this);
		flowChecker.propagate(files);

		logger.logTimedMessage("Typed " + count + " source file(s).",
				System.currentTimeMillis() - tmpTime, tmpMemory - runtime.freeMemory());

		// ========================================================================
		// Code Generation
		// ========================================================================

		runtime = Runtime.getRuntime();
		tmpTime = System.currentTimeMillis();
		tmpMemory = runtime.freeMemory();

		CodeGenerator generator = new CodeGenerator(this,flowChecker);
		HashSet<Path.Entry<?>> generatedFiles = new HashSet<Path.Entry<?>>();
		for (Pair<Path.Entry<?>, Path.Root> p : delta) {
			Path.Entry<?> src = p.first();
			Path.Root dst = p.second();
			if (src.contentType() == WhileyFile.ContentType) {
				Path.Entry<WhileyFile> source = (Path.Entry<WhileyFile>) src;
				Path.Entry<WyilFile> target = dst.create(src.id(), WyilFile.ContentType);
				graph.registerDerivation(source, target);
				generatedFiles.add(target);
				WhileyFile wf = source.read();
				new DefiniteAssignmentAnalysis(wf).check();
				WyilFile wyil = generator.generate(wf, target);
				target.write(wyil);
			}
		}

		logger.logTimedMessage("Generated code for " + count + " source file(s).",
					System.currentTimeMillis() - tmpTime, tmpMemory - runtime.freeMemory());

		// ========================================================================
		// Pipeline Stages
		// ========================================================================

		for (Pair<Path.Entry<?>, Path.Root> p : delta) {
			Path.Entry<?> src = p.first();
			Path.Root dst = p.second();
			Path.Entry<WyilFile> wf = dst.get(src.id(), WyilFile.ContentType);
			new ModuleCheck(this).apply(wf.read());
			new CoercionCheck(this).apply(wf.read());
		}

		// ========================================================================
		// Done
		// ========================================================================

		long endTime = System.currentTimeMillis();
		logger.logTimedMessage("Whiley => Wyil: compiled " + delta.size() + " file(s)",
				endTime - startTime, startMemory - runtime.freeMemory());

		return generatedFiles;
	}

	// ======================================================================
	// Public Accessors
	// ======================================================================

	public boolean exists(Path.ID id) {
		try {
			return project.exists(id, WhileyFile.ContentType)
					|| project.exists(id, WyilFile.ContentType);
		} catch(Exception e) {
			return false;
		}
	}

	/**
	 * Determine whether a given name exists or not.
	 *
	 * @param nid --- Name ID to check
	 * @return
	 */
	public boolean isName(NameID nid) throws IOException {
		Path.ID mid = nid.module();
		Path.Entry<WhileyFile> wf = srcFiles.get(mid);
		if(wf != null) {
			// FIXME: check for the right kind of name
			return wf.read().hasName(nid.name());
		} else {
			Path.Entry<WyilFile> m = project.get(mid,WyilFile.ContentType);
			if(m != null) {
				return m.read().hasName(nid.name());
			} else {
				return false;
			}
		}
	}

	/**
	 * This method takes a given import declaration, and expands it to find all
	 * matching modules.
	 *
	 * @param imp
	 * @return
	 */
	public List<Path.ID> imports(Trie key) throws ResolveError {
		try {
			ArrayList<Path.ID> matches = importCache.get(key);
			if (matches != null) {
				// cache hit
				return matches;
			} else {
				// cache miss
				matches = new ArrayList<Path.ID>();

				for(Path.Entry<WhileyFile> sf : srcFiles.values()) {
					if(key.matches(sf.id())) {
						matches.add(sf.id());
					}
				}
				if(key.isConcrete()) {
					// A concrete key is one which does not contain a wildcard.
					// Therefore, it corresponds to exactly one possible item.
					// It is helpful, from a performance perspective, to use
					// NameSpace.exists() in such case, as this conveys the fact
					// that we're only interested in a single item.
					if(project.exists(key,WyilFile.ContentType)) {
						matches.add(key);
					}
				} else {
					Content.Filter<?> binFilter = Content.filter(key,
							WyilFile.ContentType);
					for (Path.ID mid : project.match(binFilter)) {
						matches.add(mid);
					}
				}
				importCache.put(key, matches);
			}
			return matches;
		} catch(Exception e) {
			throw new ResolveError(e.getMessage(),e);
		}
	}

	/**
	 * Get the source file associated with a given module identifier. If the
	 * source file does not exist, null is returned.
	 *
	 * @param mid
	 * @return
	 * @throws IOException
	 */
	public WhileyFile getSourceFile(Path.ID mid) throws IOException {
		Path.Entry<WhileyFile> e = srcFiles.get(mid);
		if(e != null) {
			return e.read();
		} else {
			return null;
		}
	}

	/**
	 * Get the (compiled) module associated with a given module identifier. If
	 * the module does not exist, a resolve error is thrown.
	 *
	 * @param mid
	 * @return
	 * @throws IOException
	 */
	public WyilFile getModule(Path.ID mid) throws IOException {
		return project.get(mid, WyilFile.ContentType).read();
	}

	// ======================================================================
	// Private Implementation
	// ======================================================================

//	private void process(WyilFile module) throws IOException {
//		Runtime runtime = Runtime.getRuntime();
//		long start = System.currentTimeMillis();
//		long memory = runtime.freeMemory();
//		String name = name(stage.getClass().getSimpleName());
//
//		try {
//			stage.apply(module);
//			logger.logTimedMessage("[" + module.getEntry().location() + "] applied " + name,
//					System.currentTimeMillis() - start, memory - runtime.freeMemory());
//			System.gc();
//		} catch (RuntimeException ex) {
//			logger.logTimedMessage("[" + module.getEntry().location() + "] failed on " + name + " (" + ex.getMessage() + ")",
//					System.currentTimeMillis() - start, memory - runtime.freeMemory());
//			throw ex;
//		} catch (IOException ex) {
//			logger.logTimedMessage("[" + module.getEntry().location() + "] failed on " + name + " (" + ex.getMessage() + ")",
//					System.currentTimeMillis() - start, memory - runtime.freeMemory());
//			throw ex;
//		}
//	}
//
//	private static String name(String camelCase) {
//		boolean firstTime = true;
//		String r = "";
//		for(int i=0;i!=camelCase.length();++i) {
//			char c = camelCase.charAt(i);
//			if(!firstTime && Character.isUpperCase(c)) {
//				r += " ";
//			}
//			firstTime=false;
//			r += Character.toLowerCase(c);;
//		}
//		return r;
//	}
}
