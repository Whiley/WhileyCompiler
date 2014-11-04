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

package wyrl.util;

/**
 * This class represents a pair of items
 *
 * @author djp
 *
 * @param <FIRST> Type of first item
 * @param <SECOND> Type of second item
 */
public class Pair<FIRST,SECOND> {
	private final FIRST first;
	private final SECOND second;

	public Pair(FIRST f, SECOND s) {
		first=f;
		second=s;
	}

	public FIRST first() { return first; }
	public SECOND second() { return second; }

	public int hashCode() {
		int fhc = first == null ? 0 : first.hashCode();
		int shc = second == null ? 0 : second.hashCode();
		return fhc ^ shc;
	}

	public boolean equals(Object o) {
		if(o instanceof Pair) {
			Pair<FIRST, SECOND> p = (Pair<FIRST, SECOND>) o;
			boolean r = false;
			if(first != null) { r = first.equals(p.first()); }
			else { r = p.first() == first; }
			if(second != null) { r &= second.equals(p.second()); }
			else { r &= p.second() == second; }
			return r;
		}
		return false;
	}

	public String toString() {
		String fstr = first != null ? first.toString() : "null";
		String sstr = second != null ? second.toString() : "null";
		return "(" + fstr + ", " + sstr + ")";
	}
}
