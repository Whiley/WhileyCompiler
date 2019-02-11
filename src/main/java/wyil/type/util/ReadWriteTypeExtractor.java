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

import wyc.util.ErrorMessages;

import static wyc.util.ErrorMessages.errorMessage;
import static wyil.lang.WyilFile.*;

import wycc.util.ArrayUtils;
import wyil.type.subtyping.EmptinessTest.LifetimeRelation;
import wyil.type.util.AbstractTypeCombinator.LinkageStack;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.SemanticType;
import wyil.lang.WyilFile.Type;
import wyil.lang.WyilFile.SemanticType.Array;
import wyil.lang.WyilFile.SemanticType.Atom;
import wyil.lang.WyilFile.SemanticType.Record;
import wyil.lang.WyilFile.SemanticType.Reference;
import wyil.type.subtyping.SubtypeOperator;

import java.util.ArrayList;
import java.util.Arrays;
import wybs.lang.CompilationUnit;
import wybs.lang.SyntacticItem;
import wybs.lang.SyntaxError;
import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Tuple;

/**
 * <p>
 * A generic foundation for "convential" type extractors. That is, those which
 * extract type information from types (rather than, for example, information
 * about type invariants, etc). The approach consists of two phases:
 * </p>
 *
 * <ol>
 * <li><b>(Conversion)</b>. First we convert the given type into <i>Disjunctive
 * Normal Form (DNF)</i>. That is a type with a specific structure made up from
 * one or more "conjuncts" which are disjuncted together. Each conjunct contains
 * one or more signed atoms which are intersected. For example,
 * "<code>int&(null|bool)</code>" is converted into
 * "<code>(int&null)|(int&bool)</code>".</li>
 * <li><b>(Construction)</b>. Second, we attempt to construct the DNF type into
 * an instance of the target type. This requires that all types in a given
 * conjunct match the given target type (or are negations thereof). One
 * exception, however, is when a given conjunct gives a contradiction (i.e.
 * reduces to void). Such conjuncts can be safely ignored. For example, consider
 * the type "<code>!null & (null|{int f})</code>". This expands to the conjuncts
 * "<code>!null & null</code>" and "<code>(!null)&{int f}</code>". Since the
 * former reduces to "<code>void</code>", we are left with
 * "<code>(!null)&{int f}</code>" which generates the extraction
 * "<code>{int f}</code>".</li>
 * </ol>
 *
 * <p>
 * During construction the concrete subclass is called to try and construct an
 * instance of the target type from a given atom. This gives the subclass the
 * ability to determine what the target type is. Furthermore, the subclass must
 * provide appropriate methods for combining instances of the target type though
 * <i>union</i> , <i>intersection</i> and <i>subtraction</i> operators.
 * </p>
 * <p>
 * For further reading on the operation of this algorithm, the following is
 * suggested:
 * <ul>
 * <li><b>Sound and Complete Flow Typing with Unions, Intersections and
 * Negations</b>. David J. Pearce. In Proceedings of the Conference on
 * Verification, Model Checking, and Abstract Interpretation (VMCAI), volume
 * 7737 of Lecture Notes in Computer Science, pages 335--354, 2013.</li>
 * </ul>
 * </p>
 *
 * @author David J. Pearce
 *
 * @param <T>
 */
public class ReadWriteTypeExtractor {

	public static final Combinator<SemanticType.Array> READABLE_ARRAY = new ReadableArrayCombinator();
	public static final Combinator<SemanticType.Array> WRITEABLE_ARRAY = new WriteableArrayCombinator();
	public static final Combinator<SemanticType.Reference> READABLE_REFERENCE = new ReadableReferenceCombinator();
	public static final Combinator<SemanticType.Reference> WRITEABLE_REFERENCE = new ReadableReferenceCombinator();
	public static final Combinator<Type.Callable> READABLE_CALLABLE = new ReadableCallableCombinator();
	public static final Combinator<SemanticType.Record> READABLE_RECORD = new ReadableRecordCombinator();
	public static final Combinator<SemanticType.Record> WRITEABLE_RECORD = new WriteableRecordCombinator();
	//
	private final SubtypeOperator subtypeOperator;

	public ReadWriteTypeExtractor(SubtypeOperator subtypeOperator) {
		this.subtypeOperator = subtypeOperator;
	}

