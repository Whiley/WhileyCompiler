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

import java.util.Arrays;
import java.util.Iterator;

import wycc.util.Trie;

/**
 * <p>
 * Provides a standard implementation of the Path.ID and Path.Filter interfaces.
 * It employs the <i>flyweight pattern</i> to ensure every ID can only ever
 * correspond to a single instanceof of Trie. That, it ensures that any two
 * instances which represent the same Path.ID are, in fact, the same instance.
 * </p>
 * <p>
 * As the name suggests, the implementation is based around a n-ary tree where
 * each node stores a component of a path. There is a single master root of all
 * possible paths and all possible instances of <code>Trie</code> extend this.
 * </p>
 * <p>
 * <b>NOTE:</b> the implementation does not currently attempt to garbage collect
 * tries. Therefore, the memory consumed is proportional to the total number of
 * distinct tries created throughout the program's life
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class Path extends Filter {
	public static final Path ROOT = new Path(Trie.ROOT);

	private Path(Trie t) {
		super(t);
	}

	// =========================================================
	// Public Methods
	// =========================================================

	@Override
	public Path parent() {
		Trie p = trie.parent();
		//
		return (p == null) ? null : new Path(p);
	}

	@Override
	public Path subpath(int start, int end) {
		return new Path(trie.subpath(start, end));
	}

	@Override
	public Path parent(int depth) {
		Trie p = trie.parent(depth);
		//
		return (p == null) ? null : new Path(p);
	}

	public Path append(final String component) {
		return new Path(trie.append(component));
	}

	public Path append(final Path p) {
		return new Path(trie.append(p.trie));
	}

	/**
	 * Construct a Path from a string, where '/' is the separator. s
	 *
	 * @param str
	 * @return
	 */
	public static Path fromString(String str) {
		if(!isPathString(str)) {
			throw new IllegalArgumentException("invalid path string (\"" + str + "\")");
		}
		return new Path(Trie.fromString(str));
	}

	/**
	 * Check whether or not a given string is a valid path string.
	 *
	 * @param str
	 * @return
	 */
	public static boolean isPathString(String str) {
		String[] components = str.split("/");
		for (int i = 0; i != components.length; ++i) {
			if (!isPathComponent(components[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Construct a path from an array of components.
	 *
	 * @param str
	 * @return
	 */
	public static Path fromStrings(String... str) {
		Trie t = Trie.ROOT;
		for (int i = 0; i != str.length; ++i) {
			String ith = str[i];
			if(!isPathComponent(ith)) {
				throw new IllegalArgumentException("invalid path component (\"" + ith + "\")");
			}
			t = t.append(ith);
		}
		return new Path(t);
	}

	/**
	 * Check whether or not a given string is a valid path component.
	 *
	 * @param str
	 * @return
	 */
	public static boolean isPathComponent(String str) {
		// Sanity check path
		for (int i = 0; i != str.length(); ++i) {
			char c = str.charAt(i);
			if (i == 0 && isPathStart(c)) {
				// continue
			} else if (i > 0 && isPathMiddle(c)) {
				// continue
			} else {
				return false;
			}
		}
		return true;
	}

	public static boolean isPathStart(char c) {
		return Character.isLetter(c) || c == '.' || c == '_' || c == '-';
	}

	public static boolean isPathMiddle(char c) {
		return isPathStart(c) || Character.isDigit(c);
	}
}
