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

import java.io.IOException;
import java.util.*;

/**
 * The fundamental building block of the build system. A BuildRule identifies a set of
 * target entries and their corresponding dependents.
 * 
 * @author David J. Pearce
 * 
 */
public interface BuildRule {

	/**
	 * Determine the target entries that are dependent on a given source, as
	 * determined by this rule. If the given source is not matched by this rule
	 * then it just returns the empty set (i.e. no dependents).
	 * 
	 * @param source
	 *            --- entry to determine dependents of.
	 * @return
	 */
	public Set<Path.Entry<?>> dependentsOf(Path.Entry<?> source) throws IOException;
	
	/**
	 * <p>
	 * Given a complete list of targets scheduled for recompilation, apply this
	 * rule. This will compile all targets in the list which are matched by this
	 * rule, and which do not depend on other schedule targets not not matched
	 * by this rule.
	 * </p>
	 * 
	 * <p>
	 * An entry should only be rebuilt if one or more of its dependents have a
	 * modification date after their corresponding target (as defined by this
	 * rule). If at least such one dependent, then it must be recompiled to
	 * produce an updated target.
	 * </p>
	 * 
	 * <p>
	 * <b>NOTE:</b> this rule must remove any entries from the
	 * <code>targets</code> list that it has rebuilt.
	 * </p>
	 * 
	 * @throws IOException
	 */
	public void apply(Set<Path.Entry<?>> targets) throws Exception;
}
