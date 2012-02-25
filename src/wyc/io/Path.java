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

package wyc.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class Path {
	
	/**
	 * A Path ID represents a sequence of zero or more names which describe a
	 * path through the namespace for a given project.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public interface ID extends Iterable<String>, Comparable<ID> {
		
		/**
		 * Get the number of components that make up this ID.
		 * @return
		 */
		public int size();
		
		/**
		 * Return the component at a given index.
		 * @param index
		 * @return
		 */
		public String get(int index);
		
		/**
		 * A convenience function that gets the last component of this path.
		 * 
		 * @return
		 */
		public String last();
		
		/**
		 * Get the parent of this path.
		 * 
		 * @return
		 */
		public ID parent();
		
		/**
		 * Append a component onto the end of this id.
		 * 
		 * @param component
		 *            --- to be appended
		 * @return
		 */
		public ID append(String component);
	}
	
	/**
	 * A path entry represents an item reachable from root on the WHILEYPATH
	 * which corresponds to a Whiley Module.
	 * 
	 * @author djp
	 * 
	 */
	public interface Entry<T> {
		
		/**
		 * Return the identify of this entry.
		 * 
		 * @return
		 */
		public ID id();

		/**
		 * Return the suffix of the item in question. This is necessary to
		 * determine how we will process this item.
		 * 
		 * @return
		 */
		public String suffix();
		
		/**
		 * Return a string indicating the true location of this entry.
		 * 
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
		 * Mark this entry as being modified.
		 * 
		 * @return
		 */
		public void touch();
		
		/**
		 * Get the content type associated with this file.
		 */
		public Content.Type<T> contentType();
		
		/**
		 * Read contents of file
		 */
		public T read() throws Exception;
			
		/**
		 * Write some data (the new content) in a given content type to this file.
		 * 
		 * @param contents
		 */
		public void write(Content.Type<T> contentType, T contents) throws Exception;
		
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
	
	/**
	 * Represents the root of a hierarchy of named objects. 
	 * 
	 * @author djp
	 *
	 */
	public interface Root {		
		
		/**
		 * Check whether or not a given package is contained.
		 */
		public boolean exists(ID id, Content.Type<?> ct) throws Exception;
		
		/**
		 * Lookup a given object.
		 * 
		 * @param mid
		 *            --- id of module to lookup.
		 * @return
		 * @throws IOException
		 */
		public <T> Path.Entry<T> get(ID id, Content.Type<T> ct) throws Exception;
		
		/**
		 * List all objects of a given content type stored in this root.
		 * 
		 * @param ct
		 * @return
		 */
		public <T> List<Path.Entry<T>> list(Content.Filter<T> ct) throws Exception;
	}
	
	public static abstract class AbstractEntry<T> implements Entry<T> {
		private final ID id;		
		private Content.Type<T> contentType;
		private T contents = null;
		private boolean modified = false;
		
		public AbstractEntry(ID mid, Content.Type<T> contentType) {
			this.id = mid;
			this.contentType = contentType;
		}
		
		public ID id() {
			return id;
		}
		
		public void touch() {
			this.modified = true;
		}
		
		public boolean isModified() {
			return modified;
		}
		
		public Content.Type<T> contentType() {
			return contentType;
		}
		
		public T read() throws Exception {
			if (contents == null) {
				contents = contentType.read(this);
			}
			return contents;
		}		
				
		public void write(Content.Type<T> contentType, T contents) throws Exception {
			this.contentType = contentType;
			this.contents = contents; 
		}
	}
	
	public static abstract class AbstractRoot implements Root {
		protected final Content.Registry contentTypes;
		private Path.Entry<?>[] contents = null;
		
		public AbstractRoot(Content.Registry contentTypes) {
			this.contentTypes = contentTypes;
		}
		
		public boolean exists(ID id, Content.Type<?> ct) throws Exception {
			if(contents == null) {
				contents = contents();
			}
			for(Path.Entry e : contents) {
				if (e.id().equals(id) && e.contentType() == ct) {
					return true;
				}
			}
			return false;
		}
		
		public <T> Path.Entry<T> get(ID id, Content.Type<T> ct) throws Exception {
			if(contents == null) {
				contents = contents();
			}
			for (Path.Entry e : contents) {
				if (e.id().equals(id) && e.contentType() == ct) {
					return e;
				}
			}
			return null;
		}
		
		public <T> List<Entry<T>> list(Content.Filter<T> filter) throws Exception {
			if(contents == null) {
				contents = contents();
			}	
			ArrayList<Entry<T>> entries = new ArrayList<Entry<T>>();			
			for(Path.Entry<?> e : contents) {
				Path.Entry<T> r = filter.match(e);
				if(r != null) {
					entries.add(r);
				}
			}
			return entries;
		}
		
		
		/**
		 * Extract all entries from the given type.
		 */
		protected abstract Path.Entry<?>[] contents() throws Exception;
	}
}
