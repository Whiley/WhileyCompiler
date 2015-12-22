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

package whiley.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;

import wyjc.runtime.Util;
import wyjc.runtime.WyByte;
import wyjc.runtime.WyObject;
import wyjc.runtime.WyArray;
import wyjc.runtime.WyRecord;

public class File$native {
	public static WyObject NativeFileReader(WyArray _filename) {
		try {
			String filename = Util.il2str(_filename);
			return new WyObject(new FileInputStream(filename));
		} catch(FileNotFoundException e) {
			// ARGH
		}
		return null;
	}

	public static WyObject NativeFileWriter(WyArray _filename) {
		try {
			String filename = Util.il2str(_filename);
			return new WyObject(new FileOutputStream(filename));
		} catch(FileNotFoundException e) {
			// ARGH
		}
		return null;
	}

	public static void close(WyObject p) {
		Object o = p.state();
		try {
			if(o instanceof FileInputStream) {
				((FileInputStream)o).close();
			} else {
				((FileOutputStream)o).close();
			}
		} catch (IOException ioe) {
			// what to do here??
		}
	}


	public static void flush(WyObject p) {
		Object o = p.state();
		try {
			if(o instanceof FileOutputStream) {
				((FileOutputStream)o).flush();
			}
		} catch (IOException ioe) {
			// what to do here??
		}
	}

	public static boolean hasMore(WyObject p) {
		FileInputStream fin = (FileInputStream) p.state();
		return false; // BROKEN
	}

	public static BigInteger available(WyObject p) {
		FileInputStream fin = (FileInputStream) p.state();
		try {
			return BigInteger.valueOf(fin.available());
		} catch (IOException ioe) {
			// what to do here??
		}
		return BigInteger.ZERO;
	}

	public static WyArray read(WyObject p, BigInteger max) {
		FileInputStream fin = (FileInputStream) p.state();

		WyArray r = new WyArray();
		byte[] bytes = new byte[max.intValue()];
		try {
			int nbytes = fin.read(bytes);
			for(int i=0;i!=nbytes;++i) {
				r.add(WyByte.valueOf(bytes[i]));
			}
		} catch (IOException ioe) {
			// what to do here??
		}

		return r;
	}

	private static final int CHUNK_SIZE = 1024;
	public static WyArray read(WyObject p) {
		FileInputStream fin = (FileInputStream) p.state();

		WyArray r = new WyArray();
		try {
			int nbytes = 0;
			do {
				byte[] bytes = new byte[CHUNK_SIZE];
				nbytes = fin.read(bytes);
				for(int i=0;i!=nbytes;++i) {
					r.add(WyByte.valueOf(bytes[i]));
				}
			} while(nbytes == CHUNK_SIZE);
		} catch (IOException ioe) {
			// what to do here??
		}

		return r;
	}

	public static void write(WyObject p, WyArray bytes) {
		FileOutputStream fout = (FileOutputStream) p.state();

		try {
			byte[] bs = new byte[bytes.size()];
			for(int i=0;i!=bs.length;++i) {
				WyByte r = (WyByte) bytes.get(i);
				bs[i] = r.value();
			}
			fout.write(bs);
		} catch (IOException ioe) {
			// what to do here??
		}
	}
}
