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

import java.io.PrintStream;
import java.util.List;
import wycc.util.Trie;

public class Syntactic {
	/**
	 * Represents an exception which has been raised on a synctic item. The purpose
	 * of the exception is to identify this item in order that better information
	 * can be reported.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Exception extends RuntimeException {
		/**
		 * The file entry to which this error applies
		 */
		private Syntactic.Heap source;
		/**
		 * The SyntacticElement to which this error refers.
		 */
		private Syntactic.Item element;

		/**
		 * Identify a syntax error at a particular point in a file.
		 *
		 * @param msg
		 *            Message detailing the problem.
		 * @param entry
		 *            The path entry for the compilation unit this error refers to
		 * @param element
		 *            The syntactic element to this error refers
		 */
		public Exception(String msg, Syntactic.Heap entry, Syntactic.Item element) {
			super(msg);
			this.source = entry;
			this.element = element;
		}

		public Exception(String msg, Syntactic.Heap entry, Syntactic.Item element, Throwable ex) {
			super(msg,ex);
			this.source = entry;
			this.element = element;
		}

		public Syntactic.Item getElement() {
			return element;
		}

		public Syntactic.Heap getEntry() {
			return source;
		}

		public void outputSourceError(PrintStream out, boolean brief) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * A syntactic heap represents a collection of syntactic items.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static interface Heap {

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
		public Item getRootItem();

		/**
		 * Set the "root" of this syntactic heap. That is a distinguished item which is
		 * considered the root of all active items. Any item not reachable from the root
		 * is considered to be eligible for garbage collection.
		 *
		 * @param item
		 */
		public void setRootItem(Item item);

		/**
		 * Return the ith syntactic item in this heap. This may return null if the
		 * item in question has been garbage collected.
		 *
		 * @param index
		 * @return
		 */
		public Item getSyntacticItem(int ith);

		/**
		 * Determine the index of a given syntactic item in this heap.
		 *
		 * @param item
		 * @return
		 */
		public int getIndexOf(Item item);

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
		public <T extends Item> T allocate(T item);

		/**
		 * Get first parent of a syntactic item matching the given kind. If no item
		 * was found, then null is returned.
		 *
		 * @param child
		 * @param kind
		 * @return
		 */
		public <T extends Item> T getParent(Item child, Class<T> kind);

		/**
		 * Get all parents of a syntactic item matching the given kind.
		 *
		 * @param child
		 * @param kind
		 * @return
		 */
		public <T extends Item> List<T> getParents(Item child, Class<T> kind);

		/**
		 * Get first ancestor of a syntactic item matching the given kind. If no
		 * item was found, then null is returned.
		 *
		 * @param child
		 * @param kind
		 * @return
		 */
		public <T extends Item> T getAncestor(Item child, Class<T> kind);


		/**
		 * Get the parent associated with this heap (if any).
		 *
		 * @return
		 */
		public Heap getParent();

		/**
		 * Find all instances of a given kind which are reachable from the root item.
		 * This traverses each SyntacticItem in the reachable syntax tree looking for
		 * such matches.
		 *
		 *
		 * @param kind
		 * @return
		 */
		public <T extends Item> List<T> findAll(Class<T> kind);


		/**
		 * Replace all references to a given syntactic item with references to another
		 * syntactic item. Observe that all reachable items are traversed, except for
		 * those within the item being replaced. The original item remains until the
		 * next garbage collection phase.
		 *
		 * @param from
		 * @param to
		 */
		public <T extends Item> void replace(T from, T to);

		/**
		 * Trigger garbage collection for this heap. This may result in the indices of
		 * all items being changed and, hence, is a relatively destructive operation.
		 */
		public boolean gc();
	}


	/**
	 * Abstracts the mechanism for allocating items into this heap.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Allocator<H extends Heap> {

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
		public Item allocate(Item item);
	}

	public static interface Item extends Comparable<Item> {

		/**
		 * Get the enclosing compilation unit in which this syntactic item is
		 * contained. This maybe null if the item is not yet allocate to a heap.
		 *
		 * @return
		 */
		public Heap getHeap();

