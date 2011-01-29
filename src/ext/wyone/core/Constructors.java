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

package wyone.core;

import java.util.*;
import java.util.concurrent.atomic.*;

public class Constructors {

	/**
	 * The following method identifies any matching subterms. This is done by
	 * traversing the entire tree of subterms for an expression and looking for
	 * matches, rather than just traversing a single level.
	 * 
	 * @param <T>
	 * @param match
	 * @return
	 */
	public static <T> Set<T> match(Class<T> match, Constructor expr) {
		HashSet<T> matches = new HashSet<T>(); 		
		if(match.isInstance(expr)) {
			matches.add((T)expr);
		}
		for(Constructor p : expr.subterms()) {
			matches.addAll(match(match,p));
		}
		return matches;
	}
}
