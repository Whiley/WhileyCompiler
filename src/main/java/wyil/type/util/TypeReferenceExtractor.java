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
import wyc.lang.WhileyFile.SemanticType.Reference;
import wyil.type.subtyping.SubtypeOperator;

/**
 * <p>
 * Responsible for extracting a readable reference type. This is a conservative
 * approximation of that described in a given type which is safe to use when
 * reading elements from that type. For example, the type
 * <code>(&int)|(&bool)</code> has a readable reference type of
 * <code>&(int|bool)</code>. This is the readable type as, if we were to read an
 * element from either bound, the return type would be in <code>int|bool</code>.
 * However, we cannot use the readable reference type for writing as this could
 * be unsafe. For example, if we actually had an reference of type
 * <code>&int</code>, then writing a boolean value is not permitted. Not all
 * types have a readable reference type and, furthermore, care must be exercised
 * for those that do. For example, <code>(&int)|int</code> does not have a
 * readable reference type.
 * </p>
 *
 * @author David J. Pearce
 *
 **/
public class TypeReferenceExtractor extends AbstractTypeExtractor<SemanticType.Reference> {

	public TypeReferenceExtractor(NameResolver resolver, SubtypeOperator subtypeOperator) {
		super(resolver, subtypeOperator);
	}

	@Override
	protected SemanticType.Reference construct(Atom type) {
		if(type instanceof SemanticType.Reference) {
			return (SemanticType.Reference) type;
		} else {
			return null;
		}
	}

	@Override
	protected SemanticType.Reference union(Reference lhs, Reference rhs) {
		if (lhs instanceof Type.Reference && rhs instanceof Type.Reference) {
			// NOTE: this case is required to ensure that, when given two Types, the type
			// extractor produces a Type (rather than a SemanticType).
			return null;
		} else {
			return new SemanticType.Reference(intersectionHelper(lhs.getElement(),rhs.getElement()));
		}
	}

	@Override
	protected SemanticType.Reference intersect(Reference lhs, Reference rhs) {
		return new SemanticType.Reference(intersectionHelper(lhs.getElement(), rhs.getElement()));
	}

	@Override
	protected SemanticType.Reference subtract(Reference lhs, Reference rhs) {
		return new SemanticType.Reference(new SemanticType.Difference(lhs.getElement(),rhs.getElement()));
	}

}
