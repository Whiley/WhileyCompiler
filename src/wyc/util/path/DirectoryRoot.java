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

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import wyil.lang.ModuleID;
import wyil.lang.PkgID;

public class DirectoryRoot implements Path.Root {
	private static final FileFilter filter = new FileFilter() {
		public boolean accept(File file) {
			return file.getName().endsWith(".whiley");
		}
	};
	
	private final java.io.File srcDirectory;	
	private final BinaryDirectoryRoot outputDirectory;
	private HashMap<PkgID,ArrayList<Entry>> cache = null;
	
	/**
	 * Construct a directory root from a filesystem path expressed as a string,
	 * and an appropriate file filter. In converting the path to a File object,
	 * an IOException may arise if it is an invalid path.
	 * 
	 * @param path
	 *            --- location of directory on filesystem, expressed as a native
	 *            path (i.e. separated using File.separatorChar, etc)
	 * @param outputDirectory
	 *            --- the output directory for this source directory (or null if
	 *            source directory is output directory).
	 * @throws IOException
	 */
	public DirectoryRoot(String path, BinaryDirectoryRoot outputDirectory) throws IOException {
		this.srcDirectory = new File(path);				
		this.outputDirectory = outputDirectory;
	}
	
	/**
	 * Construct a directory root from a filesystem path expressed as a string,
	 * and an appropriate file filter. In converting the path to a File object,
	 * an IOException may arise if it is an invalid path.
	 * 
	 * @param path
	 *            --- location of directory on filesystem, expressed as a native
	 *            path (i.e. separated using File.separatorChar, etc)
	 * @param outputDirectory
	 *            --- the output directory for this source directory (or null if
	 *            source directory is output directory).
	 * @throws IOException
	 */
	public DirectoryRoot(File dir, BinaryDirectoryRoot outputDirectory) throws IOException {
		this.srcDirectory = dir;			
		this.outputDirectory = outputDirectory;
	}
	
	public File location() {
		return srcDirectory;
	}
	
	public boolean exists(PkgID pkg) throws IOException {
		if(cache == null) {
			cache = new HashMap<PkgID,ArrayList<Entry>>();
			traverse(srcDirectory,PkgID.ROOT);
		}
		return cache.containsKey(pkg);
	}
	
	public List<Entry> list() throws IOException {
		if(cache == null) {
			cache = new HashMap<PkgID,ArrayList<Entry>>();
			traverse(srcDirectory,PkgID.ROOT);
		}	
		ArrayList<Entry> entries = new ArrayList<Entry>();
		for(PkgID pid : cache.keySet()) {
			entries.addAll(cache.get(pid));
		}
		return entries;
	}
	
	public List<Entry> list(PkgID pkg) throws IOException {
		if(cache == null) {
			cache = new HashMap<PkgID,ArrayList<Entry>>();
			traverse(srcDirectory,PkgID.ROOT);
		}	
		return cache.get(pkg);
	}
		
	public <T extends Path.Entry> T get(ModuleID mid, ContentType<T> ct) throws IOException {
		if(cache == null) {
			cache = new HashMap<PkgID,ArrayList<Entry>>();
			traverse(srcDirectory,PkgID.ROOT);
		}
		ArrayList<Entry> contents = cache.get(mid.pkg());
		if(contents != null) {			
			for(Entry e : contents) {
				T t = ct.accept(e);
				if(t != null && e.id().equals(mid)) {					
					return t;
				}
			}			
		}
		return null;
	}

	public String toString() {
		return srcDirectory.getPath();
	}
	
	/**
	 * A WFile is a file on the file system which represents a Whiley module. The
	 * file may be encoded in a range of different formats. For example, it may be a
	 * source file and/or a binary wyil file.
	 * 
	 * @author djp
	 * 
	 */
	public static class Entry<T> implements Path.Entry<T> {
		private final ModuleID mid;
		private final java.io.File file;
		private ContentType<T> contentType;
		private T contents;
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
		
		public T read() throws IOException {
			if (contents == null) {
				contents = contentType.read(new FileInputStream(file));
			}
			return contents;
		}		
		
		public void write(T contents) throws IOException {
			this.contents = contents; 
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
	private void traverse(File location, PkgID pkg) throws IOException {
		if (location.exists() && location.isDirectory()) {			
			ArrayList<Entry> entries = new ArrayList<Entry>();
			String path = location.getPath();
			for (File file : location.listFiles()) {						
				if(file.isDirectory()) {
					traverse(file,pkg.append(file.getName()));
				} else if(filter.accept(file)) {					
					String filename = file.getName();					
					String name = filename.substring(0, filename.lastIndexOf('.'));
					ModuleID mid = new ModuleID(pkg, name);					
					BinaryDirectoryRoot.Entry binEntry = null;

					// Now, see if there exists a binary version of this file which has
					// a modification date no earlier. Binary files are always preferred
					// over source entries.

					if (outputDirectory != null) {
						binEntry = outputDirectory.get(mid);					
					} else {
						File binFile = new File(path + File.separatorChar + name + ".class");
						if(binFile.exists()) {
							binEntry = new BinaryDirectoryRoot.Entry(mid,binFile);
						}
					}

					entries.add(new Entry(mid, file, binEntry));					
				}				
			}	
			cache.put(pkg, entries);
		}
	}
}
