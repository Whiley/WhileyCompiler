// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyc.builder;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import wybs.lang.Build;
import wycc.util.Pair;
import wyfs.lang.Path;
import wyfs.lang.Path.Entry;
import wyfs.lang.Path.Root;
import wyil.io.WyilFilePrinter;
import wyil.lang.WyilFile;

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
			if (src.contentType() == WyilFile.ContentType) {
				Path.Entry<WyilFile> e = (Path.Entry<WyilFile>) src;
				WyilFile wf = e.read(); // force file to be parsed
				new WyilFilePrinter(System.out).apply(wf);
			}
		}

		return Collections.EMPTY_SET;
	}

	@Override
	public Build.Project project() {
		return project;
	}
}
