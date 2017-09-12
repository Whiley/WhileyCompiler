// Copyright 2017 David J. Pearce
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
package wyc.type;

import static wyc.lang.WhileyFile.*;
import wybs.lang.NameResolver.ResolutionError;

/**
 * <p>
 * Represents the subtype operation over types. This can be though of as similar
 * to the subset operator commonly found in set theory. Following this view,
 * types are seen as sets containing their possible values as elements. For
 * example, the type <code>int</code> would correspond to the set of all
 * possible integer values. Then, one type is a subtype another if the set it
 * corresponds to is a subset of the other's corresponding set.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public interface SubtypeOperator {

	enum Result {
		True,
		False,
		Unknown
	}

	/**
	 * <p>
	 * Determine whether the <code>rhs</code> type is a <i>subtype</i> of the
	 * <code>lhs</code> (denoted <code>lhs :> rhs</code>). In the presence of
	 * type invariants, this operation is undecidable. Therefore, a
	 * <i>three-valued</i> logic is employed. Either it was concluded that the
	 * subtype relation <i>definitely holds</i>, or that it <i>definitely does
	 * not hold</i> that it is <i>unknown</i> whether it holds or not.
	 * </p>
	 *
	 * <p>
	 * For example, <code>int|null :> int</code> definitely holds. Likewise,
	 * <code>int :> int|null</code> definitely does not hold. However, whether
	 * or not <code>nat :> pos</code> holds depends on the type invariants given
	 * for <code>nat</code> and <code>pos</code> which this operator cannot
	 * reason about. Observe that, in some cases, we do get effective reasoning
	 * about types with invariants. For example, <code>null|nat :> nat</code>
	 * will be determined to definitely hold, despite the fact that
	 * <code>nat</code> has a type invariant.
	 * </p>
	 *
	 * <p>
	 * Depending on the exact language of types involved, this can be a
	 * surprisingly complex operation. For example, in the presence of
	 * <i>union</i>, <i>intersection</i> and <i>negation</i> types, the subtype
	 * algorithm is surprisingly intricate.
	 * </p>
	 *
	 * @param lhs
	 *            The candidate "supertype". That is, lhs's raw type may be a
	 *            supertype of <code>rhs</code>'s raw type.
	 * @param rhs
	 *            The candidate "subtype". That is, rhs's raw type may be a
	 *            subtype of <code>lhs</code>'s raw type.
	 * @return
	 * @throws ResolutionError
	 *             Occurs when a nominal type is encountered whose name cannot
	 *             be resolved properly. For example, it resolves to more than
	 *             one possible matching declaration, or it cannot be resolved
	 *             to a corresponding type declaration.
	 */
	public Result isSubtype(Type lhs, Type rhs) throws ResolutionError;
}
