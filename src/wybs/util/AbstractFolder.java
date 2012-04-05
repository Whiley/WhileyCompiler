// Copyright (c) 2012, David J. Pearce (djp@ecs.vuw.ac.nz)
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

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import wybs.lang.*;
import wybs.lang.Path.Entry;
import wybs.lang.Path.ID;

/**
 * An abstract folder contains other folders, and path entries. As such, it
 * cannot be considered a concrete entry which can be read and written in the
 * normal manner. Rather, it provides access to entries. In a physical file
 * system, a folder would correspond to a directory.
 * 
 * @author David J. Pearce
 * 
 */
public abstract class AbstractFolder implements Path.Folder {
	protected final Path.ID id;
	private Path.Item[] contents;
	private int nentries;
	
	/**
	 * Construct an Abstract Folder representing a given ID (taken relative to
	 * the enclosing root).
	 * 
	 * @param id
	 */
	public AbstractFolder(Path.ID id) {
		this.id = id;
	}
	
	@Override
	public Path.ID id() {
		return id;
	}	
	
	@Override
	public boolean contains(Path.Entry<?> e) throws IOException {
		updateContents();	
		Path.ID eid = e.id();
		boolean contained;
		
		if(id == eid.parent()) {	
			// The requested entry is contained in this folder. Therefore, we
			// need to search for it. 
			contained = true;
		} else if(id == eid.subpath(0,id.size())) {
			// This folder is a parent of the requested entry. Therefore, we
			// need to looking for a matching folder entry. If we find one, then
			// we ask it for the requested entry.
			eid = eid.subpath(0,id.size()+1);
			contained = false;
		} else {
			return false;
		}
		
		int idx = binarySearch(contents,nentries,eid);			
		if(idx >= 0) {
			// At this point, we've found a matching index for the given ID.
			// However, there maybe multiple matching IDs (e.g. with different
			// content types). Therefore, we need to check them all to see if
			// they match the requested entry.
			Path.Item item = contents[idx];
			do {
				if (item == e) {
					return true;
				} else if(!contained && item instanceof Path.Folder) {
					Path.Folder folder = (Path.Folder) item;
					return folder.contains(e);
				}
			} while (++idx < nentries
					&& (item = contents[idx]).id().equals(eid));
		}
			 					
		// no dice
		return false;
	}
	
	@Override
	public boolean exists(ID id, Content.Type<?> ct) throws IOException{				
		return get(id,ct) != null;
	}
	
	@Override
	public <T> Path.Entry<T> get(ID eid, Content.Type<T> ct) throws IOException{				
		updateContents();				
		
		ID tid = id.append(eid.get(0));		
				
		int idx = binarySearch(contents,nentries,tid);
		if(idx >= 0) {
			// At this point, we've found a matching index for the given ID.
			// However, there maybe multiple matching IDs with different
			// content types. Therefore, we need to check them all to see if
			// they match the requested entry.
			Path.Item item = contents[idx];
			do {
				if(item instanceof Entry && eid.size() == 1) {
					// In this case, we're looking for and have found an exact
					// item.
					Entry entry = (Entry) item;
					if (entry.contentType() == ct) {
						return entry;
					}
				} else if (item instanceof Path.Folder && eid.size() > 1) {
					// In this case, the ID is indicates the item is not
					// contained in this folder.
					Path.Folder folder = (Path.Folder) item;
					return folder.get(eid.subpath(1,eid.size()), ct);
				}
			} while (++idx < nentries
					&& (item = contents[idx]).id().equals(eid));
		}
			
		// no dice
		return null;
	}
	
