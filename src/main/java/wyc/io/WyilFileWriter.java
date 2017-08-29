// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// This software may be modified and distributed under the terms
// of the BSD license.  See the LICENSE file for details.

package wyc.io;

import java.io.*;

import wybs.io.SyntacticHeapWriter;
import wyc.lang.WhileyFile;


/**
 * <p>
 * Responsible for writing a WyilFile to an output stream in binary form. The
 * binary format is structured to given maximum flexibility and to avoid
 * built-in limitations in terms of e.g. maximum sizes, etc.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public final class WyilFileWriter extends SyntacticHeapWriter {
	private static final int MAJOR_VERSION = 0;
	private static final int MINOR_VERSION = 1;

	public WyilFileWriter(OutputStream output) {
		super(output, WhileyFile.getSchema());
	}

	@Override
	public void writeHeader() throws IOException {
		writeMagicNumber();
		writeVersionNumber();
		// Pad to next byte boundary
		out.pad_u8();
	}

	public void writeMagicNumber() throws IOException {
		out.write_u8(0x57); // W
		out.write_u8(0x59); // Y
		out.write_u8(0x49); // I
		out.write_u8(0x4C); // L
		out.write_u8(0x46); // F
		out.write_u8(0x49); // I
		out.write_u8(0x4C); // L
		out.write_u8(0x45); // E
	}

	public void writeVersionNumber() throws IOException {
		out.write_uv(MAJOR_VERSION);
		out.write_uv(MINOR_VERSION);
	}
}
