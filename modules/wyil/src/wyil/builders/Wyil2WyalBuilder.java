// Copyright (c) 2012, David J. Pearce (djp@ecs.vuw.ac.nz)
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

package wyil.builders;

import java.io.IOException;
import java.util.*;

import wybs.lang.Build;
import wybs.lang.Builder;
import wyfs.lang.Path;
import wyil.attributes.SourceLocationMap;
import wyil.lang.*;
import wyil.util.AttributedCodeBlock;
import wycc.util.Logger;
import wycc.util.Pair;
import wycs.syntax.Expr;
import wycs.syntax.WyalFile;

/**
 * Responsible for converting a Wyil file into a Wycs file which can then be
 * passed into the Whiley Constraint Solver (Wycs).
 *
 * @author David J. Pearce
 *
 */
public class Wyil2WyalBuilder implements Builder {

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

	private String filename;

	public Wyil2WyalBuilder(Build.Project project) {
		this.project = project;
	}

	public Build.Project project() {
		return project;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public Set<Path.Entry<?>> build(
			Collection<Pair<Path.Entry<?>, Path.Root>> delta)
			throws IOException {
		Runtime runtime = Runtime.getRuntime();
		long start = System.currentTimeMillis();
		long memory = runtime.freeMemory();

		VcGenerator vcg = new VcGenerator(this);
		
		// ========================================================================
		// Translate files
		// ========================================================================
		HashSet<Path.Entry<?>> generatedFiles = new HashSet<Path.Entry<?>>();
		for(Pair<Path.Entry<?>,Path.Root> p : delta) {
			Path.Entry<WyilFile> sf = (Path.Entry<WyilFile>) p.first();
			Path.Root dst = p.second();
			Path.Entry<WyalFile> df = (Path.Entry<WyalFile>) dst.create(sf.id(), WyalFile.ContentType);
			generatedFiles.add(df);
			WyalFile contents = vcg.transform(sf.read());
			// Write the file into its destination
			df.write(contents);
			// Then, flush contents to disk in case we generate an assertion
			// error later. In principle, this should be unnecessary when
			// syntax errors are no longer implemented as exceptions.
			df.flush();
		}

		// ========================================================================
		// Done
		// ========================================================================

		long endTime = System.currentTimeMillis();
		logger.logTimedMessage("Wyil => Wyal: compiled " + delta.size()
				+ " file(s)", endTime - start, memory - runtime.freeMemory());

		return generatedFiles;
	}

	
}
