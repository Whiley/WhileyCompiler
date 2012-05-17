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

package wybs.lang;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.*;

public class Path {
	
	/**
	 * Represents a sequence of zero or more names which describe a path through
	 * the namespace for a given project. For example, "whiley/lang/Math" is a
	 * valid ID with three components: "whiley","lang","Math".
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
		 * Get a sub ID from this id, which consists of those components between
		 * start and end (exclusive).
		 * 
		 * @param start
		 *            --- starting component index
		 * @param start
		 *            --- one past last component index
		 * @return
		 */
		public ID subpath(int start, int end);
		
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
	 * Represents an abstract or physical item of some sort which is reachable
	 * from a <code>Root</code>. Valid instances of <code>Item</code> include
	 * those valid instances of <code>Entry</code> and <code>Folder</code>.
	 */
	public interface Item {
		/**
		 * Return the identify of this item.
		 * 
		 * @return
		 */
		public ID id();
		
		/**
		 * Force item to refresh contents from permanent storage (where
		 * appropriate). For items which have been modified, this operation has
		 * no effect (i.e. the new contents are retained). For folders, this
		 * forces sub-folders to be refreshed as well.
		 */
		public void refresh() throws IOException;

		/**
		 * Force item to write contents to permanent storage (where
		 * appropriate). For items which have not been modified, this operation
		 * has no effect (i.e. the old contents are retained). For folers, this
		 * forces sub-folders to be flushed as well.
		 */
		public void flush() throws IOException;
	}
	
	/**
	 * Represents a physical item of some sort which is reachable from a
	 * <code>Root</code>. Valid instances of <code>Entry</code> may correspond
	 * to files on the file system, entries in a Jar file, or abstractions from
	 * other tools (e.g. eclipse's <code>IFile</code>).
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public interface Entry<T> extends Item {

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
		 * Get the content type associated with this file. This provides a
		 * generic mechanism for describing the information contained within the
		 * file.
		 */
		public Content.Type<T> contentType();

		/**
		 * Associate this entry with a content type, and optionally provide the
		 * contents. The ability to provide the contents is a convenience
		 * function for cases where determining the content type requires
		 * actually reading the contents!
		 * 
		 * @param contentType
		 *            --- content type to associate
		 * @param contents
		 *            --- contents to associate, or null if none.
		 */
		public void associate(Content.Type<T> contentType, T contents);

		/**
		 * Return those entries (if any) which depend upon this entry. That is, if
		 * this entry is modified, then those entries should be rebuilt.
		 * 
		 * @return
		 */
		public Set<Path.Entry<?>> dependents();
		
		/**
		 * Return those entries (if any) upon which this entry depends. That is,
		 * if any of those entries are modified, then this entry should be
		 * rebuilt.
		 * 
		 * @return
		 */
		public Set<Path.Entry<?>> dependencies();
		
		/**
		 * Read contents of file. Note, however, that this does not mean the
		 * contents are re-read from permanent storage. If the contents are
		 * already available in memory, then they will returned without
		 * accessing permanent storage.
		 */
		public T read() throws IOException;

		/**
		 * Write the contents of this entry. It is assumed that the contents
		 * matches the content-type given for this entry. Finally, note also
		 * that this does not mean the contents are written to permanent
		 * storage.
		 * 
		 * @param contents
		 */
		public void write(T contents) throws IOException;

		/**
		 * Open a generic input stream to the entry.
		 * 
		 * @return
		 * @throws IOException
		 */
		public InputStream inputStream() throws IOException;

