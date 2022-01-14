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
package wyil.util;

import jbuildgraph.util.Pair;

/**
 * Implements a binary relation between values of a given type.
 *
 * @author David J. Pearce
 *
 * @param <L>
 * @param <R>
 */
public interface BinaryRelation<T> {

	/**
	 * Check whether a relationship exists between two values or not.
	 *
	 * @param lhs
	 *            First value
	 * @param rhs
	 *            Second value
	 * @return
	 */
	public boolean get(T lhs, T rhs);

	/**
	 * Set the relationship status between two values.
	 *
	 * @param lhs
	 *            First value
	 * @param rhs
	 *            Second value
	 * @param value
	 *            Status (i.e. related or not)
	 */
	public void set(T lhs, T rhs, boolean value);

	/**
	 * A simple and rather inefficient implementation of BinaryRelation which
	 * employs a HashSet underneath.
	 *
	 * @author David J. Pearce
	 *
	 * @param <T>
	 */
	public static class HashSet<T> implements BinaryRelation<T> {
		private java.util.HashSet<Pair<T, T>> relations;

		public HashSet() {
			this.relations = new java.util.HashSet<>();
		}

		@Override
		public boolean get(T lhs, T rhs) {
			return relations.contains(new Pair<>(lhs, rhs));
		}

		@Override
		public void set(T lhs, T rhs, boolean value) {
			Pair<T, T> p = new Pair<>(lhs, rhs);
			if (value) {
				relations.add(p);
			} else {
				relations.remove(p);
			}
		}
	}
}
