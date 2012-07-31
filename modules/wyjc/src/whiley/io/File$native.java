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

import wyjc.runtime.WyObject;
import wyjc.runtime.WyList;
import wyjc.runtime.WyRecord;

public class File$native {
	public static WyObject Reader(String filename) {
		WyRecord r = new WyRecord();
		try {			
			FileInputStream fin = new FileInputStream(filename);
			r.put("fileName", filename);
			r.put("$fin", fin);
			WyObject p = new WyObject(r);
			p.start();
			return p;
		} catch(FileNotFoundException e) {
			r.put("msg", e.getMessage());			
		}
		return null;
	}
	
	public static WyObject Writer(String filename) {
		WyRecord r = new WyRecord();
		try {			
			FileOutputStream fout = new FileOutputStream(filename);
			r.put("fileName", filename);
			r.put("$fout", fout);			
			WyObject p = new WyObject(r);
			p.start();
			return p;
		} catch(FileNotFoundException e) {
			r.put("msg", e.getMessage());	
		}
		return null;
	}
	
	public static void close(WyObject p) {
		FileInputStream fin = (FileInputStream) ((HashMap) p.state())
				.get("$fin");		
		try {
			if(fin != null) {
				fin.close();
			} else {
				FileOutputStream fout = (FileOutputStream) ((HashMap) p.state())
				.get("$fout");		
				fout.close();
			}
		} catch (IOException ioe) {
			// what to do here??
		}
	}
	
	public static WyList read(WyObject p, BigInteger max) {		
		FileInputStream fin = (FileInputStream) ((HashMap) p.state())
				.get("$fin");
		
		WyList r = new WyList();
		byte[] bytes = new byte[max.intValue()];		
		try {
			int nbytes = fin.read(bytes);
			for(int i=0;i!=nbytes;++i) {				
				r.add(bytes[i]);
			}
		} catch (IOException ioe) {
			// what to do here??
		}
		
		return r;		
	}
	
	private static final int CHUNK_SIZE = 1024;
	public static WyList read(WyObject p) {		
		FileInputStream fin = (FileInputStream) ((HashMap) p.state())
				.get("$fin");
		
		WyList r = new WyList();				
		try {
			int nbytes = 0;
			do {
				byte[] bytes = new byte[CHUNK_SIZE];
				nbytes = fin.read(bytes);
				for(int i=0;i!=nbytes;++i) {
					r.add(bytes[i]);
				}
			} while(nbytes == CHUNK_SIZE);			
		} catch (IOException ioe) {
			// what to do here??
		}
		
		return r;		
	}
	
	public static void write(WyObject p, WyList bytes) {
		FileOutputStream fout = (FileOutputStream) ((HashMap) p.state())
				.get("$fout");
				
		try {			
			byte[] bs = new byte[bytes.size()];
			for(int i=0;i!=bs.length;++i) {
				Byte r = (Byte) bytes.get(i); 
				bs[i] = r.byteValue();
			}			
			fout.write(bs);			
		} catch (IOException ioe) {
			// what to do here??
		}		
	}
	
	public static void flush() {		
		System.out.flush();
	}
}
