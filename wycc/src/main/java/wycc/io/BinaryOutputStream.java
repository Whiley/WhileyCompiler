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

public class BinaryOutputStream extends OutputStream {
	protected OutputStream output;
	protected int value;
	protected int count;
	protected int length;

	/**
	 * Write out data in big-endian format.
	 * @param output
	 */
	public BinaryOutputStream(OutputStream output) {
		this.output = output;
	}

	/**
	 * Get number of bytes written.
	 * @return
	 */
	public int length() {
		return length;
	}

	/**
	 * Write an unsigned integer value using 8bits using a big-endian encoding.
	 *
	 * @param w
	 * @throws IOException
	 */
	@Override
	public void write(int i) throws IOException {
		if(count == 0) {
			output.write(i & 0xFF);
			length++;
		} else {
			write_un(i & 0xFF,8);
		}
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		for(byte b : bytes) {
			write(b);
		}
	}

	@Override
	public void write(byte[] bytes, int offset, int length) throws IOException {
		for(;offset < length;++offset) {
			write(bytes[offset]);
		}
	}

	/**
	 * Write an unsigned integer value using 8bits using a big-endian encoding.
	 *
	 * @param w
	 * @throws IOException
	 */
	public void write_u8(int w) throws IOException {
		if(count == 0) {
			output.write(w & 0xFF);
			length++;
		} else {
			write_un(w & 0xFF,8);
		}
	}

	/**
	 * Write an unsigned integer value using 16bits using a big-endian encoding.
	 *
	 * @param w
	 * @throws IOException
	 */
	public void write_u16(int w) throws IOException {
		write_u8((w >> 8) & 0xFF);
		write_u8(w & 0xFF);
	}

	/**
	 * Write an unsigned integer value using 32bits using a big-endian encoding.
	 *
	 * @param w
	 * @throws IOException
	 */
	public void write_u32(int w) throws IOException {
		write_u8((w >> 24) & 0xFF);
		write_u8((w >> 16) & 0xFF);
		write_u8((w >> 8) & 0xFF);
		write_u8(w & 0xFF);
	}

	/**
	 * Write an unsigned integer value using a variable amount of space. The
	 * value is split into 4 bit (big-endian) chunks, where the msb of each
	 * chunk is a flag indicating whether there are more chunks. Therefore,
	 * values between 0 and 7 fit into 4 bits. Similarly, values between 8 and
	 * 63 fit into 8 bits, etc
	 *
	 * @param w
	 *            --- number to convert (which cannot be negative)
	 * @throws IOException
	 */
	public void write_uv(int w) throws IOException {
		if(w < 0) {
			throw new IllegalArgumentException("cannot write negative number in a variable amount of space");
		}
		do {
			int t = w & 7;
			w = w >> 3;
			if(w != 0) {
				write_un(8|t,4);
			} else {
				write_un(t,4);
			}
		} while(w != 0);
	}

	/**
	 * Write an unsigned integer value using n bits using a big-endian encoding.
	 *
	 * @param w
	 * @throws IOException
	 */
	public void write_un(int bits, int n) throws IOException {
		int mask = 1;
		for(int i=0;i<n;++i) {
			boolean bit = (bits & mask) != 0;
			write_bit(bit);
			mask = mask << 1;
		}
	}

	public void write_bit(boolean bit) throws IOException {
		value = value >> 1;
		if(bit) {
			value |= 128;
		}
		count = count + 1;
		if(count == 8) {
			count = 0;
			output.write(value);
			length++;
			value = 0;
		}
	}

	/**
	 * Pad out stream to nearest byte boundary
	 * @throws IOException
	 */
	public void pad_u8() throws IOException {
		if (count > 0) {
			output.write(value >>> (8-count));
			length++;
			value = 0;
			count = 0;
		}
	}

	@Override
	public void close() throws IOException {
		flush();
		output.close();
	}

	@Override
	public void flush() throws IOException {
		if(count != 0) {
			// In this case, we're closing but we have a number of bits left to
			// write. This means we have to pad out the remainder of a byte.
			// Instead of padding with zeros, I pad with ones. The reason for
			// this is that it forces an EOF when reading back in with read_uv().
			value = value >>> (8-count);
			int mask = 0xff & ((~0) << count);
			value = value | mask;
			output.write(value);
			length++;
		}
	}

	public static String bin2str(int v) {
		if(v == 0) {
			return "0";
		}
		int mask = 1 << 31;
		String r = "";
		boolean leading = true;
		for(int i=0;i!=32;++i) {
			if((v&mask) != 0) {
				r = r + "1";
				leading=false;
			} else if(!leading) {
				r = r + "0";
			}
			v = v << 1;
		}
		return r;
	}

	public static void main(String[] argss) {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			BinaryOutputStream binout = new BinaryOutputStream(bout);

			binout.write_uv(11);
			binout.flush();
			binout.close();
			ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
			BinaryInputStream binin = new BinaryInputStream(bin);
			while(true) {
				boolean bit = binin.read_bit();
				System.out.println(bit ? "1" : "0");
			}
			//binin.close();
		} catch(IOException e) {

		}
	}
}
