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

package wyjc.runtime;

import java.math.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;

public class IO {
	public static Process openReader(ArrayList name) {
		WhileyRecord r = new WhileyRecord();
		try {
			String filename = Util.toString(name);
			FileInputStream fin = new FileInputStream(filename);
			r.put("fileName", name);
			r.put("$fin", fin);
			Process p = new Process(r);
			return p;
		} catch(FileNotFoundException e) {
			r.put("msg", e.getMessage());			
		}
		return null;
	}
	
	public static Process openWriter(ArrayList name) {
		WhileyRecord r = new WhileyRecord();
		try {
			String filename = Util.toString(name);
			FileOutputStream fout = new FileOutputStream(filename);
			r.put("fileName", name);
			r.put("$fout", fout);
			Process p = new Process(r);
			return p;
		} catch(FileNotFoundException e) {
			r.put("msg", e.getMessage());			
		}
		return null;
	}
	
	public static void closeFile(Process p) {
		FileInputStream fin = (FileInputStream) ((HashMap) p.getState())
				.get("$fin");		
		try {
			if(fin != null) {
				fin.close();
			} else {
				FileOutputStream fout = (FileOutputStream) ((HashMap) p.getState())
				.get("$fout");		
				fout.close();
			}
		} catch (IOException ioe) {
			// what to do here??
		}
	}
	
	public static ArrayList readFile(Process p, BigRational max) {		
		FileInputStream fin = (FileInputStream) ((HashMap) p.getState())
				.get("$fin");
		
		ArrayList r = new ArrayList();
		byte[] bytes = new byte[max.intValue()];		
		try {
			int nbytes = fin.read(bytes);
			for(int i=0;i!=nbytes;++i) {				
				r.add(BigRational.valueOf(bytes[i] & 0xFF));
			}
			System.out.println("READ: " + nbytes);
		} catch (IOException ioe) {
			// what to do here??
		}
		
		return r;		
	}
	
	private static final int CHUNK_SIZE = 1024;
	public static ArrayList readFile(Process p) {		
		FileInputStream fin = (FileInputStream) ((HashMap) p.getState())
				.get("$fin");
		
		ArrayList r = new ArrayList();				
		try {
			int nbytes = 0;
			do {
				byte[] bytes = new byte[CHUNK_SIZE];
				nbytes = fin.read(bytes);
				for(int i=0;i!=nbytes;++i) {
					r.add(BigRational.valueOf(bytes[i] & 0xFF));
				}
			} while(nbytes == CHUNK_SIZE);			
		} catch (IOException ioe) {
			// what to do here??
		}
		
		return r;		
	}
	
	public static void writeFile(Process p, List bytes) {		
		FileOutputStream fout = (FileOutputStream) ((HashMap) p.getState())
				.get("$fout");
				
		try {
			byte[] bs = new byte[bytes.size()];
			for(int i=0;i!=bs.length;++i) {
				BigRational r = (BigRational) bytes.get(i); 
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
	
	public static Method functionRef(String clazz, String name) {
		try {
			Class cl = Class.forName(clazz);
			for(Method m : cl.getDeclaredMethods()) {
				if(m.getName().equals(name)) {
					return m;
				}
			}
			throw new RuntimeException("Method Not Found: " + clazz + ":" + name);
		} catch(ClassNotFoundException e) {
			throw new RuntimeException("Class Not Found: " + clazz);
		}
	}
}
