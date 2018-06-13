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
import java.util.*;

import wyal.lang.WyalFile;
import wybs.lang.Build;
import wybs.lang.NameResolver;
import wyc.lang.WhileyFile;
import wyc.util.WhileyFileResolver;
import wycc.util.Logger;
import wycc.util.Pair;
import wyfs.lang.Path;
import wyil.stage.VerificationConditionGenerator;

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

		// ========================================================================
		// Translate files
		// ========================================================================
		NameResolver resolver = new WhileyFileResolver(project);
		HashSet<Path.Entry<?>> generatedFiles = new HashSet<>();
		for (Pair<Path.Entry<?>, Path.Root> p : delta) {
			Path.Entry<WhileyFile> source = (Path.Entry<WhileyFile>) p.first();
			Path.Root dst = p.second();
			Path.Entry<WyalFile> target = (Path.Entry<WyalFile>) dst.create(source.id(), WyalFile.ContentType);
			graph.registerDerivation(source, target);
			generatedFiles.add(target);
			WyalFile contents = new VerificationConditionGenerator(new WyalFile(target),
					resolver).translate(source.read());
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
