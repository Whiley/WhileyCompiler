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

package wybs.lang;

import java.io.IOException;
import java.util.*;

import wycc.util.Pair;
import wyfs.lang.Path;

/**
 * <p>
 * Responsible for transforming files from one content type to another.
 * Typically this revolves around compiling a source file into one or more
 * binary files, although other kinds of transformations are possible (e.g.
 * source-to-source translations, etc).
 * </p>
 *
 * @author David J. Pearce
 *
 */
public interface Builder {

	/**
	 * Get the project this builder is operating on.
	 *
	 * @return
	 */
	public Build.Project project();

	/**
	 * Build a given set of source files to produce target files in specified
	 * locations. A delta represents a list of pairs (s,t), where s is a source
	 * file and t is the destination root for all generated files. Each file may
	 * be associated with a different destination directory, in order to support
	 * e.g. multiple output directories.
	 *
	 * @param delta
	 *            --- the set of files to be built.
	 * @param graph
	 *            --- The build graph being constructed
	 * @return --- the set of files generated or modified.
	 */
	public Set<Path.Entry<?>> build(
			Collection<Pair<Path.Entry<?>, Path.Root>> delta, Build.Graph graph) throws IOException;
}
