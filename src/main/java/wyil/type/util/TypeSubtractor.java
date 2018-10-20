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

import static wyc.util.ErrorMessages.errorMessage;

import java.util.ArrayList;
import java.util.Set;

import wybs.util.AbstractCompilationUnit.Identifier;
import wybs.util.AbstractCompilationUnit.Tuple;
import wyc.util.ErrorMessages;
import wyil.type.subtyping.EmptinessTest.LifetimeRelation;
import wyil.type.util.AbstractTypeCombinator.LinkageStack;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Type;
import wyil.lang.WyilFile.Type.Array;
import wyil.lang.WyilFile.Type.Function;
import wyil.lang.WyilFile.Type.Method;
import wyil.lang.WyilFile.Type.Record;
import wyil.lang.WyilFile.Type.Reference;
import wyil.type.subtyping.SubtypeOperator;

public class TypeSubtractor extends AbstractTypeCombinator {

	public TypeSubtractor(SubtypeOperator subtyping) {
		super(subtyping);
	}

	@Override
	protected Type apply(Type lhs, Type rhs, LifetimeRelation lifetimes, LinkageStack stack) {
		Type t = super.apply(lhs, rhs, lifetimes, stack);
		if (t == null) {
			return lhs;
		} else {
			return t;
		}
	}

	@Override
	protected Type apply(Type.Null lhs, Type.Null rhs, LifetimeRelation lifetimes, LinkageStack stack) {
		return Type.Void;
	}

	@Override
	protected Type apply(Type.Bool lhs, Type.Bool rhs, LifetimeRelation lifetimes, LinkageStack stack) {
		return Type.Void;
	}

	@Override
	protected Type apply(Type.Byte lhs, Type.Byte rhs, LifetimeRelation lifetimes, LinkageStack stack) {
		return Type.Void;
	}

	@Override
	protected Type apply(Type.Int lhs, Type.Int rhs, LifetimeRelation lifetimes, LinkageStack stack) {
		return Type.Void;
	}

	@Override
	protected Type apply(Array lhs, Array rhs, LifetimeRelation lifetimes, LinkageStack stack) {
		Type element = apply(lhs.getElement(), rhs.getElement(), lifetimes, stack);
		if (element instanceof Type.Void) {
			return Type.Void;
		} else {
			return new Type.Array(element);
		}
	}

	@Override
	protected Type apply(Reference lhs, Reference rhs, LifetimeRelation lifetimes, LinkageStack stack) {
		//
		if (subtyping.isSubtype(lhs, rhs, lifetimes)) {
			return lhs;
		} else {
			return Type.Void;
		}
	}

