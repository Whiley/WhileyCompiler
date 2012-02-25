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

import java.util.HashMap;

public class Content {

	public interface Filter<T> {

		/**
		 * Check whether a given entry is matched by this filter.
		 * 
		 * @param entry
		 *            --- entry to test.
		 * @return --- entry (retyped) if it matches, otherwise null.
		 */
		public Path.Entry<T> match(Path.Entry<?> entry);
		
		/**
		 * Get the content type that this filter matches.
		 * @return
		 */
		public Type<?> contentType();
	}
	
	/**
	 * Construct a filter which matches all items with a given parent path, and
	 * with a given content type.
	 * 
	 * @param id
	 * @param ct
	 * @return
	 */
	public static <T> Filter<T> pathFilter(final Path.ID id, final Content.Filter<T> ct) {
		return new Filter<T>() {
			public Path.Entry<T> match(Path.Entry<?> e) {
				if(e.id().parent().equals(id)) {
					return ct.match(e);
				}
				return null;
			}
			public Content.Type<?> contentType() {
				return ct.contentType();
			}
		};
	}
	
	/**
	 * A content type provides an abstract mechanism for reading and writing file in
	 * a given format. Whiley source files (*.whiley) are one example, whilst JVM
	 * class files (*.class) are another.
	 * 
	 * @author David J. Pearce
	 * 
	 * @param <T>
	 */
	public interface Type<T> {
		
		/**
		 * Read the contents of source matching this content type.
		 * 
		 * @param input
		 *            --- input stream representing in the format described by this
		 *            content type.
		 * @return
		 */
		public T read(Path.Entry entry) throws Exception;
		
		/**
		 * Convert a given value into an appropriate byte stream and write it to a
		 * given output.
		 * 
		 * @param output
		 *            --- stream which this value is to be written to.
		 * @param value
		 *            --- value to be converted into bytes.
		 */
		public void write(Path.Entry entry, T value) throws Exception;		
	}

	public interface Registry {
		/**
		 * Get the content type associated with a given suffix.
		 * 
		 * @param suffix
		 * @return
		 */
		public Content.Type<?> get(String suffix);
	}
	
	public static class RegistryImpl implements Registry {
		private final HashMap<String,Content.Type> types = new HashMap<String,Content.Type>();
				
		public void register(String suffix, Content.Type<?> contentType) {
			types.put(suffix,contentType);
		}
		
		public Content.Type<?> get(String suffix) {
			return types.get(suffix);
		}
	}
}
