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
package wycc.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;

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
public final class Trie implements Iterable<String>, Comparable<Trie> {

	// =========================================================
	// Private Constants
	// =========================================================

	private static final Trie[] ONE_CHILD = new Trie[1];

	// =========================================================
	// Public Constants
	// =========================================================

	public static final Trie ROOT = new Trie(null,"");
	public static final Trie STAR = fromString("*");
	public static final Trie STARSTAR = fromString("**");
	/**
	 * A default filter which recursively matches everything
	 */
	public static final Trie EVERYTHING = Trie.fromString("**/*");

	// =========================================================
	// Private State
	// =========================================================

	private final Trie parent;
	private final String component;
	private final int depth;
	private final boolean isConcrete;
	private Trie[] children;
	private int nchildren;

	// =========================================================
	// Public Methods
	// =========================================================


	Trie(final Trie parent, final String component) {
		this.parent = parent;
		this.component = component;
		if(parent != null) {
			this.depth = parent.depth + 1;
		} else {
			this.depth = -1;
		}
		this.children = ONE_CHILD;
		this.nchildren = 0;
		this.isConcrete = (parent == null || parent.isConcrete)
				&& !component.contains("*");
	}

	public int size() {
		return depth + 1;
	}

	public boolean isConcrete() {
		return isConcrete;
	}

	public String get(final int index) {
		if(index == depth) {
			return component;
		} else if(index > depth) {
			throw new IllegalArgumentException("index out-of-bounds");
		} else {
			return parent.get(index);
		}
	}

	public Path toPath() {
		if(parent == null) {
			return Paths.get(component);
		} else {
			return parent.toPath().resolve(component);
		}
	}

	public boolean matches(Trie id) {
		return match(id, 0, 0, false);
	}

	public String last() {
		return component;
	}

	public Trie parent() {
		return parent;
	}

	public Trie subpath(int start, int end) {
		Trie id = Trie.ROOT;
		for(int i=start;i!=end;++i) {
			// TODO: this could be made more efficient
			id = id.append(get(i));
		}
		return id;
	}
	public Trie parent(int depth) {
		if(this.depth < depth) {
			return this;
		} else {
			return parent.parent(depth);
		}
	}

	@Override
	public Iterator<String> iterator() {
		return new InternalIterator(this);
	}

	@Override
	public int compareTo(final Trie o) {
		if(o instanceof Trie) {
			// We can be efficient here
			Trie t1 = this;
			Trie t2 = o;
			while(t1.depth > t2.depth) {
				t1 = t1.parent;
			}
			while(t2.depth > t1.depth) {
				t2 = t2.parent;
			}
			while(t1.parent != t2.parent) {
				t1 = t1.parent;
				t2 = t2.parent;
			}
			int c = t1.component.compareTo(t2.component);
			if(c != 0) { return c; }
			// assert t2 == o
			int tDepth = t2.depth;
			if(depth < tDepth) {
				return -1;
			} else if(depth > tDepth) {
				return 1;
			} else {
				return 0;
			}
		} else {
			throw new IllegalArgumentException("Attempting to compare Trie with some other Path.ID");
		}
	}

	@Override
	public int hashCode() {
		int hc = component.hashCode();
		if(parent != null) {
			hc = hc ^ parent.hashCode();
		}
		return hc;
	}

	@Override
	public boolean equals(final Object o) {
		return this == o;
	}

	public Trie append(final String component) {
		int index = binarySearch(children, nchildren, component);
		if(index >= 0) {
			return children[index];
		}

		Trie nt = new Trie(this,component);
		index = -index - 1; // calculate insertion point

		if((nchildren+1) < children.length) {
			System.arraycopy(children, index, children, index+1, nchildren - index);
		} else {
			Trie[] tmp = new Trie[children.length * 2];
			System.arraycopy(children, 0, tmp, 0, index);
			System.arraycopy(children, index, tmp, index+1, nchildren - index);
			children = tmp;
		}

		children[index] = nt;
		nchildren++;
		return nt;
	}

	public Trie append(final Trie t) {
		Trie r = this;
		// TODO: this could be more efficient
		for (int i = 0; i != t.size(); ++i) {
			r = r.append(t.get(i));
		}
		return r;
	}

	@Override
	public String toString() {
		if(parent == null || parent == ROOT) {
			return component;
		} else {
			return parent.toString() + "/" + component;
		}
	}

	public String toNativeString() {
		if(parent == null || parent == ROOT) {
			return component;
		} else {
			return parent.toNativeString() + File.separatorChar + component;
		}
	}

	/**
	 * Construct a Trie from a string, where '/' is the separator.
	 * s
	 * @param str
	 * @return
	 */
	public static Trie fromString(String str) {
		String[] components = str.split("/");
		Trie r = ROOT;
		for(int i=0;i!=components.length;++i) {
			r = r.append(components[i]);
		}
		return r;
	}

	public static Trie fromStrings(String... components) {
		Trie r = ROOT;
		for(int i=0;i!=components.length;++i) {
			r = r.append(components[i]);
		}
		return r;
	}

	// =========================================================
	// Private Methods
	// =========================================================

	private boolean match(Trie t, int idIndex, int myIndex, boolean submatch) {
		int mySize = depth + 1;
		if (myIndex == mySize && idIndex == t.size()) {
			return true;
		} else if(idIndex == t.size()) {
			return submatch;
		} else if (myIndex == mySize) {
			return false;
		}

		String myComponent = get(myIndex);
		if (myComponent.equals("*")) {
			return match(t, idIndex + 1, myIndex + 1, submatch);
		} else if (myComponent.equals("**")) {
			myIndex++;
			for (int i = idIndex; i <= t.size(); ++i) {
				if (match(t, i, myIndex, submatch)) {
					return true;
				}
			}
			return false;
		} else {
			return myComponent.equals(t.get(idIndex))
					&& match(t, idIndex + 1, myIndex + 1, submatch);
		}
	}

	private static final int binarySearch(final Trie[] children, final int nchildren, final String key) {
		int low = 0;
        int high = nchildren-1;

        while (low <= high) {
            int mid = (low + high) >> 1;
            int c = children[mid].component.compareTo(key);

            if (c < 0) {
                low = mid + 1;
            } else if (c > 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return -(low + 1);
	}

	private static final class InternalIterator implements Iterator<String> {
		private final Trie id;
		private int index;

		public InternalIterator(Trie id) {
			this.id = id;
			this.index = 0;
		}

		@Override
		public boolean hasNext() {
			return index <= id.depth;
		}

		@Override
		public String next() {
			return id.get(index++);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	public static void main(String[] args) {
		Trie t1 = ROOT.append("Hello");
		Trie t2 = t1.append("*");
		Trie t3 = t2.append("Blah");
		System.out.println("T1: " + t3.parent(1));
		System.out.println("T2: " + t3.parent(2));
		System.out.println("T3: " + t3.parent(3));
		Trie[] ids = {ROOT,t2,t3,t1};
		for(Trie id : ids) {
			System.out.println(id + "(" + id.size() + ")");
		}
		Arrays.sort(ids);
		for(Trie id : ids) {
			System.out.println(id);
		}

		for(String c : t3) {
			System.out.println(c);
		}
	}
}
