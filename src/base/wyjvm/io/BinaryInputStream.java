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

package wyjvm.io;

import java.io.*;
import java.io.OutputStream;

public class BinaryInputStream extends InputStream {
	protected InputStream input;

	public BinaryInputStream(InputStream input) {
		this.input = input;
	}

	public int read() throws IOException {
		return input.read();		
	}

	public int read_i1() throws IOException {
		return read();
	}
	
	public int read_i4() throws IOException {
		return ((read() & 0xFF) << 24) | ((read() & 0xFF) << 16)
				| ((read() & 0xFF) << 8) | (read() & 0xFF);
	}
	
	public int read_u1() throws IOException {
		return read() & 0xFF;
	}

	public int read_u2() throws IOException {
		return (read_u1() << 8) | read_u1();
	}
		
	public long read_u4() throws IOException {
		// FIXME: this is most definitely broken
		return (read_u1() << 24) | (read_u1() << 16) | (read_u1() << 8)
				| read_u1();
	}
}
