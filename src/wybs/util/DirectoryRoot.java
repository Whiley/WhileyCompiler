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

package wybs.util;

import java.io.*;
import java.util.*;

import wybs.lang.Content;
import wybs.lang.Content.Filter;
import wybs.lang.Path;
import wybs.lang.Content.Registry;
import wybs.lang.Content.Type;
import wybs.lang.Path.Entry;
import wybs.lang.Path.ID;

/**
 * Provides an implementation of <code>Path.Root</code> for representing a file
 * system directory.
 * 
 * @author David J. Pearce
 * 
 */
public final class DirectoryRoot extends AbstractRoot {
	
	public final static FileFilter NULL_FILTER = new FileFilter() {
		public boolean accept(File file) {
			return true;
		}
	};
	
	private final FileFilter filter;		
	private final File dir;
	
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
		this.dir = new File(path);
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
		this.dir = new File(path);				
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
		this.dir = dir;			
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
		this.dir = dir;			
		this.filter = filter;		
	}
	
	public File location() {
		return dir;
	}

	public String toString() {
		return dir.getPath();
	}
	
	@Override
	protected Folder root() {
		return new Folder(Trie.ROOT);
	}
	
	/**
	 * An entry is a file on the file system which represents a Whiley module. The
	 * file may be encoded in a range of different formats. For example, it may be a
	 * source file and/or a binary wyil file.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public static final class Entry<T> extends AbstractEntry<T> implements Path.Entry<T> {		
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
		
		public File file() {
			return file;
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
		
		public InputStream inputStream() throws IOException {
			return new FileInputStream(file);
		}
		
		public OutputStream outputStream() throws IOException {
			return new FileOutputStream(file);
		}
		
		public String toString() {
			return file.toString();
		}
	}
	
	/**
	 * Represents a directory on a physical file system.
	 * 
	 * @author David J. Pearce
	 *
	 */
	public final class Folder extends AbstractFolder {
		public Folder(Path.ID id) {
			super(id);
		}
		
		@Override
		protected Path.Item[] contents() throws IOException {			
			File myDir = new File(dir, id.toString().replace('/', File.separatorChar));		
			
			if (myDir.exists() && myDir.isDirectory()) {
				File[] files = myDir.listFiles(filter);
				Path.Item[] items = new Path.Item[files.length];
				int count = 0;
				for(int i=0;i!=files.length;++i) {
					File file = files[i];
					String filename = file.getName();
					if (file.isDirectory()) {
						items[count++] = new Folder(id.append(filename));
					} else {
						int idx = filename.lastIndexOf('.');
						if (idx > 0) {
							String name = filename.substring(0, idx);
							Path.ID oid = id.append(name);
							Entry e = new Entry(oid, file);
							contentTypes.associate(e);
							items[count++] = e;
						}
					}
				}
				
				if(count != items.length) {
					// trim the end since we didn't use all allocated elements.
					return Arrays.copyOf(items,count);
				} else {
					// minor optimisation
					return items;
				}
			} else {
				return new Path.Item[0];
			}
		}

		@Override
		public <T> Path.Entry<T> create(ID nid, Content.Type<T> ct,
				Path.Entry<?>... sources) throws IOException {	
			if (nid.size() == 1) {
				// attempting to create an entry in this folder
				Path.Entry<T> e = super.get(nid.subpath(0, 1), ct);
				if (e == null) {
					// Entry doesn't already exist, so create it
					nid = id.append(nid.get(0));
					String physID = nid.toString().replace('/',
							File.separatorChar);
					physID = physID + "." + contentTypes.suffix(ct);
					File nfile = new File(dir.getAbsolutePath()
							+ File.separatorChar + physID);
					e = new Entry(nid, nfile);
					e.associate(ct, null);
					super.insert(e);
				}
				return e;
			} else {
				// attempting to create entry in subfolder.
				Path.Folder folder = getFolder(nid.get(0));
				if (folder == null) {
					// Folder doesn't already exist, so create it.
					folder = new Folder(id.append(nid.get(0)));
					super.insert(folder);
				}
				return folder.create(nid.subpath(1, nid.size()), ct, sources);
			}
		}
		
		public String toString() {
			return dir + ":" + id;
		}
	}
}
