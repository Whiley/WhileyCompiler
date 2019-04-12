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
import wyc.lang.WhileyFile;
import wycc.util.Logger;
import wycc.util.Pair;
import wyfs.lang.Path;
import wyfs.lang.Path.Entry;
import wyil.lang.WyilFile;
import wyil.transform.VerificationConditionGenerator;

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

	/**
	 * Target file being generated
	 */
	private final Path.Entry<WyalFile> target;

	/**
	 * Source file being compiled
	 */
	private final Path.Entry<WyilFile> source;

	public Wyil2WyalBuilder(Build.Project project, Path.Entry<WyalFile> target, Path.Entry<WyilFile> source) {
		this.project = project;
		this.target = target;
		this.source = source;
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
	public List<Entry<?>> getSources() {
		return Arrays.asList(source);
	}

	@Override
	public Entry<WyalFile> getTarget() {
		return target;
	}

	@Override
	public boolean isReady() {
		// FIXME: this is broken
		return true;
	}


	@Override
	@SuppressWarnings("unchecked")
	public boolean apply() throws IOException {

		Runtime runtime = Runtime.getRuntime();
		long start = System.currentTimeMillis();
		long memory = runtime.freeMemory();

		WyalFile contents = new VerificationConditionGenerator(new WyalFile(target)).translate(source.read());
		// Write the file into its destination
		target.write(contents);

		long endTime = System.currentTimeMillis();
		logger.logTimedMessage("Wyil => Wyal: compiled 1 file(s)", endTime - start, memory - runtime.freeMemory());
		//
		return true;
	}
}
