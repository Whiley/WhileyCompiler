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

package wyil.util.path;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import wyil.lang.ModuleID;
import wyil.lang.PkgID;

public class Path {
	
	public interface Root {		
		
		/**
		 * Check whether or not a given package is contained.
		 */
		public boolean exists(PkgID pid) throws Exception;
		
		/**
		 * Lookup a given module.
		 * 
		 * @param mid
		 *            --- id of module to lookup.
		 * @return
		 * @throws IOException
		 */
		public Entry lookup(ModuleID mid) throws Exception;
		
		/**
		 * List contents of a given package.
		 * 
		 * @param pid
		 * @return
		 * @throws IOException
		 */
		public Collection<Entry> list(PkgID pid) throws Exception;
	}
	
	/**
	 * A path entry represents an item reachable from root on the WHILEYPATH
	 * which corresponds to a Whiley Module.
	 * 
	 * @author djp
	 * 
	 */
	public interface Entry {
		public ModuleID id();

		/**
		 * Return the suffix of the item in question. This is necessary to
		 * determine how we will process this item.
		 * 
		 * @return
		 */
		public String suffix();
		
		/**
		 * Return a string indicating the location of this entry.  
		 * @return
		 */
		public String location();
		
		/**
		 * 
		 * @return
		 */
		public long lastModified();
		
		/**
		 * Open the source file for reading.
		 */
		public InputStream contents() throws Exception;
	}
}
