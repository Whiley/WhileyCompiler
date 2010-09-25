// This file is part of the Whiley-to-Java Compiler (wyjc).
//
// The Whiley-to-Java Compiler is free software; you can redistribute 
// it and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 3 of the License, or (at your option) any later version.
//
// The Whiley-to-Java Compiler is distributed in the hope that it 
// will be useful, but WITHOUT ANY WARRANTY; without even the 
// implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
// PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Whiley-to-Java Compiler. If not, see 
// <http://www.gnu.org/licenses/>
//
// Copyright 2010, David James Pearce. 

package wyil.jvm.rt;

import java.util.*;

public final class WhileySet extends HashSet {
	public WhileySet() {
		super();
	}
	
	public WhileySet(Collection c) {
		super(c);
	}
	
	public WhileySet clone() {
		return new WhileySet(this);
	}
	
	public String toString() {
		String r = "{";
		boolean firstTime=true;
		ArrayList<String> ss = new ArrayList<String>(this);		
		Collections.sort(ss);

		for(Object o : ss) {
			if(!firstTime) {
				r = r + ", ";
			}
			firstTime=false;
			r = r + o.toString();
		}
		return r + "}";
	}		
	
	public boolean equals(WhileySet ws) {
		// FIXME: optimisation opportunity here
		return super.equals(ws);
	}
	
	public boolean notEquals(WhileySet ws) {
		return !super.equals(ws);
	}
	
	public boolean subset(WhileySet ws) {
		return ws.containsAll(this) && ws.size() > size();
	}
	
	public boolean subsetEq(WhileySet ws) {
		return ws.containsAll(this);
	}
	
	public WhileySet union(WhileySet rset) {
		WhileySet set = new WhileySet(this);
		set.addAll(rset);
		return set;
	}
	
	public WhileySet difference(WhileySet rset) {
		WhileySet set = new WhileySet(this);
		set.removeAll(rset);
		return set;
	}
	
	public WhileySet intersect(WhileySet rset) {
		WhileySet set = new WhileySet(); 		
		for(Object o : this) {
			if(rset.contains(o)) {
				set.add(o);
			}
		}
		return set;
	}
}
