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
 * Responsible for extracting a "writeable reference" from a given type. A
 * writeable reference is a conservative approximation of the references
 * described in a given type. Furthermore, it is safe to use when writing
 * elements from that type. For example, the type <code>(&int)|(&any)</code> has
 * a writeable reference type of <code>&int</code>. This is the writeable type
 * as, if we were to write an <code>int</code> to either bound this would be
 * safe. However, we cannot use the readable reference type for reading as this
 * could be unsafe. For example, if we actually had an reference of type
 * <code>&bool</code>, then reading a integer value is not permitted.
 * </p>
 * <p>
 * Not all types have a writeable reference type and, furthermore, care must be
 * exercised for those that do. For example, <code>(&int)|int</code> does not
 * have a writeable reference type.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class WriteableReferenceExtractor extends AbstractTypeExtractor<Type.Reference> {

	public WriteableReferenceExtractor(NameResolver resolver, TypeSystem typeSystem) {
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
		return new Type.Reference(unionHelper(lhs.getElement(),rhs.getElement()));
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
