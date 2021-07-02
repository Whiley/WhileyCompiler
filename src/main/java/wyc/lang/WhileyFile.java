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
import wycc.lang.Content;
import wycc.lang.Path;
import wycc.lang.SourceFile;
import wycc.lang.Content.Type;

public class WhileyFile extends SourceFile {
	// =========================================================================
	// Source Content Type
	// =========================================================================

	public static final Content.Type<WhileyFile> ContentType = new Content.Type<>() {

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
		public WhileyFile read(Path id, InputStream in) throws IOException {
			return new WhileyFile(id,in.readAllBytes());
		}

		@Override
		public void write(OutputStream output, WhileyFile value) throws IOException {

			output.write(value.getBytes());
		}

		@Override
		public String toString() {
			return "Content-Type: whiley";
		}

		@Override
		public String getSuffix() {
			return "whiley";
		}

		@Override
		public boolean includes(Class<?> kind) {
			return kind == WhileyFile.class;
		}
	};

	private final Path path;

	private final List<WhileyFileLexer.Token> tokens;

	public WhileyFile(Path ID, byte[] bytes) {
		this(ID,new String(bytes));
	}

	public WhileyFile(Path ID, String content) {
		super(ID, content);
		this.tokens = new WhileyFileLexer(content).scan();
		this.path = ID;
	}

	@Override
	public Path getPath() {
		return path;
	}

	@Override
	public Content.Type<WhileyFile> getContentType() {
		return WhileyFile.ContentType;
	}

	public List<WhileyFileLexer.Token> getTokens() {
		return Collections.unmodifiableList(tokens);
	}

	@Override
	public String toString() {
		return path + ":whiley";
	}
}