	public <T extends SemanticType.Atom> T apply(SemanticType type, LifetimeRelation lifetimes, Combinator<T> kind) {
		//
		// First, convert type into conjunctive normal form. This allows all atom
		// combinations to be tried and potentially reduced to void which, in turn,
		// allows further simplifications.
		Disjunct dnf = toDisjunctiveNormalForm(type);
		// Now, convert from DNF back into a type whilst, hopefully, preserving the
		// underlying form we are looking for (e.g. an array or record, etc).
		return construct(dnf, lifetimes, kind);
	}

	// ====================================================================================
	// toDisjunctiveNormalForm
	// ====================================================================================

	/**
	 * Convert an arbitrary type to <i>Disjunctive Normal Form (DNF)</i>. That is a
	 * type with a specific structure made up from one or more "conjuncts" which are
	 * unioned together. Each conjunct contains one or more signed atoms which are
	 * intersected. For example, <code>int&(null|bool)</code> is converted into
	 * <code>(int&null)|(int&bool)</code>.
	 *
	 * @param type
	 * @param atoms
	 * @return
	 * @throws ResolutionError
	 */
	protected Disjunct toDisjunctiveNormalForm(SemanticType type) {
		switch (type.getOpcode()) {
		case TYPE_null:
		case TYPE_bool:
		case TYPE_byte:
		case TYPE_int:
			return toDisjunctiveNormalForm((Type.Primitive) type);
		case TYPE_function:
		case TYPE_method:
		case TYPE_property:
			return toDisjunctiveNormalForm((Type.Callable) type);
		case SEMTYPE_array:
		case TYPE_array:
			return toDisjunctiveNormalForm((SemanticType.Array) type);
		case TYPE_record:
		case SEMTYPE_record:
			return toDisjunctiveNormalForm((SemanticType.Record) type);
		case TYPE_reference:
		case TYPE_staticreference:
		case SEMTYPE_reference:
		case SEMTYPE_staticreference:
			return toDisjunctiveNormalForm((SemanticType.Reference) type);
		case TYPE_nominal:
			return toDisjunctiveNormalForm((Type.Nominal) type);
		case TYPE_union:
		case SEMTYPE_union:
			return toDisjunctiveNormalForm((SemanticType.Union) type);
		case SEMTYPE_intersection:
			return toDisjunctiveNormalForm((SemanticType.Intersection) type);
		case SEMTYPE_difference:
			return toDisjunctiveNormalForm((SemanticType.Difference) type);
		default:
			throw new IllegalArgumentException("unknown type encountered: " + type);
		}
	}

	protected Disjunct toDisjunctiveNormalForm(Type.Primitive type) {
		return new Disjunct(type);
	}

	protected Disjunct toDisjunctiveNormalForm(Type.Callable type) {
		return new Disjunct(type);
	}

	protected Disjunct toDisjunctiveNormalForm(Type.Nominal nominal) {
		Decl.Type decl = nominal.getDeclaration();
		return toDisjunctiveNormalForm(decl.getVariableDeclaration().getType());
	}

	protected Disjunct toDisjunctiveNormalForm(SemanticType.Record type) {
		return new Disjunct(type);
	}

	protected Disjunct toDisjunctiveNormalForm(SemanticType.Reference type) {
		return new Disjunct(type);
	}

	protected Disjunct toDisjunctiveNormalForm(SemanticType.Array type) {
		return new Disjunct(type);
	}

	protected Disjunct toDisjunctiveNormalForm(SemanticType.Difference type) {
		Disjunct lhs = toDisjunctiveNormalForm(type.getLeftHandSide());
		Disjunct rhs = toDisjunctiveNormalForm(type.getRightHandSide());
		return lhs.intersect(rhs.negate());
	}

	protected Disjunct toDisjunctiveNormalForm(SemanticType.Union type) {
		Disjunct result = null;
		//
		for (int i = 0; i != type.size(); ++i) {
			Disjunct child = toDisjunctiveNormalForm(type.getOperand(i));
			if (result == null) {
				result = child;
			} else {
				result = result.union(child);
			}
		}
		//
		return result;
	}

	protected Disjunct toDisjunctiveNormalForm(SemanticType.Intersection type) {
		Disjunct result = null;
		//
		for (int i = 0; i != type.size(); ++i) {
			Disjunct child = toDisjunctiveNormalForm(type.getOperand(i));
			if (result == null) {
				result = child;
			} else {
				result = result.intersect(child);
			}
		}
		//
		return result;
	}

