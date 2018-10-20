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
package wyc.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import wyc.io.WhileyFileLexer;
import wyfs.lang.Content;
import wyfs.lang.Path;
import wyil.lang.WyilFile;

public class WhileyFile {
	// =========================================================================
	// Source Content Type
	// =========================================================================

	public static final Content.Type<WhileyFile> ContentType = new Content.Type<WhileyFile>() {

		/**
		 * This method simply parses a whiley file into an abstract syntax tree. It
		 * makes little effort to check whether or not the file is syntactically
		 * correct. In particular, it does not determine the correct type of all
		 * declarations, expressions, etc.
		 *
		 * @param file
		 * @return
		 * @throws IOException
		 */
		@Override
		public WhileyFile read(Path.Entry<WhileyFile> e, InputStream inputstream) throws IOException {
			return new WhileyFile(e);
		}

		@Override
		public void write(OutputStream output, WhileyFile value) {
			// for now
			throw new UnsupportedOperationException();
		}

		@Override
		public String toString() {
			return "Content-Type: whiley";
		}

		@Override
		public String getSuffix() {
			return "whiley";
		}
	};

	private final Path.Entry<WhileyFile> entry;

	private final List<WhileyFileLexer.Token> tokens;

	public WhileyFile(Path.Entry<WhileyFile> entry) throws IOException {
		this.entry = entry;
		this.tokens = new WhileyFileLexer(entry).scan();
	}

	public WhileyFile(List<WhileyFileLexer.Token> tokens) {
		this.entry = null;
		this.tokens = tokens;
	}

	public Path.Entry<WhileyFile> getEntry() {
		return entry;
	}

	public List<WhileyFileLexer.Token> getTokens() {
		return Collections.unmodifiableList(tokens);
	}
}
