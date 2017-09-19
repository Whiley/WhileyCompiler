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
package wyc.type.subtyping;

import java.util.Arrays;
import java.util.Comparator;

import wybs.lang.NameResolver.ResolutionError;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyc.lang.WhileyFile.Decl;
import wyc.lang.WhileyFile.Type;
import wyc.type.TypeSystem;
import wyc.type.subtyping.StrictSubtypeOperator.Assumptions;
import wyc.type.subtyping.StrictSubtypeOperator.Atom;
import wyc.type.subtyping.StrictSubtypeOperator.Term;

import static wyc.lang.WhileyFile.*;

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

	@Override
	protected int matchRecordFields(Atom<Type.Record> lhs, Atom<Type.Record> rhs, Assumptions assumptions)
			throws ResolutionError {
		Tuple<Decl.Variable> lhsFields = lhs.type.getFields();
		Tuple<Decl.Variable> rhsFields = rhs.type.getFields();
		//
		boolean sign = (lhs.sign == rhs.sign);
		int matches = 0;
		//
		for (int i = 0; i != lhsFields.size(); ++i) {
			Decl.Variable lhsField = lhsFields.get(i);
			Term<?> lhsTerm = new Term<>(lhs.sign, lhsField.getType(), lhs.maximise);
			for (int j = 0; j != rhsFields.size(); ++j) {
				Decl.Variable rhsField = rhsFields.get(j);
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
						return -1;
					} else {
						matches = matches + 1;
					}
				}
			}
		}
		return matches;
	}

	@Override
	protected boolean analyseRecordMatches(int matches, boolean lhsSign, boolean lhsOpen,
			Tuple<Decl.Variable> lhsFields, boolean rhsSign, boolean rhsOpen, Tuple<Decl.Variable> rhsFields) {
		return super.analyseRecordMatches(matches, lhsSign, true, lhsFields, rhsSign, true, rhsFields);
	}
}
