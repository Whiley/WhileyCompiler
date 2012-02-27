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

import wyc.lang.Content;
import wyc.lang.Path.Entry;
import wyc.lang.Path.ID;

public abstract class AbstractEntry<T> implements Entry<T> {
	private final ID id;		
	private Content.Type<T> contentType;
	private T contents = null;
	private boolean modified = false;
	
	public AbstractEntry(ID mid) {
		this.id = mid;
	}
	
	public ID id() {
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
	
	public T contents() throws Exception {
		if (contents == null) {
			contents = contentType.read(this);
		}
		return contents;
	}		
			
	public void setContents(T contents) throws Exception {
		this.contents = contents; 
	}
	
	public void associate(Content.Type<T> contentType) {
		if(this.contentType != null) {
			throw new IllegalArgumentException("content type already associated with this entry");
		}
		this.contentType = contentType;
	}
}

