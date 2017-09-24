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

import static wyc.lang.WhileyFile.Type;
import wybs.lang.NameResolver;
import wyil.type.TypeSystem;

/**
 * <p>
 * Responsible for extracting a "readable reference" from a given type. A
 * readable reference is a conservative approximation of the references
 * described in a given type. Furthermore, it is safe to use when reading
 * elements from that type. For example, the type <code>(&int)|(&bool)</code>
 * has a readable reference type of <code>&(int|bool)</code>. This is the
 * readable type as, if we were to read an element from either bound, the return
 * type would be in <code>int|bool</code>. However, we cannot use the readable
 * reference type for writing as this could be unsafe. For example, if we
 * actually had an reference of type <code>&int</code>, then writing a boolean
 * value is not permitted.
 * </p>
 * <p>
 * Not all types have a readable reference type and, furthermore, care must be
 * exercised for those that do. For example, <code>(&int)|int</code> does not
 * have a readable reference type.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class ReadableReferenceExtractor extends AbstractTypeExtractor<Type.Reference> {

	public ReadableReferenceExtractor(NameResolver resolver, TypeSystem typeSystem) {
		super(resolver, typeSystem);
	}

	@Override
	protected Type.Reference construct(Type.Atom type) {
		if(type instanceof Type.Reference) {
			return (Type.Reference) type;
		} else {
			return null;
		}
	}

	@Override
	protected Type.Reference union(Type.Reference lhs, Type.Reference rhs) {
		//
		return new Type.Reference(intersectionHelper(lhs.getElement(),rhs.getElement()));
	}

	@Override
	protected Type.Reference subtract(Type.Reference lhs, Type.Reference rhs) {
		return new Type.Reference(intersectionHelper(lhs.getElement(), new Type.Negation(rhs.getElement())));
	}

	@Override
	protected Type.Reference intersect(Type.Reference lhs, Type.Reference rhs) {
		return new Type.Reference(intersectionHelper(lhs.getElement(), rhs.getElement()));
	}
}
