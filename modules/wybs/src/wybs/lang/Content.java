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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import wybs.lang.Path.Filter;
import wybs.util.Trie;

public class Content {

	/**
	 * Provides an abstract mechanism for reading and writing file in
	 * a given format. Whiley source files (*.whiley) are one example, whilst JVM
	 * class files (*.class) are another.
	 * 
	 * @author David J. Pearce
	 * 
	 * @param <T>
	 */
	public interface Type<T> {
		
		/**
		 * Physically read the raw bytes from a given input stream and convert
		 * into the format described by this content type.
		 * 
		 * @param input
		 *            --- input stream representing in the format described by
		 *            this content type.
		 * @return
		 */
		public T read(Path.Entry<T> e, InputStream input) throws IOException;
		
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
	 * A generic mechanism for selecting a subset of content based on a path
	 * filter and a content type. For example, one might specify an
	 * includes="whiley/**\/*.whiley" filter on a given root to identify which
	 * source files should be compiled. This would be implemented using either a
	 * content or path filter.
	 * 
	 * @author David J. Pearce
	 * 
	 * @param <T>
	 */
	public interface Filter<T> {

		/**
		 * Check whether a given entry is matched by this filter.
		 * 
		 * @param entry
		 *            --- entry to test.
		 * @return --- entry (retyped) if it matches, otherwise null.
		 */
		public boolean matches(Path.ID id, Content.Type<T> ct);	
		
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
	
	/**
	 * Construct a content filter from a path filter and a content type.
	 * 
	 * @param filter --- path filter
	 * @param contentType
	 * @return
	 */
	public static <T> Filter<T> filter(final Path.Filter filter, final Content.Type<T> contentType) {
		return new Filter<T>() {
			public boolean matches(Path.ID id, Content.Type<T> ct) {
				return ct == contentType && filter.matches(id);
			}
			public boolean matchesSubpath(Path.ID id) {
				return filter.matchesSubpath(id);
			}
			public String toString() {
				return filter.toString();
			}
		};
	}	
	
	/**
	 * Construct a content filter from a string representing a path filter and a content type.
	 * 
	 * @param filter --- path filter
	 * @param contentType
	 * @return
	 */
	public static <T> Filter<T> filter(final String pathFilter, final Content.Type<T> contentType) {
		final Path.Filter filter = Trie.fromString(pathFilter);
		return new Filter<T>() {
			public boolean matches(Path.ID id, Content.Type<T> ct) {
				return ct == contentType && filter.matches(id);
			}
			public boolean matchesSubpath(Path.ID id) {
				return filter.matchesSubpath(id);
			}
			public String toString() {
				return filter.toString();
			}
		};
	}	
	/**
	 * Combine two filters together produce one filter whose items must be
	 * matched by at least one of the original filters.
	 * 
	 * @param f1
	 * @param f2
	 * @return
	 */
	public static <T> Filter<T> or(final Filter<T> f1, final Filter<T> f2) {
		return new Filter<T>() {
			public boolean matches(Path.ID id, Content.Type<T> ct) {
				return f1.matches(id, ct) || f2.matches(id, ct);
			}
			public boolean matchesSubpath(Path.ID id) {
				return f1.matchesSubpath(id) || f2.matchesSubpath(id);
			}
			public String toString() {
				return f1.toString() + "|" + f2.toString();
			}
		};
	}
	
	/**
	 * Combine two filters together produce one filter whose items must be
	 * matched by both of the original filters.
	 * 
	 * @param f1
	 * @param f2
	 * @return
	 */
	public static <T> Filter<T> and(final Filter<T> f1, final Filter<T> f2) {
		return new Filter<T>() {
			public boolean matches(Path.ID id, Content.Type<T> ct) {
				return f1.matches(id, ct) && f2.matches(id, ct);
			}
			public boolean matchesSubpath(Path.ID id) {
				return f1.matchesSubpath(id) && f2.matchesSubpath(id);
			}
			public String toString() {
				return f1.toString() + "&" + f2.toString();
			}
		};
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
		 * Attempt to associate a content type with this entry.
		 * 
		 * @param e
		 *            --- entry to associate with a content type.
		 * @return
		 */
		public void associate(Path.Entry<?> e);
		
		/**
		 * Determine an appropriate suffix for a given content type.
		 * 
		 * @param t
		 * @return
		 */
		public String suffix(Type<?> t);
	}
}
