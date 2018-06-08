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
package wyll.io;

import java.io.IOException;

import wybs.io.SyntacticHeapReader;
import wybs.lang.SyntacticItem;
import wyfs.lang.Path;
import wyll.core.WyllFile;

/**
 * Read a binary WYIL file from a byte stream and convert into the corresponding
 * WyllFile object.
 *
 * @author David J. Pearce
 *
 */
public final class WyllFileReader extends SyntacticHeapReader {
	private static final char[] magic = { 'W', 'Y', 'L', 'L', 'F', 'I', 'L', 'E' };

	private Path.Entry<WyllFile> entry;

	public WyllFileReader(Path.Entry<WyllFile> entry) throws IOException {
		super(entry.inputStream(), WyllFile.getSchema());
		this.entry = entry;
	}

	@Override
	public WyllFile read() throws IOException {
		SyntacticItem[] items = readItems();
		return new WyllFile(entry,items);
	}

	@Override
	protected void checkHeader() throws IOException {
		// Check magic number
		for (int i = 0; i != 8; ++i) {
			char c = (char) in.read_u8();
			if (magic[i] != c) {
				throw new IllegalArgumentException("invalid magic number");
			}
		}
		// Check version number
		int major = in.read_uv();
		int minor = in.read_uv();
		// Pad to next byte boundary
		in.pad_u8();
	}
}
