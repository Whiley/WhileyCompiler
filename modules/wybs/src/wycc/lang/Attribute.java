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

package wycc.lang;

/**
 * Represents a piece of meta-information that may be associated with a WYIL
 * bytecode or declaration. For example, the location of the element in the
 * source code which generated this bytecode.
 *
 * @author David J. Pearce
 *
 */
public interface Attribute {

	/**
	 * Represents a location in the source code of a Whiley Module. For example,
	 * this may be the location which generated a particular bytecode, or the
	 * location of a particular type declaration.
	 *
	 * @author David J. Pearce
	 *
	 */
	public final static class Source implements Attribute {
		public final int start;	 // starting character index
		public final int end;	 // end character index
		public final int line;   // line number

		public Source(int start, int end, int line) {
			this.start = start;
			this.end = end;
			this.line = line;
		}

		public String toString() {
			return "@" + start + ":" + end;
		}
	}

	/**
	 * Represents an originating source location for a given syntactic element.
	 * This typically occurs if some element from one file is included in
	 * another element from another file.
	 *
	 * @author David J. Pearce
	 *
	 */
	public final static class Origin implements Attribute {
		public final String filename;
		public final int start;	 // starting character index
		public final int end;	 // end character index
		public final int line;   // line number

		public Origin(String filename, int start, int end, int line) {
			this.filename = filename;
			this.start = start;
			this.end = end;
			this.line = line;
		}

		public String toString() {
			return filename + "@" + start + ":" + end;
		}
	}
}
