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
