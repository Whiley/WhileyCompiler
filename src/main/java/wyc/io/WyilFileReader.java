// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyc.io;

import java.io.IOException;

import wybs.io.SyntacticHeapReader;
import wybs.lang.SyntacticHeap;
import wybs.lang.SyntacticItem;
import wyfs.io.BinaryInputStream;
import wyfs.lang.Path;
import wyc.lang.WhileyFile;

/**
 * Read a binary WYIL file from a byte stream and convert into the corresponding
 * WhileyFile object.
 *
 * @author David J. Pearce
 *
 */
public final class WyilFileReader extends SyntacticHeapReader {
	private static final char[] magic = { 'W', 'Y', 'I', 'L', 'F', 'I', 'L', 'E' };

	private Path.Entry<WhileyFile> entry;

	public WyilFileReader(Path.Entry<WhileyFile> entry) throws IOException {
		super(entry.inputStream(), WhileyFile.getSchema());
		this.entry = entry;
	}

	@Override
	public WhileyFile read() throws IOException {
		SyntacticItem[] items = readItems();
		return new WhileyFile(entry,items);
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
