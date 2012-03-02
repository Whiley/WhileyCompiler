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

package wybs.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wybs.lang.Content;
import wybs.lang.Path;
import wybs.lang.Path.Entry;
import wybs.lang.Path.ID;
import wybs.lang.Path.Root;

/**
 * Provides a simple implementation of <code>Path.Root</code>. This maintains a
 * cache all entries contained in the root.
 * 
 * @author David J. Pearce
 * 
 */
public abstract class AbstractRoot implements Root {
	protected final Content.Registry contentTypes;
	protected Path.Entry<?>[] contents = null;
	protected int nentries = 0;
	
	public AbstractRoot(Content.Registry contentTypes) {
		this.contentTypes = contentTypes;
	}
	
	public int size() throws Exception{
		updateCache();
		return nentries;
	}
			
	public Path.Entry<?> get(int index) throws Exception{
		updateCache();
		return contents[index];
	}		
	
	public boolean contains(Path.Entry<?> e) {
		Path.ID id = e.id();
		int idx = binarySearch(contents,nentries,id);
		if(idx >= 0) {
			Path.Entry<?> entry = contents[idx];
			do {
				if (entry == e) {
					return true;
				}
			} while (++idx < nentries
					&& (entry = contents[idx]).id().equals(id));
		}
		return false;
	}
	
	public boolean exists(ID id, Content.Type<?> ct) throws Exception{		
		updateCache();
		
		int idx = binarySearch(contents,nentries,id);
		if(idx >= 0) {
			Path.Entry<?> entry = contents[idx];
			do {
				if (entry.contentType() == ct) {
					return true;
				}
			} while (++idx < nentries
					&& (entry = contents[idx]).id().equals(id));
		}
		return false;
	}
	
	public <T> Path.Entry<T> get(ID id, Content.Type<T> ct) throws Exception{		
		updateCache();			
	
		int idx = binarySearch(contents,nentries,id);
		if(idx >= 0) {
			Path.Entry entry = contents[idx];
			do {
				if (entry.contentType() == ct) {
					return entry;
				}
			} while (++idx < nentries
					&& (entry = contents[idx]).id().equals(id));
		}
		return null;
	}
	
	public <T> List<Entry<T>> get(Content.Filter<T> filter) throws Exception{		
		updateCache();
			
		ArrayList<Entry<T>> entries = new ArrayList<Entry<T>>();			
		for(int i=0;i!=nentries;++i) {
			Path.Entry e = contents[i];
			if(filter.matches(e.id(),e.contentType())) {
				entries.add(e);
			}
		}
		return entries;
	}
	
	public <T> Set<Path.ID> match(Content.Filter<T> filter) throws Exception{		
		updateCache();
					
		HashSet<Path.ID> entries = new HashSet<Path.ID>();			
		for(int i=0;i!=nentries;++i) {
			Path.Entry e = contents[i];
			if(filter.matches(e.id(),e.contentType())) {
				entries.add(e.id());
			}
		}
		
		return entries;
	}	
	
	public void refresh() throws Exception{
		updateCache();
	}
	
	public void flush() throws Exception{
		for(int i=0;i!=nentries;++i) {
			contents[i].flush();			
		}
	}
	
	/**
	 * Add a newly created entry to this path.
	 * 
	 * @param entry
	 */
	protected void insert(Path.Entry<?> entry) throws Exception{
		updateCache();
		
		Path.ID id = entry.id();
		int index = binarySearch(contents,nentries,id);
		
		if(index < 0) {
			index = -index - 1; // calculate insertion point
		} else {
			// indicates already an entry with a different content type
		}

		if ((nentries + 1) < contents.length) {
			System.arraycopy(contents, index, contents, index + 1, nentries
					- index);			
		} else {			
			Path.Entry[] tmp = new Path.Entry[(nentries+1) * 2];
			System.arraycopy(contents, 0, tmp, 0, index);
			System.arraycopy(contents, index, tmp, index + 1, nentries - index);
			contents = tmp;			
		}
		
		contents[index] = entry;
		nentries++;
	}
	
	private final void updateCache() throws Exception{
		if(contents == null) {
			contents = contents();			
			nentries = contents.length;
			Arrays.sort(contents,entryComparator);
		}
	}
	
	/**
	 * Extract all entries from the given type.
	 */
	protected abstract Path.Entry<?>[] contents() throws Exception;
	
	private static final int binarySearch(final Path.Entry<?>[] children, int nchildren, final Path.ID key) {
		int low = 0;
        int high = nchildren-1;
            
        while (low <= high) {
            int mid = (low + high) >> 1;
            int c = children[mid].id().compareTo(key);
                
            if (c < 0) {
                low = mid + 1; 
            } else if (c > 0) {
                high = mid - 1;
            } else {
            	// found a batch, locate start point
            	mid = mid - 1;
				while (mid >= 0 && children[mid].id().compareTo(key) == 0) {
					mid = mid - 1;
				}
                return mid + 1;
            }
        }
        return -(low + 1);
	}
	
	private static final Comparator<Path.Entry> entryComparator = new Comparator<Path.Entry>() {
		public int compare(Path.Entry e1, Path.Entry e2) {
			return e1.id().compareTo(e2.id());
		}
	};
}