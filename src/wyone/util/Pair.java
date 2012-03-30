// This file is part of the Wyone automated theorem prover.
//
// Wyone is free software; you can redistribute it and/or modify 
// it under the terms of the GNU General Public License as published 
// by the Free Software Foundation; either version 3 of the License, 
// or (at your option) any later version.
//
// Wyone is distributed in the hope that it will be useful, but 
// WITHOUT ANY WARRANTY; without even the implied warranty of 
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See 
// the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with Wyone. If not, see <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyone.util;

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
			@SuppressWarnings("unchecked")
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
