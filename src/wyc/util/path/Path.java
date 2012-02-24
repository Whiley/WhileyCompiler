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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import wyc.util.path.DirectoryRoot.Entry;
import wyil.lang.ModuleID;
import wyil.lang.PkgID;

public interface Path {
	
	public interface Root {		
		
		/**
		 * Check whether or not a given package is contained.
		 */
		public boolean exists(PkgID pid) throws Exception;
		
		/**
		 * Check whether or not a given package is contained.
		 */
		public <T> boolean exists(ModuleID pid, ContentType<T> ct) throws Exception;		
		
		/**
		 * Lookup a given module.
		 * 
		 * @param mid
		 *            --- id of module to lookup.
		 * @return
		 * @throws IOException
		 */
		public <T> Path.Entry<T> get(ModuleID mid, ContentType<T> ct) throws Exception;
		
		/**
		 * List contents of a given package.
		 * 
		 * @param pid
		 * @return
		 * @throws IOException
		 */
		public Collection<? extends Entry> list(PkgID pid) throws Exception;
		
		/**
		 * List contents of all packages.
		 * 
		 * @return
		 * @throws IOException
		 */
		public Collection<? extends Entry> list() throws Exception;		
	}
	
	/**
	 * A path entry represents an item reachable from root on the WHILEYPATH
	 * which corresponds to a Whiley Module.
	 * 
	 * @author djp
	 * 
	 */
	public interface Entry<T> {
		public ModuleID id();

		/**
		 * Return the suffix of the item in question. This is necessary to
		 * determine how we will process this item.
		 * 
		 * @return
		 */
		public String suffix();
		
		/**
		 * Return a string indicating the location of this entry.  
		 * @return
		 */
		public String location();
		
		/**
		 * Get the last modification time for this file.
		 * 
		 * @return
		 */
		public long lastModified();
		
		/**
		 * Check whether this file has been modified or not.
		 * 
		 * @return
		 */
		public boolean isModified();
		
		/**
		 * Mark this entry as being modified
		 * 
		 * @return
		 */
		public void touch();
		
		/**
		 * Get the content type associated with this file.
		 */
		public ContentType<T> contentType();
		
		/**
		 * Read contents of file
		 */
		public T read() throws Exception;
			
		/**
		 * Write contents of file.
		 * 
		 * @param contents
		 */
		public void write(T contents) throws Exception;
		

		/**
		 * Open a generic input stream to the entry.
		 * 
		 * @return
		 * @throws Exception
		 */
		public InputStream inputStream() throws Exception;
		
		/**
		 * Open a generic output stream to the entry.
		 * 
		 * @return
		 * @throws Exception
		 */
		public OutputStream outputStream() throws Exception;
	}	
	
	public static abstract class AbstractEntry<T> implements Entry<T> {
		private final ModuleID mid;		
		private final ContentType<T> contentType;
		private T contents = null;
		private boolean modified = false;
		
		public AbstractEntry(ModuleID mid, ContentType<T> contentType) {
			this.mid = mid;
			this.contentType = contentType;
		}
		
		public ModuleID id() {
			return mid;
		}
		
		public void touch() {
			this.modified = true;
		}
		
		public boolean isModified() {
			return modified;
		}
		
		public ContentType<T> contentType() {
			return contentType;
		}
		
		public T read() throws Exception {
			if (contents == null) {
				contents = contentType.read(this);
			}
			return contents;
		}		
				
		public void write(T contents) throws Exception {
			this.contents = contents; 
		}
	}
	
	public static abstract class AbstractRoot implements Root {
		protected final ContentType.Registry contentTypes;
		private Path.Entry[] contents = null;
		
		public AbstractRoot(ContentType.Registry contentTypes) {
			this.contentTypes = contentTypes;
		}
		
		public boolean exists(PkgID pkg) throws Exception {
			if(contents == null) {
				contents = contents();
			}
			for(Path.Entry e : contents) {
				if(e.id().pkg().equals(pkg)) {
					return true;
				}
			}
			return false;
		}
		
		public <T> boolean exists(ModuleID mid, ContentType<T> ct) throws Exception {
			if(contents == null) {
				contents = contents();
			}
			for(Path.Entry e : contents) {
				if (e.id().equals(mid) && e.contentType() == ct) {
					return true;
				}
			}
			return false;
		}
		
		public List<Entry> list() throws Exception {
			if(contents == null) {
				contents = contents();
			}	
			ArrayList<Entry> entries = new ArrayList<Entry>();			
			for(Path.Entry e : contents) {
				entries.add(e);
			}
			return entries;
		}
		
		public List<Entry> list(PkgID pkg) throws Exception {
			if(contents == null) {
				contents = contents();
			}	
			ArrayList<Entry> entries = new ArrayList<Entry>();			
			for(Path.Entry e : contents) {
				if(e.id().pkg().equals(pkg)) {
					entries.add(e);
				}
			}
			return entries;			
		}
		
		public <T> Path.Entry<T> get(ModuleID mid, ContentType<T> ct) throws Exception {
			if(contents == null) {
				contents = contents();
			}
			for(Path.Entry e : contents) {
				if (e.id().equals(mid) && ct.matches(e.suffix())) {
					return e;
				}				
			}
			return null;
		}
		
		/**
		 * Extract all entries from the given type.
		 */
		protected abstract Path.Entry[] contents() throws Exception;
	}
}
