package wybs.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

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
public abstract class AbstractFolder {
	protected final Path.ID id;
	protected AbstractFolder[] children;
	protected Path.Entry[] contents;
	protected int nentries;
	
	public AbstractFolder(Path.ID id) {
		this.id = id;
	}
	
	public Path.ID id() {
		return id;
	}	
	
	public boolean contains(Path.Entry<?> e) throws IOException {		
		updateContents();		
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
	
	public boolean exists(ID id, Content.Type<?> ct) throws IOException{				
		updateContents();
		
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
	
	public <T> Path.Entry<T> get(ID id, Content.Type<T> ct) throws IOException{				
		updateContents();			
	
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
	
	public <T> void addMatchingEntries(Content.Filter<T> filter, ArrayList<Entry<T>> entries) throws IOException{			
		updateContents();
								
		for(int i=0;i!=nentries;++i) {
			Path.Entry e = contents[i];
			if(filter.matches(e.id(),e.contentType())) {
				entries.add(e);
			}
		}
	}
	
	public <T> void addMatchingIDs(Content.Filter<T> filter, HashSet<Path.ID> entries) throws IOException{			
		updateContents();
					
		for(int i=0;i!=nentries;++i) {
			Path.Entry e = contents[i];
			if(filter.matches(e.id(),e.contentType())) {
				entries.add(e.id());
			}
		}
	}	
	
	/**
	 * Add a newly created entry to this folder.
	 * 
	 * @param entry
	 */
	protected void insert(Path.Entry<?> entry) throws IOException{
		updateContents();
		
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
	protected abstract Path.Entry<?>[] contents() throws IOException;
	
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
