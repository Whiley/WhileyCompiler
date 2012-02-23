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
	
	public final static FileFilter NULL_FILTER = new FileFilter() {
		public boolean accept(File file) {
			return true;
		}
	};
	
	private final FileFilter filter;	
	private final ContentType.Factory contentTypes;
	private final java.io.File srcDirectory;	
	private HashMap<PkgID,ArrayList<Entry>> cache = null;
	
	/**
	 * Construct a directory root from a filesystem path expressed as a string,
	 * and an appropriate file filter. In converting the path to a File object,
	 * an IOException may arise if it is an invalid path.
	 * 
	 * @param path
	 *            --- location of directory on filesystem, expressed as a native
	 *            path (i.e. separated using File.separatorChar, etc)
	 * @throws IOException
	 */
	public DirectoryRoot(String path, ContentType.Factory contentTypes) throws IOException {
		this.srcDirectory = new File(path);				
		this.filter = NULL_FILTER;
		this.contentTypes = contentTypes;
	}
	
	/**
	 * Construct a directory root from a filesystem path expressed as a string,
	 * and an appropriate file filter. In converting the path to a File object,
	 * an IOException may arise if it is an invalid path.
	 * 
	 * @param path
	 *            --- location of directory on filesystem, expressed as a native
	 *            path (i.e. separated using File.separatorChar, etc)
	 * @param filter
	 *            --- filter on which files are included.
	 * @throws IOException
	 */
	public DirectoryRoot(String path, FileFilter filter, ContentType.Factory contentTypes) throws IOException {
		this.srcDirectory = new File(path);				
		this.filter = filter;
		this.contentTypes = contentTypes;
	}

	/**
	 * Construct a directory root from a filesystem path expressed as a string,
	 * and an appropriate file filter. In converting the path to a File object,
	 * an IOException may arise if it is an invalid path.
	 * 
	 * @param path
	 *            --- location of directory on filesystem, expressed as a native
	 *            path (i.e. separated using File.separatorChar, etc)
	 * @throws IOException
	 */
	public DirectoryRoot(File dir, ContentType.Factory contentTypes) throws IOException {
		this.srcDirectory = dir;			
		this.filter = NULL_FILTER;
		this.contentTypes = contentTypes;
	}
	
	/**
	 * Construct a directory root from a filesystem path expressed as a string,
	 * and an appropriate file filter. In converting the path to a File object,
	 * an IOException may arise if it is an invalid path.
	 * 
	 * @param path
	 *            --- location of directory on filesystem, expressed as a native
	 *            path (i.e. separated using File.separatorChar, etc)
	 * @param filter
	 *            --- filter on which files are included.
	 * @throws IOException
	 */
	public DirectoryRoot(File dir, FileFilter filter, ContentType.Factory contentTypes) throws IOException {
		this.srcDirectory = dir;			
		this.filter = filter;
		this.contentTypes = contentTypes;
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
		
	public <T> Path.Entry<T> get(ModuleID mid, ContentType<T> ct) throws IOException {
		if(cache == null) {
			cache = new HashMap<PkgID,ArrayList<Entry>>();
			traverse(srcDirectory,PkgID.ROOT);
		}
		ArrayList<Entry> contents = cache.get(mid.pkg());
		if(contents != null) {			
			for(Entry e : contents) {				
				if (e.id().equals(mid) && ct.matches(e.suffix())) {
					return e;
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
		private final ContentType<T> contentType;
		private T contents;
		private boolean modified = false;
		
		public Entry(ModuleID mid, java.io.File file, ContentType<T> contentType) {
			this.mid = mid;
			this.file = file;
			this.contentType = contentType;
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
		
		public ContentType<T> contentType() {
			return contentType;
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
					int i = filename.lastIndexOf('.');
					String name = filename.substring(0, i);
					String suffix = filename.substring(i+1);
					ModuleID mid = new ModuleID(pkg, name);										
					entries.add(new Entry(mid, file, contentTypes.get(suffix)));					
				}				
			}	
			cache.put(pkg, entries);
		}
	}
}
