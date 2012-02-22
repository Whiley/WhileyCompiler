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

package wyc.util.path;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.*;

import wyil.lang.ModuleID;
import wyil.lang.PkgID;

/**
 * Represents a Jar file on the file system.
 * 
 * @author djp
 *
 */
public final class JarFileRoot implements Path.Root {
	private final JarFile jf;	
	
	public JarFileRoot(String dir) throws IOException {
		this.jf = new JarFile(dir);		
	}
	
	public JarFileRoot(JarFile dir) {
		this.jf = dir;				
	}

	public boolean exists(PkgID pkg) throws IOException {			
		String pkgname = pkg.toString().replace('.', '/');
		return jf.getEntry(pkgname) != null;		
	}
	
	public List<Path.Entry> list(PkgID pkg) throws IOException {		
		String pkgname = pkg.toString().replace('.', '/');
		Enumeration<JarEntry> entries = jf.entries();
		ArrayList<Path.Entry> contents = new ArrayList<Path.Entry>();
		while (entries.hasMoreElements()) {
			JarEntry e = entries.nextElement();
			String filename = e.getName();
			if(filename.endsWith(".class")) {				
				int pos = filename.lastIndexOf('/');
				String tmp = filename.substring(0, pos);
				if (tmp.equals(pkgname)) {
					// strip suffix
					filename = filename.substring(pos + 1,
							filename.length() - 6);
					ModuleID mid = new ModuleID(pkg, filename);
					contents.add(new Entry(mid, jf, e));
				}
			}
		}		
		return contents;
	}
	
	public List<Path.Entry> list() throws IOException {				
		Enumeration<JarEntry> entries = jf.entries();
		ArrayList<Path.Entry> contents = new ArrayList<Path.Entry>();
		while (entries.hasMoreElements()) {
			JarEntry e = entries.nextElement();
			String filename = e.getName();
			if(filename.endsWith(".class")) {				
				int pos = filename.lastIndexOf('/');
				PkgID pkg = PkgID.fromString(filename.substring(0, pos));
				// strip suffix
				filename = filename.substring(pos + 1,
						filename.length() - 6);
				ModuleID mid = new ModuleID(pkg, filename);
				contents.add(new Entry(mid, jf, e));				
			}
		}		
		return contents;
	}
	
	public Entry lookup(ModuleID mid) throws IOException {
		String filename = mid.toString().replace('.', '/') + ".class";
		JarEntry entry = jf.getJarEntry(filename);
		if(entry != null) {
			return new Entry(mid,jf,entry);
		} else {
			return null;
		}
	}
	
	public String toString() {
		return jf.getName();
	}
	
	public static class Entry implements Path.Entry {
		private final ModuleID mid;
		private final JarFile parent;
		private final JarEntry entry;

		public Entry(ModuleID mid, JarFile parent, JarEntry entry) {
			this.mid = mid;
			this.parent = parent;
			this.entry = entry;
		}

		public ModuleID id() {
			return mid;
		}

		public String location() {
			return parent.getName();
		}
		
		public long lastModified() {
			return entry.getTime();
		}
		
		public String suffix() {
			String suffix = "";
			String filename = entry.getName();
			int pos = filename.lastIndexOf('.');
			if (pos > 0) {
				suffix = filename.substring(pos + 1);
			}
			return suffix;
		}
		
		public InputStream contents() throws IOException {
			return parent.getInputStream(entry);
		}

		public void close() throws IOException {

		}

		public void refresh() throws IOException {

		}
	}
}
