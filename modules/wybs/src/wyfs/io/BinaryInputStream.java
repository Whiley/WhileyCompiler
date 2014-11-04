// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyfs.io;

import java.io.*;

public class BinaryInputStream extends InputStream {
	protected InputStream input;
	protected int value;
	protected int count;

	public BinaryInputStream(InputStream input) {
		this.input = input;
	}

	public int read() throws IOException {
		if(count == 0) {
			return input.read();
		} else {
			return read_un(8);
		}
	}

	public int read(byte[] bytes) throws IOException {
		for (int i = 0; i != bytes.length; ++i) {
			bytes[i] = (byte) read();
		}
		return bytes.length;
	}

	public int read(byte[] bytes, int offset, int length) throws IOException {
		for(;offset < length;++offset) {
			bytes[offset] = (byte) read();
		}
		return length;
	}

	public int read_u8() throws IOException {
		if(count == 0) {
			return input.read() & 0xFF;
		} else {
			return read_un(8);
		}
	}

	public int read_u16() throws IOException {
		return (read_u8() << 8) | read_u8();
	}

	public long read_u32() throws IOException {
		// FIXME: this is most definitely broken
		return (read_u8() << 24) | (read_u8() << 16) | (read_u8() << 8)
				| read_u8();
	}

	public int read_un(int n) throws IOException {
		int value = 0;
		int mask = 1;
		for(int i=0;i!=n;++i) {
			if(read_bit()) {
				value |= mask;
			}
			mask = mask << 1;
		}
		return value;
	}

	public int read_uv() throws IOException {
		int value = 0;
		boolean flag = true;
		int shift = 0;
		while(flag) {
			int w = read_un(4);
			flag = (w&8) != 0;
			value = ((w&7)<<shift) | value;
			shift = shift + 3;
		}
		return value;
	}

	public boolean read_bit() throws IOException {
		if(count == 0) {
			value = input.read();
			if(value < 0) { throw new EOFException(); }
			count = 8;
		}
		boolean r = (value&1) != 0;
		value = value >> 1;
		count = count - 1;
		return r;
	}

	public void pad_u8() throws IOException {
		count = 0; // easy!!
	}
}
