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
import wybs.lang.SourceFile;
import wyfs.lang.Content;
import wyfs.lang.Content.Type;
import wyfs.lang.Path;
import wyfs.lang.Path.ID;

public class WhileyFile extends SourceFile {
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
		public WhileyFile read(ID id, InputStream input) throws IOException {
			return new WhileyFile(id, input.readAllBytes());
		}
	};

	private final Path.Entry<WhileyFile> entry;
	private final Path.ID ID;

	private final List<WhileyFileLexer.Token> tokens;

	public WhileyFile(Path.Entry<WhileyFile> entry) throws IOException {
		super(entry.id(), null);
		this.entry = entry;
		this.tokens = new WhileyFileLexer(entry).scan();
		this.ID = entry.id();
	}

	public WhileyFile(Path.ID ID, byte[] bytes) {
		this(ID,new String(bytes));
	}

	public WhileyFile(Path.ID ID, String content) {
		super(ID, content);
		this.entry = null;
		this.tokens = new WhileyFileLexer(content).scan();
		this.ID = ID;
	}

	@Override
	public ID getID() {
		return ID;
	}

	@Override
	public Type<?> getContentType() {
		return ContentType;
	}
	
	public Path.Entry<WhileyFile> getEntry() {
		return entry;
	}

	public List<WhileyFileLexer.Token> getTokens() {
		return Collections.unmodifiableList(tokens);
	}
}
