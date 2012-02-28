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

import java.util.*;

import wycore.lang.*;

/**
 * Provides a straightforward implementation of NameSpace. This encompasses one
 * or more "path roots" which are locations on the file system where named items
 * may be found. Such locations may be, for example, directories. However, they
 * may also be jar files, or even potentially network locations.
 * 
 * @author David J. Pearce
 * 
 */
public class StandardNameSpace implements NameSpace {
	
	/**
	 * The roots of named objects in the namespace described by this root. 
	 */	
	protected final ArrayList<Path.Root> roots;
	
	public StandardNameSpace(Collection<Path.Root> roots) {
		this.roots = new ArrayList<Path.Root>(roots);
	}
	
	// ======================================================================
	// Accessors
	// ======================================================================		
	
	public boolean exists(Path.ID id, Content.Type<?> ct) throws Exception {
		for(int i=0;i!=roots.size();++i) {
			if(roots.get(i).exists(id, ct)) {
				return true;
			}
		}
		return false;
	}
	
	public <T> Path.Entry<T> get(Path.ID id, Content.Type<T> ct) throws Exception {
		for(int i=0;i!=roots.size();++i) {
			Path.Entry<T> e = roots.get(i).get(id, ct);
			if(e != null) {
				return e;
			}			
		}
		return null;
	}
	
	public <T> ArrayList<Path.Entry<T>> get(Content.Filter<T> filter) throws Exception {
		ArrayList<Path.Entry<T>> r = new ArrayList<Path.Entry<T>>();
		for(int i=0;i!=roots.size();++i) {
			r.addAll(roots.get(i).get(filter));
		}
		return r;
	}
	
	public <T> HashSet<Path.ID> match(Content.Filter<T> filter) throws Exception {
		HashSet<Path.ID> r = new HashSet<Path.ID>();
		for(int i=0;i!=roots.size();++i) {
			r.addAll(roots.get(i).match(filter));
		}
		return r;
	}
	
	// ======================================================================
	// Mutators
	// ======================================================================		

	
	public void flush() throws Exception {
		for(int i=0;i!=roots.size();++i) {
			roots.get(i).flush();
		}
	}
	
	public void refresh() throws Exception {
		for(int i=0;i!=roots.size();++i) {
			roots.get(i).refresh();
		}
	}
}
