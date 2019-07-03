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

import wybs.util.AbstractCompilationUnit.Tuple;
import wyil.lang.WyilFile.SemanticType;
import wyil.lang.WyilFile.Type;
import wyil.type.util.BinaryRelation;

/**
 * <p>
 * Provides a relaxation of the <code>StrictSubtypeOperator</code> which
 * corresponds with the notion of subtyping used at the Whiley source level.
 * Under this intepration, for example, "<code>{int x, int y}</code>" is
 * equivalent to "<code>{int y, int x}</code>". Likewise,
 * "<code>{int x, int y}</code>" is considered a subtype of
 * "<code>{int x}</code>". Finally, "<code>int:8</code>" is considered
 * equivalent to "<code>int:16</code>".
 * </p>
 * <p>
 * The purpose of this operator (compared with the strict operator) is to enable
 * intuitive subtyping relationships whilst preventing non-sensical ones. In
 * practice, however, some of these intuitive relationships are rejected by the
 * strict operator. What this means is that, under the hood, some kind of
 * representation conversion will be required for one type to flow into the
 * other. The exact details of this, however, depend on the backend in question.
 * In some backends (e.g. JavaScript) relatively few transformations are
 * required. In others (e.g. C), much more emphasis is placed on exact data
 * layout and, hence, many more conversions between layouts are required.
 * </p>
 *
 * @see StrictTypeEmptinessTest
 *
 * @author David J. Pearce
 *
 */
public class RelaxedTypeEmptinessTest extends StrictTypeEmptinessTest {
	/**
	 * <p>
	 * Determine whether the intersection of two function types is void or not. For
	 * example, <code>function(int)->(int)</code> intersecting with
	 * <code>function(bool)->(int)</code> gives void. In contrast, intersecting
	 * <code>function(int|null)->(int)</code> with <code>function(int)->(int)</code>
	 * does not give void. Likewise, <code>function(int)->(int)</code> intersecting
	 * with <code>!function(int)->(int)</code> gives void, whilst intersecting
	 * <code>function(int)->(int)</code> with <code>!function(bool)->(int)</code>
	 * does not give void.
	 * </p>
	 *
	 *
	 * @param lhsSign
	 *            The sign of the first type being intersected. If true, we have a
	 *            positive atom. Otherwise, we have a negative atom.
	 * @param lhs.
	 *            The first type being intersected, referred to as the "left-hand
	 *            side".
	 * @param rhsSign
	 *            The sign of the second type being intersected. If true, we have a
	 *            positive atom. Otherwise, we have a negative atom.
	 * @param rhs
	 *            The second type being intersected, referred to as the "right-hand
	 *            side".
	 * @param assumptions
	 *            The set of assumed subtype relationships private boolean @
	 */
	@Override
	protected boolean isVoidCallable(Atom<Type.Callable> lhs, Atom<Type.Callable> rhs,
			BinaryRelation<Term<?>> assumptions, LifetimeRelation lifetimes) {
		boolean lhsMeth = (lhs.type instanceof Type.Method);
		boolean rhsMeth = (rhs.type instanceof Type.Method);
		//
		// FIXME: this needs to deal properly with lifetime parameters
		//
		if (lhsMeth != rhsMeth && lhsMeth && lhs.sign) {
			// Intersecting positive method (lhs) with positive or negative function (rhs)
			// never gives void. This is because the set of methods includes the set of
			// functions, but not vice-versa.
			return false;
		} else if (lhsMeth != rhsMeth && rhsMeth && rhs.sign) {
			// Intersecting positive method (rhs) with positive or negative function (lhs)
			// never gives void. This is because the set of methods includes the set of
			// functions, but not vice-versa.
			return false;
		} else if (lhs.sign || rhs.sign) {
			// The sign indicates whether were in the pos-pos case, or in the
			// pos-neg case.
			Tuple<Type> lhsParameters = lhs.type.getParameters();
			Tuple<Type> rhsParameters = rhs.type.getParameters();
			Tuple<Type> lhsReturns = lhs.type.getReturns();
			Tuple<Type> rhsReturns = rhs.type.getReturns();
			// FIXME: should maximise be flipped for parameters as well?
			//
			// Parameters are contravariant. We can think of this as turning the hierarchy
			// upside down. Things which were large before are now small, etc. For example,
			// fun(int)->(int) & !fun(any)->(int) is not void. This is because, under
			// contravariance, any is *smaller* than int. However, fun(int|null)->(int) &
			// !fun(int)->(int) is void.
			boolean paramsContravariantVoid = isVoidParameters(lhs.negate(), lhsParameters, rhs.negate(), rhsParameters,
					assumptions, lifetimes);
			// Returns are covariant, which is the usual way of thinking about things. For
			// example, fun(int)->(int) & !fun(int)->any is void, whilst
			// fun(int)->(int|null) & !fun(int)->(int) is not.
			boolean returnsCovariantVoid = isVoidParameters(lhs, lhsReturns, rhs, rhsReturns, assumptions, lifetimes);
			// If both parameters and returns are void, then the whole thing is void.
			return paramsContravariantVoid && returnsCovariantVoid;
		} else {
			// In this case, we are intersecting two negative function types.
			// For example, !(function(int)->(int)) and
			// !(function(bool)->(int)). This never reduces to void.
			return false;
		}
	}

