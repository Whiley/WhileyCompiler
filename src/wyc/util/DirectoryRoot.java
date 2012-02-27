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

package wyc.util;

import java.io.*;
import java.util.*;

import wyc.lang.Content;
import wyc.lang.Path;
import wyc.lang.Content.Registry;
import wyc.lang.Content.Type;
import wyc.lang.Path.Entry;
import wyc.lang.Path.ID;

public final class DirectoryRoot extends AbstractRoot {
	
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
	public DirectoryRoot(String path, Content.Registry contentTypes) throws IOException {
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
	public DirectoryRoot(String path, FileFilter filter, Content.Registry contentTypes) throws IOException {
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
	public DirectoryRoot(File dir, Content.Registry contentTypes) throws IOException {
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
	public DirectoryRoot(File dir, FileFilter filter, Content.Registry contentTypes) throws IOException {
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

	public <T> Path.Entry<T> create(Path.ID id, Content.Type<T> ct) throws Exception {
		Path.Entry<T> e = super.get(id,ct);
		if(e == null) {
			// need to do something here
		}
		return e;
	}
	
	public void flush() {
		// TODO
	}
	
	protected Path.Entry<?>[] contents() throws IOException {
		ArrayList<Path.Entry<?>> contents = new ArrayList<Path.Entry<?>>();
		traverse(srcDirectory,TreeID.ROOT,contents);
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
	private static final class Entry<T> extends AbstractEntry<T> implements Path.Entry<T> {		
		private final java.io.File file;
		
		public Entry(Path.ID id, java.io.File file) {
			super(id);
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
	private void traverse(File location, TreeID id,
			ArrayList<Path.Entry<?>> contents) throws IOException {
		if (location.exists() && location.isDirectory()) {
			for (File file : location.listFiles(filter)) {
				if (file.isDirectory()) {
					traverse(file, id.append(file.getName()), contents);
				} else {
					String filename = file.getName();
					int i = filename.lastIndexOf('.');
					if (i > 0) {
						String name = filename.substring(0, i);
						String suffix = filename.substring(i + 1);
						Path.ID oid = id.append(name);
						Entry e = new Entry(oid, file);
						contentTypes.associate(e);
						contents.add(e);
					}
				}
			}
		}
	}
}
