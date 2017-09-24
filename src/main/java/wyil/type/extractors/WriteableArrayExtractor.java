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
 * Responsible for extracting a "writeable array" from a given type. A writable
 * array is a conservative approximation of the arrays described in a given
 * type. Furthermore, it is safe to use when write elements from that type. For
 * example, the type <code>(any[])|(bool[])</code> has a writeable array type of
 * <code>bool[]</code>. This is the writeable type as, if we were to write an
 * element of type <code>bool</code> this is accepted by either bound. However,
 * we cannot use the writeable array type for reading as this could be unsafe.
 * For example, if we actually had an array of type say <code>int[]</code>, then
 * reading a boolean value is not permitted.
 * </p>
 * <p>
 * Not all types have a writeable array type and, furthermore, care must be
 * exercised for those that do. For example, <code>(int[])|int</code> does not
 * have a writeable array type. Finally, negations play an important role in
 * determining the writeable array type. For example,
 * <code>(int|null)[] & !(int[])</code> generates the writeable array type
 * <code>null[]</code>.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class WriteableArrayExtractor extends AbstractTypeExtractor<Type.Array> {

	public WriteableArrayExtractor(NameResolver resolver, TypeSystem typeSystem) {
		super(resolver, typeSystem);
	}

	@Override
	protected Type.Array construct(Type.Atom type) {
		if(type instanceof Type.Array) {
			return (Type.Array) type;
		} else {
			return null;
		}
	}

	@Override
	protected Type.Array union(Type.Array lhs, Type.Array rhs) {
		// int[] | bool[] => 0
		// any[] | bool[] => bool[]
		return new Type.Array(intersectionHelper(lhs.getElement(),rhs.getElement()));
	}

	@Override
	protected Type.Array subtract(Type.Array lhs, Type.Array rhs) {
		return new Type.Array(intersectionHelper(lhs.getElement(), new Type.Negation(rhs.getElement())));
	}

	@Override
	protected Type.Array intersect(Type.Array lhs, Type.Array rhs) {
		// {any x, int y}[] & {int x, any y}[] => {int x, int y}[]
		//
		return new Type.Array(intersectionHelper(lhs.getElement(),rhs.getElement()));
	}
}
