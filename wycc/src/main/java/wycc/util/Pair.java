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
 * This class represents a pair of items
 *
 * @author David J. Pearce
 *
 * @param <FIRST> Type of first item
 * @param <SECOND> Type of second item
 */
public class Pair<FIRST,SECOND> {
	protected final FIRST first;
	protected final SECOND second;

	public Pair(FIRST f, SECOND s) {
		first=f;
		second=s;
	}

	public FIRST first() { return first; }
	public SECOND second() { return second; }

	@Override
	public int hashCode() {
		int fhc = first == null ? 0 : first.hashCode();
		int shc = second == null ? 0 : second.hashCode();
		return fhc ^ shc;
	}

	@Override
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

	@Override
	public String toString() {
		String fstr = first != null ? first.toString() : "null";
		String sstr = second != null ? second.toString() : "null";
		return "(" + fstr + ", " + sstr + ")";
	}
}
