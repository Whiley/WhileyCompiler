// Copyright (c) 2014, David J. Pearce (djp@ecs.vuw.ac.nz)
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

package wyil.attributes;

import wyil.lang.Attribute;
import wyil.lang.CodeBlock;
import wyil.util.AbstractAttributeMap;

/**
 * <p>
 * The source location map is used to map individual bytecodes to their
 * positions within their originating source file(s). The information stored for
 * each bytecode is simply the character start and end positions within the
 * original source file.
 * </p>
 *
 * <p>
 * <b>NOTE:</b> Multiple bytecodes may map to the same or overlapping positions
 * within the source file. Also, bytecodes within the same file can map to
 * different source files.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class SourceLocationMap extends AbstractAttributeMap<SourceLocation>
		implements Attribute.Map<SourceLocation> {

	@Override
	public Class<? extends Attribute> type() {
		return SourceLocation.class;
	}
	
	@Override
	public void put(CodeBlock.Index location, SourceLocation data) {
		super.put(location, data);
	}
	
	public SourceLocation get(CodeBlock.Index location) {
		SourceLocation l = super.get(location);
		return l;
	}
}
