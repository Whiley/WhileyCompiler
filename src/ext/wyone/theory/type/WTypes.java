package wyone.theory.type;

import java.util.*;
import wyone.core.*;

public class WTypes {
	// This file is part of the Wyone automated theorem prover.
	//
	// Wyone is free software; you can redistribute it and/or modify
	// it under the terms of the GNU General Public License as published
	// by the Free Software Foundation; either version 3 of the License,
	// or (at your option) any later version.
	//
	// Wyone is distributed in the hope that it will be useful, but
	// WITHOUT ANY WARRANTY; without even the implied warranty of
	// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
	// the GNU General Public License for more details.
	//
	// You should have received a copy of the GNU General Public
	// License along with Wyone. If not, see <http://www.gnu.org/licenses/>
	//
	// Copyright 2010, David James Pearce.


	/**
	 * Determine the type of a given expression; that is, the type of the value
	 * that this will evaluate to.
	 */
	public static WType type(WExpr e, SolverState state) {
		WType t = WAnyType.T_ANY;

		for(WConstraint f : state) {
			if(f instanceof WTypeDecl) {
				// FIXME: probably would make more sense to build up a GLB from
				// all possible types.
				WSubtype st = (WSubtype) f;
				if(aliases.contains(st.lhs())) {
					WType tmp = st.rhs();
					if(t.isSubtype(tmp, Collections.EMPTY_MAP)) {
						t = tmp;
					}
				}
			}
		}

		return t;
	}
}
