// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wycc.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import wycc.util.Trie;


/**
 * Provides various interfaces for mapping the structured content held in memory
 * to unstructured content held on disk. For example, we can define sources of
 * structured content from directories or compressed archives.
 *
 * @author David J. Pearce
 *
 */
public interface Content {
	/**
	 * Get the content type associated with this piece of content.
	 *
	 * @return
	 */
	public Type<?> getContentType();

	/**
	 * Provides a means for selecting one or more content types from a repository or
	 * root.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Filter<T extends Content> {
		/**
		 * Check whether this filter includes a given content type
		 *
		 * @param ct Content Type to check for inclusion.
		 * @return
		 */
		public boolean includes(Content.Type<?> ct, Trie p);
	}

	/**
	 * Provides an abstract mechanism for reading and writing file in
	 * a given format. Whiley source files (*.whiley) are one example, whilst JVM
	 * class files (*.class) are another.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 */
	public interface Type<T extends Content> {
		/**
		 * Get the suffix associated with this content type
		 *
		 * @return
		 */
		public String getSuffix();

		/**
		 * Physically read the raw bytes from a given input stream and convert into the
		 * format described by this content type.
		 *
		 * @param id       Name of this entry.
		 * @param input    Input stream representing in the format described by this
		 *                 content type.
		 * @param registry Content registry to be used for creating content within the
		 *                 given type.
		 * @return
		 */
		public T read(Trie id, InputStream input, Content.Registry registry) throws IOException;

		/**
		 * Convert an object in the format described by this content type into
		 * an appropriate byte stream and write it to an output stream
		 *
		 * @param output
		 *            --- stream which this value is to be written to.
		 * @param value
		 *            --- value to be converted into bytes.
		 */
		public void write(OutputStream output, T value) throws IOException;
	}

	/**
	 * Provides a general mechanism for reading content from a given source.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Source {
		/**
		 * Get a given piece of content from this source.
		 *
		 * @param <T>
		 * @param kind
		 * @param p
		 * @return
		 */
		public <T extends Content> T get(Content.Type<T> kind, Trie p) throws IOException;

		/**
		 * Get a given piece of content from this source.
		 *
		 * @param <T>
		 * @param kind
		 * @param p
		 * @return
		 */
		public <T extends Content> List<T> getAll(Content.Filter<T> filter) throws IOException;

		/**
		 * Find all content matching a given filter.
		 *
		 * @param <S>
		 * @param kind
		 * @param f
		 * @return
		 */
		public List<Trie> match(Content.Filter<? extends Content> filter);

		/**
		 * Find all content matching a given predicate.
		 *
		 * @param <S>
		 * @param kind
		 * @param p
		 * @return
		 */
		public <T extends Content> List<Trie> match(Content.Filter<T> ct, Predicate<T> p);

		/**
		 * Get the content regsitry associated with this source.
		 *
		 * @return
		 */
		public Registry getContentRegistry();
	}

	/**
	 * A ledger is content source which is additionally journaled. That means we can
	 * see <i>differences</i> between files, rather than just the state of the files
	 * are they are now. In particular, the ledger provides sequence of
	 * <i>snapshots</i> which determine the state at different points in time.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Ledger extends Source {
		/**
		 * Get the number of snapshots within the ledger.
		 *
		 * @return
		 */
		public int size();

		/**
		 * Get a given snapshot from within this ledger. The snapshot handle must be
		 * between <code>0</code> and <code>size()-1</code>.
		 */
		public Source get(int snapshot);
	}

	/**
	 * Provides a general mechanism for writing content into a given source.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Sink {
		/**
		 * Write a given piece of content into this sink.
		 *
		 * @param <T>
		 * @param kind
		 * @param p
		 * @param value
		 */
		public void put(Trie p, Content value);

		/**
		 * Remove a given piece of content from this sink.
		 * @param p
		 */
		public void remove(Trie p, Content.Type<?> type);

		/**
		 * Get the content regsitry associated with this sink.
		 *
		 * @return
		 */
		public Registry getContentRegistry();
	}

	/**
	 * A content root represents an interface to an underlying medium (e.g. the file
	 * system). As such it provides both read and write access, along with the
	 * ability for synchronisation.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Root extends Source, Sink {
		/**
		 * Create a relative root. That is, a root which is relative to this
		 * root.
		 *
		 * @param id
		 * @return
		 */
		public Root subroot(Trie path);

		/**
		 * Synchronise this root against the underlying medium. This does two things. It
		 * flushes writes and invalidates items which have changed on disk. Invalidate
		 * items will then be reloaded on demand when next requested.
		 */
		public void synchronise() throws IOException;
	}

	/**
	 * <p>
	 * Responsible for associating content types to path entries. The simplest
	 * way to do this is to base the decision purely on the suffix of the entry
	 * in question. A standard implementation (wyc.util.SuffixRegistry) is
	 * provided for this common case.
	 * </p>
	 *
	 * <p>
	 * In some situations, it does occur on occasion that suffix alone is not
	 * enough. For example, a JVM class file may correspond to multiple content
	 * types if it may come from different source languages. In such cases, a
	 * probe of the content may be required to fully determine the content type.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Registry {
		/**
		 * Determine an appropriate suffix for a given content type.
		 *
		 * @param t
		 * @return
		 */
		public String suffix(Type<?> t);

		/**
		 * Determine the content type appropriate for a given suffix (if any).
		 *
		 * @param suffix
		 * @return <code>null</code> if none found.
		 */
		public Type<?> contentType(String suffix);
	}

	/**
	 * Matches all possible content.
	 */
	public static final Filter<? extends Content> ANY = new Content.Filter<>() {
		@Override
		public boolean includes(Content.Type<?> ct, Trie p) {
			return true;
		}
	};

	public static <T extends Content> Filter<T> Filter(Content.Type<T> ct, Trie path) {
		return new Content.Filter<T>() {

			@Override
			public boolean includes(Type<?> c, Trie p) {
				return ct == c && path.matches(p);
			}

		};
	}

	/**
	 * Default implementation of a content registry. This associates a given set
	 * of content types and suffixes. The intention is that plugins register new
	 * content types and these will end up here.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class DefaultRegistry implements Registry {
		private HashMap<String, Content.Type> contentTypes = new HashMap<>();

		public DefaultRegistry register(Content.Type<?> contentType, String suffix) {
			contentTypes.put(suffix, contentType);
			return this;
		}

		public DefaultRegistry unregister(Content.Type<?> contentType, String suffix) {
			contentTypes.remove(suffix);
			return this;
		}

		@Override
		public String suffix(Content.Type<?> t) {
			for (Map.Entry<String, Content.Type> p : contentTypes.entrySet()) {
				if (p.getValue() == t) {
					return p.getKey();
				}
			}
			// Couldn't find it!
			return null;
		}

		@Override
		public Content.Type<?> contentType(String suffix) {
			return contentTypes.get(suffix);
		}
	}
}
