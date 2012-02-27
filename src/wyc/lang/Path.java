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

package wyc.lang;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import wyc.lang.Content.Type;

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
		 * 
		 * @throws Exception
		 *             --- in case of some failure (e.g. IOException).
		 */
		public boolean exists(ID id, Content.Type<?> ct) throws Exception;
		
		/**
		 * Lookup a given object.
		 * 
		 * @param id
		 *            --- id of module to lookup.
		 * @return
		 * @throws ResolveError
		 *             if id is not found.
		 * @throws Exception
		 *             --- in case of some failure (e.g. IOException).
		 */
		public <T> Path.Entry<T> get(ID id, Content.Type<T> ct) throws Exception;
		
		/**
		 * Get all objects matching a given content filter stored in this root.
		 * 
		 * @throws Exception
		 *             --- in case of some failure (e.g. IOException).
		 * 
		 * @param ct
		 * @return
		 */
		public <T> List<Path.Entry<T>> get(Filter<T> ct) throws Exception;
		
		/**
		 * Identify all objects matching a given content filter stored in this root.
		 * 
		 * @throws Exception
		 *             --- in case of some failure (e.g. IOException).
		 * 
		 * @param ct
		 * @return
		 */
		public <T> Set<Path.ID> match(Filter<T> ct) throws Exception;
		
		/**
		 * Create an entry of a given content type at a given path. If the entry
		 * already exists, then it is just returned.
		 * 
		 * @param id
		 * @param ct
		 * @return
		 * @throws Exception
		 */
		public <T> Path.Entry<T> create(ID id, Content.Type<T> ct) throws Exception;
		
		/**
		 * Force root to flush entries to permanent storage (where appropriate).
		 * This is essential as, at any given moment, path entries may only be
		 * stored in memory. We must flush them to disk in order to preserve any
		 * changes that were made.
		 */
		public void flush() throws Exception;
		
		/**
		 * Force root to refresh entries from permanent storage (where
		 * appropriate). For items which has been modified, this operation has
		 * no effect (i.e. the new contents are retained).
		 */
		public void refresh() throws Exception;
	}
	
	public interface Filter<T> {

		/**
		 * Check whether a given entry is matched by this filter.
		 * 
		 * @param entry
		 *            --- entry to test.
		 * @return --- entry (retyped) if it matches, otherwise null.
		 */
		public Path.Entry<T> match(Path.Entry<?> entry);		
	}

	public static <T> Filter<T> chain(final Filter<?> filter, final Content.Type<T> contentType) {
		return new Filter<T>() {
			public Path.Entry<T> match(Path.Entry<?> entry) {
				if(entry.contentType() == contentType) {
					return (Path.Entry<T>) filter.match(entry);
				}
				return null;
			}
		};
	}
}
