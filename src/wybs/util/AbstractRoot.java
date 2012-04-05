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

import java.io.IOException;
import java.util.ArrayList;
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
	protected final AbstractFolder root;
	
	public AbstractRoot(Content.Registry contentTypes) {
		this.contentTypes = contentTypes;
		this.root = root();
	}
			
	public boolean contains(Path.Entry<?> e) throws IOException {
		return root.contains(e);
	}
	
	public boolean exists(ID id, Content.Type<?> ct) throws IOException{
		return root.exists(id,ct);
	}
	
	public <T> Path.Entry<T> get(ID id, Content.Type<T> ct) throws IOException{		
		Path.Entry<T> e = root.get(id,ct);
		return e;
	}
	
	public <T> List<Entry<T>> get(Content.Filter<T> filter) throws IOException{	
		ArrayList<Entry<T>> entries = new ArrayList<Entry<T>>();
		root.getAll(filter, entries);
		return entries;
	}
	
	public <T> Set<Path.ID> match(Content.Filter<T> filter) throws IOException{	
		HashSet<Path.ID> ids = new HashSet<Path.ID>();
		root.getAll(filter, ids);
		return ids;
	}	
	
	@Override
	public <T> Path.Entry<T> create(Path.ID id, Content.Type<T> ct,
			Path.Entry<?>... sources) throws IOException {
		return root.create(id,ct,sources);	
	}
	
	public void refresh() throws IOException{
		root.refresh();
	}
	
	public void flush() throws IOException{
		root.flush();
	}
	
	/**
	 * Get the root folder for this abstract root. Note that this should be
	 * loaded from scratch, and not cached in any way. This ensures that
	 * invoking AbstractRoot.refresh() does indeed refresh entries.
	 * 
	 * @return
	 */
	protected abstract AbstractFolder root();
}