	/**
	 * <p>
	 * Subtract one record from another. For example, subtracting
	 * <code>{null f}</code> from <code>{int|null f}</code> leaves
	 * <code>{int f}</code>. Unfortunately, there are relatively limited conditions
	 * when a genuine subtraction can occur. For example, subtracting
	 * <code>{null f, null g}</code> from <code>{int|null f, int|null g}</code>
	 * leaves <code>{int|null f, int|null g}</code>! This may seem surprising but it
	 * makes sense if we consider that without <code>{null f, null g}</code> the
	 * type <code>{int|null f, int|null g}</code> still contains
	 * <code>{int f, int|null g}</code> and <code>{int|null f, int g}</code>.
	 * </p>
	 * <p>
	 * What are the conditions under which a subtraction can take place? When
	 * subtracting <code>{S1 f1, ..., Sn fn}</code> from
	 * <code>{T1 f1, ..., Tn fn}</code> we can have at most one "pivot". That is
	 * some <code>i</code> where <code>Ti - Si != void</code>. For example,
	 * subtracting <code>{int|null f, int g}</code> from
	 * <code>{int|null f, int|null g}</code> the pivot is field <code>g</code>. The
	 * final result is then (perhaps surprisingly)
	 * <code>{int|null f, null g}</code>.
	 * </p>
	 */
	@Override
	protected Type apply(Record lhs, Record rhs, LifetimeRelation lifetimes, LinkageStack stack) {
		Tuple<Type.Field> lhsFields = lhs.getFields();
		Tuple<Type.Field> rhsFields = rhs.getFields();
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
		Type.Field[] pivots = determinePivotFields(lhsFields, rhsFields, lifetimes, stack);
		// Check how many pivots we have actuallyfound
		int count = countPivots(pivots);
		// Act on number of pivots found
		switch(count) {
		case 0:
			// no pivots found means everything was void.
			return lhs.isOpen() == rhs.isOpen() ? Type.Void : lhs;
		case 1:
			// Exactly one pivot found. This is something we can work with!
			for(int i=0;i!=pivots.length;++i) {
				if(pivots[i] == null) {
					pivots[i] = lhsFields.get(i);
				}
			}
			return new Type.Record(lhs.isOpen(),new Tuple<>(pivots));
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
	private int countFieldMatches(Tuple<Type.Field> lhsFields, Tuple<Type.Field> rhsFields) {
		int matches = 0;
		for (int i = 0; i != lhsFields.size(); ++i) {
			Type.Field lhsField = lhsFields.get(i);
			Identifier lhsFieldName = lhsField.getName();
			for (int j = 0; j != rhsFields.size(); ++j) {
				Type.Field rhsField = rhsFields.get(j);
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
	private Type.Field[] determinePivotFields(Tuple<Type.Field> lhsFields, Tuple<Type.Field> rhsFields,
			LifetimeRelation lifetimes, LinkageStack stack) {
		Type.Field[] pivots = new Type.Field[lhsFields.size()];
		//
		for (int i = 0; i != lhsFields.size(); ++i) {
			Type.Field lhsField = lhsFields.get(i);
			Identifier lhsFieldName = lhsField.getName();
			for (int j = 0; j != rhsFields.size(); ++j) {
				Type.Field rhsField = rhsFields.get(j);
				Identifier rhsFieldName = rhsField.getName();
				if (lhsFieldName.equals(rhsFieldName)) {
					// Matched field, now compute its type.
					Type type = apply(lhsField.getType(), rhsField.getType(), lifetimes, stack);
					// Check whether is a pivot or not
					if (!(type instanceof Type.Void)) {
						// Yes, is a pivot
						pivots[i] = new Type.Field(lhsFieldName, type);
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
	private int countPivots(Type.Field[] pivots) {
		int count = 0;
		for(int i=0;i!=pivots.length;++i) {
			if(pivots[i] != null) {
				count = count + 1;
			}
		}
		return count;
	}

	@Override
	protected Type apply(Function lhs, Function rhs, LifetimeRelation lifetimes, LinkageStack stack) {
		if (lhs.equals(rhs)) {
			return Type.Void;
		} else {
			return lhs;
		}
	}

	@Override
	protected Type apply(Method lhs, Method rhs, LifetimeRelation lifetimes, LinkageStack stack) {
		if (lhs.equals(rhs)) {
			return Type.Void;
		} else {
			return lhs;
		}
	}

	@Override
	protected Type apply(Type lhs, Type.Nominal rhs, LifetimeRelation lifetimes, LinkageStack stack) {
		Decl.Type decl = rhs.getDeclaration();
		if (decl.getInvariant().size() > 0) {
			// rhs is a constrained type, meaning we cannot subtract anything.
			return lhs;
		} else {
			return apply(lhs, decl.getVariableDeclaration().getType(), lifetimes, stack);
		}
	}

	@Override
	protected Type apply(Type lhs, Type.Union rhs, LifetimeRelation lifetimes, LinkageStack stack) {
		Type[] types = new Type[rhs.size()];
		for (int i = 0; i != types.length; ++i) {
			types[i] = apply(lhs, rhs.get(i), lifetimes, stack);
			// If any element of rhs subsumes lhs, then all subsumed.
			if (types[i] instanceof Type.Void) {
				return Type.Void;
			}
		}
		return union(types);
	}
}
