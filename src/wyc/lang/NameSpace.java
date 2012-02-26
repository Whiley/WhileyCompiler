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

package wyc.lang;

import wyc.lang.Path.ID;

/**
 * <p>
 * A NameSpace is on the core features underlying the design of the Whiley
 * compiler system. In any given build system, the challenge is always to map
 * named objects identified in source files to actual files on the file system.
 * The NameSpace is responsible for exactly that. It encompasses one or more
 * "path roots" which are locations on the file system where named items may be
 * found. Such locations may be, for example, directories. However, they may
 * also be jar files, or even potentially network locations.
 * </p>
 * 
 * <p>
 * One of the key responsibilities for a NameSpace is to map path items to
 * physical locations. There is an implicit assumption here that every path item
 * corresponds to at most one physical location.
 * </p>
 * 
 * <p>
 * Another important responsibility is to control the creation of Path ID's.
 * Every Path ID used within a given NameSpace instance should have been
 * constructed via that NameSpace's <code>id()</code> function. This helps
 * integrate into systems (e.g. Eclipse) which have their own notion of a path
 * identified (e.g. IPath).
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public interface NameSpace extends Path.Root {
	
	/**
	 * Determine the physical location for an item with a given content type at
	 * a given path.
	 * 
	 * @param id
	 * @return
	 */
	public Path.Root locate(Path.ID id, Content.Type<?> ct);
	
	/**
	 * Create an entry of a given content type at a given path. The physical
	 * location of the entry is first determined using the locate function. If
	 * the entry already exists, then it is just returned.
	 * 
	 * @param id
	 * @param ct
	 * @return
	 * @throws Exception
	 */
	public <T> Path.Entry<T> create(ID id, Content.Type<T> ct) throws Exception;
	
	/**
	 * Force all roots to flush their items to permanent storage (where
	 * appropriate). This is essential as, at any given moment, path items may
	 * only be stored in memory. We must flush them to disk in order to preserve
	 * any changes that were made.
	 */
	public void flush();
	
	/**
	 * Force all roots to refresh their items from permanent storage (where
	 * appropriate). For items which has been modified, this operation will have
	 * no effect (i.e. the new contents are retained).
	 */
	public void refresh();
	
	/**
	 * Create a Path ID from a string, where '/' separates path components.
	 * 
	 * @param s
	 * @return
	 */
	public Path.ID id(String s);
}
