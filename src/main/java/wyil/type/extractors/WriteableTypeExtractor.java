package wyil.type.extractors;

import java.util.ArrayList;

import wybs.lang.NameResolver;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Tuple;
import static wyc.lang.WhileyFile.*;

import wyc.lang.WhileyFile.Decl;
import wyc.lang.WhileyFile.Type;
import wyc.lang.WhileyFile.Type.Atom;
import wyil.type.TypeSystem;

/**
 * <p>
 * Responsible for extracting a writeable type, such as a writeable array or
 * writeable record type. This is a conservative approximation of that described
 * in a given type which is safe to use when writing elements from that type.
 * For example, <code>(int[]|any[])</code> has a writeable array type.
 * </p>
 * <p>
 * <b>Writeable Arrays.</b> For example, the type <code>(any[])|(bool[])</code>
 * has a writeable array type of <code>bool[]</code>. This is the writeable type
 * as, if we were to write an element of type <code>bool</code> this is accepted
 * by either bound. However, we cannot use the writeable array type for reading
 * as this could be unsafe. For example, if we actually had an array of type say
 * <code>int[]</code>, then reading a boolean value is not permitted. Not all
 * types have a writeable array type and, furthermore, care must be exercised
 * for those that do. For example, <code>(int[])|int</code> does not have a
 * writeable array type. Finally, negations play an important role in
 * determining the writeable array type. For example,
 * <code>(int|null)[] & !(int[])</code> generates the writeable array type
 * <code>null[]</code>.
 * </p>
 * <p>
 * <b>Writeable Records.</b> For example, the type <code>{int f}|{any f}</code>
 * has a writeable record type of <code>{int f}</code>. This is the writeable
 * type as, if we were to write field <code>f</code> with an <code>int</code>
 * this is safe for either bound. However, we cannot use the writeable record
 * types for reading as this could be unsafe. For example, if we actually had a
 * record of type <code>{bool f}</code>, then reading a int value is not
 * permitted. Not all types have writeable record type and, furthermore, care
 * must be exercised for those that do. For example, <code>{int f}|int</code>
 * does not have a writeable record type. Likewise, the writeable record type
 * for <code>{T1 f, T2 g}|{T3 f}</code> is <code>{T1&T3 f, ...}</code>. Finally,
 * negations play an important role in determining the writeable record type.
 * For example, <code>{int|null f} & !{int f}</code> generates the writeable
 * record type <code>{null f}</code>.
 * </p>
 * <p>
 * <b>Writeable References.</b> For example, the type <code>(&int)|(&any)</code>
 * has a writeable reference type of <code>&int</code>. This is the writeable
 * type as, if we were to write an <code>int</code> to either bound this would
 * be safe. However, we cannot use the readable reference type for reading as
 * this could be unsafe. For example, if we actually had an reference of type
 * <code>&bool</code>, then reading a integer value is not permitted. Not all
 * types have a writeable reference type and, furthermore, care must be
 * exercised for those that do. For example, <code>(&int)|int</code> does not
 * have a writeable reference type.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class WriteableTypeExtractor extends AbstractTypeExtractor<Type> {

	public WriteableTypeExtractor(NameResolver resolver, TypeSystem typeSystem) {
		super(resolver, typeSystem);
	}

	@Override
	protected Type construct(Atom type) {
		return type;
	}

	@Override
	protected Type union(Type lhs, Type rhs) {
		int opcode = lhs.getOpcode();
		if(opcode == rhs.getOpcode()) {
			switch (opcode) {
			case TYPE_record:
				return union((Type.Record) lhs, (Type.Record) rhs);
			case TYPE_array:
				return union((Type.Array) lhs, (Type.Array) rhs);
			case TYPE_reference:
				return union((Type.Reference) lhs, (Type.Reference) rhs);
			}
		}
		return null;
	}

	@Override
	protected Type intersect(Type lhs, Type rhs) {
		int opcode = lhs.getOpcode();
		if(opcode == rhs.getOpcode()) {
			switch (opcode) {
			case TYPE_record:
				return intersect((Type.Record) lhs, (Type.Record) rhs);
			case TYPE_array:
				return intersect((Type.Array) lhs, (Type.Array) rhs);
			case TYPE_reference:
				return intersect((Type.Reference) lhs, (Type.Reference) rhs);
			}
		}
		return null;
	}

	@Override
	protected Type subtract(Type lhs, Type rhs) {
		int opcode = lhs.getOpcode();
		if(opcode == rhs.getOpcode()) {
			switch (opcode) {
			case TYPE_record:
				return subtract((Type.Record) lhs, (Type.Record) rhs);
			case TYPE_array:
				return subtract((Type.Array) lhs, (Type.Array) rhs);
			case TYPE_reference:
				return subtract((Type.Reference) lhs, (Type.Reference) rhs);
			}
		}
		// In this case, there is nothing useful to subtract. For example, {int x} - int
		// is just {int x}.
		return lhs;
	}

	// ============================================================
	// Primitives
	// ============================================================

	// ============================================================
	// Records
	// ============================================================

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
					Type type = intersectionHelper(lhsField.getType(), rhsField.getType());
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

	protected Type.Record subtract(Type.Record lhs, Type.Record rhs) {
		ArrayList<Decl.Variable> fields = new ArrayList<>();
		Tuple<Decl.Variable> lhsFields = lhs.getFields();
		Tuple<Decl.Variable> rhsFields = rhs.getFields();
		for (int i = 0; i != lhsFields.size(); ++i) {
			Decl.Variable lhsField = lhsFields.get(i);
			Identifier lhsFieldName = lhsField.getName();
			boolean matched = false;
			for (int j = 0; j != rhsFields.size(); ++j) {
				Decl.Variable rhsField = rhsFields.get(j);
				Identifier rhsFieldName = rhsField.getName();
				if (lhsFieldName.equals(rhsFieldName)) {
					Type diff = new Type.Difference(lhsField.getType(),rhsField.getType());
					fields.add(new Decl.Variable(new Tuple<>(), lhsFieldName, diff));
					matched = true;
					break;
				}
			}
			//
			if(!matched && !rhs.isOpen()) {
				// We didn't find a corresponding field, and the rhs is fixed. This means the
				// rhs is not compatible with the lhs and can be ignored.
				return lhs;
			} else if(!matched) {
				// We didn't find a corresponding field, and the rhs is open. This just means
				// the rhs is not taking anything away from the lhs (for this field).
				fields.add(lhsField);
			}
		}
		return new Type.Record(lhs.isOpen(), new Tuple<>(fields));
	}

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
	// ============================================================
	// Arrays
	// ============================================================

	protected Type.Array union(Type.Array lhs, Type.Array rhs) {
		// int[] | bool[] => 0
		// any[] | bool[] => bool[]
		return new Type.Array(intersectionHelper(lhs.getElement(),rhs.getElement()));
	}

	protected Type.Array subtract(Type.Array lhs, Type.Array rhs) {
		return new Type.Array(new Type.Difference(lhs.getElement(),rhs.getElement()));
	}

	protected Type.Array intersect(Type.Array lhs, Type.Array rhs) {
		// {any x, int y}[] & {int x, any y}[] => {int x, int y}[]
		return new Type.Array(intersectionHelper(lhs.getElement(),rhs.getElement()));
	}

	// ============================================================
	// References
	// ============================================================

	protected Type.Reference union(Type.Reference lhs, Type.Reference rhs) {
		return new Type.Reference(intersectionHelper(lhs.getElement(),rhs.getElement()));
	}

	protected Type.Reference subtract(Type.Reference lhs, Type.Reference rhs) {
		return new Type.Reference(new Type.Difference(lhs.getElement(),rhs.getElement()));
	}

	protected Type.Reference intersect(Type.Reference lhs, Type.Reference rhs) {
		return new Type.Reference(intersectionHelper(lhs.getElement(), rhs.getElement()));
	}
}
