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
package wyil.type;

import static wyc.lang.WhileyFile.*;
import wybs.lang.NameResolver.ResolutionError;
import wyil.type.SubtypeOperator.LifetimeRelation;

/**
 * <p>
 * Responsible for extracting a type of a given form from an arbitrarily complex
 * type. For example, consider type checking the following Whiley function:
 * </p>
 *
 * <pre>
 * function get(int[]|null xs, int i) -> (int|null r):
 *     if xs is null:
 *        return null
 *     else:
 *        return xs[i]
 * </pre>
 * <p>
 * The expression <code>xs[i]</code> presents something of a challenge here. To
 * type check this, we must check that <code>xs</code> returns an array type
 * whose element is a subtype of the return type <code>int|null</code>. The type
 * determined for <code>xs</code> at this point will be
 * <code>(int[]|null)&!null</code>. Unfortunately, there are two problems here:
 * firstly, its not immediately obvious that this corresponds to an array type;
 * secondly, if so, it's still not obvious what its element type would be.
 * </p>
 * <p>
 * To tackle the above problem we must <i>extract</i> an array type from
 * <code>(int[]|null)&!null</code>. The extraction procedure will simplify the
 * type as necessary until either it determines the array type or it determines
 * this does not represent an array type (in which case an error would likely be
 * reported). The purpose of this interface is to capture the concept of
 * extracting one type from another (arbitrarily complex) type. Since this does
 * not just apply to array types, there are multiple possible implementations
 * (e.g. for extracting records, references, etc).
 * </p>
 * </p>
 *
 * @author David J. Pearce
 *
 * @param <T>
 *            The type form to be extracted (e.g. <code>Type.Array</code>).
 * @param <S>
 *            Optional supplementary information (this is used when extracting
 *            invariants only).
 */
public interface TypeExtractor<T,S> {
	/**
	 * Attempt to extract a certain kind of type from an arbitrary type. This is
	 * necessary in the presence of powerful type connectives such as
	 * <i>union</i>, <i>intersection</i> and <i>negation</i>. For example, given
	 * the type <code>{int x}|{int x}</code> we can extract the type
	 * <code>{int x}</code>.
	 *
	 * @param type
	 *            The type for which information is to be extracted
	 * @param lifetimes
	 *            The within relation between lifetimes that should be used when
	 *            determine whether the <code>rhs</code> is a subtype of the
	 *            <code>lhs</code>.
	 * @param supplementary
	 *            Supplementary information which may be used by the extractor.
	 * @return
	 */
	public T extract(Type type, LifetimeRelation lifetimes, S supplementary) throws ResolutionError;
}
