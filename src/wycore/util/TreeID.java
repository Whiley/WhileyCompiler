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

package wycore.util;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

import wycore.lang.Path;

/**
 * <p>
 * An TreeID provides a standard implementation of the Path.ID interface. It
 * employs the <i>flyweight pattern</i> to ensure every ID can only ever
 * correspond to a single instanceof of TreeID. That, it ensures that any two
 * instances which represent the same Path.ID are, in fact, the same instance.
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public final class TreeID implements Path.ID {

	private static final TreeID[] ONE_CHILD = new TreeID[1];
	
	// =========================================================
	// Public Constants
	// =========================================================

	public static final TreeID ROOT = new TreeID(null,"");
		
	// =========================================================
	// Private State
	// =========================================================

	private final TreeID parent;
	private final String component;
	private final int depth;
	private TreeID[] children;
	private int nchildren;

	// =========================================================
	// Public Methods
	// =========================================================

	
	TreeID(final TreeID parent, final String component) {
		this.parent = parent;
		this.component = component;
		if(parent != null) {
			this.depth = parent.depth + 1;
		} else {
			this.depth = -1;
		}
		this.children = ONE_CHILD;
		this.nchildren = 0;
	}
	
	public int size() {
		return depth + 1;
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
	
	public String last() {
		return component;
	}
	
	public TreeID parent() {
		return parent;
	}
	
	public Iterator<String> iterator() {
		return new InternalIterator(this);
	}
	
	public int compareTo(final Path.ID o) {
		if(o instanceof TreeID) {
			// We can be efficient here
			TreeID t1 = this;
			TreeID t2 = (TreeID) o;
			while(t1.depth > t2.depth) {
				t1 = t1.parent;
			}
			while(t2.depth > t1.depth) {
				t2 = t2.parent;
			}
			while(t1 != t2) {
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
			throw new IllegalArgumentException("Attempting to compare TreeID with some other Path.ID");
		}
	}
	
	public int hashCode() {
		int hc = component.hashCode();
		if(parent != null) {
			hc = hc ^ parent.hashCode();
		}
		return hc;
	}
	
	public boolean equals(final Object o) {
		return this == o;
	}
	
	public TreeID append(final String component) {
		int index = binarySearch(children, nchildren, component);
		if(index >= 0) {
			return children[index];
		} 
		
		TreeID nt = new TreeID(this,component);
		index = -index - 1; // calculate insertion point

		if((nchildren+1) < children.length) {
			System.arraycopy(children, index, children, index+1, nchildren - index);
		} else {
			TreeID[] tmp = new TreeID[children.length * 2];
			System.arraycopy(children, 0, tmp, 0, index);
			System.arraycopy(children, index, tmp, index+1, nchildren - index);	
			children = tmp;
		}
		
		children[index] = nt;
		nchildren++;
		return nt;
	}
	
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
			return parent.toString() + File.separatorChar + component;
		}
	}
	
	/**
	 * Construct a TreeID from a string, where '/' is the separator.
	 * s
	 * @param str
	 * @return
	 */
	public static TreeID fromString(String str) {
		String[] components = str.split("/");
		TreeID r = ROOT;
		for(int i=0;i!=components.length;++i) {
			r = r.append(components[i]);
		}
		return r;
	}
	
	private static final int binarySearch(final TreeID[] children, final int nchildren, final String key) {
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
		private final TreeID id;
		private int index;
		
		public InternalIterator(TreeID id) {
			this.id = id;
			this.index = 0;
		}
		
		public boolean hasNext() {
			return index <= id.depth;
		}
		
		public String next() {
			return id.get(index++);
		}
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	public static void main(String[] args) {
		TreeID t1 = ROOT.append("Hello");
		TreeID t2 = t1.append("World");
		TreeID t3 = t1.append("Blah");
		TreeID[] ids = {ROOT,t2,t3,t1};
		for(TreeID id : ids) {
			System.out.println(id + "(" + id.size() + ")");
		}
		Arrays.sort(ids);
		for(TreeID id : ids) {
			System.out.println(id);
		}
		
		for(String c : t3) {
			System.out.println(c);
		}
	}
}
