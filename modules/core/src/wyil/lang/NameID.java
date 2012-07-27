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

package wyil.lang;

import wybs.lang.Path;

/**
 * A Name Identifier consists of a module, and a name within that module. The
 * purpose of this is to provide a uniform way of referring to modules +
 * names throughout the compiler.
 * 
 * @author David J. Pearce
 * 
 */
public final class NameID {
	private final Path.ID module;
	private final String name;
		
	public NameID(Path.ID module, String name) {		
		this.module = module;
		this.name = name;
	}

	public String name() {
		return name;
	}
	
	public Path.ID module() {
		return module;
	}
	
	public String toString() {
		return module + ":" + name;
	}
	
	public int hashCode() {
		return name.hashCode() ^ module.hashCode();
	}
	
	public boolean equals(Object o) {
		if (o instanceof NameID) {
			NameID u = (NameID) o;			
			return u.module.equals(module) && u.name.equals(name);						
		}
		return false;
	}
}
