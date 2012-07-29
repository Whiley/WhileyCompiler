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

import wyone.theory.logic.*;

public interface WExpr extends Comparable<WExpr> {

	/**
	 * The cid is a specifier used to carve up the domain, into distinct regions
	 * of different values. The purpose of the cid is to simply the task of
	 * implementing the compareTo method for any given implementation of WExpr.
	 * 
	 * @return
	 */
	public int cid();

	/**
	 * <p>
	 * This method replaces all occurrences of a given expression with another
	 * expression. Typically, this is used to substitute a variable for an
	 * expression, though not in all cases. Formulas automatically simplify
	 * themselves to be as small and compact as possible. For example, a formula
	 * where all variables have been eliminated necessarily becomes a constant
	 * of some sort.
	 * </p>
	 * 
	 * <p>
	 * <b>NOTE:</b> An important contract required by this method is that, if
	 * the substitution did not alter this expression in any way, then the
	 * return must be <b>this</b> expression (not a clone of this expression).
	 * The reason for this primarily to improve performance of this operation
	 * which is executed fairly frequently.
	 * </p>
	 * 
	 * 
	 * @param binding
	 * @return
	 */
	public WExpr substitute(Map<WExpr,WExpr> binding);

	/**
	 * The following method rearranges an expression, given that it's return
	 * value is known to equal another expression. This helps us to ensure that
	 * equalities are expressed in a normalised form, and also to identify
	 * assignments.
	 * 
	 * @param returnValue
	 * @return
	 */
	public WLiteral rearrange(WExpr returnValue);

	/**
	 * The following method returns the set of parameters to this expression
	 * constructor, in the order they are given.
	 * 
	 * @return
	 */
	public List<? extends WExpr> subterms();

	/**
	 * The following method gives the type of this expression. That is, the type
	 * of the value that this expression will evaluate to. A reference to the
	 * solver state is required in order to handle variables, whose type is
	 * determined by the current state. This is particularly useful for compound
	 * values, such as tuples and lists. In such cases, we need to know whether
	 * the element is an integer or not.
	 * 
	 * @param state
	 * @return
	 */
	public WType type(SolverState state);
}
