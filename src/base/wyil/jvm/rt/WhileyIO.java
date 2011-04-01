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

package wyil.jvm.rt;

import java.math.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;

public class WhileyIO {
	public static WhileyProcess openReader(ArrayList name) {
		WhileyRecord r = new WhileyRecord();
		try {
			String filename = Util.toString(name);
			FileInputStream fin = new FileInputStream(filename);
			r.put("fileName", name);
			r.put("$fin", fin);
			WhileyProcess p = new WhileyProcess(r);
			return p;
		} catch(FileNotFoundException e) {
			r.put("msg", e.getMessage());			
		}
		return null;
	}
	
	public static void closeFile(WhileyProcess p) {
		FileInputStream fin = (FileInputStream) ((HashMap) p.state())
				.get("$fin");
		try {
			fin.close();
		} catch (IOException ioe) {
			// what to do here??
		}
	}
	
	public static ArrayList readFile(WhileyProcess p, BigRational max) {		
		FileInputStream fin = (FileInputStream) ((HashMap) p.state())
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
	public static ArrayList readFile(WhileyProcess p) {		
		FileInputStream fin = (FileInputStream) ((HashMap) p.state())
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
