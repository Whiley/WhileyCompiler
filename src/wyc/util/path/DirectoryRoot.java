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

import wyil.lang.ModuleID;
import wyil.lang.PkgID;

public final class DirectoryRoot extends Path.AbstractRoot {
	
	public final static FileFilter NULL_FILTER = new FileFilter() {
		public boolean accept(File file) {
			return true;
		}
	};
	
	private final FileFilter filter;		
	private final java.io.File srcDirectory;		
	
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
	public DirectoryRoot(String path, ContentType.Registry contentTypes) throws IOException {
		super(contentTypes);
		this.srcDirectory = new File(path);				
		this.filter = NULL_FILTER;		
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
	public DirectoryRoot(String path, FileFilter filter, ContentType.Registry contentTypes) throws IOException {
		super(contentTypes);
		this.srcDirectory = new File(path);				
		this.filter = filter;		
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
	public DirectoryRoot(File dir, ContentType.Registry contentTypes) throws IOException {
		super(contentTypes);
		this.srcDirectory = dir;			
		this.filter = NULL_FILTER;		
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
	public DirectoryRoot(File dir, FileFilter filter, ContentType.Registry contentTypes) throws IOException {
		super(contentTypes);
		this.srcDirectory = dir;			
		this.filter = filter;		
	}
	
	public File location() {
		return srcDirectory;
	}

	public String toString() {
		return srcDirectory.getPath();
	}

	protected Path.Entry[] contents() throws IOException {
		ArrayList<Path.Entry> contents = new ArrayList<Path.Entry>();
		traverse(srcDirectory,PkgID.ROOT,contents);
		return contents.toArray(new Path.Entry[contents.size()]);
	}
	
	/**
	 * A WFile is a file on the file system which represents a Whiley module. The
	 * file may be encoded in a range of different formats. For example, it may be a
	 * source file and/or a binary wyil file.
	 * 
	 * @author djp
	 * 
	 */
	public static class Entry<T> extends Path.AbstractEntry<T> implements Path.Entry<T> {		
		private final java.io.File file;
		
		public Entry(ModuleID mid, java.io.File file, ContentType<T> contentType) {
			super(mid,contentType);			
			this.file = file;			
		}
		
		public String location() {
			return file.getPath();
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
		
		public InputStream inputStream() throws Exception {
			return new FileInputStream(file);
		}
		
		public OutputStream outputStream() throws Exception {
			return new FileOutputStream(file);
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
	private void traverse(File location, PkgID pkg, ArrayList<Path.Entry> contents) throws IOException {
		if (location.exists() && location.isDirectory()) {						
			String path = location.getPath();
			for (File file : location.listFiles()) {						
				if(file.isDirectory()) {
					traverse(file,pkg.append(file.getName()),contents);
				} else if(filter.accept(file)) {					
					String filename = file.getName();	
					int i = filename.lastIndexOf('.');
					String name = filename.substring(0, i);
					String suffix = filename.substring(i+1);
					ModuleID mid = new ModuleID(pkg, name);										
					contents.add(new Entry(mid, file, contentTypes.get(suffix)));					
				}				
			}				
		}
	}
}
