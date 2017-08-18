// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyil.io;

import java.io.IOException;

import wybs.io.SyntacticHeapReader;
import wybs.lang.SyntacticHeap;
import wybs.lang.SyntacticItem;
import wyfs.io.BinaryInputStream;
import wyfs.lang.Path;
import wyil.lang.WyilFile;

/**
 * Read a binary WYIL file from a byte stream and convert into the corresponding
 * WyilFile object.
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
		SyntacticItem[] items = readItems();
		return new WyilFile(entry,items);
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
