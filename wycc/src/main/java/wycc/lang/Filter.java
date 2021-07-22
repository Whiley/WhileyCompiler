package wycc.lang;

import java.util.Iterator;

import wycc.util.Trie;

/**
 * Represents a special form of path which can be used to match other paths.
 */
public class Filter implements Iterable<String>, Comparable<Filter> {
	public static final Filter STAR = fromString("*");
	public static final Filter STARSTAR = fromString("**");

	/**
	 * A default filter which recursively matches everything
	 */
	public static final Filter EVERYTHING = fromString("**/*");

	/**
	 * The internal trie used in this filter.
	 */
    protected final Trie trie;

    Filter(Trie trie) {
		this.trie = trie;
    }

    // =========================================================
    // Public Methods
    // =========================================================

    public int size() {
        return trie.size();
    }

    public String get(final int index) {
        return trie.get(index);
    }

    public String last() {
        return trie.last();
    }

    public Filter parent() {
        return new Filter(trie.parent());
    }

    public Filter subpath(int start, int end) {
        return new Filter(trie.subpath(start,end));
    }

    public Filter parent(int depth) {
    	return new Filter(trie.parent(depth));
    }

    @Override
	public Iterator<String> iterator() {
        return trie.iterator();
    }

    public boolean matches(Path path) {
        return trie.matches(path.trie);
    }

    @Override
	public int compareTo(Filter t2) {
       return trie.compareTo(t2.trie);
    }

    @Override
    public int hashCode() {
    	return trie.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if(o instanceof Filter) {
        	return trie == ((Filter)o).trie;
        } else {
        	return false;
        }
    }

    public Filter append(final Filter f) {
        return new Filter(trie.append(f.trie));
    }

    @Override
    public String toString() {
        return trie.toString();
    }

	public static Filter fromString(String str) {
		String[] components = str.split("/");
		for (int i = 0; i != components.length; ++i) {
			if (!isFilterString(components[i])) {
				throw new IllegalArgumentException("invalid filter string");
			}
		}
		return new Filter(Trie.fromString(str));
	}

	public static boolean isFilterString(String str) {
		// Sanity check path
		for (int i = 0; i != str.length(); ++i) {
			char c = str.charAt(i);
			if (i == 0 && isFilterStart(c)) {
				// continue
			} else if (i > 0 && isFilterMiddle(c)) {
				// continue
			} else {
				return false;
			}
		}
		return true;
	}

    public static boolean isFilterStart(char c) {
        return c == '*' || Path.isPathStart(c);
    }

    public static boolean isFilterMiddle(char c) {
        return c == '*' || Path.isPathMiddle(c);
    }
}
