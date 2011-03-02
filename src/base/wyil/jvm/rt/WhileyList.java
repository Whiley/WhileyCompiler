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
import java.math.*;

public final class WhileyList extends ArrayList {
	public WhileyList() {
		super();
	}
	
	public WhileyList(Collection c) {
		super(c);
	}
	
	public boolean equals(WhileyList ws) {
		// FIXME: optimisation opportunity here
		return super.equals(ws);
	}
	
	public boolean notEquals(WhileyList ws) {
		return !super.equals(ws);
	}
	
	public WhileyList clone() {
		return Util.list_clone(this);		
	}
	
	public Object get(BigRational index) {	
		int idx = index.intValue();
		return get(idx); 		
	}
	
	public WhileyList append(WhileyList rhs) {
		WhileyList r = new WhileyList(this);
		r.addAll(rhs);
		return r;
	}
	
	public void set(BigRational index, Object val) {	
		int idx = index.intValue();
		set(idx,val); 		
	}		
	
	public static String toString(WhileyList list) {
		String r = "";
		for(Object o : list) {
			if(o instanceof BigRational) {
				int v = ((BigRational)o).intValue();
				r += (char) v;
			} else {
				throw new RuntimeException("Invalid WhileyList");
			}
		}
		return r;
	}		
}
