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
import java.util.Collections;
import java.util.Set;

import wybs.lang.Build;
import wyc.io.WhileyFilePrinter;
import wyc.lang.WhileyFile;
import wycc.util.Pair;
import wyfs.lang.Path;
import wyfs.lang.Path.Entry;
import wyfs.lang.Path.Root;

/**
 * Responsible for decompiling WyIL files into a human readable form.
 *
 *
 * @author David J. Pearce
 *
 */
public final class DecompileTask implements Build.Task {

	/**
	 * The master project for identifying all resources available to the
	 * builder. This includes all modules declared in the project being compiled
	 * and/or defined in external resources (e.g. jar files).
	 */
	private final Build.Project project;

	public DecompileTask(Build.Project project) {
		this.project = project;
	}

	@Override
	public Set<Entry<?>> build(Collection<Pair<Entry<?>, Root>> delta, Build.Graph graph) throws IOException {

		// ========================================================================
		// Parse and print WyIL files
		// ========================================================================

		for (Pair<Path.Entry<?>,Path.Root> p : delta) {
			Path.Entry<?> src = p.first();
			if (src.contentType() == WhileyFile.BinaryContentType) {
				Path.Entry<WhileyFile> e = (Path.Entry<WhileyFile>) src;
				WhileyFile wf = e.read(); // force file to be parsed
				new WhileyFilePrinter(System.out).apply(wf);
			}
		}

		return Collections.EMPTY_SET;
	}

	@Override
	public Build.Project project() {
		return project;
	}
}
