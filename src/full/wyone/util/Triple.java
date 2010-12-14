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
 * This class represents a triple of items
 * 
 * @author djp
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
	public int hashCode() {		
		int phc = super.hashCode();
		int thc = third == null ? 0 : third.hashCode();
		return phc ^ thc; 
	}
	
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
	
	public String toString() {
		String f = first() == null ? "null" : first().toString();
		String s = second() == null ? "null" : second().toString();
		String t = third == null ? "null" : third.toString();
		return "(" + f + "," + s + "," + t + ")";
	}
}
