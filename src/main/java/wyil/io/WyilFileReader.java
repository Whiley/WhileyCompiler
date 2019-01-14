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
package wyil.io;

import java.io.IOException;

import wybs.io.SyntacticHeapReader;
import wybs.lang.SyntacticHeap;
import wybs.lang.SyntacticItem;
import wycc.util.Pair;
import wyfs.io.BinaryInputStream;
import wyfs.lang.Path;
import wyil.lang.WyilFile;

/**
 * Read a binary WYIL file from a byte stream and convert into the corresponding
 * WhileyFile object.
 *
 * @author David J. Pearce
 *
 */
public final class WyilFileReader extends SyntacticHeapReader {
	private static final char[] magic = { 'W', 'Y', 'I', 'L', 'F', 'I', 'L', 'E' };

	private Path.Entry<WyilFile> entry;

	public WyilFileReader(Path.Entry<WyilFile> entry) throws IOException {
		super(entry.inputStream(), WyilFile.getSchema());
		this.entry = entry;
	}

	@Override
	public WyilFile read() throws IOException {
		Pair<Integer,SyntacticItem[]> p = readItems();
		return new WyilFile(entry,p.first(),p.second());
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
