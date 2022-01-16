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
package wycc.io;

import java.io.*;

public class BinaryInputStream extends InputStream {
	protected InputStream input;
	protected int value;
	protected int count;

	public BinaryInputStream(InputStream input) {
		this.input = input;
	}

	@Override
	public int read() throws IOException {
		if(count == 0) {
			return input.read();
		} else {
			return read_un(8);
		}
	}

	@Override
	public int read(byte[] bytes) throws IOException {
		for (int i = 0; i != bytes.length; ++i) {
			bytes[i] = (byte) read();
		}
		return bytes.length;
	}

	@Override
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