		/**
		 * Open a generic output stream to the entry.
		 * 
		 * @return
		 * @throws IOException
		 */
		public OutputStream outputStream() throws IOException;
	}

	/**
	 * An folder represents a special kind of entry which contains entries (and
	 * other folders). As such, it cannot be considered a concrete entry which
	 * can be read and written in the normal manner. Rather, it provides access
	 * to entries. For example, in a physical file system, a folder would
	 * correspond to a directory.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public interface Folder extends Item {
		
		/**
		 * Check whether or not a given entry is contained in this folder;
		 * 
		 * @param entry
		 * @return
		 */
		public boolean contains(Path.Entry<?> entry) throws IOException;

		/**
		 * folder) and content-type is contained in this folder.
		 * 
		 * @throws IOException
		 *             --- in case of some I/O failure.
		 */
		public boolean exists(ID id, Content.Type<?> ct) throws IOException;

		/**
		 * Get the entry corresponding to a given ID (taken relative to this
		 * folder) and content type. If no such entry exists, return null.
		 * 
		 * @param id
		 *            --- id of module to lookup.
		 * @return
		 * @throws ResolveError
		 *             if id is not found.
		 * @throws IOException
		 *             --- in case of some I/O failure.
		 */
		public <T> Path.Entry<T> get(ID id, Content.Type<T> ct)
				throws IOException;

		/**
		 * Get all objects matching a given content filter stored in this folder.
		 * In the case of no matches, an empty list is returned.
		 * 
		 * @throws IOException
		 *             --- in case of some I/O failure.
		 * 
		 * @param ct
		 * @return
		 */
		public <T> void getAll(Content.Filter<T> ct, List<Path.Entry<T>> entries)
				throws IOException;

		/**
		 * Identify all entries matching a given content filter stored in this
		 * folder. In the case of no matches, an empty set is returned.
		 * 
		 * @throws IOException
		 *             --- in case of some I/O failure.
		 * 
		 * @param filter
		 *            --- filter to match entries with.
		 * @return
		 */
		public <T> void getAll(Content.Filter<T> filter, Set<Path.ID> entries)
				throws IOException;
		
		/**
		 * Create a new entry in this folder with the given ID (taken relative
		 * to this folder) and content-type. This will recursively construct
		 * sub-folders as necessary.
		 * 
		 * @throws IOException
		 *             --- in case of some I/O failure.
		 * 
		 * @param entry
		 */		
		public <T> Path.Entry<T> create(Path.ID id, Content.Type<T> ct,
				Path.Entry<?>... sources) throws IOException;		
	}
	
	/**
	 * Represents the root of a hierarchy of named entries. A instance of root
	 * may correspond to a file system directory, a Jar file, or some other
	 * abstraction representings a collection of files (e.g. eclipse's
	 * <code>IContainer</code>).
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public interface Root {

		/**
		 * Check whether or not a given entry is contained in this root;
		 * 
		 * @param entry
		 * @return
		 */
		public boolean contains(Path.Entry<?> entry) throws IOException;

		/**
		 * Check whether or not a given entry and content-type is contained in
		 * this root.
		 * 
		 * @throws IOException
		 *             --- in case of some I/O failure.
		 */
		public boolean exists(ID id, Content.Type<?> ct) throws IOException;

		/**
		 * Get the entry corresponding to a given ID and content type. If no
		 * such entry exists, return null.
		 * 
		 * @param id
		 *            --- id of module to lookup.
		 * @return
		 * @throws ResolveError
		 *             if id is not found.
		 * @throws IOException
		 *             --- in case of some I/O failure.
		 */
		public <T> Path.Entry<T> get(ID id, Content.Type<T> ct)
				throws IOException;

		/**
		 * Get all objects matching a given content filter stored in this root.
		 * In the case of no matches, an empty list is returned.
		 * 
		 * @throws IOException
		 *             --- in case of some I/O failure.
		 * 
		 * @param ct
		 * @return
		 */
		public <T> List<Path.Entry<T>> get(Content.Filter<T> ct)
				throws IOException;

		/**
		 * Identify all entries matching a given content filter stored in this
		 * root. In the case of no matches, an empty set is returned.
		 * 
		 * @throws IOException
		 *             --- in case of some I/O failure.
		 * 
		 * @param filter
		 *            --- filter to match entries with.
		 * @return
		 */
		public <T> Set<Path.ID> match(Content.Filter<T> filter)
				throws IOException;

		/**
		 * Create an entry of a given content type at a given path, derived from
		 * zero or more entries. If the entry already exists, then it is just
		 * returned. An entry is derived from another entry if it is, in some
		 * way, generated from that entry (e.g. it is compiled from that file).
		 * 
		 * @param id
		 *            --- Path.ID for the new entry
		 * @param ct
		 *            --- content type of the new entry
		 * @param sources
		 *            --- entries from which this entry is derived.
		 * @return
		 * @throws IOException
		 */
		public <T> Path.Entry<T> create(ID id, Content.Type<T> ct, Path.Entry<?> ...sources)
				throws IOException;
		
		/**
		 * Force root to flush entries to permanent storage (where appropriate).
		 * This is essential as, at any given moment, path entries may only be
		 * stored in memory. We must flush them to disk in order to preserve any
		 * changes that were made.
		 */
		public void flush() throws IOException;

		/**
		 * Force root to refresh entries from permanent storage (where
		 * appropriate). For items which has been modified, this operation has
		 * no effect (i.e. the new contents are retained).
		 */
		public void refresh() throws IOException;
	}

	/**
	 * A generic mechanism for selecting one or more paths. For example, one
	 * might specify an includes="whiley/**\/*.whiley" filter on a given root to
	 * identify which source files should be compiled. This would be implemented
	 * using either a content or path filter.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	public interface Filter {

		/**
		 * Check whether a given entry is matched by this filter.
		 * 
		 * @param id
		 *            --- id to test.
		 * @return --- true if it matches, otherwise false.
		 */
		public boolean matches(Path.ID id);
		
		/**
		 * Check whether a given subpath is matched by this filter. A matching
		 * subpath does not necessarily identify an exact match; rather, it may
		 * be an enclosing folder.
		 * 
		 * @param id
		 * @return
		 */
		public boolean matchesSubpath(Path.ID id);
	}

}
