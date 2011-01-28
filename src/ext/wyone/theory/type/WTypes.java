package wyone.theory.type;

import java.util.*;

import wyil.lang.Type;
import wyone.core.*;
import static wyone.core.Constructor.*;

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
	public static Type type(Variable e, Solver.State state) {
		Type t = Type.T_ANY;

		// An interesting question here, is whether or not we really do need to
		// build up the lub. If the type combining inference rule is
		// functioning, then shouldn't there only ever be one instance of a
		// matching type declaration?
		
		for(WConstraint f : state) {
			if(f instanceof WTypeDecl) {
				// FIXME: probably would make more sense to build up a GLB from
				// all possible types.
				WTypeDecl st = (WTypeDecl) f;
				if(st.var().equals(e)) {
					t = Type.leastUpperBound(t,st.declType());
				}
			}
		}

		return t;
	}
}
