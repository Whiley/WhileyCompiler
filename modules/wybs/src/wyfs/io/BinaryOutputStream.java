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

public class BinaryOutputStream extends OutputStream {
	protected OutputStream output;
	protected int value;
	protected int count;

	/**
	 * Write out data in big-endian format.
	 * @param output
	 */
	public BinaryOutputStream(OutputStream output) {
		this.output = output;
	}

	/**
	 * Write an unsigned integer value using 8bits using a big-endian encoding.
	 *
	 * @param w
	 * @throws IOException
	 */
	public void write(int i) throws IOException {
		if(count == 0) {
			output.write(i & 0xFF);
		} else {
			write_un(i & 0xFF,8);
		}
	}

	public void write(byte[] bytes) throws IOException {
		for(byte b : bytes) {
			write(b);
		}
	}

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
			value = 0;
			count = 0;
		}
	}

	public void close() throws IOException {
		flush();
		output.close();
	}

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

			binout.write_bit(true);
			binout.write_bit(false);
			binout.write_bit(true);
			binout.pad_u8();
			binout.write_bit(true);
			binout.write_bit(false);
			binout.write_bit(true);
			binout.write_bit(true);

			binout.close();
			ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
			BinaryInputStream binin = new BinaryInputStream(bin);

			System.out.println(binin.read_bit());
			System.out.println(binin.read_bit());
			System.out.println(binin.read_bit());

			binin.pad_u8();

			System.out.println(binin.read_bit());
			System.out.println(binin.read_bit());
			System.out.println(binin.read_bit());
			System.out.println(binin.read_bit());
		} catch(IOException e) {

		}
	}
}
