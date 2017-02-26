// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyil.builders;

import java.io.IOException;
import java.util.*;

import wyal.lang.WyalFile;
import wybs.lang.Build;
import wycc.util.Logger;
import wycc.util.Pair;
import wyfs.lang.Path;
import wyil.lang.*;

/**
 * Responsible for converting a Wyil file into a Wycs file which can then be
 * passed into the Whiley Constraint Solver (Wycs).
 *
 * @author David J. Pearce
 *
 */
public class Wyil2WyalBuilder implements Build.Task {

	/**
	 * The master namespace for identifying all resources available to the
	 * builder. This includes all modules declared in the project being verified
	 * and/or defined in external resources (e.g. jar files).
	 */
	protected final Build.Project project;

	/**
	 * For logging information.
	 */
	protected Logger logger = Logger.NULL;

	public Wyil2WyalBuilder(Build.Project project) {
		this.project = project;
	}

	@Override
	public Build.Project project() {
		return project;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public String id() {
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Set<Path.Entry<?>> build(Collection<Pair<Path.Entry<?>, Path.Root>> delta, Build.Graph graph)
			throws IOException {
		Runtime runtime = Runtime.getRuntime();
		long start = System.currentTimeMillis();
		long memory = runtime.freeMemory();

		VerificationConditionGenerator vcg = new VerificationConditionGenerator(this);

		// ========================================================================
		// Translate files
		// ========================================================================
		HashSet<Path.Entry<?>> generatedFiles = new HashSet<>();
		for (Pair<Path.Entry<?>, Path.Root> p : delta) {
			Path.Entry<WyilFile> source = (Path.Entry<WyilFile>) p.first();
			Path.Root dst = p.second();
			Path.Entry<WyalFile> target = (Path.Entry<WyalFile>) dst.create(source.id(), WyalFile.ContentType);
			graph.registerDerivation(source, target);
			generatedFiles.add(target);
			WyalFile contents = vcg.translate(source.read(), target);
			// Write the file into its destination
			target.write(contents);
			// Then, flush contents to disk in case we generate an assertion
			// error later. In principle, this should be unnecessary when
			// syntax errors are no longer implemented as exceptions.
			target.flush();
		}

		// ========================================================================
		// Done
		// ========================================================================

		long endTime = System.currentTimeMillis();
		logger.logTimedMessage("Wyil => Wyal: compiled " + delta.size() + " file(s)", endTime - start,
				memory - runtime.freeMemory());

		return generatedFiles;
	}
}