	// ====================================================================================
	// construct
	// ====================================================================================

	/**
	 * Construct a given target type from a given type in DisjunctiveNormalForm.
	 *
	 * @param type
	 * @return
	 * @throws ResolutionError
	 */
	protected <T extends SemanticType.Atom> T construct(Disjunct type, LifetimeRelation lifetimes, Combinator<T> kind) {
		T result = null;
		Conjunct[] conjuncts = type.conjuncts;
		for (int i = 0; i != conjuncts.length; ++i) {
			Conjunct conjunct = conjuncts[i];
			if (!isVoid(conjunct, lifetimes)) {
				T tmp = construct(conjunct, lifetimes, kind);
				if (tmp == null) {
					// This indicates one of the conjuncts did not generate a proper
					// extraction. In this case, we can simply ignore it.
				} else if (result == null) {
					result = tmp;
				} else {
					result = kind.union(result, tmp, lifetimes);
				}
			}
		}
		return result;
	}

	/**
	 * Determine whether a given conjunct is equivalent to <code>void</code> or not.
	 * For example, <code>int & !int</code> is equivalent to <code>void</code>.
	 * Likewise, <code>{int f} & !{any f}</code> is equivalent to <code>void</code>.
	 *
	 * @param type
	 * @return
	 * @throws ResolutionError
	 */
	protected boolean isVoid(Conjunct type, LifetimeRelation lifetimes) {
		// FIXME: do we really need to defensively copy all this data here?
		SemanticType.Atom[] positives = Arrays.copyOf(type.positives, type.positives.length);
		SemanticType.Atom[] negatives = Arrays.copyOf(type.negatives, type.negatives.length);
		SemanticType.Intersection lhs = new SemanticType.Intersection(positives);
		SemanticType.Union rhs = new SemanticType.Union(negatives);
		return subtypeOperator.isVoid(new SemanticType.Difference(lhs, rhs), lifetimes);
	}

	protected <T extends SemanticType.Atom> T construct(Conjunct type, LifetimeRelation lifetimes, Combinator<T> kind) {
		T result = null;
		// First, combine the positive terms together
		SemanticType.Atom[] positives = type.positives;
		for (int i = 0; i != positives.length; ++i) {
			SemanticType.Atom pos = positives[i];
			T tmp = kind.extract(pos, lifetimes);
			if (tmp == null) {
				return null;
			} else if (result == null) {
				result = tmp;
			} else {
				result = kind.intersect(result, tmp, lifetimes);
			}
		}
		if (result != null) {
			// Second, subtract all the negative types
			SemanticType.Atom[] negatives = type.negatives;
			for (int i = 0; i != negatives.length; ++i) {
				T tmp = kind.extract(negatives[i], lifetimes);
				if (tmp != null) {
					result = kind.subtract(result, tmp, lifetimes, subtypeOperator);
				}
			}
		}
		return result;
	}

	// ====================================================================================
	// Disjunct
	// ====================================================================================

	protected static class Disjunct {
		private final Conjunct[] conjuncts;

		public Disjunct(SemanticType.Atom atom) {
			conjuncts = new Conjunct[] { new Conjunct(atom) };
		}

		public Disjunct(Conjunct... conjuncts) {
			for (int i = 0; i != conjuncts.length; ++i) {
				if (conjuncts[i] == null) {
					throw new IllegalArgumentException("conjuncts cannot contain null");
				}
			}
			this.conjuncts = conjuncts;
		}

		public Disjunct union(Disjunct other) {
			Conjunct[] otherConjuncts = other.conjuncts;
			int length = conjuncts.length + otherConjuncts.length;
			Conjunct[] combinedConjuncts = Arrays.copyOf(conjuncts, length);
			System.arraycopy(otherConjuncts, 0, combinedConjuncts, conjuncts.length, otherConjuncts.length);
			return new Disjunct(combinedConjuncts);
		}

		public Disjunct intersect(Disjunct other) {
			Conjunct[] otherConjuncts = other.conjuncts;
			int length = conjuncts.length * otherConjuncts.length;
			Conjunct[] combinedConjuncts = new Conjunct[length];
			int k = 0;
			for (int i = 0; i != conjuncts.length; ++i) {
				Conjunct ith = conjuncts[i];
				for (int j = 0; j != otherConjuncts.length; ++j) {
					Conjunct jth = otherConjuncts[j];
					combinedConjuncts[k++] = ith.intersect(jth);
				}
			}
			return new Disjunct(combinedConjuncts);
		}

