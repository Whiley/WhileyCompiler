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

import static wyc.lang.WhileyFile.*;

import java.util.ArrayList;
import java.util.function.BiFunction;

import wybs.lang.NameResolver;
import wyc.lang.WhileyFile.SemanticType;
import wyc.lang.WhileyFile.SemanticType.Atom;
import wyc.lang.WhileyFile.SemanticType.Record;
import wyil.type.subtyping.EmptinessTest;
import wyil.type.subtyping.SubtypeOperator;

/**
 * <p>
 * Responsible for extracting a readable record type. This is a conservative
 * approximation of that described in a given type which is safe to use when
 * reading elements from that type. For example, the type
 * <code>{int f}|{bool f}</code> has a readable record type of
 * <code>{int|bool f}</code>. This is the readable type as, if we were to read
 * field <code>f</code> from either bound, the return type would be in
 * <code>int|bool</code>. However, we cannot use the readable record type for
 * writing as this could be unsafe. For example, if we actually had a record of
 * type <code>{int f}</code>, then writing a boolean value is not permitted. Not
 * all types have readable record type and, furthermore, care must be exercised
 * for those that do. For example, <code>{int f}|int</code> does not have a
 * readable record type. Likewise, the readable record type for
 * <code>{int f, int g}|{bool f}</code> is <code>{int|bool f, ...}</code>.
 * Finally, negations play an important role in determining the readable record
 * type. For example, <code>{int|null f} & !{int f}</code> generates the
 * readable record type <code>{null f}</code>.
 * </p>
 */
public class TypeRecordExtractor extends AbstractTypeExtractor<SemanticType.Record> {

	public TypeRecordExtractor(NameResolver resolver, SubtypeOperator subtypeOperator) {
		super(resolver, subtypeOperator);
	}

	@Override
	protected Record construct(Atom type) {
		if(type instanceof SemanticType.Record) {
			return (SemanticType.Record) type;
		} else {
			return null;
		}
	}

	@Override
	protected SemanticType.Record union(SemanticType.Record lhs, SemanticType.Record rhs) {
		//
		Tuple<? extends SemanticType.Field> lhsFields = lhs.getFields();
		Tuple<? extends SemanticType.Field> rhsFields = rhs.getFields();
		// Determine the number of matching fields in the two records, as this is the
		// critical factor in determining the outcome.
		int count = countMatchingFields(lhsFields,rhsFields);
		// Determine whether result is an open record or not. If either of the records
		// is open, then the result is open. Likewise, the result is open if we
		// "compact" two compatible records down into one.
		boolean isOpenRecord = lhs.isOpen() || rhs.isOpen();
		isOpenRecord |= (lhsFields.size() > count || rhsFields.size() > count);
		//
		if(lhs instanceof Type && rhs instanceof Type) {
			// NOTE: this case is required to ensure that, when given two Types, the type
			// extractor produces a Type (rather than a SemanticType).
			Type.Field[] fields = new Type.Field[count];
			extractMatchingFieldsUnioned(lhsFields,rhsFields,fields,true);
			return new Type.Record(isOpenRecord, new Tuple<>(fields));
		} else {
			SemanticType.Field[] fields = new SemanticType.Field[count];
			extractMatchingFieldsUnioned(lhsFields,rhsFields,fields,false);
			return new SemanticType.Record(isOpenRecord, new Tuple<>(fields));
		}
	}

	protected int extractMatchingFieldsUnioned(Tuple<? extends SemanticType.Field> lhsFields,
			Tuple<? extends SemanticType.Field> rhsFields, SemanticType.Field[] result, boolean concrete) {
		int index = 0;
		// Extract all matching fields first.
		for (int i = 0; i != lhsFields.size(); ++i) {
			for (int j = 0; j != rhsFields.size(); ++j) {
				SemanticType.Field lhsField = lhsFields.get(i);
				SemanticType.Field rhsField = rhsFields.get(j);
				Identifier lhsFieldName = lhsField.getName();
				Identifier rhsFieldName = rhsField.getName();
				if (lhsFieldName.equals(rhsFieldName)) {
					SemanticType type = unionHelper(lhsField.getType(), rhsField.getType());
					SemanticType.Field combined = concrete ? new Type.Field(lhsFieldName, (Type) type)
							: new SemanticType.Field(lhsFieldName, type);
					result[index++] = combined;
				}
			}
		}
		return index;
	}

