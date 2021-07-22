// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package wycc.util;

/**
 * This class represents a triple of items
 *
 * @author David J. Pearce
 *
 * @param <FIRST> Type of first item
 * @param <SECOND> Type of second item
 * @param <THIRD> Type of second item
 */
public class Triple<FIRST,SECOND,THIRD> extends Pair<FIRST,SECOND> {
	public THIRD third;

	public Triple(FIRST f, SECOND s, THIRD t) {
		super(f,s);
		third=t;
	}

	public THIRD third() { return third; }

	@Override
	public int hashCode() {
		int phc = super.hashCode();
		int thc = third == null ? 0 : third.hashCode();
		return phc ^ thc;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Triple) {
			@SuppressWarnings("unchecked")
			Triple<FIRST,SECOND,THIRD> p = (Triple<FIRST,SECOND,THIRD>) o;
			boolean r=false;
			if(first() != null) { r = first().equals(p.first()); }
			else { r = p.first() == first(); }
			if(second() != null) { r &= second().equals(p.second()); }
			else { r &= p.second() == second(); }
			if(third != null) { r &= third.equals(p.third()); }
			else { r &= p.third() == third; }
			return r;
		}
		return false;
	}

	@Override
	public String toString() {
		String f = first() == null ? "null" : first().toString();
		String s = second() == null ? "null" : second().toString();
		String t = third == null ? "null" : third.toString();
		return "(" + f + "," + s + "," + t + ")";
	}
}