	@Override
	public <T> void getAll(Content.Filter<T> filter, List<Entry<T>> entries) throws IOException{			
		updateContents();
		
		// It would be nice to further optimise this loop. The key issue is that,
		// at some point, we might know the filter could never match. In which
		// case, we want to stop the recursion early, rather than exploring a
		// potentially largel subtree.
		
		for(int i=0;i!=nentries;++i) {
			Path.Item item = contents[i];
			if(item instanceof Entry) {
				Entry entry = (Entry) item;
				if(filter.matches(entry.id(),entry.contentType())) {
					entries.add(entry);
				}
			} else if (item instanceof Path.Folder
					&& filter.matchesSubpath(item.id())) {
				Path.Folder folder = (Path.Folder) item;
				folder.getAll(filter, entries);
			}
		}
	}
	
	@Override
	public <T> void getAll(Content.Filter<T> filter, Set<Path.ID> entries) throws IOException{			
		updateContents();
		
		// It would be nice to further optimise this loop. The key issue is that,
		// at some point, we might know the filter could never match. In which
		// case, we want to stop the recursion early, rather than exploring a
		// potentially largel subtree.
		
		for(int i=0;i!=nentries;++i) {
			Path.Item item = contents[i];
			if (item instanceof Entry) {
				Entry entry = (Entry) item;
				if (filter.matches(entry.id(), entry.contentType())) {
					entries.add(entry.id());
				}
			} else if (item instanceof Path.Folder
					&& filter.matchesSubpath(item.id())) {
				Path.Folder folder = (Path.Folder) item;
				folder.getAll(filter, entries);
			}
		}
	}		
	
	@Override
	public void refresh() {
		contents = null;
	}
	
	@Override
	public void flush() throws IOException {
		if(contents != null) {
			for(int i=0;i!=nentries;++i) {
				contents[i].flush();
			}
		}
	}
	
	protected Path.Folder getFolder(String name) throws IOException {
		updateContents();				
		
		ID tid = id.append(name);		
		
		int idx = binarySearch(contents, nentries, tid);
		if (idx >= 0) {
			// At this point, we've found a matching index for the given ID.
			// However, there maybe multiple matching IDs with different
			// content types. Therefore, we need to check them all to see if
			// they match the requested entry.
			Path.Item item = contents[idx];
			do {
				if (item instanceof Path.Folder) {
					// In this case, the ID is indicates the item is not
					// contained in this folder.
					return (Path.Folder) item;
				}
			} while (++idx < nentries
					&& (item = contents[idx]).id().equals(tid));
		}
			
		// no dice
		return null;
	}
	
	/**
	 * Insert a newly created item into this folder. Observe we assume
	 * <code>entry.id().parent() == id</code>.
	 * 
	 * @param item
	 */
	protected void insert(Path.Item item) throws IOException {
		if (item.id().parent() != id) {
			throw new IllegalArgumentException(
					"Cannot insert with incorrect Path.Item (" + item.id() + ") into AbstractFolder (" + id + ")");
		}
		updateContents();
		Path.ID id = item.id();
		int index = binarySearch(contents, nentries, id);

		if (index < 0) {
			index = -index - 1; // calculate insertion point
		} else {
			// indicates already an entry with a different content type
		}

		if ((nentries + 1) < contents.length) {
			System.arraycopy(contents, index, contents, index + 1, nentries
					- index);
		} else {
			Path.Item[] tmp = new Path.Item[(nentries + 1) * 2];
			System.arraycopy(contents, 0, tmp, 0, index);
			System.arraycopy(contents, index, tmp, index + 1, nentries - index);
			contents = tmp;
		}

		contents[index] = item;
		nentries++;
	}
	
	private final void updateContents() throws IOException{
		if(contents == null) {
			contents = contents();			
			nentries = contents.length;
			Arrays.sort(contents,entryComparator);
		}
	}
	
	/**
	 * Extract all entries from the given folder.
	 */
	protected abstract Path.Item[] contents() throws IOException;
	
	private static final int binarySearch(final Path.Item[] children, int nchildren, final Path.ID key) {
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
	
	private static final Comparator<Path.Item> entryComparator = new Comparator<Path.Item>() {
		public int compare(Path.Item e1, Path.Item e2) {
			return e1.id().compareTo(e2.id());
		}
	};
}
