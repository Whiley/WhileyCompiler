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
package wyll.task;

import static wyc.lang.WhileyFile.Tuple;
import static wyc.lang.WhileyFile.Type;
import static wyc.lang.WhileyFile.Identifier;

/**
 * <p>
 * A type mangler is responsible for turning the list of parameter types for a
 * function, method or property into an appropriate "mangle". That is, a string
 * which uniquely encodes the type. This is necessary because Whiley support
 * overloading of functions, methods and properties. For example, consider this
 * Whiley code:
 * </p>
 *
 * <pre>
 * function append(int[] xs, int x):
 *    ...
 *
 * function append(int[] xs, int[] ys):
 *    ...
 * </pre>
 *
 * <p>
 * Each of these is translated into a WyLL method of the form
 * <code>append_X</code> where <code>X</code> is the appropriate mangle. For the
 * first function above, an appropriate mangle might be "aii", indicating an
 * array of integers and an integer parameter. Then, the second would be "aiai"
 * indicating two arrays of integers.
 * </p>
 * <p>
 * The actual rules for generating the mangle are platform-dependent. This is
 * because they are constrained by the permitted set of characters in an
 * identifier. As such, the concept of a type mangle is a parameter to the
 * translation process to allow platform-specific customisation.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public interface TypeMangler {
	/**
	 * Construct the mangle for a given sequence of zero or more types.
	 *
	 * @param types
	 * @return
	 */
	public String getMangle(Tuple<Type> types, Tuple<Identifier> lifetimes);
}
