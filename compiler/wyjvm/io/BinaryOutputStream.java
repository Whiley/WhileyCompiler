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

public class BinaryOutputStream extends OutputStream {
	protected OutputStream output;
	
	public BinaryOutputStream(OutputStream output) {
		this.output = output;
	}
		
	public void write(int i) throws IOException {
		output.write(i);
	}		
	
	public void write(byte[] bytes) throws IOException {
		output.write(bytes);
	}
	
	public void write_u1(int w) throws IOException {
		output.write(w & 0xFF);
	}

	public void write_u2(int w) throws IOException {
		output.write((w >> 8) & 0xFF);
		output.write(w & 0xFF);
	}

	public void write_u4(int w) throws IOException {
		output.write((w >> 24) & 0xFF);
		output.write((w >> 16) & 0xFF);
		output.write((w >> 8) & 0xFF);
		output.write(w & 0xFF);
	}	
}