		/**
		 * Allocated the given item to a syntactic heap. Note that an item can only
		 * be allocated to one heap at a time. Therefore, an exception will be
		 * raised if this item is already allocated to another heap.
		 *
		 * @param heap
		 *            The heap into which this item is being allocated
		 * @param index
		 *            The index at which this item is being allocated
		 */
		public void allocate(Heap heap, int index);

		/**
		 * The opcode which defines what this bytecode does. Observe that certain
		 * bytecodes must correspond with specific subclasses. For example,
		 * <code>opcode == LOAD</code> always indicates that <code>this</code> is an
		 * instanceof <code>Load</code>.
		 */
		public int getOpcode();

		/**
		 * Mutate the opcode of this item
		 *
		 * @param opcode
		 */
		public void setOpcode(int opcode);

		/**
		 * Get the number of operands in this bytecode
		 *
		 * @return
		 */
		public int size();

		/**
		 * Return the ith top-level child in this bytecode.
		 *
		 * @param i
		 * @return
		 */
		public Item get(int i);

		/**
		 * Return the top-level children in this bytecode.
		 *
		 * @return
		 */
		public Item[] getAll();

		/**
		 * Mutate the ith child of this item
		 *
		 * @param ith
		 * @param child
		 */
		public void setOperand(int ith, Item child);

		/**
		 * Get the index of this item in the parent's items table.
		 *
		 * @return
		 */
		public int getIndex();

		/**
		 * Get any data associated with this item. This will be null if no data is
		 * associated.
		 *
		 * @return
		 */
		public byte[] getData();

		/**
		 * Get the first syntactic item of a given kind which refers to this item.
		 *
		 * @param kind
		 * @return
		 */
		public <T extends Item> T getParent(Class<T> kind);

		/**
		 * Get all syntactic items of a given kind which refer to this item.
		 *
		 * @param kind
		 * @return
		 */
		public <T extends Item> List<T> getParents(Class<T> kind);

		/**
		 * Get the first syntactic item of a given kind which refers directly or
		 * indirectly to this item.
		 *
		 * @param kind
		 * @return
		 */
		public <T extends Item> T getAncestor(Class<T> kind);

		/**
		 * Create a new copy of the given syntactic item with the given operands.
		 * The number of operands must match <code>size()</code> for this item, and
		 * be of appropriate type.
		 *
		 * @param operands
		 * @return
		 */
		public Item clone(Item[] operands);

		// ============================================================
		// Schema
		// ============================================================

		public enum Operands {
			ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, MANY
		}

		public enum Data {
			ZERO,
			ONE,
			TWO,
			MANY
		}

		public static abstract class Descriptor {
			private final Operands operands;
			private final Data data;
			private final String mnemonic;

			public Descriptor(Operands operands, Data data) {
				this(operands,data,"unknown");
			}

			public Descriptor(Operands operands, Data data, String mnemonic) {
				this.operands = operands;
				this.data = data;
				this.mnemonic = mnemonic;
			}

			public Operands getOperandLayout() {
				return operands;
			}

			public Data getDataLayout() {
				return data;
			}

			public String getMnemonic() {
				return mnemonic;
			}

			public abstract Item construct(int opcode, Item[] operands, byte[] data);

			@Override
			public String toString() {
				return "<" + operands + " operands, " + data + ">";
			}
		}
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
		public Item.Descriptor getDescriptor(int opcode);
	}

	/**
	 * A marker represents some kind of information which should be communicated to
	 * the user. For example, a syntax error or a warning. However, there are other
	 * possible markers which could be used such as for reporting possible
	 * refactoring positions, etc.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static interface Marker extends Item {
		/**
		 * Get the message associated with this marker.
		 *
		 * @return
		 */
		public String getMessage();

		/**
		 * Get the syntactic item to which this marker is associated.
		 *
		 * @return
		 */
		public Item getTarget();

		/**
		 * Get concrete path of enclosing source file.
		 *
		 * @return
		 */
		public Trie getSource();
	}

	/**
	 * A span associates a given syntactic item with a contiguous region of text in
	 * the original source file.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Span {
		public int getStart();

		public int getEnd();
	}

	/**
	 *
	 * @author djp
	 *
	 * @param <T>
	 */
	public interface AttributeMap<T> {
		public T get(Item item);
	}
}