		public Disjunct negate() {
			Disjunct result = null;
			for (int i = 0; i != conjuncts.length; ++i) {
				Disjunct d = conjuncts[i].negate();
				if (result == null) {
					result = d;
				} else {
					result = result.intersect(d);
				}
			}
			return result;
		}

		@Override
		public String toString() {
			String r = "";
			for (int i = 0; i != conjuncts.length; ++i) {
				if (i != 0) {
					r += " \\/ ";
				}
				r += conjuncts[i];
			}
			return r;
		}
	}

	protected static class Conjunct {
		private final SemanticType.Atom[] positives;
		private final SemanticType.Atom[] negatives;

		public Conjunct(SemanticType.Atom positive) {
			positives = new SemanticType.Atom[] { positive };
			negatives = new SemanticType.Atom[0];
		}

		public Conjunct(SemanticType.Atom[] positives, SemanticType.Atom[] negatives) {
			this.positives = positives;
			this.negatives = negatives;
		}

		public Conjunct intersect(Conjunct other) {
			SemanticType.Atom[] combinedPositives = ArrayUtils.append(positives, other.positives);
			SemanticType.Atom[] combinedNegatives = ArrayUtils.append(negatives, other.negatives);
			return new Conjunct(combinedPositives, combinedNegatives);
		}

		public Disjunct negate() {
			int length = positives.length + negatives.length;
			Conjunct[] conjuncts = new Conjunct[length];
			for (int i = 0; i != positives.length; ++i) {
				SemanticType.Atom positive = positives[i];
				conjuncts[i] = new Conjunct(EMPTY_ATOMS, new SemanticType.Atom[] { positive });
			}
			for (int i = 0, j = positives.length; i != negatives.length; ++i, ++j) {
				SemanticType.Atom negative = negatives[i];
				conjuncts[j] = new Conjunct(negative);
			}
			return new Disjunct(conjuncts);
		}

		@Override
		public String toString() {
			String r = "(";
			for (int i = 0; i != positives.length; ++i) {
				if (i != 0) {
					r += " /\\ ";
				}
				r += positives[i];
			}
			r += ") - (";
			for (int i = 0; i != negatives.length; ++i) {
				if (i != 0) {
					r += " \\/ ";
				}
				r += negatives[i];
			}
			return r + ")";
		}

		private static final Type.Atom[] EMPTY_ATOMS = new Type.Atom[0];
	}

	// ======================================================================================================
	// Combinator
	// ======================================================================================================

	public interface Combinator<T extends SemanticType.Atom> {
		public T extract(SemanticType.Atom atom, LifetimeRelation lifetimes);

		public T union(T lhs, T rhs, LifetimeRelation lifetimes);

		public T intersect(T lhs, T rhs, LifetimeRelation lifetimes);

		public T subtract(T lhs, T rhs, LifetimeRelation lifetimes, SubtypeOperator subtyping);
	}

	// ======================================================================================================
	// Readable /Writeable Record Combinators
	// ======================================================================================================

	private static class ReadableRecordCombinator implements Combinator<SemanticType.Record> {

		@Override
		public SemanticType.Record extract(Atom atom, LifetimeRelation lifetimes) {
			if (atom instanceof SemanticType.Record) {
				return (SemanticType.Record) atom;
			} else {
				return null;
			}
		}

		@Override
		public Record union(Record lhs, Record rhs, LifetimeRelation lifetimes) {
			Tuple<? extends SemanticType.Field> lhsFields = lhs.getFields();
			Tuple<? extends SemanticType.Field> rhsFields = rhs.getFields();
			// Determine the number of matching fields in the two records, as this is the
			// critical factor in determining the outcome.
			int count = countMatchingFields(lhsFields, rhsFields);
			// Determine whether result is an open record or not. If either of the records
			// is open, then the result is open. Likewise, the result is open if we
			// "compact" two compatible records down into one.
			boolean isOpenRecord = lhs.isOpen() || rhs.isOpen();
			isOpenRecord |= (lhsFields.size() > count || rhsFields.size() > count);
			//
			SemanticType.Field[] fields = new SemanticType.Field[count];
			extractMatchingFieldsUnioned(lhsFields, rhsFields, fields);
			return new SemanticType.Record(isOpenRecord, new Tuple<>(fields));
		}