	protected boolean isVoidParameters(Context lhsContext, Tuple<Type> lhs, Context rhsContext, Tuple<Type> rhs, BinaryRelation<Term<?>> assumptions, LifetimeRelation lifetimes) {
		boolean sign = lhsContext.sign == rhsContext.sign;
		//
		if (lhs.size() != rhs.size()) {
			// Different number of parameters. In either pos-pos or neg-neg
			// cases, this reduces to void. Otherwise, it doesn't.
			return false;
		} else {
			//
			for (int i = 0; i != lhs.size(); ++i) {
				Type lhsParameter = lhs.get(i);
				Type rhsParameter = rhs.get(i);
				Term<?> lhsTerm = new Term<>(lhsParameter, lhsContext);
				Term<?> rhsTerm = new Term<>(rhsParameter, rhsContext);
				if (sign == isVoidTerm(lhsTerm, rhsTerm, assumptions, lifetimes)) {
					// For pos-pos / neg-neg case, there is no intersection
					// between this parameterand, hence, no intersection
					// overall; for pos-neg case, there is some
					// intersection between these parameters which means
					// that some intersections exists overall. For example,
					// consider the case (int,int|null) & !(int,int). There is
					// no intersection for first parameter (i.e. since int &
					// !int = void), whilst there is an intersection for second
					// parameter (i.e. since int|null & !int = null). Hence, we
					// can conclude that there is an intersection between them
					// with (int,null).
					return sign;
				}
			}
			if (sign == true) {
				// for pos-pos case, all parameters have intersection. Hence,
				// there is a possible intersection.
				return false;
			} else {
				// for pos-neg case, no parameters have intersection. Hence, no
				// possible intersection.
				return true;
			}
		}
}
	@Override
	protected int matchRecordFields(Atom<SemanticType.Record> lhs, Atom<SemanticType.Record> rhs, BinaryRelation<Term<?>> assumptions,
			LifetimeRelation lifetimes) {
		Tuple<? extends SemanticType.Field> lhsFields = lhs.type.getFields();
		Tuple<? extends SemanticType.Field> rhsFields = rhs.type.getFields();
		//
		boolean sign = (lhs.sign == rhs.sign);
		int matches = 0;
		//
		for (int i = 0; i != lhsFields.size(); ++i) {
			SemanticType.Field lhsField = lhsFields.get(i);
			Term<?> lhsTerm = new Term<>(lhsField.getType(), lhs);
			for (int j = 0; j != rhsFields.size(); ++j) {
				SemanticType.Field rhsField = rhsFields.get(j);
				if (!lhsField.getName().equals(rhsField.getName())) {
					continue;
				} else {
					Term<?> rhsTerm = new Term<>(rhsField.getType(), rhs);
					if (sign == isVoidTerm(lhsTerm, rhsTerm, assumptions, lifetimes)) {
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
			Tuple<? extends SemanticType.Field> lhsFields, boolean rhsSign, boolean rhsOpen,
			Tuple<? extends SemanticType.Field> rhsFields) {
		return super.analyseRecordMatches(matches, lhsSign, true, lhsFields, rhsSign, true, rhsFields);
	}
}
