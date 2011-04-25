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

package wyjvm.io;

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
			output.write(i);
		} else {
			write_un(i,8);
		}
	}		
	
	public void write(byte[] bytes) throws IOException {
		for(byte b : bytes) {
			write(b);
		}
	}
	
	/**
	 * Write an unsigned integer value using 8bits using a big-endian encoding.
	 * 
	 * @param w
	 * @throws IOException
	 */
	public void write_u1(int w) throws IOException {
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
	public void write_u2(int w) throws IOException {		
		write_u1((w >> 8) & 0xFF);
		write_u1(w & 0xFF);		
	}

	/**
	 * Write an unsigned integer value using 32bits using a big-endian encoding.
	 * 
	 * @param w
	 * @throws IOException
	 */
	public void write_u4(int w) throws IOException {		
		write_u1((w >> 24) & 0xFF);
		write_u1((w >> 16) & 0xFF);
		write_u1((w >> 8) & 0xFF);
		write_u1(w & 0xFF);		
	}

	/**
	 * Write an unsigned integer value using a variable amount of space. The
	 * value is split into 4 bit (big-endian) chunks, where the msb of each
	 * chunk is a flag indicating whether there are more chunks. Therefore,
	 * values between 0 and 7 fit into 4 bits. Similarly, values between 8 and
	 * 63 fit into 8 bits, etc
	 * 
	 * @param w
	 * @throws IOException
	 */
	public void write_uv(int w) throws IOException {
		if(w >= 0 && w <= 7) {
			write_un(w,4);
		} else if(w >= 0 && w <= 63){
			write_un(8|((w>>3)&7),4);
			write_un(w&7,4);
		} else if(w >= 0 && w <= 511){
			write_un(8|((w>>6)&7),4);
			write_un(8|((w>>3)&7),4);
			write_un(w&7,4);
		} else {
			throw new RuntimeException("Need to implement general case for write_uv");
		}
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
			writeBit(bit);
			mask = mask << 1;
		}		
	}	
	
	public void writeBit(boolean bit) throws IOException {
		value = value >> 1;
		if(bit) {
			value |= 128;
		}
		count = count + 1;
		if(count == 8) {
			count = 0;
			System.out.println("WRITING: " + value);
			output.write(value);
			value = 0;
		}
	}
		
	public void close() throws IOException {
		if(count != 0) {					
			value = value >> (8-count);
			System.out.println("WRITING: " + value);
			output.write(value);
		}
		output.close();
	}
	
	public static void main(String[] argss) {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			BinaryOutputStream binout = new BinaryOutputStream(bout);						
			
			binout.write_un(15,4);			
			binout.write_un(13,4);
			binout.write_u1(123);
			
			binout.close();
			ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
			BinaryInputStream binin = new BinaryInputStream(bin);
			System.out.println("GOT: " + binin.read_un(4));
			System.out.println("GOT: " + binin.read_un(4));			
			System.out.println("GOT: " + binin.read_u1());								
		} catch(IOException e) {
			
		}
	}
}