		@Override
		public Record intersect(Record lhs, Record rhs, LifetimeRelation lifetimes) {
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
		public Record subtract(Record lhs, Record rhs, LifetimeRelation lifetimes, SubtypeOperator subtyping) {
			Tuple<? extends SemanticType.Field> lhsFields = lhs.getFields();
			Tuple<? extends SemanticType.Field> rhsFields = rhs.getFields();
			// Check the number of field matches
			int matches = countFieldMatches(lhsFields,rhsFields);
			if(matches < rhsFields.size()) {
				// At least one field in rhs has no match in lhs. This is definitely a redundant
				// subtraction.
				return lhs;
			} else if(matches < lhsFields.size() && !rhs.isOpen()) {
				// At least one field in lhs has not match in rhs. If the rhs is open, this is
				// fine as it will auto-fill. But, if its not open, then this is redundant.
				return lhs;
			}
			// Extract all pivot fields (i.e. fields with non-void subtraction)
			SemanticType.Field[] pivots = determinePivotFields(lhsFields, rhsFields, lifetimes, subtyping);
			// Check how many pivots we have actually found
			int count = countPivots(pivots);
			// Act on number of pivots found
			switch(count) {
			case 0:
				// no pivots found means everything was void.
				return lhs.isOpen() == rhs.isOpen() ? null : lhs;
			case 1:
				// Exactly one pivot found. This is something we can work with!
				for(int i=0;i!=pivots.length;++i) {
					if(pivots[i] == null) {
						pivots[i] = lhsFields.getOperand(i);
					}
				}
				return new SemanticType.Record(lhs.isOpen(), new Tuple<>(pivots));
			default:
				// All other cases basically are redundant.
				return lhs;
			}

		}

		/**
		 * Simply count the number of fields in the lhs which match a field in the rhs.
		 * This provides critical information. For example, when subtracting
		 * <code>{int f, int g}</code> from <code>{int f, int h}</code> it is apparent
		 * that not all fields in the lhs are matched.
		 *
		 * @param lhsFields
		 * @param rhsFields
		 * @return
		 */
		private int countFieldMatches(Tuple<? extends SemanticType.Field> lhsFields, Tuple<? extends SemanticType.Field> rhsFields) {
			int matches = 0;
			for (int i = 0; i != lhsFields.size(); ++i) {
				SemanticType.Field lhsField = lhsFields.getOperand(i);
				Identifier lhsFieldName = lhsField.getName();
				for (int j = 0; j != rhsFields.size(); ++j) {
					SemanticType.Field rhsField = rhsFields.getOperand(j);
					Identifier rhsFieldName = rhsField.getName();
					if (lhsFieldName.equals(rhsFieldName)) {
						matches++;
						break;
					}
				}
			}
			return matches;
		}

		/**
		 * Find all pivots between the lhs and rhs fields, and calculate their types.
		 *
		 * @param lhsFields
		 * @param rhsFields
		 * @param lifetimes
		 * @param stack
		 * @return
		 */
		private SemanticType.Field[] determinePivotFields(Tuple<? extends SemanticType.Field> lhsFields,
				Tuple<? extends SemanticType.Field> rhsFields, LifetimeRelation lifetimes, SubtypeOperator subtyping) {
			SemanticType.Field[] pivots = new SemanticType.Field[lhsFields.size()];
			//
			for (int i = 0; i != lhsFields.size(); ++i) {
				SemanticType.Field lhsField = lhsFields.getOperand(i);
				Identifier lhsFieldName = lhsField.getName();
				for (int j = 0; j != rhsFields.size(); ++j) {
					SemanticType.Field rhsField = rhsFields.getOperand(j);
					Identifier rhsFieldName = rhsField.getName();
					if (lhsFieldName.equals(rhsFieldName)) {
						// Matched field, now compute its type.
						SemanticType type = new SemanticType.Difference(lhsField.getType(), rhsField.getType());
						// Check whether is a pivot or not
						if (!subtyping.isVoid(type, lifetimes)) {
							// Yes, is a pivot
							pivots[i] = new SemanticType.Field(lhsFieldName, type);
						}
						break;
					}
				}
			}
			return pivots;
		}

		/**
		 * Count the number of pivots (i.e. non-null entries) in a given array.
		 *
		 * @param pivots
		 * @return
		 */
		private int countPivots(SemanticType.Field[] pivots) {
			int count = 0;
			for(int i=0;i!=pivots.length;++i) {
				if(pivots[i] != null) {
					count = count + 1;
				}
			}
			return count;
		}
	}

