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

package wyil.io;

import java.io.*;

/**
 * <p>
 * A JavaIdentifierOutputStream provides a mechanism for encoding 8-bit data
 * into a valid Java identifier. According to JLS3.8 a Java Identifier is a
 * string matching <code>[A-Za-z_][A-Za-z0-9$_]*</code>
 * </p>
 * <p>
 * Since there are 64 possibilities for each character in a Java identifier,
 * we're basically encoding 8-bit data into a 6-bit stream (with encoding).
 * </p>
 * 
 * @author djp
 * 
 */
public class IdentifierOutputStream extends OutputStream {	
	private int value;
	private int count;

	private StringBuilder builder;

	public IdentifierOutputStream() {
		builder = new StringBuilder();
	}

	public void write(int b) throws IOException {
		for(int i=0;i!=8;++i) {
			boolean bit = (b & 1) == 1;
			writeBit(bit);
			b = b >> 1;
		}
	}

	public void close() {
		if(count != 0) {
			builder.append(encode(value));
		}			
	}

	private void writeBit(boolean bit) throws IOException {
		value = value << 1;
		if(bit) {
			value |= 1;
		}
		count = count + 1;
		if(count == 6) {
			count = 0;
			builder.append(encode(value));
			value = 0;
		}
	}

	public static char encode(int b) {
		if(b == 0) {
			return '$';
		} else if(b <= 10) {
			b = b - 1;
			b = b + '0';
			return (char) b;
		} else if(b <= 36) {
			b = b - 10;
			b = b + 'A';
			return (char) b;
		} else if(b == 37) {
			return '_';
		} else if(b < 64) {
			b = b - 38;
			b = b + 'a';
			return (char) b;
		} else {
			throw new IllegalArgumentException("Invalid byte to encode: " + b);
		}
	}
	
	public String toString() {
		return builder.toString();
	}
}

