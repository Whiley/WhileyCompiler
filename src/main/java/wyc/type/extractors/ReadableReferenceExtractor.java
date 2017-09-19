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
package wyc.type.extractors;

import static wyc.lang.WhileyFile.Type;
import wybs.lang.NameResolver;
import wyc.type.TypeSystem;
import wyc.type.util.AbstractTypeExtractor;

/**
 * <p>
 * Responsible for extracting a "readable reference" from a given type. This is
 * relatively straightforward. For example, <code>&int</code> is extracted as
 * <code>&int</code>. However, <code>(&int)|(&bool)</code> is not extracted as
 * as <code>&(int|bool)</code>. It remains unclear whether this can be expanded
 * to support for flexible readable reference types.
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
		return null;
	}

	@Override
	protected Type.Reference subtract(Type.Reference lhs, Type.Reference rhs) {
		return null;
	}

	@Override
	protected Type.Reference intersect(Type.Reference lhs, Type.Reference rhs) {
		if(lhs.equals(rhs)) {
			return lhs;
		} else {
			return null;
		}
	}
}