	private static class WriteableRecordCombinator extends ReadableRecordCombinator {
		@Override
		public Record union(Record lhs, Record rhs, LifetimeRelation lifetimes) {
			return intersect(lhs, rhs, lifetimes);
		}
	}

	// ======================================================================================================
	// Readable Array Combinator
	// ======================================================================================================

	private static class ReadableArrayCombinator implements Combinator<SemanticType.Array> {

		@Override
		public Array extract(Atom atom, LifetimeRelation lifetimes) {
			if (atom instanceof SemanticType.Array) {
				return (SemanticType.Array) atom;
			} else {
				return null;
			}
		}

		@Override
		public Array union(Array lhs, Array rhs, LifetimeRelation lifetimes) {
			return new SemanticType.Array(unionHelper(lhs.getElement(), rhs.getElement()));
		}

		@Override
		public Array intersect(Array lhs, Array rhs, LifetimeRelation lifetimes) {
			return new SemanticType.Array(intersectionHelper(lhs.getElement(), rhs.getElement()));
		}

		@Override
		public Array subtract(Array lhs, Array rhs, LifetimeRelation lifetimes, SubtypeOperator subtyping) {
			return new SemanticType.Array(new SemanticType.Difference(lhs.getElement(), rhs.getElement()));
		}
	}

	private static class WriteableArrayCombinator extends ReadableArrayCombinator {
		@Override
		public Array union(Array lhs, Array rhs, LifetimeRelation lifetimes) {
			return intersect(lhs, rhs, lifetimes);
		}

	}

	// ======================================================================================================
	// Readable Reference Combinator
	// ======================================================================================================

	private static class ReadableReferenceCombinator implements Combinator<SemanticType.Reference> {

		@Override
		public SemanticType.Reference extract(Atom atom, LifetimeRelation lifetimes) {
			if (atom instanceof SemanticType.Reference) {
				return (SemanticType.Reference) atom;
			} else {
				return null;
			}
		}

		@Override
		public Reference union(Reference lhs, Reference rhs, LifetimeRelation lifetimes) {
			return new SemanticType.Reference(intersectionHelper(lhs.getElement(), rhs.getElement()));
		}

		@Override
		public Reference intersect(Reference lhs, Reference rhs, LifetimeRelation lifetimes) {
			return new SemanticType.Reference(intersectionHelper(lhs.getElement(), rhs.getElement()));
		}

		@Override
		public Reference subtract(Reference lhs, Reference rhs, LifetimeRelation lifetimes, SubtypeOperator subtyping) {
			return new SemanticType.Reference(new SemanticType.Difference(lhs.getElement(), rhs.getElement()));
		}
	}

	// ======================================================================================================
	// Readable Reference Combinator
	// ======================================================================================================

	private static class ReadableCallableCombinator implements Combinator<Type.Callable> {

		@Override
		public Type.Callable extract(Atom atom, LifetimeRelation lifetimes) {
			if (atom instanceof Type.Callable) {
				return (Type.Callable) atom;
			} else {
				return null;
			}
		}

		@Override
		public Type.Callable union(Type.Callable lhs, Type.Callable rhs, LifetimeRelation lifetimes) {
			return null;
		}

		@Override
		public Type.Callable intersect(Type.Callable lhs, Type.Callable rhs, LifetimeRelation lifetimes) {
			return null;
		}

		@Override
		public Type.Callable subtract(Type.Callable lhs, Type.Callable rhs, LifetimeRelation lifetimes, SubtypeOperator subtyping) {
			return null;
		}
	}

