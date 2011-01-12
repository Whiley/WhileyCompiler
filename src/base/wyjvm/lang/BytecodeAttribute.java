// This file is part of the Wyjvm bytecode manipulation library.
//
// Wyjvm is free software; you can redistribute it and/or modify 
// it under the terms of the GNU General Public License as published 
// by the Free Software Foundation; either version 3 of the License, 
// or (at your option) any later version.
//
// Wyjvm is distributed in the hope that it will be useful, but 
// WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See 
// the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with Wyjvm. If not, see <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyjvm.lang;

import java.util.*;
import java.io.*;

import wyjvm.io.BinaryInputStream;
import wyjvm.io.BinaryOutputStream;

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
	 * @author djp	 
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
			
			int index =  input.read_u2();
			int origLen = (int) input.read_u4();
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
	 * @author djp
	 * 
	 */
	public static interface Reader {
		public String name();
		
		public BytecodeAttribute read(BinaryInputStream input,
				Map<Integer, Constant.Info> constantPool) throws IOException;		
	}
}
