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

public class WExprs {

	/**
	 * The following method is provided so that subtypes of WExpr can register
	 * for a CID and, in turn, can implement compareTo in safe fashion across
	 * all possible WExpr implementations.
	 */	
	public static int registerCID() {
		return cid.getAndAdd(1);
	}
	
	private static final AtomicInteger cid = new AtomicInteger(0);
	
	/**
	 * The following method identifies any matching subterms. The method is
	 * currently used during Quantifier Instantiation to identify the set of
	 * quantified variables which need to be replaced.
	 * 
	 * @param <T>
	 * @param match
	 * @return
	 */
	public static <T> Set<T> match(Class<T> match, WExpr expr) {
		HashSet<T> matches = new HashSet<T>(); 		
		if(match.isInstance(expr)) {
			matches.add((T)expr);
		}
		for(WExpr p : expr.subterms()) {
			matches.addAll(match(match,p));
		}
		return matches;
	}
}
