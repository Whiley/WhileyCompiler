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

/**
 * <p>
 * Provides identifying information for a source file which contributed to the
 * generation of a given WyIL file. The identifying information is largely
 * system dependent, but should identify the originating source file (rather
 * than, say, its enclosing directory). Typically this includes the filename and
 * either a complete or partial path.
 * </p>
 *
 * <p>
 * <b>NOTE:</b> There may be more than one <code>SourceFile</code> attribute for
 * a given WyIL file. This can arise if the WyIL was generated from the
 * combination of multiple files (e.g. as a result of weaving, inlined
 * functions, etc).
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class SourceFile implements Attribute {
	private String filename;

	public SourceFile(String name) {
		this.filename = name;
	}

	public String sourceFile() {
		return filename;
	}

	public void setSourceFile(String name) {
		// could check for syntactically valid names here.
		this.filename = name;
	}
}
