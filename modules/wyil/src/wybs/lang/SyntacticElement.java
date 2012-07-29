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

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import wyil.lang.Attribute;

/**
 * A Syntactic Element represents any part of a source file which is relevant to
 * the syntactic structure of the file, and in particular parts we may wish to
 * add information too (e.g. line numbers, types, etc).
 * 
 * @author David J. Pearce
 * 
 */
public interface SyntacticElement {
	/**
     * Get the list of attributes associated with this syntactice element.
     * 
     * @return
     */
	public List<Attribute> attributes();
	
	/**
     * Get the first attribute of the given class type. This is useful
     * short-hand.
     * 
     * @param c
     * @return
     */
	public <T extends Attribute> T attribute(Class<T> c);
	
	public class Impl  implements SyntacticElement {
		private List<Attribute> attributes;
		
		public Impl() {
			// I use copy on write here, since for the most part I don't expect
			// attributes to change, and hence can be safely aliased. But, when they
			// do change I need fresh copies.
			attributes = new CopyOnWriteArrayList<Attribute>();
		}
		
		public Impl(Attribute x) {
			attributes = new ArrayList<Attribute>();
			attributes.add(x);
		}
		
		public Impl(Collection<Attribute> attributes) {
			this.attributes = new ArrayList<Attribute>(attributes);			
		}
		
		public Impl(Attribute[] attributes) {
			this.attributes = new ArrayList<Attribute>(Arrays.asList(attributes));			
		}
		
		public List<Attribute> attributes() { return attributes; }
		
		public <T extends Attribute> T attribute(Class<T> c) {
			for(Attribute a : attributes) {
				if(c.isInstance(a)) {
					return (T) a;
				}
			}
			return null;
		}		
	}
}
