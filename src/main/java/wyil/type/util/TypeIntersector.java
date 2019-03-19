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
package wyil.type.util;

import java.util.Set;

import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyil.lang.WyilFile.SemanticType;
import wyil.lang.WyilFile.Type;
import wyil.type.subtyping.SubtypeOperator;
import wyil.type.subtyping.EmptinessTest.LifetimeRelation;
import wyil.type.util.AbstractTypeCombinator.Linkage;

/**
 * Intersect a concrete type with a semantic type. The rough idea is that the
 * semantic type "selects" from the concrete type. For example, when
 * intersecting <code>int|null</code> with <code>int</code> we are selecting the
 * latter from the former. More complex situations arise though. For example,
 * intersection <code>{int|null f}</code> with <code>{int f}|{null f}</code>.
 * The following example illustrates how this can arise:
 * </p>
 *
 * <pre>
 * function f(int|{int|null f} x) -> (int|null r):
 *  if x is {int f}|{null f}:
 *      return x.f
 *  else:
 *    return null
 * </pre>
 * <p>
 * The question is, what should the resulting type of <code>x</code> be after
 * this? An easy solution is to determine the type as
 * <code>int|{int|null f}&({int f}|{null f})</code> which reduces to
 * <code>({int|null f}&{int f})|({int|null f}&{null f})</code> and, finally, to
 * <code>{int f}|{null f}</code>. But, does that make sense?
 * </p>
 * <p>
 * The following illustrates how another intersection how might arise:
 * </p>
 *
 * <pre>
 * function f((int|null)[] xs) -> int:
 *    if x is int[]:
 *      return |x|
 *    else:
 *      return 0
 * </pre>
 *
 * Here, the concrete type for <code>x</code> in <code>|x|</code> will be
 * <code>(int|null)[] & int[]</code>, which produces just <code>int[]</code>.
 */
public class TypeIntersector extends AbstractTypeCombinator {

	public TypeIntersector(SubtypeOperator subtyping) {
		super(subtyping);
	}

	@Override
	protected Type apply(Type lhs, Type rhs, LifetimeRelation lifetimes, LinkageStack stack) {
		Type t = super.apply(lhs, rhs, lifetimes, stack);
		if (t == null) {
			return Type.Void;
		} else {
			return t;
		}
	}

	@Override
	protected Type apply(Type.Null lhs, Type.Null rhs, LifetimeRelation lifetimes, LinkageStack stack) {
		return lhs;
	}

	@Override
	protected Type apply(Type.Bool lhs, Type.Bool rhs, LifetimeRelation lifetimes, LinkageStack stack) {
		return lhs;
	}

	@Override
	protected Type apply(Type.Byte lhs, Type.Byte rhs, LifetimeRelation lifetimes, LinkageStack stack) {
		return lhs;
	}

	@Override
	protected Type apply(Type.Int lhs, Type.Int rhs, LifetimeRelation lifetimes, LinkageStack stack) {
		return lhs;
	}

	/**
	 * Intersect a concrete array with a semantic array. The following illustrates
	 * how this might arise:
	 *
	 * <pre>
	 * function f((int|null)[] xs) -> int:
	 *    if x is int[]:
	 *      return |x|
	 *    else:
	 *      return 0
	 * </pre>
	 *
	 * Here, the concrete type for <code>x</code> in <code>|x|</code> will be
	 * <code>(int|null)[] & int[]</code>, which produces just <code>int[]</code>.
	 *
	 * @param lhs
	 *            Concrete type being intersected against
	 * @param rhs
	 *            Semantic type being intersected with
	 * @return
	 */
	@Override
	protected Type apply(Type.Array lhs, Type.Array rhs, LifetimeRelation lifetimes, LinkageStack stack) {
		Type element = apply(lhs.getElement(), rhs.getElement(), lifetimes, stack);
		if (element instanceof Type.Void) {
			return Type.Void;
		} else {
			return new Type.Array(element);
		}
	}

	@Override
	protected Type apply(Type.Reference lhs, Type.Reference rhs, LifetimeRelation lifetimes, LinkageStack stack) {
		if (subtyping.isSubtype(lhs, rhs, lifetimes) && subtyping.isSubtype(rhs, lhs, lifetimes)) {
			return lhs;
		} else {
			return Type.Void;
		}
	}