	@Override
	protected Record intersect(SemanticType.Record lhs, SemanticType.Record rhs) {
		Tuple<? extends SemanticType.Field> lhsFields = lhs.getFields();
		Tuple<? extends SemanticType.Field> rhsFields = rhs.getFields();
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
			return null;
		} else if (matches < rhsFields.size() && !lhs.isOpen()) {
			// Not enough matches made to meet the requirements of the rhs
			// type.
			return null;
		} else {
			// At this point, we know the intersection succeeds. The next
			// job is to determine the final set of field declarations.
			int lhsRemainder = lhsFields.size() - matches;
			int rhsRemainder = rhsFields.size() - matches;
			SemanticType.Field[] fields = new SemanticType.Field[matches + lhsRemainder + rhsRemainder];
			// Extract all matching fields first
			int index = extractMatchingFields(lhsFields, rhsFields, fields);
			// Extract remaining lhs fields second
			index = extractNonMatchingFields(lhsFields, rhsFields, fields, index);
			// Extract remaining rhs fields last
			index = extractNonMatchingFields(rhsFields, lhsFields, fields, index);
			// The intersection of two records can only be open when both
			// are themselves open.
			boolean isOpen = lhs.isOpen() && rhs.isOpen();
			//
			return new SemanticType.Record(isOpen, new Tuple<>(fields));
		}
	}

	@Override
	protected Record subtract(SemanticType.Record lhs, SemanticType.Record rhs) {
		//
		ArrayList<SemanticType.Field> fields = new ArrayList<>();
		Tuple<? extends SemanticType.Field> lhsFields = lhs.getFields();
		Tuple<? extends SemanticType.Field> rhsFields = rhs.getFields();
		for (int i = 0; i != lhsFields.size(); ++i) {
			SemanticType.Field lhsField = lhsFields.get(i);
			Identifier lhsFieldName = lhsField.getName();
			boolean matched = false;
			for (int j = 0; j != rhsFields.size(); ++j) {
				SemanticType.Field rhsField = rhsFields.get(j);
				Identifier rhsFieldName = rhsField.getName();
				if (lhsFieldName.equals(rhsFieldName)) {
					SemanticType diff = new SemanticType.Difference(lhsField.getType(), rhsField.getType());
					fields.add(new SemanticType.Field(lhsFieldName, diff));
					matched = true;
					break;
				}
			}
			//
			if (!matched && !rhs.isOpen()) {
				// We didn't find a corresponding field, and the rhs is fixed. This means the
				// rhs is not compatible with the lhs and can be ignored.
				return lhs;
			} else if (!matched) {
				// We didn't find a corresponding field, and the rhs is open. This just means
				// the rhs is not taking anything away from the lhs (for this field).
				fields.add(lhsField);
			}
		}
		return new SemanticType.Record(lhs.isOpen(), new Tuple<>(fields));
	}

	/**
	 * Count the number of matching fields. That is, fields with the same name.
	 *
	 * @param lhsFields
	 * @param rhsFields
	 * @return
	 */
	protected int countMatchingFields(Tuple<? extends SemanticType.Field> lhsFields, Tuple<? extends SemanticType.Field> rhsFields) {
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

	/**
	 * Extract all matching fields (i.e. fields with the same name) into the
	 * result array.
	 *
	 * @param lhsFields
	 * @param rhsFields
	 * @param result
	 * @return
	 */
	protected int extractMatchingFields(Tuple<? extends SemanticType.Field> lhsFields,
			Tuple<? extends SemanticType.Field> rhsFields, SemanticType.Field[] result) {
		int index = 0;
		// Extract all matching fields first.
		for (int i = 0; i != lhsFields.size(); ++i) {
			for (int j = 0; j != rhsFields.size(); ++j) {
				SemanticType.Field lhsField = lhsFields.get(i);
				SemanticType.Field rhsField = rhsFields.get(j);
				Identifier lhsFieldName = lhsField.getName();
				Identifier rhsFieldName = rhsField.getName();
				if (lhsFieldName.equals(rhsFieldName)) {
					SemanticType type = intersectionHelper(lhsField.getType(), rhsField.getType());
					SemanticType.Field combined = new SemanticType.Field(lhsFieldName, type);
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
	protected int extractNonMatchingFields(Tuple<? extends SemanticType.Field> lhsFields, Tuple<? extends SemanticType.Field> rhsFields,
			SemanticType.Field[] result, int index) {
		outer: for (int i = 0; i != lhsFields.size(); ++i) {
			for (int j = 0; j != rhsFields.size(); ++j) {
				SemanticType.Field lhsField = lhsFields.get(i);
				SemanticType.Field rhsField = rhsFields.get(j);
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
}
