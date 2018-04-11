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


import wybs.lang.NameResolver;
import wybs.lang.NameResolver.ResolutionError;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyc.lang.WhileyFile.SemanticType;

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

	public RelaxedTypeEmptinessTest(NameResolver resolver) {
		super(resolver);
	}

	@Override
	protected int matchRecordFields(Atom<SemanticType.Record> lhs, Atom<SemanticType.Record> rhs, Assumptions assumptions,
			LifetimeRelation lifetimes) throws ResolutionError {
		Tuple<? extends SemanticType.Field> lhsFields = lhs.type.getFields();
		Tuple<? extends SemanticType.Field> rhsFields = rhs.type.getFields();
		//
		boolean sign = (lhs.sign == rhs.sign);
		int matches = 0;
		//
		for (int i = 0; i != lhsFields.size(); ++i) {
			SemanticType.Field lhsField = lhsFields.get(i);
			Term<?> lhsTerm = new Term<>(lhs.sign, lhsField.getType(), lhs.maximise);
			for (int j = 0; j != rhsFields.size(); ++j) {
				SemanticType.Field rhsField = rhsFields.get(j);
				if (!lhsField.getName().equals(rhsField.getName())) {
					continue;
				} else {
					Term<?> rhsTerm = new Term<>(rhs.sign, rhsField.getType(), rhs.maximise);
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
