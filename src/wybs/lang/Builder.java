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

import wyil.util.Pair;

/**
 * <p>
 * Responsible for transforming files from one content type to another.
 * Typically this revolves around compiling the source file into some kind of
 * binary, although other kinds of transformations are possible.
 * </p>
 * 
 * <p>
 * A given builder may support multiple transformations and the builder must
 * declare each of these.
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public interface Builder {
	
	/**
	 * Get the namespace this builder is operating on.
	 * @return
	 */
	public NameSpace namespace();
	
	/**
	 * Build a given set of source files to produce a given set of target files.  	 
	 * A delta represents a list of pairs (s,t), where s is a source file and t
	 * is its corresponding target entry. That is, the source entry is
	 * transformed into the target entry by the builder.
	 * 
	 * @param delta
	 *            --- the set of files to be built.
	 */
	public void build(List<Pair<Path.Entry<?>,Path.Entry<?>>> delta) throws Exception;	
}
