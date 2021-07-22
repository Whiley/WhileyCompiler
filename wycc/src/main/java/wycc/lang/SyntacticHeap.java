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
package wycc.lang;

import java.util.List;

/**
 * A syntactic heap represents a collection of syntactic items.
 *
 * @author David J. Pearce
 *
 */
public interface SyntacticHeap {

	/**
	 * Get the number of items in the heap.
	 *
	 * @return
	 */
	public int size();

	/**
	 * Get the "root" of this syntactic heap. That is a distinguished item which is
	 * considered the root of all active items. Any item not reachable from the root
	 * is considered to be eligible for garbage collection.
	 *
	 * @return
	 */
	public SyntacticItem getRootItem();

	/**
	 * Set the "root" of this syntactic heap. That is a distinguished item which is
	 * considered the root of all active items. Any item not reachable from the root
	 * is considered to be eligible for garbage collection.
	 *
	 * @param item
	 */
	public void setRootItem(SyntacticItem item);

	/**
	 * Return the ith syntactic item in this heap. This may return null if the
	 * item in question has been garbage collected.
	 *
	 * @param index
	 * @return
	 */
	public SyntacticItem getSyntacticItem(int ith);

	/**
	 * Determine the index of a given syntactic item in this heap.
	 *
	 * @param item
	 * @return
	 */
	public int getIndexOf(SyntacticItem item);

	/**
	 * <p>
	 * Allocate a given syntactic item into this heap. The item must not already
	 * be allocated to another heap. This will recursively allocate children not
	 * already allocated to this heap. Observe that the item returned is the
	 * actual object allocated into this heap. One should not assume that the
	 * item given is that which is actually allocated.
	 * </p>
	 * <p>
	 * This method will not permit mixed allocation items. That is, when it
	 * encounters an item already allocated to another heap it will simple throw
	 * an exception.
	 * </p>
	 *
	 * @param item
	 * @return
	 */
	public <T extends SyntacticItem> T allocate(T item);

	/**
	 * Get first parent of a syntactic item matching the given kind. If no item
	 * was found, then null is returned.
	 *
	 * @param child
	 * @param kind
	 * @return
	 */
	public <T extends SyntacticItem> T getParent(SyntacticItem child, Class<T> kind);

	/**
	 * Get all parents of a syntactic item matching the given kind.
	 *
	 * @param child
	 * @param kind
	 * @return
	 */
	public <T extends SyntacticItem> List<T> getParents(SyntacticItem child, Class<T> kind);

	/**
	 * Get first ancestor of a syntactic item matching the given kind. If no
	 * item was found, then null is returned.
	 *
	 * @param child
	 * @param kind
	 * @return
	 */
	public <T extends SyntacticItem> T getAncestor(SyntacticItem child, Class<T> kind);


	/**
	 * Get the parent associated with this heap (if any).
	 *
	 * @return
	 */
	public SyntacticHeap getParent();

	/**
	 * Find all instances of a given kind which are reachable from the root item.
	 * This traverses each SyntacticItem in the reachable syntax tree looking for
	 * such matches.
	 *
	 *
	 * @param kind
	 * @return
	 */
	public <T extends SyntacticItem> List<T> findAll(Class<T> kind);


	/**
	 * Replace all references to a given syntactic item with references to another
	 * syntactic item. Observe that all reachable items are traversed, except for
	 * those within the item being replaced. The original item remains until the
	 * next garbage collection phase.
	 *
	 * @param from
	 * @param to
	 */
	public <T extends SyntacticItem> void replace(T from, T to);

	/**
	 * Trigger garbage collection for this heap. This may result in the indices of
	 * all items being changed and, hence, is a relatively destructive operation.
	 */
	public boolean gc();

	/**
	 * Abstracts the mechanism for allocating items into this heap.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Allocator<H extends SyntacticHeap> {

		/**
		 * <p>
		 * Allocate a given syntactic item into a heap. The item must not already be
		 * allocated to another heap. This will recursively allocate children not
		 * already allocated to this heap. Observe that the item returned is the actual
		 * object allocated into this heap. One should not assume that the item given is
		 * that which is actually allocated.
		 * </p>
		 * <p>
		 * This method will not permit mixed allocation items. That is, when it
		 * encounters an item already allocated to another heap it will simple throw an
		 * exception.
		 * </p>
		 *
		 * @param item
		 * @return
		 */
		public SyntacticItem allocate(SyntacticItem item);
	}

	/**
	 * Abstracts the mechanism for decoding a given heap whilst preserving
	 * versioning information.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Schema {
		/**
		 * Get the minor version of this schema.
		 *
		 * @return
		 */
		public int getMinorVersion();

		/**
		 * Get the major version of this schema.
		 *
		 * @return
		 */
		public int getMajorVersion();

		/**
		 * Get the schema from which this schema extends (if any).
		 *
		 * @return
		 */
		public Schema getParent();

		/**
		 * Get the schema for a given item based on its opcode. This schema is use to
		 * decode the instruction.
		 *
		 * @param opcode
		 * @return
		 */
		public SyntacticItem.Descriptor getDescriptor(int opcode);
	}
}
