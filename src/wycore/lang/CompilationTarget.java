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

package wycore.lang;

import java.util.Set;

/**
 * <p>
 * An abstraction representing files which are the target of one or more
 * compilation steps. The primary purpose of this abstraction is to provide a
 * generic mechanism for extracting dependency information from such files.
 * </p>
 * 
 * <p>
 * For example, consider the following Whiley program:
 * 
 * <pre>
 * int sum([real] vals):
 *    sum = 0
 *    for r in vals:
 *        sum = sum + Math.round(r)
 *    return sum
 * </pre>
 * 
 * This file will generate a wyil file which, as a result of the call to
 * <code>Math.round()</code> depends upon the wyil module
 * <code>whiley.lang.Math</code>.
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public interface CompilationTarget {
	
	/**
	 * Return the set of entries on which this compilation target depends.
	 * @return
	 */
	public Set<Path.Entry<?>> dependencies();
}
