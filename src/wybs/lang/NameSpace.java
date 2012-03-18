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

package wybs.lang;

import java.util.List;
import java.util.Set;

import wybs.lang.Path.Filter;
import wybs.lang.Path.ID;

/**
 * <p>
 * A NameSpace is on the core features underlying the design of the Whiley
 * compiler system. In any given build system, the challenge is always to map
 * named objects identified in source files to actual files on the file system.
 * The NameSpace is responsible for exactly that. 
 * </p>
 * 
 * <p>
 * One of the key responsibilities for a NameSpace is to map path items to
 * physical locations. There is an implicit assumption here that every path item
 * corresponds to at most one physical location.
 * </p>
 *  
 * 
 * @author David J. Pearce
 * 
 */
public interface NameSpace {
		
	/**
	 * Check whether or not a given entry and content-type is contained in this root.
	 * 
	 * @throws Exception
	 *             --- in case of some failure (e.g. IOException).
	 */
	public boolean exists(ID id, Content.Type<?> ct) throws Exception;
	
	/**
	 * Get the entry corresponding to a given ID and content type. If no
	 * such entry exists, return null.
	 * 
	 * @param id
	 *            --- id of module to lookup.
	 * @return
	 * @throws ResolveError
	 *             if id is not found.
	 * @throws Exception
	 *             --- in case of some failure (e.g. IOException).
	 */
	public <T> Path.Entry<T> get(ID id, Content.Type<T> ct) throws Exception;
	
	/**
	 * Get all objects matching a given content filter stored in this root.
	 * In the case of no matches, an empty list is returned.
	 * 
	 * @throws Exception
	 *             --- in case of some failure (e.g. IOException).
	 * 
	 * @param ct
	 * @return
	 */
	public <T> List<Path.Entry<T>> get(Content.Filter<T> ct) throws Exception;
	
	/**
	 * Identify all entries matching a given content filter stored in this
	 * root. In the case of no matches, an empty set is returned.
	 * 
	 * @throws Exception
	 *             --- in case of some failure (e.g. IOException).
	 * 
	 * @param filter
	 *            --- filter to match entries with.
	 * @return
	 */
	public <T> Set<Path.ID> match(Content.Filter<T> filter) throws Exception;
	
	/**
	 * Force all roots to flush their items to permanent storage (where
	 * appropriate). This is essential as, at any given moment, path items may
	 * only be stored in memory. We must flush them to disk in order to preserve
	 * any changes that were made.
	 */
	public void flush() throws Exception;
	
	/**
	 * Force all roots to refresh their items from permanent storage (where
	 * appropriate). For items which has been modified, this operation will have
	 * no effect (i.e. the new contents are retained).
	 */
	public void refresh() throws Exception;	
}
