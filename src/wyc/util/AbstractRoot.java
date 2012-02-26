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

package wyc.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wyc.lang.Content;
import wyc.lang.Path;
import wyc.lang.Path.Entry;
import wyc.lang.Path.ID;
import wyc.lang.Path.Root;

public abstract class AbstractRoot implements Root {
	protected final Content.Registry contentTypes;
	private Path.Entry<?>[] contents = null;
	
	public AbstractRoot(Content.Registry contentTypes) {
		this.contentTypes = contentTypes;
	}
	
	public boolean exists(ID id, Content.Type<?> ct) throws Exception {
		if(contents == null) {
			contents = contents();
		}
		for(Path.Entry e : contents) {
			if (e.id().equals(id) && e.contentType() == ct) {
				return true;
			}
		}
		return false;
	}
	
	public <T> Path.Entry<T> get(ID id, Content.Type<T> ct) throws Exception {
		if(contents == null) {
			contents = contents();
		}
		for (Path.Entry<?> e : contents) {
			if (e.id().equals(id) && e.contentType() == ct) {
				return (Path.Entry<T>) e;
			}
		}
		// this is what we're supposed to do!
		throw new ResolveError("unable to locate " + id);
	}
	
	public <T> List<Entry<T>> get(Content.Filter<T> filter) throws Exception {
		if(contents == null) {
			contents = contents();
		}	
		ArrayList<Entry<T>> entries = new ArrayList<Entry<T>>();			
		for(Path.Entry<?> e : contents) {
			Path.Entry<T> r = filter.match(e);
			if(r != null) {
				entries.add(r);
			}
		}
		return entries;
	}
	
	public <T> Set<Path.ID> match(Content.Filter<T> filter) throws Exception {
		if(contents == null) {
			contents = contents();
		}	
		HashSet<Path.ID> entries = new HashSet<Path.ID>();			
		for(Path.Entry<?> e : contents) {
			Path.Entry<T> r = filter.match(e);
			if(r != null) {
				entries.add(r.id());
			}
		}
		return entries;
	}
	
	public void refresh() throws Exception {
		contents = contents();
	}
	
	/**
	 * Extract all entries from the given type.
	 */
	protected abstract Path.Entry<?>[] contents() throws Exception;
}