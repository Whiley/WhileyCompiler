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

import wybs.lang.NameResolver;
import wyc.lang.WhileyFile.SemanticType;
import wyc.lang.WhileyFile.Type;
import wyc.lang.WhileyFile.SemanticType.Atom;
import wyil.type.subtyping.SubtypeOperator;

/**
 * <p>
 * Responsible for extracting a readable array type. This is a conservative
 * approximation of that described in a given type which is safe to use when
 * reading elements from that type. For example, the type
 * <code>(int[])|(bool[])</code> has a readable array type of
 * <code>(int|bool)[]</code>. This is the readable type as, if we were to read
 * an element from either bound, the return type would be in
 * <code>int|bool</code>. However, we cannot use the readable array type for
 * writing as this could be unsafe. For example, if we actually had an array of
 * type <code>int[]</code>, then writing a boolean value is not permitted. Not
 * all types have readable array type and, furthermore, care must be exercised
 * for those that do. For example, <code>(int[])|int</code> does not have a
 * readable array type. Finally, negations play an important role in determining
 * the readable array type. For example, <code>(int|null)[] & !(int[])</code>
 * generates the readable array type <code>null[]</code>.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class TypeArrayExtractor extends AbstractTypeExtractor<SemanticType.Array> {

	public TypeArrayExtractor(NameResolver resolver, SubtypeOperator subtypeOperator) {
		super(resolver, subtypeOperator);
	}

	@Override
	protected SemanticType.Array construct(Atom type) {
		if(type instanceof SemanticType.Array) {
			return (SemanticType.Array) type;
		} else {
			return null;
		}
	}

	@Override
	protected SemanticType.Array union(SemanticType.Array lhs, SemanticType.Array rhs) {
		if (lhs instanceof Type.Array && rhs instanceof Type.Array) {
			// NOTE: this case is required to ensure that, when given two Types, the type
			// extractor produces a Type (rather than a SemanticType).
			return new Type.Array(unionHelper((Type) lhs.getElement(), (Type) rhs.getElement()));
		} else {
			return new SemanticType.Array(unionHelper(lhs.getElement(), rhs.getElement()));
		}
	}

	@Override
	protected SemanticType.Array intersect(SemanticType.Array lhs, SemanticType.Array rhs) {
		return new SemanticType.Array(intersectionHelper(lhs.getElement(), rhs.getElement()));
	}

	@Override
	protected SemanticType.Array subtract(SemanticType.Array lhs, SemanticType.Array rhs) {
		return new SemanticType.Array(new SemanticType.Difference(lhs.getElement(),rhs.getElement()));
	}

}
