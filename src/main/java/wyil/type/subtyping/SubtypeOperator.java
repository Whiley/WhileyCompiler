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
package wyil.type.subtyping;

import static wyc.lang.WhileyFile.*;

import java.util.HashSet;

import wybs.lang.NameID;
import wybs.lang.NameResolver;
import wybs.lang.NameResolver.ResolutionError;
import wyc.lang.WhileyFile.Decl;
import wyc.lang.WhileyFile.Type;
import wyil.type.subtyping.EmptinessTest.LifetimeRelation;

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
public class SubtypeOperator {
	private final NameResolver resolver;
	private final EmptinessTest<SemanticType> emptinessTest;

	enum Result {
		True, False, Unknown
	}

	public SubtypeOperator(NameResolver resolver, EmptinessTest<SemanticType> emptinessTest) {
		this.resolver = resolver;
		this.emptinessTest = emptinessTest;
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
	public boolean isSubtype(SemanticType lhs, SemanticType rhs, LifetimeRelation lifetimes) throws ResolutionError {
		boolean max = emptinessTest.isVoid(lhs, EmptinessTest.NegativeMax, rhs, EmptinessTest.PositiveMax, lifetimes);
		//
		// FIXME: I don't think this logic is correct yet for some reason.
		if (!max) {
			return false;
		} else {
			boolean min = emptinessTest.isVoid(lhs, EmptinessTest.NegativeMin, rhs, EmptinessTest.PositiveMin,
					lifetimes);
			if (min) {
				return true;
			} else {
				return false;
			}
		}
	}

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
	public boolean isVoid(SemanticType type, LifetimeRelation lifetimes) throws ResolutionError {
		return emptinessTest.isVoid(type, EmptinessTest.PositiveMax, type, EmptinessTest.PositiveMax, lifetimes);
	}

	/**
	 * <p>
	 * Contractive types are types which cannot accept value because they have an
	 * <i>unterminated cycle</i>. An unterminated cycle has no leaf nodes
	 * terminating it. For example, <code>X<{X field}></code> is contractive, where
	 * as <code>X<{null|X field}></code> is not.
	 * </p>
	 *
	 * <p>
	 * This method returns true if the type is contractive, or contains a
	 * contractive subcomponent. For example, <code>null|X<{X field}></code> is
	 * considered contracted.
	 * </p>
	 *
	 * @param type
	 *            --- type to test for contractivity.
	 * @return
	 * @throws ResolveError
	 */
	public boolean isContractive(NameID nid, Type type) throws ResolutionError {
		HashSet<NameID> visited = new HashSet<>();
		return isContractive(nid, type, visited);
	}

	private boolean isContractive(NameID name, Type type, HashSet<NameID> visited) throws ResolutionError {
		switch (type.getOpcode()) {
		case TYPE_void:
		case TYPE_any:
		case TYPE_null:
		case TYPE_bool:
		case TYPE_int:
		case TYPE_staticreference:
		case TYPE_reference:
		case TYPE_array:
		case TYPE_record:
		case TYPE_function:
		case TYPE_method:
		case TYPE_property:
		case TYPE_invariant:
		case TYPE_byte:
		case TYPE_unresolved:
			return true;
		case TYPE_union: {
			Type.Union c = (Type.Union) type;
			for (int i = 0; i != c.size(); ++i) {
				if (!isContractive(name, c.get(i), visited)) {
					return false;
				}
			}
			return true;
		}
		default:
		case TYPE_nominal: {
			Type.Nominal n = (Type.Nominal) type;
			Decl.Type decl = resolver.resolveExactly(n.getName(), Decl.Type.class);
			NameID nid = decl.getQualifiedName().toNameID();
			if (nid.equals(name)) {
				// We have identified a non-contract type.
				return false;
			} else if (visited.contains(nid)) {
				// NOTE: this identifies a type (other than the one we are looking for) which is
				// not contractive. It may seem odd then, that we pretend it is in fact
				// contractive. The reason for this is simply that we cannot tell here with the
				// type we are interested in is contractive or not. Thus, to improve the error
				// messages reported we ignore this non-contractiveness here (since we know
				// it'll be caught down the track anyway).
				return true;
			} else {
				visited.add(nid);
				return isContractive(name, decl.getType(), visited);
			}
		}
		}
	}
}
