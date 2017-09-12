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
package wyc.type.subtyping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;

import wycc.util.Pair;
import wybs.lang.NameResolver;
import wybs.lang.NameResolver.ResolutionError;
import wyc.type.SubtypeOperator;
import wyc.type.TypeSystem;

import static wyc.lang.WhileyFile.*;
import static wyc.lang.WhileyFile.Name;

/**
 * <p>
 * The subtype operator implements the algorithm for determining whether or not
 * one type is a <i>subtype</i> of another. For the most part, one can take
 * subtype to mean <i>subset</i> (this analogy breaks down with function types,
 * however). Following this analogy, <code>T1</code> is a subtype of
 * <code>T2</code> (denoted <code>T1 <: T2</code>) if the set of values
 * represented by <code>T1</code> is a subset of those represented by
 * <code>T2</code>.
 * </p>
 * <p>
 * The algorithm actually operates by computing the <i>intersection</i> relation
 * for two types (i.e. whether or not an intersection exists between their set
 * of values). Subtyping is closely related to intersection and, in fact, we
 * have that <code>T1 :> T2</code> iff <code>!(!T1 & T2)</code> (where
 * <code>&</code> is the intersection relation). The choice to compute
 * intersections, rather than subtypes, was for simplicity. Namely, it was
 * considered conceptually easier to think about intersections rather than
 * subtypes.
 * </p>
 * <p>
 * <b>NOTE:</b> for this algorithm to return correct results in all cases, both
 * types must have been normalised first.
 * </p>
 * <h3>References</h3>
 * <ul>
 * <li><p>David J. Pearce and James Noble. Structural and Flow-Sensitive Types for
 * Whiley. Technical Report, Victoria University of Wellington, 2010.</p></li>
 * <li><p>A. Frisch, G. Castagna, and V. Benzaken. Semantic subtyping. In
 * Proceedings of the <i>Symposium on Logic in Computer Science</i>, pages
 * 137--146. IEEE Computer Society Press, 2002.</p></li>
 * <li><p>Dexter Kozen, Jens Palsberg, and Michael I. Schwartzbach. Efficient
 * recursive subtyping. In <i>Proceedings of the ACM Conference on Principles of
 * Programming Languages</i>, pages 419--428, 1993.</p></li>
 * <li><p>Roberto M. Amadio and Luca Cardelli. Subtyping recursive types. <i>ACM
 * Transactions on Programming Languages and Systems</i>,
 * 15:575--631, 1993.</p></li>
 * </ul>
 *
 * @author David J. Pearce
 *
 */
public class CoerciveSubtypeOperator extends StrictSubtypeOperator {

	public CoerciveSubtypeOperator(TypeSystem typeSystem) {
		super(typeSystem);
	}

	/**
	 * <p>
	 * Determine whether the intersection of two record types is void or not.
	 * For example, <code>{int f}</code> intersecting with <code>{int g}</code>
	 * gives void. In contrast, intersecting <code>{int|null f}</code> with
	 * <code>{int f}</code> does not give void. Likewise, <code>{int f}</code>
	 * intersecting with <code>!{int f}</code> gives void, whilst intersecting
	 * <code>{int f}</code> with <code>!{int g}</code> does not give void.
	 * </p>
	 *
	 * @param lhsSign
	 *            The sign of the first type being intersected. If true, we have
	 *            a positive atom. Otherwise, we have a negative atom.
	 * @param lhs.
	 *            The first type being intersected, referred to as the
	 *            "left-hand side".
	 * @param rhsSign
	 *            The sign of the second type being intersected. If true, we
	 *            have a positive atom. Otherwise, we have a negative atom.
	 * @param rhs
	 *            The second type being intersected, referred to as the
	 *            "right-hand side".
	 * @param assumptions
	 *            The set of assumed subtype relationships
	 * @return
	 * @throws ResolutionError
	 */
	@Override
	protected boolean isVoidRecord(Atom<Type.Record> lhs, Atom<Type.Record> rhs, Assumptions assumptions)
			throws ResolutionError {
		Tuple<Declaration.Variable> lhsFields = lhs.type.getFields();
		Tuple<Declaration.Variable> rhsFields = rhs.type.getFields();
		//
		if (lhs.sign || rhs.sign) {
			// The sign indicates whether were in the pos-pos case, or in the
			// pos-neg case.
			boolean sign = (lhs.sign == rhs.sign);
			// Attempt to match all fields In the positive-positive case this
			// reduces to void if the fields in either of these differ (e.g.
			// {int f} and {int g}), or if there is no intersection between the
			// same field in either (e.g. {int f} and {bool f}).
			int matches = 0;
			//
			for (int i = 0; i != lhsFields.size(); ++i) {
				Declaration.Variable lhsField = lhsFields.getOperand(i);
				Term<?> lhsTerm = new Term<>(lhs.sign, lhsField.getType(), lhs.maximise);
				for (int j = 0; j != rhsFields.size(); ++j) {
					Declaration.Variable rhsField = rhsFields.getOperand(j);
					if (!lhsField.getName().equals(rhsField.getName())) {
						continue;
					} else {
						Term<?> rhsTerm = new Term<>(rhs.sign, rhsField.getType(), rhs.maximise);
						if (sign == isVoidTerm(lhsTerm, rhsTerm, assumptions)) {
							// For pos-pos case, there is no intersection
							// between these fields and, hence, no intersection
							// overall; for pos-neg case, there is some
							// intersection between these fields which means
							// that some intersections exists overall. For
							// example, consider the case {int f, int|null g} &
							// !{int f, int g}. There is no intersection for
							// field f (i.e. since int & !int = void), whilst
							// there is an intersection for field g (i.e. since
							// int|null & !int = null). Hence, we can conclude
							// that there is an intersection between them with
							// {int f, null g}.
							return sign;
						} else {
							matches = matches + 1;
						}
					}
				}
			}
			//
			if(matches == lhsFields.size() && matches == rhsFields.size()) {
				// If we get here, then: for pos-pos case, all fields have
				// intersection; for pos-neg case, no fields have intersection.
				return !sign;
			} else if (matches != lhsFields.size() && matches !=rhsFields.size()) {
				// If we get here, then both records have fields not contained in the other. For
				// pos-pos case we have {int x, int y} & {int x, int z} gives {int x, int y, int
				// z}. For pos-neg case we have {int x, int y, ...} & !{int x, int z, ...}.
				// gives {int x, int y, ...}
				return false;
			} else if(matches < lhsFields.size()) {
				// If we get here then rhs fields contained in lhs fields. For pos-pos case we
				// have {int x, int y} & {int x}. For pos-neg case we have {int x, int y} &
				// !{int x} OR !{int x, int y} & {int x}.
				return !rhs.sign;
			} else {
				// Symmetric case to above where lhs fields contained in rhs fields.
				return !lhs.sign;
			}
		} else {
			// In this case, we are intersecting two negative record types. For
			// example, !({int f}) and !({int g}). This never reduces to void.
			return false;
		}
	}
}
