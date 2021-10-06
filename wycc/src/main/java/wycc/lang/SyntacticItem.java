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

import jbfs.util.Trie;

public interface SyntacticItem extends Comparable<SyntacticItem> {

	/**
	 * Get the enclosing compilation unit in which this syntactic item is
	 * contained. This maybe null if the item is not yet allocate to a heap.
	 *
	 * @return
	 */
	public SyntacticHeap getHeap();

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
	public void allocate(SyntacticHeap heap, int index);

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
	public SyntacticItem get(int i);

	/**
	 * Return the top-level children in this bytecode.
	 *
	 * @return
	 */
	public SyntacticItem[] getAll();

	/**
	 * Mutate the ith child of this item
	 *
	 * @param ith
	 * @param child
	 */
	public void setOperand(int ith, SyntacticItem child);

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
	public <T extends SyntacticItem> T getParent(Class<T> kind);

	/**
	 * Get all syntactic items of a given kind which refer to this item.
	 *
	 * @param kind
	 * @return
	 */
	public <T extends SyntacticItem> List<T> getParents(Class<T> kind);

	/**
	 * Get the first syntactic item of a given kind which refers directly or
	 * indirectly to this item.
	 *
	 * @param kind
	 * @return
	 */
	public <T extends SyntacticItem> T getAncestor(Class<T> kind);

	/**
	 * Create a new copy of the given syntactic item with the given operands.
	 * The number of operands must match <code>size()</code> for this item, and
	 * be of appropriate type.
	 *
	 * @param operands
	 * @return
	 */
	public SyntacticItem clone(SyntacticItem[] operands);

	// ============================================================
	// Attributes
	// ============================================================

	/**
	 * A marker represents some kind of information which should be communicated to
	 * the user. For example, a syntax error or a warning. However, there are other
	 * possible markers which could be used such as for reporting possible
	 * refactoring positions, etc.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Marker extends SyntacticItem {
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
		public SyntacticItem getTarget();

		/**
		 * Get concrete path of enclosing source file.
		 *
		 * @return
		 */
		public Trie getSource();
	}

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

		public abstract SyntacticItem construct(int opcode, SyntacticItem[] operands, byte[] data);

		@Override
		public String toString() {
			return "<" + operands + " operands, " + data + ">";
		}
	}
}
