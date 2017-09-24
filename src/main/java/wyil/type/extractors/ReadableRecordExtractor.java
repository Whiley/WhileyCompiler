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
package wyil.type.extractors;

import java.util.ArrayList;

import static wyc.lang.WhileyFile.*;

import wybs.lang.NameResolver;
import wybs.lang.NameResolver.ResolutionError;
import wyil.type.TypeSystem;

/**
 * <p>
 * Responsible for extracting a "readable record" from a given type. A readable
 * record is a conservative approximation of the records described in a given
 * type. Furthermore, it is safe to use when reading field values from that type.
 * For example, the type <code>{int f}|{bool f}</code> has a readable record
 * type of <code>{int|bool f}</code>. This is the readable type as, if we were
 * to read field <code>f</code> from either bound, the return type would be in
 * <code>int|bool</code>. However, we cannot use the readable record type for
 * writing as this could be unsafe. For example, if we actually had a record of
 * type <code>{int f}</code>, then writing a boolean value is not permitted.
 * </p>
 * <p>
 * Not all types have readable record type and, furthermore, care must be
 * exercised for those that do. For example, <code>{int f}|int</code> does not
 * have a readable record type. Likewise, the readable record type for
 * <code>{int f, int g}|{bool f}</code> is <code>{int|bool f, ...}</code>.
 * Finally, negations play an important role in determining the readable record
 * type. For example, <code>{int|null f} & !{int f}</code> generates the
 * readable record type <code>{null f}</code>.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class ReadableRecordExtractor extends AbstractTypeExtractor<Type.Record> {

	public ReadableRecordExtractor(NameResolver resolver, TypeSystem typeSystem) {
		super(resolver, typeSystem);
	}

//	@Override
//	public Type.Record extract(Type type, Object supplementary) throws ResolutionError {
//		System.out.println("EXTRACTING RECORD: " + type);
//		Type.Record r = super.extract(type, supplementary);
//		System.out.println("DONE EXTRACTING: " + type + " => " + r);
//		return r;
//	}

	@Override
	protected Type.Record construct(Type.Atom type) {
		if(type instanceof Type.Record) {
			return (Type.Record) type;
		} else {
			return null;
		}
	}

	@Override
	protected Type.Record union(Type.Record lhs, Type.Record rhs) {
		ArrayList<Decl.Variable> fields = new ArrayList<>();
		Tuple<Decl.Variable> lhsFields = lhs.getFields();
		Tuple<Decl.Variable> rhsFields = rhs.getFields();
		for (int i = 0; i != lhsFields.size(); ++i) {
			for (int j = 0; j != rhsFields.size(); ++j) {
				Decl.Variable lhsField = lhsFields.get(i);
				Decl.Variable rhsField = rhsFields.get(j);
				Identifier lhsFieldName = lhsField.getName();
				Identifier rhsFieldName = rhsField.getName();
				if (lhsFieldName.equals(rhsFieldName)) {
					Type type = unionHelper(lhsField.getType(), rhsField.getType());
					fields.add(new Decl.Variable(new Tuple<>(), lhsFieldName, type));
				}
			}
		}
		//
		boolean isOpenRecord = lhs.isOpen() || rhs.isOpen();
		isOpenRecord |= (lhsFields.size() > fields.size() || rhsFields.size() > fields.size());
		//
		return new Type.Record(isOpenRecord, new Tuple<>(fields));
	}

	@Override
	protected Type.Record subtract(Type.Record lhs, Type.Record rhs) {
		ArrayList<Decl.Variable> fields = new ArrayList<>();
		Tuple<Decl.Variable> lhsFields = lhs.getFields();
		Tuple<Decl.Variable> rhsFields = rhs.getFields();
		for (int i = 0; i != lhsFields.size(); ++i) {
			for (int j = 0; j != rhsFields.size(); ++j) {
				Decl.Variable lhsField = lhsFields.get(i);
				Decl.Variable rhsField = rhsFields.get(j);
				Identifier lhsFieldName = lhsField.getName();
				Identifier rhsFieldName = rhsField.getName();
				if (lhsFieldName.equals(rhsFieldName)) {
					// FIXME: could potentially do better here
					Type negatedRhsType = new Type.Negation(rhsField.getType());
					Type type = intersectionHelper(lhsField.getType(), negatedRhsType);
					fields.add(new Decl.Variable(new Tuple<>(), lhsFieldName, type));
					break;
				}
			}
		}
		if(fields.size() != lhsFields.size()) {
			// FIXME: need to handle the case of open records here.
			return lhs;
		} else {
			return new Type.Record(lhs.isOpen(), new Tuple<>(fields));
		}
	}

	@Override
	protected Type.Record intersect(Type.Record lhs, Type.Record rhs) {
		//
		Tuple<Decl.Variable> lhsFields = lhs.getFields();
		Tuple<Decl.Variable> rhsFields = rhs.getFields();
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
			Decl.Variable[] fields = new Decl.Variable[matches + lhsRemainder + rhsRemainder];
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
			return new Type.Record(isOpen, new Tuple<>(fields));
		}
	}

	/**
	 * Count the number of matching fields. That is, fields with the same name.
	 *
	 * @param lhsFields
	 * @param rhsFields
	 * @return
	 */
	protected int countMatchingFields(Tuple<Decl.Variable> lhsFields, Tuple<Decl.Variable> rhsFields) {
		int count = 0;
		for (int i = 0; i != lhsFields.size(); ++i) {
			for (int j = 0; j != rhsFields.size(); ++j) {
				Decl.Variable lhsField = lhsFields.get(i);
				Decl.Variable rhsField = rhsFields.get(j);
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
	protected int extractMatchingFields(Tuple<Decl.Variable> lhsFields, Tuple<Decl.Variable> rhsFields,
			Decl.Variable[] result) {
		int index = 0;
		// Extract all matching fields first.
		for (int i = 0; i != lhsFields.size(); ++i) {
			for (int j = 0; j != rhsFields.size(); ++j) {
				Decl.Variable lhsField = lhsFields.get(i);
				Decl.Variable rhsField = rhsFields.get(j);
				Identifier lhsFieldName = lhsField.getName();
				Identifier rhsFieldName = rhsField.getName();
				if (lhsFieldName.equals(rhsFieldName)) {
					Type type = intersectionHelper(lhsField.getType(), rhsField.getType());
					Decl.Variable combined = new Decl.Variable(new Tuple<>(), lhsFieldName, type);
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
	protected int extractNonMatchingFields(Tuple<Decl.Variable> lhsFields, Tuple<Decl.Variable> rhsFields,
			Decl.Variable[] result, int index) {
		outer: for (int i = 0; i != lhsFields.size(); ++i) {
			for (int j = 0; j != rhsFields.size(); ++j) {
				Decl.Variable lhsField = lhsFields.get(i);
				Decl.Variable rhsField = rhsFields.get(j);
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
