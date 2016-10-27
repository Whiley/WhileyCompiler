// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyil.io;

import java.io.*;

import wyfs.lang.Path;
import wyil.lang.*;

/**
 * <p>
 * A module reader is responsible for physically reading information about a
 * given module. Distinct module readers will be needed for different underlying
 * implementations of Whiley. For example, on the JVM, we might want an instance
 * which reads module information directly from a JVM classfile.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public interface ModuleReader {

	/**
	 * Read a given given module from an input stream.
	 *
	 * @param module
	 *            --- the identifier of the module being read.
	 * @param input
	 *            --- an input stream containing the contents of the module to
	 *            be read.
	 * @return
	 * @throws IOException
	 */
	public WyilFile read(Path.ID module, InputStream input) throws IOException;
}
