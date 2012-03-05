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

import java.util.*;
import wybs.lang.Path;

import wybs.lang.Content;

/**
 * Provides a simple implementation of <code>Path.Entry</code>. This caches
 * content in a field and employs a <code>modifies</code> bit to determine if
 * that content needs to be written to permanent storage.
 * 
 * @author David J. Pearce
 * 
 * @param <T>
 */
public abstract class AbstractEntry<T> implements Path.Entry<T> {
	protected final Path.ID id;		
	protected Content.Type<T> contentType;
	protected T contents = null;
	protected boolean modified = false;
	
	public AbstractEntry(Path.ID mid) {
		this.id = mid;
	}
	
	public Path.ID id() {
		return id;
	}
	
	public void touch() {
		this.modified = true;
	}
	
	public boolean isModified() {
		return modified;
	}
	
	public Content.Type<T> contentType() {
		return contentType;
	}
	
	public void refresh() throws Exception {
		if(!modified) {
			contents = null; // reset contents
		}
	}
	
	public void flush() throws Exception {
		if(modified && contents != null) {
			contentType.write(outputStream(), contents);
			this.modified = false;
		}
	}
	
	public T read() throws Exception {
		if (contents == null) {
			contents = contentType.read(this,inputStream());
		}
		return contents;
	}		
			
	public void write(T contents) throws Exception {
		this.modified = true;
		this.contents = contents; 
	}
	
	public void associate(Content.Type<T> contentType, T contents) {
		if(this.contentType != null) {
			throw new IllegalArgumentException("content type already associated with this entry");
		}
		this.contentType = contentType;
		this.contents = contents;
	}	
	
	public Set<Path.Entry<?>> dependents() {
		return Collections.EMPTY_SET;
	}
	public Set<Path.Entry<?>> dependencies() {
		return Collections.EMPTY_SET;
	}
}
