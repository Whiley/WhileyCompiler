// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyil.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import wybs.lang.CompilationUnit;
import wyc.io.WhileyFileLexer;
import wyc.io.WhileyFileParser;
import wyc.lang.WhileyFile;
import wyc.util.AbstractWhileyFile;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyfs.lang.Path.Entry;

public class WyilFile extends AbstractWhileyFile<WyilFile> {

	// =========================================================================
	// Content Type
	// =========================================================================

	public static final Content.Type<WyilFile> ContentType = new Content.Type<WyilFile>() {

		/**
		 * This method simply parses a whiley file into an abstract syntax tree.
		 * It makes little effort to check whether or not the file is
		 * syntactically correct. In particular, it does not determine the
		 * correct type of all declarations, expressions, etc.
		 *
		 * @param file
		 * @return
		 * @throws IOException
		 */
		@Override
		public WyilFile read(Path.Entry<WyilFile> e, InputStream inputstream) throws IOException {
			throw new RuntimeException("IMPLEMENT ME");
		}

		@Override
		public void write(OutputStream output, WyilFile value) {
			// for now
			//throw new UnsupportedOperationException();
		}

		@Override
		public String toString() {
			return "Content-Type: wyil";
		}

		@Override
		public String getSuffix() {
			return "wyil";
		}
	};

	// =========================================================================
	// Constructors
	// =========================================================================

	public WyilFile(Entry<WyilFile> entry) {
		super(entry);
	}

	public WyilFile(Entry<WyilFile> entry, CompilationUnit other) {
		super(entry,other);
	}
}