	@Override
	protected Type apply(Type.Record lhs, Type.Record rhs, LifetimeRelation lifetimes, LinkageStack stack) {
		Tuple<Type.Field> lhsFields = lhs.getFields();
		Tuple<Type.Field> rhsFields = rhs.getFields();
		// Determine the number of matching fields. That is, fields with the
		// same name.
		int matches = countMatchingFields(lhsFields, rhsFields);
		// When intersecting two records, the number of fields is only
		// allowed to differ if one of them is an open record. Therefore, we
		// need to pay careful attention to the size of the resulting match
		// in comparison with the original records.
		if (matches < lhsFields.size() && !rhs.isOpen()) {
			// Not enough matches made to meet the requirements of the lhs
			// type.
			return Type.Void;
		} else if (matches < rhsFields.size() && !lhs.isOpen()) {
			// Not enough matches made to meet the requirements of the rhs
			// type.
			return Type.Void;
		} else {
			// At this point, we know the intersection succeeds. The next
			// job is to determine the final set of field declarations.
			int lhsRemainder = lhsFields.size() - matches;
			int rhsRemainder = rhsFields.size() - matches;
			Type.Field[] fields = new Type.Field[matches + lhsRemainder + rhsRemainder];
			// Extract all matching fields first
			int index = extractMatchingFields(lhsFields, rhsFields, fields, lifetimes, stack);
			// Extract remaining lhs fields second
			index = extractNonMatchingFields(lhsFields, rhsFields, fields, index);
			// Extract remaining rhs fields last
			index = extractNonMatchingFields(rhsFields, lhsFields, fields, index);
			// The intersection of two records can only be open when both
			// are themselves open.
			boolean isOpen = lhs.isOpen() && rhs.isOpen();
			// Finally, sanity check the result. If any field has type void, then the whole
			// thing is void.
			for(int i=0;i!=fields.length;++i) {
				if(fields[i].getType() instanceof Type.Void) {
					return Type.Void;
				}
			}
			// Done
			return new Type.Record(isOpen, new Tuple<>(fields));
		}
	}

	@Override
	protected Type apply(Type.Function lhs, Type.Function rhs, LifetimeRelation lifetimes, LinkageStack stack) {
		if (lhs.equals(rhs)) {
			return lhs;
		} else {
			return Type.Void;
		}
	}

	@Override
	protected Type apply(Type.Method lhs, Type.Method rhs, LifetimeRelation lifetimes, LinkageStack stack) {
		if (lhs.equals(rhs)) {
			return lhs;
		} else {
			return Type.Void;
		}
	}

	@Override
	protected Type apply(Type.Nominal lhs, Type.Nominal rhs, LifetimeRelation lifetimes, LinkageStack stack) {
		if (lhs.getLink().equals(rhs.getLink())) {
			return lhs;
		} else {
			return super.apply(lhs, rhs, lifetimes, stack);
		}
	}

	// ===================================================================================
	// Helpers
	// ===================================================================================

	/**
	 * Extract all matching fields (i.e. fields with the same name) into the result
	 * array.
	 *
	 * @param lhsFields
	 * @param rhsFields
	 * @param result
	 * @return
	 */
	private int extractMatchingFields(Tuple<Type.Field> lhsFields, Tuple<Type.Field> rhsFields, Type.Field[] result,
			LifetimeRelation lifetimes, LinkageStack stack) {
		int index = 0;
		// Extract all matching fields first.
		for (int i = 0; i != lhsFields.size(); ++i) {
			for (int j = 0; j != rhsFields.size(); ++j) {
				Type.Field lhsField = lhsFields.get(i);
				Type.Field rhsField = rhsFields.get(j);
				Identifier lhsFieldName = lhsField.getName();
				Identifier rhsFieldName = rhsField.getName();
				if (lhsFieldName.equals(rhsFieldName)) {
					Type type = apply(lhsField.getType(), rhsField.getType(), lifetimes, stack);
					Type.Field combined = new Type.Field(lhsFieldName, type);
					result[index++] = combined;
				}
			}
		}
		return index;
	}

	/**
	 * Extract fields from lhs which do not match any field in the rhs. That is,
	 * there is no field in the rhs with the same name.
	 *
	 * @param lhsFields
	 * @param rhsFields
	 * @param result
	 * @param index
	 * @return
	 */
	private static int extractNonMatchingFields(Tuple<Type.Field> lhsFields, Tuple<Type.Field> rhsFields,
			Type.Field[] result, int index) {
		outer: for (int i = 0; i != lhsFields.size(); ++i) {
			for (int j = 0; j != rhsFields.size(); ++j) {
				Type.Field lhsField = lhsFields.get(i);
				Type.Field rhsField = rhsFields.get(j);
				Identifier lhsFieldName = lhsField.getName();
				Identifier rhsFieldName = rhsField.getName();
				if (lhsFieldName.equals(rhsFieldName)) {
					// This is a matching field. Therefore, continue on to the
					// next lhs field
					continue outer;
				}
			}
			result[index++] = lhsFields.get(i);
		}
		return index;
	}

	/**
	 * Count the number of matching fields. That is, fields with the same name.
	 *
	 * @param lhsFields
	 * @param rhsFields
	 * @return
	 */
	private static int countMatchingFields(Tuple<? extends SemanticType.Field> lhsFields,
			Tuple<? extends SemanticType.Field> rhsFields) {
		int count = 0;
		for (int i = 0; i != lhsFields.size(); ++i) {
			for (int j = 0; j != rhsFields.size(); ++j) {
				SemanticType.Field lhsField = lhsFields.get(i);
				SemanticType.Field rhsField = rhsFields.get(j);
				Identifier lhsFieldName = lhsField.getName();
				Identifier rhsFieldName = rhsField.getName();
				if (lhsFieldName.equals(rhsFieldName)) {
					count = count + 1;
				}
			}
		}
		return count;
	}
}