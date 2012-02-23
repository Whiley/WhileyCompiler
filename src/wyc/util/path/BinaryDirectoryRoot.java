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
import java.util.*;

import wyil.lang.Module;
import wyil.lang.ModuleID;
import wyil.lang.PkgID;

/**
 * A Directory represents a directory on the file system. Using this, we can
 * list items on the path and see what is there.
 * 
 * @author djp
 * 
 */
public class BinaryDirectoryRoot implements Path.Root {
	private static final FileFilter filter = new FileFilter() {
		public boolean accept(File file) {
			return file.getName().endsWith(".class");
		}
	};
	
	private final java.io.File dir;	
	
	/**
	 * Construct a directory root from a filesystem path expressed as a string,
	 * and an appropriate file filter. In converting the path to a File object,
	 * an IOException may arise if it is an invalid path.
	 * 
	 * @param path
	 *            --- location of directory on filesystem, expressed as a native
	 *            path (i.e. separated using File.separatorChar, etc)
	 * @param filter
	 *            --- filter which determines what constitutes a valid entry for
	 *            this directory.
	 * @throws IOException
	 */
	public BinaryDirectoryRoot(String path) throws IOException {
		this.dir = new File(path);				
	}
	
	/**
	 * Construct a directory root from a given directory and file filter.
	 * 
	 * @param file
	 *            --- location of directory on filesystem.
	 * @param filter
	 *            --- filter which determines what constitutes a valid entry for
	 *            this directory.
	 */
	public BinaryDirectoryRoot(java.io.File dir) {
		this.dir = dir;
	}
	
	public boolean exists(PkgID pkg) throws IOException {
		File location = new File(dir + File.separator + pkg.fileName());
		return location.exists() && location.isDirectory();
	}
	
	public List<Path.Entry> list() throws IOException { 
		File root = new File(dir + File.separator);
		ArrayList<Path.Entry> entries = new ArrayList<Path.Entry>();
		traverse(root,PkgID.ROOT,entries);
		return entries;
	}
	
	public List<Path.Entry> list(PkgID pkg) throws IOException {
		File location = new File(dir + File.separator + pkg.fileName());

		if (location.exists() && location.isDirectory()) {
			ArrayList<Path.Entry> entries = new ArrayList<Path.Entry>();

			for (File file : location.listFiles(filter)) {
				String filename = file.getName();
				String name = filename.substring(0, filename.lastIndexOf('.'));
				ModuleID mid = new ModuleID(pkg, name);
				entries.add(new Entry(mid, file));				
			}
			return entries;
		} else {									
			return Collections.EMPTY_LIST;
		}
	}
	
	public <T extends Path.Entry> T get(ModuleID mid, ContentType<T> ct) throws IOException {
		File location = new File(dir + File.separator + mid.fileName()
				+ ".class");
		if (location.exists()) {
			Entry e = new Entry(mid, location); 		
			T t = ct.accept(e);
			if(t != null && e.id().equals(mid)) {
				return t;
			}
		}
		
		return null; // not found		
	}

	public String toString() {
		return dir.getPath();
	}
	
	/**
	 * A WFile is a file on the file system which represents a Whiley module. The
	 * file may be encoded in a range of different formats. For example, it may be a
	 * source file and/or a binary wyil file.
	 * 
	 * @author djp
	 * 
	 */
	public static class Entry implements Path.Entry {
		private final ModuleID mid;
		private final java.io.File file;		
		private boolean modified;
		
		public Entry(ModuleID mid, java.io.File file) {
			this.mid = mid;
			this.file = file;
			this.modified = false;
		}
		
		public ModuleID id() {
			return mid;
		}
		
		public String location() {
			return file.getPath();
		}
		
		public void touch() {
			this.modified = true;
		}
		
		public boolean isModified() {
			return modified;
		}
		
		public long lastModified() {
			return file.lastModified();
		}
		
		public String suffix() {
			String filename = file.getName();
			String suffix = "";
			int pos = filename.lastIndexOf('.');
			if (pos > 0) {
				suffix = filename.substring(pos + 1);
			}
			return suffix;
		}
		
		public InputStream contents() throws IOException {
			return new FileInputStream(file);
		}		
	}	
	
	/**
	 * Recursively traverse a file system from a given location.
	 * 
	 * @param location
	 *            --- current directory in file system.
	 * @param pkg
	 *            --- package that location represents.
	 * @param entries
	 *            --- list of entries being accumulated into.
	 */
	private void traverse(File location, PkgID pkg, ArrayList<Path.Entry> entries) throws IOException {
		if (location.exists() && location.isDirectory()) {			
			for (File file : location.listFiles()) {				
				if(file.isDirectory()) {
					traverse(file,pkg.append(file.getName()),entries);
				} else if(filter.accept(file)) {
					String filename = file.getName();
					String name = filename.substring(0, filename.lastIndexOf('.'));
					ModuleID mid = new ModuleID(pkg, name);
					entries.add(new Entry(mid, file));
				}
			}	
		}
	}
}