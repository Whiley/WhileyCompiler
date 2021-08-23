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
import java.io.InputStream;

import wycc.io.SyntacticHeapReader;
import wycc.lang.*;
import wycc.lang.SyntacticHeap.Schema;
import wycc.io.BinaryInputStream;
import wycc.util.Pair;
import wycc.util.Trie;
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
	private int minorVersion;
	private int majorVersion;

	public WyilFileReader(InputStream input) throws IOException {
		super(input);
	}

	@Override
	public WyilFile read() throws IOException {
//		Pair<Integer,SyntacticItem[]> p = readItems();
//		return new WyilFile(p.first(), p.second(), majorVersion, minorVersion);
		throw new IllegalArgumentException("deadcode reached");
	}

	public WyilFile read(Trie ID) throws IOException {
		Pair<Integer, SyntacticItem[]> p = readItems();
		return new WyilFile(ID, p.first(), p.second(), majorVersion, minorVersion);
	}

	@Override
	protected Schema checkHeader() throws IOException {
		// Extract current schema
		Schema schema = WyilFile.getSchema();
		// Check magic number
		for (int i = 0; i != 8; ++i) {
			char c = (char) in.read_u8();
			if (magic[i] != c) {
				throw new IllegalArgumentException("invalid magic number");
			}
		}
		// Check version number
		majorVersion = in.read_uv();
		minorVersion = in.read_uv();
		//
		if (majorVersion > schema.getMajorVersion()
				|| (majorVersion == schema.getMajorVersion() && minorVersion > schema.getMinorVersion())) {
			String msg = "WyilFile compiled with newer version of WyC [" + majorVersion + "." + minorVersion
					+ " > " + schema.getMajorVersion() + "." + schema.getMinorVersion() + "]";
			throw new SyntacticException(msg, null, null);
		} else {
			schema = selectSchema(majorVersion,minorVersion,schema);
		}
		// Pad to next byte boundary
		in.pad_u8();
		//
		return schema;
	}

	/**
	 * Select the most appropriate schema for decoding this file based on its
	 * embedded version information.
	 *
	 * @param major
	 * @param minor
	 * @param current
	 * @return
	 */
	private static SyntacticHeap.Schema selectSchema(int major, int minor, SyntacticHeap.Schema current) {
		if (current.getMajorVersion() == major && current.getMinorVersion() == minor) {
			return current;
		} else {
			// FIXME: need to check for incompatible parents.
			return selectSchema(major, minor, current.getParent());
		}
	}
}
