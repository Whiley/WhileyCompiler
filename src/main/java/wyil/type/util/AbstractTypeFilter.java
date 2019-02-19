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

import java.util.ArrayList;
import java.util.List;

import wycc.util.ArrayUtils;
import wyil.lang.WyilFile.Decl;
import wyil.lang.WyilFile.Type;

/**
 * <p>
 * Given an expected type, filter out the target array type. For example,
 * consider the following method:
 * </p>
 *
 *
 * <pre>
 * method f(int x):
 *    int[]|null xs = [x]
 *    ...
 * </pre>
 * <p>
 * When type checking the expression <code>[x]</code> the flow type checker will
 * attempt to determine an <i>expected</i> array type, in order to then
 * determine the appropriate expected element type for expression
 * <code>x</code>. Thus, it filters <code>int[]|null</code> down to just
 * <code>int[]</code>.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class AbstractTypeFilter<T extends Type> {
	private final Class<T> kind;
	private final T any;

	public AbstractTypeFilter(Class<T> kind, T any) {
		this.kind = kind;
		this.any = any;
	}

	public T[] apply(Type type) {
		ArrayList<T> results = new ArrayList<>();
		filter(type, results);
		return ArrayUtils.toArray(kind, results);
	}

	public void filter(Type type, List<T> results) {
		if (kind.isInstance(type)) {
			results.add((T) type);
		} else if(type instanceof Type.Any) {
			results.add(any);
		} else if (type instanceof Type.Nominal) {
			Type.Nominal t = (Type.Nominal) type;
			Decl.Type decl = t.getDeclaration();
			filter(decl.getType(), results);
		} else if (type instanceof Type.Union) {
			Type.Union t = (Type.Union) type;
			for (int i = 0; i != t.size(); ++i) {
				filter(t.getOperand(i), results);
			}
		}
	}
}
