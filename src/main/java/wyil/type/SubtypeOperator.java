// Copyright 2011 The Whiley Project Developers
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
package wyil.type;

import static wyc.lang.WhileyFile.*;

import wybs.lang.NameID;
import wybs.lang.NameResolver.ResolutionError;
import wyil.type.SubtypeOperator.LifetimeRelation;

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
	 * <code>lhs</code> (denoted <code>lhs :> rhs</code>). In the presence of type
	 * invariants, this operation is undecidable. Therefore, a <i>three-valued</i>
	 * logic is employed. Either it was concluded that the subtype relation
	 * <i>definitely holds</i>, or that it <i>definitely does not hold</i> that it
	 * is <i>unknown</i> whether it holds or not.
	 * </p>
	 *
	 * <p>
	 * For example, <code>int|null :> int</code> definitely holds. Likewise,
	 * <code>int :> int|null</code> definitely does not hold. However, whether or
	 * not <code>nat :> pos</code> holds depends on the type invariants given for
	 * <code>nat</code> and <code>pos</code> which this operator cannot reason
	 * about. Observe that, in some cases, we do get effective reasoning about types
	 * with invariants. For example, <code>null|nat :> nat</code> will be determined
	 * to definitely hold, despite the fact that <code>nat</code> has a type
	 * invariant.
	 * </p>
	 *
	 * <p>
	 * Depending on the exact language of types involved, this can be a surprisingly
	 * complex operation. For example, in the presence of <i>union</i>,
	 * <i>intersection</i> and <i>negation</i> types, the subtype algorithm is
	 * surprisingly intricate.
	 * </p>
	 *
	 * @param lhs
	 *            The candidate "supertype". That is, lhs's raw type may be a
	 *            supertype of <code>rhs</code>'s raw type.
	 * @param rhs
	 *            The candidate "subtype". That is, rhs's raw type may be a subtype
	 *            of <code>lhs</code>'s raw type.
	 * @param lifetimes
	 *            The within relation between lifetimes that should be used when
	 *            determine whether the <code>rhs</code> is a subtype of the
	 *            <code>lhs</code>.
	 * @return
	 * @throws ResolutionError
	 *             Occurs when a nominal type is encountered whose name cannot be
	 *             resolved properly. For example, it resolves to more than one
	 *             possible matching declaration, or it cannot be resolved to a
	 *             corresponding type declaration.
	 */
	public Result isSubtype(Type lhs, Type rhs, LifetimeRelation lifetimes) throws ResolutionError;

	/**
	 * <p>
	 * Check whether a type is equivalent to <code>void</code> or not. The
	 * complexities of Whiley's type system mean that this is not always obvious.
	 * For example, the type <code>int&(!int)</code> is equivalent to
	 * <code>void</code>. Likewise, is the type <code>!any</code>. Another
	 * interesting case is the following:
	 * </p>
	 *
	 * <pre>
	 * type T is { T t }
	 * </pre>
	 *
	 * <p>
	 * This is only considered equivalent to <code>void</code> under an
	 * <i>inductive</i> interpretation of types (which is assumed in Whiley). The
	 * distinction is that, under a <i>coinductive</i> interpretation, instances of
	 * <code>T</code> do exist which, by construction, are infinite chains. Since
	 * such chains cannot be constructed in Whiley, we can disregard them.
	 * </p>
	 *
	 * @param type
	 *            The type being tested to see whether or not it is equivalent to
	 *            void.
	 * @param lifetimes
	 *            The within relation between lifetimes that should be used when
	 *            determine whether the <code>rhs</code> is a subtype of the
	 *            <code>lhs</code>.
	 *
	 * @return
	 * @throws ResolutionError
	 */
	public boolean isVoid(Type type, LifetimeRelation lifetimes) throws ResolutionError;

	/**
	 * <p>
	 * Contractive types are types which cannot accept value because they have
	 * an <i>unterminated cycle</i>. An unterminated cycle has no leaf nodes
	 * terminating it. For example, <code>X<{X field}></code> is contractive,
	 * where as <code>X<{null|X field}></code> is not.
	 * </p>
	 *
	 * <p>
	 * This method returns true if the type is contractive, or contains a
	 * contractive subcomponent. For example, <code>null|X<{X field}></code> is
	 * considered contracted.
	 * </p>
	 *
	 * @param type --- type to test for contractivity.
	 * @return
	 * @throws ResolveError
	 */
	public boolean isContractive(NameID nid, Type type) throws ResolutionError;

	/**
	 * <p>
	 * A lifetime relation determines, for any two lifetimes <code>l</code> and
	 * <code>m</code>, whether <code>l</code> is contained within <code>m</code> or
	 * not. This information is critical for subtype checking of reference types.
	 * Consider this minimal example:
	 * </p>
	 *
	 * <pre>
	 * method create() -> (&*:int r):
	 *    return this:new 42
	 * </pre>
	 * <p>
	 * This example should not compile. The reason is that the lifetime
	 * <code>this</code> is contained <i>within</i> the static lifetime
	 * <code>*</code>. Thus, the cell allocated within <code>create()</code> will be
	 * deallocated when the method ends and, hence, the method will return a
	 * <i>dangling reference</i>.
	 * </p>
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface LifetimeRelation {

		/**
		 * Determine whether one lifetime is contained entirely within another. This is
		 * the critical test for ensuring sound subtyping between references.
		 * Specifically, an assignment <code>&l:T p = q</code> is only considered safe
		 * if it can be shown that the lifetime of the cell referred to by
		 * <code>p</code> is <i>within</i> that of <code>q</code>.
		 *
		 * @param outer
		 * @param inner
		 * @return
		 */
		public boolean isWithin(String inner, String outer);
	}
}