	// ===============================================================
	// Misc
	// ===============================================================
	protected static int extractMatchingFieldsUnioned(Tuple<? extends SemanticType.Field> lhsFields,
			Tuple<? extends SemanticType.Field> rhsFields, SemanticType.Field[] result) {
		int index = 0;
		// Extract all matching fields first.
		for (int i = 0; i != lhsFields.size(); ++i) {
			for (int j = 0; j != rhsFields.size(); ++j) {
				SemanticType.Field lhsField = lhsFields.getOperand(i);
				SemanticType.Field rhsField = rhsFields.getOperand(j);
				Identifier lhsFieldName = lhsField.getName();
				Identifier rhsFieldName = rhsField.getName();
				if (lhsFieldName.equals(rhsFieldName)) {
					SemanticType type = unionHelper(lhsField.getType(), rhsField.getType());
					SemanticType.Field combined = new SemanticType.Field(lhsFieldName, type);
					result[index++] = combined;
				}
			}
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
	protected static int countMatchingFields(Tuple<? extends SemanticType.Field> lhsFields,
			Tuple<? extends SemanticType.Field> rhsFields) {
		int count = 0;
		for (int i = 0; i != lhsFields.size(); ++i) {
			for (int j = 0; j != rhsFields.size(); ++j) {
				SemanticType.Field lhsField = lhsFields.getOperand(i);
				SemanticType.Field rhsField = rhsFields.getOperand(j);
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
	 * Extract all matching fields (i.e. fields with the same name) into the result
	 * array.
	 *
	 * @param lhsFields
	 * @param rhsFields
	 * @param result
	 * @return
	 */
	protected static int extractMatchingFields(Tuple<? extends SemanticType.Field> lhsFields,
			Tuple<? extends SemanticType.Field> rhsFields, SemanticType.Field[] result) {
		int index = 0;
		// Extract all matching fields first.
		for (int i = 0; i != lhsFields.size(); ++i) {
			for (int j = 0; j != rhsFields.size(); ++j) {
				SemanticType.Field lhsField = lhsFields.getOperand(i);
				SemanticType.Field rhsField = rhsFields.getOperand(j);
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
	protected static int extractNonMatchingFields(Tuple<? extends SemanticType.Field> lhsFields,
			Tuple<? extends SemanticType.Field> rhsFields, SemanticType.Field[] result, int index) {
		outer: for (int i = 0; i != lhsFields.size(); ++i) {
			for (int j = 0; j != rhsFields.size(); ++j) {
				SemanticType.Field lhsField = lhsFields.getOperand(i);
				SemanticType.Field rhsField = rhsFields.getOperand(j);
				Identifier lhsFieldName = lhsField.getName();
				Identifier rhsFieldName = rhsField.getName();
				if (lhsFieldName.equals(rhsFieldName)) {
					// This is a matching field. Therefore, continue on to the
					// next lhs field
					continue outer;
				}
			}
			result[index++] = lhsFields.getOperand(i);
		}
		return index;
	}

	/**
	 * Provides a simplistic form of type union which, in some cases, does slightly
	 * better than simply creating a new union. For example, unioning
	 * <code>int</code> with <code>int</code> will return <code>int</code> rather
	 * than <code>int|int</code>.
	 *
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected static <T extends SemanticType> T unionHelper(T lhs, T rhs) {
		if (lhs.equals(rhs)) {
			return lhs;
		} else if (lhs instanceof Type.Void) {
			return rhs;
		} else if (rhs instanceof Type.Void) {
			return lhs;
		} else if (lhs instanceof Type && rhs instanceof Type) {
			return (T) new Type.Union(new Type[] { (Type) lhs, (Type) rhs });
		} else {
			return (T) new SemanticType.Union(new SemanticType[] { lhs, rhs });
		}
	}

	/**
	 * Provides a simplistic form of type intersect which, in some cases, does
	 * slightly better than simply creating a new intersection. For example,
	 * intersecting <code>int</code> with <code>int</code> will return
	 * <code>int</code> rather than <code>int&int</code>.
	 *
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	protected static SemanticType intersectionHelper(SemanticType lhs, SemanticType rhs) {
		if (lhs.equals(rhs)) {
			return lhs;
		} else if (lhs instanceof Type.Void) {
			return lhs;
		} else if (rhs instanceof Type.Void) {
			return rhs;
		} else {
			return new SemanticType.Intersection(new SemanticType[] { lhs, rhs });
		}
	}

	private <T> T syntaxError(String msg, SyntacticItem e) {
		// FIXME: this is a kludge
		CompilationUnit cu = (CompilationUnit) e.getHeap();
		throw new SyntaxError(msg, cu.getEntry(), e);
	}
}
