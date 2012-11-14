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

package wyjvm.lang;

import java.util.*;
import java.io.*;

import wyone.io.BinaryInputStream;
import wyone.io.BinaryOutputStream;

public interface BytecodeAttribute {
	public String name();
	
	/**
	 * This method requires the attribute to write itself to the binary stream.
	 * 
	 * @param writer
	 * @returns the number of bytes written.
	 * @throws IOException
	 */
	public void write(BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool, ClassLoader loader)
			throws IOException;
	
	/**
	 * When this method is called, the attribute must add all items that it
	 * needs to the constant pool.
	 * 
	 * @param constantPool
	 */
	public void addPoolItems(Set<Constant.Info> constantPool, ClassLoader loader);
	
	/**
	 * This method is used to print the contents of the attribute in
	 * human-readable form, similar to that produced by "javap".
	 * 
	 * @param output
	 * @param constantPool
	 * @throws IOException
	 */
	public void print(PrintWriter output,
			Map<Constant.Info, Integer> constantPool, ClassLoader loader)
			throws IOException;
	
	/**
	 * Class for representing unknown attributes
	 * 
	 * @author David J. Pearce	 
	 */
	public static class Unknown implements BytecodeAttribute {
		private byte[] bytes;
		private String name;
		
		public Unknown(String n, byte[] bs) { 
			bytes = bs;
			name = n;
		}
		
		public String name() {
			return name;
		}
		
		public void addPoolItems(Set<Constant.Info> constantPool, ClassLoader loader) {
			// this seems a little broken, but what can we do?
		}
		
		public void write(BinaryOutputStream writer,
				Map<Constant.Info, Integer> constantPool, ClassLoader loader) throws IOException {
			writer.write(bytes);
		}
		
		public void print(PrintWriter output,
				Map<Constant.Info, Integer> constantPool, ClassLoader loader) throws IOException {
			output.println("  Unknown: " + name);
			output.println("   Size: " + bytes.length);
		}
	}

	public static class Fn {
		/**
		 * Read a list of BytecodeAttributes from an input stream, using the
		 * given constant pool and readers map to decode them.
		 * 
		 * @param count
		 *            --- number of attributes to read
		 * @param input
		 *            --- input stream to read from
		 * @param constantPool
		 *            --- pool for decoding constants
		 * @param readers
		 *            --- list of decoders for reading attributes
		 * @return
		 */
		public static List<BytecodeAttribute> read(int count, BinaryInputStream input,
				Map<Integer, Constant.Info> constantPool,
				Map<String, BytecodeAttribute.Reader> readers)
				throws IOException {
			ArrayList<BytecodeAttribute> attributes = new ArrayList<BytecodeAttribute>();
			for(int i=0;i!=count;++i) {
				attributes.add(read(input,constantPool,readers));
			}
			return attributes;
		}

		/**
		 * Read a BytecodeAttribute from an input stream, using the given
		 * constant pool and readers map to decode them.
		 * 
		 * @param input
		 *            --- input stream to read from
		 * @param constantPool
		 *            --- pool for decoding constants
		 * @param readers
		 *            --- list of decoders for reading attributes
		 * @return
		 */
		public static BytecodeAttribute read(BinaryInputStream input,
				Map<Integer, Constant.Info> constantPool,
				Map<String, BytecodeAttribute.Reader> readers)
				throws IOException {
			
			int index =  input.read_u16();
			int origLen = (int) input.read_u32();
			int len = origLen + 6;
			Constant.Utf8 cu = (Constant.Utf8) constantPool.get(index);											
			byte[] bs = new byte[len];
			for(int i=6;i!=len;++i) {
				bs[i] = (byte) input.read();
			}
			
			// restore the header data we've already read
			bs[0] = (byte) ((index >> 8) & 0xFF);
			bs[1] = (byte) (index & 0xFF);
			bs[2] = (byte) ((origLen >> 24) & 0xFF);
			bs[3] = (byte) ((origLen >> 16) & 0xFF);
			bs[4] = (byte) ((origLen >> 8) & 0xFF);
			bs[5] = (byte) (origLen & 0xFF);
			
			BytecodeAttribute.Reader reader = readers.get(cu.str);

			if(reader != null) {			
				try {
					return reader.read(new BinaryInputStream(
							new ByteArrayInputStream(bs)), constantPool);
				} catch(IOException ioex) {
					throw new RuntimeException(ioex.getMessage(),ioex);
				}
			} else {		
				// unknown attribute		
				return new BytecodeAttribute.Unknown(cu.str,bs);
			}
		}
	}

	/**
	 * A BytecodeAttribute.Reader is an interface used for reading a given kind
	 * of BytecodeAttribute. The name returned by the reader is used to
	 * determine those bytecode attributes it will read (i.e. those with the
	 * same name).
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static interface Reader {
		public String name();
		
		public BytecodeAttribute read(BinaryInputStream input,
				Map<Integer, Constant.Info> constantPool) throws IOException;		
	}
}
